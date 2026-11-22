# Moderating and publishing articles

For the people who review author submissions and schedule them for publication.
If you run the rest of the site (scripts, data feeds, workflows, the read
counter), see [`MAINTAINERS.md`](MAINTAINERS.md). If you came here to write an
article, you want [`CONTRIBUTING.md`](CONTRIBUTING.md).

An article arrives as a pull request on
[foojayio/website](https://github.com/foojayio/website/pulls) that adds a
folder under `draft/<slug>/`. Moderating is two separate steps, and keeping
them apart is what keeps the author's life easy: **merge the PR, then schedule
the draft.** Nothing is published by the merge, because `draft/` is outside
`content/`.

## 1. Review and merge the pull request

Work from the PR list, not from a clone. The PR template points authors at
`draft/README.md`, and `pr-check.yml` (`build-and-validate`) runs the
frontmatter validator plus a full build; its red output names the file and the
fix. `main` is protected by a ruleset (see `AGENTS.md`), so a PR needs that
check green before it merges.

Read the PR for what the validator cannot judge:

- **Relevant and not marketing.** Company articles are welcome when they are
  Java, OpenJDK or JVM oriented with working example code; a product
  description is declined.
- **Only the article and its images** are in the PR (`index.md` or `index.adoc`
  plus images). Example projects and videos belong in the author's own
  repository or on YouTube.
- **A real author.** A new author profile has a real photo (no logo) and a link
  we can verify them by. No company or team profiles.
- **Leave `date:` alone.** Don't ask the author to pick one; step 2 sets it.

Ask for changes in the PR when the article is not acceptable. For anything the
scheduling skills fix themselves (categories, related posts, image size, alt
text, frontmatter comments), **merge as is**: sending a first-time contributor
back for these is the tax `AGENTS.md` tells us to avoid. A draft that is not
ready stays in `draft/`, and you name it as excluded when you schedule.

## 2. Schedule the merged drafts with the skills

The skills live in `.claude/skills/` and run in Claude Code from the repository
root. They cover the second half of the pipeline: date the draft, move it into
`content/posts/<y>/<m>/<d>/<slug>/`, and finish the content.

| Skill | Use it when | Example request |
|---|---|---|
| `schedule-posts-single` | one draft, one date | `Schedule draft/my-slug for 2026-12-03` |
| `schedule-multiple-posts` | several drafts, spread over days or weeks | `Schedule the posts in /draft for the next weeks, except waiting-for-feedback-x` |
| `mastodon-account-moderation` | a sign-up on foojay.social, unrelated to articles | `Review the pending Mastodon registrations` |

What the article skills do for each draft: validate the frontmatter, check the
categories and `related_posts`, make sure the header image exists (generating
a card when it is missing), strip the template comments, set `date:` (a day,
**no time**), `git mv` the folder to match, resize images, turn body images
into galleries, write missing alt text and run
`jbang scripts/validate/Frontmatter.java`. `schedule-multiple-posts` also
builds the calendar (max 3 posts a day, Monday to Friday, never the same author
twice in a day, no two posts on one topic back to back), commits once as
`New posts from draft: schedule N posts for <range>`, and **comments on every
delivering PR** with the date and the author's profile link, adding the Slack
invitation for a new author.

Before you run them:

- The drafts are merged: `git pull` on `main` first, since the skills read
  `draft/` and find each PR from the merge commit.
- You have `jbang` with Java 21+, an authenticated `gh`, and the image venv
  (`python3 -m venv ~/.venvs/foojay-images && ~/.venvs/foojay-images/bin/pip install Pillow`).
- Commit signing and `gh` can need the Claude Code sandbox disabled; the skills
  say when.

The skills do not review the article text. Skim the body for spelling, dead
links and promotion, and check the report each skill prints for anything it
could not do. The skill commits to `main`, which the ruleset lets an admin do
directly; with no bypass, open a PR for that commit instead.

## 3. After scheduling

`publish-scheduled.yml` checks twice a day whether a post has come due and
dispatches the deploy, so nothing further is needed. A scheduled post shows
under "Coming soon" on the home page until its day. If one does not appear on
its date, look at that workflow's run before touching the post: its log says
whether anything was due.
