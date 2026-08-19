---
title: "Records"
description: "Records, introduced as a preview in Java 14 and finalised in Java 16, are a concise way to declare classes whose primary purpose is to hold immutable data. Declaring record Point(int x, int y) {} gives you a class with ..."
url: "/pedia/records/"
frozen: false
---

Records, introduced as a preview in Java 14 and finalised in Java 16, are a concise way to declare classes whose primary purpose is to hold immutable data. Declaring `record Point(int x, int y) {}` gives you a class with a constructor, `x()` and `y()` accessor methods, `equals()`, `hashCode()`, and `toString()` — all generated automatically by the compiler.

Records are transparently immutable: the fields are `final` and can only be set via the constructor. You can add your own methods, implement interfaces, and define compact constructors (for validation), but you cannot add instance fields beyond those declared in the record header, and records cannot extend other classes.

Records pair naturally with sealed classes and pattern matching. Together, these features make it straightforward to model algebraic data types in Java — a style of programming sometimes called data-oriented programming.
