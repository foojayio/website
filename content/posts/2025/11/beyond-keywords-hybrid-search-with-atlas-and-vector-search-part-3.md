---
title: "Beyond Keywords: Hybrid Search With Atlas and Vector Search (Part 3)"
slug: "beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3"
date: "2025-11-13T13:56:23+00:00"
lastmod: "2025-12-12T21:19:43+00:00"
description: "Bringing together semantic vectors and exact keyword matching with $rankFusionIf you’ve been following along this series, you already know we started by giving our movie search app the ability to understand meaning—not just keywords—using semantic search, as discussed in Part 1: Implementing Semantic Search in Java With Spring Data. Then, we made it even smarter by adding filters and optimizing performance with embedding strategies in Part 2: Optimizing Vector Search With Filters and Caching.Now, in this final installment, we’re taking our search capability to its ultimate form: combining the precision of full-text search with the semantic understanding of vector search. Welcome to hybrid search."
authors:
  - "ricardo-mello"
image: "https://foojay.io/wp-content/uploads/2025/11/Screenshot-2025-11-11-at-1.58.35-PM.png"
categories:
  - "AI"
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Bringing together semantic vectors and exact keyword matching with $rankFusion

If you've been following along this series, you already know we started by giving our movie search app the ability to understand meaning---not just keywords---using semantic search, as discussed in [*Part 1: Implementing Semantic Search in Java With Spring Data*](https://foojay.io/today/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/). Then, we made it even smarter by adding filters and optimizing performance with embedding strategies in [*Part 2: Optimizing Vector Search With Filters and Caching*](https://foojay.io/today/beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2/).

Now, in this final installment, we're taking our search capability to its ultimate form: combining the precision of full-text search with the semantic understanding of vector search.

Welcome to hybrid search.

One search might not be enough {#h2-0-one-search-might-not-be-enough}
---------------------------------------------------------------------

Think about how people actually search for movies. Sometimes, they only remember fragments---such as, "a ship that sinks at night after hitting an iceberg"---and hope the app can figure it out. Other times, they know exactly what they want---like "Titanic"---and expect to see it right away.

These two very different situations expose a critical gap: **No single search technique works perfectly for every type of query**.

This is because full-text search and vector search work on fundamentally different principles:

1. Full-text search works by matching exact keywords or their variants within specific fields, like *title* or *description*.
2. Vector search compares the overall meaning of the query to the meaning of documents using semantic embeddings.

Let's see how this plays out in our examples:

**Case 1**: When the user types, "a ship that sinks at night after hitting an iceberg":

* Vector search shines here and correctly surfaces *Titanic.*
* Full-text search will likely return null or irrelevant results because it relies on matching specific keywords.

**Case 2**: When someone searches for "Titanic":

* A vector search, focused on semantic similarity, might return *Poseidon* (another sinking-ship movie).
* A full-text search, however, nails it instantly because it finds the exact title.

Clearly, both methods have their strengths. Full-text is unbeatable for exact matches and well-known titles, while vector search excels when the query is descriptive or fuzzy. The challenge is that if we rely on only one, we risk leaving users frustrated.

Merging the best of both worlds {#h2-1-merging-the-best-of-both-worlds}
-----------------------------------------------------------------------

That's where **hybrid search** comes in. By combining the precision of full-text search with the intelligence of semantic search, we can deliver results that understand both what the user wrote and what they meant. MongoDB Atlas makes this possible with the new $rankFusion operator, which merges and re-ranks results from multiple pipelines.

For more details, take a look at the [Hybrid Search Explained](https://www.mongodb.com/resources/products/capabilities/hybrid-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim).

Prerequisites {#h2-2-prerequisites}
-----------------------------------

If you've been following from Part 1, you should already have everything set up: a MongoDB Atlas cluster, Java 17+, a Voyage AI API token, and the embedded_movies collection.

For this final part, there's one more requirement:

* **MongoDB Atlas 8.1 or higher**, since hybrid search relies on the $rankFusion operator introduced in this version.

The vector search {#h2-3-the-vector-search}
-------------------------------------------

So far, our application uses **vector search with pre-filters**. That means we can run semantic queries while narrowing the search space by year, genres, and IMDb rating. Under the hood, the query looks something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;{
&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filter: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$and: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ genres: { $in: ["Action", "Drama"] } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ year: { $gte: 1980, $lte: 2003 } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ "imdb.rating": { $gte: 9.0 } }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "vector_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 8,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 160,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding_voyage_3_large",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-0.027284348, ....
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;}
&nbsp;}
]</pre>

This works well for descriptive searches, because the embeddings capture meaning beyond exact words.

The full-text search {#h2-4-the-full-text-search}
-------------------------------------------------

But there's a catch. In the *Titanic* example, vector search is perfect when the user types a descriptive query like, "a ship that sinks at night after hitting an iceberg", since it understands intent. However, if the user knows the exact title and types simply "Titanic", vector search may return other sinking-ship movies like *Poseidon*.

On top of that, vector search requires generating embeddings for every query. In this case, that means calling an external API just to embed the word "Titanic", an unnecessary round trip when we could just match the text directly.

That's where [**full-text search**](https://www.mongodb.com/resources/basics/full-text-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim) comes in. Unlike vector search, it looks for exact keyword matches in fields such as title. If the title is in the database, full-text search will find it right away, faster and without embedding overhead.

### Implementing the full-text index {#h3-5-implementing-the-full-text-index}

Run this command in your MongoDB shell to create a dynamic search index on the embedded_movies collection:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.embedded_movies.createSearchIndex(
&nbsp;&nbsp;"fulltextsearch",
&nbsp;&nbsp;{ mappings: { dynamic: true } }
)</pre>

Note on indexing: The **dynamic: true** parameter is ideal for prototyping as it automatically indexes every field in your documents. For production, consider a custom mapping to optimize performance and cost by indexing only necessary fields. Review the [documentation on mapping](https://www.mongodb.com/docs/atlas/atlas-search/define-field-mappings/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim) for guidance.

### Executing a basic text query {#h3-6-executing-a-basic-text-query}

With the index created, we can now execute a simple query to find "Titanic" by its title:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.embedded_movies.aggregate([
&nbsp;{
&nbsp;&nbsp;&nbsp;$search: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index:&nbsp; "fulltextsearch",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: "Titanic",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "title"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}
&nbsp;},
])</pre>

You should see something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"title": "Titanic",
&nbsp;"year": "1996",
&nbsp;"plot": &nbsp;"The story of the 1912 sinking ..",
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Action",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Drama",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"History"
&nbsp;&nbsp;],
&nbsp;...
}</pre>

### Improving the experience with fuzzy search {#h3-7-improving-the-experience-with-fuzzy-search}

A common user experience problem is typos. What if our user wants to find *Titanic* but types **titani** (missing the final "c")? Try running the exact-match query yourself and you'll see it will return no results.

This is where the [fuzzy](https://www.mongodb.com/docs/atlas/atlas-search/text/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim) option comes to the rescue. Let's modify our query:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.embedded_movies.aggregate([&nbsp;
&nbsp;{
&nbsp;&nbsp;&nbsp;$search: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "fulltextsearch",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: "titani",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "title",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;maxEdits: 1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}
&nbsp;}
])</pre>

In short: With **maxEdits: 1**, our search for "titani" becomes more flexible. It will now match not only the intended "Titanic" (adding one character, "c") but also other titles like "Titans" (replacing "i" with "s") or "Titan" (removing one character, "i"). Possible results would be:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">title="Titanic"

title="Titan A.E."

title="Raise the Titanic"

title="Clash of the Titans"</pre>

### Refining results with score boosting {#h3-8-refining-results-with-score-boosting}

Not all fields are equally important when searching for movies. If a user types *"Titanic"* , a match in the **title** field should clearly outweigh a match in the **plot** or **fullplot**. Without boosting, MongoDB Atlas Search would treat all matches the same, which could push less relevant results higher in the ranking.

This is where **score boosting** becomes essential. Boosting lets us tell the search engine which fields matter more by increasing their influence on the final relevance score.

In our case:

* **title** gets the highest boost: direct matches on titles are prioritized.  
* **plot** receives a medium boost: useful when titles don't match but descriptions do.  
* **fullplot** has a lower boost: still relevant, but less critical than the main plot or title.  

We can apply this logic using a **compound operator**, which searches across multiple fields while applying different boost values:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.embedded_movies.aggregate(
[
&nbsp;{
&nbsp;&nbsp;&nbsp;$search: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "fulltextsearch",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;should: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: "titanic",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "title",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;maxEdits: 1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 4.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: "titanic",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;maxEdits: 1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 3.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: "titanic",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "fullplot",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;maxEdits: 1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 2.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}
&nbsp;}
]
)</pre>

With this setup, the search engine understands our priorities: A movie with a matching title like **Titanic** will always rank higher than another movie where the query only appears in a long description.

**Note:** You can also project the computed relevance score in your results by adding to your $project stage.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{ "score": { "$meta": "searchScore" } }</pre>

This will include the boosted score.

Combining forces with hybrid search {#h2-9-combining-forces-with-hybrid-search}
-------------------------------------------------------------------------------

We now have both components in place:

* Vector search for semantic understanding  
* Full-text search for exact with fuzzy and boost

The question is: Why choose one when we can use both? That's exactly what MongoDB's **$rankFusion** operator enables.

### The $rankFusion {#h3-10-the-rankfusion}

[$rankFusion](https://www.mongodb.com/ja-jp/docs/rapid/reference/operator/aggregation/rankFusion/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim) lets us run multiple search pipelines in the same aggregation, then combine their results into a single ranked output. In our case, we'll use two pipelines:

* A searchPipeline: the full-text search  
* A vectorPipeline: the semantic search

Here's the basic structure of a hybrid query using $rankFusion:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;{
&nbsp;&nbsp;&nbsp;$rankFusion: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pipelines: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;searchPipeline: [],
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vectorPipeline: []
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;combination: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;weights: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;searchPipeline: 0.5,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vectorPipeline: 0.5
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;scoreDetails: false
&nbsp;&nbsp;&nbsp;}
&nbsp;}
]</pre>

Let's break it down:

1. The **pipelines** section defines the individual search strategies you want to combine (full-text and vector, in our case).  
2. The **weights** section then decides how much influence each pipeline has on the final ranking---a higher number means greater importance, so 0.8 will outweigh 0.5.

### How to decide the right weights {#h3-11-how-to-decide-the-right-weights}

Once you set up the aggregate, the big question is: *How much weight should each pipeline get?*

There's no universal rule for picking these values---it depends entirely on your application and how users interact with it.

In some cases, giving more weight to full-text search makes sense (when exact titles matter most). In others, boosting the vector pipeline produces better results (when queries are more descriptive).

The key is to**experiment with your own data and queries**, adjusting the weights until you find the balance that delivers the best user experience.

Refactoring the application {#h2-12-refactoring-the-application}
----------------------------------------------------------------

### The full-text search pipeline {#h3-13-the-full-text-search-pipeline}

Let's go back to our application to refactor the MovieService class and apply the new $rankFusion, combining full-text search with vector search. Create the following method:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private BsonDocument buildFullTextSearchPipeline(String query) {
&nbsp;&nbsp;&nbsp;return Aggregates.search(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound().should(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List.of(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text(SearchPath.fieldPath("title"), query)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.fuzzy(fuzzySearchOptions().maxEdits(1))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.score(boost(4.0F)),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text(SearchPath.fieldPath("plot"), query)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.fuzzy(fuzzySearchOptions().maxEdits(1))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.score(boost(3.0F)),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text(SearchPath.fieldPath("fullplot"), query)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.fuzzy(fuzzySearchOptions().maxEdits(1))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.score(boost(2.0F))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SearchOptions.searchOptions().index("fulltextsearch")
&nbsp;&nbsp;&nbsp;).toBsonDocument();
}</pre>

This method does exactly what we saw previously: It builds the full-text search pipeline. Notice how we're using **compound** , **should,** **fuzzy** , **text** , and **boost**, just like before.

The vector search pipeline {#h2-14-the-vector-search-pipeline}
--------------------------------------------------------------

Now, let's create the method for the vector search pipeline inside MovieService:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private Bson buildVectorSearchPipeline(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;return VectorSearchOperation.search(config.vectorIndexName())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.path(config.vectorField())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.vector(embeddingService.embedQuery(req.query()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.limit(config.topK())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(req.toCriteria())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.numCandidates(config.numCandidates())&nbsp; &nbsp; &nbsp; &nbsp; 
         .withSearchScore("score")
         .toDocument(Aggregation.DEFAULT_CONTEXT);
}</pre>

What we did here was simply move the vector search code out of the searchMovies method and place it into its own dedicated method, making the code cleaner and easier to reuse.

### The RankFusion in searchMovies {#h3-15-the-rankfusion-in-searchmovies}

The last step is to put everything together inside the searchMovies method using **$rankFusion**.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {
&nbsp;&nbsp;AggregationOperation rankFusion = context -&gt; new Document("$rankFusion",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("input",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("pipelines",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", List.of(buildFullTextSearchPipeline(req.query()), new Document("$limit", config.topK())))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", List.of(buildVectorSearchPipeline(req)))))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("combination",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("weights",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", 0.5)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", 0.5)))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("scoreDetails", false));
&nbsp;&nbsp;
Aggregation aggregation = Aggregation.newAggregation(rankFusion);

&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;aggregation,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;).getMappedResults();
}</pre>

<br />

Here, we combine the two pipelines we created before:

1. The **full-text search** pipeline
2. The **vector search** pipeline  

And we tell MongoDB to merge their results with equal weights (0.5 each). This way, the final ranking takes into account both text relevance and vector similarity.

**Note** : To use Document class, make sure to import it from *org.bson.Document*;.

### Inspecting the generated pipeline {#h3-16-inspecting-the-generated-pipeline}

Now, let's run the application again and check the pipeline that is being generated. First, update your application.yml to enable debug logging for MongoDB:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">logging:
&nbsp;level:
&nbsp;&nbsp;&nbsp;org.springframework.data:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mongodb: DEBUG</pre>

With logging enabled, the application will print out the exact aggregation pipeline being sent to MongoDB. Next, run the following request:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">### POST
POST http://localhost:8080/movies/search
Content-Type: application/json

{
&nbsp;"query": "a ship that sinks at night after hitting an iceberg",
&nbsp;"minIMDbRating": 5,
&nbsp;"yearFrom": 1980,
&nbsp;"yearTo": 2003,
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;"Drama", "Action"
&nbsp;],
&nbsp;"excludeGenres": false
}</pre>

You'll see both the **full-text search** pipeline (with fuzzy, should, and boost as we defined earlier) and the **vector search** pipeline (with filters on genres, year, and IMDb rating).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;{
&nbsp;&nbsp;&nbsp;$rankFusion: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pipelines: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;searchPipeline: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$search: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;should: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a ship that sinks at night after hitting an iceberg",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "title",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: { maxEdits: 1 },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 4.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a ship that sinks at night after hitting an iceberg",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: { maxEdits: 1 },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 3.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a ship that sinks at night after hitting an iceberg",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "fullplot",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fuzzy: { maxEdits: 1 },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boost: { value: 2.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "fulltextsearch"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $limit: 8 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vectorPipeline: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filter: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$and: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;genres: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$in: ["Action", "Drama"]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$gte: 1980,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$lte: 2003
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"imdb.rating": { $gte: 5.0 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "vector_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 8,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 160,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding_voyage_3_large",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.03693888, 0.026406106
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;combination: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;weights: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;searchPipeline: 0.5,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vectorPipeline: 0.5
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;scoreDetails: false
&nbsp;&nbsp;&nbsp;}
&nbsp;}
]</pre>

Imprecise results without proper filtering {#h2-17-imprecise-results-without-proper-filtering}
----------------------------------------------------------------------------------------------

So far, we've been testing step by step by running the aggregation pipeline directly (via curl). Now, let's move to the application itself and run the same query through the web interface.

Open your browser at[**http://localhost:8080**](http://localhost:8080), and apply the same filters we used in the previous curl request:

* **Search term** = *a ship that sinks at night after hitting an iceberg*
* **Released year**= 1980--2003
* **Minimum IMDb rating** = 5
* **Genres** = (Drama, Action)

Just like in the screenshot below:  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-1.58.35-PM.png)

If we look closely at the results, we notice that some movies don't satisfy the pre-filters---for example, ***Night at the Museum*** is being returned even though it's from 2006, outside the requested year range of 1980--2003.

This happens because the filters were applied only inside the **vector search pipeline** . The **full-text pipeline** doesn't have those restrictions, so when $rankFusion merges the results, movies that score highly in full-text (like *Night at the Museum*) can still appear, even if they don't match the vector filters.

Making results accurate again {#h2-18-making-results-accurate-again}
--------------------------------------------------------------------

To make sure filters are applied consistently, we need to add them not only in the **vector pipeline** , but also in the **full-text pipeline**.

In practice, this means mirroring the same constraints (genres, year, IMDb rating) inside the compound.filter of the full-text query.

That way, both pipelines enforce the same rules before ranking results. Here's how the full-text pipeline looks once we align it with the vector filters:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;$search: {
&nbsp;&nbsp;&nbsp;index: 'fulltextsearch',
&nbsp;&nbsp;&nbsp;compound: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filter: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;in: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: 'genres',
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value: ['Action', 'Drama']
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;range: { path: 'year', gte: 1980 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;range: { path: 'year', lte: 2003 }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;range: { path: 'imdb.rating', gte: 5}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
should: [ { ... } ]
&nbsp;&nbsp;&nbsp;}
&nbsp;}
}</pre>

### Adjusting the index for filters {#h3-19-adjusting-the-index-for-filters}

If we look closely at the previous pipeline, we notice the use of the **"in"** operator on the genres field. For this to work correctly, we need to update our MongoDB Atlas Search index. String fields must be indexed as token type for operators like **"equals"** or **"in"** to function properly.

Here's the update to the full-text search index:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"mappings": {
&nbsp;&nbsp;&nbsp;"dynamic": true,
&nbsp;&nbsp;&nbsp;"fields": {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"genres": {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"normalizer": "lowercase",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "token"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}
&nbsp;}
}</pre>

Refactoring the pipeline in code {#h2-20-refactoring-the-pipeline-in-code}
--------------------------------------------------------------------------

Now that we've seen how the aggregation works in the shell, let's bring it into our Java code. To make things cleaner, we'll refactor the logic into small helper methods.

### 1. Creating the filters {#h3-21-1-creating-the-filters}

Open the MovieService and include the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private List&lt;SearchOperator&gt; buildFilters(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;var filters = new ArrayList&lt;SearchOperator&gt;();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (req.genres() != null &amp;&amp; !req.genres().isEmpty()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filters.add(in(SearchPath.fieldPath("genres"), req.genres()));
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;if (req.yearFrom() != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filters.add(numberRange(SearchPath.fieldPath("year")).gte(req.yearFrom()));
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;if (req.yearTo() != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filters.add(numberRange(SearchPath.fieldPath("year")).lte(req.yearTo()));
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;if (req.minIMDbRating() != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filters.add(numberRange(SearchPath.fieldPath("imdb.rating")).gte(req.minIMDbRating()));
&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;return filters;
}</pre>

The buildFilters() method collects all the filtering rules based on the MovieSearchRequest. It optionally adds filters for genres, year range, and IMDb rating, if they're provided.

### 2. Including search boost {#h3-22-2-including-search-boost}

The buildSearchClauses() method defines the fields where we'll search for text, the title, plot, and fullplot. Each field gets a different **boost** value to indicate how much it should influence the score.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private List&lt;SearchOperator&gt; buildSearchClauses(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;Map&lt;String, Float&gt; fieldConfigs = Map.of(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title", 4.0F,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"plot", 3.0F,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"fullplot", 2.0F
&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;return fieldConfigs.entrySet().stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.map(entry -&gt; text(SearchPath.fieldPath(entry.getKey()), req.query())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.fuzzy(fuzzySearchOptions().maxEdits(1))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.score(boost(entry.getValue())))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.collect(Collectors.toList());
}</pre>

### 3. The final pipeline {#h3-23-3-the-final-pipeline}

Still in the MovieService, replace the buildFullTextSearchPipeline() with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private BsonDocument buildFullTextSearchPipeline(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;var filters = buildFilters(req);
&nbsp;&nbsp;&nbsp;var searchClauses = buildSearchClauses(req);
&nbsp;&nbsp;&nbsp;var compound = compound();
&nbsp;&nbsp;&nbsp;compound = !filters.isEmpty() ? compound.filter(filters) : compound;

&nbsp;&nbsp;&nbsp;return Aggregates.search(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound.should(searchClauses),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SearchOptions.searchOptions().index("fulltextsearch")
&nbsp;&nbsp;&nbsp;).toBsonDocument();
}</pre>

**In short** : This method builds a compound query where the **filters** go into the filter() clause and the **text matches** go into the should() clause.

At this point, you'll notice that the searchMovies method will cause a compilation error, because the buildFullTextSearchPipeline method now takes a MovieSearchRequest object. To fix this, just pass it instead of sending only the query:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {

&nbsp;&nbsp;AggregationOperation rankFusion = context -&gt; new Document("$rankFusion",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("input",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("pipelines",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", List.of(buildFullTextSearchPipeline(req), new Document("$limit", config.topK())))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", List.of(buildVectorSearchPipeline(req)))))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("combination",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("weights",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", 0.5)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", 0.5)))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("scoreDetails", false));

&nbsp;&nbsp;Aggregation aggregation = Aggregation.newAggregation(rankFusion);

&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;aggregation,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;).getMappedResults();
}</pre>

### 4. Testing the refactored pipeline {#h3-24-4-testing-the-refactored-pipeline}

#### Case 1: Including genres

Let's run the same query again with our new pipeline.  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-2.01.23-PM.png)

As you can see in the results, the filters look correct.

#### Case 2: Excluding genres

Now, suppose the user clicks **Exclude selected genres** while keeping the same filter.  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-2.01.53-PM.png)

In this case, instead of asking for movies that include *Drama* or *Action* , we want the opposite: Only return movies **that do not belong** to these genres.

If we run the application right now, you'll notice that movies with *Action/Drama* still appear in the results:  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-2.02.17-PM.png)

This happens because our query doesn't yet apply any exclusion logic. What we really want to tell MongoDB Atlas Search is:

*"Return all documents that satisfy the other filters, but exclude anything with these genres."*

To fix this, we'll make two small adjustments:

1. Remove the **"in"** clause from the filter section.
2. Add the **"in"** clause inside a [mustNot](https://www.mongodb.com/docs/atlas/atlas-search/compound/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=spring-data-mongodb-hybrid-search-vectors&utm_term=ricardo.mello) option.

The updated pipeline will look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;$search: {
&nbsp;&nbsp;&nbsp;index: 'fulltextsearch',
&nbsp;&nbsp;&nbsp;compound: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;filter: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ range: { path: 'year', gte: 1980 } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ range: { path: 'year', lte: 2003 } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ range: { path: 'imdb.rating', gte: 5 } }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mustNot: [&nbsp; { in: { path: 'genres', value: ['Action', 'Drama'] }} ],
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;should: [ { … }&nbsp; ]
&nbsp;&nbsp;&nbsp;}
&nbsp;}
}</pre>

Adding exclusion logic to the application {#h2-25-adding-exclusion-logic-to-the-application}
--------------------------------------------------------------------------------------------

The final step is to update our application code so that it builds the mustNot clause. First, create the buildMustNot() method:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private List&lt;SearchOperator&gt; buildMustNot(MovieSearchRequest req) {
&nbsp;&nbsp;var mustNot = new ArrayList&lt;SearchOperator&gt;();

&nbsp;&nbsp;if (req.genres() != null &amp;&amp; !req.genres().isEmpty() &amp;&amp; req.excludeGenres()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mustNot.add(in(SearchPath.fieldPath("genres"), req.genres()));
&nbsp;&nbsp;}
&nbsp;&nbsp;return mustNot;
}</pre>

Next, update the buildFilters() method so it only adds genres when the **exclude selected genres** option is **not** selected. Open the method and replace the current block...

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (req.genres() != null &amp;&amp; !req.genres().isEmpty()) {
&nbsp;&nbsp;filters.add(in(SearchPath.fieldPath("genres"), req.genres()));
}</pre>

...with this version:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (req.genres() != null &amp;&amp; !req.genres().isEmpty() &amp;&amp; !req.excludeGenres()) {
&nbsp;&nbsp;filters.add(in(SearchPath.fieldPath("genres"), req.genres()));
}</pre>

And finally, replace the buildFullTextSearchPipeline() with this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private BsonDocument buildFullTextSearchPipeline(MovieSearchRequest req) {
&nbsp;&nbsp;var filters = buildFilters(req);
&nbsp;&nbsp;var searchClauses = buildSearchClauses(req);
&nbsp;&nbsp;var mustNot = buildMustNot(req);
&nbsp;&nbsp;var compound = compound();

&nbsp;&nbsp;if (!filters.isEmpty()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound = compound.filter(filters);
&nbsp;&nbsp;}

&nbsp;&nbsp;if (!mustNot.isEmpty()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound = compound.mustNot(mustNot);
&nbsp;&nbsp;}

&nbsp;&nbsp;return Aggregates.search(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compound.should(searchClauses),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SearchOptions.searchOptions().index("fulltextsearch")
&nbsp;&nbsp;).toBsonDocument();
}</pre>

Once that adjustment is made, we can restart the app and run the same query again. This time, you'll see that movies tagged with **Drama** or **Action** are no longer returned, ensuring the results respect the exclusion filter.  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-2.04.10-PM.png)

Prioritizing the vector pipeline {#h2-26-prioritizing-the-vector-pipeline}
--------------------------------------------------------------------------

When we first run the hybrid query with equal weights (0.5 for vector and 0.5 for full-text), the results look interesting: **Titanic** shows up first, followed by **A Knight's Tale**.

Why does this happen?

* **Titanic** is ranked highly by the **vector search** . The embedding of our query, "a ship that sinks at night after hitting an iceberg", is semantically very close to the plot of *Titanic* , so the vector similarity score pushes it to the top.  
* **A Knight's Tale** , on the other hand, comes from the **full-text search** . The query contains the word "night", and since we enabled fuzzy matching (maxEdits: 1), MongoDB Atlas Search interprets "knight" as close enough to "night". Because the match happens in the **title field** (which we boosted with a higher score), the movie gets a strong ranking, even though it's unrelated to our intended meaning.

Let's tweak our pipeline to give more weight to semantic similarity: Set the vector pipeline to **0.8** and the full-text pipeline to **0.2**:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {

&nbsp;&nbsp;AggregationOperation rankFusion = context -&gt; new Document("$rankFusion",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("input",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("pipelines",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", List.of(buildFullTextSearchPipeline(req), new Document("$limit", config.topK())))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", List.of(buildVectorSearchPipeline(req)))))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("combination",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("weights",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Document("searchPipeline", 0.2)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("vectorPipeline", 0.8)))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("scoreDetails", false));

&nbsp;&nbsp;Aggregation aggregation = Aggregation.newAggregation(rankFusion);

&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;aggregation,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;).getMappedResults();
}</pre>

Then, run the search again with the same inputs:  
![](/images/posts/2025/11/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3/Screenshot-2025-11-11-at-2.05.52-PM.png)

Now, we can see that the top results make more sense for this descriptive query. Try yourself by changing the weights and boost, and see the results.

Conclusion {#h2-27-conclusion}
------------------------------

We've reached the end of the Beyond Keywords series, where we explored how to go beyond traditional search approaches and build smarter applications with MongoDB.

* In Part 1:[*Implementing Semantic Search in Java with Spring Data*](https://foojay.io/today/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/), we focused on vector search with Spring Data, learning how to generate embeddings with Voyage AI and run semantic queries.  
* In Part 2:[*Beyond Keywords: Optimizing Vector Search with Filters and Caching*](https://foojay.io/today/beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2/), we enhanced our application with pre-filters for more precise results and explored strategies like caching embeddings to save cost and improve performance.  
* In this final chapter, we dug into Atlas Search, added filters, and combined it with vector search through hybrid search, unlocking the best of both worlds: exact keyword matching and semantic understanding.

It's important to remember: there's no universal rule for the "right" weights, boosts, or filters. The best setup is always query-dependent, some queries benefit more from vector similarity, others from exact keyword matching. The real goal is to establish a solid baseline that works well for most use cases, then adapt and fine-tune based on how your users actually search.

This is just the beginning, real-world applications will always require experimentation, fine-tuning, and iteration to balance precision and recall.

If you want to learn more join the[MongoDB Community](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-atlas-foojay-part3&utm_term=tony.kim) to ask questions and share your experience. And if you'd like to check the full source code from this series, you can find it [here](https://github.com/mongodb-developer/spring-data-mongodb-vector-search).

<br />
