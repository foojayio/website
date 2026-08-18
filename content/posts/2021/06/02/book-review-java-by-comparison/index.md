---
title: "Announcing New Book Review: \"Java by Comparison\""
date: "2021-06-02T08:13:15+00:00"
lastmod: "2021-09-04T07:46:46+00:00"
description: "“Java by Comparison”, Simon Harrer, Jörg Lenhard, and Linus Dietz, promises the reader to become a “Java Craftsman” by studying 70 examples."
authors:
  - "cay-horstmann"
image: "javabycomparison.png"
categories:
  - "Book Review"
  - "Books"
related_posts:
  - "book-review-help-your-boss-help-you"
  - "book-review-seriously-good-software"
  - "openjdk-vs-openjfx-release-cycles"
frozen: false
---

{{< img src="book-java-by-comparison-423x510.png" class="size-medium" width="423" height="510" >}}

The book ["Java by Comparison" by Simon Harrer, Jörg Lenhard, and Linus Dietz](https://pragprog.com/titles/javacomp/java-by-comparison/), promises the reader to become a "Java Craftsman" through the study of 70 examples. The book is published by The Pragmatic Bookshelf.

Each "example" is structured as a before-and-after comparison. It's best to illustrate that with, well, an example:

### **Return Boolean Expressions Directly**

**Before:**

```java
boolean isValid() {
   if (missions < 0 || name == null || name.trim().isEmpty()) {
      return false;
   } else {
      return true;
   }
}
```

**After:**

```java
boolean isValid() {
   return missions >= 0 && name != null && !name.trim().isEmpty();
}
```

If you, dear reader, are wondering why and how to transform the former into the latter, then this book is for you. The authors imagine their audience as someone who knows the basics of the Java language, perhaps from a university curriculum, and who can pass [Fizz Buzz](https://blog.codinghorror.com/why-cant-programmers-program/).

Or you may have people on your team who should be doing this kind of transformation but aren't. Then give them a copy.

As an aside, I had the nagging question why the authors use `name.trim().isEmpty()` and not `name.isBlank()`. Then I realized that the book was written before Java 11. There is a section **Favor Java API over DIY** that exhorts readers to be on top of the ever-evolving Java API.

Here is another nice piece of advice, **Favor format over Concatenation**, which shows how to rewrite:

```java
String entry = author.toUpperCase() + ": [" + formattedMonth + "-" +
   today.getDayOfMonth() + "-" + today.getYear() + "](Day " +
   (ChronoUnit.DAYS.between(start, today) + 1) + ")> " +
   message + System.lineSeparator();
```

...using `String.format`, or, as of Java 15, the `formatted` method of the `String` class.

I liked the fact that each of the 70 "examples" was short and easily digestible in one sitting. The items are also carefully cross-linked.

The items are grouped into nine chapters:

1. Simple clean-up
2. Code style
3. Comments
4. Naming
5. Exceptions
6. Unit testing
7. Class design
8. Streams
9. Tools

My favorite was the "unit testing" chapter. I hadn't seen this advice in a single place before.

One problem that every book author faces is how to pick code examples. If an example is too long, readers can easily get overwhelmed. If the example is too simplistic, it can be hard for readers to match it up with realistic situations.

I generally try to pick examples that revolve around tasks that most programmers have seen before, such as processing of invoices or web pages. But that can be difficult when the intended audience hasn't done much programming yet. The authors of this book chose an "astronaut" theme for the sample code so that readers have some intuition. That is better than animals that bark and quack, but it's not perfect. Consider this example:

**Before:**

```java
class Hull {
   int holes;
}
class HullRepairUnit {
   void repairHole(Hull hull) {
      if (isIntact(hull)) {
         return;
      }
   hull.holes--;
}

   boolean isIntact(Hull hull) {
      return hull.holes == 0;
   }
}
```

When I first saw this code snippet, it didn't remind me of anything I had seen before. In my experience, students don't commonly write code that puts the data in one class and the methods into another. Maybe some enterprise pattern???

The suggested refactoring is:

**After:**

```java
class Hull {
   int holes;
   void repairHole() {
      if (isIntact()) {
         return;
      }
      holes--;
   }
   boolean isIntact() {
       return holes == 0;
   }
}
```

Sure, that's better. The authors must have seen this in the wild, since they write with passion to **Combine State and Behavior** .

The book has all the right guidance about `Optional`: Favor `Optional` over `null`. Avoid `Optional` fields. Use `filter` and `map`/`flatMap` with `Optional`. But I was baffled by the title of the last item. It's called **Use Optionals as Streams**. Of length 0 or 1, that is. I am not sure that title works well for beginners. Optionals aren't streams. There are only three common methods. I'd just name the methods, as in fact I just did.

Finally, is it really necessary to **Avoid Single-Letter Names** at all cost? What about loop indexes? Type parameters?

Those are just quibbles of the kind that every book review must have. I thought the book was fun and easy to read. Many intermediate level developers would benefit from it.

I learned something new myself. In the item **Document Using Examples**, the authors point out that one can embed comments inside regular expressions. It looks even better with text blocks:

```java
var regex = """
(?x:(([1-9]|1[0-2]) #hours
:([0-5][0-9])) #minutes
[ap]m)""";
```

It had never occurred to me to do that, but it seems like a good idea. And of course, as the authors point out, with a complex regular expressions, a few examples do wonders: `11:59am`, `1:05pm`. Here is one more thing I like. It's a book! Carefully edited, cross-referenced, reviewed, and typeset. Not a bunch of blog articles.

In conclusion, I can warmly recommend this book to Java programmers who have learned to code and strive to code well. Don't take every item as gospel, but think about each of them. And, as I said, if you have people on your team who would benefit from this kind of advice, get them a copy. You'll be glad you did.
