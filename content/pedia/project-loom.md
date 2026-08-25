---
title: "Project Loom"
description: "Project Loom is the OpenJDK project that made concurrency cheap again: virtual threads, structured concurrency and scoped values, so a server can handle a request per thread instead of a request per pooled thread."
url: "/pedia/project-loom/"
frozen: false
---

Project Loom is the OpenJDK project that set out to make **high-throughput concurrent Java simple again**. Its premise is that the thread-per-request style every Java developer already knows how to read and debug was abandoned only because operating-system threads are expensive — so make threads cheap instead of asking developers to rewrite their code as chains of callbacks and futures.

Three things came out of it, each with its own entry here:

* **[Virtual threads](/pedia/virtual-threads/)** — lightweight threads scheduled by the JVM rather than the operating system, so an application can have millions of them. Finalised in Java 21 (JEP 444). This is the deliverable most people mean when they say "Loom".
* **[Structured concurrency](/pedia/structured-concurrency/)** — treats a group of concurrent subtasks as a single unit of work with a defined lifetime, so a failure or cancellation propagates in a way you can reason about. Still a preview API.
* **[Scoped values](/pedia/scoped-values/)** — a way to share immutable data with callees without passing it through every method signature, and the replacement for `ThreadLocal` in a world where threads are created freely rather than pooled.

The reason those last two exist is that virtual threads break assumptions the old model relied on. `ThreadLocal` was a reasonable cache when threads were scarce and reused; with a million short-lived threads it is neither. Thread pools were how you bounded concurrency; with virtual threads the bound belongs on the resource, not on the threads.

**Adopting virtual threads is not purely a matter of swapping an executor.** Code that holds a lock or calls into native code while blocking can *pin* its carrier thread, quietly turning a virtual-thread application back into a small thread pool — the failure mode Loom is most often reported as "not working". Pinning on `synchronized` blocks was addressed in later releases, but native calls still pin.

More reading on Foojay:

* [What the Heck Is Project Loom for Java?](/today/what-the-heck-is-project-loom-for-java/)
* [Project Loom: Structured Concurrency - Java](/today/project-loom-structured-concurrency-java/)
* [A Field Guide to Virtual Thread Pinning](/today/virtual-thread-pinning-field-guide/)
* [Loom is just HyperThreading in Java](/today/loom-is-just-hyperthreading-in-java/)

## See Also

* [Virtual Threads](/pedia/virtual-threads/)
* [Structured Concurrency](/pedia/structured-concurrency/)
* [Scoped Values](/pedia/scoped-values/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
* [Thread Dump](/pedia/thread-dump/)
