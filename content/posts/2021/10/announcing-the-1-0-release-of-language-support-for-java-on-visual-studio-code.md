---
title: "1.0 Release of Language Support for Java on Visual Studio Code"
slug: "announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code"
date: "2021-10-25T07:53:21+00:00"
lastmod: "2021-10-25T08:30:55+00:00"
description: "We are excited to announce the 1.0 release of Language Support for Java™ by Red Hat on Visual Studio Code."
authors:
  - "nick-zhu"
image: "https://foojay.io/wp-content/uploads/2021/10/java17.png"
categories:
  - "Release Notes"
  - "VS Code"
tags:
related_posts:
frozen: false
---

We are excited to announce the 1.0 release of [Language Support for Java™ by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.java "Language Support for Java™ by Red Hat") on Visual Studio Code.

We believe this is a significant milestone for Java support on Visual Studio Code and the result of a multi-year collaboration between Microsoft, Red Hat and the entire Visual Studio Code and Java community. This release wouldn't have been possible without your support and we want to thank everyone who has provided feedback or made contributions.

Release highlights {#h2-0-release-highlights}
---------------------------------------------

When we started with our very first release, our goal was to provide an outstanding Java development experience on Visual Studio Code. Since then, we have maintained a release cycle of 1-2 times per month, through many iterations.

In this 1.0 release, we want to highlight several improvements:

### Java 17 support {#h3-1-java-17-support}

Our focus has always been to support the latest technology provided by the Java language whenever possible. In our 1.0 release, Java 17 is now officially supported on our Java extensions.

![](/images/posts/2021/10/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/java17.png)

### Performance Improvements {#h3-2-performance-improvements}

Performance is also another area of improvement in this release. Thanks to the new features in the LSP specification, we were able to defer certain computations for better performance in many scenarios.

In addition, default JVM options are used to improve the user experience of larger and more complicated projects. Last but not least, we made many [smaller improvements](https://github.com/eclipse/eclipse.jdt.ls/issues?q=is%3Aissue+label%3Aperformance+updated%3A%3E2021-08-01 "smaller improvements") to boost responsiveness of the language server.

### Type Hierarchy {#h3-3-type-hierarchy}

![](/images/posts/2021/10/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/typehierachy.gif)

Type hierarchy can now be easily accessed in Visual Studio Code! This is a feature that was introduced recently and has been one of the top asks in the Java community.

### Source Lookup {#h3-4-source-lookup}

Currently, developers can quickly locate the source code of a library. Now we have expanded this support to unmanaged projects. As long as the library comes from Maven Central repo, we can resolve its sources.

![](/images/posts/2021/10/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/sourcelookup.gif)

More advanced configurations are supported as well.

### More Code Actions {#h3-5-more-code-actions}

Usability is another area we continue to improve. We have adopted more code actions to provide an easier development experience.

![](/images/posts/2021/10/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/morecodeactions.gif)

### Gradle Kotlin (.kts) support {#h3-6-gradle-kotlin-kts-support}

We already support Gradle projects using Groovy scripts, but we heard the need for Kotlin support from the community and now provide basic support for this.

![](/images/posts/2021/10/announcing-the-1-0-release-of-language-support-for-java-on-visual-studio-code/gradlekotlin.png)

Our hope is that we can continue to expand upon this and deliver a great experience for Kotlin in the future.

Java Journey on Visual Studio Code {#h2-7-java-journey-on-visual-studio-code}
-----------------------------------------------------------------------------

The language support for Java™ by Red Hat extension is a fundamental piece of the Java expansion pack and offers essential components such as Java code editing, completion, refactoring and navigation. Over the past few years, Microsoft have been working together closely with Red Hat and adding new features to this language server. We have also been fine-tuning the performance of the language server and improving the stability by addressing issues and bugs.

In addition to the main Java language support, we have built various Java extensions based on the language server and expanded the Java tooling features on Visual Studio Code including project management, build tools support for Maven/Gradle, and better testing support.

Our goal behind all this work is to provide the best Java development experience possible on Visual Studio Code.

Going Forward {#h2-8-going-forward}
-----------------------------------

Going forward, we have planned many items for the Java language support on Visual Studio Code.

Some of these improvements are:

* Make it easier to configure compiler error/warnings and code formatting options
* More useful auto-completion suggestions (eg. Postfix completion)
* Keep adopting convenient code actions (eg. new Java language features) and eventually reach feature parity with those provided by the Eclipse Java IDE
* Further improving the overall performance of the language server and startup time
* Embedding a Java runtime in the extension to improve the overall "Getting Started" experience

We believe the 1.0 release of the language support for Java™ by Red Hat is an important step forward for the overall Java journey on Visual Studio Code and we still have a long way to go.

Going forward, we plan to collaborate with Red Hat and invest in fundamental Java language support and we are committed to delivering an outstanding Java development experience.

As always, your feedback is critical to our product improvement so please don't hesitate to give it a try. You can [follow this link](https://github.com/eclipse/eclipse.jdt.ls/issues?q=is%3Aissue+label%3Aperformance+updated%3A%3E2021-08-01 "follow this link") to get started on Java using Visual Studio Code.
