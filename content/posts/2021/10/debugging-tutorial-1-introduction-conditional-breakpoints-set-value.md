---
title: "Debugging Tutorial 1 – Introduction: Conditional Breakpoints & Set Value"
slug: "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
date: "2021-10-27T08:03:53+00:00"
lastmod: "2021-10-27T08:05:13+00:00"
description: "Debug conditional breakpoints, setValue, and more, for VSCode, IntelliJ/IDEA, PyCharm, WebStorm, Java, JavaScript (NodeJS), Kotlin & Python."
canonical: "https://talktotheduck.dev/debugging-tutorial-1-introduction-conditional-breakpoints"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2021/10/Lightrun-Talk-to-the-duck-Debugging-Tutorial-Opt3-scaled.jpg"
categories:
  - "IntelliJ IDEA"
  - "Kotlin"
  - "Tutorials"
  - "VS Code"
tags:
related_posts:
  - "the-debugger-checklist-part-i"
  - "the-debugger-checklist-part-ii"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "debug-without-breakpoints"
frozen: false
---

In this series, I'll walk you through the process of debugging applications and finding issues within them. As we debug, we'll cover the techniques important for most developers. I will cover the following debuggers:

* IntelliJ/IDEA -- with Java/Kotlin
* PyCharm -- Python
* VSCode -- for JavaScript
* WebStorm -- for JavaScript

These should cover most use cases you'll run into and some things will seem duplicate/redundant so you can just skip to the applicable section if you feel you "got the gist".

Notice that while I won't cover TypeScript, everything discussed in the JavaScript tutorial is applicable to TypeScript too. The same applies to most JVM languages like Scala etc.

Normally people separate these tutorials based on IDE/Language. I think having all of them together is beneficial in providing the "big picture". We also move through languages/IDEs in our career and having a wider viewpoint is helpful.

In a future post I'd also like to also talk about browser debugging with Chrome DevTools and Firefox Web Developer Tools. However, these are a slightly different process so I'll leave them out for now.

I also included a video tutorial highlighting the steps visually. This might help if my instructions are unclear:

Motivation {#h2-0-motivation}
-----------------------------

The main goal in this series is to bring you up to speed so we can dig into the real interesting tutorials near the end. The first part might seem a bit trivial since I'm assuming zero knowledge of debugging techniques but I plan to get in-depth with these posts as we move forward.

But you might be asking yourself, why even go through this?

Why do we need to go through a debugging tutorial? I already know programming and using a debugger isn't exactly rocket science...

Well... we spend 50% of our time chasing bugs according to this [study](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.370.9611&amp;rep=rep1&amp;type=pdf), although I saw numbers ranging from 25% to 75%. Unfortunately, this is a skill that universities don't really teach. Even at our jobs, people gloss over this essential skill. There are books that cover this subject but a lot are out of date and aim at more complex scenarios.

Debugging isn't just about finding bugs. When I need to implement a new feature in an area of the code I'm unfamiliar with, I often pull up the debugger and start debugging those areas. Trying to look at the flow as if I'm debugging an issue, inspecting the stack, members etc., to gain a better understanding of the way the system works and validate my assumptions.

Getting Started {#h2-1-getting-started}
---------------------------------------

To get started, we need a simple app we can debug in the initial parts of this tutorial. For this, we'll use Prime Main which calculates prime numbers. It's a trivial app which you can just fetch and open in your IDE.

Notice that the apps are specifically unoptimized and verbose to give us more places to place breakpoints and step over.

Here are versions of it for the various languages:

* **Java** -- <https://gist.github.com/shai-almog/e400134f01decc9639230a6a99d51eab>
* **Kotlin** -- <https://gist.github.com/shai-almog/c454d39464ca2893c014807838c5102f>
* **Python** -- <https://gist.github.com/shai-almog/8c8bbbb4297f758f7ce1d5f7a4cc1c74>
* **JavaScript** -- <https://gist.github.com/shai-almog/167a34571b0fae6eeed56742c44895cd>

Download the appropriate source file and add it as the main source file in a new project in your IDE. You can run the application but take in mind that it will run for a VERY long time searching for prime numbers.

Simple Conditional Breakpoint {#h2-2-simple-conditional-breakpoint}
-------------------------------------------------------------------

First we need to debug the app. In IDEs from JetBrains, we just right click the executable file and select debug as such:

![Screen Shot 2021-10-19 at 15.16.43.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1634756719807/8jHNnemXB.png)

Please notice you might need to wait for the IDE to finish scanning and indexing the files so it will show the appropriate debug action.

The same is true for VSCode. Select the debug view on the left of the IDE and click the "Run and Debug" button.

![Screen Shot 2021-10-19 at 16.27.39.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1634756764983/VKGY3QpPI.png)

You will be prompted with an environment to use, pick Node.js to continue.

Once you start debugging you can set a breakpoint by clicking once on the "gutter" area to the left of the file. Let's do a quick experiment for every file type/IDE.

Running {#h2-3-running}
-----------------------

![Running on JetBrains IntelliJ/IDEA](https://cdn.hashnode.com/res/hashnode/image/upload/v1634756909711/3Ynut12B6.png)  
**Running on JetBrains IntelliJ/IDEA**

![Running on VSCode](https://cdn.hashnode.com/res/hashnode/image/upload/v1634756974344/r-c89Vbb6.png)  
**Running on VSCode**

We place a breakpoint on line in the file. Notice that the Java file is more elaborate than the other Prime Main files:

* For Java place the breakpoint at line 28
* For Kotlin line 21
* For JavaScript line 11
* For Python line 11

Once the breakpoint is hit you should be able to see the stack trace on the bottom left. The stack trace represents the methods that invoked the current method. At the top of the stack you see the current method. You can click on each "stack frame" to go to see the caller and the state of the variables within the caller frame.

On the bottom right (for JetBrains) or the top left for VSCode, you can see the variables in the current stack frame. Notice n which is the number we're calculating. Notice it isn't a primitive but rather a BigInteger which we use to support fantastically large numbers in Java (numbers potentially larger than 9,223,372,036,854,775,807 which is the limit of Long).

We can change the values of variables using the "Set Value" feature:

![Setting Value in JetBrains](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757073584/Thl0eRQkk.png)  
**Setting Value in JetBrains**

![Setting Value in VSCode](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757271261/Z4hmrkhLZ.png)  
**Setting Value in VSCode**

This is extremely helpful for debugging edge cases. Just set the variable to the value you're having trouble with and reproduce the issue.

A nice JetBrains feature lets you view a value differently e.g. as hex, binary etc.:

![Screen Shot 2021-10-19 at 16.18.44.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757331027/tXxN9mt5p.png)

Finally we can right click on the breakpoint to edit it. We can set a condition for the breakpoint which will make execution stop only if the condition is met:

![Conditional Breakpoint in JetBrains on IntelliJ/IDEA](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757365298/aZkAgUrSe.png)  
**Conditional Breakpoint in JetBrains on IntelliJ/IDEA**

![Conditional Breakpoint is added via Edit Breakpoint in VSCode](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757413237/ylJ4nUfvk.png)  
**Conditional Breakpoint is added via Edit Breakpoint in VSCode**

![Conditional Breakpoint Editing in VSCode](https://cdn.hashnode.com/res/hashnode/image/upload/v1634757443176/ZgYwCDMR9.png)  
**Conditional Breakpoint Editing in VSCode**

Since the Java code is a bit different I used `primesToTest[0] == 2` which will always be true. You can try setting it to 3 which will take a long while and you can see that it will only stop at that point.

In the other cases I used `num % 2 == 0` as a condition that will always be false. Since the line above that line checks if a number is even which it never will be. Flipping this condition to `num % 2 == 1` will always be true and the breakpoint will be hit.

Taking this Further {#h2-4-taking-this-further}
-----------------------------------------------

Conditional breakpoints are one of the most powerful yet sadly under utilized tools in the arsenal of a debugger. When you experience a failure in a loop we often walk over the loop again and again and again... Waiting for the right conditions to materialize.

By using set value or a condition we can move much faster in our debugging process and possibly reproduce issues more accurately without the delays you'd normally see for stepping over.

TL;DR {#h2-5-tl-dr}
-------------------

In this first installment of this series, I have tried to stress the importance of debugging skills for our daily work. Great developers use their tools effectively and efficiently to increase productivity and the debugger is a complex machine that we need to wield effectively. According to some statistics, debugging might be as important as our coding skills.

Unfortunately, even basic features like conditional breakpoints, set value etc., are rarely used by most developers. I hope this and future articles in this series will help change that for you and help you find bugs quickly!
