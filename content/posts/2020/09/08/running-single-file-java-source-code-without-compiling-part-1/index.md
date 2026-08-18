---
title: "Running Single-File Java Source Code Without Compiling (Part 1)"
slug: "running-single-file-java-source-code-without-compiling-part-1"
date: "2020-09-08T08:15:03+00:00"
lastmod: "2020-09-11T10:45:36+00:00"
description: "From OpenJDK 11 onwards, you get the option to launch a single Java source file directly, without intermediate compilation."
canonical: "https://www.infoq.com/articles/single-file-execution-java11/"
authors:
  - "mohamed-taman"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "learning-java-as-a-first-language"
  - "fantastic-jvms-and-where-to-find-them"
  - "highlights-of-changes-to-the-core-java-platform"
  - "how-does-java-handle-different-images-and-colorspaces-part-3-introducing-the-bufferedimage"
frozen: false
---

If you recall the old days, before OpenJDK 11, say you have a `HelloUniverse.java` source file that contains a class definition and a static main method, which prints out as a single line of text to the terminal, like the following:

```java
public class HelloUniverse{
      public static void main(String[] args) { 
            System.out.println("Hello foojay!");
      }
}
```

Normally to run this class, first, you would need to compile it, using a Java compiler (`javac`), which would result in a `HelloUniverse.class` file:

```markdown
> javac HelloUniverse.java
```

Then you would use a `java` virtual machine (interpreter) command to run the resulting class file:

```markdown
> java HelloUniverse
Hello foojay!
```

This starts up the JVM, loads the class, and executes the code.

But what if you want to quickly test a piece of code or if you're new to learning Java and want to experiment with the language? Those two steps in the process may seem like a bit of heavy lifting.

From OpenJDK 11 onwards, you get the option to launch a single Java source file directly, without intermediate compilation. This feature is particularly useful for someone new to the language who wants to try out simple programs and when you combine this feature with the `jshell` (a topic for another time), you get a great beginner's learning toolset.

Professionals can also make use of these tools to explore new language changes or to try out an unknown API. In my opinion, greater power comes when we can automate a lot of tasks, such as writing Java programs as scripts, and then executing them from the operating system shell. This combination gives us the flexibility of shell scripts with the power of the Java language.

We will explore this in more detail in the next part of this series.

**Note:** Used with permission and thanks --- [originally written and published by Mohamed Taman](https://www.infoq.com/articles/single-file-execution-java11/).
