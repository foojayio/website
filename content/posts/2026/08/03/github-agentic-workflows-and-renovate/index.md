---
title: "GitHub agentic workflows and Renovate"
date: "2026-08-03T05:54:08+00:00"
lastmod: "2026-09-21T05:54:50+00:00"
description: "I've been a big fan of Renovate for a couple of years already. Renovate scans your repositories, detects outdated package versions, and opens pull…"
canonical: "https://blog.frankel.ch/github-agentic-workflow-renovate/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "AI"
  - "DevOps"
related_posts:
  - "a-github-agentic-workflow"
  - "solving-gradle-metadata-and-renovate-integration"
  - "whats-new-in-actions-setup-java-5-4-and-5-5-signature-verification-kona-jdk-and-a-better-maven-experience"
  - "how-to-publish-a-java-maven-project-to-maven-central-using-jreleaser-and-github-actions-2025-guide"
frozen: false
---

I've been a big fan of Renovate for [a couple of years already](https://blog.frankel.ch/renovate-alternative-dependabot/). Renovate scans your repositories, detects outdated package versions, and opens pull requests to automatically bump them. It's similar to Dependabot in that it keeps your dependencies up to date. If I had to compare them in one sentence, I'd say Renovate is less integrated in the GitHub ecosystem, but handles more ecosystems and, more importantly, is extensible. My current company is a happy Renovate user, and I already started to use it in some repositories.

I have previously written about [Agentic Workflows](https://blog.frankel.ch/agentic-github-workflows/). As a reminder, you first create the workflow in Markdown with a very loose syntax. Then, you "compile" it to generate a GitHub Workflow-compliant YAML file, suffixed with `.lock.yml`.

In this post, I want to describe the problem when combining the two, how I tried to fix it, and the correct solution.

## The core problem

Renovate is very eager to please. By default, it scans all ecosystems used for dependencies: `build.gradle.kts`, `pom.xml`, `pyproject.toml`, `Dockerfile`, GitHub workflows, etc. Most "manual" GitHub workflows use GitHub actions, which are versioned dependencies.

The generated lock of agentic workflows does make use of GitHub actions, in particular `gh-aw-actions/setup`.

When Renovate worked on my repository, it did what it always does. It searched for candidate files, found my documentation-review workflow's lock file, scanned the dependencies, and opened a PR bumping the SHA to the latest, including `gh-aw-actions/setup`. So far, so good.

However, it created an interesting problem. The action SHA pointed to a new version of `gh-aw-actions`. The script body inside the same file didn't change, but referenced a helper script that the new version didn't provide. The workflow failed.

## The wrong fix

I have to admit I didn't understand the above synchronization issue at first.

To fix it, I created a new non-agentic GitHub workflow. Whenever Renovate upgrades `lock.yml`, the workflow automatically recompiles the agentic workflow. This introduced a subtler bug. The trigger fired on *any* change to `docs-review.lock.yml`, not just `gh-aw-actions` version bumps.

In my case, Renovate later opened a separate PR to pin the Node.js version inside the same file: the recompile workflow triggered, ran `gh aw compile`, and overwrote the file, rolling back the node-version change Renovate had just made.

## The actual solution

After the second rollback, I realized something was fishy and understood the root cause. The compiler generates the whole lock file from a single `gh-aw` release. The action pin and the inline scripts inside the file must match. Renovate bumped the former and left the latter untouched.

The solution was much simpler. It consisted of two parts.

**Exclude the lock file from Renovate**

Renovate has an `ignorePaths` entry. I added the lock file and `agentics-maintenance.yml`, another workflow that the compilation generates:

```json
"ignorePaths": [
  ".github/workflows/docs-review.lock.yml",
  ".github/workflows/agentics-maintenance.yml"
]
```

**Update the actual source, then recompile everything**

The real source of version pins is `.github/aw/actions-lock.json`, a manifest file that `gh aw compile` reads to resolve action SHAs. Renovate can't update this file yet. There's an open [feature request](https://github.com/renovatebot/renovate/issues/44753) to support it. In the meantime, `gh aw update-actions` refreshes the manifest. I kept my recompile workflow but narrowed its trigger to this single file. When the manifest changes, the workflow fires and runs bare `gh aw compile`, regenerating all compiled artifacts at once.

```yaml
on:
  pull_request:
    branches: [master]
    paths:
      - .github/aw/actions-lock.json
```

```bash
GH_TOKEN=${{ secrets.GITHUB_TOKEN }} gh aw compile
```

The [`gh-aw` documentation](https://github.github.com/gh-aw/reference/compilation-process/) warns against editing generated references by hand: you should run `gh aw compile` to regenerate them. It boils down to the same split: the manifest owns the pins, the compiler owns the generated outputs.

## Conclusion

Generated files are not dependency files, even when they contain version strings. We all know we shouldn't change generated files. New tools shouldn't change this approach.

In our case, Renovate should only know about the source, not the generated files, even when they are under source control. Ergo, filter out files generated by agentic workflows.

**To go further:**

* [GitHub Agentic Workflows](https://github.github.com/gh-aw/)
* [ignorePaths](https://docs.renovatebot.com/configuration-options/#ignorepaths)
* [Add support for the GitHub Agentic Workflows (gh-aw) lockfile](https://github.com/renovatebot/renovate/issues/44753)
* [Compilation Process](https://github.github.com/gh-aw/reference/compilation-process/)
* [Dependabot Manifest Generation](https://github.github.com/gh-aw/reference/dependabot/)

*Originally published at [A Java Geek](https://blog.frankel.ch/github-agentic-workflow-renovate/) on August 2^nd^, 2026.*
