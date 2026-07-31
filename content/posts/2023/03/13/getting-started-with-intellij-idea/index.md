---
title: "Getting Started with IntelliJ IDEA"
slug: "getting-started-with-intellij-idea"
date: "2023-03-13T08:26:00+00:00"
lastmod: "2023-03-13T08:26:02+00:00"
description: "Starting in a new team can be daunting. You have to learn All The Things, one of which is your IDE! So, how can you best learn IntelliJ IDEA?"
canonical: "https://www.helenjoscott.com/2023/02/28/getting-started-with-intellij-idea/"
authors:
  - "helenjoscott"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "Developer Tools"
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "everyday-shortcuts-in-intellij-idea"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

First up, I have[created a tutorial on the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/) if you want to view the content with videos. I'll concentrate on text-based for this article.

When someone new joins your team, it can be quite an overwhelming experience.

There are lots of people to meet, new technologies to learn, different processes to learn and of course, an IDE to learn!

It's helpful to remember what every new starter wants, they want to make a difference, they want to add value, they want to *move the needle*.

I have created these tips and grouped them to help everyone who is new to IntelliJ IDEA learn in logical groups.

You don't need to know *everything* in the IDE to be a badass developer, but learning a bunch of functionality that will help you to go faster in 80% of cases is a great way to start.

I've ordered these tips in the order you're most likely to want to get to grips with them, but feel free to choose your own adventure!

Working together {#h2-0-working-together}
-----------------------------------------

There really is no substitute for sitting next to someone and pairing on something in the IDE; it's a great way to learn a bunch of new keyboard shortcuts and see how your peers use the tool. However, the world was reshaped a few years ago, and now remote working is commonplace, which means you may not be co-located with your peers to benefit from this way of learning.

This is where[JetBrains Code With Me](https://www.jetbrains.com/code-with-me/) can add a lot of value, especially if you use it with the[Presentation Assistant plugin](https://plugins.jetbrains.com/plugin/7345-presentation-assistant) loaded so you can see the host's keyboard shortcuts. There are some licensing considerations to be aware of, but it's available in IntelliJ IDEA Community edition for individual users of up to 3 users for 30 minutes for free.

You can watch these videos to learn more about Code With Me:

* [Introducing Code With Me](https://www.youtube.com/watch?v=Lq0fCMCK-Yw)
* [Code With Me Quick Setup and Overview](https://www.youtube.com/watch?v=uewvw4KlRKE)
* [Code With Me In Depth Tour](https://www.youtube.com/watch?v=BXUai0IIlRw)

And read about it on the JetBrains blog:

* [Posts about Code With Me](https://blog.jetbrains.com/tag/codewithme/)

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/working-together/).

Learning the IDE {#h2-1-learning-the-ide}
-----------------------------------------

This one seems kinda obvious... of course, you want to Learn the IDE; that's why you're here! That said, you may not know about the Feature Trainer because it's a relatively recent addition to the product.

You can access the Feature Trainer from the home page by clicking on the **Learn** tab, or, if you're already in a project, go to **Help** \> **Learn IDE Features**.

The current lesson list is:

* Onboarding tour
* Essential
* Editor basics
* Code completion
* Refactorings
* Code assistance
* Navigation
* Run and debug
* Git

Each of these has several lessons within it that take you through a feature that can help you in your workflow.

Even not-so-new-starters can learn something from the Feature Trainer, so go ahead and check it out on a Friday afternoon when you have some headspace to learn something new.

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/learning-the-ide/).

Customising your IntelliJ IDEA Environment {#h2-2-customising-your-intellij-idea-environment}
---------------------------------------------------------------------------------------------

Since we spend a large portion of our day working inside the IDE, it's reasonable to assume that we will want to customise it for our eyes.

Of course, this is separate to code style, which should be standardised across the team you're working with; that's one advantage of checking in your `.idea` file.

When you're new to IntelliJ IDEA, the Preferences/Settings dialog (**⌘, \| Ctrl+Alt+S**) can be quite daunting but you can search for what you're looking for, such as "theme", "keymap", "plugins" or "font".

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/setting-up-the-env/).

Navigating Code {#h2-3-navigating-code}
---------------------------------------

One of the first tasks you have when you are getting to know a new codebase is how to move around it. Again there are lots of ways to do that in IntelliJ IDEA, but I've focused on the ones that I think are most helpful for new users.

Of course, you can use the Project tool window to move around your files, but I recommend getting comfortable with both Recent Files (**⌘E** \| **Ctrl+E** ) and Recent Locations (**⌘⇧E** \| **Ctrl+Shift+E** ) as these can be thought of as your *working context*.

As you get more familiar with the structure of the codebase, I recommend you add Go To Declaration or Usages (**⌘B** \| **Ctrl+B** ) and Go to Implementation (**⌘⌥B** \| **Ctrl+Alt+B**) to your repertoire.

Finally, as you start to figure out your workflow you may need to move between files in a sequential order; this is where (**⌘\[** \| **Ctrl+Alt+←** ) to go backwards and (**⌘\[** \| **Ctrl+Alt+→**) to go forwards comes in.

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/navigating-code/).

Finding Code {#h2-4-finding-code}
---------------------------------

As you move from looking around the code to looking for specific code that performs a task, you can get a lot of support from your IDE. Search Everywhere (**⇧⇧** \| **Shift+Shift** ) and Find Action (**⌘⇧A** \| **Ctrl+Shift+A**) are great ways to narrow down your search in the first instance.

As you progress to more specific searching, I recommend you check out Structural Search by using either Search Everywhere or Find Action and typing in "structural".

You can then build your own queries as required. There's an example in the link below for the IntelliJ IDEA Guide.

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/finding-code).

Reading Code {#h2-5-reading-code}
---------------------------------

As we all realise as we progress through our careers, we spend more time reading code then writing it. Maybe we wrote that code, or someone else wrote that code; either way you often need to understand what some code does.

Tools you can use to help you read code include Inlay Hints, which you can turn on and off in the Settings and Preferences (search for "inlay hints"). These _decorate _your code with annotations that can help you to understand the flow of data.

The scrollbars are another great source of information. Coloured blocks on the left-hand scroll bar show you if code has been added, deleted, changed or has an error in for the code you are currently viewing.

The right-hand scrollbar shows you the same information for the whole file so you can see at a glance without scrolling if you have any errors and if everything is as you expect it to be.

Finally, sometimes you're reading code because you're fixing bugs. IntelliJ IDEA has an Inspections Widget, which appears at the top right of the file by default.

This tells you the _state _of the file and if there are any errors. You can either click on it to open the Problems tool window, go to the next error directly with (**F2 \| F2**).

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/reading-code/).

Understanding Code {#h2-6-understanding-code}
---------------------------------------------

When you come across a block of code that you don't fully understand, you can use Context Actions (**⌥⏎ \| Alt+Enter**) to see if IntelliJ IDEA has any suggestions for reshaping the code without changing what it does.

This is worth doing even if there's no highlighting on the code to indicate a potential refactor.

IntelliJ IDEA also allows you to Find the Type (**⌃⇧P** \| **Ctrl+Shift+P**) which can be a helpful shortcut to remember, especially when you're working with larger, more complex blocks of code.

Finally, you can analyse the flow of data through your code with **Code \> Analyze Code \> Data Flow to Here** and to analyse data flow downstream use **Code \> Analyze Code \> Data Flow from Here**.

Using data flow analysis allows you to track both the data input (producer) and as the data output (consumer).

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/understanding-code/).

Writing Code {#h2-7-writing-code}
---------------------------------

Writing code is of course part of our day job and, again an area that IntelliJ IDEA can support you. As you'd expect from your IDE, IntelliJ IDEA gives you extensive code completion including Type-Based completion (**⌃␣** \| **Ctrl+Space**), which filters the list to only types that apply to that specific context.

Support for code generation is extensive. You can use (**⌘N** \| **Alt+Ins**) both in the Project tool window and in the editor to generate new files and common code constructs.

Similar to Search Everywhere, IntelliJ IDEA supports Running Anything (**⌃⌃** \| **Ctrl+Ctrl** ) which you can use to run any of your Run Configurations as well as scripts and commands such as `mvn clean` or `gradle --status`.

Lastly, IntelliJ IDEA has recently made a number of changes to support you in managing dependencies. You can use the new Dependencies tool window to view and update your dependencies using the[Package Search](https://package-search.jetbrains.com/) functionality.

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/writing-code/).

Changing Code {#h2-8-changing-code}
-----------------------------------

Changing code is one of those inevitabilities in our jobs. Perhaps we're refactoring it, adding functionality or deleting it; either way, you will be changing some code you or someone else wrote at some point. Use (**⌃T** \| **Ctrl+Alt+Shift+T**) to pop up the refactoring menu.

Once you've refactored code, you can reformat the code with (**⌥⌘L** \| **Ctrl+Alt+L**).

Maybe you want to make the same change in multiple places; IntelliJ IDEA allows you to create multiple carets either in a stacked list or randomly as required. (**⌥** \| **Ctrl** ) twice, holding it down the second time and then pressing the **Down** arrow to create a stack of carets or by clicking in your code to add a caret. You can learn more about this feature from this[blog post on foojay](https://foojay.io/today/working-with-multiple-carets-in-intellij-idea/).

Sometimes, especially with long blocks of code or poorly formatted code, it's hard to see where scope starts and stops. You can use (**⌥↑** \| **Ctrl+W** ) to expand the scope and (**⌥↓** \| **Ctrl+Shift+W**) to collapse the scope.

IntelliJ IDEA supports as many clipboards as you need meaning everything you copy inside IntelliJ IDEA is available for you to paste, even if it wasn't the last thing you copied. One of my favourite features is being able to copy multiple things and then pasting them in one list from the Clipboard History (**⌘⇧V \| Ctrl+Shift+V**).

Lastly, sometimes things go wrong, and it can seem like you've lost your work. IntelliJ IDEA takes snapshots of your code while you work (no save necessary!) when certain events happen. Those events might be refactoring a piece of code, a test passing or failing, or some other change you make.

These changes are stored as your Local History. While it is not a substitute for a version control system such as Git, Local History can step in and help when things take an unexpected turn.

You can access Local History from the right-click context menu both at the directory level in the Project tool window and on an individual file basis. The diff view it provides allows you to select which changes you'd like to restore to your file, or perhaps the whole file!

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/changing-code/).

Testing Code {#h2-9-testing-code}
---------------------------------

Of course, you want to test your code! Perhaps you're even doing TDD, but how can IntelliJ IDEA help you here? First up, you can split windows in the editor, allowing you to have your code on one side and your test on the other so you don't have to flick between them.

You can open any file from the Project tool window in a right split by holding down (**⇧** \| **Shift** ) when you press (**⏎** \| **Enter** ). The same tip works to open a file from the Recent Files dialog (**⌘E** \| **Ctrl+E**).

Sometimes it's helpful to jump from the file to its associated test if it has one. If you're in a class and use Navigate to Test (**⇧⌘T** \| **Ctrl+Shift+T**), IntelliJ IDEA will look for a test that it thinks tests that file.

For example, if your class is called BlueMoon, IntelliJ IDEA might reasonably assume that a file called BlueMoonTest is in fact the test for that file and take you there.

If you use Navigate to Test and IntelliJ IDEA can't find a test class it will suggest creating a new one for you and offer to generate some test method code constructs.

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/testing-code/).

Seeing How Far You've Come {#h2-10-seeing-how-far-you-ve-come}
--------------------------------------------------------------

The Productivity Guide in IntelliJ IDEA is a fun way to see which shortcuts you have used and which you have yet to try. You can access it from **Help** \> **My** **Productivity**.

Check it out to see what you could learn next!

.... and finally,[the step in the IntelliJ IDEA Guide](https://www.jetbrains.com/idea/guide/tutorials/getting-started-intellij-idea/seeing-how-far-youve-come/).
