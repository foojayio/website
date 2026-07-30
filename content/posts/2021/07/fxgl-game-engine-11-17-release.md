---
title: "Announcement: FXGL Game Engine 11.17 Release"
slug: "fxgl-game-engine-11-17-release"
date: "2021-07-20T11:23:27+00:00"
lastmod: "2021-07-20T11:23:29+00:00"
description: "The FXGL game engine is now at 11.17. Most of the changes in this release focus on internal code quality and fixes."
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

The [FXGL game engine](https://github.com/AlmasB/FXGL) is now at 11.17. Most of the changes in this release focus on internal code quality and fixes.

Major additions to note:

1. Added download file API to NetService. Using this API developers can download files from any URL. For example, if certain assets are stored remotely.
2. Rotation and scale in 3D now support origin points (pivot points). This is a beneficial addition since some animations will need specific origin points for transformations to achieve the desired effect.

Other minor changes include:

* Added a setting that allows music to be paused when the game is minimized, paused by default
* Added Model3D sample showing how to load .obj models
* Added JointSample and support for RevoluteJoints from box2d
* Added fluent API to HearingSenseComponent, thanks to @jo372
* Updated physics collision sample, thanks to @jo372
* Ignore and warn during when adding a component during a component update, thanks to @lydianeU
* Added PropertyMapChangeListener
* Added replication support for PropertyMap
* Camera in 3D has a new setting allowing the pitch (rotationX) to be clamped between -90 and 90 deg
* Multiple superfluous modules merged into few

Bug fixes:

* AutoRotationComponent smooth now shouldn't make sharp turns

Version bump:

* jacoco 0.8.6 -\> 0.8.7
* kotlin 1.4.30 -\> 1.5.10
