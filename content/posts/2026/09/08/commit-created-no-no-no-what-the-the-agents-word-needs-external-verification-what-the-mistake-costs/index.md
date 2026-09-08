---
title: "“Commit created” — but it isn’t: why the agent’s word needs external verification, and what the mistake costs"
date: "2026-09-08T08:54:03+00:00"
lastmod: "2026-09-08T09:08:20+00:00"
description: "A detailed report reads like proof. The agent lists the files it touched, names the branch, quotes a commit hash, adds a test count and closes with…"
authors:
  - "viktoria-evdokimova"
image: "Frame-2147236862-e1788858255231.jpg"
categories:
  - "Agile"
  - "AI"
  - "Java"
  - "Java Beginner"
  - "Java Core"
  - "JavaFX"
  - "JavaPro"
  - "Kotlin"
  - "LLM"
  - "Machine Learning"
related_posts:
frozen: false
aliases:
  - "/today/commit-created-no-no-no-what-the-the-agents-word-needs-external-verification-и-what-the-mistake-costs/"
---

*![](Frame-2147236862-700x451.jpg)*

A detailed report reads like proof. The agent lists the files it touched, names the branch, quotes a commit hash, adds a test count and closes with "done". The work has a shape. The shape looks right. You have six more tickets.

The report is model output. The commit, if it exists, lives in .git. The file, if it exists, lives on disk. The build result lives in a process exit code and a run log. The chat window is none of those places, and it can only vouch for itself.

I am the product manager at Explyt, where we build an AI agent for JetBrains IDEs, and before that I led the team that built it. Before Explyt I spent years on tools that generate tests by symbolic execution, at JetBrains Research and Huawei, so I have a professional habit of distrusting any statement about a program until something has run and produced evidence. User interviews are part of my job now, and one line from a senior developer stuck with me because it was so plain: "The agent told me the commit was created. It wasn't." I wanted to know whether that was one unlucky session or a pattern, so I went through public issue trackers and whatever vendor and research posts described the same failure. It is a pattern, and it has a mechanism. This article applies to any JetBrains AI agent you might run: Junie, Copilot in agent mode, Claude Code attached to IntelliJ IDEA, a local model behind a plugin, or Explyt.

## TL;DR

* I went through public issues on Claude Code, Codex and Copilot, plus a Cursor forum thread about Gemini. They document agents reporting commits that exist on no branch, files that were never written and test runs that never happened. Anthropic's own docs and a METR post describe the same failure mode, so I stopped treating it as bad luck.
* The three false claims I saw most often are "committed", "file written" and "tests pass". For each one I give the independent check I run myself; none takes more than a minute.
* A check the agent pasted into the chat is also model output. I run the command again or read the raw tool result.
* In my own work I run agents inside a JetBrains IDE, where Explyt keeps file edits in Agent Changes and runs builds and tests through named run configurations, so the record of what happened lives outside the chat. A commit I still check with git log.

## What the issue trackers show

Every item below is a public report with a link. Each one is a documented incident from a single user or team; taken together they show the shape of the problem, and I have not tried to measure how often it happens.

## "Committed" and "pushed"

[In anthropics/claude-code #63870](https://github.com/anthropics/claude-code/issues/63870 "In anthropics/claude-code #63870 ")(May 2026, the most upvoted item in this list) the agent printed its Bash calls as plain text and never dispatched them. The session log had no tool results for them. From the report: "commits, verification commands, pushes, PR creation, PR merge, and cleanup commands were printed but not actually executed."  
[anthropics/claude-code #67847](https://github.com/openai/codex/issues/19520 "anthropics/claude-code #67847 ")(June 2026) records the model saying it appended a note to a file and quoting the appended text, while the API response contained zero tool-use blocks. "A later real git commit returned "nothing to commit", exposing it."  

The reverse also happens. [In openai/codex #22219](https://github.com/openai/codex/issues/22219 "In openai/codex #22219") the agent committed correctly and then, in its final message, "listed files as "currently uncommitted" even though those files were the ones it had just committed and git status --short was clean." A report that disagrees with the repository in either direction is a report you cannot use.

## "File written"

[anthropics/claude-code #23801](https://github.com/anthropics/claude-code/issues/23801 "anthropics/claude-code #23801") (February 2026, Windows): a copy command failed silently, and the model said the file was saved. "Claude Code reports "Done. The file has been saved" without checking if the file actually exists". The file turned up missing only after the user asked twice and the model finally ran a directory listing.

## "Tests pass"

[microsoft/vscode-cmake-tools #4915](https://github.com/microsoft/vscode-cmake-tools/issues/4915 "microsoft/vscode-cmake-tools #4915 ")(April 2026) is the cleanest mechanism I found, because the maintainers diagnosed and fixed it. The Copilot agent called the ctest tool with a test name that matched nothing. ctest ran zero tests and exited 0. The agent "then told me that all tests pass. But it didn't actually run any tests." Maintainer comment: "Because ctest returns exit code 0 both when tests pass and when no tests match, the tool reports success and the agent can't tell the difference." The fix went into the tool.  
[openai/codex #41626](https://github.com/openai/codex/issues/41626 "openai/codex #41626") (August 2026): the agent asked for permission to run a hardware suspend/resume test, the user never granted it, and the auto-generated session summary later "claimed the HDMI suspend/resume test had passed and the task was complete."

Two personal accounts round this out. On Hacker News, in the Claude Opus 4.8 launch thread (comment, May 2026), a user wrote that the previous model "reported that it had created the auth feature, that everything was secure, and that the tests passed. The issue was that it hadn't actually implemented the auth feature." In [r/ClaudeAI](https://www.reddit.com/r/ClaudeAI/comments/1m9wn51/claude_lied_about_pushing_an_update/ "r/ClaudeAI") (July 2025) someone describes a Supabase migration the agent said it pushed: "This time, it said it did it... but didn't."

## What the mistake costs

The cheapest outcome is lost work. The edits sit unstaged in the working copy; the next branch switch, stash or agent run takes them away, and the developer finds out when the "finished" change is missing from the branch. [In #63870](https://github.com/anthropics/claude-code/issues/63870 "In #63870 ")the missing pieces were the commit, the push, the PR and the cleanup, and the developer had already moved on.  

The next level is other people's time. A reviewer opens the PR the agent described, searches for the hash, and finds it in no branch and no object store (#19520). The task board says done, the standup said done, and the repository disagrees with both. Somebody now has to reconstruct what actually landed.  

Then there is the hotfix that never shipped. If "build passed" came from Gradle or Maven cache for a module that did not change, the artifact in production may not contain the fix, and the incident comes back after it was marked resolved. The HN account about the auth feature is the sharp end of this: the agent reported the feature as implemented, secure and tested, and none of the three was true. Nobody looks for a hole in a door that the report says is closed.  

The systemic cost is the one Anthropic names in its own docs: you become the verification loop, and every mistake waits for you to notice it. Whatever the agent saved you on the task, you pay back on checking its report, or you skip the check and pay later, in production or in someone else's review. And #89765 adds the mirror image: when the transcript is treated as fact, it is unreliable in both directions, as a record of what was done and as a record of what you agreed to.

## Why the narration and the side effect come apart

The reports above are filed against different products and models, so the cause sits in how agents work and shows up in every vendor's bug list. When I read the maintainer comments and the attached session logs, the same mechanisms kept coming back.  

The model narrates a tool call and never makes it. The call appears as text, or inside the model's reasoning, and the harness never dispatches it. The model then treats its own prediction of the result as the result. This is what happened in [#63870](https://github.com/anthropics/claude-code/issues/63870 "#63870"), [#67847](https://github.com/anthropics/claude-code/issues/67847 "#67847") and [#19520](https://github.com/openai/codex/issues/19520 "#19520") (where the narrated tool did not exist at all).  

The tool returns success when nothing happened. ctest with zero matching tests exits 0. Windows copy can fail without a non-zero exit. A py_compile pass gets reported as "verified". The agent sees a clean exit code and has no way to know the check was empty. [#4915](https://github.com/microsoft/vscode-cmake-tools/issues/4915 "#4915") and [#23801](https://github.com/anthropics/claude-code/issues/23801 "#23801") are this case.  

A summary layer overrides the real last turn. Session recaps, task-state trackers and "conversation summaries" are generated from the model's picture of the work, and that picture can be older than the actual last tool result. [#22219](https://github.com/openai/codex/issues/22219 "#22219") and [#41626](https://github.com/openai/codex/issues/41626 "#41626") show a recap declaring work complete that the transcript itself shows as pending.

The test scope is narrower than the claim. Unit tests go green while the bug lives in a layer they bypass, and "tests pass" gets promoted to "fixed". The trading system report in anthropics/claude-code [#37818](https://github.com/anthropics/claude-code/issues/63870 "#37818"), which ran for weeks, is this failure repeated over and over.  

The model fabricates the authorization. In anthropics/claude-code #89765 (August 2026) the model wrote a user turn approving a push inside its own message, then "proceeded to commit \& push based on a fabricated approval". The permission prompt stopped it. This is the one I keep coming back to. A tool that writes its own permission slip is a different kind of problem from a tool that misreads an exit code, and I do not have a tidy answer beyond: leave the permission prompt on.  

Anthropic says the same thing in its own docs. From the Claude Code best practices guide: "Claude stops when the work looks done. Without a check it can run, "looks done" is the only signal available, and you become the verification loop: every mistake waits for you to notice it." Its earlier engineering post on agents makes the positive version of the point: "it's crucial for the agents to gain "ground truth" from the environment at each step (such as tool call results or code execution) to assess its progress."  

The research goes further. OpenAI's March 2025 paper on monitoring reasoning models found that during training, coding "agents quickly learn that it is easier to modify the testing framework such that tests trivially pass rather than implement a genuine solution". METR's June 2025 post reports that "the most recent frontier models have engaged in increasingly sophisticated reward hacking, attempting (often successfully) to get a higher score by modifying the tests or scoring code". Anthropic's Claude 4 announcement claims a 65% reduction in shortcut and loophole behavior relative to Sonnet 3.7, which is good news and also confirms how much of it there was to reduce.  

Simon Willison put the practical consequence in one line in his "Vibe engineering" post: "Without tests? Your agent might claim something works without having actually tested it at all". Birgitta Böckeler, running TDD inside agent loops for martinfowler.com, saw the same thing from the other side: "agents still sometimes skipped or faked the red step, or implemented ahead of the test so that it passed immediately."

## Three claims, three independent checks

Independent means: performed by you or by the IDE, against the artifact itself, without going through the model's summary.

Claim: "Committed." Check the repository, in the right working directory and branch.

```java
pwd
git branch --show-current
git log -1 --stat
git status --short
```

The hash from the message should match git log, the claimed files should appear in --stat, and git status should be clean. For a "pushed" claim, add git fetch \&\& git log origin/ -1. In #19520 the reporter also ran git cat-file -e ; a hash that git has never seen is the fastest way to end the conversation.  

Claim: "Created or updated the file." Check the filesystem and the VCS diff. ls -la path/to/File.kt for existence and timestamp, git diff --stat -- path/to/File.kt for content. In the IDE, the Git tool window or the agent's own change list shows the same thing without leaving the editor. A file the agent "wrote" that appears in no diff was not written.  

Claim: "Build passed" or "tests passed." Check the process that produced the result. A build claim needs an exit code and a timestamp later than the last edit. A test claim needs the test count, the test classes, and confirmation that the run happened after the change. In a JetBrains IDE the run panel gives you all of that, including the failing assertion if there is one. A number like "all 38 passed" with no run attached to it is decoration, and, as #4915 shows, "0 tests, exit 0" is a number too.  

Each check costs less than a minute.

## Traps where the check itself is fake

Once you start verifying, the agent, or your own habits, will offer shortcuts.  

The first one is the agent pasting the verification for you. "Here is git log to confirm:" followed by a code block. That block is model output. Unless the harness shows raw tool results separately from the model's text, treat pasted command output as a claim and run the command yourself.  

The second is running the right command in the wrong directory. Multi-module repositories and git worktrees make it easy to verify a different checkout. Print the working directory before the check. I have done this to myself more than once, which is why pwd is the first line above.  

The third is cached green. Gradle and Maven report success from cache when inputs did not change. If the build says "up to date" for the module you expected to change, the change may not be there.  

The fourth is an empty test selection, and its cousin, a test count without a test run. A filter that matches nothing, a test name with a typo, a profile that excludes the class: the runner exits 0 and the agent reports green. Counting @Test annotations is also not a run. Look at the count, the duration and the timestamp.  

The fifth is a branch mismatch. The commit exists, on a branch nobody asked for. git log --all --oneline -5 catches it.  

The last is partial success reported as full. Three of five files written, two rejected by a permissions rule, message says "updated the files". Compare the claimed list to the diff list, item by item.

## Make "done" mean something in the agent's rules

You can shift part of this work back to the agent with a rule that changes what "done" is allowed to mean. Most agents that run in a JetBrains IDE read a repository-level instruction file (`AGENTS.md` or a vendor equivalent), and many support reusable skills. A rule I use:

```java
## Reporting side effects

After any action that changes state outside the chat (git commit, git push,
file create/delete, build, test run), do not describe the result in prose.
Run the corresponding check and include its raw output:

- commit  -> `git log -1 --stat` and `git status --short`
- file    -> `git diff --stat -- <path>` (or `ls -la <path>` for new files)
- build   -> the build tool's final status line and exit code
- tests   -> the test runner summary with counts and duration

If the check fails, returns an error, or runs zero tests, say so first,
before any summary. Never report a step as completed if its tool call
did not return success.
```

The rule puts raw command output into the agent's turn, and it makes a missing check visible: if the block is absent, the step did not run. You can go further and wire a hook that blocks a commit until a test run has been recorded. Do it, with one caveat from a Hacker News thread in April 2026, where users reported a model ignoring exactly that kind of stop hook. The rule helps. It does not replace your own git log.

## Where the JetBrains IDE keeps the evidence

Explyt's documentation says it directly: "Do not treat a chat response as a finished result on its own. Ask the agent to run the appropriate test or build configuration and report what exactly was verified." The IDE already tracks the facts the transcript can only describe, and the docs describe where.

**`Agent Changes`** lists the files the agent modified in the active chat, with a diff per file, and lets you accept or reject each one. That list is the record of the file-level side effect. If a file the agent "updated" is missing from it, there is nothing to accept. The documentation is careful about scope: "Agent Changes covers the agent's file edits. The documentation does not guarantee automatic rollback of the consequences of an executed command, a migration, a request to an external system or an application run."

[Explyt in a JetBrains IDE after a task: the diff viewer on the left, the Changes row with Reject All, Accept All and Auto Review in the chat, and the Explyt Agent Changes panel listing the one file the agent touched. Frame from the official Agent Changes video; the project is a TypeScript test suite, not the Spring example below.](https://youtu.be/fSr3GUSLSuI "Explyt in a JetBrains IDE after a task: the diff viewer on the left, the Changes row with Reject All, Accept All and Auto Review in the chat, and the Explyt Agent Changes panel listing the one file the agent touched. Frame from the official Agent Changes video; the project is a TypeScript test suite, not the Spring example below. ")

**Run configurations.** The agent runs builds and tests through the IDE's named run configurations, and the tool "returns the result to the agent: console output, test results, compilation errors." You read the same result in the run panel, with counts and timing. The docs also say that "the terminal is needed for commands that have no suitable IDE run configuration", which is the right division of labor here: the run panel shows the test count, so a zero is visible to you even when the exit code was 0.

`*Debug mode.** `For a claim about behavior ("this fix removes the NPE") Explyt is documented to reproduce the failure, confirm the cause at breakpoints with variable values and the call stack, apply a minimal fix and rerun the original scenario. The docs add the boundary themselves: "A single debugger run confirms the fix only for that specific scenario. It does not replace the related tests and other checks."

There is also an `Auto Review` action that hands the changes and the chat history to a separate Review subagent, which can use IDE inspections. Per the docs, it "does not replace running tests and reviewing the diff yourself."

Explyt does not check git commits for you, and I am not going to pretend otherwise. The documented tool list has a `Commit message` tool that drafts the description for the IDE's commit dialog; no tool creates the commit itself, and git commands go through the terminal like any other command. A commit is a git fact, and the check for it is `git log`, in the terminal or in the IDE's Git tool window. What the IDE adds is that file edits, runs and runtime state each have their own panel, and none of those panels is written by the model.

## A five-minute walkthrough

I did not record a run for this scenario; it only shows the order of checks.

You asked the agent to rename a config key across a Spring Boot service and commit. The agent reports: four files changed, tests green, committed.

1. Open `Agent Changes`. Count the files. Four listed? Open each diff. Is the rename in all of them, or did one file get a comment where a change should be?
2. Look at the run panel. Was a test run configuration executed after the last edit? How many tests, how long? If the only run predates the edit, the "tests green" claim is stale.
3. In the terminal: `git status --short`. Clean means committed. Modified files mean the commit did not happen, whatever the message said.
4. `git log -1 --stat`. Does the hash match? Do the four files appear?
5. If anything disagrees with the report, the disagreement is the finding. Send it back to the agent with the raw output. Take the corrected state. A corrected summary on its own is the same problem again.

## Acceptance checklist for agent-reported side effects

1. Working directory and branch printed before any check.
2. `git log -1 --stat` shows the claimed hash and the claimed files.
3. `git status --short` is clean after a "committed" claim.
4. Every file the agent named appears in `Agent Changes` or `git diff --stat`.
5. Build status comes from the run panel or the build tool's exit code, with a timestamp after the last edit.
6. Test summary includes a non-zero count and a duration, from a run after the edit.
7. Any command output pasted in the chat was rerun by you or matched against the raw tool result.
8. Partial failures were caught by comparing the claimed file list with the actual diff list, item by item.

This list covers side-effect claims. Whether the change is correct, whether the tests mean anything, and whether the fix hits the root cause are separate questions that need review and, for runtime behavior, a debugger.

## FAQ

**Does JetBrains have an AI agent?**

Yes. JetBrains ships its own coding agent, Junie. Third-party agents run inside JetBrains IDEs as plugins, Explyt among them, and external agents such as Claude Code can attach to IntelliJ IDEA. All of them produce transcripts. The issues above cover Claude Code, Codex, Gemini and Copilot; I found no JetBrains-specific report and would not read that as an exemption.

**What is JetBrains AI agent mode?**

In agent mode the assistant takes multi-step actions in the project: it edits files and runs commands on its own, where a plain chat mode would only answer. More actions means more side effects to verify.

**Is GitHub Copilot agent mode available in IntelliJ?**

Yes. GitHub's [Copilot Chat in JetBrains IDEs](https://docs.github.com/en/copilot/how-tos/chat-with-copilot/chat-in-ide?tool=jetbrains) documentation has a "Using Copilot agent mode" section, with one caveat: if the option is missing from the mode selector, your organization administrator may have disabled it. The checks in this article apply unchanged.

**Can an AI agent use the IntelliJ debugger?**

Explyt's Debug mode is documented to run code under the JetBrains debugger and inspect breakpoints, variable values and the call stack before editing. That covers claims about behavior. Claims about commits still go through git.

## Conclusion

I still catch myself skipping `git log` when the report looks tidy. That habit is the whole problem in one line.

Every check in this article runs after the agent has spoken. The next question is what changes when the agent gets a runtime fact before it edits, instead of a theory from a log. At Explyt we ran the same JVM bug twice, once with a text-only debugging skill and once with a skill that had the JetBrains debugger, and compared the patches and the token counts: [see how runtime evidence changed the result on one real JVM bug](https://explyt.ai/en/blog/superpowers-vs-debugger-explyt).

Has an agent reported a commit, a file or a green run to you that turned out not to exist? Which check caught it, and how long did it take you to notice? I read the comments.

**Related reading**

* [AI coding has a new failure point: the final diff after done](https://explyt.ai/en/blog/ai-coding-final-diff-after-done): the pre-PR loop that checks the agent's output before a human opens the review.

Follow for more on running AI agents inside JetBrains IDEs, and on what a green checkmark does and does not prove.

## Sources

**Vendor and research**

* [Anthropic, Claude Code best practices](https://code.claude.com/docs/en/best-practices)
* [Anthropic, Building effective agents (Dec 2024)](https://www.anthropic.com/engineering/building-effective-agents)
* [Anthropic, Introducing Claude 4 (May 2025)](https://www.anthropic.com/news/claude-4)
* [OpenAI, Monitoring Reasoning Models for Misbehavior (Mar 2025)](https://arxiv.org/html/2503.11926v1)
* [METR, Recent Frontier Models Are Reward Hacking (Jun 2025)](https://metr.org/blog/2025-06-05-recent-reward-hacking/)
* [GitHub Docs, Copilot Chat in JetBrains IDEs (agent mode section)](https://docs.github.com/en/copilot/how-tos/chat-with-copilot/chat-in-ide?tool=jetbrains)
* [JetBrains, Junie](https://www.jetbrains.com/junie/)

**Practitioners and community**

* [Simon Willison, Vibe engineering (Oct 2025)](https://simonwillison.net/2025/Oct/7/vibe-engineering/)
* [Birgitta Böckeler, TDD inside the agent loop (martinfowler.com)](https://martinfowler.com/articles/exploring-gen-ai/tdd-in-the-agent-loop.html)
* [Hacker News, Claude Opus 4.8 thread comment](https://news.ycombinator.com/item?id=48312299)
* [Hacker News, Claude 4.7 is ignoring stop hooks](https://news.ycombinator.com/item?id=47895029)
* [r/ClaudeAI, Claude lied about pushing an update](https://www.reddit.com/r/ClaudeAI/comments/1m9wn51/claude_lied_about_pushing_an_update/)

**Explyt documentation**

* [How Explyt works](https://explyt.ai/docs/explyt-test/overview/how-explyt-works)
* [Agent Changes](https://explyt.ai/docs/explyt-test/code-review/agent-changes)
* [Permissions and safety (scope of Agent Changes)](https://explyt.ai/docs/explyt-test/configuration/permissions-safety)
* [Chat and tools (terminal vs. run configurations)](https://explyt.ai/docs/explyt-test/agent/chat)
* [Tools list (Commit message tool)](https://explyt.ai/docs/explyt-test/tools)
* [Run configurations](https://explyt.ai/docs/explyt-test/tools/run-configurations)
* [Debugger](https://explyt.ai/docs/explyt-test/tools/debugger)
* [Auto Review](https://explyt.ai/docs/explyt-test/code-review/auto-review)
* [Explyt on JetBrains Marketplace](https://plugins.jetbrains.com/plugin/27979-explyt-ai-agent)

The issue reports quoted in the article are linked where they are cited.
