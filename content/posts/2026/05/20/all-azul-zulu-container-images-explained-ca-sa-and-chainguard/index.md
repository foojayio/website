---
title: "All Azul Zulu Container Images Explained: CA, SA, and Chainguard"
slug: "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
date: "2026-05-20T09:56:00+00:00"
description: "Previously in this series: Trusted Java Containers: Azul Zulu OpenJDK Joins Docker’s Official Images The Road to Docker Official Images for Java: The Azul - by Frank Delporte"
canonical: "https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/"
authors:
  - "frankdelporte"
image: "azul-zulu-ca-sa-chainguard.avif"
categories:
  - "Cloud"
  - "Java"
tags:
related_posts:
  - "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
  - "the-road-to-docker-official-images-for-java-the-azul-zulu-story"
  - "using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
enlighterjs: true
frozen: false
---

*Previously in this series:*

* *[Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)*
* *[The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)*
* *[Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/)*

The launch of [Azul Zulu as Docker Official Images](https://hub.docker.com/_/azul-zulu) was a milestone, but it's just one of several ways to get Azul Zulu Builds of OpenJDK in a container. Depending on whether you're evaluating Azul Zulu, running it in production under a support contract, or operating in a high-security environment, the right image source will vary. In this post, I'll show you all the possibilities.
![](azul-zulu-ca-sa-chainguard-1024x576.avif)

## First: Understanding Community vs. Subscriber Availability

Before looking at what's available where, it helps to understand the two flavors of Azul Zulu Builds of OpenJDK (Zulu):

**Community Availability (CA)** builds are free to download and use: no account, no token, no license agreement beyond the open-source terms. These are what you get on Docker Hub and what the new Docker Official Images are based on. They're a great fit for evaluation, development, open-source projects, and any deployment where you don't need a commercial support contract.

**Subscriber Availability (SA)** builds are commercially supported, tested, and certified releases exclusively available to Azul customers. On top of everything CA provides, SA adds:

* Security updates beyond what the community releases
* Extended platform and configuration support
* Priority access to critical patches
* CPU and PSU release tracks (more on those below)
* Access to Azul Support engineers averaging 20+ years of Java experience

If you're running Java in production and need guaranteed support when something breaks, SA is the right choice.

## Getting Zulu CA in a Container

You can get Community Availability images from two sources, as explained below and with more details in the Azul Documentation in [Running Azul Zulu CA in a Docker Container](http://docs.azul.com/core/install/linux-ca-docker). The full list of available CA tags is also documented on [Zulu CA Docker Tags](https://docs.azul.com/core/docker-image-tags#ca-docker-tags) and [Zulu CA Docker Official Tags](https://docs.azul.com/core/docker-image-tags#ca-docker-official-tags).

### Docker Official Images (new)

The newest addition: `azul-zulu` is now a [Docker Official Image on Docker Hub](https://hub.docker.com/_/azul-zulu). These images don't have a namespace prefix, are maintained by Azul, get reviewed by Docker, and are continuously scanned for CVEs.

```
docker pull azul-zulu:21
docker pull azul-zulu:21-jre
docker pull azul-zulu:25
```

This is the recommended starting point for any new project. Full details and example usage are explained in the previous posts of this blog post series (**s**ee links above).

### Legacy Docker Hub Images (`azul/zulu-openjdk-*`)

The original Azul images on Docker Hub are still available and cover a wider range of base OS combinations:

|             Image              |   Base OS    |
|--------------------------------|--------------|
| `azul/zulu-openjdk`            | Ubuntu       |
| `azul/zulu-openjdk-debian`     | Debian       |
| `azul/zulu-openjdk-alpine`     | Alpine Linux |
| `azul/zulu-openjdk-centos`     | CentOS       |
| `azul/zulu-openjdk-distroless` | Distroless   |

Example usage:

```
docker pull azul/zulu-openjdk:21
docker run --rm azul/zulu-openjdk:21 java -version
```

These images will be **deprecated later in 2026** as the Official Images expand to cover the same variants. If you're starting something new today, prefer `azul-zulu`. If you're using the old images, plan your migration.

## Getting Zulu SA in a Container

Subscriber Availability images are delivered through two channels: Azul's own private registry, and Chainguard's registry.

### Azul Customer Registry

SA images are hosted on Azul's private container registry and require authentication with an access token. Azul customers can manage their access tokens through [access.azul.com](https://access.azul.com).

```
docker login sa.registry.azul.com
# Username: <your-ftp-username>
# Password: <your-access-token>

docker run -it sa.registry.azul.com/zulu-sa-ubuntu:17-jre
```

The SA registry provides images based on multiple base systems:

|                   Image                   |   Base OS    |
|-------------------------------------------|--------------|
| `sa.registry.azul.com/zulu-sa-ubuntu`     | Ubuntu       |
| `sa.registry.azul.com/zulu-sa-alpine`     | Alpine Linux |
| `sa.registry.azul.com/zulu-sa-debian`     | Debian       |
| `sa.registry.azul.com/zulu-sa-distroless` | Distroless   |
| `sa.registry.azul.com/zulu-sa-rockylinux` | Rocky Linux  |

The full documentation is available on docs.azul.com: [Running Azul Zulu SA in a Docker Container](https://docs.azul.com/core/install/linux-sa-docker).

#### CPU vs PSU: Choosing the Right Update Track

SA builds are available in two update types:

* **CPU** (Critical Patch Updates): Contain fixes to security vulnerabilities and critical bug fixes only. CPU releases are based on prior-cycle PSU releases with security fixes applied, providing a low-risk deployment option for urgent security updates.
* **PSU** (Patch Set Updates): Include all CPU fixes plus additional non-security bug fixes. PSU releases align with OpenJDK quarterly releases, including both security fixes and general improvements.

You can identify the release type from the Zulu minor version in the tag:

* odd minor = CPU, e.g. `21.43`
* even minor = PSU, e.g. `21.44`

#### SA Tagging Scheme

SA images use both fixed version tags (pinned to a specific build) and mutable alias tags (always pointing to the latest of a given type):

**Fixed version tags**: immutable, safe to pin for reproducible deployments:

|        Tag         |                 What you get                 |
|--------------------|----------------------------------------------|
| `21.43-21.0.7`     | Zulu 21.43, OpenJDK 21.0.7, CPU release, JDK |
| `21.43-21.0.7-jre` | Same, JRE only                               |
| `21.44-21.0.8`     | Zulu 21.44, OpenJDK 21.0.8, PSU release, JDK |
| `21.44-21.0.8-jre` | Same, JRE only                               |

**Mutable alias tags**: convenient when you always want to use the latest version, but will pull different content over time:

|     Tag      |      What you get      |
|--------------|------------------------|
| `21-cpu`     | Latest Java 21 CPU JDK |
| `21-cpu-jre` | Latest Java 21 CPU JRE |
| `21-psu`     | Latest Java 21 PSU JDK |
| `21-psu-jre` | Latest Java 21 PSU JRE |

Available Java versions for SA images currently include 8, 11, 17, 21, 25, and 26.

In a `Dockerfile`, you typically use a fixed version tag for production builds to ensure reproducibility, and a mutable alias in development or CI pipelines where you want automatic updates:

```
# Production: pin to an exact build
FROM sa.registry.azul.com/zulu-sa-ubuntu:21.44-21.0.8-jre AS runtime

# CI/development: always get the latest security patch
FROM sa.registry.azul.com/zulu-sa-ubuntu:21-cpu-jre AS runtime
```

For the complete tag listing, see the [Zulu SA Docker Tags](https://docs.azul.com/core/docker-image-tags#sa-docker-tags) in the Azul Documentation.

### Chainguard Images

For environments with strict supply chain security requirements, Azul Zulu SA is also available through [Chainguard's registry](https://www.chainguard.dev/containers). This is part of the Chainguard Images collection, built on [Wolfi](https://edu.chainguard.dev/open-source/wolfi/overview/), a purpose-built Linux undistro designed for minimal, secure containers.

Chainguard Zulu images offer:

* Minimal design, with no unnecessary software included
* Daily builds to ensure container images are up-to-date with available security patches
* [High quality build-time SBOMs](https://edu.chainguard.dev/chainguard/chainguard-images/how-to-use/retrieve-image-sboms/#image-sboms-in-the-chainguard-console) attesting to the provenance of all artifacts within the image
* [Verifiable container image signatures](https://edu.chainguard.dev/chainguard/chainguard-images/how-to-use/verifying-chainguard-images-and-metadata-signatures-with-cosign/#verifying-container-image-signatures)
* Reproducible builds with Cosign and apko ([read more about reproducibility](https://www.chainguard.dev/unchained/reproducing-chainguards-reproducible-image-builds))

```
docker pull cgr.dev/ORGANIZATION/zulu-jdk:latest
docker run -it --rm cgr.dev/ORGANIZATION/zulu-jdk:latest java -version
```

Replace `ORGANIZATION` with your organization's name in the Chainguard registry. Access to the Chainguard registry [requires authentication](https://edu.chainguard.dev/chainguard/chainguard-registry/authenticating/). That way, Chainguard can reach you if something important changes in an image you are using.

You can find more resources at [Chainguard Academy](https://edu.chainguard.dev/), [Chainguard Courses](https://courses.chainguard.dev/), and [Running Azul Zulu SA in a Chainguard Container](https://docs.azul.com/core/install/linux-sa-docker-chainguard) in the Azul Documentation.

## Choosing the Right Image

Here's a quick decision guide:

|                                       If you are...                                       |                       Use...                       |
|-------------------------------------------------------------------------------------------|----------------------------------------------------|
| Evaluating Azul, building open-source projects, or developing locally                     | Docker Official Image (`azul-zulu`)                |
| Running in production, need commercial support or extended patches                        | SA Private Registry (`sa.registry.azul.com`)       |
| In a high-security environment requiring SBOMs, signed images, and minimal attack surface | Chainguard (`cgr.dev`)                             |
| Still using old Docker Hub images                                                         | Migrate to `azul-zulu` before the 2026 deprecation |

CPU versus PSU is a secondary choice for SA users: if your priority is minimizing the risk of change when applying security patches, pin to CPU releases. If you want all quarterly bug fixes alongside security fixes, use PSU.

## What's Missing? Tell Us.

The SA and Official image offerings are actively growing. If you need a specific Java version, base OS combination, or architecture that isn't currently available, create an issue on GitHub in [AzulSystems/azul-zulu-images](https://github.com/AzulSystems/azul-zulu-images). The Azul team is actively prioritizing based on demand.

*Full documentation for each image source:*

* *CA Docker images: [docs.azul.com/core/install/linux-ca-docker](https://docs.azul.com/core/install/linux-ca-docker)*
* *SA Docker images: [docs.azul.com/core/install/linux-sa-docker](https://docs.azul.com/core/install/linux-sa-docker)*
* *SA Chainguard images: [docs.azul.com/core/install/linux-sa-docker-chainguard](https://docs.azul.com/core/install/linux-sa-docker-chainguard)*
* *All image tags: [docs.azul.com/core/docker-image-tags](https://docs.azul.com/core/docker-image-tags)*
