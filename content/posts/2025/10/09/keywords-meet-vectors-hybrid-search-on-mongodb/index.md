---
title: "Keywords Meet Vectors: Hybrid Search on MongoDB"
date: "2025-10-09T14:49:49+00:00"
lastmod: "2025-10-09T14:49:51+00:00"
description: "Hybrid search in MongoDB brings together two complementary search techniques:Full text search (BM25 via Atlas Search)—optimized for exact keyword matches, powered by Lucene inside mongot. Perfect when users expect documents that literally contain their query terms.Vector search (kNN via Atlas Vector Search)—optimized for semantic similarity. It uses dense embeddings from ML models to find conceptually related content, even when no keywords match.On their own, each method has advantages and limitations. Text search misses context (“non-linear crime story” won’t return Memento). Pure semantic search may return results that are semantically aligned but sometimes not practically useful. Hybrid search combines the strengths of both, ensuring results are contextually relevant and precise."
authors:
  - "arekborucki"
image: "Favicon-3-2.png"
categories:
  - "AI"
  - "Databases"
  - "Mongo"
related_posts:
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-an-ai-semantic-movie-recommender-with-vector-search"
frozen: false
---

In the previous issues, I explained how to run a local [MongoDB](https://www.linkedin.com/company/mongodbinc/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BnV1cKI74RDKmiAI4lr1TaA%3D%3D) Atlas cluster using [Atlas CLI](https://www.linkedin.com/pulse/run-local-atlas-cluster-minutes-locally-arek-borucki-mmiqf/?trackingId=M71jHZpGSQCdFygE5EgAOw%3D%3D), what [vector search](https://www.linkedin.com/pulse/power-your-ai-application-vector-search-arek-borucki-sjw0f/?trackingId=vjRobV3lSdy1nc4SIgjM%2Fg%3D%3D) is, and [how to use it](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=S%2FClUOdjSMGzvkR2ZLIS9Q%3D%3D). Now, let's take a closer look at hybrid search.

Reading time: 4--5 min

## What is hybrid search?

Hybrid search in MongoDB brings together **two complementary search techniques**:

* **Full text search** ([BM25](https://en.wikipedia.org/wiki/Okapi_BM25) via [Atlas Search](https://www.mongodb.com/docs/atlas/atlas-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant))---optimized for exact keyword matches, powered by Lucene inside mongot. Perfect when users expect documents that literally contain their query terms.

<!-- -->

* **Vector search** ([kNN](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm) via [Atlas Vector Search](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-overview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant))---optimized for semantic similarity. It uses dense embeddings from ML models to find conceptually related content, even when no keywords match.

On their own, each method has advantages and limitations. Text search misses context ("non-linear crime story" won't return Memento). Pure semantic search may return results that are semantically aligned but sometimes not practically useful. Hybrid search combines the strengths of both, ensuring results are contextually relevant and precise.

## How does it work in MongoDB?

MongoDB Atlas (and soon MongoDB Community Edition and Enterprise Advanced) handles both layers natively:

* **mongot** runs [BM25](https://python.langchain.com/docs/integrations/retrievers/bm25/) (text search) and [kNN](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm) (vector search) on the same dataset.

<!-- -->

* Results are merged using ranking strategies such as **Reciprocal Rank Fusion** ([RRF](https://medium.com/@devalshah1619/mathematical-intuition-behind-reciprocal-rank-fusion-rrf-explained-in-2-mins-002df0cc5e2a)) or **Relative Score Fusion** ([RSF](https://docsbot.ai/article/enhanced-rag-search-with-the-relativescorefusion-algorithm)).

<!-- -->

* You can also filter or rerank using metadata (e.g., year, rating, genre) with the aggregation pipeline.

This means you don't need multiple databases (like MongoDB + Elastic + Pinecone). Both your operational data and embeddings stay in MongoDB.

### Why movies collection are the perfect demo

Movie data (from the MongoDB [sample dataset](https://www.mongodb.com/docs/atlas/sample-data/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#available-sample-datasets)) contains both **structured fields** (title, year, genres, ratings) and **unstructured text** (plots). That makes the [sample_mflix.embedded_movies](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies) dataset an ideal sandbox: You can run keyword search, vector search, or combine them into hybrid pipelines. You can read more about it in 👉 [this article](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D). Let's walk through an example using *Inception* (2010) as the anchor.

### Inspecting the anchor document

You first need to confirm that the Inception document exists in the dataset and that embeddings are present. This provides metadata for context and the vector that drives semantic similarity.

```
db.getSiblingDB("sample_mflix").embedded_movies.find({ title: "Inception" },{ title: 1, year: 1, genres: 1, imdb: 1, plot: 1, plot_embedding: 1})
```

The query should return:

```
{
  "title": "Inception",
  "year": 2010,
  "genres": ['Action', 'Mystery', 'Sci-Fi'],
  "imdb": { rating: 8.8, votes: 1294646, id: 1375666 },
  "plot": "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
  "plot_embedding": <Binary Data, 1536 dimensions>
}
```

The presence of **plot_embedding** confirms this document can serve as a query vector.

### Sanity checks

You need a [vector index](https://www.mongodb.com/docs/manual/reference/command/createsearchindexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant) on [plot_embedding](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies), and you need embeddings stored in BSON Binary (Float32). If the index does not exist, create it (check this article 👉 [how](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D)). Otherwise, verify it's READY.

```
// Check that the vector search index is available
db.getSiblingDB("sample_mflix").embedded_movies.getSearchIndexes()
```

Expected output:

```
{
    id: '68983b85c2c844543026fa6a',
    name: 'plot_embedding_index',
    type: 'search',
    status: 'READY',
    queryable: true,
    latestVersion: 0,
    latestDefinition: {
      mappings: {
        dynamic: false,
        fields: {
          plot_embedding: { type: 'knnVector', dimensions: 1536, similarity: 'cosine' }
        }
      }
    }
  }
]
```

### Step 1: Prepare the query vector

MongoDB stores embeddings compactly as BSON Binary (Float32) for storage and indexing efficiency, while [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=hybrid-search-foojay&utm_term=megan.grant#mongodb-pipeline-pipe.-vectorSearch) expects the queryVector as a plain [JavaScript array](https://www.w3schools.com/js/js_arrays.asp). You need to extract and convert it at query time.

```
const d = db.getSiblingDB("sample_mflix").embedded_movies.findOne(
  { title: "Inception" },
  { plot_embedding: 1, _id: 0 }
)

const qv = Array.from(d.plot_embedding.toFloat32Array())
```

Here, **qv** becomes a 1,536-element JavaScript array representing the semantic meaning of Inception.

### Step 2: Run semantic search

With the query vector ready (**qv** ), you search for movies whose **plots are conceptually similar** to *Inception---*for example, titles involving dream manipulation, layered realities, high-stakes heists, or unreliable perception. This step ignores exact keywords and measures conceptual closeness.

```
db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
  {
    $vectorSearch: {
      index: "plot_embedding_index",
      path: "plot_embedding",
      queryVector: qv,
      numCandidates: 200,
      limit: 5
    }
  },
  { $match: { title: { $ne: "Inception" } } },
  {
    $project: {
      title: 1,
      year: 1,
      genres: 1,
      score: { $meta: "vectorSearchScore" },
      _id: 0
    }
  }
])
[
  {
    year: 2001,
    genres: [ 'Action', 'Crime', 'Thriller' ],
    title: 'Swordfish',
    score: 0.931791365146637
  },
  {
    genres: [ 'Fantasy', 'Sci-Fi' ],
    title: 'The City of Lost Children',
    year: 1995,
    score: 0.9285156726837158
  },
  {
    year: 2013,
    genres: [ 'Action', 'Crime', 'Thriller' ],
    title: 'Parker',
    score: 0.9258596897125244
  },
  {
    year: 1999,
    genres: [ 'Action', 'Adventure', 'Comedy' ],
    title: 'Inspector Gadget',
    score: 0.9235274791717529
  }
]
```

Semantic search finds thematically close titles, but ranking does not yet reflect quality.

### Step 3: Apply hybrid scoring

You need to combine semantic similarity with IMDb ratings to boost well-reviewed titles. This ensures results are not only close in meaning but also valued by audiences.

```
db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
  {
    $vectorSearch: {
      index: "plot_embedding_index",
      path: "plot_embedding",
      queryVector: qv,
      numCandidates: 1500,
      limit: 100
    }
  },
  { $match: { year: { $gte: 1990 }, title: { $ne: "Inception" } } },
  {
    $addFields: {
      hybridScore: {
        $add: [
          { $multiply: [ { $meta: "vectorSearchScore" }, 0.7 ] },
          { $multiply: [ { $divide: ["$imdb.rating", 10] }, 0.3 ] }
        ]
      }
    }
  },
  { $sort: { hybridScore: -1 } },
  { $limit: 5 },
  {
    $project: {
      title: 1,
      year: 1,
      imdb: 1,
      hybridScore: 1,
      score: { $meta: "vectorSearchScore" },
      _id: 0
    }
  }
])
```

Example results:

```
[
  {
    imdb: { rating: 8.7, votes: 1080566, id: 133093 },
    year: 1999,
    title: 'The Matrix',
    hybridScore: 0.9070646867752075,
    score: 0.9229495525360107
  },
  {
    title: 'Athadu',
    year: 2005,
    imdb: { rating: 8.4, votes: 4569, id: 471571 },
    hybridScore: 0.8886410732269286,
    score: 0.909487247467041
  },
  {
    title: 'The City of Lost Children',
    year: 1995,
    imdb: { rating: 7.7, votes: 52784, id: 112682 },
    hybridScore: 0.880960970878601,
    score: 0.9285156726837158
  },
  {
    title: 'Room 8',
    year: 2013,
    imdb: { rating: 8, votes: 762, id: 2949338 },
    hybridScore: 0.8803098821640014,
    score: 0.9147284030914307
  },
  {
    imdb: { rating: 7.8, votes: 271917, id: 2802144 },
    year: 2014,
    title: 'Kingsman: The Secret Service',
    hybridScore: 0.8751985874176025,
    score: 0.9159979820251465
  }
]
```

This hybrid scoring surfaces titles that are both semantically similar and widely acclaimed.

### Step 4: Hybrid ranking with Reciprocal Rank Fusion (RRF)

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

```
db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
  // A) TEXT (BM25) — compute rank and RRF contribution in this leg
  {
    $search: {
      index: "hybrid_text",
      text: { query: "computer hacker", path: ["title","plot","fullplot"] }
    }
  },
  { $set: { score: { $meta: "searchScore" } } },
  { $setWindowFields: { sortBy: { score: -1 }, output: { textRank: { $documentNumber: {} } } } },
  { $set: { rrf: { $divide: [1, { $add: [60, "$textRank"] }] } } }, // RRF piece for the text leg
  { $project: { title:1, year:1, genres:1, imdb:1, textRank:1, rrf:1 } },

  // B) VECTOR (kNN) — do the same for the vector leg
  {
    $unionWith: {
      coll: "embedded_movies",
      pipeline: [
        {
          $vectorSearch: {
            index: "plot_embedding_index",
            path: "plot_embedding",
            queryVector: qv,        // Inception embedding array
            numCandidates: 1000,
            limit: 300
          }
        },
        { $set: { score: { $meta: "vectorSearchScore" } } },
        { $setWindowFields: { sortBy: { score: -1 }, output: { vectorRank: { $documentNumber: {} } } } },
        { $set: { rrf: { $divide: [1, { $add: [60, "$vectorRank"] }] } } }, // RRF piece for the vector leg
        { $project: { title:1, year:1, genres:1, imdb:1, vectorRank:1, rrf:1 } }
      ]
    }
  },

  // C) Fusion — sum RRF contributions; carry ranks from each leg if present
  {
    $group: {
      _id: "$_id",
      title:  { $first: "$title" },
      year:   { $first: "$year" },
      genres: { $first: "$genres" },
      imdb:   { $first: "$imdb" },
      textRank:   { $max: { $ifNull: ["$textRank", 0] } },
      vectorRank: { $max: { $ifNull: ["$vectorRank", 0] } },
      rrf:        { $sum: "$rrf" }
    }
  },
  // (optional) keep only overlap (items present in BOTH legs)
  // { $match: { textRank: { $gt: 0 }, vectorRank: { $gt: 0 } } },
  // (optional) exclude the anchor movie if it shows up
  { $match: { title: { $ne: "Inception" } } },
  { $sort: { rrf: -1 } },
  { $limit: 10 },
  { $project: { _id:0, title:1, year:1, genres:1, imdb:1, rrf:1, textRank:1, vectorRank:1 } }
])
```

Example result:

```
[
  {
    title: 'The Matrix',
    year: 1999,
    genres: [ 'Action', 'Sci-Fi' ],
    imdb: { rating: 8.7, votes: 1080566, id: 133093 },
    textRank: 1,
    vectorRank: 7,
    rrf: 0.03131881575727918
  },
  {
    title: 'TRON',
    year: 1982,
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    imdb: { rating: 6.8, votes: 88860, id: 84827 },
    textRank: 2,
    vectorRank: 6,
    rrf: 0.03128054740957967
  },
  {
    title: 'Swordfish',
    year: 2001,
    genres: [ 'Action', 'Crime', 'Thriller' ],
    imdb: { rating: 6.5, votes: 148103, id: 244244 },
    textRank: 14,
    vectorRank: 2,
    rrf: 0.02964254577157803
  },
  {
    title: 'The Net',
    year: 1995,
    genres: [ 'Action', 'Crime', 'Drama' ],
    imdb: { rating: 5.8, votes: 45996, id: 113957 },
    textRank: 11,
    vectorRank: 30,
    rrf: 0.025195618153364633
  },
  {
    title: 'Arrambam',
    year: 2013,
    genres: [ 'Action', 'Drama', 'Mystery' ],
    imdb: { rating: 7.5, votes: 5957, id: 2555958 },
    textRank: 3,
    vectorRank: 54,
    rrf: 0.024644945697577275
  },
  {
    title: 'Blackhat',
    year: 2015,
    genres: [ 'Action', 'Crime', 'Drama' ],
    imdb: { rating: 5.4, votes: 27798, id: 2717822 },
    textRank: 10,
    vectorRank: 42,
    rrf: 0.024089635854341734
  },
  {
    title: 'Speed 2: Cruise Control',
    year: 1997,
    genres: [ 'Action', 'Crime', 'Romance' ],
    imdb: { rating: 3.7, votes: 57010, id: 120179 },
    textRank: 6,
    vectorRank: 60,
    rrf: 0.023484848484848483
  },
  {
    title: 'Sivaji',
    year: 2007,
    genres: [ 'Action', 'Adventure', 'Drama' ],
    imdb: { rating: 7.4, votes: 7920, id: 479751 },
    textRank: 23,
    vectorRank: 29,
    rrf: 0.023284147827264113
  },
  {
    title: 'Open Windows',
    year: 2014,
    genres: [ 'Action', 'Crime', 'Thriller' ],
    imdb: { rating: 5.2, votes: 8894, id: 2409818 },
    textRank: 26,
    vectorRank: 39,
    rrf: 0.02172891707775429
  },
  {
    title: 'Nicotina',
    year: 2003,
    genres: [ 'Action', 'Comedy', 'Crime' ],
    imdb: { rating: 6.7, votes: 2969, id: 337930 },
    textRank: 7,
    vectorRank: 94,
    rrf: 0.021418879627834852
  }
]
```

This yields **one ranked list** that balances **keyword intent** (textRank) and **semantic meaning** (vectorRank). Items that rank well in both legs rise to the top; items strong in only one leg still get credit but are ranked lower.

## Wrap-up

* **Hybrid search = BM25 + k-NN** . In MongoDB, you combine **Atlas Search** (Lucene/BM25 in mongot) with **Atlas Vector Search** (k-NN over knnVector fields) to capture both exact intent and semantic similarity.

<!-- -->

* **Run semantic retrieval** . $vectorSearch surfaces titles close in meaning to *Inception* (dreams, layered realities, heists), regardless of shared keywords.

<!-- -->

* **Add business signals**. Re-rank with a hybridScore that blends the vector similarity with a normalized IMDb rating so results reflect both theme and quality.

<!-- -->

* **Fuse text + vectors with RRF** . When knnBeta can't be combined with a compound in a single $search, run two legs---$search.text and $vectorSearch---rank each leg, then fuse with **RRF**. Items with non-zero textRank and vectorRank (1 = best per leg) are promoted, yielding one balanced list.

<!-- -->

* **Keep it in MongoDB**. Indexing, vectors, filters, and ranking all live in one place---no cross-system syncing.

### More tips like this

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 [MongoDB in Action: Building on the Atlas Data Platform](https://www.manning.com/books/mongodb-in-action-third-edition?utm_source=borucki&utm_medium=affiliate&utm_campaign=book_borucki&a_aid=borucki&a_bid=523e1217&chan=mm_flyinghighwithflutter). Published by [Manning Publications Co](https://www.linkedin.com/company/manning-publications-co/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BnV1cKI74RDKmiAI4lr1TaA%3D%3D).
