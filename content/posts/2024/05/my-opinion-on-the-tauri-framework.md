---
title: "My opinion on the Tauri framework"
slug: "my-opinion-on-the-tauri-framework"
date: "2024-05-30T16:05:06+00:00"
lastmod: "2024-05-30T16:06:08+00:00"
description: "Tauri is a Rust-based framework for building desktop applications. Here's my view."
canonical: "https://blog.frankel.ch/opinion-tauri/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/05/header_light.png"
categories:
  - "Opinion"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**I've always liked , both desktop-based and browser-based before you needed five years of training on the latter. That's the reason I loved, and still love [Vaadin](https://vaadin.com/): you can develop web UIs without writing a single line of HTML, JavaScript, and CSS. I'm still interested in the subject; a couple of years ago, I analyzed the [state of JVM desktop frameworks](https://blog.frankel.ch/focus/state-jvm-desktop-frameworks/).**

I also like the Rust programming language a lot.

Tauri is a Rust-based framework for building desktop applications. Here's my view.

Overview {#h2-0-overview}
-------------------------

> Build an optimized, secure, and frontend-independent application for multi-platform deployment.
>
> -- [Tauri website](https://tauri.app/)

A Tauri app is composed of two modules: the client-side module in standard Web technologies (HTML, Javascript, and CSS) and the backend module in Rust. Tauri runs the UI in a dedicated Chrome browser instance.

Users interact with the UI as usual. Tauri offers a binding between the client-side JavaScript and the backend Rust via a specific JS module, *i.e* , `window.__TAURI__.tauri`. It also offers other modules for interacting with the local system, such as the filesystem, OS, clipboard, window management, etc.

Binding is based on strings. Here's the client-side code:

<pre class="EnlighterJSRAW" data-enlighter-language="js">const { invoke } = window.__TAURI__.tauri;

let greetInputEl;
let greetMsgEl;

greetMsgEl.textContent = await invoke("greet", { name: greetInputEl.value });  //1</pre>

1. Invoke the Tauri command named `greet`

Here's the corresponding Rust code:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">#[tauri::command]                                                              //1
fn greet(name: &amp;str) -&gt; String {                                               //1
    format!("Hello, {}! You've been greeted from Rust!", name)
}</pre>

1. Define a Tauri command named `greet`

In the following sections, I'll list Tauri's good, meh, and bad points. Remember that it's my subjective opinion based on my previous experiences.

The good {#h2-1-the-good}
-------------------------

* Getting started:Fortunately, it is becoming increasingly rare, but some technologies need to remember that before you're an expert, you're a newbie. The first section of any site should be a quick explanation of the technology, and the second a getting started. Tauri succeeds in this; I got my first Tauri app in a matter of minutes by following the [Quick start](https://tauri.app/v1/guides/getting-started/setup/) guide.
* Documentation:Tauri's documentation is comprehensive, extensive (as far as my musings browsed them), and well-structured.
* Great feedback loop:I've experienced exciting technologies where the feedback loop, the time it takes to see the results of a change, makes the technology unusable. [GWT](https://www.gwtproject.org/), I'm looking at you. Short feedback loops contribute to a great developer experience.

  In this regard, Tauri scores points. One can launch the app with a simple `cargo tauri dev` command. If the front end changes, Tauri reloads it. If any metadata changes, *e.g.* , anything stored in `tauri.conf.json`, Tauri restarts the app. The only downside is that both behaviors lose the UI state.
* Complete lifecycle management:Tauri doesn't only help you develop your app, it also provides the tools to [debug](https://tauri.app/v1/guides/debugging/application/), [test](https://tauri.app/v1/guides/testing/mocking), [build](https://tauri.app/v1/guides/building/windows), and [distribute](https://tauri.app/v1/guides/distribution/publishing) it.

The meh {#h2-2-the-meh}
-----------------------

At first, I wanted to create my usual showcase for desktop applications, a [file renamer app](https://github.com/ajavageek/renamer-swing). However, I soon hit an issue when I wanted to select a directory using the file browser button. First, Tauri doesn't allow to use the regular JavaScript file-related API; Instead, it provides a more limited API. Worse, you need to explicitly configure which file system paths are available at build time, and they are part of an enumeration.

I understand that security is a growing concern in modern software. Yet, I fail to understand this limitation on a desktop app, where every other app can access any directory.

The bad {#h2-3-the-bad}
-----------------------

However, Tauri's biggest problem is its design, more precisely, its separation between the front and the back end. What I love in Vaadin is its management of all things frontend, leaving you to learn the framework only. It allows your backend developers to build web apps without dealing with HTML, CSS, and JavaScript.

Tauri, though a desktop framework, made precisely the opposite choice. Your developers will need to know frontend technologies.

Worse, the separation reproduces the request-response model created using browser technologies to create UIs. Reminder: early desktop apps use the [Observer model](https://en.wikipedia.org/wiki/Observer_pattern), which better fits user interactions. We designed apps around the request-response model only after we ported them on the web. Using this model in a desktop app is a regression, in my opinion.

Conclusion {#h2-4-conclusion}
-----------------------------

Tauri has many things to like, mainly everything that revolves around the developer experience. If you or your organization uses and likes web technologies, try Tauri.

However, it's a no-go for me: to create a simple desktop app, I don't want to learn how to center a `div` or about the flexbox layout, etc.

**To go further:**

* [Tauri](https://tauri.app/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/opinion-tauri/) on May 12^th^, 2024*

*[GUI]: Graphical User Interface
