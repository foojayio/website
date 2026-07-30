---
title: "Released: JReleaser 0.4.0 is Now Available"
slug: "jreleaser-0-4-0-is-now-available"
date: "2021-05-31T20:11:44+00:00"
lastmod: "2021-11-30T10:30:53+00:00"
description: "JReleaser streamlines releases for Homebrew, Snapcraft, Docker, Chocolatey, JBang, Twitter, SdkMan, Zulip, Gitter, Slack, Teams, etc."
authors:
  - "andres-almiray"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
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

JReleaser v0.4.0 has been released!

JReleaser is a tool that streamlines creating releases for \[Java\] projects. It can create a GitHub/GitLab/Gitea release, while also packaging binaries for Homebrew, Snapcraft, Docker, Chocolatey, Scoop, JBang and, more than that, announces releases to Twitter, SdkMan!, e-mail, Zulip, Discord, Gitter, Slack, Teams, and more.

It's CI friendly and runs on a variety of services.

JReleaser provides a unified model across all supported services and delivers equal user experience on local environments and CI servers.

This release

* adds support for Codeberg and generic git services
* configures Linux (glibc) and Linux (musl) cross-platform Java Runtimes
* supports multiple Dockerfile configurations per distribution
* announces releases to Mastodon \& Mattermost
* enables uploading of artifacts to an HTTP server
* improves to checksum calculation and file signing

The website now has searchable docs. 14 different CI/CD setup guides available.

It should come to no surprise that JReleaser can release itself, as witnessed by the [v0.4.0](https://github.com/jreleaser/jreleaser/releases/tag/v0.4.0 "v0.4.0") page.

More information about the tool can be found at <https://jreleaser.org>.
