---
title: "Heap Dump"
description: "A heap dump is a snapshot of all the objects in a Java process's heap memory at a specific point in time, written to a file. It captures every object, its class, its size, and the references connecting objects to ..."
url: "/java-terms-explained/heap-dump/"
aliases:
  - "/pedia/heap-dump/"
---

A heap dump is a snapshot of all the objects in a Java process's heap memory at a specific point in time, written to a file. It captures every object, its class, its size, and the references connecting objects to each other.

Heap dumps are an essential tool for diagnosing memory-related problems. If your application is running out of memory, growing slowly over time (a memory leak), or consuming far more RAM than expected, a heap dump lets you see exactly what is occupying the heap and trace why objects are being retained.

Common ways to capture a heap dump include the `jmap` command-line tool, the `-XX:+HeapDumpOnOutOfMemoryError` JVM flag (which writes a dump automatically when the application crashes with an `OutOfMemoryError`), and JDK Mission Control. The resulting file — usually with a `.hprof` extension — can be analysed with tools such as Eclipse MAT (Memory Analyzer Tool) or VisualVM.

A heap dump is closely related to a [thread dump](https://foojay.io/java-terms-explained/thread-dump/), but while a thread dump captures the state of running threads, a heap dump captures the state of memory.

## See Also

* [Thread Dump](/java-terms-explained/thread-dump/)
* [JDK Mission Control (JMC)](/java-terms-explained/jdk-mission-control-jmc/)
* [Virtual Threads](/java-terms-explained/virtual-threads/)
