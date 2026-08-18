---
title: "Elevating Java in Visual Studio Code: New Build Server for Gradle"
date: "2023-10-30T15:24:14+00:00"
lastmod: "2023-11-01T08:04:30+00:00"
description: "Microsoft and Gradle Enterprise have joined forces to explore a novel approach to Gradle project import and building, based on the Build Server Protocol (BSP)."
authors:
  - "nick-zhu"
image: "build-server-gradle.png"
categories:
  - "Gradle"
  - "Tools"
  - "VS Code"
related_posts:
  - "vs-code-getting-better-and-better-for-java"
  - "five-ways-to-use-gradle-enterprise-to-identify-and-manage-flaky-tests"
  - "how-to-create-sboms-in-java-with-maven-and-gradle"
frozen: false
---

### Introducing Build Server for Gradle

In recent years, Gradle has become one of the most popular Java build tools due to its flexibility in configuring build processes and its powerful extensibility. In Visual Studio Code, users can import Gradle projects into their workspace for development. However, there are some areas where support for Gradle projects is not entirely satisfactory, with two major issues that users have below:

* Compiled files are output to the 'bin' directory, which differs from Gradle project's default output location.
* Support for code generation, such as Annotation Processing, is suboptimal.

To address these issues, Microsoft and Gradle have joined forces to explore a novel approach to Gradle project import and building, based on the [Build Server Protocol (BSP)](https://build-server-protocol.github.io/).

The Build Server for Gradle, developed in collaboration with Gradle, will delegate build tasks to the Gradle build tool, which will be responsible for compiling and generating code directly.

This ensures that the results of building in VS Code will be consistent with the output obtained by running Gradle build commands, fundamentally resolving the two major issues below.

### What is Build Server Protocol (BSP)

The [Build Server Protocol](https://build-server-protocol.github.io/) (BSP) draws inspiration from another protocol, the Language Server Protocol (LSP). The purpose behind LSP's creation was to establish an abstraction layer between development tools and programming languages. With this abstraction layer in place, different development tools that aim to support a particular programming language do not need to individually implement complex functionalities such as code analysis. Instead, they only need to correctly respond to events defined by LSP. For more information about LSP, you can refer to the [official LSP website](https://microsoft.github.io/language-server-protocol/).

BSP, inspired by LSP, seeks to create a similar abstraction layer between development tools and build tools, providing a unified way of exchanging information. BSP is also a valuable complement to LSP. While LSP focuses on functionalities related to code analysis, such as code completion and navigation, BSP is concerned with code building, running, and testing. Together, they form a closed-loop for code development.

Currently, BSP protocol has found widespread application in the Scala development ecosystem, and readers can [explore known projects](https://build-server-protocol.github.io/docs/overview/implementations/) based on BSP on its official website.

### Why Gradle

Apart from addressing the issues mentioned at the beginning of this article regarding the current support for Gradle projects in VS Code for Java, one of the reasons for prioritizing the implementation of a Gradle-oriented build service is Gradle's rich Tooling API. At present, nearly all the requests involved in BSP can be achieved through the Gradle Tooling API.

Details and design considerations regarding the Gradle build service we have implemented will be shared in future articles. Please stay tuned for more updates!

### How to Get Started

If you want to explore the new Gradle project development experience based on the Gradle Build Server Protocol (BSP) in VS Code, you can follow these steps:

#### Step 1 -- Install the "Extension Pack for Java" extension.

[![Image javaext](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/javaext.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/javaext.png)

#### Step 2 -- Install the "Gradle for Java" extension.

[![Gradle extension pack](https://devblogs.microsoft.com/java-ch/wp-content/uploads/sites/59/2023/09/gradle.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/gradle.png)

#### Step 3 -- Open your Gradle project in VS Code. If the project has been opened in VS Code before, open the command palette (F1) and execute the command "Java: Clean Java Language Server Workspace \> Reload and delete."

> Note: Please note that the Gradle Build Server currently does not support Android projects. After the Gradle Build Server starts, it will output status information in the VS Code Output Channel. If you don't see any output in the Output Channel after loading the project, please try executing the "Java: Clean Java Language Server Workspace \> Reload and delete" command again.

#### Step 4 -- Checking Gradle Build Server Status Output

[![Image buildserver](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/buildserver.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/09/buildserver.png)
> If you encounter any issues during your trial or have any suggestions, please feel free to let us know by [creating a GitHub Issue](https://github.com/microsoft/vscode-gradle/issues).

### Future Plans

We will continue to maintain and enhance the Build Server for Gradle project in the future. In the coming months, in addition to addressing bugs and improving accuracy and stability, we also plan to delegate the running and testing tasks to Gradle execution.

This way, even if users have customized complex build processes for testing or running tasks in Gradle scripts, they can be directly handed over to Gradle without the need for additional configuration in the development tool, ensuring an elegant out-of-the-box experience.

### Install Extension Pack for Java

To use all features mentioned above, please download and install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) on Visual Studio Code.

[![Extension pack for Java](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)

If you are a Spring developer working on a Spring Boot application, you can also download the [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) for specialized Spring experience.

[![Spring boot extension pack](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)

### Feedback and suggestions

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

### Resources

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
