---
title: "JavaFX Links of May 2024"
date: "2024-06-01T08:12:12+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of May 2024, published on jfx-central.com during this month. Core When Johan Vos shares his notes, you…"
canonical: "https://webtechie.be/post/2024-05-31-javafx-links-of-may-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-april-2024"
  - "javafx-links-of-march-2024"
  - "javafx-links-of-february-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of May 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

## Core

* When [**Johan Vos** shares his notes](https://mastodon.social/@johanvos/112477892809739391), you may want to keep on eye on them...: "While working on Java on Mobile, I [keep my notes here](https://download2.gluonhq.com/mobile/) (best viewed with lynx). This is not user/developer documentation, but people interested/familiar with OpenJDK building might keep an eye to see how far we are."
* [Nice visualization by **afoo**](https://x.com/afoo_me/status/1795340765469855915) to illustrate the flow of event phases (capturing with EventFilter versus bubbling with EventHandler) in JavaFX.

## Applications

* [**Robert Ladstätter** announced a new release of LogoRRR](https://twitter.com/logorrr/status/1785790043451249020): "This update is a maintenance release with visual improvements and bugfixes. Internally, substiantial work was done to improve test coverage (End2End tests and performance tests)."
  * And a [video shows a new viewport visualisation](https://twitter.com/logorrr/status/1789428827828974030) of the current visible text in the box view on the left.
  * And did you know [you can play Snake with your logs](https://twitter.com/logorrr/status/1788685166547800300)?!
* **Sean Phillips** is on a roll again with a Twitter Thread with a lot of amazing 3D data visualizations.
  * "[Hi res Lunar elevation scans combined with hi res color spectrum imagery](https://twitter.com/SeanMiPhillips/status/1789361307361083527) (100m accuracy). Working on an easy to use Line Of Sight viz for Lunar surface assets. Mixing JavaFX and the NASA WorldWind Java SDK is a very powerful combination!"
  * "Borrowing from Worldwind's Line of Sight example I integrated a [JavaFX point \& click tool with hi res lunar elevation data/terrain](https://twitter.com/SeanMiPhillips/status/1790000502555451591). Upgraded the computations to run using #Java parallel streams. 100 LOS checks computed/rendered \< 1 second. Developed in a weekend."
  * "[Extended the lunar LOS tool by several orders of magnitude to check for performance](https://twitter.com/SeanMiPhillips/status/1790084437029327307). 10k LOS checks, 30k 3D artifacts added to the scene: 244 ms (including the rendering)... on a decent laptop. But Java is slow and JavaFX sucks and all those other foolish things you hear..."
* [Screenshot of an application by **BJ Dela Cruz**](https://www.linkedin.com/posts/bj-delacruz_javafx-fiji-buildinpublic-activity-7197088263570944000-gQfh?utm_source=share&utm_medium=member_desktop): "With my JavaFX app Flight Display, you can dream about landing in the beautiful island nation of Fiji. My dream of creating my own flight display board has come true!"
* [**Aman Singh** is excited to share his latest project](https://www.linkedin.com/feed/update/urn:li:activity:7196818552274767872/): "An Internet Download Manager built using JavaFX! With features like dynamic URL input, file management, and multi-threaded downloading, it's designed to streamline your downloading experience. [Check out the code](https://github.com/Aman298871/Internet-Download-Manager) and let me know your thoughts!"
* [**Pedro Duque Vieira**](https://x.com/P_Duke/status/1791442291070058732) shared a [video of Hero version 4 in action](https://youtu.be/P06dmx-SpkM?si=l6QkoY2fTQR-9sk7) which uses JMetro, FXThemes, and FXSkins.
* [**Christopher Schnick** shared](https://x.com/crschnick/status/1791903229397103051): "XPipe 9 comes with an integrated VNC client, written in JavaFX! Get it from [GitHub](https://github.com/xpipe-io/xpipe)".
* [**Emad Hanif** shared an update of this Barcodify application](https://www.linkedin.com/posts/emad-hanif-00b4aa227_javafx-activity-7201626665133785090-eHOS/): "It's been a great month working with JavaFX. By the way, the application is now faster and more memory efficient. Templates are generated in batches, columns are processed concurrently, and everything is merged into a single PDF — all while maintaining perfect alignment. Soon, will be sharing short video to demonstrate the magic!"
* [**Heshan Kariyawasam**](https://x.com/Heshantk) is [working on Libro](https://www.linkedin.com/posts/heshanthenura_java-javafx-library-activity-7200502273158885376-uak0/): Open Source Library Management App. The sources are on [GitHub](https://github.com/heshanthenura/Libro).
* [**Xiong Chun** shared a video](https://x.com/DaXiong008/status/1795857529894781252): "Since I started using EventBus in JavaFX, my UI element and content changes are entirely event-driven/data-driven, making development more streamlined. I've recently completed my Datacollie's interaction effect with selected tree node and the data displayed in the right-pane."

## Games

* [**Auron** started work on a new Pixelart Game project](https://x.com/WigglyGull/status/1791292994064396793): "There's lots of placeholder stuff right now but the main logic this there."

## Components, Libraries, Tools

* [**Pedro Duque Vieira** announced FXThemes Version 1.5](https://twitter.com/P_Duke/status/1786017713283817828) which introduces macOS support. Thanks to contributions by [**Carl Dea**](https://twitter.com/carldea). Read more in this [blog post](https://pixelduke.com/2024/05/02/fxthemes-version-1-5-released/).
* [**Dirk Lemmermann** announced support for search "history" in the SearchTextField control of GemsFX](https://twitter.com/dlemmermann/status/1788958790416556511). Available with release 2.17.0, thanks to [**Li Wang Yang**](https://twitter.com/LeeWyatt_7788).
* [**Carl Dea** shared a video](https://twitter.com/carldea/status/1747046776765284445), showing "A JavaFX background blur effect library for the MacOS is now able to support Light and Dark Mode!". You can find it on [GitHub](https://github.com/carldea/windowblur).
* [**Carl Dea** also published a lightweight JavaFX MVVM library "Cognitive"](https://x.com/carldea/status/1796344316618817798) on GitHub and Maven Central, with [a lot of info and examples here](https://github.com/carldea/cognitive/wiki).

## Podcast, Video, Books

* **Frank Delporte** [wrote a review of the book "Frontend Development with JavaFX and Kotlin"](https://webtechie.be/post/2024-05-06-book-review-javafx-kotlin/), written by **Peter Späth**.

## Tutorials

* A complete set of video tutorials by [**Tim Buchalka**](https://twitter.com/timbuchalka), all combined into a single YouTube video of more than five hours, yes indeed, 5 hours: [Java Programming Masterclass Updated To Java 17: JavaFX part 1](https://www.youtube.com/watch?v=YX9ad_9jtXQ)

## Miscellaneous

* [**Tobias Briones** entered the Matrix...](https://www.linkedin.com/posts/tobiasbriones_computerscience-javafx-art-activity-7190393026538536960-CZMY).
* [**DaShaun Carter** wants to create a JavaFX Chat Buddy](https://twitter.com/dashaun/status/1788735171514028142).
* [**Xiong Chun** shared a screenshot](https://twitter.com/DaXiong008/status/1786781960318439803): "JavaFX TreeView is very powerful. Completely meets my functional requirements. This technology selection of native desktop framework is still very successful so far."
* On the Twitter profile of [**Jodi Childress**](https://x.com/JodiChildrej) a long list of logo screenshots are posted which all seem to be generated with JavaFX during a computer programming class.
* Some JavaFX love on X ...
  * [**Xiong Chun**](https://x.com/DaXiong008/status/1792070496152076630): "Yep, JavaFX may take a good balance between cross-platform features and performance. In fact, I used Electron+Vue 3 to dev Datacollie. However, I found this architecture pattern, interactive experience and the performance are all not good enough for me. So, I switched to JavaFX."
  * [**Evander Torres**](https://x.com/EvanderTor57903/status/1792001252349346050): "I like JavaFX and Gluon not only because it has very proven and tested UI components but they are updated with the latest Java versions so you can take 100% advantage from Java."
  * [**Junior ADI**](https://x.com/caifyoca/status/1791530281058357650): "Done! JavaFX is seriously interesting. Give a try."
  * [**lucia scarlet**](https://x.com/luciascarlet/status/1793317192890507599): "If you're gonna use Java for the love of all that is holy use JavaFX instead of Swing because Swing apps are Distinctly Unpleasant to use (far, far more so than any Electron app). I would also look into Compose Multiplatform on Kotlin though."
  * [**Gerrit Grunwald**](https://x.com/hansolo_/status/1794086894008930686): "After more coding I think now it's time for the weekend... and again I realized that JavaFX is so productive... amazing."
  * [**Tanmay**](https://x.com/maytanthegeek/status/1794393624995885123): "Not gonna lie. I still love JavaFX. Android layout learned a lot from it. JavaFX is so fun and simple. I would really like to see it take a leading spot some day for GUI programming."
* And some Kotlin+JavaFX love ...
  * [**Daniel Zimmermann**](https://x.com/DystopianSnow/status/1793140611773554938): "To your dismay I have to tell you I write all my desktop applications using Kotlin and JavaFX..."
* **Dirk Lemmermann** has created a new repository for utility classes related to using the [Retrofit framework](https://square.github.io/retrofit/) (a type-safe HTTP client for Android and Java) in a JavaFX applications: "Obviously it is called [RetrofitFX (on GitHub)](https://github.com/dlsc-software-consulting-gmbh/RetrofitFX) and currently contains a total of two classes 🙂 Most importantly a class called ServiceInvocation which runs async server calls via Retrofit and then returns the result on †the JavaFX thread via various handlers that can be attached to the ServiceInvocation class. Hope some of you will find it useful ... although pretty specific."

## JFX Central

* New libraries
  * [FxForm2](https://www.jfx-central.com/libraries/fxform2) to easily creating forms.
  * ["ChartFX" for creating scientific charts](https://jfx-central.com/libraries/chartfx), thanks to [**Ralph J. Steinhagen**](https://www.jfx-central.com/people/r.steinhagen).
* [Message by **Dirk Lemmermann**](https://twitter.com/dlemmermann/status/1791084367718027559): "JavaFX library developers: if you haven't done so already, please add your library to JFX Central so that everybody can find it very easily. You can add it yourself [in this GitHub repository](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data)."
* The Links Of The Week are now also [available via RSS](https://www.jfx-central.com/lotw/rss.xml) thanks to contributions by [**Frank Delporte**](https://x.com/FrankDelporte) and [**Florian Kirmaier**](https://x.com/FlorianKirmaier).
