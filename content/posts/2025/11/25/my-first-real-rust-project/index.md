---
title: "My first real Rust project"
date: "2025-11-25T17:03:42+00:00"
lastmod: "2026-09-21T05:59:45+00:00"
description: "I have been learning Rust for a couple of years, and using it for pet projects and demos alike. Working for a JVM-heavy company, I thought it would be my…"
canonical: "https://blog.frankel.ch/first-real-rust-project/"
authors:
  - "nicolas-frankel"
image: "cover.jpg"
categories:
  - "Java"
related_posts:
  - "runtime-initialized-variables-in-rust"
  - "error-management-in-rust-and-libs-that-support-it"
frozen: false
---

I have been learning Rust for a couple of years, and using it for pet projects and [demos](https://github.com/nfrankel/opentelemetry-tracing/tree/master/inventory) alike. Working for a JVM-heavy company, I thought it would be my fate forever. Last week, I had a nice surprise: I convinced my management that using Rust for a particular project was the right choice. It's not a huge project, but I want to describe my experience using Rust in a "real" project.

## The project

Our main software platform has baked-in health sensors for monitoring. These sensors are exposed as HTTP APIs. The problem is that most customers do not actively monitor those endpoints. For an engineer such as myself, they are responsible for their lack of maturity regarding observability; for customer success managers, they should be helped and cared for.

The goal is simple: provide a component that polls the sensors' state and sends them via email. One can configure the sensors polled and the severity, *e.g.*, warning and above, as well as the email and a few other parameters.

## Why Rust?

While I like Rust a lot, I also love Kotlin, would like to practice Gleam on Beam, etc. There are objective reasons to use Rust for this project, and they are tied to the context and solution design.

The first thing I had to advocate for was to design the component outside the platform. People want to put everything inside it. When all you have is a hammer, then everything looks like a nail. Yet, if the platform is already crashing, then chances are the reporting component will be unavailable.

Once it was agreed, the component's tech stack became independent of the platform's. It was time to challenge another widespread Pavlovian reflex. When I was asked to schedule tasks in a JVM application, my go-to solution was [Quartz](https://www.quartz-scheduler.org/).
> Quartz is a richly featured, open source job scheduling library that can be integrated within virtually any Java application - from the smallest stand-alone application to the largest e-commerce system. Quartz can be used to create simple or complex schedules for executing tens, hundreds, or even tens of thousands of jobs; jobs whose tasks are defined as standard Java components that may execute virtually anything you may program them to do. The Quartz Scheduler includes many enterprise-class features, such as support for JTA transactions and clustering.
>
> -- [What is the Quartz Job Scheduling Library?](https://www.quartz-scheduler.org/documentation/quartz-2.5.x/introduction.html)

It worked, it works, and it will work. However, it requires the application to keep running, as in web apps, for example. What happens if the application crashes? How will it restart? Quartz doesn't answer these questions.

With experience, I have changed my approach. I prefer now to run to completion and use the operating system scheduler, *e.g.* , `cron`. It's the application of the single responsibility principle: both the application and `cron` do their job and only theirs. It renders the application smaller and the overall solution more resilient.

The JVM benefits long-lived applications, whose performance it can improve over time. Within the context of a short-lived process running to completion, it's not a great fit: it will consume a lot of memory, and before it has started compiling the bytecode to native code, the application will have ended. At this point, I had two alternatives: Go or Rust. I hate Go, more specifically, its error handling approach, or even more specifically, its lack of proper error handling.

Different customers will use different operating systems. Rust allows [cross-platform compiling](https://rust-lang.github.io/rustup/cross-compilation.html):
> Rust [supports a great number of platforms](https://doc.rust-lang.org/nightly/rustc/platform-support.html). For many of these platforms The Rust Project publishes binary releases of the standard library, and for some the full compiler. `rustup` gives easy access to all of them.
>
> When you first install a toolchain, `rustup` installs only the standard library for your *host* platform - that is, the architecture and operating system you are presently running. To compile to *other* platforms you must install other target platforms.
>
> -- [Cross-compilation](https://rust-lang.github.io/rustup/cross-compilation.html)

The above arguments helped me convince my management that Rust was the right choice.

## Choosing crates

Crates are either libraries or applications in Rust. For the sake of simplicity, I'll use the term "crate" instead of "library crate" in this post.

Java became 30 this year. The ecosystem had time to grow, evolve, and solidify. Moreover, I started very early in the game. When I need a dependency on the JVM, I have my catalog ready. Rust is more recent, and I don't have as much experience with it. In this section, I want to explain my choice of crates.

* HTTP requests: I need TLS, to add request headers, and to deserialize the response body. I successfully used [reqwest](https://docs.rs/reqwest/latest/reqwest/) in the past. It fulfils each of the above requirements. Check!
* Configuration: The component requires several configuration parameters, for example, the list of sensors to query and the credentials to authenticate on the platform. The former is structured configuration; the latter is considered secret. Hence, I want to offer multiple configuration options: a configuration file and environment variables. For this, the [config](https://docs.rs/config/latest/config/) crate fits the bill.
* SMTP: While I had the two previous crates already on my list, I never sent mail in Rust before. A cursory search revealed the `lettre` crate:  
  > Lettre is an email library that allows creating and sending messages. It provides:
  > * An easy-to-use email builder
  > * Pluggable email transports
  > * Unicode support
  > * Secure defaults
  > * Async support
  >
  > -- [Crate lettre on doc.rs](https://docs.rs/lettre/0.11.19/lettre/)

  `lettre` does the job, although it brings along a lot of dependencies (see below).
* Other crates of interest:
  * [serde](https://docs.rs/serde/latest/serde/) for JSON deserialization
  * [strum](https://docs.rs/strum/latest/strum/) for everything around `enum`, including JSON deserialization
  * [anyhow](https://docs.rs/anyhow/latest/anyhow/) for exception handling. For more information, check [Error management in Rust, and libs that support it](https://blog.frankel.ch/error-management-rust-libs/).
  * [chrono](https://docs.rs/chrono/latest/chrono/) for date and time management
  * [log](https://docs.rs/log/latest/log/) and [env_logger](https://docs.rs/env_logger/latest/env_logger/) for logging

## Macros for the win

None of the programming languages I know have macros. In Java, you do meta-programming with reflection, with the help of annotations. Macros are the Rust way to achieve the same, but at compile time.

For example, I wanted to order the sensor results by severity descending before emailing them. Programming languages generally offer two ways to order a `Vec`: by *natural* order and with a dedicated comparator. Rust is no different. In my case, it stands to reason to use the natural order, since I probably won't compare them any other way. For that, `Severity` must implement the `Ord` trait:
> 

```rust
pub trait Ord: Eq + PartialOrd {
   // Required method
   fn cmp(&self, other: &Self) -> Ordering;

   // Provided methods
   fn max(self, other: Self) -> Self
      where Self: Sized { ... }
   fn min(self, other: Self) -> Self
      where Self: Sized { ... }
   fn clamp(self, min: Self, max: Self) -> Self
      where Self: Sized { ... }
}
```

>
> -- [Trait Ord](https://doc.rust-lang.org/std/cmp/trait.Ord.html)

If you think you only need to implement `cmp`, you're mistaken. `Ord` requires both `Eq` and `PartialOrd`, whose functions you also need to implement. The whole trait tree is:

{{< img src="ord-tree-506x510.png" class="aligncenter size-medium" alt="Ord trait implementation tree" width="506" height="510" >}}

While it's possible to implement a couple of functions for the `Severity` enum, most of it can be inferred: an enum is only equal to itself, and its order is its declaration order. The [derive macro](https://doc.rust-lang.org/reference/procedural-macros.html#r-macro.proc.derive) does exactly this. With the help of the `strum` crate, I can implement the above as:

```rust
#[derive(PartialEq, Eq, PartialOrd, Ord)]
enum Severity {
    // Declare levels
}
```

I also need to display, clone, and use the `Severity` as a key in a `HashMap`. The full declaration is:

```rust
#[derive(Debug, Display, Deserialize, PartialEq, Eq, PartialOrd, Ord, Copy, Clone, Hash)]
enum Severity {
    // Declare levels
}
```

## Compilation on Windows

I was living the dream, until I added the `letter` crate. At this point, Rust stopped compiling, citing a linker issue with too many symbols. I first tried to set `default_features = false` to as many crates as possible; I still hit the limit, albeit a bit later. Rust was still able to compile in `--release` mode, because it aggressively optimizes code. Yet, I couldn't just keep developing in release mode only; breakpoints are foundational when debugging. I started to search.

It turns out that the default Rust toolchain on Windows uses Microsoft Visual C++, and it was the culprit. The alternative is to use the GNU toolchain, *i.e.* , `x86_64-pc-windows-gnu`. You need to install [MSYS2](https://www.msys2.org/):
> MSYS2 is a collection of tools and libraries providing you with an easy-to-use environment for building, installing and running native Windows software.

`rustc` complains if it doesn't find the required commands. Install them with MSYS2.

I can't write down in detail, because:

* I went back and forth
* I didn't think about taking notes on my journey; I focused on making things work instead
* The project is on my work laptop

Anyway, I hope it will be enough to help those who find themselves in the same situation.

## Conclusion

This project has been a great opportunity. On one hand, it confirmed that my Rust skills were adequate for a simple component. On the other hand, I could deepen my knowledge of library crates. Finally, I managed to get acquainted with Rust on Windows.

**To go further:**

* [Cross-compilation](https://rust-lang.github.io/rustup/cross-compilation.html)
* [rustup overrides](https://rust-lang.github.io/rustup/overrides.html)
* [reqwest crate](https://docs.rs/reqwest/latest/reqwest/)
* [config crate](https://docs.rs/config/latest/config/)
* [letter crate](https://docs.rs/lettre/latest/lettre/)

*Originally published at [A Java Geek](https://blog.frankel.ch/first-real-rust-project/) on November 23^rd^, 2025*
