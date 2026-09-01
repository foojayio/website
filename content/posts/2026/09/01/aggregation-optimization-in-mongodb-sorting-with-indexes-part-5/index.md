---
title: "Aggregation Optimization in MongoDB: Sorting With Indexes (Part 5)"
date: "2026-09-01T14:07:36+00:00"
lastmod: "2026-09-01T14:07:37+00:00"
description: "Part 5 of our aggregation optimization series: swapping an explicit $sort for an index-backed sort cut MongoDB query time from 51ms to 14ms"
authors:
  - "graeme-robinson"
image: "fri1.png"
categories:
  - "Mongo"
related_posts:
  - "aggregation-optimization-in-mongodb-data-duplication-to-improve-read-performance-part-4"
  - "aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3"
  - "mongodb-as-a-vector-database-for-ai-agents-mongodb"
  - "what-is-sharding-in-mongodb-and-when-should-you-use-it"
frozen: false
---

*And why MongoDB might be a better relational database than you ever realized.*

[*Design reviews*](https://www.mongodb.com/events/mongodb-schema-design-reviews/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part5-foojay&utm_term=tony.kim)*are one-on-one meetings where MongoDB experts deliver advice on data modeling best practices and application design challenges. In this series, we are going to explore common real-life scenarios where design reviews helped developers achieve meaningful success with MongoDB.*

![](fri1-2.png)

In this series, we've described our steps to improve the performance of a slow running MongoDB [aggregation pipeline](https://www.mongodb.com/docs/manual/aggregation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part5-foojay&utm_term=tony.kim#aggregation-operations). The pipeline was part of a fictional video streaming service application, mapping user profiles to the devices those users were using to access the service, and was based on a real use case I'd encountered during a recent design review.

[In Part 1](https://foojay.io/today/aggregation-optimization-in-mongodb-a-case-study-from-the-field-part-1/), we broke down the initial pipeline design, based on what we'd encountered during the design review, and explained what each stage of the pipeline was designed to do. If you haven't read that yet or need a refresher on MongoDB aggregation pipelines, I'd suggest you refer back to it before continuing.

Following our first three rounds of improvements involving [removing unnecessary unwind stages](https://medium.com/mongodb/aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2-50bff2e1006c), using embedding to more efficiently [model the many-to-many relationship](https://medium.com/mongodb/aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3-fccdf603ddc8), and [using duplication to improve read performance](https://medium.com/mongodb/aggregation-optimization-in-mongodb-data-duplication-to-improve-read-performance-part-4-fb704cbaafef), we had successfully updated our query to beat our SLA target with a 230X increase in performance over the initial design.

|                                      |                        |                                                                  |
|--------------------------------------|------------------------|------------------------------------------------------------------|
| Pipeline description                 | Average time per query | Total elapsed time (300 query iterations, 15 concurrent threads) |
| Initial design                       | 11.8 seconds           | 260 seconds                                                      |
| $unwind removed                      | 4.7 seconds            | 105 seconds                                                      |
| Refactored many-to-many relationship | 2.9 seconds            | 62.5 seconds                                                     |
| Duplicated device names              | 51 milliseconds        | 1.2 seconds                                                      |

## Optimization Step 4: index-based sorts

The dramatic performance improvement realized by duplicating the device name information in the prior step meant the query performance was now well under the target one-second response time. However, there was one further quick and easy refinement we identified:

Each iteration of the pipeline design thus far had included a [$sort](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sort/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part5-foojay&utm_term=tony.kim#-sort--aggregation-) stage to sort matched documents by profileID:

```
{
  $sort: {
    profileID: 1
  }
}
```

This could be problematic as there could be many documents still being processed by the pipeline by the time the $sort stage was reached. For example, a search for all profiles in Austin using an iPhone12 to connect to the service resulted in 1,289 documents being passed into the $sort stage, all of which would need to be processed by MongoDB in-memory.

Generally speaking, sorting is CPU- and memory-intensive, and so $sort operations should be used with caution, especially when working with larger document sets. Whilst the response time for an individual sort operation run in isolation can often seem acceptable, the resources consumed whilst doing so at scale can often lead to capacity issues.

The recommended alternative to explicit $sort operations is to use the fact that indexes return documents ordered by key, and so if an index key includes the value by which we are trying to sort, documents will be returned already in the required order, negating the need for an explicit $sort operation.

In our scenario, we were able to utilize index-based sorting by adding profileID to the index defined on the profiles collection:

```
{"contact.address.city": 1, "devices.deviceName": 1, profileID: 1}
```

Note the order of the fields in the index definition was important. With this specification, entries in the index would be ordered by city first, then device name, then profileID. This meant that after identifying profiles with our target combination of city and device name, walking the index would return additional matches ordered by profileID. This is an example of using the [Equality, Sort, Range (ESR) rule](https://www.mongodb.com/docs/manual/tutorial/equality-sort-range-rule/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part5-foojay&utm_term=tony.kim#the-esr--equality--sort--range--rule) to determine optimal field ordering in compound index definitions. TLDR; fields on which we are doing an equality match should come before fields by which we are sorting in the index definition.

Updating the index definition in this way allowed us to eliminate the $sort stage from our pipeline. With this last change in place, our final pipeline definition looked like this:

```
[
  {
    $match: {
      "contact.address.city": "Austin",
      "deviceSNs.deviceName": "iPhone 12"
    }
  },
  {
    $skip: 0
  },
  {
    $limit: 10
  },
  {
    $lookup: {
      from: "Devices",
      localField: "deviceSNs.deviceSN",
      foreignField: "deviceSN",
      pipeline: [
        {
          $match: {
            deviceName: "iPhone 12"
          }
        },
        {
          $set: {
            _id: "$$REMOVE"
          }
        }
      ],
      as: "deviceData"
    }
  },
  {
    $set: {
      accountNum: "$$REMOVE",
      mappingData: "$$REMOVE",
      customerType: "$$REMOVE",
      DOB: "$$REMOVE",
      _id: "$$REMOVE"
    }
  }
]
```

Testing this pipeline version showed a further improvement in performance. Individual queries were now averaging under 15ms and the total time to complete 300 query iterations was 655 milliseconds:

|                                      |                        |                                                                  |
|--------------------------------------|------------------------|------------------------------------------------------------------|
| Pipeline description                 | Average time per query | Total elapsed time (300 query iterations, 15 concurrent threads) |
| Initial design                       | 11.8 seconds           | 260 seconds                                                      |
| $unwind removed                      | 4.7 seconds            | 105 seconds                                                      |
| Refactored many-to-many relationship | 2.9 seconds            | 62.5 seconds                                                     |
| Duplicated device names              | 51 milliseconds        | 1.2 seconds                                                      |
| **Index Sort**                       | **14 milliseconds**    | **655 milliseconds**                                             |

## Wrapping it up

We covered a lot of ground in this series, but the primary takeaway is that how you model and query data in MongoDB is as important as, if not more important than, it is in a traditional RDBMS. The key points we covered were:

* In aggregation pipelines, if you are using an $unwind stage simply to process all elements in an array, [there's often a more efficient way to do the same thing](https://medium.com/mongodb/aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2-50bff2e1006c).
* MongoDB documents natively support arrays and sub-documents, and this gives you options to model relationships in data in ways that [eliminate expensive lookup/join operations](https://medium.com/mongodb/aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3-fccdf603ddc8) and make RDBMS style workarounds like associative tables unnecessary.
* [Data duplication](https://medium.com/mongodb/aggregation-optimization-in-mongodb-data-duplication-to-improve-read-performance-part-4-fb704cbaafef), used appropriately, is not the evil many of us have been led to believe it is.
* Indexes are always crucial to query performance and scalability, but also, don't forget their role in sorting data.

Much of what we covered came down to an example of working with a many-to-many relationship. MongoDB is described by some people as non-relational, but the reality is that data always contains relationships, and the only thing that changes with MongoDB and the document data model is how we model those relationships. As we saw, it could very well be argued that MongoDB provides better options for modeling one-to-many and many-to-many relationships than traditional tabular databases. Does that make it a better relational database than an RDBMS? Let us know what you think in the comments.

If you are interested in learning more about optimizing aggregation pipelines, I highly recommend Paul Done's excellent book, available both as an [ebook](https://www.practical-mongodb-aggregations.com/front-cover.html) and in paperback. I consider this essential reading for anyone working with MongoDB.
