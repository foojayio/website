# `scripts/`

[JBang](https://www.jbang.dev/) scripts — run them from the **repo root**, since
they resolve `content/` and `data/` relative to the working directory:

```bash
jbang scripts/fetch/Jugs.java
```

They are grouped by **lifetime and job**, not by what they happen to be called:

| folder | what it does | when it runs |
| --- | --- | --- |
| `fetch/` | pulls data from community-run upstreams into `data/*` | CI, and by hand |
| `validate/` | PR-time content checks | CI |

**`transfer/`, `cleanup/` and `shared/` are gone**, deleted at cutover on the
Phase 4 list in `CUTOVER.md`. `transfer/` scraped the live WordPress site
(posts, authors, sponsors, comments, view counts), `cleanup/` held the one-off
repairs of what those scrapers produced, and `shared/HtmlToMarkdown.java` was
the WordPress HTML → Markdown converter both of them called. All three answered
a question the site no longer has. The git history keeps them if you ever need
to read one.

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

## Adding a script

Put it in the folder that answers the lifetime question above, and name it for
**what it produces**, not for the verb — the folder already supplies the verb
(`fetch/Jugs.java`, not `fetch/FetchJugs.java`). Code shared by two scripts goes
in a new `shared/`, pulled in with jbang's `//SOURCES`, rather than copied
across.
