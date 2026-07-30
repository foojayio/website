---
title: "Spring Boot Annotation Reference - 01/a"
slug: "spring-boot-annotation-reference-01-a"
date: "2022-12-22T09:57:33+00:00"
lastmod: "2023-01-06T09:56:40+00:00"
description: "Spring Boot Annotation Guide, providing an easy reference for daily development and provides code samples."
authors:
  - "sumith-puri"
image: "https://foojay.io/wp-content/uploads/2021/11/1280px-Spring_Framework_Logo_2018.svg.png"
categories:
  - "Microservices"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "spring-boot-annotation-reference-01-b"
  - "a-simple-service-with-spring-boot"
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

In 2018, I was introduced to the topic of Microservices and Spring Boot via a formal training, during my brief stint as a Senior Architect in Manila, Philippines.

Though I had worked on a 'similar architecture' way back in 2007-'08 while working as a Software Engineer at Symantec - I found the idea of the Uber JAR really exciting. Also, since it will now be enforced via the most popular framework brings in more possibilities.

This includes building 'executable applications' for windows much easier!

![](https://blogger.googleusercontent.com/img/a/AVvXsEghRn9_AnCnub4H-xJJZ3IDGxtXEzayfXKV-XqD0czmugSGjJGEVYDe1cVOxHKRyK9G5jM8V03LJfxUWL5O0cdKgAB-PjoBOPu-1z6Ctv-uMZqP7ygj0J_mTdDL5CqVLZKGAP2SgG-fwGfqpWgoIMGZJv3hbX5swfCm9Snmp_nA4zui3bdm_3Q09TPtFA)

Anyways, the topic of our discussion is Spring Boot Annotations. Recently, in August 2022 while training a team of 10, I realized that though even though I know most of the Spring Boot Annotations, I may not be aware of all of them. So, I decided to write this article.

I hope it helps the readers to have a quick glance either during their daily work or as a general reference. Since I have been working on Spring Boot, Cloud, Spring Security, Spring Data since the last 4 years, I will later write a \[Part-02\] of this article covering the other annotations as well. It will cover annotations of Spring Security, Spring Data and Spring Cloud.

Herein, I will try to cover the annotations that you may most frequently see in daily development. Some of them you may know vaguely or just seen them in code but not understood completely. This article help you refresh what you already know and also to know more about the ones you had just come across.

**GitHub Repository:** [Spring Boot Annotations (Sample)](https://rebrand.ly/skp-sb-annot-git "Spring Boot Annotations (Sample)")

**@SpringBootApplication**

Well, this might be surprising. @SpringBootApplication is actually a combination of three features or annotations. In other words, it has the effect of three annotations together: @EnableAutoConfiguration, @ComponentScan, @Configuration.

The main class of your Spring Boot Application should be annotated with this annotation, which has a main method.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation;  

 import org.springframework.boot.SpringApplication;  
 import org.springframework.boot.autoconfigure.SpringBootApplication;  
 import org.springframework.context.annotation.Bean;  

 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleImpl;  
 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleInterface;  

 @SpringBootApplication  
 public class SpringBootAnnotationApplication {  

      public static void main(String[] args) {  
           SpringApplication.run(SpringBootAnnotationApplication.class, args);  
      }  

      @Bean  
      public SBASampleInterface getSBAService() {  

           return new SBASampleImpl();  
      }  
 }</pre>

**@EnableAutoConfiguration**

Spring allows for the automatic configuration of the application, by creating and registering the spring beans in the classpath. The @EnableAutoConfiguration allows to define the base search package.

By default, the base package for searching of beans will be the same package as of the class that declares this annotation.

Usually, you will place this annotation on your main class. If you use @SpringBootApplication, you may not need this annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation;  

 import org.springframework.boot.SpringApplication;  
 import org.springframework.boot.autoconfigure.EnableAutoConfiguration;  
 import org.springframework.context.annotation.Bean;  
 import org.springframework.context.annotation.ComponentScan;  
 import org.springframework.context.annotation.Configuration;  

 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleImpl;  
 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleInterface;  

 //@SpringBootApplication  
 @EnableAutoConfiguration  
 @Configuration  
 @ComponentScan(basePackages = "xyz.sumithpuri.spring.boot.annotation")  
 public class SpringBootAnnotationApplication {  

      public static void main(String[] args) {  
           SpringApplication.run(SpringBootAnnotationApplication.class, args);  
      }  

      @Bean  
      public SBASampleInterface getSBAService() {  

           return new SBASampleImpl();  
      }  
 }</pre>

**@SpringBootTest**

This one is straightforward, @SpringBootTest is used to create an application context object that supports testing.

You must annotate your Test Class file with this annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation;  

 import org.junit.jupiter.api.Test;  
 import org.junit.runner.RunWith;  
 import org.springframework.boot.test.context.SpringBootTest;  
 import org.springframework.test.context.junit4.SpringRunner;  

 @RunWith(SpringRunner.class)  
 @SpringBootTest  
 class SpringBootAnnotationApplicationTests {  

      @Test  
      void contextLoads() {  
      }  
 }</pre>

**@SpringBootConfiguration**

Even though I have not used it much in my applications, from what I could gather I have found it is already part of the @SpringBootApplication.

The only difference that exists between @Configuration and @SpringBootConfiguration is that latter allows to automatically locate the configuration.

This will be useful for unit and integration tests.

**@ConditionalOnClass**   

Will match only when the specified classes are in the classpath.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.configuration;  

 import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;  
 import org.springframework.context.annotation.Bean;  
 import org.springframework.context.annotation.Configuration;  

 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleImpl;  
 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleInterface;  

 /**  
  * @author sumith.puri  
  *  
  */  
 @Configuration  
 @ConditionalOnClass(SBASampleImpl.class)  
 public class SBASampleConfiguration {  

      @Bean  
      public SBASampleInterface getSBAService() {  

           return new SBASampleImpl();  
      }  
 }</pre>

With Spring DevTools Enabled, you will see one such log on the console that are the debug statements showing the matches or evaluations against the conditions.

![](https://blogger.googleusercontent.com/img/a/AVvXsEh_YHALIvRTyRrUibkYVbv4T6wajr7lTequVrbA6XybZubtdP61FHgch9J84jsHrPcvXmlTmoo0_Q4CFzo9SRYompQJgGkuZWWHcP3MBtJKfoSI4ByS7-VuG7Jpf2tbMKqHi-fgivNuWH76fWzHjLGsfGp3OYhY5s0JhilC5kDne9a-BJCqIzkuy1k_3A)

**@ConditionalOnProperty**

Will match only when the specified environment property is present and it has a specific value.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import xyz.sumithpuri.spring.boot.annotation.service.SBASampleImpl;
import xyz.sumithpuri.spring.boot.annotation.service.SBASampleInterface;

/**
* @author sumith.puri
*
*/
@Configuration
//@ConditionalOnClass(SBASampleImpl.class)
@ConditionalOnProperty(name="mode", havingValue="false")
public class SBASampleConfiguration {

  @Bean
  public SBASampleInterface getSBAService() {

     return new SBASampleImpl();

  }

}</pre>

Please go ahead and add the property 'mode=false' in your application.properties

If this property is not present or has a different value, your server will refuse to start as there will be not property present to inject for an autowired bean. (Refer to the code in the GitHub Repository).

![](https://blogger.googleusercontent.com/img/a/AVvXsEghRn9_AnCnub4H-xJJZ3IDGxtXEzayfXKV-XqD0czmugSGjJGEVYDe1cVOxHKRyK9G5jM8V03LJfxUWL5O0cdKgAB-PjoBOPu-1z6Ctv-uMZqP7ygj0J_mTdDL5CqVLZKGAP2SgG-fwGfqpWgoIMGZJv3hbX5swfCm9Snmp_nA4zui3bdm_3Q09TPtFA)

**@ConfigurationProperties**   
**@ConfigurationPropertiesScan**

It marks a class as a configuration properties source (mapping it from a properties or yaml file), which can then be used to control and also to validate properties. ConfigurationPropertiesScan can be used to scan locations for property files. The location can be specified as the parameter to the annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

/**
* @author sumith.puri
*
*/
@Component
@ConfigurationProperties(prefix = "proptest")
@ConfigurationPropertiesScan
public class SBASampleConfigurationProperties {

private String name;
private String pass;
private String mail;
private String year;
private long uuid;

public String getName() {
   return name;
}

public void setName(String name) {
   this.name = name;
}

..... // Getter and Setter Methods</pre>

**Typical Properties File to be Read By ConfigurationProperties**

![](https://drive.google.com/uc?export=view&id=1v987XfEFVZndvJGJOvwOC1f7LuWlsq7j)

**Debug Print Messages on Invocation of a Controller Endpoint**

![](https://drive.google.com/uc?export=view&id=1iwbWi0rSKdMpzDxP3Zi33kgYvZq8VfG1)

My next article in this series wil be named as the [Spring Boot-Annotation Reference-01/b](https://foojay.io/today/spring-boot-annotation-reference-01-b/) I will be focussing on REST/Web/MVC and Stereotype Annotations.
