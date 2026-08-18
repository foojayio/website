---
title: "Vaadin 24: Java 17, Jakarta EE 10, Servlet 6, Spring Boot 3"
slug: "vaadin-24-java-17-jakarta-ee-10-servlet-6-spring-boot-3"
date: "2023-03-22T09:22:27+00:00"
lastmod: "2023-03-22T09:25:55+00:00"
description: "Vaadin 24 is a step forward with a new technology baseline that includes Java 17, Jakarta EE 10, Servlet 6.0, Spring 6.0, and Spring Boot 3."
authors:
  - "sami-ekblad"
image: "VaadinLogo_RGB_1000x310.png"
categories:
  - "Cloud"
  - "Release Notes"
  - "Vaadin"
tags:
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
frozen: false
---

Vaadin 24 delivers a significant step forward with a new technology baseline that includes Java 17, Jakarta EE 10, Servlet 6.0, Spring 6.0, and Spring Boot 3.

This ensures you can continue building Vaadin Flow apps with the latest Java technologies.

It also provides several exciting improvements that help you build Java applications even faster. Let me introduce some of them.

Faster build times
------------------

You can now experience significantly improved build and start-up times during development.

Speedup is made possible using the new pre-compiled front-end bundle, which doesn't require front-end installation or compilation.

This means that there's no longer a requirement to install tools like Node.js, npm/pnpm, or download the npm package. How fast is it?

[Read more](https://vaadin.com/docs/latest/configuration/development-mode/#pre-compiled-front-end-bundle-for-faster-start-up "Read more") how to configure your development build.

Simplified component styling - using CSS
----------------------------------------

We've added full support for ::part() selector for a simpler and more flexible approach to styling UI components. All of your CSS can be regular, non-Shadow-DOM CSS.

* No more class vs. theme: use class names for everything.
* Styling is now entirely based on regular, native CSS -- no Vaadin-specific things to learn.
* You have the freedom to structure your stylesheets how you like -- while still a good practice, there is no need to split them per component.

Check [this post by Rolf Smeds](https://vaadin.com/blog/simplified-theming-in-vaadin-24 "this post by Rolf Smeds") explaining the details.

Convert Polymer templates to Lit
--------------------------------

Following our quest for continuously simplifying the client-side, Polymer support has been deprecated since Vaadin 18, November 2020, in favor of faster and much simpler Lit templates.

To keep you along, we created a conversion tool to assist you in converting your Polymer templates to Lit. In Vaadin 24, the built-in support for Polymer templates has been removed (still available as an add-on for Prime and Ultimate customers), but the tool will keep your app modern.

Check the [migration documentation](https://vaadin.com/docs/latest/upgrading#polymer-templates "migration documentation") for details.

Click to find your code from the UI
-----------------------------------

The handy new component locator in DevTools lets you click on any UI component directly in your application UI and instantly jump to the relevant code within your IDE.

These we some of the highlight of Vaadin 24.[Build your own Vaadin 24 application](http://start.vaadin.com/ " Build your own Vaadin 24 application") and give it a try.
