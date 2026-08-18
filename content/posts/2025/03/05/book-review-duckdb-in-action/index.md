---
title: "Book Review: DuckDB in Action"
slug: "book-review-duckdb-in-action"
date: "2025-03-05T13:42:59+00:00"
lastmod: "2025-03-05T13:43:48+00:00"
description: "This review is about DuckDB in Action by JoMark Needham, Michael Hunger, and Michael Simons from Manning."
authors:
  - "nicolas-frankel"
image: "DuckDB_in_Action_large.jpg"
categories:
  - "Book Review"
  - "Databases"
tags:
related_posts:
  - "book-review-api-design-patterns"
  - "book-review-designing-apis-with-swagger-and-openapi"
  - "book-review-effortless-cloud-native-app-development-using-skaffold"
frozen: false
---

Disclaimer: this post includes affiliate links; I may receive compensation if you purchase the book from the different links provided in this post.

This review is about [DuckDB in Action](https://www.manning.com/books/duckdb-in-action?utm_source=frankel&utm_medium=affiliate&utm_campaign=affiliate&a_aid=frankel) by JoMark Needham, Michael Hunger, and Michael Simons from Manning.

The book was sent to me by [Michael Simons](https://mastodon.social/@rotnroll666). He asked for my feedback: I changed my reading schedule, took a few months, and here it is.

Facts
-----

* 10 chapters
* 288 pages
* $33.59 (eBook)

Note that MotherDuck, the company behind DuckDB, offers a [free PDF copy](https://motherduck.com/duckdb-book-brief/).

Chapters
--------

1. An introduction to DuckDB
2. Getting started with DuckDB
3. Executing SQL queries
4. Advanced aggregation and analysis of data
5. Exploring data without persistence
6. Integrating with the Python ecosystem
7. DuckDB in the cloud with MotherDuck
8. Building data pipelines with DuckDB
9. Building and deploying data apps
10. Performance considerations for large datasets
11. Conclusion

The book takes a step-by-step approach, starting from the basics of DuckDB **and** SQL.

Pros and cons
-------------

After the mandatory section about introducing DuckDB and installing it, the book explains SQL. At first, I admit I thought that the book was for newbies, as it explained things as simple as `JOIN`. However, the chapter afterwards explained Common Table Expressions and built complex queries upon them.

I like how the authors describe DuckDB in its "natural environment", *i.e.*, how it integrates into different ecosystems, such as Python's Pandas and Apache Spark. In addition, a complete appendix section focuses on DuckDB in Java. Finally, the authors dedicate a section to MotherDuck, a DuckDB cloud provider.

Conclusion
----------

My thoughts on the book are a bit divided. On one side, it tries to explain DuckDB itself and its integration in different environments. On the other hand, it teaches regular and advanced SQL, which users of an SQL database product should know IMHO. The book can't decide between a Learn SQL with DuckDB approach and a DuckDB in Action one. The role of a book editor is to help authors make hard decisions. It feels as if this one didn't want to stop the authors' enthusiasm and couldn't bring themselves to rein them in.

Don't get me wrong: the book is great and can teach you a lot, but it could have been better with more focus.



*Originally published on [A Java Geek](https://blog.frankel.ch/duckdb-in-action/) on November 2^nd^, 2024*
