---
title: "First Experiments with Java on the LattePanda IOTA: An Alternative to Raspberry Pi?"
slug: "first-experiments-with-java-on-the-lattepanda-iota"
date: "2025-12-11T09:13:14+00:00"
lastmod: "2025-12-11T16:25:58+00:00"
description: "After years of experimenting with Raspberry Pi boards, Java, JavaFX, and Pi4J to control electronics, I wanted to explore whether my knowledge and - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-11-25-first-test-lattepanda-iota-with-ubuntu-and-java/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2025/11/lattepanda-iota-first-impressions.jpg"
categories:
  - "Embedded"
  - "Java"
  - "Java Beginner"
  - "Java Core"
  - "JavaFX"
  - "Videos"
tags:
related_posts:
  - "javafx-links-of-november-2025"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
  - "foojay-podcast-83"
  - "introducing-a-new-java-dmx512-library-with-demo-javafx-user-interface"
enlighterjs: true
frozen: false
---

After years of experimenting with Raspberry Pi boards, Java, JavaFX, and [Pi4J](https://www.pi4j.com/) to control electronics, I wanted to explore whether my knowledge and experience could be applied to similar boards from other providers. There are many alternatives available these days, based on ARM, Intel processors, and RISC-V architectures.

I reached out to several suppliers to see if I could get evaluation copies, and I'm happy to share that I received my first box from DFRobot containing the [**LattePanda IOTA**](https://www.dfrobot.com/product-2989.html).

{{< youtube jCOv1gXSzCA >}}

Unboxing the LattePanda IOTA {#h2-0-unboxing-the-lattepanda-iota}
-----------------------------------------------------------------

The box contained multiple smaller boxes, but the most important one was the LattePanda IOTA board itself, based on an Intel Twin Lake N150 quad-core processor (up to 3.6GHz). It has a clear warning on the packaging: **"Do not operate without a heatsink"**. This thing will definitely get hot if you ignore that warning I guess 😉

The board is a bit bigger than a Raspberry Pi and appears very well-made. It has:

* A GPIO header (similar to Raspberry Pi, though the pin numbering is different)
* Network connection
* Connections for storage options and other expansions
* Three USB ports
* A full-size HDMI connector (more convenient than the mini or micro HDMI on Raspberry Pi)

In the same box, I also received:

1. **M2 expansion board**: for extra storage
2. **Active cooler**: essential to prevent overheating
3. **UPS hat**: for battery backup functionality
4. **Power over Ethernet shield**: handy, will test later
5. **4G LTE module** with SIM card support

![](/images/posts/2025/12/first-experiments-with-java-on-the-lattepanda-iota/unboxed-1024x573.png)

The cooling fan has a nice logo and excellent build quality. The PoE shield connects directly to a new network connector on the board, unlike Raspberry Pi expansion boards that use the Pi's existing network connection.

Assembly {#h2-1-assembly}
-------------------------

Following the documentation, I applied thermal paste to the processor, attached the cooling fan, and connected the M2 expansion board.

Setting Up The Board {#h2-2-setting-up-the-board}
-------------------------------------------------

### First Boot: Windows Pre-installed {#h3-3-first-boot-windows-pre-installed}

After finding the power button, the LattePanda logo appeared on screen, and... Windows started booting. Windows was pre-installed, though I'm not sure if this is default or just for evaluation units. Either way, I immediately noticed 100% CPU usage, the exact reason I left Windows long ago, as I never understood that it's an ongoing problem with Windows... Memory usage was also pretty high.
![](/images/posts/2025/12/first-experiments-with-java-on-the-lattepanda-iota/windows-cpu.png)

This thing definitely works with Windows, but I don't use Windows myself. Time to turn this into a Linux device.

### Installing Ubuntu {#h3-4-installing-ubuntu}

I put the latest Ubuntu system on a USB stick to boot from it, restarted the device, and kept pressing the delete button to enter the BIOS. The system recognized the USB drive immediately. After selecting it and choosing "Save and exit", it booted into Ubuntu installation mode. A few configuration steps later, I had a nice combination: LattePanda running Ubuntu.

### Setting Up Java Development {#h3-5-setting-up-java-development}

As expected, Java isn't pre-installed in Ubuntu, but several installation options were suggested. However, there's an easier way to prepare a Linux embedded board like this or a Raspberry Pi for Java development: the **[Pi4J OS repository](https://github.com/pi4J/pi4j-os)**.

This repository contains scripts to set up boards for Java development, making it easy to have everything prepared and ready to start. There are two scripts available:

1. One for Raspberry Pi
2. One for non-Raspberry Pi boards

Using the second option, `curl` downloads and executes the script for non-Raspberry Pi boards with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -sL https://raw.githubusercontent.com/Pi4J/pi4j-os/main/script/prepare-for-java-non-rpi.sh | bash</pre>

This performs:

* System update
* Installation of extra dependencies for Java and I2C
* SDKMAN installation
* Java installation
* Maven installation
* JBang installation

I also installed **Visual Studio Code**, the preferred Java editor for this kind of board because it's lightweight and has excellent extensions for Java and JavaFX applications. These are the recommended extensions for Java development:

* Extension Pack for Java: Installs many tools for Java development
* JBang: To execute JBang code directly from VS Code

Testing Java, JavaFX, and Pi4J {#h2-6-testing-java-javafx-and-pi4j}
-------------------------------------------------------------------

I cloned the [Pi4J JBang examples project](https://github.com/Pi4J/pi4j-jbang) and opened it in Visual Studio Code, to execute code in an easy way.

### HelloWorld with JBang {#h3-7-helloworld-with-jbang}

The simple "Hello World" example ran perfectly. There's also an extended example using the Jackson library for JSON parsing, demonstrating how JBang can create single-file applications with dependencies, without needing a full Maven or Gradle project.

### JavaFX Test {#h3-8-javafx-test}

Since I installed the Java version from Azul with JavaFX included, I could also run a JavaFX demo application. It uses Pi4J to detect the board type, though this only contains methods to detect Raspberry Pi board versions at this moment, so it didn't recognize the LattePanda.

But the application **ran smoothly**! It showed we're running on a Linux 64-bit system with Java 25. The board wasn't recognized yet as expected, maybe we can in the future add detection tools in the Pi4J library to show the brand or manufacturer information.
![](/images/posts/2025/12/first-experiments-with-java-on-the-lattepanda-iota/javafx-demo-1024x576.png)

Without any extra work, we have a JavaFX application running very smoothly on this board!

### Pi4J Test {#h3-9-pi4j-test}

Now for the fun part: let's see what happens when we run something Pi4J-specific. I tried a project that uses an RGB-LED and changes colors. It compiled, but gave errors about user groups not being configured correctly. This was expected, I've never tried Pi4J on a non-Raspberry Pi single-board-computer before, so I wasn't expecting it to work on the first attempt.

This is something I'll dive into further and post follow-up videos about what can be achieved with the Pi4J library on boards like this.

### Performance Check {#h3-10-performance-check}

With `htop`, I checked the CPU usage. Compared to Windows using 100% CPU, we have here in an idle state almost nothing. There's a lot of room for applications we can run on this board. Great!!!

Conclusion {#h2-11-conclusion}
------------------------------

This was the first quick test, and it only took me about an hour to unbox everything, assemble it, and record this. Very promising results:

* Java runs perfectly
* JavaFX runs very smoothly
* Pi4J not working yet, but that was expected 🙂

The next step will be to determine which configuration changes are needed, either at the system level or within Pi4J itself. I'm very happy with this first result. The LattePanda IOTA is a very good-looking board, well-made, and comes with a good fan. You don't hear it running during normal usage. It only ramps up when you start demanding applications.

Promising results! I'm looking forward to experimenting more with this and similar boards to see what's possible with Java(FX) and Pi4J on alternative hardware platforms.

Stay tuned for follow-up videos and blog posts!
