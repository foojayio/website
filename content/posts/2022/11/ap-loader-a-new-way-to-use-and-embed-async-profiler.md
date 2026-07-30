---
title: "AP-Loader: A New Way to Use and Embed async-profiler"
slug: "ap-loader-a-new-way-to-use-and-embed-async-profiler"
date: "2022-11-22T14:54:35+00:00"
lastmod: "2022-11-30T09:52:56+00:00"
description: "Using async-profiler can be quite a hassle, especially when trying to use it in a library. The new ap-loader project tries to fix this!"
authors:
  - "johannes-bechberger"
image: "https://foojay.io/wp-content/uploads/2022/11/Untitled.png"
categories:
  - "Developer Tools"
  - "Performance"
  - "Tools"
tags:
related_posts:
frozen: false
---

Using [async-profiler](https://github.com/jvm-profiling-tools/async-profiler) can be quite a hassle.

First, you have to download the right archive from [GitHub](https://github.com/jvm-profiling-tools/async-profiler/releases/tag/v2.8.3 "GitHub") for your OS and architecture, then you have to unpack it and place it somewhere.

Or you get it from your OS distribution, hoping that it is the current version.

It gets worse if you want to embed it into your library, agent, or application. Library developers cannot just use a Maven dependency but have to create wrapper code and build scripts that deal with packaging the binaries themselves. Or, worse, they depend on a preinstalled version which they do not control.

I started the [AP-Loader](https://github.com/jvm-profiling-tools/ap-loader) project to fix all this!

{#more-61074}

* Want to run async-profiler? Just grab the latest loader JAR from [GitHub](https://github.com/jvm-profiling-tools/ap-loader/releases/latest/download/ap-loader-all.jar), and run `java -jar ap-loader-all.jar profiler` regardless of your OS or architecture
* Want to use the async-profiler as a Java Agent? You can use the loader JAR as javaagent and it behaves like the native async-profiler agent
* Want to use jattach? `java -jar ap-loader-all.jar jattach` is your friend
* Wondering what version of async-profiler you're using? `java -jar ap-loader-all.jar version` has you covered
* Want to use the converter to convert between formats? Just use `java -jar ap-loader-all.jar converter`
* Want to use async-profiler in your library? Just add a dependency to the `me.bechberger.ap-loader:-all-SNAPSHOT` from the [Sonatype OSS repository](https://s01.oss.sonatype.org/content/repositories/snapshots) and use `one.profiler.AsyncProfilerLoader.load()`
* Want to use the converter too? All the converter classes are included in the JAR, look no further than the `one.profiler.converter` package
* Just want all this for one platform only? I build and package versions for all platforms. There can be multiple platform versions on the classpath
* But what about the JAR size? It's just under 800KB, so no worries

This project uses the original binaries from async-profiler's GitHub releases page and tests the resulting project using the original tests from async-profiler, so you can expect it to behave as async-profiler does.

The idea for this project came up in a discussion with the creator of async-profiler, Andrei Pangin.

I use this project daily to profile my applications, so might you too?

I'm open to suggestions, bug reports and happy help to integrate ap-loader into your open-source library.

If I enticed you: Go over to [GitHub](https://github.com/jvm-profiling-tools/ap-loader) to get more detailed information on this project.

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. I built it to integrate async-profiler into more applications and libraries, like my upcoming profiler UI. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/).***
