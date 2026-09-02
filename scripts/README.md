# `scripts/`

[JBang](https://www.jbang.dev/) scripts — run them from the **repo root**, since
they resolve `content/` and `data/` relative to the working directory:

```bash
jbang scripts/fetch/Jugs.java
```

They are grouped by **lifetime and job**, not by what they happen to be called.
The question a folder answers is *"does this still exist after cutover?"*:

| folder | what it does | after cutover |
| --- | --- | --- |
| `fetch/` | pulls data from community-run upstreams into `data/*` | **stays** — runs in CI |
| `transfer/` | pulls content out of the live WordPress site | **goes** — WordPress is gone |
| `cleanup/` | one-off rewrites of what is already in `content/` | **goes** — nothing left to repair |
| `validate/` | PR-time content checks | **stays** — runs in CI |
| `shared/` | common code, never run on its own | stays as long as its callers do |

## `fetch/` — external data (ongoing)

Run at every deploy and on a cron (`build-deploy.yml`,
`sync-external-content.yml`, `sync-view-counts.yml`). The `data/*` files they
write are **generated — never hand-edit them**; fix the entry upstream.

| script | writes | source |
| --- | --- | --- |
| `Jugs.java` | `data/jugs.yaml` | [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs) |
| `JavaChampions.java` | `data/java-champions.yaml`, `data/geocode-cache.yaml` | [aalmiray/java-champions](https://github.com/aalmiray/java-champions), plus [geocode.maps.co](https://geocode.maps.co) for the map coordinates |
| `JugEvents.java` | `data/jug-events.json` | the iCal feed each JUG publishes (its own site, Google Calendar, Meetup) |
| `ViewCounts.java` | `data/views.json` | our own read counter (`worker/views/`) |
| `JvmWeekly.java` | `data/jvm-weekly.yaml` | the public RSS feed of [JVM Weekly](https://www.jvm-weekly.com/), for the monthly Foojay roundup shown at `/jvm-weekly/` |
| `DiscoverJugCalendars.java` | nothing — it reports | JUG websites; finds calendars missing from GlobalWWJugs, to be fixed **upstream** |
| `PodcastTranscripts.java` | `transcript.md` in each podcast episode's bundle | the automatic captions on foojay's own YouTube channel, via `yt-dlp` |

`JvmWeekly.java` keeps only the editions that are the monthly Foojay roundup —
24 of the archive's 197. It reads the archive listing (titles, ~5 requests) plus
the RSS feed (bodies, but only for the newest 20), and fetches an individual
edition's body **only when it is both a candidate and not already known**:
`data/jvm-weekly.yaml` doubles as the cache, so a warm run makes a handful of
requests and a cold rebuild about 24, paced — that endpoint rate-limits.

Which edition is a roundup has no upstream marker today (Artur has a Substack
section for it but has never filed a post under it), so it is derived in two
steps: the title looks like one (`"<Article>" with <Foojay author>`, or `Best of
Foojay.io <Month> Edition`) **and** the body links at least one Foojay article.
Neither is sufficient alone — see the script header for the rules that were
tried and rejected, and for the archive paging bug that made 27 editions look
like a seven-month pause in the newsletter. `--dry-run` prints the YAML, `--all`
reports what was skipped and dropped, `--refetch` ignores the cache.

`DiscoverJugCalendars.java` is run **by hand, never in CI** — it exists to
produce an upstream pull request, not to change anything here.

`PodcastTranscripts.java` is run **by hand** too, and writes into `content/`
rather than `data/`: it needs the `yt-dlp` binary (`brew install yt-dlp`), it
talks to YouTube once per episode, and the natural moment to run it is the pull
request that publishes a new episode — `--slug foojay-podcast-101`. It never
replaces a transcript that is already there, so a corrected one survives every
later run; `--force` is the way to overwrite deliberately. Raw caption files are
cached in `.cache/podcast-captions/` (gitignored) so the conversion can be
re-run over the whole archive without re-fetching. `--report-variants` prints
what speech recognition actually made of the show's vocabulary, which is where
the substitution list in the script came from — and where the next one should
come from.

`JavaChampions.java` is the only one here that needs a credential:
**`GEOCODE_API_KEY`** (a free key from [geocode.maps.co](https://geocode.maps.co),
a repository secret in CI, an env var locally) for the coordinates behind the
world map on `/java-champions/`. It is only consulted for a place that isn't
already in `data/geocode-cache.yaml` — that cache is keyed by
`"<city>, <country>"` rather than by champion, so 422 champions are 252 places
and a normal run looks up **none** of them. Missing key, dead geocoder or an
exhausted quota never fails the run; the newest champions just aren't on the map
yet. `--no-geocode` skips the lookups, `--geocode-limit N` caps them.

## `transfer/` — WordPress → Hugo (dies at cutover)

Everything here reads the live foojay.io WordPress site over its public HTML and
REST routes; no admin, database or credential is assumed. Delete the folder once
the WordPress site is switched off.

| script | does | run |
| --- | --- | --- |
| `Posts.java` | `/today/` posts → `content/posts/` | repeatedly, until cutover |
| `Authors.java` | `/today/author/` → `content/authors/` | repeatedly, until cutover |
| `Sponsors.java` | `/our-sponsors/` → `content/sponsors/` | by hand |
| `Comments.java` | legacy WP comments → a `comments.json` per post bundle | repeatedly, until cutover — no credential |
| `LegacyViews.java` | WP view counts → `data/legacy-views.json`, `--seed` loads the counter | by hand, repeatedly, until cutover |

The scrapers are **idempotent**: they update a bundle rather than duplicating
it, look it up by slug so it stays put across re-runs, and skip any file whose
frontmatter is hand-marked `frozen: true`. `--url <page>` converts a single page,
for tuning selectors against real markup.

`Sponsors.java` is deliberately run by hand rather than in CI: it scrapes a site
that goes away at cutover, so it does not belong next to the `fetch/` scripts.

`Comments.java` **used to post the 580 legacy comments into the GitHub
Discussions giscus reads, and no longer does — GitHub banned the account it
posted as** after only a few posts had been handled. Several hundred API-driven
comment creations from a fresh account is indistinguishable from spam at
GitHub's end, and no variant of that approach avoids looking like the thing that
got blocked. It now writes an archive into the repo instead: one `comments.json`
per post bundle, rendered under the giscus widget by
`partials/legacy-comments.html` as "Discussions on the previous Foojay site".
That needs no token, touches nothing outside this repository, and is a diff
rather than an irreversible public write. giscus still owns all *new* comments.

## `cleanup/` — one-off content migrations (already run)

Each of these rewrote `content/` once and is **idempotent**, so a re-run is a
no-op. They are kept because the WordPress site keeps serving the old markup
until cutover, so a late re-scrape can reintroduce what they repaired. All of
them take `--dry-run` (report, change nothing) and most take `--path <dir>`.

| script | repaired |
| --- | --- |
| `EnlighterToFences.java` | EnlighterJS `<pre>` markup → Markdown fences, plus WP's double-escaped entities and non-breaking indent spaces |
| `GalleriesToShortcode.java` | WordPress gallery blocks → `{{< gallery >}}` |
| `CloudflareEmails.java` | email addresses Cloudflare had obfuscated away from the scrapers (re-fetches the live page) |
| `HeadingAnchors.java` | every WordPress `{#id}` Flexmark carried over — the positional `{#h2-N-slug}` on a heading, the `{#31db}` on a Medium import's links, and the 1268 on captions, read-more breaks and whole paragraphs that Goldmark never consumes and the reader therefore SEES (91 posts). Reports, rather than strips, anything mid-line |
| `HeaderlessTables.java` | the empty header row that makes a WordPress table a GFM table again — 111 tables across 54 posts had a delimiter row with no header above it, which Goldmark renders as a wall of literal pipes. Empty rather than promoting the first row: 86 of the 111 open with a legend or a note box, not a header |
| `NormalizeMarkdown.java` | setext headings → ATX, decorative `<br>` spacers dropped |
| `images.py` | the WordPress-era media weight — animated GIF → animated WebP, large PNG → JPEG, oversized rasters resized, animated `image:` heroes given a still poster. Took the built site from 1.39 GB to 0.69 GB, under GitHub Pages' 1 GB artifact limit. **The one script here that outlives cutover** (content keeps arriving) and the one that isn't jbang Java — writing an *animated* WebP needs Pillow, which Java has no equivalent for |
| `Descriptions.java` | two Yoast defects in `description:` — the spaces it dropped building one from the body (`…pattern.What you'll learn`, 22 posts), and the `" - by <Author>"` tail it stamps on an auto-generated one (290 posts, removed only when the name is one the post actually credits). Prints what it can't tell apart from a type name, or from prose |
| `SanitizeSlugs.java` | slugs → lowercase `[a-z0-9_-]` |
| `PostsToBundles.java` | flat post files → leaf bundles |
| `AuthorsToBundles.java` | flat author files → leaf bundles |

The converter now emits the corrected shape directly (mostly in
`shared/HtmlToMarkdown.java`), which is what makes a re-scrape a no-op — change
one and change the other.

## `validate/` — PR-time checks

`Frontmatter.java` is run by `.github/workflows/pr-check.yml` in lieu of a
per-PR preview URL. It covers `content/` **and** `draft/`, which nothing else in
the PR check can see. Add a rule here whenever a mistake would otherwise fail
*silently* — an author slug with no bundle, two series pages claiming one
`weight`, an unknown key in a `data/events/` file.

`BuiltSite.java` checks the site Hugo actually **produced**, so it runs after a
build — in `pr-check.yml` and again in `build-deploy.yml`, there between the
build and the deploy, so a broken build stops before it replaces the live site.
Locally:

```bash
hugo --gc --minify && jbang scripts/validate/BuiltSite.java
```

Two checks, both derived — there is no list of URLs to keep in step with the
content:

1. **every source page produced a built page.** `content/` is the expectation
   and `public/` is the answer, via the permalinks in `hugo.toml`. This is what
   catches a whole *section* going missing, the failure mode of the branch-bundle
   conversion where the templates rendered fine and simply matched nothing.
2. **every internal link resolves** — `href`, `src`, `srcset`, `poster`, and the
   meta-refresh in all 596 alias pages, so every legacy URL is verified to still
   land somewhere real. It reads files rather than making HTTP requests, so it
   needs no server, takes ~5s over half a million links, and cannot be flaky.
   External links are not checked at all: a third-party host being down is not a
   reason to block a deploy of our own site.

**Only one kind of dead link blocks.** A link the *templates* emit — nav,
pagination, stylesheet, thumbnail, alias target — is broken on every page for
every reader and is a bug in this repo, so it fails the run. A link an author
typed inside their own article is a fact about 2000 imported WordPress posts:
there are 53 today (`_wp_link_placeholder`, `/wp-admin/post.php`, bare domains
written without `https://`), none introduced by the build, and blocking every
future deploy on a 2021 typo is how a gate gets switched off within the week.
Those are reported with their count instead, the way `fetch/DiscoverJugCalendars.java`
reports its near-misses. The boundary is `.prose`, which is exactly where
`.Content` is rendered and nowhere else. `--strict` fails on those too, which is
the way to drive a cleanup pass to zero.

## `shared/` — common code

`HtmlToMarkdown.java` is pulled in with `//SOURCES ../shared/HtmlToMarkdown.java`
and is never run on its own. It is the single definition of WordPress HTML →
Markdown: code fences, galleries, Cloudflare email decoding, entity repair,
image localization, widget preservation. The `transfer/` scrapers and the
`cleanup/` migrations both call it, which is what keeps them agreeing.

## Adding a script

Put it in the folder that answers the lifetime question above, and name it for
**what it produces**, not for the verb — the folder already supplies the verb
(`fetch/Jugs.java`, not `fetch/FetchJugs.java`). If it needs `HtmlToMarkdown`,
add the `//SOURCES ../shared/HtmlToMarkdown.java` line rather than copying logic
across.
