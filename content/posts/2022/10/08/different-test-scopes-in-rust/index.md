---
title: "Different test scopes in Rust"
date: "2022-10-08T15:25:09+00:00"
lastmod: "2026-09-21T06:05:26+00:00"
description: "I'm still working on learning Rust. Beyond syntax, learning a language requires familiarizing oneself with its idioms and ecosystem. I'm at a point where…"
canonical: "https://blog.frankel.ch/different-test-scopes-rust/"
authors:
  - "nicolas-frankel"
image: "pexels-david-abbram-3640451.jpg"
categories:
  - "Testing"
related_posts:
  - "mutation-testing-in-rust"
frozen: false
---

I'm still working on learning Rust. Beyond syntax, learning a language requires familiarizing oneself with its idioms and ecosystem. I'm at a point where I want to explore testing in Rust.

## The initial problem

We have used Dependency Injection *a lot* - for ages on the JVM. Even if you're not using a framework, Dependency Injection helps decouple components. Here's a basic example:

```kotlin
class Car(private val engine: Engine) {

    fun start() {
        engine.start()
    }
}

interface Engine {
    fun start()
}

class CarEngine(): Engine {
    override fun start() = ...
}

class TestEngine(): Engine {
    override fun start() = ...
}
```

In regular code:

```kotlin
val car = Car(CarEngine())
```

In test code:

```kotlin
val dummy = Car(TestEngine())
```

is about executing different code snippets depending on the context.
> To change a function into a test function, add `#[test]` on the line before fn. When you run your tests with the `cargo test` command, Rust builds a test runner binary that runs the annotated functions and reports on whether each test function passes or fails.
>
> -- [The Anatomy of a Test Function](https://doc.rust-lang.org/book/ch11-01-writing-tests.html#the-anatomy-of-a-test-function)

At its most basic level, it allows for defining test functions. These functions are only valid when calling `cargo test`:

```rust
fn main() {
    println!("{}", hello());
}

fn hello() -> &'static str {
    return "Hello world";
}

#[test]
fn test_hello() {
    assert_eq!(hello(), "Hello world");
}
```

`cargo run` yields the following:

```
Hello world
```

On the other hand, `cargo run` yields:

```
running 1 test
test test_hello ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured; 0 filtered out; finished in 0.00s

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured; 0 filtered out; finished in 0.01s
```

However, our main issue is different: we want to code depending on whether it's a testing context.

The `test` macro is not the solution we are looking for.

## Playing with the `cfg` macro

Rust differentiates between "unit" tests and "integration" tests. I added double quotes because I believe the semantics can be misleading. Here's what they mean:

* Unit tests are written in the same file as the main. You annotate them with the `#[test]` macro and call `cargo test` as seen above
* Integration tests are external to the code to test. You annotate code to be part of integration tests with the `#[cfg(test)]` macro.

Enter the `cfg` macro:
> Evaluates boolean combinations of configuration flags at compile-time.
>
> In addition to the `#[cfg]` attribute, this macro is provided to allow boolean expression evaluation of configuration flags. This frequently leads to less duplicated code.
>
> -- [Macro std::cfg](https://doc.rust-lang.org/std/macro.cfg.html)

The `cfg` macro offers lots of out-of-the-box configuration variables:

|        Variable        |                                                               Description                                                               |                Example                |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------|
| `target_arch`          | Target's CPU architecture                                                                                                               | * `"x86"` * `"arm"` * `"aarch64"`     |
| `target_feature`       | Platform feature available for the current compilation target                                                                           | * `"rdrand"` * `"sse"` * `"se2"`      |
| `target_os`            | Target's operating system                                                                                                               | * `"windows"` * `"macos"` * `"linux"` |
| `target_family`        | More generic description of a target, such as the family of the operating systems or architectures that the target generally falls into | * `"windows"` * `"unix"`              |
| `target_env`           | Further disambiguating information about the target platform with information about the `ABI or libc` used                              | * `""` * `"gnu"` * `"musl"`           |
| `target_endian`        | `"big"` or `"little"`                                                                                                                                                          ||
| `target_pointer_width` | Target's pointer width in bits                                                                                                          | * `"32"` * `"64"`                     |
| `target_vendor`        | Vendor of the target                                                                                                                    | * `"apple"` * `"pc"`                  |
| `test`                 | Enabled when compiling the test harness                                                                                                                                        ||
| `proc_macro`           | When the crate compiled is being compiled with the `proc_macro`                                                                                                                ||
| `panic`                | Depending on the panic strategy                                                                                                         | * `"abort"` * `"unwind"`              |

You may have noticed the `test` flag among the many variables. To write an integration test, annotate the code with the `#[cfg(test)]` macro:

```rust
#[cfg(test)]
fn test_something() {
    // Whatever
}
```

One can also use the macro to provide alternative code in the `test` context:

```rust
fn hello() -> &'static str {
    return "Hello world";
}

#[cfg(test)]
fn hello() -> &'static str {
    return "Hello test";
}
```

The above snippet works during `cargo run` but not during `cargo test`. In the first case, the second function is ignored; in the second, it's not, and Rust tries to compile two functions with the same signature.

```
error[E0428]: the name `hello` is defined multiple times
  --> src/lib.rs:10:1
   |
5  | fn hello() -> &'static str {
   | -------------------------- previous definition of the value `hello` here
...
10 | fn hello() -> &'static str {
   | ^^^^^^^^^^^^^^^^^^^^^^^^^^ `hello` redefined here
   |
   = note: `hello` must be defined only once in the value namespace of this module
```

Fortunately, the `cfg` macro offers boolean logic. Hence we can negate the `test` config for the first function:

```rust
fn main() {
    println!("{}", hello());
}

#[cfg(not(test))]
fn hello() -> &'static str {
    return "Hello world";
}

#[cfg(test)]
fn hello() -> &'static str {
    return "Hello test";
}

#[test]
fn test_hello() {
    assert_eq!(hello(), "Hello test");
}
```

* `cargo run` yields `Hello world`
* `cargo test` *compiles* then executes the test successfully

While it solves our problem, it has obvious flaws:

* It's binary - test context or not
* It doesn't scale: after a specific size, the sheer number of annotations will make the project unmanageable

## Refining the design

To refine the design, let's imagine a simple scenario that I've faced multiple times on the JVM:

* during the regular run, code connects to the production database, *e.g.*, Postgres
* for integration testing, code uses a local database, *e.g.*, SQLite
* for unit testing, the code doesn't use a database but a mock

Here's the foundation for the design:

```rust
fn main() {
    // Get a database implementation                          // 1
    db.do_stuff();
}

trait Database {
    fn doStuff(self: Self);
}

struct MockDatabase {}
struct SqlitDatabase {}
struct PostgreSqlDatabase {}

impl Database for MockDatabase {
    fn doStuff(self: Self) {
        println!("Do mock stuff");
    }
}

impl Database for SqlitDatabase {
    fn doStuff(self: Self) {
        println!("Do stuff with SQLite");
    }
}

impl Database for PostgreSqlDatabase {
    fn doStuff(self: Self) {
        println!("Do stuff with PostgreSQL");
    }
}
```

1. How to get the correct implementation depending on the context?

We have three contexts, and `cfg[test]` only offers a boolean flag. It's time for a new approach.

## Leveraging Cargo features

As I searched for a solution, I asked on the Rust Slack channel. [William Dillon](http://housedillon.com/) was kind enough to answer and proposed that I look at Cargo's features.
> Cargo "features" provide a mechanism to express conditional compilation and optional dependencies. A package defines a set of named features in the `[features]` table of `Cargo.toml`, and each feature can either be enabled or disabled. Features for the package being built can be enabled on the command-line with flags such as `--features`. Features for dependencies can be enabled in the dependency declaration in `Cargo.toml`.
>
> -- [Features](https://doc.rust-lang.org/cargo/reference/features.html)

### Defining features

The first step is to define what features we will use. One configures them in the `Cargo.toml` file:

```
[features]
unit = []
it = []
prod = []
```

### Using the features in the code

To use the feature, we leverage the `cfg` macro:

```rust
fn main() {
    #[cfg(feature = "unit")]                   // 1
    let db = MockDatabase {};
    #[cfg(feature = "it")]                     // 2
    let db = SqlitDatabase {};
    #[cfg(feature = "prod")]                   // 3
    let db = PostgreSqlDatabase {};
    db.do_stuff();
}

trait Database {
    fn do_stuff(self: Self);
}

#[cfg(feature = "unit")]                       // 1
struct MockDatabase {}

#[cfg(feature = "unit")]                       // 1
impl Database for MockDatabase {
    fn do_stuff(self: Self) {
        println!("Do mock stuff");
    }
}

// Abridged for brevity's sake                 // 2-3
```

1. Compiled only if the `unit` feature is activated
2. Compiled only if the `it` feature is activated
3. Compiled only if the `prod` feature is activated

### Activating a feature

You must use the `-F` flag to activate a feature.

```bash
cargo run -F unit
```

```
Do mock stuff
```

### Default feature

The "production" feature should be the most straightforward one. Hence, it's crucial to set it by default.

It has bitten me in the past: when your colleague is on leave, and you need to build/deploy, it's a mess to read the code to understand what flags are mandatory.

Rust allows setting default features. They don't need to be activated; they are on by default. The magic happens in the `Cargo.toml` file.

```
[features]
default = ["prod"]                             # 1
unit = []
it = []
prod = []
```

1. The `prod` feature is set as default

We can now run the program without explicitly setting the `prod` feature:

```bash
cargo run
```

```
Do stuff with PostgreSQL
```

### Exclusive features

All three features are exclusive: you can activate only one at a time. To disable the default one(s), we need an additional flag:

```bash
cargo run --no-default-features -F unit
```

```
Do mock stuff
```

The documentation offers multiple approaches to avoid activating exclusive features at the same time:
> There are rare cases where features may be mutually incompatible with one another. This should be avoided if at all possible, because it requires coordinating all uses of the package in the dependency graph to cooperate to avoid enabling them together. If it is not possible, consider adding a compile error to detect this scenario.
>
> -- [Mutually exclusive features](https://doc.rust-lang.org/cargo/reference/features.html#mutually-exclusive-features)

Let's add the code:

```rust
#[cfg(all(feature = "unit", feature = "it"))]
compile_error!("feature \"unit\" and feature \"it\" cannot be enabled at the same time");
#[cfg(all(feature = "unit", feature = "prod"))]
compile_error!("feature \"unit\" and feature \"prod\" cannot be enabled at the same time");
#[cfg(all(feature = "it", feature = "prod"))]
compile_error!("feature \"it\" and feature \"prod\" cannot be enabled at the same time");
```

If we try to run with the `unit` feature while the default `prod` feature is enabled:

```bash
cargo run -F unit
```

```
error: feature "unit" and feature "prod" cannot be enabled at the same time
 --> src/main.rs:4:1
  |
4 | compile_error!("feature \"unit\" and feature \"prod\" cannot be enabled at the same time");
  | ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

## Fixing the above design

The above design is not so slightly misleading. In tests, the entry point is not the `main` function but the test functions themselves.

Let's re-add some tests as in the initial phase.

```rust
#[cfg(feature = "prod")]                            // 1
fn main() {
    let db = PostgreSqlDatabase {};
    println!("{}", db.do_stuff());
}

trait Database {
    fn do_stuff(self: Self) -> &'static str;        // 2
}

#[cfg(feature = "unit")]
struct MockDatabase {}
#[cfg(feature = "prod")]
struct PostgreSqlDatabase {}

#[cfg(feature = "unit")]
impl Database for MockDatabase {
    fn do_stuff(self: Self) -> &'static str {
        "Do mock stuff"
    }
}

#[cfg(feature = "prod")]
impl Database for PostgreSqlDatabase {
    fn do_stuff(self: Self) -> &'static str {
        "Do stuff with PostgreSQL"
    }
}

#[test]
#[cfg(feature = "unit")]
fn test_unit() {
    let db = MockDatabase {};
    assert_eq!(db.do_stuff(), "Do mock stuff");     // 3
}

// it omitted for brevity
```

1. The `PostgreSqlDatabase` struct is not available when any test feature is activated
2. Change the signature to be able to test
3. Test!

At this point, we can run the different commands:

```bash
cargo test --no-default-features -F unit            #1
cargo test --no-default-features -F it              #2
cargo run                                           #3
```

1. Run the unit test
2. Run the "integration test" test
3. Run the application

## Conclusion

In this post, I described the problem caused by having different test suites, focusing on different scopes. The default `test` configuration variable is binary: either the scope is `test` or not. It's not enough when one needs to separate between unit and integration tests, each one requiring a different trait implementation.

Rust's features are a way to solve this issue. A feature allows guarding some code behind a label, which one can enable per run on the command line.

To be perfectly honest, I don't know if Rust features are the right way to implement different test scopes. In any case, it works and allows me to understand the Rust ecosystem better.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/rust-test-suites).

**To go further:**

* [Testing in Rust](https://doc.rust-lang.org/book/ch11-00-testing.html)
* [Rust by example: testing](https://doc.rust-lang.org/rust-by-example/testing.html)
* [Conditional compilation](https://doc.rust-lang.org/reference/conditional-compilation.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/different-test-scopes-rust/) on October 9^th^, 2022*

*[DI]: Dependency Injection
