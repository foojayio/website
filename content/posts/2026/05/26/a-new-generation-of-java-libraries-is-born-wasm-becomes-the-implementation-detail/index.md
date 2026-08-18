---
title: "A New Generation of Java Libraries Is Born: Wasm Becomes the Implementation Detail"
date: "2026-05-26T13:00:47+00:00"
lastmod: "2026-05-27T11:23:08+00:00"
description: "If you're running JRuby in production, you're running WebAssembly. If TrinoDB is evaluating your Python UDFs, that's WebAssembly too. If Microcks is - by Andrea Peruffo"
authors:
  - "andrea-peruffo"
image: "Screenshot-2026-05-27-at-13.22.27.png"
categories:
  - "Tools"
related_posts:
frozen: false
---

If you're running JRuby in production, you're running WebAssembly. If TrinoDB is evaluating your Python UDFs, that's WebAssembly too. If Microcks is running JavaScript dispatchers to route your mock API responses, WebAssembly is doing the work.

You didn't install a native binary. You didn't configure JNI. You didn't cross-compile anything for your target platform. It just works, because the Wasm module is hiding inside a regular JAR on your classpath.

This is the same pattern that already powers the web. Every day, millions of people use Google Sheets, Figma, or Photoshop without knowing that WebAssembly is what makes those applications possible. Wasm is already invisible infrastructure in the browser. Now the same thing is happening on the JVM.

## The problem every Java developer knows

Some of the best libraries in the world are written in C, C++, Rust, or Go. SQLite. QuickJS. Protocol Buffers. OPA. They are battle-tested, widely deployed, and actively maintained. At some point, every Java project wants to use one of them.

The options have never been great. You can rewrite the library in Java, but that's expensive, error-prone, and perpetually behind the upstream. Or you can reach for JNI and ship platform-specific native binaries. That path comes with its own pain: your application becomes OS and architecture-specific. Execution leaves the safety and observability of the JVM. Distribution turns into a matrix of `.so`, `.dylib`, and `.dll` files. Debugging across the JNI boundary is nobody's idea of fun. And if you're in a restrictive environment, such as a locked-down container or a platform that blocks native library loading, JNI may not be an option at all.

For decades, this trade-off felt permanent. You could have the capability, or you could have the JVM's guarantees. Not both.

## What if the library just ran inside the JVM?

WebAssembly changes this equation. Take a proven C or Rust library, compile it to Wasm, and run it *within* JVM boundaries. You keep everything the JVM gives you: guaranteed memory safety, fault isolation, platform independence, advanced JIT, observability, and the "write once, run anywhere" promise. The Wasm module becomes just another artifact inside your JAR, an implementation detail that your users never need to think about.

This isn't a theoretical possibility. It's happening right now, in production, across a growing ecosystem of Java libraries.

## The ecosystem: it's already here

**SQLite4j** is a pure-Java JDBC driver for SQLite. The real SQLite, compiled to WebAssembly and embedded in a JAR. Before: you shipped platform-specific native binaries for every OS and architecture your users might run on, and hoped your CI matrix covered them all. After: one JAR, everywhere. Same SQLite, zero native dependencies. Add a Maven dependency and you have an embedded relational database.

**QuickJs4j** puts a full JavaScript runtime inside your Java application. QuickJS, a small, fast, standards-compliant JS engine, is compiled to Wasm and wrapped in a clean Java API. It's already powering real production systems. [Microcks](https://microcks.io/), a CNCF project, uses it for JavaScript dispatchers that control how mock responses are selected based on request headers, payloads, and parameters. It works across all Microcks distributions, including native-compiled images. Before: reach for Nashorn or Rhino (both deprecated), or GraalJS (heavy dependency). After: a lightweight, sandboxed JavaScript engine in a single dependency.

**pglite4j** embeds PGlite, a lightweight PostgreSQL build, inside the JVM via WebAssembly. In-process, no external server, no native binaries. Useful for testing, local development, or anywhere you need Postgres compatibility without running a separate process.

**Protobuf4J** compiles protocol buffers without the massive native `protoc` toolchain. Compile-time dependencies drop from 403MB to roughly 90MB. Same output. Fraction of the footprint.

**JRuby** uses Prism, the Ruby parser, compiled to Wasm and consumed from Java without JNI. The JRuby team didn't have to rewrite a parser or maintain platform-specific binaries. They added a dependency.

**TrinoDB** runs user-defined Python functions inside the query engine, sandboxed via WebAssembly. Users write Python UDFs; Trino executes them safely within JVM boundaries.

And the list keeps growing: **Debezium** single message transforms, **OpenFeature** flag evaluation, **OPA** policy engine running in-process (no network hop to an external sidecar), and more.

The pattern is always the same. Take a proven library. Compile it to Wasm. Wrap it in a JAR. Ship it on Maven Central. Your users add a dependency and get the capability. No JNI, no platform matrix, no native binary management. **Wasm becomes the implementation detail.**

## Endive: a new chapter for WebAssembly on the JVM

This entire ecosystem was built on [Chicory](https://github.com/dylibso/chicory), a pure-Java WebAssembly runtime started in September 2023. Within two years it went from experiment to infrastructure, powering production systems across multiple organizations and proving that WebAssembly fits naturally into Java applications.

Now the project is entering its next chapter. **Endive** is a fork of Chicory and a [Bytecode Alliance](https://bytecodealliance.org/) Hosted project, a vendor-neutral home where the project can grow openly. The community, the vision, and the code carry forward under the same Apache-2.0 license. What changes is that the project now belongs to its ecosystem.

Why the [Bytecode Alliance](https://bytecodealliance.org/)? The BA is the foundation behind [Wasmtime](https://wasmtime.dev/), [WASI](https://wasi.dev/), and the [Component Model](https://component-model.bytecodealliance.org/): the core runtime, system interface, and component standard that define WebAssembly outside the browser. If Wasm is to become a durable cross-language component format, the JVM, one of the world's major managed runtime ecosystems, needs to be part of that story. Endive gives the BA community a place to collaborate on JVM integration directly.

The first major milestone is integrating the experimental [Redline](https://github.com/roastedroot/chicory-redline) compiler into the mainline. Redline uses [Cranelift](https://cranelift.dev/), the same compiler backend behind [Wasmtime](https://wasmtime.dev/), to compile Wasm to native machine code. On Java 25+, it achieves this with zero additional dependencies, thanks to Panama's Foreign Function \& Memory API becoming a standard part of the platform. You get native-speed execution *and* the pure-Java packaging story.

Further out, the roadmap includes WasmGC, enabling the Java garbage collector to manage Wasm object references, and [Component Model](https://component-model.bytecodealliance.org/) support for consuming cross-language components through typed interfaces.

For the full story on the fork, the governance model, and the migration path for existing Chicory users, see the [Bytecode Alliance announcement](https://bytecodealliance.org/articles/endive-and-the-next-chapter-of-webassembly-on-the-jvm).

## Compose, don't rewrite

The future isn't about rewriting native libraries in Java. It isn't about wrestling with JNI or maintaining platform-specific builds. It's about *composing* your application from components written in any language (Rust, C, Go, Python, JavaScript) through WebAssembly.

One runtime. Any language. No rewrites.

Java has always been about writing code once and running it anywhere. WebAssembly extends that promise to *any codebase in any language*. With Endive, the JVM becomes a place where established libraries, regardless of what language they were written in, can run safely, portably, and at native speed.

Wasm becomes the invisible glue. The implementation detail that makes it all work, without you even noticing.

## Get involved

The [Endive repository](https://github.com/bytecodealliance/endive) is already available. The first release will prioritize strong continuity with Chicory, preserving compatibility and documenting migration steps clearly.

If you care about Java, WebAssembly, secure plugin systems, cross-language components, or the future of portable software, [come build with us](https://github.com/bytecodealliance/endive).
