# TODO

## Still TODO

* [ ] Visualization of events/calendar

## Finished

### ConverPosts

* [X] Search for `[\[email protected\]](/cdn-cgi/l/email-protection)`
  * Not a conversion bug of ours: foojay.io runs behind Cloudflare with **Email
    Address Obfuscation** on, so an address never reaches a non-browser client.
    The HTML carries a placeholder plus an XOR-encoded copy, and a script in the
    *reader's* browser swaps them back. The scrapers run no JavaScript, so the
    literal "[email protected]" is what landed in content/ -- 293 occurrences
    across 161 files.
  * Cloudflare matches a loose `x@y`, so it also mangled things that only look
    like addresses, **inside code**: `git@github.com:...` in a clone command,
    every line of `java --list-modules` output (`javafx.base@14.0.2`),
    `setup-java@v5.5.0`, and one `<code>@name</code>`.
  * Fixed in two places, the usual split:
    * `HtmlToMarkdown.decodeCloudflareEmails` undoes it at conversion time, so a
      re-scrape is correct. The encoding is reversible (first hex byte is the XOR
      key), so nothing is guessed. Anything that decodes to a non-address is
      restored as text, never linked.
    * `scripts/cleanup/CloudflareEmails.java` repaired what was already in content/.
      Unlike the other migrations it cannot repair from what it has -- the stored
      files kept only the placeholder -- so it re-fetches each page and reads the
      addresses back out, and only writes when the placeholder count matches the
      live page's, so the n-th placeholder provably pairs with the n-th address.
      148 files, 279 addresses, including `links@jfx-central.com` on every JFX
      Links post.
  * Two things left deliberately:
    * `content/pages/terms-of-use.md` has `[info@azul.com](mailto:info@azul.io)`
      -- that mismatch is in the WordPress source, faithfully reproduced. Fix it
      there (or here) if `azul.io` is a typo.
    * `.../best-practices-for-working-with-ai-agents-...` really does contain the
      words "[email protected]" in two prompt examples; the live page has the
      same literal, so it is the author's own text, not damage.
* [X] Em dashes became `---` in the conversion — real fix was for GitHub comments
  * Flexmark's html2md converter rewrites the characters WP serves (`—` → `---`,
    `–` → `--`, `…` → `...`). **But Hugo turns them back**: Goldmark's typographer
    extension is on by default and renders `---` as `&mdash;`, so the site was
    never showing a literal `---`. The earlier note here claiming otherwise was
    wrong.
  * Where it DID reach a reader is outside Hugo: GitHub Discussions have no
    typographer, so the comment import would have posted `Fair challenge --- JEP
    491` verbatim. Fixed at the source — `HtmlToMarkdown.CONVERTER_OPTIONS` now
    sets `TYPOGRAPHIC_SMARTS = false`, so every imported comment (and every future
    scrape) keeps the real character. `TYPOGRAPHIC_QUOTES` stays ON deliberately:
    ASCII quotes are easier to type and diff and render identically.
  * `content/` was normalized too (`cleanup/NormalizeMarkdown.java` pass 3: 2207 stand-ins
    in 499 files) — **cosmetic**, done so a re-scrape of an old post doesn't show
    a dash change on top of the real edits. Verified render-neutral: of 10,021
    built files every HTML page is byte-identical once `&mdash;` is decoded, and
    the 145 RSS feeds differ only in entity-vs-character form.
  * Left alone: ` -- ` (1185) and `...` (1782). The typographer renders them
    correctly, and an author really does type `--` (every long CLI flag written
    outside backticks), so rewriting the source would destroy intent while
    changing no page. A re-scrape fixes them properly.
  * Watch out when diffing builds in this repo: `sidebar.html` shuffles its author
    and JUG widgets per page, so 1483 files differ build-to-build with identical
    content. Exclude the `<aside class="sidebar">` block or you'll chase ghosts.
* [X] Image galleries -> `{{< gallery >}}` shortcode, migrated with `scripts/cleanup/GalleriesToShortcode.java`
* [X] Tags -> won't do as we have fixed list of categories

### Missing Content

* [X] log4j-cve.md
* [X] advisory board text -> `content/pages/board.md` (intro + grid) with one
  page per member under `content/pages/board/`, rendered by
  `themes/foojay/layouts/board/{list,single}.html`. WordPress hid all 19 in an
  accordion; splitting them gives each a linkable page. Adding a member is a
  copy of `template/board-member.md` plus a logo -- no list to update.
* [X] Foojay.io AI Portal -> `content/pages/ai.md` + `themes/foojay/layouts/ai/single.html`
  * On WordPress the page is the **"Machine Learning" category landing page**
    (WP category slug `ai-ml`, 66 posts) with an editorial intro on top -- not a
    hand-picked list. So `ai.md` holds the intro and one key,
    `list_category: "Machine Learning"`, and the layout derives everything else:
    lead card, the full article grid, the "Topics covered" chips and the count.
    A new AI post appears here by carrying the category, with nothing to update.
  * Our 66 matches WordPress's 66 exactly, same order.
  * Cards on this page show **every** category, not the usual first two --
    `post-card.html` now takes an optional `maxCategories` (0 = all). Verified
    render-neutral elsewhere: across 1188 pages that render cards, every card's
    markup is byte-identical to before.
  * Not paginated (Hugo's `.Paginate` only works on list pages, and a portal
    that shows all 66 beats one that hides two thirds); the category page it
    links to does paginate.

### Features

* [X] Read counter -> our own Cloudflare Worker + D1 on `foojay.io/api/views/*`
  * **Not a hosted analytics service**, and the reason goes past "not Google
    Analytics": the Worker receives a slug and stores a slug and an integer. No
    cookie, no identifier, no IP, no user agent — nothing collected, so nothing
    to anonymise. Being first-party also means no adblocker can tell it from the
    rest of the site, which the third-party alternatives (GoatCounter, Plausible,
    Cloudflare Analytics) can't say — their own docs open with "check if your
    adblocker is blocking us", and on an audience of Java developers that is not
    a rounding error. foojay.io is already on Cloudflare DNS, so the route needed
    no migration. Free tier: 100k requests/day against ~5–10k pageviews/day.
  * Displayed next to the date, byline and reading time (`11,459 views`) on the
    article **and on every card**, and on **pages, `/pedia/` entries and author
    profiles** too — page head, under the term, and on the author's profile and
    featured-author card. Baked into the HTML at build time from
    `data/views.json` rather than fetched client-side — no JS, no number that
    appears after paint, and cards can't be done any other way.
  * Keyed `<section>/<slug>` (`posts/some-article`, `pedia/bytecode`,
    `authors/jbellis`), never a pathname — the trial serves
    `/website/today/<slug>/` and production `/today/<slug>/`, so pathname keys
    would zero every count at cutover. The section half stops the four sections
    colliding in a permanent store. `partials/views-key.html` is the single
    definition: add a section there and it starts being counted, no other change
    and no Worker redeploy.
  * `scripts/transfer/LegacyViews.java` is the import. foojay.io runs the Post Views
    Counter plugin, which serves the numbers on an **open** REST route, so no WP
    admin, database or credential is needed — same posture as the comment import.
    **2210 entries, 13.8M views**: all 2145 posts (the 3 emoji slugs resolve
    through the same `sanitizeSlug` the conversion used), 35 pages and all 30
    pedia entries. The pedia glossary is a custom WP post type (`terminology`)
    that isn't in the REST API, so each entry's id is read out of its rendered
    page instead. The 7 items reported unmatched are WP listing pages (`today`,
    `author`, `sitemap`, `home-page`, …) with no single Hugo page behind them.
  * **Author pages have no WordPress baseline** — the plugin can count user
    archives but the option is off on foojay.io (verified: its user-views route
    returns 0 for every author checked). They start at zero when the Worker goes
    live, and the script says so on every run so it doesn't look like a bug.
  * Re-runnable until cutover, as asked: the WordPress number is the Worker's
    `legacy` column and live views accumulate in `live`, so seeding **sets** a
    new baseline instead of adding to one. Rejected a `legacy_views:` frontmatter
    field for this — it would rewrite 2145 content files on every catch-up run;
    `data/legacy-views.json` is one generated file, like `data/jugs.yaml`.
  * **Remaining: deploy the Worker** (`worker/views/README.md` — create D1, load
    schema, set `SEED_TOKEN`, `wrangler deploy`), then seed it. Worth doing early:
    nothing in WordPress serves `/api/`, so the route can go up while WP is still
    live, and the counter starts accumulating real views before cutover depends
    on it. Until then `data/views.json` is `{}` and no number renders.
  * The old GoatCounter scaffold (`partials/stats.html`) is deleted. It also held
    an unwired share button — nothing replaces that; ask if it's wanted.
* [X] Discussions -> giscus on this repo's GitHub Discussions
  * `partials/comments.html` is now called from `posts/single.html`, and
    `[params.giscus]` is in `hugo.toml`. **It renders nothing until you finish
    three manual steps** (see README "Comments"): enable Discussions with a
    comment-accepting "Blog Comments" category, install the giscus app, and paste
    `repoId`/`categoryId` — `jbang scripts/transfer/Comments.java --print-config`
    prints the block ready to paste.
  * A thread is keyed on the post **slug**, not the pathname: the trial deploy
    serves `/website/today/<slug>/` and production `/today/<slug>/`, so
    pathname-keyed threads would all be orphaned at cutover. `data-strict="1"`
    with it, because 30 slugs are substrings of another slug and fuzzy matching
    would show one post's comments on another.
  * Existing comments: **not** transfer/Posts.java — a new one-off,
    `scripts/transfer/Comments.java`. Posts writes files and gets re-run
    constantly; this writes irreversible public content to GitHub and needs a
    token, so mixing them would put a credential and a GitHub side effect in
    every routine re-scrape.
    * Source is WP's own open REST API (`/wp-json/wp/v2/comments`): 580 approved
      comments on 270 posts, all 270 matched to a local bundle, verified by
      `--dry-run`.
    * Posts as the Foojay account (`GITHUB_TOKEN`), each comment opening with
      `_Originally posted by **<author>** on <date> in Foojay.io Discussions._`
      (the name links to the commenter's URL when WP has one).
    * Idempotent and resumable — a discussion is reused when the term already has
      one, a comment is skipped when its `<!-- wp-comment-id: N -->` marker is
      already there. Needed, because ~850 writes run into GitHub's
      content-creation rate limit; `--limit N` batches and it backs off.
    * WP threads deeper than one level are flattened onto the top-level comment
      (GitHub Discussions only nest one level) — affects 4 comments.
    * **Not run yet**: needs the Foojay account's token. Run `--dry-run --slug
      <post>` first to see exact bodies, then one post for real, then the lot.
      Run it again just before cutover to pick up late WP comments.
* [X] Featured authors
  * See for background info: https://foojay.io/today/featured-authors-july-and-august-2026/
  * Specified in `hugo.toml`: `params.featuredAuthors = ["cristobal-escobar", "shai-almog"]`
    (author slugs). Rotating them monthly = editing that one line; everything
    shown comes from the author's own bundle. `validate/Frontmatter.java` fails
    the PR on a slug with no matching author.
  * `/today/author/`: two spotlight cards above the A-Z grid
    (`partials/featured-authors-band.html`).
  * Homepage: bottom of the right column (`partials/featured-authors-widget.html`,
    called from `sidebar.html` on the home page only).