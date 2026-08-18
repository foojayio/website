---
title: "JavaFX Links of September 2024"
slug: "javafx-links-of-september-2024"
date: "2024-09-30T02:00:00+00:00"
lastmod: "2024-10-01T07:18:45+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of September 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-09-27-javafx-links-of-september-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-august-2024"
  - "javafx-links-of-july-2024"
  - "javafx-links-of-june-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of September 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

A lot of milestones this month...

* ***Java and JavaFX 23*** were released!
* The ***100th JavaFX Links Of The Week*** was published on [jfx-central.com/links](https://www.jfx-central.com/links)!
* The ***release of the iOS JFX Central App***!

More info and links below in this overview...

## Core

* [**Pedro Duke** shared the following](https://x.com/P_Duke/status/1830644652287926277): "Noteworthy features coming in the next release of JavaFX (23) coming this month (September 17):"
  * CSS transitions: This introduces basic animation support in CSS. Won't be able to do it on Background and Borders for now. Work is already underway to also support that in a next release.
  * Support "@3x" and greater high-density image naming convention. Currently JavaFX supports `img.png` and [[email protected]](/cdn-cgi/l/email-protection), soon also [[email protected]](/cdn-cgi/l/email-protection).
  * Horizontal scroll support with the keyboard on controls like ListView, TreeView
  * TextTruncated property to know when text is being truncated
  * Add support for EXT-X-MEDIA tag in HTTP Live Streaming
* And [**Pedro** also highlights](https://x.com/P_Duke/status/1831373478806667774) a new "Public Focus Traversal API for JavaFX" proposal:
  * [Draft description on GitHub](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/FocusTraversal/FocusTraversal.md).
  * And the [pull request in the OpenJDK JFX repository](https://github.com/openjdk/jfx/pull/1555).
* Are you ready for Java and JavaFX 23 next week? 🙂 This is the [commit by **Kevin Rushforth**](https://github.com/openjdk/jfx/commit/639f138380a4091befa3046f7211aab77f8d77dd) with "Release Notes for JavaFX 23".
* Gluon published a [blog post about the JavaFX 23 release](https://gluonhq.com/javafx-23-is-here/): "This new version brings a host of improvements and enhancements, offering developers even more power and flexibility for creating cross-platform desktop experiences."
* You can find the [highlights of JavaFX 23 on openjfx.io](https://openjfx.io/highlights/23/).
* A list with all the changes in this release [is available on GitHub](https://github.com/openjdk/jfx/blob/jfx23/doc-files/release-notes-23.md).
* Important note: "JavaFX 23 Requires JDK 21 or later."

## Applications

* [PDFsam announced version 5.2.5 of PDFsam Basic](https://x.com/PDFsamOSS/status/1826188220570226707): "Most notably, you can use the keyword 'last' in the page selection of the extract pages tool, allowing you to extract the last page from multiple PDF documents." You can find the [Release Notes here](https://t.co/R3sf3sVdRh).
* [Deep Netts (Build and deploy ML models in Java) has enhanced debugging with JavaFX visualization tools](https://x.com/DeepNetts/status/1835292030315995390) that let you analyze weight statistics and explore network architectures in 3D, providing better insights for diagnosing issues and optimizing performance.
* [**Xiong Chun** is still #buildinginpublic on Datacollie](https://x.com/xiongchun007/status/1838109763806044588): "Completed the function of data editing of cells in the query results table."
* [**Jago de Vreede** released a new version of SKDMAN-UI](https://x.com/JagoVreede/status/1838442853204160807): "It now contains all possible candidates, and an auto update for the next version." Release notes [on GitHub](https://github.com/jagodevreede/sdkman-ui/releases/tag/v0.1.0).
* [**Ulas Ergin**](https://x.com/ulasergin/status/1838293466658296179) shared a [major milestone](https://www.linkedin.com/posts/ulasergin_modernizing-primereact-javafx-activity-7243973455702822913-cNrX/): "We've rolled out a new, cutting-edge client powered by PrimeReact and JavaFX, replacing the legacy Java Swing client. A better, faster, smarter core banking application, we call it core+. This transformation will empower our colleagues across the bank to serve our customers even better, offering enhanced performance and a more seamless, intuitive user experience."
* [**Patrik Karlström** announced version 24.09.0 of nbRsync](https://x.com/PatrikKarlstrom/status/1837847203844415847), the rsync GUI written in Java with JavaFX on the NetBeans Platform. [Release notes and downloads on GitHub](https://github.com/trixon/nbRsync/releases/tag/v24.09.0).

## Games

* [**Mark J Koch** shared](https://mastodon.social/@maehem/113165558900968220): "A good moment for a dev update on the Neuromancer PC game I've been unofficially porting to JavaFX. The player can now connect to one of the banks and crack the password using appropriate software. Hope to have something 'Alpha' quality that folks can try out in the coming weeks."
* [**ayuusse** is "going to make Chess](https://x.com/ayuusse136620/status/1836963064408854681) in JavaFX in my spare time. Good Start I guess 🙃."
* [**Catalin Rontu** coded a 2048 game using JavaFX](https://www.linkedin.com/posts/catalin-rontu_github-rontzew2048demo-javafx-2048-game-activity-7241840037804273665-REXR/): "It's small and fun and it helped me learn something new while also enjoying myself. I plan on making many updates (check the readme file) to it so stay tuned for future versions!"

## Components, Libraries, Tools

* [**Johan Vos** shared on Mastodon](https://mastodon.social/@johanvos/113085258956232434): "We're getting closer to a new version of Gluon Substrate, enabling Java 23 and JavaFX 23 on mobile (ios/android). Also, more focus on creating static libs (containing compiled versions of your Java code) that can be plugged in new/existing ios/android projects. Since we don't have devrel/marketing, the website updates are way behind what we do. But we'll update it this time. Keep an eye on [gluonhq.com](https://gluonhq.com)."
* Library updates by [**Pedro Duque Vieira**](https://x.com/P_Duke):
  * [FXComponents version is 1.6.2 released](https://x.com/P_Duke/status/1828096262228648294): "Includes tweaks to NavigationPane."
  * Work in progress on the next release of Transit Theme: "[Created a new sampler app](https://x.com/P_Duke/status/1819356682457137601) (using FXComponents NavigationPane), and changed Button appearance along with extra Button styles. New LIGHT and DARK styles for [ToggleButton and Checkbox](https://x.com/P_Duke/status/1829148999825404359) are added. Same for [RadioButton and ComboBox](https://x.com/P_Duke/status/1830998729123311908)."
  * [Transit Theme WIP Part 4: New LIGHT and DARK styles for TextField and PasswordField added](https://x.com/P_Duke/status/1833159116258230771).
  * [Transit Theme WIP Part 5: New LIGHT and DARK styles for ProgressBar and Slider added](https://x.com/P_Duke/status/1834223862424211912).
  * [FXSkins version 1.1.0 released](https://x.com/P_Duke/status/1834280387901596054): "Update to Java and JavaFX version used for compilation. And fixed an issue with ProgressBar skin getting stuck (stopped). As usual you can get it through Maven Central."

## Podcasts, Videos, Books

* [Video by **Sean Phillips**](https://www.youtube.com/watch?v=ccvhOEXtqJ4): "JSON RPC control of JavaFX visualization from Python/Jupyter. In this example a simple Python script, runnable from either CLI or Jupyter notebook uses httpx to post JSON formatted data (225 mbs of AI feature vectors) and commands to a receiving JavaFX application called Trinity."
* **Frank Delporte** published new interviews in his "JavaFX In Action series":
  * [FxCalculator, an Android app built with Scala and JavaFX](https://webtechie.be/post/2024-09-17-jfxinaction-maciej-gorywoda/) with [**Maciej Gorywoda**](https://x.com/makingthematrix).
  * [**Ramiro Domínguez Ayub** about the Televic Generic Update Tool (TGUT)](https://webtechie.be/post/2024-09-24-jfxinaction-ramiro-dominguez-ayubat/).

## Tutorials

* New [tutorial videos on JFX Central](https://www.jfx-central.com/learn-javafx), see below.
* School of Computing, National University of Singapore, is working on a free JavaFX tutorial the [first sections are available here](https://se-education.org/guides/tutorials/javaFx.html).
* Tutorial by On Exception: [Creating a Simple Weather App with JavaFX and Jackson Libraries using Maven](https://onexception.dev/news/1393916/javafx-weather-app-with-maven).

## Miscellaneous

* [**Rumble Tumble Kid** shared a GitHub project](https://x.com/rumbletumblekid/status/1829538211846357065): "Here's a small template I created a while ago that shows you how to either package your Scala GUI application using jlink and jpackage or compile it ahead-of-time via Graal Native": [package-scalafx](https://github.com/RumbleTumbleKid/package-scalafx).
* Check [this thread by **Sankalp**](https://twitter.com/Sankalp0704/status/1831374170195947685). He found an old book and is comparing old Java that didn't have resource files or design-time layout tools, versus current FXML and SceneBuilder.
* [**Tim Pote** spent the weekend working with JavaFX](https://twitter.com/potetm/status/1830966979332620442): "I gotta say having access to the JVM and real threads while writing a UI is very, very nice. Big shoutout to [@v1aaad](https://twitter.com/v1aaad) for his work on [cljfx](https://github.com/cljfx/cljfx)!".
  * cljfx = "Declarative, functional, and extensible wrapper of JavaFX inspired by better parts of react and re-frame."
* [JabRef is asking the community for help](https://foojay.social/@jabref/113126358640056652): "Anyone encountered this strange UI font rendering before? Popped up in JabRef, but apparently other applications are affected as well. Anyone a clue or maybe someone else can reproduce this?"
* [**Aadil Raja** is learning Java and created a media player](https://twitter.com/Akill03712420/status/1833591578574942649) using Java and JavaFX. You can find the sources on [GitHub](https://github.com/aadilraja/MediaPlayer).

## JFX Central

* The Links of August got also [published on Foojay.io](https://foojay.io/today/javafx-links-of-august-2024/).
* [**Dirk Lemmermann** "is having way too much fun styling the intro page for a freshly installed JFX Central
  mobile app :-)"](https://x.com/dlemmermann/status/1831367446131044553).
  * With a video preview [in this tweet](https://x.com/dlemmermann/status/1831649178440630603).
* [**Dirk Lemmermann** shared App Store screenshots](https://x.com/dlemmermann/status/1832065670030283200) for the JFX Central app, [created with @AppScreensASO](https://x.com/AppScreensASO). But ... "[Apple review is giving me a hard time](https://x.com/dlemmermann/status/1833421417913675942) to get the app into the App Store. E.g. '"'the app is not using any native iPhone features, e.g. Core location'"'. So simply making lots of data available doesn't seem to be good enough for Apple. But it would be for the users..."
* The [JFX Central App is now available from the Apple App Store](https://apps.apple.com/ch/app/jfxcentral/id1613971561)!!! Keep being informed about all JavaFX news, libraries, persons, books, tutorials with this very handy app! Based on the same source code of the JFX Central website and with the same database, it brings the same content now to desktop, web, and (iOS) mobile! Soon also on Android...
* [**Dirk Lemmermann** calls out to all developers](https://x.com/dlemmermann/status/1834983836389642275): "Got a cool JavaFX application? Wanna showcase it so the entire world can see it? Just submit it to [\[email protected\]](/cdn-cgi/l/email-protection) and we will make it happen... on everybody's desktop, in the browser, on their phones."
* New content:
  * Showcase: [MelodyMatrix - Look at your music...](https://www.jfx-central.com/real_world/melodymatrix). It's a desktop application created by **Frank Delporte** and his 14y old son **Vik** to visualize music being played on MIDI instruments.
  * Tutorial: [JavaFX with Kotlin versus Java](https://www.jfx-central.com/learn-javafx/kotlin-versus-java). Kotlin also runs on the Java Virtual Machine (JVM) and is a very close sister of Java. In this tutorial, you'll learn the difference in code style when combining it with JavaFX.
  * Video: [JavaFX In Action with Ramiro Domínguez Ayub](https://www.jfx-central.com/videos/1gvKCS35ono).
  * Video: [JFX In Action with Maciej Gorywoda about the Android app FxCalculator](https://www.jfx-central.com/videos/93OozqMTqJQ).
  * Video added to "Learn JavaFX": [Your first JavaFX application](https://www.jfx-central.com/learn-javafx/hello-world)
  * Video added to "Learn JavaFX": [Using the Button Component](https://www.jfx-central.com/learn-javafx/button)
  * Video added to "Learn JavaFX": [Styling JavaFX Buttons with CSS](https://www.jfx-central.com/learn-javafx/button-styling)
