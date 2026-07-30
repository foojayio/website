---
title: "Java Profiling Overview"
slug: "java-profiling-overview"
date: "2022-11-14T09:14:25+00:00"
lastmod: "2022-12-06T15:32:49+00:00"
description: "When I ask Java devs whether they profile, the answer is usually \"no\". A reason is the lack of info and knowledge for everyday developers."
authors:
  - "johannes-bechberger"
image: "/images/posts/2022/11/java-profiling-overview/Screenshot-2022-10-24-at-11.46.10.png"
categories:
  - "Developer Tools"
  - "JDK Flight Recorder"
  - "Performance"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "continuous-production-profiling-and-diagnostics"
  - "external-debugging-tools-1-dtrace-and-strace"
  - "using-java-flight-recorder-and-mission-control-part-1"
  - "using-async-profiler-and-jattach-programmatically-with-ap-loader"
frozen: false
---

When I ask Java developers whether they do profile, the answer is usually "no".

The few that profiled before usually used VisualVM as a student and maybe JProfiler or YourKit years ago at work.

One of the reasons for this is a lack of available information and thus knowledge for everyday Java developers.

This is a pity as profiling should be a part of the tool belt for every experienced developer (not just for Java). The problem is that most of the open-source profilers are targeted to the OpenJDK developers (or their colleagues), even if they won't admit it. This can be seen in the lack of entry-level material on this topic and even the little that is out there is distributed across multiple conference websites, blogs, YouTube channels, and Twitter accounts.

A few months ago I started working on this topic and as a result, held a talk at the Java User Group Karlsruhe in the middle of October: It is an introductory talk answering the simple questions: Why *should we profile* ? Which *profilers to use* ? How *to obtain and view these profiles*? A recording can be seen on YouTube:

{{< youtube Fglxqjcq4h0 >}}

<br />

The gist of this talk is:

* Why? Profiling helps you find the parts of your code that are slow and that are worth to be fixed.
* Which and how? The consensus seems to be to use JFR or [async-profiler with --jfrsync](https://github.com/jvm-profiling-tools/async-profiler) and [JMC](https://wiki.openjdk.org/display/jmc/Main) as a profile viewer.

While working on this talk, I collected a list of interesting conference talks on this topic:

{{< youtube videoseries >}}

<br />

This list includes talks on a variety of profilers, ranging from deep dives to overviews. Many of these talks and people were recommended elsewhere on the internet, on blogs, on Twitter, or in private conversations. Which I present in the following.

{#more-61012}

I start with a collection of notable blogs which you definitely read if you're interested to go deeper into profiling:

* Aleksey Shipilev: <https://shipilev.net/>
* Nitsan Wkart: <http://psy-lob-saw.blogspot.com/>
* Richard Startin: <https://richardstartin.github.io/>
* Peter Lawrey: <http://blog.vanillajava.blog/>
* Martin Thompson: <https://mechanical-sympathy.blogspot.com/>
* Jean-Philippe Bempel: <https://jpbempel.github.io/>
* Marcus Hirt: <http://hirt.se/blog/>
* Krzysztof Ślusarski: <https://krzysztofslusarski.github.io/>

Even the decade-old posts on these blogs are worth reading, as the foundations of profiling did not change in the last few years. Only the tools themselves got more powerful.

In addition to the profiling-focused blogs there are also one-of resources in other locations:

* [Improving the performance of the Spring-Petclinic sample application (part 1 of 5)](https://blog.ippon.fr/2013/03/11/improving-the-performance-of-the-spring-petclinic-sample-application-part-1-of-5/)
* [Hunting down code hotspots is probably the most common task for Java profilers.](https://bell-sw.com/announcements/2020/07/22/Hunting-down-code-hotspots-with-JDK-Flight-Recorder/)
* [Using Java Flight Recorder and Mission Control (Part 1) \| foojay](https://foojay.io/today/using-java-flight-recorder-and-mission-control-part-1/)
* [JMC/JFR: Kotlin spezial: Profiling/Monitoring with joy (talk slides)](https://raw.githubusercontent.com/mirage22/jmc-jvm-lang-tutorial/master/20211109_IngJUG_JFR_KotlinSpezial.pdf)
* [](https://foojay.io/today/continuous-production-profiling-and-diagnostics/)[Continuous Production Profiling and Diagnostics \| foojay](https://foojay.io/today/continuous-production-profiling-and-diagnostics/)

My collection process started before people considered leaving Twitter, so here is a list of people that can be followed on Twitter (and Mastodon) that tweet regularly on the topic of profiling and performance engineering:

* [Andrei Pangin](https://twitter.com/AndreiPangin): Creator of async-profiler
* [JVMPerformance](https://twitter.com/JVMPerformance): JVM performance news (old)
* [Alexsey Shipilev](https://twitter.com/shipilev)
* [Jean-Philippe Bempel](https://twitter.com/jpbempel)
* [Peter Veentjer](https://twitter.com/PeterVeentjer)
* [Gunnar Morling](https://twitter.com/gunnarmorling)
* [Mario Fusco](https://twitter.com/mariofusco)
* [Francesco Nigro](https://twitter.com/forked_franz)
* [Chris Newland](https://twitter.com/chriswhocodes): with his homepage full of helper tools to explore JEPs, VM options, ... ([chriswhocodes.com](https://www.chriswhocodes.com/))

Following these users, you can keep up to date in the field of open-source profiling and discover new talks and discussions regularly.

I know that these resources probably won't cover the need of everyone. The lack of entry- and mid-level tutorials and talks is still a problem. So I hope that many people from the vibrant profiler community see this outreach not as a burden, but as a virtue: Helping people to discover the joy in profiling and all the neat features that modern profilers have to offer.

<br />

If you think I missed a nice resource: Send me a tweet [@parttimen3rd](https://twitter.com/parttimen3rd/) or a toot [@\[email protected\]](https://mastodon.social/@parttimenerd)and open a Pull Request to the collection repo on [GitHub](https://github.com/parttimenerd/jug-profiling-talk). I'm also happy to hold a talk on the topic of profiling at your local user group.

<br />

<br />

***This whole endeavor is part of my work in the [SapMachine](https://sapmachine.io "SapMachine") team at [SAP](https://sap.com). This blog post first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de).***

<br />
