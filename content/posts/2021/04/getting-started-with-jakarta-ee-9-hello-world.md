---
title: "Getting Started with Jakarta EE 9: Hello World"
slug: "getting-started-with-jakarta-ee-9-hello-world"
date: "2021-04-07T08:55:22+00:00"
lastmod: "2021-09-03T08:38:23+00:00"
description: "Regarding backward compatibility, Jakarta EE 9 is historic as namespaces changed (like package names that changed from 'javax' to 'jakarta')."
canonical: "https://blog.payara.fish/getting-started-with-jakarta-ee-9-hello-world"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "https://foojay.io/wp-content/uploads/2021/03/Getting-Started-with-Jakarta-EE.jpg"
categories:
  - "Jakarta EE"
tags:
related_posts:
enlighterjs: true
frozen: false
---

The [release of Jakarta EE 9](https://jakarta.ee/release/9/), at the end of 2020, was in many ways a historic event. The Java Enterprise framework is already 20 years old, having its first release in 1999. It has changed names a few times but the main concepts of the first release can still be found in this new release. During all those years, it has adapted itself to keep it up to date but has always adhered to its main principle of stability and backward compatibility.

Regarding backward compatibility, this release was also historic as the namespaces changed (like package names that changed from 'javax' to 'jakarta'). The change is straightforward, no other changes are introduced between Jakarta EE 8 and EE 9. This to make the migration as easy as possible.

Since older technologies are removed like support for CORBA and other specifications will not evolve anymore like SOAP, another major step is taken into the modernisation of this platform. You can see Jakarta EE 9 as the rebirth of Java Enterprise having their experience of 20 years of successful support for Enterprise applications.

We decided to start a 'Getting Started with Jakarta EE 9' a blog and video series to introduce those who are not familiar with the platform to the basics of Jakarta EE 9. And as you all probably know, the classic 'Hello World' example must start this series!

### Setting Up with Maven {#h3-0-setting-up-with-maven}

You can use the Maven build tools to compile and package your Jakarta EE 9 application. With the Maven Archetypes, you can bootstrap many types of applications. For the moment, there is no specific archetype for Jakarta EE 9 available. You can start from any other maven project and adopt the *pom.xml* file or you can use the quickstart archetype as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart</pre>

These are the important parts of the maven configuration for your Jakarta EE 9 project:

* Define the `jakartaee-web-api` dependency so that your project can use all the frameworks that are part of the Web Profile. For most projects, the Web Profile is sufficient, otherwise, you can use the `jakartaee-api` dependency that corresponds with the Full Profile.
* Use the scope provided as all the required code for Jakarta EE 9 is already available within The Payara Platform products. They don't need to be included in your WAR file which makes the file small and efficient.
* Define the JDK version you want to use for your application. In this case, we use JDK 11 as it is the latest LTS version of Java and the recommended version for any new project you start today. Officially, Jakarta EE 9 is only supported on JDK 8 but it works without any issues on JDK 11 with the Payara Platform products. The configuration parameters to specify the Java version are `maven.compiler.source` and `maven.compiler.target`.
* Define the name of the WAR file that needs to be created by defining it in the `<finalName>` parameter.
* Do not forget to define the packaging type as war so that the Maven tool will generate a Web Archive. You also need to specify the configuration `<failOnMissingWebXml>false</failOnMissingWebXml>` as we do not need a web.xml file in this simple example. The file is optional for Jakarta but without the configuration property, the Maven WAR plugin will complain.

Your *pom.xml* could like look this for the Jakarta EE 9 Hello World example:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;
   &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
   &lt;groupId&gt;fish.payara.jakarta.ee9.start&lt;/groupId&gt;
   &lt;artifactId&gt;hello&lt;/artifactId&gt;
   &lt;version&gt;1.0&lt;/version&gt;
   &lt;packaging&gt;war&lt;/packaging&gt;

   &lt;dependencies&gt;
      &lt;dependency&gt;
         &lt;groupId&gt;jakarta.platform&lt;/groupId&gt;
         &lt;artifactId&gt;jakarta.jakartaee-web-api&lt;/artifactId&gt;
         &lt;version&gt;9.0.0&lt;/version&gt;
         &lt;scope&gt;provided&lt;/scope&gt;
      &lt;/dependency&gt;
   &lt;/dependencies&gt;

   &lt;build&gt;
      &lt;finalName&gt;hello&lt;/finalName&gt;
   &lt;/build&gt;

   &lt;properties&gt;
      &lt;maven.compiler.source&gt;11&lt;/maven.compiler.source&gt;
      &lt;maven.compiler.target&gt;11&lt;/maven.compiler.target&gt;
      &lt;failOnMissingWebXml&gt;false&lt;/failOnMissingWebXml&gt;
      &lt;project.build.sourceEncoding&gt;UTF-8&lt;/project.build.sourceEncoding&gt;
   &lt;/properties&gt;
&lt;/project&gt;</pre>

You are now ready to open the Maven Project in your favorite IDE and start coding your application.

### Setting Up with Gradle {#h3-1-setting-up-with-gradle}

You can also use Gradle as an alternative for Maven as your build tool. It doesn't have many templates available, but you can use the following command to generate a basic Gradle project.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">gradle init --type basic</pre>

It generates the basic files, like a *build.gradle* file containing the main configuration for your Gradle project, and the required wrapper files for Gradle.

This are the most important parts of the configuration that you should define within *build.gradle* file:

* Define the plugins java and war so that Gradle can create the Web Archive containing our application and compile the Java source files.
* Define Maven Central as the place where the dependencies of the project should be searched.
* Define the `jakartaee-web-api` dependency so that your project can use all the frameworks that are part of the Web profile. For most projects, the Web Profile is sufficient, otherwise, you can use the `jakartaee-api` dependency that corresponds with the Full Profile.
* Use providedCompile for the Jakarta EE 9 dependency since the [Payara Platform Products](https://www.payara.fish/) already contain all code. It doesn't need to be included in the WAR file which makes it small and efficient.
* Define the JDK version you want to use for your application. In this case, we use JDK 11 as it is the latest LTS version of Java and the recommended version for any new project you start today. Officially, Jakarta EE 9 is only supported on JDK 8 but it works without any issues on JDK 11 with the [Payara Platform products](https://www.payara.fish/). The parameters to set are `targetCompatibility` and `sourceCompatibility`.

Your *build.gradle* could like look this for the Jakarta EE 9 Hello World example:

<pre class="EnlighterJSRAW" data-enlighter-language="json" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">plugins {
   id 'java'
   id 'war'
}

group = 'fish.payara.jakarta.ee9.start'
version = '1.0'

repositories {
   mavenCentral()
}

dependencies {
   providedCompile 'jakarta.platform:jakarta.jakartaee-web-api:9.0.0'
}

compileJava {
   targetCompatibility = '11'
   sourceCompatibility = '11'
}

war {
   archiveName 'hello.war'
}</pre>

You are now ready to open the Gradle Project in your favoriite IDE and start coding your application.

### Hello Servlet {#h3-2-hello-servlet}

Now that we have a project created, we can develop the Hello World Servlet. Servlets are not much used any more in today's development but are still very important as they form the basis for many frameworks that are built on top of it like REST endpoints and server-side applications like the Jakarta Faces (which was called Java Server Faces in Java EE)

In one of the next Jakarta EE 9 blogs, we will go deeper into the Servlet usage for implementing a File download, but today we will use the typical Hello World demo.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/world")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getOutputStream().println("Hello World");

    }

}</pre>

Some background information on the example:

* The class extends the `HTTPServlet` abstract class so that basic support for the HTTP requests is available. It allows you to implement methods like `doGet()` and `doPost()` to respond to the specific HTTP Methods that are sent to the application.
* The link with the URL on which the Servlet responds is defined by the annotation `@WebServlet`. It defines the URL that the end-user can use to reach your Servlet functionality. The general pattern is *http://localhost:8080/\<root\>/\<@WebServlet-value\>*.
* The `HttpServletRequest` parameter can be used to interact with the information sent by the user.
* `HttpServletRequest.getQueryString()` returns the entire query string appended to the URL.
* `HttpServletRequest.getPatameter("paramName")` returns the vale of the Query parameter *paramName*.
* `HttpServletRequest.getRequestURI()` returns the URL that the user entered. In our example, we have bound a single URL (*/world* ) to the servlet but it is possible to use wildcards so that multiple URLs are handled by the same class (like **@WebServlet**("/test/\*")
* `HttpServletRequest.getSession()` returns the HTTP Session for the request and allows to set values in the session (with the method .`setAttribute()`) so that these values are avaiable the next time the user access your application again (depending on session timeout configuration)
* The `HttpServletResponse` is a class to send information back to the user. In our cases, just the "*Hello World*" text.

### Conclusion {#h3-3-conclusion}

Getting started with Jakarta EE 9 is easy. You just have to define one single dependency for your application that brings in the entire API of the Java Enterprise framework. The API needs to be defined as provided as the Payara Platform products contain all the required code already, which means that the Web Archive file is small and efficient.

We have shown how a Hello World response can be sent back from the server and in the next parts of this beginners Jakarta EE 9 series, we will see how other frameworks like Jakarta Rest and Jakarta Faces are building on top of this Servlet specification.

Visit out Getting Started with Jakarta EE Beginners series on YouTube for video tutorials:

* [Getting Started with Jakarta EE using Maven](https://youtu.be/NwnwSM89MPg)
* [Getting Started with Jakarta EE using Gradle](https://youtu.be/PAvn_jBNVNE)

Originally written by Rudy De Busscher.
