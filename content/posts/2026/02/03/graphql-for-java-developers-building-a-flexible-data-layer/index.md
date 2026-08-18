---
title: "GraphQL for Java Developers: Building a Flexible Data Layer"
slug: "graphql-for-java-developers-building-a-flexible-data-layer"
date: "2026-02-03T15:43:07+00:00"
lastmod: "2026-02-03T15:43:10+00:00"
description: "For many years, REST has been the standard architectural style for creating APIs in the Java ecosystem. Frameworks such as Spring MVC and, more recently, - by Matteo Rossi"
authors:
  - "matteo-rossi"
image: "gra.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3"
frozen: false
---

For many years, REST has been the standard architectural style for creating APIs in the Java ecosystem. Frameworks such as Spring MVC and, more recently, Spring WebFlux make it easy to expose HTTP endpoints with the REST paradigm, supported by well-structured service layers. In many cases, this model works well and serves as the basis for numerous enterprise solutions.

However, as applications grow and frontend requirements become more dynamic, REST-based APIs are beginning to show their limitations. Multiple endpoints that return rigid DTOs often lead to over-fetching, under-fetching, and a proliferation of specialised endpoints created to meet slightly different customer needs. Over time, APIs become more difficult to evolve without introducing radical changes.

GraphQL approaches the problem from a different perspective. Instead of exposing a series of endpoints, it exposes a strongly typed schema that defines the available data and operations. Clients describe what they want, and the server decides how to retrieve it. This change may seem bizarre at first, but it fits surprisingly well with concepts already familiar to Java developers: contracts, type safety, and explicit evolution.

In this article, we'll look at how to build a flexible, production-ready GraphQL data layer using Spring for GraphQL, Netflix DGS, and [MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=graphql-mongodb-foojay&utm_term=megan.grant). We'll focus on design decisions, trade-offs, patterns, and models that are essential once a GraphQL API moves beyond the experimental phase.

## GraphQL fundamentals

Conceptually, GraphQL is based on three main principles:

* A schema as an API contract
* Queries and mutations as operations
* Resolvers (data retrievers) as execution logic

For Java developers, the most important difference from REST is that the schema is not just documentation. It is executable. Every query is checked against the schema before it even reaches the Java code. This alone eliminates an entire class of runtime errors that are common in loosely specified APIs.

Another important change is that GraphQL APIs are client-driven. The server no longer decides the exact form of the response. Instead, it ensures that the requested fields are available and formally valid. This makes it easier for APIs to evolve, but it also places greater responsibility on backend developers to think carefully about performance and data access patterns.

## Why GraphQL fits well in the Spring ecosystem

Spring applications are already organised according to some well-established principles: inversion of control, declarative configuration, and separation of concerns. GraphQL does not replace these ideas, but complements them.

Spring for GraphQL integrates GraphQL into the Spring ecosystem, allowing GraphQL execution to take advantage of:

* Dependency injection.
* Validation.
* Security filters.
* Observability and metrics.

Resolvers are simply Spring beans, and GraphQL requests pass through the same application context as the rest of the system. This means that GraphQL is not a foreign technology, but rather a natural extension of existing Spring-based architectures.

## Choosing Netflix DGS with Spring for GraphQL

Spring for GraphQL is a bridge between GraphQL Java and the Spring ecosystem, handling schema loading, request execution, and connecting the runtime naturally within Spring. For simpler GraphQL APIs, it may be sufficient on its own.

However, as an API begins to grow, other considerations come into play: schema lifecycle management, resolver organisation, batching, testability, and long-term evolution. Netflix DGS makes a difference in these areas.

Based on Spring for GraphQL, DGS uses a schema-first approach, treating the GraphQL schema as a first-class artifact rather than something that is implicitly derived from Java code. This makes the contract explicit, version-controlled, and easier to evolve safely over time.

DGS also provides strong support for all aspects related to data retrieval, including batching and caching. While DataLoader is available in Spring for GraphQL, DGS offers clearer conventions and first-class abstractions that reduce the possibility of performance issues such as the N+1 query problem.

From a maintainability perspective, DGS is well-suited to larger codebases. Its clear separation between query, mutation, and field resolvers, along with dedicated testing tools and optional code generation, helps keep GraphQL APIs manageable as more developers and clients are added.

Finally, DGS was designed with federated GraphQL architectures in mind. Even when federation is not an immediate requirement, choosing a framework that does not limit the future evolution of the architecture is often a pragmatic decision.

## Project setup

For the creation of the prototype of this article, we assume we will use:

* Java 25.
* Spring Boot 3.5.
* MongoDB.
* Maven.

### Dependencies

The Netflix DGS starter contains all the dependencies needed to use GraphQL on Spring Boot. We will also use Spring Data to interact with MongoDB.

```
<dependency>
  <groupId>com.netflix.graphql.dgs</groupId>
  <artifactId>graphql-dgs-spring-boot-starter</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

At this point, the application is ready to load GraphQL schemas and perform queries and mutations on them.

## Domain model overview

We will work on a very simple but realistic domain:

* User
* Order
* Product

A user can place multiple orders, and each order can have multiple products. This model was chosen because it highlights one of the most important aspects of GraphQL, which is the ability to efficiently resolve relationships.

## Defining the GraphQL schema

Within Netflix DGS, the scheme is the starting point.

```
type Query {
  users: [User!]!
  userById(id: ID!): User
}

type Mutation {
  createUser(input: CreateUserInput!): User!
}

type User {
  id: ID!
  email: String!
  name: String!
  orders: [Order!]!
}

type Order {
  id: ID!
  totalAmount: Float!
  products: [Product!]!
}

type Product {
  id: ID!
  name: String!
  price: Float!
}

input CreateUserInput {
  email: String!
  name: String!
}
```

It is important to highlight a few design choices:

* Non-nullability is explicit and intentional.
* Input types are separate from output types.
* Relationships are part of the schema, not an afterthought.

The schema already communicates API expectations much more clearly than most REST contracts.

## Persistence with MongoDB

Persistence with Spring Data MongoDB is handled traditionally.

```
@Document("users")
public class UserDocument {
    @Id
    private String id;
    private String email;
    private String name;
}
```

It is good practice to avoid using persistence models directly as GraphQL types. MongoDB documents tend to evolve rapidly and often contain internal fields or denormalised data that should not be exposed publicly.

A dedicated mapping layer keeps responsibilities separate and makes schema evolution more secure.

## Query resolvers with Netflix DGS

To translate a GraphQL query into a backend operation, you need to use a query resolver. Its responsibility is to orchestrate, not to apply business logic.

```
@DgsComponent

public class UserQueryResolver {
    private final UserRepository repository;
    public UserQueryResolver(UserRepository repository) {
        this.repository = repository;
    }

    @DgsQuery
    public List<User> users() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toGraphQL)
                .toList();
    }
}
```

From this snippet, we note that:

* The resolver is a simple Spring bean.
* The schema determines the structure of the API, not the method signature.
* The business logic remains in the service layer.

Keeping resolvers lean makes them easier to test and understand.

## Mutations and input validation

Mutations represent write operations and should be treated with the same care as REST POST and PUT endpoints.

```
@DgsComponent

public class UserMutationResolver {
    private final UserRepository repository;
    public UserMutationResolver(UserRepository repository) {
        this.repository = repository;
    }

    @DgsMutation
    public User createUser(@InputArgument CreateUserInput input) {
        UserDocument doc = new UserDocument(
            input.getEmail(),
            input.getName()
        );
        return UserMapper.toGraphQL(repository.save(doc));
    }
}
```

Input validation and validation can be handled using:

* Bean Validation annotations.
* Explicit checks in the resolver.
* Custom GraphQL error mapping.

GraphQL supports partial failures, so validation errors must be precise and manageable.

## Resolving relationships in MongoDB

Unlike relational databases, MongoDB does not support join operations. In GraphQL, relationships are resolved lazily, field by field.

```
@DgsComponent

public class UserFieldResolver {
    private final OrderRepository orderRepository;
    public UserFieldResolver(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @DgsData(parentType = "User", field = "orders")
    public List<Order> orders(DgsDataFetchingEnvironment env) {
        User user = env.getSource();
        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(OrderMapper::toGraphQL)
                .toList();
    }
}
```

This approach is powerful, but it carries a significant downside.

## The N+1 query problem

This is one of those cases where GraphQL seems simple in demos, but can cause significant problems in production environments.

A query such as...

```
{
  users {
    id
    orders {
      id
    }
  }
}
```

...can easily lead to:

* A query for users.
* A query for orders associated with users.

This model requires consideration of the scalability of the solution.

## Using DataLoader in Netflix DGS

DataLoader enables GraphQL to group and cache related data retrieved within a single request.

```
@DgsComponent

public class OrderDataLoader {
    @DgsDataLoader(name = "ordersByUser")
    public BatchLoader<String, List<Order>> ordersByUser(OrderRepository repo) {
        return userIds -> CompletableFuture.supplyAsync(() ->
            repo.findByUserIds(userIds)
        );
    }
}
```

The field resolver then becomes asynchronous:

```
@DgsData(parentType = "User", field = "orders")

public CompletableFuture<List<Order>> orders(DgsDataFetchingEnvironment env) {
    DataLoader<String, List<Order>> loader =
        env.getDataLoader("ordersByUser");
    return loader.load(env.<User>getSource().getId());
}
```

This transforms N database calls into a single grouped query and should be considered mandatory for non-trivial schemas.

## Error handling in GraphQL

GraphQL allows for partial success: One field may fail while others continue to return data. This requires a different approach than the REST paradigm.

In practice:

* Map domain exceptions to GraphQL errors.
* Avoid communicating internal details externally.
* Use error extensions for structured metadata.

Netflix DGS offers hooks to customise error handling consistently.

## Security considerations

GraphQL exposes a large attack surface through a single endpoint, which makes traditional endpoint-based security insufficient. A robust security strategy usually combines several layers:

* HTTP-level authentication, handled by Spring Security (JWT, Oauth2)
* Method-level authorization, applied in resolvers or services
* Field-level restrictions for sensitive data
* Query depth and complexity limits to prevent service abuse

These measures allow GraphQL to remain flexible without becoming permissive.

## When GraphQL is (and isn't) the right choice

GraphQL is a very powerful tool, but it is not suitable for all use cases.

It works well when:

* Multiple frontend clients are using the same API.
* Data visualisation requirements change frequently.
* The domain model is rich and connected.

It is not suitable when there are:

* APIs with a lot of writes and high throughput.
* Simple CRUD services.
* Streaming or binary data.

Choosing GraphQL is an architectural choice, not an automatic one.

## Best practices recap

Before concluding, it is important to summarise some of the concepts that have been explained:

* It is better to develop the schema first.
* Consider the schema as a public contract.
* Keep resolvers simple and specialised.
* Use DataLoader from the outset.
* Separate API models from persistence models.
* Measure and limit query complexity.

Applying these simple practices is more important than the specific framework, and often determines the success or failure of the application itself.

## Conclusion

Spring for GraphQL and Netflix DGS offer Java developers a mature, production-ready stack for creating flexible APIs. When used together with MongoDB, they enable expressive data access patterns, but only if performance, security, and schema design are considered priorities.

GraphQL does not aim to replace the REST paradigm everywhere. Rather, it is a targeted choice, in certain cases, where you want to use an abstraction that supports change more simply and immediately. When used carefully, it can become a shared language between backend and frontend teams, without sacrificing the robustness and clarity that Java developers expect.

[Get the source code](https://github.com/matteoroxis/grapholizer) for the prototype described in the article.
