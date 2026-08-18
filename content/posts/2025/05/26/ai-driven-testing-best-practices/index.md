---
title: "AI-Driven Testing Best Practices"
slug: "ai-driven-testing-best-practices"
date: "2025-05-26T08:06:15+00:00"
lastmod: "2025-05-26T08:12:58+00:00"
description: "AI can generate tests, but the result is not totally reliable. This article covers best practices to incorporate AI into the test generation."
authors:
  - "jonathan-vila"
image: "ai-test-generation.png"
categories:
  - "Java"
  - "Testing"
tags:
related_posts:
  - "increase-readability-and-reduce-complexity-with-javas-pattern-matching"
  - "devops-for-developers-continuous-integration-github-actions-and-sonar-cloud"
  - "foojay-podcast-49"
  - "foojay-podcast-58"
enlighterjs: true
frozen: false
---

**So, AI Can Write Tests Now? Cool, But...**
--------------------------------------------

AI assisted coding tools are everywhere now, helping with autocomplete, suggesting fixes, and sometimes writing surprisingly large blocks of code. A hot topic is using generative AI to generate tests automatically -- unit, integration, e2e, etc.

The idea's definitely appealing. Who wouldn't want an AI to help crank out tests, bump up those coverage numbers, and maybe save us from some of the testing grind? It sounds like a fast track to better feedback and tackling that mountain of untested code.

But, hang on a sec. Like any tool, especially one this complex, artificial intelligence is not a silver bullet. Just grabbing AI driven tests and calling it a day is risky. You might *think* your code's solid because the test count is high, but the tests themselves might be junk.

These AI language models learn from tons of code online and in repos -- and let's face it, a lot of that code isn't exactly high quality nor correct code.

This article is for devs figuring out how to actually *use* these AI tools without creating a mess and produce high quality software. We'll touch on the good stuff but focus on the traps: the tests might be flat-out *wrong*, or they might just "prove" that your buggy code works exactly like the buggy mess it is, instead of checking if it meets user needs.

We'll also bring in static analysis with SonarQube, using its big list of Java test rules \[<https://rules.sonarsource.com/java/tag/tests/>\] to show concrete examples of what can go wrong and what to watch for. The point isn't to ditch AI, but to use it intelligently, so you don't trade real quality for fake coverage.

**How AI Learns to Code (And Why That's a Problem for Tests)**
--------------------------------------------------------------

To understand why AI tests can be uncertain, it helps to know how these code-generating AIs learn. Most are Large Language Models (LLMs) trained on absolutely massive datasets. These datasets contain billions of lines of code from GitHub, Stack Overflow, open-source projects, maybe your own company's code.

The AI digests all this and learns patterns: common code structures, how people usually use certain APIs, popular libraries, coding styles. It gets really good at predicting the next bit of code in a sequence, leading it to write stuff that often *looks* right.

But that's the catch. The training data is just... code. All kinds of code. Including:

* Plain old **bugs**.
* Nasty **security holes**.
* Weird **anti-patterns**.
* **Outdated code** using old libraries, patterns or approaches.
* Code that **ignores** **style guides**.
* Code with **zero useful** **comments**.

The AI doesn't *understand* good code from bad code. It just mimics the patterns it saw. If buggy code patterns were common in its training data, it'll happily reproduce them. It's the classic "garbage in, garbage out" deal.

So when you ask this AI to write tests, problems pop up:

1. **The Tests Have Bugs:** The generated test code itself might be flawed, misuse resources, have race conditions -- just like buggy tests humans write.
2. **The Tests Verify Bugs:** This is the really sneaky one. The AI looks at your *current* code, sees how it works (even the buggy parts), and writes a test to confirm *that* behavior. It doesn't know what the code *should* do from requirements; it just tests what the code *does*.

Think of learning English only by reading internet comments. You'd get good at slang and common mistakes, but you wouldn't be able to write clean technical docs. An AI testing tool trained on a huge, messy pile of code is similar -- good at mimicry, not guaranteed to be correct or follow best practices in software development.

AI powered tests can be inaccurate and may only validate existing code, not the intended behavior. Let's see some of the main problems you can encounter by generating the tests with AI.

**Problem #1: AI Tests Might Just Be Wrong**
--------------------------------------------

Yeah, AI can generate code that uses @Test and compiles. It will save a lot of manual effort on time consuming test case generation. But is it *correct*? Often, it might not be. When you're reviewing AI-generated tests, watch out for:

* **Looks Right, Works Wrong:** AI usually nails the syntax. But code that compiles doesn't mean the test logic is sound or it tests anything useful.
* **Incomplete Tests**: Super common. The AI sets things up, calls the method, and... forgets the important part.
* **No Asserts**: A test without asserts is pointless. AI often forgets to actually check the result.
* **Weak Asserts**: An assertNotNull(result) is better than nothing, but doesn't prove the result is correct. Also assertTrue(true) is useless.
* **Happy Path Only**: AI often tests the simple case. What about nulls, errors, edge conditions? AI might miss these unless you specifically tell it to check them.
* **Weird or Irrelevant Tests:** AI can "hallucinate" and generate tests for things that don't make sense for your app, or test trivial details instead of important behavior.
* **Sneaky Logic Bugs**: These look okay at first glance.
* **Bad Setup**: Mocking things wrong, starting the test in an invalid state.
* **Bad Asserts**: Using the wrong comparison, expecting the wrong result, off-by-one errors.
* **Flaky Tests**: Tests involving threads or async code are hard. AI might generate tests that sometimes pass, sometimes fail due to timing issues.
* **Missing the Big Picture:** Good tests often need domain knowledge. AI usually doesn't have deep context about *your specific app* unless you give it lots of info. It might test a method fine in isolation but miss its system-wide impact.
* **Dynamic Stuff \& Async:** Testing tricky things like UIs, message queues, or async operations? AI often struggles to generate reliable tests for these without a lot of help or manual fixes.

Here's a quick example:

```
// AI might generate something like this:
@Test
void testProcessItem() {
    ItemProcessor processor = new ItemProcessor(/* dependencies */);
    Item item = getTestItem();

    // Maybe AI doesn't know mocking is needed here
    // MockItemRepository mockRepo = mock(ItemRepository.class);
    // when(mockRepo.save(any(Item.class))).thenReturn(item);
    // processor.setRepository(mockRepo);

    processor.process(item);

    // Problem: No assertion! Does 'process' do anything? Is item saved?
}
```


Looks like a test, runs, but proves nothing. And it will be GREEN !! . *You gotta check.*

**Problem #2: Testing the Code You Have, Not the Code You Need (Verification vs. Validation Trap)**
---------------------------------------------------------------------------------------------------

This is the deeper problem. Even if an AI test is technically correct *for the current code* , it might be testing the wrong thing if the code itself is buggy. It's about **Verification vs. Validation**:

* **Verification:** "Are we building the product right?" Does the code do what the current implementation says it does? **AI is okay at this**.
* **Validation:** "Are we building the right product?" Does the code actually meet the user's *real* needs? Does it solve the problem correctly? **AI struggles here.**

If your calculateTax method has a bug and returns negative tax for some inputs, an AI looking at the code might generate a test asserting that calculateTax(badInput) *should* return that negative number. It *verifies* the bug.

Here is a simple example of this buggy method and its AI generated test:

```
public BigDecimal calculateTax(BigDecimal income) {
   BigDecimal grossTax = income.multiply(TAX_RATE);

   // *** THE BUG IS HERE ***
   // Simple subtraction without checking if the result is negative.
   BigDecimal netTax = grossTax.subtract(STANDARD_DEDUCTION);

   // Rounding for standard currency format (e.g., 2 decimal places)
   return netTax.setScale(2, RoundingMode.HALF_UP);
}

@Test
@DisplayName("Test calculateTax: Should return expected negative tax for low income due to BUG")
void calculateTax_whenIncomeIsLow_shouldReturnNegativeTax_dueToBug() {
  BigDecimal lowIncome = new BigDecimal("10000.00");
  // Expected calculation: (10000 * 0.15) - 5000 = 1500 - 5000 = -3500
  BigDecimal expectedNegativeTax = new BigDecimal("-3500.00");
  BigDecimal actualTax = calculator.calculateTax(lowIncome);
  // We are specifically asserting that the bug produces this negative result.
  assertEquals(expectedNegativeTax, actualTax,
                "BUG CONFIRMATION: calculateTax should return -3500.00 for 10000.00 income");
}
```


Why?

1. **Code is the Source:** The AI learns from the code you give it.
2. **No Requirements Mind-Reading:** Without clear, up-to-date requirements, AI doesn't know what the code *should* do.
3. **It Matches Patterns:** It sees input -\\\> process -\\\> output in your code and writes a test for *that specific pattern*, bug or not.

Let's see which are the challenges with the validation trap and recommendations to avoid them.

* **The False Confidence Problem:** This is bad. A test passing *because* of a bug makes everything look green, but the bug is still there, now with a test "protecting" it. Fix the bug later, and the AI's test fails, confusing everyone.
* **Ignoring Requirements Changes:** Requirements evolve. Code written last month might be wrong now. AI testing the *code* won't know that. It just keeps confirming the potentially outdated behavior.
* **Analogy:** Like spell-checking a document but not fact-checking it. Verification passes, validation fails.
* **Can AI Test Requirements Directly?** Some tools try. You feed them requirements (like Gherkin specs), and they generate tests \\cite{aws, visuresolutions, thoughtworks}. Better, but still needs perfect, up-to-date requirements and the AI can still misinterpret them. Many simple AI tools just look at the code.

Consider this buggy code:

```
// Buggy Implementation
public String formatUsername(String name) {
    if (name == null || name.trim().isEmpty()) {
        return "guest"; // Should maybe throw exception?
    }
    // Bug: Doesn't handle names with spaces well
    return name.toLowerCase();
}

// AI-Generated Test (Based on Buggy Code)
@Test
void whenNameHasSpace_shouldReturnLowerCase() { // Validates bug!
    UserFormatter formatter = new UserFormatter();
    String result = formatter.formatUsername("Test User");
    // AI sees the code returns "test user", so it asserts that.
    // Requirement might be to remove spaces or throw error.
    assertEquals("test user", result);
}
```


This test passes but locks in the bad behavior of allowing spaces. You, the dev, need to check if the test matches the *requirement*, not just the buggy code.

**So? What to Do? Don't Use AI for Generating Tests? Nah, Rely on Your AI Test Quality Guardian**
-------------------------------------------------------------------------------------------------

Given that AI tests can be wonky, using static analysis tools is pretty much essential. These tools automatically scan your code (including tests) against a huge rulebook, finding potential bugs, security issues, and just plain confusing code. When AI is potentially adding lots of code fast, you need this automated check.

Some of these tools even promote AI Code assurance, to keep AI-generated code in check, sometimes with even stricter rules. Makes sense -- treat AI code with the same (or more) skepticism as human code.

One of these tools is SonarQube, which has 47 specific rules just for Java tests \[<https://rules.sonarsource.com/java/tag/tests/>\]. Let's break down the kinds of issues it catches, with quick examples showing how AI might mess up.

**1. Assertions - Did You Actually Check Anything?**

* **Purpose:** Ensure tests make meaningful checks. It can be easy to forget assertions and the test will pass making it difficult to spot.
* **Example ([Rule S2699](https://rules.sonarsource.com/java/RSPEC-2699/)):**

```
// Noncompliant code
@Test
void testAddItem() {
    Cart cart = new Cart();
    Item item = new Item("Thing");
    cart.add(item);
    // Forgot to assert!
}

// Compliant code
@Test
void testAddItem() {
    Cart cart = new Cart();
    Item item = new Item("Thing");
    cart.add(item);
    assertEquals(1, cart.getItemCount()); // Added assertion
}
```


* **AI Trap:** AI might just call the method and forget the assert.

**2. Test Structure, Setup, Teardown - Getting the Basics Right**

* **Purpose:** Enforce standard test structure conventions needed by frameworks like JUnit.
* **Example ([Rule S5786](https://rules.sonarsource.com/java/RSPEC-5786/)):**

```
// Noncompliant code (JUnit 5)
@Test
private void myPrivateTest() { // Test methods shouldn't be private
    assertTrue(true);
}

// Compliant code (JUnit 5)
@Test
void myVisibleTest() { // Default visibility is fine, or public
    assertTrue(true);
}
```


* **AI Trap:** Generating methods with wrong visibility (private, static) or return types.

**3. Naming Conventions - Can Anyone Understand This?**

* **Purpose:** Make tests readable and understandable from their names. The BDD convention is widely adopted, but there are [others](https://dzone.com/articles/7-popular-unit-test-naming).
* **Example ([Rule S3577](https://rules.sonarsource.com/java/RSPEC-3577/)):**

```
// Noncompliant code
@Test
void test1() {
    // ... complex setup and assert ...
}

// Compliant code
@Test
void shouldThrowIllegalArgumentException_WhenInputIsNull() {
    // ... clear test logic for null input ...
}
```


* **AI Trap:** Using generic names like testMethod1 or test_feature_abc.

**4. Using Test Frameworks Correctly - JUnit/TestNG Gotchas**

* **Purpose:** Ensure proper use of framework features and APIs. Test frameworks provide specific ways of handling different use cases. In this particular case, exceptions.
* **Example ([Rule S5776](https://rules.sonarsource.com/java/RSPEC-5776/)):**

```
// Noncompliant code (JUnit 5) - Old way to check exceptions
@Test
void testDivisionByZero_OldWay() {
    Calculator calc = new Calculator();
    try {
        calc.divide(1, 0);
        fail("Should have thrown ArithmeticException");
    } catch (ArithmeticException expected) {
        // Expected exception caught, test passes implicitly
    }
}

// Compliant code (JUnit 5) - Using assertThrows
@Test
void testDivisionByZero_NewWay() {
    Calculator calc = new Calculator();
    assertThrows(ArithmeticException.class, () -> {
        calc.divide(1, 0);
    });
}
```


* **AI Trap:** Using outdated patterns (like the try/catch/fail for exceptions) or mixing framework versions.

**5. Performance and Resource Usage - Don't Slow Down the Build**

* **Purpose:** Avoid bad practices like printing to console or leaking resources in tests. It is not easily configurable, can mess with build tools and it's a sync process that will slow down the build.
* **Example ([Rule S106](https://rules.sonarsource.com/java/RSPEC-106/)):**

```
// Noncompliant code
@Test
void testSomethingComplex() {
    // ... logic ...
    System.out.println("Debug: Intermediate value = " + value); // Avoid this
    // ... asserts ...
}

// Compliant code
@Test
void testSomethingComplex() {
    // ... logic ...
    log.debug("Debug: Intermediate value = " + value);
    // ... asserts ...
}
```


* **AI Trap:** Leaving System.out.println calls used during generation/debugging.

**6. Mocking Frameworks - Using Mocks Correctly**

* **Purpose:** Guide correct usage of mocking frameworks like Mockito, specially on the setup phase. If not done correctly can lead to unexpected issues.
* **Example ([Rule S5979](https://rules.sonarsource.com/java/RSPEC-5979)):**

```
// Noncompliant code (Potential Issue: Forgetting to mock)@Testvoid testServiceUsingRepository() {
    // Missing mock setup for repository dependency
    MyRepository repo; // = mock(MyRepository.class);
    MyService service = new MyService(repo); // Might throw NPE if repo is null
    service.doWork();
    // Assertions might fail unpredictably
}

// Compliant code (Basic Mocking)
@Test
void testServiceUsingRepository() {
    MyRepository repo = mock(MyRepository.class); // Mock dependency
    when(repo.getData()).thenReturn("mock data"); // Stub method call
    MyService service = new MyService(repo);
    service.doWork();
    verify(repo).getData(); // Verify interaction
    // Add assertions based on service logic
}
```


* **AI Trap:** Generating incomplete mock setups or incorrect verification logic.

**7. Exception Handling - Be Specific**

* **Purpose:** Ensure tests checking for exceptions look for the *specific* expected exception. Generic exceptions can swallow several different use cases, and the code should catch those exceptions types that can handle.
* **Example ([Rule S112](https://rules.sonarsource.com/java/RSPEC-112)):**

```
// Noncompliant code
@Test
void testInvalidInput() {
    Processor processor = new Processor();
    // This is too broad, might catch unexpected runtime exceptions
    assertThrows(Exception.class, () -> {
        processor.process(null);
    });
}

// Compliant code
@Test
void testInvalidInput() {
    Processor processor = new Processor();
    // Be specific about the expected exception
    assertThrows(IllegalArgumentException.class, () -> {
        processor.process(null);
    });
}
```


* **AI Trap:** Using generic Exception when a more specific one is appropriate.

Seeing these examples shows how easy it is for generated code (and human code!) to violate basic testing hygiene. SonarQube acts as your automated checklist for this stuff.

**How to Use AI Test Tools Without Getting Burned**
---------------------------------------------------

So, how do you actually use these tools without causing chaos?

1. **Human Review is Mandatory (Really!):** Never skip this. Check if the test makes sense, if the asserts are good, if it tests the *requirement*, if it covers edge cases, and if your static analysis guardian is happy.
2. **Use Static Analysis Everywhere:** Put a linter in your IDE. Put it in your CI pipeline. Fail the build if quality drops. Make it non-negotiable.
3. **Let AI Do the Easy Stuff:** Don't expect miracles. Use AI for:
   * **Boilerplate:** Test methods, basic setup/teardown.
   * **Simple Mocking:** Basic when/thenReturn.
   * **Test Variations:** Generating different inputs for a test *you* already wrote and trust.
4. **Give Better Instructions:** Garbage prompts \\= garbage tests.
   * **Add Context:** Give it docs, requirements snippets, good examples.
   * **Be Specific:** Tell it *exactly* what to test, what to mock, what to assert.
5. **Iterate:** Treat the AI output as a first draft. Review, fix, improve.
6. **Learn the Tools:** Figure out how *your* specific AI tool works best. Practice prompting. Learn to spot its common mistakes quickly.
7. **Start Small:** Try it on a safe project first. See if it *really* saves time after you account for fixing its output.

**Wrapping Up**
---------------

AI test generation? It's here and it can write test code fast, which is cool. But don't just trust it blindly. AI often gets things wrong, misses assertions, or writes tests that just confirm your bugs are still there.

Think of AI as a helper, not ***the*** expert. Let it write first drafts or boring bits. But *you* need to review everything. Does it test the actual requirement? Is the logic sound? Use static analysis to automatically check for common mistakes in your pipeline. Keep your brain engaged, and you can probably get some real speed benefits from AI without sacrificing quality.
