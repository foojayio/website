---
title: "JavaFX Links of January 2024"
date: "2024-01-31T08:55:53+00:00"
lastmod: "2024-02-01T13:27:44+00:00"
description: "This is the first JavaFX LinksOfTheMonth review for 2024, an overview of the LinksOfTheWeek that got published on jfx-central.com during January."
canonical: "https://webtechie.be/post/2024-01-26-javafx-links-of-january-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-december-2023"
  - "javafx-links-of-november-2023"
  - "javafx-links-of-october-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

This is the first JavaFX LinksOfTheMonth review for 2024, an overview of the LinksOfTheWeek that got published on [jfx-central.com](https://www.jfx-central.com/) during January.

## Core

* [JavaFX 21.0.2 (January 2024) is available on the Gluon website](https://gluonhq.com/products/javafx/).

## Applications

* **Robert Ladstätter** added ZIP file support to LogoRRR: "[No more unzipping before analysing your latest bugs from ops](https://twitter.com/rladstaetter/status/1741795036826566704)!"
* [A Christmas present by **Carl Dea**](https://twitter.com/carldea/status/1738937742984126795): "I wanted to give you a sneak peek at a JavaFX based clinical interface terminology system (knowledge base)."
* **Heshan Kariyawasam** "[made something random today night just for fun](https://www.linkedin.com/posts/heshanthenura_java-javafx-music-activity-7148745375539970048-aKXD). For me it's so satisfying to watch..."
* **Christopher Schnick** is working on a [standalone application running on NixOS](https://twitter.com/crschnick/status/1743296483544150114) with Deepin desktop environment: "The popup windows don't support transparency and the tray icon is cut off, but other than that it is working fine."
* **Robert Ladstätter** published a [new release of LogoRRR](https://twitter.com/rladstaetter/status/1745586922515288416): "This update brings enhanced zip file support and animated clipboard actions, alongside various bug fixes." [Here you can find the release notes](https://github.com/rladstaetter/LogoRRR/releases/tag/24.2.0).
* [**Carl Dea** bumped versions for ScenicView](https://www.linkedin.com/posts/carldea_java-javafx-activity-7153563217930125312-llns/): "Yay, my [PR was accepted](https://github.com/JonathanGiles/scenic-view) for the excellent JavaFX debugging tool ScenicView. It now works with JDK 11+ and JavaFX 21.0.1. I believe the release should be out soon, but if you want to the latest binaries just clone and build the project locally 🙂."
* [**OrangoMango** is having fun with maths](https://twitter.com/orango_mango/status/1746593368463876123): "Now the application automatically inverts the transformation equations and applies them to any function or quadratic equation for y."
* [Video with a walk-through of LogoRRR](https://www.youtube.com/watch?v=5ogC95PX0Ag): "While exploring the log files is an essential part of troubleshooting, sometimes you may not want to go so deep. In fact, when you get an error, you may simply want to see what happened as fast as possible and in a clear way. LogoRRR is a tool that does precisely that and provides you with a quick way to filter out critical events or other points of interest."
* Some nice small demo applications again by [**OrangoMango**](https://twitter.com/orango_mango):
  * [Falling sand video](https://www.youtube.com/shorts/2T3UNo5EVXg)
  * [Clock drawn on the Canvas, running in the browser](https://orangomango.github.io/Clock/) with about [100 lines of code](https://github.com/OrangoMango/Clock/blob/main/src/main/java/com/orangomango/clock/Clock.java).
* **Patrik Karlström** is "[in the middle of a major facelift of mapollage](https://twitter.com/PatrikKarlstrom/status/1750568870241280298), a KML generator. This time combining JavaFX with NetBeans Java Platform. It's great as usual, and now I'm using the "output window" with all it's goodies like folding, coloring \& links. It's all there, for free!"

## Games

* **OrangoMango** made a perfect Snake AI, [check the video](https://twitter.com/orango_mango/status/1741480547627507809)! You can find the game (and play it) with a link to the sources on [orangomango.itch.io/snake](https://orangomango.itch.io/snake).
* **Almas Baim** published FXGL game engine 21 with:
  * Java and JavaFX 21
  * Numerous dialogue editor improvements
  * Initial hex support for Tiled
  * Extra Image processing API
  * Video cutscenes
  * Full [changelog on GitHub](https://github.com/AlmasB/FXGL/releases/tag/21)
* Almas also [found another awesome project](https://twitter.com/AlmasBaim/status/1741128302050378091) built with JavaFX and FXGL: "Royal Demons". From the readme: "... won the Best Project Competition out of a total of 114 teams at Georgia Tech CS 2340 Objects and Design course during Spring 2021."
  * And [he found out](https://twitter.com/AlmasBaim/status/1749742997145583662) that: "team JavaFX is now right in the middle of JVM game projects on the 1st page of GitHub search with 3960 stars! Since FXGL is now 49.4% #Kotlin and 49.1% #Java, I had to cheat and include both `language: Java, language: Kotlin` in [the search](https://github.com/search?o=desc&q=java+game+language%3AJava+language%3AKotlin&s=stars&type=repositories)."
* [Max Rydahl Andersen](https://twitter.com/maxandersen/status/1750504675630022941) wants to collaborate with Almas to make FXGL and game development easier with JBang: "I'm happy to help to make [FXGL example games](https://github.com/almasb/FXGLGames) "jbang friendly" ... there are lots of ways we can do that."
* New [video update by **London Softworks**](https://twitter.com/LondonSoftworks/status/1742674131299000615): "Trying my best to work every day, so many exciting things to come in the near future! For now, enjoy a small demo of a few components (Texture, Mesh, Transformation) being fully implemented!"
* [Beta preview by **Hlan Htet Kyaw** of Brain Buster](https://twitter.com/HlanHtetKyaw1/status/1742194376820814247): "Please test and provide feedback. Any help would be appreciated."

## Components, Libraries, Tools

* [**Steve Hannah**](https://twitter.com/shannah78) published a new [IntelliJ Plugin for jDeploy](https://jdeploy.substack.com/p/new-intellij-plugin-for-jdeploy) to create a new desktop app with automated releases on GitHub in under 2 minutes: "In addition to creating the project locally, the wizard will create a new GitHub repository, that is set up to generate new releases on every commit. Within 30 seconds of creating your project, you should be able to download and install the app from GitHub releases."
  * He also shared a GitHub project as a showcase for jDeploy: [Sample FXGL project deployed with jDeploy via GitHub releases](https://github.com/shannah/fxgl-test8).
* **Michael Gasche** shared a [showcase of a rapid development framework](https://products.autumo.ch/modules/overview#at_ui) on the Foojay Slack with some out-of-the-box functionalities (installer, licensing/registering, app-config, update-checks, some further dialogs) for Java FX UI apps: "It has default views which can be used with a one-liner to show an HTML page, license dialog, or other content that can be switched off per platform, e.g. because the Windows installer already has one. The Registration module in the demo is a "dummy" as it is quite big and advanced and possibly involves a server."
* [**Laurent Bourgès**](https://twitter.com/laurent_bourges) reached the milestone of +125,000 downloads of his [Marlin Renderer](https://github.com/bourgesl/marlin-renderer): "An open source (GPL2 + CP) Java2D RenderingEngine optimized for performance (improved memory usage (\~ no GC) and footprint, better multi-threading) and better visual quality based on OpenJDK's Pisces implementation." And he adds: "[Enjoy FOSS \& see you at FOSDEM24](https://mastodon.social/@laurent_bourges/111776582923358418)".
* [**Carl Dea** shared a video](https://twitter.com/carldea/status/1746712384725483585): "JavaFX with the native background blurring effect on the MacOS is now working! Thanks to [**Steve Hannah**](https://twitter.com/shannah78) for the tremendous help in understanding the native side (MacOS). [Demo here](https://github.com/carldea/windowblur). Eventually will end up in [**Pedro Duque Vieira**](https://twitter.com/P_Duke)'s FXThemes project. I also want to give a shout-out to the amazing software engineer [**Martin Fox**](https://github.com/beldenfox) (a JavaFX contributor)."
* [**Pedro Duque Vieira** is asking for help testing out FXThemes](https://twitter.com/P_Duke/status/1750632079430189283): "If you have a machine running Windows 10 can you run the fxthemes-samples subproject and tell me if the window that shows up has a background blur? The project is using Java17 for now..."

## Podcast, Video, Books

* The [recording of the live stream](https://www.youtube.com/watch?v=IufaUwDsHUA) by **Frank Delporte** and **Almas Baim** provides a code walk-through of this blog post: [A JavaFX Game Application in a Single Java File with JBang and FXGL](https://webtechie.be/post/2023-12-14-jbang-fxgl/).
* New book by **Peter Späth** : [Frontend Development with JavaFX and Kotlin: Build State-of-the-Art Kotlin GUI Applications](https://www.amazon.nl/Frontend-Development-JavaFX-Kotlin-State/dp/1484297164).
* Nice video by **Akif (Sorest) Karaca** to help you [understand different sorting algorithms](https://www.youtube.com/watch?v=HmoRQkmyPnk) (selection, insertion, quick, bubble, heap, shell) by visualizing them with JavaFX (in Turkish).
* [**Kinsley Kajiva**](https://www.linkedin.com/in/kinsley-kajiva/) published a video: "[Ripple WebRTC - JavaFX G Streamer Demo](https://www.youtube.com/watch?v=PYv9Pp-Wu3c)."
* [Airhacks #278: "Java at Azul: The Interesting Features"](https://airhacks.fm/#episode_278): [Adam Bien](https://twitter.com/AdamBien) talks with [Gerrit Grunwald](https://twitter.com/hansolo_) about Java desktop applications with Swing and JavaFX + many other topics.

## Tutorials

* [**Tobias Briones**](https://twitter.com/tobiasbriones_) added [support for rounded triangles](https://blog.mathsoftware.engineer/drawing-a-rounded-triangle-via-quadratic-curves-2023-12-22) to the Canvas Play JavaFX project using quadratic Bézier curves.
* [Examples of Transformations in JavaFX](https://examples.javacodegeeks.com/examples-of-transformations-in-javafx/) by [**Omozegie Aziegbe**](https://twitter.com/OAziegbe).
* [**Tech Buddy**](https://twitter.com/techbuddy_dev): "[Building Three-Layered JavaFX Apps with TornadoFX and Kotlin](https://techbuddy.dev/kotlin-tornadofx-three-layered-javafx)"

## Miscellaneous

* The **JFX Adopters Meeting 2024** takes place on the 6th of March at ZEISS in Munich Germany. It's a user meeting about JavaFX technology and the [registration is open](https://zeiss.com/meditec/en/news-events/events/jfx-adopters-meeting.html#register). You can still apply to be a speaker at this event!
* **Heshan Kariyawasam** [fell in love with JavaFX canvas](https://www.linkedin.com/posts/heshanthenura_javafx-java-night-activity-7149071331672616960-gXKa/): "So I'm going to simulate night sky. I'll try to add Constellations and Meteors so it will be more realistic."
  * And he shared this [list of coding challenges](https://www.youtube.com/playlist?list=PLRqwX-V7Uu6ZiZxtDDRCi6uhfTH4FilpH) that he would like to solve with JavaFX. Keep scrolling, the list is very long 😉
  * He continues his 3D experiments... [Texture And Light on GitHub](https://github.com/heshanthenura/TextureAndLight) is only one of those. Follow him on [Twitter/X](https://twitter.com/Heshantk) or [LinkedIn](https://www.linkedin.com/in/heshanthenura/recent-activity/all/) for videos and links to sources...
* Want to combine Quarkus and JavaFX? Seems work is ongoing:
  * A lot of [new comments in a Quarkus ticket](https://github.com/quarkusio/quarkus/issues/9313) started in 2020.
  * With a link to [quarkus-fx-extension](https://github.com/CodeSimcoe/quarkus-fx-extension).
* [**Laurent Bourgès** realized he needs to celebrate](https://mastodon.social/@laurent_bourges/111782539726528107) "10 years as openjdk committer, 10 years since first Marlin-renderer 0.3: 2014.01, and a new Marlin 0.9.4.8 release: 2024.01. I want to launch the Marlin-renderer Drawing Contest #MDC btw 01.20 and 02.03: *Please draw me a nice thing with the marlin-renderer* in 3 categories. Submissions will be shared as png, svg, pdf files under author's declared license. What do you think?"
  * Geographic maps (gis)
  * Illustration / plans / complex charts
  * Computer arts
* A [tip from **Heshan Kariyawas**](https://www.linkedin.com/posts/heshanthenura_java-javafx-debugging-activity-7156150466643468289-GcHG/): "Before the production release of a JavaFX app on Windows using JPackage, enable the console with the `--win-console` flag for easy debugging. However, after the production release, no debuggers are available. This tip proved very useful for me today."

## JFX Central

* JFX Central is on the cover page of Javamagazin 2.2024, and the article by **Frank Delporte** is eight (8!) pages. A complete walkthrough of the website and interviews with **Dirk Lemmermann** , **Li Wang Yang** , **Florian Kirmaier** , and **Mike Hearn** . The [full article is also available here](https://entwickler.de/java/jfx-central-javafx) (with login).
* The summary of our [Links Of December are shared on Foojay](https://foojay.io/today/javafx-links-of-december-2023/).
