---
title: "JavaFX Templates for Desktop Applications | Foojay.io Today"
slug: "javafx-templates-for-desktop-applications"
date: "2021-10-15T08:48:16+00:00"
lastmod: "2021-10-18T06:43:51+00:00"
description: "Both for Maven and Gradle lovers there are different possibilities to build Java executables and GitHub provides the free tools to do so!"
authors:
  - "frankdelporte"
image: "/images/posts/2021/10/javafx-templates-for-desktop-applications/Screenshot-2021-10-10-at-22.08.21.png"
categories:
  - "JavaFX"
tags:
related_posts:
  - "creating-mobile-apps-with-javafx-part-1"
  - "cross-platform-development-in-java-with-gluon-and-graalvm"
  - "native-applications-for-multiple-devices-from-a-single-javafx-project-with-gluon-mobile-and-github-actions"
  - "creating-a-javafx-world-clock-from-scratch-part-4"
enlighterjs: true
frozen: false
---

Here on Foojay we already talked about JavaFX write-once-run-everywhere applications and how they can be created on GitHub with Gluon and GraalVM:

* [Creating Mobile Apps with JavaFX](https://foojay.io/today/creating-mobile-apps-with-javafx-part-1/) by Gail Anderson
* [Cross-Platform Development in Java with Gluon and GraalVM](https://foojay.io/today/cross-platform-development-in-java-with-gluon-and-graalvm/) by Bruno Lowagie
* [Native Applications for Multiple Devices from a Single JavaFX Project with Gluon Mobile and GitHub Actions](https://foojay.io/today/native-applications-for-multiple-devices-from-a-single-javafx-project-with-gluon-mobile-and-github-actions/) by myself

In this post, we are going to take a look at other different approaches.

### Modularized JavaFX Template with Gradle by [Gerrit Grunwald](https://twitter.com/hansolo_) {#h3-0-modularized-javafx-template-with-gradle-by-gerrit-grunwald}

[This is a little project on GitHub](https://github.com/HanSolo/fxmodules) that can be used as a template for modularized JavaFX projects, based on JDK17. You can import the `build.gradle` file as a project into your IDE and start the application by using `gradlew Main` from the command line or from within the IDE.

<figure class="wp-block-image size-full is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2021/10/javafx-templates-for-desktop-applications/Screenshot-2021-10-10-at-22.08.21.png" alt="" class="wp-image-49758" width="401" height="183">
 <figcaption>
  The running template application
 </figcaption>
</figure>

You will find three script files

* build_app_windows.bat
* build_app_macos.sh
* build_app_linux.sh

...which can be used to build native packages and installers on the platform of your choice. If you would like to build a native package on MacOS you need to perform the following steps:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./gradlew clean build
bash build_app_macos.sh</pre>

After the script has finished you will find the dmg, the pkg and the app file in the folder

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">fxmodules/build/installer</pre>

In addition the project also comes with a github action in the folder

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">fxmodules/.github/workflows</pre>

The `ci.yml` file will build the native packages for each platform on GitHub after each push to the project. The artifacts will be attached to the build so that you can download it from there.

### JPackageScriptFX by [Dirk Lemmermann](https://twitter.com/dlemmermann/status/1446045501577641986) and [Michael Paus](https://twitter.com/MichaelPaus) {#h3-1-jpackagescriptfx-by-dirk-lemmermann-and-michael-paus}

[This project, originally created by Michael Paus was turned into a template project by Dirk Lemmermann](https://github.com/dlemmermann/JPackageScriptFX). It demonstrates how projects can use scripts to build self-contained, platform-specific executables and installers of their JavaFX applications via the `jdeps`, `jlink`, and `jpackage` tools. Two scripts are included for running builds on Mac/Linux and Windows. The `jpackage` tool is bundled with the JDK since version 14.

The project in this repository uses a multi-module Maven setup with a parent module containing three child modules. One of these child modules is the "main" module as it contains the main class. This module also contains the build scripts and its target directory will contain the results of the build. The JavaFX application consists of a single-window displaying three labels. The first one shows the currently configured locale and the other two labels get imported from module 1 and module 2 respectively.

<figure class="wp-block-image is-resized">
 <a href="https://github.com/dlemmermann/JPackageScriptFX/blob/master/app.png" target="_blank" rel="noreferrer noopener"><img decoding="async" src="https://github.com/dlemmermann/JPackageScriptFX/raw/master/app.png" alt="alt text" width="216" height="216" title="Demo App"></a>
 <figcaption>
  The second template application
 </figcaption>
</figure>

The platform-specific versions are created with Maven and this whole process is very well and detailed explained [in the README file of the GitHub project](https://github.com/dlemmermann/JPackageScriptFX/blob/master/README.md).

### maven-jpackage-template by [Will Iverson](https://twitter.com/wiverson) {#h3-3-maven-jpackage-template-by-will-iverson}

And there is even [one more Maven template project](https://github.com/wiverson/maven-jpackage-template) we can share here! It generates a custom JVM and installer package for a JavaFX application. It can easily be adapted to work with Swing instead.

The generated installers come in at around 30-40MB. The example source in the project includes demonstrations of several native desktop features - for example, drag-and-drop from the Finder/Explorer, as well as a few macOS Dock integration examples. Removing the code and the demonstration dependencies gets a "Hello World" build size closer to 30MB.

### Build with GitHub Actions {#h3-4-build-with-github-actions}

All three projects produce the promised platform-specific runtime with GitHub Actions.

* The Gradle-project by Gerrit uses a [single action to produce Linux, Windows and Mac versions](https://github.com/HanSolo/fxmodules/actions/runs/1315436370).
* The Maven-projects have an action file for each platform version
  * actions in [JPackageScriptFX](https://github.com/dlemmermann/JPackageScriptFX/actions)

  <!-- -->

  * actions in [maven-jpackage-templates](https://github.com/wiverson/maven-jpackage-template/actions)

### Conclusion {#h3-5-conclusion}

Both for Maven and Gradle lovers there are different possibilities to build Java executables and GitHub provides the free tools to do so.

Thanks to Gerrit, Michael, Dirk, Will, and other contributors - who share these kinds of templates - getting started with JavaFX for desktop applications became easier again!
