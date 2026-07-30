---
title: "Introducing Bld: A New Pure Java Build System"
slug: "introducing-bld-a-new-pure-java-build-system"
date: "2023-04-10T20:12:47+00:00"
lastmod: "2023-05-23T12:56:08+00:00"
description: "We created bld because we're not really interested in build tools. We use them because we have to, but we'd rather just get on with coding."
authors:
  - "geert-bevin"
image: "https://foojay.io/wp-content/uploads/2023/04/og-bld.png"
categories:
  - "Developer Tools"
  - "Release Notes"
  - "RIFE2"
  - "Tools"
tags:
related_posts:
  - "getting-started-with-rife2-java-web-framework-v1-0-0"
  - "web-app-startup-in-3ms-with-rife2-and-graalvm"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "why-i-moved-my-blog-to-rife2-after-23-years"
enlighterjs: true
frozen: false
---

[RIFE2's `bld`](https://rife2.com/bld) is a new build system that allows you to write your build logic in pure Java.

`bld` was initially created for RIFE2 web applications, but has already moved outside of that narrow focus and is now used for non-web projects also, like the [RIFE2 framework](https://rife2.com) itself and our [URL Encoder for Java library](https://github.com/gbevin/urlencoder).

We created `bld` because we're not really interested in build tools. We use them because we have to, but we'd rather just get on with coding the real stuff.

`bld` is designed with the following principles in mind:

* tasks don't happen without you telling them to happen
* no auto-magical behavior, task behavior is explicit and API-defined
* managing libs yourself is fine, having that automated also, or mix and match
* build logic is written in Java, with all the advantages of Java
* standard collection of Java-centric tasks for common operations
* `bld` is part of RIFE2, if you have the RIFE2 jar, you have the build system

Where does bld fit? {#h2-0-where-does-bld-fit}
----------------------------------------------

From a very high level, build tools can be organized in a matrix:

* either your tool is declarative or in code
* either your tool first describes a plan or immediately executes a plan

|        | Declarative | Code | Describes | Immediate |
|--------|-------------|------|-----------|-----------|
| Maven  | ✅           |      | ✅         |           |
| Gradle |             | ✅    | ✅         |           |
| `bld`  |             | ✅    |           | ✅         |

Writing your build logic in the same language as your application (Java), significantly reduces the cognitive load, and taking actions immediately without having to mentally construct a described plan, makes it easier to reason about your build.

`bld` lets your build logic get out of the way so that you can focus on writing applications.

Designed for modern Java {#h2-1-designed-for-modern-java}
---------------------------------------------------------

`bld` relies on Java 17 and leverages many of the features that this version of Java provides.

Thanks to the modern language constructs, your Java build logic ends up looking very concise, is easily readable and understood by any IDE.

You automatically get support for auto-completion and javadoc documentation, and you can split your build logic into multiple files and classes when you outgrow a single file.

Here is a complete `bld` file for a Java Hello World application, nothing else is needed to be able to run it, test it and deploy it:

<br />

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example;

import rife.bld.Project;
import java.util.List;
import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class MyappBuild extends Project {
    public MyappBuild() {
        pkg = "com.example";
        name = "Myapp";
        mainClass = "com.example.MyappMain";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);
        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,9,2)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,9,2)));
    }

    public static void main(String[] args) {
        new MyappBuild().start(args);
    }
}</pre>

Creating a new project {#h2-2-creating-a-new-project}
-----------------------------------------------------

After [installing `bld`](https://github.com/rife2/bld/wiki/Installation), you can create a new Java application with the package `com.example` and the name `myapp`, by typing the following:

<br />

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">bld create-blank com.example myapp</pre>

The project template that is integrated into `bld` is used to create the complete structure of your application and the library dependencies required to compile and run it are automatically downloaded.

This is the only time that any downloads happen without you telling `bld` to do so.

You can now go into your project directory and for instance run it:

<br />

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./bld compile run</pre>

These also work:

<br />

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./bld jar
./bld uberjar
./bld clean</pre>

Java and RIFE2 powered {#h2-3-java-and-rife2-powered}
-----------------------------------------------------

`bld` project files benefit from all the power of Java and RIFE2.

You can for instance extract logic out into classes with custom `bld` operations, or you can create specialized project classes that make it easy to apply common traits, or you can use the RIFE2 template engine to generate files during your build cycle, ... the possibilities are endless.

Learn more {#h2-4-learn-more}
-----------------------------

You can learn more on the [RIFE2 `bld` wiki](https://github.com/rife2/bld/wiki), or by reaching out to us on [Discord](https://discord.gg/DZRYPtkb6J) or [Mastodon](https://uwyn.net/@gbevin).
