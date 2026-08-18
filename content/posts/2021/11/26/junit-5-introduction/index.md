---
title: "JUnit 5 Introduction"
date: "2021-11-26T15:22:59+00:00"
lastmod: "2021-12-10T12:31:37+00:00"
description: "Many of us have been used to JUnit 4 as a formidable unit testing framework, here is an introduction to JUnit 5, with references to JUnit 4."
canonical: "https://cguntur.me/2019/07/07/using-junit5-part-1/"
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2019/07/dukejunit5.png"
categories:
  - "Testing"
related_posts:
  - "book-review-seriously-good-software"
  - "java-testing-with-vs-code"
  - "generating-code-with-intellij-idea"
frozen: false
---

Code Katas are a great way of teaching programming practices. The effectiveness of a code kata is to "solve" something repeatedly in order to gain a "muscle memory" of sorts on the subject matter.

Nothing stresses repeatability more than unit tests. Code Katas thus, in many cases can be associated with or run via unit tests.

Many of us have been long used to JUnit 4 as a formidable unit testing framework. This article is not going to be a comparison between JUnit 4 and JUnit 5, but you will notice some differences as italicized text.

Let us explore JUnit 5 as it was used for a recent code kata, that is, this is how I learnt using JUnit 5 !  
![JUnit5 Logo](https://cgunturme.files.wordpress.com/2019/07/dukejunit5.png "JUnit5")

## JUnit 5 dependencies

JUnit 5 can be added as a single maven dependency:

```
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit5.version}</version>
            </dependency>
```

The equivalent gradle dependency can be inferred.

## What is JUnit 5 and What is Jupiter?

JUnit5 is made of three separate parts:

1. **JUnit5 Platform**: Provides a TestEngine and a testing platform for the JVM.
2. **JUnit5 Jupiter**: Programming and extension model for JUnit5 tests.
3. **JUnit Legacy**: Backward compatibility TestEngine for JUnit 3 and 4.

Read more about this at the JUnit5 User Guide (<https://junit.org/junit5/docs/current/user-guide/>).

## JUnit5 Basics

Base package for JUnit 5 is: `org.junit.jupiter`. Most unit test annotations are located at: `org.junit.jupiter.api`package (in the **junit-jupiter-api** module). Methods in JUnit5 Test can be typically grouped into :

1. **Test methods**: Methods that are run as unit tests.
2. **Lifecycle methods**: Methods that are executed as before or after one or more (or all) test methods.

### Basic Annotations

`@Test`: Identifies a method as a test method. Unlike prior versions, this annotation does not have attributes. #TestMethod

`@Disabled`: An annotation to ignore running a method marked as @Test. #TestMethod

`@BeforeEach`: A setup method that is run before execution of each test. #LifecycleMethod

`@BeforeAll`: A **static** setup method run once before execution of all tests. #LifecycleMethod

`@AfterEach`: A teardown method this is run after execution of each test. #LifecycleMethod

`@AfterAll`: A **static** teardown method run once after execution of all tests. #LifecycleMethod

### Other Annotations

`@Tag`: A category or grouping annotation. This is very useful specially when filtering which tests should be run via build tools such as maven. Example in another article in this series.

`@DisplayName`: A string that can represent the class or method in the JUnit exection results instead of the actual name. Example in another article in this series.

`@DisplayNameGeneration`: A class that can generate class and method names based upon conditions. Examples in another article in this series.

**Custom annotations**: It is quite simple to create custom annotations and inherit the behavior.

## JUnit5 Conditional Control of Test Methods

### Operating System Conditions

`@EnabledOnOs`: Enable a test to run on a specific array of one or more operating systems.

`@DisabledOnOs`: Disable a test to run on a specific array of one or more operating systems.

### Java Runtime Environment Conditions

`@EnabledOnJre`: Enable a test to run on a specific array of one or more Java Runtime Environments.

`@DisabledOnJre`: Disable a test to run on a specific array of one or more Java Runtime Environments.

### System Property Conditions

`@EnabledIfSystemProperty`: Enable a test to run if a System Property matches the condition attributes.

`@DisabledIfSystemProperty`: Disable a test to run if a System Property matches the condition attributes.

## Ordering Test method execution

JUnit 5 allows for ordering test method execution. **This causes mixed feelings for me**.
> ***My feelings** : Ordering methods may lead to some developers building out dependent tests where the result of one test is needed for the next to run or pass. Tests should be independent. That said, it is an incredibly useful a feature when used in code katas where the run of tests may have to follow a certain sequence. In the past, I used to solve this by naming my test methods with some numeral-inclusive prefix and sort the results alphabetically. **With great power, comes great responsibility.***

`@TestMethodOrder`: Test methods can be ordered when the Test class is marked with this annotation.

`@Order`: Each test method can then include an Order annotation that includes a numeric attribute.

These were some of the basic covered. The next article in this series will show examples of how we use these features!
