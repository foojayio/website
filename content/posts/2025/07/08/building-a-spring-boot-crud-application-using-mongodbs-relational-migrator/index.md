---
title: "Building a Spring Boot CRUD Application Using MongoDB’s Relational Migrator"
date: "2025-07-08T19:05:43+00:00"
lastmod: "2025-07-08T19:08:25+00:00"
description: "Learn how transitioning from traditional relational databases to MongoDB’s dynamic and schema-less architecture can be overwhelming, but MongoDB’s Relational Migrator simplifies the process."
canonical: "https://dev.to/mongodb/building-a-spring-boot-crud-application-using-mongodbs-relational-migrator-59kf"
authors:
  - "aasawari-sahasrabuddhe"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
related_posts:
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "data-modeling-for-java-developers-structuring-with-postgresql-and-mongodb"
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
frozen: false
---

Imagine this: You're working with a relational database that's served you well for years, but now your applications demand flexibility, scalability, and faster development cycles. Transitioning from structured tables to MongoDB's dynamic, schema-less JSON documents might be daunting. That's where MongoDB's Relational Migrator becomes your secret weapon.

The Relational Migrator tool built by MongoDB no longer only supports migrating data from relational databases to MongoDB. Rather, it supports features like code generation and also a query converter that converts a relational database, such as a Postgres query, or queries from views and stored procedures, into a MongoDB query.

Relational Migrator is a free tool that leverages intelligent algorithms and Gen AI to solve the most common data modeling, code conversion, and migration challenges, reducing the effort and risk involved in migration projects.

In this tutorial, we'll explore how to harness the power of Spring Boot to build a fully functional CRUD application on top of your modernized database using the Relational Migrator.

This guide will help modernize a legacy application built with relational databases into a complete application using MongoDB.

Let's get started!

## Pre-requisites

Before we get into the actual implementation to build the application, below are the pre-requisites that you need to have:

1. Relational Migrator---you can download the tool available for free using the [MongoDB Tools](https://www.mongodb.com/try/download/relational-migrator?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe) download page
2. The [SQL schema script](https://github.com/mongodb-developer/relational-migrator-lab/blob/main/docker/sample-postgres-library/init/1-library-schema-and-data.sql) to create the Postgres database and tables
3. [Java 21](https://www.oracle.com/in/java/technologies/downloads/) or above
4. The IDE of your choice
5. A free Atlas cluster---create your first[Atlas cluster for free](https://account.mongodb.com/account/register?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe)

## Relational Migrator

The [Relational Migrator](https://www.mongodb.com/docs/relational-migrator/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe) is a tool developed by MongoDB that helps you migrate data from a relational database into MongoDB. It is an application with a UI that helps you map the data from the relational schema to the MongoDB schema.

Along with providing migration capability, the tool offers more than the name suggests.

1. **Generate the application code**: After the data has been migrated into the Atlas cluster, the developer asks how we begin the text. Well! The code generation feature of the RM tool is the answer to the questions. This allows you to get the boilerplate code for performing the basic CRUD operations and also expand the project to perform other complex functionality based on the project requirements.
2. **Query converter**: The tool's query converter feature helps you convert your SQL queries, views, and stored procedures into equivalent MongoDB queries and validate them. The query converter uses the relational schema, the MongoDB schema, and the mapping rules in your current project to determine how the queries should be converted. Conversions may fail or be incorrect if the queries reference tables that are not in your relational schema or if they are not mapped to MongoDB collections. This query converter also works for views and stored procedures.

## Migrating the data from the PostgreSQL schema to MongoDB

Once your free Atlas cluster is ready, download the [SQL schema script](https://github.com/mongodb-developer/relational-migrator-lab/blob/main/docker/sample-postgres-library/init/1-library-schema-and-data.sql), which has a sample Postgres schema with data inserted into the tables. Use the [PgAdmin](https://www.pgadmin.org/download/) application to upload the schema and explore the created schema. The sample schema created nine different tables and their relationships for this tutorial. We will use the same schema to generate the equivalent and simpler MongoDB schema, following the different design patterns. First, let's examine both schemas in detail and then understand how to convert them to the equivalent MongoDB schema for further application development.

### Analysing the Postgres schema

In this section, we will understand an example of a library management system where we have nine different tables, and the relationship between them is shown in the diagram below:
[![Image representing the relational schema diagram for the database](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Ftjz0onokqk2qp839418n.png)](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Ftjz0onokqk2qp839418n.png)

The ER diagram above also depicts the relationship between these tables. Relational databases often require intricate joins, nested subqueries, and multiple layers of aggregation to retrieve even moderately complex data relationships. As a result, what might seem like a simple query can quickly escalate into a large, complex, and unwieldy SQL statement. This not only increases the cognitive load for developers but also makes the code harder to maintain and prone to errors.

For example, let's consider a scenario where we need to list all authors with their aliases.

To do so, we would write the SQL query as:

```
SELECT a.name, aa.alias
FROM library.authors a
LEFT JOIN library.author_alias aa ON a.id = aa.author_id;
```

This leads to the joining of two different tables using the foreign key constraint. In the next section, we will understand how this query would look in MongoDB.

### Creating mappings to generate the equivalent MongoDB schema

The nine different tables in the relational schema need to be converted into five different collections using the [mapping technique](https://www.mongodb.com/docs/relational-migrator/mapping-rules/mapping-rules/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe) given by MongoDB. These techniques follow the [design patterns](https://www.mongodb.com/docs/manual/data-modeling/design-patterns/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe) followed by MongoDB in data modelling concepts.

After the mappings have been applied to the tables, the MongoDB schema would like the following:
[![Image representing the desired MongoDB schema](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2F8dycmqr8zxtnpi6ovgtq.png)](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2F8dycmqr8zxtnpi6ovgtq.png)

Let's try to convert the above SQL query into the equivalent MongoDB query:

```
db.collection.aggregate([
{
$project: {
"name": 1,
"aliases": 1,
"_id": 0
}
}
]
)
```

This is one such example where a huge ER diagram with nine different tables can be combined to form five different collections and make the query simpler.

After applying the mapping techniques, once the desired MongoDB schema is achieved, the next step is to migrate the data into the MongoDB Atlas cluster.

### Migrating the data into MongoDB

To migrate the data into the cluster, click on "Data Migration" and select "Create Job Migration."

To begin the migration, you should have the host as the relational database connection secure with you, and the destination is where you can apply the MongoDB connection string. Once both the source and the destination databases are all set up, the next step would be to select the [type of migrations.](https://www.mongodb.com/docs/relational-migrator/jobs/sync-jobs/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe)

In MongoDB, Relational Migrator offers two types of migrations:

1. **Continuous migrations**: Continuous migration jobs cover new incoming data with a zero-downtime change data capture (CDC) migration strategy.
2. **Snapshot migration**: This only runs once and usually does a point-in-time migration.

After selecting the appropriate migration job based on your requirements, the next step is to click on "Review Summary" and then "Start." The migration process will take some time, depending on the size of your data.

Once the migration is complete, you'll find your data seamlessly transferred to the MongoDB Atlas cluster. However, this is often the point where developers encounter uncertainty about what to do next.

But don't worry---MongoDB Relational Migrator eliminates that confusion.

The tool's Code Converter feature is designed to simplify your journey further by providing boilerplate code in the programming language of your choice. This makes it easy to jumpstart your application development, so you can focus on building great features without worrying about the groundwork.

## Code generation with Relational Migrator

To create an application with migrated data using Relational Migrator, you should click on the "Code Generation" tab and select the language and template as Java and Spring Data, respectively. The below screenshot from the Relational Migrator tools explains the steps for the code generation.
[![Screenshot from Relational Migrator tool performing the code generation](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fgftcazognj7pfmnk7clw.png)](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fgftcazognj7pfmnk7clw.png)

Once you select the framework, you are all set to download/copy the code in the editor of your choice for further processing.

## Building Spring Boot application

At this point, building the application becomes very easy, as all the essential code is provided. You just need to perform the business logic, and the application is all set to fulfil the requirements.

In this tutorial, we are using IntelliJ as the IDE and copying all the code provided by the Relational Migrator into a new Spring Boot project created using the Spring Initializr.

Depending on the data model that has been selected, the Relational Migrator tools create the [entity](https://spring.io/guides/gs/accessing-data-jpa) and the [repository](https://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html) files for the application.

Once the entity and the repository files are all copied/downloaded, all you need to do is create the service and the controller to perform the business logic of the applications. After all the files are copied, the project will look like this:
[![Screenshot from InteliJ representing the project structure of the application](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fjq1djs4ewlxkux75y8id.png)](https://media2.dev.to/dynamic/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fjq1djs4ewlxkux75y8id.png)

The controller has all the REST API calls, and the service files are where we need to write the aggregate pipelines.

Let us understand a few examples in the next section.

### Examples of aggregation pipelines

At this point in the application development, we have all the entity and repository files required for building the complete application.

Let us take an example where you need to find all books reviewed within a specific date range. To perform so in the Postgres query, you would have the query as:

```
SELECT 
    r.timestamp,
    u.name AS reviewer_name,
    b.title AS book_title,
    r.rating,
    r.text
FROM 
    library.reviews r
JOIN 
    library.books b ON r.book_id = b.id
JOIN 
    library.users u ON r.user_id = u.id
WHERE 
    r.timestamp BETWEEN '2022-01-01' AND '2024-12-31'
ORDER BY 
    r.timestamp ASC;
```

The equivalent MongoDB Java query in the schema can simply be written as:

```
public List<ReviewsEntity> getBookReviews() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp")
                        .gte(new java.util.Date(1640995200000L))
                        .lte(new java.util.Date(1735603200000L))),
                Aggregation.lookup("books", "_id.bookId", "_id", "book"),
                Aggregation.unwind("book"),
                Aggregation.project()
                        .and("timestamp").as("timestamp")
                        .and("book.title").as("bookTitle")
                        .and("rating").as("rating")
                        .and("text").as("text"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "timestamp"))
        );

        AggregationResults<ReviewsEntity> result = mongoTemplate.aggregate(
                aggregation,
                "reviews",
                ReviewsEntity.class
        );
        return result.getMappedResults();
    }
```

Similarly, a simple query can get an alias name for the author, for which the Postgres query can be written as below:

```
SELECT a.name, aa.alias
FROM library.authors a
LEFT JOIN library.author_alias aa ON a.id = aa.author_id;
```

This Postgres query can be simply used in MongoDB using the $project stage as:

```
public List<AuthorAlias> getAuthorAliases() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andInclude("name", "authorAliases")
                        .andExclude("_id")
        );
        AggregationResults<AuthorAlias> result = mongoTemplate.aggregate(
                aggregation,
                "authors",
                AuthorAlias.class
        );
        return result.getMappedResults();
    }
```

More such examples are provided in the [GitHub repository](https://github.com/mongodb-developer/Spring-Boot-With-Relational-Migrator), which you can use and extend to perform more functionalities.

This tutorial therefore explains how to leverage the Relational Migrator and its functionality to create applications with no complexity and where the focus is more on the business logic and less on the boilerplate repetitive code.

## Conclusion

This article shows how transitioning from traditional relational databases to MongoDB's dynamic and schema-less architecture can be overwhelming, but MongoDB's Relational Migrator simplifies the process. This tool facilitates seamless data migration and offers advanced features like SQL-to-MongoDB query conversion and automatic boilerplate code generation. In a practical tutorial, the article demonstrates how to modernize a legacy relational database-driven application into a Spring Boot-based CRUD application using MongoDB and Relational Migrator. By converting a sample relational schema into an optimized MongoDB schema, the tool reduces the complexity of joins and nested queries, paving the way for faster and more intuitive development cycles.

The guide further explores the step-by-step implementation, from migrating data into MongoDB Atlas using Snapshot or Continuous Migration strategies to generating ready-to-use application code for CRUD operations. The Relational Migrator provides a streamlined workflow for developers by offering code templates compatible with Java and Spring Data, making it easy to build on top of the migrated database. Additionally, the article showcases how MongoDB's aggregation framework simplifies queries, such as filtering reviews within a date range or retrieving authors with aliases, compared to SQL. With examples and practical insights, this tutorial empowers developers to create scalable, modern applications effortlessly.

If you have any questions related to the article or you wish to learn more about MongoDB concepts, please visit the [MongoDB community forums](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Building%20a%20Spring%20Boot%20CRUD%20Application%20Using%20MongoDB%E2%80%99s%20Relational%20Migrator&utm_term=aasawari.sahasrabuddhe) for the latest updates.
