---
title: "Announcement: JReleaser 0.6.0 Released!"
date: "2021-08-03T18:15:49+00:00"
lastmod: "2021-08-03T18:15:51+00:00"
description: "JReleaser is a tool that streamlines creating releases for [Java] projects. It can create a GitHub/GitLab/Gitea release, and much much more!"
authors:
  - "andres-almiray"
image: "Favicon-3-2.png"
categories:
  - "DevOps"
  - "Release Notes"
related_posts:
  - "jreleaser-0-10-0-released"
  - "jreleaser-0-9-0-released"
  - "jreleaser-0-8-0-released"
  - "jreleaser-0-7-0-released"
frozen: false
---

**JReleaser is a tool that streamlines creating releases for \[Java\] projects. It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.**

In this release you'll find updates and features such as:

* Several updates to the Homebrew packager, such as multi-platform (OSX \& Linux) support, ability to use the `binary` stanza with `.zip` artifacts, ability to use the `appcast` stanza with casks, ability to explicitly enable or disable generation of Casks.
* Sdkman has been split into packager and announcer, giving you more options to publish multiple candidates per project. The old behavior remains in place, that is, the announcer can publish and announce a single candidate for the whole project.
* Uploaders such as JFrog Artifactory and HTTP can now configure a downloadURL variable that can be used in templates.
* You can now target with pinpoint accuracy how artifacts, files, and globs should be uploaded, released, checksumed, and signed. These 4 sections offer configurable properties to activate or deactivate their targets. You also have the choice of adding extra properties to artifacts fur further tweaking the desired behavior.
* Partial model evaluation lets you execute a release for artifacts matching a particular platform. Useful when dealing with Native Image and Native Package distributions when not all artifacts are locally available. Model properties may also be overridden from the command line, user or project properties (Maven), or project properties (Gradle).
* Changelog formatting options now include the ability to hide specific commit categories (such as merge commits if you have configured such category) as well as contributors (such as bots). You can also format how contributors are displayed, for example linking to their Git profile with their username or name.
* The Docker packager now lets you publish packaged files (Dockerfile + supporting files) to a Git repository of your choice.
* The Article announcer lets you bootstrap a blog post or article based on templates.

Full changelog can be found at the [v0.6.0 release](https://github.com/jreleaser/jreleaser/releases/tag/v0.6.0 "v0.6.0 release") page.

More information about the tool can be found at <https://jreleaser.org>.
