---
title: "Foojay Podcast #23: Java Profiling and Performance"
slug: "foojay-podcast-23"
date: "2023-05-22T10:23:52+00:00"
lastmod: "2025-11-13T09:05:44+00:00"
description: "There are challenges with Java profiling, and the need for profiling depends on application complexity and performance requirements."
authors:
  - "frankdelporte"
  - "hirt"
image: "podcast-guests-profiling-and-performance-1024x404-1.png"
categories:
  - "DevOps"
  - "Java Core"
  - "Microservices"
  - "Performance"
  - "Podcast"
tags:
related_posts:
  - "foojay-podcast-14"
  - "continuous-production-profiling-and-diagnostics"
  - "contributing-to-openjdk-mission-control"
  - "virtual-thread-pinning-field-guide"
frozen: false
---

How do you get the maximum performance out of your Java application?

And how to use profiling to find the bottlenecks?

Let's learn all about it in this podcast, with Heinz Kabutz, Marcus Lagergren, Chris Newland, and Frank Delporte!  

Java profiling is a crucial technique for measuring and improving the performance of applications.

It helps identify bottlenecks, memory leaks, and other application performance issues.

There are various challenges with using Java profiling, and the need for profiling depends on the complexity of the application and the performance requirements.

Let us learn more about the challenges, different profiling approaches, and when to use Java profiling to reach the best performance with our Java code.

{{< youtube lVmIEhYNAfg >}}

Podcast Apps
------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

**Guests**
----------

* Chris Newland
  * [@chriswhocodes](https://twitter.com/chriswhocodes)
  * [mastodon.soc](https://mastodon.social/@chriswhocodes)[ial/@c](https://mastodon.social/@chriswhocodes)[hriswhocodes](https://mastodon.social/@chriswhocodes)
  * [chriswhocodes.com/](https://chriswhocodes.com/)
* Marcus Hirt
  * [@hirt](https://twitter.com/hirt)
  * [hirt.se/blog/](http://hirt.se/blog/)
* Heinz Kabutz
  * [@heinzkabutz](https://twitter.com/heinzkabutz)
  * [linkedin.com/in/heinzkabutz](https://linkedin.com/in/heinzkabutz)
  * The JavaSpecialists' Newsletter: [www.javaspecialists.eu/](https://www.javaspecialists.eu/)

**Podcast**
-----------

* Host: Marcus Lagergren
  * [@lagergren](https://twitter.com/lagergren)
* Production: Frank Delporte
  * [@FrankDelporte](https://twitter.com/FrankDelporte)
  * [foojay.social/@frankdelporte](https://foojay.social/@frankdelporte)

**Content**
-----------

* 00'00 Introduction of the host and guests
  * [jitwatch](https://chrisnewland.com/jitwatch)
  * [jacoline](https://jacoline.dev/inspect)
  * [foojay.io/command-line-arguments](https://foojay.io/command-line-arguments/openjdk-11/?tab=alloptions)
  * [Book: Optimizing Java](https://optimizingjava.com/)
  * [JCrete](https://www.jcrete.org/)
* 10'42 History of Java and how performance was a challenge in the beginning
* 14'21 What is profiling? What should be profiled? What is good profiling?
* 28'44 What you should learn about profiling and performance
* 31'43 Impact of the different garbage collectors on performance
* 32'59 Performance and profile should focus on the right requirement for your system
* 34'39 Ergonomics in the JVM and tunes itself for the system it is running on
  * [mail.openjdk.org/pipermail/hotspot-dev/2023-May/074325.html](http://mail.openjdk.org/pipermail/hotspot-dev/2023-May/074325.html)
* 39'49 What are current important evolutions and upcoming coming or required changes in profiling?
* 43'19 Break-throughs in Stop-The-World approaches
* 46'43 Minimize the number of JVM flags you use
  * <https://jacoline.dev/stats>
* 56'47 About Errors and Exceptions
* 58'30 The current runtimes and operating systems are very forgiving
  * <https://openjdk.org/jeps/312> (Thread-Local Handshakes)
  * <https://openjdk.org/jeps/444> (Virtual Threads)
* 1:04'26 Is profiling becoming less relevant?
  * [foojay.io/today/continuous-production-profiling-and-diagnostics](https://foojay.io/today/continuous-production-profiling-and-diagnostics/)
* 1:10'20 Conclusion
