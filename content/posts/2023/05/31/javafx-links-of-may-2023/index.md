---
title: "JavaFX Links of May 2023"
slug: "javafx-links-of-may-2023"
date: "2023-05-31T06:48:43+00:00"
lastmod: "2023-05-31T06:51:37+00:00"
description: "Again a busy month in JavaFX-world! Here is a nice list with links for your reading and clicking pleasure!"
canonical: "https://webtechie.be/post/2023-05-30-javafx-links-of-may-2023"
authors:
  - "frankdelporte"
image: "javafx-community.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-april-2023"
  - "javafx-links-of-march-2023"
  - "javafx-links-of-february-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

Again a busy month in JavaFX-world! Here is a nice list with links for your reading and clicking pleasure!

This is the summary of the #LinksOfTheWeek as published on [jfx-central.com](https://www.jfx-central.com/) in May 2023.

JavaFX Core
-----------

* [JavaFX 21-ea+17 is available for download from GluonHQ or from Maven central](https://techhub.social/@gluonhq/110374163797572361).
* [**Johan Vos** believes it's time to deprecate Java Swing](https://mastodon.social/@johanvos/110378814066039175): "It doesn't make sense to maintain Swing and move JavaFX forward. Deprecation doesn't mean it needs to be removed immediately. But let's deprecate it. We need to move forward, and we need the resources for this."

SceneBuilder
------------

* [**Raumzeitfalle** is wondering if you want to conveniently install Scene Builder 20 for JavaFX](https://twitter.com/Raumzeitfalle/status/1657471102673715202)? You can now do this thanks to the use of JDeploy.
  * This new version now offers a [direct menu shortcut to jfx-central.com](https://twitter.com/Raumzeitfalle/status/1656697093463982083)!
  * This release uses [JDK 20.0.1 and JavaFX 20.0.1](https://techhub.social/@gluonhq/110337334600751096).
* [Shared by **Johan Vos**](https://mastodon.social/@johanvos/110371523942727201): "Now that Scene Builder 20 is released, it is a good moment to work on functionality/bug fixes that can make it in 21. For most projects I work on, I prefer doing major changes shortly after a release, rather than shortly before a release."

JavaFX Applications
-------------------

* Shared by [**MarsX.dev**: Lunatics](https://twitter.com/marsxdev/status/1656911587163410432), a JavaFX-based email testing tool with a dummy SMTP server that lets you test email sending applications, monitor transactions, and simulate various network conditions.
* [**Clemens Lanthaler** announced LibrawFX 1.8.4](https://twitter.com/lanthale/status/1642981173826842624) with speed updates and raw settings added, using JDK 20 and JavaFX 20.
* [**Homebookner** shared SANEScanFX, a scanner application](https://twitter.com/Heimbuchner_J/status/1653019966453473280), not finished yet but already usable.
* [**Pedro Duque Vieira** proudly shared that Modellus received the recommended award again this year from Mac Informer](https://twitter.com/P_Duke/status/1656677709106520064) (3rd consecutive year)! It's a freely available app used worldwide that enables students \& teachers (high school and university) to use mathematics to create or explore models interactively.
* [**Christopher Schnick** is working on context menu actions for XPipe](https://twitter.com/crschnick/status/1661349160547766272). What else would you like to see added?
  * And he shared a [video with more AtlantaFX themes plus smooth theme transitions](https://twitter.com/crschnick/status/1661340242052251650).
* [**Alessio Vinerbi** seems to be working on MoonsonFX](https://twitter.com/Alessio_Vinerbi/status/1659864509320687619), an alternative AfterEffects application. This makes me very curious as he already showed other impressive UI-work.
* [**Juanan** seems to be working on a Mastodon client](https://mastodon.social/@juananpe/110383623013926873), see also his other Toots on Mastodon.
* [binjr (FOSS timeseries browser) 3.12 is available](https://fosstodon.org/@binjr/110380055859735465), with a new indexing mode for log files that makes it easy to search for arbitrary strings of character without the need for wildcards while maintaining the same level of performance, even on very large files.
* **Sean Phillips** shared several images and videos of Trinity, an AI / ML analysis tool.
  * [By colorizing Binary Classification feature space from a deep learning model using JavaFX 3D](https://twitter.com/SeanMiPhillips/status/1653721465059942401) we see decision manifolds have subgroupings.
  * And an extra one with [Trinity's UMAP projection cluster data maintaining references back to their hyper-dimensional vectors and imagery](https://twitter.com/SeanMiPhillips/status/1653492689193644032). This demonstrates anchoring 2D nodes to Shape3D nodes as the PerspectiveCamera is rotated.
  * Too complex to understand? I agree, but his work is just amazing and really mind-blowing 😉
  * If you want to know more about it, this is a link to an interview of December: ["Visualizing Brain Computer Interface Data Using JavaFX"](https://foojay.io/today/visualizing-brain-computer-interface-data-using-javafx/)
  * And in those tweets he announced Trinity will be available soon on Github!
  * [UMAP projector to visualize COVID infected tissue samples, clustered by genetic sequence classification](https://twitter.com/SeanMiPhillips/status/1655678667257782272). Clear distinction between brain and body tissue using the Yule metric.
  * [Process, cluster and render COVID tissue samples by genetic sequence code classification](https://twitter.com/SeanMiPhillips/status/1656459698302861318).
  * [Similar, using the Yule distance metric, with 2500 samples each with over 18k dimensions](https://twitter.com/SeanMiPhillips/status/1656462050804092928)!
  * Don't tell anyone, but Sean is actually a time traveler, who worked (will work?!) on the StarTrek user interfaces...

JavaFX Games
------------

* [Pac-Man and Ms. Pac-Man by **Armin Reichert** ported to the web with WebFX](https://twitter.com/WebFXProject/status/1661336335427182593).
* [Rubik's cube v3.0 by **OrangoMango** is available on itch.io](https://twitter.com/orango_mango/status/1659545495704928257) for Windows, Android, Linux and Mac.
  * [He also shared his Chess game](https://twitter.com/orango_mango/status/1655259186576474112): sources and executables for Windows, Linux, macOS and Android. Features: play agains stockfish or LAN multiplayer, export game as FEN and PGN, arrows, premoves, time control, and much more...
  * And a link to the [sources of his 3D engine and the Minecraft-clone](https://twitter.com/orango_mango/status/1652974233864085504).
* [**Almas Baim** shared a video of a game where the player can now use the shockwave ability](https://twitter.com/AlmasBaim/status/1657718394064601089) once the meter is full. It allows the player to quickly clear out a safe path in front of them.
  * [And he had a fruitful discussion with game dev students on how to improve the UI](https://twitter.com/AlmasBaim/status/1658565437053980691). In the video, you can check out the new health and weapon indicators around the player, without the need to look elsewhere to get this info.

JavaFX Components
-----------------

* [**Matt Coley** made a minimal/flexible TreeMap chart](https://twitter.com/invokecoley/status/1660951086876749825).
* [**Christopher Schnick** is feeling magic with dynamic OS theme detection](https://twitter.com/crschnick/status/1658174428612550671)! Done with jSystemThemeDetector.
* [**Sean Phillips** got a lot of interest on the animated circular progress indicator](https://twitter.com/SeanMiPhillips/status/1658120303539519491) he made for Trinity and uploaded a version of it along with a tester app so folks could play with it.

JavaFX on Foojay.io, the website for Friends of OpenJDK
-------------------------------------------------------

* ["If the user has installed programs to connect to remote systems, why not try to use them from Java instead of via libraries?"](https://foojay.io/today/presenting-xpipe/) And then, on top of that, a tool for people who work with remote shell connections? **Christopher Schnick** presenting X-Pipe.
* [Interview by **Frank Delporte** with **Paul Kocian** aka @Orango_Mango](https://foojay.io/today/interview-with-paul-kocian-aka-orango_mango/), who shared his progress on Twitter in creating a 3D engine with JavaFX, from a Rubik's cube in January '23, over a basic tumbling car in February, to a full Minecraft-like world in April! His age? 16!

Podcast
-------

* [**Adam Bien** and **Shai Almog** discuss JavaFX, Codename One, Swing, Flutter, and a Bit Android](https://airhacks.fm/#episode_242).

Miscellaneous
-------------

* [**Dirk Lemmermann** is about to rent 190 square meters of office space in Zurich for our Swiss subsidiary of Senapt](https://twitter.com/dlemmermann/status/1657027053202423808). Exciting times, and JavaFX made that happen!
* Help! [**Almas Baim** is doing strange teleportation things with the Java Duke](https://twitter.com/AlmasBaim/status/1652066132872896512)...
* It seems GraalVM is going to [announce some JavaFX related news with the next feature release, expected on June 13th](https://twitter.com/shaunmsmith/status/1651621631322726413).

JFX-Central
-----------

* New content:
  * Real-world app: [Satergo](https://www.jfx-central.com/real_world/satergo), a desktop wallet app for the "Ergo" cryptocurrency.
  * [Sneak preview of the new website taking advantage of Safari's unified toolbar](https://twitter.com/dlemmermann/status/1655893575480090625), aka "theme color" meta tag.
* **Dirk Lemmermann** shared a lot of previews of the upcoming new version of jfx-central.com.
  * [The "skeleton" for the detail pages](https://twitter.com/dlemmermann/status/1658067095664443394). It will be used to list information on people, books, libraries, tools, blogs, etc... Every page will be responsive and auto-adjust to large (desktop), medium (tablet) and small devices (phone).
  * [The new showcases section](https://twitter.com/dlemmermann/status/1661055817041297409).
  * [Videos will no longer be shown in a separate overlay, but "inline"](https://twitter.com/dlemmermann/status/1659203119136509955).
  * [Ikonli will also have a nice new home](https://twitter.com/dlemmermann/status/1661325621471969281).
  * [The shiny new home for the "links of the week" section](https://twitter.com/dlemmermann/status/1661718165577953280). Also coming soon... the RSS button.
  * [Having a professional designer do your app's design is always worth the money](https://twitter.com/dlemmermann/status/1661676012466307072). Not only do you save time, but it will also look much better.
