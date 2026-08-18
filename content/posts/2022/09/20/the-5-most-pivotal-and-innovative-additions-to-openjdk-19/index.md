---
title: "The 5 Most Pivotal and Innovative Additions to OpenJDK 19"
slug: "the-5-most-pivotal-and-innovative-additions-to-openjdk-19"
date: "2022-09-20T16:25:12+00:00"
lastmod: "2022-09-21T15:13:48+00:00"
description: "Although OpenJDK 19 is not an LTS, it is still a significant release, in my opinion. It includes several game-changing features!"
authors:
  - "bazlur-rahman"
image: "cafe-g56e2bcea6_1920.jpg"
categories:
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-5"
  - "openjdk-19-released"
  - "what-the-heck-is-project-loom-for-java"
  - "what-can-we-expect-in-openjdk-19"
frozen: false
---

Although [OpenJDK 19](https://foojay.io/today/openjdk-19-released/) is not an LTS, it is still a significant release, in my opinion.

It includes several game-changing features that will alter the Java landscape.

Many features intrigue my interest, but there are five in particular that I can't wait to try out. Let's break those down and talk about them separately.  

**[JEP 425: Virtual Threads (Preview)](https://openjdk.org/jeps/425)**

Under the umbrella of Project Loom, JEP 425 introduces virtual threads, which aim to dramatically reduce the effort of writing, maintaining, and observing high-throughput concurrent applications on the Java platform. This is a [preview feature](https://openjdk.java.net/jeps/12). Consider the following example:

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        var vThread = Thread.startVirtualThread(() -> {
            System.out.println("Hello from the virtual thread");
        });

        vThread.join();
    }
}
```

Since this is a preview feature, a developer will need to provide the **--enable-preview** flag to compile this code, as shown in the following command:

`javac --release 19 --enable-preview Main.java`

The same flag is also required to run the program:

`java --enable-preview Main`

However, one can directly run this using the [source code launcher](https://openjdk.java.net/jeps/330). In that case, the command line would be:  
`java --source 19 --enable-preview Main.java`

The [jshell](https://docs.oracle.com/javase/9/jshell/introduction-jshell.htm#JSHEL-GUID-630F27C8-1195-4989-9F6B-2C51D46F52C8) option is also available but requires enabling the preview feature as well:

`jshell --enable-preview`

While **Thread.startVirtualThread(Runnable)** is the convenient way to create a virtual thread, new APIs like [**Thread.Builder**](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Thread.Builder.html), [**Thread.ofVirtual()**](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Thread.html#ofVirtual()), and [**Thread.ofPlatform()**](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Thread.html#ofPlatform()) were added to create virtual and platform threads.

If you want to know more about it, please, head over the my GitHub repository: <https://github.com/rokon12/project-loom-slides-and-demo-code>.

**[JEP 428: Structured Concurrency (Incubator)](https://openjdk.org/jeps/428)**   

Structured Concurrency allows you to treat multiple tasks running on different threads as an atomic operation, making multithreaded programming easier. As a result, error handling and cancellation will be simplified, reliability will increase, and observability will be boosted. Let's see an example:  

```java
Response handle() throws ExecutionException, InterruptedException {
   try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
       Future<String> user = scope.fork(() -> findUser());
       Future<Integer> order = scope.fork(() -> fetchOrder());

       scope.join();          // Join both forks
       scope.throwIfFailed(); // ... and propagate errors

       // Here, both forks have succeeded, so compose their results
       return new Response(user.resultNow(), order.resultNow());
   }
}
```

This API runs on top of JEP 425, [Virtual Threads (Preview)](https://openjdk.java.net/jeps/425), also targeted for JDK 19

Compile the above code as shown in the following command:

`javac --release 19 --enable-preview --add-modules jdk.incubator.concurrent Main.java`

The same flag is also required to run the program:

`java --enable-preview --add-modules jdk.incubator.concurrent Main;`

However, one can directly run this using the [source code launcher](https://openjdk.java.net/jeps/330). In that case, the command line would be:

`java --source 19 --enable-preview --add-modules jdk.incubator.concurrent Main.java`

The [jshell](https://docs.oracle.com/javase/9/jshell/introduction-jshell.htm#JSHEL-GUID-630F27C8-1195-4989-9F6B-2C51D46F52C8) option is also available, but requires enabling the preview feature as well:

`jshell --enable-preview --add-modules jdk.incubator.concurrent`

[**JEP 427: Pattern Matching for switch (Third Preview)**](https://openjdk.org/jeps/427)

Pattern Machining (Third Preview) is to add a pattern for switch expressions and statements to the Java programming language. For concise and safe expression of complex data-oriented queries, it allows testing against multiple patterns, each with a distinct action. Consider the following example-

```java
package ca.bazlur;

public class PatternMatching {

  public static String transform(Integer status) {
    return switch (status) {
      case 200 -> "Ok";
      case 301 -> "Moved Permanently";
      case 404 -> "Not found";
      case 500 -> "Internal Server Error";
      case Number n when n.intValue() >= 600 -> "Invalid";
      default -> "Valid";
    };
  }

  public static void main(String[] args) {
    System.out.println(transform(200));
    System.out.println(transform(600));
    System.out.println(transform(404));
  }
}
```

This is also a preview feature, requiring developers to add `--enable-preview`.

**[JEP-424: Foreign Function \& Memory API (Preview)](https://openjdk.org/jeps/424)**

By utilizing the Foreign Function and Memory API, Java applications can talk to and use data that is not built into the JRE. Without the hassle and security concerns of the JNI, Java programs can now access native memory, invoke native functions, and process native data. Example-

```java
Linker linker = Linker.nativeLinker();
SymbolLookup stdlib = linker.defaultLookup();
MethodHandle radixSort = linker.downcallHandle(
                             stdlib.lookup("radixsort"), ...);
```

Developers who want to learn more about this JEP can leverage this series on Foojay: <https://foojay.io/today/project-panama-for-newbies-part-1/>  

**[JEP 405: Record Patterns (Preview)](https://openjdk.org/jeps/405)**

The purpose of Record Patterns is to enrich the language with record patterns that can be used to deconstruct record values. To "enable a robust, declarative, and composable form of data navigation and processing," record patterns can be combined with type patterns.

Let's consider the following example-

```java
package ca.bazlur;

import java.util.stream.Stream;

public class Main {

  public static void main(String[] args) {
    var s1 = new Circle(5);
    var s2 = new Rectangle(2, 4);
    var s3 = new Triangle(3, 5);

    Stream.of(s1, s2, s3, 42).forEach(e -> {
      var area = switch (e) {
        case Circle c -> "Radius of the circle: " + c.radius + " and area: "+ c.area();
        case Rectangle r -> "Height "+ r.height + ", width: "+ r.width +" and the area of Rectangle: " + r.area();
        case Shape s -> "Area of the Shape: " + s.area();
        case Integer n -> "It is: " + n;
        default -> "not supported";
      };
      System.out.println(area);
    });
  }

  private interface Shape {

    int area();
  }

  record Triangle(int baseLength, int height) implements Shape {

    @Override
    public int area() {
      return baseLength * this.height / 2;
    }
  }

  record Rectangle(int height, int width) implements Shape {

    @Override
    public int area() {
      return height * width;
    }
  }

  record Circle(int radius) implements Shape {

    @Override
    public int area() {
      return (int) (Math.pow(this.radius, 2) * Math.PI);
    }
  }
}
```

Since this is also a [preview feature](https://openjdk.java.net/jeps/12), developers require adding `--enable-preview` while compiling the above example.   

**Conclusion:**

There you have it; those are the five features I have been looking forward to most.

Please keep in mind that none of these features should be used in a production setting without first going through the preview process.

However, it is always recommended to try them, and feedback is greatly appreciated if an issue is found.

This set of features, however, is all we expect to see in the upcoming LTS version.

Finally, if you liked this article and want to read more like it in the future, [follow me on Twitter](https://twitter.com/bazlur_rahman) (why wouldn't you?).
