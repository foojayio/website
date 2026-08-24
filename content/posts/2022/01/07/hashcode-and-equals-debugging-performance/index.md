---
title: "Hashcode and Equals: Debugging and Performance"
date: "2022-01-07T13:31:35+00:00"
lastmod: "2022-01-07T13:31:36+00:00"
description: "Standard Java methods hashcode & equals are crucial to performance but this is very hard to detect as they're often too small for profilers."
canonical: "https://talktotheduck.dev/hashcode-and-equals-debugging-performance"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Time-Travel-Debuggers-01.jpg"
categories:
  - "Performance"
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "book-review-why-programs-fail"
  - "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
frozen: false
---

A few weeks ago I ran into [this story on reddit](https://www.reddit.com/r/java/comments/qi8yu8/hint_to_myself_and_other_poor_souls_dont_use/) that discusses the problem with using the URL class as a key in a Map. This boils down to a remarkably slow implementation of the hashcode() method in java.net.URL which makes this class unusable in such situations.

Unfortunately, this is a part of the Java API specification and is no longer fixable without breaking backwards compatibility.

What we can do is understand the problem with equals and hashcode. How can we avoid such problems in the future?

## What's the problem with URLs Hashcode and Equals?

To understand this, let's ‌look at the JavaDoc of hashcode and equals:
> Compares this URL for equality with another object. If the given object is not a URL then this method immediately returns false. Two URL objects are equal if they have the same protocol, **reference equivalent hosts**, have the same port number on the host, and the same file and fragment of the file. **Two hosts are considered equivalent if both host names can be resolved into the same IP addresses;** else if either host name can't be resolved, the host names must be equal without regard to case; or both host names equal to null. Since hosts comparison requires name resolution, this operation is a blocking operation.

Since hosts comparison requires name resolution, this operation is a blocking operation."

This might be unclear. Let's clarify it with a simple block of code:

```java
System.out.println(new URL("http://localhost/").equals(new URL("http://127.0.0.1/")));
System.out.println(new URL("http://localhost/").hashCode() == new URL("http://127.0.0.1/").hashCode());
```

Will print out:

```java
true
true
```

This might be pretty simple with localhost, but if we compare domains and the Strings aren't identical (which they often aren't) we need to do a DNS lookup. We need to do that just for a hashcode() call!

## A Quick Workaround

A quick workaround for this case is to avoid URL. Sun deeply embedded the class in the original JVM code, but we can use URI for most purposes.

E.g., if we change the hashcode and equals calls from above to use URI instead of URL we will get this result:

```java
System.out.println(new URI("http://localhost/").equals(new URI("http://127.0.0.1/")));
System.out.println(new URI("http://localhost/").hashCode() == new URI("http://127.0.0.1/").hashCode());
```

We will get false for both statements. While this might be problematic for some use cases, it's a vast difference in performance.

## A Bigger Pitfall

If all we ever used as a map key were Strings we'd be fine. This sort of bug can hit us in every place where we use these methods:

* Sets
* Maps
* Storage
* Business logic

But it gets deeper. When writing our own classes with our own hashcode and equals logic we can often fall prey to bad code. A small performance penalty on a hashcode method or an overly simplistic version can cause major performance penalties that would be very hard to track.

E.g. A stream operation that takes longer because the hashcode method is slow or incorrect can represent a long-term problem.

### The Best Hashcode Implementation

To understand the best hashcode and equals method, we first need to understand some mediocre code. Now I won't show horrible or old code. This is good code, but it isn't the best:

```java
public int hashCode() {
    return Objects.hash(id, core, setting, values, sets);
}
```

This code might seem OK at first, but is it? Here's the ideal code:

```java
public int hashCode() {
    return id;
}
```

This is fast, 100% unique and correct. There's literally no reason to do anything beyond that. There's one exception of an id which is an object. In that case we might want to do Objects.hashCode(id) instead which will also work for null, etc.

### Hashcode isn't Equals

Well, obviously… This is one of the most important things you need to keep in mind when writing a hashcode implementation. This method must perform fast and must be consistent with equals for the false case. It will not be correct for the case of true.

To clarify, hashcode must always obey this law:

```java
assert(obj1.hashCode() != obj2.hashCode() && !obj1.equals(obj2));
```

That means if hashcode results are different, the objects must be different and must return false from equals. But the inverse isn't the case:

```java
if(obj1.hashCode() == obj2.hashCode()) {
    if(obj1.equals(obj2)) {
       // this can be false...
    }
}
```

The value here is in performance. A hashcode method should perform much faster than equals. It should let us skip the potentially expensive equals calculation and index elements quickly.

### Special Case with JPA

JPA developers often just use a hardcoded value for hashcode or use the Class object to generate the hashCode(). This seems weird until you think about this.

If you let the database generate the ID for you, you would save an object and it would no longer be equal to the source object… One solution is to use the `@NaturalId` annotation and data types. But that would require changing the data model. Unfortunately, there's no decent workaround for the entity classes.

In fact, I would theorize that a lot of the problems JPA developers experienced with Lombok are because it generates hashcode and equals methods for you. Those could be problematic.

## Is this a Blog about Debugging?

Sorry about that long setup, but yes it damn well is. So I needed all of that preface to talk about this in a more generic sense of debugging. Notice that this is true for other languages that use similar paradigms for common interfaces.

This blog started with a performance issue and I would like to discuss that aspect in the lens of debugging. In many profilers, the overhead of a hashcode method would be almost unnoticeable. But because it's invoked so often and has wide-reaching implications, it's possible you ultimately feel the repercussions and cast the blame elsewhere.

The knee jerk reaction would be to implement a "dummy" hashcode method and see the resulting performance difference. Just return a hard coded number instead of a valid number.

This is valuable for some cases and might even solve problems like the one mentioned at the top where the hashcode method performs badly. However, it won't help with maps. If the hashcode would return identical values, using it as a key in a map would effectively disable all the performance benefits that hashcode can offer.

How do we know if a hashcode method is good?

Well… We can use the debugger to figure it out. Just inspect your map and look at the distribution of the objects between the various buckets to get a sense of the real world value of the hashcode method.

If you have code verification process on commit I would strongly recommend defining a rule on the complexity level of a hashcode method. This should be set very low to prevent slow code from seeping in.

But the problem is nesting. E.g. think about code like the one we discussed before:

```java
public int hashCode() {
    return Objects.hash(id, core, setting, values, sets);
}
```

It's short and simple. Yet, performance of this code can be all over the place. The method would invoke the hashcode method of all internal objects. These methods can be far worse in terms of performance. We need to be vigilant about this. Even for JDK classes such as URL which, as we discussed earlier, is problematic.

## TL;DR

We often auto-generate hashcode and equals methods. The IDEs are normally pretty good at that; they offer us an option to pick the fields we wish to compare. Unfortunately, they then apply both sets of fields to hashcode and equals.

Sometimes, this doesn't matter. Often we don't "see" the places where it does matter since the methods are too small to make a dent in the profiler. But they have wide-ranging implications we should optimize for.

Debugging lets us inspect the map and look at bucket distribution so we can get a sense of how well our hashcode method is performing and whether we should tune it to get more consistent results from maps and similar APIs.
