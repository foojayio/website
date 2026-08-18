---
title: "Java Module System (JPMS)"
description: "The Java Platform Module System (JPMS), introduced in Java 9 as part of Project Jigsaw, brings a formal module concept to the Java platform. A module is a named, self-describing collection of packages. Each module declares what it exports (makes ..."
url: "/pedia/java-module-system-jpms/"
frozen: false
---

The Java Platform Module System (JPMS), introduced in Java 9 as part of Project Jigsaw, brings a formal module concept to the Java platform. A module is a named, self-describing collection of packages. Each module declares what it exports (makes available to other modules) and what it requires (depends on), in a `module-info.java` file at the root of the source tree.

Before JPMS, the JVM had only the concept of the classpath — a flat list of jars with no enforced boundaries between them. Any code could access any public class from any jar, which made large codebases hard to reason about and the JDK itself monolithic. JPMS enforces encapsulation: a package that is not explicitly exported is simply not accessible to code in other modules, even if it is public.

JPMS also enables the `jlink` tool, which can assemble a custom, minimal JRE containing only the JDK modules your application actually needs. The result can be significantly smaller than a full JDK installation.

Adoption in the wider ecosystem has been gradual. Many libraries ship unnamed (classpath) modules alongside or instead of proper named modules, so it is common to run modularised JDK internals while leaving application code on the classpath.
