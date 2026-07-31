---
title: "What is MicroProfile? | Foojay.io Today"
slug: "what-is-microprofile"
date: "2022-09-22T09:03:08+00:00"
lastmod: "2022-09-22T09:19:10+00:00"
description: "What is the MicroProfile specification, what is it used for, and why might you need it? Find out here and get started! "
canonical: "https://blog.payara.fish/what-is-microprofile"
authors:
  - "jadon-ortlepp"
  - "luis-neto"
image: "microprofile.png"
categories:
  - "Cloud"
  - "Microservices"
tags:
related_posts:
  - "can-java-jakarta-ee-do-microservices"
  - "evolution-of-microservices"
  - "microprofile-metrics-with-prometheus-and-grafana"
frozen: false
---

The Java programming language can be enhanced with specifications. An specification is a baseline platform definition - a framework - to guide concrete implementations.

One of these specifications is called MicroProfile.

But what is MicroProfile specification, what is it used for and why might you need it?   

This article explains all!

What Is MicroProfile? {#h2-0-what-is-microprofile}
--------------------------------------------------

The [MicroProfile](https://microprofile.io/) specification is a set of Enterprise Java APIs and technologies, designed to help with the challenge of building microservices architectures in Java.

They are intended to be used on top of [Jakarta EE](https://jakarta.ee/) APIs to add specific functionalities that are needed when developing microservices.  

<figure class="alignright is-resized">
 <img fetchpriority="high" decoding="async" src="https://blog.payara.fish/hs-fs/hubfs/microprofile%20square.png?width=403&amp;name=microprofile%20square.png" alt="microprofile square" width="473" height="472">
</figure>

### Wait, What Is Jakarta EE? {#h3-1-wait-what-is-jakarta-ee}

Jakarta EE is a set of software components that extend [Java SE](https://www.java.com/en/) - the standard edition Java programming language - with ways to perform the functions particularly useful for an enterprise application.

You can read our previous blog ['What is Jakarta EE?'](https://blog.payara.fish/jakarta-ee-java-ee-guide) for more information.

### Wait, What Are Microservices? {#h3-2-wait-what-are-microservices}

Microservices refer to a software architecture style where your application is structured in small code, granular modules or services.

Services can then be deployed and maintained independently from each other. Read our ['Explaining Microservices: No Nonsense Guide for Decision Makers'](https://www.payara.fish/resource/explaining-microservices-no-nonsense-guide-for-decision-makers/) for more information about microservices.

How Does MicroProfile Help You With Microservices Applications? {#h2-3-how-does-microprofile-help-you-with-microservices-applications}
--------------------------------------------------------------------------------------------------------------------------------------

MicroProfile has ready-made APIs to deal with common challenges in microservices architecture. It means you can use MicroProfile APIs, rather than write custom code to fix some problems. For example:

* **Problem:** Difficult to debug and trace issues in a microservices architecture, when there are multiple different modules or services.  
  **MicroProfile Solution:** [Open Tracing](https://download.eclipse.org/microprofile/microprofile-opentracing-2.0/microprofile-opentracing-spec-2.0.html)- APIs where you can build traces for remote calls (a client app creates a request for a resource from a remote service) and use them for debugging purporses.
* **Problem:** Microservices architecture necessitate lots of remote calls to other remote services - these might not be available and cause problems for the end user.  
  **MicroProfile Solution:** [Fault Tolerance](https://microprofile.io/project/eclipse/microprofile-fault-tolerance)- this allows you to add annotations to a remote call, so if it isn't successful, you can provide fall back to a different service, or APIs for retrying the call.
* **Problem:** More independently built services = more data and metrics in different systems. How do you keep track of them to monitor performance and usage?  
  **MicroProfile Solution:** [Metrics](https://download.eclipse.org/microprofile/microprofile-metrics-4.0/microprofile-metrics-spec-4.0.html)- this API gathers data and metrics from different services and combines them into a centralized system.

Who Maintains MicroProfile? {#h2-4-who-maintains-microprofile}
--------------------------------------------------------------

Both MicroProfile and Jakarta EE are managed by the [Eclipse Foundation,](https://www.eclipse.org/org/foundation/) a not-for-profit software corporation that stewards many open source projects.

MicroProfile is open source, so anyone can get involved in the project.

The Eclipse Foundation manages the [MicroProfile Working Group,](https://microprofile.io/workinggroup/#:~:text=MicroProfile%20is%20an%20open%20forum,with%20a%20goal%20of%20standardization.) an open forum where different vendors work together to build the brand, establish the technical roadmap, define the rules around compatibility and what makes a MicroProfile compatible implementation.

What Is A MicroProfile Implementation? {#h2-5-what-is-a-microprofile-implementation}
------------------------------------------------------------------------------------

Like Jakarta EE, each MicroProfile API has a Technology Compatibility Kit (TCK), a test suite your software has to pass to prove it is compatible with the API.

Compatible implementations will follow the specifications and pass the TCKs.

What Is A MicroProfile Runtime? {#h2-6-what-is-a-microprofile-runtime}
----------------------------------------------------------------------

MicroProfile, like Jakarta EE, is designed to work with a runtime. A runtime is a program where your application runs. It handles HTTP requests sent by its clients over the internet.

A MicroProfile runtime will need to pass the TCKs to be a compatible implementation - and allow you to use your applications with MicroProfile!

Many different vendors have created MicroProfile compatible runtimes, including Payara. [Payara Micro](https://www.payara.fish/products/payara-micro/) is our lightweight solution: MicroProfile and Jakarta EE compatible and designed for containerized Jakarta EE and MicroProfile deployments.

Further Reading : {#h2-7-further-reading}
-----------------------------------------

* [Demystifying Microservices for Jakarta EE \& Java EE Developers](https://info.payara.fish/demystifying-microservices-for-java-ee-developers)
* [MicroProfile Fault Tolerance to Build Cloud Native Applications](https://www.payara.fish/page/how-to-use-eclipse-microprofile-fault-tolerance-api-to-build-cloud-native-applications-on-payara-platform/)
* [Configurable Applications with MicroProfile Config API](https://www.payara.fish/page/build-highly-configurable-applications-on-payara-server-using-the-eclipse-microprofile-config-api/)
* [Mastering Microservices with MicroProfile and Payara in the Cloud](https://info.payara.fish/microservices-with-microprofile-and-payara)
* [Explaining Microservices: No Nonsense Guide for Decision Makers](https://www.payara.fish/resource/explaining-microservices-no-nonsense-guide-for-decision-makers/)
* [Payara Micro Getting Started Guide](https://www.payara.fish/resource/payara-micro-getting-started-guide/)
