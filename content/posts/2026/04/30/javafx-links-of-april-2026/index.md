---
title: "JavaFX Links of April 2026"
date: "2026-04-30T09:02:34+00:00"
description: "Here are the JavaFX LinksOfTheMonth of April 2026. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there anything you want to - by Frank Delporte"
canonical: "https://webtechie.be/post/2026-04-24-javafx-links-of-april-2026/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-march-2026"
  - "javafx-links-of-february-2026"
  - "javafx-links-of-january-2026"
  - "javafx-links-of-december-2025"
frozen: false
---

Here are the JavaFX LinksOfTheMonth of April 2026. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

## Core

* **Frank Delporte** published a rework on Foojay of last week's Azul Blog post about the Oracle announcement related to JavaFX: [Oracle's Java Verified Portfolio and JavaFX: What It Actually Means](https://foojay.io/today/the-javafx-revival/).
* Blog post by Gluon: [April 2026 Critical Patch Update for OpenJFX Now Available](https://gluonhq.com/april-2026-critical-patch-update-for-openjfx-now-available/). With a [message shared by **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3mjzkzn6zzs2v): "*Enjoy the latest and safest releases of JavaFX! And if you want to help us building/distributing these releases, consider our [LTS support at gluonhq.com/lts](https://gluonhq.com/services/javafx-support/#pricing).*"
* Related to the three-monthly security update, a merge request has been created in the OpenJFX repo with the [release notes for 26.0.1](https://github.com/openjdk/jfx26u/pull/23/changes) with seven fixes and one known issue: "*Media Playback Does Not Work on Ubuntu 26.04. This is because JavaFX Media does not support libavcodec version 62. Support will be added with [JDK-8378510](https://bugs.openjdk.org/browse/JDK-8378510).*".
* Still on Java 8 with your JavaFX application and using Amazon Corretto? Apparently the [April 2026 release is the last one Amazon is providing with JavaFX 8 support](https://aws.amazon.com/about-aws/whats-new/2026/04/amazon-corretto-april-2026-quarterly-updates/).

## SceneBuilder

* On Reddit, **No-Security-7518** is asking for feedback: [SceneBuilder is a GREAT piece of software. What features could make it even greater?](https://www.reddit.com/r/JavaFX/comments/1sebnus/scenebuilder_is_a_great_piece_of_software_what/): "*I'm thinking maybe I could become a contributor. Anyway, I was thinking: What could make Scenebuilder even better?*"

## Applications

* [PDFsam announced](https://bsky.app/profile/pdfsam.org/post/3midyc6gytc2l): "*The new PDFsam Basic 6.0.0 is out with a lot of work done on the PDF engine and accessibility, upgraded JDK and JavaFX and more.* " Check [this post](https://blog.pdfsam.org/new-release/pdfsam-basic-6-0-0-is-out/2592/) for more details.
* **Frank Delporte** did the first release of [MelodyMatrix](https://melodymatrix.rocks/), live on camera, together with **Steve Hannah** , the creator of [jDeploy](https://www.jdeploy.com/). You can watch the [full video on YouTube](https://www.youtube.com/watch?v=_-IL7uHalIU) and find more information [in this blog post](https://webtechie.be/post/2026-04-16-first-release-of-melodymatrix-with-jdeploy/). MelodyMatrix is a desktop app to experience music in a new way with real-time MIDI recording, multiple visualization views, and powerful playback features. Perfect for musicians, educators, and music enthusiasts.
* [**Viktor Karpyuk** shared on LinkedIn](https://www.linkedin.com/posts/viktor-karpyuk_mongodb-opensource-developertools-ugcPost-7449866993488297985-sKxe/?utm_source=share&utm_medium=member_desktop&rcm=ACoAAAMtmUkBuuAVyJKMU3vtXsADMtuaTKIO8IA): "*It started with a small frustration: Studio 3T Community Edition only allowed 3 database connections. That limitation kept getting in the way of real day-to-day work, so instead of working around it, I decided to build something simpler, lighter, and more practical for everyday MongoDB usage. That is how Mongo Explorer was born --- a native MongoDB client focused on the things developers actually need: quick connections, easy browsing, solid querying, and a clean desktop experience without unnecessary overhead.* " It's [available on GitHub](https://github.com/viktor-karpyuk/mongo-explorer).

## Components, Libraries, Tools

* [**Dirk Lemmermann** shared amazing screenshots](https://bsky.app/profile/dlemmermann.bsky.social/post/3mign6rr7q22i): "*Last weekend I worked on FlexGanttFX (flexganttfx.com) improvements and support for AtlantaFX theming / styling. A new showcase app with new demos / samples is also in the works.*"
* [**Lee Wyatt** announced JavaFX Tools v2.0](https://bsky.app/profile/leewyatt.bsky.social/post/3mj4iduyq3s2y): "*Free IntelliJ plugin for JavaFX devs. CSS completion, variable resolution and gutter previews, 63,000+ Ikonli icons browser, weekly JFXCentral LOTW digest. Thanks to **Dirk Lemmermann** for testing and guidance!* " Check the [video on YouTube](https://www.youtube.com/watch?v=a03BkmqNefk). The plugin is [available on the JetBrains Marketplace](https://plugins.jetbrains.com/plugin/17514-javafx-tools).
* [**Hidekazu Kubota** announced](https://bsky.app/profile/sosuisen.bsky.social/post/3miqzesecvk2b) the first stable release of the [JavaFX Builder API](https://github.com/sosuisen/javafx-builder-api): "*This API allows UI code to represent nested structures that mirror the container hierarchy of the user interface. This project aims to reintroduce builder classes to JavaFX. Although these classes were included in JavaFX 2, they were removed from the official library due to concerns about maintenance overhead. Nonetheless, for those who prefer a fluent style, having an API like this is a valuable addition.*"
* Shared by [**Striking_Creme864** on Reddit](https://www.reddit.com/r/JavaFX/comments/1slll8c/running_javafx_apps_with_updates_and_dynamic/): "*Running JavaFX apps with updates and dynamic plugins. Today I want to share our project [Weaverbird](https://github.com/techsenger/weaverbird) and show how it can be used with JavaFX. Usually, a JavaFX application is started with all modules loaded int the boot layer. However, JPMS allows you to create an unlimited number of child layers and build a graph from them, which in turn lets us separate application management from the application itself. For exactly this purpose, Weaverbird was created - it runs in the boot layer and is responsible for creating and managing the layers (at the same time its capabilities go much further).*"
* [**Christopher Schnick** shared info about KickstartFX v1.1](https://www.reddit.com/r/JavaFX/comments/1sr105r/kickstartfx_v11_the_most_advanced_template_for/): "*A few months ago I released a ready-to-use application template called [KickstartFX](https://github.com/xpipe-io/kickstartfx). You can clone it and get started instantly or try out the pre-built releases on GitHub. The code and buildscripts are the same you find in a real-world production application as most of them are taken straight from one, in this case [XPipe](https://github.com/xpipe-io/xpipe). Since then, quite a few additions and bug fixes have been integrated.*"
* [**Frank Delporte** blogged about headless testing of Lottie4J](https://webtechie.be/post/2026-04-20-lottie4j-unit-test-with-headless-javafx/), a library for parsing Lottie animations as Java objects and playing them as JavaFX animations: "*Lottie4J had a unit test I marked 'can not run on CI, because it requires a display.' JavaFX 26 fixed that. There's a built-in headless platform now, one JVM flag, and GitHub Actions just work without any display setup. There's a small catch with Java version juggling (JavaFX 26 requires Java 24+, Lottie4J targets Java 21), but a Maven profile handles it cleanly.*"
* [**Dirk Lemmermann** published a FlexGanttFX Showcase Application](https://www.reddit.com/r/JavaFX/comments/1sti4zh/flexganttfx_showcase_application/) : "*created a jdeploy installer for the FlexGanttFX showcase application. You can [find it here](https://www.jdeploy.com/~flexganttfxshowcase). The installer will allow you to run the demo locally and the installation will auto-update whenever I push a new release. FlexGanttFX is a framework for building UIs for planning and scheduling applications. The showcase application contains a couple of demos and feature samples. If there is anything you would like to see being added to the demos then please let me know and I will try to come up with an example. I will soon add a JPro-based website that will allow you to run the same application in your browser.*"

## Podcasts, Videos, Books

* [**Catherine Edelveis** published a new video](https://www.youtube.com/watch?v=Ytdo8OGEYFI): "*New on CyberJAR: Comparing Top OpenJDK Distributions. If you're looking for more than vanilla Java - JavaFX, Java 6 an 7, hardened container images, extended LTS support - check out this comparative summary.*"
* [Live coding session with **Johannes Rabauer** and **Ryan Jarvinen**](https://www.youtube.com/watch?v=uvQwVpG3c5A): "*AI Coding with IBM Bob: Building a JavaFX Chess Game Live. In this live coding session we'll we explore IBM Bob, IBM's new AI-first development environment designed to act as a true software engineering partner rather than just an autocomplete tool. Bob integrates directly into the IDE and supports chat-driven development, real-time code review, and security-aware refactoring, while understanding your codebase and intent. It is purpose-built for tasks like Java modernization, large-scale refactoring, and enterprise-grade development workflows. The goal of this session is simple: Use a limited trial budget (40 Bobcoins) to build a functional JavaFX chess game and evaluate how far an AI IDE can realistically take us.*"
* [And also a finished chess game in 3D by **Olivier Pillods**](https://www.linkedin.com/posts/olivier-pillods-9286a0113_javajavafx-3d-chess-final-project-2023-ugcPost-7450104328989663232-MdE6): "*We were asked to replicate the classic chess game with JavaFX, and to add new pieces and rule variants. I was the only student that decided to make it 3D. Developed my own obj file importer. I created piece animations and colored interactions for movement availability. To finish, I made a graveyard system, and a rollback functionality that remembers all until start.*"
* **Frank Delporte** was "*struggling with the BentoFX layout in MelodyMatrix, and called **Matt Coley**, the person who wrote it. We used ScenicView to inspect the running JavaFX scene graph, cleaned up some AI-generated code, and possibly found a bug. Honest and practical session.* " Watch the [video on YouTube](https://www.youtube.com/watch?v=grwzIWWZMNw) and check the [blog post with more info and links](https://webtechie.be/post/2026-04-21-improving-melodymatrix-ui-with-bentofx/).
* Video by **Lee Wyatt** : [JavaFX Hot Reload in IntelliJ --- One Click, Zero Code Changes (FxmlKit + JavaFX Tools)](https://www.youtube.com/watch?v=ycj1X_TwfeU): "*JavaFX hot reload without touching your production code. FxmlKit 1.5.1 introduces a system property to enable dev mode externally. JavaFX Tools 2.1.1 takes it one step further --- just click the purple Runner button in IntelliJ IDEA, and your app starts with FXML/CSS hot reload enabled automatically. No need to call FxmlKit.enableDevelopmentMode() in your code. No risk of shipping dev mode to production. No extra configuration.*"

## Conferences, Presentations

* [Picture shared by **Wolfgang Weigend**](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3mjz5326mxk2p): "*A nice conversation about various topics such as Java dependencies, JUnit and also JavaFX at the oracle Java booth at the JCON 2026 conference in Cologne with **Christian Stein** and **Adam Bien**.*"

## Miscellaneous

* Article by **Daniel Harris** : [Interactive Floor Plan Editor in Java: Tools and Frameworks: A practical guide for developers building drag‑and‑drop architectural layout editors with Java and JavaFX](https://www.coohom.com/article/interactive-floor-plan-editor-in-java-tools-and-frameworks): "*Over the last decade, I've worked on several internal tools for architecture studios and real‑estate visualization teams. One recurring request is the same: build an interactive floor plan editor in Java that allows designers to quickly sketch layouts, drag walls, and experiment with room configurations. ... In this guide I'll walk through the frameworks, design patterns, and engineering decisions that make Java floor plan editors usable and scalable.*"

## JFX Central

* Content changes on JFX Central:
  * Fifty (!) new [libraries](https://www.jfx-central.com/libraries) and three [books](https://www.jfx-central.com/books) were added to the JFX Central website.
* The desktop client was updated to JavaFX 25 and now uses the new EXTENDED stage style, which allows for a more native feel of the application on Windows, Mac, Linux.
* **Dirk Lemmermann** is working on more improvements:
  * "*[Copilot is now updating the libraries catalogue for JFX Central for me](https://bsky.app/profile/dlemmermann.bsky.social/post/3mhzvnc76wc2u). 'Find the most popular JavaFX libraries on GitHub and summarise the readme and download the images and integrate into libraries.json'. That's what AI was made for 🙂*"
  * "*A little competition never hurt anyone ..... [JFX Central will start showing the GitHub stars that a library has received so far.](https://bsky.app/profile/dlemmermann.bsky.social/post/3mi2dduhvnk2o)*"
  * "*[Here is another one](https://bsky.app/profile/dlemmermann.bsky.social/post/3mi2dgp7xdk2o)* ... the header changes layout when a library has its own icon .... good job **Gerrit Grunwald** :-)_"
* The JavaFX Links Of The Week of March got bundled and [published on Foojay.io](https://foojay.io/today/javafx-links-of-march-2026/).
