---
title: "Abstracting Data Access in Java With the DAO Pattern"
slug: "abstracting-data-access-in-java-with-the-dao-pattern"
date: "2026-01-29T16:37:06+00:00"
lastmod: "2026-01-29T16:43:24+00:00"
description: "The Data Access Object (DAO) pattern is a structural pattern that isolates your application's business logic from persistence operations. By using an abstract API, the DAO pattern hides all the complexity of performing CRUD operations against your database—whether that's MongoDB, a relational database, or any other storage mechanism. The DAO pattern ensures both layers can evolve independently.In this tutorial written by Tim Kelly published on Friends of OpenJDK (Foojay.io), we'll implement the DAO pattern with MongoDB as our backend. We'll start with a simple in-memory example to understand the core concepts, then build a production-ready implementation using the MongoDB Java Driver. Along the way, you'll see how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs—no complex entity mappings required."
authors:
  - "tim-kelly"
image: "https://foojay.io/wp-content/uploads/2025/10/546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "building-java-microservices-with-the-repository-pattern"
  - "building-rest-apis-in-java-with-spring-boot"
  - "building-systems-that-know-why-they-exist-when-data-logic-and-intent-finally-align"
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
enlighterjs: true
frozen: false
---

The Data Access Object (DAO) pattern is a structural pattern that isolates your application's business logic from persistence operations. By using an abstract API, the DAO pattern hides all the complexity of performing CRUD operations against your database---whether that's MongoDB, a relational database, or any other storage mechanism.

This separation is crucial: Your business logic shouldn't care whether you're using MongoDB's flexible document model or a rigid SQL schema. The DAO pattern ensures both layers can evolve independently.

In this tutorial, we'll implement the DAO pattern with MongoDB as our backend. We'll start with a simple in-memory example to understand the core concepts, then build a production-ready implementation using the MongoDB Java Driver. Along the way, you'll see how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs---no complex entity mappings required.

A simple implementation {#h2-0-a-simple-implementation}
-------------------------------------------------------

Let's build a basic example to understand how the DAO pattern works. We'll create an inventory management application that tracks products while keeping the domain model completely agnostic about persistence. You can read through, or follow along:

### Prerequisites {#h3-1-prerequisites}

Before starting this tutorial, ensure you have:

* **Java Development Kit (JDK) 17 or higher**: Verify with java -version.
* **Apache Maven 3.9.0 or higher**: Verify with mvn -version.
* **MongoDB Atlas account with a cluster** (M0 free tier works fine): Sign up for free at [MongoDB](https://account.mongodb.com/account/register?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=data+access+object&utm_term=tim.kelly).
  * **Database user credentials**: Create a user with read/write privileges in your Atlas cluster.
  * **Network access configured**: Allow your IP address to connect to the cluster.
  * **Connection string**: Available from your Atlas cluster's "Connect" button.

Now, you can create a simple maven application in the IDE of your choosing and follow along.

### The domain class {#h3-2-the-domain-class}

First, we need a simple domain class to represent our products:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.math.BigDecimal;

public class Product {
&nbsp;&nbsp;&nbsp;&nbsp;private String id;
&nbsp;&nbsp;&nbsp;&nbsp;private String name;
&nbsp;&nbsp;&nbsp;&nbsp;private String category;
&nbsp;&nbsp;&nbsp;&nbsp;private BigDecimal price;
&nbsp;&nbsp;&nbsp;&nbsp;private int stockQuantity;
&nbsp;&nbsp;&nbsp;&nbsp;public Product() {
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public Product(String name, String category, BigDecimal price, int stockQuantity) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.name = name;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.category = category;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.price = price;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.stockQuantity = stockQuantity;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;// standard getters and setters
&nbsp;&nbsp;&nbsp;&nbsp;public String getId() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return id;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void setId(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.id = id;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public String getName() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return name;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void setName(String name) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.name = name;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public String getCategory() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return category;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void setCategory(String category) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.category = category;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public BigDecimal getPrice() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return price;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void setPrice(BigDecimal price) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.price = price;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public int getStockQuantity() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return stockQuantity;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void setStockQuantity(int stockQuantity) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.stockQuantity = stockQuantity;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public String toString() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return "Product{id='" + id + "', name='" + name +&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"', category='" + category + "', price=" + price +&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", stock=" + stockQuantity + "}";
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Notice that this is just a plain old Java object (POJO). No annotations, no framework dependencies---just a container for product data. This is exactly what we want: a clean domain model that knows nothing about how it will be persisted.

### The DAO API {#h3-3-the-dao-api}

Now, let's define the DAO interface that will abstract all persistence operations:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.List;
import java.util.Optional;
public interface Dao&lt;T&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;T&gt; get(String id);
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;T&gt; getAll();
&nbsp;&nbsp;&nbsp;&nbsp;void save(T t);
&nbsp;&nbsp;&nbsp;&nbsp;void update(T t);
&nbsp;&nbsp;&nbsp;&nbsp;void delete(String id);
}</pre>

This interface defines a generic API for CRUD operations on any type T. The beauty of this abstraction is that our application code can work with this interface without knowing anything about the underlying persistence mechanism.

### The ProductDao class {#h3-4-the-productdao-class}

Let's create an in-memory implementation to see the pattern in action:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

public class ProductDao implements Dao&lt;Product&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;private List&lt;Product&gt; products = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;public ProductDao() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product laptop = new Product("ThinkPad X1", "Electronics",&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new BigDecimal("1299.99"), 15);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;laptop.setId(UUID.randomUUID().toString());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.add(laptop);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product desk = new Product("Standing Desk", "Furniture",&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new BigDecimal("499.99"), 8);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;desk.setId(UUID.randomUUID().toString());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.add(desk);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product chair = new Product("Ergonomic Chair", "Furniture",&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new BigDecimal("349.99"), 12);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;chair.setId(UUID.randomUUID().toString());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.add(chair);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public Optional&lt;Product&gt; get(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return products.stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(product -&gt; product.getId().equals(id))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.findFirst();
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Product&gt; getAll() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new ArrayList&lt;&gt;(products);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public void save(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (product.getId() == null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setId(UUID.randomUUID().toString());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.add(product);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public void update(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(p -&gt; p.getId().equals(product.getId()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.findFirst()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.ifPresent(existingProduct -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existingProduct.setName(product.getName());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existingProduct.setCategory(product.getCategory());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existingProduct.setPrice(product.getPrice());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;existingProduct.setStockQuantity(product.getStockQuantity());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public void delete(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;products.removeIf(product -&gt; product.getId().equals(id));
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

This in-memory implementation uses a simple ArrayList as our "database." In the constructor, we populate it with a few sample products. The key thing to notice here is that all the persistence logic---even if it's just managing a list---is encapsulated within the DAO.

### Using the DAO {#h3-5-using-the-dao}

Here's how an application would use our DAO:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.math.BigDecimal;
import java.util.List;

public class InventoryApplication {
&nbsp;&nbsp;&nbsp;&nbsp;private static Dao&lt;Product&gt; productDao;
&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao = new ProductDao();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Get all products
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Current inventory:");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; allProducts = productDao.getAll();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;allProducts.forEach(System.out::println);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Update a product's stock
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (!allProducts.isEmpty()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product product = allProducts.get(0);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nUpdating stock for: " + product.getName());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setStockQuantity(product.getStockQuantity() - 5);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.update(product);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Add a new product
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product keyboard = new Product("Mechanical Keyboard", "Electronics",&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new BigDecimal("159.99"), 25);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.save(keyboard);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nAdded new product: " + keyboard.getName());

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Delete a product
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (allProducts.size() &gt; 1) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String idToDelete = allProducts.get(1).getId();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.delete(idToDelete);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nDeleted product with ID: " + idToDelete);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Show final inventory
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nFinal inventory:");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.getAll().forEach(p -&gt;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("&nbsp; " + p.getName() + " - $" + p.getPrice() +&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" (Stock: " + p.getStockQuantity() + ")")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The critical insight here is that InventoryApplication has no idea how products are being stored. It just knows that it can create, read, update, and delete them through the Dao interface. This means we could swap out the in-memory implementation for a database-backed one without changing a single line of application code.

Now, we can run our app:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn clean compile&nbsp;

mvn exec:java -Dexec.mainClass="com.mongodb.InventoryApplication"</pre>

And we should see an output:

Current inventory:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Product{id='1d9bf23e-8b7d-49ae-ab2b-995dacec3571', name='ThinkPad X1', category='Electronics', price=1299.99, stock=15}

Product{id='cab93a44-c5c6-4d36-a0a7-73b8536d7748', name='Standing Desk', category='Furniture', price=499.99, stock=8}

Product{id='a5301b84-8104-40fc-912a-c1c710ee58db', name='Ergonomic Chair', category='Furniture', price=349.99, stock=12}

Updating stock for: ThinkPad X1

Added new product: Mechanical Keyboard

Deleted product with ID: cab93a44-c5c6-4d36-a0a7-73b8536d7748

Final inventory:

&nbsp;&nbsp;ThinkPad X1 - $1299.99 (Stock: 10)

&nbsp;&nbsp;Ergonomic Chair - $349.99 (Stock: 12)

&nbsp;&nbsp;Mechanical Keyboard - $159.99 (Stock: 25)</pre>

Using MongoDB as the persistence layer {#h2-6-using-mongodb-as-the-persistence-layer}
-------------------------------------------------------------------------------------

Let's implement a production-ready DAO using MongoDB. You might wonder: Doesn't MongoDB already provide a clean API through its driver? Why add another layer?

Here's why the DAO pattern still makes sense with MongoDB:

1. **Domain-specific API**: You expose only the operations your application needs, not MongoDB's entire API.
2. **Business logic boundary**: Prevent MongoDB queries and document manipulation from leaking into your service layer.
3. **Testability**: It's easy to mock or swap implementations for testing.
4. **Future flexibility**: Changing databases or adding caching becomes trivial.

### Setting up MongoDB {#h3-7-setting-up-mongodb}

First, add the MongoDB Java Driver to your pom.xml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mongodb&lt;/groupId&gt;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb-driver-sync&lt;/artifactId&gt;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;5.5.2&lt;/version&gt;&nbsp;&nbsp;
&lt;/dependency&gt;</pre>

### The MongoDBProductDao class {#h3-8-the-mongodbproductdao-class}

Here's our MongoDB-backed implementation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;&nbsp;&nbsp;
import com.mongodb.client.MongoCollection;&nbsp;&nbsp;
import com.mongodb.client.MongoDatabase;&nbsp;&nbsp;
import org.bson.Document;&nbsp;&nbsp;
import org.bson.types.ObjectId;&nbsp;&nbsp;
import java.math.BigDecimal;&nbsp;&nbsp;
import java.util.ArrayList;&nbsp;&nbsp;
import java.util.List;&nbsp;&nbsp;
import java.util.Optional;&nbsp;&nbsp;
import static com.mongodb.client.model.Filters.eq;&nbsp;&nbsp;

public class MongoDBProductDao implements Dao&lt;Product&gt; {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private final MongoCollection&lt;Document&gt; collection;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public MongoDBProductDao(MongoClient mongoClient, String databaseName) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDatabase database = mongoClient.getDatabase(databaseName);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.collection = database.getCollection("products");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public Optional&lt;Product&gt; get(String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document doc = collection.find(eq("_id", new ObjectId(id))).first();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Optional.ofNullable(doc).map(this::documentToProduct);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Product&gt; getAll() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; products = new ArrayList&lt;&gt;();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.find().forEach(doc -&gt; products.add(documentToProduct(doc)));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return products;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public void save(Product product) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document doc = productToDocument(product);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.insertOne(doc);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Set the generated ID back to the product object&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setId(doc.getObjectId("_id").toHexString());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public void update(Product product) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document doc = productToDocument(product);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.replaceOne(&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;eq("_id", new ObjectId(product.getId())),&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;doc&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public void delete(String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.deleteOne(eq("_id", new ObjectId(id)));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;// Helper methods for mapping between Product and Document&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private Document productToDocument(Product product) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document doc = new Document()&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("name", product.getName())&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("category", product.getCategory())&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("price", product.getPrice().doubleValue())&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.append("stockQuantity", product.getStockQuantity());&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (product.getId() != null) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;doc.append("_id", new ObjectId(product.getId()));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return doc;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;private Product documentToProduct(Document doc) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product product = new Product();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setId(doc.getObjectId("_id").toHexString());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setName(doc.getString("name"));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setCategory(doc.getString("category"));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setPrice(BigDecimal.valueOf(doc.getDouble("price")));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setStockQuantity(doc.getInteger("stockQuantity"));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return product;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
}</pre>

Let's break down what's happening here:

**Constructor**: We inject the MongoClient and database name, then get a reference to our "products" collection. This follows the dependency injection pattern, making testing easier.

**CRUD operations**: Each method uses the MongoDB Java Driver API but keeps all that complexity hidden behind our simple Dao interface. Notice we're using MongoDB's filter builders (eq) and working with BSON Document objects internally.

**ID handling**: MongoDB uses ObjectIds, so we convert between string IDs (what our domain model uses) and MongoDB's ObjectId type in the helper methods.

**Mapping helpers**: The productToDocument and documentToProduct methods handle the translation between our domain model and MongoDB's document format. This is much simpler than ORM mapping---we're just moving fields around, no complex entity lifecycle to manage.

### Connecting to MongoDB {#h3-9-connecting-to-mongodb}

Before we can use our DAO, we need to establish a connection to MongoDB. Here's a simple connection manager:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;&nbsp;&nbsp;
import com.mongodb.client.MongoClients;&nbsp;&nbsp;

public class MongoDBConnection {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private static MongoClient mongoClient;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public static MongoClient getClient() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (mongoClient == null) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Add your connection string here&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String uri = "mongodb+srv://&lt;username&gt;:&lt;password&gt;@&lt;cluster&gt;.mongodb.net/?retryWrites=true&amp;w=majority";&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoClientSettings settings = MongoClientSettings.builder()&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.applyConnectionString(new ConnectionString(uri))&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.applicationName("data-access-object-java")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mongoClient = MongoClients.create(settings);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return mongoClient;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static void close() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (mongoClient != null) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mongoClient.close();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
}</pre>

In a production application, you'd typically want to externalize the connection string to configuration files. For our purposes, this simple singleton approach works fine.

### The application class {#h3-10-the-application-class}

Now, let's see everything in action:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;&nbsp;&nbsp;
import java.math.BigDecimal;&nbsp;&nbsp;
import java.util.List;&nbsp;&nbsp;
import java.util.Optional;&nbsp;&nbsp;

public class MongoDBInventoryApplication {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private static Dao&lt;Product&gt; productDao;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Get MongoDB connection&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoClient mongoClient = MongoDBConnection.getClient();&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Create our DAO&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao = new MongoDBProductDao(mongoClient, "inventory_db");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Create a new product&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product mouse = new Product("Wireless Mouse", "Electronics",&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new BigDecimal("29.99"), 50);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;saveProduct(mouse);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Saved product with ID: " + mouse.getId());&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Get the product we just created&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Product retrievedProduct = getProduct(mouse.getId());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Retrieved: " + retrievedProduct);&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Update the product (simulate a sale)&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;retrievedProduct.setStockQuantity(retrievedProduct.getStockQuantity() - 3);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;retrievedProduct.setPrice(new BigDecimal("24.99")); // Price drop!&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;updateProduct(retrievedProduct);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Updated product with new stock and price");&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Get all products&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nCurrent inventory:");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getAllProducts().forEach(product -&gt;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("&nbsp; " + product.getName() +&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" - $" + product.getPrice() +&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" (Stock: " + product.getStockQuantity() + ")")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Delete the product&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deleteProduct(mouse.getId());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("\nDeleted product: " + mouse.getName());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} finally {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDBConnection.close();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static Product getProduct(String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;Product&gt; product = productDao.get(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return product.orElseThrow(() -&gt;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new RuntimeException("Product not found with id: " + id)&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static List&lt;Product&gt; getAllProducts() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return productDao.getAll();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static void updateProduct(Product product) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.update(product);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static void saveProduct(Product product) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.save(product);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;public static void deleteProduct(String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.delete(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
}</pre>

Notice how MongoDBInventoryApplication is almost identical to our earlier InventoryApplication---the only difference is how we initialize the DAO. The business logic remains completely unchanged.

This is the power of the DAO pattern: We've swapped from an in-memory implementation to a full-fledged MongoDB backend without touching our application code. Our domain model (Product) stayed clean, and our business logic stayed focused on what it should do, not how to persist data.

Now, we can run our new implementation with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn exec:java -Dexec.mainClass="com.mongodb.MongoDBInventoryApplication"</pre>

And we should see an output similar to:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">...

Saved product with ID: 69121300b595be0bb189af76

Retrieved: Product{id='69121300b595be0bb189af76', name='Wireless Mouse', category='Electronics', price=29.99, stock=50}

Updated product with new stock and price

Current inventory:

&nbsp;&nbsp;Wireless Mouse - $24.99 (Stock: 47)

Deleted product: Wireless Mouse

...</pre>

Advanced considerations {#h2-11-advanced-considerations}
--------------------------------------------------------

### Preventing business logic leakage {#h3-12-preventing-business-logic-leakage}

One of the most important benefits of the DAO pattern is keeping business logic out of your data access code. Here's what you should avoid:

**Bad practice**---business logic in the DAO:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// DON'T DO THIS

public void save(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;// Business validation doesn't belong here
&nbsp;&nbsp;&nbsp;&nbsp;if (product.getPrice().compareTo(BigDecimal.ZERO) &lt;= 0) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new IllegalArgumentException("Price must be positive");
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;// Business rules don't belong here
&nbsp;&nbsp;&nbsp;&nbsp;if (product.getStockQuantity() &lt; 10) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sendLowStockAlert(product);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;// Pricing logic doesn't belong here
&nbsp;&nbsp;&nbsp;&nbsp;if (isHolidaySeason()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setPrice(product.getPrice().multiply(new BigDecimal("1.2")));
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;Document doc = productToDocument(product);
&nbsp;&nbsp;&nbsp;&nbsp;collection.insertOne(doc);
}</pre>

**Good practice**---clean separation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// In your service layer

public class InventoryService {
&nbsp;&nbsp;&nbsp;&nbsp;private final Dao&lt;Product&gt; productDao;
&nbsp;&nbsp;&nbsp;&nbsp;private final PriceValidator priceValidator;
&nbsp;&nbsp;&nbsp;&nbsp;private final AlertService alertService;
&nbsp;&nbsp;&nbsp;&nbsp;public void addProduct(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business validation in the service layer
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;priceValidator.validatePrice(product.getPrice());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business pricing rules in the service layer
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (isHolidaySeason()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setPrice(calculateHolidayPrice(product.getPrice()));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Persist through DAO
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;productDao.save(product);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business logic in the service layer
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (product.getStockQuantity() &lt; 10) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;alertService.sendLowStockAlert(product);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}
}

// In your DAO
public void save(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;// Pure persistence logic only
&nbsp;&nbsp;&nbsp;&nbsp;Document doc = productToDocument(product);
&nbsp;&nbsp;&nbsp;&nbsp;collection.insertOne(doc);
&nbsp;&nbsp;&nbsp;&nbsp;product.setId(doc.getObjectId("_id").toHexString());
}</pre>

Your DAO should focus exclusively on translating between your domain model and MongoDB's document model. All business rules, validation, and workflow logic belong in your service layer.

### Adding custom query methods {#h3-13-adding-custom-query-methods}

While our basic DAO interface covers CRUD operations, real applications often need custom queries. Here's how to add them without polluting your DAO:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface ProductDao extends Dao&lt;Product&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; findByCategory(String category);
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; findLowStockProducts(int threshold);
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}</pre>

And the implementation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import static com.mongodb.client.model.Filters.*;

public class MongoProductDao implements ProductDao {
&nbsp;&nbsp;&nbsp;&nbsp;// ... existing methods ...
&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Product&gt; findByCategory(String category) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; products = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.find(eq("category", category))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.forEach(doc -&gt; products.add(documentToProduct(doc)));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return products;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Product&gt; findLowStockProducts(int threshold) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; products = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.find(lte("stockQuantity", threshold))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.forEach(doc -&gt; products.add(documentToProduct(doc)));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return products;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Override
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;Product&gt; findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Product&gt; products = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.find(and(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;gte("price", minPrice.doubleValue()),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;lte("price", maxPrice.doubleValue())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;)).forEach(doc -&gt; products.add(documentToProduct(doc)));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return products;
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

This approach keeps your query logic encapsulated in the DAO while still maintaining clean separation of concerns.

### Error handling {#h3-14-error-handling}

Production DAOs need robust error handling:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;

@Override
public void save(Product product) {
&nbsp;&nbsp;&nbsp;&nbsp;try {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document doc = productToDocument(product);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.insertOne(doc);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;product.setId(doc.getObjectId("_id").toHexString());
&nbsp;&nbsp;&nbsp;&nbsp;} catch (MongoWriteException e) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new DuplicateProductException("Product already exists", e);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new DataAccessException("Failed to save product", e);
&nbsp;&nbsp;&nbsp;&nbsp;} catch (MongoException e) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new DataAccessException("Database error while saving product", e);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Notice we're translating MongoDB-specific exceptions into application-specific exceptions. This prevents MongoDB dependencies from leaking into your business layer. We've made use of two custom exceptions, DuplicateProductException and DataAccessException, to help with debugging.

Conclusion {#h2-15-conclusion}
------------------------------

The DAO pattern remains valuable even with modern databases like MongoDB. It creates a clean separation between your business logic and persistence concerns, making your code more maintainable and testable.

We started with a simple in-memory implementation to understand the pattern's core concepts, then built a production-ready MongoDB implementation using a product inventory system. Along the way, we saw how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs---no complex entity mappings or lazy loading to worry about.

The key takeaway: Keep your DAOs focused purely on persistence operations. All business logic, validation, and workflow concerns belong in your service layer. This separation will pay dividends as your application grows and evolves.

For more information on the MongoDB Java Driver and best practices, check out the [official MongoDB Java Driver documentation](https://www.mongodb.com/docs/drivers/java/sync/current/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=data+access+object&utm_term=tim.kelly).
