---
title: "Foojay Podcast #14: Debugging Tools and Skills for Fun and Profit"
date: "2023-02-20T08:29:01+00:00"
lastmod: "2025-11-13T09:15:05+00:00"
description: "Let's talk about debugging and observability. We work with debugging all the time, but how well do we know this common practice?"
authors:
  - "frankdelporte"
  - "johannes-bechberger"
  - "marit-van-dijk"
  - "shai-almog"
  - "ties-van-de-ven"
image: "podcast-debugging-guests.png"
categories:
  - "Developer Tools"
  - "Java Core"
  - "Podcast"
  - "Testing"
related_posts:
  - "debug-like-a-senior-developer"
  - "a-short-primer-on-java-debugging-internals"
  - "package-checker-find-fix-vulnerabilities-with-intellij-idea-ultimate"
  - "offline-crypto-address-validation-in-java"
frozen: false
---

Let's talk about debugging and observability.

We work with debugging all the time, but how well do we know this common practice?

Observability, monitoring, and debugging at scale for your production...

{{< youtube ozcC3scY0Ig >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* A lot of rubber ducks
* Johannes Bechberger
  * JVM and profiler developer
  * Worked on the JDWP protocol and profiling
  * <https://github.com/parttimenerd>
  * <https://twitter.com/parttimen3rd>
  * <https://mastodon.social/@parttimenerd>
  * [AsyncGetStackTrace: A better Stack Trace API for the JVM](https://mostlynerdless.de/blog/2023/01/19/asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm/)
  * [A short primer on Java debugging internals](https://mostlynerdless.de/blog/2022/12/27/a-short-primer-on-java-debugging-internals/)
* Marit van Dijk
  * Developer Advocate at JetBrains
  * <https://maritvandijk.com/>
  * <https://twitter.com/MaritvanDijk77>
  * <https://mastodon.social/@maritvandijk>
* Ties van de Ven
  * Software Engineer @ JDriven, Coach @ Jcore
  * <https://www.tiesvandeven.nl/>
  * <https://twitter.com/ties_ven>
  * [6 Steps to help you debug your application](https://blog.jdriven.com/2017/10/6-steps-to-help-you-debug-your-application/)

## Host

* Shai Almog
  * Author of "[Practical Debugging at Scale](https://www.amazon.com/dp/1484290410/)"
  * <https://debugagent.com/>
  * <https://mastodon.social/@debugagent>
  * <https://twitter.com/debugagent>

## Producer

* Frank Delporte
  * <https://foojay.social/@frankdelporte>
  * <https://twitter.com/frankdelporte>

## Content

* 00'00 Intro and music
* 00'24 About the topic of this podcast
* 00'58 Introduction of the guests and host
* 05'14 Debugging with IntelliJ IDEA and discoverability of tools
  * YouTube: [Debugger playlist](https://www.youtube.com/watch?v=59RC8gVPlvk&list=PLPZy-hmwOdEUWF85MuwrKV8YVWLmZW4ZA)
  * YouTube: [Profiling tools](https://www.youtube.com/watch?v=OQcyAtukps4)
  * YouTube: [Profiling live stream](https://www.youtube.com/watch?v=TDpbt4thECc)
* 13'27 JDWP protocol
  * [DZone: Remote Debugging Java Applications With JDWP](https://dzone.com/articles/remote-debugging-java-applications-with-jdwp)
  * [Foojay: PSA: The Risks of Remote JDWP Debugging](https://foojay.io/today/psa-the-risks-of-remote-jdwp-debugging/)
  * [Foojay: A Short Primer on Java Debugging Internals](https://foojay.io/today/a-short-primer-on-java-debugging-internals/)
* 19'43 Exception breakpoints
  * [Foojay: Exception Breakpoint that Doesn't Suck and a Real Use Case for Method Breakpoints](https://foojay.io/today/exception-breakpoint-that-doesnt-suck-and-a-real-use-case-for-method-breakpoints/)
* 20'34 External debugging tools
  * <https://rubberduckdebugging.com/>
* 26'55 Observability
  * <https://istio.io/latest/about/service-mesh/>
  * <https://www.redhat.com/en/topics/microservices/what-is-a-service-mesh>
  * <https://openjdk.org/jeps/435>
* 37'58 What information should you look for while debugging
* 45'46 Be aware of tunnel vision while debugging
* 49'33 What to do if you don't know where to search for the bug
  * <https://git-scm.com/docs/git-bisect>
  * [Foojay: External Debugging Tools 2: git bisect](https://foojay.io/today/understand-the-root-cause-of-regressions-with-git-bisect/)
* 57'05 Outro
