---
title: "VS Code: SpringOne Updates, UX Improvements, Community Feedback"
slug: "vs-code-java-august-updates-springone-updates-ux-improvements-community-feedback"
date: "2021-09-20T06:46:49+00:00"
lastmod: "2021-09-23T07:02:45+00:00"
description: "Let's share some exciting updates from the SpringOne 2021 conference, as well as various user experience improvements!"
authors:
  - "nick-zhu"
image: "https://foojay.io/wp-content/uploads/2021/09/Spring-Boot.gif"
categories:
  - "Kotlin"
  - "Spring"
  - "VS Code"
tags:
related_posts:
frozen: false
---

Hi everyone, welcome to the August edition of the Visual Studio Code Java update!

In this article, we are going to share some exciting updates from the SpringOne 2021 conference, as well as various user experience improvements.

When it comes to Java development, we always keep Spring developers in mind. In this year's [SpringOne conference](https://devblogs.microsoft.com/java/join-microsoft-at-springone-2021/ "SpringOne conference"), we shared an update on our Spring tooling support in Visual Studio Code, and showcased various Spring related features in two breakout sessions.

Spring Support on GitHub Codespaces {#h2-0-spring-support-on-github-codespaces}
-------------------------------------------------------------------------------

We are excited to share that Spring framework is fully supported on [GitHub Codespaces](https://aka.ms/Bootiful-Tools "GitHub Codespaces") via partnership with VMWare. This means developers can develop a Spring application seamlessly in a browser-based environment without installing anything on the local machine. Here is a quick demo of starting a Spring Boot application on Codespaces.

![](/images/posts/2021/09/vs-code-java-august-updates-springone-updates-ux-improvements-community-feedback/Spring-Boot.gif)

We had a full demo-focused session at SpringOne to talk about Spring on GitHub Codespaces. If you are interested in this session, [follow the link](https://aka.ms/Bootiful-Tools "follow the link") here to watch the whole recording.

Spring Tooling on Visual Studio Code {#h2-1-spring-tooling-on-visual-studio-code}
---------------------------------------------------------------------------------

In addition to Spring support on GitHub Codespaces, we have also delivered a session to talk about how various Spring tools are supported in Visual Studio Code in general. This includes a full demonstration of how to start a Spring Boot application from scratch as well as opening and running an existing sample project such as Spring PetClinic. We also touched upon Visual Studio Code's rich extension selection that allows developer to deploy Spring applications to cloud directly. You can watch the complete session [using this link](https://aka.ms/Bootiful-Tools "using this link").

Spring ecosystem is a critical part of the Java application development and we will continue to invest in this area and make sure Spring developers have an awesome experience in Visual Studio Code.

Apart from Spring updates, we have made various improvements on user experience in the latest release. To see those new features, please make sure to have the [latest Expansion Pack](https://foojay.io/ "latest Expansion Pack") for Java installed.

Maven Project Creation {#h2-2-maven-project-creation}
-----------------------------------------------------

Previously, when creating a new Maven project from the Explorer, a dialog always pops up in Visual Studio Code and asks the developer if they want to directly open the project. However, we realize in some scenarios developers might want different actions.

To address this, we have added a new setting that allows developer to customize the behavior after a Maven project is created. Depending on the option that is chosen, different actions will be triggered by Visual Studio Code.

* Interactive - Visual Studio Code will ask developer for confirmation to open the project (Default behavior)
* Open - Visual Studio Code will directly open the newly created project
* Add to Workspace - Visual Studio Code will add this project to the current workspace

![](/images/posts/2021/09/vs-code-java-august-updates-springone-updates-ux-improvements-community-feedback/mavenprojectopen.png)

New Java File Creation {#h2-3-new-java-file-creation}
-----------------------------------------------------

"New File Contribution Point" is a new feature that came from recent release of Visual Studio Code. We have adopted this feature to make Java file creation workflow more intuitive and user-friendly

In the "File" menu of Visual Studio Code, you will see a new option called "New File..." (Note the extra dots at the end). When you click on this, Visual Studio Code will prompt you to select the type of file to add. If you select "New Java class" from the dropdown, Visual Studio Code will generate the basic Java class snippet for you and you can then also pick type of Java file (Class/Interface/Enum/etc). Here is a quick demonstration of the feature.

![](/images/posts/2021/09/vs-code-java-august-updates-springone-updates-ux-improvements-community-feedback/New-File.gif)

As part of product improvement process, we recently launched a [Twitter poll](https://twitter.com/foojayio/status/1427179687948734464 "Twitter poll") via [Foojay.io](https://foojay.io/ "Foojay.io"). We really appreciate all the comments and feedback regarding Visual Studio Code Java development experience. There are a few areas we'd like to call out here:

* **Support for large projects.** We are aware of the performance issues when it comes to complicated project strcture, and we have been working diligently to identify those performance bottlenecks. As we shared in our roadmap in June, this is one of our areas of focus for the next few months.
* **Gradle support.** Gradle is one of the top asks from the community. We are close to a milestone that aims to provide much better Gradle support. Please stay tuned.
* **Kotlin.** We are excited to learn that developers are using Visual Studio Code for Kotin development. While we do not have immediate plan to support Kotlin officially, we do already have some great choices from the community (Such as [Code Runner extension](https://marketplace.visualstudio.com/items?itemName=formulahendry.code-runner "Code Runner extension") and [Kotlin Language extension](https://marketplace.visualstudio.com/items?itemName=mathiasfrohlich.Kotlin "Kotlin Language extension")). You can see more Kotlin related extensions[via this link](https://marketplace.visualstudio.com/search?term=kotlin&amp;target=VSCode&amp;category=All%20categories&amp;sortBy=Relevance " via this link").

Please don't hesitate to try our product! Your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to leave us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose "Open an issue") on our GitHub Issues page
* Resources

Here is a list of links that are helpful to learn Java on Visual Studio Code.

[Learn more about Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java "Learn more about Java on Visual Studio Code").
