---
title: "From a JAR to a full-fledged MacOS app"
slug: "from-a-jar-to-a-full-fledged-macos-app"
date: "2026-02-05T10:15:05+00:00"
lastmod: "2026-02-05T10:18:16+00:00"
description: "A couple of years ago, I developed a small Kotlin GUI to help me rename my files in batch. I actually created it with different JVM frameworks to compare - by Nicolas Frankel"
canonical: "https://blog.frankel.ch/jar-to-macos-app/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2026/01/cover_large-3.jpg"
categories:
  - "Java"
  - "Use Cases"
tags:
related_posts:
  - "apache-apisix-north-america-tour"
  - "apache-apisix-loves-rust"
  - "annotation-free-spring"
  - "an-example-of-overengineering-keep-it-wet"
enlighterjs: true
frozen: false
---

A couple of years ago, I developed a small [Kotlin GUI](https://blog.frankel.ch/state-jvm-desktop-frameworks/2/) to help me rename my files in batch. I actually created it with [different JVM frameworks](https://blog.frankel.ch/focus/state-jvm-desktop-frameworks/) to compare their relative merits. In any case, I didn't use it up until last week. And then, I was surprised to see that it didn't work to rename a network volume, although it had in the past. In this brief post, I aim to describe the issue and its solution.

The problem {#h2-0-the-problem}
-------------------------------

When launching the UberJAR, I couldn't see the network volumes. If I typed the path in the field, I couldn't see any sub-nodes. I searched online for a solution, and it mentioned giving the application the `Local Network` permissions: *Settings \> Privacy \& Security \> Local Network*. I tried to add the Java SDK or the UberJAR to the list, but I didn't find a way.

<img fetchpriority="high" decoding="async" class="aligncenter wp-image-122504 size-medium" src="/images/posts/2026/02/from-a-jar-to-a-full-fledged-macos-app/security-settings-531x510.png" alt="" width="531" height="510">

<br />

The solution {#h2-1-the-solution}
---------------------------------

The solution is straightforward: create a regular MacOS app from the UberJAR. At its simplest, a MacOS app is only a [folder with a specific structure](https://eclecticlight.co/2025/12/04/the-anatomy-of-a-macos-app/). You could replicate it by hand, but I'm lazy, and there are tools for this. [jpackage](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html) is such a tool. `jpackage` is one of the tools I learned about, am ecstatic about, used once, and forgot until the next time. Add one to the counter.

I naively used `jpackage` like this at first:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">jpackage \                                                      
  --type app-image \                                  #1
  --input target \                                    #2
  --name RenamerSwing \                               #3
  --main-jar renamer-swing-1.0-SNAPSHOT.jar           #4</pre>

1. Set value
2. Folder to include
3. Name of the app
4. Path to the UberJAR

It works and generates a MacOS app. We can launch the resulting app, and the OS asks whether you want to allow Local Network access. Done.

However, `jpackage` copies the whole `target` folder content into the app. It includes not only the original JAR, classes, but also the rest of the content.

Improving the build {#h2-2-improving-the-build}
-----------------------------------------------

I decided to improve the Maven build instead of calling `jpackage` manually.  

The first step is to move the UberJAR to an empty folder, so that we can set `jpackage`'s `input`.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;artifactId&gt;maven-resources-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.3.1&lt;/version&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;copy-jar-for-jpackage&lt;/id&gt;
            &lt;phase&gt;package&lt;/phase&gt;
            &lt;goals&gt;
                &lt;goal&gt;copy-resources&lt;/goal&gt;
            &lt;/goals&gt;
            &lt;configuration&gt;
                &lt;outputDirectory&gt;${project.build.directory}/jpackage-input&lt;/outputDirectory&gt;
                &lt;resources&gt;
                    &lt;resource&gt;
                        &lt;directory&gt;${project.build.directory}&lt;/directory&gt;
                        &lt;includes&gt;
                            &lt;include&gt;${project.build.finalName}.jar&lt;/include&gt;
                        &lt;/includes&gt;
                    &lt;/resource&gt;
                &lt;/resources&gt;
            &lt;/configuration&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;</pre>

The next step is actually to find a Maven plugin that wraps `jpackage`. I found [org.panteleyev:jpackage-maven-plugin](https://github.panteleyev.org/jpackage-maven-plugin/). Here's how to use it:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.panteleyev&lt;/groupId&gt;
    &lt;artifactId&gt;jpackage-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;1.6.5&lt;/version&gt;
    &lt;configuration&gt;
        &lt;name&gt;RenamerSwing&lt;/name&gt;                     &lt;!--1--&gt;
        &lt;appVersion&gt;${project.version}&lt;/appVersion&gt;
        &lt;vendor&gt;Nicolas Fränkel&lt;/vendor&gt;
        &lt;destination&gt;target&lt;/destination&gt;
        &lt;input&gt;target/jpackage-input&lt;/input&gt;          &lt;!--2--&gt;
        &lt;mainJar&gt;${project.build.finalName}.jar&lt;/mainJar&gt; &lt;!--3--&gt;
        &lt;mainClass&gt;${main.class}&lt;/mainClass&gt;
        &lt;type&gt;APP_IMAGE&lt;/type&gt;                        &lt;!--4--&gt;
    &lt;/configuration&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;jpackage&lt;/id&gt;
            &lt;phase&gt;package&lt;/phase&gt;
            &lt;goals&gt;
                &lt;goal&gt;jpackage&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;</pre>

1. App's name
2. Point to the folder created in the previous step
3. Path to the main JAR
4. Set value

Note that the parameters mimic the command line options.

Finishing touches {#h2-3-finishing-touches}
-------------------------------------------

There are two finishing touches: the version and the app's size.

Let's start with the version. The above configuration uses `${project.version}`. The build fails if it's suffixed with `-SNAPSHOT`, as a Mac app version must follow the pattern `major.minor.bugfix`. To fix this, we can leverage the [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/). It offers several goals, including [parse-version](https://www.mojohaus.org/build-helper-maven-plugin/parse-version-mojo.html) that destructures Maven's version into several properties.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.codehaus.mojo&lt;/groupId&gt;
    &lt;artifactId&gt;build-helper-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.6.0&lt;/version&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;parse-version&lt;/id&gt;
            &lt;phase&gt;initialize&lt;/phase&gt;
            &lt;goals&gt;
                &lt;goal&gt;parse-version&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;</pre>

We can replace the version's line with:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;appVersion&gt;${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}&lt;/appVersion&gt;</pre>

The second improvement is the final app size. The above configuration yields a 160MB app on my machine. Not exactly concerning for a JVM-based app, but not great either. On the good side, `jpackage` leverages `jlink` underneath; `jlink` creates a custom JRE. The app is self-contained.

`jpackage` uses all configured modules, or all by default. On the not-so-good side, we didn't configure any. By just adding the necessary modules, we can cut the app size by half, down to 82MB.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.panteleyev&lt;/groupId&gt;
    &lt;artifactId&gt;jpackage-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;1.6.5&lt;/version&gt;
    &lt;configuration&gt;
        &lt;name&gt;RenamerSwing&lt;/name&gt;
        &lt;appVersion&gt;${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}&lt;/appVersion&gt;
        &lt;vendor&gt;ch.frankel.blog&lt;/vendor&gt;
        &lt;destination&gt;target&lt;/destination&gt;
        &lt;input&gt;target/jpackage-input&lt;/input&gt;
        &lt;mainJar&gt;${project.build.finalName}.jar&lt;/mainJar&gt;
        &lt;mainClass&gt;${main.class}&lt;/mainClass&gt;
        &lt;type&gt;APP_IMAGE&lt;/type&gt;
        &lt;addModules&gt;
            &lt;addModule&gt;java.base&lt;/addModule&gt;
            &lt;addModule&gt;java.desktop&lt;/addModule&gt;
            &lt;addModule&gt;java.logging&lt;/addModule&gt;
        &lt;/addModules&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

At this point, you can move the app to your Mac's `Applications` folder. You can also customize it further with icons, etc. As for me, it's good enough for my purposes.

Conclusion {#h2-4-conclusion}
-----------------------------

In this post, we started with an UberJAR unable to rename files on remote volumes because of the current MacOS security model. To make it work, we wrapped the UberJAR in a native MacOS app. Then, we improved the situation with explicit modules by cutting the size in half.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/renamer-swing).

**To go further:**

* [Controlling app access to files in macOS](https://support.apple.com/guide/security/controlling-app-access-to-files-secddd1d86a6/web)
* [The Anatomy of a macOS App](https://eclecticlight.co/2025/12/04/the-anatomy-of-a-macos-app/)
* [jpackage](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/jar-to-macos-app/) on January 25^th^, 2025*
