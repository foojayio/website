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
  and `template/` (article / author / page starter files + the category list;
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
  and daily (`meetup-sync.yml`, before `FetchMeetupEvents.java`), both of
  which commit the refreshed file back to `main` — same pattern as
  `events.json`. JUG leaders add/update their own group by opening a PR
  against that repo, not this one. Derives `meetup_slug`/`meetup_url`
  whenever a JUG's `website` is a meetup.com URL.
- **`scripts/FetchJavaChampions.java`**: regenerates `data/java-champions.yaml`
  from [aalmiray/java-champions](https://github.com/aalmiray/java-champions)'s
  single `java-champions.yml` file — the data behind
  [javachampions.org](https://javachampions.org/). Run at every deploy and
  daily, same as `FetchJugs.java` above. Champions add/update their own entry
  by editing that file directly upstream, not this repo. No coordinates yet
  (unlike JUGs) — a pending PR
  ([aalmiray/java-champions#318](https://github.com/aalmiray/java-champions/pull/318))
  adds `location: {lat, lng}` via a one-time geocoding script, but it hasn't
  merged; pick that field up here once it does, same way `FetchJugs.java`
  reads JUG coordinates.
- **`scripts/FetchMeetupEvents.java`**: pulls JUG events via Meetup's GraphQL
  API for `.github/workflows/meetup-sync.yml` (daily cron), writing
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
  `authors:` slug without a matching author bundle), run by
  `.github/workflows/pr-check.yml` in lieu of a visual preview (GitHub Pages
  has no per-PR preview URLs).
- **`.github/workflows/build-deploy.yml`**: builds with Hugo and deploys to
  GitHub Pages on push to `main`. Also refreshes and commits `data/jugs.yaml`
  before building (see `FetchJugs.java` above) — needs `permissions.contents:
  write` and a `[skip ci]` commit message for exactly this reason (otherwise
  that commit would re-trigger the same workflow).
- **`data/jugs.yaml`**: auto-generated by `scripts/FetchJugs.java` — see
  above. Never hand-edit it; add/fix a JUG upstream in GlobalWWJugs instead.
  Rendered at `/jugs/` (`content/pages/java-user-groups-jugs.md`, `type:
  "jugs"` → `themes/foojay/layouts/jugs/single.html`), including a Leaflet +
  marker-clustering world map built from its `latitude`/`longitude` fields.
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
7. **Comments/likes/views** (`themes/foojay/layouts/partials/stats.html` +
   `comments.html`) are built (giscus for comments+reactions, GoatCounter for
   a live client-side view count) but **not yet wired into
   `posts/single.html`** and not yet added to `hugo.toml` params — this was
   mid-flight when the search work took priority. To finish: call both
   partials from `posts/single.html`, add `[params.giscus]` (repo/repo-id/
   category/category-id from giscus.app, after enabling GitHub Discussions)
   and `[params.goatcounter]` (`code = "..."` after signing up) to hugo.toml.
   Also open: whether to capture each post's current WP view count into a
   `legacy_views` frontmatter field (cheap, and the number disappears once
   WordPress is decommissioned) and whether to build a one-time seeding
   script to backfill it into GoatCounter via its `/api/v0/count` API (which
   does support backdated `created_at` per hit for exactly this) — ask before
   building the seeder, it needs a real GoatCounter account to test against.
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
- **Galleries: raw WP HTML for migrated posts, `{{< gallery >}}` for new ones.**
  55 migrated posts carry WordPress gallery markup, preserved verbatim by
  `SELECTOR_PRESERVE` (two shapes: modern nested `<figure>`s, and 15 posts using
  the older `<ul class="blocks-gallery-grid">`, which had **no CSS at all** and
  rendered as a bulleted list until it was added). Nobody can be asked to type
  that markup, so `themes/foojay/layouts/shortcodes/gallery.html` is what an
  author uses: `{{< gallery "a.png" "b.png" >}}`, or
  `{{< gallery images="a.png|caption, b.png|caption" cols="2" >}}`. `cols` is a
  maximum — the CSS grid auto-fits to fewer columns on narrow screens. Filenames
  go through `resource-url.html`, so a bare name means the file next to
  `index.md`. No lightbox wiring needed: `static/js/lightbox.js` binds every
  `.prose img`.
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
- Posts are contributed via PR (see `CONTRIBUTING.md`); the repo is public.
- **`data/jugs.yaml` is generated, not authored here** — it's overwritten by
  `scripts/FetchJugs.java` at every deploy and daily sync. Never add/edit a
  JUG entry directly in this repo; changes belong upstream in
  [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs).
