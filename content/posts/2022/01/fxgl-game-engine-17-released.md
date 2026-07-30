---
title: "FXGL Game Engine 17 Released | Foojay.io Today"
slug: "fxgl-game-engine-17-released"
date: "2022-01-03T10:43:05+00:00"
lastmod: "2022-01-03T10:44:09+00:00"
description: "Game developers unite around Java and JavaFX! Version 17 of the FXGL game engine brings a number of improvements in many areas."
authors:
  - "almasbaimagambetov"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Game Development"
  - "JavaFX"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

[Version 17](https://github.com/AlmasB/FXGL/releases/tag/17) of the FXGL game engine brings a number of improvements in many areas. Key changes include:

* FXGL source is now built with Java 17 and JavaFX 17.
* The changelog is now automatically generated from commits.
* Animated textures (sprite sheet animations) are now driven by the unified FXGL system, meaning you can use interpolators, play in reverse and many other supported features.
* 3D skybox implementation.
* DialogueContext object for storing data that is local to the context in which the dialogue is being run.
* Improvements to avoid various crashes on the Raspberry Pi.

Auto-generated Changelog {#h2-0-auto-generated-changelog}
---------------------------------------------------------

The full changelog is given below.

### build {#h3-1-build}

* kotlin 1.5.10 -\> 1.5.32 [commit](https://github.com/AlmasB/FXGL/commit/a2b5daf2d)
* maven pmd plugin 3.13.0 -\> 3.15.0 [commit](https://github.com/AlmasB/FXGL/commit/db737a7ed)
* base Java 17, switch to 17+dev-SNAPSHOT [commit](https://github.com/AlmasB/FXGL/commit/e1acdecbb)
* javafx 16 -\> 17.0.0.1, closes #1107 [commit](https://github.com/AlmasB/FXGL/commit/87b3f0ade)

### docs {#h3-2-docs}

* added commit message guidelines for changelog generation [commit](https://github.com/AlmasB/FXGL/commit/0f7dee3b7)
* clarified release process [commit](https://github.com/AlmasB/FXGL/commit/e2b212a26)

### feat {#h3-3-feat}

* FXGL 17 intro [commit](https://github.com/AlmasB/FXGL/commit/1077d768b)
* added transferFrom() to Inventory [commit](https://github.com/AlmasB/FXGL/commit/17bc460e6)
* new API to set time to animation directly and to build sequential animations [commit](https://github.com/AlmasB/FXGL/commit/bdb000542)
* dialogue syntax check, show an error icon if there are incomplete paths, #651 [commit](https://github.com/AlmasB/FXGL/commit/13694971c)
* added showChoiceBox to DialogService, closes #1094 [commit](https://github.com/AlmasB/FXGL/commit/0d50a6397)
* added audioFileName support to nodes, #651 [commit](https://github.com/AlmasB/FXGL/commit/444c95108)
* bind debug camera to CTRL+8, to avoid issues with CTRL+C [commit](https://github.com/AlmasB/FXGL/commit/3783bc0d5)
* added DialogueContext, #1116 [commit](https://github.com/AlmasB/FXGL/commit/deec0ec96)
* added bulk dialogue editor actions [commit](https://github.com/AlmasB/FXGL/commit/fefdd6778)
* undo (ctrl+z) is complete for main editor actions, #651 [commit](https://github.com/AlmasB/FXGL/commit/eb41e261a)
* added TimeComponent::copy, #1041 [commit](https://github.com/AlmasB/FXGL/commit/abc84de2a)
* Added draft skybox implementation [commit](https://github.com/AlmasB/FXGL/commit/a3e5599eb)
* add Platform.EMBEDDED, #1079 [commit](https://github.com/AlmasB/FXGL/commit/daf4fba94)
* added public API to change render fill of FXGLPane, set default to White for consistency with native mode, #1085 [commit](https://github.com/AlmasB/FXGL/commit/a8598950d)
* added randomColorHSB() [commit](https://github.com/AlmasB/FXGL/commit/ddc489daf)
* added embeddedShutdown() that allows restarting an FXGL instance without the need to exit JavaFX, added sample, #1075 [commit](https://github.com/AlmasB/FXGL/commit/904def3f2)

### fix {#h3-4-fix}

* fixed non-uniform frame distribution for AnimationTexture, closes #1067 [commit](https://github.com/AlmasB/FXGL/commit/211e867de)
* fixed a bug that causes audio service to not load properly on embedded devices [commit](https://github.com/AlmasB/FXGL/commit/842b079b2)
* fixed a bug that would cause FS access via gluon attach on embedded devices [commit](https://github.com/AlmasB/FXGL/commit/2cb4736da)

### refactor {#h3-5-refactor}

* internal refactor Inventory [commit](https://github.com/AlmasB/FXGL/commit/2ea725dd0)
* clean up MDIWindow, closes #815 [commit](https://github.com/AlmasB/FXGL/commit/e46b34af3)
* remove redundant code, active is always false at that point, closes #1024 [commit](https://github.com/AlmasB/FXGL/commit/8ea6b755e)

### repo {#h3-6-repo}

* ignore auto-generated changelog files [commit](https://github.com/AlmasB/FXGL/commit/6316a0c9a)

Contribute {#h2-7-contribute}
-----------------------------

You can contribute to the development on [GitHub](https://github.com/AlmasB/FXGL/blob/dev/CONTRIBUTING.md).
