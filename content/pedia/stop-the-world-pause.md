---
title: "Stop-the-World Pause"
description: "A stop-the-world (STW) pause is a period during garbage collection when all application threads are suspended so the GC can safely examine and modify the heap. Because objects cannot move or be created while pointers are being updated, the collector ..."
url: "/pedia/stop-the-world-pause/"
frozen: false
---

A stop-the-world (STW) pause is a period during garbage collection when all application threads are suspended so the GC can safely examine and modify the heap. Because objects cannot move or be created while pointers are being updated, the collector temporarily freezes the application.

Short STW pauses are unavoidable in most GC designs — even collectors like ZGC and Shenandoah, which do most of their work concurrently, still require brief STW phases for certain operations. The practical impact on applications ranges from imperceptible (pauses of a few milliseconds) to very serious (pauses of several seconds in poorly configured or overloaded systems).

STW pauses are closely related to [tail latency](https://foojay.io/pedia/latency/): even if 99% of requests complete quickly, a long GC pause at an inopportune moment can push the worst-case response time far above the acceptable threshold. This is why low-pause collectors exist and why careful GC configuration matters for latency-sensitive applications.

## See Also

* [Latency](/pedia/latency/)
* [GC Algorithms: G1, ZGC, and Shenandoah](/pedia/gc-algorithms-g1-zgc-and-shenandoah/)
* [Garbage Collection](/pedia/garbage-collection/)
