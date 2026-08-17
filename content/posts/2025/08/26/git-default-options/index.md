---
title: "Git default options"
slug: "git-default-options"
date: "2025-08-26T11:59:57+00:00"
lastmod: "2025-08-26T12:01:52+00:00"
description: "In this post, we describe the default of two of the most common Git commands. I hope it sheds some light on them."
canonical: "https://blog.frankel.ch/git-default-options/"
authors:
  - "nicolas-frankel"
image: "tree-3097419.jpg"
categories:
  - "Developer Tools"
tags:
related_posts:
  - "a-list-of-cache-providers"
  - "poor-mans-api"
  - "kubernetes-gateway-api"
  - "real-world-stream-collector"
enlighterjs: true
frozen: false
---

Git has become a fundamental part of our developers' daily routine that it's hard to remember our lives without it. And yet, most of us use a limited set of commands **and** options. Today, I want to focus on two commands most developers probably use **every** day and look at the defaults behind them.

git push {#h2-0-git-push}
-------------------------

After `git commit`, `git push` is probably the second most used command. I don't think I'll teach you anything with this excerpt from the documentation:
> git-push - Update remote refs along with associated objects
>
> -- [Git documentation](https://git-scm.com/docs/git-push)

The default options pushes the *current branch* to the `origin` remote; it assumes a branch of the same name as the local branch exists on the latter. If it doesn't exist, you need to use `-u` option to create it.

Also, the default assumes a *single* upstream named `origin`. If you want to push the current branch to another upstream, you need to specify it explicitly:

```bash
git push other_upstream
```


Likewise, the default assumes pushing to a remote branch with the same name. To push to another branch, we also must specify it along with the upstream.

```bash
git checkout my_branch
git push other_upstream master
```


git rebase {#h2-1-git-rebase}
-----------------------------

The default `git push` options are straightforward. The default ones for `git rebase` aren't the reason why we probably use one of them all the time.
> git-rebase - Reapply commits on top of another base tip
>
> -- [Git documentation](https://git-scm.com/docs/git-rebase)

Let's try the `git rebase` command on a simple tree:

```
A   B   C   D
o---o---o---o master
     \
      E   F   G
      o---o---o branch1 [HEAD]
```


Nothing happens. Or more precisely, "it depends"™. If we didn't set an `origin` remote, `git` complains.

```
There is no tracking information for the current branch.
Please specify which branch you want to rebase against.
```


Imagine the following remote repo:

```
A   B   C   D
o---o---o---o master
     \
      E   F   G   H
      o---o---o---o branch1 [HEAD]
```


Let's configure the remote and bind the local branch to the remote branch.

```bash
git fetch
git branch --set-upstream-to=origin/branch1
```


If we call `rebase` again, git tries to apply every commit from the **remote** branch, starting from the root one. Since `H` doesn't exist on the local branch, it just adds it to the tip of it.

Let's try with `master` and test again:

```bash
git rebase -i master
```


`-i` allows rebasing interactively.  

Here's the proposal.

```
pick 5529dc4 E
pick 93af602 F
pick 7f79811 G
pick c6f853b H
```


As per the documentation, the command switched branch, got the commits from `master`, and now applies the commits in the current branch.

The result is the following:

```
A   B   C   D
o---o---o---o master
             \
              E   F   G   H
              o---o---o---o branch1 [HEAD]
```


Git is a huge beast. Most developers, including me, only use a fraction of its features. In this post, we described the default of two of the most common Git commands. I hope it sheds some light on them.

**To go further:**

* [git-push](https://git-scm.com/docs/git-push/)
* [git-rebase](https://git-scm.com/docs/git-rebase)
* [The multiple usages of git rebase --onto](https://blog.frankel.ch/multiple-usages-git-rebase-onto/)
* [Why does git rebase with no arguments work the way that it does?](https://stackoverflow.com/questions/50643026/why-does-git-rebase-with-no-arguments-work-the-way-that-it-does)
* [What does command 'git rebase' mean when no arguments followed?](https://superuser.com/questions/788912/what-does-command-git-rebase-mean-when-no-arguments-followed)



*Originally published at [A Java Geek](https://blog.frankel.ch/git-default-options/) on July 27^th^, 2025*
