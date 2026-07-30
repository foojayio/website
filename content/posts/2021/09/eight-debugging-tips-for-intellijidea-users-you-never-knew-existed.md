---
title: "8 Debugging Tips for IntelliJ IDEA Users You Never Knew Existed"
slug: "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
date: "2021-09-09T07:15:54+00:00"
lastmod: "2021-09-09T07:29:59+00:00"
description: "8 proven debugger tips for developers. Improve debugging in IntelliJ IDEA. Learn advanced data collection, from breakpoints to new features!"
canonical: "https://lightrun.com/blog/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/"
authors:
  - "noga-badhav"
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2021/09/Debugging-tips_comp.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "beginning-javafx-with-intellij"
  - "creating-a-simple-spring-boot-application-in-intellij-idea"
  - "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
frozen: false
---

As developers, we're all familiar with debuggers. We use debugging tools on a daily basis -- they're an essential part of programming. But let's be honest. Usually, we only use the breakpoint option. If we're feeling frisky, we might use a conditional breakpoint.

But guess what, the [IntelliJ IDEA](https://www.jetbrains.com/idea/ "IntelliJ IDEA") debugger has many powerful and cutting-edge features that are useful for debugging more easily and efficiently. To help, we've compiled a list of tips and tricks from our very own developers here at [Lightrun](https://go.lightrun.com/request-a-demo?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "Lightrun"). We hope these tips will help you find and resolve bugs faster.

Let's get started.

### 1. Use an Exception Breakpoint {#h3-0-1-use-an-exception-breakpoint}

Breakpoints are places in the code that stop the program, to enable debugging. They allow inspecting the code behavior and its functions to try to identify the error. IntelliJ offers a wide variety of breakpoints, including [line breakpoints, method breakpoints and exception breakpoints](https://www.jetbrains.com/help/idea/using-breakpoints.html "line breakpoints, method breakpoints and exception breakpoints").

We recommend using the exception breakpoint. This breakpoint type suspends the program according to an exception type, and not at a pre-defined place. We especially recommend the IntelliJ Exception breakpoint because you can also filter the class or package the exceptions are a part of.

So you can define a breakpoint that will stop on a line that throws NullPointerException and ignore the exceptions that are thrown from files that belong to other libraries. All you have to do is define the package that has your project's files. This will help you focus the analysis of your code behavior.

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/Use-an-Exception-Breakpoint-700x439.png)

Lightrun offers snapshots -- breakpoints that do not stop the program from running. [Learn more here](https://lightrun.com/product/?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "Learn more here").

### 2. Use Conditions in Your Breakpoints {#h3-1-2-use-conditions-in-your-breakpoints}

This is one of the most under-utilised tools in debuggers and possibly one of the most effective ones. Use conditions to narrow down issues far more easily, to save time and the work of hunting for issues. For example, in a loop you can define a breakpoint that will only stop on the actual bug, relieving you from manually going over loops until you run into an issue!

In the loop below, you can see the breakpoint will stop the service when the agent id value is null. So instead of throwing a null pointer exception we'll be able to inspect the current state of the VM (virtual machine) before it does.

Notice that a condition can be very elaborate and even invoke methods as part of the condition.

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/Use-Conditions-in-Your-Breakpoints-700x373.png)

Lightrun offers conditions for all its actions: snapshots, logs etc. [Learn more here](https://lightrun.com/product/?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "Learn more here").

### 3. Enable the "Internal Actions" Menu for Custom Plugin Development {#h3-2-3-enable-the-internal-actions-menu-for-custom-plugin-development}

If you're writing a custom IntelliJ/IDEA plugin, enable Internal Actions (Tools -\> Internal Actions) for easy debugging. This feature includes a lot of convenient options, like a component inspector and a UI debugger. It's always handy to have a wide set of tools at your disposal, providing you with options you may have never thought of yourself.

[To enable Internal Actions](https://www.jetbrains.org/intellij/sdk/docs/reference_guide/internal_actions/enabling_internal.html "To enable Internal Actions") select Help -\> Edit Custom Properties. Then type in `idea.is.internal=true` and save. Upon restart you should see the new option under the Tools menu.

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/Enable-the-Internal-Actions-Menu-for-Custom-Plugin-Development--372x510.png)

### 4. Use the "Analyze Thread Dump" Feature {#h3-3-4-use-the-analyze-thread-dump-feature}

A thread dump is a snapshot that shows what each thread is doing at a specific time. Thread dumps are used to diagnose system and performance issues. Analyzing thread dumps will enable you to identify deadlocks or contention issues.

We recommend using IntelliJ's "Analyze Thread Dump" feature because of its convenient browsing capabilities that make the dump easy to analyze. "Analyze Thread Dump" automatically detects a stack trace in the clipboard and instantly places it with links to your source code. This capability is very useful when traversing stack dumps from server logs, because you can instantly jump to the relevant files like you can with a local stack trace.

To access the feature go to the Analyze menu. The IDE supports activating this feature dynamically when the IDE detects a stack trace in the clipboard.

### 5. Use the Stream Debugger {#h3-4-5-use-the-stream-debugger}

Java 8 streams are very cool to use but notoriously hard to debug. Streams condense multiple functions into a single statement, so simply stepping over the statements with a debugger is impractical. Instead, you need a tool that can help you analyze what's going on inside the stream.

IntelliJ has a brand new cool tool, the [stream debugger](https://dzone.com/articles/debugging-java-streams-with-intellij "stream debugger"). You can use it to inspect the results of the stream operation visually. When you hit a breakpoint on a stream, press the stream debugger icon in the debugger. You will see the UI mapping of the value of the stream elements at each stage/function of the stream. Thus, each step is visualized and you can see the operations in the stream and detect the problem.

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/stream-debugger-e1592293166245-700x141.png)

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/streamdebugger2-700x410.png)

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/stream-debugger-3-700x404.png)

### 6. Use Field Watchpoints {#h3-5-6-use-field-watchpoints}

The Field Watchpoint is a type of breakpoint that suspends the program when the defined field is accessed or modified. This can be very helpful when you investigate and find out that a field has a wrong value and you don't know why. Watching this field could help finding the fault origin.

To set this breakpoint, simply add it at the line of the desired field. The program will suspend when, for example, the field is modified:

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/field-watchpoints-700x220.png)

### 7. Debug Microservices with the Lightrun Plugin {#h3-6-7-debug-microservices-with-the-lightrun-plugin}

Lightrun's IntelliJ plugin enables adding logs, snapshots and performance metrics, even while the service is running. Meaning, you can add instrumentation to the production and staging environments. You can debug monolith microservices, Kubernetes, K8, Docker Swarm, ECS, Big Data workers, serverless, and more. Multi-instance support is available through a tagging mechanism.

The Lightrun plugin is useful for saving time, so instead of going through multiple iterations of local reproduction of environments, restarts and redeployments you can debug straight in production.

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/lightrun-plugin-700x464.png)

Want to learn more? [Request a demo.](https://go.lightrun.com/request-a-demo?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "Request a demo.")

### 8. Use a Friend -- Real or Imaginary {#h3-7-8-use-a-friend-real-or-imaginary}

When it comes to brainstorming, 1+1=3. And when it comes to dealing with complex debugging issues, you are going to need all the brainpower you can get. Working with someone provides a fresh set of eyes that views the problem in a different manner and might identify details you missed.

Or you both complement each other until you reach the solution. Just by asking each other questions and undermining some of each other's assumptions, you will reach new conclusions that will help you find the problem. You can also use each other for ["Rubber Duck Debugging"](http://lists.ethernal.org/oldarchives/cantlug-0211/msg00174.html "“Rubber Duck Debugging”"), or as we like to call it, "Cheetah debugging".

![](/images/posts/2021/09/eight-debugging-tips-for-intellijidea-users-you-never-knew-existed/rubber-duck-debugging-700x397.png)

We hope these tips by our own developers will help you with your debugging needs. Feel free to share your debugging tips and best practices with us and to share this blog post to help others.

You can find a list of online Java developer communities [here](https://go.lightrun.com/java-communities?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "here").

As we mentioned in tip no. 7, Lightrun's IntelliJ plugin enables developers to debug live microservices without interrupting them. You can securely add logs and performance metrics to production and staging in real-time, on-demand. [Request a demo](https://go.lightrun.com/request-a-demo?utm_source=blog&amp;utm_medium=LR_blog&amp;utm_campaign=eight-debugging-tips-for-intellijidea "Request a demo") and find out more.
