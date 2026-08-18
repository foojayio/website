---
title: "Native Memory in Java: Arenas, Malloc, and Pools"
date: "2026-03-20T10:20:04+00:00"
lastmod: "2026-03-23T10:45:17+00:00"
description: "Managing native memory in Java using the Foreign Function & Memory API, covering Arenas, manual allocation, pooling, and slicing"
canonical: "https://davidvlijmincx.com/posts/java-native-memory-allocation-ffm-api/"
authors:
  - "david-vlijmincx"
image: "ffmep.png"
categories:
  - "Java"
  - "Java Core"
  - "Performance"
related_posts:
  - "pointer-arithmetic-in-modern-java"
  - "async-file-io-with-java-and-io_uring"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
  - "idempotent-spring-boot-starter"
frozen: false
---

## What is the Memory API

The Foreign Function \& Memory (FFM) API is Java's new way of interacting with native code and memory. It is mostly useful when storing data off-heap or passing arguments to a native method. Handling this native memory comes down to balancing how much control you need against the risk of memory leaks. You can rely on the provided `Arena`s to bind memory to safe scopes, or you do it yourself using `malloc` and `free` for absolute control, or implement custom pools and slices to optimize allocations for your use case.

## Arenas

`Arena`s are the built-in way to allocate and manage native memory. Arenas work with a scope, meaning that an `Arena` gets opened, allocates memory, and gets closed again. The Arena acts as a guard to make sure the memory is valid while the scope is open and freed when it is closed.

There are basically two types of Arenas you get out of the box with Java 22: those with a deterministic lifetime and those with a non-deterministic lifetime. Let's look at the deterministic lifetime first. These are the `Arena.ofConfined` and `Arena.ofShared`. These arenas have a specific start and end. You have to open them, and close them in your code. The other type of Arena is non-closeable, such as `Arena.global` and `Arena.ofAuto`. You can open these arenas but you cannot close them. The memory allocated using `Arena.global()` is freed when the application exits. The `Arena.ofAuto()` uses the garbage collector to decide when a `MemorySegment` should be deallocated.

Below is an overview of the different properties each arena has.

| Arena Type | Bounded Lifetime | Manually Closeable | Multi-thread Access |
|------------|------------------|--------------------|---------------------|
| Global     | No               | No                 | Yes                 |
| Auto       | Yes              | No                 | Yes                 |
| Confined   | Yes              | Yes                | No                  |
| Shared     | Yes              | Yes                | Yes                 |

### Using Arenas

Using an arena is quite straightforward. You can use the `Arena` interface to create each of the four arena types. The first one is the global arena. The memory you allocate with it won't be deallocated till the application exits.

```java
Arena arena = Arena.global();
MemorySegment segment = arena.allocate(42);
```

In the example, we allocate 42 bytes and create a `MemorySegment` that you can use. Next up is the `Arena.ofAuto()` that uses the garbage collector to decide when to free memory.

```java
Arena arena = Arena.ofAuto();
MemorySegment segment = arena.allocate(42);
```

The previous two examples showed Arenas that are not explicitly closed. You can see that we didn't call any close method. The next Arena is closeable, it also implements the `Closeable` interface. As you can see in the next example with the try-with-resources statement:

```java
try (Arena arena = Arena.ofShared()) {
    MemorySegment segment = arena.allocate(42);
} // The segment is deallocated here
```

Here we explicitly opened and closed the `Arena`. The `MemorySegment` is only valid inside that scope, because when we exit the `try` the arena is closed and the memory freed. `ofShared` and `ofConfined` work mostly the same. The only difference is that the `MemorySegment` created with a `Confined` arena can't be shared by different threads. You can only use it in the thread that created the arena.

### Creating your own arena

If the provided arenas are not working out for you, you can create your own by implementing the `Arena` interface. This could be useful for when you want to have a different allocation strategy, or need to do something else on allocation. Below is a LoggingArena, that implements the basics and logs when an allocation happens.

```java
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class LoggingArena implements Arena {
    private final Arena backingArena = Arena.ofConfined();

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        System.out.println("Allocating segment of size: " + byteSize + " bytes");
        return backingArena.allocate(byteSize, byteAlignment);
    }

    @Override
    public MemorySegment.Scope scope() {
        return backingArena.scope();
    }

    @Override
    public void close() {
        System.out.println("Closing arena and freeing memory.");
        backingArena.close();
    }
}
```

Yeah... it's not a true allocator because it doesn't manage memory itself. That is something you can't really do because the actual allocation of memory is closed off to the outside. When you create your own arena you are always wrapping an existing Arena like `ofConfined`. With that being said, let's see how we can break free from these limitations and manage the memory ourselves.

## Native Memory allocation methods

When you need more control, you can opt to use the allocation methods provided by C. Meaning that you use methods like `malloc`, `calloc` and `free` just as you would in C, but without leaving Java! This gives you the most control over the memory lifetimes. The downside is that you have to manage the `MemorySegment` yourself, meaning there is a chance that you go out of bounds, or introduce a memory leak if you forget to free a segment.

### Using Malloc and Free

To use `malloc` and `free` you basically do the same thing as with any other downcall you want to make. You use the `Linker` and create the `downcallHandle` for each C method.

```java
Linker linker = Linker.nativeLinker();

malloc = linker.downcallHandle(
    linker.defaultLookup().find("malloc").orElseThrow(),
    FunctionDescriptor.of(ADDRESS, JAVA_LONG)
);

MethodHandle free = linker.downcallHandle(
    linker.defaultLookup().find("free").orElseThrow(),
    FunctionDescriptor.ofVoid(JAVA_LONG)
);
```

Now all you need to do is call these methods to allocate and free memory. In the following example you can see how these downcalls are used.

```java
MemorySegment segment =  ((MemorySegment) malloc.invokeExact(size)).reinterpret(size);
free.invokeExact(segment.address());
```

The return value of malloc is cast to a `MemorySegment`. This segment now has the correct address but a size of zero... it is basically a pointer. So we have to call the reinterpret method to set the correct size and make it usable. The second line in the example frees the memory that was just allocated.

Small side note: You don't have to use `MemorySegment`s if you want to work with addresses as `long`s and pass those around in your code; that is also totally fine. It saves you from having to create two `MemorySegment` instances. The `malloc` call itself and `reinterpret` both create a new `MemorySegment` instance. If you want to work with a `long`, you need to use this descriptor for malloc: `FunctionDescriptor.of(JAVA_LONG, JAVA_LONG)`.

## Pool of reusable memory

Memory pools in this context aren't provided by a specific built-in FFM API class. Instead, they are an architectural pattern you implement yourself. The two most common types are:

* **Segment Queues/Stacks**: Pre-allocating a fixed number of identically sized MemorySegment objects and holding them in a thread-safe data structure (like an ArrayBlockingQueue or ConcurrentLinkedQueue).
* **Large Block Managers**: Allocating one massive MemorySegment upfront and writing custom logic to hand out logical slices of it to requesters, tracking which offsets are free or in use.

### Why you would use them

Native allocation (malloc or system-level calls behind Arena creation) is relatively slow. Creating Java object wrappers like MemorySegment for every single native allocation generates garbage for the GC to clean up. If you are writing high-frequency, low-latency code (like packet processing or using IO_uring!), you want to avoid hitting the OS memory allocator repeatedly. A pool allows you to pay the allocation cost once at startup and reuse the memory indefinitely.

### How to use a memory pool

The implementation depends on your needs, but a basic fixed-size pool could look like this.

```java
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SegmentPool implements AutoCloseable {
    private final Arena arena;
    private final Queue availableSegments;

    public SegmentPool(int poolSize, long segmentSize) {
        this.arena = Arena.ofShared();
        this.availableSegments = new ConcurrentLinkedQueue();
        for (int i = 0; i < poolSize; i++) {
            availableSegments.offer(arena.allocate(segmentSize));
        }
    }

    public MemorySegment borrow() {
        MemorySegment segment = availableSegments.poll();
        if (segment == null) {
            throw new OutOfMemoryError("Pool is exhausted");
        }
        return segment;
    }

    public void returnSegment(MemorySegment segment) {
        // Optional: Zero out the memory before returning it
        segment.fill((byte) 0);
        availableSegments.offer(segment);
    }

    @Override
    public void close() {
        arena.close();
    }
}
```

We are trading complexity and memory usage for performance. This pool is holding onto memory even when you aren't actively using it, and you have to trust your application logic to actually return the segments to the pool to avoid running out of memory. The example uses a `ConcurrentLinkedQueue`. This isn't necessarily the most performant way, but can work. The most performant way totally depends on your use case.

## Slicing

Slicing is taking an existing, already-allocated block of memory and working with a section of it. The Memory API gives you a few ways to do this:

* MemorySegment.asSlice(offset, size)
* SegmentAllocator.slicingAllocator(MemorySegment)
* Working with pure Offsets.

Slicing is useful when you have a block of memory and you want to pass a specific chunk of it to a method without giving that method access to the entire parent segment. Using a slicing allocator is good for when you want to allocate several smaller segments sequentially out of one large backing segment without the cost of separate native allocations. The downside is that you can only free the whole block of memory, not the individual slices.

### How to use them

Using `asSlice` creates a new `MemorySegment` instance that acts as a view into the original memory. It shares the same lifetime scope as the parent, but operates within smaller, strictly defined spatial bounds.

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment parent = arena.allocate(100);

    // Create a slice starting at byte 10, with a length of 20 bytes
    MemorySegment child = parent.asSlice(10, 20);
}
```

You could also use a slicing allocator to hand out segments sequentially:

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment block = arena.allocate(1024);
    SegmentAllocator allocator = SegmentAllocator.slicingAllocator(block);

    // Allocates the first 8 bytes of the block
    MemorySegment firstLong = allocator.allocate(ValueLayout.JAVA_LONG);

    // Allocates the next 4 bytes
    MemorySegment nextInt = allocator.allocate(ValueLayout.JAVA_INT);
}
```

The trade off with `asSlice` and `slicingAllocator` is that they still instantiate Java `MemorySegment` objects, causing GC pressure. If you want zero allocation overhead, just calculate and pass the `long` offsets manually, in essence it is just adding up two `long` values. At that point you are doing [pointer arithmetic in Java](https://davidvlijmincx.com/posts/pointer-arithmetic-in-java/).

## TL;DR

|           Strategy            | Allocation Speed |               Lifetime / Safety                |                              Best Used For                              |
|-------------------------------|------------------|------------------------------------------------|-------------------------------------------------------------------------|
| **Arena.ofConfined / Shared** | Moderate         | Deterministic / Safe (bounds \& scope checked) | Standard off-heap usage. Short-to-medium lifespan tasks.                |
| **Arena.ofAuto**              | Moderate         | Non-deterministic (tied to GC)                 | When you want native memory but prefer the GC to handle cleanup.        |
| **Arena.global**              | Moderate         | Lives until JVM exit                           | Static native data, application-wide lookup tables.                     |
| **Manual Malloc / Free**      | Fast (C-level)   | Manual / Unsafe (leaks \& crashes possible)    | Maximum control, integrating with C libraries that expect manual frees. |
| **Memory Pools**              | Very Fast        | Manual (must return segments)                  | High-frequency allocations, avoiding OS overhead and GC pressure.       |
| **Slicing Allocator**         | Very Fast        | Bound to parent segment                        | Breaking up large allocations, parsing sequential structs.              |

## Conclusion

Managing native memory in Java used to be a hassle, but the Memory API makes it standard and manageable. The built-in `Arena` classes provide a balance between safety and control, catching out-of-bounds access and handling cleanup for you based on the scope. When those don't fit your use case, you can bypass them entirely and use `malloc` calls or build your own memory pools to cut down on allocation overhead.

Just remember that moving data off-heap introduces complexity, and dropping down to manual memory management means you take on the risk of the errors that come with it, like messing up your bounds or forgetting to free a segment. Stick to the standard `Arena` unless your profiling shows you actually need the raw performance of a custom pool or something like `malloc`.
