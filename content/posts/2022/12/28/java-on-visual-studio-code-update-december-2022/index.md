---
title: "Java on Visual Studio Code Update – December 2022"
slug: "java-on-visual-studio-code-update-december-2022"
date: "2022-12-28T08:11:44+00:00"
lastmod: "2022-12-28T08:11:45+00:00"
description: "Hi everyone, we are near the end of year. Looking back on 2022, there are a few highlights that we wanted to share with you all!"
authors:
  - "nick-zhu"
image: "springbootdashboard-1.png"
categories:
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "two-million-java-developers-on-visual-studio-code-november-2022-update"
  - "java-on-azure-tooling-update-october-2022"
  - "java-on-azure-tooling-update-august-2022"
enlighterjs: true
frozen: false
---

Hi everyone, we are near the end of year. Looking back on 2022, there are a few highlights that we wanted to share with you all!

### Highlights from 2022 {#h3-0-highlights-from-2022}

#### Major Visualization Experience Upgrade for Spring Boot Extensions

Starting earlier this year, we began to make a series of improvements on [Spring Boot dashboard](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack), aiming to increase Spring developer's productivity on Visual Studio Code.

This includes the beans and endpoints mapping view, more live information display for Spring applications and the brand new memory as a graph.

We hope that these improvements make it easier to develop Spring applications and observe the state of application when it's running.

[![Spring Boot dashboard smaller](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/06/springbootdashboard-1.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/06/springbootdashboard-1.png)

#### Fundamental Coding Experience Improvements

Coding experience matters.

In 2022, We have made a series of important improvements to make Java developer's daily coding life easier than ever.

This includes [postfix completion like IntelliJ IDEA](https://devblogs.microsoft.com/java/two-million-java-developers-on-visual-studio-code-november-2022-update/), [more shortcuts to generate code snippets](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-march-2022/), [signature help optimization](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-may-2022/) and so on.

Fundamental experience will continue to be our focus going forward.

#### Built-in Lombok support

Project Lombok is a popular and widely used Java library that is used to minimize or remove the boilerplate code.

Starting in July, our Java extensions can directly support projects that are using Lombok without installing any additional extensions.

We also want to thank [@GabrielBB](https://github.com/GabrielBB) who started the original Lombok extension. You can find how to enable the Lombok support in [this blog post](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-july-2022/).

#### Two million Java developers on Visual Studio Code

In November, we shared the news that [there were two million Java developers](https://devblogs.microsoft.com/java/two-million-java-developers-on-visual-studio-code-november-2022-update/) on Visual Studio Code, this was an important milestone and we wanted to thank for all the support from the community, thank you!

In addition to the highlights from the 2022, let's look at the new features from the latest release!

### December 2022 Update {#h3-1-december-2022-update}

#### Spring Boot -- Live Memory View

As the highlight of this month's update, we are introducing a new graphical memory view as the newest addition to the Spring Boot Dashboard. This feature is a result of collaboration between VMWare and Microsoft (special thanks to [@vudayani-vmw](https://github.com/vudayani-vmw), [@martinlippert](https://github.com/martinlippert), and [@Eskibear](https://github.com/Eskibear)).

The memory view visualizes the memory and the garbage collection activities of a running Spring Boot application, much in the same way than the Spring Boot Dashboard already provides you with insights into running Spring Boot applications like active beans and request mappings.

There are four graphical real-time visualizations available under the memory view:

* Heap memory -- provides a stacked overview of the different heap areas, the total memory used and total memory size in the JVM
* Non-heap memory -- displays a stacked overview of the different non-heap regions in JVM relative to the total memory used and total memory size
* Gc Pauses -- depicts the frequency and duration of pause time for a GC event
* Garbage Collection -- displays the count of the GC events in the application

The memory view uses the Spring Boot Actuator data to gather the data from the running application, in the same way the other live data from running Spring Boot applications is gathered from running Spring Boot applications. So as soon as you have the Spring Boot Actuator dependency on your project, you will be able to use the live memory view.

In contrast to the beans and request mapping views of the Spring Boot perspective in VSCode, the memory view displays information from one specific running Spring Boot application only, it does not visualize the data from multiple running applications simultaneously. Therefore, you can select which application the view should visualize directly inside the view.

Here is a demo for this live memory view feature. Please note this feature is only available when you start an application from Spring Boot dashboard.

#### Annotation processing support in Gradle

Annotation processing is a Java compilation option which has been around since Java 5. It enables the generation of additional files during compilation, such as classes or documentation.

If we are using Gradle and want to use annotation processing, we do this by marking a particular dependency as part of the annotationProcessor dependency configuration. For example:

```
dependencies {
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.3.1.Final'
    ...
}
```


In our latest release, we have supported annotation processing in our [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) extension, so if you have annotationProcessor marked in your dependencies, you will be able to see your generated files during compilation.

#### User Experience -- Better Build Error Message Prompt

When our extensions detect a build error in the project, usually there is a pop-up that prompts the user to take certain actions. However, previously there are certain limitations with this pop-up. In our latest release, we have made improvements regarding two main scenarios.

* **Projects without build tools (Unmanaged folder).** Previously, if there are multiple folders in the workspace, and only one of the folders has errors, and the user is trying to run the other folders, we will still prompt the user with build error pop-up, which is not good user experience. We have improved this behavior and now we will now only prompt the build error message if the current folder has an issue. Here's a demo.

<!-- -->

* **Maven project.** We have also made improvements for Maven project. If you have a multi-module project and there is an error in one of the dependencies, we will prompt you with the build error message. However, as soon as that error is fixed, the prompt will be gone and you will be able to run the application smoothly. Please see the demo below.

Last but not least, you might have noticed, the prompt now has changed to "Continue", "Always Continue", Fix" to be more straightforward to the user.

#### Visual Studio Code Java in 2023

Once again, we want to thank everyone for all the awesome support in the past year.

We wish everyone a merry Christmas and happy new year.

There will be many great updates coming in 2023, so please stay tuned, and see you in next year!

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

Resources {#h2-2-resources}
---------------------------

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
