---
title: "Server-Side Rendering with Spring Boot"
slug: "server-side-rendering-with-spring-boot"
date: "2024-09-23T06:58:41+00:00"
lastmod: "2024-09-24T11:50:15+00:00"
description: "Understanding the shared steps in the project setup is crucial before delving into the specifics of each client-augmenting technology."
canonical: "https://blog.frankel.ch/ajax-ssr/2/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/09/ai-generated-8709325.jpg"
categories:
  - "Spring"
tags:
related_posts:
  - "a-short-history-of-ajax-and-ssr"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "9-outdated-ideas-about-java"
  - "augmenting-the-client-with-alpine-js"
enlighterjs: true
frozen: false
---

Understanding the shared steps in the project setup is crucial before delving into the specifics of each client-augmenting technology. My requirements [from the last post](https://blog.frankel.ch/ajax-ssr/1/) where quite straightforward:
> * I'll assume the viewpoint of a backend developer
> * No front-end build step: no TypeScript, no minification, etc.
> * All dependencies are managed from the backend app, *i.e.*, Maven

It's important to note that the technology I'll be detailing, except Vaadin, follows a similar approach. Vaadin, with its unique paradigm, really stands out among the approaches.

WebJars {#h2-0-webjars}
-----------------------

WebJars is a technology [designed in 2012 by James Ward](https://jamesward.com/2012/04/25/introducing-webjars-web-libraries-as-managed-dependencies/) to handle these exact requirements.
> WebJars are client-side web libraries (e.g. jQuery \& Bootstrap) packaged into JAR (Java Archive) files.
>
> * Explicitly and easily manage the client-side dependencies in JVM-based web applications
> * Use JVM-based build tools (e.g. Maven, Gradle, sbt, ...) to download your client-side dependencies
> * Know which client-side dependencies you are using
> * Transitive dependencies are automatically resolved and optionally loaded via RequireJS
> * Deployed on [Maven Central](https://search.maven.org/)
> * Public CDN, generously provided by [JSDelivr](https://www.jsdelivr.com/)
>
> -- [Webjars website](https://www.webjars.org/)

A WebJar is a regular JAR containing web assets. Adding a WebJar to a project's dependencies is nothing specific:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars.npm&lt;/groupId&gt;
        &lt;artifactId&gt;alpinejs&lt;/artifactId&gt;
        &lt;version&gt;3.14.1&lt;/version&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

The framework's responsibility is to expose the assets under a URL. For example, Spring Boot does it in the `WebMvcAutoConfiguration` class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public void addResourceHandlers(ResourceHandlerRegistry registry) {
  if (!this.resourceProperties.isAddMappings()) {
    logger.debug("Default resource handling disabled");
    return;
  }
  addResourceHandler(registry, this.mvcProperties.getWebjarsPathPattern(),                  //1
    "classpath:/META-INF/resources/webjars/");
  addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), (registration) -&gt; {
    registration.addResourceLocations(this.resourceProperties.getStaticLocations());
    if (this.servletContext != null) {
      ServletContextResource resource = new ServletContextResource(this.servletContext, SERVLET_LOCATION);
      registration.addResourceLocations(resource);
    }
  });
}</pre>

1. The default is `"/webjars/**"`

Inside the JAR, you can reach assets by their respective path and name. The agreed-upon structure is to store the assets inside `resources/webjars//`. Here's the structure of the `alpinejs-3.14.1.jar`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">META-INF
  |_ MANIFEST.MF
  |_ maven.org.webjars.npm.alpinejs
  |_ resources.webjars.alpinejs.3.14.1
    |_ builds
    |_ dist
      |_ cdn.js
      |_ cdn.min.js
    |_ src
    |_ package.json</pre>

Within Spring Boot, you can access the non-minified version with `/webjars/alpinejs/3.14.1/dist/cdn.js`.

Developers release client-side libraries quite often. When you change a dependency version in the POM, you must change the front-end path, possibly in multiple locations. It's boring, has no added value, and you risk missing a change.

The WebJars Locator project aims to avoid all these issues by providing a path with no version, *i.e.* , `/webjars/alpinejs/dist/cdn.js`. You can achieve it by adding the `webjars-locator` JAR to your dependencies:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars.npm&lt;/groupId&gt;
        &lt;artifactId&gt;alpinejs&lt;/artifactId&gt;
        &lt;version&gt;3.14.1&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars&lt;/groupId&gt;
        &lt;artifactId&gt;webjars-locator&lt;/artifactId&gt;
        &lt;version&gt;0.52&lt;/version&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

I'll use this approach for every front-end technology. I'll also add the [Bootstrap](https://getbootstrap.com/) CSS library to provide a better-looking user interface.

Thymeleaf {#h2-1-thymeleaf}
---------------------------

Thymeleaf is a server-side rendering technology.
> **Thymeleaf** is a modern server-side Java template engine for both web and standalone environments.
>
> Thymeleaf's main goal is to bring elegant natural templates to your development workflow --- HTML that can be correctly displayed in browsers and also work as static prototypes, allowing for stronger collaboration in development teams.
>
> With modules for Spring Framework, a host of integrations with your favourite tools, and the ability to plug in your own functionality, Thymeleaf is ideal for modern-day HTML5 JVM web development --- although there is much more it can do.
>
> -- [Thymeleaf](https://www.thymeleaf.org/)

I was still a consultant when I first learned about Thymeleaf. At the time, were at the end of their life. were trying to replace them; IMHO, they failed.

I thought Thymeleaf was a fantastic approach: it allows you to see the results in a static environment at design time and in a server environment at development time. Even better, you can seamlessly move between one and the other using the same file. I've never seen this capability used.

However, Spring Boot fully supports Thymeleaf. Icing on the cake: the latter is available via an HTML namespace on the page. If you didn't buy into JSF (spoiler: I didn't), Thymeleaf is today's go-to SSR templating language.

Here's the demo sample from the website:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;table&gt;
  &lt;thead&gt;
    &lt;tr&gt;
      &lt;th th:text="#{msgs.headers.name}"&gt;Name&lt;/th&gt;
      &lt;th th:text="#{msgs.headers.price}"&gt;Price&lt;/th&gt;
    &lt;/tr&gt;
  &lt;/thead&gt;
  &lt;tbody&gt;
    &lt;tr th:each="prod: ${allProducts}"&gt;
      &lt;td th:text="${prod.name}"&gt;Oranges&lt;/td&gt;
      &lt;td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}"&gt;0.99&lt;/td&gt;
    &lt;/tr&gt;
  &lt;/tbody&gt;
&lt;/table&gt;</pre>

Here is a Thymeleaf 101, in case you need to familiarise yourself with the technology.

* When you open the HTML file, the browser displays the regular value inside the tags, *i.e.* , `Name` and `Price`. When you use it in the server, Thymeleaf kicks in and renders the value computed from `th:text`, `#{msgs.headers.name}` and `#{msgs.headers.price}`.
* The `$` operator queries for a Spring bean of the same name passed to the model. `${prod.name}` is equivalent to `model.getBean("prod").getName()"`.
* The `#` calls a function.
* `th:each` allows for loops

Thymeleaf integration with the front-end framework {#h2-2-thymeleaf-integration-with-the-front-end-framework}
-------------------------------------------------------------------------------------------------------------

Most, if not all, front-end frameworks work with a client-side model. We need to bridge between the server-side model and the client-side one.

The server-side code I'm using is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">data class Todo(val id: Int, var label: String, var completed: Boolean = false) //1

fun config() = beans {
  bean {
    mutableListOf(                                                              //2
      Todo(1, "Go to the groceries", false),
      Todo(2, "Walk the dog", false),
      Todo(3, "Take out the trash", false)
    )
  }
  bean {
    router {
      GET("/") {
        ok().render(                                                           //3
          "index",                                                             //4
          mapOf("title" to "My Title", "todos" to ref&lt;List&lt;Todo&gt;&gt;())           //5
        )
      }
    }
  }
}</pre>

1. Define the `Todo` class
2. Add an in-memory list to the bean factory. In a regular app, you'd use a `Repository` to read from the database
3. Render an HTML template
4. The template is `src/main/resources/templates/index.html` with Thymeleaf attributes
5. Put the model in the page's context

Thymeleaf offers a `th:inline="javascript"` attribute on the \`\` tag. It renders the server-side data as JavaScript variables. The documentation explains it much better than I ever could:
> The first thing we can do with script inlining is writing the value of expressions into our scripts, like:
>
> ```html
> /*    ...
>
>    var username = /*[[${session.user.name}]]*/ 'Sebastian';
>
>    ...
> /*]]>*/
> ```
>
> The `/*[[...]]*/` syntax, instructs Thymeleaf to evaluate the contained expression. But there are more implications here:
>
> * Being a javascript comment `(/*...*/)`, our expression will be ignored when displaying the page statically in a browser.
> * The code after the inline expression (`'Sebastian'`) will be executed when displaying the page statically.
> * Thymeleaf will execute the expression and insert the result, but it will also remove all the code in the line after the inline expression itself (the part that is executed when displayed statically).
>
> -- [Thymeleaf documentation](https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#script-inlining-javascript-and-dart)

If we apply the above to our code, we can get the model attributes passed by Spring as:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;script th:inline="javascript"&gt;
/*&lt;![CDATA[*/
  window.title = /*[[${title}]]*/ 'A Title'
  window.todos = /*[[${todos}]]*/ [{ 'id': 1, 'label': 'Take out the trash', 'completed': false }]
/*]]&gt;*/
&lt;/script&gt;</pre>

When rendered server-side, the result is:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;script&gt;
/*&lt;![CDATA[*/
  window.title = "My title";
  window.todos: [{"id":1,"label":"Go to the groceries","completed":false},{"id":2,"label":"Walk the dog","completed":false},{"id":3,"label":"Take out the trash","completed":false}]
/*]]&gt;*/
&lt;/script&gt;</pre>

Summary {#h2-3-summary}
-----------------------

In this post, I've described two components I'll be using throughout the rest of this series:

* WebJars manage client-side dependencies in your Maven POM
* Thymeleaf is a templating mechanism that integrates well with Spring Boot

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/compare-frontends).

**Go further:**

* [WebJars](https://www.webjars.org/)
* [WebJars Instructions for Spring Boot](https://www.webjars.org/documentation#springboot)
* [Introduction to WebJars](https://www.baeldung.com/maven-webjars)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/2/) on September 15^th^, 2024*

*[JSP]: Java Server Pages
*[JSF]: Java Server Faces
