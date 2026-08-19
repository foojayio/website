---
title: "JavaFX Links of January 2025"
date: "2025-02-01T16:20:33+00:00"
lastmod: "2025-02-01T16:20:34+00:00"
description: "Here is the first overview of the JavaFX LinksOfTheMonth for 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there…"
canonical: "https://webtechie.be/post/2025-01-31-javafx-links-of-january-2025/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-december-2024"
  - "javafx-links-of-november-2024"
  - "javafx-links-of-october-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

**Here is the first overview of the JavaFX LinksOfTheMonth for 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links).**

Did we miss anything? Is there anything you want to have included in one of the next overviews?

Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* CPU (security) releases for OpenJFX are available. Read more [on the Gluon website](https://gluonhq.com/announcing-the-availability-of-javafx-23-0-2-21-0-6-lts-and-17-0-14-lts/). You can [get the SDKS here](https://gluonhq.com/products/javafx/) or use the Maven artifacts from Maven Central.
* [An early access build of the JavaFX from the "metal" branch of the openjdk/jfx-sandbox repository is available](https://jdk.java.net/javafxmetal/), implementing the new Metal graphics renderning pipeline for macOS: "*The goal of this release is to solicit feedback as we work toward integrating this functionality into JavaFX. Binaries are provided as a convenience so that users do not need to build from the source code. Warning: This build is based on an incomplete version of JavaFX 25.*"
* Still running JavaFX 8 apps? As Oracle will end support for JavaFX on Java 8 in March 2025, [Azul has put up a page to inform you about your options](https://www.azul.com/javafx-java8-oracle-end-of-support/).
* [**Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3lgxb47ksvc2q): "*What if... we could build a JDK including JavaFX, using the JDK build system? As a POC, I added 3 javafx modules to my fork of openjdk/jdk and built them on Linux. The openjdk/jdk build system is excellent, and it required only minimal changes to do this. See [GitHub](https://github.com/johanvos/jdk/tree/openjfx).*"

## Applications

* [JabRef released the first 6.0 alpha version](https://blog.jabref.org/2024/12/23/JabRef6-0-alpha/): "*Packed with the cool features from GSOC and many other fixes: AI features, CSL Styles in LibreOffice, New search.*"
* [**Maksymilian** announced the biggest update yet of his project Picture Comparer FX](https://dev.to/maksik997/major-release-total-overhaul-javafx-renaissance-51m): "*This application is designed to help you automatically find and manage redundant images in your collection. Whether you're dealing with duplicate photos or just want a more organized image library, this tool simplifies the process.*"
* [**Brian Schlining** shared a link to vars-annotation](https://bsky.app/profile/schlining.bsky.social/post/3lfjvdzhsnk2s): "*Video Annotation Application for MBARI's Media Management (M3) software stack.*"
* [**Daryl** shared a screenshot](https://bsky.app/profile/zolbatar.bsky.social/post/3lgkt6i7bns2y): "*I haven't posted an update in a while, so a sneaky peek at the latest version of Daric in development. It might look a little familiar... Why invent, when you can re-invent...*" Daric is a modern BASIC dialect. It has a clean syntax, strong data structures, good library support and compiles to fast JVM bytecode.

## Games

* **Almas Baim** is continuing his experiments with shaders in FXGL and shared a lot of the results. Here are just a few, check his Twitter/X timeline for more...
  * [First public demo](https://x.com/AlmasBaim/status/1871485496737821077): "*If you are on Windows and feel bored, give this demo a try and play with different shaders. If anything fails / crashes / doesn't work, please report an issue on the main FXGL repo.*"
  * "*There are some really cool shaders available online. [Here's one from **shadertoy**](https://shadertoy.com/view/XfyXRV). The demo shows a property bound to a shader variable, hence the ability to control the rotation speed. The glass effect and reflections are just 🤩*"
  * "*One of the use cases I envisaged for a [combined JavaFX scene graph and OpenGL context](https://x.com/AlmasBaim/status/1872280066786476253). The majority of the game is rendered by JavaFX / FXGL, but some complex visual effects are done with OpenGL shaders. From performance point of view, this is also sensible.*"
* A [video on LinkedIn by **Almas Baim**](https://www.linkedin.com/posts/almasb_hand-tracking-via-mediapipe-in-fxgl-is-getting-activity-7288195001124098048-wgAc/) shows how he can move objects in an application with motion tracking: "*Hand tracking via MediaPipe in FXGL is getting an upgrade in the next version. The demo consumes raw tracking data with no fine-tuning. So improved user experience results are certainly possible.*"
* [**Almas Baim** shared a video](https://x.com/AlmasBaim/status/1882864344888922298): "*Just a quick proof-of-concept demo running WebGL inside FXGL. Given native GLSL support is on its way, WebGL support is unlikely to reach production.*"
* A new Steam game: "*[SuperCowBoy](https://store.steampowered.com/app/3436980/) is a 2D side-scrolling game developed using JavaFX. There are three professions in the game. The game is divided into 8 levels with gradually increasing difficulty, and the total game time is 20 hours. The game map is full scene, with a bright pixel art style high frame rate display.*"
* [**Mark J. Koch** shared a video](https://bsky.app/profile/markjkoch.bsky.social/post/3lghw3v5rb22l): "*Scene working! Going to call this a wrap for now. Check out that cameo! Lots of future work to do here but it's roughed out. Will pick another scene to work on over the weekend.*"

## Components, Libraries, Tools

* [Version 4.1.17 of Openglfx got released](https://github.com/husker-dev/openglfx/tags): "*This library adds a new element to the JavaFX for rendering OpenGL graphics using LWJGL, JOGL, LWJGL2 or LibGDX. It is optimized for each platform and includes some auxiliary functions for working with OpenGL from JavaFX.*"
* [**Hugo Quinn** released v1.0.0 of FxPopup](https://github.com/HugoQuinn2/fxpopup/releases/tag/v1.0.0): "*JavaFX library that simplifies the creation of automatic forms and popup messages with minimal effort. With just a single line of code, developers can generate dynamic forms or display messages, while maintaining the flexibility to use custom views for both functionalities.*"
* [**Steve Hannah** released jDeploy 4.0.34](https://x.com/shannah78/status/1744059640231735638): "*It fixes 'offline' support for apps. If you have an app deployed with [jDeploy](https://jdeploy.com), all you need to do is re-install your app for it to work fully offline.*"

## Podcasts, Videos, Books

* Part 3 of the overview of the "JavaFX In Action" interviews by **Frank Delporte** got [published on Foojay](https://foojay.io/today/video-series-javafx-in-action-part-3/) with:
  * **Özkan Pakdil**: Swaggerific, an open-source Postman alternative written in JavaFX
  * **Clément de Tastes**: QuarkusFX, combining the strengths of Quarkus and JavaFX
  * **Almas Baim**: FXGL, a multipurpose game library for JavaFX
  * **Steve Hannah**: jDeploy, to distribute your Java app as a native bundle
  * **Jago de Vreede**: SDKman UI, a user interface on top of SDKMAN for all platforms
* [**Roberto Marquez** published a video (with a link to the sources)](https://www.youtube.com/watch?v=k4h7EhCuoW8) "*demonstrating a JavaFX application that communicates with a temperature and humidity sensor and displays the values on screen. It uses software from **Frank Delporte** (LED display) and jSerialComm, and hardware from Adafruit.*"
* Not from last week, but just discovered these now: [**Josh Long** and **Max Rydahl Andersen** talk about JBang](https://www.youtube.com/watch?v=fBvcUJbCMXo) in a live stream and show how you can use it to run JavaFX code very easily.
* An [interview on LinkedIn with **Stephen Chin**](https://www.linkedin.com/posts/apress_featuredfriday-javafx-openjfx-activity-7283490269948178434-9jvg/), coauthor (along with **James Weaver** and **Johan Vos** ) of the Third Edition of "*The Definitive Guide to Modern Java Clients with JavaFX: Cross-Platform Mobile and Cloud Development Updated for JavaFX 21 and 23*".
* The [first "JavaFX In Action" interview of 2025 is live! With Hydraulic Software, **Mike Hearn**](https://www.youtube.com/watch?v=CuI7-PllJZQ) solves a problem many developers struggle with: efficiently distributing your application and ensuring the users get the latest version. With Conveyor, he created a tool to easily create an installer for JavaFX, Electron, and Flutter apps! Check the [blog for more info](https://webtechie.be/post/2025-01-23-jfxinaction-mike-hearn-conveyor/).

## Conferences

* The schedule has been published of JavaOne 2025 "*Where Java developers come to skill up* ", March 18--20, 2025, USA. It contains three JavaFX sessions:
  * [JavaFX 24 and Beyond](https://reg.rf.oracle.com/flow/oracle/javaone25/catalog/page/catalog/session/1734125339212001XwBE) by **Kevin Rushfort**
  * [JavaFX in the web](https://reg.rf.oracle.com/flow/oracle/javaone25/catalog/page/catalog/session/1728506254826001MEHs) by **Karl Berger** and **Florian Kirmaier**
  * [Building a Multiplatform SDKMAN in JavaFX](https://reg.rf.oracle.com/flow/oracle/javaone25/catalog/page/catalog/session/1728227349663001Wod9) by **Jago de Vreede**
* The live streams from the [JChampions Conference](https://jchampionsconf.com/schedule.html) (January 23, 24, 27, and 28) are available on YouTube:
  * "[JTaccuino - A better Jupyter Experience for Java Developers"](https://www.youtube.com/watch?v=R1gHQtBXfYk), presented by **Sven Reimers** : "*This session shows a new tool for interactive computing for Java developers. It provides a seamless interactive experience to write Java code, interact with the results using a full graphical interface and all this without the tedious process of compiling and running every time you make a small change achieving a lightweight rapid development cycle.*"
  * "[Looking at Music, an experiment with Kotlin, JavaFX, MIDI, and Virtual Threads](https://www.youtube.com/watch?v=UW6fDQt-8BI)", presented by **Vik and Frank Delporte**.

## Tutorials

* [**Crystal Furman Sheldon** published a challenge for students](https://www.linkedin.com/pulse/twas-week-before-breakfun-javafx-project-end-year-furman-sheldon-fhvqc/): "*'Twas the week before winter break when all through the school, the CS teachers wondered what interesting things they could do. The projects and exams were all set for the year, in the hopes of something fun to finish the year. JavaFX to the rescue a fun creative tool. Allow students to explore and learn something new...*"
* [**polypragmatist**](https://bsky.app/profile/polypragmatist.bsky.social/post/3lef6zcqez22t): "*I came up with an interesting approach to create 'Layout-centric' styleable properties in JavaFX. Instead of creating a single-use named class, a small amount of setup makes it easy to create StyleableProperties on the fly for your layouts.*"
* [**Almas Baim** published a video](https://www.youtube.com/watch?v=VLMHkyRXjxI): "*FXGL 25 Tutorial: Shaders. We go through a basic workflow related to using shaders in FXGL. As of December 2024, this only works on WIndows. Once the full release is ready, it will work on Mac/Linux.*"
* [Kotlin for Desktop Applications: Using JFX and Swing](https://codezup.com/kotlin-desktop-applications-jfx-swing/): "*In this comprehensive tutorial, we will explore the core concepts, implementation guide, and best practices for building desktop applications using Kotlin, JFX, and Swing.*"
* [**Webdox** published a YouTube tutorial](https://www.youtube.com/watch?v=ew1k9yr-LHk): "*JavaFX GUI Tutorial for Beginners. In this video, you'll learn how to set up a basic JavaFX project, create a simple user interface, and understand how to use JavaFX components like Buttons, Labels, and Scenes. Whether you're new to Java or just getting started with JavaFX, this guide will help you build a solid foundation.*"

## Miscellaneous

* Check the [Twitter/X timeline of **Divyanshu Yadav**](https://x.com/DVyadav2307/status/1873136518807863599) for more like this: "*I've been using charts and graphs in JavaFX as a fun way to visualize how data manipulation algorithms work esp. sorting algorithms. Check out this GIF to see how the BubbleSort algo works.*"
* Some JavaFX love to start 2025:
  * [**Rachid Laborantin**](https://x.com/RLaborantin/status/1874026919014101011): "*And I thought Java was old-fashioned, but it continues to work wonders with JavaFx. I've been using Swing for a long time and I think it's time to go to JavaFx.*"
  * [**Gerrit Grunwald**](https://bsky.app/profile/hansolo.eu/post/3ldt6ifoknk2n): "*Can't say it often enough but JavaFX is sooooo good for creating desktop tools... simply love it 🫶🏻*"
* [**Johan Vos** needs your feedback](https://bsky.app/profile/johanvos.bsky.social/post/3ldsmmxrxrc2a)= "*I'm working on StrangeFX, and looking for input from JavaFX developers. What would be your preferred root layout container for this concept? StrangeFX currently use a Group for this, but I am thinking about changing this, to improve embedding capabilities in e.g. JTaccuino.*"
* Article by **Frank Delporte** on TheServerSide: [Swing vs. JavaFX: Compare Java GUI frameworks](https://www.theserverside.com/tip/Swing-vs-JavaFX-Compare-Java-GUI-frameworks): "*Yes, developers can use Java to develop graphical user interfaces (GUIs). In fact, Java's ability to create cross-platform desktop apps was one of the language's biggest selling features when it was released in 1995. However, as Java evolved and changed throughout the years, so did the desktop-rendering toolkits it supported, with the original Abstract Window Toolkit (AWT) giving way to Swing components which has now given way to JavaFX, the preferred choice for modern development.*"
* [**Peter Pilgrim** on Bluesky](https://bsky.app/profile/peterpilgrim.bsky.social/post/3lfwtvhci722y): "***Jasper Potts** when he worked at Oracle had a lot of JavaOne demos including [this JavaFX 3D container ports](https://www.jasperpotts.com/project/3d-container-port-in-javafx/). Look at the H/W available today MacMini M4 then think of the possibilities, especially in Augmented Reality.*"
* [Top Java Chart Libraries for Data Visualization in 2025](https://www.fromdev.com/2025/01/top-java-chart-libraries-for-data-visualization-in-2025.html): "*In this article, we explore some of the best free and open-source Java chart libraries available. Each of these libraries offers unique features that cater to different requirements, making them ideal for a wide range of applications.*"
* [**Max Rydahl Andersen** published a blog post](https://quarkus.io/blog/introducing-mcp-servers/) "*about my side-project of implementing some sample MCP servers in Java using Quarkus and JBang. Connect to Any JDBC datasource, access the filesystem and draw art on a JavaFX canvas. Lets grow the list!*"
* [**Caroline Scharf** wrote a long post](https://blog.tomsawyer.com/node-graph-visualization): "*Exploring Graph Node Visualization Techniques in JavaFX and Python.*"

## JFX Central

* New content on JFX Central:
  * Showcase: [Barcodify, an application for barcode generation](https://www.jfx-central.com/showcases/barcodify)
  * Library: [FxPopup](https://www.jfx-central.com/libraries/fxpopup)
  * People: [Hugo Quinn](https://www.jfx-central.com/people/h.quinn)
  * Video: [JavaFX In Action with Mike Hearn about Conveyor](https://www.jfx-central.com/videos/CuI7-PllJZQ)
* The overview of the links of the week of December got [published on Foojay](https://foojay.io/today/javafx-links-of-december-2024/).
