---
title: "Value Objects (Project Valhalla)"
description: "Value objects are a new kind of Java class being developed under Project Valhalla — available in preview in recent Java releases — that behave like primitive types at the JVM level: they have no object identity, are always compared ..."
url: "/pedia/value-objects-project-valhalla/"
frozen: false
---

Value objects are a new kind of Java class being developed under **Project Valhalla** — available in preview in recent Java releases — that behave like primitive types at the JVM level: they have no object identity, are always compared by state rather than reference, and can be stored as flat data in arrays and fields rather than as heap references.

In standard Java, every object has an **identity** — two distinct `new Point(1, 2)` objects are `!=` even if they hold the same data. This identity requires each object to live on the heap with a header, and arrays of objects store references (pointers) rather than the data itself. For small, data-centric types (coordinates, complex numbers, currency amounts), this is wasteful: each object adds 12–16 bytes of header overhead plus a pointer in every collection.

A value class declared with the `value` modifier has no identity. The JVM can store its fields inline — directly in the containing array or object — eliminating the header and the pointer indirection. For a million-element array of `Point` values, this can halve memory usage and dramatically improve cache performance.

```java
value class Point {
    int x;
    int y;
    // equals() and hashCode() are derived from fields automatically
}
```

The trade-off is that value objects cannot be used as synchronisation monitors (`synchronized(point)` is a compile error) and cannot be null in the same way as reference types (a new `?` syntax is being developed for nullable value types).

Project Valhalla is a long-running effort. Its current state can be tracked at [openjdk.org/projects/valhalla](https://openjdk.org/projects/valhalla).

## See Also

* [OpenJDK Projects](/pedia/openjdk-projects/)
* [Records](/pedia/records/)
* [Sealed Classes](/pedia/sealed-classes/)
