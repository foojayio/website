---
title: "YOW! 2025"
date: "2025-12-17T08:59:55+00:00"
lastmod: "2026-09-21T05:59:35+00:00"
description: "I have been eyeing the YOW! conferences for probably more than a decade. They occur in Australia, and feature top industry experts. I was thus overjoyed…"
canonical: "https://blog.frankel.ch/yow-2025/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "Conference"
related_posts:
  - "why-is-my-talk-selected-reflections-from-a-program-committee-reviewer"
frozen: false
---

I have been eyeing the [YOW!](https://yowcon.com/) conferences for probably more than a decade. They occur in Australia, and feature top industry experts. I was thus overjoyed when they invited me to speak on the YOW! tour earlier this year. Here's a summary of my amazing time there.

## My participation

YOW! takes place in three different cities:\]Melbourne, Brisbane, and Sidney. I presented my brand new talk on [WebAssembly on Kubernetes](https://yowcon.com/melbourne-2025/sessions/3613/webassembly-on-kubernetes) in each city. It stems from an [earlier article](https://blog.frankel.ch/webassembly-kubernetes/), which I reworked and updated. YOW! will probably release the video soon.

In the meantime, here's the plan as a spoiler:

* Understanding WebAssembly
* WebAssembly in its own words
* Different actors of the WebAssembly-Cloud ecosystem
* The demo. In case you're interested, you can check the [GitHub repo](https://github.com/ajavageek/wasm-kubernetes), but note that the `README` is mostly for me.
* The good, the bad, and the ugly
* Conclusion

Regular readers of this blog know [my continued interest](https://blog.frankel.ch/tag/opentelemetry/) in [OpenTelemetry](https://opentelemetry.io/). I also had the pleasure of leading a one-day [masterclass on OpenTelemetry](https://yowcon.com/melbourne-2025/masterclasses/560/gain-practical-in-depth-experience-with-observability-using-opentelemetry) in Melbourne. OpenTelemetry is an amazing tool to observe your information system and get continuous insight from it.

In the class, I taught attendees OpenTelemetry by alternating between theory and four labs of real practice. At the end of the day, they had a good grasp of OpenTelemetry, how to generate metrics, logs, and traces across the JVM, Python, and NodeJS, and how to store these in the stack.

## Other talks

Beside my own talk and the masterclass, I also attended other speakers' talks. Here are some of them in no particular order, along with my summary and my opinion.

* Java Container Mastery: Optimizing Images Across Build Tools by [Matthias Haeussler](https://www.linkedin.com/in/matthiashaeussler/):

  Matthias is a good friend since a couple of years, and he told me we generally have very similar talks. This one follows the rule, but I wanted to check if things had changed since I presented on it. In this talk, he explains the different ways to create a JVM application in Docker:\]adding a JAR to a base JRE, using a multi-stage build, leveraging Cloud-Native buildpacks, and compiling to native with GraalVM.

  I advise you to watch the talk once YOW! releases it on its [YouTube channel](https://www.youtube.com/@GOTO-/videos), as it gives a good overview of all options, and their respective pros and cons.
* Pushing Java to the Limits: Processing a Billion Rows in Under 2 Seconds by [Roy van Rijn](http://www.royvanrijn.com):

  Last year, around Christmas, Gunnar Morling challenged Java developers. Given a file of one billion rows, how fast can you ingest it?\]In the talk, Roy described the challenge in details, as well as what its strategy was, and how well he fared. For a full description, or if you want to participate in the challenge for fun, check the [GitHub repo](https://github.com/gunnarmorling/1brc/blob/main/README.md).

  Fun fact: [Thomas Würthinger](https://thomaswue.dev/), of GraalVM fame, participated in the challenge. He came in second, so he hired the one developer who beat him.

  It wasn't mentioned in the talk, but I'm very happy that my former boss and friend, Jaromir Hamala, finished third.
* The Past, Present and Future of Programming Languages by [Kevlin Henney](https://about.me/kevlin):

  The talk lists "popular" languages according to a couple of reports, and proceeds to analyze how they found themselves in the top. Interestingly enough, there's quite a strong correlation between the age of a language and its place in the top 10. It seems that languages have inertia in the ecosystem.

  I never thought about this, but it's a very astute observation.
* Conceptualisation by [Michael Feathers](https://michaelfeathers.silvrback.com/):

  The speaker introduces a Payment class that conflates payment and tax responsibilities. Most developers would naturally split the class in two:\]one class for payment, and one for taxation. But perhaps the problem lies elsewhere?\]What if the issue was that the designer didn't find the right word to name the class?

  Follows very interesting thoughts on naming, semantics, and the lack of words to describe some concepts in certain languages. The speaker ended the talk with some consideration about LLMs, which I found much less interesting, but I understand the reasons behind the mention.
* The C4 Model - Beyond The Basics by [Simon Brown](https://simonbrown.je/):

  The C4 model is an architectural diagramming tool, invented by the speaker. I never used it myself, as UML covers more than most of my needs, but it's pretty popular. I wanted to check what usages the author recommended, and there were plenty.

  The speaker is soon to release a [book with O'Reilly](https://www.oreilly.com/library/view/the-c4-model/9798341660113/), so I won't disclose anything further.

Though I did attend other talks, I'm afraid I was too jet lagged to retain anything useful.

## Thanks

On top of the conference, the organizers made sure we tourists had tons of fun in additional activities.

{{< img src="koalas-kangaroos-383x510.jpg" class="aligncenter size-medium" alt="Koalas and kangaroos cross this road daily" width="383" height="510" >}}

I'd like to express my thanks to the amazing YOW! organizers, [Sabine Wolf](https://www.linkedin.com/in/sabine-s-wolf/), [Damian Maclennan](https://www.linkedin.com/in/damianmaclennan/), [Tracy Chen](https://www.linkedin.com/in/tracy--chen/), and all track hosts and volunteers. It was an amazing experience!

*Originally published at [A Java Geek](https://blog.frankel.ch/yow-2025/) on December 14^th^, 2025*

*[LGTM]: Loki Grafana Tempo Mimir
