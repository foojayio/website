---
title: "Ask Yourself: Do You Really Need Kubernetes?"
slug: "do-you-really-need-kubernetes"
date: "2021-07-07T07:37:55+00:00"
lastmod: "2021-07-09T09:02:44+00:00"
description: "Whether or not you need Kubernetes depends on many different factors. Statistically, you don't need Kubernetes more often than you do need it."
canonical: "https://blog.payara.fish/do-you-need-kubernetes"
authors:
  - "rudy-de-busscher"
image: "/images/posts/2021/07/do-you-really-need-kubernetes/21_d3cvM_400x400.png"
categories:
  - "Kubernetes"
tags:
related_posts:
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "creating-a-kubernetes-operator-in-java"
  - "securing-microservices-with-auth0-and-microprofile-in-kubernetes"
  - "warm-up-fast-run-lean-vertical-scaling-for-java-on-kubernetes-with-azul-prime-and-kedify"
frozen: false
---

These days, it seems [Kubernetes](https://kubernetes.io/) is a topic that is never too far from people's lips. The tool, and the associated tools built around it, are talked about so often it seems it's the only subject important to developers these days - especially as the IT world becomes increasingly orientated towards cloud and microservices.

But in spite of all the conversation around Kubernetes... do you *really* need Kubernetes for your environment? Or is it just another case of the next 'new and shiny' object, with people distracted by the novelty and possibility, rather than the facts? In this article, I'll take a closer look at why Kubernetes might be a case of the hype outweighing the helpfulness in most cases.

Whether or not you need Kubernetes depends on many different factors. But statistically, I think you ***don't*** need Kubernetes more often than you do need it.

Let us review what problem Kubernetes solves and determine when you would benefit from learning and using Kubernetes in your environment.

What is Kubernetes? {#h2-0-what-is-kubernetes}
----------------------------------------------

Let's start by looking at a standard description of Kubernetes. This is what you find on[Wikipedia](https://en.wikipedia.org/wiki/Kubernetes):

*"Kubernetes is an open-source container-orchestration system for automating computer application deployment, scaling, and management."*

So first of all, Kubernetes works with containers. As you are most likely aware, containers are the replacement for Virtual Machines (VMs), and they make more efficient use of the resources of your hardware infrastructure. Replacing the VMs with a container solution is a cost-effective change. The architecture has its challenges, however, as processes and applications still need to be properly separated, which is more difficult with Kubernetes than VMs.

When you have many containers to manage, it becomes difficult to do it manually. This is where Kubernetes comes into the picture. Kubernetes makes it possible to set up multiple containers that work together and have the ability to scale.

This description may sound familiar; you probably recognize a few of the properties from the microservices architectural model. Microservices architecture involves a set of independent applications that collaborate, creating the responses for user requests, which means you can scale each microservice individually. Kubernetes is designed to work with a microservices architecture, to help when there are large numbers of independent applications that need to be set up and scaled together.

Kubernetes is also about automating workflows. When you need to execute some scripts between the deployment of the first and the second service in Kubernetes, you can write a Kubernetes operator to automate this process, even implementing some complex logic into how the Kubernetes resources are called and how they interact with each other.

Why Kubernetes Might Not Work for You {#h2-1-why-kubernetes-might-not-work-for-you}
-----------------------------------------------------------------------------------

Now that we have explored what Kubernetes is, we can come to the question of why you would use it or even if you really need to use Kubernetes at all.

As mentioned above, Kubernetes is in alignment with typical microservices architecture, where you have many applications that work together and might need some complex initialisation and setup.

Therefore, container orchestration platforms are successful for large companies like [Netflix](https://www.google.com/search?q=netflix&rlz=1C1CHBD_en-GBGB919GB919&sxsrf=ALeKk02Z3vqud0J08J-9lb8KC-01iGqsIw%3A1623073256589&ei=6CG-YOm_I46lUur7lhA&oq=netflix&gs_lcp=Cgdnd3Mtd2l6EAMyBAgAEEMyBAgAEEMyCggAELEDEIMBEEMyBQgAELEDMgUIABCxAzIFCAAQsQMyBQgAELEDMgUIABCxAzIFCAAQsQMyBQgAELEDOgQIIxAnOg4ILhCxAxCDARDHARCjAjoICAAQsQMQgwE6BwgAELEDEENQn-4LWNPzC2CX9gtoAHACeACAAW2IAagFkgEDNy4xmAEAoAEBqgEHZ3dzLXdpesABAQ&sclient=gws-wiz&ved=0ahUKEwjp05yH04XxAhWOkhQKHeq9BQIQ4dUDCA4&uact=5)and [Amazon,](https://www.amazon.co.uk/)which operate a huge microservices environment. But most of us do not work for one of the world's biggest organisations with colossal software systems to match; where there are a vast amount of different workflows and independent applications, as well as armies of developers to work on them.

Instead, you probably have a handful of developers that create and maintain the one or few applications that are critical for your business.

Learning, setting up, and using Kubernetes is a specialization on its own. It is not something you can accomplish in an hour on a Friday afternoon! It requires a large investment of time and resources as you need many servers to set up Kubernetes, with it requiring a cluster of several machines by default.

For smaller businesses, therefore, the likelihood the the benefits outweigh the costs is small. Using Kubernetes is most often a case of solving the problems experienced by larger companies, but that you yourself do not have - and adopting Kubernetes will only cause more expenditure, both in time and resources.

Therefore, before diving into Kubernetes or a container orchestration tool, you should ensure it actually does solve one of the problems you encounter in your environment.

Use Kubernetes-Based Tools {#h2-2-use-kubernetes-based-tools}
-------------------------------------------------------------

As mentioned already, Kubernetes can be helpful when you are dealing with a large environment, and cloud providers have caught on, offering support for Kubernetes with their service.

However, using Kubernetes directly on the cloud provider isn't a good idea in many cases. The installation may be managed for you, but you are still required to deal with all of the low-level details of Kubernetes. And this requires a lot of investment to do it properly.

Recently, several tools emerged that are built on top of Kubernetes, which bring you the capabilities and power provided by Kubernetes but with an easier to use interface for users and developers.

This is also the philosophy behind our new product, [Payara Cloud,](https://www.payara.fish/products/payara-cloud/)to be launched in the fall of 2021.

Perhaps you are interested in just configuring and running your application without wanting to learn the concepts of Kubernetes deployments, services, and other tasks like SSL certificate management, routing, and setting up the monitoring. Tools such as Payara Cloud use Kubernetes to bring your application to the cloud in a way that you are familiar with, without you having to retrain developers and make it all work yourself - it's handled for you. Deploying an application to Payara Cloud is very similar to deploying an application on a runtime that you have running locally.

You Probably Don't Need Kubernetes {#h2-3-you-probably-don-t-need-kubernetes}
-----------------------------------------------------------------------------

Kubernetes is useful if you are dealing with many containers and require some automation of the steps when starting them. So, unless you have a large microservice environment, Kubernetes is unlikely to bring much added value. Probably, it is not needed or suited for your case and you should not invest in it.

Several tools that have been released to make use of Kubernetes while shielding your team from the hassle and the details of using it. You get all of the benefits of Kubernetes without having to learn how to make it work. [Payara Cloud is an example of this that caters specifically to the Jakarta EE runtime:](https://www.payara.fish/products/payara-cloud/) you just need to configure your application, upload it, and you are ready to go. This is the kind of Kubernetes usage that is useful for the majority of companies.

I hope this blog has created some food for thought and maybe helped inspire a more questioning attitude to Kubernetes. Just because it's the buzzword of the moment doesn't mean it necessarily fits your business!
