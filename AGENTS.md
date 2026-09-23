# Project context for Claude and other LLM coding tools

foojay.io is a static Hugo site, built from this repo and deployed to GitHub
Pages behind Cloudflare. It replaced the WordPress site at **cutover on
2026-09-22**. If you're picking this up fresh, read this before making changes.

`CUTOVER.md` is the runbook that was followed on the day. It is history now,
except for its "Redirect rules" section, which is live Cloudflare config.

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

## Hard requirement: don't overload comments

A comment earns its place by saying what the code cannot. Keep new ones short:

- **Say WHY, not what** — the code already says what it does.
- **Three or four lines is the ceiling**, and one is usually enough. Only a
  genuinely subtle trap earns more.
- **Say it once.** If something is already explained elsewhere, point at it
  rather than repeating it.
- **No narrating the diff**, and no history of what the code used to do.

This applies to every language here: Go templates, CSS, JS, Java, frontmatter.
Plenty of existing comments are longer than this allows — shorten them when you
touch them, and don't take them as the model for new ones.

## What exists

- **Hugo skeleton**: `hugo.toml`, `themes/foojay/` (layouts +
  `static/css/style.css`), and `template/` (starter files for article, page,
  author, board member, ad and event, plus the category list — see
  `template/README.md`). There is deliberately **no `archetypes/`**: nothing
  runs `hugo new`, and two sets of starter files drifted — the post archetype
  wrote a singular `author:` against author *files* where posts take an
  `authors:` list of author *folders*. Add starter files to `template/`.

- **`scripts/` is grouped by lifetime, not by verb** — `fetch/` (external data,
  runs in CI) and `validate/` (PR-time checks). A script is named for **what it
  produces**, the folder supplying the verb (`fetch/Jugs.java`, not
  `FetchJugs.java`). All run from the repo root — they resolve `content/` and
  `data/` against the working directory. `scripts/README.md` is the index.

  `transfer/`, `cleanup/` and the `shared/` converter they both called were
  deleted at cutover. Where a convention below names `HtmlToMarkdown`,
  `Posts.java` or `cleanup/images.py`, that is one of them: they are named
  because they explain the shape of what is in `content/`, not because there is
  a file to open. Read one out of git history if you need it.

- **`content/` came out of a scraper, not a database export.** The deleted
  `transfer/` scripts read the live foojay.io site (no WP admin or DB access)
  and wrote the Markdown in the repo today. When something in `content/` looks
  odd, the answer is usually "this is what the WordPress page rendered".

### Data fetchers (`scripts/fetch/`)

All run daily from `sync-external-content.yml`, commit their result, then
**dispatch** a deploy (see workflows below).

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

### The read counter

- **`worker/views/`** is a Cloudflare Worker over a D1 table of
  `<section>/<slug> -> (legacy, live)`, routed at `foojay.io/api/views/*`.
  Deployed by hand (`wrangler deploy`), never by CI, because it writes outside
  the repo and needs a credential. Deployed and seeded on 2026-08-24 by IT, so
  `wrangler.toml`'s `database_id` is still the `REPLACE_WITH_D1_DATABASE_ID`
  placeholder and a deploy from here would not work until it is filled in.
  See `worker/views/README.md`, and the read-counter convention below for why
  this exists rather than a hosted analytics service.

### Validators

- **`scripts/validate/Frontmatter.java`** — PR-time content check run by
  `pr-check.yml`, in lieu of a preview (GitHub Pages has no per-PR URLs).
  Required fields present; no dangling `related_posts`; no sponsor `authors:`
  slug without an author bundle; no emoji in a post title; no two pages in a
  folder claiming one series `weight`; a post's `date:` matching its folder;
  closed key sets on events, ads and sponsor author entries; no self-alias.

  **`checkDrafts` covers `draft/`, which nothing else can see.** Drafts live
  outside `content/` so they don't publish themselves — which also means the
  Hugo build never reads one, so a submission missing every required field and
  naming a nonexistent author went green. It applies the rules a published post
  gets, because publishing is only a maintainer moving the folder.

  **It is the ONE hard check scoped to `--changed-since`, deliberately.**
  Everything else guards `content/`, which publishes, so whose PR introduced a
  break is beside the point. `draft/` is a staging area where a submission
  legitimately sits half-finished — an article waiting on its author's profile,
  or the reverse. Checking every draft on every PR failed everybody else's
  unrelated work over someone's unfinished submission. The skip is printed, and
  an unscoped local run still checks all of them.

  **`//JAVA 21+`, not 17.** `TITLE_EMOJI` uses `\p{IsExtended_Pictographic}`,
  which only arrived in JDK 21 — on 17 it throws from the static initialiser,
  so the whole check dies before a single rule runs. It stayed hidden because
  this script runs only on a **pull request**, and most fixes go straight to
  `main`.

  This is why **`.github/PULL_REQUEST_TEMPLATE.md` is not a checklist.** It was
  one, and every item had rotted: it asked for `tags` (no such taxonomy), for
  `draft: false` (no such mechanism), and for a `hugo server -D` preview that
  cannot render a draft. The checks moved into the script; the template asks
  the one question a machine can't answer — what's in this PR.

- **`scripts/validate/BuiltSite.java`** — the check on what Hugo actually
  *produced*, run by `pr-check.yml` and again by `build-deploy.yml` **between
  the build and the deploy**, so a broken build stops before it replaces the
  live site. It reads `public/` off disk, so it needs no server, takes ~5
  seconds over half a million links, and cannot be flaky. External links are
  not checked — a third party being down is not a reason to block our deploy.

  Two checks, both derived, so there is no URL list to keep in step:
  1. **Every source page produced a built page**, through the permalinks in
     `hugo.toml`. This is what catches a whole *section* vanishing — the
     branch-bundle failure mode, where a `where` still parsed and matched
     nothing. A `bundles` flag per section keeps bundle resources out of it.
  2. **Every internal link resolves** — `href`, `src`, `srcset`, `poster` and
     the meta-refresh in every alias page. It subsumes a "the nav works" check,
     since a dead menu entry is a dead link on 4200 pages.

  **The two kinds of dead link are not the same problem, and only one may stop
  a deploy.** A link the TEMPLATES emit is broken for every reader and is a bug
  in this repo, so it fails the run. A link an author typed inside their own
  article is a fact about 2000 imported posts, so those are reported with a
  count — blocking every future deploy on a 2021 typo is how a gate gets
  switched off within the week. The boundary is **`.prose`**, which is exactly
  where `.Content` renders; `--strict` fails on those too, for a cleanup pass.

  Four things are load-bearing:
  - **The base path is read from the home page's own `canonical`**, not
    configured, so it is `/` in production and the subpath under any preview.
  - **A root-relative link not starting with the base path** is its own kind of
    failure ("escapes the base path") — it resolves on a laptop and 404s once
    deployed under a subpath, which is invisible locally.
  - **Existence is tested against a `Set` of the build's filenames**, not the
    filesystem, which makes it case-SENSITIVE everywhere. macOS is not and
    GitHub Pages is.
  - **Percent-escapes are decoded by hand**, not with `URLDecoder`, which turns
    `+` into a space. The emoji `aliases:` are why this matters.

  It has been seen to FAIL, which is the only way a green check means anything.

- **`tests/e2e/` — the browser half of the deploy gate**, run by
  `build-deploy.yml` after `BuiltSite.java`. These are the only checks that can
  see the parts of foojay that **exist only once JavaScript runs** — search,
  both maps, the lightbox, the sortable tables, syntax highlighting, mermaid,
  the calendar grid. Every one fails *silently*: the page still returns 200 and
  simply stops doing the thing. `tests/e2e/README.md` is the full guide.

  - **The "staging environment" is localhost.** `server.mjs` serves `public/`
    on 127.0.0.1 and is hand-written rather than `npx serve` because the point
    is to behave like **GitHub Pages specifically**: pretty URLs, a 301 on a
    missing trailing slash, `404.html` with a 404 status, `Range` support, and
    everything under the base path.
  - **Which pages get tested is DERIVED from the build.** `discover.mjs` finds
    the first post with a code block, a gallery, an embed, and so on. A
    hardcoded pick rots into a test that still *passes*, because the page still
    loads and simply has no diagram on it. A feature the build contains none of
    resolves to `null` and its test skips with a reason.
  - **Third parties are stubbed, never fetched**, so the suite is fast and
    cannot fail on someone else's bad afternoon. That is also why **there are
    no retries**: nothing depends on the network, so a failure that comes and
    goes is a real bug and a retry would hide it.
  - **"Does the video play" is two questions and only one belongs in a gate.**
    A third-party embed is checked structurally; the one self-hosted `.mp4` is
    fetched, range-requested and waited on until `canplay`. Existence is not
    playability — a killed encode once left a 0-byte file that every "does it
    exist" check called fine.
  - **Console errors are not collected** (a stubbed third party logs noise that
    says nothing about this site); `pageerror` and same-origin 4xx/5xx are.
  - **Nothing is resolved against the working directory, and
    `reuseExistingServer` is `false` even locally.** Playwright runs
    `webServer.command` with the config's own directory as cwd, so a relative
    path resolved to `tests/e2e/tests/e2e/`. It failed only in CI, because the
    default `!process.env.CI` had silently reused a stale local server on every
    local run — so the CI launch path had never been exercised once. **Don't
    put that default back.**
  - **`playwright.config.mjs`'s module body runs in EVERY worker**, so nothing
    that writes a file may live there — measured at 39 evaluations on one
    CI-shaped run, i.e. 39 truncating writes racing a read, and the loser died
    on `SyntaxError: Unexpected end of JSON input`. It is `global-setup.mjs`
    now, and the write is staged to a temp file and renamed.
  - **The gate must not count itself.** A browser walking 20 pages per deploy
    would add 20 reads to the numbers *printed on the site*. Two layers: the
    routes are stubbed, and Chromium runs with
    `--host-resolver-rules=MAP * ~NOTFOUND, EXCLUDE 127.0.0.1`, below the level
    any page script can reach — because the first is a Playwright-level promise
    and `sendBeacon` is exactly the request shape not to be wrong about.
  - **Pagefind's matching is fuzzy enough that nonsense still matches** —
    `xqjvbzkwqpfmdlrn` returns 214 results — so only several nonsense tokens
    ANDed together are a genuinely empty query. And **a `loading="lazy"` image
    below the fold has no box**, so it never becomes clickable until the test
    scrolls and waits for the decode.

  `@playwright/test` is pinned **at 1.56.1 or later**: every version below
  1.55.1 downloads browser binaries without verifying the TLS certificate.

### Workflows

- **`build-deploy.yml`** builds with Hugo and deploys to GitHub Pages on push
  to `main` and on `workflow_dispatch` — which is how the two scheduled syncs
  get their data onto the site. It refreshes and commits the generated data
  files **before** the Hugo step, so anything that makes that push fail takes
  the deploy down with it.

  **`hugo-version` is PINNED, here and in `pr-check.yml`.**
  `peaceiris/actions-hugo` resolves `latest` through `formulae.brew.sh`, so an
  `ECONNRESET` from Homebrew's API failed a deploy before Hugo was even
  downloaded. Same argument that vendored Leaflet, mermaid and the webfonts. A
  pin also means the site is built by the version it was developed against.
  Move it deliberately, in both files, having built locally on it first.

- **Three workflows push to `main`, the concurrency group covers only two, so
  every push RETRIES.** The two syncs share `concurrency: data-sync`;
  `build-deploy.yml` cannot join them, its group being `pages` with
  `cancel-in-progress`. That is not a stale-data bug, it is a lost deploy: the
  push runs before Hugo, so whichever job loses the race fails. All three wrap
  the push in a five-attempt `git pull --rebase --autostash && git push` loop.
  **Don't replace that with concurrency configuration** — pull-then-push is two
  commands and no group setting closes the window between them. And don't read
  `! [rejected] main -> main (fetch first)` as a protection failure; that is
  git's own fast-forward check, where a ruleset says `GH006`.

- **A PUSH BY A WORKFLOW DOES NOT BUILD THE SITE, so the deploy is
  DISPATCHED.** Events triggered by `GITHUB_TOKEN` do not create a workflow
  run, and `workflow_dispatch` is the documented exception. For as long as the
  syncs existed their commits landed and nothing rebuilt — confirmed against
  the run history, not the docs. Both now end with `gh workflow run
  build-deploy.yml --ref main`, and only on runs where a file changed. Their
  commits also carry **`[skip ci]`**, which did nothing under `GITHUB_TOKEN`
  but stops a double build now the pushes use an App token.

  Don't "simplify" this by relying on the push, and don't add a `workflow_run`
  trigger: that fires on every completed sync run, including the majority where
  nothing changed, and would deploy on a timestamp.

- **`publish-scheduled.yml`** is what actually causes a scheduled post to go
  out — see the scheduling convention below. It commits nothing, which makes it
  the cheapest workflow here: no App token, no bypass entry, no toolchain.

- **Protecting `main`. LIVE since 2026-08-31** as a single repository ruleset
  targeting the default branch, carrying *deletion*, *non_fast_forward*,
  *pull_request* and *required_status_checks* (`build-and-validate`), bypassed
  by `Repository admin` and the app. `BRANCH_PROTECTION.md` is the runbook.

  The blocker was never the rules, it was the token: **`GITHUB_TOKEN` cannot be
  granted a bypass** — a ruleset bypass list takes roles, teams, apps and
  deploy keys. So all three workflows mint an App token
  (`actions/create-github-app-token@v2`, app id `4781767`, *Contents: read and
  write* only), guarded by `if: vars.DATA_SYNC_APP_ID != ''` so a fork is
  unaffected. `Require signed commits` stays off, because the bots do not sign.
  The bypass is verified by commit `421fb7b57`, a bot commit landing two
  minutes after the ruleset went active. **A green sync run is not evidence** —
  the one meant to prove it took an early branch and never reached `git push`.

  **Two deployed details differ from what the runbook recommends, and both want
  a decision rather than a rediscovery:**
  - **It is ONE ruleset, and `Repository admin` bypasses it in mode `always`**,
    so an admin currently bypasses *deletion* and *non_fast_forward* too — i.e.
    `main` can be force-pushed or deleted. It wants to be **two**: `main
    integrity` (deletions + force pushes, with an **empty** bypass list, since
    those rules don't block an ordinary push) and `main review` (PR + status
    checks, bypassing admin and the app). A bypass exempts an actor from every
    rule in its own ruleset, which is the whole reason to split.
  - **Required approvals is 1, not 0.** With a single maintainer that is not a
    review gate — GitHub won't let an author approve their own PR, so merges
    happen through the admin bypass rather than the rule. Set it to 0 if the
    intent is "a PR must pass `build-and-validate`".

### Generated data files

Never hand-edit any of these.

- **`data/jugs.yaml`** — `fetch/Jugs.java`, rendered at `/jugs/`. Fix a JUG
  upstream in GlobalWWJugs.
- **`data/java-champions.yaml`** — `fetch/JavaChampions.java`, rendered at
  `/java-champions/`. Fix an entry upstream.
- **`data/geocode-cache.yaml`** — `fetch/JavaChampions.java`. Place string →
  coordinates, or `found: false`. **Do** delete an entry, or the whole file, to
  force a fresh lookup; a full rebuild is ~250 requests inside the free tier.
- **`data/jug-events.json`** — `fetch/JugEvents.java`. Read as
  `index hugo.Data "jug-events"`; a dash cannot be a field selector.
- **`data/jvm-weekly.yaml`** — `fetch/JvmWeekly.java`, read the same way.
- **`data/views.json`** — `fetch/ViewCounts.java`. `slug -> total reads`, baked
  into the HTML.
- **`data/legacy-views.json`** — each post's WordPress view count at the final
  import. Nothing reads or writes it now; the counter holds these numbers in
  its `legacy` column. Committed because it is the **only** copy of a number
  with no other source left.

## Known gaps

Most of what this section used to list closed at or before cutover: the
scrapers are deleted, the fetchers all run daily for real, the theme carries
the live site's own palette, Featured Authors exist, `/pedia/` holds all 47
entries, search is live, and the paid banner carousel is built (see the ads
convention below). What is genuinely still open:

1. **Ketch's Google Consent Mode plugin is listed but not configured** on the
   `azul`/`foojay_io` property: the config has `"googletag": {}` — an empty
   object, where `gpc` beside it carries real `purposeMappings`. So Ketch
   records consent on its own side and never calls `gtag('consent','update')`.
   That is why loading GA4 directly flatlined analytics at cutover, and why
   the site is back on the GTM container with no Consent Mode defaults.
   Configuring that plugin is what would make a direct GA4 tag viable again —
   and **a plugin key in the config listing is not evidence it is
   configured**; only a browser can tell you.
2. **The accessibility backlog is the site's biggest real gap** — roughly 3,100
   images across `content/` with no description, plus 287 podcast posts with no
   transcript. `Frontmatter.checkImageAltText` warns rather than fails; see the
   accessibility convention for why.
3. **53 author-written dead links** inside article bodies, reported by
   `BuiltSite.java` rather than failing it. Three clusters: bare domains with
   no scheme, WordPress artifacts (`_wp_link_placeholder`, `/wp-admin/…`), and
   `/wp-content/uploads/` paths never localised, including two `.mp4`s and a
   PDF simply absent from the build.
4. **212 external image references are unrecoverable** — 44 dead, 9 behind a
   bot wall, 4 mermaid.ink 503s, 3 connection failures and 5 live CI badges,
   which are deliberate. Each is named with its post in the sweep's report.
5. **`worker/views/wrangler.toml` still has a placeholder `database_id`**, so
   a `wrangler deploy` from this repo would not work. It was deployed by IT.
6. **The two branch-protection deviations** above want a decision: one ruleset
   instead of two, and required approvals at 1 instead of 0.
7. **Hotjar (`hjid` 2547610) and Reo.dev (`b38cec169d83063`)** ran on the
   WordPress site and were deliberately not carried over. Decide separately.

**A lesson worth keeping from a gap that closed**: `/pedia/` looked complete
because the WordPress index is **paginated and lists 31 of 47**.
`terminology-sitemap.xml` was the honest count. Check a sitemap, not an index
page, if anything here is ever compared against an archived copy of the old
site.

## Conventions to keep following

### Accessibility

- **The site targets WCAG 2.2 AA, and `/accessibility/` is the public
  statement of where it actually is.** Not a legal deadline — foojay.io is very
  likely outside the European Accessibility Act's scope — but it is an
  Azul-branded property procurement asks about, and a Java-developer audience
  browses with a keyboard. Keep that page's "not there yet" list honest and
  **update it when a gap closes**.

  Seven things are load-bearing, each a measured failure before it was a rule:
  - **`--focus-ring` is its own token, and in light it is NOT `--brand-accent`**
    (2.02:1 on white, so the one cue a keyboard user navigates by was
    invisible). Dark keeps the accent at 8.57. **Never write `outline: none`.**
  - **A link in running text is UNDERLINED** (`.prose a`) — no blue foojay owns
    clears 3:1 against both the body text and the page. Write it as a bare
    `.prose a`, **never a chain of `:not()`s**: each adds its argument's
    specificity and beat every exclusion (422 champion avatars kept theirs).
  - **Anything hidden off-screen must leave the TAB ORDER too.** `visibility`
    does it; descendants use **`inherit`, not `visible`**, or they climb back
    out of the fix.
  - **A thing that covers the page is a modal**: focus in, trapped, and
    returned. `nav.js` and `lightbox.js` do it by hand; `/calendar/`'s dialog
    gets it free from native `<dialog>`, which is the better route.
  - **A click handler on a non-focusable element is a mouse-only feature.**
    Where an image is inside a link, bind the LINK.
  - **Anything moving for more than five seconds needs a visible pause**
    (2.2.2). A hover/focus pause is not one — a pause a mouse movement undoes
    is not a pause, hence `stopped` in `ad-carousel.js`.
  - **A sideways-scrolling box needs `tabindex="0"`**, and a table wide enough
    to need one must be in a box at all, or the whole page scrolls sideways.

  **`Frontmatter.checkImageAltText` WARNS, it does not fail** — the one check
  that doesn't. Whether `![](x.png)` is wrong depends on whether the image
  carries meaning, which nothing there can see, and failing would block a
  first-time contributor over a judgement call whose predictable answer is
  `alt="image"` — worse than empty. `pr-check.yml` passes `--changed-since` so
  an author sees their own post, not the imported backlog.

### Images

- **Images have a per-file budget, and the deploy is why.** The built site hit
  **1.26 GB against GitHub Pages' hard 1 GB artifact limit**, and that warning
  lands on a run that otherwise goes **green**. The site is ~864 MB now with
  ~136 MB of headroom. Anything over 4 MB fails the PR check.

  **JPEG, not WebP, for large PNGs** — Frank's call; q85 saves 81% against
  WebP's 89%, either clears the limit, and JPEG is what the other 1400 images
  already are. The hard limit is transparency: the 22 files that genuinely use
  alpha stay PNG, which is not the same as *having* an alpha channel (many
  WordPress PNGs are RGBA with every pixel opaque). The `--png-min` floor is
  100 KB, and it is what makes the policy self-selecting — a flame graph was
  measured and DECLINED, because flat line art compresses better as PNG.

  Four lessons from `cleanup/images.py`, all of which cost real damage:
  1. **Finish each bundle before starting the next.** Rewriting references at
     the END let a mid-run commit capture converted files pointing at deleted
     `.gif`s — 4 broken images on the live site.
  2. **Never unlink a source unless the destination is verified on disk.** Two
     disagreeing thresholds lost two GIFs, recovered from git.
  3. **Encode to a temp path and rename.** A killed run left a 0-byte `.webp`
     that the "destination exists" guard then treated as real.
  4. **Do not verify an animation by exact frame count.** libwebp merges
     duplicate frames heavily; the floor is 20%, catching gross truncation only.

  **`rewrite_in_bundle` matches WHOLE FILENAMES.** A plain `str.replace`
  corrupted two posts: one bundle holds both `dummies.png` and
  `image-764x1024-dummies.png`, so converting the first rewrote the suffix of
  the second. **Audit every local image reference after any bulk image pass** —
  a reference to a file that is not there is invisible in a build log.

- **Every image a post references is stored locally, and an external one is
  named `<stem>-<8 hex of its URL>.<ext>`.** Hotlinking spends a stranger's
  bandwidth, leaks each reader's IP, and dies without telling us — 129 of 1710
  hotlinked images were already dead at import. The hash is not decoration: 58
  bundles reference an external URL whose basename is **already** a file in
  that bundle, so without it `Files.exists` short-circuits and the page
  silently shows a different picture. A foojay-hosted image keeps its bare
  basename.

  Four rules govern anything added by hand now:
  - **Lookup is by STEM, ignoring extension**, which finds a file later
    re-encoded and copes with the 221 external URLs that carry no extension.
  - **What came back has to BE an image.** A dead URL rarely 404s cleanly — it
    serves a login wall or placeholder with a 200, and writing that as
    `foo.png` turns a visibly broken image into one that *looks* localised.
  - **WordPress's emoji images are restored to the CHARACTER**, since the alt
    text is the character itself.
  - **A GitHub Actions `badge.svg` stays hotlinked** (5 in `content/`) — it
    reports whether a build passes *now*. That is the whole exclusion list.

  **Shrink BEFORE the file enters the repo**: nothing rewrites git history, so
  a full-size blob downloaded into `content/` is there for ever.

- **A body `<h1>` is the author's heading.** A bare `h1` in the scraper's noise
  selector, meant to drop the repeated title, removed **503 headings across 137
  posts** — and the text under a deleted heading stays, so articles silently
  read as one slab with nothing downstream able to tell. Only an h1 repeating
  the TITLE is removed; the rest are demoted to h2, since `title:` is already
  the page's h1.

### Content storage and the shape of `content/`

- **Idempotency everywhere**: any script touching `content/` must be safe to
  re-run. **And a re-run must not produce a diff that carries no information** —
  a large diff is what the next real change hides behind. Hence: write a data
  file only when its content changed, not when a timestamp moved; and emit a
  key only when the script could ever have filled it (`gitlab:` is omitted
  where empty, but `bluesky: ""` stays, because that genuinely means "we looked
  and there was no icon").

- **URLs are load-bearing.** Every converted post, author and page keeps its
  legacy path via `aliases:` (and an explicit `url:` for pages) — don't
  restructure a URL without adding one. WordPress ran a redirect layer on top:
  **89 concrete rules are `aliases:` in `content/`** (chains resolved to their
  final destination, since an alias aimed at another redirect is one search
  engines discard), and **3 regexes are Cloudflare config** in `CUTOVER.md` —
  `^/blog/(.*)` (209k hits, foojay's original scheme), `^/almanac/(jdk|java)-…`
  (103k) and `^/docs/(.*)` (530). 17 export rules were deliberately skipped
  because their targets 404 on WordPress too.

  Three post URLs carry an emoji `aliases:` entry written as the literal
  character, because `stripEmoji` removed it from the folder name. **One
  deliberate exception**: heading *fragments*. WP's `#h2-N-slug` anchors are
  gone, so pre-cutover deep links land at the top of the post. Accepted
  knowingly.

- **Every page self-canonicalises, and `canonical:` means "not ours".** `$self`
  is **not** `.Permalink` — on a paginated list that is page 1's URL for *every*
  pager, so `/today/page/2/` and 123 category pagers declared themselves
  duplicates of page 1, asking Google to drop the only crawl path into the
  archive. The only legitimate `canonical:` points at a different page or site:
  the cross-posted articles, and `/download/`.

  **A cross-post canonical rots, and a dead one is worse than none** — it tells
  Google the real version is at a URL that 404s. All 837 were re-checked; 47
  removed. **The 48 affected posts are `frozen: true`, and that is the fix** —
  an identical audit a week earlier removed the identical 48 and a re-scrape
  put every one back. Two traps if it is ever re-run: **a 4xx from a bot wall
  is indistinguishable from a deleted page** (Medium flip-flops 403/410 on the
  same URL, so probe four times and record all four), and **check with two
  clients before believing a failure**, and probe a redirect's TARGET rather
  than the chain.

- **A headerless table is stored with an EMPTY header row**, and
  `render-table.html` omits the `<thead>` when every header cell is blank. GFM
  cannot express a table without a header, and Flexmark handed a headerless
  WordPress table emits a delimiter row with nothing above it — which Goldmark
  does not recognise as a table at all, so **111 tables across 54 posts
  rendered as a wall of literal pipes**. An empty header, **not** promoting the
  first row: only 25 of the 111 have a first row that is actually a header, and
  promoting a legend turns a data row into a heading.

- **Headings are stored as ATX (`##`), not setext underlines.** Levels map 1:1
  to the original, and a setext underline heads the **whole paragraph** above
  it, which had three posts rendering prose inside their `<h2>`. Don't
  reintroduce `----` underlines.

- **Code blocks are stored as Markdown fences and rendered as EnlighterJS.**
  `_markup/render-codeblock.html` turns a fence into the
  `<pre class="EnlighterJSRAW">` the vendored initialiser looks for; Hugo's own
  Chroma highlighting is bypassed. Storage and presentation stay separate so
  swapping the highlighter means editing one file, not 1000 posts. The hook
  maps fence tags to the 57 languages in the **vendored** bundle — that list is
  derived by reading the bundle, not from docs, after a hand-written version
  silently rendered 592 blocks as `generic`. **There is no `enlighterjs:`
  flag**: detection covers every case, and defaulting it to true would pull
  144 KB onto the 1477 pages with no code. Don't reintroduce one, and don't put
  raw `<pre class="EnlighterJSRAW">` into `content/`.

  **Check the fence balance after any bulk content pass.** A file ending inside
  an unclosed fence is invisible in the source and Hugo builds it without a
  word — 7 posts were live with everything after their last code sample
  rendered as one grey slab.

- **Mermaid diagrams are a `` ```mermaid `` fence, vendored and lazy.** The hook
  branches on the tag *before* the EnlighterJS path (mermaid is not in
  `$supported`, so it would otherwise render as highlighted source), and
  `baseof.html` loads the library when the RENDERED page contains one — so no
  `mermaid:` flag either. It is the **ESM build** (30 KB entry plus chunks,
  against a 3.4 MB UMD single file), which is why `static/vendor/mermaid/` is
  104 files; update with `npm pack` and copy the entry plus `dist/chunks/…`,
  **no `.map` files**.

  Four things are load-bearing: `htmlEscape` + `safeHTML` (diagram syntax is
  full of `-->` and `<br/>`); the source is stashed in `data-mermaid-source`
  before rendering, because mermaid replaces the text with an `<svg>` and a
  theme flip needs a re-render; the CSS keys on mermaid's own `data-processed`,
  since the `<pre>` survives and would otherwise keep code-block styling; and
  `suppressErrors: true` with `securityLevel: "strict"`.

- **AsciiDoc is a first-class content format: name the file `index.adoc`.**
  Everything else about a bundle is identical — same frontmatter, same images
  alongside, same PR checks. `template/post.adoc` is the starter file.
  Rendering goes out to the `asciidoctor` binary, which `hugo.toml`'s
  `[security.exec]` must allow (naming one binary REPLACES the default list, so
  all five are repeated there) and whose `GEM_HOME`/`GEM_PATH` are in `osEnv`
  because Hugo hands the child a filtered environment.

  **Hugo's render hooks are a Goldmark feature, so an AsciiDoc article gets
  none of them.** `partials/article-html.html` rewrites asciidoctor's HTML
  afterwards to close that gap, and is called from `baseof.html` too — which is
  not optional, since baseof decides whether to load mermaid by looking for
  markup this partial creates. It does four jobs: code blocks become
  EnlighterJS or mermaid elements (through the same language registry the
  Markdown hook uses, so the two cannot drift); root-relative and absolute
  foojay.io links get the baseURL subpath; **external links get
  `target="_blank"`**, so the two formats agree on what a link to another site
  does (AsciiDoc's own `^` suffix still works and is left alone); and callouts
  become circled digits, because EnlighterJS re-renders a block from its
  `textContent` and would otherwise print the marker's markup literally.

- **A runnable snippet is the `{{< jdoodle >}}` shortcode**, whose `.Inner` is
  the code and whose `files=` names data files kept once in `assets/jdoodle/`.
  `template/post.md` documents it for authors. The loader is derived with
  `.HasShortcode`, so there is no `jdoodle:` flag — same rule as the
  EnlighterJS and mermaid loaders.

  It replaced 17 hand-written HTML embeds that had drifted into four shapes,
  and the shortcode exists because three separate things silently corrupt a
  payload read as `textContent`: **`hugo --minify` collapses a whitespace run
  outside `<pre>`**, which had stripped every leading space from every snippet
  on the site (`keepWhitespace = true` does not fix it — measured — and costs
  +1.9 MB); **a blank line inside the code ended the surrounding CommonMark
  HTML block** and handed the rest to Goldmark; and **`<xmp>` is not a `<pre>`**,
  being RAWTEXT, so `List&lt;Person&gt;` reached the compiler literally. Inner
  text never reaches Goldmark now, leaving only the escaping — `htmlEscape` then
  `safeHTML`, once, exactly as `render-codeblock.html` does.

  **A data file is at `/uploads/<name>` inside the sandbox**, which is the path
  the snippet opens. Two tutorial steps read the same 100-row `testdata.csv`,
  which is why it lives in `assets/` rather than being pasted into both.

  **No `data-version-index` and no `clientid` parameter.** The client id is
  foojay's own and identical everywhere, so it is a constant in the shortcode
  rather than a knob set to one value. The version is omitted entirely: the
  plugin reads `dataset.versionIndex` and `JSON.stringify` drops it when
  undefined, so leaving it off means JDoodle's current default — which is what
  a tutorial teaching Java wants, and what the pins it replaced (4 and 6, both
  years stale) had stopped being.

  **Verify a change here by extracting the payload from the BUILT, MINIFIED
  HTML exactly as pym does** — `textContent` of the div, or of its
  `[data-type=script]`/`[data-type=file]` children when `data-has-files` —
  then `javac` and run it. Nothing static can check this: indentation loss, an
  undecoded entity and a truncated snippet all build green and fail only inside
  somebody else's iframe. The conversion was verified that way, all 11 payloads
  byte-identical to what the old markup produced.

  The two posts that explain JDoodle keep **raw HTML inside code fences** as
  documentation samples. Those are inert and deliberate; don't convert them.

- **Email addresses are decoded on the way in, never left obfuscated.**
  Cloudflare's Email Address Obfuscation turns every address into a placeholder
  plus an XOR-encoded copy. Two rules: a decode that isn't an address becomes
  text rather than a link (the matcher has false positives like
  `setup-java@v5.5.0`), and an address **inside code** is always plain text, or
  `--docker-email="a@b"` becomes `mailto:a@b`.

- **WordPress's decorative `<hr>`s and `<br>` spacers are dropped, not
  converted** — they carried styling the WP theme supplied. A `<br>` with text
  on its line is a real hard break and stays.

- **Posts are filed by publish date** (`content/posts/<y>/<m>/<d>/<slug>/`),
  purely to keep a 2000-post directory browsable. It has no effect on the URL.

- **There is no `tags` taxonomy, deliberately.** WordPress tags its posts but
  never renders them, so the scrapers never saw one and every post landed with
  an empty `tags:` — 0 term pages against categories' 752. All of it was
  removed. Categories are the taxonomy.

- **Emoji come off post TITLES, never out of bodies.** A title is the card, the
  RSS item, the browser tab and the `og:title`. Bodies are untouched: 404 posts
  carry 3443 emoji, among which arrows, `(TM)` and comparison-table check marks
  are load-bearing. The rule is `\p{IsExtended_Pictographic}` plus skin tones,
  keycaps, variation selectors and ZWJ — **not** `\p{IsEmoji}`, which is true
  for ASCII digits and would eat the "5" out of "The 5 Knights".

- **`related_posts` is manual**, chosen by the author — never replace it with
  an automated similarity algorithm. Four entries is the house convention.

- **Galleries are the `{{< gallery >}}` shortcode**, migrated posts included:
  one filename per line, `| caption`, `| |` before differing alt text, plus
  optional `cols=` and `caption=`. The template derives the link to each
  image's full-size original from the thumbnail name. Don't reintroduce
  WordPress block markup.

- **`render-image.html` emits `width`/`height`, and only for rasters** — without
  them every image in a long post shoves the text below it down as it decodes.
  The check is on `.MediaType.SubType`, not `ResourceType`: Hugo reports an SVG
  as an image but `.Width` on one halts the build.

- **`render-link.html` resolves page-bundle resources**, the same way
  `render-image.html` does. Without it a link to a PDF or zip in the author's
  own folder resolved against the SITE ROOT while Hugo published it inside the
  bundle — **143 internal links were dead this way**, unnoticed because
  `![](shot.png)` resolved and `[handout](handout.pdf)` did not.

- **Render hooks must redo the escaping Goldmark would have done.** Two live
  shapes: `render-codeblock.html` needs `htmlEscape .Inner | safeHTML`, or
  html/template escapes a second time and `->` reaches the reader as `-&gt;`
  (~950 posts); `render-link.html`/`render-image.html` need `htmlUnescape` on
  `.Destination`/`.Title`, or `?a=1&amp;b=2` becomes a param named `amp;b`
  (~90 posts). Check both when editing a hook.

### Templates, metadata and feeds

- **`$isTrial` is gone (2026-09-23), and the shape it had is what to keep.**
  While the Hugo site was a copy of still-live WordPress content, a crawlable
  preview host would have put ~2600 duplicate URLs into Google's index; four
  templates derived `baseURL != params.productionBaseURL` and suppressed
  indexing and analytics. **It was a derivation, never a config flag**, so it
  turned itself off when the URL changed with nobody doing anything. Nothing
  deploys anywhere else now, so it was four copies of an always-false
  condition and is deleted. If a preview host is ever wanted, re-derive it the
  same way rather than adding a flag.

- **A page title carries the short brand token.** `<title>` is the page title,
  then `(page N)` on a pager, then `| Foojay.io` — except on the home page,
  whose title IS `site.Title`, and on `og:title`, where `og:site_name` already
  carries the brand. Appending the full `site.Title` is what the template used
  to refuse, and that reasoning still holds: it is 33 of the ~60 characters
  Google renders, on 2147 titles that already average past that.

- **`og:image` always resolves, via a dedicated social card.** Pages without an
  `image:` fall back to `images/foojay-social-card.png` (1200x630) and the card
  is always `summary_large_image`. A second asset,
  `images/foojay-logo-square.png` (512x512, white ground), exists only as the
  schema.org `Organization` logo, because Google composites a logo onto white
  and the header wordmark is light-blue-on-transparent. **Don't** re-crop
  `foojay-logo.png` to make either.

  **A hero of `Favicon-3-2.png` is a migration artefact.** Yoast refuses to
  emit an SVG or AVIF as `og:image` and serves the favicon instead, so **48
  posts lost their real hero**, indistinguishable in the page HTML from a post
  with none. An **SVG hero is deliberately kept off `og:image`** — no preview
  scraper renders SVG, so such a post previews as the generic foojay card
  rather than its own picture. AVIF is left through, knowingly.

  **No post carries an SVG hero any more** (2026-09-23): the 17 that did were
  given 1600x900 JPEGs, rendered from the SVG with headless Chromium and
  composited with Pillow. Nothing is cropped — an opaque card is scaled to fit
  and the canvas extends its own border colour, so the letterbox is invisible;
  a transparent logo or diagram gets white, or `--surface-navy` when the mark
  is light and sparse. The four SVGs that are also in-article figures stay in
  their bundles. `baseof.html`'s skip remains the guard for the next one, and
  `template/post.md` already tells authors not to use SVG for a hero.

- **Structured data covers three page kinds and nothing else.** A post gets an
  `@graph` of `BlogPosting` + `Organization` + `BreadcrumbList`, an author gets
  `Person`, the home page gets `Organization` + `WebSite`. Everything is
  derived. **The Organization is ONE node** in its own partial — a post used to
  inline its own `publisher`, so foojay was two entities sharing a name, and
  the inline copy used the wide transparent wordmark Google cannot composite.
  **Breadcrumbs follow the post's FIRST category** and the last crumb has no
  `item`: one path from the root, matching the chips the page itself shows.
  Use `site.Language.Locale`, not the deprecated `LanguageCode`.

- **`partials/meta-description.html` is the single definition of a page's
  description**, used for `<meta name=description>`, `og:description` and the
  JSON-LD description, so the three cannot disagree. It replaced a bare
  `.Params.description | default site.Params.description`, which put one
  boilerplate string on **470 URLs** — 17% of the sitemap describing none of
  them. Derived per kind: an author's `bio:`, a term's name and page count, a
  sponsor's name and article count. An explicit `description:` always wins, and
  author bundles may now set one (`template/author.md` documents it) — worth
  doing wherever a long bio would otherwise be cut mid-list. Every source is
  **length-guarded at 200 characters** on a word boundary; the stored text is
  untouched.

- **`/search/` and the 404 are out of the index, and `/search/` is out of the
  sitemap** — it has no server-rendered content to rank on, so listing it and
  then telling it `noindex` was two contradictory instructions. The 404 also
  has **no** canonical: it is not a page with a URL of its own.

- **The home page's `<h1>` is visually hidden**, and is the only one on the
  site that is. The page opens straight into the lead card, so its first
  heading was an `<h2>` — the most linked-to page had no top-level heading. The
  text is `site.Title`, not a keyword line.

- **The webfonts are SELF-HOSTED and `vendor/fonts/fonts.css` is generated.**
  Barlow and Source Sans 3 came from `fonts.googleapis.com` on every page: a
  render-blocking stylesheet on someone else's server in front of the whole
  site's first paint. Both are SIL OFL 1.1. Five things are load-bearing:
  the `url()`s are **relative**, so nothing depends on the base path (the one
  class of bug `BuiltSite.java` cannot see, since it reads HTML not CSS);
  **exactly two faces are preloaded**, the ones every first paint waits on;
  **those preloads are also the test**, since a wrong path fails silently into
  Segoe UI but a preload is requested on every page; **regenerate with a modern
  User-Agent**, or Google serves TrueType at four times the bytes; and
  **`document.fonts.check()` cannot verify this** — it returns true for a
  family with no `@font-face` at all, which is exactly the failure being
  tested for.

- **The LCP image is eager and `fetchpriority="high"`; everything else is
  lazy.** The post hero and the home page's lead card are the LCP element and
  were being scheduled behind the stylesheet, the fonts and ten cards below the
  fold. `post-card.html` keeps `loading="lazy"`.

- **`partials/post-thumb.html` is the single definition of a card's thumbnail,
  and a card ALWAYS has one.** It renders the image over a derived placeholder
  tile, so three states are one box: the image loads; the post has no `image:`;
  or the image FAILS, because the `<img>` sits on top and carries
  `onerror="this.remove()"`. That third case is why it exists — **74 posts
  hotlink their hero and 11 are already dead**, and no build step can see that,
  which is why the fallback is an inline `onerror` rather than a check.

  The tile is **derived, never authored**: the label is the post's first
  category and the hue is `mod (hash.FNV32a $label) 360`, so a category reads
  as one colour site-wide. Three traps: `.post-card-image img` needs
  `position: relative; z-index: 1`, or the absolutely-positioned tile paints
  over every image on the site; it needs its own opaque background, since many
  WordPress heroes are transparent PNGs; and the search page's `hueFrom()` must
  stay byte-compatible with `hash.FNV32a` (FNV-1a with `Math.imul`).

  **`resource-url.html`'s external test is `http://`/`https://`, not a bare
  `http`** — four bundles hold a file whose *name* starts with the scheme, and
  a bare prefix check passed those through as relative paths that resolved on
  the post's own page and 404'd from every listing one directory deeper.

- **`partials/paginator.html` is the single definition of how a page
  paginates.** Not tidiness: `<head>` renders before `main`, so the canonical
  needs the pager first, and Hugo errors if `.Paginate` is called twice with
  different arguments. It must be plain `partial`, **never `partialCached`** —
  Hugo renders each pager by re-executing the same Page, so a page-keyed cache
  serves page 1's posts on every pager.

- **Author and sponsor profiles are BRANCH bundles, which is what makes them
  paginate.** `.Paginate` accepts only `home`, `section`, `taxonomy` and
  `term`; a prolific author has 290+ articles. URLs are unchanged via
  `[permalinks.section]`, and view-counter keys are unchanged because
  `.File.ContentBaseName` returns the folder name for `_index.md` too.
  **`content/authors/` is FLAT** — letter buckets became 23 sections claiming
  URLs like `/today/author/a/`.

  Four things broke silently in that conversion, all the same shape — a filter
  that still parses, still runs, and now matches nothing:
  1. **`site.RegularPages` does not contain branch bundles**, so every
     `where site.RegularPages "Section" "authors"` returned empty and the A-Z
     grid, sidebar widget, HTML sitemap and byline lookup rendered *nothing*.
     Use `partials/authors-all.html` and `partials/sponsors-ordered.html`.
  2. **`.IsPage` is false for a profile**, which silently stopped author view
     counts and `Person` JSON-LD. Both key off `type:` now.
  3. **A cascade applies to the page that declares it**, so
     `/today/author/` rendered with the *profile* layout. Both index files set
     `type:` explicitly, which always beats a cascaded value.
  4. **The RSS cascade targeted `kind: page`** and matched nothing afterwards.

- **"Edit this page on GitHub" is DERIVED from `.File.Path`**, and a page Hugo
  generates has none — **`with .File` is the whole guard**. It is a URL builder
  plus a link partial, because a podcast episode's note points at
  `transcript.md` beside the page rather than at `index.md`. Which layouts call
  it is a rule, not a list: a page gets the link when what you are reading IS
  its file, so the derived pages deliberately don't. It carries
  `data-pagefind-ignore`, or 2246 pages are a result for "edit".

- **Internal links are `.RelPermalink`; only absolute-by-contract URLs are
  `.Permalink`.** `.Permalink` is built from the CONFIGURED baseURL, which is
  not what `hugo server` serves. Keep `.Permalink` for canonical, `og:url`, the
  JSON-LD `url`/`@id`, RSS `<link>`/`<guid>` and alternate-format links.

- **A pager on a profile carries an `#articles` anchor; one on a listing does
  not** — on a profile the grid sits below a bio and stats, so paging without
  it throws the reader back to the top.

- **`partials/pagination.html` is ours, not `_internal/pagination.html`**,
  which had three faults fixable only in markup: a nested `<span>` inside each
  arrow's `<a>`, an `<a aria-current>` the theme's `.active span` rule could
  never match, and a disabled arrow rendered as an `<a>` with no href. Now
  every cell is one `.pagination__btn`, sized with `inline-flex` + fixed
  `height` rather than padding; non-links are `<span>`, so focus only lands
  where you can go. It renders **above and below** the grid, the window is
  **always five numbers** when five exist, and the ends are the numbers 1 and
  `$total`. **Use integer arithmetic** — Hugo's `math.*` return float64, and a
  comparison against an int page number is always false, which bit twice
  silently. On a phone it becomes two rows and drops `--far` cells.

- **Feeds are posts-only and capped at 30.** `[services.rss] limit = 30` —
  Hugo's default is unbounded, which made `/index.xml` 3.85 MB of 2584 items
  led by pages carrying `pubDate Mon, 01 Jan 0001`, a date some aggregators
  drop the item over. `layouts/index.rss.xml` filters `Section "posts"`,
  because "what's new on foojay" means articles.

- **`partials/feed-link.html` is the single definition of how a feed is
  OFFERED.** 484 working feeds were advertised only as a `<link rel=alternate>`
  in `<head>`, which no browser surfaces — in practice unreachable. It renders
  **nothing** when the page has no `rss` output, so no caller guards.

  **The label is NOT a parameter**, because it was one for a day and `/ai/`
  said something different from everywhere else. **One placement rule**: the
  page's own meta line, beside the read count. **It opens in a new tab** —
  deliberately not the usual same-origin rule, because the destination is not a
  page: browsers render a feed as raw XML or hand it to an app, so in the same
  tab a curious click destroys the reader's place. `--feed` is its own colour
  token in both schemes and deliberately not the canonical `#f26522`, which is
  2.77 on `--page-head-bg`.

- **`/calendar/` has a feed of the next 30 events**, which needed its own
  template because `[outputs]` takes *classes* of page and this is a `page`.
  Events come from `partials/calendar-events.html`, so the feed cannot disagree
  with the page. Two things an event feed faces that an article feed doesn't:
  **`pubDate` is the sync time, one value for every item, not the event's
  start** (a reader sorts by pubDate descending, so dating by start buries next
  week's meetup under next year's conference); and **so WHEN it is has to be in
  the item**, title and description both, **including the UTC offset**.

- **`/jugs/` and `/java-champions/` carry their WHOLE list**, the one place the
  30 limit is ignored: these lists barely change, so what a reader subscribes
  for is an ADDITION, and A-Z cut at 30 hides every JUG from H onwards for
  good. Their pubDates differ because their data does — a champion's `year` is
  a real per-item date (ordered `year` descending, dated 1 January), while a
  JUG has none, so a JUG item carries **no pubDate**, which is legal RSS 2.0
  and better than claiming all 100 were published today on every deploy.

- **A section whose children are BRANCH bundles has an EMPTY feed**, and three
  were shipping one — a `<channel>` with no `<item>`, advertised in `<head>`,
  where a reader could subscribe and receive nothing for ever. A sponsor
  profile gained its own template over `partials/sponsor-posts.html`; the two
  listing pages set `outputs: [html]`, since a feed of "new author profiles" is
  not a thing anyone wants.

- **An XML declaration is only a declaration at byte 0**, and 348 author feeds
  shipped a blank line above it — invisible, because a lenient reader takes it
  anyway. All four feed templates write it with `{{-`. Re-run a parse over
  every built feed after editing one; nothing else looks at these files.

- **`enableGitInfo` dates the pages that have no date, and sits BELOW `date:`**
  in the `[frontmatter]` chains. Leading with `:git` would stamp all 2147 posts
  with the migration date and tell Google the whole archive changed at once.
  This needs **full git history at build time**, hence `fetch-depth: 0` — on a
  shallow clone every page claims one identical lastmod.

### Sections and features

- **`static/js/cluster-map.js` is the world map**, shared by `/java-champions/`
  and `/jugs/`. The template supplies only the points, the noun and an element
  id; grouping, badge tiers and cluster totals live in the script, or the same
  number would mean something different on each map.

  **One marker per PLACE, not per item.** Coordinates are city centres, so
  items genuinely share a point (22 champions on the USA centroid, 16 on
  London) and separate markers stack into one unclickable pin. **A cluster's
  number counts ITEMS, not markers** — markercluster's default counts markers,
  so a cluster over the Benelux would read "3" while covering 30 people. **A
  popup is built on OPEN, not at page load**: passing a node instead of a
  function built all 240 popup trees up front, and once each row carried an
  `<img>` that meant 420 requests at javachampions.org before any click.

  Smaller load-bearing points: popups are **DOM nodes, never an HTML string**,
  since every name is upstream data; the grouping key is **`toFixed(5)`**, so
  `51.5072` and `51.50720` don't split; `MarkerCluster.Default.css` is
  deliberately not loaded; the badge is **sized by content**, since "1" and
  "422" are different widths; and the `<script>` tag carries **no `defer`**,
  because the inline call runs at parse time.

  **Leaflet and markercluster are VENDORED** (v1.9.4 / v1.4.1, from `npm pack`,
  **no `.map` files**, verified byte-identical to what unpkg served). They came
  from a CDN, which cost three things: the map disappeared *silently* when
  unpkg was slow, the deploy gate could only skip its map tests, and every
  reader of those pages sent a third party a request. **The map TILES are still
  third-party and cannot be anything else** — but a failed tile leaves the
  markers working over an empty ground, where a failed library left nothing.

- **`lightbox.js` skips anything inside `.leaflet-container`**, and that is not
  a nicety: it binds `.prose img`, and **every map tile is an `<img>`**, so
  tiles took the zoom cursor, a click opened a 256px tile instead of panning,
  and they joined the ‹ › sequence. It has to be a **container check at click
  time, not a selector** — tiles are created and destroyed as the reader pans.
  **`.champions-table img` is excluded too**, and that one was live: all 422
  avatars were treated as content images, making a 422-face gallery. That one
  *is* a selector, since those images all exist when the script runs.

- **`/sitemap/` is an HTML page for readers, derived from the content tree** —
  not Hugo's `/sitemap.xml`. The footer linked it from every page and nothing
  answered the URL. Nesting comes from sorting on `.File.Path`, since a
  lexicographic path sort is already tree order.

  **Articles and Authors are sortable, filterable TABLES; pages, pedia and
  sponsors stay link lists** — the split is whether an entry has FIELDS worth
  comparing. Four rules: **the HTML is sorted and the JavaScript only adds the
  buttons** (so without JS there is no dead control); **a cell sorts on
  `data-sort-value`, never on what it shows** (a timestamp behind a date, a raw
  integer behind "68,330"); **every cell reuses the site's own partial** for
  what it shows, so this page cannot credit a post differently from a card;
  and **`table-filter.js` is generic**, with `data-filter-cols` limiting which
  columns are searched and the haystack cached on first keystroke rather than
  rendered into a per-row attribute.

  Three silent CSS traps: `.sitemap-table td` is class-plus-type, so it
  outranks a single class; a filtered-out `<tr>` needs
  `display: none !important`, because `display: table-row` beats `[hidden]`;
  and the sticky header only sticks above 60rem, since an overflow container
  becomes the positioning context.

- **The calendar has two sources, split on "does it publish a feed?"** JUG
  meetups sync daily into `data/jug-events.json`; a conference has no feed, so
  those are **hand-added, one file per event**, `data/events/<slug>.yaml`.
  `template/event.yaml` is the schema and `Frontmatter.checkEvents` the check.
  Three things: **the generated file is `jug-events.json`, not `events.json`**,
  because Hugo merges `data/events.json` and `data/events/` into one key;
  **one file per event, never a shared list**, so two contributors never touch
  the same bytes; and **nothing has to be deleted**, since the layout drops an
  event the day after it ends. `checkEvents` enforces a **closed key set** —
  data files are not content, so Hugo says nothing about `website:` instead of
  `url:`, and the event just renders with a piece missing.

- **`partials/calendar-events.html` is the single definition of what an event
  is.** It flattens both sources, enriches each event with what the templates
  and the JavaScript need, and is `partialCached`, so `/calendar/` and the home
  page band cannot disagree about a date, a place or a colour.

- **The home page's "next two weeks" band is a WINDOW, not a top-N.** The
  filter is an **overlap** (`end >= today` and `start <= today+14`), because a
  five-day conference that opened yesterday is still on. It renders **nothing**
  when the window is empty — a quiet fortnight is a reason to show no section.

- **Scheduling a post is a future `date:`, and nothing else.** `buildFuture` is
  false, so a future-dated post is skipped entirely — no page, no entry in any
  list, feed, sitemap or search index. That is already the right semantics, so
  there is no `scheduled:` flag and no `draft:` mechanism; publishing is a
  maintainer moving `draft/<slug>/` into `content/posts/<y>/<m>/<d>/`.

  Four pieces make it work, three because the default breaks something:
  1. **`partials/coming-soon.html` reads pending posts OFF DISK**, since no
     template can `range` a page that was never built. It is the home page
     aside widget: four directory listings per build, not a walk of 2163 posts.
     Titles are **plain text, not links** — the page does not exist yet.
     `.IsHome` is checked in `sidebar.html`, which also bounds the cost.
     **`buildFuture = true` is the trap**: it would need a `where .Date "lt"
     now` in every list, term, feed and JSON-LD template plus the Pagefind
     body, and ONE missed spot publishes an embargoed article early, silently.
  2. **`BuiltSite.java` would otherwise BLOCK THE DEPLOY**, since it asserts a
     built page for every source — so scheduling a post would take the site
     down from the moment it merged. `isScheduled()` skips future-dated
     sources, and the count is printed next to the built one.
  3. **`Frontmatter.checkPostDates` enforces two rules.** A post's `date:` must
     MATCH its folder — the folder is the visible half but the `date:` is what
     Hugo publishes off, so a draft moved into October with August still in
     frontmatter goes live immediately, filed eight weeks back where nobody
     sees it, every check green. And a future-dated post carries **no time**.
  4. **`publish-scheduled.yml` is what causes the build.**

  **The publish time is not the author's to choose.** Everything goes out on
  the one daily build, so a scheduled post is dated `YYYY-MM-DD` — midnight
  UTC, giving that build seven hours of slack. Actions cron is best-effort and
  never fires early, so a time cannot make a post publish sooner and can only
  make it LATER: `09:00` is still in the future at the 07:00 build, so the post
  misses its morning and lands at whatever deploy happens next. Hence a hard PR
  failure. **Don't move the cron earlier to make publication "more punctual"** —
  it cannot be, and the slack is what stops a late run publishing on the wrong
  date.

  **The dispatch window starts at the LAST SUCCESSFUL deploy, not 24 hours
  ago**, which is what makes it self-healing: a skipped run still reaches back
  past the post it missed, and a 03:00 human push means the window correctly
  contains nothing. Bounded to 14 days, and it **says out loud** when nothing
  is due, because "nothing was due" and "the scan is broken" are otherwise the
  same green run.

  Two things worth not rediscovering: **a frontmatter reader must stop at the
  closing `---`** (one post has eleven `date:` lines in its body at column 0),
  and **YAML erases the distinction the no-time rule polices**, so the check
  reads the RAW line.

- **`/calendar/` is two views of its events, and only one is content.** The
  **agenda is server-rendered and the month grid is not**: the agenda is real
  HTML, so it is what Pagefind indexes and what a reader without JavaScript
  gets, while the grid is built from a JSON island. The whole toolbar starts
  `hidden` and the script switches it on, and a narrow screen opens on the
  agenda. Times are formatted **in the template, in each event's own UTC
  offset** — rebuilding them in the browser would restate every event in the
  reader's timezone. Each JUG's colour is a hue hashed from its slug.

  **A multi-day event occupies every day it runs**, via a `days` array, so a
  conference is a band rather than one chip on the Monday — and that is why
  `days` exists instead of the script reading `date`/`endDate`, since a few
  upstream entries carry an end a week after their start.

  **The calendar has no past, so neither does the grid.** The back arrow stops
  at the current month, and the current month begins at the Monday of today's
  week — a **week**, not the day, because the columns are weekdays. Forward has
  no limit. **The month arrows mean different things in the two views**: in the
  grid they step a month, in the list they step to the next month that *has*
  events, since the agenda has no section for an empty one.

  **The toolbar state is in the URL** — `?month=YYYY-MM&view=grid|list`, so a
  month and view can be shared as a link. Both arrows, Today and the view
  buttons write it; a load with either parameter reads it back, clamped to the
  current month. `view=month` is accepted as a synonym for `grid`, since that
  is the button's label. **`replaceState`, not push**: the arrows are a
  scrubber, and Back should leave the page rather than walk months. A clean
  load writes nothing, so merely opening `/calendar/` never stamps a URL.

- **A multi-page series is a folder of pages with a `weight`, and nothing
  else.** `partials/series-steps.html` is the single definition; the progress
  bar and the previous/next cards derive everything from it. Both render
  **nothing** when the page has no weight, which is load-bearing: the five
  `install-java/` pages are alternatives, not a sequence, so they correctly get
  no navigation. `checkSeriesWeights` fails the PR on two pages sharing a
  weight, since Hugo's stable sort would silently mis-order them.

- **`/java-quick-start/other-tutorials/` is tiles from a frontmatter list.** It
  deliberately does not copy the board's folder-of-pages mechanism: every entry
  exists to send the reader elsewhere, so a page each would mint a URL, a view
  key and a file to hold four fields.

- **The palette is the LIVE SITE'S, read out of its own CSS.** The scaffold's
  "warm amber accent" was simply wrong — there is no amber token in that
  stylesheet at all. **Sample the logo, don't trust a description of it**:
  `foojay-logo.png` is 97,620px of exactly `#50c2f7`.

  Three load-bearing decisions: **a bright blue cannot be both the accent and
  accent-TEXT** (`#50c2f7` is 2.02:1 on white, so it is a fill and never text;
  `--brand-accent-strong` is the same hue at 4.75) — **realigning hues to the
  live design is the point, importing its contrast failures is not**; **the
  accent is the one brand fill that does not change between schemes**; and
  **`--on-accent`/`--on-accent-strong`/`--on-ink` exist because a fill that
  inverts cannot carry a literal label colour**. Category chips are green
  because a tag is green upstream, and `--brand-secondary` is the LINK colour.
  **Sponsor tiers were untouched**, so amber correctly survives where it means
  "gold" — a grep for warm hex will find them and they are not leftovers.

- **`.highlight-panel` is the single definition of "this block is the one to
  look at"**, used by the home page lead and the featured-author band. It is a
  class, not a selector list, because the site has ONE highlight treatment.
  **`--ink-muted` is redefined INSIDE the panel** — the token is 4.39:1 on the
  tinted ground and a dozen descendants use it, so stepping it once fixes all
  of them where naming each would be a dozen rules a new descendant misses.

- **The home page spotlights a DAY, not a post.** `index.html` takes every post
  sharing the newest post's publish DATE, because posts on one day cannot be
  ranked — a scheduled post carries no time, so a day's articles land at the
  same instant and Hugo's sort falls through to the file path. **523 of 1497
  publishing days carry more than one post.** Four details: the day comes from
  the **newest post, never `now`** (foojay does not publish daily); the
  selection is a **loop with a `break`, not a `where`**, since `where` cannot
  compare dates a calendar day at a time across UTC offsets; the grid below is
  `after (len $leads)`; and the panel grid is `auto-fit` where `.post-grid` is
  `auto-fill`. **Equal cards rather than one big card plus siblings** — keeping
  an anchor keeps the arbitrary pick, which is the bug.

- **The home page's "Getting started with Java" band is the QUICK START TRAIL,
  not posts** — a category is not an editorial decision. It was `first 4` of
  "Java Beginner", which authors reach for with anything approachable, so the
  four on show changed with whatever was published last and none of them
  answered "I don't have Java yet, where do I start?".

- **The quick start trail is described ONCE, in the pages themselves.** Three
  places render it, and each Markdown page also used to write its own list by
  hand — 38 links, all absolute `https://foojay.io/…` URLs, with titles that
  had already drifted. Now `partials/quick-start-pages.html` finds the children
  of a hub by `File.Dir`, uses **weight if every child has one, `Title`
  otherwise** (all-or-nothing, because a partly weighted folder is a mistake to
  notice), and each row's gloss is the target page's own `description:`. Two
  traps: **strip leading zeros before `int`**, since Hugo's `int` reads "08" as
  octal and FAILS THE WHOLE BUILD; and **a template cannot build an element
  NAME**, so `<{{ $tag }}>` is escaped to visible text.

- **A page must never list its own `url:` in `aliases:`**, and
  `checkSelfAliases` fails the PR. 29 pages carried one, and on two it had
  taken the page off the air — Hugo wrote the redirect stub OVER the real page,
  so `/java-quick-start/` served nothing but a refresh pointing at itself. The
  other 27 happened to win the race, which is exactly why this is a check and
  not a one-time cleanup.

- **The logo file is cropped to its artwork and sized in CSS.** A 1500x500
  export whose mark occupied 1022x352 meant the padding was a silent 30%
  shrink, and every attempt to make the logo bigger fought the file. Re-export
  a padded PNG and it shrinks again with nothing in the templates to show why.

- **The footer navigation is `[[menu.footer]]` in `hugo.toml`, not markup.** It
  was hardcoded, which made it a second definition of the site's navigation
  with nothing tying it to the first, and it had drifted exactly that way.
  Three things stay in the template because none is a link an editor picks: the
  brand blurb, `now.Year`, and the **RSS URL** (resolved from
  `site.Home.OutputFormats`, since the feed path is Hugo's to decide). Two
  traps: **Hugo lowercases config keys**, so `[[menu.footerLegal]]` resolves to
  nothing with no error — keep footer menu names all-lowercase; and `relURL`
  **does** normalise a trailing slash, so verify a claim like that against
  built HTML, not the template.

- **`/team/` and `/community-support/` are gone, and both taught the same
  lesson.** `/team/` was the web agency's profile, not foojay's team; its
  aliases moved to `meet-the-team.md` and its view key was remapped.
  `/community-support/` was an alias of `/our-sponsors/` that the scraper had
  followed and stored as a page — and its `url:` collided with the section's
  own `aliases:` entry, **which Hugo does not warn about**, so the stub built
  into no page at all while still appearing in both sitemaps. Check for a `url:`
  collision when a page renders as nothing.

- **The header search field is collapsed to its magnifier, and the button is a
  real submit** — so the form still works with JavaScript off. Three details:
  the collapsed input is `visibility: hidden`, not just `opacity: 0`, or it
  stays a tab stop; that visibility is transitioned with a delay when closing
  but `0s` when opening, because **`focus()` on a `visibility: hidden` element
  does nothing**; and the mobile drawer copy is excluded from both the CSS and
  the JS filter.

- **WordPress's placeholder title/description are not metadata — replace them
  on sight.** Yoast served one title and one description for any page it had
  nothing for, so 7 pages shared an `<h1>` and 8 shared an `og:description`.
  `.Title` is the page's H1 *and* half its `<title>`, so a placeholder is
  visible twice on every affected page.

- **Sponsors appear site-wide via the sidebar**, tier-ordered with the logo
  sized by tier. Deliberately NOT subject to the TOC-height cull that drops the
  other widgets on long posts — sponsor visibility is contractual.

- **Sponsor folders can be renamed; the WP URL still has to work.** Each bundle
  records `wpSlug:`, and an `aliases:` entry is emitted when folder and
  `wpSlug` differ. Never hand-edit those two fields.

- **Sponsors ↔ articles is an author list, hand-maintained.** Each sponsor
  bundle carries `authors:`, and `partials/sponsor-posts.html` is the single
  definition — the grid, the counts and "Topics covered" all derive from it, so
  nothing goes stale. Our numbers legitimately differ from WordPress's, because
  author-based attribution is broader; narrow the list if a sponsor should own
  fewer posts.

  **An entry can be date-bounded, because people change employer** — either a
  bare slug or a map with `from:`/`till:`:

  ```yaml
  authors:
    - "tim-kelly"                 # still there: nothing to say
    - slug: "pratik-patel"
      till: "2026-04-01"          # the day they left
  ```

  The bare string stays the default because it is the common case. Four things
  are load-bearing: the
  range is **half-open `[from, till)`**, so the same date is correct for both
  employers with no gap and no double-attribution; **a post counts when it is
  inside the window of AT LEAST ONE author**, so a co-written piece is still
  the sponsor's; **dates go through `time.Format` before comparison**, since
  YAML turns an unquoted date into an object and a quoted one into a string and
  comparing the two silently fails; and the entry key set is **closed**, since
  `until:` for `till:` would be ignored with nothing saying why.

- **Featured Authors are two slugs in `hugo.toml`, and nothing else.** The pick
  rotates monthly and lives in an editor's head — case 3 of the rule.
  Everything on display is derived. **Don't add a `featured: true` frontmatter
  flag**: that is two files to edit per rotation and two to remember to unset,
  which is how an author silently stays featured for ever.

- **The category index lives at `/today/category/`, next to its terms.** Hugo
  puts a taxonomy's list page at `/categories/` while the permalink puts every
  term one level down, so the index was orphaned from its own children and both
  the sidebar and the home page band pointed at `/today/` instead. Rendered
  alphabetically with a filter box, since the top categories are already shown
  elsewhere and the only reason to open a list of 123 is to find one.

- **A pedia entry is: prose, then optional "More reading on Foojay:", then
  `## See Also`.** The two blocks mean different things — More reading links
  ARTICLES, See Also links other GLOSSARY TERMS. Headings start at `##`. The
  See Also lists are **derived in three tiers** (the entry's own links, then
  entries linking *to* it, then its topical group, only to top a short list up
  to three), capped at six, and root-relative. **A pedia image lives in
  `static/images/pedia/<slug>/`, never hotlinked** — entries are single files,
  not bundles, so there is no folder to co-locate a resource in.

- **The Advisory Board is a folder, not a list.** Each member is a page at
  `/board/<slug>/`, and `partials/board-members.html` is the single definition
  — the grid, the organisation count and the "other members" strip all derive
  from it. Adding a member is one file plus a logo. It stays a landing page
  under `content/pages/` rather than becoming a section, because the page has
  58,754 WordPress views under the `pages/board` key and a section would have
  renamed it. Logos carry `logoBackground` for the same reason sponsor logos
  do.

- **The AI portal is a derived category landing page.** `/ai/` is what
  WordPress serves as the "Machine Learning" category with an editorial intro,
  so the page file holds the intro plus `list_category:` and the layout derives
  the rest. Don't turn it into a hand-picked `posts:` list. Its grid is **not**
  paginated (`.Paginate` doesn't work on a page kind), and its cards pass
  `maxCategories: 0`, because on a portal the chips are the navigation.

- **`/search/` gives each SECTION its own quota, never a global top-N.**
  Pagefind returns one relevance-ordered list, so a global `slice(0, 30)` is
  spent on whichever section matches most: on "pi4j", 328 pages matched and the
  30 rows rendered were 29 posts and one author — **the two authors whose bio
  says Pi4J were unreachable**. Each section now shows its first 10 with a Show
  more button and its OWN total in the heading. Four load-bearing details:
  **`pagefind.filters()` must be awaited once** before per-section counts
  exist; **the quota costs one filtered search per section that HAS a hit**;
  **a folded group is re-merged on `.score`**; and **posts are re-sorted
  newest-first over the WHOLE group on every render**, so a Show more batch
  can't interleave. Two guards for silent failures: a `runId` per query, and a
  fallback to one ungrouped list if the counts don't add up.

- **Paid home page banners are page bundles under `content/ads/`**, one per
  campaign, so a banner's creative sits next to its copy (`template/ad.md` is
  the schema, `Frontmatter.checkAds` the check). It is a **campaign** model,
  not a sponsor one — one sponsor runs many banners a year, each with its own
  creative and date window — which is why this is not in the sponsor bundles.
  Three settings in `content/ads/_index.md` keep a banner from becoming a page,
  all verified against a real build: **`render: never`** (no URL, no sitemap
  entry, nothing for Pagefind), **`publishResources`** (without it `render:
  never` takes the creative with it and every slide silently loses its
  picture), and **`list: local`**. `expiryDate:` retires a campaign on its own;
  `sponsored: false` is for foojay's own promotions, which carry no "Sponsored
  Content" label.

- **A post's comment thread is keyed on its slug, never its pathname.** giscus
  is configured `data-mapping="specific"` with the slug as the term — pathname
  mapping was the tempting default and a trap, since the trial served a
  different path and every thread would have been orphaned at cutover.
  `data-strict="1"` goes with it: non-strict is a fuzzy title search taking the
  first hit, and **30 foojay slugs are substrings of another slug**. Change any
  of mapping, term or strict and a mismatch shows up as an empty comment
  section, not an error.

  **The legacy WordPress comments are NOT in Discussions and must not be put
  there** — importing them got the posting account **banned by GitHub** a few
  posts in. They are `content/posts/**/comments.json`, rendered under the
  widget. Only a handful of real discussions exist, which is correct.

- **The read counter is ours, first-party, and keyed `<section>/<slug>`.**
  Three reasons, all of which have to hold: **privacy by construction** (the
  Worker receives a slug and stores a slug and an integer — nothing to
  anonymise); **first-party, so the number is true** (a third-party analytics
  domain is blocked for a large share of an audience of Java developers, and
  the count would silently run tens of percent low — the one failure a
  *published* number can't have); and **one number, not two** (WordPress's
  count is the `legacy` column, live views accumulate in `live`, `/all`
  returns the sum, so no template adds them and there is no `legacy_views:`
  field).

  **Counting and displaying are separate.** `views-beacon.html` posts the slug
  with `sendBeacon` and renders nothing unless `[params.views] endpoint` is
  set, so a local build never counts. The number shown is baked into the HTML
  from `data/views.json` — no JavaScript, and it works on cards, which a
  per-page fetch cannot. Up-to-a-day-stale is not a defect in a view count.

  **`partials/views-key.html` is the single definition of what gets counted and
  under which key**, followed by the display, the beacon and the import; drift
  between them shows up as a number that silently stays at zero. The Worker
  validates the key's *shape* rather than an allow-list, so a forgotten
  redeploy can't drop a whole section. **A combined total is
  `views-total.html`**, a build-time sum, so an author's total cannot disagree
  with the cards below it. **Author pages have no WordPress baseline** and
  started at zero — don't go looking for the import that "must have failed".

- **Third-party analytics is the GTM container `GTM-M6ZT5NW`.**
  `partials/analytics.html` (head, as high as possible) and
  `partials/analytics-noscript.html` (straight after `<body>`) are the only
  places the site asks a browser to run someone else's code. Both render
  nothing unless `[params.analytics] gtm` is set, so a fork or a local build
  reports nothing.

  **The GA4 id appears nowhere, and a grep will tell you the site has no
  analytics.** The container fires Universal Analytics tags on `UA-726113-5`,
  which Google resolves **server side** to `G-GS21L12HYK` through its
  UA-to-GA4 connected site tag. Verified in a browser: the hit is
  `/j/collect` with `tid=UA-726113-5` and a `gjid`, which is that bridge.

  **Loading GA4 directly instead is what broke analytics at cutover** — it
  needed Consent Mode defaults, Ketch's `googletag` plugin is present but
  empty on this property, so nothing ever sent the update and every hit went
  out `gcs=G100`. There are **no `gtag('consent', …)` defaults now**, and
  azul.com has none either; gating is Ketch's own, not Consent Mode's.
  Restoring the container fixed it, **confirmed live in GA on 2026-09-23**.
  `CUTOVER.md`'s "Analytics fires" has the full account.

  **The container also injects Ketch**, so `[params.analytics.ketch]` is
  commented out — setting it loads Ketch twice. Worth fixing the other way
  round: azul.com loads Ketch from the page, first thing in `<head>`, ahead of
  its container, and its container carries no Ketch tag (checked). Delete the
  Custom HTML tag in the GTM console, then uncomment.

  **A comment meant to reach view-source must go through `printf | safeHTML`**
  — Go's `html/template` strips HTML comments out of a template.

  **GA4 and `data/views.json` will never agree, and GA4 is always lower. That
  is not a bug in either.** Google's collection domains are on Firefox's
  Enhanced Tracking Protection blocklist, strict by default in private windows,
  and every adblocker does the same — observed live, not assumed. The read
  counter is first-party and indistinguishable from the rest of the site. The
  two count different populations: **don't reconcile them, don't swap the view
  counts for GA4 numbers because GA4 looks more authoritative, and don't treat
  a growing gap as drift.** Also: a consent test in a Firefox private window
  measures ETP, not consent.

- **Posts are contributed via PR** (see `CONTRIBUTING.md`); the repo is public.
