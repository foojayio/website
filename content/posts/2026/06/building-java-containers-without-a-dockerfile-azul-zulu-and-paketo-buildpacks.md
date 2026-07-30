---
title: "Building Java Containers Without a Dockerfile: Azul Zulu and Paketo Buildpacks"
slug: "building-java-containers-without-a-dockerfile-azul-zulu-and-paketo-buildpacks"
date: "2026-06-18T08:15:45+00:00"
description: "Skip the Dockerfile. Learn how to build Java containers with Azul Zulu via Paketo Buildpacks — covering Spring Boot, jlink, JFR, JMX, and the pack CLI."
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/06/azul-docker-formats.avif"
categories:
  - "Cloud"
  - "Java"
tags:
related_posts:
enlighterjs: true
frozen: false
---

*This is the 5th post in the Azul Zulu Docker Official Images series:*

1. *[Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)*
2. *[The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)*
3. *[Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/)*
4. *[All Azul Zulu Container Images Explained: CA, SA, and Chainguard](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/)*

Every post in this series so far has shown you a Dockerfile. You pick a base image, copy a JAR, set an entrypoint, and ship. That works well. But Spring Boot developers often skip the Dockerfile entirely and still get a production-ready container with Azul Zulu as the JVM thanks to Paketo Buildpacks. Here is how that works, and how to configure it.

What Are Paketo Build Packs? {#h-what-are-paketo-build-packs}
-------------------------------------------------------------

[Paketo Buildpacks](https://paketo.io/) implement the [Cloud Native Buildpacks (CNB) specification](https://buildpacks.io/). A buildpack doesn't require a Dockerfile but instead inspects your application source. It decides what it needs to run and assembles a layered [Open Container Initiative (OCI)](https://opencontainers.org/) image. One layer provides the JVM (Azul Zulu), another compiles and packages the Java application, and others add launch helpers. Each concern stays separate and independently updatable.

The [Paketo Java buildpack](https://github.com/paketo-buildpacks/java) handles everything a Java app needs: dependency download, compilation, layering, JVM injection, and launch configuration. The JVM itself comes from a separate, swappable buildpack. The [Paketo Buildpack for Azul Zulu](https://github.com/paketo-buildpacks/azul-zulu) (buildpack ID: `paketo-buildpacks/azul-zulu`) supplies that JVM layer using Azul Zulu Builds of OpenJDK. This Buildpack is maintained by Paketo, with [significant contributions](https://github.com/paketo-buildpacks/azul-zulu/commits/main/) by [Daniel Mikusa](https://github.com/dmikusa). The new Zulu versions from the most recent security update in April '26 were integrated into Paketo within 3 days of their release.

Spring Boot Already Uses Paketo {#h-sprint-boot-already-uses-paketo}
--------------------------------------------------------------------

If you use the Spring Boot Maven Plugin and run `spring-boot:build-image`, Paketo does the work. The plugin calls the `paketobuildpacks/builder:base` builder by default. To use Azul Zulu, you add `paketobuildpacks/azul-zulu` in front of `paketobuildpacks/java` in the buildpacks list. This order matters because Paketo uses the first matching JVM buildpack.

Setting Up the Demo Project {#h-setting-up-the-demo-project}
------------------------------------------------------------

The code examples in this post are available in [FDelporte/azul-paketo-demo](https://github.com/FDelporte/azul-paketo-demo). That directory contains a minimal Spring Boot application you can run yourself.

The application has a single REST endpoint:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package be.webtechie;

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
}</pre>

The `pom.xml` adds Spring Boot's web starter and the Maven plugin:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
   &lt;dependency&gt;
      &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
      &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
   &lt;/dependency&gt;
&lt;/dependencies&gt;

&lt;build&gt;
   &lt;plugins&gt;
      &lt;plugin&gt;
         &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
         &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
         &lt;configuration&gt;    
            &lt;mainClass&gt;${your.mainClass}&lt;/mainClass&gt;
            &lt;image&gt;
               &lt;buildpacks&gt;          
                  &lt;buildpack&gt;paketobuildpacks/azul-zulu&lt;/buildpack&gt;
                  &lt;buildpack&gt;paketobuildpacks/java&lt;/buildpack&gt;
               &lt;/buildpacks&gt;
            &lt;/image&gt;
         &lt;/configuration&gt;
      &lt;/plugin&gt;
   &lt;/plugins&gt;
&lt;/build&gt;</pre>

Build the container image with a single command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn spring-boot:build-image</pre>

The output shows the Azul Zulu buildpack downloading and configuring the JVM:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[INFO]  &gt; Pulling buildpack image 'docker.io/paketobuildpacks/azul-zulu:latest' 100%
...
[INFO]     [creator]     Paketo Buildpack for Azul Zulu 11.6.1
[INFO]     [creator]       https://github.com/paketo-buildpacks/azul-zulu
...
[INFO]     [creator]       Azul Zulu JRE 25.0.3: Contributing to layer
...
[INFO] Successfully built image 'docker.io/library/paketo-demo:latest'</pre>

The created image has the following size:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker images
REPOSITORY       TAG       IMAGE ID       SIZE
paketo-demo      latest    0144b7c77181   272MB</pre>

Run it to check the [Java version](https://www.azul.com/glossary/java-versions/) through the REST endpoint:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker run --rm -p 8080:8080 paketo-demo:latest

$ curl http://localhost:8080/
Hello from Azul Zulu via Paketo!

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)</pre>

Or check the Spring log in the Docker output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Starting PaketoApplication v1.0-SNAPSHOT using Java 25.0.3 with PID 1</pre>

Configuring the Azul Zulu Buildpack {#h-configuring-the-azul-zulu-buildpack}
----------------------------------------------------------------------------

The Paketo buildpack accepts environment variables in two categories: build-time configuration (prefixed `BP_`) and launch-time configuration (prefixed `BPL_`). You set these inside the `<env>` block of the `spring-boot-maven-plugin` configuration.

### Choosing the Java Version and Type {#h-choosing-the-java-version-and-type}

You can specifiy the Java version and type of runtime. Setting `BP_JVM_TYPE` to `JDK` keeps the full JDK in the runtime image. That is useful when your application needs JDK-only tools like `jmap` or `jstack`, but it increases image size and adds unnecessary tooling to a production container. Use `JRE` unless you have a specific reason not to.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;plugin&gt;
   &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
   &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
   &lt;configuration&gt;  
      &lt;mainClass&gt;be.webtechie.PaketoApplication&lt;/mainClass&gt;
      &lt;image&gt;
         &lt;buildpacks&gt;
            &lt;buildpack&gt;paketobuildpacks/azul-zulu&lt;/buildpack&gt;
            &lt;buildpack&gt;paketobuildpacks/java&lt;/buildpack&gt;
         &lt;/buildpacks&gt;
         &lt;env&gt;
            &lt;!-- Java version: 8, 11, 17, 21, 25 --&gt;
            &lt;BP_JVM_VERSION&gt;25&lt;/BP_JVM_VERSION&gt;
            &lt;!-- Runtime type: JRE (default, smaller) or JDK --&gt;
            &lt;BP_JVM_TYPE&gt;JRE&lt;/BP_JVM_TYPE&gt;
         &lt;/env&gt;
      &lt;/image&gt;
   &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

### Using jlink to Generate a Custom JRE {#h-using-jlink-to-generate-a-custom-jre}

The Azul Zulu buildpack supports `jlink` at build time via the `BP_JVM_JLINK_ENABLED` variable. When enabled, the buildpack runs `jlink` to produce a minimal JRE containing only the Java modules your application uses:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;env&gt;
   &lt;BP_JVM_VERSION&gt;25&lt;/BP_JVM_VERSION&gt;
   &lt;BP_JVM_JLINK_ENABLED&gt;true&lt;/BP_JVM_JLINK_ENABLED&gt;
   &lt;!-- Optional: override the default jlink arguments --&gt;
   &lt;!-- Default: --no-man-pages --no-header-files --strip-debug --compress=1 --&gt;
   &lt;BP_JVM_JLINK_ARGS&gt;--no-man-pages --no-header-files --strip-debug --compress zip-6&lt;/BP_JVM_JLINK_ARGS&gt;
&lt;/env&gt;</pre>

As shown in the [previous post in this series](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/), jlink reduces a typical container image from \~370 MB (JRE) down to \~140 MB (custom runtime). The buildpack handles the `jdeps` and `jlink` steps for you automatically.

**Note for Spring Boot applications:** Spring Boot relies on reflection and classpath scanning. jlink may miss required modules at build time. Test the resulting container thoroughly before deploying with `BP_JVM_JLINK_ENABLED=true` in production.

The container created from the example project in the repository has a size of 136 MB:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn spring-boot:build-image -f pom-jlink.xml

$ docker images
REPOSITORY           TAG       IMAGE ID       SIZE
paketo-demo-jlink    latest    02a8be0879d9   136MB

$ docker run --rm -p 8080:8080 paketo-demo-jlink:latest

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA) </pre>

### Enabling Observability and Debugging Features {#h-enabling-observability-and-debugging-features}

The buildpack can bake observability configuration into the image using `BPE_DEFAULT_` prefixed variables. These set default values for `BPL_` runtime flags without requiring the container runner to pass them explicitly.

The example below enables Java Flight Recording, remote debugging, and JMX, a configuration useful for staging environments:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;env&gt;
   &lt;BP_JVM_VERSION&gt;25&lt;/BP_JVM_VERSION&gt;
   &lt;BP_JVM_TYPE&gt;JDK&lt;/BP_JVM_TYPE&gt;

   &lt;!-- Remote debugging on port 8000 --&gt;
   &lt;BPE_DEFAULT_BPL_DEBUG_ENABLED&gt;true&lt;/BPE_DEFAULT_BPL_DEBUG_ENABLED
   &lt;BPE_DEFAULT_BPL_DEBUG_PORT&gt;8000&lt;/BPE_DEFAULT_BPL_DEBUG_PORT&gt;

   &lt;!-- JMX on port 5000 --&gt;
   &lt;BPE_DEFAULT_BPL_JMX_ENABLED&gt;true&lt;/BPE_DEFAULT_BPL_JMX_ENABLED&gt;
   &lt;BPE_DEFAULT_BPL_JMX_PORT&gt;5000&lt;/BPE_DEFAULT_BPL_JMX_PORT&gt;

   &lt;!-- Java Flight Recorder --&gt;
   &lt;BPE_DEFAULT_BPL_JFR_ENABLED&gt;true&lt;/BPE_DEFAULT_BPL_JFR_ENABLED&gt;
   &lt;BPE_DEFAULT_BPL_JFR_ARGS&gt;dumponexit=true,filename=/tmp/rec.jfr,duration=600s&lt;/BPE_DEFAULT_BPL_JFR_ARGS&gt;

   &lt;!-- GC logging --&gt;
   &lt;BPE_DELIM_JAVA_TOOL_OPTIONS xml:space="preserve"&gt; &lt;/BPE_DELIM_JAVA_TOOL_OPTIONS&gt;
   &lt;BPE_APPEND_JAVA_TOOL_OPTIONS&gt;-Xlog:gc:/tmp/gc.log&lt;/BPE_APPEND_JAVA_TOOL_OPTIONS&gt;
&lt;/env&gt;</pre>

For a complete walkthrough of these debug options and how to validate them inside a running container, see the earlier post [Configuring Spring Boot to Build a Docker Image with Azul Zulu and Debug Options](https://www.azul.com/blog/configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options/).

As expected, including the full JDK increases the size of the container. In this case, we also need to add additional ports to expose the debug features:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn spring-boot:build-image -f pom-debug.xml

$ docker images
REPOSITORY           TAG       IMAGE ID       SIZE
paketo-demo-jlink    latest    4a847e1081fb   487MB

$ docker run --rm -p 8080:8080 -p 8000:8000 -p 5000:5000 paketo-demo-debug:latest
Debugging enabled on port *:8000
JMX enabled on port 5000

$ curl http://localhost:8080/version
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA) </pre>

Complete Configuration Reference {#h-complete-configuration-reference}
----------------------------------------------------------------------

For the full and up-to-date list of `BP_` and `BPL_` variables, see the ["Configuration" section in the GitHub README of paketo-buildpacks/azul-zulu](https://github.com/paketo-buildpacks/azul-zulu#configuration%5BConfiguration).

Using Paketo Without Spring Boot {#h-using-paketo-without-spring-boot}
----------------------------------------------------------------------

Paketo is not Spring Boot-specific. You can build any JVM application with the `pack` CLI directly.

Install `pack` from [buildpacks.io](https://buildpacks.io/docs/tools/pack/) and run the following command. In this example we are reusing the already compile `jar`-file from the Spring Boot demo:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ pack build paketo-demo-container \
    --path target/azul-paketo-demo-1.0-SNAPSHOT.jar \
    --buildpack docker://paketobuildpacks/azul-zulu \
    --buildpack docker://paketobuildpacks/java \
    --builder paketobuildpacks/builder-jammy-java-tiny

$ docker images
REPOSITORY             TAG       IMAGE ID       SIZE
paketo-demo-container  latest    22886b80ea2b   260MB

$ docker run --rm -p 8080:8080 paketo-demo-container:latest

$ curl http://localhost:8080/version
Java 26.0.1 (Vendor: Azul Systems, Inc., version: Zulu26.30+11-CA) </pre>

This produces an OCI image using the latest Azul Zulu as the JVM (currently version 26), with the same memory calculator, NMT, and launch helpers that the Spring Boot plugin uses.

If we want to stick to JVM 25 and use `jlink`, we can pass build-time environment variables with `--env`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ pack build paketo-demo-container-jlink \
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
Java 25.0.3 (Vendor: Azul Systems, Inc., version: Zulu25.34+17-CA)</pre>

Container Size {#h-container-size}
----------------------------------

Based on the examples in the repository, the following container sizes were created:

| Build tool  |           Type           |  Size  |
|-------------|--------------------------|--------|
| Spring Boot | Minimal example with JRE | 272 MB |
| Spring Boot | JDK with debugging       | 487 MB |
| Spring Boot | JRE with jlink           | 136 MB |
| pack        | No `BP_` settings        | 260 MB |
| pack        | JRE with jlink           | 137 MB |

Apparently Spring Boot and `pack` deliver comparable container sizes.

Which Zulu Image Does Paketo Use? {#h-which-zulu-image-does-paketo-use}
-----------------------------------------------------------------------

As you can see in the [buildpack.toml file on GitHub](https://github.com/paketo-buildpacks/azul-zulu/blob/main/buildpack.toml) in `paketo-buildpacks/azul-zulu`, `.tar.gz` Community Availability (CA) versions of the Azul Zulu Builds of OpenJDK are used. They are directly pulled from Azul's CDN. Azul Zulu CA is free to download and use. As explained in [All Azul Zulu Container Images Explained](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/), CA is the right choice for development, open-source projects, and deployments that do not require a commercial support contract.

What This Means for Your Build Pipeline {#h-what-this-means-for-your-build-pipeline}
------------------------------------------------------------------------------------

Paketo and Dockerfile-based builds solve different problems. Dockerfile builds give you full control over every layer. Paketo builds give you automatic memory tuning, NMT, JFR hooks, and a correctly layered image with no Dockerfile to maintain.

For Spring Boot teams, a few minimal configuration changes give you Azul Zulu as your runtime JVM. For teams that want explicit control over the Java version or runtime type, use the `BP_JVM_VERSION` and `BP_JVM_TYPE` variables.

The example code is in [FDelporte/azul-paketo-demo](https://github.com/FDelporte/azul-paketo-demo). Try the minimal examples, compare image sizes, and see which approach fits your pipeline best.

*** ** * ** ***

*Previously in this series:*

* [Trusted Java Containers: Azul Zulu OpenJDK Joins Docker's Official Images](https://www.azul.com/blog/trusted-java-containers-azul-zulu-openjdk-joins-dockers-official-images/)
* [The Road to Docker Official Images for Java: The Azul Zulu Story](https://www.azul.com/blog/the-road-to-docker-official-images-for-java-the-azul-zulu-story/)
* [Using the Azul Zulu Docker Official Images: From Simple Pull to Lean Container](https://www.azul.com/blog/using-the-azul-zulu-docker-official-images-from-simple-pull-to-lean-container/)
* [All Azul Zulu Container Images Explained: CA, SA, and Chainguard](https://www.azul.com/blog/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/)

*Related:*

* [Configuring Spring Boot to Build a Docker Image with Azul Zulu and Debug Options](https://www.azul.com/blog/configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options/)
