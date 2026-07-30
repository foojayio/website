---
title: "Gluon Update, Dual Screen Raspberry Pi , IntelliJ JavaFX Project Wizard"
slug: "javafx-gluon-status-update-dual-screen-on-raspberry-pi-sample-intellij-new-javafx-project-wizard"
date: "2021-06-04T07:27:41+00:00"
lastmod: "2021-12-10T13:00:24+00:00"
description: "In this post we are going to take a deeper look into some of the recent JavaFX announcements, e.g., dual screen support on Raspberry Pi!"
authors:
  - "frankdelporte"
image: "https://blog.jetbrains.com/wp-content/uploads/2021/05/UX_JavaFXWizard.gif"
categories:
  - "Embedded"
  - "Gluon"
  - "IntelliJ IDEA"
  - "JavaFX"
  - "Pi4J"
  - "Raspberry Pi"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Last week, Johan Vos of Gluon, released a video with a status update of JavaFX. In this post we are going to take a deeper look into one of the announcements: dual screen support on Raspberry Pi.

Announcements by Gluon {#h2-0-announcements-by-gluon}
-----------------------------------------------------

Since Java switched to a 6-month release cycle, JavaFX has done the same, so next version will be number 17. Keep in mind, although Java and JavaFX are on the same version-number, you can still use Java 11 and combine it with the JavaFX 17 runtime if you want to benefit from its improvements. Up till now, there were no breaking changes in either of the frameworks which force you to use a Java-version higher than 11.

Below you can find the full video, but these are the highlights:

1. More committers and 126 issues fixed in JavaFX 17
2. \>100K SDK downloads from gluonhq.com in April
3. **JavaFX on embedded: JVM on ARM32 or JVM and Statically compiled and GraalVM on ARM64**
4. Hardware acceleration using OpenGL on embedded
5. Demo of **dual screen support on Raspberry Pi**
6. JavaFX native on Android and iPhone mobile
7. Gluon Cloud connects Java applications with cloud services
8. Gluon CloudLink synchronizes and secures data in the cloud
9. And one more thing... **JavaFX in the browser as JavaScript + WebGL**, yes really!

{{< youtube LoL30W0yo6g >}}

A dual screen experiment on Raspberry Pi {#h2-1-a-dual-screen-experiment-on-raspberry-pi}
-----------------------------------------------------------------------------------------

Besides the JavaFX-in-browser, the dual-screen support for Raspberry Pi is a very nice feature that I wanted to try out myself. You can try the same, as this is already available for everyone in the latest 17-ea version of JavaFX you can [download from the Gluon website](https://gluonhq.com/products/javafx/).

For a quick demo, I combined a [Pi4J JavaFX minimal sample application](https://github.com/Pi4J/pi4j-example-javafx) (ready-to-go Maven project) with some sample code that was provided by Gluon. On the [PI4J documentation site](https://pi4j.com/getting-started/user-interface-with-javafx/) you can find all information about installing JavaFX on the Raspberry Pi and in the article ["JavaFX Running in Kiosk Mode on the Raspberry Pi" here on foojay](https://foojay.io/today/javafx-running-in-kiosk-mode-on-the-raspberry-pi/), you can find more info on desktop versus kiosk mode.

The sources of the project shown in the video below [are available on GitHub](https://github.com/FDelporte/JavaFxDualScreen).

<figure class="wp-block-embed is-type-video is-provider-vimeo wp-block-embed-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe title="JavaFX dual screen support on Raspberry Pi" src="https://player.vimeo.com/video/556590497?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

I used a CrowPi (electronics experiment kit) which has a small touchscreen, combined with a 4K display which is connected to the second micro HDMI connector of a Raspberry Pi 4.

On each screen a chess board pattern is created and by clicking on the small screen, the selected box is shown. Because both screens have a different resolution, the selected box is smaller on the big screen.

The application is first packaged with Maven and started with a bash script from another computer through SSH for easier demonstration.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn package
$ cd target/distribution
$ sudo bash run-kiosk.sh</pre>

The script first disables desktop mode with `/sbin/init 3`, starts the compiled jar with some additional settings, and when the program exits, the desktop mode is started again with `/sbin/init 5`. With this approach, **hardware acceleration** is used for maximum performance of the JavaFX rendering. It is also possible to use the **cursor and hardware rotation**, but this still needs to be fully documented.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">rm #!/usr/bin/env bash
/sbin/init 3
export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true
java \
  -Degl.displayid=/dev/dri/card0 \
  -Dmonocle.egl.lib=/opt/javafx-sdk-17/lib/libgluon_drm.so \
  -Djava.library.path=/opt/javafx-sdk-17/lib \
  -Dmonocle.platform.traceConfig=false \
  -Dprism.verbose=false \
  -Djavafx.verbose=false \
  -Dmonocle.platform=EGL \
  --module-path .:/opt/javafx-sdk-17/lib \
  --add-modules javafx.controls \
  --module be.webtechie.test/be.webtechie.test.Main $@
/sbin/init 5</pre>

New JavaFX project wizard in IntelliJ IDEA {#h2-2-new-javafx-project-wizard-in-intellij-idea}
---------------------------------------------------------------------------------------------

And that wasn't the only JavaFX announcement last week! JetBrains showed a great new JavaFX project wizard in IntelliJ IDEA (version 2021.2 EAP 1)!

*To save you time configuring settings after project creation, we've reworked the new project wizard for JavaFX. It only takes two steps. First, you add a project SDK and the language you will use, the desired build system, and the test framework.*

*Then you will have the opportunity to choose from a list of the most frequently used libraries, which come with short descriptions on the right. Once you create your new JavaFX project, your IDE will generate a fully configured sample application.*
![](https://blog.jetbrains.com/wp-content/uploads/2021/05/UX_JavaFXWizard.gif)

Conclusion {#h2-3-conclusion}
-----------------------------

I've always loved how easy it us to build nice user interfaces with JavaFX. And I love to see how this "old and reliable" framework on top of Java is gaining back the traction it deserves.

This dual-screen example on Raspberry Pi is just a quick demo to show and share the code. I'm still thinking of a nice demo that combines it with some of the electronic components in the CrowPi... Any ideas and some spare time you can share? 😉

At Gluon, they are working very hard to make sure Java developers can easily create client applications, run them on any platform, and connect them to cloud and enterprise systems. And with the powerful tools of JetBrains, developers can focus on good coding.

JavaFX in the browser will extend the market further and I'm are really looking forward to the first proof-of-concept code examples.
