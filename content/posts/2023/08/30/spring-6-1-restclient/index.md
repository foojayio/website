---
title: "Spring 6.1 - RestClient"
slug: "spring-6-1-restclient"
date: "2023-08-30T16:23:40+00:00"
lastmod: "2023-08-31T17:46:29+00:00"
description: "Getting started making use of the new RestClient introduced in Spring 6.1!"
authors:
  - "simon-verhoeven"
image: "1024px-Spring_Framework_Logo_2018.svg.png"
categories:
  - "Spring"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "annotation-free-spring"
  - "jurassic-jdk-migrate-or-extinct"
enlighterjs: true
frozen: false
---

As you might have read in this [blogpost](https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient), Spring is introducing a `RestClient` in Spring 6.1 to interact with HTTP backends.

Now some of you might be wondering as to the why, given we already have a plethora of other options such as `RestTemplate`, `WebClient`, `HttpUrlConnection`, ...​

As we can see on the [javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) page `RestTemplate` got quite massive over time.

The Spring team drew lessons from this, and the reactive `WebClient` was created with a fluent interface.

Now, one can certainly use this one in place of `RestTemplate`, but that means dragging in extra dependencies, and well `bodyToMono` looks a bit "scary" the first time you see it, doesn't it?

So the Spring team decided to introduce the `RestClient` which:

* offers a fluent API
* can be used with HTTP interfaces introduced in 6.0 (formerly only with `WebClient`)
* allows us to achieve the same results as `RestTemplate`
* uses a synchronous HTTP client

Now let's have some fun with it, and please do feel free to check out [the repository](https://github.com/SimonVerhoeven/restclient-demo)!

Usage {#_usage}
---------------

### Using rest client builder {#_using_rest_client_builder}

We start out by creating our rest client:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public JokeController(RestClient.Builder restClientBuilder, @Value("${jokeserviceUrl}") String jokeserviceUrl) {
    this.restClient = restClientBuilder.baseUrl(jokeserviceUrl).build();
}</pre>

And then we can fetch a joke using:

<pre class="EnlighterJSRAW" data-enlighter-language="java">return this.restClient
    .get()
    .uri("/j/{jokeId}", "R7UfaahVfFd")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(Joke.class);</pre>

Which as you can see is well, a lot more fluid.

When doing this call we'll be rewarded with:

<pre class="EnlighterJSRAW" data-enlighter-language="json">{
    "id": "R7UfaahVfFd",
    "joke": "My dog used to chase people on a bike a lot. It got so bad I had to take his bike away.",
    "status": 200
}</pre>

Now in case we're interested in the whole response including the status code, we can replace `.body(Joke.class)` with `.toEntity(Joke.class)`.  

Or in case of a call where the response does not interest us, we can use `.toBodilessEntity()`.

### Using a declarative HTTP interface {#_using_declarative_http_interface}

Now as mentioned at the start, one of the upsides of the new RestClient is that we can also use it for declarative HTTP clients.

Let's declare an interface to fetch a joke:

<pre class="EnlighterJSRAW" data-enlighter-language="java">interface JokeClient {
    @GetExchange(url = "/j/{jokeId}", accept = "application/json")
    Joke getJoke(@PathVariable String jokeId);
}</pre>

Then we can create our client using:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
jokeClient = factory.createClient(JokeClient.class);</pre>

After that, we can easily consume it using

<pre class="EnlighterJSRAW" data-enlighter-language="java">@GetMapping("/joke-using-interface")
Joke getJokeUsingInterface() {
    return this.jokeClient.getJoke("M7wPC5wPKBd");
}</pre>

And we'll receive:

<pre class="EnlighterJSRAW" data-enlighter-language="json">{
    "id": "M7wPC5wPKBd",
    "joke": "Did you hear the one about the guy with the broken hearing aid? Neither did he.",
    "status": 200
}

</pre>

### Error handling {#h3-3-error-handling}

By default, RestClient will throw a subclass of RestClientException upon a `4**` or `5**` status code, but we can override this using `onStatus` so that we can define our own status handlers:

<pre class="EnlighterJSRAW" data-enlighter-language="java">.onStatus(HttpStatusCode::is4xxClientError, ((request, response) -&gt; {
    throw new PunException();
}))</pre>

### More granular control {#h3-4-more-granular-control}

In some cases, we might want to do some more advanced things for which we need access to the underlying HTTP request or HTTP response. This can be achieved by using`exchange`.

**note**: Status handlers are not applied when using exchange as you already have full access to the response, so you can perform any needed error handling.

<pre class="EnlighterJSRAW" data-enlighter-language="java">return this.restClient
    .get()
    .uri("/j/{jokeId}", "UNKNOWN")
    .accept(MediaType.APPLICATION_JSON)
    .exchange((clientRequest, clientResponse) -&gt;  {
        // some criteria to determine our joke's too punny
        return "Singing in the shower is fun until you get soap in your mouth. Then it's a soap opera";
    });</pre>

Wrap-up {#_wrap_up}
-------------------

I hope this sheds some light on the how, and why. And if you're adding `Webflux` just to make use of `WebClient` please consider changing to `RestClient`.

In case you want to read a bit more about this:

* [RestClient introduction blogpost](https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient)
* [RestTemplate javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
