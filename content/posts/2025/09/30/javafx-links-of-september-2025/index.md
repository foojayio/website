---
title: "JavaFX Links of September 2025"
date: "2025-09-30T06:08:48+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of September 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there…"
canonical: "https://webtechie.be/post/2025-09-26-javafx-links-of-september-2025/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-august-2025"
  - "javafx-links-of-july-2025"
  - "javafx-links-of-june-2025"
  - "javafx-links-of-may-2025"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of September 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* Java and JavaFX 25 are released! This new Long Term Support (LTS) will be maintained and updated for many more years, so we can expect a lot of production systems will be upgraded to this version...
  * Important: JavaFX 25 Requires JDK 23 or later.
  * You can download JavaFX 25 [from the Gluon website](https://gluonhq.com/products/javafx/) or download the Maven artifacts from Maven Central.
  * [JavaFX 25 release notes by Gluon](https://gluonhq.com/products/javafx/openjfx-25-release-notes/).
  * [JavaFX 25 Highlights on openjfx.io](https://openjfx.io/highlights/25/).
  * [Java 25 release notes by Azul](https://docs.azul.com/core/release-notes), including OpenJDK JEPs and the list with JavaFX fixes.
  * [JavaFX 25 Highlights by **Kevin Rushforth**](https://inside.java/2025/09/23/javafx-25/): "*JavaFX 25 is here with several new features and improvements! JavaFX 25 is designed to work with JDK 25, and is known to work with JDK 23 and later versions. Here are the highlights of the JavaFX 25 release*"
  * **Stefan Richthofer** published on APIdia the [documentation of the new JavaFX 25 release](https://apidia.net/mvn/org.openjfx/javafx/25/): "*This includes the incubating modules (input and richtext), however they are hidden by default. You can enable hidden modules via the menu in the top-right, see [the screenshot](https://bsky.app/profile/stewori.bsky.social/post/3lz6x4ha2uk24).*"
* Gluon announced the [new website for OpenJDK on Mobile](https://openjdk-mobile.github.io): "_An initiative to collaborate on tools/expertise for real Java on Mobile, see [the `mobile-dev` mailinglist](https://mail.openjdk.org/pipermail/mobile-dev/2025-August/000985.html). Let's leverage the power, beauty and maintainability of OpenJDK to run Java apps on mobile!_"
  * You can read more in the blog post: [Bringing OpenJDK to Mobile: A Community Effort](https://gluonhq.com/bringing-openjdk-to-mobile-a-community-effort/)
* [Announcement by **Gluon**](https://bsky.app/profile/gluonhq.com/post/3lyfu2mwbhs2u): "*Another major milestone for JavaFX: the Metal pipeline is now enabled by default in 26-ea builds, which you can [download here](https://gluonhq.com/products/javafx/#ea). Read more about it [in the openjfx-dev mailinglist](https://mail.openjdk.org/pipermail/openjfx-dev/2025-August/055993.html). This is excellent work by the JavaFX team at Oracle. Feedback is greatly appreciated!* "
  * Followed by a [request from **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3lyfuw66pjs26): "_If you're on mac, please try JavaFX 26-ea+6 and share feedback. Tip: add `MARKDOWN_HASH2ad2e7464c1b84e91dbe41e72ff65eecMARKDOWN`*HASH* which will confirm you're really using metal, as shown below. Many thanks to Kevin, Ambarish, Ajit, Jayathirth and others for their great work."
* [Message by **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3lywvyv4jz22a) (inspired by the [Graal-related announcement of Oracle](https://blogs.oracle.com/java/post/detaching-graalvm-from-the-java-ecosystem-train)): "*To be very clear on the broader [OpenJDK-mobile initiative we announced last month with Gluon](https://openjdk-mobile.github.io/): this is all based on [open-source tech (github.com/openjdk/mobile)](https://github.com/openjdk/mobile). We deliberately avoided dependencies on company-owned products. We *very much* depend on, and leverage the great technical work, including the open discussions in OpenJDK mailing lists, GitHub PR's and JBS issues. Important to make the distinction between the top-quality work from engineers and the strategic decisions made by companies.*"

## Applications

* **Dean Courtney** shared the [sources of DietAI](https://github.com/dean-703/dietai): "*It's a JavaFX application designed for diet analysis, allowing users to import CSV files from diet trackers. It displays entries in a table, summarizes nutritional data, and utilizes AI for personalized evaluations. Users can set profiles and goals and compare actual intake against targets.*"
* **Helal Anwar** shared the [sources, including a video demo, of GradedAttendance](https://github.com/Hilal-Anwar/GradedAttendance): "*A comprehensive attendance management system with integrated grading capabilities, designed to streamline educational institution workflows and provide efficient student attendance tracking.* "
  * [And he shared a screenshot on LinkedIn](https://www.linkedin.com/feed/update/urn:li:activity:7375391855204696064/): "*A messaging app implemented in JavaFX. How does this look?* " Sources are available [here](https://github.com/Hilal-Anwar/GradedAttendance).
  * **Helal** also created a Quiz app as part of an internship at Elevate Labs using JavaFX. Sources, screenshots and demo video are [available on GitHub](https://github.com/Hilal-Anwar/QuizTime).
* [Message from JabRef](https://foojay.social/@jabref/115117290929844138): "*JabRef has so many features that it's sometimes difficult to get started. This year's completed GSOC project from our student Yubo changes this. He successfully implemented a Welcome Walkthrough that helps new users to set up JabRef. This also includes the revamped welcome tab with quick settings. For more details, see [our blog post](https://blog.jabref.org/2025/08/14/walkthrough-and-welcome-tab/).*"
* [Published by **Madhur Gupta** on GitHub](https://github.com/heyMadhur/ImageProcessorApp): "*ImageProcessorApp: Lightweight JavaFX demo that applies tile-based image filters synchronously or asynchronously.*"
* Just like last week, a new [AI project shared on GitHub by **Dean Courtney**](https://github.com/dean-703/aiassistant): "*AI Assistant - OpenAI Desktop Client: a JavaFX client for ChatGPT, designed to be a useful and versatile interface to the OpenAI API.*"
* Very nice application screenshots [shared on Reddit by **Gufran Thakur**](https://www.reddit.com/r/java/comments/1na6zuf/with_all_the_ai_website_slop_going_around_here/): "*With all the AI website slop going around, here are some Java desktop applications I created at work!*"
* [LogoRRR shared a screenshot](https://bsky.app/profile/logorrr.bsky.social/post/3lysnfnjgy226): "*After the release of 25.1.0, work is ongoing for the 'NextRelease'! The searchterm toolbar gets a facelift - it looks far better, what do you think? [Download LogoRRR here](https://www.logorrr.app/)*."
* [**Catherine Edelveis** refactored RaffleFX app](https://bsky.app/profile/cat-edelveis.bsky.social/post/3lynhk6wl5s2l) "*which demonstrates the usage of GraalVM Native Image with desktop apps, to use Gradle, so if you'd like to create native images of Gradle-based JavaFX projects, [you can use it as a starting point](https://github.com/code-with-bellsoft/raffle-gradle)*."

## Components, Libraries, Tools

* [jDeploy 5.0](https://www.jdeploy.com/docs/releases/5.0/release-notes.html), the tool the to distribute your Java(FX) app as a native bundle, has been released. It now includes [ARM64 support for Windows and Linux](https://jdeploy.substack.com/p/finally-arm64-support-for-windows) on both desktop and CLI apps.
* **Dirk Lemmermann** is a fan of AtlantaFX: "*Our CRM system at Senapt / Tomato Energy has now been completely transitioned to the AtlantaFX theme / theming support. The result is a much more pleasant user experience. A clean, modern looking UI. The semantic colors give us multi-theme / dark-mode support for free.* " Check the [screenshots in his Bluesky message](https://bsky.app/profile/dlemmermann.bsky.social/post/3lxryso6uy223).
* [Announcement with screenshots by **Dirk Lemmermann**](https://bsky.app/profile/dlemmermann.bsky.social/post/3lyih22nzrk25): "*CalendarFX 12.0.1 has built-in support for AtlantaFX. Several themes and dark mode support for free.*"
* [**Hidekazu Kubota** announced on Bluesky](https://bsky.app/profile/sosuisen.bsky.social/post/3lyimtmomzk2x): "*I'm releasing the results of my one month of effort. The [JavaFX Builder API Project](https://github.com/sosuisen/javafx-builder-api) provides a builder pattern API for JavaFX UI components, enabling the creation of JavaFX applications through a fluent, strongly typed, and declarative approach. This adds flavors that fluent API enthusiasts will appreciate on top of the original JavaFX API. Currently, it is a SNAPSHOT version that can be tested by adding it as a Maven dependency. I plan to release an early access version next, but before that, I would like feedback from JavaFX developers.*"
* [**Hidekazu Kubota** on Bluesky](https://bsky.app/profile/sosuisen.bsky.social/post/3lytxkzgwac2q): "*I have been developing a VSCode extension to generate JavaFX builders, but I decided to [rewrite it as a library](https://github.com/sosuisen/javafx-builder-api) for a better approach. Recognizing the library's clear potential, I chose to halt the development of the extension.* "
  * And a [follow-up message](https://bsky.app/profile/sosuisen.bsky.social/post/3lyyj2hqhus2x): "*The JavaFX Builder API has been updated to support JavaFX 25. Since the JavaFX API is quite stable, I believe that supporting it through a code generator approach is straightforward.*"
* JPro (to run JavaFX in the browser) released version 2025.3.1: "*Now supports Java and JavaFX 25, and Gradle 9. The JPro Loadbalancer is now released together with the runtime, and we've made a lot of changes under the hood.* " Check the [release notes here](https://www.jpro.one/docs/jpro-webapi/changelog/1/1/2025.3.x).

## Podcasts, Videos, Books

* Short video: [JavaFX vs Electron, The Ultimate Performance Showdown!](https://www.youtube.com/shorts/Wrl1_NnruyI) which is part of the [Ben \& Ryan Show - Episode 22 - BoxLang](https://www.youtube.com/watch?v=Pg7vuclC3KI): "*In this episode, your hosts Ben Nadel and Ryan Brown sit down with Luis Majano and Daniel Garcia from Ortus Solutions to dive deep into BoxLang, the dynamic new language for the JVM. From modular design to multi-runtime capabilities and modern tooling, this conversation explores past, present, and future of BoxLang.*"

## Tutorials

* [JavaFX Custom Shaped Buttons](https://genuinecoder.com/javafx-buttons-with-custom-shape/) with code examples and a video walk-through by **Muhammed Afsal Villan**.
* A great article with a lot of code examples by **Catherine Edelveis** on DZone: [Top 7 Mistakes When Testing JavaFX Applications](https://dzone.com/articles/top-javafx-testing-mistakes). "*Testing JavaFX programs may seem non-trivial at first. This article describes the most common mistakes when testing desktop apps, their causes, and solutions.*"

## Miscellaneous

* A [fun message by **Todd Mitchell**](https://bsky.app/profile/toddnotmitchell.com/post/3lxqm5io4322k) about "abused JavaFX code": "*Very distracting in the Unknown Number / High School Catfish doc that when the FBI guy starts talking about extracting phone data they show made up contact information for Charlie Brown, Ethan Hunt, and Kevin McCallister laid over a guy's JavaFX game code I was able to find on Russian StackOverflow.*"
* Last week we had a Mandelbrot example by **A N M "Bazlur" Rahman** . This week, thanks to a [tip by **Max Rydahl Andersen**](https://bsky.app/profile/maxandersen.xam.dk/post/3lxzl67e3222v), we have another one. MandelbrotFx, a fractal (Mandelbrot, Julia, ...) explorer written with JavaFX, is a [project shared by **Clément de Tastes**](https://github.com/CodeSimcoe/MandelbrotFx).
* [**Johan Vos** on Bluesky](https://bsky.app/profile/johanvos.bsky.social/post/3lxzbhapzn22w): "*There are so many cool performance tricks for JavaFX that I sometimes think I should write a book about it. But that takes a huge amount of time. I'll think a bit more about how to deal with this.*"
* [**Dirk Lemmermann** uses JavaFX styling in the best possible way](https://bsky.app/profile/dlemmermann.bsky.social/post/3lyiiqznzls25): "*'Empathy driven development'"' means to consider that there are users out there with disabilities. One of them is 'color vision deficiency' (CVD), and today I added a preferences pane that allows our users to pick their status colours based on their CVD.*"
* And [another message with screenshot by **Dirk**](https://bsky.app/profile/dlemmermann.bsky.social/post/3lyl3wr3w4s2a): "*Once again amazed how well JavaFX runs inside browsers via [JPRO](https://www.jpro.one).*"
* [**Frank Delporte** bumped his Raspberry Pi's to version 25](https://bsky.app/profile/frankdelporte.be/post/3lz4fnslmus2c): "*Welcome to Java 25 on the Raspberry Pi! The install script in the [Pi4J OS repository](https://github.com/Pi4J/pi4j-os) to configure a new Raspberry Pi has been updated to install Azul Zulu 25 with JavaFX. Or you can [follow the updated instructions on the Pi4J website](https://www.pi4j.com/prepare/install-java/).*"
* [**Clément de Tastes** published "Valhalla \& Value Types au pays des fractales"](https://blog.sciam.fr/2025/09/22/value-types-et-fractales.html) in French. Translated intro: "*We invite you to make a leap in the future of Java, especially through improvements that the Valhalla project prepares for us. We will take the opportunity to put them into practice on a talking example: the calculation and visualization of the whole of Mandelbrot.*" In the article, JavaFX is used to generate beautiful fractals.

## JFX Central

* New content on JFX Central:
  * [Defold, the game engine for high-performance cross-platform games](https://www.jfx-central.com/showcases/defold).
* The links of August got [published on Foojay](https://foojay.io/today/javafx-links-of-august-2025/).
