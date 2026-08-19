---
title: "Garbage Collection"
description: "Garbage Collection (GC) is the process by which the JVM automatically reclaims memory occupied by objects that are no longer reachable by the running application. Java developers do not call free() or delete() as in C or C++; the garbage ..."
url: "/pedia/garbage-collection/"
frozen: false
---

Garbage Collection (GC) is the process by which the JVM automatically reclaims memory occupied by objects that are no longer reachable by the running application. Java developers do not call `free()` or `delete()` as in C or C++; the garbage collector handles memory management automatically.

Most JVM garbage collectors divide the heap into *generations*. The assumption — the "generational hypothesis" — is that most objects die young. New objects are allocated in the young generation, which is collected frequently and cheaply. Objects that survive long enough are promoted to the old generation, which is collected less often but at greater cost.

GC involves a trade-off between throughput (how much work the application gets done per unit of time) and latency (how long GC pauses interrupt the application). Choosing the right garbage collector for your workload is one of the most impactful performance decisions you can make.

More reading on Foojay:

* [What Should I Know About Garbage Collection as a Java Developer?](https://foojay.io/today/what-should-i-know-about-garbage-collection-as-a-java-developer/)
* [The Ultimate 10 Years Java Garbage Collection Guide (2016--2026)](https://foojay.io/today/the-ultimate-10-years-java-garbage-collection-guide-2016-2026-choosing-the-right-gc-for-every-workload/)
* [Debugging RAM: Java Garbage Collection -- Java Heap Deep Dive (Part 1)](https://foojay.io/today/debugging-ram-java-garbage-collection-java-heap-deep-dive-part-1/)

## See Also

* [Epsilon GC](/pedia/epsilon-gc/)
* [GC Algorithms: G1, ZGC, and Shenandoah](/pedia/gc-algorithms-g1-zgc-and-shenandoah/)
* [The Heap, Stack, and Metaspace](/pedia/the-heap-stack-and-metaspace/)
