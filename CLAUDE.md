# Project context for Claude Code

This repo replaces the foojay.io WordPress site with a static Hugo site,
scaffolded to run in parallel with the live WordPress site during a
trial/transition period before cutover. If you're picking this up fresh in
IntelliJ's terminal, read this before making changes.

## The goal that outranks the others

**Publishing a post has to stay effortless for the author.** Contributors send
posts as pull requests (see `CONTRIBUTING.md`); most of them write Java, not
Hugo, and they should be able to open a file, write Markdown, and be done.
Every flag, frontmatter key, naming rule or manual step is a tax on that, and a
thing an author can get wrong or forget.

So **validate every change against this**, and prefer, in order:

1. **Derive it.** If the build can work it out from the content, it must —
   don't ask the author. The layout detects code blocks in the rendered page
   instead of reading an `enlighterjs:` flag; sponsor article counts and
   "Topics covered" are computed from `authors:` rather than stored.
2. **Default it.** If it can't be derived, pick the right default and let the
   rare case override.
3. **Ask for it.** Only when the answer genuinely lives in the author's head
   (`title`, `related_posts`, a sponsor's `authors:` list).

A flag that is always set to the same value is not configuration, it's a
chore — delete it. When a knob does have to exist, `ValidateFrontmatter.java`
should catch a mistake at PR time rather than letting it fail silently.

## What exists so far

- **Hugo skeleton**: `hugo.toml`, `themes/foojay/` (layouts + `static/css/style.css`),
  and `template/` (article / author / page / board-member starter files + the
  category list;
  see `template/README.md`). There is deliberately no `archetypes/` folder —
  nothing runs `hugo new`, and keeping a second set of starter files under a
  Hugo-specific name meant two places to look. They drifted: the post archetype
  wrote a singular `author:` against author *files*, while posts take an
  `authors:` list of author *folders*, so anything created from it failed
  `ValidateFrontmatter`. Add starter files to `template/`, not `archetypes/`.
- **Two jbang conversion scripts** in `scripts/`: `ConvertPosts.java` and
  `ConvertAuthors.java`. They scrape the live foojay.io site (no WP admin/DB
  access was used or assumed) and write Hugo content markdown. Both are
  idempotent (safe to re-run repeatedly) and respect a `frozen: true` frontmatter
  flag to avoid clobbering hand-edited files. (The one-off `ConvertPages.java`
  and `ConvertPedia.java` scrapers were removed once `content/pages/` and
  `content/pedia/` were converted — those sections are hand-maintained now; only
  posts and authors keep growing on the live site, so only those are re-scraped.)
- **`scripts/FetchJugs.java`**: regenerates `data/jugs.yaml` from the
  community-run [World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs)
  (one Markdown-with-YAML-frontmatter file per JUG under its `_jugs/`
  folder). Run at every deploy (`build-deploy.yml`, before the Hugo build)
  and four times a day (`sync-external-content.yml`, before `FetchMeetupEvents.java`), both of
  which commit the refreshed file back to `main` — same pattern as
  `events.json`. JUG leaders add/update their own group by opening a PR
  against that repo, not this one. Derives `meetup_slug`/`meetup_url`
  whenever a JUG's `website` is a meetup.com URL.
- **`scripts/FetchJavaChampions.java`**: regenerates `data/java-champions.yaml`
  from [aalmiray/java-champions](https://github.com/aalmiray/java-champions)'s
  single `java-champions.yml` file — the data behind
  [javachampions.org](https://javachampions.org/). Run at every deploy and
  four times a day, same as `FetchJugs.java` above. Champions add/update their own entry
  by editing that file directly upstream, not this repo. No coordinates yet
  (unlike JUGs) — a pending PR
  ([aalmiray/java-champions#318](https://github.com/aalmiray/java-champions/pull/318))
  adds `location: {lat, lng}` via a one-time geocoding script, but it hasn't
  merged; pick that field up here once it does, same way `FetchJugs.java`
  reads JUG coordinates.
- **`scripts/FetchMeetupEvents.java`**: pulls JUG events via Meetup's GraphQL
  API for `.github/workflows/sync-external-content.yml` (cron, four times a day), writing
  `data/events.json`. Only queries JUGs that have a `meetup_slug` (i.e. that
  actually use Meetup) in `data/jugs.yaml`. Requires a Meetup Pro
  subscription + OAuth token (`MEETUP_OAUTH_TOKEN` secret) — Meetup retired
  the old open REST API.
- **`scripts/ConvertSponsors.java`**: converts the sponsor section from the live
  WP site into `content/sponsors/<wp-slug>/index.md` page bundles (logo pulled
  local as a bundle resource, About text through `HtmlToMarkdown`). Reads the
  index at `/our-sponsors/` for the tier, then each `/sponsor/<slug>/` profile
  for the rest. Idempotent and `frozen: true`-aware like the `Convert*`
  scripts, and run by hand for the same reason they are — it scrapes the
  WordPress site that goes away at cutover, so it does **not** belong in CI
  next to `FetchJugs`/`FetchJavaChampions` (those pull from upstream GitHub
  repos that outlive the migration). See "sponsors ↔ articles" below for the
  one field it deliberately does not own.
- **`scripts/MigrateEnlighterToFences.java`**: rewrites legacy EnlighterJS code
  markup already sitting in `content/` (`<pre class="EnlighterJSRAW"
  data-enlighter-language="java" …>`, inline `<code class="EnlighterJSRAW">`,
  and hand-written ` ```EnlighterJSRAW ` info strings) as plain Markdown
  fences. **Storage format only — the site is visually unchanged**: the
  EnlighterJS markup goes back on at render time (see "code blocks" below).
  Contributors send posts as PRs, and a fence is what they already know how to
  type; eight attributes of WordPress plumbing is not. Already run over the
  whole tree, and idempotent — a re-run is a no-op. It stays in the repo
  because the WP site keeps serving Enlighter markup until cutover, so a late
  re-scrape can reintroduce blocks. `--dry-run` reports without writing;
  `--path <dir>` narrows the scan.

  It also repairs **WordPress's double-escaping inside fences** — bodies that
  store a lambda arrow as `-&amp;gt;`, so the code renders as `-&gt;`. (The
  live WP site shows those wrong too; it's an old content bug, not a conversion
  one.) The rule lives in `HtmlToMarkdown.resolveDoubleEscaped` so the scrapers
  and this script agree, and it is deliberately narrow: `&lt; &gt; &quot;
  &apos;` always resolve, but a bare `&amp;` does **not** — in an XML/XHTML
  sample `&amp;` is correct source. `&amp;` is only resolved as the `&&`
  operator, a shell redirect (`2>&1`) or a URL query separator. Don't "simplify"
  this into a second blanket unescape: content/ has a JSF snippet whose
  `value="Food &amp; Culture"` and a post that appends a literal `"&nbsp;"`
  string, and a blanket pass corrupts both.

  It also turns the **non-breaking spaces WordPress indents code with** into
  ordinary ones (`HtmlToMarkdown.normalizeCodeSpaces`). A U+00A0 looks like an
  indent in the rendered block but isn't one — copy the sample out and the
  compiler chokes on it. This script always did it; the SCRAPER did not, so a
  re-scrape put 10,270 of them back across 36 posts. Both call the same method
  now. Fence bodies and inline code only — a U+00A0 in prose is left alone (147
  of them, harmless, and sometimes deliberate).

  The repair covers **fence bodies, inline code spans and Markdown link
  destinations** — the three places WP damage can land. Code spans get the same
  `resolveDoubleEscaped` rule as fences (Markdown doesn't decode entities inside
  `` ` `` either, so `` `DESCRIBE KEYSPACE &lt;name>` `` renders a literal
  `&lt;`). Destinations get `HtmlToMarkdown.resolveEscapedUrl` instead, which
  collapses `&amp;` to `&` repeatedly. Note what that is and isn't fixing:
  `?a=1&amp;b=2` is CORRECT Markdown (CommonMark decodes entities in
  destinations) and only the over-escaped `?a=1&amp;amp;b=2` actually renders
  wrong. The collapse is applied anyway so storage matches what an author would
  type and what a re-scrape now emits — of the 78 files it touched, 76 were
  provably no-ops (built HTML diffed before/after: 2 pages changed). Bare prose
  and preserved raw-HTML blocks are never touched — a post has a *table of
  entity names* as its subject matter, and in raw HTML `&amp;` is correct
  markup.
- **`scripts/MigrateGalleriesToShortcode.java`**: one-off migration that
  replaced the WordPress gallery markup in `content/` with the
  `{{< gallery >}}` shortcode — 55 posts, 94 galleries, 259 images, both block
  shapes (nested `<figure>`s and the older `<ul class="blocks-gallery-grid">`).
  Same reasoning and same shape as `MigrateEnlighterToFences.java`: a
  contributor can't be asked to type 30 lines of block markup, and a gallery is
  a list of filenames. It calls `HtmlToMarkdown.galleryShortcode`, which the
  scrapers now use too, so a re-scrape emits the same thing and a re-run here is
  a no-op. `--dry-run` / `--path` as usual. See the gallery convention below for
  what the shortcode derives rather than stores.
- **`scripts/FixCloudflareEmails.java`**: one-off migration that put back the
  email addresses Cloudflare hid from the scrapers. foojay.io is behind
  Cloudflare with **Email Address Obfuscation** on, so an address never reaches
  a non-browser client: the HTML carries a placeholder plus an XOR-encoded copy,
  and a script in the *reader's* browser swaps them back. Nothing here runs
  JavaScript, so the literal `[email protected]` landed in `content/` and every
  mailto became a dead `](/cdn-cgi/l/email-protection)` -- 293 occurrences across
  161 files. Cloudflare matches a loose `x@y`, so it also mangled things that
  merely look like addresses, **inside code**: `git@github.com:...` in a clone
  command and every line of `java --list-modules` output (`javafx.base@14.0.2`).
  `HtmlToMarkdown.decodeCloudflareEmails` now undoes all of it at conversion
  time, so a re-scrape emits the right thing and a re-run here is a no-op.

  Unlike the other migrations this one **cannot repair from what it has** -- the
  stored files kept only the placeholder, the encoded copy was dropped by the
  converter -- so it re-fetches each affected page and reads the addresses back
  out of the live HTML. That makes the safety rule the interesting part: a file
  is only written when its placeholder count matches the number of obfuscated
  elements in the live page body, so the n-th placeholder provably pairs with
  the n-th address; a file that doesn't match is left alone and reported, never
  guessed at. `--dry-run` / `--path` as usual. 148 files, 279 addresses. Two
  known leftovers, both correct: `content/pages/terms-of-use.md` reproduces
  WordPress's own `[info@azul.com](mailto:info@azul.io)` mismatch, and one post
  really does contain the words "[email protected]" in a prompt example (the
  live page has the same literal). Run it again after any late re-scrape, before
  cutover kills the only source of these addresses.
- **`scripts/ImportWpComments.java`**: one-off migration that moves the legacy
  WordPress comments (580 approved, across 270 posts, read from foojay.io's open
  `/wp-json/wp/v2/comments` — no admin access needed) into the GitHub Discussions
  that giscus reads, so cutover doesn't reset every post to zero comments. Posts
  as the foojay.io account (`GITHUB_TOKEN`), because the commenters' GitHub
  identities are unknown, and opens each comment with `Originally posted by
  <author> on <date> in Foojay.io Discussions.` — the attribution the TODO asked
  for. Bodies go through `HtmlToMarkdown.toMarkdown` (made public for this), so a
  comment gets the same entity/fence/nbsp repairs the post bodies got.
  Deliberately **not** part of `ConvertPosts.java`, which the TODO wondered about:
  that script writes files and is re-run against the live WP site constantly,
  while this writes irreversible public content to a third-party API and needs a
  credential — the same reason `ConvertSponsors.java` is run by hand. Idempotent
  with the state derived from GitHub rather than a file here (a discussion is
  reused when its term already has one; a comment is skipped when its
  `<!-- wp-comment-id: N -->` marker is already in the thread), which is what
  makes it resumable across GitHub's content-creation rate limit — ~850 writes,
  `--limit N` batches, automatic back-off. `--dry-run` reports without writing
  (and with `--slug` prints the exact bodies); `--print-config` resolves the
  repo/category node ids for `hugo.toml`. Run it again just before cutover to
  pick up comments posted on WordPress in the meantime. **Not yet run.**
- **`worker/views/`**: the read counter — a Cloudflare Worker over a D1 table
  of `<section>/<slug> -> (legacy, live)`, routed at `foojay.io/api/views/*`. Deployed by
  hand (`wrangler deploy`), never by CI, for the same reason
  `ConvertSponsors.java` is run by hand: it writes outside the repo and needs a
  credential. See "read counter" under the conventions below for why this
  exists instead of a hosted analytics service, and `worker/views/README.md`
  for the setup steps. **Not yet deployed.**
- **`scripts/FetchWpViews.java`**: captures the view counts WordPress holds for
  every post, page and pedia entry (the Post Views Counter plugin exposes them
  on an open REST route — no admin, DB or credential needed, same posture as
  `ImportWpComments.java`) into `data/legacy-views.json`, and with `--seed`
  loads them into the Worker as its `legacy` baseline. Posts and pages come
  from `/wp/v2/`; the **pedia glossary is a custom post type (`terminology`)
  that WordPress does not expose to REST**, so each entry's id is read back out
  of its rendered page's body class (`postid-124618`) — all 30 resolve.
  **Author profiles have no WordPress baseline at all**: the plugin can count
  user archives, but the option is off on foojay.io (its user-views route
  returns 0 for every author checked), so they start at zero. The script prints
  that on every run rather than leaving a section silently empty. Run by hand, repeatedly,
  until cutover — the TODO's "one-time operation that needs to be repeated".
  Seeding **sets** rather than adds, and live views accumulate in a separate
  column, so a re-run can't double or discard a number. One request per post
  (the route sums when handed several ids, so there is no batching), eight at a
  time, ~3 minutes for the site; `--limit N` for a test run. Needs a browser
  `User-Agent` — WP Engine's WAF 403s a bare Java one. Every one of the 2145
  posts and all 30 pedia entries match. The 7 items it reports as unmatched are
  WordPress listing pages (`today`, `author`, `sitemap`, `home-page`, …) with no
  single Hugo page behind them; `PAGE_ALIASES` covers the one page whose Hugo
  file is named differently (`jugs` → `java-user-groups-jugs`), and
  `SECTION_MOVES` the one that changed *section* (WP page `log4j-cve` → Hugo
  post `posts/log4j-cve`). Add to `SECTION_MOVES` whenever a WP page is
  republished here as a post: WordPress can't be edited to follow, so without
  the entry the item resolves against the wrong section's slugs, lands in
  `unmatched`, and its whole count is silently dropped at the next run. The
  key in `data/legacy-views.json`/`data/views.json` has to move with it.
- **`scripts/FetchViewCounts.java`**: the CI half — reads
  `/api/views/all` into `data/views.json` at every deploy and four times a day, so the
  numbers are baked into the HTML. **Never fails the build**: if the counter is
  unreachable it keeps the committed file and exits 0.
- **`scripts/StripHeadingAnchors.java`**: one-off migration that removed the
  WordPress heading anchors (`## Title {#h2-2-title}`) from `content/`. WP
  stamps every heading with `id="h2-<index>-<slug>"`, Flexmark carries an id
  over as Markdown attribute syntax, and Goldmark applies it — the round trip
  worked, which is why it went unnoticed. Dropped because the ids are
  **positional** (inserting an H2 leaves `h2-3-` above `h2-2-`), a good few are
  corrupt at the source (WP's slugifier eats leading capitals: "Podcast Apps" →
  `h2-1--odcast-pps`, which foojay.io really does serve), and a contributor
  writing a new post would never type one. Handles ATX, setext and
  blockquote-wrapped headings; resizes setext underlines; skips fenced code,
  where `{#…}` is CSS or shell parameter expansion. 1835 files, 14344 anchors
  (plus one heading whose text the conversion had wrapped onto a second line,
  rejoined by hand).
  `HtmlToMarkdown.toMarkdown` now drops heading ids at the source, so a
  re-scrape is a no-op; the script stays for the same reason
  `MigrateEnlighterToFences.java` does. `--dry-run` / `--path` as usual.

  It also strips the same id where WordPress stamped it on a **link** rather
  than a heading -- `[Ty Morton](https://.../){#31db}`, which Medium-imported
  posts carry on every paragraph's first link. That case is worse than the
  heading one and was still live: Goldmark's attribute syntax applies to a whole
  block, so an id sitting mid-paragraph never round-tripped -- it rendered as
  the literal text `{#31db}` in the middle of a sentence, on 28 posts. 39
  removed (plus 21 heading anchors that a later re-scrape had put back).
  `HtmlToMarkdown` drops `a[id]` alongside the heading ids now. Anchored to the
  link's closing paren, so a `{#id}` in a CSS example is never touched, and
  fenced code is skipped as before.
- **`scripts/NormalizeMarkdown.java`**: one-off migration that brought
  `content/` in line with the storage format the converter now emits. Two
  things, both Flexmark defaults that were never a deliberate choice:
  **setext headings → ATX** (Flexmark underlines h1/h2 with `====`/`----` and
  only hashes from h3 down, so content was in two styles at once — 8053
  converted), and **decorative `<br>` lines dropped** (WP uses a bare `<br />`
  as a vertical spacer after images and embeds; 1152 removed, same case as the
  decorative `<hr>`s below). Bodies now end in a single newline.
  `HtmlToMarkdown` emits both shapes directly now
  (`FlexmarkHtmlConverter.SETEXT_HEADINGS = false` + the `STANDALONE_BREAK`
  pass), so a re-scrape is a no-op. `--dry-run` / `--path` as usual.

  Three things it deliberately leaves alone, each because touching them would
  change the page rather than restyle the source: a `<br>` with text on its line
  (load-bearing in a table cell or inline SVG), anything inside a fence or an
  indented code block (several posts paste multi-document YAML whose `---`
  separators would otherwise become headings), and a `---` under a **list item**
  (CommonMark says a setext underline can't interrupt a list, so that pair
  already renders as list + thematic break — 203 left underlined for this).
- **`scripts/ValidateFrontmatter.java`**: PR-time content check (required
  fields present, no dangling `related_posts` references, no sponsor
  `authors:` slug without a matching author bundle, no emoji in a post title, no
  two pages in a folder claiming the same series `weight`), run by
  `.github/workflows/pr-check.yml` in lieu of a visual preview (GitHub Pages
  has no per-PR preview URLs).
- **`.github/workflows/build-deploy.yml`**: builds with Hugo and deploys to
  GitHub Pages on push to `main`. Also refreshes and commits `data/jugs.yaml`,
  `data/java-champions.yaml` and `data/views.json` before building
  (see `FetchJugs.java` above) — needs `permissions.contents:
  write` and a `[skip ci]` commit message for exactly this reason (otherwise
  that commit would re-trigger the same workflow).
- **`data/jugs.yaml`**: auto-generated by `scripts/FetchJugs.java` — see
  above. Never hand-edit it; add/fix a JUG upstream in GlobalWWJugs instead.
  Rendered at `/jugs/` (`content/pages/java-user-groups-jugs.md`, `type:
  "jugs"` → `themes/foojay/layouts/jugs/single.html`), including a Leaflet +
  marker-clustering world map built from its `latitude`/`longitude` fields.
- **`data/views.json`**: auto-generated by `scripts/FetchViewCounts.java` —
  `slug -> total reads`, the numbers rendered on posts and cards. Never
  hand-edit it. Seeded from `data/legacy-views.json` so the counts are live on
  the site *now*, before the Worker exists; once it is deployed this is
  overwritten with `legacy + live` on every build.
- **`data/legacy-views.json`**: auto-generated by `scripts/FetchWpViews.java` —
  each post's WordPress view count at the last import. Committed because it is
  the **only** copy: these numbers vanish with the WordPress site, and they are
  what seeds the counter.
- **`data/java-champions.yaml`**: auto-generated by
  `scripts/FetchJavaChampions.java` — see above. Never hand-edit it; add/fix
  an entry upstream in aalmiray/java-champions instead. Rendered at
  `/java-champions/` (`content/pages/java-champions.md`, `type: "champions"`
  → `themes/foojay/layouts/champions/single.html`) — no map there yet, see
  gap #3 below.

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
2. **None of the jbang scripts have been executed**, including the newest,
   `FetchJavaChampions.java`. The sandbox they were written in blocks outbound
   network access to arbitrary domains (only a markdown-fetch tool was
   available — enough to confirm the GlobalWWJugs and java-champions.yml
   frontmatter/schema by hand, not enough to run the actual GitHub API +
   raw-file fetch loop), so nothing here has been run against the live site,
   a real Meetup Pro account, or the GitHub API. Treat all of it as
   reviewed-but-untested code, same as `FetchJugs.java` was before Frank ran
   it locally. **First thing to do for `FetchJavaChampions.java`**: run it
   locally once (`jbang scripts/FetchJavaChampions.java`) and check
   `data/java-champions.yaml` — in particular that the `country`/`social`
   nested objects in the source flattened correctly, and that the
   `/java-champions/` table renders sensibly for the ~700 entries missing
   most optional fields.
3. **`FetchMeetupEvents.java`'s GraphQL query/endpoint need verification**
   against Meetup's current schema once you have Pro/OAuth credentials —
   noted inline in the file.
4. **The theme is structural, not visual.** `themes/foojay/static/css/style.css`
   reproduces the section layout (nav, post grid, sidebar widgets, footer)
   but not the real foojay.io branding/design. Needs a design pass against
   actual brand assets.
5. **Content gaps**: the dynamic "Authors of the month" / "Featured Author" /
   "Trending" widgets from the WP theme don't have a built equivalent yet —
   flagged but out of scope until decided on.
6. **Search** (`content/search.md`, `themes/foojay/layouts/search/single.html`)
   is wired up via Pagefind, no external service. `.github/workflows/build-deploy.yml`
   runs `npx -y pagefind --site public` right after the Hugo build, which
   writes a static, chunked index into `public/pagefind/`; the search page
   loads it client-side and calls `pagefind.search()`. `data-pagefind-body`
   on `<main>` (baseof.html) restricts indexing to page content; sidebar.html
   carries `data-pagefind-ignore` so the repeated widgets don't pollute
   results. Gotcha: `hugo server` alone has no index (only the built
   `public/pagefind/pagefind.js` does) — test with `hugo && npx pagefind
   --site public && npx serve public` instead. Not yet run for real (this
   sandbox has no network access to the npm registry to fetch Pagefind), so
   treat it the same as the conversion scripts: reviewed, untested.
7. **Comments are wired but not switched on; the view counter needs deploying.**
   `comments.html` (giscus) is called from `posts/single.html` and
   `[params.giscus]` is in `hugo.toml` with `repoId`/`categoryId` left blank —
   the partial renders nothing until those are filled in, so **three manual
   steps remain**: enable Discussions on the repo with a comment-accepting
   "Blog Comments" category, install the giscus app, paste the two ids
   (`jbang scripts/ImportWpComments.java --print-config` prints the block).
   Then run the comment import (see the script's entry above and README
   "Comments"); nothing here has been run against GitHub yet, so treat both the
   import and the widget as reviewed-but-untested.
   **Views** are built (see "read counter" below) but the Worker is **not
   deployed** — the templates, both scripts and the Worker source are in the
   repo and the WordPress numbers are captured in `data/legacy-views.json`, but
   until someone runs `wrangler deploy` from `worker/views/` nothing is counted
   and `data/views.json` stays `{}` (the partial then renders nothing, which is
   the correct degradation, not a bug). Four steps, all in
   `worker/views/README.md`: create the D1 database, load `schema.sql`, set
   `SEED_TOKEN`, deploy. Then `jbang scripts/FetchWpViews.java --seed`. Do it
   early rather than at cutover: the route can go up while WordPress is still
   live (nothing in WP serves `/api/`), and a counter proven over weeks beats
   one switched on the day it has to work. The GoatCounter scaffold that used to
   live in `partials/stats.html` is gone — deleted, not migrated; it also
   carried an unwired share button, which nothing has replaced.
8. **The paid homepage banner carousel is NOT built — and it's revenue-bearing.**
   The live WP home page (its page title is literally "Home – CTA and Sponsor
   Blocks") opens with a Splide carousel of "Sponsored Content" teasers —
   currently CodeRabbit, Azul and foojay's own Sustainability eBook. Each slide
   has its own background colour, image, headline, description and CTA link,
   with impression attributes (`data-entry`/`data-current`) on the button. This
   is the homepage-banner benefit the tiers actually sell: 10/year gold, 6
   silver, 3 bronze. It is a **campaign** model, not a sponsor one — one
   sponsor runs many banners a year, each with its own creative and date
   window — so it does NOT belong in the sponsor bundles; it wants its own
   `data/sponsor-campaigns.yaml` (or similar) with start/end dates plus
   rotation in `index.html`. Nothing in this repo replaces it yet, so **cutting
   over without building it silently drops something sponsors have paid for.**
   Decide with Frank whether impressions/clicks need tracking too (WP counts
   them) before designing the data model.

## Conventions to keep following

- **Idempotency everywhere**: any script touching `content/` must be safe
  to re-run without duplicating or destroying hand edits (the `frozen: true`
  flag pattern). This matters because these scripts get re-run repeatedly
  during the trial period against the still-live WP site.
- **URLs are load-bearing**: every converted post/author/page keeps its
  legacy path (`aliases:` + explicit `url:` for pages) — don't restructure
  URLs without adding an alias. **One deliberate exception**: heading
  *fragments*. `StripHeadingAnchors.java` (above) dropped WP's `#h2-N-slug`
  anchors, so section-level deep links minted before cutover land at the top of
  the post instead. Paths, aliases and frontmatter are untouched, and Hugo still
  generates an id per heading from its text — the "On this page" panel and its
  scroll-spy resolve every one of their 14k links. Accepted knowingly; fragment
  links into a blog post are rare next to the cost of keeping two conventions.
- **Render hooks must redo the escaping Goldmark would have done.** Overriding
  a renderer means taking over its entity handling too, and getting it wrong is
  invisible in the template and obvious on the page. Two shapes, both live:
  `render-codeblock.html` needs `htmlEscape .Inner | safeHTML` — without
  `safeHTML`, `htmlEscape` returns a plain string that html/template escapes a
  **second** time, so `->` reaches the reader as `-&gt;` (this was breaking ~950
  posts). `render-link.html`/`render-image.html` need `htmlUnescape` on
  `.Destination`/`.Title`, because those arrive raw and CommonMark decodes
  entities in a destination, so `?a=1&amp;b=2` otherwise renders as a query
  param literally named `amp;b` (~90 posts). Check both when editing a hook.
- **Headings are stored as ATX (`##`), not setext underlines.** Levels map 1:1
  to WordPress's `<h1>`–`<h6>`, so nesting is exactly what the author wrote —
  verified by diffing the built site: across 3543 pages, **zero** changed their
  heading-level sequence when `NormalizeMarkdown.java` restyled 8053 of them.
  (Content nesting is mostly sane on its own: posts start at h2, and only 87
  skips — mostly h2 → h4 — exist across 15,930 headings in 50 files. Those are
  the authors' own markup, not a conversion artifact.) Note that a setext
  underline heads the **whole paragraph** above it, which is why 3 posts were
  rendering a paragraph of prose inside their `<h2>`; ATX heads only its own
  line, so the migration fixed those. Don't reintroduce `----` underlines.
- **Code blocks are stored as Markdown fences, rendered as EnlighterJS.**
  `content/` holds ```` ```java ````; `themes/foojay/layouts/_default/_markup/render-codeblock.html`
  turns every fence back into the `<pre class="EnlighterJSRAW">` element the
  vendored initialiser (`partials/enlighterjs.html`) looks for, so the site
  keeps its existing code styling. Hugo's own Chroma highlighting is bypassed
  on purpose — the hook returns its own HTML. Storage and presentation are
  separated so contributors write Markdown and swapping the highlighter later
  means editing that one file rather than reprocessing 1000+ posts. The
  render hook maps fence tags to the 57 languages in the **vendored** bundle
  and degrades anything unrecognised to `generic`. That list is derived by
  reading `static/vendor/enlighterjs/enlighterjs.min.js`, not from docs — an
  earlier hand-written version omitted `xml`, `yaml`, `visualbasic` and
  `verilog`, silently rendering 592 blocks (`yaml` 266, `xml`/`html` 322, `vb`
  4) as plain `generic`. `html` is a declared alias of `xml` and `vb` of
  `visualbasic`, so the hook aliases those to the canonical name. Re-derive the
  list from the bundle if it is ever updated. `baseof.html` loads the partial when the *rendered* page
  contains an EnlighterJS block. **There is no `enlighterjs:` frontmatter flag**
  — it was removed (1090 files) once detection covered every case: an author
  writes a fence and highlighting happens. Removing it beat defaulting it to
  `true`, which would have pulled 144 KB of JS+CSS onto the 1477 pages with no
  code at all; it also stopped 4 posts loading the highlighter for nothing (they
  carried the flag but have only inline backticks, which EnlighterJS no longer
  touches). Verified by rebuilding — byte-identical output apart from those 4.
  Don't reintroduce a flag for this. `HtmlToMarkdown.codeFence`/`fenceLanguage` emit
  fences from the conversion scripts, so a re-scrape produces the same shape;
  `MigrateEnlighterToFences.java` above cleans up anything that slips through.
  Don't reintroduce raw `<pre class="EnlighterJSRAW">` into `content/`.
- **Email addresses are decoded on the way in, never left obfuscated.**
  foojay.io is behind Cloudflare with Email Address Obfuscation on, so every
  address in the HTML it serves is a placeholder plus an XOR-encoded copy that
  only a browser puts back. `HtmlToMarkdown.decodeCloudflareEmails` reverses it
  (the first hex byte is the key) as the **first** thing `toMarkdown` does --
  before the code-block and preserve passes, which would otherwise bake
  `[email protected]` into a fence. Two rules worth keeping: a decode that
  isn't an address becomes text rather than a link, because Cloudflare's matcher
  has false positives (`javafx.base@14.0.2`, `setup-java@v5.5.0`,
  `<code>@name</code>`); and an address **inside code** is always plain text,
  because Flexmark renders an `<a>` in a `<pre>` as its bare href, which would
  turn `--docker-email="a@b"` into `--docker-email="mailto:a@b"`.
  `FixCloudflareEmails.java` above cleaned up what was already in `content/`.

- **WordPress's decorative `<hr>`s and `<br>` spacers are dropped, not
  converted.** Flexmark
  renders `<hr>` as `*** ** * ** ***`, and WP bodies are full of them between
  sections — they carried styling the WP theme supplied and this one doesn't,
  so on Hugo they were bare rules that added nothing. `HtmlToMarkdown` strips
  them from the converter's output (`FLEXMARK_THEMATIC_BREAK`), before the
  preserved placeholders are restored, so a code sample containing the same
  asterisks is never touched. ~950 were removed from `content/` in one pass;
  this keeps a re-scrape from putting them back. A line holding nothing but
  `<br>` tags is dropped at the same point and for the same reason
  (`STANDALONE_BREAK`) — WP uses them as vertical spacers after images and
  embeds. A `<br>` with text on its line is a real hard break and stays.
- **Posts are filed by publish date, not flat**: `content/posts/<year>/<month>/<slug>.md`,
  bucketed by the post's original publish date (parsed in `ConvertPosts.java`'s
  `bucketDirFor()`), purely to keep a 1000+-post directory browsable. This has
  no effect on the URL (`hugo.toml`'s permalinks are slug-only). `isFrozen()`
  and `writePost()` look up a post's existing file by slug recursively
  (`findExistingPostFile()`) so it stays put across re-runs even if date
  parsing is imperfect.
- **There is no `tags` taxonomy — deliberately.** WordPress *does* tag its
  posts, but its theme never renders them on a page (the `.article__tags`
  container holds only categories), so the scrapers — which read the public
  site, with no admin/DB access — never saw one. Every post landed with an empty
  `tags:`, producing 0 term pages against categories' 752, a `{{ with
  .Params.tags }}` block in `posts/single.html` that could never fire, and a
  frontmatter field authors had to look at and wonder about. All of it removed:
  the taxonomy and permalink in `hugo.toml`, the template block, the CSS, the
  scraper's `SELECTOR_TAG_LINKS`, and the key from 2146 files. `guessCategory`
  used to take tags as extra signal and now takes only the title, since the
  signal was always empty. If tags are ever wanted, they are recoverable from
  `/wp-json/wp/v2/posts?slug=…&_fields=tags` plus `/wp-json/wp/v2/tags?include=…`
  (both open, verified) — but only until cutover. Categories are the taxonomy.
- **Emoji come off post TITLES, never out of bodies.**
  `ConvertPosts.stripEmoji` runs on the scraped title, because a title isn't
  just text on the post -- it is the card in every grid, the RSS item, the
  browser tab, the `og:title` a link preview renders and the text a search
  result shows, and a decorative glyph reads as noise or breaks alignment in all
  of them. 7 titles were affected. Bodies are deliberately untouched: an emoji
  in prose is the author's writing, and 404 posts carry 3443 of them -- among
  which the arrows (`->` 336, `^` 55), `(TM)` (31) and the check/cross marks in
  comparison tables are load-bearing, not decoration. The rule is
  `\p{IsExtended_Pictographic}` plus skin tones / keycap / variation selector /
  ZWJ, **not** `\p{IsEmoji}`: the latter is true for ASCII digits, `#` and `*`,
  so it would eat the "5" out of "The 5 Knights" and every `#release` hashtag.
  Titles don't feed the URL (the slug is the bundle folder name), so nothing
  needed an alias.

  The converter only covers what it scrapes, and a contributor writes their own
  frontmatter by hand -- so `ValidateFrontmatter.checkTitleEmoji` applies the
  same rule at PR time. Both use the same character class on purpose; change one
  and change the other.
- **`related_posts` is manual**, chosen by the author — never replace it
  with an automated tag-similarity algorithm.
- **Sponsors ↔ articles is an author list, and it's hand-maintained.**
  WordPress works out a sponsor's articles through a plugin relation we have
  no access to. Here the link is explicit: each
  `content/sponsors/<slug>/index.md` carries `authors:`, a list of author
  slugs (the bundle folder names under `content/authors/`), and
  `themes/foojay/layouts/partials/sponsor-posts.html` resolves that to every
  post any of them wrote. That partial is the single definition — the article
  grid, the article/podcast/author counts and the "Topics covered" list on a
  sponsor page are all derived from it at build time, so nothing goes stale
  and no counts are stored. `ConvertSponsors.java` reads `authors:` back out of
  the existing file and writes it through unchanged, so re-scraping never
  clobbers it; `ValidateFrontmatter.java` fails the PR on a slug that matches
  no author. Note this makes our numbers legitimately differ from WordPress's
  (Redis shows 11 articles here vs 1 there) — author-based attribution is
  broader than whatever WP was doing. That's the intended semantics; if a
  sponsor should own fewer posts, narrow its `authors:` list.
- **Galleries are the `{{< gallery >}}` shortcode — migrated posts included.**
  A gallery is a list of filenames, so that is what `content/` holds: one per
  line between `{{< gallery >}}` and `{{< /gallery >}}`, `| caption` after a
  filename, `| |` before alt text when it differs from the caption, plus
  optional `cols=` and `caption=` (one caption for the whole grid) on the
  opening tag. Everything else is the template's job
  (`themes/foojay/layouts/shortcodes/gallery.html`): the CSS grid (`cols` is a
  maximum, auto-fit drops to fewer columns on narrow screens and never opens
  more columns than there are images), `resource-url.html` resolution (a bare
  name means the file next to `index.md`), and the lightbox
  (`static/js/lightbox.js` binds every `.prose img` plus the post hero, and
  steps through all of a page's images with ‹ › buttons, arrow keys or a swipe
  — one entry per distinct full-size source, so a featured image that is also a
  gallery image isn't shown twice).

  It also **derives the link to each image's full-size original**: WordPress
  wrapped a `shot-1024x768.png` thumbnail in an `<a href="shot.png">` so the
  lightbox opens the full image, and the template works that filename out
  (`-scaled` variant too) instead of the content storing it. 84 of those links
  came back identically, 7 images gained one, and the 34 links that pointed at
  the image itself went away — the lightbox falls back to the `img` src, so they
  never did anything.

  The 55 migrated posts (94 galleries, 259 images) previously carried WordPress
  block markup verbatim: modern nested `<figure>`s, plus 15 posts on the older
  `<ul class="blocks-gallery-grid">`. `MigrateGalleriesToShortcode.java`
  converted them and `HtmlToMarkdown.galleryShortcode` — the same method — emits
  the shortcode from the scrapers, so a re-scrape produces the same shape. Both
  WP shapes and their CSS are gone from the repo; don't reintroduce them.
  Verified by rebuilding: only those 55 post bodies changed, with the same 94
  galleries, same 259 images in the same order, and the same captions.
- **Featured Authors are two slugs in `hugo.toml`, and nothing else.**
  `params.featuredAuthors` lists the author slugs (folder names under
  `content/authors/`) currently spotlighted; foojay rotates the pick monthly and
  announces it in a post, so it genuinely lives in an editor's head — case 3 of
  the derive/default/ask rule. Everything on display is derived from there:
  `partials/featured-authors.html` resolves the slugs to author pages,
  `partials/author-posts.html` (the author-side twin of `sponsor-posts.html`,
  and now the single definition used by the profile page too) supplies the
  article count and latest article, and the bio/photo/socials come from the
  author's own bundle. So a rotation is one line, and no second place goes
  stale. Two renderings, one source: `featured-authors-band.html` (two cards
  atop `/today/author/`) and `featured-authors-widget.html` (sidebar, home page
  only — the authors page already leads with the band). An unknown slug is
  skipped by the template rather than rendered dead; `ValidateFrontmatter.java`
  is what fails the PR on it. Don't add a `featured: true` frontmatter flag
  instead — that's two files to edit per rotation and, worse, two to remember to
  unset, which is exactly how a "featured" author silently stays featured
  forever.
- **The Advisory Board is a folder, not a list.** `/board/`
  (`content/pages/board.md`, `type: "board"` + `layout: "list"` ->
  `themes/foojay/layouts/board/list.html`) holds the two-paragraph intro and
  nothing else. Each member is its own page at `/board/<slug>/`
  (`content/pages/board/<slug>.md`, `type: "board"` ->
  `themes/foojay/layouts/board/single.html`), and `partials/board-members.html`
  is the single definition of who is on the board -- the overview grid, the "19
  organizations" count and the "other board members" strip on a member page all
  come from it. Adding a member is one markdown file (copy
  `template/board-member.md`) plus a logo in `static/images/pages/board/`;
  there is no list to edit and no count to update. `ValidateFrontmatter.java`
  fails the PR on a member missing `type: "board"` or `logo`, because both fail
  silently -- a missing type drops the member off `/board/` entirely, and a
  missing logo renders an initial that looks deliberate.

  WordPress put all 19 members in one accordion on `/board/`, so a member could
  not be linked to and their quote was a click away. Splitting them is the same
  move `/pedia/` makes, and it is why `content/pages/board.md` stays the
  landing page rather than becoming a `content/board/` section: the page has
  58,754 WordPress views recorded under the `pages/board` key, and the section
  form would have renamed that key (see "read counter" below). Member pages are
  counted automatically as `pages/<slug>` -- they are under `content/pages/`,
  which `views-key.html` already counts.

  Logos carry `logoBackground` for the same reason sponsor logos do: the
  artwork is a mix of dark-on-transparent (white tile) and white-on-transparent
  (Chronicle, Gradle -- a tile in the brand's own dark tone), and without it one
  or the other disappears depending on the theme. A member that also sponsors
  Foojay links to its sponsor profile, matched on title rather than recorded in
  frontmatter, so a new sponsor bundle wires itself up.

- **The AI portal is a derived category landing page.** `/ai/`
  (`content/pages/ai.md`, `type: "ai"` -> `themes/foojay/layouts/ai/single.html`)
  is what WordPress serves as the **"Machine Learning" category page** (WP slug
  `ai-ml`) with an editorial intro on top. So the page file holds the intro plus
  one key -- `list_category: "Machine Learning"` -- and the layout derives the
  rest: the lead card, the full article grid, the "Topics covered" chip row
  (categories those posts carry, most-used first) and the article count. A new
  AI post shows up by carrying the category; there is no list to maintain and no
  count stored. Don't turn it into a hand-picked `posts:` list in frontmatter.
  Two details worth knowing before editing it: the grid is **not** paginated
  (`.Paginate` only works on list pages, so a page-kind template can't use it --
  the "Browse the ... category" link at the bottom leads to the paginated term
  page), and its cards pass `maxCategories: 0` to `partials/post-card.html` so
  every category chip shows instead of the usual first two -- on a portal the
  chips are the navigation. That option is the only reason the partial accepts a
  dict; called with a Page it behaves exactly as before.
- **`/sitemap/` is an HTML page for readers, derived from the content tree.**
  Not to be confused with Hugo's `/sitemap.xml`, which is for crawlers.
  WordPress serves an HTML sitemap at `/sitemap/` (34,244 views) and
  `partials/footer.html` links to it from every page, but nothing here answered
  that URL -- the footer link was dead. `content/pages/sitemap.md` (`type:
  "sitemap"` -> `themes/foojay/layouts/sitemap/single.html`) holds only a
  one-line intro; the page tree, pedia, sponsors, authors and every article by
  year are all derived, so a new page or post appears at the next build and
  there is no list to maintain. The page tree's nesting comes from sorting on
  `.File.Path`: a lexicographic path sort is already tree order
  (`pages/board.md` before `pages/board/azul.md`), so depth is a slash count
  rather than recursion.

  Fixing it turned up that **`content/all-events.md` and the scraped
  `content/pages/all-events.md` both claimed `url: "/all-events/"`**, and the
  empty scraped stub won -- so the real events calendar (`type: "events"`)
  rendered nowhere and a footer link led to a blank page. Hugo does not warn
  about two files claiming one `url:`. They are now one file under
  `content/pages/`, which is what keeps a view key resolving at all; a
  root-level `content/*.md` has no section and `views-key.html` would not count
  it.

  **The events calendar is `content/pages/calendar.md`, served at `/calendar/`.**
  A second empty scraped stub was doing the same trick one URL over --
  `content/pages/calendar.md` claimed `url: "/calendar/"` with an empty body,
  which is also the URL the "Event Calendar" menu item points at, so the item in
  the primary nav led to a blank page while `/all-events/` held the real thing.
  Resolved the other way round this time, because the view key follows the FILE
  name (`views-key.html` uses `.File.ContentBaseName`, not the URL) and
  WordPress counted 52,272 views on `/calendar/` against 20,984 on
  `/all-events/`: the events page is now named `calendar.md`, serves
  `/calendar/`, and carries `/all-events/` as an alias. `FetchWpViews.java`'s
  `PAGE_ALIASES` maps WP's `all-events` page onto the `calendar` key so it does
  not land in `unmatched`; `fetchAll` merges duplicate keys with `Math::max`, so
  the page keeps the higher count rather than summing two views of one page.
  Renaming the file back would silently move the key and drop the bigger number.
- **A multi-page series is a folder of pages with a `weight`, and nothing else.**
  The 11 Java Quick Start tutorial steps used to hand-write the same
  `<< Prev` / `Next >>` markdown pair TWICE each (top and bottom of every page),
  which is 20 links restating an order that is a property of the series, not of
  any one page -- and they had already drifted: two of the eleven were missing
  their bottom pair. Now `partials/series-steps.html` is the single definition
  ("the pages in this folder that carry a weight, sorted"), and
  `series-progress.html` (a step counter + clickable progress bar under the page
  head) and `series-nav.html` (previous/next cards at the foot) derive
  everything from it -- titles, how many steps there are, which page is first or
  last. Reordering is renumbering; inserting a step is one new file.

  Both partials are called from `_default/single.html` and render **nothing**
  when the page has no weight, so no other page is affected and any future
  folder of ordered pages gets the navigation for free. That self-disabling is
  load-bearing: the five `install-java/` pages next door are alternatives
  (Windows / macOS / Linux), not a sequence, so they carry no weight and
  correctly get no navigation. `linkTitle:` carries the short step name, because
  the full titles all begin "Getting Started with Java - " and a nav card should
  not repeat that; `.LinkTitle` falls back to `.Title`, so it stays optional.
  `ValidateFrontmatter.checkSeriesWeights` fails the PR on two pages in a folder
  claiming the same weight -- Hugo's sort is stable, so that would silently
  mis-order the series rather than error.

  One CSS gotcha, since both render inside `.prose`: `.prose li + li` and
  `.prose ul, .prose ol` are 0-1-1 selectors and outrank a single class, so the
  series rules are written class-on-class. Without that the first progress tick
  sat 0.35rem higher than the other ten.

- **`/java-quick-start/other-tutorials/` is tiles from a frontmatter list.**
  `type: "tutorials"` -> `themes/foojay/layouts/tutorials/single.html`, with the
  same card rhythm as the advisory board. It deliberately does NOT copy the
  board's mechanism, though: the board derives its members from a folder of
  pages because each member has a profile worth a URL, whereas every entry here
  exists to send the reader somewhere else. A page per tutorial would mint a
  URL, a view-counter key and a file each, to hold four fields. So the list is
  `tutorials:` in the page's own frontmatter -- case 3 of the derive/default/ask
  rule, since which tutorials to recommend genuinely lives in an editor's head.
  Adding one is five lines above the `---`; the count in the header follows on
  its own. `imagebackground` is the same escape hatch board logos have, for
  artwork that needs its own ground in both themes.

- **The logo file is cropped to its artwork, and sized in CSS.**
  `themes/foojay/static/images/foojay-logo.png` was a 1500x500 export whose mark
  only occupied 1022x352 of it -- 32% of the width and 30% of the height was
  transparent margin. Since `site-logo.html` sizes the image by HEIGHT
  (`.logo__img { height }`), that padding was a silent 30% shrink: a 34px box
  drew a 24px wordmark, and every attempt to "make the logo bigger" fought the
  file rather than the CSS. The file is now cropped, so the height in CSS is the
  height the mark renders at (36px in the header, 44px in the footer). Re-export
  a padded PNG and both shrink again with nothing in the templates to show why.
- **The header search field is collapsed to its magnifier, and the button is a
  real submit.** An always-open 210px input plus two CTAs made the top bar run
  the full 1240px on a big screen. `search-form.html` now renders the field plus
  a `[data-search-toggle]` submit button; `nav.js` opens the field on the first
  click (`preventDefault`) and lets the second one through, so the form still
  submits with a query typed and a visitor with no JavaScript lands on the
  search page instead of clicking a dead icon. Three details are load-bearing:
  the collapsed input is `visibility: hidden` rather than only `opacity: 0`, or
  it stays a tab stop; that visibility is transitioned `0s linear .2s` when
  closing but `0s` when open, because **`focus()` on a `visibility: hidden`
  element does nothing** -- ease it in and the click lands the caret nowhere; and
  the drawer copy under `.header-actions--mobile` is deliberately excluded in
  both the CSS and the JS filter, since below 900px there is room for the field
  and no hover to discover an icon with. The nav also sits next to the brand now
  (`margin-left: auto` moved from `.primary-nav` to the header's own
  `.header-actions`), so the free space collects before the actions instead of
  spreading every item to the far edges.
- **WordPress's placeholder title/description are not metadata -- replace them
  on sight.** Yoast served `title: "foojay – a place for friends of OpenJDK"` /
  `description: "foojay is the place for all OpenJDK Update Release Information.
  Learn More."` on any page whose own meta it had nothing for, so the scrapers
  faithfully stored it: 7 pages shared one `<h1>` and 8 shared one
  `og:description`. They are now written per page (`/calendar/`,
  `/java-almanac/`, `/privacy-policy/`, `/terms-of-use/`, `/team/`, `/where/`,
  `/sustainability-for-java-developers/`, `/download/`). `.Title` is the page's
  H1 *and* half its `<title>`, so a placeholder there is visible twice on every
  affected page, not just in a search result.

  One post carried the same string as a PREFIX -- "foojay – a place for friends
  of OpenJDKJava 21+ on Raspberry Pi Zero 2 ..." -- because the `<h1>` fallback
  in `ConvertPosts.scrapePost` picked up a site-title element sitting next to the
  post title and Jsoup's `text()` ran the two together. `stripSiteSuffix` is now
  `stripSiteName` and strips that leading copy as well as the trailing "| foojay"
  one, so a re-scrape cannot put it back. Anchored to that exact wording, so a
  post legitimately about being a place for friends of OpenJDK keeps its title.
- **Sponsors appear site-wide via the sidebar**, not just on `/our-sponsors/`:
  `themes/foojay/layouts/partials/sidebar-sponsors.html` lists every sponsor
  tier-ordered, with the logo sized by tier (gold largest). Deliberately NOT
  subject to the TOC-height cull that drops the authors/JUGs widgets on long
  posts — sponsor visibility is contractual, so it stays on every page the
  sidebar renders on. It replaced a dead scaffold that read a
  `params.sponsors` list from `hugo.toml` that was never populated, so the
  widget had always silently rendered nothing.
- **Sponsor folders can be renamed; the WP URL still has to work.**
  `hugo.toml`'s `[permalinks] sponsors` maps the section to
  `/sponsor/:slugorcontentbasename/`, so the bundle FOLDER name is the URL. By
  default `ConvertSponsors.java` names it after the WordPress slug, which
  reproduces the legacy path exactly. Some WP slugs are SEO strings rather than
  names, though (Azul's was
  `azul-enterprise-java-platform-foojay-io-gold-sponsor`), so folders do get
  shortened by hand — `content/sponsors/azul/` is one. Two things make that
  safe, both automatic:
  1. Each bundle records `wpSlug:`, and the script looks a bundle up by folder
     name **or** `wpSlug` (falling back to `canonical:` for bundles written
     before `wpSlug` existed). Without this a re-run doesn't recognise the
     renamed bundle and writes a **second** one under the old slug — with an
     empty `authors:`, silently emptying the sponsor's article list. This
     actually happened once during the rename; the duplicate was deleted.
  2. When folder name and `wpSlug` differ, the script emits an `aliases:` entry
     for the old `/sponsor/<wpSlug>/` path, so the live URL keeps redirecting.

  So: rename the folder freely, then **re-run the script** to regenerate
  `wpSlug`/`aliases`. Never hand-edit those two fields.
- **A post's comment thread is keyed on its slug, never its pathname.**
  `comments.html` configures giscus with `data-mapping="specific"` +
  `data-term="<slug>"` (`or .Params.slug .File.ContentBaseName` — the same thing
  `:slugorcontentbasename` resolves to, so it is derived, not authored). Pathname
  mapping is the tempting default and it is a trap here: the trial deploy serves
  `/website/today/<slug>/` and production serves `/today/<slug>/`, so every thread
  created before cutover would be orphaned after it. `data-strict="1"` goes with
  it: non-strict mode is a fuzzy `in:title` search that takes the first hit, and
  30 foojay slugs are substrings of another slug
  (`...-postgresql-connections` inside `...-postgresql-connections-part-2-batching`),
  so it would silently show one post's comments on another. Strict matches a hash
  of the term in the discussion body instead. `ImportWpComments.java` writes that
  hash marker exactly the way giscus does (`<!-- sha1: … -->`, SHA-1 of the term,
  verified against giscus's `lib/utils.ts`), so an imported thread and one giscus
  creates for a new post are indistinguishable. Change one of these three
  (mapping, term, strict) and you must change the script too — they agree by
  construction, and a mismatch shows up as an empty comment section, not an error.

  The **link** giscus stamps into a discussion body needs the same care for a
  different reason: it defaults to the URL of the page the widget is running on,
  which during the trial is the throwaway `foojayio.github.io/website` one, and a
  body posted by a reader is not ours to rewrite afterwards. So `baseof.html`
  emits `<meta name="giscus:backlink">` on post pages from
  `partials/production-url.html` (= `params.productionBaseURL` + the page's path,
  with the trial prefix stripped), and `ImportWpComments.java` writes the same
  `https://foojay.io/today/<slug>/` form. That URL is correct *today* as well —
  it's the live WordPress post — so nothing is broken before the switch. Note the
  backlink is deliberately not `.Params.canonical`: on the 838 cross-posted
  articles that field points at the original publisher, not at foojay. This is
  the only place a trial URL could leak somewhere permanent; everything else
  (canonical, og:url, RSS, JSON-LD) is regenerated on every build.
- **The read counter is ours, first-party, and keyed `<section>/<slug>`.** The
  view count on a post, page, pedia entry or author profile comes from a
  Cloudflare Worker + D1 table on `foojay.io/api/views/*` (`worker/views/`), not
  from GoatCounter, Plausible, Cloudflare Analytics or anything else hosted.
  Three reasons, and all three have to hold:
  1. **Privacy by construction, not by policy.** The Worker receives a slug and
     stores a slug and an integer. No cookie, no identifier, no IP, no user
     agent — there is nothing there to anonymise. The TODO asked for something
     that isn't Google Analytics; this is a stronger answer than swapping one
     tracker for a politer one.
  2. **First-party, so the number is true.** A third-party analytics domain is
     blocked for a large share of an audience of Java developers, and the count
     would silently run tens of percent low — the one failure mode a *published*
     number can't have. Nothing distinguishes `foojay.io/api/views` from the
     rest of the site. (foojay.io is already on Cloudflare DNS, so the route
     needed no migration.)
  3. **One number, not two.** WordPress's count is loaded in as the Worker's
     `legacy` column and live views accumulate in `live`; `/all` returns the
     sum. So no template anywhere adds a legacy count to a live one, and there
     is no `legacy_views:` frontmatter field — which the TODO floated and which
     would have meant rewriting 2145 content files on every catch-up run.

  **Counting and displaying are separate on purpose.**
  `partials/views-beacon.html` posts the slug (`navigator.sendBeacon`, so
  nothing on the page waits for it) and renders nothing unless
  `[params.views] endpoint` is set, so a local or unconfigured build never
  counts. The number shown comes from `data/views.json`, refreshed by
  `scripts/FetchViewCounts.java` at every deploy and four times a day, and is baked into
  the HTML — no JavaScript, no dash that becomes a number after paint, and it
  works on cards, which a per-page client-side fetch cannot do well.
  Up-to-a-day-stale is not a defect in a view count.

  **`partials/views-key.html` is the single definition of what gets counted and
  under which key** — `views.html` (display), `views-beacon.html` (counting) and
  `FetchWpViews.java` (the import) all follow it, and a drift between them shows
  up as a number that silently stays at zero rather than as an error. Add a
  section to the `$counted` slice there and it starts being counted; nothing
  else changes, because the Worker validates the key's *shape* rather than an
  allow-list of section names (an allow-list would mean a forgotten redeploy
  silently dropping a whole section).

  The slug half is `or .Params.slug .File.ContentBaseName`, which is what
  `:slugorcontentbasename` resolves to — derived from the bundle folder, nothing
  for an author to write or get wrong, and exactly the key a giscus comment
  thread uses. A **pathname** would have been the tempting alternative and is a
  trap for the same reason it is with giscus: the trial serves
  `/website/today/<slug>/` and production serves `/today/<slug>/`, so every
  count would reset to zero at cutover. The **section half** keeps the four
  counted sections from colliding. They don't today — 2561 slugs across posts,
  pages, pedia, authors and sponsors are all distinct — but that is luck, not a
  rule, and this is a permanent store where a collision would silently merge two
  pages' numbers.

  **Author pages are counted but have no WordPress baseline**, so they start at
  zero on the day the Worker goes live — the plugin's user-archive counting is
  off on foojay.io. Don't go looking for the import that "must have failed".
- Posts are contributed via PR (see `CONTRIBUTING.md`); the repo is public.
- **`data/jugs.yaml` is generated, not authored here** — it's overwritten by
  `scripts/FetchJugs.java` at every deploy and every external-content sync. Never add/edit a
  JUG entry directly in this repo; changes belong upstream in
  [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs).
