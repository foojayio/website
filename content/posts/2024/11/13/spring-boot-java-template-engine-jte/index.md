---
title: "Spring Boot: Java Template Engine (JTE)"
slug: "spring-boot-java-template-engine-jte"
date: "2024-11-13T09:03:00+00:00"
lastmod: "2024-11-13T16:26:06+00:00"
description: "Java Template Engine(jte) offers a secure and efficient solution tailored for Java and Kotlin."
authors:
  - "mahendra1413"
image: "jte.png"
categories:
  - "IntelliJ IDEA"
  - "Java"
  - "Maven"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "a-simple-service-with-spring-boot"
  - "annotation-free-spring"
  - "better-error-handling-for-your-spring-boot-rest-apis"
enlighterjs: true
frozen: false
---

### Hello to all Java and Spring enthusiasts. I am the Java Template Engine, a fresh newcomer to the Spring Initializer Ecosystem.

I have gained popularity as a template engine for developing user interface applications that use Java syntax within Spring Boot applications, alongside other engines such as Freemarker, Thymeleaf, Mustache, and Velocity. Many developers widely use Spring Boot, a framework that simplifies Java application development, and incorporating a template engine like mine streamlines the process of creating dynamic and responsive user interfaces.

Here's my definition:
> **The JTE documentation highlights that the **Java Template Engine(jte)** offers a secure and efficient solution tailored for Java and Kotlin. JTE aims to minimize the introduction of new keywords and uses existing language features, simplifying the understanding of a template's functionality.**

I am pleased to present the following features that I offer:

* I provide simplified and user-friendly syntax.
* You can write plain Java or \[Kotlin\](<https://kotlinlang.org/> "Kotlin") for expressions.
* The system performs context-aware HTML escaping at compile time.
* The \[IntelliJ plugin\](<https://plugins.jetbrains.com/plugin/14521-jte> "IntelliJ plugin") delivers Intellisense and refactoring capabilities.
* Automatic reloading functionality occurs during the development process.
* I ensure enhanced execution speed. For more details please refer this [link](https://jte.gg/#performance)

{#more-114515}

**Java Template Engine (JTE) is capable of integrating with various frameworks.**

1. Spring MVC
2. Spring Boot
3. Ktor
4. Micronaut
5. Quarkus
6. Javalin
7. Eclipse Vert.x
8. Severell
9. http4k

The respective build tools need to integrate the following dependency in the `pom.xml` and `build.gradle`. For,

### Maven

```
<dependency>
 <groupId>gg.jte</groupId>
 <artifactId>jte</artifactId>
 <version>3.1.13</version>
</dependency>
```

### Gradle

```
implementation("gg.jte:jte:3.1.13")
```

To render any template, you must utilize an instance of the `gg.jte.TemplateEngine` for an entire application.

```
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.CodeResolver;
import gg.jte.resolve.DirectoryCodeResolver;

// ...

CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("jte")); // This is the directory where your .jte files are located.
TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
```

Once we fully initialize the template engine instance, we can begin rendering the template.

```
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;

// ...

TemplateOutput templateOutput = new StringOutput();
templateEngine.render("home.jte", model, templateOutput);
```

Here, the term `templateOutput` indicates where the template renders. We have different template output implementation as follows

* `gg.jte.output.StringOutput` - generates output in the form of a String.
* `gg.jte.output.FileOutput` - writes content to a designated java.io.File.
* ` gg.jte.output.PrintWriterOutput` - outputs to a PrintWriter, for example, the writer obtained from HttpServletRequest.
* `gg.jte.output.WriterOutput` - transmits data to a java.io.Writer.

The `model` refers to the data provided to this template, which may be an instance of any class.

Now it's time for us to get involved with a practical Spring Boot example. To start with

**Step1:** Access [start.spring.io](http://start.spring.io)

**Step2:** Choose project, language, Spring Boot version(preferably released version), project metadata, along with Java version

**Step3:** Under dependency selection, please add both `Web` and `JTE` **(Template Engines)** dependencies.

**Step4:** And then you generate and download the project

`pom.xml` looks like this

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.4</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.bsmlabs</groupId>
    <artifactId>spring-boot-jte</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-jte</name>
    <description>Spring Boot Project with Java Template Engine</description>
    <url/>
    <licenses>
        <license/>
    </licenses>
    <developers>
        <developer/>
    </developers>
    <scm>
        <connection/>
        <developerConnection/>
        <tag/>
        <url/>
    </scm>
    <properties>
        <java.version>23</java.version>
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
            <groupId>gg.jte</groupId>
            <artifactId>jte</artifactId>
            <version>3.1.13</version>
        </dependency>
        <dependency>
            <groupId>gg.jte</groupId>
            <artifactId>jte-spring-boot-starter-3</artifactId>
            <version>3.1.13</version>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
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
                <groupId>gg.jte</groupId>
                <artifactId>jte-maven-plugin</artifactId>
                <version>3.1.13</version>
                <executions>
                    <execution>
                        <id>jte-generate</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <sourceDirectory>${project.basedir}/src/main/jte</sourceDirectory>
                            <contentType>Html</contentType>
                            <binaryStaticContent>true</binaryStaticContent>
                            <targetResourceDirectory>${project.build.outputDirectory}</targetResourceDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

Unzip the project and import into IntelliJ /Eclipse IDE

Let's create a simple controller called **IndexController and create Page Layout with instance fields**

```
package com.bsmlabs.jte;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String getIndex(Model model) {
       model.addAttribute("name", "Welcome To Java Template Engine");
       model.addAttribute("page", new Page("Hello JTE", "JTE Application"));

       return "index";
    }
}
```

```
<br>package com.bsmlabs.jte;

public record Page(String title, String description) {
}
```

The next step involves creating a template **index.jte** within the **/src/main/jte** folder, a directory that is established during the generation process at start.spring.io.  

```
 
```

```
@import com.bsmlabs.jte.Page

@param String name
@param Page page

<!DOCTYPE html>
<html lang="en">
<head>
    <title>${page.title()}</title>
    @if(page.description() != null)
       <meta name="description" content="${page.description()}">
    @endif
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>
<nav class="bg-gray-800">
    <div class="hidden sm:ml-6 sm:block">
        <div class="flex space-x-4">
            <a href="#" class="rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white" aria-current="page">Dashboard</a>
            <a href="#" class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white">Blog</a>
            <a href="#" class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white">Projects</a>
        </div>
    </div>
</nav>
<p class="text-slate-700 dark:text-slate-500 font-medium">${name}!</p>
</body>
</html>
```

```

```

1. The directive **@import** corresponds directly to Java imports in this context, thereby ensuring that `com.bsmlabs.jte.Page` is recognized by the template.
2. The parameter required for this template is the Page object i.e., `@param Page page`
3. The @if/@endif construct represents an if-block. The content within the parentheses (page.getDescription() != null) consists of standard Java code.
4. `${}` used for writing the template output inside the expression.

Inside `application.properties` we will add the following properties

```
# When developmentMode is set to true, the jte file watcher will monitor template changes and recompile them accordingly.
gg.jte.development-mode=true

spring.application.name=spring-boot-jte

# By default template files are available in /src/main/jte
gg.jte.templateLocation=src/main/jte

# with templateSuffix as .jte
gg.jte.templateSuffix=.jte
```

As we are developing with Spring Boot and incorporating the `spring-boot-starter-web starter`, both `org.springframework.web.servlet.ViewResolver` and **templateEngine** will be automatically configured.

## Conclusion

By integrating the **Java Template Engine (JTE)** into various frameworks, one can experience a novel approach that provides significant benefits, including superior performance, increased concurrency, and quicker template rendering.

The complete code can be found [over on Github](https://github.com/bsmahi/spring-boot-jte).

## References

<https://jte.gg>

Dan Vega Youtube link: <https://www.youtube.com/watch?v=KoWgHSWA1cc>
