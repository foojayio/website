---
title: "The Reason Java is Still Popular"
date: "2022-10-10T16:11:32+00:00"
lastmod: "2022-10-10T16:16:04+00:00"
description: "Java has maintained its popularity for such a long period of time: its conservative, slow, and steady approach is key to its success"
canonical: "https://debugagent.com/the-reason-java-is-still-popular"
authors:
  - "shai-almog"
image: "DALL-E-2022-09-20-19.49.55-steaming-coffee-cups-as-far-as-the-eye-can-see-epic-photo.jpg"
categories:
  - "Java Core"
  - "Opinion"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "great-time-at-javazone-2022"
  - "what-is-debugging-in-140-seconds"
frozen: false
---

A great time to post this, right after the release of Java 19, yes, another "my language is better" post. No, I didn't want to write it. But sometimes people's [bad projection](https://medium.com/codex/i-finally-gave-up-on-java-5187947bef1b) gets the better of me.

In this case the post started as a comment and I ended up deciding turn it into a post. This was strengthened with [this post](https://news.ycombinator.com/item?id=32896228) which mostly complains about Quarkus (somewhat unfairly if I might add).

The first article is mostly nonsense and outdated clickbait. I'll sum it up to you in the following claims:

* Getters/Setters
* Missing operator overloading specifically for `[]` on collections and `+` on anything
* Checked exceptions
* Dependency management

The second article has complaints that are more related to Jakarta EE and general programmer aesthetics in the JVM.

Specifically, the usage of annotations for validations and similar claims.

This is a far better informed article but has a couple of flaws that I'll address near the end.

## Getters and Setters

Getters and setters aren't necessary in modern Java. We have records since Java 14 and Lombok is still good despite some claims to the contrary.

The only point where we would need getter/setters is in very specific settings (e.g. JPA) where again, Lombok solves the problem perfectly.

The article builds into a tirade on the lack of syntactic sugar features in Java. This is intentional.

You can look at Kotlin if you're interested in the latest syntax nuances. Java is about "slow and steady". That's a good thing and the main reason behind Javas longevity.

## Syntax Sugar and Operator Overloading

Modern Java includes patterns in switch, var, multiline strings and much more. Some upcoming features include string templates. String template support takes a while because Java wants to "do it right".

There's some support for that in the API level (and has been for a while). This isn't performant. The goal of string templates is to create a radical overridable syntax that [would allow](https://openjdk.org/jeps/8273943) stuff like:

```
ResultSet rs = DB."SELECT * FROM Person p WHERE p.last_name = \{name}";
```

**Update:** since the publication of this post developers mistakenly assumed the code above is an SQL injection vulnerability. It isn't. This looks like string replacement but it's code that uses that syntax to generate a parameterized SQL call.

Notice that `name` is a variable that the compiler will check and get from scope dynamically!

`DB` can be custom implemented by the developer and doesn't need to be "built in" so you can construct complex templates right in-place.

But let's talk about the Java we have today, not the one coming in 6 months. Using append hasn't been the recommendation for `String` for a decade or more. Use `+` which is the most performant and easier to read. About collections, the difference between `get()` and `[]` is literally four characters.

Those characters matter a lot. We can easily override this. We can also understand the semantic differences. Java arrays are VERY fast, for many cases native speed fast. Collections can't be as fast, the fact that we can see this here instantly is very valuable.

The fact that operators **can't** be overloaded is a HUGE benefit. If I see `a + b` I know that this is either a string or a number not some hidden method. That's one of the biggest strengths of Java and one of the reasons it's been popular for almost 30 years while other languages are left by the side.

Java's syntax is designed for reading at scale. When you have a project with 1M lines of code or even 100k, the problems shift. At this point discovering that programmer X in module Y overrode an operator incorrectly when you're debugging a problem is hard. The syntax should be simple at that point and any minor cost you saved initially will be paid with 10x interest. The definition of simple flips as code becomes more complex and ages.

Add to that the power of tooling to parse strict simple code at a huge scale and this becomes an even bigger benefit.

## Checked Exceptions

Checked exceptions are optional. But they're one of the BEST features in Java. So much code fails unexpectedly. When you're building stuff as a hobby it might be OK. When you want to build a professional application you need to handle every error. Checked exceptions help you avoid that nonsense.

People hate checked exceptions because of laziness. Java guards you against yourself.

There should be no case where I make a network connection, a DB connection, open a file, etc. and don't need to handle the potential error. I can punt it but then checked exceptions force me to keep punting it somewhere. It's an amazing feature.

## Dependencies

I have a lot of problems with Maven and Gradle. But when you compare it to pretty much any other dependency system with some millage on it they're doing great. They have problems but you can't compare them to something young like cargo that has almost no packages by comparison.

Maven central is **MASSIVE** with 27 terabytes of jars and 496 billion requests. It ticks perfectly and has almost no downtime.

Other tools like NPM demonstrate the strengths of maven perfectly. If dependencies in maven are a problem then NPM has 100x the problem and no supervision. As these things grow there are complexities. Especially with multiple versions of maven in the market.

However, one of the things maven and gradle have going for them is the tooling. In many cases the IDEs help resolve issues and find the fix right out of the box.

## Cultural Problems in Java

The second article is a more interesting one and to some degree I do agree. Java developers tend to make every problem into a more complicated problem.

In some cases this is necessary, Java is the 800 pound gorilla of programming platforms and its solutions are often over-engineered. This tends to be better than under powered, but it does carry a price.

## Why Annotation for Validation

The article did bring up one interesting example that seems like the "right thing" to the casual observer but is problematic.

```java
@NotNull @Email
String noReplyEmailAddress
```

The author claims that this is bad and one should implement custom typing e.g.:

```java
public record EmailAddress(String value) {
  public EmailAddress {
    // We could've used an Either data type, ofc;
    Objects.requireNonNull(value);
    // regexp could be better
    if (!value.matches("^[^@\\s]+@\\S+$")) 
      throw new IllegalArgumentException(
        String.format("'%s' is not a valid email address", value));
  }
}
```

This is entirely possible in Java as the code above is valid Java code. But it has several problems which is why we have bean validation.

* This can't be optimized - Bean validation can be moved up the validation chain by the framework. It can even be validated in client side code seamlessly since it's a declarative API. Here we need to actually execute the constructor to perform the validation.
* Declarative annotations can be moved down the chain to apply database constraints seamlessly, etc.
* We can apply multiple annotations at once
* The syntax is more terse

As a result, the annotations might feel weird and don't enforce typing. That's true. But they increase performance and power. There's a lot of thought and common sense behind their usage. I get the authors point, I'm not a big IoC fan either, but in this specific case he's incorrect.

## Finally

This article spent way too much time on the defensive. It's time to switch gears. Java has been around for nearly 30 years and is still mostly compatible to Java 1.0. That is fantastic and unrivaled!

One of the powers of its conservative approach is that it can do amazing "under the hood" optimizations without any of you noticing what went on. Java 9 completely [replaced the way strings were represented in memory](https://howtodoinjava.com/java9/compact-strings/) seamlessly, and cut RAM usage significantly. Similarly Loom will boost the throughput of Java synchronous applications. Valhalla will further improve collection performance and unify the Object/Primitive divide. Panama will finally rid us of JNI and make the process of integrating with native code so much more pleasant.

The nice thing is that the JVM is a big tent. If you aren't a fan of Java, Kotlin or Scala might suit your taste. The benefit of the JVM applies universally and most of the features I mentioned here will benefit our entire joint ecosystem.
