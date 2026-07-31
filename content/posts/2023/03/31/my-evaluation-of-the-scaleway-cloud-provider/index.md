---
title: "My Evaluation of the Scaleway Cloud Provider"
slug: "my-evaluation-of-the-scaleway-cloud-provider"
date: "2023-03-31T09:54:20+00:00"
lastmod: "2023-03-31T10:21:37+00:00"
description: "Heroku's owner, Salesforce, announced it would stop the free plan, I found a new provider, Scaleway, and here are my experiences."
canonical: "https://blog.frankel.ch/evaluation-scaleway/"
authors:
  - "nicolas-frankel"
image: "2560px-Scaleway_logo_2018.svg.png"
categories:
  - "Cloud"
  - "Opinion"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "chopping-monolith"
  - "five-java-developer-must-haves"
frozen: false
---

A couple of years ago, I developed an app that helped me manage my conference submission workflow.

Since then, I have been a happy user of the free Heroku plan.

Last summer, Heroku's owner, Salesforce, announced that it would stop the free plan in November 2022.

I searched for a new hosting provider and found [Scaleway](https://www.scaleway.com/).

In this article, I'd like to explain my requirement, why I chose them, and my experience using them.

The context {#h2-0-the-context}
-------------------------------

I've already described the app in [previous blog posts](https://blog.frankel.ch/automating-conference-submission-workflow/), especially the [deployment part](https://blog.frankel.ch/automating-conference-submission-workflow-deploying/). Yet, here's a summary in case you want to avoid rereading it.

The source of truth is Trello, where I manage the state of my conference s: Backlog, Submitted, Abandoned, Accepted, and Published (on this blog). The "done" state is when I archive a Published card.

I wrote the application in Kotlin with Spring Boot. It's a webapp that listens to change events from Trello via webhooks. An event starts a BPMN workflow based on Camunda. The workflow manages my Google Calendar and a Google Sheet file.

For example, when I move a card from Backlog to Submitted, it adds the conference to my calendar. The event is labeled as Free and has a particular grey color to mark it's a placeholder. It also adds a line in the Google Sheet with the status Submitted.

When I move the card from Submitted to Accepted, it changes the Google Calendar event color to default and marks it as Busy. It also changes the Google Sheet status to Accepted.

Why Scaleway? {#h2-1-why-scaleway}
----------------------------------

As I mentioned in the introduction, I was a happy Heroku user. One of the great things about Heroku, apart from the free plan, was the "hibernating" feature: when the app was not in use, it switched it off. In essence, it was scale-to-zero for web apps.

The first request in a while was slower, but it wasn't an issue for my usage. The exciting bit is that scale-to-zero was not a feature but Heroku's way to keep costs low. Outside of Heroku's free plan, [automatic scaling](https://devcenter.heroku.com/articles/scaling#configuration) can only scale down to 1.

I'm a big fan of [Viktor Farcic](https://twitter.com/vfarcic)'s YouTube channel, [DevOps Toolkit](https://www.youtube.com/@DevOpsToolkit). At the same time Heroku announced the end of its free plan, I watched [Scaleway - Everything We Expect From A Cloud Computing Service?](https://www.youtube.com/watch?v=VlBiLFaSi7Y).

By chance, Scaleway offers free credits to startups, including the company I'm currently working for. It didn't take long for me to move the application to Scaleway.

Deploying on Scaleway {#h2-2-deploying-on-scaleway}
---------------------------------------------------

Before describing how to deploy on Scaleway, let's explain how I deployed on Heroku. The latter provides a Git repo. Every push to `master` triggers a build based on what Heroku can recognize. For example, if it sees a `pom.xml`, it knows it's a Maven project and calls the Maven command accordingly.

Under the hood, it creates a regular Docker container and stores and runs it. For the record, this approach is the foundation of Buildpacks, and Heroku is part of its creators along with VMWare. On Heroku, developers follow their regular workflow, and the platform handles both the build and the deployment parts.

Scaleway offers a dedicated scale-to-zero feature for its Serverless Containers offering. First, you need to have an already-built container. When I started to use it, the container was to be hosted on Scaleway's dedicated Container Registry; now, it can be hosted anywhere.

On the UI, one chooses the container to deploy, fills in environment variables and secrets, and Heroku deploys it.

Main issues {#h2-3-main-issues}
-------------------------------

I stumbled upon two main issues using Scaleway so far.

* **The is the only way to deploy a container.** It's a good thing to start with, but it doesn't fit regular usage. The industry standard is based on build pipelines, which compile, test, create container images, store them in a registry, and deploy them on remote infrastructure.
* **You need to fill in secrets on every deployment.** GitHub and GitLab both allow configuring deployed containers with environment variables. This way, one can create a single container but deploy it in different environments. You can configure some environment variables as secrets. Nobody can read them afterward, and they don't appear in logs. Scaleway also offers secrets. However, you must fill them out at every deployment. Beyond a couple of them, it's unmanageable.

Bugs and Scaleway's Support {#h2-4-bugs-and-scaleway-s-support}
---------------------------------------------------------------

In my short time using Scaleway, I encountered two bugs.

* The first bug was a long delay between the time I uploaded a container in Scaleway's registry and the time it was available for deployment. It lasted for a couple of days. The support was quick to answer the ticket, but afterward, it became a big mess. There were more than a couple of back-and-forth messages until the support finally acknowledged that the bug affected everybody. The worst was one of the messages telling me it was due to an existing running container of mine failing to start, i.e.; the bug was on my side.
* The second bug happened on the GUI. The deployment form reset itself while I was filling in the different fields. I tried to be fast enough when filling but to no avail. The same happened as with the previous issue: many back and forth, and no actual fixing. Finally, I tried a couple of days after I created the ticket, and I informed the support. They answered that it was normal because they had fixed it, but without telling me.

Finally, I opened a ticket to ask whether an automated deployment option was possible. After several messages, the support redirected me to a [GitHub](https://github.com/philibea/scaleway-containers-deploy) project. The latter offers a GitHub Action that seemed to fulfill my requirement. Unfortunately, it cannot provide a way to configure the deployed container with environment variables. The only alternative the support offers is to embed environment variables in the container, including secrets.

Regardless of the issues, the support's relevance ranges from average to entirely useless.

Logging {#h2-5-logging}
-----------------------

All Cloud providers I've tried so far offer a logging console. My experience is that the console looks and behaves like a regular terminal console: the oldest log line is on top and the newest at the bottom, and one can scroll through the history, limited by a buffer.

Scaleway's approach is completely different. It orders the log lines in the opposite order, the newest first and oldest last. Worse, there's no scrolling but pagination. Finally, there's no auto-refresh! One has to paginate back and forth to refresh the view - and if new log lines appear, pages don't display the same data. It severely impairs the developer experience and makes trying to follow the logs hard.

I tried to fathom why Scaleway implemented the logging console this way and came up with a couple of possible explanations:

* Engineering doesn't eat its own dog food
* Engineering doesn't care about Developer Experience
* It was cheaper this way
* Product said it was a bad Developer Experience but Engineering did it anyway because of one of the reasons above and it has more organizational power

In any case, it reflects poorly on the product.

Conclusion {#h2-6-conclusion}
-----------------------------

Even though my usage of Scaleway is 100% free, I'm pretty unhappy about the deployment part.

I came for the free credits and the scale-to-zero capability.

However, the lack of an acceptable automated deployment solution and the support of heterogeneous quality (to be diplomatic) make me reconsider.

On the other hand, the Scaleway Cloud service itself has been reliable so far.

My Trello workflow runs smoothly, and I cannot complain.

Scaleway is typical of a not-bad product ruined by an abysmally bad Developer Experience.

If you're developing a product, be sure to take care of this aspect of things: the perception of your product can take a turn for the worse because of a lack of consideration for developers.

**To go further:**

* [Scaleway](https://www.scaleway.com/)
* [Google Cloud Run](https://cloud.google.com/run)

*Originally published at [A Java Geek](https://blog.frankel.ch/evaluation-scaleway/) on March 26^th^, 2023*

*[GUI]: Graphical User Interface
*[CFP]: Call For Papers
