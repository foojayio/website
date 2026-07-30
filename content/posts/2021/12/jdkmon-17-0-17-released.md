---
title: "JDKMon 17.0.17 Released | Foojay.io Today"
slug: "jdkmon-17-0-17-released"
date: "2021-12-08T09:11:22+00:00"
lastmod: "2021-12-08T09:11:23+00:00"
description: "Point JDKMon to the folder where all your JavaFX SDK's are installed and it will check if there are any updates available."
authors:
  - "gerrit-grunwald"
image: "/images/posts/2021/12/jdkmon-17-0-17-released/Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "JavaFX"
  - "Release Notes"
tags:
related_posts:
  - "jdkmon-your-friendly-jdk-distribution-updater"
  - "jdkmon-17-0-23-released"
  - "jdkmon-17-0-18-released"
  - "introducing-sheetmusic4j-a-javafx-library-to-render-and-interact-with-sheet-music"
frozen: false
---

JDKMon is a little tool written in JavaFX that tries to detect all OpenJDK distributions installed on your machine and keep track of updates for those distributions.

It will scan for new updates every three hours and will inform you about available updates. JDKMon won't install the distributions on your machine but will only enable you to download them to a place of your choice.

At the moment the following distributions are supported:

* AdoptOpenJDK
* AdoptOpenJDK J9
* Bi Sheng
* Corretto
* Debian (pkgs not downloadable)
* Dragonwell
* Graalvm CE8
* Graalvm CE11
* Graalvm CE16
* Graalvm CE17
* JetBrains
* Kona
* Liberica
* Liberica Native
* Mandrel
* Microsoft
* OJDK Build
* Open Logic
* Oracle (not all pkgs downloadable)
* Oracle OpenJDK
* RedHat (pkgs not downloadable)
* SAP Machine
* Semeru
* Semeru Certified
* Temurin
* Trava
* Zulu
* Zulu Prime

JDKMon 17.0.17 has just been released!

The main new feature is support for JavaFX SDK's. So if you use a local installation of JavaFX, you can point JDKMon to the folder where all your JavaFX SDK's are installed and it will check if there are any updates available.

JDKMon is available for the following platforms:

* Windows x64
* Linux x64/arm64
* MacOS x64/aarch64

You can download the latest version from [github relases](https://github.com/HanSolo/JDKMon/releases "github relases").
