---
title: "The Cost of Not Knowing MongoDB - Part 2"
slug: "the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4"
date: "2025-10-21T14:26:51+00:00"
lastmod: "2025-10-21T14:26:53+00:00"
description: "This is the second part of the series “The Cost of Not Knowing MongoDB,” where we go through many ways we can model our MongoDB schemas for the same application and have different performances. In the first part of the series, we concatenated fields, changed data types, and short-handed field names to improve the application performance. In this second part, as discussed in the issues and improvement of appV4, the performance gains will be achieved by analyzing the application behavior and how it stores and reads its data, leading us to the use of the Bucket Pattern and the Computed Pattern. "
authors:
  - "artur-costa"
image: "/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "from-zero-to-vector-hero-locally"
frozen: false
---

This is the second part of the series "The Cost of Not Knowing MongoDB," where we go through many ways we can model our MongoDB schemas for the same application and have different performances. In the [first part](https://www.mongodb.com/developer/products/mongodb/cost-of-not-knowing-mongodb/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) of the series, we concatenated fields, changed data types, and short-handed field names to improve the application performance. In this second part, as discussed in the [issues and improvement of appV4](https://www.mongodb.com/developer/products/mongodb/cost-of-not-knowing-mongodb/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim#issues-and-improvements:~:text=than%20appV3.-,Issues%20and%20improvements,-Enough%20of%20focusing), the performance gains will be achieved by analyzing the application behavior and how it stores and reads its data, leading us to the use of the [Bucket Pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-bucket-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) and the [Computed Pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-computed-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim).

Application Version 5 Revision 0 and Revision 1 (appV5R0 and appV5R1): A simple way to use the Bucket Pattern {#h2-0-application-version-5-revision-0-and-revision-1-appv5r0-and-appv5r1-a-simple-way-to-use-the-bucket-pattern}
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

When generating the oneYear totals report, the Get Reports function will need to retrieve an average of 60 documents and, in the worst-case scenario, 365 documents. To access each document, one index entry must be visited, and one disk read operation must be performed.

One way to reduce the number of index entries and documents retrieved to generate the report is to use the [Bucket Pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-bucket-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim). According to the [MongoDB documentation](https://www.mongodb.com/docs/manual/data-modeling/design-patterns/group-data/bucket-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), "The bucket pattern separates long series of data into distinct objects. Separating large data series into smaller groups can improve query access patterns and simplify application logic*.*"

Looking at our application from the perspective of the Bucket Pattern, so far, we have bucketed our data daily by a user, each document containing the status totals for one user in one day. For the two application versions presented in this section, appV5R0 and appV5R1, we'll bucket the data by month (appV5R0), and by quarter (appV5R1).

As these are our first implementations using the Bucket Pattern, let's make it as [simple as possible](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/main/src/applications/app-v5-r0.ts).

For appV5R0, each document groups the events by month and user. Every document will have a field of type array called items to which each event document will be pushed. The event document pushed to the array will have its status field names shorthanded to its first letter, the same way we did in appV3 and appV4, and the date to which the event is referent.

The _id field will have a logic similar to the one used in appV4, with the values of key and date concatenated and stored as hexadecimal/binary information. The difference is the date value---instead of being composed by year, month, and day (YYYYMMDD)---will only have year and month (YYYYMM), as we are bucketing the data by month.

For appV5R1, we have almost the same implementation as appV5R0, with the difference being that we'll bucket the events by quarter, and the date value used to generate the _id field will be composed by year and quarter (YYYYQQ) instead of year and month (YYYYMM).

To build the _id field based on the key and date values for the appV5R0, the following TypeScript function was created:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const buildId = (key: string, date: Date): Buffer =\> { const \[YYYY, MM\] = date.toISOString().split(); <br /> return Buffer.from(\`${key}${YYYY}${MM}\`, 'hex'); }; |

To build the _id field based on the key and date values for the appV5R1, the following TypeScript functions were created:

|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const getQQ = (date: Date): string =\> { const month = Number(getMM(date)); if (month \>= 1 \&\& month \<= 3) return '01'; else if (month \>= 4 \&\& month \<= 6) return '02'; else if (month \>= 7 \&\& month \<= 9) return '03'; else return '04'; }; const buildId = (key: string, date: Date): Buffer =\> { const \[YYYY\] = date.toISOString().split('-'); const QQ = getQQ(date); <br /> return Buffer.from(\`${key}${YYYY}${QQ}\`, 'hex'); }; |

### Schema {#h3-1-schema}

The application implementations presented above would have the following TypeScript document schema denominated SchemaV5R0:

|----------------------------------------------------------------------------------------------------------------------|
| type SchemaV5R0 = { _id: Buffer; items: Array\<{ date: Date; a?: number; n?: number; p?: number; r?: number; }\>; }; |

### Bulk upsert {#h3-2-bulk-upsert}

Based on the specification presented, we have the following bulk updateOne operation for each event generated by the application:

|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const opeartion = { updateOne: { filter: { _id: buildId(event.key, event.date) }, update: { $push: { items: { date: event.date, a: event.approved, n: event.noFunds, p: event.pending, r: event.rejected, }, }, }, upsert: true, }, }; |

The above updateOne operation will filter/search for the document with the _id field composed by the concatenation of event key and event month/quarter and will [$push](https://www.mongodb.com/docs/manual/reference/operator/update/push/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) to the items array field a document with the event date and status.

### Get reports {#h3-3-get-reports}

Five aggregation pipelines, one for each date interval, will be needed to fulfill the Get Reports operation. Each date interval will have the following pipeline, with just the date used in the function buildId being different:

|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const pipeline = \[ { $match: { _id: { $gte: buildId(request.key, Date.now() - oneYear), $lte: buildId(request.key, Date.now()), }, }, }, { $unwind: { path: '$items', }, }, { $match: { 'items.date': { $gte: Date.now() - oneYear, $lt: Date.now() }, }, }, { $group: { _id: null, approved: { $sum: '$items.a' }, noFunds: { $sum: '$items.n' }, pending: { $sum: '$items.p' }, rejected: { $sum: '$items.r' }, }, }, { $project: { _id: 0 } }, \]; |

The first stage, [$match](https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will range-filter by the _id field. The lower bound of our range, [$gte](https://www.mongodb.com/pt-br/docs/manual/reference/operator/aggregation/gte/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will be generated using the buildId function with the key of the user for which we want the report and the date one year before the day when the report was requested. The upper bound of our range, [$lte](https://www.mongodb.com/docs/manual/reference/operator/aggregation/lte/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will be generated similarly to the lower bound, but the date provided will be the day when the report was requested. One point of attention in this stage is that the result of buildId contains information by month/quarter, not day, as we need to build the report, so further filtering by day will be necessary.

The second stage, [$unwind](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unwind/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will deconstruct the array field items from the input documents to output a document for each element in the array.

The third stage, $match, will filter all documents between the report's date range. It can be seen that we have already filtered by date, but as presented in the explanation of the first stage, we filtered by month/quarter, and to generate the report, we need to filter by day.

The fourth stage, [$group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will group all the documents in just one (_id: null), and during this process, [$sum](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sum/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) the status values, generating the status totals for the report.

The fifth stage, [$project](https://www.mongodb.com/docs/manual/reference/operator/aggregation/project/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), will only remove the _id field from the resulting document.

### Indexes {#h3-4-indexes}

As this implementation will use the _id field for its operations, it won't need an extra index to support the Bulk Upsert and Get Reports operations.

### Scenario {#h3-5-scenario}

Inserting the 500 million event documents for the initial scenario in the collections appV5R0 and appV5R1 with the schema and Bulk Upsert function presented above, and also presenting the values from the previous versions, we have the following collection stats:

|------------|-------------|-----------|---------------|--------------|---------|------------|
| Collection | Documents   | Data Size | Document Size | Storage Size | Indexes | Index Size |
| appV1      | 359.639.622 | 39,58GB   | 119B          | 8.78GB       | 2       | 20.06GB    |
| appV2      | 359.614.536 | 41.92GB   | 126B          | 10.46GB      | 2       | 16.66GB    |
| appV3      | 359.633.376 | 28.7GB    | 86B           | 8.96GB       | 2       | 16.37GB    |
| appV4      | 359.615.279 | 19.66GB   | 59B           | 6.69GB       | 1       | 9.5GB      |
| appV5R0    | 95.350.431  | 19.19GB   | 217B          | 5.06GB       | 1       | 2.95GB     |
| appV5R1    | 33.429.649  | 15.75GB   | 506B          | 4.04GB       | 1       | 1.09GB     |

Calculating the event stats for appV5R0 and appV5R1 and also presenting the values from the previous versions, we have the following:

|------------|------------------|-------------------|-------------------|
| Collection | Data Size/events | Index Size/events | Total Size/events |
| appV1      | 85B              | 43.1B             | 128.1B            |
| appV2      | 90B              | 35.8B             | 125.8B            |
| appV3      | 61.6B            | 35.2B             | 96.8B             |
| appV4      | 42.2B            | 20.4B             | 62.6B             |
| appV5R0    | 41.2B            | 6.3B              | 47.5B             |
| appV5R1    | 33.8B            | 2.3B              | 36.1B             |

Analyzing the tables above, we can see that going from appV4 to appV5R0, we practically didn't have improvements when looking at Data Size, but when considering the Index Size, the improvement was quite considerable. The index size for appV5R0 is 69% of the size of appV4.

When considering going from appV4 to appV5R1, the gains are even more impressive. In this case, we reduced the Data Size by 20% and the Index Size by 89%.

Looking at the event stats, we had considerable improvements in the Total Size/events, but what really catches the eye is the improvement in the Index Size/events, which is three times smaller for appV5R0 and *nine* times smaller for appV5R1.

This huge reduction in the index size is due to the use of the Bucket Pattern, where one document will store data for many events, reducing the total number of documents, and as a consequence, reducing the number of index entries.

With these impressive improvements regarding index size, it's quite probable that we'll also see impressive improvements in the application performance. One point of attention in the values presented above is that the index size of the two new versions is smaller than the memory size of the machine running the database, allowing the whole index to be kept in the cache, which is very good from a performance point of view.

### Load tests results {#h3-6-load-tests-results}

Executing the load test for appV5R0 and appV0R1 and plotting it alongside the results for appV4, we have the following results for Get Reports and Bulk Upsert:
![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.31.56-PM-1024x566.png) ![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.32.22-PM-1024x536.png)

The graph above shows the best improvement between versions we've ever had in this series. Both new versions, appV5R0 and appV0R1, are considerably more performative than appV4, and appV5R1 is a lot better than appV5R0. This shows that, in our case, bucketing the data by quarter is better than bucketing the data by month.

For the Get Reports operations, the appV5R0 application was able to reach at least the lower desired rate for the first time, with appV5R1 almost reaching all the desired rates for the first quarter of the load test.

For the Bulk Upsert operations, both new versions almost reached the desired rates throughout the whole test, with app5R0 falling in the last 20 minutes.

### Issues and improvements {#h3-7-issues-and-improvements}

Because we made this first implementation of the Bucket Pattern as simple as possible, some clear possible optimizations weren't considered. The main one is how we handle the items array field. In the current implementation, we just push the events documents to it, even when we already have events for a specific day.

A clear optimization here is one that we have been using from appV1 to appV4, where we create just one document per key and date/day, and when we have many events for the same key and date/day, we just increment the status of the document based on the status of the event.

Applying this optimization, we'll reduce the size of the documents because the array of items will have fewer elements. We'll also reduce the computational cost of generating the reports because we are pre-computing the status totals by day. This [build pattern](https://www.mongodb.com/company/blog/building-with-patterns-a-summary/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) of pre-computing is quite common in that it has its own name, [Computed Pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-computed-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim).

Application Version 5 Revision 2 (appV5R2): Using the Bucket Pattern with the Computed Pattern {#h2-8-application-version-5-revision-2-appv5r2-using-the-bucket-pattern-with-the-computed-pattern}
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

As discussed in the issues and improvements of appV5R0 and appV5R1, we can use the [Computed Pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-computed-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) to pre-compute the total status by day in the items array field when inserting a new event. This reduces the computation cost of generating the reports and also reduces the document size by having fewer elements in the items array field.

Most of this application version will be equal to the appV5R1, where we bucketed the events by quarter. The only difference will be in the Bulk Upsert operation, where we will update an element in the items array field if an element with the same date of the new event already exists, or insert a new element in items if an element with the same date of the new event doesn't exist.

### Schema {#h3-9-schema}

The application implementations presented above would have the following TypeScript document schema denominated SchemaV5R0:

|----------------------------------------------------------------------------------------------------------------------|
| type SchemaV5R0 = { _id: Buffer; items: Array\<{ date: Date; a?: number; n?: number; p?: number; r?: number; }\>; }; |

### Bulk upsert {#h3-10-bulk-upsert}

Based on the specification presented, we have the following bulk updateOne operation for each event generated by the application:

|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const sumIfItemExists = itemsArray.buildResultIfItemExists(event); const returnItemsOrCreateNew = itemsArray.buildItemsOrCreateNew(event); const operation = { updateOne: { filter: { _id: buildId(event.key, event.date), }, update: \[ { $set: { result: sumIfItemExists } }, { $set: { items: returnItemsOrCreateNew } }, { $unset: \['result'\] }, \], upsert: true, }, }; |

The complete code for this updateOne operation is quite big, hard to get your head around it quickly, and would also make the process of browsing through the article a little cumbersome. Because of that, here we will have a pseudocode of it. The real code that builds the operation can be found [on GitHub](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r2.ts#L12), and a full code example can be found [in this repository](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r2.ts#L12).

Our goal in this update operation is to increment the status of an element in the items array if an element with the same date of the new event already exists, or create a new element if there isn't one with the same date. It's not possible to achieve this functionality with the [MongoDB Update Operators](https://www.mongodb.com/docs/manual/reference/operator/update/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim#update-operators-1). The way around it is to use [Update with Aggregation Pipeline](https://www.mongodb.com/docs/manual/reference/method/db.collection.update/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim#update-with-aggregation-pipeline), which allows a more expressive update statement.

To facilitate the understanding of the logic used in the pipeline, a simplified JavaScript version of the functionality will be provided.

The first stage, $set, will set the field result to the logic of the variable sumIfItemExists. As the name suggests, this logic will iterate through the items array looking for elements with the same date as the event. If there is one, this element will have the status present in the event summed/added to it. As we need a way to keep track of if an element with the same date of the event was found and the event status was registered, there is an environment boolean variable called found that will keep track of it.

The following JavaScript code presents a functionality similar to what happens in the first stage of the aggregation pipeline.

|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const result = items.reduce( (accumulator, element) =\> { if (element.date === event.date) { element.a += event.a; element.n += event.n; element.p += event.p; element.r += event.r; accumulator.found = true; } accumulator.items.push(element); return accumulator; }, { found: false, items: \[\] } ); |

The result variable/field will be generated using a [reduce](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/reduce) method on the items array field from the document we want to update. The initial value for the reduce method is an object with the field found and items. The field accumulator.found has an initial value of false and is responsible for signalizing if an element in the reduced execution had the same date as the event we want to register. If there is one element with the same date as the event, element.date === event.date, the status values of the element will be incremented by the status of the event and the accumulator.found field will be set to true, indicating that the event was registered. The accumulator.items array field will have the element of each iteration pushed to it, becoming the new items array field.

The second stage, $set, will set the field items to the resulting logic of the variable returnItemsOrCreateNew. With a little effort of imagination, the name suggests that the logic present in the variable will return the items field of the previous stage if an element with the same date of the event was found, found == true, or return a new array generated by the concatenation of the items array field of the previous stage with a new array field containing the event element when an element with the same date of the event was not found during the reduced iterations, found == false.

The following JavaScript code presents the above logic closer to what happens in the aggregation pipeline.

|------------------------------------------------------------------------------------------------------------------------|
| let items = \[\]; if (result.found == true) { items = result.items; } else { items = result.items.concat(\[event\]); } |

The third stage, $unset, will only remove the field result that was created during the first stage and used in the second stage of the pipeline.

### Get reports {#h3-11-get-reports}

Five aggregation pipelines, one for each date interval, will be needed to fulfill the Get Reports operation. Each date interval will have the following pipeline, with just the date used in the function buildId being different:

|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const pipeline = \[ { $match: { _id: { $gte: buildId(request.key, Date.now() - oneYear), $lte: buildId(request.key, Date.now()), }, }, }, { $unwind: { path: '$items', }, }, { $match: { 'items.date': { $gte: Date.now() - oneYear, $lt: Date.now() }, }, }, { $group: { _id: null, approved: { $sum: '$items.a' }, noFunds: { $sum: '$items.n' }, pending: { $sum: '$items.p' }, rejected: { $sum: '$items.r' }, }, }, { $project: { _id: 0 } }, \]; |

This aggregation pipeline is the same as the appV5R0 and appV5R1. A detailed explanation can be found [in the previous \`Get reports\` section](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.cgmzohl2qj3o).

### Indexes {#h3-12-indexes}

As this implementation will use the _id field for its operations, it won't need an extra index to support the Bulk Upsert and Get Reports operations.

### Scenario {#h3-13-scenario}

Inserting the 500 million event documents for the initial scenario in the collections appV5R2 with the schema and Bulk Upsert function presented above, and also presenting the values from the previous versions, we have the following collection stats:

|------------|-------------|-----------|---------------|--------------|---------|------------|
| Collection | Documents   | Data Size | Document Size | Storage Size | Indexes | Index Size |
| appV1      | 359.639.622 | 39,58GB   | 119B          | 8.78GB       | 2       | 20.06GB    |
| appV2      | 359.614.536 | 41.92GB   | 126B          | 10.46GB      | 2       | 16.66GB    |
| appV3      | 359.633.376 | 28.7GB    | 86B           | 8.96GB       | 2       | 16.37GB    |
| appV4      | 359.615.279 | 19.66GB   | 59B           | 6.69GB       | 1       | 9.5GB      |
| appV5R0    | 95.350.431  | 19.19GB   | 217B          | 5.06GB       | 1       | 2.95GB     |
| appV5R1    | 33.429.468  | 15.75GB   | 506B          | 4.04GB       | 1       | 1.09GB     |
| appV5R2    | 33.429.649  | 11.96GB   | 385B          | 3.26GB       | 1       | 1.16GB     |

Calculating the event stats for appV5R2 and also presenting the values from the previous versions, we have the following:

|------------|------------------|-------------------|-------------------|
| Collection | Data Size/events | Index Size/events | Total Size/events |
| appV1      | 85B              | 43.1B             | 128.1B            |
| appV2      | 90B              | 35.8B             | 125.8B            |
| appV3      | 61.6B            | 35.2B             | 96.8B             |
| appV4      | 42.2B            | 20.4B             | 62.6B             |
| appV5R0    | 41.2B            | 6.3B              | 47.5B             |
| appV5R1    | 33.8B            | 2.3B              | 36.1B             |
| appV5R2    | 25.7B            | 2.5B              | 28.2B             |

Analyzing the tables above, we have the expected result presented in the introduction, from appV5R1 to appV5R2. The only noticeable difference is the 24% reduction in the Data Size.

This reduction in the Data Size and Document size will help in the performance of our application by reducing the time spent reading the document from the disk and the processing cost of decompressing the document from its compressed state.

### Load tests results {#h3-14-load-tests-results}

Executing the load test for appV5R2 and plotting it alongside the results for appV5R1, we have the following results for Get Reports and Bulk Upsert:
![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.33.09-PM-1024x550.png) ![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.33.46-PM-1024x530.png)

The graphs above show a small performance improvement in the Get Reports and a little worse performance in the Bulk Upsert when going from appV5R1 to appV5R2. I confess I was expecting more.

To understand why we got this result, we need to take into consideration the rates at which each operation is executed and its processing cost. The average rate of the Bulk Upsert is 625 operations per second and the Get Reports is 137,5 operations per second. With Bulk Upsert being executed on average 4,5 times more than Get Reports, when we increased its complexity by using the Computed Pattern, aiming to decrease the computation cost of Get Reports, we probably changed six for half a dozen. The overall computational cost probably stayed the same.

For those who have already stumbled on the definition of the Computed Pattern on the [MongoDB documentation](https://www.mongodb.com/docs/manual/data-modeling/design-patterns/computed-values/computed-schema-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), the previous result wasn't a big surprise. There, we have, "If reads are significantly more common than writes, the computed pattern reduces the frequency of data computation." In our case, writes are way more common than reads, so the Computed Pattern may not be a good fit.

### Issues and improvements {#h3-15-issues-and-improvements}

Let's try to extract more performance of our application by searching for improvement in our current operations. Looking at the aggregation pipeline of Get Reports, we find a very common anti-pattern when fields of type array are involved. This anti-pattern is the $unwind followed by a $match, which happens in the second and third stages of our aggregation pipeline.

This combination of stages can hurt the performance of the aggregation pipeline because we are increasing the number of documents in the pipeline with the $unwind stage to later filter the documents with the $match. In other words, to get to a final state with fewer documents, we're going through an intermediate state where we increase the number of documents.

In the next application revision, we'll see how we can achieve the same final result using only one stage and without having an intermediate stage with more documents.

Application Version 5 Revision 3 (appV5R3): Removing an aggregation pipeline anti-pattern {#h2-16-application-version-5-revision-3-appv5r3-removing-an-aggregation-pipeline-anti-pattern}
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

As presented in the issues and improvements of appV5R2, we have an anti-pattern in the aggregation pipeline of Get Reports that can reduce the query performance. This anti-pattern is characterized by a $unwind stage followed by a $match. This combination of stages will first increase the number of documents, $unwind, to later filter them, $match. In a simplified way, to get to a final state, we're going through a costly intermediary state.

One possible solution around this anti-pattern is to use the [$addFields](https://www.mongodb.com/docs/manual/reference/operator/aggregation/addFields/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) stage with the [$filter](https://www.mongodb.com/docs/manual/reference/operator/aggregation/filter/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) operator on the items array field. With this combination, we would replace the items array field using the $addFields stage with a new array field generated by the $filter operator in the items array, where we would filter all elements where the date is inside the report's date range.

But, considering our aggregation pipeline with the optimization presented above, there is an even better solution. With the $filter operator, we will loop through all elements in the items field and only compare their dates with the report dates to filter the elements. As the final goal of our query is to get the status totals of all elements within the report's date range, instead of just looping through the elements in items to filter them, we could already start to calculate the status totals. We can obtain this functionality by using the [$reduce](https://www.mongodb.com/docs/manual/reference/operator/aggregation/reduce/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim) operator instead of the $filter.

### Schema {#h3-17-schema}

The application implementations presented above would have the following TypeScript document schema denominated SchemaV5R0:

|----------------------------------------------------------------------------------------------------------------------|
| type SchemaV5R0 = { _id: Buffer; items: Array\<{ date: Date; a?: number; n?: number; p?: number; r?: number; }\>; }; |

### Bulk upsert {#h3-18-bulk-upsert}

Based on the specification presented, we have the following bulk updateOne operation for each event generated by the application:

|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const sumIfItemExists = itemsArray.buildResultIfItemExists(event); const returnItemsOrCreateNew = itemsArray.buildItemsOrCreateNew(event); const operation = { updateOne: { filter: { _id: buildId(event.key, event.date), }, update: \[ { $set: { result: sumIfItemExists } }, { $set: { items: returnItemsOrCreateNew } }, { $unset: \['result'\] }, \], upsert: true, }, }; |

This updateOne operation is the same as the appV5R2. A detailed explanation can be found [in the previous \`Bulk upsert\` section](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.dvjwoeoc8pll).

### Get reports {#h3-19-get-reports}

Five aggregation pipelines, one for each date interval, will be needed to fulfill the Get Reports operation:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| const pipeline = \[ { $match: docsFromKeyBetweenDate }, { $addFields: itemsReduceAccumulator }, { $group: groupSumStatus }, { $project: { _id: 0 } }, \]; |

The complete code for this aggregation pipeline is quite complicated. Because of that, we will have just a pseudocode for it here. The real code that builds the operation can be found [in this TypeScript file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r3.ts#L37) and a full code example can be found [in this Markdown file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r3.ts#L37).

The first stage, $match, and last stage, $project, of the above aggregation pipeline are the same as the [appV5R2](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.81y5w5ci21c8).

As presented in the introduction of this revision and by thinking about the name of the variable in the second stage, $addFields, it will add a new field to the document called totals that will have the status totals for the elements of the items array field where the date is in the report's date range. This totals field will be generated by the $reduce operator which will have a logic equivalent to the following JavaScript code:

|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const totals = items.reduce( (accumulator, element) =\> { if (element.date \> (Date.now() - oneYear) \&\& element.date \< Date.now()) { accumulator.a += element.a; accumulator.n += element.n; accumulator.p += element.p; accumulator.r += element.r; } return accumulator; }, { a: 0, n: 0, p: 0, r: 0 } ); |

The reduce operation will have as its initial value a document/object with the possible status fields set to 0. As the function iterates over the elements of items, it will check if the element date is in the report's date range. If the element date belongs to the report's date range, its status values will be summed to the accumulator status.

The third stage, $group, will have a logic similar to the one that we have been using so far. The only difference is that we will sum the values present in the field totals instead of items. The following code is the actual logic used in the above pipeline:

|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const groupSumStatus = { _id: null, approved: { $sum: '$items.a' }, noFunds: { $sum: '$items.n' }, pending: { $sum: '$items.p' }, rejected: { $sum: '$items.r' }, }; |

### Scenario {#h3-20-scenario}

Inserting the 500 million event documents for the initial scenario in the collections appV5R3 with the schema and Bulk Upsert function presented above, and also presenting the values from the previous versions, we have the following collection stats:

|------------|-------------|-----------|---------------|--------------|---------|------------|
| Collection | Documents   | Data Size | Document Size | Storage Size | Indexes | Index Size |
| appV1      | 359.639.622 | 39,58GB   | 119B          | 8.78GB       | 2       | 20.06GB    |
| appV2      | 359.614.536 | 41.92GB   | 126B          | 10.46GB      | 2       | 16.66GB    |
| appV3      | 359.633.376 | 28.7GB    | 86B           | 8.96GB       | 2       | 16.37GB    |
| appV4      | 359.615.279 | 19.66GB   | 59B           | 6.69GB       | 1       | 9.5GB      |
| appV5R0    | 95.350.431  | 19.19GB   | 217B          | 5.06GB       | 1       | 2.95GB     |
| appV5R1    | 33.429.468  | 15.75GB   | 506B          | 4.04GB       | 1       | 1.09GB     |
| appV5R2    | 33.429.649  | 11.96GB   | 385B          | 3.26GB       | 1       | 1.16GB     |
| appV5R3    | 33.429.492  | 11.96GB   | 385B          | 3,24GB       | 1       | 1.11GB     |

Calculating the event stats for appV5R3 and also presenting the values from the previous versions, we have the following:

|------------|------------------|-------------------|-------------------|
| Collection | Data Size/events | Index Size/events | Total Size/events |
| appV1      | 85B              | 43.1B             | 128.1B            |
| appV2      | 90B              | 35.8B             | 125.8B            |
| appV3      | 61.6B            | 35.2B             | 96.8B             |
| appV4      | 42.2B            | 20.4B             | 62.6B             |
| appV5R0    | 41.2B            | 6.3B              | 47.5B             |
| appV5R1    | 33.8B            | 2.3B              | 36.1B             |
| appV5R2    | 25.7B            | 2.5B              | 28.2B             |
| appV5R3    | 25.7B            | 2.4B              | 28.1B             |

As the document schema and Bulk Upsert operations for appV5R3 are the same as appV5R2, there is nothing to reason about in this section between the two revisions.

### Load tests results {#h3-21-load-tests-results}

Executing the load test for appV5R3 and plotting it alongside the results for appV5R2, we have the following results for Get Reports and Bulk Upsert:
![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.34.19-PM-1024x566.png) ![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.34.43-PM-1024x537.png)

The graphs above show that we have another revision that didn't bring clear gains. appV5R3 is just slightly better than appV5R2 in some parts of both functions, Get Reports and Bulk Upsert.

From a logic point of view, It's clear that the aggregation pipeline of Get Reports from appV5R3 is more optimized and less CPU- and memory-intensive than appV5R2, but it didn't yield better results. This can be explained by the current bottleneck of the instance running the MongoDB, its disk. We won't go into details on it to keep this article just big, not enormous. This disk bottleneck will become very clear when we go from appV6R3 to appV6R4 in the next article.

For those curious about how we can check or validate if the disk is the limiting factor in our instance, the graph of System CPU on Atlas Metrics has the IOWAIT metric, which shows how much the CPU is waiting for the disk. [In this GitHub folder](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/tree/main/atlas-metrics), you can find prints from some metrics for all application version tests and check the System CPU prints for appV5R2 and appV5R3 and see the IOWAIT metric reaching almost 15% of the CPU usage.

### Issues and improvements {#h3-22-issues-and-improvements}

We've just seen that our implementation's limitation is the disk. To solve that, we have two options: Upgrade the disk where MongoDB stores data or change our implementation to reduce disk usage.

As the goal of this series is to show how much performance we can achieve with the same hardware by modeling how our application stores and reads data from MongoDB, we won't upgrade the disk. A change in the application modeling for MongoDB will be left for the next article, appV6Rx.

For appV5R4, we will double down on the Computed Pattern and pre-compute the status totals by quarter, not just day. Even though we know it probably won't provide better performance for things discussed [in the "Load test result" of \`appV5R2\`](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.6vfvgbgd9a7h), let's flex our MongoDB and aggregation pipeline knowledge and also provide a reference code example for the cases where the Computed Pattern is a good fit.

Application Version 5 Revision 4 (appV5R4): Doubling down on the Computed Pattern {#h2-23-application-version-5-revision-4-appv5r4-doubling-down-on-the-computed-pattern}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

As presented in the issues and improvements of appV5R3, for this revision, we'll double down on the Computed Pattern even though we have good evidence that it won't provide a better performance---but, you know, for science.

We'll also use the Computed Pattern to pre-compute the status totals for each document. As each document stores the events per quarter and user, our application will have on each document the status totals per quarter and user. These pre-computed totals will be stored in a field called totals.

One point of attention in this implementation is that we are adding a new field to the document, which will also increase the average document size. As seen in the previous revision, appV5R3, our current bottleneck is disk, another indication that this implementation won't have better performance.

### Schema {#h3-24-schema}

The application implementations presented above would have the following TypeScript document schema denominated SchemaV5R1:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| type SchemaV5R1 = { _id: Buffer; totals: { a?: number; n?: number; p?: number; r?: number; }; items: Array\<{ date: Date; a?: number; n?: number; p?: number; r?: number; }\>; }; |

### Bulk upsert {#h3-25-bulk-upsert}

Based on the specification presented, we have the following bulk updateOne operation for each event generated by the application:

|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| const newReportFields = itemsArray.buildNewTotals(event); const sumIfItemExists = itemsArray.buildResultIfItemExists(event); const returnItemsOrCreateNew = itemsArray.buildItemsOrCreateNew(event); const operation = { updateOne: { filter: { _id: buildId(event.key, event.date), }, update: \[ { $set: newReportFields }, { $set: { result: sumIfItemExists } }, { $set: { items: returnItemsOrCreateNew } }, { $unset: \['result'\] }, \], upsert: true, }, }; |

As already explained in other complicated code that would make the article a little cumbersome to read, here we only have a pseudocode of the implementation. The code that builds the operation can be found [in this TypeScript file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r4.ts#L11), and a full code example can be found [in this Markdown file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r4.ts#L11).

The only difference between the above updateOne operation and the one for appV5R3 is the first stage, $set. This is the stage where we'll pre-compute the totals field for the quarter to which the document stores events. The following JavaScript code presents a functionality similar to what happens in the stage:

|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| if (totals.a != null) totals.a += event.a; else totals.a = event.a; if (totals.n != null) totals.n += event.n; else totals.n = event.n; if (totals.p != null) totals.p += event.p; else totals.p = event.p; if (totals.r != null) totals.r += event.r; else totals.r = event.r; |

The last three stages are strictly equal to appV5R3 which you can see a detailed explanation of in the [\`Bulk upsert\` operation](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.l63rg1dna3e).

### Get reports {#h3-26-get-reports}

Five aggregation pipelines, one for each date interval, will be needed to fulfill the Get Reports operation:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| const pipeline = \[ { $match: docsFromKeyBetweenDate }, { $addFields: itemsReduceAccumulator }, { $group: groupSumStatus }, { $project: { _id: 0 } }, \]; |

As already explained in other complicated code that would make the article a little cumbersome to read, here we only have a pseudocode of the implementation. The code that builds the operation can be found [in this TypeScript file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r4.ts#L55), and a full code example can be found [in this Markdown file](https://github.com/ArturGC/the-cost-of-not-knowing-mongodb/blob/c8cc690d6dcf9f4db09c36f1396941df11688ac1/src/applications/app-v5-r4.ts#L11).

Most of the above aggregation pipeline logic is equal to the one for appV5R3. There is a small difference: just an if logic, in the second stage, $addFields. As seen in the [Get Reports explanation of appV5R3](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.sr1kedv4sh1x), in this stage, we will create a new field called totals by looping through the elements of the array field items and adding the status. In our current implementation, we already have the totals field pre-computed in the document, so, if the quarter's date range is within the limits of the report's date range, we can use the pre-computed totals. The following JavaScript code presents a functionality similar to what happens in the stage:

|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| let totals; if (documentQuarterWithinReportDateRage) { totals = document.totals; } else { totals = document.items.reduce( (accumulator, element) =\> { if (element.date \> Date.now() - oneYear \&\& element.date \< Date.now()) { accumulator.a += element.a; accumulator.n += element.n; accumulator.p += element.p; accumulator.r += element.r; } return accumulator; }, { a: 0, n: 0, p: 0, r: 0 } ); } |

### Scenario {#h3-27-scenario}

Inserting the 500 million event documents for the initial scenario in the collections appV5R4 with the schema and Bulk Upsert function presented above, and also presenting the values from the previous versions, we have the following collection stats:

|------------|-------------|-----------|---------------|--------------|---------|------------|
| Collection | Documents   | Data Size | Document Size | Storage Size | Indexes | Index Size |
| appV1      | 359.639.622 | 39,58GB   | 119B          | 8.78GB       | 2       | 20.06GB    |
| appV2      | 359.614.536 | 41.92GB   | 126B          | 10.46GB      | 2       | 16.66GB    |
| appV3      | 359.633.376 | 28.7GB    | 86B           | 8.96GB       | 2       | 16.37GB    |
| appV4      | 359.615.279 | 19.66GB   | 59B           | 6.69GB       | 1       | 9.5GB      |
| appV5R0    | 95.350.431  | 19.19GB   | 217B          | 5.06GB       | 1       | 2.95GB     |
| appV5R1    | 33.429.468  | 15.75GB   | 506B          | 4.04GB       | 1       | 1.09GB     |
| appV5R2    | 33.429.649  | 11.96GB   | 385B          | 3.26GB       | 1       | 1.16GB     |
| appV5R3    | 33.429.492  | 11.96GB   | 385B          | 3.24GB       | 1       | 1.11GB     |
| appV5R4    | 33.429.470  | 12.88GB   | 414B          | 3.72GB       | 1       | 1.24GB     |

Calculating the event stats for appV5R3 and also presenting the values from the previous versions, we have the following:

|------------|------------------|-------------------|-------------------|
| Collection | Data Size/events | Index Size/events | Total Size/events |
| appV1      | 85B              | 43.1B             | 128.1B            |
| appV2      | 90B              | 35.8B             | 125.8B            |
| appV3      | 61.6B            | 35.2B             | 96.8B             |
| appV4      | 42.2B            | 20.4B             | 62.6B             |
| appV5R0    | 41.2B            | 6.3B              | 47.5B             |
| appV5R1    | 33.8B            | 2.3B              | 36.1B             |
| appV5R2    | 25.7B            | 2.5B              | 28.2B             |
| appV5R3    | 25.7B            | 2.4B              | 28.1B             |
| appV5R4    | 27.7B            | 2.7B              | 30.4B             |

As discussed in this revision introduction, the additional totals field on each document in the collection increased the document size and the overall storage size. The Data Size of appV5R4 is 7,7% bigger than appV5R3 and the Total Size/events is 8,2%. Because disk is our limiting factor, the performance of appV5R4 will probably be worse than appV5R3.

### Load tests results {#h3-28-load-tests-results}

Executing the load test for appV5R4 and plotting it alongside the results for appV5R3, we have the following results for Get Reports and Bulk Upsert:
![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.35.14-PM-1024x550.png) ![](/images/posts/2025/10/the-cost-of-not-knowing-mongodb-part-2-appv5r0-to-appv5r4/Screenshot-2025-10-13-at-12.35.30-PM-1024x557.png)

The graphs above show what we've been expecting since the beginning of this revision: The performance of appV5R4 is worse than appV5R3 in both functions, Get Reports and Bulk Upsert.

### Issues and improvements {#h3-29-issues-and-improvements}

As spoiled in the previous [Issues and improvements](https://docs.google.com/document/d/1dpI27GNTE4Z4QHEbP-oLzOneH4ZimGSvTqjuYUQ-pFE/edit?tab=t.0#heading=h.kf9zz4h1brb), to improve our application's performance, we need to change our MongoDB implementation in a way where we reduce disk usage. To achieve this, we need to reduce the document size.

You may think it is not possible to reduce our document size and overall collection/index size even more because we are already using just one index, concatenating two fields into one, using shorthand field names, and using the Bucket Pattern. But there is one thing called Dynamic Schema that can help us.

In the Dynamic Schema, the values of a field become field names. Thus, field names also store data and, as a consequence, reduce the document size. As this pattern will require big changes in our current application schema, we'll start a new version, appV6Rx, which we'll play around with in the third part of this series.

Conclusion {#h2-30-conclusion}
------------------------------

That is the end of the second part of the series. We covered Bucket Pattern and Computed Pattern and the many ways we can use these patterns to model how our application stores its data in MongoDB and the big performance gains it can provide when used properly.

Here is a quick review of the improvements made between the application version:

* appV4 to appV5R0/appV5R1: This is the simplest possible implementation of Bucket Pattern, grouping the events by month for appV5R0 and by quarter for appV5R1.
* appV5R1 to appV5R2: Instead of just pushing the event document to the items array, we started to pre-compute the status totals by day, using the Computed Pattern.
* appV5R2 to appV5R3: This improved the aggregation pipeline for Get Reports, preventing a costly intermediary stage. It didn't provide performance improvements because our MongoDB instance is currently disk-limited.
* appV5R3 to appV5R4: We doubled down on Computed Pattern to pre-calculate the totals field even though we knew the performance wouldn't be better---but, just for science.

We had noticeable improvements in the version presented in this second part of the series when compared to the versions from the first part of the series. appV0 to appV4. appV5R3 showed the best performance of them all, but it still can't reach all the desired rates. For the third and final version of this series, our application versions will be developed around the Dynamic Schema Pattern, which will reduce the overall document size and help with the current disk limitation.

For any further questions, you can go to the[MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cost-part2-foojay&utm_term=tony.kim), or if you want to build your application using MongoDB, the MongoDB Developer Center has lots of examples and tutorials in many different programming languages.
