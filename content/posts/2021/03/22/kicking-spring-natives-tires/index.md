---
title: "Kicking Spring Native's Tires with GraalVM"
date: "2021-03-22T17:18:05+00:00"
lastmod: "2021-03-22T17:18:08+00:00"
description: "Despite all the \"magic\" of Spring Boot, Spring Native handles most of GraalVM's required configuration out-of-the-box!"
canonical: "https://blog.frankel.ch/kick-spring-native-tires/"
authors:
  - "nicolas-frankel"
image: "logo.png"
categories:
  - "Performance"
  - "Research"
tags:
related_posts:
  - "diving-into-jvm-framework-monitoring-and-profiling"
  - "kotlin-faas-impossible-union"
  - "idempotent-spring-boot-starter"
  - "five-java-developer-must-haves"
frozen: false
---

I've been playing with GraalVM Ahead-Of-Time compilation capability since I became aware of it. As a long-time Spring *aficionado* , I carefully monitored the efforts that the engineers at Tanzu have put into making Spring AOT-compatible. Recently, they announced [the beta version](https://spring.io/blog/2021/03/11/announcing-spring-native-beta) of the integration.

In this post, I want to check how easy it is to produce a (working!) Docker image from an existing Spring Boot application.

### Introduction

GraalVM provides many different features. Among them, the component known as Substrate VM allows to AOT-compile regular bytecode to a native executable. The process "walks" the application starting from the `main` method at build time. Substrate VM leaves out the code that it doesn't follow out from the resulting binary.

For Spring applications, that's a big issue. The framework does a lot of work at runtime *e.g.*, classpath scanning and reflection.

The usual way to cope with this limitation is to record **all** interactions with the application running on a JVM via a Java agent provided by Graal VM. At the end of the run, the agent dumps out all recorded interactions into dedicated configuration files:

* Reflective access
* Serialized classes
* Proxied interfaces
* Resources and resource bundles
* JNI

This option is compelling and allows one to create native images out of nearly every possible Java application. It comes with a couple of downsides, though, as you need:

1. A complete GraalVM distribution that provides the agent
2. A test suite that tests every nook and cranny of the application
3. A process that executes the suite and creates the configuration files with every new release

None of these items are complicated, but the process is time-consuming and error-prone. It can be automated, but there's always a risk that a specific release forgets to test a particular use-case and crashes when deployed.

### Experimenting with Spring Native

In the true Spring spirit, Spring Native aims to ease the configuration. The main idea is to provide "hints" directly in the code. A dedicated plugin will use these hints and generate the required configuration files. The Spring team has already provided those hints for the framework's code. You can also annotate your application's code if necessary.

To experiment with Spring Native, I used my [imperative-to-reactive](https://github.com/hazelcast-demos/imperative-to-reactive/tree/kotlin) demo code. It offers a couple of challenges concerning AOT:

* It's a Spring application
* I use annotations and rely on runtime reflection and classpath scanning
* I use Kotlin
* I use H2, an in-memory database
* Finally, I cache serialized entities within an embedded Hazelcast instance. This is important as serialization is part of the improvements that GraalVM's latest version provides.

The first step is to make the application compatible with GraalVM. We need to remove [Blockhound](https://github.com/reactor/BlockHound) from the code. Blockhound allows verifying that no blocking code runs in unwanted places. It's a Java agent that requires a JDK, not a JRE. It's great for a demo, but it has nothing to do with a production application.

At the moment of this writing, GraalVM offers two versions of Java, 8 and 11. Since the demo initially uses Java 14, we need to downgrade Java's version from 14 to 11. Since the project is in Kotlin anyway, it has no other impact.

The second step is to add a dependency and a plugin to the POM. I put both into a dedicated profile so that the application can run "normally". These are hosted outside of Maven Central in dedicated Spring repositories.

```xml
<profiles>
  <profile>
    <id>native</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.springframework.experimental</groupId>
          <artifactId>spring-aot-maven-plugin</artifactId>
          <version>0.9.0</version>
          <executions>
            <execution>
              <id>generate</id>
              <goals>
                <goal>generate</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
    <dependencies>
      <dependency>
        <groupId>org.springframework.experimental</groupId>
        <artifactId>spring-native</artifactId>
        <version>0.9.0</version>
      </dependency>
    </dependencies>
  </profile>
</profiles>
<repositories>
  <repository>
    <id>spring-release</id>
    <url>https://repo.spring.io/release</url>
  </repository>
</repositories>
<pluginRepositories>
  <pluginRepository>
    <id>spring-release</id>
    <url>https://repo.spring.io/release</url>
  </pluginRepository>
</pluginRepositories>
```

With this configuration snippet, one can create a native image with the `native` profile:

```bash
mvn spring-boot:build-image -Pnative
```

### First Hurdles

The AOT compilation process takes a long time. It should succeed (though it displays some stack traces), and in the end, it produces a Docker image. You can run the image with:

```
docker run -it --rm -p8080:8080 docker.io/library/imperative-to-reactive:1.0-SNAPSHOT     #1
```

1. I use `--rm` so it removes the container after it has run and doesn't waste disk space

Unfortunately, this fails with the following exception:

```
Caused by: java.lang.ClassNotFoundException: org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryConfigurations$PooledConnectionFactoryCondition
    at com.oracle.svm.core.hub.ClassForNameSupport.forName(ClassForNameSupport.java:60) ~[na:na]
    at java.lang.Class.forName(DynamicHub.java:1260) ~[na:na]
    at org.springframework.util.ClassUtils.forName(ClassUtils.java:284) ~[na:na]
    at org.springframework.util.ClassUtils.resolveClassName(ClassUtils.java:324) ~[na:na]
    ... 28 common frames omitted
```

It seems that Spring Native missed this one. We need to add it ourselves. There are two ways to do that:

1. Either via annotations from the Spring Native dependency
2. Or via standard GraalVM config files

In the above section, I chose to set Spring Native in a dedicated Maven profile. For that reason, let's use regular configuration files.

```json
[
{
  "name":"org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryConfigurations$PooledConnectionFactoryCondition",
  "methods":[{"name":"<init>","parameterTypes":[] }]
}
]
```

Building and running again yields the following:

```
Caused by: java.lang.NoSuchFieldException: VERSION
    at java.lang.Class.getField(DynamicHub.java:1078) ~[na:na]
    at com.hazelcast.instance.BuildInfoProvider.readStaticStringField(BuildInfoProvider.java:139) ~[na:na]
    ... 79 common frames omitted
```

This time, a Hazelcast-related static field is missing. We need to configure the missing field, re-build and re-run again. It still fails. Rinse and repeat: I'll spare you the details; please check the [repo](https://github.com/hazelcast-demos/imperative-to-reactive/tree/native) if you're interested.

Because I configure Hazelcast with XML, the whole XML initialization process is needed. At some point, we also need to keep a resource bundle in the native image:

```json
{
"bundles":[
  {"name":"com.sun.org.apache.xml.internal.serializer.XMLEntities"}
]
}
```

Unfortunately, the build continues to fail. It's still an XML-related exception **though we configured the class correctly**!

```
Caused by: java.lang.RuntimeException: internal error
    at com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl.applyFacets1(XSSimpleTypeDecl.java:754) ~[na:na]
    at com.sun.org.apache.xerces.internal.impl.dv.xs.BaseSchemaDVFactory.createBuiltInTypes(BaseSchemaDVFactory.java:207) ~[na:na]
    at com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl.createBuiltInTypes(SchemaDVFactoryImpl.java:47) ~[org.hazelcast.cache.ImperativeToReactiveApplicationKt:na]
    at com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl.<clinit>(SchemaDVFactoryImpl.java:42) ~[org.hazelcast.cache.ImperativeToReactiveApplicationKt:na]
    at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375) ~[na:na]
    at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295) ~[na:na]
    ... 82 common frames omitted
```

### Switching to YAML

XML is a huge beast, and I'm not expert enough to understand the exact reason behind the above exception. Engineering is also about finding the right workaround. In this case, I decided to switch from XML configuration to YAML configuration. It's simple anyway:

```yaml
hazelcast:
  instance-name: hazelcastInstance
```

We shouldn't forget to add the above resource into the resource configuration file:

```
{
"resources":{
  "includes":[
    {"pattern":"hazelcast.yaml"}
  ]}
}
```

Because of missing charsets at runtime, we also need to initialize the YAML reader at build time:

    Args = --initialize-at-build-time=com.hazelcast.org.snakeyaml.engine.v2.api.YamlUnicodeReader

We need to continue adding a couple of reflectively-accesses classes, all related to Hazelcast.

### Missing Proxies

At this point, we hit a brand new exception at runtime!

```
Caused by: com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.hazelcast.cache.PersonRepository, interface org.springframework.data.repository.Repository, interface org.springframework.transaction.interceptor.TransactionalProxy, interface org.springframework.aop.framework.Advised, interface org.springframework.core.DecoratingProxy] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
    at com.oracle.svm.core.util.VMError.unsupportedFeature(VMError.java:87) ~[na:na]
    at com.oracle.svm.reflect.proxy.DynamicProxySupport.getProxyClass(DynamicProxySupport.java:113) ~[na:na]
    at java.lang.reflect.Proxy.getProxyConstructor(Proxy.java:66) ~[na:na]
    at java.lang.reflect.Proxy.newProxyInstance(Proxy.java:1006) ~[na:na]
    at org.springframework.aop.framework.JdkDynamicAopProxy.getProxy(JdkDynamicAopProxy.java:126) ~[na:na]
    at org.springframework.aop.framework.ProxyFactory.getProxy(ProxyFactory.java:110) ~[na:na]
    at org.springframework.data.repository.core.support.RepositoryFactorySupport.getRepository(RepositoryFactorySupport.java:309) ~[na:na]
    at org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport.lambda$afterPropertiesSet$5(RepositoryFactoryBeanSupport.java:323) ~[org.hazelcast.cache.ImperativeToReactiveApplicationKt:2.4.5]
    at org.springframework.data.util.Lazy.getNullable(Lazy.java:230) ~[na:na]
    at org.springframework.data.util.Lazy.get(Lazy.java:114) ~[na:na]
    at org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport.afterPropertiesSet(RepositoryFactoryBeanSupport.java:329) ~[org.hazelcast.cache.ImperativeToReactiveApplicationKt:2.4.5]
    at org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean.afterPropertiesSet(R2dbcRepositoryFactoryBean.java:167) ~[org.hazelcast.cache.ImperativeToReactiveApplicationKt:1.2.5]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1845) ~[na:na]
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1782) ~[na:na]
    ... 46 common frames omitted
```

This one is about proxies and is pretty straightforward. In this context, Spring Data proxies the `PersonRepository` interface through a couple of other components. Those are all listed in the stack trace. [GraalVM can handle proxies](https://www.graalvm.org/reference-manual/native-image/DynamicProxy/) but requires you to configure them.

```
[
  ["org.hazelcast.cache.PersonRepository",
   "org.springframework.data.repository.Repository",
   "org.springframework.transaction.interceptor.TransactionalProxy",
   "org.springframework.aop.framework.Advised",
   "org.springframework.core.DecoratingProxy"]
]
```

### And Now For Serialization

With the above configuration, the image should start successfully, which makes me feel all warm inside:

```
2021-03-18 20:22:28.305  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.3)

...blah blah blah...

2021-03-18 20:22:30.654  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2021-03-18 20:22:30.655  INFO 1 --- [           main] o.s.boot.SpringApplication               : Started application in 2.355 seconds (JVM running for 2.358)
```

If we access the endpoint at this point, the app throws a runtime exception:

```
java.lang.IllegalStateException: Required identifier property not found for class org.hazelcast.cache.Person!
    at org.springframework.data.mapping.PersistentEntity.getRequiredIdProperty(PersistentEntity.java:105) ~[na:na]
```

```

```

AOT left out serialized classes, and we need to manage them. As for proxies, GraalVM knows what to do, but it requires an explicit configuration. Let's configure the `Person` class as well as the classes of its properties:

```json
[
{"name":"org.hazelcast.cache.Person"},
{"name":"java.time.LocalDate"},
{"name":"java.lang.String"},
{"name":"java.time.Ser"}
]
```

### Success!

Now, we can (finally!) `curl` the running image:

```
curl http://localhost:8080/person/1
curl http://localhost:8080/person/1
```

The output returns the expected result:

```
2021-03-15 09:54:18.994  INFO 1 --- [onPool-worker-3] o.h.c.CachingService : Person with id 1 not found in cache
2021-03-15 09:54:19.108  INFO 1 --- [onPool-worker-3] o.h.c.CachingService : Person with id 1 put in cache
2021-03-15 09:54:46.694  INFO 1 --- [onPool-worker-3] o.h.c.CachingService : Person with id 1 found in cache
```

We need to configure the `Sort` class to work with the root '/' endpoint, which retrieves all entities at once.

### Conclusion

Despite all the "magic" of Spring Boot, Spring Native handles most of GraalVM's required configuration out-of-the-box. The steps described above are mainly specific to the application's code.

While the application is just a demo application, it's not trivial either. It's promising to see the native image working despite serialization, in-memory cache, and in-memory database.

Of course, not everything is perfect: the build displays some exceptions, some logs are duplicated at runtime, and it seems the Hazelcast node cannot join a cluster.

Yet, it's good enough, especially regarding the amount of time I spent. I'm eager to try the 1.0 version. In the meanwhile, I'll probably investigate a bit more closely the remaining warnings.

The complete source code for this post can be found on [GitHub](https://github.com/hazelcast-demos/imperative-to-reactive/tree/native).

**To go further:**

* [Spring Native documentation](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
* [Packaging OCI Images](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image)

*Originally published at [A Java Geek](https://blog.frankel.ch/kick-spring-native-tires) on March 22^nd^ 2021*
