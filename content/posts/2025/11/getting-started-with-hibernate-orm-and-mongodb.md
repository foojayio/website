---
title: "Getting Started With Hibernate ORM and MongoDB"
slug: "getting-started-with-hibernate-orm-and-mongodb"
date: "2025-11-04T17:06:26+00:00"
lastmod: "2025-12-12T21:20:53+00:00"
description: "For years, Hibernate ORM has been one of the most popular frameworks in the Java ecosystem. It was built to simplify data persistence by letting developers work with Java objects instead of SQL statements, a technique known as object-relational mapping (ORM).Traditionally, Hibernate ORM has been tightly associated with relational databases like PostgreSQL, MySQL, and Oracle. It manages connections, transactions, and entity state behind the scenes, and even provides Hibernate Query Language (HQL) so you can query your data using Java entity names rather than table names.Now, that same simplicity is available in the document-oriented world. With the MongoDB Extension for Hibernate ORM, developers can use familiar annotations such as @Entity and @Id, and the same Session.persist() and HQL queries they already know, but backed by MongoDB’s flexible schema architecture.This integration introduces a new MongoDB extension that allows Hibernate to translate entity operations and HQL queries into MongoDB commands, combining the ease of JPA with the scalability of MongoDB.In this article, we’ll walk through the setup and first steps to get Hibernate ORM running with MongoDB, from configuration to a simple CRUD example."
authors:
  - "ricardo-mello"
image: "https://foojay.io/wp-content/uploads/2025/10/546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1"
  - "beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2"
enlighterjs: true
frozen: false
---

For years, [Hibernate](https://hibernate.org/) ORM has been one of the most popular frameworks in the Java ecosystem. It was built to simplify data persistence by letting developers work with Java objects instead of SQL statements, a technique known as *object-relational mapping (ORM)*.

Traditionally, Hibernate ORM has been tightly associated with relational databases like PostgreSQL, MySQL, and Oracle. It manages connections, transactions, and entity state behind the scenes, and even provides **Hibernate Query Language (HQL)** so you can query your data using Java entity names rather than table names.

Now, that same simplicity is available in the document-oriented world. With the **MongoDB Extension for Hibernate ORM**, developers can use familiar annotations such as @Entity and @Id, and the same Session.persist() and HQL queries they already know, but backed by MongoDB's flexible schema architecture.

This [integration](https://www.mongodb.com/company/blog/product-release-announcements/introducing-mongodb-extension-for-hibernate-orm-public-preview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello) introduces a new **MongoDB extension** that allows Hibernate to translate entity operations and HQL queries into MongoDB commands, combining the ease of JPA with the scalability of MongoDB.

In this article, we'll walk through the setup and first steps to get Hibernate ORM running with MongoDB, from configuration to a simple CRUD example.

How MongoDB fits in {#h2-0-how-mongodb-fits-in}
-----------------------------------------------

While Hibernate ORM was originally designed for relational databases, its abstraction layer makes it a great candidate for integrating with other storage systems. MongoDB fits naturally into this model because it stores data as flexible, JSON-like documents instead of rigid tables.

Using the new MongoDB extension, Hibernate can now map your entities to MongoDB collections and translate familiar operations, such as persist, find, and even HQL queries, into MongoDB commands behind the scenes. For example, consider a simple HQL query like:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">from Contact where country = ?1 and age &gt; ?2</pre>

When executed, Hibernate replaces the parameters (?1 and ?2) with the actual values provided in your code---for example,*CANADA* and *18* . The MongoDB Dialect then translates the query into an equivalent **MongoDB aggregation pipeline**such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"aggregate": "contact",
&nbsp;"pipeline": [
&nbsp;&nbsp;&nbsp;{ "$match": { "$and": [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ "country": { "$eq": "CANADA" } },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ "age": { "$gt": 18 } }
&nbsp;&nbsp;&nbsp;]}},
&nbsp;&nbsp;&nbsp;{ "$project": { "_id": true, "age": true, "country": true, "name": true }}
&nbsp;]
}</pre>

This translation happens transparently; developers continue using Hibernate's familiar API while the framework builds the corresponding [**MongoDB Query Language (MQL)**](https://www.mongodb.com/docs/manual/reference/mql/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello) commands under the hood.

As a result, you can keep your Hibernate workflow and entity mappings exactly as before, while benefiting from MongoDB's scalability, flexibility, and document-oriented design.

Prerequisites {#h2-1-prerequisites}
-----------------------------------

Before we start building the project, make sure your environment has a few essentials ready.

* Java 17+ installed and an IDE of your choice
* Apache Maven---used in this project to manage dependencies and run the application (you can also use Gradle, if you prefer)
* MongoDB 7 or newer (*Replica set required* )
  * The easiest way to get started is by creating a free cluster in your [MongoDB Atlas account](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello)

With these prerequisites in place, we can move on to setting up the project and adding the required dependencies.

Tag your Atlas cluster {#h2-2-tag-your-atlas-cluster}
-----------------------------------------------------

If you're deploying this application on MongoDB Atlas, you can use [Resource Tags](https://www.mongodb.com/docs/atlas/tags/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello) to label your clusters or projects for tracking and cost visibility. For instance, I recommend tagging your cluster with values that describe this tutorial:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Key: application
Value: hibernate-crud</pre>

Adding tags is a simple but powerful way to organize your MongoDB Atlas resources, especially if you manage multiple clusters, environments, or demos. Tags make it easier to:

* Track which clusters belong to a specific application.  
* Filter and group resources in the Atlas UI.  
* Gain better visibility in billing and monitoring reports.

To add a tag:

1. Open your [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello)dashboard.  
2. Go to **Database → Cluster → Add Tag** .  
3. Click **Add Tag** and use the key/value above.  
4. Save your changes.  

This step won't affect your code, but it's a best practice to keep your Atlas environment organized. If you're running MongoDB locally, you can safely skip this step.

Project overview {#h2-3-project-overview}
-----------------------------------------

In this article, we'll build a simple project that uses Hibernate ORM with MongoDB to manage a single entity---Book.  

The goal is to understand how the MongoDB Dialect works behind the scenes and explore the basic persistence operations that every application needs:

* **Create** a new document (insert a new book)  
* **Find** all documents in the collection  
* **Update** existing data  
* **Delete** a document  
* A slightly more advanced find query using a greater than (\>) filter  

This first part focuses entirely on the Book entity to keep things simple and hands-on. Later, we'll extend the same project to include a second entity called **Review** , introducing a **one-to-many relationship** between books and reviews, but that's for another article.

If you'd like to follow along or check the complete code, it's all available on [GitHub](https://github.com/mongodb-developer/mongodb-hibernate-crud):

The project uses **tags** to separate each stage of development:

* [**Tag v1.0**](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v1.0): includes only the content covered in this article (the Book CRUD operations)  
* [**Tag v2.0**](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v2.0): adds the Review entity and relationship examples discussed later  
* [**Tag v3.0**](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v3.0): extracts the Review model into its own collection to prevent unbounded array growth inside the Book document; introduces a new approach, where each review stores the bookId it belongs to  
* [**Tag v4.0**](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v4.0): implements the subset pattern, keeping all reviews in a separate reviews collection while storing only the three most recent reviews inside each Book document under a recentReview field  

With that overview out of the way, let's set up the environment and add the necessary dependencies.

Setting up the project {#h2-4-setting-up-the-project}
-----------------------------------------------------

For this example, I'm using **IntelliJ IDEA** as my IDE. To create the project, go to *File* **→** *New* **→** *Project* , select *Java* , choose **Maven** as the build system, and click **Create**, as shown in the image below:  
![](/images/posts/2025/11/getting-started-with-hibernate-orm-and-mongodb/Screenshot-2025-10-27-at-2.52.17-PM.png)

This will generate a simple Maven-based Java project with the standard structure (src/main/java and src/main/resources).

If you prefer, you can create it manually using the command line---the structure and configuration will be exactly the same. We'll use Maven for dependency management in this example, but the same setup applies if you prefer Gradle.

Now, open your pom.xml and add the following dependencies:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;project xmlns="http://maven.apache.org/POM/4.0.0"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;
&nbsp;&nbsp;&nbsp;&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
&nbsp;&nbsp;&nbsp;&lt;groupId&gt;com.mongodb&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb-hibernate-crud&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&lt;properties&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;maven.compiler.source&gt;21&lt;/maven.compiler.source&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;maven.compiler.target&gt;21&lt;/maven.compiler.target&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;project.build.sourceEncoding&gt;UTF-8&lt;/project.build.sourceEncoding&gt;
&nbsp;&nbsp;&nbsp;&lt;/properties&gt;
&nbsp;&nbsp;&nbsp;&lt;dependencies&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mongodb&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb-hibernate&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;1.0.0.alpha.1&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&lt;/dependencies&gt;
&lt;/project&gt;</pre>

The **mongodb-hibernate** dependency provides the MongoDB Dialect, the piece that allows Hibernate to translate HQL queries and persistence operations into MongoDB commands.

### Configure Hibernate {#h3-5-configure-hibernate}

Next, create a hibernate.cfg.xml file under src/main/resources to define the connection and mapping settings:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;!DOCTYPE hibernate-configuration PUBLIC
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd"&gt;
&lt;hibernate-configuration&gt;
&nbsp;&nbsp;&nbsp;&lt;session-factory&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name="hibernate.dialect"&gt;com.mongodb.hibernate.dialect.MongoDialect&lt;/property&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name="hibernate.connection.provider_class"&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;com.mongodb.hibernate.jdbc.MongoConnectionProvider
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/property&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name="jakarta.persistence.jdbc.url"&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;REPLACE_WITH_YOUR_CONNECTION_STRING&gt;/mydb?appName=devrel-mongodb-hibernate
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/property&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name="hibernate.show_sql"&gt;true&lt;/property&gt;
&nbsp;&nbsp;&nbsp;&lt;/session-factory&gt;
&lt;/hibernate-configuration&gt;</pre>

**Important:**

Make sure to replace \<REPLACE_WITH_YOUR_CONNECTION_STRING\> with your own MongoDB connection string, the one you get from your[MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello) cluster. It should look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongodb+srv://&lt;username&gt;:&lt;password&gt;@cluster0.mongodb.net</pre>

The configuration also defines the property com.mongodb.hibernate.dialect.MongoDialect,  

which tells Hibernate to use the MongoDB Dialect, the component responsible for translating your entity operations and HQL queries into MongoDB commands (MQL) behind the scenes.

With this configuration in place, Hibernate now knows how to connect to MongoDB and how to interpret your ORM operations using the MongoDB extension.

### The Book entity {#h3-6-the-book-entity}

This class will represent the document we'll store in MongoDB. Create a new package domain and add a class named Book.java.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.domain;

import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.bson.types.ObjectId;

@Entity
@Table(name = "books")

public class Book {
&nbsp;&nbsp;&nbsp;@Id
&nbsp;&nbsp;&nbsp;@ObjectIdGenerator
&nbsp;&nbsp;&nbsp;@GeneratedValue
&nbsp;&nbsp;&nbsp;ObjectId id;
&nbsp;&nbsp;&nbsp;String title;
&nbsp;&nbsp;&nbsp;Integer pages;
&nbsp;&nbsp;&nbsp;public Book() {}

&nbsp;&nbsp;&nbsp;public Book(String title, Integer pages) {
&nbsp;&nbsp;&nbsp;this.title = title;
&nbsp;&nbsp;&nbsp;this.pages = pages;
&nbsp;}

&nbsp;public Book(ObjectId id, String title, Integer pages) {
&nbsp;&nbsp;&nbsp;&nbsp;this.id = id;
&nbsp;&nbsp;&nbsp;&nbsp;this.title = title;
&nbsp;&nbsp;&nbsp;&nbsp;this.pages = pages;
&nbsp;}
&nbsp;&nbsp;&nbsp;// getters and setters
}</pre>

Let's break down what's happening here:

1. The class is annotated with @Entity, telling Hibernate to treat it as a persistent object.  
2. The @Table(name = "books") annotation defines the collection name in MongoDB, in this case, books. So, we're simply telling Hibernate (and therefore MongoDB) that all Book documents should be stored in a collection called **books** .  
3. The annotation @ObjectIdGenerator is part of the MongoDB Hibernate extension and works together with Hibernate's identifier generation mechanism. It tells Hibernate to generate a new **ObjectId** automatically before inserting the document into MongoDB.

### Creating the SessionFactory {#h3-7-creating-the-sessionfactory}

To interact with the database, Hibernate needs a way to open sessions based on our configuration. For that, we define a small helper class that creates a SessionFactory from the hibernate.cfg.xml file and registers our entity. Create a new package called config and include the new class HibernateUtil.java:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.config;

import com.mongodb.domain.Book;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

&nbsp;&nbsp;private static final SessionFactory SESSION_FACTORY =
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Configuration().configure("hibernate.cfg.xml")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.addAnnotatedClass(Book.class)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.buildSessionFactory();

&nbsp;&nbsp;private HibernateUtil() {}
&nbsp;&nbsp;public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }
}</pre>

### Implementing the Book service {#h3-8-implementing-the-book-service}

With the entity and configuration in place, let's create a service class that will handle all basic database operations for the Book entity.

This class will contain methods for the standard CRUD operations and one additional query using a comparison filter (\>).

Inside the src/main/java/com/mongodb folder, create a new package called **service** and add a class named **BookService.java**:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class BookService {
&nbsp;&nbsp;&nbsp;public Book create(String title, Integer pages) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Transaction tx = session.beginTransaction();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Book book = new Book(title, pages);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;session.persist(book);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tx.commit();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return book;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public List&lt;Book&gt; findAll() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return session.createQuery("from Book", Book.class).list();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public boolean update(Book updatedBook) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Transaction tx = session.beginTransaction();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Book existing = session.find(Book.class, updatedBook.getId());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (existing == null) return false;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existing.setTitle(updatedBook.getTitle());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existing.setPages(updatedBook.getPages());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;session.merge(existing);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tx.commit();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return true;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public boolean deleteById(ObjectId id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Transaction tx = session.beginTransaction();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Book book = session.find(Book.class, id);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (book == null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return false;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;session.remove(book);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tx.commit();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return true;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;public List&lt;Book&gt; findBooksWithPagesGreaterThanOrEqual(int minPages) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return session.createQuery(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"from Book b where b.pages &gt;= :minPages", Book.class)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setParameter("minPages", minPages)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.list();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;}
}</pre>

#### Managing the SessionFactory

In this example, each method opens its own session using:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&nbsp;try (Session session = HibernateUtil.getSessionFactory().openSession()) { }</pre>

This is the **simplest approach** for demonstration purposes. It ensures that every operation runs independently and that sessions are properly closed after use. However, in a real-world application, you'd likely manage the SessionFactory more elegantly.

#### Understanding each method

##### create

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.persist(book);</pre>

This inserts a new document into the **books** collection. The MongoDB Dialect automatically generates the _id field and translates this into an insertOne command under the hood.

##### findAll

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.createQuery("from Book", Book.class).list();</pre>

This HQL query retrieves all documents from the books collection---equivalent to db.books.find() in MongoDB.

##### update

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.merge(existing);</pre>

Hibernate detects changes to the entity and issues an update operation in MongoDB using the document's _id as a filter.

##### deleteById

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.remove(book);</pre>

This method locates a document in the **books** collection by its _id and deletes it.

Hibernate translates the operation into a MongoDB deleteOne command, using the document's _id as the filter.

##### findBooksWithPagesGreaterThanOrEqual

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">from Book b where b.pages &gt;= :minPages</pre>

This demonstrates how **HQL comparison operators** map directly to **MongoDB operators**.

In this case, \>= becomes $gte, generating a pipeline similar to:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{ "$match": { "pages": { "$gte": 300 } } }</pre>

#### Building the main application

To test everything we've built so far, let's create a simple Java class to interact with our BookService and perform the CRUD operations directly from the console.

For simplicity, we'll use Java's Scanner class to read user input. Of course, in a real application, you might expose these operations through a REST API or a web interface, but for testing purposes, a simple console-based menu works perfectly.

Create a new class called **MyApplication.java** in the com.mongodb package:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import org.bson.types.ObjectId;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Scanner;

public class MyApplication {
&nbsp;&nbsp;&nbsp;public static void main(String[] args) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SessionFactory factory = HibernateUtil.getSessionFactory();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Scanner sc = new Scanner(System.in);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BookService bookService = new BookService();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int option;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;do {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\n=== BOOK MENU ===");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("1 - Add Book");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("2 - List Books");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("3 - Update Book");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("4 - Delete Book");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("5 - Find Books by Minimum Pages");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("0 - Exit");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Choose: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;option = sc.nextInt();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sc.nextLine();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;switch (option) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 1 -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Title: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String title = sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Number of pages: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int pages = sc.nextInt();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;var book = bookService.create(title, pages);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Created: " + book);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 2 -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Book&gt; books = bookService.findAll();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;books.forEach(System.out::println);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 3 -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Book ID: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String id = sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("New Title: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String newTitle = sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("New Page Count: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int newPages = sc.nextInt();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Book updated = new Book(new ObjectId(id), newTitle, newPages);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boolean ok = bookService.update(updated);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(ok ? "Book updated successfully!" : "Book not found.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 4 -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Book ID to delete: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String id = sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;boolean deleted = bookService.deleteById(new ObjectId(id));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(deleted ? "Book deleted successfully!" : "Book not found.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 5 -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print("Enter the minimum number of pages: ");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String pages = sc.nextLine();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Book&gt; books = bookService.findBooksWithPagesGreaterThanOrEqual(Integer.parseInt(pages));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;books.forEach(System.out::println);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case 0 -&gt; System.out.println("Bye!");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;default -&gt; System.out.println("Invalid option, try again.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} while (option != 0);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sc.close();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;factory.close();
&nbsp;&nbsp;&nbsp;}
}</pre>

Running the application {#h2-9-running-the-application}
-------------------------------------------------------

Once everything is configured, you can run the project directly from the command line using Maven. In the root of your project (where the pom.xml file is located), execute:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn clean package</pre>

And then:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn compile exec:java -Dexec.mainClass="com.mongodb.MyApplication"</pre>

Maven will compile the code, download any missing dependencies, and execute the main() method in your MyApplication class.

Make sure your hibernate.cfg.xml file is inside src/main/resources and that your MongoDB connection string is correct before running the command.

That's it: Your application should start and display the interactive menu in the terminal.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">=== BOOK MENU ===

1 - Add Book

2 - List Books

3 - Update Book

4 - Delete Book

5 - Find Books by Minimum Pages

0 - Exit

Choose:</pre>

You can now test each option in the console menu to create, list, update, and delete books---and see Hibernate and MongoDB working together in action.

Current limitations (Public Preview) {#h2-10-current-limitations-public-preview}
--------------------------------------------------------------------------------

The MongoDB Hibernate ORM extension is currently in **Public Preview**, meaning it's stable for experimentation but still expanding toward full feature coverage compared to relational databases.

At this stage, some MongoDB-specific capabilities, such as compound indexes and geospatial queries, are not yet supported directly through Hibernate. You can still create these structures manually using the MongoDB Java Driver.

For the most up-to-date list of supported features and upcoming improvements, check the [official documentation.](https://www.mongodb.com/docs/languages/java/mongodb-hibernate/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-crud&utm_term=ricardo.mello)

Wrapping up {#h2-11-wrapping-up}
--------------------------------

Hibernate ORM has long been a powerful choice for Java developers who prefer working with objects instead of raw database queries.

And now, with the new MongoDB extension for Hibernate ORM, that same convenience extends to the document world, allowing developers to work with MongoDB's flexible document model using the same familiar APIs and annotations.

This integration bridges two worlds: the stability and maturity of Hibernate with the scalability and agility of MongoDB.

It's a great way for teams already invested in Hibernate to start exploring the benefits of document databases without changing how they write persistence code. The complete project is available on[GitHub](https://github.com/mongodb-developer/mongodb-hibernate-crud).
