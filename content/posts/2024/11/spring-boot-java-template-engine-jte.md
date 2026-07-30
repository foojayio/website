---
title: "Spring Boot: Java Template Engine (JTE)"
slug: "spring-boot-java-template-engine-jte"
date: "2024-11-13T09:03:00+00:00"
lastmod: "2024-11-13T16:26:06+00:00"
description: "Java Template Engine(jte) offers a secure and efficient solution tailored for Java and Kotlin."
authors:
  - "mahendra1413"
image: "https://foojay.io/wp-content/uploads/2024/11/jte.png"
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

### Hello to all Java and Spring enthusiasts. I am the Java Template Engine, a fresh newcomer to the Spring Initializer Ecosystem. {#h3-0-hello-to-all-java-and-spring-enthusiasts-i-am-the-java-template-engine-a-fresh-newcomer-to-the-spring-initializer-ecosystem}

I have gained popularity as a template engine for developing user interface applications that use Java syntax within Spring Boot applications, alongside other engines such as Freemarker, Thymeleaf, Mustache, and Velocity. Many developers widely use Spring Boot, a framework that simplifies Java application development, and incorporating a template engine like mine streamlines the process of creating dynamic and responsive user interfaces.

<br />

<br />

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

### Maven {#h3-1-maven}

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
 &lt;groupId&gt;gg.jte&lt;/groupId&gt;
 &lt;artifactId&gt;jte&lt;/artifactId&gt;
 &lt;version&gt;3.1.13&lt;/version&gt;
&lt;/dependency&gt;

</pre>

### Gradle {#_maven}

<pre class="EnlighterJSRAW" data-enlighter-language="generic">implementation("gg.jte:jte:3.1.13")</pre>

To render any template, you must utilize an instance of the `gg.jte.TemplateEngine` for an entire application.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.CodeResolver;
import gg.jte.resolve.DirectoryCodeResolver;

// ...

CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("jte")); // This is the directory where your .jte files are located.
TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);</pre>

Once we fully initialize the template engine instance, we can begin rendering the template.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;

// ...

TemplateOutput templateOutput = new StringOutput();
templateEngine.render("home.jte", model, templateOutput);

</pre>

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

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;
    &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
    &lt;parent&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;
        &lt;version&gt;3.3.4&lt;/version&gt;
        &lt;relativePath/&gt; &lt;!-- lookup parent from repository --&gt;
    &lt;/parent&gt;
    &lt;groupId&gt;com.bsmlabs&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-jte&lt;/artifactId&gt;
    &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
    &lt;name&gt;spring-boot-jte&lt;/name&gt;
    &lt;description&gt;Spring Boot Project with Java Template Engine&lt;/description&gt;
    &lt;url/&gt;
    &lt;licenses&gt;
        &lt;license/&gt;
    &lt;/licenses&gt;
    &lt;developers&gt;
        &lt;developer/&gt;
    &lt;/developers&gt;
    &lt;scm&gt;
        &lt;connection/&gt;
        &lt;developerConnection/&gt;
        &lt;tag/&gt;
        &lt;url/&gt;
    &lt;/scm&gt;
    &lt;properties&gt;
        &lt;java.version&gt;23&lt;/java.version&gt;
    &lt;/properties&gt;
    &lt;dependencies&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-data-jpa&lt;/artifactId&gt;
        &lt;/dependency&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
        &lt;/dependency&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;gg.jte&lt;/groupId&gt;
            &lt;artifactId&gt;jte&lt;/artifactId&gt;
            &lt;version&gt;3.1.13&lt;/version&gt;
        &lt;/dependency&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;gg.jte&lt;/groupId&gt;
            &lt;artifactId&gt;jte-spring-boot-starter-3&lt;/artifactId&gt;
            &lt;version&gt;3.1.13&lt;/version&gt;
        &lt;/dependency&gt;

        &lt;dependency&gt;
            &lt;groupId&gt;com.h2database&lt;/groupId&gt;
            &lt;artifactId&gt;h2&lt;/artifactId&gt;
            &lt;scope&gt;runtime&lt;/scope&gt;
        &lt;/dependency&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
            &lt;scope&gt;test&lt;/scope&gt;
        &lt;/dependency&gt;
    &lt;/dependencies&gt;

    &lt;build&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;gg.jte&lt;/groupId&gt;
                &lt;artifactId&gt;jte-maven-plugin&lt;/artifactId&gt;
                &lt;version&gt;3.1.13&lt;/version&gt;
                &lt;executions&gt;
                    &lt;execution&gt;
                        &lt;id&gt;jte-generate&lt;/id&gt;
                        &lt;phase&gt;generate-sources&lt;/phase&gt;
                        &lt;goals&gt;
                            &lt;goal&gt;generate&lt;/goal&gt;
                        &lt;/goals&gt;
                        &lt;configuration&gt;
                            &lt;sourceDirectory&gt;${project.basedir}/src/main/jte&lt;/sourceDirectory&gt;
                            &lt;contentType&gt;Html&lt;/contentType&gt;
                            &lt;binaryStaticContent&gt;true&lt;/binaryStaticContent&gt;
                            &lt;targetResourceDirectory&gt;${project.build.outputDirectory}&lt;/targetResourceDirectory&gt;
                        &lt;/configuration&gt;
                    &lt;/execution&gt;
                &lt;/executions&gt;
            &lt;/plugin&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
                &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/build&gt;

&lt;/project&gt;

</pre>

Unzip the project and import into IntelliJ /Eclipse IDE

Let's create a simple controller called **IndexController and create Page Layout with instance fields**

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package com.bsmlabs.jte;

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
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic"><br>package com.bsmlabs.jte;

public record Page(String title, String description) {
}</pre>

The next step involves creating a template **index.jte** within the **/src/main/jte** folder, a directory that is established during the generation process at start.spring.io.  

```
 
```

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@import com.bsmlabs.jte.Page

@param String name
@param Page page

&lt;!DOCTYPE html&gt;
&lt;html lang="en"&gt;
&lt;head&gt;
    &lt;title&gt;${page.title()}&lt;/title&gt;
    @if(page.description() != null)
       &lt;meta name="description" content="${page.description()}"&gt;
    @endif
    &lt;script src="https://cdn.tailwindcss.com"&gt;&lt;/script&gt;
&lt;/head&gt;
&lt;body&gt;
&lt;nav class="bg-gray-800"&gt;
    &lt;div class="hidden sm:ml-6 sm:block"&gt;
        &lt;div class="flex space-x-4"&gt;
            &lt;a href="#" class="rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white" aria-current="page"&gt;Dashboard&lt;/a&gt;
            &lt;a href="#" class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"&gt;Blog&lt;/a&gt;
            &lt;a href="#" class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"&gt;Projects&lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/nav&gt;
&lt;p class="text-slate-700 dark:text-slate-500 font-medium"&gt;${name}!&lt;/p&gt;
&lt;/body&gt;
&lt;/html&gt;

</pre>

```

```

1. The directive **@import** corresponds directly to Java imports in this context, thereby ensuring that `com.bsmlabs.jte.Page` is recognized by the template.
2. The parameter required for this template is the Page object i.e., `@param Page page`
3. The @if/@endif construct represents an if-block. The content within the parentheses (page.getDescription() != null) consists of standard Java code.
4. `${}` used for writing the template output inside the expression.

Inside `application.properties` we will add the following properties

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># When developmentMode is set to true, the jte file watcher will monitor template changes and recompile them accordingly.
gg.jte.development-mode=true

spring.application.name=spring-boot-jte

# By default template files are available in /src/main/jte
gg.jte.templateLocation=src/main/jte

# with templateSuffix as .jte
gg.jte.templateSuffix=.jte</pre>

As we are developing with Spring Boot and incorporating the `spring-boot-starter-web starter`, both `org.springframework.web.servlet.ViewResolver` and **templateEngine** will be automatically configured.

Conclusion {#h2-3-conclusion}
-----------------------------

By integrating the **Java Template Engine (JTE)** into various frameworks, one can experience a novel approach that provides significant benefits, including superior performance, increased concurrency, and quicker template rendering.

The complete code can be found [over on Github](https://github.com/bsmahi/spring-boot-jte).

References {#h2-4-references}
-----------------------------

<https://jte.gg>

Dan Vega Youtube link: <https://www.youtube.com/watch?v=KoWgHSWA1cc>

<br />
