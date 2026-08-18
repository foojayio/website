---
title: "lntelliJ IDEA: Selectively Commit Changes to a File"
date: "2023-12-21T06:15:09+00:00"
lastmod: "2023-12-21T06:15:10+00:00"
description: "Selectively commit changes to a file using the Git integration in IntelliJ IDEA. Split unrelated changes into separate commits."
canonical: "https://maritvandijk.com/git-selectively-commit/"
authors:
  - "marit-van-dijk"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "IntelliJ IDEA"
  - "Tools"
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-ways-to-improve-your-code-reading-skills"
  - "foojay-podcast-26"
frozen: false
---

**Sometimes you're making multiple changes to a file that you don't want to commit together. For example, if you're working on a new feature, but notice some other small things you want to fix. If these changes are in separate files, we can commit each file separately. But what if they're in the same file?**

In [IntelliJ IDEA](https://www.jetbrains.com/idea/) (as of version [2023.3](https://blog.jetbrains.com/idea/2023/12/intellij-idea-2023-3/)), we can now select which chunks and specific lines we want to add to our commit.

We can see which files were changed by opening the **Commit tool window** (**⌘0** on macOS, or **Alt+0** on Windows/Linux). Here we can open the diff for a particular file to see which changes were made to that file, using **⌘D** (macOS) / **Ctrl+D** (Windows/Linux).

{{< img src="Screenshot-2023-12-14-at-11.45.51-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file. There are multiple checkboxes in the gutter of the changed file for different changes to the file." width="700" height="394" >}}

In the diff, we have the option to include specific changes to our commit, by clicking the **Include into commit** checkbox in the gutter next to each chunk of modified, deleted or newly added code.

{{< img src="Screenshot-2023-12-14-at-11.46.44-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file and checkboxes in the gutter of the changed file. A tooltip on a checkbox shows 'Include into commit'." width="700" height="394" >}}

We can even select specific lines from a change to include in a commit. To commit only a specific line from a chunk, right-click the line you want to include and select **Split Chunk and Include Current Line into Commit**.

{{< img src="Screenshot-2023-12-14-at-11.47.08-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file with a checkbox in the gutter of the changed file. The context menu shows the option 'Split Chunk and Include Current Line into Commit' highlighted." width="700" height="394" >}}

Alternatively, hover over the gutter and select the checkbox next to the line you want to include in the commit. Or, if we change our mind, we can also hover over the gutter and clear the checkbox next to the line we want to exclude.

{{< img src="Screenshot-2023-12-14-at-11.47.20-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file. There are multiple checkboxes in the gutter of the changed file. A tooltip on one of the checkboxes shows 'Include into commit'." width="700" height="394" >}}

Once we have selected all the changes we want to commit, we write a meaningful commit message, and select **Commit**. Any unselected changes will stay in the current change list, so that you can commit them separately later.

What if we don't want to add these changes to the same pull request, not even in a separate commit? Maybe you want to do some more cleaning up in your code base, and create a separate pull request for those changes later. We can undo this commit and move these changes to a different change list. To do so, select **Move to Another Changelist** from the context menu of a modified chunk.

{{< img src="Screenshot-2023-12-14-at-11.47.37-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file with a checkbox in the gutter of the changed file and a context menu with the option 'Move Lines to Another Changelist' highlighted." width="700" height="394" >}}

Next, we can name our new changelist. The changes will be assigned to this changelist and we can see it in the **Commit tool window**.

{{< img src="Screenshot-2023-12-14-at-11.48.04-700x394.png" class="size-medium" alt="IntelliJ IDEA showing a diff in a file with a popup on top. The popup is titled Move Lines to Another Changelist and the new changelist is named 'Fixes'." width="700" height="394" >}}

{{< youtube AW5Xv8n3iEo >}}

## Links

* (documentation) [IntelliJ IDEA - Select chunks and specific lines you want to commit](https://www.jetbrains.com/help/idea/2023.3/commit-and-push-changes.html#select_chunks_in_commit_changes_dialog)
* (documentation) [IntelliJ IDEA - New Changelist dialog](https://www.jetbrains.com/help/idea/2023.3/new-changelist-dialog.html)
* (documentation) [IntelliJ IDEA - Git](https://www.jetbrains.com/help/idea/2023.3/using-git-integration.html)
