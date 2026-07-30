---
title: "Beyond Keywords: Optimizing Vector Search with Filters and Caching (Part 2)"
slug: "beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2"
date: "2025-10-23T15:44:38+00:00"
lastmod: "2025-12-12T21:21:35+00:00"
description: "Welcome back! If you landed here without reading Part 1: Beyond Keywords: Implementing Semantic Search in Java With Spring Data, I recommend going back and checking it first so the steps in this article make more sense in sequence.This is the second part of a three-part series where we’re building a movie search application. So far, our app supports semantic search using vector queries with Spring Data and Voyage AI. In this article, we’ll take things further:Add filters to refine our vector search results.Explore strategies with Spring (such as caching) to reduce the cost of generating embeddings.Implement a basic frontend using only HTML, CSS, and JavaScript—just enough to test our API in a browser (UI is not the focus here)."
authors:
  - "ricardo-mello"
image: "/images/posts/2025/10/beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2/546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "AI"
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-an-ai-semantic-movie-recommender-with-vector-search"
  - "building-rest-apis-in-java-with-spring-boot"
enlighterjs: true
frozen: false
---

Enhancing precision with pre-filters and reducing costs with embedding caching

Welcome back! If you landed here without reading [*Part 1: Beyond Keywords: Implementing Semantic Search in Java With Spring Data*](https://foojay.io/today/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1/), I recommend going back and checking it first so the steps in this article make more sense in sequence.

This is the second part of a **three-part series** where we're building a movie search application. So far, our app supports **semantic search** using vector queries with Spring Data and Voyage AI. In this article, we'll take things further:

* Add filters to refine our vector search results.
* Explore strategies with Spring (such as caching) to reduce the cost of generating embeddings.
* Implement a **basic frontend** using only HTML, CSS, and JavaScript---just enough to test our API in a browser (UI is not the focus here).

The full source code for this part is available on [GitHub](https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search).

Adding filters: From story to code {#h2-0-adding-filters-from-story-to-code}
----------------------------------------------------------------------------

Imagine this: You've just finished building your shiny new semantic movie search. You type "a science fiction movie about rebels fighting an empire in space" and---boom---*Star Wars* pops up. Success!

But then your friend says:

*"Cool, but I only want movies with an IMDb rating of* ***9 or higher*** *. Can your app do that?"*

At this point, our application can only take a **query string**:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record MovieSearchRequest(String query) {}</pre>

So we need to evolve. First, let's extend the request with a minIMDbRating field to capture this new requirement:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record MovieSearchRequest(

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String query,

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double minIMDbRating

) {}</pre>

### First try: Add a post-filter in MovieService {#h3-1-first-try-add-a-post-filter-in-movieservice}

Open the MovieService class, find the searchMovies method, and append a [$match](https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=optimizing-vector-search-foojay&utm_term=tony.kim) stage that enforces the minIMDbRating threshold after the vector search:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;VectorSearchOperation vectorSearchOperation = VectorSearchOperation.search(config.vectorIndexName())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.path(config.vectorField())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.vector(embeddingService.embedQuery(req.query()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.limit(config.topK())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.numCandidates(config.numCandidates());

&nbsp;&nbsp;&nbsp;// Post-filter: apply the IMDb constraint after nearest neighbors are found
&nbsp;&nbsp;&nbsp;MatchOperation matchOperation = new MatchOperation(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Criteria.where("imdb.rating").gte(req.minIMDbRating())
&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aggregation.newAggregation(vectorSearchOperation, matchOperation),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;&nbsp;).getMappedResults();
}</pre>

Now try it:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">### POST
POST http://localhost:8080/movies/search
Content-Type: application/json
{
&nbsp;"query": "a science fiction movie about rebels fighting an empire in space",
&nbsp;"minIMDbRating": 9
}</pre>

When you run this query against our dataset, you'll notice***it returns nothing***. Why?

Because MongoDB Atlas:  

1. **First** performs the semantic vector search and finds a few close matches.
2. **Then** the $match filter is applied afterwards.

Since none of those candidates have an IMDb rating ≥ 9.0, all results are discarded, meaning you still paid for the vector search, but ended up with no data. It "works," but it's wasteful when constraints are strict.

So how can we save this extra work?

By using the [filter](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=optimizing-vector-search-foojay&utm_term=tony.kim#atlas-vector-search-pre-filter) option on [$vectorSearch](https://www.mongodb.com/docs/manual/reference/operator/aggregation/vectorsearch/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=optimizing-vector-search-foojay&utm_term=tony.kim), a pre-filter that lets MongoDB Atlas narrow results (e.g., by numeric fields like imdb.rating) before running the vector comparison. Let's check it.

### Second try: Use a pre-filter {#h3-2-second-try-use-a-pre-filter}

To implement the pre-filter, the first step is to update our **MongoDB** **Atlas Vector Search index** and include the field we want to filter by, in this case, imdb.rating:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"fields": [
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "vector",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "plot_embedding_voyage_3_large",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"numDimensions": 2048,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"similarity": "dotProduct"
&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "filter", &nbsp; &nbsp; &nbsp; &nbsp; // include this
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "imdb.rating" &nbsp; &nbsp; // include this
&nbsp;&nbsp;&nbsp;}
&nbsp;]
}</pre>

Once the index finishes updating, we can adjust our code. In the searchMovies method, remove the MatchOperation and apply the filter directly in the VectorSearchOperation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {
&nbsp;&nbsp;VectorSearchOperation vectorSearchOperation = VectorSearchOperation.search(config.vectorIndexName())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.path(config.vectorField())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.vector(embeddingService.embedQuery(req.query()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.limit(config.topK())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(Criteria.where("imdb.rating").gte(req.minIMDbRating()))//Pre-filter: apply the IMDb rating filter here
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.numCandidates(config.numCandidates());

&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aggregation.newAggregation(vectorSearchOperation),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;).getMappedResults();
}</pre>

Now, when you run the same request, the filter is applied first, and only then similarity is computed, returning results that already satisfy the IMDb rating constraint.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"title": "The Dark Knight",
&nbsp;"year": "2008",
&nbsp;"imdb": {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rating": 9.0
&nbsp;&nbsp;&nbsp;},
&nbsp;...
}</pre>

To learn more about pre-filters, check out the[official documentation](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=optimizing-vector-search-foojay&utm_term=tony.kim#atlas-vector-search-pre-filter).

### Refining the search with extra filters {#h3-3-refining-the-search-with-extra-filters}

As users refine their searches, they often want more than just a keyword, like finding movies within a time period or skipping certain genres.

We'll extend MovieSearchRequest to support year ranges and genre inclusion/exclusion:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.List;

public record MovieSearchRequest(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String query,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer yearFrom,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer yearTo,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;String&gt; genres,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double minIMDbRating,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boolean excludeGenres
) {}</pre>

To make the filters actually work, we need a way to translate the request fields into a MongoDB query. For this, our MovieSearchRequest record implements a toCriteria() method. Here is the complete MovieSearchRequest code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.data.mongodb.core.query.Criteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record MovieSearchRequest(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String query,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer yearFrom,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer yearTo,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;String&gt; genres,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double minIMDbRating,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boolean excludeGenres
) {

&nbsp;&nbsp;&nbsp;public Criteria toCriteria() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;final List&lt;Criteria&gt; parts = new ArrayList&lt;&gt;(3);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;String&gt; g = cleanedGenres();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (!g.isEmpty()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;parts.add(excludeGenres
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;? Criteria.where("genres").nin(g)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: Criteria.where("genres").in(g));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;YearBounds yb = normalizedYearBounds();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (yb.from != null || yb.to != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Criteria y = Criteria.where("year");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (yb.from != null) y = y.gte(yb.from);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (yb.to &nbsp; != null) y = y.lte(yb.to);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;parts.add(y);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (minIMDbRating != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;parts.add(Criteria.where("imdb.rating").gte(minIMDbRating));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (parts.isEmpty()) return new Criteria();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (parts.size() == 1) return parts.getFirst();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new Criteria().andOperator(parts.toArray(Criteria[]::new));
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public List&lt;String&gt; cleanedGenres() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Optional.ofNullable(genres).orElseGet(List::of).stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(Objects::nonNull)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.map(String::trim)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(s -&gt; !s.isEmpty())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.distinct()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.toList();
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;private YearBounds normalizedYearBounds() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer f = yearFrom, t = yearTo;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (f != null &amp;&amp; t != null &amp;&amp; f &gt; t) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int tmp = f; f = t; t = tmp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assert f != null;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assert t != null;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new YearBounds(f, t);
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;private record YearBounds(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer from,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer to
&nbsp;&nbsp;&nbsp;){}
}</pre>

**In short:** The request validates which filters are present---if any are set, it builds a criteria combining them; if not, it returns an empty criteria, meaning no filters are applied.

### Applying toCriteria() in the search {#h3-4-applying-tocriteria-in-the-search}

Now, instead of hardcoding just the IMDb rating filter, we can reuse our toCriteria() method. This way, any combination of filters (genres, year range, IMDb rating) is automatically applied. In MovieService, replace the searchMovies:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Movie&gt; searchMovies(MovieSearchRequest req) {
&nbsp;&nbsp;&nbsp;VectorSearchOperation vectorSearchOperation = VectorSearchOperation.search(config.vectorIndexName())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.path(config.vectorField())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.vector(embeddingService.embedQuery(req.query()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.limit(config.topK())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(req.toCriteria()) // here is the modification
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.numCandidates(config.numCandidates());

&nbsp;&nbsp;&nbsp;return mongoTemplate.aggregate(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aggregation.newAggregation(vectorSearchOperation),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;config.vectorCollectionName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Movie.class
&nbsp;&nbsp;&nbsp;).getMappedResults();
}</pre>

After these modifications, the last step is to include the additional filter fields (such as year and genres) in your MongoDB Atlas Vector Search index definition:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"fields": [
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "vector",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "plot_embedding_voyage_3_large",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"numDimensions": 2048,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"similarity": "dotProduct"
&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "filter",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "imdb.rating"
&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "filter",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "year"
&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type": "filter",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "genres"
&nbsp;&nbsp;&nbsp;}
&nbsp;]
}</pre>

Once the index finishes building, these filters will be ready to use in your queries. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">### POST

POST http://localhost:8080/movies/search
Content-Type: application/json

{
&nbsp;"query": "a science fiction movie about rebels fighting an empire in space",
&nbsp;"minIMDbRating": 9,
&nbsp;"yearFrom": 2010,
&nbsp;"yearTo": 2015,
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;"Drama", "Action"
&nbsp;],
&nbsp;"excludeGenres": false
}</pre>

You should see a similar result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"title": "The Real Miyagi",
&nbsp;"year": "2015",
&nbsp;"imdb": {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rating": 9.3
&nbsp;&nbsp;&nbsp;},
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;"Documentary",
&nbsp;&nbsp;&nbsp;"Action",
&nbsp;&nbsp;&nbsp;"History"
&nbsp;],
&nbsp;...
}</pre>

Reducing embedding costs with caching {#h2-5-reducing-embedding-costs-with-caching}
-----------------------------------------------------------------------------------

When testing the search endpoint, you might notice that embeddings are generated every single time, even if the query text doesn't change. For example, if you keep searching for...

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"query": "a science fiction movie about rebels fighting an empire in space",
&nbsp;"minIMDbRating": 9,
&nbsp;"yearFrom": 2010,
&nbsp;"yearTo": 2015,
&nbsp;"genres": [
&nbsp;&nbsp;&nbsp;"Drama", "Action"
&nbsp;],
&nbsp;"excludeGenres": false
}</pre>

...and then repeat the same query but change only the filters (genres, year, minIMDbRating), the log still shows embeddings being generated:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">2025-09-04T11:48:47.298-03:00&nbsp; INFO 27180 --- [nio-8080-exec-1] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Generating embeddings ..
2025-09-04T11:48:48.513-03:00&nbsp; INFO 27180 --- [nio-8080-exec-1] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Embeddings generated successfully!
2025-09-04T11:48:52.438-03:00&nbsp; INFO 27180 --- [nio-8080-exec-2] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Generating embeddings ..
2025-09-04T11:48:52.737-03:00&nbsp; INFO 27180 --- [nio-8080-exec-2] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Embeddings generated successfully!</pre>

But here's the thing: **If the query text doesn't change, there's no need to regenerate the embeddings**. The conversion is deterministic, the same text always produces the same vector.

### Strategy with @Cacheable {#h3-6-strategy-with-cacheable}

To avoid unnecessary API calls, we can cache embeddings for repeated queries. In Spring, this is as simple as annotating the embedQuery method in EmbeddingService:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Cacheable("embeddings")

public List&lt;Double&gt; embedQuery(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String text) {
&nbsp;&nbsp;&nbsp;logger.info("Generating embeddings .. ");
&nbsp;&nbsp;&nbsp;var res = client.embed(new EmbeddingsRequest(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List.of(text), config.model(), "query", config.outputDimension()));
&nbsp;&nbsp;&nbsp;logger.info("Embeddings generated successfully!");
&nbsp;&nbsp;&nbsp;return res.data().getFirst().embedding();
}</pre>

And finally, don't forget to enable it in your Spring Boot application class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching

public class SpringDataMongodbHybridSearchApplication
&nbsp;{
&nbsp;&nbsp;&nbsp;public static void main(String[] args) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SpringApplication.run(SpringDataMongodbHybridSearchApplication
.class, args);
&nbsp;&nbsp;&nbsp;}
}</pre>

After enabling caching, run a few searches in a row with the same query text but different filters (genres, year, minIMDbRating). You'll notice that the log message appears only the first time.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">2025-09-04T11:50:04.283-03:00&nbsp; INFO 27322 --- [nio-8080-exec-1] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Generating embeddings ..
2025-09-04T11:50:05.086-03:00&nbsp; INFO 27322 --- [nio-8080-exec-1] com.mongodb.EmbeddingService &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : Embeddings generated successfully!</pre>

**In short:** Caching embeddings prevents redundant API calls, saves cost, and improves response time without changing your search logic. For production, there are more advanced ways to manage caching (e.g., distributed caches, eviction policies). Here, we're just showing a simple idea to illustrate the concept.

A minimal frontend {#h2-7-a-minimal-frontend}
---------------------------------------------

Before wrapping up, let's add a very simple frontend. The goal here is **not** to focus on UI or design, but just to provide a way to test our API in the browser. I'll leave a small example in HTML, JavaScript, and CSS. Feel free to adapt it and build a nicer page if you'd like.

### Step 1: HTML {#h3-8-step-1-html}

Download the index.html file from [this repository](https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search/blob/main/src/main/resources/static/index.html) and save it inside src/main/resources/static.

### Step 2: JavaScript {#h3-9-step-2-javascript}

Download the script.js file from [this repository](https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search/blob/main/src/main/resources/static/script.js) and place it in the same folder.

### Step 3: CSS {#h3-10-step-3-css}

Finally, download the styles.css file from [this repository](https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search/blob/main/src/main/resources/static/styles.css)and place it in the same folder.

Running the frontend {#h2-11-running-the-frontend}
--------------------------------------------------

### Step 1: Start the application {#h3-12-step-1-start-the-application}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn spring-boot:run</pre>

### Step 2: Open the application {#h3-13-step-2-open-the-application}

With the backend already running, simply open http://localhost:8080 in your browser and search for:

* **Search term** = *a ship that sinks at night after hitting an iceberg*
* **Released year** = 1980--2003
* **Minimum IMDb rating** = 5
* **Genres** = (Drama, Action)

Then, click to view movie details.
![](/images/posts/2025/10/beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2/Screenshot-2025-10-13-at-12.52.44-PM.png)

Wrapping up {#h2-14-wrapping-up}
--------------------------------

In this second part, we explored how to use pre-filters in MongoDB Atlas Vector Search to make queries more efficient, and we looked at strategies to save resources by avoiding unnecessary embedding generation with caching.

In the third and final *Part 3 - Beyond Keywords: Hybrid Search with Atlas And Vector Search*, we'll adapt our code to implement hybrid search, combining MongoDB Atlas Search with vector queries for even more powerful results.

You can check out the full source code for this part on [GitHub](https://github.com/mongodb-developer/spring-data-mongodb-vector-search).
