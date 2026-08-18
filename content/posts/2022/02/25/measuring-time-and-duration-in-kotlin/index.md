---
title: "Measuring Time and Durations in Kotlin"
slug: "measuring-time-and-duration-in-kotlin"
date: "2022-02-25T09:56:31+00:00"
lastmod: "2022-02-25T09:57:22+00:00"
description: "The DurationAPI is coming out of its experimental stage and offers a nice DSL to easily work with time durations."
canonical: "https://lengrand.fr/measuring-time-and-durations-in-kotlin/"
authors:
  - "jlengrand"
image: "photo-1512856246663-647a81ef198e.jpeg"
categories:
  - "Kotlin"
tags:
related_posts:
  - "beautify-third-party-api-kotlin"
  - "building-command-line-interfaces-with-kotlin-using-picocli"
  - "build-and-test-non-blocking-web-applications-with-spring-webflux-kotlin-and-coroutines"
  - "debug-unresponsive-apps"
frozen: false
---

***TL;DR : The `Duration`API is coming out of its experimental stage and offers a nice DSL to easily work with time durations. If offers the obvious but also nice extra goodies, such as coercions and ISO/String conversions and is notably used to [calculate processing time](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/durations-and-time-measurement.md#measuretime-and-measuretimedvalue).*** ***You can directly run the code I present below [in the Kotlin playground](https://pl.kotl.in/QwksQa5h1)!***

In this article, we'll be diving into one of the other news of the [Kotlin 1.6 release](https://blog.jetbrains.com/kotlin/2021/11/kotlin-1-6-0-is-released/) : The `Duration`API coming out of Experimental and is available for all flavours of Kotlin (so JS as well 😊)! Let's look quickly together what it has to offer!

## Why a Duration API?

There are already multiple ways to calculate time in Java, so why the need for a Kotlin variant?

[From the proposal itself](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/durations-and-time-measurement.md), The Duration API aims at helping to solve the representation of the interval between two moments in time. It aims to be able to represent very precise values (up to the Nanosecond), and is **especially suited to calculated the execution time of a code block**, for example.

## Duration API: A Convenient DSL

The first, and nice thing of the duration API is that it provides a nice DSL to manipulate time durations, from `nanoseconds` up to `days`. No need to search for weeks, months or years.

Printing Durations also returns very human readable results (which is the point, so that's expected 🙂).

```kotlin
    val tenDays: Duration = 10.days
    val fiveNanoseconds = 5.nanoseconds

    println(tenDays + fiveNanoseconds) // 10d 0h 0m 0.000000005s

//    days, hours, minutes, seconds, nanoseconds are available
//    val oneWeek = 1.week ⚠️Nope, won't work⚠️
```

```
 
```

## Negative Durations \& Infinite Durations: Completely OK!

The API handles negative values nicely, as well as (positive and negative) infinite values. `ZERO` is also a thing, and it comes with no rounding errors

```kotlin
val minusOneDay = (-1).days

println(2.hours - 3.days) // -(2d 22h)
println((2.hours - 3.days).absoluteValue) // 2d 22h

println(1.days - INFINITE) // -Infinity
println(ZERO) // 0s
```

## Duration API: Many Conversion Functions

The API offers many conversion methods, from one `Duration` unit to another, but also into more common units like `Long`, or `Double`. The `inWhole` methods also allow for simple rounding of units for a better reader experience

```kotlin
println((12.hours + 30.minutes - 6.milliseconds).inWholeDays)
// 0

println((12.hours + 30.minutes).toDouble(DurationUnit.DAYS))
// 0.5208333333333334
```

It is also possible to convert one `Duration` unit to another, though it is currently still behind an experimental flag.

```kotlin
@OptIn(ExperimentalTime::class)
fun experimentalConversion(){
    val converted = convert(12.32, DurationUnit.DAYS, DurationUnit.MINUTES)
    println(converted)
}
// 17740.8
```

*Once thing I am confused about with this function though is the fact that is returns a `Double`, rather that the desired `Duration` output unit. To get `DurationUnit.MINUTES` back out my example you'd need to do it yourself : `convert(12.32, DurationUnit.DAYS, DurationUnit.MINUTES).minutes`*

## Easy Parsing In \& Out of the String Realm

One of the things I didn't expect to find was a simple way to actually parse the String representation of Durations back into actual `Duration` values.

Parsing can be done with, or without Exception handling and null safety. We can appreciate the clarity of the error message in the invalid case (quite useful for a Functional Style of programming)  

```kotlin
println(parseOrNull("11d 19h 18m")) // 11d 19h 18m (Duration)
println(parse("11w"))

Exception in thread "main" java.lang.IllegalArgumentException: Invalid duration string format: '11w'...
Caused by: java.lang.IllegalArgumentException: Unknown duration unit short name: w
```

The Duration API also provides functions to convert in and out of the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) time format, which I didn't know existed. Similarly, that can be done in a safe or unsafe manner. The format even allows to encode `INFINITY`.

```kotlin
println((12.days + 5.hours + 18.minutes).toIsoString())
// PT293H18M
println(INFINITE.toIsoString()) // PT9999999999999H
println(parseIsoStringOrNull("PT283H18M")) // 11d 19h 18m
```

```
 
```

## Comparing Durations and Enforcing Duration Intervals \& Bounds

As expected, `Durations` can be compared which each other. But **the library also offers several methods to enforce that values are within a certain range** . In the case they are not, the default coercion value is returned. Have a look at [coerceIn](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/coerce-in.html) for example.

```kotlin
//Comparisons
println(12.milliseconds > 120.nanoseconds) //true

// Returns at maximum 1.2ms (1200 microseconds)
println(12.milliseconds.coerceAtMost(1200.microseconds)) 
// 1.2ms

// Returns at least 1.2ms
println(12.milliseconds.coerceAtLeast(1200.microseconds)) 
// 12ms

// Returns the value if it's in the range, one of the bounds otherwise    
println(12.milliseconds.coerceIn(12.microseconds, 1200.microseconds)) 
// 1.2ms

// We can also easily create ranges of Durations, and use those as bounds
val bounds = 12.microseconds.rangeTo(1200.microseconds)
println(12.milliseconds.coerceIn(bounds))
```

## Duration API: In the Wild

I've had a quick look at the current Kotlin stdlib, and haven't found many usages of the Duration API just yet. It does make sense, given that the use case is arguably specific.

It is visible in the `measureTime` class though, in the `kotlin.time` package and is used exactly for what the name suggest : calculate the precise execution time of specific code blocks. You can run it, though it's still behind a feature flag!

```kotlin
@OptIn(ExperimentalTime::class)
fun experimentalPrintDuration(){
    val timeToPlop = measureTimedValue {
        println("plop")
    }

    println(timeToPlop.duration)
}
// 950.667us
```

## Conclusion

I really like that the Duration API focuses on a human readability as well as easy conversion and usage. It's also a very nice entry point into the `stdlib` if you're searching for simple contained to start with. I rarely dive into the standard library and the implementation is concise and easy to read. I'll surely be trying to find some uses for it in the short future :).

**Hope you like what you read, I do write quite a bit about Kotlin so [have a look at my other articles](__GHOST_URL__/tag/kotlin/), or ping me [on Twitter](https://twitter.com/jlengrand) if you want to react to the article!**
