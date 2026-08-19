---
title: "JDK Mission Control (JMC)"
description: "JDK Mission Control is a suite of tools for profiling, monitoring, and diagnosing Java applications. It provides a GUI frontend for Java Flight Recorder (JFR) data and a live connection to running JVMs via JMX. JMC was originally developed by ..."
url: "/pedia/jdk-mission-control-jmc/"
frozen: false
---

JDK Mission Control is a suite of tools for profiling, monitoring, and diagnosing Java applications. It provides a GUI frontend for [Java Flight Recorder (JFR)](https://foojay.io/pedia/jfr-java-flight-recorder/) data and a live connection to running JVMs via JMX.

JMC was originally developed by BEA Systems as JRockit Mission Control, acquired by Oracle with BEA in 2008, and open-sourced under the Eclipse Foundation in 2018 (project name: JDK Mission Control, or JMC). It is available as a standalone download and as a plugin for Eclipse IDE and IntelliJ IDEA.

Key capabilities:

* **JFR Analyser** — Opens and analyses `.jfr` recordings. Provides built-in rules that flag common performance issues (excessive GC, high lock contention, thread starvation) with descriptions and recommendations.
* **JVM Browser** — Connects to local or remote JVMs over JMX, showing live heap usage, thread counts, CPU load, and class loading stats.
* **Flight Recorder Control** — Start, stop, and configure JFR recordings on a live JVM directly from the GUI.
* **Heap Dump analysis** — Basic heap dump viewing; for deep heap analysis a dedicated tool like Eclipse Memory Analyser (MAT) is typically used alongside.

JMC download and documentation: [github.com/openjdk/jmc](https://github.com/openjdk/jmc)

## See Also

* [JFR (Java Flight Recorder)](/pedia/jfr-java-flight-recorder/)
* [Heap Dump](/pedia/heap-dump/)
* [Thread Dump](/pedia/thread-dump/)
