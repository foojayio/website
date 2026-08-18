# foojay.io — Hugo migration

This repo is an experiment to create a static ([Hugo](https://gohugo.io/)) home
for foojay.io, replacing the current WordPress site. It's built and run in
parallel with the live WordPress site during a trial/transition period, then cut
over once it proves to be solid and better.

## Structure

Content is a mix of **converted** pages (scraped from the live WordPress site by
the `Convert*` scripts) and a few **hand-written** pages. Blog posts and author
profiles are [Hugo leaf bundles](https://gohugo.io/content-management/page-bundles/) —
a folder per item, with the Markdown in `index.md` and any images co-located in
the same folder.

- `content/posts/` — blog posts (from `/today/`), one leaf bundle per post at
  `<year>/<month>/<day>/<slug>/index.md`, bucketed by original publish date
  (repo organization only — the public URL stays `/today/<slug>/`, since the
  `posts` permalink is `:slugorcontentbasename`, i.e. the **folder name is the
  slug**). Posts with no parseable date land in an `undated/` bucket.
- `content/authors/` — author profile bundles at `<first-letter>/<slug>/index.md`,
  bucketed A–Z, with the small + large avatar images alongside. URL:
  `/today/author/<slug>/`.
- `content/pedia/` — the `/pedia/` glossary of Java terms (`<slug>.md`).
- `content/pages/` — everything else (About, Java Quick Start tree, Meet the
  Team, Where to find us, JUGs, Java Champions, etc.). Each keeps its legacy URL
  via an explicit `url:` in frontmatter.
- `content/search.md` — on-site search page (see "Search" below).
- `data/jugs.yaml` — **generated** (see "External data" below), never hand-edited.
  Rendered at `/jugs/` with a Leaflet world map + a client-side name/country filter.
- `data/java-champions.yaml` — **generated**, rendered at `/java-champions/`
  with a client-side filter.
- `data/events.json` — **generated** daily by the Meetup sync workflow.
- `themes/foojay/` — the Hugo theme (structural recreation of the current site;
  see "Known limitations" below).
- `scripts/` — jbang/Java conversion, external-data, and validation scripts (see below).
- `template/` — a starter `index.md` (all frontmatter documented) + a categories
  list for authors writing a new post; see `CONTRIBUTING.md`.
- `.github/workflows/` — CI: PR checks, Pages deploy, daily external-data + Meetup sync.

## Scripts (jbang)

Requires [JBang](https://www.jbang.dev/) and **Java 21+** (`ConvertPosts.java`
uses virtual threads; the rest need 17+).

**Content conversion** — scrape the live WordPress site into `content/`:

```bash
jbang scripts/ConvertPosts.java     # /today/ posts        -> content/posts/
jbang scripts/ConvertAuthors.java   # /today/author/       -> content/authors/
```

These are **idempotent** — safe to re-run on a schedule during the trial. They
update existing bundles rather than duplicating them, look posts/authors up by
slug so a bundle stays put across re-runs, and skip any file whose frontmatter is
hand-marked `frozen: true`. Body-HTML→Markdown conversion (image localization,
YouTube/`{{< img >}}` shortcodes, widget preservation) is shared via
`HtmlToMarkdown.java`.

The one-off page and glossary scrapers (`ConvertPages.java`, `ConvertPedia.java`)
have been removed — `content/pages/` and `content/pedia/` are done and now
maintained by hand. Only posts and authors still get re-scraped, since those keep
growing on the live site.

Each `Convert*` script also supports `--url <single page URL>` to test/tune its
scraping against one real page, and `ConvertPosts` supports `--days N` / `--since
<date>` to convert only a recent window.

**External data** — regenerate the `data/*` files from community-run upstreams
(run at every deploy and daily; see "Workflows"):

```bash
jbang scripts/FetchJugs.java            # -> data/jugs.yaml          (World-Wide-JUGs/GlobalWWJugs)
jbang scripts/FetchJavaChampions.java   # -> data/java-champions.yaml (aalmiray/java-champions)
jbang scripts/FetchMeetupEvents.java    # -> data/events.json         (Meetup GraphQL; needs Pro + OAuth)
```

The `data/*` files are **generated — never hand-edit them.** Add or fix a JUG or
a Java Champion upstream (a PR against `World-Wide-JUGs/GlobalWWJugs` or an edit
to `aalmiray/java-champions`'s `java-champions.yml`), and it flows in on the next
sync.

**Validation / one-offs:**

```bash
jbang scripts/ValidateFrontmatter.java  # PR-time content check (also runs in CI)
```

```bash
jbang scripts/ImportWpComments.java --dry-run   # legacy WP comments -> GitHub Discussions
```

`MigratePostsToBundles.java`, `MigrateAuthorsToBundles.java`, and
`SanitizeSlugs.java` are one-time migrations that have already been run — kept for
reference, not part of the normal loop. `ImportWpComments.java` is a one-off too,
but it hasn't been run yet — see "Comments".

## Local preview

```bash
hugo server -D
```

(Note: the site-wide search box needs a real build — see "Search".)

## Search

No external service: [Pagefind](https://pagefind.app) indexes the built
`public/` output at deploy time (`npx pagefind --site public`, a step in
`build-deploy.yml`) and `content/search.md` loads that index client-side.

`hugo server` alone won't have a working search box, since the index only exists
after a real build. To test locally:

```bash
# --baseURL override matters: hugo.toml's baseURL is the real deploy URL
# (a GitHub Pages subpath during the trial), but `serve` serves public/ at the
# root of localhost:3000 -- without the override every asset and link 404s.
hugo --baseURL "http://localhost:3000/"
npx pagefind --site public
npx serve public
```

## Comments

Comments and reactions come from [giscus](https://giscus.app), which stores each
post's thread as a **GitHub Discussion on this repo** — no database, no third-party
account, and moderation happens with the GitHub tools the team already has.
`themes/foojay/layouts/partials/comments.html` (called from `posts/single.html`)
renders the widget; it renders *nothing* until `[params.giscus]` in `hugo.toml` is
filled in, so an unconfigured build simply has no comment section.

One-time setup:

1. Repo **Settings → General → Features → Discussions**: enable it, then create a
   category named **Blog Comments** (announcement-style is wrong here — the
   category must accept comments from anyone). Keeping post threads out of
   "General" matters: there will eventually be one per commented post.
2. Install the [giscus app](https://github.com/apps/giscus) on the repo.
3. Fill in `repoId` and `categoryId` in `hugo.toml`. Both are public node ids, not
   secrets — read them from [giscus.app](https://giscus.app) or from:

   ```bash
   GITHUB_TOKEN=... jbang scripts/ImportWpComments.java --print-config
   ```

A thread is keyed on the **post slug** (`data-mapping="specific"`), not its
pathname: this site serves `/website/today/<slug>/` during the trial and
`/today/<slug>/` after cutover, and pathname-keyed threads would all be orphaned
the day the domain moves. `data-strict="1"` makes the match exact — see the
partial's header comment for why fuzzy matching would attach some posts'
comments to the wrong thread.

### Importing the legacy WordPress comments

`scripts/ImportWpComments.java` moves the existing foojay.io comments (580
approved ones across 270 posts) into those discussions, so cutover doesn't reset
every post to zero comments.

```bash
export GITHUB_TOKEN=...                                   # the foojay.io account's token
jbang scripts/ImportWpComments.java --dry-run             # mapping check, writes nothing
jbang scripts/ImportWpComments.java --dry-run --slug java-for-scripting   # + the exact bodies
jbang scripts/ImportWpComments.java --slug java-for-scripting             # one post, for real
jbang scripts/ImportWpComments.java                       # the lot
```

- The token must belong to **the foojay.io account**, since the original
  commenters' GitHub identities are unknown — so every imported comment is posted
  by that account and opens with `Originally posted by <author> on <date> in
  Foojay.io Discussions.` Needs `public_repo` (classic) or "Discussions: Read and
  write" (fine-grained).
- It is **idempotent**, with the state derived from GitHub rather than a file in
  this repo: a discussion is reused when the term already has one, and a comment
  is skipped when its `<!-- wp-comment-id: N -->` marker is already in the thread.
  So a run interrupted by GitHub's content-creation rate limit — likely, at ~850
  writes — is resumed by running the same command again, optionally in `--limit`
  batches.
- Run it **again just before cutover** to pick up comments posted on WordPress in
  the meantime.

## Workflows (`.github/workflows/`)

- **`build-deploy.yml`** — on push to `main`: refreshes `data/jugs.yaml` +
  `data/java-champions.yaml` (commits them back with `[skip ci]`), builds with
  Hugo, runs Pagefind, and deploys to GitHub Pages.
- **`meetup-sync.yml`** — daily cron: refreshes `data/jugs.yaml`,
  `data/java-champions.yaml`, and `data/events.json`, committing the results.
- **`pr-check.yml`** — on PRs: runs `ValidateFrontmatter.java` and a Hugo build
  (GitHub Pages has no per-PR preview URL).

## Known limitations / needs verification

- **Scraping selectors** (in the `Convert*` scripts) are best-effort, based on
  foojay.io's current theme markup + standard WordPress/Yoast conventions.
  Title/description/canonical/image are reliable (standard meta tags + JSON-LD).
  Categories, tags, author links, and related-posts links use configurable CSS
  selectors at the top of each file — run with `--url` against a few real posts
  and adjust if a field comes back empty.
- **Cloudflare cache**: the `/today/` listing is CDN-cached, so a *just*-published
  post can be missing from a crawl for a while even though its own page is live.
  Convert it directly with `--url` if you need it immediately.
- **Meetup GraphQL query** (in `FetchMeetupEvents.java`) needs verifying against
  Meetup's current schema once Pro access + an OAuth token (`MEETUP_OAUTH_TOKEN`)
  are set up.
- **Theme** (`themes/foojay/`) recreates the current site's structure (nav, post
  grid, sidebar widgets, footer) but not its exact visual design — treat
  `static/css/style.css` as a starting point to refine against real brand assets.
- **Related posts** are picked by the author, not computed — see `related_posts:`
  in each post's frontmatter.
- **URLs are load-bearing**: permalinks match the legacy `/today/slug/` and
  `/today/author/slug/` patterns, and every converted post/author/page also gets
  an explicit `aliases:` entry with its legacy path as a belt-and-suspenders redirect.

See `CONTRIBUTING.md` for the author PR workflow, and `CLAUDE.md` for
implementation context if you're picking this up with Claude Code.
