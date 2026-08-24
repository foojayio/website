---
title: "Getting Started with RIFE2 Java Web Framework v1.0.0"
date: "2023-02-07T07:44:45+00:00"
lastmod: "2023-04-10T20:39:18+00:00"
description: "Benefiting from a decade of work that went into original RIFE, the new RIFE2 provides an opinionated full stack web framework in a 2MB JAR."
authors:
  - "geert-bevin"
image: "rife2.png"
categories:
  - "Cloud"
  - "Release Notes"
  - "RIFE2"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-functional-programming-techniques-in-java-a-primer"
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "why-i-moved-my-blog-to-rife2-after-23-years"
frozen: false
---

RIFE2 is a very recently released and completely re-imagined version of my RIFE framework from 10 years ago.

You can find all the details on the [project website](https://rife2.com "project website") and on [GitHub](https://github.com/gbevin/rife2 "GitHub").

## What is RIFE2?

RIFE2 provides an opinionated full stack web framework, without external dependencies, in a 2MB jar. It is designed as a whole to make the creation of dynamic web applications and REST web services very fast while preserving maintainability and leveraging the strengths of the Java platform. You can still pull in other Java libraries and frameworks, of course, however, the layers of RIFE2's full stack have knowledge about each-other, which provides many convenient features and shortcuts.

RIFE2 also provides features that you can't find anywhere else, like web continuations, bidirectional templates, integrated content management, continuations-based workflow. These can greatly streamline your application development without having to research, integrate and learn other libraries.

RIFE2 is perfect if you're a small team or a single developer that wants to focus on creating web products and solutions with the least amount of friction in Java, instead of big long-term enterprise projects.

Here are some of the features:

• Web engine with continuations  

• Out-of-container testing  

• Bidirectional logicless template engine  

• Query-builder based database engine  

• Bean-centric metadata with constraints  

• Form building  

• Dynamic validation  

• Content management framework  

• Authentication  

• Cron-like scheduler  

• Resource abstraction  

• Workflow engine with continuations  

• Localization support  

• and much more …

## Quickstart

I'm very passionate about staying in the flow, to quickly start with a project and frictionlessly iterate, to have your development version up and running immediately with a smooth path towards deployment.

RIFE2 is designed with that development approach in mind.

Let's take a look at a quick introductory *HelloWorld.java* example.

```java
public class HelloWorld extends Site {
    public void setup() {
        get("/hello", c -> c.print("Hello World"));
    }

    public static void main(String[] args) {
        new Server().start(new HelloWorld());
    }
}
```

This is a fully functional application that you can launch from your build tool, [step by step instructions](https://github.com/gbevin/rife2/wiki/Getting-Started) are available in the RIFE2 manual. Deployment is trivial also, since this class name simply needs to be set as an attribute in your [web.xml file](https://github.com/gbevin/rife2/wiki/Deployment).

## Out-of-container testing

RIFE2 comes with an API to perform out-of-container tests, directly interacting with your site to simulate full request-response interactions, without having to spin up a servlet container.

For example, using JUnit 5:

```java
class HelloTest {
    @Test void verifyHelloWorld() {
        var m = new MockConversation(new HelloWorld());
        assertEquals("Hello World", m.doRequest("/hello").getText());
    }
}
```

This entire example is obviously trivial, it registers the `/hello` route and simply prints text as the response. The main method start up the embedded Jetty server with the `Site` that contains this route.

The key part that I want to bring across here is that it really just takes a couple of lines of Java code to have a running application. Now you can extract that initial application route into a dedicated class, enhance the tests, add more routes, and very quickly work on creating the product you have in mind with virtually no friction.

As mentioned in the introduction, RIFE2 has many features, far too many to detail in this article. On the surface the route definition above looks similar to some other frameworks, but I already want to highlight one unique feature: type-safe links.

## Type-safe links

One of the most brittle aspects of web application development is typing links and URLs as text literals, without anything guaranteeing they remain correct when changes occur. RIFE2's routing API allows all your application links to be generated correctly without any effort on your behalf.

Let's add a new route that contains an HTML link towards the previous Hello World route.

You can see that routes don't have to be created inside the `setup()` method, but can also be created as part of your `Site`'s construction, allowing the routes to be stored in fields. These fields can then be used with the `c.urlFor(route)` method to generate a link that will always remain correct, no matter which path the `hello` route uses or which web application context your application is deployed into.

```java
public class HelloLink extends Site {
    Route hello = get("/hello", c -> c.print("Hello World"));
    Route link = get("/link", c-> c.print("<a href='" + c.urlFor(hello) + "'>Hello</a>"));

    public static void main(String[] args) {
        new Server().start(new HelloLink());
    }
}
```

We can now test this as such:

```java
class HelloTest {
    @Test void verifyHelloLink() {
        var m = new MockConversation(new HelloLink());
        assertEquals("Hello World", m.doRequest("/link")
            .getParsedHtml().getLinkWithText("Hello")
            .follow().getText());
    }
}
```

## Wrapping up

This was a tiny sneak peak into RIFE2's capabilities.

The code benefits from a decade of work that went into original RIFE and most of the underlying implementation has been proven in production.

RIFE2 is also covered by an extensive collection of automated tests that are ran automatically through GitHub flows on a matrix of JDK and database combinations with each commit.

If this looks interesting, please take a look at the [documentation](https://github.com/gbevin/rife2/wiki), the [JavaDocs](https://gbevin.github.io/rife2/) and the [examples](https://github.com/gbevin/rife2/tree/main/app/src/main).

Also feel free to [connect on Mastodon](https://uwyn.net/@gbevin) or to [join me on Discord](https://discord.gg/DZRYPtkb6J), I would love to hear from you!
