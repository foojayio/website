---
title: "Reactive Database Access on the JVM"
slug: "reactive-database-access-on-the-jvm"
date: "2023-07-17T09:18:37+00:00"
lastmod: "2023-07-17T09:18:38+00:00"
description: "Let's browse through the surface of the main three reactive database access: Spring Data R2DBC, Hibernate, and jOOQ!"
canonical: "https://blog.frankel.ch/reactive-database-access/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/07/superhero-534120.jpg"
categories:
  - "Databases"
  - "Kotlin"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "backpressure-in-reactive-systems"
  - "blockhound-how-it-works"
enlighterjs: true
frozen: false
---

A couple of years ago, [Reactive Programming](https://en.wikipedia.org/wiki/Reactive_programming) was all the rage, but it had one big issue: reactive stopped as soon as you accessed a SQL database.

You had a nice reactive chain up to the database, defeating the whole purpose. Given the prevalence of SQL databases in existing and new apps, one couldn't enjoy the full benefits of Reactive Programming but still pay the full price of complexity.

Since then, the landscape has changed tremendously. Most importantly, it offers many reactive drivers over popular databases: PostgreSQL, MariaDB and MySQL, Microsoft SQL Server, Oracle, you name it!

Even better, some frameworks provide a reactive API over them.

Even though I'm not providing consulting services regularly, I wanted to keep up-to-date on accessing data reactively. In this post, I'll describe Hibernate Reactive, Spring Data R2DBC, and jOOQ in no particular order.

The base application uses Project Reactor and its types - `Flux` and `Mono`. For an added twist, I use Kotlin (without coroutines). Most code snippets have unnecessary type hints for better understanding.

The demo model {#h2-0-the-demo-model}
-------------------------------------

I don't want a complicated demo model, but I don't want it to be too simple. I'll use a single many-to-many relationship and a field with `LocalDate`:

![](/images/posts/2023/07/reactive-database-access-on-the-jvm/demo-model-1024x204.png)

Spring Data R2DBC {#h2-1-spring-data-r2dbc}
-------------------------------------------

As far as I remember, the Spring ecosystem was the first to offer a reactive database access API. At first, it was limited to H2 - not very useful in production. However, new reactive drivers were easy to integrate.

Spring Data RDBC builds upon the widespread Spring Data JPA. The biggest difference is that there's a single required annotation for entities, `@Id`.

Here's the code for the `person` table:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">data class Person(
    @Id val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate?,
    @Transient
    val addresses: MutableSet&lt;Address&gt; = mutableSetOf()
)

interface PersonRepository : ReactiveCrudRepository&lt;Person, Long&gt;</pre>

R2DBC repositories look similar to regular Spring Data repositories with one big difference. They integrate Project Reactor's reactive types, `Mono` and `Flux`. Note that it's easy to use Kotlin's coroutines with an additional bridge dependency.

![](/images/posts/2023/07/reactive-database-access-on-the-jvm/spring-data-r2dbc-repos.png)

Now comes the hard problem: mapping the many-to-many relationship with the `Address`.

First, we must tell Spring Data R2DBC to use a specific constructor with an empty set of addresses.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">data class Person(
    @Id val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate?,
    @Transient
    val addresses: MutableSet&lt;Address&gt; = mutableSetOf()
) {
    @PersistenceCreator
    constructor(
        id: Long,
        firstName: String,
        lastName: String,
        birthdate: LocalDate? = null
    ) : this(id, firstName, lastName, birthdate, mutableSetOf())
}</pre>

We also need to define the `Address` repository, as well as a query to list all addresses of a person:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">interface AddressRepository : ReactiveCrudRepository&lt;Address, Long&gt; {

    @Query("SELECT * FROM ADDRESS WHERE ID IN (SELECT ADDRESS_ID FROM PERSON_ADDRESS WHERE PERSON_ID = :id)")
    fun findAddressForPersonById(id: Long): Flux&lt;Address&gt;
}</pre>

Now comes the least tasteful part: Spring Data R2DBC doesn't support many-to-many relationships at the moment. We need a hook that queries the addresses after loading a person.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class PersonLoadOfficeListener(@Lazy private val repo: AddressRepository)   //1
  : AfterConvertCallback&lt;Person&gt; {

  override fun onAfterConvert(person: Person, table: SqlIdentifier) =
    repo.findAddressForPersonById(person.id)                                //2
      .mapNotNull {
          person.addresses.add(it)                                          //3
          person
      }.takeLast(1)                                                         //4
      .single(person)                                                       //5
}</pre>

1. Annotate with `@Lazy` to avoid running into circular dependencies exception during injection
2. Use the above query
3. Add each address
4. Reactive trick to wait for the last bit of data
5. Turn into a single `Person`

As far as I can understand, Spring Data R2DBC still needs to execute additional queries, thus leading to the (in)famous [N+1 query problem](https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping).

One configures database access via all available Spring alternatives: properties, YAML, Spring profiles, environment variables, etc. Here's a YAML example:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">spring.r2dbc:
  url: r2dbc:postgresql://localhost:5432/postgres?currentSchema=people
  username: postgres
  password: root</pre>

Hibernate Reactive {#h2-2-hibernate-reactive}
---------------------------------------------

If you're familiar with regular Hibernate, you'll feel right at home with Hibernate Reactive. The mapping is the same in both cases:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Entity
@Table(name = "person", schema = "people")                           //1
class Person(
    @Id var id: Long?,
    @Column(name = "first_name")                                     //2
    var firstName: String?,
    @Column(name = "last_name")                                      //2
    var lastName: String?,
    var birthdate: LocalDate?,
    @ManyToMany
    @JoinTable(                                                      //3
        name = "person_address",
        schema = "people",
        joinColumns = [ JoinColumn(name = "person_id") ],
        inverseJoinColumns = [ JoinColumn(name = "address_id") ]
    )
    val addresses: MutableSet&lt;Address&gt; = mutableSetOf()
) {
    internal constructor() : this(null, null, null, null)            //4
}</pre>

1. Define the table and the schema if necessary
2. Define column names, if necessary
3. Define the join column
4. JPA requires a no-argument constructor

We also need to configure the database. Hibernate Reactive uses the traditional XML-based JPA approach:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;persistence xmlns="https://jakarta.ee/xml/ns/persistence"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
           version="3.0"&gt;
  &lt;persistence-unit name="postgresql"&gt;
    &lt;provider&gt;org.hibernate.reactive.provider.ReactivePersistenceProvider&lt;/provider&gt;   &lt;!--1--&gt;
    &lt;properties&gt;
      &lt;property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/postgres?currentSchema=people" /&gt;
      &lt;property name="jakarta.persistence.jdbc.user" value="postgres" /&gt;
      &lt;property name="jakarta.persistence.jdbc.password" value="root" /&gt;
      &lt;property name="jakarta.persistence.schema-generation.database.action" value="validate" /&gt;
    &lt;/properties&gt;
  &lt;/persistence-unit&gt;
&lt;/persistence&gt;</pre>

1. The only difference so far from the regular Hibernate configuration

Here's the source for the query itself:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val emf = Persistence.createEntityManagerFactory("postgresql")                            //1
val sessionFactory: Mutiny.SessionFactory = emf.unwrap(Mutiny.SessionFactory::class.java) //2
val people: Mono&lt;MutableList&lt;Person&gt;&gt; = sessionFactory
        .withSession {
            it.createQuery&lt;Person&gt;("SELECT p FROM Person p LEFT JOIN FETCH p.addresses a").resultList
        }.convert().with(UniReactorConverters.toMono())                                   //3</pre>

1. Regular `EntityManagerFactory`
2. Unwrap the underlying session factory implementation. Because we configured a `ReactivePersistenceProvider` in the `persistence.xml`, it's a `Mutiny.SessionFactory`
3. Hibernate Reactive integrates with [Vert.x](https://vertx.io/), but an extension allows to bridge to Project Reactor if wanted

Note that Hibernate Reactive is the only library among the three to return a `Mono<List>` instead of a `Flux`. In layman's terms, it means you get the whole list at once instead of getting the elements one by one and being able to do something on each one individually.

jOOQ Reactive {#h2-3-jooq-reactive}
-----------------------------------

As for the two above frameworks, jOOQ Reactive is similar to its non-reactive version. You first generate the code from the database schema, then use it.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.jooq&lt;/groupId&gt;
    &lt;artifactId&gt;jooq-codegen-maven&lt;/artifactId&gt;                               &lt;!--1--&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;jooq-codegen&lt;/id&gt;
            &lt;phase&gt;generate-sources&lt;/phase&gt;
            &lt;goals&gt;
                &lt;goal&gt;generate&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
    &lt;dependencies&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.postgresql&lt;/groupId&gt;                                 &lt;!--2--&gt;
            &lt;artifactId&gt;postgresql&lt;/artifactId&gt;
            &lt;version&gt;42.6.0&lt;/version&gt;
        &lt;/dependency&gt;
    &lt;/dependencies&gt;
    &lt;configuration&gt;
        &lt;generator&gt;
            &lt;name&gt;org.jooq.codegen.KotlinGenerator&lt;/name&gt;                     &lt;!--3--&gt;
            &lt;database&gt;
                &lt;inputSchema&gt;people&lt;/inputSchema&gt;                             &lt;!--4--&gt;
            &lt;/database&gt;
            &lt;target&gt;
                &lt;packageName&gt;ch.frankel.blog.reactivedata.jooq&lt;/packageName&gt;
            &lt;/target&gt;
        &lt;/generator&gt;
        &lt;jdbc&gt;                                                                &lt;!--4--&gt;
            &lt;driver&gt;org.postgresql.Driver&lt;/driver&gt;
            &lt;url&gt;jdbc:postgresql://localhost:5432/postgres&lt;/url&gt;
            &lt;user&gt;postgres&lt;/user&gt;
            &lt;password&gt;root&lt;/password&gt;
        &lt;/jdbc&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

1. The version is defined in the parent Spring Boot Starter parent POM
2. Set the necessary database driver(s). Note that one should use the **non-reactive** driver
3. There's a Kotlin generator!
4. Configure database configuration parameters

Once you've generated the code, you can create your data class and design the query. jOOQ class hierarchy integrates with Java's collections, Java's Reactive Streams, and Project Reactor.

![](/images/posts/2023/07/reactive-database-access-on-the-jvm/jooq-class-hierarchy.png)

The code may look complex if you're neither a SQL nor a jOOQ expert.  

Remember that variable types are unnecessary, but added for documentation purposes:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun findAll(): Flux&lt;PersonWithAddresses&gt; {                       //1
  val people: SelectJoinStep&lt;Record5&lt;Long?, String?, String?, LocalDate?, MutableList&lt;Address&gt;&gt;&gt; = //2
    ctx.select(
      PERSON.ID,
      PERSON.FIRST_NAME,
      PERSON.LAST_NAME,
      PERSON.BIRTHDATE,
      DSL.multiset(                                              //2
        DSL.select(
          PERSON_ADDRESS.ADDRESS_ID,
          PERSON_ADDRESS.address.FIRST_LINE,
          PERSON_ADDRESS.address.SECOND_LINE,
          PERSON_ADDRESS.address.ZIP,
          PERSON_ADDRESS.address.CITY,
          PERSON_ADDRESS.address.STATE,
          PERSON_ADDRESS.address.COUNTRY,
        ).from(PERSON_ADDRESS)
           .where(PERSON_ADDRESS.PERSON_ID.eq(PERSON.ID))
      ).convertFrom { it.map(addressMapper) }                   //3
  ).from(PERSON)
  return Flux.from(people)                                      //4
             .map(personWithAddressesMapper)                    //3
}</pre>

1. Return a regular Project Reactor's `Flux`
2. Use `multiset`, see below.
3. Convert the row to an ordinary Java object via a function
4. The magic happens here: wrap the regular query in a `Flux` for `people` is a Project Reactor `Publisher`

Let's dive a bit into `multiset` from the point of view of a non-jOOQ expert - me. Initially, I tried to execute a regular SQL query with results I tried to flatten with Project Reactor's API. I failed miserably because of my lack of knowledge of the API, but even if I had succeeded, it would have been the wrong approach.

After hours of research, I found `multiset` via [a post from Vlad Mihalcea](https://vladmihalcea.com/fetch-multiple-to-many-jooq-multiset/):
> The `MULTISET` value constructor is one of jOOQ's and standard SQL's most powerful features. It allows for collecting the results of a *non scalar subquery* into a single nested collection value with `MULTISET` semantics.
>
> -- [MULTISET value constructor](https://www.jooq.org/doc/3.18/manual/sql-building/column-expressions/multiset-value-constructor/)

In the above query, we first select all addresses of a person, map each row to an object, and flatten them in a list on the same result row as the person. The second mapper maps the row, including the address list, to a dedicated person with an addresses list.

I'm not a SQL master, so `multiset` is hard at first glance. However, I confirm that it's a powerful feature indeed.

Note that **nested collections are fetched eagerly on a per-record basis, whereas top-level records are streamed reactively**.

Conclusion {#h2-4-conclusion}
-----------------------------

We have browsed the surface of the main three reactive database access: Spring Data R2DBC, Hibernate, and jOOQ. So, which one should one choose?

The main deciding factor is whether you already use one of their non-reactive flavors. Use the framework you're familiar with since both reactive and non-reactive usages are similar.

I think that jOOQ is extremely powerful but requires a familiarity I currently lack. If you have complex queries that don't map easily to other approaches, it's the way to go.

Besides that, I've no strong opinion, though I find Hibernate Reactive's configuration too limited by JPA and its `Mono<List>` return type puzzling.

*Thanks for [Lukas Eder](https://twitter.com/lukaseder) and [Mark Paluch](https://twitter.com/mp911de) for their reviews on their respective sections of expertise.*

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/reactive-data).

**To go further:**

* [Hibernate Reactive Reference Documentation](https://hibernate.org/reactive/documentation/2.0/reference/html_single/)
* [Integrating Hibernate Reactive with Spring](https://itnext.io/integrating-hibernate-reactive-with-spring-5427440607fe)
* [jOOQ Reactive Fetching](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/reactive-fetching/)
* [How to fetch multiple to-many relationships with jOOQ MULTISET](https://vladmihalcea.com/fetch-multiple-to-many-jooq-multiset/)
* [How to Turn a List of Flat Elements into a Hierarchy in Java, SQL, or jOOQ](https://blog.jooq.org/how-to-turn-a-list-of-flat-elements-into-a-hierarchy-in-java-sql-or-jooq/)
* [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc)
* [Comment bien s'entendre avec avec Spring Data R2DBC... ou pas](https://blog.ippon.fr/2022/03/02/comment-bien-sentendre-avec-avec-r2dbc-ou-pas/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/reactive-database-access/) on July 9^th^, 2023*
