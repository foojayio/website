---
title: "Ensuring the right usage of the Java 21 new features"
slug: "ensuring-the-right-usage-of-java-21-new-features"
date: "2024-04-14T16:16:13+00:00"
lastmod: "2024-04-14T16:16:14+00:00"
description: "How to ensure the right usage of the new version of Java 21, and improve performance and clarity in our code base."
authors:
  - "jonathan-vila"
image: "Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "Java"
  - "JDK21"
tags:
related_posts:
  - "building-gdocweb-with-java-21-spring-boot-3-x-and-beyond"
  - "benchmark-jdbc-connectors-and-java-21-virtual-threads"
  - "revolutionising-java-collections-the-advent-of-sequenced-collections-in-java-21"
  - "sonarqube-part-4-ai-code-assurance"
enlighterjs: true
frozen: false
---

**Last September 2023 a new version of Java was released as the latest LTS (Long Time Support). This 21st version [brought lots of new features](https://www.sonarsource.com/blog/the-new-jdk-lts-is-out-long-live-jdk-21/ "brought lots of new features") that will improve performance and clarity in our code base.**

But taking advantage of these changes and new features, which we are not used to including in our code, can be a tough task. Also, it can lead to improper use or poor uptake, bugs, or basically not taking full advantage of new improvements.

To help you on that, the usage of SAST tools checking your code, and specifically Java 21 features, will guide you from the very beginning. You will benefit from the first keystroke with these tools in your IDE checking your code as you code, to the CI Quality Gates.

The [11 rules](https://rules.sonarsource.com/java/tag/java21 "11 rules") are as follows:

1. Use built-in "Math.clamp" methods
2. Use correct ranges with Math.clamp
3. Use SequencedCollection reversed() for reverse iteration order
4. Use reversed immutable lists with SequencedCollection reversed() view
5. Use switch instead of if-else for pattern matching
6. Use record pattern matching instead of explicit field access
7. Use VirtualThreads for heavy blocking operations
8. Don't misuse Thread methods with Virtual Threads
9. Virtual threads should not run blocks with synchronized code
10. Use guarded pattern labels instead of if/else
11. Use indexOf(char\|String, int, int) with correct ranges

## Use built-in "Math.clamp" methods

Sometimes you need to bounds check a number, ensuring that the value is not out of a certain range. To do this we've been using manual checks like these ones:

```
int clampedValue = value > max ? max : value < min ? min : value; // Noncompliant

int clampedValue = Math.max(min, Math.min(max, value)); // Noncompliant
```

These 2 options are hard to read and understand, and error-prone. The first one using the nested ternary operator [overcomplicates](https://www.baeldung.com/java-ternary-operator#:~:text=However%2C%20please%20note%20that%20it%E2%80%99s%20not%20recommended "overcomplicates") the code, making it difficult to understand the intention. The second one with the Math methods needs a deep read in order to understand it.

Which is the best approach then?

```
int clampedValue = Math​​.clamp(value, min, max);
```

The new Java 21 [Math.clamp](https://bugs.openjdk.org/browse/JDK-8301226 "Math.clamp") method is clear, and focused and reduces the options to include a bug.

## Use correct ranges with Math.clamp

When you use the Math.clamp method from Java 21 as suggested by the previous rule, you need to use the correct ranges, like other range-based APIs.

This method throws IllegalArgument exceptions when the ranges are not considered legal.

The following example throws an IllegalArgumentException:

```
Math.clamp(42, 0, -1); // Non compliant<code></code>
```

The following example is a redundant operation:

```
Math.clamp(42, 0, 0); // Non compliant
```

## Use SequencedCollection reversed() for reverse iteration order

When you need to iterate a collection but in reverse order, often you do manual processes using the iterator.

```
for (var it = list.listIterator(list.size()); it.hasPrevious();) {
  var element = it.previous();
  System.out.println(element);
}
```

This approach is verbose, hard to understand, and also can lead to errors if we don't do the right previous/hasPrevious calls.

Java 21 introduces the new [Sequenced Collections API](https://openjdk.org/jeps/431 "Sequenced Collections API"), which is applicable to all collections with a defined sequence on their elements, such as `LinkedList`, `TreeSet`, and others.

```
for (var element: list.reversed()) {
  System.out.println(element);
}
```

This approach is way clearer, doesn't give space to do it wrong, and ensures consistency across your code.

## Use reversed immutable lists with SequencedCollection reversed() view

Sometimes you need to iterate a collection in reverse order, and you have to do it manually, using the `Collections.reverse` method which mutates the list. Mutability can bring problems, especially in this case mutating the original list just to use a reversed view of it. Almost always [immutable approaches are preferred](https://www.sonarsource.com/blog/builders-withers-and-records-java-s-path-to-immutability/ "immutable approaches are preferred").

Java 21 introduces the new [Sequenced Collections API](https://openjdk.org/jeps/431 "Sequenced Collections API"), which is applicable to all collections with a defined sequence on their elements, such as `LinkedList`, `TreeSet`, and others.

For projects using Java 21 and onwards, this API should be utilized instead of workaround implementations that were necessary prior to Java 21.

For read-only usages of reverse iterations, the old `Collection.reverse(List)` call should be replaced by `SequencedCollection.reversed()` which will not mutate the original collection.

```
void foo(List<String> list) {
  var copy = new ArrayList<String>(list);
  Collections.reverse(copy); // Noncompliant
 // do something
 // ...
}
```

Should be changed to:

```
void foo(List<String> list) {
  var reverseList = list.reversed(); // Compliant
  // do something
  // ...
}
```

## Use switch instead of if-else for pattern matching

In versions of Java before 21, matching a variable against multiple patterns required you to chain if/else statements. However, since Java 21, the enhanced switch expression is a preferable alternative in most scenarios.

Using a switch expression provides advantages such as clearer code, assurance of handling all cases, and improved performance.

```
if (expr instanceof Plus plus) { // Noncompliant
  ...
} else if (expr instanceof Div div) {
    ...
} else ...

if (c == Color.Red) {
 ...
} else if (c == Color.Green) { // Noncompliant
 ...
} else ...

if (x == 2) { // Noncompliant
 ...
}  else if (x == 3 || x==4 ) {
 ...
}  else ...

if (shape instanceof Circle) { // Noncompliant
  Circle circle = (Circle) shape;
  ...
} else if (shape instanceof Rectangle) {
  Rectangle rectangle = (Rectangle) rectangle
  ...
} else ...
```

But we can use `switch expressions` in order to make this code more readable, and also reduce the cognitive complexity.

```
switch (expr) {
 case Plus(left, right) -> eval(left) + eval(right)
 case Div(left, right) -> eval(left)/eval(right)
 ...
}

switch (c) {
 case Red -> ...
 case Green -> ...
 ...
}

switch (x) {
 case 2 -> ...
 case 3, 4 -> ...
}

switch (shape) {
 case Circle circle -> ...  
 case Rectangle rectangle -> ...
 ...
}
```

## Use record pattern matching instead of explicit field access

When you use type pattern matching you also declare a local variable of the type you matched against, to easily access its specific members, which is a benefit on top of the use of the instanceOf conditionals.

```
static void printSum(Object obj) {
    if (obj instanceof Point p) {
        int x = p.x();
        int y = p.y();
        System.out.println(x+y);
    }
}
```

With Java 21 we can now go a step further when we type-match on records, directly extracting their components into local variables, improving readability, and reducing the possibility of introducing errors with bad or missing assignments.

```
static void printSum(Object obj) {
    if (obj instanceof Point(int x, int y)) {
        System.out.println(x+y);
    }
}
```

## Use VirtualThreads for heavy blocking operations

Java 21 comes with a powerful feature called [Virtual Threads](https://openjdk.org/jeps/444 "Virtual Threads"). Before this, when you created a new Thread it was taking a thread from the OS. This basically meant that depending on the CPU we were capable of creating only a specific number of threads.

```
Thread t = new Thread(() -> {   // Noncompliant 
      //some Http method invokation
    }).start();
```

But now these virtual threads come from a shared pool of OS threads allowing us to create millions of threads that will be put on hold for access to the IO system.

So, using virtual threads is the suggested approach.

```
Thread t = Thread.ofVirtual.start(() -> {  // Compliant
      //some Http method invokation
    });
```

## Don't misuse Thread methods with Virtual Threads

If you want to migrate from the use of platform Threads to the new Java 21 Virtual Threads there are some methods that you should not use since they don't make any sense for the new type and can even cause runtime errors.

In the old platform threads, we could have a code similar to this:

```
var kernelThread = new Thread(printThread);
kernelThread.setPriority(Thread.MIN_PRIORITY);
kernelThread.setDaemon(false);
System.out.println("Group:" + kernelThread.getThreadGroup());
kernelThread.start();
```

However, the 3 central methods will have no effect or result in a runtime exception when migrated to Virtual Threads.

```
var virtualThread = Thread.ofVirtual().factory().newThread(printThread);
virtualThread.setPriority(Thread.MIN_PRIORITY); //Not compliant
virtualThread.setDaemon(false); //Not compliant
System.out.println(virtualThread.getThreadGroup()); //Not compliant
virtualThread.start();
```

Virtual threads are always daemon threads, so invoking `.setDaemon()` will not change them to non-daemon threads. It will, at best, have no effect, and at worst (when you pass false as a parameter) cause an IllegalArgumentException.

The same goes for .setPriority because the priority of virtual threads cannot be changed from `Thread.NORM_PRIORITY`, and finally virtual threads are not active members of a ThreadGroup, therefore invoking `.getThreadGroup()` on a virtual thread returns a dummy "VirtualThreads" group that is empty.

## Virtual threads should not run blocks with synchronized code

The CPU usage optimization introduced with VirtualThread relies on the fact that these new types of threads can be "mounted" and "dismounted" from an OS thread whenever they find themselves waiting for some blocking operation ( I/O, network, etc..).

When the task wrapped by the virtual thread runs [synchronized code](https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html "synchronized code"), which will prevent other threads from entering that method, it will get pinned to its current underlying OS thread.

If during this time a blocking operation occurs, the virtual thread will not be dismounted, blocking the OS thread, and defeating the purpose of using a virtual thread in the first place.

```
Thread.startVirtualThread(() -> { // Noncompliant
      synchronized(this) {
        System.out.println();
      }
    });

Thread.startVirtualThread(() -> synchronizedMethod()); // Noncompliant
private synchronized void synchronizedMethod() { 
  System.out.println(); 
}
```

In order to obtain the best result from the Virtual Threads we should not use synchronized blocks that will block the thread.

## Use guarded pattern labels instead of if/else

When we check for the type of an object, often it also involves checking the object value. Even when we use pattern matching to make the code more readable and avoid the use of `instanceOf`, our code is still not using all the benefits of the Java language.

[Guards](https://medium.com/@scadge/if-statements-design-guard-clauses-might-be-all-you-need-67219a1a981a "Guards") are a safe and clear approach when evaluating different branches in our code but have preconditions that will make the code that follows irrelevant. So, using a guard instead of a control flow operation inside the pattern body makes the code more readable.

This is a common piece of Java code using switch pattern matching and conditionals:

```
static void testStringOld(String response) {
    switch (response) {
        case null -> { }
        case String s -> {
            if (s.equalsIgnoreCase("YES")){
              System.out.println("You got it");
            }
        }
    }
}
```

But, we can go further. Java 21 implements [guarded pattern labels](https://docs.oracle.com/en/java/javase/21/language/pattern-matching-switch-expressions-and-statements.html#GUID-A5C220F6-F70A-4FE2-ADB8-3B8883A67E8A:~:text=println(%22Something%20else%22)%3B%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%7D-,When%20Clauses,-You%20can%20add "guarded pattern labels") that can be used in switch pattern matching expressions that will make the code more readable.

```
static void testStringNew(String response) {
    switch (response) {
        case null -> { }
        case String s when s.equalsIgnoreCase("YES") -> {
            System.out.println("You got it");
        }
    }
}
```

## Use indexOf(char\|String, int, int) with correct ranges

Java 21 adds new indexOf methods that accept ranges rather than single start or stop indices. While these new API methods make it easier to provide ranges rather than having to do substring operations and adding/subtracting resulting offsets, they also throw StringIndexOutOfBounds exceptions when the range used is not considered legal.

The following cases all throw a StringIndexOutOfBoundsException but are not detected at compile time.

```
String message = "Hello, World!";
message.indexOf('!', -1, message.length()); // Noncompliant, beginIndex is negative
message.indexOf('!', 1, 0); // Noncompliant, beginIndex is greater than endIndex
message.indexOf(',', 0, message.length() + 1); // Noncompliant, endIndex is greater than the string's length by 1
```

## Conclusion

Java 21 brings a lot of new features and methods that will help us to code in a more consistent way. But it's easy to not be aware of them or miss their usage as it's a relatively new version.

Clean code also means using our programming language in the best possible way, including taking advantage of the methods provided to solve problems in a more efficient and consistent way, and doing it without misusages especially when migrating code from an older version of Java to a newer one.

The use of tools, like [SonarLint](https://www.sonarsource.com/products/sonarlint/ "SonarLint"), on the coding side can help us discover the best ways to code using the last features and improve our code in performance and readability.
