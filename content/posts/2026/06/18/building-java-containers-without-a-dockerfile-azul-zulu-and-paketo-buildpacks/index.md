---
title: "Building Java Containers Without a Dockerfile: Azul Zulu and Paketo Buildpacks"
slug: "building-java-containers-without-a-dockerfile-azul-zulu-and-paketo-buildpacks"
date: "2026-06-18T08:15:45+00:00"
description: "Skip the Dockerfile. Learn how to build Java containers with Azul Zulu via Paketo Buildpacks — covering Spring Boot, jlink, JFR, JMX, and the pack CLI."
authors:
  - "frankdelporte"
image: "azul-docker-formats.avif"
categories:
  - "Cloud"
  - "Java"
tags:
related_posts:
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container"
  - "the-road-to-docker-official-images-for-java-the-azul-zulu-story"
  - "configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options"
enlighterjs: true
frozen: false
---

*This is the 5th post in the Azul Zulu Docker Official Images series:*

1. *[Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)*
2. *[The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)*
3. *[Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/)*
4. *[All Azul Zulu Container Images Explained: CA, SA, and Chainguard](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/)*

Every post in this series so far has shown you a Dockerfile. You pick a base image, copy a JAR, set an entrypoint, and ship. That works well. But Spring Boot developers often skip the Dockerfile entirely and still get a production-ready container with Azul Zulu as the JVM thanks to Paketo Buildpacks. Here is how that works, and how to configure it.

## What Are Paketo Build Packs?

[Paketo Buildpacks](https://paketo.io/) implement the [Cloud Native Buildpacks (CNB) specification](https://buildpacks.io/). A buildpack doesn't require a Dockerfile but instead inspects your application source. It decides what it needs to run and assembles a layered [Open Container Initiative (OCI)](https://opencontainers.org/) image. One layer provides the JVM (Azul Zulu), another compiles and packages the Java application, and others add launch helpers. Each concern stays separate and independently updatable.

The [Paketo Java buildpack](https://github.com/paketo-buildpacks/java) handles everything a Java app needs: dependency download, compilation, layering, JVM injection, and launch configuration. The JVM itself comes from a separate, swappable buildpack. The [Paketo Buildpack for Azul Zulu](https://github.com/paketo-buildpacks/azul-zulu) (buildpack ID: `paketo-buildpacks/azul-zulu`) supplies that JVM layer using Azul Zulu Builds of OpenJDK. This Buildpack is maintained by Paketo, with [significant contributions](https://github.com/paketo-buildpacks/azul-zulu/commits/main/) by [Daniel Mikusa](https://github.com/dmikusa). The new Zulu versions from the most recent security update in April '26 were integrated into Paketo within 3 days of their release.

## Spring Boot Already Uses Paketo

If you use the Spring Boot Maven Plugin and run `spring-boot:build-image`, Paketo does the work. The plugin calls the `paketobuildpacks/builder:base` builder by default. To use Azul Zulu, you add `paketobuildpacks/azul-zulu` in front of `paketobuildpacks/java` in the buildpacks list. This order matters because Paketo uses the first matching JVM buildpack.

## Setting Up the Demo Project

The code examples in this post are available in [FDelporte/azul-paketo-demo](https://github.com/FDelporte/azul-paketo-demo). That directory contains a minimal Spring Boot application you can run yourself.

The application has a single REST endpoint:

```
package be.webtechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PaketoApplication {

   static void main(String[] args) {
      SpringApplication.run(PaketoApplication.class, args);
   }

   @GetMapping("/")
   public String hello() {
      return "Hello from Azul Zulu via Paketo!";
   }

   @GetMapping("/version")  
      public String version() {  
         return "Java " + System.getProperty("java.version")  
            + " (Vendor: " 
            + System.getProperty("java.vendor")   
            + ", version: " 
            + System.getProperty("java.vendor.version") 
            + ")";
   }
}
```

The `pom.xml` adds Spring Boot's web starter and the Maven plugin:

```
<dependencies>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
</dependencies>

<build>
   <plugins>
      <plugin>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-maven-plugin</artifactId>
         <configuration>    
            <mainClass>${your.mainClass}</mainClass>
            <image>
               <buildpacks>          
                  <buildpack>paketobuildpacks/azul-zulu</buildpack>
                  <buildpack>paketobuildpacks/java</buildpack>
               </buildpacks>
            </image>
         </configuration>
      </plugin>
   </plugins>
</build>
```

Build the container image with a single command:

```
$ mvn spring-boot:build-image
```

The output shows the Azul Zulu buildpack downloading and configuring the JVM:

```
[INFO]  > Pulling buildpack image 'docker.io/paketobuildpacks/azul-zulu:latest' 100%
...
[INFO]     [creator]     Paketo Buildpack for Azul Zulu 11.6.1
[INFO]     [creator]       https://github.com/paketo-buildpacks/azul-zulu
...
[INFO]     [creator]       Azul Zulu JRE 25.0.3: Contributing to layer
...
[INFO] Successfully built image 'docker.io/library/paketo-demo:latest'
```

The created image has the following size:

```
$ docker images
REPOSITORY       TAG       IMAGE ID       SIZE
paketo-demo      latest    0144b7c77181   272MB
```

Run it to check the [Java version](https://www.azul.com/glossary/java-versions/) through the REST endpoint:

```
$ docker run --rm -p 8080:8080 paketo-demo:latest

$ curl http://localhost:8080/
Hello from Azul Zulu via Paketo!

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)
```

Or check the Spring log in the Docker output:

```
Starting PaketoApplication v1.0-SNAPSHOT using Java 25.0.3 with PID 1
```

## Configuring the Azul Zulu Buildpack

The Paketo buildpack accepts environment variables in two categories: build-time configuration (prefixed `BP_`) and launch-time configuration (prefixed `BPL_`). You set these inside the `<env>` block of the `spring-boot-maven-plugin` configuration.

### Choosing the Java Version and Type

You can specifiy the Java version and type of runtime. Setting `BP_JVM_TYPE` to `JDK` keeps the full JDK in the runtime image. That is useful when your application needs JDK-only tools like `jmap` or `jstack`, but it increases image size and adds unnecessary tooling to a production container. Use `JRE` unless you have a specific reason not to.

```
<plugin>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-maven-plugin</artifactId>
   <configuration>  
      <mainClass>be.webtechie.PaketoApplication</mainClass>
      <image>
         <buildpacks>
            <buildpack>paketobuildpacks/azul-zulu</buildpack>
            <buildpack>paketobuildpacks/java</buildpack>
         </buildpacks>
         <env>
            <!-- Java version: 8, 11, 17, 21, 25 -->
            <BP_JVM_VERSION>25</BP_JVM_VERSION>
            <!-- Runtime type: JRE (default, smaller) or JDK -->
            <BP_JVM_TYPE>JRE</BP_JVM_TYPE>
         </env>
      </image>
   </configuration>
</plugin>
```

### Using jlink to Generate a Custom JRE

The Azul Zulu buildpack supports `jlink` at build time via the `BP_JVM_JLINK_ENABLED` variable. When enabled, the buildpack runs `jlink` to produce a minimal JRE containing only the Java modules your application uses:

```
<env>
   <BP_JVM_VERSION>25</BP_JVM_VERSION>
   <BP_JVM_JLINK_ENABLED>true</BP_JVM_JLINK_ENABLED>
   <!-- Optional: override the default jlink arguments -->
   <!-- Default: --no-man-pages --no-header-files --strip-debug --compress=1 -->
   <BP_JVM_JLINK_ARGS>--no-man-pages --no-header-files --strip-debug --compress zip-6</BP_JVM_JLINK_ARGS>
</env>
```

As shown in the [previous post in this series](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/), jlink reduces a typical container image from \~370 MB (JRE) down to \~140 MB (custom runtime). The buildpack handles the `jdeps` and `jlink` steps for you automatically.

**Note for Spring Boot applications:** Spring Boot relies on reflection and classpath scanning. jlink may miss required modules at build time. Test the resulting container thoroughly before deploying with `BP_JVM_JLINK_ENABLED=true` in production.

The container created from the example project in the repository has a size of 136 MB:

```
$ mvn spring-boot:build-image -f pom-jlink.xml

$ docker images
REPOSITORY           TAG       IMAGE ID       SIZE
paketo-demo-jlink    latest    02a8be0879d9   136MB

$ docker run --rm -p 8080:8080 paketo-demo-jlink:latest

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)
```

### Enabling Observability and Debugging Features

The buildpack can bake observability configuration into the image using `BPE_DEFAULT_` prefixed variables. These set default values for `BPL_` runtime flags without requiring the container runner to pass them explicitly.

The example below enables Java Flight Recording, remote debugging, and JMX, a configuration useful for staging environments:

```
<env>
   <BP_JVM_VERSION>25</BP_JVM_VERSION>
   <BP_JVM_TYPE>JDK</BP_JVM_TYPE>

   <!-- Remote debugging on port 8000 -->
   <BPE_DEFAULT_BPL_DEBUG_ENABLED>true</BPE_DEFAULT_BPL_DEBUG_ENABLED
   <BPE_DEFAULT_BPL_DEBUG_PORT>8000</BPE_DEFAULT_BPL_DEBUG_PORT>

   <!-- JMX on port 5000 -->
   <BPE_DEFAULT_BPL_JMX_ENABLED>true</BPE_DEFAULT_BPL_JMX_ENABLED>
   <BPE_DEFAULT_BPL_JMX_PORT>5000</BPE_DEFAULT_BPL_JMX_PORT>

   <!-- Java Flight Recorder -->
   <BPE_DEFAULT_BPL_JFR_ENABLED>true</BPE_DEFAULT_BPL_JFR_ENABLED>
   <BPE_DEFAULT_BPL_JFR_ARGS>dumponexit=true,filename=/tmp/rec.jfr,duration=600s</BPE_DEFAULT_BPL_JFR_ARGS>

   <!-- GC logging -->
   <BPE_DELIM_JAVA_TOOL_OPTIONS xml:space="preserve"> </BPE_DELIM_JAVA_TOOL_OPTIONS>
   <BPE_APPEND_JAVA_TOOL_OPTIONS>-Xlog:gc:/tmp/gc.log</BPE_APPEND_JAVA_TOOL_OPTIONS>
</env>
```

For a complete walkthrough of these debug options and how to validate them inside a running container, see the earlier post [Configuring Spring Boot to Build a Docker Image with Azul Zulu and Debug Options](https://www.azul.com/blog/configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options/).

As expected, including the full JDK increases the size of the container. In this case, we also need to add additional ports to expose the debug features:

```
$ mvn spring-boot:build-image -f pom-debug.xml

$ docker images
REPOSITORY           TAG       IMAGE ID       SIZE
paketo-demo-jlink    latest    4a847e1081fb   487MB

$ docker run --rm -p 8080:8080 -p 8000:8000 -p 5000:5000 paketo-demo-debug:latest
Debugging enabled on port *:8000
JMX enabled on port 5000

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)
```

## Complete Configuration Reference

For the full and up-to-date list of `BP_` and `BPL_` variables, see the ["Configuration" section in the GitHub README of paketo-buildpacks/azul-zulu](https://github.com/paketo-buildpacks/azul-zulu#configuration%5BConfiguration).

## Using Paketo Without Spring Boot

Paketo is not Spring Boot-specific. You can build any JVM application with the `pack` CLI directly.

Install `pack` from [buildpacks.io](https://buildpacks.io/docs/tools/pack/) and run the following command. In this example we are reusing the already compile `jar`-file from the Spring Boot demo:

```
$ pack build paketo-demo-container \
    --path target/azul-paketo-demo-1.0-SNAPSHOT.jar \
    --buildpack docker://paketobuildpacks/azul-zulu \
    --buildpack docker://paketobuildpacks/java \
    --builder paketobuildpacks/builder-jammy-java-tiny

$ docker images
REPOSITORY             TAG       IMAGE ID       SIZE
paketo-demo-container  latest    22886b80ea2b   260MB

$ docker run --rm -p 8080:8080 paketo-demo-container:latest

$ curl http://localhost:8080/version
Java 26.0.1 (Vendor: Azul Systems, Inc., version: Zulu26.30+11-CA)
```

This produces an OCI image using the latest Azul Zulu as the JVM (currently version 26), with the same memory calculator, NMT, and launch helpers that the Spring Boot plugin uses.

If we want to stick to JVM 25 and use `jlink`, we can pass build-time environment variables with `--env`:

```
$ pack build paketo-demo-container-jlink \
    --path target/azul-paketo-demo-1.0-SNAPSHOT.jar \
    --buildpack docker://paketobuildpacks/azul-zulu \
    --buildpack docker://paketobuildpacks/java \
    --builder paketobuildpacks/builder-jammy-java-tiny \
    --env BP_JVM_VERSION=25 \
    --env BP_JVM_JLINK_ENABLED=true

$ docker images
REPOSITORY                   TAG       IMAGE ID       SIZE
paketo-demo-container-jlink  latest    f1bcab88c325   137MB

$ docker run --rm -p 8080:8080 paketo-demo-container-jlink:latest

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)
```

## Container Size

Based on the examples in the repository, the following container sizes were created:

| Build tool  |           Type           |  Size  |
|-------------|--------------------------|--------|
| Spring Boot | Minimal example with JRE | 272 MB |
| Spring Boot | JDK with debugging       | 487 MB |
| Spring Boot | JRE with jlink           | 136 MB |
| pack        | No `BP_` settings        | 260 MB |
| pack        | JRE with jlink           | 137 MB |

Apparently Spring Boot and `pack` deliver comparable container sizes.

## Which Zulu Image Does Paketo Use?

As you can see in the [buildpack.toml file on GitHub](https://github.com/paketo-buildpacks/azul-zulu/blob/main/buildpack.toml) in `paketo-buildpacks/azul-zulu`, `.tar.gz` Community Availability (CA) versions of the Azul Zulu Builds of OpenJDK are used. They are directly pulled from Azul's CDN. Azul Zulu CA is free to download and use. As explained in [All Azul Zulu Container Images Explained](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/), CA is the right choice for development, open-source projects, and deployments that do not require a commercial support contract.

## What This Means for Your Build Pipeline

Paketo and Dockerfile-based builds solve different problems. Dockerfile builds give you full control over every layer. Paketo builds give you automatic memory tuning, NMT, JFR hooks, and a correctly layered image with no Dockerfile to maintain.

For Spring Boot teams, a few minimal configuration changes give you Azul Zulu as your runtime JVM. For teams that want explicit control over the Java version or runtime type, use the `BP_JVM_VERSION` and `BP_JVM_TYPE` variables.

The example code is in [FDelporte/azul-paketo-demo](https://github.com/FDelporte/azul-paketo-demo). Try the minimal examples, compare image sizes, and see which approach fits your pipeline best.

*Previously in this series:*

* [Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)
* [The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)
* [Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/)
* [All Azul Zulu Container Images Explained: CA, SA, and Chainguard](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/)

*Related:*

* [Configuring Spring Boot to Build a Docker Image with Azul Zulu and Debug Options](https://www.azul.com/blog/configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options/)
