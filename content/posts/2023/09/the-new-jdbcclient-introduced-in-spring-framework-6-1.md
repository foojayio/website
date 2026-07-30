---
title: "Spring's new JdbcClient API for performing database operations"
slug: "the-new-jdbcclient-introduced-in-spring-framework-6-1"
date: "2023-09-08T06:45:44+00:00"
lastmod: "2023-09-08T06:45:45+00:00"
description: "Learn how to perform JDBC operations using the new JdbcClient fluent API introduced in Spring framework 6.1."
authors:
  - "siva-katamreddy"
image: "/images/posts/2023/09/the-new-jdbcclient-introduced-in-spring-framework-6-1/1024px-Spring_Framework_Logo_2018.svg.png"
categories:
  - "Java"
  - "Spring"
  - "Testcontainers"
tags:
related_posts:
  - "testing-and-local-development-made-simpler-with-testcontainers-desktop-app"
  - "spring-6-1-restclient"
  - "how-to-create-sboms-in-java-with-maven-and-gradle"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

**Spring framework 6.1 introduced a new JdbcClient API, which is a wrapper on top of JdbcTemplate, for performing database operations using a fluent API.**

Spring Boot 3.2 is going to include Spring framework 6.1, so let's take a quick look at  

how we can use **JdbcClient** to implement various database operations in a simplified manner.

First, let's go to <https://start.spring.io/> and create a Spring Boot application by selecting  
**Spring JDBC** , **PostgreSQL Driver** , **Flyway Migration** , and **Testcontainers** starters.
> **NOTE:**   
>
> At the time of writing this article, Spring Boot 3.2.0-M2 is released,  
>
> so we are going to select 3.2.0 (M2) as the Spring Boot version.

Create Bookmark domain class {#h2-0-create-bookmark-domain-class}
-----------------------------------------------------------------

Let's start with creating a Java record representing a **Bookmark** as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.time.Instant;

public record Bookmark(Long id, String title, String url, Instant createdAt) {}</pre>

Create Flyway Migration Script {#h2-1-create-flyway-migration-script}
---------------------------------------------------------------------

Let's add the following migration script under **src/main/resources/db/migration** directory.

**V1__create_tables.sql**

<pre class="EnlighterJSRAW" data-enlighter-language="sql">create table bookmarks
(
    id         bigserial primary key,
    title      varchar   not null,
    url        varchar   not null,
    created_at timestamp
);</pre>

Implementing CRUD operations using JdbcClient {#h2-2-implementing-crud-operations-using-jdbcclient}
---------------------------------------------------------------------------------------------------

Let's implement CRUD operations on **Bookmark** domain class using **JdbcClient** API.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Repository
@Transactional(readOnly = true)
public class BookmarkRepository {
    private final JdbcClient jdbcClient;

    public BookmarkRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
    ...
    ...
    ...
}</pre>

### Fetch all bookmarks {#h3-3-fetch-all-bookmarks}

We can fetch all bookmarks using **JdbcClient** as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public List&lt;Bookmark&gt; findAll() {
    String sql = "select id, title, url, created_at from bookmarks";
    return jdbcClient.sql(sql).query(Bookmark.class).list();
}</pre>

The **JdbcClient** API will take care of dynamically creating a **RowMapper** by using **SimplePropertyRowMapper**. It will perform the mapping between bean property names to table column names by converting camelCase to underscore notation.

If you need more control over the mapping, you can create a **RowMapper** yourself and use it as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public List&lt;Bookmark&gt; findAll() {
    String sql = "select id, title, url, created_at from bookmarks";
    return jdbcClient.sql(sql).query(new BookmarkRowMapper()).list();
}

static class BookmarkRowMapper implements RowMapper&lt;Bookmark&gt; {
    @Override
    public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Bookmark(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("url"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}</pre>

### Find bookmark By ID {#h3-4-find-bookmark-by-id}

We can fetch a bookmark by **id** using **JdbcClient** as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public Optional&lt;Bookmark&gt; findById(Long id) {
    String sql = "select id, title, url, created_at from bookmarks where id = :id";
    return jdbcClient.sql(sql).param("id", id).query(Bookmark.class).optional();

    // If you want to use your own RowMapper
    //return jdbcClient.sql(sql).param("id", id).query(new BookmarkRowMapper()).optional();
}</pre>

### Create a new bookmark {#h3-5-create-a-new-bookmark}

We can use PostgreSQL **INSERT INTO ... RETURNING COL1, COL2** syntax and then use **KeyHolder** to get the generated primary key value.

So, we can insert a new row into the **bookmarks** table and get the generated primary key value as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Transactional
public Long save(Bookmark bookmark) {
    String sql = "insert into bookmarks(title, url, created_at) values(:title,:url,:createdAt) returning id";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcClient.sql(sql)
                .param("title", bookmark.title())
                .param("url", bookmark.url())
                .param("createdAt", Timestamp.from(bookmark.createdAt()))
                .update(keyHolder);
    return keyHolder.getKeyAs(Long.class);
}</pre>

### Update a bookmark {#h3-6-update-a-bookmark}

We can update a bookmark as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Transactional
public void update(Bookmark bookmark) {
    String sql = "update bookmarks set title = ?, url = ? where id = ?";
    int count = jdbcClient.sql(sql)
            .param(1, bookmark.title())
            .param(2, bookmark.url())
            .param(3, bookmark.id())
            .update();
    if (count == 0) {
        throw new RuntimeException("Bookmark not found");
    }
}</pre>

In the **update(...)** method, I have used positional parameters **(?)** instead of using named parameters **(:title)** for the demonstration purpose. I highly recommend using named parameters over positional parameters.

### Delete a bookmark {#h3-7-delete-a-bookmark}

We can delete a bookmark as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Transactional
public void delete(Long id) {
    String sql = "delete from bookmarks where id = ?";
    int count = jdbcClient.sql(sql).param(1, id).update();
    if (count == 0) {
        throw new RuntimeException("Bookmark not found");
    }
}</pre>

Test Repository using Testcontainers {#h2-8-test-repository-using-testcontainers}
---------------------------------------------------------------------------------

We should always make sure that the database is in a known state so that we can write predictable assertions. So, let's create **src/test/resources/test_data.sql** file with the following content:

<pre class="EnlighterJSRAW" data-enlighter-language="sql">TRUNCATE TABLE bookmarks;
ALTER SEQUENCE bookmarks_id_seq RESTART WITH 1;

INSERT INTO bookmarks(title, url, created_at) VALUES
('How (not) to ask for Technical Help?','https://sivalabs.in/how-to-not-to-ask-for-technical-help', CURRENT_TIMESTAMP),
('Getting Started with Kubernetes','https://sivalabs.in/getting-started-with-kubernetes', CURRENT_TIMESTAMP),
('Few Things I learned in the HardWay in 15 years of my career','https://sivalabs.in/few-things-i-learned-the-hardway-in-15-years-of-my-career', CURRENT_TIMESTAMP),
('All the resources you ever need as a Java &amp; Spring application developer','https://sivalabs.in/all-the-resources-you-ever-need-as-a-java-spring-application-developer', CURRENT_TIMESTAMP),
('SpringBoot Integration Testing using Testcontainers Starter','https://sivalabs.in/spring-boot-integration-testing-using-testcontainers-starter', CURRENT_TIMESTAMP),
('Testing SpringBoot Applications','https://sivalabs.in/spring-boot-testing', CURRENT_TIMESTAMP)
;</pre>

Now, we can add the annotation **@Sql("/test-data.sql")** to our test class so that before running each test, the specified SQL script will be executed.

Let's test our **BookmarkRepository** , of course, using [Testcontainers](https://testcontainers.com) as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.sivalabs.bookmarks.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {
   "spring.test.database.replace=none",
   "spring.datasource.url=jdbc:tc:postgresql:15.4-alpine:///db"
})
@ImportAutoConfiguration(JdbcClientAutoConfiguration.class)
@Sql("/test-data.sql")
class BookmarkRepositoryTest {

    @Autowired
    JdbcClient jdbcClient;

    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        bookmarkRepository = new BookmarkRepository(jdbcClient);
    }

    @Test
    void shouldFindAllBookmarks() {
        List&lt;Bookmark&gt; bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).isNotEmpty();
        assertThat(bookmarks).hasSize(6);
    }

    @Test
    void shouldCreateBookmark() {
        Bookmark bookmark = new Bookmark(null, "My Title", "https://sivalabs.in", Instant.now());
        Long id = bookmarkRepository.save(bookmark);
        assertThat(id).isNotNull();
    }

    @Test
    void shouldGetBookmarkById() {
        Bookmark bookmark = new Bookmark(null, "My Title", "https://sivalabs.in", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        Optional&lt;Bookmark&gt; bookmarkOptional = bookmarkRepository.findById(id);
        assertThat(bookmarkOptional).isPresent();
        assertThat(bookmarkOptional.get().id()).isEqualTo(id);
        assertThat(bookmarkOptional.get().title()).isEqualTo(bookmark.title());
        assertThat(bookmarkOptional.get().url()).isEqualTo(bookmark.url());
    }

    @Test
    void shouldEmptyWhenBookmarkNotFound() {
        Optional&lt;Bookmark&gt; bookmarkOptional = bookmarkRepository.findById(9999L);
        assertThat(bookmarkOptional).isEmpty();
    }

    @Test
    void shouldUpdateBookmark() {
        Bookmark bookmark = new Bookmark(null, "My Title", "https://sivalabs.in", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        Bookmark changedBookmark = new Bookmark(id, "My Updated Title", "https://www.sivalabs.in", bookmark.createdAt());
        bookmarkRepository.update(changedBookmark);

        Bookmark updatedBookmark = bookmarkRepository.findById(id).orElseThrow();
        assertThat(updatedBookmark.id()).isEqualTo(changedBookmark.id());
        assertThat(updatedBookmark.title()).isEqualTo(changedBookmark.title());
        assertThat(updatedBookmark.url()).isEqualTo(changedBookmark.url());
    }

    @Test
    void shouldDeleteBookmark() {
        Bookmark bookmark = new Bookmark(null, "My Title", "https://sivalabs.in", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        bookmarkRepository.delete(id);

        Optional&lt;Bookmark&gt; optionalBookmark = bookmarkRepository.findById(id);
        assertThat(optionalBookmark).isEmpty();
    }
}</pre>

We have used the Testcontainers special JDBC URL to start PostgreSQL database and run the tests using it.

Summary {#h2-9-summary}
-----------------------

The new **JdbcClient** API provides a nice fluent API to implement data access layer using JDBC.

While you can still use good old JdbcTemplate, I would highly recommend using JdbcClient over JdbcTemplate going forward.

You can find the sample code for this tutorial in this [GitHub](https://github.com/sivaprasadreddy/spring-boot-tutorials-blog-series/tree/main/spring-boot-jdbcclient-tutorial) repository.
