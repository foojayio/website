---
title: "How to Make a RAG Application With LangChain4j"
slug: "how-to-make-a-rag-application-with-langchain4j"
date: "2025-07-17T20:42:12+00:00"
lastmod: "2025-07-17T20:42:13+00:00"
description: "Let's use MongoDB with LangChain4j to create a simple RAG application. LangChain4j abstracted away a lot of the steps along the way, from segmenting our data, to connecting to our MongoDB database and embedding model."
canonical: "https://dev.to/mongodb/how-to-make-a-rag-application-with-langchain4j-1mad"
authors:
  - "tim-kelly"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Machine Learning"
  - "Mongo"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "java-on-azure-tooling-update-september-2022"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
enlighterjs: true
frozen: false
---

Retrieval-augmented generation, or RAG, introduces some serious capabilities to your large language models (LLMs). These applications can answer questions about your specific corpus of knowledge, while leveraging all the nuance and sophistication of a traditional LLM.

This tutorial will take you through the ins and outs of creating a Q\&A chatbot using RAG. The application will:

1. Retrieve data from a MongoDB Atlas database.
2. Embed and store documents as vector embeddings.
3. Use LangChain4J to query the database and augment LLM prompts with the retrieved data.
4. Enable secure, scalable, and efficient AI-powered applications.

If you want to see the completed application, it is available in the [GitHub repository](https://github.com/mongodb-developer/langchainrag).

Why use RAG? {#h2-0-why-use-rag}
--------------------------------

RAG works by retrieving relevant data from your knowledge base and using that information to enrich the input to the LLM. Here are some of the major benefits:

* Sensitive data management: RAG allows you to use sensitive or proprietary data without incorporating it into the LLM's training set. This ensures data privacy and security while still enabling intelligent responses, particularly useful for instances such as GDPR compliance.
* Real-time updates: Instead of retraining the model to include the latest information, an expensive process, RAG enables real-time updates by pulling current data from your knowledge base.
* Increased relevance: By grounding responses in your own corpus of data, RAG ensures answers are both accurate and contextually relevant.

Use cases for RAG {#h2-1-use-cases-for-rag}
-------------------------------------------

RAG is well-suited for a wide variety of applications, including:

* Q\&A applications: Answer user queries with precision, grounded in your company's documentation, FAQs, or internal knowledge base.
* Customer support chatbots: Personalize interactions by referencing customer history, CRM data, and past interactions.
* Dynamic BI tools: Enable business intelligence applications to provide insights using live operational data from databases or spreadsheets.

LangChain4J for RAG {#h2-2-langchain4j-for-rag}
-----------------------------------------------

[LangChain4J](https://docs.langchain4j.dev/) is a Java-based library designed to simplify the integration of LLMs into Java applications by abstracting away a lot of the necessary components in your AI applications. It offers an extensive toolbox for building applications powered by retrieval-augmented generation, enabling us to build quicker and create modular applications.

LangChain4J provides the building blocks to streamline your RAG implementation while maintaining full control over the underlying architecture.

MongoDB for RAG {#h2-3-mongodb-for-rag}
---------------------------------------

MongoDB is an ideal database for RAG implementations due to its:

1. Native vector search: Store and query vector embeddings directly within MongoDB alongside your operational data, enabling retrieval of relevant context.
2. Flexible schema: Add new fields or adjust data models easily without the absolute hassle and mayhem of a data migration.
3. Scalability: Handle high throughput and large datasets with MongoDB's horizontal scaling.
4. Operational efficiency: Use MongoDB's aggregation pipelines, time-series collections, and multimodal capabilities to support both RAG and non-RAG workloads.

Prerequisites {#h2-4-prerequisites}
-----------------------------------

For this tutorial, you will need:

* Java 21 or higher.
* [Maven](https://maven.apache.org/download.cgi) or Gradle (for managing dependencies):
  * We use Maven for this tutorial.
* A [MongoDB Atlas account](https://www.mongodb.com/cloud/atlas/register?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=How%20to%20make%20a%20RAG%20application%20with%20LangChain4j&utm_term=tim.kelly) with a live cluster.
* An [OpenAI API key](https://platform.openai.com/docs/overview).

Our dependencies {#h2-5-our-dependencies}
-----------------------------------------

First things first, let's add our dependencies to our POM:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;  
    &lt;dependency&gt;  
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;  
        &lt;artifactId&gt;langchain4j-open-ai&lt;/artifactId&gt;  
        &lt;version&gt;1.0.0-alpha1&lt;/version&gt;  
    &lt;/dependency&gt;  
    &lt;dependency&gt;  
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;  
        &lt;artifactId&gt;langchain4j-mongodb-atlas&lt;/artifactId&gt;  
        &lt;version&gt;1.0.0-alpha1&lt;/version&gt;  
    &lt;/dependency&gt;  
    &lt;dependency&gt;  
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;  
        &lt;artifactId&gt;langchain4j&lt;/artifactId&gt;  
        &lt;version&gt;1.0.0-alpha1&lt;/version&gt;  
    &lt;/dependency&gt;  
    &lt;dependency&gt;  
        &lt;groupId&gt;com.fasterxml.jackson.core&lt;/groupId&gt;  
        &lt;artifactId&gt;jackson-databind&lt;/artifactId&gt;  
        &lt;version&gt;2.18.1&lt;/version&gt;  
    &lt;/dependency&gt;  
&lt;/dependencies&gt;
</pre>

* langchain4j-open-ai:
  * Embedding generation: Allows you to use OpenAI's embedding models, like `text-embedding-ada-002`, for transforming textual data into vector representations.
  * Chat model integration: Supports communication with OpenAI's GPT models (e.g., GPT-3.5, GPT-4), enabling conversational AI capabilities.
  * Simplified API calls: Abstracts the complexity of interacting with the OpenAI API, reducing boilerplate code and improving developer productivity.
* langchain4j-mongodb-atlas:
  * Embedding store management: Simplifies storing and retrieving embeddings in MongoDB, making it an ideal solution for RAG applications.
  * Vector search support: Enables high-performance vector similarity queries using MongoDB Atlas's native capabilities.
  * Metadata handling: Allows storing and querying additional metadata associated with embeddings, which is vital for building rich, context-aware systems.
* langchain4j: Provides the tools for building RAG workflows. It includes:
  * Classes for text segmentation, document splitting, and chunking, which help break down large documents into manageable pieces.
  * Utilities for connecting and orchestrating various components like embedding models, vector stores, and content retrievers.
* Jackson Databind simplifies the process of loading and handling JSON data.

Setting up MongoDB and our embedding store {#h2-6-setting-up-mongodb-and-our-embedding-store}
---------------------------------------------------------------------------------------------

To make our retrieval-augmented generation application work effectively, we need a robust and scalable solution for storing and querying embeddings. MongoDB, with its Atlas Search capabilities, serves as the backbone for this task. In this section, we'll walk through how to set up MongoDB and configure an embedding store using LangChain4J's MongoDB integration.

### MongoDB setup {#h3-7-mongodb-setup}

The first step is to initialize a connection to our MongoDB cluster. We use the `MongoClient` from the MongoDB Java driver to connect to our database:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

import com.fasterxml.jackson.databind.JsonNode;  
import com.fasterxml.jackson.databind.ObjectMapper;  
import com.mongodb.client.MongoClient;  
import com.mongodb.client.MongoClients;  
import com.mongodb.client.model.CreateCollectionOptions;  
import dev.langchain4j.data.document.Document;  
import dev.langchain4j.data.document.DocumentSplitter;  
import dev.langchain4j.data.document.Metadata;  
import dev.langchain4j.data.embedding.Embedding;  
import dev.langchain4j.data.segment.TextSegment;  
import dev.langchain4j.model.chat.ChatLanguageModel;  
import dev.langchain4j.model.openai.OpenAiChatModel;  
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;  
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;  
import dev.langchain4j.model.openai.OpenAiTokenizer;  
import dev.langchain4j.rag.content.retriever.ContentRetriever;  
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;  
import dev.langchain4j.store.embedding.*;  
import dev.langchain4j.store.embedding.mongodb.IndexMapping;  
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;  
import dev.langchain4j.service.AiServices;  
import org.bson.conversions.Bson;  
import dev.langchain4j.data.document.splitter.DocumentSplitters;

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // MongoDB setup  
            MongoClient mongoClient = MongoClients.create("CONNECTION_URI");  

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
</pre>

Replace `"CONNECTION_URI"` with your actual MongoDB connection string, which includes your database credentials and cluster information. This connection will be used to interact with the database and perform operations like storing and retrieving embeddings.

We are also adding in our whole host of imports we will use throughout this tutorial. Don't worry, we will go through all these as we add them to our application.

### Configuring the embedding store {#h3-8-configuring-the-embedding-store}

The embedding store is the corpus of knowledge of our RAG application, where all embeddings and their associated metadata are stored. Let's add a method and call it from our main:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static EmbeddingStore&lt;TextSegment&gt; createEmbeddingStore(MongoClient mongoClient) {
    String databaseName = "rag_app";
    String collectionName = "embeddings";
    String indexName = "embedding";
    Long maxResultRatio = 10L;
    CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions();
    Bson filter = null;
    Set&lt;String&gt; metadataFields = new HashSet&lt;&gt;();
    IndexMapping indexMapping = new IndexMapping(1536, metadataFields);
    Boolean createIndex = true;

    return new MongoDbEmbeddingStore(
            mongoClient,
            databaseName,
            collectionName,
            indexName,
            maxResultRatio,
            createCollectionOptions,
            filter,
            indexMapping,
            createIndex
    );
}
</pre>

Let's explore the parameters we set up:

1. Database name (`databaseName`) We specify `"rag_app"` as the database where our embeddings will be stored. You can rename this to suit your application.
2. Collection name (`collectionName`) The collection `"embeddings"` will hold the embedding data and metadata. Collections in MongoDB are analogous to tables in relational databases.
3. Index name (`indexName`) The `"embedding"` index enables efficient vector search operations. This index is crucial for retrieving relevant embeddings quickly based on similarity scores.
4. Max result ratio (`maxResultRatio`) This defines the maximum number of results to return during retrieval, keeping the results manageable.
5. Create collection options (`createCollectionOptions`) Options for creating the collection can be customized here. For example, you could configure specific validation rules or shard keys.
6. Filter (`filter`) Currently set to `null`, this can be used to define custom filtering criteria if needed for specific retrieval operations.
7. Metadata fields (`metadataFields`) A set of metadata field names that can be indexed alongside embeddings for richer search capabilities, this allows for queries based on both vector similarity and metadata.
8. Index mapping (`indexMapping`) This maps the dimensionality of the embedding vectors (e.g., `1536` for OpenAI's `text-embedding-ada-002`). This ensures compatibility with the vector model being used.
9. Create index (`createIndex`) When set to `true`, this flag ensures that the necessary index for vector searches is created automatically.

In the main method, we call this method and assign the result to an `EmbeddingStore` instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // MongoDB setup  
            MongoClient mongoClient = MongoClients.create("CONNECTION_URI");  

            // Embedding Store  
EmbeddingStore&lt;TextSegment&gt; embeddingStore = createEmbeddingStore(mongoClient);

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
</pre>

This `embeddingStore` is now ready to store, retrieve, and manage our embeddings, with all the beauty and benefits of MongoDB behind it.

Creating our embedding model {#h2-9-creating-our-embedding-model}
-----------------------------------------------------------------

The embedding model is the engine that converts raw text into numerical representations, also known as embeddings. These embeddings are high-dimensional representations of our data that capture the semantic meaning of text, making them the foundation for similarity searches in a retrieval-augmented generation application.

In this section, we set up an embedding model using OpenAI's `text-embedding-ada-002`. To configure the embedding model, we use LangChain4J's `OpenAiEmbeddingModel` builder, which abstracts the complexities of interacting with OpenAI's API. Here's the implementation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // ...

            // Embedding Model setup  
OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()  
        .apiKey("OPEN_AI_API_KEY")  
        .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)  
        .build();

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}

// ...
</pre>

1. API key (`apiKey`) The API key provides access to OpenAI's services. Replace `"OPEN_AI_API_KEY"` with your actual OpenAI API key.
2. Model name (`modelName`) We specify `OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002`. This model offers:
   * High dimensionality (1536 dimensions): Captures detailed semantic information.
   * General-purpose embeddings: Suitable for a multitude of embedding tasks like document retrieval, clustering, and classification.

This embedding model is critical for generating vector representations of the text data we work with.

Configuring our chat model {#h2-10-configuring-our-chat-model}
--------------------------------------------------------------

In a retrieval-augmented generation application, the chat model serves as the conversational engine. It generates our context-aware, human-like responses based on the user's query and retrieved content. For this tutorial, we configure a chat model using OpenAI's GPT-4 (other AI models are available), simplified by LangChain4J's straightforward API.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // ...

            // Chat Model setup
            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey("OPEN_AI_API_KEY")
                    .modelName("gpt-4")
                    .build();

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}

// ...
</pre>

Replace the API key, just as before. We also specify the model name here.

The chat model becomes the core of answering user queries in the RAG flow:

1. Retrieve relevant content: The embedding store retrieves relevant documents based on the user's query.
2. Generate a response: The chat model uses the retrieved content as context to generate a detailed and accurate answer.

For instance, a query like:

"How does Atlas Vector Search work?"

Would involve retrieving our related embeddings about Atlas Vector Search from the MongoDB vector store, and then GPT-4 would generate a response using that context.

How to load our data {#h2-11-how-to-load-our-data}
--------------------------------------------------

We are going to be loading our data, which we can download from [MongoDB's Hugging Face](https://huggingface.co/datasets/MongoDB/devcenter-articles). It is a collection of approximately 600 articles and tutorials from [MongoDB's Developer Center](https://www.mongodb.com/developer/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=How%20to%20make%20a%20RAG%20application%20with%20LangChain4j&utm_term=tim.kelly). We are going to place this [`devcenter-content-snapshot.2024-05-20 1.json`](https://huggingface.co/datasets/MongoDB/devcenter-articles/tree/main) file into the resources folder.

Now, we need a method `loadJsonDocuments` to handle our logic. The `loadJsonDocuments` method handles loading and processing the dataset. It reads the JSON file, extracts relevant content (title, body, metadata), and splits it into smaller segments for embedding.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static List&lt;TextSegment&gt; loadJsonDocuments(String resourcePath, int maxTokensPerChunk, int overlapTokens) throws IOException {
    List&lt;TextSegment&gt; textSegments = new ArrayList&lt;&gt;();

    // Load file from resources using the ClassLoader
    InputStream inputStream = LangChainRagApp.class.getClassLoader().getResourceAsStream(resourcePath);

    if (inputStream == null) {
        throw new FileNotFoundException("Resource not found: " + resourcePath);
    }

    // Jackson ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    // Batch size for processing
    int batchSize = 500;  // Adjust batch size as needed
    List&lt;Document&gt; batch = new ArrayList&lt;&gt;();

    String line;
    while ((line = reader.readLine()) != null) {
        JsonNode jsonNode = objectMapper.readTree(line);

        String title = jsonNode.path("title").asText(null);
        String body = jsonNode.path("body").asText(null);
        JsonNode metadataNode = jsonNode.path("metadata");

        if (body != null) {
            String text = (title != null ? title + "\n\n" + body : body);

            Metadata metadata = new Metadata();
            if (metadataNode != null &amp;&amp; metadataNode.isObject()) {
                Iterator&lt;String&gt; fieldNames = metadataNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    metadata.put(fieldName, metadataNode.path(fieldName).asText());
                }
            }

            Document document = Document.from(text, metadata);
            batch.add(document);

            // If batch size is reached, process the batch
            if (batch.size() &gt;= batchSize) {
                textSegments.addAll(splitIntoChunks(batch, maxTokensPerChunk, overlapTokens));
                batch.clear();
            }
        }
    }

    // Process remaining documents in the last batch
    if (!batch.isEmpty()) {
        textSegments.addAll(splitIntoChunks(batch, maxTokensPerChunk, overlapTokens));
    }

    return textSegments;
}
</pre>

Documents need to be divided into smaller chunks to fit within the token limits of our embedding model. We achieve this using the `splitIntoChunks` method. Here, we will use `DocumentSplitter`, a tool provided to us by LangChain4j to divide our documents into manageable chunks, while maintaining the original context they provide.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static List&lt;TextSegment&gt; splitIntoChunks(List&lt;Document&gt; documents, int maxTokensPerChunk, int overlapTokens) {  
    // Create a tokenizer for OpenAI  
    OpenAiTokenizer tokenizer = new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002);  

    // Create a recursive document splitter with the specified token size and overlap  
    DocumentSplitter splitter = DocumentSplitters.recursive(  
            maxTokensPerChunk,  
            overlapTokens,  
            tokenizer  
    );  

    List&lt;TextSegment&gt; allSegments = new ArrayList&lt;&gt;();  
    for (Document document : documents) {  
        List&lt;TextSegment&gt; segments = splitter.split(document);  
        allSegments.addAll(segments);  
    }  

    return allSegments;  
}
</pre>

### Parameters {#h3-12-parameters}

* `maxTokensPerChunk`: Maximum tokens allowed in each segment. This ensures compatibility with the model's token limit.
* `overlapTokens`: Number of overlapping tokens between consecutive chunks. Overlaps help preserve context across segments.

Now, time to add this to our main method. The main method orchestrates the entire process: loading the data, embedding it, and storing it in the embedding store.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // ...

            // Load documents
            String resourcePath = "devcenter-content-snapshot.2024-05-20.json";
            List&lt;TextSegment&gt; documents = loadJsonDocuments(resourcePath, 800, 200);

            System.out.println("Loaded " + documents.size() + " documents");

            for (int i = 0; i &lt; documents.size()/10; i++) {
                TextSegment segment = documents.get(i);
                Embedding embedding = embeddingModel.embed(segment.text()).content();
                embeddingStore.add(embedding, segment);
            }

            System.out.println("Stored embeddings");

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}

// ...
</pre>

I added a few comments here to help us track our progress as we ingest our data. I also adjusted to only intake the first 10% of the documents. When I did this with the entire dataset, it took 30+ minutes to load in all the data on my slow internet. Feel free to adjust this, as the more data ingested, the more potentially accurate the answers.

Creating our content retriever {#h2-13-creating-our-content-retriever}
----------------------------------------------------------------------

In our retrieval-augmented generation application, the Content Retriever fetches the most relevant content from a data source based on our user query. LangChain4J provides an abstraction for this, allowing us to connect our embedding store and embedding model to retrieve content.

We use the `EmbeddingStoreContentRetriever` to retrieve content from the embedding store by embedding the user query and finding the most relevant matches.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // ...

            // Content Retriever
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(5)
                    .minScore(0.75)
                    .build();

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}

// ...
</pre>

Let's break down what makes this Content Retriever:

* `embeddingStore`: This is our corpus of data we set up earlier. It's where all the vectorized representations of our documents live.
* `embeddingModel`: This is the brains behind the operation. It's the same model we used to create the embeddings (e.g., `text-embedding-ada-002`). By using the same model here, we ensure that the user's query is embedded in the same "language" as the stored content.
* `maxResults`: Setting `maxResults` to `5` means the retriever will hand us up to five of the most relevant matches for your query.
* `minScore`: This is your quality filter. By setting a `minScore` of `0.75`, we're saying, "Don't bother showing me anything that's not highly relevant." If none of the results meet this threshold, we'll get an empty list instead of cluttered, irrelevant data.

By tweaking these parameters, we can fine-tune how your retriever performs, ensuring it delivers exactly what we need!

Asking questions {#h2-14-asking-questions}
------------------------------------------

Time to put the pieces together. We need a way to bring all our components together to query our enhanced LLM. First, we need to create an interface for our assistant. Create an interface, as shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public interface Assistant {  

    String answer(String question);  
}
</pre>

Keeping it very simple, we just want to provide a question and get an answer. Next, we need to create and call our assistant in our main class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;  

public class LangChainRagApp {  

    public static void main(String[] args) {  
        try {  
            // ...

            // Assistant
            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(chatModel)
                    .contentRetriever(contentRetriever)
                    .build();

            String output = assistant.answer("How to use Atlas Triggers and AI to summarise AirBnB reviews?");

            System.out.println(output);

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}

// ...
</pre>

Now, in this implementation, I kept it very simple and kept the query in line. There is nothing stopping you from implementing the querying system in an API, or in terminal, or any other way you can imagine. Let's take a look at our reply:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">To summarise Airbnb reviews using MongoDB Atlas Triggers and OpenAI, follow these steps:

1. **Prerequisites**: Set up an App Services application to link to the cluster with Airbnb data. Also, create an OpenAI account with API access.

2. **Set up Secrets and Values**: In App Services, create a secret named `openAIKey` with your OpenAI API key. Then, create a linked value named `OpenAIKey` and link it to the secret.

3. **Trigger code**: The trigger listens for changes in the sample_airbnb.listingsAndReviews collection. When a new review is detected, it samples up to 50 reviews, sends them to OpenAI's API for summarisation, and updates the original document with the summarised content and tags. The trigger reacts to updates that are marked with the `"process" : false` flag, which indicates that a summary hasn't been created for the batch of reviews yet.

4. **Sample Reviews Function**: To avoid overloading the API with too many reviews, a function called `sampleReviews` is defined that randomly samples up to 50 reviews.

5. **API Interaction**: Using the `context.http.post` method, the API request is sent to the OpenAI API.

6. **Updating the Original Document**: Once a successful response from the API is received, the trigger updates the original document with the summarised content, negative tags (neg_tags), positive tags (pos_tags), and a process flag set to true.

7. **Displaying the Data**: Once the data is added to the documents, it can be displayed in a VUE application by adding an HTML template.

By combining MongoDB Atlas triggers with OpenAI's powerful models, large volumes of reviews can be processed and analysed in real-time. This not only provides concise summaries of reviews but also categorises them into positive and negative tags, offering valuable insights to property hosts and potential renters.

</pre>

This is a well informed response, and actually references the information available in the original tutorial, [Using MongoDB Atlas Triggers to Summarize Airbnb Reviews With OpenAI](https://www.mongodb.com/developer/products/mongodb/atlas-open-ai-review-summary/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=How%20to%20make%20a%20RAG%20application%20with%20LangChain4j&utm_term=tim.kelly). Want the code? Just ask in the query. It will tailor the responses to exactly what you ask!

Conclusion {#h2-15-conclusion}
------------------------------

There we have it---we used MongoDB with LangChain4j to create a simple RAG application. LangChain4j abstracted away a lot of the steps along the way, from segmenting our data, to connecting to our MongoDB database and embedding model.

If you found this tutorial useful, head over to the Developer Center and check out some of our other tutorials, such as [Terraforming AI Workflows: RAG With MongoDB Atlas and Spring AI](https://www.mongodb.com/developer/languages/java/terraform-springai-rag/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=How%20to%20make%20a%20RAG%20application%20with%20LangChain4j&utm_term=tim.kelly), or head over to [LangChain4j](https://docs.langchain4j.dev/) to learn more about what you can do with MongoDB and AI in Java.

[](https://dev.to/embeddable-hq)
