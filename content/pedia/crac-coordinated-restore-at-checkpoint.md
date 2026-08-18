---
title: "CRaC (Coordinated Restore at Checkpoint)"
description: "CRaC is an OpenJDK project that solves one of the most common complaints about Java in cloud environments: slow startup. The JVM traditionally takes time to load classes, initialise frameworks, and warm up the JIT compiler before an application can ..."
url: "/pedia/crac-coordinated-restore-at-checkpoint/"
frozen: false
---

CRaC is an OpenJDK project that solves one of the most common complaints about Java in cloud environments: slow startup. The JVM traditionally takes time to load classes, initialise frameworks, and warm up the JIT compiler before an application can serve its first request. In a serverless or container-heavy deployment, this warmup cost is paid every time a new instance starts.

CRaC addresses this by letting you take a *checkpoint* of a fully-warmed, running JVM process — capturing its complete in-memory state — and then *restore* that snapshot on demand. A restored process skips the entire startup and warmup sequence, reaching full speed in milliseconds.

The project was initiated by Azul and is now an OpenJDK standard API, with support in frameworks including Spring Boot and Quarkus. It works on Linux using the CRIU (Checkpoint/Restore In Userspace) mechanism, and more recently via a new engine called Warp that does not require elevated privileges.

More reading on Foojay:

* [Superfast Application Startup: Java on CRaC](https://foojay.io/today/superfast-application-startup-java-on-crac/)
* [How to Run a Java Application with CRaC in a Docker Container](https://foojay.io/today/how-to-run-a-java-application-with-crac-in-a-docker-container/)
* [Faster Java Warmup: CRaC versus ReadyNow](https://foojay.io/today/faster-java-warmup-crac-versus-readynow/)
