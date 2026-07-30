---
title: "What's New, Noteworthy, and Enhanced in JavaFX 15?"
slug: "whats-new-in-javafx-15"
date: "2020-09-15T13:26:28+00:00"
lastmod: "2020-09-17T20:01:55+00:00"
description: "A couple of weeks ago, JavaFX version 15 was released. These are some of the highlights and improvements we’ve selected for you to understand its scope."
authors:
  - "bruno-lowagie"
image: "/images/posts/2020/09/whats-new-in-javafx-15/Favicon-3-2.png"
categories:
  - "JavaFX"
  - "Release Notes"
tags:
related_posts:
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
  - "foojay-podcast-83"
  - "azul-brings-java-from-edge-to-cloud"
  - "foojay-podcast-9"
frozen: false
---

A couple of weeks ago, JavaFX version 15 was released.

### Highlights {#h3-0-highlights}

These are some of the highlights we've selected for you:

* JavaFX now has 3D support for the newer Intel graphics drivers on Linux,
* Support for e-paper displays on i.MX6 devices was added,
* FX scripting support was enhanced.

### Enhancements {#h3-1-enhancements}

These are some of the improvements that were implemented in JavaFX 15:

* Better native keyboard handling and integration on iOS and Android,
* Adjusted font size in JavaFX apps with enabled Monocle on Raspberry Pi,
* Capped the refresh rate on iOS (to a maximum of 30 Hz).

### Code Cleanup {#h3-2-code-cleanup}

With every new release, a code cleanup is done, for instance:

* The JavaBeanXxxPropertyBuilders constructors were removed,
* The deprecated finalize() methods from JavaFX property objects were removed,
* Some unnecessary logging, e.g. for pinch gestures on iOS, is now skipped.

### Bug Fix {#h3-3-bug-fix}

In the bug fix department, you'll find, among others:

* A fix for the SVG patterns that were drawn incorrectly.
* A solution for some inconsistencies with controls, such as the incorrect arrow key traversal through tabs after reordering the tabs in a TabPane, the lost formatting when using Ctrl-A in HTMLEditor, and the wrong Scrollbar position on touch supported devices.
* A fix for memory leaks, e.g. in the ProgressIndicator class, in the ToggleButton.setToggleGroup() method, and in some other places.
* Better code hygiene to avoid exceptions such as an IndexOutOfBoundsException when requesting focus on an empty bar, a StringOutOfBoundsException when adding a ChangeListener to TextField.selectedTextProperty(), possible NullPointerExceptions in TabPaneSkin.perfromDrag(), in the MenuButtonSkinBase change listener, and while entering empty submenu with "arrow right".

### Dependency Upgrades {#h3-4-dependency-upgrades}

Finally, these are some of the dependency upgrades in JavaFX 15:

* Upgraded libFFI to version 3.3,
* Upgraded SQLite to version 3.32.3,
* Upgraded WebKit to version 609.1,
* Upgraded libxml2 to version 2.9.10

### Conclusion {#h3-5-conclusion}

A more comprehensive list of all the changes in JavaFX 15 can be found on Github:

<https://github.com/openjdk/jfx/blob/master/doc-files/release-notes-15.md>

Kudos go to the fine people at Gluon who took care of the bulk of the work on JavaFX 15.

Check their website if you need support for JavaFX: <https://gluonhq.com/services/javafx-support/>
