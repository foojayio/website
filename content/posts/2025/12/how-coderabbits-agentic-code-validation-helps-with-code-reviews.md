---
title: "How CodeRabbit's Agentic Code Validation helps with code reviews"
slug: "how-coderabbits-agentic-code-validation-helps-with-code-reviews"
date: "2025-12-07T11:10:21+00:00"
lastmod: "2025-12-07T11:14:09+00:00"
description: "Instead of chasing higher benchmark scores or relying on traditional metrics, CodeRabbit focuses on how AI systems actually perform in live engineering environments through custom evaluation methods, some visible directly on the PRs we review."
authors:
  - "ewa-szyszka"
image: "https://foojay.io/wp-content/uploads/2025/12/ee88f311-e2fd-4bb5-acb5-2a07ceff098d.webp"
categories:
  - "AI"
  - "CodeRabbit"
  - "Developer Tools"
  - "Machine Learning"
tags:
related_posts:
frozen: false
---

The [2025 Stack Overflow survey](https://survey.stackoverflow.co/2025/) reveals a paradox: while 84% of developers express confidence in adopting AI tools, nearly half (48%) still distrust the accuracy of their outputs. This tension between optimism and skepticism has reshaped how teams think about quality assurance.
![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764049813672/6ad5aea0-625a-4f7f-bf4c-a0b3c8a4903f.png?auto=compress,format&format=webp)

**From PRD to PR in days (not weeks)** {#heading-from-prd-to-pr-in-days-not-weeks}
----------------------------------------------------------------------------------

The bottleneck in software development has fundamentally shifted from writing code to validating it.

In the early days of AI-assisted development, the workflow was straightforward: AI suggested code, humans read the suggested snippet and then decided whether or not to accept that suggestion. Tab completion wrote boilerplate. Copilot suggested functions. But a senior engineer still manually validated and chose each line of code to ensure its quality, structure, and safety before making a pull request.

Today's reality is different. Advanced reasoning models like OpenAI's o1 can decompose complex requirements and generate entire features. This set the flywheel in motion for the era of agentic code generation, where humans along with agents play an active role in generating large swaths of code. The difference between accepting AI-generated code one snippet at a time and adding in AI-generated features is significant. Devs are more likely to miss issues with its quality, structure, and safety.

Reviewing AI-generated code also takes much more time. The bottleneck isn't writing code anymore - it's trusting it.

**The AI-generated code crisis nobody's talking about** {#heading-the-ai-generated-code-crisis-nobodys-talking-about}
---------------------------------------------------------------------------------------------------------------------

![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764050727379/470609c8-ac04-40f4-8406-efdd62c734f2.png?auto=compress,format&format=webp)

Engineers are right to be skeptical, since [**over 40% of AI-generated code still contains security flaws**](https://cyber.nyu.edu/2021/10/15/ccs-researchers-find-github-copilot-generates-vulnerable-code-40-of-the-time/) and here is what AI-generated code often gets wrong:

* **Dependency explosion**: A simple prompt for a "to-do list app" can generate 2-5 backend dependencies depending on the model. Each dependency expands your attack surface. Worse, models trained on older data suggest libraries with known CVEs that were patched after their training cutoff.
* **Hallucinated dependencies**: AI invents package names that don't exist. Attackers register those names in public repositories with malicious code. Developers install them blindly. This attack vector, called "slopsquatting," is uniquely enabled by AI code generation.
* **Architectural drift**: The AI swaps out your cryptography library, removes access control checks, or changes security assumptions in ways that look correct but behave insecurely. These are the bugs that static analysis misses and humans don't catch until production.

**Why did reasoning models change everything?** {#heading-why-did-reasoning-models-change-everything}
-----------------------------------------------------------------------------------------------------

A few years back, applying AI to a collaborative workflow like Code Review met with a degree of amused skepticism. The bots would catch your missing semicolons, flag unused variables, and maybe (if you were lucky) warn you about a potential null pointer. They were fast, cheap, and fundamentally shallow.

At CodeRabbit, when we started to apply Generative AI, we realized this problem pretty early and developed a technique that you see on some of our older PRs, called monologue where the model thinks through the issue and shares reasoning behind an issue comment.

With the launch of reasoning models like OpenAI's o1 and o3 the models actually think through the problem thanks to the **monologue feature** on CodeRabbit. When you ask GPT-4o to review code, it pattern-matches against things it's seen before and code review feedback is mostly superfluous. When you ask GPT-5 or Claude Sonnet 4.5, it spends time reasoning through your code's logic, tracing execution paths, considering edge cases, and understanding intent. This was important for successful code review. But there is a catch!

**What makes review more "agentic"?** {#heading-what-makes-review-more-agentic}
-------------------------------------------------------------------------------

Many thought that applying the same reasoning models to review the code they generated would cut slop or find bugs, but this wasn't entirely true. The two major missing pieces were effective context assembly (context engineering) and verifying the veracity of the results.

Traditional code validation tools are reactive. You run a linter, it tells you about unused variables. You run a static analyzer, it warns about null pointer exceptions. You run security scanners, they flag hardcoded secrets. Each tool does one thing, in isolation, with no context about what you're actually trying to build.

With generative AI, you might integrate these tools into your review pipeline. However, neither the model nor the tools are intelligent enough to effectively filter out noise and highlight crucial signals, leading to context clogging.

To effectively counter that, we developed techniques to engineer and manage the context for each model in the review pipeline. For example: We would prepare the list of most important issues suggested by all the tools in an instructive manner to the reasoning model, for better solutions. We also added a verification agent that checks and grounds the review feedback.

Here are some examples from the open-source PRs.

**Static analysis**: AST parsing with tools like ast-grep to understand code smells.
![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764049990279/6dc6960a-397f-45eb-bc56-1e206c895ee9.png?auto=compress,format&format=webp) ![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764049974691/04467ffa-0ffa-40c8-aaa9-2e39f3729048.png?auto=compress,format&format=webp)

**Incremental analysis**: Only validating wAgentiAgenhat changed, not your entire codebase.
![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764050007622/669a2b39-d1b7-453d-90bb-d1c0304394a5.png?auto=compress,format&format=webp) ![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764050040318/39ea9e7c-da40-4bb2-9462-011aea5f5855.png?auto=compress,format&format=webp)

**Security issues**: Prompt injection attacks and edge case generation.
![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764050064236/1e13090d-cb04-467a-97d1-0e39a48a584d.png?auto=compress,format&format=webp)

**Name refactoring**: Suggesting better variable and function names based on usage.
![](https://cdn.hashnode.com/res/hashnode/image/upload/v1764050080527/7993c838-7e2a-40c2-8a9a-6f7c5fe7bcbb.png?auto=compress,format&format=webp)

The "agentic" part means the AI decides which tools to run, interprets the results, and takes action. Think of it like having a senior engineer who knows when to dig deeper and when something is *not* fine.

**How CodeRabbit closes the AI code trust gap** {#heading-how-coderabbit-closes-the-ai-code-trust-gap}
------------------------------------------------------------------------------------------------------

Instead of chasing higher benchmark scores or relying on traditional metrics, CodeRabbit focuses on how AI systems actually perform in live engineering environments through custom evaluation methods, some visible directly on the PRs we review.

The technique of agentic code validation happens on each pull request reviewed by CodeRabbit; however, everything runs in **isolated, sandboxed environments** , what we call "tools in jail." As described in our [Security Posture](https://www.coderabbit.ai/blog/our-security-posture-how-we-safeguard-your-repositories), this approach ensures that verification agents can safely execute, inspect, and even stress-test code without ever compromising user data or infrastructure [integrity.](http://integrity.at/)

Agents excel at catching common vulnerabilities, analyzing patterns across thousands of lines, and running comprehensive test suites. They're designed to surface issues that are tedious or time-consuming for humans to catch manually. But agentic code validation isn't going to replace code reviews entirely. Instead, it frees developers to focus on what humans do best: architectural reasoning, business logic validation, and nuanced security considerations. The human-in-the-loop and agent-in-the-loop processes can coexist, providing redundancy and complementary reasoning similar to peer programming.
