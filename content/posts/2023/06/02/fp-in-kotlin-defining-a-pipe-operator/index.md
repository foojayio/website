---
title: "FP in Kotlin: Defining a Pipe Operator"
date: "2023-06-02T15:18:13+00:00"
lastmod: "2023-06-02T15:18:50+00:00"
description: "There's no support for the pipe operator |> in Kotlin, so we have to come up with a custom and clean implementation for this function."
authors:
  - "tobias-briones"
image: "Favicon-3-2.png"
categories:
  - "Kotlin"
related_posts:
  - "kotlin-delegation"
  - "comparison-fault-tolerance-libraries"
  - "building-command-line-interfaces-with-kotlin-using-picocli"
  - "handling-null-optional-and-nullable-types"
frozen: false
---

There's no support for the pipe operator `|>` in Kotlin, so we have to come up with a custom and clean implementation for this function.

## Defining a Pipe Operator

Next, one consistent and clean implementation is given for a pipe operator in Kotlin.

#### Finding a Suitable Symbol

First, I wanted to use the `|>` pipe symbol commonly used in functional languages, but `>` is not a supported character: `Name contains illegal characters: >`.

Then, I tried to use the other common `|` pipe symbol with no full success:  
`Name contains characters which can cause problems on Windows: |`.

To avoid issues, I either had to design a simple symbol or use a verbose `pipe `operator name.

So, I took my design from [How I Standardized Hyphen and Pipe Symbols on File Names](https://blog.mathsoftware.engineer/how-i-standardized-hypen-and-pipe-symbols-on-file-names) where I designed the standards for (among others) the pipe operator on file names. Notice that file systems also require simple symbols to work with, so the standard from my previous article was what I was looking for.

Now that **the pipe operator symbol is established to `---`**, the implementation is next.

#### Language Features

It's required to employ:

* [Infix Functions](https://kotlinlang.org/docs/functions.html#infix-notation).
* [Lambdas](https://kotlinlang.org/docs/coding-conventions.html#lambdas).
* [Basic Generics](https://kotlinlang.org/docs/generics.html).

It's useful for the given example that shows the operator usage:

* [Data Classes](https://kotlinlang.org/docs/data-classes.html).
* [Value Classes](https://kotlinlang.org/docs/inline-classes.html).
* [String Interpolation](https://kotlinlang.org/docs/idioms.html#string-interpolation).
* [Basic Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-regex.html).

#### Operator Definition

First, the `---` operator is defined:

`Pipe.kt`

```kotlin
infix fun <X, Y> X.`---`(f: (X) -> Y): Y = f(this)
```

**Definition of the Pipe ("---") Operator**

Where:

* `---` is a special function name escaped within backticks "\`\`" that  
  **denotes the "pipe operator"** as described before.
* Two generics `X` and `Y` are defined to **represent the LHS and RHS** of the  
  operator.
* The `infix` notation is used on the extension function `---` defined for  
  all `x in X`. There are two parts here:
  * The **extension function**.
  * The **infix which provides the syntax sugar**.
* `forall x in X, y in Y`, the lambda `f: (X) -> Y` is defined as the  
  **operator argument** . So, we have:
  * The constant (LHS) is given by `X`.
  * The function (RHS) to be applied is given by `f:X \to Y`.
* `---` **returns** `Y` ---result of applying

```
f
```

  to some

```
x
```

  .
* `---`'s **image is defined as `f(this)`** where `this` is an element of `X`.

That was the definition of the pipe operator.

#### Usage Example

Now, to address a concrete example to use this new feature, I implemented a  

basic DSL for an `Article` domain type.

`Pipe.kt`

```kotlin
data class Article(val title: Title, val content: String)

@JvmInline
value class Title(val value: String) {
    override fun toString(): String = value
}

val title: (String) -> Title = { Title(it) }
```

**Definition of an "Article" DSL**

So, to test the code, I will add a user input title that is not cleaned, some  

content, and I'll also define more functions with **transformations** , so we can  
*pipe them*.

`fun main | Pipe.kt`

```kotlin
val inputTitle = "FP in Kotlin: Defining a Pipe   Operator  "
val inputContent = "Lorem ipsum dolor sit amet..."
```

**Sample User Input for Example Snippet**

Then, we'll have some useful example transformations:

`fun main | Pipe.kt`

```kotlin
val clean: (String) -> String = { it.trim().replace("\\s+".toRegex(), " ") }
val uppercase: (String) -> String = { it.uppercase() }
val markdownTitle: (String) -> String = { "# $it" }
val formatTitle: (String) -> String =
    { it `---` clean `---` uppercase `---` markdownTitle }
```

**Transformations for Example Snippet**

Finally, we can create an `Article` with `title` and `content`:

`fun main | Pipe.kt`

```kotlin
print(
    Article(
        inputTitle `---` formatTitle `---` title,
        inputContent
    )
)
```

**Building an "Article" for the Example Snippet**

With this, a title `String` can be transformed into a `Title` domain type by  

piping it to the transformations declared:

* In `formatTitle`: `clean`, `uppercase`, `markdownTitle`.
* Then (in a higher-level view), applying `formatTitle` to the raw `String`  
  input and transforming this value into a `Title` via the constructor `title`:  
  `inputTitle `---` formatTitle `---` title`.

The program's output is:

```
Article(
    title=# FP IN KOTLIN: DEFINING A PIPE OPERATOR,
    content=Lorem ipsum dolor sit amet...
)
```

**Program's Output (Formatted)**

The example code is [here](https://blog.mathsoftware.engineer/fp-in-kotlin/kotlin/Pipe.kt/).

#### Functional Language Design

Notice that we're using backticks to define the infix operator like functional languages like Purescript do and [employ this feature](https://leanpub.com/purescript/read#leanpub-auto-infix-operators). It's also preferred to use normal identifier names (i.e., alphabetic) instead of predefined symbols (e.g., `+`) to avoid *abusing the syntax*.

As said above, the pipe can be commonly denoted by `|` or `|>`. Since `pipe` is a universally abstract concept, it must be **terse** , so defining a **symbol** for it is a good design. For user-specific languages, alphabetic identifiers should be used, as said above.

#### Custom Pipe in Kotlin

This was the design of a custom pipe operator that can be used in Kotlin, and some insights about functional languages as well.

## Options for a Pipe Operator in Kotlin

As developed before, we faced many constraints in Kotlin for getting a language design that enables us to use the pipe operator.

So, we have open possibilities to add this feature to our codebase, and I developed one ("---") that keeps consistent with the newest MathSwe standards I had defined before.

*Originally published at [blog \| mathsoftware.engineer](https://blog.mathsoftware.engineer/fp-in-kotlin#pipe-operator)*
