---
title: "Video series “JavaFX In Action”, Part 6 with Vlad Protsenko (Clojure), Matt Coley (Recaf), Craig Raw (Sparrow), and Florian Enner (3D Robot Visualization)"
slug: "video-series-javafx-in-action-part-6"
date: "2025-12-26T08:07:18+00:00"
description: "This is the next part in the series of \"JavaFX in Action\" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's discuss it - by Frank Delporte"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2025/12/jfxinaction-part-6-scaled.jpg"
categories:
  - "Interviews"
  - "JavaFX"
tags:
related_posts:
frozen: false
---

This is the next part in the series of "JavaFX in Action" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's discuss it in the new year!

1. [July '24: Pedro Duque Vieira, Daniel Zimmermann, Christopher Schnick, and Robert Ladstätter](https://foojay.io/today/new-video-series-javafx-in-action-part-1/)
2. [November '24: Maciej Gorywoda, Ramiro Domínguez Ayub, Christoph Schwentker, Ulas Ergin](https://foojay.io/today/video-series-javafx-in-action-part-2/)
3. [December '24: Özkan Pakdil, Clément de Tastes, Almas Baim, Steve Hannah, Jago de Vreede](https://foojay.io/today/video-series-javafx-in-action-part-3/)
4. [March '25: Mike Hearn, Sven Reimers, Chris Newland](https://foojay.io/today/video-series-javafx-in-action-part-4/)
5. [July '25: Cormac Redmond, Brian Schlining, Gerrit Grunwald, Dirk Lemmermann](https://foojay.io/today/video-series-javafx-in-action-part-5/)

Vlad Protsenko: Combining Clojure with JavaFX for Game Development with Defold {#h2-0-vlad-protsenko-combining-clojure-with-javafx-for-game-development-with-defold}
--------------------------------------------------------------------------------------------------------------------------------------------------------------------

[Vlad Protsenko](https://www.linkedin.com/in/vlad-protsenko-0999b2163/) is a Senior Developer with proficiency in many JVM-based languages. He worked both in very small and large teams, gaining experience in developing projects of various sizes, from scratch and from legacy codebases. He enjoys full-stack development, writing backend, frontend, and Android applications. He started as a game developer and switched to developing enterprise software, currently mixing both at Defold.

The [Cljfx library](https://www.jfx-central.com/tools/cljfx) is a declarative, functional, and extensible wrapper of JavaFX. It's inspired by the better parts of react and re-frame:

* Like react, it allows to specify only desired layout, and handles all actual changes underneath. Unlike react (and web in general) it does not impose xml-like structure of everything possibly having multiple children, thus it uses maps instead of hiccup for describing layout.
* Like reagent, it allows to specify component descriptions using simple constructs such as data and functions. Unlike reagent, it rejects using multiple stateful reactive atoms for state and instead prefers composing ui in more pure manner.
* Like re-frame, it provides an approach to building large applications using subscriptions and events to separate view from logic. Unlike re-frame, it has no hard-coded global state, and subscriptions work on referentially transparent values instead of ever-changing atoms.
* Like fn-fx, it wraps underlying JavaFX library so developer can describe everything with clojure data. Unlike fn-fx, it is more dynamic, allowing users to use maps and functions instead of macros and deftypes, and has more explicit and extensible lifecycle for components.

{{< youtube 1JL6zdkM1GU >}}

More info in [this blog post](https://webtechie.be/post/2025-10-16-jfxinaction-vlad-protsenko-closure-cljfx-defold/).

Matt Coley: Diving into byte code and JARs with Recaf and JavaFX libraries {#h2-1-matt-coley-diving-into-byte-code-and-jars-with-recaf-and-javafx-libraries}
------------------------------------------------------------------------------------------------------------------------------------------------------------

[Matt Coley](https://www.coley.software/) got into Java development when he wanted to find out how Minecraft works. Because of his many experiments, he gained a lot of knowledge about Java byte code, how it can be converted back to Java code, and how JARs can hide the real code from the user or contain malicious code. He combines all his knowledge in the Recaf tool and the JavaFX libraries he created.

[Recaf](https://recaf.coley.software/home.html) is an open-source Java bytecode editor that simplifies the process of editing compiled Java applications. To make things easier, Recaf abstracts away much of the class file format. Challenging tasks such as updating stack frames are done automatically. Along with additional features to help in the process of editing classes, Recaf is the most feature-rich bytecode editor available.

As Recaf wants to provide an IDE similar to IntelliJ IDEA, Matt needed a framework to manage tabs and docking. As he didn't find the perfect solution, he created the library [BentoFX](https://github.com/Col-E/BentoFX). Another nice visualization library he created for Recaf, is [TreeMapFX](https://github.com/Col-E/TreeMapFX), a flexible tree map chart control for JavaFX. Next to these libraries, Matt also created [GLCanvas-FX](https://github.com/Col-E/GLCanvasFX), a small project showing a single basic JavaFX control used to display OpenGL content from an JOGL `GLAutoDrawable`. They are demonstrated in the interview, and available on GitHub.

{{< youtube 6NIJ54h3iVY >}}

More info in [this blog post](https://webtechie.be/post/2025-10-30-jfxinaction-matt-coley-recaf-bentofx-treemapfx-glcanvasfx/).

Craig Raw: Sparrow Bitcoin Wallet {#h2-2-craig-raw-sparrow-bitcoin-wallet}
--------------------------------------------------------------------------

[Craig Raw](https://github.com/craigraw) is the creator of the Sparrow Bitcoin Wallet. He lives in South Africa. Funny fact: in the video, you can hear that he is surrounded by birds who wanted to join the conversation. Craig loves Java and JavaFX because of how easy it is to create user-friendly interfaces. He also values the security built into Java. Another important aspect for him is the ability to create reproducible builds, a key factor in the security of the Bitcoin ecosystem.

[Sparrow](https://www.sparrowwallet.com/) is a Bitcoin wallet for those who value financial self sovereignty. Sparrow's emphasis is on security, privacy and usability. Sparrow does not hide information from you - on the contrary it attempts to provide as much detail as possible about your transactions and UTXOs, but in a way that is manageable and usable.

Sparrow supports all the features you would expect from a modern Bitcoin wallet, however, it is also unique in that it contains a fully featured transaction editor that also functions as a blockchain explorer. This feature not only allows editing of all transaction's fields, but also allows you to easily inspect the transaction bytes before broadcasting.

{{< youtube Mc3fUTxoKIg >}}

More info in [this blog post](https://webtechie.be/post/2025-11-20-jfxinaction-craig-raw-sparrow-bitcoin-wallet/).

Florian Enner: Robot 3D Visualizations and Charts {#h2-3-florian-enner-robot-3d-visualizations-and-charts}
----------------------------------------------------------------------------------------------------------

[Florian Enner](https://www.linkedin.com/in/florian-enner-59b81466/) works on software libraries and applications that are aimed at simplifying development of robotic systems. For example, he has worked on an API for MATLAB that is capable of low-latency real-time control of high degree of freedom systems. It allows engineers without a strong background in computer science to develop sophisticated control algorithms without requiring knowledge of C/C++ or code generation.

In the video, Florian shows some of the JavaFX applications he has created to visualize the movements of robots in a 3D environment and the measurements of sensors in charts capable of rendering millions of data points. Based on the same code, he has created desktop and mobile applications that can receive the UDP messages directly from the devices and render the same charts and 3D.

He also demonstrates styling with AtlantaFX, the use of SceneBuilder, and how he compiles JavaFX views to native libraries so they can be integrated in C/C++ projects to visualize big amounts of data.

{{< youtube _DGz4YyojpE >}}

More info in [this blog post](https://webtechie.be/post/2025-12-04-jfxinaction-florian-enner-robot-3d-charts/).
