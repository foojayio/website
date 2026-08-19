# Legacy redirects that Hugo cannot express

Everything WordPress's Redirection plugin serves today has been carried into
this repo, but it went to **two different places**, and this file is the second
one.

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

## Verifying after cutover

```sh
for u in /blog/log4j-cve/ /blog/author/hirt/ /blog/category/java/ \
         /almanac/jdk-17 /almanac/java-8 /docs/anything/ ; do
  printf '%-34s ' "$u"
  curl -s -o /dev/null -w '%{http_code} -> %{redirect_url}\n' "https://foojay.io$u"
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
