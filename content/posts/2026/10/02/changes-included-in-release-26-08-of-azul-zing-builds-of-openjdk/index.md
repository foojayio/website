---
title: "Changes Included in Release 26.08 of Azul Zing Builds of OpenJDK"
date: "2026-10-02"
description: "Azul Zing Builds of OpenJDK, the optimized Java runtime within Azul Platform Prime, has reached the release of the 26.08 Stable Release line."
canonical: "https://www.azul.com/blog/changes-included-in-release-26-08-of-azul-zing-builds-of-openjdk/"
authors:
  - "matt-van-order"
image: "zing-2608-banner.png"
categories:
  - "Release Notes"
related_posts:
  - "azul-august-2026-release-javas-first-monthly-cspu"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
  - "how-to-train-readynow-to-achieve-optimal-java-performance"
  - "changes-included-in-release-24-08-of-azul-zing-builds-of-openjdk"
---

Azul Zing Builds of OpenJDK, the optimized Java runtime within [Azul Platform Prime](https://www.azul.com/products/prime), has reached the release of the 26.08 Stable Release line. This Stable Build represents an August 2026 promotion from Stream to Stable status, consolidating six months of Stream Build work into a single, supported release line. The headline addition is **Compilation Streaming**, which lets a JVM download pre-compiled code from Azul Optimizer Hub at startup instead of waiting on its own JIT compilation queue.

Zing builds are available in two versions:

* **Stream Builds**: Fast-moving monthly releases that include the latest features and changes in PSU releases. These are **free for development and evaluation**. Using Stream Builds in production requires an active subscription. Their version number is based on year and month. For instance, "26.07.0.0" is the July Stream Build of 2026.
* **Stable Builds**: Builds that incorporate only CPUs, PSUs, and Azul Zing critical fixes and do not uptake new features and non-critical enhancements from Stream Builds. Stable Builds are our primary vehicle for delivering time-sensitive bug fixes to customers and are **only available to Azul customers**.

Twice a year (in February and August), a Stream build becomes the new Stable build, providing a new version with many more improvements. The 26.08 Stable Build incorporates all changes from Stream Builds 26.04 through 26.08. In this post, we want to give you an overview of all the combined improvements in the 26.08 Stable Line.

## Compilation Streaming

The most significant addition in 26.08 is Compilation Streaming, a new mechanism that reduces JVM warm-up time by letting the JVM download pre-compiled code streams from Optimizer Hub at startup rather than waiting on its own JIT queue. Compilation Streaming requires Optimizer Hub 26.08.0 or later, and it cannot be combined with Indirect Delivery of ReadyNow Orchestrator profiles.

| Command-Line Option | Description | Default |
|---|---|---|
| `-XX:[+/-]CNCEnableCompilationStreaming` | Enables compilation streaming | `false` |
| `-XX:CNCCompilationStreamingMaxActiveSegments=<number>` | Number of stream segments held in memory (3-5 recommended for production) | 6 |
| `-XX:CNCCompilationStreamingStopAfterSeconds=<value>` | Duration before streaming stops; `-1` = never, `0` = immediate | 600 |
| `-XX:[+/-]CNCCompilationStreamingPrintStatsAtExit` | Prints stream installation statistics on JVM exit | `false` |

A JVM crash that could occur during stream compilation resolution in version 26.08.0.0 has been resolved in 26.08.1.0.

## Security Fixes

This version includes the April and July 2026 CPU and PSU release security fixes, together with August 2026's **Critical Security Patch Update (CSPU)** — the first CSPU ever issued for Azul Zing Builds of OpenJDK. A CSPU delivers security and stability fixes in between the quarterly update releases, without introducing new features, API changes, or other behavior changes beyond the security fixes themselves.

## General Improvements

* Lightweight locking now includes thread-local `monitorenter` and `monitorexit` variants, improving Orca optimization.
* `-XX:+UseTaggedAddressForJavaHeap` is now enabled by default on ARM64. It is automatically disabled (with a warning logged) when `-Xmx` exceeds 512 GB.
* `-XX:+UseZlibNG` is now available on JDK 25 as a performance-optimized drop-in replacement for zlib.
* The Metaspace MemoryPool MXBean can now be enabled for OpenJDK compatibility using `-XX:+EnableMetaspacePoolMXBean`.
* `ThreadOpt` has been deprecated and now logs a warning message when used.
* The runtime bundle size has been reduced by stripping `librpc.so` and `librpccpp.so`.
* Several stability fixes address crashes in non-ZST mode with large pages, a null pointer issue in `AsyncGetCallTrace`, and JFR-related problems.

## Optimizer Hub, Local Fallback, and ReadyNow

* Methods that were compiled at a reduced optimization level during local fallback are now recompiled at the intended optimization level once Cloud Native Compiler becomes reachable again.
* A new option, `-XX:CNCProductivityRemoteCpuToLocalCpuEquivalenceFactor`, allows you to tune productivity-based fallback decisions.
* The default value of `-XX:ProfileLogOutMaxNominatedGenerationCount` has changed from `0` (infinite) to `4`.
* `-XX:+EnableRNO` and `-XX:ProfileLogName` can no longer be used together. Startup now fails with an error directing you to use `-XX:ProfileName` instead.

## Multi-Tiering

Three Multi-Tiering command-line options have been promoted from experimental to product status:

| Command-Line Option | Description | Default |
|---|---|---|
| `-XX:MultiTieringSamplingIntervalMS=<milliseconds>` | Sampling interval for Multi-Tiering methods | 60000 |
| `-XX:MultiTieringSamplingStartDelayMS=<milliseconds>` | Delay before sampling begins | 0 |
| `-XX:FalconMidTierOptimizationLevel=<level>` | Falcon optimization level used for mid-tier compilation | 0 |

Multi-Tiering remains disabled by default. You can enable it using `-XX:+UseMultiTiering`.

## Garbage Collection Logging and Falcon Compiler

* `-XX:GCLogPrintRecompStatistics` logs recompilation statistics to the GC logs.
* `-XX:FalconIntrinsifyFillInStackTrace0` controls whether the `Throwable.fillInStackTrace0(int)` intrinsic is used.
* A GC log write lock contention issue that could cause checkpoint timeouts has been resolved.

## Changes in Command Line Options

**New options:**

* The Compilation Streaming family: `-XX:[+/-]CNCEnableCompilationStreaming`, `-XX:CNCCompilationStreamingMaxActiveSegments`, `-XX:CNCCompilationStreamingStopAfterSeconds`, `-XX:[+/-]CNCCompilationStreamingPrintStatsAtExit`
* `-XX:CNCProductivityRemoteCpuToLocalCpuEquivalenceFactor`
* `-XX:GCLogPrintRecompStatistics`
* `-XX:FalconIntrinsifyFillInStackTrace0`
* `-XX:+EnableMetaspacePoolMXBean`

**Promoted from experimental to product:**

* `-XX:MultiTieringSamplingIntervalMS`
* `-XX:MultiTieringSamplingStartDelayMS`
* `-XX:FalconMidTierOptimizationLevel`

**Changed defaults:**

* `-XX:ProfileLogOutMaxNominatedGenerationCount`: `0` → `4`
* `-XX:+UseTaggedAddressForJavaHeap`: now enabled by default on ARM64

**Deprecated:**

* `-XX:+UseThreadOpt`
* `-XX:ThreadOptOptions`

**Incompatible combinations:**

* `-XX:+EnableRNO` combined with `-XX:ProfileLogName` (use `-XX:ProfileName` instead)

## OpenJDK Versions in 26.08.1.0

| Major Version | OpenJDK Version |
|---|---|
| 8 | 1.8.0_504-b2 |
| 11 | 11.0.32.1+1-LTS |
| 17 | 17.0.20.1+1-LTS |
| 21 | 21.0.12.1+1-LTS |
| 25 | 25.0.4.1+1-LTS |

## Frequently Asked Questions

**How can I reduce JVM warm-up time?**
Warm-up time that is dominated by JIT compilation can be addressed with ReadyNow, which reuses profiling data from previous runs, or with the new Compilation Streaming feature, which downloads pre-compiled code from Azul Optimizer Hub at startup and installs it directly instead of waiting on the compilation queue. Compilation Streaming requires Optimizer Hub 26.08.0 or later.

**What is the difference between Stream and Stable releases?**
Stream releases ship monthly and include new features. Stable releases keep features constant and only accept security and critical fixes. Azul Prime follows this model with monthly Stream Builds for development and evaluation, and Stable Builds — available to customers only — promoted twice a year.

**What is a Critical Security Patch Update (CSPU)?**
A CSPU delivers security and stability fixes between the quarterly update releases. It introduces no new features, no API changes, and no behavior changes beyond the security fixes. Azul issued its first CSPU for Azul Zing in August 2026.

**Which Java versions does Azul Prime 26.08 support?**
Azul Prime 26.08 supports OpenJDK 8, 11, 17, 21, and 25. Running an older major version doesn't exclude you from access to the current runtime optimizations.

**What does Multi-Tiering do?**
Multi-Tiering enables quick, low-level compilation early in a method's life and selective recompilation at higher optimization levels later, trading a small amount of peak performance for a shorter warm-up. It remains disabled by default; enable it with `-XX:+UseMultiTiering`.

## Conclusion

This new 26.08 Stable Line of Azul Zing Builds of OpenJDK introduces Compilation Streaming as a new way to shrink JVM warm-up time, ships Azul's first Critical Security Patch Update, and brings a long list of general, GC, and Falcon compiler improvements — all backed by the latest security fixes.
