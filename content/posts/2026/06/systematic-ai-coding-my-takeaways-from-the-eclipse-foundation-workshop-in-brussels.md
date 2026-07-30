---
title: "Systematic AI Coding: My Takeaways from the Eclipse Foundation Workshop in Brussels"
slug: "systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels"
date: "2026-06-23T19:48:42+00:00"
lastmod: "2026-06-24T06:32:24+00:00"
description: "Most developers using AI tools are still guessing. The Eclipse Foundation's first AI Coding Workshop in Brussels was built to change that. It's a brand - by Frank Delporte"
authors:
  - "frankdelporte"
image: "/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-jonas-scaled.jpg"
categories:
  - "AI"
  - "Events"
tags:
related_posts:
  - "foojay-podcast-99"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "agentic-was-everywhere-at-money20-20-amsterdam-once-i-started-looking"
  - "ai-gives-time-not-confidence-developer-productivity-toolkit"
frozen: false
---

Most developers using AI tools are still guessing. The Eclipse Foundation's first [AI Coding Workshop](https://aieclipse.org/ai-workshop/) in Brussels was built to change that. It's a brand new format they launched in Brussels, which makes sense: most of the Eclipse event team is based there. They plan to bring it to more cities from here, so keep an eye out if you want to attend such a workshop in the future. They offered 10 free tickets to share with the BeJUG and Foojay community. And in all honesty, I used one for myself. [Jonas Helming](https://www.linkedin.com/in/jonas-helming-76303b28/) from [EclipseSource](https://eclipsesource.com/) led the workshop, with a fun quiz hosted by [Thomas Froment](https://www.linkedin.com/in/tfroment/) in the afternoon. Here's what I took away.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-jonas-scaled.jpg"><img fetchpriority="high" decoding="async" width="1024" height="578" data-id="124353" src="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-jonas-1024x578.jpg" alt="" class="wp-image-124353"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-food-scaled.jpg"><img decoding="async" width="578" height="1024" data-id="124351" src="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-food-578x1024.jpg" alt="" class="wp-image-124351"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-thankyou-scaled.jpg"><img decoding="async" width="1024" height="578" data-id="124350" src="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-thankyou-1024x578.jpg" alt="" class="wp-image-124350"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-request-model-scaled.jpg"><img loading="lazy" decoding="async" width="1024" height="578" data-id="124352" src="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-request-model-1024x578.jpg" alt="" class="wp-image-124352"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-sponsors-scaled.jpg"><img loading="lazy" decoding="async" width="578" height="1024" data-id="124349" src="/images/posts/2026/06/systematic-ai-coding-my-takeaways-from-the-eclipse-foundation-workshop-in-brussels/eclipse-ai-brussels-sponsors-578x1024.jpg" alt="" class="wp-image-124349"></a>
 </figure>
</figure>

From Vibe to Systematic {#h2-0-from-vibe-to-systematic}
-------------------------------------------------------

The workshop kicked off with an important framing question: where are we in the AI coding journey? Jonas positioned AI assistance on a scale from 1 (basic code suggestions) to 5 (fully autonomous). We are now at level 4 with tools like Claude Code. Level 5 is approaching when fully cloud-based autonomous agents will be available.

Most developers today are still somewhere in the "vibe coding" zone. Vibe coding is the *what, not how* approach: you describe what you want, don't look at the generated code too closely (or not all), and focus on the result. It can work well for POCs and low-complexity problems on well-understood domains. But it quickly crumbles once you move into real projects with real complexity.

The fundamental shift the workshop is about, is moving beyond the experimentation phase toward something repeatable and systematic. That's what Jonas calls *Systematic AI Coding*: understanding how coding agents actually work, and building workflows around them, not just prompting and hoping.

How a Coding Agent Actually Works {#h2-1-how-a-coding-agent-actually-works}
---------------------------------------------------------------------------

One of the most useful sessions was the breakdown of what happens inside a coding agent. It's easy to think of it as a magic box, but the mechanics matter a lot for using it well.

A coding agent takes several inputs: the system message, user messages, assistant responses, your codebase, and any additional context you provide. That context includes things like project context, task context (your plan), and the skills or tools available to the agent. On top of that, you can have optional agent layering, like a model router that picks the right model for the task.

From there, the agent uses tools, calls the LLM, and generates a change set: files, actions, context updates, MCP calls. Everything flows through what Jonas describes as part of the *Request Model*: tools + messages + tool messages + tool responses. The whole thing boils down to one word: CONTEXT. Context is everything.

A key insight: **the LLM is stateless and forgets immediately.** Every time it runs, you need to resend the full conversation history for it to understand what happened before. This has a real consequence as the context gets polluted and the cost rises because extra tokens are needed to handle the input. And there's a subtle trap called the "dumb zone": efficiency actually drops before you hit the context window limit. According to some experiments, this can already start at roughly ±40% of the context capacity. So a session that feels like it's still working might already be producing worse results.

The Workflow {#h2-2-the-workflow}
---------------------------------

Once you understand how agents work, the practical workflow makes a lot more sense. It looks roughly like this: Your AI Agent generates output, and you need to **Review or Decide**. From there you have several options: escape (abort), refine (adjust the prompt), redo (start a fresh chat), divide the task further, compact the session, adjust the agent configuration, or commit what's working. Then repeat.

Two important take-aways from Jonas here:

* **Don't be afraid to throw away changes and start over.** A fresh session with a cleaner, better-scoped prompt will often outperform trying to rescue a session that's gone sideways. The bad feeling you may have of "lost coding work" is real, but it's not your work. A tool did it.
* **Compact the session yourself, don't rely on the agent to do it.** If you let the agent compact automatically, you lose control over what context gets dropped. Doing it yourself means you decide what's kept.

One tip I should follow: when your AI tool is working, don't start another session in another project. Look out the window and think about the actual problem you're solving within the project to better handle the result. Context-switching is expensive for humans too.

Time Changes When You Use AI {#h2-3-time-changes-when-you-use-ai}
-----------------------------------------------------------------

Without AI, a developer typically spends roughly 70% of time on design and coding combined, and 30% on debugging. With AI, the split looks very different:

* Design: \~30%
* Review: \~30%
* Debugging: \~35%
* Actual AI-generated code: \~5%

The work doesn't disappear, but it shifts. You spend more time thinking, reviewing, and debugging, and less time typing code. This has implications for how you approach a task.

**Design** before you ever prompt the agent:

1. Think about the task and if it should be split
2. Describe your intent with precision
3. Provide examples, relevant files, and existing implementations
4. Give the agent a way to validate its own output
5. Apply the "good friend" metaphor: share the tips and tricks from your own codebase that you'd tell a new colleague

**Review** isn't optional:

1. First, find blockers
2. Decide the follow-up action (refine, redo, split, etc.)
3. Only if there are no remaining blockers: do a detailed review to finish the task

Done ≠ Ready {#h2-4-done-ready}
-------------------------------

When an agent says it's done, that's not a green light to push. AI-generated code can quietly lower the quality, introduce security risks, and produce "work-slop" that frustrates your coworkers. Before pushing, do a review the way a careful coworker would.

What Companies Need to Enable This {#h2-5-what-companies-need-to-enable-this}
-----------------------------------------------------------------------------

Jonas highlighted a few important requirements that a company needs to provide to its developers if it wants to make systematic AI coding actually happen:

* **Tools** (which cost money)
* **Time** to learn and experiment
* **Space** to experiment and share learnings across the team

That third one is often underrated. Individual experimentation is great, but if there's no mechanism to share what works, the team never levels up together.

On the Eclipse Side {#h2-6-on-the-eclipse-side}
-----------------------------------------------

There was also a short quiz by Thomas Froment that covered some Eclipse Foundation projects worth knowing about.

* **Open VSX** is an open-source, vendor-neutral alternative to the Visual Studio Marketplace: an [open registry](https://open-vsx.org/), an [open-source project](https://projects.eclipse.org/projects/ecd.openvsx), and a working group.
* [**Theia IDE**](https://theia-ide.org/) built on the [Theia Platform](https://theia-ide.org/theia-platform/), ## a modern, AI-native IDE for cloud and desktop. It has many handy AI-tools for developers, like showing every step an agent takes, token usage, a `@PR Review` agent that you can give a PR number to checkout, build, run, and create a review plan, etc.

Final Thoughts {#h2-7-final-thoughts}
-------------------------------------

Thanks to the Eclipse Foundation for the free tickets, for the perfect organization, and to everyone from BeJUG and Foojay who joined! This was their first workshop in this format, and I hope they bring it to many more cities soon. Thanks also to the sponsor (Open VSX) for a well-equipped venue and good food throughout the day.

It was a dense, practical day. This summary is only a small part of all the tips, tricks, hints, pitfalls, and all the other knowledge shared by Jonas, in over four hours. He runs a longer version of this workshop at companies and events. If you have the opportunity to meet him or join one of those sessions, you should definitely do!

**AI coding isn't magic and it isn't a shortcut. It's a different kind of engineering discipline, with its own workflows, its own failure modes, and its own learning curve. The developers who benefit most are the ones who take it seriously and build repeatable processes on top of AI tooling.**

This Post Was Co-Authored With Claude Code {#h2-8-this-post-was-co-authored-with-claude-code}
---------------------------------------------------------------------------------------------

Jonas also highlighted that you can use AI tools for much more than coding alone. So, again in all honesty, Claude Code wrote the initial version of this blog during my train ride from Brussels back to Kortrijk. The tool read my handwritten reMarkable notes, exported as a PDF. But, of course, with a full review, rewrite, and fine-tuning by yours truly :-).
