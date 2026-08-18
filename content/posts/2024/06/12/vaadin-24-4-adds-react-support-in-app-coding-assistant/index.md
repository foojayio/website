---
title: "Vaadin 24.4 Adds React Support, In-App Coding Assistant"
date: "2024-06-12T14:38:32+00:00"
lastmod: "2024-06-12T14:38:33+00:00"
description: "Vaadin 24.4: Vaadin Copilot, Hilla integration, and React support in Flow."
authors:
  - "marcus-hellberg"
image: "vaadinwebapp.png"
categories:
  - "Release Notes"
  - "Tools"
  - "Vaadin"
related_posts:
  - "a-faster-way-to-build-react-spring-boot-apps-using-hilla-1-3"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "securing-vaadin-applications-with-microsoft-entra"
frozen: false
---

## Unified Vaadin Platform: Seamless Integration with Hilla

24.4 adds React support to the entire Vaadin platform by including the Hilla framework.

The Vaadin BOM and Vaadin Spring Boot Starter now include Hilla, allowing you to choose between Flow and Hilla or mix both in your projects.

## Vaadin Copilot: an in-app development assistant

Vaadin 24.4 introduces **[Vaadin Copilot](https://vaadin.com/copilot)**, a visual development tool and AI-powered assistant.

Available by default in development mode, Copilot helps you inspect and edit the UI, and use generative AI to create or modify code.

Copilot is a code-first editor, meaning there is no intermediate design format, it modifies your code directly.

## Flow Enhancements

* **Mixing Flow and Hilla views** : Combine server-side and client-side routes in a single application. No special configuration needed. [Docs](https://vaadin.com/docs/next/flow/integrations/hilla) · [Example Project](https://github.com/vaadin/flow-hilla-hybrid-example/tree/v24.4)
* **Using React components in Flow** : Wrap any React component as a Flow component and manage its state and events automatically. [Docs](https://vaadin.com/docs/next/flow/integrations/react)
* **Using Flow components in React** : Embed Flow components in Hilla/React views using `WebComponentExporter`. [Docs](https://vaadin.com/docs/next/hilla/guides/flow-component-in-hilla)
* **React Router by default** : Vaadin Flow now uses React Router, simplifying the addition of React components and views. [Docs](https://vaadin.com/docs/next/flow/configuration/maven#properties)
* **New default frontend directory** : The default location for frontend resources is now `src/main/frontend/`, aligning better with Maven project structure.

## Hilla Enhancements

* **Hilla File Router** : Simplifies adding React views by automatically mapping files in `src/main/frontend/views/` as routes. [Docs](https://vaadin.com/docs/next/hilla/router/file-router)
* **Automatic Main Menu** : The file router's `createMenuItems()` utility populates the menu items in the React main layout. [Docs](https://vaadin.com/docs/next/hilla/router/main-menu)
* **Hilla React Signals** : Manage state in React applications with `@vaadin/hilla-react-signals`, offering a robust way to share state updates across components. [Docs](https://vaadin.com/docs/next/hilla/signals)

## Detailed Changelogs

* **Flow and Hilla** : [Flow 24.4.0](https://github.com/vaadin/flow/releases/tag/24.4.0) · [Hilla 24.4.0](https://github.com/vaadin/hilla/releases/tag/24.4.0)
* **Design System** : [Web Components 24.4.0](https://github.com/vaadin/web-components/releases/tag/v24.4.0) · [Flow Components 24.4.1](https://github.com/vaadin/flow-components/releases/tag/24.4.1)
* **Other updates** : [TestBench 9.3.0](https://github.com/vaadin/testbench/releases/tag/9.3.0) · [Classic Components 24.2.1](https://github.com/vaadin/classic-components/releases/tag/24.2.1) · [MPR 7.0.10](https://github.com/vaadin/multiplatform-runtime/releases/tag/7.0.10)

For full details, check the [release notes](https://github.com/vaadin/vaadin/releases).

## Upgrading Guides

* [Upgrading Flow to Vaadin 24](https://vaadin.com/docs/latest/flow/upgrading/changes/#changes-in-vaadin-24)
* [Upgrading Hilla to Vaadin 24](https://vaadin.com/docs/latest/hilla/guides/upgrading)
