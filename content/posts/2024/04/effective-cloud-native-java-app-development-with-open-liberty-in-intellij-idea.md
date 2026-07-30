---
title: "IntelliJ IDEA and Open Liberty - Effective Java app development"
slug: "effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea"
date: "2024-04-12T13:36:25+00:00"
lastmod: "2024-04-12T13:38:09+00:00"
description: "How to use Liberty Tools for IntelliJ IDEA to enable rapid, easy, and efficient development of cloud-native Java applications with Liberty."
canonical: "https://developer.ibm.com/articles/awb-effective-cloud-native-development-open-liberty-intellij-idea/"
authors:
  - "grace-jansen"
image: "https://foojay.io/wp-content/uploads/2024/03/ScreenshotLibertyToolsIntelliJ.png"
categories:
  - "Cloud"
  - "Developer Tools"
  - "Gradle"
  - "IntelliJ IDEA"
  - "Jakarta EE"
  - "Java"
  - "Maven"
  - "Tools"
tags:
related_posts:
  - "effective-cloud-native-development-open-liberty-vs-code"
  - "effective-cloud-native-development-eclipse-ide-open-liberty"
  - "getting-started-with-intellij-idea"
  - "whats-new-in-the-july-2026-azul-payara-release"
frozen: false
---

**See how you can use Liberty Tools for IntelliJ IDEA to enable rapid, easy, and efficient development of cloud-native Java applications with Open Liberty and WebSphere Liberty**

When it comes to integrated development environments, within the Java community, IntelliJ IDEA is the most popular IDE amongst professional developers. It is the preferred IDE of choice by two-fifths of developers, making up the biggest majority of IDE users in this community, according to JRebel's [2023 Java Developer Productivity Report](https://www.jrebel.com/resources/java-developer-productivity-report-2023).

This well-established and highly popular IDE provides a fantastic and highly efficient environment in which to develop effective cloud-native Java applications, significantly improving the development experience. However, in order to truly unlock this improved development experience and enhanced productivity, we must ensure we have the most appropriate and useful plugins.

In this article, we'll explore the Liberty Tools plugin for IntelliJ IDEA and how this can help enable fast, easy and efficient development of cloud-native Java applications with both Open Liberty and WebSphere Liberty.

*If you've not heard of Open Liberty before, Open Liberty is a lightweight, open framework, great for building fast and efficient cloud-native Java applications. To learn more about what Liberty can offer, check out the "[Six reasons why Open Liberty](https://developer.ibm.com/articles/6-reasons-why-open-liberty-is-an-ideal-choice-for-developing-and-deploying-microservices/)" and "[Why cloud-native Java developers love Liberty](https://developer.ibm.com/articles/why-cloud-native-java-developers-love-liberty/)" articles. Alternatively, you can get hands-on with this cloud-native runtime with our [Getting started with Open Liberty guide](https://openliberty.io/guides/getting-started.html).*

The open source [Liberty Tools for IntelliJ IDEA](https://ibm.biz/LibertyToolsIntelliJIDEAMarketplace), is a useful plugin when developing your application with Open Liberty. The Liberty Tools are a set of intuitive developer tools that provide a simplified yet powerful development experience and support popular IDEs, including IntelliJ IDEA, [Eclipse IDE](https://developer.ibm.com/articles/awb-effective-cloud-native-development-open-liberty-eclipse-ide/), and [Visual Studio Code](https://developer.ibm.com/articles/awb-effective-cloud-native-development-open-liberty-vs-code/).

The Liberty Tools for IntelliJ IDEA plugin can help with all stages of the extended development lifecycle now expected from cloud-native development teams, including helping you in developing, building, testing, deploying, and managing your applications -- all within your favorite IDE, IntelliJ!

![](/images/posts/2024/04/effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea/Screenshot-2024-04-03-at-13.21.37-700x437.png)

Key Capabilities of Liberty Tools {#h2-0-key-capabilities-of-liberty-tools}
---------------------------------------------------------------------------

These tools introduce capabilities that really empower you to develop, test, debug, and manage applications without having to leave your IDE, including:

* Easily view and access all detected Liberty projects in your IDE in the Liberty tool window
* Rapid, iterative development with Liberty dev mode
* Effective testing and debugging all within the IDE
* Editing assistance for you to easily make changes to your Liberty configuration files
* Coding assistance for you to write applications that use Jakarta EE (9.x and later) and MicroProfile (3.x and later) APIs, including validations, quick fixes, and completions

In this article, we'll dive further into these capabilities. If you want to view a deep dive tutorial on this tool, then watch the [Developer Deep Dive of Liberty Tools for IntelliJ IDEA](https://ibm.biz/LibertyToolsIntelliJIDEAVideo) video.

{{< youtube O-dN3yHSPdQ >}}

<br />

View and access all detected Liberty projects in your IDE in the Liberty tool window {#h2-1-view-and-access-all-detected-liberty-projects-in-your-ide-in-the-liberty-tool-window}
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Liberty Tools automatically detects Liberty Maven or Gradle projects. These projects are added to a special Liberty tool window in the IntelliJ IDEA. This can be accessed through the Liberty tab available in the menu on the very right-hand side of the IDE.

From this window, you can access a command menu to manage your Liberty projects. What this means, is that you now don't have to spend time creating and managing Liberty instances, freeing up your time to focus on the code itself and enabling greater developer productivity in your teams.

![Screenshot of the Liberty Tools plugin in IntelliJ IDEA](/images/posts/2024/04/effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea/LibertyToolsIntelliJ2-700x406.png)

Rapid, iterative development with Liberty dev mode {#h2-2-rapid-iterative-development-with-liberty-dev-mode}
------------------------------------------------------------------------------------------------------------

Liberty's hot reload functionality, named "dev mode", enables automatic detection, recompilation, and deployment of code changes whenever you save a new change. Liberty dev mode enables rapid, iterative development in a manner that aligns with agile development practices that are recommended for cloud-native applications.

It can also run unit and integration tests automatically after changes and can attach a debugger to the running server to step through your code at any time. Liberty Tools brings these dev mode features directly into the command menu for the Liberty projects in your editor. With just a few clicks, you can start and stop your Liberty application, run tests, and view test reports.

To try this for yourself, follow the steps in the Liberty Tools user guide to [run your application on Liberty using dev mode](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#view-your-applications-test-reports).

You can also run your application in dev mode in a container through the 'Start in container' action. When dev mode runs with container support, it builds a container image and runs the container. For more information on dev mode for containers, check out the [Liberty Maven devc goal](https://github.com/OpenLiberty/ci.maven/blob/main/docs/dev.md#devc-container-mode) or, alternatively, see the [Liberty Gradle libertyDevc task](https://github.com/OpenLiberty/ci.gradle/blob/main/docs/libertyDev.md#libertydevc-task-container-mode).

Effective testing and debugging within the IDE {#h2-3-effective-testing-and-debugging-within-the-ide}
-----------------------------------------------------------------------------------------------------

When your application is running on Liberty using dev mode, you can easily run the tests provided by your application. To do this, select the 'Run tests' command in the Liberty tool window, or alternatively, you can simply press enter in the terminal running Liberty in dev mode.

Additionally, you can also configure Liberty to automatically re-run tests after you've made changes by setting "hotTests" parameter to "true". After the application tests finish running, you can access the test reports that were generated. The reports will vary depending on what build tool you have used.

You can follow the steps in the Liberty Tools user guide to [run your application's tests](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#run-your-applications-tests) and [view your application's test results](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#view-your-applications-test-reports).

You can use the Liberty run/debug configuration to customize the parameters for the dev mode start command and to start dev mode with the debugger automatically attached to the Liberty server JVM that runs your application.

To do this, you'll first need to create or select a Liberty Run/Debug Configuration through the IntelliJ Run/Debug Configuration menu, and then select the Debug action in the IDE menu. If you're unsure of how to create a new Liberty run/debug config, you can access detailed instructions on how to achieve this in the [Liberty Tools for IntelliJ IDEA User Guide](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#create-a-liberty-rundebug-configuration).

![Screenshot of IntelliJ Run/Debug Configuration menu](/images/posts/2024/04/effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea/Screenshot-2024-04-03-at-13.23.47-700x397.png)

For more on this, you can see the steps required in the Liberty Tools user guide section: [debug your application](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#debug-your-application).

Editing assistance for configuration files {#h2-4-editing-assistance-for-configuration-files}
---------------------------------------------------------------------------------------------

You can also use Liberty Tools to get Liberty configuration editing assistance through the [Liberty Config Language Server](https://github.com/OpenLiberty/liberty-language-server#liberty-config-language-server), such as [code completion, diagnostics, and quick-fixes](https://github.com/OpenLiberty/liberty-language-server#features), in Liberty server.xml, server.env, and bootstrap.properties files.

To use Liberty-specific code completion, press `Ctrl + Space` or `Cmd + Space` and a drop-down list of completion suggestions will appear. In addition to this, by hovering over existing features defined within the server.xml, a description for each of these will also appear, helping developers to know exactly what they're using in their applications.

This hover-over support is also enabled for other xml elements too. All of this saves developers time and additionally means that they don't have to go and find the correct documentation to find this information -- promoting further productivity gains. Follow the steps in the Liberty Tools user guide for [configuring a Liberty server with configuration assistance](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#configure-a-liberty-server) to try this for yourself.

Coding assistance for Jakarta EE and MicroProfile APIs {#h2-5-coding-assistance-for-jakarta-ee-and-microprofile-apis}
---------------------------------------------------------------------------------------------------------------------

Another feature offered by the Liberty Tools plugin is coding assistance. This provides helpful language-support features such as code completion, diagnostics, and quick-fixes in configuration and application files for Jakarta EE and MicroProfile APIs.

Just as with the editing assistance, this can also be accessed by pressing Ctrl + Space or Cmd + Space and a drop-down list of code snippet suggestions appears. This can range from creating an entire REST class, to adding POST, GET, DELETE methods, and more.

The Jakarta EE API coding assistance is offered through [Eclipse LSP4Jakarta](https://github.com/eclipse/lsp4jakarta#eclipse-lsp4jakarta) and the MicroProfile EE API coding assistance is offered through [Eclipse LSP4MP](https://github.com/eclipse/lsp4mp#eclipse-lsp4mp---language-server-for-microprofile), the Language Servers for Jakarta EE and MicroProfile.

If you'd like further resources for this, you can explore the Liberty Tools user guide and follow the steps laid out in this to [develop with Jakarta EE and MicroProfile APIs with coding assistance](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md#develop-with-jakarta-ee-and-microprofile-apis).

Start using Liberty Tools in IntelliJ IDEA {#h2-6-start-using-liberty-tools-in-intellij-idea}
---------------------------------------------------------------------------------------------

Before you can use Liberty Tools in IntelliJ IDEA, you must satisfy these requirements:

* Java 17 (or later) is needed to run the extension (but you can choose a different version of Java to run your application)
* IntelliJ IDEA 2023.1 or later
  * *NOTE: (IntelliJ IDEA 2023.1 and above come bundled with Java 17)\[<https://intellij-support.jetbrains.com/hc/en-us/articles/206544879-Selecting-the-JDK-version-the-IDE-will-run-under>?\], so no extra set up is required*
* Install the Liberty Tools for IntelliJ IDEA plugin either from the [JetBrains marketplace](http://plugins.jetbrains.com/plugin/14856-liberty-tools) or directly from the IDE
* Liberty Maven plugin or Liberty Gradle plugin configured in the `pom.xml` or `build.gradle` file for your app. Use the most recent version of the plugin to get important bug fixes.

There are comprehensive instructions for how to install and use this plugin available on [GitHub](https://github.com/OpenLiberty/liberty-tools-intellij/blob/main/docs/user-guide.md).

When installed, the Liberty tool window and corresponding Liberty actions are available in the Liberty tab on the right-hand side of the IDE.

The Liberty tool window is automatically populated with detected projects that are already properly configured to run on Liberty and use Liberty dev mode.

![Screenshot of where the Liberty Tools window can be located within IntelliJ IDEA](/images/posts/2024/04/effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea/Screenshot-2024-04-03-at-13.33.06-700x400.png)

If you do not have any apps in your current workspace, you can [create a starter application](https://openliberty.io/start/) and import.

Once you import an app, refresh the Liberty tool window tab by clicking the refresh icon in the IDE.

![Screenshot of where to find the refresh icon in IntelliJ IDEA](/images/posts/2024/04/effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea/Screenshot-2024-04-03-at-13.33.48-700x405.png)

Summary and next steps {#h2-7-summary-and-next-steps}
-----------------------------------------------------

With the Liberty Tools plugin for IntelliJ IDEA, you can efficiently develop, deploy, debug, test, and manage your cloud-native Java applications.

Now that you have Liberty Tools set up in your IDE, why not try using it with some of the [Open Liberty guides or tutorials](http://openliberty.io/guides/?utm_source=ibmd&amp;amp;utm_medium=article&amp;amp;utm_content=idevscode)?

<br />

<br />
