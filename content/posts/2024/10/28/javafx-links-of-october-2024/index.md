---
title: "JavaFX Links of October 2024"
date: "2024-10-28T18:19:34+00:00"
lastmod: "2024-10-29T12:16:01+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of October 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-10-28-javafx-links-of-october-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-september-2024"
  - "javafx-links-of-august-2024"
  - "javafx-links-of-july-2024"
  - "javafx-links-of-july-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of October 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month. With some very nice new content for JFX Central itself, see at end of the list...

Did we miss anything? Is there anything you want to have included in one of the next overviews?

Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* Message from **Kevin Rushforth** in the `openjfx-dev-request@openjdk.org` mailing list that shows how OpenJDK and OpenJFX keep being aligned thanks to the 6-months release cycle: "*I propose to remove support for running JavaFX applications with a security manager in JavaFX 24. Any JavaFX application that uses a security manager will necessarily need to use JDK 21.x LTS going forward, and thus can similarly use JavaFX 21.x LTS.* "
  * See [JDK-8341090](https://bugs.openjdk.org/browse/JDK-8341090): "The Java Security Manager was deprecated for removal in JDK 17 by [JEP 411](https://openjdk.org/jeps/411)."
* **Artur Skowronski** highlights in his [JVM Weekly newsletter](https://www.jvm-weekly.com/p/what-will-jdk-24-bring-jvm-weekly) some JavaFX facts:
  * *[JavaFX 23 announced by Kevin Rushforth](https://github.com/openjdk/jfx/blob/master/doc-files/release-notes-23.md) has hit the scene and brought with it a warning: "It's time to update." JavaFX has dropped support for older versions and will now require JDK 21.*
  * *Oracle has [announced that support for JavaFX in JDK 8](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), the last version they released, will end in March next year.*
* [**SamFx** happily announced the merge of his first pull request into the OpenJFX code](https://x.com/SamNaFX/status/1817528341194252682) to fix "*Push Notification fails on Android when app closed*."
* Just a tip: keep an eye on [github.com/openjdk/mobile](https://github.com/openjdk/mobile). It's a project to bring more mobile support to the OpenJDK. This will, once integrated, make it easier to support JavaFX on even more (mobile) platforms.
* In an article on The New Stack, ["End of the Road for JavaFX in JDK 8: Keeping Your Apps Alive"](https://thenewstack.io/end-of-the-road-for-javafx-in-jdk-8-keeping-your-apps-alive/), **Frank Delporte** alerts users of Oracle Java 8 builds with OpenJFX: "*From April 2025 and onward, if you are using JavaFX, you need to find an alternative distribution if you'd like to continue to receive security updates.* "
  * The same message on the [Azul Blog](https://www.azul.com/blog/azul-is-here-to-help-you-with-javafx-8-changes-in-2025/).

## SceneBuilder

* [**Gluon** shared a screenshot](https://techhub.social/@gluonhq/113236888001051050) of the number of downloads of Scene Builder: "*Note how fast the adoption of Scene Builder 23 is! Work on 24 is in full-speed now.*"

## Applications

* [**Arkutu** shared a video](https://x.com/i_am_arkutu/status/1841480908492128687): "*Progress so far building my healthcare desktop application using JavaFX, Spring Boot, and MySQL! So far, I've completed the login and main home page. Now, I'm diving into creating and fetching patient data from the database. Stay tuned for more updates.*"
* [Message from **Deep Netts - AI Java library**](https://x.com/DeepNetts/status/1842186222241825212): "*Download the Java Deep Learning Library now and start building intelligent applications today. Get started quickly with powerful tools for machine learning and neural networks!* "
  * Extract from the [News Release](https://www.deepnetts.com/deep-netts-3-1-1-has-been-released/): "*We've enhanced the debugging experience with new visualization tools that allow you to analyze weight statistics and explore network architectures in 3D space using JavaFX. These tools provide deeper insights into model behavior, helping you diagnose issues and optimize performance more effectively.*"
* Two releases by **Patrik Karlström** written in Java with JavaFX on the NetBeans Platform:
  * [CRIC 24.10](https://x.com/PatrikKarlstrom/status/1843362252004700202), the Custom Runtime Image Creator (with cross-targeting) for JDK jlink. [Get your copy at GitHub](https://github.com/trixon/cric/releases/tag/v24.10).
  * [Mapollage 24.10.06](https://x.com/PatrikKarlstrom/status/1842812263310205019), the photo kml generator for Google Earth. [Get your copy at GitHub](https://github.com/trixon/mapollage/releases/tag/v24.10.06).
* [**Christopher Schnick** shared a screenshot](https://x.com/crschnick/status/1843960201688371493) of the new icon chooser for selfhosted service icons in XPipe 12.
* [**Özkan Pakdil** shared](https://twitter.com/thejvmbender/status/1845012826827456523) a link to the [sources of swaggerific](https://github.com/ozkanpakdil/swaggerific) a Postman alternative, written in JavaFX and built with GraalVM.
* [**Stefano Fago** shared](https://twitter.com/stefanofago/status/1845178676477821298) links to [the sources](https://github.com/jtaccuino/jtaccuino) and [Devoxx presentation](https://youtu.be/SMD5g0Fqn34) of JTaccuino: "*a JavaFX based notebook application for Java developers. It's built for usages in education, interactive experimentation with algorithms \& possible more advanced use cases...*"
* [**BJ Dela Cruz** shared screenshots of his Flight Display application](https://www.linkedin.com/posts/bj-delacruz_seattle-tacoma-international-airport-departures-activity-7254534029104844801-gJCx/): "*It can generate a list of arriving or departing flights for an airport you choose.* " You can find the [sources here](https://bitbucket.org/bjpeterdelacruz/flight-display/src/main/).

## Games

* [**Marino** released a tiny unorthodox project](https://x.com/MarinoDev/status/1839806509254881340) from a year ago [on itch](https://marinodev.itch.io/wander-javafx): "*A dungeon crawler made in JavaFX, also featuring 1-bit pixelart. Based on Miziziziz's 'Roguelike in a day' video.*"

## Components, Libraries, Tools

* Shared by **Pedro Duke** :
  * [A video of part 6 of work in progress on Transit Theme](https://x.com/P_Duke/status/1841874974702776404): "*New LIGHT and DARK styles for TabPane and TextArea.*"
  * [Part 7](https://x.com/P_Duke/status/1844476360598991091) of the work in progress for the next release of the Transit Theme: "*New LIGHT and DARK styles for Menus, ContextMenu and Tooltip*."
  * "_Transit Theme next release, [PART 8](https://x.com/P_Duke/status/1847993884355277286): New LIGHT and DARK styles for Color Picker, Titled Pane and Accordion._"
* [**Carl Dea** published](https://x.com/carldea/status/1843165765446627745) a simple finite state machine library called [Axonic version 0.0.2](https://github.com/carldea/axonic): "*I plan to use it in complex UI/UX scenarios*".
* [Announced on Reddit: DevToolsFX](https://www.reddit.com/r/JavaFX/comments/1g12cz6/announcing_devtoolsfx/): "*Originally intended to fix long-standing bugs in Scenic View, it has been completely rewritten from scratch. Modern Java, reduced and more maintainable codebase with clear model/UI separation, only depends on javafx.controls,...*"
* [**Dirk Lemmermann** shared a screenshot](https://twitter.com/dlemmermann/status/1845793655056920772)= "*The email field in GemsFX now comes with an optional auto-completion feature for the domain part of the address.*"
* [**Steve Hannah**, the creator of jDeploy](https://x.com/shannah78/status/1849566060644872258) published a [blog post](https://jdeploy.substack.com/p/now-you-can-deploy-your-app-as-a): "*Now you can deploy your app as a DMG. The codesign process is painful, but if you really want to deploy your app as a DMG, you can do that now. ... some developers have expressed interest being able to deploy their apps as a .dmg, since this would be more familiar to users. Some developers also have their own Apple developer account, and would prefer to sign the app themselves rather than deploy using the jDeploy certificate. ...*"

## Podcasts, Videos, Books

* **Frank Delporte** published two new "JavaFX In Action" interviews:
  * [**Christoph Schwentker** about JabRef](https://webtechie.be/post/2024-10-01-jfxinaction-christoph-schwentker/), a tool written in Java and JavaFX to collect, organize, and discover literature for research projects.
  * [**Ulas Ergin** explains how his team at Credit Europe Bank uses JavaFX for two internal applications](https://webtechie.be/post/2024-10-22-jfxinaction-ulas-ergin/). The first one is a developer tool. Based on the experience of building this first tool, they decided to use JavaFX to migrate an old Swing-based application. The new Java application combines the old Swing screens with new React user interfaces, all nicely styled and integrated thanks to JavaFX.
* Two recordings from Devoxx Belgium talks:
  * [**Jose Pereda**](https://x.com/JPeredaDnr) and [**Sven Reimers**](https://x.com/SvenNB) launched [JTaccuino, a better Jupyter Experience for Java Developers](https://www.youtube.com/watch?v=SMD5g0Fqn34). You can find the [GitHub project here](https://github.com/jtaccuino/jtaccuino).
  * **Vik and Frank Delporte** demonstrated [MelodyMatrix: Looking at Music, an experiment with Kotlin, JavaFX, MIDI, and Virtual Threads](https://www.youtube.com/watch?v=xbaLjoTU49I). You can find the [application website here](https://melodymatrix.rocks/).
* [**Adam Bien** had a short talk with **Johan Vos** at Devoxx](https://www.youtube.com/shorts/5LhUtEDPf5E).

## Tutorials

* **Frank Delporte** published a new tutorial ["Template project to build a JavaFX application as a JAR with dependencies with Maven"](https://webtechie.be/post/2024-10-02-javafx-maven-jar-template/) with the source code on GitHub, full explanation in the blog, and a video walk-through on YouTube.
* [**Loïc Lefèvre** hopes we like](https://x.com/Loic__Lefevre/status/1848390393252987106) this post, and sure we do ;-): "*Finally finished [this post about creating native images for JavaFX applications using GraalVM](https://medium.com/db-one/building-javafx-app-native-image-with-graalvm-new-achievement-unlocked-c5e236ecf11d). There is a [GitHub repository](https://github.com/loiclefevre/javafx-native) at the end.* " Intro from the blog post: "*TL;DR — Building JavaFX application native image used to require GluonFX plugins and GluonHQ substrate VM. In this post, I show it is possible to rely on the latest GraalVM distribution only. This simplifies greatly the process but requires some challenges to be solved first... are you ready to read my journey?*"

## Miscellaneous

* Some JavaFX love on Twitter:
  * [**SAI CHOWDARY** shared some key aspects of Java in front-end development](https://x.com/im_saichowdary/status/1842032245277696306)
  * Reaction by [**Bhanu**](https://x.com/ABprakash01/status/1842069633542848813): "*That's great info! JavaFX is indeed a powerful tool for creating dynamic desktop applications with impressive UI features like graphics and animations. It's a game-changer for front-end development!*"
  * In a long thread, [**21st Computer Tech**](https://x.com/21stCompTech/status/1839657037896056852) compares Java and Python GUIs with this nice remark about Community \& Support: "*Java has a large and active community for both Swing and JavaFX, with extensive documentation and examples available online.*"
* Blog post by **Frank Delporte** : [End of the Road for JavaFX in JDK 8: Keeping Your Apps Alive](https://thenewstack.io/end-of-the-road-for-javafx-in-jdk-8-keeping-your-apps-alive/): "*Oracle will end support for JavaFX in JDK 8 next March and will stop providing Java 8 builds with OpenJFX included, as explained in the Oracle Java SE Support Roadmap. This means that from April 2025 and onward, if you are using JavaFX, you need to find an alternative distribution if you'd like to continue to receive security updates.*"
* [**Homebookner** is looking for help](https://twitter.com/Heimbuchner_J/status/1845451366484914516): "*I'am currently implementing a simple workspace control. I'm using splitpanes for the content, but when hiding and showing content in the splitpane, the divider jumps around. It seems not to happen, if the system setting for screen zoom is at 100%. Ideas?*"
* [AI is coding and controlling the computer for **Stephan Janssen**](https://x.com/Stephan007/status/1848775499578794017): "*An experimental AnthropicAI Claude agent takes control of my screen, autonomously doing whatever is needed to get the job done. ... Now develops the same but using JavaFX (which it needs to install) and run 🔥🧠.*"

## JFX Central

* The overview of the links of September got [published on Foojay.io](https://foojay.io/today/javafx-links-of-september-2024/).
* New content:
  * People: [Clément de Tastes](https://www.jfx-central.com/people/c.detastes), Java \& JavaFX enthusiast, Quarkus FX contributor, Tech writer and speaker.
  * Library: [QuarkusFX](https://www.jfx-central.com/libraries/quarkusfx), a Quarkus extension that allows you to use JavaFX in your Quarkus application.
  * Video: [JavaFX In Action with Christoph Schwentker about JabRef](https://www.jfx-central.com/videos/-ddFxwh2U6E)
  * Video: [JavaFX In Action with Ulas Ergin about internal banking apps](https://www.jfx-central.com/videos/djbC7zWc-2w)
  * Learn: [Template project to build a JAR with dependencies with Maven](https://www.jfx-central.com/learn-javafx/fat-jar)
  * Learn: [Using JavaFX in your Quarkus application](https://www.jfx-central.com/learn-javafx/quarkusfx)
