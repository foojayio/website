---
title: "Welcome to the FXGL Game Engine 11.16 Release"
slug: "fxgl-game-engine-11-16-release"
date: "2021-05-25T12:10:16+00:00"
lastmod: "2021-05-25T12:10:18+00:00"
description: "Changelog and release notes. Learn More."
authors:
  - "almasbaimagambetov"
image: "Favicon-3-2.png"
categories:
  - "Game Development"
  - "JavaFX"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

FXGL is a JavaFX game engine: <https://github.com/AlmasB/FXGL>

Release Highlights
------------------

1. The restored Multiplayer Service allows easy replications of input, events and entities on a remote machine. For example, by just adding a single line of code (see MultiplayerService API), any key or mouse inputs that occur on one end of the connection can be replicated on the other end.
2. The new FPS camera allows easy control of the player (or their "line of sight") in the context of a first-person 3D game.
3. There is no longer a transitive dependency on the `javafx.swing` module. This means FXGL users will not need an extra module in their module graph.

Release Notes
-------------

* Restored and refactored MultiplayerService
* Added ReplicationEvent (javafx event that can be replicated on a remote machine)
* FPS camera (`camera3D.setFPSCamera(true)`)
* Mouse sensitivity setting
* Removed dependency on `javafx.swing` module, thanks to @FDelporte
* Performance improvements in Tiled map loading, thanks to @adambocco
* `isExperimental3D` -\> `3D`
* `isExperimentalNative` -\> `isNative`
* Model3D can be loaded from an .obj
* Model3D now has a copy()
* Added Cuboid shape
* EntityGroup::size(), thanks to @adambocco
* Allow providing a custom default cursor for all scenes
* Internal physics refactoring
* Entity no longer throws an exception when adding a duplicate Component (warns instead), thanks to @Zhack47
* JavaFX 15 -\> 16
* Compile target for Kotlin: 1.8 -\> 11

Video
-----

{{< youtube fuDQg7W0v4g >}}

<br />
