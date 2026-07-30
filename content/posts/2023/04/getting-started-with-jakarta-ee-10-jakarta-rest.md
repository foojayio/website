---
title: "Getting Started With Jakarta EE 10 - Jakarta REST"
slug: "getting-started-with-jakarta-ee-10-jakarta-rest"
date: "2023-04-12T07:42:29+00:00"
lastmod: "2023-04-12T07:43:57+00:00"
description: "In this article, we take a look at how to get started developing RESTful web services on the Jakarta EE Platform using the Jakarta REST API."
canonical: "https://blog.payara.fish/getting-started-with-jakarta-ee-10-jakarta-rest"
authors:
  - "jadon-ortlepp"
image: "/images/posts/2023/04/getting-started-with-jakarta-ee-10-jakarta-rest/payara_square_logo.jpg"
categories:
  - "Jakarta EE"
  - "Payara"
tags:
related_posts:
  - "payara-launches-payara-cloud-serverless-approach-for-jakarta-ee"
  - "what-is-an-application-server-in-3-minutes"
  - "what-is-jakarta-rpc"
enlighterjs: true
frozen: false
---

Jakarta EE 10 was released in September of 2022 as the first major release of the venerable Enterprise Java development platform since it was moved to the Eclipse Foundation.

As a major release, it did come with a slew of updates to almost all the major specifications, including [Jakarta REST.](https://blog.payara.fish/whats-new-in-jakarta-rest-3.1-in-jakarta-ee-10)

In this article, we take a look at how to get started developing RESTful web services on the Jakarta EE Platform using the Jakarta REST API.

Setup {#h2-0-setup}
-------------------

As a core [Jakarta EE](https://blog.payara.fish/jakarta-ee-java-ee-guide) specification, there's not much setup to be done in getting Jakarta REST up and running.

Your typical Jakarta EE application will already contain a dependency on the platform in your dependency file, almost certainly the Maven pom.xml file.

If you are not sure where or how to get started, check out my very [opinionated guide](https://blog.payara.fish/getting-started-with-jakarta-ee-development-in-2023) to getting started with Jakarta EE 10.

Configuration {#h2-1-configuration}
-----------------------------------

With the dependency in place, we need to configure the root resource path for your REST resources.

This path is where all your REST resources in the given will be accessed relative to.

The Hello Application class below shows a very bare-bones, fully functional root Jakarta REST configuration.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ApplicationPath("/api")
public class HelloApplication extends Application {

}</pre>

This class extends the *jakarta.ws.rs.core.Application* class and is annotated with the *jakarta.ws.rs.ApplicationPath* annotation, passing in the "/api" string.

This path, given the above configuration class, is the root path relative to which all REST resources created by this application will be accessed.

The "/" preceding the root path is optional and will be prepended if omitted.   

The Application superclass extended above has three methods, getClasses, getSingletons and getProperties that you can override to further customise your configuration.

The above configuration, however, will suffice for a large number of applications.

Resource {#h2-2-resource}
-------------------------

With our Jakarta REST configuration in place, we are ready to create our obligatory, traditional "hello, world!" resource.   

The most atomic unit of a REST resource in Jakarta REST is a Java class that is annotated with a path via which it can be accessed.

Within this class, Java methods can be exposed as REST resources through the use of annotations.

The HelloResource class shown below puts these in together.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Path("/hello-world")
public class HelloResource {

    @GET
    public String pint() {
        return "Hello, World!";
    }

}</pre>

The HelloResource is a plain old Java class annotated with the jakarta.ws.rs.Path annotation.

This annotation is passed the value "hello-world", meaning this class will be hosted at the url path /hello-world.

The class has a single method, ping, that returns the string "Hello, World!"  

The method is annotated with jakarta.ws.rs.GET annotation. This annotatation marks this method as responding to GET requests.

In the abscence of any other metadata, a HTTP GET request to the path /hello-world will result in this method being beign called by the Jakarta REST runtime.

The fully qualified URL to this class and method will be *https://my-very-shiny-domain.wow/application-context-path/api/hello-world*, broken down into your domain (localhost if you running locally), the context path being the path your Jakarta EE application is deployed to, then the configured root REST path and finally the hello-world path.

A HTTP GET request to this path should return the string "Hello, World!" to the caller.

Conclusion {#h2-3-conclusion}
-----------------------------

As a matured specification, you can develop all kinds of sophisticated REST applications using the Jakarta REST API.

Check out the specification and take a look at some of the blogs and guides we have here on the Payara website to see how to develop modern stateless RESTful applications on the Jakarta EE Platform using Jakarta REST:

* [Intercepting REST Requests With Jakarta REST Request Filters](https://blog.payara.fish/intercepting-rest-requests-with-jakarta-rest-request-filters)
* [Jakarta EE 10: What Decision Makers Need to Know](https://www.payara.fish/resource/jakarta-ee-10-what-you-need-to-know/)
* [Payara Server REST API Documentation](https://docs.payara.fish/community/docs/Technical%20Documentation/Payara%20Server%20Documentation/Management%20and%20Monitoring%20REST%20API/Overview.html)
