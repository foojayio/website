---
title: "JavaFX Links of November 2025"
slug: "javafx-links-of-november-2025"
date: "2025-11-30T06:42:41+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of November 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-11-28-javafx-links-of-november-2025/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-october-2025"
  - "javafx-links-of-september-2025"
  - "javafx-links-of-august-2025"
  - "javafx-links-of-july-2025"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of November 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

Core {#h2-0-core}
-----------------

* [Article by **Paul Krill** on InfoWorld](https://www.infoworld.com/article/4082709/will-javafx-return-to-java.html): "Will JavaFX return to Java?" "*Just as a proposal to return JavaFX to the Java Development Kit has drawn interest in the OpenJDK community, Oracle too says it wants to make the Java-based rich client application more approachable within the JDK. ... An [October 29 post by **Bruce Haddon** on an OpenJDK discussion list](https://mail.openjdk.org/pipermail/discuss/2025-October/006553.html) argues that the reasons for the separation ... are much less applicable today.* "
  * **Frank Delporte** sees a perfect match with the Java on Mobile project and published a post about it: [Will OpenJFX Be Merged Into OpenJDK? It Would Be a Perfect Match with Java on Mobile!](https://webtechie.be/post/2025-11-05-openjfx-returning-to-openjdk/) "*Again, some exciting developments seem to be happening in the Java world! There's a growing discussion about bringing JavaFX back into the OpenJDK, and it couldn't come at a better time, especially with Johan Vos and the team at Gluon working hard on making Java a first-class citizen on mobile platforms. ... I'm excited to see where this goes. Java has always been about 'write once, run anywhere,' and these initiatives could finally deliver on that promise more completely than ever before.*"
  * This post got also [republished on Foojay.io](https://foojay.io/today/will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile/).
* [Request by **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3m5jiv22b7s2u): "*JavaFX developers using Windows: please help the development by doing some performance test for Direct3D 12.* " More info in this [mailinglist message by **Lukasz Kostyra**](https://mail.openjdk.org/pipermail/openjfx-dev/2025-November/057510.html).
* [Article by **Ben Evans** on InfoQ](https://www.infoq.com/news/2025/11/java-on-ios/): "Running Java on iOS: Gluon Introduces OpenJDK Mobile Resources and Automated Build Pipelines". "*The long-awaited Hotspot-on-iOS project is reporting major progress - OpenJDK is now able to build and run on iOS. This is the next milestone on a journey that started a long time ago - InfoQ first reported on it back in 2015. More recently, the mobile repository of OpenJDK (which is downstream of the main repo) has been able to build a static version of libjvm, allowing iOS binaries to execute Java code.*"
* [Message by **Johan Vos**](https://bsky.app/profile/did:plc:tysr26jaqf3moymuf7jc2uyr/post/3m5ymv2fcw22o) highlighting the fact JavaFX is a full open-source project: "*Keep in mind that the Gluon LTS releases of JavaFX 17u and JavaFX 21u are free to download and use. With Gluon, we do [offer an LTS service](https://gluonhq.com/services/javafx-support/#pricing) but the releases themselves are really free. We don't want to charge people for creating JavaFX applications. No reason to pay unless you need support.* "
  * And he is [looking for feedback about a core JavaFX feature](https://bsky.app/profile/johanvos.bsky.social/post/3m5yjlouems2o): "*The JavaFX Properties/listeners approach is really powerful to bind UI components to changing values. But it is often over-used and the main source of performance issues. It's very easy to kill performance by adding a listener to a property that is modified during layout.*" What do you think? Did you experience such performance issues?

Applications {#h2-1-applications}
---------------------------------

* [**Rafael Gutierrez** shared on Bluesky](https://bsky.app/profile/abaddongtz.bsky.social/post/3m4rdpoes5s2a): "A Pomodoro Timer built with JavaFX and Spring Shell using Hexagonal Architecture (any feedback is welcome.)_" You can find it [on GitHub](https://github.com/abadongutierrez/pomodoro-timer).
* **Gerrit Grunwald** updated JDKMon to Java(FX) 25. [Downloads are available on GitHub](https://github.com/HanSolo/SpaceFX/releases). JDKMon is a little tool written in JavaFX that tries to detect all JDKs installed on your machine.
* [**codedead** announced](https://bsky.app/profile/codedead.com/post/3m5oxetgrec2x) Opal v1.5.1. [Here are the release notes](https://codedead.com/blog/2025/11/15/opal-1.5.1/). "*Opal is a simple app that includes different sound groupings to suit anyone, from office to fantasy. All have volume controls so you can keep faint in the background or bring them forward. You can also set up a delay timer from the settings tab to remind you to take a break. You also have the option to combine multiple sounds at varying volumes to produce the perfect background noise.*"
* [**Cormac Redmond** shared an impressive screenshot](https://mastodon.social/@credmond/115617279097981595) showing a lot of different UI windows of KafkIO: "*Version 2.0.14 is released, packed with new features. [Download the fast, easy Apache Kafka™ GUI](https://kafkio.com/).*"

Games {#h2-2-games}
-------------------

* **Gerrit Grunwald** updated SpaceFX to Java(FX) 25. [Downloads are available on GitHub](https://github.com/HanSolo/SpaceFX/releases). SpaceFX is a space shooter written in JavaFX. It can run on Mac, Windows, Linux, on mobile devices, and even in the browser. A true "write once run anywhere" application that illustrates the true power and the promise made by Java and JavaFX.
* [Defold, the free game engine with a JavaFX UI engine, shared](https://bsky.app/profile/defold.com/post/3m6m2cbjfgk2f): "*[The Defold Editor Overview manual](https://defold.com/manuals/editor/) has been updated! Get familiar with the new Editor features and let us know if there is anything we could improve further.*"

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* [Message on Reddit JavaFX](https://www.reddit.com/r/JavaFX/comments/1p178xg/webfx_now_supports_teavm_bringing_webassembly_and/) by **Bruno Salmon** : "WebFX now supports TeaVM: bringing WebAssembly and Kotlin to JavaFX on the Web!" with a [link to a full blog post](https://blog.webfx.dev/2025/11/17/teavm/): "*We're excited to announce that WebFX now officially supports TeaVM, a Java to WebAssembly compiler that unlocks faster startup times and broader JVM language compatibility with now Kotlin and Scala! We already have multiple live demos running with TeaVM.*"

Podcasts, Videos, Books {#h2-4-podcasts-videos-books}
-----------------------------------------------------

* **Frank Delporte** interviewed **Johan Vos** and **Stephen Chin** at Devoxx Belgium 2025. The video is now available [on YouTube](https://www.youtube.com/watch?v=OrhGyTGJgOg). Johan is one of the lead OpenJFX developers and talks about the history of Java and JavaFX, and the future with the Java on Mobile project. Stephen is the author of "The Definitive Guide to Modern Java Clients with JavaFX."
* New ["JavaFX In Action" interview published by **Frank Delporte** with **Craig Raw** about the Sparrow Bitcoin Wallet](https://www.youtube.com/watch?v=Mc3fUTxoKIg): "*I don't have any bitcoin myself, but still find the idea of the blockchain and 'public shared money' fascinating. And as it turns out, there is a free and open-source bitcoin wallet, Sparrow, created with JavaFX, that wants to help people understand how the Bitcoin system works and make transactions easy to understand. And while Craig explains the app itself, we also learn a lot about the Bitcoin ecosystem, reproducible builds, security, hardware wallets, and more!*"
* [Foojay Podcast #83 with **Johan Vos** and **Stephen Chin**](https://foojay.io/today/foojay-podcast-83/): OpenJDK Evolutions plus Tips and Tricks:
  * Johan takes us on a journey through Java's history, from porting Java to Linux in 1995 to his current work on bringing Java and JavaFX to mobile and embedded devices through the Java On Mobile project.
  * Stephen is the author of "The Definitive Guide to Modern Java Clients with JavaFX," who shares insights on building cross-platform client applications.

Tutorials {#h2-5-tutorials}
---------------------------

* **Troels Mortensen** is working on a series of YouTube tutorials:
  * [Your first app](https://www.youtube.com/watch?v=LsxLAjTXROw)
  * [Your first button](https://www.youtube.com/watch?v=_jEooKZFWkI)
  * [Your first TextField](https://www.youtube.com/watch?v=pv4pZx7ewww)
  * [Simple app structure](https://www.youtube.com/watch?v=tjq0XfaY6Zg)
  * [Introducing the SceneBuilder](https://www.youtube.com/watch?v=j02aTD7AMkA)
  * [Designing in the SceneBuilder](https://www.youtube.com/watch?v=iTMEyaQJxjo)
  * [Opening the SceneBuilder through IntelliJ](https://www.youtube.com/watch?v=JkONOwnDTDs)
  * [Your first controller](https://www.youtube.com/watch?v=8OO5-p2mBjs)
  * [Introducing the View Manager](https://www.youtube.com/watch?v=peZT8Fv6MzE&t=1s)
  * [Passing data between views](https://www.youtube.com/watch?v=jSXD3-rg8P0)
  * [ViewManager v3](https://www.youtube.com/watch?v=ylkQyqZxiIg)
  * [Putting fxml files into resource directory](https://www.youtube.com/watch?v=KVD12NS8VQg)
  * [Single view application](https://www.youtube.com/watch?v=8eIY_xsRm2A)
  * [The Controller Configurator](https://www.youtube.com/watch?v=AUNqzm4AJxo)
* [By **Sour coders**](https://www.youtube.com/watch?v=8gvLIt5zSxg): JavaFX install and setup in IntelliJ (Local JavaFX template) with Scene Builder setup.
* [By **Programming of Life**](https://www.youtube.com/watch?v=rLSiLMbpdUY): Mini JavaFX Particles Animation Tutorial \| Beautiful FX Effect in Under 100 Lines

Miscellaneous {#h2-6-miscellaneous}
-----------------------------------

* [**Gerrit Grunwald** has fans! 😉](https://bsky.app/profile/hansolo.eu/post/3m5fzwwhuoc2l): "*Yesterday at the event of the Amsterdam JUG, someone from the audience came to me after my session and thanked me for creating the JavaFX libraries I did create... It always makes me really happy to see people use the things that I just created for the fun of it. Love the Java Community.*"
* Interesting read: [Solving the Java 24/JavaFX 24 Compatibility Issue: Unsafe Access Flag](https://iifx.dev/en/articles/457273426/solving-the-java-24-javafx-24-compatibility-issue-unsafe-access-flag). "*The warnings you're seeing, especially those related to sun.misc.Unsafe and WARNING: package sun.misc not in java.base, are a direct result of JEP 471 and JEP 498 in the newer Java versions (starting around JDK 23/24). ... Future JavaFX Versions (JavaFX 25 and beyond) are expected to have this internal usage removed or replaced with modern alternatives (like the Foreign Function \& Memory API introduced in Java). Until then, using the --sun-misc-unsafe-memory-access=allow flag is the correct way to handle this transition period. You should keep an eye on the JavaFX release notes for updates on when this internal dependency is completely phased out!*"
* [**Mark J. Koch** needs a JavaFX-break](https://bsky.app/profile/markjkoch.bsky.social/post/3m5yyiqgmxk2y): "*My end of the year goal is to make this pile of circuits into a working analog synth. Then, and only then, am I allowed to go back to gamedev on my JavaFX Neuromancer PC remake while sipping egg nog. Too many hobbies.*"
* [Screenshot by **jaavaaguru** on Reddit](https://www.reddit.com/r/JavaFX/comments/1p6ek5p/new_color_picker_idea/): "*I'm working on a theme designer app, and came up with this color picker idea this morning and implemented it from scratch. Any thoughts for improvements?*"
* [**Frank Delporte** introduced the new hashtag #JavaOnSingleBoardComputers](https://foojay.social/@frankdelporte/115612004093020148). "*Thanks to DFRobot, I took my first step toward testing other single-board computers with Java, JavaFX, and Pi4J. The LattePanda IOTA looks like a great board and calls for more experiments after a quick first test...* " [Video and blog are available here](https://webtechie.be/post/2025-11-25-first-test-lattepanda-iota-with-ubuntu-and-java/) and show a JavaFX JBang application running smoothly on the IOTA.

JFX Central {#h2-7-jfx-central}
-------------------------------

* New content on JFX Central:
  * Tools: [SDKMAN GUI](https://www.jfx-central.com/tools/sdkman-gui), a modern graphical management tool for SDKMAN, providing an Applite-like user experience.
  * Showcase: [Sparrow Bitcoin Wallet](https://www.jfx-central.com/real_world/sparrow)
  * People: [Craig Raw](https://www.jfx-central.com/people/c.raw)
  * Video: [JavaFX In Action with Craig Raw about the Sparrow Bitcoin Wallet](https://www.jfx-central.com/videos/Mc3fUTxoKIg)
* The links of October got [published on Foojay](https://foojay.io/today/javafx-links-of-october-2025/).
