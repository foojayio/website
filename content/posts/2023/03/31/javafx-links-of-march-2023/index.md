---
title: "JavaFX Links of March 2023"
slug: "javafx-links-of-march-2023"
date: "2023-03-31T12:56:00+00:00"
lastmod: "2023-04-01T08:57:27+00:00"
description: "Already the end of March, so time to look back at the JavaFX Links Of The Week that were published on JFXCentral.com."
canonical: "https://webtechie.be/post/2023-03-31-javafx-links-of-march-2023/"
authors:
  - "frankdelporte"
image: "javafx-community.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "javafx-links-of-february-2023"
  - "javafx-links-of-january-2023"
  - "javafx-links-of-december"
  - "javafx-links-of-june-2026"
frozen: false
---

Already the end of March, so time to look back at the JavaFX Links Of The Week that were published on <https://www.jfx-central.com/>.

JavaFX Core {#h2-0-javafx-core}
-------------------------------

* [**Johan Vos** is working on backports for JavaFX 17.0.7](https://mastodon.social/@johanvos/110011897804267729) to guarantee quality and long-term support.
  * And he announced [JavaFX 20 could be released in the week of March 20th](https://mastodon.social/@johanvos/110033625841149774). As planned.
* Together with the [release of OpenJDK 20](https://foojay.io/today/its-java-20-release-day-heres-whats-new/), we also got the release of OpenJFX 20. Both right on schedule, as always!
  * [Announcement by **Gluon**](https://foojay.social/@gluonhq@techhub.social/110061453814740449).
  * Downloads are also provided via [OpenJDK on jdk.java.net](https://mastodon.social/@openjdk/110073623815076629).
  * Important message [in the release notes](https://github.com/openjdk/jfx/blob/jfx20/doc-files/release-notes-20.md): "JavaFX 20 is compiled with --release 17 and thus requires JDK 17 or later in order to run. If you attempt to run with an older JDK, the Java launcher will exit with an error message indicating that the javafx.base module cannot be read."
  * [Most important highlight for **Johan Vos**](https://twitter.com/johanvos/status/1638168304664694787): "apps created many years ago still run on the latest JavaFX with latest OS. That is far from trivial, require lots of work, and not very common in client frameworks."
  * And another [quote from **Johan**](https://mastodon.social/@johanvos/110061627191368710): "I remember people and companies telling me 5 years ago they love Java and JavaFX, but they were pretty sure JavaFX would not be around in 2 years from them. So they used other client technologies... which don't exist anymore today... while JavaFX... keeps moving forward. With less hype, and less marketing power than other client frameworks, but with dedication and focus on quality, stability and community. And with tons of stuff todo, I 100% realize that."
* You can already start experimenting with JavaFX 21 Early-Access Builds!
  * Get it from [Gluon](https://gluonhq.com/products/javafx/).
  * Or [OpenJDK](https://mastodon.social/@openjdk/110078557118569030).
* A [request by **Raumzeitfalle**](https://twitter.com/Raumzeitfalle/status/1638993956796239873): "If you like Java and JavaFX, give Scene Builder Leadinge Edge a try. Its latest version runs with Java 20 using JavaFX 20 and it combines many of the pending PRs so that one can test the functionality. Feel free to share your feedback on Github."

JavaFX and Game Development {#h2-1-javafx-and-game-development}
---------------------------------------------------------------

* [17.0.13 of JArkanoid by **Gerrit Grunwald**](https://twitter.com/hansolo_/status/1629506318389198850) adds the ability to shoot enemies.
  * He describes in a [Foojay post how to turn an existing application to an iPhone app](https://foojay.io/today/porting-an-existing-javafx-app-to-ios/).
  * And he started with a new game, [as you can see in this first screenshot of loderunner](https://twitter.com/hansolo_/status/1629224660259811332).
  * [He shared the first version of a Tetris clone](https://twitter.com/hansolo_/status/1635543158153965569).
  * This is [a screenshot](https://twitter.com/hansolo_/status/1634889764565585921).
  * You can play it online, [thanks to **WebFX**](https://twitter.com/WebFXProject/status/1635959116127404032).
* [**Markus Eisele** shared a link to the GitHub project of pacman-javafx](https://mastodon.online/@myfear/109947358052519976): a JavaFX UI (2D+3D) for Pac-Man / Ms. Pac-Man, a project by **Armin Reichert**.
* Another Minecraft-like world, in the [self-made 3D engine of **OrangoMango**](https://twitter.com/orango_mango/status/1631715021200687104).
  * [It got extended with more Minecraft-like functions](https://twitter.com/orango_mango/status/1636401197039984640): block breaking/placing, terrain generation, chunk system, and overall performance improvements.
  * [He added some new block types and fixed some issues with block breaking/placing in his Minecraft-like world](https://twitter.com/orango_mango/status/1636834800127586310), and now has a small home made out of wood and coal blocks.
  * It also has [Minecraft-like trees](https://twitter.com/orango_mango/status/1639660431160532993).
  * [**Sean Phillips** - being a JavaFX 3D expert - finds his work awesome](https://twitter.com/SeanMiPhillips/status/1638332315163082753).
* [**WhiteWoodCity** shared a video with the combination of 2D and 3D](https://twitter.com/WhiteWoodCity/status/1630151049486159874) game sub scenes.
  * [Another video of a rougelike game prototype](https://twitter.com/WhiteWoodCity/status/1638528428440944640).
  * And shared how he created a [pseudo 3D effect like Street of Rage, Dragon's Crown](https://twitter.com/WhiteWoodCity/status/1636904813228331010) with two GameSubScene and two entities of each GameWorld, and binding their properties with very clean and neat code.
  * His game got [bumped to Graal and Java 20](https://twitter.com/WhiteWoodCity/status/1635963164096598016). Yes, indeed, already before the official release of Java 20!
  * And shared how he created a [dynamic shadow effect of a jump action with a Shape and Binding](https://twitter.com/WhiteWoodCity/status/1635143145456553984) in less than 40 lines of code.
* **Almas Baim** has been very active on this month...
  * [He discovered another FXGL game](https://twitter.com/AlmasBaim/status/1635417926823198720): "The Last Cowboy Game - UTFPR".
  * And he shared a [screenshot showing FXGL is available in IntelliJIDEA](https://twitter.com/AlmasBaim/status/1634541388128821249) when creating a new JavaFX project.
  * [Announcing version 17.3 of FXGL](https://twitter.com/AlmasBaim/status/1641412978992902144): improve A\* performance, isometric support for .tmx, 3D updates (lookAt, direction, rotation, .obj models), propertyMap convenience API.
  * [Pathfinding solution added to FXGL that outperforms the existing one by up to 80%](https://twitter.com/AlmasBaim/status/1639608367516729349), by replacing ArrayList with HashSet in critical code.
  * [Another video with the FXGL engine pathfinding in action](https://twitter.com/AlmasBaim/status/1639349553496293377). This demo shows large red areas that are not passable, while dynamic entities are ignored and can be passed through.
  * [Video showing physics sandboxes are never not fun](https://twitter.com/AlmasBaim/status/1639017161455960064)! You can now pick up any entity as seen in this sample.
  * [Video of text animations with particles](https://twitter.com/AlmasBaim/status/1639804091609088002).
  * [Video with particles, one as "lead"](https://twitter.com/AlmasBaim/status/1640102122350944257), and other particles following with min and max distance, ensuring all particles are connected while the lead moves.
  * [Isometric tile support in FXGL has had significant interest over the years](https://twitter.com/AlmasBaim/status/1640474477258760192). The maths looks straightforward but the architecture, as per usual, will need some careful pondering.
  * [Screenshot of initial progress towards having first-class support for isometric levels](https://twitter.com/AlmasBaim/status/1640802453288353792) in FXGL.
  * [Video showing the new sliders in FXGL](https://twitter.com/AlmasBaim/status/1641582312730030082).
  * [He is throwing grenades...](https://twitter.com/AlmasBaim/status/1637915086571606016).

JavaFX in Science {#h2-2-javafx-in-science}
-------------------------------------------

* [**Fabrice Jossinet** shared an impressive preview video](https://twitter.com/rnartist_app/status/1629862954127568897) of a new tool to visualize the folding pathways of an RNA during its transcription. Pathways are computed with a Rust algorithm. Visualization and GUI are made with Kotlin, JavaFX and his rnartistcore library.
* [**RNArtist** shared again an impressive video](https://twitter.com/rnartist_app/status/1637923762438918150) showing RNA visualization with JavaFX.
  * ["In RNAStudio, you can animate the transcription process](https://twitter.com/rnartist_app/status/1637912428276072448) along one of the computed folding pathways. When a new helix pops, it is first highlighted then added to the 2D. You can stop/restart the animation, go backwards/forwards."

Miscellanous {#h2-3-miscellanous}
---------------------------------

* [jdeploy by **Steve Hannah** now produces signed apps for Windows installers](https://twitter.com/shannah78/status/1629515948637188096). This should make Windows Defender more pleasant to be around.
* **Dirk Lemmermann** has also been very active...
  * [TableView replacement based on GridPane. No virtualisation](https://twitter.com/dlemmermann/status/1640703431638753283). Very useful for small datasets. Will be added to GemsFX very soon.
  * [Screenshot of an answer on GitHub explaining there is only one codebase](https://twitter.com/dlemmermann/status/1640636273239916547) for jfxcentral website and desktop app, thanks to [@jpro_one](https://twitter.com/jpro_one).
  * [The TimePicker in GemsFX has been improved by @cpreisler](https://twitter.com/dlemmermann/status/1639262929752129544). It can now also display and edit seconds and milliseconds.
  * [He's looking back at something he implemented almost 20 years ago with Java 5 and Swing](https://twitter.com/dlemmermann/status/1641025184248348672), and it still works perfectly nowadays with Java 19 ... just a whole lot faster!
  * [He showed a custom tooltip implementation for charts](https://twitter.com/dlemmermann/status/1631259318195462151). The tooltips show the values of all y-values for the same x-value whenever the mouse cursor hovers over one of the data points.
  * In a [comment tweet, he shows code](https://twitter.com/dlemmermann/status/1631331619498860547) to illustrate that each data point in a JavaFX chart is also a node that allows us to integrate such a feature.
  * Read the comments to learn pro/contra of this approach and alternatives for large datasets.
  * [He added a YearMonthPicker control to GemsFX](https://twitter.com/dlemmermann/status/1635263143155757057). It's included in version 1.67.0.
  * It was the result of a [deep-dive into how to customize a ComboBox](https://twitter.com/dlemmermann/status/1634576422629961728).
* [**Fahim Bin Amin** shows in a 1,5h video](https://twitter.com/Fahim_FBA/status/1631352990719426561) how to create your own full-fledged project from scratch using JavaFX and Maven.
* [**Robert Ladstätter** shared exciting news](https://twitter.com/logorrr/status/1630942843853258753). LogoRRR is now officially available on the Apple Store!
* [**工房奥谷** shared a video showing a 3D-CAD application](https://twitter.com/tomosan119/status/1630562520229646337) to fit clothing patterns.
* [**Juanan** shared an example to learn how to use a REST API in Java](https://mastodon.social/@juananpe/109989844101182399) with a JavaFX Pokemon viewer. He added links to multiple videos demonstrating the code. Sources are available [on GitHub](https://github.com/juananpe/pokemon-viewer).
* [**JavaFX 3D** is asking who wants to contribute an importer for FBX or USDZ or GLB / glTF](https://twitter.com/JavaFX3D/status/1632612728664911872) to FXyz. It already has 3D model importers for OBJ and Maya.
  * He already found a [starting point in an existing java project](https://twitter.com/JavaFX3D/status/1632613330920800256).
* [**Jakob Jenkov** and **Andres Almiray** are talking about packaging a JavaFX app with JReleaser](https://twitter.com/jjenkov/status/1632112292597751809).
* [**MhamadHarmush** shared Java and JavaFX tutorials in Arabic](https://twitter.com/MhamadHarmush/status/1633840863435714562).
* [**WhiteWoodCity** shared a screenshots of NotificationFX](https://twitter.com/WhiteWoodCity/status/1635661530867060736).
* [**JavaFX3D** shared an article about 3DViewer by **ChrisNahr**](https://twitter.com/JavaFX3D/status/1635821978430103552)
* JavaFX at DevNexus in Atlanta (4-6 April):
  * [**Gail Anderson** and **Paul Anderson**](https://twitter.com/devnexus/status/1636031079973830657): "Modern Java with JavaFX for Rich Client UIs".
  * [**Sean Phillips**](https://twitter.com/SeanMiPhillips/status/1636040045206405121): "Harnessing the Hyper-dimensional Mind: Visualizing Brain-Computer Interfaces".
* [**Juanan Pereira** published a new video for his Software Engineering class](https://mastodon.social/@juananpe/110022581081582129): How to display custom items in JavaFX ListView
* [**Andres Almiray** updated the JavaFX plasma application to reflect changes brought by JReleaser 1.5.1](https://twitter.com/aalmiray/status/1634931119828393986), along with instructions for building.
* [**Robert Ladstätter** wrote a blog post about LogoRRR's journey to the Apple App Store](https://twitter.com/logorrr/status/1634213339470393345) using JPackage.
* [**Matt Coley** has a long Twitter thread about new the Recaf UI JavaFX work](https://twitter.com/invokecoley/status/1637693418192228352), starting with a 'please wait while the decompiler runs' animation that pulls hex dumps from the class being decompiled, and many more each with a video.
* [**Steve Hannah** shared a GitHub action](https://twitter.com/shannah78/status/1605922584310452225) to generate native installers for Java desktop apps.
* [SmartFinder by **Serendipity** version 1.7.3 now runs with JavaFX 20](https://twitter.com/SerendigityInfo/status/1639293098227388416)! It's a Desktop Search Tool APP fully developed with Java and JavaFX technology.
* **Dirk Lemmermann** again shared a lot of JavaFX library and other info:
  * "If any of you work on [planning and scheduling applications I can highly recommend #FlexGanttFX for visualizing plans / schedules](https://twitter.com/dlemmermann/status/1638910874261000192). Yes, it is a commercial library."
  * "If your JavaFX application requires an [on-screen keyboard, you might wanna check out KeyboardFX](https://twitter.com/dlemmermann/status/1638903750726418432)."
  * A screenshot of the new market data portal he is creating, [running in a browser](https://twitter.com/dlemmermann/status/1638854795074338816).
  * The new [calendar view in GemsFX now also supports "date range" selection](https://twitter.com/dlemmermann/status/1638103850107260930) (and single date, multiple dates selection).
  * Thanks to [**Florian Kirmaier**, GemsFX has been extended with a great utility class for synchronous scrolling of two VirtualFlow instances](https://twitter.com/dlemmermann/status/1638107356188798977).
  * And he's worried about his job as ["ChatGPT "generates" a JavaFX app based on requirements"](https://twitter.com/dlemmermann/status/1638137320846106625). That's probably thanks to the stability of the API over the last years!
