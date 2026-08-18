---
title: "Epsilon GC"
description: "Epsilon GC is a no-op garbage collector: it allocates memory on request but never reclaims it. When the Java heap is exhausted, the JVM exits with an OutOfMemoryError. It was introduced as an experimental feature in Java 11 (JEP 318). ..."
url: "/pedia/epsilon-gc/"
frozen: false
---

Epsilon GC is a **no-op garbage collector** : it allocates memory on request but never reclaims it. When the Java heap is exhausted, the JVM exits with an `OutOfMemoryError`. It was introduced as an experimental feature in Java 11 (JEP 318).

Epsilon sounds counterproductive, but it has legitimate use cases:

* **Performance benchmarking** --- GC activity introduces timing noise in microbenchmarks. Epsilon eliminates that noise, giving a clean measure of allocation and computation cost. Tools like JMH may benefit from Epsilon when benchmarking short-lived workloads.
* **Short-lived command-line tools** --- If an application runs for a fraction of a second and allocates only a few megabytes, GC overhead may outweigh any benefit. Epsilon reduces per-run latency to essentially zero.
* **Allocation testing** --- Engineers building GC-aware libraries can use Epsilon to verify that their code does not allocate unexpectedly, since any allocation under Epsilon is permanent and will eventually cause OOM.

Epsilon is not suitable for long-running applications or workloads with significant allocation. It requires explicitly unlocking experimental options:  

    -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC

See also: [GC Algorithms](https://foojay.io/pedia/gc-algorithms-g1-zgc-and-shenandoah/), [Garbage Collection](https://foojay.io/pedia/garbage-collection/)
