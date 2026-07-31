---
title: "Pointer Arithmetic in Modern Java"
slug: "pointer-arithmetic-in-modern-java"
date: "2026-01-15T09:00:12+00:00"
lastmod: "2026-01-19T10:06:59+00:00"
description: "How to perform pointer arithmetic using the Foreign Function & Memory API to achieve zero-GC memory access."
canonical: "https://davidvlijmincx.com/posts/pointer-arithmetic-in-java/"
authors:
  - "david-vlijmincx"
image: "jmh.png"
categories:
  - "Java"
tags:
related_posts:
  - "everything-bad-in-java-is-good-for-you"
  - "debugging-tutorial-java-return-value-intellij-jump-to-line-and-more"
  - "does-java-18-finally-have-a-better-alternative-to-jni"
  - "handling-jdk-and-gc-options-dynamically-in-elasticsearch"
enlighterjs: true
frozen: false
---

Introduction {#_introduction}
-----------------------------

In this post, we dive into a more advanced topic: pointer arithmetic in Java. With the introduction of the Foreign Function \& Memory API (Panama), we can interact with native memory.

Usually, when we work with off-heap memory, we use `MemorySegment` instances to ensure safety. However, creating these objects can sometimes add overhead. In this post, we will look at how to access native memory using addresses. The goal is to create fewer objects that are not strictly needed and only add GC pressure.

Background Info {#_background_info}
-----------------------------------

I am working on a Project that creates bindings to \[IO_Uring\](<https://github.com/davidtos/JUring>). The project has a path that loops over thousands of pointers and converts them to `MemorySegments`. This is done to access three values inside the struct the pointers point to. This loop creates a lot of short-lived Objects as I need a new `MemorySegment` for each pointer. To prevent creating so many objects, I used the pointer arithmetic you see in this post.

### Warning {#_warning}

When using a global segment, we are trading safety for speed. This approach cannot detect if the memory backing the pointer has been released or even is there to begin with.

The Setup {#_the_setup}
-----------------------

To make this work, we need a way to access memory using addresses. In the following class, we define a simple "Point" structure (with x and y coordinates) and a special constant called `GLOBAL_MEMORY`.

The `GLOBAL_MEMORY` acts as a view over the entire memory space. By creating a segment starting at address 0 and explicitly allowing it to access a huge range, we can use any long address as an offset. This allows us to read and write data just by knowing the memory address, bypassing the need to create a specific `MemorySegment` instance for each pointer.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.lang.foreign.ValueLayout;

public class ZeroGcPoint {

    public static final GroupLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y")
    );

    // This acts as the base for all memory access.
    private static final MemorySegment GLOBAL_MEMORY = MemorySegment.ofAddress(0L).reinterpret(Long.MAX_VALUE);

    // These VarHandles know the offset within the struct for these fields
    private static final VarHandle VH_X = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("x"));
    private static final VarHandle VH_Y = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("y"));

    // Get the value located at GLOBAL_MEMORY + address + X.
    public static int getX(long address) {
        return (int) VH_X.get(GLOBAL_MEMORY, address);
    }

    // Get the value located at GLOBAL_MEMORY + address + Y.
    public static int getY(long address) {
        return (int) VH_Y.get(GLOBAL_MEMORY, address);
    }

    // Setting the values without the need of a segment.
    public static void set(long address, int x, int y) {
        VH_X.set(GLOBAL_MEMORY, address, x);
        VH_Y.set(GLOBAL_MEMORY, address, y);
    }
}</pre>

The magic and the danger lies in the `GLOBAL_MEMORY` constant. We create a segment starting at address 0 and use `reinterpret` to extend its size to `Long.MAX_VALUE`. This effectively gives us a view over the entire system memory.

Because our VarHandles are derived from the Layout, they expect a MemorySegment and an offset. By passing `GLOBAL_MEMORY` as the base segment and the address as the offset, we are telling Java: "Start at 0, move forward by address, and read the data."

Comparing Approaches {#_comparing_approaches}
---------------------------------------------

Now let's see how this compares to the standard way of accessing off-heap memory. In the following example, we allocate a `ZeroGcPoint` inside an Arena and try two different approaches to read the data.

**Approach 1** represents the standard way. We take the address and reconstruct a MemorySegment from it. While safe and kind of the standard, this creates a new Java object, which adds pressure to the Garbage Collector.

**Approach 2** uses our `ZeroGcPoint` class. We simply pass the long address. Because we are using the static `GLOBAL_MEMORY` inside the utility class, no new `MemorySegment` object is created during the access. This is effectively pointer arithmetic in Java.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;


public class Main {
    public static void main(String[] args) {
        try (Arena arena = Arena.ofConfined()) {

            MemorySegment pointSegment = arena.allocate(ZeroGcPoint.LAYOUT);
            long rawAddress = pointSegment.address();
            // setting the values
            ZeroGcPoint.set(rawAddress, 10, 20);

            System.out.println("Memory Address: " + rawAddress);

            // ==========================================================
            // APPROACH 1: The Standard Way
            // ==========================================================

            VarHandle standardVhX = ZeroGcPoint.LAYOUT.varHandle(
                    MemoryLayout.PathElement.groupElement("x")
            );
            VarHandle standardVhY = ZeroGcPoint.LAYOUT.varHandle(
                    MemoryLayout.PathElement.groupElement("y")
            );

            MemorySegment retrievedSegment = MemorySegment.ofAddress(rawAddress).reinterpret(ZeroGcPoint.LAYOUT.byteSize());

            // '0L' is the offset relative to the start of 'retrievedSegment'
            int stdX = (int) standardVhX.get(retrievedSegment, 0L);
            int stdY = (int) standardVhY.get(retrievedSegment, 0L);

            System.out.printf("Uses  X: %d, Y: %d%n", stdX, stdY);

            // ==========================================================
            // APPROACH 2: The Global way
            // ==========================================================

            int fastX = ZeroGcPoint.getX(rawAddress);
            int fastY = ZeroGcPoint.getY(rawAddress);

            System.out.printf("X: %d, Y: %d%n", fastX, fastY);
        }
    }
}</pre>

In the first approach, notice line 29 `MemorySegment.ofAddress`. We are explicitly asking the JVM to instantiate a new MemorySegment object on the heap that wraps the native memory at that address. If you do this once, it's negligible. If you do this inside a loop running thousands of times, you are generating a massive number of short-lived objects that the Garbage Collector eventually has to clean up.

Benchmark {#_benchmark}
-----------------------

Running a JMH benchmark shows the following performance improvement:

<pre class="EnlighterJSRAW" data-enlighter-language="text">Benchmark       Mode  Cnt         Score       Error   Units
approach2       thrpt    5  21009813.078 ± 98033.024  ops/ms
approach1       thrpt    5   1488555.237 ± 42608.652  ops/ms</pre>

A higher score means it performs better.

Conclusion {#_conclusion}
-------------------------

In this post, we looked at how to perform pointer arithmetic using the Foreign Function \& Memory API. By using a global memory segment and addresses, we can access native memory without the overhead of creating temporary `MemorySegments`.
