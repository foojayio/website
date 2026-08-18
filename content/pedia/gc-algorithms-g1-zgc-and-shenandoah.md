---
title: "GC Algorithms: G1, ZGC, and Shenandoah"
description: "The JDK ships with several garbage collectors, each designed for different goals. Choosing the right one depends on whether your application prioritises throughput, latency, or predictability. Serial GC The simplest collector, designed for single-threaded or very small-heap applications. It uses ..."
url: "/pedia/gc-algorithms-g1-zgc-and-shenandoah/"
frozen: false
---

The JDK ships with several garbage collectors, each designed for different goals. Choosing the right one depends on whether your application prioritises throughput, latency, or predictability.

### Serial GC

The simplest collector, designed for single-threaded or very small-heap applications. It uses a single thread for both young and old generation collection and is the default in environments with a single CPU or very small heaps (under \~100 MB). For constrained containers or command-line tools, Serial GC has the lowest overhead. Use `-XX:+UseSerialGC`.

### Parallel GC

The default collector before Java 9, optimised for **maximum throughput** on multi-core hardware. It uses multiple threads to perform young and old generation collections, but all collections are stop-the-world. Ideal for batch processing, data pipelines, and applications where overall throughput matters more than pause time. Use `-XX:+UseParallelGC`.

### G1 (Garbage-First)

The default collector since Java 9. G1 divides the heap into equal-sized regions and prioritises collecting regions with the most garbage first — hence the name. It achieves a practical balance between throughput and pause time, with configurable pause-time goals (`-XX:MaxGCPauseMillis`). G1 is the right choice for most general-purpose server applications with heaps from a few hundred megabytes up to tens of gigabytes. Use `-XX:+UseG1GC` (default from Java 9).

### ZGC

ZGC (Z Garbage Collector) became production-ready in Java 15 and performs the vast majority of its work **concurrently** with the application. Stop-the-world pauses are typically under 1 millisecond regardless of heap size — ZGC has been validated on heaps exceeding a terabyte. ZGC is the best choice for latency-sensitive applications where even rare multi-millisecond pauses are unacceptable. Use `-XX:+UseZGC`.

### Shenandoah

Developed by Red Hat and included in mainline OpenJDK since Java 15, Shenandoah also performs most of its work concurrently, with very short stop-the-world pauses. It compacts the heap concurrently (unlike G1, which compacts during STW phases), delivering low tail latency at the cost of slightly higher CPU overhead. Use `-XX:+UseShenandoahGC`.

### Epsilon GC

A no-op collector: it allocates memory but **never reclaims it** . When the heap is exhausted, the JVM exits. Epsilon is useful for performance benchmarking (eliminating GC noise from measurements), short-lived tools where GC overhead would outweigh its benefit, and testing applications for allocation behaviour. Not suitable for long-running production workloads. Use `-XX:+UseEpsilonGC` (requires `-XX:+UnlockExperimentalVMOptions`).

See also:

* [Garbage Collection](https://foojay.io/pedia/garbage-collection/)
* [Stop-the-World Pause](https://foojay.io/pedia/stop-the-world-pause/)
* [Latency](https://foojay.io/pedia/latency/)
* [What Should I Know About Garbage Collection as a Java Developer?](https://foojay.io/today/what-should-i-know-about-garbage-collection-as-a-java-developer/)
* [Apache Cassandra 4.0: Taming Tail Latencies with Java 16 ZGC](https://foojay.io/today/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/)
* [Optimizing the Garbage Collector when Migrating Cloud Workloads](https://foojay.io/today/optimizing-the-garbage-collector-when-migrating-cloud-workloads/)
