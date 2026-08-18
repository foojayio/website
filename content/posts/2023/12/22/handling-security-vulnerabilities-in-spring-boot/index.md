---
title: "Handling security vulnerabilities in Spring Boot"
date: "2023-12-22T14:59:10+00:00"
lastmod: "2023-12-22T14:59:11+00:00"
description: "Keeping your dependencies in check is crucial to ensure that your Spring Boot projects run smoothly and remain resilient in the face of ever-evolving threats."
canonical: "https://snyk.io/blog/security-vulnerabilities-spring-boot/"
authors:
  - "bmvermeer"
image: "snyk-logo.png"
categories:
  - "Security"
  - "Snyk"
  - "Spring"
related_posts:
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "a-simple-service-with-spring-boot"
  - "clean-shutdown-of-spring-boot-applications"
frozen: false
---

**In the world of software development, managing dependencies is a core part of creating strong and secure applications. Spring Boot, a favorite among Java developers, makes building applications easier, but there's more to it than meets the eye. Keeping your dependencies in check is crucial to ensure that your Spring Boot projects run smoothly and remain resilient in the face of ever-evolving threats.**

One critical aspect of Spring Boot dependency management is security. Software vulnerabilities are discovered frequently, and by keeping your project's dependencies up to date, you're essentially putting on your digital security armor. Outdated dependencies can be like unlocked doors, inviting trouble from potential threats, and that's something we'd like to avoid.

## Finding vulnerabilities in Spring Boot

Software composition analysis (SCA) tools are a very useful tool for developers. They cover an easy solution to managing dependencies, handling security vulnerabilities, navigating licensing issues, and ensuring compliance in your software projects. By using [++Snyk Open Source++](https://snyk.io/product/open-source-security-management/) as your SCA, you can easily find out if your Spring Boot packages contain vulnerabilities.

In the case of my example project, I found multiple vulnerabilities. The first is a high-severity security issue in \`netty-codec-http2\`. This is a transitive dependency brought in by my \`spring-boot-start-webflux\`, as you can see below.
![blog-sprint-boot-dos-vuln](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1701273846%2Fblog-sprint-boot-dos-vuln.jpg&w=2560&q=75)

The second vulnerability I would like to discuss is a medium severity arbitrary code execution issue brought in via the \`snakeyaml\` package. This is also a transitive dependency, this time brought in by \`spring-boot-starter-security\`.
![blog-spring-boot-ACE-vuln](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1701273846%2Fblog-spring-boot-ACE-vuln.jpg&w=2560&q=75)

I won't go into the actual problem of individual vulnerabilities today, but you can check out the dedicated [++blog post++](https://snyk.io/blog/unsafe-deserialization-snakeyaml-java-cve-2022-1471/) for more information on the \`snakeyaml\` problem. Let's focus on solving the problem and implementing the best solution.

## Remediating vulnerable packages in your Spring Boot application

For the first vulnerability, there is a clear fix described. My application is based on Spring Boot 2.7.16, and therefore, the \`spring-boot-starter-webflux\` is also on version 2.7.16.

Updating the Webflux starter to 2.7.17 should fix the problem. There are multiple way

s of doing this, but not all are recommended.

Typically, your spring boot manifest looks something like the following code:  

**Maven**:

```xml
<parent>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-parent</artifactId>
   <version>2.7.16</version>
   <relativePath/>
</parent>
…
<dependencies>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-webflux</artifactId>
   </dependency>
  …
</dependencies>
```

**Gradle**:

```groovy
plugins {
 …
 id 'org.springframework.boot' version '2.7.16'
 id 'io.spring.dependency-management' version '1.1.3'
}
…
dependencies {
 implementation 'org.springframework.boot:spring-boot-starter-security'
 implementation 'org.springframework.boot:spring-boot-starter-webflux'
 …
}
```

### Update Spring Boot starter

The version number of the individual \`spring-boot-starters\` are derived from the \`spring-boot-starter-parent\` (Maven) or the \`org.springframework.boot\` plugin in Gradle.

The main reason for this is that all these starter packages belong to the same release of Spring and are tested as such.

When looking at the first vulnerability found in my project, the fix suggestion is to update \`spring-boot-starter-webflux\` to version 2.7.17 to solve the high-severity security vulnerability. This suggests that I only have to fix the Webflux package, which I can do by specifying a specific version like the one below.

**Maven:**

```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-webflux</artifactId>
       <version>2.7.17</version>
   </dependency>
```

**Gradle:**

```
implementation 'org.springframework.boot:spring-boot-starter-webflux:2.7.17'
```

**But stop right here!** This is not the recommended way of solving the problem. The specific version, 2.7.17, is used over the parent version. While this might work since this is a patch version release, it isn't the best or most secure solution. Since semver does not guarantee that API's stay intact, we can't be sure this will work. But the most important argument is that there probably is a full new release of Spring Boot.  

Remember that I told you earlier that these starters belong together? Therefore the best thing you can do is update your complete Spring Boot distribution to 2.7.17. This is even more important when talking about minor or major releases of a package --- which can break API and not run smoothly together. So, in this example, do the following. Update the parent (Maven) or the plugin (Gradle) instead of the individual starters.

**Maven:**

```xml
<parent>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-parent</artifactId>
   <version>2.7.17</version>
   <relativePath/>
</parent>
```

**Gradle:**

```groovy
plugins {
 …
 id 'org.springframework.boot' version '2.7.17'
}
```

I would advise everyone to go the extra mile and update their entire spring boot version to the latest appropriate version. To find out what that is, simply go to https://start.spring.io.
![blog-sprint-boot-initializer](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1701273847%2Fblog-sprint-boot-initializer.jpg&w=2560&q=75)

### Updating transitive dependencies

For the second issue, Snyk discovered that there is no clear remediation advice in my appliation. Currently, there is also no available version of a \`spring-boot-starter\` that does not have this insecure transitive dependency. What we do know is that there is an updated version of the transitive dependency available. Updating to \`snakeyaml\` 2.0 will solve the problem.  

Once again, I want to emphasize that this is just an example. There might already be an updated version of a \`spring-boot-starter\` available by the time you read this, so be sure to double-check. To learn about the SnakeYaml vulnerability, please check our [++dedicated blog post++](https://snyk.io/blog/snakeyaml-unsafe-deserialization-vulnerability/)on this topic.

#### Version parameter update

The first thing you should do is check of the version of the package you want to update is a property in Spring Boot. The Spring Boot docs contain an appendix with the [++dependency versions++](https://docs.spring.io/spring-boot/docs/2.7.16/reference/htmlsingle/#appendix.dependency-versions) for the specific release. You can also find the [++version properties++](https://docs.spring.io/spring-boot/docs/2.7.16/reference/htmlsingle/#appendix.dependency-versions.properties) in there. These properties can be overridden in both Maven and Gradle, so it uses a newer version of the transitive dependency.

For maven, you can add a property in the properties section.

```xml
<properties>
   <snakeyaml.version>3.0</snakeyaml.version>
</properties>
```

For Gradle, we can edit this property in a \`gradle.properties\` file:

```
snakeyaml.version=3.0
```

This is the preferred way over dependency management since \`snakeyaml\` is just a single library. However, in many cases, it is more than one library. The version can refer to a [++BOM++](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms) (bill of materials), not to be confused with an SBOM, representing a set of groups of packages that need to be used together and essentially have the same version --- such as an API and an implementation package. Both need to be on the same version to perform as expected.

#### Dependency management declaration

An alternative option is to use the mechanisms in your build tool to update transitive dependencies. This should only be used if you cannot update the version property as shown in the previous section

For Maven, it's most commonly added to the \`dependencyManagement\` block. This block will ensure that the updated version mentioned in the \`dependencyManagement\` block is used every time the library is called as a transitive dependency.

**Maven:**

```xml
<dependencyManagement>
   <dependencies>
       <dependency>
           <groupId>org.yaml</groupId>
           <artifactId>snakeyaml</artifactId>
           <version>2.2</version>
       </dependency>
   </dependencies>
</dependencyManagement>
```

Since we use the [++dependency-management++](https://docs.spring.io/dependency-management-plugin/docs/current/reference/html/)plugin for Spring in our Gradle file, we have pretty similar capabilities for Gradle available. Note that this plugin was inserted by the Spring Boot initializer when scaffolding my project.

**Gradle:**

```groovy
dependencyManagement {
   dependencies {
       dependency 'org.yaml:snakeyaml:2.2'
   }
}
```

If you don't use the \`io.spring.dependency-management\` plugin, you can also add [++constraints++](https://docs.gradle.org/current/userguide/dependency_constraints.html#sec:adding-constraints-transitive-deps) to transitive dependencies in Gradle to update a transitive dependency like the one below. Remember that these constraints do not play well with the Spring \`dependency-management\` plugin. So it is either one or the other.

```groovy
dependencies {
    …
    constraints {
        implementation('org.yaml:snakeyaml:2.2') {
            because 'previous versions have a security issue'
        }
    …
}
```

## Scanning your Spring Boot applications with Snyk

Scanning your Spring Boot application with Snyk is essential for ensuring the security and stability of your software. Snyk helps identify in your application's dependencies, which can be exploited by malicious actors if left unaddressed. After reading this article, you also know what the best way is to implement the remediation advice provide by Snyk.  

Regularly scanning your codebase proactively addresses security issues and reduces the risk of data breaches and other security incidents. So make sure that scanning is part of your regular flow and that you are well aware of how to respond when a vulnerability occurs.

Below, I scanned my application with the [++Snyk CLI++](https://snyk.io/platform/snyk-cli/) both before and after remediation. The remediation path I chose was updating the general Spring Boot version and updating the specific version property for \`snakeyaml\`.

Before:
![blog-spring-boot-21-paths](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1701273847%2Fblog-spring-boot-21-paths.jpg&w=2560&q=75)

After:
![blog-sprint-boot-6-paths](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1701273847%2Fblog-sprint-boot-6-paths.jpg&w=2560&q=75)

So, remember, when you find a vulnerability with Snyk related to a dependency in your Spring Boot application, follow these steps:

* Update your general Spring Boot version over updating a specific starter.
* Updating the version properties of the Spring Boot dependencies when updating the Spring Boot version is insufficient.
* Use dependency management in Maven or Gradle to update specific transitive dependencies.
