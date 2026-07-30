---
title: "Keywords Meet Vectors: Hybrid Search on MongoDB"
slug: "keywords-meet-vectors-hybrid-search-on-mongodb"
date: "2025-10-09T14:49:49+00:00"
lastmod: "2025-10-09T14:49:51+00:00"
description: "Hybrid search in MongoDB brings together two complementary search techniques:Full text search (BM25 via Atlas Search)—optimized for exact keyword matches, powered by Lucene inside mongot. Perfect when users expect documents that literally contain their query terms.Vector search (kNN via Atlas Vector Search)—optimized for semantic similarity. It uses dense embeddings from ML models to find conceptually related content, even when no keywords match.On their own, each method has advantages and limitations. Text search misses context (“non-linear crime story” won’t return Memento). Pure semantic search may return results that are semantically aligned but sometimes not practically useful. Hybrid search combines the strengths of both, ensuring results are contextually relevant and precise."
authors:
  - "arekborucki"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "AI"
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-an-ai-semantic-movie-recommender-with-vector-search"
enlighterjs: true
frozen: false
---

In the previous issues, I explained how to run a local [MongoDB](https://www.linkedin.com/company/mongodbinc/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BnV1cKI74RDKmiAI4lr1TaA%3D%3D) Atlas cluster using [Atlas CLI](https://www.linkedin.com/pulse/run-local-atlas-cluster-minutes-locally-arek-borucki-mmiqf/?trackingId=M71jHZpGSQCdFygE5EgAOw%3D%3D), what [vector search](https://www.linkedin.com/pulse/power-your-ai-application-vector-search-arek-borucki-sjw0f/?trackingId=vjRobV3lSdy1nc4SIgjM%2Fg%3D%3D) is, and [how to use it](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=S%2FClUOdjSMGzvkR2ZLIS9Q%3D%3D). Now, let's take a closer look at hybrid search.

Reading time: 4--5 min

What is hybrid search? {#h2-0-what-is-hybrid-search}
----------------------------------------------------

Hybrid search in MongoDB brings together **two complementary search techniques**:

* **Full text search** ([BM25](https://en.wikipedia.org/wiki/Okapi_BM25) via [Atlas Search](https://www.mongodb.com/docs/atlas/atlas-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant))---optimized for exact keyword matches, powered by Lucene inside mongot. Perfect when users expect documents that literally contain their query terms.

<!-- -->

* **Vector search** ([kNN](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm) via [Atlas Vector Search](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-overview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant))---optimized for semantic similarity. It uses dense embeddings from ML models to find conceptually related content, even when no keywords match.

On their own, each method has advantages and limitations. Text search misses context ("non-linear crime story" won't return Memento). Pure semantic search may return results that are semantically aligned but sometimes not practically useful. Hybrid search combines the strengths of both, ensuring results are contextually relevant and precise.

How does it work in MongoDB? {#h2-1-how-does-it-work-in-mongodb}
----------------------------------------------------------------

MongoDB Atlas (and soon MongoDB Community Edition and Enterprise Advanced) handles both layers natively:

* **mongot** runs [BM25](https://python.langchain.com/docs/integrations/retrievers/bm25/) (text search) and [kNN](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm) (vector search) on the same dataset.

<!-- -->

* Results are merged using ranking strategies such as **Reciprocal Rank Fusion** ([RRF](https://medium.com/@devalshah1619/mathematical-intuition-behind-reciprocal-rank-fusion-rrf-explained-in-2-mins-002df0cc5e2a)) or **Relative Score Fusion** ([RSF](https://docsbot.ai/article/enhanced-rag-search-with-the-relativescorefusion-algorithm)).

<!-- -->

* You can also filter or rerank using metadata (e.g., year, rating, genre) with the aggregation pipeline.

This means you don't need multiple databases (like MongoDB + Elastic + Pinecone). Both your operational data and embeddings stay in MongoDB.

### Why movies collection are the perfect demo {#h3-2-why-movies-collection-are-the-perfect-demo}

Movie data (from the MongoDB [sample dataset](https://www.mongodb.com/docs/atlas/sample-data/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#available-sample-datasets)) contains both **structured fields** (title, year, genres, ratings) and **unstructured text** (plots). That makes the [sample_mflix.embedded_movies](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies) dataset an ideal sandbox: You can run keyword search, vector search, or combine them into hybrid pipelines. You can read more about it in 👉 [this article](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D). Let's walk through an example using *Inception* (2010) as the anchor.

### Inspecting the anchor document {#h3-3-inspecting-the-anchor-document}

You first need to confirm that the Inception document exists in the dataset and that embeddings are present. This provides metadata for context and the vector that drives semantic similarity.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.find({ title: "Inception" },{ title: 1, year: 1, genres: 1, imdb: 1, plot: 1, plot_embedding: 1})</pre>

The query should return:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"title": "Inception",
&nbsp;&nbsp;"year": 2010,
&nbsp;&nbsp;"genres": ['Action', 'Mystery', 'Sci-Fi'],
&nbsp;&nbsp;"imdb": { rating: 8.8, votes: 1294646, id: 1375666 },
&nbsp;&nbsp;"plot": "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
&nbsp;&nbsp;"plot_embedding": &lt;Binary Data, 1536 dimensions&gt;
}</pre>

The presence of **plot_embedding** confirms this document can serve as a query vector.

### Sanity checks {#h3-4-sanity-checks}

You need a [vector index](https://www.mongodb.com/docs/manual/reference/command/createsearchindexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) on [plot_embedding](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies), and you need embeddings stored in BSON Binary (Float32). If the index does not exist, create it (check this article 👉 [how](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D)). Otherwise, verify it's READY.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Check that the vector search index is available
db.getSiblingDB("sample_mflix").embedded_movies.getSearchIndexes()</pre>

Expected output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;&nbsp;&nbsp;id: '68983b85c2c844543026fa6a',
&nbsp;&nbsp;&nbsp;&nbsp;name: 'plot_embedding_index',
&nbsp;&nbsp;&nbsp;&nbsp;type: 'search',
&nbsp;&nbsp;&nbsp;&nbsp;status: 'READY',
&nbsp;&nbsp;&nbsp;&nbsp;queryable: true,
&nbsp;&nbsp;&nbsp;&nbsp;latestVersion: 0,
&nbsp;&nbsp;&nbsp;&nbsp;latestDefinition: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mappings: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dynamic: false,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fields: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;plot_embedding: { type: 'knnVector', dimensions: 1536, similarity: 'cosine' }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
]</pre>

### Step 1: Prepare the query vector {#h3-5-step-1-prepare-the-query-vector}

MongoDB stores embeddings compactly as BSON Binary (Float32) for storage and indexing efficiency, while [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#mongodb-pipeline-pipe.-vectorSearch) expects the queryVector as a plain [JavaScript array](https://www.w3schools.com/js/js_arrays.asp). You need to extract and convert it at query time.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">const d = db.getSiblingDB("sample_mflix").embedded_movies.findOne(
&nbsp;&nbsp;{ title: "Inception" },
&nbsp;&nbsp;{ plot_embedding: 1, _id: 0 }
)

const qv = Array.from(d.plot_embedding.toFloat32Array())</pre>

Here, **qv** becomes a 1,536-element JavaScript array representing the semantic meaning of Inception.

### Step 2: Run semantic search {#h3-6-step-2-run-semantic-search}

With the query vector ready (**qv** ), you search for movies whose **plots are conceptually similar** to *Inception---*for example, titles involving dream manipulation, layered realities, high-stakes heists, or unreliable perception. This step ignores exact keywords and measures conceptual closeness.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "plot_embedding_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: qv,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 200,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 5
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $match: { title: { $ne: "Inception" } } },
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$project: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;genres: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: { $meta: "vectorSearchScore" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_id: 0
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
])
[
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 2001,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Thriller' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Swordfish',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.931791365146637
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Fantasy', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The City of Lost Children',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1995,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9285156726837158
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 2013,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Thriller' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Parker',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9258596897125244
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 1999,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Comedy' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Inspector Gadget',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9235274791717529
&nbsp;&nbsp;}
]</pre>

Semantic search finds thematically close titles, but ranking does not yet reflect quality.

### Step 3: Apply hybrid scoring {#h3-7-step-3-apply-hybrid-scoring}

You need to combine semantic similarity with IMDb ratings to boost well-reviewed titles. This ensures results are not only close in meaning but also valued by audiences.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "plot_embedding_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: qv,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 1500,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 100
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $match: { year: { $gte: 1990 }, title: { $ne: "Inception" } } },
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$addFields: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$add: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $multiply: [ { $meta: "vectorSearchScore" }, 0.7 ] },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $multiply: [ { $divide: ["$imdb.rating", 10] }, 0.3 ] }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $sort: { hybridScore: -1 } },
&nbsp;&nbsp;{ $limit: 5 },
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$project: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;imdb: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: { $meta: "vectorSearchScore" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_id: 0
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
])</pre>

Example results:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8.7, votes: 1080566, id: 133093 },
&nbsp;&nbsp;&nbsp;&nbsp;year: 1999,
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The Matrix',
&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 0.9070646867752075,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9229495525360107
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Athadu',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2005,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8.4, votes: 4569, id: 471571 },
&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 0.8886410732269286,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.909487247467041
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The City of Lost Children',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1995,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.7, votes: 52784, id: 112682 },
&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 0.880960970878601,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9285156726837158
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Room 8',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2013,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8, votes: 762, id: 2949338 },
&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 0.8803098821640014,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9147284030914307
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.8, votes: 271917, id: 2802144 },
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Kingsman: The Secret Service',
&nbsp;&nbsp;&nbsp;&nbsp;hybridScore: 0.8751985874176025,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9159979820251465
&nbsp;&nbsp;}
]</pre>

This hybrid scoring surfaces titles that are both semantically similar and widely acclaimed.

### Step 4: Hybrid ranking with Reciprocal Rank Fusion (RRF) {#h3-8-step-4-hybrid-ranking-with-reciprocal-rank-fusion-rrf}

In MongoDB, the [$search](https://www.mongodb.com/docs/manual/reference/operator/aggregation/search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) aggregation pipeline stage is also available. It runs [text search](https://www.mongodb.com/resources/basics/full-text-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) on a mongot (Lucene/BM25) index, supports **text/phrase/autocomplete** , and returns a per-document **searchScore** with **analyzers** , **stemming** , and **language** options.

#### $search vs. $vectorSearch (at a glance)

* **Engine and index** : Both run in **mongot** backed by Lucene. [$search](https://www.mongodb.com/docs/manual/reference/operator/aggregation/search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) executes text operators on a **search index** ; [$vectorSearch](https://www.mongodb.com/docs/manual/reference/operator/aggregation/vectorsearch/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) performs k-NN on knnVector fields defined in that same search index.

<!-- -->

* **Input** : $search takes a text query; $vectorSearch takes a numeric **query vector** ([embedding](https://platform.openai.com/docs/guides/embeddings)).

<!-- -->

* **Output score**: $search → { $meta: "searchScore" } (keyword relevance). $vectorSearch → { $meta: "vectorSearchScore" } (semantic proximity).

<!-- -->

* **Strength**: $search captures exact lexical intent; $vectorSearch captures meaning when words don't align.

**RRF pattern (text + vector → one list)**

Use **$search.text** (query: "**computer hacker** " across **title** , **plot** , **fullplot** ) and **$vectorSearch** (Inception's **embedding**) as two independent legs.

Rank each leg, union the results, then fuse them with RRF. When a movie appears in both legs, it has non-zero textRank and vectorRank (1 = best per leg), so RRF rewards agreement between keyword relevance and semantic similarity, producing a single balanced ranking.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
&nbsp;&nbsp;// A) TEXT (BM25) — compute rank and RRF contribution in this leg
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$search: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "hybrid_text",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text: { query: "computer hacker", path: ["title","plot","fullplot"] }
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $set: { score: { $meta: "searchScore" } } },
&nbsp;&nbsp;{ $setWindowFields: { sortBy: { score: -1 }, output: { textRank: { $documentNumber: {} } } } },
&nbsp;&nbsp;{ $set: { rrf: { $divide: [1, { $add: [60, "$textRank"] }] } } }, // RRF piece for the text leg
&nbsp;&nbsp;{ $project: { title:1, year:1, genres:1, imdb:1, textRank:1, rrf:1 } },

&nbsp;&nbsp;// B) VECTOR (kNN) — do the same for the vector leg
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$unionWith: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;coll: "embedded_movies",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pipeline: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "plot_embedding_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: qv,&nbsp; &nbsp; &nbsp; &nbsp; // Inception embedding array
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 1000,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 300
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $set: { score: { $meta: "vectorSearchScore" } } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $setWindowFields: { sortBy: { score: -1 }, output: { vectorRank: { $documentNumber: {} } } } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $set: { rrf: { $divide: [1, { $add: [60, "$vectorRank"] }] } } }, // RRF piece for the vector leg
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $project: { title:1, year:1, genres:1, imdb:1, vectorRank:1, rrf:1 } }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},

&nbsp;&nbsp;// C) Fusion — sum RRF contributions; carry ranks from each leg if present
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$group: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_id: "$_id",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title:&nbsp; { $first: "$title" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: &nbsp; { $first: "$year" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;genres: { $first: "$genres" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;imdb: &nbsp; { $first: "$imdb" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;textRank: &nbsp; { $max: { $ifNull: ["$textRank", 0] } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: { $max: { $ifNull: ["$vectorRank", 0] } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;rrf:&nbsp; &nbsp; &nbsp; &nbsp; { $sum: "$rrf" }
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;// (optional) keep only overlap (items present in BOTH legs)
&nbsp;&nbsp;// { $match: { textRank: { $gt: 0 }, vectorRank: { $gt: 0 } } },
&nbsp;&nbsp;// (optional) exclude the anchor movie if it shows up
&nbsp;&nbsp;{ $match: { title: { $ne: "Inception" } } },
&nbsp;&nbsp;{ $sort: { rrf: -1 } },
&nbsp;&nbsp;{ $limit: 10 },
&nbsp;&nbsp;{ $project: { _id:0, title:1, year:1, genres:1, imdb:1, rrf:1, textRank:1, vectorRank:1 } }
])</pre>

Example result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The Matrix',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1999,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8.7, votes: 1080566, id: 133093 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 1,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 7,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.03131881575727918
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'TRON',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1982,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 6.8, votes: 88860, id: 84827 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 2,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 6,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.03128054740957967
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Swordfish',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2001,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Thriller' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 6.5, votes: 148103, id: 244244 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 14,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 2,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.02964254577157803
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The Net',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1995,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Drama' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.8, votes: 45996, id: 113957 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 11,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 30,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.025195618153364633
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Arrambam',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2013,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Drama', 'Mystery' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.5, votes: 5957, id: 2555958 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 3,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 54,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.024644945697577275
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Blackhat',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2015,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Drama' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.4, votes: 27798, id: 2717822 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 10,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 42,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.024089635854341734
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Speed 2: Cruise Control',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1997,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Romance' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 3.7, votes: 57010, id: 120179 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 6,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 60,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.023484848484848483
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Sivaji',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2007,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Drama' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.4, votes: 7920, id: 479751 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 23,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 29,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.023284147827264113
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Open Windows',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Thriller' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.2, votes: 8894, id: 2409818 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 26,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 39,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.02172891707775429
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Nicotina',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2003,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Comedy', 'Crime' ],
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 6.7, votes: 2969, id: 337930 },
&nbsp;&nbsp;&nbsp;&nbsp;textRank: 7,
&nbsp;&nbsp;&nbsp;&nbsp;vectorRank: 94,
&nbsp;&nbsp;&nbsp;&nbsp;rrf: 0.021418879627834852
&nbsp;&nbsp;}
]</pre>

This yields **one ranked list** that balances **keyword intent** (textRank) and **semantic meaning** (vectorRank). Items that rank well in both legs rise to the top; items strong in only one leg still get credit but are ranked lower.

Wrap-up {#h2-9-wrap-up}
-----------------------

* **Hybrid search = BM25 + k-NN** . In MongoDB, you combine **Atlas Search** (Lucene/BM25 in mongot) with **Atlas Vector Search** (k-NN over knnVector fields) to capture both exact intent and semantic similarity.

<!-- -->

* **Run semantic retrieval** . $vectorSearch surfaces titles close in meaning to *Inception* (dreams, layered realities, heists), regardless of shared keywords.

<!-- -->

* **Add business signals**. Re-rank with a hybridScore that blends the vector similarity with a normalized IMDb rating so results reflect both theme and quality.

<!-- -->

* **Fuse text + vectors with RRF** . When knnBeta can't be combined with a compound in a single $search, run two legs---$search.text and $vectorSearch---rank each leg, then fuse with **RRF**. Items with non-zero textRank and vectorRank (1 = best per leg) are promoted, yielding one balanced list.

<!-- -->

* **Keep it in MongoDB**. Indexing, vectors, filters, and ranking all live in one place---no cross-system syncing.

### More tips like this {#h3-10-more-tips-like-this}

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 [MongoDB in Action: Building on the Atlas Data Platform](https://www.manning.com/books/mongodb-in-action-third-edition?utm_source=borucki&utm_medium=affiliate&utm_campaign=book_borucki&a_aid=borucki&a_bid=523e1217&chan=mm_flyinghighwithflutter). Published by [Manning Publications Co](https://www.linkedin.com/company/manning-publications-co/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BnV1cKI74RDKmiAI4lr1TaA%3D%3D).
