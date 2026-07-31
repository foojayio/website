---
title: "Intro for Java Devs to DevOps 101 Part I: CI/CD"
slug: "devops-101-part-i-ci-cd"
date: "2021-03-09T08:00:56+00:00"
lastmod: "2021-03-09T08:00:59+00:00"
description: "I’ll cover tools and terminology common to DevOps, plus the occasional newbie-friendly tutorial for emerging or established technologies."
authors:
  - "kat-cosgrove"
image: "index.png"
categories:
  - "DevOps"
tags:
related_posts:
  - "enterprise-java-quality-gates-ai"
  - "lights-camera-action-github-actions-with-java-part-3"
  - "devops-101-part-iii-package-management"
  - "devops-101-part-ii-container-registries"
frozen: false
---

> This post was originally published by [Kat Costgove](https://dev.to/katcosgrove) at [Dev.to](https://dev.to/jfrog/devops-101-ci-cd-49il).

When you're new to an industry, you encounter a lot of new concepts. This can make it really difficult to get your feet underneath you on an unfamiliar landscape, especially for junior engineers.

In this series, I'll cover [tools](https://jfrog.com/devops-tools/) and terminology common to the DevOps space, plus the occasional newbie-friendly tutorial for emerging or established technologies. If you have a request or suggestion, let me know!

Today, I'll break down CI/CD.

### What does that even stand for? {#h3-0-what-does-that-even-stand-for}

The CI stands for **Continuous Integration** , and the CD can stand for either **Continuous Delivery** or **Continuous Deployment**. Yes, they do mean different things. I know, I know, this sounds like it's already getting complicated, but I promise it's not so bad.

### What does Continuous Integration mean? {#h3-1-what-does-continuous-integration-mean}

Practicing Continuous Integration means merging all developers' working codebase with the source, multiple times a day. Doing this requires a series of automated build and unit tests to ensure none of the proposed changes cause problems, but the result is that bugs and integration issues are discovered much earlier in the development process. It also forces engineers to write code that's more modular, making it easier to support later on.

Continuous Integration has been a thing since the early 90s. Though it hasn't always been called that, and some of the implementation has changed, the spirit remains the same: merge changes into source in smaller but more frequent increments, test that the project still builds and runs with those changes, and make sure your engineers are all working on the most recent version of the source. Do this, and you won't end up with a ton of merge conflicts or surprise problems when it comes time to build.

### Okay, what's the difference between Continuous Delivery and Continuous Deployment? {#h3-2-okay-what-s-the-difference-between-continuous-delivery-and-continuous-deployment}

**Continuous Delivery** means what it says on the box: your software updates are continuously delivered. In concert with Continuous Integration, this means you should have the ability to deploy a new build very rapidly because you've automated some quality gates that would otherwise need to be performed manually, like building and testing. That reduction in manual labor means you get to release a bunch of small changes, rather than one huge update every couple of months. Since you're now making smaller, incremental changes, you can also be more confident that your release isn't going to break when you deploy to your users.

**Continuous Deployment** is similar, but it goes one step further -- deployment is automated, too. In Continuous Delivery, there is still a manual quality gate involved before an update is out in the wild. This is a controversial step for some, and requires a lot of trust in your system, but I'm personally a huge fan of it. For a modern [DevOps pipeline](https://jfrog.com/pipelines/) (and thus, you) to be as efficient as possible, human involvement has to be removed wherever possible. I say this a lot, but we are really, really bad at repetitive tasks -- we get bored, we get distracted, and we're SLOW. Write good, comprehensive tests and automate everything you can, then accept that you absolutely are going to deploy a bad update eventually, whether a human is involved in clicking the Big Green Button or not.

### Wait, so what's CI/CD? What does it get me? Go high-level. {#h3-3-wait-so-what-s-ci-cd-what-does-it-get-me-go-high-level}

CI/CD is just the term for the marriage of these concepts. It's an important part of DevOps, since automation and efficiency is what we're all about, and one doesn't really work in the context of DevOps without the other.

Implementing CI/CD practices gets you a faster, more reliable release cycle. You can add new features or bug fixes way, way faster, since you know your engineers are all working from the most recent source, you know there are unit and integration tests and you know it builds. There aren't a bunch of manual steps involved for the engineers or QA or whoever else you might have managing quality gates; instead, somebody pushes code or opens a pull request and those steps are taken care of by your CI/CD tooling.

### How does this actually work? {#h3-4-how-does-this-actually-work}

To start, you need a CI/CD tool. This is what's going to automate a bunch of manual processes for you. It does take some time to set up at the start of a new project, but personally, I'm the flavor of lazy where I'm willing to spend extra time at the beginning to make sure I don't have to do a bunch of repetitive, manual tasks every single time I push code later on. The specifics of configuring any of these tools varies, so check the documentation for your chosen tool, but broadly they all work the same way:

You set something as a trigger, like telling it to watch your source repository for a commit or a merge. You then configure a series of steps, each with pass/fail conditions, like telling it how to run your unit tests, build, [scan for vulnerabilities](https://www.jfrog.com/confluence/display/JFROG/CI-CD+Integration+with+Xray), or deploy your application. With a sufficiently detailed CI/CD pipeline, you don't have to do anything but write code and push it -- the system handles everything else for you. It's great.

### Word, sounds fantastic, sign me up. {#h3-5-word-sounds-fantastic-sign-me-up}

If you're already using some JFrog tools like Artifactory or Xray, it makes sense to stay in the same ecosystem and give Pipelines a try. That way, everything is accessible from one UI. Pipelines integrates with most other DevOps tools and handles a ton of actions natively, so it minimizes the possibility of ending up with the infrastructure version of Frankenstein's Monster. Configuration is just YAML. JFrog gives Pipelines for free as a part of their [free tier](https://jfrog.com/artifactory/start-free/#saas). It's a freemium solution (free usage is limited, but generally fine for personal projects or small stuff) so no credit card required, and it already comes with the rest of the JFrog platrom, namely Artifactory and XRay.

Among other tools I really like CircleCI and TravisCI. Both are cloud solutions with a good number of built-in integrations, both are pretty easy to configure, and both have support for a wide range of popular programming languages. There are differing limitations in what you can use as your version control and build environments.

### Summarize this like you're writing an essay in high school. {#h3-6-summarize-this-like-you-re-writing-an-essay-in-high-school}

In conclusion, CI/CD is a combination of methodology and tooling with the goal of increasing your speed and efficiency as a developer by automating tasks like building, testing, and deploying so that you can do all of those things more often. The benefit to you is more frequent software releases, earlier detection of bugs, and bad releases making it to production less often. There are several tools out there that help you accomplish this goal, from freemium tools like TravisCI or CircleCI to enterprise-scale tools with additional features like JFrog Pipelines.

I hope I've helped you understand what CI/CD is and what it does for you. If you're still confused, that's okay too -- this can be kind of a "big" problem and some of it is still in flux. Feel free to get in touch through the comments or on Twitter at [@Dixie3Flatline](https://twitter.com/Dixie3Flatline) if you have more questions. Stay tuned for the next article in this series, and let me know if you have a request!
