---
title: "Pi4J Welcomes Java 21 on the Raspberry Pi"
slug: "pi4j-welcomes-java-21-on-the-raspberry-pi"
date: "2025-02-20T10:49:07+00:00"
lastmod: "2025-02-20T10:49:09+00:00"
description: "So with the core team we made a few decisions that are allowing us to bring the project to \"the next level\" and... Java 21!."
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2021/12/pi4j-overview-scaled.jpg"
categories:
  - "Embedded"
  - "Pi4J"
  - "Raspberry Pi"
tags:
related_posts:
frozen: false
---

**Pi4J provides a friendly object-oriented I/O API and implementation libraries for Java Programmers to access the full I/O capabilities of the Raspberry Pi platform.**

The project "hides" the complexity to interact with electronic components through different ways of communication (SPI, I2C, PWM,...) by providing Java interfaces on top of native libraries that can interact with the GPIO (General Purpose Input/Output) pins on the Raspberry Pi.
![](/images/posts/2025/02/pi4j-welcomes-java-21-on-the-raspberry-pi/pi4j-overview-1024x427.jpg)

Recently, the development of the Pi4J library faced a decision point: "*Should we stick to Java 11 for existing projects and old Raspberry Pi's ([with ARMv6](https://www.pi4j.com/documentation/java-for-arm/)), versus moving on and making use of better and more performant Java code with the latest Long Term Support (LTS) version?*"

We actually wanted to move on to Java 22, as it brings a new way of interacting with C-code, thanks to [JEP 454: Foreign Function \& Memory API](https://openjdk.org/jeps/454). As this is the core behavior of Pi4J to interact with the GPIOs, it would make it much easier to implement new ways to control electronics, as confirmed [with a first test by Robert "Eitch" von Burg](https://github.com/eitch/pi4j-test/blob/develop/src/main/java/ch/eitchnet/pi4j/test/LibGPIODController.java). But as Java 22 is not an LTS release, we won't make this move yet.

We [asked our users](https://github.com/Pi4J/pi4j/discussions/409) which minimal Java version we should use, but there was no one clear answer, as expected 😉 So **with the core team we made a few decisions that are allowing us to bring the project to "the next level" and... Java 21!**.

* We renamed the repository with the sources of Pi4J V2+ from `pi4j-v2` to `pi4j` as it holds the sources of all V2 and future V3, V4,... versions.
* The minimal supported Java version for future releases (V3.X.X) is 21.
* When the next LTS becomes available, we can easily bump again (to V4?).
* When critical bugs are found, we can still backport them to V2 and make a new release. This is also used in the OpenJDK development, as described in [JEP 14: The Tip \& Tail Model of Library Development](https://openjdk.org/jeps/14).
* This documentation website was reviewed:
  * To have the correct links to the renamed repository.
  * Make it clear that V1 is deprecated and a separate repository.
  * V2, V3, and future versions are based on the same repository.
* With this bump to Java 21, we are also preparing to more easily bump to the next LTS, which will be Java 25 in September 2025.
* Bumping to the latest LTS makes it possible to make use of many newer Java language and runtime improvements, simplify some of the code, etc.

**3.0.0-SNAPSHOT of Pi4J is already available for testing from the Maven Repository if you enable snapshots in your pom-file.** See [Pi4J Downloads \> Snapshot Archives](https://www.pi4j.com/download/) for more info.

More info about V3 will be documented on the page [What's New in V3](https://www.pi4j.com/about/info-v3/).
