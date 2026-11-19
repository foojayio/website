# Beginner path

## Problems
- "Getting started with Java" sits near the bottom of the homepage, after events and categories.
- The Quick Start content is good but nested three menu levels deep.

## Plan
1. Add a "New to Java?" block above the fold on the homepage, linking to `/java-quick-start/` (see `homepage.md`, step 1).
2. Make the block derive from the series: use the `partials/series-steps.html` definition so the step count and first step title come from the content, not from a copy.
3. Flatten the menu (see `navigation.md`, step 6).
4. After each tutorial step, check that the series navigation still renders (pages with a `weight` only).

## Done when
- A first-time visitor reaches "Install Java" or step 1 of the tutorial in one click from the homepage.
