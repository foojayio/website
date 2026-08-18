---
title: "MelodyMatrix V1.0.0 Released: Shipping a JavaFX App with jDeploy, GitHub Actions, and Auto-Update"
slug: "melodymatrix-v1-0-0-released-shipping-a-javafx-app-with-jdeploy-github-actions-and-auto-update"
date: "2026-04-20T14:51:00+00:00"
description: "Some side projects take a while to get to a proper release. MelodyMatrix is one of those. The app has been downloadable for quite some time thanks to - by Frank Delporte"
canonical: "https://webtechie.be/post/2026-04-16-first-release-of-melodymatrix-with-jdeploy/"
authors:
  - "frankdelporte"
image: "edit-melodymatrix-jdeploy-release.jpg"
categories:
  - "JavaFX"
tags:
related_posts:
  - "foojay-podcast-54"
  - "video-series-javafx-in-action-part-3"
  - "javafx-links-of-december-2024"
  - "javafx-links-of-october-2024"
frozen: false
---

Some side projects take a while to get to a proper release. [MelodyMatrix](https://melodymatrix.rocks/) is one of those. The app has been downloadable for quite some time thanks to [jDeploy](https://www.jdeploy.com/), but there was no official V1.0.0 yet. No tagged release. No moment of "okay, this is it." Just a rolling build on every commit to the `main` branch.

That changed now: live, on camera, together with Steve Hannah, the creator of jDeploy.

{{< youtube _-IL7uHalIU >}}

## What Is MelodyMatrix?

[MelodyMatrix](https://melodymatrix.rocks/) is a JavaFX application for musicians. It connects to MIDI devices, lets you practice and record, and has a set of views that help you understand what you are playing. Those views are open source and available on [GitHub](https://github.com/codewriterbv/melodymatrix-app-views). The full app is free, but has some features that are only available with a license. The download packages are published via [GitHub Releases](https://github.com/codewriterbv/melodymatrix-app-releases/releases).

Building it has been a long journey. JavaFX, MIDI, and musical theory in one app, keeping it working across Windows, macOS, and Linux, while also building and distributing it automatically from GitHub Actions. That last part is where jDeploy comes in.

## What Is jDeploy?

If you have not heard of jDeploy yet, Steve explains it very well in a few sentences: "*Once you finish building a desktop application, you hit a wall. How do you share it? How does your user install it? And when you update it, how do they get the new version without manually downloading and reinstalling?*"

jDeploy solves all of that. It creates native installers for Windows (`.exe`), macOS (`.dmg`), and Linux, handles auto-update on launch, bundles its own JVM so users do not need Java installed, and uses GitHub Releases as the distribution backend. No Maven Central, no NPM account required. Your GitHub repo is your distribution channel.

I did a full interview with Steve earlier where he explains the background of the tool: [JavaFX In Action #12 with Steve Hannah about jDeploy](https://webtechie.be/post/2024-12-12-jfxinaction-steve-hannah-jdeploy/). Worth watching if you want the full picture.

## The Video: Preparing and Triggering the First Release Together

This new video is about an hour long and covers the whole process of getting MelodyMatrix ready for V1.0.0. Steve joined me, and we walked through the project configuration together, fixed a few things in the GitHub Actions workflow, discussed best practices around git tags, and then actually triggered the first release build while the camera was still running.

Some of the things we covered:

* How jDeploy is configured in the MelodyMatrix project, including how JavaFX preview features are enabled.
* npm versus GitHub for the release packages (short version: npm was much easier to get started with than Maven, and Steve modeled the GitHub metadata format on the npm package format).
* Why the Windows installer is an `.exe` file and not a zip (user experience: download, click, done).
* How jDeploy manages its own JVM per platform (default is Zulu, with some platform-specific exceptions for Windows ARM and Linux).
* Why Maven is not used for application distribution, and why Steve holds his breath every time he does a Maven deploy.
* How to set the version number for a release without breaking the path to your jar file.
* How to prevent multiple jDeploy workflows from running simultaneously.
* Best practices around git tags, including the capital V versus lowercase v debate.
* Customizing the installer and launcher splash screen with a custom HTML page.
* How jDeploy can use a local build to test the installation before pushing to GitHub.

And around the 57-minute mark, the release build finished on GitHub Actions and **MelodyMatrix V1.0.0 was live**.

## Why jDeploy, and Why It Is Free

jDeploy is completely free! Steve wants Java desktop apps to be easy to deploy, and any barrier to entry works against that goal. He is working on a paid tier with private repositories and deployment authentication, but the core tool stays free.

For a solo developer building a side project like MelodyMatrix, that matters. I can push a new version by tagging a commit, and within minutes there is a new installer available for every platform. Users get it automatically on the next launch. No manual distribution, no "please re-download the installer" emails.

## A Side Note on Lottie4J

While waiting for the GitHub Actions build to finish, Steve mentioned he had been experimenting with adding Lottie animation support to the jDeploy splash screen, and we talked about [Lottie4J](https://lottie4j.com/) for a bit. That is the kind of thing that happens in a live session. 🙂 Steve even built a Claude Code skill for creating a custom HTML splash screen with a LottieFiles animation. If you want to try it: [jdeploy-claude on GitHub](https://github.com/shannah/jdeploy-claude) and the [custom launcher splash screen skill](https://github.com/shannah/jdeploy-claude/tree/main/plugins/jdeploy/skills/custom-launcher-splash).

## Timeline

* 00:00 Introduction
* 00:48 Who is Steve Hannah and why he created jDeploy to distribute Java applications with automatic updates
* 05:42 How Frank uses jDeploy for MelodyMatrix, demo of the application
* 07:26 jDeploy configuration in the MelodyMatrix project, how JavaFX preview features are enabled, GitHub Actions
* 08:32 How new versions get distributed, npm versus Maven versus GitHub
* 10:56 A look into the GitHub Actions flow for MelodyMatrix
* 13:38 Why Maven is not used for application distribution
* 16:38 Why the Windows installer is an `.exe` file
* 23:13 The JVM runtimes used by a jDeploy application
* 26:05 Modifying the splash screen of the installer and application
* 28:23 The jDeploy desktop app to configure your project
* 31:46 Preparing the MelodyMatrix project for the first release, how to set the release version number, and improving the GitHub workflow
* 40:45 Info about the open-source part of the MelodyMatrix project and the struggle between Gradle and Maven
* 42:59 Checking the GitHub Actions for the release, and more improvements to prevent multiple simultaneous jDeploy workflows
* 45:39 Best practices regarding the use of git tags
* 48:42 Starting the build of the first MelodyMatrix release as V1.0.0!
* 49:40 While the build process is running on GitHub Actions, experimenting with jDeploy locally
* 54:34 Release builds on GitHub Actions are still busy, so another side step to LottieFiles and Lottie4J, and how they could be integrated in the jDeploy splash screen
* 57:41 The first release is ready. Installing and trying it...
* 01:00:40 jDeploy is free! Steve just wants Java to be easy to deploy
* 01:02:31 Conclusion

## Links

* [JavaFX In Action #12 with Steve Hannah about jDeploy, to distribute your Java app as a native bundle](https://webtechie.be/post/2024-12-12-jfxinaction-steve-hannah-jdeploy/)
* Steve Hannah:
  * [LinkedIn](https://www.linkedin.com/in/sjhannah/)
  * [Twitter](https://x.com/shannah78)
  * [Personal blog](https://sjhannah.com/blog/)
* MelodyMatrix:
  * [Website](https://melodymatrix.rocks/)
  * [Open-source viewers on GitHub](https://github.com/codewriterbv/melodymatrix-app-views)
  * [Download packages on GitHub](https://github.com/codewriterbv/melodymatrix-app-releases/releases)
* jDeploy:
  * [Website](https://www.jdeploy.com/)
  * [Sources on GitHub](https://github.com/shannah/jdeploy)
  * [Newsletter](https://jdeploy.substack.com/)
  * [jDeploy vs jpackage](https://jdeploy.substack.com/p/jdeploy-vs-jpackage)
  * [Now you can deploy your app as a DMG](https://jdeploy.substack.com/p/now-you-can-deploy-your-app-as-a)
  * [Claude Code plugin for jDeploy](https://github.com/shannah/jdeploy-claude)
  * [Custom launcher splash screen skill](https://github.com/shannah/jdeploy-claude/tree/main/plugins/jdeploy/skills/custom-launcher-splash)
* Lottie4J:
  * [Website](https://lottie4j.com/)
  * [Sources on GitHub](https://github.com/lottie4j/)
