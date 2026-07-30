---
title: "Service Layer Pattern in Java With Spring Boot"
slug: "service-layer-pattern-in-java-with-spring-boot"
date: "2025-11-18T14:40:26+00:00"
lastmod: "2025-11-18T14:40:28+00:00"
description: "In modern software design, it is important to develop code that is clean and maintainable. One way developers do this is using the Service Layer pattern.What you'll learnIn this article, you'll learn:What the Service Layer pattern is and why it matters.How it fits with the MVC architecture.How to implement it in a real Spring Boot application.How to add MongoDB with minimal code.Best practices and common mistakes to avoid."
authors:
  - "tim-kelly"
image: "https://foojay.io/wp-content/uploads/2025/11/Screenshot-2025-11-11-at-2.17.07-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In modern software design, it is important to develop code that is clean and maintainable. One way developers do this is using the **Service Layer pattern**.

What you'll learn {#h2-0-what-you-ll-learn}
-------------------------------------------

In this article, you'll learn:

* What the Service Layer pattern is and why it matters.
* How it fits with the MVC architecture.
* How to implement it in a real Spring Boot application.
* How to add MongoDB with minimal code.
* Best practices and common mistakes to avoid.

What is the Service Layer pattern? {#h2-1-what-is-the-service-layer-pattern}
----------------------------------------------------------------------------

The Service Layer pattern is an architectural pattern that defines an application's boundary with a layer of services that establishes a set of available operations and coordinates the application's response in each operation.

This pattern centralizes business rules, making applications more maintainable, testable, and scalable by separating core logic from other concerns like UI and database interactions.

Think of it as the "brain" of your application. It contains your business logic and orchestrates the flow between your controllers (presentation layer) and your data access layer.

Why use a service layer? {#h2-2-why-use-a-service-layer}
--------------------------------------------------------

**Separation of concerns**: Bringing your business logic to one focused layer allows you to keep your code modular and decoupled. Your controllers stay thin and focused on HTTP concerns (routing, status codes, request/response handling), while your business logic lives in services. Your repository is left responsible for only your data interaction.

**Reusability**: Business logic in services can be called from multiple controllers, scheduled jobs, message consumers, or other services.

**Testability**: Isolating the business logic to the service layer often makes it easier to unit test as it removes dependencies on external services for database access and web frameworks.

**Transaction management**: Services are the natural place to define transaction boundaries. This provides a uniform space to manage multiple database interactions, ensuring data consistency.

**Business logic encapsulation**: Complex business rules stay in one place rather than being scattered across your codebase.

How the Service Layer fits with MVC {#h2-3-how-the-service-layer-fits-with-mvc}
-------------------------------------------------------------------------------

If you're familiar with the **Model-View-Controller (MVC)** pattern, you might wonder where the Service Layer fits in. The short answer: It sits between your Controller and your Model, enhancing the traditional MVC architecture.

### Traditional MVC {#h3-4-traditional-mvc}

In a classic MVC pattern, you have three components:

* **Model**: Your data and domain objects
* **View**: The presentation layer (UI, JSON responses, etc.)
* **Controller**: Handles incoming requests and returns responses

In simpler applications, controllers might directly interact with repositories and contain business logic. While this works for small projects, it leads to several problems as your application grows:

* Controllers become bloated with business logic.
* Business logic gets duplicated across multiple controllers.
* Testing becomes harder because business logic is tightly coupled to the web layer.
* Transaction boundaries become unclear.

### MVC + Service Layer {#h3-5-mvc-service-layer}

The Service Layer pattern extends MVC by introducing an intermediate layer:

* **Controller**: Handles HTTP concerns (request validation, routing, status codes)
* **Service**: Contains business logic and orchestrates operations
* **Repository/DAO**: Handles data persistence
* **Model/Entity**: Your domain objects

This creates a cleaner separation:

HTTP Request → Controller → Service → Repository → Database

HTTP Response ← Controller ← Service ← Repository ← Database

**Why this makes sense:**

1. **Controllers stay thin**: They focus solely on web concerns---accepting requests, delegating to services, and formatting responses.
2. **Services stay focused**: They contain your business rules without worrying about HTTP details or database specifics.
3. **Clear responsibilities**: Each layer has one job. Controllers route, Services decide, Repositories persist.
4. **Framework independence**: Your business logic in services doesn't depend on Spring MVC, making it portable and easier to test.

Think of it this way: MVC tells you *how* to structure your application's UI and request handling. The Service Layer tells you *where* to put your business logic. Together, they create a robust, maintainable architecture that scales with your application's complexity.

A real example: User management service {#h2-6-a-real-example-user-management-service}
--------------------------------------------------------------------------------------

Let's build a user management system to see the Service Layer pattern in action. I'll just include what is necessary in this article to show how the Service Layer pattern exists in an application. If you want the full code, check out the [GitHub repository](https://github.com/timotheekelly/spring-service-layer). We'll start simple and progressively add complexity, showing how each layer has a distinct responsibility.

### The scenario {#h3-7-the-scenario}

We're building a user registration and management system. When someone creates an account or updates their profile, several things need to happen:

* Validate that the email is unique and properly formatted
* Generate a unique user ID
* Set default values (like creation timestamp and active status)
* Save the user to the database
* Send a welcome email
* Enforce business rules (like preventing updates to inactive users)

This is a perfect use case for the Service Layer pattern---the controller shouldn't handle validation and email logic, and the repository shouldn't care about business rules. Let's see how we separate these concerns.

### Step 1: The domain model {#h3-8-step-1-the-domain-model}

First, we define our domain object---the User entity that represents a user in our system.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class User {
&nbsp;&nbsp;&nbsp;&nbsp;private String id;
&nbsp;&nbsp;&nbsp;&nbsp;private String email;
&nbsp;&nbsp;&nbsp;&nbsp;private String name;
&nbsp;&nbsp;&nbsp;&nbsp;private LocalDateTime createdAt;
&nbsp;&nbsp;&nbsp;&nbsp;private boolean active;
&nbsp;&nbsp;&nbsp;&nbsp;// constructors, getters, setters
}</pre>

This is a plain Java object (POJO) that represents our core domain concept. It's framework-agnostic and contains no business logic---just data. This model will be used across all layers: The controller returns it as JSON, the service applies business rules to it, and the repository persists it to MongoDB.

### Step 2: The repository interface {#h3-9-step-2-the-repository-interface}

The repository defines our data access contract. It focuses purely on CRUD operations and simple queries---no business logic here.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface UserRepository {
&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;User&gt; findById(String id);
&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;User&gt; findByEmail(String email);
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;User&gt; findAll();
&nbsp;&nbsp;&nbsp;&nbsp;User save(User user);
&nbsp;&nbsp;&nbsp;&nbsp;void deleteById(String id);
&nbsp;&nbsp;&nbsp;&nbsp;boolean existsByEmail(String email);
}</pre>

The repository is the **data access layer**. It sits at the bottom of our architecture and is the only layer that knows how to talk to the database. Notice how these methods are very mechanical---"find this," "save that," "does this exist?" There's no business logic like "createUser" or "deactivateUser"---those belong in the service.

The repository doesn't enforce business rules. It will happily save a user with a duplicate email if you tell it to. That's not its job.

### Step 3: The service interface {#h3-10-step-3-the-service-interface}

The service interface defines our **business operations**. Notice how these methods are named from a business perspective, not a data perspective.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface UserService {
&nbsp;&nbsp;&nbsp;&nbsp;User createUser(String email, String name);
&nbsp;&nbsp;&nbsp;&nbsp;User getUserById(String id);
&nbsp;&nbsp;&nbsp;&nbsp;User updateUserName(String id, String newName);
&nbsp;&nbsp;&nbsp;&nbsp;void deactivateUser(String id);
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;User&gt; getAllActiveUsers();
}</pre>

Compare createUser() with the repository's save(). The service method is business-focused: "Create a user with these details." It doesn't say *how* the user is saved. Compare getAllActiveUsers() with the repository's findAll(). The service adds filtering logic (only active users) that represents a business requirement.

This allows for multiple implementations (useful for testing with mocks) and makes it easy to swap implementations without changing dependent code.

### Step 4: The service implementation {#h3-11-step-4-the-service-implementation}

This is where the real work happens---**the business logic layer**. The service orchestrates operations across multiple components and enforces business rules. This uses a fictitious EmailService to help create users and verify unique user creation.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Service&nbsp;&nbsp;

public class UserServiceImpl implements UserService {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private final UserRepository userRepository;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private final EmailService emailService;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public UserServiceImpl(UserRepository userRepository, EmailService emailService) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.userRepository = userRepository;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.emailService = emailService;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public User createUser(String email, String name) {&nbsp; // No throws clause needed!&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business rule: email must be unique&nbsp; &nbsp; &nbsp; &nbsp; if (userRepository.existsByEmail(email)) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new DuplicateEmailException("User with email " + email + " already exists");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business rule: validate email format&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (!isValidEmail(email)) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new InvalidEmailException("Invalid email format: " + email);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Create and save the user&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = new User();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setId(generateId());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setEmail(email);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setName(name);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setCreatedAt(LocalDateTime.now());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setActive(true);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User savedUser = userRepository.save(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business operation: send welcome email&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;emailService.sendWelcomeEmail(savedUser);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return savedUser;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public User getUserById(String id) {&nbsp; // No throws clause needed!&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return userRepository.findById(id)&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.orElseThrow(() -&gt; new UserNotFoundException("User not found with id: " + id));&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public User updateUserName(String id, String newName) {&nbsp; // No throws clause needed!&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = getUserById(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business rule: can't update deactivated users&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (!user.isActive()) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new UserInactiveException("Cannot update inactive user");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setName(newName);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return userRepository.save(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public void deactivateUser(String id) {&nbsp; // No throws clause needed!&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = getUserById(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.setActive(false);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;userRepository.save(user);&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Business operation: send goodbye email&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;emailService.sendDeactivationEmail(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@Override&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public List&lt;User&gt; getAllActiveUsers() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return userRepository.findAll().stream()&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.filter(User::isActive)&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.collect(Collectors.toList());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;private boolean isValidEmail(String email) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return email != null &amp;&amp; email.matches("^[A-Za-z0-9+_.-]+@(.+)$");&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;private String generateId() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return UUID.randomUUID().toString();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
}</pre>

**What's happening here**:

* **@Service annotation**: This Spring annotation marks this class as a service component, making it available for dependency injection.
* **Constructor injection**: The service depends on UserRepository and EmailService. Spring automatically injects these dependencies.
* **Business rule enforcement**: The service validates email uniqueness and format before saving---the repository doesn't do this validation.
* **Orchestration**: The service coordinates multiple operations: checking for duplicates, saving to the database, and sending emails. The controller doesn't know about any of this complexity.
* **Error handling**: The service throws meaningful business exceptions (like DuplicateEmailException) rather than letting database errors bubble up.

Notice how the service is the only place that knows the complete business workflow. The controller just says "create a user," and the service handles all the details.

### Step 5: The controller {#h3-12-step-5-the-controller}

The controller is the **presentation layer**. It stays thin and focused solely on HTTP concerns---routing requests, handling status codes, and formatting responses.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController&nbsp;&nbsp;
@RequestMapping("/api/users")&nbsp;&nbsp;

public class UserController {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;private final UserService userService;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public UserController(UserService userService) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.userService = userService;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@PostMapping&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;User&gt; createUser(@Valid @RequestBody CreateUserRequest request) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = userService.createUser(request.email(), request.name());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.status(HttpStatus.CREATED).body(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/{id}")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;User&gt; getUser(@PathVariable String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = userService.getUserById(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.ok(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@PutMapping("/{id}/name")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;User&gt; updateName(&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PathVariable String id,&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@Valid @RequestBody UpdateNameRequest request) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User user = userService.updateUserName(id, request.name());&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.ok(user);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@DeleteMapping("/{id}")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;Void&gt; deactivateUser(@PathVariable String id) {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;userService.deactivateUser(id);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.noContent().build();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping("/active")&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;public ResponseEntity&lt;List&lt;User&gt;&gt; getActiveUsers() {&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;User&gt; users = userService.getAllActiveUsers();&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return ResponseEntity.ok(users);&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;&nbsp;
}</pre>

**What the controller does**:

* **@RestController**: combines @Controller and @ResponseBody, automatically serializing return values to JSON
* **Route mapping**: @GetMapping, @PostMapping, etc. map HTTP requests to methods
* **Request binding**: @RequestBody and @PathVariable extract data from the HTTP request
* **Response formatting**: ResponseEntity allows us to set HTTP status codes (201 Created, 200 OK, 204 No Content)

**What the controller does NOT do**:

* No business logic (no validation, no email sending, no business rules)
* No database access
* No complex calculations or decision-making

If you wanted to add a different way to access your users (like a GraphQL endpoint, a command-line interface, or a scheduled batch job), you'd just create a new controller/interface that calls the same UserService. The business logic stays in one place.

Adding MongoDB to your application {#h2-13-adding-mongodb-to-your-application}
------------------------------------------------------------------------------

Now, let's see how to add MongoDB to our user management system. **MongoDB** is a document database that stores data in flexible, JSON-like documents, making it a great fit for user profiles since the schema can easily evolve.

### The simple approach: Spring Data MongoDB {#h3-14-the-simple-approach-spring-data-mongodb}

The easiest way to add MongoDB is to use [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb), which requires minimal code. If you want to see the full code, check out the [GitHub repository](https://github.com/timotheekelly/spring-service-layer) for the article:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.mongodb.springservicelayer.repository;

import com.mongodb.springservicelayer.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository&lt;User, String&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;Optional&lt;User&gt; findByEmail(String email);
&nbsp;&nbsp;&nbsp;&nbsp;boolean existsByEmail(String email);
&nbsp;&nbsp;&nbsp;&nbsp;// findById, findAll, save, deleteById are inherited from MongoRepository
}</pre>

And we'll let Spring Data know that our model maps to a document in the Users collection in MongoDB:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Document(collection = "users")

public class User {
&nbsp;&nbsp;&nbsp;&nbsp;@Id
&nbsp;&nbsp;&nbsp;&nbsp;private String id;
&nbsp;&nbsp;&nbsp;&nbsp;@Indexed(unique = true)
&nbsp;&nbsp;&nbsp;&nbsp;private String email;
&nbsp;&nbsp;&nbsp;&nbsp;private String name;
&nbsp;&nbsp;&nbsp;&nbsp;private LocalDateTime createdAt;
&nbsp;&nbsp;&nbsp;&nbsp;private boolean active;
&nbsp;&nbsp;&nbsp;&nbsp;public User() {
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public User(String id, String email, String name, LocalDateTime createdAt, boolean active) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.id = id;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.email = email;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.name = name;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.createdAt = createdAt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.active = active;
&nbsp;&nbsp;&nbsp;&nbsp;}

// Getters and setters</pre>

That's it! Spring Data MongoDB automatically generates the implementation. You get:

* All CRUD operations (save, findById, findAll, deleteById) for free.
* Custom query methods just by naming them correctly (findByEmail, existsByEmail).

No boilerplate code needed!

### Configuration {#h3-15-configuration}

Add MongoDB to your pom.xml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-data-mongodb&lt;/artifactId&gt;
&lt;/dependency&gt;</pre>

Configure the connection in application.properties:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.data.mongodb.uri=&lt;Your-connection-string&gt;

spring.data.mongodb.database=service-layer-demo</pre>

Note: You will need a [MongoDB Atlas account](https://account.mongodb.com/account/register?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=service+layer+pattern&utm_term=tim.kelly) with a cluster set up to retrieve your [connection string](https://www.mongodb.com/docs/manual/reference/connection-string/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=service+layer+pattern&utm_term=tim.kelly).

### The custom approach (optional) {#h3-16-the-custom-approach-optional}

If you need more control over MongoDB operations, you can implement the repository manually using the [MongoDB Java Driver](https://www.mongodb.com/docs/languages/java/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=service+layer+pattern&utm_term=tim.kelly). This approach gives you fine-grained control over queries, indexing, and document mapping.

### Why this works with the Service Layer pattern {#h3-17-why-this-works-with-the-service-layer-pattern}

Notice how adding MongoDB didn't require any changes to:

* Your UserService---business logic stays the same.
* Your UserController---HTTP handling stays the same.
* Your exception handling---error handling stays the same.

**The service depends on the** **UserRepository** **interface, not the MongoDB implementation.** This means you could swap MongoDB for other databases without touching your business logic. That's the power of the Service Layer pattern's separation of concerns!

Best practices {#h2-18-best-practices}
--------------------------------------

**1. Keep services focused**: Each service should have a single responsibility. Don't create a "God service" that does everything.

**2. Services can call other services**: It's perfectly fine for OrderService to call UserService and InventoryService.

**3. Don't let domain objects leak**: Consider using data transfer objects (DTOs) to separate your internal domain model from what you expose via APIs.

**4. Handle transactions at the Service Layer**: If an operation spans multiple repository calls, manage the transaction in the service with @Transactional.

**5. Keep business logic out of controllers**: If you find yourself writing complex logic in a controller, move it to a service.

**6. Test services independently**: Mock the repository and test your business logic in isolation.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Test

public void createUser_duplicateEmail_throwsException() {
&nbsp;&nbsp;&nbsp;&nbsp;when(userRepository.existsByEmail("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a6d2c3d5d2e6c3dec7cbd6cac388c5c9cb">[email&nbsp;protected]</a>")).thenReturn(true);
&nbsp;&nbsp;&nbsp;&nbsp;assertThrows(DuplicateEmailException.class, () -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;userService.createUser("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4b3f2e383f0b2e332a263b272e65282426">[email&nbsp;protected]</a>", "Test User");
&nbsp;&nbsp;&nbsp;&nbsp;});
}</pre>

Common mistakes to avoid {#h2-19-common-mistakes-to-avoid}
----------------------------------------------------------

**Anemic services**: Don't create services that just pass through to the repository. Services should add value through business logic.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Bad - just a pass-through
public User getUser(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;return userRepository.findById(id).orElse(null);
}

// Good - adds business logic
public User getUser(String id) {
&nbsp;&nbsp;&nbsp;&nbsp;return userRepository.findById(id)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.orElseThrow(() -&gt; new UserNotFoundException(id));
}</pre>

**Business logic in repositories**: Keep repositories focused on data access. Business rules belong in services.

**Tight coupling**: Depend on interfaces, not implementations. This makes testing easier and allows you to swap implementations.

Conclusion {#h2-20-conclusion}
------------------------------

The Service Layer pattern is a powerful way to organize your application's business logic. It creates clear boundaries, makes your code more testable, and keeps your controllers lean. When combined with a clean repository layer, you get a maintainable, scalable architecture that's easy to reason about and extend.

The key is to keep each layer focused on its responsibility: controllers handle HTTP, services handle business logic, and repositories handle data access. Follow this principle, and your codebase will thank you.

If you want to learn more about Spring with MongoDB, check out my tutorial [Building a Real-Time AI Fraud Detection System With Spring Kafka and MongoDB](https://dev.to/mongodb/building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb-2jbn).
