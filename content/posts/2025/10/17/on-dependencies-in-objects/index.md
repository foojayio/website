---
title: "On dependencies in objects"
date: "2025-10-17T16:58:39+00:00"
lastmod: "2026-09-21T06:01:22+00:00"
description: "In OOP, objects collaborate. The initial idea of collaboration, first found in Smalltalk, was for object A to send a message to object B. Languages…"
canonical: "https://blog.frankel.ch/dependencies-objects/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "Kotlin"
related_posts:
  - "the-pitfall-of-implicit-returns"
frozen: false
---

In , objects collaborate. The initial idea of collaboration, first found in Smalltalk, was for object A to send a message to object B. Languages designed later use method calling. In both cases, the same question stands: how does an object reference other objects to reach the desired results?

In this post, I tackle the problem of passing dependencies to an object. I will go through several options and analyze their respective pros and cons.

## Constructor injection

For constructor injection, you pass dependencies as parameters to the constructor.

```kotlin
class Delivery(private val addressService: AddressService,
               private val geoService: GeoService,
               private val zoneId: ZoneId) {

    fun computeDeliveryTime(user: User, warehouseLocation: Location): ZonedDateTime {
        val address = addressService.getAddressOf(user)
        val coordinates = geoService.getCoordinates(location)
        // return date time
    }
}
```

Constructor injection is by far the most widespread way to pass to an object its dependencies: for about ten years, every codebase I've seen has constructor injection.

I've a slight issue with constructor injection: it stores dependencies as fields, just like state. Looking at the constructor's signature, it's impossible to distinguish between the state and dependencies without proper typing.

It bugs me. Let's see other ways.

## Parameter passing

Instead of storing the dependencies along with the state, we can pass the dependency when calling the method.

```kotlin
class Delivery(private val zoneId: ZoneId) {

    fun computeDeliveryTime(addressService: AddressService,
                            geoService: GeoService,
                            user: User, warehouseLocation: Location): ZonedDateTime {
        val address = addressService.getAddressOf(user)
        val coordinates = geoService.getCoordinates(location)
        // return date time
    }
}
```

he separation of state and dependencies is now clear: the former is stored in fields, while the latter is passed as function parameters. However, the responsibility of handling the dependency is moved one level up the call chain. The longer the call chain, the more unwieldy it gets.

```
class Order() {

    fun deliver(delivery: Delivery, user: User, warehouseLocation: Location): OrderDetails {
        // Somehow get the address and the geo services
        val deliveryTime = delivery.computeDeliveryTime(addressService, geoService, user, warehouseLocation)
        // return order details
    }
}
```

Note that the call chain length is also a problem with constructor injection. You need to design the code for the call site to be as close as possible to the dependency creation one.

## ThreadLocal

Legacy design makes use of the `ThreadLocal`:
> This class provides thread-local variables. These variables differ from their normal counterparts in that each thread that accesses one (via its get or set method) has its own, independently initialized copy of the variable. `ThreadLocal` instances are typically private static fields in classes that wish to associate state with a thread (e.g., a user ID or Transaction ID).
>
> For example, the class below generates unique identifiers local to each thread. A thread's ID is assigned the first time it invokes `ThreadId.get()` and remains unchanged on subsequent calls.
>
> 

```kotlin
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadId {
    // Atomic integer containing the next thread ID to be assigned
    private static final AtomicInteger nextId = new AtomicInteger(0);

    // Thread local variable containing each thread's ID
    private static final ThreadLocal<Integer> threadId =
        new ThreadLocal<Integer>() {
            @Override protected Integer initialValue() {
                return nextId.getAndIncrement();
        }
    };

    // Returns the current thread's unique ID, assigning it if necessary
    public static int get() {
        return threadId.get();
    }
}
```

>
> -- [Class ThreadLocal](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)

We can rewrite the above code using `ThreadLocal`:

```kotlin
class Delivery(private val zoneId: ZoneId) {

    fun computeDeliveryTime(user: User, warehouseLocation: Location): ZonedDateTime {
        val addressService = AddressService.get()
        val geoService = GeoService.get()
        // return date time
    }
}
```

The `ThreadLocal` can be either set up in the call chain or lazily, on first access. Regardless, the biggest disadvantage of this approach is that it completely hides the dependency. there's no way to understand the coupling by only looking at the class constructor or the function signature; one needs to read the function's source code.

Additionally, the implementation could be a regular singleton pattern, with the same downsides.

## Kotlin context

The last approach is Kotlin-specific and has just been promoted from experimental to beta in Kotlin 2.2.
> Context parameters allow functions and properties to declare dependencies that are implicitly available in the surrounding context.
>
> With context parameters, you don't need to manually pass around values, such as services or dependencies, that are shared and rarely change across sets of function calls.
>
> -- [Context parameter](https://kotlinlang.org/docs/context-parameters.html)

Here's how we can migrate the above code to context parameters:

```kotlin
class Delivery(private val zoneId: ZoneId) {

    context(addressService: AddressService, geoService: GeoService)
    fun computeDeliveryTime(user: User, warehouseLocation: Location): ZonedDateTime {
        // return date time
    }
}
```

And here's how to call it:

```
context(addressService,geoService) {
    delivery.computeDeliveryTime(user, location)
}
```

Note that the call can be nested at any level inside the `context`.

## Summary

|       Approach        |               Pros                |            Cons            |
|-----------------------|-----------------------------------|----------------------------|
| Constructor injection | Testable                          | Mix state and dependencies |
| Parameter passing     | Testable                          | Noisy                      |
| `ThreadLocal`         |                                   | Hides coupling             |
| Context parameter     | Get dependencies on deeply-nested | Limited to Kotlin          |

I guess I'll continue to use constructor injection, unless I'm coding in Kotlin. In this case, I'll be happy to use context parameters, even though they are in beta.

*Originally published at [A Java Geek](https://blog.frankel.ch/dependencies-objects/) on October 12th, 2025*

*[OOP]: Object-Oriented Programming
