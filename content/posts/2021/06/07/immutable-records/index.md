---
title: "Avoid Multithreading Bugs Using Immutable Java Records"
slug: "immutable-records"
date: "2021-06-07T07:52:44+00:00"
lastmod: "2021-07-12T04:59:40+00:00"
description: "Records help remove repetitive error-prone code, increasing developer productivity, with immutability keeping it away from concurrency bugs."
canonical: "https://jfeatures.com/blog/immutable_records"
authors:
  - "vipin-sharma"
image: "https://jfeatures.com/img/ebook_upd.png"
categories:
  - "Records"
tags:
related_posts:
  - "the-visitor-pattern-revisited-using-data-oriented-programming-techniques"
  - "java-23-has-arrived-and-it-brings-a-truckload-of-changes"
  - "java-22-is-here-and-its-ready-to-rock"
  - "java-21-is-available-today-and-its-quite-the-update"
enlighterjs: true
frozen: false
---

In a multi-threaded Java application, any thread can change the state of an object. The [Java memory model](https://foojay.io/today/demystifying-jvm-memory-management/) in Java language specification specifies when exactly updates made by one thread are going to be visible to other threads. This is one of the biggest problems professional Java developers deal with every day. Java records are immutable, an object is considered immutable if its state cannot change after it is constructed. The immutable nature of the record eliminates problems of its usage in a multithreaded environment.

++**[Previous article](https://foojay.io/today/records/)**++ on records explains how records provide a way to create data carrier-classes without writing a lot of boilerplate code. As part of this article, we will focus on the immutable feature of records. Immutability is one of the best features provided by records, we can use a record object without worrying about other threads changing its state.

### Java language features making record immutable

In this section, we will go through a table explaining how records are made immutable in the Java language. In the below table the 2nd column explains different ways to update the state of a record object, the 3rd column explains Java language features that prevent updating the state of the Record object.

| Index |                            Way to update record                             |                                                                                                                                                         Java language features preventing it                                                                                                                                                         |
|-------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1     | Create a record sub-class and change the state of the sub-class.            | Record classes are implicitly final and can not be abstract, this way we can not create a sub-class of the record class.                                                                                                                                                                                                                             |
| 2     | Extend some other class(superclass) and change the state of the superclass. | All record classes extend java.lang.Record by default so can not extend any other class.                                                                                                                                                                                                                                                             |
| 3     | Add instance fields that can be modified.                                   | Record classes cannot declare instance fields.                                                                                                                                                                                                                                                                                                       |
| 4     | Update record components.                                                   | We can not assign a new value to record components as these are implicitly final. These are assigned values while initializing the record in a canonical constructor.                                                                                                                                                                                |
| 5     | Update record components in the constructor.                                | Only canonical constructor can update record components, which is called while initializing record. For other types of constructors, assigning any of the record components in the constructor body results in a compile-time error.                                                                                                                 |
| 6     | Update record components using reflection.                                  | Record components have specific handling in Reflection ++**[Field API](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/reflect/Field.html#set(java.lang.Object,java.lang.Object))**++ . This treatment is like hidden classes. You can read more about Hidden classes ++**[here](https://jfeatures.com/blog/HiddenClass)**++. |

### Shallowly immutable

Record components are final, which means we can not change the record components once assigned. Although we can change fields of the record component, there is no restriction on that, it makes the record shallowly immutable. Let's see this with an example.

```
public class EmployeeTest {

    public static void main(String[] args) {

        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        IntegerListRecord integerListRecord = new IntegerListRecord(integerList);
        System.out.println(integerListRecord.getListSize());

        integerList.add(2);
        System.out.println(integerListRecord.getListSize());

    }

}

record IntegerListRecord(List<Integer> integerList) {
    int getListSize() {
        return integerList.size();
    }
}
```


In this example, we created a list of integers(integerList), added one element into it, and initialized the record class with this. Calling method getListSize of record class results into 1. Now we add one more element in integerList and calling getListSize results into 2. Here we did not change the record component (integerList) but updated the fields of the record component, which does not have any restriction. This is the reason we call the record shallowly immutable.

### Conclusion

Records help you remove repetitive and error-prone code, and increases developer productivity. The immutability feature keeps it away from concurrency bugs. Using language features like this is going to make you a great developer everyone will want to hire.

If you want to get amazing Java jobs, I wrote an ebook [5 steps to Best Java Jobs](https://jfeatures.com/). You can download this step-by-step guide for free!

[![](https://jfeatures.com/img/ebook_upd.png)](https://jfeatures.com/)

### Resources

1. [JEP 395](https://openjdk.java.net/jeps/395)
2. [https://cr.openjdk.java.net/\~briangoetz/amber/datum.html](https://cr.openjdk.java.net/~briangoetz/amber/datum.html)
3. [Java language specification: Java memory model](https://docs.oracle.com/javase/specs/jls/se16/html/jls-17.html#jls-17.4)
4. Reflection [Field API Javadoc](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/reflect/Field.html#set(java.lang.Object,java.lang.Object)).
