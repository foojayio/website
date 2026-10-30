---
title: "JavaFX Links of October 2026"
date: "2026-10-31"
description: "Here are the JavaFX LinksOfTheMonth of October 2026. You can find the weekly lists on jfx-central.com."
canonical: "https://webtechie.be/post/javafx-links-of-october-2026/"
authors:
  - "frankdelporte"
image: "jfxcentral.jpg"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-september-2026"
  - "javafx-links-of-august-2026"
  - "javafx-links-of-july-2026"
  - "javafx-links-of-june-2026"
---

Here are the JavaFX LinksOfTheMonth of October 2026. You can find the weekly lists on [jfx-central.com](https://www.jfx-central.com/links). Did we miss anything? Is there anything you want to have included in one of the next overviews? Let us know via [links@jfx-central.com](mailto:links@jfx-central.com).

## Core

* [**John Hendrikx**](https://www.linkedin.com/in/john-hendrikx-0baa8819/) integrated [JDK-8290310: ChangeListener events are incorrect](https://marc.info/?l=openjdk-openjfx-dev&m=179205301698593&w=2) into OpenJFX. The related review thread ([RFR: 8290310](https://marc.info/?l=openjdk-openjfx-dev&m=179207446523885&w=2)) ran to over 50 messages.
  * Remarkable comment by **mstr2** in the [pull request](https://github.com/openjdk/jfx/pull/1081): "_Through every release and across time, this PR has always stood firm. Like a solid rock in a sea of change. Contributors came and went. JDKs grew old. But this PR remained. At some point, we stopped thinking of it as pending work, and started seeing it as a precious landmark. And now, an era has come to an end. We had prepared for another review round, but we had not prepared for closure._"
* **Johan Vos** [announced](https://mastodon.social/@johanvos/117474447449778114) the CPU releases JavaFX 17.0.21, 21.0.13, 25.0.5 and 27.0.1. All builds are available for free, with [commercial support available from Gluon](https://gluonhq.com/products/javafx). "_This release is packed with security fixes that I had to inspect line by line. To me, it was the hardest one ever, but I'm happy with the result._"
* From the OpenJFX Dev mailing list:
  * [**OpenJFX team** integrated RichTextArea image import support](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30549.html): JDK-8388276 lands the ability to import images into the `RichTextArea` control.
  * [**OpenJFX team** fixed stale TableView cells after refresh()](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30556.html): JDK-8394617 resolves a bug where `TableView.refresh()` left stale cells in the VirtualFlow pile, showing old content when scrolled back into view.
  * [OpenJFX integrates D3D9 lighting cache optimization (JDK-8392736)](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30595.html): A change was integrated into the OpenJFX mainline that caches lights instead of reuploading them to the GPU on every call, improving Direct3D9 rendering performance.
  * [JavaFX on Wayland — A Working Proof of Concept](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30638.html) was shared on the openjfx-dev mailing list: a community contributor presented a working proof of concept demonstrating native Wayland support for JavaFX, a long-requested capability for Linux desktops.
  * [Integrated: 8384258: Memory Leak in ListenerHelper](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30688.html): A fix for a memory leak in JavaFX's internal `ListenerHelper` has been integrated into OpenJFX.
  * [Integrated: 8264588: Double clicking a word in TextArea selects wrong word](https://www.mail-archive.com/openjfx-dev@openjdk.org/msg30689.html): A fix for incorrect word selection when double-clicking inside a `TextArea` has been integrated into OpenJFX.

## Applications

* [**Max Xiong** shared a new screenshot](https://bsky.app/profile/maxiong.bsky.social/post/3mwrwlzd4yk2d) of DataCollie, his ongoing #BuildInPublic project, noting that "_PostgreSQL's privilege management is much more complex than MySQL's. Still, I'm making a little progress every day._"
* **Mike** built [Klunk](https://www.kookaklunka.com/klunk/), a JavaFX log viewer for making large logs easier to navigate and search. It merges logs from files, databases, Kubernetes pods, SFTP and HTTP into one color-coded stream, with SQL-like filtering, bookmarks, real-time tailing and SQLite-backed handling of millions of rows. Free to use, with bundled runtime for Windows, Linux and (untested) macOS.
* Following up on last week's [internal JavaFX tool for automated test recording of temporary power equipment](https://bsky.app/profile/afparks.bsky.social/post/3mxcv2dkork2h), **@afparks** shared [two new videos](https://bsky.app/profile/afparks.bsky.social/post/3mxp74qb5422m). The latest shows the new users and roles pages, with a permissions table that has sticky headers staying in place while scrolling, and an overlapping scroll bar that floats over the rows instead of taking up space on the right.
* **Frank Delporte** [released MelodyMatrix 2.0.0](https://webtechie.be/post/melodymatrix-2.0.0-learn-section-fly-over-3d-sheet-music-and-audio-to-midi/) with a new Learn section with lesson cards tracking level, duration and progress, a Fly-Over view showing the sheet music in 3D with notes highlighted during playback, and a Convert feature that turns mp3, wav and other audio files into MIDI locally.
* **AsharPeshimam** [published a Java PDF app on the Microsoft Store](https://www.reddit.com/r/JavaFX/comments/1xahf70/i_just_published_my_java_pdf_app_on_the_microsoft/), shared the story on r/JavaFX, and asks for feedback.
* [Klunk, a JavaFX log viewing application](https://www.reddit.com/r/JavaFX/comments/1x9emky/i_made_klunk_a_javafx_log_viewing_application/) was shared on r/JavaFX by **XenFive**. "_It’s currently beta, but I use it daily without issues and figured others might find it useful too. Happy to hear feedback, bug reports, or feature ideas._"
* [**Max Xiong** is preparing to launch DataCollie](https://bsky.app/profile/maxiong.bsky.social/post/3myumzn6upk2e), his database client built with JavaFX, after a long #BuildInPublic journey: "_Doing some final prep before launching DataCollie. So many edge cases to handle!_"

## Games

* **Anahata ASI** shared a video, [Pure JavaFX Lemmings Cyber-Arcade Edition | 60 FPS Recreation (Level 1: Just Dig!)](https://www.youtube.com/watch?v=1jmHhGQDs9o), a pure JavaFX recreation of the classic Lemmings game running at 60 FPS.
* [**Artistic_Solution117** shared on Reddit](https://www.reddit.com/r/JavaFX/comments/1wv9wzc/pure_javafx_minecraft_running_on_netbeans_31s_jvm/) a "pure JavaFX" Minecraft demo running on NetBeans 31's JVM, with bytecode dynamically generated by Gemini 3.8 Flash: 9 Java classes (27K tokens), with no `.java` or `.class` file written to disk — in-memory JIT compilation and classloading. More JavaFX demos on the [YouTube channel](https://www.youtube.com/watch?v=76pWS0WFu_U).
* [**Hallvard Trætteberg** shared RiskFX](https://bsky.app/profile/hallvard.bsky.social/post/3myk3zjzuhjlq), an implementation of the Risk world domination game in JavaFX, with a workflow for making an Android version. It includes an AI opponent with a rig for reinforcement learning. "_The core is old code written by me, but the rest has Claude helped me implement._" Sources are [on GitHub](https://github.com/hallvard/riskfx).

## Components, Libraries, Tools

* **Frank Delporte** released version 1.2.6 of Lottie4J, the library to parse, generate & play Lottie animations natively in JavaFX. "_It's a minor update which reduces the logging when animations are started and stopped. Thanks to **David Gerber** for the merge request._" All info on the [Lottie4J website](https://lottie4j.com/releases/).
* **Frank Delporte** announced a new version of [Lottie4J](https://lottie4j.com/), the Java and JavaFX library to render Lottie animations. "_Release 1.2.7 doesn’t bring new features, but focuses fully on performance. Improvements: image assets are decoded once, and optimized track mattes._" Read more [on this blog post](https://webtechie.be/post/lottie4j-1.2.7-a-performance-release/).
* [**Mohd Dilshad Khan** shared a tip on switching themes while a Java app is running](https://lnkd.in/p/ejR5-GFK), without a restart or losing state. In JavaFX it comes down to replacing the stylesheet on the scene with `scene.getStylesheets().setAll(...)`, so themes live in CSS files a designer can tweak. For Swing, FlatLaf offers the same with `FlatDarkLaf.setup()` and `FlatLaf.updateUI()`. You can find his [project on GitHub](https://github.com/mohddilshadkhan2003/theme-hot-swap).
* **Frank Delporte** announced version [0.0.4](https://sheetmusic4j.com/status/2026/20261016-release-0.0.4/) and [0.0.5](https://sheetmusic4j.com/status/2026/20261021-release-0.0.5/) of Sheetmusic4J, the library to parse and render sheet music natively in Java/JavaFX. These releases include new Node-based and 3D fly-over views, and add support for the LilyPond format: "_load such a file and render it natively in JavaFX, without running LilyPond._" It also brings engraving improvements like better tuplet placement and octave clef support. A video with a demo of the new views is [available on YouTube](https://www.youtube.com/watch?v=8n7WONyX0yk). 
* [**Frank Delporte** released Sheetmusic4J 0.0.5 with LilyPond import/export](https://sheetmusic4j.com/): Adds LilyPond (.ly) import/export through the unified ScoreFile facade via a parser/interpreter/assembler pipeline, plus engraving improvements such as better tuplet placement and octave clef support — building on the earlier 0.0.4 release, which added the clickable/draggable NodeSheetView and 3D FlyOverSheetView.
* **Libán Bande González** [announced Vitra](https://foojay.io/today/announcing-vitra-modern-ui-library-for-javafx/), a modern UI library for JavaFX with Gantt, Kanban, calendar, charts, Fluent-inspired materials, glass surfaces and custom windows. All controls support CSS and runtime theme switching, and there are no external dependencies. More info is [on the website](https://skinning-javafx-applications.netlify.app).

## Podcasts, Videos, Books

* **Josh Long** features **Andres Almiray** in [a Spring Office Hours podcast episode](https://ms.podbean.com/media/share/pb-uv35s-1b73a7f). A wide-ranging, high-energy conversation about the future of desktop Java, the pain of Electron, the elegance of JavaFX, and JReleaser — the tool that helps Java projects publish releases, package native installers, generate distribution bundles, compute signatures and checksums, and automate releases across GitHub, GitLab, Maven Central, and more.
* [**Libán Bande González** announced his book "Skinning JavaFX Applications"](https://foojay.io/today/announcing-skinning-javafx-applications/), "_a practical, code-first book built from years of shipping real desktop business applications,_" focused on the Control-Skin architecture for building custom controls and skins. It covers custom window chrome, runtime theme switching, modern design materials like Mica and Acrylic from Fluent Design, and micro-interactions, arguing that "CSS is not enough" to make JavaFX apps look like modern Microsoft or JetBrains interfaces. More info on the [book's website](https://skinning-javafx-applications.netlify.app).
* [**Adam Bien** talks with guest **Ingo Kegel** on airhacks.fm #419: "Agents Have No Runtime Insight"](https://airhacks.fm/#episode_419): Discusses giving AI coding agents JVM runtime insight through a JProfiler MCP server, including how an agent loop was used to double jclasslib's performance by outperforming ASM's bytecode parser, alongside memory-profiling fundamentals like dominator trees. They also touch on a possible Swing and JavaFX revival, because both ship with Java and LLMs know them well.

## Tutorials

* **Sombriks** wrote [The state of java desktop](https://sombriks.com.br/blog/0097-state-of-java-desktop/), comparing Swing, JavaFX, SWT, and TamboUI through a small todo app example, noting jpackage has made distributing Java desktop apps practical, and describing JavaFX as "the really modern one" with native property bindings, cleaner event handling, and CSS-styled scene graph, best suited "for really big projects".
* [**Libán Bande González** wrote "Building a JavaFX Code Editor Control"](https://foojay.io/today/building-a-javafx-code-editor-control/), showing how to build a code editor with the Control/Skin architecture: virtualized line numbers, regex-based syntax highlighting, bracket matching, multi-cursor editing, undo/redo, and autocomplete. The code and gutter are two synchronized `ListView`s so line numbers stay fixed during horizontal scrolling. The full implementation is part of his book "Skinning JavaFX Applications".
* [**sosuisen** published "JavaFX Getting Started with VS Code"](https://sosuisen.net/posts/javafx-on-vscode/) on his renewed personal website. The article was originally written for JFX Central and was rewritten for Japanese readers and his students. The page has a toggle between Japanese and English. [Announcement on Bluesky](https://bsky.app/profile/sosuisen.bsky.social/post/3myom5czros2q).

## Miscellaneous

* [**Old_Assistance7** asked on Reddit](https://www.reddit.com/r/JavaFX/comments/1wpucad/best_way_to_store_session_tokens_securely_in/) about the best way to securely store session tokens in a JavaFX app using native OS credential stores (Windows DPAPI/Credential Manager, macOS Keychain, Linux Secret Service). Commenters suggested wrapping the native stores behind a "CredentialStore" abstraction, recommended libraries like [java-keyring](https://github.com/javakeyring/java-keyring), and pointed to the Foreign Function & Memory API ([JEP 454](https://openjdk.org/jeps/454)) as a modern alternative to JNA for calling into native code.
* [**@afparks** is building an internal JavaFX tool for automated test recording of temporary power equipment](https://bsky.app/profile/afparks.bsky.social/post/3mxcv2dkork2h), with a video of the login screen. The sign-in screens use the new `StageStyle.EXTENDED` for seamless window controls and [Lottie4J](https://lottie4j.com/) for the animation playback.
* [**Ivan Velikanov** shared how he saved 3.5 GB of RAM by migrating chat apps and a terminal to JavaFX](https://www.reddit.com/r/JavaFX/comments/1xiw5x6/i_save_35_gb_of_ram_by_migrating_all_my_chat_apps/): On r/JavaFX a discussion about JavaFX versions of WhatsApp, Slack, VS Code and a few other browser-engine/Electron apps. JavaFX-based versions are much snappier, using the serial GC in most cases. The WhatsApp version is built on [whatsmeow](https://github.com/tulir/whatsmeow), and the Slack version and terminal are planned to be shared via [fxslack.github.io](https://fxslack.github.io), which was still empty at the time of posting.

## JFX Central

* The links of September got bundled in a [monthly overview on Foojay](https://foojay.io/today/javafx-links-of-september-2026/).
