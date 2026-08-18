---
title: "Changes Included in Release 24.08 of Azul Zing Builds of OpenJDK"
date: "2024-10-18T13:53:56+00:00"
lastmod: "2024-10-18T13:53:57+00:00"
description: "Azul Zing Builds of OpenJDK, the optimized Java runtime within Azul Platform Prime, has reached the release of the 24.08 Stable Release line. Zing builds - by Frank Delporte"
canonical: "https://www.azul.com/blog/changes-included-in-release-24-08-of-azul-zing-builds-of-openjdk/"
authors:
  - "frankdelporte"
  - "matt-van-order"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Cloud"
  - "Java"
  - "Release Notes"
related_posts:
  - "how-is-leyden-improving-java-performance-part-3-of-3"
  - "how-is-leyden-improving-java-performance-part-2-of-3"
  - "foojay-podcast-92"
  - "devbcn-2026"
frozen: false
---

Azul Zing Builds of OpenJDK, the optimized Java runtime within [Azul Platform Prime](https://www.azul.com/products/prime), has reached the release of the 24.08 Stable Release line. Zing builds are available in two versions:

* **Stream Builds** : Fast-moving monthly releases (end of the month) that include the latest features and changes in PSU releases. These are **free for development and evaluation**. Using Stream Builds in production requires an active subscription. Their version number is based on year and month. For instance, "24.07.0.0" is the July Stream Build of 2024.
* **Stable Builds** : Builds that incorporate only CPUs, PSUs, and Azul Zing critical fixes and do not uptake new features and non-critical enhancements from Stream Builds. Stable Builds are our primary vehicle for delivering time-sensitive bug fixes to customers and are **only available to Azul customers**. Their version number is based on the Stream Build they originated from. For instance, "23.08.500.0" was derived from the August Stream Build of 2023 but already had multiple updates, as the 500 number indicates.

As stream builds happen in a fixed schedule, all changes are included in the [release notes](https://docs.azul.com/prime/release-notes). Twice a year (in February and August), a Stream build becomes the new Stable build, providing a new version with many more improvements. In this post, we want to give you an overview of all the combined improvements in the 24.08 Stable Line.
![](Azul-Prime-Stable-2308-1024x400.jpg)

## Changes Included in the 24.08 Stable Line

As Stable Builds overlap, your system should be on the 24.02 Stable line, and you have a window of four months to test and migrate to the 24.08 Stable line. Let's look at some of the most significant changes between the Stream Build releases 24.02 and 24.08. Version 24.08.0.0 is the branching point for the new Stable Build line, and includes all the following changes compared to the previous [Stable Builds based on 24.02](https://www.azul.com/blog/changes-included-in-release-24-02-of-azul-zing-builds-of-openjdk/).

### Security fixes

This version includes the April and July 2024 CPU and PSU release security fixes, including CPU and PSU fixes for Azul Zing Builds of OpenJDK 21.

### General Improvements

* A more efficient way was implemented to encode deopt bundles, improving system performance.
* Implementation of an intrinsification of the method `java.lang.reflect.Array.get`, leading to a significant performance improvement in some cases.
* The logic around InlineTree has been greatly improved. This change allows the decisions reached by inlining to be reconstructed on request instead of running through the tree with each query, sometimes leading to bloated recursive inlinings.
* Zing now handles requests for PrintJNI without safepoint pause, allowing PrintJNI to run concurrently with your VM process.

### Optimizer Hub

* To establish a better client/server relationship between Zing and Optimizer Hub, Zing now sends its version to Optimizer Hub, making the current version of Zing available and viewable in Optimizer Hub.
* The command line option `ProfileLogName` has been deprecated and replaced with `ProfileName`. `ProfileName` supports all existing macros available for `ProfileLogName`. It is still possible to use `ProfileLogName`, however, we recommend that you update your configuration to guarantee that you have access to all of the latest features implemented in `ProfileName`.Note that specifying ProfileName does not automatically enable ReadyNow Orchestrator processing of ReadyNow profiles. You also have to use the flag `EnableRNO`.

### Falcon Compiler

* Prime JIT compilation logs (LogCompilation) are now fully supported for JITWatch for Zing.
* Zing's crash handler has received a significant improvement, which allows it to properly generate diagnostic data when Falcon threads reach stack memory failure and OOM (Out Of Memory) errors.
* The Falcon compiler has a new feature called Multi-Tiering. This allows Falcon to schedule methods for compilation under different optimization levels based on method hotness, leading to significantly better performance during application warmup. Multi-Tiering assigns hot and active methods to final-tier compilation and cold or inactive methods to mid-tier compilation. Final-tier uses the default Falcon optimization level (usually Falcon optimization level 2) while Mid-tier uses Falcon optimization level 0. You can enable Multi-Tiering using the command line option `-XX:+EnableMultiTiering`.For more information on Multi-Tiering, see [Analyzing and Tuning Warm-Up, Using Multiple Compiler Tiers](http://doc-builder.azulsystems.com:8081/prime/release/24.08/analyzing-tuning-warmup.html#multi-tiering)

### Memory Size Improvements

* On current Intel server CPUs (Ice Lake or newer), the supported heap size without ZST is increased from 2800 GB to 14000 GB.
* For Arm64 server CPUs, a maximum heap size of 5600 GB is now supported.

### ReadyNow

You can now apply ReadyNow transformations at runtime. This is done using the newly implemented command line options `-XX:ApplyReadyNowTransformations` or `-XX:ApplyReadyNowTransformationsFile`. You can specify which transformations are used on which generation of your ReadyNow profiles. A transformation profile can be stored on your machine in YAML format and called using `-XX:ApplyReadyNowTransformationsFile=//path/to/file.yaml`. Or you can apply your transformation options directly in the parameters on the command line, using `-XX:ApplyReadyNowTransformations="\{transformations\:\[\{data: 0\}\]\}"`.  

An offline tool is available for applying transformations to a local ReadyNow file. Please contact Azul Support if you want to experiment with this feature.

### GC Log Analyzer

* GC Log Analyzer's summary page now includes the ID of the current run from Ready Now Orchestrator, listed as "Current VM ID".
* GC Log Analyzer's info page now includes the container OS, along with the node OS.

### Extra Methods in Zing MXBeans

Several methods have been added to Zing MXBean extensions, which can request several metrics from a running JVM. The following methods have been added to Zing MXBeans:

|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| MXBean                      | Method                                                                                                                                                                                                                                                               |
| **CompilationMXBean**       | getTotalOutstandingCompiles() getTotalPerformedTier1Compiles() getTotalPerformedTier2Compiles() This can return the total number of enqueued and in-progress compilations, and can return the total number of tier 1 and tier 2 compilations at the time of request. |
| **PersistentProfileMXBean** | getVmUnmatchedClassRate() getProfileClassMatchRate() This can return the ratio of matched or unmatched classes to the number of classes loaded in the VM at the time of request.                                                                                     |

You can find a general overview of Zing MXBeans in the [Zing MXBeans documentation](https://docs.azul.com/prime/MXBeans), a complete description of all Zing MXBeans methods in the [Zing MXBeans API documentation](https://docs.azul.com/prime/ZingMXBeans_javadoc/index.html), or in the Javadocs included in the Zing documentation bundle found on the [Zing customer downloads page](https://www.azul.com/products/prime/customer-downloads/).

### Changes in Command Line Options

* With the new `MallocArenaMax` option, you can define the maximum amount of memory pools available for glibc. The default value is `0`.
* `UseDefensiveHeapShrinking` is now disabled by default in cgroups where memory limiting is set. You can disable this option manually by using `-XX:-UseDefensiveHeapShrinking`. For more information about defensive heap shrinking, see [Command Line Options, Defensive Heap Shrinking](https://docs.azul.com/prime/Command-Line-Options#defensive-heap-shrinking).
* The minimum value supported for `Xms` (initial heap size) was drastically lowered from 512 MB to 128 MB. Previously, the minimum supported `Xms` was 512 MB. The minimum supported `Xmx` (maximum heap size) remains unchanged at 512 MB. This change reduces memory consumption from small utility processes that don't require a high amount of memory.  
  Note that when you set `Xms` and `Xmx` to the same value and set `Xms` somewhere between 128 MB and 512 MB, both values are rounded up to 512 MB to satisfy the minimum allowable `Xmx`.
* The default value of `-XX:ProfileStartupLimitInSeconds` has changed from `60` to `0`. This follows from a previous change where `0` was changed from "infinite" to actually 0 seconds. For more information on `ProfileStartupLimitInSeconds`, see [Command Line Options, Advanced Miscellaneous Options](https://docs.azul.com/prime/Command-Line-Options#advanced-miscellaneous-options).
* Some behavioral changes are implemented in the command line option `VMFootprintLevel`. In order to reduce memory footprint, malloc arenas now use half the number of CPU cores when setting a non-default value of `VMFootprintLevel`; `L`, `M`, or `S`.
* A new command line option `-XX:ThpDisable` can be used to disable Transparent Huge Pages (THP) in the entire JVM process, even when system THP settings are enabled. When `-XX:+ThpDisable` is set, THP is turned off for the process, overriding the system default. However, the Java heap committed initially can still use huge pages if shared memory THP is enabled.
* Thread-local backoff for secondary_super_cache updates has been ported from OpenJDK, based on [JDK-8316180](https://bugs.openjdk.org/browse/JDK-8316180%5BJDK-8316180%5D) and is disabled by default. To enable this feature, use the option `-XX:SecondarySuperMissBackoff=1000`.
* The new command line option `OptimizeIdentityHashForDistribution` enables an alternate implementation for *System.identityHashCode()* which provides better distribution of objects at the cost of making the identity hash calculation itself slower. This option is disabled by default and can be enabled using `-XX:+OptimizeIdentityHashForDistribution`.
* The MXBean PersistentProfileMXBean has been extended with `getReadyNowTier1CompilesRate()` and `getReadyNowTier2CompilesRate()`. These methods allow you to see what percentage of compiles are happening in ReadyNow, when compared to all compiles including non-ReadyNow.
* The command line option `ProfileLogName` has been deprecated and replaced with `ProfileName`. `ProfileName` supports all existing macros available for `ProfileLogName`. It is still possible to use `ProfileLogName`, however, we recommend that you update your configuration in order to guarantee that you have access to all of the latest features implemented in `ProfileName`.Note that using `ProfileName` overrides `ProfileLogName`, `ProfileLogIn`, and `ProfileLogOut`. Also note that specifying `ProfileName` does not automatically enable ReadyNow Orchestrator processing of ReadyNow profiles. You also have to use the flag `EnableRNO`.

## Conclusion

This new 24.08 Stable Line of Azul Zing Builds of OpenJDK includes many changes and improvements and contains all the latest security fixes.
