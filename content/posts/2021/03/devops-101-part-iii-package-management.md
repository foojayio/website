---
title: "DevOps 101 Part III: Package Management"
slug: "devops-101-part-iii-package-management"
date: "2021-03-24T07:35:47+00:00"
lastmod: "2021-03-24T07:35:50+00:00"
description: "DevOps has a ton of jargon in it, though. It’s like running into a brick wall. Here, I'll explain package managers!"
authors:
  - "kat-cosgrove"
image: "https://foojay.io/wp-content/uploads/2021/03/index.png"
categories:
  - "DevOps"
tags:
related_posts:
  - "devops-101-part-ii-container-registries"
  - "devops-101-part-i-ci-cd"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "enterprise-java-quality-gates-ai"
frozen: false
---

> This post was originally published by [Kat Costgove](https://dev.to/katcosgrove) at [Dev.to](https://dev.to/jfrog/devops-101-package-management-1i14).

When you're new to an industry, you encounter a lot of new concepts. This can make it really difficult to get your feet underneath you on an unfamiliar landscape, especially for junior engineers. What's all this jargon? What does DevOps really mean? What's all this software? Is DevOps a methodology, or a toolset? Is any of this actually going to make my life easier, or is it just a bunch of industry buzzwords?

A lot of the documentation out there assumes you already have additional context and experience, or are proficient in some related tooling, and that doesn't exactly make it easy to learn. DevOps has a ton of jargon in it, though. We're absolutely swimming in abbreviations and abstractions, and sometimes it's difficult to define a term satisfactorily without needing to define three more for context. It's like running into a brick wall.

Here, I'll explain package managers.

### What even is that? {#h3-0-what-even-is-that}

From Wikipedia, a package manager or package-management system is a collection of software tools that automates the process of installing, upgrading, configuring, and removing computer programs for a computer's operating system in a consistent manner.

If you're a developer, you have probably already used one of these. For instance, if you're writing Node.js, you're probably using NPM. That's a package manager. If you're using Linux and you've ever installed something with apt-get, APT is a package manager.

In plain English, a package manager is what handles the heavy lifting involved in installing something (including that thing's dependencies), validating that it is what it says it is (to an extent, by comparing checksums), keeping track of versions, upgrades, and uninstalling. This is accomplished in part through the use of a large amount of metadata, defining characteristics about a particular package, alongside the actual application binary.

Modern package managers are why you can reliably run `pip install Django=-1.3.3` or whatever and be pretty sure you are getting version 1.3.3 of Django, or just `pip install Django` and take whatever is the most recent version.

### The Before Times {#h3-1-the-before-times}

We didn't always have this, though. The earliest versions of what you could call a package manager are from the mid to late 90s, with Debian introducing dpkg in 1994 and RedHat's RPM in 1997. Before then, and until package management really took off, we had to do a lot of things manually and we had way less information about the things we were installing.

You would get a compressed directory, usually in the form of a .tar file. Decompress that, and you would find a readme file with instructions to follow. Some kind of config script would be there which, when run, would tell your C compiler what to do, where new binaries should go, what application dependencies to look for and where, etc. If anything went wrong, it would exit and you would have to go install some more dependencies.

If it found everything it needed and the config script completed, it would spit out a Makefile. Run the `make` command and, if everything compiles correctly, run `make install` to finally install the application. Updates were just as involved, if not more so. Obviously, this is a time-consuming process. Imagine having to do that for every piece of software on your computer, and every piece of software required to run all of it. A lot of things could go wrong on your journey from downloading a .tar file to actually getting the thing installed.

Understandably, everyone thought this was exhausting and the release of the first package managers for Linux was A Big Deal. It immediately changed computing and application development for the better, forever. Those early package managers pretty much just gave you install, update, and uninstall, but over time, package managers have bundled all of that work and more into simpler commands and encoded extra information alongside the application binary and its dependencies, like version numbers, checksums, dependency graphs, and more.

As the popularity of package managers for Linux grew, so did the demand for something similar for other languages. Thus, a whole series of package managers for other languages were born, from CPAN for Perl to Maven for Java to PyPi for Python to Cargo for Rust, all with the goal of making it easier to distribute and use software.

This introduced a whole new problem, though. Applications are easier to install, update, manage, and remove, so we start building more complex applications, and with Continuous Integration becoming popular in the 90s, we start releasing more often, too. One organization might be using multiple languages, and we also start caring about security. This is where binary repository managers enter the playing field.

### What's a binary repository manager? {#h3-2-what-s-a-binary-repository-manager}

A binary repository manager is there to manage all of those binaries we now have into a system of repositories. Some of them support just one or two package types for very specialized applications, but the one I'll talk about supports more. This isn't the same thing as source control, which is where your code lives, but more of an extension of it. While your code might live in a repository on GitHub or BitBucket or whatever, the result of that code being built or compiled -- your artifacts -- live in a binary repository manager like [JFrog Artifactory](https://jfrog.com/artifactory/). For DevOps to really work, this is an important part of the equation. We're delivering updates far more often now, and we need a way to organize our build artifacts in a sensible way so they're easier for our other tools, like our CI/CD system, to interact with them and ultimately deploy the updates to the user. Without one, it's much more difficult to track version numbers, control access, promote builds from testing to production, collect metadata, or detect security problems.

Try to imagine writing code and keeping it organized without tools like Git and GitHub. That sounds miserable, right? It's the same for binaries and a binary repository manager, especially on teams that have multiple languages in one house. If you use a binary repository manager like Artifactory, you can store your Java, Go, JavaScript, Docker images, and Python binaries all in one tool, plus 22 other package types.

Artifactory breaks up the repositories for each package type into three different classes:

* **Local**, remote, and virtual. Local repositories are what they sound like: repositories for your build artifacts resulting from local code that exists on your machine.
* **Remote** repositories are also fairly self-explanatory; they contain remote build artifacts, like your project's dependencies. This functions sort of like a cache, so that after the first download, your project pulls its dependencies from the associated remote repository rather than from Maven Central, NPM or PyPi or whatever. If you are using Docker, this is particularly helpful, since it limits the number of pulls you need to make against Docker Hub and you won't hit their new limit for pulls from anonymous users as quickly.
* **Virtual** repositories are a little weirder -- they create a kind of envelope around the local and remote repositories for your project, and this is what you'll be interacting with most frequently.

A lot of headaches are saved here, from an organizational standpoint. Things get released faster because things are more organized and easier to integrate with a CI/CD solution, and there's no jumping around between a dozen tools that all do the same thing but for different package types.

This improvement alone decreases the likelihood of a bad build making it out into the wild, because we humans are really bad at repetitive tasks, and this takes over a lot of repetition for us.

### Cool cool cool, how do I try using one of these things? Sounds enterprisey. {#h3-3-cool-cool-cool-how-do-i-try-using-one-of-these-things-sounds-enterprisey}

It's definitely a thing that's more beneficial to whole companies or dev teams than for an individual person on a side project, but it's still good to learn. There are a handful available, but Artifactory is free up to a certain amount of storage and data transfer.

[Try it here](https://jfrog.com/artifactory/start-free/#saas "Try it here") on the cloud provider of your choice. It also comes with Xray, a vulnerability detection tool, and Pipelines, a CI/CD system, so you're pretty close to an end-to-end DevOps solution to play with and learn on right out of the box. If you don't quite understand CI/CD (or you just need some recommendations for where to start!), check out the previous article in this series: [DevOps 101: CI/CD](https://foojay.io/today/devops-101-part-i-ci-cd/).

### Summarize this for me, high school essay style. {#h3-4-summarize-this-for-me-high-school-essay-style}

In conclusion, package managers are a set of tools that make it easier for you to install, use, update, and remove applications. They go further than just automating the steps we used to have to take manually, with config scripts and Makefiles, by also installing your dependencies and managing a bunch of extra metadata we didn't have clear access to before. The next leap from there is the use of a binary repository as an extension of our source code repositories, to manage all of these binaries and build artifacts produced by our package managers.

Doing so gives us more insight into what's going on with our builds, a simpler way to control who has access to what, and a place to keep all of our build artifacts regardless of the languages or tools involved in our applications. This is a boon to your developers from a sanity and organization standpoint, to your users in the form of faster updates, and to your legal team in the form of faster detection of critical vulnerabilities. The invention of the package manager is possibly one of the most important innovations in computing in decades, and a universal binary repository manager is one of the most important parts of a functional DevOps pipeline.

I hope I've helped you understand what package management is and what it does for you. If you're still confused, that's okay too -- there's a lot going on in this space. Stay tuned for more!

Oh, and if you have specific requests, reach out to me on [Twitter](https://twitter.com/Dixie3Flatline "Twitter")! My DMs are open.
