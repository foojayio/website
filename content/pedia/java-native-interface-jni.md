---
title: "Java Native Interface (JNI)"
description: "The Java Native Interface (JNI) is the standard mechanism that allows Java code running in the JVM to call, and be called by, native code written in C, C++, or other languages that can produce a shared library. It has ..."
url: "/pedia/java-native-interface-jni/"
frozen: false
---

The Java Native Interface (JNI) is the standard mechanism that allows Java code running in the JVM to call, and be called by, native code written in C, C++, or other languages that can produce a shared library. It has been part of Java since version 1.1.

JNI is used when Java needs to interact with platform-specific functionality (hardware access, OS APIs, existing C libraries), for performance-critical code where a native implementation is significantly faster, or to integrate with existing native codebases that cannot be rewritten in Java.

Using JNI requires writing glue code in C/C++ that bridges Java method signatures (represented as mangled function names with `JNIEnv*` and `jobject` parameters) to the native implementation. It is verbose, error-prone, and bypasses the JVM's memory safety guarantees — incorrect JNI code can crash the JVM or cause memory corruption.

For new code in Java 22+, the **Foreign Function \& Memory (FFM) API** is the recommended alternative. FFM achieves the same goals — calling native functions, reading and writing native memory — entirely from Java, without any native glue code, and with explicit lifetime management that prevents many classes of memory safety bugs. JNI remains available for backwards compatibility.

See also: [Foreign Function \& Memory API](https://foojay.io/pedia/foreign-function-memory-api/), [Bytecode](https://foojay.io/pedia/bytecode/)
