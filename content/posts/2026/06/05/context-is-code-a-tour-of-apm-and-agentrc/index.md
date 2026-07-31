---
title: "Context Is Code: A Tour of APM and AgentRC"
slug: "context-is-code-a-tour-of-apm-and-agentrc"
date: "2026-06-05T08:35:05+00:00"
lastmod: "2026-06-05T10:00:58+00:00"
description: "Why agent context needs a package manager, what APM gives you, and how AgentRC closes the loop on generation and evals."
authors:
  - "soham-dasgupta"
image: "Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "GenAI"
  - "Library"
  - "LLM"
tags:
related_posts:
  - "introducing-skills-boxlang-io-the-open-agent-skills-ecosystem-for-boxlang-the-ortus-world"
  - "context-is-a-budget-eight-levers-and-three-workflow-patterns"
  - "claude-code-sonarqube-mcp"
enlighterjs: true
frozen: false
---

If you've shipped an AI agent into a real codebase in the last twelve months, you've felt this: every agent, every developer, every machine --- different setup. A README that says "install these extensions." A `copilot-instructions.md` somebody copy-pasted from another repo. MCP server configs in three different files. The same skills duplicated for Copilot, Claude, Cursor, and Codex.  

No version pinning.  

No integrity.  

No reproducibility.

That's the problem [**APM**](https://github.com/microsoft/apm), the Agent Package Manager fixes. And it pairs with [**AgentRC**](https://github.com/microsoft/agentrc) to close the loop on *generating* and *evaluating* that context in the first place.

*** ** * ** ***

1. The problem: agent context drifts {#h2-0-1-the-problem-agent-context-drifts}
-------------------------------------------------------------------------------

Most teams' agent setup today is a tangle of hand-rolled files:

* A `copilot-instructions.md` that nobody updates after the first sprint.
* The same content copied (badly) into `CLAUDE.md` and `.cursor/rules`.
* MCP servers configured in `.vscode/mcp.json`, in a personal config, and in a `Makefile` someone forgot about.
* Skills, prompts, and rules duplicated across every harness your team has tried.

The results:

* **"It works on my agent" 🤷** --- the AI-era version of works-on-my-machine.
* **Drift between developers** even within the same repo.
* **Silent context rot** as the code evolves but the instructions don't.
* **No way to ship agent context as a versioned artifact.**
* **No security boundary around prompts** --- and a prompt is a program for an LLM.

If we treat prompts as text, we'll keep ignoring them in supply-chain reviews. If we treat them as code versioned, hashed, audited --- we get a real perimeter around what agents do.

*** ** * ** ***

2. The idea: what if agent context had a `package.json`? {#h2-1-2-the-idea-what-if-agent-context-had-a-package-json}
--------------------------------------------------------------------------------------------------------------------

One manifest. One install. Every agent, configured. That's it.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># apm.yml — ships with your repo
name: your-project
version: 1.0.0
dependencies:
  apm:
    # Skills, prompts, agents, plugins — from any git repo, version-pinned
    - anthropics/skills/skills/frontend-design
    - github/awesome-copilot/plugins/context-engineering#v2.1
    - microsoft/apm-sample-package#v1.0.0
  mcp:
    # MCP servers governed by the same manifest
    - name: io.github.github/github-mcp-server
      transport: http</pre>

Then:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ git clone &lt;repo&gt; &amp;&amp; cd &lt;repo&gt;
$ apm install
→ resolving 14 packages…
→ deploying primitives to .github/, .vscode/
✓ every agent is configured</pre>

Three details worth highlighting:

1. **Pinning** (`#v2.1`, `#v1.0.0`) --- reproducibility, the same way `package-lock.json`  
   gives it to you.
2. **MCP servers live in the same file** --- one place to declare them, one policy to  
   gate them.
3. **Git is the registry** --- any git URL works. GitHub, GitLab, Azure DevOps,  
   Bitbucket, internal Gitea or Gogs. No central marketplace required (though  
   curated marketplaces do exist).

*** ** * ** ***

3. The 3 strong guarantees {#h2-2-3-the-3-strong-guarantees}
------------------------------------------------------------

APM makes three headline guarantees, everything else flows from these.

### Portable by manifest {#h3-3-portable-by-manifest}

One `apm.yml` describes every primitive --- instructions, skills, prompts, agents,  

hooks, plugins, MCP servers and `apm install` reproduces the same setup across  

every harness on every machine. `apm.lock.yaml` pins the resolved tree the way  
`package-lock.json` does for npm.

### Secure by default {#h3-4-secure-by-default}

Agent context is executable in effect. Every install scans for hidden Unicode  

(bidi/zero-width attacks are a real prompt-injection vector), pins content hashes,  

and gates transitive MCP servers behind trust prompts.

### Governed by policy {#h3-5-governed-by-policy}

`apm-policy.yml` is enforced at install time, including on transitive dependencies  

and transitive MCP servers. Tighten-only inheritance flows enterprise → org → repo.

*** ** * ** ***

4. What an APM package can contain {#h2-6-4-what-an-apm-package-can-contain}
----------------------------------------------------------------------------

Six primitive types. You can mix and match.

|    Primitive     |                                                 What it is                                                  |
|------------------|-------------------------------------------------------------------------------------------------------------|
| **Instructions** | Repo conventions, architecture notes, do/don't rules. Compose into `AGENTS.md` / `copilot-instructions.md`. |
| **Skills**       | Reusable capabilities in the Agent Skills format. Drop-in from `anthropics/skills` or any repo.             |
| **Prompts**      | Slash-command prompts that work across harnesses.                                                           |
| **Agents**       | Single-purpose agent primitives, e.g. `api-architect.agent.md`.                                             |
| **Plugins**      | Author once, export a standard `plugin.json` for Copilot / Claude / Cursor.                                 |
| **MCP servers**  | Declared in the same manifest. Policy-checked before they touch disk.                                       |

This is the part most people underrate. APM isn't only about instructions. It's a  

single manifest for *all* the moving parts an agent needs.

*** ** * ** ***

5. The five commands you'll actually use {#h2-7-5-the-five-commands-you-ll-actually-use}
----------------------------------------------------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apm install                  # resolve manifest, scan, deploy primitives, write lockfile
apm install &lt;pkg&gt;            # add a package to apm.yml and install it
apm compile -t copilot       # render root context files (AGENTS.md / copilot-instructions.md)
apm audit                    # security + policy checks; SARIF output for CI
apm pack                     # bundle your package for distribution</pre>

*** ** * ** ***

6. One manifest, every harness {#h2-8-6-one-manifest-every-harness}
-------------------------------------------------------------------

The same packages render to whatever your team uses:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apm.yml  →  apm compile  →  GitHub Copilot · Claude Code · Cursor
                            OpenCode · Codex · Gemini · Windsurf</pre>

For Copilot specifically, `apm install` is **zero-config** : it writes the files that VS Code and GitHub Copilot already expect (`.github/instructions/`, `.github/prompts/`, `.vscode/mcp.json`), and `apm compile -t copilot` adds the `.github/copilot-instructions.md` rollup. APM itself dogfoods this on its own repo.

For other harnesses, `apm compile` emits `AGENTS.md` in the repo root (the open [agents.md](https://agents.md) standard) plus the harness-specific rules trees. So if a teammate uses Claude Code, Cursor, Codex, Gemini, OpenCode, or Windsurf, their agent is configured from the same manifest. It's your edge against agent vendor lock-in: **context survives the harness.**

*** ** * ** ***

7. Plugins and marketplaces: bundle a workflow, not a file {#h2-9-7-plugins-and-marketplaces-bundle-a-workflow-not-a-file}
--------------------------------------------------------------------------------------------------------------------------

A single skill or instruction file isn't a workflow. Real capabilities, code review, release notes, incident triage, need *several primitives bundled together*.

Take a Code Review plugin:

* An **instructions** file (the review rubric)
* An **agent** file (activates on `/review`)
* Two **skills** (summarise diff, find missing tests)
* One **MCP server** (post PR comments)

Without a plugin, every developer wires four artefacts by hand and hopes the versions match. With an APM plugin:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ apm install acme/code-review</pre>

One command drops all four primitives into the right places under `.github/` and `.vscode/` and pins them in `apm.lock.yaml`. Plugins can depend on other APM packages (transitive deps, just like npm). And `apm pack` exports a standard `plugin.json` bundle that Copilot, Claude, and Cursor can all consume.

Marketplaces are the curated layer on top: install from a registry in one command, deployed across all targets, pinned in the lockfile. `apm pack` emits a `marketplace.json` alongside the bundle when declared.

*** ** * ** ***

8. Security: treat prompts like the programs they are {#h2-10-8-security-treat-prompts-like-the-programs-they-are}
------------------------------------------------------------------------------------------------------------------

The honest pitch on security is this: agent context *is* executable in effect, and the integrity story has to match.

* **Hidden Unicode scanning.** Every install blocks bidi and zero-width characters that can hijack agent behaviour. This is a real, demonstrated prompt-injection vector.
* **Lockfile integrity.** `apm.lock.yaml` records SHA-256 per file. Bundles embed it too, so installing a packaged bundle rehashes every file before writing.
* **MCP trust gating.** Transitive MCP servers don't sneak in --- you re-declare or explicitly trust them.
* **`apm audit`** for on-demand scans, CI-friendly with SARIF output.
* **Drift detection** so generated files can't diverge silently from the manifest.
* **CI/CD ready** via the official `microsoft/apm-action` for GitHub Actions.

One caveat until today(5 June 2026): **package signing is not yet implemented**. The integrity story today is content-hash + lockfile + source allow-listing. But this is already much better than what most teams have.

*** ** * ** ***

9. Governance: one policy file, tighten-only inheritance {#h2-11-9-governance-one-policy-file-tighten-only-inheritance}
-----------------------------------------------------------------------------------------------------------------------

`apm-policy.yml` lets enterprises set the ceiling, orgs tighten further, and repos tighten further still. Each layer can only *tighten* , never *loosen* . The math: allow lists intersect, deny lists union, `max_depth` takes the minimum, and enforcement escalates `off < warn < block`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># apm-policy.yml
allow:
  sources:
    - github.com/microsoft/*
    - github.com/anthropics/*
    - dev.azure.com/contoso/*
  mcp:
    - io.github.github/github-mcp-server
    - io.github.microsoft/playwright-mcp

deny:
  primitives:
    - hooks   # no shell hooks org-wide

require:
  signed: true
  lockfile: present</pre>

Drop the file at `/.github/apm-policy.yml` and every repo in the org picks it up, no per-repo wiring. The CI gate is `apm audit --ci --policy org`, which runs all checks and renders findings on the PR.

For incident response there are two documented bypass surfaces --- `apm install --no-policy` and `APM_POLICY_DISABLE=1` --- and they're loudly logged. Use them sparingly.

The architects' takeaway: **you can roll APM out org-wide without losing control over what agents load.**

*** ** * ** ***

10. AgentRC: context engineering, automated {#h2-12-10-agentrc-context-engineering-automated}
---------------------------------------------------------------------------------------------

So APM *distributes* agent context. But where does the content come from in the first place? That's where [AgentRC](https://github.com/microsoft/agentrc) comes in.
> **Note:** AgentRC is currently marked **experimental** in its README. Pilot it on a non-critical repo and pin a commit if you adopt it today.

AgentRC is a CLI + VS Code extension that reads your codebase and generates the files an agent needs to be useful: instructions, MCP config, VS Code settings, and *evals*. The three commands map cleanly to a lifecycle:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ npx github:microsoft/agentrc readiness
Scoring 9 pillars · 5-level maturity model

  Build &amp; test          L4  ████████░░
  Linting               L2  ████░░░░░░
  Architecture docs     L1  ██░░░░░░░░
  MCP configuration     L0  ░░░░░░░░░░

$ npx github:microsoft/agentrc instructions
→ reading repo, generating .github/copilot-instructions.md
→ generating .vscode/mcp.json
→ generating agentrc.eval.json
✓ done

$ npx github:microsoft/agentrc eval
→ running 12 evals against generated instructions
✓ 11 passed, 1 regressed</pre>

Three things in one: **measure** (`readiness`), **generate** (`instructions`), **maintain** (`eval`).

The eval angle is the one most people miss. Instructions only matter if they actually improve agent responses. AgentRC measures that and can fail CI if context regresses. It's the missing feedback loop in most teams' agent setup.

*** ** * ** ***

11. How AgentRC + APM compose {#h2-13-11-how-agentrc-apm-compose}
-----------------------------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Your repo → agentrc (measure · generate · eval)
         → .instructions.md · mcp.json · eval.json
         → apm.yml (pack · publish)
         → org-wide apm install</pre>

The crucial compatibility point: **the `.instructions.md` format is shared by both tools.** No conversion when moving content from AgentRC into an APM package.

Three personas, three flows:

* **In your project (solo dev).** `agentrc init` generates your baseline, then `apm install org/standards` pulls in shared org packages on top.
* **For your team (team lead).** Package your best instructions and skills as an APM package; teammates get them with one `apm install`.
* **At scale (platform engineer).** `apm audit` + `apm-policy.yml` enforce standards; `agentrc eval` in CI catches context drift.

*** ** * ** ***

12. How to start on Monday {#h2-14-12-how-to-start-on-monday}
-------------------------------------------------------------

Six steps. Most teams should do steps 1--3 this week, 4--6 over the next quarter.

1. **Try it.** `curl -sSL https://aka.ms/apm-unix | sh`, then `apm install microsoft/apm-sample-package` on a throwaway repo.
2. **Measure your repo.** `npx github:microsoft/agentrc readiness` gives you a maturity score and a list of missing context to add first.
3. **Pin what you have.** Move your existing `copilot-instructions.md` and skills into an `apm.yml`. Commit the lockfile.
4. **Share across the team.** Extract the org-wide bits into a shared APM package. Teammates run `apm install`.
5. **Gate in CI.** Add `apm-action` + `agentrc readiness --fail-level 3` to PR checks. This is the step that prevents backsliding --- don't skip it.
6. **Govern.** Roll out an `apm-policy.yml` at the org level. Tighten as you learn.

*** ** * ** ***

13. Three things to take away {#h2-15-13-three-things-to-take-away}
-------------------------------------------------------------------

1. **Agent context is code.** Version it, pin it, ship it like dependencies.
2. **APM gives you the manifest. AgentRC gives you the content.** Together they close the loop.
3. **Security and governance are not afterthoughts.** Hashes, audits, policy, on every install, by default.

If you remember one line, make it this one:
> *Stop hand-rolling agent setup. Pin it like a dependency, scan it like a binary, govern it like infrastructure.*

*** ** * ** ***

Links {#h2-16-links}
--------------------

* [github.com/microsoft/apm](https://github.com/microsoft/apm)
* [microsoft.github.io/apm](https://microsoft.github.io/apm/)
* [github.com/microsoft/agentrc](https://github.com/microsoft/agentrc)
* [agents.md](https://agents.md)
* [modelcontextprotocol.io](https://modelcontextprotocol.io)
