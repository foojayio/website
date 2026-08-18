---
title: "How to Create Self-Contained Executable JARs"
date: "2021-02-01T08:40:08+00:00"
lastmod: "2021-08-23T13:03:51+00:00"
description: "This article aims to describe ways to create self-contained executable JARs, also known as uber-JARs or fat JARs."
canonical: "https://blog.frankel.ch/creating-self-contained-executable-jars/"
authors:
  - "nicolas-frankel"
image: "image-12.png"
categories:
  - "Java Core"
related_posts:
frozen: false
---

When your application goes beyond a dozen of lines of code, you should probably split the code into multiple classes. At this point, the question is how to distribute them. In Java, the classical format is the Java ARchive, better known as the JAR. But real-world applications probably depend on other JARs.

This article aims to describe ways to create self-contained executable JARs, also known as uber-JARs or fat JARs.

### What is an Executable JAR?

A JAR is just a collection of class files. To be executable, its `META-INF/MANIFEST.MF` file should point to the class that implements the `main()` method. You do this with the `Main-Class` attribute. Here's an example:

```
Main-Class: path.to.MainClass
```

In the above, `MainClass` has a `static main(String...​ args)` method.

### Handling the Classpath

Most applications depend on existing code. Java provides the concept of the **classpath** . The classpath is a list of path elements that the runtime will look into to find dependent code. When **running Java classes** , you define the classpath via the `-cp` command-line option:

```
java -cp lib/one.jar;lib/two.jar;/var/lib/three.jar path.to.MainClass
```

The Java runtime creates the classpath by aggregating all classes from all referenced JARs and adding the main class.

New problems arise when distributing JARs that depend on other JARs:

1. You need to define the same libraries in the same version
2. More importantly, **the `-cp` argument doesn't work with JARs** . To reference other JARs, the classpath needs to be set in a JAR's manifest via the `Class-Path` attribute:`Class-Path: lib/one.jar;lib/two.jar;/var/lib/three.jar`
3. For this reason, you need to put JARs in the same location, relative **or**absolute\*, on the target filesystem as per the manifest. That implies to open the JAR and read the manifest first.

One way to solve those issues is to create a unique deployment unit that contains classes from all JARs and that can be distributed as one artifact. There are several options to create such JARs:

* The Assembly plugin
* The Shade plugin
* The Spring Boot plugin (for Spring Boot projects)

#### The Apache Assembly Plugin

The Assembly Plugin for Maven enables developers to combine project output into a single distributable archive that also contains dependencies, modules, site documentation, and other files.--- [Apache Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/index.html)

One Maven design rule is to create one artifact per project. There are exceptions *e.g.*Javadocs artifacts and source artifacts, but in general, if you want multiple artifacts, you need to create one project per artifact. The idea behind the Assembly plugin is to work around this rule.

The Assembly plugin relies on a specific `assembly.xml` configuration file. It allows you to pick and choose which files will be included in the artifact. Note that the final artifact doesn't need to be a JAR: the configuration file lets you choose between available formats *e.g.* zip, war, etc.

The plugin manages common use-cases by providing pre-defined assemblies. The distribution of self-contained JARs is among them. The configuration looks like the following:

```xml
<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>                            
    </descriptorRefs>
    <archive>
      <manifest>
        <mainClass>ch.frankel.blog.executablejar.ExecutableJarApplication</mainClass> 
      </manifest>
    </archive>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>single</goal>                                                           
      </goals>
      <phase>package</phase>                                                          
    </execution>
  </executions>
</plugin>
```

1. Reference the pre-defined self-contained JAR configuration
2. Set the main class to execute
3. Execute the `single` goal
4. Bind the goal to the `package` phase *i.e.* after the original JAR has been built

Running `mvn package` yields two artifacts:

1. `<name>-<version>.jar`
2. `<name>-<version>-with-dependencies.jar`

The first JAR has the same content as the one that would have been created without the plugin. The second is the self-contained JAR. You can execute it like this:

```
java -jar target/executable-jar-0.0.1-SNAPSHOT.jar
```

Depending on the project, it may execute successfully...​ or not. For example, it fails in the sample Spring Boot project with the following message:

```
%d [%thread] %-5level %logger - %msg%n java.lang.IllegalArgumentException:
  No auto configuration classes found in META-INF/spring.factories.
  If you are using a custom packaging, make sure that file is correct.
```

The reason is that different JARs provide **different resources under the same path** *e.g.* `META-INF/spring.factories`. The plugin follows a last write wins strategy. The order is based on the name of the JAR.

With Assembly, you can **exclude resources but not merge them**. When you need to merge resources, you'll probably want to use the Apache Shade plugin instead.

#### The Apache Shade Plugin

The Assembly plugin is generic; the Shade plugin solely focuses on the task of creating self-contained JARs.

This plugin provides the capability to package the artifact in an uber-jar, including its dependencies and to shade - i.e. rename - the packages of some of the dependencies.--- [Apache Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/index.html)

The plugin is based on the concept of *transformers*: each transformer is responsible to handle one single type of resource. A transformer can copy a resource as-is, append static content, merge it with others, etc.

While you can develop a transformer, the plugin provides a set of out-of-the-box transformers:

|             TRANSFORMER              |                              DESCRIPTION                               |
|--------------------------------------|------------------------------------------------------------------------|
| `ApacheLicenseResourceTransformer`   | Prevents license duplication                                           |
| `ApacheNoticeResourceTransformer`    | Prepares merged `NOTICE`                                               |
| `AppendingTransformer`               | Adds content to a resource                                             |
| `ComponentsXmlResourceTransformer`   | Aggregates Plexus `components.xml`                                     |
| `DontIncludeResourceTransformer`     | Prevents inclusion of matching resources                               |
| `GroovyResourceTransformer`          | Merges Apache Groovy extends modules                                   |
| `IncludeResourceTransformer`         | Adds files from the project                                            |
| `ManifestResourceTransformer`        | Sets entries in the `MANIFEST`                                         |
| `PluginXmlResourceTransformer`       | Aggregates Mavens `plugin.xml`                                         |
| `ResourceBundleAppendingTransformer` | Merges ResourceBundles                                                 |
| `ServicesResourceTransformer`        | Relocated class names in `META-INF/services` resources and merges them |
| `XmlAppendingTransformer`            | Adds XML content to an XML resource                                    |
| `PropertiesTransformer`              | Merges properties files owning an ordinal to solve conflicts           |
| `OpenWebBeansPropertiesTransformer`  | Merges Apache OpenWebBeans configuration files                         |
| `MicroprofileConfigTransformer`      | Merges conflicting Microprofile Config properties based on an ordinal  |

The Shade plugin configuration to the Assembly's above is the following:

```xml
<plugin>
  <artifactId>maven-shade-plugin</artifactId>
  <executions>
    <execution>
      <id>shade</id>
      <goals>
        <goal>shade</goal>                        
      </goals>
      <configuration>
        <transformers>
          <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"> 
            <mainClass>ch.frankel.blog.executablejar.ExecutableJarApplication</mainClass> 
            <manifestEntries>
              <Multi-Release>true</Multi-Release> 
            </manifestEntries>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

1. The `shade` goal is bound to the `package` phase by default
2. This transformer is dedicated to generating manifest files
3. Set the `Main-Class` entry
4. Configure the final JAR to be a [multi-release JAR](https://www.baeldung.com/java-multi-release-jar). This is necessary when any of the initial JARs is a multi-release JAR

Running `mvn package` yields two artifacts:

1. `<name>-<version>.jar`: the self-contained executable JAR
2. `original-<name>-<version>.jar`: the "normal" JAR without the embedded dependencies

With the sample project, the final executable still doesn't work as expected. Indeed, there are a lot of warnings regarding duplicate resources during the build. Two of them prevent the sample project from working correctly. To merge them correctly, we need to have a look at their format:

* `META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat`: This Log4J2 file contains pre-compiled Log4J2 plugin data. It's encoded in binary format and none of the out-of-the-box transformers can merge such files. Yet, a casual search reveals somebody already had this issue and released [a transformer](https://github.com/edwgiz/maven-shaded-log4j-transformer) to handle the merge.
* `META-INF/spring.factories`: These Spring-specific files have a single key/multiple values format. While they are text-based, no out-of-the-box transformer can merge them correctly. However, the Spring developers provide this capability (and much more) in [their plugin](https://github.com/spring-projects/spring-boot).

To configure these transformers, we need to add the above libraries as dependencies to the Shade plugin:

```xml
<plugin>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.2.4</version>
  <executions>
    <execution>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <transformers>
          <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <mainClass>ch.frankel.blog.executablejar.ExecutableJarApplication</mainClass>
            <manifestEntries>
              <Multi-Release>true</Multi-Release>
            </manifestEntries>
          </transformer>
          <transformer implementation="com.github.edwgiz.maven_shade_plugin.log4j2_cache_transformer.PluginsCacheFileTransformer" /> 
          <transformer implementation="org.springframework.boot.maven.PropertiesMergingResourceTransformer"> 
            <resource>META-INF/spring.factories</resource>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>com.github.edwgiz</groupId>
      <artifactId>maven-shade-plugin.log4j2-cachefile-transformer</artifactId> 
      <version>2.14.0</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>                        
      <version>2.4.1</version>
    </dependency>
  </dependencies>
</plugin>
```

1. Merge Log4J2 `.dat` files
2. Merge `/META-INF/spring.factories` files
3. Add the required transformers code

This configuration works! Still, there are remaining warnings:

* Manifests
* Licenses, notices and similar files
* Spring Boot specific files *i.e.* `spring.handlers`, `spring.schemas` and `spring.tooling`
* Spring Boot-Kotlin specific files *e.g.* `spring-boot.kotlin_module`, `spring-context.kotlin_module`, etc.
* Service loader configuration files
* JSON files

You can add and configure additional transformers to fix the remaining warnings. All in all, the whole process requires a deep understanding of each kind of resource and how to handle them.

#### The Spring Boot plugin

The Spring Boot plugin adopts an entirely different approach. It doesn't merge resources from JARs individually; it adds dependent JARs **as they are** inside the uber JAR. To load classes and resources, it provides a specific class-loading mechanism. Obviously, it's dedicated to Spring Boot projects.

Configuring the Spring Boot plugin is straightforward:

```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <version>2.4.1</version>
  <executions>
    <execution>
      <goals>
        <goal>repackage</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

{{< img src="image-12.png" class="size-large is-resized" width="269" height="233" >}}

1. Project compiled classes
2. JAR dependencies
3. Spring Boot class-loading classes

Here's an excerpt of the manifest for our sample project:MANIFEST.MF

```
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: ch.frankel.blog.executablejar.ExecutableJarApplication
```

As you can see, the main class is a Spring Boot specific class while the "real" main class is referenced under another entry.

For more information on the structure of the JAR, please check the [reference documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-executable-jar-format.html).

### Conclusion

In this post, we've described 3 different ways to create self-contained executable JARs:

1. Assembly is a good fit for simple projects
2. When the project starts being more complex and you need to handle duplicate files, use Shade
3. Finally, for Spring Boot projects, your best bet is the dedicated plugin

The complete source code for this post can be found on [Github](https://github.com/ajavageek/executable-jar) in Maven format.

## To go further:

* [Maven Assembly plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)
* [Maven Shade plugin](https://maven.apache.org/plugins/maven-shade-plugin/)
* [maven-shaded-log4j-transformer](https://github.com/edwgiz/maven-shaded-log4j-transformer)
* [Maven Spring Boot plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)
* [The Executable Jar Format](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-executable-jar-format.html)
