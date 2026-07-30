---
title: "JavaFX Links of November 2023"
slug: "javafx-links-of-november-2023"
date: "2023-11-30T08:23:00+00:00"
lastmod: "2024-01-26T08:53:42+00:00"
description: "Have fun with this overview of the \"JavaFX LinksOfTheWeek\" that got published on jfx-central.com during November."
canonical: "https://webtechie.be/post/2023-11-24-javafx-links-of-november-2023/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/11/F97tAeWagAAEk0K.jpeg"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-october-2023"
  - "javafx-links-of-september-2023"
  - "javafx-links-of-august-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

Have fun with this overview of the "JavaFX LinksOfTheWeek" that got published on [jfx-central.com](https://www.jfx-central.com/) during November!

Core {#h2-0-core}
-----------------

* **Carl Dea** shared: "JavaFX 22 will be supporting platform specific settings! E.g. dark and light mode etc.". See [JDK-8319138: Platform preferences API](https://bugs.openjdk.org/browse/JDK-8319138).
  * Also worth following: [JDK-8305116](https://bugs.openjdk.org/browse/JDK-8305116): "Frosty / Milky / diffuse transparency areas showing desktop background"
* **Dirk Lemmermann** is [looking forward to the next release of JavaFX](https://twitter.com/dlemmermann/status/1724707717468582014): "We can hope to see CSS transitions, themes, and Platform API (dark mode, accent colors)."
* JavaFX Wish Lists by:
  * [**Dirk Lemmermann**](https://twitter.com/dlemmermann/status/1724709050670014678): removal of AWT, a tray API (tray icon),...
  * [**LeeWyatt**](https://twitter.com/LeeWyatt_7788/status/1724811343767978350): support for SVG Icons and Animations, Responsive Layout Pane/Adaptive Layout Pane
  * [**Thanhpv**](https://twitter.com/realThanhpv/status/1724995261305630972): clipping improvement
  * And others in the same thread...
* **Pedro Duque Vieira** shared ["Ongoing work to add CSS transitions to JavaFX"](https://twitter.com/P_Duke/status/1724761488714240432).

Applications {#h2-1-applications}
---------------------------------

* [**Pedro Duque Vieira** shared screenshots](https://twitter.com/P_Duke/status/1720073696541602138) of the initial prototype of HERO (a JavaFX app in production) and screenshots of the app after its redesign and implementation. He also shared some quick remarks about the redesign.
* [**OrangoMango** shared an impressive video of 3D rendering](https://twitter.com/orango_mango/status/1719446338503671953): "Now the camera is always pointing to the airplane and it can also freely move. It's on the Raspberry Pi and with this resolution it lags a bit with the screen recorder."
* [**Serendipity** is proud](https://twitter.com/SerendigityInfo/status/1718268583208219073)! "Yes, we did it! A JavaFX APP is first in the Apple Store ranking for macOS! SmartFinder number one! Another small step for all Java lovers. JavaFX write once run anywhere."
* **Robert Ladstätter** announced v23.3.0 of LogoRRR, the tool to analyze log files and filter out critical events or other points of interest: "[Successfully resolved a significant performance issue in LogoRRR. This enhancement is the highlight of version 23.3.0](https://twitter.com/logorrr/status/1720139416478351834)".
* **RNArtist** announced the availability of RNArtist 1.1.3 with the ability to define a zooming area with the left mouse button. More info and a video demo is [available here](https://github.com/fjossinet/RNArtist/discussions/25).
* [**Randil Hasanga** shared screenshots of Book Nook](https://www.linkedin.com/posts/h4-z4_stockportfolio-inventorymanagement-javafx-activity-7128600932510007298-1fpO/), a Stock Portfolio Management System.
* [**Tharindu Nuwan Madhushanka** unveilled a Media Player App and a link to the sources](https://www.linkedin.com/posts/tharindu-nuwan-madhushanka-00b689251_java-javafx-mediaplayer-activity-7128595798606450689-MV31): "From crisp audio playback to stunning video performance, this app sets a new standard".

Games {#h2-2-games}
-------------------

* [**GZYangKui** shared some tweets](https://twitter.com/YangKui7) while working on games and successfully resolved an [audio output delay issue](https://twitter.com/YangKui7/status/1722603653989556386).
* **OrangoMango** has been very active!
  * [Tetris in the browser](https://orangomango.itch.io/tetris), but also for Windows, Linux, macOS, and of course Raspberry Pi.
  * [RailTheWay 2.0](https://twitter.com/orango_mango/status/1725496263540343228): "Major update! Guide the trains to their station and avoid collisions."
  * [FlightSimulator](https://twitter.com/orango_mango/status/1724403696660894083), exclusively using the JavaFX canvas.
  * ["Update video about my Minecraft clone made with my 3D engine from scratch.](https://twitter.com/orango_mango/status/1723077440912674834)

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* [**Dirk Lemmermann**](https://twitter.com/dlemmermann/status/1719746602825297991): "[AtlantaFX](https://www.jfx-central.com/libraries/atlantafx) is currently my theme of choice. It is modern looking, very elegant, very professional, well documented, supports various modes (yes, dark mode and darcula, too), comes with an auto-updating sampler app, custom skins, much simpler styling rules than Modena, etc ... if you haven't done so, yet, please [check out the repo](https://github.com/mkpaz/atlantafx)."
  * [V1.90.0 of GemsFX](https://twitter.com/dlemmermann/status/1720475150032810405), which includes an SVGImageView control to display SVG files. The control uses the [jsvg project](https://github.com/weisJ/jsvg) to render to an AWT image, convert it, and then display it inside an ImageView.
  * Dirk also announced the release of a new JavaFX input field for phone numbers which is utilising the libphonenumber project from Google to format and validate the numbers. The project can be found [on GitHub](https://github.com/dlsc-software-consulting-gmbh/PhoneNumberFX). The main contributor is [**Gabriel Diaz**](https://twitter.com/gldiazcardenas).
  * Version 1.5.0 of that library [has been refactored and greatly simplified](https://twitter.com/dlemmermann/status/1727262578551009318).
* **Mushtak Abdulqadir** describes several libraries in a LinkedIn post (BootstrapFX, ControlsFX, FormsFX, FXGL, Ikonli, TilesFX, ValidatorFX) and [is asking for feedback](https://www.linkedin.com/posts/altmemy_javafx-uiux-desktopdevelopment-activity-7127725400717209600-ozbs/): "Which of these libraries have you used, and what has been your experience with them?"
* [Announced by **Abhinay Agarwal**](https://twitter.com/iAbhinay/status/1723642698585059640): release of ControlsFX v11.2.0! Baseline has been moved to JavaFX 17 and is compatible with JavaFX 21. The [release notes can be found here](https://github.com/controlsfx/controlsfx/releases/tag/11.2.0) and [JavaDoc here](https://controlsfx.github.io/javadoc).
  * And he is looking for advise: ["Which JavaFX control would you recommend to read very large text files?"](https://twitter.com/iAbhinay/status/1723647392908751267).
* [V4.0 of openglfx](https://github.com/husker-dev/openglfx) has been released. This library adds a new element to JavaFX to render OpenGL graphics. It is optimized for each platform and includes some auxiliary functions for working with OpenGL from JavaFX.

Podcast, Video, Books {#h2-4-podcast-video-books}
-------------------------------------------------

* At J-Fall in the Netherlands, **Frank Delporte** had a lot of live interviews with speakers and guests, including **Gerrit Grunwald** . In [this short interview, they also talk about JavaFX](https://www.youtube.com/watch?v=FV1ITrl42mk).
* On airhacks.fm, the podcast by **Adam Bien** : [How **Gerrit Grunwald** wrote SteelSeries](https://adambien.blog/roller/abien/entry/how_han_solo_wrote_steelseries). "A podcast episode about pixel perfect Java user interfaces, JavaFX, and Java's portability".

Tutorials {#h2-5-tutorials}
---------------------------

* **Edward Nyirenda Jr** again published several tutorials, for instance:
  * [JavaFX Lighting Effect](https://coderscratchpad.com/javafx-lightin): "The Light.Spot represents a point light source with characteristics similar to a spotlight."
  * [JavaFX Phone Number Input Field](https://coderscratchpad.com/javafx-phone-number-input-field/): using the library "PhoneNumberFX" that just got released.

Miscellaneous {#h2-6-miscellaneous}
-----------------------------------

* [Screenshot by **Rumble Tumble Kid** of the minimal code to run ScalaFX](https://twitter.com/rumbletumblekid/status/1719376674939101576).
* **Carl Dea** is wondering if anyone is [interested in Java 21.0.1 \& JavaFX 21.0.1 Scenic View](https://www.linkedin.com/posts/carldea_upgraded-to-gradle-84-jdk-2101-openjfx-activity-7131115551858282496-9sM-/).
* **Heshan Kariyawasam** is working on an [example project to build JavaFX executables](https://github.com/heshanthenura/JavaPackageDemo) for different platforms with Gradle, jpackage, and GitHub Actions.
* **OrangoMango** is [visualizing prime numbers](https://twitter.com/orango_mango/status/1727340684498383337) with JavaFX. [The source code is on pastebin](https://pastebin.com/u0nzj4bT).

JFX Central {#h2-7-jfx-central}
-------------------------------

* A new version has been released with several utilities / tools to develop JavaFX applications: gradient editor, SVG path extractor, effects editor, pixel-to-em converter, CSS playground. [You can find the utilities here](https://jfx-central.com/utilities).
* "Random" sorting will be added to the category pages so that every item gets the same chance to be seen or found.
* [**Dirk Lemmermann** shared a video](https://twitter.com/dlemmermann/status/1719751012318052444) showing the superfast startup of the JFX Central app, as a native app, thanks to Gluon.
* The summary with all the JFX Central links of October got [published on Foojay.io](https://foojay.io/today/javafx-links-of-october-2023/).
