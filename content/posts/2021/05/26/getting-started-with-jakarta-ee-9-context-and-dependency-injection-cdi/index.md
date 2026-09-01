---
title: "Getting Started with Jakarta EE 9: Context & Dependency Injection"
date: "2021-05-26T06:56:27+00:00"
lastmod: "2021-08-23T15:32:06+00:00"
description: "Next in the \"Getting Started with Jakarta EE\" series, we look at various specifications and how you can use them in your next application."
canonical: "https://blog.payara.fish/getting-started-with-jakarta-ee-9-context-and-dependency-injection-cdi"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "Favicon-3-2.png"
categories:
  - "Jakarta EE"
  - "Microservices"
related_posts:
  - "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "getting-started-with-apache-camel-on-jakarta-ee-10"
  - "exploring-java-records-in-a-jakarta-ee-context"
frozen: false
---

In the next part of the "[Getting Started with Jakarta EE](https://foojay.io/today/getting-started-with-jakarta-ee-9-how-to-create-a-rest-api-with-jakarta-ee-9/)" series, we discuss Context and Dependency Injection (CDI) and the CDI specification.

The CDI specification is an important backbone of Jakarta EE as it brings several specifications together. Over the years, it has become more and more important as an increasing number of specifications started using CDI as the basis for it.

We will also tell you a bit about the different scopes, the interceptor mechanism, and the Event system.

## Dependency Injection and Bean Scopes

Instead of instantiating Java Objects yourself, it is sometimes easier to ask for an instance from a central location. It can handle the lifecycle for you when the instance needs to be created and can be disposed of. It will also have all its dependencies prepared and ready and can be enhanced with some interceptors related to logging, transaction, and security, for example.

The requirements for a CDI bean are very minimal: a default constructor, implicitly or explicitly defined, and a concrete class is all that is needed. There is no need to use any kind of annotation, to indicate it can be handled through the CDI subsystem.

Most of the time, an annotation is placed on the class to indicate when the bean can be disposed of, and how many copies of the bean can be created. In the absence of such an annotation, the bean is created every time an 'Injection' is requested and disposed of when the other class is disposed of.

The typical annotations are:

|                                               |                                                                           |
|-----------------------------------------------|---------------------------------------------------------------------------|
| @jakarta.enterprise.context.ApplicationScoped | one instance for the entire application                                   |
| @jakarta.enterprise.context.SessionScoped     | an instance is typically linked with a user Session like the HTTP Session |
| @jakarta.enterprise.context.RequestScoped     | an instance for each user request                                         |

Since the *ApplicationScoped* can't hold any user or request specific information, it is ideal for providing functionality to your application, also known as services.

*SessionScoped* is typical for holding user information (like logged in user) and *RequestScoped* can be used to keep information related to the specific user request.All the classes on your classpath are scanned and metadata is assembled for all possible CDI beans. There is an option to exclude certain packages from scanning, if you want to limit the creation of the metadata. This is checked with all injection points that are defined to see if there is a match which guarantees that the entire dependency mechanism will work properly for your application.

Since most of the time you add an annotation that defines the scope of the bean, many users define the following configuration for CDI to limit the CDI beans to those that have the proper CDI scope annotation. The configuration is provided in a file called *beans.xml* and for a Web Application (WAR) it should be located in the *WEB-INF* directory.

```xml
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="
https://jakarta.ee/xml/ns/jakartaee
https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd"
bean-discovery-mode="annotated">
</beans>
```

The value annotated for the Bean Discovery mode indicates CDI should only consider explicitly annotated classes. But this configuration is optional and the beans.xml file doesn't need to be there to have the Dependency Injection functionality in your application.

Let us conclude this section with a typical example of a CDI bean.

A typical service containing some business logic, In this example, it defines the greeting template for the language.

```java
@ApplicationScoped
public class GreetingService {

   public String getGreetingTemplate(String language) {
      String result = "Hello %s";
      switch (language) {
         case "fr" : result = "Bonjour %s"; break;
         case "de" : result = "Willkommen, %s"; break;
      }
      return result;
   }
}
```

It can be included in the JAX-RS greeting endpoint we defined in the previous blog using the jakarta.inject.Inject annotation.

```java
@Path("/hello")
public class HelloResource {

   @Inject
   private GreetingService greetingService;

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/{name}")
   public String doGreeting(@PathParam("name") String someValue, @QueryParam("language") String language) {
      return String.format(greetingService.getGreetingTemplate(language), someValue);
   }
}
```

## Bean Initialisation

If we define a CDI bean, the required dependencies are 'injected' were we have used the @Inject annotation. But in some cases, the service needs to perform some initialisation steps before it can be used. We indicate which method needs to be executed after the instance is created, the dependencies are injected but BEFORE the bean itself is injected into another bean.

The method should be annotated with jakarta.annotation.PostConstruct to indicate this method.

```java
@ApplicationScoped
public class GreetingService {

   @PostConstruct
   public void init() {
      // ..
   }
}
```

## Interceptors

Another important aspect of CDI is the ability to define interceptors. They define some cross-cutting concerns for your application. These code bits need to be applied to various parts of your code and ideally should be separated from it as they don't belong to the logic of a method. But they are very important as they handle the transactional, security, or logging aspects.

Within Jakarta EE, we can indicate that such a cross-cutting concern needs to be applied to a method (or to all methods of a CDI bean) by annotating it. So assume we want to keep track of the execution time of a method, we would like to write this annotation on the method.

```java
@ApplicationScoped
public class GreetingService {

   @Timed
   public String getGreetingTemplate(String language) {
      //
   }
}
﻿
```

That is a custom annotation we have invented, so we need to define it. The special requirement here for the CDI interceptor is that we define it as an Interceptor Binding.

```java
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE})
public @interface Timed {
}
```

We need also the code that needs to be executed whenever the annotated method will be executed. We can again define this very easily through some annotations.

```java
@Interceptor
@Timed
public class TimedInterceptor {

   @AroundInvoke
   public Object timeInvocation(InvocationContext ctx) throws Exception {
      // Before method
      long start = System.currentTimeMillis();
      Object result = ctx.proceed();
      // after method

      return result.toString() + " calculated in "+ (System.currentTimeMillis() - start) + "ms";
   }
}
```

The indication that it is the code for our timed interceptor is done through the annotations jakarta.interceptor.Interceptor and our custom interceptor binding annotation.

This class needs to have a method that is annotated with jakarta.interceptor.AroundInvoke and must have a parameter of type jakarta.interceptor.InvocationContext, which gives us access to the context in which the interceptor execution is performed.

In the above example, the logic is very simple but this interceptor is also a CDI bean. So we are allowed to inject other dependencies and perform whatever, even complex logic, that is needed.

In the example, we see that we can alter the return method of the original intercepted method. This is a rare use case as in most situations, the parameters and the return value are not altered by the interceptor. The goal of the interceptor is to perform some additional actions but not to alter the parameters and return value.

The interceptors are not applied automatically by the CDI implementation, we need to activate them within the beans.xml.

```xml
<beans>
   <interceptors>
      <class>fish.payara.jakarta.ee9.start.TimedInterceptor</class>
    </interceptors>
</beans>
```

In the above example, we did not specify the namespace of the beans tag. This has the advantage that it can be used by different versions of Java EE and Jakarta EE (since the namespace changes between Java EE, Jakarta EE 8, and Jakarta EE 9) but we do not specify the bean discovery mode.

## CDI Events

Another very powerful feature of the CDI framework is the ability to send Events from producer to consumers in a very efficient and decoupled way. In many applications, you have the requirement that some information or an event needs to be used by other parts of the application. Instead of direct coupling, you can choose a loose coupling so that producers and consumers don't have to know each other.

This loose coupling has the benefit that your application is more flexible and over time, the CDI event mechanism can be replaced with a messaging system if your application is refactored into multiple microservices. The CDI framework allows only a propagation within the same application, but with the Payara Platform products, you can specify that these CDI events are propagated through the Domain Data Grid and thus producer and consumer can be within different applications in different runtimes.

You can see the CDI event feature in action in the video, and I describe it here in the blog. A CDI event has a payload, the information that is carried in the event. This is just a POJO object like the following class.

```java
public class AddPersonEvent {

   private Person person;

   public AddPersonEvent(Person person) {
      this.person = person;
}

   public Person getPerson() {
      return person;
   }
}
```

As you can see, no specific requirements for this class. You can store multiple objects in this payload class and in the above case, you can even use the Person class as Payload without the need to define a new class. Producing an event with the above payload can be done by injecting an instance of jakarta.enterprise.event.Event and execute the fire() method on it.

```java
@Inject
private Event<AddPersonEvent> addPersonEvent;

addPersonEvent.fire(new AddPersonEvent(person));
```

The listener part can be any in any other CDI bean when you indicate that the method Observes events with that specific payload.

```
public void addPerson(@Observes AddPersonEvent addPersonEvent) {
   allPersons.add(addPersonEvent.getPerson());
}
```

As you can see, there is no coupling between the producer and the consumer of the CDI events. The only link they have is the payload class, AddPersonEvent in our example.

## Conclusion

The Context and Dependency Injection specification provides you with some powerful features for the core of your Jakarta EE application. You can define beans that provide you services or keep data for a user request. You can define the lifecycle of those beans using annotations and define initialisation methods. You can also define an alternative, decorators and producers for CDI beans which I didn't cover in this blog. [Have a look at the documentation what is available and how you can use it.](https://docs.payara.fish/community/docs/documentation/payara-server/development-tools/cdi-dev-mode/README.html)

Besides the basics of Dependency Injection, we saw also some other features that are available with the specification, defining interceptors, and using CDI events.

## Watch the Video

We've also created an associated video demonstration as part of our video series['Getting Started with Jakarta EE 9'.](https://www.youtube.com/playlist?list=PLFMhxiCgmMR8rWUcvrxiSQnKqyHk2knpi)

This time, I will explain a few features of CDI. I will tell a bit about the different scopes, the interceptor mechanism, and the Event system. Watch here:

{{< youtube 79l1Baej52s >}}
