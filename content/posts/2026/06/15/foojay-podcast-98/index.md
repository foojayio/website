---
title: "Foojay Podcast #98: The End of JNI Pain: How WebAssembly Is Quietly Replacing Native Libraries in Java"
date: "2026-06-15T08:13:00+00:00"
lastmod: "2026-06-15T15:25:19+00:00"
description: "WebAssembly is already running inside Java applications, most developers just don't know it yet. In this episode, Andrea Peruffo walks us through how WebAssembly is becoming the modern, safe alternative to JNI: letting you run Rust, C, and other native libraries directly on the JVM, without the crash risks, the per-platform distribution headaches, or the observability blackhole that JNI creates. From JRuby's Prism parser to SQLite and Postgres running as pure Java bytecode, the use cases are real and the project Endive, under the Bytecode Alliance, is ready to explore."
authors:
  - "andrea-peruffo"
  - "frankdelporte"
image: "edit-98-webassembly.jpg"
categories:
  - "Podcast"
related_posts:
  - "a-new-generation-of-java-libraries-is-born-wasm-becomes-the-implementation-detail"
  - "foojay-podcast-97"
  - "foojay-podcast-96"
  - "foojay-podcast-95"
frozen: false
---

WebAssembly is already running inside Java applications, most developers just don't know it yet. In this episode, Andrea Peruffo walks us through how WebAssembly is becoming the modern, safe alternative to JNI: letting you run Rust, C, and other native libraries directly on the JVM, without the crash risks, the per-platform distribution headaches, or the observability blackhole that JNI creates. From JRuby's Prism parser to SQLite and Postgres running as pure Java bytecode, the use cases are real and the project Endive, under the Bytecode Alliance, is ready to explore.

## YouTube

{{< youtube BCbJwLphhsU >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guest

**Andrea Peruffo**   

WebAssembly engineer, creator of Chicory, maintainer of Endive under the Bytecode Alliance

* [GitHub](https://github.com/andreaTP/)
* [LinkedIn](https://www.linkedin.com/in/andrea-peruffo-32269178/)
* [Bluesky](https://bsky.app/profile/andreatp.bsky.social)

## Links

* Foojay post: [A New Generation of Java Libraries: Wasm Becomes the Implementation Detail](https://foojay.io/today/a-new-generation-of-java-libraries-is-born-wasm-becomes-the-implementation-detail/)
* [Chicory — WebAssembly interpreter for the JVM](https://github.com/dylibso/chicory)
* [Endive — WebAssembly runtime for Java, under Bytecode Alliance](https://github.com/bytecodealliance/endive)
* [Endive documentation](https://endive.run/docs/)
* [Bytecode Alliance](https://bytecodealliance.org)
* [OpenJDK Project Detroit](https://openjdk.org/projects/detroit/)

## Content

* 00:00 Introduction of topic and guests
* 00:56 What is WebAssembly?
* 03:35 Comparing the performance with JavaScript
* 05:45 JRuby already uses WebAssembly
* 09:04 JNI versus FFM API versus WebAssembly
* 13:58 Other Java-related tools that use WebAssembly
* 17:56 History of the Chicory and Endive projects to bring WebAssembly to Java
* 21:03 Projects of the Bytecode Alliance
* 22:02 The Endive project as the glue to bring WebAssembly tools to Java
* 23:30 Integration of the Redline compiler
* 28:59 Why this is the perfect solution to modernize existing Java applications
* 31:18 Is this approach performant?
* 32:24 What future changes in Java and the JVM will make this even better
* 35:04 How Endive can be used in AI development
* 37:28 What to expect in Endive
* 41:29 Conclusions
