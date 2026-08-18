---
title: "Java on Visual Studio Code Update"
slug: "java-on-visual-studio-code-update-february-2023"
date: "2023-03-21T15:47:04+00:00"
lastmod: "2023-03-21T15:48:43+00:00"
description: "In this article, we will bring you new features related to JUnit 5 parallel testing as well as new filter widget for Spring Boot dashboard."
authors:
  - "nick-zhu"
image: "parallel-test.gif"
categories:
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "java-development-with-vs-code-on-the-raspberry-pi"
  - "java-testing-with-vs-code"
frozen: false
---

Hi everyone, welcome to our February update!

In this article, we will bring you new features related to JUnit 5 parallel testing as well as new filter widget for Spring Boot dashboard.

There is also some exciting news from GitHub Copilot, so let's get started.

#### JUnit 5 Parallel Testing Support

JUnit 5 is a popular testing framework for Java developers, known for its comprehensive support for unit, integration, and functional testing. One of the significant improvements in JUnit 5 is its ability to execute tests in parallel, making testing faster and more efficient.

Parallel execution distributes test cases across multiple threads, allowing them to run simultaneously, and delivering results much faster than running tests sequentially.

In our latest release, we have supported the parallel testing feature in Visual Studio Code Java. To use this feature, you need to set junit-platform.properties file with line:

    junit.jupiter.execution.parallel.mode.default = concurrent

To learn about more about parallel testing in JUnit 5, you can visit the [official documentation.](https://junit.org/junit5/docs/snapshot/user-guide/#writing-tests-parallel-execution) Here's a demo of this feature.

![Parallel Testing](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/02/parallel-test.gif)

#### Filter Widget in Spring Boot Dashboard Extension

We have added a new filter widget to the endpoint mapping view in the Spring Boot dashboard extension. This widget allows developers to quickly search and filter through the various endpoints within their Spring application, making it easier to locate specific endpoints and analyze their behavior.

You can either use the exact filter or "fuzzy match" to search for the phrase you are interested in. By using the filter widget, developers can also easily identify duplicate or conflicting endpoints.

Overall, the filter widget allows developers to efficiently manage their application's endpoints and improve their development process. Here's a demo for this feature.

![Spring Boot filter](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/02/filter.gif)

#### GitHub Copilot AI Tech Upgraded and Generates 61% of Java Code

In a [recent article](https://visualstudiomagazine.com/articles/2023/02/15/copilot-upgrade.aspx) published on Visual Studio Code Magazine, it is revealed that GitHub Copilot already generates 61% of Java Code in editors where it's used. In average, it is already behind 46% of developer's code across all programming languages.

Even though Java is a verbose programming languages, it sill shows that Java developers are embracing the power of GitHub copilot and AI technology. With the extended partnership between Microsoft and OpenAI, there will more exciting features coming to Visual Studio Code Java as well.

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

## Resources

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
