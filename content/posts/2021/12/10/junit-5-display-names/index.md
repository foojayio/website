---
title: "JUnit 5 Display Names"
slug: "junit-5-display-names"
date: "2021-12-10T12:31:03+00:00"
lastmod: "2022-06-01T20:12:43+00:00"
description: "Continue with the JUnit 5 series on how to customize test classes and test method names to produce more meaningful output."
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2019/07/junitnodisplayname.png?w=840"
categories:
  - "Testing"
tags:
related_posts:
  - "junit-5-introduction"
  - "junit-5-testing-basics"
  - "java-testing-with-vs-code"
enlighterjs: true
frozen: false
---

In [Part 1](https://foojay.io/today/junit-5-introduction/) of this series, we looked at several annotations used in JUnit 5. We covered test methods as well as lifecycle methods.

In [Part 2](https://foojay.io/today/junit-5-testing-basics/), we looked at the basics of testing using JUnit 5. We covered test annotations such as marking a test method and asserting. We saw how a test method could be tagged and how assumptions can be used. We finally wrapped up with test execution ordering mechanisms.

Below, we will cover some customization of names for tests. First, a justification of why names should be customized at all.

## Why Customize Names?

When test class with a few test methods is run with JUnit, the output produced lists the name of the class and a status of execution for each method. The name of the class is used as the top level identifier:
![JUnitNoDisplayName](https://cgunturme.files.wordpress.com/2019/07/junitnodisplayname.png?w=840)

As is visible from the image above, a JUnit test was run on a class STestSolution3PeriodsAndDurations. This has four test methods that were tested and they all verify something. All tests passed. However, one really has to peer into the names of all the tests to understand what they executed.

For instance, the second test verifies creation of a Period using fluent methods. This was inferred and hopefully most developers name their test methods to convey meaningful intent to anyone who looks at the result.

Let's compare that to the next image:
![JUnitWithDisplayName](https://cgunturme.files.wordpress.com/2019/07/junitwithdisplayname-1.png?w=840)

Clearly the latter image communicates a lot better about what was tested and what the intent was. The test class is replaced with a meaningful text of what the overall theme for all test methods enclosed was : "**Periods (days, months, years) and Durations (hours, minutes, seconds)**". Also individual test methods had proper space-separated words rather than a camel-cased name.

Let's now look at how we customize the names in JUnit 5.

## Customizing Names in JUnit 5

There are primarily two ways in which JUnit5 allows for customizing names.

1. Using a [@DisplayName](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayName.html) on a test class or a test method.
2. Using a [@DisplayNameGeneration](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayNameGeneration.html) on the test class which accepts an attribute of a [DisplayNameGenerator](https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayNameGenerator.html) class.

DisplayName API: <https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayName.html>  

DisplayNameGeneration API: <https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayNameGeneration.html>  

DisplayNameGenerator API: <https://junit.org/junit5/docs/5.5.0/api/org/junit/jupiter/api/DisplayNameGenerator.html>

### Using a DisplayName annotation

Adding a @DisplayName annotation on a given class or test method can help customize a single class or method name. Let us look at examples.

#### DisplayName on a test class

**Example** : **[@DisplayName](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest3PeriodsAndDurationsTest.java#L35)** (<https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest3PeriodsAndDurationsTest.java#L35>)

```java
/**
* DateTime ranges: Period, Duration tests.
*
* Note: We create a Clock instance in setup() used for some of the tests.
*
* @see Clock
* @see Period
* @see Duration
* @see ChronoUnit
*/
@DisplayNameGeneration(DateTimeKataDisplayNames.class)
@DisplayName("Periods (days, months, years) and Durations (hours, minutes, seconds)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class STest3PeriodsAndDurationsTest {
```

#### DisplayName on a test method

**Example** : **[@DisplayName](https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest2LocalAndZonedDateTimesTest.java#L304)** (<https://github.com/c-guntur/java-katas/blob/baseline/java-datetime/src/solutions/java/none/cvg/datetime/STest2LocalAndZonedDateTimesTest.java#L304>)

```java
    @Test
    @Tag("PASSING")
    @Order(10)
    @DisplayName("verify conversion of UTC date time to Indian Standard Time")
    public void verifyConversionOfUTCDateTimeToIndianStandardTime() {

        ZonedDateTime allDateTimeOhFives =
                ZonedDateTime.of(5, 5, 5, 5, 5, 5, 555, ZoneId.ofOffset("", ZoneOffset.UTC));

        ZoneId gmtPlusOneZoneId = ZoneId.ofOffset("", ZoneOffset.of("+0530"));

        // DONE: Replace the ZonedDateTime.now() below to display the below UTC time in GMT +0530
        //  The ZonedDateTime created in GMT. Fix the calls so a ZonedDateTime
        //  can be created with the offset of GMT +0530. Use an ofInstant from a toInstant.
        //  Check: java.time.ZonedDateTime.ofInstant(java.time.Instant, java.time.ZoneId)
        //  Check: java.time.ZonedDateTime.toInstant()
        ZonedDateTime gmtPlusOneHourTimeForAllFives =
                ZonedDateTime.ofInstant(
                        allDateTimeOhFives.toInstant(),
                        gmtPlusOneZoneId);

        assertEquals(10,
                gmtPlusOneHourTimeForAllFives.getHour(),
                "The hour should be at 10 AM when Zone Offset is GMT +0530");

        assertEquals(35,
                gmtPlusOneHourTimeForAllFives.getMinute(),
                "The minute should be 35 when Zone Offset is GMT +0530");
    }
```

### Using DisplayNameGenerator

Using a generator to modify display names is a two step process.

1. Create a DisplayNameGenerator class.
2. Set DisplayNameGeneration annotation on the Test class.

#### Setting up the DisplayNameGenerator

DisplayNameGenerator is an interface that has three methods with very sensible names that convey theor purpose:

* `generateDisplayNameForClass(Class<?> testClass)` -- This method can be implemented to provide a meaningful display name to the test class.
* `generateDisplayNameForNestedClass(Class<?> nestedClass)` -- This method can be implemented to provide a meaningful display name to a nested class in the test class.
* `generateDisplayNameForMethod(Class<?> testClass, Method testMethod)` -- This method can be implemented to provide a meaningful display name to a test method of a given test class.

#### Usage

DisplayNameGenerator is an interface, but has two out-of-the-box implementations that can be extended/adapted as needed.

1. **DisplayNameGenerator.Standard** -- converts camel case to spaced words.
2. **DisplayNameGenerator.ReplaceUnderscores** -- converts underscores in names as space-separated words.

The example extends the Standard implementation.

**Example** : [**DisplayNameGenerator**](https://github.com/c-guntur/java-katas/blob/baseline/java-handles/src/main/java/none/cvg/handles/HandlesKataDisplayNames.java) (<https://github.com/c-guntur/java-katas/blob/baseline/java-handles/src/main/java/none/cvg/handles/HandlesKataDisplayNames.java>)

```java
package none.cvg.handles;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.isUpperCase;

public class HandlesKataDisplayNames extends DisplayNameGenerator.Standard {
    @Override
    public String <strong>generateDisplayNameForClass(Class<?> aClass)</strong> {
        return super.generateDisplayNameForClass(aClass);
    }

    @Override
    public String <strong>generateDisplayNameForNestedClass(Class<?> aClass)</strong> {
        return super.generateDisplayNameForNestedClass(aClass);
    }

    @Override
    public String <strong>generateDisplayNameForMethod(Class<?> aClass, Method method)</strong> {
        String methodName = method.getName();
        if (methodName.startsWith("reflection")) {
            return "using Reflection";
        }
        if (methodName.startsWith("unsafe")) {
            return "using Unsafe";
        }
        if (methodName.startsWith("methodHandle")) {
            return "using Method Handles";
        }
        if (methodName.startsWith("compareAndSet")) {
            return camelToText(methodName.substring(13));
        }
        if (methodName.startsWith("get")) {
            return camelToText(methodName.substring(3));
        }
        return camelToText(methodName);
    }

    private static String camelToText(String text) {
        StringBuilder builder = new StringBuilder();
        char lastChar = ' ';
        for (char c : text.toCharArray()) {
            char nc = c;

            if (isUpperCase(nc) && !isUpperCase(lastChar)) {
                if (lastChar != ' ' && isLetterOrDigit(lastChar)) {
                    builder.append(" ");
                }
                nc = Character.toLowerCase(c);
            } else if (isDigit(lastChar) && !isDigit(c)) {
                if (lastChar != ' ') {
                    builder.append(" ");
                }
                nc = Character.toLowerCase(c);
            }

            if (lastChar != ' ' || c != ' ') {
                builder.append(nc);
            }
            lastChar = c;
        }
        return builder.toString();
    }
}
```

Once a DisplayNameGenerator is created, the second step is to associate it with a test class. This requires using the @DisplayNameGeneration annotation on the test class.

#### Applying a DisplayNameGenerator

An annotation on a test class is required to avail of the generator logic. This is done by adding a @DisplayNameGeneration annotation on the test class.

**Example** : **[@DisplayNameGeneration](https://github.com/c-guntur/java-katas/blob/baseline/java-handles/src/solutions/java/none/cvg/constructors/SDefaultConstructorInvocationTest.java#L34)** (<https://github.com/c-guntur/java-katas/blob/baseline/java-handles/src/solutions/java/none/cvg/constructors/SDefaultConstructorInvocationTest.java#L34>)

```java
package none.cvg.constructors;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import none.cvg.handles.DemoClass;
import none.cvg.handles.HandlesKataDisplayNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import sun.misc.Unsafe;

import static none.cvg.handles.ErrorMessages.REFLECTION_FAILURE;
import static none.cvg.handles.ErrorMessages.TEST_FAILURE;
import static none.cvg.handles.ErrorMessages.UNSAFE_FAILURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 * DONE:
 *  This test aims at using MethodHandles to invoke a default constructor on a class in order to
 *  create a new instance.
 *  Each solved test shows how this can be achieved with the traditional reflection/unsafe calls.
 *  Each unsolved test provides a few hints that will allow the kata-taker to manually solve
 *  the exercise to achieve the same goal with MethodHandles.
 */
<strong>@DisplayNameGeneration(HandlesKataDisplayNames.class)
</strong>@DisplayName("Invoke DemoClass()")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSolutionDefaultConstructorInvocation {
```

## Summary

In this article, we saw how we can customize test classes and test method names to produce more meaningful output.

The next article will cover how we can filter tests based on tags.
