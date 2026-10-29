---
title: "Checking Module Boundaries in a Java Monolith"
date: "2026-11-10"
description: "Use an ArchUnit test to check a Java module boundary, examine a violation and move the dependency behind the module's API."
authors:
  - "omphile-matheolane"
image: "module-boundaries-cover.jpg"
categories:
  - "Java"
  - "Testing"
related_posts:
  - "spring-modulith-modularity-maturity"
  - "monolith-vs-microservices-2025"
  - "green-build-wrong-task-a-working-agreement-for-java-teams-using-ai-coding-agents"
  - "testing-jakarta-ee-three-layers-three-tools"
---

A module boundary often starts with a perfectly reasonable agreement: ordering can use the payments API, while the payment implementation stays inside payments. Everyone understands it, the diagram looks tidy, and the team gets on with building the application.

A few features later, someone finds a useful method on a public class in `payments.internal`. Using it saves a little work, the compiler is happy, and the tests stay green. The package name offers a quiet objection, but it has no vote in the build.

That dependency may look harmless until the payments team changes its implementation and discovers that ordering has acquired an interest in the details. By then, the original decision needs to be recovered from a conversation, a diagram, or whoever still remembers why the boundary was drawn there.

I would rather have that discussion while the dependency is being introduced. In this article, we will turn the agreement between ordering and payments into an architecture test, deliberately break it, and then change the dependency so the test passes.

The example uses [Modulith Rules](https://github.com/modular-monolith/modular-monolith-rules), an open-source library I maintain that provides pre-built [ArchUnit](https://www.archunit.org/) rules. Its core works without Spring and runs in test scope, alongside the application's other tests.

## Where the package boundary needs help

Our application contains two business modules under `com.shop`: `ordering` and `payments`. Payments exposes the following interface in its API package:

```java
// src/main/java/com/shop/payments/api/PaymentGateway.java
package com.shop.payments.api;

public interface PaymentGateway {
    void pay();
}
```

The implementation lives in a separate package within payments:

```java
// src/main/java/com/shop/payments/internal/PaymentGatewayImpl.java
package com.shop.payments.internal;

import com.shop.payments.api.PaymentGateway;

public class PaymentGatewayImpl implements PaymentGateway {
    @Override
    public void pay() {}
}
```

I have left the method empty so we can concentrate on the dependency. A payment implementation that does nothing would make for a fairly relaxed demonstration, although I would keep it well away from checkout.

The implementation is public, allowing other packages within payments to use it. The same visibility also lets ordering reach straight into the implementation:

```java
// src/main/java/com/shop/ordering/internal/OrderService.java
package com.shop.ordering.internal;

import com.shop.payments.internal.PaymentGatewayImpl;

public class OrderService {
    private final PaymentGatewayImpl payments;

    public OrderService(PaymentGatewayImpl payments) {
        this.payments = payments;
    }

    public void placeOrder() {
        payments.pay();
    }
}
```

This compiles because the class and its method are public. To the Java compiler, `internal` is simply another part of a package name. We have expressed an intention that a developer can read, without giving the build anything to check.

Package-private visibility helps when a type only needs to be used within one package. A business module often spans several packages, though, and some implementation types need to be public so those packages can collaborate. We still want to keep callers from other modules out of those implementation details. Here, ordering should use the payments API and leave its internals alone.

## Expressing the decision in a test

The [complete example on GitHub](https://github.com/omphile-matheolane/Resources/tree/main/boundary-example) contains two Maven projects, `before` and `after`, with the source files and build configuration used below. You will need Java 17 or newer.

For the architecture test, add the core library and JUnit in test scope:

```xml
<dependency>
    <groupId>io.modularmonolith</groupId>
    <artifactId>modulith-rules-core</artifactId>
    <version>0.2.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>6.1.2</version>
    <scope>test</scope>
</dependency>
```

We can then register the two modules using their package names and select the rule that protects internal packages. The convention-based configuration derives `com.shop.ordering` and `com.shop.payments` from the root package and the module names:

```java
// src/test/java/com/shop/architecture/ArchitectureTest.java
package com.shop.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import io.modulith.rules.ModulithRules;
import org.junit.jupiter.api.Test;

class ArchitectureTest {
    @Test
    void modulesDoNotAccessEachOthersInternals() {
        var classes = new ClassFileImporter().importPackages("com.shop");

        ModulithRules.forPackage("com.shop", "ordering", "payments")
            .boundaryRules()
            .internalsShouldNotBeAccessedFromOutside()
            .check(classes);
    }
}
```

Run the version that contains the direct implementation dependency:

```bash
cd boundary-example/before
mvn test
```

The test reports that `OrderService` accesses `payments.internal.PaymentGatewayImpl` across the module boundary. Its diagnostic identifies the class involved and suggests exposing a service interface through the payments API. We already have that interface, which makes this a pleasantly small change.

Java has checked its visibility rules, and the architecture test has checked the additional restriction we agreed for our modules. A reviewer now has a specific dependency to discuss alongside the change. Remembering every boundary in the application becomes a little less essential to having a useful code review.

## Moving the dependency to the API

The ordering service can depend on `PaymentGateway` by changing its import, field type and constructor parameter:

```java
package com.shop.ordering.internal;

import com.shop.payments.api.PaymentGateway;

public class OrderService {
    private final PaymentGateway payments;

    public OrderService(PaymentGateway payments) {
        this.payments = payments;
    }

    public void placeOrder() {
        payments.pay();
    }
}
```

The same architecture test now passes. Ordering uses the contract exposed by payments, so changes behind that contract have more room to stay within the payments module. We still need to design the interface carefully, of course; putting an interface in an `api` package does little good if its callers need to understand the implementation to use it.

An implementation still needs to be supplied when the application is assembled. The composition root or dependency injection configuration handles that wiring. I have left the mechanism out of this example because the boundary check works independently of how the application supplies the implementation.

The example's `after` project contains the corrected service:

```bash
cd ../after
mvn test
```

## Introducing checks into an existing codebase

An existing codebase is likely to have more opinions about its boundaries than this example does. Switching on every rule at once can produce a long list of violations, some of which reflect deliberate compromises and others decisions nobody remembers making. Working through that list takes time, especially where changing a dependency affects behaviour that the team needs to preserve.

I would begin with one boundary that the team understands well enough to discuss. Agree which dependencies belong there, add a focused check, and work through what it finds. That gives people a chance to judge whether the rule helps before the build starts offering architectural advice about the entire application.

Modulith Rules also supports allowed dependency lists and cycle detection, with module definitions expressed in Java or YAML. The convenience methods group checks together: `allRules()` includes allowed dependencies, internal-package protection and module cycles, while `allCoreRules()` adds API-only access and communication-contract checks. Spring-specific rules are selected separately.

The configuration has some permissive defaults that matter when interpreting a passing result:

- An empty `allowedDependencies` configuration leaves a module unrestricted by the allowed-dependency rule. To require zero outgoing dependencies, use the dedicated `moduleShouldHaveNoDependencies(name)` rule.
- API-only access skips target modules that have no configured API packages.
- Classes outside registered module packages are ignored by these module rules.
- Without explicit internal-package configuration, the library uses the `.internal.` and `.infrastructure.` conventions.

These defaults make gradual adoption possible, but they also deserve a careful read. A green test can be reassuringly quiet when we have forgotten to tell it what to inspect. Before relying on the result, check that the relevant modules are registered and that the selected rules cover the dependencies you intend to restrict.

## Understanding what the result tells us

A passing boundary test tells us something useful about the code we imported and the rules we selected. We still need to look at the other ways the modules depend on each other.

Two modules can satisfy every Java dependency rule in the build and still share a database table. Change that table and both modules may need attention. Similarly, inspecting code dependencies tells us little about what happens to an event when the process handling it fails halfway through its work.

The library's communication contracts need to be read with those limits in mind. An `ASYNCHRONOUS` declaration checks for prohibited direct calls, with a documented exception for accessor calls on classes whose simple names end in `Event`. A passing result does not establish whether event delivery is reliable or whether a consumer handles duplicate messages. Dashed arrows in the exported dependency graph represent declared asynchronous contracts rather than observed runtime behaviour.

I would keep these checks alongside behavioural tests and explicit decisions about data ownership. Each gives us a different view of the application, and a passing architecture test is easier to trust when we are clear about the questions it can answer.

## Considering Spring Modulith

[Spring Modulith](https://docs.spring.io/spring-modulith/reference/) already provides structural verification through `ApplicationModules.verify()`, including checks for module cycles, access to internals and explicitly allowed dependencies. That verification can run during tests without adopting all of Spring Modulith's runtime capabilities.

Its wider scope includes module integration testing and generated documentation, together with optional event publication infrastructure and observability. Modulith Rules focuses on configurable ArchUnit policies, with a framework-independent core and optional checks for Spring-specific architecture conventions.

If your team already uses Spring Modulith, start by checking whether its verification covers the boundary you want to protect. There is little reward for making the build check the same decision twice. Modulith Rules is worth considering where its additional policies help, or where you want a core library that works without Spring.

## Start with one boundary

The [before-and-after example](https://github.com/omphile-matheolane/Resources/tree/main/boundary-example) provides a small starting point. The [documentation](https://modularmonolith.io/) describes the available rules and their behaviour. Modulith Rules is licensed under Apache License 2.0, and questions or problems can be raised through the [issue tracker](https://github.com/modular-monolith/modular-monolith-rules/issues).

When you try a rule in your own codebase, give it something to complain about first. Add a dependency you know should fail and check that the test catches it, then remove it and keep the check in the build. The next person who reaches into an internal package will have the boundary explained while the change is still small, even if the original design discussion happened long before they joined the team.
