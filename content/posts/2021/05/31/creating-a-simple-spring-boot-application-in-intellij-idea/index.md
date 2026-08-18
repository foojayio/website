---
title: "Creating a Simple Spring Boot Application in IntelliJ IDEA"
slug: "creating-a-simple-spring-boot-application-in-intellij-idea"
date: "2021-05-31T17:06:51+00:00"
lastmod: "2021-09-04T08:48:37+00:00"
description: "Let's use the New Project Wizard in IntelliJ IDEA to create a Spring Boot project with the Spring Web dependency!"
authors:
  - "helenjoscott"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "IntelliJ IDEA"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "avoiding-nullpointerexception"
  - "beginning-javafx-with-intellij"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

This tutorial uses IntelliJ IDEA Ultimate because we want to create a new project using Spring Initializr. This functionality is only available with IntelliJ IDEA Ultimate. It is based off [Building an Application with Spring Boot](https://spring.io/guides/gs/spring-boot/).

## Creating your Spring Boot Project

1) If you're in an IntelliJ IDEA project already, select **File** \> **Project** . Alternatively click the **New Project** button on the Welcome screen.

2) Select **Spring Initializr** on the left-hand side and then enter the following options:

|  Field Name  |                                                                                                            What you should enter                                                                                                            |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name         | Give your project a helpful name such as `HelloWorld`.                                                                                                                                                                                      |
| Location     | This will be the default IntelliJ IDEA location, you can change it if you want to.                                                                                                                                                          |
| Type         | This demo uses the [Maven build system](https://maven.apache.org/what-is-maven.html) so please select that option. If you want to use the [Gradle build system](https://gradle.org/), then please let us know and we'll add it to our list. |
| Language     | This demo uses Java.                                                                                                                                                                                                                        |
| Group        | This forms part of your package name, you can change it if you want to.                                                                                                                                                                     |
| Artifact     | This is the name given to any artifacts produced, it will be derived from the Name you used so you don't need to change it.                                                                                                                 |
| Package Name | This is a concatenation of your Group and Name so you don't need to change it.                                                                                                                                                              |
| Project SDK  | This is the version of Java SDK that your project will be compiled against. This demo uses Java 11.                                                                                                                                         |
| Java         | This is the Language Level for your project which determines coding assistance provided in the editor. Again this demo using Java 11.                                                                                                       |
| Packaging    | We will use JAR for this demo because we're not going to run it on an application server. To do the latter you would need to select WAR                                                                                                     |

Your page should look similar to this:

![New Spring Project Details](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/spring_initializer.png)

Click **Next**.

3) The next page is where you can configure your Spring Initializr project and add the required dependencies. We're going to keep it really simple for this project as we just want to serve the string "Hello World" locally in our browser. Let's start typing in *Web* and see what options we get. This will show you all dependencies that relate to *Web* development, we want the first one, **Spring Web** . Alternatively, you can expand the Web node (second from the top) and select **Spring Web**. That's all you need to do here.

One thing to note here is that it's really easy to add dependencies to your Spring project once you have created it. You should only ever add what you know you'll need at this step. It's easier to add dependencies at a later date than it is to remove dependencies that you may, or may not, be relying on at a future date!

Your page should look similar to this, depending on how you searched for **Spring Web**:

![New Spring Project Dependencies](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/new-spring-project-dependencies.png)

Click **Finish**.

That's all we have to do to get our basic Spring project set up in IntelliJ IDEA. In the next step we'll take a look at what has been automatically created for you, and most importantly, why.

## Project Walk-through

In the previous step we asked IntelliJ IDEA to create us a Spring Boot project with **Spring Web** as a dependency. Let's now take a look at what has been automatically created, so that we can build on that understanding in the next step.

Let's take a look around some aspects of the project. The easiest way to do this is from the Project tool window which you can access with **⌘1** on macOS, or **Ctrl** +**1** on Windows and Linux. We will look at:

* The .mvn Folder
* The pom.xml File
* HelloWorldApplication.java
* HelloWorldApplicationTests.java

### The .mvn Folder

This folder has been created because Spring Boot uses the Maven wrapper when you create a Spring Boot project with the Maven build system. This means you don't have to install Maven locally to run your Spring Boot project. You should commit this project to version control, but you can ignore it from now on.

### The Maven pom.xml File

This file is generated with the dependencies that you selected when we created this project.

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

The first dependency on `spring-boot-starter-web````````` is there because we selected **Spring Web** as a dependency. The second dependency on `````````spring-boot-starter-test` is something you get with any Spring application. It gives you the ability to create tests with various testing libraries.

It's very easy to add dependencies to your `pom.xml` file once you have created your Project. Use **⌘N** (macOS), or **Alt+Ins** (Windows/Linux) and then browse for your dependency. However, for our demo, these are the only dependencies that we need.

### The SpringHelloWorldDemoApplication.java File

Inside your **main** \> **java** \> **com.example.helloworld** file structure you'll see your `HelloWorldApplication.java` file. Let's take a look in more detail.

This is what your Java file will look like. The name will be whatever your called the file with *Application* appended to it.

```java
package com.example.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorld.class, args);
    }
}
```

We've got our package at the top of the class as you'd expect followed by our import statements.

This `@SpringBootApplication` annotation enables additional Spring Boot functionality which is useful to know in case you find yourself troubleshooting your code.

The main line here is:

```java
SpringApplication.run(HelloWorldApplication.class, args);
```

This makes a call to SpringBoot's `run` method, and we need to pass the main class of our project to Spring, in this case, it's the same class.

When you run this method, Spring looks at what Maven has pulled in to the class path from the dependencies in our pom.xml file among other things, and makes assumptions about the shape of your project from there. There are a bunch of *transitive* dependencies that Maven can pull in based on what Spring finds on your class path and the `application.properties` file. Transitive dependencies are dependencies that your dependencies are reliant on.

For example, we have a dependency in our Maven pom.xml called `spring_boot_starter_web`. That in turn, has a transitive dependency on `spring-boot-starter-tomcat`. In this instance, when the Spring `run` method is called, it checks the class path and your `application.properties` (which is empty) among other things, and sees that you want a Tomcat webserver, so it creates one for you.

### The SpringHelloWorldDemoApplicationTests.java File

If you head down to the **test** \> **java** \> **com.example.springhelloworlddemo** folder you'll see you have another class called `HelloWorldApplicationTests.java`\`\`. This is a test you get for free with Spring Boot. It checks if the Application Context can start, it will fail if not. The test can be a useful starting point for creating your own integration tests.

### Running our Spring Boot Application

We have the basics of our Spring Boot Application at this point. You can run it with **Ctrl** +**R** (macOS), or **Shift** +**F10** (Windows/Linux). Alternatively you can use the gutter icons:

![Gutter icon to run the application](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/gutter-icon-run-application.png)

The application will run using the Tomcat webserver. You can verify it's working by going to your web browser and typing `localhost:8080`. Port 8080 is the default port for Tomcat.

You should get a 404 response which will look similar to this:

![White label 404 response](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/white-label-404-response.png)

We're getting this page because we have support for REST Controllers with the `spring-boot-starter-web` dependency, but we also need to create a controller and add a request mapping for that controller. We will do both of those things in the next step.

Lastly, before we do any more development it's a good idea to stop your server from running. You can do this with **⌘F2** (macOS), or **Ctrl** +**F2** on Windows and Linux.

## Create your Spring Controller

Now we have our functioning Spring project we need to [create a Spring Controller](https://spring.io/guides/gs/serving-web-content/#initial) to handle the web requests.

### Create our Controller

One important thing to note here is that you don't need to tell your Spring Application class about your (new) Spring Controller class. That is handled by the `@SpringBootApplication` annotation in the Application class which also consists of other annotations, including `@ComponentScan`. This means that the current package, and sub packages will be scanned for Spring components. It's a little unnerving when you first start using Spring but in time you'll get used to it once you have an appreciation of what Spring is doing behind the scenes for you.

1) Create a new Java class in the same place as your `HelloWorldApplication.java` class called `HelloWorldController.java`.

2) The first thing we need to do is tell Spring that this is a REST Controller, so you need to add a class level annotation of `@RestController`. This annotation means this class will be picked up by the component scan, because it's in the same package as our Application class. Our REST Controller, `HelloWorldController`, will therefore be available from the application context.

3) The next step is to create a method that will tell Spring that if we go the root of our webserver, we would like to see the string *Hello World from Spring Boot* . To do that we need to add a method with a `@RequestMapping` annotation like our `helloWorld` one here:

```java
package com.example.helloworld;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

  @RequestMapping("/")
  public String helloWorld(){
    return "Hello World from Spring Boot";
  }
}
```

4) Now we need to re-run our Spring application. You might need to stop it first if it's still running from the previous step. You can run it again with **Ctrl** +**R** (macOS), or **Shift** +**F10** (Windows/Linux).

5) When you go to the browser, enter the following URL `localhost:8080`. You should see your text being returned:

![Hello World being displayed in the browser](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/hello-world-text.png)

6) Assuming that's working correctly, you can start to get more adventurous. Try adding this new code below your first method:

```java
@RequestMapping("/goodbye")
public String helloWorld(){
  return "Goodbye from Spring Boot";
}
```

7) Now run your application again. At the root you should still see *Hello World from Spring Boot* because the `MARKDOWN_HASHfe7b83329725956f76335a5a0b65595eMARKDOWN`*HASH* *is* /_ indicating root. However, if you now type in `localhost:8080/goodbye`, you should see *Goodbye from Spring Boot*.

That's it! You're done, congratulations on creating your first Spring Application and serving some text in the browser! In the next section we'll create a test for our application.

## Creating a Test for your Spring Boot Application

Let's write a test to check that in the event of an HTTP request, we get the response that we are expecting, in this case the string *Greetings from Spring Boot*.

Tests for Spring Boot are written using the standard [JUnit5 Testing Library](https://junit.org/junit5/docs/current/user-guide/). If a test is going to need access to Spring's application context, it needs to be annotated with `@SpringBootTest`. These are effectively integration tests, as they rely on bootstrapping the Spring context before you can run the test.

1) We need to create a new Test class in the same place as our `HelloWorldApplicationTest.java`, we will call ours `CheckHTTPResponse.java`.  

2) Paste the following code into your test class:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CheckHTTPResponse {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldPassIfStringMatches() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/",
                String.class)).contains("Hello World from Spring Boot");
    }
}
```

Passing `WebEnvironment.RANDOM_PORT` into the `@SpringBootTest` annotation starts the web server with a random port number. We can find out what that port number is by annotating an `int` field (in our case, `int port;`) with `@LocalServerPort`, the testing framework will inject this field with the random port number.

We get a `TestRestTemplate` for free with tests annotated with `@SpringBootTest`. The `@Autowired` annotation tells Spring that we want to get it from the context.

Our test itself is annotated with `@Test`. It will pass when our expected value is the same as the actual value from the webserver - our assert statement is comparing the string we are serving from localhost at this random port, with the string in the second half of the assert statement - *Hello world from Spring Boot*.

3) Let's run the test to check it works. You can run it again with **Ctrl** +**R** (macOS), or **Shift** +**F10** (Windows/Linux). We should see that our test is green.

![Passing HTTP test](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/passing-http-test.png)

It's always worth checking our assumptions, so try changing the text to something other than *Hello World from Spring Boot* and check that the test fails. It should do!

In the next step we will summarise what we've done up to this point and list the resources we've used.

## Summary and Resources

In this tutorial we used the [New Project Wizard](https://www.jetbrains.com/help/idea/new-project-wizard.html) in IntelliJ IDEA to create a Spring Boot project with the **Spring Web** dependency.

We created a Spring Controller and served some text to the local Tomcat webserver. Finally, we added a test for our HTTP call.

### Resources

* I referenced [this guide](https://spring.io/guides/gs/spring-boot/) to create this tutorial.
* The [New Project Wizard](https://www.jetbrains.com/help/idea/new-project-wizard.html) in IntelliJ IDEA Ultimate uses [Spring Initializr](https://start.spring.io/) to create the project behind the scenes.
* There are lots of [guides](https://spring.io/guides) available on the Spring website that you can also have a play with. I recommend you work through more of them, especially if you're interested in Spring development and learning more.
* You can [read more about Spring Boot](https://spring.io/projects/spring-boot), and the project is also [available on GitHub](https://github.com/spring-projects/spring-boot).
* Finally, there are lots of [excellent blogs](https://www.marcobehler.com/guides) from [Marco Behler](https://twitter.com/MarcoBehler) that you can explore (all under the *Spring* header).
