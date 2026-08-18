---
title: "\"Agentic\" Was Everywhere at Money20/20 Amsterdam"
slug: "agentic-was-everywhere-at-money20-20-amsterdam-once-i-started-looking"
date: "2026-06-05T08:55:48+00:00"
lastmod: "2026-06-05T08:55:50+00:00"
description: "Can an agent initiate a payment, decline a loan, or file a compliance report in a way that's deterministic enough to defend?"
authors:
  - "geertjan-wielenga"
image: "1780385104999.jpeg"
categories:
  - "AI"
  - "Machine Learning"
tags:
related_posts:
frozen: false
---

**I walked the floor at [Money20/20](https://www.money2020.com/) in Amsterdam with a simple little mission: count the first ten vendor booths using "AI," "agent," or "agentic" in their pitch.**

My first impression was that this would be hard. Hundreds of organizations, and the buzzwords felt oddly absent --- payments, core banking, compliance, the usual fintech furniture, but not the wall-to-wall "agentic everything" I'd braced for. I jotted down my first ten and thought: ***huh, AI is barely here**.*

Then I kept walking. And it turns out I'd just been looking past it. The "agentic" framing wasn't shouting from every banner, it had quietly landed within the messaging of nearly everyone. Once I got going and walked through all the many halls and areas, the ten gradually became twenty-plus.

Here's what I found, grouped by what these organizations are actually trying to do, which, for those who build the systems underneath all this, is the more interesting question than the word count itself.

Core banking, reimagined as "AI-native"
---------------------------------------

The platforms that run the actual ledgers are rebranding speed and adaptability as AI:

* [CoreFi](https://corefi.co/) --- "Agentic AI Cloud Core Banking"
* [Mambu](https://www.mambu.com/) --- "Banking at the speed of AI"
* [SaaScada](https://saascada.com/) --- "AIdaptive Core Banking"

Payments and money movement
---------------------------

Unsurprisingly for Money20/20, payments showed up --- now with agents that initiate and settle:

* [BridgerPay](https://www.bridgerpay.com/) --- "AI powered payment orchestration"
* [SolvaPay](https://solvapay.com/) --- "Agents need to pay. Agentic payments"
* [Stripe](https://stripe.com/) --- "Moving money at agentic speed"

A recurring idea here --- *agents that pay* --- is interesting. It implies AI agents *initiating* transactions, which is a new design problem for anyone building payment rails.

Lending, credit, and collections
--------------------------------

The decisioning side of finance in the context of agents:

* [Experian](https://www.experian.com/) --- "Your high trust partner in the agentic lending era"
* [Purple Fabric](https://purplefabric.ai/) --- "AI-First Lending"
* [Taktile](https://taktile.com/) --- "Powering critical decisions with AI agents"
* [Acclaim AI](https://www.acclaim.ai/) --- "AI Agents for 10x More Efficient Collections"

Risk, fraud, and compliance
---------------------------

There's a lot going on here: the part of fintech where "auditable" and "agentic" have to coexist:

* [Oscilar](https://oscilar.com/) --- "The Agentic Risk Platform"
* [Graphdo SIA](https://graphdo.net/) --- "AI-powered toolkit for AML"
* [Kalipso](https://kalipso.ai/) --- "Regulatory compliance, powered by AI"
* [JupiterOne](https://www.jupiterone.com/) --- "AI Risk Management Platform"
* [DataWhisper](https://datawhisper.co.uk/) --- "Agentic AI for Regulated Industries"
* [Trustpilot](https://www.trustpilot.com/) --- "The future of commerce runs on AI --- and trust"

Build-your-own-agent platforms and tooling
------------------------------------------

Another relevant area --- the tools you'd actually use to create the AI agents themselves:

* [Sierra](https://sierra.ai/) --- "There's an AI agent for that. It's built on Sierra."
* [orq.ai](https://orq.ai/) --- "Build \& operate quality AI products"
* [Camunda](https://camunda.com/) --- "Build AI agents trusted for high-stakes work."
* [SoundHound AI](https://www.soundhound.com/) --- "AI Agents Built for Trust, Reliability and Security First"

Data and infrastructure underneath it all
-----------------------------------------

Agents are only as good as what they read and how fast:

* [ClickHouse](https://clickhouse.com/) --- "The Leading Database for AI"
* [acaisoft](https://www.acaisoft.com/) --- "Sovereign AI for European finance"

Content, language, and trust
----------------------------

The parts that make agent output usable, legible, and credible:

* [AD VERBUM](https://www.adverbum.com/) --- "AI Translations. Auditable. Every Word."
* [CKEditor](https://ckeditor.com/) --- "Bring AI where content happens"

Services and talent
-------------------

And the people-and-delivery layer:

* [HCLTech](https://www.hcltech.com/) --- "Move fast. Govern smart. Deliver with AI."
* [Karat](https://karat.com/) --- "Unlock Talent for the Human + AI Era"

Conclusions
-----------

My takeaway changed along the way between the start and the end of my walk through Money 20/20. Plus, of course, a lot of technologies use words such as "intelligent" or "intelligence", rather than explicitly calling out AI, though I was most interested in those explicitly calling out AI in their key slogan or biggest message at their booth.

I went in expecting "agentic" to be plastered everywhere and almost concluded the opposite, that it was barely present, before realizing it may also be becoming the *substrate* rather than the *slogan* itself. The word is no longer the differentiator, instead, it may of course be on the way to being the assumed baseline.

What stands out across these clusters, though, is how little of it is consumer-facing chat. Instead, it's risk engines, lending decisions, payment orchestration, AML, core banking. In other words, the high-stakes, heavily-regulated, audit-everything backend work, which is exactly the territory where the JVM has been living for decades.

The interesting engineering questions aren't anymore "can an LLM ***answer*** this," but "can an agent ***initiate*** a payment, decline a loan, or file a compliance report in a way that's deterministic enough to defend."

That's the version of "agentic" worth paying attention to and it's coming straight for the systems many of us are building.
