---
title: "Project Amber"
description: "Project Amber is the OpenJDK project behind most of what makes modern Java look different from Java 8: records, sealed classes, pattern matching, text blocks, switch expressions and var."
url: "/java-terms-explained/project-amber/"
aliases:
  - "/pedia/project-amber/"
---

Project Amber is the OpenJDK project for **smaller, productivity-oriented language features** — the ones that change how everyday Java reads rather than how the JVM performs. If code written today looks unlike Java 8, Amber is most of the reason.

What it has delivered, each with its own entry here:

* **[Records](/java-terms-explained/records/)** (Java 16) — declare a transparent carrier for data without writing constructors, accessors, `equals`, `hashCode` and `toString`.
* **[Sealed classes](/java-terms-explained/sealed-classes/)** (Java 17) — say exactly which classes may implement an interface or extend a class, which is what lets the compiler reason about a hierarchy exhaustively.
* **[Pattern matching](/java-terms-explained/pattern-matching/)** and **[switch expressions](/java-terms-explained/switch-expressions/)** — test a value's shape and bind its parts in one step, and use `switch` as an expression with exhaustiveness checked at compile time.
* **[Text blocks](/java-terms-explained/text-blocks/)** (Java 15) — multi-line string literals, so embedded JSON, SQL and HTML stop being a wall of escapes and concatenation.
* **Local variable type inference** (`var`, Java 10).

**These features are more connected than they look.** Records, sealed classes and pattern matching are deliberately designed to work together: a sealed interface with record implementations lets a `switch` over that interface be checked for exhaustiveness, which is how Java expresses an algebraic data type. Amber shipped them separately, over several releases, but they were designed as one idea.

Amber's later work has continued in the same direction — making the on-ramp to the language gentler for people writing their first program, and reducing the ceremony a small file needs before it does anything.

## See Also

* [Records](/java-terms-explained/records/)
* [Sealed Classes](/java-terms-explained/sealed-classes/)
* [Pattern Matching](/java-terms-explained/pattern-matching/)
* [Switch Expressions](/java-terms-explained/switch-expressions/)
* [Text Blocks](/java-terms-explained/text-blocks/)
* [Preview and Incubator Features](/java-terms-explained/preview-and-incubator-features/)
* [OpenJDK Projects](/java-terms-explained/openjdk-projects/)
