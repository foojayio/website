---
title: "The Heap, Stack, and Metaspace"
description: "The JVM divides memory into several distinct regions, each serving a different purpose. The Heap is where object instances live. When you call new, the object is allocated on the heap. The heap is managed by the garbage collector, which ..."
url: "/pedia/the-heap-stack-and-metaspace/"
frozen: false
---

The JVM divides memory into several distinct regions, each serving a different purpose.

**The Heap** is where object instances live. When you call `new`, the object is allocated on the heap. The heap is managed by the garbage collector, which periodically reclaims memory from objects that are no longer reachable. You control the initial and maximum heap size with `-Xms` and `-Xmx` JVM flags.

**The Stack** is per-thread memory that holds stack frames for method calls. Each frame contains local variables, the operand stack used for expression evaluation, and metadata about the method call. Stack memory is automatically freed when a method returns. Deep recursion can exhaust the stack, causing a `StackOverflowError`. Stack size is configured with `-Xss`.

**Metaspace** (called PermGen in Java 7 and earlier) stores class metadata — the structure of loaded classes, method bytecode, and constant pool data. Unlike the old PermGen, Metaspace lives in native memory outside the Java heap and grows automatically by default, though you can cap it with `-XX:MaxMetaspaceSize`. A `OutOfMemoryError: Metaspace` usually signals a class-loader leak, where new class definitions are being generated continuously without the old ones being unloaded.

More reading on Foojay: [Debugging RAM: Java Garbage Collection – Java Heap Deep Dive](https://foojay.io/today/debugging-ram-java-garbage-collection-java-heap-deep-dive-part-1/)

## See Also

* [Garbage Collection](/pedia/garbage-collection/)
* [GC Algorithms: G1, ZGC, and Shenandoah](/pedia/gc-algorithms-g1-zgc-and-shenandoah/)
* [Epsilon GC](/pedia/epsilon-gc/)
* [Project Lilliput](/pedia/project-lilliput/)
