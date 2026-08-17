---
title: "Jakarta Agentic AI Hits Its First Milestone"
date: "2026-08-05T10:43:12+00:00"
lastmod: "2026-08-07T08:11:13+00:00"
description: "Jakarta Agentic AI hits its first milestone - 1.0.0-M1 is live on Maven Central, with a draft spec defining annotation-driven AI agents for Jakarta EE."
authors:
  - "dominika-tasarz"
image: "Screenshot-2026-08-05-at-11.35.28.png"
categories:
  - "AI"
  - "Jakarta EE"
tags:
related_posts:
  - "shaping-jakarta-agentic-ai-together-watch-the-open-conversation"
enlighterjs: true
frozen: false
---

[Jakarta Agentic AI](https://jakarta.ee/specifications/agentic-ai/1.0/)just shipped its first deliverable: [version 1.0.0-M1 is live on Maven Central](https://central.sonatype.com/artifact/jakarta.agentic-ai/jakarta.agentic-ai-api/1.0.0-M1) and [the draft 1.0 specification is out for review](https://github.com/jakartaee/agentic-ai/releases/download/1.0.0-M1/jakarta-agentic-ai-1.0.0-M1.pdf). The project now has a specific API surface that developers can look at, try out and give feedback on.

Quick recap - what is Jakarta Agentic AI? {#h2-0-quick-recap-what-is-jakarta-agentic-ai}
----------------------------------------------------------------------------------------

If you missed [the original announcement,](https://www.azul.com/blog/announcing-the-jakarta-agentic-ai-project/) Jakarta Agentic AI is a new Eclipse Foundation project bringing vendor-neutral, standardized APIs for building AI agents to Jakarta EE runtimes. The goal is to do for agentic AI what Jakarta Servlet, Jakarta RESTful Web Services and Jakarta Batch did for their respective domains: give Java and Jakarta EE developers a consistent, portable programming model instead of forcing them to hand-roll agent orchestration or lock into a single vendor's framework.

What's in our First Milestone {#h2-1-what-s-in-our-first-milestone}
-------------------------------------------------------------------

The draft specification - [see here -](https://github.com/jakartaee/agentic-ai/releases/download/1.0.0-M1/jakarta-agentic-ai-1.0.0-M1.pdf) lays out the core programming model, and `jakarta.agentic-ai-api:1.0.0-M1` is now available on Maven Central so implementers and early adopters can start working against real interfaces:

```xml
<dependency>
    <groupId>jakarta.agentic-ai</groupId>
    <artifactId>jakarta.agentic-ai-api</artifactId>
    <version>1.0.0-M1</version>
</dependency>
```


A few of the key ideas the spec defines:

* **Annotation-driven agents.** An agent is just a CDI bean annotated with `@Agent`. Its workflow is built from familiar lifecycle annotations - `@Trigger` (the entry point), `@Decision` (branch or stop the workflow), `@Action` (do the work), `@Outcome` (finalize the result), and `@HandleException` (recover from errors). Decisions and actions can be freely intermixed, so agents can be as simple as "trigger + action" or as elaborate as multi-step conditional workflows.  
* **Full CDI integration.** Agents get dependency injection, interceptors, CDI events and lifecycle callbacks for free. There's also a new `@WorkflowScoped` context that ties an agent instance's lifecycle to a single workflow execution, alongside the option to use `@ApplicationScoped` for longer-lived, shared agents.  
* **A lightweight LLM facade.** Rather than trying to standardize the fast-moving world of LLM provider APIs, the spec introduces a minimal `LargeLanguageModel` interface for sending prompts and getting back typed or raw responses (backed by Jakarta JSON Binding), plus an `unwrap()` escape hatch for vendor-specific features - the same pattern Jakarta Persistence uses for `EntityManager`.  
* **Jakarta EE alignment.** Agents are designed to sit naturally alongside the rest of the platform - Persistence, Validation, Transactions, Concurrency, NoSQL, Data, and RESTful Web Services --- so agent logic doesn't have to live in a silo.

The spec's examples chapter walks through realistic patterns: fraud detection, automated documentation generation from pull requests, a customer support agent and error-recovery workflows with parameter validation - all built from the same small set of annotations.

Get involved! {#h2-2-get-involved}
----------------------------------

As [Reza Rahman](https://www.linkedin.com/in/javareza/), who leads the project, put it: 'The implementation, TCK and v1 release is close.' M1 is the annotations-and-lifecycle milestone; a reference implementation and TCK (Technology Compatibility Kit) are the next pieces needed before a full 1.0 release. A programmatic workflow API - for dynamic, runtime-modifiable agent workflows - is also flagged as a future direction beyond 1.0.

If you're already building on Jakarta EE and are curious about agentic AI, here's what you can do:

* **Grab the artifact:** [`jakarta.agentic-ai-api:1.0.0-M1` on Maven Central](https://central.sonatype.com/artifact/jakarta.agentic-ai/jakarta.agentic-ai-api/1.0.0-M1)
* **Read the draft spec:** [Jakarta Agentic AI Specification, v1.0 (PDF)](https://github.com/jakartaee/agentic-ai/releases/download/1.0.0-M1/jakarta-agentic-ai-1.0.0-M1.pdf)
* **Follow or contribute to the project:** [Jakarta Agentic AI on the Eclipse Foundation](https://projects.eclipse.org/projects/ee4j.agentic-ai)

We'd love to see you join the Jakarta Agentic AI Community!

<br />
