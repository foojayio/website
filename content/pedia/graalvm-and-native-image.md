---
title: "GraalVM and Native Image"
description: "GraalVM is a high-performance JDK distribution and runtime developed by Oracle. It extends the standard JDK with two key capabilities: the Graal JIT compiler (a Java-written replacement for HotSpot's C2 JIT that can deliver improved peak throughput for certain workloads) ..."
url: "/pedia/graalvm-and-native-image/"
frozen: false
---

**GraalVM** is a high-performance JDK distribution and runtime developed by Oracle. It extends the standard JDK with two key capabilities: the **Graal JIT compiler** (a Java-written replacement for HotSpot's C2 JIT that can deliver improved peak throughput for certain workloads) and **Native Image** (an AOT compilation tool that produces self-contained native executables).

**GraalVM Native Image** performs a closed-world static analysis of the entire application at build time — tracing all reachable code, classes, and resources — then compiles the result to a native binary using Substrate VM, a minimal VM embedded in the output. The resulting executable:

* Starts in milliseconds (typically 10--100ms) rather than seconds.
* Has a significantly smaller memory footprint at startup.
* Contains no JIT compiler, interpreter, or class-loading infrastructure; everything is compiled in.
* Does not require a JDK to be installed on the target machine.

The trade-offs are a long build time (minutes for large applications), the need for explicit configuration of reflection, JNI, serialisation, and dynamic proxies (which static analysis cannot automatically detect), and potentially lower peak throughput compared to a fully warmed JIT.

GraalVM Native Image is widely used in the cloud-native space — notably by the Quarkus and Micronaut frameworks — where cold-start time and memory footprint in containers are primary concerns. Spring Boot added first-class Native Image support in Spring Boot 3 (2022).

GraalVM is available in two editions: **GraalVM Community** (open source, GPLv2+CE, maintained at [github.com/oracle/graal](https://github.com/oracle/graal)) and **GraalVM Oracle** (free under GFTC, with additional optimisations and support).

## See Also

* [AOT Compilation (Ahead-of-Time)](/pedia/aot-compilation-ahead-of-time/)
* [JIT Compilation (Just-in-Time)](/pedia/jit-compilation-just-in-time/)
* [JDK Distributions](/pedia/jdk-distributions/)
* [Project Leyden](/pedia/project-leyden/)
