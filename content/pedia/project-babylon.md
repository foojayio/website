---
title: "Project Babylon"
description: "Project Babylon extends Java's reflection to reach inside method bodies, so a library can read and transform the code of a lambda. Its driving use case is running Java on GPUs."
url: "/pedia/project-babylon/"
frozen: false
---

Project Babylon is the OpenJDK project exploring **code reflection**: extending Java's reflective capabilities from the *shape* of a program — its classes, methods and fields — to the *contents* of a method body.

Java reflection can already tell a library that a method exists and what its parameters are. It cannot tell the library what the method does. Babylon makes a method body available as a structured, analysable model that library code can read, transform and compile to something else at run time.

The driving use case is **programming hardware the JVM does not target**. To run a computation on a GPU, something has to translate that computation into the accelerator's own language — and today that means writing it twice, once in Java and once in a kernel language, or embedding the kernel as a string. With code reflection, a Java lambda can be read by a library and compiled for the device, so the computation is written once, in Java, and stays type-checked by the Java compiler. The Heterogeneous Accelerator Toolkit (HAT) is the OpenJDK effort exploring exactly that on top of Babylon.

The same capability applies wherever a library needs the meaning of Java code rather than a description of it: translating a lambda into SQL rather than parsing a string query, automatic differentiation for machine learning, and other forms of domain-specific compilation that today rely on bytecode rewriting or annotation processors.

Babylon is a **research project**, not a delivered feature: it is developed in its own repository, and none of it is part of a shipping JDK yet. It is worth knowing about because it is the mechanism a good deal of Java-on-GPU and Java-for-AI work is being built on.

More reading on Foojay:

* [Foojay Podcast #82: OpenJDK Projects (Leyden, Babylon, Panama) and TornadoVM](/today/foojay-podcast-82/)

## See Also

* [Bytecode](/pedia/bytecode/)
* [Project Panama](/pedia/project-panama/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
* [JIT Compilation (Just-in-Time)](/pedia/jit-compilation-just-in-time/)
