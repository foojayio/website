---
title: "Virtual Threads"
description: "Virtual threads, introduced as a preview in Java 19–20 and finalised in Java 21 (JEP 444), are lightweight threads managed by the JVM rather than the operating system. They make it practical to have hundreds of thousands — or even ..."
url: "/pedia/virtual-threads/"
frozen: false
---

Virtual threads, introduced as a preview in Java 19–20 and **finalised in Java 21** (JEP 444), are lightweight threads managed by the JVM rather than the operating system. They make it practical to have hundreds of thousands — or even millions — of concurrent threads in a single JVM, something that is impossible with traditional OS-backed threads.

## The Problem They Solve

Traditional Java threads map 1:1 to OS threads. Each OS thread requires a dedicated stack (typically 0.5–2 MB) and context-switching through the OS kernel. A JVM handling 10,000 concurrent HTTP requests with one thread per request would require 10,000 OS threads — consuming gigabytes of memory and causing severe scheduling overhead.

The conventional workaround is reactive/async programming (`CompletableFuture`, RxJava, Reactor), which avoids blocking threads but at the cost of complex, callback-heavy code that is difficult to read, debug, and reason about.

## How Virtual Threads Work

Virtual threads are **mounted** on **carrier threads** (a small pool of OS threads) when they need to execute. When a virtual thread blocks — on I/O, a lock, or `Thread.sleep()` — it is **unmounted** from its carrier thread, which is immediately free to run other virtual threads. Blocking is cheap: it is a JVM-level continuation switch, not an OS context switch.

The result: you can write straightforward, blocking, imperative code and get the throughput of an async architecture:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1_000_000; i++) {
        executor.submit(() -> {
            // This blocks — but on a virtual thread, that's fine
            String result = callRemoteService();
            return result;
        });
    }
}
```

## Adoption Notes

Virtual threads are not designed for CPU-intensive work (which should still use a small pool of platform threads). They are designed for **I/O-bound** workloads: web servers, database queries, remote API calls — anywhere the thread spends most of its time waiting.

Major frameworks (Spring Boot 3.2+, Quarkus, Micronaut, Helidon) support virtual threads with minimal or no configuration. Simply set the thread model to virtual threads and the framework handles the rest.

## See Also

* [Structured Concurrency](https://foojay.io/pedia/structured-concurrency/)
* [Scoped Values](https://foojay.io/pedia/scoped-values/)
* [Thread Dump](https://foojay.io/pedia/thread-dump/)
* [Heap Dump](https://foojay.io/pedia/heap-dump/)
* [JFR (Java Flight Recorder)](https://foojay.io/pedia/jfr-java-flight-recorder/)
* [Project Loom: Structured Concurrency – Java](https://foojay.io/today/project-loom-structured-concurrency-java/)
* [Thinking About Massive Throughput? Meet Virtual Threads!](https://foojay.io/today/thinking-about-massive-throughput-meet-virtual-threads/)
