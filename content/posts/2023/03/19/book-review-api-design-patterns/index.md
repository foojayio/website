---
title: "Book Review: \"API Design Patterns\""
date: "2023-03-19T15:20:56+00:00"
lastmod: "2023-03-19T15:21:25+00:00"
description: "This review is about API Design Patterns by JJ Geewax from Manning. Check it out and if you've read it too, what did you think?"
canonical: "https://blog.frankel.ch/api-design-patterns/"
authors:
  - "nicolas-frankel"
image: "API_Design_Patterns_large.jpg"
categories:
  - "Book Review"
  - "Books"
  - "Java Core"
tags:
related_posts:
  - "book-review-practical-design-patterns-for-java-developers"
  - "book-review-seriously-good-software"
  - "book-review-help-your-boss-help-you"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
frozen: false
---

*Disclaimer: this post includes affiliate links; I may receive compensation if you purchase the book from the different links provided in this post.*

This review is about [API Design Patterns](https://www.manning.com/books/api-design-patterns?utm_source=frankel&utm_medium=affiliate&utm_campaign=affiliate&a_aid=frankel) by JJ Geewax from Manning.

I [already mentioned](https://blog.frankel.ch/structured-errors-http-apis/) how I'm trying to get to speed in the API world: reading books, viewing relevant YouTube videos and reading relevant s.

## Facts

* 30 chapters, $35.00
* The author is a Principal Software Engineer at Google
* He's also the author behind

## Chapters

* Introduction
* Design principles
  3. Naming
  4. Resource scope and hierarchy
  5. Data types and defaults
* Fundamentals
  6. Resource identification: How to identify resources in an API
  7. Standard methods: The set of standard methods for use in resource-oriented APIs
  8. Partial updates and retrievals: How to interact with portions of resources
  9. Custom methods: Using custom (non-standard) methods in resource-oriented APIs
  10. Long-running operations: How to handle methods that are not instantaneous
  11. Rerunnable jobs: Running repeated custom functionality in an API
* Resource relationships
  12. Singleton sub-resources: Isolating portions of resource data
  13. Cross references: How to reference other resources in an API
  14. Association resources: How to manage many-to-many relationships with metadata
  15. Add and remove custom methods: How to manage many-to-many relationships *without* metadata
  16. Polymorphism: Designing resources with dynamically-typed attributes
* Collective operations
  17. Copy and move: Duplicating and relocating resources in an API
  18. Batch operations: Extending methods to apply to groups of resources atomically
  19. Criteria-based deletion: Deleting multiple resources based on a set of filter criteria
  20. Anonymous writes: Ingesting unaddressable data into an API
  21. Pagination: Consuming large amounts of data in bite-sized chunks
  22. Filtering: Limiting result sets according to a user-specified filter
  23. Importing and exporting: Moving data into or out of an API by interacting directly with a storage system
* Safety and security
  24. Versioning and compatibility: Defining compatibility and strategies for versioning APIs
  25. Soft deletion: Moving resources to the "API recycle bin"
  26. Request deduplication: Preventing duplicate work due to network interruptions in APIs
  27. Request validation: Allowing API methods to be called in "safe mode"
  28. Resource revisions: Tracking resource change history
  29. Request retrial: Algorithms for safely retrying API requests
  30. Request authentication: Verifying that requests are authentic and untampered with

Each *design pattern* chapter follows the same structure:

1. Motivation: what problem solves the pattern
2. Overview: a short description of the pattern
3. Implementation: an in-depth explanation of the pattern. It's structured into different subsections.
4. Trade-offs: patterns have strong and weak points; this section describes the latter
5. Exercises: a list of questions to verify that one has understood the pattern

## Pros and cons

Let's start with the good sides:

* As I mentioned above, the structure of each chapter dedicated to a design pattern is repetitive. It makes the chapter easy to consume, as you know exactly what to expect.
* In general, I read my technical books just before going to sleep because I'm pretty busy during the day. Most books have long chapters, requiring me to stop mid-chapter when I start to fall asleep. When you start again, you need to get back a few pages to get the context back. The length of a chapter of API Design Patterns is ideal: neither too long nor too short.
* The Design principles section starts from the basics. You don't need to be an expert on API to benefit from the book. I was not; I hope that I'm more seasoned by now.
* I was a bit dubious at first about the Exercises section of each chapter, for it didn't provide any solution. However, I came to realize it activates the [active recall](https://en.wikipedia.org/wiki/Testing_effect) mechanism: instead of passively reading, actively recall what you learned in answering questions. It improves the memorization of what was learned. As an additional benefit, you can learn in a group, compare your answers and eventually debate them.

Now, I've some critics as well:

* Some patterns are directly taken from [Google's API Improvement Proposals](https://google.aip.dev/). It's not a problem *per se* , but when it's the case, there's no discussion **at all** about possible alternatives. For example, the chapter on custom methods describes how to handle actions that don't map precisely to an HTTP verb: a bank transfer is such an action because it changes two resources, the "from" and the "to" accounts.The proposed [Google AIP](https://google.aip.dev/136) is for the HTTP URI to use a `:` character followed by the custom verb, *e.g.* , `/accounts/123:transfer`. That's an exciting proposal that solves the lack of mapping issue. But there are no proposed alternatives nor any motivation for why it should be this way. As an engineer, I can hardly accept implementing a solution with such far-reaching consequences without being provided with other alternatives with their pros and cons.
* Last but not least, the book doesn't mention any relevant RFC or IETF draft. Chapter 26 describes how to manage request deduplication, the fact that one may need to send the same non-idempotent request repeatedly without being afraid of ill side effects. The proposed solution is good: the client should use a unique key, and if the server gets the same key again, it should discard the request.It's precisely what the IETF draft describes: [The Idempotency-Key HTTP Header Field](https://datatracker.ietf.org/doc/html/draft-ietf-httpapi-idempotency-key-header-02). Still, there's no mention of this draft, giving the feeling that the book is disconnected from its ecosystem.

## Author's replies

For once, I was already in touch with the author. I offered him an opportunity to review the post. Since his answers are open, I decided to publish them with his permission:

* Why isn't there more discussion about alternatives?  
  > I think you're right -- and I actually had quite a bit of discussion of the alternatives in the original manuscript. And I ended up chopping them out. One reason was that my editor wanted to keep chapters reasonably sized, and my internal debates and explanations of why one option was better or worse than another was adding less value than "here's the way to do it". The other reason was that I "should be opinionated". If this were a textbook for a class on exploring API design I could weigh all the sides and put together a proper debate on the pros and cons of the different options, but in most cases there turned out to be a very good option that we've tried out and seen work really well over the course of 5+ years (*e.g.* , `:` for custom methods). In other cases we actively didn't have that and the chapter **is** a debate showing the different alternatives (*e.g.*, Versioning). If I could do a 600-700 pages, I think it would have this for you.
* Why aren't there more references to IETF standards?  
  > This is a glaring oversight on my part. There are a lot of RFCs and I do mention some (e.g., RFC-6902 in Partial Updates, etc), but I haven't pulled in enough from that standards body and it's a mistake. If we do a 2nd edition, this will be at the top of the list.

## Conclusion

Because of the couple of cons I mentioned, [API Design Patterns](https://www.manning.com/books/api-design-patterns?utm_source=frankel&utm_medium=affiliate&utm_campaign=affiliate&a_aid=frankel) falls short of being a reference book.

Nonetheless, it's a great book that I recommend to any developer entering the world of APIs or even one with more experience to round up their knowledge.

*Originally published at [A Java Geek](https://blog.frankel.ch/api-design-patterns/) on January 15^th^, 2023*

*[IETF]: Internet Engineering Task Force
*[RFC]: Request For Comments
