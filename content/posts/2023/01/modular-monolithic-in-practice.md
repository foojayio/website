---
title: "Modular Monolithic in Practice | Foojay.io Today"
slug: "modular-monolithic-in-practice"
date: "2023-01-25T08:01:08+00:00"
lastmod: "2023-01-25T08:08:52+00:00"
description: "In this article, you learn about the Modular Monolithic architecture and how you can create it using the Spring Framework and Gradle."
authors:
  - "tristan-mahinay"
image: "https://foojay.io/wp-content/uploads/2023/01/modular-monolithic-gradle.png"
categories:
  - "Cloud"
  - "Gradle"
  - "Spring"
tags:
related_posts:
  - "chopping-monolith"
  - "chopping-monolith-demo"
  - "migrating-monoliths-to-microservices-in-practice"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

With the usage of microservices in application modernization, we have seen both the advantages and disadvantages of maintaining such software development styles.

When we create applications mostly in enterprise organizations, the first thing that comes to our mind now is how to decouple our applications.

But there will be times when creating too many microservices is not the best way and may cost you time and money.

Because of that, one alternative is to leverage the use of modules.

Modules {#h2-0-modules}
-----------------------

Modularity is a concept that is used to build applications to separate logical units through modules, giving you *highly cohesive* code.

These modules are analogous to a compartment where you put a specific feature of your application.

Creating modules has different benefits during software development, whether ground-up or maintenance.

Modular Monolithic {#h2-1-modular-monolithic}
---------------------------------------------

Modular Monolithic is an architectural style where your code is structured on the concept of modules.

It separates your functions in different logical units but still in a single application.

You can describe this as a composition of directories and those inner directories contain your working code.

In practice, this style is used when you don't want to manage different applications as seen in microservice development. Below are some reason why to use Modular Monolithic.

* Single deployment - your code can be deployed to cloud without configuring and maintaining too many applications. These can be seen in batch and some microservice applications.
* Issues of third-party dependency - imagine you have more than 5 applications with library violations using GitLab or JFrog Xray for example, these can be a big pain for the developers.
* Greenfield Development - when starting out a new application we wouldn't want to build our applications immediately in microservice. This may cause over-engineering and high maintenance cost with no reason.
* Cost of developers - Too many services is costly to an organization. This can be seen where the organization is in the stage of budgeting and has few developers in place.

Architecture {#h2-2-architecture}
---------------------------------

The architecture of the Modular Monolithic is quite simple. It's in a single project with different components separated into a well-defined logical directory. The modules or projects (for multi-module) defines their own way to prevent leaking of their implementation. The multi-module can define what to expose via custom configuration of Gradle while Spring Modulith leverages the use of *internal* modules.

This can be deployed to cloud using a single Dockerfile or a Multi-container approach using Docker Compose or Kubernetes-based technology. The application used in this demonstration is Spring and Gradle and a new module under Spring Framework called [Spring Modulith](https://spring.io/projects/spring-modulith)*.*

### Multi-Module {#h3-3-multi-module}

![Gradle Multi-module](/images/posts/2023/01/modular-monolithic-in-practice/modular-monolithic-gradle-700x455.png)

If you want to create and deploy a modular monolithic application, you can use a common approach used by developers called multi-module approach. This can be done using the build management tools like Maven or Gradle. In this post, we will be dealing with Gradle.

In the architecture above we can see that the application consists of 3 different projects inside a main project called *modular-monolithic-gradle*. The 3 projects are called sub-projects and can define what to expose to other projects.

The *common* project also called *shared* project which contains global or redundant codes that can be seen when you have too many service modules.

The *service* project may contain codes for a specific domain. In this case, its sole responsibility is to manage the functionality regarding Employee. You can have many services based on your use-case.

The *application* project is your main project that will invoke your services and behaves like a *Service Locator* . See a simple introduction for this pattern [here](https://www.geeksforgeeks.org/service-locator-pattern/). In Spring, the called for different services can be done using *@Qualifier* annotations and extending a base service.

Sub-modules itself can have their own Gradle lifecycle which is controlled by their own build file. In their, you can define on what to expose or not.

### Spring Modulith {#h3-4-spring-modulith}

![Spring Modulith Architecture](/images/posts/2023/01/modular-monolithic-in-practice/modular-monolithic-modulith-700x341.png)

Spring Modulith is an experimental project by Spring that can be use for modular monolithic applications. Its feature is to have a well-defined modular structure for Spring Beans and have control on what to expose or not.

Here we have the same structure like the multi-module above but using different packages and a main project (main package) only. We can hide some functionalities like the code for repositories or the model classes. Thus, controlling how you will expose your beans. You can expose many services here too and hide its respective persistence and view layer via a package called *internal*.

For scenarios where you want to create another service, you can just create another one under the com.rjtmahinay (main package) and can be called to other services (other service packages).

Although under the hood, Spring Modulith uses the multi-module approach too in its respective sub-functionalities. This technology can help us improve the way we create modules especially in Spring.

### Cloud Deployment {#h3-5-cloud-deployment}

![Modular Monolithic Cloud Deployment](/images/posts/2023/01/modular-monolithic-in-practice/modular-monolithic-deployment.png)

So far I've discussed the approaches to create a modular monolithic application using the common and a new approach. This time the architecture above shows the *single deployment* benefit of modular monolithic.

To deploy your application to cloud, you can do the single Dockerfile deployment which contains a single image of your application or deploy it in multi-container style which can be done by a single image (single jar) or multi-image (multiple jars). The multi-module approach can deploy the sub-projects as a multi-container deployment since it can have its own image (jar file). For Spring Modulith, since internally it can be a multi-module, the modulith application can be still be deployed in a single or multiple deployments.

Final Thoughts {#h2-6-final-thoughts}
-------------------------------------

It is understandable that developers will opt to microservices since it gives flexibility of technology to be used, style of deployment and decoupling of functionalities.

But we need to remember that all software requirements don't need to be created in a microservice fashion.

Problems arise where microservices becomes a Distributed Monolithic, which defeats its purpose.

In this post, I've presented an option called Modular Monolithic which is an alternative for the complexity seen in microservices and how modularity can be integrated to your software requirements.

Further Reading {#h2-7-further-reading}
---------------------------------------

* [Spring Modulith Reference Documentation](https://docs.spring.io/spring-modulith/docs/current/reference/html/)
* [Modular Monolithic by JRebel](https://www.jrebel.com/blog/what-is-a-modular-monolith#when-to-use-modular-monoliths)
* [Gradle Multi-module](https://docs.gradle.org/current/userguide/multi_project_builds.html)
