---
title: "Oracle's Java Verified Portfolio and JavaFX: What It Actually Means"
slug: "the-javafx-revival"
date: "2026-04-02T07:01:48+00:00"
description: "Oracle's JavaOne 2026 brought an announcement that caught some attention in the Java community: the Java Verified Portfolio (JVP), a new program that - by Frank Delporte"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/03/javafx_logo.jpg"
categories:
  - "JavaFX"
tags:
related_posts:
  - "beginning-javafx-with-intellij"
  - "book-review-learn-javafx-game-and-app-development-with-fxgl-17"
  - "foojay-podcast-9"
  - "foojay-podcast-25"
frozen: false
---

Oracle's JavaOne 2026 brought an announcement that caught some attention in the Java community: the Java Verified Portfolio (JVP), a new program that bundles JDK-related tools, frameworks, libraries, and services under a single commercially supported umbrella with clear roadmaps and support timelines. One of the headline items? Commercial support for JavaFX in JDK 17, 21, 25, 26, and future LTS releases.

Oracle's own framing: *"Driven by customer demand, interest from academia, and the growing need for advanced visualizations in AI-powered applications and analytics, Oracle is reintroducing commercial support for JavaFX."*

That is good news. But before we celebrate Oracle's renewed commitment, let's be honest about what actually happened between JavaFX being dropped from the JDK in 2017 and this announcement.

Who Kept JavaFX Alive? {#h2-0-who-kept-javafx-alive}
----------------------------------------------------

When Oracle removed JavaFX from the JDK starting with JDK 9, a small number of organizations and individuals stepped up to make sure it did not quietly disappear. The most important of these is **Gluon**.

The [sources for OpenJFX are hosted on GitHub](https://github.com/openjdk/jfx) within the OpenJDK project. Kevin Rushfort from Oracle and Johan Vos from Gluon are the main maintainers of this project. Under their guidance, essential work has been done over the last few years: keeping the rendering pipeline modern, maintaining platform support across Linux, macOS, and Windows, integrating with newer JDK versions, and ensuring that the framework continued to evolve rather than stagnate. This is not marketing work. It is the kind of deep platform engineering that keeps a technology ecosystem alive when the original sponsor walks away.

Without Gluon's sustained investment, the question of whether JavaFX had a future would have been answered a long time ago. But not in a good way...

Beyond Gluon, the broader OpenJFX community contributed bug fixes, improvements, and tooling. And several JDK vendors, including [Azul](https://www.azul.com/downloads/#zulu){#https://www.azul.com/downloads/#zulu}, continued distributing JDK builds that included JavaFX, which helped ensure that the organizations depending on it did not face a cliff edge when Oracle stopped bundling it.

OpenJFX Inspired Project Skara {#h2-1-openjfx-inspired-project-skara}
---------------------------------------------------------------------

A nice side-story about OpenJFX: it was the first OpenJDK repository to move to GitHub! The idea was to get the community more involved in the development. And it inspired Oracle to bring the full OpenJDK project to GitHub with [Project Skara](https://openjdk.org/projects/skara/). Some numbers from [github.com/openjdk](https://github.com/openjdk/):

* 135 repositories
* JDK: 905 contributors and almost 23k stars
* JFX: 100 contributors and 3.2k stars.

What the JVP Actually Changes {#h2-2-what-the-jvp-actually-changes}
-------------------------------------------------------------------

The JVP announcement matters because it removes a persistent objection that came up whenever anyone evaluated JavaFX for a new project: *Who is commercially accountable for this in the long run?*

The OpenJFX community had an answer to that with Gluon, but for many companies, this wasn't enough. Enterprise procurement teams like having a "big" name, like Oracle, commit to a technology. Now they have that. Oracle's involvement provides a formal support timeline, making it easier to justify JavaFX in procurement conversations and architecture reviews.

For teams already running JavaFX in production, not much changes immediately. Your applications keep running. The OpenJFX project continues to develop. The Oracle JVP adds a commercial support layer on top of something that was already solid. But these kinds of support contracts were already available for a long time, for instance, with the [Azul Core Support Subscriptions](https://www.azul.com/products/core/).

JavaFX and the AI Visualization Angle {#h2-3-javafx-and-the-ai-visualization-angle}
-----------------------------------------------------------------------------------

One of the reasons Oracle is highlighting JavaFX right now is the growing need for high-performance data visualization, particularly in AI-powered applications. This is not just marketing spin as there are genuinely impressive things being built.

A good example is the work Florian Enner has done with HebiCharts, a 2D and 3D plotting library built on JavaFX and ChartFX, accessible from Python, C++, and MATLAB. In a [recent video](https://www.youtube.com/live/B5GT9XAcqB8), he shows how the combination of Java's ability to handle millions of data points and JavaFX's rendering capabilities produces highly interactive, fast user interfaces that would be difficult to achieve with browser-based alternatives.

{{< youtube B5GT9XAcqB8 >}}

This is the kind of use case that makes JavaFX genuinely interesting in 2026, not just as a migration target for legacy Swing applications, but as a real option for new work in scientific computing and data-heavy tooling.

A Note on the Community and Commercial Dynamics {#h2-4-a-note-on-the-community-and-commercial-dynamics}
-------------------------------------------------------------------------------------------------------

Johan Vos raised a fair point when the JVP was announced: there is a pattern to the way large companies talk about community-driven technology. Oracle used JavaOne, historically a community event, to make a commercial announcement. The line between community stewardship and commercial positioning gets blurry, and it is worth naming that. Johan is, again, inviting all distributors to become more active in the development of JavaFX.

Where Things Stand {#h2-5-where-things-stand}
---------------------------------------------

JavaFX is in a better position today than it has been at any point since 2017. The technology has continued to improve, the community stayed active (make sure to check [JFX Central](https://www.jfx-central.com/)!), and now Oracle has re-entered with a formal commercial commitment. That is a combination worth being optimistic about.

If you are using JavaFX in production, this is a good moment to revisit your support and upgrade plans, especially if you are still on JDK 8. If you are evaluating JavaFX for something new, the long-term risk question is substantially easier to answer than it was a year ago.

And if you want to understand what keeps JavaFX moving forward technically, spend some time with the [OpenJFX project](https://openjfx.io). That is where the real story is.

<br />
