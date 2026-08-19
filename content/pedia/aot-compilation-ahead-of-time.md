---
title: "AOT Compilation (Ahead-of-Time)"
description: "Ahead-of-time (AOT) compilation converts Java source code or bytecode into native machine code before the application runs, rather than during execution. The result is a self-contained native binary that starts nearly instantly, without the warm-up phase that a JIT-compiled JVM ..."
url: "/pedia/aot-compilation-ahead-of-time/"
frozen: false
---

Ahead-of-time (AOT) compilation converts Java source code or bytecode into native machine code before the application runs, rather than during execution. The result is a self-contained native binary that starts nearly instantly, without the warm-up phase that a JIT-compiled JVM requires.

Traditional JVMs start by interpreting bytecode and then JIT-compiling hot methods at runtime. This produces excellent peak throughput but means applications take seconds — sometimes tens of seconds for framework-heavy apps — to reach full speed. AOT compilation eliminates that delay by doing the compilation work once, at build time.

The leading implementation is **GraalVM Native Image**, which statically analyses the entire application at build time and produces a Linux/macOS/Windows executable. The trade-offs are a longer build process, the need for ahead-of-time metadata for reflection and dynamic class loading, and peak throughput that may fall slightly below a fully warmed JIT — though recent advances have significantly closed this gap.

Within OpenJDK itself, **Project Leyden** ([JEP 483](https://openjdk.org/jeps/483), first delivered in Java 24) provides an official path to AOT optimisation without leaving the JVM. Leyden introduces ahead-of-time class loading and linking: a first "training" run captures profile data and class loading state, which subsequent runs restore instantly. This approach preserves full JVM semantics and JIT compilation while removing most of the startup cost.

A simpler built-in mechanism is **AppCDS (Application Class Data Sharing)**, available since Java 13, which serialises the parsed and linked class state to a shared archive that future JVM launches reload in milliseconds. AppCDS requires no code changes and is compatible with any JDK distribution.

## See Also

* [JIT Compilation (Just-in-Time)](/pedia/jit-compilation-just-in-time/)
* [CRaC (Coordinated Restore at Checkpoint)](/pedia/crac-coordinated-restore-at-checkpoint/)
* [GraalVM and Native Image](/pedia/graalvm-and-native-image/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
* [Project Leyden](/pedia/project-leyden/)
