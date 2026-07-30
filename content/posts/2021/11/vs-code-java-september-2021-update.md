---
title: "VS Code Java September 2021 Update | Foojay.io Today"
slug: "vs-code-java-september-2021-update"
date: "2021-11-01T08:49:12+00:00"
lastmod: "2021-11-01T08:51:57+00:00"
description: "Covering the new release of Gradle extension, more convenient Code Actions and the recent 1.0 language server release!"
authors:
  - "nick-zhu"
image: "https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2021/10/dependency.png"
categories:
  - "Release Notes"
  - "VS Code"
tags:
related_posts:
  - "announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code"
  - "vs-code-java-august-updates-springone-updates-ux-improvements-community-feedback"
  - "vs-code-java-july-2021-update-new-testing-experience-maven-improvements-and-product-roadmap-progress-update"
frozen: false
---

Hi everyone, welcome to the September edition of the Visual Studio Code Java update!

In this article, we are going cover the new release of Gradle extension, more convenient Code Actions and the recent 1.0 language server release.

Language Server for Java™ 1.0 Release {#h2-0-language-server-for-java-1-0-release}
----------------------------------------------------------------------------------

The 1.0 release of the Language Server for Java™ was officially released recently! We believe this is an important milestone and result of a multi-year collaboration between Microsoft and Red Hat. Please visit this [special announcement here on Foojay](https://foojay.io/today/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/) to see the release highlights as well as future plans in detail.

Gradle for Java Extension Release {#h2-1-gradle-for-java-extension-release}
---------------------------------------------------------------------------

Better Gradle support has been one of the top asks in the community. To better address the Gradle needs for developers, we are excited to announce that we have now released the "[Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle)" extension (previously called "Gradle Tasks" extension). This extension was originally started by [@badsyntax](https://github.com/badsyntax) and is now maintained by Microsoft. Please note that this extension is not yet part of [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and you may need to download it separately.

Let's take a look at some highlights of this new version:

### Local Gradle installation support {#h3-2-local-gradle-installation-support}

In previous version, Gradle wrapper in the project folder is the requirement for the extension. There was a feature request ([Issue #1004](https://github.com/microsoft/vscode-gradle/issues/1004)) to support Gradle projects without wrapper, this feature was added to the recent release. Developers can now use the following settings to control the activation behavior.

* java.import.gradle.wrapper.enabled
* java.import.gradle.version
* java.import.gradle.home

More details about these settings can be found at

### Dependency Management and Project View {#h3-3-dependency-management-and-project-view}

Another exciting new feature is the project dependency view. Developers can now easily view the dependencies in a project from the extension.

To view the dependencies of a project, simply click on the "Dependencies" item and all dependencies of a project is shown below. These dependencies are grouped by Gradle configurations and you can expand each configuration to see the dependencies in detail. For omitted dependency (marked with a (\*)), you can click on the inline button on the right to reveal the previously listed dependency.

[![Gradle dependency](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2021/10/dependency.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2021/10/dependency.png)

### Gradle Authoring Experience (Groovy) {#h3-4-gradle-authoring-experience-groovy}

Gradle file authoring experience in Groovy is also greatly improved! There are a few new features we want to highlight:

#### Auto completion

The Gradle language server supports basic auto completions for a Gradle file, when you're trying to type a Gradle closures or properties in a Gradle script, the extension will automatically suggest available Gradle closures for you.

![](/images/posts/2021/11/vs-code-java-september-2021-update/autocomplete.gif)

When you're typing a dependency in "dependencies" closure, the extension will automatically search in the Maven central and suggest result for you.

![](/images/posts/2021/11/vs-code-java-september-2021-update/autosuggestdependency.gif)

#### Syntax Highlighting

When opening a Groovy Gradle file, the Gradle language server will start and provide language features for you. Basically, we offer Groovy syntax highlighting (using VS Code default style) in Gradle files. After language server starts, it will analyze the opened Gradle file and provide semantic tokens information, providing more precise highlighting results.

![](/images/posts/2021/11/vs-code-java-september-2021-update/syntax.gif)

#### Document outline

The Gradle language server will provide the document outline for the current Gradle file. This type of view will help you to navigate to any part of the Gradle file easily

![](/images/posts/2021/11/vs-code-java-september-2021-update/documentoutline.gif)

#### Error reporting

The Gradle language server will use Groovy compile engine to analyze the Gradle build file and report syntax errors if exist. It will also get script classpaths from Gradle Build so that it can report compilation errors. The [Gradle default imports](https://docs.gradle.org/current/userguide/writing_build_scripts.html#script-default-imports) are supported.

[![Gradle error reporting](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2021/10/erroreporting.jpg)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2021/10/erroreporting.jpg)

There are many more features in this extension. Please visit [the GitHub documentation](https://github.com/microsoft/vscode-gradle#feature-overview) to see the full list of features! You can follow [this link](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) to get this Gradle extension.

### More Convenient Code Actions {#h3-5-more-convenient-code-actions}

We have been constantly receiving feedback that the current code actions are sometimes hard to find. Previously, developer has to right click, select "Source Action" and then find all the Java code actions in the menu.

Now, developers can simply use the lightbulb icon (Quick Fix) to generate common Java functions. Here is a list of scenarios we support

* Generate Getters and Setters
* Generate hashCode() and equals()
* Generate toString()

Here is a quick demo:  
![](/images/posts/2021/11/vs-code-java-september-2021-update/codeactions.gif)

### Feedback and Suggestions {#h3-6-feedback-and-suggestions}

Please don't hesitate to try our product! Your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to leave us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page

### Resources {#h3-7-resources}

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
