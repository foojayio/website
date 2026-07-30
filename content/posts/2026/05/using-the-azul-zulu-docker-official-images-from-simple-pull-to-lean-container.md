---
title: "Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container"
slug: "using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container"
date: "2026-05-08T09:05:22+00:00"
lastmod: "2026-05-08T12:32:12+00:00"
description: "Previously in this series: Trusted Java Containers: Azul Zulu OpenJDK Joins Docker’s Official Images The Road to Docker Official Images for Java: The Azul - by Frank Delporte"
canonical: "https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/04/zulu-lean-container.avif"
categories:
  - "Cloud"
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "the-road-to-docker-official-images-for-java-the-azul-zulu-story"
  - "building-java-containers-without-a-dockerfile-azul-zulu-and-paketo-buildpacks"
  - "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
enlighterjs: true
frozen: false
---

*Previously in this series:*

* *[Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)*
* *[The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)*

[Azul Zulu is now a Docker Official Image](https://hub.docker.com/_/azul-zulu), so getting a trusted, secure Java base image is now just a `FROM azul-zulu` line in your Dockerfile. But pulling the right image is just the beginning. This post shows you how to use these images in practice, and how a few extra steps can seriously cut the size of what you ship.
![](/images/posts/2026/05/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/azul-docker-official-1024x576.avif)

How to Use Azul Zulu as Docker Official Images {#h-how-to-use-azul-zulu-as-docker-official-images}
--------------------------------------------------------------------------------------------------

The images are available on Docker Hub under the `azul-zulu` name. You can pull them directly, for instance, with the following commands:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker pull azul-zulu:21
docker pull azul-zulu:21-jre
docker pull azul-zulu:25</pre>

Or you can pull and run one to check the version. This is a good first sanity check when evaluating a new image variant. You should see the following output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker run --rm azul-zulu:25 java -version
openjdk version "25.0.2" 2026-01-20 LTS
OpenJDK Runtime Environment Zulu25.32+21-CA (build 25.0.2+10-LTS)
OpenJDK 64-Bit Server VM Zulu25.32+21-CA (build 25.0.2+10-LTS, mixed mode, sharing)</pre>

Practical Examples: Building Lean Containers {#h-practical-examples-building-lean-containers}
---------------------------------------------------------------------------------------------

Getting an official, trusted base image is step one. The next step is making sure you're not shipping more than you need. The [Sustainability for Java Developers](https://foojay.io/today/announcing-sustainability-for-java-developers-a-new-collaborative-guide-from-the-foojay-io-community/) ebook (published by the Foojay.io community) has an excellent chapter by Jan Ouwens and Ko Turk walking through exactly this problem. Based on their examples, I created a [repository on GitHub](https://github.com/FDelporte/azul-docker-demo) with a minimal application and multiple example Dockerfiles. The POM file includes the plugins to build a runnable JAR.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package be.webtechie;  

public class Main {  
    public static void main(String[] args) {  
        System.out.println("Hello and welcome!");  
    }  
}</pre>

Let's package this app and check the JAR size:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn package
$ ls -lh target 
-rw-r--r--@ 1 frank  staff   2.2K Mar 23 15:41 azul-docker-demo-1.0-SNAPSHOT.jar</pre>

So our application, compiled with Java 25, produces a JAR file that is only 2.2KB. Let's keep this in mind when comparing it to the Docker container size in the following steps...

### Everything in One Container (Don't Do This) {#h-everything-in-one-container-don-t-do-this}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># dockerfile-full
FROM azul/zulu-openjdk:25

RUN mkdir /app
COPY . /app
WORKDIR /app
RUN apt-get update &amp;&amp; apt-get install -y maven
RUN mvn package

ENTRYPOINT ["java", "-jar", "target/azul-docker-demo-1.0-SNAPSHOT.jar"]</pre>

Using this file, we can create a Docker image with the following command and check the size.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker build -f dockerfile-full -t azul-docker-demo-full .

$ docker images
REPOSITORY              TAG       IMAGE ID       CREATED          SIZE
azul-docker-demo-full   latest    cf769e50f1db   52 seconds ago   568MB

$ docker run azul-docker-demo-full
Hello and welcome!</pre>

This ships your entire build environment, code, Maven, a full JDK, and all the OS tooling alongside your 2.2 KB jar. The resulting image is 568 MB.

### Multi-Stage Build with JRE {#h-multi-stage-build-with-jre}

A multi-stage build separates "what you need to build" from "what you need to run":

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># dockerfile-jre
FROM azul-zulu:25 AS build

RUN mkdir /app
COPY . /app
WORKDIR /app
RUN apt-get update &amp;&amp; apt-get install -y maven &amp;&amp; mvn package

FROM azul-zulu:25-jre

RUN mkdir /app
COPY --from=build /app /app
WORKDIR /app

ENTRYPOINT ["java", "-jar", "target/azul-docker-demo-1.0-SNAPSHOT.jar"]</pre>

Maven and the JDK stay in the build stage. The runtime image includes only your JAR and the Java Runtime Environment (JRE), a stripped-down version of the JDK containing only the tools needed to run applications. Check the blog post [The Anatomy of a JVM](https://www.azul.com/blog/the-anatomy-of-a-jvm/) to learn more about the differences between JDK, JRE, and the Java Virtual Machine (JVM).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker build -f dockerfile-jre -t azul-docker-demo-jre .

$ docker images
REPOSITORY              TAG       IMAGE ID       CREATED          SIZE
azul-docker-demo-jre    latest    16a99e205da7   40 seconds ago   370MB

$ docker run --rm azul-docker-demo-jre
Hello and welcome!</pre>

This brings the container size down to **370 MB**, and you are no longer shipping your code, Maven, and a full JDK to your production environment or users.

### Using jdeps and jlink to Create a Custom Runtime {#h-using-jdeps-and-jlink-to-create-a-custom-runtime}

The JRE still includes every Java module, including the ones your application doesn't need. `jdeps` can tell you exactly which modules your application needs, and `jlink` can assemble a minimal runtime containing only those:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># dockerfile-jlink
FROM azul-zulu:25 AS build

RUN mkdir /app
COPY . /app
WORKDIR /app
RUN apt-get update &amp;&amp; apt-get install -y maven binutils

RUN mvn package \
 &amp;&amp; jdeps --ignore-missing-deps -q --recursive \
          --multi-release 25 \
          --print-module-deps target/azul-docker-demo-1.0-SNAPSHOT.jar &gt; deps.info \
 &amp;&amp; jlink \
          --add-modules "$(cat deps.info)" \
          --strip-debug \
          --compress zip-6 \
          --no-header-files \
          --no-man-pages \
          --output /myjre

FROM debian:stable-slim

ENV JAVA_HOME=/usr/java/jdk25
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=build /myjre $JAVA_HOME
RUN mkdir /app
COPY --from=build /app /app
WORKDIR /app

ENTRYPOINT ["java", "-jar", "target/azul-docker-demo-1.0-SNAPSHOT.jar"]</pre>

The extra steps with `jdeps` and `jlink` make the Dockerfile a bit more complex, but the pattern is a one-time setup you can reuse across projects. Now let's build the container, and check the size:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker build -f dockerfile-jlink -t azul-docker-demo-jlink .

$ docker images
REPOSITORY              TAG       IMAGE ID       CREATED          SIZE
azul-docker-demo-jlink  latest    4fc3bb4dec14   35 seconds ago   141MB

$ docker run --rm azul-docker-demo-jlink
Hello and welcome!</pre>

Result: around **141 MB**. You're now shipping a tailor-made Java runtime with only the modules your application actually needs, nothing else.

Note for Spring Boot and Hibernate users: These frameworks rely heavily on reflection and classpath scanning. This means `jdeps` may not capture all required modules. You'll likely need to add some modules manually and test carefully before using this approach in production.

Size Comparison Summary {#h-size-comparison-summary}
----------------------------------------------------

|           Approach           | Approximate Image Size |
|------------------------------|------------------------|
| Build + runtime in one image | \~560 MB               |
| Multi-stage, JRE runtime     | \~370 MB               |
| JLink custom runtime         | \~140 MB               |

Every step down this table means a faster pull, less storage, a smaller attack surface, and a smaller carbon footprint! As Jan Ouwens and Ko Turk put it in the Sustainability book: "***Whatever you do, don't ship a 500MB Docker image for a 2.2KB jar file.***"

What to Do Next {#h-what-to-do-next}
------------------------------------

**Start using the official images.** For new projects, replace `azul/zulu-openjdk` references with `azul-zulu` in your Dockerfiles. For existing projects, plan the migration before the old images are deprecated later in 2026.

**Request variants.** The official images will grow to cover more Java versions and base OS combinations. If you need something specific, like a particular version, a distroless variant, or a different architecture, open a ticket at [AzulSystems/azul-zulu-images](https://github.com/AzulSystems/azul-zulu-images).

**Think about your image size.** If you're still running `FROM azul-zulu:21` in a single-stage build without jlink or multi-stage separation, now is a good time to revisit. Run the comparison yourself with your own application code and the example Dockerfiles in the [GitHub repository](https://github.com/FDelporte/azul-docker-demo).

**Keep an eye on the next post in this series**, where I will walk you through every way to containerize Azul Zulu. From the free Community Availability (CA) images to the commercially supported Subscriber Availability (SA) builds and the Chainguard variant for the most security-conscious environments.

*** ** * ** ***

*The Sustainability for Java Developers book, which inspired the Docker image examples in this post, is a free ebook published through [foojay.io](https://foojay.io/today/announcing-sustainability-for-java-developers-a-new-collaborative-guide-from-the-foojay-io-community/). Chapter 6 by Jan Ouwens and Ko Turk covers container optimization in depth alongside other practical sustainability improvements.*
