---
title: "Installing Java and JavaFX on the Raspberry Pi"
slug: "installing-java-and-javafx-on-the-raspberry-pi"
date: "2020-09-01T08:32:24+00:00"
lastmod: "2020-09-04T08:02:06+00:00"
description: "One of the most read articles on my blog is on the installation of a recent Java on Raspberry Pi, in March 13, 2019, so it was time for an update! More..."
canonical: "https://webtechie.be/post/2020-04-08-installing-java-and-javafx-on-raspberry-pi/"
authors:
  - "frankdelporte"
image: "https://webtechie.be/images/2020-04-08/java-versions-on-pi.png"
categories:
  - "JavaFX"
  - "Raspberry Pi"
tags:
related_posts:
  - "a-fresh-look-at-embedded-java"
  - "first-experiments-with-java-on-the-lattepanda-iota"
  - "javafx-links-of-november-2025"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
enlighterjs: true
frozen: false
---

One of the most read articles on my blog is about [the installation of a recent Java on Raspberry Pi (March 13, 2019)](https://webtechie.be/post/2019-03-13-pijava-part-2-java-11-on-raspberry-pi-3/), so it was time for an update, which is here republished on foojay.

Disclaimer: **this article is only valid for Raspberry Pi's with an ARMv7 or ARMv8 processor** . In the [Raspberry Pi specifications table on Wikipedia](https://en.wikipedia.org/wiki/Raspberry_Pi#Specifications), you get a clear overview of the Pi-types with this processor:

* Model A+, version 3
* Model B, version 2, 3 and 4
* Compute Module, version 3

In the [release notes of Raspbian](http://downloads.raspberrypi.org/raspbian/release_notes.txt) you can see that the version of 2019-06-20 includes OpenJDK 11:

```
2019-06-20:
* Based on Debian Buster
* Oracle Java 7 and 8 replaced with OpenJDK 11
```


So, if we start with a fresh new Raspbian OS, we indeed get this Java version result:

```
$ java -version
openjdk version "11.0.3" 2019-04-16
OpenJDK Runtime Environment (build 11.0.3+7-post-Raspbian-5)
OpenJDK Server VM (build 11.0.3+7-post-Raspbian-5, mixed mode)
```


This means we are already good to start any Java 11 based program!

As JavaFX is no longer part of the Java JDK, since version 11, running a JavaFX program on the Raspberry Pi will not work out of the box.

Luckily, we can use the Liberica JDK, which is provided by BellSoft. They have a version dedicated for the Raspberry Pi, which includes JavaFX, so you will be able to run a JavaFX application with a simple "java -jar yourapp.jar" start command.

Installing Liberica JDK
-----------------------

We only need the download link from their site to install an alternative Java JDK like this:

```
$ cd /home/pi
$ wget https://download.bell-sw.com/java/13/bellsoft-jdk13-linux-arm32-vfp-hflt.deb
$ sudo apt-get install ./bellsoft-jdk13-linux-arm32-vfp-hflt.deb
$ sudo update-alternatives --config javac
$ sudo update-alternatives --config java
```


When this is done, we can check the version again and it should look like this:

```
$ java --version
openjdk version "13-BellSoft" 2019-09-17
OpenJDK Runtime Environment (build 13-BellSoft+33)
OpenJDK Server VM (build 13-BellSoft+33, mixed mode)
```


On my test-Pi I even keeping different versions of LibericaJDK and switching is very easy with "update-alternatives".
![](https://webtechie.be/images/2020-04-08/java-versions-on-pi.png)

Install Scripts on GitHub
-------------------------

On [Github, in the sources](https://github.com/FDelporte/JavaOnRaspberryPi/tree/master/Chapter_04_Java/scripts) of my book ["Getting started with Java on Raspberry Pi"](https://webtechie.be/books/), you can find install scripts for multiple versions of Liberica JDK, which already contain the correct download link for each one:
![](https://webtechie.be/images/2020-04-08/github-scripts.png)
