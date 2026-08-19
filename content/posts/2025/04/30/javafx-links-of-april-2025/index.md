---
title: "JavaFX Links of April 2025"
date: "2025-04-30T10:06:10+00:00"
description: "Here is the overview of the JavaFX LinksOfTheMonth of April 2025. You can find the weekly lists on jfx-central.com. Did we miss anything? Is there…"
canonical: "https://webtechie.be/post/2025-04-28-javafx-links-of-april-2025/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-march-2025"
  - "javafx-links-of-february-2025"
  - "javafx-links-of-january-2025"
  - "javafx-links-of-december-2024"
frozen: false
---

Here is the overview of the JavaFX LinksOfTheMonth of April 2025. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* [Slides from the presentation "JavaFX 24 and Beyond"](https://cr.openjdk.org/~kcr/presentations/javaone-2025/JavaFX-24-Final.pdf) by **Kevin Rushforth** at JavaOne, March 18th, 2025.
* [ApiDoc of JavaFX 24](https://apidia.net/mvn/org.openjfx/javafx/24/) is available on [APIdia](https://bsky.app/profile/apidia.net), a growing collection of high-quality APIdocs, interlinked, without tracking.
  * [**Stefan Richthofer** shares a screenshot](https://bsky.app/profile/stewori.bsky.social/post/3lloo6w6ixc25): "*APIdia JavaFX 24 docs do now include the incubating modules, but you have to adjust visibility settings to see them.*"
* [**Johan Vos** on Mastodon](https://mastodon.social/@johanvos/114239659224933655): "_I'm still working on consolidation of JDK build with OpenJFX sources. Here is a [post describing my progress and a suggestion](https://johanvos.wordpress.com/2025/03/27/building-a-jdk-including-openjfx-part-2/). TLDR: I added a `MARKDOWN_HASH04d3b60467c08e78fba0bc4a87592b9fMARKDOWN`*HASH* configure option for the JDK. Key goal is not to make JDK build system harder to maintain. I'll be honest about this. It's hard work, requires diving into very different low-level things, and in the end, JavaFX developers won't see anything from this work. But I believe I have to do this, as it is vital to JavaFX."
  * And [continues](https://mastodon.social/@johanvos/114269821014592304): "*I've spent way too much time trying to find an elegant approach in generating the JavaFX shader files with the existing gradle build, and copy the results into the OpenJDK fork that can build OpenJFX. Unless someone else wants/can do this, I will move that part to the OpenJDK logic as well. Should be easier. ... I have to say that I'm slightly surprised that no other companies are helping me [with this effort](https://johanvos.wordpress.com/2025/03/27/building-a-jdk-including-openjfx-part-2/). I know at least 2 companies that distribute a JDK including OpenJFX mods. You would think they'd love this, but it's very silent.*"
* The release notes for JavaFX 24.0.1 are [being prepared in this pull request](https://github.com/openjdk/jfx24u/pull/19/files).
* [**Chad Preisler** shared some tips to distribute JavaFX apps](https://bsky.app/profile/chadpreisler.bsky.social):
  * "_One excellent way to distribute JavaFX applications is to use jlink to build custom images. You can build for Linux, Windows, and Mac all on one machine without having all the host operating systems. The package is small, and you don't need to install the JDK on each client._3
  * "*My JavaFX application bundle, built for MacOS X with jlink is just 41MB. I built the and bundled the app on Linux and ran it on OS X. No need to install Java on every client with a jlink image. I used JDK 24 to build the application.*"
* A tip by **Wolfgang Weigend** related to [JEP 493](https://openjdk.org/jeps/493): "_Add JavaFX 24 to your jdk-24.jdk image with cmd `jlink --add-modules javafx.base,javafx.controls,... --output image` and verify file release and the directory `MARKDOWN_HASH456c8dcb2e4cb13faea8318519ab5263MARKDOWN`*HASH*." (See [the original message for the full command](https://bsky.app/profile/wolfgangweigend.bsky.social/post/3llya7y6bfs23)).
* Message from **Gluon** : "*Another CPU release for JavaFX! Today, we released JavaFX 24.0.1, JavaFX 17.0.15 and JavaFX 21.0.7. [Get it from gluonhq.com](https://gluonhq.com/products/javafx/). Thanks to the fantastic JavaFX community for the combined work.*"

## SceneBuilder

* [SceneBuilder 24.0.0](https://gluonhq.com/scene-builder-24-0-0-ga-is-here/) is available. Highlights:
  * Scene Builder Kit is now published to Maven Central, making it easier for developers to integrate it into their software.
  * The Gluon controls, that are part of Scene Builder, were tightly coupled with Scene Builder Kit. Now those has been abstracted away into a pluggable component called Gluon Plugin.
  * Scene Builder has been updated for full compatibility with the Java Platform Module System (JPMS).
  * Logging to console is now enabled by default to make it easier to debug issues in Scene Builder.
  * Multiple fixes went into Accordion, DialogPane, TextField etc. to make Scene Builder more robust.

## Applications

* [**Patrik Karlström** released a new version of nbRsync](https://bsky.app/profile/trixon.se/post/3lmcattkucc2w): "*A GUI for rsync, written in Java and JavaFX atop the netbeans platform. Available as [appimage, snap, platform zips with and without a bundled JDK 24](https://github.com/trixon/nbRsync/releases/tag/v25.04).*"
* [**Serendipity** released version 2 of SmartFinder](https://bsky.app/profile/serendigityinfo.bsky.social/post/3lma43kntls27): "*Now with advanced filter building to create powerful, custom queries on file attributes and metadata. Stay tuned \& level up your file search!* " A [video demo is available on YouTube](https://www.youtube.com/watch?v=Vsm3W5zeP2A).
* [JabRef received many GSOC proposals](https://www.linkedin.com/posts/jabref_jabref-dante-2025-activity-7315844222283116544-WNhx/): "_The team is now busy reviewing them. Meanwhile, our maintainer Dr. Oliver Kopp did a talk at the DANTE2025 (a TeXLaTeX conference) presenting the development process of JabRef and how users can test, provide feedback and participate in the community.  
  The [slides (German only) from the talk are available online](https://www.dante.de/wp-content/uploads/2025/04/Oliver_Kopp-JabRef.pdf)._"
* An impressive combination of JavaFX and AI by **William Antônio Siqueira** : [LLM FX, an LLM Desktop Client free for everyone](https://github.com/jesuino/LLMFX)!
  * And he got some help: "_A single contribution by **Max Rydahl Andersen** made LLM FX look much better and professional. Thank you so much, Max! [In the video you can see it generating another report](https://www.linkedin.com/posts/william-ant%C3%B4nio-siqueira-968bba14_ai-tools-llm-ugcPost-7321383037257039875-blf4/). Notice that the report is not well formatted because of my layout (it is a grid based layout, there's a lot of room for improvements..._"
* [**Sean Phillips** shared](https://bsky.app/profile/seanmiphillips.bsky.social/post/3lneezgggrs27): "*I made a major revamp update to [Trinity XAI's Readme](https://github.com/trinity-xai/Trinity/blob/main/README.md). Added tons of content featuring the past year's development of new tools with descriptions and images.* "
  * And a [new video](https://www.youtube.com/watch?v=3erhqAsC1Og): "*Demonstrating Trinity XAI's 'Hyperdrive' importer to vectorize AI generated human faces using a locally hosted version of the CLIP multimodal embeddings model. Hyperdrive supports both images and text using a built in OpenAI API compatible REST client.*"
* Announced they are running on the latest JavaFX:
  * [LogoRRR](https://bsky.app/profile/logorrr.bsky.social/post/3lmzqfzkjis2t). "*Did you know, LogoRRR runs with the latest JavaFX version on windows, mac and linux! 🤗*"
  * [JPro](https://bsky.app/profile/jpro.one/post/3lmzd4c6hmc2q): "*With JPro release 2025.2.0, we've added support for JavaFX 24 as well as improvements to Canvas shape clipping, instance startup timeout logging, and more.*"
  * [SmartFinder](https://bsky.app/profile/serendigityinfo.bsky.social/post/3ln6fezarnc2w): "*SmartFinder runs on Java 24! Better performance and improved stability. Always one step ahead! SmartFiner is an alternative to MacOS Finder and Windows Search.*"

## Components, Libraries, Tools

* [Here you can find a list of all the Visual Studio Code extensions](https://open-vsx.org/?search=sosuisha&sortBy=relevance&sortOrder=desc) created by [**Hidekazu Kubota**](https://github.com/sosuisen/): "*I have registered all the VSCode extensions I created for JavaFX in the Open VSX repository. Now, it's possible to develop with JavaFX in VSCode-compatible editors like TheiaIDE.*"
* JDeploy, a tool that helps you to create installers/updaters, announced: "[A New Chapter for jDeploy: The Desktop App is Here](https://jdeploy.substack.com/p/a-new-chapter-for-jdeploy-the-desktop)" with:
  * From CLI to a First-Class Desktop Experience
  * No Dependencies, No Confusion
  * Deploy Direct to GitHub
  * First-Class Project Templates for Swing and JavaFX
  * And more...
* A new library by **Alan Decunto** : "*[Language Manager](https://github.com/Snoopy137/language-manager) is a JavaFX library that enables dynamic language switching at runtime, allowing you to update the application language without needing to refresh the scene.* ":
  * 🌍 Support for multiple languages using standard .properties files.
  * 🔄 Change language on the fly without reloading scenes.
  * 🔗 Automatic binding for Label, Button, TextField, and other controls using @FXML ids.
  * ⚙️ Custom annotations to ignore specific fields from auto-binding.
  * 📦 Lightweight and easy to integrate.
* [TabShell is a lightweight platform for building tab-based applications](https://github.com/techsenger/tabshell) in JavaFX using the MVVM pattern.
  * [**Striking_Creme864** shared a video on Reddit](https://www.reddit.com/r/JavaFX/comments/1k2rzze/custom_file_chooser/): "*For our project we needed users to be able to use a file chooser for file storages that may exist in the application, but not in the OS, such as Google Drive, FTP, etc. As a solution, such a custom file chooser was created, which recognizes 4 types of storage (floppy, CD, network and base). The file path is represented as a URI. Both modes (list/details) use VirtualFlow. File sorting is done by the table from details, even for the list mode (the table is not on the Scene). Storage implementations will be wrappers for OpenDAL, Commons FVS, etc.*"

## Podcasts, Videos, Books

* Live stream step-by-step in Korean - with CC 😉 - by **알클 ALOHA CLASS** in which he creates an application with SceneBuilder and FXML: [JavaFX - Controller](https://www.youtube.com/watch?v=S01AemsDXew).
* **Frank Delporte** [interviewed **Gerrit Grunwald (aka hansolo)** for the JavaFX In Action series](https://webtechie.be/post/2025-04-10-jfxinaction-gerrit-grunwald-amazing-javafx-libraries/): "*Gerrit created many JavaFX libraries and blog posts. I wanted to talk with him about his work with JavaFX, but I also learned more about SVGs and how the garbage collectors in the JVM are working, thanks to the amazing visualizations he creates with ... JavaFX of course.*"
* Some feedback on this interview:
  * [By **Matt Coley**](https://bsky.app/profile/mattcoley.bsky.social/post/3lmh3rnswp22n): "*JavaFX has a perception problem. People will say 'Why not Compose?' before 'Why not FX?' even though Compose doesn't have a functional context-menu component for desktop yet. Compose just has a much better PR department even though for desktop its a much lesser offering. FX needs a PR boost. To elaborate on the Compose remark, you can have a WindowScope MenuBar, but it's only a wrapper that creates an un-styled Swing MenuBar. Additionally, generic compose context menus only allow text+onAction properties. For anything more the official docs tell you to use Swing's JPopupMenu. The point being, an unfinished product for desktop application is given much more public praise over JavaFX due to the perceived 'FX is dead' problem. That's the sorry state of things at the moment. I love FX and this makes my soul hurt.*"
  * [By **Catherine Edelveis**](https://bsky.app/profile/cat-edelveis.bsky.social/post/3lmjapuqrdk2d): "*Gerrit proves that Java is by no means a stranger in the world of UIs. I mean, all the cool libraries he's built make me even more enthusiastic about my aspirations with #JavaFX. And I can see that I've only touched a tip of the iceberg by now 😁.*"
  * YouTube comment by **Michel Antony Barros Barrios** : "*Amazing, I remember have used several components of Gerrit's libraries to show data analytics about billing behavior in a project I work some years ago. His libraries are wonderful.*"
* [Live stream with **Dashaun Carter** and **Catherine Edelveis**](https://www.youtube.com/watch?v=IxCfpxxS88w): "*Who said that desktop apps are dead? We prove them wrong! 🥊 We explore the world of rich and smart GUI apps that combine the powers of JavaFX and SpringBoot.*"

## Tutorials

* By **Codez Up** : [JavaFX: Build Modern Desktop Applications Easily](https://codezup.com/javafx-creating-modern-desktop-applications/).
* By **Sounetra Ghosal** : [How To Use JavaFX For GUI Development?](https://codingzap.com/javafx-for-gui-development/)
* **Frank Delporte** always forgets "*how to create a JavaFX ComboBox and configure it to show a specific field of an object in the opened and closed state of the ComboBox* " and [wrote a tutorial](https://webtechie.be/post/2025-04-09-javafx-combobox-with-objects/) "*hoping I remember that I blogged about it, the next time I need this functionality*".
* [Blog by **Catherine Edelveis** on Foojay](https://foojay.io/today/a-guide-to-creating-javafx-native-images/): "*A Guide to Creating JavaFX Native Images*"

## Miscellaneous

* Very short but [impressive video by **PIE SPACE**](https://x.com/PIE_SPACE_12/status/1907782342326079866): "*Check out my custom GUI made using Processing and JavaFX—specially designed to visualize real-time data from the Agni Flight Computer V2. You can find the full flight computer design along with the source code [on my GitHub page](https://github.com/PIEspace).*"
* [**Catherine Edelveis** asks your advise](https://bsky.app/profile/cat-edelveis.bsky.social/post/3lmc6dschus2f): "*Friends, does anyone know what is the best way of adding image urls to JavaFX CSS file?*"

## JFX Central

* New content:
  * Video: [JavaFX In Action with Gerrit Grunwald: Creator of Many Amazing JavaFX Libraries](https://www.jfx-central.com/videos/6pgHlHLrX8c)
* The overview of the JavaFX Links Of The Week published in March on JFX Central, got [published on Foojay.io](https://foojay.io/today/javafx-links-of-march-2025/).
