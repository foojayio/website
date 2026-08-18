---
title: "Changes Included in the Stable release 23.08 of Azul Zulu Prime Builds of OpenJDK"
date: "2023-10-11T07:03:09+00:00"
lastmod: "2023-10-11T07:03:47+00:00"
description: "The new Stable Azul Zulu Prime Build of OpenJDK brings many improvements and fixes to bring a more performant and secure runtime to your environment."
canonical: "https://www.azul.com/blog/changes-included-in-the-stable-release-23-08-of-azul-zulu-prime-builds-of-openjdk/"
authors:
  - "frankdelporte"
  - "matt-van-order"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Java"
  - "Performance"
  - "Release Notes"
related_posts:
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "azul-enhances-readynow-to-solve-javas-warmup-problem-simplify-operations-and-optimize-cloud-costs"
  - "best-practice-comparative-evaluation-of-jdk-setups-azul-zulu-prime-vs-openjdk"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
frozen: false
---

*The latest Long Term Support (LTS) version of OpenJDK was released on September 19, 2023. Below is an overview of all the combined improvements in the latest Azul Prime Builds of OpenJDK, stable release, 23.08.01.0.*

[Azul Platform Prime](https://www.azul.com/products/prime/) is a modern, TCK-compliant Java platform based on OpenJDK. It provides consistently low response latency of your Java workloads, higher total throughput and carrying capacity, faster warmup, and infrastructure savings, achieved thanks to the [C4 pauseless garbage collector](https://www.azul.com/products/components/pgc/), [Falcon JIT compiler](https://www.azul.com/products/components/falcon-jit-compiler/), and other technologies created by Azul. Prime Builds are available in two versions, either for evaluation or production use:

* **Stream Builds** : Fast-moving monthly releases (end of the month) that include the latest features and changes in PSU releases. These are **free for development and evaluation**. The use in production requires an active subscription. Their version number is based on year and month. For instance, "23.07.0.0" is the July Stream Build of 2023.
* **Stable Builds** : Builds that incorporate only critical patch updates (CPUs), patch set updates (PSUs), and Azul Platform Prime critical fixes. Stable builds do not include new features or non-critical enhancements from stream builds. Stable builds are our primary vehicle for delivering time-sensitive bug fixes to customers and are **only available to Azul customers**. Their version number is based on the stream build they originated from. For instance, "22.02.501.0" is derived from the February stream build of 2022; but it already had multiple updates, as the 501 number indicates.

As stream builds happen in a fixed schedule, all changes are included in the [release notes](https://docs.azul.com/prime/release-notes). Twice a year (in February and August), a stream build becomes the new stable build, providing a new version with many more improvements. In this post, we want to give you an overview of all the combined improvements in the latest stable release, 23.08.01.0.
![](Azul-Prime-Stable-2308-1024x400.jpg)

## Changes Included in 23.08.1.0

As stable builds overlap, your system should be on the 23.02-stable line, and there are now four months of overlap with the 23.08-stable builds. Let's look at some of the most significant changes between 23.02 and 23.08 and the changes included in the new Stable Build 23.08.01.00 released on September 26, 2023.

### Security fixes

April and July 2023 CPU and PSU release security fixes.

### Changes in supported versions

JDK versions 13, 15, and 19 are no longer included in Prime stable releases. Only the JDK Long Term Support (LTS) versions 8, 11, and 17 continue to be included.

### New Supported Platforms

Oracle Linux (Centos 7.9) ARM is now supported since Azul Platform Prime 23.03.0.0.

### Optimizer Hub

The client for [Optimizer Hub](https://docs.azul.com/optimizer-hub/) has been upgraded to support the latest features of Optimizer Hub version 1.8. This upgrade aligns the client in Prime to support the components provided in Optimizer Hub (formerly Cloud Native Compiler). As [Cloud Native Compiler](https://www.azul.com/products/intelligence-cloud/cloud-native-compiler/) expands its scope to offer more functionality than just offloading compilations, it is time to rebrand the offering to reflect better what it does. Starting with release 1.8, we are using the following names:

* **Optimizer Hub (formerly Cloud Native Compiler):** The name of the overall component you install on your Kubernetes cluster.
  * **Cloud Native Compiler (formerly Compiler Service):** Server-side optimization solution that offloads JIT compilation to separate, dedicated service resources. Cloud Native Compiler provides more processing power to JIT compilation while freeing your client JVMs from the burden of doing JIT compilation locally.
  * **ReadyNow Orchestrator (formerly Profile Log Service):** Records and serves ReadyNow profiles. This greatly simplifies the operational use of [ReadyNow](https://www.azul.com/products/components/readynow) and removes the need to configure any local storage for writing the profile. ReadyNow Orchestrator can record multiple profile candidates from multiple JVMs and promote the best-recorded profile.

In Optimizer Hub 1.8, all major artifacts and command line switches use the updated branding. This includes, but is not limited to:

* Command-line JVM options to configure Cloud Native Compiler and ReadyNow Orchestrator. See [Command Line Options](http://doc-builder.azulsystems.com:8081/prime/release/23.08/Command-Line-Options.html#optimizer-hub-options).
* Helm repository locations, names, and parameter names: [github.com/AzulSystems/opthub-helm-charts](https://github.com/AzulSystems/opthub-helm-charts).
* REST API URLs.

All documentation of Optimizer Hub can be found on [docs.azul.com/optimizer-hub](https://docs.azul.com/optimizer-hub).

### Native Memory Tracking (NMT)

It is no longer necessary to LD_PRELOAD the [libnmt_hooks.so](http://libnmt_hooks.so) library to use extended Native Memory Tracking (NMT). The [libnmt_hooks.so](http://libnmt_hooks.so) library is now linked by default.

### Falcon Compiler

Azul Platform Prime 23.05.0.0 brought several performance optimizations, including many intrinsic functions implemented in the Falcon compiler.

Compilation ranks by priority, which allows the JVM to assign compilation ranks to methods, has been introduced to Azul Platform Prime 23.08.0.0. This allows the Falcon compiler to assign ranks (**hot** , **warm** , or **cold** ) to methods to prioritize system resources to methods depending on their hotness. For more information on compilation ranks, see [Analyzing and Tuning Warmup](http://doc-builder.azulsystems.com:8081/prime/release/23.08/analyzing-tuning-warmup.html#_setting_falcon_priorities). For newly added options, see [Command Line Options](http://doc-builder.azulsystems.com:8081/prime/release/23.08/Command-Line-Options.html#_compilation_ranks).

### Java Flight Recorder

Using Java Flight Recorder, you can now see exact JIT names for each stacktrace frame in [Azul Mission Control](https://www.azul.com/products/components/azul-mission-control/) in the Method Profiling tab. This uses the option `JFRDistinguishJITTypes`, which is set to `true` by default, and shows either C1, C2, or Falcon for each stacktrace frame. With `JFRDistinguishJITTypes` set to `false`, it shows *JIT compiled*.

### Per Thread CPU Utilization in GC Log

The per-thread CPU (Central Processing Unit) utilization tracking feature, available from Azul Platform Prime 23.06, allows the collection and charting of per-thread CPU core usage statistics. This lets you view the CPU usage of an application and the JVM internal threads of the process over time. JVM internal threads are grouped into known categories (for example, compiler or GC group) to make analysis of threads simpler. There is also an option to include CPU usage from other processes on the system.

Per-thread CPU utilization is turned on using the option \`-XX:+PrintCPUUtilization\`, which is disabled by default due to the potential overhead from data collection.

### Changes in Command Line Options

* The Command Line Option `GPGCUseAllocationPacing` has been disabled by default.
* The Command Line Option `CNCForceLocalCompiler` has been deprecated and replaced with the new option `CNCEnableRemoteCompiler`.
* The command line option, `AllocCodeCacheInLower2G`, is now supported on the aarch64 system architecture, which is set to `true` by default. This option allocates code cache and related data structures at virtual address within 2 GB. To allow allocation to higher memory addresses, use `-XX:-AllocCodeCacheinLower2G`.
* A new command line option, `GPGCCommitInitialHeapLazily`, has been introduced, which is set to `false` by default. When enabled, this option prevents the whole of the initial heap size, `InitialHeapSize` or `-Xms`, from being committed from the OS upfront.With this option enabled, use the option `GPGCLazyInitialHeapCommitPercent` to specify how much of Xms shall be committed from the OS upfront, at startup. The default value for `GPGCLazyInitialHeapCommitPercent` is `50`. The remainder gets committed based on regular elastic heap heuristics.
* The command line option `InitialHeapSize` is now incorporated in Azul Platform Prime in order to keep compatibility with OpenJDK. `InitialHeapSize` can be used instead of `-Xms<size>` on the command line.  
  TIP: The command line argument `MaxHeapSize` can also be used instead of `-Xmx<size>.`
* The command line option `PreferContainerQuotaForVMInternalCPUCount` has been set to `true` by default to make calculations of internal thread counts, as well as budgeting options, more clear in container environments. In container environments where both CPU shares and CPU quota are specified, such as with Kubernetes where these are commonly specified, the VM now uses quota to calculate compiler and GC thread counts. Prior to Azul Platform Prime 23.08, it was using half of the quota for the calculation.
* Some Falcon CPU Budgeting options have been renamed according to the following table:

|                Changed From                |               Changed To               |
|--------------------------------------------|----------------------------------------|
| CompilerTier2BudgetingThreadsPercent       | CompilerTier2BudgetingCPUPercent       |
| CompilerTier2BudgetingWarmupThreadsPercent | CompilerTier2BudgetingWarmupCPUPercent |
| CompilerTier2BudgetMaxMs                   | CompilerTier2BudgetWindowDurationMs    |

For more information on Falcon CPU Budgeting options, see [Command Line Options, CPU Budgeting Options](https://docs.azul.com/prime/Command-Line-Options#cpu-budgeting-options)

The command line option `UseTrueObjectsForUnsafe` has been set to `true` by default. This option forces unsafe objects to be returned in their true object form instead of the equivalent java class object. For example, with `UseTrueObjectsForUnsafe` disabled, java.lang.Class can be returned instead of the true klassOop.

A new option, `C2CompileThreshold`, has been added. This option allows the C2 compile threshold to be specified for individual methods. This option was introduced because some methods that are rarely called are still important and need to undergo regular optimization. This is set using `-XX:CompileCommand` in the following way:

`-XX:CompileCommand="option,<Class>::<method>,C2CompileThreshold=<threshold>"`

The command line option `-XX:CompileCommand` has been updated to use `FalconCompileThreshold`.

This option is used in the following way:

`-XX:CompileCommand="option,<Class>::<method>,FalconCompileThreshold=<threshold value>"`

The maximum supported code cache size has been increased to 1758 MB when `AllocCodeCacheInLower2G` is disabled using `-XX:-AllocCodeCacheInLower2G`.

Due to rebranding of Cloud Native Compiler to Optimizer Hub, some options have been renamed according to the following table:

|  Changed From   |     Changed To     |                                                               Note                                                               |
|-----------------|--------------------|----------------------------------------------------------------------------------------------------------------------------------|
| CNCHost         | OptHubHost         |                                                                                                                                  |
| CNCSSLRootsPath | OptHubSSLRootsPath |                                                                                                                                  |
| CNCInsecure     | OptHubUseSSL       | OptHubUseSSL is the inverse on CNCInsecure, so the default value has changed from `false` to `true` i.e. SSL is used by default. |

### Deprecation of ZVTools

ZVision and ZVRobot components have been deprecated and are no longer actively developed. While we still support these components, we encourage users to switch to [Java Flight Recorder](http://doc-builder.azulsystems.com:8081/prime/release/23.08/release-notes#https://docs.azul.com/prime/Java-Flight-Recorder), as ZVision and ZVRobot are planned for End-of-life with Azul Platform Prime 24.02.0.0.

## Resolved Issues

| Issue ID  |                                                        Description                                                        |
|-----------|---------------------------------------------------------------------------------------------------------------------------|
| ZVM-26650 | Transform head of _freeThreads to a tagged reference to avoid ABA problems                                                |
| ZVM-26648 | Missing tag update in HeapRefBufferList::grab()                                                                           |
| ZVM-26387 | \[Alpine\] Failed to bundle core from alpine container                                                                    |
| ZVM-26245 | jlink on Prime converts library symlinks to files and increase the total size by 87MB                                     |
| ZVM-25950 | Backport JDK-7059899 Stack overflows in Java code cause 64-bit JVMs to exit due to SIGSEGV                                |
| ZVM-27634 | Unify Prime's "java.vendor" with Zulu                                                                                     |
| ZVM-27514 | High JFRCheckpoint pauses seen on Prime                                                                                   |
| ZVM-27506 | Turn on JFRDistinguishJITTypes flag by default                                                                            |
| ZVM-27424 | Prime 11+ doesn't throw IncompatibleClassChangeError in instanceKlass::method_at_itable                                   |
| ZVM-27785 | Fix segmentation fault on StubRoutines::stringIndexOf                                                                     |
| ZVM-27675 | Prohibit inlining for methods with invalid method ID                                                                      |
| ZVM-27624 | Disable RSS workaround only once use of large pages are confirmed                                                         |
| ZVM-27388 | objSizes.jar application crashes with "assert(m-\>is_abstract()) failed: should be public and abstract" in fastdebug mode |
| ZVM-27549 | Avoid native method calls from VM.java class                                                                              |
| ZVM-27897 | Hadoop fails with Prime when -XX:+UseAES is used                                                                          |
| ZVM-27098 | Incompatibility with Apache Flink with RocksDB                                                                            |

## Conclusion

The new Stable Azul Zulu Prime Build of OpenJDK brings many improvements and fixes to bring a more performant and secure runtime to your environment.
