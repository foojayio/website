---
title: "Demystifying Google Container Tool, Jib: Java Image Builder"
date: "2021-02-18T18:42:44+00:00"
lastmod: "2021-08-23T13:05:22+00:00"
description: "Learn about the internals of image layering created by container image builder Jib and explore what distroless images are and their benefits."
canonical: "https://ashishtechmill.com/demystifying-google-container-tool-jib-java-image-builder"
authors:
  - "yrashish"
image: "jib-ashish-700x400.png"
categories:
  - "DevOps"
related_posts:
frozen: false
---

> This article covers some internals of image layering created by container image builder Jib and explore what distroless images are and their benefits.

Are you wondering what Jib is, actually? You should probably read my previous [article](https://ashishtechmill.com/containerizing-spring-boot-application-with-jib "article") before going ahead.

In short, Jib is an excellent tool for Java developers who are interested in containerizing Java applications, but not so interested in creating and maintaining Dockerfile or installing Docker. A Java developer can add a plugin to Maven or Gradle, and that's it. You don't have to learn new technology just to containerize your Java application.

Now, let's talk about some internals.

### Jib Image Layering

Jib intelligently divides your images into the following layers for more granular incremental builds.

1. Dependencies
2. Resources
3. Classes
4. Snapshot dependencies
5. All other dependencies, each extra directory (jib.extraDirectories in Gradle, in Maven) builds to its own layer.

When you do any code changes, only your changes are rebuilt, not your entire application. This means Jib only pushes the layer, which is changed, and the rest of the layers remain the same. For example, in the following demo, only a class file was changed and Jib pushed only the compiled class layer. Later on, we could see that during the docker pull, only the changed layer was extracted.

### Demo Video

[](https://www.youtube.com/watch?v=vizfV2opkfU)

[

{{< img src="jib-ashish-700x400.png" class="size-medium" width="700" height="400" >}}

](https://www.youtube.com/watch?v=vizfV2opkfU)

<https://www.youtube.com/watch?v=vizfV2opkfU>

Furthermore to dig deeper, now, we will use the simple spring boot application that was used in the previous article. The source code is available [here](https://github.com/yrashish/spring-boot-jib "here"). We will run maven in debug mode to understand these layers. However, In the following logs, you will see only dependencies, resources, and classes as separate layers, since we don't have other dependencies.

```
mvn compile -X jib:build
DEBUG] Containerizing application with the following files:
[DEBUG] Dependencies:
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-starter/2.3.5.RELEASE/spring-boot-starter-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot/2.3.5.RELEASE/spring-boot-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-context/5.2.10.RELEASE/spring-context-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.3.5.RELEASE/spring-boot-autoconfigure-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-starter-logging/2.3.5.RELEASE/spring-boot-starter-logging-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar
[DEBUG] /Users/ashish/.m2/repository/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar
[DEBUG] /Users/ashish/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.13.3/log4j-to-slf4j-2.13.3.jar
[DEBUG] /Users/ashish/.m2/repository/org/apache/logging/log4j/log4j-api/2.13.3/log4j-api-2.13.3.jar
[DEBUG] /Users/ashish/.m2/repository/org/slf4j/jul-to-slf4j/1.7.30/jul-to-slf4j-1.7.30.jar
[DEBUG] /Users/ashish/.m2/repository/jakarta/annotation/jakarta.annotation-api/1.3.5/jakarta.annotation-api-1.3.5.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-core/5.2.10.RELEASE/spring-core-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-jcl/5.2.10.RELEASE/spring-jcl-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/yaml/snakeyaml/1.26/snakeyaml-1.26.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.3.5.RELEASE/spring-boot-starter-web-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-starter-json/2.3.5.RELEASE/spring-boot-starter-json-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.11.3/jackson-databind-2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.11.3/jackson-annotations-2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.11.3/jackson-core-2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.11.3/jackson-datatype-jdk8–2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.11.3/jackson-datatype-jsr310–2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/com/fasterxml/jackson/module/jackson-module-parameter-names/2.11.3/jackson-module-parameter-names-2.11.3.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/boot/spring-boot-starter-tomcat/2.3.5.RELEASE/spring-boot-starter-tomcat-2.3.5.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.39/tomcat-embed-core-9.0.39.jar
[DEBUG] /Users/ashish/.m2/repository/org/glassfish/jakarta.el/3.0.3/jakarta.el-3.0.3.jar
[DEBUG] /Users/ashish/.m2/repository/org/apache/tomcat/embed/tomcat-embed-websocket/9.0.39/tomcat-embed-websocket-9.0.39.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-web/5.2.10.RELEASE/spring-web-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-beans/5.2.10.RELEASE/spring-beans-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-webmvc/5.2.10.RELEASE/spring-webmvc-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-aop/5.2.10.RELEASE/spring-aop-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/springframework/spring-expression/5.2.10.RELEASE/spring-expression-5.2.10.RELEASE.jar
[DEBUG] /Users/ashish/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar
[DEBUG] Resources:
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/application.properties
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example/spring
[DEBUG] Classes:
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example/spring
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example/spring/Controller.class
[DEBUG] /Users/ashish/Downloads/spring-boot-jib/target/classes/com/jib/example/spring/SpringbootApplication.class
```

### Deep-Dive Image Layer

You can inspect images created by Jib using the [Dive](https://github.com/wagoodman/dive "Dive") tool. As you can see, on RHS, we have exploded directory structure for classes, resources, and dependencies in the following path and their size are very minimal. The rest of the contents are of your base image.

/apps/classes,/apps/libs,/apps/resources

![Screenshot 2021-01-08 at 7.30.23 PM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1611246809879/6BWAJwpEI.png)  

Dive Image Layer screenshot.

Dive also tells you if you are wasting any space and, if you are, then you can discover ways to shrink the size of your Docker/OCI image. However, in our case, no space is wasted! You can view the content of an image with a docker history command as well. Let's see what it looks like in the image below:

![Screenshot 2021-01-09 at 1.15.58 PM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1611168829601/xK66ceNKF.png)  

Docker History Command screenshot.

### What Are Distroless Images?

Google defines distroless images as the following:
> Distroless images are lightweight base images, as they contain only dependencies required to run your application. They do not contain package managers, shells, or any other programs you would expect to find in a standard Linux distribution.

Furthermore, the layers created by Jib are created on top of a distroless base image. Jib by default uses distroless Java 8 image, but you have the option to choose an image of your choice.

### Why Distroless?

1. Fewer vulnerabilities to patch.
2. Better security.
3. Built with minimum dependencies.

### Debugging Distroless Images

Since there are no package managers installed, and you cannot do ssh to your container running with distroless base image, it makes them hard for debugging. In an ideal world, you would add better logging, instead of allowing shell access for your containers. But, there are ways with which you can add shell support and [debug](https://github.com/GoogleContainerTools/distroless#debug-images "debug") your application.

### Conclusion

In this article, we have covered some internals about Jib .i.e. image layering, which makes it fast. We have also covered distroless images with their benefits and pitfalls, such as debugging.

### Support Me

If you like what you just read, then you can buy me a coffee by clicking the link in the image below:

[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/meashish)

### Further Reading

You can also read some of my previous articles [here](https://ashishtechmill.com)
