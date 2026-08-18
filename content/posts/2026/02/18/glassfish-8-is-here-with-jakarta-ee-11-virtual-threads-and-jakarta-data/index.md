---
title: "GlassFish 8 is here with Jakarta EE 11, virtual threads, and Jakarta Data"
date: "2026-02-18T16:06:02+00:00"
lastmod: "2026-05-13T10:22:04+00:00"
description: "The final version of Eclipse GlassFish 8 is here, released on 5 February 2026. As a GlassFish committer, I'd like to share what it brings for the Java - by Ondro Mihalyi"
authors:
  - "ondro-mihalyi"
image: "glassfish-8-smaller.jpeg"
categories:
  - "Jakarta EE"
  - "Payara"
  - "Release Notes"
related_posts:
  - "glassfish-8-0-2-released"
  - "reflections-on-2024-a-remarkable-year-for-omnifish-glassfish-piranha-and-jakarta-ee"
  - "glassfish-is-rolling-forward-whats-new"
  - "glassfish-embedded-a-simple-way-to-run-jakarta-ee-apps"
frozen: false
---

The final version of Eclipse GlassFish 8 is here, released on 5 February 2026. As a GlassFish committer, I'd like to share what it brings for the Java community and some behind-the-scenes stories from the development process

Recently my company OmniFish published an announcement article introducing GlassFish 8.0.0 and covering all the major features: [GlassFish 8 Released: Enterprise-Grade Java, Redefined](https://omnifish.ee/glassfish-8-released-enterprise-grade-java-redefined/)

Here, I'd like to report on this article and also share some behind-the-scenes stories about my involvement in GlassFish 8 and why I think the new features matter for solving real pain points in the Java community.

## What makes GlassFish 8 modern and developer-friendly

First and foremost, GlassFish 8 is fully compatible with Jakarta EE Platform 11 - meaning all Jakarta EE 11 APIs are available. Runs on Java 21 and 25. And will run on Java 29 LTS too as soon as it's out, that's our plan. On disk, the size is 150 MB, the size of the runnable GlassFish JAR is 87 MB. Requires 80 MB of RAM to run a REST microservices, or just 50MB of RAM if run using the runnable JAR.

GlassFish 8 brings all the modern advancements in Java and Jakarta EE. The biggest highlights are virtual threads and the new Data API. It also brings new Jakarta NoSQL support and MicroProfile Health. This empowers developers with new out-of-the-box APIs, mainly for simplified and flexible data access. And it also enable virtual threads in configuration to tune apps and monitor apps readiness and health using a standard mechanism. More functionality and options without additional dependencies in your apps and Maven/Gradle build setup.

## The story of Jakarta Data in GlassFish 8

I was involved with the Jakarta Data implementation in GlassFish 8 from the very beginning and I'd be happy to share with you how it all happened. In the beginning, we had not easy way to support Data in GlassFish. None of the existing implementations were suitable. The one in Hibernate requires Hibernate ORM but GlassFish uses EclipseLink. The one in OpenLiberty is very tightly coupled with other OpenLiberty components. And JNoSQL only supported NoSQL, which was only half of the solution for GlassFish, which supports JPA.

In the end, I started looking into extending JNoSQL to also support JPA entities, to get a full solution into GlassFish. A simple proof of concept worked and it passed a few Data TCK tests. A year went by, and with some help of others in the community, JNoSQL passed the Java SE part of the Data TCK, with any JPA provider. Passing the whole Data TCK in GlassFish, including the part that tests integration with other Jakarta EE APIs, took a bit longer. As a result, you can now write applications that access database using Data repositories, and you can choose to use JPA entities or NoSQL entities backed by any driver that the JNoSQL project supports. For example Mongo DB or Cassandra.

Here's a simple example of a Data repository:

    @Repository
    public interface ProductRepository extends CrudRepository<Product, Long> {

        // Annotated query using @Find and @By annotations
        @Find
        List<Product> findProducts(@By("name") String name);

        // Custom query using Query Language
        @Query("WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY price")
        List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

        // Query defined by method name
        // Deprecated but supported for easy migration from similar APIs
        List<Customer> findByLastName(String lastName);
    }

`Product` can be either JPA or NoSQL entity, it's your choice. `Long` is the type of the ID of the entity or the key, depending on the type of the database.

You can find detailed info with a lot more examples in the [GlassFish Development Guide](https://glassfish.org/docs/latest/application-development-guide.html#using-jakarta-data-repositories).

## The story of virtual threads support in GlassFish 8

As you probably know, virtual threads originated as generally available feature in Java 21, long before GlassFish 8 was ready. GlassFish 7 supported Java 21 in the next version after it. First, OmniFish created a plugin for GlassFish that allowed using virtual threads for HTTP listeners. Then we collaborated with the community to improve it and donate it to Grizzly project that powers HTTP listeners in GlassFish. And then we found out that the Eclipse Grizzly project was dormant, without any active committers, and the whole effort got stuck.

Luckily, after a while, we managed to get Grizzly project merged into the GlassFish project, which allows us full control over it and its releases. It allowed us to release Grizzly 5 with the new virtual threads executor and integrate it into GlassFish. Turning into virtual threads is now as easy as running:

    asadmin set server.thread-pools.thread-pool.http-thread-pool.virtual=true

Or, in Admin console, tick the "Use Virtual Threads" checkbox for the `http-thread-pool`.

Or, when running GlassFish JAR, place the following line in `glassfish.properties`:

    command.vt=set server.thread-pools.thread-pool.http-thread-pool.virtual=true

Concurrent resources like managed executors also support virtual threads via the new "`virtual`" attribute in Jakarta EE annotations. We collaborated with Payara to support virtual threads in the Concurro component, which is used in both GlassFish and Payara Server.

## GlassFish 8 beyond Jakarta EE 11

GlassFish 8 is not only about Jakarta EE 11. As I mentioned earlier, it also contains some MicroProfile 7.1 APIs: Config, JWT, REST Client, and Health. The latter one is newly added to GlassFish, since 7.1, released just 2 months before GlassFish 8.0.

And we went a little further to integrate Jakarta EE and MicroProfile in GlassFish. We saw a great synergy between MicroProfile JWT and Jakarta Security. In fact, MicroProfile JWT in GlassFish is already implemented as a custom Jakarta Security authentication mechanism. Jakarta EE 11 brings the ability to programmatically combine multiple built-in mechanisms exposed via standard qualifiers. However, MicroProfile JWT doesn't specify such a qualifier. So we did it. We add a qualifier to GlassFish API to allow injecting the built-in MicroProfile JWT mechanism so that you can combine it with other built-in mechanisms freely. For example, secure REST endpoints with JWT and other URLs via a login form.

## Production-ready and supported

For a long time, there has been a narrative that GlassFish wasn't suitable for production environments. OmniFish has been working hard to change this. It started with GlassFish 7 where [we tremendously improved stability](https://omnifish.ee/omnifish-announces-support-for-eclipse-glassfish/). GlassFish 8, as explained in the article by OmniFish, makes it clear that this trend continued, with a lot of structural improvements in GlassFish 7.1 and 8. Since 2022, GlassFish has been on a new trajectory, with frequent updates and the backing of commercial support from OmniFish. This means companies can now confidently build and deploy mission-critical applications on GlassFish, knowing that we have a reliable and supported platform and strong partner to provide support and assistance.

If you remember GlassFish from a few years ago, you might have heard it wasn't suitable for production. That changed in 2022. Since then, we've been on a mission to improve stability, release frequently, and make GlassFish a solid choice for production workloads. GlassFish 7 brought [major stability improvements](https://omnifish.ee/omnifish-announces-support-for-eclipse-glassfish/), and GlassFish 8 continues that trend with structural improvements in 7.1 and 8. For companies that need commercial support, [OmniFish](https://omnifish.ee/glassfish-support/){#https://omnifish.ee/glassfish-support/} provides that backing. But even without it, the open-source project is active, well-maintained, and has a growing community.

## Dive deeper

Thank you for reading this far. I hope you enjoyed reading about my personal experience as a GlassFish developer and you now get my excitement from the new GlassFish release. I also highly recommend reading the GlassFish 8 announcement from OmniFish, and check out the [GlassFish website](https://glassfish.org) for more info:

[Read the OmniFish announcement about the GlassFish 8.0 release](link%20to%20the%20original%20blog%20post)

If you want to get started with GlassFish 8 right away, here are some useful links:

* [Download GlassFish 8](https://glassfish.org/download)
* [OmniFish Blog](https://omnifish.ee/blog/)
* [OmniFish on YouTube](https://www.youtube.com/@omnifishnews)

More information:

* [GlassFish 8.0 Delivers Compatibility with Jakarta EE 11, Enhanced Security and Improved Data Access](https://www.infoq.com/news/2026/02/glassfish-8-released) (InfoQ)
* [GlassFish 8.0.0 Release Notes](https://github.com/eclipse-ee4j/glassfish/releases/tag/8.0.0)
* [New Features in Jakarta EE 11, with Examples](https://omnifish.ee/new-features-in-jakarta-ee-11-with-examples/) (OmniFish)
* [](https://omnifish.ee/glassfish-is-rolling-forward-whats-new/)[Level up from Payara: Why GlassFish Is much better](https://omnifish.ee/level-up-from-payara-why-glassfish-is-much-better/) (OmniFish)
* [The Eclipse Foundation's Jakarta EE Working Group Announces Jakarta EE 11 Release](https://newsroom.eclipse.org/news/announcements/eclipse-foundation%E2%80%99s-jakarta-ee-working-group-announces-jakarta-ee-11-release) (Eclipse Foundation)

{{< img src="omnifish-logo-transparent-400px-margin.png" class="alignleft size-full is-resized" width="400" height="400" style="width:200px;height:200px" >}}

**OmniFish - Modern Jakarta EE Runtimes**

* Eclipse GlassFish Support
* Support for Payara, Quarkus, Piranha
* Jakarta EE Consulting \& Training

Contact OmniFish at their [contact page](https://omnifish.ee/contact-us/), or LinkedIn at [@OmniFishEE](https://www.linkedin.com/company/omnifish/).
