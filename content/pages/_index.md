---
title: "Pages"
# This file exists ONLY to stop Hugo publishing /pages/.
#
# Every file under content/pages/ sets its own `url:` (/board/, /calendar/,
# /jugs/, ...), so the section itself has no URL anyone designed -- but Hugo
# still generated a listing at /pages/ from it: an unstyled dump of all 40 page
# titles under the heading "Pages", indexable, in sitemap.xml, carrying the site
# boilerplate as its meta description, and linked from nowhere on the site.
# WordPress serves no such URL, so it was a page invented by the migration for
# crawlers to find and nobody to read.
#
# /sitemap/ is the curated version of exactly this list, is linked from the
# footer of every page, and is what a reader should land on.
#
# render/list "never" removes the page rather than hiding it: a `noindex` would
# still leave the URL published and crawlable, and `sitemap.disable` would leave
# it indexable. Children are unaffected -- these settings apply to the page that
# declares them, not to the section's pages, which is why /board/, /calendar/
# and the rest still build. (Contrast the `cascade` in content/authors/_index.md,
# which DOES reach the descendants.)
# (`build:`, not `_build:` -- the underscored key was removed in Hugo 0.145.)
build:
  render: never
  list: never
---
