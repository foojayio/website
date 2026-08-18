---
title: "Just-Write-And-Run prototyping with JEP-477"
slug: "exploring-new-features-in-jdk-23-just-write-and-run-prototyping-with-jep-477-not-only-for-beginners"
date: "2024-10-23T09:37:04+00:00"
lastmod: "2024-10-23T09:45:14+00:00"
description: "JEP-477 can be considered an example of the symbiotic development of multiple JEPs together, similar to the result of the development of the Java platform."
authors:
  - "miro-wengner"
image: "Favicon-3-2.png"
categories:
  - "Java"
  - "Java Beginner"
  - "JDK 23"
tags:
related_posts:
  - "exploring-new-features-in-jdk-23-simplifying-java-with-module-import-declarations-with-jep-476"
  - "exploring-new-features-in-jdk-23-null-object-pattern-to-avoid-null-pointer-exception-with-jep-455"
  - "exploring-new-features-in-jdk-23-gatherers-upgrades-pipeline-design-pattern-jep-461"
  - "exploring-new-features-in-jdk-23-module-design-pattern-with-jep-476"
enlighterjs: true
frozen: false
---

### It seems like it's never been easier to start writing a simple program that can be turned into a more advanced one as development progresses. Let's explore possibilities delivered by JDK 23 release.

**From the early days, Java may have struggled with a relatively verbose and sometimes strikingly complicated process of creating a small program. A small program just to validate an idea or assumption. Let's call that Just-Write-And-Run feature. For example, it's required to create a trivial solution for reading and parsing data, Oh now what...**

Of course, this may seem similar to the perspective of a novice developer on a platform. In such a case it is also important to reduce all steps to a minimum in order to "be up and running" without unnecessary frustration caused by the obstructions, language or rules of the platform.

Fortunately, with a cadence of 6-month release cycles, Java has already been able to improve on the missing simplifications and continue to evolve towards a bright future while maintaining the quality of a strongly-typed language and platform.

JEP-477: Implicitly declared classes and main instance methods (Third Preview) bring further improvements, namely implicitly imported classes for I/O and automatically imported classes on demand from the java.base module, making composing trivial to advanced programs very easy. The automated import of the java.base module is a result of JEP-477 being closely developed with JEP-476: Module Import Declaration(Preview) \[7\].

The Java Platform has already introduced several important JEPs, such as type inference of local variables (JEP-286\[4\]), the ability to run multi-file source programs with the need to define a package structure (JEP-458\[5\]), etc.

## Analyzing, Parsing and understanding data

In an age where machine learning techniques are required on a daily basis, it is essential to understand, analyze data quickly and parse them correctly. A data analysis scenario can take many forms.

One could be to generate a record-specific sequence. The output represents strings with specific restrictions, an example is SQL statement. The input may be a specific time or number that requires manipulation and transformation before the final statement is created (Example 1.).

```
File: Jep477Main.java
```

```
void main()  {
    var statement  = template("one", 1);
    println(statement);
}
String template(String name, int value) {
    return String.format("record name:%s, value:%d", name, value);
}
```

```
Command: $java --enable-preview Jep477Main.java
```

```
Output:
record name:one, value:1
```

**Example 1.** : Java allows direct execution without having to worry about imports for the *java.base* module and traditional I/O operations like *printnl(..)*

In fact, such use of Java is very useful as it helps to preserve the already created code for future use. Due to the JEP-477\[1\] Java platform is enabled to create various scenarios in the Just-Write-And-Run style.

This could come in even more handy when the logic couldn't be kept exactly inside just one file for readability and separation of concerns (JEP-458\[5\]). The JEP-477\[1\] allows developers to use a style similar to rapid prototyping to quickly validate an idea. A nice example could be to use a stream of numbers applying creational design patterns (*SampleFactory*) when processing multiple intermediate operations and splitting the result into chunks of output data using build-in gatherers (JEP-473\[6\], Example 2.).

```
File: Jep477Main.java
```

```
void main() {
    var list = IntStream.range(0, 10).boxed()
        .map(i -> SampleFactory.createSample(i))
        .gather(Gatherers.windowFixed(3))
        .collect(Collectors.toList());
    println("list:" + list);
}
```

```
File: Sample.java
```

```
record Sample(String name, int value){}
```

```
File: SampleFactory.java
```

```
public class SampleFactory{
    static Sample createSample(int number){
        return switch (number){
            case int i when i % 3 == 0 -> new Sample("one", 42);
            case int i when i % 2 == 0 -> new Sample("two", 22);
            default -> new Sample("", -1);
        };
    }
}
```

```
Command: $  java --enable-preview Jep477Main.java
```

```
Output:
list:[[[Sample[name=one, value=42], Sample[name=, value=-1], Sample[name=two, value=22]], [Sample[name=one, value=42], Sample[name=two, value=22], Sample[name=, value=-1]]], [[Sample[name=one, value=42], Sample[name=, value=-1], Sample[name=two, value=22]], [Sample[name=one, value=42]]]]
```

**Example 2.**: Java platform allows to execute program considering multiple files present inside the folder

Although it sounds great, there are some limitations, one of which can be considered the definition of a package private class inside the *SampleFactory.java* file or other related files, which will throw a class loader exception(Example 3.). Hopefully this can be overcome in the next iteration.

```
File: SampleFactory.java
```

```
record Sample(String name, int value){}
public class SampleFactory{
   static Sample createSample(int number)
```

```
Command: $ java --enable-preview Jep477Main.java
```

```
Output:
Exception in thread "main" java.lang.NoClassDefFoundError: Sample
        at java.base/java.lang.Class.getDeclaredMethods0(Native Method)
        at java.base/java.lang.Class.privateGetDeclaredMethods(Class.java:3650)
...
```

Example 3.: The definition of the *Sample record* is located in the *SampleFactory.java* file

Despite such limitations, the ability to use Java is also very useful for such cases as mind context switch is not required. The following example demonstrates the power of JEP-477 by reading and analyzing data provided by a given text file. The content of the text file can also include log records that need to be manipulated in a certain way in order to extract the required information (Example 4.)

```
File: Jep477Main.java
```

```
void main() throws IOException {
   ...
   var fileContentString = Files.readString(Path.of("text.txt"));
   var fileContentAsSet = Arrays.stream(fileContentString.split(","))
  .collect(Collectors.toSet());
   ...
   println("fileContentAsSet:" + fileContentAsSet);
}
```

```
Command:$ java --enable-preview Jep477Main.java
```

```
Output: 
fileContentAsSet:[1, 2, 3]
```

**Example 4.**: Reading and analyzing files is possible without the need to import additional classes as java.base module is already imported by default

The example shows that despite the automatic imports, a correct checked exception definition is still required.

## Conclusion

**In** fact, JEP-477 can be considered an example of the symbiotic development of multiple JEPs together, similar to the result of the development of the Java platform.

Written code has become more readable and understandable as verbosity has been reduced to an acceptable minimum.

Although an already existing jShell might be a good alternative, the ability to already structure the code into different files/classes would not only be useful for beginners, but also for developers of various experience levels.

JEP-477 can meet current business requirements driven by the use of various artificial intelligence techniques and its another great example of big step forward.

## References

[\[1\] JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)](https://openjdk.org/jeps/477)  
[\[2\] Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developers](https://amzn.eu/d/a3CsBu7)  
[\[4\] JEP 286: Local-Variable Type Inference](https://openjdk.org/jeps/286)  
[\[5\] JEP-458: Launch Multi-Files Source-Code Programs](https://openjdk.org/jeps/458)  
[\[6\] JEP-473: Stream Gatherers (Second Preview)](https://openjdk.org/jeps/473)  
[\[7\] JEP-476: Module Import Declaration (Preview)](https://openjdk.org/jeps/476)
