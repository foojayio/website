# Project context for Claude Code

This repo replaces the foojay.io WordPress site with a static Hugo site,
scaffolded to run in parallel with the live WordPress site during a
trial/transition period before cutover. If you're picking this up fresh in
IntelliJ's terminal, read this before making changes.

**`CUTOVER.md` in the root is the ordered runbook for going live** — DNS, the
GitHub Pages custom domain, the Cloudflare proxy sequence, the final WordPress
harvest, and what gets deleted afterwards. The obligations scattered through
this file as "before cutover" / "at cutover" are collected there in the order
they have to happen; add to it rather than only noting a new one here.

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
chore — delete it. When a knob does have to exist, `validate/Frontmatter.java`
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
  `validate/Frontmatter.java`. Add starter files to `template/`, not `archetypes/`.
- **`scripts/` is grouped by lifetime, not by verb** — `fetch/` (external data,
  runs in CI, outlives the migration), `transfer/` (reads the live WordPress
  site, deleted at cutover), `cleanup/` (one-off rewrites of what is already in
  `content/`, deleted at cutover), `validate/` (PR-time checks) and `shared/`
  (common code, never run on its own). The question a folder answers is "does
  this still exist after cutover?", which is the one that actually matters here:
  two of the five folders get deleted whole, and nothing has to be untangled
  from the ones that stay. So a script is named for **what it produces**, with
  the folder supplying the verb — `fetch/Jugs.java`, not `FetchJugs.java` in a
  flat directory of twenty. `shared/HtmlToMarkdown.java` is pulled in with
  `//SOURCES ../shared/HtmlToMarkdown.java`; jbang resolves that relative to the
  calling script, and every script still runs from the repo root because they
  resolve `content/`/`data/` against the working directory, not their own path.
  `scripts/README.md` is the per-folder index — add a new script's line there.
- **Two jbang conversion scripts** in `scripts/`: `transfer/Posts.java` and
  `transfer/Authors.java`. They scrape the live foojay.io site (no WP admin/DB
  access was used or assumed) and write Hugo content markdown. Both are
  idempotent (safe to re-run repeatedly) and respect a `frozen: true` frontmatter
  flag to avoid clobbering hand-edited files. (The one-off `ConvertPages.java`
  and `ConvertPedia.java` scrapers were removed once `content/pages/` and
  `content/pedia/` were converted — those sections are hand-maintained now; only
  posts and authors keep growing on the live site, so only those are re-scraped.)
- **`scripts/fetch/Jugs.java`**: regenerates `data/jugs.yaml` from the
  community-run [World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs)
  (one Markdown-with-YAML-frontmatter file per JUG under its `_jugs/`
  folder). Run at every deploy (`build-deploy.yml`, before the Hugo build)
  and once a day (`sync-external-content.yml`, before `fetch/JugEvents.java`), both of
  which commit the refreshed file back to `main` — same pattern as
  `jug-events.json`. JUG leaders add/update their own group by opening a PR
  against that repo, not this one. Derives `meetup_slug`/`meetup_url`
  whenever a JUG's `website` is a meetup.com URL.
- **`scripts/fetch/JavaChampions.java`**: regenerates `data/java-champions.yaml`
  from [aalmiray/java-champions](https://github.com/aalmiray/java-champions)'s
  single `java-champions.yml` file — the data behind
  [javachampions.org](https://javachampions.org/). Run at every deploy and
  once a day, same as `fetch/Jugs.java` above. Champions add/update their own entry
  by editing that file directly upstream, not this repo.

  **It also resolves the coordinates behind the world map on
  `/java-champions/`, from three sources in order**, the geocoding logic lifted
  from Frank's own
  [aalmiray/java-champions#318](https://github.com/aalmiray/java-champions/pull/318):

  1. an upstream `location: {lat, lon}` on the member — what that PR adds, so
     the moment it merges those champions cost no request at all. Same
     self-retiring shape as the `transfer/LegacyViews.java` view-count bridge:
     the better source wins on its own, with nothing here to switch off.
  2. **`data/geocode-cache.yaml`, keyed by the PLACE STRING and not by
     champion.** That key choice is what makes this affordable on a script that
     runs on every push: 422 champions live in 252 distinct places, so the 22
     in "USA" and the 16 in "London, UK" are one lookup each, and renaming a
     champion or editing their socials costs nothing. Committed, like
     `data/legacy-views.json`, because it is the only copy — and both workflows
     `git add` it, without which every deploy would re-geocode from scratch.
  3. [geocode.maps.co](https://geocode.maps.co) on a cache miss only — i.e.
     genuinely new or moved champions, normally none. Free tier is 5000/day at
     1/sec, so even a cold rebuild (252 lookups, ~5 min) fits in one day's
     quota. Needs **`GEOCODE_API_KEY`**, a *repository* secret (not an
     environment one: the jobs that run this declare no `environment:`).

  The query is `"<city>, <country>"` with country = residence, falling back to
  nomination — **byte-identical to what PR #318's `onetimeAddLocations.java`
  builds**, on purpose, so our coordinates and the ones upstream will store for
  the same champion agree and nobody visibly moves when source 1 takes over.

  Four behaviours are load-bearing:
  - **It never fails over geocoding.** No key, a dead geocoder, an exhausted
    quota: the run still writes every champion, just without coordinates for
    the ones not yet cached. Both workflows that call this commit their result,
    so a hard failure here would block a deploy over a map. Same posture as
    `fetch/ViewCounts.java`.
  - **A definitive miss is cached; a transient failure is not.** An empty
    result array means the geocoder answered and knows nowhere by that name, so
    `found: false` is recorded and we stop asking daily — exactly the
    distinction `fetch/JugEvents.java` draws between a 404 (a real "not found"
    someone can fix) and a fetch error. An HTTP/timeout failure is retried next
    run rather than being written down as "this place does not exist".
  - **401/403/429 abort the whole geocoding pass**, rather than making 251 more
    requests to discover the key is wrong or the quota is gone. A 5xx or a
    timeout does not: one flaky response must not abandon a cold run that is
    200 places in, so those count toward a *consecutive* failure limit (5) —
    which is what a geocoder genuinely being down looks like.
  - **`0,0` is rejected as invalid.** It is in the Atlantic and is what a
    geocoder returns when it parsed something it did not understand.

  `--no-geocode` skips the lookups entirely, `--geocode-limit N` caps how many
  new places one run may resolve (default 500), `--geocode-key` passes the key
  without an env var.

  **The cache is already primed and committed — all 252 places, 420 of the 422
  champions on the map — so the secret is not blocking anything.** It is only
  consulted for champions added or moved *from now on*; until it is set, a newly
  added champion is simply absent from the map and everyone else is unaffected.
  A warm run with no key at all makes zero requests, keeps every coordinate, and
  leaves the cache byte-identical (verified).

  The 2 champions not on the map are **malformed source strings, reported on
  every run** rather than silently dropped — a cached `found: false` is never
  queried again, so without that report they would vanish for good. Both need
  fixing upstream, not here: `Maassluis, South Holland Province, The Netherlands`
  (a "Province" suffix Nominatim can't parse) and `Zhytomyr/Limassol, Cyprus`
  (two cities joined by a slash). Same posture as
  `fetch/DiscoverJugCalendars.java` printing its near-misses instead of guessing.
  Note there is deliberately **no country-level fallback** for these: a champion
  who did record a city would then be shown in the middle of the Netherlands with
  nothing saying why, and the upstream typo would stop being visible.
- **`scripts/fetch/JugEvents.java`** (was `FetchMeetupEvents.java`): pulls JUG
  events for `.github/workflows/sync-external-content.yml`, writing
  `data/jug-events.json`. **Needs no credential, and is not Meetup-specific.** It
  used to POST to Meetup's GraphQL API, which requires a Meetup Pro
  subscription plus an OAuth client — a paid dependency for reading events
  Meetup already publishes to anyone, and one that capped the calendar at the
  32 JUGs who use Meetup at all. **iCal is the generic system**: of the 90 JUGs
  in the directory, 30 record a `calendar:` URL and every single one is an iCal
  feed — Google Calendar (4), a file on the JUG's own site (5), or Meetup's own
  export. So the script speaks iCal to whatever a JUG publishes:

  1. `calendar:` from `data/jugs.yaml`, whoever hosts it. Same format Luma,
     Eventbrite, Tito, Bevy and Mobilizon export too. These feeds **do** carry
     `LOCATION` (a postal address, split into venue + city by `splitLocation`,
     on commas *and* semicolons — one feed uses the latter).
  2. Otherwise `meetup_slug:` → `https://www.meetup.com/<slug>/events/ical/`,
     the feed a member subscribes to from their calendar app.
  3. For anything still missing a venue — i.e. every Meetup event, since
     Meetup's iCal export has **no `LOCATION` property at all** — the
     schema.org JSON-LD `Event` block on the event's own page, the format
     Meetup, Eventbrite, Luma and WordPress all publish for search engines.
     That pass is best-effort: a page that fails or won't parse leaves
     `venue`/`city` null and keeps the event.

  Result: 43 groups and 34 events, 32 with a venue, where the Meetup-only
  version managed 32 groups, 25 events and 0 venues. Start/end times keep the
  feed's real IANA `TZID:Europe/Berlin` rather than a fixed offset, so a
  monthly event stays correct across a DST change.

  Both sources are public and machine-readable, and meetup.com's `robots.txt`
  permits the iCal route (it disallows the rss/atom/xml variants of that same
  events route) and lists event pages in its own sitemap — checked, not
  assumed; check it again before widening what this fetches. Requests identify
  themselves as foojay.io (`USER_AGENT`) rather than posing as a browser, go
  one at a time with a pause between them, and the calendar links every event
  back to its source. **Don't** reach for the `__NEXT_DATA__` Apollo state on a
  Meetup group page instead: it has more (RSVP counts), but it is the internal
  state of Meetup's front-end app, it changes with any deploy of theirs, and
  `/_next/data/*` is disallowed.

  Four behaviours worth keeping:
  - A **404 is recorded as "not found"**, not as a fetch failure: the URL in
    GlobalWWJugs is wrong or the group was renamed, which a JUG lead can fix (8
    of 43 are in that state today, named under the calendar).
  - **Two JUGs pointing at one feed** is an upstream mistake and would list the
    same events twice under two names, so the second is skipped and reported
    (MuensterJUG's `calendar:` is HessenJUG's Meetup feed).
  - A **meetup.com `calendar:` URL is normalised** to `/events/ical/` — the
    directory has one entry pointing at Meetup's HTML page and another carrying
    a `/de-DE/` locale prefix. This is the one platform whose URL shape we
    already know; nothing else is rewritten.
  - **Past events are filtered out here**, not in the template: a JUG's own
    feed is its whole history (one Google Calendar holds 170 events back to
    2014), so without it the calendar would fill with 2014.

  It runs **once a day** — ~150 requests a run, and an event is announced days
  ahead, not hours. That cadence is why `sync-external-content.yml` is daily
  and the read counter moved to its own six-hourly `sync-view-counts.yml`:
  everything external here (JUG list, Champions, events) changes slowly, and
  the view count is the one thing that moves continuously. Both workflows
  commit to `main`, so they share a `concurrency: data-sync` group and rebase
  before pushing rather than racing. And it **only rewrites `data/jug-events.json` when the events themselves
  changed**: `generatedAt` moves on every run, so writing unconditionally would
  commit, and therefore deploy, on a timestamp. `--dry-run` / `--limit N` /
  `--jug <slug>` print the JSON instead of writing a file that would be missing
  every group they skipped; `--no-venues` skips the JSON-LD pass.
- **`scripts/fetch/DiscoverJugCalendars.java`**: run by hand, never in CI. Reports
  JUGs whose own website advertises a calendar their GlobalWWJugs entry doesn't
  record — 45 of the 90 have neither `calendar:` nor `meetup_slug:`, so they
  can't appear on `/calendar/` at all. It exists because the answer belongs
  **upstream**: `data/jugs.yaml` is generated, a local edit would be wiped by
  the next `fetch/Jugs.java` run, and a fetcher that scraped 45 home pages on
  every sync would be fragile and invisible. So this finds them once and prints
  the frontmatter lines to add (`--yaml`).

  The verification is the point. A JUG's site links to sibling JUGs and to
  Meetup's own marketing pages, so "the page mentions meetup.com" is not
  evidence: a candidate is only reported as confident when its iCal feed
  actually loads **and** the group's name shares a significant word with the
  directory entry (stopwords like "java"/"user"/"group"/"jug" dropped, camel
  case split). That check is what kept `JavaforumMalmo` → `jforum-stockholm`
  out of the upstream PR. Everything else — a Luma or Tito link, an `.ics` that
  404s, a name that doesn't match — is printed as "needs a human", never as a
  suggestion. First run: 13 confident, 2 near-misses that were right anyway
  (`DubJUG` → "Dublin Java User Group", `WarsawJUG` → "Warszawa JUG"), 5 for a
  human. All 15 went upstream as
  [GlobalWWJugs#98](https://github.com/World-Wide-JUGs/GlobalWWJugs/pull/98).
- **`scripts/transfer/Sponsors.java`**: converts the sponsor section from the live
  WP site into `content/sponsors/<wp-slug>/index.md` page bundles (logo pulled
  local as a bundle resource, About text through `HtmlToMarkdown`). Reads the
  index at `/our-sponsors/` for the tier, then each `/sponsor/<slug>/` profile
  for the rest. Idempotent and `frozen: true`-aware like the other
  `transfer/` scrapers, and run by hand for the same reason they are — it scrapes the
  WordPress site that goes away at cutover, so it does **not** belong in CI
  next to `fetch/Jugs.java`/`fetch/JavaChampions.java` (those pull from upstream GitHub
  repos that outlive the migration). See "sponsors ↔ articles" below for the
  one field it deliberately does not own.
- **`scripts/cleanup/EnlighterToFences.java`**: rewrites legacy EnlighterJS code
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
- **`scripts/cleanup/GalleriesToShortcode.java`**: one-off migration that
  replaced the WordPress gallery markup in `content/` with the
  `{{< gallery >}}` shortcode — 55 posts, 94 galleries, 259 images, both block
  shapes (nested `<figure>`s and the older `<ul class="blocks-gallery-grid">`).
  Same reasoning and same shape as `cleanup/EnlighterToFences.java`: a
  contributor can't be asked to type 30 lines of block markup, and a gallery is
  a list of filenames. It calls `HtmlToMarkdown.galleryShortcode`, which the
  scrapers now use too, so a re-scrape emits the same thing and a re-run here is
  a no-op. `--dry-run` / `--path` as usual. See the gallery convention below for
  what the shortcode derives rather than stores.
- **`scripts/cleanup/CloudflareEmails.java`**: one-off migration that put back the
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
- **`scripts/transfer/Comments.java`**: one-off migration that moves the legacy
  WordPress comments (580 approved, across 270 posts, read from foojay.io's open
  `/wp-json/wp/v2/comments` — no admin access needed) into the GitHub Discussions
  that giscus reads, so cutover doesn't reset every post to zero comments. Posts
  as the foojay.io account (`GITHUB_TOKEN`), because the commenters' GitHub
  identities are unknown, and opens each comment with `Originally posted by
  <author> on <date> in Foojay.io Discussions.` — the attribution the TODO asked
  for. Bodies go through `HtmlToMarkdown.toMarkdown` (made public for this), so a
  comment gets the same entity/fence/nbsp repairs the post bodies got.
  Deliberately **not** part of `transfer/Posts.java`, which the TODO wondered about:
  that script writes files and is re-run against the live WP site constantly,
  while this writes irreversible public content to a third-party API and needs a
  credential — the same reason `transfer/Sponsors.java` is run by hand. Idempotent
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
  `transfer/Sponsors.java` is run by hand: it writes outside the repo and needs a
  credential. See "read counter" under the conventions below for why this
  exists instead of a hosted analytics service, and `worker/views/README.md`
  for the setup steps. **Not yet deployed.**
- **`scripts/transfer/LegacyViews.java`**: captures the view counts WordPress holds for
  every post, page and pedia entry (the Post Views Counter plugin exposes them
  on an open REST route — no admin, DB or credential needed, same posture as
  `transfer/Comments.java`) into `data/legacy-views.json`, and with `--seed`
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
- **`scripts/fetch/ViewCounts.java`**: the CI half — reads
  `/api/views/all` into `data/views.json` at every deploy and four times a day
  (`sync-view-counts.yml`, its own workflow — see below), so the
  numbers are baked into the HTML. **Never fails the build**: if the counter is
  unreachable it keeps the committed file and exits 0.

  That degradation is what is happening today, and it is why
  `sync-view-counts.yml` also runs **`transfer/LegacyViews.java --write-views`**
  once a day: with no Worker on the route, "keep the committed file" means the
  counts are frozen at the last seed and drift further behind daily. WordPress is
  still live and still counting, and that script already asks it for exactly these
  numbers, so it writes `data/views.json` too. No Cloudflare, no credential.

  The bridge **retires itself** — `fetch/ViewCounts.java` runs after it in the
  same job, so the Worker's `legacy + live` wins the moment the route answers.
  Two things follow: re-run `--seed` when the Worker goes up, or its `legacy`
  snapshot will be older than what the bridge was already showing and the number
  on the page will visibly DROP; and the bridge lives in `transfer/` because it
  reads WordPress, so it dies at cutover together with the workflow step and the
  second cron entry that exist only for it. Don't move it to `fetch/`.

  The workflow has **two cron entries** for the same reason: reading the Worker is
  one request and can be six-hourly, while the bridge is ~2200 requests against
  foojay.io's live WordPress and runs once a day. The step keys off
  `github.event.schedule` to tell them apart.
- **`scripts/cleanup/HeadingAnchors.java`**: one-off migration that removed the
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
  `cleanup/EnlighterToFences.java` does. `--dry-run` / `--path` as usual.

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
- **`scripts/cleanup/NormalizeMarkdown.java`**: one-off migration that brought
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
- **`scripts/cleanup/Descriptions.java`**: one-off migration that put back the
  spaces Yoast dropped when it built a post's meta description by concatenating
  the body's text nodes with no separator -- so a heading ran into the paragraph
  after it and the boundary punctuation lost its space
  (`...using the Service Layer pattern.What you'll learn`). That string is what a
  reader sees in a search result, a link preview and the `BlogPosting` JSON-LD,
  even though the page itself renders fine. 22 posts.

  The rule lives in `HtmlToMarkdown.repairRunOnSentences`, which
  `transfer/Posts.java` and `transfer/Sponsors.java` now apply to the scraped
  description, so a re-scrape emits the repaired form and a re-run here is a
  no-op. **The guards are the whole design**, because the damage is spelled
  exactly like a Java identifier: the word before the punctuation must start
  lowercase (which rules out `System.Logger`, `FetchType.EAGER`) and its
  whitespace-delimited token must hold no other `.` (which rules out
  `sun.misc.Unsafe`). `:` is repaired alongside `.!?` -- same heading-boundary
  artefact, and no identifier spelling to collide with.

  What it **declines** is printed rather than guessed at, the way
  `fetch/DiscoverJugCalendars.java` reports its near-misses: candidates whose
  preceding word is capitalised, which no lexical rule can tell apart from a type
  name. It reports **3** today and all three are correct refusals
  (`System.Logger`, `FetchType.EAGER`, `DALL.E API` -- a mis-typed DALL-E), so a
  non-empty report is not automatically a problem. The 4 that were real damage
  (`ReadyNow.Azul`, `MongoDB.In`, `Hibernate API.If`, `Caching.Now`) were fixed by
  hand. An exception list would have automated those 4 and was rejected on
  purpose: it rots, and a space inserted into a type name reads as our bug where a
  missing space reads as WordPress's. Note that a hand fix here is only as durable
  as the post -- `transfer/Posts.java` rebuilds frontmatter from scratch, so
  re-scraping one of those 4 reverts it, and the script's report is what catches
  that. Note the residual
  `learnIn`-style damage (a lowercase letter running straight into a capital, no
  punctuation at all) is **not** repairable: it is indistinguishable from
  `JavaFX`, `OpenJDK` and `MongoDB`. `--dry-run` / `--path` as usual.
- **`scripts/validate/Frontmatter.java`**: PR-time content check (required
  fields present, no dangling `related_posts` references, no sponsor
  `authors:` slug without a matching author bundle, no emoji in a post title, no
  two pages in a folder claiming the same series `weight`), run by
  `.github/workflows/pr-check.yml` in lieu of a visual preview (GitHub Pages
  has no per-PR preview URLs).

  **`checkDrafts` covers `draft/`, which the rest of the PR check cannot see.**
  Drafts live outside `content/` so they don't publish themselves, which also
  means the `hugo --gc --minify` step never reads one — so a submission missing
  every required field, naming an author who doesn't exist and colliding with a
  published URL went green. The first-time contributor's PR was the only one
  nothing checked. It applies the same rules a published post gets
  (`checkRequired`, `SLUG_FMT`, `slug:`-matches-folder, `TITLE_EMOJI`,
  `related_posts`), because publishing is only a maintainer moving the folder
  into `content/posts/<y>/<m>/<d>/` — a rule that applies after the move fails
  when the author is gone. Required fields are the four `template/post.md`
  marks "Required" (plus `categories`), each verified to hold across all 2147
  published posts before being required. Two checks go further than
  `content/posts` gets, both silent failures nobody would look for: an
  `authors:` slug with no author bundle (the post renders but never appears on
  the author's profile), and a hero `image:` naming a file that isn't in the
  folder (a remote URL is left alone — 76 published posts use one). It also
  catches the near-miss of copying `template/post.md` to `draft/<slug>.md`
  instead of `draft/<slug>/index.md`, and leftover template placeholder text.

  This is why **`.github/PULL_REQUEST_TEMPLATE.md` is not a checklist.** It was
  one, unchanged since the scaffold commit, and every item had rotted: it asked
  for `tags` (no such taxonomy), for images under `static/images/` with absolute
  paths (they go in the bundle), for `draft: false` (no such mechanism), and for
  a preview with `hugo server -D` (which cannot render a draft — `draft/` isn't
  mounted). Asking an author to self-certify five things is a tax on the
  publishing goal, and four-fifths of it was wrong. Derive it, don't ask: the
  checks moved into the script above, and the template is now the one question
  a machine can't answer — what's in this PR.
- **`.github/workflows/build-deploy.yml`**: builds with Hugo and deploys to
  GitHub Pages on push to `main`. Also refreshes and commits `data/jugs.yaml`,
  `data/java-champions.yaml` and `data/views.json` before building
  (see `fetch/Jugs.java` above) — needs `permissions.contents:
  write` and a `[skip ci]` commit message for exactly this reason (otherwise
  that commit would re-trigger the same workflow).
- **`data/jugs.yaml`**: auto-generated by `scripts/fetch/Jugs.java` — see
  above. Never hand-edit it; add/fix a JUG upstream in GlobalWWJugs instead.
  Rendered at `/jugs/` (`content/pages/java-user-groups-jugs.md`, `type:
  "jugs"` → `themes/foojay/layouts/jugs/single.html`), including a Leaflet +
  marker-clustering world map built from its `latitude`/`longitude` fields.
- **`data/views.json`**: auto-generated by `scripts/fetch/ViewCounts.java` —
  `slug -> total reads`, the numbers rendered on posts and cards. Never
  hand-edit it. Seeded from `data/legacy-views.json` so the counts are live on
  the site *now*, before the Worker exists; once it is deployed this is
  overwritten with `legacy + live` on every build.
- **`data/legacy-views.json`**: auto-generated by `scripts/transfer/LegacyViews.java` —
  each post's WordPress view count at the last import. Committed because it is
  the **only** copy: these numbers vanish with the WordPress site, and they are
  what seeds the counter.
- **`data/java-champions.yaml`**: auto-generated by
  `scripts/fetch/JavaChampions.java` — see above. Never hand-edit it; add/fix
  an entry upstream in aalmiray/java-champions instead. Rendered at
  `/java-champions/` (`content/pages/java-champions.md`, `type: "champions"`
  → `themes/foojay/layouts/champions/single.html`), including a Leaflet +
  marker-clustering world map built from its `latitude`/`longitude` fields,
  same as `/jugs/`.

  **That map is one marker per PLACE, not per champion, and the grouping
  happens in the browser.** Both halves matter. The coordinate is a city
  centre — nobody's address, which the page says out loud — so the 16 champions
  in "London, UK" genuinely share one point, and 16 markers there would stack
  into a single unclickable pin with the other 15 unreachable at any zoom; the
  popup lists everyone in the city instead. And Hugo has no map mutation, so
  grouping in the template would mean ~420 `merge` calls to build a dict of
  lists, where the JS does it in one pass over the array it already has. The
  popup is built as **DOM nodes, not an HTML string** — a champion's name is
  upstream data, so it goes in as `textContent` and can never be markup. The
  grouping key is `toFixed(5)` rather than the raw float's string form, so two
  champions sharing a cache entry can't be split by `51.5072` vs `51.50720`.
- **`data/geocode-cache.yaml`**: auto-generated by
  `scripts/fetch/JavaChampions.java` — place string → coordinates (or
  `found: false`). Never hand-edit it, but **do** feel free to delete an entry,
  or the whole file, to force a fresh lookup: that is the documented way to
  retry a miss, and a full rebuild is ~250 requests inside the free tier.

## Known gaps / things to verify before relying on this

1. **Scraping selectors are unverified against real HTML.** The environment
   this was built in could only fetch pages through a markdown-extraction
   tool, not raw HTML, so the CSS selectors in the three `scripts/transfer/`
   scrapers (categories, tags, author link, related-posts links) are
   best-effort WordPress/Yoast conventions, not confirmed against
   foojay.io's actual theme markup. Title/description/canonical/image are
   solid (they come from standard meta tags + JSON-LD, which foojay.io does
   emit). **First thing to do**: run each script with `--url <a real post/author/page>`
   and check the output; fix the `SELECTOR_*` constants at the top of the
   file if something's empty.
2. **Most of the jbang scripts have never been executed.** The sandbox they
   were written in blocks outbound network access to arbitrary domains (only a
   markdown-fetch tool was available — enough to confirm the GlobalWWJugs and
   java-champions.yml frontmatter/schema by hand, not enough to run the actual
   GitHub API + raw-file fetch loop), so most of it has not been run against
   the live site or the GitHub API. Treat all of it as reviewed-but-untested
   code, same as `fetch/Jugs.java` was before Frank ran it locally.

   Three are now exceptions, run in full for real: `fetch/JugEvents.java` (see
   gap 3), `fetch/Jugs.java`, and **`fetch/JavaChampions.java`** — 422
   champions parsed, `country`/`social` flattening confirmed, and all 252
   distinct places geocoded and cached. What is still worth an eye there is the
   **map's coverage rather than its plumbing**: 97 champions record no city, so
   their marker is a whole country's centroid (a pin in the middle of the
   Atlantic-facing bulge of the USA, say), which is honest but coarse. If that
   reads badly, the fix is upstream — those champions adding a `city:` — not a
   heuristic here.
3. ~~**`FetchMeetupEvents.java`'s GraphQL query/endpoint need verification**~~
   — gone: `fetch/JugEvents.java` no longer uses the API, needs no Meetup Pro
   subscription and no `MEETUP_OAUTH_TOKEN`, and has been run for real against
   all 43 feeds (34 events, 32 with a venue). See its entry above.
   What to watch now is the *shape* of the two public sources: if venues start
   coming back empty, Meetup changed its JSON-LD; if whole groups start
   failing, check `robots.txt` and the iCal route still behave as described.
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
   (`jbang scripts/transfer/Comments.java --print-config` prints the block).
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
   `SEED_TOKEN`, deploy. Then `jbang scripts/transfer/LegacyViews.java --seed`. Do it
   early rather than at cutover: the route can go up while WordPress is still
   live (nothing in WP serves `/api/`), and a counter proven over weeks beats
   one switched on the day it has to work. The GoatCounter scaffold that used to
   live in `partials/stats.html` is gone — deleted, not migrated; it also
   carried an unwired share button, which nothing has replaced.
8. ~~**17 `/pedia/` entries are missing**~~ — fixed. `content/pedia/` now holds
   all 47 the live site publishes. The one-off scraper that ported them was
   deleted again once it had run, the same way `ConvertPedia.java` was, so the
   section stays hand-maintained.

   **Keep the discovery lesson, since the scraper is gone:** the gap existed
   because the WordPress `/pedia/` index page is **paginated and lists 31 of
   47**, so it silently looks complete. `foojay.io/terminology-sitemap.xml` is
   the honest count — check that, not the index, if the glossary is ever
   compared against the live site again. At the time of the port 41 of 47
   entries matched the live page exactly and the 6 that differed were
   storage-level only (`--` versus a real en dash, which Goldmark's typographer
   renders identically), so nothing upstream was left behind.

9. **The paid homepage banner carousel is NOT built — and it's revenue-bearing.**
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

- **Images have a per-file budget, and the deploy is why.** The built site hit
  **1.26 GB against GitHub Pages' hard 1 GB artifact limit** — and the warning for
  that ("Deployment might fail") lands on a run that otherwise goes **green**, so
  the site drifts past the limit invisibly until a deploy finally breaks, by which
  point the cause is two thousand posts old. It was never the HTML: 4100 pages are
  ~120 MB. It was 1229 MB of images, 78% of it in 1214 files over 200 KB, including
  the *same* 52 MB animated GIF in three bundles — all three of them `image:` heroes.

  `cleanup/images.py` brought the build to **0.69 GB** (307 MB of headroom), and
  two checks in `validate/Frontmatter.java` keep it there:
  `checkImageWeight` (no single bundle image over the budget) and
  `checkHeroImageStill`.

  **A hero must be a STILL image**, and that is not a format rule — 41 posts use
  `.webp`, 5 `.avif`, one `.svg`, all fine. The constraint is animation, because a
  hero is the card thumbnail, the `og:image` and the JSON-LD `image`, none of which
  animate: a link preview shows frame one, a grid of animating cards is unreadable,
  and the whole file downloads to draw a thumbnail. Detection is per container and
  **fails open** — GIF frame count via ImageIO, an `ANIM` chunk in WebP (Java has no
  WebP reader at all), an `avis` brand in AVIF; anything it cannot inspect passes
  rather than being guessed at. 17 of the 20 posts it flagged already showed the
  same file in their body, so the animation was not lost; the other 3 are reported
  for a human rather than silently edited.

  **JPEG, not WebP, for the large PNGs** — Frank's call, and the measured cost is
  small: over a 40-file sample JPEG q85 saves 81% against WebP q82's 89% (297 MB vs
  327 MB projected across 541 files). Either clears the limit, and JPEG is what the
  other 1400 images already are. Checked at 100% zoom on flat line art, JPEG's worst
  case, and it is indistinguishable. The hard limit is transparency: JPEG has no
  alpha, so the **22 files that genuinely use it stay PNG** — and note that is not
  the same as *having* an alpha channel, since many of these WordPress PNGs are RGBA
  with every pixel opaque, which is why the check reads the channel's actual minimum.

  Four things that script learned the hard way, all of which cost real damage:
  1. **Finish each bundle before starting the next.** The first version converted
     every GIF and rewrote references at the END; a commit landed mid-run and
     captured converted files with references still pointing at deleted `.gif`s —
     4 broken images on the live site.
  2. **Never unlink a source unless the destination is verified on disk.** Two
     thresholds disagreed (write at `< 90%` of the original, bail at `>= 100%`), so
     a result in that gap was never written and the code still deleted the GIF.
     `image3.gif` and `codeactions.gif` were lost and recovered from git.
  3. **Encode to a temp path and rename.** Writing straight to the destination let
     a killed run leave a **0-byte** `.webp`, which the "destination exists" guard
     then treated as real — permanently blocking that GIF, and it got committed.
     `.gitignore` now covers the staging files too.
  4. **Do not verify an animation by exact frame count.** libwebp merges duplicate
     consecutive frames, legitimately and heavily — 221→198 on one recording, and
     far more when re-encoding an already-lossy one. An equality check rejected 23
     of 65 GIFs; a 50% floor still rejected every rung of the two over-budget WebPs,
     leaving them silently at 6.2 MB. The floor is 20%, which catches gross
     truncation only.

  Two things deliberately **not** done: the 40 MB in 95 unreferenced bundle images
  is left alone, because `gallery.html` *derives* full-size originals from thumbnail
  names and a file absent from the markdown can still be in use; and nothing
  rewrites git history, so `.git` stays large — the 1 GB limit is on the artifact.

- **Idempotency everywhere**: any script touching `content/` must be safe
  to re-run without duplicating or destroying hand edits (the `frozen: true`
  flag pattern). This matters because these scripts get re-run repeatedly
  during the trial period against the still-live WP site.
- **URLs are load-bearing**: every converted post/author/page keeps its
  legacy path (`aliases:` + explicit `url:` for pages) — don't restructure
  URLs without adding an alias.

  **WordPress runs a redirect layer on top of the slugs, and it is now carried
  over -- in two places, because Hugo can only express one of the two shapes.**
  Checked against the live site: 2143 of 2147 post slugs match exactly, and on
  top of those the Redirection plugin serves 89 concrete 301s plus 3 regular
  expressions. The plugin's own export (`redirects.json`, taken 2026-08-19) is
  what these were built from -- it is the authoritative list, not a guess, and
  it is worth re-exporting just before cutover in case a rule is added
  meanwhile.

  1. **The 89 concrete rules are `aliases:` in `content/`.** Per-URL, so Hugo
     emits a redirect page for each and there is nothing to configure and
     nothing to forget. 62 were added from the export and carry a "From
     WordPress's Redirection plugin table" comment. **Chains are resolved to the
     final destination** -- the plugin has rules pointing at rules (`/china/` ->
     `/jugchain/` -> `/china-jug/` -> ...), and an alias aimed at another
     redirect is one search engines discard. Two rules also had to be
     **re-pointed**: the Quick Start section was renamed twice after they were
     written (`/getting-started-with-java/` -> `/java-learning-trail/` ->
     `/java-quick-start/`), so the recorded target 404s while the page is alive
     one path over.

     17 rules were **deliberately skipped**: their targets 404 on the live
     WordPress site too, so recreating them would mint a redirect to a missing
     page. `cutover/legacy-redirects.md` lists them, so nobody rediscovers that
     they were skipped on purpose.

  2. **The 3 regexes cannot be aliases and must be configured on the host.**
     `^/blog/(.*)` -> `/today/$1` (209,365 hits -- foojay's original URL scheme,
     and it covers `/blog/author/…`, `/blog/category/…` and the feeds, which
     per-post aliases could not), `^/almanac/(jdk|java)-([0-9+])` ->
     javaalmanac.io (102,636) and `^/docs/(.*)` -> `/today/` (530).
     **`cutover/legacy-redirects.md` has them as ready-to-paste Cloudflare
     Redirect Rules plus a verification script.** 312,531 hits between them, so
     this is the one item on the cutover list that is bigger than everything the
     aliases cover put together.

  Three post URLs additionally 404'd because the WP slug ends in an emoji that
  `stripEmoji` removed before the bundle folder was named from it. Those now
  carry the emoji URL as an `aliases:` entry, written as the literal character
  (which is what `%F0%9F...` decodes to). **One deliberate exception**: heading
  *fragments*. `cleanup/HeadingAnchors.java` (above) dropped WP's `#h2-N-slug`
  anchors, so section-level deep links minted before cutover land at the top of
  the post instead. Paths, aliases and frontmatter are untouched, and Hugo still
  generates an id per heading from its text — the "On this page" panel and its
  scroll-spy resolve every one of their 14k links. Accepted knowingly; fragment
  links into a blog post are rare next to the cost of keeping two conventions.
- **The trial deploy is noindex, and that is derived from baseURL.** The site is
  a byte-for-byte copy of the still-live WordPress content, so a crawlable
  foojayio.github.io/website put ~2600 duplicate URLs into Google's index
  competing with foojay.io for foojay.io's own rankings -- made worse by a
  permissive `robots.txt` that advertised the sitemap. Both `baseof.html` and
  `layouts/robots.txt` now compute `$isTrial` as `baseURL != params.productionBaseURL`
  and emit `noindex, nofollow` + `Disallow: /` when it holds. **Never turn this
  into a config flag**: as a derivation it flips itself the moment `baseURL`
  becomes the production URL, and there is nothing to remember to unset on the
  day it matters most. `build-deploy.yml` passes `--baseURL` on the command
  line and `site.BaseURL` reflects that override, so this reads the URL actually
  being built for. Verified both ways: a `--baseURL https://foojay.io/` build has
  no `noindex` outside `/search/` and the 404.

- **Every page self-canonicalises, pagers included, and `canonical:` frontmatter
  means "not ours".** `.Params.canonical | default $self` -- and `$self` is
  **not** `.Permalink`, because `.Permalink` on a paginated list is page 1's URL
  for *every* pager, so `/today/page/2/` and all 123 category pagers used to
  declare themselves duplicates of page 1, i.e. ask Google to drop the only crawl
  path into the older archive. `$self` comes from the pager's own `.URL`, joined
  to scheme+host off `urls.Parse site.BaseURL` -- **not** `absURL`, which would
  prepend the `/website` prefix a second time to a `.URL` that already carries
  it. (Hugo 0.165 has no readable pagination path: `page.SiteConfig` has no
  `Pagination` field, so don't reach for `site.Config.Pagination.Path`.)

  So the only legitimate `canonical:` is one pointing at a **different** page or
  site: the 838 cross-posted articles naming their original publisher, and
  `content/pages/download.md`, which is genuinely the same page as
  `/java-quick-start/install-java/`. The other 43 were removed -- 42 restated the
  page's own permalink, and `content/sponsors/azul/` pointed at
  `/sponsor/azul-enterprise-java-platform-foojay-io-gold-sponsor/`, the renamed
  bundle's OLD path, which exists only as the `aliases:` redirect the same script
  emits. A canonical aimed at a redirect is one search engines discard.

  **A cross-post canonical rots, and a dead one is worse than none** -- it tells
  Google the real version of the article is at a URL that 404s, so foojay's copy
  is suppressed in favour of nothing. All 838 were checked against the live web
  (2026-08-19): 790 resolve, **48 were removed** and one was a typo
  (`blog.franke.ch` -> `blog.frankel.ch`, which resolves). Those posts now
  self-canonicalise, which is the honest answer once the original is gone.

  Two things make this check harder than it looks, and both cost a false
  positive if ignored:

  1. **A 4xx from a bot wall is indistinguishable from a deleted page.** Medium
     (24 URLs) and blogs.oracle.com answer a script with 403 -- and Medium
     flip-flops between 403 and **410 Gone** on the SAME url between requests,
     so its 410 is bot mitigation, not a claim about the post. Those were kept.
     DZone's 410 WAS reproducible three times running, so that one went.
  2. **Check with two clients before believing a failure.** Python's urllib
     reported 5 `hirt.se` URLs as SSL failures and 6 `ashishtechmill.com` as
     timeouts; curl got 200 for hirt.se. `talktotheduck.dev` looked like a dead
     domain (connection refused) but actually resolves, serves a redirect stub
     over plain http, and 404s every article -- so those 26 are genuinely gone,
     which only the second client could establish.

  What was deliberately NOT removed: 35 URLs behind bot protection or a 502
  (`ashishtechmill.com` serves its root but 502s every article -- a server
  error is not "gone"). Dropping a canonical there would wrongly assert the
  article is original to foojay.

  **`transfer/Posts.java` reverts this.** It rebuilds frontmatter from the live
  WordPress page and copies `link[rel=canonical]` through without knowing
  whether the target still exists, so re-scraping any of those 48 posts puts the
  dead canonical back. Re-run the check before cutover, and after any bulk
  re-scrape.
  `transfer/Sponsors.java` no longer writes the field (and its dead
  `canonical:`-based bundle lookup went with it), so a re-scrape can't put it
  back.

- **`partials/paginator.html` is the single definition of how a page paginates.**
  Not a tidiness move: `<head>` renders before `{{ block "main" }}`, so the
  canonical above needs the pager *first*, and Hugo errors if `.Paginate` is
  called twice with different arguments -- a bare `.Paginator` in the head would
  collide with `posts/list.html`'s `.Paginate .RegularPagesRecursive.ByDate.Reverse`.
  One partial makes the arguments identical by construction, and a new paginated
  section gets its canonical by adding a branch. Two traps inside it: it must be
  plain `partial`, **never `partialCached`** (Hugo renders each pager by
  re-executing the same Page with the pager advanced, so a page-keyed cache would
  serve page 1's posts on every pager), and it uses one `return` at the end
  assigning into a variable, because Hugo rejects a `return` that other
  statements fall through past.

- **Author and sponsor profiles are BRANCH bundles, and that is what makes them
  paginate.** `.Paginate` accepts only `home`, `section`, `taxonomy` and `term`
  kinds -- on a page kind it errors with "pagination not supported for this
  page". A prolific author has 290+ articles and Azul's authors 360 between them,
  which was one grid several screens long with no way to reach the older half. So
  each profile is `content/authors/<slug>/_index.md` and
  `content/sponsors/<slug>/_index.md`, i.e. a section, and
  `partials/paginator.html` gained a branch for each. `template/author.md` and the
  `/today/how-to-submit-your-next-article-on-foojay-io/` guide both spell out the
  underscore, because an `index.md` there renders nothing.

  URLs are unchanged, which needed `[permalinks.section]` in `hugo.toml`: the flat
  `[permalinks]` keys apply to the `page` kind only, so without it Hugo would
  serve `/sponsors/azul/` from the file path instead of the legacy
  `/sponsor/azul/`. **The view-counter keys are unchanged too** --
  `.File.ContentBaseName` returns the folder name for `_index.md` exactly as it
  did for `index.md`, so `authors/frankdelporte` still resolves.

  **`content/authors/` is now FLAT.** The letter buckets
  (`content/authors/f/frankdelporte/`) existed only to keep 344 folders
  browsable, and they cannot survive this: Hugo turns every directory holding
  pages into a section, so the 23 letter folders became 23 sections claiming URLs
  like `/today/author/a/`. `transfer/Authors.java` writes flat now and its
  `bucketFor` is gone.

  Four things broke silently in the conversion, all of the same shape -- a filter
  that still parses, still runs, and now matches nothing:
  1. **`site.RegularPages` does not contain branch bundles.** Every
     `where site.RegularPages "Section" "authors"` returned an empty list, so the
     A-Z grid, the sidebar widget, the HTML sitemap and the byline lookup rendered
     *nothing* rather than failing. `partials/authors-all.html` is now the single
     definition (`where site.Pages "Type" "author"`), and the four callers go
     through it. **Sponsors were converted in the same change and two callers of
     theirs were missed**, both silent in exactly the same way: `/sitemap/`'s
     Sponsors section rendered empty (and its header counted "0 sponsors"), and a
     board member who also sponsors Foojay lost the link to their profile. Both go
     through `partials/sponsors-ordered.html` now -- it takes no arguments and
     returns every sponsor, so it is the sponsor-side `authors-all.html`. Reach for
     one of those two partials rather than filtering `site.RegularPages` by
     section: for authors and sponsors that filter compiles, runs, and matches
     nothing.
  2. **`.IsPage` is false for a profile.** `views-key.html` used it to decide what
     gets counted, so every author's read count would have silently stopped being
     recorded; `json-ld.html` used it too and stopped emitting `Person` on all 344.
     Both key off `type: "author"` now.
  3. **A cascade applies to the page that declares it, not just its
     descendants.** `cascade: {target: {kind: section}, type: "author"}` in
     `content/authors/_index.md` therefore hit that file as well, and
     `/today/author/` rendered with the *profile* layout -- an empty "0 articles"
     page where the A-Z grid belongs. `/our-sponsors/` did the same. Both index
     files now set `type:` explicitly, which always beats a cascaded value.
  4. **The RSS cascade targeted `kind: page`.** After the conversion that matched
     nothing and would have dropped every author feed. It targets `kind: section`
     now, and the template moved to `layouts/author/section.rss.xml`.

  A profile and its section's landing page are both sections in the same section,
  so they are told apart by `type` -- `author`/`sponsor` (singular, cascaded onto
  the children) versus `authors`/`sponsors` (explicit, on the index). That is also
  what routes them to `layouts/author/section.html` rather than a shared layout.

- **Internal links are `.RelPermalink`; only absolute-by-contract URLs are
  `.Permalink`.** `.Permalink` is built from the CONFIGURED baseURL, which is not
  what `hugo server` serves -- so with the trial baseURL in `hugo.toml` every post
  card on a local preview linked to `foojayio.github.io`, and clicking a result
  left localhost. 14 templates were changed. Keep `.Permalink` where the URL must
  be absolute and is not a navigation `href`: canonical, `og:url`, the JSON-LD
  `url`/`@id`, RSS `<link>`/`<guid>`, and the alternate-format `<link>` tags.

  Same trap, same cause, in `baseof.html`'s pager canonical: it built the URL from
  `site.BaseURL` and so pointed every local pager at the trial host. The origin
  now comes from the page itself -- `strings.TrimSuffix .RelPermalink .Permalink`
  is exactly `scheme://host` -- which is correct under `hugo server` and in a
  build, and still needs no pagination-path config lookup.

- **A pager on a profile carries an `#articles` anchor; one on a listing does
  not.** `partials/pagination.html` takes an optional `anchor`, appended to every
  link. On an author or sponsor profile the grid sits below a bio, stats and an
  About section, so paging without it threw the reader back to the top of the page
  and they had to scroll down again to page once more. `/today/` and the category
  pages pass nothing, because there the grid already *is* the top of the content.

- **`partials/pagination.html` is ours, not `_internal/pagination.html`.** Hugo's
  internal template produced three faults that could not be fixed from the CSS
  side, because they are in the markup: it wraps each arrow's glyph in a nested
  `<span>` inside the `<a>` (and the theme styled both `.page-item a` and
  `.page-item span` as a button, so arrows were a box inside a box and stood
  taller than the numbers); it marks the current page with an `<a
  aria-current>` while the theme's highlight rule targeted `.active span`, an
  element Hugo stopped emitting, so the selected page was never highlighted at
  all; and it renders a disabled First/Previous as an `<a>` with no href, which
  is not a link. Now every cell is one `.pagination__btn` -- arrows, numbers,
  current and disabled share a box by construction -- sized with `inline-flex` +
  a fixed `height` rather than padding, because a glyph and a 3-digit number
  have different intrinsic widths and padding alone let their heights drift.
  Non-links are `<span>`, so focus only lands on somewhere you can go.

  Three behaviours worth keeping: it renders **above and below** the grid (a
  215-page archive is several screens, so a pager only at the bottom means
  scrolling past everything to page again); the window is **always five numbers**
  when five exist, slid rather than truncated at the ends, so page 1 shows 1-5
  and page 215 shows 211-215; and the ends are the **numbers** 1 and `$total`
  rather than `««`/`»»`, which is what removed the "Page 3 of 215" caption -- a
  button reading 215 already says how many pages there are. The ellipsis appears
  only when a page is genuinely skipped, so at 6 pages the row reads `1 2 3 4 5 6`
  and not `1 2 3 4 5 … 6`.

  Use **integer arithmetic** throughout. Hugo's `math.*` helpers return float64,
  so a comparison against an int page number is always false -- this bit twice,
  silently: `math.Min`/`math.Max` in the window clamp (page 215 rendered three
  buttons) and `math.Abs` in the `--far` test (the class never rendered at all).
  Neither errors; both just quietly do nothing.

  **On a phone it becomes two rows and drops the outer numbers.** Nine cells
  (`1 … 106-110 … 215`) overflow a 390px screen, so under 34rem the numbers take
  their own line with « and » paired beneath (a better thumb target than one at
  each screen edge), and `.pagination__cell--far` -- current ±2, marked in the
  template from the distance to the current page, never by `:nth-child`, since
  which cells exist changes page by page -- is hidden, leaving `1 … 107 108 109 …
  215`. Separately: the whole site overflows horizontally at 390px (headings and
  cards are cut off, right edge flush at the viewport with no gutter). That is
  **pre-existing** and unrelated -- it reproduces at HEAD with the pagination
  changes stashed -- but it is why a mobile pager looks off-centre.

- **Feeds are posts-only and capped at 30.** `[services.rss] limit = 30` in
  `hugo.toml` -- Hugo's default is unbounded, which made `/index.xml` 3.85 MB of
  2584 items led by Quick Start pages carrying `pubDate Mon, 01 Jan 0001`, a date
  some aggregators drop the whole item over. `layouts/index.rss.xml` overrides the
  embedded template to filter `Section "posts"`, because the home page's
  `.RegularPages` is every page on the site and "what's new on foojay" means
  articles (which is what the WordPress feed served). One number governs the home,
  section, term and per-author feeds -- `authors/page.rss.xml` already read the
  same value.

- **`enableGitInfo` dates the pages that have no date, and sits BELOW `date:`.**
  441 URLs shipped without `<lastmod>` (the Quick Start steps, install-java, the
  board members) because those pages carry no `date:`. The `[frontmatter]` chains
  put `:git` last -- `lastmod = ["lastmod", "date", ":git", "publishDate"]` -- on
  purpose: a post's real date is in its frontmatter while its git date is when the
  *migration* ran, so leading with `:git` would stamp all 2147 posts with an
  Aug 2026 lastmod and tell Google the entire archive changed at once. Now 0 of
  2713 URLs lack a lastmod and posts keep their own dates.

  This needs **full git history at build time**, which is why
  `build-deploy.yml` checks out with `fetch-depth: 0`. On the default shallow
  clone every file resolves to the same single commit, so every page would claim
  one identical lastmod -- worse than having none, and invisible locally.

- **A post title carries no site name.** `<title>` is `.Title` alone (the home
  page is the exception, and a pager appends `(page N)` so pagers don't share a
  title). The `" | foojay.io - Friends of OpenJDK"` suffix cost 33 of the ~60
  characters Google renders, on 2147 titles that already average past that.

- **`og:image` always resolves, via a dedicated social card.** Pages without an
  `image:` -- the home page, `/today/`, 123 category pages, 345 author profiles --
  used to preview as a bare `summary` card with no picture. `baseof.html` falls
  back to `images/foojay-social-card.png` (1200x630, the wordmark on
  `--surface-navy`) and the card is always `summary_large_image`. There is a
  second generated asset, `images/foojay-logo-square.png` (512x512, white ground),
  used only as the schema.org `Organization` logo: Google composites a logo onto
  white and wants >=112px on its short side, and the header wordmark is
  light-blue-on-transparent, i.e. invisible exactly where Google would draw it.
  Both are derived from `foojay-logo.png` -- **don't** re-crop or resize
  `foojay-logo.png` itself to make one of them (see the logo note below for what
  padding does to the header).

- **Structured data covers three page kinds, and nothing else.**
  `partials/json-ld.html`: a post -> an `@graph` of `BlogPosting` +
  `Organization` + `BreadcrumbList` (each credited author a full `Person` with
  `sameAs`, plus `articleSection` from `categories:` and `inLanguage` from the
  site -- all derived, nothing per post to write), an author -> `Person`, the
  home page -> an `@graph` of `Organization` + `WebSite`. The Organization's
  `sameAs` is the four profiles foojay controls and is kept in step with
  `partials/footer.html`; the footer's Slack link is deliberately excluded,
  being a join invite rather than an identity. The WebSite's `SearchAction`
  targets the real working `?q=` route Pagefind reads, so it is a claim that
  holds. Nothing is emitted on other list/section/taxonomy pages -- they are not
  a single creative work, person or site.
  `site.Language.LanguageCode` is deprecated in Hugo 0.158+; use
  `site.Language.Locale`.

  **The Organization is ONE node, in `partials/json-ld-organization.html`.** A
  post used to inline its own `publisher` -- so foojay was two entities that
  happened to share a name, and the inline copy carried `params.logo`
  (`foojay-logo.png`, the wide light-blue-on-transparent wordmark), which is the
  one image Google cannot use: it composites a publisher logo onto white, where
  that artwork is invisible. The shared node points at the 512x512 square built
  for exactly this. It is *emitted* in the post's graph rather than only
  referenced by `@id`, because a bare `{"@id": ".../#organization"}` on a post
  page points at a node that is not in that document.

  **Breadcrumbs follow the post's FIRST category, and the last crumb has no
  `item`.** One path from the root, not the set of places the post can be
  reached from -- a post carries up to eight categories and listing them all
  would be a nonsense trail. The trail matches what the page itself shows (the
  chips above the `<h1>` link exactly there), and the current page is left
  unlinked, which is schema.org's own guidance.

- **`partials/meta-description.html` is the single definition of a page's
  description**, used by `baseof.html` for `<meta name=description>` and
  `og:description` and by `json-ld.html` for the `BlogPosting` description --
  so the three cannot disagree. It replaced a bare
  `.Params.description | default site.Params.description`, which put ONE
  boilerplate string on 470 URLs: no author bundle carries a `description:`
  (0 of 344) and a taxonomy term has no frontmatter at all, so 345 author
  profiles and 124 category pages -- 17% of the sitemap -- all told Google
  "Foojay is a place for Friends Of OpenJDK, providing free, reference materials
  and blogs for daily Java usage.", which describes none of them.

  All three derivations follow the derive/default/ask rule rather than adding a
  key to 470 files: an author's is their own `bio:` (332 of 344 have one), a
  term's is its name and `len .Pages`, and a sponsor's is its name and its
  article count from `sponsor-posts.html` -- 6 of the 7 sponsor bundles carry
  `description: ""`, so the pages with money attached were the ones describing
  themselves with the site boilerplate. A hand-written `description:` always
  wins (Azul has one). A new author, category or sponsor gets a real description
  on the build that first sees it.

  It also **length-guards every source at 200 characters**, cutting on a word
  boundary with an ellipsis. Not cosmetic: 525 of 2148 post descriptions came out
  of Yoast over 160 characters and the longest was 1231 -- a whole paragraph. The
  stored text is untouched; this only shapes the tag, and repairing the
  descriptions themselves is `cleanup/Descriptions.java`'s job.

- **`/search/` and the 404 are out of the index, and `/search/` is out of the
  sitemap.** The search page has no server-rendered content of its own to rank on
  (Pagefind fills it in client-side), so it was being listed in `sitemap.xml` and
  then told `noindex` -- two contradictory instructions about one URL. It now
  carries `sitemap: {disable: true}`. The 404 additionally has **no** canonical:
  it is not a page with a URL of its own, and it used to self-canonicalise to
  `/404.html`.

- **The home page's `<h1>` is visually hidden, and that is the only one on the
  site that is.** `index.html` opens straight into the lead card, so its first
  heading was the `<h2>` on the Podcasts band -- the most linked-to page on the
  site had no top-level heading for a search engine or a screen reader. It is
  hidden rather than drawn because the page deliberately has no hero to put one
  in, and adding a visible title would push the lead article down the fold to
  satisfy a crawler. The text is `site.Title` -- what the page IS, not a keyword
  line written for a robot, which is the thing Google actually penalises.

- **The LCP image is eager and `fetchpriority="high"`; everything else is
  lazy.** The post hero (`posts/single.html`) and the home page's lead card are
  the LCP element on 2147 posts and the home page respectively, and both were
  being lazy-loaded or left at default priority -- so the one image the score is
  measured on was scheduled behind the stylesheet, the fonts and the ten cards
  below the fold. `post-card.html` keeps `loading="lazy"`, because those really
  are below the fold.

- **`partials/post-thumb.html` is the single definition of a card's thumbnail,
  and a card ALWAYS has one.** `post-card.html` and `post-card-lead.html` call
  it unconditionally; it renders the post's image over a derived placeholder
  tile, so the three states are one box:

  1. the image loads -> the image;
  2. the post has no `image:` -> the tile (3 posts);
  3. the image FAILS to load -> the tile, because the `<img>` sits on top of it
     and carries `onerror="this.remove()"`.

  Case 3 is the one that made this worth building. **74 posts point their hero
  at a third-party host** (the author's own blog, imgur, cloudinary, an S3
  bucket since emptied) and **11 are already 404/403** -- checked, not assumed --
  so those cards rendered a grey box with a broken-image glyph. A hotlink can
  die at any time and no build step can see it, which is why the fallback is an
  inline `onerror` in the browser rather than a check in the template. The post
  hero in `posts/single.html` carries the same attribute for the same reason,
  and `search/single.html` builds the same markup in JS.

  The tile is **derived, never authored**: the label is the post's first
  category and the hue is `mod (hash.FNV32a $label) 360`, the same trick a JUG's
  dot on `/calendar/` uses -- so a category reads as one colour site-wide, a new
  category colours itself, and there is nothing to pick or store. Saturation and
  lightness are fixed in the CSS, which is what keeps 123 arbitrary hues looking
  like one family instead of a paint chart.

  Three things are load-bearing. `.post-card-image img` needs
  `position: relative; z-index: 1` -- the tile is absolutely positioned and a
  positioned element paints ABOVE a non-positioned sibling whatever the source
  order, so without it the tile covers every image on the site. It also needs
  its own opaque `background`, because a great many WordPress heroes are
  transparent PNG logos and the tile's stripes showed straight through them (the
  tile is the fallback for a missing image, not a mat to sit a present one on).
  And the search page's `hueFrom()` must stay byte-compatible with Hugo's
  `hash.FNV32a` -- FNV-1a with `Math.imul`, since a plain `*` on 32-bit values
  loses precision -- or one post gets two different colours depending on where
  you see it. Verified: "security" -> 123 and "java" -> 141 in both. The search
  side gets the label from `data-pagefind-meta="category"` on the first category
  chip, falling back to the section name ("Author", "Pedia") for the sections
  that have no category.

  **`resource-url.html`'s external test is `http://`/`https://`, NOT a bare
  `http`.** Four post bundles hold a file whose *name* starts with the scheme --
  WordPress saved a hotlinked hero as
  `https___res.cloudinary.com_..._blog-snakeyaml-pr-upgrade.jpg` -- and a bare
  `hasPrefix "http"` passed those through untouched. The trap is that the result
  is a *relative* path (no colon, so no scheme), which resolved correctly on the
  post's own page and 404'd from every listing URL one directory deeper. That is
  what the broken thumbnails on `/today/author/bmvermeer/page/2/` were, and
  nothing reported it: the file is present and Hugo publishes it.

  The 11 dead hotlinks now degrade gracefully but are still dead, and
  `transfer/Posts.java` copies the hero URL through from the live WP page -- so a
  re-scrape puts a dead one back and localising them is the only durable fix.
  The audit is `curl -o /dev/null -w '%{http_code}'` over every `image:` value
  that starts with a scheme; re-run it before cutover, the way the cross-post
  `canonical:` check is re-run.

- **`render-image.html` emits `width`/`height`, and only for rasters.** Without
  them the browser reserves no space and every image in a 2000-word post shoves
  the text below it down as it decodes -- cumulative layout shift on all 2147
  article pages. The CSS still caps the display size (`.prose img { max-width:
  100% }`) and the browser scales the reserved box by the same ratio, so the
  attributes fix the shift without fixing the size. Hugo reports an SVG as
  `ResourceType` "image" too, but `.Width` on one raises "this method is only
  available for raster images" and halts the build -- so the check is on
  `.MediaType.SubType`, not the resource type. The 76 posts on remote image URLs
  get nothing, which is correct: those cannot be measured at build time.

- **`render-link.html` resolves page-bundle resources, the same way
  `render-image.html` does.** Without it a link to a file in the author's own
  folder -- a PDF, a video, a zip -- fell through to `relURL` and resolved
  against the SITE ROOT (`/website/handout.pdf`) while Hugo published the file
  inside the bundle (`/website/today/<slug>/handout.pdf`), so the link 404'd and
  nothing said so. **143 internal links across `content/` were dead this way.**
  It went unnoticed because the asymmetry is invisible in the source:
  `![](shot.png)` resolved and `[handout](handout.pdf)` did not.

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
  heading-level sequence when `cleanup/NormalizeMarkdown.java` restyled 8053 of them.
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
  `cleanup/EnlighterToFences.java` above cleans up anything that slips through.
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
  `cleanup/CloudflareEmails.java` above cleaned up what was already in `content/`.

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
  bucketed by the post's original publish date (parsed in `transfer/Posts.java`'s
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
  `Posts.stripEmoji` runs on the scraped title, because a title isn't
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
  frontmatter by hand -- so `Frontmatter.checkTitleEmoji` applies the
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
  and no counts are stored. `transfer/Sponsors.java` reads `authors:` back out of
  the existing file and writes it through unchanged, so re-scraping never
  clobbers it; `validate/Frontmatter.java` fails the PR on a slug that matches
  no author. Note this makes our numbers legitimately differ from WordPress's
  (Redis shows 11 articles here vs 1 there) — author-based attribution is
  broader than whatever WP was doing. That's the intended semantics; if a
  sponsor should own fewer posts, narrow its `authors:` list.

  **An entry can be date-bounded, because people change employer.** Two Azul
  authors left on 2026-04-01 and their later articles, written elsewhere, kept
  appearing on Azul's page. So an entry is EITHER a bare slug or a map with
  optional `from:`/`till:`:

  ```yaml
  authors:
    - "tim-kelly"                 # still there: nothing to say
    - slug: "pratik-patel"
      till: "2026-04-01"          # the day they left
  ```

  The bare string stays the default because it is the common case (16 of Azul's
  18) -- case 2 of the derive/default/ask rule. `partials/sponsor-authors.html`
  normalises the two shapes into one and is the single definition; the post
  filter, the author list and the author COUNT all read it, where each used to
  re-read `.Params.authors` assuming a list of strings.

  Four things about it are load-bearing:
  1. **The range is HALF-OPEN, `[from, till)`** -- `till` is the first day *not*
     attributed, i.e. the day they left, which is the date a maintainer actually
     knows. That is not arbitrary: it makes the same date correct for both
     sponsors when someone moves (old employer `till: "2026-04-01"`, new one
     `from: "2026-04-01"`) with no gap and no double-attribution, which an
     inclusive `till` cannot do without an off-by-one.
  2. **A post counts when it is inside the window of AT LEAST ONE of its
     authors**, so a piece co-written by a current colleague and a departed one
     is still the sponsor's (`foojay-podcast-94` is exactly this -- Geertjan
     Wielenga plus five current Azul authors). That is why the check is per-post
     rather than a filter on the slug list.
  3. **Dates go through `time.Format` before comparison.** YAML turns an
     unquoted `2026-04-01` into a date object and a quoted one into a string, and
     comparing those two silently fails rather than erroring. Normalising to ISO
     strings also makes the comparison plain lexicographic, which is correct for
     ISO-8601 and needs no time arithmetic. There is also a fast path: with no
     dated entry the per-post loop is skipped entirely, which is 6 of 7 sponsors.
  4. **`Sponsors.java` keeps the block as RAW LINES.** It used to parse the list
     into slug strings and re-emit `  - "slug"` lines, which became destructive
     the moment an entry could be a map: the scan read `slug: "pratik-patel"` as a
     slug and then stopped at the `till:` line -- which does not start with `- `
     and so looked like the next key -- silently dropping every author after it.
     Verified byte-identical on Azul's 20-line/18-entry block. The script has no
     reason to understand the shape of a list it does not own, and a future key
     needs no change there.

  `Frontmatter.checkSponsorAuthorEntry` fails the PR on each way this goes wrong
  silently: an unknown key (a **closed** key set -- `until:` for `till:` is simply
  ignored by the template, so the departed author's new posts keep appearing and
  nothing says why), a map with no `slug` (matches nobody, dropping all their
  articles), a date `time.Format` can't read (halts the build pointing at the
  template rather than the content), and `from >= till` (an empty window: the
  author is listed with none of their articles). All five paths were exercised
  against a real bundle, not assumed.
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
  `<ul class="blocks-gallery-grid">`. `cleanup/GalleriesToShortcode.java`
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
  skipped by the template rather than rendered dead; `validate/Frontmatter.java`
  is what fails the PR on it. Don't add a `featured: true` frontmatter flag
  instead — that's two files to edit per rotation and, worse, two to remember to
  unset, which is exactly how a "featured" author silently stays featured
  forever.
- **The category index lives at `/today/category/`, next to its terms.**
  Hugo puts a taxonomy's own list page at `/categories/` while `hugo.toml`'s
  `[permalinks] categories` puts every term at `/today/category/<slug>/`, so the
  index was orphaned one level up from its own children -- built on every deploy
  and linked from nowhere, which is why the sidebar's "All categories" and the
  home page band both pointed at `/today/` (all *articles*) instead.
  `content/categories/_index.md` moves it with `url:` and keeps `/categories/`
  as an alias, since the site has already been deployed under that path.
  `_default/terms.html` renders it **alphabetically**, with the same filter box
  `/today/author/` uses: the sidebar widget and the home page band already show
  the top categories by count, so the only reason to open a page listing all 123
  is to find a specific one. Everything on it is derived from the taxonomy --
  a new category appears when a post carries it, and there is no count stored.

- **A pedia entry is: prose, then optional "More reading on Foojay:", then
  `## See Also`.** One convention across all 47, and the two blocks mean
  different things -- More reading links foojay ARTICLES, `## See Also` links
  other GLOSSARY TERMS, which is what a reader following a definition wants
  next. Headings inside an entry start at `##`, because the layout renders the
  term as the `<h1>`; ten entries were on `###` and one mixed both.

  The See Also lists are **derived, in three tiers**: the pedia links the entry
  already made (author intent, order preserved), then entries that link TO it
  (reciprocity, so a relationship stated once shows up on both ends), then its
  topical group -- only to top a short list up to three, and never across
  groups. Capped at six, because a longer list stops being navigation. Links are
  root-relative (`/pedia/<slug>/`), not the absolute `https://foojay.io/...`
  form the scraper emits, so they work under `hugo server` too.

- **A pedia image lives in `static/images/pedia/<slug>/`, never hotlinked.**
  Entries are single FILES, not page bundles, so there is no folder beside the
  markdown to co-locate a resource in — `resource-url.html` resolves the
  root-relative path instead. `latency`'s figure had been left pointing at
  azul.com, which is what a re-scrape does with a third-party image
  (`HtmlToMarkdown.localizeImages` only pulls foojay-hosted ones local), and the
  URL WordPress stores for it now 404s — on the live foojay.io page too, so that
  entry renders a broken image there today. The working copy is under
  `/wp-content/uploads/2020/11/`; it is now in
  `static/images/pedia/latency/`. Check the figure still loads if that entry is
  ever re-scraped, because the scraper will hotlink it again.

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
  there is no list to edit and no count to update. `validate/Frontmatter.java`
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
  one-line intro; the page tree, pedia, sponsors, authors and every article are
  all derived, so a new page or post appears at the next build and
  there is no list to maintain. The page tree's nesting comes from sorting on
  `.File.Path`: a lexicographic path sort is already tree order
  (`pages/board.md` before `pages/board/azul.md`), so depth is a slash count
  rather than recursion.

  **Articles and Authors are sortable, filterable TABLES; pages, pedia and
  sponsors stay link lists.** The split is whether an entry has FIELDS worth
  comparing. An article has a date, an author and a read count; an author has a
  set of links, an article count and the reads across all of them -- and 2147
  or 344 of those are worth sorting by any column. A pedia entry is a name, and
  a table of one column is a list with extra rules. The Articles section's year
  headings went with the grouping the table replaced: a table can only carry
  one order, and sorting on the date column is that grouping's order anyway.

  1. **The HTML is sorted, the JavaScript only adds the buttons.**
     `static/js/sortable-table.js` enhances any `table[data-sortable]`: it reads
     the server-rendered `aria-sort` to know where it starts, and it CREATES the
     header buttons rather than the template rendering them, so without
     JavaScript there is no dead control -- a `<th>` is plain text and the
     default order (newest first, and A-Z for authors) is the useful one. Same
     posture as `/calendar/`'s toolbar. A column opts in with
     `data-sort-type`; the authors' Links column deliberately has none, since
     there is no order to put eight links in that anyone would ask for.
  2. **A cell sorts on `data-sort-value`, never on what it shows** -- a
     timestamp behind "18 Aug 2026", the raw integer behind "68,330" (a
     thousands separator otherwise parses as 68), and the FIRST credited
     author's display name behind a byline that may list seven of them. Sorting
     on the author slug would file `frankdelporte` and `frank-delporte` apart.
  3. **Every cell reuses the site's own definition of what it shows**:
     `partials/byline.html` for a post's authors (so this page cannot credit a
     post differently from a post card), `partials/author-social.html` for an
     author's links (same pills, same RSS entry as their profile),
     `partials/author-posts.html` for the article count (so it cannot disagree
     with the heading on their own profile) and `partials/views-total.html` for
     both read counts -- over that same post set for an author, and over a
     one-page slice for a single article. Numbers are bare because the column
     heading says Views. An em dash means the counter has no number; a 0 is
     printed as 0, because an author with nothing published yet is a real state.
  4. **`static/js/table-filter.js` is the filter, and it is generic**: an
     `input[data-filter-for="<table id>"]` narrows that table's rows, ANDing
     the words typed ("frank javafx 2024"). Three things earn their keep.
     `data-filter-cols` limits which columns are searched -- without it "42"
     matches every article read 42 times, and "linkedin" matches half the
     authors. Including the articles' DATE column is what gives back the year
     lookup the old year headings did ("2021", "aug 2021"). And the haystack is
     read from the cells and cached on first keystroke rather than rendered
     into a `data-filter` attribute per row: on 2147 rows that attribute is
     ~170KB of duplicated text, and a second copy of a title is a second thing
     that can disagree with the first. The box is `hidden` in the markup and
     revealed by the script, so it is never a search field that does nothing.

  Both scripts are loaded by that one template, not from `baseof.html`: the
  other 4000 pages have no such table. The four older inline filters
  (champions, JUGs, the author grid, the category index) predate this one and
  still carry their own copy -- champions and JUGs filter table rows and could
  move onto it.

  Three CSS traps live in the styling, all silent. `.sitemap-table td` is a
  class PLUS a type selector, so it outranks a single class -- a numeric column
  needs `td.sitemap-table__num` or its `text-align: right` loses (the same
  specificity trap the `.prose` rules have). A filtered-out `<tr>` needs an
  explicit `display: none !important`, because `display: table-row` beats
  `[hidden]` from the UA stylesheet -- which is why the champions and JUGs
  tables already carry that line. And the sticky header only sticks above
  60rem: below that the table scrolls sideways, and an overflow container
  becomes the sticky positioning context, so `top` would be measured against
  the table itself and the header would never stick at all.

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
  `/calendar/`, and carries `/all-events/` as an alias. `transfer/LegacyViews.java`'s
  `PAGE_ALIASES` maps WP's `all-events` page onto the `calendar` key so it does
  not land in `unmatched`; `fetchAll` merges duplicate keys with `Math::max`, so
  the page keeps the higher count rather than summing two views of one page.
  Renaming the file back would silently move the key and drop the bigger number.
- **The calendar has two sources, and the split is "does it publish a feed?"**
  `scripts/fetch/JugEvents.java` syncs JUG meetups into `data/jug-events.json`
  daily; a conference has no feed anyone can subscribe to, so those are
  **hand-added, one file per event, by pull request**:
  `data/events/<slug>.yaml`, `name`/`url`/`start` required, `end`/`type`/
  `venue`/`city`/`country`/`online` optional. `template/event.yaml` is the
  starter file (its comments are the schema), `CONTRIBUTING.md` the
  walkthrough, and `Frontmatter.checkEvents` the PR-time check.

  Three things about it are load-bearing:
  1. **The generated file is `jug-events.json`, not `events.json`.** Hugo maps
     `data/events.json` and a `data/events/` folder onto the same
     `hugo.Data.events` key and merges them, so a generated feed and
     hand-written entries would share one namespace -- and the layout would
     have to skip `groups`/`generatedAt`/`source` by name to iterate the
     entries. Renamed instead. `index hugo.Data "jug-events"` is how the layout
     reads it (a dash can't be a field selector).
  2. **One file per event, never a shared list.** Two contributors adding two
     conferences in the same week both append to the end of a list file and
     both get a merge conflict; with a file each they never touch the same
     bytes. Same reason GlobalWWJugs has one file per JUG.
  3. **Nothing has to be deleted.** The layout drops an event the day after it
     ends, so a stale file is inert -- a calendar whose upkeep is a chore is a
     calendar that rots.

  `checkEvents` enforces a **closed key set**, which is the check that earns
  its keep: data files are not content, so Hugo says nothing about
  `website:` instead of `url:` or `dates:` instead of `start:` -- the event
  just renders with a piece missing. Everything else is derived: the dot colour
  from the filename hash, the days a multi-day conference occupies from its
  dates, the "N conferences" count (and whether that word widens to
  "conferences & workshops") from the entries themselves.

- **`partials/calendar-events.html` is the single definition of what an event
  is**, and it is what makes the home page band possible without a second
  source of truth. It flattens both sources, enriches every event with the
  fields the templates and the JavaScript need (dates formatted, place folded,
  colour hashed, `days` expanded) and returns `{events, conferences, errored,
  generatedAt}`. `/calendar/` and `partials/upcoming-events.html` both call it
  — `partialCached`, since the result depends on the data files and not on the
  page — so neither can disagree with the other about a date, a place or a
  group's colour, and a third caller gets all of it free. Same move as
  `sponsor-posts.html`, `author-posts.html`, `board-members.html` and
  `series-steps.html`.

- **The home page's "next two weeks" band is a WINDOW, not a top-N.**
  `partials/upcoming-events.html` renders the events whose date range overlaps
  the next fourteen days, as one sideways-scrolling row under the article grid.
  Two details are deliberate: the filter is an **overlap** (`end >= today` and
  `start <= today+14`), not "starts within the window", because a five-day
  conference that opened yesterday is still on this week and dropping it is a
  bug a reader would notice; and the band renders **nothing** when the window
  is empty, the way the podcast band does — a quiet fortnight is a reason to
  show no section, not an empty one. A top-N would look similar and quietly
  stretch to next spring, which is not what "what could I still go to?" means.
  It sits on a plain band with sunken cards rather than the reverse, because
  the podcast band below it is sunken and two sunken bands in a row read as one
  block with two headings.

- **`/calendar/` is two views of its events, and only one of them is
  content.** `themes/foojay/layouts/events/single.html` flattens
  `jug-events.json`'s groups into a single list, joins each group's `jug` slug
  to `data/jugs.yaml` for the country (all 32 groups match), and appends the
  `data/events/` entries with the same keys -- so downstream, the grid, the
  agenda, the dialog and the JSON island are one code path and the only thing
  that differs is `kind`. The page file holds a one-line intro and nothing else -- the event count, the group count, the
  country count and the "Groups on the calendar" legend are all derived, the
  same way `/jugs/` and `/java-champions/` are. Each JUG's dot colour is a hue
  hashed from its slug (`hash.FNV32a`), so a group keeps one colour across the
  grid, the legend and the detail dialog without a colour ever being configured.

  The **agenda list is server-rendered and the month grid is not**, which is the
  part to keep: the agenda is real HTML (so it is what Pagefind indexes and what
  a reader without JavaScript gets), while the grid is a visualisation the
  script builds from a JSON island. The whole toolbar -- month arrows, Today,
  the Month/List switch -- starts `hidden` and the script switches it on, so
  without JS the page is simply the agenda rather than a row of dead buttons,
  and a narrow screen opens on the agenda because seven columns of chips are
  unreadable on a phone. Times are formatted in the template, in each event's
  OWN UTC offset (what the JUG means by "19:00", and what Meetup shows);
  rebuilding them from the ISO string in the browser would silently restate
  every event in the reader's timezone. An event chip is a real link to the
  event on Meetup with the click intercepted for the detail dialog, so
  middle-click and copy-link still do the obvious thing.

  Where an event is happens in ONE place too: the layout folds `online`,
  `venue` and `city` into a single `place` string ("Online", else "venue,
  city", else empty), which the agenda, the dialog and the JSON island all
  read. The agenda falls back to the JUG's country only when there is no place
  at all -- a venue already names its city, so printing the country next to it
  is noise.

  Groups whose sync failed (`error` in the JSON) are named under the calendar
  rather than dropped -- an empty month should not be indistinguishable from a
  broken fetch. Note the upstream data is not always sane: a few recurring
  "Stammtisch" events carry an end time a week after their start, which is why
  the agenda renders a bare "until 4 Sep" instead of pretending to know better.

  **A multi-day event occupies every day it runs; a meetup occupies one.** The
  layout gives each event a `days` array -- one date for a meetup, five for
  Devoxx Belgium -- and the script buckets the grid on that, so a conference is
  a band of "Day 1/5"..."Day 5/5" chips tinted in its own hue rather than one
  chip on the Monday. The asymmetry is deliberate and is why `days` exists
  instead of the script reading `date`/`endDate`: those same broken Stammtisch
  entries would otherwise smear an upstream typo across seven cells.

  **The month arrows work in both views, and they mean different things.** In
  the grid they step a month, empty or not. In the list they step to the
  previous/next month **that has events** and scroll its section into view --
  the agenda has no section for an empty month, so stepping into one is a
  button that visibly does nothing, which is what they did before. An arrow at
  either end is `disabled`, not silently inert. The toolbar is `position:
  sticky` under the site header for the same reason: a 40-event agenda is
  several screens, so a toolbar that scrolled away would let you step forward
  exactly once. `Today` stays in the view the reader chose rather than forcing
  the grid -- forcing it dropped a phone, which opens on the agenda, into the
  one view that does not fit on it.

  The source/attribution note sits **full width under the calendar, not in the
  rail**: it is the only prose on the page, and at 300px it ran long enough to
  push the two legends into a scroll they did not need. Both legend lists are
  capped at `27rem` with `overflow-y: auto`, because they grow with the data --
  90 JUGs publish a calendar.
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
  correctly get no navigation. `linkTitle:` is an optional override for the
  wording a nav card shows -- `.LinkTitle` falls back to `.Title`. It was
  introduced when every step title still began "Getting Started with Java - "
  and a card should not repeat that on all eleven; the titles have since been
  shortened to the step name itself ("Choosing an Editor", "Hello World"), so
  eight of the eleven `linkTitle:` lines now merely restate `title:` and only
  three still say anything ("Using the Arguments" -> "Using Arguments and String
  Arrays", and two punctuation/case fixes). By this file's own rule a key always
  set to the same value is a chore, not configuration -- so the eight redundant
  ones can go, and a new step needs `linkTitle:` only when the nav should read
  differently from the page's H1.
  `Frontmatter.checkSeriesWeights` fails the PR on two pages in a folder
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
- **The footer navigation is `[[menu.footer]]` in `hugo.toml`, not markup.** Same
  two-level shape as `[[menu.main]]`: a top-level entry is a column heading, its
  children are the links in it, and `partials/footer.html` is a `range` over
  them. `partials/footer-link.html` renders one entry for both menus.

  It was hardcoded in the template, which made it a **second definition of the
  site's navigation with nothing tying it to the first** -- and it had drifted
  exactly the way a second definition does. Three labels disagreed with the
  header (`Meet The Team` vs `Meet the Team`, the short vs long "Where to Find"
  form), the podcast sat in the resources column while the nav files it under
  News, sponsors were a Community Hub item in the footer and an About item in
  the nav, and four pages the nav offers (Java Champions, the Sustainability
  eBook, the AI portal, Write for Foojay) were reachable from the hover panel
  and nowhere else. None of that is visible while the two live in different
  languages in different files.

  Three things stay in the template because none of them is a link an editor
  picks: the brand blurb, `now.Year`, and the **RSS URL**. That last one is the
  interesting case -- it carries `rss = true` instead of a `url`, and
  `footer-link.html` resolves it from `site.Home.OutputFormats.Get "rss"`. The
  feed path is Hugo's to decide (it follows `[outputs] home` and the format's
  baseName) and the rest of the theme already derives it that way
  (`_default/term.html`, `partials/author-social.html`), so a literal
  `"/index.xml"` in config would have been the one copy that goes stale in
  silence. Everything else about an entry -- label, order, column, `rel="me"` --
  is config.

  Two traps found while building it. **Hugo lowercases config keys**, so
  `[[menu.footerLegal]]` registers as `footerlegal` and `site.Menus.footerLegal`
  resolves to nothing -- the whole utility row rendered as an empty `<div>`, no
  error. The menu is `[[menu.legal]]` for that reason; keep footer menu names
  all-lowercase. And `relURL` **does** normalise a trailing slash on
  `"pedia"` -> `/website/pedia/`, so the slashless URLs that were in both the
  footer and the nav were a source-consistency wart and not the 301 they looked
  like. Verify a claim like that against built HTML, not against the template.

- **The old WordPress `/team/` page is gone, and it was not `/meet-the-team/`.**
  WordPress served two team pages: `/meet-the-team/` (Foojay's own people) and
  `/team/`, a profile of the web development agency that built the WP site,
  naming five staff by first name and city. The second is not Foojay's team and
  nothing here linked it -- it was built and sitemapped on every deploy,
  reachable only by typing the URL. Deleted, together with
  `static/images/pages/team/`.

  Two things had to move with it, and both fail silently. Its **aliases** --
  `/team/` *and* the legacy `/about-our-team/`, which existed nowhere else -- are
  now `aliases:` on `content/pages/meet-the-team.md`; the built page list is what
  caught the second one (4188 pages before, 4187 after, one removal that was not
  the file deleted). And its **view-counter key**: `transfer/LegacyViews.java`'s
  `PAGE_ALIASES` maps `team` -> `meet-the-team`, without which the WP item lands
  in `unmatched` and prints every run. Note `fetchAll` merges with `Math::max`,
  so meet-the-team keeps its 31201 rather than summing in `/team/`'s 3654 --
  these were two real pages, so that number is genuinely discarded; leaving the
  mapping out would discard it too and add report noise, which is why the
  mapping wins. `data/views.json`'s dead `pages/team` key clears itself on the
  next run; don't hand-edit it.

- **`/community-support/` is an ALIAS of `/our-sponsors/`, and never was a page
  here.** It is one on WordPress too -- a redirect, added to
  `content/sponsors/_index.md`'s `aliases:` by "Missing redirects currently in
  place in WordPress". So the scraper had followed that redirect and stored its
  target as a page of its own, `content/pages/community-support.md`: a
  half-captured duplicate of the sponsor listing with one sponsor (Azul),
  hardcoded article/podcast/event counts that `partials/sponsor-posts.html`
  derives correctly next door, and a "View Profile" link to the pre-rename bundle
  path. Deleted, with `static/images/pages/community-support/`.

  What it was doing in the meantime is the part worth remembering, because
  nothing reported it. Its `url:` and the section's `aliases:` entry **both
  claimed `/community-support/`** -- the same collision as
  `all-events`/`calendar`, which Hugo does not warn about -- and the alias won,
  so the stub was built into **no** page at all. It was still listed in
  `sitemap.xml` and on `/sitemap/` though, i.e. crawlers were being pointed at a
  URL that redirects, and a reader clicking it in the HTML sitemap bounced to the
  sponsor page. Deleting the file removed the entry from both.

  Nothing had to be carried over, which is worth checking rather than assuming:
  the alias that preserves the URL already lives on `content/sponsors/_index.md`,
  and there is no view key to move -- `pages/community-support` is absent from
  both `data/views.json` and `data/legacy-views.json` (WordPress records the
  redirect, not a page with reads of its own). Note there would have been
  nowhere to put one: `views-key.html`'s `$counted` is `posts pages pedia
  authors`, so the sponsor listing is not counted at all, and a `SECTION_MOVES`
  entry aimed at it would have minted a key nothing ever displays.

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
  in `Posts.scrapePost` picked up a site-title element sitting next to the
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
  default `transfer/Sponsors.java` names it after the WordPress slug, which
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
  of the term in the discussion body instead. `transfer/Comments.java` writes that
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
  with the trial prefix stripped), and `transfer/Comments.java` writes the same
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
  `scripts/fetch/ViewCounts.java` at every deploy and four times a day, and is baked into
  the HTML — no JavaScript, no dash that becomes a number after paint, and it
  works on cards, which a per-page client-side fetch cannot do well.
  Up-to-a-day-stale is not a defect in a view count.

  **`partials/views-key.html` is the single definition of what gets counted and
  under which key** — `views.html` (display), `views-beacon.html` (counting) and
  `transfer/LegacyViews.java` (the import) all follow it, and a drift between them shows
  up as a number that silently stays at zero rather than as an error. Add a
  section to the `$counted` slice there and it starts being counted; nothing
  else changes, because the Worker validates the key's *shape* rather than an
  allow-list of section names (an allow-list would mean a forgotten redeploy
  silently dropping a whole section).

  **A combined total is `partials/views-total.html`**, which sums a slice of
  pages through those same two sources -- so the "68,330 views" beside an author
  profile's article count can't disagree with the numbers on the cards below it.
  It is a sum at build time and not a stored field: it moves every time
  `fetch/ViewCounts.java` runs and every time the author publishes. It sits next
  to the article TOTAL rather than in the profile head, because the head's
  `views.html` line is the profile PAGE's own count -- two numbers there would
  read as the same thing. Returns 0 rather than nothing when no page in the set
  is counted, so a caller can `with` it and render nothing, the way `views.html`
  does on a brand new post.

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

- **Third-party analytics is GA4 + Ketch, loaded directly, and dark until
  cutover.** `partials/analytics.html` is the only place the site asks a
  reader's browser to run someone else's code, and it is called from
  `baseof.html`'s `<head>`. Configured in `[params.analytics]`: `ga` (the GA4
  measurement id) and `[params.analytics.ketch]` `org`/`property` for the
  consent manager. Each half renders only when its own values are set, so a
  local build, a fork or a contributor's `hugo server` never reports a pageview
  into foojay's property and never shows a consent banner.

  **The GA4 measurement id is invisible from the outside, and a grep will tell
  you the site has no analytics.** It doesn't. foojay.io loads exactly one
  thing — GTM container `GTM-M6ZT5NW` — carrying two Universal Analytics
  pageview tags on `UA-726113-5` and a Custom HTML tag injecting the Ketch
  snippet for org `azul`, property `foojay_io`. No `G-` id appears in the page
  HTML or in the container. It is resolved **server side**: the UA tag makes
  Google fetch `googletagmanager.com/gtag/destination?id=UA-726113-5`, which
  returns a `__zone` tag whose child container is `G-GS21L12HYK` with
  `inheritParentConfig` — Google's UA-to-GA4 *connected site tag* migration,
  which keeps the retired UA id working as an alias for the GA4 property. So
  the stats are current even though the only visible tag is a 2023-vintage
  Universal Analytics one. **Resolve the UA id against googletagmanager.com
  before concluding anything about what a Google tag reports into.**

  `[params.analytics] ga` therefore names `G-GS21L12HYK` and loads it directly,
  which drops that indirection: one fewer legacy shim Google can retire
  unilaterally, and the property is named in the repo instead of hidden behind
  a dead id. The container also still carries OneTrust trigger conditions from
  a consent manager the site no longer uses, and its second UA tag sets UA
  custom dimension 1 from the `?internal=` query parameter — which a connected
  tag does not turn into a GA4 parameter, so it already reaches nothing today
  and is deliberately not reproduced. Pointing at the container would load
  Ketch **twice**,
  once from its Custom HTML tag and once from here, while firing the dead UA
  tags and shipping 360 KB of JavaScript to deliver eleven lines. The deeper
  reason is reviewability: a tag manager moves half the site's third-party
  behaviour into a web console where it is invisible to this repo and to a PR.
  A tag manager, if ever genuinely wanted, replaces this partial rather than
  joining it.

  Three behaviours are load-bearing:
  1. **Nothing renders on the trial deploy**, derived from the same
     `baseURL != params.productionBaseURL` test `baseof.html` uses for
     `noindex` — so it switches itself on at cutover with nothing to unset. The
     trial is a byte-for-byte copy of the live content, so counting it would
     inflate every number in the property with a second site's traffic, and a
     consent banner on a throwaway host stores a reader's choice against the
     wrong domain. **Don't turn this into a flag.** To see the markup before
     cutover, build for the real URL:
     `hugo server --baseURL https://foojay.io/ --appendPort=false`.
  2. **Google Consent Mode defaults are emitted only when Ketch is**, because
     they are correct exactly when something will send the update: with a
     consent manager, default-deny is the only defensible posture; without one,
     nothing could ever grant consent and GA would be reduced to permanent
     modelling. So it follows Ketch rather than being a third knob. If GA
     reports every session as consent-denied after cutover, check Ketch's own
     Google Consent Mode plugin on the property — the defaults here are working
     and nothing is updating them.
  3. **The trial's suppression notice goes through `printf | safeHTML`.** Go's
     `html/template` strips HTML comments out of a template, so a literal
     `<!-- ... -->` renders nothing at all — indistinguishable from the partial
     never having been wired up, i.e. exactly the confusion the notice exists
     to prevent. Same trap applies anywhere else a comment is meant to reach
     the reader's view-source.

  **GA4 and `data/views.json` will never agree, and GA4 is always the lower
  one. That is not a bug in either.** `google-analytics.com` and
  `region1.analytics.google.com` are on Firefox's Enhanced Tracking Protection
  blocklist, which is **strict by default in private windows** — the requests
  are dropped at the network layer before any tag, consent or Ketch logic runs.
  Observed live, not assumed: a `/g/collect` hit that was invisible in a
  Firefox private window appeared the moment tracking protection was lowered.
  Every adblocker does the same, and this audience is Java developers, so the
  shortfall is large and systematic rather than noise.

  This is the same argument the read counter above is built on, now with a
  measurement behind it: the counter is first-party on foojay.io and
  indistinguishable from the rest of the site, so nothing blocks it, while GA4
  is a third-party domain a mainstream browser rejects on its default settings.
  The two are counting different populations. **Don't "reconcile" them, don't
  swap the view counts for GA4 numbers because GA4 looks more authoritative,
  and don't treat a growing gap as drift** — a published number on a post is
  exactly the place a silent tens-of-percent undercount cannot be tolerated,
  which is why that number comes from the Worker and not from here.

  Also worth knowing when debugging what fires and when: a consent test run in
  a Firefox private window measures ETP, not consent. Use a normal window with
  a fresh profile, or another browser, or the absence of a hit will be read as
  a consent manager doing its job.

  Two things on the live site are **not** carried over and were not asked for:
  Hotjar (`hjid` 2547610) and Reo.dev (`b38cec169d83063`), both loaded straight
  from the WordPress `<head>` rather than through GTM. Decide on them
  separately; neither is wired up here.
- Posts are contributed via PR (see `CONTRIBUTING.md`); the repo is public.
- **`data/jugs.yaml` is generated, not authored here** — it's overwritten by
  `scripts/fetch/Jugs.java` at every deploy and every external-content sync. Never add/edit a
  JUG entry directly in this repo; changes belong upstream in
  [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs).
