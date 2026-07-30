---
title: "Installing Java with SDKMAN on Raspberry Pi | Foojay.io Today"
slug: "installing-java-with-sdkman-on-raspberry-pi"
date: "2022-02-07T12:23:15+00:00"
lastmod: "2022-02-07T12:23:17+00:00"
description: "Java on Raspberry Pi has always been possible, but SDKMAN makes the getting-started process a lot easier. It's as easy as pi..."
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Embedded"
  - "Raspberry Pi"
tags:
related_posts:
  - "video-sdkman-explained"
  - "installing-java-and-javafx-on-the-raspberry-pi"
  - "java-17-on-the-raspberry-pi"
  - "a-fresh-look-at-embedded-java"
enlighterjs: true
frozen: false
---

If you create a new SD card for a Raspberry Pi with the operating system, you can choose the "Raspberry Pi OS Full (32-bit)" edition, which includes Java 11. But a lot of the other available OS-versions don't have Java included.

There are many ways to install Java but, personally, I find [SDKMAN](https://sdkman.io/ "SDKMAN") the easiest one to use.

It allows you to easily install a JDK, but also to switch between different versions.
> **SDKMAN!** is a tool for managing parallel versions of multiple **Software Development Kits** on most Unix based systems. It provides a convenient Command Line Interface (CLI) and API for installing, switching, removing and listing Candidates.

A nice video with a [full demo of SDKMAN is available here on foojay.io](https://foojay.io/today/video-sdkman-explained/).

SDKMAN can show you a list of available JDK-distributions based on the operating system and architecture. Until recently that didn't work on Raspberry Pi because that runs on ARM. But as this is an open-source project it is possible to request new features and help to achieve the solution. As you can see in [GitHub issue #836](https://github.com/sdkman/sdkman-cli/issues/836), this required some rework in different parts of the project. But we have a working version now!!!

Try it out {#h2-0-try-it-out}
-----------------------------

If you want to dive in directly, grab yourself a Raspberry Pi, open a terminal and install ZIP and SDKMAN.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo apt install zip
$ curl -s "https://beta.sdkman.io" | bash

# Open a new terminal or run this command
$ source "$HOME/.sdkman/bin/sdkman-init.sh"

$ sdk list java
================================================================================
Available Java Versions for Linux ARM 32bit Hard Float
================================================================================
 Vendor        | Use | Version      | Dist    | Status     | Identifier
--------------------------------------------------------------------------------
 Liberica      |     | 17.0.1       | librca  |            | 17.0.1-librca
               |     | 11.0.13      | librca  |            | 11.0.13-librca
 Zulu          |     | 11.0.13      | zulu    |            | 11.0.13-zulu
               |     | 8.0.312      | zulu    |            | 8.0.312-zulu
================================================================================
</pre>

Yep, that's right, even for the Raspberry Pi different distributions can be used! So let's try out Azul Zulu 11.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sdk install java 11.0.13-zulu
$ java -version
openjdk version "11.0.13" 2021-10-19 LTS
OpenJDK Runtime Environment Zulu11.52+13-CA (build 11.0.13+8-LTS)
OpenJDK Client VM Zulu11.52+13-CA (build 11.0.13+8-LTS, mixed mode)</pre>

Confirmed! Azul Zulu 11 installed with SDKMAN runs fine on a Raspberry Pi.

Side note: although both Liberica and Zulu are listed for "Linux ARM 32bit Hard Float", **only Zulu works on ARMv6, e.g. the Raspberry Pi Zero 1**. At this point BellSoft has no plans to support this processor.

About the processor architecture {#h2-1-about-the-processor-architecture}
-------------------------------------------------------------------------

As there are multiple Raspberry Pi versions since 2013 ([full history on Wikipedia](https://en.wikipedia.org/wiki/Raspberry_Pi#Specifications)), different ARM processor types were used. The ARMv6 only supports 32-bit, while ARMv7 is a 64-bit processor.

The architecture can be checked with the `uname` command and these are some of the outputs:

|---------------------|------------------|------------|
| Board               | Operating System | `uname -m` |
| Raspberry Pi Zero 1 | 32-bit           | armv6l     |
| Raspberry Pi 4      | 32-bit           | armv7l     |
| Raspberry Pi 4      | 64-bit           | aarch64    |

If you want to know more about your system, use the `lscpu` command, this is the output on a Raspberry Pi 4 with 32-bit OS:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ lscpu
Architecture:        armv7l
Byte Order:          Little Endian
CPU(s):              4
On-line CPU(s) list: 0-3
Thread(s) per core:  1
Core(s) per socket:  4
Socket(s):           1
Vendor ID:           ARM
Model:               3
Model name:          Cortex-A72
Stepping:            r0p3
CPU max MHz:         1500.0000
CPU min MHz:         600.0000
BogoMIPS:            108.00
Flags:               half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt vfpd32 lpae evtstrm crc32</pre>

64-bit Operating System {#h2-2-64-bit-operating-system}
-------------------------------------------------------

After a long time of trialing a beta version, on February 2th, Raspberry Pi officially announced a 64-bit version of their operating system. You can [read more about this release on the Raspberry Pi Blog](https://www.raspberrypi.com/news/raspberry-pi-os-64-bit/). And because this is a more used architecture, the output of SDKMAN is really impressive...

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">================================================================================
Available Java Versions for Linux ARM 64bit
================================================================================
 Vendor        | Use | Version      | Dist    | Status     | Identifier
--------------------------------------------------------------------------------
 AdoptOpenJDK  |     | 8.0.275+1.hs | adpt    |            | 8.0.275+1.hs-adpt   
               |     | 8.0.252.hs   | adpt    |            | 8.0.252.hs-adpt     
 Corretto      |     | 17.0.2.8.1   | amzn    |            | 17.0.2.8.1-amzn     
               |     | 17.0.0.35.1  | amzn    |            | 17.0.0.35.1-amzn  

... 45 EXTRA LINES HERE

 Temurin       |     | 17.0.2       | tem     |            | 17.0.2-tem          
               |     | 11.0.14      | tem     |            | 11.0.14-tem         
               |     | 8.0.322      | tem     |            | 8.0.322-tem         
 Zulu          |     | 17.0.2       | zulu    |            | 17.0.2-zulu         
               |     | 11.0.14      | zulu    |            | 11.0.14-zulu        
               |     | 8.0.322      | zulu    |            | 8.0.322-zulu        
================================================================================
</pre>

So using the Temurin 17 version is as easy as `sdk install java 17.0.2-tem`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sdk install java 17.0.2-tem

Downloading: java 17.0.2-tem

In progress...

################################################################################################################################################################################ 100.0%################################################################################################################################################################################ 100.0%

Repackaging Java 17.0.2-tem...

Done repackaging...

Installing: java 17.0.2-tem
Done installing!

Setting java 17.0.2-tem as default.

$ java -version
openjdk version "17.0.2" 2022-01-18
OpenJDK Runtime Environment Temurin-17.0.2+8 (build 17.0.2+8)
OpenJDK 64-Bit Server VM Temurin-17.0.2+8 (build 17.0.2+8, mixed mode, sharing)
</pre>

Conclusion {#h2-3-conclusion}
-----------------------------

Java on Raspberry Pi has always been possible, but SDKMAN makes the getting-started process a lot easier.

Installing a first JDK or changing the version has now become a piece of cake pi(e)!
