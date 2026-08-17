---
title: "Spring Data Neo4j: How to update an entity"
slug: "spring-data-neo4j-how-to-update-an-entity"
date: "2025-02-05T18:58:12+00:00"
lastmod: "2025-02-05T18:58:14+00:00"
description: "After working on a new online Spring Data Neo4j course, I learned a couple more things about updating an entity."
authors:
  - "jennifer-reif"
image: "unsplash-sdn-update-entity-scaled-e1737583612770.jpg"
categories:
  - "Databases"
  - "Graph"
  - "Java"
  - "Neo4J"
  - "Spring"
tags:
related_posts:
  - "building-a-fullstack-imdb-clone-with-a-java-backend-using-sparkjava-and-neo4j"
  - "how-to-run-neo4j-on-kubernetes"
  - "journeys-in-java-level-6-build-a-neo4j-microservice"
  - "visualization-of-the-message-flow-between-business-functions-with-vaadin-and-neo4j"
enlighterjs: true
frozen: false
---

**After working on a new [online Spring Data Neo4j course](https://graphacademy.neo4j.com/courses/app-spring-data/), I learned a couple more things about updating an entity.**

The course required a different set of scenarios than outlined in my [previous SDN update blog post](https://jmhreif.com/blog/2023/sdn-cypher-update-entity/), so I wanted to cover those scenarios, as well.

Spring save() method {#_spring_save_method}
-------------------------------------------

First up is the out-of-the-box `save()` method that is provided by Spring as a default. This method takes an input of the entity object you want to save to the database.

Here is an example of what this looks like with a popular movie data set as the model.

Movie domain class:

```java
@Node
public class Movie {
    @Id
    private String movieId;

    private String title;
    private String plot;
    private String poster;
    private String url;
    private String released;
    private Long imdbVotes;

    @Relationship(value = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List actors;
}
```


Movie controller class:

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    //inject repository + constructor

    @PostMapping("/save")
    Movie save(@RequestBody Movie movie) {
        return movieRepo.save(movie);
    }
}
```


This works well to save a new movie, as you can provide a subset of properties or all properties, as long as the `id` is present, and anything you don't provide will be set to `null`. However, the issue arises when you need to update an existing entity because it means any attributes you do not provide will be *overwritten* to `null`.

To better understand how this works, let's see it in action by saving a movie, and then trying to update it.

Movie request object:

```json
{
  "movieId": "9876",
  "title": "MyMovie"
}
```


Save Movie object:

```bash
% http ":8080/movies/save" @src/main/resources/movie.json
{
    "actors": null,
    "imdbId": null,
    "imdbVotes": null,
    "movieId": "9876",
    "plot": null,
    "poster": null,
    "title": "MyMovie",
    "url": null,
    "year": null
}
```


Now let's use the same method to try to update the movie with a `year` property.

MovieUpdated object:

```json
{
  "movieId": "9876",
  "year": 2018
}
```


Save updated Movie object:

```bash
% http ":8080/movies/save" @src/main/resources/movieUpdated.json
{
    "actors": null,
    "imdbId": null,
    "imdbVotes": null,
    "movieId": "9876",
    "plot": null,
    "poster": null,
    "title": null,
    "url": null,
    "year": 2018
}
```


In the output above, you can see that the `title` property is `null`, and the `year` property is populated. This is because the title is not specified in the updated JSON object, so it is overwritten to `null`.

This may not be too big of an effort if you have only a few attributes in your domain class, although I would still find it frustrating to include all properties for only updating one or two properties. In that case, I would need to resort to the [POJO method outlined in my previous SDN update blog post](https://jmhreif.com/blog/2023/sdn-cypher-update-entity/) where you pull the database entity, set each property, and then save the entity. The added business logic seems like an unnecessary maintenance headache.

What if you wanted to preserve what was already there without providing all the properties defined in the domain class to the request? In these scenarios, there are a couple of other options at your disposal, though neither allow dynamic updates to random properties per request.

Patch Year {#_patch_year}
-------------------------

The first option is that you don't have to set all properties if you use a PATCH method and only set the values you want to update. Here is an example.

Movie controller class:

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    //inject repository + constructor

    @PatchMapping("/patchYear")
    Movie patchYear(@RequestBody Movie moviePatch) {
        Movie existingMovie = movieRepository.findById(moviePatch.getMovieId()).get();
        existingMovie.setYear(moviePatch.getYear());

        return movieRepository.save(existingMovie);
    }
}
```


PatchYear object:

```json
{
  "movieId": "9876",
  "year": 2024
}
```


Patch movie year:

```bash
% http PATCH ":8080/movies/patchYear" @src/main/resources/moviePatch.json

{
    "budget": null,
    "countries": null,
    "imdbId": null,
    "imdbRating": null,
    "imdbVotes": null,
    "languages": null,
    "movieId": "9876",
    "plot": null,
    "poster": null,
    "released": null,
    "revenue": null,
    "runtime": null,
    "title": "MyMovie",
    "tmdbId": null,
    "url": null,
    "year": 2024
}
```


This allows you to set specific values without overwriting other property values to `null`. You also don't need to set all the values in the movie object programmatically. If you modified the initial `save()` method to just include the `setYear()` line, it would still overwrite other values. This approach prevents that, although you still have to call `setProperty()` for each field you want to update.

**Note:** For this approach to work, your domain entity must be a class (not a record) because records are immutable, which means you cannot change (or set/update) fields on the entity instance. For immutable objects, you have to create a new instance of the object and copy property values to the new object before saving.

You can avoid setting each property on an object and retain existing values with a couple of options covered next.

Custom Cypher {#_custom_cypher}
-------------------------------

One of the more flexible options is to use [custom Cypher](https://docs.spring.io/spring-data/neo4j/reference/appendix/custom-queries.html). For this, you would write a Cypher statement that sets the new values to the properties you want to update. You can even add/set properties that do not exist on the application's domain class. The negative is that you would need to make changes to the application (Cypher statement) if you wanted to update different properties, so it is not fully dynamic.

The example below uses the same movie domain but adds a Cypher statement and method to the repository interface to update the `year` property without overwriting the `title`.

Repository interface:

```java
interface MovieRepository extends Neo4jRepository {
    @Query("MATCH (m:Movie {movieId: $movieId}) " +
            "SET m.year = toInteger($year) " +
            "RETURN m;")
    Movie updateYear(String movieId, Long year);
}
```


Movie controller class:

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    //inject repository + constructor

    @PatchMapping("/updateYear")
    Movie patchYear(@RequestParam String movieId, @RequestParam Long year) {
        return movieRepository.updateYear(movieId, year);
    }
}
```


Then, the following request calls the method and updates the movie's year property.

Update movie year:

```bash
% http PATCH ":8080/movies/updateYear?movieId=9876&year=2018"

{
    "actors": [],
    "imdbId": null,
    "imdbVotes": null,
    "movieId": "9876",
    "plot": null,
    "poster": null,
    "title": "MyMovie",
    "url": null,
    "year": 2018
}
```


It worked! The movie's title remained the same (not overwritten to `null`), and a value was saved for the `year` property.

This ad hoc Cypher approach could work well when values or property updates occur somewhat frequently, as updating the Cypher statement makes updates flexible. You could also make the incoming property generic (`value`) and pass in any value (or multiple values) and set whichever properties you'd like by changing the Cypher. While still not completely dynamic, this option is probably the most flexible and dynamic of the list.

A custom Cypher approach might work well when you need to update certain properties, but if you have a subset of properties that operate together, another option is to create a projection of the domain class.

Projections {#_projections}
---------------------------

To provide a consistent set of values for update and leave other properties as-is, [projections](https://docs.spring.io/spring-data/neo4j/reference/projections/sdn-projections.html) are probably the nicest option I've found so far. This approach still requires setting consistent properties (like with custom Cypher), but avoids overwriting consistent values by creating a "view" of the larger entity, only working with those values and leaving other field values alone.

**Note:** There are two types of projections - interface and DTO. Interface projections are immutable, which means you cannot update values, but have to create a new object and copy existing values over. DTO objects, then, are more straightforward when dealing with update operations. For this reason, the examples use DTO-based projections.

There are two different ways to [save a projection](https://docs.spring.io/spring-data/neo4j/reference/projections/sdn-projections.html#projections.sdn.persistence) - 1. send the projection (subset of properties) and save into the full domain entity, 2. send a full domain object but only save the projection fields. Really, the difference is the incoming request object, whether you have a smaller set or larger set and only want to save those values.

Let's see how this operates.

### Projection as Movie {#_projection_as_movie}

The first example sends a projection object (subset of the full domain object's properties) and saves the trimmed object as the full movie entity. We have defined a projection that only includes the `movieId` and `plot` properties of a movie.

MovieDTOProjection class:

```java
public class MovieDTOProjection {
    private String movieId;
    private String plot;

    public String getMovieId() { return movieId; }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }
}
```


MovieController class method:

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    //inject repository + constructor

    //Only updates properties in projection
    @PatchMapping("/projectionAsMovie")
    MovieDTOProjection saveProjectionAsMovie(@RequestBody MovieDTOProjection movieDTO) {
        return neo4jTemplate.save(Movie.class).one(movieDTO);
    }
}
```


ProjectionAsMovie object (request object):

```json
{
  "movieId": "9876",
  "plot": "Here is the plot."
}
```


Update Movie with a projection object:

```bash
% http PATCH ":8080/movies/projectionAsMovie" @src/main/resources/projectionAsMovie.json

{
    "movieId": "9876",
    "plot": "Here is the plot."
}
```


Full database entity:

```json
{
    "plot": "Here is the plot.",
    "year": 2024,
    "movieId": "9876",
    "title": "MyMovie"
}
```


The request successfully updated the entity with the new plot value ("Here is the plot.") without overwriting the title or year properties on the existing entity! The method in the controller class takes the projection object input and saves it as a `Movie` class entity.

This approach is helpful when you only want to send certain properties over the wire and not the full object. You can set any fields you wish to update in the projection and only send those values in the request.

### Movie entity as projection {#_movie_entity_as_projection}

This type of projection only saves a defined subset of properties out of a potentially larger or variable object. The save will only update the fields included in the projection and ignore anything else that might be in the request object.

The example below uses the same [`MovieDTOProjection` class](https://github.com/JMHReif/sdn-update-entity-round2/blob/main/src/main/java/com/jmhreif/sdn_update_entity_round2/MovieDTOProjection.java) as the above example. Then, we only need a new method in the controller to save the projection.

MovieController class method:

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    //inject repository + constructor

    //Only updates properties in projection (ignores other values)
    @PatchMapping("/movieAsProjection")
    MovieDTOProjection saveMovieAsProjection(@RequestBody Movie movie) {
        return neo4jTemplate.saveAs(movie, MovieDTOProjection.class);
    }
}
```


MovieAsProjection object:

```json
{
  "movieId": "9876",
  "title": "TestTitle",
  "plot": "Some plot cliche here.",
  "year": 2025
}
```


Send Movie object (only save projection values):

```bash
% http PATCH ":8080/movies/movieAsProjection" @src/main/resources/movieAsProjection.json

{
    "movieId": "9876",
    "plot": "Some plot cliche here."
}
```


Full database entity:

```json
{
    "plot": "Some plot cliche here.",
    "year": 2024,
    "movieId": "9876",
    "title": "MyMovie"
}
```


This also worked! The controller method accepts a `Movie` request object as input and saves it as the projection entity, which only retains the subset of values defined in the projection and ignores the rest.

This approach would be helpful if you have request objects with varying fields of a `Movie` entity, but only want to update a certain subset each time. In terms of the incoming request object, this option seems to be the most flexible in allowing you to provide all kinds of fields as input, and it will only save the relevant ones defined in the projection.

Wrapping Up! {#_wrapping_up}
----------------------------

In this post, we delved a bit deeper into updating entities using Spring Data and Neo4j. A [previous blog post on the topic](https://jmhreif.com/blog/2023/sdn-cypher-update-entity/) outlined some examples for that use case at the time (microservices), but I learned about a few more options after working on the [GraphAcademy Spring Data Neo4j course](https://graphacademy.neo4j.com/courses/app-spring-data/). These options included custom Cypher statements and projections.

There are still other pieces of the puzzle to explore and integrate, such as optimistic locking (with the `@Version` field) and custom repository implementations for extending repositories (e.g. to combine `Neo4jRepository`, `Neo4jTemplate`, and/or `Neo4jClient` levels of abstraction), but we will save those for a future blog post. Happy coding!

Resources {#_resources}
-----------------------

* Code: [sdn-update-entity-round2](https://github.com/JMHReif/sdn-update-entity-round2)
* Blog post: [previous SDN update article](https://jmhreif.com/blog/2023/sdn-cypher-update-entity/)
* Graphacademy course (free, online, self-paced): [Spring Data Neo4j](https://graphacademy.neo4j.com/courses/app-spring-data/)
* Documentation: [SDN projection persistence](https://docs.spring.io/spring-data/neo4j/reference/projections/sdn-projections.html#projections.sdn.persistence)
* Documentation: [SDN custom repository implementations](https://docs.spring.io/spring-data/neo4j/reference/repositories/custom-implementations.html)
