---
title: "AI-based Images generation with Quarkus and OpenAI DALL.E"
slug: "images-generation-with-quarkus-and-openai"
date: "2023-08-21T07:38:07+00:00"
lastmod: "2023-08-21T07:39:49+00:00"
description: "We explore image generation with Quarkus and OpenAI using the new REST Client Reactive to invoke the OpenAI DALL.E API."
authors:
  - "anto_perrone"
image: "/images/posts/2023/08/images-generation-with-quarkus-and-openai/pic01-1.png"
categories:
  - "Jakarta EE"
  - "Java"
  - "Machine Learning"
  - "Tutorials"
tags:
related_posts:
  - "book-review-quarkus-for-spring-developers"
  - "native-graphql-api-with-neo4j-auradb-on-heroku"
  - "native-image-quarkus"
  - "why-we-moved-our-timefold-java-worker-pods-from-amd-to-arm64"
enlighterjs: true
frozen: false
---

**In this article, we explore how to integrate OpenAI API with Quarkus. We will create a Quarkus application using the new REST Client Reactive to invoke the OpenAI DALL.E API for images generation.**

OpenAI API Overview {#h2-0-openai-api-overview}
-----------------------------------------------

Before jumping into the code, let's explore the OpenAI [Create Image](https://platform.openai.com/docs/api-reference/images/create) API and how it works.

### Create Image Request {#h3-1-create-image-request}

The body request is made of the following parameters:

* **prompt**: the only required parameter is the description of the desired image (max 1000 characters)
* **n**: number of desired images (from 1 to 10)
* **size** : the size of a generated image (default 1024x1024, also admitted 256x256, 512x512)  
  other parameters are:
* **response_format**: to specify the response image format (default url, also admitted b64_json)
* **user**: identifier to track user activities.

To authenticate with the API, we'll generate an [API key](https://platform.openai.com/account/api-keys). We'll set this key in the Authorization header while calling the API.  

Here is a sample API request:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl https://api.openai.com/v1/images/generations \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $OPENAI\_API\_KEY" \
-d '{
   "prompt": "A cute baby sea otter",
   "n": 2,
   "size": "1024x1024"
}'</pre>

### Create Image Response {#h3-2-create-image-response}

The response will be a JSON object with *created* and *data* fields. The *data* field will be an array of objects. Each object will have a *url* field containing the response to the prompt.

The number of objects in the *data* array will equal the optional *n* parameter in the request. If the *n* parameter is not specified, the *data* array will contain a single object.  

Here is a sample API response:

<pre class="EnlighterJSRAW" data-enlighter-language="json" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
"created": 1589478378,
"data": [
   {
      "url": "https://..."
   },
   {
      "url": "https://..."
   }
]
}</pre>

Setting up the Quarkus Application {#h2-3-setting-up-the-quarkus-application}
-----------------------------------------------------------------------------

To startup our application, go to and configure it by selecting the following extensions:

* RESTEasy Reactive Jackson
* RESTEasy Reactive
* REST Client Reactive
* REST Client Reactive Jackson  
  Or using maven with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn io.quarkus.platform:quarkus-maven-plugin:3.2.3.Final:create \
-DprojectGroupId=com.foojay.openai \
-DprojectArtifactId=quarkus-openai-app \
-Dextensions='resteasy-reactive-jackson,rest-client-reactive-jackson,resteasy-reactive' \
-DnoCode</pre>

After creating the project, add your OpenAI API Key with the following line inside the *application.properties* file.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># OpenAI properties 
openai.api.key=$OPENAI_API_KEY</pre>

### The Model {#h3-4-the-model}

Now starting from what we have seen before, we create a class to model the API request body of OpenAI Image Generation API:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class CreateImageRequest {

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("n")
    private int n;

    @JsonProperty("size")
    private String size;

    public CreateImageRequest() {
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}</pre>

Let's define the API response class:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class CreateImageResponse {

    @JsonProperty("created")
    private Integer created;

    @JsonProperty("data")
    private List&lt;Item&gt; urls;

    public List&lt;Item&gt; getUrls() {
        return urls;
    }

    public void setUrls(List&lt;Item&gt; urls) {
        this.urls = urls;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }
}</pre>

And finally, the *Item* class

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Item {
    @JsonProperty("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}</pre>

### REST Client {#h3-5-rest-client}

To invoke the OpenAI API we define our REST client using the new REST Client Reactive, which allows a declarative way to define the API to be invoked using the new Jakarta EE and Microprofile annotations.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterRestClient(baseUri = "https://api.openai.com")
@Path("/v1")
public interface OpenAIRestClient {

        @GET
        @Path("/models")
        @ClientHeaderParam(name = "Authorization", value = "Bearer ${openai.api.key}")
        ModelResponse getModels();

        @POST
        @Path("/images/generations")
        @ClientHeaderParam(name = "Authorization", value = "Bearer ${openai.api.key}")
        CreateImageResponse generateImage(CreateImageRequest createImageRequest);

}</pre>

The `@RegisterRestClient` allows Quarkus to know that this interface should be available for CDI injection as a REST Client, the baseUri properties define the url the client point to. `@Path`, `@POST` are standard Jakarta REST annotations defining how to access the API.

To manage API auth, as we have seen before, we need to pass the API KEY inside the header of the request. To do this in our client, we use the `@ClientHeaderParam` passing the name and the value of the entries. In this case, we read the key from the *application.properties* using the notation *${opena.ai.key}* that allows injecting the properties' value directly.

### Testing the client {#h3-6-testing-the-client}

To test our client, let's define a REST resource that we will call from out browser.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Path("/quarkus-openai")
public class OpenAIEndpoint {

    @RestClient
    private OpenAIRestClient openAIRestClient;

    @GET
    @Path("/generate-image")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateImage(@QueryParam("description") String description,
            @DefaultValue("1") @QueryParam("n") int n,
            @DefaultValue("1024x1024") @QueryParam("size") String size) {

        final CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setPrompt(description);
        createImageRequest.setN(n);
        createImageRequest.setSize(size);

        URI uri;
        try {
            uri = new URI(
                    openAIRestClient.generateImage(createImageRequest).getUrls().get(0).getUrl());
        } catch (URISyntaxException e) {
            return Response.noContent().build();
        }
        return Response.seeOther(uri).build();
    }
}</pre>

We define `OpenAIEndpoint` and inject the REST client using `@RestClient` annotation. Then we create the API `generateImage` that accepts the prompt and optionally the number of images and their size.

Inside the method, we build the request and pass it to our client; calling the method `generateImage`, we get the result and perform a redirect to the first URL of the response payload.

To see the result let's start our project with the command:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">quarkus dev</pre>

and point the browser to this example url passing our prompt as the value of *description* parameters:

[http://localhost:8080/quarkus-openai/generate-image?description=a%20photo%20of%20a%20happy%20corgi%20puppy%20sitting%20in%20front%20of%20sea](http://localhost:8080/quarkus-openai/generate-image?description=a%20photo%20of%20a%20happy%20corgi%20puppy%20sitting%20and%20facing%20forward,%20studio%20light,%20longshot)

and the output will be:
![](/images/posts/2023/08/images-generation-with-quarkus-and-openai/pic01.png)

Try with other prompts and enjoy the results.

Conclusion {#h2-7-conclusion}
-----------------------------

This article we explored how to use the OpenAI DALL.E API to generate images from prompts. We created a Quarkus application that calls the API using the new REST Client Reactive and manage the result.

The code examples for this tutorial are available on [GitHub](https://github.com/fushji/quarkus-openai-app).

References {#h2-8-references}
-----------------------------

* [OpenAI DALL.E API Reference](https://platform.openai.com/docs/api-reference/images/create)
* [Quarkus REST Client Reactive](https://quarkus.io/guides/rest-client-reactive)
