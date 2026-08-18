---
title: "More readability & less complexity with Java's Pattern Matching."
slug: "increase-readability-and-reduce-complexity-with-javas-pattern-matching"
date: "2024-03-14T13:29:36+00:00"
lastmod: "2024-03-15T14:09:35+00:00"
description: "Increase readability, reduce cognitive complexity, and avoid bugs that are hard to spot with Java's Pattern Matching."
authors:
  - "jonathan-vila"
image: "instanceof.png"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "top-most-detected-issues-in-java-projects"
  - "builders-withers-and-records-javas-path-to-immutability"
  - "building-for-failure-best-practices-for-easy-production-debugging"
  - "make-the-life-of-your-developer-clients-easier-with-smart-builders"
enlighterjs: true
frozen: false
---

**Increase readability, reduce cognitive complexity, and avoid bugs that are hard to spot with Java's Pattern Matching.**

I bet you don't like writing ugly but necessary boilerplate code or reading it. But, sometimes we need to create logic that has to deal with an object of unknown type and follow different paths depending on the type. This code is prone to be too verbose, is complex to understand, and may involve some hidden errors hard to spot due to intermediate assignments.

In this article, I will show different ways of checking the type of an object and keeping the code easy to understand while also reducing the chances of introducing bugs hard to spot.

The usage of instanceOf
-----------------------

In Java, we've been using `instanceOf` conditional statements, type casting, and temporary assignments for that purpose.

```
public String processElement(Object element) {
  String result;

  if (element instanceOf String) { 
    String elementStr = (String) element;
    result = elementStr;
  } else if (element instanceOf Person) {
    Person elementPerson = (Person) element;
    result = elementPerson.getName();
  }

  return result + " value";
}
```


This involves a lot of boilerplate code which is not very readable. But even more important, it allows coding errors to remain hidden. In this structure, nothing is ensuring we are assigning a value to the intermediate variable `result` and that could mean having an empty value at the end.

But Java has included new features since version 14 that will help us to improve in this area. Let's discover them.

Pattern Matching
----------------

In Java 16 an improvement was added in order to reduce code repetition and boilerplate: [Pattern Matching](https://openjdk.org/projects/amber/design-notes/patterns/pattern-matching-for-java "Pattern Matching") for `instanceOf` cases. With this approach, the cast is included in the condition which is easier to read, reducing the boilerplate code.

```
public String processElement(Object element) {
  String result;

  if (element instanceOf String s) { 
   result = s;
  }

  if (element instanceOf Person elementPerson) {
    result = elementPerson.getName();
  }

  return result + " value";
}
```


With this change, we avoid the need for an extra type-cast, that is making the code harder to read, and even can involve more errors.

Yes, I agree with you, this is not solving the problem entirely. We have improved but we are not there yet. Let's see if Java provides more tricks ...

Pattern matching in switch cases
--------------------------------

In order to improve the readability a bit and reduce complexity we can use a switch/case statement. With this approach, we get rid of the "else if" clauses, making it clear that cases are exclusive and have different branches.

```
public String processElement(Object element) {
  String result;

  switch (element) {
   case (String s): 
     result = s;
     break;
   case (Person elementPerson):
    result = elementPerson.getName();
    break;
  }

  return result + " value";
}
```


But this code is still hard to read, I know. And it's still weak in terms of errors that can happen by missing one break or by not assigning the value to the intermediate variable.

Switch expressions
------------------

In order to fix this situation we can use a very interesting feature included in Java 14: switch expressions. We will reduce the code even more, increase the readability and clarity, and avoid the bugs caused by missing intermediate assignments.

Also, we reduce the [cognitive complexity](https://www.baeldung.com/java-cognitive-complexity "cognitive complexity") of the resulting code by half and this positively impacts the readability and maintainability of the code. We need to keep in mind that [too high complexity](https://rules.sonarsource.com/java/RSPEC-3776/ "too high complexity") is one of the [most common issues detected by Sonar tools](https://www.sonarsource.com/blog/top-issues-in-java-projects "most common issues detected by Sonar tools") in all the thousands of projects analyzed.

```
return switch (obj) {
    case Person person-> String.format("Person %s", person.getName());
    case String s -> String.format("Str %s", s);
    default -> obj.toString();
  } + " value";
```


With this approach, we have a very clear idea of what the code is doing and also reduce the risk of errors.

If you want to calculate the cognitive complexity of your code, you can use the ["Code complexity" plugin](https://plugins.jetbrains.com/plugin/21667-code-complexity "Code complexity plugin") (in IntelliJ) that will give you a hint of your method's complexity.

```
private void getStringsUsingInstanceOfIfs(Object user) { @ simple(25%)
    ...
}

private void getStringsUsingSwitchExpressionPattern(Object user) { @ simple(0%)
    ...
}
```


The Sonar Java analyzer will warn you if your code has too [high complexity](https://rules.sonarsource.com/java/RSPEC-3776 "high complexity"), and also will suggest using the [switch pattern matching approach](https://rules.sonarsource.com/java/RSPEC-6201 "switch pattern matching approach") and the [switch expression](https://rules.sonarsource.com/java/RSPEC-5194 "switch expression") in order to improve readability.

Conclusions
-----------

We [spend way more time reading code than writing it](https://bayrhammer-klaus.medium.com/you-spend-much-more-time-reading-code-than-writing-code-bc953376fe19 "spend way more time reading code than writing it"), so it's super important to make our code conventional and intentional in order to help us understand its purpose.

The Java language adds new features in every release to help you write[consistent, simple, and robust code](https://www.sonarsource.com/blog/what-is-clean-code/ " consistent, simple, and robust code") providing standardized ways of solving common issues and reducing the time to understand the purpose of the code and the probability of errors.

<br />

<br />
