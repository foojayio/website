---
title: "The FFM API: How OpenJDK Changed the Game for Native Interactions (And Made Pi4J Better!)"
slug: "the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better"
date: "2026-01-16T08:18:00+00:00"
lastmod: "2026-01-16T12:15:52+00:00"
description: "The Pi4J project is a Java library that allows you to control the GPIO pins and electronic components connected to a Raspberry Pi with pure Java code. It - by Frank Delporte"
canonical: "https://www.javaadvent.com/2025/12/ffm-api-for-java-on-raspberry-pi.html"
authors:
  - "frankdelporte"
image: "/images/posts/2026/01/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/pi4j-overview-scaled.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-83"
  - "project-panama-for-newbies-part-4"
  - "writing-c-code-in-java"
  - "project-panama-for-newbies-part-1"
enlighterjs: true
frozen: false
---

The [Pi4J project](https://www.pi4j.com/) is a Java library that allows you to control the GPIO pins and electronic components connected to a Raspberry Pi with pure Java code. It removes the complexity of using native libraries and the [Java Native Interface (JNI)](https://www.baeldung.com/jni), allowing you to focus on your application logic.

***This is a crosspost from the article I contributed to [this year's JVM Advent](https://www.javaadvent.com/2025/12/ffm-api-for-java-on-raspberry-pi.html).***
![](/images/posts/2026/01/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/pi4j-overview-1024x427.jpg)

In the Java Advent of 2020, I published "[Light up your Christmas lights with Java and Raspberry Pi](https://www.javaadvent.com/2020/12/light-up-your-christmas-lights-with-java-and-raspberry-pi.html)", using Java 11 and Pi4J V1.2. Wow, things have changed a lot: this article uses Java 25 and a snapshot of the soon-to-be-released Pi4J V4!

I became a contributor to Pi4J while working on my book "[Getting Started with Java on the Raspberry Pi](https://webtechie.be/books/)" around 2020. But even after many years of working on Pi4J's code, I get puzzled when I dive deep into its sources. Please help me, do you understand what's happening in this piece of code?

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">JNIEXPORT jobject JNICALL Java_com_pi4j_library_gpiod_internal_GpioD_c_1gpiod_1chip_1open
  (JNIEnv* env, jclass javaClass, jstring path) {
    struct gpiod_chip* chip;
    const char* nativeString = (*env)-&gt;GetStringUTFChars(env, path, NULL);
    chip = gpiod_chip_open(nativeString);
    (*env)-&gt;ReleaseStringUTFChars(env, path, nativeString);
    if(chip == NULL) {
      return NULL;
    }
    jclass cls = (*env)-&gt;FindClass(env, "java/lang/Long");
    jmethodID longConstructor = (*env)-&gt;GetMethodID(env, cls, "&lt;init&gt;", "(J)V");
    return (*env)-&gt;NewObject(env, cls, longConstructor, (jlong) (uintptr_t) chip);
}</pre>

It's one of the "connection points" between Java and the native libraries to communicate with the GPIO pins. Using JNI and [Java Native Access (JNA)](https://github.com/java-native-access/jna), Docker build environments to compile the native libraries to be used from Java, and a lot of complexity, it makes it possible to make the interaction from Java easy for the end user, but difficult to maintain and debug for the Pi4J developers.

In this post, I'll explain how the Foreign Function \& Memory API (FFM API) has revolutionized the way Java developers interact with native libraries and memory, and how this has significantly simplified the Pi4J project. This article is based on a talk I gave at the [Devoxx](https://www.youtube.com/watch?v=2BcWWWkb8ac) and JFall conferences about the history and evolution of this addition to OpenJDK.

A Quick History Lesson {#h2-0-a-quick-history-lesson}
-----------------------------------------------------

My Java journey started 15 years ago when I switched from C# to Java and never looked back. That was right in the middle of Java's 30-year history, and I've been following its evolution closely ever since. I even had the chance this year, to [talk to James Gosling, the "Father of Java", for the Foojay Podcast](https://foojay.io/today/foojay-podcast-71/).

In recent years, we have seen many evolutions in the Java language and virtual machine, such as improved switch-case, virtual threads, performance improvements, and more. One of the most significant recent developments has been [**Project Panama**](https://openjdk.org/projects/panama/) and the Foreign Function \& Memory (FFM) API that emerged from it.

Foreign Function \& Memory (FFM) API {#h2-1-foreign-function-memory-ffm-api}
----------------------------------------------------------------------------

The FFM API was officially released in Java 22 as a finalized feature. It represents years of work within Project Panama and has three main goals:

1. **Memory safety**: Safe access to off-heap memory while maintaining proper cleanup.
2. **Easy interaction**: Simple ways to call native libraries.
3. **High performance**: Matching or exceeding JNI's performance.

### The Problem With JNI {#h3-2-the-problem-with-jni}

JNI has been around since Java 1.1, and while it works, it has a few critical drawbacks:

* **Manual memory management** with a steep learning curve.
* **Complex implementation** requiring C headers and compilation steps.
* **Hard to use by design**: As I learned from Simon Ritter, a Sun engineer once said JNI was deliberately made difficult to discourage people from using it!

![](/images/posts/2026/01/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/ffm-quote-simon-ritter-1024x390.png)

There were attempts to improve this situation with libraries such as JNA and [Java Native Runtime (JNR)](https://github.com/jnr), but they came with their own overhead and limitations.

### How The FFM API Evolved {#h3-3-how-the-ffm-api-evolved}

The development of the FFM API is a fascinating story that shows how OpenJDK evolves through careful iteration. The project was broken down into separate JEPs (JDK Enhancement Proposals), which started being delivered in Java 14. As you will see, most of the JEPs were incubator or preview features, which can only be used with the `--enable-preview` flag, as you can [learn in this detailed explanation](https://docs.azul.com/core/incubator-preview-features).

#### Foreign Memory Access API

As a first step, the OpenJDK team created a new API to access off-heap memory, enabling safe, efficient access to foreign memory. None of these were finalized, as they got integrated into OpenJDK as incubator features, preparing for something bigger.

* [OpenJDK 14: JEP 370: Foreign-Memory Access API (Incubator)](https://openjdk.org/jeps/370)
* [OpenJDK 15: JEP 383: Foreign-Memory Access API (Second Incubator)](https://openjdk.org/jeps/383)
* [OpenJDK 16: JEP 393: Foreign-Memory Access API (Third Incubator)](https://openjdk.org/jeps/393)

#### Foreign Linker API

In the next step, statically typed pure-Java access to native code got integrated, again as an incubator feature.

* [OpenJDK 16: JEP 389: Foreign Linker API (Incubator)](https://openjdk.org/jeps/389)

#### Foreign Function \& Memory API

Finally, the FFM API was finalized in Java 22. It's a combination of the two previous APIs, and it went through incubator and preview phases across multiple Java releases.

* [OpenJDK 17: JEP 412: Foreign Function \& Memory API (Incubator)](https://openjdk.org/jeps/412)
* [OpenJDK 18: JEP 419: Foreign Function \& Memory API (Second Incubator)](https://openjdk.org/jeps/419)
* [OpenJDK 19: JEP 424: Foreign Function \& Memory API (Preview)](https://openjdk.org/jeps/424)
* [OpenJDK 20: JEP 434: Foreign Function \& Memory API (Second Preview)](https://openjdk.org/jeps/434)
* [OpenJDK 21: JEP 442: Foreign Function \& Memory API (Third Preview)](https://openjdk.org/jeps/442)
* [OpenJDK 22: JEP 454: Foreign Function \& Memory API](https://openjdk.org/jeps/454)

This iterative approach allowed the OpenJDK team to gather community feedback and ensure the API was stable, performant, and truly useful.

### Simple Code Examples {#h3-4-simple-code-examples}

Let me show you how much simpler things have become with a few examples.

#### Accessing Memory Directly

Here's a basic example of accessing memory directly:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void main() {
    // Open a confined Arena that manages off-heap memory
    // and will release it automatically.
    try (Arena arena = Arena.ofConfined()) {
        // Allocate 5 ints in the arena
        MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT, 5);

        // Fill the segment with random values for each int.
        System.out.print("Setting values: ");
        for (int i = 0; i &lt; 5; i++) {
            int randomValue = new Random().nextInt(100);
            segment.setAtIndex(ValueLayout.JAVA_INT, i, randomValue);
            System.out.print(randomValue + " ");
        }
        System.out.println("");

        // Print the values back from memory.
        System.out.print("Reading values: ");
        for (int i = 0; i &lt; 5; i++) {
            System.out.print(segment.getAtIndex(ValueLayout.JAVA_INT, i) + " ");
        }
        System.out.println("");
    }
}</pre>

Notice a few things about this code:

* I execute it with Java 25, which means I can use the new simplified main method delivered with [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512). As a result, I don't need to use a class and package, and a lot of the `main` method "clutter" is no longer needed in a simple example like this.
* The `Arena` manages memory cleanup automatically in the `try` block.
* The `MemorySegment` is a simple wrapper around a native memory address.
* No manual memory management is needed.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java ArenaDemo.java
Setting values: 16 7 27 50 80 
Reading values: 16 7 27 50 80 </pre>

A more extended example is [available here in `FFMMemoryManagement.java`](https://webtechie.be/code/ffm/FFMMemoryManagement.java), with a [Java 11 example here `Java11MemoryManagement.java`](https://webtechie.be/code/ffm/Java11MemoryManagement.java) illustrating how much more complex this was before the FFM API. The Java 11 version [must be executed with JBang](https://www.jbang.dev/) as described in the comments, to force the use of Java 11.

#### Calling Native Functions

To illustrate how to call a native library function, we'll make the most complex `String.length()` implementation 😉 A perfect example of how this can be done with FFM API within one simple-to-read method:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void main() throws Throwable {
    // The text we will use in the demo.
    var text = "Hello JVM Advent!";

    // Obtain a Linker that knows how to call
    // native (C) functions on this platform.
    Linker linker = Linker.nativeLinker();

    // Create a SymbolLookup that can find symbols
    // (like C functions) in the default native libraries.
    SymbolLookup lookup = linker.defaultLookup();

    // Create a MethodHandle to call the native C function
    MethodHandle strlen = linker.downcallHandle(
        // Look up the address of the `strlen` symbol,
        // or throw if it isn't found.
        lookup.find("strlen").orElseThrow(),
        // Describe the C function:
        // - returns long
        // - takes one pointer (address) argument
        FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
    );

    // Open a confined Arena that manages off-heap memory
    // and will release it automatically.
    try (Arena arena = Arena.ofConfined()) {
        // Allocate native memory for a C-style string
        // and copy "Hello World!" into it.
        MemorySegment str = arena.allocateFrom(text);

        // Call the native `strlen` function via the MethodHandle,
        // passing the string's memory address,
        // and cast the result to a long.
        long length = (long) strlen.invoke(str);

        // Print the result.
        System.out.println("Length with native library: " + length);
    }

    System.out.println("Length with String.length(): " + text.length());
}</pre>

No JNI headers, no C-compilation, just pure Java code!

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java LinkerDemo.java

Length with native library: 17
Length with String.length(): 17</pre>

Also, for this example, you can find a more extended example [here in `FFMNativeCalls.java`](https://webtechie.be/code/ffm/FFMNativeCalls.java), with a [Java 11 in `Java11NativeCalls.java`](https://webtechie.be/code/ffm/Java11NativeCalls.java).

#### Performance Comparison Example

I like demos that have a visual output. So I created a simple benchmark that generates moving gradients, which involves many memory writes. In both Java 11 and 25, I try to refresh the gradient every 5 milliseconds.
![](/images/posts/2026/01/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/ffm-gradient-demo.png)

The results speak for themselves (tested on a MacOS M2 with Azul Zulu 25):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ jbang Java11PixelBuffer.java
[jbang] Building jar for Java11PixelBuffer.java...
Interval: 57 - Generated 250000 pixels
Interval: 56 - Generated 250000 pixels

$ java FFMPixelBuffer.java
Interval: 5 - Generated 250000 pixels
Interval: 5 - Generated 250000 pixels</pre>

* **[Java 11 approach](https://webtechie.be/code/ffm/Java11PixelBuffer.java)** using `BufferedImage` and arrays: \~50-60 milliseconds per frame.
* **[FFM API approach](https://webtechie.be/code/ffm/FFMPixelBuffer.java)** with direct memory access: \~5 milliseconds per frame.

That's up to **11x performance improvement** just by avoiding the overhead of Java object management!

Why the FFM API Matters for Raspberry Pi Projects {#h2-5-why-the-ffm-api-matters-for-raspberry-pi-projects}
-----------------------------------------------------------------------------------------------------------

Let me first answer a more general question I get asked a lot: "**Why Java on a Raspberry Pi?**" The answer is simple: Java is the language that allows me to do everything. I can build user interfaces with JavaFX, make API calls to services, and leverage the extensive library ecosystem. When I started experimenting with Java on Raspberry Pi over five years ago, I didn't want to learn a new language. I wanted to learn how to use and interact with electronic components, using the tools I already knew and loved.

This is where the Pi4J project comes in: it's a Java library that lets you control the GPIO pins and electronic components connected to a Raspberry Pi. But here's the catch: this library has always relied on native C/C++ code to communicate with the hardware. Until now, that meant dealing with the complexity of JNI and JNA.

### Pi4J Architecture {#h3-6-pi4j-architecture}

[Pi4J uses a plugin architecture](https://www.pi4j.com/architecture/) in which different "providers" handle communication with GPIO pins. Previously, these providers relied on native libraries and JNI/JNA, which meant:

* Complex multi-layered code with up to 5 levels before executing GPIO operations.
* Need for Docker builds to compile native libraries.
* Cryptic JNI header generation and wizard-like C code.

These plugins get loaded dynamically at runtime, in the "black bar" in this architecture diagram:
![](/images/posts/2026/01/the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better/pi4j-architecture-1024x668.jpg)

### The FFM Transformation {#h3-7-the-ffm-transformation}

Pi4J now has an almost-ready FFM-based provider, which will be available in V4. The improvements are dramatic:

* Performance Benchmarks
  * Getting GPIO input state: **10x faster!**.
  * SPI communication: Significant improvement, though less dramatic.
* Code Simplification
  * Direct Java-to-kernel communication.
  * No more complex JNI layers.
  * Readable, maintainable Java code.
  * No need for native library compilation with a complex Docker build process.

### A Community Success Story {#h3-8-a-community-success-story}

What makes this even better is that the FFM implementation came from the community. [Nick Gritsenko (aka @DigitalSmile)](https://github.com/DigitalSmile) had already created a Java 22 library for GPIO interaction using FFM. When I discovered his work, I asked if we could use it. But even better, he contributed it directly to Pi4J himself and made it fit perfectly into the plugin architecture! This resulted in a [major pull request](https://github.com/Pi4J/pi4j/pull/458) that's now merged into the Pi4J V4 snapshot.

I'm very proud that this happens again and again. In the past, [Alexander Liggesmeyer added support for the Raspberry Pi 5](https://www.pi4j.com/blog/2024/20240318_interview_alexander_liggesmeyer/) with a new plugin. And at this moment, [Stefan Haustein](https://github.com/stefanhaustein), [Stephen More](https://github.com/mores), [Tom Aarts](https://github.com/taartspi), and others are actively working on a new [Pi4J drivers library](https://github.com/pi4j/pi4j-drivers/) and example implementations to make it much easier for everyone to create applications that interact with more complex electronic components, such as joysticks, LCD screens, LED strips, and more...

### Beyond Raspberry Pi {#h3-9-beyond-raspberry-pi}

The FFM-based implementation opens up exciting possibilities. Since it's based on standard Linux kernel methods available in Debian, we believe Pi4J could potentially work on:

* Orange Pi boards and other Linux-based SBCs (Single Board Computers).
* RISC-V processors running Debian.

I'm looking forward to experimenting with these different hardware platforms! Maybe you'll read more about that in next year's JVM Advent...

### Pi4J Examples Using the FFM API {#h3-10-pi4j-examples-using-the-ffm-api}

I often demo with a CrowPi -- a neat kit with a Raspberry Pi and pre-wired components in a single box. It's great for learning because you can't wire things incorrectly!

Here's a simplified example using JBang to blink an RGB LED, using a snapshot build of Pi4J V4. Once this version is released you can remove the line with `//REPOS` and update the version.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">/// usr/bin/env jbang "$0" "$@" ; exit $?

//REPOS mavencentral,pi4j-snapshots=https://oss.sonatype.org/content/repositories/snapshots
//DEPS com.pi4j:pi4j-core:4.0.0-SNAPSHOT
//DEPS com.pi4j:pi4j-plugin-linuxfs:4.0.0-SNAPSHOT

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.*;

/**
 * Execute with `jbang Pi4JExample.java`
 */
void main() throws InterruptedException {
    var pi4j = Pi4J.newAutoContext();

    var red = pi4j.digitalOutput().create(17);
    var green = pi4j.digitalOutput().create(27);
    var blue = pi4j.digitalOutput().create(22);

    // Blink pattern
    for (int i = 0; i &lt; 10; i++) {
        red.high();
        Thread.sleep(500);
        red.low();
        green.high();
        Thread.sleep(500);
        green.low();
        blue.high();
        Thread.sleep(500);
        blue.low();
    }
}</pre>

More examples like this, which can be executed with JBang, are available in the [Pi4J JBang repository](https://github.com/Pi4J/pi4j-jbang).

Important Notes {#h2-11-important-notes}
----------------------------------------

While working on the presentation about the FFM API and this article, I noticed a few points worth mentioning.

* **Memory Safety Warning** : When using FFM, you're stepping outside the "safe JVM-managed garbage collector". You'll see a warning about enabling native access with a few of the examples from this article. This is intentional as the OpenJDK team wants to ensure developers understand they're working with potentially dangerous operations. Use the `--enable-native-access` flag to acknowledge this and suppress the warning. This may change in future versions of OpenJDK.
* **JEPs Are Worth Reading** : If you're curious about how Java evolves, I highly recommend [reading the JDK Enhancement Proposals](https://openjdk.org/jeps/0). They're not just technical specifications! They're well-written documents that explain the thinking behind design decisions, include examples, and provide deep insights from the architects of Java. Similarly, [read more about the OpenJDK projects](https://openjdk.org/projects/), for instance, by subscribing to their mailing list to follow what is happening within such a project.
* **Keep Up With Java Releases**: Java has a six-month release cycle, and every release is a good one! Each brings many bug fixes, improvements, and evolutions (from projects and JEPs). If you can update your systems, especially your development systems, you should! The FFM API was introduced in Java 22, but I learned at Devoxx that Java 24 brought significant performance improvements to the FFM implementation, without any API changes! This shows that the OpenJDK team continues to optimize and improve existing features.

Conclusion {#h2-12-conclusion}
------------------------------

The FFM API has been a significant improvement for developers working with native code in recent years. It removes the complexity of JNI while maintaining, and even improving, performance. It opens new possibilities for Java in embedded systems and hardware interfacing. It will also drive closer integration of Artificial Intelligence (AI), Machine Learning (ML), and Large-Language Model (LLM) development with Java.

Feel free to experiment and contribute. OpenJDK and other open source projects, like Pi4J, thrive on community involvement!

If you're interested in Java on Raspberry Pi, check out:

* The [Pi4J website](https://www.pi4j.com/).
* My blog posts on [webtechie.be](https://webtechie.be/) and [Foojay.io](https://foojay.io/today/author/frankdelporte/).
* My ebook "[Getting Started with Java on the Raspberry Pi](https://webtechie.be/books/)", which I keep updating with new Java versions, Pi4J improvements, etc.

Remember: We must thank OpenJDK and all its contributors for amazing features like the Garbage Collector, JIT compilation, Lambdas, Virtual Threads, and now the FFM API. Java keeps getting better, and that's worth celebrating!

<br />
