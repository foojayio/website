---
title: "MicroProfile Config for Java SE"
slug: "microprofile-config-for-java-se"
date: "2022-10-13T16:10:31+00:00"
lastmod: "2022-10-13T16:10:32+00:00"
description: "How can you use MicroProfile Config in a pure Java SE environment? What libraries support this, what are the supported features, etc?"
authors:
  - "rudy-de-busscher"
image: "microprofile.png"
categories:
  - "Java Core"
  - "Microservices"
tags:
related_posts:
  - "evolution-of-microservices"
  - "how-to-bring-your-java-microservices-to-the-cloud"
  - "microprofile-metrics-with-prometheus-and-grafana"
frozen: false
---

The [MicroProfile Config](https://microprofile.io/microprofile-config/) specification is one of those attempts to create a specification around application configuration for the Java Enterprise world. In the past, there were already some other attempts within Java EE to define this specification for Java EE, but they were never finalised.

Currently, another attempt is in progress with Jakarta EE Config but since MicroProfile is closely related to Jakarta EE, it is the best available solution today. That is the reason why almost all Jakarta EE runtimes also support MicroProfile Config and the other specifications of the MicroProfile umbrella.

Although the MicroProfile Config specification is based on the Context and Dependency Injection specific actions (Jakarta CDI) there is a large part that covers programmatic retrieval of configuration values.

It is namely also the idea to use this config specification for the configuration of the runtime itself. This means it must also work, at least large parts of it, on plain Java SE.

We look a bit more in detail in this article about this plain Java SE aspect of MicroProfile config and 2 libraries that implement recent versions of the specification.

## Programmatic access

The programmatic access of the `Config` object allows you to get the configuration values from the sources that are available for your application.

```java
Config config = ConfigProvider.getConfig();
```

This *Config* object has methods to retrieve and convert a value from the environment.

```java
String value = config.getValue("key", Integer.class);
Boolean flag = config.getOptionalValue("otherKey", Boolean.class);
```

The class type on the method determines the *Converter* that is used to transform the String value obtained from the *ConfigSource* to the desired type.

There are many Converters available by default but also Implicit Converters can be used if the specified class has a static method with a certain method signature or constructor with a String parameter.

You can also define converters yourself and define the order in which they are tried. The order is determined by the value specified in the `@Priority`, or a default value is assumed when not specified. Depending on the version of the MicroProfile config that is supported by the implementation, this Priority is the javax or jakarta one. See further on for some examples.

Converters are picked up through the ServiceLoader mechanism or can also be defined programmatically when using the ConfigBuilder. Have a look at the [specification](https://github.com/eclipse/microprofile-config/blob/master/spec/src/main/asciidoc/converters.asciidoc) if you want to learn more about this.

```java
@Priority
public class MyConverter implements Converter {
}
```

## Supported features

Some MicroProfile Config implementations have split up their project into 2 parts. A part that only contains those features that can be used in Java SE, and another artifact that adds the CDI integration on top of the first part.

What features are supported when using the pure Java SE version?

* ConfigSources, the 3 default implementations with their default ordinal values, and the possibility to define custom ones through the ServiceLoader mechanism.
* Custom ConfigSourceProvider's can be loaded through the ServiceLoader mechanism.
* Converter, the implicitly defined one as specified in the specification, and the possibility to define custom converters using the ServiceLoader mechanism.
* Support for optional values
* Support for expressions where a value is a result of combining other configuration values and constant expressions.
* Support for Config Profile defining the application phase (dev, test, ...) on the property and ConfigSource level.
* Support for ConfigBuilder and creating custom Config instances.

Other like the `@ConfigProperty` and `@ConfigProperties` which clearly required the CDI engine to function, are not included in those artifacts.

## Testing

How can you test code that makes use of the *ConfigProvider*? Since the Config is created once, for each Classloader, you might think it is difficult to have several tests where various values or the absence of a value for a key can be tested.

But you can actually work around this relatively easily. You can create a custom ConfigSource for your tests (don't forget to define it in a service loader configuration file) that retrieves values from a ThreadLocal Map. Resetting that map after each test allows you to define test values for each test separately and works also when you run tests in parallel.

The gist of that solution can be found in this [Github gist](https://gist.github.com/rdebusscher/f344ec2c6eb777f799ee775425745627).

## Use Case

What are typical use cases for MicroProfile Config on Java SE? Defining configuration values can be important for any kind of application, not only web applications. So all applications that you have, might benefit from a proven reusable framework to define the configuration, not only when running web applications on a runtime.

* Command line programs that perform some tasks and need some configuration can be implemented using MicroProfile Config.
* JavaFX or programs using another desktop UI technology can also make use of this configuration system.

The implementations that I will discuss next, also support native compilation with GraalVM, so also your native compiled programs can make use of it.

## Implementations

I cover two implementations that have an artifact available that cover the MicroProfile Config functionality that can run on pure Java SE.

### SmallRye Config

The Configuration framework that is used within Quarkus and OpenLiberty can use on Java SE only. It is based on version 2.0 of the specification which is still using the javax namespace. You can make use of it in your application by adding the following dependencies to your project.

```java
 <dependency>
    <groupId>io.smallrye.config</groupId>
    <artifactId>smallrye-config-core</artifactId>
    <version>2.12.0</version>
</dependency>

<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>1.3.5</version>
</dependency>
```

The jakarta annotation api one is needed since the order of the converters is defined through the `@Priority` annotation as mentioned above and is not included within the core SmallRye config dependency.

### Atbash MP Config

The [Atbash Runtime](https://www.atbash.be/2022/04/03/testing-the-jakarta-ee-core-profile-with-atbash-runtime/) product, a playground for the new Jakarta core profile, also has [configuration support](https://www.atbash.be/2022/10/03/a-microprofile-config-implementation-for-plain-java-se/) and can also be used in pure Java SE. This library is already based on the Jakarta namespace as it is intended to be used with Jakarta EE 9.1 and Jakarta EE 10. It is based on the Microprofile Config 3.0 specification.

You only need to add the following dependency to your Java SE project to have it available.

```xml
<dependency>
    <groupId>be.atbash</groupId>
    <artifactId>mp-config-se</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Conclusion

Configuration is a very important aspect of any application, not only your web application.

So almost all frameworks and java enterprise runtimes have already a solution available, but there is still nothing defined for Java SE.

There were a few attempts in the past but today, using a library that implements MicroProfile config, can be used on pure Java SE as an option.

In this article, we looked at what is supported on Java SE, how you can configure your project to make use of it, and showed an example of writing tests when using MicroProfile Config on Java SE.
