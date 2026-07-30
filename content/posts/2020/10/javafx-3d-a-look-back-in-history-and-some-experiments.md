---
title: "JavaFX 3D: A Look Back Through History & Some Experiments"
slug: "javafx-3d-a-look-back-in-history-and-some-experiments"
date: "2020-10-27T09:11:30+00:00"
lastmod: "2020-11-08T20:53:12+00:00"
description: "JavaFX 3D really is a hidden gem! I've been using JavaFX already for a long time now but wasn't aware of these 3D features!"
authors:
  - "frankdelporte"
image: "/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/Screenshot-from-2020-10-25-22-04-10.png"
categories:
  - "Embedded"
  - "JavaFX"
  - "Raspberry Pi"
tags:
related_posts:
  - "a-fresh-look-at-embedded-java"
  - "first-experiments-with-java-on-the-lattepanda-iota"
  - "javafx-links-of-november-2025"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
enlighterjs: true
frozen: false
---

After my virtual conference talk ["Java and JavaFX on the Raspberry Pi" at the "Oracle Groundbreakers APAC Virtual Tour 2020"](https://webtechie.be/post/2020-10-21-apacouc-java-and-javafx-on-raspberry-pi/), I got in touch with some people who were working on JavaFX 3D in the past, and were curious how that would behave on the Raspberry Pi.

Only one way to find out! Let's experiment!

History of JavaFX 3D {#h2-0-history-of-javafx-3d}
-------------------------------------------------

JavaFX is an open-source, next-generation Java library for rich client applications. JavaFX started with a focus on 2D UI elements.

But by JavaFX 8, it became apparent that certain use cases needed 3D graphics. For example, one of Oracle's customers needed 3D graphics to visualize a 3-dimensional layout of their shipping containers:

{{< youtube AS26gZrYNy8 >}}

Work on 3D graphics capabilities within JavaFX progressed to the point that 3D characters could be animated with JavaFX. At JavaOne 2013, a chessboard with animated Duke chess pieces was presented at the keynote demo. This animated demo was presented along with a robotic arm that controlled a 3D-printed version of these Duke chess pieces, controlled from the same server.
![](/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/Screenshot-from-2020-10-25-22-04-10-1024x521.png)

{{< youtube 4og3QCOnSaQ >}}

You can get a behind the scenes look at how Duke was brought to life in the following presentation from 2014.

<figure class="wp-block-embed-youtube wp-block-embed is-type-video is-provider-youtube wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  https://www.youtube.com/watch?v=FJe-_3nZkns
 </div>
</figure>

There is even JavaFX "in space" as this cool video of NASA shows. It's a demonstration of the "JavaFX Deep Space Trajectory Explorer" as presented on DevNexus 2018.

{{< youtube U7wdvhRKEiY >}}

When JavaFX was taken out of the JDK by Oracle for version 11 in 2018, [GluonHQ](https://gluonhq.com/) became one of the main contributors to further develop it within the [OpenJFX project](https://openjfx.io/). Since then, they kept releasing new versions with the same 6-month release cycle as the OpenJDK. Each version brings new features, improvements, and bug and security fixes. JavaFX 3D is still supported and has become even more powerful with all the ongoing improvements in the framework.

For the upcoming version 16 of OpenJFX, better support for embedded devices running on ARM-processors (e.g. the Raspberry Pi) is in development with better integration of the [Direct Rendering Manager (DRM)](https://en.wikipedia.org/wiki/Direct_Rendering_Manager).

<figure class="wp-block-embed-twitter wp-block-embed is-type-rich is-provider-twitter">
 <div class="wp-block-embed__wrapper">
  <blockquote class="twitter-tweet" data-width="500" data-dnt="true">
   <p lang="en" dir="ltr">Imagine all of the embedded possibilities of running JavaFX using framebuffer-based rendering on Raspberry Pi and other devices! Gluon can, and that is why we are gearing up to announce JavaFX support, here's a short teaser: <a target="_blank" href="https://t.co/ruH9nkCtMI">https://t.co/ruH9nkCtMI</a></p>— GluonHQ (@GluonHQ) <a target="_blank" href="https://twitter.com/GluonHQ/status/1315747705155719170?ref_src=twsrc%5Etfw">October 12, 2020</a>
  </blockquote>
 </div>
</figure>

Another valuable resource for JavaFX 3D is available at the [GitHub project "FXyz3D"](https://github.com/FXyz/FXyz). This allows you to create many different 3D custom shapes and even has a GUI application which allows you to visualize all the samples and the different options.
![](/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/fxsampler-1024x655.png)

Chess seems to be a popular topic for 3D-experiments as this final impressive example shows. In this application, both a 2D and 3D board are combined and show the same moves simultanously.

{{< youtube 6uEbfeW-9Gg >}}

**Let's experiment!** {#h2-1-let-s-experiment}
----------------------------------------------

### **The code** {#h3-2-the-code}

For this post, I collected some existing stuff in this [GitHub JavaFX3D project](https://github.com/FDelporte/JavaFX3D/). I didn't use the original repositories but reworked them a bit to use Maven, so you can get started easily.

#### **Duke**

Within the [GithHub OpenJDK project](https://github.com/openjdk), you can find not only the sources of the JDK, but also [the Duke in many different formats](https://github.com/openjdk/duke)!

For this experiment, I copied the files from the "Chess Duke" directory as there a multiple 3D images available here.

#### **MoleculeSampleApp**

This application renders a water molecule in JavaFX 3D.

#### **3DViewer**

This application can load different 3D image file formats and visualize them.

<figure class="wp-block-gallery columns-3 is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <ul class="blocks-gallery-grid">
  <li class="blocks-gallery-item">
   <figure>
    <img loading="lazy" decoding="async" width="250" height="385" src="/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/duke-welcome-1.jpg" alt="" data-id="36189" data-full-url="https://foojay.io/wp-content/uploads/2020/10/duke-welcome-1.jpg" data-link="https://foojay.io/?attachment_id=36189" class="wp-image-36189">
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <img loading="lazy" decoding="async" width="795" height="621" src="/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/MoleculeSampleApp.png" alt="" data-id="36190" data-full-url="https://foojay.io/wp-content/uploads/2020/10/MoleculeSampleApp.png" data-link="https://foojay.io/?attachment_id=36190" class="wp-image-36190">
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <img loading="lazy" decoding="async" width="798" height="487" src="/images/posts/2020/10/javafx-3d-a-look-back-in-history-and-some-experiments/3DViewer.png" alt="" data-id="36188" data-full-url="https://foojay.io/wp-content/uploads/2020/10/3DViewer.png" data-link="https://foojay.io/?attachment_id=36188" class="wp-image-36188">
   </figure></li>
 </ul>
</figure>

### **Build and run on PC** {#h3-3-build-and-run-on-pc}

Let's try out if all this works on a Linux PC with OpenJDK 11. We will use this in combination with the current latest JavaFX provided by Gluon.

* Download the JavaFX JDK from https://gluonhq.com/download/javafx-16-ea-sdk-linux/
* Unpack the zip to e.g. /home/{YOUR_NAME}/javafx-sdk-16/
* Move into the directory of one of the Maven projects and build it as a JAR with Maven with `mvn clean package`
* Run in with `java -jar ...` and a the javafx-modules we just downloaded

See the detailed info below for each application.

#### **Building and running MoleculeSampleApp**

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd MoleculeSampleApp
$ mvn clean package
$ java --module-path /home/frank/javafx-sdk-16/lib 
      --add-modules=javafx.controls 
      -jar target/moleculesampleapp-0.0.1-jar-with-dependencies.jar</pre>

And oh yeah it runs! As we can expect from Java's promise for backwards compatibility, this demo created for a Java version in 2013, still works many years later on a much later version of the JDK.

<figure class="wp-block-embed-vimeo wp-block-embed is-type-video is-provider-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe loading="lazy" title="JavaFX 3D Molecule sample application" src="https://player.vimeo.com/video/471780531?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

#### **Building and running 3DViewer**

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd 3DViewer
$ mvn clean package
$ java --module-path /home/frank/javafx-sdk-16/lib 
     --add-modules=javafx.controls,javafx.fxml 
     -jar target/Jfx3dViewerApp-0.0.1-jar-with-dependencies.jar</pre>

When the application has started, I opened the Duke image "allStacked_solidColors_w0005_loweredPawnHat.ma" from the Duke Chess directory. The viewer provides multiple options to change the 3D visualization, lighting, etc.

<figure class="wp-block-embed-vimeo wp-block-embed is-type-video is-provider-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe loading="lazy" title="JavaFX 3D Viewer" src="https://player.vimeo.com/video/471782769?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

### **JavaFX 3D on the Raspberry Pi** {#h3-4-javafx-3d-on-the-raspberry-pi}

#### Some examples

JavaFX 3D is still an experimental feature for embedded but you can get it working if you go back to Java 8 as this tweet of [@javafx3d](https://twitter.com/javafx3d) shows:

<figure class="wp-block-embed-twitter wp-block-embed is-type-rich is-provider-twitter">
 <div class="wp-block-embed__wrapper">
  <blockquote class="twitter-tweet" data-width="500" data-dnt="true">
   <p lang="en" dir="ltr">Here's a Duke chess piece I modeled and animated in Maya for a JavaOne demo, running 3D on <a target="_blank" href="https://twitter.com/hashtag/JavaFX?src=hash&amp;ref_src=twsrc%5Etfw">#JavaFX</a> 8 on <a target="_blank" href="https://twitter.com/Raspberry_Pi?ref_src=twsrc%5Etfw">@Raspberry_Pi</a> in real-time, thanks to <a target="_blank" href="https://twitter.com/hashtag/JavaFX?src=hash&amp;ref_src=twsrc%5Etfw">#JavaFX</a> embedded by <a target="_blank" href="https://twitter.com/JPeredaDnr?ref_src=twsrc%5Etfw">@JPeredaDnr</a> and <a target="_blank" href="https://twitter.com/johanvos?ref_src=twsrc%5Etfw">@johanvos</a> at <a target="_blank" href="https://twitter.com/GluonHQ?ref_src=twsrc%5Etfw">@GluonHQ</a>! <a target="_blank" href="https://twitter.com/hashtag/JavaFX?src=hash&amp;ref_src=twsrc%5Etfw">#JavaFX</a> and <a target="_blank" href="https://twitter.com/GluonHQ?ref_src=twsrc%5Etfw">@GluonHQ</a> rock! <a target="_blank" href="https://t.co/0bU9mFcymo">pic.twitter.com/0bU9mFcymo</a></p>— Tech Designer 3D (John) (@TechDesigner3D) <a target="_blank" href="https://twitter.com/TechDesigner3D/status/1320220430674788353?ref_src=twsrc%5Etfw">October 25, 2020</a>
  </blockquote>
 </div>
</figure>

A practical use-case has been presented some time ago already by [PiDome](https://pidome.org/), an open-source full home automation platform developed especially for the Raspberry Pi. It's a powerful platform providing ease-of-use for non-technical users with possibilities which power users expect.

{{< youtube ROlXtpefjqM >}}

#### Window manager versus framebuffer

Regarding JavaFX on embedded, there are a large number of possible configurations on how you want to used. For the Pi, there are two approaches which need there own configuration:

1. Run your application inside a window manager (e.g. X11 with Raspberry Pi OS). In this case the application will run in a desktop window.
2. Run your application directly to the framebuffer, taking control of the full screen. This is what the DRM-approach allows for ([Direct Rendering Manager](https://en.wikipedia.org/wiki/Direct_Rendering_Manager)).

For both configurations, JavaFX can run with hardware acceleration or with software rendering. From a Java developer point, there is no difference, the code is the same. For applicability to the industry, there are important differences. In a development environment, a window manager is preferred, but in embedded hardware, a full-screen solution is often preferred as you don't want the end-user to open any other application.

As this tweet of [José Pereda](https://twitter.com/JPeredaDnr) illustrates, smooth 3D animations on the Raspberry Pi can be achieved!

<figure class="wp-block-embed-twitter wp-block-embed is-type-rich is-provider-twitter">
 <div class="wp-block-embed__wrapper">
  <blockquote class="twitter-tweet" data-width="500" data-dnt="true">
   <p lang="en" dir="ltr">Playing around with <a target="_blank" href="https://twitter.com/hashtag/JavaFX?src=hash&amp;ref_src=twsrc%5Etfw">#JavaFX</a> 3D (15) on a <a target="_blank" href="https://twitter.com/hashtag/raspberrypi?src=hash&amp;ref_src=twsrc%5Etfw">#raspberrypi</a> 4, with framebuffer at around 50 FPS, not bad!! <a target="_blank" href="https://twitter.com/GluonHQ?ref_src=twsrc%5Etfw">@GluonHQ</a> <a target="_blank" href="https://twitter.com/TeamRaspi?ref_src=twsrc%5Etfw">@TeamRaspi</a> <a target="_blank" href="https://twitter.com/JavaFX3D?ref_src=twsrc%5Etfw">@javafx3d</a> <a target="_blank" href="https://twitter.com/FrankDelporte?ref_src=twsrc%5Etfw">@FrankDelporte</a> <a target="_blank" href="https://twitter.com/hashtag/OpenJFX?src=hash&amp;ref_src=twsrc%5Etfw">#OpenJFX</a> <a target="_blank" href="https://twitter.com/hashtag/Rubik?src=hash&amp;ref_src=twsrc%5Etfw">#Rubik</a> <a target="_blank" href="https://t.co/oz4bJh6065">pic.twitter.com/oz4bJh6065</a></p>— José Pereda (@JPeredaDnr) <a target="_blank" href="https://twitter.com/JPeredaDnr/status/1319990311737765894?ref_src=twsrc%5Etfw">October 24, 2020</a>
  </blockquote>
 </div>
</figure>

Conclusion {#h2-5-conclusion}
-----------------------------

**JavaFX 3D really is a hidden gem**! I've been using JavaFX already for a long time now but wasn't aware of these 3D features... And the demos presented here really impressed me.

I didn't manage to get a running example myself yet on Raspberry Pi, but I hope to spend some time on this in the near future and combine it with some electronics to interact with the 3D environment. **To be continued...** 😉
