---
title: "Book review: \"Get Your Hands Dirty on Clean Architecture - Second Edition\""
date: "2023-10-14T11:51:34+00:00"
lastmod: "2023-10-14T11:51:35+00:00"
description: "A magnificent job of walking through a lot the different architectural considerations one has to make to develop maintainable code."
authors:
  - "simon-verhoeven"
image: "GetYourHandsDirtyOnCleanArchitecture-1.jpg"
categories:
  - "Book Review"
  - "Books"
tags:
related_posts:
  - "book-review-api-design-patterns"
  - "book-review-designing-apis-with-swagger-and-openapi"
  - "book-review-effortless-cloud-native-app-development-using-skaffold"
  - "book-review-tidy-first"
frozen: false
---

**Clean Architecture helps us ensure a solid foundation for our applications** ,**and helps keep our code organized and maintainable. Furthermore when properly applied it helps keep ones team on the same page and ensures our applications stay robust and flexible over time. Of course there are a lot of pitfalls to take into account, and a lot of pros/cons to each approach. There certainly is no one size fits all approach. But luckily this book by [Tom Hombergs](https://www.linkedin.com/in/thombergs/) provides a lot of valuable practical insights.**

{{< img src="image-830x1024.png" class="aligncenter size-large is-resized" width="301" height="371" style="width:301px;height:371px" >}}

## About the book

**price** : €26.99 for the eBook  
**publication date** : July 2023  
**publisher** : Packt  
**pages** : 168  
**ISBN**: 9781805128373

## Content

1. Maintainability =\> what does it mean, and how do we ensure it
2. What's wrong with layers? =\> What are the implications \& consequences
3. Inverting dependencies =\> possible architectures, and how do they help us
4. Organizing code =\> here we dive into different approaches (feature/layer/...) and their up- \& downsides
5. Implementing a use case =\> a practical example using different approaches
6. Implementing a web adapter =\> a clean way to do this, using proper slicing
7. Implementing a Persistence adapter =\> what are the responsibilities, do we use ports, how do we slice one, what about transactions
8. Testing architecture elements =\> how do we ensure and apply proper unit/integration testing, and is what we're doing actually enough?
9. Mapping between boundaries =\> there are many boundary mapping approaches, and each has it's up- \& downsides, Tom takes us through a thorough practical explanation of each
10. Assembling the application =\> how we assemble it, and what considerations are there
11. Taking shortcuts consciously =\> sometimes we consciously take shortcuts to make our lives easier, how do we identify these, what are potential consequences, and in which case are they warranted?
12. Enforcing architecture boundaries =\> the importance of proper visibility modifications, build artifact management, ...
13. Managing bounded contexts =\> How many hexagons should we use per bounded context, how do we (de)couple bounded contexts, and how do they apply to clean architecture?
14. A component-based approach to software architecture =\> a very nice, practical example on how to properly modularize our application through components, and how to enforce the boundaries
15. Deciding on an Architecture style =\> what are the considerations, and how do you evolve it further?

## My thoughts

Tom Hombergs does a magnificent job of walking through a lot the different architectural considerations one has to make to develop maintainable code (including pros/cons), using practical examples and demonstrations (which are also available in a GitHub repository).

He has managed to write it in a clear, concise and very understandable manner.

This book is a magnificent, practical companion, for the more equivalent theoretical ones such as Robert C. Martin's Clean architecture, and Alistair Cockburn's Hexagonal architecture.
