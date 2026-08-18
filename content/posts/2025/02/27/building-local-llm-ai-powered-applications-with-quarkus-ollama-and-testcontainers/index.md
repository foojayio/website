---
title: "Build local LLM applications with Quarkus, Ollama, Testcontainers"
slug: "building-local-llm-ai-powered-applications-with-quarkus-ollama-and-testcontainers"
date: "2025-02-27T10:13:19+00:00"
lastmod: "2025-05-06T10:50:02+00:00"
description: "Explore how to combine Quarkus with Ollama, a platform for running AI models locally. We’ll also demonstrate how tools like Testcontainers and Quarkus Dev Services simplify development and testing workflows"
authors:
  - "jonathan-vila"
image: "https___dev-to-uploads.s3.amazonaws.com_uploads_articles_defv9j6s88xk87tot29t.png"
categories:
  - "Java"
  - "LangChain4j"
  - "Testcontainers"
tags:
related_posts:
  - "book-review-developing-apps-with-gpt-4-and-chatgpt"
  - "building-simple-home-assistant-langchain4j-raspberry-pi"
  - "foojay-podcast-47"
  - "foojay-podcast-56"
enlighterjs: true
frozen: false
---

Traditionally, many AI-powered applications rely on cloud-based APIs or centralized services for model hosting and execution. While this approach has its advantages, such as scalability and ease of use, it also introduces challenges around latency, data privacy, and dependency on third-party providers.

This is where local AI models shine. By running models directly within your application's infrastructure, you gain greater control over performance, data security, and deployment flexibility. However, building such systems requires the right tools and frameworks to bridge the gap between traditional software development and AI model integration.

In this article, we explore how to combine Quarkus, a modern Java framework optimized for cloud-native applications, with Ollama, a platform for running AI models locally. We'll also demonstrate how tools like Testcontainers and Quarkus Dev Services simplify development and testing workflows. Using the PingPong-AI project as a practical example, you'll learn how to build and test AI-driven applications that harness the power of local models.

The PingPong-AI project demonstrates a simple implementation of AI-powered functionality using Quarkus as the backend framework and Ollama for handling AI models. Let's break down the architecture and walk through key components of the code.



Project Overview
----------------

In PingPong-AI, Ollama is used to simulate a simple conversation model where a curious service generates questions around a topic and a wise service responds with informated answers, that will generate more questions on the curious service.

The project integrates Quarkus with Ollama to create an AI model-driven application. It leverages Quarkus's lightweight and fast development model to serve as a backend for invoking and managing AI interactions. Here's an overview of what we'll cover:

1. Integrating Quarkus with Ollama
2. Using Testcontainers for Integration Testing
3. Leveraging Quarkus Dev Services for Simplified Development



1. Integrating Quarkus with Ollama
----------------------------------

### Why Ollama?

Ollama simplifies the deployment and use of AI models in applications. It provides a runtime for AI model execution and can be easily integrated into existing applications.

### Quarkus and Ollama Integration

The integration with Quarkus is handled using REST endpoints to interact with Ollama. The Quarkus application serves as the middleware to process client requests and communicate with the Ollama runtime.

Here's a snippet from `PingPongResource.java`, which defines the REST endpoint for the PingPong interaction:

```java
@Path("/chat")
public class CuriousChatResource {
    @Inject
    CuriousService curiousService;

    @Inject
    WiseService wiseService;

   @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{numberOfQuestions}")
    public String chat(@PathParam("numberOfQuestions") Integer numberOfQuestions, String topic) {
        StringBuilder sb = new StringBuilder();
        sb.append("> Topic: ").append(topic).append("\n");
        while (numberOfQuestions-- > 0) {
            String question = curiousService.chat(topic);
            sb.append("> Question: ").append(question).append("\n");
            topic = wiseService.chat(question);
            sb.append("> Answer: ").append(topic).append("\n");
        }

        return sb.toString();
    }
}
```


In this code:

* The `CuriousChatResource` handles client POST requests.
* The injected `CuriousService` and `WiseService` interface with the Ollama runtime to process the message.

These Ollama services are responsible for calling the Ollama runtime. Here's a snippet from `CuriousService.java`:

```java
@RegisterAiService
@SystemMessage("You are a curious person that creates a short question for every message you receive.")
public interface CuriousService {
    public String chat(@UserMessage String message);
}
```


We can even use different models for each service, specifying the configuration property that identifies the model. This example comes from `CuriousService.java`:

```java
@RegisterAiService(modelName = "curiousModel")
```


And we identify the model in `application.properties` :

```
quarkus.langchain4j.ollama.wiseModel.chat-model.model-id=tinydolphin
quarkus.langchain4j.ollama.curiousModel.chat-model.model-id=tinyllama
```




2. Using Testcontainers for Integration Testing
-----------------------------------------------

### Why Testcontainers?

**Testcontainers** is a Java library for running lightweight, disposable containers during tests. In PingPong-AI, Testcontainers are used to set up an environment with an Ollama runtime for integration testing.

### Example: Setting up a Testcontainer for Ollama

The test class demonstrates how to configure and use Testcontainers:

```java
@QuarkusTest
class CuriousChatResourceTest {
    @Inject
    CuriousService curiousService;

    @Inject
    WiseService wiseService;

    @Test
    @ActivateRequestContext
    void testFXMainControllerInteraction() {

        // Perform interaction and assertions
        var curiousAnswer = curiousService.chat("Barcelona");
        var response = wiseService.chat(curiousAnswer);

        Log.infof("Wise service response: %s", response);
        // Using llama2 model we can check if the response contains 'Barcelona', but not
        // with tinyllama
        assertFalse(response.isEmpty(), "Response should not be empty");
    }
    @Test
    @ActivateRequestContext
    void testChatEndpoint() {
        given()
            .when()
                .body("Barcelona")
                .contentType(ContentType.TEXT)
                .post("/chat/3")
            .then()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(not(empty()))
                .body(org.hamcrest.Matchers.stringContainsInOrder("Question:", "Answer:", "Question:", "Answer:", "Question:", "Answer:"));

    }
}
```


### Key Points:

* The `@QuarkusTest` annotation allows Quarkus to run the application in a test-friendly mode.
* Quarkus finds a service (Ollama) for which it needs an instance and it will spin up a container for that.



3. Leveraging Quarkus Dev Services for Ollama
---------------------------------------------

### What Are Quarkus Dev Services?

Quarkus Dev Services simplifies the setup of required services during development. For this project, Quarkus Dev Services can spin up an Ollama runtime container automatically.

### Configuring Dev Services

The `application.properties` file includes configurations for enabling Dev Services:

```bash
quarkus.langchain4j.ollama.chat-model.model-id=tinyllama
quarkus.langchain4j.ollama.devservices.model=tinyllama
quarkus.langchain4j.log-requests=true
```


With these configurations, Quarkus Dev Services automatically starts an Ollama container when the application is run in development mode or in test, removing the need for manual setup.

### Development Workflow

You can launch the application in development mode using the following command:

```bash
./mvnw quarkus:dev
```


This command:

* Starts the Quarkus application.
* Automatically sets up an Ollama runtime container.
* Enables hot-reloading for rapid development.



Conclusion
----------

The PingPong-AI project demonstrates a seamless integration of Quarkus with Ollama, making it easy to build AI-powered applications. By leveraging Testcontainers and Quarkus Dev Services, developers can efficiently test and develop their applications in a containerized and automated environment.

### Key Takeaways:

* Quarkus provides a lightweight framework for building and deploying Java applications.
* Ollama simplifies AI model integration with backend systems.
* Testcontainers and Dev Services streamline the testing and development workflows.

To explore this project further, check out the [PingPong-AI GitHub repository](https://github.com/jonathanvila/pingpong-ai).
