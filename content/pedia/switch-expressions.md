---
title: "Switch Expressions"
description: "Switch expressions, finalised in Java 14 (JEP 361), extend the traditional switch statement to work as an expression — producing a value — and introduce a cleaner arrow-case syntax that eliminates fall-through. The new arrow form uses -> instead of ..."
url: "/pedia/switch-expressions/"
frozen: false
---

Switch expressions, finalised in Java 14 (JEP 361), extend the traditional `switch` statement to work as an expression — producing a value — and introduce a cleaner arrow-case syntax that eliminates fall-through.

The new arrow form uses `->` instead of `:` and does not fall through to subsequent cases:

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    case THURSDAY, SATURDAY     -> 8;
    case WEDNESDAY              -> 9;
};
```

When more than one statement is needed, a `yield` expression returns the value from a block:

```java
int result = switch (input) {
    case "one" -> 1;
    default -> {
        System.out.println("Unknown: " + input);
        yield -1;
    }
};
```

Switch expressions require exhaustiveness: the compiler verifies that all possible input values are handled. For `enum` types and (in Java 21+) sealed class hierarchies, the compiler enforces that every case is covered without a default, catching missing cases at compile time.

Switch expressions laid the groundwork for [pattern matching in switch](https://foojay.io/pedia/pattern-matching/) (finalised in Java 21), which allows patterns — not just constants — to appear in case labels.

See also: [Pattern Matching](https://foojay.io/pedia/pattern-matching/), [Sealed Classes](https://foojay.io/pedia/sealed-classes/)
