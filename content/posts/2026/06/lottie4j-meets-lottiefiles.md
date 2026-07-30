---
title: "Lottie4J Meets LottieFiles: A Conversation with Naail Abdul Rahman"
slug: "lottie4j-meets-lottiefiles"
date: "2026-06-09T07:31:00+00:00"
description: "Lottie animations run on Android, iOS, and the web. Getting them working on the JVM is a different story. Lottie4J started as a question: can JavaFX - by Frank Delporte"
canonical: "https://webtechie.be/post/2026-06-04-interview-with-naail-from-lottiefiles/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/06/edit-lottiefiles-naail-scaled.jpg"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

Lottie animations run on Android, iOS, and the web. Getting them working on the JVM is a different story. [Lottie4J](https://lottie4j.com) started as a question: can JavaFX render them without a WebView? That question turned into a library with a first release in March 2026! Since then, I received the first pull requests, recently added headless unit testing with JavaFX 26, and now this: a video conversation with Naail from the LottieFiles team.

I got to sit down with [Naail Abdul Rahman](https://www.linkedin.com/in/kudanai/), R\&D engineer at LottieFiles, the company behind the Lottie animation platform. We talked about where the format came from, where it is going, and what that means for a Java implementation like Lottie4J.

{{< youtube R6k4NZ_SQqU >}}

What we talked about {#h2-0-what-we-talked-about}
-------------------------------------------------

The conversation covers a lot, so here is a quick overview of the topics if you want to jump to a specific part:

* 00:00 Introduction of Lottie and Naail
* 04:57 Use cases for Lottie animations, e.g. sprites in games, on ESP32,...
* 06:21 History of the Android Airbnb player
* 08:07 About Lottie4J, the Java(FX) player
* 10:31 Nice Lottie animation examples
* 14:44 Early history of Lottie, started as Bodymovin
* 15:54 .json Lottie versus .zip dotLottie formats
* 18:24 "Example Drive Development" of Lottie4J and headless unit testing with JavaFX 26
* 19:40 About the Lottie Animation Community (LAC)
* 22:43 Lottie format documentation with live examples
* 24:52 Taking a look at the web viewer and other tools on the LottieFiles website
* 27:20 What the LottieFiles Team Plan offers
* 29:07 Lottie animations can be exported from After Effects and Figma
* 30:07 Experiments with AI to generate animations
* 32:36 LottieFiles Marketspace with free available animations
* 33:26 What should be the next steps for Lottie4J?
* 35:50 What's on the LottieFiles roadmap
* 39:38 Can Lottie4J be validated to be compliant with the community specification?
* 41:47 Growing interest in Lottie for desktop applications (KDE)
* 42:53 Where to start if you're interested in Lottie
* 44:03 Pull requests received for Lottie4J
* 47:34 Looking into a pending Lottie4J issue
* 49:04 JavaFX gains more popularity as desktop applications become more important in AI use
* 50:05 Conclusion

Lottie: from Bodymovin to everywhere {#h2-1-lottie-from-bodymovin-to-everywhere}
--------------------------------------------------------------------------------

One thing I always find interesting about the Lottie format is that it did not start at LottieFiles. It started as [Bodymovin](https://aescripts.com/bodymovin/), an After Effects plugin for exporting animations to the web. Airbnb picked it up, built their Android player around it, and the name Lottie stuck. From there the format spread across platforms, communities, and now also into Java.

The open-source specification lives at [lottie.github.io](https://lottie.github.io), driven by the Lottie Animation Community (LAC). That is the reference Lottie4J works against.

dotLottie: the format worth paying attention to {#h2-2-dotlottie-the-format-worth-paying-attention-to}
------------------------------------------------------------------------------------------------------

Most Lottie work today happens with `.json` files. The dotLottie format (`.lottie`) is a ZIP container that can hold multiple animations, theming through slots, and interactive state machines, all in one file. It is where things are heading, and it is definitely what I need to add proper support for in Lottie4J.

The difference matters practically. A `.lottie` file compresses much better than the equivalent JSON, it can bundle assets cleanly, and the slots and states features open up real interactivity that a static JSON animation just cannot do.

What this means for Lottie4J {#h2-3-what-this-means-for-lottie4j}
-----------------------------------------------------------------

The conversation pushed me to think concretely about where to take the project next. Three areas stood out as important steps:

* Compliance checking against the LAC specification. Naail mentioned this is something the community wants more of. Knowing which features Lottie4J supports accurately gives the community a clear picture of where the project stands.
* Better dotLottie support, including multiple animations in a single file and eventually slots and state machines.
* The headless JavaFX 26 testing setup I recently documented is a real step forward for CI reliability. That blog post is [here](https://webtechie.be/post/2026-04-20-lottie4j-unit-test-with-headless-javafx/) if you want the details.

Another important moment in the conversation: Naail noted that interest in Lottie for desktop applications is growing, including from projects like KDE. JavaFX is in good company there! Even more right now, considering the recent announcement by Oracle that are now offering extended JavaFX support. This is something [Azul has done for years](https://www.azul.com/blog/the-javafx-revival-good-news-for-the-community-business-as-usual-for-azul/).

Links from the video {#h2-4-links-from-the-video}
-------------------------------------------------

* [Lottie format Wikipedia article](https://en.wikipedia.org/wiki/Lottie_(file_format))
* Lottie4J
  * [Lottie4J](https://lottie4j.com)
  * [Sources on GitHub](https://github.com/lottie4j/)
  * [Blog: Headless unit testing with JavaFX 26](https://webtechie.be/post/2026-04-20-lottie4j-unit-test-with-headless-javafx/)
* Lottie
  * [Open-source specification](https://lottie.github.io/)
  * [Animation Community issues and spec work](https://github.com/lottie/lottie.github.io/issues)
  * [Community implementations list](https://lottie.github.io/implementations/)
* LottieFiles:
  * [Free animations](https://lottiefiles.com/free-animations/loading-bar)
  * [Education resources](https://lottiefiles.com/education)
  * [Integrations page](https://lottiefiles.com/integrations#embed-lottie-libraries)
  * [Lottie JSON playground](https://lottiefiles.github.io/lottie-docs/playground/json_editor/)
* LottieFiles Case Studies:
  * [Walmart](https://lottiefiles.com/case-studies/Walmart)
  * [CNN Create](https://lottiefiles.com/case-studies/cnn-create)
  * [Robinhood](https://lottiefiles.com/case-studies/robinhood)
* [Glaxnimate (KDE animation tool)](https://apps.kde.org/glaxnimate/)
* [Compottie (Kotlin Multiplatform player)](https://github.com/alexzhirkevich/compottie)
* [ThorVG (C++ rendering library)](https://www.thorvg.org/)

Conclusion {#h2-5-conclusion}
-----------------------------

The conversation confirmed something I suspected: Lottie4J is not a niche experiment. Desktop animation is getting serious attention across the Java and Linux ecosystems. If you want to help shape where Lottie4J goes next, head over to [lottie4j.com](https://lottie4j.com). Pull requests, bug reports, and questions are all welcome in the comments of the video or in the [Lottie4J GitHub project](https://github.com/lottie4j/).
