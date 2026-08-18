---
title: "Book Review: “Effortless Cloud-Native App Development Using Skaffold”"
date: "2021-11-25T12:52:08+00:00"
lastmod: "2021-11-25T12:53:06+00:00"
description: "Skaffold is a cloud native open source framework from Google that lets SpringBoot devs build Kubernetes apps easily and deploy effortlessly!"
canonical: "https://talktotheduck.dev/cloud-native-skaffold-book-review"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Book-Review-Recovered-01.jpg"
categories:
  - "Book Review"
  - "Books"
  - "DevOps"
related_posts:
  - "production-horrors-handling-disasters-public-debrief"
  - "book-review-why-programs-fail"
  - "the-debugger-checklist-part-ii"
  - "modernize-legacy-code-in-production-rebuild-your-airplane-midflight-without-crashing"
frozen: false
---

I'm a developer who cares deeply about production. But I'm not a devops and unfortunately I'm pretty bad at that. That's why when I heard about Skaffold it instantly piqued my interest. Write Kubernetes cloud native apps without well... Writing Kubernetes native apps... Sign me up!

Unfortunately, as we all know. The time to pick up a new technology is when we actually need it and then it's a rush to get something out. Few of us have time to take off from our busy day to study something and learn something new. That's why when [Ashish Choudhary](https://twitter.com/iASHeeesh), the author of the book "**Effortless Cloud Native App Development Using Skaffold** " asked for reviewers in the [foojay.io](https://foojay.io/) slack group, I jumped on the opportunity. I can learn something interesting and be productive (this book review).

Before we go on to the book review (and note [another Foojay book review on the same book is here](https://foojay.io/today/book-review-effortless-cloud-native-app-development-using-skaffold/)), I'd like to get the following things out of the way:

* I don't know Ashish Choudhary personally
* I receive nothing for this review and don't provide any affiliate links or stuff like that. [This is the only link to the book](https://www.packtpub.com/product/effortless-cloud-native-apps-development-using-skaffold/9781801077118) in this post, and it has no affiliate code!
* I did some work for the publisher Packt in the past (an online course) - I don't think that affects my judgement here. It's been a while since I did that work

Another important detail: I didn't run the code or go through the samples. I just read the book. I also did that relatively quickly and used the PDF version. As such, I feel I lost some of the core experience in the book.

## What's Skaffold

[Skaffold](https://github.com/GoogleContainerTools/skaffold) is an open source project started by a Google engineer after suffering the pain of cloud native Kubernetes deployments. It's effectively a command-line tool that automates the build, push, and deploy steps for Kubernetes applications.

That alone warrants an info sheet. The surrounding system integrates with Maven/Gradle/Spring etc. to create a smooth development experience. This makes building/debugging Kubernetes apps locally almost as easy as running a regular Spring Boot application.

There's obviously additional complexity, and configuration, but the core idea is the same. Developers still need to understand basic Kubernetes ideas, there's no way around it. But you can remove some of the hassle involved with it. Kubernetes is aimed at devops and a lot of its features are redundant for developers. This way we can build cloud native microservices without the hassle.

## The Book

Packt books offer a quick and practical introduction to current technologies. They have the advantage of including a lot of current details that other books might miss. However, these details might become outdated by the time you need the book. It's a tradeoff you need to make based on your experience with the publication.

Since the subject ‌is a very specific tool, I see the value of going into specific details and samples.

Choudhary divided the book into 3 sections:

1. The Kubernetes Nightmare - Skaffold to the Rescue
2. Getting Started with Skaffold
3. Building and Deploying Cloud-Native Spring Boot Applications with Skaffold

In the first section, we get a glimpse into the "problem" of building Kubernetes applications with chapters such as "Developing Cloud-Native Applications with Kubernetes - A Developer's Nightmare".

Choudhary introduces Skaffold in the second section. The third section gets into the finer points of deployment, alternatives, etc.

### Cloud Native

The book focuses on the separation of the inner/outer loop of the development cycle. The inner loop is the local development environment, where the outer loop is the production/CI/CD environment. What makes this development style "Cloud Native" is the similarity between the inner and outer loops.

There's a similar focus on the value delivered in integration tests during the CI cycle and finally in pushing to production via tools like GitOps. The cool thing is that we use an environment that's pretty close to production in all stages of development.

Another aspect that makes Skaffold native to the cloud is the instant deployment via jib. As a result, we can instantly see changes in our local Kubernetes environment as we save changes in the IDE. That's pretty cool, and this alone is worth the price of admission to Skaffold (which admittedly is free).

## What I Liked

### Writing and Focus

Choudhary is a talented writer who uses clear language and examples. The examples weren't too verbose, as is sometimes the case with such books. He wrote the book targeting Kotlin/Java developers, which is great.

The core ideas and benefits of Skaffold are covered early on in the book and are pretty obvious. There are a few diagrams that illustrate the big set pieces.

### Objectivity

The book covers competitors to Skaffold and seems to be reasonably objective with its treatment. This is at the end of the book, so if you have doubts about Skaffold, I suggest reading the ending first.

### Publishing

While somewhat wasteful (more on that later), the layout of the Packt books is as usual pretty great. Packt books have excellent layout, table of contents and index. This makes the reading/referencing process much smoother.

## What I Didn't Like

### Cloud Native

I feel the phrase cloud native was ‌co-opted by GraalVM and frameworks such as Quarkus. I understand what Choudhary is aiming at and think this makes sense for the book. But it's one of those overused phrases that loses some of its meaning. A cloud native book is a pretty vague statement, Skaffold narrows it down but only after you know what that means.

There's an entire chapter on GKE which I skipped entirely for that reason. It just isn't relevant to me.

### Publishing

While I like Packt books, I have two grievances. I hope they improve on these:

* I don't like filler in books. The book could be 100 pages shorter, this is due to blank pages, large margins, over spacing, etc. Think of the tree's Packt (this goes to other publishers too!).
* I wish other publishers would pick up the source annotation style of manning. This is hard to do as [I discovered](https://medium.com/sketch-app-sources/using-sketch-and-asciidoc-to-generate-a-professional-tech-book-ef5b5d8dd410) , but the readability is so much better...

Notice that both comments aren't unique to Packt and apply to most publishers.

## Final Word

I liked the book. I still haven't made up my mind about Skaffold though. As a latecomer to the cloud native realm, I feel I need to leapfrog some of the hassle. That makes it attractive. But I try to keep away from [Google services due to past trauma](https://talktotheduck.dev/production-horrors-handling-disasters-public-debrief) . Skaffold doesn't require GCP and can still be pretty useful without it, so it's something I intend to try.

I like that Choudhary focused the book on Java/Kotlin as I'm a Java guy. But I'm doing a lot of cloud native polyglot work recently, and this could be a limitation. I saw a mention in the book of NodeJS debugging, but that's it. I honestly don't know if I can use Skaffold for one of my Polyglot demos. It would be really cool for that.

I assume this isn't discussed much because of a limitation in Skaffold, not because of an omission in the book.
