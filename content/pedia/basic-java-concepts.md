---
title: "Basic Java Concepts"
description: "The Java programming language and Java virtual machine (also known as Java runtime environment) provide the tools to write operating system independent applications. A Java application employs the JVM (Java Virtual Machine) on the target system to run its code. ..."
url: "/pedia/basic-java-concepts/"
frozen: false
---

The Java programming language and Java virtual machine (also known as Java runtime environment) provide the tools to write operating system independent applications. A Java application employs the JVM (Java Virtual Machine) on the target system to run its code. Java applications are therefore independent from the JVM and one can be updated/patched independent from the other.

A Java application is deployed in a platform-independent deployment format called Byte Code. Byte Code is commonly packaged and shipped as a collection of ".jar" files. The compilation and packaging of Java source code into JAR files is a comparatively lightweight process that does not create a classical CPU and OS dependent binary. To run a Java application, a platform-specific Java Runtime Environment (JRE) or Java Virtual Machine (JVM) is required.

The vendor or creator of a Java application may choose to bundle the application with a JRE for a specific platform or ship only the platform-independent Byte Code and leave the choice of JVM to the operator.

As Java is fully standardized, the JVM (within a major version) is interchangeable and the Java application OS and CPU independent for a correctly designed and standard compliant Java application. No recompilation is required in any case.

## **Key concepts of Java**

* **Java.** The programming language as standardized in the Java SE standard.
* **Java source code.** A collection of files with the .java extension. Java source code is compiled into Byte Code.
* **Byte Code.** Platform independent code which can be run on a JVM. The most common package format is ".jar" files. Other languages like Scala or Kotlin can run on the JVM when compiled into Byte Code.
* **JDK (Java Development Kit).** The collection of standardized tools, such as the Java compiler, to write Java applications.
* **JRE (Java Runtime environment).** The smallest type of JVM bundle. Used to run Java applications.
* **JVM (Java virtual machine).** A runtime environment to execute Java applications shipped in ".jar" files. The smallest Java SE compliant JVM bundle is the JRE.

{{< img src="/images/pedia/basic-java-concepts/image2020-11-4_13-5-7-700x359.png" class="size-medium" width="700" height="359" >}}

## JDK

[Java Development Kits (JDKs)](https://en.wikipedia.org/wiki/Java_Development_Kit) are implementations of the [Java SE platform specification](https://www.oracle.com/java/technologies/java-se-glance.html) by different vendors and groups of people, such as the open source community. Some of them are built from the [OpenJDK code on GitHub](https://github.com/openjdk). JDKs include the Java Runtime Environment (JRE), as well as other tools that help you develop Java.

## JRE

The Java Runtime Environment (JRE) is the minimal component of the JDK that is required to run Java. Since Java 11 the JRE is a part of the JDK itself rather than a separate entity meaning you can no longer download it separately.

## JVM

The JVM is the core of the runtime environment. Although colloquially use as a synonym to JRE, strictly speaking the JVM does not include the API libraries.

The JVM defines a consistent object, memory, garbage collection and threading model for executing the semantics defined in byte code. Other languages have been defined making use of the JVM semantics relying on the Java APIs more or less. E.g the Android Runtime follows the JVM model but is not Java SE compliant (also it tries to be, to make the life of Java developers easier on Android).

## See Also

* [JDK Distributions](https://foojay.io/pedia/jdk-distributions/)
* [Bytecode](https://foojay.io/pedia/bytecode/)
* [OpenJDK, JDKs and Every Java Acronym in Between](https://foojay.io/blog/openjdk-jdks-and-every-java-acronym-in-between/)
