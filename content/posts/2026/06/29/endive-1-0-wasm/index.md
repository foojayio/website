---
title: "Endive 1.0 Is Here: Wasm on the JVM Ships Under the Bytecode Alliance"
slug: "endive-1-0-wasm"
date: "2026-06-29T00:11:00+00:00"
lastmod: "2026-06-29T10:32:40+00:00"
description: "A few weeks ago, we wrote about a new generation of Java libraries powered by WebAssembly. SQLite, QuickJS, Protocol Buffers, the Ruby Prism parser, all - by Andrea Peruffo"
authors:
  - "andrea-peruffo"
image: "log_plus_text_green-1.png"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "a-new-generation-of-java-libraries-is-born-wasm-becomes-the-implementation-detail"
  - "foojay-podcast-98"
  - "playing-with-wasm-on-docker"
  - "rust-jvm"
enlighterjs: true
frozen: false
---

A few weeks ago, we wrote about [a new generation of Java libraries](https://foojay.io/today/a-new-generation-of-java-libraries-is-born-wasm-becomes-the-implementation-detail/) powered by WebAssembly. SQLite, QuickJS, Protocol Buffers, the Ruby Prism parser, all compiled to Wasm and shipped as regular JARs. No JNI, no platform-specific binaries. Wasm as an implementation detail.

Today, the runtime that makes it all work has its first official release under the [Bytecode Alliance](https://bytecodealliance.org/). **[Endive 1.0](https://endive.run/blog/endive-1.0)** is available on Maven Central.

What Shipped {#h2-0-what-shipped}
---------------------------------

Endive continues the work started as [Chicory](https://github.com/dylibso/chicory), the pure-Java WebAssembly runtime that has been powering this ecosystem since 2023. Same API, same community, same codebase. The move to the Bytecode Alliance is organizational, not technical. For details on the fork and governance, see the [announcement article](https://bytecodealliance.org/articles/endive-and-the-next-chapter-of-webassembly-on-the-jvm).

For existing Chicory users, migrating is a find-and-replace. Maven coordinates move from `com.dylibso.chicory` to `run.endive`, Java packages follow the same pattern. The [migration guide](https://endive.run/docs/migration/from-chicory) covers the full list.

Beyond the rename, Endive 1.0 ships new technical work.

WasmGC: The Libraries Get Smarter {#h2-1-wasmgc-the-libraries-get-smarter}
--------------------------------------------------------------------------

The previous article focused on wrapping C and Rust libraries. Those languages compile to Wasm straightforwardly because they manage their own memory. But a growing number of languages target the WasmGC proposal instead: Kotlin/Wasm, Dart, and others. Google Sheets already runs its Java-based calculation engine through WasmGC in production. Endive passes the full WasmGC spec testsuite, and the project testsuite already includes a Kotlin/Wasm hello-world to validate the integration end to end:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="kotlin" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">fun main() {
    println("Hello from Kotlin via WASI")
    println("Current 'realtime' timestamp is: ${wasiGetTime(0)}")
}

@WasmImport("wasi_snapshot_preview1", "clock_time_get")
private external fun wasiRawClockTimeGet(clockId: Int, precision: Long, resultPtr: Int): Int

@OptIn(UnsafeWasmMemoryApi::class)
fun wasiGetTime(clockId: Int): Long = withScopedMemoryAllocator { allocator -&gt;
    val rp0 = allocator.allocate(8)
    val ret = wasiRawClockTimeGet(
        clockId = clockId,
        precision = 1,
        resultPtr = rp0.address.toInt()
    )
    check(ret == 0) {
        "Invalid WASI return code $ret"
    }
    (Pointer(rp0.address.toInt().toUInt())).loadLong()
}
</pre>

Standard Kotlin, targeting WasmGC and WASI, running through Endive with no changes.

What Endive 1.0 adds is the host integration. When GC-managed objects (structs, arrays, `externref` values) cross the boundary between Wasm and Java, they are now tracked by the JVM garbage collector directly. No separate store, no manual lifecycle management. The annotation processor supports `externref` as plain `Object`, so host functions work naturally with GC types.

This is the foundation for the next wave of Wasm-powered Java libraries, ones built from languages that rely on garbage collection rather than manual memory management. If you already use WasmGC types at the host boundary, the runtime will guide you through a small update. See the [release notes](https://endive.run/blog/endive-1.0) for details.

Tail Call Optimizations {#h2-2-tail-call-optimizations}
-------------------------------------------------------

CPython 3.14 adopted tail calls in its interpreter loop. The Endive compiler now optimizes tail-call dispatch by eliminating unnecessary stack frame allocation. If you're running Python UDFs in [Trino](https://trino.io/docs/current/udf/python.html), this is where you feel it.

One More JAR: tree-sitter for Java {#h2-3-one-more-jar-tree-sitter-for-java}
----------------------------------------------------------------------------

In the previous article, every example followed the same shape. Take a proven native library. Compile it to Wasm. Wrap it in a JAR. Ship it on Maven Central.

It happened again.

[tree-sitter](https://tree-sitter.github.io/tree-sitter/) is the incremental parsing library behind Neovim, Zed, GitHub's code navigation, and a growing list of editors and tools. It supports dozens of language grammars and produces concrete syntax trees fast enough for real-time editor use. It's written in C, and until now, using it from Java meant JNI bindings and platform-specific native libraries.

[treesitter4j](https://github.com/roastedroot/treesitter4j) compiles tree-sitter and its grammars (Java, XML, YAML, HTML, Markdown, JSON, Properties) to a single Wasm module and wraps them in a Java API. Add a Maven dependency, parse any supported language, get a syntax tree.

For example, the Snowdrop [migration tool](https://github.com/snowdrop/migration-tool), which helps teams migrate Spring Boot applications to Quarkus, [just adopted treesitter4j](https://github.com/snowdrop/migration-tool/pull/339) as one of its code scanners. It uses tree-sitter queries to find Java classes, annotations, imports, and property files, building a structural map of the codebase before migration begins. The entire grammar ecosystem of tree-sitter is now available to any Java tool as a regular dependency.

Meanwhile, [Reshapr](https://github.com/reshaprio/reshapr), the open-source no-code MCP server for AI-native API access, is [adding JavaScript scripting](https://github.com/reshaprio/reshapr/issues/199) for custom tool orchestration, powered by QuickJs4J running QuickJS through Wasm. No embedded V8, no GraalJS needed.

Another battle-tested native library, another JAR on the classpath, another use case that would have been painful with JNI.

Beyond Libraries: Endive as a Host {#h2-4-beyond-libraries-endive-as-a-host}
----------------------------------------------------------------------------

The library-wrapping story is just the beginning. At the [wasmCloud community call](https://www.youtube.com/watch?v=QyVyD37cvrw) in May, Bailey Hayes built a wasmCloud host in Java using Endive, scheduling workloads over NATS. The demo then embedded Wasm functions alongside plain Java handlers in an [Eclipse Vert.x](https://vertx.io/) app: two plain Java routes, two Wasm routes, one HTTP server, one JVM. The Wasm invoker was three lines of code. The Java caller had no idea it was talking to WebAssembly.

If you want to see the full picture of where this is heading, the [JNation 2026 talk](https://www.youtube.com/watch?v=GiaJiiyuw_Y) walks through the ecosystem end to end.

To stress-test the runtime, we compiled javac itself to Wasm via [GraalVM WebImage](https://www.graalvm.org/jdk25/reference-manual/web-image/) and ran it back in Endive. It produced valid `.class` files. We also ran a [Scala.js](https://www.scala-js.org/) WasmGC hello-world through the build-time compiler end to end. These aren't meant for production. They're the kind of exercises that shake out edge cases and prove the runtime handles real-world complexity. The [examples are on GitHub](https://github.com/andreaTP/metacircular-java-wasm).

What's Next {#h2-5-what-s-next}
-------------------------------

Community members have started prototyping [Component Model](https://github.com/roastedroot/endive-cm) support, the standard for typed, language-neutral interfaces between Wasm components. Cranelift-based native compilation is also in the works, bringing near-native execution speed while preserving the pure-Java packaging story.

Get Started {#h2-6-get-started}
-------------------------------

Documentation, getting started guides, and the full migration reference are at [endive.run](https://endive.run/docs/). The source lives at [github.com/bytecodealliance/endive](https://github.com/bytecodealliance/endive).

If you're building something with Endive, or thinking about wrapping a native library for the JVM, we'd love to hear about it. [Join the conversation on Zulip](https://bytecodealliance.zulipchat.com/#narrow/stream/endive).

More information about this project: [Foojay Podcast #98](https://foojay.io/today/foojay-podcast-98/).

{{< youtube BCbJwLphhsU >}}
