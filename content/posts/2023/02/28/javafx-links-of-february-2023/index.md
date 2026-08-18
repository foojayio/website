---
title: "JavaFX Links of February 2023"
date: "2023-02-28T11:24:43+00:00"
lastmod: "2023-02-28T11:25:04+00:00"
description: "A lot of game and 3D development, releases, interesting ongoing development, and so much more to read. Enjoy reading and clicking!"
authors:
  - "frankdelporte"
image: "newreleases.png"
categories:
  - "JavaFX"
  - "Release Notes"
related_posts:
  - "javafx-links-of-january-2023"
  - "javafx-links-of-december"
  - "javafx-links-of-november"
  - "javafx-links-of-july-2026"
frozen: false
---

February is a short month, but this list seems to be longer than ever...

A lot of game and 3D development, releases, interesting ongoing development, and so much more to read. Enjoy reading and clicking!

This is a summary of the Links Of The Week as published on [jfx-central.com](https://www.jfx-central.com) during February.

### JavaFX/OpenJFX Core

* **Kevin Rushforth** of Oracle announced on the mailinglist "As a reminder, JavaFX 20 is now in Rampdown Phase Two (RDP2). Now that we are in RDP2, the goal is to stabilize what is there". So we will soon get a new version of JavaFX being released!
* [**Chad Preisler** wants to thank all JavaFX maintainers](https://twitter.com/cpreisler/status/1620893592435978240): "The people that maintain and enhance #JavaFX are great. They fixed an issue with Mac back in December, and today when a dev got a M1 all we needed to do was update the JavaFX dependencies. Everything runs great now."
* [**Gluon** announced public access to its JavaFX 17 builds](https://twitter.com/GluonHQ/status/1623680620315529216), including 17.0.6 and subsequent versions.
  * With an important note regarding version compatibility: "**As the development of JavaFX 20 picks up momentum, it's important to note a key change -- JavaFX 20 will require Java 17 or later.**"
  * [**Johan Vos** of Gluon also shared](https://twitter.com/johanvos/status/1623958106387410945): "Gluon leverages GraalVM in Gluon Substrate, allowing JavaFX apps to be converted into native client apps for desktop, mobile and embedded."
  * [They announced improved sound support](https://techhub.social/@gluonhq/109902223934756469) for iOS in Gluon Attach.
  * And thank [**Bruno Salmon**](https://twitter.com/salmon_bruno) for a great contribution by adding the iOSAudioService.
  * See the [code changes in the pull request](https://github.com/gluonhq/attach/pull/347), optimised for games that may play sounds simultaneously frequently, without degrading performances.
* [**Dave Barrett** is a big fan of JavaFX + Kotlin](https://twitter.com/Polypragmatist/status/1623457419404914690): "it's a match made in heaven. Kotlin gives you the tools to streamline your layout code in ways you never could in Java."
* [**Chad Preisler** shared a 5 minute about property binding](https://twitter.com/cpreisler/status/1623174913891659777): "JavaFX makes getting data from your form controls into your model very easy.".
* [**Gerrit Grunwald** warns about the JavaFX Canvas being really nice and fast, but](https://twitter.com/hansolo_/status/1621651332011642881) "be beware of effects... Using one simple dropshadow in a GraphicsContext can really bring down performance... Just as a reminder."

### Scene Builder

* [**Raumzeitfalle** shared an update for Scene Builder Leading Edge](https://twitter.com/Raumzeitfalle/status/1627038605225955329): preview of unofficial and features-in-progress. February 2023 brings us support to create controllers in Scala and JRuby and a Chinese translation.

### UI Development

* [**WhiteWoodCity**](https://twitter.com/WhiteWoodCity/) shared a lot of JavaFX news:
  * [He found this impressive video of VFX](https://twitter.com/WhiteWoodCity/status/1621461727681589248), a JavaFX UI [framework](https://www.bilibili.com/video/BV1c24y1B7jg/).
  * The [sources of VFX are available on GitHub](https://github.com/wkgcass/vfx).
  * A [video of a self-made new JavaFX UI by **WhiteWoodCity**](https://twitter.com/WhiteWoodCity/status/1618947794638884866).
  * [How to use of VFX components to decorate a JavaFX application](https://twitter.com/WhiteWoodCity/status/1622774096957431808) with a link to video and sources.
  * [A video of a nice JavaFX UI](https://twitter.com/WhiteWoodCity/status/1624978159996407811).
* In the previous edition of this list, a link was included to Matt Coley sharing his wish-list to extend RichText. There is a [GitHub issue by **Andy Goryachev** of Oracle asking for "Any missing APIs in JavaFX which are needed for RichTextFX"](https://github.com/FXMisc/RichTextFX/issues/1167) to gather feedback.
* [**Sean Phillips** spotted a JavaFX user interface](https://twitter.com/SeanMiPhillips/status/1628819152122019841) on a transparent Science- Fiction-like screen.

### JavaFX Libraries

* [**Frank Delporte** shared it is still a long way to go, but Lottie4J can now read both fixed and animated beziers](https://twitter.com/FrankDelporte/status/1622870327301746688). It includes a screenshot of the very first result of a loaded animation with colors, strokes, fills... being the next step.
  * And shared a [link to an article](https://foojay.social/@lottie4j/109839719108396708) why it could become important to have a JavaFX implementation of LottieFiles: "4.7 Million Motion Graphics Designers and Developers Turn to Lottie for Efficient Animation Workflow."
  * [**Lottie4J** is making small progress](https://foojay.social/@lottie4j/109909075718845055) in bringing LottieFiles animations to JavaFX with a first correctly colored stroke width and color.
* [**Dirk Lemmermann** created a new project on GitHub called LayoutFX](https://twitter.com/dlemmermann/status/1628069536074280964) and would like to use it to collect interesting layout solutions for JavaFX. If you have any custom panes with fancy approaches to laying out scene graph nodes and would like to contribute, then please feel free to add it.
  * And he's [adding a custom control to GemsFX that allows to horizontally position and scroll multiple cells based on an items list](https://twitter.com/dlemmermann/status/1628745308296425473). The control fades out to the left and right.

### JavaFX Applications

* [JDKMon by **Gerrit Grunwald** got downloaded 10k times](https://twitter.com/hansolo_/status/1623028117219450881)!
* [**Dirk Lemmermann** is facing another nice design challenge for his CRM for the energy market](https://twitter.com/dlemmermann/status/1621512306311200769).
  * He also spotted [JavaFX in the wild, in the online presence of an office supplies company](https://twitter.com/dlemmermann/status/1628342392964239360), running in the browser via Jpro.
* [**Frank Greco** plans to create a JavaFX ChatGPT application this weekend](https://twitter.com/frankgreco/status/1623701464362229760).
* [The first alpha of X-Pipe, a new remote connection tool created with Java(FX)](https://mastodon.social/@java_discussions/109849881898714868).
* [**Alessio Vinerbi** shared a video](https://twitter.com/Alessio_Vinerbi/status/1628098767160213525) showing the interaction between his visual modeler and FXML.
* [**JabRef** now has a dark theme created by **Joel Maximilian Mai**](https://foojay.social/@jabref/109836297203164251).
* [**trinaryoperator** created a JavaFX version of WinDirStat to do a cleanup of some directories](https://www.reddit.com/r/JavaFX/comments/11a36yv/windirstat_in_javafx/). In the future it will have actual tools to help hard drive clean-up.
* [**Chad Preisler** built a very basic Kafka topic viewer](https://twitter.com/cpreisler/status/1628752475434909702) and shared a 7-minute video with a link to the source code in the video description.

### Game Development

* [**Almas Baim** shared a fancy particle effect demo](https://twitter.com/AlmasBaim/status/1620569177928142848).
  * [And he is practising his "summing skills"](https://twitter.com/AlmasBaim/status/1621649400052211715).
  * [Shared a video of a fishing game made with FXGL](https://twitter.com/AlmasBaim/status/1624788479686180864) shared on YouTube. Does anyone know the creator?
  * He also shared [a quick 20 LoC prototype with absolutely horrible UX](https://twitter.com/AlmasBaim/status/1624475188942319617). However, it shows with a bit of refinement here and there, you could totally build yet another Minecraft clone.
* [**Jhonny Göransson** managed to mix JavaFX nodes with raw OpenGL calls](https://twitter.com/jhonnygoransson/status/1620563738347847682) from native cpp via drift-fx.
* [**ParrotMan** shared a project created 2 years ago](https://twitter.com/ParrotMan18/status/1621884694081204225): "I made the soundtracks, pixel art sprites, and almost all of the underlying systems from scratch. It looks janky as heck but it was a worthwhile learning experience."
* [**GZYanKui** share a video with a game](https://twitter.com/YangKui7/status/1622748759309570050).
* We're looking forward to the blogpost [**Gerrit Grunwald** will write on how to run a JavaFX application on iOS using Gluon](https://twitter.com/hansolo_/status/1620132608205266945) with his sample application will JArkanoid.
  * [He spent a weekend with some JArkanoid coding](https://twitter.com/hansolo_/status/1621905556075077634).
  * [And finished JArkanoid levels 4 - 7](https://twitter.com/hansolo_/status/1621905661012353025).
  * [And will build it with GitHub Actions](https://twitter.com/hansolo_/status/1622151570119852033).
  * [Implemented the last levels missing in JArkanoid.](https://twitter.com/hansolo_/status/1625344281849352192). It now has all 32 levels of the original except the very last level. [You can download the sources and builds for various systems from GitHub](https://github.com/HanSolo/jarkanoid).
  * And he [thanks José Pereda from Gluon](https://twitter.com/hansolo_/status/1626220741065850882) to help him to get sound working on iPhone.
  * You can [also run JArkanoid on Raspberry Pi](https://twitter.com/hansolo_/status/1628055233094991873).
  * Gerrit [thanks **Gluon** to make porting to mobile sooooooo easy](https://twitter.com/hansolo_/status/1627022312808513536).
  * [**WebFX** announced a web version](https://twitter.com/WebFXProject/status/1627654253253722113) that can be played [online at jarkanoid.webfx.dev](https://jarkanoid.webfx.dev/).
  * [**Max Rydahl Andersen** created a JBang version](https://twitter.com/maxandersen/status/1627453787412594688) that can be simply started with "jbang jarkanoid@maxandersen".
* [**GZYangKui** shared an other retro game](https://twitter.com/YangKui7/status/1627317531966058499).
* [**WhiteWoodCity** is using FXGL to simplify the code of UI applications](https://twitter.com/WhiteWoodCity/status/1627302125960302594).
  * And [migrated his game fully to FXGL](https://twitter.com/WhiteWoodCity/status/1628316669268606976).

### 3D

* [**OrangoMango** keeps experimenting with 3D](https://twitter.com/orango_mango/status/1620493609287172096).
  * And what is really impressive... [it is running on a Raspberry Pi with 2GB of memory](https://twitter.com/orango_mango/status/1620827009646759937)!
  * [A rotating light that simulates the sun](https://twitter.com/orango_mango/status/1621578572895854595), only with matrices and vectors in a self-made 3D engine.
  * [Improved shadows and performance by adding cache (video)](https://twitter.com/orango_mango/status/1625759767464484864), in a self-made 3D engine from scratch.
  * [Experimenting with chess pieces](https://twitter.com/orango_mango/status/1627331295536680960) with his 3D engine.

### Podcast

* [**Adam Bien** talked in his podcast with **Karol Harezlak**](https://twitter.com/AdamBien/status/1619811640802955265) briefly about JavaFX.

### Miscellaneous

* Not directly JavaFX related, but nice to know... [**Heinz Kabutz** shared graphs](https://mastodon.social/@HeinzKabutz/109799634014176668) showing that a lot of the work in recent Java versions was to stabilize and improve the platform, rather than just adding hundreds of new classes. The number of lines of code might even decrease in the future.
* The research team of [**Almas Baim** completed basic initialization and setup steps](https://twitter.com/AlmasBaim/status/1627783341805117447) for UI and robot interaction. The hype is real at the Robotics AI Lab.

### Jobs

* [JavaFX Developer (Remote)](https://remotewant.com/job/javafx-developer-2/)
* [Java Entwickler (Berlin), including JavaFX](https://germantechjobs.de/jobs/Honeypot-GmbH-Java-Entwickler)
* [Lead JavaFX Application Developer (Remote)](https://remotewant.com/job/lead-javafx-application-developer/)

### New Releases

* [3.2.0 of KeenWrite by **Dave Jarvis**](https://github.com/DaveJarvis/keenwrite), a free, open-source, cross-platform desktop Markdown editor that can produce beautifully typeset PDFs.
* [2.2.1 of KeenType used in KeenWrite](https://github.com/DaveJarvis/KeenType) with modernized DANTE e.V.'s Java-based NTS system for rendering TeX.
* [3.11 of binjr](https://twitter.com/binjr_app/status/1620873802522701824), a standalone time series browser that renders data produced by other applications as dynamically editable views and provides advanced features to navigate the data smoothly and efficiently.
* [5, 5.0.1 and 5.0.2 of PDFsam](https://twitter.com/PDFsamOSS/status/1620779451146719232) a powerful and professional PDF editor.
* [v2.1.4 of FXGraphics2D by **David Gilbert**](https://twitter.com/david_m_gilbert/status/1625034499355561984). This enables drawing on the JavaFX Canvas using the Java2D APIs. The update includes great contributions from [**Laurent Bourges**](https://twitter.com/laurent_bourges) to fix clipping issues and boost performance!

### New content on jfx-central.com:

* Company added: [Intechcore](https://www.jfx-central.com/companies)
