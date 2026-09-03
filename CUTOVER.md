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

- [ ] **Pre-create the Cloudflare Redirect Rules** from
      `cutover/legacy-redirects.md` — **five families now, not three.**
      `aliases:` cannot express a regex, and rules 1–3 alone carry **312,531
      recorded hits**, more than every per-page alias combined. Doing it now
      rather than on the day is deliberate: every rule reproduces something the
      live site already serves, so creating them while WP is up changes nothing
      observable, and it takes the highest-traffic item off the cutover-day
      critical path.

      **Rules 4 and 5 are not in the Redirection plugin's export**, which is why
      they were missed the first time round: the export lists redirects somebody
      *added*, not the URLs WordPress serves by being WordPress.
      - **4 — hierarchical category paths.** WP categories nest and Yoast
        canonicalises to the nested form, so `/today/category/tools/maven/` is
        the *indexed* URL while Hugo only has the flat one. 55 URLs plus their
        `page/N/` and `feed/` variants.
      - **5 — `/feed/`.** WP serves RSS at `/feed/` and `<archive>/feed/`; Hugo
        serves `/index.xml`. Every existing subscriber 404s at cutover, and this
        one cannot be an `aliases:` entry even in principle — a Hugo alias is an
        HTML meta-refresh page, which no feed reader follows.

---

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
- [ ] **The regex redirects work.** Run the verification loop at the bottom of
      `cutover/legacy-redirects.md` — `/blog/…`, `/almanac/jdk-17`, `/docs/…`,
      a nested category path, and `/feed/`. That loop also checks the two URLs
      rule 4a must *not* touch (`/today/category/java/page/2/` and
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
