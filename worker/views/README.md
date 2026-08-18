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
jbang scripts/FetchWpViews.java                       # writes data/legacy-views.json
VIEWS_SEED_TOKEN=... jbang scripts/FetchWpViews.java --seed   # ...and pushes it
```

Re-run this whenever you want to catch up with WordPress — right up to cutover.
`/seed` **sets** the `legacy` column rather than adding to it, so re-running is
idempotent and never doubles a number. Views counted here in the meantime live
in a separate `live` column and are not touched.

## Endpoints

| | |
|---|---|
| `POST /api/views/hit/<key>` | Count a view. Always 204. Fired by `partials/views-beacon.html`. |
| `GET /api/views/all` | `{"<key>": <total>, ...}`. Read at build time by `scripts/FetchViewCounts.java`. Public. |
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
