---
title: "JavaFX Links of December 2024"
slug: "javafx-links-of-december-2024"
date: "2024-12-30T08:06:43+00:00"
lastmod: "2024-12-30T10:27:45+00:00"
description: "Here is the last overview of the JavaFX LinksOfTheMonth for 2024. You can find the weekly lists on jfx-central.com. We hope you enjoyed all the previous - by Frank Delporte"
canonical: "https://webtechie.be/post/2024-12-20-javafx-links-of-december-2024/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-november-2024"
  - "javafx-links-of-october-2024"
  - "javafx-links-of-september-2024"
  - "javafx-links-of-june-2026"
frozen: false
---

Here is the last overview of the JavaFX LinksOfTheMonth for 2024. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). We hope you enjoyed all the previous editions and we promise to go on in the next year... Have a nice holiday and see you in 2025!

Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [\[email protected\]](/cdn-cgi/l/email-protection).

Core {#h2-0-core}
-----------------

* The OpenJFX project is [looking for UI designers and developers for a new modern theme](https://mail.openjdk.org/pipermail/openjfx-dev/2024-December/051222.html): "*Ideally, this is more than a "Modena 2025" refresh. I completely agree that this is a big project, and it requires talented people to pull it off. However, there are talented UI designers and developers in the JavaFX community. I hope this will start a discussion on how a modern JavaFX user experience can look like, and how we'd be able to achieve it.*"
* [**Johan Vos** shared](https://mastodon.social/@johanvos/113630372338659041): "*Progress. This is a screenshot from the XCode console, output from an iPhone. HelloWorld.java on iOS using hotspot (zero interpreter mode). Finally back to the point reached by the great **Bob Vandette** many years ago. Need to anchor this.*"

Applications {#h2-1-applications}
---------------------------------

* [**BJ Dela Cruz** on LinkedIn](https://www.linkedin.com/posts/bj-delacruz_javafx-java-programmingisfun-activity-7270631777281486849-tAoR/): "*My HEX Viewer application is nearly done! ⭐️ Users can edit a file at a byte or bit level, and the last screenshot shows the changes after they have been saved. Note that the MD5 and SHA256 hashes will be different once the file is saved.* " You can find the [sources on Bitbucket](https://bitbucket.org/bjpeterdelacruz/hex-viewer/src/main/).
* [**Patrik Karlström** released CRIC 24.12](https://bsky.app/profile/trixon.se/post/3lcqdymorck2x): "*Custom Runtime Image Creator, a gui for the jlink command. It produces custom runtime images, kind of Java Runtime Environments. Starting with this release, the snap supports classic confinement. Enjoy!* " You can find the [releases and source on GitHub](https://github.com/trixon/cric/releases/tag/v24.12).
* [**Alon Xiong** is sharing more Datacollie progress on Bluesky](https://bsky.app/profile/xiongchun.bsky.social): "*Today, I finished the function for executing Update \| Insert \| Delete statements in SQL Console of Datacollie. And now, I have to go out in the sun, I feel like I'm growing mold.*"
* [**Sean Phillips**](https://bsky.app/profile/seanmiphillips.bsky.social/post/3ldbhwnymi22l): "*New full [release for the Trinity XAI analysis tool](https://github.com/trinity-xai/Trinity/releases/tag/v2024.12.13) just in time for the holiday season 💙. Thanks again to [**Samypr100**](https://github.com/samypr100) for his build wizardry!*"
* [**Patrik Karlström**](https://bsky.app/profile/trixon.se/post/3ldbewsqm7s2f): "*Say hello to [nbRsync 24.12](https://github.com/trixon/nbRsync/releases/tag/v24.12)! 🎆 It's a GUI for rsync written in Java \& JavaFX atop the NetBeans Platform. nbRsync has a built-in scheduler that manages backups in a cron like fashion. AppImage for Linux is available too.*"

Games {#h2-2-games}
-------------------

* [**Mark J. Koch**](https://bsky.app/profile/markjkoch.bsky.social/post/3lcdxjbcazs24): "*Making some small progress on dialogs for my next point and click adventure called 'Chiba City Blues'. It's an unauthorized remake of Neuromancer Commodore/PC (1988/1989). Same game content, new engine and new artwork. 100% open source. So much to still do.*"

Components, Libraries, Tools {#h2-3-components-libraries-tools}
---------------------------------------------------------------

* Ongoing work by **Dirk Lemmermann** on GemsFX:
  * "*[Just released a new control inside GemsFX 2.69.0](https://bsky.app/profile/dlemmermann.bsky.social/post/3lcdka4kqyc2b) that will allow you to toggle between a wrapped node of your application (e.g. a list view) and a progress indicator (when loading / refreshing data). Quite handy. I use it all the time.*"
  * "*[Working on a new PagingListView control for GemsFX](https://bsky.app/profile/dlemmermann.bsky.social/post/3lcfr5iuv3k2q). It will ship with a feature-rich pagination control and background data loading. Abandoning the VirtualFlow will give you pixel perfect accuracy. The control wraps a standard ListView and standard ListCells.*"
  * "*[Getting a kick out of styling the PagingControls view in GemsFX](https://bsky.app/profile/dlemmermann.bsky.social/post/3lchyxgze322x). Reusing [CSS styles by **Jasper Potts**](http://fxexperience.com/2011/12/styling-fx-buttons-with-css/).*"
* [**Almas Baim** shares a video of an experiment with shaders](https://x.com/AlmasBaim/status/1869841144886243590) in pure Java (at runtime) in a 3D animation with FXGL.
  * [**WhiteWoodCity** hopes to see more](https://x.com/WhiteWoodCity/status/1869712571865113040): "*plez do it, we are all expecting this \& plez support vulkan. I made a [demo project for using shader in JavaFX](https://github.com/chengenzhao/java-vulkan-mac). it works for mac and should work for windows, since vulkan and ffm are both cross-platformed.*"
* **Pedro Duque Vieira** is wrapping up the for the next release of Transit Theme:
  * [PART 13](https://bsky.app/profile/p-duke.bsky.social/post/3lddzex7j7c2i): "*New LIGHT and DARK styles for ToolBar and DatePicker, including a "light" variation for ToolBar.*"
  * "*The release is getting close. Still missing but already partially implemented: ListView, TreeView, TableView and TableTreeView.*"

Podcasts, Videos, Books {#h2-4-podcasts-videos-books}
-----------------------------------------------------

* New JFXInAction interviews by **Frank Delporte** :
  * In episode 11 he talks with **Almas Baim** about FXGL, a library that helps us create JavaFX games. But it's much more than games! With the integrated Goal-Oriented Action Planning (GOAP) functionality, it can use game technology and AI to solve any goal, based on actions and preconditions. In the video, Almas [live codes such a solution with a minimal amount of code](https://webtechie.be/post/2024-12-05-jfxinaction-almas-baim/)!
  * In episode 12 you'll [learn about jDeploy, created by **Steve Hannah**](%5D(https://webtechie.be/post/2024-12-12-jfxinaction-steve-hannah-jdeploy/)): "*Building a JavaFX app is easy and fun, but how do you efficiently distribute it to different systems? jpackage and GraalVM can help, but jDeploy makes things even more effortless by handling all the packaging and providing an upgrading flow!*"
  * [**Jago de Vreede**](https://bsky.app/profile/jagovreede.bsky.social) is working on a user interface for SDKMAN that also brings its full functionality to Windows! He uses Java, JavaFX, GraalVM, SceneBuilder,... and more as you can learn from [episode 13 of the "JFX In Action" interviews](https://webtechie.be/post/2024-12-19-jfxinaction-jago-de-vreede-sdkman-ui/).
* The third edition of "*The [Definitive Guide to Modern Java Clients with JavaFX](https://www.amazon.com/Definitive-Guide-Modern-Clients-JavaFX/dp/B0DFP9PY1T): Cross-Platform Mobile and Cloud Development Updated for JavaFX 21 and 23* " is now available. 644 pages of JavaFX brought to you by **Stephen Chin** , **Johan Vos** , and **James Weaver**.
* The talk by **Frank Delporte** and his son **Vik** about the MelodyMatrix application, at the JFall conference in November, got published: "*[Looking at Music: an experiment with Kotlin, JavaFX, MIDI, and VirtualThreads](https://www.youtube.com/watch?v=bwc8Y3qnduo)*"

Tutorials {#h2-5-tutorials}
---------------------------

* **Muhammed Afsal Villan** created a [livestream tutorial on YouTube](https://www.youtube.com/live/mWXTf27RmWc): "*JavaFX with Spring AI. Building a real LLM-based AI Assistant from Scratch.* " The accompanying code [is available on GitHub](https://github.com/afsalashyana/javafx-ai-assistant).
* [**polypragmatist** shared](https://bsky.app/profile/polypragmatist.bsky.social/post/3lcxlt4izw226): "*I've just published what I believe to be the most comprehensive guide to styling the JavaFX TableView available. [This guide includes some tutorials](https://www.pragmaticcoding.ca/javafx/elements/styling-guide-tableView) plus a complete (I think) catalogue of TableView CSS selectors.*"
* [**Radhika Vyas** on index.dev](https://www.index.dev/blog/how-to-build-java-gui-application): "*The Best Way to Build a Java GUI Application: A Step-by-Step Guide*"

Miscellaneous {#h2-6-miscellaneous}
-----------------------------------

* [**Maciej Gorywoda** shared on Bluesky](https://bsky.app/profile/makingthematrix.github.io/post/3lc3bi4z2vk26): "*This is my BSky account if anyone was interested in talking about JavaFX on Android. In fact, I have a whole [repository of examples](https://github.com/makingthematrix/scalaonandroid)*."
* [Coding tip by **Johan Vos**](https://bsky.app/profile/johanvos.bsky.social/post/3lc3b5tmnwk2u): "*[VisualVM](https://visualvm.github.io/) can be a great help in performance tuning for JavaFX apps. The screenshot shows the single-threaded JavaFX AppThread spends ca 2/3 of its time in "userspace" (Platform.runLater) and about 30% time in rendering (QuantumToolkit$$...).*"
* **Frank Delporte** wrote a [post for JVM Advent 2024, the JVM Programming Advent Calendar](https://www.javaadvent.com/2024/12/coding-for-fun-an-experiment-with-virtual-threads-javafx-and-music.html): "*Coding for fun: An experiment with Virtual Threads, JavaFX, and Music! When a nerdy dad and 14-year-old music-playing son join forces and start experimenting with music and code, some nice things can happen. Did you ever present your music piece in a business dashboard with charts? Did you know that the FXGL game library can be used to generate a piano with fireworks? And can Virtual Threads play back MIDI events with just a few lines of code and thousands of threads?*"
* In an ongoing discussion on Bluesky, [**Josh Long** is asking for support](https://bsky.app/profile/starbuxman.joshlong.com/post/3lclwi4hj6s23): "*What is the recommended way to do GraalVM images w JavaFX?* "
  * And a similar question by [**Marcin Grzejszczak**](https://bsky.app/profile/toomuchcoding.com/post/3lckqbwylgs2w): "*Is there anybody out there who has managed to setup a JavaFx application so that a native image is being produced?*"
* [**Johan Vos** on Bluesky](https://bsky.app/profile/johanvos.bsky.social/post/3lckiwrvafs2n): "*Combining 2 of my gigs (JavaFX and quantum physics): I think about the pulse job (the job that renders the scenegraph) as the collapse of the wavefunction. Events happen before the collapse, and the relation between events and rendering is... complex.*"
* [**polypragmatist** is looking for better JavaFX documentation](https://bsky.app/profile/polypragmatist.bsky.social/post/3lc4foi7t4k2r): "*I'm writing a new article about styling TableView. I'm trying to aim for something that can be both a tutorial and a reference and I am amazed at the depth of styling available. I'm also appalled that NONE of this stuff is documented anywhere. I've been researching this for hours. I mean. Would you have imagined that you can style the dots that indicate column sort order? Would you have imagined that you can style them differently for ascending vs descending? Would you have imagined that they wouldn't have documented this????*"
* A tip by **Johan Vos** : "_I believe `MARKDOWN_HASH3bdab50a6aee2d902143dfd493d0a1a5MARKDOWN`*HASH* is a less-known but very powerful API in JavaFX. I use it to fix performance issues in apps using ListView, where the backingList is sometimes updated very often. Sending individual ListChange events to the control kills performance, so I sync once per pulse. Interestingly, the [JBS issue, JDK-8097917](https://bugs.openjdk.org/browse/JDK-8097917), for this was created by **Stuart Marks** who is my #1 reference for (amongst other things) Java List performance!"()
* [**René Gielen** created a small demo](https://bsky.app/profile/rgielen.bsky.social/post/3lcnbxv7wp222): "*Stripping Spring Boot + JavaFX to the core. Feedback appreciated!* " You can [find it on GitHub](https://github.com/rgielen/springboot-javafx-fxweaver-demo).
* A [pro tip by **Dirk Lemmermann**](https://bsky.app/profile/dlemmermann.bsky.social/post/3lcxfgznlbs2q): "*Make [ScenicView](https://www.jfx-central.com/tools/scenicview) an integral part of your application. Define a keyboard shortcut to bring it up whenever you want, e.g. CTRL+SHIFT+S -\> ScenicView.launch(scene). So helpful!* " Thanks to [**Jonathan Giles**](https://www.jfx-central.com/people/j.giles).
* An [article in the JVM Advent by **Cay Horstmann**](https://www.javaadvent.com/2024/12/java-in-the-small.html) that also mentions the JavaFX application JTaccuino: "*Java in the Small*".
* [**Heshan Kariyawasam** shared a video](https://www.linkedin.com/posts/heshanthenura_java-javafx-activity-7274047577895899137-Sf9v/): "*I created a fun project called Java Rabbit using JavaFX. It lets you draw on a canvas by entering simple commands. It's just for fun, but I'm planning to add more features in the future. 🎉* ". You can [find it on GitHub](https://github.com/heshanthenura/JavaRabbit).
* [**Carl Dea**](https://x.com/carldea/status/1868649928584745005): "*I wish someone could create [UIVerse](http://UIVerse.io) for JavaFX developers.😁 It was my wishful thinking for the JavaFX enthusiast in many of us.*"

JFX Central {#h2-7-jfx-central}
-------------------------------

* New content:
  * Tools: [jDeploy, distribute your JavaFX app as a native bundle](https://www.jfx-central.com/tools/jdeploy)
  * People: [Steve Hannah, creator of jDeploy](https://www.jfx-central.com/people/s.hannah)
  * Video: [JavaFX In Action with Almas Baim about FXGL](https://www.jfx-central.com/videos/RFSIBrP4mZM)
  * Video: [JavaFX In Action with Steve Hannah about jDeploy](https://www.jfx-central.com/videos/Lhmf9U0KYsg)
  * Video: [JavaFX In Action with Jago de Vreede about SDKman UI](https://www.jfx-central.com/videos/t7CROVJbYto)
* A summary of the JavaFX Links Of The Week of November got [published on Foojay.io](https://foojay.io/today/javafx-links-of-november-2024/).
