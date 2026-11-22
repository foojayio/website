# Maintaining foojay.io

Operational notes for people who run the site, rather than write for it. If you
came here to publish an article, you want [`README.md`](README.md) and
[`CONTRIBUTING.md`](CONTRIBUTING.md) instead.

Four other documents carry the detail this one only points at:

| Document | What it covers |
|---|---|
| [`scripts/README.md`](scripts/README.md) | Every script, grouped by whether it survives cutover |
| [`worker/views/README.md`](worker/views/README.md) | The read counter: Worker + D1 setup and deploy |
| [`REDIRECTS.md`](REDIRECTS.md) | The live Cloudflare redirect rules carrying old URLs to their Hugo equivalents |
| [`AGENTS.md`](AGENTS.md) | Why things are built the way they are — read before changing a convention |

## Repository layout

| Path | What it holds | Edited by |
|------|---------------|-----------|
| `content/posts/` | 2,147 articles, one folder per post at `<year>/<month>/<day>/<slug>/index.md`. The date buckets are for browsability only — the URL is `/today/<slug>/`, taken from the **folder name**. | authors, by PR |
| `content/authors/` | 344 author profiles, **flat**, one folder each at `<slug>/_index.md` (note the underscore: a profile is a Hugo *section*, so it can paginate). URL: `/today/author/<slug>/`. | authors, by PR |
| `content/pedia/` | 47 glossary entries, one `<slug>.md` each. | maintainers |
| `content/pages/` | 53 standalone pages — About, the Java Quick Start tree, the board, `/calendar/`, `/jugs/`. Each pins its legacy URL with an explicit `url:`. | maintainers |
| `content/sponsors/` | 7 sponsor profiles at `<slug>/_index.md`. | maintainers |
| `draft/` | Author submissions awaiting review. **Outside `content/`, so nothing here is ever published** — that is the whole point. | authors, by PR |
| `template/` | Starter files for every content type. See [`template/README.md`](template/README.md). | — |
| `data/` | Generated feeds plus hand-added events — see below. | mostly generated |
| `themes/foojay/` | The theme: layouts, partials, CSS, JS. | maintainers |
| `scripts/` | jbang/Java tooling, grouped by lifetime. | maintainers |
| `worker/views/` | The read counter (Cloudflare Worker + D1). | maintainers |

## Moderating submissions

An article arrives as a pull request on
[foojayio/website](https://github.com/foojayio/website/pulls) that adds a
folder under `draft/<slug>/`. Moderating is two separate steps, and keeping
them apart is what keeps the author's life easy: **merge the PR, then schedule
the draft.** Nothing is published by the merge, because `draft/` is outside
`content/`.

### 1. Review and merge the pull request

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

### 2. Schedule the merged drafts with the skills

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

### 3. After scheduling

`publish-scheduled.yml` checks twice a day whether a post has come due and
dispatches the deploy, so nothing further is needed. A scheduled post shows
under "Coming soon" on the home page until its day. If one does not appear on
its date, look at that workflow's run before touching the post: its log says
whether anything was due.

## Generated files — never hand-edit these

Each is overwritten on a schedule, so a hand edit survives at most a day. Fix
the **source** instead.

| File | Regenerated by | Fix it here |
|------|----------------|-------------|
| `data/jugs.yaml` | `fetch/Jugs.java` | [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs) |
| `data/java-champions.yaml` | `fetch/JavaChampions.java` | [aalmiray/java-champions](https://github.com/aalmiray/java-champions) |
| `data/java-conferences.yaml` | `fetch/JavaConferences.java` | [javaconferences.org](https://javaconferences.org/) |
| `data/jug-events.json` | `fetch/JugEvents.java` | the JUG's own calendar feed, via GlobalWWJugs |
| `data/views.json` | `fetch/ViewCounts.java` | the counter itself |
| `data/legacy-views.json` | `transfer/LegacyViews.java` | — kept in the repo because it is the **only** copy; the numbers vanish with WordPress |

`data/events/<slug>.yaml` is the exception: one hand-written file per
conference, added by pull request. It is a folder rather than a list file so two
people adding two events never touch the same bytes, and it is named
`jug-events.json` next door precisely because `data/events.json` plus a
`data/events/` folder would collide on one `hugo.Data` key.

## Running it locally

```bash
hugo server            # http://localhost:1313/website/
```

Two things that surprise people:

- **`draft/` is not mounted**, so `hugo server -D` does *not* preview a
  submission. To look at one, copy the folder to
  `content/posts/<year>/<month>/<day>/<slug>/` — which is also exactly how it
  gets published.
- **Search needs a real build.** Pagefind indexes the built output, so the
  search box does nothing under `hugo server`. Use `./test.sh`, which builds
  with a localhost `--baseURL`, runs Pagefind and serves the result. The
  `--baseURL` override is not optional: `hugo.toml` carries the deploy URL with
  its `/website/` subpath, and `npx serve` serves from the root.

Requires [Hugo extended](https://gohugo.io/) 0.165+, plus
[JBang](https://www.jbang.dev/) and Java 21+ for the scripts, and Node for
Pagefind.

## Workflows

| Workflow | Trigger | Does |
|---|---|---|
| `build-deploy.yml` | push to `main` | refreshes the generated `data/*`, commits them back with `[skip ci]`, builds, runs Pagefind, deploys to Pages |
| `sync-external-content.yml` | daily | JUG list, Java Champions, Java conferences, JUG events |
| `sync-view-counts.yml` | 4×/day | read counts — the one thing that moves continuously |
| `pr-check.yml` | pull requests | `validate/Frontmatter.java` + a full build (GitHub Pages has no per-PR preview URL, so this is the review signal) |

`build-deploy.yml` checks out with `fetch-depth: 0`. That is load-bearing:
`enableGitInfo` dates pages that carry no `date:` from their last commit, and a
shallow clone makes every page claim the same one.

## Comments

[giscus](https://giscus.app) stores each post's thread as a **GitHub Discussion
on this repo** — no database, no third-party account, moderation with the tools
the team already has. `partials/comments.html` renders nothing until
`[params.giscus]` in `hugo.toml` has `repoId`/`categoryId`, so an unconfigured
build simply has no comment section.

One-time setup — **already done**: Discussions are enabled, the **Blog
Comments** category exists and accepts comments (not announcement-style), the
[giscus app](https://github.com/apps/giscus) is installed, and both ids are in
`hugo.toml`. Get the ids from [giscus.app](https://giscus.app) if they ever need
re-reading.

A thread is keyed on the **post slug**, never the pathname, because the site
serves `/website/today/<slug>/` today and `/today/<slug>/` after cutover — every
pathname-keyed thread would be orphaned that day. Same reasoning and same key
shape as the read counter. Change the mapping and existing threads
are orphaned: a mismatch shows up as an empty comment section, not an error.

The 580 legacy WordPress comments were **imported into these Discussions** once,
in the shape giscus expects, so they appear in the widget like any other
thread. Don't re-import them: a fast first run got the posting account
**banned by GitHub** as spam, and the slow rerun only succeeded because it
waited between writes. The script is deleted; read it from git history
(`scripts/migrate/Discussions.java`) if you ever need to repeat the approach.

Imported text must not contain a live `@`: that script escaped Markdown but not
`@`, so `@Mihai` and even `@NotNull` pinged real GitHub accounts, and 17
comments had to be edited by hand afterwards. A WordPress commenter's name
matches no GitHub user, so escape every `@`.

## Read counter

The `12,345 views` line on posts, pages, glossary entries and author profiles
comes from a Cloudflare Worker over a D1 table at `foojay.io/api/views/*`, not
from a hosted analytics service. Three reasons, and all three have to hold:

- **Privacy by construction.** The Worker receives a slug and stores a slug and
  an integer. No cookie, no identifier, no IP, no user agent — nothing to
  anonymise, because nothing is collected.
- **First-party, so the number is true.** A third-party analytics domain is
  blocked for a large share of an audience of Java developers, and a *published*
  number that silently runs tens of percent low is the one failure mode it
  cannot have.
- **One number, not two.** The WordPress counts load in as the `legacy` column
  and live views accumulate in `live`; the API returns the sum. So no template
  ever adds a legacy count to a live one.

Counting and displaying are deliberately separate: `partials/views-beacon.html`
posts the slug and renders nothing unless `[params.views] endpoint` is set (so a
local build never counts), while `partials/views.html` reads `data/views.json`,
baked into the HTML at build time. A few hours stale is not a defect in a view
count, and `fetch/ViewCounts.java` never fails a build — if the counter is
unreachable it keeps the committed file.

`partials/views-key.html` is the single definition of what gets counted and
under which key. **Add a section there and it starts being counted**; nothing
else changes.

Setup and deploy: [`worker/views/README.md`](worker/views/README.md). **Not yet
deployed** — until it is, `data/views.json` holds the imported WordPress
numbers, which is why the counts are already on the site.

Redirects from old WordPress/foojay URLs are documented in
[`REDIRECTS.md`](REDIRECTS.md). `AGENTS.md`'s "Known gaps" section carries
detail on the rest of what cutover touched (security headers, `baseURL`, the
read counter).
