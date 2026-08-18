---
title: "How & When to Use JDK Flight Recorder in Production"
date: "2020-12-15T12:44:28+00:00"
lastmod: "2020-12-15T12:50:14+00:00"
description: "A discussion on the usage of JDK Flight Recorder and that it’s possible to aim at specific time frames where a recording could be useful."
canonical: "https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/"
authors:
  - "brice-dutheil"
image: "Favicon-3-2.png"
categories:
  - "JDK Flight Recorder"
tags:
related_posts:
frozen: false
---

While it would certainly be useful to record the whole lifetime, this is unpractical, even airplane Flight Data Recorders (and Cockpit Voice Recorders) only keep recent history. Instead, it's possible to aim at specific time frames where a recording could be useful:

* At **startup**, the JVM does a lot of things, so does the application, it generally initializes a lot of long lived objects, generally services, threads, etc.
* **Continuously** at **runtime** , this is likely a sliding time window in which one can access what's happened in last *X \<time unit\>*(this is either limited by age or by size). It this case the dump could be done when required.
* At **shutdown** whether the JVM was **killed** or **crashed**. It this case the JFR files are an alternative to heap dumps for the autopsy.

For more details on this topic, see [Continuous Production Profiling and Diagnostics](https://foojay.io/today/continuous-production-profiling-and-diagnostics/), by Marcus Hirt.

### Record Application Startup

I mentioned startup as a separate time frame, because it's useful to inspect startup recording. During this time, the JVM initialize a lot of things, most code has yet to be warmed up, depending on the workload there may be a lot of allocations.

I found that having this startup recording very useful to tune the readiness of an application, as we'll see after. Time bound recording at JVM startup:

```
-XX:StartFlightRecording=settings=profile,duration=6m,name=app-startup,filename=/var/log/jfr/app-startup.jfr
```

Eventually it's possible to tweak this recording with, other parameters like:

* `path-to-gc-roots=true`, which allows to identify leaks using the `OldObjectSample` (enabled in the `profile` settings)
* `maxsize` to set a size threshold to the recording
* `disk=false` if you want to keep the event in memory only before dumping to the configured `filename`. Otherwise the JVM will use it's default strategy which is to evacuate chunks of event to disk, in the JFR `repository` (by default a folder in the temporary folder).

In addition to these recording parameter, it can be useful to set a few JFR wide options, i.e. that affects all recordings, e.g. `-XX:FlightRecorderOptions=stackdepth=96`, which augments the size of the *captured* stack, be advised, that the bigger the number the higher the impact. In the container, checking JFR:

```
❯ jcmd $(pgrep java) JFR.check
6:
Recording 1: name=app-startup duration=6m (running) [1]
❯ jcmd $(pgrep java) JFR.check
6:
No available recordings. [2]

Use jcmd 6 JFR.start to start a recording.
❯ ls -lah /var/log/jfr/app-startup.jfr
-rw-r--r--   1 root root 57M May  6 22:35 /var/log/jfr/app-startup.jfr
```

|---|----------------------------------------------------|
| 1 | Indicates the configured 30s recording is ongoing. |
| 2 | No more recording once the duration is over.       |

I'll show how to use this recording later in this article.

### Record Application Post-Startup or Continuous Recording

Once startup has been recording, it's useful to set up a continuous recording. The good thing is that the JVM allows to define multiple recording in the command line. Let's add another `-XX:StartFlightRecording` with the `delay` parameter. Delayed continuous recording:

```
-XX:StartFlightRecording=settings=profile,delay=5m,maxage=10m,name=post-startup,filename=/var/log/jfr/post-startup.jfr
```

This will register a continuous profiling that will start 5m after the JVM starts. And it sets a retention of 10 minutes, or a retention of the default maximum size which is `250 MiB` in JDK11.

If this is the only recording, `JFR.check` will output something like that.In the container, checking JFR:

```
$ jcmd $(pgrep java) JFR.check
6:
Recording 1: name=post-startup maxage=10m (delayed) [1]
❯ jcmd $(pgrep java) JFR.check
6:
Recording 1: name=app-startup maxage=10m (running) [2]
```

|---|----------------------------------------------------------------------------|
| 1 | Indicates there's a recording that will start at some point in the future. |
| 2 | Indicates the configured continuous recording is ongoing.                  |

Note that in the case of the continuous recording it's necessary to dump the recording via `JFR.dump`.

### Recording for Shutdown

The only thing to do is to set the recording parameter `dumponexit=true` (on each recording). The record will be stored in the configured `filename` otherwise JFR will create a file similar to this the working directory of the process `hotspot-pid-6-id-1-2020_05_03_12_54_14.jfr`

The JVM source code suggests that JFR has the notion of [emergency JFR dump](https://github.com/openjdk/jdk11u/blob/e81745c5c5e8194791047728a73b74a5262f1634/src/hotspot/share/jfr/recorder/repository/jfrEmergencyDump.cpp#L250-L289), but the mechanism is different as it seems those are dumped in the working directory of the process, which may not be writable in a container. I don't think it's currently possible to change the location. But from what I've seen SOE or OOM are dumped fine via `dumponexit=true` and `filename=...​`.OutOfMemory ⇒ `hs_oom_pid<pid>.jfr`StackOverflowError ⇒ `hs_soe_pid<pid>.jfr`Other error ⇒ `hs_err_pid<pid>.jfr`

### Putting It All Together

Putting it all together, let's put these in the `JDK_JAVA_OPTIONS`. Record startup then record continuously, and dump on exit:

```
-XX:StartFlightRecording=settings=profile,duration=6m,name=app-startup,dumponexit=true,filename=/var/log/jfr/app-startup.jfr
-XX:StartFlightRecording=settings=profile,delay=5m,maxage=10m,name=post-startup,dumponexit=true,filename=/var/log/jfr/post-startup.jfr
-XX:FlightRecorderOptions=stackdepth=96
```

After acquiring the record files, you are ready to exploit them. We've seen `jfr` on which it's possible to build upon, now in the upcoming sections, we'll briefly present the other elephant in the room (in a positive way), **JDK Mission Control** which empowers its users with remarkable diagnosis skills!
