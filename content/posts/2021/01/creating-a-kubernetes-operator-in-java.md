---
title: "How to Create a Kubernetes Operator in Java"
slug: "creating-a-kubernetes-operator-in-java"
date: "2021-01-27T08:46:27+00:00"
lastmod: "2021-07-05T20:02:49+00:00"
description: "Kubernetes is much more than a runtime platform -- through its API you can not only create custom clients, but also extend Kubernetes itself."
canonical: "https://blog.payara.fish/creating-a-kubernetes-operator-in-java"
authors:
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2021/01/kuber.jpg"
categories:
  - "Kubernetes"
  - "Videos"
tags:
related_posts:
frozen: false
---

Kubernetes is much more than a runtime platform for Docker containers.

Through its API, you can not only create custom clients, but you can also extend Kubernetes. Those custom Controllers are called Operators and work with application-specific custom resource definitions. You can not only write those Kubernetes operators in Go, but you can do this also in Java.

In this talk, delivered by Payara's[Rudy De Busscher](https://twitter.com/rdebusscher) at [JCON 2020](https://jcon.one/en/), you will be guided through setting up your environment to your first explorations of the Kubernetes API within a plain Java program.

{{< youtube JdfQzv2YbVE >}}

Rudy explores the concepts of resource listeners, programmatic creation of deployments and services, and how this can be used for your custom requirements.
