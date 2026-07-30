---
title: "Migrate to Spring Boot 3.2 with OpenRewrite"
slug: "openrewrite-migrate-to-spring-boot-3-2"
date: "2024-02-06T08:17:07+00:00"
lastmod: "2024-02-06T11:44:28+00:00"
description: "Simplify your migration process to Spring Boot 3.2 with the help of OpenRewrite."
authors:
  - "mahendra1413"
image: "https://foojay.io/wp-content/uploads/2023/09/OpenRewrite.gif"
categories:
  - "Jakarta EE"
  - "Java"
  - "JDK21"
  - "Maven"
  - "OpenRewrite"
  - "Security"
  - "Spring"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**As a developer, we frequently face the challenges of migrating to newer versions of frameworks and refactoring code. However, we can effortlessly achieve these tasks with the assistance of [OpenRewrite](https://docs.openrewrite.org/). OpenRewrite provides a stack of recipes specifically designed for migration purposes. By utilizing the appropriate recipes and integrating with the rewrite plugin, we can effectively migrate our code.**

Two months ago, the Spring Boot 3.2 version released, bringing numerous changes to the framework. If you are involved in multiple projects and require migrating to the latest version, developers may find manually updating all the code repositories with the necessary upgrade changes a challenging task.

I successfully utilized **`UpgradeSpringBoot_3_2`** Recipes to transfer all of my personal projects to the latest version of Spring Boot, 3.2.x. This migration significantly reduced the time required for the process.

Notable releases in this version include:

* Enabling [Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-DC4306FC-D6C1-4BCC-AECE-48C32C1A8DAA "Virtual")
* Support for [RestClient](https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient/ "RestClient"), [JdbcClient](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/simple/JdbcClient.html "JdbcClient"), [Apache Pulsar](https://pulsar.apache.org/ "Apache") (alternative to Kafka), and Jetty12
* Observability Enhancements
* Initial Support for JVM Coordinated Restore at Checkpoint (CRaC)

We can effortlessly transfer dependencies, modify build files, remove or alter them, update changes to deprecated or preferred APIs, and migrate configuration settings from the previous version of the application using OpenRewrite recipes. The Spring Boot 3.2 recipe also applies to this.

This article will focus on the recipes that we will utilize for migrating to Spring Boot 3.2.x with the following recipes:

* **`org.openrewrite.java.migrate.UpgradeToJava21`**
* **`org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2`**

The UpgradeSpringBoot_3_2 recipe combines multiple recipes that fulfill our requirements. Furthermore, we will also activate and execute the following recipes.

1. Migrate to Spring Boot 3.1
2. Update the versions of Maven or Gradle dependencies
3. Update the version of the Maven parent project
4. Update a Gradle plugin based on its id
5. Migrating to Spring Security 6.2, Spring Boot Properties to 3.2, and Spring Cloud 2023
6. Enabling [Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-DC4306FC-D6C1-4BCC-AECE-48C32C1A8DAA "Virtual") on Java21
7. Switching to a more up-to-date qualifiedTypeName, replacing the older version

Usage of Recipes {#h2-0-usage-of-recipes}
-----------------------------------------

I have used it for Maven projects, and I have employed this approach to accomplish it.

```xml

```

### **1. First add the following plugin to the pom.xml** {#h3-1-1-first-add-the-following-plugin-to-the-pom-xml}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;plugin&gt;
 &lt;groupId&gt;org.openrewrite.maven&lt;/groupId&gt;
 &lt;artifactId&gt;rewrite-maven-plugin&lt;/artifactId&gt;
 &lt;version&gt;5.21.0&lt;/version&gt;
 &lt;configuration&gt;
  &lt;activeRecipes&gt; ... &lt;/activeRecipes&gt;
 &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

### **2. Under the activeRecipes tag, please add the following UpgradeSpringBoot_3_2 recipe** {#h3-2-2-under-the-activerecipes-tag-please-add-the-following-upgradespringboot-3-2-recipe}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;recipe&gt;org.openrewrite.java.migrate.UpgradeToJava21&lt;/recipe&gt;
&lt;recipe&gt;org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2&lt;/recipe&gt;</pre>

### 3. And the above recipes can be activated by adding the following dependency {#h3-3-3-and-the-above-recipes-can-be-activated-by-adding-the-following-dependency}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.openrewrite.recipe&lt;/groupId&gt;
  &lt;artifactId&gt;rewrite-migrate-java&lt;/artifactId&gt;
  &lt;version&gt;2.7.1&lt;/version&gt;
 &lt;/dependency&gt;
 &lt;dependency&gt;
  &lt;groupId&gt;org.openrewrite.recipe&lt;/groupId&gt;
  &lt;artifactId&gt;rewrite-spring&lt;/artifactId&gt;
  &lt;version&gt;5.3.0&lt;/version&gt;
 &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

### **4. Finally configuration would be** {#h3-4-4-finally-configuration-would-be}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;project&gt;
  &lt;build&gt;
    &lt;plugins&gt;
      &lt;plugin&gt;
        &lt;groupId&gt;org.openrewrite.maven&lt;/groupId&gt;
        &lt;artifactId&gt;rewrite-maven-plugin&lt;/artifactId&gt;
        &lt;version&gt;5.21.0&lt;/version&gt;
        &lt;configuration&gt;
          &lt;activeRecipes&gt;
            &lt;recipe&gt;org.openrewrite.java.migrate.UpgradeToJava21&lt;/recipe&gt;
            &lt;recipe&gt;org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2&lt;/recipe&gt;
          &lt;/activeRecipes&gt;
        &lt;/configuration&gt;
        &lt;dependencies&gt;
         &lt;dependency&gt;
	     &lt;groupId&gt;org.openrewrite.recipe&lt;/groupId&gt;
	     &lt;artifactId&gt;rewrite-migrate-java&lt;/artifactId&gt;
	     &lt;version&gt;2.7.1&lt;/version&gt;
	  &lt;/dependency&gt;
          &lt;dependency&gt;
            &lt;groupId&gt;org.openrewrite.recipe&lt;/groupId&gt;
            &lt;artifactId&gt;rewrite-spring&lt;/artifactId&gt;
            &lt;version&gt;5.3.0&lt;/version&gt;
          &lt;/dependency&gt;
        &lt;/dependencies&gt;
      &lt;/plugin&gt;
    &lt;/plugins&gt;
  &lt;/build&gt;
&lt;/project&gt;</pre>

### **5. Perform the dryRun** {#h3-5-5-perform-the-dryrun}

Performing a `rewrite:dryRun` and verifying the generated file rewrite.patch under the target/rewrite folder is considered the recommended approach.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn rewrite:dryRun</pre>

<https://github.com/bsmahi/migratespring/blob/master/target/rewrite/rewrite.patch>

If you observe `rewrite.patch` file you can observe the difference in the each of the class and properties file and go through each one of them skeptically. Mainly

* Changed Java version to 21 and Spring Boot Parent to 3.2.2 (latest)
* Enabled VirtualThreads by adding the property: `spring.threads.virtual.enabled=true`
* In SecurityConfig changed from fluent api to LambdaDSL (functional programming)
* RequestMapping annotation has been simplified
* Changed `javax` namespace to `jakarta`

### 6. Run the recipe using the below command {#h3-6-6-run-the-recipe-using-the-below-command}

After gaining confidence in using the dryRun feature, you should proceed to execute the `rewrite:run` command

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn rewrite:run</pre>

Conclusion {#h2-7-conclusion}
-----------------------------

In summary, OpenRewrite recipes will reduce the time required for framework migration. Ultimately, numerous enterprise organizations need to consider cost optimization as a crucial factor.

As usual, the complete code available [over on Github](https://github.com/bsmahi/migratespring/tree/master)

### Reference {#h3-8-reference}

<https://docs.openrewrite.org/>

<br />

<br />
