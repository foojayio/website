---
title: "JavaFX Links of June 2024"
slug: "javafx-links-of-june-2024"
date: "2024-06-30T07:07:56+00:00"
lastmod: "2024-07-01T08:48:03+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of June 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-06-28-javafx-links-of-june-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-may-2024"
  - "javafx-links-of-april-2023"
  - "javafx-links-of-february-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of June 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

## Core

* New JavaFX 23 Early-Access Builds are [available on jdk.java.net/javafx23](https://jdk.java.net/javafx23/).
* [**Friedhold Matz** shared a screenshot](https://x.com/FriedholdMatz/status/1776354116517732465) showing that WebView in JavaFX 22 now works with GraalVM 22+36.1.

## Applications

* [**Christopher Schnick** shared screenshots](https://x.com/crschnick/status/1797530886289940828): "XPipe 9.4 comes with a JavaFX markdown view for notes that works through the WebView. It supports using external editors and updates in real time." Check the sources on [GitHub](https://github.com/xpipe-io/xpipe).
* [Smartfinder now runs on Java 22](https://x.com/SerendigityInfo/status/1797164327763357725). It's a Desktop Search alternative to Windows Search/Mac OS Finder.
* [LogoRRR reached a major milestone this week](https://twitter.com/logorrr/status/1800544389216997657): "10.000 downloads for LogoRRR on all platforms!! I'm very proud of this achievement, thank you guys!🙂"
* [**Xiong Chun** keeps teasing us with videos](https://x.com/xiongchun007/status/1799863501135245739) showing progress with his Datacollie application to interact with databases...
* [**Hallvard Trætteberg**](https://twitter.com/haltraet) shared [code of a LLM workbench to explore various parts of a RAG chain using Quarkus, LangChain4J and JavaFX](https://github.com/kantega/llm-starter-and-workbench) (with an extension allowing Quarkus to work with JavaFX): "The UI is rough, just what is needed functionally, but the project may still be interesting for others. It's also a showcase for a rare combination, a desktop app running on Quarkus."
* [**Sean Phillips** is taking requests and suggestions for Trinity](https://x.com/SeanMiPhillips/status/1802142816602108122): "If anyone has ideas they would like to see implemented in a 3D asteroids game or in the actual AI analysis tool, and it's feasible in my spare time, I will happily attempt to implement it. (and credit you!)"
* In a XTwitter Thread, [**Emad Hanif** shares several videos of Barcodify](https://x.com/EmadHanif_/status/1806383935083233771): "Check out how easy it is to configure and export barcodes."
* [**Patrik Karlström** is working on nbLauncher](https://x.com/PatrikKarlstrom/status/1804865421918491014), an app to build launcher configs for NetBeans Platform Apps: "Expect a release after testing and polishing." You can [find it on GitHub](https://github.com/trixon/nbLauncher).

## Games

* [**WebFX** published a new version of SpaceFX](https://x.com/WebFXProject/status/1797963817659600978) a game originally created by [**Gerrit Grunwald**](https://x.com/hansolo_). It's playable online at [spacefx.webfx.dev](https://spacefx.webfx.dev) with a few updates to make the game even more challenging for true gamers. More WebFX demos on [github.com/webfx-demos](https://github.com/webfx-demos).
  * And [another new update](https://x.com/WebFXProject/status/1803038702693740734) of the [online SpaceFX](https://spacefx.webfx.dev): "2 new weapons: Autofire Fury and Rainbow Blaster. Can you now score 100,000? All of this made possible only by the amazingly performant JavaFX game engine written by **Gerrit Grunwald** and the power of GWT."
* [The Trinity XAI analysis tool by **Sean Phillips** now supports video playback and automatic clustering algorithms](https://x.com/SeanMiPhillips/status/1801354864162967663). Because data science is also fun, he integrated the ability to turn your AI analysis data and clusters into a playable Asteroids 3D minigame. [Watch the video here](https://www.youtube.com/watch?v=vFThM9BoTLg), and the [code is available here](https://github.com/Birdasaur/Trinity). "Watch out for those alien Opticons..."
* [**OrangoMango** shared](https://x.com/orango_mango/status/1805585806863602035) a [video showcase of his best projects since February 2020](https://www.youtube.com/watch?v=npwdeEwLjpY): "JavaFX is the best 💪."

## Components, Libraries, Tools

* [**vlaaad** released version 1.9.0 of cljfx](https://twitter.com/v1aaad/status/1800614531191996618), a reactive UI wrapper of JavaFX for Clojure: "The new features bring cljfx a bit further away from re-frame and a bit closer to react." Check out the [changelog on GitHub](https://github.com/cljfx/cljfx/blob/master/CHANGELOG.md).
* Updates shared by [**Pedro Duke**](https://x.com/P_Duke):
  * Do you want your JavaFX application to have rounded borders? [FXThemes can do that for you](https://x.com/P_Duke/status/1797614063289274570).
  * [Video of an experiment with a new Login Dialog](https://x.com/P_Duke/status/1800522435734995086): "Using FXThemes for the background blur on the Dialog and FXSkins for the differently looking Progress Bar and Button animations."
  * [Video showing that the next version of FXThemes](https://x.com/P_Duke/status/1801238597707735479) will allow you to add platform decorations to an Undecorated or Transparent Stage.
  * [Version 1.6 of FXThemes](https://x.com/P_Duke/status/1803758549677293986): "Change the roundness of window corners. Set native platform decorations on TRANSPARENT or UNDECORATED Stages."
* Updates shared by [**Dirk Lemmermann**](https://twitter.com/dlemmermann/):
  * Uploaded a video to [demonstrate the AvatarView in GemsFX](https://www.youtube.com/watch?v=9CaDcCvJZ7I).
  * [Added an AvatarView control to GemsFX](https://twitter.com/dlemmermann/status/1801010129237598714), which can be used to either display the image/avatar of a user or his/her initials. Clipping will give you a round or a square view.
  * Doing a "[2nd attempt at providing a reusable control in GemsFX that features a table view with built-in column-based filtering](https://x.com/dlemmermann/status/1805595281209118818) (aka iTunes-style filtering). Trying to use what I learned since I started coding JavaFX in 2013."

## Podcasts, Videos, Books

* [**Frank Delporte**](https://x.com/FrankDelporte) started a new series of videos: "JavaFX In Action":
  * [**Pedro Duque Vieira**, aka Duke about Hero, PDFSam, FXThemes, FXComponents,...](https://webtechie.be/post/2024-06-05-jfxinaction-pedro-duque-vieira-duke/)
  * [**Daniel Zimmermann** about JavaFX and Kotlin](https://webtechie.be/post/2024-06-12-jfxinaction-daniel-zimmermann/) to build nice user interfaces, making full use of the JVM to run on any platform and perform heavy tasks like testing network speeds up to 10Gbps!
  * [**Christopher Schnick** about XPipe](https://webtechie.be/post/2024-06-18-jfxinaction-christopher-schnick/), an app to manage all your servers.
  * [**Robert Ladstätter** about LogoRRR](https://webtechie.be/post/2024-06-26-jfxinaction-robert-ladstatter/), a cross-platform log analysis tool written with Scala and JavaFX to find problems blazingly fast in log files with millions of lines.

## Tutorials

* [**Mark Leveque** shares a side project](https://www.linkedin.com/pulse/side-project-local-llm-powered-image-renaming-tool-marc-leveque-sdkfe/): "Local LLM-Powered Image Renaming JavaFX Tool. Use case: you have a disk full of randomly named images and you want to give them filenames relevant to their content. The article will covers: llama.cpp with Java, JavaFX as GUI, packaging the application and pitfalls along the way."
* Video tutorial by [**Balkrishna Srivastava**](https://twitter.com/iamBalkrishnaS): ["JavaFX GUI App to play the game of TicTacToe in Java."](https://www.youtube.com/watch?v=8h0CJCsnsa4)
* Video tutorial by **Kensoft PH** : ["JavaFX Pie Chart: World Population with National Flag."](https://www.youtube.com/watch?v=1u0boNYDGNI)

## Miscellaneous

* [**OrangoMango** shared a quick video of a handwritten digits recognizer using neural networks](https://x.com/orango_mango/status/1800494415674577288): "Of course built from scratch and in Java and JavaFX".
* JavaFX love by...
  * [**Tanmay**](https://x.com/maytanthegeek/status/1794393624995885123): "Not gonna lie. I still love JavaFX. Android layout learned a lot from it. JavaFX is so fun and simple. I would really like to see it take a leading spot some day for GUI programming."
  * [**Xiong Chun**](https://x.com/DaXiong008/status/1799322279241011688): "Never has styling a Java UI been easier than with JavaFX and CSS. Going from one theme to another, or customizing the look of just one control, can all be done with CSS. eg: I want to let the scrollbar is only be displayed when the component has focus, otherwise disappear."
  * [**Sharat Chander**](https://twitter.com/Sharat_Chander/status/1801461459840549198): "Wicked cool usage of Java AND JavaFX! @SeanMiPhillips, super genius!"
  * [**Jasper Potts**](https://twitter.com/jasperpotts/status/1800623060346921264): "Had déjà vu today seeing the new Apple Car UI, guess what Mo Chicharro and I designed 10 years ago was ahead of our time. 😀"
  * [**Evander Torres**](https://twitter.com/EvanderTor57903/status/1800928991689453989): "I think JavaFX is the true desktop ui solution for Kotlin!"
* [**Crystal Furman** wrote a long LinkedIn message about the use of JavaFX in education](https://www.linkedin.com/embed/feed/update/urn:li:share:7212075229894881280): "I encourage you to try out Java FX charts with your students. For some, data analysis is simply not going to be exciting, even if it is on a topic they care about. Adding some graphics, might just spruce up this project and make it more exciting for them. Hope you try it and let me know how it goes."
* [**Gerrit Grunwald** shares a screenshot](https://x.com/hansolo_/status/1806004059368784098): "JavaFX is simply super productive... Created this chart component in just 1.5h... From idea to running in my app... Just love it..."
* [**Carl Dea** is extending Cognitive, a JavaFX MVVM library](https://x.com/carldea/status/1805773088576974861), with a new feature, as [extensively documented on GitHub](https://github.com/carldea/cognitive/issues/2): "Additional way to look up properties using a Property Identifier type object instead of a String."
* Experiments by **konczdev** :
  * "[Git branch graph like JavaFx visualization entirely made by Claude 3.5 Sonnet](https://x.com/konczdev/status/1804825701134082331). All iterations. The input was just an image and a short prompt."
  * "[A little fun with Java 22, JavaFX and Project Panama](https://x.com/konczdev/status/1804591278073532624). Modified JavaFX window with a custom JavaFX title bar without any refection, JNI, JNA or native code."

## JFX Central

* The videos of "JavaFX In Action", mentioned above, are also added to the [video section of JFX Central](https://www.jfx-central.com/videos), select "JFX In Action" in the "Type" filter. More to come after the summer break...
* [**Dirk Lemmermann** shared visitor statistics](https://x.com/dlemmermann/status/1798688644133486973): "We are seeing more and more traffic on JFX Central coming from China and the United States. Normally Germany was on the number 1 spot. Interesting..."
* All the Links Of The Week of May are [bundled in one post on Foojay.io](https://foojay.io/today/javafx-links-of-may-2024/).
