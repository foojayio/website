---
title: "A Week of Housekeeping: What Changed on Foojay.io"
date: "2026-07-06T10:04:00+00:00"
lastmod: "2026-07-06T11:58:10+00:00"
description: "Running a community website is a bit like maintaining a codebase: the content that matters most is never completely done. And if you don't schedule - by Dominika Tasarz"
authors:
  - "dominika-tasarz"
  - "frankdelporte"
image: "foojay_linkedin_banner.png"
categories:
  - "Foojay"
related_posts:
  - "disco-api-helping-you-to-find-any-openjdk-distribution"
  - "announcing-sustainability-for-java-developers-a-new-collaborative-guide-from-the-foojay-io-community"
  - "foojay-podcast-70"
  - "foojay-podcast-71"
frozen: false
---

Running a community website is a bit like maintaining a codebase: the content that matters most is never completely done. And if you don't schedule regular housekeeping, things can get outdated... Last week, we did some cleanup, restructuring, and updating. Here's an overview of what changed.

## Navigation Reorganised

The top menu needed some restructuring...

The **News** menu now focuses purely on what it says: [Daily News](https://foojay.io/today/), the [Foojay Podcast](https://foojay.io/today/category/podcast/), the [JC-AI Newsletter](https://foojay.io/today/category/jc-ai-newsletter/) by [Miro Wengner](https://foojay.io/today/author/miro-wengner/), the new [Foojay.io AI Portal](https://foojay.io/ai/), and the [Event Calendar](https://foojay.io/calendar). The AI Portal is a recent addition we hadn't properly surfaced before. It collects AI-related resources and tooling for Java developers, so it deserves its own slot in the navigation.

A new **Resources** menu brings together some of the already existing content that doesn't fit neatly into the news stream: the [Sustainability for Java Developers](https://foojay.io/sustainability-for-java-developers/) book, the [Java Almanac](https://foojay.io/java-almanac/), and the [Java User Groups](https://foojay.io/jugs/) directory. These are things you might want to bookmark and return to rather than read once. Grouping them separately from the news feed makes sense.

**Java Basics** now cleanly covers the [Quick Start guide](https://foojay.io/java-quick-start/) and [Java Terms Explained](https://foojay.io/pedia/) (the Pedia). Both are intended for people new to Java or new to the OpenJDK ecosystem, so putting them together is the right call. The Pedia has been expanded with a lot of additional info and some of the existing items got a rewrite to bring them to "the state of things in 2026".

## The Authors: Foojay's Most Important "Asset"

Foojay only exists because of the authors! Hundreds of Java developers, advocates, and engineers have contributed articles over the years, sharing knowledge, tutorials, opinions, and project updates with the community. Without them, there is no Foojay.

To give that contribution more visibility, we're introducing **Featured Authors** : every few weeks, two authors are selected at random and highlighted on the [Authors page](https://foojay.io/today/author/). No algorithm, no popularity contest, but just a regular spotlight on the people who keep the content flowing, whether they've written two articles or two hundred.

If you've contributed to Foojay and your name appears there one week: thank you. You earned it. Just like every other Foojay author!

## Meet The Team: Finally Up to Date

The "Meet The Team" page was one of the most embarrassingly outdated pages on the site — it referenced roles and people that had changed significantly. We've replaced it with a proper current overview.

The [updated team page](https://foojay.io/meet-the-team/) now accurately describes the three people actually running Foojay day to day: **Geertjan Wielenga** , who founded Foojay and remains the driving force behind it; **Frank Delporte** , who handles reviewing, editing, contributing, and hosting the podcast; and **Dominika Tasarz-Sochacka**, who joined the team most recently and looks after community, editing, platform maintenance, and sponsorships.

If you've ever wondered who's actually behind the site, or you want to reach out about contributing, sponsoring, or collaborating, that page is now the right place to start.

## Java Almanac Gets a Proper Introduction

The [Java Almanac](https://foojay.io/java-almanac/) has been linked from Foojay for years, but it never had a proper landing page that explained what it is and why it's worth bookmarking. Fixed.

For anyone who hasn't stumbled across it yet: the Java Version Almanac at [javaalmanac.io](https://javaalmanac.io) is maintained by Marc R. Hoffmann and a group of community contributors. It covers every Java version from pre-1.0 onward, with direct links to API docs, language specs, and JVM specs for each release, plus an API diff feature that lets you compare any two versions side by side. The new foojay.io/java-almanac page explains the project, its history, how it's maintained, and where to find the source. Quick links to the current LTS releases (Java 11, 17, 21, 25) are on the page.

## Java Quick Start: Updated to Java 25

The [Java Quick Start section](https://foojay.io/java-quick-start/) is where we send beginners and developers trying Java for the first time. The installer pages now point to **Java 25**, the current LTS.

The tutorial exercises themselves also got reviewed and show the difference in the code when using all new features that are available in Java 25. The videos still are over three years old and use Java 11, but they stay relevant as they go through the very basics of the Java programming language.

## Pedia: The Biggest Undertaking

The [Pedia (Java Terms Explained)](https://foojay.io/pedia/) got the most attention this week, and honestly it needed it. The Pedia no whas over 40 entries spanning everything from AOT Compilation to the TCK. Several of the older entries had accumulated significant issues: dead links caused by the OpenJDK domain migration from `openjdk.java.net` to `openjdk.org`, outdated content (Windows 7 era details, pre-2020 licensing language, stale test counts), and missing context on features that have evolved considerably since the entries were first written.

The new entries cover terms that were either missing entirely or referenced from other articles without their own page: [**Virtual Threads**](https://foojay.io/pedia/virtual-threads/), [**Text Blocks**](https://foojay.io/pedia/text-blocks/), [**GraalVM and Native Image**](https://foojay.io/pedia/graalvm-and-native-image/), [**Project Leyden**](https://foojay.io/pedia/project-leyden/), [**Switch Expressions**](https://foojay.io/pedia/switch-expressions/), [**Structured Concurrency**](https://foojay.io/pedia/structured-concurrency/), [**Scoped Values**](https://foojay.io/pedia/scoped-values/), [**Value Objects / Project Valhalla**](https://foojay.io/pedia/value-objects-project-valhalla/), [**Foreign Function \& Memory API**](https://foojay.io/pedia/foreign-function-memory-api/), [**JDK Mission Control**](https://foojay.io/pedia/jdk-mission-control-jmc/), [**JNI**](https://foojay.io/pedia/java-native-interface-jni/), [**Java Community Process**](https://foojay.io/pedia/java-community-process-jcp/), [**Preview and Incubator Features**](https://foojay.io/pedia/preview-and-incubator-features/), and [**OpenJDK Projects**](https://foojay.io/pedia/openjdk-projects/). Virtual Threads in particular was a notable gap given how important they quickly became in modern Java development.

## Foojay Brand and Logo Page

If you've ever landed on the site and wondered "Foojay!?", the answer is straightforward: FOO is the classic programming placeholder (as in foobar), and JAY is the J in Java and OpenJDK. Put them together and you get a name that says "this is a place for Java developers" without taking itself too seriously.

The full story, including how the site came to be, is on the [Who We Are page](https://foojay.io/who-we-are/). And if you need the logo for a conference slide, a blog post, or anything els, the official assets are now in our public GitHub repo, linked from that same page.

## Why We're Telling You This

Transparency is part of what we want Foojay to be. The site exists because the Java community keeps it alive with articles, podcast appearances, event submissions, and feedback. When we make changes, small or big, it's worth saying so, both so you know where things stand and so you can point out anything we missed.

If you spot something that's still broken, outdated, or confusing on the site, the easiest way to let us know is via the [Foojay Slack](https://foojay.io/today/join-slack-com-t-foojay-signup/) or by emailing [\[email protected\]](/cdn-cgi/l/email-protection#59313c353536193f3636333820773036). The Pedia in particular is designed to grow. If there's a Java or OpenJDK term you'd like to see explained, use the submission form on any Pedia entry and we'll add it to the list.
