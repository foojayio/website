---
title: "Language Learning Flashcard System - Part 1"
slug: "language-learning-flashcard-system-part-1"
date: "2026-03-17T16:39:58+00:00"
lastmod: "2026-03-24T17:17:50+00:00"
description: "In this post, we'll write a Java Spring Boot REST API backend application without any frontend, that will store flash cards and decks in MongoDB. In a second post, we'll add the SRS part and a functional React frontend to use our cards."
authors:
  - "diego-freniche"
image: "https://foojay.io/wp-content/uploads/2026/03/Screenshot-2026-03-10-at-2.22.35-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
  - "Tutorials"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
enlighterjs: true
frozen: false
---

My native language is Spanish. I've been learning (and butchering) English my whole life. But at some point, I felt confident with English and wanted to learn Japanese. Big mistake. This is a totally different beast: three writing systems (Hiragana ひらがな, Katakana カタカナ and Kanji 漢字), a completely different grammar, no relation whatsoever with European languages... I needed help. And tools to learn. And one of them is Space Repetition System based apps.

These apps are flashcard tools, where you have the front of a card with a question, and the answer is at the back. So you can write something like "Hola" in Spanish at the front and guess the meaning (Hello). And after flipping the card, you rate your answer: total failure, you need to study harder, it was OK, or it's too easy. Modern flashcard apps include a Spacial Repetition System, a way to retry the cards that you fail most often a, without completely leaving behind the ones you already know. It's a well-known system for language learning that helps with vocabulary memorization, although it's used for test preparation, study sessions, etc.

In this post, we'll write a Java Spring Boot REST API backend application without any frontend, that will store flash cards and decks in MongoDB. In a second post, we'll add the SRS part and a functional React frontend to use our cards.

****GitHub Repository: Complete SRS Flashcard App Code**** {#h2-0-github-repository-complete-srs-flashcard-app-code}
--------------------------------------------------------------------------------------------------------------------

If you want to follow along, but don't want to copy/paste code, you can get the app at: <https://github.com/mongodb-developer/srsapp>

****How to Create a Spring Boot Project with Spring Initializr**** {#h2-1-how-to-create-a-spring-boot-project-with-spring-initializr}
-------------------------------------------------------------------------------------------------------------------------------------

Let's start by creating our base, empty project in Spring Initializr. This is a web-based tool to create a Spring Boot project quickly. Head over to <https://start.spring.io/index.html> and select:

* Maven as project build system.
* Java as programming language.
* The latest version of Spring Boot from the 4.x.x available versions.
* As group I'll use com.mongodb.nimongo (I'm learning Japanese, 日本語, in roman letters read as *nihongo* , so I mixed *MongoDB* and *Nihongo*. Yes, if you have to explain a joke...)
* Artifact is srsapp. Name will also be srsapp.
* So the package name will be com.mongodb.nimongo.srsapp.
* We will use a YAML based configuration file.
* Java v25.
* Finally, add the dependencies:
  * Spring Data MongoDB (the dependency should be spring-boot-starter-data-mongodb in your project's dependencies pom.xml file, which is the MongoDB officially supported dependency for Spring Data)
  * Spring Web (should be spring-boot-starter-web in your project's dependencies pom.xml file)

Generate the project. It will download a file named srsapp.zip. Uncompress it. Open a Terminal in your system and go to the directory.

Run the app from the command line interface (in the case of Linux / macOS) with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./mvnw spring-boot:run</pre>

You need to have a Java SDK installed and available in your PATH.

It will fail! You'll see an error like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">com.mongodb.MongoSocketOpenException: Exception opening socket</pre>

This happens because Spring Boot is already trying to connect to MongoDB, as MongoDB is part of the starter dependencies, and we don't have yet a database. Let's fix this by creating a free MongoDB Atlas cluster. You need to [register for a free Atlas account](https://account.mongodb.com/account/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-boot-foojay&utm_term=hugh.murray) and then follow the instructions on how to [Deploy a Free Cluster](https://www.mongodb.com/docs/atlas/tutorial/deploy-free-tier-cluster/). Select the Atlas UI, to create the cluster using your browser (you can also create a cluster using the command line and Atlas CLI).

****Configure MongoDB Connection URI in Spring Boot**** {#h2-2-configure-mongodb-connection-uri-in-spring-boot}
---------------------------------------------------------------------------------------------------------------

Once we have that cluster created, we can [copy the connection string](https://www.mongodb.com/docs/manual/reference/connection-string/?deployment-type=atlas&interface-atlas-only=atlas-ui&utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-boot-foojay&utm_term=hugh.murray) and add it to the properties file that Spring Boot reads on startup.

A MongoDB Connection String should look like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongodb+srv://&lt;user&gt;:&lt;password&gt;@&lt;your-cluster&gt;.mongodb.net/</pre>

Where you need to put in your user and password.

To avoid hardcoding secrets in our code, let's put our connection string in an environment variable called MONGODB_URI. Then, we'll read it in our src/main/resources/application.yaml file. The name of our database will be srsapp.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring:

&nbsp;&nbsp;application:

&nbsp;&nbsp;&nbsp;&nbsp;name: srsapp

&nbsp;&nbsp;mongodb:

&nbsp;&nbsp;&nbsp;&nbsp;base-uri: ${MONGODB_URI}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;uri: ${spring.mongodb.base-uri}?appName=devrel-tutorial-java-spring-foojay

&nbsp;&nbsp;&nbsp;&nbsp;database: srsapp</pre>

We want to add an error message at the start of our app to warn about the missing URI. For that, we'll edit the main method in src/main/java/com/mongodb/nimongo/srsapp/SrsappApplication.java

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// SrsappApplication.java
@SpringBootApplication
public class SrsappApplication implements CommandLineRunner {
&nbsp;&nbsp;&nbsp;&nbsp;private static final Logger log = LoggerFactory.getLogger(SrsappApplication.class);
&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String envUri = System.getenv("MONGODB_URI");

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (envUri == null || envUri.isBlank()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new IllegalArgumentException("Missing MongoDB URI");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SpringApplication.run(SrsappApplication.class, args);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} catch (IllegalArgumentException e) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.error(ErrorMessage.noDB);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.exit(1);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public void run(String... args) throws Exception {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("🚀 App Started");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String envUri = System.getenv("MONGODB_URI");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("MongoDB URI from environment variable: {}", envUri);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Several changes here:

* We introduce a Logger instance to print out debugging information to the console. Import the relevant classes from the org.slf4j package.
* then we check if there's an environment variable defined called MONGODB_URI. If that's not the case, we throw an IllegalArgumentException, which we immediately catch, logging the error (the ErrorMessage.noDB message is shown below, but you can use your own string message) and exiting the app.

This way, if we start the app from the command line without the MONGODB_URI environment variable defined, we'll get an error:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./mvnw spring-boot:run

11:56:24.373 [main] ERROR com.mongodb.nimongo.srsapp.SrsappApplication --&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;####### ######&nbsp; ######&nbsp; ####### ######

&nbsp;&nbsp;&nbsp;&nbsp;# &nbsp; &nbsp; &nbsp; # &nbsp; &nbsp; # # &nbsp; &nbsp; # # &nbsp; &nbsp; # # &nbsp; &nbsp; #

&nbsp;&nbsp;&nbsp;&nbsp;# &nbsp; &nbsp; &nbsp; # &nbsp; &nbsp; # # &nbsp; &nbsp; # # &nbsp; &nbsp; # # &nbsp; &nbsp; #

&nbsp;&nbsp;&nbsp;&nbsp;##### &nbsp; ######&nbsp; ######&nbsp; # &nbsp; &nbsp; # ######

&nbsp;&nbsp;&nbsp;&nbsp;# &nbsp; &nbsp; &nbsp; # &nbsp; # &nbsp; # &nbsp; # &nbsp; # &nbsp; &nbsp; # # &nbsp; #

&nbsp;&nbsp;&nbsp;&nbsp;# &nbsp; &nbsp; &nbsp; #&nbsp; &nbsp; #&nbsp; #&nbsp; &nbsp; #&nbsp; # &nbsp; &nbsp; # #&nbsp; &nbsp; #

&nbsp;&nbsp;&nbsp;&nbsp;####### # &nbsp; &nbsp; # # &nbsp; &nbsp; # ####### # &nbsp; &nbsp; #

&nbsp;&nbsp;&nbsp;&nbsp;Missing database connection string!</pre>

To fix this, set the environment variable before starting the app:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&nbsp;&nbsp;&nbsp;&nbsp;export MONGODB_URI="&lt;YOUR_CONNECTION_STRING&gt;"</pre>

Then run your application again.

Finally, if we provide a URI, the app starts:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MONGODB_URI=mongodb+srv://user:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c1b1a0b2b2b6aeb3a581b8aeb4b3eca2adb4b2b5a4b3efacaeafa6aea5a3efafa4b5">[email&nbsp;protected]</a>/srsapp ./mvnw spring-boot:run</pre>

And you should see in the log messages from org.mongodb.driver.client

****Flashcard System: Logical Data Model \& ERD**** {#h2-3-flashcard-system-logical-data-model-erd}
---------------------------------------------------------------------------------------------------

From a logical point of view, cards are organized in Decks. A Deck of Cards will have a name and will contain cards. There's no limit to the number of Decks and Cards we can have in our system. A Card will have some text in the front, some text in the back, and will belong to a Deck. A Card can be in only one Deck, so this is a one-to-many relationship: a Deck can have 0..n Cards. We can represent that with the following ERD:

erDiagram DECK \|\|--o{ CARD : contains DECK { string id string name string description } CARD { string id date frontText string backText }

****MongoDB Schema Design: One-to-Many Relationships**** {#h2-4-mongodb-schema-design-one-to-many-relationships}
----------------------------------------------------------------------------------------------------------------

To model our entities, we have several options. Let's reason through them and find the best schema for this particular problem.

Option 1: Everything in one Collection. We can put Decks in one collection and define Cards as an array inside each Deck. This is one way to model a 1-to-many relationship, recommended when we know the maximum size of the n part. Would look like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;&nbsp;&nbsp;_id: 1,
&nbsp;&nbsp;&nbsp;&nbsp;title: "Spanish Deck",
&nbsp;&nbsp;&nbsp;&nbsp;cards: [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;frontText: "Hola",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;backText: "Hello"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...
&nbsp;&nbsp;&nbsp;&nbsp;]
}</pre>

The main problem here is that we can keep adding cards to the same deck. For instance, the [Jōyō Kanji (常用漢字)](https://www.kanshudo.com/collections/joyo_kanji) is the "basic" set of kanji you're expected to learn, all 2136. That means an array of at least 2136 elements. Every single time you add, delete, or edit a Card, MongoDB reads the whole Deck in memory and saves it in one implicit transaction. This will slow down the system if many people were accessing the same Deck (not in this case), but the main problem here is the lack of boundaries. That array can grow without boundaries and hit the 16MB limit for BSON objects, in what is called the [Unbounded Array Antipattern](https://www.mongodb.com/docs/manual/data-modeling/design-antipatterns/unbounded-arrays/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-boot-foojay&utm_term=hugh.murray)

Option 2: We maintain Decks and Cards in separate collections. This will avoid the [Unbounded Array Antipattern](https://www.mongodb.com/docs/manual/data-modeling/design-antipatterns/unbounded-arrays/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring-boot-foojay&utm_term=hugh.murray) problem, and we can get all cards from a Deck:

* running two find queries (one for the Deck and another for all the Cards in that Deck)
* just with one query, using MongoDB's Aggregation Pipeline and the $lookup operator to get a Deck and all related Cards

We'll maintain a link between the Card and its Deck using the parentDeckId. It's worth mentioning here that MongoDB does not have the concept of Foreign Key enforcement, so if you need to check that a Deck in fact exists while adding a Card, that's something you'll have to do in code.

Our Decks will look like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;&nbsp;&nbsp;_id: 1,
&nbsp;&nbsp;&nbsp;&nbsp;title: "Spanish Deck",
&nbsp;&nbsp;&nbsp;&nbsp;description: "A Deck to learn the Spanish Language"
}</pre>

And our Cards:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;&nbsp;&nbsp;_id: 78,
&nbsp;&nbsp;&nbsp;&nbsp;parentDeckId: 1,
&nbsp;&nbsp;&nbsp;&nbsp;frontText: "Hola",
&nbsp;&nbsp;&nbsp;&nbsp;backText: "Hello"
}</pre>

****Java Model Classes: Deck and FlashCard (Spring Boot Records)**** {#h2-5-java-model-classes-deck-and-flashcard-spring-boot-records}
--------------------------------------------------------------------------------------------------------------------------------------

Based on the previous schema, we'll have these two model classes:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Deck.java
package com.mongodb.nimongo.srsapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
@Document(collection = "decks")
public record Deck(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@Id @JsonProperty("_id") String id,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String name,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String description) {
}</pre>

As you can see, the collection name is decks, we have a String id field in Java, that will map to the _id field in the database.

Our flash card model class FlashCard will be quite similar:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// FlashCard.java
package com.mongodb.nimongo.srsapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "cards")
public record FlashCard(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@Id @JsonProperty("_id") String id,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String frontText,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String backText,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String parentDeckId
) {
&nbsp;&nbsp;&nbsp;&nbsp;public FlashCard(String frontText, String backText, String parentDeckId) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this(null, frontText, backText, parentDeckId);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

We'll store everything in a cards collection, and use a parentDeckId as a link to the Deck this Card belongs.

****Spring Boot DevTools: How to Enable Automatic Restarts**** {#h2-6-spring-boot-devtools-how-to-enable-automatic-restarts}
----------------------------------------------------------------------------------------------------------------------------

Every single time we change something in our code, we need to stop the application and start it again. This can become a tedious process quickly, and also one that is prone to changes. If I got a penny every time I changed something without saving and then was puzzled about the fix not being deployed, I won't be writing this now. I'll be retired a sailing the world in a luxury yacht.

Open your pom.xml file and add this dependency:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;!-- Development Tools --&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-devtools&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;runtime&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;optional&gt;true&lt;/optional&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;</pre>

Now, stop and relaunch the app for the last time, and from now on, every time you change anything in your code, your app will recompile and relaunch automagically.

****REST API Design for Flashcard Decks and Cards**** {#h2-7-rest-api-design-for-flashcard-decks-and-cards}
-----------------------------------------------------------------------------------------------------------

We need several endpoints for our app.

### **Decks** {#h3-8-decks}

* to create a new Deck
  * POST /decks
  * Body: { "name": "Spanish A1", ... }
  * Response: 201 Created + deck representation (often with Location: /decks/{deckId})
* to list all Decks
  * GET /decks
  * Response: 200 OK + \[{...}, {...}\]
* to delete a Deck
  * DELETE /decks/{deckId}
  * Response: 204 No Content (or 404 if not found)
* to get the details of one Deck
  * GET /decks/{deckId} (fetch one deck)
* GET /decks/{deckId}/cards/{cardId} (fetch one card)

### **Cards** {#h3-9-cards}

* to add a new Card to a Deck
  * POST /decks/{deckId}/cards
  * Body: { "front": "hola", "back": "hello", ... }
  * Response: 201 Created + card representation (often Location: /decks/{deckId}/cards/{cardId})
* to delete a Card
  * DELETE /decks/{deckId}/cards/{cardId}
  * Response: 204 No Content (or 404 if not found)
* to list all Cards in a Deck
  * GET /decks/{deckId}/cards
  * Response: 200 OK + \[{...}, {...}\]
* to delete all Cards in a Deck
  * Request: DELETE /decks/{deckId}/cards
  * Response:
    * 204 No Content on success, or
    * 404 Not Found if the deckId doesn't exist.

****Create a Spring Boot REST Controller for Decks**** {#h2-10-create-a-spring-boot-rest-controller-for-decks}
--------------------------------------------------------------------------------------------------------------

To test that our Spring Data API points work, we'll start by adding an empty Deck controller. Create a BaseController.java and DeckController.java file in web/controller like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// BaseController.java
package com.mongodb.nimongo.srsapp.web.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.nimongo.srsapp.SrsappApplication;
public abstract class BaseController {
&nbsp;&nbsp;&nbsp;&nbsp;static final Logger log = LoggerFactory.getLogger(SrsappApplication.class);
}</pre>

BaseController adds a Logger to our controllers.

Now, our DeckController:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// DeckController.java
package com.mongodb.nimongo.srsapp.web.controller;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.nimongo.srsapp.model.Deck;
import com.mongodb.nimongo.srsapp.model.FlashCard;
@RestController
@RequestMapping("/decks")

public class DeckController extends BaseController{
&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;Deck&gt;&gt; getAllDecks(@RequestParam Optional&lt;Integer&gt; pageSize, @RequestParam Optional&lt;Integer&gt; pageNumber) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting all decks with pageSize {} and pageNumber {}", pageSize, pageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Deck&gt; emptyList = List.of();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(emptyList, HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Optional&lt;Deck&gt;&gt; getDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;Deck&gt; emptyDeck = Optional.empty();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(emptyDeck, HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}/cards")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;FlashCard&gt;&gt; getAllCardsInDeck(@RequestParam Optional&lt;Integer&gt; pageSize, @RequestParam Optional&lt;Integer&gt; pageNumber, @PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int thePageSize = pageSize.orElse(10);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int thePageNumber = pageNumber.orElse(0);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting all cards in deck with pageSize {} and pageNumber {} and deckId {}", thePageSize, thePageNumber, id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;FlashCard&gt; emptyList = List.of();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(emptyList, HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@PostMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Deck&gt; createDeck(@RequestBody Deck deck) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Creating deck with name {}", deck.description());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Deck emptyDeck = new Deck(null, deck.name(), deck.description());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(emptyDeck, HttpStatus.CREATED);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/search")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;Deck&gt;&gt; searchDecks(@RequestParam Optional&lt;String&gt; term) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Searching decks with term {}", term);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Deck&gt; emptyList = List.of();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(emptyList, HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deleteDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Deleting deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(HttpStatus.NO_CONTENT);
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}/cards")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deleteCardsInDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Deleting all cards in deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(HttpStatus.NO_CONTENT);
&nbsp;&nbsp;&nbsp;}
}</pre>

If you look at the code, you'll see that we are just returning empty responses. We want to call these endpoints and make sure the web part is working properly.

****How to Change the Default Spring Boot Port (Port 5400)**** {#h2-11-how-to-change-the-default-spring-boot-port-port-5400}
----------------------------------------------------------------------------------------------------------------------------

Then, we'll change the default port (8080) to 5400 and add some options to improve the debugging logs. Open application.yml and change it to:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring:
&nbsp;&nbsp;application:
&nbsp;&nbsp;&nbsp;&nbsp;name: srsapp
&nbsp;&nbsp;mongodb:
&nbsp;&nbsp;&nbsp;&nbsp;uri: ${MONGODB_URI}
&nbsp;&nbsp;&nbsp;&nbsp;database: srsapp
&nbsp;&nbsp;mvc:
&nbsp;&nbsp;&nbsp;&nbsp;log-request-details: true
server:
&nbsp;&nbsp;port: 5400
logging:
&nbsp;&nbsp;level:
&nbsp;&nbsp;&nbsp;&nbsp;org:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;springframework:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;web: DEBUG
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;data:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mongodb: DEBUG</pre>

****Test Spring Boot REST APIs with cURL Commands**** {#h2-12-test-spring-boot-rest-apis-with-curl-commands}
------------------------------------------------------------------------------------------------------------

To test our endpoints, we will use cURL, available in Linux/macOS and Windows.

If you copy and paste these in a terminal, you'll be able to query for all Decks:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">## GET decks

curl "http://localhost:5400/decks"

## GET decks paginated

curl "http://localhost:5400/decks?pageSize=2&amp;pageNumber=0"</pre>

To insert a new Deck:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">## POST deck

curl -X "POST" "http://localhost:5400/decks" \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-H 'Content-Type: application/json; charset=utf-8' \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-d $'{
&nbsp;&nbsp;"name": "Test new Deck",
&nbsp;&nbsp;"description": "Test new Deck description"
}'</pre>

For search and delete:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">## DELETE deck

curl -X "DELETE" "http://localhost:5400/decks/&lt;deck id&gt;"

## Search decks

curl "http://localhost:5400/decks/search?term=lang"</pre>

But wait! All this is just working with the placeholder DeckController that is not storing or retrieving anything from a database! Let's fix this by adding our MongoDB code for Decks!

****Spring Data MongoDB Repositories: DeckRepository \& CardRepository**** {#h2-13-spring-data-mongodb-repositories-deckrepository-cardrepository}
--------------------------------------------------------------------------------------------------------------------------------------------------

To access MongoDB, we will create an interface DeckRepository that extends MongoRepository. This is the quickest way to access MongoDB, as MongoRepository includes several useful methods to access our collections. We can even add our own methods to the interface to get custom behaviour. If we need something more advanced or customized, we will need to use MongoTemplate.

Create DeckRepository inside the repository folder:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// DeckRepository

package com.mongodb.nimongo.srsapp.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.mongodb.nimongo.srsapp.model.Deck;

/**
&nbsp;* Repository interface for Deck persistence operations.
&nbsp;* Extends Spring Data's {@link MongoRepository} to provide CRUD and
&nbsp;* paging support and declares additional query methods used by the service
&nbsp;* layer.
&nbsp;*/

@Repository
public interface DeckRepository extends MongoRepository&lt;Deck, String&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Search decks by name or description, using a RegExp
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param searchText - text to search, case insensitive
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param pageable &nbsp; - to paginate responses
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a List of {@link Deck} that matches the searchText
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;@Query("{$or:[ {name: {$regex: ?0, $options: 'i'}}, {description: {$regex: ?0, $options: 'i'}} ] }")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Deck&gt; searchByText(String searchText, Pageable pageable);
}</pre>

As you can see, we have added one new method: searchByText that it's using a regular expression to search for some text (denoted by ?0 inside name or description. We pass in the i option to perform a case-insensitive comparison. Using regular expressions is not the best way to search for text in MongoDB; generally is better to use [Full Text Search](https://www.mongodb.com/resources/basics/full-text-search), defining a Search Index and using $search.

While we're at it we'll also create CardRepository

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// CardRepository

package com.mongodb.nimongo.srsapp.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.mongodb.nimongo.srsapp.model.FlashCard;

/**
&nbsp;* Repository interface for FlashCard persistence operations.
&nbsp;* Extends Spring Data's {@link MongoRepository} to provide CRUD and
&nbsp;* paging support and declares additional query methods used by the service
&nbsp;* layer.
&nbsp;*/

@Repository
public interface CardRepository extends MongoRepository&lt;FlashCard, String&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Search cards by frontText or backText, using a RegExp
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param searchText - text to search, case insensitive
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param pageable &nbsp; - to paginate responses
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a List of {@link FlashCard} that matches the searchText
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/

&nbsp;&nbsp;&nbsp;&nbsp;@Query("{$or:[ {frontText: {$regex: ?0, $options: 'i'}}, {backText: {$regex: ?0, $options: 'i'}} ] }")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;FlashCard&gt; searchByText(String searchText, Pageable pageable);
&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Return a page of flash cards that belong to the given parent deck id.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param deckId&nbsp; the parent deck identifier
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param request the PageRequest containing paging parameters
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a Page of FlashCard objects
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/

&nbsp;&nbsp;&nbsp;&nbsp;Page&lt;FlashCard&gt; findAllByParentDeckId(String deckId, PageRequest request);
&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Delete all flash cards that belong to the given parent deck id.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param deckId the parent deck identifier whose cards should be removed
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;@DeleteQuery("{parentDeckId: ?0}")
&nbsp;&nbsp;&nbsp;&nbsp;void deleteAllByParentDeckId(String deckId);
}</pre>

****Implementing Business Logic with Spring Boot Services**** {#h2-14-implementing-business-logic-with-spring-boot-services}
----------------------------------------------------------------------------------------------------------------------------

Services is where we actually use the database code. Our services will interact with the database, sending the queries, inserts, updates, etc., and will expose a set of business-level operations consumed by our web controllers. Go ahead and create DeckService.java in the new folder service. Here, we will use DeckRepository and CardRepository to access the database. For instance, to get a Deck by its identifier, we'll use findById, which is part of CrudRepository and in this case will be implemented by our MongoDB driver.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Service

public class DeckService {
&nbsp;&nbsp;&nbsp;&nbsp;private final DeckRepository deckRepository;
&nbsp;&nbsp;&nbsp;&nbsp;private final CardRepository cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.deckRepository = deckRepository;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.cardRepository = cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public Optional&lt;Deck&gt; deckById(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.findById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// more code for other methods.
}</pre>

To add a Deck, we'll use save:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public Deck createDeck(Deck deck) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.save(deck);
&nbsp;&nbsp;&nbsp;&nbsp;}</pre>

Then, to delete:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public void deleteDeck(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deckRepository.deleteById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}</pre>

If we need to delete all the cards in a Deck, we'll use deleteAllByParentDeckId from CardRepository:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public void deleteCardsInDeck(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cardRepository.deleteAllByParentDeckId(id);
&nbsp;&nbsp;&nbsp;&nbsp;}</pre>

The final DeckService will look like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// DeckService.java

package com.mongodb.nimongo.srsapp.service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.mongodb.nimongo.srsapp.model.Deck;
import com.mongodb.nimongo.srsapp.model.FlashCard;
import com.mongodb.nimongo.srsapp.repository.CardRepository;
import com.mongodb.nimongo.srsapp.repository.DeckRepository;

/**
&nbsp;* Service class to handle business logic related to Decks and their associated
&nbsp;* FlashCards.
&nbsp;* This class interacts with the DeckRepository and CardRepository to perform
&nbsp;* operations such as creating, retrieving, searching, and deleting decks and
&nbsp;* their cards.
&nbsp;*/

@Service
public class DeckService {
&nbsp;&nbsp;&nbsp;&nbsp;private final DeckRepository deckRepository;
&nbsp;&nbsp;&nbsp;&nbsp;private final CardRepository cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.deckRepository = deckRepository;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.cardRepository = cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Retrieve a deck by its identifier.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param id the deck identifier
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return an Optional containing the Deck if found, otherwise empty
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Optional&lt;Deck&gt; deckById(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.findById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Return a page of decks.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param limit maximum number of decks per page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param skip&nbsp; zero-based page index
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a Page containing Deck objects for the requested page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Page&lt;Deck&gt; findAllDecks(Integer limit, Integer skip) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PageRequest request = PageRequest.of(skip, limit, Sort.unsorted());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.findAll(request);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Return a page of flash cards that belong to a given deck.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param deckId the parent deck identifier
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param limit&nbsp; maximum number of cards per page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param skip &nbsp; zero-based page index
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a Page containing FlashCard objects for the requested page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Page&lt;FlashCard&gt; allCardsInDeck(String deckId, Integer limit, Integer skip) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PageRequest request = PageRequest.of(skip, limit, Sort.unsorted());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return cardRepository.findAllByParentDeckId(deckId, request);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Search decks using a free-text term.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param theTerm the search term to match against deck text fields
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a list of Decks matching the search term (limited to first page)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Deck&gt; searchDecks(String theTerm) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PageRequest request = PageRequest.of(0, 10, Sort.unsorted());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.searchByText(theTerm, request);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Create or update a deck.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param deck the Deck to persist
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return the saved Deck instance
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Deck createDeck(Deck deck) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return deckRepository.save(deck);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Delete a deck by its identifier.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param id the deck identifier to delete
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public void deleteDeck(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deckRepository.deleteById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Delete all flash cards associated with a given deck.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param id the parent deck identifier whose cards should be removed
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public void deleteCardsInDeck(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cardRepository.deleteAllByParentDeckId(id);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

For Cards, we'll use CardRepository in a similar way:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// CardService.java

package com.mongodb.nimongo.srsapp.service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.mongodb.nimongo.srsapp.model.FlashCard;
import com.mongodb.nimongo.srsapp.repository.CardRepository;

@Service
public class CardService {
&nbsp;&nbsp;&nbsp;&nbsp;private final CardRepository cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;CardService(CardRepository cardRepository) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.cardRepository = cardRepository;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Retrieve a flash card by its identifier.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param id the flash card identifier
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return an Optional containing the FlashCard if found, otherwise empty
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Optional&lt;FlashCard&gt; cardById(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return cardRepository.findById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Return a page of flash cards.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param limit maximum number of cards per page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param skip&nbsp; zero-based page index
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a Page containing FlashCard objects for the requested page
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public Page&lt;FlashCard&gt; findAllCards(Integer limit, Integer skip) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PageRequest request = PageRequest.of(skip, limit, Sort.unsorted());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return cardRepository.findAll(request);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Search flash cards using a free-text term.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param theTerm the search term to match against card text fields
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return a list of FlashCards matching the search term (limited to first page)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;FlashCard&gt; searchCards(String theTerm) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PageRequest request = PageRequest.of(0, 10, Sort.unsorted());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return cardRepository.searchByText(theTerm, request);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Create or update a flash card.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param card the FlashCard to persist
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @return the saved FlashCard instance
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public FlashCard createCard(FlashCard card) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return cardRepository.save(card);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Delete a flash card by its identifier.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* @param id the flash card identifier to delete
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/
&nbsp;&nbsp;&nbsp;&nbsp;public void deleteCard(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cardRepository.deleteById(id);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

****Connecting Controllers to Database: DeckController Implementation**** {#h2-15-connecting-controllers-to-database-deckcontroller-implementation}
---------------------------------------------------------------------------------------------------------------------------------------------------

Now that we have our Repositories (defining the operations we want to perform on the database) and the Services (the business use cases), we can wire everything up in our controllers.

To do that, we'll add a DeckService in DeckController and will call the methods provided by our Service, which in turn will use the MongoRepository.

For instance, to get one Deck, we'll use deckById from deckService:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@GetMapping("/{id}")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Optional&lt;Deck&gt;&gt; getDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(deckService.deckById(id), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}</pre>

The complete, updated listing for DeckController:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.nimongo.srsapp.web.controller; 

import static com.mongodb.nimongo.srsapp.web.controller.Constants.DEFAULT_PAGE_NUMBER;
import static com.mongodb.nimongo.srsapp.web.controller.Constants.DEFAULT_PAGE_SIZE;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.nimongo.srsapp.model.Deck;
import com.mongodb.nimongo.srsapp.model.FlashCard;
import com.mongodb.nimongo.srsapp.service.DeckService;

@RestController
@RequestMapping("/decks")
public class DeckController extends BaseController{
&nbsp;&nbsp;&nbsp;&nbsp;private final DeckService deckService;
&nbsp;&nbsp;&nbsp;&nbsp;DeckController(DeckService deckService) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.deckService = deckService;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;Deck&gt;&gt; getAllDecks(@RequestParam Optional&lt;Integer&gt; pageSize, @RequestParam Optional&lt;Integer&gt; pageNumber) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageSize = pageSize.orElse(DEFAULT_PAGE_SIZE);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageNumber = pageNumber.orElse(DEFAULT_PAGE_NUMBER);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting all decks with pageSize {} and pageNumber {}", thePageSize, thePageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page&lt;Deck&gt; deckPage = deckService.findAllDecks(thePageSize, thePageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(deckPage.getContent(), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Optional&lt;Deck&gt;&gt; getDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(deckService.deckById(id), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}/cards")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;FlashCard&gt;&gt; getAllCardsInDeck(@RequestParam Optional&lt;Integer&gt; pageSize, @RequestParam Optional&lt;Integer&gt; pageNumber, @PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageSize = pageSize.orElse(DEFAULT_PAGE_SIZE);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageNumber = pageNumber.orElse(DEFAULT_PAGE_NUMBER);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Getting all cards in deck with pageSize {} and pageNumber {} and deckId {}", thePageSize, thePageNumber, id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page&lt;FlashCard&gt; cardPage = deckService.allCardsInDeck(id, thePageSize, thePageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(cardPage.getContent(), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@PostMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Deck&gt; createDeck(@RequestBody Deck deck) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Creating deck with name {}", deck.description());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Deck createdDeck = deckService.createDeck(deck);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(createdDeck, HttpStatus.CREATED);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/search")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;Deck&gt;&gt; searchDecks(@RequestParam Optional&lt;String&gt; term) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Searching decks with term {}", term);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String theTerm = term.orElse("");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(deckService.searchDecks(theTerm), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deleteDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deckService.deleteDeck(id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Deleting deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(HttpStatus.NO_CONTENT);
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}/cards")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deleteCardsInDeck(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deckService.deleteCardsInDeck(id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("💻 Deleting all cards in deck with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(HttpStatus.NO_CONTENT);
&nbsp;&nbsp;&nbsp;}
}</pre>

And for Cards:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.nimongo.srsapp.web.controller;

import static com.mongodb.nimongo.srsapp.web.controller.Constants.DEFAULT_PAGE_NUMBER;
import static com.mongodb.nimongo.srsapp.web.controller.Constants.DEFAULT_PAGE_SIZE;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.nimongo.srsapp.model.FlashCard;
import com.mongodb.nimongo.srsapp.service.CardService;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController extends BaseController{
&nbsp;&nbsp;&nbsp;&nbsp;private final CardService cardService;
&nbsp;&nbsp;&nbsp;&nbsp;CardController(CardService cardService) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.cardService = cardService;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;FlashCard&gt;&gt; getAllCards(@RequestParam Optional&lt;Integer&gt; pageSize, @RequestParam Optional&lt;Integer&gt; pageNumber) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageSize = pageSize.orElse(DEFAULT_PAGE_SIZE);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integer thePageNumber = pageNumber.orElse(DEFAULT_PAGE_NUMBER);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("Getting all cards with pageSize {} and pageNumber {}", thePageSize, thePageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page&lt;FlashCard&gt; cardPage = cardService.findAllCards(thePageSize, thePageNumber);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(cardPage.getContent(), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Optional&lt;FlashCard&gt;&gt; getCard(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("Getting card with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(cardService.cardById(id), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@PostMapping
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;FlashCard&gt; createCard(@RequestBody FlashCard card) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FlashCard newCard = new FlashCard(card.frontText(), card.backText(), card.parentDeckId());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("Creating card with name {}", card.frontText());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(cardService.createCard(newCard), HttpStatus.CREATED);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/search")
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;FlashCard&gt;&gt; searchCards(@RequestParam Optional&lt;String&gt; term) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("Searching cards with term {}", term);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String theTerm = term.orElse("");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(cardService.searchCards(theTerm), HttpStatus.OK);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}")
&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deleteCard(@PathVariable String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;log.info("Deleting card with id {}", id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cardService.deleteCard(id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ResponseEntity&lt;&gt;(HttpStatus.NO_CONTENT);
&nbsp;&nbsp;&nbsp;}
}</pre>

Finally, our constants:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.nimongo.srsapp.web.controller;

public class Constants {
&nbsp;&nbsp;&nbsp;&nbsp;public static final int DEFAULT_PAGE_SIZE = 10;
&nbsp;&nbsp;&nbsp;&nbsp;public static final int DEFAULT_PAGE_NUMBER = 0;
}</pre>

**Next steps** {#h2-16-next-steps}
----------------------------------

In this post, we've covered a lot of ground: building a Spring Boot API that stores our data in MongoDB and testing it. In the second part of this post, we'll add a Spaced Repetition System library and a couple of endpoints to actually do our reviews, along with the schema changes. Stay tuned!
