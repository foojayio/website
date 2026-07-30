---
title: "How to improve your spring boot skills"
slug: "how-to-improve-your-spring-boot-skills"
date: "2024-03-20T15:10:29+00:00"
lastmod: "2024-08-14T13:05:42+00:00"
description: "How to make sure you're unlocking the full potential of Spring Boot. 9 ways to improve your Spring Boot Skills."
canonical: "https://digma.ai/the-spring-way-of-doing-things-9-ways-to-improve-your-spring-boot-skills/"
authors:
  - "lee-sheinberg"
image: "https://foojay.io/wp-content/uploads/2024/03/digmalovejava-spring.png"
categories:
  - "Java"
  - "Observability"
  - "Performance"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Does anyone else ever feel overwhelmed by Spring Boot? With a rich set of options and eco-system libraries on the one hand, and a very opinionated framework on the other, I often spend considerable time deciphering the "Spring Way" of doing things. I've been working with [Spring Boot](https://digma.ai/10-spring-boot-performance-best-practices/) for over three years, yet there are moments when I sense I'm not fully harnessing the capabilities of this remarkable framework and that I need to improve my Spring Boot skills.**

I want to make sure I'm unlocking the full potential of Spring. The other day, while developing a service, I thought, "Coding this would have been so straightforward, but I delved into the intricacies of the 'Spring way' to implement it. It took me 3-4 days to utilize Transformers and SpEL.

So I started thinking to myself, how would Spring Pros approach this? How much time do they invest in exploring the framework's features, and where do they seek this information? After researching Reddit and other platforms, talking to peers, and relying on intuition, I think I've figured out a few things that would be cool to share with fellow Java devs. And I decided to write up this blog and share my experience.

Here are 9 ways to improve your Spring Boot skills: {#h2-0-here-are-9-ways-to-improve-your-spring-boot-skills}
--------------------------------------------------------------------------------------------------------------

1. Externalize your configuration: {#h2-1-1-externalize-your-configuration}
---------------------------------------------------------------------------

Another way to utilize Spring Boot to its full potential is to try as much as possible to externalize your configuration instead of hard coding them. Externalizing your configuration will make your application more flexible and easier to manage.

Another advantage of externalizing your configuration is that you don't need to shut down your application, make a change, recompile, and then redeploy your application just because of a small change.

Here is how you can externalize your configuration

To externalize your configuration, you need to make use of the application.properties or application.yml

-- Configure Your Properties:

In your application.properties or application.yml file, specify the properties with the configured prefix:

For application.properties:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">app.bootstrap-servers=localhost:3000
app.client-id=digma-id
app.group-id=dgma-group</pre>

For application.yml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">app:
  bootstrap-servers: localhost:3000
  client-id: digma-id
  group-id: dgma-group</pre>

-- Create a Configuration Properties Class:

Create a class to hold your configuration properties. Annotate it with @ConfigurationProperties to specify the prefix for your properties:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String bootstrapServer;
    private String clientId;
    private String groupId;
}</pre>

-- Inject the Configuration:

Inject the configuration properties into your components or services:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class DemoService {
    private final AppProperties appProperties;
    private final Logger logger = LoggerFactory.getLogger(DemoService.class);
    public DemoService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    public void printConfiguration() {
        logger.info("App Name: {}", appProperties.getBootstrapServer());
        logger.info("Version: {}", appProperties.getClientId());
        logger.info("Version: {}", appProperties.getGroupId());
    }
}</pre>

2. You need to keep your controllers lean {#h2-2-2-you-need-to-keep-your-controllers-lean}
------------------------------------------------------------------------------------------

Another way to utilize Spring Boot to its full potential is to ensure that you keep your controllers lean. Keeping your controllers lean will help you avoid practices that will lead to the 'Fat Controller' anti-pattern.

What is the 'Fat Controller' anti-pattern?

The 'Fat Controller' anti-pattern is a pattern in programming where controllers in a web application become overloaded with business logic, violating the principles of separation of concerns. When controllers take on too much responsibility, the following issues are bound to arise:

* Maintainability: Large controllers are difficult to maintain and understand. Any modification or addition to business logic becomes error-prone and complex.
* Testability: With business logic being embedded in controllers, it becomes difficult to carry out tests in isolation, making it challenging to create reliable unit tests.
* Reusability: You hardly can find reusable logic in controllers. If the same logic is needed in another part of the application, it is usually very difficult to extract and reuse.
* Readability: A codebase becomes less readable when you have business logic in controllers. It becomes difficult for developers to quickly understand the purpose and functionality of a controller.

To avoid the 'Fat Controller' anti-pattern, adhere to the following principles:

* Stateless: By default controllers are singletons and any state change can cause a lot of issues.  
  Delegating: Your controllers should be delegating, meaning it should not execute business logic, but rely on delegation.
* Encapsulated: The HTTP layer of your application should be handled by controllers, this should not be handled by the service layer.  
  Use-case-centric: make sure your controllers are built around use cases or the business domain.

3. Handle exception globally {#h2-3-3-handle-exception-globally}
----------------------------------------------------------------

To use Spring Boot to the fullest, you'll need to learn how to handle exceptions globally and this is important because:

* It gives you a fine-grained control over the HTTP response, allowing you to return custom objects or responses based on the exception type.  
  you can define other configurations that should apply globally, such as model attributes or response.
* It prevents the duplication of exception handling code in each controller. You get to define it once in a @ControllerAdvice class, ensuring a consistent and centralized approach to exception handling, making our code more maintainable.
* To handle exceptions globally, you need to create a class and annotate this class with this annotation **@ControllerAdvice** (Web MVC projects) or **@RestControllerAdvice** (Web API projects).

**@ControllerAdvice** is designed for Spring Boot MVC applications, it is an annotation that when applied to a class, transforms it into a global controller advisor, in turn, influences the behavior of multiple controllers. Below is what **@ControllerAdvice** looks like.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@ControllerAdvice
public class GlobalControllerAdvice {
    // Exception handling, model attributes, and other configurations
}</pre>

**@RestControllerAdvice** this annotation extends the **@ControllerAdvice.** It is specifically designed for RESTful web services. You use this with an application where the response is in the form of JSON or XML. Below is what it looks like.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@RestControllerAdvice
public class ControllerAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ServiceResponse&lt;String&gt;  
       handleResourceNotFoundException(ResourceNotFoundException ex) {
           return new ServiceResponse&lt;&gt;(ex.getMessage(), false);
    }
}</pre>

**@ExceptionHandler** this annotation is used to handle specific exceptions thrown by controllers. This annotation is used to declare methods within a controller or a class annotated with**@ControllerAdvice.** This annotation allows you to customize the behavior of your applications. Below is what it looks like.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    var response = new ServiceResponse&lt;UserDto&gt;();
        var user = userRepository.findById(id)
                .orElseThrow(() -&gt; new ResourceNotFoundException("User", "id", id));
        UserDto userDto = mapper.toDto(user);
        response.setData(userDto, true);
        log.info("Fetched user =&gt; {}", userDto);
        return response;
}</pre>

Using @ControllerAdvice or @RestControllerAdvice in your Spring Boot application allows you to centralize exception handling and other configurations across multiple controllers in a Spring Boot application.

4. Make use of Lazy Initialization {#h2-4-4-make-use-of-lazy-initialization}
----------------------------------------------------------------------------

The SpringApplication allows you as a developer to initialize your spring boot application lazily. When lazy initialization is enabled, beans get created as they are needed rather than during application startup. Enabling lazy initialization reduces the time that it takes your application to start.

Lazy initialization results in many web-related beans not being initialized until an HTTP request is received.

The downside of lazy initialization is that it delays the discovery of a problem with the application. If you lazily initialize a misconfigured bean, the failure of that bean will not occur during startup and the problem will only show up when the bean is initialized.

You need to ensure that the JVM has sufficient memory to accommodate all of the application's beans and not just those that are initialized during startup. You need to fine-tune the JVM's heap size before you enable lazy initialization.

Lazy initialization can be enabled programmatically using:

* the lazyInitialization method on SpringApplicationBuilder,
* the setLazyInitialization method on SpringApplication.
* the spring.main.lazy-initialization property

5. Make good use of Starter POMs {#h2-5-5-make-good-use-of-starter-poms}
------------------------------------------------------------------------

Just as AutoConfiguration takes away the pain of having to configure common functionalities, Starter POMs removes the pain of looking for and configuring common dependencies in your project.

The spring-boot-starter-data-jpa is a starter for using Spring Data JPA. It includes the following compiled dependencies:

* spring-boot-starter-jdbc: This starter includes the necessary libraries for JDBC.
* hibernate-core: This is the main library for Hibernate, which is an Object-Relational Mapping (ORM) solution.
* spring-data-jpa: This library provides the core Spring Data JPA functionality.
* spring-boot-starter-aop: This library enables Aspect-Oriented Programming in Spring Boot which addresses cross-cutting concerns for cleaner, modular code.
* spring-aspects: This library is typically used when you need the advanced features provided by AspectJ, such as weaving into third-party libraries.

So instead of searching for and adding all these dependencies and worrying about their compatible version, you just need to add one Starter POM --- spring-boot-starter-data-jpa

6. Make use of Spring Boot CLI {#h2-6-6-make-use-of-spring-boot-cli}
--------------------------------------------------------------------

The Spring Boot CLI is a command line tool provided by the Spring Boot team. This tool allows you to create Spring Boot web applications. Obviously, you'll need to install it in order to use it, here is how you install it.

**On Mac:**   
`$ sdk install springboot `  
**On Windows:**   

\`\> scoop bucket add extras
> scoop install spring boot

7. Master your build tool of preference {#h2-7-7-master-your-build-tool-of-preference}
--------------------------------------------------------------------------------------

One way to use Spring Boot to its fullest is to master the build tool of your preference. If you prefer the Maven or Gradle build tool, you have to master it, know its downsides and equally know how to mitigate the downside of your build tool of preference.

One common complaint of the Maven build tool is that is slow and the best way to mitigate this downside is to install and use Mvnd

One common downside of the Gradle build tool is that the methods names are getting changed all the time, so the only way to mitigate this is to keep yourself abreast of the new features and deprecated features that comes with every new release of Gradle.

8. Take Observability Seriously {#h2-8-8-take-observability-seriously}
----------------------------------------------------------------------

[Observability](https://foojay.io/today/effective-coding-with-java-observability/ "Observability") is basically the process of measuring the internal state of your application using its external outputs such as logs, metrics and traces.

Observability in Spring Boot Applications

Actuator Endpoints

One way to ensure that you are using Spring Boot to its full potential is to use the Spring Boot actuator. This library allows you to gain insight into the operational information about your application.

Once this library is on the classpath, several endpoints become available to you by default.

Add the spring-boot-actuator dependency to your package manager to enable the [spring-boot actuator.](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator)

For Maven:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
&lt;/dependency&gt;</pre>

For Gradle:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">dependencies {
   implementation 'org.springframework.boot:spring-boot-starter-actuator'
}</pre>

One thing you need to know is that, Actuator comes with most of its endpoints disabled, leaving just two endpoints enabled /health and /info

To enable all endpoints, go to your application.properties or application.yml file and add the following

For application.properties:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">management.endpoints.web.exposure.include=*</pre>

For application.yml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">management:
  endpoints:
    web:
      exposure:
        include: "*"</pre>

### Some interesting Actuator Endpoints {#h3-9-some-interesting-actuator-endpoints}

* /health: This endpoint shows you the basic health information of your application. It is one of the most commonly used endpoints and is exposed by default.  
  You can integrate this endpoint with external monitoring tools (e.g., Grafana, Prometheus) to get alerts when the health status of your application changes.
* /info: This endpoint will show you the application information. This is useful when you want to expose details like the application name, version, and any custom properties.  
  The details obtained from this endpoint can help other developers or systems understand your application context.
* /beans: This endpoint shows you a comprehensive list of all the Spring beans in your application.  
  This comes in handy when you want to analyze beans dependency, debug dependency injection issues and try to understand the structure of your application.
* /loggers: This endpoint shows you the configuration of loggers in the application.  
  With this endpoint you can dynamically adjust your application log levels, enable/disable logging for specific packages. This comes in handy when you want to do real-time debugging and troubleshooting at runtime.
* /httptrace: This endpoint shows the HTTP trace information of your application. By default, it shows only the last 100 HTTP request-response exchanges.  
  This endpoint is useful for diagnosing and analyzing the behavior of your application's HTTP traffic, and help you identify and resolve issues.
* /metrics: This endpoints the application metrics. These metrics can include things like JVM memory usage, HTTP request counts, and more. This endpoint requires a dependency on micrometer-core.  
  This endpoint helps you monitor your application's performance, detect anomalies, and make informed decisions on how to optimize or adjust your infrastructure.
* /shutdown: When you hit this endpoint, it will gracefully shutdown your Spring Boot application. This is a dangerous operation, so this endpoint should be properly secured.  
  Use this endpoint with caution; use this endpoint in a controlled environment where a graceful shutdown is required.
* /env: This endpoint shows the properties defined in the environment of your application. These properties can include system properties, environment variables, and properties from application.properties or application.yml.  
  This endpoint will help you understand your application's runtime environment, check config values, diagnose and analyze configuration-related issue.
* /mappings: This endpoint shows a list of all @RequestMapping paths in your application.  
  With this endpoint, you can quickly see the mappings of your controllers, and this comes in handy for API documentation.
* /configprops: This endpoint displays a list of collated @ConfigurationProperties.  
  You get to see how your properties are structured, locate misconfigurations, and ensure that your application is using the right configuration.

Spring Boot Actuator provides key insights into application internals via endpoints like /actuator/health, /actuator/metrics, and /actuator/env. These endpoints offer health status, detailed metrics, and environmental properties.

### Metrics Collection with Micrometer {#h3-10-metrics-collection-with-micrometer}

[Micrometer,](https://digma.ai/couch-to-fully-observed-code-with-spring-boot-3-2-micrometer-tracing-and-digma/) a versatile metrics utility, supports various monitoring systems like Prometheus, Datadog, and more. It offers a range of instrument types and is a go-to solution for application metrics, providing flexibility in choosing monitoring tools.

**Logging in Spring Boot**   

Spring Boot's logging relies on Commons Logging but remains adaptable to different log implementations. It offers default configurations for Logback, Log4J2, and Java Util Logging, simplifying logging setup and compatibility.

**Distributed Tracing using OpenTelemetry**   

OpenTelemetry provides end-to-end tracing and observability in microservice-based applications. Spring Boot seamlessly integrates with OpenTelemetry, supporting agent instrumentation and Micrometer Tracing.

**Use Ecosystem tools to bootstrap your observability and make observability Data meaningful**   

Continuous Feedback tools such as digma.ai can make the journey from no observability to full observability much easier, but more importantly, make sure that observability data becomes impactful -- instead of just pretty dashboards. Digma and similar tools can analyze what the observability data means about your code to detect common anti-patterns and issues and provide you with feedback as-you-code.

9. Test, Test, Test {#h2-11-9-test-test-test}
---------------------------------------------

With Spring Boot, writing unit tests and integration tests became less of a headache and more of a satisfying routine.

Spring Boot is an excellent choice for developing applications with robust [testing](https://foojay.io/today/foojay-podcast-43/ "testing") features. For the following reasons:

* Spring Boot integrates seamlessly with popular testing frameworks like JUnit, Mockito, and Spring Test.
* Spring Boot provides a wide range of testing annotations, including @SpringBootTest, @DataJpaTest, and @WebMvcTest, which help configure the application context for specific types of testing, streamlining the process.
* Spring Boot allows you to leverage the vast ecosystem of Spring Frameworks, such as Spring Security for security testing and Spring Data for database-related testing.
* Spring Boot support for mocking frameworks like Mockito gives you access to utilities for generating test data, enabling you to build even more robust applications.
* Spring Boot integrates well with [TestContainers](https://www.atomicjar.com/2023/10/beyond-pass-fail-a-modern-approach-to-java-integration-testing/) to fast-track your integration tests.
* If you're building a distributed application, testing is non-negotiable.
* One great thing that happened to Test-Driven Development was the use of Testcontainers. The development is fairly new and thankfully Spring Boot has an integration for Testcontainers.

### What are TestContainers? {#h3-12-what-are-testcontainers}

Testcontainers is a powerful testing library that provides easy and lightweight throwaway instances of common databases for bootstrapping integration tests with real services wrapped in Docker containers. With Testcontainers, you can write and run tests talking to the same type of services or databases you use in production without mocks or in-memory services.

### Benefits of TestContainers {#h3-13-benefits-of-testcontainers}

Reproducibility: Each test runs in a clean environment as this eliminates any issues that can be caused by stale or conflicting state.

Isolation: Testcontainers automatically create isolated environments for testing. This isolated environment helps to avoid interference with external dependencies.

Ease of Use: There is a seamlessly integration between TestContainers with popular testing frameworks such as JUnit, Mockito, TestNG etc, hence, reducing the complexity of setting up separate test environmentsPredictability: What this means is that you can use the same environment (a container) to host your software regardless of if you are building, testing, or deploying your application in production.

Conclusion: Lookahead Spring Boot 3.2 {#h2-14-conclusion-lookahead-spring-boot-3-2}
-----------------------------------------------------------------------------------

We're looking forward to:

* Virtual threads #ProjectLoom
* Initial CRaC support
* More sophisticated observability with @micrometerio
* JdbcClient, RestClient
* ActiveMQ @testcontainers/@docker compose support
* Kotlin 1.9.0
* Loom
* JdbcClient
* RestClient
* Revisited binding
* Observability improvements
* SSL improvements
* GraalVM/AOT improvement

I hope this article has inspired you to use Spring Boot to its fullest in the Spring Way. If you're interested in learning more about Continuous Feedback and different tools and practices that can be helpful, please consider joining our [Slack group.](https://github.com/digma-ai/digma "Slack group.")
