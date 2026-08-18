---
title: "JavaFX Links of April 2024"
date: "2024-04-29T15:22:05+00:00"
lastmod: "2024-04-29T15:22:51+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of April 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-04-26-javafx-links-of-april-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-march-2024"
  - "javafx-links-of-february-2024"
  - "javafx-links-of-january-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of April 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

## Core

* **Gluon** published new releases of JavaFX: 22.0.1 and the LTS releases 17.0.11 and 21.0.3. You can get them from Maven Central and the [Gluon website](https://gluonhq.com/products/javafx/).
* The new builds of Java and JavaFX, that were released this week, contain several fixes related to Common Vulnerabilities and Exposures (CVE), see for example the [release notes of Azul Zulu](https://docs.azul.com/core/release-notes#fixed-common-vulnerabilities-and-exposures). The 3-monthly security update schedule, guarantees that detected CVEs are fixed quickly and become available in a well-organized and documented way. Thanks to the power of the OpenJDK community!
* **Johan Vos** is "[Fixing one test at a time to get JavaFX Headless support in the core](https://mastodon.social/@johanvos/112325369175582324)."

## Applications

* [JabRef 5.13 now runs on JavaFX 22](https://blog.jabref.org/2024/04/03/JabRef5-13/): "In the Northern Hemisphere, Spring is on it's way and we are releasing a new version with many new features and bug fixes with a focus on improving the usability (not only) for LaTeX users."
* [**Emad Hanif** shared a video of Barcodify](https://twitter.com/EmadHanif_/status/1782111780611010776), a barcode generator, improving the application speed with Task-Level Concurrency vs Thread-Level Concurrency.
* [**Sean Phillips** shared](https://twitter.com/SeanMiPhillips/status/1781480060408537145) a [video, showing Trinity in action](https://www.youtube.com/watch?v=jI5r-flszzU) to detect the Biden robocall deepfake on a consumer grade laptop (as presented at DevNexus).
  * And Sean also [shared a futuristic user interface](https://twitter.com/SeanMiPhillips/status/1783677579980845232)

## Games

* [**Almas Baim** shared a new experiment with FXGL](https://twitter.com/AlmasBaim/status/1776166816814977289): "Is it just me, or does this thing look like an eye?"

## Components, Libraries, Tools

* [LogoRRR shared a video](https://twitter.com/logorrr/status/1774901636965519758) demonstrating [TestFX](https://github.com/TestFX/TestFX) "the testing framework for JavaFX applications. We use it to verify that everything works as designed 🙂."
  * "There is active development to support headless execution of tests - see [#803](https://github.com/TestFX/TestFX/issues/803)! 🎉"
* [**Pedro Duque Vieira** announced version 1.6 of FXComponents](https://twitter.com/P_Duke/status/1777378767670841488), introducing a new control: Navigation Pane. [More info and a video is available here](https://www.pixelduke.com/2024/04/08/fxcomponents-version-1-6-released/).
* [**Dirk Lemmermann** announced version 3.1.0 of PDFViewFX](https://twitter.com/dlemmermann/status/1777686986003796056): "It contains a fix for the zooming bug and cleans up the use of CSS. You can find the [source code on GitHub](https://github.com/dlsc-software-consulting-gmbh/PDFViewFX) and the artifacts on Maven Central."
* [**Dirk Lemmermann**](https://twitter.com/dlemmermann/) shared a lot of updates for the GemsFX library, thanks to contributions by [**Li Wang Yang**](https://twitter.com/LeeWyatt_7788). GemsFX is a collection of custom controls and utilities for JavaFX, see [sources on GitHub](https://github.com/dlsc-software-consulting-gmbh/GemsFX). Here are some screenshots:
  * [LimitedTextArea](https://twitter.com/dlemmermann/status/1780122735177339359), which allows you to specify the maximum content length (and is resizable as it inherits from ResizableTextArea).
  * [Responsive pane](https://twitter.com/dlemmermann/status/1780123307901136965), which allows you to show a node on one of the four sides either fully or in a smaller size depending on available space (or not at all when there is too little space).
  * [Circle progress indicator](https://twitter.com/dlemmermann/status/1780123562163961858).
  * [EnhancedPasswordField](https://twitter.com/dlemmermann/status/1780122006823883068), which gives you additional capabilities compared to the standard #avaFX password field (e.g. "show password").
  * [Tree and graph view](https://twitter.com/dlemmermann/status/1780123785334522160).

## Conferences

* [**Gregor Schmid** wrote a blog about the JFX Adopters Meeting in Munich](https://www.qfs.de/en/blog/article/javafx-more-alive-than-ever.html): "JavaFX -- More alive than ever. It was an impressive demonstration of the diverse activities of the FX world: organized with a lot of passion by **Christian Heilmann**."
* [**Matt Raible** shared](https://twitter.com/mraible/status/1778156667764408768): "This was an awesomely energetic talk by **Sean Phillips**! I enjoyed it immensely. Check out his Trinity project which can detect AI-generated audio in minutes. Built with JavaFX."

## Tutorials

* New tutorial by [**Dave Barrett**](https://twitter.com/Polypragmatist): [EventHandlers, Listeners and Bindings - What to Use Where](https://www.pragmaticcoding.ca/javafx/elements/events_and_listeners): "The more that I work with JavaFX, the more that I am convinced that it is one of the best frameworks for building "Reactive User Interfaces" that's out there."
* [**Abhinay Agarwal**](https://twitter.com/iAbhinay) wrote a blog post: [Platform preferences API: ColorScheme usage](https://abhinay.xyz/javafx/2024/04/06/Platform-preferences-API.html): "JavaFX 22 introduces several new features and enhancements. One of the notable additions is the Platform preferences API, which provides developers with a convenient way to access platform-specific preferences and adapt their applications accordingly. In this blog post, we'll explore the new APIs and demonstrate how to leverage them in your JavaFX applications."
* [**Hantsy Bai** rewrote the Cargotracker regapp in Quarkus and JavaFX](https://github.com/hantsy/quarkus-cargotracker-regapp): "The original Cargotracker regapp from Eric's DDD book sample was written in Swing and Spring. I have created a variant of CargoTracker Regapp (CDI/Weld + JavaFX) to submit handling events to the cargotracker core system (forked from eclipse-ee4j/cargotracker)."
* The blog and videos by **Frank Delporte** "Search in Documentation with a JavaFX ChatGPT-like LangChain4j Application" are now also [published on Foojay](https://foojay.io/today/search-in-documentation-with-a-javafx-chat-langchain4j-application/).
* [**Loïc Lefèvre**](https://twitter.com/Loic__Lefevre/) wrote on Medium: "[Building JavaFX app native image with GraalVM: New achievement unlocked!](https://medium.com/db-one/building-javafx-app-native-image-with-graalvm-new-achievement-unlocked-c5e236ecf11d)"
* [**Rushi Bhatti** shared a video showing three small JavaFX experiments](https://twitter.com/RB_Bhatti_171/status/1776931544688734495), you can find the [sources on GitHub](https://github.com/RushiBhatti/JavaFX_Projects):
  * Tic Tac Toe (basic gameplay)
  * BMI Calculator (calculates your Body Mass Index)
  * Indian Flag (a simple tribute)
* [Small code example by **Manfred Riem**](https://twitter.com/agoncal/status/1783732219615297908): "Having some fun with JavaFX. How do you [add a ToolTip to a cell in a TableView](https://manorrock.com/blog/2024/04/25/add_a_tooltip_to_a_cell_in_a_table_view.html)?"

## Miscellaneous

* [**Sean Phillips** played around with 3D cylindrical projections](https://twitter.com/SeanMiPhillips/status/1775836658304201122): "Setup a system that can convert the pixels of an image to a 3D scatter plot wrapped around a cylindrical projection, using the color hue as an elevation offset to the base radius."
* **WhiteWoodCity** : "I made it, I made it. Successfully use [Vulkan to create a triangle and then export to the javafx writable image to display it](https://twitter.com/WhiteWoodCity/status/1773700873077547085)." Check [GitHub for the source code](https://github.com/chengenzhao/java-vulkan-mac).
* [**Pavel Perikov** shared a video](https://twitter.com/ppavel24/status/1775603966262358253): "I just wanted to experiment with the canvas rasterisation performance, but now I developed some empathy to the life of the 1000 circles involved."
* [**Pedro Duque Vieira** created a song with Suno](https://twitter.com/P_Duke/status/1777678966746161226): "JavaFX all the Way"...!
  * And [**Almas Baim** did something similar with FXGL](https://twitter.com/AlmasBaim/status/1777381482257944904).
* **Sean Phillips** is "so [close to having a Cylindrical Surface projection](https://twitter.com/SeanMiPhillips/status/1777434462478803387) based on an arbitrary data grid working..."
* For macOS 14 users: [**Gerrit Grunwald** published a new version of JDKUpdater](https://twitter.com/hansolo_/status/1778774663939707037) with the ability to download builds of OpenJDK from different distributions, with or without JavaFX included. [Sources and releases are available on GitHub](https://github.com/HanSolo/JDK-Updater/releases).
* Do you want to use JavaFX on the Raspberry Pi? Thanks to contributions by [**Robert von Burg**](https://mstdn.gsi.li/@eitch), the [Pi4J JavaFX example project](https://github.com/Pi4J/pi4j-example-javafx) downloads JavaFX 22 from the Gluon website automatically when you build the application with Maven.

## JFX Central

* On Foojay.io, you can find an [overview of all the JavaFX Links of March](https://foojay.io/today/javafx-links-of-march-2024/).
