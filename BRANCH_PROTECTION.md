# Protecting `main` on foojayio/website

Everything below is your part; the repo side is already done and sitting on
branch `branch-protection`. The workflow changes are inert until [step 2](#2-two-repo-settings)
is finished (`if: vars.DATA_SYNC_APP_ID != ''` → falls back to `GITHUB_TOKEN`),
so merging them changes nothing on its own.

**Why an App at all:** `GITHUB_TOKEN` cannot be put on a bypass list — classic
protection has no entry for it, and a ruleset bypass takes roles, teams, GitHub
Apps or deploy keys. Without a bypass, the data-commit step in
`build-deploy.yml` is rejected, and since it runs before Hugo, that means no
deploy at all on any push to `main`.

## Setup steps

### 1. Create the GitHub App

[github.com/organizations/foojayio/settings/apps](https://github.com/organizations/foojayio/settings/apps)
→ **New GitHub App**

1. **Name:** `foojay data sync` · **Homepage URL:** `https://foojay.io`
2. **Webhook** → uncheck **Active**
3. **Permissions** → **Repository permissions** → **Contents: Read and write**
   (everything else: *No access*)
4. **Identifying and authorizing users** → leave the whole section at its
   defaults: Redirect URI blank, *Allow wildcard matching* off, *Request user
   authorization (OAuth) during installation* off, *Enable Device Flow* off, and
   *Expire user authorization tokens* left ticked as GitHub sets it. That section
   configures the **user-to-server** flow — a person clicking "authorize" in a
   browser and being redirected back. This App never does that: Actions signs a
   JWT with the `.pem` and exchanges it for an **installation** access token
   (server-to-server), so there is no browser, no consenting user and nothing to
   redirect to. The one field that is on by default governs user tokens that will
   never be issued, and unticking it is the less safe direction.
5. **Where can it be installed** → *Only on this account* → **Create GitHub App**
6. Note the **App ID**, then **Private keys** → **Generate a private key**
   (downloads a `.pem`)
7. Left sidebar → **Install App** → *foojayio* → *Only select repositories* →
   `foojayio/website`

### 2. Two repo settings

**Settings → Secrets and variables → Actions**

| Tab | Action | Name | Value |
| --- | --- | --- | --- |
| Variables | New repository variable | `DATA_SYNC_APP_ID` | the App ID (a number) |
| Secrets | New repository secret | `DATA_SYNC_APP_PRIVATE_KEY` | the whole `.pem`, including the `-----BEGIN…` / `-----END…` lines |

### 3. The ruleset

**Settings → Rules → Rulesets → New ruleset → New branch ruleset**

- **Name** `main` · **Enforcement status** *Active*
- **Bypass list** → **+ Add bypass** → **GitHub Apps** → *foojay data sync*
  (leave *Always*). Add **Repository admin** too if you want to keep pushing to
  `main` directly yourself.
- **Target branches** → **Add target** → *Include default branch*
- Tick:
  - Restrict deletions
  - Block force pushes
  - Require a pull request before merging (**Required approvals: 0** — you're
    the only maintainer)
  - Require status checks to pass → **+ Add checks** → `build-and-validate`
    (the job in `pr-check.yml`; type the name if it isn't in the picker yet)

> ⚠️ Leave **Require signed commits** OFF — the bots don't sign, it would break
> every sync.

**Require linear history** is safe if you want it: the syncs rebase before
pushing.

### 4. Verify (2 minutes)

1. **Actions → Sync view counts → Run workflow.** Expect a
   `chore: sync view counts [skip ci]` commit by `foojay-bot` and a **Build and
   deploy** run starting right after it (event: `workflow_dispatch`).
2. `Protected branch update failed` in the log = the bypass isn't applying →
   check the app is on the bypass list and that `DATA_SYNC_APP_ID` is set.
3. Then try pushing a trivial commit straight to `main` from your machine — it
   should be rejected (unless you added the **Repository admin** bypass).

Don't trust a bypass by role (Repository admin / Maintain / Write) to cover
`github-actions[bot]` — test it, the failure mode is a broken deploy.

## Also fixed in that branch, separately

A bug I hit while checking the workflows: **a push made with `GITHUB_TOKEN`
never starts a workflow run**, so the daily sync's commit landed on `main` and
nothing rebuilt the site. Confirmed in the run history: commit `be7257d8`
appears in no `build-deploy` run. A new meetup or a fresh read count only
reached the site when a human next pushed — on a quiet week `/calendar/` was
days stale while `data/jug-events.json` was current.

Both syncs now dispatch the deploy explicitly
(`gh workflow run build-deploy.yml`), which works today with the existing
token, and their commits carry `[skip ci]` so that once the App token is in
place you don't get two builds per sync.

Full reasoning is in `CLAUDE.md` under "Protecting main" and the entry above it.

## Keeping your own direct pushes to main

Short version: add **Repository admin** to the bypass list. But do it as **two
rulesets, not one** — a bypass exempts an actor from *every* rule in the ruleset
it's on, so putting yourself on a single ruleset would also exempt you from the
force-push and deletion protection, which is the one thing you probably want to
apply to everybody including you.

### Ruleset A — main integrity (nobody bypasses)

- **Target branches** → *Include default branch*
- **Rules:** Restrict deletions + Block force pushes
- **Bypass list:** empty — neither you nor the bots need it, because these rules
  don't block an ordinary fast-forward push
- **Effect:** `main` can't be force-pushed or deleted by anyone, ever, including
  you and the App

### Ruleset B — main review

- **Target branches** → *Include default branch*
- **Rules:** Require a pull request before merging (Required approvals `0`) +
  Require status checks to pass → `build-and-validate`
- **Bypass list:** **+ Add bypass** → **Roles** → *Repository admin* (leave
  *Always*), and → **GitHub Apps** → *foojay data sync*
- **Effect:** contributors go through a PR that must pass the check; you can
  `git push` straight to `main` for a quick fix and merge your own PRs without
  an approval; the syncs keep working

### Two notes

- A repo ruleset's bypass list takes roles, teams, GitHub Apps and deploy keys —
  **not individual usernames**. *Repository admin* is the entry that covers you.
- *Always* vs *For pull requests only* matters here: **Always** is what permits
  the direct push. With *For pull requests only* your `git push` to `main` is
  still rejected.

**Sanity check afterwards:** push a trivial commit straight to `main` (should
succeed), then `git push --force` a harmless rewrite (should be rejected —
that's ruleset A doing its job).