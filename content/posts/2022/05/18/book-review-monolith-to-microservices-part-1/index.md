---
title: "Book Review: Monolith to Microservices (Part 1)"
date: "2022-05-18T07:43:41+00:00"
lastmod: "2023-03-16T10:45:08+00:00"
description: "Do we need to do all modern development via the microservices architecture? Is it the norm, the new standard? This book argues differently."
authors:
  - "bazlur-rahman"
image: "monolith-to-microservice.jpeg"
categories:
  - "Book Review"
  - "Books"
  - "Microservices"
tags:
related_posts:
  - "chopping-monolith"
  - "building-microservices-spring-boot-fat-uber-jar"
  - "journeys-in-java-level-1-building-an-empire-of-microservices"
  - "book-review-monolith-to-microservices-part-2"
frozen: false
---

I have just finished reading ["Monolith to Microservices: Evolutionary Patterns to Transform Your Monolith](https://www.amazon.ca/Monolith-Microservices-Evolutionary-Patterns-Transform/dp/1492047848)" by Sam Newman.

{{< img src="monolith-to-microservice-388x510.jpeg" class="size-medium" width="388" height="510" >}}

This book is undoubtedly great. It explains the critical aspects applicable when moving away from a monolith to a microservices architecture.

In this article, I will try to summarize some of the key elements from the book that impacted me.

This book contains five chapters, each of which has a great deal of context and explanation.

So I will go through each chapter, one by one.

## **Chapter 1:**Just Enough Microservices

This chapter starts with defining the concept, "Microservice" itself, the benefits and the problems it creates.

### Microservices Defined

So from the definition, Microservices have the following key components:

1. **A service that can be deployed independently.** The idea is that a service has to have the ability to deploy in production without disrupting any other associated services. Therefore, any changes in a particular service shouldn't affect any other service, which is essential for having Microservices.
2. **A service has to be modelled around a business domain.** The idea is that if you need to touch two or more services to deliver a feature, then that's not an ideal scenario for Microservices. So we have to find a way so that his cross-service changes are less frequent.
3. **Each service has to own its data.** Again, we can compare this with the object-oriented design principle; the way a class encapsulates its fields, a service must encapsulate the data.

### Benefits of Microservices

Now the question is, what are the benefits it brings to the table? Well many. It puts many options on the table, you can mix and match different technology. It offers us to choose to mix different programming languages, programming styles, deployment platforms or databases to find the right mix.

Multiple services can be developed independently without getting each other ways. The developers can focus on their particular thing. Above all, it gives us flexibility. It opens up many more options regarding how we can solve a specific problem in future.

Finally, it reduces the blast radius. If a particular service goes out of service, ideally, it shouldn't impact any other services.

### Not a Silver Bullet

However, the Microservice architecture is not a silver bullet. It also brings a lot of baggage. Communication between services becomes a bottleneck. We now have to worry about latencies, and they vary, and thus the system becomes unpredictable. The transactional behaviour that we cherish so much will go away out the window.

Network calls can become a headache, for example, Service A is talking to Service B, but Service B might go offline. Since data are not in one place, getting a consistent view of data across multiple machines becomes difficult. In fact, microservice can be a terrible idea, except for all the good stuff it provides.

### Conclusion

Putting all these pros and cons upfront, the book argues whether we really need to go forward with Microservices or not. Perhaps not, in many cases. There are still many benefits of having a monolith.

For example, it has a much simpler deployment topology and much simpler developer workflow. Activities like monitoring, troubleshooting, and end-to-end testing are relatively easy in the monolith. Besides, if you want to reuse code, it's just there, and a method calls away.

Still, suppose you want to go for it. In that case, you must have a perfect reason for that: independent deployability, independent scalability, mix and max technology, reducing the blast radius, etc.

Of course, it won't be cheap, somewhat costly, but with Microservices, you can buy more options. Well, you certainly need to know that there is a cost associated with the term "buying".

That's all for today. I will discuss the next chapter in the next article in this series.
