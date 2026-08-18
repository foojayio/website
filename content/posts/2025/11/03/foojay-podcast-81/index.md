---
title: "Foojay Podcast #81: Maven 4 - The Future of Java Build Automation"
slug: "foojay-podcast-81"
date: "2025-11-03T06:03:12+00:00"
lastmod: "2025-11-03T10:36:46+00:00"
description: "Maven 4 is approaching its release, bringing many improvements to the build tool powering millions of Java projects. In this Foojay Podcast episode, we - by Frank Delporte"
authors:
  - "frankdelporte"
  - "mthmulders"
image: "episode-81-maven-4.jpg"
categories:
  - "Gradle"
  - "Maven"
  - "Podcast"
tags:
related_posts:
  - "foojay-podcast-80"
  - "foojay-podcast-79"
  - "foojay-podcast-78"
  - "foojay-podcast-77"
frozen: false
---

**Maven 4 is approaching its release, bringing many improvements to the build tool powering millions of Java projects.**

In this Foojay Podcast episode, we talk about Apache Maven 4, a significant milestone that has been years in the making. Maven has been the backbone of Java dependency management and build automation since the early 2000s; however, the road to version 4 has been a long and deliberate one. With significant performance improvements, a modernized API for plugin developers, and changes that affect how we think about project structure, Maven 4 represents both an evolution and a revolution. What does this mean for the millions of developers who depend on Maven daily? How should teams prepare for the transition? And what's the story behind the Maven Central Repository changes that have been making headlines? To answer these questions and more, we're joined by a few of the many contributors who are actually building Maven 4 and stewarding its ecosystem.

## YouTube

{{< youtube 2qiXn9vN5iE >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Hervé Boutemy
  * <https://www.linkedin.com/in/hboutemy/>
* Guillaume Nodet
  * <https://www.linkedin.com/in/guillaumenodet/>
* Maarten Mulders
  * <https://www.linkedin.com/in/mthmulders/>

## Content

00:00 Introduction of the topic and guests

04:23 Status of Maven 4 release

* <https://maven.apache.org/whatsnewinmaven4.html>
* <https://maven.apache.org/guides/mini/guide-migration-to-mvn4.html>

07:57 Why we needed a new Maven version

* <https://maarten.mulders.it/2020/11/whats-new-in-maven-4/>
* <https://maarten.mulders.it/2021/03/introduction-to-maven-toolchains/>
* <https://www.javaadvent.com/2021/12/from-maven-3-to-maven-5.html>

12:37 You can already start using Maven 4

14:35 Some benefits of switching to Maven 4

18:52 Changes in the pom file, and yes, still XML

20:30 Changes for Maven plugin developers and integrators

22:24 Changes for Maven users, for instance, the need for Java 17

28:34 Maven The Tool versus Maven The Repository

34:51 Reasons for the change in authentication for uploads to Maven Central

36:01 The one and only Maven Central URL to use

* <https://central.sonatype.com/>

38:04 About the very first "server" hosting the Maven repository

40:32 The importance of setting up your own caching repository

* <https://www.sonatype.com/blog/maven-central-and-the-tragedy-of-the-commons>
* <https://openssf.org/blog/2025/09/23/open-infrastructure-is-not-free-a-joint-statement-on-sustainable-stewardship/>
* <https://www.youtube.com/watch?v=t74ClffSUW0>

44:04 The relationship between POM, BOM, BOM-POM , and SBOM

49:43 Gradle versus Maven

57:54 How to contribute to Maven or any other open-source project, and how to get the support of your company to do so

01:05:23 How to upgrade your projects from Maven 3 to 4

* <https://maven.apache.org/tools/mvnup.html>
