---
title: "7 habits of Highly Effective Java Coding"
date: "2025-10-15T11:25:03+00:00"
lastmod: "2025-10-15T12:37:10+00:00"
description: "Habits to work with the AI to write code that's actually good—solid, secure, and won't give the next person on your team a headache or your company a serious problem."
authors:
  - "jonathan-vila"
image: "Screenshot-2025-09-23-at-19.14.51.png"
categories:
  - "AI"
  - "Developer Tools"
  - "GenAI"
  - "Java"
related_posts:
  - "devops-for-developers-continuous-integration-github-actions-and-sonar-cloud"
  - "foojay-podcast-49"
  - "foojay-podcast-58"
  - "foojay-podcast-74"
frozen: false
---

### **From AI User to AI Pro**

Let's be real, AI coding tools are everywhere now. 🤖 They're no longer some shiny new toy---they're a part of our daily grind as developers, just like our morning coffee. ☕

For us Java devs, whether we're wrestling with a giant legacy app or juggling a bunch of microservices, these tools look like a huge win for getting stuff done faster. 🚀

But here's the catch: just coding faster isn't the whole story. If you're not careful, it can actually lead to bigger problems down the road. 🤔

The real goal is to learn how to work with the AI to write code that's actually good---solid, secure, and won't give the next person on your team a headache or your company a serious problem.  

This means we have to level up from being copy-paste machines and become smart developers who really know how to handle these powerful tools. 💪

So, I'm sharing 7 key habits that will help you do just that. This isn't just about speed; it's about getting way better at the job you love. 💡

### **1. The Golden Rule: Take Pride and Ownership in Your Craft 🥇**

The first and most important habit isn't about blame; it's about **pride and ownership**. As developers, we are modern-day craftspeople. There's a deep, intrinsic satisfaction that comes from delivering high-quality, elegant, and robust code.

This sense of pride is the foundation of a successful career and a healthy team dynamic. It's what transforms a difficult pull request conversation into a collaborative design session and what turns the daily task of writing code into an opportunity for learning and mastery.

AI coding assistants are powerful new tools in our workshop, but like any power tool, they can be used to create beautiful work or to make a mess quickly. **Blindly accepting AI-generated code is the fastest way to erode that professional pride** . When you let unvetted code into your codebase, you're not just introducing potential bugs; you're forfeiting a chance to learn, to improve, and to stand behind your work with **confidence**.

The rule isn't "you will be blamed"; it's "your name is on it, so make it something to be proud of."

#### **From Painful PRs to Productive Conversations**

We've all been in pull request (PR) reviews that feel like a slog. The comments are a long list of stylistic nits, potential null pointer exceptions, and questions like, "What is this part even supposed to do?". This often happens when code is rushed or not fully understood.

Now, imagine a PR where the code has high quality, the logic is clear, and the implementation is robust, and obviously **you completely understand the code inside the PR**. The conversation instantly elevates. Instead of nitpicking syntax, the team discusses architectural choices, business logic, and potential feature enhancements. This is where real collaboration happens.

By using AI as a starting point and then meticulously refining the output, you ensure your PRs fall into the second category. You are demonstrating to your team that you've done the hard work of thinking, not just the easy work of prompting. **You own the solution**, and the resulting discussion respects that ownership.

#### **Technical Example: The AI's Brittle `CompletableFuture` vs. The Crafted Resilient Solution**

Consider a common scenario in a microservices architecture: you need to build a user's dashboard by aggregating data from three different services concurrently. You ask an AI: *`"// Java: using CompletableFuture, concurrently fetch a user's details, their 5 most recent orders, and their product review count. Combine them into a single DTO and handle errors."`*

The AI, aiming for a direct solution, might produce something like this:

```java
// AI-Generated First Draft - Brittle and Naive
public UserDashboardDTO getDashboard(long userId) throws Exception {
    // Fire off all requests in parallel
    CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getById(userId));
    CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(() -> orderService.getRecentForUser(userId, 5));
    CompletableFuture<Integer> reviewsFuture = CompletableFuture.supplyAsync(() -> reviewService.countByUser(userId));

    // Wait for all of them to complete
    CompletableFuture.allOf(userFuture, ordersFuture, reviewsFuture).join(); // The first major flaw

    // If we get here, all services succeeded.
    return new UserDashboardDTO(userFuture.get(), ordersFuture.get(), reviewsFuture.get()); // The second flaw
}
```

This code is a hidden landmine in a distributed system. 💣

* **Brittle Failure Mode:** `CompletableFuture.allOf(...).join()` creates an "all-or-nothing" scenario. If just one of the services (e.g., `reviewService`) times out or throws an error, the `join()` call will throw an exception, and the entire operation fails. The user gets an error page instead of seeing their user details and orders, which were fetched successfully.
* **No Timeouts:** There are no timeouts defined. If `orderService` is slow, this thread will hang indefinitely, consuming resources on your server.
* **Inefficient Composition:** Calling `.get()` after the `join()` can re-throw exceptions and is less elegant than a proper composition chain.

A developer who **takes pride in their craft** recognizes that in a microservices world, partial communications and network failure is not rare. They refactor the code for **resilience and graceful degradation**.

```java
// Human-Crafted, Professional Solution - Resilient and Robust
public UserDashboardDTO getDashboard(long userId) {
    ExecutorService customExecutor = Executors.newVirtualThreadPerTaskExecutor();

    // Each future is now a self-contained, resilient unit of work.
    CompletableFuture<User> userFuture = CompletableFuture
        .supplyAsync(() -> userService.getById(userId), customExecutor)
        .orTimeout(2, TimeUnit.SECONDS) // Set a reasonable timeout
        .exceptionally(ex -> new User.GuestUser()); // On error, return a default/guest user

    CompletableFuture<List<Order>> ordersFuture = CompletableFuture
        .supplyAsync(() -> orderService.getRecentForUser(userId, 5), customExecutor)
        .orTimeout(3, TimeUnit.SECONDS)
        .exceptionally(ex -> List.of()); // On error, return an empty list

    CompletableFuture<Integer> reviewsFuture = CompletableFuture
        .supplyAsync(() -> reviewService.countByUser(userId), customExecutor)
        .orTimeout(2, TimeUnit.SECONDS)
        .exceptionally(ex -> 0); // On error, return zero

    // Combine the results of the now-safe futures
    return CompletableFuture.allOf(userFuture, ordersFuture, reviewsFuture)
        .thenApply(v -> new UserDashboardDTO(
            userFuture.join(),
            ordersFuture.join(),
            reviewsFuture.join()
        )).join();
}
```

This professional solution is vastly superior. It handles failures gracefully within each asynchronous call using `.exceptionally()`, allowing the dashboard to render with partial data. It enforces timeouts with `.orTimeout()` to protect system resources.

By explaining these choices in the PR---discussing the principles of resilient design and fault tolerance---**YOU** demonstrate deep expertise that goes far beyond simply making the code "work." This is how you build a reputation for excellence and drive your career forward.

### **2. Feed the Beast: Your Project's Context is its Fuel ⛽**

AI coding assistants are incredibly powerful, but they aren't mind readers. They operate on a simple principle: garbage in, garbage out. If you give them a vague, one-line request, you'll get back a generic, probably useless, chunk of code. The secret to getting amazing results is to "feed the beast" with as much high-quality context as you possibly can.

Think of it like briefing a new developer on your team. You wouldn't just say, "Hey, go build the shipping cost feature." You'd give them the Jira ticket, point them to the requirements, explain the existing data models, and show them the acceptance criteria. You need to do the exact same thing for your AI partner.

This means going way beyond in-code comments. Give it the issue link, paste in the user story, and provide the Gherkin `feature` file if you have one. The more details you provide about the "what" and "why," the better the AI will be at generating the "how."

#### **Technical Example: The Vague Wish vs. The Detailed Brief**

Imagine you're tasked with implementing a dynamic shipping cost calculation based on a complex set of business rules.

**The Vague Wish (and its useless result):**

You start with a lazy prompt, giving the AI almost nothing to work with:

```java
// TODO: Implement the shipping cost logic
// A vague prompt to the AI:
// "Java method to calculate shipping cost for an order"
```

The AI, having no context, will produce a generic, over-simplified method that is completely wrong for your application:

```java
// AI's Generic (and wrong) Response:
public BigDecimal calculateShippingCost(Order order) {
    // A complete guess based on common, simple examples.
    BigDecimal baseRate = new BigDecimal("5.00");
    BigDecimal weightCharge = order.getWeightInKg().multiply(new BigDecimal("1.50"));
    return baseRate.add(weightCharge);
}
```

This code is a waste of time. It doesn't know about your business rules, your customer types, or your data structures. Now, let's try again by giving the AI a proper brief.

**The Detailed Brief (and its awesome result):**

This time, you treat the prompt like a mini-spec document, packing it with context right from your project.

```java
/*
 * Feature: JIRA-451 - Calculate Dynamic Shipping Cost
 * * User Story: As a user, I want to see an accurate shipping cost at checkout
 * based on my membership status, location, and the items in my cart.
 *
 * Acceptance Criteria (from feature file):
 * - Prime members in the US get free shipping.
 * - Non-Prime US orders have a $5 flat fee.
 * - International orders have a 15% surcharge on the subtotal.
 * - Any order containing a 'HEAVY_GOODS' item has an additional $25 fee.
 *
 * Relevant Data Classes:
 * class User { boolean isPrimeMember(); Address getAddress(); }
 * class Order { List<LineItem> getItems(); BigDecimal getSubtotal(); }
 * class LineItem { Product getProduct(); }
 * class Product { ProductCategory getCategory(); } // enum ProductCategory { STANDARD, HEAVY_GOODS }
 *
 * AI Prompt:
 * "Using the context and data classes above, write a Java method `calculateShipping`
 * that takes a User and an Order and returns the final shipping cost as a BigDecimal."
 */
```

With this rich context, the AI can now generate something incredibly useful and specific to your actual codebase:

```java
// AI's Context-Aware (and useful) Response:
public BigDecimal calculateShipping(User user, Order order) {
    // Rule 1: Prime members in the US
    if (user.isPrimeMember() && "US".equals(user.getAddress().getCountryCode())) {
        return BigDecimal.ZERO;
    }

    BigDecimal shippingCost;

    // Rule 2 & 3: US vs. International
    if ("US".equals(user.getAddress().getCountryCode())) {
        shippingCost = new BigDecimal("5.00"); // Flat fee for non-prime US
    } else {
        // 15% surcharge for international
        shippingCost = order.getSubtotal().multiply(new BigDecimal("0.15"));
    }

    // Rule 4: Surcharge for heavy goods
    boolean hasHeavyGoods = order.getItems().stream()
        .anyMatch(item -> item.getProduct().getCategory() == ProductCategory.HEAVY_GOODS);

    if (hasHeavyGoods) {
        shippingCost = shippingCost.add(new BigDecimal("25.00"));
    }

    return shippingCost;
}
```

Look at that difference! ✨ The second version is almost production-ready. It correctly implements the complex business logic because you gave the AI a map of your world. You spent an extra minute providing context and saved yourself thirty minutes of writing boilerplate and fixing the AI's guesses. That's a massive win.

Furthermore, this idea of creating rich context is expanding beyond just code and tickets. A whole ecosystem of specialized AI tools is emerging to help create and understand high-level documentation, which then becomes another powerful source of context.

MCP Servers can help to expand the capabilities of our AI Agents. Directories like [mcp.so](http://mcp.so/) are great places to discover these. For instance, you can use a tool to connect to Jira to get information about the issues for a given feature, or even Google Docs to get formal and extended requirements definitions.

This information then serves as excellent context for your primary coding assistant, helping it understand the system's architecture when you ask it to write related code. This creates a virtuous cycle: you use AI to generate clear documentation, which in turn helps your coding AI generate better code.

### **3. Dodge the "Ball of Mud": Keep Your Code Maintainable 🧠**

"Keep it simple" is easy advice to give, but in the real world of enterprise Java, it's not so simple, is it? A 20-line method might be simple, but if you have a hundred of them in a tangled mess, you've created a classic "Big Ball of Mud" architecture. 👎

The real goal isn't just simplicity; it's **maintainability**. We want to write code that our future selves (and our teammates) can read, debug, and extend without wanting to tear their hair out.

AI assistants, for all their power, don't have a great sense of long-term consequences. They are fantastic at solving the immediate problem you give them, but they're not thinking about your architectural goals. They can, and will, generate overly clever, complex, or just plain weird code if you let them. Our job is to be the architect, not just the bricklayer, and guide the AI toward solutions that are easy to live with.

#### **Technical Example: The "Clever" Stream vs. The Debuggable Loop**

The Java Stream API is incredibly powerful, but it's also one of the easiest ways to write code that's "write-only." An AI, trained on millions of examples of functional programming, can get a little *too* excited about streams.

Imagine you need to process a list of new user signups. For each user, you need to check if they are eligible for a promo, send them a welcome email, and add them to a database, but only if they've verified their email.

You ask your AI: *"// Java: using a stream, process this list of signups. Filter for verified users, check promo eligibility, send a welcome email, and save to the database. Return a list of the users who were successfully saved."*

The AI might produce this "clever" one-liner:

```java
// AI's "Clever" (but unmaintainable) Solution
public List<User> processSignups(List<SignUp> signups) {
    return signups.stream()
                  .filter(SignUp::isVerified) // Filter for verified users
                  .peek(signup -> { // DANGER: Side effects inside a stream!
                      boolean eligible = promoService.isEligible(signup.getEmail());
                      emailService.sendWelcomeEmail(signup.getEmail(), eligible);
                  })
                  .map(this::convertAndSaveUser) // This method handles the DB interaction
                  .collect(Collectors.toList());
}

private User convertAndSaveUser(SignUp signup) {
    User user = new User(signup.getName(), signup.getEmail());
    return userRepository.save(user);
}
```

This code is a maintenance nightmare. 😵

* **Debugging Hell:** How do you debug this? If `sendWelcomeEmail` throws an exception for one user, the whole stream fails. You can't easily put a breakpoint inside the `peek` to inspect the state for a single user without getting swamped.
* **Hidden Side Effects:** The `peek` operation is performing major side effects (sending an email!). This violates the functional principles that make streams great and makes the code incredibly hard to reason about.
* **Poor Readability:** To understand the logic, you have to mentally unpack this dense chain of operations. It's not immediately obvious what's happening.

A developer focused on **maintainability** would see this and immediately refactor it into something more "boring," but infinitely more professional: a simple loop.

```java
// Human-Crafted, Maintainable Solution
public List<User> processSignups(List<SignUp> signups) {
    List<User> successfullyProcessedUsers = new ArrayList<>();

    // A simple, "boring" loop is easy to read, easy to debug.
    for (SignUp signup : signups) {
        if (!signup.isVerified()) {
            continue; // Skip unverified users
        }

        try {
            // Each step is clear and explicit.
            boolean eligible = promoService.isEligible(signup.getEmail());
            emailService.sendWelcomeEmail(signup.getEmail(), eligible);

            User userToSave = new User(signup.getName(), signup.getEmail());
            User savedUser = userRepository.save(userToSave);
            successfullyProcessedUsers.add(savedUser);

        } catch (EmailException | DataAccessException e) {
            // We can handle errors for a single user without crashing the whole batch.
            log.error("Failed to process signup for email: {}", signup.getEmail(), e);
        }
    }
    return successfullyProcessedUsers;
}
```

This version is superior in every practical way. It's easy to read, you can stick a breakpoint anywhere you want, and the `try-catch` block provides robust, granular error handling.

This same principle applies at a higher level. Resist the urge to let an AI push you toward an overly complex design like microservices when a well-structured monolith or a Hexagonal Architecture would be far more maintainable for your team's size and scope. Use AI as a tool, but you are the architect.

Choose boring, maintainable solutions. Your future self will thank you. 🙏

### **4. Clean Your Room: No Stray Code or Sketchy Dependencies 🧹**

Think of your AI assistant as a super-enthusiastic and brilliant, but slightly messy, collaborator. In its rush to build something cool, it might leave some tools out, grab a sketchy-looking part from a random website, or leave unused scraps of code lying on the floor.

Our job is to be the diligent cleaner who tidies up afterward. "Stray code" isn't just about unused imports or dead methods; it's about ensuring every single line in our project, including our build files, is there for a reason and comes from a trusted source.

Failing to do this isn't just sloppy---it can be a massive security risk. Modern software development is built on a mountain of dependencies, and AI can inadvertently lead us to pull a malicious one right into our project.

#### **Technical Example: The AI's "Helpful" but Malicious Dependency**

This is one of the most subtle and dangerous ways an AI can cause trouble. Let's say you need to add a feature to process and manipulate some complex XML files. You're not sure which library is best.

You ask your AI: *"// I need to parse a complex XML file in Java. Suggest a good library and give me the Maven dependency for it."*

The AI, having been trained on a vast amount of public code, including forum posts and GitHub issues with typos, might suggest this:

```java
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text-utils</artifactId> 
    <version>1.10.0</version>
</dependency>
```

You, the busy developer, glance at it. "org.apache.commons" looks legit, the name seems right, and you paste it into your `pom.xml`. **You've just potentially opened the door to a typosquatting or dependency confusion attack.** 💀

A threat actor could have published a malicious library to Maven Central under that slightly incorrect name (`commons-text-utils` instead of the real `commons-text`). Your build system would happily download it, and suddenly you have malware executing with full permissions inside your build environment or even in your production application.

The **only** safe habit is to **never, ever trust a dependency string from an AI**. Always do the 30-second check:

1. Open your web browser.
2. Go to Maven Central (`search.maven.org`) or the library's official GitHub page.
3. Search for the library and copy the **official, verified** dependency snippet.

#### **What About Dead Code?**

On a less dramatic but still important note, AIs often leave behind digital clutter. It might generate three helper methods but only end up using one in its final answer.

In Java, this isn't always a critical failure. An unused import is harmless. But an unused dependency in your `pom.xml`? That still gets bundled into your final JAR/WAR, bloating your application size. Worse, if you're using a framework like Spring with broad component scanning, a class from an "unused" dependency on the classpath could be auto-detected and wired into your application, causing truly baffling behavior.

The habit here is simple hygiene. Before you commit, run your IDE's code cleanup tools. Remove unused imports, variables, and methods. Use static analysis to flag unused dependencies. It's the digital equivalent of sweeping the workshop floor before you lock up for the night. It keeps your project lean, clean, and predictable. ✨

### **5. Trust but review: Analyze the AI, the Code, and the Supply Chain 🕵️‍♀️**

Working with an AI is like getting advice from a brilliant expert who sometimes hallucinates. This is the core of my article "[AI gives you TIME not CONFIDENCE](https://dev.to/jonathanvila/ai-gives-you-not-developer-productivity-toolkit-1n9d).". The AI gives you a head start, but it doesn't give you a guarantee.

To turn that AI-generated time into a reliable product, you have to practice healthy skepticism and analyze *everything*. This habit goes deeper than just checking the generated code; it involves scrutinizing the entire development ecosystem.

We must analyze three distinct areas:

1. **The AI System Itself**: Its limitations, its biases, and its security posture.
2. **The Generated Code**: Its correctness, its security, and its adherence to modern practices.
3. **The Software Supply Chain**: The third-party dependencies the AI suggests.

### **First, Analyze Your AI System**

Before you even write a prompt, remember that the AI is not an oracle. It's a tool with known limitations.

* **Is Your Prompt Safe?** 🔐 When you paste a chunk of your company's proprietary code into a free, public AI website, where does it go? You could be unintentionally leaking trade secrets. The habit is to **use enterprise-grade, secure tools** (like GitHub Copilot for Business or self-hosted options) that guarantee your code stays private.
* **Is your AI architecture secure?** 👮When you use Agents and MCP servers, are you sure what they do? Have you checked their source code to know where your information goes?

### **Second, Analyze the Generated Code: Accuracy, Bugs, Security and Outdated Knowledge**

This is where the rubber meets the road. Recent research confirms that while AI boosts speed, it comes with significant risks to quality and security.

#### **The Sobering Reality of AI Accuracy**

Don't fall for the marketing hype. A [report from Sonar analysing top notch LLM models](https://www.sonarsource.com/resources/the-coding-personalities-of-leading-llms/), revealed that although they produce a lot of good code, they have different levels of accuracy in their responses and are still incorporating issues and vulnerabilities. This means you must assume that any code generated by an AI is likely to be flawed in some way.

#### **Security Vulnerabilities and Outdated Java Knowledge**

LLMs can directly introduce vulnerabilities into the code they generate because their training data might contain insecure patterns, or they might make logical errors during generation that result in security flaws. For example, an LLM might generate SQL code without proper input sanitization or hardcoding secrets, leading to SQL injection vulnerabilities or credentials leak.

```java
// DON'T PASTE THIS! ❌ It contains proprietary logic and sensitive keys.
// "Refactor this method to be more efficient"
public void processTransaction(Transaction tx) {
    if ("ProjectTitan".equals(tx.getProjectCode())) {
        String apiKey = "d3b07384d113edec49eaa6238ad5ff00"; // Hardcoded secret!
        var client = new ProprietaryBillingClient(apiKey);
        client.charge(tx.getAmount(), tx.getUserId());
    }
    // ... more confidential logic
}
```

Also an AI model might have a knowledge cutoff of early 2023. It knows nothing about the latest features in Java 21+. It will generate correct, but clunky and outdated code.

For example, you ask it to process different shapes. It might generate this pre-Java 21 code:

```java
// AI's Outdated (pre-Java 21) solution
public double getArea(Shape shape) {
    if (shape instanceof Circle) {
        Circle c = (Circle) shape;
        return Math.PI * c.radius() * c.radius();
    } else if (shape instanceof Square) {
        Square s = (Square) shape;
        return s.side() * s.side();
    } else {
        return 0;
    }
}
```

A modern Java developer would immediately refactor this to a much cleaner and safer `switch` expression with type patterns:

```java
// The Modern Java 21+ Solution
public double getArea(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square s -> s.side() * s.side();
        default -> 0;
    };
}
```

### **Third, Analyze the Dependencies (The Software Supply Chain)**

This is where things get really serious. An AI might suggest a cool, niche library to solve your problem. Here's your analysis checklist before you ever add it to your `pom.xml`.

**1. Is it Well-Maintained?**

You ask an AI for a CSV parsing library. It might suggest a once-popular but now-abandoned option , like [\*`net.sf`](http://net.sf/)`.supercsv:super-csv`\* discontinued in 2015.

**2. Is it Properly Licensed?**

Accidentally using the wrong license can create a legal nightmare for your company. An AI won't warn you if a library is AGPL-licensed.

* **The Threat** : An AI suggests a library for charting. You add it to your `pom.xml`, not realizing it has a restrictive license that could force you to open-source your proprietary product.
* **The Habit** : You **must** check the license. Use automated tools like the **SonarQube** or **OWASP Dependency-Check plugin** for Maven/Gradle, among others, which can automatically scan your dependencies and flag license conflicts based on policies you define.

**3. Are There Known Vulnerabilities?**

Even if a library is well-maintained and properly licensed, older versions (or even the latest) might have publicly reported security vulnerabilities.

* **The Threat** : An AI suggests `jackson-databind` version 2.9.0. You add it, unaware that this version has a critical deserialization vulnerability (CVE-2017-7525) that attackers could exploit to execute arbitrary code.
* **The Habit** : This is the most crucial check. You **must** scan your dependencies for known vulnerabilities. Tools like SonarQube\**,*\* integrate with your build process and continuously monitor your dependencies against public vulnerability databases (like the National Vulnerability Database - NVD) to alert you of potential issues. Regularly updating your dependencies is also key.

Analyzing your dependencies is a non-negotiable part of a professional developer's job. The AI is just a recommender; you are the gatekeeper. ✅

### **6. Beyond Coverage: Mandate *Meaningful* Tests ✅**

For years, we've been told to chase the holy grail of 100% code coverage. But as you astutely noted, that's often a vanity metric. A suite of heavily mocked unit tests that covers every line of code can still completely miss the point. The real goal of testing isn't to cover code; it's to **build confidence** that your software correctly solves a real-world business problem.

AI is a game-changer for testing, but it can also lead you down the wrong path if you're not careful. It's brilliant at generating boilerplate, but it has no understanding of your business intent. The modern testing habit is to use AI as a tireless assistant for the simple stuff, freeing up your valuable brainpower to design the high-level tests that truly matter.

#### **AI for the Easy Stuff: Boilerplate Unit Tests**

Let's be clear: AI is fantastic at generating simple unit tests for pure functions or utility classes. You can point it at a class and say, *"Generate JUnit 5 tests for this,"* and it will save you a ton of tedious work.

But here's the trap we discussed in the last habit: if the AI wrote the buggy code, it will happily write a test that confirms the bug, giving you a beautiful "green" test suite that is actively lying to you.

```java
// AI wrote this buggy method...
public String truncate(String text, int length) {
    // BUG: Off-by-one error. Should be <= length
    if (text.length() < length) { return text; }
    return text.substring(0, length - 1) + "...";
}

// ...and the AI will gladly write a test that confirms the bug.
@Test
void testTruncation() {
    String result = truncate("hello world", 5);
    // DANGER: The AI asserts the incorrect result it expects.
    assertEquals("hell...", result); // This passes! 😱
}
```

**The habit:** Use AI for boilerplate, but you, the human, must write the critical assertions based on the *requirements*, not based on the code's current behavior.

#### **The Human's Job: Integration Tests That Build Real Confidence**

True confidence comes from watching your code interact with real infrastructure. This is where you should focus your energy. Stop over-mocking and start writing real integration tests.

**The Wrong Way (Useless Mocking):** Many developers test their service layer by mocking the database repository. This is a low-value test.

```java
// This test proves almost nothing.
@Test
void testUserServiceWithMock() {
    // 1. Setup the mock
    UserRepository mockRepo = mock(UserRepository.class);
    when(mockRepo.findByStatus("ACTIVE")).thenReturn(List.of(new User("Jon")));

    // 2. Call the service
    UserService userService = new UserService(mockRepo);
    List<User> activeUsers = userService.findActiveUsers();

    // 3. Assert
    // This only checks if your service called the mock. It tells you NOTHING
    // about whether your actual @Query works, if your DB schema is correct,
    // or if transactions behave as expected.
    assertEquals(1, activeUsers.size());
    verify(mockRepo).findByStatus("ACTIVE");
}
```

**The Right Way (Real Confidence with Testcontainers):** A professional Java developer uses tools like **Testcontainers** to spin up a *real* database for the test.

```java
// This test builds REAL confidence.
@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // Spring Boot will automatically configure the app to use this database.
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findsOnlyActiveUsersFromRealDatabase() {
        // 1. Setup REAL data in a REAL database
        userRepository.save(new User("Jon", "ACTIVE"));
        userRepository.save(new User("Jane", "INACTIVE"));

        // 2. Call the service
        List<User> activeUsers = userService.findActiveUsers();

        // 3. Assert
        // This test proves your @Query, schema, and service logic all work together.
        assertEquals(1, activeUsers.size());
        assertEquals("Jon", activeUsers.get(0).getName());
    }
}
```

#### **The Ultimate Collaboration: AI-Powered Acceptance Tests**

Here's where it all comes together. Your Product Owner or a domain expert writes the requirements in a plain-text Gherkin file. This file becomes the ultimate source of context.

**Gherkin `login.feature` file (written by a human):**

```bash
Feature: User Login

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter "jon.doe" as my username
    And I enter "a-valid-password" as my password
    And I click the login button
    Then I should be redirected to my dashboard page
```

Now, you use this as context for your AI. **The habit:** Ask the AI to be a translator. *"Given this Gherkin feature, generate the boilerplate Java step definitions for Cucumber."*

**AI-Generated Java "Glue Code" (a huge time-saver):**

```java
// AI generates this skeleton code for you instantly.
public class LoginSteps {
    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        // TODO: Implement browser navigation logic here
        throw new io.cucumber.java.PendingException();
    }
    @When("I enter {string} as my username")
    public void i_enter_as_my_username(String username) {
        // TODO: Implement selenium/playwright logic here
        throw new io.cucumber.java.PendingException();
    }
    // ... and so on for the rest of the steps.
}
```

The AI handles the tedious mapping, and you focus on implementing the meaningful automation logic. You're not just testing code anymore; you're verifying business requirements directly.

And if you want to be extra sure your tests are good, look into **mutation testing** (e.g., with Pitest) to see if your test suite can actually catch bugs.

### **7. The Human Gateway: A Code Review for What AI Can't See 🧠**

Let's get one thing straight: automated code reviewers and SAST tools are fantastic. They are tireless defenders against simple bugs, style violations, and common security flaws. Let the machines handle that stuff. That is their job now.

Modern platforms like SonarQube, with its "[AI Code Assurance](https://www.sonarsource.com/blog/ai-code-assurance-sonar/)" feature, are evolving to specifically address code generated by AI. They can help maintain quality and consistency, acting as the first line of defense even for machine-generated code, ensuring that the human reviewer's focus remains on higher-order concerns.

This frees up human code reviews to be what they were always meant to be: a high-level conversation about the *thinking* behind the code. The Pull Request (PR) is no longer a gate for catching typos; it's a forum for sharing knowledge, questioning assumptions, and ensuring the solution truly aligns with the business domain and our long-term architectural vision.

Your role as a reviewer has been upgraded. You are no longer a human linter; you are a design partner and a knowledge steward.

#### **Focus on What Only a Human Can Judge**

When you open a PR in this new world, you can skip the stuff an automated tool can find. Instead, you focus your valuable brainpower on these questions:

* **Does it actually solve the business problem?** 🧠 An AI doesn't understand the nuances of your company's new return policy or the legal requirements of a GDPR data request. Does the code *really* do what the JIRA ticket asked for, including the unwritten assumptions?
* **Is this a maintainable design?** 🏗️ The code might work today, but will a junior developer understand it in six months? Is this a quick fix that adds to our technical debt, or a solid, long-term solution that fits our architecture (e.g., Hexagonal, DDD)?
* **What are the hidden edge cases?** ⛈️ Based on your experience, what could go wrong? What happens if a downstream API times out? What if the input list is empty? What if a user's name contains weird characters? Humans are great at this kind of "what-if" analysis.
* **Is it a good opportunity to mentor?** 🌱 A PR is one of the best places to share knowledge. It's a chance to explain a design pattern, suggest a better way to handle an error, or introduce a teammate to a more modern library or language feature.

#### **Example: A Conversation, Not a Judgment**

Imagine a developer used an AI to implement a caching layer for a frequently called service. The code is clean and the automated checks all pass. ✅

**The old way of reviewing** might be a simple "LGTM" (Looks Good To Me).

**The new way of reviewing** is a collaborative conversation that shares knowledge and improves the design.

Here's the code snippet in the PR:

```java
// service/ProductService.java
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class ProductService {
    // An in-memory cache for product details
    private final Cache<Long, ProductDetails> productCache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    public ProductDetails getProductDetails(long productId) {
        // AI correctly implemented the cache-aside pattern
        return productCache.get(productId, () -> repository.findDetailsById(productId));
    }
}
```

And here's the **human-centric review** in the PR comments:

**Senior Dev:** "Hey, great job getting the caching logic in here! The AI did a nice job with the Guava cache implementation. It's super clean. 👍"

"I have one architectural question for us to think about. This is an in-memory cache, which is perfect for a single instance. What do you think will happen when we deploy this to our production environment, which runs 3 instances of this service for high availability?"

"The risk is that the caches could get out of sync. An admin might update a product's details, and that request might hit instance A, updating its cache. But instances B and C would still be serving the old, stale data for up to 10 minutes."

"This might be a good opportunity for us to introduce a distributed cache like **Redis**. It would solve the consistency problem and give us a centralized place to manage our caching strategy. It's a bigger change, but it would make our system much more robust. What are your thoughts on that approach? No pressure to do it in this PR, but let's discuss it. 🤔"

This review accomplishes everything a human review should. It validates the work, shares deep knowledge about distributed systems, prevents a future production issue, and does it all in a collaborative, respectful way. This is a conversation AI can't have. This is where we, the humans, provide the real value.

## **Conclusion: Your AI Co-Pilot Needs a Safety Net 🚀**

The age of AI-assisted development isn't about replacing developers; it's about upgrading them.

The seven habits we've explored are a roadmap for moving beyond being a simple "user" of AI to becoming a skilled craftsperson who wields it with intention and wisdom. It's about taking pride in your work, providing deep context, demanding maintainability, practicing good hygiene, scrutinizing everything, writing tests that matter, and keeping the human element at the heart of your reviews.

Cultivating these habits is a conscious effort, but you don't have to do it alone. While you focus on high-level design and business logic, you need a safety net to catch the subtle mistakes, security vulnerabilities, and bad practices that AI can introduce into your code. This is where having an automated code quality and security toolset becomes non-negotiable.

That's why you should check out the Sonar solution. By [adding](https://www.sonarsource.com/products/sonarlint/ide-login/) **SonarQube** IDE extension(IntelliJ, VS Code, etc.), you get real-time feedback on the code as it's generated, catching bugs and vulnerabilities before they're even committed.

Then, by [connecting](https://www.sonarsource.com/products/sonarcloud/signup-free/) it to **SonarQube Cloud**, your entire team gets a shared understanding of the project's health, ensuring that what you ship is not just functional, but truly with high quality and security.

Think of it as the perfect third partner in your development process: your skill, the AI's speed, and Sonar's safety.
