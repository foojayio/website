---
title: "Summary of the AJAX Frameworks Comparison"
slug: "summary-of-the-ajax-frameworks-comparison"
date: "2024-11-14T11:35:50+00:00"
lastmod: "2024-11-15T11:23:56+00:00"
description: "In previous weeks, I've analyzed several libraries and frameworks that augment the client with AJAX capabilities."
canonical: "https://blog.frankel.ch/ajax-ssr/7/"
authors:
  - "nicolas-frankel"
image: "technology-7111798.jpg"
categories:
  - "Vaadin"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "guide-lazyinitializationexception"
  - "a-list-of-cache-providers"
  - "foojay-podcast-41"
frozen: false
---

In previous weeks, I've analyzed several libraries and frameworks that augment the client with AJAX capabilities.

* [Vue.js](https://blog.frankel.ch/ajax-ssr/3/)
* [Alpine.js](https://blog.frankel.ch/ajax-ssr/4/)
* [HTMX](https://blog.frankel.ch/ajax-ssr/5/)
* [Vaadin](https://blog.frankel.ch/ajax-ssr/6/)

In this post, I'll compare them across several axes.

Analysis
--------

* Frontend skills:Remember that I started this series from the point of view of a backend developer. In this section, I grade how much you need to know about client technologies to complete the job.
* Team organization:In the introduction, I hinted that the decoupling of frontend and backend teams profoundly impacted projects. Each team is fast on its own, and they can parallelize their work, but integrating the two can double the initial development time. Here, I grade how easy it is to integrate frontend and backend.
* Ease of setup:How easy it is to set up the tool from the backend.
* Ease of styling:Backend developers are not designers by default. Does the tool offer a default, at least average-looking style? How hard is it to create one?

For all intents and purposes, Vue.js and Alpine.js are similar; I'll refer to them as JavaScript frameworks.

|                   |                       JavaScript frameworks                        |                               HTMX                               |                                                                                                                                                                                                                                                     Vaadin                                                                                                                                                                                                                                                      |
|  Frontend skills  | Need the full HTML, JavaScript (it's in the name), and CSS tryptic | Only need HTML and CSS, HTMX takes care of the JavaScript burden |                                                                                                                                                                                                                           No frontend skills needed, Vaadin takes care of everything                                                                                                                                                                                                                            |
| Team organization | Depends on each developer's skills: * Either separation between frontend and backend development * Or they can develop their use case from the database to the UI ||                                                                                                                                                                                                                      Each developer can develop their use case from the database to the UI                                                                                                                                                                                                                      |
|   Ease of setup   | * Thanks to WebJars, you can manage dependencies in the POM * WebJars Locator allows not specifying the version number in the HTML * You still need to reference each library in the HTML page ||                                                                                                                                                                                                        Everything is in the POM. As Vaadin generates the whole frontend, you don't need additional setup                                                                                                                                                                                                        |
|  Ease of styling  |                       No default; one needs to use an existing library, *e.g.*, Bootstrap or create their own                        || Vaadin comes bundled with the Lumo theme. Other themes are available in the [Vaadin Add-ons Directory](https://vaadin.com/directory)z, such as the [Parity Theme](https://vaadin.com/directory/component/parity-theme). Applying a theme is as easy as setting it as a dependency and adding an annotation.Creating a custom theme is no small potatoes, though. You can ease the task by starting from an existing one and changing it bit by bit. Vaadin, the company, also provides custom themes for a fee. |
|-------------------|--------------------------------------------------------------------|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Time to choose
--------------

If you are still unsure how to proceed, here are my recommendations.

If you're working on a regular business app, *e.g.*, forms, choose Vaadin. Business apps are Vaadin's primary use case and will shine there, immensely increasing productivity.

If your app requires good-looking visualization widgets, choose Vaadin as well. For example, its [Vaadin Charts](https://vaadin.com/directory/component/vaadin-charts) component is truly amazing. Note that it's commercially licensed, though.

If you want to offer an API **from the start**, choose Vue or Alpine. While it's possible to use HTMX or Vaadin, it doesn't make sense in this context. I also emphasize "from the start": everybody plans to offer an API at some point, but most never do. The possible productivity potential you plan to have in the future is not worth the guaranteed productivity in the next months.

The same goes for distributing your app over several channels - **from the start** (*bis repetita placent*).

If you're in none of these situations, it's time to go into more detail. Are your developers skilled in frontend technologies? Are they willing to learn to close the gap? Will you need these skills in the near future? These are a couple of questions that can help you decide which way to go.

This post concludes my series on AJAX and SSR. I hope you had as much fun reading it as I did writing it.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/compare-frontends).



*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/7/) on October 20^th^, 2024*
