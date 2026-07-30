---
title: "Calling Gemma with Ollama, TestContainers, and LangChain4j"
slug: "calling-gemma-with-ollama-testcontainers-and-langchain4j"
date: "2024-04-12T13:55:38+00:00"
lastmod: "2024-04-12T13:55:40+00:00"
description: "Lately, for my Generative AI powered Java apps, I've used the Gemini multimodal large language model from Google. But there's also Gemma, its little sister model."
canonical: "https://glaforge.dev/posts/2024/04/04/calling-gemma-with-ollama-and-testcontainers/"
authors:
  - "guillaume-laforge"
image: "/images/posts/2024/04/calling-gemma-with-ollama-testcontainers-and-langchain4j/gemma-ollama-testcontainers-langchain4j-700x360-1.png"
categories:
  - "Java"
  - "Machine Learning"
tags:
related_posts:
  - "quick-start-with-machine-learning-in-java"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "faster-integration-tests-with-reusable-testcontainers"
  - "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
enlighterjs: true
frozen: false
---

**Lately, for my Generative AI powered Java apps, I've used the [Gemini](https://deepmind.google/technologies/gemini/#introduction) multimodal large language model from Google. But there's also [Gemma](https://blog.google/technology/developers/gemma-open-models/), its little sister model.**

Gemma is a family of lightweight, state-of-the-art open models built from the same research and technology used to create the Gemini models.

Gemma is available in two sizes: 2B and 7B. Its weights are freely available, and its small size means you can run it on your own, even on your laptop. So I was curious to give it a run with [LangChain4j](https://docs.langchain4j.dev/).

How to run Gemma {#h2-0-how-to-run-gemma}
-----------------------------------------

There are many ways to run Gemma: in the cloud, via [Vertex AI](https://console.cloud.google.com/vertex-ai/publishers/google/model-garden/335) with a click of a button, or [GKE](https://cloud.google.com/kubernetes-engine/docs/tutorials/serve-gemma-gpu-vllm) with some GPUs, but you can also run it locally with [Jlama](https://github.com/tjake/Jlama) or [Gemma.cpp](https://github.com/google/gemma.cpp).

Another good option is to run Gemma with [Ollama](https://ollama.com/), a tool that you install on your machine, and which lets you run small models, like Llama 2, Mistral, and [many others](https://ollama.com/library). They quickly added support for [Gemma](https://ollama.com/library/gemma) as well.

Once installed locally, you can run:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ollama run gemma:2b
ollama run gemma:7b</pre>

Cherry on the cake, the \[LangChain4j\]() library provides an [Ollama module](https://docs.langchain4j.dev/integrations/language-models/ollama), so you can plug Ollama supported models in your Java applications easily.

Containerization {#h2-1-containerization}
-----------------------------------------

After a great discussion with my colleague [Dan Dobrin](https://twitter.com/ddobrin) who had worked with Ollama and TestContainers ([#1](https://github.com/GoogleCloudPlatform/serverless-production-readiness-java-gcp/blob/main/sessions/next24/books-genai-vertex-langchain4j/src/test/java/services/OllamaContainerTest.java) and [#2](https://github.com/GoogleCloudPlatform/serverless-production-readiness-java-gcp/blob/main/sessions/next24/books-genai-vertex-langchain4j/src/test/java/services/OllamaChatModelTest.java#L37)) in his [serverless production readiness workshop](https://github.com/GoogleCloudPlatform/serverless-production-readiness-java-gcp/tree/main), I decided to try the approach below.

Which brings us to the last piece of the puzzle: Instead of having to install and run Ollama on my computer, I decided to use Ollama within a container, handled by [TestContainers](https://testcontainers.com/).

TestContainers is not only useful for testing, but you can also use it for driving containers. There's even a specific [OllamaContainer](https://java.testcontainers.org/modules/ollama/) you can take advantage of!

So here's the whole picture:

![](/images/posts/2024/04/calling-gemma-with-ollama-testcontainers-and-langchain4j/gemma-ollama-testcontainers-langchain4j-700x360.png)

Time to implement this approach! {#h2-2-time-to-implement-this-approach}
------------------------------------------------------------------------

You'll find the code in the Github [repository](https://github.com/glaforge/gemini-workshop-for-java-developers/blob/main/app/src/main/java/gemini/workshop/CallGemma.java) accompanying my recent [Gemini workshop](https://codelabs.developers.google.com/codelabs/gemini-java-developers)

Let's start with the easy part, interacting with an Ollama supported model with LangChain4j:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">OllamaContainer ollama = createGemmaOllamaContainer();
ollama.start();

ChatLanguageModel model = OllamaChatModel.builder()
    .baseUrl(String.format("http://%s:%d", ollama.getHost(), ollama.getFirstMappedPort()))
    .modelName("gemma:2b")
    .build();

String response = model.generate("Why is the sky blue?");

System.out.println(response);</pre>

* You run an Ollama test container.
* You create an Ollama chat model, by pointing at the address and port of the container.
* You specify the model you want to use.
* Then, you just need to call `model.generate(yourPrompt)` as usual.

Easy? Now let's have a look at the trickier part, my local method that creates the Ollama container:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// check if the custom Gemma Ollama image exists already
List&lt;Image&gt; listImagesCmd = DockerClientFactory.lazyClient()
    .listImagesCmd()
    .withImageNameFilter(TC_OLLAMA_GEMMA_2_B)
    .exec();

if (listImagesCmd.isEmpty()) {
    System.out.println("Creating a new Ollama container with Gemma 2B image...");
    OllamaContainer ollama = new OllamaContainer("ollama/ollama:0.1.26");
    ollama.start();
    ollama.execInContainer("ollama", "pull", "gemma:2b");
    ollama.commitToImage(TC_OLLAMA_GEMMA_2_B);
    return ollama;
} else {
    System.out.println("Using existing Ollama container with Gemma 2B image...");
    // Substitute the default Ollama image with our Gemma variant
    return new OllamaContainer(
        DockerImageName.parse(TC_OLLAMA_GEMMA_2_B)
            .asCompatibleSubstituteFor("ollama/ollama"));
}</pre>

You need to create a derived Ollama container that pulls in the Gemma model. Either this image was already created beforehand, or if it doesn't exist yet, you create it.

Use the Docker Java client to check if the custom Gemma image exists. If it doesn't exist, notice how TestContainers let you create an image derived from the base Ollama image, pull the Gemma model, and then commit that image to your local Docker registry.

Otherwise, if the image already exists (ie. you created it in a previous run of the application), you're just going to tell TestContainers that you want to substitute the default Ollama image with your Gemma-powered variant.

And voila! {#h2-3-and-voila}
----------------------------

You can **call Gemma locally on your laptop, in your Java apps, using LangChain4j**, without having to install and run Ollama locally (but of course, you need to have a Docker daemon running).

Big thanks to [Dan Dobrin](https://twitter.com/ddobrin) for the approach, and to [Sergei](https://twitter.com/bsideup), [Eddú](https://twitter.com/EdduMelendez) and [Oleg](https://twitter.com/shelajev) from TestContainers for the help and useful pointers.

<br />

<br />
