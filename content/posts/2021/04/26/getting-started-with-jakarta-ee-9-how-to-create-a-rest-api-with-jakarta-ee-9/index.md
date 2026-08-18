---
title: "How to Create a REST API with Jakarta EE 9"
slug: "getting-started-with-jakarta-ee-9-how-to-create-a-rest-api-with-jakarta-ee-9"
date: "2021-04-26T07:18:39+00:00"
lastmod: "2021-04-26T07:30:59+00:00"
description: "For those who are not familiar with Jakarta EE, this article should give you an indication how to create such a REST API with Jakarta EE 9."
canonical: "https://blog.payara.fish/getting-started-with-jakarta-ee-9-how-to-create-a-rest-api-with-jakarta-ee-9"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "MicrosoftTeams-image-46.jpg"
categories:
  - "Videos"
tags:
related_posts:
  - "build-flexible-jakarta-ee-applications-with-apache-couchdb-nosql-database"
  - "can-java-jakarta-ee-do-microservices"
  - "do-java-jakarta-ee-standards-matter"
  - "is-java-jakarta-ee-cloud-native"
frozen: false
---

In this second article of the "[Getting Started with Jakarta EE 9](https://foojay.io/today/getting-started-with-jakarta-ee-9-hello-world/)" series, we show you some basic scenarios using the REST specification. Although most people are using the term REST or RESTful API just to indicate they do data transfer over HTTP, and ignore the "Hypermedia as the engine of application State (HATEOS)" part of REST. The technology is used a lot lately to connect the front-end with the back-end.

For those who are not familiar with [Jakarta EE](https://blog.payara.fish/topic/jakartaee), this article should give you an indication how to create such a REST API with Jakarta EE 9.

## Configuration

Just as with the other Jakarta Specifications, you only need to add the Web API dependency to your project. This gives you access to all classes, interfaces, and annotations that you need to use in your application to write a Jakarta EE application. The server, Payara Server in this case, has all the code and implementations on board so you can have a lightweight WAR file that only contains your application code.

When using Maven as a build tool, you need to have the following dependency for your WAR application: (More on this in the [Getting Started with Jakarta EE 9 : Hello World](https://blog.payara.fish/getting-started-with-jakarta-ee-9-hello-world) article).

```xml
<dependency>
   <groupId>jakarta.platform</groupId>
   <artifactId>jakarta.jakartaee-web-api</artifactId>
   <version>9.0.0</version>
   <scope>provided</scope>
</dependency>
```

and when you are using Gradle, you need to following line in the *build.gradle* file:

```
providedCompile 'jakarta.platform:jakarta.jakartaee-web-api:9.0.0'
```

There are several configuration options possible to configure the Jakarta REST framework, but most of the time, you just need to define the part of the URL that will trigger the processing by the REST engine. This can be done by defining the following Java class in your project:

```java
@ApplicationPath("/api")
public class DemoApplication extends Application {
}
```

The class extends jakarta.ws.rs.core.Application which is the base class for the configuration, and the annotation, jakarta.ws.rs.ApplicationPath, identifies the application path that serves as the base URI for all resource URIs. In the first example, it will become clear where this */api* part of the URL fits in the final URL of the endpoint.

## Hello World EndPoint

Now that we have the application and Jakarta REST configuration in place, let us create the simplest possible endpoint.

A Java class like this is enough to create a REST resource:

```java
@Path("/hello")
public class HelloResource {

   @GET
   public String sayHello() {
      return "Hello World";
   }
}
```

The jakarta.ws.rs.Path annotation defines the link between the URL entered by the user and the Java class that is responsible for handling that request. The jakarta.ws.rs.GET annotation indicates that we need to call our endpoint using the HTTP Get method. The return of the method, the *"Hello World"* String, will be the content of the response to the client.

When an application consisting of these two classes is compiled, packaged, and deployed on Payara Server or Payara Micro, the following call to the endpoint give this result:

|----------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 | \>curl -v http://localhost:8080/rest/api/hello \* Trying ::1... \* TCP_NODELAY set \* Connected to localhost (::1) port 8080 (#0) \> GET /rest/api/hello HTTP/1.1 \> Host: localhost:8080 \> User-Agent: curl/7.64.1 \> Accept: \*/\* \> \< HTTP/1.1 200 OK \< Server: Payara Server 5.2021.1 #badassfish \< X-Powered-By: Servlet/4.0 JSP/2.3 (Payara Server 5.2021.1 #badassfish Java/Azul Systems, Inc./11) \< Content-Type: text/plain \< Content-Length: 11 \< X-Frame-Options: SAMEORIGIN \< \* Connection #0 to host localhost left intact Hello World\* Closing connection 0 |

First, let us have a look at the URL structure.

*http://\<host-name\>:\<port\>/\<context-root\>/\<REST-config\>/\<resource-config\>*

* *\<host-name\>* : The hostname of the machine running the Payara Server installation.
* *\<port\>* : The port Payara Server listens on for incoming HTTP requests. This is port 8080 by default but it can be configured.
* *\<context-root\>* : The context root assigned to the deployed application. This is the filename (without the extension) of the deployed WAR file by default but it can be specified during deployment.
* *\<REST-config\>* : The value we have defined for the @ApplicationPath annotation in our project.
* *\<resource-config\>* : The value we defined in the @Path annotation on the Java class.

If we have a look at the output of the curl command, including the headers, we see the following aspects:

Line 10 : We received a response with status 200 (OK) indicating the successful execution of the request. At the end of this blog, we show you how you can control the HTTP status that is returned to the caller.

Line 13 : The body of the response is of plain text type. We didn't specify this explicitly but it was derived from the fact that our sayHello method returned a String. The best practice is that you always specify the format of the response through the jakarta.ws.rs.Produces annotation.

```
@Produces(MediaType.TEXT_PLAIN)
```

## Reading URL Information

It's important that you can determine parts of the specified URL sent by the client when you write API endpoints as that will hold important information related to the request. We will cover two ways in this example, reading part of the URL and reading query parameters.

To demonstrate these, we can add the following lines to the *HelloResource* Java class.

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
@Path("/{name}")
public String doGreeting(@PathParam("name") String someValue, @QueryParam("language") String language) {
   return "Hello " + someValue + " with language " + language;
}
```

You can see that we also specify a @Path annotation and that it has curly braces. The curly braces indicate that it is a placeholder and that the actual value specified in the URL is transferred to the variable 'name'. The variable name is also specified in the jakarta.ws.rs.PathParam annotation. This way, the Jakarta REST engine knows that the matching URL part needs to be used as the value for the method parameter *someValue* . The second method parameter has another annotation, jakarta.ws.rs.QueryParam, and as you can guess, it will transfer the value of the query parameter *language* to this parameter.

When deployed, we can make a call to the URL*/api/hello/Payara?language=en* which will result in calling the Java method with the following parameters doGreeting("Payara","en").

## JSON Support

In the previous examples, we always used the content type *text/plain* as the return type for a response. In a production type application, most of the communication is performed using the JSON data format. In this next example, we show you how easy it is to return this type of data.

Suppose we have a Java class that holds information about a Person:

```java
public class Person {
private String name;
private int age;

// Jakarta JSON requires a no-argument constructor.
// Setters and Setters omitted
}
```

And we can define the following Java resource Class that returns such a value.

```java
@Path("/person")
public class PersonResource {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Person getPerson() {
      return // some way to return a Person instance
   }
}
```

How you retrieve the Person instance that needs to be returned is not important here. In one of the next blogs in the "Getting Started with Jakarta EE 9", we will indicate how you can inject a service and retrieve it from the Database, for example.

For this example, only the @Produces value is important here as we explicitly indicate that the response should contain a JSON payload. This is enough for the Jakarta REST system to know that it must convert the Person instance to JSON using the built-in support defined within Jakarta. Calling this endpoint will result in the following response:

```
curl -v http://localhost:8080/rest/api/person
* Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /rest/api/person HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> 
< HTTP/1.1 200 OK
< Server: Payara Server 5.2021.1 #badassfish
< X-Powered-By: Servlet/4.0 JSP/2.3 (Payara Server 5.2021.1 #badassfish Java/Azul Systems, Inc./11)
< Content-Type: application/json
< Content-Length: 24
< X-Frame-Options: SAMEORIGIN
< 
* Connection #0 to host localhost left intact
{"age":42,"name":"Rudy"}* Closing connection 0
```

You can see that we do not need to configure the Person class for JSON serialization. By default, each property is inspected and added to the output. It also uses the type of the property to determine the optimal encoding so that the integer value is written without quotes in this example.

Besides support for JSON, you can also indicate, through the Media Type value, that you need XML output.

Of course, several configuration options are possible and will be covered in a future blog in this series that discusses JSON support.

## Sending Data

Now that we have explored the possibilities of retrieving data from the server, we want to send information to be processed. Within the *PersonResource* Java Class we can create the following method:

```java
@POST
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public String handlePersonRequest(Person person) {
   return person.toString();
}
```

We have to indicate the HTTP method we are using and here we have specified the jakarta.ws.rs.POST annotation. When the Jakarta REST system receives a request matching that HTTP Method on the URL specified by the @Path, it will transfer the control to this method.

You can also see that we have specified the jakarta.ws.rs.Consumes annotation with a JSON value. This information is also used to match the correct Java method that needs to be executed for a certain request. If we send a POST request to the URL but it has another content type (like XML) this method is no longer considered as a candidate.

The Media type information is also used to convert the request body to the method Parameter. There can only be one method parameter that doesn't have any Jakarta REST annotations on it since you can only convert the body to one parameter. Additional parameters having the @PathParam and @QueryParam are allowed and the URL Information reading section we discussed earlier can be used when you send information.

## Take Control of HTTP Status in Response

In this last section, we explore the option to take control of the HTTP status value that is returned in the response. Until now, we always returned 200 (status OK) since the call was successful and contained some payload in the result.

If we return null in any of the previous examples (instead of an actual String or Person instance) the returned status is 204 (No content). But there are many cases that you would like to have control over the returned status.

The following example describes how you can do this.

```java
@Path("/evenValue")
public class ResponseResource {

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/{intVal}")
   public Response testValue(@PathParam("intVal") Integer value) {
      if (value % 2 == 0) {
         return Response.ok("Value is a correct even number") .build();
      } else {
         return Response.notAcceptable(Collections.emptyList()).build();
      }
   }
}
```

One of the other blogs in this series will go into detail about the validation specification within Jakarta EE, but there are many situations you perform some programmatic changes and want to return a status 406, not acceptable, to indicate the request was invalid.

The example describes a Java Resource that defines an endpoint that checks if the supplied number is an even value. if not, it returns a status to indicate the value is not correct.

Most of the code looks familiar as we discussed the annotations before. The new thing is the jakarta.ws.rs.core.Response return type of the method. The Response encapsulates all parts of the response, the status and the payload, and can be created using the ResponseBuilder.

In the case of a correct request, you can use the Response.ok() method. The parameter of the ok method defined the Payload. In the example it is a String but when you specify a Java instance and have the appropriate MediaType, a JSON response is possible.

In case the value is not correct, the Response.notAcceptable sends the desired status back to the client.

## Conclusion

Jakarta REST can be used with a minimal amount of configuration and is mainly centered around the Java methods that perform or delegate the actual work without the need for concern about the infrastructure parts of the request.

The configuration and infrastructure parts are specified by using annotations. The @ApplicationPath defines the URL triggers the REST support, @Path link the URL to the Java class that handle the response, @GET, @POST and other annotations define the HTTP method that is supported by the code, @PathParam and @QueryParam retrieves information from the URL and passes it as parameters to the Java Method and @Consumes and @Produces indicates the type of the Payload in the Request and Response.

And in the last example, we showed how you can have full control of the response, the HTTP status and Payload, by using the ResponseBuilder.

This blog refers also on some other topics of Jakarta EE that will be described in future blogs like Validation, to perform validation on the received data, and CDI to delegate the work to other services. Subscribe to the blog to receive updates as new articles are published.

## Video Demonstration: How to Create a REST API

We've also created a demo video, which describes how to capture path parameters from the query parameters, how to define different methods such as get and post, encoding to use JSON formatting, and how to use the Response Builder for more control over what the server sends back to the client.

Watch here:

{{< youtube D1LZAaO_pxI >}}

[](https://youtu.be/D1LZAaO_pxI)
