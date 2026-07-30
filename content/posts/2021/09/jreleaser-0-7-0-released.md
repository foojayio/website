---
title: "This Just In: JReleaser 0.7.0 Released! | Foojay.io Today"
slug: "jreleaser-0-7-0-released"
date: "2021-09-29T12:12:24+00:00"
lastmod: "2021-09-29T12:13:32+00:00"
description: "JReleaser is a tool that streamlines creating releases for [Java] projects. 0.7.0 is a small release as core features is mostly complete."
authors:
  - "gerrit-grunwald"
image: "/images/posts/2021/09/jreleaser-0-7-0-released/Favicon-3-2.png"
categories:
  - "DevOps"
  - "Release Notes"
tags:
related_posts:
  - "jreleaser-0-10-0-released"
  - "jreleaser-0-9-0-released"
  - "jreleaser-0-8-0-released"
  - "jreleaser-0-6-0-released"
frozen: false
---

JReleaser is a tool that streamlines creating releases for \[Java\] projects.

It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

This is a small release as core features are mostly complete:

* **JDKs:** The jdks-gradle-plugin now unpacks the chosen JDK distribution in a subdirectory that matches the configured named. This is a breaking change! Additionally, tasks for removing cached JDKs were also added.
* **GitHub:** Linking a release to a discussion is automatically skipped if the release is a draft or early access.
* **Changelog:** You can now specify if a full or partial changelog should be generated when the release is early access.
* **Homebrew:** Fixes automatic generation of Cask v.s Formula.
* **Release:** You may skip creating a release but keep the tag. This is useful for the case when you'd like to push release assets via an uploader but want to skip creating a Git release.

Full changelog can be found at the [v0.7.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.7.0) release page.

More information about the tool can be found at <https://jreleaser.org>.
