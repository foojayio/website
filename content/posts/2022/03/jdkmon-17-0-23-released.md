---
title: "JDKMon 17.0.23 Released | Foojay.io Today"
slug: "jdkmon-17-0-23-released"
date: "2022-03-11T09:52:39+00:00"
lastmod: "2022-03-11T10:01:02+00:00"
description: "JDKMon is a little tool written in JavaFX that tries to detect all OpenJDK distros installed while keeping track of updates for them."
authors:
  - "gerrit-grunwald"
image: "/images/posts/2022/03/jdkmon-17-0-23-released/jdkmon-17.0.23.png"
categories:
  - "DevOps"
  - "Release Notes"
  - "Tools"
tags:
related_posts:
  - "get-your-jdk-as-easily-as-possible"
  - "jdkmon-your-friendly-jdk-distribution-updater"
  - "jdkmon-17-0-18-released"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

JDKMon is a little tool written in JavaFX that tries to detect all OpenJDK distributions installed on your machine while keeping track of updates for those distributions.

It will scan for new updates every three hours and will inform you about available updates.

<figure class="wp-block-image size-full is-resized is-style-default">
 <img fetchpriority="high" decoding="async" src="/images/posts/2022/03/jdkmon-17-0-23-released/jdkmon-17.0.23.png" alt="" class="wp-image-52579" width="423" height="306">
</figure>

**Note:** JDKMon won't install the distributions on your machine but will enable you to download them to a place of your choice.

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

### New Release {#h3-0-new-release}

JDKMon 17.0.23 has just been released and brings a new feature consisting of CVE's that are found will be coloured according to their score, e.g., low is green and high is red.

<figure class="wp-block-image size-full is-resized is-style-default">
 <img decoding="async" src="/images/posts/2022/03/jdkmon-17-0-23-released/slack-imgs.com_.png" alt="" class="wp-image-52580" width="279" height="135">
</figure>

JDKMon is available for the following platforms:

* Windows x64
* Linux x64/arm64
* MacOS x64/aarch64

### Download {#h3-1-download}

You can download the latest version from [github relases](https://github.com/HanSolo/JDKMon/releases "github relases") or from[JFX Central](https://www.jfx-central.com/downloads " JFX Central").

### More Info {#h3-2-more-info}

[JDKMon Home](https://harmoniccode.blogspot.com/p/jdkmon.html "JDKMon Home")
