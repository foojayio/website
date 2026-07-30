---
title: "Java on Visual Studio Code – June 2023"
slug: "java-on-visual-studio-code-june-2023"
date: "2023-07-10T12:44:47+00:00"
lastmod: "2023-07-10T12:46:06+00:00"
description: "Hi everyone, welcome to the our June update for Visual Studio Code for Java!"
authors:
  - "nick-zhu"
image: "/images/posts/2023/07/java-on-visual-studio-code-june-2023/settings.png"
categories:
  - "Developer Tools"
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "debugging-openjdk-tests-in-vscode-without-losing-your-mind"
  - "java-on-visual-studio-code-may-2023"
  - "java-on-visual-studio-code-update-february-2023"
frozen: false
---

Hi everyone, welcome to the our June update for Visual Studio Code for Java!

In this article, we're going to provide you an update about our code completion performance improvement, user experience enhancements in both unit testing and project creation, so let's get started!

#### Code Completion Performance Improvement

Faster code completion is a crucial aspect of any developer tool that can greatly enhance coding productivity. In the past, we have heard from our users they felt Java code completion was slow sometimes which has impacted their work. As a result, we have been researching into how to make it faster and as a result, we have actually made significant progress.

We will write a detailed blog later highlighting the technical details but here is some preliminary result between version 1.16 (2023-03) and version 1.19 pre-release (2023-05)

| Latency reduction (compared to 1.16) | P99 | P95 | P90 | P80 | P50 |
|--------------------------------------|-----|-----|-----|-----|-----|
| 1.19 pre-release (2023-05-19)        | 56% | 50% | 46% | 36% | 32% |

We hope the code completion performance improvement can bring you better coding experience and we will continue to enhance the performance, reliability and compatibility of the Java language support in Visual Studio Code Java. In the next few months we will roll out a series of performance updates, please stay tuned!

#### Support postDebugTask in Test Runner for Java

In latest release of [Test Runner for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-test) (part of [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)), we have added the support for attribute postDebugTask. This attribute will launch a task at the very end of a debug session. This will be very useful if developer needs to perform some cleanup task after the unit testing is done. For example, cleaning up databases. You can configure the task in the following way:

Settings.json

[![Settings](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/settings.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/settings.png)

Tasks.json

[![Tasks](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/tasks.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/tasks.png)

Notice that these are just examples and you can configure the tasks the way whichever way you prefer.

Here's a demo:

For more about postDebugTask, you can [read more here](https://code.visualstudio.com/docs/editor/debugging#_launchjson-attributes).

#### New Project Types in Project Manager

Thanks to contribution from the community ([PR #765](https://github.com/microsoft/vscode-java-dependency/pull/765) and [PR#757](https://github.com/microsoft/vscode-java-dependency/pull/757)). Project Manager for Java now supports creating Micronaut and Graal Cloud Native Projects, you can now create those projects from the command palette (Java: Create Java Project) or just using "Create Java Project" button on the UI. Here's a demo. Notice: you will need to install the corresponding extensions for these to work.

[![New project type](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/project.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/06/project.png)

#### Install Extension Pack for Java

To use all features mentioned above, please download and install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) on Visual Studio Code.

[![Extension pack for Java](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)

If you are a Spring developer working on a Spring Boot application, you can also download the [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) for specialized Spring experience.

[![Spring boot extension pack](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)

Feedback and suggestions

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

Resources {#h2-0-resources}
---------------------------

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
