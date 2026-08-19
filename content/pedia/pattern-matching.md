---
title: "Pattern Matching"
description: "Pattern matching is a set of language features that allow you to test the structure or type of a value and extract components from it in a single, concise expression. Java has added pattern matching incrementally since Java 14, progressively ..."
url: "/pedia/pattern-matching/"
frozen: false
---

Pattern matching is a set of language features that allow you to test the structure or type of a value and extract components from it in a single, concise expression. Java has added pattern matching incrementally since Java 14, progressively replacing verbose, error-prone type-checking idioms.

The simplest form is pattern matching for `instanceof` (finalised in Java 16): instead of writing `if (obj instanceof String) { String s = (String) obj; }`, you write `if (obj instanceof String s)`. The cast and variable declaration are fused into the pattern.

The more powerful form is pattern matching in `switch` expressions and statements (finalised in Java 21). Combined with sealed classes, a `switch` can exhaustively match every subtype of a sealed hierarchy, and the compiler verifies at compile time that all cases are covered. This makes data-oriented programming in Java significantly more concise and safe.
