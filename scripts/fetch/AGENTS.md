# `scripts/fetch/` — data fetchers and generated data

Read the root `AGENTS.md` first; this file holds the detail for this folder.

### Data fetchers (`scripts/fetch/`)

All run daily from `sync-external-content.yml`, commit their result, then
**dispatch** a deploy (see Workflows in the root `AGENTS.md`).

- **`Jugs.java`** regenerates `data/jugs.yaml` from the community
  [World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs).
  JUG leads fix their own entry upstream. Derives `meetup_slug`/`meetup_url`
  when a JUG's `website` is a meetup.com URL.

- **`JavaChampions.java`** regenerates `data/java-champions.yaml` from
  [aalmiray/java-champions](https://github.com/aalmiray/java-champions), and
  resolves the coordinates behind the `/java-champions/` map from three sources
  in order: an upstream `location: {lat, lon}`; `data/geocode-cache.yaml`; then
  [geocode.maps.co](https://geocode.maps.co) on a cache miss, which needs the
  `GEOCODE_API_KEY` *repository* secret.

  **The cache is keyed by PLACE STRING, not by champion** — 422 champions live
  in 252 places, so renaming one costs nothing. It is committed because it is
  the only copy. The query is byte-identical to upstream's own
  `onetimeAddLocations.java`, so nobody visibly moves when source 1 takes over.

  Four behaviours are load-bearing:
  - **It never fails over geocoding.** No key, dead geocoder, exhausted quota:
    the run still writes every champion, just without new coordinates. A hard
    failure here would block a deploy over a map.
  - **A definitive miss is cached; a transient failure is not.** An empty
    result means the geocoder knows nowhere by that name, so `found: false` is
    recorded. An HTTP or timeout failure is retried next run.
  - **401/403/429 abort the whole pass**, rather than making 251 more requests
    to discover the key is wrong. A 5xx or timeout does not — those count
    toward a *consecutive* failure limit (5).
  - **`0,0` is rejected.** It is in the Atlantic, and is what a geocoder
    returns when it parsed something it did not understand.

  `avatarUrl` upgrades an absolute `http://` to https and does not prefix an
  already-absolute URL with `AVATAR_BASE`. The 2 champions off the map are
  malformed source strings, **reported every run** rather than dropped, and
  need fixing upstream — there is deliberately no country-level fallback, which
  would hide the typo. Flags: `--no-geocode`, `--geocode-limit N`,
  `--geocode-key`.

- **`JavaConferences.java`** regenerates `data/java-conferences.yaml` from
  [javaconferences.org](https://javaconferences.org/), the site built from
  [javaconferences/javaconferences.github.io](https://github.com/javaconferences/javaconferences.github.io).
  It reads the site's published `conferences.json`, not the README it is edited
  in: same list, already geocoded, so **no geocoder and no key**. Fix an entry
  with a PR upstream.

  Three behaviours are load-bearing:
  - **Dates are free text** (`31 January-1 February 2026`) and parsed here to
    ISO. One that does not parse is **reported and skipped, never guessed**.
  - **Past conferences stay in the file.** Filtering here would change it daily
    and deploy on a date; `/java-conferences/` and the calendar drop an event
    once it has ended.
  - **It feeds the calendar without writing `data/events/` files.**
    `partials/calendar-events.html` adds these as a second source, and a
    hand-written event file **wins**: a generated entry is dropped when one
    starts within three days and shares a website host or a city. Generated
    files in that folder would go stale, mix with hand-curated ones, and need
    deleting.

  Same keep-the-committed-file guards as `Jugs.java` (unreachable source, or
  fewer than half the committed entries; `--allow-shrink`).

- **`JugEvents.java`** writes `data/jug-events.json`. **Needs no credential and
  is not Meetup-specific**: it speaks iCal to whatever a JUG publishes —
  `calendar:` from `data/jugs.yaml` first, else `meetup_slug` →
  `meetup.com/<slug>/events/ical/`. Meetup's iCal carries no `LOCATION` at all,
  so a third best-effort pass reads the schema.org JSON-LD on the event page
  for a venue. Times keep the feed's real IANA `TZID`, so a monthly event stays
  correct across a DST change.

  Four behaviours worth keeping:
  - **A 404 is "not found", not a fetch failure** — the upstream URL is wrong
    and a JUG lead can fix it.
  - **Two JUGs pointing at one feed** is an upstream mistake; the second is
    skipped and reported, or the same events appear twice under two names.
  - **A meetup.com `calendar:` URL is normalised** to `/events/ical/`. That is
    the one platform whose URL shape we know; nothing else is rewritten.
  - **Past events are filtered out here**, not in the template — one JUG's
    Google Calendar holds 170 events back to 2014.

  It **only rewrites the file when the events changed**: `generatedAt` moves
  every run, so writing unconditionally would commit, and deploy, on a
  timestamp. Requests identify themselves as foojay.io and go one at a time.
  Meetup's `robots.txt` permits the iCal route and disallows `/_next/data/*` —
  **don't** reach for the `__NEXT_DATA__` Apollo state instead, which is
  internal front-end state that changes with any deploy of theirs. Flags:
  `--dry-run`, `--limit N`, `--jug <slug>`, `--no-venues`.

- **`JvmWeekly.java`** writes `data/jvm-weekly.yaml`: every edition of Artur
  Skowronski's JVM Weekly that is a Foojay roundup, rendered at `/jvm-weekly/`.
  Substack is the source; LinkedIn is a republication with no feed.

  **Two sources, because the feed is a window.** The RSS feed carries bodies
  for the 20 most recent editions; the archive listing holds 197 back to 2022,
  24 of them roundups. The listing supplies metadata, the feed supplies bodies.
  `data/jvm-weekly.yaml` **is the cache** — an edition already in it is never
  re-fetched, because the per-post endpoint 429s under load. Delete an entry to
  re-read it.

  **Archive paging: advance by what the page RETURNED, never by the requested
  limit.** Substack answers `offset=0` with 23 rows for `limit=50`, then 50
  after that, so stepping by 50 silently swallowed 27 consecutive editions —
  which looked exactly like a seven-month pause in the newsletter, and was
  written into this file as one. Don't infer a gap in a publication from a gap
  in what a paged API handed back.

  **Identifying a roundup**: the first edition of each month. Artur has a
  Substack section for exactly this (id `194419`) and has never filed a post
  under it; the script queries it every run and takes it as authoritative when
  non-empty, so the heuristic retires itself the moment he ticks it. **Asking
  him for that one dropdown is the highest-value follow-up here.** Until then:
  a title-shaped candidate (`"<Article>" with <Author>`, or `Best of Foojay.io
  <Month> Edition`), **confirmed** by the body linking at least one Foojay
  article. Neither is sufficient alone.

  The main article is resolved in two tiers: the edition title scored against
  every post at 80%, then the named author's posts scored **in both
  directions** at 60%, because Artur often trims our headline. Nine of the 24
  get no main article, which is correct for all nine. Rules tried and
  **rejected**, so nobody re-derives them: "the description mentions Foojay",
  "it links at least N Foojay articles" as an *inclusion* rule, and "the first
  Foojay link is the lead".

  **What is stored is a REFERENCE, not a republication** — title, date, link,
  Artur's own subtitle, and the post *slugs*. Everything else is derived from
  our copy at build time; the feed is his copyright. Posts are keyed by bundle
  folder name, with linked URLs resolved through `aliases:`. Flags:
  `--dry-run`, `--all`, `--refetch`, `--body-limit N`.

- **`ViewCounts.java`** reads `/api/views/all` into `data/views.json`, four
  times a day from `sync-view-counts.yml`. **Never fails the build** — if the
  counter is unreachable it keeps the committed file and exits 0.

  **It also keeps the committed file when the counter returns FEWER pages than
  that file already has**, which is the same failure wearing a 200, and is the
  state a freshly deployed Worker is in. Nothing ever deletes a row, so the key
  count only grows; a drop means unseeded, half-restored or wrongly-bound,
  never news about the site.

### Run by hand, never in CI

- **`fetch/DiscoverJugCalendars.java`** reports JUGs whose own site advertises a
  calendar their GlobalWWJugs entry doesn't record. The answer belongs
  **upstream** — `data/jugs.yaml` is generated, so a local edit would be wiped
  by the next run. It prints the frontmatter lines to add (`--yaml`).

  **The verification is the point.** A JUG's site links to sibling JUGs, so
  "the page mentions meetup.com" is not evidence: a candidate is confident only
  when its iCal feed actually loads **and** the group's name shares a
  significant word with the directory entry (stopwords dropped, camel case
  split). Everything else is printed as "needs a human", never as a suggestion.

- **`fetch/PodcastTranscripts.java`** writes `transcript.md` into a podcast
  episode's bundle. Needs the `yt-dlp` binary and talks to YouTube once per
  episode, and a new episode is a PR somebody is already opening.

  **The captions already exist, so nothing is transcribed here** — YouTube has
  run recognition over every episode, so the text is foojay's own content and a
  fetch takes seconds against hours of local compute. **It is a MACHINE
  transcript and every page says so**; automatic captions do not satisfy WCAG
  1.2.2, but a page that can be read and searched beats audio-only. **An
  existing `transcript.md` is never overwritten** (`--force` to replace), so a
  corrected one survives the next run.

  Five things are load-bearing:
  - **The rendering is DERIVED from the file.** `transcript.md` in a leaf
    bundle is a resource of type `page`, so `posts/single.html` renders the
    section when the file is there. No frontmatter flag to set or forget.
  - **It is an ordinary `<h2>`, not a collapsed `<details>`**, because
    find-in-page does not look inside a closed one — Ctrl-F for a guest's name
    found nothing on the one page that says it. Its TOC entry is **appended by
    hand** in `partials/toc.html`: the heading comes from a bundle resource, so
    `.TableOfContents` cannot see it. `id="transcript"` is hand-written too.
  - **YouTube's caption format repeats itself** — rolling captions re-send the
    settled line, so 2643 cues hold ~1300 distinct lines. Dropping a line
    identical to its predecessor is what collapses that.
  - **The substitution list is EVIDENCE, not guesswork** (`--report-variants`
    built it). The surprise: `forj` is **`4j`**, not Foojay — it appears only
    next to Neo, Log, SLF and LangChain, so the spelling-based guess would have
    rewritten every Log4j mention into a mention of the site. **Names are
    deliberately not corrected**: there is no spelling a script can know is the
    intended one, and inventing one puts words in a guest's mouth.
  - **The readability pass is conservative.** `[music]`/`[singing]` go,
    `[laughter]`/`[applause]` stay; `uh`/`um` go; `like`, `you know` and
    `I mean` are left alone — they carry meaning often enough that stripping
    them rewrites what someone said. Every category is counted and printed.

  Transcripts carry **`data-pagefind-ignore`**: together they run to several
  times the site's ~115k-word article index, so indexing them would make every
  episode a hit for any word anyone said out loud.

### Generated data files

Never hand-edit any of these.

- **`data/jugs.yaml`** — `fetch/Jugs.java`, rendered at `/jugs/`. Fix a JUG
  upstream in GlobalWWJugs.
- **`data/java-champions.yaml`** — `fetch/JavaChampions.java`, rendered at
  `/java-champions/`. Fix an entry upstream.
- **`data/geocode-cache.yaml`** — `fetch/JavaChampions.java`. Place string →
  coordinates, or `found: false`. **Do** delete an entry, or the whole file, to
  force a fresh lookup; a full rebuild is ~250 requests inside the free tier.
- **`data/java-conferences.yaml`** — `fetch/JavaConferences.java`, rendered at
  `/java-conferences/` and read by the calendar. Fix an entry upstream.
- **`data/jug-events.json`** — `fetch/JugEvents.java`. Read as
  `index hugo.Data "jug-events"`; a dash cannot be a field selector.
- **`data/jvm-weekly.yaml`** — `fetch/JvmWeekly.java`, read the same way.
- **`data/views.json`** — `fetch/ViewCounts.java`. `slug -> total reads`, baked
  into the HTML.
- **`data/legacy-views.json`** — each post's WordPress view count at the final
  import. Nothing reads or writes it now; the counter holds these numbers in
  its `legacy` column. Committed because it is the **only** copy of a number
  with no other source left.
