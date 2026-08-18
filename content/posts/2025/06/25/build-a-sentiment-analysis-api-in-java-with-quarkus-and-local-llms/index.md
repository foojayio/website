---
title: "Sentiment Analysis with Java, Quarkus, LangChain4j, and Local LLMs"
slug: "build-a-sentiment-analysis-api-in-java-with-quarkus-and-local-llms"
date: "2025-06-25T12:20:26+00:00"
lastmod: "2025-06-25T12:24:15+00:00"
description: "Let’s get in and build a Quarkus REST API that classifies text sentiment using a local LLM model (like Phi-3 Mini) pulled in via Ollama."
canonical: "https://myfear.substack.com/p/sentiment-analysis-java-quarkus-langchain4j-local-llm"
authors:
  - "markus-eisele"
image: "49de875e-fe8c-40d2-9613-61a6c8270eb3_1536x1024.webp"
categories:
  - "Java"
  - "LangChain4j"
tags:
related_posts:
  - "building-simple-home-assistant-langchain4j-raspberry-pi"
  - "run-ai-enabled-jakarta-ee-and-microprofile-applications-with-langchain4j-and-open-liberty"
  - "langchain4j-musings"
  - "ensuring-safe-and-reliable-ai-interactions-with-llm-guardrails"
enlighterjs: true
frozen: false
---

![Abstract image](49de875e-fe8c-40d2-9613-61a6c8270eb3_1536x1024-700x467.webp)  

In a world full of opinions, tweets, reviews, chats, emails, understanding the tone behind words is crucial. Whether you're building a feedback system, monitoring brand reputation, or adding emotion detection to a chatbot, sentiment analysis plays a key role. It turns raw text into actionable signals: Is the customer happy? Frustrated? Neutral?

Traditionally, this kind of natural language processing (NLP) required cloud APIs or heavyweight ML stacks. But now, thanks to modern Java frameworks like [Quarkus](https://quarkus.io/), local Large Language Models (LLMs), and the [LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html) library, you can build a sentiment analyzer that runs entirely on your machine---no cloud account, no API keys, no surprise billing.

This hands-on guide walks you through building a local sentiment analysis API using:

* **Quarkus**: A fast, developer-friendly Java framework.
* **LangChain4j**: A Java API to work with LLMs, inspired by LangChain.
* **Ollama + Quarkus Dev Services**: To run and manage local LLMs inside Podman with zero config.

Let's get in and build a Quarkus REST API that classifies text sentiment using a local LLM model (like Phi-3 Mini) pulled in via Ollama.

If you want to start with the fully working example, get it from [my Github repository](https://github.com/myfear/ejq_substack_articles/tree/main/sentiment-analysis).

### What You'll Build

A simple `/sentiment` REST endpoint that takes a text string and returns a sentiment classification: **POSITIVE** , **NEGATIVE** , or **NEUTRAL**. The model runs locally in a container, orchestrated automatically by Quarkus Dev Services.

This is great for:

* Privacy-focused projects.
* Offline development.
* Avoiding external API rate limits and costs.

### Prerequisites

To follow along, make sure you have the following installed:

* **Java 11+**
* **Maven**
* **Podman** (with a running Podman Machine)
* **An IDE** (e.g., IntelliJ IDEA or VS Code)

No need to manually install Ollama. Quarkus will take care of that for you during development.

### Bootstrap Your Quarkus Project

Open a terminal and scaffold a new project with the necessary extensions:

```bash
mvn io.quarkus.platform:quarkus-maven-plugin:3.22.1:create 
 -DprojectGroupId=org.acme 
 -DprojectArtifactId=sentiment-analysis 
 -Dextensions="rest-jackson,langchain4j-ollama" 
cd quarkus-local-sentiment
```


You now have a Quarkus project with:

* `rest-jackson` for creating JSON-based REST endpoints.
* `langchain4j-ollama` for interacting with local LLMs.

### Configure Ollama and Dev Services

Open `src/main/resources/application.properties` and configure the local model:

```bash
# Use Phi-3 Mini, a small and capable LLM from Ollama Hub 
quarkus.langchain4j.ollama.chat-model.model-id=phi3:mini 

# Increase timeout for initial model loading 
quarkus.langchain4j.ollama.timeout=120s
```


That's all. Quarkus Dev Services will handle pulling the Docker image, downloading the model, and wiring up the service when you run in dev mode.

### Define the Sentiment Enum

Create `src/main/java/org/acme/Sentiment.java`:

```java
package org.acme; 
public enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }
```


Just a simple enum class with the sentiments.

### Create the AI Classification Service

Quarkus Langchain4j allows you to define an interface and annotate it with `@AiService`. Quarkus and Langchain4j will auto-generate the implementation at runtime.

Create `src/main/java/org/acme/SentimentAnalyzer.java`:

```java
package org.acme; 
import dev.langchain4j.service.SystemMessage; 
import dev.langchain4j.service.UserMessage; 
import io.quarkiverse.langchain4j.RegisterAiService; 

@RegisterAiService 
public interface SentimentAnalyzer { 

@SystemMessage({ "Only return the sentiment and nothing else.", 
"Here are some examples.", 
"This is great news!", "POSITIVE", 
"I am very happy with the service.", "POSITIVE", 
"Quarkus Dev Services are amazing and save a lot of time.", "POSITIVE", 
"Langchain4j makes LLM integration surprisingly easy.", "POSITIVE", 
"I am not happy about this situation.", "NEGATIVE", 
"The response time is too slow and frustrating.", "NEGATIVE", 
"This is a terrible experience.", "NEGATIVE", 
"The weather is miserable today.", "NEGATIVE", 
"The event is scheduled for tomorrow at 10 AM sharp.", "NEUTRAL", 
"This is a factual statement about the project configuration.", "NEUTRAL", 
"The report contains data from the last quarter.", "NEUTRAL", 
"The sky is currently overcast.", "NEUTRAL" }) 
@UserMessage("Analyze sentiment of {{text}}") Sentiment classifySentiment(String text); 
@UserMessage("Does {{text}} have a positive sentiment?") boolean isPositive(String text); }
```


This is where the magic happens. With a few lines, you've created an AI-powered sentiment classifier.

### Expose the Sentiment API

Create `src/main/java/org/acme/SentimentResource.java`:

```java
package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/sentiment")
public class SentimentResource {

    private static final Logger log = LoggerFactory.getLogger(SentimentResource.class);

    @Inject
    SentimentAnalyzer analyzer;

    @Inject
    @ConfigProperty(name = "quarkus.langchain4j.ollama.chat-model.model-id")
    String modelId;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String analyzeSentiment(@QueryParam("text") String text) {
        if (text == null || text.isBlank()) {
            log.warn("Empty text received for analysis.");
            return "Please provide text using the 'text' query parameter. Example: /sentiment?text=I+love+Quarkus!";
        }

        log.info("Analyzing text: '{}'", text);
        try {
            Sentiment sentiment = analyzer.classifySentiment(text);
            return String.format("Analyzed Text: '%s'\nPredicted Sentiment: %s\n(Model: Ollama/%s)",
                    text, sentiment, modelId);
        } catch (Exception e) {
            log.error("Sentiment analysis failed", e);
            return "Error during analysis. See server logs.";
        }
    }
}
```


This class provides a simple GET endpoint for testing in the browser or via `curl`.

### Run It!

Ensure Podman is running, then launch the app in dev mode:

```
./mvnw quarkus:dev
```


On first startup, Quarkus will:

* Launch an Ollama container via Dev Services.
* Pull the `ollama/ollama` image.
* Download and cache the `phi3:mini` model.

Be patient, it might take a few minutes.

### Test It!

Try some sample requests:

```
curl "http://localhost:8080/sentiment?text=Quarkus+Dev+Services+are+so+convenient!"
```


Sample output:

```
Analyzed Text: 'Quarkus Dev Services are so convenient!' 
Predicted Sentiment: POSITIVE 
(Model: Ollama/phi3:mini)
```


Try negative or neutral examples too:

```
curl "http://localhost:8080/sentiment?text=This+local+model+is+slow+sometimes." 
curl "http://localhost:8080/sentiment?text=The+Ollama+container+started+successfully."
```


> **Note:** While local models like Phi-3 Mini are fast and private, they're also smaller and less instruction-tuned than cloud-hosted LLMs, so sentiment predictions might occasionally be off, especially for nuanced or ambiguous text. Fine-tuning examples and careful prompting help, but results may vary.

### Final Thoughts

You've just built a locally-running AI-powered sentiment analysis API in Java using modern open source tools. No cloud credits or platform lock-in required.

**Key takeaways:**

* **Langchain4j** brings LLMs to Java with a familiar, declarative style.
* **Quarkus** simplifies integration and optimizes for fast feedback during dev.
* **Dev Services** automate local infrastructure like Ollama so you can focus on code.

### What's Next?

* Swap out `phi3:mini` for more powerful models like `llama3:8b`.
* Add more few-shot examples to improve accuracy.
* Turn your classifier into a POST endpoint with JSON payloads.
* [Explore Quarkus RAG](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html) features or tool-calling capabilities.

The future of Java and AI is local, fast, and developer-friendly, and Quarkus is leading the way.
