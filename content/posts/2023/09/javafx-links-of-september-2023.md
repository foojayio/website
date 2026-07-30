---
title: "JavaFX Links of September 2023"
slug: "javafx-links-of-september-2023"
date: "2023-09-30T15:21:12+00:00"
lastmod: "2023-10-01T15:21:26+00:00"
description: "Here is the overview of the JavaFX LinksOfTheWeek that got published on jfx-central.com during September."
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
  - "JDK21"
tags:
related_posts:
frozen: false
---

This month OpenJDK and OpenJDK 21 got officially released, so links to the new downloads but also to the early access builds of the next one!

Here is the overview of the JavaFX LinksOfTheWeek that got published on [jfx-central.com](https://www.jfx-central.com/) during September.

Core {#h2-0-core}
-----------------

* [Announcement by **GluonHQ**](https://techhub.social/@gluonhq/111092296190292324): "We're proud to announce JavaFX 21 GA. Download the SDK/jmods from [gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) or get the artifacts from Maven central. Release notes with many fixes and enhancements at [gluonhq.com/products/javafx/openjfx-21-release-notes](https://gluonhq.com/products/javafx/openjfx-21-release-notes/). Great work by a growing number of contributors!"
* [Release notes by **Kevin Rushforth** on GitHub](https://github.com/openjdk/jfx/blob/master/doc-files/release-notes-21.md).
* Builds are available from java.net:
  * [Linux / x64](https://download.java.net/java/GA/javafx21/69ca518c413e4df09f6be747a2400cf6/31/GPL/openjfx-21_linux-x64_bin-sdk.tar.gz)
  * [macOS / AArch64](https://download.java.net/java/GA/javafx21/69ca518c413e4df09f6be747a2400cf6/31/GPL/openjfx-21_macos-aarch64_bin-sdk.tar.gz)
  * [macOS / x64](https://download.java.net/java/GA/javafx21/69ca518c413e4df09f6be747a2400cf6/31/GPL/openjfx-21_macos-x64_bin-sdk.tar.gz)
  * [Windows / x64](https://download.java.net/java/GA/javafx21/69ca518c413e4df09f6be747a2400cf6/31/GPL/openjfx-21_windows-x64_bin-sdk.zip)
  * And on the [Gluon website](https://gluonhq.com/products/javafx/).
* [EA (early access) builds of JavaFX 22](https://mastodon.social/@openjdk/111109621684880527)!
  * JavaFX 22 Early-Access Builds are available here:
  * [jdk.java.net/javafx22](https://jdk.java.net/javafx22/)
  * [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/)
  * [Issues addressed in this build](https://bugs.openjdk.org/issues/?jql=project%20%3D%20JDK%20AND%20fixversion%20%3D%20jfx22%20and%20component%20%3d%20javafx%20and%20%22resolved%20in%20build%22%20%3d%20b10%20order%20by%20component%2C%20subcomponent).
  * The proposed schedule for JavaFX 22:
  * RDP1: Jan 11, 2024 (aka "feature freeze")
  * RDP2: Feb 1, 2024
  * Freeze: Feb 29, 2024
  * GA: Mar 19, 2024
  * JavaFX 22-ea is designed to work with JDK 22-ea, but it is known to work with JDK 17 and later versions.
  * [**Dirk Lemmermann** is experimenting with 22-ea+9](https://twitter.com/dlemmermann/status/1707024018232983615): "Showing correct bold text on macOS and much lower initial memory consumption in our CRM software".
* [A message by **Johan Vos**](https://mastodon.social/@johanvos/111121272095237602): "One of the great (non-technical) things about JavaFX is that it is not owned by a single company that tries to use it for its business strategy (e.g. like Google does with most of their "free" stuff). It is really the wide JavaFX ecosystem that drives JavaFX forward, and that allows for pure and honest innovation."

Applications {#h2-1-applications}
---------------------------------

* [**Onkel Stipe** shared a screenshot of SoundLab](https://twitter.com/OnkelStipe/status/1696236782885474494): "I developed it to investigate in generating realtime virtual analog sounds. The Oscilloscope feels quite analog. It also has a realtime spectrum and a view called "klangteppich", which nicely imitates the spectral view of audacity."
* [**WhiteWoodCity** is upgrading dillon-boot-fx, a Permission Management System UI](https://twitter.com/WhiteWoodCity/status/1694998831480614954), to JDK 17+ and also providing jigsaw support.
* [Fx Calculator by **Maciek Gorywoda** is now available at Google Play Store](https://twitter.com/makingthematrix/status/1695108624400482636)! "Written in Scala 3, JavaFX, and built with GraalVM Native Image"
* The next version of JDKMon, the tool to keep your Java installations up-to-date, will now also show you the dates of the next OpenJDK update and the next OpenJDK release, [says **Gerrit Grunwald**](https://mastodon.social/@hansolo_/111028113635396197).
* [JabRef is using Java 21 for their new release 5.10](https://foojay.social/@jabref/110994245236386244).
* The [Logic simulator by **OrangoMango** got updated to v1.1 update](https://twitter.com/orango_mango/status/1698269059513462867): "A new web version made with WebFX, bug fix, RGB lights and some UI changes."
* [**surajit** completed M-AID (Retail Pharmacy Management)](https://twitter.com/surajit8017/status/1700781040779022628): "With a mobile app that is always connected to the desktop app via Wi-Fi, no internet is needed for operations."
* [RNArtist by **Fabrice Jossinet** offers now an undo/redo feature](https://twitter.com/rnartist_app): "It can even iterate automatically over its history. Take a look at my attempt to produce a better layout for an archaea LSU rRNA (green blinking = redo, orange = undo)."
* [**Biometrics Engineer** shared a video](https://twitter.com/Biometrics_Eng/status/1704443412546474137) showing a "JavaFX Linux Biometric Time and Attendance - Staff Registration DEMO that is implemented on Ubuntu Linux using ARATEK A600 Biometric Fingerprint Scanner."
* [**RNArtist** is announcing](https://twitter.com/rnartist_app/status/1702674834193252446): "Installers will be available next week. Thanks to the very good tool JDeploy, you will automatically get the latest version after each launch of RNArtist. Now it's time to write some documentation..."
  * And shared [screenshots of the tool 3 years ago versus now](https://twitter.com/rnartist_app/status/1702641134923583639), with some very nice comments and links to the project in the thread.
  * BTW, as someone pointed out, you can maybe use this tool to design airport terminals... 😉
* [**Sean Phillips** is using Trinity for](https://twitter.com/SeanMiPhillips/status/1703038446854345000): "Detecting ChatGTP generated medium size text blocks using manifold approximation, polyhedral volume techniques and JavaFX 3D. Sorry not sorry bad guys."
  * And [Apple M2 silicon (Arm64) builds](https://twitter.com/SeanMiPhillips/status/1703172036669972494) now supported through Trinity's GitHub Actions thanks to the magnificent CI work of samypr100!
* [**Malik Hamida** is working on a micro banking system project](https://twitter.com/_MalikHamida/status/1707405711401963981)
* [**Eltayeb Ibrahim** created a user-friendly Product Management System using JavaFX for the frontend](https://www.linkedin.com/posts/eltayeb-ibrahim-98324a193_javafx-java-javascript-activity-7113009374276526080-G99X) and Node.js with Express.js framework for the backend.
* [**Heshan Thenura Kariyawasam** made a File Shredder app](https://twitter.com/Heshantk/status/1707473425395286439) and shared the sources.

Games {#h2-2-games}
-------------------

* [London Softworks is working on a new Particle Editor and making progress with an OpenGL context being rendered to a JavaFX Canvas object (in real-time)](https://twitter.com/LondonSoftworks/status/1696427139212447753): "Making good use of FBO's for this one. Now to sort the colour issue out..."
* [**OrangoMango** announces "Chess 2.0 is finally complete"](https://twitter.com/orango_mango/status/1703057689582993815): "The WebFX version is now available at [orangomango.itch.io/chess](https://orangomango.itch.io/chess). Play single player against stockfish. Play multiplayer against a friend in LAN or on the server."
* Always fun when [**Almas Baim** shares one of his experiments with FXGL](https://twitter.com/AlmasBaim/status/1703483255331094569): "... and in this episode of how to inefficiently clear the screen ..."
* [**Ahmed Bakr** completed a Tic Tac Toe Game with Tiva C and JavaFX](https://www.linkedin.com/feed/update/urn:li:activity:7109933250885685248/): "Our primary goal in undertaking this project was to explore the integration of microcontrollers with high-powered processing computers to tackle tasks beyond the capabilities of a standalone microcontroller."
* [**WhiteWoodCity** made his game demo and self-made game utilities](https://twitter.com/WhiteWoodCity/status/1706656526726045930) fully open source on [github.com/chengenzhao/fxcity](https://github.com/chengenzhao/fxcity).

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* [**Pedro Duque Vieira** released a new library, a JavaFX theme called Transit Theme](https://twitter.com/P_Duke/status/1696149890370285877): "Modern look and feel, Zero tight coupling, Looks integrated on Windows, Also works well on other OSes, Light and Dark versions,..."
  * The announcement of this new Transit Theme [got published on **Foojay.io**](https://foojay.io/today/new-javafx-theme-library-transit-released/).
  * **Pedro** is also working on a [**dark mode**](https://twitter.com/P_Duke/status/1701577162015363556): "JavaFX Windows are always shown in Light mode (window frames with light color). This new API I've created will allow the developer to set dark mode on a native window. New API will also allow other changes to a window."
  * [And he added a new API to FXComponents](https://twitter.com/P_Duke/status/1704488446138454335): "To allow you to change the native Window frame color, native Window text color, and border. (Standard JavaFX API will always show a native Window frame with the same light color with no possibility to change it.)"
  * The release of the new FXComponents library was also [published on Foojay](https://foojay.io/today/new-fxcomponents-library-released/).
* [**Dirk Lemmermann** released version 3.0.0 of PDFViewFX](https://twitter.com/dlemmermann/status/1697192575969493372): "This release is based on Apache PDFBox 3, which contains an impressive list of fixes and enhancements."
  * [**Dirk** has a friendly reminder](https://twitter.com/dlemmermann/status/1704045289743495407): "When creating custom controls for your JavaFX project, please make sure you have a way to properly test them standalone (e.g. via [FXSampler](https://jfx-central.com/tools/fxsampler))."
  * He is also [pimping the CalendarView control in GemsFX](https://twitter.com/dlemmermann/status/1704846555004502306) and adding all kinds of options: "E.g. different layout for the header and also quick picking months and years). Come and check it out, let me know what you think or I missed."
* [**JavaSuns** came upon this great small pure Java library, SimplePNG](https://twitter.com/javasunsFX/status/1696959645384839544), for storing JavaFX images to PNG files. Great compression ratios and file sizes achieved with the use of PNGJavaFXUtils class.
* A new week, a [new library by **Pedro Duque Vieira**: FXComponents](https://twitter.com/P_Duke/status/1698687283971432648?s=20). It contains a collection of new controls to be used in JavaFX applications.
* **WhiteWoodCity** extended JavaFX, FXGL, and AtlantaFX to reduce function of binding, multiple game scenes support and navigation of them, enhanced animations, self-made theme,... [Check the X thread](https://twitter.com/WhiteWoodCity/status/1697975469965263093?s=20), as he shared the code!
* [**Johan Vos** shared a link to the GitHub project of Rich Text Area](https://twitter.com/johanvos/status/1699335002599649549): "One of the cool GluonHQ tools we are developing/maintaining".
* [**Carl Dea** is prototyping a new JavaFX-based Dockable windowing library](https://www.linkedin.com/posts/carldea_javafx-activity-7081463432486477824-LCev/).
* [**Sven Ruppert** announced the release of version 4 of TestFX](https://mastodon.social/@svenruppert/111064088106916273), simple and clean testing for JavaFX. Check [GitHub for all changes](https://github.com/TestFX/TestFX/releases/tag/v4.0.17).
* [**Sean Phillips** announced a new release of the special effects library LitFX](https://twitter.com/SeanMiPhillips/status/1700852671287107803): lightning, radio waves/bands, line of sight lighting and shadowing, animated window and floating panes, flame convolution.
* [**Michael Paus** has done a basic POC for Skia integration into JavaFX](https://twitter.com/MichaelPaus/status/1701214040876323013).

Tutorials {#h2-4-tutorials}
---------------------------

* [**Mark Baird** released the second in a series of blog posts showing how to use the beta release of the Java Maps SDK (with support for Arm Linux devices) with a Raspberry Pi](https://www.linkedin.com/posts/mark-baird-5565786_how-to-use-the-java-maps-sdk-in-a-raspberry-activity-7100825332420599808-w5P7/) to integrate IoT data collection into your geospatial applications.
* [**Edward Nyirenda Jr** explains how to add TextField autocompletion in JavaFX using ControlsFX](https://twitter.com/EdwardAlgorist/status/1697359282449072213): "TextField autocompletion is a convenient feature that enhances user experience by providing suggestions or predictions as users type into a text field."
  * [Check his Twitter/X timeline for more tutorials](https://twitter.com/EdwardAlgorist).
  * Highlighting one of the many other tutorials ["JavaFX Clipboard"](https://twitter.com/EdwardAlgorist/status/1707165031622717625): "In the JavaFX framework, clipboard functionality is seamlessly integrated, making it easy for developers to provide a smooth and user-friendly experience."
  * All Edward's tutorials are available on his website [coderscratchpad.com](https://coderscratchpad.com/category/computer-programming/javafx/).
* **Gluon** shared four new tutorial/screencasts on YouTube:
  * [Introduction to Emoji](https://www.youtube.com/watch?v=ZIvSBK66tvQ)
  * [Introduction to Gluon Maps](https://www.youtube.com/watch?v=aST8C2N5k4g)
  * [Introducing Gluon Rich Text Area](https://www.youtube.com/watch?v=E2lREX9RFa0)
  * [Basic usage of Gluon Rich Text Area](https://www.youtube.com/watch?v=___Fp_vk4CY)
* [Part 4 of the video tutorials in French by **Jason Champagne**](https://twitter.com/jachampagne8/status/1700547961921352094): "On aborde les premiers contrôles utilisateur sur JavaFX" (First user controls in JavaFX).
* [**Ken Kousen** ("Tales from the jar side") shared a video](https://mastodon.social/@kenkousen/111035349071040667): "Generate images using Java. See how to write Java to drive the Dall-E image tool. Combined text blocks, JSON parsing, records, and even a JavaFX image carousel."
* **Edward Nyirenda Jr** is continuing his series of tutorials: [Internationalization in JavaFX: Building Multilingual Apps](https://twitter.com/EdwardAlgorist/status/1701348814156685713): "Internationalization is the process of designing your application so that it can be easily adapted to different languages and regions without code changes."
* In Portuguese by **Edivaldo Brito** : ["Como instalar o Gluon Scene Builder no Linux via Flatpak"](https://twitter.com/edivaldobrito/status/1702468670130425979) (How to install Gluon Scene Builder on Linux via Flatpak).

Miscellaneous {#h2-5-miscellaneous}
-----------------------------------

* [**Dirk Lemmermann** noticed something remarkable](https://twitter.com/dlemmermann/status/1696098284002054553): "A JavaScript account retweeting a JavaFX tweet? 🙂 What's next? Flying pigs? :-)"
* [**Frederic Thevenet** wrote a long thread on Mastodon](https://mastodon.social/@fthevenet/111028413815320945) to explain why "JavaFX is a very solid piece of tech which unfortunately came to maturity at the worst possible time for a "desktop application" GUI framework."
* Looking for a specific icon? Use [JFX Central as **Dirk Lemmermann** explains here](https://twitter.com/dlemmermann/status/1699812103937290590).
* Happy to see more and more references to JavaFX when people discuss app development, like [here by **@t3chn01200**](https://twitter.com/t3chn01200/status/1699016124661121192) , [here by **@schramMedia**](https://twitter.com/schramMedia/status/1698728400318341609) and [here by **@Aman_Raj2241**](https://twitter.com/Aman_Raj2241/status/1699840433596297593).
* [**Oliver Kopp** shared more info about the reason to use Java 21 in JabRef](https://mastodon.acm.org/@koppor/111054057514152665): "We needed to have the fix for a workaround of the 64kb limit of Java. We did not backport our fix to JDK20, but relied on a sufficient quality of JDK21 at the time of our release." Check [JDK-8240567](https://bugs.openjdk.org/browse/JDK-8240567) for more background info and a link to the merge request and code changes in OpenJDK.
* [**WhiteWoodCity** is combining JavaFX and JavaScript](https://twitter.com/WhiteWoodCity/status/1704296530117906743): "Using #Graal polyglot in a JavaFX program."
* [**Webswing**](https://twitter.com/Webswing_org), a specialized web server for running Java Swing and JavaFX based applications in a web browser, announced a [Spanish version of their website](https://www.webswing.org/es).
* From time to time, JavaFX (just like Java itself) is declared dead. Luckily, there are many fans to correct this mistake:
  * [**JavaFX3D**](https://twitter.com/JavaFX3D/status/1705089119041593691): "As someone who worked on JavaFX at both Sun and Oracle, I can definitively say that JavaFX is not deprecated. Period."
  * [**Jonathan Ellis**](https://twitter.com/spyced/status/1704888013094834602): "Users hate Electron's performance and memory footprint, but devs love it for write-once-run-anywhere. I know we stopped using Swing years ago for good reasons, but still: wouldn't JavaFX be better than Electron?"
  * [**JavaFX3D**](https://twitter.com/JavaFX3D/status/1705092556617576608): "I keep seeing uninformed posts, so I'd like to set the record straight. [**Kevin Rushforth** and his team at Oracle are working on JavaFX](https://www.youtube.com/watch?v=FFlVaB8oTi0). Equally importantly, **Johan Vos** and his team at **Gluon** are the JavaFX WORA magicians."
  * [**Sean Phillips**](https://twitter.com/SeanMiPhillips/status/1703401715351904439): "As the lead developer for several of the tools referenced in that article I can say as ground truth that JavaFX was selected as the superior tech stack due to a combination of quality, performance and cross-platform support."
  * [**SystemsInCode**](https://twitter.com/SystemsInCode/status/1704796679340224546): "I would love to leverage the benefits of JavaFX, but it's a hard sell in enterprise when we need to push a critical fix... It's a shame we have to use sub optimal tech for boring practical reasons 🙂 Being able to FORCE latest version on people even with low rights is critical."
* [**Pedro Duque Vieira** shared some hidden messages](https://twitter.com/P_Duke/status/1706658008820830476): "Hint: squint, move your phone/monitor further away or zoom out, to see it more clearly."
* [**Christopher Schnick** believes JavaFX applications can always use some more Duke](https://twitter.com/crschnick/status/1706982902951485727).

JFX Central {#h2-6-jfx-central}
-------------------------------

* The JFX Central website is now reachable via both [jfxcentral.com](http://jfxcentral.com) and [jfx-central.com](http://jfx-central.com).
* [Many improvements](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2/commits/develop) are going on...
* The post by **Frank Delporte** about the new version of JFX Central got republished on Foojay:
  * [Part 1: Description of the site and changes](https://foojay.io/today/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/).
  * [Part 2: Interviews with some of the team members](https://foojay.io/today/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/).
* New content added to JFX Central
  * Library: [FXComponents](https://www.jfx-central.com/libraries/fxcomponents)
  * Showcase: [M-AID](https://www.jfx-central.com/real_world/maid)
  * Showcase: [EasyCashier](https://www.jfx-central.com/showcases/easycashier), a POS (point of sale) system from Sweden.
* Is your library on JFX Central, or book, or website, or company, or ...? Let the people know [by adding a badge](https://twitter.com/dlemmermann/status/1699044022029811824)!
* The initial data load of JFX Central is [reduced by 50% thanks to JGit](https://twitter.com/dlemmermann/status/1699103833131978953?s=20).
* [A new feature got announced that allows you to export any icon font icon to an SVG path](https://twitter.com/dlemmermann/status/1706418682170097883) so that you can use it in CSS, too.
* [**Dirk Lemmermann** is inviting all JavaFX developers to make their work visible on JFX Central](https://twitter.com/dlemmermann/status/1700125468693868626): "In the end, you will have a nice personal profile page like this. A page showing your libs, your tools, your books, your videos, your blog, your showcase apps, etc..."
* More [content was added](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data/commits/live) and [bugs fixed and improvements integrated](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2/commits/master), go [check it out on jfx-central.com](https://www.jfx-central.com/)...
