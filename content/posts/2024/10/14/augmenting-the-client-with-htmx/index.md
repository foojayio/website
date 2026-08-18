---
title: "Augmenting the client with HTMX"
slug: "augmenting-the-client-with-htmx"
date: "2024-10-14T12:37:39+00:00"
lastmod: "2024-10-14T12:37:41+00:00"
description: "In the two previous articles, I described Vue and Alpine. We configured Spring Boot to return JSON. With HTMX, we configured it to return HTML."
canonical: "https://blog.frankel.ch/ajax-ssr/5/"
authors:
  - "nicolas-frankel"
image: "Favicon-3-2.png"
categories:
  - "Research"
tags:
related_posts:
  - "a-short-history-of-ajax-and-ssr"
  - "augmenting-the-client-with-vue-js"
  - "augmenting-the-client-with-alpine-js"
enlighterjs: true
frozen: false
---

This article is part of a series comparing different ways to implement asynchronous requests on the client to augment the latter. So far, I described the process with [Vue.js](https://foojay.io/today/augmenting-the-client-with-vue-js/) and [Alpine.js](https://foojay.io/today/augmenting-the-client-with-alpine-js/). Both are similar from the developers' point of view: they involve JavaScript.

In this post, I'll focus on [HTMX](https://htmx.org/), whose approach is quite different.

## Laying out the work

I'll follow the same structure as in the previous posts of the series. Here's the setup, server- and client-side.

### Server-side

Here is how I integrate Thymeleaf and HTMX in the POM:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>        <!--1-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>  <!--1-->
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>webjars-locator</artifactId>                <!--1-->
        <version>0.52</version>
    </dependency>
    <dependency>
        <groupId>org.webjars.npm</groupId>
        <artifactId>htmx.org</artifactId>                       <!--2-->
        <version>2.0.1</version>
    </dependency>
</dependencies>
```

1. Same as with previous frameworks
2. The HTMX dependency

### Client-side

The code on the HTML side is straightforward :

```html
<script th:src="@{/webjars/htmx.org/dist/htmx.js}" src="https://cdn.jsdelivr.net/npm/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="19716d746137766b7e59283720">[email protected]</a>/dist/htmx.min.js"></script> <!--1-->
```

1. Add the HTMX dependency

## Working with HTMX

We want to implement the same features as previously.

HTMX implements a radical approach that is different from traditional AJAX frameworks. They force you to develop an HTTP API that accepts and returns JSON. With HTMX, you return **HTML fragments** instead. HTMX uses it to replace the DOM elements that you configured.

Hence, you need to write neither JavaScript nor deal with JSON and serialization of entities.

### Designing the fragments

HTMX nicely complements Thymeleaf because both work with page fragments. We can align Thymeleaf's fragments to HTMX's responses. It requires thinking ahead, which differs from the previous AJAX/API/JSON standard, but it's worth it.

Let's list interactions and what fragment we replace for each of them:

* Load the page: the whole page is rendered server-side, it's not asynchronous
* Click the completed checkbox on a line: the line is replaced with the underlying todo's new state. We didn't do anything client-side with previous frameworks; we will ignore it as well.
* Clean up completed tasks: replace the todo table's lines with lines of uncompleted tasks
* Add a new todo: replace the table with all lines plus the new one and replace the label field with an empty field to reset it. The Add field and button were in the table's footer in previous designs. There's no reason to change this.

Here's the conceptual fragments design for our app:

```
-------------------- APP --------------------
| index.html                                |
|                                           |
|  ---------------- TABLE ----------------  |
|  | table.html                          |  |
|  |                                     |  |
|  |  ------------- LINES -------------  |  |
|  |  | lines.html                    |  |  |
|  |  |                               |  |  |
|  |  ---------------------------------  |  |
|  ---------------------------------------  |
---------------------------------------------
```

I'll split the HTML page into these fragments. Because we render them via Thymeleaf, we can split each into their dedicated file for a cleaner separation. At initial load time, we use Thymeleaf's `replace` directive; we use HTMX for asynchronous client-side interactions.

### Our first interaction

We will start with the cleanup feature, as it's the easiest one with HTMX.

Here's the HTML code:

```html
<tbody id="lines">...</tbody>                                       <!--1-->

<button class="btn btn-warning"
        hx-trigger="click"                                          <!--2-->
        hx-delete="/htmx/todo:cleanup"                              <!--3-->
        hx-target="#lines">                                         <!--4-->
    Cleanup
</button>
```

1. Define the `lines` DOM element
2. HTMX triggers on the `click` event
3. HTMX will send a `DELETE` HTTP request to the URL
4. When the HTML fragment response comes back, HTMX replaces the `lines` DOM element with it

Note that there's **no explicit JavaScript** involved, not a single line of code. HTMX takes care of it.

On the server side, the code is the following:

```kotlin
fun htmx(todos: MutableList<Todo>) = router {
    DELETE("/htmx/todo:cleanup") {
        todos.removeIf { it.completed }                             //1
        ok().render("htmx/lines", mapOf("todos" to todos))          //2
    }
}
```

1. Regular cleanup
2. Use the `render()` function, instead of `body()` for API calls. Because of our previous file split, we can render only the needed HTML fragment. It uses Thymeleaf for any necessary server-side rendering.

That's the heart of HTMX: bind an HTTP call to a client-side event, and replace the configured DOM element with the server response.

Adding a new todo follows the same principle, but the DOM element is the whole table to reset the `label` value. If interested in the complete, look at [the code](https://github.com/ajavageek/compare-frontends).

### Marking a todo complete

While I mentioned that we will not return anything from the check request, it presents an exciting challenge. That's the reason why I am only addressing it now.

We have two challenges when clicking on the checkbox:

* send the state of the checkbox as the JSON payload to update it server-side
* get and use the ID of the `todo`

HTMX offers the `hx-vals` for the JSON payload. However, the URL is different for each row as we want to include the ID in the path. We must generate it server-side with Thymeleaf. TIL: Thymeleaf can manage **any** HTML attribute prefixed with `th:`: it will process the value as usual and write the attribute's name unprefixed.

```html
<input type="checkbox"
       th:checked="${todo.completed}"                               <!--1-->
       hx-trigger="click"                                           <!--2-->
       th:hx-patch="'/htmx/todo/' + ${todo.id}"                     <!--3-->
       hx-vals='js:{"checked": event.target.checked}' />            <!--4-->
```

1. Regular Thymeleaf syntax to check the box if the `todo` is completed
2. HTMX triggers on `click` events
3. Send a `PATCH` request to the server, with Thymeleaf having replaced the `id` with the value in the HTML previously
4. Static request JSON payload

Note that, as explained above, I ignored the response. In a real-world scenario, you should check/uncheck the checkbox depending on the value returned to avoid keeping the server state and the UI in synch.

## Conclusion

In the two previous articles, I described Vue and Alpine. We configured Spring Boot to return JSON. With HTMX, we configured it to return HTML. Additionally, we didn't need any JavaScript code to send the requests from the client.

Icing on the cake, there's a great synergy between Thymeleaf and HTMX: we can split the page into fragments and reuse them on both sides.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/compare-frontends).

**To go further:**

* [HTMX](https://htmx.org/)

*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/5/) on October 6^th^, 2024*
