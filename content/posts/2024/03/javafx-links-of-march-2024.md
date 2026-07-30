---
title: "JavaFX Links of March 2024"
slug: "javafx-links-of-march-2024"
date: "2024-03-31T08:39:00+00:00"
lastmod: "2024-04-03T06:43:19+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of March 2024, published on jfx-central.com during this month."
canonical: "https://webtechie.be/post/2024-03-29-javafx-links-of-march-2024/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-march-2023"
  - "javafx-links-of-february-2024"
  - "javafx-links-of-january-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of March 2024, published on [jfx-central.com](https://www.jfx-central.com/) during this month.

Components, Libraries, Tools {#h2-0-components-libraries-tools}
---------------------------------------------------------------

* **Pedro Duque Vieira** integrated this [pull request in FXThemes](https://github.com/dukke/FXThemes/pull/2): "[True Dark Mode is now also available on Mac](https://twitter.com/P_Duke/status/1762488686313120185). All thanks to [**Carl Dea**](https://twitter.com/carldea).
* Not new, but [**siedlerchr** pointed](https://twitter.com/siedlerchr/status/1762439783236636842) us on [EasyBind](https://github.com/tobiasdiez/EasyBind): "Leverages lambdas to reduce boilerplate when creating custom bindings, providing a type-safe alternative to Bindings.select\* methods and provides enhanced bindings support for Optional." It's a fork by [**Tobias Diez**](https://twitter.com/tobias_diez) of an older project by [**Tomas Mikula**](https://twitter.com/tomas_mikula).
* This month Java 22 and JavaFX 22 got released! Here are the [release notes of JavaFX 22](https://github.com/openjdk/jfx/blob/master/doc-files/release-notes-22.md), including:
  * An important change for animations ([JDK-8324658](https://bugs.openjdk.org/browse/JDK-8324658)): the Animation methods play, start, stop, and pause may now be called on any thread.
  * 8 enhancements
  * 80 fixed bugs
  * 3 security fixes
* You can download JavaFX 22 here:
  * As a separate SDK from the [Gluon website](https://gluonhq.com/products/javafx/).
  * Or included with a Java JDK from, e.g., the [Azul website](https://www.azul.com/downloads/?version=java-22-sts&package=jdk-fx#zulu).
* Release highlights of JavaFX 22 can [be found here](https://openjfx.io/highlights/22/).
  * [**Abhinay Agarwal** highlights this one](https://twitter.com/iAbhinay/status/1770347967536419186): "This release includes 'Platform preferences API' which allows developers to style their apps in accordance with the appearance of the OS."
* **Kevin Rushforth** published a [description of JavaFX Incubator Modules](https://github.com/kevinrushforth/jfx/blob/javafx.incubator/INCUBATOR-MODULES.md).
  * [**Pedro Duque Vieira** explains](https://twitter.com/P_Duke/status/1770840921753502170): "These new modules will exist in the JavaFX SDK. They'll be the home of features that are still under review, to possibly later be included as final stable features. One such feature will likely be the Rich Text Area."
* In the Oracle Java 22 Launch Stream, [Kevin Rushforth talked about "JavaFX 22 and Beyond"](https://www.youtube.com/watch?v=AjjAZsnRXtE&t=4685s).
* [JavaFX 23 Early-Access Builds](https://jdk.java.net/javafx23/) are available.

Applications {#h2-1-applications}
---------------------------------

* [**Patrik Karlström** announced the release of Mapollage 3.0.0](https://twitter.com/PatrikKarlstrom/status/1762215364845244834): "3 yrs later, on the very day! The KML generator of geotagged images for GoogleEarth. This is a major rewrite of the UI, combining NetBeans Platform and JavaFX." [Sources and downloads are available on GitHub](https://github.com/trixon/mapollage/releases/tag/v3.0.0).
* [**Matt Coley** shared a video](https://twitter.com/invokecoley/status/1764991484808380927): "Re-creating Intellij's "Search Everywhere" panel in Recaf 4X".
* [**Pedro Duque Vieira** announced version 4 of HERO](https://twitter.com/P_Duke/status/1767940438177055123), a tool which provides next-generation energy modelling capabilities. Here are the [release notes](https://blog.hero-software.com.au/hero-v4-0-released-whats-new/).
* [A video of LogoRRR displaying data more densely](https://twitter.com/logorrr/status/1768167973276057712), "and a tad nicer."
* JabRef immediately [bumped to version 22](https://foojay.social/@jabref/112124226121637837): "We just updated to the newest version of JavaFX 22, and it works fine so far! Great to see so many bugs fixed!"
* **Carl Dea** shared "a [glimpse of a responsive layout for a landing page](https://twitter.com/carldea/status/1768673053213347911). Clinical interface terminology system (knowledge base)."

Games {#h2-2-games}
-------------------

* One of the students of **Almas Baim** built a [dungeon layout generator for FXGL](https://twitter.com/AlmasBaim/status/1770767408896110958): "Once the API is finalised, it will be available from the next release."
  * He also shared a [video of a rotating cube with rotating cubes with rotating cubes with...](https://twitter.com/AlmasBaim/status/1768709121136599320)
  * And [another one video](https://twitter.com/AlmasBaim/status/1770842307169857714): "In the next release of FXGL the pathfinding API treats all grid based data structures in the same way. This means mazes, dungeons, maps, levels, including custom types, are all easily traversable by entities using pathfinding."
* [**Almas Baim** announced version 21.1 of FXGL](https://twitter.com/AlmasBaim/status/1772682076673782087):
  * new module fxgl-intelligence: speech recognition, hand tracking, text to speech
  * mazes/dungeons/grids can utilise A\* pathfinding up to 8 directions
  * completed QuestService implementation
  * [Full change log, with 5 new contributors, is here](https://github.com/AlmasB/FXGL/releases/tag/21.1)
  * And he published a [video on YouTube: "FXGL 21.1 Tutorial: Speech Recognition in Java"](https://www.youtube.com/watch?v=hdPspgAetQk)
* [**OrangoMango** announced version 2.0 of Reflection](https://twitter.com/orango_mango/status/1771786912363131183): "Can you beat all the current 30 levels? New levels, new mechanic, bugfix, new textures, android version and much more. [Play it in the browser or download it](https://orangomango.itch.io/reflection) for Windows, Linux, Mac or Android."

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* **Dirk Lemmermann** has been very productive this week...
  * Added a [few more features to the DrawerStackPane in GemsFX](https://twitter.com/dlemmermann/status/1770757809698697560): "You can now configure the animation duration and the top and side paddings."
  * Added [field validation via ValidatorFX to the dialog framework in GemsFX](https://twitter.com/dlemmermann/status/1770415222815092963): "Will be part of next release."
  * Added a ['PowerPane' to GemsFX](https://twitter.com/dlemmermann/status/1770136937044541910): "... the mother of all panes ... combining a glass pane, a drawer pane, hidden panes, dialog pane, info center (notification) pane. Basically something that will give you a great quick-start when creating a new app in JavaFX." Check the thread on Twitter for more screenshots.
  * "Implemented my own ['Friday Fun Component'](https://twitter.com/dlemmermann/status/1768640804820488349) now that [**Gerrit Grunwald**](https://twitter.com/hansolo_) is mostly on [CRaC](https://docs.azul.com/core/crac/crac-introduction) 🙂 We needed an 'energy efficiency' display for our energy software at [Senapt](https://twitter.com/SenaptEaaS)."
* And and update by **Pedro Duque Vieira** : "Work in progress (continued - very close to finished): [Navigation Control](https://twitter.com/P_Duke/status/1770112311300083931). When shrunk, show popup menus when clicking items - scroll bar shows when items exceed space. And animations when shrinking and expanding."

Podcast, Video, Books {#h2-4-podcast-video-books}
-------------------------------------------------

* A [new version of the ebook by **Frank Delporte**](https://foojay.social/deck/@frankdelporte/112007776688012320), "Getting Started with Java on the Raspberry Pi", is [now available here](https://webtechie.be/books/). It contains 20 extra pages describing a joystick-controlled game with [Pi4J](https://pi4j.com/) and the JavaFX [FXGL game-library](https://www.jfx-central.com/libraries/fxgl) of [**Almas Baim**](https://twitter.com/AlmasBaim).

Conferences {#h2-5-conferences}
-------------------------------

* [**Wolfgang Weigend** shared some pictures of the JFX Adopters Meeting](https://twitter.com/wolflook/status/1765356417760760283): "Thanks a lot for attending my session about JavaFX technology at Zeiss Meditec in Munich."
* If you attend [DEVNEXUS (Apr 9-11, 2024, Atlanta, GA)](https://twitter.com/devnexus), make sure to attend this talk: [Java, JavaFX, and Life on Jupiter's Europa](https://devnexus.com/presentations/java-javafx-and-life-on-jupiter-s-europa/) by **Jordi Turner** and **Scott Turner**: "To achieve this, we created an infrastructure in Java that allows us to responsively render and interact with maps and space views by combining JavaFX and AWT in a multi-threaded approach. This approach has been extremely successful, and now supports missions outside of Europa Clipper, analyzing spacecraft and data around Saturn, Venus, the Moon, and all the way back to Earth."

Tutorials {#h2-6-tutorials}
---------------------------

* [**Dave Barrett**](https://twitter.com/Polypragmatist) published a long article with a lot of example code [about new Binding and Listener features added to JavaFX in versions 19 and 21](https://www.pragmaticcoding.ca/javafx/subscribe_and_map): "I looked at the JavaDocs page for ObservableValue in both JFX 16 and 21. There are just 3 methods in JFX 16: addListener(), getValue(), and removeListener(). There are 9 methods in the JFX 21 version. Those 6 new methods (plus one in Observable) are what we are going to look at in this article. And yes, these are game changers!"
* [**Carl Walker**](https://twitter.com/CarlWalkerDrums) published an [article on using custom JavaFX Dialog subclasses](https://www.bekwam.net/javafx/custom-dialog.html).
* The JavaFX standard library doesn't provide a number-only input field. But with [this short snippet](https://codestore.cloud/public-snippets/c352ebe8-0045-acef-b59a-22efa873dc12), provided by [**Franz Deschler**](https://twitter.com/FranzDeschler/status/1761707840970904014), you can create your own component, that limits the possible input characters to only numbers.
* [Creating a JavaFX Project in IntelliJ IDEA: A Step-by-Step Guide](https://devcodef1.com/news/1172964/javafx-in-intellij-idea) on Dev Code F1.
* **Frank Delporte** wrote a blog post, including two "Code Walk-Trough" videos: "[Search in Documentation with a JavaFX ChatGPT-like LangChain4j Application](https://webtechie.be/post/2024-03-18-search-documentation-javafx-chat-langchain4j/)".
* JetBrains published an update of their IntelliJIDEA tutorial: [Create a new JavaFX project](https://www.jetbrains.com/help/idea/javafx.html).

Miscellaneous {#h2-7-miscellaneous}
-----------------------------------

* [**Steve Hannah**](https://twitter.com/shannah78/status/1764028712943751539) published a post: ["jDeploy vs jpackage - When does it make sense to distribute your Java desktop app with jDeploy vs jpackage"](https://jdeploy.substack.com/p/jdeploy-vs-jpackage)
* [**Bazlur Rahman**](https://twitter.com/bazlur_rahman) created a [Mandelbrot fractal using JavaFX](https://www.linkedin.com/pulse/fractal-journeys-javafx-exploration-a-n-m-bazlur-rahman-rfkjc/) with the help of Gemini LLM. The [code is available here](https://github.com/rokon12/Mandelbrot).
* A [shout-out from **Sean Phillips**](https://twitter.com/SeanMiPhillips/status/1764341506725286256) to [**Geertjan Wielenga**](https://twitter.com/GeertjanW): "...for investing so much of his personal time providing many helpful articles over the years." in a reaction to a [Tweet by **CJ**](https://twitter.com/jaimin_chovatia/status/1763977378030661739): "...due to my curiosity in desktop app development, I learned JavaFX to some extent and utilized NetBeans to create applications back in 2018-19".
* A really great research and JavaFX insights by [**Christopher Schnick**](https://twitter.com/crschnick) on [Foojay](https://twitter.com/foojayio), showing [how to run standalone JavaFX applications on exotic Linux systems like the Windows Subsystem for Linux or some embedded systems](https://foojay.io/today/javafx-on-wsl/)!
* "Pi Day just got more fun! Visualizing Pi with a JavaFX simulation", a video by **ANM Bazlur Rahman** in a [LinkedIn post](https://www.linkedin.com/posts/bazlur_javafx-piday-montecarlo-activity-7174005135688261632-4Vcj).
* [JabRef posted a first blog](https://blog.jabref.org/2024/03/11/JabRef-20-years-start/) of a series of posts on the 20 years anniversary of JabRef, talking with one of the early contributors **David Weitzman**. He started contributing to JabRef while he was in high school.
* [**WhiteWoodCity** created a Vulkan example](https://twitter.com/WhiteWoodCity/status/1765709277384384864) and is "now heading for the integration of Vulkan rendering with JavaFX PixelBuffer.". Source code is [available on GitHub](https://github.com/chengenzhao/java-vulkan-mac).
* [**Almas Baim** seems to be struggling with events and the Garbage Collector](https://twitter.com/AlmasBaim/status/1768364472798691780): "GC: *sweeping quietly in the background* Why are you all looking at me?"
* [**Sergey** has a question about mobile JavaFX development](https://twitter.com/SwiftVideoBlog/status/1770307570911031724): "I am very happy to see JavaFX being actively developed and supported. I wonder though how many developers use it for Mobile app development? Is there any recent data available to see the adoption of this technology?"
* Discussion on Reddit: [JavaFX at Oracle, present and future?](https://www.reddit.com/r/JavaFX/comments/1bodn5q/javafx_at_oracle_present_and_future/)
* [**Johan Vos** gets irritated...](https://mastodon.social/@johanvos/112155358077106884): "Even very smart people who I admire don't realize that the 'We have to write 3 clients (ios/android/web)' is wrong. You *can* do that, but technically there is no reason not to use Java once, and run it everywhere. Granted, the tools/docs/samples for Java Everywhere are way behind the web-copy-paste stuff. Nothing to do with technical stuff. It just doesn't fit in the big tech revenue models. So what?"
* [**Sean Phillips** on Twitter](https://twitter.com/SeanMiPhillips/status/1772343662447317204): "JavaFX is fun what can I say. It's great at combining 2D controls and overlays with 3D scenes. This allows me to interactively measure samples from the Biden Deep Fake robocall against known fake volumes to show which were faked and which were real. Super easy, takes 3 minutes."
  * He will be [presenting a new method for detecting Deep Fake Audio called Projected Volumetric Detection at DevNexus 2024](https://twitter.com/SeanMiPhillips/status/1771606914821931474): "Join me in Atlanta April 9-11 if you would like to know more about how to detect AI generated Deep Fakes using Java and JavaFX."

JFX Central {#h2-8-jfx-central}
-------------------------------

* A new showcase application has been added: ["bk.text"](https://jfx-central.com/showcases/bktext): "An ergonomic and accessible text system for the German justice system, which supports the digital processing of court proceedings."
* The overview of all the LinksOfTheWeek of February got [published on Foojay.io](https://foojay.io/today/javafx-links-of-february-2024/).

<br />

<br />
