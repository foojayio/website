# Draft articles

Author submissions land here before they're published. To contribute, add a
folder named with your article's URL slug, containing an `index.md` (start from
`template/post.md`, renamed to `index.md`) and any images:

    draft/
      your-article-slug/
        index.md
        an-image.png

Then open a pull request. A maintainer reviews it and moves the folder into
`content/posts/<year>/<month>/<day>/your-article-slug/` to publish it.

Publishing is **two** things, and the folder is only the visible half: set
`date:` in the frontmatter to the same day. Hugo publishes off the `date:`, so a
folder that says October over a frontmatter that still says August puts the
article eight weeks back in the archive rather than at the top — the PR check
catches that mismatch and tells you which one to change.

Dating a post in the **future** schedules it: it is not built, listed or
searchable until that day, and it shows up under "Coming soon" on the home page
in the meantime. Write the day only, with no time — every post publishes at the
one daily build (07:00 UTC), and a time can only delay it. See `template/post.md`.

See **How To Submit Your Next Article On Foojay.io** (/today/how-to-submit-your-next-article-on-foojay-io/)
for the full guide, and `template/` for the starter files. This folder is outside
`content/`, so drafts are not published until a maintainer moves them.
