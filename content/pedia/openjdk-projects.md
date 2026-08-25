---
title: "OpenJDK Projects"
description: "OpenJDK is organised into named Projects: focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in ..."
url: "/pedia/openjdk-projects/"
frozen: false
---

OpenJDK is organised into named **Projects** : focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in one or more [JEPs](https://foojay.io/pedia/jep-jdk-enhancement-proposal/) that integrate the work into mainline JDK releases.

The most significant active and recent projects:

**[Project Loom](/pedia/project-loom/)** — Delivered [virtual threads](https://foojay.io/pedia/virtual-threads/) (Java 21), structured concurrency, and scoped values. Aims to make concurrent Java programming dramatically simpler and more scalable by replacing thread-per-request models with lightweight virtual threads.

**[Project Valhalla](/pedia/value-objects-project-valhalla/)** — Long-running project to introduce value types (objects without identity, stored as flat data rather than references) into the JVM and Java language. Value objects reduce memory overhead and improve cache locality for data-intensive applications. Preview features are available in recent Java versions.

**[Project Panama](/pedia/project-panama/)** — Delivered the [Foreign Function \& Memory API](https://foojay.io/pedia/foreign-function-memory-api/) (finalised Java 22) and the Vector API (incubating). Aims to simplify Java's connection to native code and non-Java data.

**[Project Leyden](/pedia/project-leyden/)** — Focused on improving startup time, warmup time, and footprint via [ahead-of-time optimisation](https://foojay.io/pedia/aot-compilation-ahead-of-time/). The first deliverable, ahead-of-time class loading and linking (JEP 483), was included in Java 24.

**[Project Amber](/pedia/project-amber/)** — Delivers language productivity improvements. Responsible for records, sealed classes, pattern matching, text blocks, local-variable type inference (`var`), and other modern Java language features.

**[Project CRaC](/pedia/crac-coordinated-restore-at-checkpoint/)** — Developed [CRaC (Coordinated Restore at Checkpoint)](https://foojay.io/pedia/crac-coordinated-restore-at-checkpoint/), a mechanism for checkpointing a warmed JVM and restoring it instantly. Led by Azul.

**[Project Babylon](/pedia/project-babylon/)** — Research into code reflection: making a Java method body readable and transformable by library code, so a lambda can be compiled for a GPU or translated to another language. Not yet part of a shipping JDK.

**[Project Lilliput](/pedia/project-lilliput/)** — Shrinks the Java object header from 128 to 64 bits. Compact object headers save a few percent of heap on typical applications, and more on object-heavy ones.

More information on all OpenJDK projects: [openjdk.org/projects/](https://openjdk.org/projects/)

## See Also

* [Project Loom](/pedia/project-loom/)
* [Project Panama](/pedia/project-panama/)
* [Project Amber](/pedia/project-amber/)
* [Project Leyden](/pedia/project-leyden/)
* [Project Lilliput](/pedia/project-lilliput/)
* [Project Babylon](/pedia/project-babylon/)
* [Value Objects (Project Valhalla)](/pedia/value-objects-project-valhalla/)
* [CRaC (Coordinated Restore at Checkpoint)](/pedia/crac-coordinated-restore-at-checkpoint/)
* [JEP (JDK Enhancement Proposal)](/pedia/jep-jdk-enhancement-proposal/)
