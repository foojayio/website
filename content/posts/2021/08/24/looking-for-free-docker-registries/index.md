---
title: "Looking For Free Docker Registries"
date: "2021-08-24T07:19:37+00:00"
lastmod: "2021-08-24T07:19:40+00:00"
description: "Since Docker announced that it would remove unused images from Docker Hub, I've been interested in listing places where I could host mine!"
canonical: "https://blog.frankel.ch/free-docker-registries/"
authors:
  - "nicolas-frankel"
image: "business-1845350_1280.jpg"
categories:
  - "DevOps"
related_posts:
  - "starting-docker-desktop-with-spring-boot"
  - "dockerizing-a-java-26-project-with-docker-init"
  - "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
  - "run-a-java-lambda-function-from-a-docker-image"
frozen: false
---

Since Docker announced that it would remove unused images from Docker Hub, I've been interested in listing places where I could host mine.

As it's for personal purposes, I'm interested in free plans. Here's what I found:

|                                       Provider                                       |             Private?              |                                 Monthly limit                                  ||                                      Details                                      |
|                                       Provider                                       |             Private?              | Storage |                               Transfer                                |                                      Details                                      |
|--------------------------------------------------------------------------------------|-----------------------------------|---------|-----------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| [Docker Hub](https://www.docker.com/pricing)                                         | 1                                 | ❓       | * Anonymous: 100 pulls / 6 hours * Authenticated: 200 pulls / 6 hours |                                                                                   |
| [GitLab](https://docs.gitlab.com/ee/user/packages/container_registry/)               | ✅                                 | 10Gb    | ❓                                                                     | * Storage quota is per-project * Quota includes **all** artifacts, including code |
| [GitHub](https://docs.github.com/en/packages/guides/about-github-container-registry) | ✅                                 | > GitHub Container Registry is currently in public beta and subject to change. During the beta, storage and bandwidth are free                                    |||
| [AWS](https://aws.amazon.com/ecr/pricing/)                                           | * First year only * 500Mb storage | 50Gb    | * Anonymous: 500Gb * Authenticated: 5Tb                               |                                                                                   |
| [IBM Cloud](https://cloud.ibm.com/registry/catalog)                                  | ✅                                 | 500Mb   | 5Gb                                                                   |                                                                                   |

Here are some additional options with their associated pricing model:

|                                    Provider                                    |                                   Pricing                                   |
|--------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| [JFrog](https://jfrog.com/pricing/)                                            | * Can be hosted on Google Cloud, AWS or Azure * Pricing depends on provider |
| [Google](https://cloud.google.com/storage/pricing/)                            | * Storage * Transfer                                                        |
| [Azure](https://azure.microsoft.com/en-us/pricing/details/container-registry/) | * Storage * Build time (?)                                                  |
| [Quay](https://quay.io/plans/)                                                 | * Monthly subscription * Number of private repositories                     |

*Originally published at [A Java Geek](https://blog.frankel.ch/free-docker-registries/) on August 22^nd^, 2021*
