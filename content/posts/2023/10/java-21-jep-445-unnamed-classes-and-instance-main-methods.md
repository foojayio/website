---
title: "Java 21 - JEP 445 - Unnamed Classes and Instance Main Methods"
slug: "java-21-jep-445-unnamed-classes-and-instance-main-methods"
date: "2023-10-25T09:25:48+00:00"
lastmod: "2023-11-07T15:56:05+00:00"
description: "The goal of JEP 445 is to make it easier to get started with Java, as it’s all about reducing the number of keywords when you write, for instance, your very first HelloWorld Java code."
canonical: "https://webtechie.be/post/2023-09-18-jep-445-unnamed-classes-and-instance-main-methods/"
authors:
  - "frankdelporte"
image: "/images/posts/2023/10/java-21-jep-445-unnamed-classes-and-instance-main-methods/newer-than-8.jpg"
categories:
  - "Java"
  - "Java Beginner"
  - "JDK21"
  - "JEPs"
tags:
related_posts:
  - "disco-api-helping-you-to-find-any-openjdk-distribution"
  - "java-21-is-available-today-and-its-quite-the-update"
  - "foojay-podcast-28"
enlighterjs: true
frozen: false
---

Java 21, released on September 19th, 2023, brings many new features, 8 which are fully integrated and 7 which are incubator or preview.

In this article, I want to highlight one of those preview features: [Java Enhancement Proposal (JEP) 445: "Unnamed Classes and Instance Main Methods"](https://openjdk.org/jeps/445).

**It's a preview feature, meaning you need extra flags to use it. The goal of JEP 445 is to make it easier to get started with Java, as it's all about reducing the number of keywords when you write, for instance, your very first HelloWorld Java code.**

It's ideal for students or anyone who wants to start experimenting with Java. It may also help make Java more popular in boot camps, where JavaScript and Python dominate now.

{{< youtube Wrx7iVCVTwg >}}

JEP 330: "Launch Single-File Source-Code Programs" {#h2-0-jep-330-launch-single-file-source-code-programs}
----------------------------------------------------------------------------------------------------------

The video above and this post are inspired by [a post by Tom Cools](https://www.tomcools.be/post/april-2023-onboarding-now/), and to be honest, I stole a bit of his approach as I first want to show you the older [JEP 330: "Launch Single-File Source-Code Programs"](https://openjdk.org/jeps/330).

Thanks to this feature, introduced in Java 11 you can already execute a single Java-file without the need to compile the code to byte code with `javac` as visiualized in this diagram:
![](/images/posts/2023/10/java-21-jep-445-unnamed-classes-and-instance-main-methods/jep330.png)

With the changes brought by JEP 330, the conversion from Java code to byte code, is "hidden". It still happens, but you, as a user, don't need to take care of it.

JBang {#h2-1-jbang}
-------------------

Thanks to this JEP 330, it already became a lot easier to run Java code with one command, but it also has a limitation. When that single file application needs dependencies, you still need to go for a complete Maven or Gradle project to define these dependencies and versions.

Not related to the JEP, but a possible alternative solution in this case is [JBang](https://www.jbang.dev/), it's a tool also directed towards students and others experimenting with Java. It has a syntax to define that it needs to be handled as a script and can define dependencies to be included. I used JBang to create [multiple tutorials in the Pi4J project](https://pi4j.com/examples/jbang/).

JEP 445: "Unnamed Classes and Instance Main Methods" {#h2-2-jep-445-unnamed-classes-and-instance-main-methods}
--------------------------------------------------------------------------------------------------------------

Now it's finally time to talk about the "main" subject of this post, JEP 445.

### Code Examples {#h3-3-code-examples}

Until now, a minimal "Hello World" Java application, needs this code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class HelloWorld { 
    public static void main(String[] args) { 
        System.out.println("Hello World");
    }
}</pre>

Although only one line produces the output, there is a lot of "clutter" around it that needs to be explained, or in most cases is taken "as is" and doesn't get explained to newbies. Thanks to JEP 445, that same code can now be simplified to:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void main() {
    System.out.println("Hello, World!");
}</pre>

Because this is a preview feature, additional flags are needed to execute this code. Thanks to JEP 330, we don't need to compile it. If you save the above code in a file `HelloWorld.java`, it can be executed directly, with a Java version 21, with `java` like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java --source 21 --enable-preview HelloWorld.java</pre>

### Foojay Podcast: "Java 21 Has Arrived!" {#h3-4-foojay-podcast-java-21-has-arrived}

In [Foojay Podcast #28: "Java 21 Has Arrived!"](https://foojay.io/today/foojay-podcast-28), I talked with Simon Ritter, Mohamed Taman, and Piotr Przybyl about the many new features in Java 21. Not all of them find this JEP 445 the most interesting one, but they all agree it makes it easier to introduce the language to new people.

<iframe style="width: 100%; height: 241px;" src="https://app.springcast.fm/player/episode/71857?theme=springcast&amp;chapter=15" width="100%" height="241" frameborder="0" scrolling="no" seamless="true"></iframe>

<br />

Tools to Install Java {#h2-5-tools-to-install-java}
---------------------------------------------------

The [OpenJDK project](https://github.com/openjdk/jdk) only contains the sources for the full Java-project. The actual runtime and SDK are available from many different distributors.

Make sure to check out [SDKMan](https://sdkman.io/) and [JDKMon](https://github.com/HanSolo/JDKMon) to easily install Java on your system, switch between versions, and monitor installed versions.

More info about this topic is available on this Foojay post: ["Disco API: Helping You To Find Any OpenJDK Distribution"](https://foojay.io/today/disco-api-helping-you-to-find-any-openjdk-distribution/).

Conclusion {#h2-6-conclusion}
-----------------------------

This JEP 445 won't bring many improvements for experienced developers, but it may bring Java closer to education and help to experiment with the language.

As it is a preview feature, you still need additional flags to use it, but hopefully the next Java-release will bring this to its full use!

As always, start using the new Java version ASAP for your development and try to update your production systems so you can take full advantage of all the evolutions in the OpenJDK project!
![](/images/posts/2023/10/java-21-jep-445-unnamed-classes-and-instance-main-methods/newer-than-8.jpg)
