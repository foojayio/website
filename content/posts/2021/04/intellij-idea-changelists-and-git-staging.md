---
title: "About IntelliJ IDEA Changelists and Git Staging"
slug: "intellij-idea-changelists-and-git-staging"
date: "2021-04-16T07:13:37+00:00"
lastmod: "2021-04-16T07:18:31+00:00"
description: "Did you know? Since the release of IntelliJ IDEA 2020.3, there are now two ways to manage your commits to Git."
authors:
  - "helenjoscott"
image: "/images/posts/2021/04/intellij-idea-changelists-and-git-staging/intellij-idea-default-changelist-nothing-selected.png"
categories:
  - "IntelliJ IDEA"
tags:
related_posts:
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "foojay-podcast-91"
  - "lntellij-idea-selectively-commit-changes-to-a-file"
  - "using-git-interactive-rebase"
frozen: false
---

Since the release of IntelliJ IDEA 2020.3, there are now two ways to manage your commits to Git. The first one is through existing functionality with [IntelliJ IDEA changelists](https://www.jetbrains.com/help/idea/managing-changelists.html), the second is through support for [Git staging](https://www.jetbrains.com/help/idea/commit-and-push-changes.html#use-git-staging-area-to-commit-changes). This blog will take you through both approaches and highlight the differences. There is no right or wrong answer, it's a case of choosing the method that works best for you, or that you're most familiar with.

Before we get started, it's important to know that both IntelliJ IDEA Changelists and Git staging allow you to commit part of a file, a whole file or multiple files to Git. How they achieve that is slightly different, but the end result is the same. IntelliJ IDEA Changelists work on the notion of a range in your content. All changes in that range will form part of the commit. Git staging uses the git-native notion of a staging area (also known as 'index').

Both IntelliJ IDEA Changelists and Git staging are accessible from the **Commit** tool window. From IntelliJ IDEA 2020.1 you can [switch to use this non-modal commit window](https://www.jetbrains.com/help/idea/commit-changes-dialog.html?keymap=primary_windows) with **⌘,** on macOS, or **Ctrl** +**Alt** +**S** to display the Preferences/Settings. From there type in *commit* and select the 'Use non-model commit interface' checkbox. You can also use **⌘⇧A** (macOS), or **Ctrl** +**Shift** +**A** (Windows/Linux) for *Find Actions* and type in *Commit* . You can then access this with **⌘0** on macOS, or **Alt** +**0** on Windows and Linux.

Finally, before we get started with the comparison, you can't use IntelliJ IDEA Changelists and the Git staging area at the same time. You can switch between them, but not use them simultaneously. Let's start with IntelliJ IDEA Changelists.

What is an 'IntelliJ IDEA Changelist'? {#h2-0-what-is-an-intellij-idea-changelist}
----------------------------------------------------------------------------------

Prior to the release of 2020.3, IntelliJ IDEA Changelists were the only way to commit changes to your Git repository.

IntelliJ IDEA Changelists are not just for Git, they're for any supported VCS system, but in this blog we are going to focus on Git, so we can compare them to the new Git staging functionality.

### Committing a Whole File {#h3-1-committing-a-whole-file}

IntelliJ IDEA always gives you a *Default Changelist* and, when you change a file, the file will show in the *Default Changelist* with a checkbox that isn't selected:

![Default Changelist - no files selected](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/intellij-idea-default-changelist-nothing-selected.png)

From there, if you want to select the whole file to be committed, you can select the checkbox to the left of the filename:

![Default Changelist - one file selected](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/intellij-idea-default-changelist-one-selected.png)

This means that when you commit your files to Git, all the changes in the file that you selected will be added and then committed to git. IntelliJ IDEA runs both the `git add` and the `git commit` command for you.

### Committing Part of a File {#h3-2-committing-part-of-a-file}

Alternatively, if you want to commit some, but not all, of that file, you can double-click the file from the *Default Changelist* to open the *diff* view. This view shows the last known version that Git has on the left and then your local copy on the right in blue, which you can edit. You can select the checkboxes adjacent to each code change you have made on the right to specifically say that you only want to commit changes in that range, not the whole file.

![Default changelist - partial file selected](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/intellij-idea-default-changelist-partial-commit-1.png)

Note that if you only select part of the file to be committed as in this example, the *Default Changelist* will indicate that the file will be partially committed with a line through the checkbox adjacent to the filename in the Default Changelist as you can see above.

Tip: You can also use **⌘D** or **Ctrl** +**D** to open up the *diff* view from the Commit tool window.

### Reverting Changes {#h3-3-reverting-changes}

From the *diff* view you can also revert changes individually by using the `>>` arrow on the file on the left-hand side to revert the changes to your local file. In this case, the right-hand side will update to reflect your changes. If you make a mistake, you can undo it or use [Local History](https://www.jetbrains.com/help/idea/local-history.html) to get your changes back.

![Default Changelist - revert file](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/intellij-idea-default-changelist-revert-staging.png)

You can also rollback the whole file by right-clicking on it and selecting **Rollback**.

### Multiple Changelists {#h3-4-multiple-changelists}

The *Default Changelist* in IntelliJ IDEA is only part of the story. IntelliJ IDEA supports [multiple changelists](https://www.jetbrains.com/help/idea/managing-changelists.html#new_changelist). Let's say you have changes that should not be committed, you can move those into a separate changelist. Changelists in IntelliJ IDEA are used to group changes. You can only commit one Changelist at a time. So, to build groups of changes up at the same time, you need to create multiple Changelists. It's helpful to name your Changelists according to the feature or bug that they apply to. This helps you to keep track of how you should split your commits up into your Changelists.

To create multiple Changelists you can right-click on the *Default Changelist* and select 'New Changelist':

![Image of new Changelist](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/new-changelist.png)

### Moving Changes Between Changelists {#h3-5-moving-changes-between-changelists}

You can also move files between Changelists. You can move a whole file to a new Changelist and create that new Changelist if it doesn't already exist as part of that action. To do that, you can right-click on the file in the Changelist in the Commit window and select ***Move to Another Changelist***. You will be prompted to select the changelist if it exists, or create the Changelist if it doesn't. When you create a new Changelist, you can optionally set it to 'Active'. This means that all future changes will be assigned to that Changelist.

{{< youtube yvW-6Evx50Y >}}

<br />

In this example, all future changes will now be assigned to our *DateFormat* changelist because we set that one to be the 'Active' one when we created it.

There is an additional checkbox on the New Changelist dialog called **Track context** . If you are working with [tasks and contexts](https://www.jetbrains.com/help/idea/managing-tasks-and-context.html) and have connected IntelliJ IDEA with an appropriate 3rd party application, you can [use this checkbox](https://www.jetbrains.com/help/idea/new-changelist-dialog.html) to get IntelliJ IDEA to [manage the task context](https://www.jetbrains.com/help/idea/new-changelist-dialog.html).

As well as moving whole files between Changelists, you can also move single changes within a file between Changelists. This is especially useful if you're working on changes that impact one file, but you want to split the commits up for the reasons we talked about earlier. To assign some, but not all changes in a file to a different Changelist, right-click on the change from the *diff* view in **Your version** on the right-hand side. You can then select 'Move to another changelist'. You can also use **⇧⌘M** on macOS, or **Alt** +**Shift** +**M** on Windows and Linux.

{{< youtube 2vIOyoSZJsE >}}

<br />

You can also move changes between Changelists using the coloured bars in the gutter:  
![Move changes between changelists](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/move-between-changelists.png)

### Making a Changelist Active {#h3-6-making-a-changelist-active}

Lastly, we've already shown you how to set a new Changelist to be the active one, but it's worth mentioning that IntelliJ IDEA needs to know which Changelist to use for new changes; you always need one Changelist that is the active one. You can choose which Changelist is the active one when you have more than one by right-clicking on the Changelist and selecting **Set Active Changelist**. If you don't see this option, it's because the Changelist you have selected is already the active Changelist.

![Set Active IntelliJ IDEA Changelist](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/set-active-changelist.png)

When you commit any new changes, they will be placed in your *Active* IntelliJ IDEA Changelist by default.

### Committing Your Changes to Git {#h3-7-committing-your-changes-to-git}

Now that you have your IntelliJ IDEA Changelists created, and your changes split up how you want them, you're ready to commit them to Git. You can see what will be committed to Git from the Commit window.

When you're happy with that you can use **⌘K** on macOS, or **Ctrl** +**K** on Windows and Linux to open the Commit window. Before you press Commit, you need to enter a commit message. After you've done that and press commit to [commit them](https://www.jetbrains.com/help/idea/commit-dialog.html) to your Git history locally, IntelliJ IDEA runs two git commands:  
`git add`  
`git commit`

This adds the files and commits them in one step into your local Git history. You can then go ahead and [push the changes](https://www.jetbrains.com/help/idea/push-dialog-mercurial-git.html#Push_Dialog_(Mercurial_Git)-6-chapter) to your remote repository if you want to.

What is 'Git Staging'? {#h2-8-what-is-git-staging}
--------------------------------------------------

IntelliJ IDEA 2020.3 introduced support for [Git staging](https://git-scm.com/docs/git-add). It is not enabled by default, but the fastest way to enable it is with Find Actions (**⇧⌘A** on macOS, or **Ctrl** +**Shift** +**A** on Windows and Linux) and type in *git staging*. Git staging is the git-native way of committing file diffs to a git repository.

You can then turn it on:

![Enable git staging](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/enable-git-staging-1.png)

You can also enable Git staging from the Settings/Preferences dialog with **⌘,** on macOS, or **Ctrl** +**Alt** +**S** to display and then type in *git staging*.

With Git staging, this process is managed in two steps. When you *stage* a file, or part of a file, IntelliJ IDEA runs the `git add` command for the changes. When you subsequently commit those files, or some diffs of a file, IntelliJ IDEA runs the `git commit` command. The end result is the same, however with Git staging you can take advantage of adding your changes before you commit them if you're more familiar with that model.

### Staged and Unstaged Folders {#h3-9-staged-and-unstaged-folders}

With Git Staging you'll see two folders as opposed to the one IntelliJ IDEA Changelist in the Commit window. Each change you make to your file is represented by a diff between your local file and HEAD. The *Unstaged* folder is where all your diffs will appear initially. To include a diff in your next commit, you need to move it from the *Unstaged* folder to the *Staged* folder. The action of doing this means IntelliJ IDEA performs a `git add` command for all the diffs in the file.

### Unstaged Folder - Staging files {#h3-10-unstaged-folder-staging-files}

When you make a change to a file it will appear in your *Unstaged* folder initially as a diff that you can stage. You can stage the whole file by dragging it up to the Staged folder, by using the ***+*** icon in the tree, or right-click and select ***+Stage*** . IntelliJ IDEA will run a `git add` command when you do this:

![Staging a whole file](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/stage-whole-file.png)

`Git add` modifies the file content in the index (the staged content). If you subsequently modify content inside that same range, it will not be committed until it's staged. You can use the staging area to change what is going to be committed independently of any modifications to the local files. With IntelliJ Changelists, any change inside of that same range will be committed.

### Unstaged Folder - Staging Part of a File {#h3-11-unstaged-folder-staging-part-of-a-file}

Alternatively, you can add part of a file by double-clicking on the file from the *Unstaged* folder and using the `<>` arrows on the Staged version on the left-hand side:

![Reverting part of a file](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/reverting-changes.png)

You can also rollback a whole file by selecting it from the *Unstaged* area and selecting **Rollback** (or **Revert** prior to version 2021.1). This rollback the file to the state in the index, not the HEAD.

Let's take a look at the functionality in the *Staged* area. Changes in the *Staged* area means that IntelliJ IDEA has performed a `git add` for the change in the file.

### Staged Folder - Unstaging a File {#h3-12-staged-folder-unstaging-a-file}

You may change your mind and decide you want to unstage a whole file that you've previously staged. As with staging a file, you can either drag it from the *Staged* folder into the *Unstaged* folder, click the \***-\*\*** icon in the tree, or right-click and select 'Unstage':

![Unstage a file](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/unstage-a-whole-file.png)

When you unstage a file, or part of a file, IntelliJ IDEA runs a `git reset` for the change.

### Staged Folder - Unstaging Part of a File {#h3-13-staged-folder-unstaging-part-of-a-file}

You can also unstage a change in a file if you need to. To do that, you need to open the file that contains the change that you want to unstage from the *Staged* folder *diff* view. You can then use the `>>` arrows from the staged portion on the left-hand side to *Unstage* the change. This will unstage that specific change and again, IntelliJ IDEA will run a Git command to reflect the change. You can also type into the editor if you prefer.

![Unstage part of a file](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/unstage-part-file.png)

### Staging or Unstaging Individual Changes Using the Gutter Icons {#h3-14-staging-or-unstaging-individual-changes-using-the-gutter-icons}

You can also use the gutter icons in IntelliJ IDEA to stage individual changes.

When you click on the solid coloured bar you can select to *Stage* your changes, the bar will change to an outline when you stage them. Clicking on an outline bar in the gutter will give you the option to *Unstage* those changes.

![Stage from the Gutter](/images/posts/2021/04/intellij-idea-changelists-and-git-staging/stage-from-gutter.png)

### Committing Changes {#h3-15-committing-changes}

With Git Staging enabled, your commit will be made up of the changes (diffs) you have in your *Staged* folder. When you're happy with that you can use **⌘K** (macOS), or **Ctrl** +**K** (Windows/Linux) to load the Commit window. You can then enter a commit message and press [commit](https://www.jetbrains.com/help/idea/commit-dialog.html) to commit your selected changes to Git. Alternatively, you can use ***⌘⏎*** (macOS), or **Ctrl** +**Enter** (Windows/Linux) to commit the changes. IntelliJ IDEA will then run the required Git commands for the changes that you selected.

You can then go ahead and [push the changes](https://www.jetbrains.com/help/idea/push-dialog-mercurial-git.html#Push_Dialog_(Mercurial_Git)-6-chapter) to your remote repository if you want to.

Summary {#h2-16-summary}
------------------------

It's completely up to you which approach you prefer. They both achieve the same result in slightly different ways.

* Changelists focus on ranges in your content, meaning whatever changes are in that range in your file are included in the commit.
* Git Staging focuses on creating a snapshot commit in the staging area. Content that has been staged is effectively locked. You can proceed to edit the file even if it's been staged; the staged content will not change. This allows you to prepare commits in an incremental fashion.
