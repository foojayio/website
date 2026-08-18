# TODO

## ConverPosts

* [ ] Search for `[\[email protected\]](/cdn-cgi/l/email-protection)`
  * Email address in posts seem to get lost in the conversion to Hugo markdown. 
  * Some examples:
    * content/posts/2020/08/23/a-javafx-app-on-zulufx-in-60-seconds/index.md
    * content/posts/2026/07/31/javafx-links-of-july-2026/index.md
  * For instance, for JFX Links of the month posts it should link to links@jfx-central.com
* [X] Image galleries -> `{{< gallery >}}` shortcode, migrated with `scripts/MigrateGalleriesToShortcode.java`
* [X] Tags -> won't do as we have fixed list of categories

## Missing Content

* [ ] log4j-cve.md
* [ ] advisory boardext
* [ ] Foojay.io AI Portal

## Features

* [ ] Read counter
* [ ] Discussions
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