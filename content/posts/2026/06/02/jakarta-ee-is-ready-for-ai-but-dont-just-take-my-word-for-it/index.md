---
title: "Jakarta EE is Ready for AI – Five Talks from OCX26 That Prove It"
date: "2026-06-02T11:41:01+00:00"
lastmod: "2026-06-02T11:41:04+00:00"
description: "Five talks from OCX26 Brussels 2026 explore how Jakarta EE handles AI integration, local inference, agentic systems, and API design — with no framework gymnastics required."
authors:
  - "dominika-tasarz"
image: "Favicon-3-2.png"
categories:
  - "AI"
  - "Conference"
  - "Jakarta EE"
  - "Microservices"
related_posts:
frozen: false
---

Back in April I had the pleasure of attending Open Community Experience 2026 in Brussels - the Eclipse Foundation's flagship open source conference. It's always good to be in a room (or a few rooms 😉 ) with people who really care about the technology they work with. Several of my colleagues and friends were speaking - watching them present work they've spent serious time on is one of the better parts of this community.

This post is a roundup of five talks I think belong well together. They don't cover the same topic but they tell a story about where enterprise Java is, where it's going and what it means to build serious software with it in 2026.

## **Where Jakarta EE Comes From and Where It's Headed**

### The Past, Present, and Future of Enterprise Java - Ivar Grimstad (Eclipse Foundation)

If you're going to watch one talk from OCX26 to orient yourself before watching the others, make it this one. Ivar traces the full arc from J2EE's famously painful complexity, through the birth of the Spring framework and eventual influence on the platform itself, all the way to where Jakarta EE is today.  

He is really good at explaining why Jakarta EE looks the way it does: every simplification has a history, every specification carries a rationale. He walks through the TCK process, the platform profiles (full, web and core) and the key additions in Jakarta EE 10 and 11 - including Jakarta Data and virtual thread support - before turning to the EE 12 roadmap and the early moves towards AI standardisation.

{{< youtube hKiIPDtRuvg >}}

## **Jakarta EE Meets AI: Three Angles on the Same Problem**

The next three talks are best understood as a series. They each ask a version of the same question - *how do you integrate AI into enterprise Java systems responsibly?* - but approach it from different angles and with a slightly different focus.

### The Intelligent Monolith: Supercharging Jakarta EE with Local AI - Luqman Saeed (Azul)

Luqman opens with a provocation that I think resonates with anyone who's been paying attention to how AI gets adopted in enterprise settings at the moment: what if the biggest risk in your AI strategy isn't the model - it's the dependency?

Most AI integration today is built on external API calls to hosted models. That means your application's intelligence is, well... rented. You're subject to someone else's pricing decisions, rate limits, latency, availability and - critically in regulated industries - data residency constraints. Luqman's talk is a detailed, practical demonstration of what it looks like to bring that intelligence back home.

The stack he demonstrates is very much Java-native: CDI for dependency injection, LangChain4j for AI orchestration, PostgreSQL with pgvector for embeddings and Ollama for running models locally. He builds a full retrieval-augmented generation (RAG) pipeline within the application itself - with your data, your model and your infrastructure.

Luqman walks through four progressive patterns: declarative RAG pipelines, agentic workflows with decision logic, multi-agent orchestration and finally fully in-process inference using Jlama - running the model directly inside the JVM, no external process required. Each step trades a little convenience for more control, and he's honest about the trade-offs at each stage.

{{< youtube DNJUjlIFgJw >}}

### Jakarta EE 11 Meets AI: Building Intelligent Microservices with Virtual Threads and Jakarta Data - Luqman Saeed (Azul)

With Luqman's second talk, we are now moving from monolithic architecture to microservices - and in doing so, we highlight just how much Jakarta EE 11 has to offer for teams building AI-enabled systems.

The central architectural move here is using Jakarta Data repositories as the persistence layer for embeddings. Rather than reaching for a dedicated vector database, Luqman stores embeddings directly in JPA entities as byte arrays and implements cosine similarity search in plain Java. For many real-world use cases - where data volumes are moderate and operational simplicity matters - this is a very practical approach that avoids adding infrastructure complexity before you've validated whether you actually need it.

The talk also makes excellent use of Jakarta Concurrency 3.1's virtual thread support. Embedding generation and model inference are I/O-bound operations and the result is a highly concurrent system without any manual thread pool management. MicroProfile Config handles runtime model switching, so you can move between model providers without redeployment.

Luqman is being honest about when this approach reaches its limits. The in-memory vector search works… until it doesn't. The embedded model works… until your scale demands otherwise. Luqman is clear about what signals should prompt you to reach for a dedicated vector database or GPU-based inference. That kind of honest guidance - knowing not just how to do something but when to stop doing it that way - is what makes this talk relevant and practical!

{{< youtube l2tbVnqw1kc >}}

### Production-ready Agentic AI: Building Enterprise-grade Java Systems with Jakarta EE and MicroProfile - Kenji Kazumura (Fujitsu)

Where Luqman's talks focus on architecture and implementation, Kenji's talk asks the harder question: what does it actually take to put an AI-enabled system into production?

The answer, it turns out, is the same thing it's always taken for distributed systems: security, observability and transactional consistency. Kenji's argument is that Jakarta EE and MicroProfile already give you most of what you need.

The reference architecture he demonstrates is an agent-based system where a supervisor agent coordinates specialised sub-agents that interact with external tools via MCP servers. He uses OpenID Connect and JWT propagation - standard MicroProfile Security capabilities - ensuring that authentication context flows correctly across service boundaries even as agents delegate to agents.

Transaction handling in distributed AI workflows can be tricky - local ACID transactions work where they can, compensation patterns handle the cases where they can't. Observability is implemented via OpenTelemetry, giving you end-to-end tracing across what can otherwise be an extremely opaque chain of agent interactions.

Kenji introduces Jakarta Agentic AI [- a project I wrote about here -](https://www.azul.com/blog/announcing-the-jakarta-agentic-ai-project/) aimed at standardising agent lifecycle and integration patterns across the enterprise Java ecosystem.

This talk will be useful for anyone who has built an AI proof-of-concept and is now wondering how to make it something you'd actually trust in production.

{{< youtube 46t5qxs0A6o >}}

## **Getting the Fundamentals Right**

### API = Some REST and HTTP, right? RIGHT?! - Rustam Mehmandarov (Miles)

Every AI-enabled service in the previous three talks exposes APIs. Every agent that communicates with another service does so over an API. Every system Kenji secures with JWT and OpenID Connect is secured at its API boundary. The sophistication of your AI architecture means very little if the APIs it's built on are fragile, inconsistently versioned and poorly documented.

Rustam is one of my favourite Java talks presenters – his talks are energetic and funny but at the same time full of practical examples and experience-led lessons. This talk follows that approach, being a good reminder of how much often goes wrong with APIs in practice. He covers the gap between REST theory and REST reality, the widespread misuse of HTTP status codes, the underappreciated complexity of versioning strategies, and the operational challenges of deprecation and lifecycle management.

If the previously mentioned AI talks made you excited about what you're going to build, Rustam's talk is a good reminder to build it well.

{{< youtube ukt_kkFdvSc >}}
