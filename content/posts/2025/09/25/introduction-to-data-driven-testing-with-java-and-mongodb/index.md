---
title: "Introduction to Data-Driven Testing with Java and MongoDB"
slug: "introduction-to-data-driven-testing-with-java-and-mongodb"
date: "2025-09-25T13:53:57+00:00"
lastmod: "2025-09-25T13:53:59+00:00"
description: "Data-driven testing (DDT) is important because it enables you to validate business behavior across various scenarios without having to duplicate test logic. When used in conjunction with a document database like MongoDB, it becomes a powerful tool to ensure that your queries align not only with the data structure but also with the intended business outcomes. For Java applications where persistence logic goes beyond basic CRUD operations, data-driven testing helps you safeguard what matters most: correctness, clarity, and confidence."
authors:
  - "otavio-santana"
image: "java.jpeg"
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

```
git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="12757b6652757b667a67703c717d7f">[email protected]</a>:soujava/data-driven-test-mongodb.git
```


Prerequisites {#h2-0-prerequisites}
-----------------------------------

For this tutorial, you'll need:

* Java 21.
* Maven.
* A MongoDB cluster.
  * [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim) (Option 1)
  * Docker (Option 2)

You can use the following Docker command to start a standalone MongoDB instance:

```
docker run --rm -d --name mongodb-instance -p 27017:27017 mongo
```


Data-driven testing is a technique where a single test is executed with different input data sets to validate multiple scenarios. Instead of writing repetitive test methods, you define combinations of inputs and expected outcomes, making it easier to test edge cases, business rules, and regression scenarios with clarity and efficiency.

In this age, data is the heart of any software system. Ensuring consistency and validating the code based on several data conditions will increase your quality. In this process, we can use a database to inject the data as input, to check the queries themselves, and also store the results as a report.

This approach aligns naturally with MongoDB, where application logic frequently relies on query conditions---particularly when filtering based on domain-specific rules. In a typical Java application, these queries are embedded in repositories or services and are rarely tested beyond simple "find by ID" checks. DDT allows you to test the *intent* behind queries, such as a hotel management system: Is a room considered available? Does it match VIP criteria? Should it be cleaned?

In this tutorial, we'll use aJava SE project---without any heavyweight frameworks---to demonstrate how to combine Jakarta Data, JNoSQL, and JUnit 5 to write expressive, testable queries against MongoDB. Our focus will be on clarity, maintainability, and aligning tests with the business language, not just with database fields.

Step 1: Create the entities {#h2-1-step-1-create-the-entities}
--------------------------------------------------------------

The first step is to create a plain Maven project, where we can use Maven Archetype[quick start](https://maven.apache.org/archetypes/maven-archetype-quickstart/). After making the Maven project, the next step is to include Jupiter, Mockito, AssertJ, and Testcontainers for test proposals. For the Java integration and MongoDB, we will explore it using the Java Enterprise specification, Jakarta EE, where we will utilize both specifications, Jakarta NoSQL and Jakarta Data, both of which are implemented by Eclipse JNoSQL. We don't need to spend a considerable amount of time setting it up; you can clone the GitHub repository. The pom.xml shows the dependencies using Java with the Apache Maven Project:

```
<dependencies>
        <dependency>
            <groupId>net.datafaker</groupId>
            <artifactId>datafaker</artifactId>
            <version>2.4.4</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jnosql.databases</groupId>
            <artifactId>jnosql-mongodb</artifactId>
            <version>${jnosql.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jboss.weld.se</groupId>
            <artifactId>weld-se-shaded</artifactId>
            <version>${weld.se.core.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.eclipse</groupId>
            <artifactId>yasson</artifactId>
            <version>3.0.4</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.smallrye.config</groupId>
            <artifactId>smallrye-config-core</artifactId>
            <version>3.13.3</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.eclipse.microprofile.config</groupId>
            <artifactId>microprofile-config-api</artifactId>
            <version>3.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-params</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.verson}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>${mockito.verson}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss.weld</groupId>
            <artifactId>weld-junit5</artifactId>
            <version>5.0.1.Final</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mongodb</artifactId>
            <version>1.21.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.27.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```


With the project done, the next step is setting and defining the entities. In our sample, we will explore a simple hotel management system, where we will extract some use cases to further explore the data-driven test. Naturally, a hotel management system brings way more complexity than that. Thus, we won't cover points such as payment. Therefore, we will create a Room entity and its enums that will bring the Value Object perspective.

In the src/main/java directory, create a *Room* class:

```
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.Objects;

@Entity
public class Room {

    @Id
    private String id;

    @Column
    private int number;

    @Column
    private RoomType type;

    @Column
    private RoomStatus status;

    @Column
    private CleanStatus cleanStatus;

    @Column
    private boolean smokingAllowed;

    @Column
    private boolean underMaintenance;

}

public enum RoomStatus {
    AVAILABLE,
    RESERVED,
    UNDER_MAINTENANCE,
    OUT_OF_SERVICE
}

public enum RoomType {
    STANDARD,
    DELUXE,
    SUITE,
    VIP_SUITE
}

public enum CleanStatus {
    CLEAN,
    DIRTY,
    INSPECTION_NEEDED
}
```


Explanation of annotations: {#h2-2-explanation-of-annotations}
--------------------------------------------------------------

* **@Entity**: Marks the Room class as a database entity for management by Jakarta NoSQL.
* **@Id:** Indicates the primary identifier for the entity, uniquely distinguishing each document in the MongoDB collection.
* **@Column**: Maps fields (roomNumber, type) for reading from or writing to MongoDB.

Finally, with the entity done, we will create a repository where the goal is to find rooms by type, insert a new room, and check for availability on rooms---both standard and VIP rooms:

```
@Repository
public interface RoomRepository {

    @Query("WHERE type = 'VIP_SUITE' AND status = 'AVAILABLE' AND underMaintenance = false")
    List<Room> findVipRoomsReadyForGuests();

    @Query(" WHERE type <> 'VIP_SUITE' AND status = 'AVAILABLE' AND cleanStatus = 'CLEAN'")
    List<Room> findAvailableStandardRooms();

    @Query("WHERE cleanStatus <> 'CLEAN' AND status <> 'OUT_OF_SERVICE'")
    List<Room> findRoomsNeedingCleaning();

    @Query("WHERE smokingAllowed = true AND status = 'AVAILABLE'")
    List<Room> findAvailableSmokingRooms();

    @Save
    void save(List<Room> rooms);

    @Save
    Room newRoom(Room room);
    void deleteBy();

    @Query("WHERE type = :type")
    List<Room> findByType(@Param("type") RoomType type);
}
```


We have those queries that explore Jakarta Data Queries, but are they properly working? In the next step, we will generate some tests to check it.

Step 2: Create a database container {#h2-3-step-2-create-a-database-container}
------------------------------------------------------------------------------

After the entity and repository are done, the next step is to generate the test structure. Use Testcontainer to ensure that the database is running when we use this container. We will generate an enum to implement the Singleton pattern, which will create a MongoDB database container and start a MongoDB instance for testing purposes.

Thus, at src/test/java, create the DatabaseContainer class.

```
import org.eclipse.jnosql.communication.Settings;
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

    INSTANCE;

    private final GenericContainer<?> mongodb =
            new GenericContainer<>("mongo:latest")
                    .withExposedPorts(27017)
                    .waitingFor(Wait.defaultWaitStrategy());

    {
        mongodb.start();
    }

    public MongoDBDocumentManager get(String database) {
        Settings settings = getSettings(database);
        MongoDBDocumentConfiguration configuration = new MongoDBDocumentConfiguration();
        MongoDBDocumentManagerFactory factory = configuration.apply(settings);
        return factory.apply(database);
    }

    private Settings getSettings(String database) {
        Map<String,Object> settings = new HashMap<>();
        settings.put(MongoDBDocumentConfigurations.HOST.get()+".1", host());
        settings.put(MappingConfigurations.DOCUMENT_DATABASE.get(), database);
        return Settings.of(settings);
    }

    public String host() {
        return mongodb.getHost() + ":" + mongodb.getFirstMappedPort();
    }
}
```


In the code, we will generate a MongoDB database instance by container that we have a DatabaseConfiguration.

With the database container ready, the next step is to instruct CDI to use a MongoDB container from Testcontainer during testing, rather than any production configuration. We can do this by exploring the CDI capability of alternatives.

At src//test/java, create the ManagerSupplier class.

```
import jakarta.annotation.Priority;
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
public class ManagerSupplier implements Supplier<DatabaseManager> {

    @Produces
    @Database(DatabaseType.DOCUMENT)
    @Default
    @Typed({DatabaseManager.class, MongoDBDocumentManager.class})
    public MongoDBDocumentManager get() {
        return DatabaseContainer.INSTANCE.get("hotel");
    }
}
```


On this class, we can see that we are overwriting the behavior at test where we are using the DatabaseContainer where the database is called hotel. With the structure done, the next step is playing with tests and DDT.

Step 3: Generate our first DDT {#h2-4-step-3-generate-our-first-ddt}
--------------------------------------------------------------------

One of the goals of data-driven testing is to achieve test coverage across various input combinations and capture the expected output. The first test we will generate is to verify that we are saving the information properly in the database. In this case, it does not matter which room is selected; it should insert and also generate an ID for it.

At src/test/java, create the RoomServiceTest class.

```
@EnableAutoWeld
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
    Room updateRoom = this.repository.newRoom(room);
    SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(updateRoom).isNotNull();
        softly.assertThat(updateRoom.getId()).isNotNull();
        softly.assertThat(updateRoom.getNumber()).isEqualTo(room.getNumber());
        softly.assertThat(updateRoom.getType()).isEqualTo(room.getType());
        softly.assertThat(updateRoom.getStatus()).isEqualTo(room.getStatus());
        softly.assertThat(updateRoom.getCleanStatus()).isEqualTo(room.getCleanStatus());

        softly.assertThat(updateRoom.isSmokingAllowed()).isEqualTo(room.isSmokingAllowed());
    });
}

  static Stream<Arguments> room() {
        return Stream.of(Arguments.of(getRoom(), Arguments.of(getRoom(), Arguments.of(getRoom()))));
    }

    private static Room getRoom() {
        return new RoomBuilder()
                .roomNumber(FAKER.number().numberBetween(100, 999))
                .type(randomEnum(RoomType.class))
                .status(randomEnum(RoomStatus.class))
                .cleanStatus(randomEnum(CleanStatus.class))
                .smokingAllowed(FAKER.bool().bool())
                .build();
    }

private static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
    T[] constants = enumClass.getEnumConstants();
    int index = ThreadLocalRandom.current().nextInt(constants.length);
    return constants[index];
}

}
```


At the header of RoomService, we have a couple of annotations to activate Weld, the CDI implementation, in addition to which classes and packages the CDI should scan. It facilitates and makes the test startup lighter than scanning the whole class. Here, we are using AssertJ to further explore the fluent API for checking the database. We are using soft assertions that execute the whole validations and then show which conditions have break. It is way more useful when we need to do several validations in a single method.

We will generate a new test scenario that allows us to find rooms by type. Naturally, we want to ensure that it works for any kind of search. At the same class, we will generate a method where we will inject the enum by parameter, as you can see in the code below:

```
@ParameterizedTest(name = "should find rooms by type {0}")
@EnumSource(RoomType.class)
void shouldFindRoomByType(RoomType type) {
    List<Room> rooms = this.repository.findByType(type);
    SoftAssertions.assertSoftly(softly -> softly.assertThat(rooms).allMatch(room -> room.getType().equals(type)));
}
```


We are injecting several inputs to validate the tests, and we can explore it even further to see if the tests qualify:

```
import jakarta.inject.Inject;
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

    @Inject
    private RoomRepository repository;

    private static final  Faker FAKER = new Faker();

    @BeforeEach
    void setUP() {
        Room vipRoom1 = new RoomBuilder()
                .roomNumber(101)
                .type(RoomType.VIP_SUITE)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .smokingAllowed(false)
                .build();

        Room vipRoom2 = new RoomBuilder()
                .roomNumber(102)
                .type(RoomType.VIP_SUITE)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .smokingAllowed(true)
                .build();

        Room standardRoom1 = new RoomBuilder()
                .roomNumber(201)
                .type(RoomType.STANDARD)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .smokingAllowed(false)
                .build();

        Room standardRoom2 = new RoomBuilder()
                .roomNumber(202)
                .type(RoomType.DELUXE)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .smokingAllowed(false)
                .build();

        Room dirtyReservedRoom = new RoomBuilder()
                .roomNumber(301)
                .type(RoomType.DELUXE)
                .status(RoomStatus.RESERVED)
                .cleanStatus(CleanStatus.DIRTY)
                .smokingAllowed(false)
                .build();

        Room dirtySuiteRoom = new RoomBuilder()
                .roomNumber(302)
                .type(RoomType.SUITE)
                .status(RoomStatus.UNDER_MAINTENANCE)
                .cleanStatus(CleanStatus.INSPECTION_NEEDED)
                .smokingAllowed(false)
                .build();

        Room smokingAllowedRoom = new RoomBuilder()
                .roomNumber(401)
                .type(RoomType.STANDARD)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .smokingAllowed(true)
                .build();

        repository.save(List.of(
                vipRoom1, vipRoom2,
                standardRoom1, standardRoom2,
                dirtyReservedRoom, dirtySuiteRoom,
                smokingAllowedRoom
        ));
    }

    @AfterEach
    void cleanUp() {
        repository.deleteBy();
    }

    @ParameterizedTest(name = "should find rooms by type {0}")
    @EnumSource(RoomType.class)
    void shouldFindRoomByType(RoomType type) {
        List<Room> rooms = this.repository.findByType(type);
        SoftAssertions.assertSoftly(softly -> softly.assertThat(rooms).allMatch(room -> room.getType().equals(type)));
    }

    @ParameterizedTest
    @MethodSource("room")
    void shouldSaveRoom(Room room) {
        Room updateRoom = this.repository.newRoom(room);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updateRoom).isNotNull();
            softly.assertThat(updateRoom.getId()).isNotNull();
            softly.assertThat(updateRoom.getNumber()).isEqualTo(room.getNumber());
            softly.assertThat(updateRoom.getType()).isEqualTo(room.getType());
            softly.assertThat(updateRoom.getStatus()).isEqualTo(room.getStatus());
            softly.assertThat(updateRoom.getCleanStatus()).isEqualTo(room.getCleanStatus());
            softly.assertThat(updateRoom.isSmokingAllowed()).isEqualTo(room.isSmokingAllowed());
        });
    }

    @Test
    void shouldFindRoomReadyToGuest() {
        List<Room> rooms = this.repository.findAvailableStandardRooms();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(rooms).hasSize(3);
            softly.assertThat(rooms).allMatch(room -> room.getStatus().equals(RoomStatus.AVAILABLE));
            softly.assertThat(rooms).allMatch(room -> !room.isUnderMaintenance());
        });
    }

    @Test
    void shouldFindVipRoomsReadyForGuests() {
        List<Room> rooms = this.repository.findVipRoomsReadyForGuests();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(rooms).hasSize(2);
            softly.assertThat(rooms).allMatch(room -> room.getType().equals(RoomType.VIP_SUITE));
            softly.assertThat(rooms).allMatch(room -> room.getStatus().equals(RoomStatus.AVAILABLE));
            softly.assertThat(rooms).allMatch(room -> !room.isUnderMaintenance());
        });
    }

    @Test
    void shouldFindAvailableSmokingRooms() {
        List<Room> rooms = this.repository.findAvailableSmokingRooms();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(rooms).hasSize(2);
            softly.assertThat(rooms).allMatch(room -> room.isSmokingAllowed());
            softly.assertThat(rooms).allMatch(room -> room.getStatus().equals(RoomStatus.AVAILABLE));
        });
    }

    @Test
    void shouldFindRoomsNeedingCleaning() {
        List<Room> rooms = this.repository.findRoomsNeedingCleaning();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(rooms).hasSize(2);
            softly.assertThat(rooms).allMatch(room -> !room.getCleanStatus().equals(CleanStatus.CLEAN));
            softly.assertThat(rooms).allMatch(room -> !room.getStatus().equals(RoomStatus.OUT_OF_SERVICE));
        });
    }

    static Stream<Arguments> room() {
        return Stream.of(Arguments.of(getRoom(), Arguments.of(getRoom(), Arguments.of(getRoom()))));
    }

    private static Room getRoom() {
        return new RoomBuilder()
                .roomNumber(FAKER.number().numberBetween(100, 999))
                .type(randomEnum(RoomType.class))
                .status(randomEnum(RoomStatus.class))
                .cleanStatus(randomEnum(CleanStatus.class))
                .smokingAllowed(FAKER.bool().bool())
                .build();
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        int index = ThreadLocalRandom.current().nextInt(constants.length);
        return constants[index];
    }
}
```


Conclusion {#h2-5-conclusion}
-----------------------------

In software development, the gap between business rules and database logic is often where subtle bugs and misunderstandings live. By adopting data-driven testing, we shift the focus from checking technical details to validating actual business behavior---across a wide range of scenarios and edge cases.

In this tutorial, you learned how to apply this approach using Java SE, Jakarta Data, Eclipse JNoSQL, and MongoDB. You saw how to express queries with business semantics, isolate your tests using Testcontainers, and validate outcomes with JUnit 5 and AssertJ. More than just testing correctness, this style helps you design repositories and queries that align with the domain itself. That's the real power of data-driven testing: It turns your tests into a source of clarity, documentation, and confidence.

Ready to explore the benefits of MongoDB Atlas? Get started now by [trying MongoDB Atlas](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim).

[Access the source code](https://github.com/soujava/helidon-mongodb-introduction) used in this tutorial.

Any questions? Come chat with us in the [MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data_driven_test_dev_foojay&utm_term=tony.kim).

**References**:

* [Source code](https://github.com/soujava/data-driven-test-mongodb)
