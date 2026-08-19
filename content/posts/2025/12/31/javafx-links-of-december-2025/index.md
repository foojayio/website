---
title: "JavaFX Links of December 2025"
date: "2025-12-31T06:05:54+00:00"
description: "Here is the final JavaFX LinksOfTheMonth for 2025... Thank you all for sharing your knowledge and experience with JavaFX. And we hope to see even more in…"
canonical: "https://webtechie.be/post/2025-12-19-javafx-links-of-december-2025/"
authors:
  - "frankdelporte"
image: "jfxcentral.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-november-2025"
  - "javafx-links-of-october-2025"
  - "javafx-links-of-september-2025"
  - "javafx-links-of-august-2025"
frozen: false
---

Here is the final JavaFX LinksOfTheMonth for 2025... Thank you all for sharing your knowledge and experience with JavaFX. And we hope to see even more in the next year!

You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* [A one second, but still impressive, video on YouTube by Carl Dea](https://www.youtube.com/watch?v=ra-VYA7gvBg): "*I was able to get 100,000-2M particles \~ 120fps using Apple's Metal API on M4/GPU, using Java 25 FFM (project Panama), JavaFX WritableImage API. Of course there are more optimizations but really excited to be able to mix Java/JavaFX code with native bindings in a memory safe way. Still more to learn and do. There are different strategies to avoid data copies between CPU and GPU.*"

## Applications

* **Juan Antonio Breña Moral** created an "animated spinning ripple surface rendered with JavaFX" using `MemorySegment` from the FFM API (Java 22+), [JEP 454](https://openjdk.org/jeps/454), to increase performance. [Sources and screenshot on GitHub](https://github.com/jabrena/sombrero).
* [**Patrik Karlström** released CRIC 25.12](https://fosstodon.org/@trixon/115741355988091625): "*CRIC is a Java custom runtime image creator, a GUI for jlink. It's built with JDK 25, JavaFX and the Netbeans platform. [Downloads available for Windows and Linux](https://github.com/trixon/cric/releases/tag/v25.12), including appimage and Snap.*"

## Components, Libraries, Tools

* [Message by **Dirk Lemmermann** on Bluesky](https://bsky.app/profile/dlemmermann.bsky.social/post/3m7asibknqc2u): "*Check out FXMLKit. Another convention-over-configuration framework that removes boilerplate code for loading screens done in FXML. It is inspired by afterburner.fx but also supports JPro multi-user sessions and dependency injection for nested components.* " [Sources and more description on GitHub](https://github.com/dlsc-software-consulting-gmbh/FxmlKit) and available from Maven Central.
  * Version 1.2.0: "*It now also supports hot reloading of FXML and CSS. Yes, that's right ... see the changes you made to FXML without restarting your application.*"
  * [**Dirk Lemmermann** announced v1.3.0 of FxmlKit](https://bsky.app/profile/dlemmermann.bsky.social/post/3m7rvb7x5lk2z): "*Fixing an issue related to hot reloading of user agent stylesheets of custom controls. It had too many side-effects. It can still be enabled, but by default it is off.*"

## Podcasts, Videos, Books

* [A new JFXInAction interview by **Frank Delporte**](https://webtechie.be/post/2025-12-04-jfxinaction-florian-enner-robot-3d-charts/): "***Florian Enner** impressed me at the Devoxx conference with his 3D visualizations and scientific charts with millions of points, showing real-time data received from robots and sensors. And using the same code, he does this in both desktop and mobile apps! Awe-inspiring work, making maximum use of the powers of hashtag#Java to combine network communication with a JavaFX user interface. And he also shows some amazing things with a JavaFX UI in a C++ application!*"
* **Lazy Brownie** recorded the coding process of a [Two Player Pong](https://www.youtube.com/watch?v=veZXcM_Glfc&t=1886s). No shared code, no comments, just some relaxed music and a screen recording 🙂

## Tutorials

* New tutorial videos this week by **Troels Mortensen** :
  * [The Controller Factory](https://www.youtube.com/watch?v=_vEapBhebPs)
  * [Changing views through events](https://www.youtube.com/watch?v=gxKpus7t8w0)

## Miscellaneous

* A [Bluesky thread by **Sean Tilley**](https://bsky.app/profile/deadsuperhero.com/post/3m74qyncm7k2v): "*For my capstone project in Computer Science, I'm writing a desktop music player in Java. I've never tried to build one before, but it seems like a decent challenge, and it covers the different data structures we've learned about. ... Playback will also be interesting to figure out. Java's audio API doesn't support MP3 out of the box, but JavaFX does.*"
* Similarly, [**Kz** needs to create a game](https://bsky.app/profile/kossayzemzem.bsky.social/post/3m6pcqfulrs2d): "*I am required to make a game in JavaFX for a University project and I have zero experience in video game dev.*"
* Repost of the [article by **Frank Delporte** on Foojay](https://foojay.io/today/first-experiments-with-java-on-the-lattepanda-iota/): First Experiments with Java on the LattePanda IOTA: An Alternative to Raspberry Pi? "*Since I installed the Java version from Azul with JavaFX included, I could also run a JavaFX demo application. ... The application ran smoothly! It showed we're running on a Linux 64-bit system with Java 25.*"
* The JavaFX plugin in IntelliJ IDEA is marked as 'Ultimate Only' in the unified distribution IDEA 2025.3. But no panic, [it's a bug](https://youtrack.jetbrains.com/issue/IDEA-383652/JavaFX-plugin-is-marked-as-Ultimate-Only-in-IDEA-2025.3-Unified-Distribution): "*Until the fix is out, you can install the previous version 2025.2.6 of IntelliJ IDEA to bypass the issue.*"
* On Reddit, [**Rvaranda** shared a video and is asking for support](https://www.reddit.com/r/JavaFX/comments/1pk0la8/fxgl_fps_problem/): "*I'm having a weird problem with FXGL. Don't know if it's FPS related, but what happens is, when I start the game, the app's timer spikes up very briefly at the start, then it stabilizes. As a result, all moving entities moves very quickly initialy, then they slow down to their actual speed.*"
* [**Clément de Tastes** shared a screenshot](https://bsky.app/profile/cdetastes.bsky.social/post/3ma7olcch2k2q): "*Exploring Mandelbrot's fascinating fractals has been a wonderful way for me to play with Java features. It started as a JavaFX project only, then became the perfect playground to experiment with upcoming features such as Valhalla Value Types and the Vector API. Special mention to FFM as well.*"

## JFX Central

* New content on JFX Central:
  * People: [Florian Enner](https://www.jfx-central.com/people/f.enner)
  * Video: [JavaFX In Action with Florian Enner about Robot 3D Visualizations and Charts](https://www.jfx-central.com/videos/_DGz4YyojpE)
* The links of November got [published on Foojay](https://foojay.io/today/javafx-links-of-november-2025/).
