---
title: "Announcing JavaFX: June 2021 Community Update"
date: "2021-06-21T08:22:42+00:00"
lastmod: "2021-06-21T08:29:14+00:00"
description: "To get a general idea of what the community would like to see in JavaFX in the future, I have asked developers to share their thoughts!"
authors:
  - "almasbaimagambetov"
image: "Favicon-3-2.png"
categories:
  - "JavaFX"
  - "Research"
  - "Surveys"
tags:
related_posts:
frozen: false
---

JavaFX is a modern cross-platform open-source UI toolkit for the JVM. It provides a wide range of built-in controls and platform-specific features, such as hardware acceleration. If you have not used JavaFX before, you can get started at [openjfx.io](https://openjfx.io/).

In order for any technology to grow and improve, community support and feedback are paramount. To get a general idea of what the community would like to see in JavaFX in the future, I have [asked developers](https://twitter.com/AlmasBaim/status/1401833314890178566) to share their thoughts:
> Hey [#JavaFX](https://twitter.com/hashtag/JavaFX?src=hash&ref_src=twsrc%5Etfw) devs,
>
> what is the single most important feature for your project that is missing from JavaFX?
>
> Consideration of the complexity and compatibility with the [#openjfx](https://twitter.com/hashtag/openjfx?src=hash&ref_src=twsrc%5Etfw) codebase is appreciated. This can help determine whether it should be an internal or external addition.
>
> --- Almas Baim (@AlmasBaim) [June 7, 2021](https://twitter.com/AlmasBaim/status/1401833314890178566?ref_src=twsrc%5Etfw)

The collated results are given below. Each entry also includes links to open-source libraries and other resources that may provide (or help develop) some of the necessary functionalities.

#### 1. Fixes to Outstanding Bugs

The teams behind openjfx and Gluon have been working hard to ensure high quality contributions are made to the JavaFX codebase. JavaFX 17 will be [a massive release](https://twitter.com/GluonHQ/status/1387658970417045505) with a number of key issues being fixed in this version. However, there are still a few pesky bugs that are encountered by the JavaFX developers in their projects. A few of the links provided by the community are listed below, with TableView being specifically mentioned by a number of reporters. Time-permitting, members of the community are also encouraged to work on issues important to them or initiate relevant discussions on the mailing list to reach a solution and expedite the process.

Resources:

* [macos11: modifier keys are ignored when Drag and Drop a file from finder](https://bugs.openjdk.java.net/browse/JDK-8264172)
* [TableView: selection state lost on filtering](https://bugs.openjdk.java.net/browse/JDK-8087910)
* [TableView: Clicking outside of the edited cell, node, or entry should commit the value](https://bugs.openjdk.java.net/browse/JDK-8089514)
* [macOS: Accelerator assigned to button in dialog fires menuItem in owning stage](https://bugs.openjdk.java.net/browse/JDK-8205915)

#### 2. New Media Formats

The [list](https://openjfx.io/javadoc/16/javafx.media/javafx/scene/media/package-summary.html) of currently supported media types in JavaFX includes:

* AAC (Audio)
* MP3 (Audio)
* PCM (Audio)
* H.264/AVC (Video)

In addition to these, the community would also like to see support for:

* High-resolution audio, such as FLAC.
* H.265, VP9 and AV1 video.

Furthermore, other enhancements related to media were mentioned:

* Webcam support.
* Frame grabber API.

Resources:

* [vlcj-javafx](https://github.com/caprica/vlcj-javafx-demo) is an example demo application using the currently in development vlcj-5.0.0 and LibVLC 4.0.0.
* [jave2](https://github.com/a-schild/jave2) is a Java wrapper of the ffmpeg project.
* [java-stream-player](https://github.com/goxr3plus/java-stream-player) is a Java audio controller library.
* [SVT-AV1](https://gitlab.com/AOMediaCodec/SVT-AV1) is an AV1-compliant encoder/decoder library core written in C.
* [webcam-capture](https://github.com/sarxos/webcam-capture) allows using a built-in or an external webcam directly from Java.
* [lancoder](https://github.com/jdupl/lancoder) provides video and audio encoding distributed across multiple machines in a local network, controlled by a simple web interface.

#### 3. Chromium-like WebView

Many have expressed interest in getting WebView support to the same level as Chromium. On a high-level, this appears to be a significant undertaking. It would be beneficial to discuss further what specific aspects require attention the most and produce a list of tractable actions.

Resources:

* [java-cef](https://github.com/chromiumembedded/java-cef) is a simple framework for embedding Chromium-based browsers in other applications using the Java programming language.

#### 4. Native Rendering

The introduction of [PixelBuffer](https://openjfx.io/javadoc/16/javafx.graphics/javafx/scene/image/PixelBuffer.html) in JavaFX 13 was a much welcomed feature, allowing developers to avoid copying pixel data. Further improvements relate to access to native rendering, which has been and remains a popular discussion topic among JavaFX developers.

Other suggestions that have been mentioned also include:

* An implementation of the JavaFX API based on the Skia rendering engine.
* Non-swing based tools for writing JavaFX images to files.
* WebGL support.
* Thread-safety for PixelBuffer.
* Improved SVG support.

Resources:

* [Skija](https://github.com/JetBrains/skija) is a high-quality Java bindings library for Skia -- an open source 2D graphics library which provides common APIs that work across a variety of hardware and software platforms.
* [DriftFX](https://github.com/eclipse-efx/efxclipse-drift) allows rendering any OpenGL content directly into JavaFX nodes.
* [Writing JavaFX Image to file](https://stackoverflow.com/questions/27054672/writing-javafx-scene-image-image-to-file) is a stackoverflow implementation that can be helpful with a specific Image format.

#### 5. Advanced 3D

Whilst the JavaFX codebase already allows some 3D development, it is certainly possible to enhance it. For example, custom shader support is one of the features that is commonly requested and has also been discussed on the openjfx mailing list on a number of occasions.

Resources:

* [FXyz](https://github.com/FXyz/FXyz) is a JavaFX 3D visualization library that includes many custom shapes and a few 3D model loaders.
* [jfx3dimporter](http://www.interactivemesh.org/models/jfx3dimporter.html) provides a number of 3D model loaders.
* [FXGL](https://github.com/AlmasB/FXGL) is a game engine that provides extra 3D support within the game development context.

#### 6. Layout and Platform-specific Features

JavaFX developers would also like to see improved support for Look and Feel on major OSes (Windows, Mac OS, Ubuntu Linux, iOS and Android). This aligns well with recent theme related discussions on openjfx. Other suggestions related to user experience also include:

* Built-in docking layout.
* CSS support for animations, transitions and chained effects.
* SystemTray API.
* Rich-text support like in Swing.
* Active keyboard input language detection.

Resources:

* [JMetro](https://www.pixelduke.com/java-javafx-theme-jmetro/) is a set controls, stylesheets and skins inspired by Fluent Design and adapted to fit JavaFX.
* [JFoenix](https://github.com/sshahine/JFoenix) is a Java library that implements Google Material Design using Java components.
* [MaterialFX](https://github.com/palexdev/MaterialFX) is a Java library which provides material components for JavaFX.
* [RichTextFX](https://github.com/FXMisc/RichTextFX) provides a JavaFX text area that can display custom objects in-line with different text styles.

#### 7. Easy Deployment

The current JavaFX ecosystem is sophisticated enough to provide a range of options for producing self-contained platform-specific executables. However, in some cases the deployment options and / or applicable configurations may not be trivial. This is of particular importance to new JavaFX developers. Hence, a (potentially unified and) simpler workflow has been requested with an easy way to produce auto-updatable native applications.

Resources:

* [jreleaser](https://github.com/jreleaser/jreleaser) can be used to release Java projects quickly and easily.
* [javafx-maven-plugin](https://github.com/openjfx/javafx-maven-plugin) is a Maven plugin that can run and create custom images for JavaFX 11+ applications.
* [gluonfx-maven-plugin](https://github.com/gluonhq/gluonfx-maven-plugin) is a Maven plugin that leverages GraalVM, OpenJDK and JavaFX 11+, by compiling the Java Client application and all its required dependencies to native code, so it can directly be executed as a native application on the target platform.
* [JPackageScriptFX](https://github.com/dlemmermann/JPackageScriptFX) demonstrates how projects can use scripts to build self-contained, platform-specific executables and installers of their JavaFX applications via the jdeps, jlink, and jpackage tools.
* [update4j](https://github.com/update4j/update4j) is an auto-update and launcher library designed for Java 9+.
* [fxlauncher](https://github.com/edvin/fxlauncher) is an auto updating launcher for JavaFX Applications that can be combined with JavaFX native packaging to get a native installer with automatic app updates.

#### Summary

There are two key take-away messages:

**1. JavaFX has a strong and vibrant community that is more active than ever before.**

**2. The community is formed by developers from various real-world domains, where JavaFX is actively being used.**

In summary, we should be mindful that some of these features, while desirable, may not always align with the JavaFX project goals. In such cases, it would be sensible to initiate further discussions and explore whether the required functionality should be developed as an external library (on top of JavaFX) rather than internally (as part of JavaFX).

P.S. If I have missed any of the replies or relevant libraries, please comment below and I will update the article to include the missing items.
