---
title: "Why I prefer trunk-based development"
date: "2023-06-23T08:06:35+00:00"
lastmod: "2023-06-23T08:07:10+00:00"
description: "Trisha summarizes the advantages of trunk-based development (as opposed to branch-based development) in this article."
canonical: "https://trishagee.com/2023/05/29/why-i-prefer-trunk-based-development/"
authors:
  - "trisha-gee"
image: "tree.jpg"
categories:
  - "Agile"
  - "Opinion"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-ways-to-improve-your-code-reading-skills"
frozen: false
---

**Trisha summarizes the advantages of trunk-based development (as opposed to branch-based development) in this article.**

These days, distributed version control systems like Git have "won the war" of version control. One of the arguments I used to hear when DVCSs were gaining traction was around how easy it is to branch and merge with a VCS like Git. However, I'm a big fan of [Trunk-Based Development (TBD)](https://en.wikipedia.org/wiki/Trunk-based_development), and I want to tell you why.

With trunk-based development, all developers work on a single branch (e.g. 'main'). You might have read or heard [Martin Fowler](https://martinfowler.com/articles/continuousIntegration.html) or [Dave Farley](https://youtu.be/v4Ijkq6Myfc) talking about it. It's when I was working with Dave (around about the time that Git was rapidly becoming the "go to" version control system) that I really saw the benefits of trunk-based development for the team, particularly in an environment that was pioneering Continuous Delivery - Dave was writing the book with Jez Humble while I worked with him.

In contrast, the branching model encourages developers to create separate branches for every feature, bug fix, or enhancement. Although branching may seem like a logical approach to isolate changes and reduce risk, several factors make me more comfortable with trunk-based development.

{#more-98737}

**1. Speed and Efficiency**

In trunk-based development, the entire team works on a single branch. This model allows for quicker integrations and fewer merge conflicts. This is literally "[Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) (CI)", as originally suggested by the practices of Extreme Programming. While these days we tend to mean "run your build and tests on a team server every time you commit" when we say CI, what CI really meant was actually *integrate* your code regularly. Code living on separate branches is, by definition, not integrated. And the longer these branches live for, the more challenging it is to merge them back into the main codebase. It might seem fast to develop your fixes and improvements on a separate branch that isn't impacted by other developers' changes, but you still have to pay that cost at some point. Integrating small changes regularly into your code is usually less painful than a big merge at the end of a longer period of time.

**2. Greater Code Stability**

Trunk-based development encourages frequent commits, which leads to smaller and manageable changes. How and why? For the same reason that we don't want big merges from long-lived branches - the longer we leave it to commit our changes, the higher the chances our commit will clash with someone else's changes. By frequently pulling in the other developers' changes, and frequently pushing small changes of working code, we know the codebase is stable and working. Of course, this assumption of "stable and working" is easier to check if we have a CI server that's running the build and tests for each of these commits. We also have to *stop* making commits if the build breaks at any time, and focus on fixing that build. Continuously pushing small, frequent commits when the build is already broken isn't going to do anyone any favours.

In the branching model, large and infrequent merges can introduce bugs that are hard to identify and resolve due to the sheer size of the changes. Have you ever merged the trunk into your branch after someone else has merged their own big piece of work and found your code no longer works? It can take a LOT of time to track down why your tests are failing or the application isn't working the way you expect when you've made a whole bunch of changes and someone else has made a whole bunch of different, or overlapping, changes. And that's assuming you actually have reliable test coverage that can tell you there's a problem.

**3. Enhanced Team Collaboration**

My favourite way of sharing knowledge between team members is pair programming. I know not everyone is a fan, or is in a position to do it (especially now more people are working remotely, but if so, check out JetBrains' [Code With Me](https://www.helenjoscott.com/2022/12/08/using-code-with-me-to-collaborate-on-getting-to-know-intellij-idea/)). If you're not pairing, then at least you want to be working on the same code, right? If you're all working on your own branches, you are *not* collaborating. You are competing. To see who can get their code in fastest. To avoid being stomped on by someone else's code changes.

If you're all working on the same branch, you tend to have a better awareness of the changes being made. This approach fosters greater team collaboration and knowledge sharing. In contrast, branching can create a siloed work environment where you're all working independently, leading to knowledge gaps within the team.

**4. Improved Continuous Integration and Delivery (CI/CD) Practices**

Dave Farley's book, "[Continuous Delivery](https://amzn.to/43rppXn)", and his log posts and videos, argue something along the lines of "trunk-based development is inherently compatible with Continuous Integration and Continuous Delivery (CI/CD) practices".

In a trunk-based model, continuous integration becomes more straightforward because your code is committed frequently to trunk, and that's the branch your CI environment is running the build and tests on. Any failures there are seen and addressed promptly, reducing the risk of nasty failures. It's usually easy to track down which changes caused the problem. If the issue can't be fixed immediately, you can back the specific changes that caused it.

By now we should know the importance of a quick feedback loop - when you find problems faster you can locate the cause faster, and you can fix it faster. This improves your software's quality.

Continuous delivery also thrives in a trunk-based development environment. Successful continuous delivery hinges on the ability to have a codebase that is always in a deployable state. The trunk-based development approach ensures this by promoting frequent commits, frequent integrations, and tests on all of these integrations. The small number of changes being introduced at any one time makes the software easier to deploy and test.

In contrast, implementing effective CI/CD can be more complex and time-consuming with the branching model. While it's tempting to think "well I run my build and all my tests on my branch", you're not actually *integrating* every time you commit. It's at merge (or rebase) time that you start to see any integration issues. All those tests you were running on your branch "in CI" were not testing any kind of integration at all.

Merging and testing code from different branches can introduce delays and potential errors, which takes away some of the benefits of having a build pipeline in the first place.

**5. Reduced Technical Debt**

Long-lived branches often lead to 'merge hell', where the differences between one branch (like 'main') and another (for example your feature branch) are so great that merging becomes a nightmare. This can result in technical debt as you may resort to quick fixes to resolve merge conflicts, or accept suggestions from your IDE that resolve the merge but that you don't fully understand. With trunk-based development, frequent merges and smaller changes make it easier to manage and reduce the build-up of technical debt.

**In conclusion**

I, personally, think trunk-based development has clear advantages, and I have experienced them first-hand working in teams that have adopted this approach. However, it requires a mindset, a culture, within the development team. You need to frequently merge in other developers' changes into your own code. You need to commit small changes, frequently, which requires you to only change small sections of the code and make incremental changes, something which can be a difficult habit to get used to. Pair programming, comprehensive automated testing, and maybe code reviews are key practices to helping all the team to adopt the same approach and culture.

Trunk-based development, done in a disciplined way, streamlines the development process, enhances team collaboration, improves code stability, supports efficient CI/CD practices, and may result in less technical debt. While it may be challenging to adapt to this approach if you've been working with a branch-based model, the long-term benefits are worthwhile. If you want to move to working this way, you may also want to read Dave's article addressing [barriers to trunk-based development](https://www.davefarley.net/?p=269).
