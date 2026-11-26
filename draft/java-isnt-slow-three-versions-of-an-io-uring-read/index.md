---
title: "Java Isn't a Slow Language: Three Versions of an io_uring Read"
date: ""
description: "Virtual threads pinned on file I/O, so I went to io_uring through liburing, then to the ring itself. Java ends up level with fio and Seastar."
authors: ["david-vlijmincx"]
categories:
  - "Java Core"
  - "Performance"
  - "Project Panama"
image: "featured.jpg"
---
<!--more-->

Virtual threads were meant to make blocking code cheap again. For sockets they do. For files they don't.

Three versions of the same read follow. The first one pinned a carrier thread. The second worked, but every request went through a C library. The third writes straight into a shared ring and ends up competing with Fio and Seastar.

I'm writing this down as kind of a journey, and to show you what lessons I learned from it and wanted to share those.

## Version 1: the read that pins virtual threads

A file is not a socket. There's no epoll for a regular file. A socket becomes readable, you get an event, and the virtual thread parks while another one runs. A file doesn't do that. When a virtual thread calls `read` on a local file, the syscall blocks the OS thread underneath it, and the JVM can't unmount a thread sitting inside the kernel. The carrier is gone until the drive answers.

Loom's answer is to add carriers. `jdk.virtualThreadScheduler.maxPoolSize` defaults to 256, so the pool grows when carriers are pinned. On a machine with all the cores dedicated for file I/O that works. But it also gets the context switching and cache pollution that come with them.

So you start measuring and the first result wasn't the one I hoped for. At Loom's defaults, `FileChannel` and a io_uring facade perform about the same 445,000 against 441,000 reads per second. The carrier pool is doing exactly what it was designed to do, and if you're not CPU-constrained, plain `FileChannel` is fine. Keep in mind this is 256 platform threads to hit the max of a single NVMe drive. Each eating into memory and cores that could be used differently. 

So I wanted to level the playing field because I didn't wanna use so many threads. So I capped the platform threads to four. Doing so gave me these results:

| Platform-thread budget | `FileChannel` | `io_uring facade` |
|------------------------|---------------|------------------|
| 4 threads              | 43,080 ops/s  | 452,193 ops/s    |

Same device, same file, same 4 KiB random reads. One version keeps the drive at its ceiling with two carriers and two pollers. The other gets about a tenth of that, because every in-flight read needs a carrier of its own. So the goal became to hit the ceiling of the drive with as few resources as possible.

## Version 2: io_uring through liburing

Linux has an interface built for exactly to make file I/O async. `io_uring` gives the app and the kernel two shared ring buffers, one for requests and one for completions. You write a request into the submission ring, ring a doorbell, and pick the result up from the completion ring later. One thread can keep hundreds of reads in flight, and it never sits inside the kernel waiting for data to be ready.

I didn't want to write JNI, and by then Project Panama was in preview and I could use it so call C instead. So I wrote bindings for liburing and drove the ring through it. That worked. The facade ran blocking calls on virtual threads while a poller thread submitted and reaped, and the callers parked instead of pinning. It was a bit faster than `FileChannel` in the scenarios I was measuring, and it was the first time file I/O on virtual threads actually did not pin.

That version carried me for a while. Then I wanted the numbers to be good, not just better, so I started profiling.

## Why version 2 wasn't enough

In hindsight, It's pretty clear, but the code was simply doing too much work for each request.

The first issue was allocations. A call needed an `Arena`, an aligned `MemorySegment` for the buffer, a result instance, and a `CompletableFuture` to park on. The FFM API makes that easy to write, all while feeling very Java-like. The price is that you end up managing all of it yourself.

| Allocation               | Time per call |
|--------------------------|---------------|
| `malloc` + `free`, 4 KiB | 49 ns         |
| `arena.allocate`, 4 KiB  | 113 ns        |
| `arena.allocate`, 64 KiB | 530 ns        |

The second was method calls. Preparing one read through liburing goes through `io_uring_get_sqe`, `io_uring_prep_read`, `io_uring_sqe_set_data` and then `io_uring_submit`. That's three Java-to-native transitions to prepare a request, and a fourth to submit it. I optimized all of them. I made the handles static final, I tried using records with proxies. I marked the fast ones critical. I wrote C wrappers that did three calls in one, and I managed the buffers with malloc instead of the arena. A `critical(true)` handle invoked through a static final `invokeExact` costs 2.62 ns, so the transitions themselves weren't the problem.

The thing was that was trying to make calls to C faster instead of making fewer calls. liburing is a C library. Its API is one function call per request. In Java, a function call to C is a boundary crossing. And every one of those calls was doing something the ring could have done with a store.

I went down that road for a while, and it got a little faster with each change, but finding a "better" approach became harder and harder. So I had to go into a different direction. Function call can't compete with a store.

## Version 3: driving the ring from Java

The ring is memory and how you communicate with IO Uring. The kernel and your process share two rings with four counters between them, and the kernel asks you to follow a simple publish/consume protocol. If you can map that memory and write to it, you don't need a library in front of it.

The FFM API can do exactly that. `mmap` gives you an address, a `MemorySegment` gives you typed access to it, and a `VarHandle` gives you the memory ordering. So the third version maps the rings itself.

`io_uring_setup` returns a file descriptor and fills in a struct of byte offsets. The first thing you do is map three regions. The submission ring, the completion ring, and the array of 64-byte submission entries.

From there, preparing a read is six stores into the 64-byte entry: the opcode, the file descriptor, the file offset, the buffer address, the length and the `user_data`. Then you publish it by writing the slot index into the submission array and bumping the tail with a release store. Then one `io_uring_enter` submits the whole batch. In the example below, the entry is prepared and published in eight stores.

```java
int index = tail & ringMask;
long sqe = sqesAddress + (long) index * 64;

VH_BYTE.set(GLOBAL, sqe + 0, (byte) 22);   // IORING_OP_READ
VH_INT.set(GLOBAL, sqe + 4, fd);
VH_LONG.set(GLOBAL, sqe + 8, fileOffset);
VH_LONG.set(GLOBAL, sqe + 16, buffer.address());
VH_INT.set(GLOBAL, sqe + 24, (int) buffer.byteSize());
VH_LONG.set(GLOBAL, sqe + 32, index);      // user_data

VH_INT.set(GLOBAL, sqArrayAddress + (long) index * 4, index);
VH_INT.setRelease(GLOBAL, sqTailAddress, tail + 1);
```

No native call per request. No allocation per request. The buffers are registered up front and pinned in the kernel. The file descriptors are registered too. The only syscall left on the hot path is the one that tells the kernel there's work.

That was the biggest performance improvement. From asking C to prepare a request to writing the request into memory the kernel already had.

## Making it correct was harder than making it fast

Now I owned the rings, which meant I now owned the ordering too. The kernel reads memory that I write, so every write has to be visible before the tail that publishes it, and every completion has to be read before I hand the slot back. The publish side uses a release store on the tail. The completion side does an acquire on the tail, reads `user_data` and `res`, then a release store on the head. Get the order wrong and the kernel reads a half-written request. On x86 that might work for weeks. On ARM less so.

That's where jcstress comes in. A separate profile drives the real `MpscQueue` through concurrent offers and a single consumer. It checks the store/load orderings the wake path depends on, along with publish/consume fences and slot handoff. It doesn't prove the whole library correct. But it should give confidence that the memory ordering for the handoff is correct.

## Making sure the numbers were real

I didn't want to publish a number I couldn't reproduce. Which is a lot harder than you think. The Java-side measurements are JMH benchmarks, and the comparison against fio and Seastar is a small project on its own (Thanks AI for the plumbing). Each read row is five repetitions in a fresh process, and the engine order rotates between repetitions. The input file is a 10 GB aged file opened with `O_DIRECT`, and the ratio between engines is computed per pair, not between two averages from different runs. Then the intervals.

The write path is special. 130 write runs over almost nine hours, and 36 of them hit the drive's Pseudo-SLC cliff mid-run. I tried different ways that measure all three engines inside one preconditioned state, and two of seven runs were valid, which I didn't find stable enough to publish. All three engines hit the same highs and lows at different times. Fio, for example, would do 95k IOPS one run and 250K IOPS the next try. 

## How fast it is

These are single-host numbers: 16 cores, 1 TB PCIe 4.0 NVMe, kernel 7.0.12, OpenJDK 26. Reads use the 10 GiB aged file, fio runs its io_uring engine, and Seastar is the C++ framework I used as reference.

| Workload                            | JUring      | `fio` (io_uring) | Seastar     |
|-------------------------------------|-------------|------------------|-------------|
| 4 KiB random read, 64 in flight     | 318k IOPS   | 302k IOPS        | 316k IOPS   |
| 4 KiB random read, 128 in flight    | 412k IOPS   | 342k IOPS        | 391k IOPS   |
| 1 MiB sequential read, 32 in flight | 4,720 MiB/s | 4,647 MiB/s      | 4,632 MiB/s |
| 8 rings, 4 KiB random read          | 473k IOPS   | 459k IOPS        | 466k IOPS   |

At 64 in-flight it's 5.4% ahead of fio and on the same level as Seastar. At 128 it's 20.6% ahead of fio and 5.4% ahead of Seastar. The sequential row is within 2% of both, which is what the drive does, not what the engine does.

The cycle counts are the part I care about more. At 64 in flight, JUring uses about 11,300 CPU cycles per operation against 15,200 for fio.

All this sounds great, but in the end this is a capability comparison, because fio can't set my three ring flags and Seastar can't use registered files or taskrun flags. And at 128 in flight the p99 is about 919 µs against 714 µs for fio, even though the median is lower. This performance journey is not over yet. 

## What this says about Java
When I started, I assumed that if Java ever got close to C on this workload, it would be because I either made a mistake in the benchmark or Java just being really good.

The third version is fast because nothing in the hot path is Java-specific. The ring is memory, the access is a `VarHandle`, the batching is one syscall, and the JIT working its magic on the stores. It was just that all my abstractions I used were in the way. Funny enough, During the rewrites it did feel like I was writing C using Java.

So Java isn't a slow language. It's a language that makes it very easy to put things between you and the machine and then gets blamed for its performance. The FFM API is the first time I could reach the machine from Java and have a great time doing so.

## Conclusion
The three versions are all in the same repository history, and the third one is the one I use. It isn't the right tool for every file read. At a queue depth of one it's as fast as Filechannel. In the end The FFM api opens the door to a lot of these great C libraries.

If you want to see the rest, the library is on [GitHub](https://github.com/davidtos/JUring) and on Maven Central as `io.github.davidtos:juring:0.1.0`. The benchmark harness, the data are in [BENCHMARK.md](https://github.com/davidtos/JUring/blob/master/BENCHMARK.md).
