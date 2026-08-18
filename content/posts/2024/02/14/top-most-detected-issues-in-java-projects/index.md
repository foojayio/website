---
title: "Java top most detected issues"
date: "2024-02-14T09:49:16+00:00"
lastmod: "2024-02-14T09:57:11+00:00"
description: "The most detected issues in Java projects by not following the clean code approach, and how to fix them with clear examples."
authors:
  - "jonathan-vila"
image: "1500x500-1.jpeg"
categories:
  - "Debugging"
  - "Developer Tools"
  - "Java"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "building-for-failure-best-practices-for-easy-production-debugging"
  - "effective-coding-with-java-observability"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**Using the telemetry from SonarLint after analyzing thousands of projects, these are the top most raised issues in Java projects.**

We know that having clean code in our projects is important, and every developer would agree on that. But, according to what SonarLint telemetry shows, there are still lots of issues that appear in the huge list of analyzed projects.

From the SonarLint telemetry for the last 2 years, with more than 2.5 million issues, I have taken the top most common issues happening in Java projects, from the +600 rules covering the language, considering quality and security to see how we can avoid them and align our code a bit more towards having a consistent, intentional, adaptable, and responsible code.

Although some issues may seem trivial, they can have a huge impact on the software delivered in terms of security, performance, and maintenance. Most of these issues are easy to follow, so it shouldn't be an issue to not implement them, considering the huge benefit of it and the low effort to put in.

And here they are...

## 1. Code commented out

Code commented out should be removed as it is making readability harder, and in case the code is needed again it can be retrieved from the version control system.

It also introduces uncertainty to the reader, as it is not clear if the code was commented out temporarily and needed to be uncommented again or simply it should have been removed.

```
public void println(String x) {
   if (getClass() == PrintStream.class) {
       writeln(String.valueOf(x));
   } else {
       synchronized (this) {
           print(x);
           //newLine();
       }
   }
}
```

**Hint**: check the commented-out code and remove it if it no longer applies to the submitted feature or uncomment it if it was a temporary disabling

## 2. Track uses of "TODO" tags

Leaving TODO comments in the source code, which most likely will survive eons, leads to code that is not complete and that can impact several areas.

Team collaboration: some team members might not be sure which features will be included in the final release.

Bugs: not implementing those parts now could lead to bugs in the future as this feature was expected

Performance: Usually, these TODO blocks are important but developers don't want to block the new feature, so maybe this importance can leak performance in the future

Here we have an example of a real project, Apache Camel, with a TODO line introduced 9 years ago.

```
SslHandler sslHandler = configureClientSSLOnDemand();
    if (sslHandler != null) {
         //TODO  must close on SSL exception
         //sslHandler.setCloseOnSSLException(true);
         LOG.debug("Client SSL handler configured and added to the ChannelPipeline: {}", sslHandler);
         addToPipeline("ssl", channelPipeline, sslHandler);
     }
```

**Hint**: do not add new TODO blocks and implement the feature before submitting the code or record these tasks in the proper task manager to tackle them in the future by the team.

## 3. String literals duplicated

Having duplicated strings will lead to extra work or missing changes when those values need to be changed to adjust to new conditions.

```
// Noncompliant - "action1" is duplicated 3 times
public void run() {
  prepare("action1");   
  execute("action1");
  release("action1");
}
```

**Hint**: use constants to store string literals, it will make refactoring easier and improve the consistency of the code base.

```
// Compliant
private static final String ACTION = "action1";

public void run() {
  prepare(ACTION);   
  execute(ACTION);
  release(ACTION);
}
```

## 4. Cognitive Complexity of functions should not be too high

You are probably more used to hearing about cyclomatic complexity, a concept to measure how many paths are used in the code and, therefore, the level of reading complexity for a given part of the code.

But cyclomatic complexity can not express the real maintainability level that needs more considerations apart from the number of conditionals and loops. Take a look at this blog to understand more about cognitive complexity.

```
//                                                 Cyclomatic Complexity    Cognitive Complexity
int sumOfPrimes(int max) {                        // +1
    int total = 0;
    for (int i = 1; i <= max; ++i) {                // +1                       +1
       for (int j = 2; j < i; ++j) {                   // +1                       +2 (nesting=1)
           if (i % j == 0) {                               // +1                       +3 (nesting=2)
              continue;                                   //                            +1
           }
        }
        total += i;
      }
  return total;
}
```

The key takeout of this issue is that usually projects are hard to read and understand, and this will impact understanding its intention and tackling its maintenance and evolution. When you come across code that has high cognitive complexity you should invest in refactoring the code so that your code-base becomes more understandable and maintainable over time.

**Hint**: consider the complexity index of your new code and invest time trying to reduce it according to the configured threshold that should be low enough.

## 5. Unused elements (imports, assignments, variables, private fields, parameters) should be removed

It's so common that when we start coding a feature we create elements of the code that at the moment of merging it to the main branch, no longer have any purpose. These unused elements do not cause runtime errors or failing tests so, it's hard to spot these elements, that need to be removed, or in the worst case, that will force us to rethink the code if what it's right is the existence of the element.

Unused elements will reduce the readability of the code making it harder to identify the intention of the code and give confidence in its completion, you should remove them.

```
public class MyClass {
    private int foo = 42;   //private field not used

    public int compute(int a, int b) { //b argument not used
   int c = 10; //local variable not used
       return a * 42;
    }

  public int run() {
    int value=10; //assignement not used
    value=compute(2, 5);
  }
}
```

**Hint**: check the unused code and remove the one that is no longer used or consider if there's missing code that would use that dead elements.

## 6. Raw types should not be used

In Java you should not use generic types without type parameters as it avoids the type checking and catching of unsafe code during the compilation, making everything visible during runtime.

```
// Noncompliant
List myList; 
Set mySet;
```

**Hint**: use specific types that will give the right idea to the users of those variables what is really expected, and ensure no surprises appear during runtime.

```
// Compliant solution
List<String> myList;
Set<? extends Number> mySet;
```

## 7. Generic exceptions should never be thrown

The usage of generic exceptions prevents the calling methods from handling different system-generated exceptions and application-generated errors.

```
// Noncompliant
public void foo(String bar) {  
   if (bar.isEmpty()) {  
    throw new Exception();     
  }
  if (bar == "jello") {
    throw new Exception();
  }
  System.out.println("This is bar: " + bar);
}
```

**Hint**: create a custom system of exceptions that will provide enough information to the caller in order to decide what to do, having a detailed and differentiated list of catches.

```
// Compliant
public void fooException(String bar) {  
   if (bar.isEmpty()) {  
    throw new EmpyValueException();     
  }
  if (bar == "jello") {
    throw new InvalidArgumentException();
  }
  System.out.println("This is bar: " + bar);
}
```

## Conclusions

We've seen some of the issues detected on all the projects analyzed by SonarLint, that are impacting not only the intentionality of the code but also the consistency and the adaptability of the software produced.

You can detect these issues by using a static analyzer, like SonarLint.

This follows the Clean as You Code methodology that helps you clean up a project by focusing on the new code introduced. Using CI tools like SonarQube/SonarCloud can then ensure no new issues are merged into your project by providing a customizable Quality Gate.
