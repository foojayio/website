---
name: schedule-multiple-posts
description: Schedule several drafts from /draft over the coming days and weeks, spreading them by author and topic, then comment on each delivering pull request. Use when asked to schedule "the posts in /draft", a batch of drafts, or "for the next weeks" or "for the next days".
---

# Schedule multiple draft posts

Batch version of `schedule-posts`: that skill dates and moves ONE draft and
holds the per-post steps (frontmatter, images, galleries, validation). This one
decides the calendar, runs those steps for each draft, then tells the authors.

## Ask first, if the request does not say

* Which drafts to leave out (the request usually names them; skip them).
* Limits: max posts per day, which weekdays are allowed. Defaults: 3 and Mon-Fri.
* Where the schedule starts. Default: the first weekday with a free slot after today.

## 1. Inventory

For every folder in `draft/` (ignore `README.md` and excluded ones) read the
frontmatter: `title`, `authors`, `categories`, `image`, `related_posts`.
Drafts missing `index.md` are not ready: report them, do not schedule them.

Find each draft's pull request. `gh` may fail inside the sandbox; the number is
also in the merge commit:

```
c=$(git log --diff-filter=A --format=%H -- draft/SLUG | tail -1)
git log --merges --ancestry-path --format='%s' $c..HEAD | tail -1
```

A "Merge branch ..." subject is not the PR: look the number up in the recent
`Merge pull request #N from ...` history by branch name. Verify before commenting.

## 2. Build the calendar

Count what is already scheduled, because the daily maximum includes it:

```
for d in content/posts/YYYY/MM/*; do echo "$d: $(ls $d | tr '\n' ' ')"; done
```

Fill the earliest free weekday slots, then apply the spacing rules:

* Never the same author twice in a day, and keep their posts well apart.
* Don't put two posts on the same topic (shared category such as AI, Performance,
  JEPs, Spring) on one day, nor on adjacent days where an alternative exists.
  Compare against posts already on that date too, not only the new ones.
* Spread rather than cram: leave a day with a free slot empty only when it
  improves the mix.

Show the table (date, post, author, PR) in your reply. The user's request is the
authorisation, so proceed unless something is ambiguous.

## 3. Per draft: apply `schedule-posts`

Follow its steps for each draft, with these batch notes:

* Set `date: "YYYY-MM-DD"` in the draft first, then `git mv draft/SLUG content/posts/Y/M/D/SLUG`.
* Strip frontmatter comments and the blank lines inside it; fix `image:` paths
  with a leading `/`.
* Check `categories` exist in `template/categories.md`; add `Mongo`, `Codename One`
  or `BoxLang` when the post is about it. Missing `related_posts`: add four
  existing, genuinely related posts, verifying each slug is a folder under `content/posts`.
* No `image:` in the frontmatter: run `generate_header.py` from `schedule-posts`
  (reuses a suitable bundle image, else renders a card) and say so in that PR's comment.
* Remove a body image that duplicates the hero.
* A hotlinked image is stored locally as `<stem>-<8 hex of sha1(url)>.<ext>`;
  `raw.githubusercontent.com` needs `allowed_domains` in the sandbox.
* Run `resize_images.py --path <new path>` per bundle, then gallery-ify body images.
* The folder date and the frontmatter `date` must agree: both come from the table.

## 4. Verify once

```
jbang scripts/validate/Frontmatter.java
hugo --quiet -d $TMPDIR/pub
```

Only problems naming the new posts matter; ignore pre-existing ones, and the
excluded draft folder. Say plainly that body text and links were not reviewed
unless you did.

## 5. Commit and push

Remove empty `.claude` dirs in subfolders (command in `schedule-posts`), then one
commit: `New posts from draft: schedule N posts for <range>`. Commit signing and
`git push` need the sandbox disabled (GPG key access).

## 6. Comment on each pull request

After the push, one comment per PR, with the scheduled date and the author profile
link `https://foojay.io/today/author/<author-slug>/` (the `/today/author/` pattern
comes from `hugo.toml` permalinks):

```
gh pr comment N --repo foojayio/website --body "Thanks NAME! Your article is scheduled for publication on **Thursday 19 November 2026**.

Your author profile: https://foojay.io/today/author/SLUG/"
```

For a **new or recent author**, add this paragraph after the profile link:

```
Please join our Foojay community on Slack if you haven't done this yet. We announce new posts on it once they are published, have chats about all-things-java, and more. https://foojay.io/today/join-slack-com-t-foojay-signup/
```

New or recent = no published post by them before the ones being scheduled, or
their first post is under 6 months old. Check by author slug, excluding the
drafts of this batch:

```
grep -rl --include=index.md --include=index.adoc -E '^\s*-\s*"?SLUG"?\s*$|authors: \[.*"SLUG"' content/posts | sort | head -3
```

The oldest folder date is their first post. Longstanding authors get no Slack paragraph.

`gh` needs the sandbox disabled (TLS). Take the first name from the author's
`content/authors/SLUG/_index.md` title. For a co-written post, comment once and
link every author.

## 7. Report

Table of date, post and PR comment link, what was changed beyond the date
(categories, related posts, images), and anything not done.
