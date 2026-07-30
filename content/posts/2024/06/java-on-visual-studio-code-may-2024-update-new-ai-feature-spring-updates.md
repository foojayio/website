---
title: "Java on Visual Studio Code May 2024 Update - New AI Feature, Spring Updates"
slug: "java-on-visual-studio-code-may-2024-update-new-ai-feature-spring-updates"
date: "2024-06-18T10:42:26+00:00"
lastmod: "2024-06-18T10:42:28+00:00"
description: "An exciting new AI related feature for Java developers on Visual Studio Code."
authors:
  - "nick-zhu"
image: "https://foojay.io/wp-content/uploads/2024/06/pack.png"
categories:
  - "Java"
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "announcing-the-bsp-repo"
  - "boldness-in-refactoring"
frozen: false
---

Hi everyone, welcome to the May update for Visual Studio Code for Java!

In this articlem we are going to share an exciting new AI related feature for Java developers on Visual Studio Code.

In addition, there will be several important Spring updates, so let's get started.

### Rewriting your Java code with Copilot-based suggestions {#h3-0-rewriting-your-java-code-with-copilot-based-suggestions}

As our code undergoes updates and iterations, our business logic continues to grow in complexity. Meanwhile, Java versions are constantly evolving. Often, our older code can be replaced with newer, more efficient alternatives, not only improving their performance but also making the code more elegant, concise and secure. Previously, these tasks may have been done manually, but with the power of AI, many tasks can now be handled automatically.

With the [release of various AI products](https://blogs.microsoft.com/blog/2024/05/21/whats-next-microsoft-build-continues-the-evolution-and-expansion-of-ai-tools-for-developers/) at the Build conference 2024, we're excited to announce an exciting update for Java developers on Visual Studio Code. In the Insider (Pre-release) version of our Extension Pack for Java, we've introduced a new feature: "Rewrite with new Java syntax" with Copilot! This feature integrates seamlessly with GitHub Copilot, so developers will need a GitHub Copilot license to access it.

Here's how the feature works, on your Java class, a new CodeLens text will appear above the name of the class: "Rewrite with new Java syntax". Once the user clicks on this, it will trigger an inspection on the current Java class. Once the inspection is complete, Visual Studio Code will show several improvement suggestions and point users to the location.

![](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/trigger.gif)

For each suggestion, additional CodeLens will appear and shows what the solution is (and what needs to be improved).

The user can directly click on the CodeLens and this will bring up the GitHub Copilot inline chat dialog.

The user can then see the code diff, and will be offered the choice of "Accept or "Discard".

If the user accepts the suggestion, then the workflow is over. Let's see the first example where the for-loop can be refactored using IntStream.

![For loop to instream](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/intstream.gif)

For loop to instream, image

Let's look at another example, where our feature suggests that a multiple if and else-if logic can be directly re-written as a switch expression. By clicking on the CodeLens text, the code can be directly re-written as the improved code.

![Switch to if](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/06/switchtoif.gif)

Switch to if, image

##### Inspecting part of the class

Sometimes, we may not want inspect the whole class because the code is too complex. The feature above also supports inspecting the part of the code. We just need select the code we want to inspect, and then click on the lightbulb on the left, select "Rewrite with Java syntax". Here's an example:

![Partial inspection](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/06/partial.gif)

Partial inspection, image

In this way, developers can inspect the code in a much more flexible way.

This feature aims to leverage AI to offer helpful suggestions and seamless refactoring for Java developers for their code. We hope this can greatly boost developer's productivity. If you have any feedback about this feature, please [open an issue](https://github.com/microsoft/vscode-java-pack/issues) on our GitHub repo!

To use this feature, developer needs to install both Insider (Pre-release) version [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and [GitHub Copilot](https://marketplace.visualstudio.com/items?itemName=GitHub.copilot) extensions. (with a GitHub Copilot license).

### Spring updates {#h3-1-spring-updates}

#### JPQL syntax highlighting

The Spring Boot Tools extension now features syntax highlighting for JPQL query strings. The syntax highlighting works for @Query annotations inside of Java source files as well as for named query property files. This makes reading those query strings much easier. (screenshot JPQL query syntax highlighting)

#### [![JPQL Syntax highlighting](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/jpql-syntax-highlighting.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/jpql-syntax-highlighting.png)

#### Add Starters directly from within `pom.xml` files

We added a clickable hint to pom.xml files of Spring Boot projects, directly in their dependency section. This clickable hint directly invokes the support to add additional Spring Boot starter modules. This improves discoverability and usability of this feature and makes it super easy to find and use. (screenshot add pom starters)

[![Image thumbnail hint add starters](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/thumbnail_hint-add-starters.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/thumbnail_hint-add-starters.png)

#### Dashboard shows active profiles

The Spring Boot Dashboard now shows the active profiles of a running Spring Boot application directly side by side with the port the app runs on. This is available in the pre-release version. (screenshot active profiles in dashboard)

#### [![Dashboard profiles](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/dashboard-profiles.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/dashboard-profiles.gif)

#### Automatically convert application properties to YAML and back

The Spring Boot tools now offer actions to automatically convert `application.properties` files to YAML format and the other way around. This makes the switch to your (newly) preferred format super easy. (screenshot property conversion

### [![Convert properties](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/convert-properties.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2024/05/convert-properties.gif) {#h3-2-}

#### Performance and footprint improvements

The indexing infrastructure for Spring projects got improved. Previously, the mechanism was able to index about 6.5k Java source files inside of a single project before running out of memory. The latest versions can now index up to 60k Java source files instead within the same memory constraints. In addition to that the initial as well as ongoing performance of reconciling Java source files for Spring specific validations is now twice as fast as before.

### Testing Coverage {#h3-3-testing-coverage}

In April, we released the Testing Coverage feature for our Test Runner extension in Stable version on Visual Studio Code. After release, we have received lots of positive feedback. However, we also received questions on how to use the feature. One popular question was around how coverage data can be reset for every execution.

By default, the extension will append the coverage data (we use jacoco as the coverage tool, and [jacoco appends the coverage data by default](https://www.eclemma.org/jacoco/trunk/doc/agent.html))

If you want to reset the coverage data before every coverage execution, you can set the below setting:

    "java.test.config": {
         "coverage": {
             "appendResult": false
         }
    }

### Install Extension Pack for Java {#h3-4-install-extension-pack-for-java}

To use all features mentioned above, please download and install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) on Visual Studio Code.

[![Extension pack for Java](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)

If you are a Spring developer working on a Spring Boot application, you can also download the [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) for specialized Spring experience.

[![Spring boot extension pack](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)

### Feedback and suggestions {#h3-5-feedback-and-suggestions}

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

### Resources {#h3-6-resources}

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
