---
title: "JavaFX Links of July 2023"
slug: "javafx-links-of-july-2023"
date: "2023-07-28T07:50:36+00:00"
lastmod: "2023-07-28T07:54:07+00:00"
description: "Changes to the JavaFX core, new and enhanced applications, games, component libraries, tools, podcasts, videos, tutorials, and books!"
canonical: "https://webtechie.be/post/2023-07-28-javafx-links-of-july-2023/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/07/dockable.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

Although I skipped a few weeks because of busy schedules, holiday interruptions, and too few hours in a day, there was still a lot to report in the two #LinksOfTheWeek that were published on [jfx-central.com](https://www.jfx-central.com/) in July.

Have fun reading and clicking, and see you end of August for one long #LinksOfTheMonth...

Core {#h2-0-core}
-----------------

* Early-Access Builds of JavaFX 21 are available from
  * [jdk.java.net](https://jdk.java.net/javafx21/)
  * [gluonhq.com](https://gluonhq.com/products/javafx/)
  * *JavaFX 21-ea is designed to work with JDK 21-ea, but it is known to work with JDK 17 and later versions*.
* And JavaFX 22 Early-Access Builds, Build 1 is [also available](https://jdk.java.net/javafx22/)!

Applications {#h2-1-applications}
---------------------------------

* [**Mark Baird** wrote a post to describe how the beta release of the ArcGIS Maps SDK for Java (with ARM Linux support) can be used for IoT data recording apps](https://www.esri.com/arcgis-blog/products/sdk-java/developers/how-to-use-the-arcgis-maps-sdk-for-java-in-a-raspberry-pi-for-recording-gps-tracks/). He uses a GPS Unit, Raspberry Pi, Pi4J, and JavaFX.
* [**yos** shared a video demonstrating a text editor engine implemented in #JavaFX (without WebView)](https://twitter.com/yosbits/status/1676159349377617922): "I'm testing loading and scrolling performance on a huge 5.4MB of source code. Scrolling is possible at 100FPS or more in the Apple M1 environment. This is one of Snowflower Controls."
* [**Heshan Thenura Kariyawasam** shared the sources of Serial Port Monitor](https://twitter.com/Heshantk/status/1675453753863049216): "Monitoring data from a serial port with ease! This JavaFX app provides a user-friendly interface to set port and baud rate."
* [**Martin Paljak** is using NFC tags on PC](https://twitter.com/martinpaljak/status/1677216011391565824): "It should be as seamless as with mobile phones - tap\&go. My small #unix-style #Java utility got a Friday 07.07 release, together with #javafx desktop tray. QR codes, tag emulation etc. If you use NFC tags, give it a look/try."
* [**OrangoMango** is working on a logic simulator](https://twitter.com/orango_mango/status/1683512470466437121) with simple AND and NOT gates.
* [**Tobias Briones** is working on an example application and blog](https://twitter.com/tobiasbriones_/status/1680117927662501888): "Building Slides from Screenshots App in JavaFX".
* [**Sean Phillips** whipped up a Trinity easter egg for our DALLE demos](https://twitter.com/SeanMiPhillips/status/1679498134974595072). Each time services request a DALLE image, it is copied to network share. Walle Dalle scans that shared directory. Each time a new image file is copied it is animated into a spot in the 3D ring.
  * And on Mastodon he shared more [Trinity screenshots](https://foojay.social/@Birdasaur@jvm.social).
* [**Mark J Koch** is writing an application for EagleCAD schematics](https://mastodon.social/@maehem/110743557437273863): "Still lots of work to do (like view and print PC board files) but this is a wonderful milestone in my development of this app. Hope to open source it in about a month or two."
  * His first experiments [looked more like "generative art"](https://mastodon.social/@maehem/110708951041214410).

Games {#h2-2-games}
-------------------

* [**WhiteWoodCity** made the FXGL version of zombie vs plants open source](https://twitter.com/WhiteWoodCity/status/1676959035596341248) and shared the source code.
  * [**Almas Baim**: "Had a quick look, it's awesome!"](https://twitter.com/AlmasBaim/status/1677002981483347995)
* [**WebFX** made FoodDice, created by **OrangoMango** available online](https://twitter.com/WebFXProject/status/1676576239258042368). You can play it on [fooddice.webfx.dev](https://fooddice.webfx.dev/).
* [**Almas Baim** sees that FXGL is getting some interest on GitHub recently](https://twitter.com/AlmasBaim/status/1679096264988696576).The next release (21) will match Java / JavaFX 21. What would you like to see supported?
* [**OrangoMango** coded a game in 48h for the GMTK game jam](https://twitter.com/orango_mango/status/1678428149233639424). If you get to the boss, let him know (It could be difficult).

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* [**Carl Dea** is prototyping a new JavaFX-based Dockable windowing library](https://twitter.com/carldea/status/1675689706313379843): "I'm getting the mechanics down (more on L\&F / skins later)."
* [**Gerrit Grunwald** extended his Charts library with a "WaferMap"](https://twitter.com/hansolo_/status/1674799050225483779).
* [**Gluon** is launching GluonFX](https://techhub.social/@gluonhq/110786277544214779): "A growing set of JavaFX tools/components that we maintain on github, targeting all platforms. GPL license, free for developers. If you use them in a commercial product, we have licenses for that as well". See [gluonhq.com/introducing-gluonfx](https://gluonhq.com/introducing-gluonfx/).
  * [**Johan Vos** adds](https://twitter.com/johanvos/status/1684563837188739072): "Streamlining our software, adding consistency in approach, on github (e.g. badges, javadoc, maven approach), and licensing. Gradually, more of our software will be in this umbrella, and it will work on all platforms -- including mobile."

Podcast, Video, Books {#h2-4-podcast-video-books}
-------------------------------------------------

* [Jetbrains Java Annotated Monthly -- July 2023](https://blog.jetbrains.com/idea/2023/07/java-annotated-monthly-july-2023/) lists the ["Foojay Podcast #25: Game Development with Java, JavaFX, and FXGL"](https://foojay.io/today/foojay-podcast-25/).
* [**Frank Delporte** discovered that an image he created for his book "Getting Started with Java on the Raspberry Pi" with a JavaFX application](https://foojay.social/@frankdelporte/110790391834861143) is used in the 2nd edition of "The Definitive Guide to Modern Java Clients with JavaFX 17" by **Stephen Chin** , **Johan Vos** , **Jeames Weaver**, and many others.

Tutorials {#h2-5-tutorials}
---------------------------

* [**Mukul Saini** shared a video](https://www.youtube.com/watch?v=T4ftPFmis0E): "Property Binding in Javafx, How to bind one javafx property with another javafx property".
* [**Princy Victor**](https://twitter.com/princyvictor_16) wrote a tutorial about the use of ["JavaFX Timer"](https://www.educba.com/javafx-timer/).

Miscellaneous {#h2-6-miscellaneous}
-----------------------------------

* [**JetBrains** is advising how to build self-contained standalone applications](https://twitter.com/jetbrains/status/1674008633770737664).
* [**Geertjan Wielenga** started a "Twitter battle" about desktop versus web](https://twitter.com/GeertjanW/status/1674902079515631617): "Desktop applications are dead, history, a thing of the past, except maybe for some niche use cases. Yet... every time I open a browser, I'm in fact starting up... a desktop application."
* If you are looking for a #JavaFX community away from Reddit, please visit: [programming.dev/c/javafx](https://programming.dev/c/javafx), started by [**HamsterRage**](https://programming.dev/u/HamsterRage).
* [**Chad Preisler** shared](https://twitter.com/cpreisler/status/1683843706988683268): "If you're designing an API, take a look at JavaFX. It's built in a way that makes it easy for developers to extend. It's a really well designed API."
  * And he has been [working on a #JavaFX custom SortedList to overcome some performance issues](https://twitter.com/cpreisler/status/1683207830910693376) in couple applications. Links to the code are in the video description.
* [**Dirk Lemmermann** is wondering does everyone feel about another instance of the "JFX Days" this year](https://twitter.com/dlemmermann/status/1679935053541851160) ... in person ... in Zurich.
* [**CodeDead** shared screenshots comparing Caspian vs AtlantaFX vs Modena](https://twitter.com/C0DEDEAD/status/1677498043778605061).
  * And [managed to fully automate a GitHub Actions workflow for both testing and releasing JavaFX Applications](https://twitter.com/C0DEDEAD/status/1677397519066603528) (both portable and installers) using Gradle.
* [**Foojay** shared a list of its top 10 articles](https://twitter.com/foojayio/status/1681578734002864131): "JavaFX is well represented!"

JFX-Central {#h2-7-jfx-central}
-------------------------------

* Version 2 will let you [perform a name-based search for individual icons in 100+ icon packs](https://twitter.com/dlemmermann/status/1676201042168410113). **Dirk Lemmermann** shares screenshots showing the search result for "arrow left". This feature is brought to you by [**Lee Wyatt**](https://twitter.com/LeeWyatt_7788).
* Final bugs and testing are being handled before announcing V2!
