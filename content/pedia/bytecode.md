---
title: "Bytecode"
description: "When you compile a Java source file, the Java compiler (javac) does not produce native machine code for a specific CPU. Instead it produces bytecode: a compact, platform-neutral instruction set stored in .class files. Bytecode is not directly understood by ..."
url: "/pedia/bytecode/"
frozen: false
---

When you compile a Java source file, the Java compiler (`javac`) does not produce native machine code for a specific CPU. Instead it produces *bytecode* : a compact, platform-neutral instruction set stored in `.class` files. Bytecode is not directly understood by the operating system; it is executed by the Java Virtual Machine (JVM), which translates it into native instructions at runtime.

This two-step model — compile once to bytecode, run anywhere on a JVM — is the foundation of Java's "write once, run anywhere" promise. The same `.class` file runs without modification on Windows, Linux, or macOS, as long as a compatible JVM is available.

Bytecode is also the reason the JVM can host languages other than Java. Kotlin, Scala, Groovy, and Clojure all compile to the same bytecode format, allowing them to interoperate seamlessly with Java libraries.

## See Also

* [Basic Java Concepts](/pedia/basic-java-concepts/)
* [Class Loading](/pedia/class-loading/)
* [Foreign Function & Memory API](/pedia/foreign-function-memory-api/)
* [JIT Compilation (Just-in-Time)](/pedia/jit-compilation-just-in-time/)
* [Java Native Interface (JNI)](/pedia/java-native-interface-jni/)
