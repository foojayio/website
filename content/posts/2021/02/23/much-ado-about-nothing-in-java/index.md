---
title: "Much Ado About Nothing (Also Known As \"Null\") In Java"
date: "2021-02-23T15:19:02+00:00"
lastmod: "2023-02-13T12:18:59+00:00"
description: "Did you know \"var\" won’t work with a null assignment because no specific type can be inferred and we cannot declare a variable of null type?"
authors:
  - "simonritter"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "avoiding-nullpointerexception"
  - "null-safety-kotlin-vs-java"
  - "handling-null-optional-and-nullable-types"
frozen: false
---

Occasionally something in Java pops up that I thought I knew about, but it turns out I didn't appreciate all the subtle details.

This was recently the case for

```java
null
```

.

Before I started using Java, the main programming language I used was C. This was great for things like operating systems and device drivers because it uses ***explicit*** pointers. References to data are through a numerical address that can be manipulated if required. This was probably (for me at least) the hardest thing to master, especially when you try to figure out things like this:

```c
char *(*foo)(char *);
```

Eventually, you learn the order of precedence for pointers and understand that, in this case,

```c
foo
```

is a pointer to a function that takes a pointer to a

```c
char
```

as a parameter and returns a pointer to a

```c
char
```

.

Explicit pointers provide an exact reference to an address in memory (albeit often mapped from a virtual address space to a physical memory location by the memory management unit). It is sometimes desirable to set the value of a pointer in C to something that indicates it is not a valid memory address. Unfortunately, an address of zero can be quite valid, so the C standard provides an implementation-dependent pre-processor macro, NULL, which is a null pointer constant. As a developer, we don't concern ourselves with what the value is, just that it indicates a pointer to nothing.

Using simple arithmetic on an explicit pointer is often useful for accessing data by calculating its position as an offset to the pointer. The drawback is that you need to be sure your calculations are correct. It's all too easy to make a mistake and end up with a pointer that attempts to use an invalid address when dereferenced. To avoid this problem, Java takes a different approach to references and uses ***implicit*** pointers. When you instantiate an object, e.g.:

```java
Properties p = new Properties();
```

The value of

```java
p
```

will be a reference to an instantiated instance of the

```java
Properties
```

class. We have no way, however, of determining the actual address of this object. Trying to manipulate a reference as if it was an address is not valid Java syntax and will fail to compile. (There is no way to get the address of a variable as we can do in C with the \& operator.)

Whenever we dereference

```java
p
```

, for example, by calling

```java
p.propertyNames()
```

, the JVM handles the details of locating the instance

```java
p
```

in the heap and calling the appropriate method on it. This is one of the reasons why the JVM can relocate objects in the heap to reduce fragmentation during garbage collection.

We may well have a situation where the scope we want for our variable prevents us from initialising it at the point where it is declared. Since we have no object to assign a reference to, we need some way to indicate this to the compiler.

We can do this by setting the value to be

```java
null
```

, either implicitly or explicitly:

```java
String t;             // Implicit, instance variable

String s = null;      // Explicit, local variable
```

At this point, I'm sure you're thinking, "Hmmm. This is all Java 101... so, what's the big deal?"

To which, the related question is, "What is the type of

```java
null
```

?"

In the examples above, this is easy to answer: it's

```java
String
```

. Great, but is it really? Let's look at an example from JDK 10, when local variable type inference was introduced:

```java
var x = “Hello, World!”;
```

By replacing an explicit type with

```java
var
```

, we are now leaving it to the compiler to ***infer*** the correct type of

```java
x
```

. Here, it's straightforward, as we've assigned a

```java
String
```

literal, so the type can only be

```java
String
```

.

What about this, though:

```java
var y = null;
```

This code will not compile, resulting in the messages "

```java
error: cannot infer type for local variable y
```

" and "

```java
variable initializer is ‘null’
```

". You could be forgiven for thinking (as I did initially) that the compiler could infer a type here. In Java, we know that all classes ultimately inherit from

```java
Object
```

.

As a small aside here,

```java
Object
```

does not extend itself (which seems obvious). The Java Language Specification (JLS section 8.1.4) makes this clear:

> "*The extends clause must not appear in the definition of the class Object, or a compile-time error occurs, because it is the primordial class and has no direct superclass.*"

Logically, then, the compiler ***could*** infer that

```java
y
```

is of type

```java
Object
```

, assign a value of

```java
null
```

to it, and everything would work as expected. So, why doesn't it?

This is where the nuances of

```java
null
```

become important!

Referring to the JLS again, we find section 3.10.7, which defines the

```java
null
```

literal:

> "*The null type has one value, the null reference, represented by the null literal null, which is formed from ASCII characters.*"

It also states:
> "*A null literal is always of the null type."*

Section 4.1, "The Kinds of Types and Values", provides clarification of what the

```java
null
```

type is:

> "*There is also a special null type, the type of the expression null, which has no name.*
>
> *Because the null type has no name, it is impossible to declare a variable of the null type or to cast to the null type.*
>
> *The null reference is the only possible value of an expression of null type.*
>
> *The null reference can always be assigned or cast to any reference type.*"

There are two facts in this definition that explain the compiler error.

1. The first is that you cannot declare a variable of the 

```java
null
```

   type, which is effectively what we're trying to do when we use

```java
var
```

   in our example.   
2. The second is that 

```java
null
```

   can always be assigned to any reference type. Stuart Marks explained this to me very clearly when I discussed this issue with him. He said that in terms of the Java type hierarchy,

```java
Object
```

   is at the top, and all types ultimately inherit from that. The

```java
null
```

   type is at the *bottom* of the type hierarchy in that it represents *all* types simultaneously.

This is why `var` won't work with a `null` assignment: because no specific type can be inferred from it and we cannot declare a variable of the `null` type.

Another place where the

```java
null
```

type may not behave the way you expect is with the *instanceof* operator. Let's look at this piece of code:

```java
Date d = null;

if (d instanceof Date)
  System.out.println("We have a Date!");
else
  System.out.println("No Date here...");

if (d instanceof Object)
  System.out.println("We have an Object!");
else
  System.out.println("No Object here...");
```

The variable

```java
d
```

is explicitly defined as a

```java
Date
```

, so surely

```java
d
```

is an instance of

```java
Date
```

, correct? When you run this code, it will print the following:

```
No Date here…

No Object here…
```

The variable

```java
d
```

therefore holds neither a

```java
Date
```

*nor* an

```java
Object
```

. To get to the bottom of this, we need to look at the bytecodes generated by the compiler. Using

```java
javap -c
```

we can do that and see:

```
Code:
       0: aconst_null
       1: astore_1
       2: aload_1
       3: instanceof    #7    // class java/util/Date
       6: ifeq          20
       9: getstatic     #9    // Field java/lang/System.out:Ljava/io/PrintStream;
      12: ldc           #15   // String We have a Date!
      14: invokevirtual #17   // Method java/io/PrintStream.println
      17: goto          28
      20: getstatic     #9    // Field java/lang/System.out:Ljava/io/PrintStream;
      23: ldc           #23   // String No Date here...
      25: invokevirtual #17   // Method java/io/PrintStream.println
      28: aload_1
      29: instanceof    #2    // class java/lang/Object
      32: ifeq          46
      35: getstatic     #9    // Field java/lang/System.out:Ljava/io/PrintStream;
      38: ldc           #25   // String We have an Object!
      40: invokevirtual #17   // Method java/io/PrintStream.println
      43: goto          54
      46: getstatic     #9    // Field java/lang/System.out:Ljava/io/PrintStream;
      49: ldc           #27   // String No Object here...
      51: invokevirtual #17   // Method java/io/PrintStream.println
      54: return
```

The key here is the first instruction,

```java
aconst_null
```

, where we assign

```java
null
```

to our variable,

```java
d
```

. The description of this operation in the Java Virtual Machine Specification is "*Push the null object reference onto the operand stack.* " It also says, "*The Java Virtual Machine does not mandate a concrete value for null.* " Since the

```java
null
```

object reference is neither a

```java
Date
```

nor

```java
Object
```

type, the tests fail.

The JLS tells us that null can be cast to any reference type, so we could try casting our null to a

```java
Date
```

type:

```java
Date d = (Date)null;
```

Doing this makes no difference to either the results of running the application or the bytecodes generated by the compiler.

As you can see, although

```java
null
```

might seem like a simple, straightforward concept, there are some edge cases that make its use require a little more thought.

I hope this provides you with a better understanding of nothing (

```java
null
```

)!
