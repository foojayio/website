---
title: "Debugging BentoFX in MelodyMatrix with Matt Coley, Scenic View, and an Honest Look at AI-Generated Code"
date: "2026-05-15T10:02:40+00:00"
description: "There are bugs you can solve by yourself, and bugs where you just need to sit down with someone who knows the internals. This video is in the second…"
canonical: "https://webtechie.be/post/2026-04-21-improving-melodymatrix-ui-with-bentofx/"
authors:
  - "frankdelporte"
image: "edit-melodymatrix-bentofx.jpg"
categories:
  - "JavaFX"
related_posts:
  - "video-series-javafx-in-action-part-6"
  - "javafx-links-of-october-2025"
  - "foojay-podcast-54"
  - "melodymatrix-v1-0-0-released-shipping-a-javafx-app-with-jdeploy-github-actions-and-auto-update"
frozen: false
---

There are bugs you can solve by yourself, and bugs where you just need to sit down with someone who knows the internals. This video is in the second category. [MelodyMatrix](https://melodymatrix.rocks/) uses [BentoFX](https://github.com/Col-E/BentoFX) for its dockable panel layout. Branches, leaves, tabs on the side, content panels that open and close. It works well until something fights the layout. But I had some visual problems I could not explain, some code that felt more complicated than it should be, and no good explanation for why.

{{< youtube grwzIWWZMNw >}}

So I asked [Matt Coley](https://www.coley.software/) if he had time to take a look. Matt is the creator of BentoFX, but he is also known for [Recaf](https://www.jfx-central.com/showcases/recaf), a bytecode editor for Java that itself uses BentoFX heavily as its UI framework. That means when Matt looks at a BentoFX integration. He wrote the library and uses it heavily in his own project. If you want the full background on his work, there is an earlier interview: [JavaFX In Action #22 with Matt Coley](https://webtechie.be/post/2025-10-30-jfxinaction-matt-coley-recaf-bentofx-treemapfx-glcanvasfx/).

## What BentoFX Actually Does

BentoFX is a docking and layout library for JavaFX. The core idea is that you have a tree of containers: branches split the space horizontally (or vertically if you rotate them), and leaves hold the actual content. Tabs let you stack multiple panels in one leaf.

In MelodyMatrix, this maps to a sidebar on the left, a main content area in the middle, and additional panels on the right. It is a clean model when you understand it, but there are some behaviors that are not immediately obvious. Pruning is one of them: when you close a panel, BentoFX removes the empty container and reorganizes the remaining branches. That is usually what you want, but if you are also managing widths or visibility yourself in code, things start to conflict. That was part of my problem.

## Scenic View: Browser DevTools for JavaFX

This session reminded me that [Scenic View](https://www.jfx-central.com/tools/scenicview) exists and I should have been using it much earlier.

If you have done any web development, you know how useful browser inspect tools are. You hover over an element, you can see exactly what CSS is applied, what the padding is, why something is shifted by 12 pixels. Scenic View does the same thing for a running JavaFX application. It attaches to your running application and shows the scene graph live with all the layout properties.

We pulled it during the video to look at the tab header rotation issue I was having. Instead of guessing which CSS rule was causing the offset, we could see exactly what was happening in the layout tree. Embarrassingly, I had not used it before this session. It should be in your JavaFX debugging toolkit!

## Some Honest Thoughts on AI-Generated Code

We also spent some time looking at code that had been written or modified by Claude and Copilot. Matt spotted it quickly: the code had the patterns you recognize after reviewing AI-generated output. The rotation fix for the tab headers was one example. The code did technically work, but it was fragile: it used CSS class lookups that would break if BentoFX changed anything internally. Matt's take was pragmatic: if it works now, and you have a way to update it when it breaks, fine. But it is worth knowing that is what you are dealing with.

The broader cleanup was straightforward once Matt explained how BentoFX manages its own leaf widths. I had added animation and width-tracking code essentially fighting the library. Removing it simplified things considerably and fixed one of the issues in the process. Not all AI-generated code is bad. But a code review from someone who knows the library beats prompting your way through it. This session was a good reminder of that.

## We Might Have Found a Bug

BentoFX did something unexpected with divider modes when re-opening a panel that had been closed. Matt looked at his own source and said he suspected something was not right there. It is a 0.x library, but it is actively maintained and used in production in Recaf. Matt's response was immediate: file a ticket with reproduction steps.

* 00:00 Introduction: Matt Coley, Recaf, BentoFX
* 02:19 How BentoFX is used in MelodyMatrix
* 04:18 Visual problems with the BentoFX integration
* 06:33 A look into the MelodyMatrix code
* 09:15 Changing tab position from top to side, and why the project uses Kotlin
* 16:25 Using Scenic View to debug a JavaFX layout live
* 22:38 How pruning affects visual BentoFX components
* 34:53 Cleaning up unneeded code: let BentoFX handle leaf widths, removing animations
* 41:33 We probably found a bug in BentoFX, and a look into the BentoFX source

<!-- -->

* [JavaFX In Action #22 with Matt Coley, diving into bytecode and JARs with Recaf and JavaFX libraries](https://webtechie.be/post/2025-10-30-jfxinaction-matt-coley-recaf-bentofx-treemapfx-glcanvasfx/)
* Matt Coley:
  * [GitHub](https://github.com/Col-E)
  * [Website](https://www.coley.software/)
  * [JFX Central](https://www.jfx-central.com/people/m.coley)
* JavaFX libraries by Matt:
  * [BentoFX](https://github.com/Col-E/BentoFX)
  * [TreeMapFX](https://github.com/Col-E/TreeMapFX)
  * [GLCanvasFX](https://github.com/Col-E/GLCanvasFX)
* On JFX Central:
  * [BentoFX](https://www.jfx-central.com/libraries/bentofx)
  * [Recaf](https://www.jfx-central.com/showcases/recaf)
  * [PDFViewFX](https://www.jfx-central.com/libraries/pdffx)
  * [Scenic View](https://www.jfx-central.com/tools/scenicview)
* MelodyMatrix:
  * [Website](https://melodymatrix.rocks/)
  * [Open-source viewers on GitHub](https://github.com/codewriterbv/melodymatrix-app-views)
