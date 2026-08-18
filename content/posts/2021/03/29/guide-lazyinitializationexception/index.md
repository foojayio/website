---
title: "A (Definitive?) Guide to LazyInitializationException"
slug: "guide-lazyinitializationexception"
date: "2021-03-29T07:19:49+00:00"
lastmod: "2021-03-29T07:19:51+00:00"
description: "Posts that have been written about Hibernate's LazyInitializationException could probably fill whole books. Time for a definitive guide!"
canonical: "https://blog.frankel.ch/guide-lazyinitializationexception/"
authors:
  - "nicolas-frankel"
image: "Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "jurassic-jdk-migrate-or-extinct"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
  - "your-tls-stack-is-lying-about-zero-copy"
frozen: false
---

Posts that have been written about Hibernate's `LazyInitializationException` could probably fill whole books.

Yet, I believe each of them focuses on a particular aspect of it: some on a specific solution, some on how to solve it with Spring Boot, etc.

I'd like this post to be the definitive guide on the subject, even though I'm pretty sure it won't. At least, I'll be able to point others to it!

### Root Cause

Whether you love or hate frameworks in general, they are nonetheless pretty common in the Java ecosystem. is the ORM standard and part of the Jakarta EE specifications. [Hibernate](https://hibernate.org/) is its most widespread implementation: for example, it's the default in Spring Boot.

Let's describe the cause of the exception first.

JPA specifications are 568 pages long so allow me to take some shortcuts. Please refer to the [source](https://download.oracle.com/otn-pub/jcp/persistence-2_2-mrel-spec/JavaPersistence.pdf) if necessary.

JPA defines **entities** and **relationships**:

* An entity is pretty straightforward. It represents a database table. For example, a `Customer` entity maps to a `CUSTOMER` table.
* A relationship connects one entity to another. It may be one-to-one, one-to-many, many-to-one, and many-to-many. For example, a `Customer` entity has a one-to-one relationship with a `Cart` entity and a one-to-many relationship with an `Order` entity.

A relationship may be **eager** or **lazy**.

Hibernate fetches data in eager relationships in one single query. By default, one-to-one relationships are eager. In the example above, that means that loading a `Customer` also loads its `Cart`. This `em.find(Customer.class, 1L)` generates the following SQL:

```sql
SELECT customer0_.id as id1_2_0_, customer0_.cart_id AS cart_id2_2_0_, cart1_.id as id1_1_1_
FROM Customer customer0_
    LEFT OUTER JOIN Cart cart1_ ON customer0_.cart_id = cart1_.id
WHERE customer0_.id = 1L
```

On the other hand, one-to-many relationships are lazy by default. For such relationships, Hibernate doesn't fetch the data at query-time. Instead, it initializes the attribute that references the lazy relationship with a **proxy** . This proxy holds a reference to the Hibernate `Session` that loaded the root entity. Hibernate executes a new query when you access the attribute using the referenced `Session` (a JPA `EntityManager` wraps a `Session`).

It has two significant consequences:

1. It negatively impacts performances as you need an additional roundtrip to the database.
2. If the `Session` is closed - or the object has been detached, Hibernate cannot connect to the database and throws the dreaded `LazyInitializationException`!

Let's see how it works:

```java
var manager = factory.getEntityManager();                         // 1
var transaction = manager.getTransaction();
transaction.begin();                                              // 2
var newCustomer = new Customer();
newCustomer.addOrder(new Order());
manager.persist(newCustomer);                                     // 3
transaction.commit();                                             // 4
var id = newCustomer.getId();
var anotherManager = factory.getEntityManager();                  // 5
var customer = anotherManager.find(Customer.class, id);           // 6
anotherManager.detach(customer);                                  // 7
var orders = customer.getOrders();                                // 8
assertThrows(LazyInitializationException.class, orders::isEmpty); // 9
```

1. Get a JPA `EntityManager` assuming that `factory` is an `EntityManagerFactory`.
2. Begin the transaction.
3. Persist the newly-instantiated entity in the database.
4. Commit the transaction.
5. Get *another* `EntityManager`. It's essential because Hibernate caches the entity in the `Session` object (cf. \[Hibernate Hydrate Wiki\]'\](<https://github.com/arey/hibernate-hydrate>)).
6. Load the previously saved `Customer` under a new reference.
7. Remove the `Customer` from the `EntityManager`. If we don't, Hibernate will execute a new query using the `EntityManager`.
8. Get a reference on the `orders` attribute.
9. Any method call on `orders` throws the exception because it's not a "real" collection but a proxy.

The above sample looks pretty convoluted to reproduce the problem. However, it's straightforward in web apps as each new request should spawn a new `EntityManager`.

### Possible Solutions

Now that we have framed the problem and what causes it, we can look at possible solutions.

#### Eager Associations

I mentioned above that one-to-many associations are lazy by default, and one-to-one are eager. JPA allows you to change this default with annotations.

```java
@Entity
public class Customer {

    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(fetch = FetchType.EAGER)  // 1
    private Set<Order> orders;
}
```

1. Query the set of `orders` eagerly when loading a `Customer` instance

The following is the new generated query:

```sql
SELECT customer0_.id        AS id1_2_0_,
       customer0_.cart_id   AS cart_id2_2_0_,
       cart1_.id            AS id1_1_1_,
       orders2_.CUSTOMER_ID AS customer2_0_2_,
       orders2_.id          AS id1_0_2_,
       orders2_.id          AS id1_0_3_
FROM Customer customer0_
         LEFT OUTER JOIN Cart cart1_      ON customer0_.cart_id = cart1_.id
         LEFT OUTER JOIN "ORDER" orders2_ ON customer0_.id = orders2_.CUSTOMER_ID
WHERE customer0_.id = ?
```

Setting your associations to be eager is a terrible idea! Here are three good reasons why one-to-many associations are lazy by default - and should stay that way:

1. The eagerly loaded entity may itself eagerly load other entities. It's turtle all the way down.
2. Once you configured the association to be eager, there's no way to un-configure it. Data will be loaded whether you need them further down the line or not.
3. Collections have no limits on their size.

Putting those together, we end up with an object graph with:

* A nesting level dictated by the model
* And an unbounded number of objects at each level shaped by the data in the database

Worse, since the development database probably has a handful of data lines, you'll likely experience performance issues in staging or even, worse, in production.

#### Open-Session-In-View

I mentioned above that JPA is often used in web applications. In some contexts, developers use entities directly in the views *e.g.* . In traditional layered architectures, the `EntityManager` is opened - and closed - in the Service layer. Using a lazily-loaded attribute in a view throws a `LazyInitializationException`.

The idea behind the design is to create a new `EntityManager` and use it when it happens. It's implemented via a `Filter`. The good thing about OSIV is that it works.

The bad thing is that each lazily-loaded attribute in the graph executes a new SQL query and requires a new roundtrip to the database. It's known as the [N+1 problem](https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping): one query to fetch the root entity and N queries to fetch each child entity. Remember the object graph from the section above? The number of roundtrips depends on the nesting level.

Moreover, note that querying happens when the server renders the page. If a problem happens, there's no way to handle the exception properly.

Be aware that OSIV is **very** pervasive in the Java ecosystem. It's described in the first edition of Java Persistence with Hibernate. You can find it on the Web in multiple places. Even Spring Boot registers an OSIV filter by default though it logs a warning message about it. To avoid nasty surprises in production, I'd advise you to set `spring.jpa.open-in-view=false` as the first step in every Spring Boot project that uses both Hibernate and Web MVC.

#### Data Transfer Objects

were pretty popular in layered architectures 10 years ago. The idea behind DTO is to create a dedicated type (instead of an entity type) to send to the view. To create a DTO instance, you need to read relevant entity attributes and write their values to the DTO.

However, with most applications, the DTO type maps closely - if not one-to-one - to the entity type. Copy-pasting each attribute manually is a bore and error-prone. To improve upon this, a whole new generation of bean-to-bean mapper frameworks bloomed.

DTOs' benefit is that since mapping happens in the service layer, you can handle the error appropriately. However, it doesn't solve the N+1 problem.

A cheap trick is to avoid DTO, keep the entity and explicitly call its getters before sending it to the view. It has the same pro and con as DTOs, respectively error-handling in the right place and N+1 queries problem.

#### Hibernate Hydrate

I came upon Hibernate Hydrate while researching this post.
> As mention in the Hibernate javadoc, a LazyInitializationException indicates an access to unfetched data outside of a Hibernate session context. For example, when an uninitialized proxy or a collection is accessed after the session was closed, or after the object has been detached from the session.
>
> To solve this problem, you may prefetch all properties you need before the session is closed or use the OpenSessionInView pattern. In the first case, you can manually call the appropriate field getter or use the fetch join keyword in JPQL or HQL queries.
>
> The Hibernate hydrate project helps developer to automatically resolve uninitialized proxies. A single helper class, or 2 if you are using full JPA with Hibernate as provider.
>
> -- [Hibernate Hydrate Wiki](https://github.com/arey/hibernate-hydrate)

The project provides the several `deepHydrate()` methods that require two parameters:

* The Hibernate `Session` or `SessionFactory`. If you are using the JPA interface `EntityManager`, it's easy to get the underlying `Session` easy by calling `manager.unwrap(Session.class)`
* The entity or the collection of entities to "hydrate".

The method fetches eagerly and indiscriminately all attributes that are proxies in the object graph.

### Fetch Join

All the above solutions have performance-related issues:

* *EAGER* loads the whole object graph into the memory, whether it's necessary or not.
* The other alternatives do have the N+1 queries problem. Hibernate Hydrate even displays both issues.

It seems like a lose-lose situation. By loading eagerly, we stuff the memory because we load everything; by loading lazily, we overload the network with additional requests when lazy attributes are requested.

But I believe it's the wrong way to look at it. We do know what attributes we need on the view so that we can load them eagerly. The issue is that these attributes probably are different for each view: we need a way to configure the eagerness/laziness characteristic for each association on-demand. That's the goal of **fetch join**.

Fetch joins operate at the *query level*. You have to configure them every time you need them.

They are available:

* In :

```java
var query = em.createQuery("SELECT c FROM Customer c JOIN FETCH c.orders o WHERE c.id = :id");
query.setParameter("id", id);
var customer = (Customer) query.getSingleResult();
```

* In the Criteria API: 

```java
var builder = em.getCriteriaBuilder();
var criteria = builder.createQuery(Customer.class);
var root = criteria.from(Customer.class);
root.fetch("orders", JoinType.LEFT);<code class="language-java"></code>
```

By running one of the above snippets, Hibernate fetches the `orders` attribute of the `Customer` instance in the same query that loads the `Customer` itself. Here's the corresponding SQL query:

```sql
SELECT customer0_.id        AS id1_2_0_,
       orders1_.id          AS id1_0_1_,
       customer0_.cart_id   AS cart_id2_2_0_,
       orders1_.CUSTOMER_ID AS customer2_0_0__,
       orders1_.id          AS id1_0_0__
FROM Customer customer0_
         LEFT OUTER JOIN "ORDER" orders1_ ON customer0_.id = orders1_.CUSTOMER_ID
WHERE customer0_.id = 1
```

### Entity Graph

Fetch joins get the job done: they eagerly load lazy attributes for a query.

Still, they suffer from two downsides:

* Using fetch joins is error-prone, especially in JPQL.
* In most use-cases, the views generally require the same set of attributes. Yet, fetch joins require you to set them in each necessary query.

For that reason, JPA 2.1 brings the concept of **entity graph** . An entity graph is similar to a **reusable declarative fetch join** on a set of attributes. As its name implies, you can apply an entity graph to a whole subgraph - it's not limited to flat attributes.

It's possible to define an entity graph both declaratively via annotations and programmatically:

```java
@Entity
@NamedEntityGraph(
    name = "orders",
    attributeNodes = { @NamedAttributeNode("orders") }
)
public class Customer {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CUSTOMER_ID")
    private Set<Order> orders;
}
```

You can use an entity graph via both JPQL and the Criteria API. More importantly, you can use it in plain `find()` methods.

```java
var entityGraph = em.getEntityGraph("orders");
var props = Map.<String, Object>of("javax.persistence.loadgraph", entityGraph);
var customer = anotherManager.find(Customer.class, id, props);
```

### Conclusion

In this post, we explained the reason behind Hibernate's `LazyInitializationException`. We browsed through a couple of hacky solutions. They all have performance issues, either memory-wise or network-wise.

With JPA 2.1+, we have the option to (and I believe we should!) use entity graphs. Before that, we should fall back to fetch joins. Barring that, you'll at least be aware of what problems you'll encounter.

**To go further:**

* [The OpenSessionInView antipattern](https://blog.frankel.ch/the-opensessioninview-antipattern/)
* [The Open Session In View Anti-Pattern](https://vladmihalcea.com/the-open-session-in-view-anti-pattern/)
* [What's the Difference between JOIN, LEFT JOIN and JOIN FETCH](https://thorben-janssen.com/hibernate-tips-difference-join-left-join-fetch-join/)
* [Entity Graphs section in Jakarta EE specifications](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a2397)
* [JPA Entity Graph](https://www.baeldung.com/jpa-entity-graph)

*Originally published at [A Java Geek](https://blog.frankel.ch/guide-lazyinitializationexception) on March 28^th^ 2021*

*[JPQL]: Java Persistence Query Language
*[OSIV]: Open-Session-In-View
*[JSP]: Java Server Pages
*[JPA]: Java Persistence API
*[ORM]: Object-Relation Mapping
*[DTO]: Data Transfer Object
