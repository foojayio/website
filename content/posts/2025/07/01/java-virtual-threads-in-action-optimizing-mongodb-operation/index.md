---
title: "Java Virtual Threads in Action: Optimizing MongoDB Operation"
slug: "java-virtual-threads-in-action-optimizing-mongodb-operation"
date: "2025-07-01T08:12:24+00:00"
lastmod: "2025-07-01T08:12:25+00:00"
description: "Java 21's virtual threads simplify handling I/O-bound operations, allowing for massive concurrency with minimal overhead."
canonical: "https://dev.to/mongodb/java-virtual-threads-in-action-optimizing-mongodb-operation-4l5k"
authors:
  - "otavio-santana"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
  - "java-concurrency-best-practices-for-mongodb"
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
enlighterjs: true
frozen: false
---

Virtual threads have become one of the most popular resources in Java and are trending inside the language. Indeed, this resource introduced a cheap way to create threads inside the JVM. In this tutorial, we will explain how to use it with MongoDB.

You can find all the code presented in this tutorial in the [GitHub repository](https://github.com/soujava/mongodb-virtual-threads):

```
git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fe99978abe99978a968b9cd09d9193">[email protected]</a>:soujava/mongodb-virtual-threads.git
```


Prerequisites
-------------

For this tutorial, you'll need:

* Java 21.
* Maven.
* A MongoDB cluster.
  * [MongoDB Atlas](https://www.mongodb.com/developer/languages/java/quarkus-eclipse-jnosql/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java_virtual_threads_mongodb&utm_term=otavio.santana) (Option 1)
  * Docker (Option 2)
* A [Quarkus project](https://www.mongodb.com/developer/languages/java/quarkus-eclipse-jnosql/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java_virtual_threads_mongodb&utm_term=otavio.santana) with MongoDB integrated.

You can use the following Docker command to start a standalone MongoDB instance:

```
docker run --rm -d --name mongodb-instance -p 27017:27017 mongo
```


Java 21 has introduced a new era of concurrency with virtual threads---lightweight threads managed by the JVM that significantly enhance the performance of I/O-bound applications. Unlike traditional platform threads, which are directly linked to operating system threads, virtual threads are designed to be inexpensive and can number in the thousands. This enables you to manage many concurrent operations without the typical overhead of traditional threading.

Virtual threads are scheduled on a small pool of carrier threads, ensuring that blocking operations---such as those commonly encountered when interacting with databases---do not waste valuable system resources.

In this tutorial, we will generate a Quarkus project that leverages Java 21's virtual threads to build a highly responsive, non-blocking application integrated with MongoDB via Eclipse JNoSQL. The focus is on exploring the benefits of virtual threads in managing I/O-bound workloads and illustrating how modern Java concurrency can transform database interactions by reducing latency and improving scalability.  

As a first step, follow the guide, [MongoDB Developer: Quarkus \& Eclipse JNoSQL](https://www.mongodb.com/developer/languages/java/quarkus-eclipse-jnosql/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java_virtual_threads_mongodb&utm_term=otavio.santana). This will help you set up the foundation of your Quarkus project. After that, you'll integrate Java 21 features to fully exploit the power of virtual threads in your MongoDB-based application.

During the creation process, ensure you generate the project's latest version on both Quarkus and Eclipse JNoSQL. Make sure that you have a version higher than:

```
<dependency>
   <groupId>io.quarkiverse.jnosql</groupId>
   <artifactId>quarkus-jnosql-document-mongodb</artifactId>
   <version>3.3.4</version>
</dependency>
```


In this tutorial, we will generate services to handle cameras. We will create cameras based on [Datafaker](https://www.datafaker.net/) and return all the cameras using virtual threads with Quarkus. In your project, locate the application.properties file (usually under src/main/resources) and add the following line:

```
# Define the database name
jnosql.document.database=cameras
```


With this foundation, we'll move on to implementing the product entity and explore how MongoDB's embedded types can simplify data modeling for your application.

Step 1: Create the Product entity
---------------------------------

To start, we'll define the core of our application: the Camera entity. This class represents the camera data structure and contains fields such as id, brand, model, and brandWithModel. We will have a static factory method where, based on the Faker instance, it will generate a Camera instance.

In the src/main/java directory, create a Camera class:

```
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
public record Camera(
        @Id @Convert(ObjectIdConverter.class) String id,
        @Column String brand,
        @Column String model,
        @Column String brandWithModel
) {

    public static Camera of(Faker faker) {
        var camera = faker.camera();
        String brand = camera.brand();
        String model = camera.model();
        String brandWithModel = camera.brandWithModel();
        return new Camera(UUID.randomUUID().toString(), brand, model, brandWithModel);
    }

    public Camera update(Camera request) {
        return new Camera(this.id, request.brand, request.model, request.brandWithModel);
    }
}
```


Explanation of annotations:

* `@Entity`: Marks the Product class as a database entity for management by Jakarta NoSQL.
* `@Column`: Maps fields (name, manufacturer, tags, categories) for reading from or writing to MongoDB.

Step 2: Create the Service
--------------------------

In our application, the CameraService class serves as a bridge between our business logic and MongoDB. We utilize Eclipse JNoSQL, which supports Jakarta NoSQL and Jakarta Data. In this tutorial, we work with the DocumentTemplate interface from Jakarta NoSQL---a specialized version of the generic Template interface tailored for NoSQL document capabilities. The Quarkus integration makes it easier once you, as a Java developer, need to use an injection annotation.

Furthermore, we inject an ExecutorService that is qualified with the @VirtualThreads annotation. This annotation instructs Quarkus to provide an executor that employs Java 21's virtual threads for improved concurrency.

Define a CameraService to interact with MongoDB:

```
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@ApplicationScoped
public class CameraService {

    private static final Logger LOGGER = Logger.getLogger(CameraService.class.getName());

    private static final Faker FAKER = new Faker();

    @Inject
    DocumentTemplate template;

    @Inject
    @VirtualThreads
    ExecutorService vThreads;

    public List<Camera> findAll() {
        LOGGER.info("Selecting all cameras");
        return template.select(Camera.class).result();
    }

    public List<Camera> findByBrand(String brand) {
        LOGGER.info("Selecting cameras by brand: " + brand);
        return template.select(Camera.class)
                .where("brand")
                .like(brand)
                .result();
    }

    public Optional<Camera> findById(String id) {
        var camera =  template.find(Camera.class, id);
        LOGGER.info("Selecting camera by id: " + id + " find? " + camera.isPresent());
        return camera;
    }

    public void deleteById(String id) {
        LOGGER.info("Deleting camera by id: " + id);
        template.delete(Camera.class, id);
    }

    public Camera insert(Camera camera) {
        LOGGER.info("Inserting camera: " + camera.id());
        return template.insert(camera);
    }

    public Camera update(Camera update) {
        LOGGER.info("Updating camera: " + update.id());
        return template.update(update);
    }

    public void insertAsync(int size) {
        LOGGER.info("Inserting cameras async the size: " + size);

        for (int index = 0; index < size; index++) {
            vThreads.submit(() -> {
                Camera camera = Camera.of(FAKER);
                template.insert(camera);
            });
        }
    }
}
```


In this code:

* The DocumentTemplate provides the necessary operations (CRUD) to interact with MongoDB.
* The vThreads ExecutorService, annotated with @VirtualThreads, submits tasks that insert fake camera records asynchronously. This is a prime example of how virtual threads can be leveraged for I/O-bound operations without manual thread management.

This setup shows how Quarkus and Eclipse JNoSQL simplify the development process: You get the benefits of Jakarta NoSQL for MongoDB without the boilerplate, and virtual threads allow you to write scalable, concurrent applications in a natural, synchronous style.

For more details on using virtual threads in Quarkus, check out the [Quarkus Virtual Threads Guide](https://quarkus.io/guides/virtual-threads).

Step 3: Expose the Camera API
-----------------------------

We'll create the CameraResource class to expose our data through a RESTful API. This resource handles HTTP requests. We will generate a camera either manually or using the asynchronous resource. You can define the size of the cameras generated, with the default being 100.

Create a CameraResource class to handle HTTP requests:

```
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("cameras")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CameraResource {

    @Inject
    CameraService service;

    @GET
    @VirtualThreads
    public List<Camera> findAll() {
        return service.findAll();
    }

    @GET
    @Path("brand/{brand}")
    public List<Camera> listAll(@PathParam("brand") String brand) {
        if (brand == null || brand.isBlank()) {
            return service.findAll();
        }
        return service.findByBrand(brand);
    }

    @POST
    public Camera add(Camera camera) {
        return service.insert(camera);
    }

    @Path("{id}")
    @GET
    public Camera get(@PathParam("id") String id) {
        return service.findById(id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @Path("{id}")
    @PUT
    public Camera update(@PathParam("id") String id, Camera request) {
        var camera = service.findById(id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
        return service.update(camera.update(request));

    }

    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") String id) {
        service.deleteById(id);
    }

    @POST
    @Path("async")
    public Response insertCamerasAsync(@QueryParam("size") @DefaultValue("100") int size) {
        service.insertAsync(size);
        return Response.accepted("Insertion of " + size + " cameras initiated.").build();
    }

}
```


Step 4: Build and run the application
-------------------------------------

It's finally time to integrate everything and run the application. After packaging the project with Maven, start the application and ensure that MongoDB runs locally or through MongoDB Atlas. Once the application runs, you can test the API endpoints to interact with the camera data.

Make sure MongoDB is running (locally or on MongoDB Atlas). Then, build and run the application:

```
mvn clean package -Dquarkus.package.type=uber-jar
java -jar target/mongodb-virtual-thread-1.0.1-runner.jar
```


Step 5: Test the API
--------------------

Create a Camera

```
curl -X POST -H "Content-Type: application/json" -d '{
  "brand": "Canon",
  "model": "EOS 5D Mark IV",
  "brandWithModel": "Canon EOS 5D Mark IV"
}' http://localhost:8080/cameras
```


Get all Cameras

```
curl -X GET http://localhost:8080/cameras
```


Get Cameras by Brand

```
curl -X GET http://localhost:8080/cameras/brand/Canon
```


Get a Camera by ID

```
curl -X GET http://localhost:8080/cameras/{id}
```


Update a Camera by ID

```
curl -X PUT -H "Content-Type: application/json" -d '{
  "brand": "Nikon",
  "model": "D850",
  "brandWithModel": "Nikon D850"
}' http://localhost:8080/cameras/{id}
```


Delete a Camera by ID

```
curl -X DELETE http://localhost:8080/cameras/{id}
```


Insert Cameras asynchronously  

This endpoint triggers the asynchronous insertion of fake camera records. The size parameter defaults to 100 if you omit it.

```
curl -X POST http://localhost:8080/cameras/async?size=100
```


Conclusion
----------

Java 21's virtual threads simplify handling I/O-bound operations, allowing for massive concurrency with minimal overhead. By integrating virtual threads with MongoDB and Quarkus while maintaining a clean, synchronous programming model, we built a scalable and responsive application.

Ready to explore the benefits of MongoDB Atlas? Get started now by [trying MongoDB Atlas](https://www.mongodb.com/cloud/atlas/lp/try4?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java_virtual_threads_mongodb&utm_term=otavio.santana).

[Access the source code](https://github.com/soujava/mongodb-virtual-threads) used in this tutorial.

Any questions? Come chat with us in the [MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java_virtual_threads_mongodb&utm_term=otavio.santana).
