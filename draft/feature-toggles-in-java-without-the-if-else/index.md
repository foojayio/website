---
title: "Feature Toggles in Java, Without the If-Else"
date: "2026-01-01"
description: "Why the usual if-else feature toggle turns into debt, how to design toggles cleanly in Java, and how Flipjack moves the decision out of your code."
authors:
  - "kamran-zafar"
image: "preview.jpg"
categories:
  - "Java"
  - "Spring"
  - "Design Patterns"
related_posts:
  - "cloud-agnostic-microservices-with-halyx"
canonical: "https://kamranzafar.org/blog/2026/10/30/feature-toggles-in-java-without-the-if-else/"
---

> Feature toggles are meant to make releases safer. Written the usual way, they also make the code worse, one `if` at a time.

Almost every team I've worked with in the last decade uses feature toggles in some form. They let you merge unfinished work into trunk, switch a new pricing engine on for staging but not production, roll something out to a handful of tenants first, and turn off a misbehaving feature at 2am without a redeploy. None of that is controversial.

What I've found much less talked about is what toggles do to the codebase over time. Most guides, and most library getting-started pages, teach the same pattern, and that pattern is where the trouble starts.

## The pattern everyone starts with

```java
public Quote quote(Cart cart) {
    if (features.isEnabled("new-pricing")) {
        return newPricingEngine.quote(cart);
    } else {
        return legacyPricing.quote(cart);
    }
}
```

It looks harmless. It is one condition, the intent is obvious, and it ships today. The problems show up in month six.

## The problems with feature toggling

### 1. The condition spreads

A feature is rarely one method. The new pricing engine also changes discounts, the quote PDF, the confirmation email and the audit record. Each of those gets its own `if (features.isEnabled("new-pricing"))`, often in different classes written by different people. The toggle is no longer a switch; it is a pattern smeared across the codebase, and nobody can say with certainty where all the copies are.

### 2. Two toggles make four paths

Toggles combine. A method that checks `checkout-v2` and then `express-shipping` has four behaviours, and three toggles in one flow have eight. Each combination is a code path that has to be reasoned about and tested, and in practice most of them never are. The bugs live in the combination nobody thought to turn on in staging.

### 3. Strings fail silently

`isEnabled("new-pricng")` compiles, runs, and returns the default. There is no error, no warning, just a feature that mysteriously never turns on in production, or worse, one that is meant to be off and isn't, because the default was `true`.

### 4. Removal is archaeology

The toggle was supposed to be temporary. Once the feature is fully rolled out, someone has to find every check, delete the `else` branch, delete the old code it called, and hope nothing else depended on it. Because that work is tedious and risky, it doesn't happen. Stale toggles pile up, and with them dead code that still has to compile, still shows up in searches, and still confuses the next person.

### 5. Business logic becomes infrastructure-aware

The method that calculates a quote now knows about a toggle service, which means its unit tests need a toggle service, which means mocks, which means every test that touches pricing has to decide what state `new-pricing` is in. The concern that should be operational (*which version runs right now?*) has leaked into the domain.

### 6. The toggle source becomes a dependency on the hot path

If `isEnabled` calls a remote flag service, a database or a config store, every business call now depends on that system being up and fast. When it isn't, what happens? Does the call throw? Does it return `false`? Does it return whatever the last caller saw? Most code using the pattern above never makes a decision about this, so the answer is whatever the client library happens to do.

## What the current libraries give you

The Java ecosystem has mature options: **Togglz**, **FF4J**, **Unleash**, and hosted services such as **LaunchDarkly** and **Harness Feature Management**. They are good at what they focus on: storing flags, administration consoles, user and percentage targeting, audit logs, and in the hosted case, governance and analytics.

But look at the first code sample in almost any of their docs and you'll find some version of:

```java
if (MyFeatures.NEW_PRICING.isActive()) { ... }      // Togglz
if (ff4j.check("new-pricing")) { ... }               // FF4J
if (unleash.isEnabled("new-pricing")) { ... }        // Unleash
```

To be fair, some of them do offer more structured options. Togglz has a Spring proxy factory that switches between two implementations, and FF4J has AOP-based flipping with `@Flip`. But these are side features, often tied to one container, and the path the documentation leads you down is the `if`. The libraries solve *where the flag lives* and *who can change it*. They mostly leave *how your code reacts to it* up to you, and the default answer is a condition in the middle of the business logic.

That is the part I think deserves more design attention.

## How toggles should be designed

Here is the set of principles I've settled on. None of them need a library; they are just good object-oriented design applied to a problem that usually gets a quick fix.

### Principle 1: Toggle at a seam, not inside the logic

A toggle chooses between two behaviours. In Java, two interchangeable behaviours already have a name: two implementations of an interface. Give the feature a seam and put each version behind it.

```java
public interface PricingService {
    Quote quote(Cart cart);
    Money discount(Cart cart);
}

public class LegacyPricing implements PricingService { ... }
public class NewPricing implements PricingService { ... }
```

Neither class knows a toggle exists. `NewPricing` is just the new pricing engine, written and tested as if it were the only one. `LegacyPricing` is untouched.

### Principle 2: Decide in exactly one place

Something still has to pick between them on every call (the whole point of a runtime toggle is that it can change while the app is running). Put that decision in a single router at the seam, so callers inject `PricingService` and never see a condition:

```java
final class PricingRouter implements PricingService {
    private final PricingService enabled, fallback;
    private final FeatureToggles toggles;

    PricingRouter(PricingService enabled, PricingService fallback, FeatureToggles toggles) {
        this.enabled = enabled;
        this.fallback = fallback;
        this.toggles = toggles;
    }

    private PricingService current() {
        return toggles.isEnabled(Features.NEW_PRICING) ? enabled : fallback;
    }

    public Quote quote(Cart cart)    { return current().quote(cart); }
    public Money discount(Cart cart) { return current().discount(cart); }
}
```

There is still one condition, but now it is *the* condition: one per feature, in a class whose only job is routing. Every method on the interface follows the same toggle, so no call can get the new quote with the old discount. (The toggle is read on each call, so a flag flipped *between* `quote()` and `discount()` takes effect from the next call.) The combinatorial explosion from problem 2 is bounded by structure rather than by discipline.

### Principle 3: Removal should be a deletion

With the seam in place, retiring the toggle is mechanical: delete `LegacyPricing`, delete the router, bind `PricingService` to `NewPricing`. No branches to untangle, no `else` blocks to read carefully. When cleanup is that cheap, it actually gets done.

### Principle 4: Names are a contract, so check them

Keep feature names as constants in one catalogue per service, so the IDE can find and rename them:

```java
public final class Features {
    public static final String NEW_PRICING   = "new-pricing";
    public static final String FAST_CHECKOUT = "fast-checkout";
}
```

Then, at startup, compare the features your code uses with the features your store holds. Anything used but not configured is probably a typo; anything configured but unused is probably a stale toggle waiting to be deleted. A five-line check at boot catches the class of bug that otherwise surfaces as "the feature never turned on".

### Principle 5: Treat evaluation as infrastructure, and decide how it fails

The toggle source should sit behind one small interface, with caching, fail-safe behaviour and overrides layered on as decorators rather than baked into each call site:

```java
public interface FeatureToggles {
    boolean isEnabled(String feature);
}

FeatureToggles toggles = new CachingToggles(
        new FailSafeToggles(remoteSource, /* default */ false),
        Duration.ofSeconds(30));
```

Two failure modes, two decisions, made once:

- **At startup, fail loudly.** A router with no fallback, or a fallback that doesn't implement the interface, is a configuration bug. Find it when the application boots, not on the first request.
- **At runtime, fail safely.** If the flag store is unreachable, keep the last known state or a known default. Never let an outage in the toggle system become an outage in the business system.

And keep a runtime override above everything else, so a kill switch works even when the store doesn't.

### Principle 6: Pass the targeting context implicitly

Per-user and per-tenant rollouts need to know who the caller is. The tempting design is to add a `user` parameter to every method that might be toggled. Don't: that leaks toggle concerns into every signature. Read the context once per call at the routing layer (from the security context, or a request-scoped holder) and hand it to the toggle source. The business methods stay exactly as they were.

## Flipjack: those principles as a library

Writing a router class per feature works, but it's boilerplate, and boilerplate is where shortcuts creep back in. That is why I built **[Flipjack](https://github.com/kamranzafar/flipjack)**, a small open-source library (Java 17+, Apache 2.0, no runtime dependencies in core) that implements the design above so your feature code contains no toggle checks at all.

### Replacing an implementation

The router from Principle 2 becomes an annotation:

```java
@Service
public class LegacyPricing implements PricingService { ... }

@Service
@Toggleable(value = Features.NEW_PRICING, fallback = LegacyPricing.class)
public class NewPricing implements PricingService { ... }
```

Callers inject `PricingService`. A dynamic proxy checks `new-pricing` on every call and routes to `NewPricing` or `LegacyPricing`. That proxy is the only place in the application where toggles are evaluated. A feature the store has no value for is off by default, so a missing entry takes the fallback rather than switching new code on.

### Toggling individual methods

When only part of a component changes, toggle the method. Each one gets its own feature and its own behaviour when off:

```java
@Service
public class OrderService implements Orders {

    @Toggleable(value = Features.FAST_CHECKOUT, fallbackMethod = "standardCheckout")
    public Receipt checkout(Cart cart) { ... }

    private Receipt standardCheckout(Cart cart) { ... }

    @Toggleable(value = Features.ORDER_EMAILS, whenDisabled = WhenDisabled.NO_OP)
    public void sendConfirmation(OrderId id) { ... }

    public Order find(OrderId id) { ... }   // not toggled
}
```

### Toggling one step inside a method

Sometimes the change is one line in a long method, say a tax calculation. Hold that step in a field, and the method body stays condition-free:

```java
@Service
public class CheckoutService {
    private final TaxRule tax;

    public CheckoutService(FeatureToggles toggles) {
        this.tax = ToggledFeature.either(TaxRule.class, Features.REGIONAL_TAX,
                this::regionalTax, this::flatTax, toggles);
    }

    public Money total(Cart cart) {
        return cart.subtotal().plus(tax.tax(cart));
    }

    private Money regionalTax(Cart cart) { ... }
    private Money flatTax(Cart cart) { ... }
}
```

### Evaluation as a decorator chain

Principle 5 maps directly onto Flipjack's sources and decorators:

```java
OverridableFeatureToggles toggles = new DatabaseFeatureToggles(dataSource)   // your own source
        .cached(Duration.ofSeconds(30))
        .failSafe(false)
        .withOverrides();

toggles.override(Features.NEW_PRICING, false);   // kill switch during an incident
```

### Failing at startup, not at 2am

Routes are resolved and validated at startup: a missing fallback bean, a `fallbackMethod` with the wrong signature, or an annotated method that isn't on any interface all stop the application from starting. The consistency check from Principle 4 runs automatically in Spring Boot, and can be made strict:

```yaml
flipjack:
  consistency-check: fail   # off | warn | fail
  toggles:
    new-pricing: true
    fast-checkout: false
```

### Targeting without changing signatures

Principle 6 is a single bean. Every toggled component reads it once per call, so class and method toggles always decide for the same caller:

```java
@Bean
FeatureContextProvider featureContext() {
    return () -> {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? FeatureContext.EMPTY : FeatureContext.of(auth.getName());
    };
}
```

`GET /actuator/flipjack` lists every feature with its effective state, and `GET /actuator/flipjack/{name}?subject=alice` evaluates one for a specific user.

### Loading toggles from your own config store

Your application already knows how to read config from wherever your organisation keeps it, whether that's SSM, Vault or something in-house. It keeps doing that, and hands Flipjack the values through a one-method loader:

```java
FeatureToggles toggles = PollingFeatureToggles.builder(toggleConfig::loadFeatureToggles)
        .interval(Duration.ofSeconds(30))
        .build();
```

`PollingFeatureToggles` calls the loader once per interval, a single bulk read, and answers every check from an in-memory snapshot, so toggle checks never touch the network. If a load fails, or a value isn't `true`/`false`, the last good snapshot is kept and calls keep routing by it. If the very first load fails, the service starts with every feature off, so calls take their fallbacks; call `.requireInitialLoad()` on the builder to fail startup instead.

### Using a flag platform

If your flags already live in a platform such as Unleash, LaunchDarkly or flagd, the `flipjack-openfeature` module uses any [OpenFeature](https://openfeature.dev) provider as the source. The platform keeps doing what it's good at: storing flags, targeting and rollouts. Flipjack does the routing, so the `if` from those libraries' getting-started pages never reaches your code.

```java
FeatureToggles toggles = OpenFeatureToggles.builder(OpenFeatureAPI.getInstance().getClient())
        .requireReady(Duration.ofSeconds(10))   // fail startup if the provider isn't ready
        .build()
        .withOverrides();                       // kill switches still work if the platform is down
```

It follows the same principles as the rest of Flipjack:

- **It fails the way Principle 5 asks.** An evaluation never throws: a missing flag, an unready provider or any other error returns the default and goes to an error handler, which by default logs each problem once rather than on every call.
- **Targeting comes from the same `FeatureContextProvider`.** The subject becomes the provider's targeting key, so per-user rollouts on the platform still need no signature changes.
- **It usually needs no cache.** Most providers keep flags in memory and update them by streaming or polling. Check yours: flagd's Java provider, for example, evaluates over RPC by default and only keeps flags in memory in its in-process mode.

The one thing you give up is Principle 4's startup check: OpenFeature can't list its flags, so a misspelt name isn't caught at boot. It shows up as a "flag not found" warning the first time it's evaluated.

## How it answers the challenges

| Challenge | Flipjack's answer |
|---|---|
| Conditions spread through the code | One proxy per component is the only place toggles are checked |
| Toggle combinations multiply | A class toggle gates every method together; method toggles refine it in a fixed order |
| Misspelt names fail silently | Name constants plus a startup consistency check that can fail the boot, and unconfigured features default to off |
| Removal is archaeology | Delete the annotation and the fallback class |
| Business code knows about toggles | Feature classes are plain implementations, tested without a toggle service |
| Per-user rollouts change signatures | A `FeatureContextProvider` read once per call and handed to the source |
| The flag store is on the hot path | Snapshot polling, caching, fail-safe defaults and overrides as decorators; OpenFeature providers that keep flags in memory |

## Where it isn't the answer

Flipjack is deliberately small, and it's worth being clear about what it doesn't do. It proxies interfaces, so a toggled type needs one. Like any proxy, method-level toggles don't apply to calls a component makes to itself (that's what `either` steps are for).

More importantly, Flipjack is designed to be lightweight, and that is a choice, not a gap waiting to be filled. It doesn't own your toggle configuration. Core doesn't talk to SSM, Vault or a database itself; it routes calls by whatever values your loader hands it. Flag platforms plug in through OpenFeature, a standard API, rather than a client Flipjack maintains for each one. Storing, editing and governing the flags stays with the config store or platform and the processes you already have around it.

So it isn't meant to compete on the features other libraries offer: there is no admin console, no user management, no approval workflows, no analytics, and the built-in sources don't do percentage rollouts. If you need targeting or rollouts, use a flag platform through `flipjack-openfeature`, or implement the two-argument `isEnabled` on your own source. Each of those features is a dependency, a surface to secure and a thing to operate, and most services don't need them to switch a code path safely.

That is the useful way to think about it. Flipjack isn't a replacement for Unleash or LaunchDarkly as a *flag store*; with OpenFeature, it works alongside them. It's a replacement for the `if` statement. You can keep your flags wherever your organisation already manages them, and use Flipjack to keep them out of your business logic.

## Getting started

```xml
<dependency>
    <groupId>org.kamranzafar.flipjack</groupId>
    <artifactId>flipjack-spring-boot-starter</artifactId>
    <version>1.1.1</version>
</dependency>
```

The starter works with Spring Boot 3.5 and 4.1. Use `flipjack-core` instead for plain Java or another DI container, and add `flipjack-openfeature` to use an OpenFeature provider as the source. The [README](https://github.com/kamranzafar/flipjack) covers wiring without Spring, Kotlin, per-user toggles, loading toggles from your own config store, and OpenFeature. Issues and contributions are welcome, and there are a few [good first issues](https://github.com/kamranzafar/flipjack/labels/good%20first%20issue) if you'd like to get involved.

Toggles should make releases safer without making the code worse. Put the decision at a seam, make it in one place, and let removal be a deletion. Whether or not you use Flipjack to do it, your future self, the one who has to clean up the toggles, will thank you.

