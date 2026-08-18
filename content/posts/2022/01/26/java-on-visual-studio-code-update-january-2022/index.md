---
title: "Java on Visual Studio Code Update – January 2022"
date: "2022-01-26T08:22:13+00:00"
lastmod: "2022-01-26T09:47:52+00:00"
description: "Since this is our first blog post of the new year, we are going to look back on highlights of 2021 and take a look at our roadmap for 2022."
authors:
  - "nick-zhu"
image: "https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/roadmap2022-1.png"
categories:
  - "VS Code"
related_posts:
  - "say-goodbye-to-project-files-in-visual-studio-code"
  - "vs-code-java-september-2021-update"
  - "java-17-on-the-raspberry-pi"
frozen: false
---

Hi everyone, welcome to the January edition of Visual Studio Code Java update!

Since this is our first blog post of the new year, we are going to look back on highlights of 2021 and take a look at our roadmap for 2022!

We also have some exciting feature updates so let us get started.

### Highlights of 2021

2021 was a productive year for Java on Visual Studio Code. We have made substantial improvements on all extensions in the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack). Among those achievements, we'd like to highlight a few important ones:

#### 1. Release 1.0 of Language Support for Java

In 2021, we released the [official 1.0 version of Language Support for Java™](https://devblogs.microsoft.com/java/language-server-1-0/) which marks a significant milestone and result of multi-year collaboration between Microsoft and Red Hat.

This release contained many important features such as Java 17 support, easier type hierarchy lookup, source lookup, Gradle Kotlin (.kts) support and so on. In addition, we also [made a series of optimization on code completion performance](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-october-2021/) starting from version 0.78.

As a result, we have seen a decrease of 60% of our average code completion response time.

#### 2. Gradle for Java extension release

Gradle support has always been one of the top asks in the community. In October 2021, we officially released the "[Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle)" extension to address developer's Gradle needs.

The extension now has more than 230K downloads and we expect to continue to iterate on the extension.

#### 3. New getting started experience

Better getting started experience is always one of the top priorities. In 2021, we have made several improvements in this area including a[new in-product welcome experience](https://code.visualstudio.com/updates/v1_63#_java) for Java developers in Visual Studio Code.

We also re-designed our welcome view and help center page so that newcomers can find the guidance they needed.

### Various user experience improvements

Throughout 2021, numerous user experience improvements were also made to optimize the developer productivity overall. To name a few remarkable ones:

* Testing -- Test Runner for Java [adopted the new Testing UX](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-july-2021/) from Visual Studio Code in order to offer a better testing experience in terms of feature, capability and ease of use.
* Project Management -- We no longer generate .project metadata files in project root folder! This was one of the top community voted issues and we finally delivered a long awaited solution. Click [here](https://devblogs.microsoft.com/java/say-goodbye-to-project-files-in-1-1-0/) to see how we addressed the problem.
* Code actions -- We [added several features](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-september-2021/) so that operations like getter/setter and construction generation were more easily accessed, and we will continue to make code actions more accessible.

### Spring support in GitHub Codespaces

Besides the core Java extension releases, there were also exciting updates in remote development area.

In September 2021, it was announced that Spring framework was fully enabled on [GitHub Codespaces](https://github.com/features/codespaces) via partnership with VMWare. Developer can directly develop a Spring application seamless in a browser environment.

Please visit [our past blogs](https://devblogs.microsoft.com/java/category/vscode/) to learn about all the improvements that we made through 2021.

### User growth

In addition to product updates, we also witnessed the growth of our users during 2021. We now have more than 1.5 million users developing Java in VS Code.

This is the result of continuous feedback from our users and it wouldn't have been possible without the support from the community.

### 2022 Roadmap

Now onto the exciting part, let us talk about our roadmap for year 2022. Here is a picture that summarizes our investment areas.

[![VS Code Java roadmap 2022](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/roadmap2022-1.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/roadmap2022-1.png)

For the year of 2022, we are going to focus on the following:

#### 1. Fundamental development experience improvement

Fundamental inner-loop experience impacts our developer's daily productivity and this area will continue to be our top focus.

This includes efforts to improve the smartness of our code completion suggestions, provide more relevant code snippet generation, and offer various shortcuts (such as "syso" and "sout") based on user's preference. In addition, we will further improve our debugging experience.

There are a few things we will look at: Allow debugging for decompiled classes, faster evaluation in variable views and lambda expression evaluation. We will also explore the possibility of enabling virtual threads powered by the[new Project Loom](https://blogs.oracle.com/javamagazine/going-inside-javas-project-loom-and-virtual-threads) for better debugging performance. In addition, showing test coverage is another feature we hope to support to further enhance our Java testing experience.

Lastly, we will always try to support the latest Java technology so Java 18 support is on our roadmap.

#### 2. Performance and reliability

Performance and reliability is another area of our focus in 2022. In this area, we hope to improve the reliability of Java Language Server and reduce the cases where Java Language Server becomes unresponsive.

We also heard from the community that importing a project for the first time could take a lot of time if there are lots of dependencies to download, so we also plan to look into that.

Last but not least, we will continue our investment to reduce the code completion response time.

#### 3. Build tools

Build and dependency management has always been a critical part of Java development, especially for large and multi-module projects. We will continue to add new features to the [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) extension as well as improve the existing [Maven extension](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven).

#### 4. Spring Boot end-to-end support

Spring Boot framework is one of the most popular Java frameworks and it allows developers to easily build a microservice or web application. The current [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) contains many features that optimize Spring development experience on Visual Studio Code, but we have heard asks from the Spring developer community and think we can do more. We will make improvements to both core Java extensions and Spring extensions in collaboration with VMWare and here is a general list of improvement areas:

* Easier creation workflow of Spring projects, controllers and beans
* Better visualization of core Spring concepts (such as beans and API mappings)
* Boilerplate code generation for Spring controllers and classes
* Improved Spring application lifecycle management in Spring Boot dashboard
* More intuitive experience to add Spring libraries when managing dependencies

With these Spring improvements, we do hope Spring development experience on Visual Studio Code will be more pleasant than ever.

#### 5. User experience

Next area of our investment is user experience (UX). During 2021, we have encountered a lot of cases where developers ask for a feature that already exists, however, they couldn't find it because it is hard to discover.

Therefore, making features easier to use and more discoverable will be our focus in this area. We also recognize that Java developers might come from different Java IDE backgrounds so we will make it easier to migrate the settings and configurations from other IDEs.

Lastly, we know that there are many student developers using Java in Visual Studio Code, so we plan to make a few improvements such as providing better JUnit testing end-to-end experience for projects without build tools, optimized project creation workflow for JavaFX / Swing projects, and more smooth package import experience for Java AWT packages. Lastly, we expect to support Live Share in our Java extensions.

#### 6. Cloud-native development

Cloud-native has been one of the most popular topics recently in the software development industry.

With cloud-native development approach, developers need to deal with microservices, cloud platforms, Kubernetes and so on. To address developer's need in this area, we plan to explore deeper integration with Kubernetes in general and interaction with different cloud services (such as [Azure Spring Cloud](https://azure.microsoft.com/en-us/services/spring-cloud/)).

With Visual Studio Code's[remote development extensions](https://code.visualstudio.com/docs/remote/remote-overview) and [GitHub Codespaces](https://code.visualstudio.com/docs/remote/codespaces), we will aim to make cloud-native development an awesome experience for Java developers.

### Feature Updates

Besides the roadmap, we also have some exciting new product features to share --

#### 1. Embedded JRE in Java extensions

We have good news for you -- With the support of [platform-specific extensions](https://code.visualstudio.com/updates/v1_61#_platform-specific-extensions) from Visual Studio Code, we have now embedded a JRE into our Java extensions, which means you no longer need to worry about configuring a JDK to run the extensions and only need to configure the JDK for your project.

We have also updated our getting started experience and Configure Java Runtime page (Ctrl+Shift+P: "Configure Java Runtime") to reflect this change. For new users, just simply install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), download the Java Development Kit for your project (Java 1.5 or above is supported), create a Java file, and happy coding!

#### 2. Configure Java formatter settings

We have been constantly hearing from developers that they need to configure the formatter settings for their Java code, and sometimes they couldn't find the formatter settings to preview the effects.

To address the formatting needs, we have previously introduced a view where developers can change formatter settings and preview the effects. You can access this feature via two ways:

First option -- Simply bring up the command palette (Ctrl+Shift+P), and run "Java: Open Formatter Settings with Preview" command

[![Formatter with preview](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/formatterpreview.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/formatterpreview.png)

Second option -- Use the "Java: Help Center" (Ctrl+Shift+P, and run "Java: Help Center" command). This will bring up the Java help center page which contains a list of very useful features and shortcuts. On this page, simply click on "Configure Formatter Settings".

[![Help Center](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/helpcenter-1.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/helpcenter-1.png)

Doing either one of the options above will lead you to the formatter settings view, where you can easily change and preview the formatter settings within Visual Studio Code.

[![Formatter](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/formatter.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/01/formatter.png)

### Feedback and suggestions

There will be lots of exciting updates for Java on Visual Studio Code in 2022, and as always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [vscjfeedback@microsoft.com](mailto:vscjfeedback@microsoft.com)

### Resources

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
