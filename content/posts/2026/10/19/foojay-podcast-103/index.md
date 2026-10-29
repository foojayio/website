---
title: "Foojay Podcast #103: Killing Reflection at Build Time with Dmitry Moskalyuk and Shai Almog"
date: "2026-10-19"
description: "A backend data layer and a cross-platform UI framework both moved reflection to build time. Dmitry Moskalyuk and Shai Almog explain why, and what it costs."
authors:
  - "dmitry-moskalyuk"
  - "frankdelporte"
  - "shai-almog"
image: "episode-103-reflection.jpg"
categories:
  - "Java"
  - "Kotlin"
  - "Performance"
  - "Podcast"
  - "Codename One"
related_posts:
  - "reflection-free-serialization-on-the-jvm-four-formats-in-avro-out"
  - "openapi-orm-svg-and-lottie"
  - "why-the-fastest-way-of-serializing-a-java-field-is-not-serializing-it-at-all"
  - "foojay-podcast-102"
frozen: false
---

Runtime reflection is one of those quiet taxes on the JVM: it is flexible, it is everywhere, and it is easy to stop noticing it until you go looking for where your CPU is actually going. In this episode, two teams from completely different corners of the ecosystem explain how they went looking and ended up at the same answer: push the work to build time.

Dmitry Moskalyuk is a new author on Foojay. His article "[Reflection-Free Serialization on the JVM: Four Formats In, Avro Out](/today/reflection-free-serialization-on-the-jvm-four-formats-in-avro-out/)" describes a model layer that ingests JSON, XML, YAML and TOML and converts everything to Avro, and the choice between Jackson and kotlinx.serialization that came down to where the reflection cost hides, and where it does not. Shai Almog has been [writing on Foojay since 2021](/today/author/shai-almog/) and works on a build-time code generation pipeline for Codename One, with the goal of "no reflection, no service loader, no Class.forName".

We compare a backend serialization layer with a UI framework's codegen pipeline, talk about whether generated code (Lombok, Manifold, jOOQ) is a good idea, look at what Records and Project Leyden change, and end with the question that matters most: should you still reach for reflection in a new project?

## YouTube

{{< youtube smHzWWNOyMU >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Dmitry Moskalyuk - Senior Backend Engineer, building high-load distributed systems on the JVM
  * [LinkedIn](https://www.linkedin.com/in/dmitry-moskalyuk/)
  * [Foojay author page](https://foojay.io/today/author/dmitry-moskalyuk/)
* Shai Almog - Author, DevRel and open source developer working on Codename One
  * [LinkedIn](https://www.linkedin.com/in/shai-almog-81a42/)
  * [Foojay author page](https://foojay.io/today/author/shai-almog/)

## Links

* [Reflection-Free Serialization on the JVM: Four Formats In, Avro Out (Dmitry's article on Foojay)](https://foojay.io/today/reflection-free-serialization-on-the-jvm-four-formats-in-avro-out)
* [OpenAPI, ORM, SVG and Lottie (Shai's article on Foojay)](https://foojay.io/today/openapi-orm-svg-and-lottie/)
* [Jackson Blackbird on GitHub](https://github.com/stevenschlansker/jackson-blackbird)
* [Apache Avro](https://avro.apache.org/)
* [Project Lombok](https://projectlombok.org)
* [Manifold](http://manifold.systems/)
* [jOOQ](https://www.jooq.org/)
* [Project Leyden](https://openjdk.org/projects/leyden/)

## Content

* 00:00 Introduction of topic and guests
* 01:50 About the new Foojay, contributing with GitHub pull requests
* 03:17 What is Apache Avro
* 04:26 What is Codename One
* 06:31 What is Blackbird in Jackson and compared to kotlinx mechanisms for different content parsers
* 10:01 Why Codename One focuses on "no reflection, no service loader, no Class.forName"
* 14:23 Is automatically generated code bad (Lombok, Manifold, JOOQ)?
* 17:47 Java evolutions that bring improvements into the system (Records, Project Leyden)
* 21:31 Is reflection changing through Java versions?
* 24:29 Build time cost versus runtime, Project Leyden
* 28:41 Use of reflection in UI versus backend
* 32:16 Benefit of your data model in a (Java) object, having one source of truth, good debugging
* 35:35 Should you worry about serialization in a small project
* 37:38 Serialization and security
* 38:57 Should I still use reflection/serialization in a new project?
* 43:22 Conclusion
