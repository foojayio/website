---
title: "Introduction to Behavior Driving Development with Java and MongoDB"
slug: "introduction-to-behavior-driving-development-with-java-and-mongodb"
date: "2026-01-27T16:35:53+00:00"
lastmod: "2026-01-27T16:35:55+00:00"
description: "When we face software development, the biggest mistake is about delivering what the client wants. It sounds like a cliché, but after decades, we are still facing this problem. One good way to solve it is to start the test focusing on what the business needs."
authors:
  - "otavio-santana"
image: "546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
  - "Spring"
tags:
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
frozen: false
---

When we face software development, the biggest mistake is about delivering what the client wants. It sounds like a cliché, but after decades, we are still facing this problem. One good way to solve it is to start the test focusing on what the business needs.

**Behavior-driven development** (BDD) is a software development methodology where the focus is on behavior and the domain terminology or ubiquitous language. It utilizes a shared, natural language to define and test software behaviors from the user's perspective. BDD builds upon test-driven development (TDD) by focusing on scenarios that are relevant to the business. These scenarios are written as plain-language specifications that can be automated as tests, simultaneously serving as living documentation.

This approach fosters a common understanding among both technical and non-technical stakeholders, ensures that the software meets user needs, and helps reduce rework and development time. In this article, we will explore more about this approach and how to use it with MongoDB and Java.

In this tutorial, you'll:

* Model a domain (Room, RoomType, RoomStatus).
* Write semantic repository queries using Jakarta Data.
* Run data-driven tests using JUnit 5 and AssertJ.
* Validate MongoDB queries in isolation using Testcontainers and Weld.

You can find all the code presented in this tutorial in the [GitHub repository](https://github.com/soujava/behavior-driven-development-mongodb):

```
git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e88f819ca88f819c809d8ac68b8785">[email protected]</a>:soujava/behavior-driven-development-mongodb.git
```

## Prerequisites

For this tutorial, you'll need:

* Java 21.
* Maven.
* A MongoDB cluster.
  * [MongoDB Atlas](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-java-behavior-foojay&utm_term=tony.kim) (Option 1)
  * Docker (Option 2)

You can use the following Docker command to start a standalone MongoDB instance:

```
docker run --rm -d --name mongodb-instance -p 27017:27017 mongo
```

In this tutorial, we'll use aJava SE project---without any heavyweight frameworks---to demonstrate how to combine Jakarta Data, JNoSQL, and JUnit 5 to write expressive, testable queries against MongoDB. Our focus will be on clarity, maintainability, and aligning tests with the business language, not just with database fields.

## Step 1: Create the project structure

The first step is generating the project using Maven. To make it easier, we have the Maven Archetype. Thus, generate the following command:

```
mvn archetype:generate                     \

"-DarchetypeGroupId=io.cucumber"           \

"-DarchetypeArtifactId=cucumber-archetype" \

"-DarchetypeVersion=7.30.0"                \

"-DgroupId=org.soujava.demos.mongodb"      \

"-DartifactId=behavior-driven-development" \

"-Dpackage=org.soujava.demos.mongodb"      \

"-Dversion=1.0.0-SNAPSHOT"                 \

"-DinteractiveMode=false"
```

The next step is to include Eclipse JNoSQL with MongoDB, the Jakarta EE components implementations: CDI, JSON, and the Eclipse Microprofile implementation.

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.soujava.demos.mongodb</groupId>
    <artifactId>behavior-driven-development</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <jnosql.version>1.1.10</jnosql.version>
        <weld.se.core.version>6.0.3.Final</weld.se.core.version>
        <mockito.verson>5.18.0</mockito.verson>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-bom</artifactId>
                <version>7.30.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.junit</groupId>
                <artifactId>junit-bom</artifactId>
                <version>5.14.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.assertj</groupId>
                <artifactId>assertj-bom</artifactId>
                <version>3.27.6</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
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
            <groupId>org.eclipse.jnosql.databases</groupId>
            <artifactId>jnosql-mongodb</artifactId>
            <version>${jnosql.version}</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-params</artifactId>
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
            <groupId>org.testcontainers</groupId>
            <artifactId>mongodb</artifactId>
            <version>1.21.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.1</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.4</version>
                <configuration>
                    <properties>
                        <!-- Work around. Surefire does not include enough
                             information to disambiguate between different
                             examples and scenarios. -->
                        <configurationParameters>
                            cucumber.junit-platform.naming-strategy=long
                        </configurationParameters>
                    </properties>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

To simplify the scope of the tutorial, we will reuse the modeling and entity from the previous post about data-driven testing with MongoDB and Java. Thus, we will use the same hotel management at the org.soujava.demos.mongodb.document package:

```
package org.soujava.demos.mongodb.document;
public enum CleanStatus {
    CLEAN,
    DIRTY,
    INSPECTION_NEEDED
}

package org.soujava.demos.mongodb.document;
public enum RoomStatus {
    AVAILABLE,
    RESERVED,
    UNDER_MAINTENANCE,
    OUT_OF_SERVICE
}

package org.soujava.demos.mongodb.document;
public enum RoomType {
    STANDARD,
    DELUXE,
    SUITE,
    VIP_SUITE
}

package org.soujava.demos.mongodb.document;
import jakarta.nosql.Column;
import jakarta.nosql.Convert;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import org.eclipse.jnosql.databases.mongodb.mapping.ObjectIdConverter;
import java.util.Objects;
@Entity
public class Room {
    @Id
    @Convert(ObjectIdConverter.class)
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
    public Room() {
    }

    public Room(String id, int number,
                RoomType type, RoomStatus status,
                CleanStatus cleanStatus,
                boolean smokingAllowed, boolean underMaintenance) {

        this.id = id;
        this.number = number;
        this.type = type;
        this.status = status;
        this.cleanStatus = cleanStatus;
        this.smokingAllowed = smokingAllowed;
        this.underMaintenance = underMaintenance;
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public CleanStatus getCleanStatus() {
        return cleanStatus;
    }

    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void update(RoomStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", roomNumber=" + number +
                ", type=" + type +
                ", status=" + status +
                ", cleanStatus=" + cleanStatus +
                ", smokingAllowed=" + smokingAllowed +
                ", underMaintenance=" + underMaintenance +
                '}';
    }

    public static RoomBuilder builder() {
        return new RoomBuilder();
    }
}

package org.soujava.demos.mongodb.document;

public class RoomBuilder {
    private String id;
    private int roomNumber;
    private RoomType type;
    private RoomStatus status;
    private CleanStatus cleanStatus;
    private boolean smokingAllowed;
    private boolean underMaintenance;

    public RoomBuilder id(String id) {
        this.id = id;
        return this;
    }

    public RoomBuilder number(int roomNumber) {
        this.roomNumber = roomNumber;
        return this;
    }

    public RoomBuilder type(RoomType type) {
        this.type = type;
        return this;
    }

    public RoomBuilder status(RoomStatus status) {
        this.status = status;
        return this;
    }

    public RoomBuilder cleanStatus(CleanStatus cleanStatus) {
        this.cleanStatus = cleanStatus;
        return this;
    }

    public RoomBuilder smokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
        return this;
    }

    public RoomBuilder underMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
        return this;
    }

    public Room build() {
        return new Room(id, roomNumber, type, status, cleanStatus, smokingAllowed, underMaintenance);
    }
}
```

The next step is to create an interface of communication between MongoDB and Java. We will simplify our lives using Jakarta Data. Thus, we will have a single interface, where we will connect to MongoDB as a repository interface, and the Jakarta provider will handle the implementation.

```
package org.soujava.demos.mongodb.document;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository {
    @Query("FROM Room")
    List<Room> findAll();
    @Save
    Room save(Room room);
    void deleteBy();
    Optional<Room> findByNumber(Integer number);
}
```

We will enable CDI and the proper files, thus generating the bean.xml and the configuration properties file at the src/main/resources/META-INF.

beans.xml

```
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"

       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee

http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"

       bean-discovery-mode="all">

</beans>
```

microprofile-config.properties

```
jnosql.mongodb.url=mongodb://localhost:27017

# mandatory define the database name

jnosql.document.database=hotels
```

Exploring the methodology, we should start with the behavior and then do the implementation. Therefore, at the test, we will generate our first feature file at the resource. We will create a *room.feature* at the src/test/resources/org/soujava/demos/mongodb folder.

```
Feature: Manage hotel rooms

  Scenario: Register a new room
    Given the hotel management system is operational
    When I register a room with number 203
    Then the room with number 203 should appear in the room list

  Scenario: Register multiple rooms
    Given the hotel management system is operational
    When I register the following rooms:
      | number | type      | status             | cleanStatus |
      | 101    | STANDARD  | AVAILABLE          | CLEAN       |
      | 102    | SUITE     | RESERVED           | DIRTY       |
      | 103    | VIP_SUITE | UNDER_MAINTENANCE  | CLEAN       |
    Then there should be 3 rooms available in the system

  Scenario: Change room status
    Given the hotel management system is operational
    And a room with number 101 is registered as AVAILABLE
    When I mark the room 101 as OUT_OF_SERVICE
    Then the room 101 should be marked as OUT_OF_SERVICE
```

## Step 2: Create the test infrastructure

As we will need to generate a MongoDB instance for the test, we will use a container and run the test on it. We will create a DatabaseContainer as a singlethon instance. At the src/test/java/org/soujava/demos/mongodb/config, make the class DatabaseContainer.

```
package org.soujava.demos.mongodb.config;

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

The next step is making this database available to the CDI container. We will create a ManagerSupplier that teaches the CDI how to generate a MongoDB instance. In this case, we will use the properties from the MongoDB test container.

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

Cucumber has the feature to allow injection using an ObjectFactory. Once we are using CDI, we will generate an implementation to create those classes using CDI. In this case, at the src/test/java/org/soujava/demos/mongodb/config, generate the WeldCucumberObjectFactory class.

```
package org.soujava.demos.mongodb.config;

import io.cucumber.core.backend.ObjectFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class WeldCucumberObjectFactory implements ObjectFactory {
    private Weld weld;
    private WeldContainer container;
    @Override
    public void start() {
        weld = new Weld();
        container = weld.initialize();
    }

    @Override
    public void stop() {
        if (weld != null) {
            weld.shutdown();
        }
    }

    @Override
    public boolean addClass(Class<?> stepClass) {
        return true; // accept all step classes
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return (T) container.select(type).get();
    }
}
```

SPI loads this class, so we need to register our new class to be executed by Cucumber. Create the src/test/resources/META-INF/services and put the io.cucumber.core.backend.ObjectFactory file.

```
org.soujava.demos.mongodb.config.WeldCucumberObjectFactory
```

Also, at the src/test/resources/META-INF, generate the beans.xml file:

```
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
       http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
       bean-discovery-mode="annotated>
</beans>
```

The class on configuration is the class that will convert the table into the Room entities, where at the src/test/java/org/soujava/demos/mongodb, we will create the RoomDataTableMapper class:

```
package org.soujava.demos.mongodb;

import io.cucumber.java.DataTableType;
import jakarta.enterprise.context.ApplicationScoped;
import org.soujava.demos.mongodb.document.CleanStatus;
import org.soujava.demos.mongodb.document.Room;
import org.soujava.demos.mongodb.document.RoomStatus;
import org.soujava.demos.mongodb.document.RoomType;
import java.util.Map;

@ApplicationScoped
public class RoomDataTableMapper {
    @DataTableType
    public Room roomEntry(Map<String, String> entry) {
        return Room.builder()
                .number(Integer.parseInt(entry.get("number")))
                .type(RoomType.valueOf(entry.get("type")))
                .status(RoomStatus.valueOf(entry.get("status")))
                .cleanStatus(CleanStatus.valueOf(entry.get("cleanStatus")))
                .build();
    }
}
```

## Step 3: Generate our first scenario test

The code infrastructure is ready, where we set the ObjectFactory using Weld, and the table mapper to convert the table into our entities. The next step is the test generation itself. As it's necessary to highlight in the BDD methodology, we start with the test. Then we start the implementation, but once the focus is more on showing the tool with MongoDB than the methodology itself, we finalize this tutorial with what should be the first step. We will create our last class in this tutorial: the HotelRoomSteps.

```
package org.soujava.demos.mongodb;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.assertj.core.api.Assertions;
import org.soujava.demos.mongodb.document.*;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HotelRoomSteps {

    @Inject
    private RoomRepository repository;

    @Before
    public void cleanDatabase() {
        repository.deleteBy();
    }

    @Given("the hotel management system is operational")
    public void theHotelManagementSystemIsOperational() {
        Assertions.assertThat(repository).as("RoomRepository should be initialized").isNotNull();
    }

    @When("I register a room with number {int}")
    public void iRegisterARoomWithNumber(Integer number) {
        Room room = Room.builder()
                .number(number)
                .type(RoomType.STANDARD)
                .status(RoomStatus.AVAILABLE)
                .cleanStatus(CleanStatus.CLEAN)
                .build();
        repository.save(room);
    }

    @Then("the room with number {int} should appear in the room list")
    public void theRoomWithNumberShouldAppearInTheRoomList(Integer number) {
        List<Room> rooms = repository.findAll();
        Assertions.assertThat(rooms)
                .extracting(Room::getNumber)
                .contains(number);
    }

    @When("I register the following rooms:")
    public void iRegisterTheFollowingRooms(List<Room> rooms) {
        rooms.forEach(repository::save);
    }

    @Then("there should be {int} rooms available in the system")
    public void thereShouldBeRoomsAvailableInTheSystem(int expectedCount) {
        List<Room> rooms = repository.findAll();
        Assertions.assertThat(rooms).hasSize(expectedCount);
    }

    @Given("a room with number {int} is registered as {word}")
    public void aRoomWithNumberIsRegisteredAs(Integer number, String statusName) {
        RoomStatus status = RoomStatus.valueOf(statusName);
        Room room = Room.builder()
                .number(number)
                .type(RoomType.STANDARD)
                .status(status)
                .cleanStatus(CleanStatus.CLEAN)
                .build();
        repository.save(room);
    }

    @When("I mark the room {int} as {word}")
    public void iMarkTheRoomAs(Integer number, String newStatusName) {
        RoomStatus newStatus = RoomStatus.valueOf(newStatusName);
        Optional<Room> roomOpt = repository.findByNumber(number);
        Assertions.assertThat(roomOpt)
                .as("Room %s should exist", number)
                .isPresent();
        Room updatedRoom = roomOpt.orElseThrow();
        updatedRoom.update(newStatus);
        repository.save(updatedRoom);
    }

    @Then("the room {int} should be marked as {word}")
    public void theRoomShouldBeMarkedAs(Integer number, String expectedStatusName) {
        RoomStatus expectedStatus = RoomStatus.valueOf(expectedStatusName);
        Optional<Room> roomOpt = repository.findByNumber(number);
        Assertions.assertThat(roomOpt)
                .as("Room %s should exist", number)
                .isPresent()
                .get()
                .extracting(Room::getStatus)
                .isEqualTo(expectedStatus);
    }
}
```

## Conclusion

Behavior-driven development (BDD) encourages us to look beyond code and concentrate on a shared understanding among stakeholders. By integrating Jakarta Data, Eclipse JNoSQL, and Cucumber, we have learned how to articulate business expectations through executable scenarios. These scenarios are written in plain language and linked to actual database operations. This approach not only guarantees technical accuracy but also fosters alignment among developers, testers, and domain experts. Furthermore, it links more with another methodology that I enjoyed that is about[domain driven design](https://bpbonline.com/products/domain-driven-design-with-java), which I've written a new book about.

In this tutorial, you discovered how to model a hotel domain, store and query data in MongoDB, and connect behavior specifications with concrete database assertions---all without relying on complex frameworks. The outcome is a clean, testable, and business-oriented foundation for your application.

BDD reminds us that software development is not merely about meeting requirements; it is about clearly communicating intent. When business language is incorporated into our code and tests, we move closer to the ultimate goal of software engineering: creating systems that operate exactly as users expect.

Ready to explore the benefits of MongoDB Atlas? Get started now by [trying MongoDB Atlas](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-java-behavior-foojay&utm_term=tony.kim).

[Access the source code](https://github.com/soujava/behavior-driven-development-mongodb) used in this tutorial.

Any questions? Come chat with us in the [MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-java-behavior-foojay&utm_term=tony.kim).

**References**:

* [Source code](https://github.com/soujava/behavior-driven-development-mongodb)
