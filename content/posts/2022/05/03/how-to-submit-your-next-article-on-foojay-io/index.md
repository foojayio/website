---
title: "How To Submit Your Next Article On Foojay.io"
date: "2022-05-03T18:32:09+00:00"
lastmod: "2026-07-31T00:00:00+00:00"
description: "The complete guide to publishing your article on Foojay.io: prepare a content folder from the template, add your images, and submit it by pull request or email."
authors:
  - "bazlur-rahman"
  - "frankdelporte"
  - "geertjan-wielenga"
image: ""
categories:
  - "Foojay"
related_posts:
  - "how-to-add-an-event-to-the-foojay-event-calendar"
frozen: true
---

Foojay, the place for friends of OpenJDK, is a friendly community of Java and Kotlin developers who share tips and insights every day on [Foojay Today](/today/). We would love to publish your article too!

In September 2026, Foojay.io moved from a WordPress system to a static site built with [Hugo](https://gohugo.io/) and published from a public GitHub repository, [github.com/foojayio/website](https://github.com/foojayio/website). Contributing an article means adding a small folder of files (your text and images) to that repository. You don't need to know Hugo — everything you need is in a ready-made template.

## 1. Make sure you have an author profile

Every article needs at least one author, and each author has a profile folder named with the author **slug**, grouped by first letter: `content/authors/<first-letter>/<slug>/`. Your post's `authors:` frontmatter uses that slug. For example the profile `content/authors/f/frankdelporte/` is referenced as `authors: ["frankdelporte"]`.

First, check whether your profile folder already exists under `content/authors/` (look in the folder for the first letter of your slug). If it does, use that folder name as your slug and move on to the next step.

**If it doesn't exist yet, create it and include it in the same submission.** Make a folder `content/authors/<first-letter>/<your-slug>/` (e.g. `content/authors/j/jane-doe/`) containing an `index.md`. Start from `template/author.md`, which lists every field with a comment. At a minimum set `title` (your name) and a short `bio`, and add your social links (Bluesky, Mastodon, LinkedIn, GitHub, YouTube, website). For an avatar, drop a square photo into the folder and reference it by filename: put the small version in `avatar:` and, optionally, a larger version in `avatarFull:`. Then use that folder name as the slug in your article's `authors:` list.

## 2. Prepare your article folder

Each article lives in its own folder that holds an `index.md` file (your article) and any images it uses, all together.

1. **Create a folder** named with the URL "slug" you want for your article — lowercase, words separated by dashes. For example a folder called `my-first-java-record` becomes the URL `/today/my-first-java-record/`.
2. **Copy `template/post.md`** (from the repository's `template/` folder) into your new folder and rename it to `index.md`. It contains every frontmatter field with comments explaining how to fill it in.
3. **Fill in the frontmatter** at the top of `index.md`: `title`, `date`, `description`, your `authors` slug(s), and `categories`. The `template/categories.md` file lists the categories already in use — please reuse an existing one where it fits.
4. **Write your article** below the frontmatter in [Markdown](https://www.markdownguide.org/basic-syntax/). Start your section headings at `##` — do **not** add a top-level `#` heading, because the `title` is already shown as the page heading. The template includes examples of headings, code blocks, images, tables, and shortcodes (for example `{{</* youtube VIDEO_ID */>}}` to embed a video).
5. **Add your images** into the same folder and reference them by filename, e.g. `![A caption](diagram.png)`. Set the `image:` field in the frontmatter section to your preferred preview image.

## 3. Submit your article

In order of preference:

### Option 1 — Pull request (direct access)

If you have write access to the repository, create your folder under **`draft/your-slug/`**, commit it, and open a pull request. A maintainer reviews it and moves it into place. If you'd like this access, just ask — mention it in your first pull request or in an email to us.

### Option 2 — Pull request (from a fork)

No access yet? [Fork the repository](https://github.com/foojayio/website/fork), add your **`draft/your-slug/`** folder to your fork exactly as in Option 1, and open a pull request from your fork back to the main repository.

### Option 3 — Email us a zip

Not comfortable with Git? No problem. **Zip up your article folder** (the `index.md` and its images) and email it to [hello@foojay.io](mailto:hello@foojay.io). We'll take care of the rest.

## What happens next

We review every submission for a good fit and light editing, then publish it and share it with the community. Thanks for writing for the friends of OpenJDK!
