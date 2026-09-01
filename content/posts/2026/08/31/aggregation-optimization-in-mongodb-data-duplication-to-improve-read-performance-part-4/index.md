---
title: "Aggregation Optimization in MongoDB: Data Duplication to Improve Read Performance (Part 4)"
date: "2026-08-31T14:45:04+00:00"
lastmod: "2026-08-31T14:45:05+00:00"
description: "And why MongoDB might be a better relational database than you ever realized. Design reviews are one-on-one meetings where MongoDB experts deliver advice…"
authors:
  - "graeme-robinson"
image: "fri1-2.png"
categories:
  - "Mongo"
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "aggregation-optimization-in-mongodb-a-case-study-from-the-field-part-1"
  - "aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3"
frozen: false
---

*And why MongoDB might be a better relational database than you ever realized.*

[*Design reviews*](https://www.mongodb.com/events/mongodb-schema-design-reviews/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim)*are one-on-one meetings where MongoDB experts deliver advice on data modeling best practices and application design challenges. In this series, we are going to explore common real-life scenarios where design reviews helped developers achieve meaningful success with MongoDB.*

![](fri1-2.png)

In this series, we've described our steps to improve the performance of a slow running MongoDB [aggregation pipeline](https://www.mongodb.com/docs/manual/aggregation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#aggregation-operations). The pipeline was part of a fictional video streaming service application, mapping user profiles to the devices those users were using to access the service, and was based on a real use case I'd encountered during a recent design review.

[In Part 1](https://foojay.io/today/aggregation-optimization-in-mongodb-a-case-study-from-the-field-part-1/), we broke down the initial pipeline design, based on what we'd encountered during the design review, and explained what each stage of the pipeline was designed to do. If you haven't read that yet or need a refresher on MongoDB aggregation pipelines, I'd suggest you refer back to that before continuing.

Following our first two rounds of improvements involving [removing unnecessary unwind stages](https://medium.com/mongodb/aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2-50bff2e1006c) and [using embedding to more efficiently model the many-to-many relationship](https://medium.com/mongodb/aggregation-optimization-in-mongodb-optimizing-many-to-many-relationships-part-3-fccdf603ddc8), we had seen a 75% improvement in performance. However, we were still well short of our target sub-one second query response.

|                                      |                        |                                                                  |
|--------------------------------------|------------------------|------------------------------------------------------------------|
| Pipeline Description                 | Average time per query | Total elapsed time (300 query iterations, 15 concurrent threads) |
| Initial design                       | 11.8 seconds           | 260 seconds                                                      |
| $unwind removed                      | 4.7 seconds            | 105 seconds                                                      |
| Refactored many-to-many relationship | 2.9 secnds             | 62.5 seconds                                                     |

## Optimization, Step 3: duplicating device name information

Looking at the overall structure of the query pipeline, one of the most striking aspects was that the initial [$match](https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#-match--aggregation-) stage, filtering profiles by city, was returning a result set of thousands of profiles. As an example, selecting "Austin" as the city returned a set of 6,763 documents. From this starting point, the pipeline would eventually reduce this to just 10 documents, which meant the overwhelming majority of documents were being processed just to be subsequently discarded.  

Looking at how we could reduce the number of documents being processed unnecessarily, the first idea was to duplicate device name information from device documents into the relevant corresponding profile documents. By doing this, the initial match operation could now be updated to identify only those profiles that met both of our search criteria—city *and* at least one device of the target type used to connect to the streaming service. This would greatly reduce the number of documents being processed unnecessarily.

To facilitate this, the array of device serial number references we had added as part of our prior optimization step was now updated to also include the device name:

```
{
  "_id": {...},
  "DOB": "0001-01-01T00:00:00Z",
  "SSN": "943-83-1203",
  "accountNum": "G173VDREI5",
  "contact": {...},
  "customerType": "S",
  "devices": [
    {
      "deviceSN": "59737620-3d81-4c13-a161-b3c4e045cfe3",
      "deviceName": "Panasonic TV"
    },
    {
      "deviceSN": "24652cac-27a7-4961-8d11-1d77a55a1774",
      "deviceName": "Sceptre TV"
    },
    {
      "deviceSN": "6ea648d9-dc75-4909-82e2-0955fffb7a94",
      "deviceName": "iPhone 12"
    }
  ],
  "firstName": "Martin",
  "lastName": "Smith",
  "profileID": "G173VDREI5-1"
}
```

Next, the index on city in the profiles collection was replaced with a compound index on both city and the new deviceName field:

```
{"contact.address.city": 1, "devices.deviceName": 1}
```

Finally, the initial $match stage was updated to search on both city *and* device name, and the [$sort](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sort/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#-sort--aggregation-), [$skip](https://www.mongodb.com/docs/manual/reference/operator/aggregation/skip/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#-skip--aggregation-), and [$limit](https://www.mongodb.com/docs/manual/reference/operator/aggregation/limit/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#-limit--aggregation-) stages were repositioned in the pipeline to occur before the [$lookup](https://www.mongodb.com/docs/manual/reference/operator/aggregation/lookup/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim) stage (more on that in a moment).

The complete pipeline now looked like this:

```
[
  {
    $match: {
      "contact.address.city": "Austin",
      "deviceSNs.deviceName": "iPhone 12"
    }
  },
  {
    $sort: {
      profileID: 1
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

With this latest set of changes in place, retesting the pipeline revealed a *dramatic* increase in performance. Individual queries were now averaging 51 *milliseconds* with the total elapsed time for 300 query iterations reduced to 1.2 seconds.

|                                      |                        |                                                                  |
|--------------------------------------|------------------------|------------------------------------------------------------------|
| Pipeline Description                 | Average time per query | Total elapsed time (150 query iterations, 15 concurrent threads) |
| Initial design                       | 11.8 seconds           | 260 seconds                                                      |
| $unwind removed                      | 4.7 seconds            | 105 seconds                                                      |
| Refactored many-to-many relationship | 2.9 secnds             | 62.5 seconds                                                     |
| **Duplicated device names**          | **51 milliseconds**    | **1.2 seconds**                                                  |

To understand why this had such a dramatic impact on performance, we compared an[explain plan](https://www.mongodb.com/docs/manual/reference/explain-results/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=agg-part4-foojay&utm_term=tony.kim#explain-results) for the pipeline from before the latest changes were made with one from after the changes were made. The explain plan from before the latest changes were implemented showed an initial search for profiles in "Austin" returned 6,763 documents. All of these documents would need to be processed by each subsequent stage in the pipeline.

By contrast, the explain plan from after the changes were made showed that by adding device name to the query criteria, the number of matching documents — for city "Austin" *and* at least one device of type "iPhone 12" — fell to 1,234. This was a significant reduction in the number of documents the subsequent pipeline stages needed to work with.

As an additional benefit, as we now had all of the information needed to identify matching profiles directly from the profile documents (i.e., we had both the city *and* the device types they had used to connect to the service), we could immediately execute the $sort stage following the $match, arranging the matched profile documents into the required profileID order, and then apply the $skip and $limit stages to further reduce the number of documents down to just the 10 that would be returned. This meant that by the time we reached the $lookup stage, we were only carrying it out for 10 documents rather than — in the case of "Austin" — 6,763 documents with the prior pipeline design. A huge win.

The elephant in the room with this change, of course, is that it did involve duplicating the device name information. It was now listed on both profiles and devices, so we had increased the update cost if device name information needed to be changed. We also slightly increased the system storage requirements. In classical relational data modeling theory, these are often cited as reasons why data duplication should be avoided, but are they really valid reasons? Whilst storage is certainly not free, it's not anywhere near as expensive as it was when Edgar Codd developed his normal forms in the 1970s. The reality is, if we read data way more frequently than we update it, using data duplication judiciously to improve query performance and associated CPU/memory costs during read operations will often more than offset any additional storage and update costs. This is especially true when duplicating data that rarely, if ever, changes, as is frequently the case with lookup or reference values.

In our scenario, duplicating the device names data was deemed acceptable as device names almost never change, so the update cost if they did change was deemed an acceptable trade-off for the query performance improvements realized. After measuring the change, we also found that the amount of duplicated data also did not significantly alter the overall system storage requirements.

We had now successfully brought our query performance below our one-second target. But there was one more quick and easy improvement we could make. In the fifth and final part of this series, we will show how indexes can be used to avoid expensive in-memory sort operations.
