# TODO

## ConverPosts

* [ ] Search for `[\[email protected\]](/cdn-cgi/l/email-protection)`
  * Email address in posts seem to get lost in the conversion to Hugo markdown. 
  * Some examples:
    * content/posts/2020/08/23/a-javafx-app-on-zulufx-in-60-seconds/index.md
    * content/posts/2026/07/31/javafx-links-of-july-2026/index.md
  * For instance, for JFX Links of the month posts it should link to links@jfx-central.com
* [ ] Em dashes became `---` in the conversion
  * Flexmark's html2md converter applies typographic replacements, so a `—` in a
    WP body is stored as `---` (and `–` as `--`, `…` as `...`). Goldmark doesn't
    turn those back, so the site renders a literal `---` mid-sentence: 295 files
    have one, against 93 that kept a real em dash.
  * Fix is one option on `HtmlToMarkdown.CONVERTER_OPTIONS` (Flexmark's
    typographic smarts) plus a one-off pass over `content/` — same shape as
    `NormalizeMarkdown.java`. Watch out for `---` that is really a thematic
    break or YAML separator inside a fence.
  * Worth doing **before** `ImportWpComments.java` runs: it shares the converter,
    and a comment already posted to GitHub won't be fixed by a re-run.
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