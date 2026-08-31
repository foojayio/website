# Legacy redirects that Hugo cannot express

Everything WordPress's Redirection plugin serves today has been carried into
this repo, but it went to **two different places**, and this file is the second
one.

> **The plugin's table is not the whole list.** It records the redirects somebody
> *added*; it says nothing about the URLs WordPress serves by virtue of being
> WordPress. Two families of those exist, both 200 on the live site today, both
> absent from every sitemap comparison because Yoast lists only the canonical
> form — the **hierarchical category paths** and the **`/feed/` URLs**. They are
> in "More rules" below, and they are the reason this file is worth re-reading
> rather than assuming the plugin export covered it.

- **86 of the 89 concrete rules are `aliases:` in `content/`.** They are
  per-URL, so Hugo emits a redirect page for each and they work on GitHub Pages
  during the trial as well as after cutover. Nothing has to be configured
  anywhere for those, and nothing can be forgotten. 62 were added from the
  plugin export (`aliases:` blocks carrying a "From WordPress's Redirection
  plugin table" comment); the rest were already there or were found earlier by
  crawling `content/` for links that 404'd.

- **3 are regular expressions, which `aliases:` has no way to express.** They
  are below. They must be configured on the host **before cutover**, or the
  URLs they cover start returning 404 on the day foojay.io stops being
  WordPress.

The three carry **312,531 recorded hits between them** — more than every
concrete rule combined — so this is not a tidy-up item.

Rules **4 and 5** below are the two families the plugin export could not tell us
about; they are host config for the same reason.

## The rules

| Pattern | Destination | Hits | Why a rule and not aliases |
|---|---|---|---|
| `^/blog/(.*)` | `/today/$1` | 209,365 | foojay's original URL scheme. Matches posts **and** `/blog/author/…`, `/blog/category/…`, `/blog/page/2/` and the feeds. Per-post aliases would cover only the posts, and would mean an `aliases:` line in 2,147 files to do a worse job than one rule. |
| `^/almanac/(jdk\|java)-([0-9+])` | `https://javaalmanac.io/jdk/$2` | 102,636 | Off-site. The Java Almanac was never part of foojay's own content; `/almanac/` itself is a concrete rule to the same place. |
| `^/docs/(.*)` | `/today/` | 530 | A retired docs section; everything under it collapses to the article index. |

Note the plugin matches **case-insensitively** and ignores a trailing slash
(`flag_case: false`, `flag_trailing: false`), so the replacements should too.

## Cloudflare

foojay.io is already on Cloudflare DNS, so this is a Redirect Rule (Rules →
Redirect Rules → Create), one per row, `301` / permanent:

```
> **Body links no longer depend on rule 1 or 3.**
> `HtmlToMarkdown.normalizeLegacyUrls` applies all three of these at scrape time, so
> a post stored in `content/` links to `/today/…` directly. These rules are for
> INBOUND traffic — the 209,365 hits arriving from other sites, search results and
> bookmarks — which is what makes them worth configuring, not our own markup.

# 1. the old blog scheme
When:  (starts_with(http.request.uri.path, "/blog/"))
Then:  concat("/today/", substring(http.request.uri.path, 6))     dynamic, 301

# 2. the almanac
When:  (http.request.uri.path matches "^/almanac/(jdk|java)-([0-9]+)")
Then:  regex_replace(http.request.uri.path, "^/almanac/(jdk|java)-([0-9]+).*$", "https://javaalmanac.io/jdk/${2}")
                                                                  dynamic, 301

# 3. the retired docs section
When:  (starts_with(http.request.uri.path, "/docs/"))
Then:  "/today/"                                                  static, 301
```

Rule 1 must be evaluated **before** any catch-all, and none of the three should
fire for `/today/…` itself.

## More rules: what WordPress serves without being told to

Neither family below is in the plugin export, because neither was ever
configured — WordPress serves them because it is WordPress. Both 200 on the live
site today, and both 404 the moment it stops being WordPress.

### 4. Category paths are HIERARCHICAL there and flat here

WordPress categories nest, and **Yoast canonicalises to the nested form**: the
indexed URL is `/today/category/tools/maven/`, and `/today/category/maven/`
declares *it* as canonical. Hugo's taxonomy is flat, so every one of those is a
404 after cutover — **55 URLs**, plus their `page/N/` and `feed/` variants.

41 of them differ only by the parent segment, so one rule covers the lot:

```
# 4a. drop the parent category segment
When:  (http.request.uri.path matches "^/today/category/[^/]+/(?!page/|feed/)[^/]+/")
Then:  regex_replace(http.request.uri.path, "^/today/category/[^/]+/", "/today/category/")
                                                                  dynamic, 301
```

The `(?!page/|feed/)` is load-bearing and is the whole trap: without it,
`/today/category/tools/feed/` rewrites to `/today/category/feed/` and
`/today/category/java/page/2/` to `/today/category/page/2/` — the rule would
break two URL shapes that work today in the course of fixing a third. Because
the replacement only strips a prefix, a pager or feed under a *nested* category
(`/today/category/tools/maven/page/2/`) still lands correctly on
`/today/category/maven/page/2/`.

The remaining 14 are renames — the slug itself changed, so no pattern can derive
them and each is its own rule (or one rule with a lookup map):

| WordPress | here |
|---|---|
| `/today/category/ai-ml/` | `/ai/` |
| `/today/category/books/book-reviews/` | `/today/category/book-review/` |
| `/today/category/game/` | `/today/category/game-development/` |
| `/today/category/interview/` | `/today/category/interviews/` |
| `/today/category/jakartaee/` | `/today/category/jakarta-ee/` |
| `/today/category/survey/` | `/today/category/surveys/` |
| `/today/category/tools/cassandra/` | `/today/category/apache-cassandra/` |
| `/today/category/tools/deepnetts/` | `/today/category/deep-netts/` |
| `/today/category/tools/idea/` | `/today/category/intellij-idea/` |
| `/today/category/tools/pulsar/` | `/today/category/apache-pulsar/` |
| `/today/category/tools/tomcat/` | `/today/category/apache-tomcat/` |
| `/today/category/tools/vscode/` | `/today/category/vs-code/` |
| `/today/category/tutorial/` | `/today/category/tutorials/` |
| `/today/category/uncategorized/` | `/today/` |

`ai-ml` is the one worth a second look: it is WordPress's **"Machine Learning"**
category, so the literal equivalent is `/today/category/machine-learning/`. It
points at `/ai/` instead because that page renders *exactly that category*
(`list_category: "Machine Learning"` — the same 66 articles) with an editorial
introduction on top, so it is the same post set on the better page. Repoint it
at the term page if the portal ever stops tracking the category.

These 14 must be ordered **before** rule 4a, or 4a strips the parent off the six
`tools/…` ones first and lands them on a term that does not exist.

### 5. RSS lives at `/feed/`, not `/index.xml`

WordPress serves a feed at `/feed/` and at `<any archive>/feed/`; Hugo serves
`/index.xml`. Every existing subscriber — feed reader, aggregator, newsletter
tool, planet — silently 404s at cutover, and the traffic is invisible in advance
because a feed reader is not a page view.

**These cannot be `aliases:`.** A Hugo alias is an HTML page carrying a
`<meta http-equiv="refresh">`, which a browser follows and a feed reader does
not — it would hand every subscriber a page of HTML where XML belongs, which is
worse than a 404 because it looks like a working response. They have to be real
301s on the host.

```
# 5. every WordPress feed URL -> its Hugo equivalent
When:  (http.request.uri.path matches "^(/.*)?/feed/?$")
Then:  regex_replace(http.request.uri.path, "^(.*?)/feed/?$", "${1}/index.xml")
                                                                  dynamic, 301
```

That single rule covers `/feed/` -> `/index.xml`, `/today/feed/`,
`/today/author/<slug>/feed/` and `/today/category/<term>/feed/`, because Hugo
emits `index.xml` beside every one of those pages. Two notes:

- It must be evaluated **after** rules 1 and 4, so `/blog/feed/` and
  `/today/category/tools/maven/feed/` are normalised first.
- `/comments/feed/` is the one that has no equivalent — there is no site-wide
  comment feed here — so let it fall through to the 404 rather than aiming it
  at something that is not what it claims.

## Verifying after cutover

```sh
for u in /blog/log4j-cve/ /blog/author/hirt/ /blog/category/java/ \
         /almanac/jdk-17 /almanac/java-8 /docs/anything/ \
         /today/category/tools/maven/ /today/category/tools/maven/page/2/ \
         /today/category/jeps/records/ /today/category/tools/vscode/ \
         /today/category/ai-ml/ /today/category/tutorial/ \
         /feed/ /today/feed/ /today/author/frankdelporte/feed/ \
         /today/category/java/feed/ /today/category/tools/maven/feed/ ; do
  printf '%-42s ' "$u"
  curl -s -o /dev/null -w '%{http_code} -> %{redirect_url}\n' "https://foojay.io$u"
done
```

The two that must **not** move are worth checking in the same pass, since rule
4a is written to leave them alone and a wrong `(?!…)` is invisible otherwise:

```sh
for u in /today/category/java/page/2/ /today/category/tools/ ; do
  printf '%-42s ' "$u"
  curl -s -o /dev/null -w '%{http_code} (expect 200)\n' "https://foojay.io$u"
done
```

Each should answer `301` with the destination from the table. Run the same
command against the live WordPress site first if you want the expected output —
it is what these rules are copied from.

## What was deliberately NOT carried over

17 plugin rules point at pages that **404 on the live WordPress site too** — the
rule outlived its target. Recreating them here would mint a redirect to a
missing page, which is worse than a 404 for both readers and crawlers. They are
listed here so nobody has to rediscover that they were skipped on purpose:

- `/command-line-arguments/` and its six `openjdk-NN-command-line-arguments`
  variants (3,725 hits) — the whole section is gone from WordPress.
- `/cli` (6 hits) — same target.
- The five China JUG aliases (`/china/`, `/china-jug/`, `/jugchina/`,
  `/jugs-china/`, `/jugs/china-jug/`, `/china-java-user-group/`, 1,529 hits)
  — they chain to `/jugs/china/`, which does not exist. Per-JUG pages are not a
  thing on either site; `/jugs/` is one directory page built from
  `data/jugs.yaml`.
- `/foojay-day-live/` and `/foojaydaylive/` (2 hits) — chain to
  `/foojayday2022live/`, gone.
- `/java-learning-trail/learn-more-on-foojay/` (0 hits) — gone.

One rule in the export is **disabled** (`/calendar/` → `/all-events/`) and was
skipped for that reason; this site resolves that pair the other way round
anyway (see the calendar note in `CLAUDE.md`).
