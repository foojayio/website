---
title: "How To Submit Your Next Article On Foojay.io"
date: "2022-05-03T18:32:09+00:00"
lastmod: "2026-10-29T00:00:00+00:00"
description: "The complete guide to publishing your article on Foojay.io: prepare a content folder from the template, add your images, and submit it by pull request or email."
authors:
  - "bazlur-rahman"
  - "frankdelporte"
  - "geertjan-wielenga"
image: "foojay-categories.jpg"
categories:
  - "Foojay"
related_posts:
  - "announcing-the-new-foojay"
  - "asciidoc-support-on-foojay"
  - "diagrams-as-code-mermaid-support-on-foojay"
  - "how-to-add-an-event-to-the-foojay-event-calendar"
  - "join-slack-com-t-foojay-signup"
# WordPress's admin URLs. Nothing here has a login, and these are the two paths
# people (and bots) still try -- so they land on how to actually contribute.
aliases:
  - "/wp-admin/"
  - "/wp-login.php"
  - "/write-for-foojay/"
---

Foojay, the place for Friends Of OpenJDK, is a friendly community of Java and Kotlin developers who share tips and insights every day on [Foojay Today](/today/). We would love to publish your article too!

In September 2026, Foojay.io moved from a WordPress system to a static site built with [Hugo](https://gohugo.io/) and published from a public GitHub repository, [github.com/foojayio/website](https://github.com/foojayio/website). See [Announcing the new Foojay](/today/announcing-the-new-foojay/) for the full story of that move, and why articles now go through a pull request instead of a login. Contributing an article means adding a small folder of files (your text and images) to that repository. You don't need to know Hugo — everything you need is in a ready-made template.

## What we publish

Foojay is about Java, not about who pays for the keyboard. **Articles about a company's product or service are welcome, as long as they are Java/OpenJDK/JVM/Developer-oriented**: a reader has to come away with something they can use:

* Java example code (or Kotlin, or another JVM language)
* Information about a tool that can improve developer work
* Best practices, tips and tricks
* ...

The same goes for who is behind an article. **Every author profile is a real person**, with a real photo of themselves and links we can use to verify them: at least one of GitHub, LinkedIn, Bluesky, Mastodon or a personal website. Company, brand and team accounts (with a logo as the avatar) are not accepted. If you write for your employer, say so in your bio. Profiles we can't verify as a person are not merged.

## 1. Make sure you have an author profile

Every article needs at least one author, and each author has a profile folder named with the author **slug**: `content/authors/<slug>/`. Your post's `authors:` frontmatter uses that slug. For example the profile `content/authors/frankdelporte/` is referenced as `authors: ["frankdelporte"]`.

First, check whether your profile folder already exists under `content/authors/` — they are all listed there, one folder per author. If it does, use that folder name as your slug and move on to the next step.

**If it doesn't exist yet, create it and include it in the same submission.** Make a folder `content/authors/<your-slug>/` (e.g. `content/authors/jane-doe/`) containing an `_index.md` — note the underscore, which is what lets the profile paginate a long article list. Start from `template/author.md`, which lists every field with a comment. At a minimum set `title` (your own name, not a company's), a short `bio`, and at least one link that lets us verify you (GitHub, LinkedIn, Bluesky, Mastodon, YouTube or website); more is better. Then use that folder name as the slug in your article's `authors:` list.

For your **profile picture**, use a real photo of yourself, not a logo. Drop the photo into the same folder and reference it by filename in the `avatar:` field. Everywhere the site shows one it is cropped to a **circle, from the middle of the image outwards**, so the ideal file is **square, 192 x 192 pixels, JPEG, under 30 KB**:

- **Square**, because the crop keeps the middle and discards the rest — on a portrait photo it is the top of your head and your shoulders that go. Crop it square yourself and you decide what stays, and leave a little room around your face, as the circle trims the corners too.
- **JPEG** for a photo, and PNG only for a drawn or logo-style avatar with hard edges that JPEG makes look fuzzy. Not an SVG: your picture also goes into the page's structured data, which the tools that read it won't render.
- **On a solid background**, not a transparent one. A circle cut out of a transparent image lets the page show through, and dark artwork on a transparent background all but disappears in dark mode.
- Nothing is resized when the site is built, so the file you commit is the file every reader downloads. 192 pixels is twice the size the biggest circle on the site is drawn at, which is what keeps it sharp on a high-resolution screen without being heavy — the author overview page shows all 350+ of them at once.

The `avatarFull:` field next to it is **optional**: set it only if you have a larger portrait than the small one, ideally **400 x 400, under 100 KB**. It is used where your picture is shown on its own — your profile page, the monthly featured-authors spotlight, and your thumbnail in the site search — and falls back to `avatar:` when it is empty. `avatar:` is the one that always needs filling in: leave *it* empty and you are missing from the author overview and the sidebar widget even if `avatarFull:` is set.

## 2. Prepare your article folder

Each article lives in its own folder that holds an `index.md` file (your article) and any images it uses, all together.

1. **Create a folder** named with the URL "slug" you want for your article — lowercase, words separated by dashes. For example a folder called `my-first-java-record` becomes the URL `/today/my-first-java-record/`.
2. **Copy a starter template** (from the repository's `template/` folder) into your new folder. Either format works: copy `template/post.md` and rename it to `index.md` to write in Markdown, or copy `template/post.adoc` and rename it to `index.adoc` to write in AsciiDoc. Both contain every frontmatter field with comments explaining how to fill it in. Markdown is the shorter of the two and what most articles use; AsciiDoc is worth it when you want captions on code blocks, numbered callouts inside a snippet, admonitions, or tables that span cells.
3. **Fill in the frontmatter** at the top of the file: `title`, `description`, your `authors` slug(s), and `categories`. This part is identical in both formats — same fields, same `---` fences, whether the file is `index.md` or `index.adoc`. The `template/categories.md` file lists the categories already in use — please reuse an existing one where it fits. **Leave `date:` as it is in the template** — a maintainer sets it and moves your folder to match when the article is published, so you never have to pick a day or a folder yourself. Only change it if you need the article out on a specific day. In that case, clearly specify the preferred date in your pull request.
4. **Write your article** below the frontmatter, in [Markdown](https://www.markdownguide.org/basic-syntax/) or in [AsciiDoc](https://docs.asciidoctor.org/asciidoc/latest/syntax-quick-reference/) depending on the template you started from. Start your section headings at `##` in Markdown, or at `==` in AsciiDoc — in neither case add a top-level heading of your own, because the `title` is already shown as the page heading. Each template includes examples of headings, code blocks, images, tables and shortcodes (for example `{{</* youtube VIDEO_ID */>}}` to embed a video) written in its own syntax. Need something a template doesn't show? [AsciiDoc support on Foojay](/today/asciidoc-support-on-foojay/) covers named blocks, source callouts and admonitions for when a code block needs a caption or a warning needs to stand apart from the paragraph before it. Use `index.adoc` instead of `index.md` when you need those, and use the AsciiDoc format. And any article can include a diagram: [Diagrams as Code: Mermaid support on the new Foojay](/today/diagrams-as-code-mermaid-support-on-foojay/) explains writing a sequence, class, state, entity-relationship or Gantt diagram as a fenced ` ```mermaid ` block, reviewable in your pull request like everything else.
5. **Add your images** into the same folder and reference them by filename, e.g. `![The Ports view in IntelliJ, showing the app on port 8080](diagram.png)`. The text in the square brackets is the image's *description*, not a caption: it is the only part of the image a reader using a screen reader gets, so write what the image shows.
6. **Set the preview image.** The `image:` field in the frontmatter is your article's card on the home page and in the article overviews, and the picture that appears when your post is shared on social media, so every article needs one. Put the file in the same folder and reference it by filename. The ideal file is **1600 x 900 pixels (16:9), JPEG, under 300 KB**:
    - **16:9** is exactly the shape of the large card on the home page, and it crops cleanly to the smaller cards and to the social preview. All of those crop from the centre outwards, so keep anything that has to stay readable — text, logos, faces — away from the edges. The 1600 pixel width is for the social preview; the site itself never needs more than that.
    - **JPEG** for photos and screenshots, and PNG only for hard-edged line art that JPEG makes look fuzzy. Not an SVG: social platforms won't render one, so your article would preview with the generic Foojay card instead of your own image.
    - **A still image on a solid background.** Cards and social previews don't animate, so point `image:` at a still frame and put an animated version in the article body instead. And a transparent background can disappear in dark mode, where cards paint their own colour behind the image.

**Previewing is optional.** The pull request check builds the site for you. To see your article locally first, run `hugo server` and open `http://localhost:1313/website/today/your-slug/`. The `draft/` folder is not built, so either copy your folder into `content/posts/<year>/<month>/<day>/` using today's date (in the path and in `date:`), or keep a future `date:` and start the server with `hugo server --buildFuture`. Don't commit that copy.

## 3. Submit your article

In order of preference:

### Option 1 — Pull request (direct access)

If you have write access to the repository, create your folder under **`draft/your-slug/`**, commit it, and open a pull request. A maintainer reviews it and moves it into place. If you'd like this access, just ask — mention it in your first pull request or in an email to us.

### Option 2 — Pull request (from a fork)

No access yet? Add your **`draft/your-slug/`** folder to your own fork of the repository and open a pull request from there back to the main repository. You need a free GitHub account, but you do **not** need to clone the whole repository: with its history and years of images it is around 2 GB, and you only need to add a few files. Pick whichever of these fits you:

#### In the browser, without installing anything

This is the quickest route for a text article.

1. Open [github.com/foojayio/website](https://github.com/foojayio/website) and go to the `draft` folder.
2. Choose **Add file → Create new file**. GitHub tells you that you need to fork the repository to propose changes, and does that for you.
3. In the file name box type `your-slug/index.md`. Typing the `/` creates the folder. Paste your article, including the frontmatter from `template/post.md`, into the editor.
4. Choose **Commit changes…**, then **Propose changes**, and on the next screen **Create pull request**.
5. Now add your images. On the pull request page, follow the link to your branch in your fork, open `draft/your-slug/`, and choose **Add file → Upload files**. Every upload is added to the same pull request. A new author profile (`content/authors/<your-slug>/`) is added the same way.

If you already have an old fork, use **Sync fork** on its page first, or your pull request will carry commits that are not yours.

#### On your computer, with a small clone

If you prefer your own editor, [fork the repository](https://github.com/foojayio/website/fork) and download only what you need: a shallow clone without history, restricted to the few folders an article touches.

```bash
git clone --depth 1 --filter=blob:none --sparse https://github.com/<your-github-name>/website.git
cd website
git sparse-checkout set draft template content/authors
```

That fetches a fraction of the full size. Then create your article and push it:

```bash
git checkout -b my-article
mkdir draft/your-slug
cp template/post.md draft/your-slug/index.md
# edit index.md, add your images to the same folder
git add draft/your-slug
git commit -m "Add article: <your title>"
git push -u origin my-article
```

GitHub prints a link in the output of the push to open the pull request. If you are adding an author profile, `git add content/authors/<your-slug>` as well. Because only some folders are checked out, you cannot run `hugo server` from this clone, which is fine since the pull request check builds the site for you.

### Option 3 — Email us a zip

Not comfortable with Git? No problem. **Zip up your article folder** (the `index.md` and its images) and email it to [hello@foojay.io](mailto:hello@foojay.io). We'll take care of the rest.

## Code of Conduct

By submitting an article or a pull request you agree to follow our [Code of Conduct](/code-of-conduct/): communicate in a friendly way, respect other opinions, and keep feedback constructive.

## What happens next

We review every submission for a good fit and light editing, then publish it and share it with the community. Thanks for writing for the Friends Of OpenJDK!
