---
title: "Foojay Podcast #90: Highlights of the Java Features Between LTS 21 and 25"
date: "2026-02-16T07:32:52+00:00"
lastmod: "2026-07-28T13:24:18+00:00"
description: "Every six months, we get a new version of Java. Java 26 is just around the corner and will be released soon. But most companies stick to LTS (Long-Term…"
authors:
  - "frankdelporte"
  - "jakob-jenkov"
image: "episode-90-java-21-to-25.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Podcast"
related_posts:
  - "foojay-podcast-89"
  - "what-should-i-know-about-garbage-collection-as-a-java-developer"
  - "foojay-podcast-28"
  - "foojay-podcast-78"
frozen: false
# WordPress keeps every slug a post has ever had and 301s the old one; the
# migration only carried the CURRENT slug, so this URL -- still live on
# foojay.io today -- had nothing behind it here.
aliases:
  - "/today/foojay-podcast-90-highlights-of-the-java-features-between-lts-21-and-25/"
---

Every six months, we get a new version of Java. Java 26 is just around the corner and will be released soon. But most companies stick to LTS (Long-Term Support) versions, which are maintained and receive security updates for many more years. Versions 8, 11, 17, 21, and 25 are such LTS versions. Hopefully, most of your systems are already on the latest versions and you are not stuck on 8 or earlier. As a reminder, 8 was released in 2014, so much has changed since then.

If you are doubting moving from 21 to 25, or even from an earlier version to the latest LTS, this podcast is for you! Together with Jakob Jenkov, we discussed the most important changes, and this episode includes a few quotes from interviews recorded at conferences last year.

## YouTube

{{< youtube fKDhVUEVT3g >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Jakob Jenkov
  * <https://www.linkedin.com/in/jakob-jenkov-4a3a8/>
* Jonathan Vila
  * <https://www.linkedin.com/in/jonathanvila/>
* Ryan Svihla
  * <https://www.linkedin.com/in/ryan-svihla-096752182/>
* Mary Grygleski
  * <https://www.linkedin.com/in/mary-grygleski/>
* Anton Arhipov
  * <https://www.linkedin.com/in/antonarhipov/>
* Ronald Dehuysser
  * <https://www.linkedin.com/in/ronalddehuysser/>
* Jonathan Ellis
  * <https://www.linkedin.com/in/jbellis/>

## Content

00:00 Introduction of topic and guest

* [Tutorials by Jakob](https://jenkov.com/tutorials/java/index.html)
* [Podcast #89: Quarkus and Agentic Commerce](https://foojay.io/today/foojay-podcast-89/)

03:30 Bugfixes and performance improvements "under the hoods"

* *Quote Jonathan Vila*

08:00 Java as a scripting language

* *Quote Ryan Svihla*
* Compact Source Files and Instance Main methods
* Launch Multi-File Source-Code Programs
* <https://www.jbang.dev/>
* *Quote Mary Grygleski*

15:03 GC Improvements

* Generational Shenandoah
* [Trash Talk - Exploring the JVM memory management by Gerrit Grunwald](https://www.youtube.com/watch?v=Jh79ojcror0)
* [What Should I Know About Garbage Collection as a Java Developer?](https://foojay.io/today/what-should-i-know-about-garbage-collection-as-a-java-developer/)

19:44 Project Loom: Virtual Threads and Structured Concurrency

* *Quote Anton Arhipov*

29:44 How Java evolves

* 6-months release cycle
* How incubator and preview features are used to get feedback from the **community**
* Long-Term Support Short-Term Support versions
* [Foojay Podcast #28: Java 21 Has Arrived!](https://foojay.io/today/foojay-podcast-28/)
* [Foojay Podcast #45: Welcome to Java 22](https://foojay.io/today/foojay-podcast-45/)
* [Foojay Podcast #57: Welcome to OpenJDK (Java) 23](https://foojay.io/today/foojay-podcast-57/)
* [Foojay Podcast #68: Welcome to OpenJDK (Java) 24](https://foojay.io/today/foojay-podcast-68/)
* [Foojay Podcast #78: Welcome to OpenJDK 25!](https://foojay.io/today/foojay-podcast-78/)

32:15 Project Leyden: Ahead-of-time features

* Ahead-of-Time Command-Line Ergonomics
* Ahead-of-Time Method Profiling
* Ahead-of-Time Class Loading \& Linking

39:15 Project Babylon

* Java on CPU, GPU, FPGA?
* This is already possible with TornadoVM
* [Foojay Podcast #82: OpenJDK Projects (Leyden, Babylon, Panama) and TornadoVM](https://foojay.io/today/foojay-podcast-82/)

43:25 Class-File API

* *Quote Ronald Dehuysser*
* [JavaFX In Action #22 with Matt Coley, diving into byte code and JARs with Recaf and JavaFX libraries](https://webtechie.be/post/2025-10-30-jfxinaction-matt-coley-recaf-bentofx-treemapfx-glcanvasfx/)

49:20 Foreign Function and Memory API

* [The FFM API: How OpenJDK Changed the Game for Native Interactions (And Made Pi4J Better!)](https://foojay.io/today/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/)
* [jChampions Conference talk 'Foreign Function \& Memory (FFM) API on Raspberry Pi'](https://webtechie.be/post/2026-01-27-jchampions-talk-ffmapi-on-raspberrypi/)

54:26 Vector API

* *Quote Jonathan Ellis + Ryan Svihla*

59:59 Removal of String templates

01:00:26 Taking a look into the JVM of the future

01:03:08 Conclusion
