---
title: "Compact Profiles"
description: "Note: Compact Profiles are a Java 8 feature. Starting with Java 9, the same goal — creating a minimal, self-contained runtime — is achieved more flexibly by jlink and the Java Module System (JPMS). If you are working with Java ..."
url: "/pedia/compactprofiles/"
frozen: false
---

> **Note:** Compact Profiles are a Java 8 feature. Starting with Java 9, the same goal --- creating a minimal, self-contained runtime --- is achieved more flexibly by `jlink` and the Java Module System (JPMS). If you are working with Java 9 or later, see [Java Module System (JPMS)](https://foojay.io/pedia/java-module-system-jpms/) instead.

Introduced in OpenJDK 8 (JSR 337), compact profiles define three nested subsets of the Java SE 8 API designed to reduce the runtime footprint on resource-constrained devices such as embedded systems and IoT hardware.

**Profile 1 (compact1)** includes the minimal core: `java.lang`, `java.io`, `java.net` (partial), `java.nio`, `java.security`, `java.util`, and logging. **Profile 2 (compact2)** adds RMI, JDBC, and XML parsing. **Profile 3 (compact3)** adds management and instrumentation APIs, bringing it close to the full Java SE 8 platform. A JRE built to one profile is significantly smaller than a full JRE.

In Java 9+, `jlink` replaced compact profiles as the preferred mechanism for building minimal runtimes. Unlike profiles, `jlink` works at the module granularity and can produce a custom runtime image containing only the exact modules an application needs --- without modifying the JDK itself. A `jlink`-built image can be smaller than any of the three compact profiles and can include the application itself.
