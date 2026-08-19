# foojay.io — a place for Friends Of OpenJDK

This repository **is** [foojay.io](https://foojay.io). Every article, author
profile and page on the site is a Markdown file in here, and the site rebuilds
and redeploys itself whenever something lands on `main`.

Which means the way to get published on foojay is to open a pull request.

## Write for foojay

foojay is written by the Java community, for the Java community: 2,100+ articles
from 340+ authors — JUG leads, Java Champions, library maintainers, and a lot of
people who simply worked something out and wrote it down.

**We would like that to include you.**

What we publish, roughly: anything a working Java developer would be glad to
find. Deep dives, release notes, benchmarks, "here is how I debugged this",
tooling walkthroughs, JUG and conference reports, opinion. Beginner-level is
definitely welcome — the [Java Quick Start](https://foojay.io/java-quick-start/) is some of
the most-read content on the site.

A few things worth knowing before you start:

- **You keep your byline.** Every article carries your name, photo, bio and
  links, and you get [an author page](https://foojay.io/today/author/) collecting
  everything you have written.
- **Already published it on your own blog? Post it here too.** Set `canonical:`
  to your original and search engines keep crediting your site, while your
  article reaches foojay's readers. Around 800 articles here are cross-posts.
- **No paywall, no ads in your article, no sign-up wall for readers.**
- **You write Markdown, not Hugo.** If you can write a `README`, you can publish
  here. Nothing in the build needs to be understood or configured by you.

### Publishing, in three steps

1. **Add yourself as an author** — copy [`template/author.md`](template/author.md)
   to `content/authors/<your-slug>/_index.md` and drop your photo beside it.
   Once, before your first article.
2. **Write the article** — copy [`template/post.md`](template/post.md) to
   `draft/<your-article-slug>/index.md`, put any images in that same folder, and
   write. The file's own comments explain every field; the required ones are
   `title`, `date`, `description`, `authors`, `image` (the card and social
   preview) and `categories` (pick from
   [`template/categories.md`](template/categories.md)).
3. **Open a pull request.** An automated check reads your frontmatter and builds
   the site, so you find out about a typo in an author slug or a missing image in
   a minute rather than after review. A maintainer then moves your folder into
   `content/posts/` and it is live.

No write access needed — fork the repo. If a pull request is not your thing at
all, the guide below covers sending a zip instead.

> **📖 Full walkthrough:
> [How To Submit Your Next Article On Foojay.io](https://foojay.io/today/how-to-submit-your-next-article-on-foojay-io/)**
>
> Screenshots and all the details: creating your profile, preparing images,
> frontmatter, and every way of delivering the post.

[`template/README.md`](template/README.md) is the index of starter files, and
[`CONTRIBUTING.md`](CONTRIBUTING.md) is the short version of the above.

## Other ways to contribute

- **Add a conference or workshop to [the calendar](https://foojay.io/calendar/)** —
  one small YAML file, copied from [`template/event.yaml`](template/event.yaml).
  See [`CONTRIBUTING.md`](CONTRIBUTING.md).
  *(A JUG's own meetups need nothing here — they sync automatically from the
  calendar your group already publishes.)*
- **Missing or wrong JUG?** That data is community-run and lives
  [upstream in the World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/_jugs).
  Fix it there and it flows into foojay — and into every other site reading the
  directory — at the next daily sync.
- **Spotted a typo in an article?** Every page's source is a file in
  `content/posts/`. Edit it on GitHub and open a PR; that is a perfectly good
  first contribution.
- **Java Champion entry out of date?** Also upstream, in
  [aalmiray/java-champions](https://github.com/aalmiray/java-champions).

## Questions

Ask in the [foojay Slack](https://bit.ly/join-foojay-slack) or open an
issue. If you are unsure whether an idea fits, propose it — the answer is
usually yes, and it is much easier to say so early than to review a finished
draft that needed a different shape.

## Running the site locally

Optional — you do not need this to contribute, since the PR check builds
everything for you.

```bash
hugo server     # http://localhost:1313/website/
```

Note that `draft/` is deliberately *not* built (that is what keeps unreviewed
submissions unpublished), so to preview your own article, copy its folder to
`content/posts/<year>/<month>/<day>/<your-slug>/` — the same move that publishes
it.

## Under the hood

A [Hugo](https://gohugo.io/) static site, deployed by GitHub Actions.
Search is [Pagefind](https://pagefind.app), comments are
[giscus](https://giscus.app) over GitHub Discussions, and the read counter is a
small first-party Cloudflare Worker.

Maintainers: [`MAINTAINERS.md`](MAINTAINERS.md) for how to run it,
[`scripts/README.md`](scripts/README.md) for the tooling, and
[`CLAUDE.md`](CLAUDE.md) for why any of it is built the way it is — worth reading
before changing a convention.
