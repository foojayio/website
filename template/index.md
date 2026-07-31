---
# ============================================================================
#  New foojay.io article — starter template
# ----------------------------------------------------------------------------
#  HOW TO USE
#   1. Copy this whole `template/` folder into
#         content/posts/<year>/<month>/<day>/<your-article-slug>/
#      Name the folder with the URL slug you want, e.g. .../my-great-article/.
#      The folder name becomes the URL: /today/my-great-article/  (no `slug`
#      frontmatter needed — the folder name is the slug).
#   2. Rename this file's folder, fill in the frontmatter below, write the
#      article, and drop any images into the SAME folder.
#   3. See `categories.md` (in this folder) for the list of existing categories.
#      Delete `categories.md` before submitting — it's just a reference.
# ============================================================================

# Article title. Rendered as the page H1 — do NOT repeat it as a "# " heading in
# the content (start your sections at "## "). Required.
title: "Your Article Title Here"

# Publish date (ISO 8601). Should match the folder date. A future date keeps the
# post unpublished until then. Required.
date: 2026-01-01T09:00:00+00:00

# Optional: last-updated date, if you revise the article later.
# lastmod: 2026-01-02T09:00:00+00:00

# One or two sentences. Shown on cards/listings and used for SEO and social
# previews. Required.
description: "A short, one- or two-sentence summary of the article."

# Author slug(s): the filename of your author profile in content/authors/
# (e.g. content/authors/f/frankdelporte.md  ->  "frankdelporte"). One or more.
# Required — every post needs at least one author.
authors:
  - "your-author-slug"

# Preview / hero image. Put the file in THIS folder and reference it by name.
# Leave empty ("") for no hero image.
image: ""

# Categories (see categories.md for the existing list — prefer existing ones).
categories:
  - "Java"

# Free-form tags. Optional — leave as [] if you have none.
tags: []

# Slugs (folder names) of related foojay articles to show at the bottom. Optional.
related_posts: []

# Advanced / optional -------------------------------------------------------
# slug: "override-url-slug"   # only to make the URL differ from the folder name
# canonical: "https://example.com/original/"  # only if first published elsewhere
# frozen: false               # only relevant to the WP import scripts; ignore
---

<!--
  Write your article below in Markdown.

  IMPORTANT: do NOT use a top-level "# Heading" — the `title` above is already the
  page's H1. Start your section headings at "## " (H2) and go deeper with "###".

  This block is an HTML comment and won't be published; delete it (and everything
  below that you don't need) once you get going.
-->

## A section heading

Write normal paragraphs. Inline formatting: **bold**, *italic*, `inline code`,
and [a link](https://foojay.io/). Links to other sites automatically open in a
new tab; links to other foojay pages stay in the same tab.

### Lists

- A bullet
- Another bullet
  - A nested bullet

1. A numbered item
2. Another one

### Quote and horizontal rule

> A blockquote for pull-quotes or citations.

---

### Code

Fenced code blocks are syntax-highlighted automatically — add the language after
the opening fence:

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, foojay!");
    }
}
```

### Images

Put the image file in THIS folder, then reference it by filename:

![Describe the image for accessibility](my-image.png)

For a smaller, floated, or captioned image, use the `img` shortcode
(class can be `alignleft`, `alignright` or `aligncenter`):

{{< img src="my-image.png" class="alignright" width="320" caption="An optional caption" >}}

### YouTube video

Pass just the video id:

{{< youtube dQw4w9WgXcQ >}}

### Tables

| Feature      | Supported |
|--------------|-----------|
| Markdown     | Yes       |
| Shortcodes   | Yes       |
