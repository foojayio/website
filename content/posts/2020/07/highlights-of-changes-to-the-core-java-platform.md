---
title: "Highlights of Changes to the Core Java Platform"
slug: "highlights-of-changes-to-the-core-java-platform"
date: "2020-07-02T14:12:00+00:00"
lastmod: "2021-08-23T12:58:38+00:00"
description: "As a language, Java has evolved over the last 25 years in a way that has provided excellent backwards compatibility. Here's an overview of the highlights!"
authors:
  - "simonritter"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
  - "learning-java-as-a-first-language"
  - "running-single-file-java-source-code-without-compiling-part-1"
  - "fantastic-jvms-and-where-to-find-them"
  - "how-does-java-handle-different-images-and-colorspaces-part-3-introducing-the-bufferedimage"
frozen: false
---

February 12th, 1996. This is a significant date for me personally since it was the day I started work at Sun Microsystems as a Solaris Systems Engineer. It was also two weeks and six days after the release of the Java Development Kit (JDK 1.0).

Of course, the origins of Java go further back than this; James Gosling and others started work on a new programming language in 1991, and it was the May 23rd 1995 when Java was officially announced to the world.

As a language, Java has evolved over the last 25 years in a way that has, with few exceptions, provided excellent backwards compatibility.

* JDK 1.1: inner classes, remote method invocation (RMI) and JDBC for database access.  

* JDK 1.2: Swing and the strictfp modifier  

* JDK 1.4: Assertions. One of the only places where backwards compatibility was impacted. Prior to this, you could use assert as a variable name, so any code that did this would have to be changed to make it compile on JDK 1.4. Thankfully, a simple search and replace is all that's called for. (Technically, this also applies to strictfp in JDK 1.2, but I'm not sure how many people would have used that as a variable name).  

* JDK 5: This was the most significant set of changes since the language was launched and introduced several notable new features
  * Generics
  * Annotations
  * Autoboxing/unboxing
  * Enumerations (another compatibility issue as enum became a reserved word)
  * Varargs
  * Enhanced for each loop
  * Static imports
* JDK 6: One of the least feature-rich releases of Java. No changes to the language and nothing inspiring in the APIs, either.  

* JDK 7: This had a batch of small language changes grouped under project coin (small change in your pocket, get it?)
  * Strings in switch statements
  * try-with-resources
  * The diamond operator for generics
  * Multi-catch statements
  * Binary literals
  * Simplified varargs
  * Underscores in numeric literals.
* JDK 8: This contained probably the biggest changes to Java since its introduction in the form of Lambda expressions and the streams API, which add functional-style programming. This has proved to be a hugely popular feature and got many developers excited about using Java again.  

* JDK 9: Project Jigsaw, the Java Platform Module System. A contentious development which involved delays to the release of JDK 9 after the JCP took the unprecedented step of voting *against* the public review of JSR 376. This led to changes to the encapsulation of internal APIs and a reconsideration ballot. Many users have stuck with JDK 8 because of the perceived complexity involved in moving to use modules. I think breaking up the core class libraries into modules and adding the jlink command was essential. The rest of the module system, I'm not so sure.  

* JDK 10: The first release under the new six-month release cadence brought us local variable type inference; var comes to Java.  

* JDK 11: This was more about removal than addition. We bid farewell to all of the APIs under the java.se.ee aggregator module (CORBA, JAX-B and JAX-WS) and the Oracle JDK lost the browser plugin (surely, the end of applets), Java Web Start and JavaFX.  

* JDK 12: Switch expressions (the first preview feature, meaning it isn't part of the Java SE specification in this release).  

* JDK 13: Text blocks (also a preview feature)  

* JDK 14: Another significant set of changes, adding records (finally!), pattern matching for instanceof (both preview features) and, my favourite, helpful NullPointerExceptions.
