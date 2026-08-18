---
title: "Debugging RAM: Java Garbage Collection - Java Heap Deep Dive (Part 1)"
date: "2022-01-11T15:36:04+00:00"
lastmod: "2022-02-10T08:48:27+00:00"
description: "Memory usage is one of the most important aspects for devs in general and Java SE devs in particular. GC tips, tricks, internals, and more!"
canonical: "https://talktotheduck.dev/debugging-ram-java-garbage-collection-java-heap-deep-dive-part-1"
authors:
  - "shai-almog"
image: "Lightrun_-_Talk_to_the_duck_blog_-_memory_management-02.jpg"
categories:
  - "Performance"
  - "Tutorials"
related_posts:
  - "debugging-ram-detect-fix-memory-leaks-in-managed-languages-heap-deep-dive-part-2"
  - "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
  - "production-horrors-handling-disasters-public-debrief"
  - "optimizing-the-garbage-collector-when-migrating-cloud-workloads"
frozen: false
---

There are many excellent articles on Java Garbage Collection, Java Memory usage and generally Java heap. Unfortunately, they are all over the place. They mix architecture, concepts and problem solving as separate pieces. A lot of the material is out of date or doesn't include pragmatic information for solving problems with the garbage collector. E.g. e.g. pause times, heap space usage etc.

In this post I won't go into memory leaks. They're important but this is a different subject I would like to discuss in a post on its own.

## Garbage Collector Tradeoff

GCs are amazing, unreferenced objects are collected in an automatic process. But garbage collection still makes some tradeoff:

* Memory Footprint
* Pauses
* Performance

Pick two of those. A garbage collector can tradeoff RAM to provide faster performance and fewer GC stalls. In this post I'll discuss the strategies to picking and tuning a GC.

Normally, when we want to pick a library we just do a benchmark. But benchmarking a GC is much harder. If we overload a GC we might end up with a GC that handles stress well but isn't optimal for typical memory allocation. It's crucial to understand how the garbage collectors work and that we profile the GC with "real world" workloads.

## Not your Fathers Stop the World Mark Sweep

Java GC's have come a long way since Java 1.0's stop the world GC. While there are many types of garbage collectors most of the new ones are generational and parallel/concurrent. This might not seem important when working on our local machines. But the difference is very noticeable when GCing very large heaps.

GCs "seamlessly" detect unused objects to reclaim heap space. But there are tradeoffs. When is an object deemed as an "unused object" is the core memory management tradeoff.

### Generational Garbage Collection

Most modern GCs assume object life-cycle fits into a generational paradigm. The old generation space objects live a long life and rarely get collected, this . They don't need frequent scanning. Younger generation objects live and die quickly. Often together.

Generational garbage collection (typically) traverse the young generation more frequently and give special attention to connections between the generations. This is important as there are fewer areas to scan during a minor garbage collection cycle. The term for the shorter cycle is incremental GC as opposed to the full GC cycle. GCs typically try to minimize full GC cycles.

### Concurrent vs. Parallel Garbage Collector

Parallel GC is often confused with concurrent GC. To make matters even more confusing a GC can be both a parallel GC and a concurrent GC (e.g. G1).

The difference is simple though:

* A parallel GC has multiple GC threads. The GC threads perform the actual garbage reclaiming. They are crucial for large scale collection
* A concurrent GC lets the JVM do other things while it's in the mark phase and optionally during other stages

Intuitively most of us would want to have both all the time and make sue of application threads. But this isn't always the right choice. Concurrency and multiple application threads incur an overhead. Furthermore, these GC's often make tradeoffs that miss some unreachable objects and leave some heap memory unreclaimed for a longer period. To be clear, they will find all unused memory in a full GC cycle but they try to avoid such cycles and you might pay a penalty.

The following are the big ticket GCs as of JDK 17.

### Serial Collector

This is a single thread garbage collector. That means it's a bit faster than most GCs but results in more pauses. If you're benchmarking performance it makes sense to turn on this GC to reduce variance. Since pretty much ever CPU is multi-core this GC isn't useful for most real world deployment, but it makes debugging some behaviors much easier.

There is one case where serial collector might have a big benefit in production and that's serverless workloads (e.g. lambdas etc.). In those cases the smallest/fastest solution wins and this might be the right solution to work with limited physical memory on a single core VM.

Notice that despite its relative simplicity, the serial collector is a generational GC. As such it's far more modern than the old Java GCs.

You can turn on this GC explicitly using the `-XX:+UseSerialGC`.

### Parallel Collector aka Throughput Collector

The multi-thread equivalent of the serial collector. This is a fine collector that can be used in production, but there are better. The serial collector is better for benchmarks and ZGC/G1 usually offer better performance.

You can turn on this GC explicitly using the `-XX:+UseParallelGC` option.

One of the big benefits of the parallel GC is its configurability. You can use the following JVM Options to tune it:

* `-XX:ParallelGCThreads=ThreadCount` - The number of GC threads used by the collector
* `-XX:MaxGCPauseMillis=MaxDurationMilliseconds` - Place a limit on GC pauses in milliseconds. This defaults to no-limit
* `-XX:GCTimeRatio=ratio` - Sets the time dedicated to GC in a `1/(ratio + 1)` so a value of `9` would mean `1 / (9 + 1)` or `10%`. So `10%` of CPU time would be spent on GC. The default value is `99` which means `1%`

### G1 Garbage Collector

The G1 Garbage Collector is a heavy duty GC designed for big workloads on machines with large heap sizes (roughly 6GB or higher). It tries to adapt to the working conditions in the given machine. You can explicitly enable it using the JVM option `-XX:+UseG1GC`.

G1 is a concurrent GC that works in the background and minimizes pauses. One of it's cooler features is string de-duplication which reduces the overhead of strings in RAM. You can activate that feature using `-XX:+UseStringDeduplication`.

### Z Garbage Collector (ZGC)

ZGC was experimental until recent versions of the JVM. It's designed for even larger heap sizes than G1 and is also a concurrent GC. It does support smaller environments and can be used for heap size as small as 8mb all the way to 16TB maximum heap size!

One of its biggest features is that it doesn't pause the execution of the application for more than 10ms. The cost is a reduction in throughput.

ZGC can be enabled using the `-XX:+UseZGC` JVM option.

## Picking and Debugging a Garbage Collector

Java 8 used the `-verbose:gc` flag to generate GC logs and the `-XX:+PrintGCDetails` flag.

Newer JDKs use `-Xlog:gc:file.log` which prints the GC details to the given file. By enabling these features and running your application as normal you can track the GC behavior and tune your code/deployment appropriately.

A while back I ran into [GCeasy](https://gceasy.io/) , which is a website that analyzes GC logs rather nicely. There are several other tools like that and they can provide you with some interesting information. However, the log file is also readable directly and you can learn a lot from reading it.

Furthermore, you can get more verbose GC information using the JVM option:

    -Xlog:gc*=debug:file=gc-verbose.log

Notice that on Linux/Unix you will need to surround this command in quotes so the shell won't try to expand it.

With verbose output you get deeper insight into the inner workings of the GC and can follow up on tuning the JVM heap. In fact I recommend that any JVM developer try this flag at least once to get a sense of the inner workings of the Java heap space.

### Benchmarking/Measuring

As I mentioned before, GCs are terrible for application performance benchmarks. If we just used regular benchmarks the serial GC would often win even though it shouldn't be the first pick for most of us. The trick is to use realistic loads then review the GC logs.

Then we can decide on the tradeoffs we're willing to live with based on the statistics each GC provides. Notice we can also limit ourselves to external metrics only such as CPU and RAM usage. That can be a perfectly fine approach. However, doing a heavy load test might not be the best representation of a GC performance. To be clear, you should still do a heavy load tests.

## GC Tuning

The first thing pretty much any Java developer does when tuning memory is define the maximum size and minimum size. The initial heap size is easily determined using `-Xmx` and -`Xms` JVM arguments that have been with us for decades.

One common approach is to set both to the same size. This isn't necessarily bad. It simplifies the memory management logic within the GC which focuses on one value now. This does pose a risk though. It means there's very little room for error.

The most important performance improvements you can do are through application code. It's very rare that a performance issue falls to GC flag tuning (although it can happen).

### Reducing RAM Usage

Most developers prefer reducing garbage collection times, but for some memory consumption is a bigger issue. If you're running in a restricted environment, e.g. a microservice or serverless container. You can deal with memory shortage by using the following Java flags:

* Reduce the values of `-XX:MaxHeapFreeRatio` which defaults to `70%` and `-XX:MinHeapFreeRatio` which defaults to `40%`. The max value can be reduced to as much as `10%`
* You can use `-XX:-ShrinkHeapInSteps` which will trigger GC more frequently and push down memory usage at the expense of performance

### Generational Optimization

Typically most Java applications perform better when you add RAM. But sometimes a very large amount of RAM can trigger very long GC stalls that can sometimes even trigger a timeout.

A big source of stalls is full GC cycles which can happen if the GC fails to build the right memory pools of young/survivor generations. E.g if you have an application that creates and discards objects quickly you might need a larger young generation than an older generation. You can tune that using `-XX:NewRatio` which lets you define the ration between old and young generations. This defaults to 2, which means the new generation is double the size of the old generation.

You can also tune the -XX:NewSize value which specifies the amount of RAM dedicated to the new generation.

## Metaspace, Permanent Generation, Stack Size, etc.

These aren't technically a part of the GC but they often get mixed up with Java heap memory related issues so it's a good place to discuss them.

If you have a thread heavy application you might want to consider reducing the stack size if applicable. Usually the stack size is tuned to allow for a larger size, this lets us support deeply recursive algorithms.

With Java 8 PermGen (AKA Permanent Generation) was finally killed. PermGen was a special memory space that stored class files and meta data. We occasionally had to tune it for applications that generated bytecode dynamically as it would trigger memory errors if there were too many class files. The new Metaspace has automatic memory management and solves most of the issues in PermGen.

We can still set the size of the meta-space using the hint `-XX:MaxMetaspaceSize`.

It's important to tune the application properly to GC. Reducing usage of native code (and finalizers), weak references, soft and phantom references. All of these features create an overhead to the GC. Although to be fair, in most cases on the server, these aren't the determining factor.

## The Future at Valhalla

GCs are amazing, but there are some edge cases in Java heap memory performance.

E.g. if we write native memory handling in C vs. Java we can get roughly equivalent performance to native code with something like:

```java
int[] myArray = new int[2000];
```

In some cases this can perform faster than C due to Javas fast allocator code and primitive support.

The same isn't true for:

```java
Integer myArray = new Integer[2000]; 
```

Or for:

```java
Point[] myPointArray = new Point[2000];
```

In C++ we can define a stack object whose memory is directly connected to its parent. Be it the stack frame or the object in which it's contained. This has its downsides as data needs to be copied, it's no longer just a pointer. But data sits in the same page or even register and thus memory fragmentation is non-existent and overhead is very low.

This is part of the huge problem Valhalla is trying to solve. It will add the ability to define objects as value or primitive which will let us define them as part of something else. This will effectively remove the overhead of `Optional` and of the primitive wrappers. It will also make nullability a bit more nuanced.

The impact on the GC would also be huge. Imagine an array of 2000 Point objects collected as a single operation...

This is a big change to Java SE both in the language and the virtual machine. As such I used to be on the fence about it. But after reading some of the [materials related to the project](https://mail.openjdk.java.net/pipermail/valhalla-spec-experts/2021-December/001747.html) I'm cautiously optimistic.

## TL;DR

Garbage collector threads are rarely the reason your app performs badly or even runs out of memory. For `99.9%` of the cases the cause would be in the application code. Don't expect command-line options that would fix an issue magically.

However, these tools let you track the inner working of your application from the GC point of view. When you experience a memory issue in your JVM process you can use these tools to narrow it down.

Having said all that, GC pauses are a real problem that can cause production failures. Especially in very large heaps. Again, you need to review the objects in memory first, but understanding the tradeoffs the virtual machine makes with memory crucial.

### Learn More

Follow me on [Twitter](https://twitter.com/debugagent/) to learn more and get updates for future posts.
