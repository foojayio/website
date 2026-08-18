---
title: "MicroStream - Part 5: Caching, Integrations and Clustering"
date: "2022-07-06T07:11:38+00:00"
lastmod: "2022-07-06T07:13:35+00:00"
description: "In this article, we discuss MicroStream caching and clustering functionality. We also indicate the available integrations."
authors:
  - "rudy-de-busscher"
image: "microstream.png"
categories:
  - "Databases"
  - "DataEngineering"
  - "nosql"
related_posts:
  - "microstream-part-1-what-is-it"
  - "microstream-part-2-configure-the-storage-manager"
  - "microstream-part-3-storing-data"
  - "microstream-part-4-serialisation-engine"
frozen: false
---

**In this last article of the series, we cover a few other MicroStream features: caching, clustering, and the integrations into other frameworks.**

We have now covered three main aspects of MicroStream:

* We have seen how you can [configure the StorageManager](https://foojay.io/today/microstream-part-2-configure-the-storage-manager/) to define where data is stored.
* We have discussed the [strategy](https://foojay.io/today/microstream-part-3-storing-data/) that you should follow to make sure the changes are also reflected in the storage so that we do not lose data.
* We described the [serialisation framework of MicroStream](https://foojay.io/today/microstream-part-4-serialisation-engine/) that stores the objects in a binary format so that it survives a process restart.

In this last article of the series, we cover a few other features of MicroStream: caching, integrations into other products, and we discuss a major upcoming feature.

## Caching

The MicroStream Cache project provides you with an implementation of the Cache specification. This specification describes how you can keep values cached for later usage if creating or retrieving certain results are too expensive and don't change often. Besides the implementation of this specification, MicroStream also adds the functionality to persist the values so that the cache is also available at the next start of your process.

If you want to make use of it, you can add the following dependency to your project

```xml
    <dependency>
        <groupId>one.microstream</groupId>
        <artifactId>microstream-cache</artifactId>
        <version>${microstream.version}</version>
    </dependency>
```

You now have the MicroStream's implementation of JCache available and can start creating caches like this one. We first create a configuration for the cache we need where we can provide a storage manager that stores the cache entries. With this configuration, we can create the actual cache from the *CacheManager*.

```java
    CacheConfiguration<String, String> configuration = CacheConfiguration
            .Builder(String.class, String.class, "jCache", storageManager)
            .expiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE))
            .build();
    CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

    Cache<String, String> capitals = cacheManager.createCache("jCache", configuration);
```

But MicroStream provides more caching integrations than just JCache. You can also define it as a secondary cache for Hibernate for example. This gives it an additional functionality, your secondary cache can be restored from a previous run and thus provide more caching functionality.

The documentation describes how you can set up this [caching functionality](https://docs.microstream.one/manual/cache/index.html) when using Hibernate as a JPA provider or within the Spring framework.

## Integrations

Speaking of the Spring framework, there are several integrations available of MicroStream.

One of them is the integration by the MicroStream team itself with the Spring Boot framework. It allows the configuration of the Storage Manager, storing the data in a directory, within a database, or cloud storage. All storage types are supported. The integration code defines the StorageManager as a Spring bean that can be used throughout your application code.

You can find the info around the integration at the [GitHub project](https://github.com/microstream-one/microstream/tree/master/integrations/spring-boot).

We also provide integration with the MicroProfile compatible runtimes that is based on CDI and MicroProfile Config. This integration also defines the StorageManager as a CDI bean but also provides some additional annotations. With these annotations, you no longer need to write any MicroStream-related java statement in your code and the annotations are sufficient to store the data at the end of the method.

The StorageManager is configured through the MicroProfile config configuration values in a similar way as with the SpringBoot integration. The `@Storage` annotation indicates the root object and with the `@Store` CDI interceptor the collections within the root are saved.  

For the next release, this integration will be improved to handle the changes in your object graph better.

You can read about this integration at the [GitHub Project](https://github.com/microstream-one/microstream/tree/master/integrations/cdi).

But there are also integrations done by some microservice product creators. The Helidon Team created an integration with MicroStream shortly after the code was open-sourced in 2021. They not only provide the StorageManager that is configurable through configuration properties, but you also have the option to get some health and metrics information about the MicroStream.

And with this integration, you no longer need to add the MicroStream dependency to your application as it comes through the Helidon runtime itself.

You can read more about this integration in this [Medium blog](https://medium.com/helidon/microstream-helidon-brave-performance-combination-12d3d288aa).

And the MicroStream project is recently also integrated within the Micronaut framework. It provides similar functionality, multiple sets of configurations for the StorageManager, and some additional annotations to decorative indicate what needs to be stored by the StorageManager.

You can follow this [step-by-step tutorial](https://micronaut-projects.github.io/micronaut-microstream/snapshot/guide/) to learn more about the integration.

As mentioned, we are working on improved integrations and also providing integrations for more products.

## MicroStream Cluster

The OpenSource version of MicroStream is a single node implementation. Only a single JVM is allowed to write to a certain store. When multiple JVMs are used, the storage can be corrupted as the Object Graph of the different JVM instances is not identical.

You can work around this limitation yourself in the free version by using the MicroStream communication module to replicate the object graph between instances. You can create some kind of master-slave architecture so that there is still only a single instance that writes to the storage. Reading from a storage by multiple instances is allowed.

You can read more about this solution in the [reference documentation](https://docs.microstream.one/manual/communication/index.html).

Later this year, we will launch a new product called MicroStream Cluster that can handle an installation in a cluster for you, automatically. It is a Kubernetes-based solution that synchronises the Object graph for you so that the entire cluster looks like one and can store the data in a storage.

More information will follow later on that will allow you to run MicroStream in a large-scale environment very easily.

## Resources

* [MicroStream website](https://microstream.one/)
* [MicroStream reference manual](https://docs.microstream.one/manual/intro/welcome.html)
* [GitHub code repository](https://github.com/microstream-one/microstream)
