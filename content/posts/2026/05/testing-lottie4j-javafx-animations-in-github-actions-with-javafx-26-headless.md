---
title: "Testing Lottie4J JavaFX Animations in GitHub Actions Without a Display: JavaFX 26 Headless to the Rescue"
slug: "testing-lottie4j-javafx-animations-in-github-actions-with-javafx-26-headless"
date: "2026-05-13T07:35:26+00:00"
description: "When I released Lottie4J 1.1.0, I mentioned something a bit embarrassing in the release notes and this blog post: there was a new unit test to compare the - by Frank Delporte"
canonical: "https://webtechie.be/post/2026-04-20-lottie4j-unit-test-with-headless-javafx/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/04/manual-comparison.png"
categories:
  - "JavaFX"
tags:
related_posts:
enlighterjs: true
frozen: false
---

When I released [Lottie4J 1.1.0](https://lottie4j.com/releases/#2026-03-10-110), I mentioned something a bit embarrassing in the release notes and [this blog post](https://webtechie.be/post/2026-03-10-new-release-of-lottie4j/): there was a new unit test to compare the JavaFX player output against a JavaScript reference player, but it "*can not run on CI, because it requires a display output*." A TODO. A known limitation. One of those notes you write hoping future-you will figure it out.

[JavaFX 26 was released on March 17, 2026](https://gluonhq.com/javafx-26-is-now-available/) and includes a new headless platform, allowing me to get the test running on GitHub Actions without a display.

The Test, and Why It Mattered {#h2-0-the-test-and-why-it-mattered}
------------------------------------------------------------------

The core challenge with Lottie4J is correctness. The Lottie format is complex with a lot of nested data, and my JavaFX renderer has to produce output that matches what a JavaScript player would show. Pixel-perfect is too ambitious, but "is this a close enough match" is a reasonable bar.

During development, I use a separate application within the Lottie4J project: [LottieFileDebugViewer](https://github.com/lottie4j/lottie4j/blob/main/fxfileviewer/src/main/java/com/lottie4j/fxfileviewer/LottieFileDebugViewer.java). This is a JavaFX application that loads a Lottie file and renders it both with the JavaFX player, and inside a Webview with the official Lottie player. This makes it easy to compare the result and debug differences by diving into the data structure and different layers.
![](/images/posts/2026/05/testing-lottie4j-javafx-animations-in-github-actions-with-javafx-26-headless/manual-comparison-1024x342.png)

Based on this debug viewer, I created a unit-test approach with two steps:

1. A [WebViewScreenshotGenerator](https://github.com/lottie4j/lottie4j/blob/main/fxfileviewer/src/test/java/com/lottie4j/fxfileviewer/WebViewScreenshotGenerator.java) that I run once on my developer machine. It loads each animation in a JavaFX WebView using the LottieFiles JavaScript player, and captures screenshots of specific frames. These are the reference images and are committed to the repo.

2. The unit test [CompareFxViewWithWebViewTest](https://github.com/lottie4j/lottie4j/blob/main/fxfileviewer/src/test/java/com/lottie4j/fxfileviewer/CompareFxViewWithWebViewTest.java) then renders the same animations with the Lottie4J JavaFX player, takes screenshots at the same frames, and compares pixel data against the references.

The reference images are generated once and committed. The test just checks that the JavaFX output stays consistent with them. If something breaks in the renderer, the test will catch it.

This was all working fine locally. The problem was GitHub Actions. The CI runner has no display and no graphics stack. So I disabled this test for CI with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@DisabledIfEnvironmentVariable(named = "CI", matches = "true")</pre>

What Changed in JavaFX 26 {#h2-1-what-changed-in-javafx-26}
-----------------------------------------------------------

JavaFX 26 added a [Headless Platform Prototype](https://openjfx.io/highlights/26/) built directly into the `javafx.graphics` module. No extra dependencies, no native libraries, no Monocle setup. You pass a single JVM flag:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">-Dglass.platform=headless</pre>

That is it. JavaFX starts up, you get a functional toolkit, you can create scenes, render nodes, take snapshots, and run animations, all without a display attached. The [Gluon team](https://gluonhq.com/) did the heavy lifting on this for JavaFX 26, and it makes CI testing of JavaFX components much more practical. The flag works the same way as running your application normally. The difference is that there is nothing being drawn to a screen. For testing purposes, that is exactly what you want. It also opens the door to server-side rendering, for example, to generate a snapshot of a UI component without a display.

The Catch: JavaFX 26 Requires Java 24 {#h2-2-the-catch-javafx-26-requires-java-24}
----------------------------------------------------------------------------------

Lottie4J targets [Java 21 and JavaFX 21](https://github.com/lottie4j/lottie4j/blob/main/pom.xml#L38). That is the LTS version most projects are still running on. As this version is widely adopted, I don't want to force users of the library to jump to a newer version just because I want fancier test infrastructure. So the main project stays on 21 (for now).

But JavaFX 26 [requires Java 24 or higher](https://openjfx.io/highlights/26/) to run. They bumped the compiled bytecode level to `--release 24` in this release, so if you try to use it with an older JDK you get an error immediately. This means the test infrastructure has to use a different Java and JavaFX version than the main build. The solution I landed on was a Maven profile in the [root pom.xml](https://github.com/lottie4j/lottie4j/blob/main/pom.xml#L50) that overrides both version properties and configures the surefire plugin:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;profile&gt;
    &lt;!-- Activates JavaFX 26 headless windowing for unit tests in CI. --&gt;
    &lt;!-- Usage: mvn test -Pheadless-tests --&gt;
    &lt;id&gt;headless-tests&lt;/id&gt;
    &lt;properties&gt;
        &lt;java.version&gt;25&lt;/java.version&gt;
        &lt;javafx.version&gt;26&lt;/javafx.version&gt;
        &lt;surefire.argLine.headless&gt;
            -Dglass.platform=headless --enable-native-access=javafx.graphics
        &lt;/surefire.argLine.headless&gt;
    &lt;/properties&gt;
    &lt;build&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
                &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
                &lt;version&gt;3.0.0-M5&lt;/version&gt;
                &lt;configuration&gt;
                    &lt;argLine&gt;
                        --add-opens com.lottie4j.fxfileviewer/com.lottie4j.fxfileviewer=ALL-UNNAMED
                        -Dglass.platform=headless --enable-native-access=javafx.graphics
                    &lt;/argLine&gt;
                &lt;/configuration&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/build&gt;
&lt;/profile&gt;</pre>

When this profile is active, Maven bumps `java.version` to 25 and `javafx.version` to 26, so the dependency resolution picks up JavaFX 26 for the test classpath while the main source still compiles to Java 21 targets. The surefire plugin then passes two JVM arguments to the test JVM:

* `-Dglass.platform=headless` tells JavaFX to use the new headless glass backend instead of trying to connect to a display.
* `--enable-native-access=javafx.graphics` is required because the headless platform uses native code paths that the Java module system would otherwise block.

The `--add-opens` line gives the test runner access to the fxfileviewer module internals it needs to load and compare the rendered output.

The [fxfileviewer/pom.xml](https://github.com/lottie4j/lottie4j/blob/main/fxfileviewer/pom.xml#L95) and [fxplayer/pom.xml](https://github.com/lottie4j/lottie4j/blob/main/fxplayer/pom.xml#L52) pick up the overridden `javafx.version` property through normal Maven inheritance, so those modules automatically get JavaFX 26 on the test classpath when the profile is active.

The GitHub Actions Side {#h2-3-the-github-actions-side}
-------------------------------------------------------

The [Maven workflow](https://github.com/lottie4j/lottie4j/blob/main/.github/workflows/maven.yml#L26) sets up the environment with a Java 25 JDK so the JavaFX 26 runtime can load, and invokes Maven with the profile:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn test -Pheadless-tests</pre>

The rest of the build still compiles against Java 21 targets, so the library artifact itself is not affected. The profile only kicks in for the test run. The workflow does not need any display setup, no `Xvfb`, no `DISPLAY` environment variable tweaks. The headless flag handles all of that!

What This Actually Tests {#h2-4-what-this-actually-tests}
---------------------------------------------------------

The [unit test compares screenshots](https://github.com/lottie4j/lottie4j/blob/main/fxfileviewer/src/test/java/com/lottie4j/fxfileviewer/CompareFxViewWithWebViewTest.java) of Lottie animations rendered by the JavaFX player against the pre-generated reference images from the JavaScript player. It loads a set of known animation files, renders specific frames from each one, takes a snapshot using `WritableImage` and `SnapshotParameters`, and then does a pixel-level comparison with a configurable tolerance.

The result is a regression test that runs on every push. If someone changes the rendering logic in a way that visibly breaks an animation, CI will catch it. This is more useful than it sounds, because Lottie rendering involves a lot of layered transformations, easing functions, and shape operations where subtle bugs are easy to introduce.

Would I Recommend This Pattern? {#h2-5-would-i-recommend-this-pattern}
----------------------------------------------------------------------

Yes, with some caveats.

The version juggling is real work. If you want to use JavaFX 26 headless for testing while keeping your library on an older Java version, you need to be careful about separating the test JVM configuration from the main build. Maven makes this doable but not exactly elegant.

The reference image approach also requires discipline. The references need to be generated consistently, ideally on a reproducible setup, and you need to think about what tolerance makes sense for your comparisons. Too strict and you get flaky tests. Too loose and you miss real regressions.

But the payoff is real. The test that I had marked "can not run on CI" now runs on CI. No virtual framebuffer, no Docker tricks, no manual intervention. JavaFX starts up, renders the animations, and the comparison happens cleanly.

For any library that does visual rendering in JavaFX, this is the kind of testing infrastructure that was genuinely missing before. Good work, OpenJFX contributors!

*** ** * ** ***

Links:

* [Lottie4J website](https://lottie4j.com/)
* [Lottie4J on GitHub](https://github.com/lottie4j/lottie4j)
* [JavaFX 26 Highlights](https://openjfx.io/highlights/26/)
* [Gluon: Introducing the Headless Platform for JavaFX](https://gluonhq.com/introducing-the-headless-platform-for-javafx/)
* [Gluon: JavaFX 26 is Now Available](https://gluonhq.com/javafx-26-is-now-available/)
