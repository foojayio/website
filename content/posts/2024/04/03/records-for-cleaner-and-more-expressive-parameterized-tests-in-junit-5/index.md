---
title: "Records for Cleaner and More Expressive Parameterized Tests in JUnit 5"
slug: "records-for-cleaner-and-more-expressive-parameterized-tests-in-junit-5"
date: "2024-04-03T09:00:38+00:00"
lastmod: "2024-04-03T09:00:39+00:00"
description: "Discover how Java records enhance parameterized testing in JUnit 5. Learn to create cleaner, more expressive, and type-safe tests."
authors:
  - "bazlur-rahman"
image: "Gemini_Generated_Image-2.jpeg"
categories:
  - "Java"
tags:
related_posts:
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
enlighterjs: true
frozen: false
---

### **Introduction**: {#h3-0-introduction}

Parameterized testing in JUnit 5 is a potent technique for executing the same test logic with various inputs. While you can use a variety of data structures, such as custom classes, arrays, or collections, Java records offer a compelling advantage in readability, type safety, and expressiveness.

Let's examine how to leverage Java records for parameterized tests through a concrete example -- testing an expression evaluator.

### The Expression Evaluator Scenario {#h3-1-the-expression-evaluator-scenario}

Consider a simple expression evaluator capable of handling the following:

* Constants (integer values)
* Basic arithmetic operations: addition, subtraction, multiplication, and division.

Here's a possible implementation using records:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public sealed interface Expression {

   record Constant(int value) implements Expression { }

   record Addition(Expression left, Expression right) implements Expression {}

   record Subtraction(Expression left, Expression right) implements Expression { }

   record Multiplication(Expression left, Expression right) implements Expression { }

   record Division(Expression left, Expression right) implements Expression { }
}
</pre>

And the expression evaluator:  

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class ExpressionEvaluator {
   public int evaluate(Expression expression) {
       return switch (expression) {
           case Constant(int value) -&gt; value;
           case Addition(var left, var right) -&gt; evaluate(left) + evaluate(right);
           case Subtraction(var left, var right) -&gt; evaluate(left) - evaluate(right);
           case Multiplication(var left, var right) -&gt; evaluate(left) * evaluate(right);
           case Division(var left, var right) -&gt; {
               if (evaluate(right) == 0) {
                   throw new ArithmeticException("Division by zero");
               }
               yield evaluate(left) / evaluate(right);
           }
       };
   }
}</pre>

### **Traditional Parameterized Test (with ArgumentsSource)** {#h3-2-traditional-parameterized-test-with-argumentssource}

Let's first look at how a parameterized test for this evaluator might be structured using the traditional [**ArgumentsSource**](https://junit.org/junit5/docs/5.10.2/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/ArgumentsSource.html) method. This will give us a baseline to understand how records can streamline our tests.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static bazlur.ca.Expression.*;
import static bazlur.ca.ExpressionEvaluatorTest.TestOutcome.Failure;
import static bazlur.ca.ExpressionEvaluatorTest.TestOutcome.Success;

class ExpressionEvaluatorTest {
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    static Stream&lt;Arguments&gt; testCasesForEvaluate() {
        return Stream.of(
                Arguments.of("Evaluating a constant", new Constant(5), 5),
                Arguments.of("Adding two constants", new Addition(new Constant(3), new Constant(2)), 5),
                Arguments.of("Subtracting two constants", new Subtraction(new Constant(10), new Constant(4)), 6),
                Arguments.of("Multiplying by zero", new Multiplication(new Constant(6), new Constant(0)), 0),
                Arguments.of("Dividing by a non-zero constant", new Division(new Constant(8), new Constant(2)), 4),
                Arguments.of("Dividing by zero - Expecting ArithmeticException", new Division(new Constant(5), new Constant(0)), ArithmeticException.class)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("testCasesForEvaluate")
    void testEvaluate(String description, Expression expression, Object expectedOutcome) { // Changes here
        if (expectedOutcome instanceof Class&lt;?&gt;) {
            Class&lt;? extends Exception&gt; expectedException = (Class&lt;? extends Exception&gt;) expectedOutcome;
            Assertions.assertThrows(expectedException, () -&gt; evaluator.evaluate(expression));
        } else {
            int expectedResult = (int) expectedOutcome;
            Assertions.assertEquals(expectedResult, evaluator.evaluate(expression));
        }
    }
}</pre>

### **Refactoring with Records** {#h3-3-refactoring-with-records}

Now, let's see how we can improve the clarity and maintainability of our tests by introducing records.

**The TestCase Record:**

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">record TestCase(String description, Expression expression, TestOutcome expectedOutcome) {}</pre>

**Sealed TestOutcome:**

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sealed interface TestOutcome {
    record Success(int value) implements TestOutcome {}
    record Failure(Supplier&lt;? extends RuntimeException&gt; exceptionSupplier) implements TestOutcome {}
}</pre>

<br />

**Refactored Tests**   

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static Stream&lt;TestCase&gt; provideExpressionsForEvaluate() {
    return Stream.of(
            new TestCase("Valid constant", new Constant(5), new Success(5)),
            new TestCase("Valid addition", new Addition(new Constant(2), new Constant(3)), new Success(5)),
            new TestCase("Division by zero", new Division(new Constant(6), new Constant(0)), new Failure(ArithmeticException::new)),
            new TestCase("Valid subtraction", new Subtraction(new Constant(10), new Constant(3)), new Success(7)),
            new TestCase("Valid multiplication", new Multiplication(new Constant(3), new Constant(4)), new Success(12)),
            new TestCase("Negative result", new Subtraction(new Constant(5), new Constant(8)), new Success(-3)),
            new TestCase("Combined operations",
                    new Addition(new Constant(3), new Multiplication(new Constant(2), new Constant(5))),
                    new Success(13)),

            new TestCase("Nested division by zero",
                    new Division(new Constant(12), new Division(new Constant(6), new Constant(0))),
                    new Failure(ArithmeticException::new))
    );
}

@ParameterizedTest
@MethodSource("provideExpressionsForEvaluate")
void testEvaluateExpression(TestCase testCase) {
    ExpressionEvaluator evaluator = new ExpressionEvaluator();

    switch (testCase.expectedOutcome()) {
        case Success(int expectedResult) -&gt;
                Assertions.assertEquals(expectedResult, evaluator.evaluate(testCase.expression()),
                        "Incorrect evaluation for: " + testCase.description());
        case Failure(Supplier&lt;? extends RuntimeException&gt; exceptionSupplier) -&gt;
                Assertions.assertThrows(exceptionSupplier.get().getClass(),
                        () -&gt; evaluator.evaluate(testCase.expression()),
                        "Incorrect error handling for: " + testCase.description());
    }
}</pre>

### Benefits of the Record-Based Approach {#h3-4-benefits-of-the-record-based-approach}

Let's break down the key advantages of using records in this context:

* **Clarity and Conciseness** : The **TestCase** record makes the structure of test data immediately apparent. It clearly separates the test description, the input expression, and the expected outcome.
* **Type Safety:** By explicitly typing expressions and outcomes, we reduce the potential for runtime errors caused by mismatched data types.
* **Flexible Outcome Handling:** The **Failure** record allows us to model various error scenarios, including associating a specific exception type with the failure.
* **Pattern Matching:** Records and sealed interfaces make our test case logic more concise through the use of pattern matching or switch expressions

### Conclusion {#h3-5-conclusion}

Records offer a compelling solution for structuring test cases in JUnit 5 parameterized tests.

When compared to traditional approaches, records significantly improve code clarity, type safety, and overall maintainability.
