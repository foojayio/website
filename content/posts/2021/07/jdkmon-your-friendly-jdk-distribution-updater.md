---
title: "JDKMon: Your Friendly JDK Distribution Updater"
slug: "jdkmon-your-friendly-jdk-distribution-updater"
date: "2021-07-06T06:59:15+00:00"
lastmod: "2021-07-06T22:22:41+00:00"
description: "JDKMon scans your computer for installed OpenJDK distributions and uses the Disco API to check whether there are updates available!"
authors:
  - "gerrit-grunwald"
image: "https://github.com/HanSolo/JDKMon/raw/main/screenshot.png"
categories:
  - "JavaFX"
  - "Tools"
tags:
related_posts:
frozen: false
---

I don't know if you have the same problem but I have around 8 OpenJDK distributions installed on my machine and I always have the problem of keeping them up to date. I know there is [sdkman](https://sdkman.io/ "sdkman") which is awesome but I somehow never got used to it.

Meaning to say I download the OpenJDK distributions manually and install them on my machine. So the main problem is that there so many different distributions out there and all of them have a different way on how to get the latest version. To solve exactly this problem we at foojay.io created the DiscoAPI which keeps track on (hopefully) all available OpenJDK distributions available. Well and because we like to eat our own dogfood I've created a little tool that helps me keeping my installed OpenJDK distributions up to date... [JDKMon](https://github.com/HanSolo/JDKMon "JDKMon").

It's just a little tool written in Java(FX) which makes use of [FXTrayIcon](https://github.com/dustinkredmond/FXTrayIcon "FXTrayIcon"), a nice little library by Dustin Redmond that makes it possible to run a JavaFX application in the system tray of your operating system. Well, that means it works on Windows and MacOS but unfortunately not on all Linux distributions. But no worries, JDKMon will also run on Linux and it will stay in the dock instead.

In principle, JDKMon is just a little tool that scans your computer for installed OpenJDK distributions and uses the [Disco API](https://github.com/foojay2020/discoapi) to check whether there are updates available for one of the distributions. In case it finds updates it will present you buttons for each package it finds. When you click on one of these buttons (e.g. tar.gz, zip, pkg etc.) you have to select a folder where the selected package should be downloaded to. The download process will be visualized by a little progressbar at the bottom of the window. After the download is done, you have to install the downloaded package manually.

Because JDKMon won't scan your whole machine trying to find installed JDK's you have to point it to a folder that it should check. On MacOS for example usually the JDK's will be installed in the following folder `/Volumes/Macintosh HD/Library/Java/JavaVirtualMachines`, where on Windows it might be `C:\Program Files\Java`and on Linux it's probably `/usr/lib/jvm`. On the JDKMon menu you will find an entry called `SearchPath`, when selecting this entry you can select a folder that JDKMon will then check for installed JDK distributions. You just have to select that folder ones because it will be stored in a properties file.

If you would like to trigger a rescan for updates you can select the entry `Rescan`but JDKMon will also run a rescan every 3 hours. In case it will find updates for one of the installed distributions it will show a popup window on the screen that disappears after a couple of seconds showing the available updates.

The JDKMon main window comes in different flavors, there is a native looking version for MacOS and Windows. On Linux the window will look like on MacOS.

MacOS and Linux:  
![JDKMon MacOS and Linux](https://github.com/HanSolo/JDKMon/raw/main/screenshot.png "JDKMon MacOS and Linux")

Windows:  
![JDKMon Windows](https://github.com/HanSolo/JDKMon/raw/main/screenshot_win.png "JDKMon Windows")

I've tried to make the windows look as native as possible in the given time. 🙂

As you can see on the screenshots the JDKMon window will show you the installed distributions with their version numbers. If a distributions comes bundled with JavaFX, you will find `(FX)`behind the distribution name. In case there is an update available you will see an arrow, followed by the latest available version for this distribution. After the version you will find a list of buttons that have different colors. Each of these buttons has a tooltip that shows which package it points to. When you click on one of those buttons you have to select a folder where to download it to and then the download will start.

There are distributions that do not support direct downloads of their packages (e.g. Oracle and RedHat). In this case the buttons will be gray and cannot be clicked. Meaning to say you have to check the website of the distribution and download it manually. But at least you get the information that a new package is available.

Currently the following distributions are supported by the DiscoAPI and so by JDKMon:

* AdoptOpenJDK
* AdoptOpenJDK J9
* Corretto
* Dragonwell
* GraalVM CE8
* GraalVM CE11
* GraalVM CE16
* JetBrains
* Liberica
* Liberica Native
* Mandrel
* Microsoft
* OJDK Build
* OpenLogic
* Oracle
* Oracle OpenJDK,
* RedHat
* SAP Machine,
* Temurin (no packages yet)
* Trava
* Zulu
* Zulu Prime

To download JDKMon you might want to check the [github releases](https://github.com/HanSolo/JDKMon/releases "github releases") page of the project. There are installers for Windows and Mac and also jar files for all platforms. To run the jar file you need to have JDK16 or later installed, please start it as follows:

`java -jar --enable-preview JDKMon-16.0.jar`

If you need help or encounter problems, please file [issues](https://github.com/HanSolo/JDKMon/issues "issues") over at GitHub.

That's it... so keep coding!
