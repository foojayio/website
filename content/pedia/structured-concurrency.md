---
title: "Structured Concurrency"
description: "Structured Concurrency, finalised in Java 24 (JEP 499), is a programming model that treats concurrent tasks executing in parallel as a single unit of work. The key insight is that tasks spawned within a scope must complete before that scope ..."
url: "/pedia/structured-concurrency/"
frozen: false
---

Structured Concurrency, finalised in Java 24 (JEP 499), is a programming model that treats concurrent tasks executing in parallel as a single unit of work. The key insight is that tasks spawned within a scope must complete before that scope exits — just as sequential control flow requires a called method to return before the caller continues.

The `StructuredTaskScope` API encapsulates this: you open a scope, submit subtasks to it, then wait for them to finish (or for the scope's configured policy to trigger).

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<User>    user    = scope.fork(() -> fetchUser(id));
    Subtask<Basket>  basket  = scope.fork(() -> fetchBasket(id));

    scope.join().throwIfFailed();

    return new Page(user.get(), basket.get());
}
```

Two built-in policies ship with Java 24: `ShutdownOnFailure` (cancels remaining tasks if any subtask fails) and `ShutdownOnSuccess` (cancels remaining tasks as soon as any subtask succeeds). Custom policies can be written for more complex scenarios.

Structured Concurrency prevents several concurrency hazards that are difficult to avoid with `ExecutorService`: thread leaks (tasks outliving their logical scope), lost exceptions (failure in one task being silently ignored while others run), and difficult cancellation semantics. It integrates directly with [Virtual Threads](https://foojay.io/pedia/virtual-threads/) and [Scoped Values](https://foojay.io/pedia/scoped-values/).
