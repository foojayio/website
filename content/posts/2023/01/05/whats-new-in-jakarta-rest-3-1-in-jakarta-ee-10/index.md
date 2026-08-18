---
title: "What’s New in Jakarta REST 3.1 in Jakarta EE 10?"
slug: "whats-new-in-jakarta-rest-3-1-in-jakarta-ee-10"
date: "2023-01-05T11:30:17+00:00"
lastmod: "2023-01-05T11:35:20+00:00"
description: "The latest version of Jakarta REST is 3.1, which shipped with Jakarta EE 10. This version comes with two great new noteworthy features!"
canonical: "https://blog.payara.fish/whats-new-in-jakarta-rest-3.1-in-jakarta-ee-10"
authors:
  - "luqman-saeed"
image: "payara_square_logo.jpg"
categories:
  - "Cloud"
  - "Jakarta EE"
  - "Payara"
  - "Release Notes"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "can-java-jakarta-ee-do-microservices"
enlighterjs: true
frozen: false
---

The Representational State Transfer or RESTful architecture is a stateless, HTTP based communication standard for modern applications. It was originally proposed by Dr. Roy Fielding in his PhD thesis.

It has, over the years, become the default programming language-agnostic means of enabling machine-to-machine communication.

An application written in the Django framework in Python can have REST resources that are consumed by another application written in Java with Jakarta EE. Similarly, a Jakarta EE application can create resources that can be consumed by a C# application.

The Jakarta RESTful Web Services specification is the Jakarta EE standard for creating REST web services on the platform. The specification defines several interfaces and annotations for creating web services in combination with other Jakarta EE specifications like the Jakarta Contexts and Dependency Injection.

The latest version of Jakarta REST is 3.1, which shipped with Jakarta EE 10. This version comes with two new noteworthy features that we will explore in this article.

## Java SE Bootstrap API

In the past, to run a Jakarta EE REST application required a full deployment to a compatible runtime like the Payara Platform or Payara Cloud. Jakarta REST 3.1 in Jakarta EE 10 introduced a new API to bootstrap REST resources outside of a container.

The simplest way to get a REST API deployed is to call the start() method on the jakarta.ws.rs. SeBootstrap class. This method returns a CompletionStage that you can then chain for different purposes.

For example:

```
SeBootstrap.Instance instance = SeBootstrap.start(new Application() { 
 @Override 
 public Set<Class<?>> getClasses() { 
 return Collections.singleton(HelloResourceSeBootstrap.class); 
 } 
}).toCompletableFuture().get(); 

UriBuilder uriBuilder = instance.configuration().baseUriBuilder(); 
var httpClient = HttpClient.newBuilder().build(); 
var httpRequest = HttpRequest.newBuilder() 
 .uri(uriBuilder 
 .path("hello-world-se") 
 .path("Jake").build()) 
 .header("Content-Type", "application/json") 
 .GET().build(); 

HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()); 
assertNotNull(response); 
var body = response.body(); 
assertNotNull(body);
```

Using the SeBoostrap class, we call the static start method, which takes a jakarta.ws.rs.core.Application instance and a default, implicit SeBootstrap#Configuration. We then call the baseUriBuilder() method on the returned Instance to build a URL that we pass to the java.net.http HttpRequest builder. This is the Java SE HTTP client introduced in Java 11. The HTTP client then makes a call to the configured resource and makes some assertions on it.

The start method of the SeBootstrap class has an overloaded version that can be used to pass a SeBootstrap#Configuration object. This can be used to configure the underlying HTTP server on which the REST resource is deployed. An example of such configuration could be as follows

This configuration can then be passed to the start method of SeBootstrap as follows:

```java
final SeBootstrap.Configuration config = SeBootstrap.Configuration 
 .builder() 
 .protocol(protocol) 
 .host(host) 
 .port(port) 
 .rootPath(rootPath) 
 .sslClientAuthentication(clientAuth) 
 .build();
```

This configuration can then be passed to the start method of SeBootstrap, as follows:

```java
SeBootstrap.Instance instance = SeBootstrap.start(new Application() { 
 @Override 
 public Set<Class<?>> getClasses() { 
 return Collections.singleton(HelloResourceSeBootstrap.class); 
 } 
}, config).toCompletableFuture().get();
```

Jakarta EE with Jakarta REST 3.1 has a number of ways you can now configure and deploy REST resources outside of a typical runtime container. You can take a look at these examples from the Jakarta REST GitHub repo for inspiration for your own projects.

## Multipart Support

Jakarta REST 3.1 in Jakarta EE 10 finally has standard, portable support for multipart as defined in RFC 7578. You can inject multipart EntityPart into a resource method through @FormParameter or as a list. The example below shows the injection of an EntityPart the represents a user's picture into a resource method.

```java
@POST 
@Path("submit/picture") 
@Consumes(MediaType.MULTIPART_FORM_DATA) 
public Response postForm(@FormParam("userId") String userId, 
 @FormParam("picture") EntityPart pic) { 

 String fileName = pic.getFileName().orElseThrow(NotSupportedException::new); 
 if (isFileExtension(fileName)) { 
 InputStream content = pic.getContent(); 
 //Do something with the content... 
 } 
 return Response.ok("Picture uploaded successfully").build(); 
}
```

The pic parameter in the resource method is a jakarta.ws.rs.core.EntityPart type annotated @FormParam. This will be extracted from the request body and automatically injected into the annoated field. The EntityPart has methods to get the headers, file name, name, file content as input stream among others.

The specification has small caveat about being careful with using Strings and EntityParts because parts of a multipart entity can be quite large, so care should be taken when using String parameter types as that will load the entire content of the part into the Java heap. We can make client requests to the above endpoint using the Jakarta REST client as follows.

```java
var client = ClientBuilder.newBuilder().build(); 
WebTarget target = client.target(URI.create(contextPath.toExternalForm() + "/api/hello-world")); 
EntityPart entityPart = 
 EntityPart.withName("passport-picture").content(pictureInputStream).fileName("passport-pic.jpg").build(); 
Entity<EntityPart> entity = Entity.entity(entityPart, MediaType.MULTIPART_FORM_DATA); 
Response response = target 
 .path("submit/picture") 
 .request() 
 .post(entity);
```

We construct the request using the EntityPart, then pass it as the body of the jakarta.ws.rs.client. Entity instance passed to the resource method. Another nice addition in Jakarta REST release is the ability to register a jakarta.ws.rs.core.Feature declaratively by placing an instance in META-INF/services/.

The release of Jakarta EE 10 marks an important milestone in the history of enterprise Java development. t represents a foundation of cloud-native capabilities in collaboration with Eclipse MicroProfile, enabling developers to create modern, applications for the cloud.

[Read about Jakarta EE 10 in our dedicated guide!](https://blog.payara.fish/cs/c/?cta_guid=0cb2c537-8010-41e4-aced-c4131027fdec&signature=AAH58kF4cJ0EATLI9YhyqoELZBkGi4wshA&pageId=93306925090&placement_guid=05c69179-0d81-4ed9-9a53-46b0486f091c&click=0a9ce12f-54e1-406b-8a34-db091724be1b&hsutk=4d47342e8dee74bb1d89f469ca70ef4e&canon=https%3A%2F%2Fblog.payara.fish%2Fwhats-new-in-jakarta-rest-3.1-in-jakarta-ee-10&portal_id=334594&redirect_url=APefjpHzVLqalPeA7d27G77EdhoUZZYP0PnKavPAcL1486nENFp7bxwvz4_Ly5HnXm4RdqrGEu93u4c5SujCKGHtzSAMf0NYs-8pPQUg9HRJBnoSExowo5WNFwnEiEji5E5NmgRupghMBzqCVvIx7NuhrIqn4dRnDj-AOEPmFkpNU3v2ReThiyw&__hstc=229474563.4d47342e8dee74bb1d89f469ca70ef4e.1672917432395.1672917432395.1672917432395.1&__hssc=229474563.1.1672917432395&__hsfp=700330257&contentType=blog-post)

You can start using Jakarta EE 10 immediately with [Payara Community 6.2022.1](https://www.payara.fish/downloads/payara-platform-community-edition/).
