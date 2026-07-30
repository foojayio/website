---
title: "Augmenting the client with Alpine.js"
slug: "augmenting-the-client-with-alpine-js"
date: "2024-10-14T10:59:44+00:00"
lastmod: "2024-10-14T10:59:45+00:00"
description: "Alpine is very similar to Vue, with the notable difference of the lack of templating; components are only available via a price. All other features have an equivalent."
canonical: "https://blog.frankel.ch/ajax-ssr/4/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2024/10/augmenting-the-client-with-alpine-js/mountains-6486093.jpg"
categories:
  - "Developer Tools"
  - "Spring"
  - "Tools"
tags:
related_posts:
  - "a-short-history-of-ajax-and-ssr"
  - "server-side-rendering-with-spring-boot"
  - "augmenting-the-client-with-vue-js"
  - "web-caching-server"
enlighterjs: true
frozen: false
---

This article is part of a series comparing different ways to implement asynchronous requests on the client, which is colloquially known as AJAX. I dedicated the [previous post](https://foojay.io/today/augmenting-the-client-with-vue-js/) to Vue.js; I'll dedicate this one to [Alpine.js](https://alpinejs.dev/) - not to be confused with Alpine Linux.

I'll follow the same structure as previously.

Laying out the work {#h2-0-laying-out-the-work}
-----------------------------------------------

Here's the setup, server- and client-side.

### Server-side {#h3-1-server-side}

Here is how I integrate Thymeleaf and Alpine.js in the POM:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;        &lt;!--1--&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-thymeleaf&lt;/artifactId&gt;  &lt;!--1--&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars&lt;/groupId&gt;
        &lt;artifactId&gt;webjars-locator&lt;/artifactId&gt;                &lt;!--1--&gt;
        &lt;version&gt;0.52&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars.npm&lt;/groupId&gt;
        &lt;artifactId&gt;alpinejs&lt;/artifactId&gt;                       &lt;!--2--&gt;
        &lt;version&gt;3.14.1&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.webjars.npm&lt;/groupId&gt;
        &lt;artifactId&gt;axios&lt;/artifactId&gt;                          &lt;!--1--&gt;
        &lt;version&gt;1.7.3&lt;/version&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

1. Same as last week with Vue
2. Alpine instead of Vue

It's similar to Vue's setup.

### Client-side {#h3-2-client-side}

Here's the code on the HTML side:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;script th:src="@{/webjars/axios/dist/axios.js}" src="https://cdn.jsdelivr.net/npm/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="34554c5d5b4774051a03">[email&nbsp;protected]</a>/dist/axios.min.js"&gt;&lt;/script&gt; &lt;!--1--&gt;
&lt;script th:src="@{/webjars/alpinejs/dist/cdn.js}" src="https://cdn.jsdelivr.net/npm/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f59499859c9b909f86b5c6dbc4c1dbc4">[email&nbsp;protected]</a>/dist/cdn.min.js" defer&gt;&lt;/script&gt; &lt;!--2--&gt;
&lt;script th:src="@{/alpine.js}" src="../static/alpine.js"&gt;&lt;/script&gt;  &lt;!--3--&gt;
&lt;script th:inline="javascript"&gt;
/*&lt;![CDATA[*/
    window.alpineData = {                                           &lt;!--4--&gt;
        title: /*[[${ title }]]*/ 'A Title',
        todos: /*[[${ todos }]]*/ [{ 'id': 1, 'label': 'Take out the trash', 'completed': false }]
    }
/*]]&gt;*/
&lt;/script&gt;</pre>

1. [Axios](https://axios-http.com/) helps making HTTP requests
2. Alpine itself
3. Our client-side code
4. Set the data

As for the POM, it's the same code for Alpine as for Vue.

The Alpine code {#h2-3-the-alpine-code}
---------------------------------------

We want to implement the same features as for Vue.

### Our first steps into Alpine {#h3-4-our-first-steps-into-alpine}

The first step is to bootstrap the framework. We already added the link to our custom `alpine.js` file above.

<pre class="EnlighterJSRAW" data-enlighter-language="js">document.addEventListener('alpine:init', () =&gt; {                    //1
    Alpine.data('app', () =&gt; ({                                     //2
        // The next JavaScript code snippets will be inside the block
    }))
})</pre>

1. Run the block when the `alpine:init` event is triggered; the triggering event is specific to Alpine.
2. Bootstrap Alpine and configure it to manage the HTML fragment identified by `app`

We now set the `app` id on the HTML side.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;div id="app"&gt;
&lt;/div&gt;</pre>

Until now, it's very similar to Vue.js, a straight one-to-one mapping.

Unlike Vue.js, Alpine doesn't seem to have [templates](https://github.com/alpinejs/alpine/issues/414). The official [UI components](https://alpinejs.dev/components) are not free. I found an [Open Source approach](https://github.com/markmead/alpinejs-component), but it's unavailable on WebJars.

### Basic interactions {#h3-5-basic-interactions}

Let's implement the check of the complete checkbox.

Here's the HTML code:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;input type="checkbox" :checked="todo.completed" @click="check(todo.id)"&gt; &lt;!--1--&gt;
&lt;input type="checkbox" :checked="todo.completed" @click="check" /&gt;  &lt;!--2--&gt;</pre>

1. Alpine code
2. Vue code

The code is very similar, with the difference that Alpine allows passing parameters.

On the Javascript side, we must define the function, and that's all:

<pre class="EnlighterJSRAW" data-enlighter-language="js">Alpine.data('app', () =&gt; ({
    check(id) {
        axios.patch(`/api/todo/${id}`, {checked: event.target.checked})
    }
}))</pre>

### Client-side model {#h3-6-client-side-model}

You might wonder where the `todo` above comes from. The answer is: from the local model.

We initialize it in the `app` or to be more precise, we initialize the list:

<pre class="EnlighterJSRAW" data-enlighter-language="js">Alpine.data('app', () =&gt; ({
    title: window.alpineData.title,                                 //1
    todos: window.alpineData.todos,                                 //2
}))</pre>

1. Initialize the `title` even if it's read-only
2. Initialize the `todos` list;  
   at this point, it's read-only but we are going to update it the next section

### Updating the model {#h3-7-updating-the-model}

In this section, we will implement adding a new `Todo`.

Here's the HTML snippet:

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;form&gt;
    &lt;div class="form-group row"&gt;
        &lt;label for="new-todo-label" class="col-auto col-form-label"&gt;New task&lt;/label&gt;
        &lt;div class="col-10"&gt;
            &lt;input type="text" id="new-todo-label" placeholder="Label" class="form-control" x-model="label" /&gt; &lt;!--1--&gt;
        &lt;/div&gt;
        &lt;div class="col-auto"&gt;
            &lt;button type="button" class="btn btn-success" @click="create()"&gt;Add&lt;/button&gt; &lt;!--2--&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/form&gt;</pre>

1. The `x-model` defines a model and binds the `label` property defined in `app`
2. Define the behavior of the button, as in the previous section

The related code is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="js">Alpine.data('app', () =&gt; ({
    label: '',                                                      //1
    create() {
        axios.post('/api/todo', {label: this.label}).then(response =&gt; { //2
            this.todos.push(response.data)                          //3
        }).then(() =&gt; {
            this.label = ''                                         //4
        })
    }
}))</pre>

1. Define a new `label` property
2. Send a `POST` request with the `label` value as the JSON payload
3. Get the response payload and add it to the local model of `Todo`
4. Reset the `label` value

Conclusion {#h2-8-conclusion}
-----------------------------

Alpine is very similar to Vue, with the notable difference of the lack of templating; components are only available via a price. All other features have an equivalent.

I may need to be corrected because the documentation is less extensive. Also, Vue is much more popular than Alpine.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/compare-frontends).

**To go further:**

* [Alpine.js](https://alpinejs.dev/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/4/) on September 29^th^, 2024*
