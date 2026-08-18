---
title: "AI-Assisted Unused & Dead Code Removal"
slug: "ai-assisted-unused-dead-code-removal"
date: "2026-06-28T07:19:42+00:00"
description: "Why Your Codebase Is Forcing AI to Underperform, and What to Do About It Your AI coding assistant is only as good as the codebase it works on. If your - by Frank Delporte"
canonical: "https://www.azul.com/blog/ai-assisted-unused-dead-code-removal/"
authors:
  - "erikcostlow"
  - "frankdelporte"
image: "ai-assisted-dead-code-removal.avif"
categories:
  - "AI"
  - "Conference"
  - "Debugging"
  - "Developer Tools"
  - "Events"
  - "Java"
tags:
related_posts:
frozen: false
---

Why Your Codebase Is Forcing AI to Underperform, and What to Do About It
------------------------------------------------------------------------

<figure class="alignleft size-large is-resized">
 <img fetchpriority="high" decoding="async" width="1024" height="576" src="ai-assisted-dead-code-removal-1024x576.avif" alt="" class="wp-image-124402" style="width:250px">
</figure>

Your AI coding assistant is only as good as the codebase it works on. If your Java application carries years of dead \& unused code, and most do, the AI spends its reasoning budget on code that nobody runs. The result is more hallucinations, worse suggestions, and higher token costs. Here's what to do about it.

This post walks through the problem and provides a practical workflow that uses Azul Intelligence Cloud's Code Inventory alongside an AI coding agent to find and remove dead code from production Java applications.

The Context Window Is Your AI's Working Memory
----------------------------------------------

To understand why unused code hurts AI performance, you first need to understand how large language models (LLMs) process code. When you ask an AI assistant to add a feature, fix a bug, or refactor a module, it doesn't just look at the file you're pointing at. It loads your prompt, your chat history, related files, imported dependencies, and anything else it needs to reason about your request. All those files are combined into the **context window**.

The context window is the AI's working memory for a given task. It's measured in **tokens**, roughly chunks of words or code fragments. Importing a function signature might cost around 20 tokens. A moderately complex file might cost 5,000. Modern models offer context windows of 200,000 tokens or more, which sounds generous until you're working on a large, interconnected monolith.

Context cost isn't about how many files are in your repo. It's about how much the AI has to load to answer one question. On a clean, modular codebase, the AI can pull in just the function it needs and stop. On a tangled, legacy-heavy codebase, that same request triggers a cascade: deep import chains, [god objects](https://en.wikipedia.org/wiki/God_object), dynamic dispatchers, and code that nobody has touched in years.

The AI assistant's output becomes degraded and you get more hallucinations, incorrect method signatures, wrong parameter types, and so on.

In addition to the problem of bad code generation, overloading the context window also impacts your budget. The cost of using LLM models is calculated based on the number of tokens used. So, with each request dragging in a huge amount of unnecessary content, the cost of handling a request increases significantly.

Unused \& Dead Code Is More Common Than You Think
-------------------------------------------------

In 2025, [Azul surveyed Java engineers about the state of their codebases](https://www.azul.com/newsroom/azul-2025-state-of-java-survey-report/). **62% report a loss of DevOps productivity due to dead or unused code.** For developers, that burden shows up as cognitive load. They spend time understanding code that serves no purpose. For AI assistants, it shows up as wasted tokens and degraded reasoning.

But here's the part that surprises most teams: **most unused code doesn't look dead.**

Classic dead code, for example a private method in a single class that never gets called, is easy to catch. Your IDE can find it. IntelliJ has a built-in analyzer for exactly this case. Static analysis tools handle it well.

The real problem is the code that appears to be alive but never actually runs in production. Some examples:

* **Spring beans and services** wired up via dependency injection. They are technically reachable, but they may never get invoked.
* **Feature-flagged modules** that were gated off, never enabled in production, and quietly forgotten.
* **Reflection-driven components** that a static analyzer assumes are reachable because they technically could be.
* **Old API controllers** from deprecated workflows are still present and fully wired, but never receive traffic.

Static analyzers answer the question *"Could this code be called?"* They don't answer *"Was this code actually called in production?"* For Java applications, those are very different questions.

Runtime Evidence Changes the Picture
------------------------------------

This is where [Azul Intelligence Cloud's Code Inventory](https://www.azul.com/products/components/code-inventory/) feature comes in. The Intelligence Cloud Agent running alongside the JVM instances in your production environment forwards data that the JVM already collects to the Intelligence Cloud system. Rather than checking the reachability from source code, Code Inventory reports what actually executes at runtime in your production environment. It flags everything else as unused, regardless of how reachable it looks.

The output is a structured data file generated by the Intelligence Cloud API that maps your entire codebase to real production behavior. Instead of "this code *could* be called," you get "this code *was never* called during the observation window."

That distinction changes what you can act on. You can now tell the difference between:

* Code that runs in development but not in production (environment drift).
* Code that runs in some environments but not others (feature flag divergence).
* Code that has never run at all across any observed environment.

For engineering managers, that means removal decisions backed by production data, not gut feel. For Java developers, it's a starting point for cleanup that your IDE can't give you.

AI-Assisted Code Removal Workflow
---------------------------------

Identifying unused code is the easy part. Removing it safely without breaking tests or missing a hidden dependency is where most teams get stuck. That's where an AI coding agent earns its keep.

A typical workflow using this approach follows these steps:

1. **Instrument and Observe.**Deploy Code Inventory alongside your application and let it run. For a meaningful picture, you want to cover a representative time window: a full release cycle, a fiscal year-end or a major traffic event. Just let your application run normally while the data accumulates.
2. **Export the Code Inventory Report.** When you're ready to act, export the Code Inventory report. The result is a [Parquet file](https://parquet.apache.org/) containing every class (and optionally, every method) in your application, tagged with whether it ran during the observation period.
3. **Bring the Data to Your AI Agent.** Code Inventory data pairs naturally with an AI coding agent. Such agents handle structured input data very well and understand any codebase out of the box. Load the Code Inventory report into an AI coding agent like Claude Code, or an autonomous agent like Devin AI and prompt it to:
   * Analyze the report against your repository.
   * Identify which unused classes have no external dependencies (safe candidates for removal).
   * Map the dependency graph for unused code that *is* referenced elsewhere.
   * Create a safe, incremental removal plan.
4. **Address the Unit Test Problem.** One of the most common concerns when removing legacy code is the test suite. Removing an unused class breaks any unit tests written against it, and that breaks the build.   

   Claude Code can also handle this problem because a test that only validates deleted behavior is no longer valid. Such tests are just keeping dead code artificially alive. Removing both the code and its tests together eliminates the broken build and genuinely reduces build time, since you're no longer validating behavior that doesn't need to exist.
5. **Iterate and Scale.** You don't have to remove everything at once. Start with the most obvious candidates, such as self-contained packages, clearly gated features, and old API endpoints you *know* are retired. Remove those in small batches, verify the build, and increase your build confidence in the process. As your observation window grows and your trust in the data increases, you can tackle larger chunks.

The Business Case for Cleaning Up Unused \& Dead Code
-----------------------------------------------------

Unused \& dead code removal pays off in more places than AI performance alone:

* **Developer onboarding.** Every new developer who joins your team has to build a mental model of your application. Dead code pollutes that model. Clean module boundaries mean faster ramp-up, less time spent on archaeological expeditions through code that nobody owns or understands.
* **PR review speed.** Smaller, cleaner codebases have shorter diffs, more focused reviews, and fewer discussions about whether a piece of code is still relevant.
* **Build and test cycle time.** Every test you're running against unused code is time wasted on every CI run. Removing the code also removes the test, which permanently shortens your feedback loop.
* **Future AI scalability.** Your team generates increasing amounts of code when using AI. The new code coming in will be cleaner and more modular if the AI isn't fighting its way through a legacy tangle of unused and dead code to figure things out.

Practical Considerations
------------------------

Not every piece of unused code is equally safe to remove. Teams working through their first cleanup run into the same questions. Here are the ones that come up most often, and how to handle them.

* **Code that runs infrequently.**Year-end reporting, financial close processes, and disaster recovery paths are use cases that need a longer observation window to show up as "used." Don't rush to remove anything seasonal. Use your intuition to identify which parts of the codebase you've been suspicious about for a while and start there.
* **Error handling code.**Defensive code that handles rare failure modes is worth keeping even if it never ran during your observation window. Add logging to it so you know when/if it fires in the future, and extend your observation period before making a removal decision.
* **Mistakenly removed code.**It's in your Git history. If you ever need to resurrect a removed feature, the code is one git revert away. The cost of removal is low. The cost of clutter is ongoing.

The Bigger Picture
------------------

The cheapest line of code is the one that isn't there. You don't have to read it, maintain it, test it, or have your AI wade through it every time you ask a question about your application.

Getting from a legacy monolith to a codebase where AI does most of the work doesn't require a big-bang rewrite. It starts with removing what you no longer need, based on what production instances actually run, and creating the clean module boundaries that let both developers and AI assistants reason clearly about what's left.

Azul Intelligence Cloud's Code Inventory feature provides the runtime evidence you need to do this with confidence. Pair it with an AI coding agent, and you have a workflow that works on codebases with a long history and accumulated complexity for years.

Start by instrumenting your application, watch the data come in and then let the AI help you find your way out.

What's Next
-----------

If you'd like to see this blog in action, register for the [AI4J Leadership Summit](https://www.azul.com/webinars/ai4j-the-ai-leadership-summit/register/) on June 30. Erik Costlow, Sr Director of Product Management for Azul Intelligence Cloud, will walk through a live demo showing you how to use Azul Code Inventory and AI coding tools together to find and delete unused code in a Java application.

<br />
