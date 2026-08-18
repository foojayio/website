---
title: "Jakarta EE"
description: "Jakarta EE is the open-source, community-driven successor to Java EE (Java Platform, Enterprise Edition). It defines a set of specifications for building enterprise Java applications — covering everything from web services and dependency injection to persistence, messaging, and security. The ..."
url: "/pedia/jakarta-ee/"
frozen: false
---

[Jakarta EE](https://jakarta.ee/) is the open-source, community-driven successor to Java EE (Java Platform, Enterprise Edition). It defines a set of specifications for building enterprise Java applications — covering everything from web services and dependency injection to persistence, messaging, and security.

The name change happened when Oracle transferred stewardship of Java EE to the Eclipse Foundation in 2017. The transition involved more than a rename: because Oracle retained the `javax.*` namespace, Jakarta EE 9 (released in 2020) migrated all specifications from `javax.*` packages to `jakarta.*`. This was a breaking change — existing code importing `javax.servlet`, `javax.persistence`, and similar packages had to be updated — but it gave the community full ownership of the platform going forward.

## Key Specifications

Jakarta EE is not a single framework but a collection of individually versioned specifications, each with its own reference implementation. Some of the most widely used include Jakarta Servlet (HTTP request handling), Jakarta CDI (Contexts and Dependency Injection, the backbone of managed beans), Jakarta Persistence (ORM and database access, formerly JPA), Jakarta RESTful Web Services (REST APIs, formerly JAX-RS), Jakarta Messaging (asynchronous messaging, formerly JMS), and Jakarta Security.

Jakarta EE 11, released in 2024, raised the minimum Java version to Java 21 and added Jakarta Data — a new specification for repository-style data access that brings a familiar, annotation-driven model to Jakarta Persistence.

## Implementations and Compatibility

To use Jakarta EE you need a compatible *application server* or *runtime* that implements the full specification. Well-known implementations include Eclipse GlassFish (the reference implementation), WildFly, Payara, Open Liberty, and Apache TomEE. Lighter runtimes like Quarkus and Micronaut implement subsets of the Jakarta EE specifications alongside their own extensions.

A related initiative, **Eclipse MicroProfile**, extends Jakarta EE with specifications tailored for microservices — health checks, metrics, fault tolerance, JWT authentication, and OpenAPI support — and many application servers implement both.

More reading on Foojay:

* [New Features in Jakarta EE 11, with Examples](https://foojay.io/today/new-features-in-jakarta-ee-11-with-examples/)
* [GlassFish 8 is here with Jakarta EE 11, virtual threads, and Jakarta Data](https://foojay.io/today/glassfish-8-is-here-with-jakarta-ee-11-virtual-threads-and-jakarta-data/)
* [From Spring Boot To Jakarta EE 11: How Payara Starter Eases The Transition](https://foojay.io/today/from-spring-boot-to-jakarta-ee-11-how-payara-starter-eases-the-transition/)
* [Jakarta EE is Ready for AI](https://foojay.io/today/jakarta-ee-is-ready-for-ai-but-dont-just-take-my-word-for-it/)
