---
title: "A Minor But Useful Refactoring Technique That Would Reduce Your Code Footprint (Part 2)"
date: "2022-10-26T15:16:18+00:00"
lastmod: "2022-10-26T15:30:47+00:00"
description: "This is a continuing attempt to refactor from Java 11 to later versions of Java, and today's topic is about two methods of the Stream API."
authors:
  - "bazlur-rahman"
image: "michiel-leunens-0wIHsm2_1fc-unsplash-scaled.jpg"
categories:
  - "Java Core"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "comparison-fault-tolerance-libraries"
  - "offline-crypto-address-validation-in-java"
frozen: false
---

As I keep refactoring, this article will focus on a few more interesting ways to do it. These are pretty much minor yet effective and useful changes.

## Stream.noneMatch() and Stream.anyMatch()

In some situations, we need to find a single case among many. For example, we want to do a certain operation if we have a list of items and a certain item is in that list. Usually, we run a for loop and then use a loop inside the for loop to check the value against our case. Check out the following piece of code:

```java
private void addIfNotExist(final List<GrantedAuthority> reachableRoles,
    final GrantedAuthority authority) {
  for (final GrantedAuthority testAuthority : reachableRoles) {
    final String testKey = testAuthority.getAuthority();
    if ((testKey != null) && (testKey.equals(authority.getAuthority()))) {
      return;
    }
  }
  reachableRoles.add(authority);
}
```

The above code is easily understandable, which is great, but at the same time, pretty imperative. We are basically instructing our code on every line, what we want and how we want. In many cases, that's fine, but I'd argue that if we can get the same result with a declarative approach, the code will be easier to read and more maintainable. The idea is, let's not reinvent the wheel. When tools can do a job, we should take the opportunity. Less work is always better. The above code can be refactored as follows:

```java
private void addIfNotExistDeclarative(final List<GrantedAuthority> reachableRoles,
     final GrantedAuthority authority) {
   final var exist = reachableRoles.stream()
       .map(GrantedAuthority::getAuthority)
       .noneMatch(testKey -> (testKey != null) && (testKey.equals(authority.getAuthority())));

   if (exist) {
     reachableRoles.add(authority);
   }
 }
```

Regarding the number of lines, both approaches have the same number of lines, but I would argue that the second method is preferable because it talks more about its objective when read. When we read code, we mostly want to know the intent behind particular things, but how they were done isn't our main focus.

Let's see another example-

```java
public boolean isProbablyPlayFramework(String className){
    return className.startsWith("play.api")
        || className.startsWith("play.core")
        || className.startsWith("play.data")
        || className.startsWith("play.filters")
        || className.startsWith("play.libs")
        || className.startsWith("play.mvc")
        || className.startsWith("play.templates");
  }
```

The above method tests if a fully qualified class name is a part of the play framework or not. Simple, but again, it contains the imperative code. This code can be rewritten as follows -

```java
public boolean isProbablyPlayFramework(String className) {
  return Stream.of("play.api", "play.core", "play.data", "play.filters", "play.libs", "play.mvc",
          "play.templates", "play.utils")
      .anyMatch(className::startsWith);
}
```

This code certainly reduces the LoC. The other thing we could do is, instead of putting all the class names inside this method, we could extract them and put them in a constant.

This is all for today. In my next article, I'll talk about some small refactoring techniques.

Until then, happy coding.
