---
title: "OpenRewrite: Automatic Code Refactoring and More - Part 2"
date: "2023-09-14T06:56:36+00:00"
lastmod: "2023-09-14T06:56:37+00:00"
description: "Discover OpenRewrite Library, the ultimate solution for code refactoring and maintenance. Optimize your code with ease and efficiency."
authors:
  - "mahendra1413"
image: "OpenRewrite-poster.png"
categories:
  - "Java"
  - "OpenRewrite"
related_posts:
  - "openrewrite-automatic-code-refactoring-and-maintenance"
  - "we-all-grow-older-but-do-our-projects-really-have-to-openrewrite"
  - "how-to-upgrade-to-jakarta-ee-10-and-glassfish-7-its-much-easier-than-you-think"
  - "idempotent-spring-boot-starter"
frozen: false
---

In the [previous article](https://foojay.io/today/openrewrite-automatic-code-refactoring-and-maintenance/), we outlined the significance of the OpenRewrite Library and its integration at an elevated level.

This article explains how to improve, refactor, and move a Spring Boot application with OpenRewrite.  
![Open Rewrite](OpenRewrite.webp)

Source: OpenReWrite{#caption-attachment-102151}

Before proceeding, we shall understand the various goals in the **Maven or Gradle plugin** **configuration**.

The **OpenRewrite Maven** offers the following goals:

* `mvn rewrite:cyclonedx` - This will generate a [Cyclonedx](https://cyclonedx.org/) bill of materials describing the application/project dependencies, including [transitive dependencies](https://en.wikipedia.org/wiki/Transitive_dependency).
* `mvn rewrite:discover` - Generate a report detailing the recipes that are currently available within the classpath.
* `mvn rewrite:dryRun` - This precautionary command will generate a set of differences solely for the modifications made within the `/target/rewrite` directory, which will be saved in the `rewrite.patch`.
* `mvn rewrite:dryRunNoFork` - This precautionary command will generate a set of differences solely for the modifications made within the `/target/rewrite` directory, which will be saved in the `rewrite.patch`.
* `mvn rewrite:run`- This command runs the configured recipes and applies the changes locally.
* `mvn rewrite:runNoFork` -This command runs the configured recipes and applies the changes locally.
* For `runForFork:` This different way of doing things doesn't split up the Maven process and could work better when using Rewrite with other Maven tasks in a continuous integration (CI) system.

The **OpenRewrite Gradle** offers the following goals:

* `gradle rewriteDiscover` - This task will list down the recipes available in the classpath.
* `gradle rewriteDryRun` - This precautionary task will generate a set of modifications in the build log and doesn't modify any source files.
* `gradle rewriteRun` - This task will run the configured recipes and apply the changes in the source files.

In this part, we will use some recipes from the list to make it easier to move the code.

For instance, we shall employ the following recipes:

* `org.openrewrite.java.OrderImports`
* `org.openrewrite.staticanalysis.CommonStaticAnalysis`
* `org.openrewrite.staticanalysis.JavaApiBestPractices`
* `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0`

We can make a spring boot app and use the recipes for moving and fixing issues with static analysis.

The primary modifications entailed in transitioning from Spring Boot 2.X to Spring Boot 3.X encompass the migration from Java 8/11 to Java 17 and the shift from the `javax` to the `jakarta` namespace.

Consequently, our objective is to construct a Spring Boot 2.X application on Java 11, featuring an embedded Tomcat server, and to compose some poorly written code that alludes to classes from the `javax` namespace.

Let's start by creating Spring Boot 2.X application using the [Spring Initializr](https://start.spring.io/) select Java 11 and add `spring-boot-starter-web` and `spring-boot-starter-data-jpa` dependency. Here go with the `pom.xml` for our Spring Boot Project.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.15</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>
  <groupId>com.bsmlabs</groupId>
  <artifactId>migrate-spring-boot-demo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>migrate-spring-boot-demo</name>
  <description>Demo project for Spring Boot</description>
  <properties>
    <java.version>11</java.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

Now let's create a simple controller with a `/message` endpoint.

The controller method should a return a message while accessing, we have annotated with `@RequestMapping(value="/message", method = RequestMethod.`*GET*`)` annotation, it should change to the simplified version i.e., `@GetMapping("/message")` and we intentionally declared static string code badly so that recipes will identify and migrate it.

```java
package com.bsmlabs.migratespringbootdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    private final static String base_message = "Spring Boot 3 Migration with OpenRewrite";

    @RequestMapping(value="/message", method = RequestMethod.GET)
    public String getMessage() {
        return base_message;
    }
}
```

To rectify our coding errors and execute the Spring Boot upgrade, it is imperative to configure OpenRewrite to utilize the `CommonStaticAnalysis`, `JavaApiBestPractices` and `UpgradeSpringBoot_3_0` recipes in tandem.

These recipes comprise numerous smaller refactoring recipes. The complete inventory of recipes encompassed within `CommonStaticAnalysis` can be accessed [here](https://docs.openrewrite.org/recipes/staticanalysis/commonstaticanalysis), `JavaApiBestPractices` can be found [here](https://docs.openrewrite.org/recipes/staticanalysis/javaapibestpractices), while the list for the `UpgradeSpringBoot_3_0`\` recipe can be found [here](https://docs.openrewrite.org/recipes/java/spring/boot3/upgradespringboot_3_0).

## Add the OpenRewrite Maven Plugin

Below is the configuration of OpenRewrite Maven Plugin for the Spring Boot Project

```xml
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.5.2</version>
    <configuration>
       <activeRecipes>
        <recipe>org.openrewrite.java.OrderImports</recipe>
        <recipe>org.openrewrite.staticanalysis.CommonStaticAnalysis</recipe>
        <recipe>org.openrewrite.staticanalysis.JavaApiBestPractices</recipe>
        <recipe>org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0</recipe>
      </activeRecipes>
    </configuration>
    <dependencies>
       <dependency>
             <groupId>org.openrewrite.recipe</groupId>
         <artifactId>rewrite-spring</artifactId>
         <version>5.0.10</version>
       </dependency>
       <dependency>
         <groupId>org.openrewrite.recipe</groupId>
         <artifactId>rewrite-static-analysis</artifactId>
         <version>1.0.7</version>
        </dependency>
    </dependencies>
</plugin>
```

As mentioned in the previous blog post, as precautionary, we must always execute the `mvn rewrite:dryRun` command and OpenRewrite will generate patch file under `target/rewrite/rewrite.patch`. In this patch file, we can review the changes.

```
diff --git a/springworkspace/migrate-spring-boot-demo/pom.xml b/springworkspace/migrate-spring-boot-demo/pom.xml
index 9668ce6..fd3017f 100644
--- a/springworkspace/migrate-spring-boot-demo/pom.xml
+++ b/springworkspace/migrate-spring-boot-demo/pom.xml
@@ -5,7 +5,7 @@ org.openrewrite.config.CompositeRecipe
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
-       <version>2.7.15</version>
+       <version>3.0.10</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.bsmlabs</groupId>
@@ -14,7 +14,7 @@
    <name>migrate-spring-boot-demo</name>
    <description>Demo project for Spring Boot</description>
    <properties>
-       <java.version>11</java.version>
+       <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
@@ -25,6 +25,11 @@
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
+       <dependency>
+           <groupId>org.glassfish.jaxb</groupId>
+           <artifactId>jaxb-runtime</artifactId>
+           <scope>provided</scope>
+       </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>

diff --git a/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/SampleController.java b/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/SampleController.java
index b7c6d44..2d593da 100644
--- a/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/SampleController.java
+++ b/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/SampleController.java
@@ -1,15 +1,14 @@ org.openrewrite.config.CompositeRecipe
 package com.bsmlabs.migratespringbootdemo;

-import org.springframework.web.bind.annotation.RequestMapping;
-import org.springframework.web.bind.annotation.RequestMethod;
+import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RestController;

 @RestController
 public class SampleController {

-    private final static String base_message = "Spring Boot 3 Migration with OpenRewrite";
+    private static final String base_message = "Spring Boot 3 Migration with OpenRewrite";

-    @RequestMapping(value="/message", method = RequestMethod.GET)
+    @GetMapping("/message")
     public String getMessage() {
         return base_message;
     }

diff --git a/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/Person.java b/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/Person.java
index 68f3220..482e848 100644
--- a/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/Person.java
+++ b/springworkspace/migrate-spring-boot-demo/src/main/java/com/bsmlabs/migratespringbootdemo/Person.java
@@ -1,9 +1,9 @@ org.openrewrite.config.CompositeRecipe
 package com.bsmlabs.migratespringbootdemo;

-import javax.persistence.Column;
-import javax.persistence.Entity;
-import javax.persistence.GeneratedValue;
-import javax.persistence.Id;
+import jakarta.persistence.Column;
+import jakarta.persistence.Entity;
+import jakarta.persistence.GeneratedValue;
+import jakarta.persistence.Id;
 import java.util.Objects;

 @Entity
@@ -42,8 +42,12 @@

     @Override
     public boolean equals(Object o) {
-        if (this == o) return true;
-        if (o == null || getClass() != o.getClass()) return false;
+        if (this == o) {
+            return true;
+        }
+        if (o == null || getClass() != o.getClass()) {
+            return false;
+        }
         Person person = (Person) o;
         return Objects.equals(id, person.id) && Objects.equals(name, person.name);
     }
```

For the Maven **pom.xml**, you can observe the following changes and In the diff file itself, OpenRewrite also creates comments for each recipe that was used to modify the file

* OpenRewrite changed JDK version to 17
* Changed Spring Boot Version to `3.0.`10
* Added `jaxb-runtime` for with the scope as `test`

The following changes by OpenRewrite for the `SampleController` and `Person` class files:

* Declared static final consant correctly
* Changing to @GetMapping from @RequestMapping for GET method.
* Changed Import statements for the `javax` namespace to `jakarta` namespace
* In `Person` file, refactored `equals` method

OpenRewrite fixed many coding problems in our code, making it easier to switch to Spring Boot 3.x. They also made the GET method shorter by using @GetMapping.

You can find the sample spring boot project [here](https://github.com/bsmahi/migrate-spring-boot-demo)

## References

* OpenRewrite Documentation -<https://docs.openrewrite.org/>
