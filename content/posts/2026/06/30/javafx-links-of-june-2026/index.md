---
title: "JavaFX Links of June 2026"
date: "2026-06-30T07:23:31+00:00"
description: "Here are the JavaFX LinksOfTheMonth of June 2026. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there anything you want to…"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-may-2026"
  - "javafx-links-of-april-2026"
  - "javafx-links-of-march-2026"
  - "javafx-links-of-february-2026"
frozen: false
---

Here are the JavaFX LinksOfTheMonth of June 2026. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* [Message by **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3mnans5s67c2u): "*I've spent most of today (and the past few days) on JavaFX 17/21/25 LTS. It's a lot of work, but almost a pleasure because the tools/procedures are so clear. Skara is extremely helpful with backports. And it's deterministic. JavaFX is very well maintained.*"
* From the dev mailinglist: [**Michael Strauß** integrated JDK-8385459](https://github.com/openjdk/jfx/commit/738be0f10f8dbbbe182aa3d06ee86313fa181bb1) in [PR #2177](https://github.com/openjdk/jfx/pull/2177): Animations should respect reducedMotion preference: "*Several JavaFX controls use animations to convey state changes, but none respected the reducedMotion accessibility preference. This change makes TableRowSkinBase, TitledPaneSkin, TabPaneSkin, PaginationSkin, and Charts honor Scene.Preferences.reducedMotion.*"
* Apparently, the JFX Adopters Meeting 2026 led to a pull request by **Marius Hanl** in the OpenJFX repository: [8386663: Stylesheet/StyleClass list should be lazily initialized](https://github.com/openjdk/jfx/pull/2191). Thanks, [**Oliver Kopp** for sharing this info](https://mastodon.acm.org/@koppor/116810873255149882).

## Applications

* Video by [**Ethan Lee**](https://bsky.app/profile/ingstudios.dev/post/3mncdhcnbds2f): "*I Built My Own Agentic AI Browser [Turtlebrowse](https://turtlebrowse.ingstudios.dev/) now with Java CEF, Swing, and embedded JavaFX components. [Go watch it on YouTube](https://www.youtube.com/watch?v=WzU8Cnl3kE0).*"
* [**Ethan Lee** released Turtlebrowse v1.1.1](https://bsky.app/profile/ingstudios.dev/post/3mondjccunc2o), the open-source browser built to be agentic: "*This brings features like a multi-process architecture and custom profiles. It also brings patches for internal API errors. It uses Gemma4 under the hood, perfect for everyday tasks. Get it at [turtlebrowse.ingstudios.dev](https://turtlebrowse.ingstudios.dev/)!*"
* [**Gufran Thakur** created a simple, minimal Canvas Application in JavaFX](https://www.reddit.com/r/java/comments/1u10ble/created_a_simple_minimal_canvas_application_in/): "*After like a year finally getting back to JavaFX, decided to make an excalidraw inspired app for fun. It's a very minimal canvas app with basic features. [Source code is on ExplainFX](https://github.com/gufranthakur/ExplainFX). Features: Drawing, Create Squares and Circles, Text, Vary stroke size/font size,... Please let me know if you have any questions!*"
* **Max Xiong** continuous his "Building In Public" journey for his DataCollie application. [Follow him on Bluesky for regular updates and screenshots](https://bsky.app/profile/maxiong.bsky.social/post/3mnulx7aomc2u): "*Tonight I implemented the custom UI styling for Project context menu items.*"
* [Found on Reddit, by **xdsswar**](https://www.reddit.com/r/JavaFX/comments/1u9hfn1/dropping_a_small_pdf_viewer_for_you_guys_javafx/): "*Dropping a small PDF viewer for you guys (JavaFX + native PDFium). It's done on native PDFium via Java's FFM API (no PDFBox, no AWT). Has zoom, text selection, search, thumbnails, the usual, would love some feedback. [Repo link](https://github.com/xdsswar/ultimate-pdf-viewer).*"
* [By **RGiskard7**](https://www.reddit.com/r/JavaFX/comments/1u5zjpo/i_built_jylos_a_localfirst_opensource_knowledge/): "*I built Jylos, a local-first open-source knowledge management app using Java and JavaFX. It started as a personal project to explore desktop application architecture, JavaFX, Markdown processing and software design. Over time it evolved into a complete application with: Markdown notes with live preview, Wiki-links and backlinks, Interactive knowledge graph,... Everything is stored locally. No accounts, no cloud backend and no telemetry. The [project is open source](https://github.com/RGiskard7/jylos) under the MIT license and binaries are available for Windows, Linux and macOS. I'd really appreciate any feedback, especially from JavaFX developers.*"
* [**Mechanical-pasta** shared his "First JavaFx Application" on Reddit](https://www.reddit.com/r/JavaFX/comments/1ua30k4/my_first_javafx_application/): "*I tried JavaFx, and, as a support project to learn the language, I've decided to build my own MineSweeper but with some differences. You can find it [here if you're interested](https://github.com/TargolLagadec/MineSweeperTribute).*"
* In the [Links Of The Week of 2026-05-29](https://www.jfx-central.com/links/2026-05-29), we shared "DiskSpace", a cross-platform disk space visualizer by **Marcus Hirt** . In the blog post [Using GraalVM, JavaFX and a Clanker to Build a Cross Platform Desktop App](https://hirt.se/blog/?p=1606), he shares a lot of background information about the project, the architecture, and the build process.

## Components, Libraries, Tools

* [**Striking_Creme864** shared a video on Reddit](https://www.reddit.com/r/JavaFX/comments/1tul6x8/live_property_editing_for_javafx_nodes_in_devtools/): "*Live property editing for JavaFX nodes in DevTools. When building JavaFX applications, we often need not only to inspect a node's properties, but also tweak them to see how they behave. Restarting the app every time to test small changes quickly becomes a major time sink. So we added live property editing directly to the DevTools in our platform (TabShell). The property editor supports three simple forms: basic values, enums, and Insets, but this made UI debugging and iteration much faster.*"
* [**Dirk Lemmermann** shared screenshots](https://bsky.app/profile/dlemmermann.bsky.social/post/3mncofbca722s) of the new `StageStyle.EXTENDED`: "*I recently updated the FlexGanttFX showcase application to use the new HeaderBar component and StageStyle.EXTENDED type. This results in a very clean and professional looking UI.* "
  * You can also try it online at [demos.jpro.one/flexganttfx-showcase.html](https://demos.jpro.one/flexganttfx-showcase.html) or install locally [via jdeploy](https://www.jdeploy.com/~flexganttfxshowcase).
* **Frank Delporte** released [Lottie4J 1.2.4](https://lottie4j.com/releases/index.html): "*Fixed Lottie arc rendering: constrained the easing solver to prevent bezier curve divergence. Added bisection fallback for flat-point curves. Fixed full-circle trim path flickering caused by floating-point precision loss in offset wrapping. Fixed border rendering error. Added Pi4J test file.* " Available on [Maven Central](https://central.sonatype.com/search?q=g:com.lottie4j).
* [FXML/2 for JavaFX](https://plugins.jetbrains.com/plugin/32337-fxml-2-for-javafx): IntelliJ IDE plugin for the [FXML/2 markup format](https://jfxcore.github.io/fxml-compiler/), supporting syntax highlighting, folding, formatting, EditorConfig-aware indentation, tag and attribute resolution, code completion, and navigation to JavaFX classes. Presented as "FXML 2.0: Write Markup, Ship Bytecode" by **Michael Strauß** at the [JFX Adopters Meeting](https://www.zeiss.com/meditec/en/news-events/events/jfx-adopters-meeting-2026.html).
* **Jonathan S. Fisher** is working on [DiFX, a CDI / Dependency Injection For JavaFX](https://github.com/exabrial/difx): "*The goal is to have CDI backed beans and events inside JavaFx Desktop Applications. I could use a few eyes/ideas before I cut an initial release and stabilize the API.*"
* [**techsenger** published ShellFX on GitHub](https://github.com/techsenger/shellfx): a platform for building JavaFX applications using an extended MVP pattern. Part of the growing Techsenger JavaFX library ecosystem (alongside CEFFX, TabPanePro, PatternFX, StagePro, etc.).
* [FX Flow 0.6.1](https://github.com/int4-org/FX/releases/tag/0.6.1) got released: declarative, fluent UI library for JavaFX. Adds validation support for atomic multi-value updates. More info by **john16384** on [Reddit/r/JavaFX](https://www.reddit.com/r/JavaFX/comments/1uf8l0h/fx_flow_061_released_declarative_ui_building_for/).

## Podcasts, Videos, Books

* [**Frank Delporte** published a blog and video](https://webtechie.be/post/2026-06-04-interview-with-naail-from-lottiefiles/): "*Lottie4J started as a single question: can JavaFX render Lottie animations without a WebView? That question turned into a library. The library attracted pull requests. And now I had a 50-minute conversation with **Naail Abdul Rahman**, R\&D engineer at LottieFiles, to talk about where the format is heading and what that means for a Java implementation.*"
* [**Pedro Duque Vieira** shared a video on LinkedIn](https://www.linkedin.com/posts/pedro-duque-vieira-2644038_healthcare-healthtech-healthcareit-ugcPost-7467911189063245824-igkN/): "*New capability added to the IKE Knowledge Layout Editor: display semantics can now be viewed in a table format. This provides a more structured way to inspect semantic information, which can be especially useful when working with large numbers of semantics.*"
* A live session of 2,5 hours with **Johannes Rabauer** and **Anton Arhipov** : [Building a MIDI Visualizer with Junie, IntelliJ and JavaFX](https://www.youtube.com/live/LekXCf-FJ00): "*AI assisted development is becoming a core part of modern software engineering. But what does that actually look like in a real Java project? In this live coding session, we explore AI powered development inside IntelliJ IDEA while building a JavaFX application that visualizes MIDI music through animated waveform based visualizations. Rather than treating AI as a code generator, we will use Junie and other IntelliJ AI capabilities as development partners throughout the process. Together, we'll explore how AI can help with architecture, implementation, refactoring, testing, and day to day development decisions.*"
* **Frank Delporte** interviewed **David Gutierrez** in a new video: [JavaFX In Action #27 with David Gutierrez about JMathAnim to Create Mathematical Animations](https://webtechie.be/post/javafx-in-action-%2327-with-david-gutierrez-about-jmathanim-to-create-mathematical-animations/): "*JMathAnim is a Java library and UI built on JavaFX that lets you create animated mathematical visualizations. The goal is simple: make it easy to build videos that explain mathematical concepts through animation. It's inspired by the kind of content you see on YouTube channels like 3Blue1Brown.*"

## Conferences, Presentations

* ZEISS Meditec hosted the [JFX Adopters Meeting 2026](https://www.zeiss.com/meditec/en/news-events/events/jfx-adopters-meeting-2026.html) on June 16 in Munich. The full-day agenda included: "Low-Latency JavaFX: Robotics and Native Bindings" (**Florian Enner** ), "JavaFX -- Status and Beyond" (**Wolfgang Weigend** ), "JPRO -- the Future of Unified JavaFX Application Development across Desktop and Web" (**Florian Kirmaier** ), "FXML 2.0: Write Markup, Ship Bytecode" (**Michael Strauß** ), "Beyond MVC: A Practical Guide to MVVM in JavaFX" (**Tibor Malanik** ), "Diagnosis in the Context of eBike Systems with JavaFX" (**Frido Fechner** ), "JavaFX for Electronics Control" (**Thorsten Stüker** ), "Lets Contribute Together" (**Marius Hanl** ), and "Spreadsheet Calculation and Document Processing in JavaFX: Lessons from SCell and bk.text" (**Vasily Smeltsov** ). Pictures shared by **Wolfgang Weigend** :
  * "*The [official opening of the JFX Adopters Meeting 2026](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3mofuegyfvc2n) by **Christian Heilmann** and **Lisa**, both work with JavaFX at Zeiss Meditec AG.*"
  * "*The [robotics session](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3mofusejv5k2l) 'Low-Latency JavaFX: Robotics and Native Bindings' by Florian Enner.*"
  * "*[Great welcome in the morning](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3mofuzfnzes2l) with **Tom Schindl** , **Stefano Negri** and **Dirk Lemmermann** — all about JavaFX applications*"
* [**Wolfgang Weigend**](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3mocwnoogjc2n): "*Please find the session '[JavaFX UI technology as a central component of the Java ecosystem](https://jugf.github.io/posts/javafx-ui-technologie-als-zentraler-baustein-im-java-okosystem-24-06-2026/)' on Wednesday 24th of June 2026 at the Java User Group Frankfurt am Main in the National Library.*"

## JFX Central

* New content on JFX Central:
  * Library: [SvgNode](https://www.jfx-central.com/libraries/svgnode), a lightweight JavaFX node for rendering SVG paths at any size. Fully supports FXML, property binding, and CSS styling.
  * Video: [JavaFX In Action with David Gutierrez about JMathAnim](https://www.jfx-central.com/videos/pb54youm6AM).
* The JavaFX Links Of The Week of May got [bundled and published on Foojay.io](https://foojay.io/today/javafx-links-of-may-2026/).
