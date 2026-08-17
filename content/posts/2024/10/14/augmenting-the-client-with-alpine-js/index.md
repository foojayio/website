---
title: "Augmenting the client with Alpine.js"
slug: "augmenting-the-client-with-alpine-js"
date: "2024-10-14T10:59:44+00:00"
lastmod: "2024-10-14T10:59:45+00:00"
description: "Alpine is very similar to Vue, with the notable difference of the lack of templating; components are only available via a price. All other features have an equivalent."
canonical: "https://blog.frankel.ch/ajax-ssr/4/"
authors:
  - "nicolas-frankel"
image: "mountains-6486093.jpg"
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
        <artifactId>alpinejs</artifactId>                       <!--2-->
        <version>3.14.1</version>
    </dependency>
    <dependency>
        <groupId>org.webjars.npm</groupId>
        <artifactId>axios</artifactId>                          <!--1-->
        <version>1.7.3</version>
    </dependency>
</dependencies>
```


1. Same as last week with Vue
2. Alpine instead of Vue

It's similar to Vue's setup.

### Client-side {#h3-2-client-side}

Here's the code on the HTML side:

```html
<script th:src="@{/webjars/axios/dist/axios.js}" src="https://cdn.jsdelivr.net/npm/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="34554c5d5b4774051a03">[email protected]</a>/dist/axios.min.js"></script> <!--1-->
<script th:src="@{/webjars/alpinejs/dist/cdn.js}" src="https://cdn.jsdelivr.net/npm/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f59499859c9b909f86b5c6dbc4c1dbc4">[email protected]</a>/dist/cdn.min.js" defer></script> <!--2-->
<script th:src="@{/alpine.js}" src="../static/alpine.js"></script>  <!--3-->
<script th:inline="javascript">
/*<![CDATA[*/
    window.alpineData = {                                           <!--4-->
        title: /*[[${ title }]]*/ 'A Title',
        todos: /*[[${ todos }]]*/ [{ 'id': 1, 'label': 'Take out the trash', 'completed': false }]
    }
/*]]>*/
</script>
```


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

```javascript
document.addEventListener('alpine:init', () => {                    //1
    Alpine.data('app', () => ({                                     //2
        // The next JavaScript code snippets will be inside the block
    }))
})
```


1. Run the block when the `alpine:init` event is triggered; the triggering event is specific to Alpine.
2. Bootstrap Alpine and configure it to manage the HTML fragment identified by `app`

We now set the `app` id on the HTML side.

```html
<div id="app">
</div>
```


Until now, it's very similar to Vue.js, a straight one-to-one mapping.

Unlike Vue.js, Alpine doesn't seem to have [templates](https://github.com/alpinejs/alpine/issues/414). The official [UI components](https://alpinejs.dev/components) are not free. I found an [Open Source approach](https://github.com/markmead/alpinejs-component), but it's unavailable on WebJars.

### Basic interactions {#h3-5-basic-interactions}

Let's implement the check of the complete checkbox.

Here's the HTML code:

```html
<input type="checkbox" :checked="todo.completed" @click="check(todo.id)"> <!--1-->
<input type="checkbox" :checked="todo.completed" @click="check" />  <!--2-->
```


1. Alpine code
2. Vue code

The code is very similar, with the difference that Alpine allows passing parameters.

On the Javascript side, we must define the function, and that's all:

```javascript
Alpine.data('app', () => ({
    check(id) {
        axios.patch(`/api/todo/${id}`, {checked: event.target.checked})
    }
}))
```


### Client-side model {#h3-6-client-side-model}

You might wonder where the `todo` above comes from. The answer is: from the local model.

We initialize it in the `app` or to be more precise, we initialize the list:

```javascript
Alpine.data('app', () => ({
    title: window.alpineData.title,                                 //1
    todos: window.alpineData.todos,                                 //2
}))
```


1. Initialize the `title` even if it's read-only
2. Initialize the `todos` list;  
   at this point, it's read-only but we are going to update it the next section

### Updating the model {#h3-7-updating-the-model}

In this section, we will implement adding a new `Todo`.

Here's the HTML snippet:

```html
<form>
    <div class="form-group row">
        <label for="new-todo-label" class="col-auto col-form-label">New task</label>
        <div class="col-10">
            <input type="text" id="new-todo-label" placeholder="Label" class="form-control" x-model="label" /> <!--1-->
        </div>
        <div class="col-auto">
            <button type="button" class="btn btn-success" @click="create()">Add</button> <!--2-->
        </div>
    </div>
</form>
```


1. The `x-model` defines a model and binds the `label` property defined in `app`
2. Define the behavior of the button, as in the previous section

The related code is the following:

```javascript
Alpine.data('app', () => ({
    label: '',                                                      //1
    create() {
        axios.post('/api/todo', {label: this.label}).then(response => { //2
            this.todos.push(response.data)                          //3
        }).then(() => {
            this.label = ''                                         //4
        })
    }
}))
```


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



*Originally published at [A Java Geek](https://blog.frankel.ch/ajax-ssr/4/) on September 29^th^, 2024*
