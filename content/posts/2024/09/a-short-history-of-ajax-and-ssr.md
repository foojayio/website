---
title: "A Short History of AJAX and SSR"
slug: "a-short-history-of-ajax-and-ssr"
date: "2024-09-23T06:57:01+00:00"
lastmod: "2024-09-24T11:50:58+00:00"
description: "My journey in programming began over two decades ago, a time when JavaScript was a far cry from its current state, and developers were primarily focused on Microsoft Internet Explorer."
canonical: "https://blog.frankel.ch/ajax-ssr/1/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/09/book-8858593.jpg"
categories:
  - "Java"
tags:
related_posts:
frozen: false
---

**My journey in programming began over two decades ago, a time when JavaScript was a far cry from its current state, and developers were primarily focused on Microsoft Internet Explorer. One of my proudest achievements back then was writing a few lines of code that allowed users to add and remove table rows entirely on the client side. We called it . Many developers today have forgotten about it---or never knew it existed.**

A few years later, emerged, revolutionizing the way we approached web development. The emergence of AJAX marked a significant shift in web development, transferring more logic from the server to the client, and this shift was not without reason.

Client-Side Rendering {#h2-0-client-side-rendering}
---------------------------------------------------

This shift gained momentum due to two key factors: advancements in the JavaScript language and improvements in browser capabilities. JavaScript modules, for example, have greatly enhanced the separation of concerns, leading to more maintainable code. Meanwhile, the introduction of the Local Storage API has been a game-changer.

Client-side rendering brings a host of benefits, with improved responsiveness being the most significant one, greatly enhancing the user experience. Keeping interactions local and avoiding server roundtrips makes the user experience noticeably faster.

I mentioned the Local Storage API earlier. Along with Web Workers, it enables offline functionality---something browsers previously couldn't provide. I recall developing with [Java Web Start](https://openwebstart.com/) (now OpenWebStart), downloading an application with server connectivity, and marvelling at how it worked offline. At the time, it was one of the few technologies offering this capability. Now, we have similar functionality directly in the browser!

Another advantage of migrating logic to the user's machine is reducing server computational load. This not only improves performance but also reduces cloud costs.

Some Organizational Insights {#h2-1-some-organizational-insights}
-----------------------------------------------------------------

In parallel with these technical advancements, mobile devices rose to prominence. Many companies split their applications into client web apps and server APIs to adapt. With the advent of native applications, this approach became the norm and is often taken for granted.

This separation of concerns has profound implications. Given the rapid pace of client-side technology changes and their growing complexity, it's nearly impossible for a single developer to handle an entire use case from end to end. Consequently, most organizations have split their developers into front-end and backend teams. Each team operates in its domain, moving quickly within that space. But when it comes time to integrate, glitches arise, leading to adjustments, *a.k.a.*, bug fixes. This process can be cumbersome, as it requires identifying where the issue lies and assigning it to the appropriate team. And let's not even get into the blame game that can ensue.

This artificial separation between client and server is an unnecessary hurdle for many small to mid-sized organizations. A simple webapp with well-adjusted CSS is often more than sufficient. That is why I'm a big fan of the Vaadin framework. With Vaadin, your developers only need to learn a single technology stack using one programming language and a small set of APIs. Each developer can work on both the backend and the UI, making the process simpler and more cost-effective. Yet, like with microservices, the herd mentality has been strong.

The Rise of Server-Side Rendering {#h2-2-the-rise-of-server-side-rendering}
---------------------------------------------------------------------------

As with any new technology, early adopters jump on the bandwagon, the technology gains traction, and issues arise over time. Client-side rendering is no different.

As more code moved to the client side, some software began to hit the limits of what browsers could handle despite their improvements. Before it could start rendering the page, the browser had to download all necessary assets---primarily JavaScript libraries. To mitigate this, we minified libraries and increased the number of parallel downloads. At one point, we even resorted to creating artificial subdomains because browsers were heavily limited in the number of parallel downloads from a single domain.

We developed complex techniques to trick users into thinking the page had loaded quickly, even if it wasn't fully rendered. It involved managing only the visible portion of the page initially and deferring the rest until necessary. Many of these techniques also depend on the browser's engine, which changes frequently. It led to a rise in "cargo cult" programming and black magic in what was supposed to be engineering.

Another significant issue was . Bots that crawl and index pages were designed for a more straightforward Web, where pages were rendered server-side. While Google and others have made strides in improving JavaScript-aware bots, nothing beats server-side rendering for SEO.

Finally, server-side rendering improves initial load times and simplifies development organization.

We must recognize the benefits that client-side rendering offers, but perhaps the pendulum has swung too far. Is it possible to have the best of both worlds? In some corners of the industry, cooler heads have prevailed, and the term has been coined to describe a return to what we've been doing for ages---albeit with some modern enhancements. The idea is to leverage AJAX, JavaScript, and browser improvements without the unnecessary bloat. While many tools are available, I frequently hear about [Vue. js](https://vuejs.org/) and [HTMX](https://htmx.org/). A recent search also led me to [Alpine.js](https://alpinejs.dev/). And I've long been a proponent of [Vaadin](https://vaadin.com/).

I plan to explore these technologies in this focused series by implementing a small to-do application with each. Here are my requirements:

* I'll approach this from the perspective of a backend developer.
* No front-end build steps: no TypeScript, no minification, etc.
* The backend app manages all dependencies, *i.e.*, Maven.

**To go further:**

* [DHTML](https://en.wikipedia.org/wiki/Dynamic_HTML)
* [AJAX](https://en.wikipedia.org/wiki/Ajax_(programming))
* [SSR](https://en.wikipedia.org/wiki/Server-side_scripting)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/1/) on September 8^th^, 2024*

*[SSR]: Server-Side Rendering
*[SEO]: Search Engine Optimization
*[AJAX]: Asynchronous JavaScript and XML
*[DHTML]: Dynamic HTML
