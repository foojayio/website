---
title: "How to Best Use Java Records as DTOs in Spring Boot 3"
slug: "how-to-best-use-java-records-as-dtos-in-spring-boot-3"
date: "2023-03-09T17:06:46+00:00"
lastmod: "2023-03-09T17:06:47+00:00"
description: "Explore how to best use compact Java Records as Data Transfer Objects (DTOs) for database and API calls in Spring Boot 3 with Hibernate 6."
canonical: "https://dzone.com/articles/how-to-best-use-java-records-as-dtos-in-spring-boo"
authors:
  - "denis-magda"
image: "https://foojay.io/wp-content/uploads/2023/03/java_records_as_dtos.jpg"
categories:
  - "Databases"
  - "Java Core"
  - "Records"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

With the Spring 6 and Spring Boot 3 releases, [Java 17+ became the baseline framework](https://spring.io/blog/2022/11/16/spring-framework-6-0-goes-ga "Java 17+ became the baseline framework") version.

So now is a great time to start using compact [Java Records](https://docs.oracle.com/en/java/javase/14/language/records.html "Java Records") as Data Transfer Objects ([DTOs](https://en.wikipedia.org/wiki/Data_transfer_object "DTOs")) for various database and API calls.

Whether you prefer reading or watching, let's review a few approaches for using Java records as DTOs that apply to Spring Boot 3 with Hibernate 6 as the persistence provider.

{{< youtube uy6iN0d6J8E >}}

<br />

Sample Database {#h2-0-sample-database}
---------------------------------------

Follow these intructions if you'd like to install the sample database and experiment yourself. Otherwise, feel free to skip this section:

* Download the [Chinook Database](https://gist.github.com/dmagda/aea6e71985eebd7ba44e937972c190e8 "Chinook Database") dataset (music store) for the PostgreSQL syntax.
* Start an instance of YugabyteDB, a [PostgreSQL-compliant distributed database](https://www.yugabyte.com/postgresql/postgresql-compatibility/ "PostgreSQL-compliant distributed database"), in Docker:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mkdir ~/yb_docker_data

docker network create custom-network

docker run -d --name yugabytedb_node1 --net custom-network \
  -p 7001:7000 -p 9000:9000 -p 5433:5433 \
  -v ~/yb_docker_data/node1:/home/yugabyte/yb_data --restart unless-stopped \
  yugabytedb/yugabyte:latest \
  bin/yugabyted start \
  --base_dir=/home/yugabyte/yb_data --daemon=false</pre>

* Create the `chinook` database in YugabyteDB:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">createdb -h 127.0.0.1 -p 5433 -U yugabyte -E UTF8 chinook</pre>

* Load the sample dataset: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">psql -h 127.0.0.1 -p 5433 -U yugabyte -f Chinook_PostgreSql_utf8.sql -d chinook</pre>

Next, create a sample Spring Boot 3 application:

* Generate an application template using Spring Boot 3+ and Java 17+ with Spring Data JPA as a dependency: <https://start.spring.io/>
* Add the PostgreSQL driver to the `pom.xml` file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
    &lt;groupId&gt;org.postgresql&lt;/groupId&gt;
    &lt;artifactId&gt;postgresql&lt;/artifactId&gt;
    &lt;version&gt;42.5.4&lt;/version&gt;
&lt;/dependency&gt;</pre>

* Provide YugabyteDB connectivity settings in the `application.properties` file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">spring.datasource.url = jdbc:postgresql://127.0.0.1:5433/chinook
spring.datasource.username = yugabyte
spring.datasource.password = yugabyte</pre>

All set! Now, you're ready to follow the rest of the guide.

Data Model {#h2-1-data-model}
-----------------------------

The Chinook Database comes with many relations, but two tables will be more than enough to show how to use Java records as DTOs.

The first table is `Track`, and below is a definition of a corresponding JPA entity class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Entity
public class Track {
    @Id
    private Integer trackId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(nullable = false)
    private Integer mediaTypeId;

    private Integer genreId;

    private String composer;

    @Column(nullable = false)
    private Integer milliseconds;

    private Integer bytes;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    // Getters and setters are omitted
}</pre>

The second table is `Album` and has the following entity class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Entity
public class Album {
    @Id
    private Integer albumId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer artistId;

    // Getters and setters are omitted
}</pre>

In addition to the entity classes, create a Java Record named `TrackRecord` that stores short but descriptive song information:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public record TrackRecord(String name, String album, String composer) {}</pre>

Naive Approach {#h2-2-naive-approach}
-------------------------------------

Imagine you need to implement a REST endpoint that returns a short song description. The API needs to provide song and album names, as well as the author's name.

The previously created `TrackRecord` class can fit the required information. So, let's create a record using the naive approach that gets the data via Entity classes:

* Add the following JPA Repository: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface TrackRepository extends JpaRepository&lt;Track, Integer&gt; {
}</pre>

* Add a Spring Boot's Service-level method that creates a `TrackRecord` instance from the `Track` entity class. The latter is retrieved via the `TrackRepository` instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Transactional(readOnly = true)
public TrackRecord getTrackRecord(Integer trackId) {
    Track track = repository.findById(trackId).get();

    TrackRecord trackRecord = new TrackRecord(
            track.getName(),
            track.getAlbum().getTitle(),
            track.getComposer());

    return trackRecord;
}</pre>

The solution looks simple and compact, but it's very inefficient because Hibernate needs to instantiate two entities first---`Track` and `Album` (see the `track.getAlbum().getTitle()`).

To do this, it generates two SQL queries that request all the columns of the corresponding database tables:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Hibernate: 
    select
        t1_0.track_id,
        t1_0.album_id,
        t1_0.bytes,
        t1_0.composer,
        t1_0.genre_id,
        t1_0.media_type_id,
        t1_0.milliseconds,
        t1_0.name,
        t1_0.unit_price 
    from
        track t1_0 
    where
        t1_0.track_id=?
Hibernate: 
    select
        a1_0.album_id,
        a1_0.artist_id,
        a1_0.title 
    from
        album a1_0 
    where
        a1_0.album_id=?</pre>

Hibernate selects 12 columns across two tables, but `TrackRecord` needs only three columns!

This is a waste of memory, computing, and networking resources, especially if you use distributed databases like YugabyteDB that scatters data across multiple cluster nodes.

TupleTransformer {#h2-3-tupletransformer}
-----------------------------------------

The naive approach can be easily remediated if you query only the records the API requires then transform a query result set to a respective Java Record.

The Spring Data module of Spring Boot 3 relies on Hibernate 6. That version of Hibernate split the `ResultTransformer` interface into two interfaces - `TupleTransformer` and `ResultListTransformer`.

The `TupleTransformer` class supports Java records, so, the implementation of the `public TrackRecord getTrackRecord(Integer trackId)` can be optimized this way:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Transactional(readOnly = true)
public TrackRecord getTrackRecord(Integer trackId) {
    org.hibernate.query.Query&lt;TrackRecord&gt; query = entityManager.createQuery(
            """
            SELECT t.name, a.title, t.composer
            FROM Track t
            JOIN Album a ON t.album.albumId=a.albumId
            WHERE t.trackId=:id
            """).
            setParameter("id", trackId).
            unwrap(org.hibernate.query.Query.class);

    TrackRecord trackRecord = query.setTupleTransformer((tuple, aliases) -&gt; {
        return new TrackRecord(
                (String) tuple[0],
                (String) tuple[1],
                (String) tuple[2]);
    }).getSingleResult();

    return trackRecord;
}
</pre>

* `entityManager.createQuery(...)` - creates a JPA query that requests three columns that are needed for the `TrackRecord` class.
* `query.setTupleTransformer(...)` - the TupleTransformer supports Java records which means a `TrackRecord` instance can be created in the transformer's implementation.

This approach is more efficient than the previous one because you no longer need to create Entity classes and can easily construct a Java Record with the `TupleTransformer`.

Plus, Hibernate generates a single SQL request that returns only the required columns:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Hibernate: 
    select
        t1_0.name,
        a1_0.title,
        t1_0.composer 
    from
        track t1_0 
    join
        album a1_0 
            on t1_0.album_id=a1_0.album_id 
    where
        t1_0.track_id=?</pre>

However, there is one, very visible downside to this approach ---the implementation of the `public TrackRecord getTrackRecordV2(Integer trackId)` became longer and wordier.

Java Record Within JPA Query {#h2-4-java-record-within-jpa-query}
-----------------------------------------------------------------

There are several ways to shorten the previous implementation. One is to instantiate a Java Record instance within a JPA query.

First, expand the implementation of the `TrackRepository` interface with a custom query that creates a `TrackRecord` instance from requested database columns:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface TrackRepository extends JpaRepository&lt;Track, Integer&gt; {
        @Query("""
                        SELECT new com.my.springboot.app.TrackRecord(t.name, a.title, t.composer)
                        FROM Track t
                        JOIN Album a ON t.album.albumId=a.albumId
                        WHERE t.trackId=:id
                        """)
        TrackRecord findTrackRecord(@Param("id") Integer trackId);</pre>

Next, update the implementation of the `public TrackRecord getTrackRecord(Integer trackId)` this way:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Transactional(readOnly = true)
public TrackRecord getTrackRecord(Integer trackId) {
     return repository.findTrackRecord(trackId);
}</pre>

So, the method implementation is a one-liner that gets a `TrackRecord` instance straight from the JPA repository. As simple as possible.

But that's not all. There is one more small issue. The JPA query that constructs a Java Record requires you to provide a full package name for the `TrackRecord` class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SELECT new com.my.springboot.app.TrackRecord(t.name, a.title, t.composer)...</pre>

Let's find a way to bypass this requirement. Ideally, the Java Record needs to be instantiated without the package name:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SELECT new TrackRecord(t.name, a.title, t.composer)...</pre>

Hypersistence Utils {#h2-5-hypersistence-utils}
-----------------------------------------------

[Hypersistence Utils](https://github.com/vladmihalcea/hypersistence-utils "Hypersistence Utils") library comes with many goodies for Spring and Hibernate. One feature allows you to create a Java Record instance within a JPA query without the package name.

Let's enable the library and this Java records-related feature in the Spring Boot application:

* Add the library's Maven artifact for Hibrenate 6: <https://github.com/vladmihalcea/hypersistence-utils>
* Create a custom `IntegratorProvider` that registers `TrackRecord` class with Hibernate:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class ClassImportIntegratorProvider implements IntegratorProvider {
    @Override
    public List&lt;Integrator&gt; getIntegrators() {
        return List.of(new ClassImportIntegrator(List.of(TrackRecord.class)));
    }
}</pre>

* Update the `application.properties` file by adding this custom `IntegratorProvider`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">spring.jpa.properties.hibernate.integrator_provider=com.my.springboot.app.ClassImportIntegratorProvider
</pre>

After that you can update the JPA query of the `TrackRepository.findTrackRecord(...)` method by removing the Java Record's package name (`com.my.springboot.app`) from the query string:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Query("""
               SELECT new TrackRecord(t.name, a.title, t.composer)
               FROM Track t
               JOIN Album a ON t.album.albumId=a.albumId
               WHERE t.trackId=:id
               """)
 TrackRecord findTrackRecord(@Param("id") Integer trackId);</pre>

It's that simple!

Summary {#h2-6-summary}
-----------------------

The latest versions of Java, Spring, and Hibernate have a number of significant enhancements to simplify and make coding in Java more fun.

One such enhancement is built-in support for Java records that can now be easily used as DTOs in Spring Boot applications.

Enjoy!
