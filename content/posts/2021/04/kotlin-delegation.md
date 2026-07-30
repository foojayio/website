---
title: "Taking a Closer Look at Kotlin's Delegation"
slug: "kotlin-delegation"
date: "2021-04-30T13:53:00+00:00"
lastmod: "2021-11-08T08:37:51+00:00"
description: "Kotlin offers many cool features, most tend to cite null safety as their favorite. For me, it's function extensions, with delegation second."
canonical: "https://blog.frankel.ch/kotlin-delegation/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/04/startup-883575_1280.jpg"
categories:
  - "Kotlin"
  - "Research"
tags:
related_posts:
  - "examining-the-switch-statement-and-the-factory-pattern-in-three-jvm-languages"
  - "exposed-kotlin-orm-complete-guide"
  - "jc-ai-newsletter-15"
  - "jc-ai-newsletter-13"
enlighterjs: true
frozen: false
---

Kotlin offers many exciting features. In general, developers tend to cite null safety as their favorite. For me, it's function extensions. But delegation comes a close second.

The delegation pattern {#h2-0-the-delegation-pattern}
-----------------------------------------------------

The delegation pattern is described in the book:
> *Delegation* is a way to make composition as powerful for reuse as inheritance \[Lie86, JZ91\]. In delegation, *two* objects are involved in handling a request: a receiving object delegates operations to its *delegate* . This is analogous to subclasses deferring requests to parent classes. But with inheritance, an inherited operation can always refer to the receiving object through the `this` member variable in C++ and `self` in Smalltalk. To achieve the same effect with delegation, the receiver passes itself to the delegate to let the delegated operation refer to the receiver.

<img fetchpriority="high" decoding="async" class="aligncenter size-medium wp-image-43993" src="/images/posts/2021/04/kotlin-delegation/window-rectangle-700x117.png" alt="" width="700" height="117">

<br />

Delegation is critical when one chooses *composition* over *inheritance*.

![](/images/posts/2021/04/kotlin-delegation/composition-inheritance.png)

Manual and native delegation {#h2-1-manual-and-native-delegation}
-----------------------------------------------------------------

In Java, you need to code delegation manually. The example above translates into the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="java">interface A {
    void foo();
}

class B implements A {
    @Override
    public void foo() {
    }
}

class Composition implements A {
    private final B b;

    Composition(B b) {
        this.b = b;
    }

    @Override
    public void foo() {
        b.foo();
    }
}</pre>

Kotlin handles the delegation natively using the keyword `by`. You can write the same code in Kotlin like this:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">interface A {
    fun foo()
}

class B : A {
    override fun foo() {}
}

class Delegate(b: B) : A by b  // 1</pre>

1. With this, you can call `foo()` on any `Delegate` instance

As explained in the docs:
> The by-clause in the supertype list for `Delegate` indicates that `b` will be stored internally in objects of `Delegate`, and the compiler will generate all the methods of `B` that forward to `b`.
>
> -- [Delegation](https://kotlinlang.org/docs/reference/delegation.html)

Delegated properties {#h2-2-delegated-properties}
-------------------------------------------------

Kotlin also offers **delegated properties** , a property that delegates its getter (and its setter if a `var`) to "something else". A delegated property also uses the `by` keyword.

A couple of out-of-the-box delegates are available through the standard library.

* Non-null delegate: A non-`null` delegate behaves the same way as the `lazyinit` keyword: if one uses the variable before one has initialized it to a non `null` value, it will throw an `IllegalStateException`.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">var notNull: String by Delegates.notNull()</pre>

* Lazy delegate: A lazy delegate computes the value *on the first access* , stores it, and then returns the stored value. As its name implies, you use `lazy` when the value is expensive to compute and doesn't change after computation.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val lazy: String by lazy { "An expensive computation" }</pre>

* Observable: An observable delegate offers a hook when the value is accessed so you can execute code **afterward** .

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val observed = "Observed"
val observable: String by Delegates.observable(observed) {
    _, old, new -&gt; println("old: $old, new: $new")
}</pre>

* Vetoable: A vetoable delegate is the opposite of the observable. It offers a hook that executes **before** . If this hook returns `true`, the set of the value executes as expected; if it returns `false`, the set doesn't happen.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val vetoable: String by Delegates.vetoable(observed) {
    _, _, _ -&gt; Random.nextBoolean()
}</pre>

  Here, the set fails randomly 50% of the time. It's not helpful but fun to debug for your colleagues.

Your own delegated property {#h2-3-your-own-delegated-property}
---------------------------------------------------------------

If you want to create your own delegated property, it needs to point to a class that has:

1. An `operator fun getValue(thisRef: T, prop: KProperty): U` *operator* function for fields whose value is immutable
2. An extra `operator fun getValue(thisRef: T, prop: KProperty, value: U)` if it's mutable

* `T` is the class' type
* `U` the property's
* `thisRef` is the class instance
* `value` is the new value
* `prop` is the property itself

As an illustration, let's implement a distributed cache delegated property based on Hazelcast IMDG.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class HazelcastDelegate&lt;T&gt;(private val key: String) {

  private val map: IMap&lt;String, Any&gt; by lazy {                          // 1
    val config = Config().apply {
      instanceName = "Instance"
    }
    Hazelcast.getOrCreateHazelcastInstance(config).getMap("values")
  }

  operator fun getValue(thisRef: T, prop: KProperty&lt;*&gt;) = map[key]      // 2

  operator fun setValue(thisRef: T, prop: KProperty&lt;*&gt;, value: Any?) {
      map[key] = value                                                  // 3
  }
}</pre>

1. Create a reference to a Hazelcast `IMap`
2. Get the value from the `IMap`
3. Set the value in the `IMap`

Using the above delegate is straightforward:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class Foo {
  var cached: Any? by HazelcastDelegate&lt;Foo&gt;("cached")
}

fun main() {
  val foo = Foo()
  foo.cached = "New value"
  println(foo.cached)
}</pre>

Conclusion {#h2-4-conclusion}
-----------------------------

The delegate pattern is ubiquitous in the Object-Oriented Programming world. Some languages, such as Kotlin, provides a native implementation.

But delegation doesn't stop at the class level. Kotlin does provide delegation at the property level. It provides some out-of-the-box delegates, but you can easily create your own.

*Original published at [A Java Geek](https://blog.frankel.ch/kotlin-delegation/) on April 18^th^, 2021*

*[GoF]: Gang of Four
