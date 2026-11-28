---
title: "JCast Roundup: AI Agents, Clean Code, Cloud and Quarkus (Summer 2026)"
date: "2026-10-24"
description: "Six new JCast episodes from June to October 2026: AI hype and agents, clean code, cloud costs, Quarkus with Kevin Dubois, and meeting-heavy days."
authors:
  - "oumaima-zerouali"
image: "jcast-roundup-summer-2026.jpg"
categories:
  - "Podcast"
  - "AI"
  - "Cloud"
  - "Interviews"
related_posts:
  - "introducing-jcast-conversations-about-java-and-developer-life-in-dutch"
  - "foojay-podcast-100"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
  - "spring-quarkus-jooby-vertx-java-framework-comparison"
---

Back in May, we [introduced JCast on Foojay](/today/introducing-jcast-conversations-about-java-and-developer-life-in-dutch/): a Dutch-language podcast about IT, dev life and soft skills, hosted by three Belgian developers, Oumaima Zerouali, Viktor Van Steenweghen and Maarten Casteels. Season 2 kicked off with Frank Delporte, and since then we have kept recording.

From now on, we'll share a short overview here every three months of what we published in that period. This first roundup covers June to mid-October 2026: six episodes, two guests from the Belgian tech scene, one Red Hat special, and a lot of "gezonde meningsverschillen" (healthy disagreements).

The episodes are in Dutch, but the topics are universal. So here's what you missed, in English.

## S02E02: From AI Agent to a Farm in France

*Published June 1, 2026 · hosts only*

No guest this time, because we had more than enough to talk about ourselves. The starting question: which tools actually make a lasting difference, and which AI hypes disappear as fast as they came?

FOMO turns out to be very personal. Oumaima gets it as soon as she sees something truly impressive, Viktor can switch it off but wants to understand how things work, and Maarten is happy as long as the result looks good. We also look at what we automate ourselves, including our own podcast workflow: thumbnails, transcripts, show notes and blog posts are largely handled by AI, while we experiment with local models to keep token costs under control.

Then the pitfalls: an AI budget that went from a hundred to tens of thousands of euros a month, and an agent that got 100 euros to day-trade crypto and lost it overnight. That leads to guardrails, prompt injection, and the reminder that sometimes a good old deterministic script is still the smartest choice.

*Listen: [S02E02 on jcast.dev](https://jcast.dev/episodes/s02e02-van-ai-agent-tot-boerderij-in-frankrijk/)*

## S02E03: Who's Actually in Charge, You or Your Agent?

*Published July 6, 2026 · with Jeroen Bastijns*

Software architect [Jeroen Bastijns](https://www.linkedin.com/in/jeroen-bastijns-bb265538/) (Sopra Steria) returns for his second JCast appearance to talk about AI agents in everyday development work.

Jeroen explains the "onion" model of an agent, inspired by Birgitta Böckeler's writing at Thoughtworks: the language model at the core, tooling like GitHub Copilot or Cursor around it, and on the outside a **harness** you build yourself. That harness has two parts: *guidelines* that steer the agent, and *sensors* that give feedback. Some sensors are deterministic (the compiler, linting, unit tests), others are not (checks on domain concepts or security rules).

We also talk about token costs, why context matters more than picking the "best" model, and the lack of observability in agent workflows. The recurring question: which skills stay human? Critical thinking, questioning requirements, and, according to Jeroen, reading well.

*Listen: [S02E03 on jcast.dev](https://jcast.dev/episodes/s02e03-wie-is-er-eigenlijk-in-charge-jij-of-je-agent/)*

## S02E04: It Works… But Is It Good?

*Published August 3, 2026 · hosts only*

What is clean code, and where does tidiness turn into perfectionism? We share our own legacy war stories: a class with ten different author names, all of them long gone; a first project without any structure; and a consulting gig where everything was already on fire and had to be fixed in two days.

A big takeaway: code quality agreements are often missing, not because nobody cares, but because everyone assumes they're obvious. Linters, formatters and pull request conventions exist but are rarely enforced. Set up the tooling from day one, and half the review discussions about spacing and braces disappear.

We also discuss pair programming as built-in code review, the broken windows theory, and end with a quickfire round on comments versus self-explanatory code, naming, and pull request size.

*Listen: [S02E04 on jcast.dev](https://jcast.dev/episodes/s02e04-het-werkt-maar-is-het-goed/)*

## S02E05: AWS Is Lego, Azure Is Playmobil

*Published September 7, 2026 · with Pieter Vincken*

[Pieter Vincken](https://www.linkedin.com/in/pieter-vincken/), Public Cloud Solution Architect at Cegeka, gives an honest look at what "the cloud" really is: hardware plus a layer of automation and managed services.

Topics include infrastructure as code with Terraform and OpenTofu (and why fully provider-agnostic abstractions often cause more problems than they solve), hidden costs such as logging and data transfer, and why cloud is neither inherently insecure nor automatically safer. Developers now carry responsibilities that used to be split over network, firewall and VM teams.

Pieter's analogy gave the episode its title: AWS is Lego, flexible building blocks that fit together; Azure is Playmobil, pieces that don't always fit across sets. And yes, there's a war story about a script that wiped all internal Nexus repositories overnight. Lesson: test your backups.

*Listen: [S02E05 on jcast.dev](https://jcast.dev/episodes/s02e05-aws-is-lego-azure-is-playmobil/)*

## Special: Quarkus, AI and Why Java Is Far From Done

*Published September 21, 2026 · with Kevin Dubois*

For our special episode, we talked with [Kevin Dubois](https://www.linkedin.com/in/kevindubois/), principal software engineer at IBM (Red Hat) and closely involved with Quarkus and LangChain4j.

Kevin's path went from studying in Rome to ColdFusion and PHP in the US, and back to Java. Frustration with environments that couldn't run locally led him to Docker, cloud native development and eventually Red Hat.

Of course we discussed Quarkus versus Spring. Kevin admits a slight bias, but gives Spring credit for its ecosystem; Maarten sticks with Spring because he knows it and gets his work done. The point: make a deliberate choice.

On AI, Kevin sees a real opportunity for Java, because enterprise software needs stability, security and maintainability. He walks us through demos built with LangChain4j and Quarkus, including a car rental app that analyzes photos to decide on maintenance, and an AIOps plugin for Argo Rollouts that uses an LLM to analyze logs and metrics and trigger rollbacks.

*Listen: [the Quarkus special on jcast.dev](https://jcast.dev/episodes/s02e20-quarkus-ai-en-waarom-java-nog-lang-niet-klaar-is/)*

## S02E06: Meetings, Context Switches and an Empty Tank

*Published October 5, 2026 · hosts only*

We plan as if we have eight productive hours. But what does a developer's day really look like? Stand-ups, syncs, ad-hoc calls, and suddenly the day is over without clear output.

Viktor brings Eurostat numbers on how many hours Europeans actually work (Belgium: 34.3 hours a week on average). We discuss what counts as work, the hidden cost of context switching between projects, why a failed deployment feels dramatic but isn't life or death, and how a day full of interactions can drain you even when everything went well technically.

The quickfire round ends with a classic debate: camera on or off in meetings?

*Listen: [S02E06 on jcast.dev](https://jcast.dev/episodes/s02e06-meetings-context-switches-en-een-vat-dat-af-is/)*

## Where to Listen

All episodes are available on [jcast.dev](https://jcast.dev) and on every major podcast platform:

* [Spotify](https://open.spotify.com/show/3wfdG6IjCoJjfEx33MsM2W)
* [Apple Podcasts](https://podcasts.apple.com/podcast/jcast/id1814550001)
* [Pocket Casts](https://pca.st/itunes/1814550001)
* [Deezer](https://www.deezer.com/show/1001888441)

You can also follow us on [Instagram](https://www.instagram.com/jcast_crew/).

## See You Next Quarter

Thanks to Jeroen, Pieter and Kevin for joining us, and to everyone who listens during their commute, while coding or over lunch. Got a topic or a guest suggestion for the next season? Reach out via [jcast.dev](https://jcast.dev).

We'll be back on Foojay in three months with the next roundup.
