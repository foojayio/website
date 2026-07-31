---
title: "Equals and Hashcode Implementation Considerations"
slug: "equals-and-hashcode-implementation-considerations"
date: "2021-11-21T11:55:39+00:00"
lastmod: "2021-11-21T11:55:41+00:00"
description: "I always struggled with how to implement equals and hashcode, until I learned about the difference between entities and value objects!"
canonical: "https://www.wimdeblauwe.com/blog/2021/04/26/equals-and-hashcode-implementation-considerations/"
authors:
  - "wim-deblauwe"
image: "Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "for-the-record"
  - "generating-code-with-intellij-idea"
enlighterjs: true
frozen: false
---

I always struggled with how to implement equals and hashcode, until I learned about the difference between entities and value objects.

### Why implement equals and hashcode? {#_why_implement_equals_and_hashcode}

All classes in Java inherit from `java.lang.Object`.

The [equals()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#equals(java.lang.Object)) and [hashCode()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#hashCode()) methods are two important methods that you usually should override when defining your own classes.

`equals()` is important for comparing 2 objects to check if they represent *the same thing*.

We will see in a bit what that means exactly for different types of objects.

`hashCode()` is important if you put your object in a `HashSet` or a `HashMap`. It facilitates the [hashing](https://www.educative.io/edpresso/what-is-hashing) that is used by those data structures.

### Entity vs Value Object {#_entity_vs_value_object}

Even if you don't know [Domain Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design), you might have heared about entities and value objects.  

If you have not, here is a small recap about their differences:

* **Entity** : An object that has a distinct identity within the application domain. For instance, a `User` or an `Invoice`.
* **Value Object** : Objects that only matter because of the value they represent. For instance, a `Money` or `Temperature` object. Usually, these objects are immutable.

### Equals and hashcode for value objects {#_equals_and_hashcode_for_value_objects}

Let's imagine a fairly simple value object that represents temperature.

It has a value and a unit and the code could look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class Temperature {
    private final double value;
    private final Unit unit;

    public Temperature(double value,
                       Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public Unit getUnit() {
        return unit;
    }

    enum Unit {
        KELVIN, CELCIUS, FAHRENHEIT;
    }
}</pre>

For value objects, we want to state that objects are equal when *all* of their properties are equal.

The implementation should be this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class Temperature {

    ...

    @Override
    public boolean equals(Object o) {
        if (this == o) { //
            return true;
        }
        if (o == null || getClass() != o.getClass()) { //
            return false;
        }
        Temperature that = (Temperature) o; //
        return Double.compare(that.value, value) == 0 &amp;amp;&amp;amp; unit == that.unit; //
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit); //
    }

    ...
}</pre>

Short-circuit if the passed in object is the same reference (in memory) as the current object. An object can never be equal to \`null\` and it cannot be equal to an object of another class. We can safely cast the passed in object as we are sure it is of the same class as this object. Compare each of the properties of the passed in object with the current object Use the JDK \`Objects.hash()\` method to generate a hash code using all of the properties of the current object.

We can validate now that 2 `Temperature` objects with the same properties are equal:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Test
void testEqualTemperature() {
    Temperature temperature1 = new Temperature(37.0, Temperature.Unit.CELCIUS);
    Temperature temperature2 = new Temperature(37.0, Temperature.Unit.CELCIUS);

    boolean equal = temperature1.equals(temperature2);
    assertTrue(equal);
}</pre>

I explictly called the `equals()` method here in the test, but this is not how you would normally do this.  

Either you would use the `assertEquals()` method of JUnit, or the `assertThat(..).isEqualTo(..)` method of AssertJ, both of which will call `equals()` internally in the end.

We can test our `hashCode()` implementation like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Test
void testHashCodeForEqualObjects() {
    Temperature temperature1 = new Temperature(37.0, Temperature.Unit.CELCIUS);
    Temperature temperature2 = new Temperature(37.0, Temperature.Unit.CELCIUS);

    int hashCode1 = temperature1.hashCode();
    int hashCode2 = temperature2.hashCode();

    assertThat(hashCode1).isEqualTo(hashCode2);
}</pre>

We test that equal objects should give equal hash codes.

Note that the opposite does not need to be true.

Different objects (as determined by the `equals()` implementation) can return the same hashcode, this is not a problem at all.

### Equals and hashcode for entities {#_equals_and_hashcode_for_entities}

For an entity, all that really matters is the identifier.

We want to see 2 instances that have the same identifier as *the same thing*, even if other properties are different.

Suppose this simple `User` entity:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    protected User() {
    }

    public User(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}</pre>

Since we only care about the `id` field, a naive implementation would look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Don't do this for your entities!

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }</pre>

Unfortunately, this is wrong.

The problem is that the `id` field is generated by the database and only filled in *after* the object is persisted.

So for the same object, the `id` is initially `null` and then gets a certain value after it is stored in the database.

Luckily, Vlad Mihalcea shows us [how to implement this correctly](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/):

<pre class="EnlighterJSRAW" data-enlighter-language="java">    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id != null &amp;amp;&amp;amp;
                id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }</pre>

2 important notes:

* We will only *see* instances of `User` as equal if the `id` is filled in. 2 `User` instances that both have not been stored in the database will never be equal.
* Hashode uses a hardcoded value, because it is not allowed that a hashCode value changes between the time the object is created and the time it is persisted in the database.

See [How to implement equals and hashCode using the JPA entity identifier (Primary Key)](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier) for more in-depth details on this.

### Equals and hashcode for entities using early primary key generation {#_equals_and_hashcode_for_entities_using_early_primary_key_generation}

If you don't like the way we need to implement `equals()` and `hashCode()` for JPA entities, then there is a different route you can take.

When you generate the primary key before you create the object, there are 2 advantages:

1. The `id` can be made required in the constructor so you can't create "invalid" objects.
2. The equals() and hashCode() methods can be simplified to just take the `id` into account.

In code, we can imagine this entity:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book {
    @Id
    private Long id;

    private String name;

    protected Book() {
    }

    public Book(Long id,
                String name) {
        Assert.notNull(id, "id should not be null");
        Assert.notNull(name, "name should ot be null");
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}</pre>

The `Book` entity does not have the `@GeneratedValue` annotation, so we will need to pass in a value at construction time.

Now that we know the `id` field is never `null`, we can use this implementation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }</pre>

We just use `id` for `equals()`, and we can relay on `id` as well for `hashCode()`

|------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Note | If you like to use early primary key generation, then check out my open-source library [JPearl](https://github.com/wimdeblauwe/jpearl). It has base classes and a Maven plugin that makes [the implementation of this a breeze](https://github.com/wimdeblauwe/jpearl#usage). |

A test on equals could look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">    @Test
    void testEquals() {
        Book book1 = new Book(1L, "Taming Thymeleaf");
        Book book2 = new Book(1L, "Taming Thymeleaf");

        assertThat(book1).isEqualTo(book2);
    }</pre>

Since we only test the id, this test will also succeed:

<pre class="EnlighterJSRAW" data-enlighter-language="java">    @Test
    void testEquals() {
        Book book1 = new Book(1L, "Taming Thymeleaf");
        Book book2 = new Book(1L, "Totally different title");

        assertThat(book1).isEqualTo(book2);
    }</pre>

This might be counter-intuative at first, but this is really what you want.

Entities are defined by their id, when the id is the same, we are talking about *the same thing*.

### Testing equals and hashCode implementations {#_testing_equals_and_hashcode_implementations}

The tests that I have shown here only scratch the surface of all the things that you need to test to fully implement the `equals()` and `hashCode` contracts.

To ensure your methods are correctly implemented, use [EqualsVerifier](https://jqno.nl/equalsverifier/).

Add it to your `pom.xml`:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">    nl.jqno.equalsverifier
    equalsverifier
    3.6
    test</pre>

And write the test:

<pre class="EnlighterJSRAW" data-enlighter-language="java">    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Temperature.class).verify();
    }</pre>

This will test if `equals()` is reflexive, symmetric, transitive and consistent. It also tests if `hashCode()` adheres to the contract defined in the `java.lang.Object` API.

|------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Note | When writing the blog entry, the test pointed out equals of `Temperature` was not `final` (See <https://jqno.nl/equalsverifier/errormessages/subclass-equals-is-not-final/>). The best fix was to make the whole class final as the class was not intended to be subclassed anyway. So verifying your implementation is certainly worth it. |

### Conclusion {#_conclusion}

To correctly implement the `equals()` and `hashCode()`, it is important to first determine if your object is a value object or an entity.

If it is one of the those, you can follow the rules set forth in this article. If it is neither (e.g., a `Controller`, `Service`, `Repository`​) then you probably don't want to override the methods.
