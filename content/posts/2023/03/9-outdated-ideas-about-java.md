---
title: "9 Outdated Ideas About Java | Foojay.io Today"
slug: "9-outdated-ideas-about-java"
date: "2023-03-11T08:56:17+00:00"
lastmod: "2023-07-28T08:01:17+00:00"
description: "In this article, we want to look into some false assumptions and outdated ideas about Java based on early versions."
authors:
  - "frankdelporte"
image: "/images/posts/2023/03/9-outdated-ideas-about-java/nine-outdate-ideas.jpg"
categories:
  - "Java Core"
  - "JBang"
  - "Opinion"
tags:
related_posts:
  - "java-where-the-wild-code-isnt"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "are-java-security-updates-important"
  - "foojay-podcast-83"
enlighterjs: true
frozen: false
---

Since the first release of Java in 1995, a lot has changed in both software and hardware, and each evolution impacts the next.

Powerful processors and cheaper memory allow software to do more and do it faster.

As Azul Deputy CTO Simon Ritter says, "***Java shows no danger of becoming the new COBOL. By adding new features to the platform under the six-month release cadence, we are seeing Java evolve faster than ever before.***"

In this article, we want to look into some false assumptions and outdated ideas about Java.
![](/images/posts/2023/03/9-outdated-ideas-about-java/nine-outdate-ideas-1024x410.jpg)

1. Myth: Java is old and not evolving {#h2-0-1-myth-java-is-old-and-not-evolving}
---------------------------------------------------------------------------------

Why do people think Java has been around so long? PHP is just as old, and Python is four years older.

Maybe it's because until Java 9, Java didn't have a set release schedule.

A new version was released whenever a predefined list of new features was finished.

But since 2018, Java has had a six-month release cycle on fixed dates, and each new version contains features that are fully finished and tested by the community, along with new and experimental features still in progress.

Still, they will only be available if you compile your application and start it with additional flags.

Thanks to this new approach, each new version brings both performance and coding improvements.

And because backward compatibility is very well managed in OpenJDK, you can run existing applications in most cases with newer JDKs without code changes while still benefitting from improved startup times, lower memory usage, and better Garbage Collection.

Here's a very brief overview of some of the changes in OpenJDK versions, but be aware these are only a few of the many new features and improvements.

Some features got first introduced as a preview and only got finalized in a later version.

|  Version   |                                                                                          Changes                                                                                          |
|------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| OpenJDK 19 | New preview features: -- Virtual threads -- Pattern matching for switch                                                                                                                   |
| OpenJDK 18 | Switch expressions further extended                                                                                                                                                       |
| OpenJDK 17 | Sealed Types                                                                                                                                                                              |
| OpenJDK 16 | Record classes Pattern matching for instanceof                                                                                                                                            |
| OpenJDK 15 | Text blocks                                                                                                                                                                               |
| OpenJDK 14 | Helpful nullpointerexceptions Streaming from running application to Flight Recorder Java Packager (jpackage) Pattern matching, see "Use Pattern Matching to Simplify Java                 |
| OpenJDK 13 | Improved versions of preview features                                                                                                                                                     |
| OpenJDK 12 | First preview feature of Text blocks                                                                                                                                                      |
| OpenJDK 11 | Run single file source code Flight recorder                                                                                                                                               |
| OpenJDK 10 | Local-Variable Type Inference (introduction of var)                                                                                                                                       |
| OpenJDK 9  | Modularity: how applications work and can be distributed Java Dependency Analyser (jdeps) Java Shell (jshell)                                                                             |
| OpenJDK 8  | Lambdas and streams introduced a completely new style of programming -- It's a different thought process -- Works functional instead of procedurally -- Default methods on Java Interface |

**Fact: the 6-months release cycle brought a lot of evolution to OpenJDK.**

2. Myth: You need to compile your code before you can run it {#h2-1-2-myth-you-need-to-compile-your-code-before-you-can-run-it}
-------------------------------------------------------------------------------------------------------------------------------

Indeed, Java is a compiled language, and the best way to execute it is to compile your code into a class file.

That file contains byte code that can be run by the Java Virtual Machine (JVM) on any platform.

But now there are other ways to execute Java code!

**Java 9** brought us `jshell` as part of the Java Development Kit (JDK). This is a command line interface to execute Java code and can, for instance, be used to test some Java code. As described in the final "Dependences" chapter of [JEP 222](https://openjdk.org/jeps/222) the code is still compiled, but this happens behinds the scenes.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% jshell
|  Welcome to JShell -- Version 18.0.1
|  For an introduction type: /help intro

jshell&gt; String message = "Hello World";
message ==&gt; "Hello World"

jshell&gt; "Test: " + message
$2 ==&gt; "Test: Hello World" 

jshell&gt; 8*9
$3 ==&gt; 72</pre>

**Java 11** introduced a new way to **execute single-file code**. This allows you to run "java HelloWorld.java," for instance, just like how you would execute a PHP or bash script.

And other tools provide excellent added value to this topic. Take a look at **[JBang](https://www.jbang.dev/)**, which lets you execute code that uses dependencies without the need to compile your code or use a build tool like Maven or Gradle. And if you didn't install Java yet on your machine, it will do this for you!

**Fact: behind the scenes, indeed Java code must be compiled before it can be executed. But different tools are available to "hide" this process and make it very easy to execute Java code directly.**

3. Myth: Java is slow {#h2-2-3-myth-java-is-slow}
-------------------------------------------------

Java has been a Just-In-Time Compiled language since 1999.

The current Hotspot compiler and the improved Falcon compiler developed by Azul run Java applications on par with C++ applications.

Java is much faster than most popular scripted languages like Python or JavaScript.

Java applications are slower to start than C++ applications, requiring a specific warm-up till the Just In Time Compiler can take effect.

This effect made Java applications on the desktop noticeably slower to start than native C++ applications on older laptops in the early 2000s and led to the impression that Java is slow.

Java may not be very popular for desktop applications nowadays, but it is the most commonly used language on the server side.

**Fact: Java runs "most of the internet" and dominates custom-developed business applications and is [the preferred runtime for the top trading companies](https://www.azul.com/use-cases/trading-risk/), thanks to its speed.**

4. Myth: Java is insecure {#h2-3-4-myth-java-is-insecure}
---------------------------------------------------------

In the late '90s and 2000s, browser plugins were a popular means of bringing applications to the internet where plain HTML was insufficient.

The most popular technologies were **Flash, ActiveX, Silverlight, and Java Applets**.

All those plugin-based technologies are obsolete and outright banned in the corporate environment as their deployment and security model were very poor.

**Flash** was the most famous entry point for attacking desktops and mobile phones.

While Java Applets were better sandboxed and safer than Flash, they shared the same underlying issue and contributed to the "Java is insecure" myth.

Java applications running on a maintained Java version (Azul offers support for Java 6, initially released in 2007, and onwards) are safer than most other programming languages as the Java Runtime can be updated independently from the application itself.

**Standardized JavaScript has superseded all the above plugin approaches as part of the HTML5 standard.**

Another argument that proves Java is a secure environment are the four releases per year of the active versions.

Many distributors release such improved runtimes within hours or days after the list of detected Common Vulnerabilities and Exposures (CVE) is published.

[Azul provides a list of the most recent CVEs](https://docs.azul.com/core/cve), and in which versions of JDK they were handled.

**Fact: A fully secure system doesn't exist, but having a workflow that collects, documents, and fixes issues guarantees that the security of Java is continuously guarded and improved.**

5. Myth: It's challenging to install Java on your machine, and the license is not clear {#h2-4-5-myth-it-s-challenging-to-install-java-on-your-machine-and-the-license-is-not-clear}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

As OpenJDK is an open-source project, anyone can create a JVM to execute Java programs.

Luckily many non-profit and commercial organizations have taken on this role.

This has led to a long list of distributions you can use on all kinds of devices, ranging from high-end servers to even inexpensive boards like the Raspberry Pi. But how do you select such a version?

Take whichever you want that will work on your device, as they are all based on the same OpenJDK branches and (should be) tested to comply with the specifications! Some companies -- like Azul -- can provide extra tools, support, and licenses used on top of that, so check the websites of those providers to discover what they offer.

[Foojay.io offers a search tool](https://foojay.io/download) to find all available OpenJDK distributions for all platforms.

If you are running on macOS or Linux, you should look at **[SDKMAN](https://sdkman.io/)**. This tool can show you a complete list of all available distributions for your system and install them with a single command. For instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sdk install java 17.0.4-zulu</pre>

**Fact: most of the OpenJDK distributions provide installers for the various platforms and tools like SDKMAN allow to easily install and switch between versions.**

6. Myth: You need a Java runtime installed on your PC to run Java code {#h2-5-6-myth-you-need-a-java-runtime-installed-on-your-pc-to-run-java-code}
---------------------------------------------------------------------------------------------------------------------------------------------------

Traditionally a Java Runtime (JRE) installed by the user independently (as part of a JDK) is used to execute a Java program.

The recommended alternative since **OpenJDK 11** is to bundle the required JRE with the application for the convenience of deployment and maximum compatibility.

`jlink` (see [JEP 282](https://openjdk.org/jeps/282)) is a tool that can assemble modules and their dependencies into a custom Java Runtime image.

You could say it creates the minimal required Java Runtime Environment (JRE) specifically supporting your application.

Since **OpenJDK 14** , `jpackage` was added (see [JEP 343](https://openjdk.org/jeps/343)) to create a native executable based on the output of `jlink`. `jpackage` creates a single executable that unpacks automatically without user interaction.

There are other packagers too. For instance, "**[GluonFX Plugin for Maven](https://docs.gluonhq.com/#_gluonfx_plugin_for_maven)**" can create native applications for Windows, macOS, Linux, Android, and iOS. Even mobile applications can also be created with Java and run on all your devices!

**Fact: Yes, having a Java runtime installed on your computer is the easiest way to run Java applications. But other tools are available to create executables that have the runtime included.**

7. Myth: Your application stops while memory cleanup is ongoing {#h2-6-7-myth-your-application-stops-while-memory-cleanup-is-ongoing}
-------------------------------------------------------------------------------------------------------------------------------------

In some cases, that's precisely what you want!

For long-running batch operations, the correctness of the result is crucial above the execution speed.

And in such a case, a so-called "Stop-The-World" Garbage Collector (GC) is the best option.

But over the years, many different GCs have been implemented and were part of Java. Some of them run isolated from your application threads (concurrent GC) and have almost no impact on executing your code.

Azul has a proprietary GC implementation: the **C4 GC (Continuously Concurrent Compacting Collector)** available in Azul Prime Build of OpenJDK.

This version is used by all the world's top 10 trading companies and six of the top 10 U.S. financial firms, all companies that want to achieve transaction speeds below milliseconds that never should be stopped by a GC.

With flags, you can select which GC you want to use. For more info on this topic and these flags, see the article: "[What Should I Know About Garbage Collection as a Java Developer?](https://foojay.io/today/what-should-i-know-about-garbage-collection-as-a-java-developer/)"

**Fact: It depends! In some use cases, a Stop-The-World Garbage Collector is exactly what you need. For other use cases, different Garbage Collectors are available which work differently and don't have these stops.**

8. Myth: Java only supports single inheritance {#h2-7-8-myth-java-only-supports-single-inheritance}
---------------------------------------------------------------------------------------------------

Java has no multiple inheritance of state indeed.

But JDK 8 introduced **multiple inheritance of behavior** that we can use to avoid the so-called Diamond problem.

Let's look at an example with two classes, `A` and `B`, having a method `doSomething()`.

When a class `C` would inherit from both `A` and `B`, calling the method `doSomething()` would introduce ambiguity as both `A` and `B` provide this method, and it's not clear for the compiler which one you want to use.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class ExampleC implements ExampleA, ExampleB {
    @Override
    public void doSomething() {
        System.out.println("Output of C");
    }

    public static void main(String args[]){
        ExampleC example = new ExampleC();
        example.doSomething();
    }
}

interface ExampleA {
    default void doSomething() {
        System.out.println("Output of A");
    }
}

interface ExampleB {
    default void doSomething() {
        System.out.println("Output of B");
    }
}</pre>

The above code without the `doSomething`-method in `ExampleC` will generate this error:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class ExampleC inherits unrelated defaults for doSomething() from types ExampleA and ExampleB</pre>

But by adding it, the compiler understands how we want this method to behave and will output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Output of C</pre>

**Fact: Since JDK 8 multiple inheritance of behavior is available.**

9. Myth: Interfaces can't contain implementations {#h2-8-9-myth-interfaces-can-t-contain-implementations}
---------------------------------------------------------------------------------------------------------

The default methods and static methods mentioned in the previous topic also removed this limitation from Java.

They allow you to put implementation within an interface.

As you can see in the example code above, both the interfaces `ExampleA` and `ExampleB` contain a method executing code.

**Fact: This is no longer true, and implementations can be added to an interface.**

Conclusion {#h2-9-conclusion}
-----------------------------

Let's try to dismiss outdated ideas about Java.

Since the switch to a six-month release cycle in 2018, the Java language and tools have seen a significant evolution and a lot of changes!

Some of these are "under the hoods" for improved speed and memory usage, while others focus on developer experience, making code easier to write and maintain.

Many other evolutions are ongoing and are already part of the latest JDKs as preview features or in development.

Next, there is a long list of new ideas within the Java community.

Some of these new ideas are already described, as seen in the "Draft and submitted JEPs" section of [openjdk.org](https://openjdk.org/jeps/0).
