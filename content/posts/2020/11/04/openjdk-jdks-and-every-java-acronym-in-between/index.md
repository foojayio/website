---
title: "OpenJDK, JDKs and Every Java Acronym in Between"
slug: "openjdk-jdks-and-every-java-acronym-in-between"
date: "2020-11-04T09:57:46+00:00"
lastmod: "2020-11-10T08:59:10+00:00"
description: "The Java SE landscape is strewn with acronyms picked up over the last 25 years. Sometimes those acronyms even mean multiple things!"
canonical: "https://www.helenjoscott.com/posts/the-java-ecosystem"
authors:
  - "helenjoscott"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
frozen: false
---

The Java SE landscape is strewn with acronyms that it has picked up over the last 25 years. Sometimes those acronyms even mean multiple things. This post attempts to explain them all in terms of two main groupings:

* OpenJDK
* Java Development Kits (JDKs)

What is 'OpenJDK'? {#h2-0-what-is-openjdk}
------------------------------------------

The phrase *OpenJDK* is used to describe at least three fundamental things in the Java ecosystem.

***First use***   

Firstly, there is a place called *OpenJDK* which lives at <https://openjdk.java.net/> also known to some as *The OpenJDK Project* , which is a place to collaborate on an open-source implementation of the Java specifications from Oracle - <https://www.oracle.com/java/technologies/java-se-glance.html>, which are released every 6 months. Sun Microsystems open-sourced the majority of Java in 2006 under the GNU General Public License (GNU GPL) version 2 with a linking exception. *OpenJDK* serves as a continuation of that change. This is the use that I will refer to througout this blog. When I say *OpenJDK* I mean the <https://openjdk.java.net/>.

***Second use***   
*OpenJDK* can also refer to Oracle's free official reference implementation of the Java specifications which live at <https://jdk.java.net>. These are version specific binaries built from the source code which is available on GitHub at <https://github.com/openjdk> of OpenJDK. Many other vendors use the term *OpenJDK* for their specific JDK binary, which is produced by building a binary off the OpenJDK code on GitHub. For example AdoptOpenJDK, which is available at <https://adoptopenjdk.net/>. Oracle provides fixes to this JDK for 6 months (until the next release of Java).

***Third use***   

Lastly, *OpenJDK* can also refer to just the source code repository on GitHub that we spoke about earlier, available at <https://github.com/openjdk>. This is the reference implementation of the Java specifications that we mentioned above.

### Who Contributes to the OpenJDK Project? {#h3-1-who-contributes-to-the-openjdk-project}

There are [various channels to contribute](https://openjdk.java.net/contribute/) to [OpenJDK](https://openjdk.java.net/). [This blog](https://blogs.oracle.com/java-platform-group/the-arrival-of-java-15) from Oracle has a graphic which breaks down committers for Java 15 - those that committed code to OpenJDK. To contribute to OpenJDK, you need to use a [JDK Enhancement Proposal (JEP)](https://openjdk.java.net/jeps/0) to start with.

*Correction with thanks to Marc Maathuis: You don't always need a JEP to contribute to the OracleJDK. Bug fixes, for example may not. Contributors need to have signed the [Oracle Contributor Agreement](https://www.oracle.com/technetwork/oca-405177.pdf).*

### What are JEPs? {#h3-2-what-are-jeps}

[JDK Enhancement Proposal (JEP)](http://openjdk.java.net/jeps/0) is a proposed change to OpenJDK. You can think of them as the roadmap for Java. Like all good roadmaps, there's no commitment to inclusion or timescales. Some JEPs require changes to the Java specifications. In this instance, a corresponding [Java Specification Request (JSR)](https://jcp.org/en/jsr/all) is required.

### What is a JSR? {#h3-3-what-is-a-jsr}

A [Java Specification Request (JSR)](https://jcp.org/en/jsr/all) may detail potential specification changes (where present) that arise from one or more [JEPs](http://openjdk.java.net/jeps/0). Not all JEPs will have JSRs; if the JEP doesn't have changes to the specifications, you don't need a JSR. A JSR may also be a suggested new specification for Java SE that does not require a JEP, such as [a new specification for an API for computer vision](https://jcp.org/en/jsr/detail?id=381). JSRs are considered for inclusion in the Java specifications by the [Java Community Process (JCP)](https://www.jcp.org/en/home/index).

### What is the JCP? {#h3-4-what-is-the-jcp}

The [Java Community Process (JCP)](https://www.jcp.org/en/home/index) is a process to facilitate the review and ultimate inclusion of changes to the [Java specifications](https://docs.oracle.com/javase/specs/).

What are JDKs? {#h2-5-what-are-jdks}
------------------------------------

[Java Development Kits (JDKs)](https://en.wikipedia.org/wiki/Java_Development_Kit) are implementations of the [Java SE platform specification](https://www.oracle.com/java/technologies/java-se-glance.html) by different vendors and groups of people, such as the open source community. Some of them are built from the [OpenJDK code on GitHub](https://github.com/openjdk). JDKs include the Java Runtime Environment (JRE), as well as other tools that help you develop Java.

### What is the Java Runtime Environment (JRE) {#h3-6-what-is-the-java-runtime-environment-jre}

The Java Runtime Environment (JRE) is a component of the JDK that is required to run Java. It used to be a separate download from the JDK, but, since Java 11, it's now part of the JDK itself rather than a separate entity meaning you can no longer download it separately.

### Are all JDKs the same? {#h3-7-are-all-jdks-the-same}

Vendors may introduce little implementation differences such as garbage collection, branding, and utilities, but they are all implementations of the [Java platform specifications](https://docs.oracle.com/javase/specs/). For the purpose of this blog I've assumed the Java SE platform.

### When can you call something a JDK? {#h3-8-when-can-you-call-something-a-jdk}

To be called a JDK officially, the binaries need to have passed a Java Compatibility Kit (JCK) for that release, which is a collection of tests provided by the [Technology Compatibility Kits (TCK)](https://foojay.io/pedia/tck/), for testing each [JSR](https://jcp.org/en/jsr/all) to ensure that the implementation of the specification behaves as expected. [Oracle's OpenJDK](https://jdk.java.net/15/) we spoke about earlier has passed the TCK, for example.

### What's the Technology Compatibility Kit (TCK)? {#h3-9-what-s-the-technology-compatibility-kit-tck}

The [Technology Compatibility Kit (TCK)](https://foojay.io/pedia/tck/) is a set of tests that is applicable for a JSR. There has been controversy on the license for the TCK given it's an open source project. There is now a specific license to allow the TCK to be run against the OpenJDK under the GPL license.

### Who Makes JDKs? {#h3-10-who-makes-jdks}

There are lots of JDKs out there. They all vary in terms of license, support, branding, and implementation differences. The list includes, but is not limited to [AdoptOpenJDK](https://adoptopenjdk.net/), [OracleJDK](https://www.oracle.com/uk/java/technologies/javase-downloads.html), [Oracle OpenJDK](https://jdk.java.net/), [RedHat](https://developers.redhat.com/products/openjdk/download), [Alibaba](https://github.com/alibaba/dragonwell8/releases), [Azul](https://www.azul.com/downloads/zulu-community/?architecture=x86-64-bit&package=jdk), [Bell Soft](https://bell-sw.com/pages/downloads/), [Amazon](https://docs.aws.amazon.com/corretto/index.html), and [IBM](https://www.ibm.com/support/pages/java-sdk-downloads).

It is also possible to build your own JDK, but that's a whole other blog! Talking of blogs, [this one from the Java Champions](https://medium.com/@javachampions/java-is-still-free-2-0-0-6b9aa8d6d244) is excellent and helps fill in some of the gaps as well.

Summary {#h2-11-summary}
------------------------

'OpenJDK' is either:

* [An open source project](https://openjdk.java.net)
* [The Java source code in GitHub](https://github.com/openjdk)
* [A JDK such as the one from Oracle](https://jdk.java.net/15/)

Java Development Kits:

* A binary which is an implementation of the [Java platform specification](https://docs.oracle.com/javase/specs/) that has passed the [TCK](https://foojay.io/pedia/tck/)
* Some JDKs are free to use, some have a cost associated with them for various things such as commercial use, fixes and support

Contributions to Java and updates:

* Anyone can [contribute](https://openjdk.java.net/contribute/) to [OpenJDK](https://openjdk.java.net/)
* The [JCP](https://www.jcp.org/en/home/index) is used to manage updates to the [Java specifications](https://docs.oracle.com/javase/specs/). Anyone can join the JCP.
* [JEPs](http://openjdk.java.net/jeps/0)) are the process for including changes to the OpenJDK (the source reference implementation of Java).
* [JSRs](https://jcp.org/en/jsr/all) are the standards for Java SE, which may, or may not be implemented in the JDK itself.
