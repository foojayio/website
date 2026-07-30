---
title: "JavaFX Links of March 2025"
slug: "javafx-links-of-march-2025"
date: "2025-03-28T11:12:21+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of March 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-03-28-javafx-links-of-march-2025/"
authors:
  - "frankdelporte"
image: "/images/posts/2025/03/javafx-links-of-march-2025/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-february-2025"
  - "javafx-links-of-january-2025"
  - "javafx-links-of-december-2024"
  - "javafx-links-of-november-2024"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of March 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

Core {#h2-0-core}
-----------------

* March 18th, OpenJDK and OpenJFX 24 were released! Check the [release notes of FX 24 here](https://github.com/openjdk/jfx/blob/jfx24/doc-files/release-notes-24.md): "*These release notes cover the standalone JavaFX 24 release. JavaFX 24 requires JDK 22 or later. JDK 24 is recommended.* " Downloads are available on the [Gluon website](https://gluonhq.com/products/javafx/), or you can install bundles which include JavFX with [SDKMAN!](https://sdkman.io/):

  * `$ sdk list java | grep 24.fx`
  * `24.fx-librca`
  * `24.fx-zulu`
* [JavaFX performance tip by **Johan Vos**](https://mastodon.social/@johanvos/114097968879188574): "_In a flow where data is changed, avoid using `MARKDOWN_HASH8f64619377b3d655089ba919b77f6a04MARKDOWN`*HASH* until as close as possible to code that does a UI update. The FX App Thread is single-threaded and also needs to render the SG. Do not use it for things that can run on other threads. ... This becomes increasingly important. 10 years ago, my PC had 4 cores. Hence, FX App Thread could use 25% of my resources. Today, I have 20 cores. Hence, only 5% of the CPU power is available to the FX AppThread. Use it wisely!"

* And a [call for help by **Johan**](https://mastodon.social/@johanvos/114143923702905662): "*One of the reasons I wrote the blog post "[Building OpenJFX using JDK](https://johanvos.wordpress.com/2025/02/27/building-openjfx-using-jdk/)" is to make it easier to create JavaFX SDKs for embedded systems. That is, Java SDKs including JavaFX. First class. Stay tuned. How I wish someone helped us with doing the business for this (JavaFX on embedded). We (Gluon) did this before, had many downloads, but almost no revenue. Spending lots of time to make it even better/faster and more maintainable now. But it takes lots of time and energy. I'm doing this because I believe it is the right thing to do. But sometimes I'm getting tired.*"

  * He [shared a screenshot about this approach](https://bsky.app/profile/johanvos.bsky.social/post/3lk7dbuum722a): "*A small, boring screenshot, but imho an important step. I cross compiled the latest openjdk/jdk with javafx base/graphics/controls mods on Linux x86-64 to Linux aarch64 and ran it on a Raspberry Pi.*"
  * And [a picture of a JavaFX app running on a Raspberry Pi](https://mastodon.social/@johanvos/114201127889794555): "*Progress with combined OpenJDK/OpenJFX repo (my [OpenJDK fork that contains all my changes](https://github.com/johanvos/jdk/tree/jfx-0311)). I did a cross-compiled build for Linux-aarch64.*"
* [**Ted M. Young** is asking for feedback](https://bsky.app/profile/ted.dev/post/3lkoaotxktk2x): "*JavaFX now has a Rich Text Area Control in incubation in 24 aimed at displaying formatted text. It supports a pluggable code syntax highlighter. Make sure to try it out and provide feedback!*"

* The [JVM Weekly newsletter by **Artur Skowroński**](https://www.linkedin.com/pulse/march-rest-story-jvm-weekly-vol-123-artur-skowro%C5%84ski-vktof/) also highlights some facts from the JavaFX 24 release notes: "*JavaFX 24 hits the scene with significant changes, including a requirement to use at least JDK 22 (targeting 24), a need to explicitly enable native access (--enable-native-access) in the context of JEP 472, the inclusion of the jdk.jsobject module (now part of JavaFX, replacing the one from the JDK), and support for pluggable image loaders via javax.imageio. The ability to run with the Security Manager (which itself was completely disabled in JDK 24) has also been removed. Among the new features on the list are, for instance, the incubating RichTextArea control, pluggable InputMap, support for @1x in image filenames, or the reducedMotion preference. These updates are complemented by a substantial set of fixes in areas such as UI, WebView, tools, and multimedia support. Additionally, ScrollPane now only responds to keyboard events when it actually has focus.*"

Applications {#h2-1-applications}
---------------------------------

* [**JabRef** is excited](https://foojay.social/@jabref/114082584649148423): "*Once again, we get the chance to be part of the outstanding Google Summer of Code program! We are looking forward to some high-quality projects that benefit our large user base. You are interested in Java, JavaFX, and opensource and want to work on a project with a large user base? Check out [our application guide](https://summerofcode.withgoogle.com/programs/2025/organizations/jabref-ev).*"
* [**Carl Dea** shared a video](https://www.linkedin.com/posts/carldea_java-jpro-javafx-ugcPost-7304272653765230592-XJ2q/): "*[Integrated Knowledge Management (IKM)](https://www.ikm.dev/). We created a cross platform installed application for MacOS, Windows, Linux. Now it can run as a Web App using [JPro.one](https://www.jpro.one/)'s technology. It uses Java 23 and JavaFX 23.*"
* [**Emad Hanif** shared a video](https://www.linkedin.com/posts/emad-hanif-00b4aa227_built-a-native-image-for-barcodify-keygenexe-activity-7307394485787750400-vXUJ): "_Built a native image for barcodify-keygen.exe (a JavaFX app) using GraalVM -- feels like an achievement. Compiled with `MARKDOWN_HASHe6c90c0bc395cbe9958a59c1080b8517MARKDOWN`*HASH*. Native images on desktop are great for such cases where we just need a standalone executable without an embedded JDK via an installer or requiring separate JDK installation on client's machine."
* **Gerrit Grunwald** released the [first version of ConfiCheck4J](https://github.com/HanSolo/conficheck4j/releases): "*A little tool to help you keeping track of Java conferences you either attend or speak at. You can filter for continents, conferences with open call for papers, conferences you attend and you speak at. Session proposals can be created and stored separately and also your speaker information can be stored incl. a picture. All of the speaker info can be copied to the clipboard so that you can easily paste it anywhere.*"
* [XPipe by **Christopher Schnick** gets noticed](https://www.linkedin.com/posts/crschnick_javafx-activity-7306661738228125696-d-mg/): "*A JavaFX desktop application is trending on GitHub? In 2025? Yes!*"
* [**Sean Phillips** shared screenshots](https://bsky.app/profile/seanmiphillips.bsky.social/post/3lkluwaxa7c2b): "*New Trinity XAI feature 'Hyperdrive' helps users import text and images, convert to embeddings, caption and label using local and remote hosted multimodal LLMs all in batch. Proof of fake life... bulk embedding vector and label selection for AI generated deep fakes of human faces.*"
* [**Alessandro Parisi** is working on FeedFX](https://github.com/palexdev/FeedFX): "*FeedFX is a desktop application built with Java and JavaFX that allows users to read and store RSS feeds from multiple sources. A key feature of FeedFX is its ability to categorize feeds using tags, making it easy to filter and organize content based on user preferences.*"
* [**Catherine Edelveis** is combining JavaFX with Spring Boot](https://bsky.app/profile/did:plc:tenw77gbf6i5ftndeidw6k64/post/3lkvkthb6xc2r): "*I'm such a weirdo 🫢 Every time I start learning something, I can't wait to share my knowledge! I've only been studying solfeggio for two months, and ALREADY I'm writing an app that builds scales and chords for the beginners to learn and practice 😅*"
* [**Jago de Vreede** released version 0.3.1 of SDKman-UI](https://bsky.app/profile/did:plc:qyi6hspkvd2mrtyut5cpw4xg/post/3ll5faaz5xk25): "*Small release with usability fixes. Now sorts version numbers correctly, and no longer bugs users about environment settings (setting in config). And some more little stuff, [grab it here](https://github.com/jagodevreede/sdkman-ui/releases/tag/v0.3.1).*"

Games {#h2-2-games}
-------------------

* A new release of [Randomizer-CS2](https://github.com/Metaphoriker/randomizer-cs2) by **Benjamin Sommerfeld** : "*A JavaFX application that allows you to create custom sequences of random actions to trigger them randomly in Counter-Strike 2. Make your friends in the game jump, shoot, reload, or drop their weapons at unfavorable moments -- all without injecting into the game itself.*"

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* [Great work by **Hidekazu Kubota**](https://x.com/sosuisen_net/status/1899918084401750466): "*I developed five VSCode extensions to assist Java beginners in creating applications with JavaFX. Additionally, I worked on several Maven archetypes. Drawing on my three years of teaching experience, I aimed to eliminate the common stumbling blocks my students faced.*"
* **Dumilde Paulo Fernando** is [working on Jamba UI](https://github.com/DumiJDev/jamba-ui): "*It's a modular Java-based UI framework built in my free time to empower developers with a lightweight, rapid, and enjoyable way to create desktop applications. Inspired by the simplicity of Spring Boot and the speed of Vaadin, this framework is designed to make UI development fun and hassle-free.*"

Podcasts, Videos, Books {#h2-4-podcasts-videos-books}
-----------------------------------------------------

* A new JavaFX In Action interview was published by **Frank Delporte** : "***Brian Schlining** has a dream job! He is responsible for a complex system at the Monterey Bay Aquarium Research Institute (MBARI) that allows scientific researchers to research animals in the deep sea. He provides them with the tools to annotate videos and images made by submarines diving thousands of meters deep in the oceans. While he works on this software, he gets to know all the amazing creatures living in this mysterious world.* " Check the [video on YouTube](https://www.youtube.com/watch?v=W9cs44DHIlA) or read more [in this blog post](https://webtechie.be/post/2025-03-20-jfxinaction-brian-schlining-annotating-deep-sea/).
* [Published on Foojay: Part 4 of the overview of "JavaFX In Action" interviews](https://foojay.io/today/video-series-javafx-in-action-part-4/) by **Frank Delporte** , with:
  * **Mike Hearn** (Conveyor)
  * **Sven Reimers** (JTaccuino)
  * **Chris Newland** (DemoFX, JitWatch,...).

Tutorials {#h2-5-tutorials}
---------------------------

* By [**Catherine Edelveis**](https://bsky.app/profile/cat-edelveis.bsky.social)
  * YouTube tutorial: [*Use Scene Builder to Create User Interfaces with Java FX*](https://www.youtube.com/watch?v=PKvuXsfWe_M).
  * Blog ["How to Create JavaFX Native Images"](https://bell-sw.com/blog/how-to-create-javafx-native-images/): "*Combining JavaFX-based applications with GraalVM Native Image will enable you to create platform-specific executables that don't require JVM to run. In this article, we will look into two ways of turning JavaFX applications into native images: manually and with the Maven plugin. We will also learn to integrate this process into the CI/CD pipeline with GitHub Actions.*"
* YouTube tutorials by [**Cameron McKenzie**](https://bsky.app/profile/cameronmckenzie.com):
  * [*Introduction to JavaFX tutorial for Beginners*](https://www.youtube.com/watch?v=YGciHV_Z65Y).
  * Create a number guessing game: [*Advanced JavaFX Tutorial for Java GUI Developers and Desktop Programmers*](https://www.youtube.com/watch?v=dJlHpcibo8c).
* [Video tutorial by **Natsoft**](https://www.youtube.com/watch?v=fGqvk7C1FRo): "*JavaFX \& Spring Boot 2024 Integration - Graphical User Interface (GUI) \| IntelliJ IDEA 2024*"

Miscellaneous {#h2-6-miscellaneous}
-----------------------------------

* [**Lost Arcadia** needs feedback](https://bsky.app/profile/lostarcadia.bsky.social/post/3ll6re4ymp22j): "*I wanna start doing some coding stuffs on stream. Toying with the idea of a small little minigame compilation like you would see on a late 90's website made in JavaFX. Just not sure what games I should add.*"

JFX Central {#h2-7-jfx-central}
-------------------------------

* New content:
  * Video: [JavaFX In Action with Brian Schlining: Annotating the Deep-Sea Wildlife](https://www.jfx-central.com/videos/W9cs44DHIlA)
* The overview of the JavaFX Links Of The Week of February got [published on Foojay.io](https://foojay.io/today/javafx-links-of-february-2025/).
