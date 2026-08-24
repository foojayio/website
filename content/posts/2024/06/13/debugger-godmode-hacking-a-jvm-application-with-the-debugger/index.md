---
title: "Hack a Java application with the debugger"
date: "2024-06-13T04:58:03+00:00"
lastmod: "2024-11-12T16:19:59+00:00"
description: "Use the debugger to find out how a Java application works under the hood, access its memory, and modify it without a single source file"
canonical: "https://flounder.dev/posts/debugger-god-mode/"
authors:
  - "igor-kulakov"
image: "banner.png"
categories:
  - "Debugging"
  - "IntelliJ IDEA"
  - "Java"
  - "Kotlin"
  - "Tools"
  - "Tutorials"
  - "Use Cases"
related_posts:
  - "a-short-primer-on-java-debugging-internals"
  - "boldness-in-refactoring"
  - "debug-like-a-senior-developer"
frozen: false
---

Read in other languages: [中文](https://flounder.dev/zh/posts/debugger-god-mode/) [Español](https://flounder.dev/es/posts/debugger-god-mode/) [Português](https://flounder.dev/pt/posts/debugger-god-mode/)

**Back in the day, computer games were different. Not only have graphics and mechanics evolved since, but there's also one characteristic that doesn't seem very common in games today: nearly all of them had cheat codes.**

Cheat codes were sequences of keys that would give you something extraordinary, such as infinite ammo or the ability to walk through walls. The most common and powerful of them was 'god mode' – it made you invincible.

![Screenshot of the marine from Doom with 'god mode' enabled](https://flounder.dev/img/debugger-god-mode/iddqd.png)

This is what your character would look like when you entered 'IDDQD' – the key combination for 'god mode' in [Doom](https://en.wikipedia.org/wiki/Doom_(1993_video_game)). In fact, this particular key sequence was so popular that it became a [meme](https://www.urbandictionary.com/define.php?term=iddqd) and gained popularity beyond the game itself.

While 'god mode' is not as prevalent in games as it once was, and the era of the IDDQD meme seems to be fading, one might wonder if a contemporary equivalent exists. Personally, I have my own modern take on IDDQD. Though it's not necessarily related to games, it does evoke the same sense of having superpowers.

## Space Invaders

To illustrate my point, I'd like to bring in a fun scenario right here. Even if you aren't familiar with Doom, you've most probably seen this even older game called [Space Invaders](https://en.wikipedia.org/wiki/Space_Invaders). Like Doom, its plot centers around the theme of fighting invaders in space.

My friend and colleague, [Eugene](https://www.linkedin.com/in/eugene-nizienko-016a153a/), has written an [IntelliJ IDEA plugin](https://plugins.jetbrains.com/plugin/19383-space-invaders), which lets you play this game right in the editor – a great way to spend some time while waiting for indexing to complete.

![Space Invaders in IntelliJ IDEA's editor](https://flounder.dev/img/debugger-god-mode/space-invaders-dark.png)

There's no god mode in this game, but if we are very determined, can we add it ourselves? Let's bring back the classic tradition of hacking programs with a debugger and find out!

**Note** :  

Be responsible! I got Eugene's consent before tampering with his program. If you're using the debugger on code that is not yours, make sure it's ethical. Otherwise don't do that.

## Get the tools ready

Prepare for a meta experience – we're going to debug IntelliJ IDEA using its own debugger.

Here's a little problem that we need to solve: for debugging IntelliJ IDEA, we need to suspend it. This will consequently render the IDE unresponsive. Therefore, we need an extra IDE instance that will remain functional and serve as our debugging tool.

To manage several IDE instances, I will be using [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/). This is a utility that organizes installed JetBrains' IDEs. With it, you can install several versions or create shortcuts for running them with different sets of VM options.

Let's install two instances of IntelliJ IDEA:

![JetBrains Toolbox shows several JetBrains IDEs including two instances of IntelliJ IDEA called Space Invaders and Debug](https://flounder.dev/img/debugger-god-mode/jb-toolbox-dark.png)

If you are using the same IDE version for both instances, make sure to specify a different system, config, and logs directories in **Tool actions** \| **Settings** \| **Configuration**. On this page, you can also assign names to the IDE instances for convenience.

To be able to debug the 'Space Invaders' instance, click **More** near it, then go to **Settings** \| **Edit JVM options**. In the file that opens, paste the following line:

    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

![The file with VM options that are going to be passed to the IDE instance](https://flounder.dev/img/debugger-god-mode/toolbox-add-vm-options-dark.png)

This will make the target JVM run with the debug agent and listen to incoming debugger connections on port `5005`.

## Run the game

Run the `Space Invaders` instance, [install the game](https://www.jetbrains.com/help/idea/managing-plugins.html), and launch it by running the **Space Invaders** action. To find the action, hit Shift twice, and start typing `Space Invaders`:

![Running the Space Invaders action through the dialog that opens on hitting double Shift](https://flounder.dev/img/debugger-god-mode/space-invaders-action-dark.png)

Let's play for a while and observe the behavior that we want to fix: when enemy missiles hit the spaceship, the health bar in the top-left corner of the screen goes down.

### Attach and suspend

Our debugging journey begins with opening the 'Debug' IDE instance and setting up a new Kotlin project. The primary reason why we need this project is that it would not be possible to launch the debugger without one.

Additionally, IntelliJ IDEA includes the Java/Kotlin standard library in the new project template, which we might use later. I'll explain the use of the standard library in the subsequent chapters.

After creating the project, go to the main menu and select **Run** \| **Attach to Process**. This will show the list of local JVMs listening for debugger attach requests. Select the other running IDE from the list.

![A popup with the list of locally running JVMs](https://flounder.dev/img/debugger-god-mode/attach-dark.png)

We should see the following message in the console confirming that the debugger has successfully attached to the target VM.

    Connected to the target VM, address: 'localhost:5005', transport: 'socket'

We're getting to the interesting part: how do we suspend the application?

Typically, one would set a breakpoint in the application code, but in this case, we lack the sources for both IntelliJ IDEA and the Space Invaders plugin. Not only does this prevent us from setting a breakpoint, it also complicates our understanding of how the program operates. At first glance, there appears to be nothing to inspect or step through.

Luckily for us, IntelliJ IDEA has a feature known as [Pause](https://flounder.dev/posts/debug-without-breakpoints/). It allows you to suspend the program at any arbitrary point in time, without needing to specify the corresponding line of code. You can find it in the debugger toolbar or in the main menu: **Run** \| **Debugging Actions** \| **Pause**.

![The Debug tool window for the suspended Space Invaders instance](https://flounder.dev/img/debugger-god-mode/invaders-paused-dark.png)

The application gets suspended. This gives us a starting point for debugging.

**Pause** is a very powerful technique, which is especially helpful in several advanced scenarios. To learn more, check out the related articles:

* [Debug Without Breakpoints](https://flounder.dev/posts/debug-without-breakpoints/)
* [Debug Unresponsive Apps](https://flounder.dev/posts/debug-unresponsive-apps/)

## Find the relevant objects

If we look at our goal in programming terms, it boils down to preventing the spaceship's health from going down. Let's find the object that holds the corresponding state.

Since we don't know anything about the plugin code, we can directly inspect the heap using the IntelliJ IDEA debugger's **Memory** view:

![A menu appears on clicking Layout Settings in the top-right corner of the Debug tool window](https://flounder.dev/img/debugger-god-mode/open-memory-view-dark.png)

This feature gives you information about all objects that are currently alive. Let's type `invaders` and see if we can find anything:

![Typing 'invaders' in the Memory view's search field shows objects of classes that belong to the 'spaceinvaders' package](https://flounder.dev/img/debugger-god-mode/search-in-heap-dark.png)

Apparently, the plugin classes come under the package `com.github.nizienko.spaceinvaders`. Within this package, there is `GameState` class with several live instances. It looks like what we need.

Double-clicking `GameState` shows all the instances of this class:

![A dialog opens showing live GameState instances](https://flounder.dev/img/debugger-god-mode/instances-of-enum-dark.png)

As it turns out, it's an enum – which isn't exactly what we were looking for. Continuing our search, we stumble upon a single instance of `Game`.

Expanding the node lets us inspect the instance's fields:

![Memory view with an expanded object node showing the object's fields](https://flounder.dev/img/debugger-god-mode/fields-in-memory-view-dark.png)

The `health` property appears to be the one of interest here. Among its fields, we can find `_value`. In my case, it was `100`, which correlates with the health bar being full when I suspended the game. So, it's likely the correct field to consider, and its value seems to range from `0` to `100`.

Let's put this hypothesis to the test. Right-click `_value`, then select **Set Value** . Choose a value that is different from your current one. For instance, I chose `50`.

![Memory view with a text field against the 'health' field containing the user-entered value of 50](https://flounder.dev/img/debugger-god-mode/memory-view-set-value-dark.png)

At this step, we bump into an error that reads **Cannot evaluate methods after Pause action**:

![An error message saying 'Cannot evaluate methods after Pause'](https://flounder.dev/img/debugger-god-mode/cannot-evaluate-after-pause-dark.png)

The problem arises because we used **Pause** instead of breakpoints, and this feature comes with [some limitations](https://flounder.dev/posts/debug-without-breakpoints/#limitations). However, we can turn to a little trick to work around this.

I described it in [one of the previous posts](https://flounder.dev/posts/debug-without-breakpoints/#secret-stepping-trick) dedicated to **Pause** . In case you missed it there, here's what needs to be done: once the application has paused, perform a stepping action, such as **Step Into** or **Step Over** . Doing so will enable the use of advanced features like **Set Value** and **Evaluate Expression**.

After applying the trick described above, we should be able to set the value for `health`. Try modifying the value, then resume the application to see if the health bar displays any changes. Indeed, it does!

So we've located the object that holds the relevant state. At the very least, we can manually refill the health bar from time to time. Although it isn't a complete success just yet, it's already a significant step forward.

## Labels and expressions

Now that we have identified the object to focus on, it would be handy to [mark](https://www.jetbrains.com/help/idea/examining-suspended-program.html#use-labels) it. For those unfamiliar with debug labels, this is what a marked object looks like:

![Variables tab showing an array of User objects, with one of them marked with a debug label saying User_Charlie](https://flounder.dev/img/debugger-god-mode/labeled-object-dark.png)

Labels can be beneficial in many ways. Within the context of this article, marking the relevant object ensures that we can directly use it in features like **Evaluate Expression** without being dependent on the current execution context.

Unfortunately, it's not possible to direcly mark `_value`, but we can mark the object that encloses it. To do this, right-click `health`, select **Mark Object**, then give it a name.

![Select Object Label dialog prompting the user to enter a name for the object](https://flounder.dev/img/debugger-god-mode/mark-health-object-dark.png)

We can now test how the label works elsewhere. Open the [Evaluate Expression](https://www.jetbrains.com/help/idea/examining-suspended-program.html#evaluate-arbitrary-expression) dialog, a [great tool for prototyping fixes and altering the program flow](https://flounder.dev/posts/efficient-debugging-exceptions/#prototype-the-fix), and enter `health_object_DebugLabel` as the expression. As you can see, the object is accessible from any place in the program through the **Evaluate** dialog:

![Evaluate dialog with debug label entered as the expression](https://flounder.dev/img/debugger-god-mode/evaluate-label-dark.png)

What about changing the spaceship's health from **Evaluate** ?  
`health_object_DebugLabel._value = 100` does not work.

At the same time, `_value` appears to be a backing field of a Kotlin property. If this is true, Kotlin must have generated a corresponding getter: `health_object_DebugLabel.getValue()`

The **Evaluate** dialog doesn't think this is valid code, but we'll try anyway:

![Referencing a property through a debug label in the Evaluate dialog](https://flounder.dev/img/debugger-god-mode/evaluate-property-dark.png)

The expression returns the current spaceship's health, so this approach works!  

Reasonably, the setter should work too: `health_object_DebugLabel.setValue(100)`

After evaluating the setter, let's resume the application and verify that the changes took effect. Indeed, the health bar is full!

## Hook the expression

The only remaining step for reaching our goal is to automate the modification of the state, so that the 'health refill' happens behind the scenes, letting us enjoy the gameplay without interruptions.

This can be done using [non-suspending breakpoints](https://blog.jetbrains.com/idea/2022/10/cultivating-good-printf-debugging-habits/#logging-breakpoints). This type of breakpoints is commonly used for logging; however, the logging expression does not necessarily need to be pure. Therefore, we can introduce the desired side effect within the logging expression. Still, it remains uncertain where to set this breakpoint, since we don't have the source code of the application.

[Remember I said](#attach-suspend) that we might use the sources of the Java standard library? Here is the idea. IntelliJ IDEA and its plugins are written in Java/Kotlin, and they use Swing as the UI framework. Consequently, Space Invaders is sure to call code from these dependencies. This means that we can use their sources to set breakpoints.

To maintain simplicity, we didn't specify a particular JDK version and initialized the project with the one suggested by IntelliJ IDEA. However, for best results, it's recommended to use sources that closely match the version used in the runtime.

There are numerous locations suitable for setting a breakpoint. I decided to set a method breakpoint in `java.awt.event.KeyListener::keyPressed`. This will trigger the side effect every time we press a key:

![Breakpoints dialog showing a logging breakpoint for java.awt.event.KeyListener::keyPressed](https://flounder.dev/img/debugger-god-mode/key-pressed-breakpoint-dark.png)

Setting a method breakpoint in hot code [might significantly slow down](https://flounder.dev/posts/troubleshoot-slow-debugging/#conditional-breakpoints-in-hot-code) the target application.

Let's return to Space Invaders and see if our home-cooked IDDQD works. It does!

![Playing Space Invaders in IntelliJ IDEA – every time that the spaceship gets hit, its health bar automatically refills](https://flounder.dev/img/debugger-god-mode/success.gif)

## Conclusion

In this article, we used the debugger to find out how an application works under the hood. After sorting this out, we were able to navigate in its memory and modify its functionality, all without accessing the application's sources! I hope my comparison of the debugger with IDDQD didn't come across as too audacious, and that you learned some techniques that will empower you in your debugging challenges.

I'd like to extend my kudos to [Eugene Nizienko](https://www.linkedin.com/in/eugene-nizienko-016a153a/) for making the [Space Invaders plugin](https://plugins.jetbrains.com/plugin/19383-space-invaders) and [Egor Ushakov](https://twitter.com/EgorEars) for constantly sharing with me a wealth of creativity in debugging and programming. Computers are twice as fun with such people around.

If you have debugging challenges in mind that you want me to tackle in the upcoming posts, let me know! For updates on the new posts like this, you can subscribe to my [blog](https://flounder.dev) or follow me on [Twitter](https://x.com/flounder4130).

Happy hacking!
