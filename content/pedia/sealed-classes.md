---
title: "Sealed Classes"
description: "Sealed classes, introduced as a preview in Java 15 and finalised in Java 17, let you explicitly control which classes or interfaces are permitted to extend or implement a given type. The sealed modifier on a class is paired with ..."
url: "/pedia/sealed-classes/"
frozen: false
---

Sealed classes, introduced as a preview in Java 15 and finalised in Java 17, let you explicitly control which classes or interfaces are permitted to extend or implement a given type. The `sealed` modifier on a class is paired with a `permits` clause listing the allowed subtypes.  

```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}
```

Each permitted subtype must itself be declared `final`, `sealed`, or `non-sealed`. This makes the type hierarchy closed and knowable at compile time.

The main benefit is enabling exhaustive `switch` expressions. If you switch over a sealed `Shape`, the compiler can verify that you have handled every permitted subtype and will warn you if you add a new subtype without updating existing switch statements. This eliminates an entire class of bugs that previously required discipline and code review to catch.

Sealed classes work best in combination with records (for the leaf types) and pattern matching (for the switch logic).

## See Also

* [Switch Expressions](/pedia/switch-expressions/)
* [Records](/pedia/records/)
* [Pattern Matching](/pedia/pattern-matching/)
* [Project Amber](/pedia/project-amber/)
