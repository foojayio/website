---
title: "Categories"
description: "Every category articles on foojay.io are filed under, A-Z, with the number of articles in each."
# Hugo puts a taxonomy's own list page at /categories/ by default, while
# hugo.toml's `[permalinks] categories` puts every TERM at
# /today/category/<slug>/. That left the index orphaned one level up from its
# own children, which is why nothing linked to it and "All categories" pointed
# at /today/ (all articles) instead. `url:` moves the index to the parent of its
# terms; `aliases:` keeps the default path redirecting, since it has been built
# and deployed under it.
url: "/today/category/"
aliases:
  - "/categories/"
---

Everything published on foojay.io is filed under one or more categories. Browse
them all below, or use [search](/search/) if you already know what you're after.
