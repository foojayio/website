---
title: "On Cosmetics vs. Intrinsics in Programming"
date: "2022-09-12T09:39:56+00:00"
lastmod: "2022-09-12T09:43:40+00:00"
description: "Instead of arguing about cosmetics, e.g., annotations vs. \"functional\", we need to spend time on intrinsics more: actors, asynchronous, etc."
canonical: "https://blog.frankel.ch/on-cosmetics-vs-intrinsics-programming/"
authors:
  - "nicolas-frankel"
image: "spice-chiles-paprika-chili-54453.jpg"
categories:
  - "Opinion"
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "blockhound-how-it-works"
  - "chopping-monolith"
  - "foojay-podcast-20"
frozen: false
---

A ruthless battle occurs every day on the World Wide Web. Its goal is to decide which programming flavor is the best: or ? I assume that imperative and procedural programming are not part of the contenders.

Arguments range from the factual to the irrelevant to the utterly stupid. A couple of years ago, I wanted to listen to a video of Martin Odersky (of Scala fame). I remember neither the exact talk nor the subject. What I remember is the introduction, though: he explained that FP was more popular than OOP... because there were many more conferences dedicated to the former than to the latter.

At the time, I didn't think popularity was a relevant factor that helped me deliver projects. At the time of this writing, I still don't. Moreover, it's akin to saying that electricity isn't popular because there aren't any conferences dedicated to it. I'm afraid that M. Odersky mistook popularity in academic research for relevancy. I stopped after his "argument," and to this day, I never watched a talk of his again.

This being said, my point is not to bash M. Odersky but to highlight the sheer vacuity of some arguments. For example, for FP aficionados, its immutability is not one, as OOP can also make good use of it. The only difference is that immutability is a requirement in FP. On the opposite side, pushing OOP too far results in languages like Java, where every method must belong to a class, even static ones. In this case, classes are just an additional namespace for methods - they bring no OOP "value".

The scope goes well beyond OOP vs. FP. Consider the following snippets:

|----------------------------|----------------------------|
| 

```java
fun router(repo: PersonRepository) = router {
    val handler = Handler(repo)
    GET("/person", handler::getAll)
}

class Handler(private val repo: PersonRepository) {
    fun getAll(r: ServerRequest) =
            ok().body(repo.findAll())
}
```

 | 

```java
fun router(repo: PersonRepository) = router {
    val handler = Handler(repo)
    GET("/person/{id}", handler::getOne)
}

class  Handler(private val repo: PersonRepository) {
    fun getAll(r: ServerRequest) =
            ok().bodyValue(repo.findAll())
}
```

 |

Obviously, the difference lies in the `ok().body()` vs. `ok().bodyValue()`. If you're unfamiliar with the Spring framework, you're unlikely to correctly identify the left snippet as WebMVC.fn and the right as Web Flux.

It can be even more confusing if `repo.findAll()` is updated from blocking to non-blocking, as you won't spot any difference. You can only distinguish one from the other by looking at the package imports:

* Blocking: `org.springframework.web.servlet.function.*`
* Non-blocking: `org.springframework.web.reactive.function.server.*`

You can rewrite both above snippets using annotations instead of handlers.

|----------------------------|----------------------------|
| 

```java
@RestController
class PersonController(private val repo: PersonRepository) {

  @GetMapping
  fun getAll() = repo.findAll()
}
```

 | 

```java
@RestController
class PersonController(private val repo: PersonRepository) {

  @GetMapping
  fun getAll() = repo.findAll()
}
```

 |

Both snippets appear similar on the surface, but for the imports. Cosmetics are identical, while intrinsics - blocking vs. non-blocking - are fundamentally different.

Let's have a look at Kotlin [coroutines](https://kotlinlang.org/docs/coroutines-basics.html). Here's a snippet taken from Kotlin's [documentation](https://kotlinlang.org/docs/composing-suspending-functions.html#async-style-functions):

```kotlin
measureTimeMillis {
    val one = somethingUsefulOne()                             // 1
    val two = somethingUsefulTwo()                             // 1
    runBlocking {
        println("The answer is ${one.await() + two.await()}")
    }
}
```

1. Function points to a suspending computation

Coroutine code cosmetically appears imperative while being asynchronous.

It's *the* advantage of coroutines:

* It looks imperative on the surface; hence it's easy enough to understand
* Behind the scenes, the library runs the code asynchronously

Code has cosmetic and intrinsic characteristics. I hope the few examples above convinced you that they are entirely orthogonal. You can achieve the same intrinsics with different cosmetics and *vice versa*.

We constantly argue about *cosmetics* , *e.g.* , annotations vs. "functional", but it's essentially a matter of personal taste. To solve problems, we need to spend time on **intrinsics** a lot more: actors, asynchronous, etc.

*Originally published at [A Java Geek](https://blog.frankel.ch/on-cosmetics-vs-intrinsics-programming/) on July 31^st^, 2022*

*[OOP]: Object-Oriented Programming
*[FP]: Functional Programming
