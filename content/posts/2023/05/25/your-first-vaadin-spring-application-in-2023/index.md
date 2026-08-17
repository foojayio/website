---
title: "Your First Vaadin Spring Application in 2023"
slug: "your-first-vaadin-spring-application-in-2023"
date: "2023-05-25T12:08:30+00:00"
lastmod: "2023-05-25T12:08:31+00:00"
description: "Create new Vaadin Java web application projects using start.spring.io. Configure the dependencies and choose your Maven or Gradle."
authors:
  - "sami-ekblad"
image: "reindeer-lines-474x510-1.png"
categories:
  - "Cloud"
  - "Vaadin"
tags:
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
enlighterjs: true
frozen: false
---

Spring pun intended, but I wanted to update the guide for beginners interested in combining these two technologies.

By following these steps, you'll have a basic Vaadin-Spring Boot application up and running in no time.

Recently I noticed that it is 8 years since Vaadin was first released in [start.spring.io](https://start.spring.io/ "start.spring.io"). Since then, there have been many additions to both Spring boot and Vaadin.

Recent upgrade to[Java 17 and Jakarta EE 9](https://spring.io/blog/2021/09/02/a-java-17-and-jakarta-ee-9-baseline-for-spring-framework-6 " Java 17 and Jakarta EE 9") baseline. Vaadin 24 added a new [pre-compiled frontend mod](https://vaadin.com/docs/latest/configuration/development-mode#pre-compiled-front-end-bundle-for-faster-start-up "pre-compiled frontend mod")e making the initial startup matter of seconds (native compilation using GraalVM would make that even faster). And many many more. Time to grow a new project.  
![](plant-lines2-691x510.png)

Step 1: Set Up Project {#h2-0-step-1-set-up-project}
----------------------------------------------------

The Spring Initializr will help us create a new Spring Boot application with the desired dependencies. Think of it as a `pom.xml` configurator.

1. Open [start.spring.io](https://start.spring.io/ "start.spring.io") If this this your first time here, you can use the following setting to configure your project:
   * Project: Maven
   * Spring Boot: 3.0.6
   * Click on "Add Dependencies" and search for "Vaadin".
   * Add any other dependencies you might want for your project, like "Spring Data JPA", "Spring Security", or "JOOQ Access Layer".
   * Enter the Artifact and Group you want for your project.
   * Write a brief description for your project.
2. Click "Generate" to download the configured project.
3. Unzip the package and import the folder into your Integrated Development Environment (IDE) as a Maven project.

Now you have now created an empty project and are ready to start developing the User Interface (UI) and functionality.

Step 2: Create a Simple HelloWorld Application {#h2-1-step-2-create-a-simple-helloworld-application}
----------------------------------------------------------------------------------------------------

Let's start with a simple HelloWorld application to make sure everything is working fine.

Here's a small HelloWorld.java you can use to test:

```
package org.vaadin.example.springapp;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("/")
public class HelloWorld extends Div {

    public HelloWorld() {
        add(new Button("Click to say hello", this::sayHello));
    }

    public void sayHello(ClickEvent<Button> e) {
        Notification.show("Hello stranger");
    }
}
```


To start the server, just open the context menu on the SpringAppApplication.java file and click "Run Java". This will start the embedded web server and Vaadin in development mode.

Now that your server is running, head to <http://localhost:8080/> to access your newly created Vaadin application.

Bonus: quick links to typical configurations {#h2-2-bonus-quick-links-to-typical-configurations}
------------------------------------------------------------------------------------------------

The sample application above didn't do too much, and you are likely to want to add something more. You can do this incrementally, but here are some typical setups:

* [Vaadin with JPA and HSQLDB](https://start.spring.io/#!type=maven-project&amp;language=java&amp;platformVersion=3.0.6&amp;packaging=jar&amp;jvmVersion=17&amp;groupId=org.vaadin.example&amp;artifactId=springapp&amp;name=springapp&amp;description=Demo%20project%20for%20Vaadin%20on%20Spring%20Boot&amp;packageName=org.vaadin.example.springapp&amp;dependencies=vaadin,data-jpa,hsql). This is an perfect setup for simple DB applications.
* [Vaadin with JPA and MySQL](https://start.spring.io/#!type=maven-project&amp;language=java&amp;platformVersion=3.0.6&amp;packaging=jar&amp;jvmVersion=17&amp;groupId=org.vaadin.example&amp;artifactId=springapp&amp;name=springapp&amp;description=Demo%20project%20for%20Vaadin%20on%20Spring%20Boot&amp;packageName=org.vaadin.example.spring-app&amp;dependencies=vaadin,data-jpa,mysql). Perfect setup for more serious DB apps and [really easy CRUD UI](https://vaadin.com/directory/component/crud-ui-add-on "really easy CRUD UI").
* [Vaadin with JOOQ](https://start.spring.io/#!type=maven-project&amp;language=java&amp;platformVersion=3.0.6&amp;packaging=jar&amp;jvmVersion=17&amp;groupId=org.vaadin.example&amp;artifactId=springapp&amp;name=springapp&amp;description=Demo%20project%20for%20Vaadin%20on%20Spring%20Boot&amp;packageName=org.vaadin.example.spring-app&amp;dependencies=vaadin,jooq). Make sure to add also the[jOOQ for Vaadin add-on](https://vaadin.com/directory/component/jooq-for-vaadin " jOOQ for Vaadin add-on") to get most out of it.

That's a-may-zing! You have just set up and run your first Vaadin application with Spring Boot. Enjoy exploring more features and functionalities of Vaadin and Spring Boot.  
![](reindeer-lines-474x510.png)
