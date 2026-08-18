---
title: "Announcing the Build Server for Gradle Open-Source Repository"
slug: "announcing-the-bsp-repo"
date: "2023-11-21T10:08:33+00:00"
lastmod: "2023-11-21T10:09:01+00:00"
description: "We are excited to announce our decision to officially open-source the Build Server for Gradle project!"
authors:
  - "nick-zhu"
image: "gradleext.png"
categories:
  - "Java"
  - "Java Core"
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "elevating-java-development-in-visual-studio-code-experience-the-new-build-server-for-gradle"
  - "java-on-visual-studio-code-july-2023"
  - "java-on-visual-studio-code-june-2023"
frozen: false
---

### Build Server for Gradle

**In September, we shared that Microsoft and Gradle [have joined forces](https://devblogs.microsoft.com/java/new-build-server-for-gradle/) to explore a novel approach to Gradle project import and building, based on the [Build Server Protocol (BSP)](https://build-server-protocol.github.io/). The objective is address the existing Gradle issues on Visual Studio Code and improve support for Gradle projects.**

The Build Server for Gradle, developed in collaboration with Gradle, will delegate build tasks to the Gradle build tool, which will be responsible for compiling and generating code directly. This ensures that the results of building in Visual Studio Code will be consistent with the output obtained by running Gradle build commands.

The preview version for this was released in September version and stable version was released in October on Visual Studio Code. We have received many positive comments after the release.

### Open-sourcing the Build Server for Gradle Project

After two months of testing and collecting feedback, we have observed that Build Server has performed as expected. Based on data we have collected, the import success rate for non-Android Gradle projects has increased by 19%.

Additionally, we have received a lot of useful feedback from the community for this kind of approach.

Therefore, we are excited to announce our decision to officially open-source [Build Server for Gradle](https://github.com/microsoft/build-server-for-gradle) project today! You can use [this link](https://github.com/microsoft/build-server-for-gradle) to visit the repo.

### How to use Build Server for Gradle

Using Build Server for Gradle is straightforward. You can follow these simple steps.

#### Step 1 -- Install the "Extension Pack for Java" extension.

[![Image javaext](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/javaext.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/javaext.png)

#### Step 2 -- Install the "Gradle for Java" extension. (Currently, Build Server for Gradle is integrated into [Gradle for Java extension](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle))

[![](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/gradleext.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/gradleext.png)

After installing this extension, you can enable Build Server for Gradle for import your Gradle projects.

By default, Build Server for Gradle will only import newly opened Gradle projects. If your project has been imported into Visual Studio Code before, you need to execute the '**Java: Clean Java Language Server Workspace \> Reload and delete**' command to clear the cache and reimport.

If you wish to disable Build Server for Gradle, you can go to the settings and set '**java.gradle.buildServer.enable** d' to '**off.**'

[![Turning build server for gradle off](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/11/bsg.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/11/bsg.png)

### Future Plans

We will continue to improve the Build Server for Gradle project in the future to enhance the development experience for Visual Studio Code Java users working on Gradle projects. This includes:

* Further optimizing and improving project loading accuracy
* Enhancing project loading and building performance
* Supporting the execution of Gradle tasks
* Supporting test delegation

### Feedback and suggestions

Currently, the project is still in its early stages, and there will be many issues to explore and resolve in the future. We encourage everyone to try out Build Server for Gradle and provide feedback for improvement.

We also greatly appreciate various forms of contributions, including but not limited to submitting issues and pull requests. Project repository link: .

Thank you as always!
