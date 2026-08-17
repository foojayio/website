---
title: "Monitoring Across Frameworks: Spring Boot, Quarkus, Micronaut, Helidon"
slug: "monitoring-across-frameworks-spring-boot-micronaut-quarkus-and-helidon"
date: "2021-03-02T18:44:07+00:00"
lastmod: "2021-08-23T12:45:15+00:00"
description: "Gone are the times when developers' jobs ended with the release of the application and Spring Boot, Quarkus, Micronaut, Helidon can help!"
canonical: "https://blog.frankel.ch/monitoring-across-frameworks/"
authors:
  - "nicolas-frankel"
image: "pexels-mike-887843.jpg"
categories:
  - "Spring"
tags:
related_posts:
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "prevent-ldap-injection-in-java-with-springboot"
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "spring-i-o-2026-field-notes-from-barcelona"
enlighterjs: true
frozen: false
---

Gone are the times when developers' jobs ended with the release of the application. Nowadays, developers care more and more about the operational side of IT: perhaps they operate applications themselves, but more probably, their organizations foster increased collaboration between Dev and Ops.

I started to become interested in the Ops side of software when I was still a consultant. When Spring Boot released the Actuator, I became excited. Via its convention-over-configuration nature, it was possible to add monitoring endpoints with just an additional dependency.

Since then, other frameworks have popped up. They also provide monitoring capabilities. In this article, I'd like to compare those frameworks concerning those capabilities.

### Spring Boot {#h3-0-spring-boot}

[Spring Boot](https://spring.io/projects/spring-boot) is the framework that started the trend regarding providing monitoring capabilities. To enable them is only a matter of adding a single dependency known as the **Actuator**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.4.1</version>
</dependency>
```


The Actuator offers three kinds of endpoints:

* Default endpoints *e.g.* `/health`, `/metrics`, `/beans`, etc.
* Endpoints contributed by dependencies. For example, adding Flyway will enable the `/flyway` endpoint.
* Custom endpoints that you can provide.

With Spring Boot, one can expose endpoints over JMX and HTTP. For security reasons, by default, JMX is enabled for all endpoints. On the other hand, HTTP is enabled only for `/health` (in a condensed form) and `/info`.

You can secure HTTP endpoints via Spring Security. This allows widespread use-cases. For example, you can enable a specific endpoint but only allow authenticated clients to access it.

### Micronaut {#h3-1-micronaut}

[Micronaut](https://micronaut.io/) also offers monitoring endpoints. Those endpoints mostly map to those provided by Spring Boot with a few [exceptions](#sum-up).

To add management capabilities, add a single dependency:

```xml
<dependency>
    <groupId>io.micronaut</groupId>
    <artifactId>micronaut-management</artifactId>
    <version>2.2.2</version>
</dependency>
```


Micronauts expose endpoints over HTTP. To also expose them over JMX, you need to add an extra dependency.

```xml
<dependency>
    <groupId>io.micronaut.jmx</groupId>
    <artifactId>micronaut-jmx</artifactId>
    <version>2.1.0</version>
</dependency>
```


By default, all endpoints are enabled, but `/cache` and `/stop`.

Note that the `/metrics` endpoint requires a dependency to Micrometer Core.

### Quarkus {#h3-2-quarkus}

Last but not least comes [Quarkus](https://quarkus.io/). Quarkus' approach differs from Spring Boot's and Micronaut's: it's doesn't implement endpoints itself but relies on a third-party dependency. Before getting to the core, let me digress a bit.

Once upon a time, there was a concept named Java EE and steered by Sun, then by Oracle. The idea was to design a set of specifications and let actors of the industry implement them. This would benefit customers as they could transparently migrate from one Java EE-compatible platform to another. Though the reality was less than ideal, it worked more or less until two things happened.

Java EE platforms were designed when resources were scarce and were to be shared among several applications. The first wrench in the works was the rise of Agile and Cloud, which favored smaller and more efficient runtimes.

The second bump was that Oracle lost interest in Java EE as it didn't generate enough (any?) revenue. Java EE found a new home at the Eclipse Foundation under the Jakarta EE name, but it didn't happen overnight - it took about three years. During that time, no technical improvements happened in Java EE.

With the pace of technology, it was a deadly threat. To prevent Jakarta EE from becoming obsolete at the end of the migration period, a new set of specifications known as [MicroProfile](https://projects.eclipse.org/projects/technology.microprofile) emerged in parallel. MicroProfile offers a focused set of sub-specifications (the actual version is in brackets):

* Config (2.0)
* Fault Tolerance (3.0)
* Health (3.0)
* JWT RBAC (1.2)
* Metrics (3.0)
* Open API (2.0)
* Open Tracing (2.0)
* Rest Client (2.0)

Interestingly enough, I found at least two Microprofile implementations in the form of libraries - as opposed to a full-fledged application server: [SmallRye](https://smallrye.io/) and [Launcher](https://github.com/fujitsu/launcher) by Fujitsu. Providers of application servers, and developers, can use those dependencies instead of re-implementing the specification themselves. That's the approach that Quarkus follows.

A specific dependency implements each capability.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-metrics</artifactId>
    <version>1.10.5.Final</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
    <version>1.10.5.Final</version>
</dependency>
```


Also, Quarkus provides multiple endpoints specific to its CDI implementation (ArC). Because of its focus on Cloud-Native, Quarkus implements compile-time CDI. Hence, it's able to detect beans that are not used anywhere else and not instantiate them. Note that ArC endpoints are only available in development mode unless explicitly enabled.

When a SmallRye dependency is on the classpath at compile-time, it's enabled by default. A dedicated flag allows to disable them individually. This is true for other configuration properties, *e.g.*, the path to the endpoint.

### Helidon {#h3-3-helidon}

[Helidon](https://helidon.io/) is a framework provided by Oracle based on a sub-set of Jakarta EE APIs and MicroProfile.

I know that it supports `/health` and `/metrics` out-of-the-box. For each of them, you need to add a dependency.

I must admit I didn't spend enough time to tell more. If you're interested, please check it by yourself.

### Sum Up {#sum-up}

Here's a summary of endpoints for all frameworks.

|             Description              |    Spring Boot    |   Micronaut   |           Quarkus            |
|--------------------------------------|-------------------|---------------|------------------------------|
| Application health status            | `/health`         | `/health`     | `/health`                    |
| Metrics information                  | `/metrics`        | `/metrics`    | `/metrics`                   |
| Static metadata                      | `/info`           | `/info`       | -                            |
| "Beans"                              | `/beans`          | `/beans`      | `/quarkus/arc/beans`         |
| Available caches                     | `/caches`         | `/caches`     | -                            |
| Framework-dependent environment      | `/env`            | `/env`        | -                            |
| Logging; allows to change log levels | `/loggers`        | `/loggers`    | -                            |
| Thread data                          | `/threaddump`     | `/threaddump` | -                            |
| Web URI                              | `/mappings`       | `/routes`     | -                            |
| Stop the application                 | `/shutdown`       | `/stop`       | -                            |
| Bean-enabling conditions             | `/conditions`     | -             | -                            |
| Spring configuration properties      | `/configprops`    | -             | -                            |
| Scheduled tasks                      | `/scheduledtasks` | -             | -                            |
| Refresh the application              | -                 | `/refresh`    | -                            |
| Entrypoint to ArC-related data       | -                 | -             | `/quarkus/arc`               |
| Removed beans                        | -                 | -             | `/quarkus/arc/removed-beans` |

**To go further:**

* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)
* [Micronaut's Management \& Monitoring](https://docs.micronaut.io/latest/guide/#management)
* [MicroProfile](https://projects.eclipse.org/projects/technology.microprofile)
* [SmallRye](https://smallrye.io/)
* [Quarkus - MicroProfile Health](https://quarkus.io/guides/microprofile-health)
* [Quarkus - MicroProfile Metrics](https://quarkus.io/guides/microprofile-metrics)
* [Quarkus - Context and Dependency Injection](https://quarkus.io/guides/cdi-reference#dev-mode)

*Originally published at [A Java Geek](https://blog.frankel.ch/monitoring-across-frameworks/) on February 28^th^ 2021*
