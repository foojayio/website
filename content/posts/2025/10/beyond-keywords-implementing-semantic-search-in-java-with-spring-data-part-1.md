---
title: "Beyond Keywords: Implementing Semantic Search in Java With Spring Data (Part 1)"
slug: "beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1"
date: "2025-10-16T13:39:59+00:00"
lastmod: "2026-01-05T19:33:09+00:00"
description: "Have you ever tried to search for something such as a product, a song, or a movie but couldn’t quite remember its exact name? Maybe you recall only a clue—a desert pyramid, a short melody, or “that ship that hit an iceberg.” Keyword search struggles with that. Vector search doesn’t: It lets you search by meaning.It works by turning text into embeddings, vectors (arrays of numbers) that capture semantic similarity, so results are ranked by what they mean, not just what they say.With recent vector query support in Spring Data, Java developers can build semantic search using familiar repositories and queries."
authors:
  - "ricardo-mello"
image: "/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/458-4589658_spring-framework-logo-spring-boot-png-transparent-png.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
  - "Spring"
  - "Tools"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "introduction-to-data-driven-testing-with-java-and-mongodb"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "mongodb-schemas-in-java"
enlighterjs: true
frozen: false
---

Building a semantic movie search app with embeddings and vector queries

Have you ever tried to search for something such as a product, a song, or a movie but couldn't quite remember its exact name? Maybe you recall only a clue---a desert pyramid, a short melody, or "that ship that hit an iceberg." Keyword search struggles with that. Vector search doesn't: It lets you search by meaning.

It works by turning text into embeddings, vectors (arrays of numbers) that capture semantic similarity, so results are ranked by what they mean, not just what they say.

With recent vector query support in Spring Data, Java developers can build semantic search using familiar repositories and queries.

In this article, we'll build a small **movie search** app that understands intent beyond keywords. You'll type queries like "movie with pyramids in Egypt" or "a science fiction movie about rebels fighting an empire in space" and the app will surface relevant titles.
![](/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/Screenshot-2025-10-09-at-12.11.48-PM.png)

Along the way, we'll explore how to generate embeddings, perform vector searches, and retrieve the most relevant results.

The magic behind vector search {#h2-0-the-magic-behind-vector-search}
---------------------------------------------------------------------

When searching for a movie in the past, the most common approach was keyword-based. You'd type something like**title =** ***"Star Wars"***, and the system would return the exact match.

But if your query was anything different, maybe a misspelling, a synonym, or simply because you couldn't remember the title, it became much harder to get the right result. And it got even worse if all you had in mind was a scene or a general idea of the story.

For example, if you searched for "a science fiction movie about rebels fighting an empire in space,"a keyword engine would struggle. To make that work, you'd have to set up synonym lists, custom rules, and a lot of manual mappings to connect this description back to *Star Wars*, a process that would take a lot of effort and resources to maintain.

Vector search takes a very different approach. Instead of looking for literal keywords, it looks for **similarity in meaning**. The process works like this:

1. **Generate embeddings:**Unstructured data such as text, audio, or images is sent to a machine learning model, which converts it into an embedding (a numerical vector), and is stored in the database.
2. **Convert the user query**: When a user types a search, the query is also transformed into an embedding by the same model.
3. **Compare vectors**: The query embedding is compared against the embeddings stored in the database, and the closest matches are returned.

Together, these three steps form the foundation of vector search.

**Note** : It is recommended to use the same model for both creating and querying embeddings. For example, if the dataset was embedded with [Voyage AI](https://www.voyageai.com/), the queries should also be embedded with Voyage AI to ensure the most accurate and meaningful results.

Prerequisites {#h2-1-prerequisites}
-----------------------------------

Before we start building the application, make sure you have the following in place:

* A [MongoDB Atlas account](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-data-monogbd-hybrid-search-foojay&utm_term=tony.kim)
  * [Create a free M0 cluster to get started](https://www.mongodb.com/docs/atlas/tutorial/create-new-cluster/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-data-monogbd-hybrid-search-foojay&utm_term=tony.kim)
* Java 21+ installed and an IDE of your choice
* A [Voyage AI API](https://dashboard.voyageai.com/organization/api-keys) token
* The [sample dataset](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-data-monogbd-hybrid-search-foojay&utm_term=tony.kim) uploaded to your cluster
  * The embedded_movies collection, which we'll query throughout the examples

Tag your Atlas Cluster {#h2-2-tag-your-atlas-cluster}
-----------------------------------------------------

If you're deploying this application on MongoDB Atlas, you can use [Resource Tags](https://www.mongodb.com/docs/atlas/tags/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hybrid-search&utm_term=ricardo.mello) to label your clusters or projects for tracking and cost visibility. For instance, I recommend tagging your cluster with values that describe this tutorial:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Key: application
Value: hybrid-search</pre>

Adding tags is a simple but powerful way to organize your MongoDB Atlas resources, especially if you manage multiple clusters, environments, or demos. Tags make it easier to:

* Track which clusters belong to a specific application.

<!-- -->

* Filter and group resources in the Atlas UI.

<!-- -->

* Gain better visibility in billing and monitoring reports.

To add a tag:

1. Open your [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hybrid-search&utm_term=ricardo.mello) dashboard.

2. Go to **Database → Cluster → Add Tag.**

3. Click **Add Tag** and use the key/value above.

4. Save your changes.

This step won't affect your code, but it's a best practice to keep your Atlas environment organized. If you're running MongoDB locally, you can safely skip this step.

Embeddings with Voyage AI {#h2-3-embeddings-with-voyage-ai}
-----------------------------------------------------------

[Voyage AI](https://www.voyageai.com/) is an embedding platform offering high-quality, production-ready models behind a simple API. In this project, we use Voyage AI to generate embeddings in **two places**:

1.**Generate embeddings:** Each movie's plot was sent to [voyage-3-large](https://docs.voyageai.com/docs/embeddings), and the returned vector was stored in the embedded_movies collection as plot_embedding_voyage_3_large.(This preprocessing is already done.)
![](/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/Screenshot-2025-10-09-at-12.25.25-PM.png)

2.**Generate embeddings (user query)**: When the user searches, we encode the query with the same voyage-3-large model and compare that query vector to the stored document vectors. We then return the most similar movies.
![](/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/Screenshot-2025-10-09-at-12.14.03-PM.png)

The similarity comparison is executed by [MongoDB Atlas Vector Search](https://www.mongodb.com/products/platform/atlas-vector-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-data-monogbd-hybrid-search-foojay&utm_term=tony.kim) against the stored vectors.

If you'd like to explore more details about the model we use here, you can check the official[Voyage AI blog post](https://blog.voyageai.com/2025/01/07/voyage-3-large/?utm_source=chatgpt.com).

Preparing the dataset {#h2-4-preparing-the-dataset}
---------------------------------------------------

Before creating the index, make sure the **embedded_movies** collection has been imported into your MongoDB Atlas cluster. In our case, this dataset already comes with a field called **plot_embedding_voyage_3_large**, which stores the pre-computed embeddings for each movie plot.
![](/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/Screenshot-2025-10-09-at-12.27.14-PM.png)

With the dataset in place, the next step is to create a vector index so MongoDB Atlas knows which field to use, its dimensionality, and the similarity metric.

MongoDB Atlas Vector Search (index and retrieval) {#h2-5-mongodb-atlas-vector-search-index-and-retrieval}
---------------------------------------------------------------------------------------------------------

To compare embeddings at query time, MongoDB Atlas needs a search index that tells it which field stores your vectors, their dimensionality, and which similarity metric to use. Once the collection is in place, [create the following index](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-data-monogbd-hybrid-search-foojay&utm_term=tony.kim):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.embedded_movies.createSearchIndex(
&nbsp;&nbsp;"vector_index",
&nbsp;&nbsp;"vectorSearch",
&nbsp;&nbsp;{ "fields": [{
&nbsp;&nbsp;&nbsp;&nbsp;"type": "vector",
&nbsp;&nbsp;&nbsp;&nbsp;"path": "plot_embedding_voyage_3_large",
&nbsp;&nbsp;&nbsp;&nbsp;"numDimensions": 2048,
&nbsp;&nbsp;&nbsp;&nbsp;"similarity": "dotProduct"
&nbsp;&nbsp;}]}
)</pre>

Let's break it down:

* **"vector_index"**: the index name
* **path**: the field that stores document embeddings
* **numDimensions**: must match the model's embedding size
* **similarity**: metric used for nearest-neighbor ranking (e.g., dotProduct, cosine, euclidean)

Building the movie search app {#h2-6-building-the-movie-search-app}
-------------------------------------------------------------------

Now that we've seen what vector search is, how embeddings are generated, and created the vector index in MongoDB Atlas, let's put everything into practice. To get started, open [Spring Initializr](https://start.spring.io/), create a new project, and select Spring Web and Spring Data MongoDB as dependencies. Download the project and open it in your favorite IDE.
![](/images/posts/2025/10/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/Screenshot-2025-10-09-at-12.16.10-PM.png)

### Configuring the application {#h3-7-configuring-the-application}

After opening the project, the first thing is to configure our MongoDB connection and a few settings for the embedding provider and vector search. Open or create your application.yml file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring:
&nbsp;data:
&nbsp;&nbsp;&nbsp;mongodb:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;uri: ${MONGODB_URI}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;database: sample_mflix
voyage:
&nbsp;api-key: ${VOYAGE_API_KEY}
&nbsp;base-url: https://api.voyageai.com/v1
&nbsp;model: voyage-3-large
&nbsp;output-dimension: 2048
&nbsp;vector-index-name: vector_index
&nbsp;vector-collection-name: embedded_movies
&nbsp;vector-field: plot_embedding_voyage_3_large
&nbsp;top-k: 8
&nbsp;num-candidates: 160</pre>

What this does (briefly):

* **MongoDB**: connects Spring Data to your Atlas cluster and the embedded_movies collection
* **Voyage:** sets up the API key, model, and embedding size
* **Vector Search:** tells MongoDB Atlas which index and field to use, plus how many results (top-k) to return

Next, we need to connect our application.yml settings to the code. To do that, we create a @ConfigurationProperties record (VoyageConfigProperties) that maps all voyage.\* values into a strongly-typed object we can use later in the application.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "voyage")
public record VoyageConfigProperties(
&nbsp;&nbsp;&nbsp;&nbsp;String model,
&nbsp;&nbsp;&nbsp;&nbsp;int outputDimension,
&nbsp;&nbsp;&nbsp;&nbsp;String vectorIndexName,
&nbsp;&nbsp;&nbsp;&nbsp;String vectorCollectionName,
&nbsp;&nbsp;&nbsp;&nbsp;String vectorField,
&nbsp;&nbsp;&nbsp;&nbsp;int topK,
&nbsp;&nbsp;&nbsp;&nbsp;int numCandidates,
&nbsp;&nbsp;&nbsp;&nbsp;String baseUrl,
&nbsp;&nbsp;&nbsp;&nbsp;String apiKey){}</pre>

### The document model {#h3-8-the-document-model}

Our embedded_movies collection contains several fields that describe each movie, such as title, year, plot, and cast. To work with this data in our application, we'll define a simple record that maps to the collection but only includes the fields we want to return to the client. Create a record named Movie and annotate it with *Document("embedded_movies")*:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("embedded_movies")
public record Movie(
&nbsp;&nbsp;&nbsp;String title,
&nbsp;&nbsp;&nbsp;String year,
&nbsp;&nbsp;&nbsp;String fullplot,
&nbsp;&nbsp;&nbsp;String plot,
&nbsp;&nbsp;&nbsp;String poster,
&nbsp;&nbsp;&nbsp;Imdb imdb,
&nbsp;&nbsp;&nbsp;List&lt;String&gt; genres,
&nbsp;&nbsp;&nbsp;List&lt;String&gt; cast)
{
&nbsp;&nbsp;&nbsp;record Imdb(Double rating) {}
}</pre>

### Wire the request DTO {#h3-9-wire-the-request-dto}

Next, let's create a request record with a single query field to hold the user's search text, for now. We'll revisit this class later to add extra fields for filtering:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record MovieSearchRequest(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String query
) {}</pre>

### Communicating with Voyage AI {#h3-10-communicating-with-voyage-ai}

In this step, we'll set up the classes needed to talk to the Voyage AI API. The idea is simple: We send a request with some text, and Voyage AI returns the corresponding list of embeddings.

To model this exchange, we'll use two records:

**EmbeddingsRequest**: This represents the payload we send to Voyage AI. It includes the input text, the model name, and a few optional parameters like input_type and output_dimension.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.List;

public record EmbeddingsRequest(
&nbsp;&nbsp;&nbsp;List&lt;String&gt; input,
&nbsp;&nbsp;&nbsp;String model,
&nbsp;&nbsp;&nbsp;String input_type,
&nbsp;&nbsp;&nbsp;Integer output_dimension
) {}</pre>

**EmbeddingsResponse**: This represents the response from Voyage AI.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.List;

public record EmbeddingsResponse(List&lt;Item&gt; data) {
&nbsp;public record Item(List&lt;Double&gt; embedding) {}
}</pre>

#### The VoyageEmbeddingsClient

To call the Voyage AI API, we'll define a small HTTP client using Spring's declarative HTTP interfaces:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;url = "/embeddings",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contentType = MediaType.APPLICATION_JSON_VALUE,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;accept = MediaType.APPLICATION_JSON_VALUE
)

public interface VoyageEmbeddingsClient {
&nbsp;@PostExchange
&nbsp;EmbeddingsResponse embed(@RequestBody EmbeddingsRequest body);
}</pre>

**In short**: This interface acts as a strongly-typed wrapper around Voyage AI's /embeddings endpoint, letting us call the API as if it were a regular Java method.

#### The VoyageClientConfig

To actually use our VoyageEmbeddingsClient, we need to configure how Spring will build it. That's what the following class does:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class VoyageClientConfig {
&nbsp;&nbsp;&nbsp;@Bean
&nbsp;&nbsp;public VoyageEmbeddingsClient voyageEmbeddingsClient(VoyageConfigProperties props) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RestClient client = RestClient.builder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.baseUrl(props.baseUrl())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.defaultHeader("Authorization", "Bearer " + props.apiKey())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.defaultHeader("Content-Type", "application/json")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return factory.createClient(VoyageEmbeddingsClient.class);
&nbsp;&nbsp;}
}</pre>

**In short**: This config builds the HTTP client, injects the API key into every request, and exposes a ready-to-use VoyageEmbeddingsClient bean.

### The EmbeddingService {#h3-11-the-embeddingservice}

Next, let's add an EmbeddingService that wraps our client and handles generating embeddings for a given query text.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EmbeddingService {
&nbsp;&nbsp;private final Logger logger = Logger.getLogger(EmbeddingService.class.getName());
&nbsp;&nbsp;private final VoyageEmbeddingsClient client;
&nbsp;&nbsp;private final VoyageConfigProperties config;
&nbsp;&nbsp;public EmbeddingService(VoyageEmbeddingsClient client, VoyageConfigProperties config) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.client = client;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.config = config;
&nbsp;&nbsp;}
&nbsp;&nbsp;public List&lt;Double&gt; embedQuery(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String text) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;logger.info("Generating embeddings .. ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;var res = client.embed(new EmbeddingsRequest(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List.of(text), config.model(), "query", config.outputDimension()));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;logger.info("Embeddings generated successfully!");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return res.data().getFirst().embedding();
&nbsp;&nbsp;}
}</pre>

This service calls the Voyage AI API with the user's text, generates the embedding using the configured model, and returns the vector as a list of numbers.

Querying with Spring Data Vector Search operation {#h2-12-querying-with-spring-data-vector-search-operation}
------------------------------------------------------------------------------------------------------------

There are multiple ways to run a vector search. You could even work directly with raw document queries. But in this tutorial, we'll focus on the brand-new [Spring Data MongoDB support for semantic search](https://github.com/spring-projects/spring-data-mongodb/releases?page=2), introduced in Spring Data MongoDB 4.5.

The VectorSearchOperation class is at the core of this feature, and it's what we'll use to express our queries in a clean, type-safe way.

To run the search, let's create a MovieService that generates embeddings for the user's query and executes the vector search against the embedded_movies collection:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@EnableConfigurationProperties(VoyageConfigProperties.class)
public class MovieService {

&nbsp;&nbsp;&nbsp;private final MongoTemplate mongoTemplate;

&nbsp;&nbsp;&nbsp;private final VoyageConfigProperties config;
&nbsp;&nbsp;&nbsp;private final EmbeddingService embeddingService;

&nbsp;&nbsp;&nbsp;MovieService(MongoTemplate mongoTemplate, VoyageConfigProperties config, EmbeddingService embeddingService) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.mongoTemplate = mongoTemplate;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.config = config;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.embeddingService = embeddingService;
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;VectorSearchOperation vectorSearchOperation = VectorSearchOperation.search(config.vectorIndexName())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.path(config.vectorField())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.vector(embeddingService.embedQuery(req.query()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.limit(config.topK())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.numCandidates(config.numCandidates());

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aggregation.newAggregation(vectorSearchOperation),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).getMappedResults();
&nbsp;&nbsp;&nbsp;}
}</pre>

The *searchMovies* method takes the user's text, generates an embedding with EmbeddingService, and uses Spring Data's new [VectorSearchOperation](https://docs.spring.io/spring-data/mongodb/reference/5.0/mongodb/repositories/vector-search.html) to query MongoDB Atlas Vector Search, returning the most relevant movies directly as mapped Movie objects.

### The MovieController {#h3-13-the-moviecontroller}

With everything in place, the last step is to expose our API through a simple controller. This class wires the MovieService and makes the /movies/search endpoint available:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

&nbsp;&nbsp;&nbsp;private final MovieService movieService;

&nbsp;&nbsp;&nbsp;public MovieController(MovieService movieService) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.movieService = movieService;
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@PostMapping("/search")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;Movie&gt;&gt; searchMovies(@RequestBody MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.ok(movieService.searchMovies(req));
&nbsp;&nbsp;&nbsp;}
}</pre>

**In short**: The controller takes in a search request, delegates to MovieService, and returns a list of matching movies.

By the end, you'll have a project structure similar to this.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring-data-mongodb-hybrid-search/
├── .idea/
├── .mvn/
└── src/
&nbsp;&nbsp;&nbsp;└── main/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── java/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; └── com/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; └── mongodb/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── EmbeddingService.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── EmbeddingsRequest.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── EmbeddingsResponse.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── Movie.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── MovieController.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── MovieSearchRequest.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── MovieService.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── SpringDataMongodbHybridSearchApplication.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── VoyageClientConfig.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├── VoyageConfigProperties.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; └── VoyageEmbeddingsClient.java
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── resources/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── static/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── templates/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── application.yml</pre>

**Note**: There's no strict separation into layers or packages here. It's up to you, the reader, to organize the code however you prefer.

Running the application {#h2-14-running-the-application}
--------------------------------------------------------

Set the required environment variables:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">export MONGODB_URI=&lt;YOUR CONNECTION&gt;

export VOYAGE_API_KEY=&lt;YOUR API KEY&gt;</pre>

Then, start the application:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn spring-boot:run</pre>

With the app running, let's perform a POST request to our new endpoint:

### Example request {#h3-15-example-request}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">### Searching movies
POST http://localhost:8080/movies/search
Content-Type: application/json

{
&nbsp;"query": "a science fiction movie about rebels fighting an empire in space"
}</pre>

You should see results coming back from the embedded_movies collection, movies semantically close to the description, even though the exact title wasn't mentioned.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"title": "Star Wars: Episode IV - A New Hope",
&nbsp;"year": "1977",
&nbsp;"fullplot": "A young boy from Tatooine..",
&nbsp;"plot": "Luke Skywalker joins ..",
&nbsp;"imdb": {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rating": 8.7
&nbsp;&nbsp;&nbsp;},
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Action",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Adventure",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Fantasy"
&nbsp;&nbsp;],
&nbsp;...
}</pre>

Looking ahead {#h2-16-looking-ahead}
------------------------------------

In this first part, we explored what vector search is, its core principles, and how it enables semantic search beyond simple keywords. We saw how to generate embeddings with Voyage AI, create a vector index in MongoDB Atlas, and use the brand-new Spring Data MongoDB support for vector queries to build a working movie search application.

If you'd like to check out the full project code, you can find it on [GitHub](https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search).In *Part 2: Beyond Keywords: Optimizing Vector Search with Filters and Caching*, we'll enhance this application by adding filters to our vector search, exploring how they work under the hood, and refining the overall search experience.
