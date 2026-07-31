# Contributing a post

The full, up-to-date guide for writing and submitting an article lives on the
site itself:

**→ [How to submit your next article on foojay.io](https://foojayio.github.io/website/today/how-to-submit-your-next-article-on-foojay-io/)**

It walks through creating your author profile, preparing your post folder, adding
images, and delivering it (pull request, fork, or zip).

## In short

- Posts are contributed via pull request (fork the repo if you don't have write access).
- Each post is a folder under `content/posts/<year>/<month>/<day>/<your-slug>/`
  with the text in `index.md` and its images in the same folder. Copy
  `template/index.md` as your starting point.
- New author? Add yourself under `content/authors/<first-letter>/<your-slug>/`
  in the same PR.
- Preview locally with `hugo server -D`, then check
  `http://localhost:1313/today/<your-slug>/` before opening the PR. The PR check
  validates your frontmatter and builds the site — fix anything it flags.

See the guide linked above for the details.
