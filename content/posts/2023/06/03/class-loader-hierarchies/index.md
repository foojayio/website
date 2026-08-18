---
title: "Class Loader Hierarchies"
slug: "class-loader-hierarchies"
date: "2023-06-03T08:56:55+00:00"
lastmod: "2023-06-03T08:56:56+00:00"
description: "Understanding class loader hierarchies is essential when developing instrumenting Java agents. So I wrote an article about it."
authors:
  - "johannes-bechberger"
image: "classloaderhierarchies.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "a-short-primer-on-java-debugging-internals"
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "continuous-production-profiling-and-diagnostics"
frozen: false
---

Understanding class loader hierarchies is essential when developing Java agents, especially if these agents are instrumenting code.

In my [Instrumenting Java Code to Find and Handle Unused Classes](https://foojay.io/today/instrumenting-java-code-to-find-and-handle-unused-classes/) article, I instrumented all classes with an agent and used a `Store` class in this newly added code:
> A challenge here is that all instrumented classes will use the Store. We, therefore, have to put the store onto the bootstrap classpath, making it visible to all classes.

Class loaders are responsible for (possibly dynamically) loading classes, and they form a hierarchy:
> A class loader is an object that is responsible for loading classes. The class `ClassLoader` is an abstract class. Given the [binary name](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html#binary-name) of a class, a class loader should attempt to locate or generate data that constitutes a definition for the class. A typical strategy is to transform the name into a file name and then read a "class file" of that name from a file system.
>
> \[...\]
>
> The `ClassLoader` class uses a delegation model to search for classes and resources. Each instance of `ClassLoader` has an associated parent class loader. When requested to find a class or resource, a `ClassLoader` instance will usually delegate the search for the class or resource to its parent class loader before attempting to find the class or resource itself.
> [ClassLoader Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html)

An application has multiple class loaders:
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image.png)

A typical Java application has a bootstrap class loader (internal JDK classes and the ClassLoader class itself, implemented in C++ code), a platform classloader (all other JDK classes), and an application/system class loader (application classes):
> * Bootstrap class loader. It is the virtual machine's built-in class loader, typically represented as `null`, and does not have a parent.
> * [Platform class loader](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html#getPlatformClassLoader()). The platform class loader is responsible for loading the *platform classes* . Platform classes include Java SE platform APIs, their implementation classes and JDK-specific run-time classes that are defined by the platform class loader or its ancestors. The platform class loader can be used as the parent of a `ClassLoader` instance. \[...\]
> * [System class loader](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html#getSystemClassLoader()). It is also known as *application class loader* and is distinct from the platform class loader. The system class loader is typically used to define classes on the application class path, module path, and JDK-specific tools. The platform class loader is the parent or an ancestor of the system class loader, so the system class loader can load platform classes by delegating to its parent.
>
> [ClassLoader Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html)

An application might create more class loaders to load classes, e.g., from JARs or do some access control; these classes typically have the application class loader as their parent.

Classes loaded by the application class loader (or children of it) can reference JDK classes but not vice versa. This leads to the problem mentioned before. We can mitigate this by putting all classes that our instrumentation-generated code uses into a runtime JAR which we then "put" on the bootstrap class path.

But we don't put it there but instead tell the bootstrap class loader to also look into our runtime JAR when looking for a class. We do this by using the method` void appendToBootstrapClassLoaderSearch(JarFile jarfile)` of the `Instrumentation` class:
> Specifies a JAR file with instrumentation classes to be defined by the bootstrap class loader.
>
> When the virtual machine's built-in class loader, known as the "bootstrap class loader", unsuccessfully searches for a class, the entries in the `JAR file` will be searched as well.
>
> This method may be used multiple times to add multiple JAR files to be searched in the order that this method was invoked.
> [Instrumentation Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.instrument/java/lang/instrument/Instrumentation.html#appendToBootstrapClassLoaderSearch(java.util.jar.JarFile))

But the documentation also tells us that you can create a giant mess when you aren't careful, including only the minimal number of required classes in the added JAR:
> The agent should take care to ensure that the JAR does not contain any classes or resources other than those to be defined by the bootstrap class loader for the purpose of instrumentation. Failure to observe this warning could result in unexpected behavior that is difficult to diagnose. For example, suppose there is a loader L, and L's parent for delegation is the bootstrap class loader.
>
> Furthermore, a method in class C, a class defined by L, makes reference to a non-public accessor class C$1. If the JAR file contains a class C$1 then the delegation to the bootstrap class loader will cause C$1 to be defined by the bootstrap class loader. In this example an `IllegalAccessError` will be thrown that may cause the application to fail. One approach to avoiding these types of issues, is to use a unique package name for the instrumentation classes.
> [Instrumentation Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.instrument/java/lang/instrument/Instrumentation.html#appendToBootstrapClassLoaderSearch(java.util.jar.JarFile))

You have to append the classes to the search path before (!) the first reference of the classes, as a class that cannot be resolved when first referenced will never be adequately resolved.

If you want to learn more on how to write an agent, consider reading my [Instrumenting Java Code to Find and Handle Unused Classes](https://mostlynerdless.de/blog/2023/04/06/instrumenting-java-code-to-find-and-handle-unused-classes/) blog post or watching my talk[Instrument to Remove: Using Java agents for fun and profit](https://cfp.gulas.ch/gpn21/talk/QJ8DYB/) at the Gulasch Programmier Nacht at June the 10th (a live stream and recordings will be available).

More information on class loaders can be found in the Baeldung article [Class Loaders in Java](https://www.baeldung.com/java-classloaders).

## How to get the class loader hierarchy of your project

I wanted to know the class loader hierarchy for my own projects, so of course, I wrote an agent for it: The [ClassLoader Hierarchy Agent](https://github.com/parttimenerd/classloader-hierarchy-agent) prints the class loader hierarchy at agent load time, the JVM shutdown, and in regular intervals.

Its usage is quite simple. Just attach it to a JVM or add it at startup:

```
Usage: java -javaagent:classloader-hierarchy-agent.jar[=maxPackages=10,everyNSeconds=0] <main class>
  maxPackages: maximum number of packages to print per classloader
  every: print the hierarchy every N seconds (0 to disable)
```

For the `finagle-http` [renaissance](https://renaissance.dev/) benchmark, the agent, for example, prints the following when the benchmark is in full swing:

```
[root]
  platform
       java.sql
       sun.util.resources.provider
       sun.text.resources.cldr.ext
       sun.util.resources.cldr.provider
    app
         me.bechberger               # class loader hierarchy agent 
         org.renaissance             # benchmark harness code
         org.renaissance.core
      null                           # the actual benchmark
        Thread: finagle/netty4/boss-1
           scala
           scala.collection
           scala.jdk
           scala.io
           scala.runtime
```

The root node is the bootstrap class loader. For every class loader, it gives us a thread that uses it as its primary class loader, a short list of packages associated with the class loader, and its child class loaders.

Class loaders can have names, but sadly not many class loader creators use this feature, which turns understanding the individual class loader hierarchies into a guessing game. This is especially the case for Spring based applications like the [Spring PetClinic](https://github.com/spring-projects/spring-petclinic):

```
[root]  
  platform
       java.sql
       javax.sql
       sun.security.ec
       sun.security.jgss
       sun.security.smartcardio
    app
      Thread: main
         me.bechberger
         jdk.jshell.execution.impl
         org.springframework.boot.loader
         jdk.internal.org.jline
         org.springframework.boot.loader.jar
      null
        Thread: mysql-cj-abandoned-connection-cleanup
           jakarta.servlet
           jakarta.validation
           org.postgresql
           jakarta.transaction
           jakarta.el
```

Feel free to try this agent on your applications; maybe you gain some new insights.

## Conclusion

Understanding class loader hierarchies helps to understand subtle problems in writing instrumenting agents. Knowing how to write small agents can empower you to write simple tools to understand the properties of your application.

I hope this blog post helped you to understand class loader hierarchies and agents a little bit better. I'm writing it in a lovely park in Milan:
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image-1-2000x1507.png)

After giving [a talk at JUG Milano on profiling](http://www.jugmilano.it/meeting-145.html) on Wednesday:
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image-3-2000x1500.png)

Next week, I will write a short article on my talk (with slides and the [recording](https://www.youtube.com/watch?v=DhYDzff6UCE&t=348s)). If you live near Munich, you can attend my talk [Write your own Java Profiler in 240 lines of pure Java](https://www.meetup.com/openvaluemuenchen/events/293736106/) on Monday, June 5th.

As always, feel free to fork my code, share my article, and send suggestions or corrections; see you next week, either on my blog or in person.

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone.* *This post has first been published on my personal blog [mostlynerdless.de](https://mostlynerdless.de).***
