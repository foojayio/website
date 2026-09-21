---
title: "On programming languages, targets, and platforms"
date: "2026-06-24T17:22:32+00:00"
lastmod: "2026-09-21T05:55:12+00:00"
description: "I started as a Java developer, but for some time now, I have broadened my horizons. Recently, I thought about how early languages were dedicated to a…"
canonical: "https://blog.frankel.ch/programming-languages-targets-platforms/"
authors:
  - "nicolas-frankel"
image: "cover_large-2.jpg"
categories:
  - "Java"
related_posts:
  - "making-illegal-state-unrepresentable"
  - "does-language-still-matter-in-the-age-of-ai-yes-but-the-tradeoff-has-changed"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
  - "research-measuring-energy-consumption-in-programming-languages-for-ai-applications"
frozen: false
---

I started as a Java developer, but for some time now, I have broadened my horizons. Recently, I thought about how early languages were dedicated to a single target and platform, and now they are broadening their focus. In this post, I want to write down my thoughts in the hope that it may be useful to others, probably to my future self.

## Definitions

You may have been wondering about the title terms. I'm pretty sure that if you read this post, you have a pretty good picture of what a programming language is. Some may disagree on some finer points or raise a hair-splitting one, but it's not a PhD thesis, only a post on my blog. I must define what I mean by **target** and **platform** in the context of this post before going further.

**Target**   

A target only makes sense in the context of compiled programming languages. For example, C's target is *native code* , and Java's is *bytecode*.

**Platform**   

A platform is the system that will ultimately run the target. Native code runs on the operating system; bytecode on the JVM.

## Early programming languages

Early programming languages had a single target and platform. I mentioned C and Java, but Ruby, Python, JavaScript, etc., were all the same.

| Programming language |   Target    |         Platform          |
|----------------------|-------------|---------------------------|
| C                    | Native code | Operating system          |
| C++                  | Native code | Operating system          |
| Java                 | Bytecode    | JVM                       |
| Python               | -           | Python runtime            |
| TypeScript           | JavaScript  | Browser \& server-side JS |
| JavaScript           | -           | Browser                   |

I believe it was the case for a long time. It changed at some point, though.

## Multi-target is the new black

The first time I heard about multi-target was in Scala. Scala came from the era of single-target and targeted bytecode on the JVM platform. However, in 2015, Martin Odersky announced Scala.js, which added JavaScript to Scala's target.

The original article was published on InfoWorld, but it seems to have redirection issues nowadays. Here's the introduction on a copy:
> Scala, developed as a functional and object-oriented language for the JVM, is now multiplatform, with developers using it in abundance on JavaScript via Scala.js, Scala founder Martin Odersky says.
>
> With Scala.js, developers write code in Scala, and the code is then compiled to JavaScript, analogous to using Microsoft's TypeScript. Developers can leverage their Scala skills in Web development. "\[Scala is\] very popular on JavaScript now," Odersky said at the Scala Days conference in San Francisco.
>
> —- [Scala.js lets you compile Scala to JavaScript](https://goinfotech.blogspot.com/2015/03/scalajs-lets-you-compile-scala-to.html)

While Scala was the first I had heard about, other languages started to target JavaScript: I know at least Kotlin and Clojure, two originally JVM-bound languages. From that point on, it seemed every language started to add more targets. When they didn't, third parties tried to do it.

I believe that Kotlin was the epitome of such a strategy. It started with JavaScript, but the team later added native with LLVM and is currently working on WebAssembly, as far as I know. Java developers weren't left behind. The [GraalVM](https://www.graalvm.org/) project, managed by Oracle, allows them to generate native code. A third-party project, [TeaVM](https://teavm.org/), targets JavaScript and WebAssembly. It seems that nowadays lots of languages target Wasm.

Here's a small excerpt of languages and what targets and platforms they support at the time of this writing. It can't be exhaustive and obviously focuses on the JVM ecosystem, which I happen to know better.

| Programming language |                               Native/supporting project                               |                           Target                           |       Platform       |
|----------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------|----------------------|
| Java                 | Native                                                                                | Bytecode                                                   | JVM                  |
| Java                 | [GraalVM](https://www.graalvm.org/)                                                   | Native code                                                | Desktop OS           |
| Java                 | [TeaVM](https://teavm.org/)                                                           | JavaScript                                                 | Browser              |
| Java                 | [TeaVM](https://teavm.org/)                                                           | [WebAssembly](https://teavm.org/docs/wasm-gc-backend.html) | Wasm runtime         |
| Java                 | [TeaVM](https://teavm.org/)                                                           | [C](https://teavm.org/docs/c-backend.html)                 | -                    |
| Kotlin               | Native                                                                                | Bytecode                                                   | JVM                  |
| Kotlin               | Native                                                                                | JavaScript                                                 | Browser              |
| Kotlin               | Native                                                                                | JavaScript                                                 | Server-side runtimes |
| Kotlin               | Native                                                                                | Native                                                     | Desktop OS           |
| Kotlin               | Native                                                                                | Native                                                     | iOS                  |
| Kotlin               | Native                                                                                | Native                                                     | Android              |
| Dart                 | Native                                                                                | JavaScript                                                 | Browser              |
| Dart                 | Native                                                                                | WebAssembly                                                | Wasm runtime         |
| Dart                 | [DartNative](https://github.com/dart-native/dart_native)                              | Native                                                     | Desktop OS           |
| Dart                 | [DartNative](https://github.com/dart-native/dart_native)                              | Native                                                     | iOS                  |
| Dart                 | [DartNative](https://github.com/dart-native/dart_native)                              | Native                                                     | Android              |
| Rust                 | Native                                                                                | Native code                                                | Desktop OS           |
| Rust                 | Native via a `target`                                                                 | WebAssembly                                                | Wasm runtime         |
| Rust                 | [rustc_codegen_jvm](https://github.com/IntegralPilot/rustc_codegen_jvm)               | Bytecode                                                   | JVM                  |
| Zig                  | Native                                                                                | Native code                                                | Desktop OS           |
| Zig                  | [Zig and WebAssembly](https://zigwasm.org/)                                           | WebAssembly                                                | Wasm runtime         |
| Swift                | Native                                                                                | Native code                                                | Desktop OS           |
| Swift                | Native                                                                                | ARM machine code                                           | Android              |
| Swift                | [SwiftWasm](https://swiftwasm.org/)                                                   | WebAssembly                                                | Wasm runtime         |
| Python               | Native                                                                                | Python runtime                                             | Desktop OS           |
| Python               | [jythonc](https://www.jython.org/jython-old-sites/archive/22/jythonc.html) (archived) | Bytecode                                                   | JVM                  |
| Python               | [py2wasm](https://github.com/wasmerio/py2wasm)                                        | WebAssembly                                                | Wasm runtime         |

Again, this is just a snapshot of projects I know at the time of this writing.

## Discussion

Programming languages were initially focused on a single target and platform. With time, more and more languages broaden their horizon. It comes either as part of the language itself or is an effort from third parties.

It made sense so far. The bigger your organization's investment in a programming language, the harder it is to pivot to another one. In that light, GraalVM's native is a value proposition for Java-heavy organizations using Kubernetes. The JVM was meant for long-running tasks, while Kubernetes is built on the idea of stopping pods and starting new ones.

However, with the fast progress of AI, I wonder whether the trend will continue. Some might question the value. Instead of GraalVM'ifying existing Java applications, why not rewrite them directly in Rust, which natively compiles to machine code? This won't happen for core applications at the beginning, but I expect some may experiment with peripheral ones. If the experience nets benefits in terms of resource usage, then it may gnaw at core apps.

I'm very interested in the next few years to understand whether I'm imagining things, or if it will be a complete upheaval of the landscape.

**To go further:**

* [Scala.js lets you compile Scala to JavaScript](https://goinfotech.blogspot.com/2015/03/scalajs-lets-you-compile-scala-to.html)
* [GraalVM](https://www.graalvm.org/)
* [TeaVM](https://teavm.org/)
* [DartNative](https://github.com/dart-native/dart_native)
* [rustc_codegen_jvm](https://github.com/IntegralPilot/rustc_codegen_jvm)
* [Zig and WebAssembly](https://zigwasm.org/)
* [SwiftWasm](https://swiftwasm.org/)

*Originally published at [A Java Geek](https://blog.frankel.ch/programming-languages-targets-platforms/) on June 21^st^, 2026.*
