---
title: "Working with Multiple Carets in IntelliJ IDEA"
slug: "working-with-multiple-carets-in-intellij-idea"
date: "2021-11-14T12:21:26+00:00"
lastmod: "2024-02-06T11:58:24+00:00"
description: "Sometimes you need to modify multiple lines of code on separate lines inside IntelliJ IDEA with the same change. Here's how!"
authors:
  - "helenjoscott"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "IntelliJ IDEA"
tags:
related_posts:
  - "creating-a-simple-spring-boot-application-in-intellij-idea"
  - "generating-code-with-intellij-idea"
  - "beginning-javafx-with-intellij"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

Sometimes you need to modify multiple lines of code on separate lines inside IntelliJ IDEA with the same change. The fastest way to achieve that is with [multiple carets](https://www.jetbrains.com/help/idea/working-with-source-code.html?keymap=primary_windows#multiple_cursor). In IntelliJ IDEA you can:

* Create multiple carets in a vertical line
* Create multiple carets at the end of lines
* Create multiple carets at any point in your code

<br />

<br />

### Create Multiple Carets in a Vertical Line {#h3-0-create-multiple-carets-in-a-vertical-line}

[Creating carets](https://www.jetbrains.com/help/idea/working-with-source-code.html?keymap=primary_windows#multiple_cursor) that are stacked vertically where you have content on different lines, like a list, can save you a lot of time when you're editing your code.

* **macOS** press **Option** twice and hold it down the second time. Then use the arrow keys (up or down)
* **Windows** press **Ctrl** twice and hold it down the second time. Then use the arrow keys (up or down)

Let's say you have defined some variables at the start of your class, but retrospectively you realise that their scope should be `private`, and since we're defining them here, they should also be `final`:

```java
public String oak = "Oak";
public String sycamore = "Sycamore";
public String pine = "Pine";
```


You could click each one and replace `public` with `private` but there's a faster way in IntelliJ IDEA. You can press **Option** (macOS), or **Ctrl** (Windows/Linux) twice, holding it down the second time you press it and then use arrow keys to create a tower of vertical carets. You can then replace public with private just once, not three times. You can also add `final` since you're here:

[![Modifying variables with multiple carets](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-variables.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-variables.mp4)

How about dealing with a big list that you've pasted into your code? How can you inline everything? First let's make an array for our data, then we can use the same clone caret operation before wrapping it in braces and completing the statement:

[![Inlining a long list](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-input-list.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-input-list.mp4)

Finally, how about HTML tags? Again, it's the same process:

[![HTML tags and multiple carots](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-html-tags.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-html-tags.mp4)

### Create Multiple Carets at the End of Lines {#h3-1-create-multiple-carets-at-the-end-of-lines}

Content at the start of your line is likely to be left-aligned, but that's not the case at the end of your line. We can deal with that by using the mouse to drag the caret down, instead of the vertical arrow keys.

Place your cursor to the far right of the furthest line of code and:

* **macOS** hold down **Option** and drag the mouse down (or up)
* **Windows** hold down **Alt** and drag the mouse down (or up)
  * Note that for Windows the shortcut for this one is **Alt** , not **Ctrl**

Let's say that you want to add something to the end of each of these strings, for example:

```java
public final String lion = "Lives in Africa";
public final String stoat = "Lives in the United Kingdom";
public final String wolf = "Lives in Europe";
```


```

```

This time, instead of cloning the caret in a vertical line with the down arrow, hold down **Option** (macOS), or **Alt** (Windows/Linux) and use the mouse to place your caret to the right of the all the lines and then drag the mouse down. The caret will be placed at the end of each line. You can then add the text even though the lines are not in a straight line:

[![Multiple carets at the end of the line](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-end-of-line.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-end-of-line.mp4)

### Create Multiple Carets at Any Point in Your Code {#h3-2-create-multiple-carets-at-any-point-in-your-code}

And finally, what if you want multiple carets, but not in a stacked vertical line or at the end of each line? Again, the shortcut is similar:

* **macOS** hold down **Option** and then **Shift** and click where you want the caret
* **Windows** hold down **Alt** and then **Shift** and click where you want the caret

Let's say you want to add "Name" to these variables, but only to some of them:

[![Multiple carets at the end of the line](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-random.png)](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/multiple-carets-random.mp4)

### Summary {#h3-3-summary}

There's lots of places that [multiple carets](https://www.jetbrains.com/help/idea/working-with-source-code.html?keymap=primary_windows#multiple_cursor) are helpful in [IntelliJ IDEA](https://www.jetbrains.com/idea/), especially when you're working with lists. Just remember that for carets in a vertical line, press **Option** (macOS) and **Ctrl** (Windows/Linux) twice, holding it down the second time.

For carets to the right of a line hold down **Option** (macOS), or **Alt** (Windows/Linux) and drag the mouse pad/wheel down (or up).

For random carets, you add press and hold **Option** +**Shift** (macOS), or **Alt** +**Shift** (Windows/Linux), then click anywhere to place extra carets.

Multiple carets can also be created using [Column Selection Mode](https://www.jetbrains.com/help/idea/working-with-source-code.html?keymap=secondary_macos#multiple_cursor). There is an example of this, and the techniques we talked about above, in [this video from JetBrains](https://www.youtube.com/watch?v=JEpeHNsWIMk), specifically at [this time point](https://youtu.be/JEpeHNsWIMk?t=186). There are lots of other helpful tips in the video as well.
