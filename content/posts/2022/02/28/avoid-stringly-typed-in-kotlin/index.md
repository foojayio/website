---
title: "Avoiding Stringly-typed in Kotlin | Foojay Today"
slug: "avoid-stringly-typed-in-kotlin"
date: "2022-02-28T16:35:28+00:00"
lastmod: "2022-02-28T16:35:29+00:00"
description: "It is more beneficial to use sealed and value class instead of enumerations in Kotlin; let’s find out why."
canonical: "https://blog.frankel.ch/avoid-stringly-typed-kotlin/"
authors:
  - "nicolas-frankel"
image: "strong-50597.jpg"
categories:
  - "Kotlin"
tags:
related_posts:
  - "kotlin-delegation"
  - "an-example-of-overengineering-keep-it-wet"
  - "a-simple-service-with-spring-boot"
  - "replacing-postman-with-the-jetbrains-http-client"
enlighterjs: true
frozen: false
---

A couple of years ago, I developed an application in Kotlin based on Camunda to help me manage my conference submission workflow. It tracks my submissions in Trello and synchronizes them on Google Calendar and in a Google Sheet. Google Calendar offers a REST API. As REST APIs go, it's cluttered with `String` everywhere. Here's an excerpt of the code:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun execute(color: String, availability: String) {
    findCalendarEntry(client, google, execution.conference)?.let {
        it.colorId = color                                          // 1
        it.transparency = availability                              // 2
        client.events()
              .update(google.calendarId, it.id, it).execute()
    }
}</pre>

1. Set the event's color. Valid values are "0", "1", ... to "11"
2. Set the event's availability. Valid values are `"transparent"` and `"opaque"`

However, my experience has taught me to favor strong typing. I also want to avoid typos. I want to list some alternatives to using `String` in this post.

Constants {#h2-0-constants}
---------------------------

The oldest trick in the book, available in most languages, is to define constants. Before Java 5, developers used this alternative *a lot* as it was the only one available. It would look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">const val Default = "0"
const val Blue = "1"
const val Green = "2"
const val Free = "transparent"
const val Busy = "opaque"</pre>

We can now call the function accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">execute(Blue, Busy)</pre>

Constants help with typos. The flip side is that they cannot enforce strong typing:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">execute(Blue, Red)        // 1
execute(Free, Red)        // 2</pre>

1. Pass two colors, but the compiler is fine
2. Invert the arguments; the compiler is still fine

Type aliases {#h2-1-type-aliases}
---------------------------------

The idea behind type aliases is to alias the name of an existing type to something more meaningful.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">typealias Color = String
typealias Availability = String</pre>

With this, we can change the signature of the function:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun execute(color: Color, availability: Availability) {
    // ...
}</pre>

Unfortunately, type aliases are just cosmetic. Whatever the alias, a `String` stays a `String`. We can still write incorrect code:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">execute(Blue, Red)       // 1
execute(Free, Red)       // 1</pre>

1. Nothing has improved

Enumerations {#h2-2-enumerations}
---------------------------------

Whether in Java or Kotlin, enumerations are the first step toward strong typing. I believe most developers know about them. Let's change our code to use enums:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">enum class Color(val id: String) {
    Default("0"),
    Blue("1"),
    Green("2"),
}

enum class Availability(val value: String) {
    Free("transparent"),
    Busy("opaque"),
}</pre>

We need to change the function accordingly, both the signature and the implementation:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun execute(color: Color, availability: Availability) {
    findCalendarEntry(client, google, execution.conference)?.let {
        it.colorId = color.id                                       // 1
        it.transparency = availability.value                        // 1
        client.events()
            .update(google.calendarId, it.id, it).execute()
    }
}</pre>

1. Extract the value wrapped by the `enum`

The usage of enumerations enforces strong-typing:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">execute(Color.Blue, Availability.Busy)          // 1
execute(Color.Blue, Color.Red)                  // 2
execute(Availability.Free, Color.Blue)          // 2</pre>

1. Compile
2. Doesn't compile!

Inline classes {#h2-3-inline-classes}
-------------------------------------

A recent Kotlin feature is fully dedicated to strong typing: inline classes. An inline class wraps a single "primitive" value, such as `Int` or `String`. Picture the following class:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">data class Person(givenName: String, familyName: String)</pre>

Callers of this class would have to remember whether the first parameter is the given name or the family name. Kotlin already helps by allowing named parameters:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val p = Person(givenName = "John", familyName = "Doe")</pre>

However, we can improve the snippet above by wrapping the `String` in two different value types, one for each role.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@JvmInline value class GivenName(value: String)
@JvmInline value class FamilyName(value: String)

val p = Person(GivenName("John"), FamilyName("Doe"))</pre>

At this point, one cannot swap a given name for a family name, or *vice versa*. Likewise, we can use value classes in our example and define possible values in a companion object.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@JvmInline
value class Color(val id: String) {
    companion object {
        val Default = Color("0")
        val Blue = Color("1")
        val Green = Color("2")
    }
}

@JvmInline
value class Availability(val value: String) {
    companion object {
        val Free = Availability("transparent")
        val Busy = Availability("opaque")
    }
}

execute(Color.Blue, Availability.Busy)          // 1
execute(Color.Blue, Color.Red)                  // 2
execute(Availability.Free, Color.Blue)          // 2</pre>

1. Compile
2. Doesn't compile!

Sealed classes {#h2-4-sealed-classes}
-------------------------------------

Sealed classes are another possible way to enforce strong typing. The limitation is we need to define all subclasses of a sealed class in the same file. There can't be any inheritance by third parties. In effect, it makes the class `open` for your code and `final` for client code.

Instead of defining a type and several instances of it as in value classes, we define the different types directly.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">sealed class Color(val id: String) {
    object Default: Color("0")
    object Blue:    Color("1")
    object Green:   Color("2")
}

sealed class Availability(val value: String) {
    object Free : Availability("transparent")
    object Busy : Availability("opaque")
}

execute(Color.Blue, Availability.Busy)          // 1
execute(Color.Blue, Color.Red)                  // 2
execute(Availability.Free, Color.Blue)          // 2</pre>

1. Compile
2. Doesn't compile!

Note that I defined the objects in their respective parent classes. Depending on your context, you may want to make them top-level instead.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">sealed class Color(val id: String)
object Default: Color("0")
object Blue:    Color("1")
object Green:   Color("2")

sealed class Availability(val value: String)
object Free : Availability("transparent")
object Busy : Availability("opaque")

execute(Blue, Busy)</pre>

Conclusion {#h2-5-conclusion}
-----------------------------

Kotlin offers several options to enforce strong typing on one's APIs: enumerations, value classes, and sealed classes.

While most developers are pretty comfortable with enumerations, I'd advise considering value and sealed classes as they bring additional benefits to the table.

**To go further:**

* [Enum classes](https://kotlinlang.org/docs/enum-classes.html)
* [Inline classes](https://kotlinlang.org/docs/inline-classes.html)
* [Sealed classes](https://kotlinlang.org/docs/sealed-classes.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/avoid-stringly-typed-kotlin/) on February 20^nd^, 2022*

*[BPMN]: Business Process Management Notation
