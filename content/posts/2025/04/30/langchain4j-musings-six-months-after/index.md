---
title: "LangChain4J musings, six months after"
date: "2025-04-30T17:36:31+00:00"
lastmod: "2026-09-21T06:02:51+00:00"
description: "Last year, I started to dig a bit around LangChain4J. It's a fast-growing project, and I wanted to get familiar with the updates. I also wanted to check…"
canonical: "https://blog.frankel.ch/langchain4j-musings-six-months-after/"
authors:
  - "nicolas-frankel"
image: "cover-1-1024x683.jpg"
categories:
  - "LangChain4j"
related_posts:
  - "foojay-podcast-74"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "jc-ai-newsletter-15"
  - "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
frozen: false
---

Last year, [I started to dig a bit](https://blog.frankel.ch/langchain4j-musings/) around [LangChain4J](https://docs.langchain4j.dev/). It's a fast-growing project, and I wanted to get familiar with the updates. I also wanted to check how to integrate a Model Context Protocol server in LangChain4J.

## Version 1 beta

I wrote my last post in November 2024 and used the latest version available at the time, v0.35. LangChain4J started its journey toward 1.0 last December.

|         Date         |   Release    |
|----------------------|--------------|
| September 25th, 2024 | 0.35.0       |
| December 22th, 2024  | 1.0.0-alpha1 |
| February 10th, 2025  | 1.0.0-beta1  |
| March 13th, 2025     | 1.0.0-beta2  |
| April 12th, 2025     | 1.0.0-beta3  |

LangChain4J follows [SemVer](https://semver.org/). Maintainers used the occasion to introduce breaking changes. In my case, I had to update my code to account for breaking API changes.

|           v0.35            |        v1.0.0-beta3        |
|----------------------------|----------------------------|
| 

```kotlin
val s = Sinks.many()
             .unicast()
             .onBackpressureBuffer<String>()
chatBot.talk(m.sessionId, m.text)
       .onNext(s::tryEmitNext)
       .onError(s::tryEmitError)
       .onComplete {
           s.tryEmitComplete()
       }.start()
return ServerResponse.ok().bodyAndAwait(
    s.asFlux().asFlow()
)
```

 | 

```kotlin
val s = Sinks.many()
             .unicast()
             .onBackpressureBuffer<String>()
chatBot.talk(m.sessionId, m.text)
       .onPartialResponse(s::tryEmitNext)
       .onError(s::tryEmitError)
       .onCompleteResponse {
           s.tryEmitComplete()
       }.start()
return ServerResponse.ok().bodyAndAwait(
    s.asFlux().asFlow()
)
```

 |

## Project Reactor integration

LangChain4J offers a Project Reactor integration; I missed it in my previous musings. With Kotlin coroutines, it simplifies the code **a lot**.

I'm using `AiServices`, so I previously defined an interface for LangChain4J to implement at runtime:

```kotlin
interface ChatBot {
    fun talk(@MemoryId sessionId: String, @UserMessage message: String): TokenStream
}
```

We should add the following dependency:

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-reactor</artifactId>
    <version>1.0.0-beta3</version>
</dependency>
```

We can now change the return type from a `Flux` to a `TokenStream`. Here's the updated signature:

```kotlin
interface ChatBot {
    fun talk(@MemoryId sessionId: String, @UserMessage message: String): Flux<String>
}
```

It makes the creation of the `sink` above unnecessary. We can simplify the code as follows:

```kotlin
val flux = chatBot.talk(m.sessionId, m.text)
ServerResponse.ok().bodyAndAwait(flux.asFlow())
```

Remember that two days of debugging can easily save you two hours of reading the documentation! I didn't do the latter.

## Integrating a Model Context Protocol server

Up to this point, our changes were minimal. In this section, I want to integrate an in my LangChain4J application.

### Retrieval-Augmented Generation

One needs lots and lots of resources to train an : it directly translates into time and money. For this reason, companies limit the training of new model versions. A model's relevancy decreases over time as information accrues and changes, while the LLM's database is immutable. Moreover, LLMs are trained on public data–by nature, while most companies want to query their private data too.

Retrieval Augmented Generation was the traditional way to cope with these limits. is a two-step process. In the first step, the tool parses data, vectorizes them according to the LLM, and stores it in a vector database; in the second, the tool uses the database as additional data when querying the LLM.

### Model Context Protocol

The most recent way to handle the static nature of LLMs is MCP.
> MCP is an open protocol that standardizes how applications provide context to LLMs. Think of MCP like a USB-C port for AI applications. Just as USB-C provides a standardized way to connect your devices to various peripherals and accessories, MCP provides a standardized way to connect AI models to different data sources and tools.
>
> -- [Get started with the Model Context Protocol](https://modelcontextprotocol.io/introduction)

MCP has two benefits over RAG:

* Data processed by a RAG is tailored for a model. If one wants to use a new model, one must re-execute the parsing phase. MCP standardizes the interactions between a client and a server, making them technology-independent.
* RAG allows the reading of data. MCP allows any API call to either access data dynamically **or execute actions**!

MCP defines [two transport alternatives](https://modelcontextprotocol.io/specification/2024-11-05/basic/transports) for client-server communications:

* stdio: The client launches a subprocess, and communication happens over standard in and standard out
* HTTP with

### Architecting the solution

After the above theory, we are now ready for the hands-on part. It starts by choosing an MCP server. [Here](https://mcp.so/)'s a good starting point. However, I chose the [official GitHub MCP server](https://github.com/github/github-mcp-server) because the LangChain4J documentation mentions it.

The GitHub MCP server offers the *stdio* transport. It means we should get the binary and start it from the application. It's fast compared to the HTTP transport, but given the overall time comprising the HTTP call to the model and the compute time on its side, it's irrelevant. From an architecture point of view, I'd prefer a dedicated component with its process.

After some research, I found the [mcp-proxy](https://github.com/sparfenyuk/mcp-proxy) project. It lets you switch between either from stdio to HTTP or from HTTP to stdio. It's also available as a Docker image. We can combine both the server and the proxy with the following `Dockerfile`:

```dockerfile
FROM ghcr.io/sparfenyuk/mcp-proxy:latest

ENV VERSION=0.2.0
ENV ARCHIVE_NAME=github-mcp-server_Linux_x86_64.tar.gz

RUN wget https://github.com/github/github-mcp-server/releases/download/v$VERSION/$ARCHIVE_NAME -O /tmp/$ARCHIVE_NAME \ #1
    && tar -xzvf /tmp/$ARCHIVE_NAME -C /opt \                      #2
    && rm /tmp/$ARCHIVE_NAME                                       #3

RUN chmod +x /opt/github-mcp-server                                #4
```

1. Download the archive
2. Extract it
3. Remove the archive
4. Make the binary executable

Note that we can't define the `CMD` as the binary only allows configuring the port and the host with parameters. For this reason, we must defer the command at runtime, or in my case, in the `docker-compose.yaml`:

```yaml
services:
  mcp-server:
    build:
      context: github-mcp-server
    env_file:
      - .env                                                       #1
    command:
      - --pass-environment                                         #2
      - --sse-port=8080                                            #3
      - --sse-host=0.0.0.0                                         #4
      - --                                                         #5
      - /opt/github-mcp-server                                     #6
      - --toolsets
      - all
      - stdio
```

1. We need a `GITHUB_PERSONAL_ACCESS_TOKEN` environment variable with a valid token to authenticate on GitHub
2. Pass all environment variables to the subprocess
3. Set the listening port
4. Bind to any IP
5. The proxy "connects" to the stdio MCP server after the dash
6. Run the server with all options enabled

The image will provide the `/sse` endpoint on port 8080.

### Coding the solution

The coding part is the easiest. Head down to the [LangChain4J documentation on MCP](https://docs.langchain4j.dev/tutorials/mcp) and follow along. In the project, it translates as the following:

```kotlin
bean {
    val transport = HttpMcpTransport.Builder()
        .sseUrl(ref<ApplicationProperties>().mcp.url)              //1
        .logRequests(true)                                         //2
        .logResponses(true)                                        //2
        .build()
    val mcpClient = DefaultMcpClient.Builder()
        .transport(transport)
        .build()
    mcpClient.listTools().forEach { println(it) }                  //3
    McpToolProvider.builder()
        .mcpClients(listOf(mcpClient))
        .build()
}
bean {
    coRouter {
        val chatBot = AiServices
            .builder(ChatBot::class.java)
            .streamingChatLanguageModel(ref<StreamingChatLanguageModel>())
            .chatMemoryProvider { MessageWindowChatMemory.withMaxMessages(40) }
            .contentRetriever(EmbeddingStoreContentRetriever.from(ref<EmbeddingStore<TextSegment>>()))
            .toolProvider(ref<McpToolProvider>())                  //4
            .build()
        POST("/")(PromptHandler(chatBot)::handle)
    }
}
```

1. I added a `ConfigurationProperty` class to parameterize the SSE URL
2. The MCP protocol provides a way to send logs back to the client
3. Not necessary, but it helped me to make sure the client connected to the server and could list the tools provided
4. Plug in the MCP tool provider created above in the `AiServices`

At this point, the model should forward a request that matches any of the registered tools to the MCP server.

```bash
curl -N -H 'Content-Type: application/json' localhost:8080 -d '{ "sessionId": "1", "text": "What are my top three most popular GitHub repos?" }'
```

I tried multiple times, and I got answers along these lines:

```
Unfortunately, the provided text does not contain any information about your top three most popular GitHub repositories. The text appears to be a blog post or a personal website, and it mentions some of your projects and experiences with GitHub, but it does not provide any metrics or statistics on the popularity of your repositories.

If you want to know more about the popularity of your GitHub repositories, I would recommend checking out GitHub's own analytics tools, such as GitHub Insights or the Repository Insights API. These tools can provide information about the number of followers, stars, and forks for each repository, as well as other metrics like engagement and activity.
```

The model just ignored the tools despite the documentation claiming the contrary.

### Fixing the solution

I read the LangChain4J documentation a couple of times but to no avail. I tried to use OpenAI and a handful of other AI tools with no success. Most of the answers confirmed it should work out of the box. Some mentioned calling the tool directly, which defeats the purpose; one mentioned that Ollama didn't support tools. I checked the Ollama blog: it announced tools' support in 2024. I was stuck for nearly a day, wondering what I did wrong.

The decoupled architecture introduces more moving pieces. I suspected something might be wrong in the whole call chain. I removed the MCP proxy, added the `github-mcp-server` directly to the application image, and changed the code from HTTP to stdio. It didn't fix the issue.

I was about to abandon when I decided to come back to the roots. I copied-pasted the [sample from the documentation](https://docs.langchain4j.dev/tutorials/mcp#developing-the-tool-provider): it just worked! It was my ha-ha moment.

The sample uses OpenAI while I was using Ollama. I tried MCP with OpenAI, Mistral AI, and Ollama. Only the OpenAI model works with MCP. I sent the same request as above:

```bash
curl -N -H 'Content-Type: application/json' localhost:8080 -d '{ "sessionId": "1", "text": "What are my top three most popular GitHub repos?" }'
```

Now, OpenAI correctly maps the request to the correct tool and returns the answer I was expecting:

```
Here are my findings regarding your top three most popular GitHub repositories:

1. **[opentelemetry-tracing](https://github.com/nfrankel/opentelemetry-tracing)**  
   - **Description**: Demo for end-to-end tracing via OpenTelemetry.  
   - **Stars**: 68  
   - **Forks**: 25  
   - **Open Issues**: 10  

2. **[kaadin](https://github.com/nfrankel/kaadin)**  
   - **Description**: Kotlin DSL for Vaadin.  
   - **Stars**: 44  
   - **Forks**: 12  
   - **Open Issues**: 3  

3. **[jvm-controller](https://github.com/nfrankel/jvm-controller)**  
   - **Description**: Example on how to write a Kubernetes controller in Java.  
   - **Stars**: 33  
   - **Forks**: 10  
   - **Open Issues**: 0  

These repositories demonstrate a range of your interests and contributions in the areas of observability, Kotlin development, and Kubernetes.%
```

Because we pass an authentication token to the MCP server, which passes it to the GitHub API, the latter knows which user makes the call. Hence, it can interpret the **my repos** part in the above query. I admit that it's an unusual use case for regular web applications that cater to multiple users, but use a single authentication token each. However, it perfectly fits the use case of a desktop application.

Other regular questions, *e.g.*, find the most popular repositories on GitHub, are relevant to web applications, as they don't have implicit context–the user.

## Conclusion

The main focus of this post is the integration of an MCP server in a LangChain4J app. While the configuration is straightforward thanks to the documentation, there are a few caveats.

First, how the MCP server fits in your architecture is still up to you. I had to be creative to make it decoupled, using the excellent `mcp-proxy`. Then, LangChain4J appears to be a leaky abstraction. It makes everything possible to provide you with a strong abstraction layer, but the implementations underneath it shields you from are not equal. I wish the documentation would mention it, even though I understand the current version is in beta.

All in all, it was a fun ride. I learned about MCP in the real world, and it opened quite a few doors for project ideas.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/langchain4j-musings).

**To go further:**

* [Get started with the Model Context Protocol](https://modelcontextprotocol.io)
* [Find Awesome MCP Servers and Clients](https://mcp.so/)
* [LangChain4J - Model Context Protocol (MCP)](https://docs.langchain4j.dev/tutorials/mcp)

*Originally published at [A Java Geek](https://blog.frankel.ch/langchain4j-musings-six-months-after/) on April 27^th^, 2025*

*[SSE]: Server-Sent Events
*[RAG]: Retrieval-Augmented Generation
*[MCP]: Model Context Protocol
*[LLM]: Large Language Model
