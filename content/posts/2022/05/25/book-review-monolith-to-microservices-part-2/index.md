---
title: "Book Review: Monolith to Microservices (Part 2)"
slug: "book-review-monolith-to-microservices-part-2"
date: "2022-05-25T09:09:08+00:00"
lastmod: "2023-03-16T10:45:19+00:00"
description: "In many cases, microservices are probably not ideal, but if we are to do it, we should take baby steps. Let's find out the what alternatives we have on the table."
authors:
  - "bazlur-rahman"
image: "monolith-to-microservice.jpeg"
categories:
  - "Book Review"
  - "Books"
  - "Microservices"
tags:
related_posts:
  - "building-microservices-spring-boot-fat-uber-jar"
  - "chopping-monolith"
  - "easy-jakarta-ee-integration-testing"
  - "book-review-monolith-to-microservices-part-1"
frozen: false
---

This is the second article [of the first one I wrote the other day](https://foojay.io/today/book-review-monolith-to-microservices-part-1/), "[Monolith to Microservices: Evolutionary Patterns to Transform Your Monolith](https://www.amazon.ca/Monolith-Microservices-Evolutionary-Patterns-Transform/dp/1492047848)" by Sam Newman.

In this article, I will summarize chapter two.

![](monolith-to-microservice.jpeg)

## **Chapter #2: Planning to Migration**

In this chapter, the book discusses the goal and mindset. When we are looking for a Microservices, what should be our goal.

It argues that our goal shouldn't be the Microservices. Something worked for Netflix or Amazon, which means it will work for me as well. This thought process has a major flaw. It makes you fall into a cargo cult mentality.

Every set of problems is different and can be further varied on the situation, and the resources we have at our disposal. Every decision has its cost. Nothing comes free. I cannot simply copy ideas from Amazon or Google and apply them to my cases.

Before adopting Microservices architecture, we should ask three questions:

1. What are we hoping to achieve? First, we must have to have a clear understanding of what we are trying to accomplish with this architecture. It shouldn't be the case that we are doing it because it's cool or everyone is doing so.
2. Have we considered an alternative to Microservices? Before delving into it, we should consider other approaches because we can often achieve what we want to accomplish without Microservices.
3. How will we know the transition is working? First, we have to figure out a way to validate that we are in the right direction. We will discuss this in chapter #5 later.

Now that the questions are out of the way let's discuss why we consider Microservices, which aligns with our goal.

### **Improve Team autonomy**

Having Microservices enables us to have to create autonomous teams. Keeping a team small, allowing them to build close bonding and work effectively together without bringing in too much bureaucracy, in turn, help the organization grow and scale more effectively. We can consider amazon's two-pizza team model.

Usually, team autonomy can empower people, help them step up and grow and get the job done.

***Alternative to Microservices***, giving ownership to parts of the codebase to different teams, we can achieve this sort of autonomy to the groups, e.g. modular monolith can be a perfect answer here.

### **Reduce time to market**

Being able to make and deploy changes independently, it requires less coordination between multiple teams; thus, it reduces the time to put things out for actual use.

***Alternatively***, with proper automation suites, highlighting the pain point can help reduce the time to market.

### **Scale Cost-Effectively for Load**

Since things are now broken into smaller pieces, it's now easy to scale independently. In a complete system, the idea is that not all parts would require handling huge loads, so we can focus on those parts that need it. That gives us more control over operational costs.

***Alternatively***, vertical scaling can help to a great extent. Horizontal scaling -basically running multiple copies- could prove to be highly effective,

### **Improve robustness**

The expectation grows when we move from a single talent software to a multitenant SaaS application. For example, the application must be available all the time. However, since in Microservice, the application is decomposed into multiple smaller pieces, an impact on a particular area doesn't require putting the whole system down.

Thus, it reduces the blast radius; if something goes wrong, the affecting area becomes granular, and we can focus our energy on that small piece and fix it quickly.

***Alternately***, we can put multiple copies of the monolith behind the load balancer, or a queue can improve robustness. Distributing instances of the monolith across multiple failure planes can also further help to handle failure gracefully.

### **Scale the number of developers**

We all know it doesn't work if we put many developers on a project. The joke "one developer does a job in a month; two developers do it two months" is well known. However, if we can partition things into separate independent work units with less interaction between them, then it works.

With clear boundaries and Microservices architecture, it is easy to scale developers' productivity with more developers in the organization.

***Alternatively***, a modular monolith can also help to achieve the same principle. Each team can have one module and work independently as long as the interface with the module remains stable.

### **Embrace new technology**

Although mature organizations limit how many technology stacks they support, it opens the door to having more than one stack running for multiple services. It even makes developers happy to some extent.

***Alternately***, we can safely adopt a new language if the runtime remains the same. For example, JVM can run code written in multiple languages within the same running process. E.g. Groovy, Scala, Kotlin, Java, Clojure, JRuby, etc.

Now that we have understood the goal of having Microservices, let's discuss when this isn't really a good fit.

### **Uncertain Domain**

Getting service boundaries wrong can be extremely expensive. It can lead to an overly coupled component, in general, could be worse than a monolith. So if we don't have a full grasp of the domain, we must first get the domain expertise before coming to microservices.

### **Startups**

Usually, a startup is likely to have limited funding and resources, where things start with experimenting with ideas; it's probably not a good idea, to begin with, microservices.

A company like Netflix and Airbnb didn't start their product with microservice in the first place; rather, they moved to it gradually.

When a startup experiments with its idea, if the idea doesn't work, it doesn't really matter if its software architecture is great.

### **Not having really good reasons**

Doing microservices just because everyone else is doing it is a terrible idea.

If we don't have a clear idea of what we want to achieve, we shouldn't go for microservices.

### Conclusion

Now that we have laid out the whole picture, it's your call to decide whether you want Microservice or not. In many cases, microservices are probably not ideal, but if you're going to do it with all the good reasons we mentioned, take the baby steps.

First, create a strategy to onboard the team to develop a vision and plan towards it. Small and short-term wins matter; it boasts the team's confidence.

Put things in production. Always put checks and balance whether it is working or not. If not, then go back to the alternative ways.

Don't go for a big bang rewrite, as Martin Fowler stated, if you do a bing-bang rewrite, the only thing you're guaranteed of is a big bang.
>
> #### *If you do a bing-bang rewrite, the only thing you're guaranteed of is a big bang.*
>
> #### *--- Martin Fowler*
>
That's for today, I will write about the next chapter in the following article.
