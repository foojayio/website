---
title: "Foojay Podcast #99: Testing the Untestable: LLM Security for Java Developers with Tiberius"
slug: "foojay-podcast-99"
date: "2026-06-22T06:54:00+00:00"
lastmod: "2026-06-22T08:46:05+00:00"
description: "Your Java AI app is live. But have you tested whether it can be jailbroken? Iryna Dohndorf introduces Tiberius, an open-source security testing library for LLM applications in Java, and explains how to deal with the biggest challenge in AI testing: non-determinism by design."
authors:
  - "frankdelporte"
  - "iryna-dohndorf"
image: "https://foojay.io/wp-content/uploads/2026/06/episode-99-tiberius.png"
categories:
  - "AI"
  - "Podcast"
  - "Spring"
  - "Testing"
  - "Tools"
tags:
related_posts:
frozen: false
---

Your AI-powered Java application is live in production. But have you actually tested whether it can be jailbroken or manipulated into leaking data it should never reveal? In this episode, Iryna Dohndorf walks us through Tiberius, an open-source security testing library for LLM applications in Java, and explains why everything you know about unit testing needs a rethink when non-determinism is part of the design.

YouTube {#h2-0-youtube}
-----------------------

{{< youtube 7bBcTzeevEo >}}

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Guests {#h2-2-guests}
---------------------

* Iryna Dohndorf - Software Engineer at Karakun Group, active member of the Basel One and Devoxx UK program committees, and creator of Tiberius
  * [LinkedIn](https://www.linkedin.com/in/iryna-dohndorf)

Links {#h2-3-links}
-------------------

* [Tiberius article on Foojay](https://foojay.io/today/tiberius-a-security-testing-framework-for-llm-applications-in-java/)
* [Tiberius on GitHub](https://github.com/tiberius-security/tiberius)
* [Maven Central: io.github.tiberius-security:tiberius:1.0.0](https://central.sonatype.com/artifact/io.github.tiberius-security/tiberius)
* [Security Testing Guide](https://github.com/tiberius-security/tiberius/blob/main/docs/SECURITY_TESTING_GUIDE.md)

Content {#h2-4-content}
-----------------------

* 00:00 Introduction of topic and guest
* 01:05 The problem Tiberius wants to solve
* 06:39 How "traditional" unit tests don't work for LLM integrations
* 10:23 Scan-Fixture-Validate principle and sharing artifacts
* 15:15 Using different skills, for example, the grandmother skill
* 17:33 Testing for required versus forbidden bias
* 19:35 The probes across nine attack categories used by Tiberius
* 20:44 Buff mutation testing
* 26:55 Using Tiberius in your pipelines and when to fail
* 29:35 Using multi-trial scans
* 31:14 Fingerprinting: which model you use, should not be detectable
* 32:55 Combining multiple models, model as a judge
* 34:41 Sharing JSON models to improve tests
* 36:05 How to get started with Tiberius in Spring and with LangChain4j
* 36:41 Quarkus not supported yet, plans for the future
* 39:07 Conclusions and a call out to everyone to become a Foojay author
