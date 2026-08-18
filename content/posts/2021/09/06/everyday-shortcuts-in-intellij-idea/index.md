---
title: "Everyday Shortcuts in IntelliJ IDEA"
slug: "everyday-shortcuts-in-intellij-idea"
date: "2021-09-06T07:27:35+00:00"
lastmod: "2021-09-06T07:31:41+00:00"
description: "It's really helpful to use keyboard shortcuts because it speeds you up and keeps you in the flow of coding!"
authors:
  - "helenjoscott"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "IntelliJ IDEA"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "javafx-gluon-status-update-dual-screen-on-raspberry-pi-sample-intellij-new-javafx-project-wizard"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

Let's take a look at some of my favourite keyboard shortcuts in [IntelliJ IDEA](https://www.jetbrains.com/idea/).

It's really helpful to use keyboard shortcuts because it speeds you up and keeps you in the flow of coding!

## Intention Actions - ⌥⏎ / Alt+Enter

You can use [this shortcut](https://www.jetbrains.com/help/idea/intention-actions.html) in a number of places, even when it's not an error or when IntelliJ IDEA has not given you any indication that you could use it. It's always worth trying it just to see what your options are.

Let's say you have declared three strings in one statement and you want to split them into separate declarations. You can place the caret anywhere on line 7 and use **⌥⏎** / **Alt\&Enter** and then select **Split into separate declarations**:

![Alt and Enter to split declaration](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/alt-enter-split-declaration.png)

When you press **⏎** / **Enter**, you have three separate declarations:

![Separate declarations](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/split-declarations.png)

You can use **⌥⏎** / **Alt\&Enter** in a number of different scenarios. It's always worth trying it to see what IntelliJ IDEA suggests in that respect.

## Search Everywhere - ⇧⇧ or Shift+Shift

You can tap **Shift** twice to display the [Search Everywhere](https://www.jetbrains.com/help/idea/searching-everywhere.html) dialogue:

![Search everywhere](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/search-everywhere.png)

In this dialog, you can search for pretty much anything right across your project. For example, you can search for *ignore*, and then you can tab through the various options that are available to you.

[![Search everywhere tab](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/search-everywhere-image-for-video.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/search-everywhere-tab.mp4)

The Git view is fairly new and really helpful for searching through your Git commits.

## Statement Completion - ⇧⌘⏎ / Ctrl+Shift+Enter

[Statement completion](https://www.jetbrains.com/help/idea/auto-completing-code.html#statements_completion) is a great habit to get into because it means that IntelliJ IDEA will do the best it can to keep your code compiling. That includes not only putting semicolons in places where semicolons should be, but also opening and closing your brackets and putting your caret in the right place.

For example, you can press **⇧⌘⏎** / **Ctrl+Shift+Enter** here to add the semi-colon. Yes, in this example it's more key presses, but it's about getting into the habit because it will remove the element of *not enough coffee* that we all suffer from periodically.

[![Statement Completion - add semi-colon](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/statement-completion-semi-colon.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/statement-completion-semi-colon.mp4)

Let's look at another example, you can type `private void newMethod(` and IntelliJ IDEA will close the bracket for you immediately, but then you can use Statement Completion again with **⇧⌘⏎** / **Ctrl+Shift+Enter** and IntelliJ IDEA will open and close the curly brackets and put the caret exactly where you expect it to be:

[![Statement Completion - add brackets](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/statement-completion-brackets-image.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/statement-completion-brackets.mp4)

## Reformat Code - ⌥⌘L / Ctrl+Alt+L

If you have some code that is badly formatted such as this one, there is a really easy way to [fix it up](https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html#reformat_code). You can use **⌥⌘L** / **Ctrl+Alt+L** to get IntelliJ IDEA to reformat the class for you:

[![Reformat Code](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/reformat-file-image.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/reformat-file.mp4)

IntelliJ IDEA will then reformat your code to adhere to your coding and your style guidelines, which is really nice, especially if you've just been hammering away at the code!

## Summary

Here are the four shortcuts that you can try in IntelliJ IDEA:

* Context Actions - ⌥⏎ or Alt+Enter
* Search Everywhere - ⇧⇧ or Shift+Shift
* Statement Completion - ⇧⌘⏎ / Ctrl+Shift+Enter
* Reformat Code - ⌥⌘L / Ctrl+Alt+L

There's also a [video available](https://youtu.be/matPBmotxvY) on the IntelliJ IDEA YouTube channel.
