---
title: "Building an AI Semantic Movie Recommender with Vector Search"
slug: "building-an-ai-semantic-movie-recommender-with-vector-search"
date: "2025-09-30T13:23:18+00:00"
lastmod: "2025-09-30T13:23:38+00:00"
description: "Last time, we created a vector search index in a local MongoDB Atlas cluster. Now, let’s put it to work with a real case: building an AI-powered movie recommender that suggests films similar to The Matrix–without any shared keywords."
authors:
  - "arekborucki"
image: "Favicon-3-2.png"
categories:
  - "AI"
  - "Mongo"
tags:
related_posts:
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-rest-apis-in-java-with-spring-boot"
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
  - "data-modeling-for-java-developers-structuring-with-postgresql-and-mongodb"
frozen: false
---

Last time, we created a [vector search index](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D) in a [local MongoDB Atlas cluster](https://www.linkedin.com/pulse/run-local-atlas-cluster-minutes-locally-arek-borucki-mmiqf/?trackingId=ntYzEbTVSuauLVmp2Zbt4w%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D). Now, let's put it to work with a real case: building an AI-powered movie recommender that suggests films similar to *The Matrix*--without any shared keywords.

🕒 Reading time: 3-4 min

🎯 The challenge

This demo will be entirely based on the pre-generated vector embeddings already stored in the [sample_mflix](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim#std-label-sample-mflix) dataset---no API calls and no new model runs. Everything will remain local. If you haven't done it yet, learn how in this article 👉 [Loading embeddings into MongoDB](https://www.linkedin.com/pulse/from-zero-vector-hero-locally-arek-borucki-w5otf/?trackingId=xNlcCImhQCC0HsnlThlQFg%3D%3D&lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B1MUlV%2B0kQm%2BvelL3UScxcA%3D%3D)

🧠 *The Matrix* scenario

When you load the sample dataset to MongoDB, one of the movies you'll find in the [embedded_movies](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim#std-label-mflix-embedded_movies) collection is *The Matrix* . You can check it with the [find command](https://www.mongodb.com/docs/manual/reference/method/db.collection.find/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=tony.kim):

```
db.getSiblingDB("sample_mflix").embedded_movies.find({ title: "The Matrix" })
```

The document includes standard fields like title, plot, and genres, plus two vector embeddings:

* plot_embedding → 1536 dimensions from OpenAI's [text-embedding-ada-002](https://platform.openai.com/docs/models/text-embedding-ada-002)
* plot_embedding_voyage_3_large → 2048 dimensions from Voyage AI's [voyage-3-large](https://blog.voyageai.com/2025/01/07/voyage-3-large/)

```
{
  "title": "The Matrix",
  "year": 1999,
  "genres": ["Action", "Sci-Fi"],
  "rated": "R",
  "plot": "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
  "fullplot": "Thomas A. Anderson is a man living two lives...",
  "imdb": { "rating": 8.7, "votes": 1080566 },
  "metacritic": 73,
  "languages": ["English"],
  "writers": ["Andy Wachowski", "Lana Wachowski"],
  "directors": ["Andy Wachowski", "Lana Wachowski"],
  "cast": ["Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss", "Hugo Weaving"],
  "countries": ["USA", "Australia"],
  "runtime": 136,
  "released": "1999-03-31",
  "awards": "Won 4 Oscars. Another 33 wins & 40 nominations.",
  "poster": "https://m.media-amazon.com/images/M/...jpg",
  "plot_embedding": [-0.0065, -0.0334, -0.0149, -0.0390, -0.0114, 0.0089, -0.0314, -0.01881, -0.0534,-0.0734, -0.016608...],
  "plot_embedding_voyage_3_large": [-0.0376, 0.0339, -0.0164, -0.0154,-0.0134,-0.5164, -0.0371, -0.01881, -0.016608, 0.0920, 0.0474, ...]
}
```

These embeddings encode meaning, not just words. You can use them so MongoDB finds movies with a similar concept, even when plots share no obvious keywords.

For this tutorial, let's use *The Matrix* 's [plot_embedding](https://www.mongodb.com/docs/atlas/sample-data/sample-mflix/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#std-label-mflix-embedded_movies) as your query vector. Since this embedding is already stored in the document, you simply retrieve it and pass it to the [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#syntax) stage as a query parameter---no extra model calls required.

🔍 Validating the search index

First, check if a knnVector index exists (vector field type used for k-nearest neighbors search on high-dimensional numeric data), because [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant) only works on indexed vector fields. If the index is missing, create it 👉 [Creating Vector Search index](https://www.linkedin.com/article/edit/7355331187805978625/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3B9ndpuBouS2KE9PcuhU4W2A%3D%3D). You can also read [this article](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#std-label-avs-types-vector-search). Now, execute [getSearchIndexes](https://www.mongodb.com/docs/manual/reference/method/db.collection.getSearchIndexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant).

```
db.getSiblingDB("sample_mflix").embedded_movies.getSearchIndexes()
```

You should see something like:

```
[
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

Both indexes should be in READY status so you can run queries.

📦 Checking stored vectors

You'll be using the plot_embedding. First, confirm that the plot_embedding field stores its data as [BSON](https://en.wikipedia.org/wiki/BSON) Binary in Float32 format. This ensures it is ready for efficient vector search.

```
db.getSiblingDB("sample_mflix").embedded_movies.countDocuments({ plot_embedding: { $type: "binData" } })
```

Result:

```
3402
```

3402 means 3,402 documents in embedded_movies have a ready-to-use vector in BSON Binary format.

📏 Extracting the query vector

The $vectorSearch can search BSON Binary vector fields directly inside MongoDB because the index is built on that binary Float32 data.

However, when you run $vectorSearch from the MongoDB shell (mongosh) or from application code, you must pass the query vector as a plain JavaScript array of numbers---not as raw BSON binary.

MongoDB [stores embeddings in documents as BSON Binary](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant) (Float32) because it's compact and efficient for indexing. The vector search index uses this binary data internally without conversion. But the [queryVector](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#fields) parameter is an input to the search operation. It isn't read from the indexed data---it's sent from your code. This means you need to decode the BSON Binary into a standard JavaScript array before passing it to $vectorSearch.

As shown below, you fetch the plot_embedding BSON Binary for *The Matrix*, convert it to a Float32Array, and then convert that to a plain JavaScript array for $vectorSearch.

```
// Get The Matrix embedding from the document
const d = db.getSiblingDB("sample_mflix").embedded_movies.findOne(
  { title: "The Matrix" },
  { plot_embedding: 1, _id: 0 }
)

// Convert BSON Binary (Float32) -> Float32Array -> plain JS array
const qv = Array.from(d.plot_embedding.toFloat32Array())
```

Next, run:

```
qv.length

1536
```

qv.length returns 1536, confirming the correct dimension.

Now, you can use the qv variable as the queryVector parameter in a $vectorSearch stage to find documents whose embeddings are most similar in meaning to *The Matrix*.

🚀 Running semantic search

Pass qv into [$vectorSearch](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant#mongodb-pipeline-pipe.-vectorSearch) as the query vector---the reference point used to compare against all indexed vectors. The search engine uses cosine similarity to measure how close each stored embedding is to qv, ranking results from most to least similar. This returns movies that are conceptually close to *The Matrix*, even if they don't share obvious keywords.

```
db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
  {
    $vectorSearch: {
      index: "plot_embedding_index",
      path: "plot_embedding",
      queryVector: qv,
      numCandidates: 200,
      limit: 10
    }
  },
  {
    $match: { title: { $ne: "The Matrix" } }
  },
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
```

Expected output:

```
[
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'TRON',
    year: 1982,
    score: 0.9550351500511169
  },
  {
    genres: [ 'Action', 'Drama', 'Mystery' ],
    title: 'Arrambam',
    year: 2013,
    score: 0.9546242952346802
  },
  {
    year: 2001,
    genres: [ 'Action', 'Crime', 'Thriller' ],
    title: 'Swordfish',
    score: 0.9543327689170837
  },
  {
    year: 1995,
    genres: [ 'Action', 'Crime', 'Drama' ],
    title: 'The Net',
    score: 0.9502608180046082
  },
  {
    genres: [ 'Action', 'Drama' ],
    title: 'Tuff Turf',
    year: 1985,
    score: 0.9378551244735718
  },
  {
    year: 2015,
    genres: [ 'Action', 'Comedy', 'Crime' ],
    title: 'Spy',
    score: 0.9367037415504456
  },
  {
    genres: [ 'Action', 'Sci-Fi' ],
    title: 'V: The Final Battle',
    year: 1984,
    score: 0.9352985620498657
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Jumper',
    year: 2008,
    score: 0.9346113204956055
  },
  {
    year: 2014,
    genres: [ 'Action', 'Adventure', 'Comedy' ],
    title: 'Kingsman: The Secret Service',
    score: 0.9341350793838501
  }
]
```

Here, *TRON* , *Swordfish* , and *The Net* rank high, thematically similar to *The Matrix* even without matching keywords.

⚖ Running hybrid search (vector + IMDb rating)

Instead of ranking results purely by semantic similarity, [you can combine meaning-based search with a quality signal](https://www.mongodb.com/resources/products/capabilities/hybrid-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=semantic-foojay&utm_term=megan.grant)---in this case, IMDb ratings. By weighting semantic similarity at 75% and IMDb rating at 25%, you still get matches that are close in meaning to *The Matrix*, but movies that are also popular and well-reviewed will rank higher.

This hybrid approach is useful when you want search results that are both relevant in meaning and favored by audiences or critics.

Below, the pipeline first performs a vector search to get semantically similar movies, filters by genre/year, then adds a hybrid score combining the vector score with the normalized IMDb rating, sorts by this hybrid score, and returns the top results:

```
db.getSiblingDB("sample_mflix").embedded_movies.aggregate([
  {
    $vectorSearch: {
      index: "plot_embedding_index",
      path: "plot_embedding",
      queryVector: qv,
      numCandidates: 1500,
      limit: 50
    }
  },
  { $match: { genres: "Sci-Fi", year: { $gte: 1990 }, title: { $ne: "The Matrix" } } },
  {
    $addFields: {
      hybrid: {
        $add: [
          { $multiply: [ { $meta: "vectorSearchScore" }, 0.75 ] },
          { $multiply: [ { $divide: ["$imdb.rating", 10] }, 0.25 ] }
        ]
      }
    }
  },
  { $sort: { hybrid: -1 } },
  { $limit: 20 },
  {
    $project: {
      title: 1,
      year: 1,
      genres: 1,
      score: { $meta: "vectorSearchScore" },
      imdb: 1,
      hybrid: 1,
      _id: 0
    }
  }
])
```

Expected output:

```
[
  {
    imdb: { rating: 8.1, votes: 548314, id: 2015381 },
    year: 2014,
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Guardians of the Galaxy',
    hybrid: 0.89929194688797,
    score: 0.92905592918396
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Guardians of the Galaxy',
    year: 2014,
    imdb: { rating: 8.1, votes: 539583, id: 2015381 },
    hybrid: 0.8992636048793793,
    score: 0.9290181398391724
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Edge of Tomorrow',
    year: 2014,
    imdb: { rating: 7.9, votes: 357609, id: 1631867 },
    hybrid: 0.8965482211112976,
    score: 0.9320642948150635
  },
  {
    genres: [ 'Animation', 'Action', 'Sci-Fi' ],
    title: 'Ghost in the Shell Arise: Border 1 - Ghost Pain',
    year: 2013,
    imdb: { rating: 7.3, votes: 1537, id: 2636124 },
    hybrid: 0.8788538080453873,
    score: 0.9284717440605164
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Transformers',
    year: 2007,
    imdb: { rating: 7.1, votes: 479049, id: 418279 },
    hybrid: 0.8741268122196197,
    score: 0.9288357496261597
  },
  {
    genres: [ 'Action', 'Sci-Fi' ],
    title: 'Terminator 3: Rise of the Machines',
    year: 2003,
    imdb: { rating: 6.4, votes: 279627, id: 181852 },
    hybrid: 0.8587124979496003,
    score: 0.9316166639328003
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Jumper',
    year: 2008,
    imdb: { rating: 6.1, votes: 226607, id: 489099 },
    hybrid: 0.8534584903717041,
    score: 0.9346113204956055
  },
  {
    imdb: { rating: 5.9, votes: 94874, id: 216216 },
    year: 2000,
    genres: [ 'Action', 'Mystery', 'Sci-Fi' ],
    title: 'The 6th Day',
    hybrid: 0.8465619003772735,
    score: 0.9320825338363647
  },
  {
    genres: [ 'Action', 'Horror', 'Sci-Fi' ],
    title: 'Scanners II: The New Order',
    year: 1991,
    imdb: { rating: 5.2, votes: 1889, id: 102848 },
    hybrid: 0.8295471119880676,
    score: 0.9327294826507568
  },
  {
    genres: [ 'Action', 'Horror', 'Sci-Fi' ],
    title: 'Scanners II: The New Order',
    year: 1991,
    imdb: { rating: 5.2, votes: 1884, id: 102848 },
    hybrid: 0.8295387524366379,
    score: 0.9327183365821838
  },
  {
    genres: [ 'Action', 'Adventure', 'Sci-Fi' ],
    title: 'Stealth',
    year: 2005,
    imdb: { rating: 5, votes: 43764, id: 382992 },
    hybrid: 0.8236240744590759,
    score: 0.9314987659454346
  },
  {
    genres: [ 'Action', 'Sci-Fi' ],
    title: 'Ra.One',
    year: 2011,
    imdb: { rating: 4.8, votes: 29134, id: 1562871 },
    hybrid: 0.8185986828804016,
    score: 0.9314649105072021
  },
  {
    genres: [ 'Action', 'Sci-Fi' ],
    title: 'Ra.One',
    year: 2011,
    imdb: { rating: 4.8, votes: 29134, id: 1562871 },
    hybrid: 0.8185513865947723,
    score: 0.9314018487930298
  }
]
```

In hybrid mode, *Guardians of the Galaxy* and *Edge of Tomorrow* appear at the top because they are both conceptually similar to *The Matrix* and have strong IMDb scores, whereas pure semantic search might rank less popular but slightly more semantically similar films higher.

🧠 Comparing pure semantic search vs. hybrid search

* Semantic search ranks only by meaning similarity.
* Hybrid search blends meaning with popularity, giving extra weight to high-rated titles.

📘 More tips like this

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 [MongoDB in Action: Building on the Atlas Data Platform](https://www.manning.com/books/mongodb-in-action-third-edition?utm_source=borucki&utm_medium=affiliate&utm_campaign=book_borucki&a_aid=borucki&a_bid=523e1217&chan=mm_flyinghighwithflutter). Published by [Manning Publications Co](https://www.linkedin.com/company/manning-publications-co/?lipi=urn%3Ali%3Apage%3Ad_flagship3_pulse_read%3BKzrYp9wkQfqYI%2FcNXQLuVQ%3D%3D).
