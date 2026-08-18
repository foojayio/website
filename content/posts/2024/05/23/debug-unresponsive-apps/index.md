---
title: "Learn how to debug unresponsive Java/JVM applications, then reload the fix on the fly, using a hands-on example"
slug: "debug-unresponsive-apps"
date: "2024-05-23T07:14:31+00:00"
lastmod: "2024-08-01T07:36:11+00:00"
description: "Learn how to debug unresponsive Java/JVM applications, then reload the fix on the fly, using a hands-on example."
canonical: "https://flounder.dev/posts/debug-unresponsive-apps/"
authors:
  - "igor-kulakov"
image: "debug-unresponsive-apps-banner.png"
categories:
  - "IntelliJ IDEA"
  - "Java"
  - "Kotlin"
  - "Tutorials"
tags:
related_posts:
  - "debug-without-breakpoints"
  - "duplicate-finder-for-documentation"
  - "localize-apps-with-ai"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

Read in other languages: [中文](https://flounder.dev/zh/posts/debug-unresponsive-apps/) [Español](https://flounder.dev/es/posts/debug-unresponsive-apps/) [Português](https://flounder.dev/pt/posts/debug-unresponsive-apps/)

**There are a lot of debugger tutorials out there that teach you how to set line breakpoints, log values, or evaluate expressions. While this knowledge alone gives you a lot of tools for debugging your application, real-world scenarios may be somewhat trickier and require a more advanced approach.**
![Debug Unresponsive Apps – post banner](https://flounder.dev/img/debug-unresponsive-apps-banner.png)

In this article, we will learn how to locate code that causes a UI freeze without much prior knowledge of the project and fix faulty code on the fly.

The problem
-----------

If you want to follow along, start by cloning this repository: <https://github.com/flounder4130/debugger-example>

Suppose you have a complex application that hangs when you perform some action. You know how to reproduce the bug, but the difficulty is that you don't know which part of the code is in charge of this functionality.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/sophisticated-app-1.png" alt="The UI of the sample application has lots of buttons to perform some actions" style="width:400px">
</figure>

In our example app, the hanging happens when you click **Button N**. However, it is not so easy to find the code that is responsible for this action:

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/find-in-files-dark.png" alt="" style="width:649px">
</figure>

Let's see how we can use the debugger to find it.

Method breakpoints
------------------

The advantage of method breakpoints over line breakpoints is that they can be used on entire hierarchies of classes. How is this useful in our case?

If you look at the example project, you'll see that all action classes are derived from the  
`Action` interface with a single method: `perform()`.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/method-breakpoint-in-interface-dark.png" alt="Method breakpoint icon in the editor gutter" style="width:466px">
</figure>

Setting a method breakpoint in this interface method will suspend the application whenever one of the derived methods is called. To set a method breakpoint, click the line that declares the method.

Start the debugger session and click **Button N** . The application gets suspended in `ActionImpl14`. Now we know where the code corresponding to this button is located.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/suspended-in-implementing-class-dark.png" alt="The application got suspended in a class that implements the Action interface" style="width:532px">
</figure>

Though in this article we are focused on finding the bug, this technique can also save you a lot of time when you want to understand how something works in a large codebase.

Pause application
-----------------

The approach with method breakpoints works well, but it is based on the assumption that we know something about the interface that the class implements. What if this assumption is wrong, or we cannot use this approach for some other reason?

Well, we can even do it without breakpoints. Click **Button N** , and while the application is hanging, go to IntelliJ IDEA. From the main menu, select **Run** \| **Debugging Actions** \| **Pause Program**.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/examine-threads-in-paused-app-dark.png" alt="Call stack for the main thread shows what it is currently doing" style="width:630px">
</figure>

The application will be suspended, letting us examine the current state of the threads in the **Threads \& Variables** tab. This gives us an understanding of what the application is doing at the moment. Since it is hanging, we can identify the hanging method and trace back to the call site.

This approach has some advantages over a more traditional thread dump, which we'll cover shortly. For example, it gives you information about variables in a convenient form and allows you to control the further execution of the program.

For more tips and tricks with **Pause** see [Debug Without Breakpoints](https://flounder.dev/posts/debug-without-breakpoints/) and [Debugger.godMode()](https://flounder.dev/posts/debugger-god-mode/).

Thread dumps
------------

Finally, we can use a thread dump, which is not strictly a debugger feature. It is available regardless of whether you are using the debugger.

Click **Button N** . While the application is hanging, go to IntelliJ IDEA. From the main menu, select **Run** \| **Debugging Actions** \| **Get Thread Dump** . Scan through the available threads on the left, and in **AWT-EventQueue** you'll see what is causing the problem.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/ij-thread-dump-dark.png" alt="Thread dump viewer in IntelliJ IDEA" style="width:925px">
</figure>

The downside of thread dumps is that they only provide a snapshot of the program state at the time when they were made. You can't use thread dumps to explore variables or control the program's execution.

In our example, we don't need to resort to a thread dump. However, I still wanted to mention this technique as it may be useful in other cases, like when you are trying to debug an application that has been launched without the debug agent.

Understanding the issue
-----------------------

Regardless of the debugging technique, we arrive at `ActionImpl14`. In this class, someone intended to perform the work in a separate thread, but confused `Thread.start()` with `Thread.run()`, which runs the code in the same thread as the calling code.

IntelliJ IDEA's static analyzer even warns us about this at design time:

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/thread-run-warning-dark.png" alt="IntelliJ IDEA's static analysis gives a warning about suspicious call to Thread.run()" style="width:589px">
</figure>

A method that does heavy lifting (or heavy sleeping in this case) is called on the UI thread and blocks it until the method finishes. That's why we cannot do anything in the UI for some time after we click **Button N**.

HotSwap
-------

Now that we've discovered the cause of the bug, let's fix the issue.

We could stop the program, recompile the code, and then rerun it. However, it is not always convenient to redeploy the entire application just because of a small change.

Let's do it the smart way. First, correct the code using the suggested quick-fix:

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/quick-fix-replace-thread-run-dark.png" alt="Context menu (Alt-Enter) gives an option to fix the suspicious code" style="width:716px">
</figure>

After the code is good to go, click **Run** \| **Debugging Actions** \| **Reload Changed Classes**. A balloon appears, confirming that the new code has made its way to the VM.

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="https://flounder.dev/img/hotswap-balloon-dark.png" alt="A balloon confirms that updated classes have made their way to the runtime" style="width:454px">
</figure>

Let's go back to the application and check. Clicking **Button N** no longer hangs the app.

**Note** : Keep in mind that HotSwap has its [limitations](https://www.jetbrains.com/help/idea/altering-the-program-s-execution-flow.html#hotswap-limitations). If you are interested in extended HotSwap capabilities, it might be a good idea to take a look at advanced tools like [DCEVM](http://dcevm.github.io) or [JRebel](https://www.jrebel.com).

Summary
-------

Using our reasoning and a couple of debugger features, we were able to locate the code that was causing a UI freeze in our project. Then, we proceeded to fix the code without wasting any time on recompilation and redeployment, which can be lengthy in real-world projects.

I hope you find the described techniques helpful. To get updated about new tips and tricks, subscribe to me on [X](https://flounder.dev/), or use the mailing list [on my blog](https://flounder.dev).

Stay tuned for more!

<br />

<br />
