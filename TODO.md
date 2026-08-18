# TODO

## ConverPosts

* [ ] Search for `[\[email protected\]](/cdn-cgi/l/email-protection)`
  * Email address in posts seem to get lost in the conversion to Hugo markdown. 
  * Some examples:
    * content/posts/2020/08/23/a-javafx-app-on-zulufx-in-60-seconds/index.md
    * content/posts/2026/07/31/javafx-links-of-july-2026/index.md
  * For instance, for JFX Links of the month posts it should link to links@jfx-central.com
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
  * `content/` was normalized too (`NormalizeMarkdown.java` pass 3: 2207 stand-ins
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
* [X] Image galleries -> `{{< gallery >}}` shortcode, migrated with `scripts/MigrateGalleriesToShortcode.java`
* [X] Tags -> won't do as we have fixed list of categories

## Missing Content

* [ ] log4j-cve.md
* [ ] advisory boardext
* [ ] Foojay.io AI Portal

## Features

* [ ] Read counter
  * Can we integrate a free system that counts the number of reads per post/page?
  * Do not use Google Analytics for this purpose, as it is not privacy-friendly.
  * Number of reads should be displayed on page below the title in the line with, e.g., "Jun 9, 2026
    Frank Delporte 4 min read"
  * A script needs to be added to have a conversion of the current number of reads from the WordPress database to the Hugo site. This is a one-time operation, but it needs to be done before the site goes live and needs to be repeated until we go live.
* [X] Discussions -> giscus on this repo's GitHub Discussions
  * `partials/comments.html` is now called from `posts/single.html`, and
    `[params.giscus]` is in `hugo.toml`. **It renders nothing until you finish
    three manual steps** (see README "Comments"): enable Discussions with a
    comment-accepting "Blog Comments" category, install the giscus app, and paste
    `repoId`/`categoryId` — `jbang scripts/ImportWpComments.java --print-config`
    prints the block ready to paste.
  * A thread is keyed on the post **slug**, not the pathname: the trial deploy
    serves `/website/today/<slug>/` and production `/today/<slug>/`, so
    pathname-keyed threads would all be orphaned at cutover. `data-strict="1"`
    with it, because 30 slugs are substrings of another slug and fuzzy matching
    would show one post's comments on another.
  * Existing comments: **not** ConvertPosts.java — a new one-off,
    `scripts/ImportWpComments.java`. ConvertPosts writes files and gets re-run
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
    shown comes from the author's own bundle. `ValidateFrontmatter.java` fails
    the PR on a slug with no matching author.
  * `/today/author/`: two spotlight cards above the A-Z grid
    (`partials/featured-authors-band.html`).
  * Homepage: bottom of the right column (`partials/featured-authors-widget.html`,
    called from `sidebar.html` on the home page only).