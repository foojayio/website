---
title: "Thread Dump"
description: "A thread dump is a snapshot of the state of all threads in a running Java process at a specific point in time, written as plain text. It captures each thread's name, state (RUNNABLE, BLOCKED, WAITING, etc.), and full stack ..."
url: "/pedia/thread-dump/"
frozen: false
---

A **thread dump** is a snapshot of the state of all threads in a running Java process at a specific point in time, written as plain text. It captures each thread's name, state (RUNNABLE, BLOCKED, WAITING, etc.), and full stack trace. Thread dumps are the primary tool for diagnosing deadlocks, thread contention, hung threads, and unexpected blocking behaviour.

### Generating a Thread Dump

Several tools can generate thread dumps:

* **`jcmd <pid> Thread.print`** — The recommended command-line approach. Works for all thread types including virtual threads. Example: `jcmd 5145 Thread.print -e > dump.txt`
* **`jstack <pid>`** — Classic tool, still widely used. Prints the thread dump to stdout. Example: `jstack 5145 > dump.txt`. Run `jps` to find the process pid.
* **`jcmd <pid> Thread.dump_to_file <path>`** — Available since Java 21; writes a structured (JSON) dump to a file that includes virtual thread details.
* **JDK Flight Recorder** — Captures thread state as part of a continuous recording, allowing retrospective analysis. Useful when you want a history rather than a point-in-time snapshot. See [JFR](https://foojay.io/pedia/jfr-java-flight-recorder/).
* **JDK Mission Control (JMC)** — Visual tool for connecting to a running JVM, taking thread dumps, and analysing them alongside JFR data.
* **`kill -3 <pid>` (Linux/macOS)** — Sends SIGQUIT to the JVM, which prints a thread dump to stdout. Useful when the process is unresponsive.

> **Note on jconsole:** jconsole (the GUI monitoring tool) has been deprecated and its removal is planned for a future JDK release. For new workflows, prefer `jcmd`, JDK Mission Control, or a profiler.
