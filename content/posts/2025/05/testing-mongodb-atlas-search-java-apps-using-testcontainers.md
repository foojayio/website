---
title: "Testing MongoDB Atlas Search Java Apps Using TestContainers"
slug: "testing-mongodb-atlas-search-java-apps-using-testcontainers"
date: "2025-05-29T18:07:27+00:00"
lastmod: "2025-05-29T18:08:18+00:00"
description: "A few examples of how you can use the awesome TestContainers projects to enhance testing of your MongoDB Atlas Search Java apps."
authors:
  - "luke-thompson"
image: "/images/posts/2025/05/testing-mongodb-atlas-search-java-apps-using-testcontainers/mongodb-lucene-1.png"
categories:
  - "Databases"
  - "Mongo"
  - "Testcontainers"
tags:
related_posts:
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "exploring-java-records-in-a-jakarta-ee-context"
  - "five-apache-projects-you-probably-havent-heard-of-yet"
enlighterjs: true
frozen: false
---

This will be an exploration of testing MongoDB Atlas Search solutions written in Java using TestContainers and JUnit5. We'll start simple and build up to more advanced uses which load seed data and provide an environment for consistent and easy to maintain tests.

TLDR; if (like me!) you want to get straight to the code rather than reading lots of tedious words:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">git clone https://github.com/luketn/mongodb-atlas-local-testcontainers.git
cd mongodb-atlas-local-testcontainers
mvn test</pre>

What is MongoDB Atlas Search, anyway? {#h2-0-what-is-mongodb-atlas-search-anyway}
---------------------------------------------------------------------------------

MongoDB Atlas Search is an extension to the built-in indexing capabilities that are part of MongoDB itself, using the awesome open source indexing and query library [Lucene](https://lucene.apache.org/). MongoDB has built a wrapper around Lucene called [mongot](https://www.mongodb.com/docs/atlas/atlas-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant#fts-architecture/).

Mongot has two responsibilities:

* First, it follows the change stream of any collection you choose to index and builds Lucene indexes asynchronously.
* Second, when you run the $search aggregation stage in a MongoDB query, mongot will be invoked to perform a Lucene query on the index and return a stream of document ids for further processing and data retrieval.

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdH3ab-XATCrN5-LLJCvpbECaU-yIa91oAvqb0TmfmYlyEgp--tM6m3g11X-gZBA2zFm82mkBt1Rv1OCFgsgQqkTYplnfzTCqSvYhKsR1_qn5zWgA-eJWccH3QxMBg_XVCtg4VOTg?key=xtuklWIXthgOT-_RVHPDsg)

Lucene and MongoDB Atlas Search support many index types which are very different to the excellent, efficient, and super-fast [b-tree based MongoDB indexes](https://www.mongodb.com/docs/manual/indexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant#details).

Some examples of the use-cases the new MongoDB Atlas Search indexes support are:

1. Incredible full-text search engines (à la Google)

* [Ranked](https://www.mongodb.com/docs/atlas/atlas-search/scoring/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) searching: best results returned first
* Many powerful query types like [phrase](https://www.mongodb.com/docs/atlas/atlas-search/phrase/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant), [wildcard](https://www.mongodb.com/docs/atlas/atlas-search/wildcard/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant), and [text](https://www.mongodb.com/docs/atlas/atlas-search/text/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant)
* [Fielded searching](https://www.mongodb.com/docs/atlas/atlas-search/queryString/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) (e.g., title, author, contents)
* Fast, memory-efficient, and typo-tolerant [suggesters](https://www.mongodb.com/docs/atlas/atlas-search/highlighting/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant)
* [Highlighting](https://www.mongodb.com/docs/atlas/atlas-search/highlighting/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) of terms matched in text

2. High performance, multi-index search

Because of the inverted index style, and the efficient storage used in Lucene, [multiple indexes](https://www.mongodb.com/docs/atlas/atlas-search/compound/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) can be searched at once with extremely high performance and parallelism.

This overcomes a major limitation in traditional MongoDB indexes that the query planner will almost never use more than one index.

This is one of the highest value features in MongoDB Atlas Search, and one of the main motivations for me when I started using it.

3. Amazon.com-style user interfaces with counted and grouped categories (facets)

[Facets](https://www.mongodb.com/docs/atlas/atlas-search/facet/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) are an amazing feature of MongoDB Atlas Search. Because of the speed of the search engine and the way it performs queries, it can group and count tokenised or numeric indexed fields alongside all the other search types it is performing.

This allows you, for example, to build an Amazon.com-style tree of categories to the left of your search results page, which shows the number of results that *would* be present if that category were applied as a filter.

4. Artificial intelligence (AI) retrieval-augmented generation (RAG) using vectors

* Nearest-neighbor search for high-dimensionality [vectors](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant)

MongoDB Atlas Search surfaces these Lucene capabilities in a familiar MongoDB aggregate stage syntax, and allows you to create amazing and powerful applications built on this super fast and flexible search engine.

MongoDB Atlas Search is an Atlas-cloud hosted service which MongoDB automatically maintains for you and runs alongside your database or on dedicated search nodes in your cluster.

Local development and testing with MongoDB Atlas Search {#h2-1-local-development-and-testing-with-mongodb-atlas-search}
-----------------------------------------------------------------------------------------------------------------------

If you are building an application or service based on MongoDB Atlas Search, a crucial part of the developer experience is how to debug and test locally.

Seeing this community need, MongoDB has packaged mongot and mongod up in an awesome Docker container: [MongoDB Atlas Local](https://www.mongodb.com/docs/atlas/cli/current/atlas-cli-deploy-docker/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant). You can find the container (mongodb/mongodb-atlas-local) on [DockerHub](https://hub.docker.com/r/mongodb/mongodb-atlas-local).

You can run this container on your machine, and then build and experiment with all the features locally.

In addition, if you are using Java, you can write unit tests using the awesome unit test support provided for [MongoDB and MongoDB Atlas Local containers](https://java.testcontainers.org/modules/databases/mongodb/#mongodbatlaslocalcontainer) in the TestContainers project.

### What's TestContainers? {#h3-2-what-s-testcontainers}

TestContainers is a handy unit testing library that helps you write integration tests by managing containerised versions of services you might use in your code. A few things it does for you are it:

* Automatically manages containers for unit test contexts.
* Runs consistently in any environment---e.g., locally, on CI/CD like GitHub Actions with minimal or no config.
* Manages port conflicts dynamically and provides system-unique connection strings.
* Cleans up after each scope for a consistent environment every test run.

Even though we're going to be using the Java TestContainers project here, TestContainers is available for lots of languages and platforms. You can check it out on [their website](https://testcontainers.com/).

Let's write some code! {#h2-3-let-s-write-some-code}
----------------------------------------------------

We'll build a simple Java data access layer with unit tests, then gradually add features and more comprehensive tests as we go.

### Simple CRUD data access and unit tests {#h3-4-simple-crud-data-access-and-unit-tests}

Here's a simple DataAccess class in Java, using the awesome Java record immutable data type Person. It has simple CRUD methods:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mycodefu;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class PersonDataAccess implements AutoCloseable {
   private final MongoClient mongoClient;
   private final MongoCollection&lt;Person&gt; collection;

   public record Person(
           @BsonId
           @BsonRepresentation(BsonType.OBJECT_ID)
           String id,
           String name,
           int age,
           String job,
           String bio
   ) {
       public static Person of(String name, int age, String job, String bio) {
           return new Person(null, name, age, job, bio);
       }
   }

   public PersonDataAccess(String connectionString) {
       this.mongoClient = MongoClients.create(connectionString);
       this.collection = this.mongoClient.getDatabase("examples").getCollection("person", Person.class);
   }

   public String insertPerson(Person person) {
       InsertOneResult insertOneResult = this.collection.insertOne(person);
       return Objects.requireNonNull(insertOneResult.getInsertedId()).asObjectId().getValue().toHexString();
   }

   public Person getPerson(String id) {
       return this.collection.find(eq("_id", new ObjectId(id))).first();
   }

   public void updatePerson(Person person) {
       this.collection.replaceOne(eq("_id", new ObjectId(person.id())), person);
   }

   public void deletePerson(String id) {
       this.collection.deleteOne(eq("_id", new ObjectId(id)));
   }

   @Override
   public void close() {
       this.mongoClient.close();
   }
}</pre>

So let's see how we'd use TestContainers and JUnit5 to unit test this class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mycodefu;

import com.mycodefu.PersonDataAccess.Person;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PersonDataAccessTest {

   @Container
   private static final MongoDBAtlasLocalContainer mongoDBContainer = new MongoDBAtlasLocalContainer("mongodb/mongodb-atlas-local:8.0.5");
   @AutoClose
   private static PersonDataAccess personDataAccess;

   @BeforeAll
   static void beforeAll() {
       personDataAccess = new PersonDataAccess(mongoDBContainer.getConnectionString());
   }

   @Test
   void shouldInsertAndRetrievePerson() {
       // Given
       Person person = Person.of(
               "John Doe",
               30,
               "Software Developer",
               "John is a software developer who loves to code."
       );

       // When
       String id = personDataAccess.insertPerson(person);
       Person retrievedPerson = personDataAccess.getPerson(id);

       // Then
       assertNotNull(id);
       assertNotNull(retrievedPerson);
       assertEquals(id, retrievedPerson.id());
       assertEquals("John Doe", retrievedPerson.name());
       assertEquals(30, retrievedPerson.age());
       assertEquals("Software Developer", retrievedPerson.job());
       assertEquals("John is a software developer who loves to code.", retrievedPerson.bio());
   }

   @Test
   void shouldUpdatePerson() {
       // Given
       Person person = Person.of(
               "Jane Smith",
               25,
               "Data Scientist",
               "Jane is a data scientist who loves to analyze data."
       );
       String id = personDataAccess.insertPerson(person);

       // When
       Person updatedPerson = new Person(
               id,
               "Jane Smith",
               26,
               "Senior Data Scientist",
               "Jane is a senior data scientist who loves to analyze data."
       );
       personDataAccess.updatePerson(updatedPerson);
       Person retrievedPerson = personDataAccess.getPerson(id);

       // Then
       assertEquals(26, retrievedPerson.age());
       assertEquals("Senior Data Scientist", retrievedPerson.job());
   }

   @Test
   void shouldDeletePerson() {
       // Given
       Person person = Person.of(
               "Bill Lumbergh",
               40,
               "Manager",
               "Bill is a manager of tech teams. Often asks 'What's happening?'."
       );
       String id = personDataAccess.insertPerson(person);

       // When
       personDataAccess.deletePerson(id);
       Person retrievedPerson = personDataAccess.getPerson(id);

       // Then
       assertNull(retrievedPerson);
   }
}</pre>

There are a few really nice things to notice about the code:

*@TestContainers + @Container Annotations*   

Classes annotated with @TestContainers will look for @Container annotated fields in order to start up our container before the class, and tear it down again afterwards. This gives you a nice instance of MongoDB Atlas Local which only exists while the tests in this class are running.

*@AutoClose Annotation*   

Because our PersonDataAccess class implements AutoCloseable, adding this annotation guarantees the class will have its Close() method called after the unit tests are run to close the MongoDB client connection cleanly.

After that, the tests are standard JUnit tests, which set up preconditions, perform an operation, and assert postconditions in the Given/When/Then style.

### MongoDB Atlas Search with seed data and index wait {#h3-5-mongodb-atlas-search-with-seed-data-and-index-wait}

Alright, let's get into MongoDB Atlas Search!

We're going to extend our PersonDataAccess CRUD class with a new search() method, and then see how we can seed some data into the database and initialise a search index for us to test it on.

Let's write the tests first!

We'll create a new test class, PersonDataAccessSearchTest, for testing the MongoDB Atlas Search features of the data access. This is important because the CRUD tests affect the data in the collection and set up a race condition between their inserts and the MongoDB Atlas Search index. We want a consistent index to search against, otherwise our tests could be flaky.

Let's write some code to add some test data before the tests run, and create a MongoDB Atlas Search index over the test data. This time, we'll put a bit more text in the bio of the example people, and insert them upfront so we can play with the text search capabilities (thanks, [Phi 4](https://techcommunity.microsoft.com/blog/aiplatformblog/introducing-phi-4-microsoft%E2%80%99s-newest-small-language-model-specializing-in-comple/4357090), for the bios!).

Create a stub method in the data access class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Person&gt; findPersonByBio(String query) {return List.of();}</pre>

Then, we'll add the test class to invoke the stub method and test our assertions about what it should do:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mycodefu;

import com.mongodb.client.ListSearchIndexesIterable;
import com.mycodefu.PersonDataAccess.Person;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PersonDataAccessSearchTest {

   @Container
   private static final MongoDBAtlasLocalContainer mongoDBContainer = new MongoDBAtlasLocalContainer("mongodb/mongodb-atlas-local:8.0.5");
   @AutoClose
   private static PersonDataAccess personDataAccess;

   @BeforeAll
   static void beforeAll() {
       System.out.println("Initializing data access with MongoDB connection string: " + mongoDBContainer.getConnectionString());
       personDataAccess = new PersonDataAccess(mongoDBContainer.getConnectionString());

       //insert a few records for testing
       personDataAccess.insertPerson(Person.of("Miss Scotty Leffler", 32, "farmer", "At 32, Miss Scotty Leffler is a dedicated farmer known for her innovative approaches to sustainable agriculture on her family-owned farm. Passionate about environmental stewardship, she combines traditional farming methods with modern technology to enhance crop yield and soil health."));
       personDataAccess.insertPerson(Person.of("Raymon Wehner", 27, "dental hygienist", "At just 27 years old, Raymon Wehner is an accomplished dental hygienist dedicated to promoting oral health through comprehensive patient education and preventative care practices. With a passion for community outreach, Raymon frequently volunteers at local schools to teach children about the importance of maintaining good dental hygiene habits from an early age."));
       personDataAccess.insertPerson(Person.of("Miss Steve Rempel", 22, "businessman", "At just 22 years old, Miss Steve Rempel has already made a significant mark as an innovative entrepreneur with a keen eye for emerging market trends and opportunities. Her dynamic approach to business is characterized by her ability to adapt quickly and lead diverse teams towards achieving ambitious goals, establishing herself as a rising star in the entrepreneurial landscape."));
       personDataAccess.insertPerson(Person.of("Dustin Schinner", 45, "engineer", "At 45, Dustin Schinner is an accomplished engineer with over two decades of experience in innovative design and sustainable technology development. Known for his forward-thinking approach, he has led numerous successful projects that integrate cutting-edge solutions to address modern engineering challenges."));
       personDataAccess.insertPerson(Person.of("Eartha Mosciski", 39, "window cleaner", "At 39, Eartha Mosciski has mastered the art of window cleaning, transforming ordinary buildings into sparkling showcases with her meticulous touch and eye for detail. Beyond just clearing away grime, she sees each pane as a canvas where light is artistically framed, bringing clarity and brightness to every view."));
       personDataAccess.insertPerson(Person.of("Jackqueline Osinski", 23, "astronomer", "At just 23 years old, Jacqueline Osinski is making waves as an innovative astronomer dedicated to exploring the mysteries of distant galaxies. Her cutting-edge research on dark matter distribution has already earned her recognition in the scientific community and promises to reshape our understanding of the cosmos."));
       personDataAccess.insertPerson(Person.of("Richard Ortiz II", 55, "lecturer", "Richard Ortiz II, at 55, is an esteemed lecturer renowned for his engaging teaching style and profound knowledge in his field of expertise. With years of experience shaping the minds of students across various disciplines, he continues to inspire through innovative educational approaches and a passion for lifelong learning."));
       personDataAccess.insertPerson(Person.of("Brenton Bergstrom", 50, "bookkeeper", "At 50, Brenton Bergstrom is a seasoned bookkeeper with over two decades of experience ensuring the financial accuracy and integrity of businesses. Known for his meticulous attention to detail and dedication to precision, he plays a vital role in helping companies maintain their fiscal health and compliance."));
       personDataAccess.insertPerson(Person.of(
               "Carroll Ankunding",
               39,
               "travel agent",
               "At 39, Carroll Ankunding is an experienced travel agent who combines her passion for exploration with a knack for crafting unforgettable journeys for clients. With nearly two decades of industry experience, she excels in tailoring personalized travel experiences that cater to the unique desires and needs of each traveler."));

       personDataAccess.collection.createSearchIndex("person_search", BsonDocument.parse("""
               {
                  "mappings": {
                    "dynamic": false,
                    "fields": {
                      "name": {
                          "type": "string",
                          "analyzer": "lucene.standard"
                      },
                      "age": {
                        "type": "number",
                        "representation": "int64",
                        "indexDoubles": false
                      },
                      "job": [
                        {
                          "type": "token"
                        },
                        {
                          "type": "stringFacet"
                        }
                      ],
                      "bio": {
                          "type": "string",
                          "analyzer": "lucene.standard"
                      }
                    }
                  }
                }
               """));

       Instant startTime = Instant.now();
       Awaitility.await()
               .atMost(10, TimeUnit.SECONDS)
               .until(() -&gt; {
                   ListSearchIndexesIterable&lt;Document&gt; searchIndexes = personDataAccess.collection.listSearchIndexes();
                   Document personIndex = searchIndexes.into(new ArrayList&lt;&gt;()).stream().filter(index -&gt; index.getString("name").equals("person_search")).findFirst().orElseThrow();
                   return personIndex.getString("status").equals("READY");
               });
       System.out.printf("Index created and ready in %dms%n", Duration.between(startTime, Instant.now()).toMillis());
   }

   @Test
   void shouldFindPersonByBioWord_dedicated() {
       // Given
       String word = "dedicated";

       // When
       List&lt;Person&gt; dedicatedPeople = personDataAccess.findPersonByBio(word);

       // Then
       assertEquals(3, dedicatedPeople.size());
       assertTrue(dedicatedPeople.stream().allMatch(person -&gt; person.bio().contains(word)));
   }

   @Test
   void shouldFindPersonByBioWord_fuzzy_yesr() {
       // Given year (with a typo)
       String word = "yesr";

       // When fuzzy searched
       List&lt;Person&gt; yearPeople = personDataAccess.findPersonByBio(word);

       // Then match bios with 'year', or 'years'
       assertEquals(4, yearPeople.size());
       assertTrue(yearPeople.stream().allMatch(person -&gt; person.bio().contains("year")));

       //find the surrounding words and print them
       yearPeople.forEach(person -&gt; {
           String bio = person.bio();
           int index = bio.indexOf("year");
           int start = Math.max(0, index - 20);
           int end = Math.min(bio.length(), index + word.length() + 20);
           String surroundingYear = bio.substring(start, end);
           System.out.println(surroundingYear);
       });
   }
}</pre>

OK, so now we have 10 people in the database before the tests run, and we've created an Atlas search index with some different field types over each of the Person document fields.

Let's explain what each index field type means, and what we can do with it:

* Name + bio fields `{ type: 'string', analyzer: 'lucene.standard' }`
* The name and bio fields have an index type of string. Don't let appearances fool you---this is the most complex type of index in MongoDB Atlas Search/Lucene. This index type tokenizes (splits) the strings in these fields into a list of terms using an analyzer. Here, we're explicitly calling out the [default analyzer](https://www.mongodb.com/docs/atlas/atlas-search/analyzers/standard/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant), which splits on non-character boundaries, lower-casing (to ignore case), ignores stop words like *in* , *and* , and *the*, and handles acronyms and email addresses. This leaves the index with a nice ordered term list which is super fast to search and has ordinal indexes to the documents.
* Age `{"type": "number", "representation": "int64", "indexDoubles": false}`
* The age field is an integer number, and it will [allow searching by ranges, equality, or faceting](https://www.mongodb.com/docs/atlas/atlas-search/field-types/number-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant). Very flexible and handy!
* Job `[{ type: 'token' }, { type: 'stringFacet' }]`
* This is an interesting one. We're telling MongoDB Atlas Search to index this field twice---in both cases, not to split the string up as [tokens](https://www.mongodb.com/docs/atlas/atlas-search/field-types/token-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) but to treat it as a single value. However, in the first case, we want an index for equality matching, and in the second, for [faceting](https://www.mongodb.com/docs/atlas/atlas-search/field-types/string-facet-type/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant).

So far, we have inserted some seed data using the data access, and created a MongoDB Atlas Search index.

We also used the nice [awaitility](https://github.com/awaitility/awaitility) library to await the READY status on the MongoDB Atlas Search index. This is important because you can't use the index until it has fully indexed all the data. In this case, we are inserting the data first and then adding the index, so once it is READY, we can be sure all the data can be searched.

At this point, our tests are expected to fail---let's go implement the `findPersonByBio()` method to bring them to green.

We'll use a fuzzy text search on the bio field in `PersonDataAccess`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Person&gt; findPersonByBio(String query) {
   //use Atlas Search to find a person by their bio
   List&lt;Bson&gt; aggregateStages = List.of(
           Aggregates.search(
                   SearchOperator
                           .text(fieldPath("bio"), query)
                           .fuzzy(FuzzySearchOptions
                                   .fuzzySearchOptions()
                                   .maxEdits(2)
                                   .prefixLength(2)
                                   .maxExpansions(50)
                           )
           , SearchOptions.searchOptions().index("person_search"))
   );

   if (log.isTraceEnabled()) {
       for (Bson aggregateStage : aggregateStages) {
           log.trace(aggregateStage.toBsonDocument().toJson(JsonWriterSettings.builder().indent(true).build()));
       }
   }

   ArrayList&lt;Person&gt; results = collection.aggregate(aggregateStages, Person.class).into(new ArrayList&lt;&gt;());

   if (log.isTraceEnabled()) {
       for (Person result : results) {
           log.trace(result.toString());
       }
   }

   return results;
}</pre>

You can see a couple of nice capabilities of MongoDB Atlas Search demonstrated with regular match and fuzzy matches on string indexed fields.

The options we are using there control just how fuzzy our fuzzy text search can be:

`maxEdits`: Maximum number of single-character edits required to match the specified search term. Value can be 1 or 2. The default value is 2. Uses [Damerau-Levenshtein](https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance) distance.

`prefixLength`: This is a key one. I feel like if the term doesn't even start with what you typed, it sometimes feels pretty inaccurate. It refers to the number of characters at the beginning of each term in the result that must exactly match. The default value is 0.

`maxExpansions`: Maximum number of variations to generate and search for. This limit applies on a per-token basis. The default value is 50.

You can experiment with these levers to see what kind of results you get with your data set and whether they make sense.

Hopefully, you (like me!) now have green ticks and passing tests. Very satisfying.

I renamed my original test class PersonDataAccessCRUDTest and can run them both together. You can see they each get their own independent container and set of test data, giving good isolation of test cases and seed data.

### Advanced seed data loading: MongoDB Database Tools {#h3-6-advanced-seed-data-loading-mongodb-database-tools}

Finally, we'll use a couple of more advanced TestContainers methods to load a more significant amount of seed data and run commands on the database.

The Atlas Local container includes all of the [MongoDB Database Tools](https://www.mongodb.com/docs/database-tools/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant) and the [mongo shell](https://www.mongodb.com/docs/mongodb-shell/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant). The key utilities for us are:

| mongorestore |     Restores data from a mongodump database dump into a mongod or mongos     |
|--------------|------------------------------------------------------------------------------|
| mongoimport  | Imports content from an Extended JSON, CSV, or TSV export file               |
| mongosh      | A client for MongoDB, including the ability to run ad-hoc scripts from files |

Why does that matter for us? Seed data loading!

TestContainers has some awesome options which we can use here for loading seed data---mounting directories and executing commands within the container.

In our previous example, we manually created 10 records for testing search... What if we wanted to test against 15,000 records?

#### Mounting directories

You can mount directories to the container in two ways: by a [resource class path](https://www.javadoc.io/doc/org.testcontainers/testcontainers/1.10.6/org/testcontainers/containers/GenericContainer.html#withClasspathResourceMapping-java.lang.String-java.lang.String-org.testcontainers.containers.BindMode-) or by a [file system directory path](https://www.javadoc.io/doc/org.testcontainers/testcontainers/1.10.6/org/testcontainers/containers/GenericContainer.html#withFileSystemBind-java.lang.String-java.lang.String-org.testcontainers.containers.BindMode-).

This is just like making a volume mapping when running a Docker container at the command line like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run --rm -it -v $(pwd):/tmp/local -w /tmp/local --entrypoint bash mongodb/mongodb-atlas-local</pre>

To mount a resource directory, we can just add a call to withClasspathResourceMapping to the container instance we are constructing for unit tests:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Container
private static final MongoDBAtlasLocalContainer mongoDBContainer = new MongoDBAtlasLocalContainer("mongodb/mongodb-atlas-local:8.0.5")
       .withClasspathResourceMapping(
               "/seed-data",
               "/tmp/seed-data",
               BindMode.READ_WRITE
       );</pre>

So once this container is running, the resource files under the directory /seed-data will be mounted within the container under the path /tmp/seed-data.

#### Running tools

Let's assume we had a JSON file *documents.jsonl* in that resources location. We could then run mongoimport to import it:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongoDBContainer.execInContainer(ExecConfig.builder()
       .workDir("/tmp/seed-data")
       .command(toArray("mongoimport", "-d", "examples", "-c", "person", "documents.jsonl"))
       .build());</pre>

#### Running Mongo Shell scripts

You can also run Mongo Shell scripts which can be helpful for performing little maintenance tasks, applying indexes, or other tasks.

Mongo Shell supports eval to directly execute a command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongoDBContainer.execInContainer(ExecConfig.builder()
       .workDir("/tmp/seed-data")
       .command(toArray("mongosh", "--eval", "db.getSiblingDB('examples').person.insert({'test': '123'})"))
       .build());</pre>

Or you can pass a JavaScript file to execute:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongoDBContainer.execInContainer(ExecConfig.builder()
       .workDir("/tmp/seed-data")
       .command(toArray("mongosh", "-f", "atlas-index-utils.js"))
       .build());</pre>

#### Running Shell scripts

If you want, you can also execute a bash script like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongoDBContainer.execInContainer(ExecConfig.builder()
       .workDir("/tmp/seed-data")
       .command(toArray("bash", "seed-data.sh"))
       .build());</pre>

### Loading a *mongodump* BSON database and index {#h3-7-loading-a-mongodump-bson-database-and-index}

Let's create some seed data and MongoDB Atlas Search indexes, which we'll store in a resource directory /seed-data.
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdlQGT-Gx9KjXh62fBIazUC4WwMXa3Ti--NVjGkt3YqlpAsQoIpGl_rQiqtz7Db-xAgDfYx71au5tzv3yIO35ipy04k7I_f4wkepw2RoSM-qdeBF59W_0A1YbeOce3zcvzqzEiH_g?key=xtuklWIXthgOT-_RVHPDsg)

I've built a little test dataset of 15,000 Person documents in MongoDB and used mongodump to export it. Just for fun, I used a [local LLM](https://github.com/luketn/generate-person-data) to generate it.

If you want these files, you can get the seed data files from the [examples folder on GitHub](https://github.com/luketn/mongodb-atlas-local-testcontainers/tree/main/advanced-search-seed-data-test/src/test/resources/seed-data/dump/examples).

Now, we can change our MongoDB Atlas Search test class to load up data from the resources:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mycodefu;

import com.mongodb.client.ListSearchIndexesIterable;
import com.mycodefu.PersonDataAccess.Person;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.ExecConfig;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import org.testcontainers.shaded.com.google.common.io.Resources;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.apache.commons.lang3.ArrayUtils.toArray;

@Testcontainers
class PersonDataAccessSearchTest {

   @Container
   private static final MongoDBAtlasLocalContainer mongoDBContainer = new MongoDBAtlasLocalContainer("mongodb/mongodb-atlas-local:8.0.5")
           .withClasspathResourceMapping(
                   "/seed-data",
                   "/tmp/seed-data",
                   BindMode.READ_WRITE
           );
   @AutoClose
   private static PersonDataAccess personDataAccess;

   @BeforeAll
   static void beforeAll() throws IOException, InterruptedException {
       System.out.println("Initializing data access with MongoDB connection string: " + mongoDBContainer.getConnectionString());
       personDataAccess = new PersonDataAccess(mongoDBContainer.getConnectionString());

       Instant startSeedDataRestore = Instant.now();
       mongoDBContainer.execInContainer(ExecConfig.builder()
               .workDir("/tmp/seed-data")
               .command(toArray("mongorestore", "--gzip"))
               .build());
       System.out.println("Loading seed data took: " + Instant.now().minusMillis(startSeedDataRestore.toEpochMilli()).toEpochMilli() + "ms");

       Instant startIndex = Instant.now();
       String personSearchMappings = Resources.toString(Resources.getResource("seed-data/dump/examples/person_search.json"), UTF_8);
       personDataAccess.collection.createSearchIndex("person_search", BsonDocument.parse(personSearchMappings));
       Awaitility.await()
               .atMost(10, TimeUnit.SECONDS)
               .until(() -&gt; {
                   ListSearchIndexesIterable&lt;Document&gt; searchIndexes = personDataAccess.collection.listSearchIndexes();
                   Document personIndex = searchIndexes.into(new ArrayList&lt;&gt;()).stream().filter(index -&gt; index.getString("name").equals("person_search")).findFirst().orElseThrow();
                   return personIndex.getString("status").equals("READY");
               });
       System.out.printf("Index created and ready in %dms%n", Duration.between(startIndex, Instant.now()).toMillis());
   }

   @Test
   void shouldFindPersonByBioWord_dedicated() {
       // Given
       String word = "dedicated";

       // When
       List&lt;Person&gt; dedicatedPeople = personDataAccess.findPersonByBio(word, false);

       // Then
       assertEquals(50, dedicatedPeople.size());
       assertTrue(dedicatedPeople.stream().allMatch(person -&gt; person.bio().contains(word)));
   }

   @Test
   void shouldFindPersonByBioWord_fuzzy_yesr() {
       // Given year (with a typo)
       String word = "yesr";

       // When fuzzy searched
       List&lt;Person&gt; yearPeople = personDataAccess.findPersonByBio(word, true);

       // Then match bios with 'year', or 'years'
       assertEquals(50, yearPeople.size());
       assertTrue(yearPeople.stream().allMatch(person -&gt; person.bio().contains("year")));

       //find the surrounding words and print them
       yearPeople.forEach(person -&gt; {
           String bio = person.bio();
           int index = bio.indexOf("year");
           int start = Math.max(0, index - 20);
           int end = Math.min(bio.length(), index + word.length() + 20);
           String surroundingYear = bio.substring(start, end);
           System.out.println(surroundingYear);
       });
   }
}</pre>

I also tweaked the data access class `findPersonByBio()` method to cope with a larger dataset:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public List&lt;Person&gt; findPersonByBio(String query, boolean fuzzy) {
   //use Atlas Search to find a person by their bio
   TextSearchOperator bioOperator = SearchOperator.text(fieldPath("bio"), query);
   if (fuzzy) {
       bioOperator = bioOperator
               .fuzzy(FuzzySearchOptions
                       .fuzzySearchOptions()
                       .maxEdits(2)
                       .prefixLength(2)
                       .maxExpansions(100)
               );
   }
   List&lt;Bson&gt; aggregateStages = List.of(
           Aggregates.search(
                   bioOperator
           , SearchOptions.searchOptions().index("person_search")),
           Aggregates.limit(50)

   );

   if (log.isTraceEnabled()) {
       for (Bson aggregateStage : aggregateStages) {
           log.trace(aggregateStage.toBsonDocument().toJson(JsonWriterSettings.builder().indent(true).build()));
       }
   }

   ArrayList&lt;Person&gt; results = collection.aggregate(aggregateStages, Person.class).into(new ArrayList&lt;&gt;());

   if (log.isTraceEnabled()) {
       for (Person result : results) {
           log.trace(result.toString());
       }
   }

   return results;
}</pre>

So now, we are loading our data (all 15,000 documents!) from a gzip'ed archive using the mongorestore utility on a mounted volume in the container. Then, we're loading another resource file with the MongoDB Atlas Search index mapping and creating the index over the loaded data.

Having the seed data external to the code is extremely flexible and useful.

For example, we can:

* Load seed data from a remote cloud storage (e.g., AWS simple storage service (S3)).
* Reuse seed data between test contexts.
* Use seed data on remote Atlas cloud databases.
* Use seed data on local containers outside of unit tests.

Having the mappings JSON external is also useful, for instance:

* Index validation: I like to have a piece of production code validate that when my app runs in production, the index mapping it is expecting is present on production. I do that by comparing the current production DB with the resource JSON. [Check out an example](https://github.com/luketn/mongodb-atlas-local-testcontainers/blob/eceef5416d8e359ecdb44010d0f44e4de363c5eb/advanced-search-seed-data-test/src/main/java/com/mycodefu/atlassearch/util/IndexValidator.java) of how you might do that. I recommend logging a warning rather than raising an error if there is a mismatch, to alert you to the difference whilst allowing for rollout of changes gradually.
* Local index creation: In new environments (e.g., starting a new local container to test with), you can use this index mapping to initialise the MongoDB Atlas Search indexes.
* Production index rollout: Using a source controlled index mapping and performing a (controlled) rollout ahead of changes to software which will rely on it makes sense.

Wrapping up {#h2-8-wrapping-up}
-------------------------------

So we've been through a few examples of how you can use the awesome TestContainers projects to enhance testing of your MongoDB Atlas Search Java apps.

I hope you find it useful. Feel free to reach out if you have questions through the comments. Happy coding!

### Further reading {#h3-9-further-reading}

Here are a few links for further reading:

* [TestContainers](https://testcontainers.com/) is an awesome project. For instance, you can try this one for mocking Amazon Web Services (AWS) with [LocalStack](https://java.testcontainers.org/modules/localstack/).
* [Build faceted full-text search APIs with Java](https://www.mongodb.com/developer/products/atlas/java-faceted-full-text-search-api/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant).
* I highly recommend [MongoDB University](https://learn.mongodb.com/learning-paths/mongodb-java-developer-path/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Testing+MongoDB+Atlas+Search+Java+Apps+Using+TestContainers&utm_term=megan.grant). There are excellent courses that cover MongoDB with Java generally, Atlas Search, and much more.
