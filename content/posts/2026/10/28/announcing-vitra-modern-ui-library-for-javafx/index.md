---
title: "Announcing Vitra – A Modern UI Library for JavaFX"
description: "Vitra is a carefully crafted UI library that brings modern controls, Fluent-inspired materials and professional surfaces to JavaFX applications."
date: "2026-10-28"
authors: ["liban-bande-gonzalez"]
categories: ["JavaFX", "Library"]
image: "vitra.jpg"
related_posts:
  - "announcing-skinning-javafx-applications"
  - "building-a-javafx-code-editor-control"
  - "custom-controls-in-javafx-part-iv"
  - "javafx-nodes-versus-canvas"
---

Building a polished desktop application with JavaFX still requires more work than it should. CSS can change colours and fonts, but it quickly reaches its limits when you need custom window chrome, glass surfaces, advanced charts, or controls that simply do not exist in the platform.

**Vitra** was created to close that gap.

It is a focused UI library that provides a set of modern, production-ready controls and surfaces designed specifically for contemporary JavaFX applications. Every component follows the Control–Skin architecture, supports runtime theme switching, and is built to integrate cleanly with existing codebases.

## What Vitra offers

Vitra includes more than ten carefully designed controls and skins:

- Gantt chart  
- Kanban board  
- Calendar  
- Fluent-inspired materials (Mica, Acrylic, Reveal)  
- Custom windows and dialogs  
- Donut chart and radial gauge  
- Enhanced skins for ScrollPane, ListView, TableView and TreeView  
- Glassmorphism surfaces
- And much more.  

## Built on solid foundations

All controls share the same technical principles:

- Clean public API separated from the visual skin  
- Full CSS support with looked-up colours  
- Consistent layout and focus behaviour  
- No external dependencies  

This makes Vitra easy to adopt incrementally. You can introduce a single control or surface into an existing application without rewriting your UI layer.

## Why it exists

After years of building desktop business applications with JavaFX, the same problems kept appearing: the platform is powerful, yet many modern visual patterns still have to be implemented from scratch. Vitra is the result of solving those problems once, carefully, and packaging the solutions so other developers can use them.

Whether you need a professional code editor, a clean Kanban board, glass panels, or custom window chrome that matches current desktop standards, the goal is the same: reduce the time spent reinventing visual infrastructure so you can focus on the actual product.

## Learn more

You can explore Vitra and the accompanying material at
[skinning-javafx-applications.netlify.app](https://skinning-javafx-applications.netlify.app/).
