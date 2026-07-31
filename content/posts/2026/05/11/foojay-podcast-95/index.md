---
title: "Foojay Podcast #95: Is Your Java App Actually Secure, Or Does It Just Look That Way?"
slug: "foojay-podcast-95"
date: "2026-05-11T09:57:00+00:00"
lastmod: "2026-05-11T15:00:10+00:00"
description: "Is your Java application actually secure, or does it just look that way? In this episode of the Foojay Podcast, Frank is joined by Steve Poole and David - by Frank Delporte"
authors:
  - "frankdelporte"
  - "steve-poole"
image: "episode-95-security-eol-cve.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Podcast"
  - "Security"
tags:
related_posts:
  - "foojay-podcast-94"
  - "foojay-podcast-93"
  - "foojay-podcast-92"
  - "foojay-podcast-91"
frozen: false
---

Is your Java application actually secure, or does it just look that way? In this episode of the Foojay Podcast, Frank is joined by Steve Poole and David Welch, both from [HeroDevs](https://www.herodevs.com/), to dig deep into the state of Java security in 2025 and beyond.

Steve introduces the concept of zombie dependencies: end-of-life libraries that appear safely dormant but are quietly accumulating vulnerabilities waiting to bite you. David, a co-chair of the CVE Automation Working Group, explains what a CVE actually is, how the identification and disclosure process works in practice, and why AI tools like Mythos are dramatically accelerating the pace at which new vulnerabilities are found --- on both sides of the wall.

Together they cover how CVEs in the Java runtime are handled through coordinated disclosure, why Maven Central is safer than most ecosystems but not a silver bullet, and what insurance companies are starting to demand from organizations that haven't cleaned up their dependency trees. They also discuss practical steps any Java developer can take today, from generating an SBOM and running [Snyk](https://snyk.io/) or [Trivy](https://trivy.dev/), to adopting [OpenRewrite](https://docs.openrewrite.org/) and [Renovate](https://docs.renovatebot.com/) in your pipelines, and why vibe coding with AI tools may be quietly making your security posture worse if you are not reviewing the dependency choices being made for you.

An animated, occasionally alarming, and ultimately optimistic conversation about a problem the Java community is well-positioned to lead on.

YouTube {#h2-0-youtube}
-----------------------

{{< youtube -T5h4HqRpVw >}}

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Guests {#h2-2-guests}
---------------------

### Steve Poole {#h3-3-steve-poole}

* [LinkedIn](https://www.linkedin.com/in/noregressions/)
* [Foojay Author profile](https://foojay.io/today/author/steve-poole/)
* [Crossing the River Styx: Spring Boot 3.5 and the Zombie Dependency Problem](https://foojay.io/today/crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem/)
* [Why Java Developers Over-Trust AI Suggestions](https://foojay.io/today/why-java-developers-over-trust-ai-dependency-suggestions/)

### David Welch {#h3-4-david-welch}

* [LinkedIn](https://www.linkedin.com/in/dwelch2344/)

Content {#h2-5-content}
-----------------------

00:00 Introduction of topics and guests  

04:00 What are Zombie dependencies?  

05:36 What are CVEs?  

11:39 How Mythos and other AI tools are influencing the CVE reporting process  

16:53 How CVEs in the Java runtime are handled  

21:30 How the industry is looking at the increased security threats  

30:17 Developers need to make better decisions "the first time" and use the right tools  

31:42 Keep your OS, JVM, and dependencies up-to-date! Insurance companies will force you...  

44:48 How "safe" is Maven Central compared to other repository systems  

50:48 What you can do as a Java developer to make your apps safer  

59:01 Should we be scared for the following years and be careful with vibe coding?  

01:04:27 Conclusion
