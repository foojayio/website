---
title: "Introducing BoxLings! An interactive teacher for BoxLang and TDD/BDD"
slug: "introducing-boxlings-an-interactive-teacher-for-boxlang-and-tdd-bdd"
date: "2026-04-09T08:48:57+00:00"
description: "BoxLings gives you 129 progressive exercises across 28 topics from the basics of variables and functions all the way to async programming, Java interop, destructuring, and CLI app development."
authors:
  - "luis-majano"
image: "boxlings-banner.jpg"
categories:
  - "BoxLang"
  - "Testing"
tags:
related_posts:
frozen: false
---

We believe the best way to learn a programming language is by writing code --- real code, with real feedback, and real tests. That's exactly why we built **BoxLings**.

Inspired by the beloved [rustlings](https://github.com/rust-lang/rustlings) project, **BoxLings** is an interactive CLI tool that teaches you **BoxLang** through hands-on exercises. You read failing tests, fix broken code, and level up --- one exercise at a time.

Oh, and the whole thing is written in BoxLang itself. 🥊 Dogfooding at its finest.

What Is BoxLings? {#h2-0-what-is-boxlings}
------------------------------------------

![](https://www.ortussolutions.com/__media/contentbox:2026/boxlang/boxlings-intro.png)

BoxLings gives you **129 progressive exercises across 28 topics** --- from the basics of variables and functions all the way to async programming, Java interop, destructuring, and CLI app development.

But here's what makes BoxLings different: **we teach TDD/BDD as a first-class skill** , not an afterthought, using [TestBox](https://testbox.run); our BDD/TDD testing library.

From day one, you'll read **TestBox** specs before touching any implementation code. You'll learn to think in tests. By the time you hit the intermediate exercises, you'll be writing your own. By Phase 3, you'll be doing the full red-green-refactor cycle like a pro.

*** ** * ** ***

The Full Learning Path {#h2-1-the-full-learning-path}
-----------------------------------------------------

BoxLings is organized into three progressive phases, with 28 topics and 129 exercises total.

### 🟢 Phase 1 --- Core Fundamentals (50 Exercises) {#h3-2-phase-1-core-fundamentals-50-exercises}

Perfect for beginners and developers new to BoxLang:

| #  |      Topic       | Exercises |                    What You Learn                    |
|----|------------------|-----------|------------------------------------------------------|
| 1  | **Introduction** | 2         | Get started with BoxLings and BoxLang basics         |
| 2  | **Variables**    | 6         | Dynamic typing, the `var` keyword, scoping basics    |
| 3  | **Functions**    | 6         | UDFs, closures, lambdas                              |
| 4  | **Conditionals** | 4         | `if/else`, ternary, `switch`                         |
| 5  | **Data Types**   | 8         | Strings, numbers, booleans, arrays, structs          |
| 6  | **Arrays**       | 4         | Array operations and member functions                |
| 7  | **Scopes**       | 5         | `variables`, `local`, `this`, `arguments` scopes     |
| 8  | **Structs**      | 5         | Struct manipulation and operations                   |
| 9  | **Strings**      | 6         | Interpolation, multi-line strings, string operations |
| 10 | **Imports**      | 4         | Importing classes and the `java:` prefix             |

### 🟡 Phase 2 --- Intermediate (40 Exercises) {#h3-3-phase-2-intermediate-40-exercises}

Dive deeper into BoxLang's power features:

| #  |        Topic         | Exercises |                 What You Learn                 |
|----|----------------------|-----------|------------------------------------------------|
| 11 | **Structs Advanced** | 4         | Deep operations, merging, complex manipulation |
| 12 | **Null Handling**    | 4         | Elvis operator, safe navigation                |
| 13 | **Error Handling**   | 6         | `try/catch`, `throw`, custom exceptions        |
| 14 | **Interfaces**       | 4         | Implementing Java interfaces from BoxLang      |
| 15 | **Testing**          | 5         | **Write your own TestBox specs!**              |
| 16 | **Functional**       | 8         | `map`, `filter`, `reduce`, lambdas             |
| 17 | **Async**            | 6         | Threads, futures, async programming            |
| 18 | **Components**       | 3         | `bx:http`, `bx:query`, and more                |

### 🔴 Phase 3 --- Advanced (48 Exercises) {#h3-4-phase-3-advanced-48-exercises}

Master BoxLang-specific and power-user features:

| #  |       Topic       | Exercises |                     What You Learn                      |
|----|-------------------|-----------|---------------------------------------------------------|
| 19 | **Casting**       | 5         | `castAs`, `javaCast`, type conversions                  |
| 20 | **Quizzes**       | 3         | Comprehensive knowledge reviews                         |
| 21 | **Classes**       | 8         | OOP, properties, metadata                               |
| 22 | **BIFs**          | 6         | Built-in functions and member functions                 |
| 23 | **Templating**    | 4         | `.bxm` files and template syntax                        |
| 24 | **CLI Apps**      | 4         | Building real CLI tools with BoxLang                    |
| 25 | **Java Interop**  | 6         | Calling Java, the `java:` prefix in depth               |
| 26 | **Destructuring** | 4         | Struct and array destructuring, renaming, nesting       |
| 27 | **Spread**        | 4         | Spread operator for arrays, structs, and function calls |
| 28 | **Range**         | 2         | The `..` range operator and functional methods          |
| 29 | **Assert**        | 2         | The `assert` statement with custom messages             |

*** ** * ** ***

The TDD/BDD Learning Journey {#h2-5-the-tdd-bdd-learning-journey}
-----------------------------------------------------------------

BoxLings teaches test-driven development alongside BoxLang in four progressive stages:

**Step 1 --- Reading Tests (Topics 1--10)**   

Read TestBox specs to understand requirements. Tests are your documentation.

**Step 2 --- Understanding Patterns (Topics 11--14)**   

Multiple assertions, setup/teardown with `beforeEach`/`afterEach`, edge cases, and error scenarios.

**Step 3 --- Writing Tests (Topic 15)**   

Now *you* write the specs. Practice `describe` / `it` / `expect` from scratch.

**Step 4 --- Full TDD Cycle (Topics 16--29)**   

Red → Green → Refactor. The real deal.

*** ** * ** ***

How It Works {#h2-6-how-it-works}
---------------------------------

```bash
git clone https://github.com/ortus-boxlang/boxlings.git
cd boxlings
boxlang BoxLings.bx
```

BoxLings drops you into **watch mode** --- it monitors your exercise files and reruns them automatically every time you save. Fix the code, hit save, see the tests go green.

**Keyboard shortcuts in watch mode:**

| Key |         Action         |
|-----|------------------------|
| `n` | Next exercise          |
| `h` | Show hint              |
| `t` | Show test file         |
| `l` | List all exercises     |
| `r` | Rerun current exercise |
| `q` | Quit                   |

Three exercise types are supported: scripts (`.bxs`), classes (`.bx`), and templates (`.bxm`), covering the full breadth of how BoxLang is used in practice.

*** ** * ** ***

Built for Learners, Classrooms \& Workshops {#h2-7-built-for-learners-classrooms-workshops}
-------------------------------------------------------------------------------------------

BoxLings is self-contained and runs completely offline after the initial clone. Whether you're learning solo, teaching a workshop, or onboarding a new team member, BoxLings provides a structured, guided path with immediate feedback.

**Estimated completion time:**

* 🆕 Beginners: \~15--20 hours
* 💻 Experienced developers new to BoxLang: \~6--10 hours
* 🔥 Java developers: \~4--6 hours

*** ** * ** ***

Get Started {#h2-8-get-started}
-------------------------------

You'll need **BoxLang 1.12+** . We recommend [BVM](https://boxlang.ortusbooks.com/getting-started/installation/boxlang-version-manager-bvm) to manage your BoxLang versions:

```bash
curl -fsSL https://install-bvm.boxlang.io/ | bash
bvm install 1.12.0
bvm use 1.12.0
```

Then clone and go:

```bash
git clone https://github.com/ortus-boxlang/boxlings.git
cd boxlings
boxlang BoxLings.bx
```

*** ** * ** ***

Join the Community {#h2-9-join-the-community}
---------------------------------------------

We'd love to hear what you think --- and contributions are very welcome. New exercises, bug fixes, documentation --- all of it.

* 📖 [BoxLang Docs](https://boxlang.ortusbooks.com/)
* 💬 [Community Forum](https://community.ortussolutions.com/c/boxlang/42)
* 🤝 [BoxLang Slack](https://boxteam.ortussolutions.com/)
* 🐛 [Open an Issue](https://github.com/ortus-boxlang/boxlings/issues)

👉 **[github.com/ortus-boxlang/boxlings](https://github.com/ortus-boxlang/boxlings)**

Now go fix some broken code. 🥊
