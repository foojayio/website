---
title: "Vaadin: Battery-Included Server-Side AJAX Framework"
slug: "vaadin-battery-included-server-side-ajax-framework"
date: "2024-10-22T09:39:11+00:00"
lastmod: "2024-10-22T09:39:12+00:00"
description: "The beauty of Vaadin lies in its simplicity - you only write backend code."
canonical: "https://blog.frankel.ch/ajax-ssr/6/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2024/10/vaadin-battery-included-server-side-ajax-framework/reindeer-5635891.jpg"
categories:
  - "Vaadin"
tags:
related_posts:
  - "browserless-testing-of-vaadin-applications-with-karibu-testing"
  - "video-vaadin-drag-drop-support-its-so-easy"
  - "enterprise-java-application-development-with-jakarta-ee-and-vaadin"
enlighterjs: true
frozen: false
---

**I've written a lot about [Vaadin](https://vaadin.com/). I was so enthusiastic that I wrote the [first book](https://www.amazon.fr/Learning-Vaadin-Nicolas-Frankel/dp/1849515220) about it (besides the Book of Vaadin), its [updated edition](https://www.amazon.fr/Learning-Vaadin-Second-Nicolas-Frankel/dp/1782169776) for Vaadin 7, and a [companion website](https://morevaadin.com/). Still, I'm amazed that so many people in the JVM world never heard of it.**

In this article, I'd like to introduce Vaadin in the context of AJAX and SSR.

Short introduction to Vaadin {#h2-0-short-introduction-to-vaadin}
-----------------------------------------------------------------

The beauty of Vaadin lies in its simplicity - **you only write backend code** . You read that well. A Vaadin developer only needs to know Java, or any JVM language, and the Vaadin API. At runtime, Vaadin will create the client-side code, *i.e.*, HTML, JavaScript and CSS. This approach empowers developers to focus on the application's core functionality, making the development process more productive.

Vaadin builds upon components and layouts, just like regular desktop-based frameworks do. If you know Swing or JavaFX, you will feel right at home.

I mentioned CSS above: Vaadin allows you to develop your CSS in a dedicated reusable package called a *theme*. The icing on the cake: developing a theme can be done in parallel to backend development and has no adherence to the latter; the code doesn't need to use a specific template or to add specific classes to the HTML.

Vaadin setup {#h2-1-vaadin-setup}
---------------------------------

Setting up Vaadin in the context of Spring Boot is a breeze:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;project&gt;
    &lt;properties&gt;
        &lt;java.version&gt;17&lt;/java.version&gt;
        &lt;kotlin.version&gt;1.9.24&lt;/kotlin.version&gt;
        &lt;vaadin.version&gt;24.4.9&lt;/vaadin.version&gt;                   &lt;!--1--&gt;
    &lt;/properties&gt;
    &lt;dependencyManagement&gt;
        &lt;dependencies&gt;
            &lt;dependency&gt;
                &lt;groupId&gt;com.vaadin&lt;/groupId&gt;
                &lt;artifactId&gt;vaadin-bom&lt;/artifactId&gt;               &lt;!--2--&gt;
                &lt;version&gt;${vaadin.version}&lt;/version&gt;
                &lt;type&gt;pom&lt;/type&gt;
                &lt;scope&gt;import&lt;/scope&gt;
            &lt;/dependency&gt;
        &lt;/dependencies&gt;
    &lt;/dependencyManagement&gt;
    &lt;dependencies&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;com.vaadin&lt;/groupId&gt;
            &lt;artifactId&gt;vaadin-spring-boot-starter&lt;/artifactId&gt;   &lt;!--3--&gt;
        &lt;/dependency&gt;
&lt;/project&gt;</pre>

1. Set Vaadin version along with other properties
2. Keep the version of all dependencies consistent
3. Add the Vaadin Spring Boot integration library

Vaadin builds upon a regular Java Servlet, which maps to the root by default. The Vaadin Spring Boot integration allows overriding the default. Because our codebase integrates multiple frameworks, we map it to `/vaadin` via the relevant property:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">vaadin.url-mapping=/vaadin/*</pre>

At the first request from a client, Vaadin will return the JavaScript engine's code. The engine will make subsequent requests to retrieve the configured UI and scaffold the latter client side. From then on, the engine handles all user interactions and updates the UI if necessary.

First steps with Vaadin {#h2-2-first-steps-with-vaadin}
-------------------------------------------------------

Once we set up the project, we must configure which component Vaadin displays when it receives a request.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Route("/")                                                       //1
@PageTitle("Vaadin")                                              //2
class TodoView(todos: ArrayList&lt;Todo&gt;) : VerticalLayout() {       //3-4-5

    init {                                                        //6
        // ...                                                    //7
    }
}</pre>

1. Associates the component to the Vaadin servlet **subcontext** root
2. Set the static page title. In case you need a dynamic title, you can implement [HasDynamicTitle](https://github.com/vaadin/flow/blob/main/flow-server/src/main/java/com/vaadin/flow/router/HasDynamicTitle.java)
3. Define a `RootComponent` class
4. `VerticalLayout` is a class that Vaadin renders as an HTML `div`
5. The Vaadin Spring Boot starter takes care of injecting the list
6. Vaadin executes the `init()` function at the first browser request
7. The next code snippets will go there

Adding components {#h2-3-adding-components}
-------------------------------------------

In the above snippet, we inherited from `VerticalLayout`, a Vaadin-provided *component*.
> The Vaadin Design System includes a set of components that you can use to build your UI. The components have a server-side Java API in addition to the TypeScript API for client-side development.
>
> You use a component by first creating it and then adding it to a containing layout.
>
> -- [Creating UI in Vaadin Applications](https://vaadin.com/docs/latest/flow/create-ui)

Some components can contain others, and they know how to lay their subcomponents out. For example, `VerticalLayout` places components top-to-bottom in a column; `HorizontalLayout` places them left-to-right in a row.

Adding components to a layout is straightforward:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">add(Label("Hello"))                                               //1
add(Label("world!"))</pre>

1. In the context of the `init()` function

While this works perfectly, we can improve the situation using [Karibu-DSL](https://github.com/mvysny/karibu-dsl) since we use Kotlin. We can rewrite the above snippet as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">label("Hello")                                                    //1
label("world!")</pre>

1. `label()` is a Karibu DSL extension function on the `HasComponent` interface

Karibu is great, but with a slight downside: it doesn't offer extension functions for the whole API. For example, you need to fall back to the regular API to add footer to a `Grid` component:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">appendFooterRow().apply {
    getCell(completedProp).component = Button("Clean up") {
        todos.removeIf { it.completed }
        refresh()
    }
}</pre>

On the plus side, Karibu is Open Source, and you can always contribute if you have something to add.

Specific components related to the UI are not important for the general understanding. If you're interested, you can always check the [source code](https://github.com/ajavageek/compare-frontends).

User interactions {#h2-4-user-interactions}
-------------------------------------------

When mainframes were the kings of computing, you accessed them via terminals. The UI was pretty limited, and rendering occurred on the "dumb" terminal. Personal computers moved the rendering functionality from the server to the client. At this time, developers attached behaviour to a component via a trigger. For example, you could bind printing `Hello world!` when the user clicks a button.

Web applications changed this paradigm. As our previous articles showed, every interaction maps now to a request-response flow, synchronous or asynchronous. Vaadin brings us back to the original paradigm.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">Checkbox(todo.completed).apply {                                  //1
    addValueChangeListener { todo.completed = it.value }          //2
}</pre>

1. Initialize a new `Checkbox` component with a value
2. When the value of the checkbox changes, execute the lambda - we change the underlying model's value

There's no need for JavaScript code; Vaadin manages the interaction independently.

Conclusion {#h2-5-conclusion}
-----------------------------

The post was but a short introduction to Vaadin in the context of AJAX and SSR.

Most developers who learn programming on web apps and are thus used to the request-response model react poorly when exposed to Vaadin. Their main argument is the absence of API. IMHO, it's a benefit: some apps, particularly business apps, don't evolve to the point where you'll need to develop dedicated mobile clients.

Vaadin comes with a default CSS set, as stated in the introduction. This default theme ensures Vaadin applications look good from the start, providing users with a comfortable and visually appealing work environment. However, you can always integrate another or even develop your own.

The real benefit, however, is found again at the organizational level. In the introductory post, I mentioned that separating frontend and backend development creates issues during their integration. Because Vaadin does not have such a separation, project planning is more predictable, as there is no integration step between the front end and back end. Likewise, theming can happen in parallel to development.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/compare-frontends).

**To go further:**

* [Vaadin](https://vaadin.com/)
* [What is Flow?](https://vaadin.com/docs/latest/flow/what-is-flow)
* [More Vaadin](https://morevaadin.com/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/6/) on October 13^th^, 2024*
