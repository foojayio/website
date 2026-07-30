---
title: "Class Loading"
description: "Before the JVM can execute any code it must load the corresponding .class file into memory. This process is handled by class loaders, which locate, read, and define classes at runtime. Class loading is lazy by default: a class is ..."
url: "/pedia/class-loading/"
frozen: false
---

Before the JVM can execute any code it must load the corresponding `.class` file into memory. This process is handled by **class loaders**, which locate, read, and define classes at runtime. Class loading is lazy by default: a class is not loaded until it is first referenced.

The JVM has a hierarchy of built-in class loaders. The **bootstrap class loader** loads the core Java platform classes from the JDK itself. The **platform class loader** (formerly extension class loader) loads JDK extension modules. The **application class loader** loads classes from the application's classpath or module path. Custom class loaders can be written to load classes from databases, networks, or encrypted archives.

Class loaders follow a **parent-delegation model** : before attempting to load a class itself, a loader first delegates to its parent. This ensures that core platform classes (such as `java.lang.String`) can never be overridden by application code. Custom class loaders can break delegation deliberately, this is how Jakarta EE application servers and OSGi containers isolate application modules from one another.

The JPMS (Java Module System, introduced in Java 9) added a layer of explicit control on top of class loading: modules declare which packages they export and which modules they require, and the JVM enforces those boundaries at load time. This makes accidental access to internal APIs a hard error rather than a runtime surprise.

See also: [Java Module System (JPMS)](https://foojay.io/pedia/java-module-system-jpms/), [Bytecode](https://foojay.io/pedia/bytecode/)
