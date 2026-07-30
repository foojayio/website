---
title: "Resolving Git Merge Conflicts in IntelliJ IDEA | Foojay.io Today"
slug: "resolving-git-merge-conflicts-in-intellij-idea"
date: "2022-09-21T11:04:06+00:00"
lastmod: "2022-09-21T11:12:42+00:00"
description: "At some point in your career, probably many points, you'll have to resolve merge conflicts. Learn how on Foojay.io!"
authors:
  - "helenjoscott"
image: "https://foojay.io/wp-content/uploads/2022/09/merge-feature-into-main-1.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

At some point in your career, probably many points, you'll have to resolve merge conflicts.

A common cause of merge conflicts is when you're working on a `feature` branch while other changes have been applied to the `main` branch.

Merge feature branch into main {#h2-0-merge-feature-branch-into-main}
---------------------------------------------------------------------

In this tutorial, we are currently on the `main` branch, and we want to merge the `feature` branch into `main`.

You can do this by selecting the feature branch in the *Git* tool window which you can open with **⌘9** (macOS), or **Alt+9** (Windows/Linux).

Select the feature branch and then choose *Merge feature into main*.

![Merge feature branch into main IntelliJ IDEA popup](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/merge-feature-into-main.png)

When you press **⏎** (macOS), or **Enter** (Windows/Linux), IntelliJ IDEA opens a popup telling us there are merge conflicts.

![Conflicts dialog](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/conflicts-dialog.png)

The *Conflicts* dialog offers several options:

* **Accept yours** will apply the changes on the current branch (main) and discard the changes from the other branch (feature).
* **Accept theirs** will apply the changes from the other branch (feature) and discard the changes from current branch (main).
* **Merge** will open up the Merge dialog. We will focus on this option in this tutorial.

If you are unsure about which is "yours" and which is "theirs", note that these are also visible in the table to the left of the buttons and the branch names are mentioned in brackets.

![Yours Theirs labels in the Conflicts dialog](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/yours-theirs.png)

You see that both branches have been modified. Choosing either **Accept yours** or **Accept theirs** in this case would discard the changes made on the `feature` or `main` branch respectively.

Only choose those options if you want to discard those changes.

If you click **Close** in the *Conflicts* dialog, IntelliJ IDEA tells you that the feature was *Merged with Conflict* and you can click **Resolve** to reopen the *Conflicts* dialog.

![Feature Merged with Conflict](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/feature-merged-with-conflict.png)

Alternatively, if you accidentally click **Close** , a *Merge Conflicts* node will appear in the Local Changes view too. You can open the **Commit** tool window using **⌘0** (macOS), or **Alt+0** (Windows/Linux) and open the file.

![Changes Commit Tool Window](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/changes-commit-tool-window.png)

Click the arrow to see the *Merge Conflicts* node. You can click **Resolve** to reopen the *Conflicts* dialog.

Resolving Merges {#h2-1-resolving-merges}
-----------------------------------------

Back on the *Conflicts* dialog, there are changes on both branches that we want to keep so you can click **Merge** to open the *Merge Revision*s dialog.

Here you can see the changes to our current branch (`main`) on the left, and the changes that we want to merge from the `feature` branch on the right. There is a fully-functional editor in the middle.

![Merge Non Conflicting Changes](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/merge-non-conflicting-changes.png)

There are several changes:

* a removed line is shown in grey
* lines that were added are shown in green
* lines that were changed are shown in blue; the changes made are highlighted
* conflicts are shown in red

For each change you can decide to ignore it by clicking the **x** or to accept it by clicking the arrows.

You can use **⌘Z** (macOS), or **Ctrl+Z** on (Windows/Linux) to undo an action here if required.  

Depending on the number of changes, accepting or ignoring each change individual change might take some time.

You can merge all non-conflicting changes automatically with **Apply All Non-Conflicting Changes**.

Alternatively, you can use **Apply Non-Conflicting Changes from the Left Side** or **Apply Non-Conflicting Changes from the Right Side** to merge non-conflicting changes from the left/right parts of the dialog respectively.

When you have accepted all the changes, the **Left/All/Right** buttons are unavailable as there are no more non-conflicting changes to apply. There is just one remaining conflict.

![Final Conflict displayed](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/final-conflict.png)

Another way of resolving conflicts is to right-click on the highlighted conflict in the central pane and use the commands from the context menu.

![Right-click merge context menu](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/right-click-context-menu.png)

When you select **Accept the left side** or **Accept the right side**, the change from the selected side will be applied and the change on the other side will remain open.

When you choose **Resolve using Left** or **Resolve using Right**, the changes from either the left or right respectively will be applied and the changes from the other side will be ignored. In this example, lets combine the changes from both sides, so we will choose to accept them both.

At this point, IntelliJ IDEA has concluded that all changes have been processed, so we can click **Apply** to save the changes and finish merging which will close this dialog.

![Apply changes and finish merging](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/save-changes-finish-merging.png)

However, as we can see from the error highlighting this doesn't look quite right. The reason here is that both branches have added fields to this record, so we need to fix the brackets.

![Errors in the middle pane](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/errors-middle-pane-fixed.png)

The middle pane is a fully-functional editor, so you can edit it here.

![Fixed the errors in the middle pane](/images/posts/2022/09/resolving-git-merge-conflicts-in-intellij-idea/errors-middle-pane-fixed.png)

Once you've done that, you can click **Apply** to finish resolving your merge conflicts!

* In depth video: [IntelliJ IDEA YouTube channel - Resolving Git Merge Conflicts](https://www.youtube.com/watch?v=WgipWkaU2MM "IntelliJ IDEA YouTube channel - Resolving Git Merge Conflicts")
* In tutorial format: [IntelliJ IDEA Guide - Resolving Git Merge Conflicts](https://www.jetbrains.com/idea/guide/tutorials/resolving-git-merge-conflicts/)
