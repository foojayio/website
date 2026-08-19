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
| `JavaChampions.java` | `data/java-champions.yaml` | [aalmiray/java-champions](https://github.com/aalmiray/java-champions) |
| `JugEvents.java` | `data/jug-events.json` | the iCal feed each JUG publishes (its own site, Google Calendar, Meetup) |
| `ViewCounts.java` | `data/views.json` | our own read counter (`worker/views/`) |
| `DiscoverJugCalendars.java` | nothing — it reports | JUG websites; finds calendars missing from GlobalWWJugs, to be fixed **upstream** |

`DiscoverJugCalendars.java` is run **by hand, never in CI** — it exists to
produce an upstream pull request, not to change anything here.

## `transfer/` — WordPress → Hugo (dies at cutover)

Everything here reads the live foojay.io WordPress site over its public HTML and
REST routes; no admin, database or credential is assumed. Delete the folder once
the WordPress site is switched off.

| script | does | run |
| --- | --- | --- |
| `Posts.java` | `/today/` posts → `content/posts/` | repeatedly, until cutover |
| `Authors.java` | `/today/author/` → `content/authors/` | repeatedly, until cutover |
| `Sponsors.java` | `/our-sponsors/` → `content/sponsors/` | by hand |
| `Comments.java` | legacy WP comments → GitHub Discussions (giscus) | by hand, needs `GITHUB_TOKEN` — **not yet run** |
| `LegacyViews.java` | WP view counts → `data/legacy-views.json`, `--seed` loads the counter | by hand, repeatedly, until cutover |

The scrapers are **idempotent**: they update a bundle rather than duplicating
it, look it up by slug so it stays put across re-runs, and skip any file whose
frontmatter is hand-marked `frozen: true`. `--url <page>` converts a single page,
for tuning selectors against real markup.

`Comments.java` and `Sponsors.java` are deliberately run by hand rather than in
CI: one writes irreversible public content to a third-party API, the other
scrapes a site that goes away — neither belongs next to the `fetch/` scripts.

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
| `HeadingAnchors.java` | WordPress's positional `{#h2-N-slug}` heading and link ids |
| `NormalizeMarkdown.java` | setext headings → ATX, decorative `<br>` spacers dropped |
| `Descriptions.java` | the spaces Yoast dropped building a `description:` from the body (`…pattern.What you'll learn`) — 22 posts; prints the 7 it can't tell apart from a type name |
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
