# Schedule a draft post for publication

Foojay authors submit articles as a pull request that adds a folder to `draft/`.
A maintainer reviews the article and gives it a publication date. This task
covers that second half: take a reviewed draft, date it, and move it into
`content/posts/`.

A draft is a page bundle: a folder named for the article's URL slug, holding an
`index.md` (or `index.adoc`), plus every image the article uses. The frontmatter
carries the metadata. `draft/README.md` documents the shape for authors, and
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

**1. Check the frontmatter.**

`jbang scripts/validate/Frontmatter.java` is the definition of correct, and it
runs on every PR. It requires `title`, `description`, `authors`, `image` and
`categories` on a draft, rejects an emoji in a title, and fails on a
`related_posts` entry whose slug matches no post.

Two things the validator does not check, so check them by eye:

* `categories`: at least one, and each one has to exist in
  `template/categories.md`.
* `related_posts`: four or more recent, genuinely related posts reads best. This
  is a house preference, not a rule that blocks a merge. Find related posts if none are included.

Make sure the author profile exists.

**2. Set the date.**

Write the day only, with no time:

```yaml
date: "2026-10-03"
```

A time on a future date fails the PR check. Every post goes out at the daily
scheduled build rather than at the time in the field, so a time later than the
build silently holds the article back past its own date, and an earlier one
changes nothing.

**3. Move the bundle.**

```
git mv draft/SLUG content/posts/YYYY/MM/DD/SLUG
```

The `YYYY/MM/DD` in the path has to match the `date` in the frontmatter. The
check fails the PR when they disagree, because Hugo publishes off the date and
the folder is the cosmetic half. Use `git mv` so the move stays a rename in the
history rather than a delete plus an add.

**4. Resize the images.**

```
python3 .claude/resize_images.py --path content/posts/YYYY/MM/DD/SLUG
```

Run this after the move, on the new path. It caps the long edge at 1600px,
converts a PNG over 100 KB to JPEG and an animated GIF to WebP, and gives an
animated hero a still poster. The conversions rename files, so the script
rewrites the references in `index.md` and `index.adoc` itself. Add `--dry-run`
first to see what it would touch.

The validator fails a PR on an image over 4 MB, which is the backstop this step
exists to stay clear of.

**5. Verify.**

```
jbang scripts/validate/Frontmatter.java
```

Expect `Frontmatter check passed`. The lines starting with `~` are advisory and
span the whole archive, so read the ones naming this post and ignore the rest.

**6. Review the content.**

Review the content of the post for spelling mistakes, broken links, and other errors. The validator does not check the body of the post, ask the website maintainer for answers if something is not clear.

Avoid too promotional posts, and make sure the content is relevant to the audience. If you find any issues, fix them or ask how to be handled before proceeding.

**7. Transcript.**

If the post is a podcast and/or contains a YouTube embed, run the transcript script to generate a `transcript.md` in the post bundle. This is optional, but recommended for accessibility and SEO.

**8. Commit to `main`.**

A message in the shape of `New post from draft: SLUG` matches what the history
already uses. Publishing needs nothing further from you.
`publish-scheduled.yml` checks twice a day, at 07:07 and 14:07 Brussels time,
whether a post has come due, and asks `build-deploy.yml` to deploy when one
has.
