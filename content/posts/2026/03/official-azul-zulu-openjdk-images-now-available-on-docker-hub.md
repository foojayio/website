---
title: "Azul Zulu OpenJDK Docker Images Now Available on Docker Hub"
slug: "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
date: "2026-03-23T07:53:56+00:00"
lastmod: "2026-03-23T08:01:59+00:00"
description: "Azul Zulu OpenJDK is now available as a Docker Official Image, giving developers secure, signed, and automatically updated Java containers across multiple versions directly from Docker Hub."
authors:
  - "dominika-tasarz"
image: "https://foojay.io/wp-content/uploads/2026/03/Docker-Azul-Zulu-Blog-Image-40-1024x576.png"
categories:
  - "DevOps"
  - "Java"
  - "Java Beginner"
tags:
related_posts:
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container"
  - "the-road-to-docker-official-images-for-java-the-azul-zulu-story"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
enlighterjs: true
frozen: false
---

[Azul recently announced that](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/ "Azul recently announced that") Azul Zulu Builds of OpenJDK are now available as Docker Official images on Docker Hub. That means you can pull TCK‑verified, fully compliant OpenJDK builds directly from the same Official Images library you already trust for your base OS and databases.

You can browse and pull the images here:  
<https://hub.docker.com/_/azul-zulu>

Why should you care about official images? {#h2-0-why-should-you-care-about-official-images}
--------------------------------------------------------------------------------------------

With the official Docker images you get:

* Verified cryptographic signing using Azul's GPG key, so you can validate what your CI pulls.
* Automatic rebuilds whenever upstream base images are patched, so CVEs are addressed without you manually rebuilding everything.
* Fully open‑source licensing (GPL v2 with Classpath Exception plus relevant OpenJDK licenses).
* Images built and maintained to Docker's security and maintenance standards, rather than ad‑hoc community images.

This is especially useful if you are:

* Standardizing Java base images across multiple services.

* Locking down a software supply chain (SBOMs, signing, provenance).

* Reducing the noise from ad‑hoc "java:latest" images in different teams.

Which versions and variants can you use? {#h2-1-which-versions-and-variants-can-you-use}
----------------------------------------------------------------------------------------

The Azul Zulu Official Images cover multiple Java releases, including the key LTS versions many teams already rely on:

* Java 8
* Java 11
* Java 17
* Java 21
* Java 25

Each version comes with:

* `-jdk`, `-jre`, and `-headless` variants per major version.
* `-debian` and `-debian13` suffix tags so you can pin the base image.

Example tags include:

* `azul-zulu:17-jdk-debian`
* `azul-zulu:21-jre-headless-debian13`

This is just the start - more tags and base images are planned. As new Java releases and additional base images are introduced, they are expected to show up under the same Official Images namespace so you can keep a consistent pattern across services.

Where to start? {#h2-2-where-to-start}
--------------------------------------

Getting the images into your workflow should be straightforward if you already use Docker for Java.

Basic steps:

1. Go to the Official Images page: <https://hub.docker.com/_/azul-zulu>.

2. Pick the Java version and variant that matches your service (e.g. `17-jdk-debian13`).

3. Pull it locally:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker pull azul-zulu:17-jdk-debian13
</pre>

4. Use it in your Dockerfile:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">FROM azul-zulu:17-jdk-debian13

WORKDIR /app
COPY target/app.jar app.jar
CMD ["java", "-jar", "app.jar"]
</pre>

5. Wire it into your CI/CD templates so all new services share the same trusted base.

For more details, Dockerfiles and tag information, [check the GitHub repo.](https://github.com/AzulSystems/azul-zulu-images "check the GitHub repo.")

What's next and how to stay involved? {#h2-3-what-s-next-and-how-to-stay-involved}
----------------------------------------------------------------------------------

This is just the beginning, here's what's coming next:

* Additional Java releases (including future STS and LTS versions) under the same Official Images namespace.

* More base images over time, so you can choose the footprint and OS family that fits your stack.

* Continued automatic rebuilds and security updates to keep your containers current with minimal noise.

Azul Zulu OpenJDK Official Images are designed to make running Java in containers as straightforward and trustworthy as possible. By standardizing on these images, you get a consistent, signed and actively maintained base that fits naturally into modern CI/CD workflows.

[Download from Docker Hub](https://hub.docker.com/_/azul-zulu "Download from Docker Hub")
