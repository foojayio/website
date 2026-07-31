---
title: "How to Deploy a Vaadin Application as a WAR on Tomcat 11"
slug: "how-to-deploy-a-vaadin-application-as-a-war-on-tomcat-11"
date: "2025-05-19T07:40:43+00:00"
lastmod: "2025-05-19T07:40:44+00:00"
description: "If you want to run a Vaadin application on an external servlet container like Apache Tomcat 11, you need to package your application as a WAR."
authors:
  - "simon-martinelli"
image: "vaadinlogo.png"
categories:
  - "Apache Tomcat"
  - "Spring"
  - "Tutorials"
  - "Vaadin"
tags:
related_posts:
  - "browserless-testing-of-vaadin-applications-with-karibu-testing"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
enlighterjs: true
frozen: false
---

**If you want to run a Vaadin application on an external servlet container like Apache Tomcat 11, you need to package your application as a WAR (Web Application Archive) instead of the usual executable JAR.**

In this post, I'll show you step-by-step how I did it.

Step 1: Download Tomcat 11 {#h2-0-step-1-download-tomcat-11}
------------------------------------------------------------

First, download Tomcat 11 from the [official Apache Tomcat website](https://tomcat.apache.org/download-11.cgi). After extracting it to a local folder, you have a clean Tomcat installation ready to use.

Step 2: Create a New Vaadin Project {#h2-1-step-2-create-a-new-vaadin-project}
------------------------------------------------------------------------------

Next, create a new Vaadin project. You can easily do this by either using [https://start.vaadin.com](https://start.vaadin.com/) or [https://start.spring.io](https://start.spring.io/).

By default, the project is configured to create a JAR file and run using the embedded Spring Boot server.

Step 3: Adjust the `pom.xml` {#h2-2-step-3-adjust-the-pom-xml}
--------------------------------------------------------------

To prepare the project for deployment to Tomcat, I changed the packaging from `jar` to `war` in the `pom.xml`. This tells Maven to build a WAR file instead of a standalone JAR.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;packaging&gt;war&lt;/packaging&gt;</pre>

You also want to exclude the embedded Tomcat because we will deploy the WAR to Tomcat. This can be done by marking the dependency as provided.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-starter-tomcat&lt;/artifactId&gt;
    &lt;scope&gt;provided&lt;/scope&gt;
&lt;/dependency&gt;</pre>

Step 4: Update the Spring Boot Application Class {#h2-3-step-4-update-the-spring-boot-application-class}
--------------------------------------------------------------------------------------------------------

A WAR needs a special entry point for the servlet container.

I modified the `@SpringBootApplication` class to extend `SpringBootServletInitializer` and override the `configure` method:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootApplication
public class VaadinWarApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VaadinWarApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(VaadinWarApplication.class, args);
    }
}</pre>

This setup makes sure that Tomcat can start the Spring Boot application correctly when the WAR is deployed.

Step 5: Build the Application for Production {#h2-4-step-5-build-the-application-for-production}
------------------------------------------------------------------------------------------------

Vaadin applications must be built in production mode to create an optimized production-ready bundle. I used Maven to build the project with the production profile:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./mvnw package -Pproduction</pre>

This command generates a WAR file in the `target` directory.

Step 6: Deploy the WAR to Tomcat {#h2-5-step-6-deploy-the-war-to-tomcat}
------------------------------------------------------------------------

Copie the generated `.war` file into the `webapps` folder of my Tomcat installation.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">cp target/vaadin-war-application.war /path/to/tomcat/webapps/</pre>

When starting Tomcat (with `bin/startup.sh` or `bin/startup.bat`), it automatically unpacked the WAR and started the application.

The Vaadin application is up and running without any additional configuration!

Conclusion {#h2-6-conclusion}
-----------------------------

Running a Vaadin application as a WAR in Tomcat 11 is straightforward:

* Set the packaging to `war`
* Exclude the embedded Tomcat dependency
* Extend `SpringBootServletInitializer`
* Build for production
* Deploy to Tomcat's `webapps` folder

This way, you can use Tomcat (or any other servlet container) as your runtime environment, which can be helpful if you are working in environments where you cannot use the embedded Spring Boot server.

The source code can be found on GitHub: <https://github.com/simasch/vaadin-war>
