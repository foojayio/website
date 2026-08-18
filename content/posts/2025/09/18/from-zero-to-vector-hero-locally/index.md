---
title: "From Zero to Vector Hero - Locally! (Vector Search)"
date: "2025-09-18T15:25:23+00:00"
lastmod: "2025-09-18T15:25:24+00:00"
description: "After launching your local MongoDB Atlas cluster and running the show dbs command in mongosh, you’ll see only the default system databases: admin, config, and local. These are used internally by MongoDB and contain no user data or vector embeddings at this point. To understand how embeddings come into play, take a look at the diagram below. It illustrates how they are generated and stored in MongoDB together with application data."
authors:
  - "arekborucki"
image: "Screenshot-2025-09-18-at-10.22.30-AM.png"
categories:
  - "Databases"
  - "Mongo"
related_posts:
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-rest-apis-in-java-with-spring-boot"
  - "data-modeling-for-java-developers-structuring-with-postgresql-and-mongodb"
  - "enforcing-governance-in-mongodb-atlas-with-resource-policies"
frozen: false
---

In the previous issue, I explained how to run a local [MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=vector-search-3&utm_term=tony.kim) Atlas cluster using Atlas CLI - no cloud account required. If you missed it, read it here 👉 Run an Atlas cluster locally in minutes. Now let's see how to use Vector Search in that local environment.

🕒 Reading time: 3-4 min

🧠 Explaining the Embedding Workflow

After launching your local MongoDB Atlas cluster and running the show dbs command in mongosh, you'll see only the default system databases: admin, config, and local. These are used internally by MongoDB and contain no user data or vector embeddings at this point.

To understand how embeddings come into play, take a look at the diagram below. It illustrates how they are generated and stored in MongoDB together with application data.  
![Image by MongoDB 2024. Process of generating embeddings from data and using them for similarity search.](Screenshot-2025-09-18-at-10.17.33-AM.png)

Raw data is processed by an embedding model, which produces a high-dimensional vector. This vector is stored in a MongoDB collection and used to support semantic queries via the $vectorSearch aggregation pipeline operator.

You can generate embeddings using a model such as OpenAI or Voyage AI (🔁 read 👉 [How to Create Vector Embeddings](https://www.mongodb.com/docs/atlas/atlas-vector-search/create-embeddings/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=vector-search-3&utm_term=megan.grant)), and store them along with any relevant metadata.

👉 If you want to understand better how Vector Search works in MongoDB, check out this article 👉 Power your AI application with Vector Search

🔢 Loading embeddings into MongoDB

Alternatively, you can load a sample MongoDB dataset that already contains pre-generated vector embeddings using mongorestore. First, make sure [MongoDB Database Tools are installed](https://www.mongodb.com/docs/database-tools/installation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=vector-search-3&utm_term=megan.grant). Then, download the sample dataset with curl, as shown in the example below:

```
curl https://atlas-education.s3.amazonaws.com/sampledata.archive -o sampledata.archive
```

Find the connection string for your local MongoDB Atlas cluster with:

```
atlas deployments connect --connectWith connectionString
```

You will get a connection string similar to "mongodb://localhost:55015/?directConnection=true". Then, load the sample dataset using mongorestore and the connection string:

```
mongorestore --archive=sampledata.archive --uri 
"mongodb://localhost:55015/?directConnection=true"
```

After reconnecting to your local Atlas cluster, run show dbs to confirm that the new sample_mflix database has been added. It includes the embedded_movies collection with pre-generated vector embeddings from the MongoDB sample dataset.

🔎 Finding embeddings

To retrieve a document from the embedded_movies collection within this database, run the following command:

```
db.getSiblingDB("sample_mflix").embedded_movies.findOne()
```

This command queries the sample_mflix.embedded_movies namespace and returns a single document containing standard movie metadata such as title, cast, genres, and release date. It also includes one or more vector embeddings of the plot field, which are stored as Float32Array binaries. Here is a simplified example of the returned document:

```
{
  "_id": ObjectId("573a1392f29313caabcd9ca6"),
  "title": "Scarface",
  "plot": "An ambitious and near insanely violent gangster climbs the ladder of success...",
"plot_embedding": Binary.fromFloat32Array(new Float32Array([
 -0.0155, -0.0342, 0.0152, -0.0426, -0.0208, 0.0263,
 // ... 1436 more values ...
  ])),
 "plot_embedding_voyage_3_large": Binary.fromFloat32Array(new Float32Array([
 -0.0300, 0.0311, -0.0156, -0.0366, 0.0248, 0.0085,
    // ... 1948 more values ...
  ]))
}
```

The example includes two different embeddings of the same plot: plot_embedding contains 1536-dimensional vectors generated using OpenAI's text-embedding-ada-002 model, while plot_embedding_voyage_3_large contains 2048-dimensional vectors from Voyage AI's voyage-3-large model.

These vectors enable semantic comparison. For example, they allow you to find movies with similar narrative content, tone, or themes, even if the descriptions don't share the same words.

Now you just need to create a vector index on the embedding field, and you'll be ready to perform semantic search. This is required for the $vectorSearch aggregation stage to work efficiently.

🛠️ Creating Vector Search index

Use the createSearchIndex command to define a vector index on the plot_embedding_voyage_3_large field. This enables fast similarity search over 2048-dimensional vectors.

```
db.getSiblingDB("sample_mflix").embedded_movies.createSearchIndex({
  name: "plot_embedding_voyage_index",
  definition: {
   mappings: {
     dynamic: false,
       fields: {
          plot_embedding_voyage_3_large: {
          type: "knnVector",
          dimensions: 2048,
          similarity: "cosine"
        }
      }
    }
  }
})
```

The plot_embedding_voyage_3_large field is indexed as a knnVector, a specialized vector field designed for storing high-dimensional numeric data. Cosine means the similarity between vectors is based on the angle between them; the smaller the angle, the higher the similarity, regardless of their magnitude.

To confirm the index exists, run:

```
db.getSiblingDB("sample_mflix").embedded_movies.getSearchIndexes()
```

You're now ready to run similarity queries against this field using the $vectorSearch operator.

★ The query must include a vector input with exactly 2048 float values to match the index dimensions. This vector must also be generated by the same embedding model used to produce the stored vectors, ensuring that semantic meaning is comparable. This allows MongoDB to compare the input vector with indexed vectors using cosine similarity.

📙 What's Next

In the next episode, you'll learn how to run similarity queries using the $vectorSearch operator. We'll use the vector index you just created to search for documents with similar plot embeddings in your local Atlas environment.

📘 More tips like this

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 [MongoDB in Action: Building on the Atlas Data Platform](https://www.manning.com/books/mongodb-in-action-third-edition?utm_source=borucki&utm_medium=affiliate&utm_campaign=book_borucki&a_aid=borucki&a_bid=523e1217&chan=mm_flyinghighwithflutter). Published by Manning Publications Co.
