---
title: "The Story of a Java 17 Native Memory Leak"
date: "2022-09-13T08:15:53+00:00"
lastmod: "2022-09-13T08:15:55+00:00"
description: "How a native memory leak in Java 17 got fixed, improving the future operability of the JVM platform for millions!"
canonical: "https://www.nickebbitt.com/blog/2022/01/26/the-story-of-a-java17-memory-leak"
authors:
  - "nick-ebbitt"
image: "twitter-java-17.png"
categories:
  - "Java Core"
  - "Performance"
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "7-functional-programming-techniques-in-java-a-primer"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

## Context

When Java 17 was released, we (the platform team at [Auto Trader](https://careers.autotrader.co.uk/) where I was working at the time) were fairly quick to provide a new Docker base image to allow our developers to gain the benefits of the new goodness in the JDK available since Java 11, the previous LTS version.

Over the course of a few years, we've standardised the way the JVM is configured out of the box for any new applications that make use of the base image. In general, this provides product teams with a good default starting point from which to get up \& running quickly with any new service they plan to deploy.

Up until the release of Java 17 the majority of workloads targetted Java 11 which by default uses the G1 garbage collector (G1GC).

One decision we made therefore was to enable [String Deduplication](https://openjdk.java.net/jeps/192) (`-XX:+UseStringDeduplication`) by default for all workloads.

The goal of this flag is to reduce the live heap size by automatically deduplicating the use of duplicate strings. This achieved the desired result, reducing the overall memory footprint across our platform.

## Discovery

The new Java 17 base image was adopted and deployed through to production for a number of services pretty much straight away.

Everything looked good.

In general we were seeing a reduced memory footprint across all workloads, as well better metrics around GC frequency and times due to the muiltitude of improvements made in the JVM since Java 11.

Then, after a few days, we noticed that the overall memory footprint for a few of the services was slowly increasing.

As part of the Java base images we start applications with the [Prometheus JMX Exporter](https://github.com/prometheus/jmx_exporter) agent. This stands up a simple web server alongside the application that consumes and exposes the JVM's JMX management beans as Prometheus metrics. When a service is deployed a scrape is automatically configured to pull the metrics into Prometheus.

From these metrics we could quite quickly see that both the heap and non-heap memory areas were behaving as expected, no obvious slow memory leak there. The direct memory looked fine too.

This meant we were dealing with other native memory usage being consumed by the process.

At this point I reached for [Native Memory Tracking](https://docs.oracle.com/en/java/javase/17/vm/native-memory-tracking.html) but this involved enabling it via JVM option and then using the `jcmd` tool against live workloads.

As the leak was pretty slow our first attempts with this didn't provide anything useful.

Unsure where to look next I decided to see if anyone in the community was seeing similar issues.

> [#Java](https://twitter.com/hashtag/Java?src=hash&ref_src=twsrc%5Etfw) people who are using Java 17 - has anyone noticed a change in their app's memory usage since you upgraded?  
>
>
> On some apps we're seeing slow memory growth/leak. It's appears to be at the OS/native memory level as the heap is stable, as are the non-heap and direct memory areas [pic.twitter.com/rv5J2Dxdgp](https://t.co/rv5J2Dxdgp)
> — Nick Ebbitt (@nickebbitt) [October 27, 2021](https://twitter.com/nickebbitt/status/1453284230964912153?ref_src=twsrc%5Etfw)

Unfortunately this didn't get much traction.

## Trial \& Error

Java 17 brings many benefits (e.g. improved GC) from both a platform and language perspective so it felt right to invest more time trying to understand what was actually happening.

This mainly took the form lot's of trial and error such as experimenting with different JVM options and swapping out the JDK for different distributions to help narrow down the problem.

The most significant learning that came from this was that we didn't see the leak using GraalVM whereas all other HotSpot based distributions did produce the leak. Still though, this didn't narrow the problem space down enough to really give us any actionable information.

Something that helped with the experimentation is that Auto Trader has invested heavily in creating a delivery platform that supports an efficient and safe CI/CD process. Changes at both the application and base image level could be made, rolled out and verified within a short space of time.

However, the fact that the visibility of the leak was slow to manifest meant that overall this was still a pretty painful feedback loop!

## Progress

Having originally made little progress using Native Memory Tracking, one of our engineers decided to take another look.

To work around the challenge of the leak being so slow to manifest and the awkwardness of using `jcmd` on a live deployment they decided to externalise the Native Memory Tracking data via a set custom Prometheus metrics. This was achieved by executing JVM diagnostic commands (the equivalent of those used with `jcmd` from the command line) via Java code using the [ManagementFactory](https://docs.oracle.com/en/java/javase/17/docs/api/java.management/java/lang/management/ManagementFactory.html) capability.

Here's a snippet of the kind of thing they got working…

```java
ManagementFactory.getPlatformMBeanServer().invoke(
    new ObjectName("com.sun.management:type=DiagnosticCommand"),
    "vmNativeMemory,
    new Object[]{"summary"},
    new String[]{"[Ljava.lang.String;"});
```

The output from this was effectively the same as running `jcmd ${pid} VM.native_memory summary` against a running process from a terminal, for example:

```
$ jcmd 1 VM.native_memory summary
1:

Native Memory Tracking:

(Omitting categories weighting less than 1KB)

Total: reserved=1530885KB, committed=1079713KB
-                 Java Heap (reserved=819200KB, committed=819200KB)
                            (mmap: reserved=819200KB, committed=819200KB)

-                     Class (reserved=215479KB, committed=10679KB)
                            (classes #13404)
                            (  instance classes #12588, array classes #816)
                            (malloc=2487KB #50156)
                            (mmap: reserved=212992KB, committed=8192KB)
                            (  Metadata:   )
                            (    reserved=65536KB, committed=58624KB)
                            (    used=58053KB)
                            (    waste=571KB =0.97%)
                            (  Class space:)
                            (    reserved=212992KB, committed=8192KB)
                            (    used=7337KB)
                            (    waste=855KB =10.44%)

-                    Thread (reserved=59433KB, committed=9281KB)
                            (thread #94)
                            (stack: reserved=59172KB, committed=9020KB)
                            (malloc=153KB #562)
                            (arena=108KB #185)

-                      Code (reserved=252117KB, committed=62997KB)
                            (malloc=4429KB #18870)
                            (mmap: reserved=247688KB, committed=58568KB)

-                        GC (reserved=79110KB, committed=79110KB)
                            (malloc=15834KB #27193)
                            (mmap: reserved=63276KB, committed=63276KB)

-                  Compiler (reserved=986KB, committed=986KB)
                            (malloc=822KB #1325)
                            (arena=165KB #5)

-                  Internal (reserved=895KB, committed=895KB)
                            (malloc=891KB #25578)
                            (mmap: reserved=4KB, committed=4KB)

-                     Other (reserved=2395KB, committed=2395KB)
                            (malloc=2395KB #59)

-                    Symbol (reserved=11408KB, committed=11408KB)
                            (malloc=9898KB #282713)
                            (arena=1511KB #1)

-    Native Memory Tracking (reserved=7043KB, committed=7043KB)
                            (malloc=21KB #315)
                            (tracking overhead=7022KB)

-        Shared class space (reserved=12288KB, committed=12100KB)
                            (mmap: reserved=12288KB, committed=12100KB)

-               Arena Chunk (reserved=178KB, committed=178KB)
                            (malloc=178KB)

-                   Tracing (reserved=32KB, committed=32KB)
                            (arena=32KB #1)

-                   Logging (reserved=8KB, committed=8KB)
                            (malloc=8KB #294)

-                 Arguments (reserved=2KB, committed=2KB)
                            (malloc=2KB #87)

-                    Module (reserved=1698KB, committed=1698KB)
                            (malloc=1698KB #6218)

-                 Safepoint (reserved=8KB, committed=8KB)
                            (mmap: reserved=8KB, committed=8KB)

-           Synchronization (reserved=216KB, committed=216KB)
                            (malloc=216KB #2247)

-            Serviceability (reserved=1KB, committed=1KB)
                            (malloc=1KB #18)

-                 Metaspace (reserved=66258KB, committed=59346KB)
                            (malloc=722KB #879)
                            (mmap: reserved=65536KB, committed=58624KB)

-      String Deduplication (reserved=2097KB, committed=2097KB)
                            (malloc=2097KB #32625)

-                   Unknown (reserved=32KB, committed=32KB)
                            (mmap: reserved=32KB, committed=32KB)
```

The output was then parsed and transformed into the correct metric format for Prometheus and exposed via a metrics endpoint on the service.

*In the past I've wondered why Native Memory Tracking data are not exposed via JMX beans similarly to how heap/non-heap memory usage is, this issue made me consider this again so I reached out to [Aleksey Shipilëv](https://twitter.com/shipilev) to see if he knew. He didn't but suggested maybe it just hadn't been worked on yet. I did a bit of digging and found [this issue](https://bugs.openjdk.java.net/browse/JDK-8182634) in the OpenJDK bug tracker which suggest that is the case.*

This approach allowed us to visualise the Native Memory Tracking data and importantly leave it running long enough to see if any specific memory areas were growing disproportionately compared to others.

The memory area that stood out was `String Deduplication`.

To validate whether this was the cause they explicitly disabled String Deduplication (`-XX:-UseStringDeduplication`) for the service and observed again.

No leak!

This gave us some additional information so I jumped back on Twitter to call for help again…

> One of our devs worked out that the memory issue with Java 17 goes away when you disable String Deduplication.   
>
>
> So something changed between Java 11 -\> 17 that means with -XX:+UseStringDeduplication we see a slow off-heap memory leak <https://t.co/QWwNRy2rZr>
> — Nick Ebbitt (@nickebbitt) [November 30, 2021](https://twitter.com/nickebbitt/status/1465617649547849733?ref_src=twsrc%5Etfw)

## The bug \& the fix

Within 15 minutes of posting the tweet a member of the Java community spotted it and tagged [Aleksey Shipilëv](https://twitter.com/shipilev) who works for Red Hat and is a subject matter expert when it comes to GC on the JVM.

Within a few hours they had [reproduced it and filed a bug report](https://bugs.openjdk.java.net/browse/JDK-8277981), as well [submitted a PR to the Open JDK project](https://github.com/openjdk/jdk/pull/6613) with the fix. It turns out it was a simple maths problem.

> Ha-ha, math problem, whoops. <https://t.co/lKM04DKs77>
> — Aleksey Shipilëv (@shipilev) [November 30, 2021](https://twitter.com/shipilev/status/1465659474773950467?ref_src=twsrc%5Etfw)

This was awesome!

## Verification

We were eager to get our hands on the fix however it wouldn't be available in an official release of the JDK until the next patch version, `17.0.2`, which wasn't due until the middle of January.

We decided to verify the fix by creating a Java base Docker image using the nightly builds produced via the `openjdk17u` branch available via Adoptium's [temurin17-binaries](https://github.com/adoptium/temurin17-binaries/releases/tag/jdk-17%2B35) GitHub repo.

This gave us a way to deploy a version of Java 17 with the fix and provide feedback/confidence that it had in fact resolved the issues we'd seen with `String Deduplication`.
> Note: I wouldn't recommend running these nightly builds in production as they haven't gone through the same rigourous testing \& quality checks that an official release build will have.

This indeed did verify that we were no longer seeing the memory leak.

> Deployed a nightly containing the fix for one of the affected workloads, not that there was any doubt but it's looking good 😀  
>
>
> You can see multiple 17.0.1 deployments with StringDeduplication on/off, the final one with -XX:+UseStringDeduplication and the fix  
>
>
> Thanks [@shipilev](https://twitter.com/shipilev?ref_src=twsrc%5Etfw) 👏 [pic.twitter.com/GdrhCSv5GM](https://t.co/GdrhCSv5GM)
> — Nick Ebbitt (@nickebbitt) [December 7, 2021](https://twitter.com/nickebbitt/status/1468157655365607425?ref_src=twsrc%5Etfw)

## The future

At the time of writing this, Java 17.0.2 is due to be released as part of the scheduled quarterly updates.

Once available our process for producing the Java 17 base Docker image will pick up the new version and our consumer applications will start to use it.

We'll also then consider re-enabling `String Deduplication` with renewed confidence in Java 17.

## Final thoughts…

I hope you enjoyed this little story.

I shared it because, for me, it demonstrates one of the power of the Java community, as well as a good side of social media.

Sometimes it takes the actions of one person to choose to engage and connect other people to trigger a series of events that result in a better environment for all.

In this case it was the connection from me to [Aleksey Shipilëv](https://twitter.com/shipilev) by [Steven Willems](https://twitter.com/willemsst) that made all the difference.

That one connection has resulted in a native memory leak in Java 17 being fixed, improving the future operability of the JVM platform for millions of developers and organisations worldwide.
