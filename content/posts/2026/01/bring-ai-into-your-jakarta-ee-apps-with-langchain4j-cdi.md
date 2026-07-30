---
title: "Bring AI into your Jakarta EE apps with LangChain4J-CDI"
slug: "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
date: "2026-01-24T17:20:02+00:00"
lastmod: "2026-01-26T22:03:09+00:00"
description: "Goal: This article will demonstrate how to add AI features to a Jakarta EE / MicroProfile application using LangChain4J‑CDI, with simple to implement - by Buhake Sindi"
authors:
  - "buhake-sindi"
image: "https://foojay.io/wp-content/uploads/2026/01/image-3-1024x943.png"
categories:
  - "AI"
  - "Jakarta EE"
  - "Java"
  - "LangChain4j"
  - "Library"
  - "LLM"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Goal** : This article will demonstrate how to add AI features to a Jakarta EE / MicroProfile application using **LangChain4J‑CDI**, with simple to implement examples that runs on Payara, WildFly, Open Liberty, Helidon, Quarkus or any CDI 4.x compatible runtime.

***Note:** This is an updated article to the one published on the [JAVAPRO's magazine - "04-2025 \| Java 25 - Special Edition"](https://javapro.io/2025/10/28/04-2025-java-25-special-edition/). Since the release of LangChain4J-CDI version 1.0.0, there's been minor changes, but the fundamental architecture and usage of the library is the same.*

What is LangChain4J-CDI? {#h2-0-what-is-langchain4j-cdi}
--------------------------------------------------------

Langchain4J is a Java library that simplifies the integration of AI and LLMs easier, and with their feature of AI services it provides a declarative and type-safe API for developers to define interfaces that represent AI services, abstracting away the complexities of direct LLM communication

LangChain4J‑CDI is a CDI extension that wires **LangChain4J** components (chat models, embedding models, memories, retrievers, tools) into your application using familiar Jakarta annotations and **MicroProfile Config**. You declare an AI service interface and the extension generates and registers a CDI bean that you can inject anywhere (CDI-managed beans, REST resources, EJBs, Schedulers, etc.).

With these key benefits that LangChain4J-CDI provides, enterprise developers will benefit from:

* **Declarative integration** using CDI annotations. LangChain4J AI services interface annotated with `@RegisterAIService` annotation can be injected as CDI beans.
* **Flexible component configuration**: LangChain4J-CDI config utilizes Microprofile Config to configure various LangChain4J components as config parameters.
* **Visibility and observability**: Observe your LLM input and output AI metrics using observability (Microprofile Telemetry) and service resiliency with Microprofile Fault Tolerance.
* **Portability**: works across Jakarta EE/Microprofile compliant application servers and frameworks.

The **Key features** provided by Langchain4J Microprofile:

1. Build with **CDI at its core** . With `@RegisterAIService` annotation annotated on a Lanchain4J AI service interface, the AI service proxy becomes CDI discoverable bean ready for injection. LangChain4J-CDI provides 2 CDI service extensions that developers can choose from in order to make their AI service CDI discoverable: CDI portable extension or CDI Build Compatible Extension (introduced in CDI 4.0 and higher).
2. **Langchain4J Microprofile config** : Developers can benefit from the power of Microprofile Config to build fundamental elements of LangChain4J such as `ChatModel`/`StreamingChatModel`, `ChatMessage`, `ChatMemory`, `ContentRetriever`, `ToolProvider`(useful for Model Context Protocol, MCP), and more, without requiring developers to write builders to generate such elements.
3. **Langchain4J Microprofile Fault Tolerance** : Leveraging the power of Microprofile Fault Tolerance resilience and policies on your existing Lanchain4J AI services (such as `@Retry`, `@Timeout`, `@RateLimit`, `@Fallback`, etc).
4. **Langchain4J Microprofile Telemetry** : When enabled, developers can observer their LLM metrics (that follows the [Semantic Conventions for GenAI Metrics](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-metrics/)), through Open Telemetry.

***Please note:** Langchain4J-CDI is a module developed by the Microprofile members, initially called SmallRye-LLM. It has since been donated to Langchain. The SmallRye-LLM repo on GitHub has been retired.*

Getting started with LangChain4J-CDI {#h2-1-getting-started-with-langchain4j-cdi}
---------------------------------------------------------------------------------

Langchain4J-CDI provides a [working example](https://github.com/langchain4j/langchain4j-cdi/tree/main/examples) on building a conversational AI agent for a car booking system. This demonstration is inspired by the insightful ["Java meets AI" talk from Lize Raes at Devoxx Belgium 2023](https://youtu.be/BD1MSLbs9KE?si=yBeR8-3IJUYMWd8v) (with further contributions from [Jean-François James](http://jefrajames.fr/). The original demo is from [Dmytro Liubarskyi](https://www.linkedin.com/in/dmytro-liubarskyi/)). Developers can view how the same example are implemented on popular Jakarta EE 10 application servers.

Before we begin, we'll assume that you are familiar with the following:

* Java development.
* Basic knowledge of Maven.
* Basic knowledge of LangChain4J. Lize Raes has written a brilliant article on building an hands-on AI agent with Langchain4J [here](https://javapro.io/2025/04/23/build-ai-apps-and-agents-in-java-hands-on-with-langchain4j/).

We're using OpenLiberty as application server of choice (you can browse the `examples/liberty-car-booking` example from the example link provided above) but you can use any Jakarta EE / Microprofile compliant application server that you're comfortable with (see the examples of other application servers that runs the Car Booking example).

Let's start building our own AI service, purely in Java. Please ensure that your project is Mavenized.

The current release of LangChain4J-CDI, as of the time of writing, is `1.0.0` and supports LangChain4J core version `1.10.0`, and the community version of `1.10.0-beta18` (the latest at this time of writing).

### 1) Add dependencies (Maven) {#h3-2-1-add-dependencies-maven}

We always import the **langchain4j-cdi-core** library as your `dependency`:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;!-- Core CDI integration --&gt;
&lt;dependency&gt;
    &lt;groupId&gt;dev.langchain4j.cdi&lt;/groupId&gt;
    &lt;artifactId&gt;langchain4j-cdi-core&lt;/artifactId&gt;
    &lt;version&gt;${langchain4j.cdi.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

where *${langchain4j.cdi.version}* is the latest LangChain4J CDI version. The LangChain4J-CDI core module automatically depends on the `langchain4j-core` module, so you do not need to explicitly add it as a dependency (unless you want to explicitly specify your own langchain4j-core version yourself).

You also need to import a LangChain4J model provider. For this example we'll use Azure Open AI, thus its LangChain4J Maven artifact ID is `langchain4j-azure-open-ai`.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;
    &lt;artifactId&gt;langchain4j-azure-open-ai&lt;/artifactId&gt;
    &lt;version&gt;${dev.langchain4j.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

where *${dev.langchain4j.version}* is the latest Langchain4J main version (in this case `1.10.0`).

### 2) Configure your model(s) with MicroProfile Config {#h3-3-2-configure-your-model-s-with-microprofile-config}

You can leverage Microprofile Config to define and customize Langchain4J AI service components to be used by your application.

Firstly, we need to add the `langchain4j-cdi-config` module on our Maven project.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;dev.langchain4j.cdi&lt;/groupId&gt;
    &lt;artifactId&gt;langchain4j-cdi-config&lt;/artifactId&gt;
    &lt;version&gt;${langchain4j.cdi.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

where *${langchain4j.cdi.version}* is the latest LangChain4J CDI version. This requires that your application server supports Microprofile Config.

The Langchain4J-CDI **class** configuration follow this pattern:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.&lt;beanName&gt;.&lt;key&gt;=&lt;value&gt;</pre>

The `dev.langchain4j.cdi.plugin..class` property is **mandatory** as it tells CDI which concrete implementation of the LangChain4J AIServices component is to be assigned it to upon CDI registration.

Optionally, to apply the CDI scope to each of your AI service component, set key as **scope** in your configuration. The value is the fully-qualified CDI scope annotation name (one of `@RequestScoped`, `@ApplicationScoped`, `@SessionScoped`, `@Dependent`). The default scope is `@ApplicationScoped`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.&lt;beanName&gt;.scope=jakarta.enterprise.context.ApplicationScoped</pre>

And the class builder **config** configuration follow this pattern:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.&lt;beanName&gt;.config.&lt;key&gt;=&lt;value&gt;</pre>

Every AI service requires (first and foremost) a `ChatModel` as this interfaces with your LLM. Each model provider provides an implementation of the LangChain4J `ChatModel` interface. For example, LangChain4J Azure Open AI provider provides its implementation to `ChatModel`, `AzureOpenAiChatModel`. Each model provider provides a `Builder`, which is builder pattern to build their corresponding `ChatModel` object. For LangChain4J Azure Open AI, `AzureOpenAiChatModel.Builder` build its `AzureOpenAiChatModel` and the builder config properties uses its builder, where ` is the builder method of the same name, and the ` is the corresponding value that is passed to the builder's method.

In our `microprofile-config.properties` we set our `ChatModel` as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.chat-model.class=dev.langchain4j.model.azure.AzureOpenAiChatModel
dev.langchain4j.cdi.plugin.chat-model.config.api-key=${azure.openai.api.key}
dev.langchain4j.cdi.plugin.chat-model.config.endpoint=${azure.openai.endpoint}
dev.langchain4j.cdi.plugin.chat-model.config.service-version=2024-02-15-preview
dev.langchain4j.cdi.plugin.chat-model.config.deployment-name=${azure.openai.deployment.name}
dev.langchain4j.cdi.plugin.chat-model.config.temperature=0.1
dev.langchain4j.cdi.plugin.chat-model.config.topP=0.1
dev.langchain4j.cdi.plugin.chat-model.config.timeout=PT120S
dev.langchain4j.cdi.plugin.chat-model.config.max-retries=2
dev.langchain4j.cdi.plugin.chat-model.config.logRequestsAndResponses=true</pre>

The ` is the CDI bean name that will be assigned to the object ``class`` key ``dev.langchain4j.cdi.plugin..``. In this example, the bean name for our chat model is ``chat-model`` and it's assigned to the chat model class ``dev.langchain4j.model.azure.AzureOpenAiChatModel`.

All the properties found within the `langchain4j.cdi.plugin..config.` property, LangChain4J CDI will populate the value to its corresponding Langchain4J `ChatModel` declared. In this case the `dev.langchain4j.model.azure.AzureOpenAiChatModel.Builder` class (this is done for you internally).

The \`\` builder property can follow the lowercase dashed property value that matches the camel case builder property bean.

For example, should you want to log all chat request you will need to set the `logRequests` to `true` on the Builder. In the config, all uppercase letters can be lowered and prepended with a dash `-`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.chat-model.config.log-requests=true</pre>

Is equivalent to the config property:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.chat-model.config.logRequests=true</pre>

The config creator (internally) will identify config values that contains dashes and rework it to its camel-case property and match it to the Builder and then assign the value accordingly.

### 3) Declare an AI Service interface {#h3-4-3-declare-an-ai-service-interface}

Using LangChain4J's AiServices, it allows developers the ability to plugin any of the AiServices component much more flexible. Now, we're powering the AI Services with the power of Jakarta EE CDI.

#### 3.1) The @RegisterAiService annotation.

The `@RegisterAIService` annotation is the glue that automatically applies LangChain4J AI services components to your AI services. Each annotation attribute correspond to the LangChain4J AI service component by their CDI bean name. If any of the property name is assigned as `#default` then CDI container will find the default AI Services component (based on the component class type) that is ready for injection.

#### 3.2) Your AI Service agent

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.cdi.RegisterAiService;

@RegisterAIService(
    scope = ApplicationScoped.class,
    chatLanguageModelName = "chat-model"
)
public interface Assistant {

    @SystemMessage("You are a concise enterprise assistant.")
    @UserMessage("Answer clearly: {{question}}")
    String answer(String question);
}</pre>

The interface describes what we want: a `Assistant` object with one method: `answer(String)`. We specify a `SystemMessage` (this is optional).

* The input is a `String`, so LangChain4J will infer that this is the `UserMessage`.
* The output is a `String`, so LangChain4J will automatically infer that this is the model output.

The `@RegisterAIService.chatLanguageModelName` property matches the \`\` value that we've specified on the config property file.

The default CDI scope for your AI Service is `RequestScoped`. You can apply an alternative CDI scope by overriding the `@RegisterAIService.scope` property.

#### 3.3) Adding Memory

LLMs are **stateless** , meaning it doesn't remember your previous conversation and context. One way that LLMs remember the conversation is to pass the previous messages and append the current message **at every call** . It's for that reason that LangChain4J provide the `ChatMemory` component.  

A `ChatMemory` is basically a **list of `ChatMessages`** and you can manually add the messages for every `UserMessage` you send and every `AiMessage` you receive back. But if you **combine `ChatMemory` with an `AiService`**, LangChain4J will take care of updating the memory for you.

Please note that adding memory eats tokens, so please monitor your usage cost.

You can configure `ChatMemory` with Microprofile Config, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.chat-memory.class=dev.langchain4j.memory.chat.MessageWindowChatMemory
dev.langchain4j.cdi.plugin.chat-memory.scope=jakarta.enterprise.context.ApplicationScoped
dev.langchain4j.cdi.plugin.chat-memory.config.maxMessages=10</pre>

which is equivalent to physically write the code as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);</pre>

Now, we update our existing `Assistant` `@RegisterAIService` to include `ChatMemory` with `chatMemoryName` property to bean `chat-memory`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterAIService(
    chatLanguageModelName = "chat-model",
    chatMemoryName = "chat-memory"
)</pre>

#### 3.4) Adding Tools

There are various ways to add tools for CDI registration:

* Either add a fully-qualified class name of class(es) that contains the LangChain4J `@Tool` annotations on `@RegisterAIService.tools` annotation property (the `tools` property is type of `Class[]`), OR
* Specify a `@RegisterAIService.toolProviderName` for a declared LangChain4J `ToolProvider`. The `ToolProvider` can be declared using the configurable properties approach.

For example, if you want to connect to an MCP server, LangChain4J provides an integration to any MCP server through their provided `McpToolProvider`.

#### 3.5) RAG (Retrieval-Augmented Generation)

LangChain4J provides the interfaces `ContentRetriever` that you can implement. It provides 4 implementations out of the box, that you can use:

* **WebSearchEngineContentRetriever**: the LLM turns the original prompt into a web search query and a number of search results are used as context
* **SqlContentRetriever**: the LLM is given the database schema and turns the original prompt into SQL to retrieve information that will be used as context
* **Neo4jContentRetriever**: the LLM is given the schema and turns the original prompt into Cypher (neo4j query) to retrieve information that will be used as context
* **EmbeddingStoreContentRetriever**: to retrieve relevant fragments from all documents that we provide (text, excel, images, audio, ...).

The scope for building easy RAG and advance RAG using LangChain4J is beyond the scope of this article, but for this example we'll include a simple easy RAG using the configurable approach:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.docRagRetriever.class=dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
dev.langchain4j.cdi.plugin.docRagRetriever.config.embeddingStore=lookup:default
dev.langchain4j.cdi.plugin.docRagRetriever.config.embeddingModel=lookup:default
dev.langchain4j.cdi.plugin.docRagRetriever.config.maxResults=3
dev.langchain4j.cdi.plugin.docRagRetriever.config.minScore=0.6</pre>

The `lookup:default` value will cause CDI to lookup the default `EmbeddingStore` or `EmbeddingModel` registered in the CDI container. Otherwise, provide a fully-qualified class name of the specified interface class type.

Our `EmbeddingModel` and `EmbeddingStore` are CDI produced using CDI producer fields.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ApplicationScoped
public class DocRagIngestor {

    // Used by ContentRetriever
    @Produces
    private EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // Used by ContentRetriever
    @Produces
    private EmbeddingStore&lt;TextSegment&gt; embeddingStore = new InMemoryEmbeddingStore&lt;&gt;();

    //Code made short for brevity   
}</pre>

Then we register it to `@RegisterAIService` by providing the CDI name of the `ContentRetriever` as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterAIService(
    chatLanguageModelName = "chat-model",
    chatMemoryName = "chat-memory",
    contentRetrieverName = "docRagRetriever"
)</pre>

### 4) Inject and use it {#h3-5-4-inject-and-use-it}

Now, you can simply `@Inject` your AI `Assistant`.  

In this example, our `ChatResource` RESTful Service (using Jakarta RESTful Web Service) we just inject our `Assistant` just we normally do with any Jakarta EE CDI services:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotation.Operation;

@Path("/assist")
public class AssistantResource {
    @Inject Assistant assistant;

    @POST
    @Operation(summary = "Ask your question to our friendly assistant.")
    @Path("/ask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AnswerDto ask(QuestionDto q) {
        return new AnswerDto(assistant.answer(q.getQuestion()));
    }
}</pre>

Your `AnswerDto` and `QuestionDto` are standard POJO.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class QuestionDto implements Serializable {

    @JsonbProperty
    private String question;

    public QuestionDto() {}

    public QuestionDto(String question) { 
        this.question = question; 
    }

    public String getQuestion() { 
        return question; 
    }

    public void setQuestion(String question) { 
        this.question = question; 
    }
}

public class AnswerDto implements Serializable {

    @JsonbProperty
    private String answer;

    public AnswerDto() {}

    public AnswerDto(String answer) { 
        this.answer = answer; 
    }

    public String getAnswer() { 
        return answer; 
    }

    public void setAnswer(String answer) { 
        this.answer = answer; 
    }
}</pre>

Now, you can run your application by deploying it to your application server and do an HTTP POST to your RESTful endpoint.

### 4) Observability and resiliency. {#h3-6-4-observability-and-resiliency}

#### 4.1) Fault Tolerance using Microprofile Fault Tolerance.

Fault Tolerance capability was added to ensure system stability and resilience to your LangChain4J-CDI AI Services applications. With Microprofile Fault Tolerance integration, AI services can apply features like:

* **Circuit Breaker:** Prevents cascading failures by quickly failing requests to services experiencing issues, allowing them to recover. Use annotation `@org.eclipse.microprofile.faulttolerance.CircuitBreaker`.
* **Rate Limiter:** Controls the rate of requests to a service, preventing overload.
* **Retry:** Automatically retries failed operations, useful for transient errors. Use annotation `@org.eclipse.microprofile.faulttolerance.Retry`.
* **Bulkhead:** Isolates failing parts of the system to prevent them from affecting others. Use annotation `@org.eclipse.microprofile.faulttolerance.Bulkhead`.
* **Time Limiter:** Enforces a timeout on operations, preventing long-running or hung calls. Use annotation `@org.eclipse.microprofile.faulttolerance.Timeout`.
* **Fallback:** Utilize fallback mechanisms to provide alternative responses or default behavior when an AI model or external service is unavailable or returns an error. Use annotation `@org.eclipse.microprofile.faulttolerance.Fallback`.
* **Asynchronous** For asynchronous processing for long-running operations. Use annotation `@org.eclipse.microprofile.faulttolerance.Asynchronous`.

This example (found on `examples/liberty-car-booking`) utilizes Microprofile Fault Tolerance to ensure resiliency.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterAIService(scope = ApplicationScoped.class, tools = BookingService.class, chatMemoryName = "chat-ai-service-memory")
public interface ChatAiService {

    @SystemMessage("""
            You are a customer support agent of a car rental company named 'Miles of Smiles'.
            Before providing information about booking or canceling a booking, you MUST always check:
            booking number, customer name and surname.
            You should not answer to any request not related to car booking or Miles of Smiles company general information.
            When a customer wants to cancel a booking, you must check his name and the Miles of Smiles cancellation policy first.
            Any cancelation request must comply with cancellation policy both for the delay and the duration.
            Today is {{current_date}}.
            """)
    @Timeout(unit = ChronoUnit.MINUTES, value = 5)
    @Retry(abortOn = { BookingCannotBeCanceledException.class,
            BookingAlreadyCanceledException.class,
            BookingNotFoundException.class }, maxRetries = 2)
    @Fallback(fallbackMethod = "chatFallback", skipOn = {
            BookingCannotBeCanceledException.class,
            BookingAlreadyCanceledException.class,
            BookingNotFoundException.class })
    String chat(String question);

    default String chatFallback(String question) {
        return String.format(
                "Sorry, I am not able to answer your request %s at the moment. Please try again later.",
                question);
    }
}</pre>

Please note that LangChain4J `ChatModel` has a retry policy built inside the `ChatModel.chat()` method. Thus, adding a `@Retry` to your AI Service will add additional retry `maxRetries` to its existing LangChain4J ChatModel `maxRetries`. Some LangChain4J AI providers do provide the ability to configure the `maxRetries` so we suggest to set the `ChatModel.maxRetries = 0` in order to fully rely on Microprofile's Fault Tolerance retry mechanism.

To apply fault tolerance to our AI services, we need to add the `langchain4j-cdi-fault-tolerance` module on our Maven project.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;dev.langchain4j.cdi.mp&lt;/groupId&gt;
    &lt;artifactId&gt;langchain4j-cdi-fault-tolerance&lt;/artifactId&gt;
    &lt;version&gt;${langchain4j.cdi.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

where *${langchain4j.cdi.version}* is the latest LangChain4J CDI version. This requires that your application server supports Microprofile Fault Tolerance.

#### 4.2) Observability using Microprofile Telemetry

LangChain4J-CDI Telemetry builds upon the observability features in the Microprofile Telemetry to provide insights into AI-related operations. LangChain4J-CDI Telemetry provides metrics and tracing capabilities for the `ChatModel` component, based on the [Semantic Conventions for GenAI Metrics](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-metrics/)).

To apply Generative AI telemetry to our AI services, we need to add the `langchain4j-cdi-telemetry` module on our Maven project.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;dev.langchain4j.cdi.mp&lt;/groupId&gt;
    &lt;artifactId&gt;langchain4j-cdi-telemetry&lt;/artifactId&gt;
    &lt;version&gt;${langchain4j.cdi.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

where *${langchain4j.cdi.version}* is the latest LangChain4J CDI version. This requires that your application server supports Microprofile Telemetry.

The LangChain4J-CDI Telemetry provides 2 implementation of the `ChatModelListener`:

* `dev.langchain4j.cdi.telemetry.SpanChatModelListener`: To represent a span for every `ChatModelRequest` call to Generative AI model or service and a `ChatModelResponse` based on the input prompt.
* `dev.langchain4j.cdi.telemetry.MetricsChatModelListener`: To represent generative AI metrics for every `ChatModelRequest` to the LLM, along with its `ChatModelResponse`.

Using the configurable properties method, we can apply the following `ChatModelListener` to our `ChatModel` as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.&lt;beanName&gt;.config.listeners=@all</pre>

The value set to `@all` tells CDI to inject all CDI discoverable `ChatModelListener` to the `ChatModel` that supports listeners.

Alternatively, you can specify your `ChatModel` individually as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.langchain4j.cdi.plugin.&lt;beanName&gt;.config.listeners=dev.langchain4j.cdi.telemetry.SpanChatModelListener,dev.langchain4j.cdi.telemetry.MetricsChatModelListener</pre>

The value are comma separated, fully qualified class name. The class must implement the `ChatModelListener` interface.

In Summary {#h2-7-in-summary}
-----------------------------

LangChain4J-CDI simplifies the process of integrating LangChain4J components into AI services. Its strong CDI integration and pluggability to MicroProfile, LangChain4J-CDI makes it an attractive choice for Jakarta EE and Microprofile developers who want to add LangChain4J AI capabilities without the usual overhead and boilerplate code. Thus, LangChain4J-CDI lets you focus on the value that generative AI can bring to your business logic.

Important Links {#h2-8-important-links}
---------------------------------------

* LangChain4J-CDI GitHub Repo: <https://github.com/langchain4j/langchain4j-cdi/>
* LangChain4J-CDI Examples GitHub Repo: <https://github.com/langchain4j/langchain4j-cdi/tree/main/examples>
* "Build AI Apps and Agents in Java: Hands-On with LangChain4" by Lize Raes: <https://javapro.io/2025/04/23/build-ai-apps-and-agents-in-java-hands-on-with-langchain4j/>

You are welcome to contribute too. Please follow our [contribution guidelines](https://github.com/langchain4j/langchain4j-cdi/blob/main/CONTRIBUTING.md) should you find bugs that you want to raise as an issue or if you have anything worth contributing to the project.

* LangChain4J-CDI is a project built and maintained by the Jakarta EE/MicroProfile Working Group, under the LangChain4J umbrella.
* Thank you to the Microprofile AI team: Emily Jiang, Emmanuel Hugonnet, Yann Blazart, Ed Burns, Arjav Desai, Phil Chung, Luis Neto, John Clingan, Clement Escoffer, Buhake Sindi, Don Bourne, and other contributors for their immense contribution to the LangChain4J-CDI project.
