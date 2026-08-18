---
title: "Presenting with IntelliJ IDEA"
date: "2022-10-11T07:14:30+00:00"
lastmod: "2022-10-11T07:14:32+00:00"
description: "Let's learn a lot of different tips and tricks that will level up your presentation skills with IntelliJ IDEA!"
authors:
  - "marit-van-dijk"
image: "qss-enter-presentation-mode.png"
categories:
  - "Developer Tools"
  - "IntelliJ IDEA"
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "beginning-javafx-with-intellij"
frozen: false
---

In this article, we will take a look at ways to level up your presentation skills with IntelliJ IDEA.

## Presentation Assistant plugin

Our first tip is to use the Presentation Assistant plugin. The Presentation Assistant plugin will show which shortcuts are being used.

{{< img src="presentation-assistant-example-700x137.png" class="size-medium" alt="Presentation Assistant plugin example" width="700" height="137" >}}

To install the plugin, open Preferences using **⌘,** (on Mac) or **Ctrl+Alt+S** (on Windows/Linux). Go to Plugins and search for "Presentation assistant" on the Marketplace tab. Click **Install** and when it's done, click **OK** to apply the changes and close the dialog or click **Apply** to keep the dialog open.

{{< img src="presentation-assistant-install-700x508.png" class="size-medium" alt="Installing the Presentation Assistant plugin in IntelliJ IDEA Preferences" width="700" height="508" >}}

To configure the Presentation Assistant plugin, go back to **Preferences** and go to **Appearance \& Behavior \> Presentation Assistant**.

![](presentation-assistant-config-700x508.png)

Here we can configure font size, duration, alignment, and which keymaps to show. We are using macOS, with Windows as the alternative keymap. We have also configured the Presentation Assistant plugin to show the shortcuts at the top of the screen.

## Presentation Mode

When presenting, you can use **Presentation Mode**. The IDE switches to full screen and everything is hidden except for the main editor window. The font size is increased, so it is easier to read from a distance.

{{< img src="presentation-mode-700x436.png" class="size-medium" alt="Presentation Mode in IntelliJ IDEA" width="700" height="436" >}}

You can switch to Presentation Mode by clicking **View \> Appearance \> Enter Presentation Mode**.

{{< img src="enter-presentation-mode-700x280.png" class="size-medium" alt="Enter Presentation Mode using the menu in IntelliJ IDEA" width="700" height="280" >}}

**Exit Presentation Mode** by clicking **View \> Appearance \> Exit Presentation Mode**.

{{< img src="exit-presentation-mode-700x280.png" class="size-medium" alt="Exit Presentation Mode using the menu in IntelliJ IDEA" width="700" height="280" >}}

If needed, the font size in Presentation Mode can be configured in **Preferences \> Appearance \& Behavior \> Appearance** . Scroll down to **Presentation Mode** , and font size and set the font size you want. Click **OK** to apply the changes and close the dialog or click **Apply** to keep the dialog open. Click **Cancel** to discard the changes and close the dialog.

{{< img src="presentation-mode-config-font-size-615x510.png" class="size-medium" alt="Configuring Presentation Mode Font Size in IntelliJ IDEA Preferences" width="615" height="510" >}}

We can also open Presentation Mode using short-cuts. Open the **Quick Switch Scheme** using **⌃ BackTick** (on Mac) or **Ctrl+BackTick** (on Windows/Linux). Use the arrows to select **View Mode** and then select **Enter Presentation Mode**.

![Switch to View Mode in Quick Switch Scheme](qss-view-mode.png) ![Enter Presentation Mode in Quick Switch Scheme](qss-enter-presentation-mode.png)

We can use **Quick Switch Scheme** again to **Exit Presentation Mode**.

{{< img src="qss-exit-presentation-mode-1-700x323.png" class="size-medium" alt="Exit Presentation Mode in Quick Switch Scheme" width="700" height="323" >}}

## Mouse Zoom

To enable mouse zoom, you need to turn it on explicitly. Go to **Preferences \> Editor \> General** and select **Change font size with Command+Mouse Wheel** (on Mac) or **Change font size with Control+Mouse** (on Windows/Linux). Click **OK** to apply the changes and close the dialog or click **Apply** to keep the dialog open.

{{< img src="enable-mouse-zoom-698x510.png" class="size-medium" alt="Enable Mouse Zoom in IntelliJ IDEA Preferences" width="698" height="510" >}}

Now we can use **Command+Mouse Wheel** (on Mac) or **Control+Mouse Wheel** (on Windows/Linux) to zoom in or out. While we are using mouse zoom, a popup appears containing the current font size on the left and a link to reset to the original font size on the right. Click the link to reset the font size.

![Reset font size](resize.png)

## Font

To configure font and font size, go to **Preferences \> Editor \> Font**. We can select the font we want to use, and set the font size.

{{< img src="font-700x508.png" class="size-medium" alt="Configuring font and font size in IntelliJ IDEA Preferences" width="700" height="508" >}}

When presenting in person, make sure the font can be read from a distance. If possible, check out the room where you'll be speaking, do your setup before your talk, and walk to the back of the room to check if the code is readable.

## Theme

By default, IntelliJ IDEA uses the Darcula theme. To change it, go to **Preferences \> Appearance \& Behavior \> Appearance**.

Select the UI theme from the Theme list:

* **IntelliJ Light** is a traditional light theme for IntelliJ-based IDEs
* **macOS Light** or **Windows 10 Light** is an OS-specific light theme available as a bundled plugin
* **Darcula** is the default dark theme that we're using here
* **High contrast** is a theme designed for users with color vision deficiency

{{< img src="theme-700x507.png" class="size-medium" alt="Selecting a Theme in IntelliJ IDEA Preferences" width="700" height="507" >}}

Which theme to use depends on your personal preference. When presenting in front of an audience, you may want to take into account how light or dark the room is. If possible, try different themes and walk to the back of the room to see which one works best before your presentation. When sharing your screen during an online meeting, you may also want to check with teammates which theme works best.

## Tool windows

We recommend closing all unused windows so we can focus on the code we're looking at. Use **Command + Shift + F12** (on Mac) or **Control+Shift+F12** (on Windows/Linux) to hide all tool windows.

{{< img src="hide-all-windows-1-700x419.png" class="size-medium" alt="Hide all windows in IntelliJ IDEA" width="700" height="419" >}}

We can always reopen them using shortcuts. Here are the shortcuts to some of the most used windows:

* To open or close the **Project Tool Window** use **⌘1** (on Mac) or **Alt+1** (on Windows/Linux)
* To open or close the **Commit Tool Window** use **⌘0** (on Mac) or **Alt+0** (on Windows/Linux)
* To open or close the **Terminal Tool Window** use **Alt+F12**
* To open or close the **Git Tool Window** use **⌘9** ( on Mac) or **Alt+4** (on Windows/Linux)
* To open or close the **Run Tool Window** use **⌘4** ( on Mac) or **Alt+4** (on Windows/Linux)
* To open or close the **Debug Tool Window** use **⌘5** ( on Mac) or **Alt+5** (on Windows/Linux)

You can also use keyboard shortcuts to stretch the active window. Go to the relevant window, and use **⌃ ⌥ ←** or **⌃ ⌥ →** (on Mac) or **Control+Alt+Shift+Left** or **Control+Alt+Shift+Right** (on Windows/Linux) to stretch the window left or right. This also works with the tool windows at the bottom, using the up or down arrows to stretch the window up or down.

## Find action

If you want to do something, but don't remember the shortcut or menu option, use **Find Action** . Press **⇧⌘A** (on Mac) or **Control+Shift+A** (on Windows/Linux) to open the **Find Action** dialog. Search for the action you want, and select the relevant action.

{{< img src="find-action-700x57.png" class="size-medium" alt="Find Action in IntelliJ IDEA" width="700" height="57" >}}

For example, let's use **Find Action** to apply soft-wrap.

## Soft-wrap

When using a file that has long lines, we can prevent horizontal scrolling by using soft-wrap. Let's use **Find Action** to enable soft-wrap. Press **⇧⌘A** (on Mac) or **Control+Shift+A** (on Windows/Linux) to open the Find Action dialog, and search for "soft-wrap".

{{< img src="find-action-softwrap-700x106.png" class="size-medium" alt="Find Action soft-wrap in IntelliJ IDEA" width="700" height="106" >}}

We get the option to turn on soft-wrap, which will be for this file only. You can click this option to turn soft-wrap on or off.

{{< img src="find-action-softwrap-on-700x109.png" class="size-medium" alt="Find Action and turn soft-wrap ON in IntelliJ IDEA" width="700" height="109" >}}

Alternatively, we can configure soft-wrap. Let's select "**Soft wrap these files** ", which will soft-wrap several types of text files. Click **OK** to apply the changes and close the Preferences dialog.

{{< img src="config-softwrap-700x505.png" class="size-medium" alt="Configure soft-wrap in IntelliJ IDEA Preferences" width="700" height="505" >}}

## Shortcuts

We recommend using shortcuts as much as possible when presenting. This is easier to do during a presentation than using a mouse or trackpad, especially if you get nervous and your hands might get slippery. There are several ways to learn shortcuts.

* **Find Action** lets you search for commands and settings across all menus and tools.
* [Key Promoter X](https://plugins.jetbrains.com/plugin/9792-key-promoter-x) is a plugin that shows a popup notification with the corresponding keyboard shortcut whenever a command is executed using the mouse. It also suggests creating a shortcut for commands that are executed frequently.
* If you are using one of the predefined keymaps, you can print the [default keymap reference card](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf) and keep it on your desk to consult it if necessary. This cheat sheet is also available under **Help \> Keyboard Shortcuts PDF**.
* To print a non-default or customized keymap, use the [Keymap exporter plugin](https://plugins.jetbrains.com/plugin/7066-keymap-exporter).

Let's take a look at some shortcuts for navigation.

## Navigation

We can use several shortcuts to navigate around a project:

We can **Find a class** using **⌘ O** (on Mac) or **Ctrl+N** (on Windows/Linux).

{{< img src="find-class-700x317.png" class="size-medium" alt="Find Class in IntelliJ IDEA" width="700" height="317" >}}

We can move the caret to a method and use **⌘ B** (on Mac) or **Ctrl+B** (on Windows/Linux) to go to the declaration or usages.

{{< img src="find-usages-700x316.png" class="size-medium" alt="Find Usages in IntelliJ IDEA" width="700" height="316" >}}

{{< img src="find-only-usage-700x243.png" class="size-medium" alt="Find Usage in IntelliJ IDEA" width="700" height="243" >}}

We can also navigate backwards by using **⌘ \[** (on Mac) or **Ctrl+Alt+Left** (on Windows/Linux), or navigate forward: **⌘ \]** (on Mac) or **Ctrl+Alt+Right** (on Windows/Linux).

Use **⇧ ⌘ E** (on Mac) or **Ctrl+Shift+E** (on Windows/Linux) to find **Recent Locations**.

{{< img src="recent-locations-700x358.png" class="size-medium" alt="Recent Locations in IntelliJ IDEA" width="700" height="358" >}}

Or find **Last edited location** with **⇧ ⌘ ⌫** (on Mac) or **Ctrl+Shift+Backspace** (on Windows/Linux).

Finally, we can find **Recent Files** using **⌘ E** (on Mac) or **Ctrl+E** (on Windows/Linux).

{{< img src="recent-files-700x357.png" class="size-medium" alt="Recent Files in IntelliJ IDEA" width="700" height="357" >}}

## Bookmarks

Another way to navigate through code, is to use bookmarks. Press **F3** (on Mac) or **F11** (on Windows/Linux) to create an anonymous line bookmark.

{{< img src="anonymous-bookmark-700x157.png" class="size-medium" alt="Anonymous Line Bookmark in IntelliJ IDEA" width="700" height="157" >}}

To add a mnemonic line bookmark, press **⌥ F3** (on Mac) or **Control+F11** (on Windows/Linux). In the popup that opens, select a number or a letter that you want to use as an identifier for this bookmark. Press **⏎** (on Mac) or **Enter** (on Windows/Linux) to save the bookmark. To bookmark files, packages, folders, and modules, right-click the item you want to bookmark in the **Project Tool Window** , and add an anonymous bookmark (**F3** (on Mac) or **F11** (on Windows/Linux)), or a mnemonic bookmark (**⌥ F3** (on Mac) or **Control+F11** (on Windows/Linux)).

{{< img src="mnemonic-bookmark-digit-700x308.png" class="size-medium" alt="Add Mnemonic Bookmark in IntelliJ IDEA" width="700" height="308" >}}

{{< img src="mnemonic-bookmark-700x124.png" class="size-medium" alt="Mnemonic Line Bookmark in IntelliJ IDEA" width="700" height="124" >}}

There are several ways to navigate between bookmarks. Press **⌘ F3** (on Mac) or **Control+F11** (on Windows/Linux) to open a popup showing bookmarks and select the desired bookmark with the keyboard and press **⏎**, or select the corresponding digit or letter for a mnemonic bookmark.

{{< img src="bookmarks-700x284.png" class="size-medium" alt="Bookmarks in IntelliJ IDEA" width="700" height="284" >}}

To jump straight to a mnemonic bookmark, hold **\^** (on Mac) or **Control** (on Windows/Linux) and press the mnemonic digit or letter on the keyboard.

## Summary and Shortcuts

Now we know several tricks that will level up our presentation skills with IntelliJ IDEA.

### IntelliJ IDEA Shortcuts Used

Here are the IntelliJ IDEA shortcuts that we used.

|                                               Name                                               |   macOS Shortcut    |  Windows / Linux Shortcut   |
|--------------------------------------------------------------------------------------------------|---------------------|-----------------------------|
| [Open Preferences](https://www.jetbrains.com/help/idea/settings-preferences-dialog.html)         | **⌘,**              | **Ctrl+Alt+S**              |
| Open Quick Switch Scheme                                                                         | **⌃`** | **Ctrl+`** |
| Hide all windows                                                                                 | **⇧⌘F12**           | **Control+Shift+F12**       |
| Open / Close [Project Tool Window](https://www.jetbrains.com/help/idea/project-tool-window.html) | **⌘1**              | **Alt+1**                   |
| Open / Close \[Commit Tool Window\]                                                              | **⌘1**              | **Alt+1**                   |
| Open / Close \[Terminal Tool Window\]                                                            | **⌥F12**            | **Alt+F12**                 |
| Open / Close [Git Log tool Window](https://www.jetbrains.com/help/idea/investigate-changes.html) | **⌘9**              | **Alt+9**                   |
| Open / Close [Run Tool Window](https://www.jetbrains.com/help/idea/run-tool-window.html)         | **⌘4**              | **Alt+4**                   |
| Open / Close \[Debug Tool Window\]                                                               | **⌘5**              | **Alt+5**                   |
| Stretch to Left                                                                                  | **⌃⌥←**             | **Ctrl+Alt+Shift+Left**     |
| Stretch to Right                                                                                 | **⌃⌥→**             | **Ctrl+Alt+Shift+Right**    |
| Stretch to Top                                                                                   | **⌃⌥↑**             | **Ctrl+Alt+Shift+Up**       |
| Stretch to Bottom                                                                                | **⌃⌥↓**             | **Ctrl+Alt+Shift+Down**     |
| Find Action                                                                                      | **⇧⌘A**             | **Control+Shift+A**         |
| Find a class                                                                                     | **⌘O**              | **Control+N**               |
| Go to declaration or usages                                                                      | **⌘B**              | **Control+B**               |
| Navigate backward                                                                                | **⌘\[**             | **Control+Alt+Left**        |
| Navigate forward                                                                                 | **⌘\]**             | **Control+Alt+Right**       |
| Recent Locations                                                                                 | **⇧⌘E**             | **Control+Shift+E**         |
| Last Edited Location                                                                             | **⇧⌘⌫**             | **Control+Shift+Backspace** |
| Recent Files                                                                                     | **⌘E**              | **Control+E**               |
| Anonymous Bookmark                                                                               | **F3**              | **F11**                     |
| Mnemonic Bookmark                                                                                | **⌥F3**             | **Control+F11**             |
| Save Mnemonic Bookmark                                                                           | **⏎**               | **Enter**                   |

### Related Links

* [(video) JetBrains - Presenting with IntelliJ IDEA](https://www.youtube.com/watch?v=h-HGg9b6Dqw)
* [(docs) JetBrains - Settings/Preferences](https://www.jetbrains.com/help/idea/settings-preferences-dialog.html)
* [(docs) JetBrains - Install plugins](https://www.jetbrains.com/help/idea/managing-plugins.html)
* [(marketplace) JetBrains - Presentation Assistant plugin](https://plugins.jetbrains.com/plugin/7345-presentation-assistant)
* [(docs) JetBrains - IDE viewing modes](https://www.jetbrains.com/help/idea/ide-viewing-modes.html)
* [(docs) JetBrains - IntelliJ IDEA keyboard shortcuts](https://www.jetbrains.com/help/idea/mastering-keyboard-shortcuts.html)
* [(pdf) IntelliJ IDEA reference card](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf)
* [(docs) JetBrains - Configure keyboard shortcuts](https://www.jetbrains.com/help/idea/configuring-keyboard-and-mouse-shortcuts.html)
* [(docs) JetBrains - Creating custom shortcuts](https://www.jetbrains.com/idea/guide/tips/assign-top-shortcuts/)
* [(marketplace) JetBrains - Key Promoter X plugin](https://plugins.jetbrains.com/plugin/9792-key-promoter-x)
* [(marketplace) JetBrains - Keymap exporter plugin](https://plugins.jetbrains.com/plugin/7066-keymap-exporter)
* [(docs) JetBrains - Navigation and search](https://www.jetbrains.com/help/idea/discover-intellij-idea.html?keymap=secondary_macos#navigation-and-search)
* [(docs) JetBrains - Source code navigation](https://www.jetbrains.com/help/idea/navigating-through-the-source-code.html#advanced-features)
* [(docs) JetBrains - Bookmarks](https://www.jetbrains.com/help/idea/bookmarks.html)
