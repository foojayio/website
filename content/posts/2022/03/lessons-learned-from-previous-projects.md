---
title: "Lessons learned from previous projects | Foojay Today"
slug: "lessons-learned-from-previous-projects"
date: "2022-03-14T13:02:01+00:00"
lastmod: "2022-03-14T13:02:03+00:00"
description: "This article discusses our shifting thought process on the notion that our practices, which were once considered good practices, can fade over time."
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2022/03/gratisography_large.jpeg"
categories:
  - "Opinion"
tags:
related_posts:
  - "10-basic-questions-about-pdf-files-for-java-developers"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-functional-programming-techniques-in-java-a-primer"
enlighterjs: true
frozen: false
---

An exciting part of software development is what was unanimously considered good practice at one point in time can be more ambiguous years later. Or even plain wrong. However, you generally need to do it multiple times over time to realize it. Here are my top learnings from my experience in Java projects.

Packaging by layers {#h2-0-packaging-by-layers}
-----------------------------------------------

When I started my developer career in Java, every project organized their classes by layers - controllers, services and s (repositories). A typical project's structure would look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ch.frankel
  ├─ controller
  │  ├─ FirstController
  │  └─ SecondController
  ├─ service
  │  ├─ FirstService
  │  └─ SecondService
  └─ dao
     ├─ FirstDao
     └─ SecondDao</pre>

This approach has two main disadvantages:

* From a visibility point-of-view, to use classes outside their package, you need to mark them as `public`. `FirstController` uses `FirstService`, hence the latter must be `public`. Because of this, any other class can use it, whereas I want it to be used only for "First"-related classes.
* If you want to split the application, you'll first need to analyze the dependencies to understand the coupling between packages.

To fix these issues, I found that packaging by feature is a much more natural fit:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ch.frankel
  ├─ first
  │  ├─ FirstController
  │  ├─ FirstService
  │  └─ FirstDao
  └─  second
     ├─ SecondController
     ├─ SecondService
     └─ SecondDao</pre>

This way, the controller is `public` and represents the entry point in the feature. Services and DAOs are an "implementation detail": they have the `package` visibility and can only be accessed from inside their package.

As an added benefit, if you need to split your code, you only need to do it by package.

Blindly obey quality tools {#h2-1-blindly-obey-quality-tools}
-------------------------------------------------------------

I found myself using a quality tool named Hammurapi a long time ago. For the record, it still has an [online presence](http://www.hammurapi.biz/hammurapi-biz/ef/xmenu/hammurapi-group/products/hammurapi/index.html), even if it feels like it hasn't been updated in ages. Anyway, when I ran the engine on my codebase, the most reported violation was the lack of JavaDocs on public methods. Given that all getters and setters were public, I got many of them.

It was easy to automate adding JavaDocs via a program:

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 Get the &lt;code&gt;foo&lt;/code&gt;.

 @return Current value of &lt;code&gt;foo&lt;/code&gt;
*/
public Foo getFoo() {
  return foo;
}

/**
 Set the &lt;code&gt;foo&lt;/code&gt;.

 @param foo New value of &lt;code&gt;foo&lt;/code&gt;
*/
public void setFoo(Foo foo) {
    this.foo = foo;
}</pre>

It satisfied the side of me that loves green checks. However, there was no added value.

In fact, most quality tools have a pretty low return over investment. It's not because you used tabs instead of spaces that your project's quality decreases drastically. Code quality is hard to define, complicated to measure, and doing so in an automated way even more so.

While I'm not saying to avoid quality tools, be careful with [metrics](https://blog.frankel.ch/metrics) they give you. Engineers and managers love metrics, but it can lead your team/organization to places you don't want to go, even with the best intentions.

Setters {#h2-2-setters}
-----------------------

After creating a class, Java developers always generate accessors for it, *i.e.*, getters, and setters.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class Money {

    private final Currency currency;
    private BigDecimal amount;

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return balance;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

public class Account {

    private Money balance;

    public Currency getBalance() {
        return balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}</pre>

It's like a Pavlovian reflex. Worse, it's part of the [JavaBean](https://stackoverflow.com/questions/3295496/what-is-a-javabean-exactly#answer-3295517) conventions, so that a lot of tools rely on them: frameworks, serialization libraries, *e.g.* Jackson, mapping tools, *e.g.* MapStruct, etc.

Hence, if you rely on any of those tools, you have no choice. If you don't, then you should probably think about whether you want to go this way or not.

Here's an alternative (and simplified) design to the above class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Account {

    // Field and getter
    // NO SETTER!

    public BigDecimal creditFrom(Account account, Money amount) {
        // Check that currencies are compatible
        // Do the credit
    }

    public BigDecimal debitFrom(Account account, Money amount) {
        // Check that currencies are compatible
        // Do the debit
    }
}</pre>

Note that getter alternatives make for a more complex design without many added benefits. I'm willing to keep them if they don't expose private data - either immutable objects or copies.

Abstractions everywhere {#h2-3-abstractions-everywhere}
-------------------------------------------------------

One of the first lessons I was taught in enterprise was that "good" developers always design their implementation around the following three components:

![](/images/posts/2022/03/lessons-learned-from-previous-projects/LOen3i8m34Ltdy8NQ0K3AoeB4iTm4oCYujIodG69mzEA3h2z_zwVFCYYImL_bgPa99YruF5qWDA2xKw9yReiGBE7KGfCbTpniiyjVSrADz4Ai0CENOVt-U_vmpXs7TUyQNYKpCYYyK9XcTaS9cwvI_q0.png)

The problem is that `FooImpl` is the only `Foo` implementation, and it becomes apparent when you need to name the classes. The most common scheme is to prefix the abstract class with `Abstract` and suffix the concrete one with `Impl`. Another way to spot the issue is where to implement the method: between the abstract class and the concrete one, there's no easy way to decide the best place.

Abstractions do lower coupling. However, coupling in applications has much less impact than in libraries, if at all.

Data Transfer Objects {#h2-4-data-transfer-objects}
---------------------------------------------------

I've used for a very long time. One of my [earliest blog posts](https://blog.frankel.ch/automated-beans-conversion/) is actually about DTOs, bean mapping, and the [Dozer](https://github.com/DozerMapper/dozer) library to automate the mapping process. I even remember that a fellow architect advised me to design a dedicated class for each layer:

* Entities for the DAO layer
* Service objects for the layer of the same name
* View objects for the controller layer

Moreover, since s are not supposed to leak outside the database, we had a dedicated identifier column to pass around.

Did I hear you say over-engineering? Well, you might not be completely wrong.

It got me thinking about DTOs. They probably are a good idea if your view is *very* different from the underlying table(s). However, it was not the case in most, if not all, of the applications I worked on. They perfectly mimicked the database structure.

In that case, I'll probably favour one of the techniques listed in this [previous post](https://blog.frankel.ch/alternatives-dto/).

Conclusion {#h2-5-conclusion}
-----------------------------

In this post, I've described five techniques I'd probably not use anymore, or at least be very careful on the context I apply them to.

The more years you have behind you, the more mistakes you'll probably have made. The idea is to build upon your experience to avoid repeating the same mistakes. As the Latin would say, *errare humanum est, sed perseverare diabolicum*.

* [Quality Tools: humble servants or tyrants?](https://blog.frankel.ch/quality-tools-humble-servants-or-tyrans/)
* [Encapsulation: I don't think it means what you think it means](https://blog.frankel.ch/encapsulation-dont-think-means-think-means/)
* [Are you guilty of overengineering?](https://blog.frankel.ch/are-you-guilty-of-overengineering/)
* [Alternatives to DTOs](blog.frankel.ch/alternatives-dto/)

*Originally published at [A Java Geek](https://blog.frankel.ch/lessons-learned-previous-projects/) on March 13^th^, 2022*

*[DAO]: Data Access Object
*[ORM]: Object-Relational Mapping
*[PK]: Primarey Key
*[DTO]: Data Transfer Object
