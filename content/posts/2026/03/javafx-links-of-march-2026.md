---
title: "JavaFX Links of March 2026"
slug: "javafx-links-of-march-2026"
date: "2026-03-31T07:49:54+00:00"
description: "Here are the JavaFX LinksOfTheMonth of March 2026. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there anything you want to - by Frank Delporte"
canonical: "https://webtechie.be/post/2026-03-27-javafx-links-of-march-2026/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

Here are the JavaFX LinksOfTheMonth of March 2026. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via links@jfx-central.com.

Core {#h2-0-core}
-----------------

* Java and JavaFX 26 got released!
  * JavaFX 26 requires JDK 24 or later.
  * You can find all important changes, removed and new features, and fixed issues in the [Release Notes for JavaFX 26](https://github.com/openjdk/jfx/blob/jfx26/doc-files/release-notes-26.md).
  * You can download JavaFX 26 [from the Gluon website](https://gluonhq.com/products/javafx/).
  * A [remarkable move by Oracle](https://www.techzine.eu/news/devops/139673/oracle-releases-java-26-ai-security-and-the-java-verified-portfolio/): "*Oracle is introducing the Java Verified Portfolio (JVP). This is a curated collection of enterprise-grade tools, frameworks, and libraries that Oracle commercially supports. ... It is worth noting that JavaFX, the Java GUI framework, is once again supported via JVP. According to Oracle, this is due to growing demand. Support will be available for all new Java versions and all LTS versions during the five-year Premium Support tier. Support for JDK 8 will be extended until March 2028.*"
  * [Blog post by Oracle](https://blogs.oracle.com/java/announcing-jvp): "*Announcing the Oracle Java Verified Portfolio including Helidon and reintroduction of JavaFX Commercial Support*."
  * [**Johan Vos** announced](https://mastodon.social/@johanvos/116245211784256953): "*Gluon continues to lead the OpenJFX project. Here is JavaFX 26.* " With a link to a [Gluon blog post about JavaFX 26](https://gluonhq.com/javafx-26-is-now-available/).
  * Video with the recording of the talk by Kevin Rusforth at JavaOne: [JavaFX 26 Today](https://inside.java/2026/03/25/javaone-javafx/): "*Building a compelling desktop app today requires features such as UI controls, charts, interactive media, web content, animation, CSS styling, 2D and 3D rendering, rich text, and property binding, with an easy-to-use programming paradigm that runs cross-platform. JavaFX is all this and more, delivering a rich graphical UI toolkit for building your applications and can also seamlessly interoperate with Swing. In this session you'll learn about the new and exciting features we've developed over the past couple of years, culminating with the release of JavaFX 26. You'll also get an update on RichTextArea. We'll show plenty of demos and sample code, and finish with a sneak peek at what's coming next.*"
  * [APIdia announced](https://www.linkedin.com/posts/java-javafx-javafx26-share-7442544660272627712-vVJf/): "*Adjacent to Java releases are JavaFX releases. Of course, API documentation of JavaFX 26 is similarly [available on APIdia](https://apidia.net/mvn/org.openjfx/javafx/26/).*"
* [Quality Outreach Heads-up - JavaFX 27: Metal Is Now the Default Rendering Pipeline on macOS](https://inside.java/2026/03/03/quality-heads-up/): "*This heads-up is part of the quality outreach sent to the projects involved. ... On macOS, the default JavaFX rendering pipeline has been switched to Metal since JavaFX 27 Early Access (EA) build 3. Metal provides improved performance and better compatibility on modern hardware.*"
* A blog by Gluon: [The Art of the Backport: Why JavaFX Security Doesn't "Just Happen"](https://gluonhq.com/the-art-of-the-backport-why-javafx-security-doesnt-just-happen/): "*In a perfect world, keeping a tech stack current is as simple as bumping a version number in a pom.xml or build.gradle file. You run a clean build, and suddenly, the latest fixes are integrated. But for those of us building mission-critical desktop and embedded applications, "just upgrading" to the latest major release isn't always feasible. When your application is built on a stable Long Term Support (LTS) foundation, you need that environment to remain predictable. You want the stability of your current version, but you absolutely need the security fixes that are discovered in the newer branches. This is where the "Art of the Backport" comes in, and it's a significant part of the heavy lifting we do at Gluon.*"
* Related to the announcement of the Oracle Java Verified Portfolio (JVP) and the reintroduction of commercial support for JavaFX, **Frank Delporte** wrote a blog: [The JavaFX Revival: Good News for the Community, Business as Usual for Azul](https://www.azul.com/blog/the-javafx-revival-good-news-for-the-community-business-as-usual-for-azul/).
* Already want to experiment with JavaFX 27? Check the [early-access builds](https://jdk.java.net/javafx27/).

Applications {#h2-1-applications}
---------------------------------

* LogoRRR announced [release 26.2.0](https://github.com/rladstaetter/LogoRRR/releases/tag/26.2.0). [**Robert Ladstätter** published a video showing the new features](https://www.youtube.com/watch?v=4INweBlGWoQ).
* [**Mapton** announced](https://bsky.app/profile/mapton.org/post/3mfysk3uxw22d): "_[Here is a new version of Mapton](https://github.com/trixon/mapton/releases)!, 'some kind of map application', built with Jva \& JavaFX on the Netbeans platform. Let's call this one the `MARKDOWN_HASHca329fd857db05db392388a1d1c2b7f9MARKDOWN`*HASH*. Enjoy!"
* [Message by **Jakob Jenkov**](https://www.linkedin.com/feed/update/urn:li:groupPost:10070360-7435938631854419968/) : "*I have started the process of building up the Polymorph Player in Java 25 and JavaFX 25. The code will now be located in the [Polymorph mono-repo](https://github.com/jjenkov/polymorph). The first bits are already there, but the player app does not do anything yet, except starting up 😊 But from now on, you can always clone or pull this repo to see just exactly how much is working, officially. This is a project where we can allow ourselves to question the entire current status quo of tech. Some should be kept - but other parts could probably be replaced with better formats / models / architectures. Let's experiment to see what works, and what doesn't.*"
* [**Frederick Salazar** released OllamaFX v0.5.0](https://github.com/fredericksalazar/OllamaFX/releases/tag/v0.5.0). "*OllamaFX is a modern, native desktop client for Ollama, built with JavaFX. It provides a beautiful, user-friendly interface to manage your local LLMs and chat with them, featuring a sleek GNOME/Adwaita-inspired design.*"
* **Robert von Burg** shared the [sources of LumineLog](https://github.com/eitch/LumineLog): "*A modern, cross-platform log viewer application built with JavaFX. It provides a real-time 'tail -f' experience with powerful highlighting and multi-file support.* "
  * [Release of LumineLog 0.3.0](https://mstdn.gsi.li/@eitch/116285194315067384): "_A modern, cross-platform log viewer application built with JavaFX. It provides a real-time `MARKDOWN_HASHc21d50ccb3e2b0daf559d6015794f6a7MARKDOWN`*HASH* experience with powerful highlighting and multi-file support. As always, feedback is welcome. Feel free to [raise a ticket](https://github.com/eitch/LumineLog)."
* [Message by **Robert Ladstätter**](https://bsky.app/profile/rladstaetter.bsky.social/post/3mhaoaaiw6s2k): "*Just released CameraApp on Windows and Linux appstores! [Check out this project](https://github.com/rladstaetter/CameraApp) which resurrects my old passion of doing work with JavaCV and JavaFX. This project can be used as a starting point for experiments with OpenCV and Java.*"

Components, Libraries, Tools {#h2-2-components-libraries-tools}
---------------------------------------------------------------

* **Frank Delporte** announced the first release of Lottie4J, a new Java(FX) library: "*With this library, you can load and parse LottieFiles animations as Java objects, and integrate them as a JavaFX animation component in your application. Watch [this video for more info](https://www.youtube.com/watch?v=6t1O7APENIo) or [read this blog](https://webtechie.be/post/2026-03-03-introducing-lottie4j/).* "
  * Followed by release V1.1.0 of [Lottie4J](https://github.com/lottie4j/lottie4j), a JavaFX player for LottieFiles animations. "*This release includes improved rendering and additional debugging tools. And of course, it also uses the new and animated Lottie4J logo for testing 😉* " All info and a video demonstration are available [on the website in the release notes](https://lottie4j.com/releases/).
  * And another release: "*Version 1.2.0 of [Lottie4J](https://lottie4j.com/index.html) is out, and it's again a big release! The headline feature is support for the dotLottie zip-container format, but that's just the start. This release also brings marker-based playback, cropping, adaptive rendering, significant performance improvements, and a lot of core model fixes driven by testing more complex real-world animations. Detailed info and a video showing all new features are [explained in this blog post](https://webtechie.be/post/2026-03-20-release-1.2.0-of-lottie4j/).*"
* A [good read by **Liu Tiger**](https://dev.to/liu_tiger_ef0f0505e13c8be/javafx-ui-automation-challenges-existing-tools-and-real-world-event-handling-problems-57k): "*JavaFX UI Automation: Challenges, Existing Tools, and Real-World Event Handling Problems. Automation testing has become a fundamental component of modern software engineering. In web development, automation ecosystems such as Selenium, Playwright, and Cypress are mature and widely adopted. However, the situation is very different for Java desktop applications, particularly those built using JavaFX.*"
* [**Pavel Castornîi**](https://www.linkedin.com/in/pavelcastornii/) is preparing a new release of [TabShell](https://github.com/techsenger/tabshell), "*a platform for building tab-based applications in JavaFX, where an application is structured as a tree of MVP components, each of which has its own lifecycle, history, etc. The platform provides abstract classes for creating the main types of components: tab, area, page, dialog, and popup, as well as containers for them.*" Try it out and let him know if there are any remarks...
* [**Gerrit Grunwald** created svgconverter](https://github.com/HanSolo/svgconverter): "*An SVG to JavaFX converter that can handle nearly everything except animations and masking. You can load a svg file and render it either to the JavaFX SceneGraph using nodes or to the JavaFX Canvas. It took some time to get the Canvas thing working because it is missing some things but now it works kind of ok.*"
* **Dirk Lemmermann** announced: "*I created a new [website for GemsFX](https://dlsc-software-consulting-gmbh.github.io/GemsFX/) that will give you an idea which controls are available in this library.*"
* [**Lee Wyatt** shared the new library CarouselFX](https://bsky.app/profile/leewyatt.bsky.social/post/3mhvd52wuks2u): "*A JavaFX carousel / slideshow component with 70+ built-in transition effects.* " [Demo on YouTube](https://www.youtube.com/watch?v=oFeuHs_HFwU) and [sources on GitHub](https://github.com/dlsc-software-consulting-gmbh/CarouselFX).

Podcasts, Videos, Books {#h2-3-podcasts-videos-books}
-----------------------------------------------------

* [GNUBSD404 Long N162 PacMan XXL (JavaFX Game) (Linux/FreeBSD)](https://www.youtube.com/watch?v=5T5DI23rJxE): "*This is another example of a "Quality" game on Linux made by some developers. THANKS ! to this developers is that Linux/FreeBSD gaming (and off course thanks to Valve and Steam) is alive!. The game is available on Linux andFreeBSD may work with OpenJDK or may require Linuxuator.*"
* New video by **Helal Anwar** : [Student management app (Part 6)](https://www.youtube.com/watch?v=ZHnSynN5R3Y)
* **Florian Enner** published a video demonstrating [HebiCharts](https://www.youtube.com/live/B5GT9XAcqB8): "*A 2D and 3D plotting library built in JavaFX with ChartFX, compiled as GraalVM native-image, and accessible from Python / C++ / MATLAB via idiomatic interface over a C ABI.*"

Miscellaneous {#h2-4-miscellaneous}
-----------------------------------

* [**Robert Ladstätter** is experimenting with JavaFX 26 and shared a video](https://bsky.app/profile/rladstaetter.bsky.social/post/3mg3f6kort22e): "*Vibe coding an editable ToolBar for #JavaFX supporting drag'n drop for the editable Search Term Toolbar Feature. It is amazing how easy it is to implement such things with JavaFX. Looking forward to JavaFX26 which will be released soon.*"
* [Interesting read by **SikorSky**](https://dev.to/sikorsky43/building-a-tcp-group-chat-app-with-javafx-and-maven-1e46): "*Building a Real-Time Group Chat with Java TCP Sockets and JavaFX. We recently implemented a mini project where the objective was to build a real-time group chat application using Java TCP sockets and JavaFX. This article summarizes the architecture, design decisions, and lessons learned during development.*"
* [**Dirk Lemmermann**](https://bsky.app/profile/dlemmermann.bsky.social/post/3mgflg35sgc2u) "*did some more evaluation of GitHub Copilot CLI today and built a nice launcher app for my GemsFX open source project. The productivity I get out of this surpassed all my expectations.*"
* [Post by Gluon](https://gluonhq.com/why-spend-a-week-on-a-bug-that-we-can-fix-in-an-hour/): "*We've all been there. You're working on a JavaFX application, and you hit that wall. Maybe it's a strange rendering glitch on a specific OS, a memory leak you can't pin down, or a performance bottleneck that only appears in production. You search the forums. You check Stack Overflow. You spend days tweaking code, hoping for a breakthrough. There is a faster way! To make it easier for teams to experience the value of expert support, we are introducing a one-time JavaFX Quick-Fix Package.*"

JFX Central {#h2-5-jfx-central}
-------------------------------

* New content on JFX Central:
  * Library: [Lottie4J](https://www.jfx-central.com/libraries/lottie4j)
  * Libraries: [TabShell](https://www.jfx-central.com/libraries/tabshell) got updated.
* Internal improvements in JFX Central with latest release of [JPro](https://www.jpro.one/).
* [Screenshots shared by **Dirk Lemmermann**](https://bsky.app/profile/dlemmermann.bsky.social/post/3mgs3b37pjc2p): "*Thanks to the efforts of several open source developers we can now display syntax-highlighted code in markdown files via JPro Markdown by [**Florian Kirmaier**](https://bsky.app/profile/floriankirmaier.bsky.social). Available hopefully by the end of the week.*"
* [**Dirk Lemmermann** shared another screenshot](https://bsky.app/profile/dlemmermann.bsky.social/post/3mhxunuetrk2h): "*We finally moved to JavaFX 25 for JFX Central and this now allows us to use StageStyle.EXTENDED. Sounds like a minor thing but allows apps to look much more native than before. No more custom resizing borders and behaviour.*"
* The JavaFX Links Of The Week of February got bundled and [published on Foojay.io](https://foojay.io/today/javafx-links-of-february-2026/).
