# Accessibility: alt text and transcripts

## Done
- **Cards:** post thumbnails already emit `alt=""` and are hidden from assistive tech. Board and sponsor logo tiles now take `decorative`, used where the link already names the organisation, so nothing is announced twice (372 duplicates on the built site before, 0 after).
- **Measure:** `Frontmatter.reportAltTextBacklog` prints undescribed images per year on every run, and writes a table to the job summary in CI.
- **New posts:** `pr-check.yml` keeps `--changed-since`; the `schedule-posts` skill has a "Describe every image" step.
- **Backlog:** every locally stored image in `content/` was viewed and described (Markdown images, `{{< img >}}` shortcodes, uncaptioned gallery lines, page and glossary images). 423 undescribed images became 50.
- **Podcasts:** `transcript.md` is fetched and rendered for 105 of 106 episodes; the one without says so on its page.
- **`/accessibility/`:** updated to say the descriptions are machine-written and about 50 are missing.

## Still open
1. **About 50 hotlinked images have no description** (Snyk, Google Drive, GitHub, Payara and others). They cannot be viewed here. Localise them first (see the images convention in `AGENTS.md`), then describe them; dead ones need an author or removal.
2. **Review the machine-written descriptions.** An AI model wrote all of them from the image; no person has checked them. About 20 early ones (pilot, 2025, 2026) came from the surrounding text because the file could not be opened, and the six animated gif/webp ones describe only the first frame. Sample a few per year before calling this closed.
3. **Three headings consist only of an image** (`## ![alt](x.png)`): `book-review-quarkus-for-spring-developers`, `five-apache-projects-you-probably-havent-heard-of-yet`, `building-formpilot-…`. Their anchor link wraps an image with an empty alt. Turn them into a text heading followed by the image.
4. **Linked figure grids** in `flutter-better-in-java` and `embedded-linux-ui-in-18-mb` are still raw HTML. A gallery would drop the per-image links to codenameone.com articles; decide whether that is acceptable.
5. **Transcripts are automatic captions**, which do not meet WCAG 1.2.2. Correcting them is manual work.
6. **Five oversized hero images** fail the PR check on touched posts (`indexing-all-of-wikipedia-on-a-laptop`, `new-video-series-javafx-in-action-part-1`, `windsurf-ai-java-code-quality`, `the-triforce-that-slays-legacy-java-myths-happy-40th-zelda`, `eclipse-glassfish-this-isnt-your-fathers-glassfish`). Unrelated to alt text, but they block a PR that touches those posts.

## Done when
- No card image is announced as a redundant duplicate. **Met.**
- The per-year count of missing alt texts is reported in CI and trending down. **Met, at 50.**
- Every image on the site has a description a person has seen. **Open: items 1 to 3.**
