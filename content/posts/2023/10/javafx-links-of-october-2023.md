---
title: "JavaFX Links of October 2023"
slug: "javafx-links-of-october-2023"
date: "2023-10-31T13:04:32+00:00"
lastmod: "2023-10-31T13:08:36+00:00"
description: "Have fun with this overview of the \"JavaFX LinksOfTheWeek\" that got published on jfx-central.com during October."
canonical: "https://webtechie.be/post/2023-10-27-javafx-links-of-october-2023/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-september-2023"
  - "javafx-links-of-august-2023"
  - "javafx-links-of-july-2023"
  - "javafx-links-of-june-2026"
frozen: false
---

Thanks to the Devoxx conference, there are many hours you can spend on JavaFX-related videos!

But as every month, there is a lot more to read and learn about JavaFX...

Have fun with this overview of the "JavaFX LinksOfTheWeek" that got published on [jfx-central.com](https://www.jfx-central.com/) during October.

Core {#h2-0-core}
-----------------

* Last month Java and JavaFX 21 were released. And [**Johan Vos** already had an update](https://mastodon.social/@johanvos/111147510047965780): "JavaFX 22-ea+11 has just been released to maven central (and on [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx)). This contains the much anticipated memory improvements :)"
  * "This contains a major change in some CSS processing, see [this commit by **John Hendrikx**](https://github.com/openjdk/jfx/commit/5e145cc06ef68c50a4ffc95574fdafd44e054100), that leads to improved performance (less GC). Please test and evaluate."
* **Kevin Rushforth** and [**Johan Vos**](https://mastodon.social/@johanvos) had a BOF at Devoxx in Antwerp.
  * During this BOF, some of the new features that could be included in JavaFX 22 were presented:
  * Platform APIs
  * (unlikely) CSS theming
  * InputMap/Behavior
  * Feedback was asked from the users regarding blocking issues in JavaFX itself to be able to build more amazing stuff:
  * Removal of remaining AWT dependencies.
  * Full integration with the desktop (alerts, dark/light,...).
  * Missing 3D point and line drawing methods.
* JavaFX 21.0.1 and JavaFX 17.0.9 are now available for download from the [Gluon website](https://gluonhq.com/products/javafx) and from Maven Central. "[Enjoy Java on the client!](https://techhub.social/@gluonhq/111255546373474509)".

SceneBuilder {#h2-1-scenebuilder}
---------------------------------

* Version 21 is [now available](https://github.com/gluonhq/scenebuilder/releases/tag/21.0.0)!
  * Refactored: Clean up DocumentWatchingController
  * Uses JDK and JavaFX 21
  * Contributors:
    * Abhinay Agarwal
    * Almas Baim
    * José Pereda
* Scene Builder 21.0.1 RC1 is [available for testing](https://github.com/gluonhq/scenebuilder/releases).

Applications {#h2-2-applications}
---------------------------------

* At Devoxx we got blown away by the demos given by Florian Enner to visualize robot arms and interact with them. He wasn't a speaker at the conference, but we sure hope he will be next year or on any other conference. Make sure to check out of few of his videos...
  * [JavaFX w/ GraalVM native image (Windows)](https://www.youtube.com/watch?v=XxVoG1ft7w8): a desktop application with charts and controls, but a robot simulated in 3D.
  * [JavaFX: migrating to AtlantaFX themes](https://www.youtube.com/watch?v=vjl5tz8bE90): migrating from a custom JavaFX design to AtlantaFX themes. And even more 3D robots and charts!!!
  * [JavaFX 3D: Dynamic CubeWorld](https://www.youtube.com/watch?v=Xac03kLqKrA): 3D cubes, a loooooot of cubes...
  * And these are [all his YouTube videos](https://www.youtube.com/@florianenner7435/videos?view=0&sort=dd&shelf_id=0).
* [The Hero app by **Pedro Duque Vieira** has an alert/error system where the button will show as filled whenever there's an error/alert](https://twitter.com/p_duke/status/1707409002026463457): "This immediately warns u in a subtle way that u may have things to fix. After you've fixed everything the alert button shows up empty."
  * He also shared more info and a video about [Visual Styles in HERO](https://twitter.com/P_Duke/status/1713901762027905025): "It allows the user to see properties of his CAD project quickly, at a glance."
* [RNArtist has been updated](https://github.com/fjossinet/RNArtist/tags), mainly to run on Windows. Releases 1.0.8 and 1.0.9 are available (just restart RNArtist). 1.0.9 will just increase the maximum memory to be used (needed for Windows).
  * Nice new [screenshot by **RNArtist**](https://twitter.com/rnartist_app/status/1714315738691903512) showing: "Insight into a random RNA 2D with a size of 1kb and a pairing density of 60%. Bracket notation with colored helices in the lower part. Simple rendering of the 2D plot in the upper part.".
  * Here you can find [a picture to summarize the new workflow](https://twitter.com/rnartist_app/status/1715295482195349674) that will be in RNArtist 1.1.0.
  * And he shared a blog post: [Visualize experimental data on your RNA 2D](https://fjossinet.github.io/visualize-experimental-data/).
* [**Clemens Lanthaler** release V1.3.3 of Photoslide](https://github.com/lanthale/PhotoSlide/releases/tag/v1.3.3), a simple photo management application with a modern and reactive user interface. This version brings updates to JavaFX 21/JDK21 and fixes to the filter module: "Thanks to FXGL examples the filters are now realtime and therefore I can now start implementing more filters and the edit module".
* **Tobias Briones** is working on [Building Slides from Screenshots App in JavaFX: "A great title where I granularly blogged the development of a powerful JavaFX desktop app"](https://blog.mathsoftware.engineer/leveraging-git-to-finish-my-article-2023-10-09).
  * And he has a ["JavaFX app that uses Tesseract OCR to detect words in the slide images, allowing users to underline them accurately"](https://www.linkedin.com/posts/tobiasbriones_softwareengineering-machinelearning-ai-activity-7117552150427095040-uYtb/).
* **OrangoMango** shared a [video showing his logic simulator app](https://youtube.com/shorts/bP25LmL8TCk). More info, link to the sources and browser version can be found on [orangomango.itch.io/logicsimulator](https://orangomango.itch.io/logicsimulator).

Games {#h2-3-games}
-------------------

* **GZYangKui** has spent some time optimizing the audio and video output of his Nintendo-like game: ["The results were quite satisfactory"](https://twitter.com/YangKui7/status/1711764945946501188).
  * And shared a few retro game videos:
  * [The taste of childhood...](https://twitter.com/YangKui7/status/1715650412856893867).
  * [Successfully supported the Nintendo MMC3 cardridge](https://twitter.com/YangKui7/status/1717077263533949406).
* **Alessio Vinerbi** is making a [trash game](https://twitter.com/Alessio_Vinerbi/status/1710307540775354454).
* [**Almas Baim** shared a video](https://twitter.com/AlmasBaim/status/1658565437053980691) after a "fruitful discussion with game dev students on how to improve the UI. Check out the new health and weapon indicators around the player. Now there is no need to look elsewhere to get this info."

Components, Libraries, Tools {#h2-4-components-libraries-tools}
---------------------------------------------------------------

* [A new library announcement by **Pedro Duque Vieira**, FXThemes](https://pixelduke.com/2023/10/02/fxthemes-java-javafx-library-released/): "It is a Java library that contains classes to help in advanced JavaFX theme development. Right now, it contains helper classes to change the appearance of a native window frame as well as the backdrop of JavaFX native Windows." The announcement page also contains more info about the reason of providing this functionality in a new library.
  * True Dark Mode on Windows10 [will be available soon in a new release of FXThemes](https://twitter.com/P_Duke/status/1714655184327168474): "Win10 is the most used Windows version (Windows being the most used OS). This was a pain to implement. Native code is different from win11. On win10 this API is undocumented."
* [GemsFX 1.82.0 by **Dirk Lemmermann**](https://twitter.com/dlemmermann/status/1707043072956113368) with:
  * Early access version of a TreeNodeView with many configuration options (added by [**LeeWyatt**](https://twitter.com/LeeWyatt_7788))
  * Gives you controls / pickers for: choosing a date, choosing a date range, choosing a month, choosing a year, choosing a time, choosing a duration.
  * See the [DateRangePicker control in action here](https://www.youtube.com/watch?v=n7HesjJZ7K4).
* **JPro** announced [version 2023.3.0](https://www.jpro.one/docs/current/3.1/2023.3.X): JavaFX 21 is used by default, and many more improvements!
  * And [version 2023.3.1](https://jpro.one/docs/current/3.1/2023.3.X) with a critical bugfix to handle bot traffic better, and a new feature regarding MimeTypes.

Podcast, Video, Books {#h2-5-podcast-video-books}
-------------------------------------------------

* **Robert von Burg** and **Frank Delporte** had some fun during a live [video stream with LED strips and a JavaFX UI](https://www.youtube.com/watch?v=eToIXACqSuY).
* Presentations at Devoxx in Antwerpen, Belgium:
  * **Kevin Rushforth**:
  * [Building and Deploying Java Client Desktop Applications With JDK 21 and Beyond](https://www.youtube.com/watch?v=Afehjldx4yM)
  * [JavaFX Notebook](https://www.youtube.com/watch?v=R9yhbaN5Xxs)
    * [Remark by **Chad Preisler**](https://twitter.com/cpreisler/status/1710291345590345799): "JavaFX Notebook would attract the scientific community to Java, because if you're crunching large amounts of data there is nothing faster than JavaFX and Java, and scientists are usually not programmers. This needs to be easily extendable."
  * **Johan Vos**:
  * [Quantum Computing in Java: an exceptionential opportunity](https://www.youtube.com/watch?v=eylmTHUGcks)
  * [**Sean Phillips**](https://jvm.social/@Birdasaur):
  * [Explainable AI Analysis Visualization: Applications from Brain Computer Interfaces to ChatGPT](https://www.youtube.com/watch?v=LYtZRWo4t4E).
  * In between sessions, he [updated the dev branch for of his XAI tool Trinity](https://twitter.com/SeanMiPhillips/status/1709184231916573012) to support audio file processing, playback and spectrum analysis in 3D.
  * [**Cédric Champeau**](https://mastodon.xyz/@melix):
  * [JSol'Ex : solar image processing written in Java](https://www.youtube.com/watch?v=j6KMOXhldEs).
  * **Paul and Gail Anderson**:
  * [Say the Words: Modern Java with JavaFX and GraalVM for Rich Client UIs](https://www.youtube.com/watch?v=3nT8vurpmqc).
  * **Thanos Stratikopoulos** : [TornadoVM: Write once, run everywhere everywhere!](https://www.youtube.com/watch?v=POanHvoC4qA). He demonstrates an impressive performance boost with a JavaFX ray tracer using the GPU. Sources of the demo are available on [github.com/Vinhixus/TornadoVM-Ray-Tracer](https://github.com/Vinhixus/TornadoVM-Ray-Tracer).
  * **Nicolai Parlog** published the [second half of his video report of Devoxx Belgium](https://www.youtube.com/watch?v=WoQJnnMIlFY), including an interview with **Kevin Rushforth** about GUI development with Java.

Tutorials {#h2-6-tutorials}
---------------------------

* Please keep an eye on **Edward Nyirenda Jr** 's website as he keeps on giving! For instance, ["JavaFX with Gradle"](https://coderscratchpad.com/javafx-with-gradle/) was published this month.

Miscellaneous {#h2-7-miscellaneous}
-----------------------------------

* [**Pedro Duque Vieira** shares a video](https://twitter.com/P_Duke/status/1711370140216955086) to illustrate that a JavaFX scene graph can handle a lot of nodes: "The view on the left is using retained mode rendering. There are more than 1k nodes visible at a time and yet without major optimizations it runs fine. Tip: No need to rush to a Canvas implementation (immediate mode)."
* Impressive 3D results by **OrangoMango** , considering he is running it on a Raspberry Pi: ["If I render the triangles, the application runs at 5 fps."](https://twitter.com/orango_mango/status/1711777605702005138).
* **Homebookner** shared a link to an interesting project by **Sheikah45** : [FX2J - FXML to Java Builder](https://github.com/Sheikah45/fx2j): a post-processor that converts fxml files into Java builder source files.
* **Christopher Schnick** [reported some issues on new gnome-based desktops](https://mail.openjdk.org/pipermail/openjfx-dev/2023-October/043118.html), e.g. on Ubuntu 23: "For the JavaFX devs who use the system tray, note that there are various GTK issues. If you are using FXTrayIcon, your app will freeze."

JFX Central {#h2-8-jfx-central}
-------------------------------

* New content
  * Libraries
  * [Transit Theme](https://www.jfx-central.com/libraries/transit): builds upon the lessons and my work with JMetro.
  * [FXThemes](https://www.jfx-central.com/libraries/fxthemes): classes to help in advanced theme development.
  * Showcase application
  * [Trinity](https://jfx-central.com/showcases/trinity): Explainable AI analysis tool and 3D visualization) by **Sean Phillips**.
  * Tutorials
  * We already mentioned a few times the tutorials by **Edward Nyirenda Jr** in these Links Of The Week. You can [find them now in the tutorial-section](https://www.jfx-central.com/tutorials/coderscratchpad).
* The summary with all the JFX Central links of September got [published on Foojay.io](https://foojay.io/today/javafx-links-of-september-2023/).
* **Dirk Lemmermann** shared a [screenshot that shows the very helpful menu](https://twitter.com/dlemmermann/status/1716417891371770052), provided when you locally install JFX Central.
* A [first video by **Dirk Lemmermann**](https://twitter.com/dlemmermann/status/1714227457086124072) showing JFX Central running as an app on iOS.
  * ["Entered a first testing phase for iOS. Hopefully will be available soon in Apple's AppStore"](https://twitter.com/dlemmermann/status/1715759319457665510).
  * But it seems [Apple doesn't like it...](https://twitter.com/dlemmermann/status/1717481578408841255)
* Multiple tools will be available soon:
  * SVG Path Extractor
  * Effect Designer (shadows, blurs, etc...)
  * Gradient Designer
  * CSS Playground
  * [Pixel to EM Converter](https://twitter.com/dlemmermann/status/1716343834584506864) for quickly converting all your pixel values in your CSS file to em values for proper scaling.
* Work is ongoing by **Li Wang Yang** to bring new "Learn" sections to JFX Central:
  * "Getting Started with JavaFX"
  * "JavaFX on Mobile"
  * "JavaFX on Raspberry Pi"
  * Thanks to [**Gail Anderson**](https://www.jfx-central.com/people/g.anderson), [**Paul Anderson**](https://www.jfx-central.com/people/p.anderson), [**Edward Nyirenda Jr**](https://www.jfx-central.com/people/e.nyirenda), and [**Frank Delporte**](https://www.jfx-central.com/people/f.delporte) for sharing their tutorials!
  * Check the tickets to see what is happening...
  * [#512 New main section: Learn](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2/issues/512)
  * [#312 Content for the Learn JavaFX section](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data/issues/312)

<br />

<br />
