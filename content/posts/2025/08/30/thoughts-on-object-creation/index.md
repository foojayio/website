---
title: "Thoughts on object creation"
date: "2025-08-30T17:30:29+00:00"
lastmod: "2026-09-21T06:01:39+00:00"
description: "Creational patterns were first described in the famous Gang of Four's Design Patterns. The book presents each pattern in a dedicated chapter and follows a…"
canonical: "https://blog.frankel.ch/thoughts-object-creation/"
authors:
  - "nicolas-frankel"
image: "cover_large-2.jpg"
categories:
  - "Java"
related_posts:
frozen: false
---

Creational patterns were first described in the famous Gang of Four's [Design Patterns](https://en.wikipedia.org/wiki/Design_Patterns). The book presents each pattern in a dedicated chapter and follows a strict structure for each one: intent, motivation, applicability, structure, participants, collaborations, consequences, implementation, sample codes, known uses, and related patterns. The intent pattern presents a succinct goal of the pattern, while the applicability tells when you should use it.

For example, here's an excerpt for the *Builder* pattern:
> **Intent**:
>
> Separate the construction of a complex object from its representation so that the same construction process can create different representations.
>
> **Applicability**:
>
> Use the Builder pattern when
>
> * the algorithm for creating a complex object should be independent of the Parts that make up the object and how they're assembled.
> * the construction process must allow different representations for the object that's constructed.

The has been foundational in the domain of and has influenced the design of programming languages, including widespread ones such as Java. However, it may come as intimidating or irrelevant to modern software development.

As I'm back in an engineering position, I come across newly written Java code that has a lot of improvement potential regarding maintainability. It works, but I imagine engineers having to update it, including the original author's future self and me, and I'm sure I can help them. This week, I refactored code using creational patterns to improve its maintainability. In this post, I want to describe the issues I faced and mention how patterns helped me.

## Constructor with many parameters of the same type

Let's imagine a constructor with many String parameters:

```java
public License (
    String id, String licenseeName,
    String licenseId, String environment,
    LocalDateTime generatedAt
)
```

When calling this constructor, chances are the caller may unwillingly switch parameter orders:

```java
var license = new License("My license", "XXX-123", "Customer", "User-acceptance tests", new LocalDateTime());
```

Oops, I switched the licensee name and the license ID. Your IDE may help here, but there are other ways.

### Type wrappers

Proponents of pure OOP will happily point out that one should never directly use a string. Instead, one should wrap each parameter in a dedicated type, *e.g.* , a Java `record`:

```java
public record Id(String id) { ... }
public record LicenseeName(String licenseeName) { ... }
public record LicenseeId(String licenseId) { ... }
public record Environment(String environment) { ... }
public record GeneratedAt(LocalDateTime generatedAt) { ... }
```

Now, we can't make a mistake:

```java
var id = new Id("My license");
var licenseeName = new LicenseeName("Customer");
var licenseId = new LicenseeId("XXX-123");
var environment = new Environment("User-acceptance tests");
var generatedAt = new LocalDateTime();

var license = new License(id, licenseId, licenseName, environment, generatedAt); //1
```

1. Compile-time error

While this approach definitely improves maintainability, the wrapper increases the memory size. The exact increase depends on the JDK implementation, but for a single type, it's around 5 times larger.

Kotlin makes it a breeze by providing [inline value classes](https://kotlinlang.org/docs/inline-classes.html): the wrapping is a compile-time check, but the bytecode points to the wrapped type with the following limitation:
> An inline class must have a single property initialized in the primary constructor

### Named parameters

Java offers only method calls with positional parameters, but other languages, *e.g.*, Python, Kotlin, and Rust also offer named parameters.

Here's a Kotlin constructor that mirrors the above class:

```java
class License (
    val id: String, val licenseeName: String,
    val licenseId: String, val environment: String,
    val generatedAt: LocalDateTime
)
```

You can call the constructor by naming the parameters, thus reducing the risks of making a mistake:

```java
val license = License(
    id = "My license", licenseeName = "Customer",
    licenseId = "XXX-123", environment = "User-acceptance tests",
    generatedAt = LocalDateTime()
)
```

### The *Builder* pattern

The *Builder* pattern is another viable approach, even though it's not part of the use-cases described in the GoF.

Here's the code:

```java
public class License {

    private final String id;
    private final String licenseeName;
    private final String licenseId;
    private final String environment;
    private final LocalDateTime generatedAt;

    private License (                              //1
        String id, String licenseeName,
        String licenseId, String environment,
        LocalDateTime generatedAt
    ) { ... }

    public static LicenseBuilder builder() {       //2
        return new LicenseBuilder();
    }

    public static class LicenseBuilder {

        private String id;                         //3
        private String licenseeName;               //3
        private String licenseId;                  //3
        private String environment;                //3
        private LocalDateTime generatedAt;         //3

        private LicenseBuilder() {}                //1

        public LicenseBuilder withId(String id) {  //4
            this.id = id;                          //5
            return this;                           //4
        }

        // Other `withXXX` methods

        public License build() {                   //6
            return new License(
                id, licenseeName,
                licenseId, environment,
                generatedAt
            );
        }
    }
}
```

1. Prevent direct object instantiation
2. Create a new builder
3. The builder fields mimic the object's fields
4. Each method returns the builder object itself
5. Assign the attribute
6. Return the complete object

One can now call the builder as such:

```java
val license = License.builder()
                     .withId("My license")
                     .withLicenseName("Customer")
                     .withLicenseId("XXX-123")
                     .withEnvironment("User-acceptance tests")
                     .withGeneratedAt(new LocalDateTime())
```

Creating the builder code is a pain (unless you use AI), but it allows for better readability. Moreover, one can add validation for every method call, ensuring the object under construction is valid. For more complex objects, one can also implement a [Faceted Builder](https://edup.code.blog/2020/03/06/faceted-builder-pattern/).

## Summary

|     Approach     |            Pros             |                              Cons                              |
|------------------|-----------------------------|----------------------------------------------------------------|
| Type wrappers    | Object-Oriented Programming | * More verbose * Can be memory-heavy depending on the language |
| Named parameters | Easy                        | Not available in Java                                          |
| Builder pattern  | Verbose                     | * Allows creating complex objects * Allows validating \<//ul\> |

## Constructors throwing exceptions

In the same codebase, I found the following code:

```java
public Stuff(UuidService uuidService, FallbackUuidService fallbackUuidService) {

    try {
        uuid = uuidService.getUuid();
    } catch(CannotGetUuidException e) {
        try {
            uuid = fallbackUuidService.getUuid();
        } catch(CannotGetUuidException e1) {
            uuid = "UUID can be fetched";
        }
    }
}
```

With a modicum of experience, you can notice what's wrong in the above snippet. If both services fail, `uuid` is initialized with a string that's not a UUID. All code that relies on UUID must deal with possibly non-UUID values. One must fail fast. A quick fix would look like this:

```java
public Stuff(UuidService uuidService, FallbackUuidService fallbackUuidService) {

    try {
        uuid = uuidService.getUuid();
    } catch(CannotGetUuidException e) {
        try {
            uuid = fallbackUuidService.getUuid();
        } catch(CannotGetUuidException e1) {
            throw new RuntimeException(e1);
        }
    }
}
```

Now, every `Stuff` object has a valid UUID. However, throwing exceptions inside constructors has potential issues:

* Resource Leaks: If the constructor allocates resources (*e.g.* , files, sockets) and throws an exception, those resources may not be released. It can be mitigated by being careful and using `try-catch-finally` blocks.
* Inheritance: If a superclass constructor throws an exception, the subclass constructor won't run.
* Checked exceptions: It's impossible to use checked exceptions in constructors, only runtime ones.

For these reasons, I think exceptions don't have their place in constructors and I avoid them. One can use the *Builder* pattern described in the first section, but as mentioned, it's a lot of code; I don't think it's necessary. Another creational pattern to the rescue, *Factory Method*.
> **Intent**
>
> Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.
>
> **Applicability**
>
> Use the Factory Method pattern when:
>
> * a class can't anticipate the class of objects it must create.
> * a class wants its subclasses to specify the objects it creates.
> * classes delegate responsibility to one of several helper subclasses, and you want to localize the knowledge of which helper subclass is the delegate.

Note that in this case, we use it for a different reason. Here's the updated code:

```java
public class Stuff {

    private final UUID uuid;

    private Stuff(UUID uuid) {                               //1
        this.uuid = uuid;
    }

    public static Stuff create(UuidService uuidService, FallbackUuidService fallbackUuidService) throws CannotGetUuidException {

        try {
            return new Stuff(uuidService.getUuid());
        } catch(CannotGetUuidException e) {
            return new Stuff(fallbackUuidService.getUuid()); //2
        }
    }
}
```

1. Prevent outside instantiation
2. If it fails, throws a new `CannotGetUuidException`

One calls the above like this:

```java
var stuff = Stuff.create(uuidService, fallbackUuidService);  //1
```

1. Need to catch `CannotGetUuidException`

At this point, we are sure that the object is fully initialized if the call succeeds. If it doesn't, no object is created.

## Conclusion

In this post, I've described two usages of the GoF's creational patterns, which aren't listed in the book:  

improving maintainability and ensuring objects are fully initialized.  

Knowing your classics allows you to use them in cases that fit, but weren't initially listed for.

**To go further:**

* [GoF Design Patterns](https://en.wikipedia.org/wiki/Design_Patterns)
* [A dive into the Builder pattern](https://blog.frankel.ch/a-dive-into-the-builder-pattern/)
* [The Builder pattern is a finite state machine!](https://blog.frankel.ch/builder-pattern-finite-state-machine/)

*Originally published at [A Java Geek](https://blog.frankel.ch/thoughts-object-creation/) on August 31^st^, 2025*

*[OOP]: Object-Oriented Programming
*[GoF]: Gang of Four
