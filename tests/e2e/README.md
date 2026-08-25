# `tests/e2e/` — the browser half of the deploy gate

Everything else that checks this site is static: `validate/Frontmatter.java`
reads `content/`, `validate/BuiltSite.java` reads `public/`. Neither can see the
parts of foojay that **only exist once JavaScript runs** — search results,
the two world maps, the lightbox, the sortable sitemap tables, syntax
highlighting, mermaid diagrams. All of those fail *silently*: the page still
returns 200, still has its content, and simply stops doing the thing.

So these run a real browser over the built site, in
`.github/workflows/build-deploy.yml`, **between the build and the deploy**.

```bash
hugo --gc --minify
npx -y pagefind --site public      # search has no index without this
npm ci && npx playwright install chromium
npm run test:e2e
```

`npm run test:e2e -- --ui` for the interactive runner, `--grep search` for one
file, `--headed` to watch it.

If `npx playwright install chromium` cannot finish on your machine (a truncated
download leaves a `chromium_headless_shell-*` folder missing or a few hundred KB
short, and every test then fails with "Executable doesn't exist"), the suite
runs unchanged against a locally installed Chrome — add
`use: { channel: 'chrome' }` on top of this config in a throwaway config file
and point `--config` at it. Same 37 tests, same result; CI always uses the
pinned browser.

## The "staging environment" is localhost

GitHub Pages has no staging slot, and this needs none. `server.mjs` serves the
built `public/` on 127.0.0.1, reachable only from the workflow run — nothing to
provision, nothing public, nothing to clean up.

It is written by hand rather than reaching for `npx serve` because the point is
to behave like **GitHub Pages specifically**: pretty URLs resolve to
`index.html`, a directory without a trailing slash 301s to one, a miss serves
`404.html` with a 404 status, media answers a `Range` request, and everything
lives under the base path — so a link that escapes `/website/` fails here
exactly as it would in production. A generic static server has its own opinions
about all five.

The base path is read from the home page's own `canonical`, the same way
`BuiltSite.java` derives it, so `/website/` and `/` both work with nothing to
change at cutover.

**Nothing here is resolved against the working directory.** `site.mjs` derives
the repo root from its own file location, because Playwright runs
`webServer.command` with the cwd *it* chooses — the config's directory, not the
repo root — so a relative `node tests/e2e/server.mjs` became
`tests/e2e/tests/e2e/server.mjs` and the server never started. It failed only in
CI, for a reason worth knowing: `reuseExistingServer: !process.env.CI` is the
default advice, and a server left running from an earlier local session was
silently reused on every run, so the launch path CI actually takes was never
exercised once. It is `reuseExistingServer: false` now — a fraction of a second
per run to make the local path and the CI path the same path.

## Which pages get tested is derived, not listed

`discover.mjs` scans the build for what it actually contains: the first post
with a code block, the first with a gallery, the first with an embedded video,
the first author profile, the self-hosted media files. A hardcoded
"the post with the diagram is `/today/foo/`" rots the moment that post is
renamed — and it rots into a test that still *passes*, because the page still
loads and simply has no diagram on it.

First match in sorted order, so two runs of one build test the same pages.

A feature the build contains **none** of resolves to `null` and its test skips
with a reason. `mermaid` is `null` today: the library is vendored and the render
hook is wired, but no published post uses a ```` ```mermaid ```` fence yet. The
day one does, that test starts running on its own.

## Third parties are stubbed, never fetched

438 posts embed a YouTube player, 19 embed Vimeo, and both world maps draw
OpenStreetMap tiles. Letting the suite load them would make it slow, make it
fail when someone else's CDN has a bad afternoon, and fire a request at YouTube
for every build. They are answered locally with an empty body of the right type,
so the page still lays out and the `<iframe>` is still there to assert on.

**Watch what stubbing hides.** The webfonts and the map library were on this list
too, and an empty body is a *plausible* response for both — so the suite was
quietly asserting nothing about the typeface the site renders in, and skipping
the map tests entirely. Both are served from the repo now and exercised for
real, which is what "the webfonts are served by us, and actually load" and the
two map tests are. When something moves onto this list, ask what assertion it
takes with it.

This is why **there are no retries**. Nothing here depends on the network, so a
failure that comes and goes is a real bug in the page, not weather — and a retry
would hide exactly the flake worth knowing about.

## What "does the video play" honestly means

Two different questions, and only one belongs in a gate:

- **A third-party embed is not ours to test.** Asserting a YouTube player
  reaches `playing` is asserting that YouTube is up, on a check that blocks our
  deploy. So an embed is checked *structurally*: it survived the markdown
  pipeline, it is inside the article, it has an absolute `https` source.
- **A file we serve is ours to test, all the way to decoding.** The one
  self-hosted `.mp4` is fetched, range-requested, handed to a real `<video>` and
  waited on until `canplay` with a duration. Existence is not playability —
  `cleanup/images.py` learned that when a killed encode left a 0-byte file that
  every "does it exist" check called fine.

## What skips, and why

A skip here is a statement about the build, not a disabled test. One today, out
of 38:

| skipped | why | what would un-skip it |
| --- | --- | --- |
| mermaid renders to SVG | no published post uses a ```` ```mermaid ```` fence yet | the `draft/` post about mermaid landing |

**The three map skips are gone, and how they went is the lesson.** Both world
maps and the lightbox's tile exclusion used to skip for one reason: Leaflet and
markercluster came from unpkg.com, and nothing here reaches the network. So the
assertion that mattered — that grouping 90 JUGs or 422 champions actually
produces markers — had never once run. The skips were not a gap in the tests but
a report on a live fault: `cluster-map.js` guards on `!window.L` and returns, so
whenever unpkg was unreachable **the map silently disappeared and the page still
rendered clean** — no error, no message, a gap where the map was. Vendoring both
libraries (`themes/foojay/static/vendor/`, see `partials/cluster-map.html`)
removed the dependency and turned three skips into three real tests, and
`typeof window.L` is now asserted rather than skipped on, because a same-origin
library that is missing is a broken path in this repo.

Only the map **tiles** are third-party now, and they cannot be anything else —
OpenStreetMap's tiles *are* the map. They are stubbed like every other third
party, so the tests exercise markers, clusters and popups over an empty ground,
which is also exactly what a reader gets when a tile fails to load.

The video test also *annotates* rather than fails when the browser cannot decode
a codec: Playwright ships the open-source Chromium build, which carries no
proprietary codecs, and the one self-hosted file is H.264. That is told apart
from a broken file by the media error code — `MEDIA_ERR_SRC_NOT_SUPPORTED` (4)
is the browser's limitation, while a network (2) or decode (3) error is ours and
fails. Asking "can this browser play any video at all" is the wrong question and
answers yes: `canPlayType` returns `""` for avc1 and `"probably"` for vp9.

## Two things measured, worth not re-discovering

- **Pagefind's matching is fuzzy enough that nonsense still matches.**
  `qqzzxxjjvvww` returns 1 result and a random `xqjvbzkwqpfmdlrn` returns **214**.
  Only several nonsense tokens ANDed together produce a genuinely empty result
  set, which is why the empty-state test queries `qqq zzz xxx jjj vvv`.
- **A `loading="lazy"` image below the fold has no box, and an element with no
  box never becomes clickable.** The gallery test scrolls and waits for the
  decode before clicking — the order a reader does it in — and the broken-image
  sweep walks the page first, or it would pass by never looking.

## Files

| file | what it is |
| --- | --- |
| `site.mjs` | where `public/` is and what base path it is served at |
| `server.mjs` | the GitHub-Pages-shaped static server |
| `discover.mjs` | picks representative pages out of the build |
| `fixtures.mjs` | stubs third parties, collects uncaught errors and failed same-origin requests |
| `pages.spec.mjs` | one page of every kind renders cleanly; nav, 404, theme toggle |
| `search.spec.mjs` | Pagefind: grouping, per-section counts, Show more, empty state, the header box |
| `media.spec.mjs` | self-hosted video decodes; embeds survive; no broken same-origin images |
| `interactive.spec.mjs` | highlighting, lightbox, both maps, sitemap tables, mermaid, the view beacon |

## The gate must not count itself

`[params.views] endpoint` is set, so `views-beacon.html` fires a real beacon at
`foojay.io/api/views` on every page. A browser walking 20 pages on every deploy
would add 20 reads to the numbers **printed on the site** — the one measurement
here that is published. Two layers stop it: third-party routes are stubbed, and
Chromium is launched with `--host-resolver-rules=MAP * ~NOTFOUND, EXCLUDE
127.0.0.1`, so DNS fails for everything but localhost below the level any page
script can reach. The second exists because the first is a Playwright-level
promise and `sendBeacon` is exactly the request shape one would not want to be
wrong about.
