---
title: "AI-powered Chat Application using watsonx.ai and Spring AI"
slug: "ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai"
date: "2025-01-03T08:31:51+00:00"
lastmod: "2025-01-03T08:39:32+00:00"
description: "Discover on how quickly to integrate the IBM watsonx.ai platform in your Java application using Spring AI by creating a chat application"
authors:
  - "tristan-mahinay"
image: "https://foojay.io/wp-content/uploads/2024/12/generative-ai-in-enterprise-systems-1024x589.png"
categories:
  - "Java"
  - "JDK21"
  - "Spring"
  - "Tools"
  - "Tutorials"
  - "Vaadin"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Generative Artificial Intelligence (Gen AI) disrupted enterprises with the introduction of GPT-4 foundation model by Open AI in the late 2022 to early 2023 triggering big tech to release their own Gen AI platform and foundation models. Among them are Google, Microsoft, AWS and IBM. In this article, we will deal with the Gen AI platform and model of IBM called watsonx and integrate it with Spring AI to create a custom chat application using the IBM Granite Foundation Model.**

Large-scale machine learning models trained on massive and diversed dataset to perform general-purpose tasks. One of the use-cases of foundation models are natural language processing tasks which augments the daily activities of human such as the ff:

* Summarization
* Question and Answer
* Translation
* Sentiment Analysis
* Text Generation
* Image Recognition

These activities usually incorporate a specific foundation model to achieve. Below are some scenarios that were considered in Gen AI development.

1. Language Models  
   Models that were trained to generate text, classify text, extract meaning of a text, summarize and generate code based on a textual context. These were trained to analyze the human language and give response based on an initial text input.
2. Image Models  
   Models that were trained to understand, analyze, generate and manipulate visual data to the users. These models accepts images and drawings as an input and generates a response by understanding it.
3. Multi-modal Models  
   Incorporates the capabilities of language and image models. It is trained to different modalities and perform a combination of it. The data that this model can handle can span to text, audio, video and images.

In this article, we will focus on Language Models and Chat applications. Resurfacing again this excellent playlist explaining large language models and chatbots by IBM Technology.

{{< youtube videoseries >}}

<br />

The flagship AI and machine learning platform by IBM. It is designed to empower business to build, deploy, scale AI models effectively. This technology is not the foundation model of IBM rather a platform that incorporates multiple foundation models plus the IBM's flagship model called IBM Granite.

To see the table of all supported foundation models, you can check it out [here](https://www.ibm.com/products/watsonx-ai/foundation-modelshttp:// "here").

Pricing {#h2-0-pricing}
-----------------------

If you're an organization interested in trying IBM watsonx.ai, you can check-out its [pricing table](https://www.ibm.com/products/watsonx-ai/pricing "pricing table")

IBM Granite is the IBM's flagship family of foundation models developed for Generative AI. It is part of the IBM watsonx.ai platform and focuses in giving solutions to enterprises. One of the advantage of using Granite models is that it is optimized for business users, making enterprise organizations competitive using the platform.

Articles related to the capabilities of IBM Granite can be read below:

* [IBM Introduces Granite 3.0: High Performing AI Models Built for Business](https://newsroom.ibm.com/2024-10-21-ibm-introduces-granite-3-0-high-performing-ai-models-built-for-business "IBM Introduces Granite 3.0: High Performing AI Models Built for Business")
* [IBM's New Granite 3.0 Generative AI Models Are Small, Yet Highly Accurate and Efficient](https://developer.nvidia.com/blog/ibms-new-granite-3-0-generative-ai-models-are-small-yet-highly-accurate-and-efficient/ "IBM’s New Granite 3.0 Generative AI Models Are Small, Yet Highly Accurate and Efficient")

The platform will be used to integrate a IBM Granite Model with Spring AI. For this demonstration we will be using a language model for chat applications using [IBM granite-13b-chat-v2](https://www.ibm.com/docs/en/watsonx/w-and-w/2.1.x?topic=models-granite-13b-chat-v2-model-card "IBM granite-13b-chat-v2")

Streamlines the creation of generative artificial intelligence in a Java application without unnecessary complexities. The project main focus is to connect your enteprise data and APIs to foundation models.

The Spring AI API provides abstractions that have multiple implementations for easy development and minimal code changes.

The API provids various functionalities that spans from a simple application to a complex one. These functionalities can be combined to build an enterprise-grade full-stack application.

* AI Models (Chat, Image, Audio, Moderation and Embeddings)
* Vector Databases
* Retrieval-Augmented Generation
* Observability
* Prompting

Additional functionalities that Spring AI supports which is significant for AI customizations

* Function Calling
* Structured Output
* Multi-modality
* ETL Pipelines
* Model Context Protocol

In this demonstration, we will not deal in-depth of these concepts. We will show only the features that IBM watsonx.ai API supports on how quickly we can create a simple chat application.

IBM watsonx.ai {#h2-1-ibm-watsonx-ai}
-------------------------------------

The IBM watsonx.ai API supports chat and embedding models in Spring AI context. For chat it supports *WatsonxAiChatModel* which is both a text generation and text stream generation.

To generate embeddings, the platform supports *WatsonxAiEmbeddingModel*.

![](/images/posts/2025/01/ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai/watsonx-springai-uml.png)

The image above is the UML Diagram of WatsonxAi API. The chat model implements the provided abstraction of Spring AI for basic chat and streaming chat. To provide implementations of these methods, the WatsonxAiChatModel needs to call the corresponding watsonx.ai platform API via REST and HTTP protocol.

Creating IBM watsonx.ai Chat-based Application {#h2-2-creating-ibm-watsonx-ai-chat-based-application}
-----------------------------------------------------------------------------------------------------

### Prerequisites {#h3-3-prerequisites}

The code that will be demonstrated is already available in [watsonx-spring-ai-hilla](https://github.com/rjtmahinay/watsonx-spring-ai-hilla "watsonx-spring-ai-hilla") project. If you're interested to run the demo project, you may fork and run it locally.

Spring AI is already available as a dependency in Spring Initialzr. To add the watsonx.ai Spring Boot starter just specify the following dependency in your build.gradle

```groovy
implementation 'org.springframework.ai:spring-ai-watsonx-ai-spring-boot-starter'
```

See the <https://github.com/rjtmahinay/watsonx-spring-ai-hilla/blob/main/build.gradle> file of the project.

### Configurations {#h3-4-configurations}

The watsonx platform needs the following properties to start the Spring AI application.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">spring:
  ai:
    watsonx:
      ai:
        // The project id of your watsonx project
        project-id: ${PROJECT_ID}
        // The API key from IBM Cloud
        i-a-m-token: ${API_KEY}
        // Base URL of the IBM Cloud where watsonx.ai is connected
        base-url: https://us-south.ml.cloud.ibm.com
        // The text endpoint of watsonx.ai service
        text-endpoint: "/ml/v1/text/generation?version=2024-05-31"
        // The streaming text endpoint of watsonx.ai service
        stream-endpoint: "/ml/v1/text/generation_stream?version=2024-05-31"</pre>

All Spring AI properties starts with a prefix of `spring.ai.`. Every AI platform has their own specific options. For chat properties, watsonx.ai API provides a Chat Options with default values and can be overriden in the properties or yaml file. Below is an example override of the chat options.

Project ID and API key should be stored in a secured vault to prevent security violations.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">spring:
  ai:
    watsonx:
      ai:
        chat:
          options:
            // The chat model to be used
            model: "ibm/granite-13b-chat-v2"
            /*
            Sets the decoding strategy either greedy or sampling. For this chat application, we want
            a more factual result rather a creative one. Thus, we will use greedy
            */
            decoding-method: "greedy"
            /*
            The maximum token that the LLM will use. In this case, the granite-13b-chat-v2 has a
            maximum of 8192 limit.
            */
            max-new-tokens: 8191
            // Defaults to 0. Sets how many token does the LLM generate
            min-new-tokens: 0
            /*
            Temperature, top-p, and top-k are used for Sampling Decoding Method. We will not use
            this in this demonstration.
            */
            temperature: 0.0
            top-p: 1.0
            top-k: 1
            // Penalize the LLM for repetition of text. Defaults to 1.0 and has a maximum of 2.0
            repetition-penalty: 1.0</pre>

To see the token limits of all available foundation models in watsonx.ai platform, please check the [documentation](https://dataplatform.cloud.ibm.com/docs/content/wsj/analyze-data/fm-models-details.html?context=wx&amp;locale=en&amp;audience=wdp "documentation").

### Chatbot {#h3-5-chatbot}

The user-interface of the application is created via Hilla Framework. Hilla is part of Spring Initialzr dependencies. To learn more about Hilla, check out this [documentation](https://vaadin.com/docs/latest/hilla/guides "documentation").

The chat application uses the abstracted client called ChatClient. The ChatClient consumes as a parameter an AI model.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Configuration
public class ChatConfiguration {

    /**curl
     * This method is used to create a ChatClient bean.
     * @param watsonxAiChatModel The chat model to be used
     * @return The ChatClient bean
     */
    @Bean
    ChatClient watsonxChatClient(WatsonxAiChatModel watsonxAiChatModel) {
        return ChatClient.builder(watsonxAiChatModel)
                .defaultSystem("You are a helpful ai assistant").build();
    }
}</pre>

I've created a configuration class that creates a ChatClient bean and consumes the WatsonxAiChatModel. I instructed the ChatClient to behave as a helpful AI assistant.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Endpoint
@AnonymousAllowed
@RequiredArgsConstructor
public class ChatEndpoint {

    private final ChatClient watsonxChatClient;

    /**
     * This method is used to generate a response from the chatbot.
     * @param userInput The user's input to the chatbot
     * @return The chatbot's response
     */
    public String generateMessage(String userInput) {
        return watsonxChatClient.prompt().user(userInput).call().content();
    }

    /**
     * This method is used to generate a streaming response from the chatbot.
     * @param userInput The user's input to the chatbot
     * @return The chatbot's response as a stream of strings
     */
    public Flux&lt;String&gt; generateStreamingMessage(String userInput) {
        return watsonxChatClient.prompt().user(userInput).stream().content();
    }
}</pre>

Since we're using Hilla, the controller is called an endpoint. These endpoint class will be converted to a TypeScript file upon invocation of bootRun task.

Below is the example conversion to a TypeScript file. The file is called *ChatEndpoint.ts*

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import { EndpointRequestInit as EndpointRequestInit_1, Subscription as Subscription_1 } from "@vaadin/hilla-frontend";
import client_1 from "./connect-client.default.js";
async function generateMessage_1(userInput: string | undefined, init?: EndpointRequestInit_1): Promise&lt;string | undefined&gt; { return client_1.call("ChatEndpoint", "generateMessage", { userInput }, init); }
function generateStreamingMessage_1(userInput: string | undefined): Subscription_1&lt;string | undefined&gt; { return client_1.subscribe("ChatEndpoint", "generateStreamingMessage", { userInput }); }
export { generateMessage_1 as generateMessage, generateStreamingMessage_1 as generateStreamingMessage };
</pre>

If you inspect both generateMessage and generateStreamingMessage and Java methods were converted to a JavaScript method. In this case, we don't need to have a network call via REST since we can now call the method from the TypeScript conversion. Below is the example usage of these methods.

*ChatView.tsx*

<pre class="EnlighterJSRAW" data-enlighter-language="generic">const ChatView = () =&gt; {

    // Remove other logic for brevity.

    const handleSubmit = async (event: CustomEvent) =&gt; {
        // Remove other logic for brevity.

        try {
            const response = await ChatEndpoint.generateMessage(inputMessage);

            console.info('AI response:', response);

            const aiResponse: Message = {
                id: (Date.now() + 1).toString(),
                sender: 'ai',
                content: response,
            };
            setMessages((prevMessages) =&gt; [...prevMessages, aiResponse]);
        } catch (error) {
            console.error('Error sending message:', error);
        } finally {
            setIsLoading(false);
        }
    };
}</pre>

*StreamingChatView.tsx*

<pre class="EnlighterJSRAW" data-enlighter-language="generic">const StreamingChatView = () =&gt; {

    // Remove other logic for brevity.

    const handleSubmit = async (event: CustomEvent) =&gt; {
       // Remove other logic for brevity.

        try {
            const response = ChatEndpoint.generateStreamingMessage(inputMessage);

            const aiResponse: Message = {
                id: (Date.now() + 1).toString(),
                sender: 'ai',
                content: '',
            };
            setMessages((prevMessages) =&gt; [...prevMessages, aiResponse]);

            response.onNext((chunk: string | undefined) =&gt; {
                setMessages((prevMessages) =&gt; {
                    const lastMessage = prevMessages[prevMessages.length - 1];
                    return [
                        ...prevMessages.slice(0, -1),
                        {
                            ...lastMessage,
                            content: lastMessage.content + chunk
                        }
                    ];
                });
            });
    };
}</pre>

Chat Application {#h2-6-chat-application}
-----------------------------------------

I've showed how quickly it is to create a custom chat application and integrate it to watsonx.ai using Spring AI and Hilla.

### Basic Chat {#h3-7-basic-chat}

The basic chat waits for the AI platform to fully return the response. Thus, waiting time may be longer.

![](/images/posts/2025/01/ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai/watsonx-chat.gif)

### Streaming Chat {#h3-8-streaming-chat}

The streaming chat responds by chunks and prevents frustration to the end user.

![](/images/posts/2025/01/ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai/watsonx-streaming-chat.gif)

watsonx.ai can seamlessly connected to Spring AI and build a chat application with minimal coding changes. Using this platform, we used the IBM Granite model as the foundation model and generated both a basic and streaming response.

For future demonstrations, watsonx.ai can be used for RAG, Function Calling (via MistralAI) and Embeddings.

You can access my watsonx.ai Chat application [here](https://github.com/rjtmahinay/watsonx-spring-ai-hilla "here")

1. **Spring AI Documentation** - <https://docs.spring.io/spring-ai/reference/index.html>
2. **watsonx.ai Documentation** - [https://dataplatform.cloud.ibm.com/docs/content/wsj/getting-started/welcome-main.html?context=wx\&locale=en\&audience=wdp](https://dataplatform.cloud.ibm.com/docs/content/wsj/getting-started/welcome-main.html?context=wx&amp;locale=en&amp;audience=wdp)
3. **watsonx Developer Hub** - <https://www.ibm.com/watsonx/developer>
