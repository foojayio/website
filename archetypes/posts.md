---
# hugo new content posts/2026/07/my-slug.md --kind posts
title: "{{ replace .Name "-" " " | title }}"
date: {{ .Date }}

# Shown in cards, search results, OpenGraph and Twitter meta. 1-2 sentences,
# under ~160 characters. Not optional — cards look broken without it.
description: ""

# Set ONLY when the piece was first published elsewhere and that copy should
# stay the SEO original.
canonical: ""

# Slug of the author file: content/authors/<author>.md
author: ""

# Card + hero + og:image. Landscape, at least 1200x630.
image: ""

# First category is the primary one; cards show at most two.
categories: []
tags: []

# Related posts are picked by the author, not computed automatically.
# List the slugs of other posts (the filename under content/posts/).
related_posts: []

# Only needed if this post's URL must differ from the default /today/:slug/
# pattern, or to add extra legacy-URL redirects.
aliases: []

# Set true to stop the WordPress conversion scripts from overwriting hand edits.
frozen: false

draft: true
---

Write your post content here.

<!--
Style notes for the theme:
- Don't start with an H1 — the layout renders `title` as the page H1.
- Structure with `##` and `###`; both are styled and spaced by `.prose`.
- Fenced code blocks, tables, blockquotes and figures are all styled already;
  no HTML classes needed in the markdown.
-->
