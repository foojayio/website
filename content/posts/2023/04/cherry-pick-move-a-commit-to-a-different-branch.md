---
title: "Cherry-Pick: Move a Commit to a Different Branch"
slug: "cherry-pick-move-a-commit-to-a-different-branch"
date: "2023-04-05T13:53:20+00:00"
lastmod: "2023-08-03T08:27:44+00:00"
description: "Moving a commit to a different branch: not nearly as scary as it sounds! Let the IDE help to turn this into a quick, low-stress task."
canonical: "https://maritvandijk.com/cherry-pick-move-a-commit-to-a-different-branch/"
authors:
  - "marit-van-dijk"
image: "https://foojay.io/wp-content/uploads/2023/03/GoBackToMain-1024x575-1.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

There are several reasons why you might want to move a commit to a different branch.

Let's take a look at some of them.

Committed to the wrong branch {#h2-0-committed-to-the-wrong-branch}
-------------------------------------------------------------------

You're working on a new feature, but an urgent bug came in.

You fixed the bug and committed the fix, but oops... you forgot to create a new branch for the bugfix!

Now this bugfix is on the wrong branch. How do we fix this?
![IntelliJ IDEA Git log window showing a bugfix commit on a new-feature branch](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/CommitOnWrongBranch-1024x575.png) Bugfix commit is on the wrong branch

Use cherry-pick to move the commit {#h2-1-use-cherry-pick-to-move-the-commit}
-----------------------------------------------------------------------------

I could redo the work, especially if it's a small change, but ... I don't want to! Luckily, there is a better way.

We only want to move this one commit from the feature branch to a separate bugfix branch. We can do this using Git's "cherry pick" option from [IntelliJ IDEA](https://www.jetbrains.com/idea/).

First, let's go back to main and create the bugfix branch that we should have created in the first place.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/GoBackToMain-1024x575.png) Back on the main branch

Once we're back on the main branch, we can create a new branch named "bugfix".
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/CreateBugfixBranch-1024x575.png) Create a new Bugfix branch

On the newly created branch, we can select the bugfix commit from the other branch and select **Cherry-Pick** to apply that commit to our current branch.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/CherryPickFromMenu-1024x575.png) Cherry Pick the selected commit from the context menu

### Cherry-pick from the command line {#h3-2-cherry-pick-from-the-command-line}

Yes, we can do this from the command linetoo, but there's no cute cherry icon on the command line. To cherry-pick a commit from the command line, we can use the command `git cherry-pick <commit hash>`. We would need to find the commit hash of the commit we want to cherry-pick, which we can find for example in the Commit Details pane in the Git log window (see below).
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/CommandLine-1024x574.png) Cherry-pick on the command line

As we can see, the bugfix commit is now on the bugfix branch.
![IntelliJ IDEA Git log showing the Bugfix commit on the bugfix branch and a message "Cherry-pick successful".](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/BugfixCommitOnBugfixBranch-1024x575.png) Cherry-pick successful

Other use cases for cherry-picking {#h2-3-other-use-cases-for-cherry-picking}
-----------------------------------------------------------------------------

Cherry picking can be useful in other situations too. Let's take a look at some other use cases for cherry-picking.

### Backporting a fix {#h3-4-backporting-a-fix}

We can also use cherry-picking to backport a fix to a previous release branch. For example, let's move our bugfix commit also to the v1-release.

To do so, first we need to look for the last release (v1). We can search for a specific commit hash, branch or tag name in the Git log (**⌘ F** on Mac or **Ctrl+F** on Windows/Linux).
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/SearchForHashBranchTag-1024x574.png) Search Git log for Hash/Branch/Tag

We can also filter commits in the commit log by branch, user, date or path.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/FilterByBranchUserEtc-1024x574.png) Filter by branch, user, date or path

To see which commits have not yet been applied to this branch, we can click **View Options** and select **Highlight \| Not Cherry-Picked Commits**. We'll compare with the new-feature branch. Commits that have already been applied to the current branch are greyed out.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/NotCherryPickedCommits-1024x574.png) Select the Not Cherry-Picked Commits

When we select a commit, we can look at the information in the Commit Details area (at the bottom right) to make sure these are the changes we want to transfer to this branch. In the Commit Details area we can see which files were changed in a particular commit. We can right-click a file and select **Show Diff**from the context menu to open the changes that were made to that file.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/CommitDetails-1024x574.png) Look at the details of a commit

If we are sure these are the changes we want, we can cherry-pick them to the previous release branch.

### Cherry pick part of a commit {#h3-5-cherry-pick-part-of-a-commit}

In the Commit details pane on the right, select the files containing the changes you want to apply to the target branch, right-click and select **Cherry-Pick Selected Changes** from the context menu.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/Cherry-PickSelectedChanges-1024x574.png) Cherry-Pick Selected Changes

The cherry picked changes are transferred to the change list and we can commit them from there.
![IntelliJ IDEA Commit window with Changes selected to be committed.](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/ChangeList-1024x574.png) Partial commit added to the Change List to be committed

Dealing with conflicts {#h2-6-dealing-with-conflicts}
-----------------------------------------------------

So far, cherry picking went smoothly because there are no conflicting changes. What if there are conflicts?!

When we cherry-pick a commit that has conflicts with our current branch, the Merge Conflicts dialog opens.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/MergeConflict-1024x574.png) Merge Conflict

We can resolve the merge conflicts here. We want to keep some changes, and reject others.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/MergeConflictDialog-1024x574.png) Merge Conflicts dialog ![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/MergeConflictResolved-1024x574.png) Merge conflicts have been resolved

If you're not able to resolve the merge conflicts, you can also abort the cherry pick.
![IntelliJ IDEA Git log showing a notification that the Cherry-pick was performed with conflicts and a popup to Abort the Cherry-Pick](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/Abort-1024x574.png) Abort Cherry-pick

Continue after cherry-picking {#h2-7-continue-after-cherry-picking}
-------------------------------------------------------------------

Once we're done cherry-picking, we can go back to the "feature" branch. Since we haven't pushed these changes yet, we can remove the commit from the feature branch by selecting **Drop commit**.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/DropCommit-1024x574.png) Drop Commit

What if you have pushed the changes already? Then you might want to revert it on this branch instead. Right-click the commit and from the context menu select **Revert Commit**.
![](/images/posts/2023/04/cherry-pick-move-a-commit-to-a-different-branch/RevertCommit-1024x574.png) Revert Commit

Now we can continue working on the new feature!

Conclusion {#h2-8-conclusion}
-----------------------------

Moving a commit to a different branch. Not nearly as scary as it sounds! Let the IDE help to turn this into a quick, low-stress task.

<figure class="wp-block-embed is-type-rich is-provider-embed-handler wp-block-embed-embed-handler wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <p>PRESERVEDHTMLBLOCKZZ0ZZEND</p>
 </div>
 <figcaption class="wp-element-caption">
  Cherry pick a commit to a different branch in any JetBrains IDE
 </figcaption>
</figure>

### Links {#h3-9-links}

* (code) <https://github.com/mlvandijk/git_tips>
* (JetBrains -- IntelliJ IDEA) [Cherry-pick separate commits](https://www.jetbrains.com/help/idea/apply-changes-from-one-branch-to-another.html#cherry-pick)
* (Git documentation) [Git cherry-pick](https://git-scm.com/docs/git-cherry-pick)
* (blog) [Marco Behler](https://www.marcobehler.com/): [Git: Merge, Cherry-Pick \& Rebase](https://www.marcobehler.com/guides/git-merge-rebase)
* (blog) [IntelliJ IDEA: Resolving Merge Conflicts in Git](https://maritvandijk.com/intellij-idea-resolving-merge-conflicts-in-git/)
* (blog) [Foojai.io](https://foojay.io/): [Resolving Git Merge Conflicts in IntelliJ IDEA](https://foojay.io/today/resolving-git-merge-conflicts-in-intellij-idea/)

[](https://maritvandijk.com/cherry-pick-a-commit-to-a-different-branch/)
