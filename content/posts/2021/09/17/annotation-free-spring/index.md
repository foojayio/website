---
title: "A Return to Annotation-Free Spring"
slug: "annotation-free-spring"
date: "2021-09-17T12:03:24+00:00"
lastmod: "2021-09-23T07:03:27+00:00"
description: "In this article, I'd like to show you how to remove annotations for different features that Spring provides!"
canonical: "https://blog.frankel.ch/annotation-free-spring/"
authors:
  - "nicolas-frankel"
image: "background-6181129_1280.jpg"
categories:
  - "Kotlin"
  - "Research"
  - "Spring"
tags:
related_posts:
  - "exposed-kotlin-orm-complete-guide"
  - "agent-memory-with-spring-ai-redis"
  - "checking-out-junie-a-coding-agent-by-jetbrains"
  - "duplicate-finder-for-text-requirements"
enlighterjs: true
frozen: false
---

Some, if not most, of our judgments regarding technology stacks come either from third-party opinions or previous experiences. Yet, we seem to be adamant about them. For a long time (and sometimes even now), I've seen posts that detailed how Spring is bad because it uses XML for its configuration. Unfortunately, they blissfully ignore the fact that annotation-based configuration has been available for ages.

And probably for of the same reason, I recently read that Spring is bad... *because of annotations*. If you belong to this crowd, I've news for you: you can get rid of most annotations and even more so if you're using Kotlin.

In this article, I'd like to show you how to remove annotations for different features that Spring provides.

## Annotation-free beans

The first place where we tend to set annotations is to register beans. Let's see how to move away from them. It involves several steps. We shall start from the following code:

```java
@Service
public class MyService {}
```

The `@Service` stereotype annotation serves two functions:

* It marks the `MyService` class as belonging to the *service* layer
* It lets the framework know about the class so that it will instantiate a new object and make it available in the context

The first step is to move the annotation away from the class to a dedicated configuration class.

```java
public class MyService {}

@Configuration
public class MyConfiguration {

    @Bean
    public MyService service() {
        return new MyService();
    }
}

@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Because `@SpringBootApplication` is itself annotated with `@Configuration`, we can simplify the code further:

```java
public class MyService {}

@SpringBootApplication
public class MyApplication {

    @Bean
    public MyService service() {
        return new MyService();
    }

    // Run the app
}
```

At this point, the `MyService` class is free of annotations. For me, that would be enough. However, my earlier promise was to remove annotations altogether.

For this, Kotlin offers the Beans . You can refactor the above snippet like this:

```kotlin
class MyService

fun beans() = beans {
    bean<MyService>()                        // 1
}

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args) {
        addInitializers(beans())
    }
}

@SpringBootApplication                       // 2
class MyApplication
```

1. Create a new bean without annotation
2. Single annotation to start the Spring Boot application; see below for how to remove it

## Controllers to routes

Our next feature focuses on web endpoints. The traditional Spring way to provide them is via the `@Controller` annotation:

```java
@Controller                                                        // 1
public class MyController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)  // 2
    @ResponseBody                                                  // 3
    public String hello() {
        return "Hello";
    }
}
```

1. Register the class as a controller
2. Register the method as a request handler
3. Return the result directly without involving a view

For REST controllers, like the snippet above, Spring makes it simpler by providing compound annotations. We can refactor the code as:

```java
@RestController                                                    // 1
public class MyController {

    @GetMapping("/hello")                                          // 2
    public String hello() {
        return "Hello";
    }
}
```

1. Compound `@Controller` and `@ResponseBody`
2. `@RequestMapping` with the `method` attribute set to `GET`

Refactoring doesn't fulfill the "no annotation" promise. Yet, since Spring Web MVC v5.0, the framework offers an alternative to controllers called *routes*. Let's use them to refactor the previous code:

```java
@Bean
RouterFunction<ServerResponse> hello() {
    return route(GET("/hello"),
                 req -> ServerResponse.ok().body("Hello"));
}
```

You could object that there's still one annotation - `@Bean` but we handled this case in the previous paragraph with the help of Kotlin. Spring also provides a dedicated DSL for routes. By using both the above Beans DSL and the Routes DSL, we can rid of all annotations:

```kotlin
bean {
    router {
        GET("/") { ok().body("Hello") }
    }
}
```

## Cross-cutting concerns

A lot (all?) of Spring cross-cutting concerns are configurable with annotations. Such concerns include transaction management and caching. In this paragraph, I'll use caching as an example, but all related features are similar.

```java
@Cacheable("things")
public Thing getAddress(String key) {
    // Get the relevant Thing from the data store
}
```

Spring wraps methods annotated with `@Cacheable` in a proxy. When you call the proxied method, it first checks whether the object is in the cache:

1. If it is, it returns the cached entity, bypassing the datastore-fetching logic
2. If not, it does call it and puts the value in the cache.

Nothing prevents you from eschewing annotations and implementing the above logic yourself.

```java
public class ThingRepository {

    private final Cache cache;

    public ThingRepository(Cache cache) {
        this.cache = cache;
    }

    public Thing getAddress(String key) {
        var value = cache.get(key, Thing.class);
        if (value == null) {
            // Get Thing and return it
        }
        return value;
    }
}
```

If you're a Functional Programming fan, you can refactor the above code to something more suitable to your tastes:

```java
public class ThingRepository {

    private final Cache cache;

    public ThingRepository(Cache cache) {
        this.cache = cache;
    }

    public Thing getAddress(String key) {
        return Optional.ofNullable(cache.get(key, Thing.class))
                       .orElse(/* Get Thing */);
    }
}
```

## Error handling

Spring provides a rich error handling mechanism to ease developers' life via annotations. It makes no sense to paraphrase [the documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-exceptionhandler) as it's pretty well documented:

Here's an example of using `@ExceptionHandler` in a controller:

```java
@RestController
public class MyController {

    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public String hello() {
        service.hello();                                       // 1
    }

    @GetMapping("/world")
    public String world() {
        service.world();                                       // 1
    }

    @ErrorHandler
    public ResponseEntity<String> handle(ServiceException e) { // 2
        return ResponseEntity(e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

1. May throw an unchecked `ServiceException`
2. Spring calls this method if a `ServiceException` class is thrown in one of the above methods

However, nothing prevents you from handling the error in your code. Here's how you can do it:

```java
@RestController
public class MyController {

    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        try {
        	return ResponseEntity(service.hello(), HttpStatus.OK);
        } catch (ServiceException e) {
            return handle(e);
        }
    }

    @GetMapping("/world")
    public String world() {
        try {
        	return ResponseEntity(service.world(), HttpStatus.OK);
        } catch (ServiceException e) {
            return handle(e);
        }
    }

    private ResponseEntity<String> handle(ServiceException e) {
        return ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

I consider it a bit noisy. Of course, we can also use routes:

```java
@Bean
public RouterFunction<ServerResponse> hello(MyService service) {
    return route(GET("/hello"),
        req -> {
            try {
                return ServerResponse.ok().body(service.hello());
            } catch (ServiceException e) {
                return handle(e);
            }
        }).andRoute(GET("/world"),
        req -> {
            try {
                return ServerResponse.ok().body(service.world());
            } catch (ServiceException e) {
                return handle(e);
            }
        });
}

private ServerResponse handle(ServiceException e) {
    return ServerResponse.status(500).body(e.getMessage());
}
```

But I don't think the above snippet is a significant improvement. Kotlin Router DSL doesn't help much either:

```java
router {
    fun handle(e: ServiceException) = status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    GET("/hello") {
        try {
            ok().body(ref<MyService>().hello())
        } catch (e: ServiceException) {
            handle(e)
        }
    }
    GET("/world") {
        try {
            ok().body(ref<MyService>().world())
        } catch (e: ServiceException) {
            handle(e)
        }
    }
}
```

We don't have any annotations, but IMHO, it's not much more readable than the initial snippet.

We can redesign `MyService` to replace exception throwing with a functional approach to improve the code. The easiest path is to use Kotlin's `Result` type from the stdlib. It contains either the requested value or an `Exception` type. Alternative types include [Arrow](https://arrow-kt.io/docs/0.11/apidocs/arrow-core-data/arrow.core/-either/) or [Vavr](https://docs.vavr.io/#_either) `Either` type.

```kotlin
class MyService {
    fun hello(): Result<String> = // compute hello
    fun world(): Result<String> = // compute world
}

var routes = router {
    fun handle(e: ServiceException) = status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    GET("/hello") {
        ref<MyService>().hello().fold(
            { ok().body(it) },
            { handle(it as ServiceException) }
        )
    }
    GET("/world") {
        ref<MyService>().world().fold(
            { ok().body(it) },
            { handle(it as ServiceException) }
        )
    }
}
```

## Starting the application

So far, we have been able to remove every annotation, but the main one: `@SpringBootApplication` compounds `@SpringBootConfiguration`, `@EnableAutoConfiguration`, and `@ComponentScan`. If you dislike annotations, it's a nightmare come true as it does a lot of "magic" under the cover.

It's possible to remove it anyway, provided you accept to use APIs considered experimental. The solution is [Spring Fu](https://github.com/spring-projects-experimental/spring-fu), with "Fu" standing for *functional*. It's available in two flavors, one for Java and one for Kotlin, respectively named JaFu and KoFu.

Here's a snippet from the GitHub repo:

```kotlin
val app = webApplication {                   // 1
    messageSource {
        basename = "messages/messages"
    }
    webMvc {
        thymeleaf()
        converters {
            string()
            resource()
            jackson {
                indentOutput = true
            }
        }
        router {
            resources("/webjars/**", ClassPathResource("META-INF/resources/webjars/"))
        }
    }
    jdbc(DataSourceType.Generic) {
        schema = listOf("classpath*:db/h2/schema.sql")
        data = listOf("classpath*:db/h2/data.sql")
    }
    enable(systemConfig)
    enable(vetConfig)
    enable(ownerConfig)
    enable(visitConfig)
    enable(petConfig)
}

fun main() {
    app.run()                                // 2
}
```

1. Configure the context
2. Start the application with no annotations

## Conclusion

In this post, I've shown you how to move away from annotations in Java and Kotlin, using stable and experimental APIs.

On a more general note, I believe in Darwinism for libraries and frameworks. I'm pretty interested in Quarkus and Micronaut, and I think that their birth made Spring better.

However, things move fast in our industry. Critical or not, I'd suggest that every developer regularly check if their knowledge is still relevant when they express an opinion - and reassess it regularly.

*Originally published at [A Java Geek](https://blog.frankel.ch/annotation-free-spring/) on September 12^th^, 2021*

*[DSL]: Domain-Specific Language
