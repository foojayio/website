---
title: "JavaFX Links of February 2024"
date: "2024-02-29T09:46:49+00:00"
lastmod: "2024-03-01T09:47:05+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of February 2024 that got published on jfx-central.com during this month. Core Christopher Schnick - by Frank Delporte"
canonical: "https://webtechie.be/post/2024-02-28-javafx-links-of-february-2024/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-january-2024"
  - "javafx-links-of-december-2023"
  - "javafx-links-of-november-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of February 2024 that got published on [jfx-central.com](https://www.jfx-central.com/) during this month.

## Core

* [**Christopher Schnick** shared a video](https://twitter.com/crschnick/status/1752252092029251610): "The JavaFX 22 platform preferences API in action. You can now query and observe the system color schemes..
* [Gluon announced](https://techhub.social/@gluonhq/111901859568537205) that new EA releases for the upcoming JavaFX 22 (22-ea+28) and JavaFX 23 (23-ea+3) platforms are available for download from [gluonhq.com](https://gluonhq.com/products/javafx/) and from Maven Central.

### Highlights from the openjfx-dev mailing list

* Version 23 will [bump the minimum version of the JDK needed to run JavaFX to JDK 21](https://github.com/openjdk/jfx/pull/1370).
* A [new feature is proposed: RichTextArea](https://github.com/andy-goryachev-oracle/Test/blob/rich.jep.review/doc/RichTextArea/RichTextArea.md). "Intended to bridge the functional gap with Swing and its StyledEditorKit/JEditorPane. The main design goal is to provide a control that is complete enough to be useful out-of-the box, as well as open to extension by the application developers. We are looking for feedback, and will update the proposal based on the suggestions we receive from the community."
* **Johan Vos** shared the following message: "I created a [branch in the jfx-sandbox repository](https://github.com/openjdk/jfx-sandbox/tree/johanvos-headless) for experimenting with a headless glass platform. This addresses [JDK-8324941](https://bugs.openjdk.org/browse/JDK-8324941) where I suggest a POC for a Headless platform. There are a number of use cases for this, including:
  * Applications that require JavaFX rendering without presenting this to a window (and instead send it to a printer for example).
  * Running tests without requiring a window manager."

## Applications

* [**Amit Kumar Mondal** announced OSGi.fx v2.4.4](https://twitter.com/am1t_m0ndal/status/1753042932821623124): "Packed with enhancements, integrating Java 21 and JavaFX 21 seamlessly." OSGi.fx is an easy-to-use desktop application to manage OSGi frameworks remotely. You can find the [release notes on GitHub](https://github.com/amitjoy/osgifx/releases/tag/v2.4.4).
* **Sean Phillips** is using the open source Trinity software for deep fake detection: "[This clip demonstrates visualizing a 3D Fast Fourier Transform (FFT)](https://twitter.com/SeanMiPhillips/status/1755246775504433374) of the January 2024 deep fake of US President Biden. Trinity is written in Java using JavaFX for rendering."
* [PDFsam released the new PDFsam Basic 5.2.2](https://twitter.com/PDFsamOSS/status/1755193888908665333): "We upgraded the bundled Java, JavaFX and few other dependencies. We also fixed/improved drag and drop of files into the selection table with autoscroll at the edges and drop of files between rows." See the [release notes](https://blog.pdfsam.org/bug-fix/new-release-5-2-2-for-pdfsam-basic/2520/).
* **David Youcef Khodja** [shared a video of Geoptic](https://twitter.com/DavidYKhodja/status/1759703816973283401), an app to measure distances and surfaces on maps: "I'm actually using my own custom UI controls and CSS inspired by IntelliJ IDEA", made with SpringBoot, JavaFX, Leaflet, JavaScript.
* **Heshan Thenura Kariyawasam** is [replicating Instagram's story feature](https://twitter.com/Heshantk/status/1758869052066329014): "This Java application dynamically changes background color based on the dominant color of uploaded images." Sources are [available on GitHub](https://github.com/heshanthenura/DominantBackgroundColor).
* **Namuan** [shared a video of MirrOllama](https://twitter.com/deskriders_twt/status/1759688935444287993): "Desktop application to talk to multiple models using [Ollama](https://github.com/ollama/ollama) (Get up and running with large language models locally). Idea is to use multiple (up to 3) models simultaneously and select the best answer. ️I built it for my own use but hopefully useful for others as well." Sources are [available on GitHub](https://github.com/namuan/mirrollama).

## Games

* [**ilyriadz** open sourced his JavaFX game engine "mawdja"](https://github.com/ilyriadz/mawdja) and "My javafx game ['Almaseer Almahtoom'](https://ilyriadz.itch.io/almaseer-almahtoom) RC1 was realized."
* **Almas Baim** announced new speech-to-text in FXGL:
  * "The next version of FXGL brings the fxgl-intelligence module with a range of features. [The one shown in the video is text to speech](https://twitter.com/AlmasBaim/status/1756048026882150872). The API is pure Java code. You can play with it very soon."
  * "An [example of speech to text](https://twitter.com/AlmasBaim/status/1756610895088636371), coming to the next version FXGL. The left app uses text to speech, the right one uses speech to text. Demo shows: Java String -\> speech output (speakers) -\> speech input (mic) -\> String. Anyone wants to build speech controlled games? 😃"
* **Heshan Thenura Kariyawasam** , **OrangoMango** , and **Sean Phillips** are [cooperating on X and sharing experiences](https://twitter.com/SeanMiPhillips/status/1755640542120063394).

## Components, Libraries, Tools

* [**Frank Delporte** published a small update about the Lottie4J project](https://lottie4j.com/index.html) as he is diving deeper into the Lottie-format (vector animations for the web).
* [**Max Rydahl Andersen** is expecting 2024 to be the year of Java on desktop](https://twitter.com/maxandersen/status/1751725403553546275): "We got early [JavaFX support for Quarkus contribution in Quarkuverse Hub](https://github.com/quarkiverse/quarkus-fx)".
* [**Hallvard Trætteberg**](https://twitter.com/haltraet/status/1753728173387153866) shared [fxml-template-processor on GitHub](https://github.com/hallvard/fxml-template-processor): "String templates (preview in Java 21) allow you to inject expression values into strings, so you don't need to use explicit string concatenation, StringBuilder or formatting. An important point of template strings is building other data types from the string and constrain and validate the input accordingly. This project contains a String template processor for FXML, so you can use template strings for FXML code and convert to Node structures with FXMLLoader."
* **Pedro Duque Vieira** [shared videos showing work in progress on NavigationPane Control](https://twitter.com/P_Duke/status/1760765867992731766) that's in the FXComponents library, using the FXThemes library behind the scenes to achieve a background blur effect on the window.

## Podcast, Video, Books

* For the online JChampions Conference, **Paul and Gail Anderson** presented "Are You Game? Mobile Development with Modern Java, JavaFX, and GraalVM". The video recording [is available here](https://www.youtube.com/watch?v=9h-BoB6UjiE).
* At Fosdem in Brussels, **Frank Delporte** showed a JavaFX AI-Chat-application based on LangChain4J to interact with a documentation set. The [video and links are available on his blog](https://webtechie.be/post/2024-02-02-links-presentation-experiment-ai-llm-chat-with-docs/).
* [**Wolfgang Weigend** announced](https://twitter.com/wolflook/status/1744805486690537806) the [final agenda online](https://www.zeiss.com/meditec/en/news-events/events/jfx-adopters-meeting.html) for the "JFX Adopters Meeting" on 6th of March 2024.

## Tutorials

* Published on JavaTechOnline: "[JavaFX Tutorial: How To Create Rich Desktop Applications In Java?](https://javatechonline.com/javafx-tutorial-javafx-applications-in-java/)"

## Miscellaneous

* WebFX now [supports WebGL](https://github.com/webfx-project/webfx/discussions/27): "This means that you can program a JavaFX canvas with a WebGL context in the browser." A [web demo is available here](https://cube.webfx.dev/).
* [Message from **Johan Vos**](https://mastodon.social/@johanvos/111838246384894266): "[**Laurent Bourges**](https://mastodon.social/@laurent_bourges) is an exceptional *independent* OpenJDK and OpenJFX contributor. I guess most of the developers using his code don't even realise the amazing work he does. Please [let him know how much you appreciate his work on the Marlin renderer](https://mastodon.social/@laurent_bourges/111776388216851460)."
* [**Dirk Lemmermann** is a big fan of AtlantaFX](https://twitter.com/dlemmermann/status/1752347204474249564): "We liked the sampler app of [AtlantaFX](https://www.jfx-central.com/libraries/atlantafx) so much that we are now using it in-house for our own theming / styles app."

## JFX Central

* [Webswing got listed in the tools section](https://www.jfx-central.com/tools/webswing). It's a web server that allows you to run your (old) Swing, JavaFX, NetBeans or Applet application inside your web browser in a blink of an eye. They posted a nice [shoutout to JFX Central on Twitter/X](https://twitter.com/Webswing_org/status/1753054902941556928).
* Available soon: [new detailed icon page](https://twitter.com/dlemmermann/status/1756715833848459656) where you can find out everything there is to know about the icon, e.g. the Ikonli "literal" (code), the CSS rule, the Java code required, the maven / gradle dependency, the SVG path, and so on. The main reason for having this detail page is so that you can send a URL to somebody else pointing directly at a specific icon and all of its info.
* All the JFX Central Links Of The Week of January got [posted on Foojay.io](https://foojay.io/today/javafx-links-of-january-2024), the website for Friends Of OpenJDK.
