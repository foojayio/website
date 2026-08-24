---
title: "Faster Integration Tests with Reusable Testcontainers"
date: "2022-08-17T11:05:05+00:00"
lastmod: "2022-08-17T11:21:15+00:00"
description: "Learn how to improve your test performance against container-based resources by magnitudes in a couple of easy steps!"
authors:
  - "michael-simons"
image: "logo.png"
categories:
  - "Databases"
  - "Neo4J"
  - "Performance"
  - "Testcontainers"
  - "Testing"
related_posts:
  - "native-graphql-api-with-neo4j-auradb-on-heroku"
  - "easy-jakarta-ee-integration-testing"
  - "build-and-test-non-blocking-web-applications-with-spring-webflux-kotlin-and-coroutines"
  - "soft-assertions-testing-kindly"
frozen: false
---

In my job, I often need integration tests against a [Neo4j](https://neo4j.com) database. My software is written in Java, and so is Neo4j. Neo4j is embeddable, too. So, in theory, I could just depend on the embedded version, open a connection, test my stuff and call it a day. It would not be a different engine, it would be - at least from a query-engine and planer perspective - the same thing, in contrast to using an embedded SQL database as a drop-in for PostgresQL for example.

The biggest downside, however, is that I need to test across several versions, with all of them having a lot of different dependencies. That has caused a lot of trouble and maintenance work in setting up our build files. For that reason we are incredibly thankful for a container based solution in the form of [Testcontainers](https://www.testcontainers.org) and the official [Neo4j module](https://www.testcontainers.org/modules/databases/neo4j/).

I like my tests fast, the same way I like fast road bikes. Therefore, I am gonna show you in two easy examples how to optimize your container-based tests. While I do use the Neo4j container in this example, it works the same with other modules, being it databases or other technology.

In case you prefer a video, here's an animated version of this text in form of [amateur cycling-nutrition and pro-testing tips](https://www.youtube.com/watch?v=737xm5He8aU).

## The simple test

Have a look at the following simple test:

```java
package foojay;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class SimpleTest {

  static final long startTime = System.nanoTime();

  @Container
  static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:4.4");

  static Driver driver;

  @BeforeAll
  static void initConnection() {
    driver = GraphDatabase.driver(
      neo4j.getBoltUrl(),
      AuthTokens.basic("neo4j", neo4j.getAdminPassword())
    );
  }

  @BeforeEach
  void clearDatabase() {
    try (var session = driver.session()) {
      session.run("MATCH (n) DETACH DELETE n").consume();
    }
  }

  @Test
  void shouldCreateNode() {
    try (var session = driver.session()) {
      long internalId = session.run("""
        CREATE (n:Foojay) <-[:WRITES_FOR]-(a:Author {name: 'Michael'})
        RETURN id(a)"""
      ).single().get(0).asLong();
      assertTrue(internalId >= 0);
    }
  }

  @AfterAll
  static void takeTime() {
    var duration = Duration.ofNanos(System.nanoTime() - startTime);
    LoggerFactory.getLogger(SimpleTest.class)
      .info("Test took {}", duration);
  }
}
```

It uses `@Testcontainers` from the Testcontainers JUnit extension `org.testcontainers:junit-jupiter:jar:1.17.3` to mark this test as test depending on Docker and the `@Container` annotation from the same library to mark a final class field as container. The annotation ensures that the container is started before all tests (so that it can be used in a `@BeforeAll` method to retrieve URLs or credentials) and that it gets shutdown after all tests have run.

Apart from that the only thing sticking out is the naive time keeping: It turns out that - at least my IDE - doesn't display the time the whole class initialization took while running the test.

The test itself in this class is irrelevant and does not add to the runtime.

Otherwise this test seems rather ok, as a `@BeforeEach` method makes sure the database is cleared before each test so that individual tests are not interdependent.

On my rather fast machine this test takes about 10 seconds in total… Compared to the single test method taking under 1 sec. This not really fast and I can understand people who say this is not for every day work.

But, fear not, there is...

## The Optimized test

I left out the imports in the following listing. They are the same as the above.

```java
@Testcontainers(disabledWithoutDocker = true)
class OptimizedTest {

  static final long startTime = System.nanoTime();

  static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:4.4")
    .withReuse(true);

  static Driver driver;

  @BeforeAll
  static void initContainerAndConnection() {
    neo4j.start();
    driver = GraphDatabase.driver(neo4j.getBoltUrl(), AuthTokens.basic("neo4j", neo4j.getAdminPassword()));
  }

  @BeforeEach
  void clearDatabase() {
    try (var session = driver.session()) {
      session.run("MATCH (n) DETACH DELETE n").consume();
    }
  }

  @Test
  void shouldCreateNode() {
    try (var session = driver.session()) {
      long internalId = session.run("""
        CREATE (n:Foojay) <-[:WRITES_FOR]-(a:Author {name: 'Michael'})
        RETURN id(a)"""
      ).single().get(0).asLong();
      assertTrue(internalId >= 0);
    }
  }

  @AfterAll
  static void takeTime() {
    var duration = Duration.ofNanos(System.nanoTime() - startTime);
    LoggerFactory.getLogger(SimpleTest.class)
      .info("Test took {}", duration);
  }
}
```

The important differences are:

* Don't use `@Container`
* Start the container manually (in the above example it is done in `@BeforeAll` method named `initContainerAndConnection`)
* Don't stop / close the container
* Use the builder method `.withReuse(true)` when creating the container (in my example this is done on field initialization)

For the latter to work, you will need to prepare your environmenet for Testcontainers. This is done in `~/.testcontainers.properties` (A properties file in your home directory). Mine looks like this:

```properties
testcontainers.reuse.enable=true
```

That is all. The above test will run the same 10 seconds on the first run and after that, it takes roughly 200ms per run. Testcontainers monitor will keep the Neo4j container around and reuse it for all containers based on the same image and the configuration. There is nothing more todo.

In case you need several containers based on the same image and configuration in tests that should not affect each other, you can add arbitrary labels to each container through the `.withLabels(Map.of("label1", "value1"))` builder method.

The Maven build descriptor for the above classes looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>org.example</groupId>
  <artifactId>foojay_fastertests</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.junit</groupId>
        <artifactId>junit-bom</artifactId>
        <version>5.9.0</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.neo4j.driver</groupId>
      <artifactId>neo4j-java-driver</artifactId>
      <version>4.4.9</version>
    </dependency>

    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>1.17.3</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>neo4j</artifactId>
      <version>1.17.3</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-simple</artifactId>
      <version>1.7.36</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.10.1</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M7</version>
      </plugin>
    </plugins>
  </build>
</project>
```

Again, this works of course will all the Testcontainers modules. You do need to make sure however that your test data accross your testsuite does not have interdependencies or if so, make sure you tag the containers with dedicated labels.

Happy coding!
