---
title: "Builders, Withers, and Records - Java’s path to immutability"
slug: "builders-withers-and-records-javas-path-to-immutability"
date: "2024-02-28T12:19:11+00:00"
lastmod: "2024-03-06T11:01:56+00:00"
description: "Immutability in Java with creational patterns Builders and Withers, along with a new type of immutable object in Java: Records."
authors:
  - "jonathan-vila"
image: "sonarlint.png"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "top-most-detected-issues-in-java-projects"
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "12-lessons-learned-from-doing-the-one-billion-row-challenge"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
enlighterjs: true
frozen: false
---

**We know that immutable objects are easier to maintain, lead to fewer errors, and are multi-thread friendly. In this article, I will talk about two different approaches in Java to creating objects: Builders and Withers, typically used in the context of immutable objects, along with a new type of immutable object in Java: Records.**

When it comes to creating objects in Java, we can use fluent approaches, especially for complex objects containing lots of fields, that will increase readability and also adaptability allowing us to evolve the code with lower impact on the existing code. And we want to because fluent code is both easier to read and easier to write.

We also know that immutable objects are easier to maintain, lead to fewer errors, and are multi-thread friendly.

In this article, I will talk about two different approaches to creating objects: Builders and Withers, typically used in the context of immutable objects, along with a new type of immutable object in Java: Records.

JavaBean pattern
----------------

The usual way of defining classes in Java follows the [JavaBean pattern](https://en.wikipedia.org/wiki/JavaBeans "JavaBean pattern"). This involves using a default constructor with no arguments, and accessors and mutators for properties.

```
public class Person {
  private int age;
  private String name;

  public int getAge() {
    return age;
  }

  public String getName() {
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setName(String name) {
    this.name = name;
  }
}

Person person = new Person();
person.setAge(15);
person.setName("Antonio");
```


This approach implies that the state of the object can be "unsafe" as we could create an instance of Person without specifying any mandatory and key values. It even allows mutating the object during its lifetime, potentially making the system [less safe](https://blogs.oracle.com/javamagazine/post/java-immutable-objects-strings-date-time-records "less safe"), especially with multithreaded approaches. [Immutability brings a lot of benefits](https://dzone.com/articles/java-and-immutability-avoid "Immutability brings a lot of benefits").

The path to immutability and a safe state
-----------------------------------------

So, the next step in order to fix this issue would be to create a constructor with the mandatory and key properties, and not expose mutators (setters) for them.

```
public class Person {
  private int socialNumber;
  private String name;
  private String address;

  public Person(String name, int socialNumber) {
    if (name == null || name.isBlank()) 
      throw new IllegalArgumentException();
    this.name = name;
    this.socialNumber = socialNumber;
  }

  public int getSocialNumber() {
    return socialNumber;
  }

  public String getName() {
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
}

Person person = new Person("Antonio", 1566778890);
person.setAddress("Barcelona");
```


With this approach though, we face potential issues in terms of readability and adaptability when the class grows into a more complex definition.

```
public Person(String name, int age, String id, String phoneNumber, String email, Person parent1, Person parent2) { ... }

Person person = new Person("Antonio", 15, 1445678, "+34 666 77 88 99", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="14757a607b7a7d7b54716c75796478713a777b79">[email protected]</a>", juan, carla);
```


In case we add more mandatory properties, as we see above, we need to add more parameters to the constructor and this will impact the existing code making us modify it on every call to the constructor.

Considering mandatory and optional arguments, for immutable objects, we can run into the ["telescoping constructors"](https://medium.com/nerd-for-tech/avoid-telescoping-constructors-with-the-builder-pattern-2114b75360b7 "“telescoping constructors”") problem where we need to create several constructors considering the different nullability combinations.

```
public Person(String name, int age, String id, String phoneNumber, String email, Person parent1, Person parent2) {...}
public Person(String name, int age, String id, String phoneNumber, String email, Person parent1,) {...}
public Person(String name, int age, String id, String phoneNumber, String email) {...}
public Person(String name, int age, String id, String phoneNumber) {...}
```


The Builder approach
--------------------

To fix this we can use Builders, which will help with readability and also on future changes making it easier to add the new properties.

First let's remove any mutator, leave the accessors, and make it "impossible" to create a new instance with a constructor.

```
public class Person {
  private String name;
  private int socialNumber;

  // Invisible constructor 
  private Person() {
  }

  public String getName() {
    return this.name;
  }
  public int getSocialNumber() {
    return this.socialNumber;
  }

  @Override
  public String toString() {
    return "Person [name=" + name + ", socialNumber=" + socialNumber + "]";
  }
}
```


Now we will add the inner class in charge of building the new instance and a new method that invokes the Builder.

```
// inside Person class

// Fluent Builder API
public static PersonBuilder builder() {
  return new PersonBuilder();
}

public static class PersonBuilder {
  private String name;
  private int socialNumber;

  PersonBuilder() {
  }

  public PersonBuilder name(String name) {
    this.name = name;
    return this;
  }

  public PersonBuilder socialNumber(int socialNumber) {
    this.socialNumber = socialNumber;
    return this;
  }

  public Person build() {
    // Validations
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException();
    }
  // Build
    Person person = new Person();
    person.name = name;
    person.socialNumber = socialNumber;
    return person;
  }
}
```


And with this approach now we are able to create a new immutable instance with a validated state.

```
Person person = Person.builder()
                        .name("Antonio")
                        .socialNumber(15546464564)
.build();
```


The above approach includes a lot of boilerplate code that can discourage us from using it. To make things easier we can use libraries with annotations that will generate the code for us: [Immutables](https://immutables.github.io/immutable.html#:~:text=.build()%3B-,Builder,-By%20default%2C%20builders), [Lombok](https://projectlombok.org/features/Builder "Lombok"), [Auto](https://github.com/google/auto/blob/main/value/userguide/autobuilder.md "Auto"), [FreeBuilder](https://freebuilder.inferred.org/ "FreeBuilder"), etc.

```
@lombok.Builder
public class Person {
  private String name;
  private int socialNumber;
}

Person person = Person.builder().name("Antonio").socialNumber(2023452).build();
```


The Wither approach
-------------------

Another approach to having a fluent API and immutability is the usage of "withers", or with\* methods, that create a new instance on every property change.

The idea behind it is that every mutator creates a new object instance, and we can chain those calls in order to produce complete instances.

```
// inside Person class
// remove setters

public Person(String name, int age) {
  if (name == null) throw new NullPointerException("name");
  this.name = name;
  this.age = age;
}

public Person withName(String name) {
  if (name == null) throw new NullPointerException("name");

  return (this.name == name) ? this : new Person(name, age);
}

public Person withAge(int age) {
  if (age < 0) throw new IllegalArgumentException("age");

  return (this.age == age) ? this : new Person(name, age);
}
```


We can consume this approach like this, making it very easy to apply small changes to an existing object by obtaining a new object. We are "cloning" the object and changing one property at a time.

```
Person person = new Person("Luis", 45);
Person person2 = person.withName("Jose");
// here we have person2 = Jose, 45
```


Again in order to reduce boilerplate code, and be less error-prone, we can leverage existing libraries with annotation processors that will make the process smoother and cleaner.

```
public class Person {
  @lombok.With @NonNull private final String name;
  @lombok.With private final int age;

  public Person(@NonNull String name, int age) {
    this.name = name;
    this.age = age;
  }
}
```


The main drawback to the Withers approach is that we rely a lot on the garbage collector in order to remove intermediary objects, especially when we chain Withers:

```
person.withName(“John”).withAge(50)
```


Those objects are not used in the end and we will need to wait for the garbage collector to remove them. This can impact performance in systems with high object creation rates.

Records
-------

Finally, the language itself, since Java 16, provides a struct definition called [Records](https://docs.oracle.com/en/java/javase/16/language/records.html "Records"), which is focused on immutability, mainly to store data values, reduce boilerplate code, and increase readability. With Records, we can be sure our objects are immutable as they don't provide mutators, only accessors, and fields are final.

So in our case, our Person class could be defined as:

```
record Person(String name, int age) {}

...
Person person = new Person("Pedro", 66);
```


This would end up in the same code for Person as we had at the beginning of this article, removing the setters and making all fields final.

Some creational issues are not solved out of the box with Records, like the mandatory/optional fields and the constructor, and it's not easy to create new objects based on existing ones, but we can rely on libraries like [RecordBuilder](https://github.com/randgalt/record-builder "RecordBuilder") to help us with that.

```
// Builder
@RecordBuilder
public record Person(String name, int age){}

Person person = PersonBuilder.builder().name("Jose").age(89).build();

// Wither
@RecordBuilder
public record Car(String brand, String model, int year) implements CarBuilder.With{}

Car car = new Car("Seat", "Ibiza", 2015);
Car car2 = car.withModel("Cordoba");
```


Despite these issues, Records are a great solution for representing data with immutable state, while also reducing the boilerplate code in order to define the structures.

Conclusions
-----------

**Immutability** is a concept that will [provide many benefits](https://supakon-k.medium.com/the-advantages-of-using-immutable-objects-in-java-e32f6d326738 "provide many benefits") to our code, like predictability, easy testing, thread safety, and others that will impact our code's intentionality, consistency, adaptability, and responsibility.

In order to achieve immutability, we have different options like Builders, Withers, or the use of Record type, but ultimately, the choice between Builders and Withers depends on the specific requirements of your application and the design principles you want to follow.

Builders are often preferred for complex object creation with many optional parameters, while withers can be more suitable for modifying existing immutable objects. If you are on Java 16 or above, consider that the use of Records is recommended over ordinary classes as they are immutable per definition.

Remember that [SonarLint](https://www.sonarsource.com/products/sonarlint/ "SonarLint"), [SonarQube](https://www.sonarsource.com/products/sonarqube/ "SonarQube"), and [SonarCloud](https://www.sonarsource.com/products/sonarcloud/ "SonarCloud") with their Java analyzer will help you deliver clean code with a long [list of rules](https://rules.sonarsource.com/java "list of rules") to consider when you code.
