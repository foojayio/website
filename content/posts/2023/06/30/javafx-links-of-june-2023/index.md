---
title: "JavaFX Links of June 2023"
slug: "javafx-links-of-june-2023"
date: "2023-06-30T09:18:30+00:00"
description: "Again a lot has been shared this month in the jfx-central.com #LinksOfTheWeek! And that website itself is \"under heavy construction\" as version - by Frank Delporte"
canonical: "https://webtechie.be/post/2023-06-30-javafx-links-of-june-2023/"
authors:
  - "frankdelporte"
image: "Favicon-3-2.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-may-2023"
  - "javafx-links-of-april-2023"
  - "javafx-links-of-march-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

Again a lot has been shared this month in the [jfx-central.com](https://www.jfx-central.com/home) #LinksOfTheWeek! And that website itself is "under heavy construction" as version 2 is getting a completely new design and several improvements. Your help is wanted! See the last section of this summary...

Core
----

* [Rampdown Phase 1 (RDP1) for JavaFX 21 starts on July 13, 2023 at 16:00 UTC (09:00 Pacific time)](https://twitter.com/OpenJDK/status/1669074682505572353), about four weeks from now.
* JavaFX 21 Early-Access Builds Build 21 is available from [jdk.java.net/javafx21](https://jdk.java.net/javafx21/), addressing [these issues](https://bugs.openjdk.org/browse/JDK-8301312?jql=project%20%3D%20JDK%20AND%20fixversion%20%3D%20jfx21%20and%20component%20%3D%20javafx%20and%20%22resolved%20in%20build%22%20%3D%20b21%20order%20by%20component%2C%20subcomponent). JavaFX 21-ea is designed to work with JDK 21-ea, but it is known to work with JDK 17 and later versions.
* [**Johan Vos** is planning to spend more time on Java on Mobile (OpenJDK Mobile)](https://mastodon.social/@johanvos/110547534890057649) in the near future: "There is 0 support from big tech because they fall in 2 categories: 1/ they have no revenue on mobile, 2/ they have competing OS tech that drives devs/users to their revenue-driven cloud services. But since I'm not big tech, I can just do it."
* [**Gluon** asks all JavaFX developers to give JavaFX 21-ea+21 a try](https://twitter.com/GluonHQ/status/1671461934049243136).
  * [And is working on making their JavaFX software (libraries, tools, components) more consistent and developer-friendly](https://techhub.social/@gluonhq/110615181663234520): "Over the past years, we learned a lot, and we're putting that experience now in our code."
* [**Dirk Lemmermann** is looking for help with a Webkit issue in JavaFX 18.0.1](https://github.com/dlsc-software-consulting-gmbh/GMapsFX/issues/204).
* [**Abdelrahman Bayoumi**](https://twitter.com/Abdelrahman_B1) explores a solution to resolve the challenges when it comes to [rendering Arabic, being a right-to-left script](https://dev.to/abdelrahmanbayoumi/arabic-text-rendering-issues-in-javafx-3j9i).

Applications
------------

* [**Sean Phillips** is almost ready to make the sources of Trinity available](https://twitter.com/SeanMiPhillips/status/1662197944718876672)...
  * [He is using Trinity to distinguish between human and chatGPT altered text](https://twitter.com/SeanMiPhillips/status/1666617132338102273).
  * After [teasing us](https://twitter.com/SeanMiPhillips/status/1669087717743788033) for a few weeks, it finally happened! [**Sean Phillips** announced](https://twitter.com/SeanMiPhillips/status/1669089367694901250) that Trinity, an Explainable AI Analysis and Visualization tool written in Java and JavaFX, is now officially public and open source. His message: "Have fun you fine young cannibals."
  * Video demo of Trinity to [project and distinguish between human and chatGPT altered text blurbs](https://twitter.com/SeanMiPhillips/status/1668296662857424896).
  * With many thanks to [**JavaFX 3D** , **Jasper Potts** , **Alexander Kouznetsov** , **Chien Yang** , **Kevin Rushforth** , **Richard Bair**, and many others](https://twitter.com/JavaFX3D/status/1669523111484719105) for all their hard work on JavaFX and 3D and more!
  * [**Sean Phillips** shared a screenshot](https://twitter.com/SeanMiPhillips/status/1671682955855446016): projecting decision manifolds from Pilot Assist AI Models for dog fighting pilots.
  * [Rendering a rainbow highway](https://twitter.com/SeanMiPhillips/status/1671674205098704899).
  * [**Johan Vos** is still impressed by the Devnexs keynote given by Sean](https://mastodon.social/@johanvos/110566790213698291): "This is a very cool demonstration about Trinity, an open-source tool developed/used at John Hopkins University. It shows how Java developers can create cool, amazing and extremely useful stuff with great visualization using JavaFX."
* [**Christopher Schnick** improved the speed of Pdx-Unlimiter](https://twitter.com/crschnick/status/1662109280323993600) with cached dynamically generated images.
* [Deep Netts has a neural network visual weights analysis tool ready for the next release](https://twitter.com/johanvos/status/1666416554110513158). It helps to understand what's going on inside layers, and debugging trained networks.
* [**Maleesha Herath** introduces "tecmis", an Information Management System](https://twitter.com/MaleeshaH/status/1666865457599770625) that revolutionizes how educational institutions handle data.
* [**Tobias Briones** finished the implementation for Code Snipped Slides](https://twitter.com/tobiasbriones_/status/1666863274938204172) with the exact color scheme of IntelliJ, except for specific language tokens. It's all composed with JavaFX Nodes, like Shapes.
* New version of [Mapton with bug fixes and minor improvements along with new WMS sources](https://twitter.com/mapton_app/status/1667529564120023042). Dependencies such as NetBeans 18 platform and the bundled Java \& JavaFX from Azul are updated too.
* [**Christopher Schnick** gave the welcome page of v1.2 of XPipe some personality](https://twitter.com/crschnick/status/1672101399486951425).
* [**Dirk Lemmermann** really likes the latest version of the sample app for AtlantaFX](https://twitter.com/dlemmermann/status/1670757586260377602): "Very nicely polished theme and controls. Packaged with Conveyor from HydraulicDev."
* [**Maciek Gorywoda** shared v1.0.0 FxCalculator](https://twitter.com/makingthematrix/status/1669374799246774278): "It's, well, a calculator for your Android phone." Read the whole thread to learn more, e.g. that it's written in Scala 3.3, JavaFX, Gluon, and built with GraalVM Native Image.
* [**Jamie Macaulay** is working on SUDUnpacker](https://twitter.com/jam_machund/status/1671102745309265920) to handle SoundTraps sud files.

Games
-----

* [**Johan Vos** stumbled into this addictive web game, written in JavaFX by **Gerrit Grunwald** using WebFX: tetris.webfx.dev](https://foojay.social/@johanvos@mastodon.social/110461937691524803): "It's great to see how developers are using JavaFX to make cool stuff."
* [**OrangoMango** made Snake v1.0 available on itch](https://twitter.com/orango_mango/status/1662758757506469889). Thanks to WebFX, there is now a web version that is playable in the browser (fullscreen is recommended).
  * [He extended his chess game](https://twitter.com/orango_mango/status/1666727933258485760) with drag and drop, and improved the image resolution.
  * [He also shared a video showing collision detection with convex shapes](https://twitter.com/orango_mango/status/1671252291658055680). It resulted in a nice chat with [**Sean Phillips**](https://twitter.com/SeanMiPhillips/status/1671662634930696195) and [**Carl Dea**](https://twitter.com/carldea/status/1672041067909193734).
  * [He added a teleport stone, a propeller, and rotating platforms to FoodDice](https://twitter.com/orango_mango/status/1673693403664764938). New levels with those new items are coming in the next update. Also implemented the new collision system that he wrote before for these new rotating items.
  * And he has been [experimenting with a physics engine made from scratch](https://twitter.com/orango_mango/status/1672648493372981249). Right now, there are just some basic physics laws and chains.
* Another [JavaFX game has been ported to the Web with **WebFX** Food Dice!](https://twitter.com/WebFXProject/status/1668227539154747396). It's a plain JavaFX game written for the GMTK Game jam 2022 and was coded in 48h from scratch with no 3rd party lib. Congrats to the 16-year-old author **OrangoMango\*\*** . Play online on [fooddice.webfx.dev](https://fooddice.webfx.dev).
* [**Almas Baim** shows image mesh warping](https://twitter.com/AlmasBaim/status/1671169432524316674): "No specific use case, but I'm sure this will come in handy for some cool effects in the future."
  * [He is trying to hypnotize us](https://twitter.com/AlmasBaim/status/1664730029887037441).
  * [And he is drawing circles with triangles and FXGL](https://twitter.com/AlmasBaim/status/1673700044556386304).

Components, Libraries, Tools
----------------------------

* Last week we shared the TreeMap chart by **Matt Coley** here. In a new video, you can see a [practical demo of TreeMapFX in Recaf](https://twitter.com/invokecoley/status/1663073175431675904).
* [**Sven Ruppert** shared that work has started on TestFX 4](https://mastodon.social/@svenruppert/110436643725621744), after longer silence. You can find the project on [testfx.github.io/TestFX](https://testfx.github.io/TestFX/).
* [**Dirk Lemmermann** sees there truly is a big need for custom window decorations and found an extra one](https://twitter.com/dlemmermann/status/1669473442163294210).
* [**Jaroslav Tulach** is discussing JavaFX Lite](https://twitter.com/JaroslavTulach/status/1449827890300915718) and is asking for feedback: "Would you find #javafxlight - e.g. coding in #javafx \& rendering via #HTML5 useful?"

Podcast, Videos
---------------

* [**Gerrit Grunwald** and **Ixchel Ruiz** talking at JNation](https://www.linkedin.com/feed/update/urn:li:activity:7071865436115468288/) about Graphical User Interfaces with Java.
* [Foojay Podcast #25: Game Development with Java, JavaFX, and FXGL](https://foojay.io/today/foojay-podcast-25/). **Gerrit Grunwald** (aka _hansolo), **Almas Baim** (aka the FXGL creator), and **Chengen Zhao** (aka WhiteWoodCity) talk with **Frank Delporte** about why Java should be on your game-development-language-list.

Tutorials
---------

* **Coding Examples** uploaded new videos to "JavaFX 3D Tutorials". For example: ["Animation \| Fade Transition", to create a Text and make it blink using the FadeTransition class](https://www.youtube.com/watch?v=9YSor8d3Hzk). using the shape's transparency to achieve the required effect.
* [**Sten Nordström** got asked if there are any good JavaFX courses available?](https://twitter.com/safetyvalve/status/1664349425228038144): "Asker has experience with other platforms and some Java knowledge. Online, or possibly in person in Scandinavia/Northern Europe." Who has more ideas? What should we add to [jfx-central.com/tutorials](https://www.jfx-central.com/tutorials)?
  * This is actually a nice one 🙂 ! [**MITHIN DEV** shared "Building Your First JavaFX App!"](https://twitter.com/MithinDev/status/1665709087135506432)
* Not new, but a nice reminder by [**Jakob Jenkov**: "I have a tutorial covering a lot of the basics of JavaFX"](https://twitter.com/jjenkov/status/1666472122535886849).
* In French: [**Jason Champagne** has a series of videos, part 2](https://twitter.com/jachampagne8/status/1674085894700113921): "Point essentiel en JavaFX sur l'architecture des différents composants, en particulier le Scene Graph et la gestion des fenêtres (stages)".
* Not new, but just discovered: [Video series by **Jaret Wright** to create a JavaFX Memory Game](https://www.youtube.com/watch?v=guXTwZpGVRk&list=PLoodc-fmtJNbeL8P1DizFcgjp62UjvJ3t).

Miscellaneous
-------------

* [**Donald Raab** is hoping to get back to experimenting with JavaFX again this summer](https://twitter.com/TheDonRaab/status/1662199629407027203) and to submit conference talks...
* [**Sten Nordström** is looking for JavaFX tutorials](https://mastodon.online/@sten/110470561467745227): "Asker has experience with other platforms and some Java knowledge. Online, or possibly in person in Scandinavia/Northern Europe." Who can help him? First starting point should of course be: [jfx-central.com/tutorials](https://www.jfx-central.com/tutorials) 😉
* [A help request on Reddit to build a JavaFX project in VsCode](https://www.reddit.com/r/JavaFX/comments/1483fio/need_help_building_javafx_project_in_vscode/).

JFX-Central
-----------

* Service call to all the people who have "something" on JFX-Central (library, book, tutorial,...). We are reviewing all info on the website in preparation of the new JFX-Central version. But we need your help, please contact us or make a pull request in [jfxcentral-data](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data) to make sure all the info is still correct and up-to-date. Thanks!
* More previews of version 2 of jfx-central.com:
  * [**Dirk Lemmermann** is drooling with the layout](https://twitter.com/dlemmermann/status/1663916396814761987).
  * And [more drooling](https://twitter.com/dlemmermann/status/1663916816794517504)...
  * [Social login capabilities were added](https://twitter.com/dlemmermann/status/1662388854366560256).
  * And he is having almost too much fun 🙂 [Added banners to the tips and tricks pages](https://twitter.com/dlemmermann/status/1664334599319592969).
  * Finding out what's coming to the next JavaFX release [will be super easy by visiting the new OpenJFX page on JFX-Central](https://twitter.com/dlemmermann/status/1668690954876755974).
  * Finding that one library you recently heard about [will be easy via JFX-Central... and who wrote it, and videos showing it, and the repo coordinates, and and and](https://twitter.com/dlemmermann/status/1669472701059047424).
  * [**Lee Wyatt**, one of the main contributors to the new JFX-Central version](https://twitter.com/dlemmermann/status/1672975213376552964) added a "Credits" page, one of the many pages he created and/or improved.
* **Dirk Lemmermann** wants your opinion about a new logo for jfx-central (and JavaFX itself?)
  * [First idea with a lot of remarks and adjusted proposals by others](https://twitter.com/dlemmermann/status/1666478961675862022).
  * [The next proposal](https://twitter.com/dlemmermann/status/1666735588500811777).
  * After many iterations, [Dirk found the perfect new logo](https://twitter.com/dlemmermann/status/1669026529429397521): "Work on the logo for the JFX-Central website is finished. In the end large, small, color, black, white, etc.... versions were needed to make it look code in all places."
* Testing the layout of the new version [with "responsive design" mode is in Safari](https://twitter.com/dlemmermann/status/1666771832765132801).
* And the new desktop version will [come with its own custom stage when running on desktop](https://twitter.com/dlemmermann/status/1667169729784545280). Other improvements for the app version of the new JFX-Central:
  * [A couple of screens were added for a nice bootstrap sequence](https://twitter.com/dlemmermann/status/1670798362453368840). When running the app for the first time it will clone its data repo (takes a while). Any launch after that will perform an update on it (fast).
  * [It will also come with a tray icon for quick access to anything JavaFX](https://twitter.com/dlemmermann/status/1671506280026972164)
  * [It is now an even better citizen when running on Mac](https://twitter.com/dlemmermann/status/1671538241952948228). Can you spot the difference aka the "dots"?
* We have been very busy working on the new JFX-Central version... Maybe you want to help out? [The repository is now public](https://github.com/dlemmermann/jfxcentral2)! Feel free to make contributions. Testing, and reporting bugs. All are very welcome.
