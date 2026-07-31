---
title: "Foojay Podcast #68: Welcome to OpenJDK (Java) 24"
slug: "foojay-podcast-68"
date: "2025-03-17T06:44:08+00:00"
lastmod: "2025-11-13T08:38:56+00:00"
description: "We serve you a podcast about the new Java version every six months. Our regular guest, Simon Ritter, Deputy CTO of Azul, is known on social media as - by Frank Delporte"
authors:
  - "frankdelporte"
  - "hanno-embregts"
  - "simonritter"
image: "episode-68-java-24.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Podcast"
  - "Release Notes"
tags:
related_posts:
  - "java-24-rolls-out-today-find-out-why-its-aptly-named"
  - "java-24-whats-new"
  - "foojay-podcast-57"
  - "foojay-podcast-45"
frozen: false
---

We serve you a podcast about the new Java version every six months.

Our regular guest, Simon Ritter, Deputy CTO of Azul, is known on social media as "speakjava." He is part of the OpenJDK vulnerability group, JCP executive committee, and expert group for the Java SE specification request so that he can share a lot of inside information with us.

In this episode, we are joined by Hanno Embregts, a Java Developer by day and musician by night. He publishes a post on Foojay with all the details of every new Java release and prepared a long description of all the new features included in Java 24.

Let's see what this new release brings us...

Video {#h2-0-video}
-------------------

{{< youtube hIgw8lo0zgw >}}

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Read more about Java 24 on Foojay {#h2-2-read-more-about-java-24-on-foojay}
---------------------------------------------------------------------------

* [Java 24 Rolls Out Today! Find Out Why It's Aptly Named](https://foojay.io/today/java-24-rolls-out-today-find-out-why-its-aptly-named/) by Hanno Embregts
* [Java 24 : What's New?](https://foojay.io/today/java-24-whats-new/) by Loic Mathieu

Guests {#h2-3-guests}
---------------------

### Simon Ritter {#h3-4-simon-ritter}

* <https://www.linkedin.com/in/siritter/>
* <https://bsky.app/profile/speakjava.bsky.social>

### Hanno Embregts {#h3-5-hanno-embregts}

* <https://www.linkedin.com/in/hannotify/>
* <https://bsky.app/profile/hanno.codes>

Content {#h2-6-content}
-----------------------

00:00 Introduction of the topic and guests  

00:58 Why 24 JEPs in release 24?  

02:16 Overview of the changes in Java 24

03:37 **The changes in Hotspot and GC**   

JEP [404](https://openjdk.org/jeps/404): Generational Shenandoah (Experimental)  

JEP [450](https://openjdk.org/jeps/450): Compact Object Headers (Experimental)  

JEP [475](https://openjdk.org/jeps/475): Late Barrier Expansion for G1  

04:46 JEP [483](https://openjdk.org/jeps/483): Ahead-of-Time Class Loading \& Linking  

07:30 JEP [491](https://openjdk.org/jeps/491): Synchronize Virtual Threads without Pinning

10:27 **Security JEPs and Quantum Resistance**   

JEP [478](https://openjdk.org/jeps/478): Key Derivation Function API (Preview)  

JEP [496](https://openjdk.org/jeps/496): Quantum-Resistant Module-Lattice-Based Key Encapsulation Mechanism  

JEP [497](https://openjdk.org/jeps/497): Quantum-Resistant Module-Lattice-Based Digital Signature Algorithm

13:00 **Tools**   

JEP [493](https://openjdk.org/jeps/493): Linking Run-Time Images without JMODs

16:47 **Repreviews and Finalizations**   

JEP [489](https://openjdk.org/jeps/489): Vector API (Ninth Incubator)  

18:27 JEP [484](https://openjdk.org/jeps/484): Class-File API  

19:13 JEP [485](https://openjdk.org/jeps/485): Stream Gatherers  

21:22 JEP [487](https://openjdk.org/jeps/487): Scoped Values (Fourth Preview)  

22:15 JEP [488](https://openjdk.org/jeps/488): Primitive Types in Patterns, instanceof, and switch (Second Preview)  

22:30 How JEPs get finalized and included  

23:44 JEP [492](https://openjdk.org/jeps/492): Flexible Constructor Bodies (Third Preview)  

24:09 JEP [494](https://openjdk.org/jeps/494): Module Import Declarations (Second Preview)  

25:07 JEP [495](https://openjdk.org/jeps/495): Simple Source Files and Instance Main Methods (Fourth Preview)  

29:24 JEP [499](https://openjdk.org/jeps/499): Structured Concurrency (Fourth Preview)

34:04 **Deprecations \& Restrictions**   

34:46 JEP [472](https://openjdk.org/jeps/472): Prepare to Restrict the Use of JNI  

37:15 JEP [486](https://openjdk.org/jeps/486): Permanently Disable the Security Manager  

38:53 JEP [490](https://openjdk.org/jeps/490): ZGC: Remove the Non-Generational Mode  
[Trash Talk - Exploring the JVM memory management by Gerrit Grunwald](https://www.youtube.com/watch?v=Jh79ojcror0)  

42:09 JEP [498](https://openjdk.org/jeps/498): Warn upon Use of Memory-Access Methods in sun.misc.Unsafe  

45:43 Removal of 32-bit support  

JEP [479](https://openjdk.org/jeps/479): Remove the Windows 32-bit x86 Port  

JEP [501](https://openjdk.org/jeps/501): Deprecate the 32-bit x86 Port for Removal

47:37 Should we use Java 24 in production?  

51:09 Looking forward to the next LTS in September  

54:14 Conclusion

<br />
