---
title: "Java on Visual Studio Code – May 2023"
date: "2023-06-13T11:03:09+00:00"
lastmod: "2023-06-13T11:03:10+00:00"
description: "Tons of new features covering performance improvement, user experience as well as Spring Boot integration."
authors:
  - "nick-zhu"
image: "projectexplorer.gif"
categories:
  - "Developer Tools"
  - "Tools"
  - "VS Code"
related_posts:
  - "say-goodbye-to-project-files-in-visual-studio-code"
  - "two-million-java-developers-on-visual-studio-code-november-2022-update"
  - "boldness-in-refactoring"
frozen: false
---

**Hi everyone, welcome to our May update for Visual Studio Code Java! In this article, we have tons of new features covering performance improvement, user experience as well as Spring Boot integration, so let's get started!**

#### Built-in Java Profile Templates in VS Code

[Profiles](https://code.visualstudio.com/docs/editor/profiles) is a recent feature from VS Code that allows you to quickly switch your editor extensions, settings, and UI layout based on your current project or task. Starting in VS Code version 1.78, we have provided two built-in Java profile template for you to use. The two Java profiles are

* Java General -- Provides a good starting point for all general Java work.
* Java Spring -- Provides a good starting point for Spring developers

[![Profile template dropdown](https://code.visualstudio.com/assets/updates/1_78/profile-template-dropdown.png)](https://code.visualstudio.com/assets/updates/1_78/profile-template-dropdown.png)

Once you switch to these profiles, you can easily get started for your Java projects and customize the profiles further. Please let us know if these templates can be improved at [https://github.com/Microsoft/vscode-java-pack.](https://github.com/Microsoft/vscode-java-pack)

For details for both profiles, please visit the [official documentation](https://code.visualstudio.com/docs/editor/profiles#_java-general-profile-template) here.

#### New Java Project Explorer UI Updates

We introduced our new Java Project Explorer UI last month, and we have been making new improvements. Here's the list of improvements we made

* A filter button that allows you to hide/show non-Java resources
* Automatically hide files you configure from settings.json
* Different right click action (New Java class, package, file) depending on the current context

Here's a demo that covers all the features above. Please let us know if you have any [feedback](https://github.com/Microsoft/vscode-java-pack) regarding these UI updates.

![Java Project Explorer New UX](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/05/projectexplorer.gif)

#### Whitelist for exception types

We have also added a debugging feature that allows you to specify a white list of exception types, so you can break on them without setting breakpoints, e.g. NullPointerException. This is a useful feature when you are expecting some kind of exception types or catching unexpected bugs.

You can set these exception types using this setting: "java.debug.settings.exceptionBreakpoint.exceptionTypes". Here's a quick demo.

![Excpetion Type](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/05/whitelist.gif)

#### Project import time improvement using Maven parallel download

In addition to user experience improvements, we also made important performance improvements. Starting in version 1.17, we introduced parallel downloading for downloading Maven dependencies (Previously, all dependency downloading was serial).

Since this change was added, we have noticed significant improvement time decrease for project import. We did some internal testing on fresh projects on Codespaces on Spring-petclinic project. Here's the result.

| Version | Time Cost (Average) |
|---------|---------------------|
| 1.16    | 2 min 58s           |
| 1.17    | 1 min 41s           |

The data shows project import time is indeed improved by the Maven parallel download mechanism and we hope you try out the extension as well.

#### Run Spring applications using Spring profiles

Lastly, we want to share an update for Spring Boot dashboard. Profiles are a core feature of the Spring framework --- allowing us to map our beans to different profiles --- for example, dev / test, or different data sources.

In our latest release, we have supported starting a Spring Boot application with a selected Spring profile from the Spring Boot dashboard directly using the UI. The way to use this feature is to right click on a Spring application in the Apps panel and select "Run with Profile". Here's a demo.

![Spring profile](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/05/springprofile.gif)

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
