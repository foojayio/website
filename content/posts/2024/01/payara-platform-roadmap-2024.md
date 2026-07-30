---
title: "Payara Platform Roadmap 2024"
slug: "payara-platform-roadmap-2024"
date: "2024-01-17T10:39:43+00:00"
lastmod: "2024-02-09T09:41:21+00:00"
description: "Read on to find out more about Payara Rodmap in the coming months -  Java 21 compatibility, the upcoming Jakarta EE 11 and our plan for support, and more!"
canonical: "https://blog.payara.fish/payara-platform-roadmap-2024"
authors:
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2024/01/image-3.png"
categories:
  - "Payara"
  - "Tools"
tags:
related_posts:
frozen: false
---

**Now that New Year is here, we reflect on 2023 and look ahead to what you can expect from our entire suite of Payara products in 2024. What is the future for Payara Community, Enterprise and Payara Cloud?**

Read below to find out more about Payara Rodmap in the coming months - Java 21 compatibility, the upcoming Jakarta EE 11 and our plan for support, and more!

You can also [watch the Payara Roadmap 2024 presented](https://www.crowdcast.io/c/virtualpayaraconference/xstAZ) at the Virtual Payara Conference last month.

<br />

<br />

Jakarta EE, MicroProfile \& Payara Platform in 2023 - Recap {#h2-0-jakarta-ee-microprofile-payara-platform-in-2023-recap}
-------------------------------------------------------------------------------------------------------------------------

We've had a busy year with some exciting new products and features being launched.

### Payara 6 is Born! {#h3-1-payara-6-is-born}

In March 2023 we released [Payara 6 Enterprise](https://www.payara.fish/page/payara-enterprise-downloads/), and MicroProfile 6 Community and Enterprise editions, which are Jakarta EE 10 certified and compiled with JDK 11, along with the related tooling support.​

In May 2023, we made the Payara 5 to 6 upgrade tooling and support available, in order to support our customers in upgrading to Payara 6.​

And by June 2023, Payara Cloud also supported Payara 6.

### There's more - JDK 21, Payara Cloud, MicroProfile \& Payara Starter {#h3-2-there-s-more-jdk-21-payara-cloud-microprofile-payara-starter}

In August 2023, we launched [Payara Cloud free trial](https://www.payara.fish/products/payara-cloud/). ​

By October 2023, JDK 21 was supported in Payara 6 Community edition, and we took the decision to extend Payara 5 full support for another year, because of the breaking changes between 5 and 6 and the rate of migration.​

In September and October 2023, we continued improving Payara Cloud, first supporting JDK 17 and then JDK 21 as well as providing Billing Management and Multiuser Subscriptions.​

Then at the end of the year, [MicroProfile 6.1](https://blog.payara.fish/whats-new-with-microprofile-6.1) became available on both Payara Platform Community and Payara Platform Enterprise.

[Our last release of the year](https://blog.payara.fish/whats-new-in-the-december-2023-payara-platform-release) is supporting JDK21 on Payara 6 Enterprise and providing Micro Maven Tools.

### Payara Starter​ {#h3-3-payara-starter}

At the end of 2023, we launched [our new Payara Starter](https://start.payara.fish/) application which simplifies the process of starting new projects on the Payara Platform with a user friendly interface to generate Payara Server or Micro Sample projects, and we continued to provide new functionality for Payara Cloud.​
![](/images/posts/2024/01/payara-platform-roadmap-2024/image-2-1024x576.png)

[](https://info.payara.fish/hubfs/Slide4.jpg)

Summary of 2023 - Jakarta EE, MicroProfile \& Payara Cloud {#h2-4-summary-of-2023-jakarta-ee-microprofile-payara-cloud}
-----------------------------------------------------------------------------------------------------------------------

### Jakarta EE 10​ {#h3-5-jakarta-ee-10}

* Major release with backwards incompatible changes
* New functionality in over 20 component specifications
* New features for building modernised, simplified, and lightweight cloud native Java applications

### ​Payara 6 and MicroProfile 6 ​ {#h3-6-payara-6-and-microprofile-6}

* Jakarta EE 10 certified runtime and runs on JDK 11, JDK 17 and JDK 21
* Affects all Jakarta EE 8 and below applications
* Enterprise users - tooling, support, migration, keep Jakarta EE 8 and JDK 8
* MicroProfile OpenAPI and JWT Authentication updates
* MicroProfile Metrics update, backward incompatible changes
* MicroProfile Telemetry added and replaces MicroProfile OpenTracing
* Allows products to be certified on Java SE 11 or higher

### Payara Cloud {#h3-7-payara-cloud}

[![payara cloud roadmap 1](https://blog.payara.fish/hs-fs/hubfs/payara%20cloud%20roadmap%201.png?width=307&height=310&name=payara%20cloud%20roadmap%201.png)](https://info.payara.fish/hubfs/payara%20cloud%20roadmap%201.png)

* No Self Assembly
* All pieces work together
* Versioned Runtime
* Security
* Monitoring
* Health
* High Availability
* Scalability
* Focus on application
* PAAS
* [Free 15 days trial](https://www.payara.fish/products/payara-cloud/)

2024 and Beyond {#h2-8-2024-and-beyond}
---------------------------------------

### Payara Platform Vision {#h3-9-payara-platform-vision}

![](/images/posts/2024/01/payara-platform-roadmap-2024/image-3-1024x576.png)

### [](https://info.payara.fish/hubfs/Slide9.jpg)Payara Core {#h3-10-payara-core}

* Foundation of the platform
* Single JVM runtime - MicroProfile X and Jakarta EE X
* Similar to Payara Micro or single Instance of Payara Server

**Work on Payara Core is predominantly focussed on:**​

* Standards Advancement
* Standards Compatibility
* Performance both raw speed and resource usage
* Application instrumentation and core telemetry

![](/images/posts/2024/01/payara-platform-roadmap-2024/image-4.png)

### [](https://info.payara.fish/hubfs/payara%20Core.png)Payara Server {#h3-11-payara-server}

**Payara Server is focused on:**​

* Enabling Jakarta EE**developers** to utilise their existing skills to **take Jakarta EE applications** **to new deployment platforms**
* **Delivering mission critical stability** for demanding Enterprise workloads
* **Most innovative application server** with the best out of the box **operations and developer** **experience**
* Managing and Monitoring capabilities
* Adapting to new architectures and deployment infrastructure such as cloud and containers

![Slide13](https://blog.payara.fish/hs-fs/hubfs/Slide13.jpg?width=1280&height=720&name=Slide13.jpg)Jakarta EE 11 {#h2-12-jakarta-ee-11}
---------------------------------------------------------------------------------------------------------------------------------------

Previous work on Jakarta EE has focused on establishing a framework and foundation for future innovation so as to make Jakarta EE a solid basis for open source developers to build on. ​

The focus is to make sure that Jakarta EE is always leveraging the latest and greatest capabilities of the new Java version (in this case it's Java 21, released in September 2023), build new specifications and further unify and simplify the platform.​

Java 21 brings us improvements like virtual threads, string templates and pattern matching on switch, among other things.​

**Virtual Threads:** These are designed to be lighter and more efficient than traditional threads, thus enabling applications to handle a greater number of tasks concurrently. [Check out our blog on the subject.](https://blog.payara.fish/a-look-at-virtual-threads-in-a-jakarta-ee-managed-context)​  

**String Templates:** simplify string manipulation, making the code more readable and maintainable.​  
**Pattern Matching for Switch**: This feature allows for more concise and user-friendly switch statements.​

Some specifications will need to make API changes to support language features introduced between Java 11 and Java 21, specifically: Virtual Threads and Records. ​

The Theme for Jakarta EE 11 is **Performance and Developer Productivity.**​

Another focus area is **platform unification** -- Jakarta EE, and Java EE before it, being around for some time, have contributed to the fact that things can be done differently across different specifications, which can be confusing and time consuming for developers. With API unification, we hope to bring enhanced usability. As part of this, Jakarta EE will continue becoming more CDI centric and more streamlined.

The specifications that will all have updated versions for Jakarta EE 11 are listed below:​

* Jakarta Annotations 3.0
* Jakarta Authentication 3.1
* Jakarta Authorization 3.0
* **Jakarta Bean Validation 3.1​**
* Jakarta Concurrency 3.1
* **Jakarta Contexts and** **Dependency Injection 4.1​**
* Jakarta Expression Language 6.0
* Jakarta Faces 5.0
* Jakarta Interceptors 2.2
* Jakarta Pages 4.0
* **Jakarta Persistence 3.2​**
* **Jakarta RESTful Web Services** **4.0​**
* **Jakarta Security 4.0​**
* Jakarta Servlet 6.1
* Jakarta WebSocket 2.2

​There are also some new specifications which are candidates for inclusion, such as Jakarta Data 1.0, Jakarta MVC 3.0 and Jakarta NoSQL 1.0, which you will also have seen in our roadmap.​

​Payara Developer Tools​ {#h2-13-payara-developer-tools}
--------------------------------------------------------

![](/images/posts/2024/01/payara-platform-roadmap-2024/image-5-1024x576.png)

[](https://info.payara.fish/hubfs/Slide16.jpg)**Payara Connectors**​ {#h2-14-payara-connectors}
-----------------------------------------------------------------------------------------------

* Supports additional non-standard connectors to other Enterprise infrastructure e.g. Kafka, JMS Providers, Config Sources, Notification Sinks, Security providers.
* Usable across Payara Core, Payara Server and Payara Cloud.

**Payara Tools**​

* IDE plugins to support developing Jakarta EE and MicroProfile applications.
* Deployment of applications to any of the other Payara products.
* Support for other standard developer tool chains including; CI/CD tools, Maven, Gradle, test frameworks like Arquillian.

Payara Cloud PaaS and Project Aquarium​ {#h2-15-payara-cloud-paas-and-project-aquarium}
---------------------------------------------------------------------------------------

![](/images/posts/2024/01/payara-platform-roadmap-2024/image-6-1024x576.png)

[](https://info.payara.fish/hubfs/Slide18.jpg) {#h2-16-}
--------------------------------------------------------

We currently offer **Payara Cloud PaaS, which**is a fully managed installation of Payara Cloud on Payara managed infrastructure (on Azure). It is billed to the customer based on usage. ​

Other than billing management, it will be substantially similar to Project Aquarium.​

**Project Aquarium**is how we are currently referring to Payara Cloud installable product. We will be providing Cloud as a user installable product that is a Kubernetes native application platform for running Jakarta EE and MicroProfile. Payara Cloud builds on Payara Core to provide user management, application management on top of Kubernetes. ​

Payara Cloud will have multiple editions, depending on the target infrastructure:​

* AWS Edition
* Azure Edition
* Native K8s Edition - should support OpenShift, Rancher, CharmedK8s

Payara Cloud is suitable for users who want to run Jakarta EE applications without looking after the runtime. Payara Cloud extends the Jakarta EE model and again takes advantage of separating the runtime from application deployment.​

With Payara Cloud there is no self-assembly and all the pieces work together. We provide a versioned runtime with security and monitoring and health. It provides high availability and scalability, and focuses on the application.​

Developers simply have to package a WAR file and Payara Cloud will manage everything else. ​

The current version is based on Kubernetes (although we will be supporting additional frameworks in the near future). The difference between deploying applications on Kubernetes, for example, and using Payara Cloud is in operation automation.

Normally, a developer has to manually manage all the related operations, like provisioning nodes and pods, routing, SSL certificates, and more, but in Payara Cloud we have automated most of those processes, leaving just application configuration and deployment, which saves valuable time. This means that developers can focus on writing business logic and then are able to deploy to a fully managed infrastructure.​

**Payara Cloud PAAS**​

* Fully managed installation of Payara Cloud on Payara managed infrastructure (Azure)
* Consumption Pricing
* Similar to Project Aquarium except for billing management

**Payara Cloud (Project Aquarium)**​

* User installable product
* Kubernetes native application platform for running Jakarta EE and MicroProfile
* Leverages Core to provide user management, application management on top of Kubernetes
* Multiple editions depending on the target infrastructure:
* AWS Edition
* Azure Edition
* Native K8s Edition - OpenShift, Rancher, CharmedK8s

Summary {#h2-17-summary}
------------------------

* Payara Platform is first for Jakarta EE, leveraging all the benefits of separating the application from the runtime
* Developers write business logic, and Payara Platform manages the infrastructure and simplifies deployment
* Intelligent platform that delivers data, analytics, awareness and actions to optimise Jakarta EE applications
* Our suite of products support different use cases:  
  * **Payara Enterprise**
  * **Payara Community**
  * **Payara Server**
  * **Payara Micro**
  * **Payara Cloud**
* We have exciting plans for all of our products with a jam-packed roadmap for the coming years!

[Watch the full Payara 2024 Roadmap presentation by clicking here.](https://www.crowdcast.io/c/virtualpayaraconference/xstAZ) {#h2-18-watch-the-full-payara-2024-roadmap-presentation-by-clicking-here}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

<br />
