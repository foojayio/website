---
title: "The Road to Docker Official Images for Java: The Azul Zulu Story"
date: "2026-04-24T08:57:12+00:00"
description: "Previously in this series: Trusted Java Containers: Azul Zulu OpenJDK Joins Docker’s Official Images After more than two years of collaboration, reviews,…"
canonical: "https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/"
authors:
  - "frankdelporte"
image: "azul-docker-official.avif"
categories:
  - "Cloud"
  - "Developer Tools"
  - "Java"
  - "Java Core"
related_posts:
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container"
  - "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
frozen: false
---

*Previously in this series:*

* *[Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)*

After more than two years of collaboration, reviews, and back-and-forth with the Docker team, Azul Zulu Builds of OpenJDK have officially joined [Docker's Official Images](https://hub.docker.com/_/azul-zulu) program. This is not just a badge of honor. It changes how Java developers can pull and trust their container base images.

The formal announcement is on the [Azul blog](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/). In this post, I'll walk you through what happened, why it matters, and how to start using these images today.

## What Are Docker Official Images, and Why Does It Matter?

Docker Official Images are a curated set of base images maintained directly in the [docker-library/official-images](https://github.com/docker-library/official-images) repository. They are reviewed by Docker staff, kept up to date with security patches, and available without any namespace prefix, so you can pull `azul-zulu` directly instead of `azul/zulu-openjdk`.

For enterprise teams, this distinction matters quite a bit. Official images go through a structured review process, are continuously scanned for vulnerabilities, and you know they are maintained and where they come from. When you base your production containers on an image, you want to know it is maintained and secure.

## The Journey: Two Years in the Making

The [pull request to add Zulu images](https://github.com/docker-library/official-images/pull/16141) was first opened in January 2024, with the actual commits dating back to July 2023. It was finally merged on March 17, 2026.

That's a long time, but it shows how Docker takes the review process seriously for official images. The accompanying [documentation pull request](https://github.com/docker-library/docs/pull/2320) went through its own review cycle in parallel and got merged together with the images.

The review covers things like:

* Image build reproducibility
* Update cadence and maintenance commitment
* Documentation completeness
* Tag naming conventions and tagging policy
* Security posture of the base image

For Azul, this also meant committing to support **all Java versions**, not just the Long Term Support (LTS) releases. Many developers test against upcoming versions, write feature-preview code, and get their codebases ready for the next LTS before it ships. Supporting Short Term Support (STS) releases alongside LTS means developers working with preview features or upcoming versions are also covered.

## How Azul Zulu Official Images Are Built and Published

Getting a new Java release into Docker's Official Images program is not as simple as pushing a Dockerfile. There is a pipeline behind every image that makes sure what lands on Docker Hub has been validated before anyone runs a `docker pull`.

### Pre-release validation

The process starts before the Java release is even public. Azul pre-builds the images internally and runs validation against the upcoming release, so they are ready to go live the moment the actual Java release drops.

### PM approval and promotion

When a Java release is ready for publication, a Product Manager reviews and approves it before anything is promoted. Only after that approval, the pre-tested Dockerfiles get published to Azul's public image repository at [github.com/AzulSystems/azul-zulu-images](https://github.com/AzulSystems/azul-zulu-images). This repository is the source of truth for all Azul Zulu Dockerfiles. You can browse it to see exactly what goes into each image.

### Submitting to Docker Hub

Publishing to Docker's Official Images program is done via a pull request. Azul's team submits the proposed change to the [docker-library/official-images](https://github.com/docker-library/official-images) repository via a fork at [github.com/AzulSystems/official-images](https://github.com/AzulSystems/official-images). Docker's team then reviews the change and, once approved, takes responsibility for building the final official images on their infrastructure. Azul provides the Dockerfiles and the validation while Docker builds and serves the images.

This separation of responsibilities is intentional and is part of what makes the Official Images program trustworthy. Azul cannot just push an image to Docker Hub without Docker's review and build process sitting in between.

## What's Changing: From Docker Hub to Docker Official Images

Previously, Azul's Docker images were available at [hub.docker.com/r/azul/zulu-openjdk](https://hub.docker.com/r/azul/zulu-openjdk), offering variants based on Debian, Ubuntu, Alpine, CentOS, and distroless. Those images served many users well and will remain available for now, but they are **planned for deprecation later in 2026** as the official images mature and cover the same ground.

The official images start with a limited set of variants and will be expanded over time. If you need a specific combination, like a particular Java version with a particular base OS, head to the [AzulSystems/azul-zulu-images](https://github.com/AzulSystems/azul-zulu-images) repository and create a ticket. The team is actively prioritizing variants based on community demand.

## More Options for Azul Customers

The Docker Official Images are based on Community Availability (CA) builds. These are free, open, and available to everyone. If you are an Azul customer, you also have access to Subscriber Availability (SA) images, which go a step further.

SA images are hosted on Azul's private registry and are also available as Chainguard Containers and include:

* Security updates going beyond what the community releases provide
* Extended platform and configuration support
* Priority access to critical patches
* A choice between CPU (security-only) and PSU (full quarterly) update tracks

Check the documentation about running Azul Zulu SA in a [Docker Container](https://docs.azul.com/core/install/linux-sa-docker) or [Chainguard Container](https://docs.azul.com/core/install/linux-sa-docker-chainguard). In a future post in this series, I will explain all available Zulu Container options.

## Conclusion

Getting Azul Zulu into Docker's Official Images program took over two years, but the result is worth it. These images are now reviewed, scanned, and maintained the way you would expect for production workloads. Whether you are starting fresh or moving an existing project, `FROM azul-zulu` is now the trusted starting point for your Java containers.

The old `azul/zulu-openjdk` images on Docker Hub will stick around for a while, but the migration path is straightforward, so now is a good time to make the switch. Start using the official images, and if you need a variant that is not yet available, open an issue at [AzulSystems/azul-zulu-images](https://github.com/AzulSystems/azul-zulu-images) to request it.

In the next post, I will show you a few examples of how you can use these Official images to build a container with your application.
