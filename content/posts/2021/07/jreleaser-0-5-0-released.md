---
title: "Announcement: JReleaser 0.5.0 Released!"
slug: "jreleaser-0-5-0-released"
date: "2021-07-01T07:54:13+00:00"
lastmod: "2021-08-03T17:10:51+00:00"
description: "JReleaser is a tool that streamlines creating releases for [Java] projects. It can create a GitHub/GitLab/Gitea release, and more!"
authors:
  - "andres-almiray"
image: "https://foojay.io/wp-content/uploads/2021/07/jreleaser-duke.png"
categories:
  - "Release Notes"
tags:
related_posts:
  - "jreleaser-0-10-0-released"
  - "jreleaser-0-9-0-released"
  - "jreleaser-0-8-0-released"
  - "jreleaser-0-7-0-released"
frozen: false
---

JReleaser v0.5.0 has been released!

JReleaser is a tool that streamlines creating releases for \[Java\] projects. It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

In this release you'll find updates and features such as:

* Support for two additional distribution types: NATIVE_PACKAGE and BINARY. The first type enables artifacts of type `.dmg`, `.pkg`, `.deb`, `.rpm`, `.msi`, `.exe` to be used with packagers (currently Homebrew); the second type enables non-Java distributions as long as they provide an executable or launcher script.
* The Homebrew packager supports generating casks for `.dmg` and `.pkg` artifacts.
* Artifacts of distributions of type NATIVE_IMAGE must now be packaged as zip file; the nativeImage assembler has been updated to produce zip files.
* The Sdkman announcer can publish distributions of type JLINK and NATIVE_IMAGE.
* Speaking of announcers, there are two brand new announcers, the first is Google ChatBot, the second is for generic incoming webhooks.
* When updating a Git release you can now choose a combination of title, body, assets.

Full changelog can be found at the [v0.5.0 release](https://github.com/jreleaser/jreleaser/releases/tag/v0.5.0 "v0.5.0 release") page.

More information about the tool can be found at <https://jreleaser.org>.
