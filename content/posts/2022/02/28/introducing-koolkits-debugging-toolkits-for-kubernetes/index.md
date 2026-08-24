---
title: "OSS Debugging Toolkits for Kubernetes"
date: "2022-02-28T12:27:18+00:00"
lastmod: "2022-02-28T12:59:18+00:00"
description: "New OSS approach for debugging live applications - right from inside your IDE or terminal window, and without stopping the live application."
canonical: "https://lightrun.com/debugging/koolkits-debugging-toolkits-for-kubernetes/"
authors:
  - "shai-almog"
image: "lightrun-koolkits.png"
categories:
  - "Kubernetes"
  - "Microservices"
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "the-debugger-checklist-part-ii"
  - "what-is-debugging-in-140-seconds"
frozen: false
---

KoolKits (**K** ubernetes t**oolkits**) are highly-opinionated, language-specific, batteries-included debug container images for Kubernetes. In practice, they're what you would've installed on your production pods if you were stuck during a tough debug session in an unfamiliar shell.

To briefly give some background, note that these container images are intended for use with the new [kubectl debug feature](https://kubernetes.io/docs/tasks/debug-application-cluster/debug-running-pod/#ephemeral-container), which spins up [Ephemeral containers](https://kubernetes.io/docs/concepts/workloads/pods/ephemeral-containers/) for interactive troubleshooting. A KoolKit will be pulled by kubectl debug, spun up as a container in your pod, and have the ability to access the same process namespace as your original container.

Since production containers are usually [rather bare](https://cloud.google.com/architecture/best-practices-for-building-containers#remove_unnecessary_tools), using a KoolKit enables you to troubleshoot with power tools instead of relying on what was left behind due to the generosity (or carelessness) of whoever originally built the production image.

The tools in each KoolKit were carefully selected, and you can read more about the motivation behind this entire project [below](https://lightrun.com/debugging/koolkits-debugging-toolkits-for-kubernetes/#the-motivation-behind-koolkits).

If you just want to take a look at the good stuff, feel free to check out the full project [on GitHub](https://github.com/lightrun-platform/koolkits).

## Debugging Kubernetes is Hard

It's not trivial to understand what's going on inside a Kubernetes pod.

First of all, your application is not a single entity anymore – it is comprised of multiple pods, replicated for horizontal scaling, and sometimes even scattered across multiple clusters.

Furthermore, to access your application with local tools (like debuggers) you need to deal with pesky networking issues like discovery and port forwarding, which slows down the use of such tools.

And, the crown jewel of the distributed systems world-altering the state of or completely halting the running pod (e.g. when placing a breakpoint) might cause cascading failures in other parts of your system, which will exacerbate the existing problem.

## The Motivation Behind KoolKits

Lightrun was built with Kubernetes in mind – we work across multiple pods, multiple clusters, and even multiple clouds. We understood early on that packing a punch by using the right tools is a great source of power for the troubleshooting developer – and we figured we'd find a way to give back to the community somehow – and that's how we came up with the idea for KoolKits.

Let's dive deep for a second to explain why KoolKits can be pretty useful:

There's a [well-known Kubernetes best practice](https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-how-and-why-to-build-small-container-images) that states that one should build small container images. This makes sense for a few different reasons:

1. Building the image will consume less resources (aka CI hours)
2. Pulling the image will take less time (who wants to pay for so much ingress anyways?)
3. Less stuff means less surface area exposed to security vulnerabilities, in [a world where even no-op logging isn't safe anymore](https://en.wikipedia.org/wiki/Log4Shell)

There's also a lot of tooling in existence that helps you get there without doing too much heavy lifting:

1. [Alpine Linux](https://hub.docker.com/_/alpine) base images are super small
2. [DistroLess Docker images](https://github.com/GoogleContainerTools/distroless) go a step further and remove everything but the runtime
3. [Docker multi-stage builds](https://docs.docker.com/develop/develop-images/multistage-build/) help create thin final production images

The problem starts when you're trying to debug what's happening inside those containers. By using a small production image you're forsaking a large amount of tools that are **invaluable** when wrapping your head around a problem in your application.

By using a KoolKit, you're allowing yourself the benefits of a small production image without compromising on quality tools – each KoolKit contains hand-picked tools for the specific runtime it represents, in addition to a more generic set of tooling for Linux-based systems.

P.S. KoolKits was inspired by [kubespy](https://github.com/huazhihao/kubespy) and [netshoot](https://github.com/nicolaka/netshoot).

## Considerations

There's quite a few decisions we made during the construction of these images – some things we took into consideration are listed below.

### Size of Images

KoolKits Docker images tend to run, uhm, rather **large**.

KoolKits are intended to be downloaded once, kept in the cluster's Docker registry, and then spun up immediately on demand as containers. Since they're not intended for constant pulling, and since they're intended to be packed with goodies, this is a side effect we're willing to endure.

### Using Ubuntu base images

Part of the reason it's hard to create a really slim image is due to our decision to go with a full Ubuntu 20.04 system as the basis for each KoolKit. This mainly came from our desire to replicate the same environment you would debug with locally inside your clusters.

For example, this means no messing around with Alpine alternatives to normal Ubuntu packages you're used to working with. Actually, this means we have a way of including tools that have no Alpine versions in each KoolKit.

### Using language version managers

Each KoolKit uses (wherever possible) a language version manager instead of relying on language-specific distros. This is done to allow you to install older runtime versions easily, and in order to allow you to swap between runtime versions at will (for example, to get specific versions of tooling that only exist for specific runtime versions), as need be.

## Available KoolKits

Each of the folders in the repo contains the Dockerfile behind the KoolKit and a short explanation of the debug image. All KoolKits are based on the [ubuntu:20.04](https://hub.docker.com/layers/ubuntu/library/ubuntu/20.04/images/sha256-57df66b9fc9ce2947e434b4aa02dbe16f6685e20db0c170917d4a1962a5fe6a9?context=explore) base image, since real people need real shells.

The list of available KoolKits:

1. [koolkit-jvm](https://github.com/lightrun-platform/koolkits/blob/main/jvm/README.md) – AdoptOpenJDK 17.0.2 \& related tooling (including jabba for easy version management and Maven 3.8.4)
2. [koolkit-node](https://github.com/lightrun-platform/koolkits/blob/main/node/README.MD) – Node 16.13.1 \& related tooling (including nvm for easy version management)
3. [koolkit-python](https://github.com/lightrun-platform/koolkits/blob/main/python/README.md) – Python 3.10.2 \& related tooling (including pyenv for easy version management)

Note that you don't actually have to build them yourselves – all KoolKits are hosted publicly on Docker Hub and available free of charge.

## KoolKits Coming up

* A whole new, Go 1.17.7 KoolKit
* JVM KoolKit – [jvm-profiler](https://github.com/uber-common/jvm-profiler), [jHiccup](https://github.com/giltene/jHiccup) support
* Node.js KoolKit – [llnode](https://github.com/nodejs/llnode), [thetool](https://github.com/sfninja/thetool) support
* Python KoolKit – [vardbg](https://github.com/CCExtractor/vardbg), [memprof](https://github.com/jmdana/memprof) support

## Contribution

We'd be more than happy to add tools we missed to any image – just [open a pull request](https://github.com/lightrun-platform/koolkits/pulls) or [an issue](https://github.com/lightrun-platform/koolkits/issues) to suggest one.
