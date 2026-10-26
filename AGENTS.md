# Project context for Claude and other LLM coding tools

foojay.io is a static Hugo site, built from this repo and deployed to GitHub
Pages behind Cloudflare. It replaced the WordPress site at **cutover on
2026-09-22**. If you're picking this up fresh, read this before making changes.

`REDIRECTS.md` documents the live Cloudflare redirect rules that carry old
URLs to their Hugo equivalents.

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

### Data fetchers, hand-run scripts and generated data

See `scripts/fetch/AGENTS.md`. All fetchers run daily from
`sync-external-content.yml`, commit their result, then dispatch a deploy.
**Never hand-edit a generated file under `data/`.**

### The read counter

- **`worker/views/`** is a Cloudflare Worker over a D1 table of
  `<section>/<slug> -> (legacy, live)`, routed at `foojay.io/api/views/*`.
  Deployed by hand (`wrangler deploy`), never by CI, because it writes outside
  the repo and needs a credential. Deployed and seeded on 2026-08-24 by IT, so
  `wrangler.toml`'s `database_id` is still the `REPLACE_WITH_D1_DATABASE_ID`
  placeholder and a deploy from here would not work until it is filled in.
  See `worker/views/README.md`, and the read-counter convention below for why
  this exists rather than a hosted analytics service.

### Slack announcements

- **`notify/SlackPosts.java`** posts from the site's own RSS feed through two
  incoming webhooks (secrets `SLACK_WEBHOOK_CONTENT`, `SLACK_WEBHOOK_GENERAL`);
  unset, it prints and exits 0. **"New" is a diff, not remembered state**:
  `build-deploy.yml` saves the live feed before deploying and the `announce`
  job compares it with the new build's, so a scheduled post is announced when it
  goes live. An empty previous feed skips rather than announcing everything.
  `slack-weekly.yml` posts the previous Monday-to-Sunday week every Monday.

### Validators

See `scripts/validate/AGENTS.md` (`Frontmatter.java`, `BuiltSite.java`) and
`tests/e2e/AGENTS.md` (the browser half of the deploy gate).

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
  ~136 MB of headroom. An image a PR adds or changes over 2 MB fails the PR check (4 MB for what is already there).

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
  engines discard), and **3 regexes are Cloudflare config** in `REDIRECTS.md` —
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

See `themes/foojay/AGENTS.md`, which also holds the template- and
JS-level notes for the sections below.

### Sections and features

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

- **Scheduling a post is a future `date:`, and nothing else.** `buildFuture` is
  false, so a future-dated post is skipped entirely — no page, no entry in any
  list, feed, sitemap or search index. That is already the right semantics, so
  there is no `scheduled:` flag and no `draft:` mechanism; publishing is a
  maintainer moving `draft/<slug>/` into `content/posts/<y>/<m>/<d>/`.

  Four pieces make it work, three because the default breaks something:
  1. **`partials/scheduled-posts.html` reads pending posts OFF DISK** (the home
     widget `coming-soon.html` and `/today/coming-soon/` both render its list), since no
     template can `range` a page that was never built. It is the home page
     aside widget: four directory listings per build, not a walk of 2163 posts.
     Titles are **plain text, not links** — the page does not exist yet.
     `.IsHome` is checked in `sidebar.html`, which also bounds the cost.
     The byline is **`byline.html` given slugs instead of a Page** (that input
     exists for this caller), so faces and names here cannot look or resolve
     differently from a card's.
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

- **A page must never list its own `url:` in `aliases:`**, and
  `checkSelfAliases` fails the PR. 29 pages carried one, and on two it had
  taken the page off the air — Hugo wrote the redirect stub OVER the real page,
  so `/java-quick-start/` served nothing but a refresh pointing at itself. The
  other 27 happened to win the race, which is exactly why this is a check and
  not a one-time cleanup.

- **`/team/` and `/community-support/` are gone, and both taught the same
  lesson.** `/team/` was the web agency's profile, not foojay's team; its
  aliases moved to `meet-the-team.md` and its view key was remapped.
  `/community-support/` was an alias of `/our-sponsors/` that the scraper had
  followed and stored as a page — and its `url:` collided with the section's
  own `aliases:` entry, **which Hugo does not warn about**, so the stub built
  into no page at all while still appearing in both sitemaps. Check for a `url:`
  collision when a page renders as nothing.

- **WordPress's placeholder title/description are not metadata — replace them
  on sight.** Yoast served one title and one description for any page it had
  nothing for, so 7 pages shared an `<h1>` and 8 shared an `og:description`.
  `.Title` is the page's H1 *and* half its `<title>`, so a placeholder is
  visible twice on every affected page.

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
