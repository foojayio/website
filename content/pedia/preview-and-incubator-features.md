---
title: "Preview and Incubator Features"
description: "Java ships new language features and APIs in two provisional states before they are finalised: Preview features (for language changes and JVM features) and Incubator modules (for new APIs). Both mechanisms exist to gather real-world feedback before a feature is ..."
url: "/pedia/preview-and-incubator-features/"
frozen: false
---

Java ships new language features and APIs in two provisional states before they are finalised: **Preview features** (for language changes and JVM features) and **Incubator modules** (for new APIs). Both mechanisms exist to gather real-world feedback before a feature is locked in.

## Preview Features

A preview feature is a complete, fully specified language or JVM feature that is available in a JDK release but **not yet finalised**. Feedback from real-world use may cause the feature to change between releases before it becomes permanent. Preview features:

* Require explicit opt-in at compile time and runtime: `--enable-preview`
* Are described in a JEP with a "Preview" scope
* May change, be refined, or (rarely) be withdrawn in future releases
* Cannot be used in production code that ships without the `--enable-preview` flag

Examples: pattern matching for `instanceof` (preview in Java 14–15, finalised Java 16), records (preview Java 14–15, finalised Java 16), virtual threads (preview Java 19–20, finalised Java 21).

## Incubator Modules

An incubator module is a provisional API module (`jdk.incubator.*`) shipped in a JDK release so that the community can experiment with it. Incubator modules:

* Must be explicitly added to the module graph: `--add-modules jdk.incubator.xxx`
* May change API signatures between releases without deprecation warnings
* Graduate to standard modules, change significantly, or be dropped based on feedback

The Vector API (for SIMD-style operations using Java) has been in incubation since Java 16; it has been progressively refined and is expected to finalise once Project Valhalla delivers the value type support it depends on.

## See Also

* [Records](/pedia/records/)
* [Sealed Classes](/pedia/sealed-classes/)
* [Pattern Matching](/pedia/pattern-matching/)
