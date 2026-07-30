---
title: "What is JVM Bytecode?"
slug: "what-is-jvm-bytecode"
date: "2020-09-16T03:55:43+00:00"
lastmod: "2020-09-16T08:23:59+00:00"
description: "Everyone who programs in Java is familiar with the term \"bytecode\". But how many of us understand what JDK bytecode actually is?"
authors:
  - "kevinfarnham"
image: "/images/posts/2020/09/what-is-jvm-bytecode/Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-v1-13-0-compatibility-concurrency-and-formatter-maturity"
  - "runtime-code-analysis-in-the-age-of-vibe-coding"
  - "the-curious-case-of-different-runtimes-with-different-training-data-jit"
frozen: false
---

Everyone who programs in Java, or any of the other languages built on top of the Java Virtual Machine (Scala, Closure, Kotlin, Groovy, Nashorn, Jython, JRuby, et al.) is familiar with the term "bytecode." But how many of us understand what JDK bytecode actually is?

I became more curious about this after I wrote my last foojay.io post, [Why the JVM Is a Brilliant Platform for New Programming Languages](https://foojay.io/blog/why-the-jvm-is-a-brilliant-platform-for-new-programming-languages/). What exactly *is* bytecode?

Bytecode is clearly a layer of code that lies between a higher level programming language and the JVM. And the JVM itself takes that bytecode and solves all the problems that distinguish different operating systems and hardware platforms, by translating that bytecode into the machine language that is needed for each particular platform.

So, in actuality, the JVM is specific for each operating system and hardware type upon which Java and any other JVM language will run. When you download the JVM, you download a version that is targeted at the specific hardware and operating system platform on which your application will run.

In ways, JDK bytecode is similar to [Assembly Language](https://en.wikipedia.org/wiki/Assembly_language). But I believe JDK bytecode is still a layer above Assembly, in that JVM bytecode is an input stream to the JVM, and the JVM translates the bytecode into the machine language (in essence, Assembly) for each particular OS and hardware platform.

The JDK, in other words, translates what the programmer wants to happen into how that can happen on individual operating systems and hardware platforms. This was the founding principle behind Java: "Write once, run anywhere."

Of course, this means that the Java Virtual Machine itself is necessarily an incredibly complex piece of software. It must embed the ability to translate any bytecode into whatever is required for any supported OS and hardware. And, given that JDK languages can run on anything from micro devices to servers in the cloud, to fulfill the needs of any software environment, the JDK must be modularized (a capability that was extended providing developers with much greater flexibility with the completion of [Project Jigsaw](https://openjdk.java.net/projects/jigsaw/) in Java 9).

JVM bytecode is created whenever you compile source code written in Java or another JVM language. The code is stored in files with the extension *.class* . The *javap* command is one way that you can investigate what's inside the *.class* file.

The article [View Bytecode of a Class File in Java](https://www.baeldung.com/java-class-view-bytecode) illustrates some of the options that *javap* provides. In essence, *javap* lets you look at your bytecode *.class* file at various degrees of detail.

Why would you want to know about JVM bytecode? Two reasons are obvious: 1) you are experiencing performance issues with your application; 2) you are inventing a new JVM language.

In the first case, knowing JVM bytecode can help you find where the performance bottleneck takes place in your Java or other JVM language software. I worked for a while with a company whose product was designed for Windows and Unix systems, with a Java client. The Windows server software had major scaling issues with respect to number of concurrent users. That was written in Microsoft's C++. The only way we were able to see what was actually causing the problem was to go into the layer of code beneath our C++ code. There we found a lock that allowed only one thread to execute at a time. In other words, though the original developers had intended to create a fully multi-threaded thread-safe application, Microsoft's compilation of the C++ code down to the next layer made the application in essence single-threaded at that point in its execution. Similarly, learning JVM bytecode can help developers using any JVM language diagnose performance issues.

The second case is a bit different. If you are developing a new language, its performance is going to be dependent on what bytecode your compiler produces. Different compilers will produce different JVM bytecode. So, if you are developing a new JVM language, the creation of your compiler is a very significant task. You can have the greatest syntax imaginable for the purpose of your new JVM language; but if your compiler produces inefficient JVM bytecode, your language will run very slowly and hence not attract many users.

[This article](https://en.wikipedia.org/wiki/Java_bytecode) provides a basic introduction to what Java/JVM bytecode looks like. If your application is aiming for superior performance, or if you are writing a new JVM language, knowledge of JVM bytecode is essential.
