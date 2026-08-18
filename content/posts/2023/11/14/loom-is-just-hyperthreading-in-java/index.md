---
title: "Loom is just HyperThreading in Java"
date: "2023-11-14T10:54:06+00:00"
lastmod: "2023-11-14T10:54:08+00:00"
description: "I had an epiphany: Aren't virtual threads with Loom just a version of HyperThreading on the JVM?"
authors:
  - "johannes-bechberger"
image: "ht_vs_vt_pyramid-1-2000x1125-1.png"
categories:
  - "Java"
tags:
related_posts:
  - "foojay-podcast-14"
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "virtual-thread-pinning-field-guide"
frozen: false
---

While sitting in [Cay Horstmann](https://horstmann.com/unblog/2023-09-19/index.html)'s ["Looming Changes in Java Concurrency" talk at BaselOne](https://baselone.ch/speech.html?id=BEB1A232-BA37-4619-A7F9-33802755DFEB), I had an epiphany: Aren't virtual threads with Loom just a version of HyperThreading on the JVM?  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/ht_vs_vt_pyramid-1-2000x1125.png)

Both try to utilize a computation resource fully, be it hardware core or platform thread, by multiplexing multiple tasks onto it, despite many tasks waiting regularly for IO operations to complete:
![](https://mostlynerdless.de/wp-content/uploads/2023/10/ht_vs_vt_interleaving-2000x560.png)

When one task waits, another can be scheduled, improving overall throughput. This works especially well when longer IO operations follow short bursts of computation.

There are, of course, differences between the two, most notably: HyperThreading doesn't need the tasks to cooperate, as Loom does, so a virtual core can't starve other virtual cores. Also noteworthy is that the scheduler for Hyper-Threading is implemented in silicon and cannot be configured or even changed, while the virtual thread execution can be targeted to one's needs.

I hope you found this small insight helpful in understanding virtual threads and putting them into context. You can find more about these topics in resources like JEP 444 (Virtual Threads) and the "Hyper-Threading Technology Architecture and Microarchitecture" paper.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article appeared originally on my personal blog [mostlynerdless.de](https://mostlynerdless.de/).*
