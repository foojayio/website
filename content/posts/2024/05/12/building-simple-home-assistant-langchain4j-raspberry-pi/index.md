---
title: "Building a Simple Home Assistant using Langchain4j and Raspberry Pi"
date: "2024-05-12T05:31:16+00:00"
lastmod: "2024-05-12T05:31:18+00:00"
description: "Discover how to build a smart home assistant using Langchain4j and Raspberry Pi, featuring capabilities such as question-answering with RAG, integrating external APIs, controlling smart devices, and utilizing camera and multimodal functionalities."
authors:
  - "jansen-ang"
image: "raspberry-pi-smart-assistant-1.jpg"
categories:
  - "Java"
  - "Machine Learning"
  - "Raspberry Pi"
related_posts:
  - "spring-ai-how-to-write-genai-applications-with-java"
  - "quick-start-with-machine-learning-in-java"
  - "predicting-secure-java-projects-on-maven-central"
  - "introducing-boxlang-ai-explorer-a-local-catalog-for-every-ai-pattern"
frozen: false
---

**Many believe that the future of IoT is AI. Building a Smart Home Assistant is less complex today than ever before. AI has become so accessible that you only need an internet connection and a computer to connect to an API.**

While training specialized deep learning models or using commercial APIs to harness machine learning for specific deterministic use cases is still an option, it is now more common and accessible to use LLMs. Today, you can effortlessly utilize numerous LLMs from various providers through an LLM orchestration framework like [Langchain4j](https://github.com/langchain4j/langchain4j). This framework not only allows the use of LLMs but also provides components that simplify the creation of AI applications such as chatbots and question-answering systems. With these tools, anyone can assemble a Generative AI-powered app in minutes.

In this article, I will demonstrate a sample pet project to show how easily one can build a smart home assistant using Langchain4j, Raspberry Pi, and an internet connection. For this project, I am using the Raspberry Pi 4 Model B which supports dual 4K monitors, with up to 8GB of RAM, Gigabit Ethernet, and dual-band WiFi, making it ideal for demanding IoT applications. However, other Raspberry Pi models should also work, provided they can run Java and have access to the internet.

Our simple smart home assistant features the ability to:

* Converse with the user
* Provide the latest news
* Analyze the environment using its camera
* Toggle a smart strip on
* Answer questions using your own data

The complete code is accessible at this GitHub [link](https://github.com/jmgang/SmartHomeAssistant), which also includes speech-to-text and text-to-speech capabilities using Amazon Polly and Transcribe.

## Creating a simple conversational chatbot

To start, we use the `AiServices` class from Langchain4j, a declarative approach to creating customizable AI interactions through an interface. This integration allows for interaction with prompts, models, memory, and tools, as we'll explore further.

```java
var assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

System.out.println(assistant.chat("Hello"));
```

LLMs are stateless by default and cannot remember previous prompts and responses. To address this, we introduce memory using the `MessageWindowChatMemory` class, with a maximum message retention of about 10 messages.

```java
var chatMemory = MessageWindowChatMemory.withMaxMessages(10);
var assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .build();

System.out.println(assistant.chat("Hello"));
```

With these few lines of code, we're able to implement the user conversation feature.

## Utilizing Function Calling for External Actions

A remarkable aspect of Langchain4j and LLMs is their ability to create agent-like workflows that orchestrate complex tasks through planning, memory, and the use of external tools. This is achieved through function calling, which enables the LLM to automatically invoke your methods as needed.

To access external APIs for news, we use the `@Tools` annotation. This annotation helps describe each method, allowing the framework to invoke these methods appropriately. In our code, methods have been implemented to retrieve the latest news from a news API. In this case, the method is implemented inside the `CurrentInformationRetrieverService` class.

```java
public class CurrentInformationRetrieverService {

    @Tool("Retrieves the latest news. Summarize the highlights into 2 lines only.")
    public String retrieveNews() {
        final NewsRetriever newsRetriever = new NewsRetriever();
        final Optional optionalRetrievedNews = newsRetriever.retrieveTopStories();

        if (!optionalRetrievedNews.isPresent() || optionalRetrievedNews.get().meta().found() == 0) {
            return "No news found at the moment.";
        }

        News retrievedNews = optionalRetrievedNews.get();
        StringBuilder collectedNews = new StringBuilder();
        collectedNews.append("Found ").append(retrievedNews.meta().limit()).append(" news.\n");

        retrievedNews.data().stream()
            .map(news -> String.format("%s. Details: %s. Source: %s. Published at: %s",
                                       news.title(), news.description(), news.source(), news.publishedAt()))
            .forEach(collectedNews::append);

        return collectedNews.toString();
    }
}
```

These are then incorporated into our `AiServices` instance using the `.tools()` method.

```java
var assistant = AiServices.builder(ActionAIAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .tools(new CurrentInformationRetrieverService())
                .build();
```

## Capturing and Analyzing an image

To recognize and analyze an image, we first capture an image, in which the image will be saved to a predetermined file location. It then will retrieve this image and pass it to a multimodal LLM capable of image recognition.

In this code, I used [Google's Gemini Pro Vision model](https://ai.google.dev/gemini-api/docs/models/gemini), which can understand images, and a webcam, although a Pi camera module could also be utilized. In this case, the method is implemented inside the `EnvironmentRecognizerService` class.

```java
public class EnvironmentRecognizerService {

@Tool("Captures an image of the environment and describes the image in the perspective of an agent.")
    public String recognizeEnvironmentByImage() {
        try {
            captureImage(); // Captures image using fswebcam command
            var response = VertexAIGeminiClient.analyzeImage(getProperty("assistant.tools.environment-recognizer.filepath"));
            return response;
        } catch (Exception e) {
            Logger.info(e.getMessage());
            return "Having trouble processing an image.";
        }

    }
}
```

Once captured, the image is retrieved from the specified path and analyzed by the Gemini Pro Vision model with a prompt that adds context. In the `analyzeImage` method, the image is read and converted into a byte array. This is then passed directly to the `generateContent` method of an instance of `GenerativeModel`, where we can provide both the prompt and the data.

This method can also accept URLs, such as those from Google Cloud Storage. In this case, we can simply set the model to act as a smart assistant and ask it what it sees in the image, simulating a robot observing its environment.

```java
public static String analyzeImage(String filePath) throws Exception {
        try (VertexAI vertexAI = new VertexAI(getProperty("assistant.api.googlecloud.projectid"),
                getProperty("assistant.api.googlecloud.location"))) {
            byte[] imageBytes = Files.readAllBytes(Path.of(filePath));

            GenerativeModel model = new GenerativeModel("gemini-1.0-pro-vision", vertexAI);
            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            "You are a smart assistant. What do you see? Make it brief and concise.",
                            PartMaker.fromMimeTypeAndData("image/png", imageBytes)
                    ));

            return ResponseHandler.getText(response);
        }
    }
}
```

We can then add the `EnvironmentRecognizerService` class on our `AiServices` assistant by the same way we added the `CurrentInformationRetrieverService` class.

```java
var assistant = AiServices.builder(ActionAIAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .tools(new CurrentInformationRetrieverService(),
                        new EnvironmentRecognizerService())
                .build();
```

## Controlling an External Smart Device

For external smart device integration, I used [Home Assistant](https://www.home-assistant.io/), an open-source home automation platform that centralizes control of various smart devices within a single user interface.

It can be installed on Raspberry Pi via the HomeAssistant OS, a Docker container, or a Python virtual environment, allowing users to manage everything from lighting and climate to security systems directly from their Raspberry Pi. In this project, we utilized a Docker container.

![Home Assistant Integration](https://smart-assistant-langchain4j-blog.s3.amazonaws.com/homeassistant+integration+2.jpg)

After starting the HomeAssistant Docker container, we integrate the smart device using HomeAssistant's extensive device integration options. In this instance, I integrated a TP-Link Kasa Smart Strip via the Home Assistant UI.

The methods to control the smart strip are described in their [REST API](https://developers.home-assistant.io/docs/api/rest/), which facilitates interaction through the `@Tools` annotation. In my implementation, I tracked which outlet should be activated using a HashMap, as my smart strip has three individual outlets. The values stored in the HashMap are the entity IDs of each outlet, which HomeAssistant uses to determine which outlet to turn on.

```java
private static final Map STRIP_ENTITY_MAP = Map.of("FIRST", getProperty("assistant.api.homeassistant.strip.entity-id.first"),
            "SECOND", getProperty("assistant.api.homeassistant.strip.entity-id.second"),
            "THIRD", getProperty("assistant.api.homeassistant.strip.entity-id.third"));

@Tool("Turns on a specific outlet number in a smart strip. The choices are only first, second and third.")
public String turnOnSpecificOutlet(String outletNumber) {
    outletNumber = outletNumber.toUpperCase();
    if(!STRIP_ENTITY_MAP.containsKey(outletNumber)) {
        return "The specified outlet " + outletNumber  + " was not found. Please specify again.";
    }

    if(isOn(STRIP_ENTITY_MAP.get(outletNumber))) {
        return "The " + outletNumber + " outlet is already turned on.";
    }

    try {
        turnOn(STRIP_ENTITY_MAP.get(outletNumber));
        return "The " + outletNumber + " outlet was turned on successfully.";
    } catch (IOException e) {
        Logger.info(e.getMessage());
        return "I'm having trouble turning on the outlet.";
    }
}
```

Behind the scenes, the `turn_on` method is simply sending a POST request to the HomeAssistant `switch/` API to turn on the outlet.

```java
public static void turnOn(String entityId) throws IOException {
    sendCommand("turn_on", new HomeAssistantCommand(entityId));
}

private static void sendCommand(String service, HomeAssistantCommand command) throws IOException {
    HttpRequestBuilder builder = new HttpRequestBuilder(getProperty("assistant.api.homeassistant.url"))
            .addPathSegments("api", "services", "switch", service)
            .addHeader("Authorization", "Bearer " + getProperty("assistant.api.homeassistant.access-token"))
            .addHeader("Content-Type", "application/json");
    List changes = httpClient.sendPost(builder.build(), builder.getHeaders(), command, new TypeReference() {
    });

    System.out.println("Number of entities changed: " + changes.size());
    changes.forEach(change -> System.out.println("Entity ID: " + change.entityId() + ", State: " + change.state()));
}
```

As noted before, we can add the class to our `AiServices` instance using the `tools()` method. Now, we have three distinct classes capable of observing the environment, retrieving information from external APIs, and even controlling a smart strip!

```java
var assistant = AiServices.builder(ActionAIAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .tools(new CurrentInformationRetrieverService(),
                        new SmartOutletManagerService(),
                        new EnvironmentRecognizerService())
                .build();
```

## Using our own data for Question-Answering

Currently, we can either engage in conversation with the LLM or direct it to perform external actions, but it remains unable to answer questions about new information or topics not previously known to it. To empower the LLM to respond to queries based on our data, we utilize [Retrieval Augmented Generation](https://docs.langchain4j.dev/tutorials/rag/).

This method involves storing information in a vector database and retrieving relevant data when needed, using it to provide context for the LLM. Although I've used a text file for data retrieval in this instance using the `FileSystemDocumentLoader` class, Langchain4j also supports other storage options like Amazon S3 or direct retrieval from URLs. Additionally, it has utility classes that you can use to clean or transform your data.

```java
Document document = FileSystemDocumentLoader
                .loadDocument("smart_assistant_article.txt");

var embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

DocumentSplitter documentSplitter = DocumentSplitters.recursive(300, 0);

var embeddingStore = EmbeddingStore embeddingStore = WeaviateEmbeddingStore.builder()
            .apiKey(getProperty("assistant.weaviate.apikey"))
            .scheme("https")
            .host(getProperty("assistant.weaviate.host"))
            .avoidDups(true)
            .consistencyLevel("ALL")
            .build();

EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        .documentSplitter(documentSplitter)
        .embeddingModel(embeddingModel)
        .embeddingStore(embeddingStore)
        .build();
ingestor.ingest(transformedDocument);
```

In the above code, the `BgeSmallEnV15QuantizedEmbeddingModel` is designed to generate embeddings for text segments. These embeddings map text data to a vector representation that can be efficiently searched and compared. `DocumentSplitters.recursive` is used to recursively split the document into chunks of up to 300 tokens, which is crucial for managing large documents by breaking them into manageable pieces for processing and retrieval.

The `EmbeddingStoreIngestor` is configured with the document splitter, embedding model, and an embedding store — [Weaviate](https://weaviate.io/), in this case, which offers a free sandbox environment lasting 14 days. The `ingest` method processes the document by splitting it, generating embeddings for each chunk, and storing these embeddings in an embedding store for quick retrieval during the question-answering phase.

For retrieval, the `DocumentRetriever` class configures a `get` method that sets up an `EmbeddingStoreContentRetriever`. This setup involves retrieving an embedding store instance, which he had initialized earlier, then initializing the embedding model, and specifying retrieval parameters to return up to three results with a minimum relevance score of 0.8.

This configuration ensures that only the most relevant and high-quality document segments are retrieved to support the LLM's responses based on specific data queries.

```java
public class DocumentRetriever {

    public static ContentRetriever get() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(EmbeddingStore.get())
                .embeddingModel(new BgeSmallEnV15QuantizedEmbeddingModel())
                .maxResults(3)
                .minScore(0.8)
                .build();
    }

}
```

## Revisiting the Current Architecture

![Model Bottleneck](https://smart-assistant-langchain4j-blog.s3.amazonaws.com/smart+assistant+archi+bottleneck.PNG)

Our model currently processes all queries, determining if a tool can be used while also querying our database. This isn't very efficient. To improve, we split the workload: one LLM handles conversational queries and another manages commands. This requires another LLM to classify the intent of each query as conversational or actionable. We define these intents with an enum and use the `@Description` annotation to inform the LLM.

```java
public enum Intent {

    @Description("An intent of conversing with someone as a chatbot.")
    CONVERSATIONAL_INTENT,

    @Description("An intent of toggling a smart outlet either on or off.")
    TOGGLE_OUTLET_INTENT,

    @Description("An intent of inquiry regarding today's news.")
    INQUIRE_NEWS_INTENT,

    @Description("An intent of seeing via a camera captured still image as of the moment. Thus, you can describe things around you.")
    SEE_INTENT;

    public boolean isConversational() {
        return CONVERSATIONAL_INTENT.equals(this);
    }
}
```

Furthermore, we employ Few-Shot prompting in our Intent Classifier Assistant to minimize errors and ensure the correct intent classification. In this case, I've explained to the LLM its role, the context and two examples of the expected input and output, each with their own explanations.

```java
@UserMessage("Your role is to specify the intent of a given query for a smart home assistant with camera: {{it}}. " +
            "EXAMPLES: " +
            "Why is the sky blue? " +
            "CONVERSATIONAL_INTENT" +
            "Explanation: This is a general question asking about the sky and not about a still image that was captured." +
            " " +
            "Where are you located right now?" +
            "SEE_INTENT" +
            "Explanation: The intent was asking your location and thus where you are right now.")
Intent specifyIntent(String text);
```

The query method is the entry point of the user's message and it first goes through the Intent Assistant before deciding whether it's conversational or actionable.

```java
public String query(String query) {
    Intent intent = intentControllerAssistant.specifyIntent(query);

    System.out.println("Intent: " + intent);

    if(intent == null || intent.isConversational()) {
        return conversationalAIAssistantService.chat(query);
    }

    return actionAIAssistantService.sendActionCommand(query);
}
```

## Adding Moderation Models

We include moderation models to monitor both user queries and LLM responses, ensuring content remains appropriate and free from harmful material. This is true for both the user query and the LLM responses. The `Moderator` interface defines a single method moderate, marked with the `@Moderate` annotation, which suggests it applies specific content moderation rules to the input string.

```java
public interface Moderator {
    @Moderate
    String moderate(String query);
}
```

In the code below, we create an instance of the `Moderator` via `AiServices`, which integrates both a moderation model along with a chat language model. The query method uses this service to first moderate the user's input.

If the input violates moderation rules, it throws an exception, where in this case, we can format the response to let the user know of content violations. Likewise, the LLM's response are also subjected to moderation, ensuring that all interactions remain within ethical boundaries.

```java
var moderationModel = OpenAiModerationModel.withApiKey(getProperty("assistant.openai.apikey"));
var moderator = AiServices.builder(Moderator.class)
                    .chatLanguageModel(chatLanguageModel)
                    .moderationModel(moderationModel).build();

// updated query method
public String query(String query) {

    try {
        moderator.moderate(query);
    }catch (ModerationException me) {
        return "Please avoid making statements that violate the content policy.";
    }

    Intent intent = intentControllerAssistant.specifyIntent(query);

    System.out.println("Intent: " + intent);

    String response;
    if(intent == null || intent.isConversational()) {
        response = conversationalAIAssistantService.chat(query);
    }else {
        response = actionAIAssistantService.sendActionCommand(query);
    }

    try {
        moderator.moderate(response);
    }catch (ModerationException me) {
        return "We restricted the response made by one of our LLMs that maybe harmful to you. We take ethical responses very seriously.";
    }

    return response;
    }
```

## Final Thoughts

The journey to creating a smart home assistant showcases the power of modern AI tools like Langchain4j and accessible hardware such as the Raspberry Pi.

This project not only applies AI in smart home technology but also demonstrates how emerging technologies can be easily adopted.

By incorporating features like question-answering with RAG, external API integrations, camera functionalities, and multimodal capabilities, this setup mirrors the sophistication of advanced smart home systems.

These advancements open new possibilities for both educational purposes and personal innovation.
