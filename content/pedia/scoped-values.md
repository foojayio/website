---
title: "Scoped Values"
description: "Scoped Values, finalised in Java 24 (JEP 487), provide a mechanism to share immutable data within and across threads in a structured, predictable way — designed as a safer and more scalable alternative to ThreadLocal in the era of virtual ..."
url: "/pedia/scoped-values/"
frozen: false
---

Scoped Values, finalised in Java 24 (JEP 487), provide a mechanism to share immutable data within and across threads in a structured, predictable way — designed as a safer and more scalable alternative to `ThreadLocal` in the era of virtual threads.

A `ScopedValue` binds a value for the duration of a delimited scope (a lambda or method call). Any code executing within that scope — including code in child threads spawned via structured concurrency — can read the value. When the scope exits, the binding is automatically removed.

```java
static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

ScopedValue.where(CURRENT_USER, authenticatedUser)
    .run(() -> {
        // Any code here, including virtual threads, can read CURRENT_USER.get()
        processRequest();
    });
```

The key advantages over `ThreadLocal`:

* **Immutable** — a scoped value cannot be mutated after binding, eliminating a class of hidden-state bugs.
* **Structured lifetime** — the binding is automatically removed when the scope exits, with no risk of thread pool leakage.
* **Virtual-thread friendly** — with millions of virtual threads, per-thread mutable state is expensive; immutable scoped values are shared cheaply.

Scoped Values are designed to work in conjunction with [Structured Concurrency](https://foojay.io/pedia/structured-concurrency/) and [Virtual Threads](https://foojay.io/pedia/virtual-threads/).

## See Also

* [Structured Concurrency](/pedia/structured-concurrency/)
* [Virtual Threads](/pedia/virtual-threads/)
* [Records](/pedia/records/)
* [Project Loom](/pedia/project-loom/)
