---
title: "Changes Included in Release 24.02 of Azul Zing Builds of OpenJDK"
slug: "changes-included-in-release-24-02-of-azul-zing-builds-of-openjdk"
date: "2024-04-03T20:25:50+00:00"
lastmod: "2024-04-03T20:26:46+00:00"
description: "In addition to many improvements and fixes, the new Stable Azul Zing Build of OpenJDK includes the new Long Term Support (LTS) version, OpenJDK 21."
canonical: "https://www.azul.com/blog/changes-included-in-release-24-02-of-azul-zing-builds-of-openjdk/"
authors:
  - "frankdelporte"
  - "matt-van-order"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Java"
  - "Release Notes"
tags:
related_posts:
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "apple-silicon-with-zulu-openjdk-and-intellij-idea"
  - "are-java-security-updates-important"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
frozen: false
---

[Azul Platform Prime](https://www.azul.com/products/prime) is a Java platform with a modern, TCK-compliant JVM, Azul Zing, based on OpenJDK.

Zing provides low, consistent response latency of your Java workloads, higher total throughput and carrying capacity, faster warm-up, and infrastructure savings, achieved thanks to the [C4 pauseless garbage collector](https://www.azul.com/products/components/pgc/), [Falcon JIT compiler](https://www.azul.com/products/components/falcon-jit-compiler/) and other technologies created by Azul.

Zing Builds are available in two versions, either for evaluation or production use:

* **Stream Builds** : Fast-moving monthly releases (end of the month) that include the latest features and changes in PSU releases. These are **free for development and evaluation**. Using Stream Builds in production requires an active subscription. Their version number is based on year and month. For instance, "23.07.0.0" is the July Stream Build of 2023.
* **Stable Builds** : Builds that incorporate only CPUs, PSUs, and Azul Zing critical fixes and do not uptake new features and non-critical enhancements from Stream Builds. Stable Builds are our primary vehicle for delivering time-sensitive bug fixes to customers and are **only available to Azul customers**. Their version number is based on the Stream Build they originated from. For instance, "22.02.501.0" was derived from the February Stream Build of 2022 but already had multiple updates, as the 501 number indicates.

As stream builds happen in a fixed schedule, all changes are included in the [release notes](https://docs.azul.com/prime/release-notes).

Twice a year (in February and August), a Stream build becomes the new Stable build, providing a new version with many more improvements. In this post, we want to give you an overview of all the combined improvements in the release 24.02.0.0.

Changes Included in 24.02 {#h-changes-included-in-24-02}
--------------------------------------------------------

As Stable builds overlap, your system should be on the 23.08 Stable line, and you have a window of four months to test and migrate to the 24.02 Stable line.

Let's look at some of the most significant changes between the Stream Build releases 23.08 and 24.02.

Version 24.02.0.0 is the branching point for the new Stable Build line, and includes all the following changes compared to the previous Stable Builds based on 23.08.

### Rebranding {#h-rebranding}

From now on, the JVM provided by Azul Platform Prime will be referenced as Azul Zing Builds of OpenJDK (Zing).

This name was used before and was recently reintroduced to clarify the distinction between Platform Prime, a set of tools and services, and the Zing JDK.

### Security fixes {#h-security-fixes}

This version includes the October 2023 and January 2024 CPU and PSU release security fixes, including CPU and PSU fixes for Azul Zing Builds of OpenJDK 21.

### Changes in supported versions {#h-changes-in-supported-versions}

Since 23.10, Azul Zing contains the General Availability (GA) release of OpenJDK 21.

### Java Runtime Environment (JRE) {#h-java-runtime-environment-jre}

A new lightweight, fully functional distribution of the Java Runtime Environment (JRE) is now included for Java 8, 11, 17, and 21. The new Java JREs save a significant amount of space by removing various debugging options, developer options, and by eliminating various tools such as GC Log Analyzer.

JRE builds are ideal in containerized environments, where local file size is limited and the size of the image downloaded contributes to the overall time to first transaction when starting a new JVM.

The Zing JRE distributions still fully support [Azul Optimizer Hub](https://docs.azul.com/optimizer-hub/) and [Azul Intelligence Cloud](https://docs.azul.com/intelligence-cloud/) (formerly known as Azul Vulnerability Detection).

|------------------------------------------------------------|-------------------------|------------------------------|-------------------------|------------------------------|
|                                                            | **JDK**                                               || **JRE**                                               ||
| Distribution and artifact name (\*)                        | Compressed archive (MB) | Unpackaged distribution (MB) | Compressed archive (MB) | Unpackaged distribution (MB) |
| JDK 8.0.402 `zing24.02.0.0-4-TYPE8.0.402-linux_x64.tar.gz` | 216                     | 468                          | 146                     | 324                          |
| JDK 11.0.22 `zing24.02.0.0-4-TYPE11.0.22-linux_x64.tar.gz` | 295                     | 552                          | 143                     | 322                          |
| JDK 17.0.10 `zing24.02.0.0-4-TYPE17.0.10-linux_x64.tar.gz` | 301                     | 560                          | 136                     | 305                          |
| JDK 21.0.2 `zing24.02.0.0-4-TYPE21.0.2-linux_x64.tar.gz`   | 313                     | 582                          | 140                     | 312                          |

(\*) TYPE = `jdk` or `jre`

### Garbage Collector Log Analyzer (GCLA) Improvements {#h-garbage-collector-log-analyzer-gcla-nbsp-improvements}

More concise logging of the Compilation Ranking feature has been implemented in 23.12 to better asses the behavior and impact of this feature.

Newly collected data has been added to the pre-existing charts in GC Log Analyzer, *Compiler Statistics \> Compiler Queues* and *Compiler Statistic \> Tier 2 Compiler Counts*. Newly collected and viewable data includes the following:

* The total number of hot and warm methods that have made it to the compile queue is divided by the total number of methods.
* The total number of hot and warm methods that have begun compilation is split from the total number of methods.
* The total number of methods that were *not* promoted to the compiler queue due to being identified as cold methods. These are methods that have reached the compile threshold but not quickly enough to be considered warm or hot methods.

Since 24.01, the garbage collector's (GC) CPU usage is logged by default. Previously, you had to use the option `-XX+:PrintGCDetails`. By default, you can view these metrics in GC Log Analyzer in the *GC CPU Usage* graph.

### ARM Support for Optimizer Hub {#h-arm-support-for-optimizer-hub}

Zing 24.02.0.0 includes support for Optimizer Hub (formerly Cloud Native Compiler) on Arm64 system architecture.

### ReadyNow Support {#h-readynow-support}

This new Zing version improves integration with [ReadyNow Orchestrator](https://docs.azul.com/optimizer-hub/about/readynow-orchestrator).

**Prioritization of Profile Generations**

ReadyNow Orchestrator allows you to set different minimum sizes and recording durations for different generations of your profiles. Often, you want to promote the first generation of your profile as quickly as possible so new JVMs are not starting with nothing. Still, you want your second generation to record for a longer time before promotion so it is more complete.

New configuration settings are available to achieve this: `minProfileSize`, `minProfileDuration`, `minProfileSizePerGeneration`, and `minProfileDurationPerGeneration`. Check [ReadyNow Orchestrator Defaults](https://docs.azul.com/optimizer-hub/configuring/readynow-orchestrator.html#readynow-orchestrator-defaults) for more info.

**Fallback to Local Profiles**

A new experimental feature is available on client side with the command line option "-XX:RNOProfileFallbackInput". With this option, you can configure a local filesystem path, which gets used in case no profile data is available from ReadyNow Orchestrator, e.g., in case of a missing connection or the requested profile name doesn't exist on the server.

### Changes in Command Line Options {#h-changes-in-command-line-options}

* A new option in 23.09, `GPGCSafepointWaitForMutatorResume`, has been introduced and is set to `true` by default. This flag tells the Garbage Collector to pause and wait for mutator threads to be woken up before resuming, after every GC safepoint. If `-XX:-GPGCSafepointWaitForMutatorResume` is set, the Garbage Collector resumes its work in parallel with mutator threads waking up.
* A small but significant change is introduced in 24.01 to the behavior of `-XX:ProfileStartupLimitInSeconds`. Now, when you set this option to `0`, it means 0 seconds. Previously, if you set this flag to `0`, it would be interpreted as "infinite". You can still specify "infinite" by using any negative number, for example `-1`. The default behavior without setting this option remains the same, i.e. the default value has changed from `0` to `-1`.

Conclusion {#h-conclusion}
--------------------------

In addition to many improvements and fixes, the new Stable Azul Zing Build of OpenJDK includes the new Long Term Support (LTS) version, OpenJDK 21.

It makes it the best Java runtime for your production environment!

<br />

<br />
