---
title: "Spring AI: How to Write GenAI Applications with Java"
slug: "spring-ai-how-to-write-genai-applications-with-java"
date: "2024-05-09T18:46:55+00:00"
lastmod: "2024-05-09T21:46:43+00:00"
description: "We'll look at how to write GenAI applications with Java using the Spring AI framework and utilize RAG for improving answers."
authors:
  - "jennifer-reif"
image: "unsplash-ai-chip-scaled.jpg"
categories:
  - "Graph"
  - "Java"
  - "Neo4J"
  - "Spring"
tags:
related_posts:
  - "foojay-podcast-47"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "fabiane-nardon-machine-learning-data-science"
  - "intro-to-rag-foundations-of-retrieval-augmented-generation-part-2"
frozen: false
---

Generative AI (GenAI) is currently a hot topic in the tech world. It's a subset of artificial intelligence that focuses on creating new content, such as text, images, or music. One popular type of GenAI component is the Large Language Model (LLM), which can generate human-like text based on a prompt.

Retrieval-Augmented Generation (RAG) is a technique that enhances the accuracy and reliability of generative AI models by grounding them in external knowledge sources. While most GenAI applications and related content are centered around Python and its ecosystem, what if you want to write a GenAI application in Java?

In this blog post, we'll look at how to write GenAI applications with Java using the Spring AI framework and utilize RAG for improving answers.

## What is Spring AI?

Spring AI is a framework for building generative AI applications in Java. It provides a set of tools and utilities for working with generative AI models and architectures, such as large language models (LLMs) and retrieval augmented generation (RAG).

Spring AI is built on top of the Spring Framework, which is a popular Java framework for building enterprise applications, allowing those already familiar with or involved in the Spring ecosystem the ability to incorporate GenAI strategies to their already existing applications and workflow.

There are also other options for GenAI in Java, such as Langchain4j, but we'll focus on Spring AI for this post.

## Creating a project

To get started with Spring AI, you'll need to either create a new project or add the appropriate dependencies to an existing project. You can create a new project using the Spring Initializr at <https://start.spring.io/>, which is a web-based tool for generating Spring Boot projects.

When creating a new project, you'll need to add the following dependencies:

* Spring Web
* OpenAI (or other LLM model, such as Mistral, Ollama, etc.)
* Neo4j Vector Database (other vector database options also available)
* Spring Data Neo4j

If you're adding these dependencies manually to an existing project, you can see the dependency details in [today's related Github repository](https://github.com/JMHReif/springai-goodreads).

The Spring Web dependency allows us to create a REST API for our GenAI application. We need the OpenAI dependency to access the OpenAI model, which is a popular LLM. The Neo4j Vector Database dependency allows us to store and query vectors, which are used for similarity searches.

Finally, adding the Spring Data Neo4j dependency provides support for working with Neo4j databases in Spring applications, allowing us to run Cypher queries in Neo4j and map entities to Java objects.

Go ahead and generate the project, and then open it in your favorite IDE. Looking at the `pom.xml` file, you should see that the milestone repository is included. Since Spring AI is not a general-availability release yet, we need to include the milestone repository to get the pre-release version of the dependencies.

## A bit of boilerplate

First thing that we need is a Neo4j database. I like to use the [Neo4j Aura free tier](https://dev.neo4j.com/aura-java) because the instance is managed for me, but there are also Docker images and other methods.

Depending on the LLM model you chose, you will also need an API key. For OpenAI, you can get one by signing up at [OpenAI](https://platform.openai.com/signup).

Once you have a Neo4j database and an API key, you can set up the config in the `application.properties` file. Here's an example of what that might look like:

```properties
spring.ai.openai.api-key=<YOUR API KEY HERE>
spring.neo4j.uri=<NEO4J URI HERE>
spring.neo4j.authentication.username=<NEO4J USERNAME HERE>
spring.neo4j.authentication.password=<NEO4J PASSWORD HERE>
spring.data.neo4j.database=<NEO4J DATABASE NAME HERE>
```

Note: It's a good idea to keep sensitive information like API keys and passwords in environment variables or other location external to the application. To create environment variables, you can use the `export` command in the terminal or set them in your IDE.

We can set up [Spring Beans](https://www.baeldung.com/spring-bean) for the Neo4j driver, OpenAI client, and the Neo4j vector store that will allow us to access necessary components wherever we need them in our application.

We can put these in our `SpringAiApplication` class by adding the following code to the class:

```java
@Bean
public Driver driver() {
	return GraphDatabase.driver(System.getenv("SPRING_NEO4J_URI"),
		AuthTokens.basic(System.getenv("SPRING_NEO4J_AUTHENTICATION_USERNAME"),
			System.getenv("SPRING_NEO4J_AUTHENTICATION_PASSWORD")));
}

@Bean
public EmbeddingClient embeddingClient() {
	return new OpenAiEmbeddingClient(new OpenAiApi(System.getenv("SPRING_AI_OPENAI_API_KEY")));
}

@Bean
public Neo4jVectorStore vectorStore(Driver driver, EmbeddingClient embeddingClient) {
	return new Neo4jVectorStore(driver, embeddingClient,
		Neo4jVectorStore.Neo4jVectorStoreConfig.builder()
			.withLabel("Review")
			.withIndexName("review-embedding-index")
			.build());
}
```

The `Driver` bean creates a connection to the Neo4j database by passing in the credentials for our instance (in this case, from environment variables). The `EmbeddingClient` bean creates a client for the OpenAI API and passes in our API key environment variable. Lastly, the `Neo4jVectorStore` bean configures Neo4j as the store for embeddings (vectors).

We customize the configuration by specifying the label for the nodes that will store the embeddings, as Spring's default looks for `Document` entities. We also specify our index name for the embeddings (default is `spring-ai-document-index`).

## Data set

For this example, we'll use a dataset of books and reviews from Goodreads. You can pull a curated version of the dataset from [here](https://github.com/JMHReif/graph-demo-datasets/blob/main/goodreadsUCSD/ai-embeddings/ai-load-data.cypher). The dataset contains information about books, as well as related reviews.

I have already generated embeddings using OpenAI's API, so if you want to generate your own, you will need to comment out the [final Cypher statement in the script](https://github.com/JMHReif/graph-demo-datasets/blob/main/goodreadsUCSD/ai-embeddings/ai-load-data.cypher#L92) and instead run the `generate-embeddings.py` script (or your custom version) to generate and load the review embeddings to Neo4j.

## Application model

Next, we need to create a domain model in our application to map to our database model. In this example, we'll create a `Book` entity that represents a book node. We'll also create a `Review` entity that represents a review of a book. The `Review` entity will have an embedding (vector) associated with it, which we'll use for similarity searches.

These entities are standard Spring Data Neo4j code, so I won't show the code here. However, full code for each class is available in the Github repository - [Book class](https://github.com/JMHReif/springai-goodreads/blob/main/src/main/java/com/jmhreif/springaigoodreads/Book.java), [Review class](https://github.com/JMHReif/springai-goodreads/blob/main/src/main/java/com/jmhreif/springaigoodreads/Review.java).

We also need a repository interface defined so that we can interact with the database. While we will need to define a custom query, we'll come back and add that in a bit later.

```java
public interface BookRepository extends Neo4jRepository<Book, String> {
}
```

Next, the core of this application where all the magic happens is the controller class. This class will contain the logic for taking a search phrase provided by the user and calling the `Neo4jVectorStore` to calculate and return the most similar ones.

We can then pass those similar reviews into a Neo4j query to retrieve connected entities, providing additional context in the prompt for the LLM. It will use all the information provided to respond with some similar book recommendations for the original searched phrase.

## Controller

Our controller class contains a couple of common annotations, to start. We'll also inject the `Neo4jVectorStore` and `BookRepository` beans that we defined earlier, as well as the `OpenAiChatClient` for our embedding client.

The next thing is to define a string for our prompt. This is the text that we will pass to the LLM to generate the response. We'll use the search phrase provided by the user and the similar reviews we find in the database to populate our prompt parameters in a few minutes. Next, we define the constructor for the controller class, which will inject the necessary beans.

```java
@RestController
@RequestMapping("/")
public class BookController {
    private final OpenAiChatClient client;
    private final Neo4jVectorStore vectorStore;
    private final BookRepository repo;

    String prompt = """
            You are a book expert with high-quality book information in the CONTEXT section.
            Answer with every book title provided in the CONTEXT.
            Do not add extra information from any outside sources.
            If you are unsure about a book, list the book and add that you are unsure.

            CONTEXT:
            {context}

            PHRASE:
            {searchPhrase}
            """;

    public BookController(OpenAiChatClient client, Neo4jVectorStore vectorStore, BookRepository repo) {
        this.client = client;
        this.vectorStore = vectorStore;
        this.repo = repo;
    }

    //Retrieval Augmented Generation with Neo4j - vector search + retrieval query for related context
    @GetMapping("/rag")
    public String generateResponseWithContext(@RequestParam String searchPhrase) {
        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(searchPhrase).withTopK(5).withSimilarityThreshold(0.8));

        //more code shortly!
    }
}
```

Finally, we define a method that will be called when a user makes a GET request to the `/rag` endpoint. This method will first take a search phrase as a query parameter and pass that to the vector store's `similaritySearch()` method to find similar reviews. I have also added a couple of customization filters to the query by limiting to the top five results (`.withTopK(5)`) and only pull the most similar results (`withSimilarityThreshold(0.8)`).

The actual implementation of Spring AI's `similaritySearch()` method is below.

```java
@Override
public List<Document> similaritySearch(SearchRequest request) {
	Assert.isTrue(request.getTopK() > 0, "The number of documents to returned must be greater than zero");
	Assert.isTrue(request.getSimilarityThreshold() >= 0 && request.getSimilarityThreshold() <= 1,
			"The similarity score is bounded between 0 and 1; least to most similar respectively.");

	var embedding = Values.value(toFloatArray(this.embeddingClient.embed(request.getQuery())));
	try (var session = this.driver.session(this.config.sessionConfig)) {
		StringBuilder condition = new StringBuilder("score >= $threshold");
		if (request.hasFilterExpression()) {
			condition.append(" AND ")
				.append(this.filterExpressionConverter.convertExpression(request.getFilterExpression()));
		}
		String query = """
				CALL db.index.vector.queryNodes($indexName, $numberOfNearestNeighbours, $embeddingValue)
				YIELD node, score
				WHERE %s
				RETURN node, score""".formatted(condition);

		return session
			.run(query,
					Map.of("indexName", this.config.indexName, "numberOfNearestNeighbours", request.getTopK(),
							"embeddingValue", embedding, "threshold", request.getSimilarityThreshold()))
			.list(Neo4jVectorStore::recordToDocument);
	}
}
```

Then, we map the similar `Review` nodes back to `Document` entities because Spring AI expects a general document type. The `Neo4jVectorStore` class contains methods to convert `Document` to a custom record, as well as the reverse for record to `Document` conversion. The actual implementation for those methods is shown next.

```java
private Map<String, Object> documentToRecord(Document document) {
	var embedding = this.embeddingClient.embed(document);
	document.setEmbedding(embedding);

	var row = new HashMap<String, Object>();

	row.put("id", document.getId());

	var properties = new HashMap<String, Object>();
	properties.put("text", document.getContent());

	document.getMetadata().forEach((k, v) -> properties.put("metadata." + k, Values.value(v)));
	row.put("properties", properties);

	row.put(this.config.embeddingProperty, Values.value(toFloatArray(embedding)));
	return row;
}

private static Document recordToDocument(org.neo4j.driver.Record neoRecord) {
	var node = neoRecord.get("node").asNode();
	var score = neoRecord.get("score").asFloat();
	var metaData = new HashMap<String, Object>();
	metaData.put("distance", 1 - score);
	node.keys().forEach(key -> {
		if (key.startsWith("metadata.")) {
			metaData.put(key.substring(key.indexOf(".") + 1), node.get(key).asObject());
		}
	});

	return new Document(node.get("id").asString(), node.get("text").asString(), Map.copyOf(metaData));
}
```

Back in our controller method for book recommendations, we now have similar reviews for the user's searched phrase. But reviews (and their accompanying text) aren't really helpful in giving us book recommendations. So now we need to run a query in Neo4j to retrieve the related books for those reviews. This is the retrieval augmented generation (RAG) piece of the application.

Let's write the query in the `BookRepository` interface to find the books associated with those reviews.

```java
public interface BookRepository extends Neo4jRepository<Book, String> {
    @Query("MATCH (b:Book)<-[rel:WRITTEN_FOR]-(r:Review) " +
            "WHERE r.id IN $reviewIds " +
            "AND r.text <> 'RTC' " +
            "RETURN b, collect(rel), collect(r);")
    List<Book> findBooks(List<String> reviewIds);
}
```

In the query, we pass in the ids of the reviews from the similarity search (`$reviewIds`) and pull the `Review → Book` pattern for those reviews. We also filter out any reviews that have the text 'RTC' (which is a placeholder for reviews that don't have text). We then return the `Book` nodes, the relationships, and the `Review` nodes.

Now we need to call that method in our controller and pass the results to a prompt template. We will pass that to the LLM to generate a response with a book recommendation list based on the user's search phrase (we hope!). 🙂

```java
//Retrieval Augmented Generation with Neo4j - vector search + retrieval query for related context
@GetMapping("/rag")
public String generateResponseWithContext(@RequestParam String searchPhrase) {
    List<Document> results = vectorStore.similaritySearch(SearchRequest.query(searchPhrase).withTopK(5).withSimilarityThreshold(0.8));

    List<Book> bookList = repo.findBooks(results.stream().map(Document::getId).collect(Collectors.toList()));

    var template = new PromptTemplate(prompt, Map.of("context", bookList.stream().map(b -> b.toString()).collect(Collectors.joining("\n")), "searchPhrase", searchPhrase));
    System.out.println("----- PROMPT -----");
    System.out.println(template.render());

    return client.call(template.create().getContents());

}
```

Starting right after the similarity search, we call our new `findBooks()` method and pass in the list of review ids from the similarity search. The retrieval query returns to a list of books called `bookList`.

Next, we create a prompt template with the prompt string, the context data from the graph, and the user's search phrase, mapping the `context` and `searchPhrase` prompt parameters to the graph data (list with each item on new line) and the user's search phrase, respectively.

I have also added a `System.out.println()` to print the prompt to the console so that we can see what is getting passed to the LLM.

Finally, we call the template's `create()` method to generate the response from the LLM. The returning JSON object has a `contents` key that contains the response string with the list of book recommendations based on the user's search phrase.

Let's test it out!

## Running the application

To run our Goodreads AI application, you can use the `./mvnw spring-boot:run` command in the terminal. Once the application is running, you can make a GET request to the `/rag` endpoint with a search phrase as a query parameter. Some examples are included next.

```bash
http ":8080/rag?searchPhrase=happy%20ending"
http ":8080/rag?searchPhrase=encouragement"
http ":8080/rag?searchPhrase=high%tech"
```

### Sample call and output + full prompt

Call and returned book recommendations:

```bash
jenniferreif@elf-lord springai-goodreads % http ":8080/rag?searchPhrase=encouragement"

The Cross and the Switchblade
The Art of Recklessness: Poetry as Assertive Force and Contradiction
I am unsure about 90 Minutes in Heaven: A True Story of Death and Life
The Greatest Gift: The Original Story That Inspired the Christmas Classic It's a Wonderful Life
I am unsure about Aligned: Volume 1 (Aligned, #1)
```

Application log output:

```bash
----- PROMPT -----
You are a book expert with high-quality book information in the CONTEXT section.
Answer with every book title provided in the CONTEXT.
Do not add extra information from any outside sources.
If you are unsure about a book, list the book and add that you are unsure.

CONTEXT:
Book[book_id=772852, title=The Cross and the Switchblade, isbn=0515090255, isbn13=9780515090253, reviewList=[Review[id=f70c68721a0654462bcc6cd68e3259bd, text=encouraging, rating=4]]]
Book[book_id=89375, title=90 Minutes in Heaven: A True Story of Death and Life, isbn=0800759494, isbn13=9780800759490, reviewList=[Review[id=85ef80e09c64ebd013aeebdb7292eda9, text=inspiring & hope filled, rating=5]]]
Book[book_id=1488663, title=The Greatest Gift: The Original Story That Inspired the Christmas Classic It's a Wonderful Life, isbn=0670862045, isbn13=9780670862047, reviewList=[Review[id=b74851666f2ec1841ca5876d977da872, text=Inspiring, rating=4]]]
Book[book_id=7517330, title=The Art of Recklessness: Poetry as Assertive Force and Contradiction, isbn=1555975623, isbn13=9781555975623, reviewList=[Review[id=2df3600d488e182a3ef06bff7fc82eb8, text=Great insight, great encouragement, and great company., rating=4]]]
Book[book_id=27802572, title=Aligned: Volume 1 (Aligned, #1), isbn=1519114796, isbn13=9781519114792, reviewList=[Review[id=60b9aa083733e751ddd471fa1a77535b, text=healing, rating=3]]]

PHRASE:
encouragement
```

We can see that the LLM generated a response with a list of book recommendations based on the books found in the database (CONTEXT section of prompt). The results of the similarity search + graph retrieval query for the user's search phrase are in the prompt, and the LLM's answer uses that data for a reponse.

## Wrapping Up!

In today's post, you learned how to build a GenAI application with Spring AI in Java.

We used the OpenAI model to generate book recommendations based on a user's search phrase.

We used the Neo4j Vector Database to store and query vectors for similarity searches.

We also mapped the domain model to our database model, wrote a repository interface to interact with the database, and created a controller class to handle user requests and generate responses.

I hope this post helps to get you started with Spring AI and beyond. Happy coding!

## Resources

* Code (Github repository): [Spring AI Goodreads](https://github.com/JMHReif/springai-goodreads)
* Documentation: [Spring AI](https://docs.spring.io/spring-ai/reference/index.html)
* Webpage: [Spring AI project](https://spring.io/projects/spring-ai)
* API: [Spring AI - Neo4jVectorStore](https://docs.spring.io/spring-ai/docs/0.8.1/api/org/springframework/ai/vectorstore/Neo4jVectorStore.html#similaritySearch(org.springframework.ai.vectorstore.SearchRequest))
