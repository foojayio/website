---
title: "JavaFX Links Of October"
slug: "javafx-links-of-october"
date: "2022-10-31T16:15:24+00:00"
lastmod: "2022-10-31T16:15:26+00:00"
description: "When I (re)started the JavaFX Links Of The Week in September, I was wondering if there would be enough material to share every week..."
authors:
  - "frankdelporte"
image: "Favicon-3-2.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-june-2026"
  - "javafx-links-of-may-2026"
  - "javafx-links-of-april-2026"
  - "javafx-links-of-february-2026"
frozen: false
---

When I (re)started the JavaFX Links Of The Week on [jfx-central.com](https://www.jfx-central.com/home) in September, I was wondering if there would be enough material to share every week.

But that was a stupid mistake as you can see below in the summary of what happened in October... 🙂

JavaFX 19 and 20 {#h2-0-javafx-19-and-20}
-----------------------------------------

* JavaFX 19 just got released a few weeks ago, but [**Johan Vos**](https://twitter.com/johanvos) is already [looking forward](https://twitter.com/johanvos/status/1575159889994911744?t=PJn2au0k_icq2qseeLVOXA&amp;s=09) to the next one: *a really-worth-mentioning improvement that will be in JavaFX 20 is the update to MarlinFX 0.9.4.6 by [**Laurent Bourgès**](https://twitter.com/laurent_bourges). Thank you very much Laurent for your contributions. They are an important part to the success of JavaFX. See [JDK-8287604](https://bugs.openjdk.org/browse/JDK-8287604).*
  * [Laurent is even sharing his TODO list on GitHub](https://github.com/users/bourgesl/projects/1/views/1) in case you are curious about what he is working on...
* Want to test JavaFX 20 Early Access? It's already [available on the **Gluon** website](https://gluonhq.com/products/javafx/#ea)!
* Up till now, each of the newer JavaFX versions could run with a lower JDK, e.g. JavaFX 17 works with JDK 11. But this will change as [was mentioned on the mailinglist](https://mail.openjdk.org/pipermail/openjfx-dev/2022-October/036089.html): "JavaFX 20 requires JDK 17 or later."
* [**Dirk Lemmermann**](https://twitter.com/dlemmermann) shared [a screenshot in a tweet](https://twitter.com/dlemmermann/status/1582021109423042562) of the [pull requests in the openjdk/jfx project](https://github.com/openjdk/jfx/pulls?q=is%3Apr+is%3Aopen+label%3Arfr): "Wow, 47 pull requests "ready for review" in #OpenJFX. Looks like we have a traffic jam. Some several years old. Some ending with last comment "can you review this?" 🙂 Hope Oracle really does increase their #javafx team member count."
* At JavaOne on October 20th, some JavaFX related announcements were made.
  * [A tweet](https://twitter.com/JavaFXpert/status/1583174188587589632) by [**James Weaver**](https://twitter.com/JavaFXpert): "Great #JavaFX announcements from @kevinrushforth after being introduced by @mono_quito89 at #JavaOne, while bandmate and @Java legend @BrianVerm mixes drinks. Friend and colleague @johanvos at @GluonHQ highlighted in the process."
  * And a [LinkedIn message](https://www.linkedin.com/posts/btratra_java-javafx-openjdk-activity-6988940880757882880-s4RA?utm_source=share&amp;utm_medium=member_desktop) by [**Bernard Traversat**](https://twitter.com/BTraTra): "We announced today we will be producing JavaFX build for JDK 20! Providing a modern UI toolkit for the Java platform will continue to make Java the most compelling platform for educators to teach programming and for UI innovators to explore new rich application UI designs."
  * So the latest JavaFX will not only be available on the [Gluon website](https://gluonhq.com/products/javafx/), but also on [jdk.java.net/javafx20](https://jdk.java.net/javafx20/).
  * Curious what the impact of this announcement will be and what we can share here next week...

SceneBuilder {#h2-1-scenebuilder}
---------------------------------

* [**Chad Preisler**](https://twitter.com/cpreisler) shared [a video showing how to create a form using SceneBuilder and JavaFX](https://www.youtube.com/watch?v=auao5UNrUcg), getting the form to resize the correct way.
* [**Gluon**](https://twitter.com/GluonHQ/) announced the release of **Gluon Scene Builder 19** . You can get it from [github.com/gluonhq/scenebuilder/releases](https://github.com/gluonhq/scenebuilder/releases/tag/19.0.0).
  * It incorporates JavaFX 19 wich brings lots of improvements, so you benefit [from all these release highlights](https://openjfx.io/highlights/19/).
  * [This tweet shows a video](https://twitter.com/Raumzeitfalle/status/1578692849746718720?t=mNxAaRN22Frjkf6kTfGy-w&amp;s=09) of a bugfix on macOS where copy\&paste often resulted in entries doubled after paste. A new preference setting "alternative paste behavior for text input" is available on macOS and is enabled by default.

Devoxx Belgium {#h2-2-devoxx-belgium}
-------------------------------------

* Devoxx Belgium (10-14 October) [thanks Gluon in a tweet](https://twitter.com/Devoxx/status/1577934708100456448) for their continued support for the #OSS Devoxx mobile app. Sources of the DevoxxBadges JavaFX app [are available on GitHub](https://github.com/gluonhq/DevoxxBadges).
* The tweet wall was a crucial part of the information exchange between the visitors of Devoxx, showing the upcoming talks, highest ranked talks, etc. This tweet wall is a community effort driven by [**@jugbodensee**](https://twitter.com/jugbodensee) members, with the support of Gluon, and the [sources are on GitHub](https://github.com/TweetWallFX/TweetwallFX).
  * [**Johan Vos**](https://twitter.com/johanvos) shared a [picture in a tweet](https://twitter.com/johanvos/status/1580097937270788096).
  * By the way (1), TweetwallFX has its own [Twitter account @TweetwallFX](https://twitter.com/TweetwallFX).
  * By the way (2), the official "Devoxx" mobile app is also a JavaFX project, created by Gluon, that you can find [on GitHub](https://github.com/devoxx/MyDevoxxGluon). Check the GitHub workflows to learn [more about how it is building and publishing to the Google and Apple stores](https://github.com/devoxx/MyDevoxxGluon/tree/main/.github/workflows).

Various news from "The Web" {#h2-3-various-news-from-the-web}
-------------------------------------------------------------

* [**Dirk Lemmermann**](https://twitter.com/dlemmermann) announced [in a tweet](https://twitter.com/dlemmermann/status/1576974458761486338) version 11.12.1 of CalendarFX with new views for displaying resource allocations, improved editing behaviour, plenty of fixes and enhancements. You can [find it on GitHub](https://github.com/dlsc-software-consulting-gmbh/CalendarFX), and new link for the [documentation is here](https://dlsc-software-consulting-gmbh.github.io/CalendarFX/).
  * Dirk also added a new custom control to [**GemsFX**](https://github.com/dlsc-software-consulting-gmbh/GemsFX) for displaying screens and windows of a JavaFX application, inspired by MacOS. For a screenshot, check [this tweet](https://twitter.com/dlemmermann/status/1578426485299449857?t=bCzbA3BPMatyoQVM362l-A&amp;s=09) and a [demo is available on YouTube](https://www.youtube.com/watch?v=Kv7jo9fF9tc).
  * He also announced release 11.12.2 of [CalendarFX](https://www.jfx-central.com/libraries/calendarfx) with various bug fixes related to time zones and recurring entries.
  * And he shared [this video](https://www.youtube.com/watch?v=kwVXO0MdIdk) showing a custom JavaFX control that can be used to display groups of notifications either expanded or as a stack, inspired by the MacOS notification center. However, plenty of customisation options via API or CSS are available to make this control fit into any application.
* [**Robert Ladstätter**](https://twitter.com/rladstaetter) wanted to use JavaFX on a Windows aarch64 (virtual) machine, and [described the process to build it from the sources](http://ladstatt.blogspot.com/2022/10/a-javafx-fanboy-forgets-about-his.html).
* [**Almas Baim**](https://twitter.com/AlmasBaim/) shared [a video in a tweet](https://twitter.com/AlmasBaim/status/1576154186315882496) showing a bridge generated with FXGL (game engine) to illustrate a distance joint (which constrains two entities to preserve their distance to each other) that produces some interesting results. Entities with different densities are thrown at it and fly through the screen using physics.
  * Related to FXGL: the book ["Learn JavaFX Game and App Development with FXGL 17"](https://www.jfx-central.com/books/fxgl17) by [**Almas Baim**](https://twitter.com/AlmasBaim/) got reviewed by [**Frank Delporte**](https://twitter.com/FrankDelporte), read it on [foojay.io](https://foojay.io/today/book-review-learn-javafx-game-and-app-development-with-fxgl-17/).
* [**OrangoMango**](https://twitter.com/orango_mango) shared a [project to visualize and solve a Rubik's Cube with JavaFX](https://github.com/OrangoMango/RubikCube), that is also available as an Android APK. It's inspired by a [similar example created by Gluon](https://github.com/gluonhq/gluon-samples/tree/master/rubiks-cube).
* [**Abhinay Agarwal**](https://twitter.com/iAbhinay) shared a few very interesting JavaFX links:
  * [**edencoding.com**](https://edencoding.com/category/javafx/) has the "Most vibrant collection of JavaFX articles I have come across recently. Kudos to [**Ed Eden-Rump** AKA NerdyEden](https://twitter.com/NerdyEden)."
  * A list created by **Abhinay** himself with all the flags that might help you while debugging a JavaFX application on ["Flags for JavaFX application"](https://abhinay.xyz/javafx/2022/10/03/OpenJFX-flags.html)
* [**Robert Ladstätter**](https://twitter.com/rladstaetter) shared a [HelloWorld example project on GitHub](https://github.com/rladstaetter/javafx-advancedinstaller-example) to show how to install a JavaFX app with a Windows Installer using [**Advanced Installer**](https://twitter.com/advinst).
* [**Frank Delporte**](https://twitter.com/FrankDelporte) spoke on Devoxx in Antwerp about [Pi4J](https://pi4j.com/) and showed a JavaFX application running on a Raspberry Pi showing sensor data with the TilesFX library of [**Gerrit Grunwald**](https://twitter.com/hansolo_): [recording of the presentation on YouTube](https://www.youtube.com/watch?v=lnV0Hn2tias) and [links used in the presentation](https://webtechie.be/post/2022-10-10-devoxx-belgium-links/).
* [**Pedro Duque Vieira**](https://twitter.com/P_Duke) added two new controls to the FXComponents library:
  * BlockingProgressBar: [check the video example in this tweet](https://twitter.com/P_Duke/status/1580570179376816129).
  * ReordableListView, a ListView that can be reordered with the mouse by drag and dropping its cells and also supports drag and dropping from an outside source into a ListView cell position as you can see [in the video in this Tweet](https://twitter.com/P_Duke/status/1582732021448978433).
* [**Will Iverson**](https://twitter.com/wiverson) updated his [Java, JavaFX and Swing template for generating nice native installers for macOS, Windows and Ubuntu](https://github.com/wiverson/maven-jpackage-template) with a new single GitHub Action matrix script to generate all the installers w/nice human-friendly, matching version numbers.
* [**Clemens Lanthaler**](https://twitter.com/lanthale) released [PhotoSlide 1.3](https://www.jfx-central.com/real_world/photoslide) with many small updates and fixes including new version of librawfx and libheiffx and updates to JDK19/Javafx19 with better Multi-Threading again.
* A [new release 22.3.0](https://github.com/rladstaetter/LogoRRR/releases/tag/22.3.0) of LogoRRR - log file viewer - [has been announced](https://twitter.com/logorrr/status/1581654374719557632).
* [**WebFX**](https://twitter.com/WebFXProject) - a JavaFX to JavaScript transpiler - can now access local files, as you can read on [this GitHub discussion](https://github.com/webfx-project/webfx/discussions/14), with a [demo on files.webfx.dev](https://files.webfx.dev/).
  * [This new demo](https://webfx.dev/#/demos) shows MediaView added to the JavaFX Media emulation, which means that WebFX can now display videos.
* On September 30, [**Gail Anderson**](https://twitter.com/gail_asgteach) was speaking on IntelliJ IDEA Conference about "JavaFX for Mobile Development". We promised here to share the link to the video when available, and [here it is](https://www.youtube.com/watch?v=-8epeIFdKWo&amp;t=14498s).
* [**Sean Phillips**](https://twitter.com/SeanMiPhillips) shared several videos with an impressive data visualization tool.
  * [Arcing through decoded hyper-dimensional space using #Java and #JavaFX](https://twitter.com/SeanMiPhillips/status/1584287746637828098).
  * [Trinity JavaFX 3D Convex Hull Generator](https://www.youtube.com/watch?v=NXHQY5Fh1Do)
  * [Hull Point Cloud using #JavaFX 3D](https://twitter.com/SeanMiPhillips/status/1584204309956202496)
* [**Jakob Jenkov**](https://twitter.com/jjenkov) asked [in this tweet with a lot of interesting replies](https://twitter.com/jjenkov/status/1584090714320695296): "Hi JavaFXers - do we have some JavaFX application design patterns somewhere? Advice about how to structure a #Java + #JavaFX application so the code base and application does not get messy as the app grows? I have some ideas -but I'd like to see what the rest of you have too :-)"

New content on jfx-central.com {#h2-4-new-content-on-jfx-central-com}
---------------------------------------------------------------------

* Real World App: [JabRef](https://www.jfx-central.com/real_world/jabref) is an open-source, cross-platform citation and reference management tool, see [jabref.org](https://www.jabref.org/).
* Tool: ["Conveyor"](https://www.jfx-central.com/tools/conveyor) by [**Hydraulic**](https://twitter.com/HydraulicCorp), is an alternative/replacement for the jpackage tool but with support for (background) updates, signing, notarisation.
* Another library by [**Pedro Duque Vieira**](https://twitter.com/P_Duke): [FXParallax](https://www.jfx-central.com/libraries/fxparallax). This framework adds controls to add Parallax effects to JavaFX application, this effect can add a sense of depth (3D like) to where it's used.
* Coming soon... [**Florian Kirmaier**](https://twitter.com/FlorianKirmaier) is pimping the jfx-central website to make it much faster very soon. Due to the architecture of [jpro.one](https://www.jpro.one/), scrolling in a ScrollPane requires server calls (as the JavaFX app lives on the server). A custom skin, should bring a solution...
* People: the work of [**Jan Gassen**](https://twitter.com/jan_gassen) is listed on [this page](https://www.jfx-central.com/people/j.gassen).
* Library: [NSMenuFX](https://www.jfx-central.com/libraries/nsmenufx), by Jan Gassen, a simple library to customize the macOS menu bar to give your JavaFX app a more native look and feel.
* Tips: [Flags for JavaFX applications](https://www.jfx-central.com/tips/application_flags) to either add debug logs or switch configuration.
* Blogs: [abhinay.xyz](https://www.jfx-central.com/blogs/abhinay.xyz) by [**Abhinay Agarwal**](https://twitter.com/iAbhinay) about things good to know about JavaFX.
