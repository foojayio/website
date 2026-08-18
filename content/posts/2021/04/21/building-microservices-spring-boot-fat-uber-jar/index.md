---
title: "Building Microservices with Spring Boot Fat (Uber) Jar"
date: "2021-04-21T09:28:43+00:00"
lastmod: "2023-08-28T12:47:43+00:00"
description: "Explore the simple steps of building Spring Boot Fat (Uber) Jars of your own project and running it as microservices."
authors:
  - "tetiana-fydorenchyk"
image: "spring-boot-fat.png"
categories:
  - "Jelastic"
  - "Microservices"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "clean-shutdown-of-spring-boot-applications"
  - "containerizing-spring-boot-applications-with-jib"
  - "controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi"
frozen: false
---

In most minds,*microservices* is an approach to make a traditional monolithic system more structured, dividing it into logical components that correspond to different functional areas of application. Thus, acting as a microservice, each component becomes self-contained, easily scaled, maintained and even upgraded without affecting the overall system. Also, with *microservice* architecture, you can use a software written in different programming languages, including Java. Such freedom attracts but may frighten at the same time.

You can spend hours reading numerous articles in the net, regarding how to build *microservices* along with some boring examples. However theory without practice gets nowhere.

For a quick start, we have prepared a package with [Maven](https://maven.apache.org/what-is-maven.html) and popular framework [Spring Boot](https://projects.spring.io/spring-boot/) inside Java Engine node. It automates building a sample Java project as *Fat* *(or so-called Uber) Jar*to run it as a microservice.

![jelastic spring boot microservices tutorial](https://jelastic.com/blog/wp-content/uploads/2018/08/maven-microservice-package.png)

## Installation of Spring Boot Fat (Uber) Jar Builder

To get started, log in to Jelastic dashboard, find the *Spring Boot Fat* *Jar* *Builder* in the **Marketplace** and click **Install**.

![building microservices](https://jelastic.com/blog/wp-content/uploads/2018/08/spring-boot-fat-jar-builder.png)

Or you can **Import** the required manifest using the link from GitHub:

[https://github.com/jelastic-jps/spring-boot/blob/master/microservice-fat-jar/manifest.jps](https://github.com/jelastic-jps/spring-boot/blob/master/microservice-fat-jar/manifest.jps?utm_source=spring-boot-fat)

[![maven archetype spring boot](https://jelastic.com/blog/wp-content/uploads/2018/08/import-fat-jar-manifest.png)](https://github.com/jelastic-jps/spring-boot/blob/master/microservice-fat-jar/manifest.jps?utm_source=spring-boot-fat)

If required, change installation settings such as environment name or Git repository link to a custom Spring Boot project. Then press **Install** *.*

*![java spring boot](https://jelastic.com/blog/wp-content/uploads/2018/08/spring-boot-fat-jar-installation.png)*

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.

![how to build microservices](https://jelastic.com/blog/wp-content/uploads/2018/08/fat-jar-microservice-deployment.png)

Afterwards, you can make sure, that application is up and running by pressing **Open in browser** button.

![spring boot uber jar](https://jelastic.com/blog/wp-content/uploads/2018/08/fat-jar-jelastic-url.png)

## Running Multiple Microservices with Spring Boot Projects

You can use just created Maven node for building extra projects and deploying them to different environments to get a set of distributed microservices.

![spring boot microservices example](https://jelastic.com/blog/wp-content/uploads/2018/08/set-of-microservices-in-maven-node.png)

First of all, create a separate environment with *Java Engine*.

![spring boot maven tutorial](https://jelastic.com/blog/wp-content/uploads/2018/08/java-engine-environment.png)

Then click **Add Project** next to the *Maven* node in the initial environment.

![spring boot maven](https://jelastic.com/blog/wp-content/uploads/2018/08/add-project-to-environment.png)

Specify the name and link to the project, as well as choose the environment where it should be deployed. Additionally, you can activate automatic updates. Then confirm pressing **Add + Deploy**.

![spring boot maven plugin](https://jelastic.com/blog/wp-content/uploads/2018/08/deploy-application.png)

More details on how to build and deploy Java applications can be found at the [Maven node documentation](https://docs.jelastic.com/java-vcs-deployment?utm_source=spring-boot-fat).

In this way, you can easily build and deploy your Spring Boot based applications packaged in JAR files using Fat/Uber approach. [Register and try out](https://jelastic.com/?utm_source=spring-boot-fat) this implementation for your custom project to feel the benefits of microservices running in the cloud.
