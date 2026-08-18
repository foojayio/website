# Starter templates

Everything you need to start a new piece of content, in one place. Copy the
file you need, fill it in, open a pull request.

| File | Copy it to | For |
|------|------------|-----|
| `author.md` | `content/authors/<first-letter>/<your-slug>/index.md` | **Your author profile** — needed once, before your first article. |
| `post.md` | `draft/<your-article-slug>/index.md` | **An article.** Start here — rename it to `index.md`. |
| `categories.md` | *(nowhere — read it)* | The list of categories already in use. Reuse one where it fits. |
| `page.md` | `content/pages/...` | A static site page. **Maintainers only**, not article contributors. |

Full walkthrough: [How To Submit Your Next Article On Foojay.io](https://foojay.io/today/how-to-submit-your-next-article-on-foojay-io/).

## Why there's no `archetypes/` folder

Hugo's `archetypes/` exists to feed `hugo new content ...`. Nothing here uses
that command — contributors copy a file and open a PR — so keeping a second set
of starter files under a Hugo-specific name meant two places to look and two
places to keep in step. These templates are the single source now.
