---
title: "Announcement: JReleaser 0.8.0 Released! | Foojay.io Today"
slug: "jreleaser-0-8-0-released"
date: "2021-11-02T12:12:29+00:00"
lastmod: "2021-11-02T12:14:10+00:00"
description: "JReleaser streamlines creating releases for [Java] projects, creates GitHub/GitLab/Gitea releases, while packaging binaries for Homebrew, etc."
authors:
  - "gerrit-grunwald"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "DevOps"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

JReleaser is a tool that streamlines creating releases for \[Java\] projects.

It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

This release closes all required items in the v1.0.0 roadmap, opening the door for v1.0.0 to be released next after polish and housekeeping items are taken care of.

Features added in this release include:

* **Internationalization.** All modules have been retrofitted with internationalization support. In particular the CLI tool has been translated to English, German, French, Italian, Brazilian Portuguese, Dutch, Spanish, Catalan, Hindi, and Japanese.
* **AWS S3.** You can now upload release artifacts to AWS S3 buckets.
* **Changelog.** Changelog generation supports the notion of presets, which are preconfigured settings that control how the changelog may be formatted. Currently supported are Gitmoji and Conventional Commits.
* **Archive.** You may create zip/tar distributions with the brand new Archive assembler instead of using external tools.
* **Telegram.** Telegram joins the list of supported announcers.
* **Signing.** File signing can also be done by specifying an external command, allowing you to reuse existing configuration instead of exporting public/secrets keys.

The full changelog can be found at the [v0.8.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.8.0) release page.

More information about the tool can be found at <https://jreleaser.org>.
