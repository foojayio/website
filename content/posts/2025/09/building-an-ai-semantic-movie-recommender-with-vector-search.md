---
title: "Building an AI Semantic Movie Recommender with Vector Search"
slug: "building-an-ai-semantic-movie-recommender-with-vector-search"
date: "2025-09-30T13:23:18+00:00"
lastmod: "2025-09-30T13:23:38+00:00"
description: "Last time, we created a vector search index in a local MongoDB Atlas cluster. Now, let’s put it to work with a real case: building an AI-powered movie recommender that suggests films similar to The Matrix–without any shared keywords."
authors:
  - "arekborucki"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "AI"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Last time, we created a [vector search index](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D) in a [local MongoDB Atlas cluster](https://www.linkedin.com/pulse/run-local-atlas-cluster-minutes-locally-arek-borucki-mmiqf/?trackingId=ntYzEbTVSuauLVmp2Zbt4w%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D). Now, let's put it to work with a real case: building an AI-powered movie recommender that suggests films similar to *The Matrix*--without any shared keywords.

🕒 Reading time: 3-4 min

🎯 The challenge

This demo will be entirely based on the pre-generated vector embeddings already stored in the [sample_mflix](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim#std-label-sample-mflix) dataset---no API calls and no new model runs. Everything will remain local. If you haven't done it yet, learn how in this article 👉 [Loading embeddings into MongoDB](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D)

🧠 *The Matrix* scenario

When you load the sample dataset to MongoDB, one of the movies you'll find in the [embedded_movies](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim#std-label-mflix-embedded_movies) collection is *The Matrix* . You can check it with the [find command](https://www.mongodb.com/docs/manual/reference/method/db.collection.find/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.find({ title: "The Matrix" })</pre>

The document includes standard fields like title, plot, and genres, plus two vector embeddings:

* plot_embedding → 1536 dimensions from OpenAI's [text-embedding-ada-002](https://platform.openai.com/docs/models/text-embedding-ada-002)
* plot_embedding_voyage_3_large → 2048 dimensions from Voyage AI's [voyage-3-large](https://blog.voyageai.com/2025/01/07/voyage-3-large/)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"title": "The Matrix",
&nbsp;&nbsp;"year": 1999,
&nbsp;&nbsp;"genres": ["Action", "Sci-Fi"],
&nbsp;&nbsp;"rated": "R",
&nbsp;&nbsp;"plot": "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
&nbsp;&nbsp;"fullplot": "Thomas A. Anderson is a man living two lives...",
&nbsp;&nbsp;"imdb": { "rating": 8.7, "votes": 1080566 },
&nbsp;&nbsp;"metacritic": 73,
&nbsp;&nbsp;"languages": ["English"],
&nbsp;&nbsp;"writers": ["Andy Wachowski", "Lana Wachowski"],
&nbsp;&nbsp;"directors": ["Andy Wachowski", "Lana Wachowski"],
&nbsp;&nbsp;"cast": ["Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss", "Hugo Weaving"],
&nbsp;&nbsp;"countries": ["USA", "Australia"],
&nbsp;&nbsp;"runtime": 136,
&nbsp;&nbsp;"released": "1999-03-31",
&nbsp;&nbsp;"awards": "Won 4 Oscars. Another 33 wins &amp; 40 nominations.",
&nbsp;&nbsp;"poster": "https://m.media-amazon.com/images/M/...jpg",
&nbsp;&nbsp;"plot_embedding": [-0.0065, -0.0334, -0.0149, -0.0390, -0.0114, 0.0089, -0.0314, -0.01881, -0.0534,-0.0734, -0.016608...],
&nbsp;&nbsp;"plot_embedding_voyage_3_large": [-0.0376, 0.0339, -0.0164, -0.0154,-0.0134,-0.5164, -0.0371, -0.01881, -0.016608, 0.0920, 0.0474, ...]
}</pre>

These embeddings encode meaning, not just words. You can use them so MongoDB finds movies with a similar concept, even when plots share no obvious keywords.

For this tutorial, let's use *The Matrix* 's [plot_embedding](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies) as your query vector. Since this embedding is already stored in the document, you simply retrieve it and pass it to the [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#syntax) stage as a query parameter---no extra model calls required.

🔍 Validating the search index

First, check if a knnVector index exists (vector field type used for k-nearest neighbors search on high-dimensional numeric data), because [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant) only works on indexed vector fields. If the index is missing, create it 👉 [Creating Vector Search index](https://www.linkedin.com/article/edit/7355331187805978625/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B9ndpuBouS2KE9PcuhU4W2A%3D%3D). You can also read [this article](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#std-label-avs-types-vector-search). Now, execute [getSearchIndexes](https://www.mongodb.com/docs/manual/reference/method/db.collection.getSearchIndexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.getSearchIndexes()</pre>

You should see something like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{
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

Both indexes should be in READY status so you can run queries.

📦 Checking stored vectors

You'll be using the plot_embedding. First, confirm that the plot_embedding field stores its data as [BSON](https://en.wikipedia.org/wiki/BSON) Binary in Float32 format. This ensures it is ready for efficient vector search.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.countDocuments({ plot_embedding: { $type: "binData" } })</pre>

Result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">3402</pre>

3402 means 3,402 documents in embedded_movies have a ready-to-use vector in BSON Binary format.

📏 Extracting the query vector

The $vectorSearch can search BSON Binary vector fields directly inside MongoDB because the index is built on that binary Float32 data.

However, when you run $vectorSearch from the MongoDB shell (mongosh) or from application code, you must pass the query vector as a plain JavaScript array of numbers---not as raw BSON binary.

MongoDB [stores embeddings in documents as BSON Binary](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant) (Float32) because it's compact and efficient for indexing. The vector search index uses this binary data internally without conversion. But the [queryVector](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#fields) parameter is an input to the search operation. It isn't read from the indexed data---it's sent from your code. This means you need to decode the BSON Binary into a standard JavaScript array before passing it to $vectorSearch.

As shown below, you fetch the plot_embedding BSON Binary for *The Matrix*, convert it to a Float32Array, and then convert that to a plain JavaScript array for $vectorSearch.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Get The Matrix embedding from the document
const d = db.getSiblingDB("sample_mflix").embedded_movies.findOne(
&nbsp;&nbsp;{ title: "The Matrix" },
&nbsp;&nbsp;{ plot_embedding: 1, _id: 0 }
)

// Convert BSON Binary (Float32) -&gt; Float32Array -&gt; plain JS array
const qv = Array.from(d.plot_embedding.toFloat32Array())</pre>

Next, run:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">qv.length

1536</pre>

qv.length returns 1536, confirming the correct dimension.

Now, you can use the qv variable as the queryVector parameter in a $vectorSearch stage to find documents whose embeddings are most similar in meaning to *The Matrix*.

🚀 Running semantic search

Pass qv into [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#mongodb-pipeline-pipe.-vectorSearch) as the query vector---the reference point used to compare against all indexed vectors. The search engine uses cosine similarity to measure how close each stored embedding is to qv, ranking results from most to least similar. This returns movies that are conceptually close to *The Matrix*, even if they don't share obvious keywords.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "plot_embedding_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: qv,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 200,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 10
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$match: { title: { $ne: "The Matrix" } }
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$project: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;genres: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: { $meta: "vectorSearchScore" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_id: 0
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
])</pre>

Expected output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'TRON',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1982,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9550351500511169
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Drama', 'Mystery' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Arrambam',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2013,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9546242952346802
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 2001,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Thriller' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Swordfish',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9543327689170837
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 1995,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Crime', 'Drama' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The Net',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9502608180046082
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Drama' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Tuff Turf',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1985,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9378551244735718
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 2015,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Comedy', 'Crime' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Spy',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9367037415504456
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'V: The Final Battle',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1984,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9352985620498657
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Jumper',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2008,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9346113204956055
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Comedy' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Kingsman: The Secret Service',
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9341350793838501
&nbsp;&nbsp;}
]</pre>

Here, *TRON* , *Swordfish* , and *The Net* rank high, thematically similar to *The Matrix* even without matching keywords.

⚖ Running hybrid search (vector + IMDb rating)

Instead of ranking results purely by semantic similarity, [you can combine meaning-based search with a quality signal](https://www.mongodb.com/resources/products/capabilities/hybrid-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant)---in this case, IMDb ratings. By weighting semantic similarity at 75% and IMDb rating at 25%, you still get matches that are close in meaning to *The Matrix*, but movies that are also popular and well-reviewed will rank higher.

This hybrid approach is useful when you want search results that are both relevant in meaning and favored by audiences or critics.

Below, the pipeline first performs a vector search to get semantically similar movies, filters by genre/year, then adds a hybrid score combining the vector score with the normalized IMDb rating, sorts by this hybrid score, and returns the top results:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$vectorSearch: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;index: "plot_embedding_index",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path: "plot_embedding",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;queryVector: qv,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;numCandidates: 1500,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;limit: 50
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $match: { genres: "Sci-Fi", year: { $gte: 1990 }, title: { $ne: "The Matrix" } } },
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$addFields: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hybrid: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$add: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $multiply: [ { $meta: "vectorSearchScore" }, 0.75 ] },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ $multiply: [ { $divide: ["$imdb.rating", 10] }, 0.25 ] }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $sort: { hybrid: -1 } },
&nbsp;&nbsp;{ $limit: 20 },
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$project: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;year: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;genres: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;score: { $meta: "vectorSearchScore" },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;imdb: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 1,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_id: 0
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
])</pre>

Expected output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8.1, votes: 548314, id: 2015381 },
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Guardians of the Galaxy',
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.89929194688797,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.92905592918396
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Guardians of the Galaxy',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 8.1, votes: 539583, id: 2015381 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8992636048793793,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9290181398391724
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Edge of Tomorrow',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2014,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.9, votes: 357609, id: 1631867 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8965482211112976,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9320642948150635
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Animation', 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Ghost in the Shell Arise: Border 1 - Ghost Pain',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2013,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.3, votes: 1537, id: 2636124 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8788538080453873,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9284717440605164
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Transformers',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2007,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 7.1, votes: 479049, id: 418279 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8741268122196197,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9288357496261597
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Terminator 3: Rise of the Machines',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2003,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 6.4, votes: 279627, id: 181852 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8587124979496003,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9316166639328003
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Jumper',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2008,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 6.1, votes: 226607, id: 489099 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8534584903717041,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9346113204956055
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.9, votes: 94874, id: 216216 },
&nbsp;&nbsp;&nbsp;&nbsp;year: 2000,
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Mystery', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'The 6th Day',
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8465619003772735,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9320825338363647
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Horror', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Scanners II: The New Order',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1991,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.2, votes: 1889, id: 102848 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8295471119880676,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9327294826507568
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Horror', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Scanners II: The New Order',
&nbsp;&nbsp;&nbsp;&nbsp;year: 1991,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5.2, votes: 1884, id: 102848 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8295387524366379,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9327183365821838
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Stealth',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2005,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 5, votes: 43764, id: 382992 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8236240744590759,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9314987659454346
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Ra.One',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2011,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 4.8, votes: 29134, id: 1562871 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8185986828804016,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9314649105072021
&nbsp;&nbsp;},
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;genres: [ 'Action', 'Sci-Fi' ],
&nbsp;&nbsp;&nbsp;&nbsp;title: 'Ra.One',
&nbsp;&nbsp;&nbsp;&nbsp;year: 2011,
&nbsp;&nbsp;&nbsp;&nbsp;imdb: { rating: 4.8, votes: 29134, id: 1562871 },
&nbsp;&nbsp;&nbsp;&nbsp;hybrid: 0.8185513865947723,
&nbsp;&nbsp;&nbsp;&nbsp;score: 0.9314018487930298
&nbsp;&nbsp;}
]</pre>

In hybrid mode, *Guardians of the Galaxy* and *Edge of Tomorrow* appear at the top because they are both conceptually similar to *The Matrix* and have strong IMDb scores, whereas pure semantic search might rank less popular but slightly more semantically similar films higher.

🧠 Comparing pure semantic search vs. hybrid search

* Semantic search ranks only by meaning similarity.
* Hybrid search blends meaning with popularity, giving extra weight to high-rated titles.

📘 More tips like this

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 [MongoDB in Action: Building on the Atlas Data Platform](https://www.manning.com/books/mongodb-in-action-third-edition?utm_source=borucki&utm_medium=affiliate&utm_campaign=book_borucki&a_aid=borucki&a_bid=523e1217&chan=mm_flyinghighwithflutter). Published by [Manning Publications Co](https://www.linkedin.com/company/manning-publications-co/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BKzrYp9wkQfqYI%2FcNXQLuVQ%3D%3D).
