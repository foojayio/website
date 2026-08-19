---
title: "Video series “JavaFX In Action”, Part 4 with Mike Hearn (Conveyor), Sven Reimers (JTaccuino), and Chris Newland (DemoFX, JitWatch)"
date: "2025-03-26T06:51:35+00:00"
lastmod: "2025-03-26T07:01:10+00:00"
description: "This is the next part in the series of \"JavaFX in Action\" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's talk! July…"
authors:
  - "frankdelporte"
image: "jfxinaction-part-4.jpg"
categories:
  - "Interviews"
  - "JavaFX"
related_posts:
  - "new-video-series-javafx-in-action-part-1"
  - "video-series-javafx-in-action-part-2"
  - "video-series-javafx-in-action-part-3"
  - "video-series-javafx-in-action-part-6"
frozen: false
---

This is the next part in the series of "JavaFX in Action" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's talk!

* [July '24: Pedro Duque Vieira, Daniel Zimmermann, Christopher Schnick, and Robert Ladstätter](https://foojay.io/today/new-video-series-javafx-in-action-part-1/)
* [November '24: Maciej Gorywoda, Ramiro Domínguez Ayub, Christoph Schwentker, Ulas Ergin](https://foojay.io/today/video-series-javafx-in-action-part-2/)
* [December '24: Özkan Pakdil, Clément de Tastes, Almas Baim, Steve Hannah, Jago de Vreede](https://foojay.io/today/video-series-javafx-in-action-part-3/)

## Mike Hearn: Conveyor, build self-updating desktop app packages

[Mike Hearn](https://www.linkedin.com/in/mike-hearn-2523962/) solves a problem many developers are struggling with: efficiently distributing your application and ensuring the users get the latest version. With Conveyor, he created a tool that can do that easily with JavaFX, Electron, and Flutter apps!

[Conveyor](https://hydraulic.dev/) makes distributing desktop apps as easy as shipping a web app. It's a tool, not a service, that generates and signs self-upgrading packages for Windows, macOS, and Linux using each platform's native package formats without requiring you to have those operating systems.

It's free for open-source apps and has simple per-project pricing for commercial apps.

{{< youtube CuI7-PllJZQ >}}

More info in [this blog post](https://webtechie.be/post/2025-01-23-jfxinaction-mike-hearn-conveyor/).

## Sven Reimers: JTaccuino, notebook application for Java developers

[Sven Reimers](https://www.linkedin.com/in/svenreimers/) created a JavaFX-based notebook application to make it easier to learn Java and experiment with notebooks that can visualize the variables differently, such as tables and graphs.

[JTaccuino](https://jtaccuino.github.io/) is a JavaFX-based notebook application for Java developers. It is built for usages in education, interactive experimentation with algorithms, and more advanced use cases. JShell, the awesome Java REPL, provides Java code execution.

{{< youtube gkHgsamCoGc >}}

More info in [this blog post](https://webtechie.be/post/2025-02-06-jfxinaction-sven-reimers-jtaccuino/).

## Chris Newland: DemoFX and JitWatch

[Chris Newland](https://www.linkedin.com/in/chriswhocodes/) has a long history of Java and JavaFX development. I invited him to talk about two of his JavaFX projects: DemoFX and JITWatch. While the demos are impressive already, Chris also gives a "crash course" in this video about Java and Byte code and how the Just-In-Time compiler converts these to native code in the Java Virtual Machine.

[DemoFX](https://github.com/chriswhocodes/DemoFX) is a performance test platform for JavaFX. It can layer and schedule effects on a timeline. It is used to discover the best techniques to optimize JavaFX performance on the Raspberry Pi and Desktop.

[JITWatch](https://github.com/AdoptOpenJDK/jitwatch) is a log analyser and visualiser for the HotSpot JIT compiler. It helps you to inspect inlining decisions, hot methods, bytecode, and assembly. You can view the results in a JavaFX user interface.

{{< youtube 8s14hFLp7cI >}}

More info in [this blog post](https://webtechie.be/post/2025-02-20-jfxinaction-chris-newland-demofx-jitwatch/).
