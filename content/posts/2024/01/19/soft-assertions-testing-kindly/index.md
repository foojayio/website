---
title: "Soft Assertions - testing kindly"
slug: "soft-assertions-testing-kindly"
date: "2024-01-19T10:17:45+00:00"
lastmod: "2024-01-20T10:51:23+00:00"
description: "Using the concept of soft-assertions makes testing the impact of our changes on a single object a lot more convenient, and reduces the potential need for reruns."
authors:
  - "simon-verhoeven"
image: "chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Testing"
  - "Tools"
tags:
related_posts:
  - "archunit-testing-your-architecture"
  - "load-testing-shoot-your-application-with-gatling"
  - "pitest-do-you-test-your-tests"
  - "am-i-testing-the-right-way"
enlighterjs: true
frozen: false
---

Given it's the advent of a new year, I thought let's start with something nice and fun that can make live more enjoyable for everyone.

As developers, we write testcases all the time to help us in our development process, and to help us detect potential regressions in the future.

Now ideally we want our testcases to be as efficient as possible, and ideally to require as few reruns as possible. And this is where Soft Assertions come into play.

Hard Assertions vs Soft Assertions {#_hard_assertions_vs_soft_assertions}
-------------------------------------------------------------------------

### "Hard" assertion {#_hard_assertion}

These are the traditional assertions that everyone's used to seeing, we assert field by field, and halt as soon as one assertion fails. Hence, "hard" assertions.

For example

```
assertEquals(person1.getName(), personRecord1.name());
assertEquals(person1.getMainLanguage(), personRecord1.mainLanguage());
assertEquals(person1.getEmail(), personRecord1.email());
assertEquals(person1.getAddress(), personRecord1.address());
assertEquals(person1.getPhoneNumber(), personRecord1.phoneNumber());
assertEquals(person1.getDateOfBirth().toString(), personRecord1.dateOfBirth());
```


If, for example, our e-mail assertion were to fail we would receive no information on the validity of the address, phone number or date of birth.

Or even earlier, if our main language was wrong we'd just receive something like:

```
org.opentest4j.AssertionFailedError:
Expected :French
Actual   :English
```


### "Soft" assertion {#_soft_assertion}

With soft assertions we bundle our assertions, run them internally and then output all possible failures.  

All our assertions will be executed.

You can set up something for this yourself, or certain libraries also handle this for you as we'll cover later.

### So when should we use them? {#_so_when_should_we_use_them}

Ideally when you're performing more than one assertion on the same object.  

I dislike flaky tests, and having to run the same test multiple times just to figure out what's wrong with for example the mapping of a single object.

Library support {#_library_support}
-----------------------------------

Some of the most commonly used Assertion libraries have out of the box support for soft assertions, sadly, not all of them do.

|-----------|-----------|
| Framework | Supported |
| JUnit     | \[x\]     |
| AssertJ   | \[x\]     |
| Hamcrest  | \[ \]     |
| TestNG    | \[x\]     |
| Truth     | \[ \]     |

As of the moment of writing Truth does have an open pull request for this, but it's been in that state for a couple of years.

|-----|------------------------------------------------------------------------------------------------------------------------|
| Tip | In case you know of another library that supports soft assertions, please do let me know and I'll add it to this list. |

|-----|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Tip | To focus more on the functionality itself the testcase is very basic. We're testing the mapper, and mapping 2 objects. Let's say we're a smidge sleep-deprived, and just copied the first assertions rather than extracting them to a method, and forgot to replace some references. With hard assertions we would get a failure on each individual error. |

### Junit 5 {#_junit_5}

JUnit makes it easy for us, we merely need to statically import `assertAll`

```
assertAll("Person 2",
    () -> assertEquals(person2.getName(), personRecord2.name()),
    () -> assertEquals(person2.getMainLanguage(), personRecord1.mainLanguage()),
    () -> assertEquals(person2.getEmail(), personRecord2.email()),
    () -> assertEquals(person2.getAddress(), personRecord2.address()),
    () -> assertEquals(person2.getPhoneNumber(), personRecord1.phoneNumber()),
    () -> assertEquals(person2.getDateOfBirth().toString(), personRecord1.dateOfBirth())
);
```


And then we get a clear output:

```
org.opentest4j.MultipleFailuresError: Person 2 (3 failures)
	org.opentest4j.AssertionFailedError: expected:  but was:
	org.opentest4j.AssertionFailedError: expected:  but was:
	org.opentest4j.AssertionFailedError: expected:  but was:

	at org.junit.jupiter.api.AssertAll.assertAll(AssertAll.java:80)
	at org.junit.jupiter.api.AssertAll.assertAll(AssertAll.java:44)
	at org.junit.jupiter.api.Assertions.assertAll(Assertions.java:2961)
	at dev.simonverhoeven.softassertions.JUnitSoftTest.softAssert(JUnitSoftTest.java:48)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	Suppressed: org.opentest4j.AssertionFailedError: expected:  but was:
		...
	Suppressed: org.opentest4j.AssertionFailedError: expected:  but was:
		...
	Suppressed: org.opentest4j.AssertionFailedError: expected:  but was:
		...
```


### AssertJ {#_assertj}

AssertJ has a couple different soft assertion methods as can be seen in [their documentation](https://assertj.github.io/doc/#assertj-core-soft-assertions).  

We can write our own soft assertions, make use of an injected `SofAssertions` parameter, ...​ to tell AssertJ to aggregate the errors.  

But let's keep it easy, and clear and make use of the static `assertSoftly` method.

```
assertSoftly(softAssertions -> {
    softAssertions.assertThat(personRecord2.name()).isEqualTo(person2.getName());
    softAssertions.assertThat(personRecord1.mainLanguage()).isEqualTo(person2.getMainLanguage());
    softAssertions.assertThat(personRecord2.email()).isEqualTo(person2.getEmail());
    softAssertions.assertThat(personRecord2.address()).isEqualTo(person2.getAddress());
    softAssertions.assertThat(personRecord1.phoneNumber()).isEqualTo(person2.getPhoneNumber());
    softAssertions.assertThat(personRecord1.dateOfBirth()).isEqualTo(person2.getDateOfBirth().toString());
});
```


Rather than immediately failing on the second assertion, we'll get an error on the 2nd, 5th and 6th assertion.

```
org.assertj.core.error.AssertJMultipleFailuresError:
Multiple Failures (3 failures)
-- failure 1 --
expected: "French"
but was: "English"
at AssertJSoftTest.lambda$softAssert$1(AssertJSoftTest.java:50)
-- failure 2 --
expected: "555-5678"
but was: "555-1234"
at AssertJSoftTest.lambda$softAssert$1(AssertJSoftTest.java:53)
-- failure 3 --
expected: "1982-04-01"
but was: "1980-12-01"
at AssertJSoftTest.lambda$softAssert$1(AssertJSoftTest.java:54)
```


### TestNG {#_testng}

We can make use of the `SoftAssert` class to group our assertions, and then verify them as a group by invoking `assertAll`.

```
var personSoftAssert2 = new SoftAssert();
personSoftAssert2.assertEquals(person2.getName(), personRecord2.name());
personSoftAssert2.assertEquals(person2.getMainLanguage(), personRecord1.mainLanguage());
personSoftAssert2.assertEquals(person2.getEmail(), personRecord2.email());
personSoftAssert2.assertEquals(person2.getAddress(), personRecord2.address());
personSoftAssert2.assertEquals(person2.getPhoneNumber(), personRecord1.phoneNumber());
personSoftAssert2.assertEquals(person2.getDateOfBirth().toString(), personRecord1.dateOfBirth());
personSoftAssert2.assertAll();
```


Which results in a very clean

```
java.lang.AssertionError: The following asserts failed:
	expected [English] but found [French],
	expected [555-1234] but found [555-5678],
	expected [1980-12-01] but found [1982-04-01]

	at org.testng.asserts.SoftAssert.assertAll(SoftAssert.java:46)
	at org.testng.asserts.SoftAssert.assertAll(SoftAssert.java:30)
	at dev.simonverhoeven.softassertions.TestNGSoftTest.softAssert(TestNGSoftTest.java:54)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
```


Takeaway {#_takeaway}
---------------------

Using the concept of soft-assertions makes testing the impact of our changes on a single object a lot more convenient, and reduces the potential need for reruns.

You can always brew your own variant for this, but these three libraries have very nice support for the concept.

And please do experiment, for example you can write custom (soft-)assertions in AssertJ by doing:

```
public class CustomPersonSoftAssertion extends AbstractAssert {
    public CustomPersonSoftAssertion(Person person) {
        super(person, CustomPersonSoftAssertion.class);
    }

    CustomPersonSoftAssertion hasName() {
        Assertions.assertThat(actual.getName())
                .describedAs("Name")
                .isNotBlank();
        return this;
    }

    //...

    static CustomPersonSoftAssertion assertThat(Person person) {
        return new CustomPersonSoftAssertion(person);
    }
}

public class CustomAssertJSoftAssertions extends SoftAssertions {

    public CustomPersonSoftAssertion assertThat(Person actual) {
        return proxy(CustomPersonSoftAssertion.class, Person.class, actual);
    }
}
```


References {#_references}
-------------------------

* [The article repository](https://github.com/SimonVerhoeven/soft-assertions) containing the code snippets
* [JUnit](https://junit.org/junit5/)
* [AssertJ](https://github.com/assertj/assertj)
* [Hamcrest](https://github.com/hamcrest/JavaHamcrest)
* [TestNG](https://testng.org/)
* [Truth](https://truth.dev/)
