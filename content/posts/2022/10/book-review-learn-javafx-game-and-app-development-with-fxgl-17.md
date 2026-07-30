---
title: "Book review: Learn JavaFX Game Development with FXGL 17"
slug: "book-review-learn-javafx-game-and-app-development-with-fxgl-17"
date: "2022-10-19T07:20:32+00:00"
lastmod: "2023-03-16T10:45:28+00:00"
description: "FXGL provides an impressive list of components with methods to override and build the exact functionality your game requires."
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2022/10/41ou5Nfo9DL.jpeg"
categories:
  - "Book Review"
  - "Books"
  - "Game Development"
  - "JavaFX"
tags:
related_posts:
  - "new-book-fxgl-17-learn-javafx-game-and-app-development"
  - "creating-a-snake-game-with-javafx-fxgl-in-three-pair-programming-sessions"
  - "getting-started-with-fxgl-game-development"
frozen: false
---

This summer, I read the book [**"Entreprenerd"** by **Bruno Lowagie**](https://entreprenerd.lowagie.com/). It tells the story of how he started with the [**iText** PDF Java library](https://itextpdf.com/) and turned that into a company together with his wife, and eventually sold it with all problems related to most sales and acquisitions trajects...

In "Entreprenerd," he also describes the process of writing two books about the iText library itself, as there were no good manuals available, and he wanted to liberate himself from the ever-returning same questions.

So, when I received this book about **FXGL**, I immediately had to think back to Bruno's story. Who better to write a book about FXGL than Almas, its creator?

<figure class="wp-block-image size-full is-resized is-style-default">
 <img fetchpriority="high" decoding="async" src="/images/posts/2022/10/book-review-learn-javafx-game-and-app-development-with-fxgl-17/41ou5Nfo9DL.jpeg" alt="" class="wp-image-60838" width="248" height="375">
</figure>

[**Dr. Almas Baimagambetov**](https://twitter.com/AlmasBaim) is a principal lecturer in computer science at the University of Brighton, UK. He has considerable software development experience and is a huge fan of open source. His prominent contributions to the JVM community on GitHub include the FXGL game engine, collaborations on numerous JavaFX projects, a wide range of open-source games, and a collection of practical tutorials.

Almas also has a YouTube channel focused on Java, Kotlin, JavaFX, Unity, and Unreal Engine. He is the creator of FXGL, and although there are now [over 40 contributors to the GitHub project](https://github.com/AlmasB/FXGL/graphs/contributors), the main part of the development is done by him.

**Warning**... I'm personally a big fan of his work and used FXGL in a few projects for mobile and on the Raspberry Pi (see related articles at the bottom of this page), so I'm a bit biased.

But on the other hand, I'm still a novice in game development. I have been looking forward to the release of this book as I wanted to get more insight into how it works and what can be achieved with it.

About FXGL {#h2-0-about-fxgl}
-----------------------------

No better way to introduce FXGL than by stealing the introduction from the book:

*JavaFX is a modern high-performance graphical user interface (GUI) toolkit for Java. It provides hardware acceleration support on a range of platforms, including desktop, mobile, and embedded, allowing the development of cross-platform applications. However, to develop games with JavaFX effectively, numerous domain-specific concepts are needed.*

*To address this need, the FXGL game engine extends JavaFX and brings support for real-world game development techniques. These include the entity-component model, pathfinding, physics, particle systems, sprite sheet animations, and many other features. As a result, JavaFX developers can produce games more quickly and more effectively with FXGL and easily package their games to native platform images, including Android and iOS.*

A walk through the book {#h2-1-a-walk-through-the-book}
-------------------------------------------------------

The book contains 215 pages with eight chapters, most containing a demo application to explain a specific part of the functionality of the FXGL library.

* Chapter 1 (Introduction) and chapter 2 (Requisite Concepts) introduce Java programming basics, the FXGL project, and explain the services (timers and life cycle) and entity-components that make up the elements of a game. But on page 25, we already start creating our first FXGL that uses keyboard inputs to move a square on the screen.
* One of the oldest computer games is the topic of chapter 3. By using several components (MoveSpeedComponent, CollidableComponent, etc.), it is clear that FXGL focuses on a straightforward SOLID approach by implementing each required functionality in a separate component, either already provided by FXGL or by a custom class. By combining such components, game elements can be spawned into the game world that is configured and controlled in the main app-class to create a playable game.
* In the next parts, more in-depth information is given on essential concepts like Collision Detection and Physics Components (Mario-style game in chapter 4), and AI basics with Path Finding and Behaviors (Pacman game in chapter 5).
* Chapter 6 gives you more information about visually complex features related to graphics and rendering. A Particle System is used to generate cloud-like effects such as fireworks and explosions. With the Animation System, translations can be performed on positions, rotations, scale, opacity, color, and other custom properties. Finally, the Interpolation System is explained for easing and tweening game entities.
* Although FXGL focuses on game development, its components can also be used to build business-like applications. On this site, you can find one example, "Device Monitoring with JavaFX and FXGL," and the book gives some more examples in chapter 7. Also, the packaging and cross-platform deployment are briefly described here, using the GluonFX Maven plugin and jlink.
* Finally, chapter 8 concludes with a FAQ, future projects, and extra resources.

How FXML can be combined with standard JavaFX components and FXML-files to generate JavaFX UIs inside a game world is also explained throughout the book, making it clear that any developer with Java and JavaFX experience will be able to generate any User Interface. Also, the relation between the JavaFX Stage, Scene, and how everything is rendered and related is clearly described.

Key Take-Aways {#h2-2-key-take-aways}
-------------------------------------

I don't know if Almas is hoping to sell his FXGL library for a big cheque and [buy a beautiful old cinema as Bruno Lowagie did](https://www.lowagie.com/goed-nieuws), but **providing a clean and well-written tutorial is a goal he reached for sure**. FXGL provides an impressive list of components with methods to override and build the exact functionality your game requires.

Only a few minor points of criticism about the book. The paper version is printed in black-and-white. There are not that many images in the book, but as they are screenshots of the games, it would have been nicer to see them in color. And some concepts and abbreviations are introduced without enough description for a (non-game-)developer like me (\*).

Don't expect a book listing all the methods provided by FXGL. No, **the best approach to learning from the book** is to thoroughly examine the examples and modify values and methods to see the impact on speed, gravity, movement, etc. This way, you will be able to create your version of each of the provided examples, learn how the library works, and be able to create your own games and visual applications.

*** ** * ** ***

(\*): Almas was so helpful in giving the full terms to add here:

* tpf = time per frame
* DSL = domain-specific language
* NPC = non-playable character
* AAA = high-quality, i.e. triple A class
* Torus = in 3D geometry, a doughnut like shape
