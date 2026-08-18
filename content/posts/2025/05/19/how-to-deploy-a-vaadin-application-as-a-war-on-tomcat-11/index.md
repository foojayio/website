---
title: "How to Deploy a Vaadin Application as a WAR on Tomcat 11"
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
frozen: false
---

**If you want to run a Vaadin application on an external servlet container like Apache Tomcat 11, you need to package your application as a WAR (Web Application Archive) instead of the usual executable JAR.**

In this post, I'll show you step-by-step how I did it.

## Step 1: Download Tomcat 11

First, download Tomcat 11 from the [official Apache Tomcat website](https://tomcat.apache.org/download-11.cgi). After extracting it to a local folder, you have a clean Tomcat installation ready to use.

## Step 2: Create a New Vaadin Project

Next, create a new Vaadin project. You can easily do this by either using [https://start.vaadin.com](https://start.vaadin.com/) or [https://start.spring.io](https://start.spring.io/).

By default, the project is configured to create a JAR file and run using the embedded Spring Boot server.

## Step 3: Adjust the `pom.xml`

To prepare the project for deployment to Tomcat, I changed the packaging from `jar` to `war` in the `pom.xml`. This tells Maven to build a WAR file instead of a standalone JAR.

```xml
<packaging>war</packaging>
```

You also want to exclude the embedded Tomcat because we will deploy the WAR to Tomcat. This can be done by marking the dependency as provided.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

## Step 4: Update the Spring Boot Application Class

A WAR needs a special entry point for the servlet container.

I modified the `@SpringBootApplication` class to extend `SpringBootServletInitializer` and override the `configure` method:

```java
@SpringBootApplication
public class VaadinWarApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VaadinWarApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(VaadinWarApplication.class, args);
    }
}
```

This setup makes sure that Tomcat can start the Spring Boot application correctly when the WAR is deployed.

## Step 5: Build the Application for Production

Vaadin applications must be built in production mode to create an optimized production-ready bundle. I used Maven to build the project with the production profile:

```bash
./mvnw package -Pproduction
```

This command generates a WAR file in the `target` directory.

## Step 6: Deploy the WAR to Tomcat

Copie the generated `.war` file into the `webapps` folder of my Tomcat installation.

```bash
cp target/vaadin-war-application.war /path/to/tomcat/webapps/
```

When starting Tomcat (with `bin/startup.sh` or `bin/startup.bat`), it automatically unpacked the WAR and started the application.

The Vaadin application is up and running without any additional configuration!

## Conclusion

Running a Vaadin application as a WAR in Tomcat 11 is straightforward:

* Set the packaging to `war`
* Exclude the embedded Tomcat dependency
* Extend `SpringBootServletInitializer`
* Build for production
* Deploy to Tomcat's `webapps` folder

This way, you can use Tomcat (or any other servlet container) as your runtime environment, which can be helpful if you are working in environments where you cannot use the embedded Spring Boot server.

The source code can be found on GitHub: <https://github.com/simasch/vaadin-war>
