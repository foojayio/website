---
title: "GenAI blood, sweat, and tears: Loading data to Pinecone"
slug: "genai-blood-sweat-and-tears-loading-data-to-pinecone"
date: "2025-05-07T22:10:37+00:00"
lastmod: "2025-05-07T22:10:39+00:00"
description: "The task to load some JSON data to Pinecone was much more arduous than I had anticipated, but I learned a lot along the way!"
authors:
  - "jennifer-reif"
image: "unsplash-hard-labor-scaled.jpg"
categories:
  - "Databases"
  - "Java"
  - "Java Beginner"
  - "Spring"
tags:
related_posts:
  - "breaktime-tech-talks-ep37-vector-database-frustration-microsoft-lazygraphrag"
  - "building-a-fullstack-imdb-clone-with-a-java-backend-using-sparkjava-and-neo4j"
  - "journeys-in-java-level-3-building-an-empire-of-microservices"
  - "journeys-in-java-level-5-building-an-empire-of-microservices"
enlighterjs: true
frozen: false
---

As someone who is pretty familiar with relational and graph databases, I wanted to dig a little deeper into vector databases and understand the strengths and quirks they bring to the database table. I put together a conference abstract on vector RAG versus GraphRAG which got picked up, so I went to work building a demo and learning all I could.

I pivoted a few times along the way, but ended up with a Spring AI application that connects to both Pinecone (vector database) and Neo4j (graph database). There were a few surprises throughout, which I'll be sure to mention. Let's get started!

Getting started {#_getting_started}
-----------------------------------

The first decision I had to tackle was how to get data into Pinecone. My final Pinecone data load solution is available in the [vectordb-data-load code repository in Github](https://github.com/JMHReif/vectordb-data-load).

I previously played with a [book recommendations data set](https://github.com/JMHReif/graph-demo-datasets/tree/main/goodreadsUCSD) for a GenAI application, so that seemed a solid starting point.

After spending a bit of time exporting a smaller set of that data from Neo4j to JSON files, I started looking at how to get that data into Pinecone. I spun up a free tier instance on Pinecone's cloud database-as-a-service, which was pretty easy and painless.

Here's where the "fun" began. The first surprising hurdle was simply connecting to Pinecone.

Issue #1: APIs, SDKs, and rapid change {#_issue_1_apis_sdks_and_rapid_change}
-----------------------------------------------------------------------------

Many of the issues and hours spent troubleshooting arose from a recent API change in Pinecone and lack of documentation updates across the board (integrations for Spring AI and Neo4j APOC). The process went something like this:

1. I started with the Pinecone documentation, which I found has a [Java SDK](https://docs.pinecone.io/reference/java-sdk)! I worked on a JBang script that uses the Pinecone Java SDK to create an index and load data. But...the Pinecone Java SDK does not have near the functionality of the Python SDK. The biggest missing pieces are that the Java one doesn't support [creating an integrated index](https://docs.pinecone.io/reference/api/2025-01/control-plane/create_for_model) (creates vectors with Pinecone-hosted model) nor [upserting text](https://docs.pinecone.io/reference/api/2025-01/data-plane/upsert_records) (intake text, create the vector, and insert it). I'd have to create the vector, save it somewhere, and upsert the data.
2. Next was Spring Boot, which integrates with Pinecone and should give me more flexibility. But, I struggled to get the app to connect. Pinecone recently updated their console to show only the instance URI, which didn't match the Spring AI documentation nor Pinecone docs. I couldn't find any content that reflected the latest requirements, but found out the Neo4j APOC library can interact with Pinecone! That seemed a simpler approach.
3. The Neo4j APOC library contains several procedures for connecting to Pinecone, but I struggled with similar issues in aligning the expectations of Pinecone and APOC connection config. It wasn't clear how to provide the API key (**p.s.** it's provided as a `header` key in config param), but then the API path has changed from what was implemented in APOC. After digging through APOC's code ([open source repo](https://github.com/neo4j-contrib/neo4j-apoc-procedures)), I couldn't customize the connection string without rewriting the source code.

Since I didn't immediately have time for making library changes, submitting PRs, and waiting for approvals/release, I pivoted back to Spring AI. The next step was to figure out the config pieces and assemble them.

Issue #2: Config {#_issue_2_config}
-----------------------------------

I was back to sorting out the connection string components. Pinecone shows a URI in the console, but Spring AI uses an assembly of index name, environment, and region. I had to reference a few pieces of old Pinecone docs, older community forum posts, and old-fashioned trial and error before I finally discovered the correct arrangement of puzzle pieces I needed to connect the app to Pinecone. The format is as follows: [https://\<indexName\>-\<projectId\>.svc.\<environment\>.pinecone.io](https://<indexName>-<projectId>.svc.<environment>.pinecone.io).

What was so confusing about this is that Pinecone uses generated text for each bit, so it isn't clear what the index name, project ID, and environment are just from looking at the string. You had to know what to look for. As an example, the URI might look like this: <https://example-index-ljwer.svc.nre-4520-jknw.pinecone.io>. The index name is `example-index`, the project ID is `ljwer`, and the environment is `nre-4520-jknw`.

Then I had to map those pieces to the Spring AI config shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="properties">spring.ai.vectorstore.pinecone.index-name=example-index
spring.ai.vectorstore.pinecone.project-id=ljwer
spring.ai.vectorstore.pinecone.environment=nre-4520-jknw
spring.ai.vectorstore.pinecone.api-key=&lt;VALUE_HERE&gt;</pre>

Now that I was connected, I tackled the next challenge: loading data into Pinecone.

Issue #3: JSON format {#_issue_3_json_format}
---------------------------------------------

Another confusion was understanding how to work with data in a valid, but unusual, format called [JSON lines](https://jsonlines.org/). Instead of placing items of a list in brackets and each separated by a comma, JSON lines uses a newline character to separate each item.

Here is a sample:

<pre class="EnlighterJSRAW" data-enlighter-language="json">{"rating":5,"book_id":"676924","id":"853d3b4f5c9e8aa57a65c2ccc2e7328c","text":"LOVED IT!"}
{"rating":5,"book_id":"29780253","id":"8a3aaef2c2a7b3c5202046d4ce0f633d","text":"Trevor Noah is basically the best person ever. So touching and motivating; and always funny."}
{"rating":3,"book_id":"20613518","id":"3ab7d51c2e977169b0bc1deeeb7e7db7","text":"Great premise; poor execution."}</pre>

I ended up with this because I exported a portion of the data from a Neo4j database, and that's the format it streams. I had to use some regex magic to strip out escape characters using this command: `sed 's/\\"/"/g'` and get the data shown above.

Many forums have responses that say this is invalid JSON, though after finding the official JSON lines site and documentation, I know that's inaccurate. The helpful thing with this format is that reading the JSON does not try to suck up a large array and blow up application memory. The downside is that you have to read each line separately and operate on it, which adds a bit more processing logic than what I had hoped.

You can follow some of the debugging steps I took by looking at my [`/json-limit1`](https://github.com/JMHReif/vectordb-data-load/blob/main/src/main/java/com/jmhreif/vectordb_data_load/ReviewController.java#L33) and [`/json-limit3`](https://github.com/JMHReif/vectordb-data-load/blob/main/src/main/java/com/jmhreif/vectordb_data_load/ReviewController.java#L42) endpoints in the code repo's `ReviewController` class. I tested a few different formats before figuring out what worked and why.

Here is how I ended up reading the JSON lines in Java ([`/load` endpoint](https://github.com/JMHReif/vectordb-data-load/blob/main/src/main/java/com/jmhreif/vectordb_data_load/ReviewController.java#L50)):

<pre class="EnlighterJSRAW" data-enlighter-language="java">@GetMapping("/load")
public String load() throws IOException {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(
            new ClassPathResource("goodreads-reviews-demo.json")
                .getInputStream()));

    String currentLine;
    List&lt;Document&gt; documents = new ArrayList&lt;&gt;();

    while ((currentLine = br.readLine()) != null) {
        Review review = objectMapper
            .readValue(currentLine, Review.class);

        if (review.text() != null) {
            //decide which keys are the metadata
            //add review to list of documents
        }
    }
    br.close();
    //upsert to Pinecone
    return "Documents read";
}</pre>

With the JSON format understood and readable in the Spring Boot app, I could move on to manipulating the data into the format I needed to upsert to Pinecone!

Issue #4: Loading data to Pinecone {#_issue_4_loading_data_to_pinecone}
-----------------------------------------------------------------------

There were a couple of puzzles to solve for this part. 1. Determining how the JSON should map to data for Pinecone. 2. Figuring out how to split and batch such a large amount of data. 3. Adjusting configuration for longer timeout.

For this section, the [ETL pipeline page of the Spring AI documentation](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html) was super helpful in understanding what I needed and what it should look like.

### 1. Metadata keys {#_1_metadata_keys}

Vector databases were new, and I knew that they optimize for the vector embedding plus a bit of metadata (in key/value pairs). I had to decide which keys in the JSON should be metadata and which were irrelevant for my use case.

Because I want embeddings for the review text, I need the text value. Any other values need to be nested into a `metadata` key. Since the embedding is on the reviews but my use case is book recommendations (input of reviews, output of books), I need the `book_id` to know which book to recommend, and `rating` could also be helpful for filtering out low-rated books. The `book_id` and `rating` values are what will be the nested map for metadata.

I ended up with the following data mapping as a list of documents:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@GetMapping("/load")
public String load() throws IOException {
    //reader code
    List&lt;Document&gt; documents = new ArrayList&lt;&gt;();

    while ((currentLine = br.readLine()) != null) {
        Review review = objectMapper.readValue(currentLine, Review.class);

        if (review.text() != null) {
            documents.add(new Document(review.text(),
                        Map.of("book_id", review.book_id(),
                            "rating", review.rating())));
        }
    }
    br.close();
    //upsert to Pinecone
    return "Documents read";
}</pre>

Next, I needed to upsert the documents to Pinecone. I started by just trying to upload as-is, but quickly figured out that some of the reviews were too long and needed to be split up (text larger than context window of embedding model).

Thankfully, there were several examples of this, though I had add batching because I have so many reviews being loaded (almost 70k)!

<pre class="EnlighterJSRAW" data-enlighter-language="java">@GetMapping("/load")
public String load() throws IOException {
    //code to read and format documents
    br.close();

    TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
    List&lt;Document&gt; splitDocuments = tokenTextSplitter.apply(documents);

    int batchSize = 100;
    List&lt;List&lt;Document&gt;&gt; batches = Lists.partition(
            splitDocuments, batchSize);
    int i = 1;
    for (List&lt;Document&gt; batch : batches) {
        vectorStore.add(batch);
        System.out.println("Batch " + i + " of " + batches.size());
        i++;
    }
    return "Documents loaded";
}</pre>

I fired up the application, and it started processing, but errored out close to the end. \*sigh\* After some digging, I found that the default timeout for Pinecone is 30 seconds, and I probably exceeded that. I modified a [property in the `application.properties` file](https://github.com/JMHReif/vectordb-data-load/blob/main/src/main/resources/application.properties#L10) to increase the timeout to 60 seconds, and that did the trick.

Running again took a bit of time, but completed successfully, and I was able to check the Pinecone console to see that the data was loaded!

Alternative embeddings - Book descriptions {#_alternative_embeddings_book_descriptions}
---------------------------------------------------------------------------------------

I also toyed with loading the book descriptions as a separate index (see [`BookController` class](https://github.com/JMHReif/vectordb-data-load/blob/main/src/main/java/com/jmhreif/vectordb_data_load/BookController.java)). I haven't played with that in an application yet, but I did get the data loaded. I used the same process as above, but had to do a bit fancier mapping strategy because some of the `isbn` and `isbn13` values were null in the JSON file, and a regular map doesn't allow nulls.

Here is the code I used to map the book JSON to Spring AI documents:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Map metadata = Maps.newHashMap();

if (book.text() != null) {
    metadata.put("title", book.title());
    metadata.put("rating", book.average_rating());
    metadata.computeIfAbsent("isbn", k -&gt; book.isbn());
    metadata.computeIfAbsent("isbn13", k -&gt; book.isbn13());

    documents.add(new Document(book.title()+book.text(), metadata));
}</pre>

The [`computeIfAbsent` method](https://www.baeldung.com/java-map-putifabsent-computeifabsent) is a nice way to only add the key when the value is not null (when it exists).

Wrapping up! {#_wrapping_up}
----------------------------

The task to load some JSON data to Pinecone was much more arduous than I had anticipated, but I learned a lot along the way. I also realized that, no matter what database you choose, the data import steps can be complicated.

My ultimate solution was a Spring AI application that reads a JSON file, creates vector embeddings, and upserts data to Pinecone.

Happy coding!

Resources {#_resources}
-----------------------

* Github repository: [Accompanying code for this blog post](https://github.com/JMHReif/vectordb-data-load)
* Documentation: [Pinecone Java SDK](https://docs.pinecone.io/reference/java-sdk)
* Guide: [Pinecone upsert data](https://docs.pinecone.io/guides/data/upsert-data)
* API Reference: [Pinecone upsert text](https://docs.pinecone.io/reference/api/2025-01/data-plane/upsert_records)
* Documentation: [Spring AI Pinecone configuration properties](https://docs.spring.io/spring-ai/reference/api/vectordbs/pinecone.html#_configuration_properties)
* Documentation: [Spring AI ETL pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
