---
title: "JReleaser 0.10.0 Released! | Foojay.io Today"
slug: "jreleaser-0-10-0-released"
date: "2022-01-04T13:30:48+00:00"
lastmod: "2022-01-04T13:30:49+00:00"
description: "JReleaser v0.10.0 is the last push towards 1.0.0. We'll concentrate on bug fixing and polishing from now on."
authors:
  - "gerrit-grunwald"
image: "Favicon-3-2.png"
categories:
  - "DevOps"
  - "Release Notes"
tags:
related_posts:
  - "jreleaser-0-9-0-released"
  - "jreleaser-0-8-0-released"
  - "jreleaser-0-7-0-released"
  - "jreleaser-0-6-0-released"
frozen: false
---

JReleaser is a tool that streamlines creating releases for \[Java\] projects.

It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

JReleaser [v0.10.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.10.0 "v0.10.0") is the last push towards 1.0.0. We'll concentrate on bug fixing and polishing from now on. Be mindful of the [breaking changes](https://github.com/jreleaser/jreleaser/issues?q=is%3Aissue+label%3A%22BREAKING+CHANGE%22+milestone%3Av0.10.0+is%3Aclosed) introduced in this release.

There are plenty of improvements in several areas, a quick summary of the highlights follows:

* **JPackage.** The [jpackage](https://jreleaser.org/guide/latest/configuration/assemble/jpackage.html) tool can now be used to assemble distributions of type [NATIVE_PACKAGE](https://jreleaser.org/guide/latest/distributions/native-package.html). You have the choice to reuse an existing [jlink](https://jreleaser.org/guide/latest/configuration/assemble/jlink.html) assembler or supply a runtime image created by any other means.
* **Archive extensions.** Additional archive extensions are now supported, such as .tar.xz, .txz, tar.bz2, .tbz2. Additionally, both jlink and nativeImage assemblers now let you specify the archive format to use, with .zip as by default.
* **Platform replacements.** JReleaser defines a fixed set of platform values (such as osx-x86_64 or linux-aarch_64) which are used to identify platform specific artifacts. These values are often times added to artifact names. Sometimes you'd like to use a different value such as mac-arm64 or linux-amd64. You can use [platform replacements](https://jreleaser.org/guide/latest/configuration/platform.html) to achieve this goal.
* **Artifactory.** JFrog's [Artifactory](https://jreleaser.org/guide/latest/configuration/upload/artifactory.html) supports multiple types of repositories such as generic, rpm, deb, nuget, and more. Previously you could only configure a single generic repository, now you can configure multiple repositories of any type as needed.
* **Gofish.** The [GoFish](https://jreleaser.org/guide/latest/configuration/packagers/gofish.html) package manager joins the set of support packagers. You can use it with distributions of type BINARY, JAVA_BINARY, JLINK, and NATIVE_IMAGE.
* **Expanded Packager Support.** Chocolatey, Macports, and Spec can now be used with additional distribution types, bringing the total set to BINARY, JAVA_BINARY, JLINK, and NATIVE_IMAGE.
* **JsonSchema.** The CLI offers a new command that generates a [json-schema](https://jreleaser.org/guide/latest/tools/jreleaser-cli.html#_json_schema) file, useful for validating JSON, YAML, and TOML config files.

Full changelog can be found at the [v0.10.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.10.0) release page.

More information about the tool can be found at <https://jreleaser.org>.
