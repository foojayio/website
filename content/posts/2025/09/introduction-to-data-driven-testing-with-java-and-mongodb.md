---
title: "Introduction to Data-Driven Testing with Java and MongoDB"
slug: "introduction-to-data-driven-testing-with-java-and-mongodb"
date: "2025-09-25T13:53:57+00:00"
lastmod: "2025-09-25T13:53:59+00:00"
description: "Data-driven testing (DDT) is important because it enables you to validate business behavior across various scenarios without having to duplicate test logic. When used in conjunction with a document database like MongoDB, it becomes a powerful tool to ensure that your queries align not only with the data structure but also with the intended business outcomes. For Java applications where persistence logic goes beyond basic CRUD operations, data-driven testing helps you safeguard what matters most: correctness, clarity, and confidence."
authors:
  - "otavio-santana"
image: "https://foojay.io/wp-content/uploads/2025/09/java.jpeg"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "building-rest-apis-in-java-with-spring-boot"
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
enlighterjs: true
frozen: false
---

As applications expand, the complexity of the rules they enforce also increases. In many systems, these rules are embedded within the data, primarily in database queries that filter, join, or compute based on real-world conditions. However, the tests for these queries are often shallow, repetitive, or, worse yet, completely absent. When there is an error in the database logic, the application may still compile successfully, but the business can suffer significant consequences.

**Data-driven testing (DDT)** is important because it enables you to validate business behavior across various scenarios without having to duplicate test logic. When used in conjunction with a document database like MongoDB, it becomes a powerful tool to ensure that your queries align not only with the data structure but also with the intended business outcomes. For Java applications where persistence logic goes beyond basic CRUD operations, data-driven testing helps you safeguard what matters most: correctness, clarity, and confidence.

In this tutorial, you'll:

* Model a domain (Room, RoomType, RoomStatus).
* Write semantic repository queries using Jakarta Data.
* Run data-driven tests using JUnit 5 and AssertJ.
* Validate MongoDB queries in isolation using Testcontainers and Weld.

You can find all the code presented in this tutorial in the [GitHub repository](https://github.com/soujava/data-driven-test-mongodb):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="197e706d597e706d716c7b377a7674">[email&nbsp;protected]</a>:soujava/data-driven-test-mongodb.git</pre>

Prerequisites {#h2-0-prerequisites}
-----------------------------------

For this tutorial, you'll need:

* Java 21.
* Maven.
* A MongoDB cluster.
  * [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim) (Option 1)
  * Docker (Option 2)

You can use the following Docker command to start a standalone MongoDB instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run --rm -d --name mongodb-instance -p 27017:27017 mongo</pre>

Data-driven testing is a technique where a single test is executed with different input data sets to validate multiple scenarios. Instead of writing repetitive test methods, you define combinations of inputs and expected outcomes, making it easier to test edge cases, business rules, and regression scenarios with clarity and efficiency.

In this age, data is the heart of any software system. Ensuring consistency and validating the code based on several data conditions will increase your quality. In this process, we can use a database to inject the data as input, to check the queries themselves, and also store the results as a report.

This approach aligns naturally with MongoDB, where application logic frequently relies on query conditions---particularly when filtering based on domain-specific rules. In a typical Java application, these queries are embedded in repositories or services and are rarely tested beyond simple "find by ID" checks. DDT allows you to test the *intent* behind queries, such as a hotel management system: Is a room considered available? Does it match VIP criteria? Should it be cleaned?

In this tutorial, we'll use aJava SE project---without any heavyweight frameworks---to demonstrate how to combine Jakarta Data, JNoSQL, and JUnit 5 to write expressive, testable queries against MongoDB. Our focus will be on clarity, maintainability, and aligning tests with the business language, not just with database fields.

Step 1: Create the entities {#h2-1-step-1-create-the-entities}
--------------------------------------------------------------

The first step is to create a plain Maven project, where we can use Maven Archetype[quick start](https://maven.apache.org/archetypes/maven-archetype-quickstart/). After making the Maven project, the next step is to include Jupiter, Mockito, AssertJ, and Testcontainers for test proposals. For the Java integration and MongoDB, we will explore it using the Java Enterprise specification, Jakarta EE, where we will utilize both specifications, Jakarta NoSQL and Jakarta Data, both of which are implemented by Eclipse JNoSQL. We don't need to spend a considerable amount of time setting it up; you can clone the GitHub repository. The pom.xml shows the dependencies using Java with the Apache Maven Project:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;net.datafaker&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;datafaker&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;2.4.4&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.eclipse.jnosql.databases&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;jnosql-mongodb&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${jnosql.version}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.jboss.weld.se&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;weld-se-shaded&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${weld.se.core.version}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;compile&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.eclipse&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;yasson&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;3.0.4&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;compile&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;io.smallrye.config&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;smallrye-config-core&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;3.13.3&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;compile&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.eclipse.microprofile.config&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;microprofile-config-api&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;3.1&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;compile&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.junit.jupiter&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;junit-jupiter-api&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${junit.version}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.junit.jupiter&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;junit-jupiter-engine&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${junit.version}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.junit.jupiter&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;junit-jupiter-params&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${junit.version}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mockito&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mockito-core&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${mockito.verson}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mockito&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mockito-junit-jupiter&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;${mockito.verson}&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.jboss.weld&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;weld-junit5&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;5.0.1.Final&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.testcontainers&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;1.21.3&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.assertj&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;assertj-core&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;3.27.3&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependencies&gt;</pre>

With the project done, the next step is setting and defining the entities. In our sample, we will explore a simple hotel management system, where we will extract some use cases to further explore the data-driven test. Naturally, a hotel management system brings way more complexity than that. Thus, we won't cover points such as payment. Therefore, we will create a Room entity and its enums that will bring the Value Object perspective.

In the src/main/java directory, create a *Room* class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.Objects;

@Entity
public class Room {

&nbsp;&nbsp;&nbsp;&nbsp;@Id
&nbsp;&nbsp;&nbsp;&nbsp;private String id;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private int number;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private RoomType type;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private RoomStatus status;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private CleanStatus cleanStatus;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private boolean smokingAllowed;

&nbsp;&nbsp;&nbsp;&nbsp;@Column
&nbsp;&nbsp;&nbsp;&nbsp;private boolean underMaintenance;

}

public enum RoomStatus {
&nbsp;&nbsp;&nbsp;&nbsp;AVAILABLE,
&nbsp;&nbsp;&nbsp;&nbsp;RESERVED,
&nbsp;&nbsp;&nbsp;&nbsp;UNDER_MAINTENANCE,
&nbsp;&nbsp;&nbsp;&nbsp;OUT_OF_SERVICE
}

public enum RoomType {
&nbsp;&nbsp;&nbsp;&nbsp;STANDARD,
&nbsp;&nbsp;&nbsp;&nbsp;DELUXE,
&nbsp;&nbsp;&nbsp;&nbsp;SUITE,
&nbsp;&nbsp;&nbsp;&nbsp;VIP_SUITE
}

public enum CleanStatus {
&nbsp;&nbsp;&nbsp;&nbsp;CLEAN,
&nbsp;&nbsp;&nbsp;&nbsp;DIRTY,
&nbsp;&nbsp;&nbsp;&nbsp;INSPECTION_NEEDED
}</pre>

Explanation of annotations: {#h2-2-explanation-of-annotations}
--------------------------------------------------------------

* **@Entity**: Marks the Room class as a database entity for management by Jakarta NoSQL.
* **@Id:** Indicates the primary identifier for the entity, uniquely distinguishing each document in the MongoDB collection.
* **@Column**: Maps fields (roomNumber, type) for reading from or writing to MongoDB.

Finally, with the entity done, we will create a repository where the goal is to find rooms by type, insert a new room, and check for availability on rooms---both standard and VIP rooms:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Repository
public interface RoomRepository {

&nbsp;&nbsp;&nbsp;&nbsp;@Query("WHERE type = 'VIP_SUITE' AND status = 'AVAILABLE' AND underMaintenance = false")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; findVipRoomsReadyForGuests();

&nbsp;&nbsp;&nbsp;&nbsp;@Query(" WHERE type &lt;&gt; 'VIP_SUITE' AND status = 'AVAILABLE' AND cleanStatus = 'CLEAN'")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; findAvailableStandardRooms();

&nbsp;&nbsp;&nbsp;&nbsp;@Query("WHERE cleanStatus &lt;&gt; 'CLEAN' AND status &lt;&gt; 'OUT_OF_SERVICE'")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; findRoomsNeedingCleaning();

&nbsp;&nbsp;&nbsp;&nbsp;@Query("WHERE smokingAllowed = true AND status = 'AVAILABLE'")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; findAvailableSmokingRooms();

&nbsp;&nbsp;&nbsp;&nbsp;@Save
&nbsp;&nbsp;&nbsp;&nbsp;void save(List&lt;Room&gt; rooms);

&nbsp;&nbsp;&nbsp;&nbsp;@Save
&nbsp;&nbsp;&nbsp;&nbsp;Room newRoom(Room room);
&nbsp;&nbsp;&nbsp;&nbsp;void deleteBy();

&nbsp;&nbsp;&nbsp;&nbsp;@Query("WHERE type = :type")
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; findByType(@Param("type") RoomType type);
}</pre>

We have those queries that explore Jakarta Data Queries, but are they properly working? In the next step, we will generate some tests to check it.

Step 2: Create a database container {#h2-3-step-2-create-a-database-container}
------------------------------------------------------------------------------

After the entity and repository are done, the next step is to generate the test structure. Use Testcontainer to ensure that the database is running when we use this container. We will generate an enum to implement the Singleton pattern, which will create a MongoDB database container and start a MongoDB instance for testing purposes.

Thus, at src/test/java, create the DatabaseContainer class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentConfiguration;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentConfigurations;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentManager;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentManagerFactory;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.HashMap;
import java.util.Map;
public enum DatabaseContainer {

&nbsp;&nbsp;&nbsp;&nbsp;INSTANCE;

&nbsp;&nbsp;&nbsp;&nbsp;private final GenericContainer&lt;?&gt; mongodb =
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new GenericContainer&lt;&gt;("mongo:latest")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.withExposedPorts(27017)
&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.waitingFor(Wait.defaultWaitStrategy());

&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mongodb.start();
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public MongoDBDocumentManager get(String database) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Settings settings = getSettings(database);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDBDocumentConfiguration configuration = new MongoDBDocumentConfiguration();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDBDocumentManagerFactory factory = configuration.apply(settings);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return factory.apply(database);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;private Settings getSettings(String database) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Map&lt;String,Object&gt; settings = new HashMap&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;settings.put(MongoDBDocumentConfigurations.HOST.get()+".1", host());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;settings.put(MappingConfigurations.DOCUMENT_DATABASE.get(), database);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Settings.of(settings);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public String host() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return mongodb.getHost() + ":" + mongodb.getFirstMappedPort();
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

In the code, we will generate a MongoDB database instance by container that we have a DatabaseConfiguration.

With the database container ready, the next step is to instruct CDI to use a MongoDB container from Testcontainer during testing, rather than any production configuration. We can do this by exploring the CDI capability of alternatives.

At src//test/java, create the ManagerSupplier class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.interceptor.Interceptor;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;

import java.util.function.Supplier;

@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class ManagerSupplier implements Supplier&lt;DatabaseManager&gt; {

&nbsp;&nbsp;&nbsp;&nbsp;@Produces
&nbsp;&nbsp;&nbsp;&nbsp;@Database(DatabaseType.DOCUMENT)
&nbsp;&nbsp;&nbsp;&nbsp;@Default
&nbsp;&nbsp;&nbsp;&nbsp;@Typed({DatabaseManager.class, MongoDBDocumentManager.class})
&nbsp;&nbsp;&nbsp;&nbsp;public MongoDBDocumentManager get() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return DatabaseContainer.INSTANCE.get("hotel");
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

On this class, we can see that we are overwriting the behavior at test where we are using the DatabaseContainer where the database is called hotel. With the structure done, the next step is playing with tests and DDT.

Step 3: Generate our first DDT {#h2-4-step-3-generate-our-first-ddt}
--------------------------------------------------------------------

One of the goals of data-driven testing is to achieve test coverage across various input combinations and capture the expected output. The first test we will generate is to verify that we are saving the information properly in the database. In this case, it does not matter which room is selected; it should insert and also generate an ID for it.

At src/test/java, create the RoomServiceTest class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@EnableAutoWeld
@AddPackages(value = {Database.class, EntityConverter.class, DocumentTemplate.class, MongoDBTemplate.class})
@AddPackages(Room.class)
@AddPackages(ManagerSupplier.class)
@AddPackages(MongoDBTemplate.class)
@AddPackages(Reflections.class)
@AddPackages(Converters.class)
@AddExtensions({ReflectionEntityMetadataExtension.class, DocumentExtension.class})
class RoomServiceTest {

@Inject
private RoomRepository repository;

private static final Faker FAKER = new Faker();

@ParameterizedTest
@MethodSource("room")
void shouldSaveRoom(Room room) {
&nbsp;&nbsp;&nbsp;&nbsp;Room updateRoom = this.repository.newRoom(room);
&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom).isNotNull();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getId()).isNotNull();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getNumber()).isEqualTo(room.getNumber());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getType()).isEqualTo(room.getType());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getStatus()).isEqualTo(room.getStatus());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getCleanStatus()).isEqualTo(room.getCleanStatus());

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.isSmokingAllowed()).isEqualTo(room.isSmokingAllowed());
&nbsp;&nbsp;&nbsp;&nbsp;});
}

&nbsp;&nbsp;static Stream&lt;Arguments&gt; room() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Stream.of(Arguments.of(getRoom(), Arguments.of(getRoom(), Arguments.of(getRoom()))));
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;private static Room getRoom() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(FAKER.number().numberBetween(100, 999))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(randomEnum(RoomType.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(randomEnum(RoomStatus.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(randomEnum(CleanStatus.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(FAKER.bool().bool())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();
&nbsp;&nbsp;&nbsp;&nbsp;}

private static &lt;T extends Enum&lt;?&gt;&gt; T randomEnum(Class&lt;T&gt; enumClass) {
&nbsp;&nbsp;&nbsp;&nbsp;T[] constants = enumClass.getEnumConstants();
&nbsp;&nbsp;&nbsp;&nbsp;int index = ThreadLocalRandom.current().nextInt(constants.length);
&nbsp;&nbsp;&nbsp;&nbsp;return constants[index];
}

}</pre>

At the header of RoomService, we have a couple of annotations to activate Weld, the CDI implementation, in addition to which classes and packages the CDI should scan. It facilitates and makes the test startup lighter than scanning the whole class. Here, we are using AssertJ to further explore the fluent API for checking the database. We are using soft assertions that execute the whole validations and then show which conditions have break. It is way more useful when we need to do several validations in a single method.

We will generate a new test scenario that allows us to find rooms by type. Naturally, we want to ensure that it works for any kind of search. At the same class, we will generate a method where we will inject the enum by parameter, as you can see in the code below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ParameterizedTest(name = "should find rooms by type {0}")
@EnumSource(RoomType.class)
void shouldFindRoomByType(RoomType type) {
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findByType(type);
&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; softly.assertThat(rooms).allMatch(room -&gt; room.getType().equals(type)));
}</pre>

We are injecting several inputs to validate the tests, and we can explore it even further to see if the tests qualify:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.databases.mongodb.mapping.MongoDBTemplate;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@EnableAutoWeld
@AddPackages(value = {Database.class, EntityConverter.class, DocumentTemplate.class, MongoDBTemplate.class})
@AddPackages(Room.class)
@AddPackages(ManagerSupplier.class)
@AddPackages(MongoDBTemplate.class)
@AddPackages(Reflections.class)
@AddPackages(Converters.class)
@AddExtensions({ReflectionEntityMetadataExtension.class, DocumentExtension.class})
class RoomServiceTest {

&nbsp;&nbsp;&nbsp;&nbsp;@Inject
&nbsp;&nbsp;&nbsp;&nbsp;private RoomRepository repository;

&nbsp;&nbsp;&nbsp;&nbsp;private static final&nbsp; Faker FAKER = new Faker();

&nbsp;&nbsp;&nbsp;&nbsp;@BeforeEach
&nbsp;&nbsp;&nbsp;&nbsp;void setUP() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room vipRoom1 = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(101)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.VIP_SUITE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.AVAILABLE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.CLEAN)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(false)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room vipRoom2 = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(102)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.VIP_SUITE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.AVAILABLE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.CLEAN)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(true)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room standardRoom1 = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(201)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.STANDARD)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.AVAILABLE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.CLEAN)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(false)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room standardRoom2 = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(202)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.DELUXE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.AVAILABLE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.CLEAN)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(false)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room dirtyReservedRoom = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(301)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.DELUXE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.RESERVED)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.DIRTY)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(false)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room dirtySuiteRoom = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(302)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.SUITE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.UNDER_MAINTENANCE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.INSPECTION_NEEDED)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(false)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room smokingAllowedRoom = new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(401)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(RoomType.STANDARD)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(RoomStatus.AVAILABLE)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(CleanStatus.CLEAN)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(true)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;repository.save(List.of(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;vipRoom1, vipRoom2,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;standardRoom1, standardRoom2,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dirtyReservedRoom, dirtySuiteRoom,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;smokingAllowedRoom
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;));
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@AfterEach
&nbsp;&nbsp;&nbsp;&nbsp;void cleanUp() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;repository.deleteBy();
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@ParameterizedTest(name = "should find rooms by type {0}")
&nbsp;&nbsp;&nbsp;&nbsp;@EnumSource(RoomType.class)
&nbsp;&nbsp;&nbsp;&nbsp;void shouldFindRoomByType(RoomType type) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findByType(type);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; softly.assertThat(rooms).allMatch(room -&gt; room.getType().equals(type)));
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@ParameterizedTest
&nbsp;&nbsp;&nbsp;&nbsp;@MethodSource("room")
&nbsp;&nbsp;&nbsp;&nbsp;void shouldSaveRoom(Room room) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Room updateRoom = this.repository.newRoom(room);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom).isNotNull();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getId()).isNotNull();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getNumber()).isEqualTo(room.getNumber());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getType()).isEqualTo(room.getType());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getStatus()).isEqualTo(room.getStatus());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.getCleanStatus()).isEqualTo(room.getCleanStatus());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(updateRoom.isSmokingAllowed()).isEqualTo(room.isSmokingAllowed());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Test
&nbsp;&nbsp;&nbsp;&nbsp;void shouldFindRoomReadyToGuest() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findAvailableStandardRooms();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).hasSize(3);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; room.getStatus().equals(RoomStatus.AVAILABLE));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; !room.isUnderMaintenance());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Test
&nbsp;&nbsp;&nbsp;&nbsp;void shouldFindVipRoomsReadyForGuests() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findVipRoomsReadyForGuests();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).hasSize(2);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; room.getType().equals(RoomType.VIP_SUITE));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; room.getStatus().equals(RoomStatus.AVAILABLE));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; !room.isUnderMaintenance());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Test
&nbsp;&nbsp;&nbsp;&nbsp;void shouldFindAvailableSmokingRooms() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findAvailableSmokingRooms();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).hasSize(2);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; room.isSmokingAllowed());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; room.getStatus().equals(RoomStatus.AVAILABLE));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@Test
&nbsp;&nbsp;&nbsp;&nbsp;void shouldFindRoomsNeedingCleaning() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Room&gt; rooms = this.repository.findRoomsNeedingCleaning();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SoftAssertions.assertSoftly(softly -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).hasSize(2);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; !room.getCleanStatus().equals(CleanStatus.CLEAN));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;softly.assertThat(rooms).allMatch(room -&gt; !room.getStatus().equals(RoomStatus.OUT_OF_SERVICE));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;static Stream&lt;Arguments&gt; room() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Stream.of(Arguments.of(getRoom(), Arguments.of(getRoom(), Arguments.of(getRoom()))));
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;private static Room getRoom() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new RoomBuilder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.roomNumber(FAKER.number().numberBetween(100, 999))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.type(randomEnum(RoomType.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.status(randomEnum(RoomStatus.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.cleanStatus(randomEnum(CleanStatus.class))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.smokingAllowed(FAKER.bool().bool())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;private static &lt;T extends Enum&lt;?&gt;&gt; T randomEnum(Class&lt;T&gt; enumClass) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T[] constants = enumClass.getEnumConstants();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int index = ThreadLocalRandom.current().nextInt(constants.length);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return constants[index];
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Conclusion {#h2-5-conclusion}
-----------------------------

In software development, the gap between business rules and database logic is often where subtle bugs and misunderstandings live. By adopting data-driven testing, we shift the focus from checking technical details to validating actual business behavior---across a wide range of scenarios and edge cases.

In this tutorial, you learned how to apply this approach using Java SE, Jakarta Data, Eclipse JNoSQL, and MongoDB. You saw how to express queries with business semantics, isolate your tests using Testcontainers, and validate outcomes with JUnit 5 and AssertJ. More than just testing correctness, this style helps you design repositories and queries that align with the domain itself. That's the real power of data-driven testing: It turns your tests into a source of clarity, documentation, and confidence.

Ready to explore the benefits of MongoDB Atlas? Get started now by [trying MongoDB Atlas](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim).

[Access the source code](https://github.com/soujava/helidon-mongodb-introduction) used in this tutorial.

Any questions? Come chat with us in the [MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim).

**References**:

* [Source code](https://github.com/soujava/data-driven-test-mongodb)
