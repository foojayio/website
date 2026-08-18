---
title: "Why the JVM Is a Brilliant Platform for New Programming Languages"
date: "2020-09-09T05:07:34+00:00"
lastmod: "2020-09-11T10:43:40+00:00"
description: "The capability of the JVM to overcome problems related to hardware and operating systems is why we're seeing inventions of new languages on top of the JVM."
authors:
  - "kevinfarnham"
image: "Favicon-3-2.png"
categories:
  - "Performance"
related_posts:
  - "java-panama-polyglot-part-3"
  - "idempotent-spring-boot-starter"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "jurassic-jdk-migrate-or-extinct"
frozen: false
---

In my [previous post](https://foojay.io/blog/why-java-c-and-python-are-todays-most-utilized-programming-languages/), I speculated that Java, Python, and C are likely to be the most prominent programming languages going pretty far into the future.

Another point in my speculation was that utilizing the Java Virtual Machine facilitates the invention of new programming languages created for special purposes that could not be easily created by other means. The JVM takes care of all of the low-level infrastructure problems that are involved in the creation of any software that you'd like to be runnable on any device or operating system.

Python is wonderful. However, you can write a Python program that runs perfectly on Linux and Windows, but it will not run on Mac OS X. That's a problem when you're trying to develop a product that supports a diverse community of users who will be running your application on any number of platforms and operating systems.

The problem with Python is that it is still a bit too low level, and closer to C than the Java Virtual Machine is. Python is a great language for scientific and other applications that require high performance. But, Python does not totally exemplify the "write once, run anywhere" mantra that the JVM provides. You can write Python software that works perfectly on Windows and Linux, but which crashes on Mac OS. Hence, we have Jython [https://www.jython.org/Jython](https://www.jython.org/).

The brilliance of the Java Virtual Machine is that it is a kind of operating system that sits on top of any currently existing viable operating system. In other words, if you use the JVM as your base platform, you don't have to worry about numerous "if" statements related to the specifics of hardware and operating systems. The JVM takes care of all of that for you. Whatever you write, it's going to run perfectly on any operating system and hardware that supports the Java Virtual Machine.

A great many developers have noticed this, and they've created many languages apart from Java that utilize the JVM's capabilities. Programming languages such as Scala, Kotlin, Groovy, and Clojure introduce the opportunity to utilize the JVM for purposes specialized for specific purposes. Meanwhile, languages like Jython and JRuby provide the opportunity to write code in non-Java languages that can still take advantage of the "write once, run anywhere" capability of the Java Virtual Machine platform.

This capability of the Java Virtual Machine to transgress all problems related to different hardware and different operating systems is why I believe we're seeing most inventions of new programming languages happening on top of the JVM. It's the only thing that makes sense, if you want to eliminate the headaches of writing immense amounts of code trying to deal with diverse operating systems and hardware. As long as your language is running on top of the JVM, you're safe long into the future with respect to the survival of your language, and the survival of any application developed using your language.
