---
title: "Scripting in small apps: you don't need a container for a macro"
date: "2026-08-25T07:46:06+00:00"
lastmod: "2026-08-25T07:46:07+00:00"
description: "Disclosure: I maintain Aussom, an Apache 2.0 interpreted language for the JVM. It's the approach I argue for here, and I have tried to be clear about…"
authors:
  - "austin-lehman"
image: "aussom-logo-atom.png"
categories:
  - "Java"
related_posts:
  - "java-for-scripting"
  - "six-jdk-24-features-you-should-know-about"
  - "foojay-podcast-97"
frozen: false
---

*Disclosure: I maintain Aussom, an Apache 2.0 interpreted language for the JVM. It's the approach I argue for here, and I have tried to be clear about where it is the wrong choice.*

Your app has a scripting feature, or it should: a macro system, a rules layer, plugin hooks, something that lets users extend the app and tailor it for their use.

For twenty years the JVM answer has been to embed Groovy or a JavaScript engine and lean on the Security Manager. That option is gone now. [JEP 486](https://openjdk.org/jeps/486) disabled it in JDK 24, and it can't be turned back on. OpenJDK's own wording is that the platform no longer has a sandbox.

Almost everything written since assumes you run a multi-tenant cloud platform. Isolated heaps, resource metering, microVMs, out-of-process workers. Good advice, and close to useless if you ship a desktop app, an internal tool, or a small self-hosted server.

Small apps need a different answer. Here is the case for it.

## The usual options, and why they don't fit

|        Option        |                                                                                              The problem                                                                                               |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Containers, microVMs | You can't ask a user to install Docker to run macros in your image editor.                                                                                                                             |
| A second process     | Seconds of startup for something that should feel instant, more RAM, and now you own inter-process communication, timeouts, and orphaned processes.                                                    |
| GraalVM isolates     | You also adopt GraalVM, add a native-image build step, and ship a separate artifact per operating system.                                                                                              |
| WebAssembly          | But nobody writes Wasm by hand, and your users will not install Rust to write a macro. Put a JavaScript engine inside the module and you are running an interpreter, inside a sandbox, inside the JVM. |

Notice what causes the friction; it's not the threat model, it's **distribution.** What you can ask a user to install, what fits in a download, and what you can support with no operations team behind you.

## What small apps actually need: simplicity

Simplicity first, and it's not a consolation prize, it's the product.

Every option above is a system you have to configure correctly to be safe, and each has enough moving parts that a small team will get one of them wrong.

**A boundary you can't read is not a boundary you can trust.** That matters more for small apps than large ones, because of who stands behind the software. A cloud platform has a security team and can patch every instance on a Friday afternoon. A desktop app ships to a user's machine and runs there for years on whatever version they installed last. You find out about problems from a forum post.

So the number of things that can be misconfigured matters more than the strength of any one of them. An interpreter you can read end to end, where everything a script can do is a list you wrote, is more reliable in practice, because you can check that it holds.

Two other things matter:

**Stopping a runaway script.** The failure your users will actually hit is likely not data theft; it's a macro with an infinite loop freezing the UI. People notice that weekly; they never notice a sandbox working.

**Scripts the user didn't write.** If a user writes their own macro, there's no malicious code to defend against. The sandbox defends against scripts from somewhere else; whether it's a community forum, a project file, a colleague, or a marketplace, this is the macro virus problem.

## What you get

One jar. No runtime swap, no build step, no second process, no daemon.

The numbers below come from a load test on one eight-core laptop with other work running.

**An engine costs about 2.6 milliseconds to create** once the JVM is warm, and holds **0.37 MB** while idle. That's not a typo. You can give every open document its own engine, throw it away, and build another between keystrokes.

**Twenty thousand engines fit in one JVM**, at 7.5 GB total, built in 29 seconds. The per-engine cost stays flat at 0.37 MB the whole way from ten engines to twenty thousand.

**A thousand engines ran at once and used all eight cores.** Four thousand script runs across four rounds returned zero wrong answers.

**Stopping a script takes one call from any thread**, and it takes effect within a millisecond.

**Your users already know the syntax:** Curly braces and semicolons, `class`, `public`, `new`, `this`. `if`, `while`, `for`, `switch`, `return`. `try`/`catch`/`throw`. `//` comments. Anyone who has written Java, C#, or JavaScript can read an Aussom macro on sight and guess right about the rest. Types on parameters are optional, so short things stay short. Lists and maps have the literal syntax you would expect, `[1, 2, 3]` and `{ x: 1, y: "two"}`, and `for (item : list)` iterates.

**Java is there when you want it, on your terms.** An `extern class` from Aussom binds the Aussom class to a Java one, so files, the network, and your own application objects are all reachable if you decide they should be. What makes that safe is that you name what is reachable. Turn on the allowlist flag and anything not on your list is refused when the script is parsed, before a line of it runs:

```
Extern class 'java.lang.ProcessBuilder' is not permitted. Add it, or a
matching '<package>.*' prefix, to the security manager property
'aussom.extern.allowed'.
```

**This is the difference from embedding a general-purpose JVM scripting engine and then trying to restrict it.** With Aussom, the intended model is the reverse: Java integration is explicitly exposed, and the allowlist starts empty.

**The interpreter 'aussom-base' is an Apache 2 licensed artifact available on Maven Central.** Every line of source code is downloadable and inspectable on GitLab and GitHub.

## Hello world

This is the policy class that says what user code may do. In this case, it's enabling script mode for handling simple expressions.

```java
import com.aussom.Engine;
import com.aussom.SecurityManagerImpl;
import com.aussom.types.AussomType;

/** What user expressions may do. Everything else is off already. */
final class MacroPolicy extends SecurityManagerImpl {
    MacroPolicy() {
        this.props.put("aussom.script.mode.enable", true);
    }
}
```

That class is the entire security configuration; there's no other place to look. Reflection, evaluating a string as code, the debugger, the test runner, and the system properties a script could use to fingerprint the machine are all off in the base class. You turn on what you need in your policy.

Now run something a user typed into a settings box:

```java
Engine engine = new Engine(new MacroPolicy());
engine.setScriptMode(true);

engine.evalLine("price = 250;");
AussomType total = engine.evalLine("price * 0.9;");

System.out.println(total.getValueString());   // 225.0
```

That is a complete customization. Six lines, and your users can write the discount rule instead of filing a feature request.

If the expression runs away, stop it:

```java
engine.cancel();   // safe from any thread
```

The script stops at its next checkpoint: every loop, every call. Running code stops almost immediately, around a tenth of a millisecond in my measurements. The user sees an instant response, and the UI thread was never blocked to begin with.

There isn't enough surface here to get it wrong. That's the argument.

That example uses the direct `Engine` API, which is the one worth reaching for when you want to hold a script, cancel it, and inspect what happened. If you would rather go through `javax.script`, Aussom implements JSR 223 too. The [Embedding Aussom overview](https://aussom-lang.com/docPage?product=aussom-base&page=written/aussom-lang-embedding-overview.md&title=Embedding%20Aussom%20Overview) is a one-screen comparison of the two methods that helps you choose between them.

## Where this leaves you

You want a user to be able to write a discount rule, a rename pattern, or a chart formula. You don't want to ship a plugin SDK, and you certainly don't want to ship a container.

The whole feature is a single jar, one policy class, and six lines to evaluate something a user typed. An engine costs 0.37 MB and under three milliseconds.

You will configure it correctly because there is almost nothing to configure. For a scripting feature, that's hard to beat.
