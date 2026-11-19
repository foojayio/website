# Accessibility: alt text and transcripts

## Problems
- Thousands of images in `content/` have no description. Homepage cards often have blank alt text or just the category name.
- Every podcast post lacks a transcript.
- `/accessibility/` is the public statement and must stay honest.

## Plan
1. **Cards first (cheap and visible):** make the card template emit `alt=""` for purely decorative thumbnails (the title is already in the link). Derive this, no author change. Verify with a browser or an axe run.
2. **Measure:** have `Frontmatter.checkImageAltText` print the count of missing alt texts per year, so progress is visible. It keeps warning and does not fail (see `AGENTS.md`, accessibility).
3. **New posts:** keep `pr-check.yml`'s `--changed-since` so authors see their own warnings only.
4. **Backlog:** batch by year, newest first. Draft alt text with an LLM, review by a human, and write via a script that is safe to re-run (idempotency rule). Do not use `alt="image"`.
5. **Podcasts:** add an optional `transcript:` section to the podcast template and show "Transcript not available" only on posts without one.
6. Update `/accessibility/`'s "not there yet" list as each item closes.

## Done when
- No card image is announced as a redundant duplicate.
- The per-year count of missing alt texts is reported in CI and trending down.
