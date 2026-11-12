---
title: "Skills across coding assistants"
date: "2026-11-13"
description: "Claude Code reads skills from its own folders, while other assistants also scan .agents/skills. Where each one looks, and how symlinks fix it."
canonical: "https://blog.frankel.ch/skills-across-coding-assistants/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "AI"
  - "Tools"
related_posts:
  - "writing-an-agent-skill"
  - "how-to-create-test-install-agent-skills"
  - "best-practices-for-working-with-ai-agents-subagents-skills-and-mcp"
  - "context-is-a-budget-ai-coding-agents-team-process"
---

I love Claude Code, but I use it so much that I regularly exhaust my weekly quota before the week ends. When that happens, I usually switch to other free-tier CLIs, _e.g._, Google Antigravity, GitHub Copilot, and, just last week, Mistral's Vibe. I really wanted to try Vibe to evaluate the feasibility of moving away from Claude Code in favor of a more sovereign solution.

Vibe only offers a single model by default, Mistral Medium 3.5. It hallucinated results badly, and I noticed it immediately. When I tried to understand what happened, I realized it hadn't triggered any of my lovingly crafted skills. The reason is that Claude Code stores skills in custom locations, in opposition to most other assistants.

I went through each assistant's documentation, so you don't get bitten the same way.

| Assistant | Personal | Project |
| --- | --- | --- |
| Claude Code | `~/.claude/skills` | `./.claude/skills` |
| Antigravity | `~/.gemini/skills` | `./.agents/skills` |
| Vibe | `~/.vibe/skills`, `~/.agents/skills` | `./.vibe/skills`, `./.agents/skills` |
| Copilot CLI | `~/.copilot/skills`, `~/.agents/skills` | `./.github/skills`, `./.claude/skills`, `./.agents/skills` |

The [specification](https://agentskills.io/client-implementation/adding-skills-support#where-to-scan) is pretty clear about it:

| Scope | Path | Purpose |
| --- | --- | --- |
| Project | `<project>/.<your-client>/skills/` | Your client’s native location |
| Project | `<project>/.agents/skills/` | Cross-client interoperability |
| User | `~/.<your-client>/skills/` | Your client’s native location |
| User | `~/.agents/skills/` | Cross-client interoperability |

My conclusion is straightforward. Put skills in `~/.agents/skills` and `./.agents/skills`, then symlink `~/.claude/skills` and `./.claude/skills` to them. If the `.claude/skills` folders already exist, move their content to the `.agents/skills` ones first.

```bash
ln -s ~/.agents/skills ~/.claude/skills
ln -s ../.agents/skills .claude/skills        # 1
```

1. Run it from the project root

I love Claude Code, but it isn't a good citizen here. That's one more reason for me to try Vibe more seriously.

**To go further:**

* [Agent skills specification: where to scan](https://agentskills.io/client-implementation/adding-skills-support#where-to-scan)
* [Google: Antigravity App skill locations](https://antigravity.google/docs/skills#antigravity-app-skill-locations)
* [Vibe: skill locations](https://docs.mistral.ai/vibe/code/cli/skills#locations)
* [Copilot: finding and storing skills](https://docs.github.com/en/copilot/concepts/customizations/agent-skills#finding-and-storing-skills)

<hr>

_Originally published at [A Java Geek](https://blog.frankel.ch/skills-across-coding-assistants/) on November 8<sup>th</sup>, 2026._
