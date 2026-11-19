---
title: "You Can Delete Feign Now: Spring Boot 4's Built-in HTTP Clients"
date: "2026-01-01"
description: "Spring Boot 4 builds @HttpExchange clients from one annotation and a base URL. A runnable demo, plus how to move off RestTemplate and Feign."
authors:
  - "pushpak"
image: "cover.jpg"
categories:
  - "Spring"
  - "Tutorials"
related_posts: []
---

Most Spring projects I open still have two ways of calling other services. Old code uses `RestTemplate`. Newer code uses OpenFeign, which pulls in Spring Cloud, its own BOM, and one more version to keep in step with Spring Boot.

Spring Boot 4 does what Feign does without any of that. You write an interface, add one annotation, put a base URL in `application.yml`, and Boot creates the client bean. You don't need Spring Cloud or `@EnableFeignClients`, and you don't write a factory.

This post builds a small app that calls a public API, so you can run it in five minutes. Then it covers the parts you need in real code: headers, timeouts, errors, and moving existing Feign clients over.

## What we're building

A tiny service that reads users from [JSONPlaceholder](https://jsonplaceholder.typicode.com), a free fake REST API. Our app exposes two endpoints:

- `GET /users` returns all users from the remote API
- `GET /users/{id}` returns one user, or a clean 404 when the user doesn't exist

## Step 1: Dependencies

Create a Spring Boot 4 project (Java 21) and add two starters:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-restclient</artifactId>
</dependency>
```

In Boot 4, the HTTP service client auto-configuration lives in `spring-boot-starter-restclient`. Without it, the app fails at startup because no bean of your client type exists.

## Step 2: Describe the remote API as an interface

```java
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/users")
public interface UserClient {

    @GetExchange
    List<User> findAll();

    @GetExchange("/{id}")
    User findById(@PathVariable long id);
}
```

```java
public record User(long id, String name, String email) {}
```

If you've used Feign, this looks familiar. `@GetExchange` takes the place of `@GetMapping`, and `@PathVariable`, `@RequestParam`, `@RequestHeader` and `@RequestBody` work the same way they do in controllers. There's also `@PostExchange`, `@PutExchange`, `@PatchExchange` and `@DeleteExchange`.

The record only lists the fields we care about. JSONPlaceholder sends back address, phone and company too, and Boot's JSON mapper skips fields the record doesn't have.

## Step 3: Register the client

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@ImportHttpServices(group = "placeholder", types = UserClient.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`@ImportHttpServices` comes from Spring Framework 7. It puts `UserClient` in a group called `placeholder`, and Boot creates one `RestClient` per group. When you have many clients, use `basePackages = "com.example.clients"` instead of listing every type.

A group is usually "one remote service". All clients that talk to the payments API go in one group and share its URL, headers and timeouts.

## Step 4: Configure the group

```yaml
spring:
  http:
    serviceclient:
      placeholder:
        base-url: https://jsonplaceholder.typicode.com
        connect-timeout: 2s
        read-timeout: 5s
```

The key under `serviceclient` must match the group name in `@ImportHttpServices`. Boot reads this, builds a `RestClient` with that base URL and those timeouts, and creates a proxy for `UserClient`.

## Step 5: Use it like any other bean

```java
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserClient users;

    public UserController(UserClient users) {
        this.users = users;
    }

    @GetMapping
    public List<User> all() {
        return users.findAll();
    }

    @GetMapping("/{id}")
    public User one(@PathVariable long id) {
        return users.findById(id);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ProblemDetail notFound() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "No such user");
    }
}
```

The client is a normal Spring bean, so you inject it through the constructor and mock it in tests.

When the remote API answers 4xx or 5xx, the client throws a `RestClientResponseException` subclass. For a 404 that's `HttpClientErrorException.NotFound`. Without the handler, a missing user upstream would turn into a 500 from your API.

## Try it

```bash
./mvnw spring-boot:run
```

```bash
curl localhost:8080/users/1
```

```json
{"id":1,"name":"Leanne Graham","email":"Sincere@april.biz"}
```

```bash
curl -i localhost:8080/users/999
```

```
HTTP/1.1 404
Content-Type: application/problem+json

{"detail":"No such user","instance":"/users/999","status":404,"title":"Not Found"}
```

## Adding headers and interceptors

A fixed header, like an API key, can go straight into the config:

```yaml
spring:
  http:
    serviceclient:
      placeholder:
        base-url: https://jsonplaceholder.typicode.com
        default-header:
          X-Api-Key: ${PLACEHOLDER_API_KEY:demo}
```

For anything with logic, such as logging or a token that changes, register a configurer bean. It gets the `RestClient.Builder` of each client in the group:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

@Configuration
class ClientConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer logCalls() {
        return groups -> groups.filterByName("placeholder")
                .forEachClient((group, builder) -> builder.requestInterceptor((request, body, execution) -> {
                    System.out.println("-> " + request.getMethod() + " " + request.getURI());
                    return execution.execute(request, body);
                }));
    }
}
```

Use a real logger in your project. `System.out` keeps the demo short.

## Moving off Feign

A typical Feign client:

```java
@FeignClient(name = "users", url = "${users.url}")
public interface UserClient {

    @GetMapping("/users/{id}")
    User findById(@PathVariable long id);
}
```

The same client after moving it:

```java
@HttpExchange("/users")
public interface UserClient {

    @GetExchange("/{id}")
    User findById(@PathVariable long id);
}
```

The steps, in order:

1. Replace `@FeignClient` with `@HttpExchange` and the mapping annotations with their `@...Exchange` versions.
2. Add `@ImportHttpServices`, one group per remote service. Delete `@EnableFeignClients` only after the last Feign client is migrated. If it lists clients explicitly (`clients = {...}`), take each migrated interface out of that list, or startup fails.
3. Move each `url` into `spring.http.serviceclient.<group>.base-url`.
4. Turn Feign `RequestInterceptor` beans into a `RestClientHttpServiceGroupConfigurer`.
5. Replace `FeignException` handling with `RestClientResponseException` (or its subclasses).
6. If the Feign client had a `fallback` or `fallbackFactory`, delete that bean. Otherwise two beans implement the interface and injection by type fails with `NoUniqueBeanDefinitionException`. HTTP interface clients have no fallbacks, so move the failure handling into the caller (catch `RestClientException`) or into a `@Retryable` service method.
7. Once no `@FeignClient` is left, remove `spring-cloud-starter-openfeign`. If nothing else uses Spring Cloud, the Spring Cloud BOM can go too.

Apart from fallback handling, callers don't change, because they only ever saw the interface. You can move one client at a time: Feign and HTTP interface clients run side by side in the same app, as long as `@EnableFeignClients` and the OpenFeign starter stay until the last Feign client is gone.

Spring Cloud OpenFeign is now considered feature complete by its maintainers, who point people to Spring's own HTTP interfaces, so I wouldn't start new clients on it.

## Moving off RestTemplate

`RestTemplate` code usually looks like this:

```java
User user = restTemplate.getForObject(baseUrl + "/users/{id}", User.class, id);
```

You can swap it for a `RestClient` call:

```java
User user = restClient.get().uri("/users/{id}", id).retrieve().body(User.class);
```

But if a class is full of these calls for one service, move it to an interface instead. URL building, HTTP methods and response types then live in one place, and the calling code reads `users.findById(id)`.

## Common mistakes

**No bean of type UserClient.** You're missing `spring-boot-starter-restclient`, or the interface isn't listed in `@ImportHttpServices`.

**Requests go to the wrong host or fail with a relative URL error.** The group name in YAML doesn't match the `group` in the annotation, so the client never got its base URL. Check spelling first.

**A 404 upstream becomes a 500 in your API.** The client throws on 4xx and 5xx. Catch the exceptions you expect and map them, like the handler in Step 5.

**Calls hang for a long time when the other service is slow.** Set `connect-timeout` and `read-timeout` on every group. Don't rely on the defaults.

**Expecting retries or circuit breakers.** Feign with Spring Cloud often came with them. HTTP interface clients don't retry by themselves. Spring Framework 7 has `@Retryable` and `@ConcurrencyLimit` in core, so you can add retries without Spring Cloud.

## Wrapping up

For most services, an interface, an annotation and a base URL now replace Feign and hand-written `RestTemplate` calls. If you're starting a new client today, start with `@HttpExchange`. For existing Feign clients, move one at a time when you next touch them.

HTTP clients are one of many places where Boot 4 changed names and packages under people's feet. The starters moved, `@MockBean` is gone, test annotations moved to new packages, and JWT setup got simpler. I put all of it in my Spring Boot 4 Cheat Sheet: 48 pages of code from one real app that compiles on Spring Boot 4.1, plus 55 interview answers, 22 common errors with their fixes, and the full source project. It's $9.99, with a 30-day refund if it doesn't help: [Spring Boot 4 Cheat Sheet + Interview Guide](https://pushpak48.gumroad.com/l/spring-boot-4-cheat-sheet?utm_source=foojay&utm_campaign=http-clients-post).

Still on Feign? Tell me in the comments what's stopping you from moving, and I'll answer.
