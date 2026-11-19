# Stub and low-value pages

## Problems
Measured in `content/pages/`:
- `statistics.md` (292 bytes) and `ad-stats.md` (1,257 bytes): vanity and sponsor-sales material, not reader destinations.
- `sitemap.md` (249 bytes) and `coming-soon.md` (250 bytes): the second duplicates `/today/coming-soon/`.
- `ai-instructions.md` (10 KB) is written for crawlers and claims to be "authoritative".
- `where.md` carries a long WordPress-forum history in its frontmatter comment.

## Plan
1. For each page, check its views in `data/views.json` and `data/legacy-views.json` before deciding.
2. **Needs a decision from Frank:** keep `/statistics/` and `/ad-stats/`? If `ad-stats` is for sponsor sales, move the link out of reader-facing navigation and the footer.
3. Delete `coming-soon.md` and add its URL as an alias of `/today/coming-soon/`, with a matching Cloudflare check in `REDIRECTS.md` if needed.
4. Keep `sitemap.md` only if it is a human page that people use. Otherwise alias it to the home page. `sitemap.xml` is unaffected.
5. Review `ai-instructions.md` once for accuracy against the site, and mention its upkeep in the page itself. Shorten the `where.md` frontmatter comment to three lines (house style in `AGENTS.md`).
6. Run `BuiltSite.java` to confirm no broken internal links.

## Done when
- Every remaining page has a reason a reader would visit it.
- Removed URLs redirect.
