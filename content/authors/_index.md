---
title: "Authors"
description: "Every developer, advocate and engineer who has written for foojay.io — browse the full A-Z or start with this month's featured authors."
url: "/today/author/"
# EXPLICIT, and load-bearing: Hugo applies a cascade to the page that declares it
# as well as to its descendants, so without this the cascaded type below landed on
# THIS page too and /today/author/ silently rendered with the author-profile
# layout -- an empty "0 articles" page where the A-Z grid should be. An explicit
# value always beats a cascaded one.
type: "authors"
# Each author bundle is a BRANCH bundle (_index.md), i.e. a Hugo section rather
# than a page. Not cosmetic: .Paginate refuses a page kind ("pagination not
# supported for this page"), and a prolific author has 290+ articles, which was
# one grid several screens long.
#
# The cascade does two jobs for every author, so neither is a per-file chore:
#
#   type      routes them to layouts/author/section.html. Without it an author
#             profile and this index would be the same type and fight over one
#             layout.
#   outputs   turns RSS on per author (layouts/author/section.rss.xml). hugo.toml's
#             [outputs] only covers home/section/taxonomy/term as CLASSES of page
#             and cannot single these out without giving every section a feed.
#
# `kind: section` is what the target must say -- it was `kind: page` while authors
# were leaf bundles, and after the conversion that matched nothing, which would
# have silently dropped every author feed.
cascade:
  - target:
      kind: section
    type: "author"
    outputs:
      - html
      - rss
---
