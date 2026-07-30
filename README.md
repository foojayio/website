# foojay.io — Hugo migration

This repo is an experiment to create static (Hugo) home for foojay.io, replacing the current
WordPress site. It's being built and run in parallel with the live WordPress
site during a trial/transition period, then cut over once it proves to be solid and better.

## Structure

- `content/posts/` — blog posts (from `/today/`), filed under `<year>/<month>/<slug>.md` by original publish date (repo organization only — URLs stay `/today/<slug>/` via the permalinks config)
- `content/authors/` — author profile pages
- `content/pages/` — everything else (About, Java Quick Start tree, etc.), mirroring the legacy URL structure as directories
- `content/all-events.md` — JUG events calendar page (reads `data/events.json`)
- `content/search.md` — on-site search (see "Search" below)
- `themes/foojay/` — the Hugo theme (structural recreation of the current site; see "Known limitations" below)
- `data/jugs.yaml` — list of Java User Groups tracked for the events calendar (just add a line to add a JUG)
- `data/events.json` — generated daily by the Meetup sync workflow, do not hand-edit
- `scripts/` — jbang/Java conversion and utility scripts (see below)
- `.github/workflows/` — CI: PR checks, Pages deploy, daily Meetup sync

## Conversion scripts (jbang)

Requires [JBang](https://www.jbang.dev/) and Java 17+.

```bash
jbang scripts/ConvertPosts.java          # scrape /today/ posts -> content/posts/
jbang scripts/ConvertAuthors.java        # scrape /today/author/ -> content/authors/
jbang scripts/ConvertPages.java          # scrape remaining pages -> content/pages/
jbang scripts/ValidateFrontmatter.java   # PR-time content check (also runs in CI)
```

All three conversion scripts are **idempotent** — safe to re-run on a schedule
during the trial period. They update existing files rather than duplicating
them, and skip any file whose frontmatter has been hand-marked `frozen: true`.

Each script also supports `--url <single page URL>` to test/tune its scraping
against one real page before running a full crawl — useful since the
selectors were written without direct access to the site's raw HTML (see
"Known limitations").

## Local preview

```bash
hugo server -D
```

## Search

No external service: [Pagefind](https://pagefind.app) indexes the built
`public/` output at deploy time (`npx pagefind --site public`, added as a step
in `build-deploy.yml`) and `content/search.md` loads that index client-side.

`hugo server` alone won't have a working search box, since the index only
exists after a real build. To test locally:

```bash
# --baseURL override matters: hugo.toml's baseURL is the real GitHub Pages
# URL (.../website/), but `serve` serves public/ at the root of
# localhost:3000 with no such subpath -- without the override every asset
# and internal link 404s.
hugo --baseURL "http://localhost:3000/"
npx pagefind --site public
npx serve public
```

## Known limitations / needs verification

- **Scraping selectors** (in `ConvertPosts.java`, `ConvertAuthors.java`,
  `ConvertPages.java`) are best-effort, based on standard WordPress + Yoast
  SEO conventions, not verified against foojay.io's actual theme markup.
  Title/description/canonical/image are reliable (standard meta tags +
  JSON-LD). Categories, tags, author link, and related-posts links use
  configurable CSS selectors at the top of each file — run with `--url`
  against a few real posts and adjust if fields come back empty.
- **Meetup GraphQL query** (in `FetchMeetupEvents.java`) is a best-effort
  starting point — Meetup's API requires a Pro subscription + OAuth client,
  which wasn't available to test against. Verify the query shape against
  Meetup's current GraphQL schema once Pro access is set up.
- **Theme** (`themes/foojay/`) recreates the current site's structure
  (nav, post grid, sidebar widgets, footer) but not its exact visual design —
  treat `static/css/style.css` as a v0 starting point to refine against the
  real brand assets.
- **Related posts** are picked by the author, not computed — see
  `related_posts:` in each post's frontmatter.
- **URLs**: permalinks are configured to match the legacy `/today/slug/` and
  `/today/author/slug/` patterns, and every converted post/author/page also
  gets an explicit `aliases:` entry with its legacy path as a belt-and-suspenders
  redirect.

See `CONTRIBUTING.md` for the author PR workflow, and `CLAUDE.md` for
implementation context if you're picking this up with Claude Code.
