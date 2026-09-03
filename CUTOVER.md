# Cutover: putting the Hugo site live on foojay.io

Step-by-step runbook for replacing the WordPress site at foojay.io with this
Hugo site. Work top to bottom — **the order matters in several places**, and
each of those is called out where it applies.

Nothing here is reversible by itself, but the whole thing is: WordPress is left
running and reachable throughout, so the rollback is a DNS change (see
[Rollback](#rollback)).

Legend: **[BLOCKER]** must be done or decided before cutover day.
**[ORDER]** the surrounding steps must happen in the sequence given.

---

## Phase 0 — Before the switch

These are open items in the repo, not cutover mechanics. Each one is something
that silently gets worse or gets lost if cutover happens without it.

- [x] **[BLOCKER] Deploy the view counter Worker** — done 2026-08-24, weeks
      early as intended, and seeded the same day (2230 rows, 13.89M views).
      Every route verified against `foojay.io/api/views`; the WordPress bridge
      and its extra cron entry are deleted from `sync-view-counts.yml`.

      Two leftovers, neither blocking: it was deployed by IT rather than from
      this repo, so `worker/views/wrangler.toml`'s `database_id` is still the
      placeholder, and the D1 table holds one row from the smoke test
      (`posts/10-basic-questions-about-pdf-files-for-java-developers`, +1 view).

      The re-seed below is still required — see "Final view-count import".

- [x] **Comments are switched on.** `hugo.toml`'s `[params.giscus]` carries
      `repoId` and `categoryId`, Discussions are enabled on `foojayio/website`
      with the comment-accepting **Blog Comments** category, and the giscus app
      is installed — confirmed by giscus having created threads there on its own.
      Nothing left to do here. The legacy WordPress comments are a separate
      thing and are **not** imported into Discussions; see "Final comment
      archive" in the next phase.

- [ ] **Resolve the Ketch / Consent Mode question.** `partials/analytics.html`
      emits Google Consent Mode defaults of `denied` and depends on Ketch
      issuing a standard `gtag('consent', 'update', …)`. If Ketch's Google
      Consent Mode plugin is not enabled on the `foojay_io` property, GA4 goes
      to cookieless pings at cutover and the numbers collapse. Test in a
      **normal** browser window (a Firefox private window blocks
      `google-analytics.com` outright via Enhanced Tracking Protection and will
      look like consent gating): accept the banner, watch for `/g/collect` with
      `tid=G-GS21L12HYK`.

- [ ] **[BLOCKER] Pre-create the Cloudflare Redirect Rules** — the five
      families in [Redirect rules](#redirect-rules) below. `aliases:` cannot
      express a regex, and rules 1–3 alone carry **312,531 recorded hits**, more
      than every per-page alias combined. Doing it now rather than on the day is
      deliberate: every rule reproduces something the live site already serves,
      so creating them while WP is up changes nothing observable, and it takes
      the highest-traffic item off the cutover-day critical path.

- [ ] **Verify the domain on the GitHub org** —
  <https://github.com/organizations/foojayio/settings/pages> 
  - [X] Add domain in GitHub Foojay Settings Pages → `foojay.io`. 
  - [ ] Add a `TXT` record at `_github-pages-challenge-foojayio.foojay.io`. It prevents anyone else claiming the domain on GitHub Pages later; it does not affect serving, so
    it can be done any time before the switch.
  - [ ] Check if GitHub could verify the `TXT` record at https://github.com/organizations/foojayio/settings/pages

```
1. Create a TXT record in your DNS configuration for the following hostname: _github-pages-challenge-foojayio.foojay.io
2. Use this code for the value of the TXT record: d2b3d7045480075a08c5a52c5fd6a2
3. Wait until your DNS configuration changes. This could take up to 24 hours to propagate.
```

## Phase 1 — Final WordPress harvest

**Everything in this phase reads the live WordPress site, and every one of
those scripts hardcodes `https://foojay.io`** (`BASE_URL` / `WP_BASE` constants
in `scripts/transfer/*.java`, with no override flag). The moment DNS flips they
read the *Hugo* site instead — and they will produce empty or wrong output
rather than failing loudly. Finish this phase before touching DNS.

- [ ] **Re-scrape anything outstanding**, then re-run the repair passes that a
      re-scrape can undo:
      - `jbang scripts/cleanup/CloudflareEmails.java` — **this one cannot be
        re-run after cutover at all.** It repairs from the *live HTML*, because
        the stored files kept only Cloudflare's placeholder and the encoded copy
        was dropped at conversion. Once WordPress is gone the addresses are
        unrecoverable.
      - Re-check the 790 remaining cross-post `canonical:` URLs.
        `transfer/Posts.java` copies `link[rel=canonical]` through blindly, so a
        re-scrape puts back any dead one it finds — which is why the 48 already
        known dead are `frozen: true` (see CLAUDE.md). Freezing covers those 48;
        it does nothing for the 790 that are alive today and can die before
        cutover. Probe each URL four times across two HTTP clients and treat a
        403/410 flip-flop as a bot wall, not a deleted page.
      - Re-run the hero-image audit: `curl -o /dev/null -w '%{http_code}'` over
        every `image:` value starting with a scheme. 76 posts hotlink a hero and
        11 were already dead.

- [ ] **Final view-count import:**
      `VIEWS_SEED_TOKEN=... jbang scripts/transfer/LegacyViews.java --seed`.
      This is the last chance — the WordPress counts vanish with the site and
      `data/legacy-views.json` is the only copy.

- [ ] **Final comment archive:** `jbang scripts/transfer/Comments.java`, then
      commit whatever it changed. Run it again here even if it ran earlier, to
      pick up comments posted on WordPress in the meantime — **this is the last
      chance**, since the bodies have no other source once the site is off.
      Needs no credential. It rewrites a file only when that file's content
      changed, so a run with nothing new leaves an empty diff; check `git status`
      to see whether there was anything.

      It does **not** post to GitHub Discussions any more. It used to, and
      GitHub banned the account it posted as a few posts in — see the script's
      class comment. It writes `content/posts/**/comments.json` instead, which
      `partials/legacy-comments.html` renders under the giscus widget.

- [ ] **[ORDER] Delete the WordPress bridge from
      `.github/workflows/sync-view-counts.yml` — before or with the DNS switch,
      not after.** The step marked `BRIDGE, DELETE WITH THE REST OF transfer/ AT
      CUTOVER` runs `LegacyViews.java --write-views` daily at 03:50 UTC and
      **commits the result**. Pointed at the Hugo site it will write nonsense
      into `data/views.json` and push it. Delete the step and the `50 3 * * *`
      cron entry, leaving the six-hourly counter refresh.

---

## Phase 2 — The day before

- [ ] **Lower the TTL** on foojay.io's A/AAAA records and on
      `www.foojay.io` to 60 seconds, so a rollback propagates in a minute
      instead of a day.

- [ ] **Add `wordpress.foojay.io`** pointing at WP Engine's current IP, **DNS
      only (grey cloud)**, and confirm it actually serves the site *before* you
      need it. Two things bite here:
      - WP Engine will not serve a hostname that is not added to the install —
        add it in the WP Engine dashboard, or you get someone else's site or a
        cert error.
      - WordPress canonical-redirects to its configured `home`/`siteurl`, so
        `wordpress.foojay.io` may bounce straight back to `foojay.io`. If it
        does, that has to be fixed WP-side (or via a `HOME`/`SITEURL` override)
        or the backup is not actually readable.

- [ ] **Announce a freeze** on publishing to WordPress, so nothing is written
      after the final harvest that would then be lost.

---

## Phase 3 — Cutover day

**[ORDER] This entire phase is sequence-critical.** The awkward part is that
GitHub Pages and Cloudflare want opposite things:

- GitHub can only issue the HTTPS certificate for `foojay.io` if it can see the
  DNS resolving **to GitHub** — which means Cloudflare must be **DNS only**
  (grey cloud) at that moment.
- Cloudflare Workers routes and Redirect Rules only run on **proxied** (orange
  cloud) hostnames — so `/api/views/*` and the three legacy redirects need the
  proxy **on**.

So it is grey cloud first, then orange. foojay.io is proxied today (Cloudflare's
Email Address Obfuscation is a proxy-level feature and the WP HTML shows it), so
this is a change from the current state and back again. Keep the grey-cloud
window short: during it, the three Redirect Rules do not fire.

1. [ ] **Switch the web records to GitHub Pages, DNS only (grey cloud).**
       Leave every other record alone — see the DNS table below; touching `MX`
       or the `TXT` records breaks mail for the domain.

2. [ ] **Set the custom domain in Pages settings** —
       <https://github.com/foojayio/website/settings/pages> → *Custom domain* →
       `foojay.io` → Save. Note the repo has **no `CNAME` file** and does not
       need one: this repo deploys via GitHub Actions, where the Pages settings
       hold the domain rather than a file in the artifact.

3. [ ] **Wait for the certificate**, then tick **Enforce HTTPS**. This can take
       anything from a few minutes to a while; the checkbox stays greyed out
       until GitHub has issued the cert.

4. [ ] **[ORDER] Re-run the `Build and deploy` workflow.** This is the step
       that is easiest to miss and it is the one that actually flips the site to
       production. `build-deploy.yml` builds with
       `--baseURL "${{ steps.pages.outputs.base_url }}/"`, and that value comes
       from the Pages API — it only becomes `https://foojay.io` once the custom
       domain is set. Until the site is rebuilt, everything derived from baseURL
       is still in trial mode:
       - `noindex, nofollow` on every page and `Disallow: /` in `robots.txt`
       - **no analytics at all** (`partials/analytics.html` renders nothing on
         the trial deploy)
       - assets and links still carrying the `/website` path prefix

       Check the workflow log for the baseURL actually used. If it is not
       `https://foojay.io/`, hardcode it in `build-deploy.yml` rather than
       debugging the Pages API on the day.

5. [ ] **Turn the proxy back on (orange cloud)** for `foojay.io` and
       `www.foojay.io`, and set **SSL/TLS → Full (strict)**. Confirm the three
       Redirect Rules and the `/api/views/*` Worker route are live again.

6. [ ] **Purge the Cloudflare cache** (Caching → Configuration → Purge
       Everything). Otherwise cached WordPress HTML keeps being served over the
       new site for hours.

---

## Phase 4 — Verify, in this order

Fastest checks first, so a failure is caught before you have gone further.

- [ ] **The site is indexable.** `curl -s https://foojay.io/ | grep -i noindex`
      → no output. `curl -s https://foojay.io/robots.txt` → **not**
      `Disallow: /`, and the sitemap line present.
- [ ] **No trial URLs leaked.** `curl -s https://foojay.io/ | grep -c
      'foojayio.github.io\|/website/'` → `0`.
- [ ] **Analytics fires.** Load the site in a normal (non-private) window,
      accept the Ketch banner, confirm `/g/collect` with `tid=G-GS21L12HYK`.
- [ ] **The regex redirects work.** Run the verification loop in
      [Redirect rules](#redirect-rules) — `/blog/…`, `/almanac/jdk-17`,
      `/docs/…`, a nested category path, and `/feed/`. That loop also checks the
      two URLs rule 4a must *not* touch (`/today/category/java/page/2/` and
      `/today/category/tools/`), since a wrong negative lookahead breaks those
      silently.
- [ ] **Aliases work.** Spot-check a few of the 89 per-URL redirects and one of
      the three emoji-suffixed post URLs.
- [ ] **The view counter is counting.** `curl https://foojay.io/api/views/all`
      returns data, and a page view increments its key.
- [ ] **Comments load.** Two separate things on a post that has both (e.g.
      `/today/why-i-prefer-trunk-based-development/`, 12 archived comments):
      the giscus widget appears and can take a new comment, and the
      "Discussions on the previous Foojay site" section below it lists the
      archived ones. The archive is baked into the HTML, so if it is missing the
      build is at fault, not the network.
- [ ] **Search works** — `/search/?q=java`. Pagefind's index is built by the
      workflow (`npx -y pagefind --site public`), so this is the first time it
      is exercised against the real domain.
- [ ] **`www.foojay.io`** redirects to the apex over HTTPS.
- [ ] **Mail still works.** Send a test to `hello@foojay.io`.
- [ ] **Resubmit `sitemap.xml`** in Google Search Console and watch coverage
      over the following days.

---

## Phase 5 — After it has settled

Give it a week or two before deleting anything, and keep WordPress running and
paid for at least that long.

- [ ] **Delete `scripts/transfer/` and `scripts/cleanup/` entirely.** Both
      folders exist only to read or repair WordPress content, which is the
      question the `scripts/` layout is organised around — see `CLAUDE.md`.
      `scripts/fetch/`, `validate/` and `shared/` stay.
- [ ] **Simplify `sync-view-counts.yml`** back to a single six-hourly cron
      entry, now that the bridge step is gone.
- [ ] **Prune `CLAUDE.md`** of the sections describing scripts that no longer
      exist, and of the trial/`$isTrial` reasoning — though note the derivation
      itself is harmless once `baseURL` and `productionBaseURL` agree.
- [ ] **Retire WordPress**, keeping `data/legacy-views.json` (the only surviving
      copy of the WordPress view counts) and a final database/file backup.

---

## Redirect rules

Everything the WordPress Redirection plugin serves has been carried into the
repo, and **86 of its 89 concrete rules are `aliases:` in `content/`** — per-URL,
so Hugo emits a redirect page for each, nothing to configure and nothing to
forget. What follows is only what an alias cannot do: three regexes from the
plugin export, plus two families the export never knew about.

**Rules 4 and 5 were not in the export**, which is why they were missed the
first time round: it lists redirects somebody *added*, not the URLs WordPress
serves by virtue of being WordPress. They are invisible in a sitemap comparison
too, because Yoast lists only the canonical form. Both still 200 on the live
site (re-checked 2026-09-03).

**These are for INBOUND traffic, and that is the whole reason they matter.**
`HtmlToMarkdown.normalizeLegacyUrls` applies rules 1–3 at scrape time, so a post
stored in `content/` already links to `/today/…` — our own markup does not depend
on them. What does is the 312,531 hits arriving from other sites, search results
and bookmarks.

Cloudflare → Rules → Redirect Rules → Create, one per block, `301`/permanent.
The plugin matches **case-insensitively and ignores a trailing slash**
(`flag_case: false`, `flag_trailing: false`), so the replacements should too.

```
# 1. the old blog scheme -- 209,365 hits, foojay's original URL scheme. Also
#    covers /blog/author/…, /blog/category/…, /blog/page/2/ and the feeds, which
#    per-post aliases could not.
When:  (starts_with(http.request.uri.path, "/blog/"))
Then:  concat("/today/", substring(http.request.uri.path, 6))     dynamic, 301

# 2. the almanac -- 102,636 hits. Off-site: never foojay's own content.
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
When:  (http.request.uri.path matches "^/today/category/[^/]+/(?!page/|feed/)[^/]+/")
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
may fire for `/today/…` itself. The 14 renames below **before** 4a, or 4a strips
the parent off the six `tools/…` ones and lands them on a term that does not
exist. Rule 5 **after** 1 and 4, so `/blog/feed/` and
`/today/category/tools/maven/feed/` are normalised first.

**Three traps, each of which fails silently.**

- The `(?!page/|feed/)` in 4a is load-bearing: without it
  `/today/category/tools/feed/` rewrites to `/today/category/feed/` and
  `/today/category/java/page/2/` to `/today/category/page/2/` — breaking two URL
  shapes that work today in the course of fixing a third. Because the
  replacement only strips a prefix, a pager or feed under a *nested* category
  still lands correctly on `/today/category/maven/page/2/`.
- **A feed URL cannot be an `aliases:` entry even in principle.** A Hugo alias is
  an HTML page carrying `<meta http-equiv="refresh">`: a browser follows it, a
  feed reader does not, so every subscriber would get HTML where XML belongs —
  worse than a 404, because it looks like a working response. The traffic is also
  invisible in advance, a feed reader not being a page view.
- `/comments/feed/` has no equivalent — there is no site-wide comment feed here —
  so let it fall through to the 404 rather than aiming it at something that is
  not what it claims.

**The other 14 nested-category URLs are renames**: the slug itself changed, so no
pattern derives them and each needs its own rule (or one rule with a lookup
map). Both columns are under `/today/category/` unless shown otherwise:

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
equivalent is `/today/category/machine-learning/`. It points at `/ai/` because
that page renders exactly that category (`list_category: "Machine Learning"`,
the same 66 articles) with an editorial introduction on top — the same post set
on the better page. Repoint it at the term page if the portal ever stops
tracking the category.

### Verifying, after cutover

Each should answer `301` with the destination above. Run the same loop against
the live WordPress site first if you want the expected output — it is what these
rules were copied from.

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
for that reason; this site resolves that pair the other way round anyway (see the
calendar note in `CLAUDE.md`).

---

## DNS records

Verify the GitHub Pages IPs against
<https://docs.github.com/pages/configuring-a-custom-domain-for-your-github-pages-site>
on the day — GitHub has changed them before.

| Name | Type | Value | Proxy |
|---|---|---|---|
| `foojay.io` | `A` | `185.199.108.153`, `185.199.109.153`, `185.199.110.153`, `185.199.111.153` | grey → orange (Phase 3) |
| `foojay.io` | `AAAA` | `2606:50c0:8000::153`, `2606:50c0:8001::153`, `2606:50c0:8002::153`, `2606:50c0:8003::153` | grey → orange |
| `www.foojay.io` | `CNAME` | `foojayio.github.io` | grey → orange |
| `wordpress.foojay.io` | `A` | WP Engine's current IP | **grey, permanently** |
| `_github-pages-challenge-foojayio.foojay.io` | `TXT` | from the org verification page | n/a |

**Leave `MX` and the `SPF`/`DKIM`/`DMARC` `TXT` records untouched.**

On Cloudflare you can use **CNAME flattening** at the apex — a `CNAME` from
`foojay.io` to `foojayio.github.io` — instead of the four `A` plus four `AAAA`
records. It is one record instead of eight and it follows GitHub if the IPs
change. Either works.

---

## Rollback

WordPress stays untouched and reachable at `wordpress.foojay.io` throughout, so
recovery is a DNS change:

1. Point `foojay.io`'s `A`/`AAAA` back at WP Engine's IP, proxy **on**.
2. Purge the Cloudflare cache.
3. Remove the custom domain from the Pages settings, so GitHub stops answering
   for it.

With the TTL lowered in Phase 2 this takes about a minute to propagate.

One thing does **not** roll back, which is why it is ordered the way it is: any
view counts accumulated in the Worker's `live` column stay there (harmless —
`--seed` only sets `legacy`). The comment archive is just files in the repo, so
it rolls back with everything else — which is the point of it being an archive
here rather than 580 irreversible writes into somebody else's API.
