---
title: "Using Java Flight Recorder and Mission Control (Part 1)"
date: "2020-11-02T09:49:11+00:00"
lastmod: "2020-12-07T13:24:05+00:00"
description: "Java Flight Recorder is the profiler you can use in production, continuously, it is a feature in the JVM that traces the JVM with events."
canonical: "https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/"
authors:
  - "brice-dutheil"
image: "https://blog.arkey.fr/assets/jfr/java-flight-recorder-big-picture2.svg"
categories:
  - "JDK Flight Recorder"
  - "Tools"
related_posts:
frozen: false
---

Java Flight Recorder is the profiler you can use in production, continuously. Flight Recorder has been available before in the JDK, e.g., it shipped as part of the JDK 8, but to use it, it required that you set specific commercial VM flags to unlock Flight Recorder, this is not anymore necessary from Java 11 onwards.

|   |   |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   | This page assumes JDK 11, that means there is no need to set `-XX:+UnlockCommercialFeatures`, `-XX:+FlightRecorder` flags. Or, more particular to JFR, the use of `-XX:+UnlockDiagnosticVMOptions` and `-XX:+DebugNonSafepoints` [source](https://github.com/openjdk/jmc/blob/bacb448fd4ed1a9a5d887c50aebff4e854d3512a/core/org.openjdk.jmc.common/src/main/java/org/openjdk/jmc/common/version/JavaVersionSupport.java#L59-L60) |

### Brief Introduction to Java Flight Recorder

Flight Recorder is a feature in the JVM that **traces** what's happening inside the JVM with **events**. Flight Recorder development started a long time ago, in the early 2000, on a JVM named JRockit.

*This JVM was first developed by a Swedish company named Appeal, then Appeal got acquired by BEA. Oracle acquired BEA, then acquired Sun, and merged the source of JRockit to Hotspot.*

At some point, BEA/Appeal engineers needed insight in their JVM, in production, logging was not an option as it had too much overhead, so they designed an event based system to work reliably in production.

Mission Control, on the other hand, is a software that will analyze these events and build consolidated views for us to analyze.

JFR with Mission Control together are similar to architectures as [*Event Sourcing*](https://martinfowler.com/eaaDev/EventSourcing.html) systems.
![java flight recorder big picture2](https://blog.arkey.fr/assets/jfr/java-flight-recorder-big-picture2.svg) JFR Big Picture

I won't dive in, but most profilers do have a bias when profiling and don't report accurate results. The issue lies in how profilers handle JVM safepoints, most don't account properly the time spent in safepoints. Some great profilers, like the great [async-profiler](https://github.com/jvm-profiling-tools/async-profiler), are not subject to these biases, JFR is also not subject to it.

Here's great material provided by [Nitsan Wakart](https://twitter.com/nitsanw):

* [Why (Most) Sampling Java Profilers Are Fucking Terrible](https://psy-lob-saw.blogspot.com/2016/02/why-most-sampling-java-profilers-are.html)
* [Safepoints: Meaning, Side Effects and Overheads](https://psy-lob-saw.blogspot.com/2015/12/safepoints.html)
* [The Pros and Cons of AsyncGetCallTrace Profilers](https://psy-lob-saw.blogspot.com/2016/06/the-pros-and-cons-of-agct.html)

### Using JFR

#### How to Start a Recording ?

One of the first interesting things you can do is to start a recording at runtime: `jcmd` is the tool for this job, it's the Swiss Army knife for servicing your Java app. If you don't know it, use it, it's great!

Prior to Java 11, Flight Recorder had to be enabled before use. Now, that's not necessary anymore. It's possible to *start* a JFR session. FlightRecorder is on by default.

```
$ jcmd $(pgrep java) VM.flags -all | grep FlightRecorder
     bool FlightRecorder                           = true                                      {product} {management}
    ccstr FlightRecorderOptions                    =                                           {product} {default}
```

Just runt `JFR.start` on the running java pid.

```
$ jcmd $(pidof java) JFR.start
6:
Started recording 2. No limit specified, using maxsize=250MB as default.

Use jcmd 6 JFR.dump name=2 filename=FILEPATH to copy recording data to file.
```

The output guides the user to the next useful commands, in particular, `JFR.dump`. Also, this command tells you that a recording by default is limited to 250Mb. `jcmd` provides a `help` command that describes documents for each command options.

```
$ jcmd $(pgrep java) JFR.start \
  name=app-profile \
  duration=300s \
  filename=/tmp/app-profile-$(date +%FT%H-%M-%S).jfr \
  settings=profile
6:
Started recording 2. The result will be written to:

/tmp/app-profile-2020-03-26T16-41-48.jfr
```

In production, you'll be most likely using `duration`, `maxsize`, `filename` and `settings` options. We'll briefly look at other JFR commands after discussing the `settings`.

|   |   |
|---|---------------------------------------------------------------------------------------------------------------|
|   | Later in this series we'll see how to start recording from the command line using `-XX:StartFlightRecording`. |

#### Setting Files

The `settings` option refers to a configuration of the FlightRecorder, the JDK ships with two : `default` and `profile`. A configuration is an XML file, with `event` elements that describe how JFR in the JVM will handle events, if they are enabled, their threshold, if stacktraces are recorded, etc. And there is also a `control` element that is used by JDK Mission Control.

Here's the first few lines of the `default` settings. As its name impies, this is the configuration that is used when the command is run without a `settings` option.$JAVA_HOME/lib/jfr/default.jfc

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
     Recommended way to edit .jfc files is to use Java Mission Control,
     see Window -> Flight Recorder Template Manager.
-->

<configuration version="2.0"
               label="Continuous"
               description="Low overhead configuration safe for continuous use in production environments, typically less than 1 % overhead."
               provider="Oracle">

    <event name="jdk.ThreadAllocationStatistics">
      <setting name="enabled">true</setting>
      <setting name="period">everyChunk</setting>
    </event>

    <!-- a lot more events -->

    <!-- then the control element -->
</configuration>
```

In terms of file size magnitude on a pretty busy web application server using the `default` settings and for a duration of 5 minutes, the resulting dumped file weighs 15 MiB. With this profile you'll get more than basic information, IO, GC events, locking behavior, thread events, method profiling, etc.

The announced overhead is maximum 1% !$JAVA_HOME/lib/jfr/profile.jfc

```xml
<!--
     Recommended way to edit .jfc files is to use Java Mission Control,
     see Window -> Flight Recorder Template Manager.
-->

<configuration version="2.0"
               label="Profiling"
               description="Low overhead configuration for profiling, typically around 2 % overhead."
               provider="Oracle">

    <event name="jdk.ThreadAllocationStatistics">
      <setting name="enabled">true</setting>
      <setting name="period">everyChunk</setting>
    </event>

    <!-- a lot more event -->
</configuration>
```

With the `profile` settings, the dumped file takes around 35mb for a 5min duration. And it will get access to additional events like the `OldObjectSample` stacktraces, or TLS events like TLS handshakes, X509 validation, Classloading events, etc.

It actually has a tad more overhead, 2%. In most workload this should be ok.

To value of the `settings` option is file name of these files `default` or `profile`. In addition, it's also possible to pass an absolute file path, in other words it's possible to use configuration of our own stored elsewhere.

#### Dumping a Recording

If it's needed to acquire the recording, it's possible to dump it at anytime.JFR.dump exampleJFR.dump help

```
$ jcmd $(pidof java) JFR.dump filename=/tmp/app-profile-$(date +%FT%H-%M-%S).jfr
6:
Dumped recording, 239.5 MB written to:

/tmp/app-profile-2020-06-26T15-16-57.jfr
```

If there is a single recording at the time it's possible to just use `JFR.dump`, but JFR is powerful enough to support multiple concomitant recordings, in this case you need to specify which recording to dump, obviously. Some options override those defined in the start command like `filename` or `maxage` for the current dump in particular. THe other options are certainly interesting but I found them a bit less useful in practice.

#### Details of the Active Recording(s)

If they are multiple active recordings or if it's necessary to check the event configuration of the active recording `jcmd` comes with the `JFR.check`.JFR.check exampleJFR.check help

```
$ jcmd $(pgrep java) JFR.check
6:
Recording 2: name=2 maxsize=250.0MB (running)
```

The `verbose` option allows examining which event are enabled for a recording.

#### Stopping an Active Recording

When the recording session is deemed over, then one can stop it providing a different file name than the one set in the start command.JFR.stop exampleJFR.stop help

```
$ jcmd $(pgrep java) JFR.stop \
  name=app-profile \
  filename=/tmp/app-profile-$(date +%FT%H-%M-%S).jfr
```

#### Global Flight Recorder configuration

What we saw before is how to start a recording and how to configure this specific recording. But there is another class of options that modifies aspects of the JFR internals. As a reminder those affects all recording in some way.JFR.configure exampleJFR.configure help

```
$ jcmd $(pidof java) JFR.configure \
  stackdepth=96 \
  repositorypath=/tmp/jfr-repo
6:
Repository path: /tmp/jfr-repo/2020_06_26_16_01_58_6

Dump path: /gclogs

Stack depth: 96

$ jcmd $(pidof java) JFR.configure
6:
Current configuration:

Repository path: /tmp/jfr-repo/2020_06_26_16_03_41_6

Stack depth: 96
Global buffer count: 20
Global buffer size: 512.0 kB
Thread buffer size: 8.0 kB
Memory size: 10.0 MB
Max chunk size: 12.0 MB
Sample threads: true
```

Here I'm increasing the `stackdepth`, this might be useful to generate more accurate flamegraphs, or for some other analysis like with the `OldObjectSample`.

The `repositorypath` is where JFR dumps regularly slices or chunks of jfr events, they have maximum size of `maxchunksize`. These files behave like a log rolling appender. By default, these chunks are stored in the temporary directory and in a subfolder with a timestamp.JFR repository

```
$ ls -lah /tmp/jfr-repo/2020_06_26_16_03_41_6/
total 71M
drwxr-xr-x 2 43514 root 4.0K Jun 26 16:21 .
drwxr-xr-x 3 43514 root 4.0K Jun 26 16:03 ..
-rw-r--r-- 1 43514 root 2.4M Jun 26 16:04 2020_06_26_16_04_02.jfr
-rw-r--r-- 1 43514 root 3.6M Jun 26 16:04 2020_06_26_16_04_12.jfr
-rw-r--r-- 1 43514 root  18M Jun 26 16:10 2020_06_26_16_04_47.jfr
-rw-r--r-- 1 43514 root 2.5M Jun 26 16:10 2020_06_26_16_10_18.jfr
-rw-r--r-- 1 43514 root  19M Jun 26 16:16 2020_06_26_16_10_26.jfr
-rw-r--r-- 1 43514 root  18M Jun 26 16:21 2020_06_26_16_16_16.jfr
-rw-r--r-- 1 43514 root    0 Jun 26 16:21 2020_06_26_16_21_50.jfr
-rw-r--r-- 1 43514 root 8.7M Jun 26 16:25 2020_06_26_16_21_50.part
```

*I'm not sure why some chunks are over 12M (the default chunk size) at this time.*

Careful however as some of these options are not well documented, and may not expose what we'd expect, e.g. `dumppath` only affects dump created when the app crashes and only if the `dumponexit` recording option is true.

|   |   |
|---|----------------------------------------------------------------------------------------------------------------------|
|   | These options are also available at startup via `-XX:FlightRecorderOptions`, we'll see later how to use this option. |

#### JFR Logs

Thanks to unified logging, it's easy to open the hood on any JVM runtime feature. In order to follow JFR, it's possible to log the JFR component, to understand how it works and how options affect recordings.

```
-Xlog:jfr
```

```
[0.337s][info][jfr] Flight Recorder initialized
[0.338s][info][jfr] Created repository /tmp/2020_06_19_13_08_28_6
[0.367s][info][jfr] Creating thread sampler for java:20 ms, native 0 ms
[0.367s][info][jfr] Enrolling thread sampler
[0.367s][info][jfr] Enrolling thread sampler
[0.367s][info][jfr] Updated thread sampler for java: 20  ms, native 0 ms
[0.367s][info][jfr] Updated thread sampler for java: 20  ms, native 0 ms
[0.367s][info][jfr] Updated thread sampler for java: 20  ms, native 20 ms
[0.373s][info][jfr] Started recording "startup" (1) {maxsize=200.0MB, dumponexit=true, duration=6m, filename=/var/log/jfr/startup.jfr}
...
[0.847s][info][jfr] Updated thread sampler for java: 0  ms, native 20 ms
[0.847s][info][jfr] Disenrolling thread sampler
[0.848s][info][jfr] Stopped recording "1" (1). Reason "Dump on exit".
[0.862s][info][jfr] Wrote recording "1" (1) to /var/log/jfr/startup.jfr
[0.864s][info][jfr] Closed recording "1" (1)
[0.866s][info][jfr] Removed repository /tmp/2020_06_19_13_08_28_6
```

Increasing the log level may reveal additional details.

#### Memory Usage in the Process

The section above describes where the actual data is saved for long or large (`duration`, `maxage`, `maxsize`) profiling sessions, e.g. the reposotory, on disk, will grow within these constraints. JFR is safe to enable in prod but there's an overhead in memory as well, although it's usually minimal compared to the heap or other native memory sections, but it's worth mentioning.

If NMT is enabled, you can just display the summary:

```
$ jcmd $(pidof java) VM.native_memory
6:

Native Memory Tracking:

Total: reserved=5324939KB, committed=3600539KB
-                 Java Heap (reserved=2793472KB, committed=2793472KB)
                            (mmap: reserved=2793472KB, committed=2793472KB)

...

-                   Tracing (reserved=75866KB, committed=75866KB) 
                            (malloc=75866KB #85438)

...
```

|   |   |
|---|-----------------------------------------|
|   | JFR's `Tracing` memory zone uses \~74MB |

This was taken on a very active application, with custom events, you mileage may vary.

The next output shows the committed memory for tracing, **after** a 6 min recording, which means JFR will keep a memory zone any. That is the minimal JFR footprint I experienced.

```
-                   Tracing (reserved=21041KB, committed=21041KB)
                            (malloc=21041KB #2783)
```

______________________________________________________________________________

Used with permission and thanks, [initially written and published by Brice Dutheil in his blog](https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/).
