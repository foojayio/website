---
title: "Using Java Flight Recorder and Mission Control (Part 2)"
slug: "using-java-flight-recorder-and-mission-control-part-2"
date: "2020-11-05T16:50:59+00:00"
lastmod: "2025-02-17T09:27:47+00:00"
description: "Continuing from part 1, to exploit the recording by analyzing it, we have a tool named jfr that ships with the JDK. Let's get started!"
authors:
  - "brice-dutheil"
image: "chunk.png"
categories:
  - "JDK Flight Recorder"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Now, [continuing from part 1](https://foojay.io/blog/using-java-flight-recorder-and-mission-control-part-1/), to exploit the recording by analyzing it, we have a tool named `jfr` that ships with the JDK. On Linux the *alternative* jdk management may not be aware of `jfr`, which means you may need to use the full path to this executable.

The first interesting thing to do is to get an overview of the recording, the `summary` sub-command displays an histogram of the events, shown below.

```
$ jfr summary /tmp/app-profile-2020-03-26T16-57-14.jfr

 Version: 2.0
 Chunks: 1
 Start: 2020-03-26 16:57:14 (UTC)
 Duration: 303 s

 Event Type                            Count  Size (bytes)
===========================================================
 jdk.ThreadPark                       130278       5868710
 jdk.SocketRead                        38804       1934842
 jdk.JavaMonitorWait                   38722       1378513
 jdk.NativeMethodSample                14702        263403
 jdk.ThreadCPULoad                     11821        271763
 jdk.ExecutionSample                    3010         54177
 jdk.ModuleExport                       2505         40187
 jdk.ClassLoaderStatistics              2344         72694
 jdk.ThreadAllocationStatistics          878         16962
 jdk.ModuleRequire                       754         11964
 jdk.BooleanFlag                         648         23106
 jdk.CPULoad                             298          7450
 jdk.JavaThreadStatistics                298          6258
 jdk.ClassLoadingStatistics              298          5066
 jdk.CompilerStatistics                  298         11324
 jdk.ExceptionStatistics                 298          6258
 jdk.ActiveSetting                       285         10497
 jdk.BiasedLockRevocation                275          7831
...
 jdk.GCPhasePauseLevel1                   20           965
 jdk.CheckPoint                           17       1631868
 jdk.ExecuteVMOperation                   15           391
 jdk.DoubleFlag                           13           618
 jdk.BiasedLockClassRevocation            10           275
 jdk.GCHeapSummary                        10           475
 jdk.MetaspaceSummary                     10           580
 jdk.G1HeapSummary                        10           300
 jdk.OldObjectSample                      10           367
...
 jdk.BiasedLockSelfRevocation              2            45
 jdk.PhysicalMemory                        2            46
 jdk.ThreadDump                            2       1389568
 jdk.CodeSweeperStatistics                 2            64
 jdk.GCConfiguration                       2            60
 jdk.ThreadEnd                             1            17
 jdk.Metadata                              1         74738
 jdk.JavaMonitorEnter                      1            33
 jdk.SafepointBegin                        1            24
 jdk.JVMInformation                        1           898
 jdk.OSInformation                         1           367
 jdk.VirtualizationInformation             1            33
 jdk.CPUInformation                        1          1432
 jdk.CPUTimeStampCounter                   1            25
 jdk.CompilerConfiguration                 1            15
 jdk.CodeCacheConfiguration                1            51
...
 jdk.X509Certificate                       0             0
 jdk.TLSHandshake                          0             0
```

But other interesting things could be done using this tool. The `print` sub-command can extract these events, in XML or in JSON. From there it's possible to perform other type of aggregation using other tools.

```
$ jfr print \
  --json \
  --events jdk.ThreadPark \
  /gclogs/startup.jfr \
  | jq '.recording.events[] | .values.duration'
```

It's also possible to assemble `jfr` files or break them in smaller parts. As a side note, the files in the *repository* can be exploited this way. Keep in mind these files may be removed as soon as they are expired or as soon as every recording stops.

A summary on a chunk in the repository is shown below.

```
$ jfr summary /tmp/jfr-repo/2020_06_26_16_03_41_6/2020_06_26_16_04_12.jfr
Version: 2.0
Chunks: 1
Start: 2020-06-26 16:04:12 (UTC)
Duration: 35 s

 Event Type                            Count  Size (bytes)
===========================================================
 jdk.ThreadPark                        19853        918218
 jdk.SocketRead                         6459        325796
 jdk.JavaMonitorWait                    5581        200005
 jdk.ClassLoaderStatistics              2620         81098
...
```

______________________________________________________________________________

Used with permission and thanks, [initially written and published by Brice Dutheil in his blog](https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/).
