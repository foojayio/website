---
title: "More Surfaces, Same Deal: Cars, Sensors, Commerce, Video And Builds"
slug: "more-surfaces-same-deal-cars-sensors-commerce-video-and-builds"
date: "2026-07-10T18:17:06+00:00"
description: "This week's release adds car dashboards, motion sensors, desktop-class input, foldables, Commerce, Secrets, versioned builds, and real video generation whi"
canonical: "https://www.codenameone.com/blog/one-codebase-more-surfaces/"
authors:
  - "shai-almog"
image: "one-codebase-more-surfaces.jpg"
categories:
  - "Java"
tags:
related_posts:
  - "own-your-pixels-native-fidelity-on-your-schedule"
  - "how-we-beat-hotspot-performance-by-cheating-but-not-like-that"
  - "funding-open-source-without-the-bait-and-switch-analytics-native-maps-tv-and-more"
  - "native-linux-apple-watch-a-game-builder-and-crash-protection"
frozen: false
---

![More Surfaces, Same Deal: Cars, Sensors, Commerce, Video And Builds](https://www.codenameone.com/blog/one-codebase-more-surfaces.jpg)

Last week's release post was about funding open source without the bait and switch. This week's release tests that idea again, because two of the new features touch paid infrastructure directly: Commerce and versioned builds.
| **What is Codename One?** Codename One is an open-source framework for building native iOS, Android, desktop, and web apps from a single Java or Kotlin codebase. Learn more at [codenameone.com](https://www.codenameone.com/).

That kind of expansion often makes the model slippery. A platform adds something developers need, puts it behind an account tier, and over time the open part gets worse or the paid part starts taxing success. We do not want to drift into that, so the useful question for this week is not just what shipped, but how the paid pieces behave.

Commerce validates purchases and normalizes entitlements, but it does not replace IAP and it does not take a cut. If quota runs out, validation degrades; the Apple or Google purchase still goes through. Everyone gets the Secrets API because volume stays low, and keeping keys out of source code should not be a luxury feature. Versioned builds are back, including limited access lower down the account ladder, and master builds give the community a way to verify fixes without waiting for nightly artifacts.

That is the continuation of last week's deal: charge for services that cost money to run, but keep them optional, predictable, and as broadly useful as we can make them.

The rest of the release pushes the open source core outward: car projection, motion sensors, richer pointer input, foldable posture, frame-accurate video, sample-accurate PCM mixing, and timed subtitles. A phone screen is still the center of most apps, but it is no longer the whole app. A serious product may also need a CarPlay list, an Android Auto grid, a stylus drawing surface, trackpad zoom, motion gestures, a pinned production build, and a generated video clip for sharing or support.

Here is what shipped.

## CarPlay and Android Auto

[PR #5281](https://github.com/codenameone/CodenameOne/pull/5281) adds a portable `com.codename1.car` API for Apple CarPlay and Google Android Auto. The important caveat is that car platforms are template-based. They do not allow an app to draw an arbitrary Codename One `Form` on the dashboard. You describe a driver-safe list, grid, message, pane, navigation, or now-playing template, and Codename One maps that to `CPTemplate` on CarPlay and `androidx.car.app` templates on Android Auto.

![CarPlay list template rendered by the Codename One car API](https://www.codenameone.com/blog/carplay-android-auto-codename-one/carplay-list.png)

![Android Auto grid template rendered by the Codename One car API](https://www.codenameone.com/blog/carplay-android-auto-codename-one/android-auto-grid.png)

The API is zero cost when unused. Referencing `com.codename1.car` is what tells the build to inject CarPlay scenes, entitlements, Android Auto services, and the AndroidX dependency. Apps that never touch the package do not carry that code. Tomorrow's post walks through the template model, the simulator head unit, and the approvals you still need from Apple and Google.

## Motion, Input, And Real Hardware

[PR #5310](https://github.com/codenameone/CodenameOne/pull/5310) adds `com.codename1.sensors`, a cross-platform motion API with accelerometer, gyroscope, magnetometer, derived gravity, linear acceleration, orientation, and common gestures such as shake, flip, tilt, pick up, and free fall. This replaces the old external `sensors-codenameone` cn1lib with a core API and a core gesture engine.

[PR #5309](https://github.com/codenameone/CodenameOne/pull/5309) fills in the other side of modern hardware: rich pointer events, mouse buttons, wheel and trackpad scrolling, stylus pressure and tilt, foldable posture, desktop windowing, and external display awareness.

Together they make Codename One less phone-only. A canvas can tell the difference between a finger, a mouse, and a stylus. A foldable can split layout around a hinge. A trackpad pinch can zoom the same component as a mobile pinch. Sunday's post covers the full hardware story.

## Commerce And Secrets

[PR #5300](https://github.com/codenameone/CodenameOne/pull/5300) adds the Commerce SDK and the Secrets API.

Commerce is optional infrastructure around IAP. It answers the backend question apps usually end up writing themselves: "does this user have this entitlement right now?" It can validate receipts, normalize subscription state across Apple and Google, send lifecycle webhooks to your backend, and show revenue metrics. It does not replace `Purchase`, it delegates to it.

```java
CommerceManager cm = CommerceManager.getInstance();
cm.setAppUserId(accountId);
cm.subscribe("pro_monthly");

new Thread(() -> {
    cm.refresh();
    if (cm.isEntitled("pro")) {
        unlockProFeatures();
    }
}).start();
```

The Secrets API has deliberately lower volume and is enabled for everyone. It fetches app-readable secrets from the cloud vault at runtime and caches them in `SecureStorage`, so API keys do not need to live in code or in the binary. Server-only credentials, such as App Store or Google Play keys used by commerce validation, stay server-side and are not served to the app.

Monday's post is mostly a failure-mode pass over Commerce: what it does, what it refuses to do, what happens when quota is exhausted, and why optional validation is not an IAP tax.

![Commerce dashboard for receipt validation and entitlement tracking](https://www.codenameone.com/blog/commerce-secrets-without-iap-tax/commerce.png)

![Secrets dashboard for cloud-managed app secrets](https://www.codenameone.com/blog/commerce-secrets-without-iap-tax/secrets.png)

## Versioned Builds Are Back

Versioned builds are back, and the model is better suited to Maven than the old Ant-era point release scheme. You can pin a cloud build to a released Codename One version, or build against `master` when you need to verify an unreleased fix.

```properties
build.cn1Version=master
```

Community developers recently asked for nightly or daily builds. Building against master solves the same verification problem without waiting for an artificial daily package. You are asking the build server to use the current development head.

Subscription tiers limit versioned build access because old versions create support churn, not because fetching a few artifacts drives the business model. The further back a build goes, the harder it is to diagnose a regression against the current code. The new model still opens versioned builds much further down the account ladder than before, including limited access for basic/free usage, while giving paying teams the longer support windows they actually need. Tuesday's post covers the tradeoff.

## Video, Audio, And Subtitles

[PR #5315](https://github.com/codenameone/CodenameOne/pull/5315) adds `VideoIO`: cross-platform video encode and frame-accurate decode using native codecs. It can encode app-rendered frames plus audio into MP4/WebM/MOV/MKV style containers, enumerate codecs, decode exact frames, resample variable-frame-rate clips, and expose the audio track as PCM.

[PR #5317](https://github.com/codenameone/CodenameOne/pull/5317) adds a sample-accurate `AudioMixer` for combining PCM tracks on one clock. [PR #5319](https://github.com/codenameone/CodenameOne/pull/5319) adds timed Whisper transcription, so generated videos can get SRT or VTT captions instead of plain text transcripts.

![Frame-accurate VideoIO decode output from the Codename One test app](https://www.codenameone.com/blog/videoio-audio-mixer-whisper/videoio-decoded-frames.png)

Wednesday's post shows how these pieces fit: render frames, mix audio, encode a video, decode frames back out, and attach timed captions.

## Smaller But Important

There are four smaller changes worth calling out:

* **JavaSE upstream JCEF and bundled ffmpeg media.** [PR #5294](https://github.com/codenameone/CodenameOne/pull/5294) moves the simulator away from our home-built Chromium 84 CEF fork and onto upstream prebuilt JCEF. We used to build our own CEF fork mostly to keep H.264 media working. That build was long, fragile and kept us behind. The new approach uses upstream JCEF for the browser and bundled ffmpeg for the Codename One `Media` API.
* **Skin Designer now builds with our JavaScript port.** [PR #5303](https://github.com/codenameone/CodenameOne/pull/5303) moves Skin Designer to the local ParparVM JavaScript target. Initializr and Playground already moved; the console is now the last TeaVM internal tool. This migration did expose a regression, but the JavaScript port is still moving toward production.
* **MorphTransition grew up.** [PR #5314](https://github.com/codenameone/CodenameOne/pull/5314) adds opacity, rotation, scale, deterministic scrubbing, and arbitrary rendered elements. This matters for UI polish and also for frame export, because scrubbing lets the same transition be rendered into generated video.
* **Timed Whisper transcription.** [PR #5319](https://github.com/codenameone/CodenameOne/pull/5319) also deserves a second mention because timed segments turn transcription into subtitles. The cn1lib can render SRT and VTT payloads from segment timestamps.

## Upcoming Posts

* **Saturday.** . PR [#5281](https://github.com/codenameone/CodenameOne/pull/5281).
* **Sunday.** . PRs [#5310](https://github.com/codenameone/CodenameOne/pull/5310) and [#5309](https://github.com/codenameone/CodenameOne/pull/5309).
* **Monday.** . PR [#5300](https://github.com/codenameone/CodenameOne/pull/5300).
* **Tuesday.** .
* **Wednesday.** . PRs [#5315](https://github.com/codenameone/CodenameOne/pull/5315), [#5317](https://github.com/codenameone/CodenameOne/pull/5317), and [#5319](https://github.com/codenameone/CodenameOne/pull/5319).
* **Thursday.** . The final part of the Game Builder tutorial series.

## Wrapping Up

The release is large because the app surface is larger now. It reaches cars, foldables, desktops, sensors, stylus devices, media pipelines, cloud validation, and older build timelines.

The business side needs the same discipline as the API side. Cloud validation should help, not hold purchases hostage. Build tiers should buy capacity and support windows, not a license to keep your own revenue. Optional should mean optional, especially when the project stays open source.
