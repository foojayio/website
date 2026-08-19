---
title: "Running Single-File Java Source Code Without Compiling (Part 2)"
date: "2020-09-10T15:54:17+00:00"
lastmod: "2020-09-10T15:59:38+00:00"
description: "Let's continue this series, by looking at JEP 330, Launch Single-File Source-Code Programs, which is a new features introduced in OpenJDK 11."
canonical: "https://www.infoq.com/articles/single-file-execution-java11/"
authors:
  - "mohamed-taman"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
related_posts:
frozen: false
---

[Let's continue from part 1 of this series](https://foojay.io/today/running-single-file-java-source-code-without-compiling-part-1/), by looking at JEP 330, Launch Single-File Source-Code Programs, which is one of the new features introduced in the OpenJDK 11 release. This feature allows you to execute a Java source code file directly using the `java` interpreter. The source code is compiled in memory and then executed by the interpreter, without producing a .class file on disk.

However, this feature is limited to code that resides in a single source file. You cannot add additional source files to be compiled in the same run.

To work around this limitation, all the classes have to be defined in the same file, but there are no restrictions on the number of classes in the file, and either they are all public classes or not it doesn't matter as long as they are in the same source file.

The first class declared in the source file will be picked up to be used as the main class, and we should put our main method inside that first class. So the order matter.

#### A First Example

Now let's begin the way we always do when we start learning something new, yes, exactly as you guessed, with the simplest example: Hello Universe!

We will focus our efforts on demonstrating how to use this feature by trying out different samples, so that you'll get the idea of how this feature can be used in your day-to-day coding.

If you haven't already, create the `HelloUniverse.java` file [as listed in the previous part of this series](https://foojay.io/today/running-single-file-java-source-code-without-compiling-part-1/), compile it, and run the resulting class file.

Now let's delete the class file. You'll understand why in a moment:

```
rm HelloUniverse.class
```

Now if you run the class only with the `java` interpreter, without compilation, as in:

```
> java HelloUniverse.java
Hello foojay!
```

You should see the same result as before: it runs.

This means we can now just say `java HelloUniverse.java`. We're just passing in the source code rather than the class file: it internally compiles the source, then runs the resulting compiled code and finally, the message is output to the console.

So, there is still a compilation process going on and if there's a compilation error, you will still get an error notification. Also, you can check the directory structure and see that there is no class file generated.

It is now an in-memory compilation process.

We will explore this in more detail in the next part of this series.

**Note:** Used with permission and thanks — [originally written and published by Mohamed Taman](https://www.infoq.com/articles/single-file-execution-java11/).
