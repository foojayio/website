---
title: "Multiple Spring Boot Applications in the Same Project"
slug: "multiple-spring-boot-apps-same-project"
date: "2021-12-08T16:42:44+00:00"
lastmod: "2021-12-08T16:42:45+00:00"
description: "I frequently use Spring Boot in my demos. The latest one is no different, showing how to achieve CQRS using two different code paths."
canonical: "https://blog.frankel.ch/multiple-spring-boot-apps-same-project/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2021/12/multiple-spring-boot-apps-same-project/benches-ge9601ea94_1280.jpg"
categories:
  - "Spring"
  - "Use Cases"
tags:
related_posts:
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "spring-i-o-2026-field-notes-from-barcelona"
  - "spring-boot-actuator-health-for-microprofile-developers"
enlighterjs: true
frozen: false
---

I frequently use the [Spring Boot](https://spring.io/projects/spring-boot) framework in my demos. The latest one is no different. It shows how to achieve using two different code paths:

* the command part is implemented via [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* the query part via [jOOQ](https://www.jooq.org/)

My use case is a banking application that offers a REST layer allowing clients to call any parts. Demoing the query part is easy enough with `curl` as the URL is not complex:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">curl localhost:8080/balance/123          // 1</pre>

1. Query the balance of the account `123`

On the other hand, creating a new operation, *e.g.* , a credit, requires passing data to `curl`. While it's feasible to do that, the payload's structure itself *is* complex as it's part JSON. Hence, it's a risk to use `curl` to demo the command part in front of a live audience. I tend to avoid unnecessary risks, so I thought about a few alternatives.

My first idea was to prepare the command in advance, to copy-paste during the demo. I wanted to have a couple of different operations, so I'd have to:

* Either prepare a single command, paste it and change it before running it
* Or prepare all the different commands

To me, both were too awkward.

Another option was to create another `@SpringBootApplication` annotated class in the same project:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@SpringBootApplication
public class GeneratorApplication {

    @Bean
    public CommandLineRunner run() {
        var template = new RestTemplate();
        return args -&gt; {
            var operation = generateRandomOperation();            // 1
            LongStream.range(0, Long.parseLong(args[0]))          // 2
                .forEach(
                    operation -&gt; template.postForObject(          // 3
                        "http://localhost:8080/operation",
                        operation,
                        Object.class));
        };
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(GeneratorApplication.class)
                .run(args);
    }
}</pre>

1. Generate a random `Operation`, somehow
2. Get the number of calls from the argument
3. Call the URL of the main web application

When I launched this application after the other web one, it failed. There are two reasons for that:

1. Both applications share the same Maven POM. As the `spring-boot-starter-web` is on the classpath, the generator application tries to launch Tomcat. It fails because the first application did bind the default port.
2. Spring Boot relies on component scanning by default. Hence, the web application scans the generator application and its declared beans and creates them. It's possible to redefine some of the beans this way. However, the webapp also creates the `CommandLineRunner` bean above. It thus "posts to itself" while its server is not ready yet.

The most straightforward solution is to move each application's classes in their own dedicated Maven module. You need to create a POM in each module with only the necessary dependencies. Plus, I need to use a couple of classes in the runner from the webapp. While I could duplicate them in the other module, it's extra work and complexity.

To prevent classpath scanning, we move each application class into its package. Note that it doesn't work when packages have a parent-child relationship: they must be siblings.

To create beans of specific classes, we need to rely on particular annotations depending on their nature:

* For JPA entities, `@EntityScan`, pointing to the package to scan
* For JPA repositories, `@EnableJpaRepositories`, pointing to the package to scan
* For other classes, `@Import` points to the classes to generate bean from

The final step is to prevent the generator application from launching the webserver. You can configure it when launching the application.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@SpringBootApplication
@EnableJpaRepositories("org.hazelcast.cqrs")                      // 1
@EntityScan("org.hazelcast.cqrs")                                 // 2
public class GeneratorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GeneratorApplication.class)
                .web(WebApplicationType.NONE)                     // 3
                .run(args);
    }

    // Command-line runner
}</pre>

1. Scan for JPA repositories
2. Scan for JPA entities
3. Prevent the web server from launching

It works as expected, and we can finally reap the benefits of the setup.

For real-world applications, you'll probably create a self-executing JAR. You'll need to develop a way to configure the main class during the build, one for each application. I think it's better not to do it and keep this as a demo hack.

**To go further:**

* [Create a Non-web Application](https://docs.spring.io/spring-boot/docs/2.6.x/reference/htmlsingle/#howto.application.non-web-application)
* [Use Spring Data Repositories](https://docs.spring.io/spring-boot/docs/2.6.x/reference/htmlsingle/#howto.data-access.spring-data-repositories)
* [Separate @Entity Definitions from Spring Configuration](https://docs.spring.io/spring-boot/docs/2.6.x/reference/htmlsingle/#howto.data-access.separate-entity-definitions-from-spring-configuration)
* [Importing Additional Configuration Classes](https://docs.spring.io/spring-boot/docs/2.6.x/reference/htmlsingle/#using.configuration-classes.importing-additional-configuration)

*Originally published at [A Java Geek](https://blog.frankel.ch/multiple-spring-boot-apps-same-project/) on December 5^th^, 2021*

*[CQRS]: Command Query Responsibility Segregation
