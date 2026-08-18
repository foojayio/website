---
title: "JIT Compilation (Just-in-Time)"
description: "Just-in-time (JIT) compilation is the process by which the JVM translates bytecode into native machine code while the application is running, rather than before it starts. This is the mechanism responsible for Java's high peak throughput: the JVM profiles running ..."
url: "/pedia/jit-compilation-just-in-time/"
frozen: false
---

Just-in-time (JIT) compilation is the process by which the JVM translates [bytecode](https://foojay.io/pedia/bytecode/) into native machine code **while the application is running**, rather than before it starts. This is the mechanism responsible for Java's high peak throughput: the JVM profiles running code and compiles only the methods that are actually "hot" (frequently executed), applying increasingly aggressive optimisations as confidence in the profile grows.

The JVM starts by interpreting bytecode. When a method has been called enough times to cross a threshold, the JIT compiler (C1, the client compiler) produces an initial native compilation with limited optimisation. If the method continues to be called heavily, the more aggressive C2 (server compiler) recompiles it with speculative optimisations — inlining, loop unrolling, escape analysis, and more. This two-tier strategy (called tiered compilation, the default since Java 8) balances fast startup with high peak performance.

Modern JDKs ship with two JIT compilers: the traditional HotSpot C2 compiler, and optionally the **Graal JIT compiler** (available via GraalVM distributions or as an experimental option in mainline JDK via JVMCI). Graal is written in Java and can perform more sophisticated optimisations in some workloads, but C2 remains the default for most use cases.

The cost of JIT compilation is a warm-up period: an application running for the first time may take seconds to minutes before it reaches peak throughput. This is a significant disadvantage in short-lived or serverless deployments. The alternatives are [AOT Compilation](https://foojay.io/pedia/aot-compilation-ahead-of-time/) (pre-compiling to native code) and [CRaC](https://foojay.io/pedia/crac-coordinated-restore-at-checkpoint/) (checkpointing a warmed JVM and restoring it).
