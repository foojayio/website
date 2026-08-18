---
title: "JavaFX Links of June 2025"
slug: "javafx-links-of-june-2025"
date: "2025-06-29T08:16:51+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of June 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there anything - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-06-27-javafx-links-of-june-2025/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-may-2025"
  - "javafx-links-of-april-2025"
  - "javafx-links-of-march-2025"
  - "javafx-links-of-february-2025"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of June 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

Core
----

* [**Johan Vos** on Mastodon](https://mastodon.social/@johanvos/114629445557232949): "*Headless JavaFX platform passes almost all tests, with remaining failures being understood. Time to bring this to the next step.* " See [this message](https://mail.openjdk.org/pipermail/openjfx-dev/2025-June/054648.html) on the openjfx-dev mailing list.
* **Catherine Edelveis** [struggled with a delay when starting sounds on macOS versus Linux](https://bsky.app/profile/cat-edelveis.bsky.social/post/3lr6vzolv3k2x) and [shared the solution](https://bsky.app/profile/cat-edelveis.bsky.social/post/3lrffxcymos2h): "*I found the culprit of sound latency with JavaFX on mac - the mp3 format! Converted to wav, and everything works as a charm now. Heads up to everyone: mp3 is evil even in such non-trivial cases.*"
* An [interesting pull request by **Ambarish Rapte**](https://github.com/openjdk/jfx/pull/1824) in the OpenJFX project: "_This is the implementation of new graphics rendering pipeline for JavaFX using Metal APIs on MacOS. We released two Early Access (EA) builds and have reached a stage where it is ready to be integrated. Default rendering pipeline on macOS has not been changed by this PR. OpenGL still stays as the default rendering pipeline and Metal rendering pipeline is optional to choose by providing `MARKDOWN_HASH6fb3dbf166122f8049d99bbd64d48707MARKDOWN`*HASH*."
* [Pull request by **Kevin Rushforth** "Bump minimum JDK version for JavaFX to JDK"](https://github.com/openjdk/jfx/pull/1827) mentioned on the [openjfx-dev mailinglist](https://mail.openjdk.org/mailman/listinfo/openjfx-dev): "*In order for JavaFX to be able to use more recent JDK features, we should increase the minimum version of the JDK that can run the latest JavaFX. Additionally, there is an ongoing cost to keeping JavaFX buildable and runnable on older versions of Java, and little reason to continue to do so. This continues our recent practice of setting the minimum JDK for JavaFX N to JDK N-2. JavaFX N is primarily intended for use with JDK N and we also build and test it against JDK N-1 (which is typically what we use as the boot JDK). Anything older than that, including the proposed minimum JDK N-2 (23 in this specific case), is untested. This PR targeted to JavaFX 25, and must not be backported to any earlier version.*"

Applications
------------

* [Long message by **Sean Phillips** on LinkedIn](https://www.linkedin.com/posts/seanmiphillips_trinity-xai-auto-projecting-deep-fake-images-activity-7336811007543058433-Hmvl/) with a [link to a video demonstrating near real-time 3D clustering of Deep Fake images in JavaFX](https://www.youtube.com/watch?v=7M3R9C3tiho): "*... The video link above is an example of the Trinity XAI software demonstrating establishing a 3D clustering of Deep Fake imagery of human faces and automatically injecting new samples into the projection space (Auto-Projection). In this example the 3D clusters are established using a UMAP projection. The projection is frozen when auto projection is enabled so that new samples reuse the existing transform function achieved from the fit UMAP model. ... Trinity XAI has [cross platform binary builds here](https://github.com/trinity-xai/Trinity), as always, free and open source.*"
* A [project by **Bruno Rozendo** shared on LinkedIn](https://www.linkedin.com/posts/brunorozendo_family-has-grown-again-now-a-present-to-activity-7338551210796277761-jQk0): "*A present to you: my own GUI to chat with Ollama,
  using JavaFX. It provides custom mcp.json config file, chats are saved in sql lite, and works with local Ollama.* " You can find the [sources on GitHub](https://github.com/brunorozendo/mcp-client-gui).
* Also [found on LinkedIn](https://www.linkedin.com/posts/omarelfiki_im-excited-to-share-my-second-completed-activity-7338308176871632896-aeGY) by **Omar Elfiki** : "*I'm excited to share my second completed project at Maastricht University! A JavaFX-powered system to plan your journey across any public transportation network. Navigator transforms raw GTFS transit data into an intelligent routing engine with a graphical interface. Designed for public transit systems, it enables real-time route discovery using buses, trains, trams, and walking transfers.* " [Check it out here](https://github.com/omarelfiki/navigator).
* [**Robert Ladstätter** shared a video](https://www.youtube.com/watch?v=Ir1tmdWEgD4): "*LogoRRR is now available on flathub! This makes it very easy to install and use it - download now!* "
  * And [he shared on Bluesky](https://bsky.app/profile/rladstaetter.bsky.social/post/3ls72togyk22c): "*I'm trying (again) to get LogoRRR to run with GraalVM* " and managed to make progress thanks to to this article by **Catherine Edelveis** : [How to Create JavaFX Native Images](https://bell-sw.com/blog/how-to-create-javafx-native-images/).
* [**Christopher Schnick** shared a video](https://www.linkedin.com/posts/crschnick_javafx-activity-7339359872884338689-yP1W/): "*A new animated JavaFX loading screen and an integration for the new Apple containers. Will be available in XPipe 17.* "
  * [And he also shared multiple videos](https://www.linkedin.com/pulse/showcase-some-new-features-recently-shipped-xpipe-16-schnick-7hxcf/): "*Showcase of some of new features recently shipped in XPipe 16.*"
* [**Cryptomator** released version 1.17.0, based on Java and JavaFX 24](https://github.com/cryptomator/cryptomator/releases/tag/1.17.0): "*With Cryptomator, the key to your data is in your hands. Cryptomator secures and encrypts your sensitive data in your favorite cloud service. So you can rest assured that only you can access your data.*"
* [**JabRef** has great news](https://www.linkedin.com/posts/jabref_jabref-aarch64-arm64-activity-7340430313371688960-Hmqg/): "*JabRef now offers Linux-aarch64/arm64 binaries as well in the latest development version. This means you can run JabRef now also on your Raspberry Pi or on other Arm64 Linux notebooks. Snaps and flatpak will follow soon.*"

Games
-----

* [JediTermFX and DOOM -- Running the Legendary Game in JavaFX](https://www.linkedin.com/pulse/jeditermfx-doom-running-legendary-game-javafx-techsenger-xqiic/) by **Pavel Castornii** : "*About a year ago, we introduced our terminal emulator for JavaFX -- JediTermFX. One of its key features is that it uses a Canvas for rendering, which allows it to work with almost any terminal-based program. Recently, we decided to test how the emulator handles something a bit unusual -- the iconic game DOOM.*"
* Relesed on Steam on May 30th by **Gavin Lee Moutoux** : [Nocturne FX](https://store.steampowered.com/app/3739280/Nocturne_FX/). "*A high-intensity arcade game built entirely in JavaFX. Dodge pipes, harness unpredictable power-ups, and survive a world that unravels the longer you last---including secrets no one prepared you for.*"
* [**Gerrit Grunwald** on Bluesky](https://bsky.app/profile/did:plc:5bvlreuyymd7itwoucylq63e/post/3lrbnqeqkn22g): "*I did not check for quite some time but SpaceFX runs superfast on my M4 MBP with #JDK24 #JavaFX #Java.* " You can find it [on GitHub](https://github.com/hanSolo/spacefx).

Components, Libraries, Tools
----------------------------

* [Message from **JPro**](https://bsky.app/profile/jpro.one/post/3lqp4eaixlk2i): "*In JPro release 2025.2.1, we've updated how native keyboard shortcuts are handled, along with a number of small fixes \& improvements. We're hard at work behind the scenes, so keep an eye [on our changelog](https://www.jpro.one/docs/jpro-webapi/changelog/1/1/2025.2.x) in the coming months.*"
* [**Konstantin Gerry** announced Gadulka 1.7.0](https://bsky.app/profile/iamkonstantin.eu/post/3lrs2hurgw423): "*Gadulka is a minimalistic audio player library for Kotlin Multiplatform. Gadulka wraps the native player functionality from each target in "headless" mode. That is, the library does not provide any UI (this will be up to you). This version is mostly about updating dependencies like Kotlin 2.1.21, JavaFX and the minimum JDK is now 21. Gadulka links against but doesn't bundle JavaFX.* " You can find the [release and sources on GitHub](https://github.com/kkostov/gadulka/releases/tag/1.7.0).
* [**APIdia** published the JavaDoc of FXGL](https://apidia.net/mvn/com.github.almasb/fxgl-framework/21.1/) "*BUT, FXGL is partly written in Kotlin, which is currently not supported yet. The docs only comprise data from Java sources.*"
* [Update with screenshots by **Matt Coley** on the progress of his new library BentoFX](https://bsky.app/profile/mattcoley.bsky.social/post/3ls33isgrsc2v): "*Alright, I rewrote my JavaFX docking framework, BentoFX, from scratch (again) after incorporating it into multiple projects and learning what the pain points were with the existing architecture.*"

Podcasts, Videos, Books
-----------------------

* A [YouTube short by **Kensoft PH**](https://www.youtube.com/shorts/vwXOAa_KVKw) showing that ChatGPT can create a working minimal JavaFX application. Check his YouTube profile for more JavaFX videos.
* [**Helal Anwar**](https://www.linkedin.com/in/helal-anwar-94571016b/) pointed us at this video by Apple: [WWDC25: Explore Swift and Java interoperability](https://www.youtube.com/watch?v=QSHO-GUGidA). It doesn't mention JavaFX, but it would be nice to see if this can be extended to allow the same kind of integration with JavaFX. The sources of the project can be found [on GitHub](https://github.com/swiftlang/swift-java).
* Recording of the live stream [Quarkus Insights #210: Extension Spotlight on JavaFX](https://www.youtube.com/watch?v=yP-8wXjB_Es) is available on YouTube. **Holly Cummins** and **Max Rydahl Andersen** talked with **Clément de Tastes** about the JavaFX Quarkus extension to bring a modern, efficient, and fully featured toolkit for developing rich client applications.

Conferences
-----------

* [Recording of the talk "Building a native multiplatform SDKMAN in JavaFX"](https://www.devoxx.co.uk/talk/?id=17259) by **Jago de Vreede** at Devoxx UK has been published.

Tutorials
---------

* Short [video tutorial by **TechHiveHQ**](https://www.youtube.com/watch?v=NU2ntGrV-Yc): "*How to Enable Dark Mode in JavaFX App*"
* **Hidekazu Kubota** shared a "*[guide for beginners in Java and JavaFX to help them get started with development using VSCode](https://www.docswell.com/s/sosuisen/56VX4X-2025-06-16-234846). All 100 of my students have successfully learned JavaFX using this guide without any problems.*"
* Video tutorial by **TheCodeGenerator** : [Build a Weather App That Looks INSANE in 2025 (JavaFX + REST API)](https://www.youtube.com/watch?v=sXfOEJ4FY84): "*In this full tutorial, we'll build a real-time weather app using JavaFX, WeatherAPI.com, and modern development tools --- from the user interface to the API layer, all wrapped in clean architecture. Whether you're learning JavaFX or just want a project that actually looks good in 2025, this is for you.* " Sources [on GitHub](https://github.com/TheCodeGen/weatherlyFX).

Miscellaneous
-------------

* More AI by **Richard Baldwin** : [Enhancing JavaFX Applications with GPT-Supported Code](https://cloving.ai/tutorials/enhancing-javafx-applications-with-gpt-supported-code).
* **APIdia** added API docs for the following JavaFX components, created by **Dirk Lemmermann** : "*These controls are frequent and crucial building blocks of advanced JavaFX applications.* "
  * [afterburner.fx](https://apidia.net/mvn/com.dlsc.afterburner/afterburner.fx/)
  * [CalendarFX](https://apidia.net/mvn/com.calendarfx/view)
  * [FormsFX](https://apidia.net/mvn/com.dlsc.formsfx/formsfx-core/)
  * [GemsFX](https://apidia.net/mvn/com.dlsc.gemsfx/gemsfx/)
  * [GMapsFX](https://apidia.net/mvn/com.dlsc/GMapsFX/)
  * [KeyboardFX](https://apidia.net/mvn/com.dlsc.keyboardfx/keyboardfx/)
  * [PDFViewFX](https://apidia.net/mvn/com.dlsc.pdfviewfx/pdfviewfx/)
  * [PhonenumberFX](https://apidia.net/mvn/com.dlsc.phonenumberfx/phonenumberfx/)
  * [PickerFX](https://apidia.net/mvn/com.dlsc.pickerfx/pickerfx/)
  * [PreferencesFX](https://apidia.net/mvn/com.dlsc.preferencesfx/preferencesfx-core/)
  * [RetrofitFX](https://apidia.net/mvn/com.dlsc.retrofitfx/retrofitfx/)
  * [ShowcaseFX](https://apidia.net/mvn/com.dlsc.showcasefx/showcasefx/)
  * [UnitFX](https://apidia.net/mvn/com.dlsc.unitfx/unitfx/)
  * [WorkbenchFX](https://apidia.net/mvn/com.dlsc.workbenchfx/workbenchfx-core/)
* [Video by **Christopher Schnick** on LinkedIn](https://www.linkedin.com/posts/crschnick_javafx-activity-7338178050095742976-TUCi): "*Demo of the new macOS 26 liquid glass material with hashtag#JavaFX. This will be available in XPipe 17.*"

JFX Central
-----------

* New content:
  * Library: [JediTermFX](https://www.jfx-central.com/libraries/jeditermfx), a terminal emulator for JavaFX by **Pavel Castornii**.
  * People: [Catherine Edelveis](https://www.jfx-central.com/people/c.edelveis)
  * Company: [BellSoft](https://www.jfx-central.com/companies/bellsoft)
  * Video: [Use SceneBuilder to Create User Interfaces with JavaFX](https://www.jfx-central.com/videos/PKvuXsfWe_M)
* The overview of the Links Of The Week of May [got published on **Foojay**](https://foojay.io/today/javafx-links-of-may-2025/).
