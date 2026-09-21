---
title: "Runtime-initialized variables in Rust"
date: "2025-06-03T07:56:18+00:00"
lastmod: "2026-09-21T06:02:42+00:00"
description: "Rust offers different ways to initialize compile time-initialized variables. Recently, I had to create a runtime-initialized variable: existing approaches…"
canonical: "https://blog.frankel.ch/lazy-initialized-vars-rust/"
authors:
  - "nicolas-frankel"
image: "lazy-ferris_large.jpg"
categories:
  - "Java"
related_posts:
  - "my-first-real-rust-project"
  - "error-management-in-rust-and-libs-that-support-it"
frozen: false
---

Rust offers different ways to initialize **compile time-initialized** variables. Recently, I had to create a runtime-initialized variable: existing approaches don't work in this case. I want to describe multiple ways to achieve it in this post.

## Constants

The Rust language allows you to create constants. Two keywords are available: `const` and `static`.
> Sometimes a certain value is used many times throughout a program, and it can become inconvenient to copy it over and over. What's more, it's not always possible or desirable to make it a variable that gets carried around to each function that needs it. In these cases, the `const` keyword provides a convenient alternative to code duplication:
>
> ```rust
> const THING: u32 = 0xABAD1DEA;
>
> let foo = 123 + THING;
> ```
>
> -- [Keyword const](https://doc.rust-lang.org/std/keyword.const.html)
>
> A static item is a value which is valid for the entire duration of your program (a `'static` lifetime).
>
> On the surface, `static` items seem very similar to `const`: both contain a value, both require type annotations and both can only be initialized with constant functions and values. However, `static` are notably different in that they represent a location in memory. That means that you can have references to `static` items and potentially even modify them, making them essentially global variables.
>
> Static items do not call `drop` at the end of the program.
>
> There are two types of `static` items: those declared in association with the `mut` keyword and those without.
>
> -- [Keyword static](https://doc.rust-lang.org/std/keyword.static.html)

None of the above work with `struct`.

I stand corrected: <https://www.reddit.com/r/rust/comments/1l0uwzy/comment/mvgghb0/>.

```rust
const REGEXP_WAREHOUSE_ENDPOINT: Regex = Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap();
```

Trying to compile the previous line returns the following:

```
error[E0015]: cannot call non-const fn `regex::Regex::new` in constants
  |
6 | const REGEXP_WAREHOUSE_ENDPOINT: Regex = Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap();
  |                                          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  |
  = note: calls in constants are limited to constant functions, tuple structs and tuple variants
```

The same happens with if we change `const` to `static`.

```
error[E0015]: cannot call non-const fn `regex::Regex::new` in statics
 --> src/config.rs:6:43
  |
6 | static REGEXP_WAREHOUSE_ENDPOINT: Regex = Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap();
  |                                           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  |
  = note: calls in statics are limited to constant functions, tuple structs and tuple variants
```

## The `lazy_static` crate

When you need something, there's a high chance somebody required it before. If lucky, they created a crate for it. Case in point, the `lazy_static` crate provides a solution.
> A macro for declaring lazily evaluated statics in Rust.
>
> Using this macro, it is possible to have statics that require code to be executed at runtime in order to be initialized. This includes anything requiring heap allocations, like vectors or hash maps, as well as anything that requires non-const function calls to be computed.
>
> -- [lazy_static crate](https://crates.io/crates/lazy_static)

The usage is straightforward: we wrap the `static` declaration in a macro.

```rust
lazy_static! {
    static ref REGEXP_WAREHOUSE_ENDPOINT: Regex = Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap();
}
```

We achieved what we wanted to. Yet, we can do better.

## `LazyLock` for the win

Rust 1.80 adds `LazyLock`. `LazyLock` is a built-in exact replacement for the `lazy_static` macro.
> [pic.twitter.com/cc3PCuLEoL](https://t.co/cc3PCuLEoL)
>
> — Tom Dohrmann (@13erbse) [July 25, 2024](https://twitter.com/13erbse/status/1816491350235173228?ref_src=twsrc%5Etfw)

The documentation of `LazyLock` is straightforward:
> `pub struct LazyLock T> { /* private fields */ }`
>
> A value which is initialized on the first access.
>
> This type is a thread-safe LazyCell, and can be used in statics. Since initialization may be called from multiple threads, any dereferencing call will block the calling thread if another initialization routine is currently running.
>
> -- [LazyLock](https://doc.rust-lang.org/beta/std/sync/struct.LazyLock.html)

At this point, we can use it directly:

```rust
static REGEXP_WAREHOUSE_ENDPOINT: LazyLock<Regex> =
        LazyLock::new(|| Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap());
```

## `OnceLock`

As an alternative, one can use `OnceLock` from Rust 1.70.
> `pub struct OnceLock { /* private fields */ }`
>
> A synchronization primitive which can nominally be written to only once.
>
> This type is a thread-safe `OnceCell`, and can be used in statics. In many simple cases, you can use `LazyLock` instead to get the benefits of this type with less effort: `LazyLock` "looks like" `&T` because it initializes with `F` on deref! Where OnceLock shines is when LazyLock is too simple to support a given case, as LazyLock doesn't allow additional inputs to its function after you call `LazyLock::new(|| ...)`.
>
> -- [OnceLock](https://doc.rust-lang.org/stable/std/sync/struct.OnceLock.html)

Usage is quite different:

```rust
static REGEXP_WAREHOUSE_ENDPOINT: OnceLock<Regex> = OnceLock::new();       //1

let endpoint = REGEXP_WAREHOUSE_ENDPOINT.get_or_init(
        || Regex::new(r"^WAREHOUSE__(?P<index>\d+)__ENDPOINT$").unwrap()); //2

endpoint.find("whatever").is_some()                                        //3
```

1. Create the `OnceLock`
2. Get or initialize the variable
3. Use the variable

## Summary

In this post, we described several options to create lazy-initialized variables. Before, we had to rely on the `lazy_static` crate. With Rust 1.80, we can replace it with `LazyLock`. If you need additional input after the `LazyLock::new()`, `OnceLock` is your friend.

**To go further:**

* [Keyword const](https://doc.rust-lang.org/std/keyword.const.html)
* [lazy_static crate](https://docs.rs/lazy_static/latest/lazy_static/)
* [Struct LazyLock](https://doc.rust-lang.org/beta/std/sync/struct.LazyLock.html)
* [Struct OnceLock](https://doc.rust-lang.org/stable/std/sync/struct.OnceLock.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/lazy-initialized-vars-rust/) on June 1^st^, 2025*
