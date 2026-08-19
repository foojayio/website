---
title: "OpenJDK Projects"
description: "OpenJDK is organised into named Projects: focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in ..."
url: "/pedia/openjdk-projects/"
frozen: false
---

OpenJDK is organised into named **Projects** : focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in one or more [JEPs](https://foojay.io/pedia/jep-jdk-enhancement-proposal/) that integrate the work into mainline JDK releases.

The most significant active and recent projects:

**Project Loom** — Delivered [virtual threads](https://foojay.io/pedia/virtual-threads/) (Java 21), structured concurrency, and scoped values. Aims to make concurrent Java programming dramatically simpler and more scalable by replacing thread-per-request models with lightweight virtual threads.

**Project Valhalla** — Long-running project to introduce value types (objects without identity, stored as flat data rather than references) into the JVM and Java language. Value objects reduce memory overhead and improve cache locality for data-intensive applications. Preview features are available in recent Java versions.

**Project Panama** — Delivered the [Foreign Function \& Memory API](https://foojay.io/pedia/foreign-function-memory-api/) (finalised Java 22) and the Vector API (incubating). Aims to simplify Java's connection to native code and non-Java data.

**Project Leyden** — Focused on improving startup time, warmup time, and footprint via [ahead-of-time optimisation](https://foojay.io/pedia/aot-compilation-ahead-of-time/). The first deliverable, ahead-of-time class loading and linking (JEP 483), was included in Java 24.

**Project Amber** — Delivers language productivity improvements. Responsible for records, sealed classes, pattern matching, text blocks, local-variable type inference (`var`), and other modern Java language features.

**Project CRaC** — Developed [CRaC (Coordinated Restore at Checkpoint)](https://foojay.io/pedia/crac-coordinated-restore-at-checkpoint/), a mechanism for checkpointing a warmed JVM and restoring it instantly. Led by Azul.

More information on all OpenJDK projects: [openjdk.org/projects/](https://openjdk.org/projects/)

## See Also

* [JEP (JDK Enhancement Proposal)](/pedia/jep-jdk-enhancement-proposal/)
* [Virtual Threads](/pedia/virtual-threads/)
* [Foreign Function & Memory API](/pedia/foreign-function-memory-api/)
* [AOT Compilation (Ahead-of-Time)](/pedia/aot-compilation-ahead-of-time/)
* [CRaC (Coordinated Restore at Checkpoint)](/pedia/crac-coordinated-restore-at-checkpoint/)
* [Value Objects (Project Valhalla)](/pedia/value-objects-project-valhalla/)
