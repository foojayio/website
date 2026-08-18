---
title: "Alternatives to DTO"
date: "2022-03-07T11:03:23+00:00"
lastmod: "2022-03-07T11:12:08+00:00"
description: "I used to believe (and still do) that DTOs should be a thing of the past. Yet, it seems their usage is still widespread."
authors:
  - "nicolas-frankel"
image: "opportunity-2105406_1280.jpg"
categories:
  - "Research"
tags:
related_posts:
  - "create-a-crud-ui-in-pure-java"
  - "get-your-jdk-as-easily-as-possible"
  - "blockhound-how-it-works"
  - "the-right-feature-at-the-right-place"
frozen: false
---

More than a decade ago, I [wrote](https://blog.frankel.ch/dto-in-anger) about the :
> "A data transfer object is an object that carries data between processes. The motivation for its use is that communication between processes is usually done resorting to remote interfaces, where each call is an expensive operation. Because the majority of the cost of each call is related to the round-trip time between the client and the server, one way of reducing the number of calls is to use an object (the DTO) that aggregates the data that would have been transferred by the several calls, but that is served by one call only." -- [Wikipedia](https://en.wikipedia.org/wiki/Data_transfer_object)

I believed (and still do) that it should be a thing of the past. Yet, it seems its usage is still widespread.

I do not deny there are some valid reasons to transform data. However, there are alternatives to the traditional DTO process:

1. Return a business object from the service layer. In projects I've worked on previously, we directly mapped the BO to the entity read from the database.
2. Transform the BO to a DTO in the presentation layer.
3. Return the DTO from the presentation layer.

## Return the entity itself

When the entity's properties are a superset of the properties that need to be displayed, aggregating additional properties is not required. Transforming the entity to a DTO is not only overkill. It hinders performance.

In that case, the best approach is to return the entity itself.

## JPA projection

We make requests for specific data in a particular context. Thus, when the call reaches the data access layer, the scope of the required data is fully known: it makes sense to execute a SQL query that is tailor-fitted to this scope.

For that, JPA offers projections. In essence, a projection in a query allows selecting precisely the data one wants. Here's an example; given a `Person` entity class and a `PersonDetails` regular class:

```java
CriteriaQuery<PersonDetails> q = cb.createQuery(PersonDetails.class);
Root<Person> c = q.from(Person.class);
q.select(cb.construct(PersonDetails.class,
  c.get(Person_.firstName),
  c.get(Person_.lastName),
  c.get(Person_.birthdate)
));
```

## Jackson converter

Regarding JSON specifically, we can delegate the process of providing the correct data to the serializer framework, *e.g.* [Jackson](https://github.com/FasterXML/jackson). The idea behind it is the following: the main code processes the entity as usual, and at the edge, a Jackson converter converts it to the required JSON structure.

If less data is necessary, it's child's play. If more, then the converter needs additional dependencies to get data where it is. Of course, if this data comes from the same datastore, this is not great, and the alternative above is more relevant. If not, it's an option.

## GraphQL

Last but not least, one could return the full-blown entities and let the client decide what data makes sense in its context.

[GraphQL](http://graphql.github.io) is built around this idea: Facebook created it, and it is now fully Open Source. Its main advantage is to offer a specification and [a lot](http://graphql.github.io/code/#server-libraries) of language-specific implementations on top of it.
> "A query language for your API. GraphQL is a query language for APIs and a runtime for fulfilling those queries with your existing data. GraphQL provides a complete and understandable description of the data in your API, gives clients the power to ask for exactly what they need and nothing more, makes it easier to evolve APIs over time, and enables powerful developer tools." -- [GraphQL website](http://graphql.github.io)

## Conclusion

When a gap exists between the business and presentation models, it's easy to get back to age-old "patterns" such as the DTO. However, any of the alternatives above are probably more relevant.

**To go further:**

* [The best way to map a projection query to a DTO](https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa-and-hibernate/)
* [Entities or DTOs -- When should you use which projection?](https://www.thoughts-on-java.org/entities-dtos-use-projection/)
* [GraphQL](http://graphql.github.io/)

*Originally published at [A Java Geek](https://blog.frankel.ch/alternatives-dto/) on March 6^th^, 2022*

*[DTO]: Data Transfer Object
