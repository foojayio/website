---
title: "Getting Started with a Simple Service with Spring Boot"
date: "2020-09-22T12:59:42+00:00"
lastmod: "2023-08-28T12:43:57+00:00"
description: "How to create a simple Web Service using Spring Boot, making it effortless to develop Web Services, so long as the appropriate dependencies are in place."
authors:
  - "josh-juneau"
image: "Favicon-3-2.png"
categories:
  - "Java Beginner"
  - "Spring"
  - "Tutorials"
related_posts:
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "baeldung-series-part-2-build-a-dashboard-with-cassandra-astra-and-cql-mapping-event-data"
  - "building-microservices-spring-boot-fat-uber-jar"
frozen: false
---

In this post, I will demonstrate how to create a simple Web Service using Spring Boot. This framework makes it almost effortless to develop web services, so long as the appropriate dependencies are in place.

In this example, I will create a Web Service that will read the current temperature from a file and make it available to clients via a RESTful endpoint.

#### 1. Use the Spring Intializr

The Spring Initializr helps one to choose the dependencies that are required for production a particular solution.

The Initializr can be found at:   

<https://start.spring.io/>

The first thing to note when creating a project using the Initializr is that you can develop Spring Boot applications using a number of different JVM languages, including Java, Kotlin, or Groovy. This is one area where Spring Boot differs from Jakarta EE, which is primarily focused on the Java language. You also have the option to choose either Gradle or Maven for their Spring Boot project.

Perhaps one of the most tedious parts of using the Initialzr to create a project is choosing the appropriate dependencies for the project. The reason being that there are so many dependency options, it can take a while to learn which are the most useful for a particular solution. This is not a bad thing... it is just something that needs to be learned over time.

To get started, choose the following options within the Initializr:

* Project: Maven
* Language: Java
* Spring Boot: 2.3.3
* Dependencies: Spring Web
* Project Metadata
  * Group: org.demo
  * Artifact: poolservice
  * Name: poolservice
  * Package Name: org.demo.poolservice
  * Packaging: WAR
  * Java: 11

Once these options are chosen, click "Generate" to download the project files.

#### 2. Explore the Generated Code

Once downloaded, open the project in your favorite IDE. In this case, I will be using Apache NetBeans 12.

The project can now be built and deployed to a container, such as Payara server. The project comes configured and ready to begin adding RESTful services.

The following code shows the generated PoolserviceApplication class, which was created by the Initializr, contains the @SpringBootApplicationannotation.

```java
package org.demo.poolservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PoolserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoolserviceApplication.class, args);
    }

}
```

The @SpringBootApplication annotation is a shortcut annotation that combines the default functionality of the following three annotations:

* @EnableAutoConfiguration: enables Spring Boot auto-configuration
* @ComponentScan: enables component scanning on the package where application is loaded
* @Configuration: allows registration of extra beans in the context or the ability to import additional configuration classes

#### 3. Create a RESTful Service

Since the application is configured by default, generate a new RESTful service by creating a class named HelloController in the same package and placing the following code into it:

```
package org.demo.poolservice;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot This is the main service!";
    }

}
```

The @RestController annotation wires the class up as a RESTful web service controller by combining the traditional @Controller and @ResponseBodyannotation functionality.

In this example, I am using the request root to serve the response, as the @RequestMapping annotation indicates.

Once this controller class has been added and the code has been compiled and redeployed, the URL http://localhost:8080/poolservice can be visited to display the message: "Greetings from Spring Boot This is the main service!".

#### 4. Create a Temperature Reader

It is time to add the functionality for reading the current temperature from a text file (written by a Raspberry Pi) by creating a new class named TemperatureReader.   

This class can be made a contextual bean by annotating it with @Component and specifying a name by which the bean will be referenced. In this case, "temperatureReader".   

The functionality of the class is very simple, as it reads temperatures from a file that are in the format: (23.5, 78.3), and makes them accessible via *currentTemperatureC* and *currentTemperatureF*, respectively.

```java
package org.demo.poolservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;

@Component("temperatureReader")
public class TemperatureReader {

    private String currentTemperatureF;

    private String currentTemperatureC;

    protected String readTemperatureFile() {
        String temperatureFile = "/<>/temperature.txt";
        System.out.println("Temperature File: " + temperatureFile);
        java.nio.file.Path path = Paths.get(temperatureFile);
        String currentTemperature = null;
        try (BufferedReader reader = 
           Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                //while there is content on the current line
                currentTemperature = currentLine;
            }
        } catch (IOException ex) {
            ex.printStackTrace(); //handle an exception here
        }
        return currentTemperature;
    }

    /**
     * @return the currentTemperatureF
     */
    public String getCurrentTemperatureF() {
        String temp = readTemperatureFile();
        currentTemperatureF = temp.substring(temp.indexOf(",") + 1, temp.lastIndexOf(")"));
        return currentTemperatureF;
    }

    /**
     * @param currentTemperatureF the currentTemperatureF to set
     */
    public void setCurrentTemperatureF(String currentTemperatureF) {
        this.currentTemperatureF = currentTemperatureF;
    }

    /**
     * @return the currentTemperatureC
     */
    public String getCurrentTemperatureC() {
        String temp = readTemperatureFile();
        currentTemperatureC = temp.substring(temp.indexOf("(") + 1, temp.lastIndexOf(","));
        return currentTemperatureC;
    }

    /**
     * @param currentTemperatureC the currentTemperatureC to set
     */
    public void setCurrentTemperatureC(String currentTemperatureC) {
        this.currentTemperatureC = currentTemperatureC;
    }

}
```

#### 5. Create a Temperature Controller

Finally, to make the temperature readings available via a RESTFul service, create a controller named TemperatureController and annotate it with @RestController.   

Inject the TemperatureReader by annotating a private TemperatureReader field with @Autowired. The bean can then be used to obtain the temperature via the use of the TemperatureReader field.   

The URL mapping for the service is supplied via the @RequestMapping("/temperature") annotation on the index() method, which will be used to serve up the temperature at the respective request mapping.   

The functionality contained within the index() method is very minimal, as it merely calls upon the temperatureReader.getCurrentTemperatureF() method to return the current temperature in Fahrenheit.

```java
package org.demo.poolservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {

    @Autowired
    private TemperatureReader temperatureReader;

    @RequestMapping("/temperature")
    public String index() {
            return temperatureReader.getCurrentTemperatureF();
    }

}
```

To see the temperature, visit the URL:   

http://localhost:8080/poolservice/temperature

See the sources at: [https://github.com/juneau001/poolServiceSpringBoot](https://www.blogger.com/blog/post/edit/25636372/7521245366027905053#)

**Note:** Used with permission and thanks — [originally written and published on Josh's Dev Blog](http://jj-blogger.blogspot.com/2020/09/developing-simple-service-with-spring.html).
