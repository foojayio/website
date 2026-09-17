---
title: "Foojay Podcast #101: Java 27 in Practice: Smaller Heaps, Smarter Defaults, and Valhalla on the Horizon"
date: "2026-09-14T06:11:42+00:00"
lastmod: "2026-09-14T06:11:43+00:00"
description: "JDK 27 arrives on 15 September 2026, and it is a feature release rather than a Long-Term Support one. That distinction matters less than it sounds: as…"
authors:
  - "frankdelporte"
  - "simonritter"
image: "episode-101-java-27.jpg"
categories:
  - "Java"
  - "Java Core"
  - "OpenJDK"
  - "Podcast"
related_posts:
  - "foojay-podcast-100"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
  - "azul-august-2026-release-javas-first-monthly-cspu"
  - "what-should-i-know-about-garbage-collection-as-a-java-developer"
frozen: false
---

JDK 27 arrives on 15 September 2026, and it is a feature release rather than a Long-Term Support one. That distinction matters less than it sounds: as Simon Ritter points out, all OpenJDK releases are delivered the same way, and "long-term support" is a property of the binary distributions you install, not of the platform itself. What JDK 27 does have is two changes that switch on by default and cost you nothing but the upgrade. Compact object headers shrink the per-object header from 64 bits to 32, which the JEP measures at a 22% reduction in heap and 8% in CPU on SPECjbb2015 — and because it is internal to the JVM, a JAR you built years ago benefits without a single line changing. Alongside it, G1 becomes the default garbage collector everywhere, including the small, single-core machines that used to fall back to the serial collector.

Simon Ritter is Deputy CTO at Azul and a Java Champion, and he walks through all nine JEPs in the release. Beyond the two performance defaults, there is a security cluster worth knowing about: post-quantum hybrid key exchange for TLS 1.3, which is a defence against attackers harvesting encrypted traffic now to decrypt it later; JFR in-process data redaction, so flight recordings can be shared without leaking access tokens from environment variables or passwords from system properties; and a third preview of PEM encodings, where the primary class went back from a record to an ordinary class — a small design story Simon unpacks nicely. The previews inch along too: lazy constants (renamed from stable values), primitive types in patterns in its fifth preview, structured concurrency in its seventh, and the Vector API in a twelfth incubator that is essentially finished and waiting on Valhalla.

We also cover the shift from quarterly to monthly security updates that began in August 2026, and what that means for anyone maintaining a release pipeline. Then we look ahead to Java 28 in March 2027, where after roughly twelve years Project Valhalla finally lands its first preview: Value Objects, together with Strict Field Initialization. That is around 179,000 lines across 1,800 files, a new `value` keyword, and a change to how you think about writing classes. Simon is candid about what it will not be — don't expect it final in JDK 29 — and equally candid about the Simple JSON API landing in 28, which he suspects may follow string templates rather than survive its previews.

## YouTube

{{< youtube k3aA0AtY4U8 >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Simon Ritter - Deputy CTO at Azul, Java Champion
  * [LinkedIn](https://www.linkedin.com/in/siritter/)
  * [Foojay Profile](https://foojay.io/today/author/simonritter/)

## Links

* The release
  * [JDK 27](https://openjdk.org/projects/jdk/27/)
* Security updates between releases
  * [New Between-Quarters Security Updates for Java: What CSPUs Mean for Your Release Pipeline](https://foojay.io/today/new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline/)
  * [Azul August 2026 Release: Java's First Monthly CSPU](https://foojay.io/today/azul-august-2026-release-javas-first-monthly-cspu/)
* Performance defaults in JDK 27
  * [JEP 534: Compact Object Headers by Default](https://openjdk.org/jeps/534)
  * [JEP 523: Make G1 the Default Garbage Collector in All Environments](https://openjdk.org/jeps/523)
  * [What to Know About Garbage Collection as a Java Developer!](https://foojay.io/today/what-should-i-know-about-garbage-collection-as-a-java-developer/)
* Security and observability in JDK 27
  * [JEP 527: Post-Quantum Hybrid Key Exchange for TLS 1.3](https://openjdk.org/jeps/527)
  * [JEP 538: PEM Encodings of Cryptographic Objects (Third Preview)](https://openjdk.org/jeps/538)
  * [JEP 536: JFR In-Process Data Redaction](https://openjdk.org/jeps/536)
* Previews and incubators in JDK 27
  * [JEP 531: Lazy Constants (Third Preview)](https://openjdk.org/jeps/531)
  * [Project Amber](https://openjdk.org/projects/amber/)
  * [JEP 532: Primitive Types in Patterns, instanceof, and switch (Fifth Preview)](https://openjdk.org/jeps/532)
  * [OpenJDK: Loom](https://openjdk.org/projects/loom/)
  * [JEP 533: Structured Concurrency (Seventh Preview)](https://openjdk.org/jeps/533)
  * [OpenJDK: Panama](https://openjdk.org/projects/panama/)
  * [JEP 537: Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537)
* Looking ahead to JDK 28
  * [Project Valhalla](https://openjdk.org/projects/valhalla/)
  * [JEP 401: Value Objects (Preview)](https://openjdk.org/jeps/401)
  * [JEP 539: Strict Field Initialization in the JVM (Preview)](https://openjdk.org/jeps/539)
  * [JEP 541: Deprecate the macOS/x64 Port for Removal](https://openjdk.org/jeps/541)
  * [JEP 540: Simple JSON API (Incubator)](https://openjdk.org/jeps/540)

## Content

* 00:00 Introduction of the topic and guest
* 01:27 How long Simon has been doing Java
* 01:54 Why release 27 is important, even when not being a Long Term Support release
* 04:55 Quarterly and monthly security updates between new version releases
* 08:07 Which JVM versions are most used in companies
* 09:23 [JEP 534](https://openjdk.org/jeps/534): Compact Object Headers by Default
* 14:51 [JEP 523](https://openjdk.org/jeps/523): Make G1 the Default Garbage Collector in All Environments
* 18:59 [JEP 527](https://openjdk.org/jeps/527): Post-Quantum Hybrid Key Exchange for TLS 1.3
* 22:55 [JEP 538](https://openjdk.org/jeps/538): PEM Encodings of Cryptographic Objects (Third Preview)
* 25:35 [JEP 536](https://openjdk.org/jeps/536): JFR In-Process Data Redaction
* 28:02 [JEP 531](https://openjdk.org/jeps/531): Lazy Constants (Third Preview)
* 29:55 [JEP 532](https://openjdk.org/jeps/532): Primitive Types in Patterns, instanceof, and switch (Fifth Preview)
* 33:13 [JEP 533](https://openjdk.org/jeps/533): Structured Concurrency (Seventh Preview)
* 38:21 [JEP 537](https://openjdk.org/jeps/537): Vector API (Twelfth Incubator)
* 40:28 Looking forward to Java 28 and the first improvements from Project Valhalla
* 42:31 Changes delivered by [JEP 401](https://openjdk.org/jeps/401): Value Objects and [JEP 539](https://openjdk.org/jeps/539): Strict Field Initialization
* 47:36 Can we expect this to be finalized in Java 29?
* 48:26 LTS releases every 1, 2, or 3 years?
* 49:55 [JEP 541](https://openjdk.org/jeps/541): Deprecate macOS/x64
* 51:53 [JEP 540](https://openjdk.org/jeps/540): Simple JSON API (Incubator)
* 55:30 Conclusion, what to remember from this release
