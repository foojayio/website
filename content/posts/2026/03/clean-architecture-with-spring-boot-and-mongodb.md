---
title: "Clean Architecture with Spring Boot and MongoDB"
slug: "clean-architecture-with-spring-boot-and-mongodb"
date: "2026-03-24T16:43:14+00:00"
lastmod: "2026-03-24T16:43:15+00:00"
description: "In this article, you will build a product catalog with orders. Products have names, prices, and stock quantities. Orders reference products and enforce rules like \"you can't order more than what's in stock.\" The domain is small enough to follow in one sitting, but it has real business rules that benefit from the architecture. The tech stack is Java 17+, Spring Boot 3.x, and Spring Data MongoDB. By the end, you will have a project structure where the domain and application layers compile without Spring or MongoDB on the classpath."
authors:
  - "farhan-chowdhury"
image: "/images/posts/2026/03/clean-architecture-with-spring-boot-and-mongodb/Screenshot-2026-03-18-at-8.36.30-AM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Most Spring Boot tutorials tightly wire everything together. Controllers call services, services call repositories, and MongoDB annotations like `@Document` and `@Field` sit right next to your business logic. It works until you need to swap the database, test logic in isolation, or reuse domain rules in a different context.

Clean Architecture enforces one rule: source code dependencies always point inward. Your business logic never imports Spring or MongoDB classes. The database becomes a pluggable detail at the outermost layer, something you can replace without rewriting core application code.

In this article, you will build a product catalog with orders. Products have names, prices, and stock quantities. Orders reference products and enforce rules like "you can't order more than what's in stock." The domain is small enough to follow in one sitting, but it has real business rules that benefit from the architecture. The tech stack is Java 17+, Spring Boot 3.x, and Spring Data MongoDB. By the end, you will have a project structure where the domain and application layers compile without Spring or MongoDB on the classpath.

The complete source code is available in the [companion repository on GitHub](https://github.com/fhsinchy/clean-architecture-spring-boot-mongodb).

Prerequisites {#prerequisites}
------------------------------

* Java 17 or later
* Spring Boot 3.x (use [Spring Initializr](https://start.spring.io/) with the `Spring Data MongoDB` and `Spring Web` dependencies)
* A MongoDB Atlas cluster (the free tier is sufficient). You can set up one by following the [MongoDB Atlas getting started guide](https://www.mongodb.com/docs/atlas/getting-started/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=djanog-mongodb-queryable&utm_term=hugh.murray). Alternatively, a local MongoDB instance or Docker container works too.
* Basic familiarity with Spring Boot (controllers, services, dependency injection)

1. What is Clean Architecture? {#1-what-is-clean-architecture}
--------------------------------------------------------------

Robert C. Martin introduced Clean Architecture to keep business rules independent of frameworks, databases, and UI. The central idea is the dependency rule: source code dependencies must point inward. Inner layers define interfaces, outer layers implement them. Never the reverse.

The architecture is usually drawn as four concentric rings. Each ring is a layer. Dependencies always point inward, from the outermost frameworks ring toward the innermost domain ring:  

<figure class="aligncenter size-full is-resized">
 <img fetchpriority="high" decoding="async" width="756" height="758" src="/images/posts/2026/03/clean-architecture-with-spring-boot-and-mongodb/Screenshot-2026-03-18-at-8.36.30-AM.png" alt="" class="wp-image-123099" style="width:579px;height:auto">
</figure>

Starting from the inside:

The innermost ring is the domain layer, which contains business objects and rules. A `Product` knows its price and validates that stock cannot go negative. An `Order` knows it must have at least one item. These are plain Java classes with no framework annotations.

The next ring out is the application layer, which contains application-specific rules. "Create an order" is a use case. It coordinates domain objects but does not know how they are stored or how the user triggers the action.

The interface adapter ring translates between the format used by the need cases and the format external agencies provide. Controllers and MongoDB document classes live here. A `ProductDocument` annotated with `@Document` is an adapter concern, not a domain concern.

The outermost ring is frameworks and drivers: Spring Boot, the MongoDB driver, and the web server. Configuration and wiring happen here. `@SpringBootApplication` lives in this ring.

MongoDB sits in the outermost ring alongside Spring Boot. The domain layer defines a `ProductRepository` interface, which in Clean Architecture terminology is called a "port." A port is just a Java interface that declares what the inner layer needs without saying how it is provided. The MongoDB adapter in the outer ring implements that interface using Spring Data MongoDB. If you replaced MongoDB with PostgreSQL tomorrow, you would write a new adapter. The domain and application layers would not change.

In a typical Spring Boot application, the entity class carries `@Document`, `@Id`, and `@Field` annotations. The service imports it directly. The controller imports the service. Every layer knows about MongoDB. In Clean Architecture, the entity is a plain Java class. The `@Document` class is a separate object in the adapter layer, with mapper functions to convert between the two. This separation means your domain logic never depends on how your data is stored.

2. Project structure {#2-project-structure}
-------------------------------------------

The package layout for the project looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dev.farhan.catalog/
  domain/
    model/          → Product, Order, OrderItem
    port/
      in/           → CreateOrderUseCase, GetProductCatalogUseCase
      out/          → ProductRepository, OrderRepository
  application/
    service/        → CreateOrderService, GetProductCatalogService
  adapter/
    in/
      web/          → OrderController, ProductController, request/response DTOs
    out/
      persistence/  → ProductDocument, OrderDocument, MongoProductRepository,
                      MongoOrderRepository, TransactionalCreateOrderUseCase, mappers
  CatalogApplication.java
  BeanConfiguration.java
</pre>

The `domain` package has no imports from `adapter` or `application`. The `application` package imports from `domain` but not from `adapter`. The `adapter` package imports from both, but dependency arrows always point inward. If a developer accidentally imports a MongoDB class in the domain layer, the package structure makes it obvious that something is wrong.

Since we already covered what a port is, the directory names should make sense. The `in` ports define what the outside world can ask the application to do (use cases). The `out` ports define what the application needs from the outside world (e.g., repositories, external services).

You can clone the [companion repository](https://github.com/fhsinchy/clean-architecture-spring-boot-mongodb) to get this structure pre-built and follow along.

3. Building the domain layer {#3-building-the-domain-layer}
-----------------------------------------------------------

Domain models are plain Java classes with no framework dependencies. Start with `Product`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Product {

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;

    public Product(String id, String name, String description, BigDecimal price, int stockQuantity) {
        if (price == null || price.compareTo(BigDecimal.ZERO) &lt;= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (stockQuantity &lt; 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity &gt; this.stockQuantity) {
            throw new IllegalArgumentException(
                    "Cannot decrease stock by " + quantity + ": only " + this.stockQuantity + " available"
            );
        }
        this.stockQuantity -= quantity;
    }

    // getters omitted for brevity
}
</pre>

The constructor validates that the price is positive and the stock quantity is non-negative. The `decreaseStock` method enforces the business rule that you cannot order more than what is available. This rule belongs to the entity, not to a service class. If stock validation logic lived in a service, you could bypass it by calling the entity directly from somewhere else in the codebase.

`OrderItem` represents a single line in an order:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class OrderItem {

    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;

    public OrderItem(String productId, String productName, BigDecimal price, int quantity) {
        if (quantity &lt; 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // getters omitted for brevity
}
</pre>

`Order` has a private constructor and a public `create` method instead. This pattern (called a static factory method) forces all callers to go through `create`, where you can enforce rules that must always hold true. In this case, every order must have at least one item, and the total is calculated from those items rather than set by the caller.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Order {

    private String id;
    private List&lt;OrderItem&gt; items;
    private BigDecimal totalAmount;
    private Instant createdAt;

    private Order(String id, List&lt;OrderItem&gt; items, BigDecimal totalAmount, Instant createdAt) {
        this.id = id;
        this.items = items;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public static Order create(String id, List&lt;OrderItem&gt; items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        BigDecimal total = items.stream()
                .map(item -&gt; item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Order(id, List.copyOf(items), total, Instant.now());
    }

    // getters omitted for brevity
}
</pre>

`List.copyOf(items)` creates an unmodifiable copy of the list. Without it, whoever passed the original list could add or remove items after the order is created, breaking the total calculation. Defensive copies like this are a common practice in domain objects.

The repository port interfaces define what the domain needs from the outside world:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface ProductRepository {

    Optional&lt;Product&gt; findById(String id);
    List&lt;Product&gt; findAll();
    Product save(Product product);
}
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface OrderRepository {

    Order save(Order order);
    Optional&lt;Order&gt; findById(String id);
}
</pre>

These are plain Java interfaces in the `domain.port.out` package. No Spring annotations, no `MongoRepository` extension. If you deleted the Spring and MongoDB dependencies from your `pom.xml`, this package would still compile. That is the whole point: the domain depends on nothing but the JDK.

This matters for testing. You can write unit tests for `Product.decreaseStock()` or `Order.create()` without starting a Spring context or connecting to a database. You can also create a simple in-memory implementation of `ProductRepository` that stores products in a `HashMap`, and use it to test your services without touching MongoDB at all. Your business rules do not depend on any framework, so they are portable across different infrastructures.

4. Building the application layer {#4-building-the-application-layer}
---------------------------------------------------------------------

Use case interfaces define what the outside world can ask the application to do. These are the inbound ports:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface CreateOrderUseCase {

    Order execute(CreateOrderCommand command);

    record CreateOrderCommand(List&lt;OrderItemRequest&gt; items) {
        public record OrderItemRequest(String productId, int quantity) {}
    }
}
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface GetProductCatalogUseCase {

    List&lt;Product&gt; execute();
}
</pre>

A couple of things to unpack here. `CreateOrderCommand` and `OrderItemRequest` are Java records. A record is a compact way to define a class that just holds data. Writing `record CreateOrderCommand(List<OrderItemRequest> items)` gives you a constructor, a getter (`items()`), `equals`, `hashCode`, and `toString` automatically. You will see records used throughout this project for DTOs and command objects since they are data carriers with no behavior.

Why wrap the input in a command object instead of passing `List<OrderItemRequest>` directly to `execute`? Because if the use case later needs more context (say, a customer ID or a discount code), you add a field to the record instead of changing the method signature and every caller. The command object gives you a stable interface.

`GetProductCatalogUseCase` is simpler: it takes no input and returns the full list of products.

The service implementations coordinate domain objects through the port interfaces:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class CreateOrderService implements CreateOrderUseCase {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CreateOrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(CreateOrderCommand command) {
        List&lt;OrderItem&gt; orderItems = new ArrayList&lt;&gt;();

        for (CreateOrderCommand.OrderItemRequest itemRequest : command.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -&gt; new IllegalArgumentException(
                            "Product not found: " + itemRequest.productId()
                    ));

            product.decreaseStock(itemRequest.quantity());

            orderItems.add(new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemRequest.quantity()
            ));

            productRepository.save(product);
        }

        Order order = Order.create(UUID.randomUUID().toString(), orderItems);
        return orderRepository.save(order);
    }
}
</pre>

For each item in the command, the service looks up the product, calls `product.decreaseStock()` to enforce the stock rule, builds an `OrderItem`, and saves the updated product. It then creates the `Order` from the collected items and persists it through the repository port. `UUID.randomUUID().toString()` generates a random, unique ID for the order, like `"550e8400-e29b-41d4-a716-446655440000"`. We generate IDs in the application layer rather than letting MongoDB assign them because the domain should not depend on database behavior.

There is no `@Service` or `@Autowired` here. These are plain Java classes that accept interfaces through their constructors. Spring wires them later, but the application layer does not know or care about that. The service coordinates domain objects and calls repository interfaces. It does not contain business rules (stock validation is in the `Product` entity), and it does not know how data is stored (that is behind the port interface).

There is a subtle problem with this service, though. Each `productRepository.save()` call commits immediately. If `orderRepository.save()` fails at the end, you have products with reduced stock but no order to show for it. The fix is a database transaction that rolls back all changes on failure. But adding `@Transactional` directly to `CreateOrderService` would introduce a Spring import into the application layer. Instead, we handle this with a thin wrapper in the adapter layer that we will see in section 6.

`GetProductCatalogService` does one thing:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class GetProductCatalogService implements GetProductCatalogUseCase {

    private final ProductRepository productRepository;

    public GetProductCatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List&lt;Product&gt; execute() {
        return productRepository.findAll();
    }
}
</pre>

5. Building the MongoDB adapter {#5-building-the-mongodb-adapter}
-----------------------------------------------------------------

So far, nothing in the project references MongoDB or Spring. The adapter layer is where that changes. Document classes are separate from domain models. `ProductDocument` looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    private String description;

    @Field
    private BigDecimal price;

    @Field("stock_quantity")
    private int stockQuantity;

    // constructors, getters, and setters omitted for brevity
}
</pre>

`OrderDocument` is annotated with `@Document(collection = "orders")` and contains a list of `OrderItemDocument` objects. `OrderItemDocument` is a plain class without `@Document` since it is embedded inside the order document.

Why keep document classes separate from domain models? The domain `Product` has business methods and validation. The `ProductDocument` just holds data for MongoDB serialization. Mixing the two couples your domain to your database schema. If your MongoDB schema changes (say you rename a field or restructure nested documents), the domain model stays the same. The mapper absorbs the difference.

Mapper classes handle the conversion with static methods:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class ProductMapper {

    public static ProductDocument toDocument(Product product) {
        return new ProductDocument(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    public static Product toDomain(ProductDocument document) {
        return new Product(
                document.getId(),
                document.getName(),
                document.getDescription(),
                document.getPrice(),
                document.getStockQuantity()
        );
    }
}
</pre>

Simple field-by-field conversions. No mapping libraries needed for a project this size. `OrderMapper` follows the same pattern, handling the nested `OrderItem` to `OrderItemDocument` conversion.

The repository implementations connect the domain port interfaces to Spring Data MongoDB:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
public class MongoProductRepository implements ProductRepository {

    private final SpringDataMongoProductRepository springDataRepository;

    public MongoProductRepository(SpringDataMongoProductRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional&lt;Product&gt; findById(String id) {
        return springDataRepository.findById(id)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List&lt;Product&gt; findAll() {
        return springDataRepository.findAll().stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        ProductDocument document = ProductMapper.toDocument(product);
        ProductDocument saved = springDataRepository.save(document);
        return ProductMapper.toDomain(saved);
    }
}
</pre>

`MongoProductRepository` implements the domain's `ProductRepository` interface and is annotated with `@Component` so Spring picks it up for dependency injection. Internally, it uses `SpringDataMongoProductRepository`, which is a standard Spring Data interface:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface SpringDataMongoProductRepository extends MongoRepository&lt;ProductDocument, String&gt; {
}
</pre>

This interface extends `MongoRepository<ProductDocument, String>`. The two type parameters tell Spring Data which document class to work with (`ProductDocument`) and what type the `@Id` field is (`String`). In return, you get the standard CRUD methods (save, findById, findAll, delete) without writing any implementation. Spring Data generates the implementation at runtime. The adapter wraps this generated repository and converts between document and domain objects using the mapper. Each method in `MongoProductRepository` follows the same three-step pattern: convert the input, call Spring Data, convert the output.

`MongoOrderRepository` follows the same pattern for orders.

If you decided to switch from MongoDB to PostgreSQL, you would create a new `adapter.out.persistence` package with JPA entities, JPA repository implementations, and new mappers. The `domain` and `application` packages would not change at all. The business rules and use case logic stay exactly as they are.

6. Wiring everything with Spring Boot {#6-wiring-everything-with-spring-boot}
-----------------------------------------------------------------------------

REST controllers are the inbound adapters. `ProductController` exposes `GET /products`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController
@RequestMapping("/products")
public class ProductController {

    private final GetProductCatalogUseCase getProductCatalogUseCase;

    public ProductController(GetProductCatalogUseCase getProductCatalogUseCase) {
        this.getProductCatalogUseCase = getProductCatalogUseCase;
    }

    @GetMapping
    public List&lt;ProductResponse&gt; getProducts() {
        return getProductCatalogUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}
</pre>

`OrderController` exposes `POST /orders` and maps the incoming JSON to a `CreateOrderCommand`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity&lt;CreateOrderResponse&gt; createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(
                request.items().stream()
                        .map(item -&gt; new CreateOrderCommand.OrderItemRequest(
                                item.productId(),
                                item.quantity()
                        ))
                        .toList()
        );

        Order order = createOrderUseCase.execute(command);

        CreateOrderResponse response = new CreateOrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getCreatedAt().toString()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity&lt;Map&lt;String, String&gt;&gt; handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
</pre>

The `@ExceptionHandler` at the bottom catches `IllegalArgumentException` thrown by domain validation (like `Product.decreaseStock()`) and returns a 400 response with the error message. Without this, Spring would return a generic 500 error with a stack trace.

Request and response DTOs are defined as Java records in the `adapter.in.web` package. `CreateOrderRequest`, `OrderItemRequest`, `CreateOrderResponse`, and `ProductResponse` are all records with no logic, just data carriers for JSON serialization. Controllers map between these DTOs and domain objects. The controllers never touch MongoDB classes directly.

Remember the transaction problem from section 4? `CreateOrderService` saves products one at a time, and if the final order save fails, you are left with decremented stock and no order. We need `@Transactional`, but adding it directly to the service would pull Spring into the application layer. The solution is a thin wrapper in the adapter layer:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class TransactionalCreateOrderUseCase implements CreateOrderUseCase {

    private final CreateOrderUseCase delegate;

    public TransactionalCreateOrderUseCase(CreateOrderUseCase delegate) {
        this.delegate = delegate;
    }

    @Transactional
    @Override
    public Order execute(CreateOrderCommand command) {
        return delegate.execute(command);
    }
}
</pre>

`TransactionalCreateOrderUseCase` lives in `adapter.out.persistence` alongside the other infrastructure code. It implements the same `CreateOrderUseCase` interface, delegates to `CreateOrderService`, and adds `@Transactional` so that all database writes within `execute` either succeed together or roll back together. The `CreateOrderService` itself stays free of Spring annotations.

The wiring happens in a `@Configuration` class with explicit `@Bean` methods:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Configuration
public class BeanConfiguration {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(ProductRepository productRepository,
                                                  OrderRepository orderRepository) {
        CreateOrderService service = new CreateOrderService(productRepository, orderRepository);
        return new TransactionalCreateOrderUseCase(service);
    }

    @Bean
    public GetProductCatalogUseCase getProductCatalogUseCase(ProductRepository productRepository) {
        return new GetProductCatalogService(productRepository);
    }
}
</pre>

The `MongoTransactionManager` bean tells Spring how to manage transactions for MongoDB. Without it, `@Transactional` would have no effect. Note that MongoDB transactions require a replica set. Atlas clusters are replica sets by default, so this works out of the box. If you are running MongoDB locally, you will need to configure it as a replica set.

The `createOrderUseCase` bean first creates the plain `CreateOrderService`, then wraps it in `TransactionalCreateOrderUseCase`. Controllers receive the transactional wrapper through the `CreateOrderUseCase` interface and never know the difference.

You might wonder why the adapter repositories use `@Component` while the services use `@Bean` in a configuration class. The adapter classes already live in the outer layer, where Spring annotations are fine. But the service classes live in the application layer, which should not depend on Spring. Putting `@Service` on `CreateOrderService` would add a Spring import to a layer that is supposed to be framework-free. The `@Bean` method in `BeanConfiguration` wires them from the outside, keeping the application layer clean.

This also makes the wiring visible in one place. You can see exactly which implementations back which interfaces. The `ProductRepository` and `OrderRepository` parameters are resolved by Spring from the `@Component`-annotated adapter classes (`MongoProductRepository` and `MongoOrderRepository`).

To see how all of this connects, trace a `POST /orders` request through the layers:  
![](/images/posts/2026/03/clean-architecture-with-spring-boot-and-mongodb/Screenshot-2026-03-18-at-8.36.36-AM.png)

Each layer depends only on the layer inside it. The controller knows about use case interfaces but not about services. The services know about domain objects and repository interfaces, but not about MongoDB documents. The MongoDB adapters implement the repository interfaces and handle all the database-specific details.

At the bottom of that flow, Spring has injected `MongoProductRepository` and `MongoOrderRepository` behind the `ProductRepository` and `OrderRepository` interfaces. The service never sees these concrete classes. If you swapped in a different adapter, the request would flow through the same path but hit a different database at the bottom.

Conclusion {#conclusion}
------------------------

Clean Architecture keeps your domain and business logic independent of MongoDB and Spring Boot. The dependency rule is enforced through package structure and interfaces. MongoDB is a pluggable adapter at the outermost ring, not a concern that leaks into your business logic.

This approach adds some upfront structure: separate document classes, mappers, and port interfaces. But it pays off as the project grows. Adding a new use case means writing a service class and a `@Bean` method. Changing the database means writing a new adapter package. Domain logic does not change for infrastructure reasons.

The separation also makes testing more practical. You can test domain models with plain JUnit tests, no Spring context needed. You can test services by passing in mock or in-memory repository implementations. Integration tests that need MongoDB only touch the adapter layer.

You can find the complete project in the [companion repository](https://github.com/fhsinchy/clean-architecture-spring-boot-mongodb) and use it as a starting point for your own applications. As next steps, try adding more use cases to the catalog (update a product, cancel an order), or experiment with replacing the MongoDB adapter with an in-memory implementation to see how the domain layer stays the same.
