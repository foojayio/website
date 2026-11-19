# Events and search

## Problems
- The events list mixes English, German, French and Spanish titles with no language markers ("Das war's? Nicht ganz:" has no context).
- The archive has about 2,400 posts, but search is one small box plus a category wall.

## Plan
### Events
1. JUG meetups come from `data/jug-events.json` (fetched), conferences from `data/events/<slug>.yaml` (hand-added).
2. For conferences, add an optional `language:` key with a default of `en`, and extend the closed key set in `Frontmatter.checkEvents` and `template/event.yaml`. This is case 2 in `AGENTS.md` (default it).
3. For JUG events, derive the language from the JUG record if the feed provides one. Otherwise show nothing rather than guess.
4. Render a small language badge only when the language is not `en`.

### Search
1. Pagefind is already live. Check whether its filter UI can be enabled: filter by category and year (derived from the build) and by content type (post, pedia, page).
2. Put search in the header on every page with an accessible label.
3. Add a "no results" state that suggests categories.

## Done when
- A non-English event is visibly marked and every event passes `checkEvents`.
- A visitor can narrow results by category and year.
