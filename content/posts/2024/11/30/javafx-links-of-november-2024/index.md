---
title: "JavaFX Links of November 2024"
date: "2024-11-30T13:47:21+00:00"
lastmod: "2024-12-01T21:46:53+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of November 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-11-29-javafx-links-of-november-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-october-2024"
  - "javafx-links-of-september-2024"
  - "javafx-links-of-august-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of November 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

Did we miss anything? Is there anything you want to have included in one of the next overviews?

Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

## Core

* A lot of updates by **Johan Vos**
  * [Weekend update](https://mastodon.social/@johanvos/113415033521942389): "*Getting ready to create a developer preview for the headless platform.*"
  * [An update on OpenJDK Mobile](https://mastodon.social/@johanvos/113458868707472261), a project to bring Java (and JavaFX) better and easier to mobile: "_Progress on using hotspot on iOS without AOT: building native JDK libs = ok, *VM start = ok, Reading bytecodes = ok, Starting interpreter = work started. The reason I'm doing this with hotspot is that I only want to use code from OpenJDK. I need to 100% understand how the VM (e.g. hotspot) is doing its work. The docs in OpenJDK + JBS are extremely helpful and aligned with the JVM spec doc. Maintainability is a key asset in OpenJDK.*"
  * [Headless JavaFX update](https://bsky.app/profile/johanvos.bsky.social/post/3lbfnxsksvk27): "*First JavaFX headless builds are available for testing, see the [mailinglist](https://mail.openjdk.org/pipermail/openjfx-dev/2024-November/050984.html). Especially useful for JavaFX testing (and printing).*"
  * [Building several versions](https://bsky.app/profile/johanvos.bsky.social/post/3lbxblqdl2s2n): "*JavaFX keeps moving forward! We're now working on JavaFX 24, and JavaFX 17 and 21 are LTS releases, and so will JavaFX 25. No plans to drop support 🙂*"
  * [Coding tip](https://bsky.app/profile/johanvos.bsky.social/post/3lbwvq7ppbc2n): "*JavaFX Performance tip: do not use Platform.runLater() unless you're sure it is required. The Runnables are executed on the same platform thread that is also doing the layout at 60 fps.
    I often see Platform.runLater() is used "just to make sure..." but that can slow down performance.*"
* [**Carl Dea** shared](https://x.com/carldea/status/1856703165535584640): "*JavaFX will have custom image loaders! See [JDK-8343315](https://bugs.openjdk.org/browse/JDK-8343315). Man I love JavaFX! Thank you, [@jddarcy](https://x.com/jddarcy)!*"

## SceneBuilder

* [Again **Johan Vos** 😉](https://mastodon.social/@johanvos/113468864869281531): "*Scene Builder is getting much better. [Major changes in 24 are coming](https://gluonhq.com/scene-builder-24-0-0-rc1-whats-coming-for-javafx-ui-developers/): A major technical debt has been addressed: the whole structure, including Scene Builder Kit, is now modular. I highly recommend creating your JavaFX interfaces with this RC release!*"

## Applications

* [**Robert Ladstätter** announced LogoRRR 24.5.1](https://graz.social/@rladstaetter/113380976846373387) for Windows, Mac, and Linux: "*More information about the included features and bugfixes is [available here](https://github.com/rladstaetter/LogoRRR/releases/tag/24.5.1)*."
* [**Bluerain** announced Mago](https://x.com/bluerai1n/status/1855383577199775882): "*A JavaFX-based shell generator tool that creates payloads for Linux, Windows, and web systems. Generate payloads for various operating systems, Supports Base64 and URL encoding for generated commands, 110+ Payload.* " You can [find it on GitHub](https://github.com/blue0x1/Mago).
* v3.20.1 of binjr, a standalone time series browser, is now available: "*This is an interim release which fixes a regression introduced in v3.20.0 that severely impacts the performances of the CSV and Log files adapters. Read the [full changelog and download it here](https://binjr.eu).* "
  * Related to this release, [**Frederic Thevenet** shared](https://mastodon.social/@fthevenet/113469334194641331): "*I've been pretty pleased with my custom 'flickering neon sign' effect in JavaFX for the binjr logo, until the moment I realized my 12 y/o had no idea what that flickering was supposed to be! A very 'what's the color of a TV tuned to a dead channel?' moment...*"
  * If you want to stay informed about new binjr releases: [subscribe to their RSS](https://binjr.eu/feed_rss_updated.xml).
* [**Gerrit Grunwald** shared a video](https://bsky.app/profile/hansolo.eu/post/3lb2eehtyu22q): "*And here you go... my JavaFX-based QlockTwo with support for swipinp, on a Raspberry Pi.*" He isn't allowed to share the code because the design is copyrighted, but it is a piece of art to look at and a nice challenge to recreate it by yourself 😉
* [**Jago de Vreede** is looking for help](https://x.com/JagoVreede/status/1857082809355002346): "*I've created an early-access build for aarch64 binary for Mac of SKDman-UI. Unfortunately, I can't test that one because I don't have an M-MacBook. Does anyone with an M MacBook want to see if it works?* "
  * The [(early-access) releases are on GitHub](https://github.com/jagodevreede/sdkman-ui/releases), including a version for x86 macOS.

## Games

* [**BJ Dela Cruz** shared a screenshot of his "Wordle" application](https://www.linkedin.com/posts/bj-delacruz_javafx-java-javaprogramming-activity-7262307662577520640-8mcI/): "*Finally, it will be able to generate an image that the player can keep to remember what moves he or she performed to win the game.*"
* [**Mark J. Koch**](https://bsky.app/profile/markjkoch.bsky.social/post/3lbutov6xp22o): "*One of my bucket list game projects has reached Alpha. I'm looking for enthusiasts to try it. I ported/re-engineered Neuromancer PC to JavaFX. Original content, all new engine. For now requires NetBeans IDE to run.* " You can [find it on GitHub](https://github.com/maehem/javamancer).

## Components, Libraries, Tools

* [**Carl Dea** published Cognitive 1.5.0](https://x.com/carldea/status/1851983577321255003), a JavaFX MVVM forms library.  
  Check the [wiki for new features](https://github.com/carldea/cognitive/wiki#new-features).
* **Pedro Duke** continues with his work in progress on the next release of Transit Theme and shared more screenshots:
  * [Part 9](https://x.com/P_Duke/status/1850541268331360395): "*New LIGHT and DARK styles for Spinner and ChoiceBox*."
  * [Part 10](https://x.com/P_Duke/status/1852713716531314778): "*New LIGHT and DARK styles for MenuButton and SplitMenuButton*"
  * [Part 11](https://x.com/P_Duke/status/1855617371186020476): "*New LIGHT and DARK styles for Editable ComboBox, Pill Buttons and Pill Toggle Buttons.*"
  * [Part 12](https://x.com/P_Duke/status/1855617371186020476): "*New LIGHT and DARK styles for Editable ComboBox, Pill Buttons and Pill Toggle Buttons.*"

## Podcasts, Videos, Books

* Not new, but just discovered: [**Chad Preisler** uses a JavaFX UI to demo Kafka Outer Joins](https://bsky.app/profile/chadpreisler.bsky.social/post/3l3vjfi4z742r): "*Make sure to stick around for the [demo right around four and half minutes into the video](https://www.youtube.com/watch?v=bGffINGD9KQ). Happy coding!*"
* New "JFX In Action" interviews by **Frank Delporte** :
  * Insights into a new open-source JavaFX project: Swaggerific. It's created by [**Özkan Pakdil**](https://techhub.social/@thejvmbender), who tells us more about this Postman alternative to interact with REST endpoints documented with a Swagger JSON. The [video is on YouTube](https://www.youtube.com/watch?v=3_T0LDZ-Wt4), and more info is provided in [this blog post](https://webtechie.be/post/2024-11-05-jfxinaction-ozkan-pakdil/).
  * [Interview with **Clément de Tastes**](https://webtechie.be/post/2024-11-19-jfxinaction-clement-de-tastes/) : "*In this episode, we take a look at the combination of JavaFX and Quarkus. Thanks to [QuarkusFX](https://www.jfx-central.com/libraries/quarkusfx), we can make use of the many advantages of the Quarkus system to create a desktop application.*"
  * Video #5 till #8 of these interviews got published in a summary on Foojay: [Video series "JavaFX In Action", Part 2](https://foojay.io/today/video-series-javafx-in-action-part-2/) with **Maciej Gorywoda** about FxCalculator, **Ramiro Domínguez Ayub** about the Televic Generic Update Tool, **Christoph Schwentker** about JabRef, and **Ulas Ergin** about migrating from Swing to React UIs, all combined in one Java(FX) app.
* [**Anton Arhipov** has a box with books to review](https://bsky.app/profile/antonarhipov.bsky.social/post/3lbjqjdtf2s2c): "*I was helping with technical reviews of this edition. The author **Faisal Islam** teaches Kotlin by explaining and visualizing various algorithms. They used JavaFX for visualization though and I'd love to see another edition of this book using Compose instead.*" Let's conclude the Faisal made the right choice and Anton is wrong 😉

## Tutorials

* [**Alexander S. Ricciardi** shared a tutorial about JavaFX Layout Managers](https://x.com/AlexOmegapy/status/1855082449589748028): "*This article explores how Layout Managers provide an abstraction that streamlines the development of Graphical User Interfaces (GUIs) in JavaFX by automating component sizing and positioning. Using predefined layouts like HBox, VBox, and GridPane, developers can create organized and responsive interfaces.*"
* [**polypragmatist** shared on Bluesky](https://bsky.app/profile/polypragmatist.bsky.social/post/3lbabzug2bk2j): "*Looking to learn JavaFX? Take a look at my [Beginners' Guide to JavaFX](https://www.pragmaticcoding.ca/beginners/intro). It's a 13-part tutorial that takes you from 'Hello World' to building an Reactive GUI with a framework. No FXML, all the layouts are done in code.*"
* Tutorial by Codez Up:
  * "*[Building a Social Media Dashboard with JavaFX](https://codezup.com/building-a-social-media-dashboard-with-java-fx/).*"
  * "*[Unlock Efficient Cloud and Edge Computing with JavaFX Websocket Services](https://codezup.com/cloud-edge-computing-javafx-websocket-services/)*", to create a JavaFX application that uses Websocket Services to communicate with a server.

## Miscellaneous

* [On Bluesky, **Gerrit Gruwald** explains](https://bsky.app/profile/hansolo.bsky.social/post/3l6c24vt4222g) that the [visuals of his Devoxx presentation](https://www.youtube.com/watch?v=Jh79ojcror0) are created with JavaFX.
* You can read more about JavaFX Nodes versus Canvas experiments by **Frank Delporte** in the [German JavaMagazin edition 12.2024](https://entwickler.de/java/javafx-nodes-versus-canvas).
* [**Sean Phillips** shared on LinkedIn](https://www.linkedin.com/posts/seanmiphillips_projected-volumetric-detection-method-applied-activity-7256695177157689345-2Okp/): "*Applying 3D Projected Volumetric Detection method to the Biden Robocall Deep Fake. ... Caveats to this demonstration are that this was clearly a controlled experiment and importantly is a qualitative manual process. However it is clear the potential of the method and with sufficient further development could be largely automated.*"
* [JVM Weekly vol. 106 by Artur Skowroński](https://www.linkedin.com/pulse/microprofile-ai-jakarta-ee-12-data-whats-new-java-jvm-skowro%C5%84ski-ihd2f/) has a section "What Does Java UI Development Look Like in 2024?" which includes, of course, JavaFX:
  * "*How could we skip JavaFX, especially with a new and interesting application in this technology?*"
  * "*[JTaccuino](https://github.com/jtaccuino/jtaccuino) is a notebook developed for Java programmers, allowing users to interactively experiment with code.*"
  * "*The DeepNets project, which is a reference implementation of JSR 381, recently [boasted new tooling enhancements](https://x.com/DeepNetts/status/1835292030315995390).*"
* [**saige!** shared on Bluesky](https://bsky.app/profile/catgirlin.space/post/3l7u33iijn42k): "*I made a little animation in JavaFX. Gonna try and convince my group that we should use it for our project as a loading screen.*"
* [**Anthony Goubard** shared a screenshot](https://x.com/Anthony_Goubard/status/1853791630815277332): "*New in the IDE plugin 'Applet Runner': JavaFXApplet and SceneApplet classes to run a JavaFX application (or scene) embedded in your IDE. See the demo of TilesFX from **Gerrit Grunwald** running in IntelliJ IDEA.*"
* You want to find even more "JavaFX Goodies" than you can find on JFX Central? Head over to the [AwesomeJavaFX repository on GitHub](https://github.com/mhrimaz/AwesomeJavaFX), started by [**Hossein Rimaz**](https://x.com/mhrimaz) and [extended by many others](https://github.com/mhrimaz/AwesomeJavaFX/graphs/contributors).
* [JPro, bringing Java to the browser](https://x.com/jpro_one/status/1859539536524578973), is following the Java "release train": "*As of JPro 2024.4, JavaFX 23 is now supported and set as the default version. Browser tabs now reflect the JavaFX stage's title and favicon, and more! Catch up on the [latest updates here](https://www.jpro.one/docs/current/3.1/2024.4.X)*."
* [A video by **OrangoMango**](https://x.com/orango_mango/status/1857923838195151223): "*The start of a new project: Mandelbrot set visualization. Still not perfect, it has some small issues. 🙂*"
* [**Jonathan Mark Mwigo** shared a screenshot](https://x.com/mwigojm/status/1858842708611125690): "*JavaFX Modern Sales ERP Dashboard. Excited to see it go live! 🚀💻*"
* [**polypragmatist** is very active promoting JavaFX on Bluesky](https://bsky.app/profile/polypragmatist.bsky.social/post/3lbte3cvxvs2p), for instance: "*JavaFX is 100% a reactive declarative UI development library. No FXML required. The Observables facility is tightly integrated into the UI components and makes React look clunky in comparison. Last I looked, Java supported loops and conditionals. JavaFX works great with Kotlin too.*"
* [**Johan Vos** is testing bld to replace Maven](https://bsky.app/profile/johanvos.bsky.social/post/3lbr6rz6qoc2v): "*Did a first project using bld. Works great, and I love the 'no auto-magical behavior'. I need a deeper look to see how to deal with platform-specific jars at runtime, e.g. JavaFX, where we currently use a maven plugin to deal with this.*"

## JFX Central

* JFX Central is now also on Bluesky: [@jfxcentral.com](https://bsky.app/profile/jfxcentral.com)
* [Message from **Dirk Lemmermann**](https://x.com/dlemmermann/status/1853719535901122837): "*JFX Central is now available as a web app, a desktop app, and an iOS application (via Gluon). We still need to get it into the Google Play Store. Anyone up for the job? If any code changes are required I could work on that. I just need somebody to take over the deployment part to the Play Store. I do not feel at home at all in the Android space 🙂*"
* The overview of the September Links Of The Week is [published on Foojay](https://foojay.io/today/javafx-links-of-october-2024/).
* New content:
  * Video: [JavaFX In Action with Özkan Pakdil about Swaggerific](https://www.jfx-central.com/videos/3_T0LDZ-Wt4)
  * Video: [JavaFX In Action with Clément de Tastes about QuarkusFX](https://www.jfx-central.com/videos/Vw9S9uuPTlQ)
