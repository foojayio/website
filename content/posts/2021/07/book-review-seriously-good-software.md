---
title: "New Book Review: Seriously Good Software"
slug: "book-review-seriously-good-software"
date: "2021-07-22T07:17:33+00:00"
lastmod: "2021-09-04T07:45:39+00:00"
description: "This is another book that I can warmly recommend to Java programmers who have learned to code and strive to code well!"
authors:
  - "cay-horstmann"
image: "https://horstmann.com/unblog/2021-07-05/Faella-SGS-HI.jpeg"
categories:
  - "Book Review"
  - "Books"
tags:
related_posts:
frozen: false
---

![Cover](https://horstmann.com/unblog/2021-07-05/Faella-SGS-HI.jpeg)

I recently [reviewed the book "Java by Comparison"](//foojay.io/today/book-review-java-by-comparison/') by Simon Harrer, Jörg Lenhard, and Linus Dietz. That book is intended for programmers who know the basics of the Java language, perhaps from a university curriculum. The book teaches programmers to improve their Java language and library usage through seventy "before and after" examples, covering topics such as coding style, naming, exceptions, streams, and class design.

For contrast, I'll now review Marco Faella's book "[Seriously Good Software](https://www.manning.com/books/seriously-good-software)" that also teaches intermediate Java programmers to write better software, using an entirely different approach. (Disclaimer: Marco talked me into writing the preface for his book.)

In **chapter 1**, the book starts out with a review of "software qualities"---correctness, robustness, efficiency, readability, reusability, maintanability, and so on. Then a running example is presented that will be used to exemplify these various qualities in subsequent chapters. The example is deceptively simple. A set of water vessels is given, with some vessels connected to each other by pipes. There are two fundamental operations:

* Add water to a vessel
* Connect two vessels with a pipe

Just one twist---the water level is equalized among all vessels that are connected by pipes.

I am not normally a fan of a running example in a technical book. The resulting programs start out simple but tend to get ever more baroque in later chapters, as all features of a particular technology are brought to bear. It can be difficult to jump around, browsing for the topics of interest. Instead, one must read the book linearly, chapter by chapter, whether or not one is interested in all of them.

But the water vessel example is an inspired choice. Marco manages to nudge it into an amazing number of different directions, always starting from a simple baseline.

**Chapter 2** develops a straightforward reference implementation. Each vessel stores its water level and the set of connected vessels. There is a review of Java collections, UML, memory diagrams, and big-Oh complexity that would be helpful for a student who had two or three semesters of computer science but, as is so often the case, was rushed through the finer points.

**Chapter 3** analyzes the big-Oh complexity of several implementations, finally arriving at the classic union-find data structure (i.e. the one often used in Kruskal's minimum spanning tree algorithm). This chapter requires some knowledge about data structure implementations, in particular linked lists and trees.

In **chapter 4**, memory is at a premium. Along the way, readers learn about heap implementation, memory hierarchy, locality of reference, and floating-point representation. I really liked how these topics, which students typically see in hardware and systems courses, are integrated into the discussion.

In **chapters 5 through 7**, the focus shifts to software engineering. Marco shows how to make a design robust with contracts (preconditions, postconditions, invariants) and assertions. This is followed by an overview of testing and JUnit, and a discussion of coding and Javadoc style. Of course, all the knowledge is applied to the water vessel example. These chapters are easy to read and do not require knowledge of algorithms and data structures.

**Chapter 8** turns to concurrency. What does it take to make the connected vessels threadsafe? You can use a lock for the entire data structure. But what if you want to allow concurrent modification of unrelated vessel sets? That's much harder. When connecting two such sets, each of them needs to be protected with a lock. And to reliably grab both locks, you need a global lock. It is interesting if you are into low-level concurrent programming. But is it a good teaching example for junior programmers?

I am not convinced. By rushing headlong into locks, there is the danger of setting the signal that locks should be the first tool to try when faced with concurrent computations. That tendency is already prevalent in Java where most books and courses dwell excessively on implicit locks and conditions, because they are a part of the language.

I would have organized the concurrency chapter differently and first presented concurrency strategies that require no explicit synchronization: immutable data structures, decomposing tasks and merging partial results, built-in conccurrent collections. Those serve the situations that most Java programmers will encounter.

Admittedly, they don\\'t help for building a threadsafe union-find data structure. But if I had to teach locking, I would start out with `java.util.ReentrantLock`. I have seen too many students ascribe magical properties to intrinsic locks.

Every reviewer has to kvetch about something, and this is it for me.

I am back on board with **Chapter 9** , where the "corresponding vessels" data structure is generalized. The reader learns about Java generics, functional interfaces, and the collectors in `java.util.stream`.

**Appendix A** has a "code golf" solution of the water vessel problem, using as few characters as possible. Of course that has no practical utility, but it's just good clean fun.

**Appendix B** has the "ultimate" solution that strikes a balance among performance and readability.

Each chapter has a nice set of "pop quiz" questions and end-of-chapter exercises. Solutions are provided. Most chapters end with a section entitled "And now for something completely different", which applies the lessons of the chapter to a different scenario.

In my review of "Java by Comparison", I wrote: "Here is one more thing I like. It's a book! Carefully edited, cross-referenced, reviewed, and typeset." I feel the same way about Marco's book. These days, there are so many carelessly formatted blog articles, milking a single attention-grabbing idea for what it's worth. It is a real pleasure to read a book that presents a coherent vision of a complex set of ideas, and to view a presentation that required thought and care.

I liked the annotated figures, rendered in a minimalistic style that reminded me of [Edward Tufte](//www.edwardtufte.com/tufte'). (OK, Tufte might have removed the borders around the nodes.)

![Image Sample](https://horstmann.com/unblog/2021-07-05/faella-image-sample.png)

And **I loved the code annotations**. Marco puts the emphasis where it belongs: on the annotations.

To see why that is important, think about the code presentation that you typically get: code with syntax highlighting (keywords in one color, variables in another, literals in a third color). That makes sense in an IDE, but why is it a good idea when writing about code? How would you like it if all verbs of this article were in one color and all the nouns in another, and the adjectives in a third?

And worst of all, `why are comments in monospace`? Comments are important. They should be easy to read. `Monospace is not easy to read. You would hate it if this article was entirely written in monospace`. Monospace is good for consistent vertical alignment, which is why we use it for coding. And to call out code within text (for example, when talking about a `ConcurrentHashMap` or calling the `System.out.println` method).

Why do blogs do what they do? Because it's easy, not because it's good. You can't add nicely formatted annotations in Markdown, so most blogging platforms just slap on a syntax highlighter for a gee-whiz effect.

That's not what Marco does. Look at this sample.

![Code Sample](https://horstmann.com/unblog/2021-07-05/faella-code-sample.png)

Note how the annotations are *not in monospace*. They use a font that is easy to read (except they revert to monospace for actual code, as they should). Just like you write about code in the main text.

**In conclusion, this is another book that I can warmly recommend to Java programmers who have learned to code and strive to code well. I think it is a particularly rewarding read for computer science students who had several semesters of disjointed knowledge of programming, algorithms, computing systems, and software engineering. Marco has a knack of pulling in facts from all those courses and make them relevant in a fresh context.**
