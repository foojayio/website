---
title: "Async file IO with Java and io_uring"
date: "2025-04-18T09:28:16+00:00"
lastmod: "2025-04-18T09:30:17+00:00"
description: "Using Java together with Project Panama and virtual threads to create async file IO that is also very performant."
canonical: "https://davidvlijmincx.com/posts/async-io-with-java-and-panama/"
authors:
  - "david-vlijmincx"
image: "async.png"
categories:
  - "Java"
  - "Java Core"
  - "Project Panama"
related_posts:
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "using-async-profiler-and-jattach-programmatically-with-ap-loader"
  - "asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm"
  - "couldnt-we-just-use-asyncgetcalltrace-in-a-separate-thread"
frozen: false
---

When I first started exploring Virtual Threads in Java, I wanted to understand everything about them like, performance characteristics, when they yield, and limitations. This journey led me to an interesting challenge about file I/O operations. These operations cause Virtual Threads to become "pinned" to platform threads, limiting their effectiveness for I/O-heavy applications.

This pinning issue has been widely acknowledged across the Java community. It's mentioned in the Virtual Threads [JEP](https://openjdk.org/jeps/425), discussed on [Reddit threads](https://www.reddit.com/r/java/comments/1h0cr5g/comment/lz3lv11/), debated on [mailing lists](https://mail.openjdk.org/pipermail/loom-dev/2024-June/006638.html), and highlighted in the "[State of Loom](https://cr.openjdk.org/~rpressler/loom/loom/sol1_part1.html)". The consistent message: File I/O and Virtual Threads don't play nicely together.

The [consensus](https://youtu.be/bxSE7lXIlJI?si=ZYnjrnThjr9GkseK&t=969) is that this shouldn't be a problem because disk access is "fast enough." But is it? When accessing remote filesystems or handling thousands of concurrent I/O operations, those milliseconds add up quickly. Java's inability to differentiate between local and remote filesystems creates an opportunity for improvement.

I'll share my expedition into solving this challenge by combining project Panama for native interoperability and Linux's io_uring for high-performance asynchronous I/O. I'll walk through how I built a bridge between Java's Virtual Threads and io_uring, transforming blocking I/O operations into yielding ones.

If you're building high-throughput web servers, data processing pipelines, or any application with intensive I/O requirements, this approach might be the missing piece in fully realizing the potential of Virtual Threads.

## What is Panama

Project Panama is Java's modern approach to native interoperability, released as a standard feature in Java 22. It offers an alternative to JNI with a more elegant and safe API for interacting with native code and memory. Panama's API provides us developers with the performance benefits of native code while maintaining Java's safety and usability characteristics.

At its core, Panama solves a fundamental problem that Java developers have faced for decades: how to efficiently interact with code and data outside of Java while maintaining Java's safety guarantees. Before Panama, developers relied on JNI, which required writing C/C++ code, compiling shared libraries. JNI development was error-prone and difficult to debug, creating a significant cognitive disconnect from Java's usual programming model.

For applications that need to leverage native libraries or system capabilities, Panama transforms what was a dreaded necessity into a natural extension of Java development. This transformation is especially relevant for domains like high-performance computing, machine learning, and the focus of this exploration of high-performance I/O operations. Let's break down Panama's capabilities into its core components: memory management and function calling, to understand how they enable our io_uring integration.

## Managing memory

Memory management represents one of the most significant challenges when interacting with native code. Panama addresses this through an approach centered around the use of Arenas.

For Java applications, you rarely have to think about memory management thanks to the garbage collector. When working with native code, however, memory must be explicitly allocated and freed. This manual management creates numerous opportunities for errors like memory leaks or accessing freed memory.

Panama's Arena API provides deterministic resource management for off-heap memory. When an Arena closes, all memory segments allocated it created are deallocated. This approach aligns with Java's resource management patterns, making it intuitive for Java developers. All with a try-with-resource statement.

Different Arena implementations accommodate various usage patterns. The confined Arena ensures single-thread access. The shared Arena allows multiple threads to access memory segments. The global Arena addresses cases where memory segments need to outlive their creating threads. The Auto Arena uses the Garbage collector to deallocate segments. The confined and shared arena offer an more explicit lifetime than global and auto. Creating an Arena can be done as is shown in the following example.

```java
try(Arena arena = Arena.ofConfined()){
    MemorySegment memorySegment = arena.allocate(1024);
}
```

The `Arena` is active inside the try-with-resource statement. This means the MemorySegments created inside of it are only available inside that scope and will be freed when the arena is closed. MemorySegments serve as Panama's primary abstraction for working with off-heap memory, representing a contiguous region of memory with well-defined boundaries and lifecycle. Unlike direct ByteBuffers, MemorySegments are explicitly bound to an Arena, creating a clear ownership model.

For structured data, Panama provides the MemoryLayout API to describe complex memory organizations including structs, arrays, and unions. This enables safe manipulation of complex data structures without resorting to error prone pointer arithmetic. In the following example a `StructLayout` is created with different kinds of values. The values can be accessed using a `VarHandle`.

```java
AddressLayout C_POINTER = ValueLayout.ADDRESS
        .withTargetLayout(MemoryLayout.sequenceLayout(Long.MAX_VALUE, JAVA_BYTE));

StructLayout requestLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_LONG.withName("id"),
                C_POINTER.withName("buffer"),
                ValueLayout.JAVA_INT.withName("fd"),
                ValueLayout.JAVA_BOOLEAN.withName("read"))
        .withName("request");

VarHandle idHandle = requestLayout.varHandle(MemoryLayout.PathElement.groupElement("id"));
```

The integration of these concepts creates an approach to memory management that maintains Java's safety guarantees while providing the flexibility needed for native interoperability.

## Making calls up and down

Panama's approach to function calls builds upon Java's existing metaprogramming features like Method handles to create a bridge between Java methods and native functions. The API distinguishes between downcalls (Java calling native functions) and upcalls (native code calling back into Java methods).

For downcalls, Panama's Linker API creates method handles that invoke native functions. This process begins with obtaining a SymbolLookup to locate native symbols within libraries. Once found, the Linker transforms these symbols into method handles using FunctionDescriptors that specify parameter and return types.

```java
MethodHandle malloc = linker.downcallHandle(
        linker.defaultLookup().find("malloc").orElseThrow(),
        FunctionDescriptor.of(ADDRESS, JAVA_LONG)
);

malloc.invokeExact(size);
```

This description-based approach eliminates the verbose boilerplate associated with JNI. Instead of writing C code to bridge between Java and native functions, developers describe the function signature using Java code. This not only reduces error potential but also enables better tooling support. You will get an exception if you call the method with incorrect typing.

Upcalls follow a similar pattern but in reverse, with Java methods wrapped as function pointers that native code can invoke. This capability is particularly valuable when working with callback-based APIs, where native code needs to notify Java about events or completion status. In the following example, a down call and an up call are made for the native signal method.

```java
public static void main(String[] args) throws Throwable {

    final var linker = Linker.nativeLinker();

    // up call
    // describes the Java method
    MethodHandle handleSignal = MethodHandles.lookup()
            .findStatic(UpCallExample.class,
                    "handleSignal",
                    MethodType.methodType(void.class, int.class));
    // Signature of the java method
    FunctionDescriptor signalDescriptor = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT);

    // The stub to be passed to the native code
    MemorySegment handlerFunc = linker.upcallStub(handleSignal,
            signalDescriptor,
            Arena.ofAuto());

    // down call
    MethodHandle signal = linker.downcallHandle(
            linker.defaultLookup().find("signal").orElseThrow(),
            FunctionDescriptor.ofVoid(JAVA_INT, ADDRESS)
    );

    signal.invoke(2, handlerFunc);
}

static void handleSignal(int signal) {
    System.out.println("Received signal: " + signal);
}
```

Both upcalls and downcalls benefit from Panama's unified type mapping system, which automatically handles conversion between Java and native types. When passing complex data structures to native functions, developers can use MemoryLayout to describe the structure and MemorySegment to allocate memory. What is great about this approach is that it is a complete shift from JNI. Rather than treating native code as a separate environment with its own rules, Panama extends Java's programming model to more natural interaction with native code. Now that we understand how Panama enables safe and efficient native interoperability, let's examine how it can be used with io_uring.

## What is io_uring

IO_uring is a Linux kernel interface that improves how applications perform input/output operations. At its core, it establishes a shared memory communication channel between applications and the kernel using two ring buffers: one for requests and another for completions.

Applications submit multiple I/O operations (in batches) to the submission ring without making individual system calls for each operation. Applications can continue executing other tasks while I/O operations complete asynchronously, with results delivered through the completion queue.

It addresses the performance limitations of traditional I/O methods by eliminating per-operation system calls and context switches, enabling true asynchronous operation for all types of I/O, and dramatically reducing overhead under high concurrency. This makes it particularly valuable for applications that need to handle thousands of concurrent I/O operations efficiently.

The relationship between io_uring and Virtual Threads is particularly interesting. Virtual Threads excel at managing concurrent tasks but can become pinned during blocking I/O operations. IO_uring's asynchronous nature addresses this limitation, allowing Virtual Threads to yield during I/O operations rather than remaining pinned to carrier threads. To make these concepts concrete, let's implement a basic file read operation using io_uring through Panama. This example will demonstrate the essential pattern that supports more complex I/O operations.

## Single read with Java and Uring

Implementing a basic file read operation with io_uring through Panama illustrates how these technologies work together to create an alternative for Java's file I/O. The following application creates a bridge between Java and io_uring native library.

The implementation involves initializing an io_uring instance, preparing a read request, submitting it to the kernel, and checking for completion. Throughout this process, Panama provides memory management for buffers, function calling, and type mapping between Java and native structures.

The following pattern demonstrates how we bridge Java and io_uring through Panama to perform a read operation. First, we initialize the io_uring instance and allocate memory for our buffer within a confined Arena to ensure proper resource management. We then prepare a Submission Queue Entry (SQE) that describes our read operation, including the file descriptor, buffer location, size, and offset. After submitting this request to io_uring with io_uring_submit, we wait for the operation to complete using io_uring_wait_cqe. This is a blocking method that will wait for a completion. Once completed, we can extract the result from the Completion Queue Entry (CQE) and process the data that was read into our buffer. Finally, we mark the completion as seen and let the Arena's close method cleanup the memory segments. This basic pattern forms the foundation for more complex I/O operations while maintaining both Java's safety guarantees and io_uring's performance benefits.

```java
try (Arena arena = Arena.ofConfined()) {
    // Create and initialize the ring
    MemorySegment ring = arena.allocate(ring_layout);
    int ret = (int) io_uring_queue_init.invokeExact(10, ring, 0);

    // Prepare file descriptor and buffer
    int fd = (int) open.invokeExact(MemorySegment.ofArray("read_file".getBytes()), 0, 0);
    MemorySegment buffer = arena.allocate(1024);

    // Prepare read operation
    MemorySegment sqe = (MemorySegment) io_uring_get_sqe.invokeExact(ring);
    io_uring_prep_read.invokeExact(sqe, fd, buffer, buffer.byteSize(), 0L);

    // Submit to uring
    ret = (int) io_uring_submit.invokeExact(ring);

    // Wait for and process completion
    MemorySegment cqePtr = arena.allocate(ValueLayout.ADDRESS);
    ret = (int) io_uring_wait_cqe.invokeExact(ring, cqePtr);

    // print the result
    System.out.println(buffer.getString(0));

    // Process data and cleanup
    io_uring_cqe_seen.invokeExact(ring, cqePtr);
}
```

For applications that perform numerous read operations, such as web servers, and databases this integration delivers potential performance improvements by reducing system call overhead and enabling asynchronous I/O. Another benefit is that io_uring supports sockets, streamlining the process of reading data and sending it to clients without copying data unnecessarily. While this basic implementation works, we can further optimize it by examining some of the performance characteristics and challenges when bridging Java and native code.

## Performance improvements

Using a native library on its own can be beneficial and provide performance improvements. To get more speed out of the bindings we need to take a closer look at three patterns for memory allocation. The first speed-up is for applications doing lots of allocations.

When an Arena allocates memory using the allocateSegment function you get a continuous region of memory that is filled with zeroes. This is a safe segment of memory the application can use. However, the actual creation of a segment is quite an expensive operation compared to using `malloc` or `calloc`. These two native methods are a lot quicker when you need a continuous region of memory. To show you how much faster I created a benchmark method that allocated an N number of bytes and copied a string of N length into that segment. In the following output, you can see how much faster these native methods are.

```java
A higher score is better
Benchmark                                     (memory size)  Mode        Score                 Units
memoryAllocation.MemoryAllocation.arena                  64  thrpt    5  17388.133 ± 1075.514  ops/ms
memoryAllocation.MemoryAllocation.arena                 512  thrpt    5  13741.243 ± 1638.346  ops/ms
memoryAllocation.MemoryAllocation.arena                1024  thrpt    5   9068.026 ±  247.300  ops/ms
memoryAllocation.MemoryAllocation.arena                4096  thrpt    5   4258.966 ±    7.806  ops/ms
memoryAllocation.MemoryAllocation.arena               16384  thrpt    5   1223.211 ±   33.024  ops/ms
memoryAllocation.MemoryAllocation.arena               32768  thrpt    5    601.107 ±  116.020  ops/ms

memoryAllocation.MemoryAllocation.arenaAllocateFrom      64  thrpt    5  26621.262 ±   62.267  ops/ms
memoryAllocation.MemoryAllocation.arenaAllocateFrom     512  thrpt    5  18598.487 ±  104.566  ops/ms
memoryAllocation.MemoryAllocation.arenaAllocateFrom    1024  thrpt    5  17287.260 ±  389.436  ops/ms
memoryAllocation.MemoryAllocation.arenaAllocateFrom    4096  thrpt    5   6049.841 ±    7.588  ops/ms
memoryAllocation.MemoryAllocation.arenaAllocateFrom   16384  thrpt    5   2223.748 ±  174.010  ops/ms
memoryAllocation.MemoryAllocation.arenaAllocateFrom   32768  thrpt    5   1036.138 ±  100.833  ops/ms

memoryAllocation.MemoryAllocation.calloc                 64  thrpt    5  33741.486 ±  471.490  ops/ms
memoryAllocation.MemoryAllocation.calloc                512  thrpt    5  23376.158 ±  142.451  ops/ms
memoryAllocation.MemoryAllocation.calloc               1024  thrpt    5  19720.376 ±  734.219  ops/ms
memoryAllocation.MemoryAllocation.calloc               4096  thrpt    5   8264.018 ±  551.130  ops/ms
memoryAllocation.MemoryAllocation.calloc              16384  thrpt    5   3187.009 ±  835.737  ops/ms
memoryAllocation.MemoryAllocation.calloc              32768  thrpt    5   1122.987 ±   57.211  ops/ms

memoryAllocation.MemoryAllocation.malloc                 64  thrpt    5  65202.883 ±  113.718  ops/ms
memoryAllocation.MemoryAllocation.malloc                512  thrpt    5  49989.738 ± 2578.109  ops/ms
memoryAllocation.MemoryAllocation.malloc               1024  thrpt    5  39568.303 ±   47.276  ops/ms
memoryAllocation.MemoryAllocation.malloc               4096  thrpt    5  10834.230 ±  159.482  ops/ms
memoryAllocation.MemoryAllocation.malloc              16384  thrpt    5   4842.788 ±   30.092  ops/ms
memoryAllocation.MemoryAllocation.malloc              32768  thrpt    5   1917.703 ±  302.428  ops/ms
```

As you can see from the output generated by JMH benchmarks using native methods can allocate memory faster than an Arena can. If your use case requires the lots of memory allocation it can be beneficial to switch to native methods.

There are two ways to use this native memory. We can tie its lifetime to an arena, or you can manage the lifetime of these segments yourself and free them when necessary. The first option gives you more safety while the latter gives you more control over resources. Calling free() with a memorySegment as parameter is easy enough, so let's explore how we can use malloc and calloc in a safe way using the Arenas that project Panama provides.

```java
public class AllocArena implements Arena {

    private static final MethodHandle malloc;
    private static final MethodHandle calloc;
    private static final MethodHandle free;

    static {
        Linker linker = Linker.nativeLinker();

        malloc = linker.downcallHandle(linker.defaultLookup().find("malloc").orElseThrow(),
                FunctionDescriptor.of(ADDRESS, JAVA_LONG));
        free = linker.downcallHandle(linker.defaultLookup().find("free").orElseThrow(),
                FunctionDescriptor.ofVoid(ADDRESS));
    }

    final Arena arena = Arena.ofConfined();

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        return ((MemorySegment) malloc.invokeExact(size)).reinterpret(size, arena, m -> free(m));
    }

    @Override
    public MemorySegment.Scope scope() {
        return arena.scope();
    }

    @Override
    public void close() {
        arena.close();
    }
}
```

The arena overrides the allocation method. Doing so we can use malloc or calloc to allocate a memory segment. Calloc and malloc return a pointer to a contiguous region of memory. this pointer is an MemorySegment with a zero length, so we can't use it directly. To make it usable we have to call reinterpret which takes as a parameter the new size, and optionally an arena and cleaning method. From the client's perspective, this arena doesn't look or behave differently, just the allocation happens using malloc.

Using Native memory is not the only way to pass values to native code. There is also the option to pass the address of heap-backed memory to native code. The benefit of doing so is that it saves you from doing a memory allocation but also having to manage that segment's lifetime. All of this responsibility is handed to Java. To be able to pass heap-backed memory to a native call you need to mark a method as critical using the Linker. This option is only meant for calls that have a very short lifetime. It's also not meant to be used to create callbacks. What happens behind the scenes is that Java will create a temporary address for that value and pass it to the native code. In the following example, you see how to mark methods as critical and use them.

```java
open = linker.downcallHandle(
        linker.defaultLookup().find("open").orElseThrow(),
        FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_INT),
        Linker.Option.critical(true));

open.invokeExact(MemorySegment.ofArray((filePath).getBytes()), flags, mode);
```

Using `Linker.Option.critical(true)` enables the application to use heap memory. The `invokeExact` method creates a heap-backed memorySegment of a String. This should really only be used for methods that are so fast that copying data really hurts performance.

There are several ways to speed up memory segment allocation but the best way is to reuse memory that you have already allocated. The risk is accessing previous values, if you are not careful. The benefit is the almost free performance gains. Allocating memory will always have a cost and so will be turning that memory into a memorySegment. This can all be prevented by reusing segments. The easiest/safest way is to reuse memory that holds pointers, this is an easy way to start and learn how to do it safely. In the end, it looks almost the same as Java. Beyond memory optimization, our primary goal was addressing the Virtual Thread pinning issue. Let's explore how we can transform blocking operations into yielding operations to preserve how Virtual Thread work.

## Turning pinning into yielding

Virtual Threads face a significant limitation with file I/O operations: pinning. When a Virtual Thread performs blocking operations, including file I/O, it becomes "pinned" to its carrier thread, preventing that carrier thread from executing other Virtual Threads. This pinning effectively negates one of the primary benefits of the Virtual Thread model. More carrier threads get created if this happens, to prevent a degradation in performance. Making native calls is not great either as those will pin the virtual thread as well. So let's see how we can prevent pinning in the first place.

This only works for async workloads, but the trick to prevent pinning is to yield a virtual thread before making a pinning call. The virtual threads will yield and an event loop running in another thread will wake the virtual threads up. To get this behaviour you need a polling thread for the completion queue and futures. The future will block the virtual thread till the polling threads wake it up with a result. The next example shows you the essence of what is needed for this behavior.

```java
public final class BlockingReadResult {

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private MemorySegment buffer;

    BlockingReadResult(long id) {
        super(id);
    }

    public MemorySegment getBuffer() {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    // code continues
```

The code executed by the virtual thread will call getBuffer yielding the virtual thread on `CompletableFuture.get()`. The thread will now wait for a result from the polling thread. The polling thread that checks if values are available looks like the following example. It's a while loop that will set the result of the `CompletableFuture` allowing the virtual thread to continue.

```java
while (running) {
    final List<Result> results = jUring.peekForBatchResult(100);

    results.forEach(result -> {
        BlockingResult request = requests.remove(result.getId());
        request.setResult(result);
    });
}
```

The approach is pretty straightforward and allows you to yield virtual threads for operations that do not support virtual threads. The upside is that virtual threads will yield, the downside is that you will need an event loop to wake up the threads. If this is beneficial to you application depends on the workload.

## Bringing It All Together

We explored how Panama, io_uring, and Virtual Threads can fundamentally improve Java's approach to I/O operations. By integrating io_uring using Panama, Java applications can leverage Linux's I/O capabilities while maintaining Java's safety and productivity benefits.

This implementation exercise also reveals two distinct approaches to native integration. You can create direct bindings where Java methods map directly to native calls, resulting in a less Java-like but more straightforward API. Alternatively, you can build a facade around the native library, creating a more idiomatic Java API at the cost of additional abstraction.

For most production applications, I recommend the facade approach. It gives you more control over the API and creates a more familiar experience for Java developers. The slight performance overhead is usually worth the improved maintainability and safety.

While this solution focuses on file I/O, the same approach can be applied to other blocking operations that cause Virtual thread to pin. Network I/O, database access, and inter-process communication are all candidates for similar optimization.

As Panama and Virtual Threads continue to mature in future Java releases, we can expect even tighter integration between these technologies, potentially making [solutions like this](https://github.com/openjdk/jdk-sandbox/tree/iouring) part of the standard library. Until then, this approach offers  

a way use io_uring directly with Java. The code used as basis for this article and examples can be found [on Github](https://github.com/davidtos/JUring).
