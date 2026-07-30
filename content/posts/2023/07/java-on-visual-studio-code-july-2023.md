---
title: "Java on Visual Studio Code – July 2023"
slug: "java-on-visual-studio-code-july-2023"
date: "2023-07-31T07:11:13+00:00"
lastmod: "2023-08-01T07:11:40+00:00"
description: "Learn about the improved decompiler functionality. Additionally, we are going to do a deep-dive into our code completion."
authors:
  - "nick-zhu"
image: "https://foojay.io/wp-content/uploads/2023/07/decompile_debugging.gif"
categories:
  - "Developer Tools"
  - "VS Code"
tags:
related_posts:
  - "foojay-podcast-12"
  - "java-on-visual-studio-code-june-2023"
  - "java-on-visual-studio-code-may-2023"
frozen: false
---

Hi everyone, welcome to our July update for Visual Studio Code for Java!

In this article, we are going to provide you an exciting update about our improved decompiler functionality.

Additionally, we are going to do a deep-dive into our code completion.

Let's get started!

### Decompiler Support Upgrade {#h3-0-decompiler-support-upgrade}

Java decompiler is essential for understanding third-party libraries, debugging, and learning from well-written code. It helps the developer to reverse engineer compiled Java bytecode back into human-readable Java source code, enhancing the productivity and code comprehension.

Previously, users have reported that our extensions did not have good support of proper decompiling and debugging code. In our latest release, we have embedded a powerful Java decompiler called Fernflower (Currently an open-source project and used in IntelliJ IDEA) in [our extension pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

With this decompiler, our extension allows you to directly click into libraries and it will automatically decompile the bytecode into readable source code. Inside the decompiled code, you can debug like what you do normally. We hope this feature will greatly boost your productivity during development. Here's a simple demo.

![Decompiler experience upgrade](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/07/decompile_debugging.gif)

Decompiler experience upgrade, image

Decompiler experience upgrade

Toggling Inlay Hint on the Fly {#h2-1-toggling-inlay-hint-on-the-fly}
---------------------------------------------------------------------

Inlay Hint is a popular feature in Visual Studio Code and many other developer tools. It adds inline information to the source code to help you understand what the code does, such as parameter names, parameter types, variable types and so on.

However, we have heard from Java developers that sometimes these inline markers have obscured the original code and the editor becomes too crowded.

Actually, Visual Studio Code has a setting that allows you to toggle inlay hints on the fly so you can turn it on/off as you are typing the code, easily tuning your experience. The setting can be found by searching for "inlay hints" and selecting the options from the image below.

[![Toggling Inlay Hints on the fly](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/07/inlayhints.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/07/inlayhints.png)

Toggling Inlay Hints on the fly

Here's a simple demo.

![Toggle inlay hints on the fly demo](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2023/07/inlayhints.gif)

Toggle inlay hints on the fly demo, image

Toggle inlay hints on the fly demo

Code Completion Performance Improvement Deep-Dive {#h2-2-code-completion-performance-improvement-deep-dive}
-----------------------------------------------------------------------------------------------------------

We have consistently stressed the importance of code completion performance and its critical role of boosting the developer's productivity.

In [our previous blog post](https://devblogs.microsoft.com/java/java-on-visual-studio-code-june-2023/), we have shown the latency improvement of our recent code performance. In this month, we are pleased to share even more progress and technical details of how we have managed to make this improvement.

#### Reduction in code completion time (compared to previous release)

| Extension Version | Average |  P99   |  P95   |  P90   |  P75   |  P50   |
|-------------------|---------|--------|--------|--------|--------|--------|
| 1.18              | 13.85%  | 56.15% | 49.36% | 50.22% | 13.69% | 34.38% |
| 1.19              | 55.65%  | 17.19% | 17.01% | 15.18% | 14.14% | 9.52%  |

As the table shows, after 1.19 release, the code completion latency has been reduced significantly in average time and in all percentile ranks.

The code completion optimization efforts are tightly linked to the Eclipse Java Development (JDT) Language Server and our work is focused on three main areas.

#### 1. Optimize Diagnostic Jobs

Related Pull Requests: [PR #2587](https://github.com/eclipse/eclipse.jdt.ls/pull/2587), [PR #2574](https://github.com/eclipse/eclipse.jdt.ls/pull/2574), [PR #2664](https://github.com/eclipse/eclipse.jdt.ls/pull/2664)

The diagnostic jobs in the Eclipse JDT Language Server are responsible for analyzing Java source code and providing feedback on potential issues such as compiler errors and warnings. These PRs introduce optimizations to the diagnostic job processing, resulting in improved performance and reduced latency in providing code diagnostics. By fine-tuning the diagnostic analysis, developers can receive faster feedback and gain insights into their codebase more promptly.

#### 2. Optimize Request Handler Scheduling Rules

Related Pull Requests: [PR #2637](https://github.com/eclipse/eclipse.jdt.ls/pull/2637), [PR #2641](https://github.com/eclipse/eclipse.jdt.ls/pull/2641), [PR #2643](https://github.com/eclipse/eclipse.jdt.ls/pull/2643), [PR #2659](https://github.com/eclipse/eclipse.jdt.ls/pull/2659), [PR #2660](https://github.com/eclipse/eclipse.jdt.ls/pull/2660)

Effective scheduling of request handlers is vital in handling incoming code completion requests from the IDE. The mentioned PRs focus on optimizing the scheduling rules used by the Eclipse JDT Language Server to prioritize and process code completion requests efficiently. These improvements enable the server to allocate resources more effectively, resulting in reduced contention and faster response times. By streamlining request handler scheduling, developers can experience a more responsive code completion workflow.

#### 3. Optimize Completion Request Handler

Related Pull Requests: [PR #2642](https://github.com/eclipse/eclipse.jdt.ls/pull/2642), [PR #2639](https://github.com/eclipse/eclipse.jdt.ls/pull/2639), [PR #2621](https://github.com/eclipse/eclipse.jdt.ls/pull/2621), [PR #2614](https://github.com/eclipse/eclipse.jdt.ls/pull/2614), [PR #2638](https://github.com/eclipse/eclipse.jdt.ls/pull/2638)

The completion request handler is responsible for generating code completion suggestions based on the context within the IDE. These PRs introduce optimizations to the completion request handling process, resulting in faster and more accurate suggestion generation. The work was mainly focused in two areas

* Return suggestion list first and lazily calculate the inserted text
* Reduce the payload size for completion response and reduce the serialization/deserialization and cost

By enhancing the completion request handler, developers can experience a significant improvement in the speed and relevance of code completion suggestions, enabling them to write code more efficiently.

We want to thank everyone in the open-source community who is involved in these PRs. The progress we have achieved in enhancing code completion serves as a promising milestone of our ongoing commitment to improving Visual Studio Code Java's performance.

In addition to code completion, we will continue to refine and optimize the reliability and compatibility of our Java language server (such as less crash in our extension and errors when building the projects). Please stay tuned with our upcoming blog posts.

#### Install Extension Pack for Java

To use all features mentioned above, please download and install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) on Visual Studio Code.

[![Extension pack for Java](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)

If you are a Spring developer working on a Spring Boot application, you can also download the [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) for specialized Spring experience.

[![Spring boot extension pack](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)

Feedback and suggestions {#h2-3-feedback-and-suggestions}
---------------------------------------------------------

As always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

Resources {#h2-4-resources}
---------------------------

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
