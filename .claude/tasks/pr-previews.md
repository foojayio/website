# Task: preview URLs for draft posts and PRs

Status: designed, **not implemented, no repo changes made**. Owners: **[repo]**, **[IT]**.

## Goal

A contributor's PR (mostly from a **fork**) gets a bot comment with a URL showing the whole site with their draft in it. Today `draft/` is not built, and the PR check only proves the build is green.

Independent of the GitHub Pages → Cloudflare move (`move-to-cloudflare.md`): previews go to their own Cloudflare Pages project, so they work before and after it.

## Design

**Two workflows, because a fork PR has no secrets.** `pr-check.yml` builds (unprivileged); a second workflow triggered by it deploys (privileged, from `main`).

1. **Preview build, in `pr-check.yml`**, after "Build site" and only when `git diff HEAD^1 HEAD -- draft content/posts` is non-empty:
   - Copy every folder under `draft/` that holds an `index.md`/`index.adoc` into `content/posts/2099/12/31/<folder-name>/` (the folder is the slug; drafts may be nested one level, as `draft/waiting-for-feedback-*/` is).
   - `hugo --gc --minify --buildFuture --config hugo.toml,hugo.preview.toml --baseURL https://pr-<n>.foojay-preview.pages.dev/ --destination preview/site`. `--buildFuture` shows scheduled posts too.
   - Write `preview/pr-number`, upload `preview/` as artifact `preview-site` (1 day).
   - **No Pagefind**: search does not work in a preview, but the build is about **16,750 files** instead of 20,704, under Cloudflare Pages' Free limit of 20,000 (verify the limit; headroom is small).
2. **`hugo.preview.toml`** (new, repo root), layered over `hugo.toml`, so a preview never counts views, reports to analytics or opens giscus threads for an unpublished slug. Verified with a local build: no GTM, no `api/views` in the output.
3. **`preview-deploy.yml`** (new), `on: workflow_run` of `PR check`, success and `event == pull_request` only:
   - **No checkout.** Nothing from the artifact is executed, only uploaded.
   - Skips itself while the `CLOUDFLARE_API_TOKEN` secret is unset, and when the run has no `preview-site` artifact.
   - The PR number is read from untrusted output, so it is used only if that PR is open and its `head.sha` equals the commit the run built.
   - Strips `functions/`, `_worker.js`, `_routes.json`, `_redirects`; writes its own `_headers` (`/*` → `X-Robots-Tag: noindex`).
   - `cloudflare/wrangler-action@v3`: `pages deploy site --project-name=foojay-preview --branch=pr-<n>`.
   - Creates or updates **one** PR comment, found by an HTML-comment marker (not `--edit-last`, which would overwrite any other bot comment).
   - Concurrency per fork + branch, `cancel-in-progress`.
   - Permissions: `actions: read`, `pull-requests: write`.

Rejected: `wrangler versions upload` against the production Worker (needs the secret in the fork build); Cloudflare's Git integration (cannot run Hugo + asciidoctor + JBang).

## Steps

- [ ] **[IT]** `wrangler pages project create foojay-preview --production-branch=main`.
- [ ] **[IT]** CI token gets *Pages: Edit*; repo secrets `CLOUDFLARE_API_TOKEN` and `CLOUDFLARE_ACCOUNT_ID` (the same ones the migration needs).
- [ ] **[repo]** Add `hugo.preview.toml`, the `pr-check.yml` steps and `preview-deploy.yml` below.
- [ ] **[repo]** Docs: `CONTRIBUTING.md` (next to the "preview it locally" bullet: a bot comment links a preview at `/today/<your-folder-name>/`; no search, not indexed) and `MAINTAINERS.md` (workflow table: `pr-check.yml` row mentions the preview build, new `preview-deploy.yml` row).
- [ ] **[both]** Test with a real **fork** PR touching `draft/`: comment appears, URL loads the draft, `X-Robots-Tag: noindex` is sent, a second push updates the same comment.

## Known limits

- The comment cannot know a draft's final URL if it sets `slug:` in frontmatter; it says `/today/<folder-name>/`.
- Old `pr-<n>` previews are not removed when a PR closes. They are noindex, and the content is already public in the PR.
- The Pages project is public. Fine for a public repo; Cloudflare Access can protect it if that changes.

## Files to add

`hugo.preview.toml`:

```toml
# Layered over hugo.toml for PR previews only (pr-check.yml): a preview must not
# count views, report to analytics or open giscus threads for an unpublished slug.
[params.views]
  endpoint = ""

[params.analytics]
  gtm = ""

[params.giscus]
  repoId = ""
```

`pr-check.yml`, inserted after the "Build site" step:

```yaml
      # A second, throwaway build for reviewers: draft/ is not built, so the
      # drafts are copied into content/ first, and --buildFuture shows scheduled
      # posts. Runs on fork PRs, so it holds no secret; preview-deploy.yml
      # publishes the artifact. Pagefind is skipped to stay under the 20,000-file
      # limit of a Cloudflare Pages project, so search does not work in a preview.
      - name: Build a preview of the drafts and posts this PR touches
        id: preview
        env:
          PR: ${{ github.event.pull_request.number }}
        run: |
          if git diff --quiet HEAD^1 HEAD -- draft content/posts; then
            echo "No draft or post changed: no preview."
            exit 0
          fi
          mkdir -p content/posts/2099/12/31
          while IFS= read -r f; do
            cp -r "$(dirname "$f")" "content/posts/2099/12/31/$(basename "$(dirname "$f")")"
          done < <(find draft \( -name index.md -o -name index.adoc \))
          hugo --gc --minify --buildFuture --config hugo.toml,hugo.preview.toml \
            --baseURL "https://pr-${PR}.foojay-preview.pages.dev/" --destination preview/site
          echo "$PR" > preview/pr-number
          echo "built=true" >> "$GITHUB_OUTPUT"

      - name: Keep the preview for preview-deploy.yml
        if: steps.preview.outputs.built == 'true'
        uses: actions/upload-artifact@v7
        with:
          name: preview-site
          path: preview
          retention-days: 1
```

`.github/workflows/preview-deploy.yml`:

```yaml
name: Deploy PR preview

# Second half of the preview that pr-check.yml builds. Split in two because a
# fork PR runs with no secrets, so it can build but not deploy; this one runs
# from main with the Cloudflare token. Hence the rules: no checkout, and nothing
# from the artifact is ever executed -- it is only uploaded as static files.
on:
  workflow_run:
    workflows: ["PR check"]
    types: [completed]

concurrency:
  group: preview-${{ github.event.workflow_run.head_repository.full_name }}-${{ github.event.workflow_run.head_branch }}
  cancel-in-progress: true

jobs:
  deploy:
    if: github.event.workflow_run.event == 'pull_request' && github.event.workflow_run.conclusion == 'success'
    runs-on: ubuntu-latest
    permissions:
      actions: read
      pull-requests: write
    env:
      GH_TOKEN: ${{ github.token }}
      REPO: ${{ github.repository }}
      RUN_ID: ${{ github.event.workflow_run.id }}
      HEAD_SHA: ${{ github.event.workflow_run.head_sha }}
      CLOUDFLARE_API_TOKEN: ${{ secrets.CLOUDFLARE_API_TOKEN }}
    steps:
      - name: Fetch the preview the PR check built
        id: fetch
        if: env.CLOUDFLARE_API_TOKEN != ''
        run: |
          found=$(gh api "repos/$REPO/actions/runs/$RUN_ID/artifacts" \
            --jq '[.artifacts[] | select(.name == "preview-site")] | length')
          if [ "$found" = "0" ]; then
            echo "No preview artifact: the PR touches no draft or post."
            exit 0
          fi
          gh run download "$RUN_ID" --repo "$REPO" --name preview-site --dir preview

          # The number comes from untrusted output, so it only counts if that PR
          # is open and its head is the commit this run built.
          pr="$(cat preview/pr-number)"
          [[ "$pr" =~ ^[0-9]+$ ]] || { echo "::error::pr-number is not a number"; exit 1; }
          head="$(gh api "repos/$REPO/pulls/$pr" --jq 'select(.state == "open") | .head.sha')"
          [ "$head" = "$HEAD_SHA" ] || { echo "::error::PR #$pr does not match this run"; exit 1; }

          # Only the files, as static assets: no Pages Functions, no worker, no
          # redirects, and the response headers are ours.
          rm -rf preview/site/functions preview/site/_worker.js preview/site/_routes.json preview/site/_redirects
          printf '/*\n  X-Robots-Tag: noindex\n' > preview/site/_headers
          echo "pr=$pr" >> "$GITHUB_OUTPUT"

      - name: Deploy to Cloudflare Pages
        if: steps.fetch.outputs.pr != ''
        uses: cloudflare/wrangler-action@v3
        with:
          apiToken: ${{ secrets.CLOUDFLARE_API_TOKEN }}
          accountId: ${{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          workingDirectory: preview
          command: pages deploy site --project-name=foojay-preview --branch=pr-${{ steps.fetch.outputs.pr }} --commit-dirty=true

      - name: Link the preview from the PR
        if: steps.fetch.outputs.pr != ''
        env:
          PR: ${{ steps.fetch.outputs.pr }}
        run: |
          marker='<!-- pr-preview -->'
          body="$marker
          **Preview:** https://pr-${PR}.foojay-preview.pages.dev/ (built from \`${HEAD_SHA:0:7}\`)

          A draft is at \`/today/<its-folder-name>/\`. Search does not work in a preview, and the page is not indexed or counted."
          id="$(gh api "repos/$REPO/issues/$PR/comments" --paginate \
            --jq ".[] | select(.body | contains(\"$marker\")) | .id" | head -1)"
          if [ -n "$id" ]; then
            gh api -X PATCH "repos/$REPO/issues/comments/$id" -f body="$body" > /dev/null
          else
            gh api "repos/$REPO/issues/$PR/comments" -f body="$body" > /dev/null
          fi
```

Only the local Hugo build of the preview was tried (config layering, base URL, no GTM/views, 16,750 files). The workflows themselves have never run.
