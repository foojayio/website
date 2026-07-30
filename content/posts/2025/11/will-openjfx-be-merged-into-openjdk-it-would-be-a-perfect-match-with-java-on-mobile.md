---
title: "Will OpenJFX Be Merged Into OpenJDK? It Would Be a Perfect Match with Java on Mobile!"
slug: "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
date: "2025-11-26T09:18:36+00:00"
description: "While looking for articles for the JFX Central Links Of The Week, I found this very interesting article by Paul Krill on InfoWorld. It's based on an - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-11-05-openjfx-returning-to-openjdk/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/jfxcentral.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "a-javafx-app-on-zulufx-in-60-seconds"
  - "beginning-javafx-with-intellij"
  - "javafx-links-of-october-2025"
  - "javafx-nodes-versus-canvas"
frozen: false
---

While looking for articles for the [JFX Central Links Of The Week](https://www.jfx-central.com/links), I found this very interesting [article by **Paul Krill** on InfoWorld](https://www.infoworld.com/article/4082709/will-javafx-return-to-java.html). It's based on an [October 29 post by **Bruce Haddon** on an OpenJDK discussion list](https://mail.openjdk.org/pipermail/discuss/2025-October/006553.html) in which he argues that the reasons for the separation of OpenJFX from OpenJDK in Java 11, more than seven years ago, are much less applicable today.

Again, some exciting developments seem to be happening in the Java world! There's a growing discussion about bringing JavaFX back into the OpenJDK, and it couldn't come at a better time, especially with Johan Vos and the team at Gluon working hard on making Java a first-class citizen on mobile platforms.

Let me explain what's happening and why this could be a game-changer for the Java ecosystem.

History: Why JavaFX Got Separated From The JDK {#h2-0-history-why-javafx-got-separated-from-the-jdk}
----------------------------------------------------------------------------------------------------

JavaFX was part of the JDK till Java 10. But with Java 11, more than seven years ago, it became a separate library. You can find its [sources on GitHub within the OpenJDK project](https://github.com/openjdk/jfx). At the time, there were three main reasons for this separation:

1. **JDK Bloat**: JavaFX increased the size of the JDK.
2. **Independent Evolution**: The separation allowed both the JDK and JavaFX to evolve at their own speed.
3. **Maintenance Shift**: Johan Vos of Gluon became co-lead of the OpenJFX project in 2018, and Gluon started to build and distribute JavaFX SDKs and Maven artifacts.

For many developers, this separation meant having to make two separate installations and then wrestling with IDE configurations to get everything working together, or install a build which combines them (e.g. from Azul or BellSoft). Not exactly the smooth developer experience we all hope for...

Time to Reunite {#h2-1-time-to-reunite}
---------------------------------------

Bruce Haddon posted a compelling argument on the OpenJDK discussion list about why it's time to bring JavaFX back into the fold. His reasoning makes a lot of sense as the original concerns were largely solved.

1. **JDK Bloat**: Thanks to modularization, this is no longer an issue. You only need to ship the modules your application actually uses. If you don't need JavaFX, you don't include those modules.
2. **Synchronized Releases**: The OpenJDK and OpenJFX releases have been in lockstep for quite some time now. They share the same version numbers, release on the same dates (often within hours of each other), and even coordinate security updates.
3. **Open Source Collaboration**: Both OpenJDK and OpenJFX are available as open source, allowing community involvement and innovation to continue regardless of whether they're packaged together.

Next to this, [Johan Vos also shared a message on the mailing list](https://mail.openjdk.org/pipermail/openjfx-dev/2025-September/056477.html), "Building OpenJFX using the OpenJDK build system", in which he explained that the OpenJDK build system can now be used to build OpenJFX, bringing both projects even closer together. You can also find this project [on GitHub \> openjdk-mobile \> openjfx-build](https://github.com/openjdk-mobile/openjfx-build).

### The Developer Experience Matters {#h3-2-the-developer-experience-matters}

For students and beginners especially, having JavaFX integrated with the JDK would make a huge difference. It would send a clear message that JavaFX is on equal footing with other JDK features, not some optional add-on that requires extra effort to learn and configure.

In the [article on InfoWorld](https://www.infoworld.com/article/4082709/will-javafx-return-to-java.html), Donald Smith, Oracle's vice president of Java product management, states: "*Oracle continues to lead and be active in the OpenJFX Project. While we don't have specific announcements or plans currently, we are investigating options for improving the approachability of JavaFX with the JDK.*"

Enter Java on Mobile: The Perfect Timing {#h2-3-enter-java-on-mobile-the-perfect-timing}
----------------------------------------------------------------------------------------

Here's what I find fascinating: while the JavaFX integration discussion is happening, Johan Vos and the team at Gluon are pushing forward another fantastic initiative: bringing full OpenJDK to mobile platforms!

### The Challenge They're Solving {#h3-4-the-challenge-they-re-solving}

For nearly a decade, Gluon has been working to make Java a first-class citizen on mobile. They built a toolchain that allowed developers to run Java and JavaFX applications on iOS and Android. It worked, but maintaining it became increasingly difficult. Every new iOS or Android release, every JDK update, every change to the AOT compiler required patches and adjustments. It simply didn't scale.

### A New Approach: Java on Mobile {#h3-5-a-new-approach-java-on-mobile}

Instead of maintaining complex patches and workarounds, Johan Vos proposes to focus on building OpenJDK directly for mobile targets. Gluon created a GitHub-based community site at [openjdk-mobile.github.io](https://openjdk-mobile.github.io) that documents all the missing pieces, provides scripts and GitHub Actions to automate the work, and publishes nightly builds to catch regressions early.

The goal is to make OpenJDK work on mobile continuously, not just patch one JDK version for one iOS/Arduino release.

### Current Progress and Roadmap {#h3-6-current-progress-and-roadmap}

They already have a [working pipeline that runs a HelloWorld application on iOS](https://openjdk-mobile.github.io/ios/helloworld/)! From here, the roadmap includes:

* Adding support for the iOS Simulator and Android.
* Improving performance and exploring optimizations based on ongoing work in the [OpenJDK Project Leyden](https://openjdk.org/projects/leyden/).
* Full JavaFX applications to run as native iOS/Android apps.
* Exposing Java libraries as native mobile libraries.

Why These Two Initiatives Complement Each Other Perfectly {#h2-7-why-these-two-initiatives-complement-each-other-perfectly}
---------------------------------------------------------------------------------------------------------------------------

In my opinion, "Java on Mobile" and "OpenJFX in OpenJDK" are a perfect match. If OpenJFX gets integrated again in OpenJDK, and OpenJDK becomes a first-class citizen on mobile, then JavaFX automatically becomes mobile-ready as part of the standard Java distribution.

This would mean:

* **For Developers**: Write once, deploy everywhere - truly. The same JavaFX application could run on desktop (Windows, macOS, Linux), mobile (iOS, Android), and embedded systems without requiring separate toolchains or complicated build configurations.
* **For Students and "Newbies"**: Learning Java would include GUI development for all platforms right out of the box. No more "Java is only for backends" misconceptions.
* **For the Ecosystem**: A unified, consistent experience across all platforms. The Java ecosystem would finally have a modern, complete, and integrated GUI solution that works everywhere Java runs.
* **For Maintainability**: With JavaFX in the JDK and mobile support directly in OpenJDK, there would be less fragmentation, fewer separate patches to maintain, and a clearer path forward for updates and improvements.

A Community Effort {#h2-8-a-community-effort}
---------------------------------------------

There is one crucial point: this is a community effort. Gluon can't do the mobile work alone, and the JavaFX integration won't happen without community support and involvement.

The [openjdk-mobile project on GitHub](https://github.com/openjdk-mobile/) is open to contributions from anyone interested in helping to bring "full Java" to mobile. Whether you're interested in performance optimization, JavaFX integration, or just want to test and report issues, there's room for everyone to contribute.

As [Johan Vos wrote](https://gluonhq.com/bringing-openjdk-to-mobile-a-community-effort/): "We believe that no miracle is needed -- Java was designed to be portable, and it should run on mobile as a first-class citizen. It's just a matter of making it happen, together."

What's Next? {#h2-9-what-s-next}
--------------------------------

These are still early days. The JavaFX integration discussion is just beginning, and while there's positive sentiment, there are no official announcements yet. The Java on Mobile project is making progress but still has significant work ahead.

However, the timing couldn't be better. The technical obstacles that once prevented JavaFX from being part of the JDK are gone. The need for Java on mobile has never been clearer. And most importantly, there's a passionate community ready to make it happen.

If you're interested in following these developments or contributing, here are the key places to watch:

* [OpenJDK discuss mailing list](https://mail.openjdk.org/pipermail/discuss/) to follow the JavaFX integration discussion.
* [openjdk-mobile.github.io](https://openjdk-mobile.github.io) for the mobile initiative.
* [OpenJDK Mobile GitHub organization](https://github.com/openjdk-mobile/) for contributing to the Java on Mobile work.
* [Gluon's blog](https://gluonhq.com/news/) for updates from the team leading the JavaFX work.

I'm excited to see where this goes. Java has always been about "write once, run anywhere," and these initiatives could finally deliver on that promise more completely than ever before.

What do you think? Would you use Java(FX) on Mobile?
