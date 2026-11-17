# Task: move foojay.io hosting from GitHub Pages to Cloudflare

Status: not started. Owners: **[repo]** = this repo (Frank / Claude), **[IT]** = Azul IT in the Cloudflare dashboard, **[both]**.

## Why

- The built site is approaching the 1 GB artifact limit of GitHub Pages (`public/`, 20,704 files).
- The `deploy-pages` step times out intermittently.
- foojay.io is already proxied by Cloudflare (Redirect Rules, the views Worker), so this removes a hop rather than adding a vendor.

## What it is called

Cloudflare has two products for static hosting:

- **Workers Static Assets** (a Worker with an `assets` directory). Cloudflare's recommended home for new projects; **this task's recommendation**. `worker/views/` is already a Worker, so there is one toolchain to learn.
- **Cloudflare Pages**. Still supported, but not where new features go. `wrangler pages deploy public` is the same idea with different config.

Either way, **deploy by direct upload from GitHub Actions, not through Cloudflare's Git integration.** The build needs Hugo extended, asciidoctor, JBang, Node/Playwright and full git history (`enableGitInfo`), none of which Cloudflare's build image offers, and the validators + e2e gate must stay before the deploy.

## Blocker to settle first: file count

Cloudflare limits **files per deployment: 20,000 on Free, 100,000 on Paid** (and 25 MiB per file; the largest file here is under 10 MB). The site is at **20,704 files**, so the Free plan does not fit. Verify current limits in the Cloudflare docs, then:

- **[IT]** Confirm which plan the foojay.io account is on. A Workers Paid (or Enterprise) plan is the straightforward answer. Pagefind alone is 3,620 files.
- If a paid plan is not possible, the only lever is fewer files (bundle-count reduction, Pagefind tuning) and that is a separate, larger task. Stop here and re-plan.

## Phase 0: decisions

- [ ] **[both]** Workers Static Assets or Pages (recommended: Workers).
- [ ] **[IT]** Which Cloudflare account owns the project, and the project/Worker name (suggest `foojay-website`).
- [ ] **[both]** Rehearsal hostname, e.g. `staging.foojay.io` or the `*.workers.dev` URL. Production keeps serving from GitHub Pages until Phase 3.

## Phase 1: Cloudflare setup (IT)

- [ ] Create the API token for CI, scoped to the one account: *Workers Scripts: Edit* (Pages: Edit if Pages). Hand it over through GitHub secrets, never chat or the repo.
- [ ] Give Frank the **account ID**.
- [ ] Add repo **secrets** `CLOUDFLARE_API_TOKEN`, `CLOUDFLARE_ACCOUNT_ID` (Settings → Secrets and variables → Actions).
- [ ] Decide whether the existing zone **Cache Rules / Page Rules** assume GitHub Pages as origin (e.g. "cache everything"). Those would serve stale HTML after a deploy from the new origin. List them and trim as needed.
- [ ] Note down the current DNS records for `foojay.io` and `www.foojay.io` (the GitHub Pages CNAME/A records), so rollback is a copy-paste.

## Phase 2: Repo changes, deployed in parallel to production

- [ ] **[repo]** Add a root `wrangler.toml` (the views Worker's stays in `worker/views/`):
  ```toml
  name = "foojay-website"
  compatibility_date = "2026-11-01"
  [assets]
  directory = "./public"
  not_found_handling = "404-page"      # serves 404.html with a 404, as GitHub Pages did
  html_handling = "auto-trailing-slash" # /x -> /x/ -> x/index.html
  ```
  Custom domains (`routes = [{ pattern = "foojay.io", custom_domain = true }, …]`) are added in Phase 3, not here.
- [ ] **[repo]** `build-deploy.yml`:
  - Drop `actions/configure-pages`, `upload-pages-artifact`, `deploy-pages`, the `github-pages` environment, and the `pages: write` / `id-token: write` permissions.
  - **`hugo --baseURL`** becomes the literal `https://foojay.io/` (it came from `configure-pages`). For the rehearsal build, pass the staging URL so the `BuiltSite.java` base-path check still means something.
  - The `curl` for `feeds/previous.xml` reads the live feed through `base_url`; point it at `https://foojay.io/index.xml`.
  - `deploy` job: `cloudflare/wrangler-action` with `command: deploy` (needs the `public/` artifact: either deploy from the build job, or keep `upload-artifact`/`download-artifact` between the two jobs).
  - **Keep the filename `build-deploy.yml`**: `publish-scheduled.yml` finds the last successful deploy with `gh run list --workflow=build-deploy.yml`, and the two syncs dispatch it by name. Rename the workflow's `name:` only.
  - Keep the `pages` concurrency group (rename optional) and `cancel-in-progress`; a Worker upload is atomic, so cancelling mid-upload is safe.
  - `announce` keeps `needs: deploy`. Check that a first-time `wrangler deploy` of 20k files finishes well within the job timeout; later deploys upload only changed files.
- [ ] **[repo]** Add `static/_headers` (supported by both products), which GitHub Pages never allowed. At minimum `Cache-Control` for `/pagefind/*`, `/images/*`, `/vendor/*`; security headers are a bonus. Keep HTML on a short cache so a deploy shows up quickly.
- [ ] **[repo]** Deploy once to the rehearsal hostname and **diff it against production**: same page count, `curl -I` on a sample (home, a post, `/pedia/x/`, a `.pdf` in a bundle, `/index.xml`, `/sitemap.xml`, a missing URL → 404 + the custom page, `/today/category/x` without trailing slash → 301). Case-sensitivity should match; confirm.
- [ ] **[repo]** Run the Playwright e2e suite against the rehearsal URL (`tests/e2e/server.mjs` is a GitHub Pages lookalike on localhost; this checks the lookalike was right).
- [ ] **[repo]** Alias redirect stubs are `<meta refresh>` pages and keep working unchanged. Real 301s via `_redirects` are an option later (limit 2,000 static rules); not needed for the move.
- [ ] **[both]** **Views Worker interplay**: `foojay.io/api/views/*` is a Worker *route*, and the site will be a Worker *custom domain* on the same hostname. Test on the rehearsal hostname that the route still wins over the assets Worker and that `POST` beacons and `/all` still answer, **before** cutover. If not, route the assets Worker as `foojay.io/*` instead and rely on route specificity.
- [ ] **[repo]** `worker/views/wrangler.toml`: after cutover drop `https://foojayio.github.io` from `ALLOWED_ORIGINS` (and the staging origin once done), then redeploy by hand.

## Phase 3: Cutover (IT + Frank, one sitting, off-peak)

- [ ] **[IT]** Attach `foojay.io` and `www.foojay.io` as custom domains of the Worker (removes/replaces the GitHub Pages DNS records; Cloudflare does this for a zone it hosts). Keep the `www` behaviour as it is today (redirect or serve; check the current Redirect Rules).
- [ ] **[IT]** Purge the zone cache once.
- [ ] **[repo]** Merge the workflow change so `main` deploys to Cloudflare, then `workflow_dispatch` one deploy.
- [ ] **[both]** Smoke test: home, three posts, an `aliases:` URL, a `REDIRECTS.md` rule (`/blog/…` → `/today/…`), `/index.xml`, search, a 404, the read counter incrementing, an author page.
- [ ] **[both]** Confirm **Redirect Rules still fire**: they run before Workers, so `REDIRECTS.md` should be unaffected; verify, don't assume.
- [ ] **[both]** Confirm GTM/Ketch still load (nothing in the page depends on the host, but the consent banner is host-keyed on the Ketch property).

## Phase 4: Clean-up (after about a week of stable deploys)

- [ ] **[repo]** Rollback window over: delete nothing earlier. Until then rollback = revert the workflow commit, restore the DNS records from Phase 1, and GitHub Pages deploys again.
- [ ] **[IT]** Repo Settings → Pages: unpublish; remove the custom domain there.
- [ ] **[repo]** Update docs that name GitHub Pages: `AGENTS.md` (first paragraph, the images/1 GB convention, workflow notes), `MAINTAINERS.md:81`, `scripts/validate/AGENTS.md` (lines 6 and 70), `tests/e2e/server.mjs` header, `hugo.toml:104`, `themes/foojay/layouts/404.html:18` (the `/website/` trial-deploy comments).
- [ ] **[repo]** Reword the image-budget convention: the 1 GB artifact limit is gone, but the per-file budget still protects build time and bandwidth. **Don't loosen it without Frank's decision.**
- [ ] **[repo]** Add a **file-count** line (and warning near the plan limit) to `BuiltSite.java`'s summary; it already prints the count, and that is now the number that can break the deploy.

## Not in scope / worth knowing

- **PR previews** (including fork PRs) are a separate task, `pr-previews.md`. They use their own Cloudflare Pages project and the same two secrets as Phase 1, so ask IT for both at once (the token also needs *Pages: Edit*).
- SEO risk is low: same hostname, same URLs, same sitemap. Search Console needs no change.
- The `data-sync` workflows and the App token for pushing to `main` are unrelated to hosting and stay.
