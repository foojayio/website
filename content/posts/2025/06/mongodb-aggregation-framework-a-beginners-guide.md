---
title: "MongoDB Aggregation Framework: A Beginner’s Guide"
slug: "mongodb-aggregation-framework-a-beginners-guide"
date: "2025-06-05T06:20:02+00:00"
lastmod: "2025-11-14T15:53:23+00:00"
description: "The MongoDB Aggregation Framework works like a pipeline—a series of stages where each step processes the data in some way."
authors:
  - "ricardo-mello"
image: "/images/posts/2025/06/mongodb-aggregation-framework-a-beginners-guide/mongologo.png"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
  - "testing-mongodb-atlas-search-java-apps-using-testcontainers"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "avoiding-nullpointerexception"
enlighterjs: true
frozen: false
---

{{< youtube CbYitR4mR6I >}}

Finding exactly the data we need isn't always a simple task.

You've probably faced situations where you needed to filter information, group it, and even perform calculations to produce a final result.

And often, delivering this processed data to the client is essential for the application's success. MongoDB offers two main ways to fetch data:

* `find()`
* `aggregate()`

While `.find()` is great for basic queries, it doesn't cover more advanced scenarios like transformations and complex data processing. That's where the MongoDB Aggregation Framework comes in.

The MongoDB Aggregation Framework works like a pipeline---a series of stages where each step processes the data in some way. When we use the `aggregate()` method, we're building this sequence of operations.

Before diving into MongoDB, here's a simple example of a pipeline in Java:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;String&gt; names = Arrays.*asList*("Alice", "Aloisio",  "alice", "andre", "Ricardo", "Jose", "Maria");
var count = names.stream()
      .map(String::toLowerCase)
      .filter(name -&gt; name.startsWith("a"))
      .distinct()
      .count();
System.out.println(count); 

// output = 3</pre>

```

```

If you look closely, it uses functions like `map`, filter, distinct, and count.  

In other words, it:

1. Converts each name to lowercase.
2. Filters names that start with "a".
3. Removes duplicate names.
4. Counts the total unique names.

This is the essence of a pipeline: chaining operations that refine data step by step until you get the final result.

In MongoDB, we do something very similar.

Aggregation pipeline {#h2-0-aggregation-pipeline}
-------------------------------------------------

An [aggregation pipeline](https://www.mongodb.com/resources/products/capabilities/aggregation-pipeline) consists of one or more stages. Each stage represents a step that will be executed.

For example, consider a `transactions` collection where we want to count how many transactions contain errors. We could filter by status and then count:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[  
  {  
    $match: {  
      status: "error"  
    }  
  },  
  {  
    $count: "total errors"  
  }  
]</pre>

```

```

Here, we apply a `$match` filter to select only the documents with `status` equal to `"error,"` and then use `$count` to calculate the total number of transactions in this status.

Each stage in the pipeline is executed in order, and each one only processes the results from the previous stage. So in the example above, even if there are 1,000 transactions in total, the `$count` stage only counts the transactions that matched the `"error"` status from the `$match` stage.

Aggregation stages {#h2-1-aggregation-stages}
---------------------------------------------

As mentioned earlier, [stages](https://www.mongodb.com/docs/manual/reference/operator/aggregation-pipeline/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) are used to build a pipeline. In this section, let's take a look at some stages that are useful for our day-to-day work.

To explore their capabilities, we'll create a collection called `articles` that will contain the following documents:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.insertMany(  
   [  
       {  
           _id: 1,  
           title: "Spring Data Unlocked: Getting Started With Java and MongoDB",  
           tags: ["Java", "MongoDB", "Spring"],  
           publishedAt: ISODate("2024-11-11T00:00:00Z"),  
           authors: ["Ricardo Mello"],  
           url: "https://www.mongodb.com/developer/products/mongodb/springdata-getting-started-with-java-mongodb/"  
       },  

       {  
           _id: 2,  
           title: "Java Meets Queryable Encryption: Developing a Secure Bank Account Application",  
           tags: ["Java", "Security", "MongoDB"],  
           publishedAt: ISODate("2024-10-08T00:00:00Z"),  
           authors: ["Ricardo Mello"],  
           url: "https://www.mongodb.com/developer/products/atlas/java-queryable-encryption/"  
       },      
       {  
           _id: 3,  
           title: "Beyond Basics: Enhancing Kotlin Ktor API With Vector Search",  
           tags: ["Kotlin", "Vector Search", "MongoDB"],  
           publishedAt: ISODate("2024-09-18T00:00:00Z"),  
           authors: ["Ricardo Mello"],  
           url: "https://www.mongodb.com/developer/products/atlas/beyond-basics-enhancing-kotlin-ktor-api-vector-search/"  
       },  

   ]  
)</pre>

```

```

### $Match {#h3-2-match}

This is one of [the most common stages](https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) you'll use. It basically serves to filter documents based on a specific query. For example, if you only want to return the document with `_id: 3`, you can use:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([  
   { $match: { _id: 3 } }  
])  
// This will return Beyond Basics's article</pre>

```

```

### $Project {#h3-3-project}

We use this stage to specify which fields we'd like to include in our results.

Suppose we want to return all documents and [project](https://www.mongodb.com/docs/manual/reference/operator/aggregation/project/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) only the `title` and `author` fields.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([  
   { $project: { _id: 0, title: 1, authors: 1 } }  
])</pre>

```

```

The result would look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{  
   "title": "Beyond Basics: Enhancing Kotlin Ktor API With Vector Search",  
   "authors": ["Ricardo Mello"]  
 },  

 //.. Others..</pre>

### $Unwind {#h3-4-unwind}

The [$unwind stage](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unwind/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) is used to deconstruct an array into multiple documents. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([
   { $unwind: "$tags" }
])</pre>

```

```

For each tag in the \`tags\` array, the document will be repeated in the query results.  

This way, you can analyze or process each tag individually:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "_id": 2,
  "title": "Spring Data Unlocked: Getting Started With Java and MongoDB",
  "tags": "Java",
  // other fields...
 }
 {
  "_id": 2,
  "title": "Spring Data Unlocked: Getting Started With Java and MongoDB",
  "tags": "MongoDB",
  // other fields...
}
 {
  "_id": 2,
  "title": "Spring Data Unlocked: Getting Started With Java and MongoDB",
  "tags": "Spring",
  // other fields...
},
  // other Documents...</pre>

```

```

### $Group {#h3-5-group}

As the name suggests, we use this stage to [group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/?utm_campaign=devrel&amp;utm_source=third-party-content&amp;utm_medium=cta&amp;utm_content=aggregation_framework_a_beginner_guide&amp;utm_term=ricardo.mello) our results. This time, we'll use the \`$unwind\` stage we saw earlier to deconstruct the array of tags and find out how many articles exist for each tag:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([  
   { $unwind: "$tags" },  
   {  
       $group: {  
           _id: "$tags",  
           totalArticles: { $sum: 1 }  
       }  
   }  
])

The result would look like this:

[  
   {  
       "_id": "Security",  
       "totalArticles": 1  
   },  
   {  
       "_id": "MongoDB",  
       "totalArticles": 4  
   },  
   {  
       "_id": "Java",  
       "totalArticles": 2  
   }  
   .. other tags (Kotlin, Vector Search ..)  
]</pre>

```

```

### $Sort {#h3-6-sort}

Continuing with our example---what if we want to query all articles and [sort](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sort/?utm_campaign=devrel&amp;utm_source=third-party-content&amp;utm_medium=cta&amp;utm_content=aggregation_framework_a_beginner_guide&amp;utm_term=ricardo.mello) them by publication date, from newest to oldest?

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([  
   { $sort: { publishedAt: -1 } }  
])</pre>

```
And if we want to reverse the order—showing the oldest articles first—we just use `1` instead of `-1`.
```

### $AddFields {#h3-7-addfields}

This stage is useful when we want to [add a new field](https://www.mongodb.com/docs/manual/reference/operator/aggregation/addFields/?utm_campaign=devrel&amp;utm_source=third-party-content&amp;utm_medium=cta&amp;utm_content=aggregation_framework_a_beginner_guide&amp;utm_term=ricardo.mello) in our result.

Let's say our client requested that we display a field called \`publishedYear\` containing only the year:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate([  
   {  
     $addFields: {  
       publishedYear: { $year: "$publishedAt" }  
     }  
   }  
 ])</pre>

```
Our result would look something like this:
```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> "_id": 2,  
 .. other fields  
 "publishedYear": 2024 // FIELD ADDED  
// Other fields ..</pre>

    Here, you can see that we’re using an operator called $year to extract the year from our publishedAt field. To learn about other operators, check out our official documentation page on aggregation operators.

Combining stages {#h2-8-combining-stages}
-----------------------------------------

As we explored earlier, a pipeline can combine multiple stages. Let's say we want to know the total number of articles published in 2025 and beyond. We can combine the `$match` and [`$count`](https://www.mongodb.com/docs/manual/reference/operator/aggregation/count/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) stages for this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.articles.aggregate(
   [
       {
           $match: {
             publishedAt: { $gt: ISODate("2024-12-31T00:00:00Z") }
           }
       },
       {
           $count: 'total'
       }
   ]
)</pre>

    Notice that we’re using the $gt operator to filter for years greater than the specific date.

Wrapping up {#h2-9-wrapping-up}
-------------------------------

Aggregation Pipeline is a powerful alternative that MongoDB offers for combining stages and extracting data in an accurate and efficient way. There's a whole world of stages and operators for you to explore.

Always turn to the MongoDB [community](https://www.mongodb.com/community/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=aggregation_framework_a_beginner_guide&utm_term=ricardo.mello) for your questions. I hope this article has been useful to you all.
