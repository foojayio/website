# Navigation

Checked against https://foojay.io on 2026-11-19 (raw HTML and redirect probes).

## Current state

**Header menu**
- **News** (7): Daily News, Podcast, Event Calendar, JC-AI Newsletter, Monthly Review, Week in Review, All Categories.
- **Resources** (7): Sustainability for Java Developers, Java Almanac, JVM Options, Java User Groups, Java Conferences, Java Champions, Foojay Television.
- **Java Basics** (4 children, 3 levels deep): Java Terms Explained, Other Tutorials, Install Java (5 children), Quick Start Tutorial (11 children).
- **About** (7): What is Foojay?, Where to Find Foojay, Authors, Meet the Team, Our Sponsors, Advisory Board, Foojay Statistics.
- Utility links: Join our Slack, Write for Foojay.

**Footer**
- **Content Hub:** Friends Of OpenJDK Today, Podcast, Week in Review, Events Calendar, Java User Groups, Where to Find Foojay, Write for Foojay.
- **Java Resources**, **About Foojay**, **Join the community** and a bottom bar (Search, Sitemap, Terms, Privacy, Accessibility, Code of Conduct, AI Instructions).

**URLs**
- The glossary lives at `/java-terms-explained/`, the monthly roundup at `/month-overview/`, the AI view at `/today/category/ai/` and the weekly reviews at `/today/week-review/`.
- `/pedia/…`, `/jvm-weekly/` and `/ai/` are alias stubs that point at those URLs.
- `/today/category/ai-ml/` takes two hops: Cloudflare 301 to `/ai/`, then the stub.
- `/pedia/<slug>/index.md` (the markdown output) returns 404.

## Problems
1. **The News entries overlap.** Daily News, Monthly Review, Week in Review and JC-AI Newsletter can't be told apart from their labels.
2. **Java Basics nests three levels.** Install Java and the Quick Start Tutorial hold their own sub-entries, so the beginner path sits deep in a hover panel.
3. **The top level is four items** (News, Resources, Java Basics, About). Events sit under News and Java User Groups and Conferences under Resources, so there is no obvious place to look for community and events.
4. **`ai-ml` takes two hops.** A redirect that ends in a meta-refresh stub is weaker for search engines than a single 301.
5. **The markdown URLs of glossary entries 404** under their old path.
6. **A label and its URL can still disagree.** "Daily News" points at `/today/`, which lists everything, not only daily items.

## Plan
1. **News:** group Monthly Review, Week in Review and JC-AI Newsletter in one dropdown group ("Newsletters and reviews"), or give them one landing page under `/today/`. Keep Daily News and Podcast as direct entries.
2. **Learn:** flatten to two levels: Install Java, Quick Start Tutorial, Java Terms Explained, Other Tutorials. The step lists stay on the tutorial pages, where `series-steps.html` already renders them.
3. **Top level:** decide whether Events and Community deserve their own entries or the four current ones are enough. If they do, move Event Calendar, Java User Groups, Java Conferences and Where to Find Foojay there.
4. **`ai-ml`:** point the Cloudflare rule at `/today/category/ai/`. It needs someone with Cloudflare access; `REDIRECTS.md` documents the rule and must change with it.
5. **Markdown URLs:** check whether anything links to `/pedia/<slug>/index.md`. If so, add a Cloudflare rule for `^/pedia/(.+)/index\.md$`.
6. **Label:** rename "Daily News" to "All Articles", or keep it if the name is a deliberate brand.

## Done when
- No top-level menu has more than six children and none goes deeper than two levels.
- A first-time visitor can predict the destination of every label (try it on two colleagues).
- Every old URL ends on a page in one hop (`BuiltSite.java`, `REDIRECTS.md` and the redirect e2e spec agree).
