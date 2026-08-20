---
title: "Book review: \"Persistence Best Practices for Java Applications\""
date: "2023-10-27T08:56:21+00:00"
lastmod: "2023-10-27T08:56:22+00:00"
description: "I can heartily recommend this book, it offers a lot of valuable insights into persistence in Cloud computing, the involved technologies, and technical and architectural considerations by two developers with years of experience."
authors:
  - "simon-verhoeven"
image: "image-830x1024.jpg"
categories:
  - "Book Review"
  - "Books"
  - "nosql"
  - "sql"
related_posts:
  - "book-review-api-design-patterns"
  - "book-review-designing-apis-with-swagger-and-openapi"
  - "book-review-effortless-cloud-native-app-development-using-skaffold"
  - "book-review-tidy-first"
frozen: false
---

**In todays ever-evolving world, fast and efficient data management is becoming ever more important. With the explosion of digital data and diverse data sources, selecting the right database type, whether it's traditional relational, NoSQL, or emerging options like NewSQL, has become pivotal. This also leads us to the next challenge for us, how do we integrate with this? Do we use JPA/JOOQ/... ? Where/how do we map our data? What is a polyglot setup, and should we be using it?**

Luckily all these topics, and so many more are covered in the magnificent new book "Persistence Best Practices for Java Applications" by [Otàvio Santana](https://otaviojava.com/) and [Karina Varela](https://karinavarela.me/)  

{{< img src="image-830x1024.jpg" class="aligncenter size-large is-resized" width="456" height="563" style="width:456px;height:563px" >}}

## About the book

price: €27.99 for the eBook  

publication date: August 2023  

publisher: Packt  

pages: 202  

ISBN: 9781837631278

## Content

The book itself consist of 3 overarching sections:

### Persistence in cloud computing

* a brief history of persistence, and involved trade-offs with distributed/cloud systems
* the different flavours of databases: relational, NoSQL (and it's subvariants) \& NewSQL
* possible architectures and strategies, including implications \& pitfalls
* persistence layer design patterns \& mapping strategies (or lack thereof)

### Jakarta EE, MicroProfile, modern persistence technologies \& their trade-offs

* Jakarta EE and JPA: what's the state, what can be achieve with Quarkus \& Panache, and performance considerations
* NoSQL: what do we need to keep in mind, what can we achieve with JNoSQL, and what are the pros/cons of the different variants
* JOOQ: what is it, and why should I consider using it?
* in-memory persistence: use cases, and what is the object-relational impedance mismatch

### Architectural considerations

* Polyglot setups: what are the (dis)advantages?
* architecting distributed systems: what are some anti-patterns and fallacies, and how to resolve them
* modernization strategies \& data integration: anti-patterns \& bad practices to avoid, change data capture, cloud technologies \& offerings
* the power of documentation, data-domain testing and the importance of proper architecture

## My thoughts

I can heartily recommend this book, it offers a lot of valuable insights into persistence in Cloud computing, the involved technologies, and technical and architectural considerations by two developers with years of experience.

The matter is not only approached from a theoretical viewpoint, but also includes a lot of practical information alongside a lot of code examples in the [GitHub repository](https://github.com/PacktPublishing/Persistence-Best-Practices-for-Java-Applications).

Furthermore, it's delightful to see a book that takes JNoSQL and JOOQ into proper consideration, especially given their rising popularity.
