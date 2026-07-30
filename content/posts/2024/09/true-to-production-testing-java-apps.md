---
title: "Run true-to-production tests on your Java applications"
slug: "true-to-production-testing-java-apps"
date: "2024-09-24T11:48:46+00:00"
lastmod: "2024-10-07T09:13:43+00:00"
description: "TestContainers helps improves true-to-production testing, see how you could make use of it for your MicroProfile and Jakarta EE apps."
canonical: "https://developer.ibm.com/articles/true-to-production-testing-microprofile-testcontainers/"
authors:
  - "grace-jansen"
image: "https://foojay.io/wp-content/uploads/2024/09/testcontainers3-700x476-1.png"
categories:
  - "Cloud"
  - "Developer Tools"
  - "Testcontainers"
  - "Testing"
  - "Tools"
tags:
related_posts:
  - "testing-and-local-development-made-simpler-with-testcontainers-desktop-app"
  - "faster-integration-tests-with-reusable-testcontainers"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
frozen: false
---

As supported by the [12 factor](https://foojay.io/today/creating-cloud-native-java-applications-with-the-12-factor-app-methodology/ "12 factor") and [15 factor app](https://developer.ibm.com/articles/creating-a-12-factor-application-with-open-liberty/ "15 factor app") methodologies, in application development, we've come to realize just how important it is to test our applications in a true-to-production environment before releasing them to consumers. This helps us to mitigate potential failures and spot bugs that could appear in production but be missed in testing due to environmental differences and ensure we have high confidence in our applications being deployed to production environments. But, traditionally, developers have struggled with replicating an application's production environment locally.

However, in this article, we aim to make this a much easier task by introducing you to the testing framework [Testcontainers](https://testcontainers.com/ "TestContainers"). We'll take a look at what this framework is, how it enables true-to-production testing and how we can make use of it in our own Java applications and get hands-on experience with it.

Why use TestContainers? {#h2-0-why-use-testcontainers}
------------------------------------------------------

[Testcontainers](https://testcontainers.com/ "Testcontainers") is an open source framework that provides containers as a resource at test time, creating consistent and portable testing environments. This is especially useful for applications that have external resource dependencies such as databases, message queues, or web services. By encapsulating these dependencies in containers, Testcontainers simplifies the configuration process and ensures a uniform testing setup that closely mirrors production environments. This testing framework works with many different runtimes, such as [Open Liberty](https://openliberty.io/ "Open Liberty"), and provides integration with applications using Apache Kafka for messaging.

Although this framework is especially useful for applications that have external resource dependencies (such as databases, message queues, or web services), one of the most important features of Testcontainers is generic support for any Docker image. By encapsulating any necessary dependencies in containers, Testcontainers simplifies the configuration process and ensures a uniform testing setup that closely mirrors production environments.

![Testcontainers architecture](/images/posts/2024/09/true-to-production-testing-java-apps/testcontainers2-700x397.png)

In essence, with Testcontainers, you can run your tests in an isolated and controlled environment that closely resembles your production setup, ensuring that your tests are reliable and reproducible.

As well as this increased reliability and reproducibility of tests, there are additional key features/benefits that make Testcontainers a really useful tool, including:

* **A wide range of supported containers**
  * Testcontainers supports a large variety of Docker containers, including databases (for example, PostgreSQL, MySQL, MongoDB, DB2, or Redis), messaging brokers (for example, RabbitMQ or Kafka), web servers, and more. This diversity enables you to customise test environments to make them specific to your application stack.
* **Integration with popular testing frameworks**
  * Testcontainers integrates with popular testing frameworks including JUnit, TestNG, and Spock. You can easily and efficiently incorporate Testcontainers into your testing suite without having to make significant changes to your existing tests.
* **Declarative configuration**
  * Testcontainers provides a simple, declarative API for configuring and deploying containers. You can define any desired properties of a container using code, making it easy to configure and maintain.
* **Lifecycle management**
  * Testcontainers also handles the lifecycle of containers, ensuring they are started before the tests run and, importantly, stopped afterward. This automation removes this responsibility from your shoulders, eliminating the need for manual intervention and making it easy to maintain clean, isolated testing environments.
* **Wait strategies**
  * To prevent your tests from trying to run before any required containers are set up, Testcontainers has a helpful, built-in wait strategy. This particularly important for services with tests that rely on services that can take significant time to initialise (for example, databases).

How to use Testcontainers in your own applications {#h2-1-how-to-use-testcontainers-in-your-own-applications}
-------------------------------------------------------------------------------------------------------------

To walk you through how you can add Testcontainers to an existing Java application, the Open Liberty team have created an interactive, hands-on guide.

Firstly, this guide shows how to to set up and configure multiple containers, including the Open Liberty Docker container, to simulate a production-like environment for your tests. The guide uses Docker to run an instance of the PostgreSQL database required by this application for fast installation and setup.

![TestContainers example app architecture](/images/posts/2024/09/true-to-production-testing-java-apps/testcontainers3-700x476.png)

As Testcontainers is a black-box form of testing, you need a REST client to trigger the requests for your tests. So, next in this guide, they show how to build a test REST client to accurately verify the application's behavior by ensuring that it responds correctly to various scenarios and conditions.

By completing this guide with the demo application propvided, it should give you a good understanding of how you can then use TestContainers effectively in your own applications. You can try it out for yourself, either running locally on your own machine or using the cloud-hosted development environment offered for these Open Liberty guides: [Testcontainers guide](https://openliberty.io/guides/testcontainers.html "Testcontainers guide").

Summary {#h2-2-summary}
-----------------------

Testing in containers with TestContainers can save developers time and stress by making the testing environment they use as close to production as possible. This enables us to use Docker files to easily configure local Docker containers that act as throw away testing environments.
