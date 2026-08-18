---
title: "CQRS in Java: Separating Reads and Writes Cleanly"
slug: "cqrs-in-java-separating-reads-and-writes-cleanly"
date: "2026-04-16T20:25:28+00:00"
lastmod: "2026-04-17T18:38:14+00:00"
description: "The Command Query Responsibility Segregation (CQRS) pattern is a design method that segregates data access into separate services for reading and writing data. This allows a higher level of maintainability in your applications, especially if the schema or requirements change frequently. This pattern was originally developed with separate read and write sources in mind. However, implementing CQRS for a single data source is an effective way to abstract data from the application and make maintenance easier in the future. In this blog, we will use Spring Boot with MongoDB in order to create a CQRS pattern-based application."
authors:
  - "mike-laspina"
image: "Screenshot-2026-04-10-at-12.56.48-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-online-archive-efficiently-manage-the-data-lifecycle"
  - "mongodb-search-score-breakdown"
enlighterjs: true
frozen: false
---

### What you'll learn

* How the MongoDB Spring repository can be used to abstract MongoDB operations
* Separating Reads and Writes in your application
* How separating these can make schema design changes easier
* Why you should avoid save() and saveAll() functions in Spring

The Command Query Responsibility Segregation (CQRS) pattern is a design method that segregates data access into separate services for reading and writing data. This allows a higher level of maintainability in your applications, especially if the schema or requirements change frequently. This pattern was originally developed with separate read and write sources in mind. However, implementing CQRS for a single data source is an effective way to abstract data from the application and make maintenance easier in the future. In this blog, we will use Spring Boot with MongoDB in order to create a CQRS pattern-based application.

Spring Boot applications generally have two main components to a repository pattern: standard repository items from Spring---in this case, MongoRepository---and then custom repository items that you create to perform operations beyond what is included with the standard repository. In our case, we will be using 2 custom repositories - ItemReadRepository and ItemWriteRepository to segregate the reads and writes from each other.

The code in this article is based on the [grocery item sample app](https://github.com/mongodb-developer/mongodb-springboot). View the [updated version of this code](https://github.com/mongodb-developer/CQRSModelSpring) used in this article. Note the connection string in the application.properties file passes the app name of 'devrel-blog-java-cqrs' to the DB.

## The Spring standard repository

The standard repo items can extend the base MongoRepository class. This greatly reduces the amount of code needed for standard CRUD operations. However, if we're using the CQRS pattern, we'll want to use custom repositories for both the read and write functions to the DB. We can still use standard Spring functions for some of our read functionality.

## Creating separate repositories for the read and write

To implement CQRS, we'll need two custom repos in Spring. One for the reads and one for the writes. Although this may add a little bit of complexity up front, this will make it easier to modify going forward. Consider the following changes that may need to be made in the future:

* Adding the ability to send changes to Pub/Sub, such as Kafka.
* Changes to document schema as the system grows - in this case, consider using the [schema versioning pattern](https://www.mongodb.com/company/blog/building-with-patterns-the-schema-versioning-pattern). Having separate read and write functionality makes changes much easier in this case.
* New security requirements, such as permissioning and encryption.
* Reading and writing to different data sources.

### The read Repository

Our first repository will be for data reading. Note that this repo only queries the DB. In our case, we'll call it ItemReadRepository:

```
public interface ItemReadRepository extends MongoRepository<GroceryItem, String> {
   @Query("{name:'?0'}")
   GroceryItem findItemByName(String name);
   @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
   List<GroceryItem> findAllbyCategory(String category);
   public long count();
}
```

Here, we define two different query functions plus a count function. In this case, we can use the standard MongoRepository functions by adding query annotations.

* **findItemByName** : This passes the query {name: '\<value\>'} to the find function in MongoRepository. As seen by the declaration, it returns a single GroceryItem. This maps to the MongoDB *findOne* function and translates to the MongoDB query:

```
db.groceryitem.findOne({"name" : "<value passed in>"})
```

* **findAllbyCategory** : This returns a list of GroceryItems by category. Since this function returns a list, the MongoDB *find* function is used to return all items that meet the criteria. In this example, we add a projection using the 'fields' parameter to only return the 'name' and 'quantity' fields. The MongoDB find method is called under the covers:

```
db.groceryitem.find({"category" : "<value passed in>"}).
project({"name" : 1, "quantity" : 1})
```

* **count**: This simply counts the items in the collection.

### The write repository

The next repository will be for writing data (C, U, and D of CRUD). This repo only writes to the DB. In our case, we'll call it ItemWriteRepository:

```
public interface ItemWriteRepository {
   void updateItemQuantity(String itemName, float newQuantity);
   void bulkUpdateItemCategories(String category, String newCategory);
   void deleteAll();
   void deleteById(String id);
   void insert(GroceryItem item);
}
```

As discussed in my previous blog regarding Spring I/O and MongoDB updates, it's best to provide your own write functionality rather than relying on Spring's brute force approach of replacing the entire document - see the section regarding the Double-Edge Sword of Spring and MongoDB below.

We use the ItemWriteRepositoryImpl class in order to specify the exact operations to be carried out for each write function:

```
public class ItemWriteRepositoryImpl implements ItemWriteRepository {
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
         System.out.println(result.getModifiedCount() + " document(s) updated");
   }

   public void bulkUpdateItemCategories(String category, String newCategory) {
      Query query = new Query(Criteria.where("category").is(category));
      Update update = new Update();
      update.set("category", newCategory);
      UpdateResult result = mongoTemplate.updateMulti(query, update, GroceryItem.class);
      if(result == null)
         System.out.println("No documents updated");
      else
         System.out.println(result.getModifiedCount() + " document(s) updated");
   }

   public void deleteAll() {
      DeleteResult result = mongoTemplate.remove(new Query(), GroceryItem.class);
      if(result == null)
         System.out.println("No documents deleted");
      else
         System.out.println(result.getDeletedCount() + " document(s) deleted");
   }

   public void insert(GroceryItem itm){
      GroceryItem result =  mongoTemplate.insert(itm);
      if(result == null)
         System.out.println("No document inserted");
      else
         System.out.println("  " + result.getName() + " inserted");
   }

   public void deleteById(String id){
      Query query = new Query();
      query.addCriteria(Criteria.where("_id").is(id));
      DeleteResult result = mongoTemplate.remove(query, GroceryItem.class);
      if(result == null)
         System.out.println("No documents deleted");
      else
         System.out.println(result.getDeletedCount() + " documents deleted");
   }
}
```

## When requirements change

At some point in the future, we decide to add validation to the groceryItem so that the quantity must be greater than zero. Since we don't need to worry about this when reading data, we only need to modify the ItemWriteRepository in order to implement this check:

```
public boolean validate(GroceryItem itm){
   boolean result = true;
   if(itm.getItemQuantity() <= 0)
   {
      System.out.println("  **Validation error - quantity for item " +
            itm.getName() + " must be greater than zero.**");
      result = false;
   }
   return result;
}
```

We must also call the new validator - here's the modified insert function:

```
public void insert(GroceryItem itm){
   if(validate(itm)) {
      GroceryItem result = mongoTemplate.insert(itm);
      if (result == null)
         System.out.println("No document inserted");
      else
         System.out.println("  " + result.getName() + " inserted");
   }
   else
      System.out.println("  Could not insert " + itm.getName() + " due to validation error above.");
}
```

In our code example, we also have an "updateItemQuantity" function. Since this only takes a name and a new quantity, the rule regarding quantity \> 0 can be applied in this function by checking the 'newQuantity' parameter before doing the update. When using the CQRS pattern with Spring, we can be certain that we only need to update a single repository ItemWriteRepository, to ensure that ALL sources of application writes will apply the rules consistently.

## The double-edged sword of Spring updates in MongoDB

I've discussed this previously in my article [Building Java Microservices with the Repository Pattern](https://foojay.io/today/building-java-microservices-with-the-repository-pattern/), but it bears repeating here. In the code examples above, we wrote our own update statement to change a category. This is preferred to calling the saveAll repository function for several reasons:

* Extra data and network I/O are incurred by pulling all documents to the client and sending them all back to the DB for update.
* Performance could be severely affected if a large number of documents need to be updated.
* This uses the standard MongoRepository saveAll() function to update an existing document, which is generally considered an anti-pattern in MongoDB.

Why should we avoid save() and saveAll() when updating documents? The main reasons to avoid these are for network traffic and oplog bloat. Let's discuss these individually.

### Increased network traffic

In the original example, changing the category of a set of items required each document to be retrieved from the DB to the client. When there are only a couple of documents, this amount of overhead won't make much of a difference. However, imagine the traffic we would incur if there were thousands of items that had to have their categories changed. This could also consume a significant amount of memory on the client fetching this list.

When the saveAll() method is called, Spring will iterate through each document in the list to determine if it's a new document needing to be inserted or an existing one needing to be replaced. This is also a drag on performance as it must iterate through the list, check for the existence of the document by _id, and then decide what to do. Each document in this list will be sent to the DB individually. This is also a non-atomic operation, which could result in a partial update should there be some sort of error or outage.

### Oplog bloat and replacing documents

The MongoDB operation log (or oplog) is how MongoDB replicates writes from the primary to secondaries. The oplog is a capped collection in MongoDB, meaning it is typically a fixed size. Although this collection can grow beyond its configured size, it's best to minimize data from each operation in order to reduce storage requirements and data going across the network. As the size of each operation grows in the oplog, fewer can fit before the oldest ones are overwritten in the collection. This translates into a smaller oplog window, which is the time a secondary can be offline and able to catch up when coming back online.

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

## Conclusion

The Command Query Responsibility Segregation (CQRS) model can (and should) be used to separate read and write logic. This has several benefits:

* Less complexity when implementing business logic in the application
* Easier to maintain - all logic can be implemented in the write repository
* Easier to add functionality - for example, sending updates to Kafka for downstream systems
* Easy to separate read and write sources if needed
* Less effort to change the schema of your documents

There are a few downsides to CQRS:

* The initial code can appear more complicated as there are read and write repositories to maintain. This may not be worth the effort for a very simple microservice that reads and writes to a single DB.
* Needs to be enforced - your organization's code review process should ensure that these are kept separate in order to ensure maintainability going forward

Avoid using the standard Spring save() and saveAll() methods to update documents. They take a brute force approach by replacing the entire document, which can lead to significantly more network traffic, poor performance, and negative impacts on cluster availability due to oplog bloat.

### Further reading

* [Building Java Microservices with the Repository Pattern](https://foojay.io/today/building-java-microservices-with-the-repository-pattern/)

<!-- -->

* [MongoDB oplog](https://www.mongodb.com/docs/manual/core/replica-set-oplog/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=repository-pattern-mongodb&utm_term=megan.grant&utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_term=hugh.murray)

<!-- -->

* [Spring Data for MongoDB](https://spring.io/projects/spring-data-mongodb)
