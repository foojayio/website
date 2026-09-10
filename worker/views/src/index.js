/**
 * foojay.io view counter.
 *
 * A Cloudflare Worker over a D1 table, mounted on the real domain at
 * foojay.io/api/views/* (see wrangler.toml). Two properties follow from being
 * first-party rather than a third-party analytics script, and both are the
 * reason this exists instead of GoatCounter/Plausible/Cloudflare Analytics:
 *
 *   1. No blocklist can tell foojay.io/api/views apart from any other request
 *      to foojay.io, so the count isn't silently 30% low on an audience of
 *      Java developers running adblockers.
 *   2. The WordPress numbers can be loaded straight in as the counter's
 *      starting value (the `legacy` column), so the site renders ONE number and
 *      no template anywhere has to add two together.
 *
 * It stores a key and two integers. No IP, no user agent, no cookie, no
 * identifier of any kind -- there is nothing here to anonymise, which is a
 * stronger privacy claim than a policy promising not to look.
 *
 * Endpoints:
 *   POST /api/views/hit/<key>    count a view. Fired by navigator.sendBeacon
 *                                from partials/views-beacon.html.
 *   GET  /api/views/all          { "<key>": <legacy+live>, ... } for the whole
 *                                site. Read at build time by
 *                                scripts/fetch/ViewCounts.java -> data/views.json.
 *   GET  /api/views/<key>        one page's total. Debugging convenience.
 *   POST /api/views/seed         { "<key>": <count>, ... } -> the `legacy`
 *                                column. Bearer SEED_TOKEN. Re-runnable; see
 *                                scripts/transfer/LegacyViews.java --seed.
 *
 * A <key> is `<section>/<slug>` -- see KEY below.
 */

/**
 * Keys are `<section>/<slug>` -- "posts/some-article", "pedia/bytecode",
 * "authors/jbellis", "pages/who-we-are". The section half keeps the counted
 * sections from colliding in what is a permanent store; the slug half mirrors
 * Posts.sanitizeSlug(), which is lowercase [a-z0-9_-] and nothing else.
 * The single definition of what a key is lives in the Hugo theme
 * (partials/views-key.html) -- this is the same rule expressed as validation.
 *
 * Deliberately not an allow-list of known section names: adding a section to
 * views-key.html would then silently stop counting until someone remembered to
 * redeploy the Worker too. Bounding the SHAPE is what matters here -- an
 * unbounded key would let anyone fill the table with junk rows.
 */
const KEY = /^[a-z][a-z0-9-]{0,30}\/[a-z0-9][a-z0-9_-]{0,190}$/;

const JSON_HEADERS = {
  "content-type": "application/json; charset=utf-8",
  // The counts are published on the site anyway, so there is nothing to guard
  // here -- and being readable from a browser makes the thing debuggable.
  "access-control-allow-origin": "*",
  // The build reads /all once a day and must not be handed a cached copy from
  // the zone in front of this Worker; a day-old number is fine, a week-old one
  // read from cache would be a silent, invisible failure.
  "cache-control": "no-store",
};

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    // Everything below is relative to the mount point, so the Worker doesn't
    // care whether it is routed at /api/views or somewhere else.
    const path = url.pathname.replace(/^\/api\/views\/?/, "").replace(/\/+$/, "");

    if (request.method === "GET" && path === "all") return all(env);
    if (request.method === "POST" && path === "seed") return seed(request, env);
    if (request.method === "POST" && path.startsWith("hit/")) {
      return hit(request, env, path.slice(4));
    }
    if (request.method === "GET" && KEY.test(path)) return one(env, path);

    return json({ error: "not found" }, 404);
  },
};

/** Count one view. Always 204: a beacon has nobody to report an error to. */
async function hit(request, env, key) {
  if (!KEY.test(key)) return noContent();
  if (!originAllowed(request, env)) return noContent();

  await env.DB.prepare(
    "INSERT INTO views (page_key, live) VALUES (?1, 1) " +
    "ON CONFLICT(page_key) DO UPDATE SET live = live + 1"
  ).bind(key).run();

  return noContent();
}

/**
 * A browser attaches Origin to the no-cors POST sendBeacon makes, so checking
 * it costs nothing and turns away casual inflation from another page. It does
 * NOT stop anyone with curl, and it isn't meant to -- the honest bound on a
 * public view counter is that the number is indicative, exactly as it was under
 * WordPress. A request with no Origin at all is allowed through rather than
 * dropped: some privacy tooling strips the header, and silently not counting
 * those readers would be a worse error than counting a few forged hits.
 */
function originAllowed(request, env) {
  const origin = request.headers.get("origin");
  if (!origin) return true;
  const allowed = (env.ALLOWED_ORIGINS || "").split(",").map((s) => s.trim()).filter(Boolean);
  return allowed.length === 0 || allowed.includes(origin);
}

/** Every key -> total, as one flat object. ~2500 rows, read a few times a day. */
async function all(env) {
  const { results } = await env.DB.prepare(
    "SELECT page_key, legacy + live AS total FROM views ORDER BY page_key"
  ).all();

  const out = {};
  for (const row of results) out[row.page_key] = row.total;
  return json(out);
}

async function one(env, key) {
  const row = await env.DB.prepare(
    "SELECT legacy + live AS total FROM views WHERE page_key = ?1"
  ).bind(key).first();

  return json({ key, views: row ? row.total : 0 });
}

/**
 * Replace the `legacy` baseline. Sets rather than adds, so re-running the
 * WordPress import (which the TODO calls for, repeatedly, until cutover) is
 * idempotent instead of doubling every number.
 */
async function seed(request, env) {
  const auth = request.headers.get("authorization") || "";
  if (!env.SEED_TOKEN || auth !== `Bearer ${env.SEED_TOKEN}`) {
    return json({ error: "unauthorized" }, 401);
  }

  let body;
  try {
    body = await request.json();
  } catch (e) {
    return json({ error: "body must be JSON" }, 400);
  }
  if (!body || typeof body !== "object" || Array.isArray(body)) {
    return json({ error: 'body must be an object of {"<section>/<slug>": count}' }, 400);
  }

  const rows = [];
  const rejected = [];
  for (const [key, count] of Object.entries(body)) {
    if (!KEY.test(key) || !Number.isInteger(count) || count < 0) {
      rejected.push(key);
      continue;
    }
    rows.push([key, count]);
  }

  // D1 caps a statement at 100 bound parameters, so 50 two-column rows is the
  // largest multi-VALUES insert that fits; batching those keeps ~2200 rows to a
  // couple of round trips instead of 2200.
  const statements = [];
  for (let i = 0; i < rows.length; i += 50) {
    const chunk = rows.slice(i, i + 50);
    const placeholders = chunk.map(() => "(?, ?)").join(", ");
    statements.push(
      env.DB.prepare(
        `INSERT INTO views (page_key, legacy) VALUES ${placeholders} ` +
        `ON CONFLICT(page_key) DO UPDATE SET legacy = excluded.legacy`
      ).bind(...chunk.flat())
    );
  }
  if (statements.length) await env.DB.batch(statements);

  return json({ seeded: rows.length, rejected });
}

function json(body, status = 200) {
  return new Response(JSON.stringify(body), { status, headers: JSON_HEADERS });
}

function noContent() {
  return new Response(null, { status: 204, headers: { "access-control-allow-origin": "*" } });
}
