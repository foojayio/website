---
title: "Book Review: Modern Frontends with htmx"
date: "2023-12-24T10:07:20+00:00"
lastmod: "2023-12-24T10:07:21+00:00"
description: "Get started experimenting with htmx and the many benefits it can bring to build dynamic, user-friendly, web pages!"
canonical: "https://webtechie.be/post/2023-12-10-book-review-htmx/"
authors:
  - "frankdelporte"
image: "htmx-cover-small.jpg"
categories:
  - "Book Review"
  - "Books"
  - "Java"
  - "Spring"
related_posts:
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "new-book-taming-thymeleaf"
  - "controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi"
  - "foojay-podcast-67"
frozen: false
---

**People who follow me probably know I have a big love for user interface development with JavaFX (for desktop), and Vaadin (for browser). But as always, there are different solutions for every challenge, and building a web user interface with Java can be done with other frameworks.**

htmx seems to be one of those hot new rising stars, and I already wanted to dive deeper into it, but didn't find the time yet.

Luckily, [Wim Deblauwe](https://www.wimdeblauwe.com) is here now to help me!

{{< img src="htmx-cover-small.jpg" class="size-full is-resized" width="540" height="810" style="aspect-ratio:0.6666666666666666;width:259px;height:auto" >}}

In the past, I had the privilege to work on a project that was initially created by Wim for a startup, where Thymeleaf was used. His book "[Taming Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)" has been a reference for many people who wanted to learn more about the Thymeleaf framework.

This month, he's published a new book, "[Modern frontends with htmx](https://www.wimdeblauwe.com/books/modern-frontends-with-htmx/)" where he takes a new approach by adding htmx into the "mix" to create dynamic and interactive web applications with Spring Boot and Thymeleaf.

## Technologies

The book is filled with example applications using several technologies.

### htmx

[htmx](https://htmx.org/), created by Carson Gross, is a little JavaScript library that allows you to write rich interactive web applications, in a simple way. A minimal learning curve is needed, and it works equally well with Python, PHP, .NET,...

Using the attributes of HTML elements, htmx can modify elements of a web page without the need to fully reload and redraw the page. HTML snippets on the page are swapped on the fly with new snippets provided by the backend.

### Spring Boot

[Spring Boot](https://spring.io/projects/spring-boot) is based on the [Spring Framework](https://spring.io/projects/spring-framework) (a dependency-injection system), and provides pre-configured components (= beans = singletons) that are tied together at runtime. Within the Spring system, a long list of subprojects is available for all kinds of use cases.

Spring Boot ensures that you get a list of versions of the included libraries that are guaranteed to work together.

### Thymeleaf

[Thymeleaf](https://www.thymeleaf.org/) is a server-side template engine to generate web pages filled with data from a backend. Making such a user interface interactive, is not easy and involves a lot of JavaScript. That's exactly where htmx fits in as it can solve these limitations in Thymeleaf.

htmx is not tied to Thymeleaf and can also be used with other templating engines.

### IntelliJ IDEA

Wim advises using the IntelliJ IDEA Ultimate edition as it fully supports Thymeleaf and JavaScript. But with some additional settings described in the book, you will also be able to use the free Community edition.

### Other

For the different examples in the book, various other tools are also used and described:

* [Tailwind CSS](https://tailwindcss.com/): Utility-first CSS framework to style your application without custom CSS. It can be overwhelming at the start as it uses many classes to style HTML elements, but Wim asks "to give it the benefit of the doubt ... you will probably like it."
* [daisyUI](https://daisyui.com/): Free component library for Tailwind CSS.
* [Alpine.js](https://alpinejs.dev/): JavaScript library providing some advantages compared to "Vanilla JavaScript".
* [SortableJS](https://sortablejs.github.io/Sortable/): JavaScript library for reorderable drag-and-drop lists.
* [Shoelace](https://shoelace.style/): A forward-thinking library of web components.

## Content of the book

All the sources used in the book are [available on GitHub](https://github.com/wimdeblauwe/modern-frontends-with-htmx-sources) with a directory per chapter. The samples use Java 17 (but also work with Java 21) and Spring Boot 3.1, but Wim plans to update the book and samples to use the recently released version 3.2 of Spring Boot.

### Chapter 1: Technologies used in the book

See the info above.

### Chapter 2: Getting Started

How to create a first example as a Maven project with the `ttcli` tool.

### Chapter 3. Starting with htmx

With a simple "Hello World" example, you get to understand how HTML fragments are used by htmx, instead of the "traditional" JSON we are used to by almost all JavaScript frameworks.

webjars also get introduced, as they allow to load any JavaScript library in a project. More info about webjars is available in the video [Using webjars with Thymeleaf](https://www.youtube.com/watch?v=84zmvoSg7dc).

Ajax events are used to interact with the backend to receive new HTML fragments, and the Triggers are also explained here which define the events that will cause an Ajax request. The example code given in this section is pretty long (almost 4 pages), as it includes many different trigger use-cases.

Targets and Swapping complete this chapter.

### Chapter 4: Request and response headers

In this chapter, Wim explains the extra arguments that can be used in the request and response headers to add more advanced functionality. And he made this even simpler by creating a Maven library ([io.github.wimdeblauwe:htmx-spring-boot-thymeleaf](https://mvnrepository.com/artifact/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf)) which provides constants and other features for a smoother integration of htmx into a Spring Boot / Thymeleaf system.

### Chapter 5: Project 1: TodoMVC

When you already used Spring Boot before, this example will help you to understand the use of htmx very easily. Wim starts with a "traditional" Thymeleaf user interface which gets improved step-by-step with htmx. First, a complete HTML page gets replaced with the result from a Controller and then gradually element by element, using the full advantages of htmx to replace parts of the page based on specific triggers and events.

He uses the features of Thymeleaf in this process, to simplify the generation of repeated content like table rows and list items. In some cases, he even provides alternative implementations to illustrate that same functionality can be achieved differently, according to your preferred choice.

### Chapter 6: Out of Band Swaps

Basically, htmx is used to swap a specific HTML-element on a page. But you can also have the server respond with multiple top-level pieces of HTML. These pieces can be used in the client to replace multiple HTML parts on several places of the page, independent of the element where the event happened. This is called Out of Band Swaps and is illustrated with an example where results get updated on multiple places, by filling in numbers in a spreadsheet-like table.

### Chapter 7: Client-side scripting

Although htmx wants to reduce the need for client-side JavaScript, it doesn't mean it can't be used... The examples in this chapter enhance the user experience beyond what can be achieved with "pure htmx". Wim provides examples with Vanilla JavaScript (no extra framework), Alpine.js ("jQuery for the modern web"), and SortableJS (drag-and-drop).

This way, a few problems can be handled when no response is received from the server, an error is returned, or it was not possible to send a request.

### Chapter 8: Security

Luckily, to keep a htmx application safe, you don't have to do anything when using Spring, as the session related to the autorization will be used by htmx. Only `POST`, `PUT`, and `DELETE` requests require some extra effort if CSRF-protection is enabled. But all this gets explained in this chapter with the use of Spring Security with a bookmark demo application.

### Chapter 9: Project 2: Contact application

In this chapter, Wim makes a Java adaption of the one presented in the book [Hypermedia Systems](https://hypermedia.systems/) by Carson Gross (the founder of htmx), Adam Stepinski, and Deniz Akşimşek. It's a contacts list, with active (= while typing) search functionality, inline validation of duplicate email addresses, trigger validation while typing, pagination, etc.

### Chapter 10: Web Components

Web components are reusable custom components, with their functionality and styling encapsulated away from the rest of your code. There are a lot of free of such component libraries like [Google Material Web](https://m3.material.io/develop/web), [Vaadin Web Components](https://github.com/vaadin/web-components), [SAP Web Components](https://sap.github.io/ui5-webcomponents/), [Shoelace](https://shoelace.style/),...

In this chapter, a user interface is created with Shoelace to show an expandable tree with the names of GitHub projects for a certain user, using the GitHub API.

### Chapter 11: Server-Sent Events \& Websockets

This chapter provides examples to push data from the server to the client with two different approaches:

* Server-Sent Events (SSE): Allows the server to push data to the client (one way only).
* Websockets: Two-way communication channel between client (browser) and server, but slightly more complicated.

### Chapter 12: Closing

Some extra links to further explore htmx.

## Review

Wim's book gives a lot of examples. They not only fully explain the use of htmx, but can also be used to improve your knowledge about Spring Boot and Thymeleaf. But I would advise you to read Wim's Thymeleaf book first if you have never used it before.

The various examples invite you to experiment, which is easy as all the code is available on GitHub, nicely divided per chapter, as a complete Maven project. Each project is clearly described in the book and Wim carefully takes one step at a time to explain the various features that Thymeleaf and htmx bring to the table.

I didn't have time yet to try out all the examples, but the book definitely got me triggered to experiment with htmx and the many benefits it can bring to build dynamic, user-friendly, web pages.
