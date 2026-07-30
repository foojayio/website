---
title: "Preparing for Spring Boot 4 and Spring Framework 7: What’s New?"
slug: "preparing-for-spring-framework-7-and-spring-boot-4"
date: "2025-08-11T14:42:40+00:00"
lastmod: "2025-08-17T00:07:21+00:00"
description: "Spring Boot 4 and Spring 7 bring API changes, native threads, and resilience. Prepare your codebase for the upgrade."
authors:
  - "mahendra1413"
image: "/images/posts/2025/08/preparing-for-spring-framework-7-and-spring-boot-4/spring.png"
categories:
  - "Jakarta EE"
  - "Java"
  - "JMS"
  - "Kotlin"
  - "Maven"
  - "Performance"
  - "Spring"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-simple-service-with-spring-boot"
enlighterjs: true
frozen: false
---

**I'm a passionate Spring Framework and Spring Boot enthusiast, and I always look forward to exploring and experimenting with the latest features and improvements they introduce. With Spring Boot 4 and Spring Boot Framework 7 right around the corner, now's the perfect time to dive into the key enhancements that will shape the future of modern Java and enterprise application development.**
![Spring Boot 4](/images/posts/2025/08/preparing-for-spring-framework-7-and-spring-boot-4/SpringImage.jpg)

In this blog post, we will discuss some key features enhanced as part of Spring Framework 7 and Spring Boot 4.

To start with,

### 1. Built-in Resilience Feature {#h3-0-1-built-in-resilience-feature}

Spring Framework 7 introduces powerful resilience tools directly into its core:

* **@Retryable:** Retries failed method calls with configurable options like max attempts, delays, jitters, and backoff. It also supports reactive return types.
* **@ConcurrencyLimit:**Limits concurrent method invocations to protect services and resources---for example, by restricting access to a single thread.
* Enable both annotations using **@EnableResilientMethods** or by registering specific post-processors. For more details, please go read through [here](https://docs.spring.io/spring-framework/docs/7.0.0-M7/javadoc-api/org/springframework/resilience/annotation/EnableResilientMethods.html)

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Configuration
@EnableResilientMethods
public class ApplicationConfig {
}
</pre>

Service with **@Retryable** and **@ConcurrentLimit**

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Service
public class PaymentService {

    private int callCount = 0;

    @Retryable(
        maxAttempts = 3,
        backoff = @Retryable.Backoff(delay = 2000, multiplier = 2.0)
    )
    @ConcurrencyLimit(value = 2)  // Allow only 2 concurrent calls
    public void processPayment(String paymentId) {
        callCount++;
        System.out.println("Attempt " + callCount + " to process payment: " + paymentId);

        if (Math.random() &gt; 0.3) {
            throw new RuntimeException("Simulated failure");
        }

        System.out.println("Payment processed: " + paymentId);
    }
}
</pre>

### 2. Fluent JMS Client API {#h3-1-2-fluent-jms-client-api}

Spring now includes **JmsClient** , modeled after **JdbcClient** and RestClient. Developers can now send/receive messages using a fluent, builder-style API. This new approach is a more elegant and readable alternative to traditional JMS templates---just like how **JdbcClient** replaces **JdbcTemplate** , and **RestClient** replaces **RestTemplate**.

### 3. Robust Api Versioning {#h3-2-3-robust-api-versioning}

Spring Framework enhances API versioning with powerful new features:

* Resolves versions via media types
* Supports API deprecation notices and validation
* Allows defining fixed version sets

These enhancements work across both Spring MVC and Spring WebFlux.

### 4. Unified Message Conversion {#h3-3-4-unified-message-conversion}

Spring simplifies message conversion with a new ***HttpMessageConverters*** configuration class. This unified approach draws inspiration from reactive codecs, streamlining how HTTP messages are serialized and deserialized.

### 5. Faster and Smarter Testing {#h3-4-5-faster-and-smarter-testing}

Spring now optimizes test performance by**pausing unused application contexts.** When paused, the framework stops the context and automatically restarts it when needed. This reduces resource usage and speeds up test execution significantly.

### 6. Modern Ecosystem Integration {#h3-5-6-modern-ecosystem-integration}

Spring Framework 7 aligns with the latest platforms and standards:

* Kotlin 2.2
* Jakarta EE 11 baseline
* GraalVM 24 support

### 7. Hibernate ORM and JPA Upgrades {#h3-6-7-hibernate-orm-and-jpa-upgrades}

Spring integrates with **Hibernate ORM 7.0** and **JPA 3.2** , offering compatibility with the latest persistence standards. Prior to these, EntityManager could be injected only by defining the @PersistenceContext annotation; however, now both **EntityManagerFactory** and its associated shared **EntityManager** can now be injected using **@Inject or @Autowired**, with support for qualifiers to select a specific persistence unit when multiple are configured.

### 8. Overhauled HttpHeaders API {#h3-7-8-overhauled-httpheaders-api}

The new HttpHeaders API delivers a cleaner, more consistent developer experience when handling HTTP headers.

### 9. Support for Jackson 3.x {#h3-8-9-support-for-jackson-3-x}

Spring Framework now supports **Jackson 3.x** and provides migration guidance for deprecated Jackson features, helping developers upgrade smoothly.

### 10. Null Safety using JSpecify {#h3-9-10-null-safety-using-jspecify}

Introduces **JSpecify** for null safety that certainly replaces the former `org.springframework.lang.*` annotation. This is going to be the standard annotation approach for ***nullness*** . For more details, see <https://spring.io/blog/2025/03/10/null-safety-in-spring-apps-with-jspecify-and-null-away>

*** ** * ** ***

Spring Boot 4 is a significant leap forward in modernizing how Spring applications are developed, configured, and deployed. This milestone marks the beginning of a more modular, extensible, and developer-friendly version of the framework. Let's understand some of the significant enhancements in Spring Boot 4 and how they impact developers.

### 1. Modular Codebase---A Refactored Architecture {#h3-10-1-modular-codebase-a-refactored-architecture}

It introduces a major architectural shift by **breaking up the internal codebase into smaller, focused modules.** Previously, Spring Boot relied on large, monolithic auto-configuration JARs. With version 4, the auto-configurations have been **refactored into modular packages**, making the framework more maintainable and composable.

Each module starts with a dedicated package, such as

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">org.springframework.&lt;module&gt;</pre>

Depending on the module's purpose, it can include:

* Public APIs
* Auto-configuration logic
* Actuator-related support

### 2. Now Available in Maven Central {#h3-11-2-now-available-in-maven-central}

For the first time, milestone artifacts like 4.0.0-M1 are **published to Maven Central** in addition to Spring's repository. This helps in a greater way:

* Easier dependency management
* Smoother CI/CD integration
* Improved compatibility with build tools and enterprise repos

<https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter/4.0.0-M1>

This change enhances developer productivity by reducing the friction in bootstrapping and upgrading projects.

### 3. Enhanced Configuration Properties Metadata {#h3-12-3-enhanced-configuration-properties-metadata}

Introduces a new annotation: **@ConfigurationPropertiesSource** . This allows Spring Boot to read **@ConfigurationProperties** types defined in external modules, something that wasn't possible before. The benefits would be 1. a cleaner modular design 2. Improves tooling support (IDE autocompletion, validation, etc.) 3. makes shared configuration libraries easier to manage.

### 4. Improvements in SSL Health Reporting {#h3-13-4-improvements-in-ssl-health-reporting}

The SSL health endpoint in Spring Boot has been improved to provide more accurate and streamlined reporting.

**What's changed?**

* The WILL_EXPIRE_SOON status has been removed
* Certificates are now shown as VALID until they actually expire
* A new field ***expiringChains*** has been added to help track certificates nearing **expiration**.

These changes make it easier for teams to monitor SSL certificate validity in production environments without false alarms.

### 5. Task Scheduling with Multiple TaskDecorator Beans {#h3-14-5-task-scheduling-with-multiple-taskdecorator-beans}

One of the most developer-friendly updates in Spring Boot 4.0 is support for ***multiple*** **TaskDecorator** beans. Prior to 4.0, Spring Boot allowed only one decorator, requiring manual chaining when multiple beans (like tracing and logging) needed to be applied.

**Before Spring Boot 4.0:**

* Spring Boot allowed only one.`TaskDecorator`
* For multiple decorators (e.g., for tracing and logging), we need to manually chain them in a custom decorator.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Bean
public TaskDecorator customTaskDecorator() {
    return runnable -&gt; {
        Runnable decoratedWithTracing = tracingDecorator().decorate(runnable);
        return loggingDecorator().decorate(decoratedWithTracing);
    };
}

public TaskDecorator tracingDecorator() {
    return runnable -&gt; () -&gt; {
        System.out.println("Tracing Start");
        runnable.run();
        System.out.println("Tracing End");
    };
}

public TaskDecorator loggingDecorator() {
    return runnable -&gt; () -&gt; {
        System.out.println("Logging Start");
        runnable.run();
        System.out.println("Logging End");
    };
}
</pre>

In the above code snippet,

1. We have to do the chaining manually
2. We couldn't inject and order multiple decorators using Spring annotations

**In Spring Boot 4.0:**

Spring Boot automatically creates a **CompositeTaskDecorator** that chains all available decorators in the order specified using the **@Order** annotation:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Bean
@Order(1)
public TaskDecorator tracingDecorator() {
    return runnable -&gt; () -&gt; {
        System.out.println("Tracing Start");
        runnable.run();
        System.out.println("Tracing End");
    };
}

@Bean
@Order(2)
public TaskDecorator loggingDecorator() {
    return runnable -&gt; () -&gt; {
        System.out.println("Logging Start");
        runnable.run();
        System.out.println("Logging End");
    };
}</pre>

When the task runs, Spring applies decorators in order:

1. tracingDecorator
2. loggingDecorator
3. Then the actual task

We no longer need to manually compose decorators---Spring Boot handles it for us.

### 6. JMS Support via JdbcClient {#h3-15-6-jms-support-via-jdbcclient}

Spring Boot 4.0 now auto-configures JmsClient, introduced in Spring Framework 7. This aligns with the familiar patterns of JdbcClient and RestClient, offering a fluent, builder-pattern style, modern API for working with JMS messaging. It still coexists with **JmsTemplate** and **JmsMessagingTemplate**.

This makes JMS more accessible and cleaner to use, especially for microservices that rely on messaging systems.

### Conclusion {#h3-16-conclusion}

As a spring fan, I am super excited. Spring Boot 4.0 is shaping up to be one of the most significant releases in recent years. With **a refactored modular architecture** , **improved observability** , **native resilience patterns** , and **developer-friendly defaults**, it's ready to power the next generation of Spring applications.

### References {#h3-17-references}

<https://spring.io/blog/2025/07/17/spring-framework-7-0-0-M7-available-now>

<https://spring.io/blog/2025/07/24/spring-boot-4-0-0-M1-available-now>

<https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide>

<https://docs.spring.io/spring/reference/7.0-SNAPSHOT/core/resilience.html>

<https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes>
