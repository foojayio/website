---
title: "The Flatten Maven plugin - fooay.io"
slug: "flatten-maven-plugin"
date: "2022-01-31T14:06:05+00:00"
lastmod: "2022-01-31T14:24:35+00:00"
description: "Don’t wait until the release of Maven 5, if you are a library developer, consider using Maven Flatter Plugin. It’s a freebie.  "
canonical: "https://blog.frankel.ch/maven-flatten-plugin/"
authors:
  - "nicolas-frankel"
image: "flatten-5964529.jpg"
categories:
  - "Maven"
  - "Tools"
tags:
related_posts:
  - "faster-maven-builds-1"
  - "fixing-vulnerabilities-in-maven-projects"
  - "understanding-apache-maven-part-8-maven-plugins"
  - "effective-cloud-native-java-app-development-with-open-liberty-in-intellij-idea"
enlighterjs: true
frozen: false
---

One of the Apache Maven committers recently wrote about [their plans for Maven 5](https://www.javaadvent.com/2021/12/from-maven-3-to-maven-5.html). I consider the following one of the most significant changes:
> In summary, we need to make a distinction between two POM types: the build POM, stored in the project source control, that uses v5 schema for build time, requiring a new Maven version able to use the new features associated with the new schema. The other one is the consumer POM, that is published to Maven Central in the good old v4 schema, so every past or future build tool can continue to consume pre-built artifacts as usual for their dependencies.

It's an important dichotomy that escaped me for a long time:

* Consumers of the POM require some data, *e.g.*, users who have the project in their dependencies list
* Binary(ies) builders require other data

There are additional concerns. For example, variables make sense for the project's developers to leverage the . For consumers, it's an extra layer of indirection that makes understanding the POM harder.

On Reddit, user pmarschall mentioned they were already separating between the concerns in Maven's current version with the help of the [Maven Flatten plugin](https://www.mojohaus.org/flatten-maven-plugin/). It got me interested, and I wanted to try it. For that, I used the Spring Pet Clinic project - commit [a7439c7](https://github.com/spring-projects/spring-petclinic).

Usage is very straightforward. Just add the following snippet in the `plugins` section:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>1.2.5</version>
    <configuration>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten.clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Now, if you execute the Maven `process-resources` phase, the plugin will create a shortened version of the POM, named `.flattened-pom.xml`. Compared to the initial POM, the flattened POM has only three sections in addition to the coordinates: `licenses`, `dependencies`, and `repositories`. Additionally, Maven has resolved all variables. If you run the `install` phase and then check your local Maven repository, you'll notice the POM matches the flattened POM, not the main one. If you want to generate the flattened POM but not replace the main one, use `-DupdatePomFile=false`.

By default, the plugin keeps only the `licenses`, `dependencies`, and `repositories` sections. You can configure which sections you keep and don't via the POM. For example, the plugin removes `name`, but you can keep it easily if needed. Just add the relevant configuration:

```xml
<configuration>
    <pomElements>
        <name>keep</name>
    </pomElements>
</configuration>
```

The above method gives you the most flexibility. However, developers of the plugins have already thought about which configuration bundles make sense and offer them out-of-the-box. Here's an excerpt from the documentation that describes them:

|           Mode            |                                                                                                                                                                  Description                                                                                                                                                                   |
|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `oss`                     | For Open-Source-Software projects that want to keep all `FlattenDescriptor` optional POM elements except for `repositories` and `pluginRepositories`.                                                                                                                                                                                          |
| `ossrh`                   | Keeps all `FlattenDescriptor` optional POM elements that are required for OSS Repository-Hosting.                                                                                                                                                                                                                                              |
| `bom`                     | Like `ossrh` but additionally keeps `dependencyManagement` and `properties`. Especially it will keep the dependencyManagement as-is without resolving parent influences and import-scoped dependencies. This is useful if your POM represents a and you do not want to deploy it as is (to remove parent and resolve version variables, etc.). |
| `defaults`                | The default mode that removes all `FlattenDescriptor` optional POM elements except `repositories`.                                                                                                                                                                                                                                             |
| `clean`                   | Removes all `FlattenDescriptor` optional POM elements.                                                                                                                                                                                                                                                                                         |
| `fatjar`                  | Removes all `FlattenDescriptor` optional POM elements and all `dependencies`.                                                                                                                                                                                                                                                                  |
| `resolveCiFriendliesOnly` | Only resolves variables revision, sha1 and changelist. Keeps everything else. See Maven CI Friendly for further details.                                                                                                                                                                                                                       |

## Conclusion

The Maven Flatten plugin separates between build and consumer POMs. You don't need to wait until the release of Maven 5. It's a freebie, so if you're a library provider, you should probably consider using it.

**To go further:**

* [From Maven 3 to Maven 5](https://www.javaadvent.com/2021/12/from-maven-3-to-maven-5.html)
* [Flatten Maven Plugin](https://www.mojohaus.org/flatten-maven-plugin/)
* [Flatten Mojo description and configuration](https://www.mojohaus.org/flatten-maven-plugin/flatten-mojo.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/maven-flatten-plugin/) on January 30^th^, 2022*

*[BOM]: Bill Of Material
*[DRY]: Don't Repeat Yourself
