---
title: "Embracing JVM unified logging (JEP-158 / JEP-271)"
date: "2021-03-10T18:20:13+00:00"
lastmod: "2021-08-23T12:25:52+00:00"
description: "In the previous post, I briefly introduced unified logging and a simple GC configuration. For the savvy GC tuners, there are many options."
authors:
  - "brice-dutheil"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
related_posts:
  - "boxlang-ai-deep-dive-part-6-of-7-memory-systems-rag-building-ai-that-remembers"
  - "fantastic-jvms-and-where-to-find-them"
  - "aggregation-optimization-in-mongodb-data-duplication-to-improve-read-performance-part-4"
  - "aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3"
frozen: false
---

In the previous [blog post](https://foojay.io/today/introduction-to-jvm-unified-logging-jep-158-jep-271/), I briefly introduced unified logging and a simple GC configuration. However for the savvy GC tuners, there are many more options. And there are other logging options that transitionned to unified logging infrastructure as well.

I wasn't satisfied with the official documentation and other blog posts as they usually present only a fragmented picture of the previous options. This led me to dig in.

## Migrating the GC logging (continued)

|      |                                |
|------|--------------------------------|
| Note | These log are based on JDK11u. |

### Exhaustive translation table

I extracted the following table from the actual patches that implemented JEP-271, see [JDK-8059805](https://bugs.openjdk.java.net/browse/JDK-8059805), [JDK-8145092](https://bugs.openjdk.java.net/browse/JDK-8145092), and in particular the [related diff](https://hg.openjdk.java.net/jdk9/jdk9/hotspot/rev/f944761a3ce3) [(on github)](https://github.com/AdoptOpenJDK/openjdk-jdk11u/commit/d724e8a3489f8ebb57c7bbf82784a2b2d537fdc8).

In a lesser way I used the official [`java` documentation](https://docs.oracle.com/javase/9/tools/java.htm#JSWOR-GUID-BE93ABDC-999C-4CB5-A88B-1994AAAC74D5), which I found somewhat lacking in this regard.

| Old GC log flags usually set with `-XX:+…​` | `Equivalent` tags with log level                                                                           | Definition of the the old flag                                                                                                                                       |
|:--------------------------------------------|:-----------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PrintGC -Xloggc:`                          | `gc`                                                                                                       | Print message at garbage collection                                                                                                                                  |
| `PrintGCDetails -Xloggc:`                   | `gc*`                                                                                                      | Print more details at garbage collection                                                                                                                             |
| `-verbose:gc`                               | `gc=trace` `gc+heap=trace` `gc+heap+exit=trace` `gc+metaspace=trace` `gc+sweep=debug` `gc+heap+ergo=debug` | Verbose GC                                                                                                                                                           |
| `PrintGCCause`                              | `GC` cause is now always logged                                                                            | Include GC cause in GC logging                                                                                                                                       |
| `PrintGCID`                                 | `GC` ID is now always logged                                                                               | Print an identifier for each garbage collection                                                                                                                      |
| `PrintGCApplicationStoppedTime`             | `safepoint`                                                                                                | Print the time the application has been stopped                                                                                                                      |
| `PrintGCApplicationConcurrentTime`          | `safepoint`                                                                                                | Print the time the application has been running                                                                                                                      |
| `PrintTenuringDistribution`                 | `gc+age*=trace`                                                                                            | Print tenuring age information                                                                                                                                       |
| `PrintAdaptiveSizePolicy`                   | `gc+ergo*=trace`                                                                                           | Print information about AdaptiveSizePolicy                                                                                                                           |
| `PrintHeapAtGC`                             | `gc+heap=debug`                                                                                            | Print heap layout before and after each GC                                                                                                                           |
| `PrintHeapAtGCExtended`                     | `gc+heap=trace`                                                                                            | Print extended information about the layout of the heap when -XX:+PrintHeapAtGC is set                                                                               |
| `PrintClassHistogramBeforeFullGC`           | `classhisto*=trace`                                                                                        | Print a class histogram before any major stop-world GC                                                                                                               |
| `PrintClassHistogramAfterFullGC`            | `classhisto*=trace`                                                                                        | Print a class histogram after any major stop-world GC                                                                                                                |
| `PrintStringDeduplicationStatistics`        | `gc+stringdedup*=debug`                                                                                    | Print string deduplication statistics                                                                                                                                |
| `PrintJNIGCStalls`                          | `gc+jni=debug`                                                                                             | Print diagnostic message when GC is stalled by JNI critical section                                                                                                  |
| `PrintReferenceGC`                          | `gc+ref=debug`                                                                                             | Print times spent handling reference objects during GC                                                                                                               |
| `PrintGCTaskTimeStamps`                     | `task*=debug`                                                                                              | Print timestamps for individual gc worker thread tasks                                                                                                               |
| `PrintTaskQueue`                            | `gc+task+stats=trace`                                                                                      | Print taskqueue statistics for parallel collectors                                                                                                                   |
| `PrintPLAB`                                 | `gc+plab=trace`                                                                                            | Print (survivor space) promotion LAB's sizing decisions                                                                                                              |
| `PrintOldPLAB`                              | `gc+plab=trace`                                                                                            | Print (old gen) promotion LAB's sizing decisions                                                                                                                     |
| `PrintPromotionFailure`                     | `gc+promotion=debug`                                                                                       | Print additional diagnostic information following promotion failure                                                                                                  |
| `PrintTLAB`                                 | `gc+tlab=trace`                                                                                            | Print various TLAB related information (augmented with `-XX:+TLABStats`)                                                                                             |
| `PrintTerminationStats`                     | `gc+task+stats=debug`                                                                                      | Print termination statistics for parallel collectors                                                                                                                 |
| `G1PrintHeapRegions`                        | `gc+region=trace`                                                                                          | If set G1 will print information on which regions are being allocated and which are reclaimed                                                                        |
| `G1PrintRegionsLivenessInfo`                | `gc+liveness=trace`                                                                                        | Prints the liveness information for all regions in the heap at the end of a marking cycle                                                                            |
| `G1SummarizeConcMark`                       | `gc+marking=trace`                                                                                         | Summarize concurrent mark info                                                                                                                                       |
| `G1SummarizeRSets`                          | `gc+remset*=trace`                                                                                         | Summarize remembered set processing info                                                                                                                             |
| `G1TraceConcRefinement`                     | `gc+refine=debug`                                                                                          | Trace G1 concurrent refinement                                                                                                                                       |
| `G1TraceEagerReclaimHumongousObjects`       | `gc+humongous=debug`                                                                                       | Print some information about large object liveness at every young GC                                                                                                 |
| `G1TraceStringSymbolTableScrubbing`         | `gc+stringdedup=trace`                                                                                     | Trace information string and symbol table scrubbing                                                                                                                  |
| `PrintParallelOldGCPhaseTimes`              | `gc+phases=trace`                                                                                          | Print the time taken by each phase in ParallelOldGC                                                                                                                  |
| `CMSDumpAtPromotionFailure`                 | `gc+promotion=trace`                                                                                       | Dump useful information about the state of the CMS old generation upon a promotion failure (complemented by flags `CMSPrintChunksInDump` or `CMSPrintObjectsInDump`) |
| `CMSPrintEdenSurvivorChunks`                | `gc+heap=trace`                                                                                            | Print the eden and the survivor chunks used for the parallel initial mark or remark of the eden/survivor spaces                                                      |
| `PrintCMSInitiationStatistics`              | `gc=trace`                                                                                                 | Statistics for initiating a CMS collection                                                                                                                           |
| `PrintCMSStatistics`                        | `gc=debug` (`trace`) `gc+task=trace` `gc+survivor=trace` `log+sweep=debug` (`trace`)                       | Statistics for CMS (complemented by `CMSVerifyReturnedBytes`)                                                                                                        |
| `PrintFLSCensus`                            | `gc+freelist+census=debug`                                                                                 | Census for CMS' FreeListSpace                                                                                                                                        |
| `PrintFLSStatistics`                        | `gc+freelist+stats=debug` (`trace`) `gc+freelist*=debug` (`trace`)                                         | Statistics for CMS' FreeListSpace                                                                                                                                    |
| `TraceCMSState`                             | `gc+state=debug`                                                                                           | Trace the state of the CMS collection                                                                                                                                |
| `TraceSafepoint`                            | `safepoint=debug`                                                                                          | Trace application pauses due to VM operations in safepoints                                                                                                          |
| `TraceSafepointCleanupTime`                 | `safepoint+cleanup=info`                                                                                   | break down of clean up tasks performed during safepoint                                                                                                              |
| `TraceAdaptativeGCBoundary`                 | `heap+ergo=debug`                                                                                          | Trace young-old boundary moves                                                                                                                                       |
| `TraceDynamicGCThreads`                     | `gc+task=trace`                                                                                            | Trace the dynamic GC thread usage                                                                                                                                    |
| `TraceMetadataHumongousAllocation`          | `gc+metaspace+alloc=debug`                                                                                 | Trace humongous metadata allocations                                                                                                                                 |
| `VerifySilently`                            | `gc+verify=debug`                                                                                          | Do not print the verification progress                                                                                                                               |
[Table 1. Exhaustive GC logging option translation table (with some caveats)]

|                     |          |
|---------------------|----------|
| `PrintGCDateStamps` | `time`   |
| `PrintGCTimeStamps` | `uptime` |
[Table 2. old options are now decorators]

### Caveat when using this translation table

I noticed while analyzing GC logs with the above unified logging configuration that some logs I expected were missing, and while doing this translation table I identified the log statements I expected. They had a different *tag set*.

On the example of heap occupancy logs (IHOP), it was logged with `PrintAdaptiveSizePolicy` and now it's supposed to be logged as part of the GC ergonomics by setting `gc+ergo*` to `trace`. Looking at the code, I noticed the `ihop` tag is not always combined with `ergo`.

This tag is not the only one, in the [diff](https://github.com/AdoptOpenJDK/openjdk-jdk11u/commit/d724e8a3489f8ebb57c7bbf82784a2b2d537fdc8) I mentioned previously there's an interesting file that declares GC *log prefix* for a list of *tag-sets* . *The diff is huge and may take some time to load, search for the following file
[src/share/vm/logging/logPrefix.hpp](https://github.com/AdoptOpenJDK/openjdk-jdk11u/commit/d724e8a3489f8ebb57c7bbf82784a2b2d537fdc8#diff-c7fbf2952ef86b686c1849f6735041c9).*

Also, some logging tags are common to multiple GC. While there is nothing wrong in this, it's easy to update the logging configuration when changing the GC algorithm, e.g. using CMS then switch to G1GC. I would have preferred an additional tag for each GC algorithms (`g1` for g1GC, `cms` for Concurrent Mark and Sweep,`shenandoah` for Shenandoah, etc) that would have allowed to configure logging this way `-Xlog:zgc=info*`. Unfortunately this is only a dream at this time.

Moreover, JDK maintainers are improving the JVM sub-systems logging over time, possibly backporting improvements. This makes the tag selection hard to use properly and tedious to maintain. Very few will *grep* the JDK code base to track which tag they need to tune GCs which is already arcane enough.

The bottom line is that I believe that the configured/selected tags are too restrictive it might be counter-productive to get valuable information, and may even prevent to get log message from loggers that may be added in the future.

### Embracing unified logging for GC logs

These caveats led me to think that instead of trying to *mimic* old logging options, I should instead prefer to log more tags and simplify the overall logging configuration.

```
-Xlog:gc*=debug,gc+ergo*=trace,gc+age*=trace,safepoint*:file=/gclogs/%t-gc.log:uptime,tags,level:filecount=10,filesize=20M
```

Log config breakdown

```
-Xlog:
  gc*=debug,
  gc+ergo*=trace,
  gc+age*=trace,
  safepoint*
  :file=/gclogs/%t-gc.log:uptime,tags,level:filecount=10,filesize=20M
```

* Line 2: Logs everything under `gc` at `debug` level.
* Line 3: Specific *tagset* level configuration for ergonomics.
* Line 4: Specific *tagset* level configuration for tenuring distribution.

The above configuration is simpler at the expense of possibly larger file size. Also, using `gc*=debug` allows it to catch extra tags, and possibly new `gc` related tags that show up. In my opinion this configuration does not have any caveats.

And as mentioned I benefited from other tags under `gc` that I wasn't even looking at before because I simply didn't think to enable the logging option, like `PrintJNIGCStalls` in pre-jdk9 `jni` tag in unified logging or dreaded humongous allocations in G1GC via the `humongous` tag. In short this simpler configuration enabled more logging, which means GC analysis tool can spot other useful information.

GC tagsets with `gc*=debug` configuration

* `gc,age`
* `gc,alloc,region`
* `gc,cpu`
* `gc,ergo`
* `gc,ergo,cset`
* `gc,ergo,ihop`
* `gc,ergo,refine`
* `gc,heap`
* `gc,humongous`
* `gc,ihop`
* `gc,jni`
* `gc,marking`
* `gc,metaspace`
* `gc,mmu`
* `gc,phases`
* `gc,phases,ref`
* `gc,phases,start`
* `gc,plab`
* `gc,ref`
* `gc,ref,start`
* `gc,refine`
* `gc,remset,tracking`
* `gc,start`
* `gc,stats`
* `gc,stringdedup`
* `gc,stringtable`
* `gc,task`
* `gc,task,stats`
* `gc,tlab`

Of course if one want to focus on a certain part, it is alwasy possible to specify explicitly the tags with a higher logging level.

#### Try GC logging configurations

Unlike `os` and `container` logs, GC happens almost continuously. This opens the opportunity to try log configurations at runtime using `jcmd`. In the example below I wanted to monitor more thoroughly G1GC regions:

```bash
jcmd $(pidof java) \
  VM.log \
    what="gc*=debug,gc+ergo*=trace,gc+age*=trace,gc+region=trace,gc+liveness=trace,safepoint*" \
    decorators=time,tags,level \
    output="file=/var/log/%t-gc-region-tracing.log" \
    output_options="filecount=10,filesize=20M"
```

### Migrating the Trace\* flags

One thing I used to log is about classes, particularly during development. Especially loading and unloading. I used this a lot while debugging some aspects of Mockito and some application servers back in the days.

Some of the `Trace*` flags are still present, even in JDK 14, although they output a warning encouraging to switch to unified logging.

|                             |                                           |
|-----------------------------|-------------------------------------------|
| `TraceClassInitialization`  | `class+init=info`                         |
| `TraceClassLoading`         | `class+load=info` (`debug)`               |
| `TraceClassLoadingPreorder` | `class+preorder=debug`                    |
| `TraceClassUnloading`       | `class+unload=info` (`trace)`             |
| `TraceClassPaths`           | `class+path=info`                         |
| `TraceClassResolution`      | `class+resolve=debug`                     |
| `TraceLoaderConstraints`    | `class+loader+constraints=info`           |
| `TraceClassLoaderData`      | `class+loader+data=debug` (`trace)`       |
| `TraceRedefineClasses`      | `redefine+class*=info` (`debug`, `trace)` |
| `TraceMonitorInflation`     | `monitorinflation=debug`                  |
| `TraceBiasedLocking`        | `biasedlocking=info` (`trace)`            |
| `TraceExceptions`           | `exceptions=info`                         |
| `TraceJVMTIObjectTagging`   | `jvmti+objecttagging=debug`               |
[Table 3. Other tracing option translation]

Tracing flags declaration in the JVM code base

[src/hotspot/share/runtime/arguments.cpp](https://github.com/openjdk/jdk11u/blob/a4f2cc65079328cb428cf0317df39af93472ab1e/src/hotspot/share/runtime/arguments.cpp#L596-L630)

```cpp
// NOTE: A compatibility request will be necessary for each alias to be removed.
static AliasedLoggingFlag const aliased_logging_flags[] = {
  { "PrintCompressedOopsMode",   LogLevel::Info,  true,  LOG_TAGS(gc, heap, coops) },
  { "PrintSharedSpaces",         LogLevel::Info,  true,  LOG_TAGS(cds) },
  { "TraceBiasedLocking",        LogLevel::Info,  true,  LOG_TAGS(biasedlocking) },
  { "TraceClassLoading",         LogLevel::Info,  true,  LOG_TAGS(class, load) },
  { "TraceClassLoadingPreorder", LogLevel::Debug, true,  LOG_TAGS(class, preorder) },
  { "TraceClassPaths",           LogLevel::Info,  true,  LOG_TAGS(class, path) },
  { "TraceClassResolution",      LogLevel::Debug, true,  LOG_TAGS(class, resolve) },
  { "TraceClassUnloading",       LogLevel::Info,  true,  LOG_TAGS(class, unload) },
  { "TraceExceptions",           LogLevel::Info,  true,  LOG_TAGS(exceptions) },
  { "TraceLoaderConstraints",    LogLevel::Info,  true,  LOG_TAGS(class, loader, constraints) },
  { "TraceMonitorInflation",     LogLevel::Debug, true,  LOG_TAGS(monitorinflation) },
  { "TraceSafepointCleanupTime", LogLevel::Info,  true,  LOG_TAGS(safepoint, cleanup) },
  { "TraceJVMTIObjectTagging",   LogLevel::Debug, true,  LOG_TAGS(jvmti, objecttagging) },
  { "TraceRedefineClasses",      LogLevel::Info,  false, LOG_TAGS(redefine, class) },
  { NULL,                        LogLevel::Off,   false, LOG_TAGS(_NO_TAG) }
};

#ifndef PRODUCT
// These options are removed in jdk9. Remove this code for jdk10.
static AliasedFlag const removed_develop_logging_flags[] = {
  { "TraceClassInitialization",   "-Xlog:class+init" },
  { "TraceClassLoaderData",       "-Xlog:class+loader+data" },
  { "TraceDefaultMethods",        "-Xlog:defaultmethods=debug" },
  { "TraceItables",               "-Xlog:itables=debug" },
  { "TraceMonitorMismatch",       "-Xlog:monitormismatch=info" },
  { "TraceSafepoint",             "-Xlog:safepoint=debug" },
  { "TraceStartupTime",           "-Xlog:startuptime" },
  { "TraceVMOperation",           "-Xlog:vmoperation=debug" },
  { "PrintVtables",               "-Xlog:vtables=debug" },
  { "VerboseVerification",        "-Xlog:verification" },
  { NULL, NULL }
};
#endif //PRODUCT
```

## End words

In this article I provided an exhaustive translation table for GC logging flags and Trace\* logging flags. However after using these log configuration the surfacing idea to bring home is there is a better approach than using a one-to-one mapping from flags to tags, instead favoring tag with wildcard resulting in simpler and future-proof log configuration.
