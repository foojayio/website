---
title: "Authors"
url: "/today/author/"
# Individual author pages are Hugo "page" kind, not a taxonomy, so they don't
# get RSS from the top-level [outputs] config (which only covers home/section/
# taxonomy/term). This cascade turns RSS on for every page under /today/author/
# without affecting posts, pedia, or generic pages elsewhere in the site.
# Template: themes/foojay/layouts/authors/page.rss.xml
cascade:
  - target:
      kind: page
    outputs:
      - html
      - rss
---

