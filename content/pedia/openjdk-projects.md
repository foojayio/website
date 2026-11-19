---
title: "OpenJDK Projects"
description: "OpenJDK is organised into named Projects: focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in ..."
url: "/java-terms-explained/openjdk-projects/"
aliases:
  - "/pedia/openjdk-projects/"
---

OpenJDK is organised into named **Projects** : focused research and development efforts that explore or implement significant improvements to the Java platform. Each project has its own mailing list, repository, and contributor community. Successful work in a project typically results in one or more [JEPs](https://foojay.io/java-terms-explained/jep-jdk-enhancement-proposal/) that integrate the work into mainline JDK releases.

The most significant active and recent projects:

**[Project Loom](/java-terms-explained/project-loom/)** — Delivered [virtual threads](https://foojay.io/java-terms-explained/virtual-threads/) (Java 21), structured concurrency, and scoped values. Aims to make concurrent Java programming dramatically simpler and more scalable by replacing thread-per-request models with lightweight virtual threads.

**[Project Valhalla](/java-terms-explained/value-objects-project-valhalla/)** — Long-running project to introduce value types (objects without identity, stored as flat data rather than references) into the JVM and Java language. Value objects reduce memory overhead and improve cache locality for data-intensive applications. Preview features are available in recent Java versions.

**[Project Panama](/java-terms-explained/project-panama/)** — Delivered the [Foreign Function \& Memory API](https://foojay.io/java-terms-explained/foreign-function-memory-api/) (finalised Java 22) and the Vector API (incubating). Aims to simplify Java's connection to native code and non-Java data.

**[Project Leyden](/java-terms-explained/project-leyden/)** — Focused on improving startup time, warmup time, and footprint via [ahead-of-time optimisation](https://foojay.io/java-terms-explained/aot-compilation-ahead-of-time/). The first deliverable, ahead-of-time class loading and linking (JEP 483), was included in Java 24.

**[Project Amber](/java-terms-explained/project-amber/)** — Delivers language productivity improvements. Responsible for records, sealed classes, pattern matching, text blocks, local-variable type inference (`var`), and other modern Java language features.

**[Project CRaC](/java-terms-explained/crac-coordinated-restore-at-checkpoint/)** — Developed [CRaC (Coordinated Restore at Checkpoint)](https://foojay.io/java-terms-explained/crac-coordinated-restore-at-checkpoint/), a mechanism for checkpointing a warmed JVM and restoring it instantly. Led by Azul.

**[Project Babylon](/java-terms-explained/project-babylon/)** — Research into code reflection: making a Java method body readable and transformable by library code, so a lambda can be compiled for a GPU or translated to another language. Not yet part of a shipping JDK.

**[Project Lilliput](/java-terms-explained/project-lilliput/)** — Shrinks the Java object header from 128 to 64 bits. Compact object headers save a few percent of heap on typical applications, and more on object-heavy ones.

More information on all OpenJDK projects: [openjdk.org/projects/](https://openjdk.org/projects/)

## See Also

* [Project Loom](/java-terms-explained/project-loom/)
* [Project Panama](/java-terms-explained/project-panama/)
* [Project Amber](/java-terms-explained/project-amber/)
* [Project Leyden](/java-terms-explained/project-leyden/)
* [Project Lilliput](/java-terms-explained/project-lilliput/)
* [Project Babylon](/java-terms-explained/project-babylon/)
* [Value Objects (Project Valhalla)](/java-terms-explained/value-objects-project-valhalla/)
* [CRaC (Coordinated Restore at Checkpoint)](/java-terms-explained/crac-coordinated-restore-at-checkpoint/)
* [JEP (JDK Enhancement Proposal)](/java-terms-explained/jep-jdk-enhancement-proposal/)
