---
title: "Java on Visual Studio Code - May Update | Foojay.io Today"
slug: "java-on-visual-studio-code-may-update"
date: "2022-06-20T14:06:37+00:00"
lastmod: "2023-06-13T11:01:05+00:00"
description: "Exciting improvements to our user experience regarding signature help and code completion as well as new Gradle features."
authors:
  - "nick-zhu"
image: "/images/posts/2022/06/java-on-visual-studio-code-may-update/signaturehelp.png"
categories:
  - "Release Notes"
  - "VS Code"
tags:
related_posts:
  - "java-on-visual-studio-code-update-april-2022"
  - "java-on-visual-studio-code-update-february-2022"
  - "java-on-visual-studio-code-update-january-2022"
frozen: false
---

Hi everyone, welcome to the May update of Visual Studio Code Java.

In this month's update, we are going to share exciting improvements to our user experience regarding signature help and code completion as well as new Gradle features.

Let's get started!

#### **Signature Help Improvement**

Signature Help displays the signature of a method in a tooltip when a user types the parameter list start character (typically an opening parenthesis). In our latest release, we have made some major improvements to this feature:

* We added the capability so that the signature help can be triggered automatically
* We also added two settings to control **1)** whether the signature help needs to triggered automatically **2)** whether the detailed method description needs to show automatically (this means **both** the signature and detailed documentation of the method will be displayed)

Here is a demonstration of this feature

The settings to control how you would like to customize the signature help behavior can be found in the following screenshot:

[![Signature Help Setting](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/05/signaturehelp.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/05/signaturehelp.png)

If you are interested, you can also see a more detailed list of how you can utilize these improvements in the [original PR](https://github.com/eclipse/eclipse.jdt.ls/pull/2039) here

#### **Control Insert / Replace Mode for Code Completion**

In Visual Studio Code, when you accept a code completion suggestion, you can either overwrite or insert. This behavior can be changed in the "Editor \> Suggest: Insert Mode" setting.

However, in the past, this feature might not work for Java code as it requires LSP to adopt it.

In our latest release, we have made some changes so now it fully works with our Java extensions. In addition, you can use **Shift** key to switch between two modes on the fly!

The setting to set this preference can be found here:

[![Insert replace code completion](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/05/insertreplace.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/05/insertreplace.png)

We hope these user experience improvements can really help to speed up your coding productivity!

#### **Gradle Improvements**

As we shared in our [roadmap](https://devblogs.microsoft.com/java/java-on-visual-studio-code-update-january-2022/) previously, build tools is one of our major focuses. We are glad to share that in this area we are working very closely with [Gradle Enterprise](https://gradle.com/) to improve our [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) extension.

In May, we have a few things to highlight:

##### **Pin Favorite Task**

When working with Gradle projects, it is quite common for a developer to repeatedly run some [tasks](https://docs.gradle.org/current/dsl/org.gradle.api.Task.html). To save developers time, we have now provided a feature that allows you to "pin" a task at the top of the project view so that it's easy to find the task and run it again. You can even pass it with an argument. Here is a demo of this feature.

##### **Bug fixes -- Support Multi-level Projects and Chinese Encoding Issue**

An [issue](https://github.com/microsoft/vscode-gradle/issues/1194) was reported from the community that when our extension searches for a task in a multi-level project, it will duplicate the task indefinitely. This is because of the incorrect usage of a Gradle API call. We have [fixed](https://github.com/microsoft/vscode-gradle/pull/1224) this issue in our latest release.

Another [issue](https://github.com/microsoft/vscode-gradle/issues/1208) that would cause garbled Chinese characters was also fixed. Both issues were reported from the GitHub, and we thank the community for finding them and providing the sample projects with repro steps. Please continue sharing feedback with us.

**Feedback and suggestions**

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

**Resources** {#h2-0-resources}
-------------------------------

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
