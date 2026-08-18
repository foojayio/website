---
title: "Your TLS Stack Is Lying to You About Zero-Copy"
slug: "your-tls-stack-is-lying-about-zero-copy"
date: "2026-06-10T12:00:28+00:00"
lastmod: "2026-06-10T14:49:57+00:00"
description: "In a zero-allocation runtime, SSLEngine becomes a structural mismatch. It keeps TLS on the heap-facing side of the JVM..."
canonical: "https://blog.arkstack.dev/en/blog/your-tls-stack-is-lying-about-zero-copy"
authors:
  - "arkadiusz-przychocki"
image: "featured_cover1.png"
categories:
  - "Java"
  - "Java Core"
  - "JEPs"
  - "Performance"
  - "Project Panama"
  - "Tutorials"
tags:
related_posts:
  - "virtual-thread-pinning-field-guide"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
enlighterjs: true
frozen: false
---

The "No Waste Compute" Constraint
---------------------------------

When I started designing the Exeris Kernel, I set one non-negotiable rule very early: no waste compute. That rule sounds like a performance slogan until it starts killing otherwise normal design decisions.

I had already banned `ThreadLocal`, moved context propagation to Scoped Values, and pushed more of the runtime into explicit off-heap ownership. The idea was simple: if the hot path is supposed to stay outside GC pressure, then memory shape and lifetime cannot be treated as incidental details.

Then I reached TLS.

**Constraint upfront:** this is not a universal argument against `SSLEngine`. For a normal Java service it is still a perfectly reasonable choice --- battle-tested and deeply integrated into the ecosystem. This is about a narrower problem: what happens when TLS sits directly on the hot path of a runtime where off-heap ownership, deterministic cleanup, and zero-allocation execution are hard architectural constraints.

In most Java applications, the TLS layer is just part of the stack. It encrypts bytes, hands them off, and usually gets discussed only when certificates break or latency suddenly becomes visible in production. But in a runtime where every byte on the transport path matters, TLS is not a side concern. It is one of the defining execution boundaries. Every request passes through it. Every response passes through it. If that boundary still speaks in heap-facing contracts, then the rest of the runtime is already adapting to the wrong model.

The root issue I found with `SSLEngine` was not that it is slow in the abstract, old, or even mainly that it allocates. The deeper problem is that `SSLEngine` keeps the TLS boundary expressed in terms of JVM-managed buffer objects and heap-visible control flow, while the rest of the runtime is trying very hard to stop doing exactly that.



The Impedance Mismatch in Memory Ownership
------------------------------------------

The failure showed up at the contract level long before any benchmark.

`SSLEngine` is shaped around `ByteBuffer`. You call `wrap(src, dst)` and `unwrap(src, dst)`. You get an `SSLEngineResult` back. You stay inside a model where the TLS boundary is expressed through JVM-owned API objects, even if some of the underlying storage uses direct memory.

That matters because I am not trying to reduce heap pressure only statistically in Exeris; I am trying to define explicit ownership all the way through the hot path. What I wanted from the boundary was strict control: the kernel owns the input memory, the kernel owns the output memory, the kernel controls the lifetime, and the kernel can release native state exactly when it decides the work is done.

What `SSLEngine` gives you is different. It relies on buffer exchange through a JDK object contract and state transitions expressed through JVM return objects. Its cleanup is not shaped around the same explicit ownership model as the rest of the kernel.

In a conventional stack, delayed cleanup is usually acceptable because the whole system already tolerates a lot of deferred work. In an off-heap-first runtime, "cleanup later" is not neutral. It means native TLS state can survive beyond the point where the runtime is logically done with it. Once I noticed this mismatch in ownership semantics clearly, I stopped thinking of `SSLEngine` as a component to tune and started seeing it as a boundary that belonged to the wrong architecture.
![SSLEngine memory contract vs. Exeris Arena ownership model.](https://blog.arkstack.dev/blog/your-tls-stack-is-lying-about-zero-copy/fig1_tls_boundary.png) Figure 1: SSLEngine memory contract vs. Exeris Arena ownership model.



The Netty Question
------------------

I looked at Netty's `OpenSslEngine` directly before committing to the FFM path. It is genuinely fast and battle-tested --- and for many systems it is the right answer. But it operates under a different architectural paradigm.

Netty solves the off-heap problem through pooled buffers and manual reference counting (`retain()` and `release()`). That is a powerful model, but it comes with a structural tax: the ownership semantics inevitably leak into application code, and forgetting to release a buffer creates notoriously difficult memory leaks. It is still a model bridging JVM objects and native memory through a heavy framework abstraction.

With Panama FFM in Exeris, I don't need reference counting. I get deterministic, strict ownership. Memory boundaries are tied to scopes (like `Arena`), meaning the lifecycle of the TLS buffer is statically guaranteed by the runtime, not dynamically managed by developers counting references. The boundary is cleaner, and the cost of maintaining it drops.



Explicit State and FFM
----------------------

To see why this changes the architecture, look at the actual implementation in the Exeris Kernel.

First, I stopped letting the TLS engine silently manage its own lifecycle. In `TlsStateMachine`, the transitions are deterministic and tied to the kernel's execution context, not left to the garbage collector.

```
// Snippet from TlsStateMachine.java (Exeris Kernel)
// State transitions are explicitly modeled and bound to the off-heap lifecycle.

public void advanceState(TlsEvent event) {
    // I enforce strict state progression before any native call is made.
    // There is no ambiguous "maybe it's closed" state lingering on the heap.
    if (currentState == TlsState.HANDSHAKE && event == TlsEvent.APP_DATA) {
        throw new IllegalStateException("Cannot process application data during handshake");
    }
    // ... explicit state handling
}
```


Second, I mapped the actual cryptographic operation directly via Panama's FFM in `OffHeapTlsEngine`. Notice that I am not wrapping heap arrays. I am passing raw memory segments or delegating file descriptors directly to native OpenSSL functions.

```
// Snippet from OffHeapTlsEngine.java (Exeris Kernel)
// Zero-allocation FFM call: the raw off-heap address is passed directly —
// no MemorySegment wrapper object is created on the hot path.

public int writeRaw(MemorySegment sourceSegment) {
    // 1. The memory is off-heap and strictly owned by an Arena.
    // 2. Pass the raw native address (a long) directly via FFM downcall.
    try {
        long srcAddr = sourceSegment.address();
        return SSL_write(sslHandle, srcAddr, (int) sourceSegment.byteSize());
    } catch (Throwable t) {
        throw new TlsNativeException("FFM downcall to SSL_write failed", t);
    }
}
```


The trade-off here is explicit: I lose the safety net of `ByteBuffer` bounds checking and GC cleanup. In return, I gain absolute control over the data path.



What the Exploratory Benchmarks Prove
-------------------------------------

I prefer brutal transparency over carefully curated optimization claims. The native FFM TLS path in Exeris is still taking shape, but the early exploratory JMH results confirm exactly what I expected structurally.

I tested four distinct architectural models:

1. **JDK SSLEngine**: The standard heap-facing boundary (in-memory direct only).
2. **Netty tcnative** : Off-heap via JNI and reference-counted `ByteBuf` (embedded channel pipeline).
3. **Exeris FFM (Memory BIO)**: Native TLS via Panama, where the runtime explicitly owns the memory (in-process).
4. **Exeris FFM (FD Owner)**: The absolute hot path. OpenSSL is bound directly to the socket file descriptor, bypassing intermediate memory buffers entirely (write-loopback).

|   Architecture    |      Memory Boundary      |    Throughput    | Allocation (Per 1KB Record) |
|-------------------|---------------------------|------------------|-----------------------------|
| JDK SSLEngine     | Heap (`ByteBuffer`)       | **\~905k ops/s** | **\~2,528 B/op**            |
| Netty tcnative    | Off-heap (`ByteBuf`)      | **\~856k ops/s** | **\~560 B/op**              |
| Exeris Memory BIO | Off-heap (Panama `Arena`) | **\~922k ops/s** | **0 B/op**                  |
| Exeris FD Owner   | Direct Socket OS boundary | **\~367k ops/s** | **0 B/op**                  |

*(Methodology: JMH `gc` phase, Oracle JDK 26 GA, ZGC, commit [`f778683`](https://github.com/exeris-systems/exeris-benchmarks/commit/f778683bf1d343d0c6a3a595d2d3b754c44a696c), 2026-05-01. The Memory BIO profile phase additionally confirmed via JFR: zero `jdk.GarbageCollection` events recorded --- ZGC never ran a single collection during the entire benchmark run. Full suite in [`exeris-benchmarks`](https://github.com/exeris-systems/exeris-benchmarks).)*
![The data path of Memory BIO vs FD Owner directly binding to the socket descriptor.](https://blog.arkstack.dev/blog/your-tls-stack-is-lying-about-zero-copy/fig2_fd_owner_path.png) Figure 2: The data path of Memory BIO vs FD Owner directly binding to the socket descriptor.

Let's unpack what these numbers actually mean, because context matters more than raw digits.

In a pure in-process memory test, the **Exeris Memory BIO** implementation outpaces both standard `SSLEngine` and Netty's `tcnative`. The runtime achieves \~922,000 ops/s without paying the structural tax of heap-facing buffer exchanges.

But the most important architectural metric is the last row: **Exeris FD Owner**.

A naive reading would ask why the throughput dropped to \~367,000 ops/s. The answer is that the FD Owner benchmark leaves the synthetic in-process memory arena entirely. It writes directly to the OS loopback interface via socket file descriptors. At this stage, I am no longer benchmarking memory copy operations; I am hitting the limits of the OS network stack and syscalls.



The GC Layer and the True Cost of Abstractions
----------------------------------------------

What changed my mind was not the ops/s number. It was what `-prof gc` showed underneath it.

To process a standard 1024-byte payload, `SSLEngine` allocates over **2.5 Kilobytes** of garbage (`gc.alloc.rate.norm`). The TLS layer generates more heap waste than the data it encrypts. I had already pushed the rest of the hot path off-heap --- and the GC profiler was telling me the TLS boundary was quietly undoing that work on every record.

By contrast, the Exeris FFM paths drop the normalized allocation rate to **strict zero**. (The profiler registers \~0.01 B/op with zero actual GC counts, which is standard JMH measurement noise for absolute zero).

This is the core definition of "No Waste Compute." By eliminating the intermediate buffer tier completely, the kernel fundamentally changes the garbage collector's job. It stops doing TLS cleanup entirely. ZGC is no longer forced to clean up after the cryptography layer.
![Allocation rate (garbage generated) per 1KB payload across different TLS architectures.](https://blog.arkstack.dev/blog/your-tls-stack-is-lying-about-zero-copy/fig3_gc_allocation_rate.png) Figure 3: Allocation rate (garbage generated) per 1KB payload across different TLS architectures.



Where SSLEngine Still Wins
--------------------------

A few things remain true even after this architectural shift.

First, `SSLEngine` is still the right answer for the vast majority of systems. If I were building a normal Spring Boot application, a Netty service, or anything where the goal was strong operational simplicity with conventional JVM trade-offs, I would not force a native TLS path into the design.

Second, direct buffers and pooling still matter. This is not an article pretending the entire existing Java ecosystem is naive.

Finally, Panama FFM and native TLS do not remove complexity---they relocate it. You get absolute control, but you also inherit absolute responsibility for lifecycle, correctness, and failure modes. This is an architectural decision for a highly specialized kernel, not a generic industry recommendation.



What I Changed, and What I Gave Up
----------------------------------

A lot of JVM performance work still assumes the heap is the center of the system and the goal is simply to make it hurt less. That is a valid way to design software, but it is not the design I wanted for Exeris.

Once the runtime moved toward explicit off-heap ownership, `SSLEngine` stopped looking like a harmless standard abstraction and started looking like the one boundary that could quietly drag the whole transport path back into the wrong model.

I dropped it because for this specific runtime, it speaks the wrong language. If the hot path is supposed to be off-heap and deterministic by design, then TLS has to speak that language too.



*The FFM native TLS implementation and the explicit ownership model are built entirely off-heap in the Exeris Kernel. If you want to verify the numbers, run the code, or explore zero-allocation architecture:*

* *Review the metrics: [exeris-systems/exeris-benchmarks](https://github.com/exeris-systems/exeris-benchmarks)*
* *Inspect the Kernel and leave a star: [exeris-systems/exeris-kernel](https://github.com/exeris-systems/exeris-kernel)*
