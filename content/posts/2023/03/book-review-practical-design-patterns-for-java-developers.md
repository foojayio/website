---
title: "Book review - Practical Design Patterns for Java Developers"
slug: "book-review-practical-design-patterns-for-java-developers"
date: "2023-03-17T08:27:08+00:00"
lastmod: "2023-03-17T08:38:05+00:00"
description: "Ever wondered why you need design patterns and how to apply them? Miroslav Wengner's book has you covered."
canonical: "https://webtechie.be/post/2023-03-08-book-review-practical-design-patterns/"
authors:
  - "frankdelporte"
  - "johannes-bechberger"
image: "https://foojay.io/wp-content/uploads/2023/03/practical-cover.jpg"
categories:
  - "Book Review"
  - "Books"
  - "Java Core"
tags:
related_posts:
frozen: false
---

Hone your software design skills by implementing popular design patterns in Java {#h2-0-hone-your-software-design-skills-by-implementing-popular-design-patterns-in-java}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

This book is for sale on [Amazon in Kindle and Paperback editions](https://www.amazon.de/dp/180461467X).

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <img fetchpriority="high" decoding="async" width="830" height="1024" data-id="62853" src="/images/posts/2023/03/book-review-practical-design-patterns-for-java-developers/practical-cover-830x1024.jpg" alt="" class="wp-image-62853">
 </figure>
 <figure class="wp-block-image size-large">
  <img decoding="async" width="600" height="740" data-id="62852" src="/images/posts/2023/03/book-review-practical-design-patterns-for-java-developers/practical-backside.jpg" alt="" class="wp-image-62852">
 </figure>
 <figure class="wp-block-image size-large">
  <img decoding="async" width="426" height="426" data-id="62851" src="/images/posts/2023/03/book-review-practical-design-patterns-for-java-developers/miro.jpg" alt="" class="wp-image-62851">
 </figure>
</figure>

We'll start this post with the personal verdict of two avid readers of this book:

Frank Delporte's Verdict {#h2-1-frank-delporte-s-verdict}
---------------------------------------------------------

I've been programming since I was 10y old, but I graduated from film school as a video editor, so I never learned the "official" way to be a programmer. It was only by following courses and, most importantly, learning from colleagues that I started making "readable and maintainable" code, which is precisely the topic of this book.

After many years of being the only Java developer in a team of hardware and embedded engineers, I was introduced to SOLID and other principles when more Java colleagues joined and helped me to turn my "ugly" code to clean and structured projects. The book by Miroslav Wengner is precisely the kind of book I was missing when I got more experienced in Java development and wanted to uplift my career.

On top of that, this book also provides a lot of extra information related to the recent changes in the OpenJDK, which brought a lot of evolutions into the Java programming language and made it once more the preferred language for many different use cases.

The book's goal is to break a returning pattern many programmers go through during their careers. When you start using a programming language like Python, JavaScript, or even Java, the platform allows you to create messy code. Only when you learn and understand patterns and debugging and use a strongly typed language like Java do you start writing "real" code. When someone starts a programming career with Java and has a solid understanding of design patterns, many bad practices can be avoided.

Johannes Bechberger's Verdict {#h2-2-johannes-bechberger-s-verdict}
-------------------------------------------------------------------

When I first read the book's title, I thought, "another book on the good old design patterns," and wondered why I should bother reading it. But I was wrong; this book gives you far more than a short descriptive compilation of design patterns.

It gives you a great introduction to modern Java features and common pitfalls; this book is valuable even if you are not interested in design patterns. The author did an excellent job introducing the Java Runtime Environment, with many pointers to other superior reading materials.

The chapters on design patterns are also significant. They give a (slightly opinionated) view of all the major design patterns, complete with small examples and examples from the JDK. These chapters feel like an encyclopedia, which I'll probably revisit occasionally whenever I need to do a more complex architecture. I had courses on design patterns in university, but I wished I had this book then to guide me through these topics.

About the Author {#h2-3-about-the-author}
-----------------------------------------

Miroslav Wengner is a Java Champion and JavaOne Rockstar with an impressive career. He contributes to OpenJDK and various open-source projects (e.g., [Robo4J](http://www.robo4j.io/)) and is an executive committee member of the [Java Community Process (JCP)](https://jcp.org/). He is a principal engineer at OpenValue and a regular conference speaker and blogger.

Content {#h2-4-content}
-----------------------

The book is divided into three parts:

* Introducing Java and basic knowledge about design patterns.
* Standard design patterns using Java.
* Other essential patterns and anti-patterns.

The table of contents immediately shows how the description of each design pattern follows the same approach and is structured in a motivation, where to find it in OpenJDK, example code, and a conclusion.

There are a lot of references to the ["Gang of Four (GoF)" book](https://en.wikipedia.org/wiki/Design_Patterns). This book from 1994, with code examples in C++ and Smalltalk, describes 23 software design patterns. In his book, Miroslav extends the list to 42 practical design patterns, focusing on implementing and using them with Java code.

### Part 1: Design Patterns and Java Platform Functionalities {#h3-5-part-1-design-patterns-and-java-platform-functionalities}

This part covers the purpose of software design patterns and outlines the fundamental ideas of the APIE and SOLID design principles, with a thorough introduction to the Java platform.

#### Chapter 1: Getting into Software Design Patterns

All principles in Object-Oriented Programming (OOP) are based on Abstraction, Polymorphism, Inheritance, and Encapsulation (APIE), so this is the first topic described in this chapter. It is further used to introduce the SOLID design principle, which is the basis for all the other principles further described.

Design principles align with the fundamental pillars of OOP and APIE and promote the principles of SOLID. They also enforce the Don't Repeat Yourself (DRY) principle.

#### Chapter 2: Discovering the Java Platform for Design Patterns

As a Java developer, getting re-introduced to the platform may be strange. Still, this chapter dives deeper into Java history, how it works under the hoods, how memory is allocated and managed and describes the basics of threading and concurrency. This is all the required info to understand their impact on the principles fully. So it's both a refresh for experienced Java developers and an introduction for non-Java developers who want to compare it to another language.

Furthermore, this chapter briefly reviews the new Java features introduced from Java 11 up to Java 17, which you might have missed: Sealed classes, Pattern Matching, and more.

### Part 2: Implementing Standard Design Patterns Using Java Programming {#h3-6-part-2-implementing-standard-design-patterns-using-java-programming}

This central part of the book groups the design patterns into three categories: creational, behavioral, and structural.

#### Chapter 3: Working with Creational Design Patterns

The factory method, prototype pattern, dependency injection pattern,... can be used to achieve code maintainability and readability. They are vital for clean software composition. On top of that, better software may even help to keep [Moore's Law](https://en.wikipedia.org/wiki/Moore%27s_law) valid as they improve the performance of the software on top of the current hardware.

Eight creational design patterns are described and explained using Car themed examples.

#### Chapter 4: Applying Structural Design Patterns

Twelve structural design patterns focus on maintainable and flexible source code to create objects. They clarify relationships between created instances and make their purpose easier to understand. A few examples: bridge pattern, decorator pattern, and proxy pattern,...

#### Chapter 5: Behavioral Design Patterns

Fourteen patterns are described in this chapter! They provide transparent communication between objects, resulting in efficient memory allocations. These include the caching, command, iterator, observer, and template patterns.

### Part 3: Other Essential Patterns and Anti-Patterns {#h3-7-part-3-other-essential-patterns-and-anti-patterns}

#### Chapter 6: Concurrency Design Patterns

With the evolution in hardware since the GoF book, business requirements shifted increasingly into a concurrent and parallel world. The Java platform provides concurrency mechanisms to achieve this. Eight patterns, including the active object pattern, read-write lock pattern, and producer-consumer pattern,... are used to describe these functionalities.

In this chapter, multiple Java Flight Recorder screenshots are included to visualize the effect of, for instance, the use of threads.

#### Chapter 7: Understanding Common Anti-Patterns

The previous chapters described the "green paths" and how to improve your code. But there are also "red paths" which will reduce the code quality or program performance. This is where we reach the patterns with funny names, even though their impact is anything but funny. Spaghetti code, cut and past programming, lava flow, input kludge, working in a minefield, poltergeists, etc., are further described here.

The unwanted autoboxing anti-pattern is one I need to remember when reviewing some of my old but still-used code...

Conclusion {#h2-8-conclusion}
-----------------------------

We're impressed with the knowledge Miroslav exposes in his book.

For each principle, at least one implementation inside OpenJDK is referenced to illustrate they are part of the Java code base itself, revealing his engagement in the OpenJDK project and his experience with Java.

The long list of principles can be frightening as it illustrates what you should understand and know to produce good code and what you can do wrong or did wrong in the past.

But personally, it was also a confirmation. You grow as a developer in your career when you focus on clean code.

Some of these principles became a daily habit as we wanted to become better coders, and we started implementing them without even knowing of their existence, as they made my code better to maintain.

### The Bad {#h3-9-the-bad}

Only a few minor notes here...

* The titles under the example code confused me initially, as they are formatted like titles, and it was a bit unclear how to interpret them.
* Screenshots of the JDK Mission Control (JMC) profiler are used in the book to visualize the effect of, for instance, multiple threads or the autoboxing anti-pattern. But there is no explanation of the tool given.
* The principles and code examples are not intended for beginner developers. The whole book requires substantial knowledge of Java upfront.

### The Good {#h3-10-the-good}

* The last "bad" point also makes the first "good" point. The principles and patterns explained in the book will uplift your knowledge and bring your code quality to the next level, without explaining too much you already know.
* When you buy the paper version of the book, you can get a DRM-free PDF via an easy web form with a fast response.
* You can also download a PDF with a color version of all the images. Most are flowcharts, but for instance, for the screenshots of the thread visualization with JMC, they are handy.
* Each chapter ends with questions to validate you picked up the most important information. Also, a list with links for "Further reading" is included.
* All example code is based on a "vehicle" approach, implemented or extended into different use cases as needed to illustrate the topic. No foo/bar examples in this book!
* All example code is available on GitHub if you want to run them or dive into the code to better understand the principles.
