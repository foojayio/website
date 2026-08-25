---
title: "Getting Started with Java 16 and IntelliJ IDEA"
date: "2021-04-06T07:11:02+00:00"
lastmod: "2021-07-12T04:56:53+00:00"
description: "In this article, I will cover Java 16 to its language features, why you need them, and how you can start using them in IntelliJ IDEA."
canonical: "https://blog.jetbrains.com/idea/2021/03/java-16-and-intellij-idea/"
authors:
  - "mala-gupta"
image: "1200px-IntelliJ_IDEA_Logo.svg_.png"
categories:
  - "IntelliJ IDEA"
  - "Records"
related_posts:
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "exploring-java-records-in-a-jakarta-ee-context"
  - "java-on-azure-tooling-update-july-2022"
  - "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
frozen: false
---

If you are still working with Java 8, you might have mixed feelings about the news of the release of [Java 16](http://openjdk.java.net/projects/jdk/16/). However, you'll see these numbers are going to increment at a much faster and predictable rate with Java's six-month release cadence.

I'm personally excited about Java 16! It adds [Records](https://openjdk.java.net/jeps/395) and [Pattern Matching for instanceof](https://openjdk.java.net/jeps/394) as standard language features with [Sealed classes](https://openjdk.java.net/jeps/397) continuing to be a [preview feature](https://openjdk.java.net/jeps/12) (in the second preview).

Fun fact - Records was voted the most popular Java 16 language feature by 1158 developers in this [Twitter poll](https://twitter.com/eMalaGupta/status/1367274033184575497), with Pattern Matching for instanceof second.

In this blog post, I will limit coverage of Java 16 to its language features, why you need them, and how you can start using them in [IntelliJ IDEA](https://www.jetbrains.com/idea/nextversion/). You can use [this link](https://openjdk.java.net/projects/jdk/16/) for a comprehensive list of the new Java 16 features. Let's get started.

![Java 16 and IntelliJ IDEA](https://blog.jetbrains.com/wp-content/uploads/2021/03/Java16_blog.png)  

Records introduce a new type declaration that simplifies the task of modeling your immutable data. Though it helps cut down on boilerplate code significantly, that isn't the primary reason for its introduction. Here's an example:

```java
record Person(String name, int age) {}
```

With just one line of code, the preceding example defines a record `Person` with two components `name` and `age`. To create a record using IntelliJ IDEA 2021.1, select Record in the New Java Class dialog box. Fill in the name and you are good to go.

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-6-cover.png)

Let's quickly check the configuration of IntelliJ IDEA on your system to ensure you can get the code to run it.

## IntelliJ IDEA Configuration

Java 16 features are supported in IntelliJ IDEA 2021.1, which is scheduled to be released this March. The early access versions of 2021.1 are already available. You can configure it to use Java 16 by selecting 16 as the Project SDK and choosing *16 (Preview) – Sealed types* in the Project language level for your Project and Modules settings.

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-config.png)

You can also download Java 16 directly from IntelliJ IDEA. To do so, go to *Platform Settings* and click on *SDKs* , then click the '+' sign at the top, choose *Download JDK*, then select the Vendor and version and the directory to download the JDK to.

### Access sample code used in this blog post

All sample code used in this blog post is hosted at [Github](https://github.com/malagupta/Java16AndIntelliJIDEA).

## Implicit members added to a record

The compilation process creates a full-blown class – a record is defined as a final class, extending the `java.lang.Record` class from the core Java API. For each of the components of the record Person, the compiler defines a final instance variable (`name` and `age`). Interestingly, the name of the getter method is the same as that of the data variable (it doesn't start with `get` or `is`). Since a record is supposed to be immutable, no setter methods are defined.

The methods `toString()`, `hashCode()`, and `equals()` are also generated automatically for records.

## Why use records

Imagine you want to persist the details of, say, a person to a file. Before the introduction of records, you would need to define a regular class with instance variables, accessor methods, and implement methods from the Object class (`toString()`, `hashcode()`, and `equals()`). Though IntelliJ IDEA can generate all this code for you easily, there is no way to tag this class as a data class, so it would still be read as a regular class.

The following gif demonstrates how you can declare class `Person` as a regular class, generate code for it, implement the interface `Serializable` and run it to verify you can write it to a file and read from it:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-7-cover.png)

Now, let's declare `Person` as a record using just one line of code with components `name` (String) and `age` (int). It implements the interface `Serializable` since we need to persist its instances to a file. Note that you can still use the same methods to persist it to a text file. Also, there are no changes in how you instantiate it (by using the new operator):

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-15-cover.png)

Record `Person` is just one example on how you can use Records. You can use records to model your data, without additional overhead of defining additional methods.

## What you can and can't add to a record

Since the state of a record is defined using components in its declaration, it doesn't make much sense to allow the addition of instance variables (or fields) to a record. However, you can add static fields, and instance or static methods to a record, if you need them. Here's an example:

```java
public record Person(String name, int age) {
   Person {
       instanceCtr++;
   }
   private static int instanceCtr;
   static int getInstanceCtr() {
       return instanceCtr;
   }
}
```

## Modifying the default behavior of a constructor in a record

The default constructor of a record just initializes its state with the values you pass to it. You can change this default behavior if necessary, for example, to validate the parameter values before they are assigned.

IntelliJ IDEA lets you insert a compact, canonical, or custom constructor in a record. For your information, a compact constructor doesn't have a parameter list, not even parentheses. A canonical constructor is one whose signature matches with the record's state description. And a custom constructor lets you choose the record components you want to pass to the constructor of a record. With all these constructors, you can add validation code. A compact constructor enables you to add code without adding the full boilerplate code.

Let's see how you can insert a compact constructor using the Alt+Insert shortcut in IntelliJ IDEA, and add validation code to it:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-8-cover.png)

You can also add a canonical constructor to a record. This defines a parameter list – which must have the same names and order as those of the components of a record. A mismatch would result in a compilation error.

By invoking context actions in IntelliJ IDEA (with Alt+Enter), you can easily convert a canonical constructor to a compact constructor:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-9-cover.png)

## Truly immutable data

Records are truly immutable – you can't change their field values using reflection ( if you haven't tried it out yet, you can change the value of immutable strings using reflection).

Here is example code for a regular class, `Notebook`, which defines a private final field `pageCount`:

```java
public class NoteBook {
   private final int pageCount;

   public NoteBook(int pageCount) {
       this.pageCount = pageCount;
   }

   public int getPageCount() {
       return pageCount;
   }

   @Override
   public String toString() {
       return "NoteBook{" + "pageCount=" + pageCount + '}';
   }
}
```

And here's the code for a record `Point`:

```java
public record Point(int x, int y) {
}
```

The following code confirms that the private and final fields of a (regular) class can be changed using reflection, but records are a harder nut to crack:

```java
package com.jetbrains.java16.records;
import java.lang.reflect.Field;

public class UseReflection {
   public static void main(String[] args) throws Exception {
       changeFinalFieldValuesForNonRecords();
       changeFinalForRecords();
   }

   private static void changeFinalFieldValuesForNonRecords()
                throws NoSuchFieldException, IllegalAccessException {
       final var noteBook = new NoteBook(10);
       System.out.println(noteBook);

       Field pageField = noteBook.getClass().getDeclaredField("pageCount");
       pageField.setAccessible(true);
       int newCount = 1000;
       pageField.setInt(noteBook, newCount);

       System.out.println(noteBook);
   }

   private static void changeFinalForRecords()
                throws NoSuchFieldException, IllegalAccessException {
       final var point = new Point(12, 35);
       System.out.println(point);

       Field xField = point.getClass().getDeclaredField("x");
       xField.setAccessible(true);
       int newVal = 1000;
       xField.setInt(point, newVal);

       System.out.println(point);
   }
}
```

## Defining a Generic record

You can define records with generics. Here's an example of a record called `Parcel`, which can store any object as its contents, and capture the parcel's dimensions and weight:

```java
public record Parcel<T>(T contents,
   double length,
   double breadth,
   double height,
   double weight) {}
```

You can instantiate this record as follows:

```java
class Table{ /* class code */ }
public class Java16 {
   public static void main(String[] args) {
       Parcel<Table> parcel = new Parcel<>(
new Table(), 200, 100, 55, 136.88);
       System.out.println(parcel);
   }
}
```

## Converting Record to a regular class

If you are working with records but need to transition it to the codebase of an older Java version that doesn't support records, you can quickly convert a record to a regular class by using the context action Convert record to class or vice-versa by using the context action Convert to a record:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-10-cover.png)

## Records as components of record

A record component can be another record. In the following example, record `Automobile` defines one of its components as `Engine`, another record:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-11-cover.png)

## Adding annotations to record components

You can add an appropriate annotation to the components of a record, say, `@NotNull`, as demonstrated in the following gif:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-12-cover.png)

Developers often use third-party libraries like Jackson to persist value objects. Jackson supports records too. However, depending on the Jackson library version you are using (say, 2.11.3), you might need to annotate the components of your records using the annotation `@JsonProperty`, as follows:

```java
import com.fasterxml.jackson.annotation.JsonProperty;

public record Rectangle(
       @JsonProperty("width") int width,
       @JsonProperty("length") int length) {
}
```

**If you are using Jackson 2.12.2 or later versions, you don't need to annotate your record components with `@JsonProperty`.**   

Here's some sample code you can use to persist and read records using Jackson:

```java
public class ReadWriteRecordUsingJackson {

   public static void main(String[] args) {
       Rectangle rectangle = new Rectangle(20, 60);
       writeToFileUsingJackson(rectangle);
       System.out.println(readFromFileUsingJackson());
   }

   static void writeToFileUsingJackson(Object obj) {
       try {
           new ObjectMapper()
                   .writeValue(new FileOutputStream(getFile()),
                               obj);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   static Object readFromFileUsingJackson() {
       Object retValue = null;
       try {
           retValue = new ObjectMapper()
                        .readValue( new FileInputStream(getFile()),
                                    Rectangle.class);
       } catch (IOException e) {
           e.printStackTrace();
       }
       return retValue;
   }

   private static File getFile() {
       return new File("mydata.json");
   }
}
```

If you are working with a Maven project, you can add the following dependencies to your pom.xml:

```xml
<dependency>
   <groupId>com.fasterxml.jackson.core</groupId>
   <artifactId>jackson-core</artifactId>
   <version>2.12.2</version>
</dependency>
<dependency>
   <groupId>com.fasterxml.jackson.core</groupId>
   <artifactId>jackson-annotations</artifactId>
   <version>2.12.2</version>
</dependency>
<dependency>
   <groupId>com.fasterxml.jackson.core</groupId>
   <artifactId>jackson-databind</artifactId>
   <version>2.12.2</version>
</dependency>
```

## Reading and Writing Records to a File

You can write records to streams and read them, like other class instances. Let your record implement a relevant interface like the `Serializable` interface. Here's example code, which will write to and read from a file:

```java
package com.jetbrains.java16.records;
import java.io.Serializable;
public record Person(String name, int age) implements Serializable {
}

package com.jetbrains.java16.records;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
public class ReadWriteObj {

   public static void main(String[] args) throws Exception {
       Person person = new Person("Java", 25);
       writeToFile(person, "../temp.txt");
       System.out.println(readFromFile("../temp.txt"));
   }

   static void writeToFile(Object obj, String path) {
       try (ObjectOutputStream oos = 
                    new ObjectOutputStream(new FileOutputStream(path))){
           oos.writeObject(obj);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   static Object readFromFile(String path) {
       Object result = null;
       try (ObjectInputStream ois = 
                    new ObjectInputStream(new FileInputStream(path))){
           result = ois.readObject();
       } catch (ClassNotFoundException | IOException e) {
           e.printStackTrace();
       }
       return result;
   }
}
```

## Refactoring the signature of a Record

You can refactor a Record and modify the order of its components or types, modify their names, and add new or remove existing ones. IntelliJ IDEA has simplified how you apply Rename or Change Signature Refactorings. The changes would reflect in a record's canonical constructor and its instance creation:

![](https://blog.jetbrains.com/wp-content/uploads/2021/03/java16-13-cover.png)

## A restricted identifier

`record` is a restricted identifier (like `var`), but it isn't a regular keyword (yet). So, the following code is valid:

```java
int record = 10;
void record() {}
```

However, you may want to refrain from using `record` as an identifier because such code will become confusing as more developers start using records.

## Local records

You can define local records to model a domain object while you are processing values in a method. In the following example, the method `getTopPerformingStocks` finds and returns the names of the `Stock` with the highest value on a specified date.

```java
List<String> getTopPerformingStocks(List<Stock> allStocks, LocalDate date) {
   // TopStock is a local record
   record TopStock(Stock stock, double stockValue) {}

return allStocks.stream()
               .map(s -> new TopStock(s, getStockValue(s, dateTime)))               
               .sorted(Comparator.comparingDouble(TopStock::value).reversed())
               .limit(2)
               .map(s -> s.stock().name())
               .collect(Collectors.toList());
}
```

## Declaring implicit or explicit static members in an inner class

Starting from Java 16, an inner class can explicitly or implicitly define static members, including records.

## Local interfaces and enums

You can declare local enums and interfaces. You can encapsulate your data or business logic, which is local to a method, within the method.

```java
public void createLocalInterface() {
   interface LocalInterface {
       void aMethod();
   }
   // Code to use LocalInterface
}

public void createLocalEnum() {
   enum Color {RED, YELLOW, BLUE}
   // Code to use enum Color
}
```

However, they cannot capture any context variable. For example, for the local enum `Data`, the enum constants `FOO` and `BAR` can't be created using the method parameter input in the method `test()`:

```java
void test(int input) {
   enum Data {
       FOO(input), BAR(input*2); // Error. Can’t refer to input
       private final int i;
       Data(int i) {
           this.i = i;
       }
   }
}
```

***You can read about Sealed Classes and Interfaces as well as Pattern Matching for `instanceof` on the [main JetBrains IntelliJ IDEA blog](https://blog.jetbrains.com/idea/2021/03/java-16-and-intellij-idea/).***

IntelliJ IDEA is not only committed to supporting new Java features, but also to ensuring that our existing intentions and inspections work with them.

[IntelliJ IDEA 2021.1](https://www.jetbrains.com/idea/nextversion/) supports all the new language features from Java 16. Try out Sealed classes and interfaces, Records, and Pattern Matching for instanceof today.

We love to hear from our users. Don't forget to submit your feedback regarding the support for these features in IntelliJ IDEA.

Happy Developing!
