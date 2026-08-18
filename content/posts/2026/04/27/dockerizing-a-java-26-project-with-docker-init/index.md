---
title: "Dockerizing a Java 26 Project with Docker Init"
slug: "dockerizing-a-java-26-project-with-docker-init"
date: "2026-04-27T09:55:12+00:00"
description: "Java 26 came out in March 2026. This article walks you through Dockerizing a Java 26 Spring Boot project using Docker Init."
canonical: "https://www.dockersecurity.io/blog/dockerize-java-26-with-docker-init"
authors:
  - "aerabi"
image: "asgard-init-1024x765.png"
categories:
  - "DevOps"
  - "Java"
  - "Java Beginner"
tags:
related_posts:
  - "starting-docker-desktop-with-spring-boot"
  - "building-java-containers-without-a-dockerfile-azul-zulu-and-paketo-buildpacks"
  - "intro-to-the-boxlang-formatter"
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
enlighterjs: true
frozen: false
---

Docker Init was introduced in Docker Desktop 4.27, before LLMs became the default answer to everything. It's a "smart" interactive wizard that analyzes your project and generates:

* A `Dockerfile` (multi-stage, production-ready)

* A `compose.yaml` file

* A `.dockerignore` file

* A `README.Docker.md` with build and run instructions

What makes it valuable is that it's deterministic---not a probabilistic guess. It produces the same correct output every time, following Docker's own best practices.

![Docker Commandos setting up the command center](https://dockersecurity.io/commandos-asgard/asgard-init.png)

Technical Requirements
----------------------

* Docker Desktop 4.27 or later

Create a New Project
--------------------

I'm using a Spring Boot project. Because it's early Spring now and I haven't touched one in a while---so let's go.

Head to <https://start.spring.io/> and create a project with:

* **Project:** Maven

* **Language:** Java

* **Spring Boot:** 4.0.5 **(or whatever the latest stable is)**

* **Packaging:** Jar

* **Java:** 26

I used these coordinates, but pick your own:

* **Group:** io.dockersecurity

* **Artifact:** hello-wowlrd

* **Package Name:** io.dockersecurity.hello-wowlrd

Download, unzip, and step into the directory:

```bash
cd hello-wowlrd
```


Run Docker Init
---------------

As my British friend say, "It's Docker, innit?"

```bash
docker init
```


The interactive wizard detects your Java project automatically. Accept "Java", confirm the source directory and Java version, and enter the port:

```
? What application platform does your project use? Java
? What's the relative directory (with a leading .) for your app? ./src
? What version of Java do you want to use? 26
? What port does your server listen on? 8080
```


Docker Init generates four files. The one that matters most is the `Dockerfile`:

```dockerfile
# syntax=docker/dockerfile:1

################################################################################
# Stage 1: resolve and download dependencies
FROM eclipse-temurin:26-jdk-jammy as deps

WORKDIR /build

COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests

################################################################################
# Stage 2: build the application
FROM deps as package

WORKDIR /build

COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

################################################################################
# Stage 3: extract Spring Boot layers
FROM package as extract

WORKDIR /build

RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

################################################################################
# Stage 4: minimal runtime image
FROM eclipse-temurin:26-jre-jammy AS final

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

COPY --from=extract build/target/extracted/dependencies/ ./
COPY --from=extract build/target/extracted/spring-boot-loader/ ./
COPY --from=extract build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract build/target/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]
```


This is already a proper multi-stage build: separate stages for dependency resolution, compilation, layer extraction, and a minimal runtime image with a non-root user. Gord would approve.

A Note on Java 26 Base Images
-----------------------------

The generated Dockerfile references `eclipse-temurin:26-jdk-jammy` and `eclipse-temurin:26-jre-jammy`. Since Java 26 was just released, these Eclipse Temurin images may not be fully available on Docker Hub yet.

Swap them out for SAP Machine images instead---SAP's free OpenJDK distribution ships Java 26 on Ubuntu 24.04 (Noble Numbat):

* `sapmachine:26-jdk-ubuntu-noble`

* `sapmachine:26-jre-ubuntu-noble`

Find them on Docker Hub: <https://hub.docker.com/_/sapmachine>. Just replace `eclipse-temurin` with `sapmachine` in both `FROM` lines.

Build and Run
-------------

```bash
docker compose up --build
```


The generated `compose.yaml` is minimal:

```yaml
services:
  server:
    build:
      context: .
    ports:
      - 8080:8080
```


The application starts, and immediately stops with exit code 0. That's expected: there's no HTTP endpoint to keep it alive.

Add a Controller
----------------

Create `src/main/java/io/dockersecurity/hellowowlrd/HelloController.java`:

```java
package io.dockersecurity.hellowowlrd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, Docker Security!";
    }
}
```


Add the Spring Web dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```


Build and run again:

```bash
docker compose up --build
```


Verify:

```bash
curl http://localhost:8080
# Hello, Docker Security!
```


More Links
----------

Docker Init supports more than Java. If you want to try it with other languages, Docker's official guides are the place to start: <https://docs.docker.com/guides/>.

I co-authored the C++ guide---Docker thanked me for it at the top of the page, which means I wrote those words and then thanked myself on their behalf. Worth a read:

* <https://docs.docker.com/guides/cpp/>

Conclusion
----------

Java 26 just shipped and Docker Init handles it cleanly out of the box---multi-stage build, layer extraction, non-root user, bind mounts for caching. You get a production-ready Dockerfile in under a minute. When Eclipse Temurin catches up, swap the base images back. Until then, SAP Machine has you covered.

If you want to learn about more Docker Commandos (or commands) head to: <https://dockersecurity.io/commandos>.
