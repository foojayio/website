---
title: "Details on OpenJDK vs. OpenJFX Release Cycles"
slug: "openjdk-vs-openjfx-release-cycles"
date: "2020-10-29T14:41:57+00:00"
lastmod: "2021-10-15T08:37:46+00:00"
description: "Confused about the release cycles of OpenJDK and OpenJFX and the relationship between them? Read on to have all your questions answered."
authors:
  - "frankdelporte"
image: "/images/posts/2020/10/openjdk-vs-openjfx-release-cycles/history_java_releases.png"
categories:
  - "JavaFX"
  - "Release Notes"
tags:
related_posts:
  - "beginning-javafx-with-intellij"
  - "a-javafx-app-on-zulufx-in-60-seconds"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
frozen: false
---

Confused about the release cycles of OpenJDK and OpenJFX and the relationship between them? Read on!

OpenJDK {#h2-0-openjdk}
-----------------------

Since 2018, Java switched to a 6-months release cycle. Every new release brings new finished features, but also "preview" ones which are not finished yet. These can be enabled [with a flag](https://mkyong.com/java/java-how-to-enable-the-preview-language-features/) (\``java --enable-preview`\`) to allow developers to start experimenting with them and provide feedback to the developers to further improve these so they can become available in one of the next releases.
![](/images/posts/2020/10/openjdk-vs-openjfx-release-cycles/history_java_releases.png) Java release history

Thanks to this fixed schedule, new versions are no longer causing major changes, but provide a steady, predictable, and stable flow of new features.

If you're in doubt if you should move to a newer JDK version, make sure to read "[Modern Java toys that boost productivity, from type inference to text blocks. Developers using older versions of the Java platform are missing out.](https://blogs.oracle.com/javamagazine/modern-java-toys-that-boost-productivity-from-type-inference-to-text-blocks)" by Angie Jones on the [Oracle Java Magazine](https://blogs.oracle.com/javamagazine).

OpenJFX {#h2-1-openjfx}
-----------------------

In the past, JavaFX has been bundled with the Oracle JDK (until JDK 11), but it was always a project on its own as [openjfx.io](https://openjfx.io/), with its [sources on GitHub](https://github.com/openjdk/jfx). [Gluon](https://gluonhq.com/)builds and distributes the OpenJFX releases, following the same 6-month release cycle.
![](/images/posts/2020/10/openjdk-vs-openjfx-release-cycles/history_javafx-1.png) JavaFX history

You can find them in the [Maven Central Repository](https://search.maven.org/search?q=org.openjfx) or on the [Gluon website](https://gluonhq.com/products/javafx/).
![](/images/posts/2020/10/openjdk-vs-openjfx-release-cycles/Screenshot-from-2020-10-29-20-29-12-1024x640.png) openjfx in the Maven Central Repository

An overview of all the changes in OpenJFX since version 11 are [available here.](https://github.com/openjdk/jfx/tree/jfx15/doc-files)

Relationship Between OpenJDK and OpenJFX Releases {#h2-2-relationship-between-openjdk-and-openjfx-releases}
-----------------------------------------------------------------------------------------------------------

Until now, the OpenJFX releases did not require a specific OpenJDK version. This means you can use OpenJDK 11 combined with OpenJFX 15.

At this moment, there are no planned features or changes in OpenJFX which require new JDK features (text blocks, records, etc), so the next releases of OpenJFX will most probably still be compatible with JDK 11.

Conclusions {#h2-3-conclusions}
-------------------------------

Yes, OpenJDK and OpenJFX are aligned in the number of releases per year (2x) and follow the same major version numbering (15, next one will be 16).

No, they are not "bound together" and follow their own path.

*** ** * ** ***

*The above timelines are screenshots of a small JavaFX application which is part of my book "[Getting Started with Java on the Raspberry Pi](https://webtechie.be/books/)". The [sources are available on GitHub](https://github.com/FDelporte/JavaOnRaspberryPi/tree/master/Chapter_04_Java/javafx-timeline).*
