---
title: "On Reassessing TestNG vs. Junit"
date: "2021-10-19T08:28:48+00:00"
lastmod: "2021-10-19T08:28:49+00:00"
description: "Given that the JUnit ecosystem is much more developed, I think I'll switch to JUnit for new projects and reassess again in a few years."
canonical: "https://blog.frankel.ch/reassessing-testng-junit/"
authors:
  - "nicolas-frankel"
image: "pexels-cottonbro-6832241.jpg"
categories:
  - "Research"
tags:
related_posts:
  - "java-testing-with-vs-code"
  - "towards-continuous-performance-regression-testing"
  - "avoiding-nullpointerexception"
frozen: false
---

In [a recent article on Spring](https://foojay.io/today/annotation-free-spring/), I advised reassessing one's opinion now and then as the IT world changes so fast. What was true a couple of years ago could be dead wrong nowadays, and you probably don't want to base your decisions on outdated data. This week, I'd like to follow my advice.

One of [my first posts](https://blog.frankel.ch/the-unit-test-war-junit-vs-testng) was advocating for TestNG vs. JUnit. In the post, I mentioned several features that JUnit lacked:

* No parameterization
* No grouping
* No test method ordering

Since JUnit 5 has been out for some time already, let's check if it fixed those issues.

## Parameterization

I wrote the initial post in 2008, and I think JUnit was available in version 3 at the time. Let's skip directly to version 4: JUnit did indeed offer parameterization. Here's a snippet from their wiki:

```java
@RunWith(Parameterized.class)                               // 1
public class FibonacciTest {

    private int fInput;                                     // 2
    private int fExpected;                                  // 2

    public FibonacciTest(int input, int expected) {         // 3
        this.fInput = input;
        this.fExpected = expected;
    }

    @Parameters                                             // 4
    public static Collection<Object[]> data() {             // 5
        return Arrays.asList(new Object[][] {
            { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }
        });
    }

    @Test
    public void test() {
        assertEquals(fExpected, Fibonacci.compute(fInput));
    }
}
```

1. Set the runner
2. Define an attribute for each test parameter
3. Define a constructor with parameters for each parameter
4. Annotate the parameters method
5. Implement the parameters method. It must be `static` and return a `Collection`

Here's how you'd achieve the same with TestNG:

```java
public class FibonacciTest {

    @DataProvider                                             // 1
    public Object[][] data() {                                // 2
        return new Object[][] {
            { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }
        };
    }

    @Test(dataProvider = "data")                              // 3
    public void test(String input, String expected) {
        assertEquals(input, expected);
    }
}
```

1. Annotate with `@DataProvider`
2. Must return a `Object[][]`, no need to be `static`
3. `@Test` point to the data providing method - `data`

With version 5, JUnit offers the `@ParamerizedTest` annotation. Parameterized tests expect at least one parameter source, also specified by an annotation. Multiple sources are available:

|     Annotation     |                                   Data                                   |
|--------------------|--------------------------------------------------------------------------|
| `@ValueSource`     | Primitives, `String` and `Class`                                         |
| `@ValueSource`     | A dedicated `enum`                                                       |
| `@MethodSource`    | A specific method. The method needs to be `static` and return a `Stream` |
| `@MethodSource`    | Multi-valued data formatted as CSV                                       |
| `@CsvFileSource`   | External third-party file                                                |
| `@ArgumentsSource` | Dedicated class that implements `ArgumentsProvider`                      |

While TestNG's approach can address all use cases, JUnit 5 multiple configuration capabilities are more custom-tailored.

## Grouping

Again, the initial post mentions that with JUnit 3, one cannot run only a subset of them. JUnit 4 provides two orthogonal ways to group tests. The first one is test suites:

```java
public class A {

  @Test
  public void a() {}

  @Test
  public void a2() {}
}

public class B {

  @Test
  public void b() {}
}

@SuiteClasses( { A.class, B.class })
public class ABSuite {}
```

From that point, you can run `ABSuite` to run both `A` and `B`. For more fine-grained purposes, you can also use *categories*.

```java
public interface Fast {}
public interface Slow {}

public class A {

  @Test
  public void a() {}

  @Category(Slow.class)
  @Test
  public void b() {}
}

@Category({Slow.class, Fast.class})
public class B {

  @Test
  public void c() {}
}
```

Here's how you can run only desired categories with Maven:

```bash
mvn test -Dtest.categories=Fast
```

TestNG is pretty similar to categories (or the other way around):

```java
public class A {

  @Test
  public void a() {}

  @Test(groups = "slow")
  public void b() {}
}

@Test(groups = { "slow", "fast" })
public class B {

  @Test
  public void c() {}
}
```

```bash
mvn test -Dgroups=fast
```

JUnit 5 has redesigned its approach with the `@Tag` annotation. Tags are labels that you annotate your class with. Then you can filter the tags you want to run during test execution:

```java
public class A {

  @Test
  public void a() {}

  @Test
  @Tag("slow")
  public void b() {}
}

@Tag("fast")
@Tag("slow")
public class B {

  @Test
  public void c() {}
}
```

Both frameworks implement similarly running a subset of tests.

## Test method ordering

This point is the most debatable of all because JUnit stems from *unit testing*. In unit testing, tests need to be independent of one another. For this reason, you can run them in parallel.

Unfortunately, you'll probably need to implement some integration testing at one point or another. My go-to example is an e-commerce shop application. we want to test the checkout scenario with the following steps:

1. Users can browse the product catalog
2. They can put products in their cart
3. They can go to the checkout page
4. Finally, they can pay

Without test method ordering, you end up with a gigantic test method. If the test fails, it's impossible to know where it failed at first glance. You need to drill down and hope the log contains the relevant information.

With test method ordering, one can implement one method per step and order them accordingly. When any method fails, you know in which step it did - provided you gave the method a relevant name.

For this reason, JUnit was not suited for integration testing. It would not stand to reason to have JUnit for unit testing and TestNG for integration testing in a single project. Given that TestNG could do everything JUnit could, that's the most important reason why I favoured the former over the latter.

TestNG implements ordering by enforcing dependencies between test methods. It computes a directed acyclic graph of the dependent methods at runtime and runs the methods accordingly. Here's a sample relevant to the e-commerce above:

```java
public class CheckoutIT {

    @Test
    public void browseCatalog() {}

    @Test(dependsOnMethods = { "browseCatalog" })
    public void addProduct() {}

    @Test(dependsOnMethods = { "addProduct" })
    public void checkout() {}

    @Test(dependsOnMethods = { "checkout" })
    public void pay() {}
}
```

JUnit 5 provides a couple of ways to implement test method ordering:

|    Annotation     |                                 Order                                 |
|-------------------|-----------------------------------------------------------------------|
| `Random`          | No order                                                              |
| `MethodName`      | Alphanumerical method name                                            |
| `DisplayName`     | Alphanumerical display name set by `@DisplayName` on each test method |
| `OrderAnnotation` | Order set by `@Order` on each test method                             |

One can implement the same e-commerce testing scenario like the following:

```java
@TestMethodOrder(OrderAnnotation.class)              // 1
public class CheckoutIT {

    @Test
    @Order(1)                                        // 2
    public void browseCatalog() {}

    @Test
    @Order(2)                                        // 2
    public void addProduct() {}

    @Test
    @Order(3)                                        // 2
    public void checkout() {}

    @Test
    @Order(4)                                        // 2
    public void pay() {}
}
```

1. Define the order based on `@Order`
2. Set the order for each method

TestNG implements ordering via a , JUnit directly. TestNG's approach is more flexible as it allows the runtime to run some methods in parallel, but JUnit gets the job done.

## Conclusion

Up until now, I've favoured TestNG because of the poor parameterization design and, more importantly, the complete lack of ordering in JUnit. Version 5 of JUnit fixes both issues. Even more so, its implementation offers multiple configuration capabilities.

There are still areas where TestNG shines:

* It has a richer lifecycle
* For integration testing, its dependency-between-tests capabilities are a huge asset
* Also, I don't particularly appreciate annotating my methods with `static` for JUnit's parameterized tests.

Yet, given that the JUnit ecosystem is much more developed, I think I'll switch to JUnit for new projects and reassess again in a few years.

**To go further:**

* [The unit test war: JUnit vs TestNG](https://blog.frankel.ch/the-unit-test-war-junit-vs-testng/)
* [A Quick JUnit vs TestNG Comparison](https://www.baeldung.com/junit-vs-testng)
* [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide)
* [TestNG documentation](https://testng.org/doc/documentation-main.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/reassessing-testng-junit/) on September 19^th^, 2021*

*[DAG]: Directed Acyclic Graph
