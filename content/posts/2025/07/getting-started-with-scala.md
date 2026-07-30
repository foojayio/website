---
title: "Getting Started With Scala"
slug: "getting-started-with-scala"
date: "2025-07-16T06:41:05+00:00"
lastmod: "2025-07-16T06:42:21+00:00"
description: "At Quantexa, we love Scala. This may be the first dedicated article on Foojay.io about Scala. I hope my colleagues and I can add more in due course."
authors:
  - "steve-wilcockson"
image: "https://foojay.io/wp-content/uploads/2025/07/Scala1.png"
categories:
  - "Data Engineering"
  - "Scala"
tags:
related_posts:
frozen: false
---

**Why Scala?**

At [Quantexa](https://www.quantexa.com/), we love Scala. This may be the first dedicated article on Foojay.io about Scala. I hope my colleagues and I can add more in due course.  

Scala is built on the Java Virtual Machine (JVM). For foojay.io readers, this is "stating the bleeding obvious," but it means it compiles to Java bytecode and runs on the same runtime environment as Java. This brings significant enterprise scalability, resilience and big data ecosystem benefits.

Scala is extremely popular in data engineering (e.g., Apache Spark), concurrent systems (e.g., Akka), and functional programming. It is a first class citizen for operating with Spark, while tools like Spark MLlib, Akka Streams, and Scala-based ETL frameworks make Scala a solid choice for end-to-end data engineering workflows. Through the JVM, it can interoperate with Hadoop, Kafka, Flink, Elasticsearch, Solr and more, those classics of the OpenJDK and JVM-based big data ecosystem.   

At Quantexa, we extensively use Spark for batch processing. We also deploy Elasticsearch and OpenSearch for dynamic in-memory processing. Elsewhere, we use Python, e.g. for innovation and data science capabilities, but Scala is our production language of choice. It is the glue which binds our stack, and provides the foundation for our key capabilities. If you are enthusiastic about Scala, data engineering and/or Spark, [Quantexa is a great place to work](https://www.quantexa.com/careers/vacancies/).
![](/images/posts/2025/07/getting-started-with-scala/PlatformArchitecture-1024x516.png) ***Fig 1:**Scala is the glue for the Quantexa Platform and helps integrate with customer ecosystems*.

Quantexa's application layer is predicated on entity resolution, graphs and scoring capabilities. They underpin highly contextualized and accurate data and analytics products that service human-in-the-loop ["Decision Intelligence"](https://www.linkedin.com/pulse/must-read-books-decision-intelligence-david-pidsley-zcdke/) use cases. Quantexa's customers apply them for tangible outcomes, for example to detect financial and non-financial crimes, to perform know your customer (KYC) and to sharpen customer intelligence. They also deploy it in their enterprise layer to improve data management (through entity quality) upstream and populate AI pipelines with [AI-ready data](https://www.quantexa.com/discover/ai/) downstream.  

**Functional Programming Overview**   

In our experience, developers should seek to understand Scala in conjunction with Functional Programming.   

While not unique to Scala, functional programming is a paradigm in which we try to bind everything in pure mathematical functions style. It is a declarative type of programming, where the main focus is on "what to solve" in contrast to an imperative style where the main focus is "steps to solve". It uses expressions instead of statements - an expression is evaluated to produce a value whereas a statement is executed to assign variables. The Scala courses below will introduce you to the functional programming paradigm using Scala.  

**Some best practice guidelines**:

* use sensible and explicit variable names
* its all about readability - your code should tell a story
* write functional reusable code - if you are repeating a lot of code, there is probably a better way to write it
* error handling is there to handle edge cases and help with debugging

**Online Scala Courses**

The following courses will give you a good foundation in Scala. It should take around 1-2 days to understand the basics.   

The first is taught by [Martin Odersky](https://people.epfl.ch/martin.odersky?lang=en), the creator of the Scala language. From this, you will gain a knowledge base of essential syntax and concepts.   

The second is half theory, half practice with hands on coding exercises. It provides a balanced and thorough introduction to the whole language and its concepts, as well as the practical skills to code in Scala. There is a small fee attached to this course. Course 1 should suffice, but if you wish to build on your knowledge and apply it to some more practical examples, you can undertake the second course  

The two Scala courses are listed below.  

**Scala Course 1: Functional Programming Principles in Scala taught by Martin Odersky**   
[Functional Programming Principles in Scala](https://www.coursera.org/learn/scala-functional-programming?specialization=scala)   

This course is offered by the École Polytechnique Fédérale de Lausanne (EPFL). You will need to create an id to log on, but from that point you can 'audit' the course without doing the exercises. Once in, navigate past the setup pages to the first video of Martin Odersky. Then, we recommend reviewing these sections.  

1. Getting Started  

Week 1 - Introduction 1m  

Lecture 1.1 - Programming Paradigms 2m  

Lecture 1.2 - Elements of Programming 12m  

Lecture 1.3 - Evaluation Strategies and Termination 4m  

Lecture 1.4 - Conditionals and Value Definitions 8m  

2. Higher Order Function  

Week 2 - Introduction 30sec  

Lecture 2.1 - Higher-Order Functions 8m  

Lecture 2.2 - Currying 15m  

Lecture 2.4 - Scala Syntax Summary 4m  

Lecture 2.5 - Functions and Data 10m  

Lecture 2.7 - Evaluation and Operators 13m  

3. Data and Abstraction  

Week 3 - Introduction 1m  

Lecture 3.1 - Class Hierarchies 17m  

Lecture 3.2 - How Classes Are Organized 9m  

Lecture 3.4 - Objects Everywhere 14m  

Lecture 3.5 - Functions as Objects 5m  

4. Types and Pattern Matching  

Lecture 4.2 - Pattern Matching 11m  

Lecture 4.5 - Subtyping and Generics 9m  

This can be used in conjunction with Martin Odersky's book: Programming in Scala  
[https://people.cs.ksu.edu/\~schmidt/705a/Scala/Programming-in-Scala.pdf](https://people.cs.ksu.edu/~schmidt/705a/Scala/Programming-in-Scala.pdf)  

**Scala Course 2: Scala Applied**   
<https://www.udemy.com/stairway-to-scala-applied-part-1/learn/v4/overview>  

**Key Concepts**   

These videos will give an introduction to some of the key features used in Scala: Case Classes, Traits and Pattern Matching.  

Case Classes Explained (Youtube): [Here](https://www.youtube.com/watch?v=HqQfXUyACHw)  

- Explains some of the extra features Case Classes give.  

Scala Traits use case (Youtube): [Here](https://quantexa.atlassian.net/wiki/spaces/Training/pages/3623098062/1.3+-+Scala+Basics+v2#:~:text=use%20case%20(Youtube)%3A-,Here,-An%20example%20of)  

- An example of a single use case to understand what the trait is  

Case Class and Pattern Matching (Youtube): [Here](https://www.youtube.com/watch?v=wXgms3BH6Ns)  

- Walk-through of Pattern Matching with Case Class   

**Further Scala Materials**   

The following articles, links and videos will help give you an understanding of some key Scala features we enjoy at Quantexa.
![Scala Code](/images/posts/2025/07/getting-started-with-scala/Scala1-1024x594.png) ***Fig 2:*** *In this code, our 'Project Example', we join two datasets into a single nested dataset and collect some population metrics.*

**Case Classes**   

Case Classes have the benefit that they are immutable compared to regular classes and also offer additional methods.   

Useful Links:  

Case Class Tutorial (Youtube): [Here](https://quantexa.atlassian.net/wiki/spaces/Training/pages/3623098062/1.3+-+Scala+Basics+v2#:~:text=(Youtube)%3A-,Here,-Example%20of%20using)  

- Example of using a Case Class  

Case Classes: [Here](https://docs.scala-lang.org/tour/case-classes.html)  

- Official Scala overview of Case Classes  

Learning Scala Chapter 8 \& 9 (eBook - O'Reilly): [Here](https://www.oreilly.com/library/view/learning-scala/9781449368814/ch09.html)  

**Traits**   

A trait encapsulates method and field definitions, which can then be reused by mixing them into classes. Unlike class inheritance, in which each class must inherit from just one superclass, a class can mix in any number of traits.  

Useful Links:  

Scala Traits Tutorial (Youtube): [Here](https://youtu.be/KARUSWj8sZs)  

- Gives a high-level example of how traits work across classes.  

Traits: [Here](https://docs.scala-lang.org/tour/traits.html)  

- Official Scala overview of traits  

Learning Scala Chapter 9 (eBook - O'Reilly): [Here](https://www.oreilly.com/library/view/learning-scala/9781449368814/ch09.html)  

**Pattern Matching**   

Pattern Matching is used to check a value against a specific pattern. It can be used in place of several if-else statements.

<br />

**Useful Links**

* Walk-through of Pattern Matching with Case Class Pattern Matching: [Here](https://docs.scala-lang.org/tour/pattern-matching.html) and [Here](https://www.baeldung.com/scala/pattern-matching)
* Official Scala overview of Pattern Matching, and a Baeldung.com overview

**Key Learning Points**

* Do you understand the difference between a Class, Case Class and an Object? What are the advantages to using a Case Class?
* In what cases is Pattern Matching better than IF statements and why is it better in these cases?

**And Finally!**

Like the Java User Groups (JUGs) frequented by Foojay.io readers, there are many active Scala communities to join, learn and network.

[Quantexa's London-based developers collaborate with and support, for example, the London Scala User Group](https://x.com/londonscala/status/1880013845282136330).

In Spain, my friends at [Habla Computing](https://hablapps.com/) contribute to [Scala Programming Madrid](https://www.youtube.com/@ScalaProgrammingMadrid?app=desktop). There are many more.

To learn more about Open Source at Quantexa, including in areas such as NLP and graph projects not discussed in this article, [read this Quantexa Community article](https://community.quantexa.com/kb/getting-started-on-the-quantexa-platform/open-source-at-quantexa/39256).
