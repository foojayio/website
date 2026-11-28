# Redirect rules

Reference for the Cloudflare-side redirects that carry old foojay.io /
WordPress URLs to their Hugo equivalents. This is dashboard-only config — none
of it lives in this repo — so this file is the only record of what's actually
configured.

Everything the WordPress Redirection plugin served has been carried over, and
**88 of its 92 concrete rules are `aliases:` in `content/`** — per-URL, so Hugo
emits a redirect page for each, nothing to configure and nothing to forget.
What follows is only what an alias cannot do: three regexes from the plugin
export, plus two families the export never knew about.

**A third thing the export never knew about: `_wp_old_slug`.** Rename a post in
WordPress and it keeps the old slug in postmeta and redirects from it for ever,
with no plugin rule to show for it. Nine such URLs were live and would have
404'd here; they were added as `aliases:` (2026-09-21), found by diffing the
SQL dump against `content/`:

```sql
SELECT p.post_name, m.meta_value AS old_slug
FROM wp_postmeta m JOIN wp_posts p ON p.ID = m.post_id
WHERE m.meta_key = '_wp_old_slug';
```

Same lesson as rules 4 and 5 below: the plugin table lists redirects somebody
*added*, and every rename since is invisible in it. Two more rules (ids 106 and
107, the `commit-created…` chain) postdate the export and are aliases now as
well.

**Rules 4 and 5 were not in the plugin export**, which is why they were nearly
missed: it lists redirects somebody *added*, not the URLs WordPress serves by
virtue of being WordPress. They are invisible in a sitemap comparison too,
because Yoast lists only the canonical form.

**These are for INBOUND traffic, and that is the whole reason they matter.**
`HtmlToMarkdown.normalizeLegacyUrls` applies rules 1–3 at scrape time, so a post
stored in `content/` already links to `/today/…` — our own markup does not
depend on them. What does is the 312,531 hits arriving from other sites, search
results and bookmarks.

Cloudflare → Rules → Redirect Rules, ruleset "Foojay cutover redirects". All
five families answer `301`/permanent (flipped from an initial `302` window that
let a wrong rule be walked back before it got cached in browsers). The plugin
matches **case-insensitively and ignores a trailing slash** (`flag_case:
false`, `flag_trailing: false`), so the replacements do too.

```
# 1. the old blog scheme -- 209,365 hits, foojay's original URL scheme. Also
#    covers /blog/author/…, /blog/category/…, /blog/page/2/ and the feeds, which
#    per-post aliases could not.
When:  (starts_with(http.request.uri.path, "/blog/"))
Then:  concat("/today/", substring(http.request.uri.path, 6))     dynamic, 301

# 2. the almanac -- 102,636 hits. Off-site: never foojay's own content.
#    Only matches the versioned form (jdk-N / java-N); bare /almanac/ and
#    /almanac 404 rather than redirecting to javaalmanac.io's homepage -- a
#    known gap, left as-is by decision (won't fix).
When:  (http.request.uri.path matches "^/almanac/(jdk|java)-([0-9]+)")
Then:  regex_replace(http.request.uri.path, "^/almanac/(jdk|java)-([0-9]+).*$", "https://javaalmanac.io/jdk/${2}")
                                                                  dynamic, 301

# 3. the retired docs section -- 530 hits; everything under it collapses to the
#    article index.
When:  (starts_with(http.request.uri.path, "/docs/"))
Then:  "/today/"                                                  static, 301

# 4a. WP categories NEST and Yoast canonicalises to the nested form, so
#     /today/category/tools/maven/ is the INDEXED url while Hugo has only the
#     flat one -- 55 URLs plus their page/N/ and feed/ variants. 41 of them
#     differ only by the parent segment, so one rule covers the lot.
#     Cloudflare's regex engine has no negative lookahead, so the page/ and
#     feed/ exclusion is a second, negated condition rather than "(?!page/|feed/)".
When:  (http.request.uri.path matches "^/today/category/[^/]+/[^/]+/"
        and not http.request.uri.path matches "^/today/category/[^/]+/(page|feed)/")
Then:  regex_replace(http.request.uri.path, "^/today/category/[^/]+/", "/today/category/")
                                                                  dynamic, 301

# 5. every WordPress feed URL -> its Hugo equivalent. WP serves a feed at /feed/
#    and at <any archive>/feed/; Hugo serves index.xml beside every one of those
#    pages, so this single rule covers /feed/, /today/feed/,
#    /today/author/<slug>/feed/ and a category feed.
When:  (http.request.uri.path matches "^(/.*)?/feed/?$")
Then:  regex_replace(http.request.uri.path, "^(.*?)/feed/?$", "${1}/index.xml")
                                                                  dynamic, 301
```

**Order matters in three places.** Rule 1 before any catch-all, and none of 1–3
may fire for `/today/…` itself. Rule 4b (the 14 renames, below) sits **before**
4a, or 4a strips the parent off the six `tools/…` ones and lands them on a term
that does not exist. Rule 5 sits **after** 1 and 4, so `/blog/feed/` and
`/today/category/tools/maven/feed/` are normalised first.

**Three traps, each of which fails silently.**

- The `page/|feed/` exclusion in 4a is load-bearing (written as an `and not`
  condition, since Cloudflare has no negative lookahead): without it
  `/today/category/tools/feed/` rewrites to `/today/category/feed/` and
  `/today/category/java/page/2/` to `/today/category/page/2/` — breaking two URL
  shapes that work today in the course of fixing a third. Because the
  replacement only strips a prefix, a pager or feed under a *nested* category
  still lands correctly on `/today/category/maven/page/2/`.
- **A feed URL cannot be an `aliases:` entry even in principle.** A Hugo alias is
  an HTML page carrying `<meta http-equiv="refresh">`: a browser follows it, a
  feed reader does not, so every subscriber would get HTML where XML belongs —
  worse than a 404, because it looks like a working response.
- `/comments/feed/` has no equivalent — there is no site-wide comment feed here
  — so it falls through to the 404 rather than aiming it at something that is
  not what it claims. Rule 5 as built does catch it, sending it to
  `/comments/index.xml`, which 404s anyway; the outcome is the same status code
  by a worse route.

**The 14 nested-category URLs (rule 4b) are renames**: the slug itself changed,
so no pattern derives them and each needs its own rule. Both columns are under
`/today/category/` unless shown otherwise:

| WordPress | here |
|---|---|
| `ai-ml/` | `/ai/` — see below |
| `books/book-reviews/` | `book-review/` |
| `game/` | `game-development/` |
| `interview/` | `interviews/` |
| `jakartaee/` | `jakarta-ee/` |
| `survey/` | `surveys/` |
| `tools/cassandra/` | `apache-cassandra/` |
| `tools/deepnetts/` | `deep-netts/` |
| `tools/idea/` | `intellij-idea/` |
| `tools/pulsar/` | `apache-pulsar/` |
| `tools/tomcat/` | `apache-tomcat/` |
| `tools/vscode/` | `vs-code/` |
| `tutorial/` | `tutorials/` |
| `uncategorized/` | `/today/` |

`ai-ml` is WordPress's **"Machine Learning"** category, so the literal
equivalent is `/today/category/machine-learning/`. The live rule still
targets `/ai/`, which is now a Hugo alias of `/today/category/ai/`
(`content/categories/ai/_index.md`), so it lands there in two hops. Point the
rule at `/today/category/ai/` directly when someone with Cloudflare access can.

#### Rule 4b, the renames

**Position: FIRST in the Single Redirects list, above 4a and above 5.** That
position is load-bearing, not a preference. Redirect rules stop at the first
match, and 4a strips the parent segment off any nested category path, so with
4b second `tools/vscode/` becomes `/today/category/vscode/` and 404s before 4b
is ever consulted. Rule 5 has to stay below for the same reason, otherwise
`tutorial/feed/` turns into `/today/category/tutorial/index.xml`.

**Settings:** type `Dynamic`, status `301`, **Preserve query string ON**.
Regex needs the Business plan or above, which 4a already relies on.

**When incoming requests match:**

```
http.host eq "foojay.io" and http.request.uri.path matches "^/today/category/(ai-ml|books/book-reviews|game|interview|jakartaee|survey|tools/(cassandra|deepnetts|idea|pulsar|tomcat|vscode)|tutorial|uncategorized)(/.*)?$"
```

**Target URL, as an expression:**

```
concat("https://foojay.io", regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(regex_replace(http.request.uri.path, "^/today/category/ai-ml/?$", "/ai/"), "^/today/category/ai-ml/(.+)$", "/today/category/machine-learning/${1}"), "^/today/category/books/book-reviews(/.*)?$", "/today/category/book-review${1}"), "^/today/category/game(/.*)?$", "/today/category/game-development${1}"), "^/today/category/interview(/.*)?$", "/today/category/interviews${1}"), "^/today/category/jakartaee(/.*)?$", "/today/category/jakarta-ee${1}"), "^/today/category/survey(/.*)?$", "/today/category/surveys${1}"), "^/today/category/tools/cassandra(/.*)?$", "/today/category/apache-cassandra${1}"), "^/today/category/tools/deepnetts(/.*)?$", "/today/category/deep-netts${1}"), "^/today/category/tools/idea(/.*)?$", "/today/category/intellij-idea${1}"), "^/today/category/tools/pulsar(/.*)?$", "/today/category/apache-pulsar${1}"), "^/today/category/tools/tomcat(/.*)?$", "/today/category/apache-tomcat${1}"), "^/today/category/tools/vscode(/.*)?$", "/today/category/vs-code${1}"), "^/today/category/tutorial(/.*)?$", "/today/category/tutorials${1}"), "^/today/category/uncategorized(/.*)?$", "/today${1}"))
```

Fifteen `regex_replace` calls for fourteen renames, nested so each one feeds the
next. A path only ever matches one of them, and the order between them does not
matter, with one exception noted below.

**Three things in there that look odd and are deliberate:**

- **`(/.*)?$` carries the rest of the path through**, so `tutorial/page/3/`
  reaches `tutorials/page/3/` and `tutorial/feed/` reaches `tutorials/feed/`,
  which rule 5 then turns into `index.xml` on the client's second request.
- **It is also what stops a rewrite cascading.** `survey` → `surveys` cannot be
  re-matched by the `survey` pattern afterwards, because the group demands `/`
  or end-of-string and finds `s`. The same holds for `interview` and
  `tutorial`. Loosen those anchors and the chain eats its own output.
- **`ai-ml` gets two lines, and they are the one ordered pair.** The bare
  category goes to `/ai/`, see above. Anything deeper goes
  to `/today/category/machine-learning/`, the literal term page, because the AI
  term's `page/2/` and feed hold different posts. The bare-path line runs first so the subtree
  line cannot claim it.

### Verifying

Each should answer `301` with the destination shown above. Add `-L` and check
the final code, not just the hop, or a redirect to a 404 reads as a pass.

```sh
for u in /blog/log4j-cve/ /blog/author/hirt/ /blog/category/java/ \
         /almanac/jdk-17 /almanac/java-8 /docs/anything/ \
         /today/category/tools/maven/ /today/category/tools/maven/page/2/ \
         /today/category/jeps/records/ /today/category/tools/vscode/ \
         /today/category/ai-ml/ /today/category/tutorial/ \
         /feed/ /today/feed/ /today/author/frankdelporte/feed/ \
         /today/category/java/feed/ /today/category/tools/maven/feed/ ; do
  printf '%-42s ' "$u"
  curl -s -o /dev/null -w '%{http_code} -> %{redirect_url} ' "https://foojay.io$u"
  curl -sL -o /dev/null -w '(ends %{http_code})\n' "https://foojay.io$u"
done

# The two that must NOT move. 4a is written to leave them alone and a wrong
# negative lookahead is invisible otherwise -- both must stay 200.
for u in /today/category/java/page/2/ /today/category/tools/ ; do
  printf '%-42s ' "$u"
  curl -s -o /dev/null -w '%{http_code} (expect 200)\n' "https://foojay.io$u"
done
```

### What was deliberately NOT carried over

**17 plugin rules point at pages that 404 on the live WordPress site too** — the
rule outlived its target, and recreating one would mint a redirect to a missing
page, which is worse than a 404 for readers and crawlers alike. Recorded here so
nobody rediscovers that they were skipped on purpose: `/command-line-arguments/`
and its six `openjdk-NN-command-line-arguments` variants (3,725 hits) plus
`/cli` (6), the section being gone from WordPress; the six China JUG aliases
(`/china/`, `/china-jug/`, `/jugchina/`, `/jugs-china/`, `/jugs/china-jug/`,
`/china-java-user-group/`, 1,529 hits), which chain to `/jugs/china/` — per-JUG
pages exist on neither site, `/jugs/` being one directory page built from
`data/jugs.yaml`; `/foojay-day-live/` and `/foojaydaylive/` (2), which chain to
a gone `/foojayday2022live/`; and
`/java-learning-trail/learn-more-on-foojay/` (0).

One export rule is **disabled** (`/calendar/` → `/all-events/`) and was skipped
for that reason; this site resolves that pair the other way round anyway (see
the calendar note in `AGENTS.md`).

**`/almanac/` and `/almanac` (bare, no version) 404** rather than redirecting.
Decided not to fix — left as-is.
