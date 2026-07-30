# Contributing a post

Posts are contributed via pull request. GitHub Pages doesn't give us an
automatic preview URL per PR, so please preview locally before opening one.

## 1. Create your post

```bash
hugo new content/posts/your-post-slug.md
```

This uses `archetypes/posts.md` and fills in the required frontmatter fields:

```yaml
---
title: "Your Post Title"
date: 2026-07-30
description: "One or two sentences for search results and social previews."
canonical: ""            # leave blank unless this was published elsewhere first
author: "your-author-slug"   # must match a file in content/authors/
image: "/images/posts/your-post-slug/hero.png"
categories: ["Java"]
tags: ["performance"]
related_posts: []        # add the slugs (filenames, no .md) of 1-3 related posts you'd pick yourself
aliases: []              # only needed if this post replaces an old URL
draft: true              # set to false when ready to publish
---
```

`related_posts` is manual, not automatic — pick the posts you think are
genuinely relevant, the same way it worked on the old WordPress site.

## 2. Add images

Put images under `static/images/posts/your-post-slug/` and reference them
with an absolute path, e.g. `/images/posts/your-post-slug/diagram.png`.

## 3. Preview locally

```bash
hugo server -D
```

Check your post renders correctly at `http://localhost:1313/today/your-post-slug/`.

## 4. Open the PR

- Set `draft: false` once it's ready.
- The PR check workflow will validate your frontmatter and build the site —
  fix anything it flags before requesting review.
- If you don't have write access to open a branch directly, fork the repo
  and open the PR from your fork.

## New author?

Add yourself first with `hugo new content/authors/your-author-slug.md`
(see `archetypes/authors.md` for the fields), in the same PR or a separate one.
