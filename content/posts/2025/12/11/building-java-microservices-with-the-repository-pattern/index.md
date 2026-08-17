---
title: "Building Java Microservices with the Repository Pattern"
slug: "building-java-microservices-with-the-repository-pattern"
date: "2025-12-11T23:19:10+00:00"
lastmod: "2025-12-11T23:19:11+00:00"
description: "The repository pattern is a design method that allows for abstraction between business logic and the data of an application. This allows for retrieving and saving/updating objects without exposing the technical details of how that data is stored in the main application. In this blog, we will use Spring Boot with MongoDB in order to create a repository pattern-based application. Spring Boot applications generally have two main components to a repository pattern: standard repository items from spring—in this case, MongoRepository—and then custom repository items that you create to perform operations beyond what is included with the standard repository."
authors:
  - "mike-laspina"
image: "Screenshot-2025-11-11-at-2.17.07-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3"
enlighterjs: true
frozen: false
---

### What you'll learn {#h3-0-what-you-ll-learn}

* How the MongoDB Spring repository can be used to abstract MongoDB operations
* Ensuring data access is separate from core application logic
* Why you should avoid save() and saveAll() functions in Spring
* Why schema and index design still matters in this case

The repository pattern is a design method that allows for abstraction between business logic and the data of an application. This allows for retrieving and saving/updating objects without exposing the technical details of how that data is stored in the main application. In this blog, we will use Spring Boot with MongoDB in order to create a repository pattern-based application.

Spring Boot applications generally have two main components to a repository pattern: standard repository items from spring---in this case, MongoRepository---and then custom repository items that you create to perform operations beyond what is included with the standard repository.

The code in this article is based on the [grocery item sample app](https://github.com/mongodb-developer/mongodb-springboot). View the [updated version of this code](https://github.com/mongodb-developer/RepositryModelSpring) used in this article.

The Spring standard repository {#h2-1-the-spring-standard-repository}
---------------------------------------------------------------------

The standard repo items can extend the base MongoRepositry class. This greatly reduces the amount of code needed for standard CRUD operations. Note the use of the @Query annotation to allow for shorthanding the various functions---for example:

```
public interface ItemRepository extends MongoRepository<GroceryItem, String> {
   @Query("{name:'?0'}")
   GroceryItem findItemByName(String name);
   @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
   List<GroceryItem> findAll(String category);
   public long count();
}
```


Here, we define two different query functions:

* **findItemByName** : This passes the query {name: '\<value\>'} to the find function in MongoRepository. As seen by the declaration, it returns a single GroceryItem. This maps to the MongoDB *findOne* function and translates to the MongoDB query:

```
   db.groceryitem.findOne({"name" : "<value passed in>"})
```


* **findAll** : This returns a list of GoceryItems by category. Since this function returns a list, the MongoDB *find* function is called in order to return all items that meet the criteria. In this example, we add a projection using the 'fields' parameter to only return the 'name' and 'quantity' fields. The MongoDB find method is called under the covers:

```
db.groceryitem.find({"category" : "<value passed in>"}).

project({"name" : 1, "quantity" : 1})
```


Custom repository functions {#h2-2-custom-repository-functions}
---------------------------------------------------------------

Next, we need a repository model for our specific entity/collection CRUD handling in MongoDB. This is done using the 'CustomItemRepository' class to define any functions we want to provide:

```
package com.example.mdbspringboot.repository;

public interface CustomItemRepository {
   void updateItemQuantity(String itemName, float newQuantity);
}
```


Note in this case, we're only providing a single function to update the item quantity.

Next, we need to implement the updateItemQuantity function using the CustomItemRepositoryImpl class:

```
package com.example.mdbspringboot.repository;
import …
@Component
public class CustomItemRepositoryImpl implements CustomItemRepository {

   @Autowired
   MongoTemplate mongoTemplate;
   public void updateItemQuantity(String name, float newQuantity) {
      Query query = new Query(Criteria.where("name").is(name));
      Update update = new Update();
      update.set("quantity", newQuantity);
      UpdateResult result = mongoTemplate.updateFirst(query, update, GroceryItem.class);
      if(result == null)
         System.out.println("No documents updated");
      else
         System.out.println(result.getModifiedCount() + " document(s) updated..");
   }
}
```


In this case, we're sending an update to the database to do the actual update. We'll see why that's a good idea in the next section.

When requirements change {#h2-3-when-requirements-change}
---------------------------------------------------------

At some point, the requirement to change the item category was added. A developer added the function 'updateCategoryName' to the MdbSpringBootApplication app. This also moves data operations out of the repository functions and directly into the application code. In general, this is not a good idea as it breaks the abstraction between the application and the repository model:

```
public void updateCategoryName(String category) {
// Change to this new value
String newCategory = "munchies";

// Find all the items with the category
List<GroceryItem> list = groceryItemRepo.findAll(category);
list.forEach(item -> {
    // Update the category in each document
    item.setCategory(newCategory);
});

// Save all the items in database
List<GroceryItem> itemsUpdated = groceryItemRepo.saveAll(list);
if(itemsUpdated != null)
    System.out.println("Successfully updated " + itemsUpdated.size() + " items.");       
}
```


Although this uses the base repository function saveAll() to do the update, it's likely to result in poor performance for a few reasons:

* All items for the category are returned to the client.
* The saveAll function is suboptimal in terms of performance---the collection must be queried in order to determine which items in the list need to be inserted versus which ones need to be updated.
* The entire document is being sent to the DB for replacement when only a single field is being changed. This is a known [anti-pattern in MongoDB](https://learn.mongodb.com/courses/schema-design-anti-patterns/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=repository-pattern-mongodb&utm_term=megan.grant) as it results in more network traffic as well as oplog bloat.

A better approach is to forgo the standard save and saveAll repository functions and send the update directly to the DB. This will result in much better performance and reduced oplog traffic. Be sure there is an index on the 'category' field for best performance. This is especially true if this is going to be a common update.

For this, we'll want to add a function to our CustomItemRepository class to do the update on the DB side rather than retrieving a list of documents and replacing them all:

void bulkUpdateItemCategories(String category, String newCategory);

We'll also need to write the implementation in CustomItemRepositoryImpl using the updateMulti repo function:

```
public void bulkUpdateItemCategories(String category, String newCategory) 
{
   Query query = new Query(Criteria.where("category").is(category));
   Update update = new Update();
   update.set("category", newCategory);
   UpdateResult result = mongoTemplate.updateMulti(query, update, GroceryItem.class);
   if(result == null)
      System.out.println("No documents updated");
   else
      System.out.println(result.getModifiedCount() + " document(s) updated..");
}
```


This makes the update in the main application much simpler as well as moving the data interface out of the main application:

```
public void updateCategoryName(String category) {
// Change to this new value
String newCategory = "munchies";
customRepo.bulkUpdateItemCategories(category, newCategory);
}
```


The double-edged sword of Spring updates in MongoDB {#h2-4-the-double-edged-sword-of-spring-updates-in-mongodb}
---------------------------------------------------------------------------------------------------------------

In the revised code examples above, we wrote our own update statement to change a category. This is preferred to the original code of reading all items to the client, updating, and then calling the saveAll repository function for several reasons:

* Extra data and network I/O are incurred by pulling all documents to the client and sending them all back to the DB for update.
* Performance could be a severe problem if a large number of documents need to be updated.
* This uses the standard MongoRepository saveAll() function to update an existing document, which is generally considered an anti-pattern in MongoDB.

Why should we avoid save() and saveAll() when updating documents? The main reasons to avoid these are for network traffic and oplog bloat. Let's discuss these individually.

### Increased network traffic {#h3-5-increased-network-traffic}

In the original example, changing the category of a set of items required each document to be retrieved to the client. When there are only a couple of documents, this amount of overhead won't make much of a difference. However, imagine the amount of traffic we would incur if there were thousands of items that had to have the category changed. This would also potentially consume a great deal of memory on the client fetching this list.

When the saveAll() method is called, Spring will iterate through each document in the list to determine if it's a new document needing to be inserted, or an existing one needing to be replaced. This is also a drag on performance as it must iterate through the list and check for the existence of the document by _id and then decide what to do. Each document in this list will be sent to the DB one by one. This is also a non-atomic operation, which could result in a partial update should there be some sort of error or outage.

### Oplog bloat and replacing documents {#h3-6-oplog-bloat-and-replacing-documents}

The MongoDB operation log (or oplog) is how MongoDB replicates writes from the primary to secondaries. The oplog is a capped collection in MongoDB, meaning it is a fixed size. As the size of each operation grows in the oplog, fewer can fit before the oldest ones are overwritten in the collection. This translates into a smaller oplog window, which is the time a secondary can be offline and able to catch up when coming back online.

Let's see an example. The standard save() method will replace the entire document based on the existing _id. The resulting oplog entry would look something like this (some fields are eliminated for brevity):

```
{
  "op": "u", // for Update
  "ns": "mygrocerylist.GroceryItem",
  "o2": {"_id": "Whole Wheat Biscuit"},
  "o": {
    "_id": "Whole Wheat Biscuit",
    "name": "Whole Wheat Biscuit",
    "quantity": 5,
    "category": "munchies",
    "_class": "com.example.mdbspringboot.model.GroceryItem"
  }
}
```


Using the 'updateMulti' function in our bulkUpdateItemCategories function, this translates to an update of a single field using the MongoDB $set operator. The oplog entry would be smaller, in this case:

```
{
  "op" : "u", //  for update
  "ns": "mygrocerylist.GroceryItem",
  "o2": {"_id": "Whole Wheat Biscuit"},
  "o" : { "$set" : { "category" : "munchies" } } 
}
```


In the case of the first example, all of the highlighted fields have not changed and are simply bloating the oplog. Imagine if this document had 200 fields---we would be including *all* of the fields in the oplog for a single field update! When updating documents, it's best to write your own repo functions to avoid sending all of the document's fields to the DB for replacement. Use the updateXXX repo functions to provide an update that uses the $set MongoDB function under the covers.

Why schema and indexing matter {#h2-7-why-schema-and-indexing-matter}
---------------------------------------------------------------------

Regardless of what repository model you use, good schema design is key for performant operations in MongoDB. You may be tempted to embed GroceryItems in another collection as an array. This is fine as long as nearly all carts have a reasonable number of GroceryItems (\<=200). Once arrays grow beyond 200 or so items, performance can suffer. In addition, updating the category of a few items in the cart could be very inefficient if you're using the save() method to replace the entire document. Updates to individual array items would be more efficient.

Indexing also matters a great deal at scale. Note that we have two update functions in our repository:

* bulkUpdateItemCategories, which updates an item using the 'category' field
* updateItemQuantity, which updates a quantity based on the item 'name'

For best performance, both the fields 'category' and 'name' should have an index. In our small example, there are only a few documents in this collection. Imagine how poorly this would perform doing these updates for thousands or even millions of items! The cost of not having an index on these fields can be catastrophic in terms of performance, when at scale.

Conclusion {#h2-8-conclusion}
-----------------------------

The repository model can (and should) be used to abstract database I/O from the core application logic. This has several benefits:

* Code is less complex
* Less effort to change the schema of your documents
* Business rules can be incorporated in custom repo functions

Keeping the core application logic separate from database CRUD operations makes changes easier to implement and test---on both sides of the application.

Avoid using the standard Spring save() and saveAll() methods to update documents. They take a brute force approach by replacing the entire document, which can lead to significantly more network traffic, poor performance, and negative impacts on cluster availability due to oplog bloat.

As with any software, the concepts of schema design as well as indexing strategy are very important to ensure that the system performs well at scale.

### Further reading {#h3-9-further-reading}

* [MongoDB oplog](https://www.mongodb.com/docs/manual/core/replica-set-oplog/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=repository-pattern-mongodb&utm_term=megan.grant)

<!-- -->

* [MongoDB indexing strategies](https://www.mongodb.com/docs/manual/applications/indexes/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=repository-pattern-mongodb&utm_term=megan.grant)

<!-- -->

* [Spring Data for MongoDB](https://spring.io/projects/spring-data-mongodb)
