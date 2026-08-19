# View counter (Cloudflare Worker + D1)

The read counter behind the `12,345 views` line on posts, pages, `/pedia/`
entries and author profiles. A Worker on `foojay.io/api/views/*` in front of a
D1 table of `<section>/<slug> -> (legacy, live)`.

Why this and not a hosted analytics service, in short: it sits on foojay.io
itself, so no adblocker can tell it apart from any other request to the site;
it stores a slug and two integers, so there is no personal data to protect in
the first place; and the WordPress numbers load in as the counter's starting
value, so the site shows one number instead of adding two together in a
template. The longer version is in the header comment of `src/index.js` and in
the repo's `CLAUDE.md`.

## Until this is deployed: the WordPress bridge

`fetch/ViewCounts.java` asks `foojay.io/api/views/all` for the numbers. While no
Worker is attached to that route the request gets WordPress's 404, the script
keeps the committed `data/views.json` and exits 0 — correct, but it means the
counts on the site are frozen at whenever that file was last seeded.

WordPress is still live and still counting, so until the route is up
`.github/workflows/sync-view-counts.yml` refreshes the numbers straight from it,
once a day:

```bash
jbang scripts/transfer/LegacyViews.java --write-views
```

That writes `data/views.json` as well as `data/legacy-views.json`. The bridge
**retires itself**: the workflow runs `fetch/ViewCounts.java` immediately after,
so the moment this Worker answers, its `legacy + live` overwrites the file and
nothing needs changing. When that happens, delete the bridge step and fold the
two cron entries back into one six-hourly line.

One thing to keep doing meanwhile: re-run `--seed` when you deploy the Worker.
Its `legacy` column is a snapshot, and if it is older than what the bridge has
already been showing, the number on the page visibly drops.

## Setup (once)

Needs `wrangler` (`npm i -g wrangler`) and an account on the Cloudflare zone
that already serves foojay.io.

```bash
cd worker/views
wrangler login

# 1. Create the database, then paste the printed database_id into wrangler.toml.
wrangler d1 create foojay-views

# 2. Create the table.
wrangler d1 execute foojay-views --remote --file=schema.sql

# 3. A token for the seed endpoint. Generate any long random string and keep it
#    somewhere you can find it again -- the WordPress import needs it.
wrangler secret put SEED_TOKEN

# 4. Deploy. This claims the foojay.io/api/views/* route.
wrangler deploy
```

Nothing in WordPress serves `/api/`, so step 4 is safe to run while the WP site
is still live — and it should be run early, so the counter is accumulating real
views and is proven working before cutover depends on it.

Check it:

```bash
curl https://foojay.io/api/views/all                # {} until seeded
curl -X POST https://foojay.io/api/views/hit/posts/some-post
curl https://foojay.io/api/views/posts/some-post    # {"key":"posts/some-post","views":1}
```

## Loading the WordPress numbers

From the repo root, with the token from step 3:

```bash
jbang scripts/transfer/LegacyViews.java                       # writes data/legacy-views.json
VIEWS_SEED_TOKEN=... jbang scripts/transfer/LegacyViews.java --seed   # ...and pushes it
```

Re-run this whenever you want to catch up with WordPress — right up to cutover.
`/seed` **sets** the `legacy` column rather than adding to it, so re-running is
idempotent and never doubles a number. Views counted here in the meantime live
in a separate `live` column and are not touched.

## Endpoints

| | |
|---|---|
| `POST /api/views/hit/<key>` | Count a view. Always 204. Fired by `partials/views-beacon.html`. |
| `GET /api/views/all` | `{"<key>": <total>, ...}`. Read at build time by `scripts/fetch/ViewCounts.java`. Public. |
| `GET /api/views/<key>` | One page's total. Debugging. |
| `POST /api/views/seed` | Set the `legacy` baseline. `Authorization: Bearer $SEED_TOKEN`. |

A `<key>` is `<section>/<slug>` — `posts/some-article`, `pages/who-we-are`,
`pedia/bytecode`, `authors/jbellis`. The Worker validates the *shape* rather
than an allow-list of section names, so adding a section in the theme
(`partials/views-key.html`) needs no redeploy here.

## Free tier

Measured while designing this, foojay.io takes roughly 5–10k pageviews/day
(sampled from WordPress's own `site-views` counter twice, minutes apart). The
Workers free plan allows 100,000 requests/day and D1 allows 100,000 row-writes
/day, and one view is one request and one row-write. So there is roughly an
order of magnitude of headroom; if foojay ever needs more, the paid plan starts
at $5/month.

The build-time read is one request a day scanning ~2,200 rows, against a free
allowance of 5 million rows read per day.
