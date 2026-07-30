---
title: "Embracing Java 17: Here’s What We Learned at Picnic | Foojay.io Today"
slug: "embracing-java-17-heres-what-we-learned-at-picnic"
date: "2022-05-30T08:10:20+00:00"
lastmod: "2022-05-31T13:45:14+00:00"
description: "Most Java 17 blog posts focus on shiny new features. We’d like to share what it takes to adopt Java 17 in a large tech team."
canonical: "https://blog.picnic.nl/embracing-java-17-heres-what-we-learned-69779d95fdf2"
authors:
  - "jakob-loehnertz"
image: "https://foojay.io/wp-content/uploads/2022/05/1_JQzYk7gPC63LmZnhBCMb8g.png"
categories:
  - "Use Cases"
tags:
related_posts:
frozen: false
---

Java 17? Count us in! At [Picnic](https://picnic.app/nl/), we're more than a grocery delivery company.{#eb88}

There's a lot of tech behind the scenes that enables us to provide the affordable, sustainable service our customers have come to expect --- and the back end of most of that tech is written in Java.{#eb88}

So when the new Java 17 release was announced, we were excited to get started.{#eb88}

Having moved to Java 11 within the quarter after its release, we stepped it up a gear this time and are proudly running Java 17 in production in the same quarter it was released --- we like to move fast!{#eb88}

Staying up to date with the latest technology helps keep our awesome tech colleagues engaged and motivated, but it also means we never run the risk of running business-critical applications on unsupported or unmaintained tech --- that's just not us.{#1e2b}

Incremental performance gains when moving to newer JDK versions are another welcome benefit, without having to change a single line of code.{#1e2b}

As we have a central platform team, we initiated the move to Java 17 immediately.{#6a33}

From there, we can roll it out to all product teams in a controlled manner.{#6a33}

Most Java 17 blog posts focus on the shiny new language features. That's all great, but we'd like to share what it takes to adopt Java 17 in a large tech team in the first place. Only then can you start thinking about adopting new language features. Here are the top 5 lessons we learned throughout the upgrade process.{#6a33}

Building on stable and well-maintained dependencies in the Java ecosystem is a must-have in order to ensure full Java 17 compatibility. Finding out that a dependency is lagging, or even unmaintained, can completely block a migration (unless you get your hands dirty yourself; more on that below).{#388f}

Examples of key low-level libraries and tools that we rely on are [Error Prone](https://errorprone.info/), [BlockHound](https://github.com/reactor/BlockHound), and [JaCoCo](https://www.eclemma.org/jacoco/). Fortunately, these libraries are well-maintained and supported Java 17 pretty early on. Another example is [Google Java Format](https://github.com/google/google-java-format), which we use to format our full codebase. By running the latest GJF version we ensure support for Java 17's new syntax.{#8cf0}

On the application development side, Spring [promises support](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) for Java LTS (Long-Term Support) versions and best-effort support for all other releases. Since our Java applications are all Spring- and Spring Boot-based, that gives us the flexibility to move to Java 17.{#714a}

It helps that, compared to companies of a similar size, Picnic has a very consolidated tech stack. This allows for a more simplified upgrade process overall. On the Java side, all of our applications are built on top of what we call the [Java Platform Support Modules](https://blog.picnic.nl/becoming-a-multiplier-on-our-java-developer-platform-17fe87de2e20) (PSM).{#ee07}

PSM provides both a unified build system and a Spring Boot-based runtime that services build upon. This means most of the heavy lifting for the migration can be done by a relatively small team, whilst the rest of the company reaps the benefits with very little effort.{#ee07}

Our approach to upgrading was to ensure that PSM, our shared build system, and support libraries, were JDK 17 runtime-compatible, whilst still targeting JDK 11. It's as easy as a, b, c:{#adb1}

**a)** We made the PSM foundation compatible with JDK 17 without requiring immediate adoption. This mainly entails upgrading all dependencies to be Java 17 compatible.{#7cbd}

**b)** Our product teams then adopted JDK 17 as a runtime JDK --- a relatively straightforward process due to the compatibility provided by the foundation, and of course, Java itself.{#1632}

**c)** Once the vast majority of downstream users have upgraded, we will update the foundation itself to require JDK 17, enabling it to reap the benefits itself, and start using Java 17 features in the implementation.{#aa2b}

It all sounds easy enough so far, but what if one of the dependencies or tools doesn't work with Java 17? One case where we ran into this was with the [New Relic Java agent](https://github.com/newrelic/newrelic-java-agent). Running without observability is not an option, so we did what every true developer would do: fork the agent's code and [make it run on Java 17 ourselves](https://github.com/PicnicSupermarket/newrelic-java-agent/compare/v7.3.0...v7.3.0-picnic-1).{#c44f}

This is not ideal, and we generally like to avoid this situation through lesson 1. But sometimes, you gotta do what you gotta do. (Since then, New Relic has released Java agent version [7.4.0](https://github.com/newrelic/newrelic-java-agent/releases/tag/v7.4.0), which officially supports JDK 17.){#c44f}

Another example is the Maven Dependency Plugin, which at the time of writing isn't fully Java 17 compatible without explicitly [overriding](https://issues.apache.org/jira/browse/MDEP-753?focusedCommentId=17413177#comment-17413177) its ASM dependency. Slightly inconvenient, but fortunately no forking is involved here.{#62d2}

As of Java 16, JDK internals are strongly encapsulated by default ([JEP 396](https://openjdk.java.net/jeps/396)). These and other changes mean that some dependencies now [require](https://github.com/reactor/BlockHound/issues/33) additional JVM flags such as `--add-opens` and `-XX:+AllowRedefinitionToAddDeleteMethods` to function properly. We updated our shared build system such that teams can configure these flags in a single place, ensuring that test and production runtimes remain in sync.{#9e0c}

As mentioned in the introduction, this is not the first time we're upgrading Java in this way. And because of Java's regular release cadence, we can already predict when we'll migrate to the next LTS version. This wasn't the case before Java 11 when things were less predictable.{#01ed}

Moving from Java 11 to Java 17 was a smoother transition than when we upgraded from Java 8 to Java 11. Many large changes between 8 and 11, including the introduction of the module system in Java 9 ([JEP 261](https://openjdk.java.net/jeps/261)), made that migration quite an undertaking. Now, features and changes are introduced in the JDK in a more gradual manner.{#36d5}

And, as with all things, practice makes perfect. By upgrading third-party dependencies early and often, we're ultimately making things easier for ourselves. These days the [Renovate](https://renovatebot.com/) bot helps us by automatically creating upgrade Pull Requests when new versions of dependencies are available. Where possible, upgrades are applied to PSM within days and rolled out to all other teams within weeks or less.{#9546}

Yes, our applications now run on JDK 17 in production. This means we'll automatically benefit from bug fixes, security improvements, and performance enhancements of the newer JVM. But that's only the beginning! We can now look forward to using the exciting new language features that were introduced since Java 11, like Switch Expressions and Text Blocks.{#b3fb}

Some features also spur discussion on established patterns we're using. For example, we're avid users of [Immutables](https://immutables.github.io/) to efficiently model immutable data classes. Java now features Records, a language-native way of defining such data classes. How we will adopt Records, and what the impact of our Immutables usage will be, is still an open discussion within the tech team. If you're using [Lombok](https://projectlombok.org/)'s `@Value`, you may face a similar question.{#d247}

So, all in all, we're super excited to get to know the new features over time and will be sure to keep you updated with our progress --- watch this space! {#1cb5}

<br />

*This post was co-written by [Sander Mak](https://medium.com/@sander.mak),* [*Nathan Kooij*](https://medium.com/u/e52ce2e1a997?source=post_page-----69779d95fdf2--------------------------------)*,* [*Stephan Schroevers*](https://medium.com/u/f691c947a256?source=post_page-----69779d95fdf2--------------------------------)*, and* [*Ryan Whitmore*](https://medium.com/u/c37ce8652748?source=post_page-----69779d95fdf2--------------------------------)*.*{#c03d}

👉 Come [work with us](https://picnic.app/careers/jobs/986403/technology--amp--engineering/amsterdam-north-holland-netherlands/java-developer) to shape the future of Java at Picnic!
