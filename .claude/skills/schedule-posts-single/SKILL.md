# Schedule a draft post for publication

Foojay authors submit articles as a pull request that adds a folder to `../../../draft`.
A maintainer reviews the article and gives it a publication date. This task
covers that second half: take a reviewed draft, date it, and move it into
`content/posts/`.

A draft is a page bundle: a folder named for the article's URL slug, holding an
`index.md` (or `index.adoc`), plus every image the article uses. The frontmatter
carries the metadata. `../../../draft/README.md` documents the shape for authors, and
`template/post.md` is the starter file they copy.

Hugo publishes a post on its `date`. A date in the future schedules it: Hugo
does not build the post at all until that day, and `partials/coming-soon.html`
lists it on the home page in the meantime.

## Ask first, if the request does not say

* Which draft to schedule.
* Which date to publish it on.

Several posts on one day is normal, so a date that already has posts needs no
second thought.

## Steps

### Check the frontmatter

`jbang scripts/validate/Frontmatter.java` is the definition of correct, and it
runs on every PR. It requires `title`, `description`, `authors`, `image` and
`categories` on a draft, rejects an emoji in a title, and fails on a
`related_posts` entry whose slug matches no post.

Two things the validator does not check, so check them yourself and propose improvements if needed:

* `categories`: at least one, and each one has to exist in
  `../../../template/categories.md`.
  Some authors write mostly about one product. When the post is about
  MongoDB, Codename One or BoxLang (named in the title or description, or the
  subject of the body), add `Mongo`, `Codename One` or `BoxLang` even if the
  author left it out, so the post shows up on that category page.
* `image`: check the image file exists, is in the bundle, and is not too large (see step 4). It should also be a .jpg or .png, not a .gif, .svg, or .webp. In that case, convert it, otherwise it will not be picked up by social media cards when the post is shared.
* Missing `image`: don't send the draft back. Run
  `~/.venvs/foojay-images/bin/python .claude/skills/schedule-posts/generate_header.py --path <bundle>`
  (after the move, before the resize step). It reuses the best image already in
  the bundle when one is at least 1000px wide and close to 16:9, otherwise
  renders a plain branded card from the title and first category, and sets
  `image: "header.jpg"`. Mention in the PR comment that the header was generated, so the
  author can supply a better one.
* No duplicate header image: the layout renders `image` as a large hero above the content, so the body must not open with that same image (`![...](same-file)` as the first line, or the first file in a leading gallery). Delete it from the body, but keep any other images.
* `related_posts`: up to six recent, genuinely related posts reads best. This
  is a house preference, not a rule that blocks a merge. Find related posts if none are included.

### Remove the frontmatter comments

The starter file carries long `#` comments between the keys. Delete every comment line inside the frontmatter (between the two `---` lines) and any blank line they leave behind. Leave the body alone: a `#` there is a heading or code. This also applies for new author profiles in `content/authors/`, which are also page bundles with frontmatter.

### Set the date

Write the day only, with no time:

```yaml
date: "2026-10-03"
```

A time on a future date fails the PR check. Every post goes out at the daily
scheduled build rather than at the time in the field, so a time later than the
build silently holds the article back past its own date, and an earlier one
changes nothing.

### Move the bundle

```
git mv draft/SLUG content/posts/YYYY/MM/DD/SLUG
```

The `YYYY/MM/DD` in the path has to match the `date` in the frontmatter. The
check fails the PR when they disagree, because Hugo publishes off the date and
the folder is the cosmetic half. Use `git mv` so the move stays a rename in the
history rather than a delete plus an add.

### Resize the images

```
~/.venvs/foojay-images/bin/python .claude/skills/schedule-posts/resize_images.py --path content/posts/YYYY/MM/DD/SLUG
```

Pillow lives in a venv because Homebrew's Python refuses `pip install`. If the
venv is missing: `python3 -m venv ~/.venvs/foojay-images && ~/.venvs/foojay-images/bin/pip install Pillow`.

Run this after the move, on the new path. It caps the long edge at 1600px,
converts a PNG over 100 KB to JPEG and an animated GIF to WebP, and gives an
animated hero a still poster. The conversions rename files, so the script
rewrites the references in `index.md` and `index.adoc` itself. Add `--dry-run`
first to see what it would touch.

The validator fails a PR on an image over 4 MB, which is the backstop this step
exists to stay clear of.

### Present the images as galleries

If the body contains images, improve how they render with the `{{< gallery >}}`
shortcode (syntax in `template/post.md`) rather than leaving bare `![](x.png)`
lines. Do this after the resize step, so the filenames are the final ones.
`content/posts/2026/10/30/rewrite-everything-in-java-not-rust/index.md` is the
model:

* Images that belong together (a series of screenshots, before/after, one per
  platform) go in one gallery, `cols` set to the count, at most 3.
* A lone image becomes a `cols="1"` gallery, so it also enlarges on click.
* One filename per line, then `| caption`. Add a second `| alt text` when the
  description for a screen reader should differ from the caption, and write one
  whenever the original alt text was empty or `image`, describing what the
  picture shows.
* Keep the author's own wording for captions and alt text where it exists.
* Raw HTML image grids (`<div class="release-screenshots">` with `<figure>`,
  `<img>` and `<figcaption>`, often from syndicated posts) are galleries too:
  `<figcaption>` becomes the caption and `alt` the second field. Convert them
  even when each image is wrapped in a link; the shortcode has no links, and
  the article is normally linked in the text nearby. Search for `<img` and
  `<figure`, not only `![`.
* Leave the frontmatter `image:` alone, and leave diagrams, logos and inline
  icons that sit inside a sentence as plain images.
* Afterwards, check the bundle for a PNG left beside its JPG conversion that
  nothing references, and delete it.

Check the rendered page afterwards; a filename that does not match a file in
the bundle shows no picture.

### Describe every image

Every image in the body needs a description, including the ones that stay
plain after the gallery step. Look at each file and write what it shows in the
context of the article, one sentence, ideally under 125 characters. Never
`image` or `screenshot` alone, and never repeat the caption word for word.
Cover all the shapes an image takes:

* `![](x.png)` gets text between the brackets.
* `{{< img src="x.png" >}}` gets `alt="..."`.
* A gallery line with no caption gets the second `|` field empty and the third
  filled: `x.png | | description`.
* Raw `<img>` and `image::x.png[]` (AsciiDoc) get the same.

Leave a description empty only for a decoration, such as a divider. Keep the
author's own text where it exists, and use no double quotes, square brackets or
pipes in it. The frontmatter `image:` hero needs none.

### Verify

```
jbang scripts/validate/Frontmatter.java
```

Expect `Frontmatter check passed`. The lines starting with `~` are advisory and
span the whole archive, so read the ones naming this post and ignore the rest.
None may name this post under "no description": fix those before committing.

### Review the content

Review the content of the post for spelling mistakes, broken links, and other errors. The validator does not check the body of the post, ask the website maintainer for answers if something is not clear.

Syndicated posts (for example from the Codename One blog) can carry a "Coming This
Week" section listing dated articles. Reword the heading and intro to fit the
publication date: "Last Week on the ... Blog" when every listed date has passed.

Avoid too promotional posts, and make sure the content is relevant to the audience. If you find any issues, fix them or ask how to be handled before proceeding.

### Transcript

If the post is a podcast and/or contains a YouTube embed, run the transcript script to generate a `transcript.md` in the post bundle. This is optional, but recommended for accessibility and SEO.

### Commit to `main`

A message in the shape of `New post from draft: SLUG` matches what the history
already uses. Publishing needs nothing further from you.
`publish-scheduled.yml` checks twice a day, at 07:07 and 14:07 Brussels time,
whether a post has come due, and asks `build-deploy.yml` to deploy when one
has.

### Comment on the pull request

After the push, tell the author with one comment on the PR that delivered the
draft, holding the scheduled date and their profile link
`https://foojay.io/today/author/<author-slug>/` (the `/today/author/` pattern
comes from `hugo.toml` permalinks).

Find the PR first. `gh` may fail inside the sandbox; the number is also in the
merge commit:

```
c=$(git log --diff-filter=A --format=%H -- draft/SLUG | tail -1)
git log --merges --ancestry-path --format='%s' $c..HEAD | tail -1
```

A "Merge branch ..." subject is not the PR: look the number up in the recent
`Merge pull request #N from ...` history by branch name. Verify before commenting.

```
gh pr comment N --repo foojayio/website --body "Thanks NAME! Your article is scheduled for publication on **Thursday 19 November 2026**.

Your author profile: https://foojay.io/today/author/SLUG/
You may need to wait a few minutes for the profile to appear, as it takes up to 10 minutes to appear after the commit."
```

Also mention here anything you changed beyond the date (generated header,
replaced `related_posts` slugs, categories added).

For a **new or recent author**, add this paragraph after the profile link:

```
Please join our Foojay community on Slack if you haven't done this yet. We announce new posts on it once they are published, have chats about all-things-java, and more. https://foojay.io/today/join-slack-com-t-foojay-signup/
```

New or recent = no published post by them before the one being scheduled, or
their first post is under 6 months old. Check by author slug, excluding the
draft itself:

```
grep -rl --include=index.md --include=index.adoc -E '^\s*-\s*"?SLUG"?\s*$|authors: \[.*"SLUG"' content/posts | sort | head -3
```

The oldest folder date is their first post. Longstanding authors get no Slack paragraph.

`gh` needs the sandbox disabled (TLS). Take the first name from the author's
`content/authors/SLUG/_index.md` title. For a co-written post, comment once and
link every author.

### Clean up

Delete empty `.claude` directories the tooling left in subfolders, never the one at the repo root:

```
find . -mindepth 2 -type d -name .claude -not -path './.git/*' | while read d; do rmdir "$d/.cc-writes" 2>/dev/null; rmdir "$d"; done
```
