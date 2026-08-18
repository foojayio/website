---
title: "JUnit 5 Testing Basics"
slug: "junit-5-testing-basics"
date: "2021-12-03T12:29:06+00:00"
lastmod: "2022-06-01T20:13:05+00:00"
description: "Continuing the series on JUnit 5, this artcle shows examples of a JUnit test which makes use of a few of the annotations from Part 1!"
canonical: "https://cguntur.me/2019/07/13/using-junit5-part-2/"
authors:
  - "c-guntur"
image: "Favicon-3-2.png"
categories:
  - "Testing"
tags:
related_posts:
  - "junit-5-introduction"
  - "equals-and-hashcode-implementation-considerations"
  - "choosing-a-cache-1"
enlighterjs: true
frozen: false
---

In [Part 1](https://foojay.io/today/junit-5-introduction/) of this series of articles, we looked at several annotations used in JUnit5. We covered test methods as well as lifecycle methods.

This article will share examples of a JUnit test which makes use of a few of these annotations.

Testing
-------

### Marking a method as a Test

Tests in JUnit5 are annotated with the **@Test** annotation. *Unlike prior versions of JUnit, the JUnit5 `@Test` annotation does not have any attributes. Prior versions supported extensions via attributes, while JUnit5 fosters a custom annotation based extension* (more on this in a future article).

**Example** : **[@Test](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L43)** (<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L43>[)](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest1InstantAndDateInteropTest.java#L43)

```java
    <strong>@Test</strong>
    @Tag("PASSING")
    @Order(1)
    public void verifyInstantAndDateHaveSameEpochMilliseconds() {

        // DONE: Replace the Instant.now() with an instant from classicDate.
        //  Use a Date API that converts Date instances into Instant instances.
        //  Check: java.util.Date.toInstant()
        Instant instant = classicDate.toInstant();

        // DONE: Replace the "null" below to get milliseconds from epoch from the Instant
        //  Use an Instant API which converts it into milliseconds
        //  Check: java.time.Instant.toEpochMilli()
        assertEquals(Long.valueOf(classicDate.getTime()),
                instant.toEpochMilli(),
                "Date and Instant milliseconds should be equal");
    }
```


### Assertions

Assertions are how testing is conducted. Several types of assertions exist for testing. Some examples include:

* `assertTrue` / `assertFalse`
* `assertEquals` / `assertNotEquals` / `assertSame` / `assertNotSame`
* `assertNull` / `assertNotNull`
* `assertArrayEquals` / `assertIterableEquals` / `assertLinesMatch`
* `assertThrows` / `assertNotThrows`
* `assertAll`
* `assertTimeout` / `assertTimeoutPreemptively`
* `fail`

There are several polymorphs for most of the methods listed above. JUnit5 aggregates all such assertions as static methods in a single factory utility aptly named [**Assertions**](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/Assertions.html) (<https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/Assertions.html>).

Assertions are mostly unary or binary (There are assertions, with other [arities](https://en.wikipedia.org/wiki/Arity), that are less commonly used)

**Unary** assertions are usually boolean condition evaluators. `assertTrue` or `assertNull` are good examples of unary assertions. **The expectation in such cases is built into the actual assertion method name.** A second optional parameter for unary assertions is a *message* that is returned in case of an assertion failure and exists to provide more meaningful readable failure details.

**Binary** assertions typically have an ***expected* value (a known), an *actual* value (evaluated)** and an optional third parameter of *message* (in case of the expectation not being met). `assertEquals` and `assertSame` are good examples of binary assertions.

Typically, unit tests statically import the assertions required for the given tests in a test class. An example of such an assertion is the `assertEquals` method as shown below.

**Example** : **[Assertion](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L56)** (<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L56>)

```java
    @Test
    @Tag("PASSING")
    @Order(1)
    public void verifyInstantAndDateHaveSameEpochMilliseconds() {

        // DONE: Replace the Instant.now() with an instant from classicDate.
        //  Use a Date API that converts Date instances into Instant instances.
        //  Check: java.util.Date.toInstant()
        Instant instant = classicDate.toInstant();

        // DONE: Replace the "null" below to get milliseconds from epoch from the Instant
        //  Use an Instant API which converts it into milliseconds
        //  Check: java.time.Instant.toEpochMilli()
        <strong>assertEquals(Long.valueOf(classicDate.getTime()),
                instant.toEpochMilli(),
                "Date and Instant milliseconds should be equal");</strong>
    }
```


**See also** : **[Static Import](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest1InstantAndDateInteropTest.java#L18)** ([](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest1InstantAndDateInteropTest.java#L18)<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L18>)

```java
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static none.cvg.datetime.LenientAssert.assertAlmostEquals;
<strong>import static org.junit.jupiter.api.Assertions.assertEquals;</strong>
import static org.junit.jupiter.api.Assertions.assertTrue;
```


> ***NOTE**: Assertion parameter ordering in JUnit 5 is different from the order in prior versions. In my opinion, the current parameter arrangement makes a lot more sense.*

Filtering and Categorizing Tests
--------------------------------

### Tags

Tags are a means to categorize test methods and classes. Tagging also leads to discovery and filtering of tests. Tagging is done by annotating the class or method with an [**@Tag**](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/Tag.html) annotation. More on filtering in another blog of this series.

**Example: [@Tag](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L62)** ([](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L62)<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L62>)

```java
    @Test
    <strong>@Tag("PASSING")</strong>
    @Order(1)
    public void verifyInstantAndDateHaveSameEpochMilliseconds() {

        // DONE: Replace the Instant.now() with an instant from classicDate.
        //  Use a Date API that converts Date instances into Instant instances.
        //  Check: java.util.Date.toInstant()
        Instant instant = classicDate.toInstant();

        // DONE: Replace the "null" below to get milliseconds from epoch from the Instant
        //  Use an Instant API which converts it into milliseconds
        //  Check: java.time.Instant.toEpochMilli()
        assertEquals(Long.valueOf(classicDate.getTime()),
                instant.toEpochMilli(),
                "Date and Instant milliseconds should be equal");
    }
```


### Assumptions

Assumptions are conditions that determine if the rest of the test code block should be **either evaluated or aborted**. Not meeting an assumption will not cause the code block conditioned by it, to fail. It would rather simply abort execution of such a code block. Some assumption methods:

* assumeTrue / assumeFalse
* assumeThat

Similar to assertions, assumptions are grouped as static methods, in a factory utility class, [**Assumptions**](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/Assumptions.html) (<https://junit.org/junit5/docs/5.5.0/user-guide/#writing-tests-assumptions>).

```java
class TestWithAssumptions {

    @Test
    void testOnlyOnHost123() {
        <strong>assumeTrue("host123".equals(System.getenv("HOSTNAME")));</strong>
        // remainder of test
    }

    @Test
    void testOnHost123OrAbortWithMessage() {
        <strong>assumeTrue("host123".equals(System.getenv("HOSTNAME")),
            () -> "Aborting test: not on host123");</strong>
        // remainder of test
    }

}
```


Typically, unit tests statically import the assumptions required for the given tests in a test class.

***There is no current example of an assumption in the code kata.***

Ordering Tests
--------------

### Test execution order

As stated in the previous part of the blog series, I reserve my opinions of ordering the sequence of test executions. It is useful in certain cases, such as code katas. Test ordering requires either one or two steps depending on the type of ordering.

***NOTE**: If no order is specified for a test class, JUnit 5 looks for instructions from parent class hierarchy and if still none found, will order tests in a deterministic but non-obvious manner.*

#### Instructing JUnit to order tests

An annotation on the test class is needed to instruct JUnit5 to order tests. This annotation is called the **[@TestMethodOrder](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/TestMethodOrder.html)** . This annotation accepts an attribute of type [**MethodOrderer**](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/MethodOrderer.html). There are three default implementations that exist:

1. [**Alphanumeric**](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/MethodOrderer.Alphanumeric.html) -- uses `String::compareTo` to order execution of test methods
2. **[OrderAnnotation](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/MethodOrderer.OrderAnnotation.html)** -- uses the `@Order` annotation on each test method to determine order. The Order annotation accepts a int attribute that specifies the ranking.
3. **[Random](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/MethodOrderer.Random.html)** -- uses a random order *either* simply from `System.nanoTime()` or in combination with a custom seed.

More custom orders can be created by implementing the `MethodOrderer` interface.

**Example** : **[@TestMethodOrder](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L31)** ([](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest1InstantAndDateInteropTest.java#L31)<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L31>)

```java
/**
 * The tests in this class aim to show interoperability between
 * `java.util.Date` and the newer `java.time.Instant`.
 *
 * @see Instant
 * @see Date
 * @see LenientAssert
 */
@DisplayNameGeneration(DateTimeKataDisplayNames.class)
@DisplayName("Instant And Date Interoperability")
<strong>@TestMethodOrder(MethodOrderer.OrderAnnotation.class)</strong>
public class STest1InstantAndDateInteropTest {
```


#### Extra Step (For OrderAnnotation only): Adding an Order via annotations

In addition to the above annotation instructing JUnit to order test methods, **an additional annotation is needed if the `OrderAnnotation` orderer is specified** . The **[@Order](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/Order.html)** annotation accepts an integer that specifies the ascending order of execution.

**Example** : **[@Order](https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L45)** (<https://github.com/c-guntur/java-katas/blob/main/java-datetime/src/test/java/none/cvg/datetime/TestKata1InstantAndDateInterop.java#L45>)

```java
    @Test
    @Tag("PASSING")
    <strong>@Order(1)</strong>
    public void verifyInstantAndDateHaveSameEpochMilliseconds() {

        // DONE: Replace the Instant.now() with an instant from classicDate.
        //  Use a Date API that converts Date instances into Instant instances.
        //  Check: java.util.Date.toInstant()
        Instant instant = classicDate.toInstant();

        // DONE: Replace the "null" below to get milliseconds from epoch from the Instant
        //  Use an Instant API which converts it into milliseconds
        //  Check: java.time.Instant.toEpochMilli()
        assertEquals(Long.valueOf(classicDate.getTime()),
                instant.toEpochMilli(),
                "Date and Instant milliseconds should be equal");
    }
```


That's a wrap of part two of this series on Junit 5.

The next article will include customizing tests with DisplayNames and writing a custom DisplayNameGeneration.

Happy coding!
