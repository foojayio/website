---
title: "Lottie4J 1.2.0: dotLottie Support, Marker Playback, Cropping, and a Big Speed Boost"
date: "2026-03-24T07:51:00+00:00"
description: "Version 1.2.0 of Lottie4J is out, and it's again a big release! The headline feature is support for the .lottie container format, but that's just the…"
authors:
  - "frankdelporte"
image: "lottie4j-v1.2.0-scaled.jpg"
categories:
  - "JavaFX"
related_posts:
  - "lottie4j-meets-lottiefiles"
  - "closing-the-visual-gap-between-the-official-lottie-webplayer-and-lottie4j"
  - "lottie4j-1-1-0-better-rendering-smarter-debugging"
  - "javafx-links-of-july-2026"
frozen: false
---

Version 1.2.0 of [Lottie4J](https://lottie4j.com) is out, and it's again a big release! The headline feature is support for the `.lottie` container format, but that's just the start. This release also brings marker-based playback, cropping, adaptive rendering, significant performance improvements, and a lot of core model fixes driven by testing more complex real-world animations.

{{< youtube 9lE6UO8XNpU >}}

## dotLottie File Support

Until now, Lottie4J only supported the plain JSON format (`.json`). That's the original Lottie format, but LottieFiles also introduced a newer container format: `.lottie`. It's essentially a ZIP archive that can hold one or more animations, embedded images, sounds, and a manifest file that describes the contents.

The `.lottie` format is a practical improvement: it makes Lottie files smaller and self-contained, bundling images and other assets alongside the animation data instead of relying on external references. Lottie4J now supports loading `.lottie` files directly via the core `FileLoader`. There are two loading modes available:

```
// Load the first animation from a .lottie file (simplest path)
LottieAnimation animation = FileLoader.load(path);

// Load the full .lottie container to access the manifest and all animations
DotLottie lottie = FileLoader.loadDotLottie(path);
```

The `Lottie` object gives you access to the manifest (author, version metadata) and the full list of animations. At the moment, most real-world `.lottie` files I found, only bundle a single animation, but the API is ready for multi-animation files when they appear.

## New Player Features

### Play Between Markers

Lottie animations can embed named markers. Those are timestamp labels inside the animation JSON that indicate points of interest or looping regions. This is a feature Lottie4J previously parsed but didn't expose in the player. Now it does.

The new `play(startMarker, endMarker)` method makes the player start at frame 1, and then loop between the two named positions in the timeline.

### Cropping Support

`LottiePlayer` now supports cropping via `crop(top, right, bottom, left)`. This lets you clip the rendered output to a sub-region of the animation canvas, which is handy when you want to embed only part of an animation into a layout without modifying the original file.

### Resizable Player

`LottiePlayer` is now properly resizable and adjusts its rendering accordingly. Resizing also has a secondary effect: smaller sizes reduce the rendering load, so you can trade size for performance when needed.

## Performance Improvements

Rendering speed has improved significantly in this release. The main gains come from reducing the number of rendering passes per frame and adding a caching layer for layer and precomp render metadata, which avoids redundant recalculation on heavy animations.

The difference is measurable: one test animation that previously played back at around 20--30 FPS now runs at \~50 FPS. A heavier animation that struggled at 11 FPS now reaches \~31 FPS at full size, and scales higher as the player is resized smaller.

### Adaptive Rendering Mode

A new adaptive rendering toggle `setAdaptiveOffscreenScalingEnabled(enabled)` trades rendering sharpness for speed. In adaptive mode, JavaFX uses a faster rendering path that can introduce slight blurring on some elements (particularly sharp text or fine detail at certain sizes). In non-adaptive mode, rendering is pixel-precise but slower.

Which mode works better depends on the animation: simpler animations often look fine in adaptive mode and benefit from the speed, while text-heavy or detail-rich animations are better left in the default mode. The toggle is exposed in both the `LottiePlayer` and the file viewers so you can test your specific animations.

## Core Model Improvements

A lot of work went into the core library this release, driven by testing more complex real-world Lottie files:

* **Merge and modifier shape support**: a shape layer feature used in more complex animations.
* **Blend mode support**: layers can now carry blend mode instructions that affect compositing.
* **Spatial bezier interpolation**: position animations now correctly interpolate using bezier curves rather than linear paths, which produces the characteristic easing motion that makes Lottie animations feel polished.
* **Missing model fields**: mask, layer, and animation objects had gaps that caused incorrect rendering on specific files, these are now filled in.
* **Improved JSON output**: when recreating a Lottie JSON from the model (write path), value ordering is now more consistent, and the reconstructed file more closely matches the original.

### Jackson 3 Upgrade

The core library has been upgraded to Jackson 3. This is a major version bump: the group ID changes from `com.fasterxml.jackson` to `tools.jackson`, so if you depend on the core directly and also pull in Jackson yourself, you'll want to align on version 3. The upgrade also includes CVE-related dependency updates and follow-up compatibility fixes that came out of the migration.

Note that `jackson-annotations` has not yet moved to the `tools.jackson` group ID and remains on its previous coordinates for now.

## Debug Tooling Updates

The `LottieFileDebugViewer` has been refactored to extract the duplicated WebView JavaScript bridge code into reusable components. The FX versus JS side-by-side views are improved, and the comparison test has better frame synchronization. A new validation mode tests the player at a resized dimension to verify that rendering holds up under scaling.

The automated `CompareFxViewWithWebViewTest`, which renders both the JavaFX and JavaScript players frame by frame and checks visual similarity, now also uses `.lottie` files.

## Trying It Out

Update your Maven dependency:

```
<!-- Just the core model, no player -->
<dependency>
    <groupId>com.lottie4j</groupId>
    <artifactId>core</artifactId>
    <version>1.2.0</version>
</dependency>

<!-- JavaFX player -->
<dependency>
    <groupId>com.lottie4j</groupId>
    <artifactId>fxplayer</artifactId>
    <version>1.2.0</version>
</dependency>
```

The full list of changes is [available on GitHub](https://github.com/lottie4j/lottie4j/compare/v1.1.0...v1.2.0).

## What's Next

The automated comparison test still can't run on GitHub Actions because it requires a display. JavaFX 26 (released alongside Java 26 this week) includes headless rendering support, which may make it possible to run the visual regression tests on GitHub Actions. That's the next thing to investigate...

As always: if you run into a Lottie file that doesn't render correctly, please open an issue and attach screenshots. The more real-world files get tested, the better the library gets.

* Website: [lottie4j.com](https://lottie4j.com)
* GitHub: [github.com/lottie4j/lottie4j](https://github.com/lottie4j/lottie4j)
* Release notes: [lottie4j.com/releases](https://lottie4j.com/releases/index.html)
