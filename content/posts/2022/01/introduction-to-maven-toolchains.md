---
title: "Introduction to Maven Toolchains | Foojay.io Today"
slug: "introduction-to-maven-toolchains"
date: "2022-01-25T08:20:47+00:00"
lastmod: "2022-01-25T14:32:30+00:00"
description: "How to make sure I can build projects projects on Java 8, 11, and 17 without having to constantly switch Java runtimes?"
authors:
  - "mthmulders"
image: "/images/posts/2022/01/introduction-to-maven-toolchains/toolchain-minipoll-550x510.png"
categories:
  - "Maven"
tags:
related_posts:
  - "fixing-vulnerabilities-in-maven-projects"
  - "faster-maven-builds-1"
  - "understanding-apache-maven-part-1-the-basics"
  - "enterprise-java-quality-gates-ai"
enlighterjs: true
frozen: false
---

Java evolves at a much faster pace than it used to do.

But not all of the projects we work on keep up with that pace.

I have projects on Java 8, 11, and 17 and sometimes I want to play with early access builds of newer versions as well.

How to make sure I can build them without having to constantly switch Java runtimes?

Switching Java versions the whole day long {#h2-0-switching-java-versions-the-whole-day-long}
---------------------------------------------------------------------------------------------

Switching Java versions on the command line [doesn't have to be hard](https://maarten.mulders.it/2017/02/quickly-switch-java-versions-on-macos/). In my case, it's as easy as typing `j8`, `j11`, `j17`. But doing that every time you're seeing that "release version 17 not supported" is a bit tedious. More importantly, it doesn't solve the root cause of the issue.

So, what *is* the root cause, you ask?

The root cause here is that by default, the [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/) will use the Java compiler that comes with the Java runtime that Maven runs in. You can see which one that is by inspecting `mvn -version`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$ mvn -version
Apache Maven 4.0.0-alpha-1-SNAPSHOT (9e19b57c720d226b0b30992535819f700a665d14)
Maven home: /usr/local/Cellar/maven-snapshot/4.0.0-alpha-1-SNAPSHOT_117/libexec
Java version: 11.0.10, vendor: AdoptOpenJDK, runtime: /Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home
Default locale: en_GB, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.7", arch: "x86_64", family: "mac"</pre>

In this example, Maven uses a Java 11 Development Kit.

But in my project, I've configured the Maven Compiler Plugin to set the `-release` argument for the compiler to **17** , by setting the `maven.compiler.release` property.

(To target Java versions below 9, you should set two properties: `maven.compiler.source` and `maven.compiler.target`.)

The compiler from the Java 11 Development Kit obviously doesn't know how to target Java 17, hence we see "release version 17 not supported".

Toolchains to the rescue! {#h2-1-toolchains-to-the-rescue}
----------------------------------------------------------

Luckily, the solution is right at our disposal. In fact, the ["Compiling Sources Using A Different JDK" guide](https://maven.apache.org/plugins/maven-compiler-plugin/examples/compile-using-different-jdk.html) of the Maven Compiler Plugin starts with it:
> The preferable way to use a different JDK is to use the toolchains mechanism.

So what exactly is a toolchain? The same guide, a few lines later, summarises:
> A toolchain is a way to specify the path to the JDK to use for all of those plugins in a centralised manner, independent from the one running Maven itself.

The last part of that sentence is very important, so let me stress that once more: a toolchain is **independent from the one running Maven itself**.

So, how do we employ this?

First, we use the **toolchain** goal of the [Apache Maven Toolchains Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/) to check that the toolchains requirements for a project can be satisfied using the configured toolchains:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;project&gt;
  &lt;!-- omitted for brevity --&gt;
  &lt;build&gt;
    &lt;plugins&gt;
      &lt;plugin&gt;
        &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
        &lt;artifactId&gt;maven-toolchains-plugin&lt;/artifactId&gt;
        &lt;version&gt;3.0.0&lt;/version&gt;
        &lt;configuration&gt;
          &lt;toolchains&gt;
            &lt;!-- this project needs a JDK toolchain, version 17 --&gt;
            &lt;jdk&gt;
              &lt;version&gt;17&lt;/version&gt;
            &lt;/jdk&gt;
          &lt;/toolchains&gt;
        &lt;/configuration&gt;
        &lt;executions&gt;
          &lt;execution&gt;
            &lt;goals&gt;
              &lt;goal&gt;toolchain&lt;/goal&gt;
            &lt;/goals&gt;
            &lt;!-- the toolchain goal binds to the validate phase automatically --&gt;
          &lt;/execution&gt;
        &lt;/executions&gt;
      &lt;/plugin&gt;
    &lt;/plugins&gt;
  &lt;/build&gt;
&lt;/project&gt;</pre>

The above snippet says: we specify that the project needs a toolchain of type JDK with version 17. If we try to build the project again, the build still fails, but the message is different:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[INFO] --- maven-toolchains-plugin:3.0.0:toolchain (default) @ sample-project ---
[INFO] Required toolchain: jdk [ version='17' ]
[ERROR] No toolchain found for type jdk
[ERROR] Cannot find matching toolchain definitions for the following toolchain types:
jdk [ version='17' ]</pre>

That's a clear message: Maven cannot build this project as there is no JDK toolchain with version 17 installed. Well - there is, but we didn't tell Maven where to find it.

Defining Toolchains {#h2-2-defining-toolchains}
-----------------------------------------------

We can do that using the [Toolchain Configuration](https://maven.apache.org/ref/3.6.3/maven-core/toolchains.html), which lives in **\~/.m2/toolchains.xml**.

To declare the JDK 17 toolchain that lives on my machine, I should write:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;?xml version="1.0" encoding="UTF8"?&gt;
&lt;toolchains&gt;
  &lt;toolchain&gt;
    &lt;type&gt;jdk&lt;/type&gt;
    &lt;provides&gt;
      &lt;version&gt;17&lt;/version&gt;
    &lt;/provides&gt;
    &lt;configuration&gt;
      &lt;jdkHome&gt;/Library/Java/JavaVirtualMachines/adoptopenjdk-17.jdk/Contents/Home&lt;/jdkHome&gt;
    &lt;/configuration&gt;
  &lt;/toolchain&gt;
&lt;/toolchains&gt;</pre>

As you can see, this file contains the full path to a Java installation.

This makes the file specific to the machine where it is stored. That's why its location is in the **.m2** directory for the local user, and why the file cannot be part of the project's source code version control. Everyone who works on the team will need their own copy of the file, adapting it as needed for the correct paths. That also includes the build servers where the project will be built!

Popularity of Toolchains {#h2-3-popularity-of-toolchains}
---------------------------------------------------------

Some ten months ago, I [asked around on Twitter](https://twitter.com/mthmulders/status/1367754038826201089) to see if people know this feature, and whether they use it.

<img fetchpriority="high" decoding="async" width="550" height="510" class="size-medium wp-image-52099" src="/images/posts/2022/01/introduction-to-maven-toolchains/toolchain-minipoll-550x510.png" alt="Toolchains mini-poll">

<br />

Although the response wasn't very large, it's interesting to have a look at the results:

1. Roughly half of the people don't know that Toolchains exist.
2. Roughly a third of the people that knows Toolchains doesn't use it.

How can this be? It seems like a powerful feature. Even the people that know Toolchains don't always use it.

I think part of the explanation is that Maven itself is written in Java.

Imagine if Maven was written in another language. In that case, you would always have to specify where to find a Java Development Kit, as Maven wouldn't know it automatically. But now that Maven runs on the JVM, it already knows one JVM it could use. It may not be the best one for the current project, but at least it is one, and it will attempt to use it.

What's Next? {#h2-4-what-s-next}
--------------------------------

We already saw that the Maven Compiler Plugin understands the concept of Toolchains and knows how to use it.

But that's not the only plugin that may benefit.

Indeed, many official Maven plugins understand the concept and use a toolchain when configured. This includes the Javadoc, JAR and Surefire plugins, to name a few.

Even some non-official plugins work with toolchains, like the Protocol Buffer and the Keytool plugin. The full list is in the [Guide to Using Toolchains](http://maven.apache.org/guides/mini/guide-using-toolchains.html).

Apart from the JDK toolchain, it is even possible to declare ones own toolchains.

That is way beyond the scope of this article, but if you're interested, the [Toolchain Plugin documentation](https://maven.apache.org/plugins/maven-toolchains-plugin/toolchains/custom.html) gets you started.
