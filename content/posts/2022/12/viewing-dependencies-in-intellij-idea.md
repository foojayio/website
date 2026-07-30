---
title: "Viewing Maven and Gradle Dependencies in IntelliJ IDEA"
slug: "viewing-dependencies-in-intellij-idea"
date: "2022-12-07T08:54:13+00:00"
lastmod: "2022-12-07T08:54:14+00:00"
description: "In this article, we're going to take a look at different ways to view your external dependencies in IntelliJ IDEA."
canonical: "https://maritvandijk.com/viewing-dependencies/"
authors:
  - "marit-van-dijk"
image: "/images/posts/2022/12/viewing-dependencies-in-intellij-idea/dependency-details-pane.png"
categories:
  - "Gradle"
  - "IntelliJ IDEA"
  - "Maven"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
frozen: false
---

In this article, we're going to take a look at different ways to view your external dependencies in IntelliJ IDEA.

Introduction {#h2-0-introduction}
---------------------------------

If you're working on a real-world application, your project will probably use external libraries and frameworks.

Occasionally, you might want to see which dependencies your project uses, for various reasons.

There are several ways to view dependencies in [IntelliJ IDEA](https://www.jetbrains.com/idea/ "IntelliJ IDEA").

Each view has a different focus.

Dependency management config file {#h2-1-dependency-management-config-file}
---------------------------------------------------------------------------

You can find direct dependencies in the dependency management config file. Direct dependencies are the dependencies that your project depends on directly.

They are declared in the dependency management config file.

One example is this pom.xml in a Maven project.

[![Maven pom.xml file](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/pom-xml.png "Maven pom.xml file")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/pom-xml.png "Maven pom.xml file")

Another example is the build.gradle in a Gradle project.

[![Gradle build.gradle file](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-gradle.png "Gradle build.gradle file")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-gradle.png "Gradle build.gradle file")

Note that the dependency management config file includes only declared dependencies and not their transitive dependencies (or the dependencies that these declared dependencies depend on).

Project tool window {#h2-2-project-tool-window}
-----------------------------------------------

In the Project tool window, **⌘1** (on Mac) or **Alt+1** (on Windows/Linux), under External Libraries we can see all the JAR files needed by our application, including the transitive dependencies.

However, we cannot tell the difference between direct dependencies and transitive dependencies. One declared dependency might bring in multiple JAR files.

[![Project tool window](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/project-tool-window.png "Project tool window")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/project-tool-window.png "Project tool window")

Build tool window {#h2-3-build-tool-window}
-------------------------------------------

To see direct dependencies and their transitive dependencies, we can look in the Build tool window. There is no shortcut to open the Build tool window.

We can open it by clicking Quick Launch in the bottom-left and selecting Gradle, or Maven depending on what we're using.

[![Open the Maven Build Tool Window in the Quick Launch menu](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-maven.png "Open the Build Tool Window in the Quick Launch menu")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-maven.png "Open the Build Tool Window in the Quick Launch menu")

[![Open the Gradle Build Tool Window in the Quick Launch menu](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-gradle.png "Open the Gradle Build Tool Window in the Quick Launch menu")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-gradle.png "Open the Gradle Build Tool Window in the Quick Launch menu")

Alternatively, we can open it by using Recent Files, **⌘E** (on Mac) or **Ctrl+E** (on Windows/Linux), and typing "gradle" or "maven", or the name of your build system.

[![Open the Gradle Build Tool Window using the Recent Files popup](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-gradle.png "Open the Gradle Build Tool Window using the Recent Files popup")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-gradle.png "Open the Gradle Build Tool Window using the Recent Files popup")

[![Open the Maven Build Tool Window using the Recent Files popup](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-maven.png "Open the Maven Build Tool Window using the Recent Files popup")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-maven.png "Open the Maven Build Tool Window using the Recent Files popup")

The Build tool window shows you each IntelliJ IDEA module separately, and each module's "Dependencies" folder shows you all your dependencies in a hierarchical structure.

We can expand our dependencies to see their transitive dependencies.

[![Gradle Build Tool Window showing dependencies](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-tool-window-gradle.png "Gradle Build Tool Window showing dependencies")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-tool-window-gradle.png "Gradle Build Tool Window showing dependencies")

[![Maven Build Tool Window showing dependencies](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-tool-window-maven.png "Maven Build Tool Window showing dependencies")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/build-tool-window-maven.png "Maven Build Tool Window showing dependencies")

Dependency tool window {#h2-4-dependency-tool-window}
-----------------------------------------------------

Finally, we can view and manage dependencies in the Dependencies tool window. The Dependencies tool window becomes available when the current project has at least one supported module.

All types of dependencies are supported for Maven. For Gradle only a top level `dependencies { }` block is supported in the build script.

Since there is no shortcut to open the Dependencies tool window directly either, we can again use Recent Files, **⌘E** (on Mac) or **Ctrl+E** (on Windows/Linux), and type in "dependencies" to open the Dependencies tool window.

[![Open the Dependencies Tool Window using the Recent Files popup](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-dependencies.png "Open the Dependencies Tool Window using the Recent Files popup")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/recent-files-dependencies.png "Open the Dependencies Tool Window using the Recent Files popup")

Alternatively, we can open it by clicking Quick Launch in the bottom-left and selecting Dependencies.

[![Open the Dependencies Tool Window in the Quick Launch menu](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-dependencies.png "Open the Dependencies Tool Window in the Quick Launch menu")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/quick-launch-dependencies.png "Open the Dependencies Tool Window in the Quick Launch menu")

Here we can see our project's direct dependencies. Select "All Modules" to see the dependencies for all modules, or select an indivual module to see the dependencies for that specific module.

The Dependencies tool window shows direct dependencies, and not their transitive dependencies.

[![Dependencies Tool Window](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/dependency-tool-window.png "Dependencies Tool Window")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/dependency-tool-window.png "Dependencies Tool Window")

We can see details about a selected dependency in the dependency details pane.

[![Dependency Details Pane](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/dependency-details-pane.png "Dependency Details Pane")](/images/posts/2022/12/viewing-dependencies-in-intellij-idea/dependency-details-pane.png "Dependency Details Pane")

The dependency details pane displays the information about the selected dependency, such as:

* Repository or repositories where it's available, for example Maven Central
* A description if it is available
* GitHub information if the dependency sources are hosted on GitHub
* The licence under which an open source library is available
* A link to the project website, documentation and readme
* List of usages in the current module.
* Authors if available
* Supported Kotlin or Multiplatform platforms if it is a Kotlin Multiplatform dependency

Summary and Shortcuts {#h2-5-summary-and-shortcuts}
---------------------------------------------------

Now we know the different ways in which we can view our project's dependencies in IntelliJ IDEA, and the different focus for each view.

### IntelliJ IDEA Shortcuts Used {#h3-6-intellij-idea-shortcuts-used}

Here are the IntelliJ IDEA shortcuts that we used.

|                                                          Name                                                          | macOS Shortcut | Windows / Linux Shortcut |
|------------------------------------------------------------------------------------------------------------------------|----------------|--------------------------|
| Open / Close [Project Tool Window](https://www.jetbrains.com/help/idea/project-tool-window.html "Project Tool Window") | ⌘1             | Alt+1                    |
| Recent Files                                                                                                           | ⌘E             | Control+E                |

### Related Links {#h3-7-related-links}

* (video) JetBrains - Viewing Dependencies
* (docs) JetBrains - Package Search
* (code) JetBrains - intellij-samples
* (code) Spring PetClinic
