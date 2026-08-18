---
title: "An Example of Overengineering - Keep it WET"
slug: "an-example-of-overengineering-keep-it-wet"
date: "2021-04-28T09:20:52+00:00"
lastmod: "2021-08-23T12:54:26+00:00"
description: "This week's post is pretty short. I've already written about overengineering, but this adds a personal touch."
canonical: "https://blog.frankel.ch/example-overengineering/"
authors:
  - "nicolas-frankel"
image: "complexity-5902955_1280.jpg"
categories:
  - "Opinion"
tags:
related_posts:
enlighterjs: true
frozen: false
---

This week's post is pretty short. I've already [written about overengineering](https://blog.frankel.ch/are-you-guilty-of-overengineering/), but this adds a personal touch.

I had to rewrite my [Jet Train demo](https://github.com/hazelcast/jet-train) to use another data provider, switching from a Swiss one to a Bay Area one. One of the main components of the demo is a streaming pipeline.

The pipeline:

1. Reads data from a web endpoint
2. Transforms data through several steps
3. Writes the final data into an in-memory data grid

Most of the transform steps in #2 enrich the data. Each of them requires an implementation of a `BiFunction`.

These implementations all follow the same pattern:

* We evaluate the second parameter of the `BiFunction`.
* If it is `null`, we return the first parameter;
* if not, we use the second parameter to enrich the first parameter with and return the result.

It looks like this snippet:

```kotlin
fun enrich(json: JsonObject, data: String?): JsonObject =
  if (data == null) json
  else JsonObject(json).add("data", data)
```

In the parlance of Object-Oriented Programming, this looks like the poster child for the Template Method pattern. In Functional Programming, this is plain function composition. We can move the null-check inside a shared function outside of the bi-function.

```kotlin
fun unsafeEnrich(json: JsonObject, data: String?): JsonObject =
    JsonObject(json).add("data", data)                                         // 1

fun <T, U> nullSafe(f: BiFunction<T, U?, T>): BiFunction<T, U?, T> =           // 2
    BiFunction<T, U?, T> { t: T, u: U? ->
        if (u == null) t
        else f.apply(t, u)
    }

val unsafeEnrich = BiFunction<JsonObject, String?, JsonObject> { json, data -> // 3
  unsafeEnrich(json, data)
}

val safeEnrich = nullSafe(unsafeEnrich)                                        // 4
```

1. Move the null-check out of the function
2. Factor the null-check into a `BiFunction`
3. Create a `BiFunction` variable from the function
4. Wrap the non null-safe `BiFunction` into the safe one

We can now test:

```kotlin
println(safeEnrich.apply(orig, null))
println(safeEnrich.apply(orig, "x"))
```

It works:

```json
{"foo":"bar"}
{"foo":"bar","data":"x"}
```

When I finished the code, I looked at the code and thought about the quote from Jurassic Park:
> Your scientists were so preoccupied with whether or not they could, they didn't stop to think if they should.

I'm no scientist, but I felt it applied to the work I just did. I realized that I refactored in order to comply to the principle. When looking at the code, it didn't look more readable and the code added to every function was minimal anyway. I threw away my refactoring work in favor of the principle.

There are two lessons here:

1. Think before you code - this one I regularly forget.
2. Don't be afraid to throw away your code.

*Originally published at [A Java Geek](https://blog.frankel.ch/example-overengineering/) on April 14^th^, 2021*

*[WET]: Write Everything Twice
*[DRY]: Don't Repeat Yourself
