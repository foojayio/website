---
title: "The JVM Options Explorer"
description: "Every JVM command-line flag, for every JDK version and every vendor, in one searchable table — where it came from, what it defaults to, and which release added it."
url: "/jvm-options/"
frozen: false
---

**Ever needed to know whether a `-XX:` flag exists in the JDK you are actually running?**

**Wanted to see which options a JDK upgrade added, deprecated or removed?**

**[chriswhocodes.com/vm-options-explorer.html](https://chriswhocodes.com/vm-options-explorer.html) is the site to have in your bookmarks!**

## History

Created and maintained by [Chris Newland](/today/author/chriswhocodes/), the VM Options Explorer answers a question the `java` command itself is unhelpful about: what flags does this JVM understand, and what do they do? `java -XX:+PrintFlagsFinal -version` will list them, but with no descriptions, no history, and no way to compare one release with another.

The tool takes the honest route to the answer: it **reads the OpenJDK source**. The flags are declared in the JVM's own `globals.hpp` and its siblings, so parsing those files produces a list that is complete by construction and cannot drift from the implementation the way hand-written documentation does.

For each option you get:

| Column | What it tells you |
|---|---|
| Name | The flag itself, searchable |
| Since / Deprecated | The release that introduced it, and the one that deprecated it |
| Type | `bool`, `int`, `uintx`, `ccstr`, `double`… |
| Default | The value in effect if you set nothing |
| Availability | `product`, `diagnostic`, `experimental` or `develop` — i.e. whether you are allowed to rely on it |
| Component | Which part of the JVM owns it (`gc`, `cds`, `compiler`, `runtime`…) |
| OS / CPU | Whether it exists only on some platforms |
| Description | The JVM's own description of the option |
| Defined in | The source file it comes from, so you can go and read the code |

That `Availability` column is the one worth internalising. A flag marked `experimental` or `diagnostic` needs `-XX:+UnlockExperimentalVMOptions` or `-XX:+UnlockDiagnosticVMOptions` before the JVM will accept it, and it carries no compatibility promise into the next release. Plenty of advice found online quietly omits that.

## Not just OpenJDK

The same treatment is applied to every JDK that publishes its source, which is what makes the tool unusual: options are listed **per version and per vendor**, from JDK 6 through the releases currently in development.

Alibaba Dragonwell, Amazon Corretto, Azul Zulu, BellSoft Liberica, Eclipse Temurin, GraalVM (both the JDK-based distribution and Native Image), JetBrains Runtime, Microsoft Build of OpenJDK, Eclipse OpenJ9, Oracle JDK and SAP SapMachine are all covered — so "does my vendor's JDK have this flag?" is a question you can actually look up rather than guess at.

There is also a [changes between OpenJDK versions](https://chriswhocodes.com/hotspot_option_differences.html) view, which is the fastest way to find out what a JDK upgrade did to your carefully tuned startup script.

## Popular versions

* [OpenJDK 25 options](https://chriswhocodes.com/hotspot_options_openjdk25.html)
* [OpenJDK 21 options](https://chriswhocodes.com/hotspot_options_openjdk21.html)
* [OpenJDK 17 options](https://chriswhocodes.com/hotspot_options_openjdk17.html)
* [OpenJDK 11 options](https://chriswhocodes.com/hotspot_options_openjdk11.html)
* [OpenJDK 8 options](https://chriswhocodes.com/hotspot_options_openjdk8.html)

## More from the same author

Chris maintains a whole shelf of OpenJDK tooling, most of it built on the same idea of reading the source or the logs rather than trusting the documentation:

* [VM Intrinsics Explorer](https://chriswhocodes.com/vm-intrinsics-explorer.html) — the methods HotSpot replaces with hand-written machine code
* [GC Explorer](https://chriswhocodes.com/gc-explorer.html) — garbage collector flags and their interactions
* [JITWatch](https://github.com/AdoptOpenJDK/jitwatch) — a log analyser for the HotSpot JIT compiler
* [JEPMap](https://chriswhocodes.com/jepmap.html) and [JEPSearch](https://chriswhocodes.com/jepsearch.html) — [JEPs](/pedia/jep-jdk-enhancement-proposal/) mapped to their [OpenJDK projects](/pedia/openjdk-projects/), and full-text searchable
* [hsdis builds](https://chriswhocodes.com/hsdis/) — the disassembler plugin, prebuilt, so you can read the assembly HotSpot generated
* [Byte-Me](https://byte-me.dev) — Java source to [bytecode](/pedia/bytecode/), side by side

If you tune JVMs, debug a performance problem, or just want to know what that flag in a Dockerfile you inherited actually does, this is one of those quietly indispensable resources you will reach for more often than you expect.
