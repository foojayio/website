---
title: "Java on Visual Studio Code Update – April 2022 | Foojay.io Today"
slug: "java-on-visual-studio-code-update-april-2022"
date: "2022-04-25T08:14:40+00:00"
lastmod: "2022-04-25T08:16:19+00:00"
description: "Java 18 support, inlay hints for parameter names, new language server status UX, lambda expression support in debugging and more!"
authors:
  - "nick-zhu"
image: "/images/posts/2022/04/java-on-visual-studio-code-update-april-2022/lambda.gif"
categories:
  - "Release Notes"
  - "VS Code"
tags:
related_posts:
  - "java-development-with-vs-code-on-the-raspberry-pi"
  - "java-on-visual-studio-code-update-february-2022"
  - "java-on-visual-studio-code-update-january-2022"
frozen: false
---

Hi everyone, welcome to the April update of Visual Studio Code Java.

This time we are bringing you many exciting updates regarding our fundamental experience improvement including Java 18 support, inlay hints for parameter names, new language server status UX, lambda expression support in debugging and so on.

Let's get into it.

### Microsoft JDConf {#h3-0-microsoft-jdconf}

Before we start our update, we want to share an exciting news: Microsoft JDConf is coming up! Join us for the second JDConf event on May 4 -- 5, 2022.

It is a virtual, Java focused conference where developers can come together to share interesting topics and stay engaged.

Our goal with this event is to highlight external speakers and showcase the great work that's going on across the Java community. You can get more information and register via this link:

### Java 18 Support {#h3-1-java-18-support}

We have always been targeting to support the latest Java technology.

With Java 18 reaching General Availability in March this year, we have enabled the support for Java 18 in our latest version as well.

This means you can now use Java 18 in your projects!

### Support Inlay Hints for Parameter Names {#h3-2-support-inlay-hints-for-parameter-names}

Have you ever wondered about which parameter you are filling for in some unfamiliar methods? Don't worry, Inlay Hints is here to help you.

In our latest release, we have enabled this feature for Java projects.

Once enabled, Visual Studio Code will show a hint on the parameter name in the method signature, and there are three modes of this feature.

* none (Disable parameter name hints)
* literals (Enable parameter name hints only for literal arguments) (Default)
* all (Enable parameter name hints for literal and non-literal arguments)

You can change this setting at any time by going to the "inlay" setting under Java

[![Inlay Setting](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/inlaysetting.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/inlaysetting.png)

Here is a demo of the feature in action:

![Inlay demo](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/inlay.gif)

### New Java Language Server Status UX {#h3-3-new-java-language-server-status-ux}

Previously, there are multiple items on the status bar to show the current state of the Java language server, this can make the UX crowded and confusing sometimes.

Since version 1.65, Visual Studio Code [has finalized its new Language Status Item APIs](https://code.visualstudio.com/updates/v1_65#_language-status-items).

In our latest release, we not only adopted the new API, but also made some minor adjustments.

If you update to the latest version of Visual Studio Code and [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), you will see the language server items have been merged into a unified item, not only making it more concise, but also providing more actions for developers to access from a single place.

Here is a demo:

![LS demo](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/languageserverstatus.gif)

As you can see from the demo above, currently the language status item provides the following information and action:

\| Information \| Click Action \|  

\| Build Status \| Detailed build information in the terminal \|  

\| Build file (Whether a build configuration exists or not) \| Navigate to the actual Maven or Gradle build configuration file \|  

\| Project JDK information \| Configure Java Runtime \|

If there is an issue or error, you will see an indicator on the status icon and you can click on "Show problems" to reveal the error details.

[![Language Server Error](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/languageservererror.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/languageservererror.png)

We hope this improvement will provide better user experience about the state of the Java language server and please share your feedback with us so we can continue to improve this area.

### Code action to extract lambda body to method {#h3-4-code-action-to-extract-lambda-body-to-method}

As a minor feature update, we have also added a code action (from Quick Fix) to allow developers to easily extract lambda body to a method. Here is a quick demo:

![Lambda demo](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/lambda.gif)

### Lambda expression evaluation support in debugging {#h3-5-lambda-expression-evaluation-support-in-debugging}

Lastly, we wanted to provide an update on lambda expression support in debugging. Previously, there was [an issue](https://github.com/microsoft/vscode-java-debug/issues/754) that prevents developer from properly evaluating variables inside lambda expressions when debugging. In our latest release, we have fixed this issue.

Here is a quick demo of evaluating a variable from enclosing methods when using lambda in a chain call:

![Debugging demo](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/04/debugginglambda.gif)

For complete list of supported case, please visit the [original issue](https://github.com/microsoft/vscode-java-debug/issues/754).

### Feedback and suggestions {#h3-6-feedback-and-suggestions}

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

### Resources {#h3-7-resources}

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
