---
title: "JavaFX Links of December 2023"
slug: "javafx-links-of-december-2023"
date: "2023-12-30T13:11:51+00:00"
lastmod: "2023-12-30T13:11:53+00:00"
description: "Thanks for following these updates and looking forward to more of your JavaFX work in the new year!"
canonical: "https://webtechie.be/post/2023-12-22-javafx-links-of-december-2023/"
authors:
  - "frankdelporte"
image: "Screen-Shot-2021-05-17-at-12.15.51-AM.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-november-2023"
  - "javafx-links-of-october-2023"
  - "javafx-links-of-september-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

This is the final JavaFX LinksOfTheMonth review for 2023.

It was an amazing year with many evolutions in Java and JavaFX and a complete "fresh" version of JFX Central.

Thanks for following these updates and looking forward to more of your JavaFX work in the new year...

This is the overview of the LinksOfTheWeek that got published on [jfx-central.com](https://www.jfx-central.com/) during December.

## Core

* JavaFX 22 Early-Access Builds, Build 19 (2023/11/24), is available on [jdk.java.net/javafx22](https://jdk.java.net/javafx22/).
  * Issues addressed in [Build 18](https://bugs.openjdk.org/issues/?jql=project%20%3D%20JDK%20AND%20fixversion%20%3D%20jfx22%20and%20component%20%3d%20javafx%20and%20%22resolved%20in%20build%22%20%3d%20b18%20order%20by%20component%2C%20subcomponent) and [Build 19](https://bugs.openjdk.org/browse/JDK-8319996?jql=project%20%3D%20JDK%20AND%20fixversion%20%3D%20jfx22%20and%20component%20%3D%20javafx%20and%20%22resolved%20in%20build%22%20%3D%20b19%20order%20by%20component%2C%20subcomponent).
  * JavaFX 22-ea is designed to work with JDK 22-ea, but it is known to work with JDK 17 and later versions.
* [**Dirk Lemmermann** is happy](https://twitter.com/dlemmermann/status/1731945181056803237) with a [pull request in OpenJFX](https://github.com/openjdk/jfx/pull/1293) "that finally adds styling support to the fitWidth / fitHeight / preserveRatio / smooth properties of the ImageView class. I always wondered why they were missing. Turns out they were simply forgotten (but a TODO was in the source code :-))."
* A little [insight into the OpenJFX project by **Johan Vos**](https://mastodon.social/@johanvos/111596707584064200): "We often get requests about 'Why don't you provide a JavaFX build for this or that platform?' The short answer: it's easy to create a one-time build with some parameters, but it's hard to maintain. And expensive. Our AWS bill is impressive. And since all our OpenJFX builds are available for free, this is really a major cost for us."

## Applications

* **RNArtist** is doing amazing user interface work... This is only [one of his recent messages](https://twitter.com/rnartist_app/status/1730530572227764240): "If the RNA 2D layout selected at first doesn't fit your needs, RNArtist will provide you a new panel to compute and preview alternate layouts."
* An impressive and super-smooth first [timeline management demo](https://twitter.com/Alessio_Vinerbi/status/1730177673316503598) of the personal After Effects app by **Alessio Vinerbi** .
  * A [video by **Alessio Vinerbi**](https://twitter.com/Alessio_Vinerbi/status/1730630540569522287) introducing the first time interpolation demo of MoonsonFX, his personal After Effects written entirely in JavaFX.
  * A new teaser [video by **Alessio Vinerbi** of MoonsonFX](https://twitter.com/Alessio_Vinerbi/status/1733534005792805267), a JavaFX animation tool.
* **Donald Raab** uses the [Jackson library to persist his JavaFX ToDo List to JSON](https://twitter.com/TheDonRaab/status/1732273493092524526).
* **Divyanshu Yadav** is working on a weather app: ["Dear Web Devs! JavaFX is also in the race!!"](https://twitter.com/DVyadav2307/status/1731645629536124981).
* **Heshan Kariyawasam** shared some nice projects again!
  * A [JavaFX-based statistical calculator](https://www.linkedin.com/feed/update/urn:li:activity:7139904558327099392/): "Quickly compute mean, median, mode, and variance with an intuitive user interface."
  * A [Dynamic Background Demo](https://github.com/heshanthenura/DynamicBackground) that dynamically adjusts the background image to fit the screen dimensions. It utilizes JavaFX's Stage and ImageView components to create a responsive background. The app also includes functionality to open a new window, demonstrating basic window management in JavaFX. The video shows a cool use of multiple windows in one application.
  * And on Foojay.io, his first blog was published: [Creating Executables For JavaFX Applications](https://foojay.io/today/creating-executables-for-javafx-applications/): "Let's take a look in this article at the current state of what can be done with jpackage and GitHub Actions to create executables for JavaFX applications."
* **Patrik Karlström** is [updating rsync GUI, an application he wrote in 2015](https://twitter.com/PatrikKarlstrom/status/1737534168043552769): "Today I ran my first #backup with the next version based on NetBeans Platform and JavaFX. Still very rough around the edges though."

## Games

* **Almas Baim** is working on new features for FXGL:
  * "The current version of the FXGL engine can only show up to 5 dialogue options. The next version will use a scroll bar, where needed, to show any number of options, [as can be seen in the demo](https://twitter.com/AlmasBaim/status/1730336323750224104)."
  * "Adding NPCs that can act as companions somehow instantly makes it [more interesting to explore the world](https://twitter.com/AlmasBaim/status/1730000612777976047)."
  * [**Almas Baim** shared a new proof-of-concept of the node inspector panel](https://twitter.com/AlmasBaim/status/1734865416697114945) in FXGL, which allows editing various node properties: "This way each dialogue node in the main UI just captures the text associated with it and reduces visual clutter".
* **OrangoMango** published a [trailer of RailTheWay on YouTube](https://www.youtube.com/watch?v=OAunQTTsbio): "A game made in Java/JavaFX without the use of any game engine. Guide the trains to their station. Each train has a color that represents its destination. Sometimes there are some cargo trains that disturb you, try to let them go away and be sure to avoid the cars on your way!"
* [**Sulaimon Muhammad** shared a brief video](https://twitter.com/SulaimonMuhamm9/status/1731418084617933140) of the "Who Wants to Be a Millionaire" game developed using JavaFX and Hibernate: "Exciting things happening with Java!"
* [**London Softworks**](https://twitter.com/LondonSoftworks) is building a custom 2D game engine in Java and shares the progress in various tweets.
* **Frank Delporte** published ["A JavaFX Game Application in a Single Java File with JBang and FXGL"](https://webtechie.be/post/2023-12-14-jbang-fxgl/). A video walk-through together with **Almas Baim** will follow soon...

## Components, Libraries, Tools

* **Gerrit Grunwald** is [bumping his libraries to JDK 21](https://twitter.com/hansolo_/status/1729494468309500362).
* PhoneNumberFX by **Dirk Lemmermann** got extended: "The field can now automatically create prompt texts showing you example phone numbers for the selected country", [and more](https://twitter.com/dlemmermann/status/1728080959520526807).
* yWorks, a software library for visualizing, editing and analyzing graphs, [shared their newest update](https://twitter.com/yworks/status/1732707567346872829) which is compatible with the latest JDKs.

## Podcast, Video, Books

* In the [third part of the Foojay J-Fall Report Podcast](https://foojay.io/today/foojay-podcast-36/), [**Gerrit Grunwald**](https://www.jfx-central.com/people/g.grunwald) and [**Anthony Goubard**](https://twitter.com/Anthony_Goubard) talk about desktop application development with Java.

## Tutorials

* On Foojay, part 1 of a series by **Christopher Schnick** , the creator of [XPipe](https://www.jfx-central.com/showcases/xpipe), was published: ["Java for desktop applications: Tips and Tricks"](https://foojay.io/today/java-for-desktop-applications-part-1/).
* [**shiratsuyudachi** asked ChatGPT](https://twitter.com/shiratsuyudachi/status/1734874003100364858) to create a minimal JavaFX application.
* A new tutorial by [**Dave Barrett**](https://twitter.com/Polypragmatist): "An [introduction to ListView](https://www.pragmaticcoding.ca/javafx/elements/listview-basics) and understanding how to use it to display something more than just lists of Strings."

## Miscellaneous

* **Graham Billington** is looking for help: "What the hell is [happening with this light and how do I make it stop](https://twitter.com/GBillington7/status/1729908761958215950)?"
* **Jonathan Giles** is sharing [good memories to when he was working on JavaFX at Sun](https://twitter.com/JonathanGiles/status/1729774535354794466).
* **Heshan Thenura** shared a GitHub repository that shows how to create [JavaFX executables with jpackage for all platforms](https://github.com/heshanthenura/JavaPackageDemo).
* **Heshan Thenura** 's screen got invaded by a little Samurai walking on his task bar. A [fun demo project](https://github.com/heshanthenura/DesktopCompanion) using animated GIFs and a transparent stage.
* **Michael Schnell** created a [JavaFX CDI Example](https://github.com/fuinorg/javafx-cdi-example) project (Java 17 with "jakarta" namespace, Maven build, GitHub build, Sub controllers, Blocking UI, About dialog, Exception dialog, TestFX unit test). There is also a [JavaFX CDI Archetype](https://github.com/fuinorg/javafx-cdi-archetype) based on it that allows easily bootstrapping your own project.
* [**Divyanshu Yadav** needs some serious UI suggestions](https://twitter.com/DVyadav2307/status/1735285920885277156)...
* **Dirk Lemmermann** now has an ["official JavaFX" office](https://twitter.com/dlemmermann/status/1734941451971182773).
* [**Gail Anderson** has a call for all JavaFX developers](https://twitter.com/gail_asgteach/status/1736836965683892661): "Please let the GraalVM team know that JavaFX and mobile is important by [filling out this survey](https://graalvm.typeform.com/survey?typeform-source=t.co)."

## JFX Central

* The bundled overview of the JavaFX Links Of The Week of November, [got published on Foojay.io](https://foojay.io/today/javafx-links-of-november-2023/).
* The main menu of JFX Central got reworked as we ran out of space, because... we added a complete new section: "Learn"!
  * You can already find a few new pages with content provided by [**Edward Nyirenda Jr.**](https://www.jfx-central.com/people/e.nyirenda), [**Gail Anderson**](https://www.jfx-central.com/people/g.anderson), and [**Frank Delporte**](https://www.jfx-central.com/people/f.delporte).
  * In the coming weeks, months,... we will be adding more of this, including new videos.
  * [Learn JavaFX](https://www.jfx-central.com/learn-javafx): from getting started with JavaFX to all the components that are available. This will become the largest section.
  * [Learn JavaFX for Mobile](https://www.jfx-central.com/learn-mobile): how to create applications for Android and iPhone.
  * [Learn JavaFX on Raspberry Pi](https://www.jfx-central.com/learn-raspberrypi): build user interfaces on the Raspberry Pi to interact with electronic components.
  * Thanks to [**Florian Kirmaier**](https://www.jfx-central.com/people/f.kirmaier) and [**Li Wang Yang**](https://twitter.com/LeeWyatt_7788) for the integration of this new section in JFX Central and some required new features in JPro!
  * Several tutorials were added to the "Learn JavaFX" section a week later, with content provided by [**Edward Nyirenda Jr.**](https://www.jfx-central.com/people/e.nyirenda):
  * [CheckBox](https://www.jfx-central.com/learn-javafx/checkbox)
  * [Chart](https://www.jfx-central.com/learn-javafx/chart)
  * [GridPane](https://www.jfx-central.com/learn-javafx/gridpane)
