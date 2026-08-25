---
title: "Project Panama"
description: "Project Panama connects Java to code and data outside the JVM. It delivered the Foreign Function & Memory API, the jextract tool that generates bindings from C headers, and the incubating Vector API."
url: "/pedia/project-panama/"
frozen: false
---

Project Panama is the OpenJDK project concerned with the boundary between the JVM and everything outside it: **native libraries, off-heap memory, and hardware the JVM does not expose directly**. Its goal is to make that boundary crossable from plain Java, without writing C.

Its deliverables:

* **[Foreign Function & Memory API](/pedia/foreign-function-memory-api/)** — call native functions and safely access off-heap memory from Java. Finalised in Java 22 (JEP 454), and the replacement for [JNI](/pedia/java-native-interface-jni/) in new code.
* **jextract** — a tool that reads a C header file and generates the Java bindings for it, so wrapping a native library is a build step rather than a hand-written project.
* **Vector API** — expresses computations that reliably compile to SIMD instructions on the CPU. It has been incubating for many releases, deliberately: the API is held back until the underlying compiler support is ready, and it depends on work in [Project Valhalla](/pedia/value-objects-project-valhalla/).

**Why this replaced JNI rather than improving it.** JNI required a C shim compiled for every platform you shipped to, and a mistake in it crashed the JVM rather than throwing. The Foreign Function & Memory API keeps the checks in Java: memory is accessed through a `MemorySegment` with known bounds and a known lifetime, so a use-after-free becomes an exception instead of a segfault. The cost is that unsafe operations are explicit and must be enabled — which is the point.

More reading on Foojay:

* [Building Project Panama's jextract tool by yourself](/today/building-project-panamas-jextract-tool-by-yourself/)
* [Java Panama Polyglot (C++) Part 1](/today/java-panama-polyglot-part1/)
* [Foojay Podcast #82: OpenJDK Projects (Leyden, Babylon, Panama) and TornadoVM](/today/foojay-podcast-82/)

## See Also

* [Foreign Function & Memory API](/pedia/foreign-function-memory-api/)
* [Java Native Interface (JNI)](/pedia/java-native-interface-jni/)
* [Value Objects (Project Valhalla)](/pedia/value-objects-project-valhalla/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
