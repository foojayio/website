# TODO

## ConverPosts

* [ ] Search for `[\[email protected\]](/cdn-cgi/l/email-protection)`
  * If JFX Central -> links@jfx-central.com
* [ ] Image galleries
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