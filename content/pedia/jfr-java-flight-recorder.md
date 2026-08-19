---
title: "JFR (Java Flight Recorder)"
description: "Java Flight Recorder (JFR) is a low-overhead profiling and event-collection framework built into the JDK. It records a continuous stream of events about the JVM and the running application — garbage collection pauses, thread states, CPU usage, I/O, exception throws, ..."
url: "/pedia/jfr-java-flight-recorder/"
frozen: false
---

Java Flight Recorder (JFR) is a low-overhead profiling and event-collection framework built into the JDK. It records a continuous stream of events about the JVM and the running application — garbage collection pauses, thread states, CPU usage, I/O, exception throws, and hundreds more — with negligible impact on application performance (typically less than 1% overhead).

JFR was originally a commercial feature in Oracle JDK and was open-sourced and contributed to OpenJDK in JDK 11. It is enabled with a simple JVM flag (`-XX:StartFlightRecording`) or programmatically via the `jdk.jfr` API, and recordings can be made in a continuous loop or for a fixed duration.

The companion tool for viewing JFR recordings is **JDK Mission Control (JMC)**, which provides a graphical interface for exploring the captured data. Together, JFR and JMC let you diagnose performance problems, memory pressure, lock contention, and other production issues — often without having to reproduce the problem in a controlled environment.

More reading on Foojay: [JDK Flight Recorder article archive](https://foojay.io/today/category/jdk-flight-recorder/)

## See Also

* [JDK Mission Control (JMC)](/pedia/jdk-mission-control-jmc/)
* [Thread Dump](/pedia/thread-dump/)
* [Virtual Threads](/pedia/virtual-threads/)
