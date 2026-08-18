---
# ============================================================================
#  New foojay.io article — starter template
# ----------------------------------------------------------------------------
#  HOW TO USE
#   1. Copy this file to  draft/<your-article-slug>/index.md
#      Name the folder with the URL slug you want, e.g. draft/my-great-article/.
#      That folder name becomes the URL: /today/my-great-article/  (no `slug`
#      frontmatter needed — the folder name is the slug). A maintainer moves it
#      into content/posts/<year>/<month>/<day>/ when it is published, so you
#      never have to pick that path yourself. See draft/README.md.
#   2. Fill in the frontmatter below, write the article, and drop any images
#      into the SAME folder next to this file.
#   3. See `categories.md` (in the template folder) for the list of existing
#      categories. Don't copy it into your own folder — it's just a reference.
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

# Author slug(s): the FOLDER name of your author profile in content/authors/,
# which is grouped by first letter
# (e.g. content/authors/f/frankdelporte/  ->  "frankdelporte"). One or more.
# Required — every post needs at least one author. If you don't have a profile
# yet, create one from archetypes/authors.md and include it in the same PR.
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
---

<!--
  Write your article below in Markdown.

  IMPORTANT: do NOT use a top-level "# Heading" — the `title` above is already the
  page's H1. Start your section headings at "## " (H2) and go deeper with "###".

  This block is an HTML comment and won't be published; delete it (and everything
  below that you don't need) once you get going.
-->

## Use Headings

Write normal paragraphs. Inline formatting: **bold**, *italic*, `inline code`,
and [a link](https://foojay.io/). Links to other sites automatically open in a
new tab; links to other foojay pages stay in the same tab.

## Extra Formatting Options

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

Fence code blocks with `` ``` ``. The content gets syntax-highlighted
automatically with [EnlighterJS](https://github.com/EnlighterJS/EnlighterJS)
if you add the language after the opening fence.

For example, Java code:

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, foojay!");
    }
}
```

The tags used most on Foojay: `java`, `bash`, `yaml`, `xml`, `kotlin`, `json`, `cpp`, `html`, `python`, `javascript`, `css`, `sql`, `groovy`, `dockerfile`, `rust`, `powershell`, `c`, `lua`. 

Others that work: `csharp`, `go`, `typescript`, `ruby`, `php`, `scala`, `swift`, `dart`, `r`, `markdown`, `diff`, `ini`, `nginx`, `shell`, `latex`, `matlab`, `scss`, `less`, `jsx`.

Use a tag even when it isn't in this list — an unknown one just renders unhighlighted, never broken. Or leave the tag off entirely for plain, unhighlighted output, for example:

```
$ ./gradlew build
```

### Images

Put the image file in THIS folder, then reference it by filename:

![Describe the image for accessibility](my-image.png)

For a smaller, floated, or captioned image, use the `img` shortcode
(class can be `alignleft`, `alignright` or `aligncenter`):

{{< img src="my-image.png" class="alignright" width="320" caption="An optional caption" >}}

Every image in an article is click-to-enlarge automatically — you don't need to
do anything for that.

### Image gallery

Several images as a responsive grid — just list the filenames:

{{< gallery "one.png" "two.png" "three.png" >}}

Want captions, or a different number of columns? Use the named form. A `|`
separates a filename from its caption, and `cols` is a *maximum* — the grid
still drops to fewer columns on a phone:

{{< gallery images="one.png|The first one, two.png|The second one" cols="2" >}}

### YouTube video

Pass just the video id:

{{< youtube dQw4w9WgXcQ >}}

### Tables

| Feature      | Supported |
|--------------|-----------|
| Markdown     | Yes       |
| Shortcodes   | Yes       |
