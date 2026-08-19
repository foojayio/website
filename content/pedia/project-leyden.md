---
title: "Project Leyden"
description: "Project Leyden is the OpenJDK initiative focused on improving the startup time, warmup time, and footprint of Java programs by capturing and reusing work done in previous runs. Unlike GraalVM Native Image, which produces a self-contained native binary, Leyden keeps ..."
url: "/pedia/project-leyden/"
frozen: false
---

Project Leyden is the OpenJDK initiative focused on improving the **startup time, warmup time, and footprint** of Java programs by capturing and reusing work done in previous runs. Unlike GraalVM Native Image, which produces a self-contained native binary, Leyden keeps the JVM running — it optimises the JVM's initialisation work, not the compiled code model.

The first deliverable was **JEP 483: Ahead-of-Time Class Loading and Linking**, included in Java 24. Under JEP 483, a "training" run of the application is executed while a special JVM agent records class loading and linking activity. On subsequent runs, the JVM loads a pre-built cache that replaces the expensive class-loading and linking phases, significantly reducing startup time without changing the application code.

Subsequent Leyden phases will extend the idea to profile-guided AOT compilation: capturing JIT-compiled code from a training run and reusing it in subsequent runs, eliminating the warm-up period. This is philosophically similar to [AppCDS](https://foojay.io/pedia/aot-compilation-ahead-of-time/) but far more comprehensive.

Leyden's design goals are deliberately conservative: full JVM semantics must be preserved, and the training/restore process must be transparent to application code. Applications do not need to be modified; the optimisation is applied at the JVM layer.

See also: [AOT Compilation](https://foojay.io/pedia/aot-compilation-ahead-of-time/), [CRaC](https://foojay.io/pedia/crac-coordinated-restore-at-checkpoint/), [GraalVM and Native Image](https://foojay.io/pedia/graalvm-and-native-image/)
