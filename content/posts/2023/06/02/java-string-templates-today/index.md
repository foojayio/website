---
title: "Java String Templates Today"
date: "2023-06-02T16:28:25+00:00"
lastmod: "2023-06-02T16:29:05+00:00"
description: "String manipulation is improving (JEP 430), but Manifold can help us now. Even on JDK 8, it goes further with a sophisticated template engine"
canonical: "https://debugagent.com/java-string-templates-today"
authors:
  - "shai-almog"
image: "thumbnail-19.png"
categories:
  - "Developer Tools"
  - "Tutorials"
related_posts:
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
  - "relearning-java-thread-primitives"
  - "boldness-in-refactoring"
frozen: false
---

In [our last article](https://foojay.io/today/revolutionize-json-parsing-in-java-with-manifold/), we introduced you to the [Manifold](http://manifold.systems/) project and how it offers a revolutionary set of language extensions for Java, including the ability to parse and process JSON files seamlessly in Java.

Today, we will take a look at another exciting feature of the Manifold project: **string templates**.

{{< youtube ite6a5y50yQ >}}

But before we get to that, some of the feedback I got from the previous post was that I was unclear about Manifold. Manifold is a combination of an IDE plugin and plugins to Maven or Gradle.

Once used we can enhance the Java language (or environment) almost seamlessly in a fluid way.

A frequent question was how is it different from something like [Lombok](https://www.youtube.com/watch?v=ouPPle7v-hs)?

There are many similarities and in fact, if you understand Lombok then you are on your way to understand Manifold. Lombok is a great solution for some problems in the Java language. It is a bandaid on the verbosity of Java and some of its odd limitations (I mean bandaid as a compliment, no hate mail). Manifold differs from Lombok in several critical ways:

* It's modular - all the extensions built into Manifold are separate from one another. You can activate a particular feature or leave it out of the compiler toolchain.
* It's bigger - Lombok has many features but Manifold's scope is fantastic and far more ambitious.
* It tries to do the "right thing" - Lombok is odd. We declare private fields but then use getters and setters as if they aren't private. Manifold uses properties (which we will discuss later) that more closely resemble what the Java language "should have offered".

Manifold also has some drawbacks:

* It only works as a compiler toolchain and only in one way. Lombok can be compiled back to plain Java source code and removed.
* It only supports one IDE - IntelliJ.

These partially relate to the age of Manifold which is a new project by comparison. But it also relates to the different focus. Manifold focuses on language functionality and a single working fluid result.

## JEP 430 String Interpolation

One of the big features coming to JDK 21 is [JEP 430](https://openjdk.org/jeps/430), which is a string interpolation language change. It will allow writing code like this:

```
String name = "Joan";
String info = STR."My name is \{name}";
```

In this case, `info` will have the value `"My name is Joan"`. This is just the tip of the iceberg in this JSR as the entire architecture is pluggable. I will discuss this in a future video but for now, the basic functionality we see here is pretty fantastic.

Unfortunately, it will take years to use this in production. It will be in preview in JDK 21, then it will be approved. We will wait for an LTS, and then wait for the LTS to reach critical mass. In the meantime, can we use something as nice as this today?

## Maven Dependencies

Before we dive into the code, I want to remind you that all the code for this and other videos in this series is available on [GitHub](https://github.com/shai-almog/java-book/) (feel free to star it and follow).

String templating has no dependencies. We still need to make changes to the pom file but we don't need to add dependencies. I'm adding one dependency here for the advanced templates we will discuss soon. All that's needed is the compiler plugin. That means that string templates are a compile-time feature and have no runtime impact!

```
<dependencies>
   <dependency>
       <groupId>systems.manifold</groupId>
       <artifactId>manifold-templates-rt</artifactId>
       <version>${manifold.version}</version>
   </dependency>
</dependencies>

<build>
   <plugins>
       <plugin>
           <groupId>org.apache.maven.plugins</groupId>
           <artifactId>maven-compiler-plugin</artifactId>
           <version>3.8.0</version>
           <configuration>
               <source>19</source>
               <target>19</target>
               <encoding>UTF-8</encoding>
               <compilerArgs>
                   <!-- Configure manifold plugin -->
                   <arg>-Xplugin:Manifold</arg>
               </compilerArgs>

               <!-- Add the processor path for the plugin -->
               <annotationProcessorPaths>
                   <path>
                       <groupId>systems.manifold</groupId>
                       <artifactId>manifold-strings</artifactId>
                       <version>${manifold.version}</version>
                   </path>
                   <path>
                       <groupId>systems.manifold</groupId>
                       <artifactId>manifold-templates</artifactId>
                       <version>${manifold.version}</version>
                   </path>
               </annotationProcessorPaths>
           </configuration>
       </plugin>
   </plugins>
</build>
```

## Manifold String Interpolation

To begin, we can create a new variable that we can use to get external input. In the second line, we integrate that variable into the printout:

```
String world = args.length > 0 ? args[0] : "world";
System.out.println("Hello $world! I can write \$world as the variable...");
```

The backslash syntax implicitly disables the templating behavior, just like in other string elements in Java. This will print `"Hello world! I can write $world as the variable..."`.

There's something that you can't really see in the code, you need to look at a screenshot of the same code:

{{< img src="84f52be2-30c3-435f-bbf6-4a53937b3859-700x59.png" class="size-medium" width="700" height="59" >}}

It's subtle, do you see it?

Notice the `$world` expression, it is colored differently. It's no longer just a string but a variable embedded in a string. This means that we can control-click it and go to the variable declaration, rename it, or see find its usage.

There's another way to escape a string, and we can use the `@DisableStringLiteralTemplates` annotation on a method or a class to disable this functionality in the respective block of code. This can be useful if we use the dollar sign frequently in a block of code:

```
@DisableStringLiteralTemplates
private static void noTemplate(String word) {
   System.out.println("Hello $world!");
}
```

## Templates

The Manifold project allows us to create JSP-like templates without all of the baggage. We can define a base class to a template to create generic code for the templates and place common functionality in a single location. We can create a file called HelloTemplate.html.mtl in the resources/templates directory with the following content. Notice the params we define in the template file can be anything:

```
<%@ params(String title, String body) %>
<!DOCTYPE html>
<html lang="en">
<head>
 <meta charset="UTF-8">
 <title>${title}</title>
</head>
<body>
   ${body}
</body>
</html>
```

This will seem very familiar to those of us with a JSP background. We can then use the file in the Java code like this, we can pass the parameters and they will replace the appropriate blocks in the HTML file:

```
System.out.println(HelloTemplate.render("My Title", "My Body"));
```

Notice the generated template it compiled to a class, similarly to JSP. Unlike JSP this template isn't a servlet and can be used in any context. A local application, a server, etc. The templating language is more lightweight and doesn't depend on various server APIs. It is also less mature. The main value is in using such an API to generate arbitrary files like Java source files or configuration files.

The templating capabilities are powerful yet simple. Just like we could in JSP, we can embed Java source code into the template e.g. we can include control flow and similar restrictions just like we could in JSP:

```
<% if(body != null) {%>

   ${body}

<% } %>
```

### Why Not: JSP, Velocity, Thymeleaf or Freemarker?

There are so many templating languages in Java already... Adding yet another one seems like a heavy burden of replication. I think all of those are great and this isn't meant to replace them, at least not yet.

Their focus is very much on web generation, they might not be ideal for more fluid use cases like code generation or web frameworks like Spark.

Another big advantage is size and performance. All of these frameworks have many dependencies and a lot of runtime overhead. Even JSP performs the initial compilation in runtime by default. This templating support is compiled and Spartan, in a good way. It's fast, simple and deeply integrated into the application flow.

### Import

We can import Java packages just like we can in every Java class using code like this:

```
<%@ import com.debugagent.stringtemplates.* %>
```

Once imported we can use any class within the code. Notice that this import statement must come above other lines in the code, just like a regular import statement.

### Include

We can use include to simply include another template into the current template, allowing us to assemble sophisticated templates like headers and footers. If we want to generate a complex Java class, we can wrap the boilerplate in a generic template and include that in. We can conditionally include a template using an if statement and use a for loop to include multiple entries:

```
<%@ include JavaCode("ClassName", classBody) %>
```

Notice that we can include an entry with parameters and pass them along to the underlying template. We can pass hardcoded strings or variables along the include chain.

### A Lot More

I skipped extends because of a documentation issue, which has since been fixed. it has a lot of potential. There's layout functionality that has a lot of potential but is missing parameter passing at the moment. But the main value is in the simplicity and total integration.

When I define a dependency on a class and remove it from the code the error appears even in the template file. This doesn't happen in Thymeleaf.

## Final Word

In conclusion, with the Manifold project, we can write fluent text processing code today without waiting for a future JVM enhancement. The introduction of String templates can help Java developers generate files that aren't a web application, which is useful in several cases e.g. where code generation is needed.

Manifold allows us to create JSP-like templates without all of the baggage and generate any arbitrary file we want. With the inclusion of sophisticated options like layout, the sky's the limit.

There's a lot more to Manifold and we will dig deeper into it as we move forward.
