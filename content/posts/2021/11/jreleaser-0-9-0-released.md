---
title: "JReleaser 0.9.0 Released! | Foojay.io Today"
slug: "jreleaser-0-9-0-released"
date: "2021-11-30T10:29:17+00:00"
lastmod: "2021-11-30T10:30:24+00:00"
description: "JReleaser streamlines releases, creating GitHub/GitLab/Gitea releases and packaging binaries for Homebrew, Snapcraft, Docker, and more!"
authors:
  - "gerrit-grunwald"
image: "/images/posts/2021/11/jreleaser-0-9-0-released/Favicon-3-2.png"
categories:
  - "DevOps"
  - "Release Notes"
tags:
related_posts:
  - "jreleaser-0-8-0-released"
  - "jreleaser-0-7-0-released"
  - "jreleaser-0-6-0-released"
  - "jreleaser-0-10-0-released"
frozen: false
---

JReleaser is a tool that streamlines creating releases for \[Java\] projects. It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

JReleaser [v0.9.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.9.0 "v0.9.0") has just been released! JReleaser v0.9.0 is a big update as we gear towards v1.0.0. Plenty of improvements were added to this release to make it the best one so far. Be mindful of the [breaking changes](https://github.com/jreleaser/jreleaser/issues?q=is%3Aclose+is%3Aissue+label%3A%22BREAKING+CHANGE%22+milestone%3Av0.9.0 "breaking") introduced in this release.There are plenty of improvements in several areas, a quick summary of the highlights follows:

* **Jlink.** We've made several improvements to the [Jlink](https://jreleaser.org/guide/latest/configuration/assemble/jlink.html "Jlink") assembler. In particular it's now possible to create a Jlink image for Quarkus applications using their fast JAR layout. You'll find an example at the project's documentation. You can find a fully working example based on [Quarkus](https://quarkus.io/ "Quarkus") at <https://github.com/aalmiray/q-cli-jlink>.
* **JDKs.** You can now provision [JDKs](https://jreleaser.org/guide/latest/tools/jdks-maven.html "JDKs") using the [Foojay Discovery API](https://github.com/foojayio/discoapi "Foojay"). This feature lets you define JDK requirements that the Disco API will resolve into downloadable packages. You may continue to use the explicit setup as well.
* **Docker.** There's an additional template that lets you create [Docker](https://jreleaser.org/guide/latest/configuration/packagers/docker.html "Docker") images by downloading artifacts from their published URL. You may continue to build Docker images using local artifacts as well. Publication of Docker sources to a Git repository used to place each version in its own sub directory, this is now an optional feature and is disabled by default.
* **Gradle.** Plenty of updates made to the [Gradle plugin](https://jreleaser.org/guide/latest/tools/jreleaser-gradle.html "Gradle") to ensure it's on par with its Maven counterpart. In particular`brew.tap` has been renamed to`brew.repoTap`. The default outputs of the`distZip` and`distTar` task are no longer automatically added as a distribution, you must explicitly register the artifacts you need.
* **FileSets.** [FileSets](https://jreleaser.org/guide/latest/artifacts.html "FileSets") are similar to Globs in the sense that you can define a pattern to collect files to be added to an assembler. Unlike Globs, FileSets retain the relative directory structure. FileSet now accept extraProperties like Globs and Artifacts, also their properties accept named templates. All assemblers support fileSets.
* **MacPorts.** [MacPorts](https://www.macports.org/ "MacPorts") joins the list of supported platform [packagers](https://jreleaser.org/guide/latest/configuration/packagers/macports.html "packagers"). For the time being only JAVA_BINARY and NATIVE_IMAGE distributions are supported.
* **Engine.** Refinements to inclusion/exclusion of assemblers, packagers, uploaders, and announcers were added to the CLI, Ant tasks, Maven, and Gradle plugins. You may now include or exclude multiple items, giving you finer control.
* **Chocolatey.** You can now locally package and publish [Chocolatey](https://jreleaser.org/guide/latest/configuration/packagers/chocolatey.html "Chocolatey") packages if you're running on Windows. PowerShell is required.
* **Internationalization.** We now have full translations for core in English, Russian, and Catalan. German is in the works. We also have new Traditional Chinese and Russian translations for the CLI.

Full changelog can be found at the [v0.9.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.9.0) release page.

More information about the tool can be found at <https://jreleaser.org>.
