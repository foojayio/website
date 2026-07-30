# Project context for Claude Code

This repo replaces the foojay.io WordPress site with a static Hugo site,
scaffolded to run in parallel with the live WordPress site during a
trial/transition period before cutover. If you're picking this up fresh in
IntelliJ's terminal, read this before making changes.

## What exists so far

- **Hugo skeleton**: `hugo.toml`, `themes/foojay/` (layouts + `static/css/style.css`),
  `archetypes/` for posts/authors/pages.
- **Three jbang conversion scripts** in `scripts/`: `ConvertPosts.java`,
  `ConvertAuthors.java`, `ConvertPages.java`. They scrape the live
  foojay.io site (no WP admin/DB access was used or assumed) and write Hugo
  content markdown. All three are idempotent (safe to re-run repeatedly) and
  respect a `frozen: true` frontmatter flag to avoid clobbering hand-edited files.
- **`scripts/FetchMeetupEvents.java`**: pulls JUG events via Meetup's GraphQL
  API for `.github/workflows/meetup-sync.yml` (daily cron), writing
  `data/events.json`. Requires a Meetup Pro subscription + OAuth token
  (`MEETUP_OAUTH_TOKEN` secret) — Meetup retired the old open REST API.
- **`scripts/ValidateFrontmatter.java`**: PR-time content check (required
  fields present, no dangling `related_posts` references), run by
  `.github/workflows/pr-check.yml` in lieu of a visual preview (GitHub Pages
  has no per-PR preview URLs).
- **`.github/workflows/build-deploy.yml`**: builds with Hugo and deploys to
  GitHub Pages on push to `main`.
- **`data/jugs.yaml`**: the list of JUGs tracked for the calendar — just add
  an entry to track a new group, no code changes needed.

## Known gaps / things to verify before relying on this

1. **Scraping selectors are unverified against real HTML.** The environment
   this was built in could only fetch pages through a markdown-extraction
   tool, not raw HTML, so the CSS selectors in the three `Convert*.java`
   scripts (categories, tags, author link, related-posts links) are
   best-effort WordPress/Yoast conventions, not confirmed against
   foojay.io's actual theme markup. Title/description/canonical/image are
   solid (they come from standard meta tags + JSON-LD, which foojay.io does
   emit). **First thing to do**: run each script with `--url <a real post/author/page>`
   and check the output; fix the `SELECTOR_*` constants at the top of the
   file if something's empty.
2. **None of the four jbang scripts have been executed.** The sandbox they
   were written in blocks outbound network access to arbitrary domains
   (only a markdown-fetch tool was available), so nothing here has been
   run against the live site or a real Meetup Pro account. Treat all of it
   as reviewed-but-untested code.
3. **`FetchMeetupEvents.java`'s GraphQL query/endpoint need verification**
   against Meetup's current schema once you have Pro/OAuth credentials —
   noted inline in the file.
4. **The theme is structural, not visual.** `themes/foojay/static/css/style.css`
   reproduces the section layout (nav, post grid, sidebar widgets, footer)
   but not the real foojay.io branding/design. Needs a design pass against
   actual brand assets.
5. **Content gaps**: comments, on-site search, and the dynamic "Authors of
   the month" / "Featured Author" / "Trending" widgets from the WP theme
   don't have a built equivalent yet — flagged but out of scope until
   decided on.

## Conventions to keep following

- **Idempotency everywhere**: any script touching `content/` must be safe
  to re-run without duplicating or destroying hand edits (the `frozen: true`
  flag pattern). This matters because these scripts get re-run repeatedly
  during the trial period against the still-live WP site.
- **URLs are load-bearing**: every converted post/author/page keeps its
  legacy path (`aliases:` + explicit `url:` for pages) — don't restructure
  URLs without adding an alias.
- **Posts are filed by publish date, not flat**: `content/posts/<year>/<month>/<slug>.md`,
  bucketed by the post's original publish date (parsed in `ConvertPosts.java`'s
  `bucketDirFor()`), purely to keep a 1000+-post directory browsable. This has
  no effect on the URL (`hugo.toml`'s permalinks are slug-only). `isFrozen()`
  and `writePost()` look up a post's existing file by slug recursively
  (`findExistingPostFile()`) so it stays put across re-runs even if date
  parsing is imperfect.
- **`related_posts` is manual**, chosen by the author — never replace it
  with an automated tag-similarity algorithm.
- Posts are contributed via PR (see `CONTRIBUTING.md`); the repo is public.
