---
title: "Vaadin 24.1: Bringing You More Than Minor Improvements!"
date: "2023-06-15T14:58:13+00:00"
lastmod: "2023-06-15T14:58:47+00:00"
description: "While Vaadin 24.0 was about upgrading the technology baseline and compatibility, this version brings you new features!"
authors:
  - "sami-ekblad"
image: "vvb.png"
categories:
  - "Cloud"
  - "Release Notes"
  - "Vaadin"
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "how-to-style-a-vaadin-application"
frozen: false
---

I am pleased to share with you the newest release of Vaadin 24.1.

While [Vaadin 24.0](http://https://foojay.io/today/vaadin-24-java-17-jakarta-ee-10-servlet-6-spring-boot-3/ "Vaadin 24.0") was about upgrading the technology baseline and compatibility, this version brings you some new features.

Here are the best of them!

## Visual View Builder

[![Vaadin View Builder](https://vaadin.com/docs/latest/static/37d22f0783d625059a3b94ed276b7d7a/cfb07/view-builder-start.webp "Vaadin View Builder")](https://vaadin.com/docs/latest/tools/view-builder "Vaadin View Builder")

Built into start.vaadin.com, Visual View Builder helps you to create application prototypes using Vaadin UI components, generate Java code and share your prototypes to gather feedback.

[Try it out →](https://vaadin.com/docs/latest/tools/view-builder "Try it out -&gt;")

## Visual Theme Editor

[![Vaadin Real-time Theme Editor](https://vaadin.com/docs/latest/static/c50310949470420af2bc09943c940a4f/5f228/theme-editor.webp "Vaadin Real-time Theme Editor")](https://vaadin.com/docs/latest/tools/theme-editor "Vaadin Real-time Theme Editor")

Visually fine-tune the appearance of your UI components in-app while in real-time. Easily modify colors, fonts, spacing, and more through an intuitive interface without the need for tedious and time-consuming CSS editing.

[Read more →](https://vaadin.com/docs/latest/tools/theme-editor "Read more -&gt;")

## Faster, easier production builds

Pre-compiled production bundle eliminates the need to run front-end tools like npm and Vite.

Faster, error-free process, if no add-ons or front-end files are used in the application.

[Read more about production builds →](https://vaadin.com/docs/latest/production/production-build "Read more -&gt;")

## Code splitting for component loading

Faster initial load time of the application by only loading components when they are needed.

By default, only the components of the routes "" and "login" are eagerly loaded.

For other routes, components are first loaded when navigating to these routes.

[Read more about bundle optimizations →](https://vaadin.com/docs/latest/production/production-build#bundle-component-loading-optimizations "Read more -&gt;")

## Faster Grids and lazyloading for columns

Significant improvements to enhance the performance of all Vaadin grids, including Grid, Tree Grid, Grid Pro, and CRUD components.

This release also adds support for lazy rendering of columns for faster loading of grids with many columns.

[Read more about lazy column rendering →](https://vaadin.com/docs/latest/components/grid#lazy-column-rendering "Read more -&gt;")

[![Vaadin SideNav Component](sidenav-24.1-700x379.png)](https://vaadin.com/docs/latest/components/side-nav)

New SideNav component provides a vertical list of navigation links with support for collapsible hierarchy, icons and notification badges.

[Read more about SideNav component →](https://vaadin.com/docs/latest/components/side-nav "Read more -&gt;")

Added ARIA label APIs needed by screen readers as well as a number of other accessibility improvements to conform with Web Content Accessibility Guidelines.

[Vaadin WCAG compatibility matrix →](https://docs.google.com/spreadsheets/d/1VJuzr1H2BWxPAGdtLxTe7yj12_vn0RcTu-UO79WOp8w/edit#gid=1281315720 "Read more -&gt;")

Have I missed anything important that you were anticipating to hear from us?

For the complete list of improvements, see [the Vaadin release notes in GitHub](https://github.com/vaadin/platform/releases/tag/24.1.0 "the Vaadin release notes in GitHub").
