---
title: "Finally final: embracing JEP 500"
date: "2026-11-26"
description: "JEP 500 makes final mean final. How Tentackle's TRIP serializer stopped writing final fields for good, and a blueprint you can reuse for your own framework."
authors:
  - "harald-krake"
image: "finally-final-jep-500.jpg"
categories:
  - "Java"
  - "JEPs"
  - "Java Core"
related_posts:
  - "java-26-is-here-and-with-it-a-solid-foundation-for-the-future"
  - "java-26-whats-new"
  - "reflection-free-serialization-on-the-jvm-four-formats-in-avro-out"
  - "explaining-java-deserialization-vulnerabilities-part-1"
---

Every Java developer learns early on that a `final` field is assigned exactly once. Fewer learn the
footnote: *unless somebody calls `setAccessible(true)`*. For almost thirty years, a `final` field has
been final for you and me, but not for the frameworks running our code. They could, and regularly
did, write to it whenever it suited them.

With JDK 26 that footnote is on its way out. [JEP 500, *Prepare to Make Final Mean
Final*](https://openjdk.org/jeps/500), starts the countdown, and this article is about what that means
for the code that has relied on the footnote the most: serializers.

## JEP 500 in two minutes

Since JDK 26, writing a `final` field through deep reflection (`Field::set` after `setAccessible(true)`)
still works, but the JVM prints a warning. That's the friendly phase. The JEP is very clear that a
future release will switch the default to *deny*, and from then on the same call throws an
`IllegalAccessException`.

Two command-line options control the behaviour:

- `--enable-final-field-mutation=M1,M2` lets the named modules (or `ALL-UNNAMED` for the class path)
  keep mutating finals without a warning. That's an explicit opt-in, the same idea as `--add-opens`.
- `--illegal-final-field-mutation=allow|warn|debug|deny` decides what happens to everybody else. `warn`
  is today's default, and `deny` is tomorrow's.

Why bother? Because a `final` that can be changed behind your back is not a guarantee, it's a
suggestion. When `final` really means final, you can reason about your objects with confidence, and so
can the JIT: it can treat final instance fields as constants and fold them away.

{{< tip >}}
Try running your application with `--illegal-final-field-mutation=deny` today. Anything that breaks
would break anyway, just later and at a less convenient time.
{{< /tip >}}

## Who should care

Most application code will never notice. People don't usually write `Field.set` on a final field in
business logic, and the ones who do probably already feel bad about it.

The code that *does* notice is the infrastructure that builds objects behind your back: dependency
injection containers, mocking libraries, and above all **serializers**. For decades, the standard recipe
for deserialization has been:

1. Allocate an empty object, ideally without running any constructor
   (`Unsafe.allocateInstance`, `ReflectionFactory`, or the way Java Object Serialization does it).
2. Fill in the fields one by one through reflection, `final` or not.

It's convenient, and it has two well-known downsides. Constructors never run, so the invariants they
check are never checked. And an object built from untrusted bytes with no constructor in the way is
exactly the raw material of the deserialization gadget chains that kept security teams busy for the
last decade. JEP 500 now takes away the second step for final fields.

The JEP does leave serializers one door open: `sun.reflect.ReflectionFactory` remains supported for
deserializing `Serializable` classes. That's the quick fix. It also keeps constructors bypassed, works
only for `Serializable` classes, and ties you to an internal API in `jdk.unsupported`. We wanted a
proper fix instead.

## What this post is about

[Tentackle](https://tentackle.org/) is an open source Java framework for multi-tier applications, and
[TRIP](https://tentackle.org/trip/), the Tentackle Remote Invocation Protocol, is its replacement for RMI
and Java serialization. TRIP moves real object graphs between JVMs, with identity, cycles and
polymorphism preserved: you send an `Order`, and the receiving side gets the same `Order`, with its
lines pointing back to it.

From [Tentackle 25.20.1.0](https://github.com/krake-com/tentackle/blob/develop/CHANGELOG.md) on, TRIP
**never writes a final field**. This post shows how we got there and what it took. None of it is specific
to TRIP. If you maintain a serializer, a mapper or anything else that builds objects from data, think of
it as a blueprint you can borrow.

## The chicken and the egg

Here's the running example: an order, its lines, and a small immutable value class.

```java
public class Order {
  private Customer customer;
  private List<OrderLine> lines = new ArrayList<>();
}

public class OrderLine {
  private Order order;           // points back to the order: a cycle
  private String article;
  private Money price;
}

public final class Money {
  private final BigDecimal amount;
  private final String currency;

  public Money(BigDecimal amount, String currency) {
    this.amount = Objects.requireNonNull(amount);
    this.currency = Objects.requireNonNull(currency);
  }
}
```

Writing this graph is easy. Remember which objects you've already written, and when one shows up a
second time, write a short reference ("see object #0") instead of the object itself.

Reading it back is the tricky part. When the reader arrives at `OrderLine.order` and finds "see object
#0", that order must already exist, even though the reader is still busy reading the order's `lines`.
The order has to be created *before its contents are known*. That's why "allocate first, fill in later"
became the default: it's the obvious answer to the chicken-and-egg problem.

But look at which field actually causes the problem: `OrderLine.order`, the one that closes the cycle.
`Money` isn't part of any cycle. It's a leaf. Nothing inside a `Money` ever needs to refer back to it,
so there's no reason it couldn't be built the normal way: read the amount, read the currency, call the
constructor.

That's the whole idea of this post in one sentence: **only the edges that close a cycle need to be
mutable. Everything else can go through a constructor, and should.**

## Do it like records do

Records already show how deserialization should work. A record has a canonical constructor whose
parameters the compiler knows by name and order. Java serialization reads all component values first and
then calls that constructor once. If the compact constructor validates its arguments, the validation
runs on every deserialization. You simply can't materialize an invalid record from a byte stream.

Values in, object out, with the constructor as the only way in. TRIP now applies that model to every
class. To deserialize an object, it picks the first of these that applies:

1. A **canonical constructor**, if the class declares one. TRIP reads its arguments, calls it, and then
   reads any remaining components.
2. The **no-arg constructor** (it doesn't have to be public), followed by setting the remaining
   *non-final* fields through `VarHandle`s.
3. A registered **`TripInstanceCreator`** for classes that offer neither.

There's no step four. No `Unsafe`, no `ReflectionFactory`, and no object without a constructor call.

And if a class lands in step 2 with a final field still to fill? TRIP refuses, loudly, and tells you
how to fix it:

```
cannot deserialize final private final java.lang.String com.example.Money.currency (JEP-500):
provide a @CanonicalConstructor or @CanonicalBuilder, make the field transient, non-final or @TripIgnore,
use @TripInstanceCreatorService/@TripComponentProviderService, or enable the mutation via
-Dtentackle.trip.mutateFinalFields=true
```

TRIP does this no matter which `--illegal-final-field-mutation` mode the JVM runs in. Why wait for a
future JDK to say no when you can say it yourself today?

## Classes you own: a little annotation goes a long way

For a record, the compiler answers two questions that a serializer has to answer for every other class:

- **Which constructor?** A class can have several.
- **Which argument is which?** Parameter names aren't in the class file unless you compile with
  `-parameters`, and Tentackle doesn't want to force that flag on every application. Guessing by type
  doesn't work either: two `String` parameters look exactly alike.

Tentackle answers them with a small family of
[canonical construction annotations](https://tentackle.org/reflect/#canonical-construction) in
`org.tentackle.reflect`:

| Annotation | On | Meaning |
|---|---|---|
| [`@CanonicalConstructor`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/reflect/CanonicalConstructor.java) | constructor | Build the object with *this* one |
| [`@CanonicalName`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/reflect/CanonicalName.java) | parameter | Which component this parameter receives |
| [`@CanonicalField`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/reflect/CanonicalField.java) | field | This field is constructor argument number *n* |
| [`@CanonicalGetter`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/reflect/CanonicalGetter.java) | getter | This getter supplies constructor argument number *n* |
| [`@CanonicalBuilder`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/reflect/CanonicalBuilder.java) | type or constructor | Build the object through a builder |

`Money` only needs two of them:

```java
public final class Money {

  private final BigDecimal amount;
  private final String currency;

  @CanonicalConstructor
  public Money(@CanonicalName("amount") BigDecimal amount,
               @CanonicalName("currency") String currency) {
    this.amount = Objects.requireNonNull(amount);
    this.currency = Objects.requireNonNull(currency);
  }
}
```

On the receiving side, TRIP reads `amount` and `currency`, calls the constructor, and the null checks
run. Nobody writes a field by reflection. The constructor doesn't have to be public, by the way: a
protected one reserved for deserialization works just as well.

Two details turned out to matter more than expected in practice:

- **Superclass fields count.** Canonical names are matched against the fields of the class *and* its
  superclasses. A subclass constructor that passes its arguments to `super(...)` therefore takes care
  of its parent's final fields too.
- **A getter doesn't need a field behind it.** That's what makes `@CanonicalGetter` interesting.
  `java.util.Properties` keeps its defaults in a protected field inside `java.util`, which a framework
  can't reach because the JDK doesn't open its packages. Tentackle's `ChainedProperties` gets the
  defaults across anyway, by the front door:

```java
public class ChainedProperties extends Properties {

  @CanonicalConstructor
  public ChainedProperties(ChainedProperties defaults) {
    super(defaults);
  }

  public ChainedProperties() {
    super();
  }

  @CanonicalGetter(ordinal = 0)
  public ChainedProperties getDefaults() {
    return (ChainedProperties) defaults;
  }
}
```

TRIP sends the defaults first, creates the map through the constructor, then adds the entries. The
JDK's field is never touched.

For classes built through a builder, `@CanonicalBuilder` names the builder class instead, and Tentackle's
DTO generator produces that shape out of the box. Records, of course, need no annotation at all.

## Classes you don't own: a constructor from the outside

Annotations are great as long as you can edit the class. Real object graphs, however, are full of
classes you can't touch: the JDK's exceptions, JDBC driver exceptions, library types. You can't put
`@CanonicalConstructor` on `java.text.ParseException`, and its private fields are out of reach anyway.

Exceptions are a good test case, because they travel a lot. A server call fails, and the client wants
the real exception with its message, cause and stack trace, not a generic "something went wrong".

TRIP solves this with two small plug-in interfaces that work as a pair.

A [`TripComponentProvider`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/trip/TripComponentProvider.java)
decides **what** gets transported and **how it's read**. Tentackle registers one for `Throwable`, which
covers the whole hierarchy. It reads the message through `getMessage()`, the cause through `getCause()`
and the stack trace through `getStackTrace()`. For any other field declared in a `java.*` class, it
looks for a public getter of the same name. That's how `ParseException.errorOffset` travels as
`getErrorOffset()`. Public API only.

A [`TripInstanceCreator`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/trip/TripInstanceCreator.java)
decides **how the object is created**:

```java
public interface TripInstanceCreator<T> {

  T create(Class<T> clazz);

  default T create(Class<T> clazz, Map<TripComponent<T, ?>, Object> values) {
    throw new TripInstantiationException(clazz, "constructor creation from deserialized values is not supported");
  }

  default boolean isCreationFromValuesSupported(Class<T> clazz) {
    return false;
  }
}
```

If `isCreationFromValuesSupported` returns `true`, TRIP reads all values first and hands them over. The
creator then picks a public constructor and calls it. For `ParseException`, the whole thing is this:

```java
@TripInstanceCreatorService(ParseException.class)
public class ParseExceptionInstanceCreator<T extends ParseException>
       extends AbstractThrowableInstanceCreator<T> {

  @Override
  protected T create(Class<T> clazz, Values values) throws ReflectiveOperationException {
    return newInstance(clazz, new Class<?>[] { String.class, int.class },
                       values.get("message"), values.getInt("errorOffset", 0));
  }
}
```

The base class does the parts every throwable shares: it sets the cause if the constructor didn't and
restores the stack trace. And because the creator is generic in `T extends ParseException`, it covers
subclasses too, looking up the `(String, int)` constructor of the actual class.

So the provider reads values through public getters, and the creator turns them back into an object
through a public constructor. Put together, **they are a canonical constructor added from the
outside**. No `--add-opens`, no private field access, and no final-field mutation.

What if no suitable constructor exists? Then the client gets a `TripInstantiationException` carrying
the original class name, message, cause and stack trace. One exotic exception deep in a cause chain
can't break the whole response anymore.

Tentackle ships creators like this for the `FileSystemException` family, `DateTimeParseException`,
`ParseException`, `URISyntaxException`, `PatternSyntaxException`, `InvalidPathException`,
`MissingResourceException`, `InvocationTargetException`, and `SQLException` with all its subclasses.
All of them are registered by annotation and collected at build time, so nothing scans the class path at
runtime.

## The price of final: cycles

So far, so good. Now back to the chicken and the egg. A constructor can only run once its arguments are
known, so until then the object doesn't exist and nothing can refer to it. We call this *delayed
construction*. How much of an object's content may point back to it depends on how it's built:

| Built through | Object exists | May point back to it |
|---|---|---|
| no-arg constructor | before any component is read | every component |
| `@CanonicalConstructor` | after the constructor arguments are read | every non-constructor component |
| `@CanonicalBuilder` or record | after *all* components are read | nothing |

This isn't a TRIP quirk, it's simple logic: **you can't pass an object to its own constructor.** Every
serializer that respects a record's canonical constructor hits the same wall as soon as a record is part
of a cycle through its own components.

In practice it rarely hurts, because two different kinds of classes are involved:

- **Values** like `Money`, validation results and exceptions are immutable, sit at the leaves of the
  graph, and want their invariants enforced. They get a canonical constructor or become records.
- **Entities** like `Order` and `OrderLine` have identity, change over time, and form the cycles. They
  keep their no-arg constructor, and the reference that closes the cycle stays non-final. It's mutable
  in the domain anyway: a line can move to a different order.

So `final` and cycles only conflict at the edges where a cycle closes, and in a sensible domain model
those edges were never final to begin with.

## A blueprint for your framework

Here's the migration path that worked for us, generalised for any serializer or framework that builds
objects:

1. **Make it fail first.** Run your tests with `--illegal-final-field-mutation=deny`, or refuse final
   writes in your own code as TRIP does. Every failure names a field, so the to-do list writes itself.
2. **Give values a constructor path.** Support records, and offer a way to mark a canonical constructor
   and map its parameters to components. Remember the superclass fields.
3. **Don't transport derived state.** A cached hash code or a lazily computed view becomes `transient`
   and is recomputed on the other side.
4. **Open a door for classes you don't own.** Let users plug in "how to read it" and "how to build it"
   separately, using public API only.
5. **Drop `final` only on cycle edges.** If an object really is referenced by its own components, it
   needs a no-arg constructor and a non-final back-reference. That's the one legitimate exception.
6. **Offer an escape hatch, with an expiry date.** TRIP has `-Dtentackle.trip.mutateFinalFields=true`
   (together with `--enable-final-field-mutation=org.tentackle.core`) for applications that need time to
   migrate. Once the JDK switches to *deny*, the main value of that flag will be the list of warnings it
   produced.

What did it take in Tentackle itself? Less than you'd think. The copy-on-write collections, the
validation results and a few exceptions got canonical constructors. A cached hash code became
`transient`. Four classes lost a `final` modifier where they genuinely needed to be filled in after
construction. The JDK exceptions got a component provider and a handful of creators. The domain
entities needed no changes at all: they are mutable by design, and they're exactly the objects that form
the cycles.

The fact that all of this fitted into existing extension points was no accident. Every decision TRIP
makes about a type can be replaced, from a single annotation up to a custom type builder. When the
platform changes the rules, the fix becomes "add a few implementations" rather than "redesign the
serializer". That's probably the most reusable lesson of the whole exercise.

## Finally final

JEP 500 is often described as a restriction. For serialization, it's more of a correction. "Allocate an
empty object and fill in the fields" was always a shortcut. It undermined `final`, skipped every
invariant, and made deserialization one of Java's favourite attack surfaces.

Moving object graphs, cycles included, doesn't need that shortcut. It needs a reference table for
identity and cycles, a constructor path for every class that wants its fields final, a way to describe
that path for classes you can't change, and a design where all of this can be added without touching the
serializer's core.

Records showed us the way. We just followed them a little further. And now `final` is final, finally.

## Further reading

- [JEP 500: Prepare to Make Final Mean Final](https://openjdk.org/jeps/500)
- [TRIP documentation](https://tentackle.org/trip/), in particular the section on
  [final fields and JEP 500](https://tentackle.org/trip/#final-fields-jep-500)
- [Canonical construction and delayed construction](https://tentackle.org/reflect/#canonical-construction)
  in the Tentackle docs
- [TRIP's source code](https://github.com/krake-com/tentackle/tree/develop/tentackle-core/src/main/java/org/tentackle/trip)
  on GitHub, including
  [`FieldTripComponent`](https://github.com/krake-com/tentackle/blob/develop/tentackle-core/src/main/java/org/tentackle/trip/impl/FieldTripComponent.java),
  where final fields are refused
- [Tentackle on GitHub](https://github.com/krake-com/tentackle) and the
  [project website](https://tentackle.org/)
