---
title: "Foojay Podcast #104: 53 CVEs, Two Emergency Releases: Why Java Security Holds Up with Simon Ritter"
date: "2026-11-02"
description: "A record 53 CVEs and two out-of-cycle releases in one week. Simon Ritter explains what happened, why Java security holds up, and how to stay on top of patching."
authors:
  - "frankdelporte"
  - "simonritter"
image: "episode-104-security-updates.jpg"
categories:
  - "Java"
  - "OpenJDK"
  - "Podcast"
  - "Security"
related_posts:
  - "free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble"
  - "azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release"
  - "foojay-podcast-101"
  - "owasp-java-security-libraries-please-update"
frozen: false
---

53 CVEs in one quarterly update, followed by two emergency releases within days. That is a lot of issues in a short time, but it is also a sign that the system works: AI tools are now analyzing Java, the problems get found and fixed first, and a whole community of engineers and commercial distributors is behind every patch.

In this episode, Simon Ritter, Deputy CTO at Azul, Java Champion and member of the OpenJDK Vulnerability Group, explains what happened over the past weeks. He is normally invited twice a year for a new Java release, but this time the security news could not wait.

We start with what a CVE is and how a vulnerability travels from a report to a patch, in a closed and encrypted group, before anyone can exploit it. From there we cover why the number of fixes jumped from an average of 7.6 per quarter to 53, how the release cycle is moving toward monthly security updates, the eMudhra certificate problem that caused two out-of-cycle releases, and the backporting work behind every supported Java version. We close with free versus paid OpenJDK builds, the CPU, PSU and CSPU abbreviations, and the one thing to remember: check your Java version and be ready to update at any moment.

If you want to understand how CVEs are handled behind the scenes and what the different types of security updates actually mean, this episode explains it.

## YouTube

{{< youtube Ss3mZMgNaZE >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Simon Ritter - Deputy CTO at Azul and Java Champion
  * [LinkedIn](https://www.linkedin.com/in/siritter/)
  * [Foojay Author Page](https://foojay.io/today/author/simonritter/)

## Links

* [Free Java, Real Risk: Why Unsupported OpenJDK Is Now a Security Gamble](/today/free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble/)
* [Azul Zulu October 2026 Quarterly Update and Out-of-Cycle Release](/today/azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release/)

## Content

* 00:00 Introduction of topic and guest
* 01:59 What is a CVE and how are they reported and handled?
* 08:45 How the Java security release cycle is changing and each have more CVEs
* 11:48 How AI impacts the discovery of CVEs
* 14:09 What was the eMudhra problem causing two out-of-cycle security updates?
* 21:49 The impact of creating an out-of-cycle update (and backports)
* 26:05 How safe will OpenJDK builds be in the future? And dependencies?
* 29:06 Free versus paid versions of OpenJDK builds
* 32:40 CPU vs PSU vs CSPU: the names used for different types of security updates (security only versus including patches)
* 35:52 What will happen with old Java versions like 6, 7, 8
* 37:05 Why would you pay for a supported OpenJDK build?
* 41:50 Conclusion: check your Java version and be ready to update at any moment!
