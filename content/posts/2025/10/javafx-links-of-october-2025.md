---
title: "JavaFX Links of October 2025"
slug: "javafx-links-of-october-2025"
date: "2025-10-31T07:31:45+00:00"
lastmod: "2025-10-31T07:33:20+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of October 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-10-31-javafx-links-of-october-2025/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of October 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via links@jfx-central.com.

Core {#h2-0-core}
-----------------

* [Article by **Paul Krill**](https://www.infoworld.com/article/4065025/javafx-25-previews-javafx-controls-in-title-bars.html): JavaFX 25 previews JavaFX controls in title bars. "*Preview feature in latest update of the Java client application platform defines a Stage style that allows applications to place scene graph nodes in the header bar area.*"
* [**Johan Vos** is working on Java on Mobile](https://mastodon.social/@johanvos/115309280336500000): "*I can now run HelloFX on an iPhone. This required only really minor changes to OpenJFX and OpenJDK, and I'll update [github.com/openjdk-mobile/openjfx-build](https://github.com/openjdk-mobile/openjfx-build) as soon as I find some time. Most of the work (\> 99%) went into understanding the tons of options in XCode. Next step is the integration of Leyden code into OpenJDK/mobile. Hope to get support from the Leyden experts.*"
* [Message from Gluon](https://bsky.app/profile/gluonhq.com/post/3m3puhsuhrk2a): "*The CPU releases for JavaFX are available. [Download the latest JavaFX SDKs with security fixes](https://gluonhq.com/products/javafx/) or get them from Maven Central. You'll find the latest released, JavaFX 25.0.1 and the Gluon JavaFX LTS releases 17.0.17 and 21.0.9.*"
* Is the "OpenSource Model" broken for OpenJFX (and all other projects)? And are those who are making money from it not interested in fixing it? [Interesting discussion on Bluesky](https://bsky.app/profile/johanvos.bsky.social/post/3m3mejmxq5k2p)...

SceneBuilder {#h2-1-scenebuilder}
---------------------------------

* **Plant Fall** is working on a SceneBuilder-alternative without XML, see the [video on YouTube](https://www.youtube.com/watch?v=TpYZLPtD62A). Looks like it's in early stage, curious to see how it will evolve...
  * **Plant Fall** published a new video about a SceneBuilder-alternative without XML: "*In [this video](https://www.youtube.com/watch?v=BrXzMbWUU-Y), I show the progress of Morphos Desktop FX -- Version 2, a powerful GUI builder that lets you create your JavaFX interfaces visually --- and now it can generate complete source code and save your layouts as JSON files.*"

Applications {#h2-2-applications}
---------------------------------

* [**Patrik Karlström** released version 25.09 of nbRsync](https://bsky.app/profile/trixon.se/post/3lzsd33jkzs2a), a rsync GUI: "*It's mostly about dependency updates such as Netbeans 27, JDK and JavaFX 25.* " You can find it on [GitHub](https://github.com/trixon/nbRsync).
* [Video shared by LogoRRR on Bluesky](https://bsky.app/profile/logorrr.bsky.social/post/3lzywsev5622b): "*Working on a nice addition to LogoRRR - Group and reuse your search queries! Reapply them to your multiple log files - this will surely save you time. This will be part of the next release.* "
  * [LogoRRR shared a screenshot of an upcoming release](https://bsky.app/profile/logorrr.bsky.social/post/3m3bbbh3pjk2x): "*The goal is absolute simplicity for you, which requires a lot of development and careful planning behind the scenes.*"
* **Patrik Karlström** announced [Mapton 25.10](https://bsky.app/profile/trixon.se/post/3m2k7itmwi22z): "*...the open source Java(FX) based generic map platform built on WorldWind Netbeans 27 platform \& JDK 25. 👋 Downloads with runtime from
  Azul are [available for Windows \& Linux](https://github.com/trixon/mapton/releases) (including appimage \& snapcraft).*"
* [Message by **Mirko Sertic**](https://bsky.app/profile/mirkosertic.de/post/3m3sgoql3k22o): "*I found some time to update JavaFX DesktopSearch to the latest Java 25, Lucene and#Tika releases. Maybe I will also add some LLM or MCP features. We'll find out. [Checkout on GitHub](https://github.com/mirkosertic/FXDesktopSearch) for more to come 🙂*"
* [v3.25.0 of binjr, a standalone time series browser, is now available](https://social.binjr.eu/@binjr/statuses/01K8R4TE2R0HDXQR1816KE4MC6): "*Aside from being based on the latest Java25 runtime --- making this a somewhat themed release --- the main features this time around are: much improved support for the ZGC garbage collector logs files for the OpenJDK JVM, improvements to handling of CSV files with things like support for comments and better number parsing for uncommon notations, bug fixes, and quality of life enhancements. Full changelog and download links are available on [binjr.eu](https://binjr.eu).*"

Games {#h2-3-games}
-------------------

* No JavaFX, but still very impressive Java-based game development: Nostr Game Engine on [GitHub](https://github.com/NostrGameEngine/ngengine) and [here is the website](https://ngengine.org/). "*Based on jMonkeyEngine. A game engine and framework for building games and applications integrated with the Nostr ecosystem and p2p networking.*"

Components, Libraries, Tools {#h2-4-components-libraries-tools}
---------------------------------------------------------------

* KickstartFX is an impressive project by **Christopher Schnick** : "*An advanced, ready-to-use template for JavaFX applications. It can serve as a solid foundation for your own JavaFX application as everything is fully customizable and extendable. KickstartFX is much more than just a basic template that opens a simple window. It contains a lot of code to handle the challenges of applications in the real world to achieve the best possible desktop application experience across all operating systems. The code is based on [XPipe](https://github.com/xpipe-io/xpipe), a well-established JavaFX application, and is the result of years of experience developing a desktop application that is used by many thousands of users right now.* " Very [detailed documentation is available here](https://kickstartfx.xpipe.io) and the [sources are available on GitHub](https://github.com/xpipe-io/kickstartfx).
* [JPM promises to provide a better experience compared to Maven and Gradle](https://bsky.app/profile/sunnykentz.bsky.social/post/3m2k2gs3t6c2l): "_Imagine if it was simple to create a desktop app in kotlin.... Wait it is: [jpmhub.org](https://www.jpmhub.org/): `MARKDOWN_HASH141e92adc7387a4d2beb57a986c7513bMARKDOWN`*HASH*."
  * [JPM shared a video](https://bsky.app/profile/sunnykentz.bsky.social/post/3m37jrdglpc2a): "*To show the true power of [JPM](www.jpmhub.org) I created Neutron, a native app builder that leverages the power of JavaFX's webview for easy development.* " [Sources on GitHub](https://github.com/jpm-hub/neutron?tab=readme-ov-file).
* [Message from **Clément de Tastes**](https://bsky.app/profile/cdetastes.bsky.social/post/3m2w77c2kts2w): "*A major milestone has been reached on the quarkus-fx extension as it now supports native build in its 0.10.0 release. [Please report any found issue](https://github.com/quarkiverse/quarkus-fx).*"
* [Message by Dirk Lemmermann](https://bsky.app/profile/dlemmermann.bsky.social/post/3m3mrf4f2ps2u): "*Chasing memory leaks in my JavaFX based application is a breeze when using [JMemoryBuddy](https://github.com/Sandec/JMemoryBuddy) from [**Florian Kirmaier**](https://bsky.app/profile/did:plc:mszvyrtwuphvznao54kwrqat) I add it to my prod code, not just test classes. At any time I can see whether UI views got garbage collected or not. When I see that a view did to get garbage collected I launch [VisualVM](https://visualvm.github.io/), search for the JMemoryBuddyLive instance (in the heapdump) with the uncollected view, click on the 'referent' and open the 'GC Root' view. That tells me what is still holding a reference to the view.* "
  * And [**Dirk** shared screenshots](https://bsky.app/profile/dlemmermann.bsky.social/post/3m3mk7r2usk2z): "*I have added an updated and improved version of the SegmentedBar control to GemsFX. Will be released today in version 3.6.0. This is a real-world example of the SegmentedBar control being used. Our app is using it as part of the 'debt collection' user interface. It shows how many bills haven't been paid, how many have started the collection process, etc...*"
  * [Followed by screenshots of version 3.6.1](https://bsky.app/profile/dlemmermann.bsky.social/post/3m3ufrkafyc27): "*I have added a new 'StretchingTilePane' container so that I can finally create a nice responsive tile-based layout for the module selection view of our CRM solution. The default JavaFX TilePane does not fill the available width. It also came in handy for the launch pad section of our "market data portal". Each card is a "tile" and depending on available width we want to either have two or three of them in a row.*"

Conferences, Presentations {#h2-5-conferences-presentations}
------------------------------------------------------------

* Talks from Devoxx Belgium using JavaFX:
  * [Java Adventures - JTaccuino, Java 25 and AI](https://m.devoxx.com/events/dvbe25/talks/23160/java-adventures-jtaccuino-java-25-and-ai) by **Sven Reimers** : "*A demo packed session awaits you showing you the latest Java 25 features using JTacccuino notebooks. JTaccuino is a pure Java and JavaFX notebook solution, which allows for easy exploration in Java by leveraging JShell, dependency resolution, custom extensions and more. The aim is to reduce typical existing ceremony in API's for simpler and more scripting style interaction.* " The [video is available here](https://www.youtube.com/watch?v=HJIst2dXKEA).
  * [Empowering Agentic AI with Industrial and Scientific JavaFX Desktop Applications via MCP](https://m.devoxx.com/events/dvbe25/talks/26808/empowering-agentic-ai-with-industrial-and-scientific-javafx-desktop-applications-via-mcp) by **Michael Hoffer** : "*Most companies, research groups and development temas have established, high-quality desktop tools that are cut off from the benefits of Agentic AI. This talk will show how to integrate these existing JavaFX applications with AI using the Model Context Protocol (MCP), unlocking sophisticated new automation workflows.*"
  * [Robotics and GraalVM native libraries](https://m.devoxx.com/events/dvbe25/talks/20674/robotics-and-graalvm-native-libraries) by **Florian Enner** : "*Our company creates custom robotic solutions for a wide range of applications, from performing real-world industrial inspection tasks to providing robust platforms for building intelligent autonomous systems. This presentation provides a rare behind-the-scenes look into how we use Java for real-time control, and why we are considering replacing parts of our C++ codebase with GraalVM's native shared libraries.* " The [video is available here](https://www.youtube.com/watch?v=md2JFgegN7U). It shows a very impressive JavaFX UI with 3D visualization of the interactions with robot arms.

Podcasts, Videos, Books {#h2-6-podcasts-videos-books}
-----------------------------------------------------

* [**Frank Delporte** is working on the 2025 update](https://bsky.app/profile/did:plc:jx7h5s74cqipmtc7zrb5224m/post/3m27psgxob22t) of his book with Java and JavaFX examples for the Raspberry Pi: "*I have just published a new version of my book, 'Getting Started with Java on the Raspberry Pi,' on Leanpub. If you purchase now, you will receive any future updates at no additional cost. Updated for Java 25, with more updates following soon!*"
* A new video by **Catherine Edelveis** : [Top 7 JavaFX Testing Mistakes You Need To Avoid!](https://www.youtube.com/watch?v=2KiFPZIc0MI): "*Stop making these common JavaFX testing mistakes! No more random NullPointerExceptions or deadlocks --- in this video, we'll show you how to fix the 7 most common TestFX pitfalls when testing JavaFX applications. Learn how to handle FX threading, integrate with Spring Boot, avoid event queue races, fix pixel test differences, configure headless CI with Monocle, and properly separate business logic from UI tests. Whether you're writing your first JavaFX test or debugging flaky CI builds, this guide will help you build reliable and maintainable UI tests.*"
* New "JavaFX In Action" interviews, published by **Frank Delporte** :
  * [**Vlad Protsenko**, Combining Clojure with JavaFX for Game Development with Defold](https://webtechie.be/post/2025-10-16-jfxinaction-vlad-protsenko-closure-cljfx-defold/). "*Vlad is a Clojure developer working at Defold. While I initially wanted to learn about the Cljfx project, our conversation evolved into a learning experience: a practical getting-started guide to Clojure, a hands-on demonstration of building JavaFX user interfaces with minimal code, and an inside look at the Defold game engine and its JavaFX-based IDE.*"
  * [**Matt Coley** about Recaf and the JavaFX libraries he's working on]((https://webtechie.be/post/2025-10-30-jfxinaction-matt-coley-recaf-bentofx-treemapfx-glcanvasfx/)): "*But unexpectedly, I got a deep-dive course on #Java byte code, obfuscated code, and how JARs can be (ab)used to hide the real code they are executing...! So, it's not just a new JavaFX In Action interview, but an inspiring story about how Matt got into the foundations of the Java language thanks to his love for Minecraft.*"

Tutorials {#h2-7-tutorials}
---------------------------

* **Abid Maqbool** published [My Journey of Porting JavaFX to macOS and iPhone --- Challenges, Tips, and Cross-Platform Lesson](https://www.linkedin.com/pulse/my-journey-porting-javafx-macos-iphone-challenges-tips-maqbool-7pxme/): "*In this article, I'll share my motivations, challenges, setup, results, and key lessons from testing JavaFX for macOS and iPhone. Hopefully, this will help other developers aiming to explore true cross-platform Java development.*"
* **Haidar Ali** on the Baeldung website: [Constructor vs initialize() Method in OpenJFX](https://www.baeldung.com/javafx-constructor-vs-initialize): "*In this article, we'll compare the standard constructor method of POJO and the JavaFX-specific initialize() method. First, we'll grasp the JavaFX controller lifecycle, and then we'll compare it against a constructor. Finally, we'll look at some of the gotchas, pitfalls, and best practices that we can employ in a JavaFX software.*"
* From **Michiel** : "*Yes, I've managed to deploy my very first 100% Java (with JavaFX) mobile application in the [Google Play Store](https://play.google.com/store/apps/details?id=nl.dotjava.javafx.iceconverter)! I've described the [process of getting it in the Play Store here](https://www.dotjava.nl/2025/10/05/signing-your-app-to-get-it-in-the-google-play-store/). Now you can convert euros to Icelandic krónur without building the app first 😉 Of course, it's still [free and open source](https://github.com/michiel-jfx/iceconverter).*"

Miscellaneous {#h2-8-miscellaneous}
-----------------------------------

* [**Johan Vos** is looking forward](https://bsky.app/profile/johanvos.bsky.social/post/3lzzz7kiwhc26) to Devoxx Belgium next week: "*Excellent speakers/sessions, and great atmosphere. I'm more than happy to do interviews about [Java/JavaFX on Mobile](https://github.com/openjdk-mobile) so ping me if interested. (also happy to chat about quantum computing, javafx, science, cycling...)*"
* [Weekend hustle by **David**](https://bsky.app/profile/thejeed.bsky.social/post/3lzsrffhc622y): "*Testing JavaFX Direct3D 12 support 🤓 Since I've got the RTX 4090, JavaFX projects feel a bit brittle on Nvidia's Game Ready and Studio drivers. I'm not sure if it's really a JFX issue or the drivers don't work as expected...maybe I'll know more in a few hours.*"
* [**Dirk Lemmermann** shared screenshots](https://bsky.app/profile/dlemmermann.bsky.social/post/3m2lx4k2ul22v): "*Thanks to the new stage style EXTENDED in JavaFX 25 we now have full control over the user experience in JavaFX applications. Together with the new HeaderBar control we can place controls in the title bar area. Add AtlantaFX theming (including dark mode) and UIs are pro level.*"
* [**Gerrit Grunwald** is using JavaFX as an animation tool](https://bsky.app/profile/hansolo.eu/post/3m3jqu2uans27): "*Was looking for a tool to animate text to create some stuff for my PixelMug ... well the easiest thing to so was creating the animations with JavaFX , export as png's and create an animated gif...done 😁 I ❤️ it.*"
* [Tip by **Matt Coley**](https://bsky.app/profile/mattcoley.bsky.social/post/3m3wok2nync2m): "*If you're using the JavaFX 'Flowless' library for virtualized controls and are observing sluggish performance, do not add stylesheets to the Virtualized Node. Add them to the Scene. I observed an immediate 10x performance boost by doing this.* " A video demo is [available here](https://www.youtube.com/watch?v=l4PcbsDa-zU).
* [**Clément de Tastes** wrote a JavaFX app that uses Java's upcoming "Value Types"](https://bsky.app/profile/cdetastes.bsky.social/post/3m3wel4pj522w) from OpenJDK Project Valhalla: "*Combining the strength of encapsulation with the performance of primitives.* " Code is [on GitHub](https://github.com/CodeSimcoe/MandelbrotFx/tree/valhalla).

JFX Central {#h2-9-jfx-central}
-------------------------------

* New content on JFX Central:
  * Showcase: [Polarion Application Lifecycle Management (ALM)](https://www.jfx-central.com/showcases/polarion)
  * Showcase: [Recaf](https://www.jfx-central.com/showcases/recaf)
  * People: [Vlad Protsenko](https://www.jfx-central.com/people/v.protsenko)
  * People: [Matt Coley](https://www.jfx-central.com/people/m.coley)
  * Library: [TreeMapFX](https://www.jfx-central.com/libraries/treemapfx)
  * Tools: [Cljfx](https://www.jfx-central.com/tools/cljfx)
  * Video: [JavaFX In Action with Vlad Protsenko: Combining Clojure with JavaFX](https://www.jfx-central.com/videos/1JL6zdkM1GU)
  * Video: [JavaFX In Action with Matt Coley about Recaf and his JavaFX libraries](https://www.jfx-central.com/videos/6NIJ54h3iVY)
* The links of September got [published on Foojay](https://foojay.io/today/javafx-links-of-september-2025/).
