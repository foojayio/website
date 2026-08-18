---
title: "Working with GitLab Merge Requests in IntelliJ IDEA"
date: "2023-08-10T09:57:10+00:00"
lastmod: "2023-08-10T09:57:12+00:00"
description: "IntelliJ IDEA supports working with GitLab Merge Requests. See Merge Requests, perform code reviews and merge them from inside IntelliJ IDEA."
canonical: "https://maritvandijk.com/gitlab-merge-requests/"
authors:
  - "marit-van-dijk"
image: "logo-1.png"
categories:
  - "GitLab"
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
frozen: false
---

**In this tutorial, we will take a look at working with GitLab Merge Requests inside IntelliJ IDEA.**

When reviewing Merge Requests in a web interface, we don't have the same support that our IDE gives us, like syntax highlighting and the context within which the code was written.

IntelliJ IDEA has support for reviewing and merging GitLab Merge Requests. These features are available if the IntelliJ IDEA project has a remote that points to GitLab.

![Remote](remote.png)

We can open the GitLab Merge Requests using the **GitLab** logo on the left, or from the menu by going to **Git \> Gitlab \> Show Gitlab Merge Request**.

![Open Merge Requests from logo](logo.png)

![Open Show GitLab Merge Requests from menu](menu.png)

If we aren't already logged in to GitLab via IntelliJ IDEA, the GitLab Merge Requests tool window will prompt us to **Log In**.

![Log In](login.png)

We can log into GitLab with a token. When we click **Generate** , this will take us to the GitLab page where a token with the right scopes can be generated for us. Click the button **Create personal access token** to create the token. When the token is generated, click the button to copy the token.

![Generate a token](generate.png)

![Create Personal Access Token](personal-access-token.png)

![Copy the token](copied.png)

We can copy the token into the **Token** field in the popup and click **Log In**.

![Paste the token into the Token field](token.png)

Alternatively, we can use an existing token, as long as it has the required scopes.

We can also log in to our GitLab account in the **Settings** . Open the Settings (**⌘,** on macOS \| **Ctrl+Alt+S** on Windows/Linux) and go to **Version Control \> GitLab**.

Click **Add Account** (**⌘N** on macOS \| **Alt+Insert** on Windows/Linux) to add an account if there is no account logged in, or click the + button at the top left to add an account. In the popup, add the **Server** if needed, paste the token from GitLab into the **Token** field in the popup and click **Log In**.

![Add GitLab account in Settings](add-account.png)

![Paste the token into the Token field in Settings](settings-token.png)

![Account added in Settings](settings-account.png)

Once we're logged in, the GitLab Merge Requests tool window will show all open Merge Requests for the GitLab repository.

![GitLab Merge Requests tool window](mr-tool-window.png)

We can change the search criteria to look for specific Merge Requests. We can use predefined filters, or search for something more specific.

![Filter](filter.png)

![Search](search.png)

If the Merge Request has assignees and/or reviewers assigned, we can see them here.

![Assignee](assignee.png)

![Reviewer](reviewer.png)

We can see the details of a specific Merge Request by double-clicking on it.

![Merge Request details](mr-details.png)

We can go back to the list of search results by clicking the project name in the top left of the Merge Requests tool window. We can see that the Merge Request we just looked at still has a tab open here, in case we want to go back.

:back:

Usually, you'll want to look for open Merge Requests, since these are the ones that need attention. Let's double-click, or press **Enter**, on one of these Merge Requests.

![Open Merge Requests](open.png)

If the Merge Request does not have a reviewer assigned, we can assign one from the Merge Request tool window in IntelliJ IDEA.

Click the three dots at the bottom left of the Merge Request details and select the action **Request Review**. From the list that pops up, select the reviewer(s) to assign this Merge Request to.

![Request Review](request-review.png)

![Assign reviewer](assign-reviewer.png)

We can see the files that have been changed in the Merge Request in the bottom left window, and the timeline in the main editor pane.

![Changed files and timeline](changed-files.png)

We can also select a file to see the diff as well as the comments for that file. We can go back to the timeline by clicking **View Timeline**.

The timeline shows the same information as the GitLab Merge Request activity page. The timeline shows activity on the Merge Request, including comments and other status changes in the main editor pane. If there are inline comments on the code, we can see them here.

It also shows the result of any checks that were run. We can go to the details to see which checks failed, and even click them to go straight to the failing check on GitLab, so we can look at the details.

![Checks failed](checks.png)

We can open the Merge Request in our browser in several ways. We can right-click the Merge Request number and select **Open in Browser**.

Alternatively, we can open the Merge Request in our browser by right-clicking it, either when it's already open, or in the list of Merge Requests and selecting **Open Merge Request in Browser**.

![Open on GitLab](open-on-gitlab.png)

![Open in browser](open-in-browser.png)

IntelliJ IDEA also shows if there are any conflicts, so we know if this request is safe to merge or not.

![Conflicts](conflicts.png)

We can open any of the files that make up the Merge Request, and IntelliJ IDEA will show them in the diff viewer, so we can have a closer look at the changes that make up the Merge Request. Inline comments will be displayed in the diff view too.

Alternatively, we can open the diff view by selecting the file and using the shortcut for **Show Diff** (**⌘D** on macOS \| **Ctrl+D** on Windows/Linux).

![Show diff](diff.png)

If the Merge Request contains changes to multiple files, we can navigate between those files.

We can do so using the arrows at the top of the diff viewer, or using the shortcuts to **Compare Next File** (**⌃⇧→** on macOS \| **Alt+→** on Windows/Linux) or **Compare Previous File** (**⌃⇧←** on macOS \|**Alt+←** on Windows/Linux).

![Navigate between files](navigate-between-files.png)

If the Merge Request consists of multiple commits, we can see the individual commits in the **Changes from** dropdown list.

We can look at the changes for an individual commit if we want.

![Multiple commits](commits.png)

We can also see the number of comments for the Merge Request, and for each file.

We can **Hide All Windows** (**⇧⌘F12** on macOS \| **Ctrl+Shift+F12** on Windows/Linux) to focus on the diff. We can **Restore Windows** using the same shortcut.

Alternatively, we can reopen the Merge Requests window by opening the Recent Files dialog (**⌘ E** on macOS \| **Ctrl+E** on Windows/Linux), which also lets us open up any of the Tool Windows. Here we can search for "merge" to find the Merge Request tool window.

![Recent files](recent-files.png)

Let's look at comments on our Merge Request.

We can add comments to the GitLab conversation. Comments on this page are comments that apply to the Merge Request as a whole, and not a specific piece of code.

We can see these comments inside IntelliJ IDEA too.

We might need to refresh our Merge Request window, either using the shortcut to **Refresh Reviews** (**⌘R** on macOS \| **Ctrl+F5** on Windows/Linux) or by right-clicking the Merge Request window and selecting **Refresh Merge Request**.

![Comment from GitLab](comment-from-gitlab.png)

![Refresh reviews](refresh-reviews.png)

We can also add comments from inside IntelliJ IDEA. We can place high-level comments, like the comment we just placed from GitLab.

![Comment from IntelliJ IDEA](comment-from-intellij-idea.png)

We might also want to add comments on particular parts of the code. We can do so from inside IntelliJ IDEA by clicking on the plus in the gutter of the diff viewer. For example, let's add a comment to the current Merge Request.

![Inline comment](inline-comment.png)

We can edit or delete comments. We can also reply to comments or resolve a comment that is no longer relevant, for example, if the code has been updated in line with the comment.

![Working with comments](edit.png)

From the diff viewer, we can submit our review and approve the Merge Request if we think it's ready to be merged.

![Submit Review and Approve](approve.png)

One feature that's really helpful when we're reviewing a Merge Request is that we can check out the code that is in the Merge Request. In this project, I'm currently on the `main` branch. Let's check out the branch for this Merge Request.

![Check out branch locally](checkout.png)

With the branch checked out locally, we can navigate the code related to this Merge Request. This lets us not only look at the code, but also make sure that the project still builds and tests pass. We can potentially make changes too.

![Build success](build.png)

Finally, let's look at how to finish off the Merge Request.

If a Merge Request is no longer relevant, and we're not going to take the changes that went into the Merge Request, we can close it. We can do so by clicking the three dots at the bottom left of the Merge Request tool window and selecting **Close Merge Request**.

![Close](close.png)

If the Merge Request is ready, meaning all the checks have passed, questions have been answered and requested changes to the code have been made, we can merge it. We can do so by clicking the **Merge** button at the bottom left of the Merge Request window.

We also have the option to **Squash and Merge**, meaning all commits will be combined into a single commit when merging. We can add our own commit message for the merge, or edit the default one if we want to.

![Merge](merge.png)

![Edit commit message](commit-message.png)

The Merge Request will be merged, and if we do a **Fetch** we can see the updated branches for this repository.

![Fetch](fetch.png)

![Updated branches](updated-branches.png)

We can see in our browser that the Merge Request has been closed.

![Merged](merged.png)

As we've seen, we can work with GitLab Merge Requests right inside IntelliJ IDEA.

We can see all Merge Requests on a project, filter them or search for a specific Merge Request, perform a code review and add comments, see whether checks have passed and there are no merge conflicts, checkout the branch to run it locally, submit our review and approve the Merge Request, and merge (or close) the Merge Request all from inside our IDE.

### IntelliJ IDEA Shortcuts Used

Here are the IntelliJ IDEA shortcuts that we used.

|                Name                | macOS Shortcut | Windows / Linux Shortcut |
|------------------------------------|----------------|--------------------------|
| Open Settings                      | **⌘,**         | **Ctrl+Alt+S**           |
| Add Account                        | **⌘N**         | **Alt+Insert**           |
| Show Diff                          | **⌘D**         | **Ctrl+D**               |
| Compare Next File                  | **⌃⇧→**        | **Alt+→**                |
| Compare Previous File              | **⌃⇧←**        | **Alt+←**                |
| Hide all windows / Restore windows | **⇧⌘F12**      | **Ctrl+Shift+F12**       |
| Recent files                       | **⌘E**         | **Ctrl+E**               |
| Refresh reviews                    | **⌘R**         | **Ctrl+F5**              |

### Related Links

* [(video) JetBrains - IntelliJ IDEA: GitLab Merge Request](https://youtu.be/I_k9v9bBaCA)
* [(documentation) JetBrains IntelliJ IDEA -- GitLab](https://www.jetbrains.com/help/idea/gitlab.html)
* [(blog) JetBrains Company blog - GitLab Support in JetBrains IDEs](https://blog.jetbrains.com/blog/2023/07/26/gitlab-support-in-jetbrains-ide/)

{{< youtube I_k9v9bBaCA >}}
