---
title: "Spring Boot Annotation Reference-01/b"
slug: "spring-boot-annotation-reference-01-b"
date: "2023-01-06T09:54:05+00:00"
lastmod: "2023-01-06T09:55:03+00:00"
description: "In part #2 of my article series on Spring Boot Annotations, we focus on the REST and Spring Stereotype Annotations."
authors:
  - "sumith-puri"
image: "https://foojay.io/wp-content/uploads/2022/12/skp_spring_boot_annotations_002.png"
categories:
  - "Microservices"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**In the second part of my article series on Spring Boot Annotations, we focus on REST and Spring Stereotype Annotations.**

In continuation of [part 01/a of this article](https://foojay.io/today/spring-boot-annotation-reference-01-a/), let's continue our Spring Boot Annotations journey.

What percentage (depth and extent) of Spring Boot Annotations do you think you really know? This includes annotations in all their glory and the power that they bring via all of their options. I am sure whatever your answer may be, you will appreciate this quote from Albert Einstein: "The more I learn, the more I realize how much I don't know."

### **Spring-Stereotype** {#h3-0-spring-stereotype}

**@Service**

Service Layer usually holds the core business logic of an application. In Spring, we denote the interface or class that holds the business logic with this annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.service;  

import java.util.HashSet;  

import org.springframework.stereotype.Service;  

import xyz.sumithpuri.spring.boot.annotation.model.Book;  

/**  
 * @author Sumith Puri  
 *  
 */  
@Service  
public class BookServiceImpl implements BookService {  

     HashSet&lt;Book&gt; bookList = new HashSet&lt;Book&gt;();  

     @Override  
     public HashSet&lt;Book&gt; findAllBook() {  
          if (bookList.isEmpty())  
               return null;  
          else  
               return bookList;  
     }  

     @Override  
     public Book findBookByID(long id) {  
          Book book = bookList.stream().filter(b -&gt; b.getId() == id).findAny().orElse(null);  
          return book;  
     }  
    ....</pre>

**@Controller
@RestController**

@Controller\>@Controller is a specialized component that is primarily used in the web layer. It is typically used in combination with annotated handler methods based on the RequestMapping annotation.

@RestController is annotated with @Controller and is used for web layer request handling. Types that carry the @RestController annotation are treated as controllers where @RequestMapping methods assume @ResponseBody semantics by default.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">import java.util.HashSet;  

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.bind.annotation.DeleteMapping;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.PathVariable;  
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestBody;  
import org.springframework.web.bind.annotation.RestController;  

import xyz.sumithpuri.spring.boot.annotation.configuration.SBASampleConfigurationProperties;  
import xyz.sumithpuri.spring.boot.annotation.model.Book;  
import xyz.sumithpuri.spring.boot.annotation.service.BookServiceImpl;  

/**  
 * @author Sumith Puri  
 *  
 */  
@RestController  
public class SBASampleController {  

     @Autowired  
     BookServiceImpl bookServiceImpl;  

     @Autowired  
     SBASampleConfigurationProperties sbasConfigProps;  

     @GetMapping("/findall")  
     public HashSet&lt;Book&gt; getAllBook() {  
          return bookServiceImpl.findAllBook();  
     }  

     @GetMapping("/findbyid/{id}")  
     public Book geBookById(@PathVariable long id) {  
          return bookServiceImpl.findBookByID(id);  
     }  
    ...</pre>

**@Component**

@Component is used to create any Spring managed component. It can be used as a Spring Bean. Any bean with @Bean that is created within a component will have a 'Prototype' scope, as opposed to a 'Singleton' scope of beans that is created within a @Configuration annotated class. @Repository and @Controller are all specialized components.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.component;  

 import javax.annotation.PostConstruct;  

 import org.springframework.beans.factory.annotation.Autowired;  
 import org.springframework.stereotype.Component;  

 import xyz.sumithpuri.spring.boot.annotation.service.SBASampleInterface;  

 /**  
  * @author sumith.puri  
  *  
  */  
 @Component  
 public class SpringBootAnnotationComponent {  

      @Autowired  
      private SBASampleInterface sbaSampleInterfaceImpl;  

      @PostConstruct  
      private void postConstruct() {  
           System.out.println("Testing @SpringBootApplication, @Component and @PostConstruct");  
      }  

 }</pre>

**@Repository**

@Repository is a specialized @Component that is used to mark a class that provides persistence or storage operations. It will provide operations like create, update, retrieve, delete and search type of operations. It is mostly used in conjunction with RDBMS or any other Database.

### **Spring - REST/Web/MVC** {#h3-1-spring-rest-web-mvc}

**@RequestMapping**

This annotation is from MVC/Web that will associate a given URI with a method in the controller. It can be used in the following format.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.PATCH)</pre>

**@GetMapping**

This annotation is used to map a HTTP GET request to a specific handler method in the controller. It is equivalent to the following alternative.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.GET)

</pre>

**@PostMapping**

This annotation is used to map a HTTP POST request to a specific handler method in the controller. It is equivalent to the following alternative.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.POST)

</pre>

**@DeleteMapping**

This annotation is used to map a HTTP DELETE request to a specific handler method in the controller. It is equivalent to the following alternative.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.DELETE)</pre>

**@PutMapping**

This annotation is used to map a HTTP PUT request to a specific handler method in the controller. It is equivalent to the following alternative.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.PUT)<code> </code></pre>

**@PatchMapping**

This annotation is used to map a HTTP PATCH request to a specific handler method in the controller. It is equivalent to the following alternative.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">@RequestMapping(method = RequestMethod.PATCH)</pre>

**@RequestBody**   

This annotation is used to bind a method parameter/object to incoming request parameters.

**@ResponseBody**   

This is used inside a controller and signifies that the returned object will be automatically serialized and passed back into the HttpResponse object. Note that if you are using @RestController you may not need to use this as automatically it is a combination of @Controller and @ResponseBody.

**@RequestParam**   

This is used to bind a method parameter directly to a request attribute.

**@RequestHeader**   

This is used to bind a method parameter directly to a request header.

**@RequestAttribute**   

This can be used to bind a method parameter to a request attribute that was added from an intermediary layer like filter or interceptor.

**@PathVariable**   

This is used to bind a method parameter from a request template URI. Note that It can be used to bind multiple method parameters.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package xyz.sumithpuri.spring.boot.annotation.controller;  

 import java.util.HashSet;  

 import org.springframework.beans.factory.annotation.Autowired;  
 import org.springframework.web.bind.annotation.DeleteMapping;  
 import org.springframework.web.bind.annotation.GetMapping;  
 import org.springframework.web.bind.annotation.PathVariable;  
 import org.springframework.web.bind.annotation.PostMapping;  
 import org.springframework.web.bind.annotation.RequestBody;  
 import org.springframework.web.bind.annotation.RestController;  

 import xyz.sumithpuri.spring.boot.annotation.configuration.SBASampleConfigurationProperties;  
 import xyz.sumithpuri.spring.boot.annotation.model.Book;  
 import xyz.sumithpuri.spring.boot.annotation.service.BookServiceImpl;  

 /**  
  * @author Sumith Puri  
  *  
  */  
 @RestController  
 public class SBASampleController {  

      @Autowired  
      BookServiceImpl bookServiceImpl;  

      @Autowired  
      SBASampleConfigurationProperties sbasConfigProps;  

      @GetMapping("/findall")  
      public HashSet&lt;Book&gt; getAllBook() {  
           return bookServiceImpl.findAllBook();  
      }  

      @GetMapping("/findbyid/{id}")  
      public Book geBookById(@PathVariable long id) {  
           return bookServiceImpl.findBookByID(id);  
      }  

      @DeleteMapping("/delete")  
      public void deleteBook() {  
           bookServiceImpl.deleteAllData();  
      }  

      @PostMapping("/")  
      public void addBook(@RequestBody Book book) {  

           System.out.println("Testing Properties: " + sbasConfigProps.getName() + "; "   
                                                                  + sbasConfigProps.getMail() + "; "  
                                                                  + sbasConfigProps.getYear());  
           bookServiceImpl.addBook(book);  
      }  
 }</pre>

( @RestController, @GetMapping, @PostMapping, @DeleteMapping, @Autowired, @Pathvariable )
