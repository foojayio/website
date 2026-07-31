---
title: "DevOps 101 Part II: Introduction to Container Registries"
slug: "devops-101-part-ii-container-registries"
date: "2021-03-17T08:58:35+00:00"
lastmod: "2021-03-17T08:59:33+00:00"
description: "I’m covering tools and terminology common to DevOps, plus the occasional newbie-friendly tutorial for emerging or established technologies."
authors:
  - "kat-cosgrove"
image: "index.png"
categories:
  - "DevOps"
tags:
related_posts:
  - "devops-101-part-iii-package-management"
  - "devops-101-part-i-ci-cd"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "enterprise-java-quality-gates-ai"
frozen: false
---

> This post was originally published by [Kat Costgove](https://dev.to/katcosgrove) at [Dev.to](https://dev.to/jfrog/devops-101-container-registries-1lil).

When you're new to an industry, you encounter a lot of new concepts. This can make it really difficult to get your feet underneath you on an unfamiliar landscape, especially for junior engineers.

In this series ([part 1 is here](https://foojay.io/today/devops-101-part-i-ci-cd/)), I'm covering tools and terminology common to the DevOps space, plus the occasional newbie-friendly tutorial for emerging or established technologies. If you have a request or suggestion, let me know!

Today, I'll break down container registries.

### What's a container registry? {#h3-0-what-s-a-container-registry}

A **container registry** is a tool for hosting, versioning, and distributing container images within repositories. They can be public (like Docker Hub) or private (like [JFrog Container Registry](https://jfrog.com/container-registry/ "JFrog Container Registry")).

If you've used Docker, you've pulled an image from a container registry before. There are several out there, both public and private, free and paid, but I'll mostly be focusing on JFrog Container Registry since it's free, can run locally or on a cloud provider, and provides a few other nice-to-have features that other services don't, like a Helm repository and advanced metadata.

### What's the difference between public and private container registries? {#h3-1-what-s-the-difference-between-public-and-private-container-registries}

**Public registries** allow anyone to create a repository and host container images there for other people to use. Getting started with a public registry is fast, since you don't have to deal with any of the infrastructure. However, public registries do sacrifice security in exchange for that ease of use. If using standard images works for your project, and you aren't concerned about security, a public registry might be the right choice for you.

**Private registries** are hosted by the organization that will be using them. JFrog Container Registry can be hosted either locally on your hardware (on-prem) or with a cloud provider like Google, Amazon, or Microsoft. Hosting your own container registry gives you fine-grained access control and the ability to define your own security policies. Since the organization is in complete control of the images, repositories, and access control for those things, they can be certain that container images are what they say they are and that no one has access to things they shouldn't. In the case of JFrog Container Registry, you also have the option to organize your images into various local, remote, generic, and virtual repositories, depending on your needs. For larger organizations, this additional reliability and security is worth the effort involved in setting up the infrastructure itself.

### What are all these different types of repositories? {#h3-2-what-are-all-these-different-types-of-repositories}

**Local repositories** are exactly what they sound like -- locally-managed repositories into which you deploy artifacts. Pretty simple.

**Remote repositories** are used as caching proxies for remotely-managed repositories. These are a little more complicated, as you must define the behavior of the caching and proxying. It is not the same as a mirror; an artifact only exists in a remote repository once it is requested by something else, according to the configuration you define.

**Generic repositories** have no specific package type associated with them. Any time you create a repository, you must define a package type. JFrog Container Registry uses this to do some behind-the-scenes work for you, indexing the artifacts and calculating metadata for your packages to optimize performance. Generic repositories have no package type, so you can put pretty much whatever you want in here, but you do lose out on some features you would see otherwise.

**Virtual repositories** bundle up a collection of various remote and local repositories with the same package type into a single URL.

### Neat. How do I try one of these things? {#h3-3-neat-how-do-i-try-one-of-these-things}

The quickest and easiest way is with the [cloud hosted](https://jfrog.com/container-registry/cloud-registration/ "cloud hosted") version of JFrog Container Registry, since it's still free-ish (you pay for usage on the cloud provider over a certain amount), mostly configures itself, and doesn't require you to set up a reverse proxy. Sign up here with your preferred flavor of cloud provider and follow along with the automated setup wizard to configure some initial options and create your default repositories.

Once that's done, it's really easy to interact with. You can use the native Docker client, just like you would with Docker Hub. Just log in:

`docker login ${server-name}-{repo-name}.jfrog.io`

To push an image, give it a tag and push:

`docker tag ${server-name}-{repo-name}.jfrog.io/`  
`docker push ${server-name}-{repo-name}.jfrog.io/`

Pulling an image is easy, too:

`docker pull ${server-name}-{repo-name}.jfrog.io/`

Setup and usage is a bit more complicated with the on-prem version, but fortunately, the [official documentation](http://https://www.jfrog.com/confluence/display/JCR/Getting+Started+with+JFrog+Container+Registry+as+a+Docker+Registry "official documentation") is pretty explicit.

### Summarize this for me. {#h3-4-summarize-this-for-me}

If your organization or team is getting serious about cloud native development, a private container registry is probably something you're going to work with at some point. The goal of a private registry is to provide you with a place to organize, version, and deploy your container images with increased security options and better integration into your existing CI/CD workflows when compared to a public registry, whether it's hosted on a local server or out on the cloud.

[JFrog Container Registry](https://jfrog.com/container-registry/ "JFrog Container Registry") is free on-prem or freemium on any of the three major cloud providers and gives you everything the other services on the market do, plus some extra goodies like a Helm repository and rich metadata.

I hope I've helped you understand what this tool does for you and given you a reasonable pushing-off point into using one. If you're still confused, that's okay too -- feel free to get in touch if you have more questions.

Stay tuned for the next article in this series, and let me know if you have a request for deep dives or demos!
