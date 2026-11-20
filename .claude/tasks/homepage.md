# Homepage

## Problems
- The page is a news portal, learning site, event directory and sponsor showcase at once, and the hero copy is generic.
- The latest podcast appears three times (latest podcast, Foojay Podcasts, first feed card).
- Categories appear in three places (sidebar, "Most popular post categories", "All Categories" menu item), and the sidebar order looks arbitrary.
- The "Java" badge is on almost every card and says nothing.
- About ten sidebar modules sit before the footer.
- Featured Authors shows two names with no explanation.

## Plan
1. **Hero:** replace the generic line with a concrete one that names what a visitor gets (for example: daily Java news, OpenJDK data, tutorials) and gives two buttons, "Read the news" and "New to Java?".
2. **Remove duplicates:** keep one podcast block and drop the podcast card from the feed when it is already shown above. Keep one category block and delete the other.
3. **Category order:** sort by count, descending, and cap at a visible number with a link to all.
4. **Hide the "Java" badge** on cards in the template (derive it, no per-post change). Check that it is not a category used for filtering.
5. **Sidebar budget:** audit `themes/foojay/layouts/partials/sidebar.html`, choose at most five modules, and move the rest to their own pages.
6. **Featured Authors:** add one line ("Rotated monthly by the editors") next to the heading. The slugs stay in `hugo.toml`.

## Done when
- Each block appears once on the homepage.
- A visitor finds the purpose and the two main paths in the first screen.
- The sidebar has five or fewer modules.
