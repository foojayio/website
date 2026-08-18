---
title: "Modeling Relationships With Hibernate ORM and MongoDB"
slug: "modeling-relationships-with-hibernate-orm-and-mongodb"
date: "2025-11-04T17:08:49+00:00"
lastmod: "2025-12-12T21:20:20+00:00"
description: "In the previous article—Getting Started With Hibernate ORM and MongoDB—we learned how to configure Hibernate to work with MongoDB, create an entity, and perform basic CRUD operations using the familiar Hibernate API.If you haven’t read that first part yet, I recommend starting there before continuing. It covers the project setup, dependencies, and the fundamentals that we’ll build upon here.In this second part, we’ll extend our application to model relationships between entities—introducing a Review entity and linking it to our existing Book class. This will allow us to explore more advanced capabilities of the MongoDB Hibernate integration, including:Representing one-to-many relationships.Storing embedded data structures.Executing more complex queries.By the end of this tutorial, you’ll see how Hibernate and MongoDB can work together to model richer, interconnected data, all while using the same familiar annotations and APIs from the ORM world."
authors:
  - "ricardo-mello"
image: "546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
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

In the[previous article](https://foojay.io/today/getting-started-with-hibernate-orm-and-mongodb/)---*Getting Started With Hibernate ORM and MongoDB*---we learned how to configure Hibernate to work with MongoDB, create an entity, and perform basic CRUD operations using the familiar Hibernate API.

If you haven't read that first part yet, I recommend starting there before continuing. It covers the project setup, dependencies, and the fundamentals that we'll build upon here.

In this second part, we'll **extend our application** to model relationships between entities---introducing a Review entity and linking it to our existing Book class. This will allow us to explore more advanced capabilities of the MongoDB Hibernate integration, including:

* Representing one-to-many relationships.  
* Storing embedded data structures.  
* Executing more complex queries.  

By the end of this tutorial, you'll see how [Hibernate and MongoDB](https://www.mongodb.com/company/blog/product-release-announcements/introducing-mongodb-extension-for-hibernate-orm-public-preview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-hibernate-data-modeling&utm_term=ricardo.mello) can work together to model richer, interconnected data, all while using the same familiar annotations and APIs from the ORM world.

## Prerequisites

Before continuing, make sure you have the project from the first article up and running.  

That project includes the initial configuration, the Book entity, and all CRUD operations we'll build upon here.

If you don't have it yet, you can clone the repository from GitHub and check out the [**v1.0**](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v1.0) tag, which represents the state of the project at the end of the first article:

```
git clone https://github.com/mongodb-developer/mongodb-hibernate-crud.git

cd mongodb-hibernate-crud/

git checkout v1.0
```

Make sure your environment still meets the same requirements: Java 17+, Maven, and MongoDB 6.0+ (replica set enabled).

## One-to-many relationship

In the current version of our project, we have a single entity---**Book**---that represents the documents stored in the MongoDB books collection. Each book contains basic information like title and number of pages:

```
{
  "_id": ObjectId('68f2af48650f2f612e319c61'),
  "pages": 405,
  "title": "Mastering MongoDB"
}
```

Now, we'll extend this model to make it more realistic. A book can have multiple reviews, and each review belongs to a specific book, forming a classic one-to-many relationship.

### Approach 1: Embedding reviews inside books

*This section corresponds to Git* [*tag* *v2.0*](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v2.0).

The first approach we'll explore is to embed the reviews directly inside the Book document.  

This means each book will contain an array of reviews within the same collection, a common and simple concept in MongoDB when the relationship between entities is tightly coupled.

To represent this, we'll define a new class called Review, annotated with @Embeddable and @Struct, inside the domain package:

```
package com.mongodb.domain;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(name = "Review")

public class Review {
   private String author;
   private String title;
   private String comment;
   private double rating;
   //getters and setters
}
```

This tells Hibernate that the class ***does not*** represent a separate collection but rather an embedded structure inside another entity.

In the Book class, we can now add a list of reviews:

```
@Entity
@Table(name = "books")

public class Book {
  // other fields
   List<Review> reviews;
   public void addReview(Review review) {
      if (this.reviews == null) {
         this.reviews = new ArrayList<>();
      }
      this.reviews.add(review);
   }
 // getters and setters
}
```

To add a review to a book, we can use a simple method that loads the Book by ID, appends the review to the list, and merges the updated document back into MongoDB. In the BookService:

```
public boolean addReview(ObjectId bookId, Review review) {

   try (Session session = HibernateUtil.getSessionFactory().openSession()) {

      Transaction tx = session.beginTransaction();
      Book book = session.find(Book.class, bookId);

      if (book == null) return false;

      book.addReview(review);
      session.merge(book);
      tx.commit();

      return true;
   }
}
```

Finally, in the MyApplication class, add a new menu option for inserting reviews:

```
do {
   System.out.println("\n=== BOOK MENU ===");

   // Other options
   System.out.println("6 - Add Review");

   option = sc.nextInt();

   sc.nextLine();

   switch (option) {
   //other options

case 6 -> {
   System.out.print("Book ID: ");

   String id = sc.nextLine();

   System.out.print("Author: ");

   String author = sc.nextLine();

   System.out.print("Title: ");

   String title = sc.nextLine();

   System.out.print("Comment: ");

   String comment = sc.nextLine();

   System.out.print("Rating: ");

   double rating = sc.nextDouble();
   sc.nextLine();

   Review review = new Review(author, title, comment, rating);
   boolean ok = bookService.addReview(new ObjectId(id), review);
   System.out.println(ok ? "Review added successfully!" : "Book not found.");
}
}
```

When this code runs, Hibernate updates the document directly in the books collection. A Book document now looks like this:

```
{
  "_id": ObjectId('68f2af48650f2f612e319c61'),
  "pages": 405,
  "title": "Mastering MongoDB",
  "reviews": [
   {
     "author": "Ricardo",
     "comment": "This is my favorite book",
     "rating": 10,
     "title": "I love this book"
   },
   {
     "author": "Maria",
     "comment": "This book seems interesting",
     "rating": 8.2,
     "title": "Just started .."
   }
 ]
}
```

#### Evaluating the embedded model

This embedded approach offers some clear advantages.

1. Each Book document always contains its reviews, meaning that fetching a single book also retrieves all its related data---no extra queries or joins needed.
2. It also keeps the relationship consistent: If a book is deleted, all of its reviews are removed along with it, since they live inside the same document.

However, this strategy has an important limitation: If a book becomes extremely popular---a real **best-seller** ---the number of reviews may grow significantly, causing the document to become very large. In MongoDB, this can lead to a massive array known as an *unbounded array*, where an ever-growing array field negatively impacts performance and eventually hits the document size limit (16 MB).

To address that, the next step is to **move the reviews to their own collection**, allowing them to scale independently while still keeping a logical relationship to their book.

### Approach 2: Moving reviews to a separate collection

*This section corresponds to Git* [*tag* *v3.0*](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v3.0).

As we saw earlier, embedding reviews directly inside the Book document works well for small datasets, but it's not good when a book has thousands of reviews.

To make the data model more flexible and avoid the *unbounded array*, we'll now move the reviews into their own collection and store only a reference to the corresponding book.

#### Updating the data model

We'll start by simplifying the Book entity, **removing** the list of embedded reviews and keeping only the core fields:

```
@Entity
@Table(name = "books")

public class Book {
   @Id
   @ObjectIdGenerator
   @GeneratedValue

   ObjectId id;
   String title;
   Integer pages;
 // getters and setters
}
```

Next, we'll turn Review into a **full entity** mapped to its own collection:

```
import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.bson.types.ObjectId;

@Entity
@Table(name = "reviews")

public class Review {
  @Id
  @ObjectIdGenerator
  @GeneratedValue

  ObjectId id;
  private ObjectId bookId;
  private String author;
  private String title;
  private String comment;
  private double rating;

  public Review() {}
  public Review(String author, ObjectId bookId, String title, String   comment, double rating) {
    this.author = author;
    this.bookId = bookId;
    this.title = title;
    this.comment = comment;
    this.rating = rating;
  }

  @Override
  public String toString() {
    return "Review{author='%s', title='%s', comment='%s', rating=%.1f}"
          .formatted(author, title, comment, rating);
  }
}
```

By annotating the class with @Entity and @Table("reviews"), Hibernate now treats it as a top-level document collection instead of an embedded structure. Each review references its book using the bookId field, similar to a foreign key, but in MongoDB terms, it's just an ObjectId stored inside the document.

#### Updating the configuration

Since we now have two entities, we must register both in our Hibernate configuration. Open the HibernateUtil class and include the Review.class:

```
package com.mongodb.config;

import com.mongodb.domain.Book;
import com.mongodb.domain.Review;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
   private static final SessionFactory SESSION_FACTORY =
         new Configuration().configure("hibernate.cfg.xml")
               .addAnnotatedClass(Book.class)
               .addAnnotatedClass(Review.class)
               .buildSessionFactory();

   private HibernateUtil() {}
   public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }
}
```

#### Adding a review service

To handle review operations, we'll add a new service class:

```
package com.mongodb.service;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Review;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ReviewService {
   public void insert(Review review) {
      try (Session session = HibernateUtil.getSessionFactory().openSession()) {
         Transaction tx = session.beginTransaction();
         session.persist(review);
         tx.commit();
         System.out.println("Review inserted: " + review);
      }
   }
}
```

#### Linking books and reviews

Now that reviews live in their own collection, the Book entity no longer manages them directly.  

That means we can **remove the old** **addReview()** method from the Book class; reviews are no longer embedded inside the document, so Hibernate doesn't update them as part of the book entity anymore.

Instead, we'll add a helper method in the BookService to retrieve a single book along with all its associated reviews. This method loads the book document from the books collection and then queries the reviews collection to find matching entries by bookId.

**Note:** The @OneToMany annotation is not yet supported in the Public Preview of the MongoDB Hibernate ORM extension. For now, relationships between entities can be handled programmatically as shown below, and native support for annotations such as @OneToMany is planned for the GA release.

```
public BookWithReviews findAllBooksWithReviewsById(ObjectId id) {
   try (Session session = HibernateUtil.getSessionFactory().openSession()) {

      Book book = session.find(Book.class, id);

      if (book == null) {
         return null;
      }

      List<Review> reviews = session.createQuery(
                  "from Review r where r.bookId = :bookId", Review.class)
            .setParameter("bookId", book.getId())
            .list();

      return new BookWithReviews(book, reviews);
   }
}

public record BookWithReviews(Book book, List<Review> reviews) {}
```

#### Updating the console menu

Since the reviews are now stored in their own collection, the old addReview() method in BookService no longer exists. If you try to run the previous version of the menu, the option for adding a review will fail because that method was part of the embedded model.

To fix this, we'll replace that option with a new implementation that uses the **ReviewService** instead, inserting reviews directly into the reviews collection, and also add a new option to list all reviews for a specific book.

Here's how the updated section of your MyApplication class should look:

```
// other fields
ReviewService reviewService = new ReviewService();
int option;

do {
   System.out.println("\n=== BOOK MENU ===");
   //other options
   System.out.println("7 - List Books and Reviews by Id");
   option = sc.nextInt();
   sc.nextLine();

   switch (option) {
case 6 -> {
   System.out.print("Book ID: ");

   String bookId = sc.nextLine();

   System.out.print("Author: ");

   String author = sc.nextLine();

   System.out.print("Review Title: ");

   String rTitle = sc.nextLine();

   System.out.print("Comment: ");

   String comment = sc.nextLine();

   System.out.print("Rating: ");

   double rating = sc.nextDouble();
   sc.nextLine();
   reviewService.insert(new Review(author, new ObjectId(bookId), rTitle, comment, rating));
   System.out.println("Review added!");

}

case 7 -> {
   System.out.print("Book ID: ");

   String bookId = sc.nextLine();

   BookService.BookWithReviews br = bookService.findAllBooksWithReviewsById(new ObjectId(bookId));

   System.out.printf("\n%s - %s (%d reviews)\n",
         br.book().getId(), br.book().getTitle(), br.reviews().size());
   br.reviews().forEach(System.out::println);
}
```

Now, each time you insert a review, it's stored in a separate **reviews** collection while keeping a reference to the corresponding **bookId**. This approach eliminates the growth issue of embedded arrays and allows each collection to scale independently.

Unlike the previous model, the Book document no longer contains a reviews list.  

Instead, reviews are now stored as **independent documents** in their own collection, each linked back to its parent book through the bookId field.

Here's what the new structure looks like in MongoDB:

**books collection:**

```
{
 "_id": ObjectId("68f2be11aa80073def51e555"),
 "pages": 549,
 "title": "Learning Java"
}
```

**reviews collection:**

```
{
 "_id": ObjectId("68f2be3caa80073def51e556"),
 "author": "Ricardo Mello",
 "bookId": ObjectId("68f2be11aa80073def51e555"),
 "comment": "I like how this book ..",
 "rating": 8.5,
 "title": "An excellent book"
}
```

While this new structure solves the size and scalability issues of the embedded model, it also introduces a trade-off. Since Book and Review are now stored in separate collections, retrieving all reviews for a specific book requires two queries, one to load the book and another to fetch its reviews using the bookId.

In relational databases, this would be handled automatically with a join, but in MongoDB, joins are simulated through aggregation pipelines or multiple lookups at the application level. That's perfectly fine for moderate workloads, but as your dataset grows, repeated joins or multi-collection queries can impact performance and increase complexity. Because of that, it's often better to **avoid frequent joins** and keep the most relevant or recently accessed data close to the parent document.

To address this, we can apply a common modeling technique known as the **Subset Pattern**---which we'll explore in the next section.

### Approach 3: Bringing back recent reviews (Subset Pattern)

*This section corresponds to Git* [*tag v4.0*](https://github.com/mongodb-developer/mongodb-hibernate-crud/tree/v4.0).

The [**Subset Pattern**](https://www.mongodb.com/company/blog/building-with-patterns-the-subset-pattern/?utm_campaign=devrel&%20utm_source=third-part-content&utm_medium=cta&utm_content=mongodb-hibernate-data-modeling&utm_term=ricardo.mello) is useful when a document can potentially grow very large, but only a small portion of its data is frequently accessed. Instead of embedding all related documents (like every review ever written), we can embed only the most relevant subset, for example, **the last three**or five reviews.

#### Applying the Subset Pattern

In our case, we can apply the Subset Pattern by keeping only the **three most recent reviews** embedded inside each Book document.

```
{
 "_id": ObjectId("68f67da69114e74455c989ce"), 
 "pages": 406,
 "title": "High Performance with MongoDB"
 "recentReview": [
   {"author": "Martha", "comment": "A very practical guide that balances theory   with hands-on .." },
   {"author": "Ricardo", "comment": "The structure is well organized and the examples" },
   {"author": "Pietro",    "comment": "I found this book to be both informative and inspiring.." } 
 ]
}
```

This way, whenever we load a book, we immediately get its latest feedback, without the need to query another collection or perform a join.

At the same time, we'll still store **all reviews** in a separate reviews collection. That means if we ever need to display the complete review history for a book (for example, on a "See all reviews" page), we can simply query that collection directly.

```
{
 "_id": ObjectId("68f67dd79114e74455c989d1"), 
 "author": "Martha",
 "bookId": ObjectId("68f67da69114e74455c989ce"),
 "comment": "A very practical guide that balances theory with hands-on ..",
 "rating": 8.2,
 "title": "Practical and Insightful"
}
```

This approach offers a great balance:

* **Fast access** to the most relevant information (the latest reviews).  
* **Scalability** for long-term data growth (older reviews stored separately).  

In this simplified example, we're embedding only the author and comment fields inside the book. But in a real-world scenario, we could include additional fields such as rating, date, or short summaries, depending on what's most useful for quick access.

### Implementing the Subset Pattern

To keep our documents lightweight and still show the latest feedback, we'll embed only the **three most recent reviews** inside each Book, while continuing to store all reviews in the separate reviews collection. Here's the step-by-step implementation:

#### Create the embeddable type for recent reviews

```
package com.mongodb.domain;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(name = "RecentReview")

public record RecentReview (
      String author,
      String comment)
{}
```

#### Add the recentReview field to the Book class

```
@Entity
@Table(name = "books")

public class Book {
   // other fields
   List<RecentReview> recentReview = new ArrayList<>();
   // getters and setters
}
```

#### Update the ReviewService

After persisting a new review, we'll run a native MongoDB update to push the latest review into the recentReview array and keep only the last three entries.

```
package com.mongodb.service;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import com.mongodb.domain.Review;
import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ReviewService {
   public void insert(Review review) {
      try (Session session = HibernateUtil.getSessionFactory().openSession()) {
         Transaction tx = session.beginTransaction();
         session.persist(review);
         addRecentReview(review.getBookId(), review, session);
         System.out.println("Review inserted: " + review);
         tx.commit();
      }
   }

   private void addRecentReview(ObjectId bookId, Review review, Session session) {
         var mql = """
           {
               "update": "books",
               "updates": [{
                   "q": { "_id": { "$oid": "%s" } },
                   "u": {
                       "$push": {
                           "recentReview": {
                               "$each": [{
                                   "author": "%s",
                                   "comment": "%s"
                               }],
                               "$slice": -3
                           }
                       }
                   }
               }]
           }

           """.formatted(
               bookId.toString(),
               escapeJson(review.getAuthor()),
               escapeJson(review.getComment())
         );

         int updated = session.createNativeQuery(mql, Book.class).executeUpdate();

         System.out.println(updated + " document updated.");
      }

      private String escapeJson(String input) {
         if (input == null) return "";

         return input.replace("\\", "\\\\")
               .replace("\"", "\\\"")
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t");
      }
   }
```

Notice that we're using createNativeQuery() to execute a raw MongoDB command (MQL) directly through Hibernate.  

This feature allows you to go beyond standard entity operations and run low-level MongoDB updates, inserts, or aggregates when you need full control.  

It's especially useful for applying specific operators, such as $push, $each, or $slice, that aren't yet abstracted by Hibernate's ORM layer.

How it works:

* **createNativeQuery()** sends a raw MongoDB command (MQL) through Hibernate.  
* **$push** appends a new element to the array.  
* **$slice: -3** trims the array to keep only the last three elements.  

### Testing the feature

To test this feature, simply run the application and choose option 6, Add Review from the console menu.  

Each time you add a new review:

* A document will be created in the reviews collection, containing all review details.  
* The corresponding Book document will be updated, adding that review to the recentReview list.

After inserting a few reviews for the same book, you should see that only the three most recent entries remain in the recentReview array, confirming that the $slice: -3 operator is working as expected.

## Wrapping up

Throughout this series, we explored three different strategies for modeling one-to-many relationships with Hibernate ORM and MongoDB. From embedding data directly inside documents to referencing separate collections and finally applying the Subset Pattern, we saw how flexible MongoDB can be when paired with a familiar ORM framework like Hibernate.

The key takeaway is that working with MongoDB requires a shift in mindset. Instead of thinking in rows and joins, we design around documents---taking advantage of powerful features such as embedded data, flexible schemas, and modeling patterns that align with real-world access needs. Each approach offers its own trade-offs, and choosing the right one depends on how your application reads, writes, and scales its data.

Whenever you need more control, remember that you can always fall back to the MongoDB Java Driver to run native MQL commands directly. It's a great option for edge cases where ORM abstractions might not yet expose certain MongoDB capabilities.

You can find the complete project---including all three strategies (*tags v1.0, v2.0, v3.0,* and*v4.0* )---on [GitHub](https://github.com/mongodb-developer/mongodb-hibernate-crud).
