---
title: "Java 22 to 24: Level up your Java Code by embracing new features in a safe way"
slug: "java-22-to-24-level-up-your-java-code-by-embracing-new-features-in-a-safe-way"
date: "2025-07-23T15:29:09+00:00"
lastmod: "2025-07-23T15:42:47+00:00"
description: "By embracing the new features in Java 22, 23, and 24—such as unnamed variables and patterns, Markdown in Javadoc, the Class-File API, and Stream Gatherers—developers can write more efficient, and more maintainable code resulting in a higher-quality code."
authors:
  - "jonathan-vila"
image: "Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "Java"
tags:
related_posts:
  - "builders-withers-and-records-javas-path-to-immutability"
  - "code-reviews-with-ai-a-developer-guide"
  - "building-for-failure-best-practices-for-easy-production-debugging"
  - "ai-driven-testing-best-practices"
frozen: false
---

### Java introduces several new language features in the 22 to 24 versions which collectively simplify code, enhance documentation, and provide powerful tools for bytecode manipulation and advanced stream processing. This article shows you how to leverage these new features with simple examples.

Understanding these new features in Java is crucial for writing updated, efficient, and high quality code. To assist developers in adopting these changes correctly, SonarQube has introduced [several new rules](https://rules.sonarsource.com/java/tag/java22) designed to check for the proper usage of unnamed variables and patterns, javadoc and markdown, Class-file new API and Stream gatherers, ensuring your code adheres to best practices and avoids common pitfalls.

We'll cover several new Java features, with new rules in SonarQube:

* Java 22 : Unnamed variables
* Java 23 : JavaDoc and Markdown
* Java 24 : Class-File API and Stream Gatherers

A significant and welcome addition in Java 22 is the finalization of unnamed variables and patterns, officially detailed in [JEP 456](https://openjdk.java.net/jeps/456). This feature enhances code clarity by allowing developers to use an underscore (`_`) for variables and patterns that are intentionally left unused.

This elegantly addresses common scenarios where a variable is required by syntax but has no relevance to the business logic, such as a caught exception object that is never inspected or a loop variable in an enhanced for-loop where only the iteration count matters.

By replacing these placeholder names with a simple underscore, developers can reduce code clutter, eliminate "unused variable" warnings, and more clearly express their intent. This ultimately leads to higher-quality,more maintainable Java code.

SonarQube introduces a suite of new rules to ensure proper adoption of Java 22's unnamed variables and patterns. These rules --- including [S7466](https://rules.sonarsource.com/java/RSPEC-7466/), [S7467](https://rules.sonarsource.com/java/RSPEC-7467), and [S7475](https://rules.sonarsource.com/java/RSPEC-7475) --- guide developers in leveraging this feature for more maintainable code. Adhering to these guidelines enables teams to significantly improve code clarity and address redundant warnings.

### **Rule S7466: Unnamed variable declarations should use the `var` identifier**

When declaring an unnamed variable, the type declaration often becomes redundant. The primary purpose of an unnamed variable is to signal that it won't be used, making its specific type less critical. Using `var` in this context enhances conciseness and maintains focus on the intent: to intentionally ignore the variable.

Let's look at an example. When iterating over a collection where only the number of iterations matters, the element itself is not used.

**Noncompliant Code Example:**

```
int count = 0;
for (String element : myList) { // "element" is unused
    count++;
}
```

In Java 22, you can use an unnamed variable. However, explicitly declaring the type is unnecessary.

**Noncompliant Code Example:**

```
int count = 0;
for (String _ : myList) { // The type "String" is redundant
    count++;
}
```

This is where rule S7466 comes in, suggesting a cleaner, more concise approach.

**Compliant Solution:**

```
int count = 0;
for (var _ : myList) {
    count++;
}
```

By using `var`, the code becomes less verbose and the intent remains clear.

### **Rule S7467: Unused exception parameters should use the unnamed variable pattern**

A common scenario in Java is catching an exception where the exception object itself is not needed. Previously, developers would have to declare the exception variable, even if it was never referenced, leading to "unused variable" warnings from static analysis tools. Java 22's unnamed variables provide a perfect solution for this.

Consider a `try-catch` block where the simple fact that an exception was caught is enough, and its details are irrelevant.

**Noncompliant Code Example:**

```
try {
    // some operation that might throw an exception
} catch (NumberFormatException e) { // "e" is unused
    // log that the format was invalid
}
```

While functional, the declaration of `e` is noise. Using an unnamed variable is a better approach, and this is what SonarQube now recommends.

**Compliant Solution:**

```
try {
    // some operation that might throw an exception
} catch (NumberFormatException _) {
    // log that the format was invalid
}
```

This compliant solution is cleaner and explicitly communicates that the exception object itself is not important for the handling logic.

### **Rule S7475: Types of unused record components should be removed from pattern matching**

Record patterns, a powerful feature for deconstructing record instances, are also enhanced by unnamed patterns. When pattern matching against a record, you might only be interested in a subset of its components. With unnamed patterns, you can ignore the components you don't need.

When an entire record component is unused in a pattern match, specifying its type is superfluous. Rule S7475 encourages the removal of these unnecessary type declarations, leading to more readable and less cluttered code.

Imagine you have a `ColoredPoint` record and you only need the `Point` component in your logic.

**Noncompliant Code Example:**

```
if (obj instanceof ColoredPoint(Point p, Color c)) { // "c" is unused
    // logic that only uses p
}
```

In Java 22, you can use an unnamed pattern for the `Color` component. However, including the type is not necessary if the component is completely ignored.

**Noncompliant Code Example:**

```
if (obj instanceof ColoredPoint(Point p, Color _)) { // The type "Color" is redundant
    // logic that only uses p
}
```

The most concise and readable version, as enforced by SonarQube, omits the type for the unused component entirely.

**Compliant Solution:**

```
if (obj instanceof ColoredPoint(Point p, _)) {
    // logic that only uses p
}
```

This approach makes the code more focused on the relevant data, improving maintainability.

Java 23 introduces an enhancement to JavaDoc, allowing comments that begin with three slashes \\`///\` to be interpreted as JavaDoc comments using Markdown syntax.

This subtle yet significant change aims to simplify the process of writing rich and readable documentation directly within the code.

By leveraging Markdown, developers can more easily format their JavaDoc comments with features like bold text, italics, lists, and code blocks, without needing to learn specific JavaDoc HTML tags, officially detailed in [JEP 445](https://openjdk.org/jeps/445).

This streamlines the documentation process, making it more intuitive and encouraging the creation of better-formatted and more accessible API documentation.

SonarQube has introduced new rules to assist developers in adopting Java 23's Javadoc and Markdown enhancements. These rules --- including [S7476](https://rules.sonarsource.com/java/RSPEC-7476) and [S7474](https://rules.sonarsource.com/java/RSPEC-7474) --- ensure that documentation is consistently formatted, easy to read, and free from common migration pitfalls.

By leveraging these rules, developers can seamlessly integrate Markdown into their Javadoc comments to improve clarity and maintainability, ensuring that the old code is completely aligned with the new features.

### **Rule S7476: Comments should not start with more than two slashes**

With Java 23, comments starting with `///` are now officially interpreted as Javadoc comments that use Markdown syntax. Before, they were simply ignored by the Javadoc tool and treated as regular implementation comments.

This change means that existing comments in your codebase could unintentionally become part of your public API documentation after migrating to Java 23. This can lead to confusing or unprofessional-looking documentation and increases the effort required for migration. This new rule helps you find these cases in advance so they can be corrected.

**Noncompliant Code Example:**

```
public class Calculator {

  /////////////////////////////////////////////
  // A section for advanced math operations. //
  // These are experimental.                 //
  /////////////////////////////////////////////
  public int add(int a, int b) {
    /// This is a super important implementation note for the add method.
    /// It should not be in the final Javadoc.
    return a + b;
  }
}
```

In the example above, both the decorative block comment and the `///` comment would be incorrectly processed as Javadoc in Java 23. **Compliant Solution:**

```
public class Calculator {

  // A section for advanced math operations.
  // These are experimental.
  public int add(int a, int b) {
    // This is a super important implementation note for the add method.
    // It should not be in the final Javadoc.
    return a + b;
  }
}
```

The compliant solution is to ensure regular comments use the standard `//` syntax.

### **Rule S7474: Markdown, HTML and Javadoc tags should not be mixed**

Java 23's introduction of Markdown in Javadoc comments is a significant step towards cleaner, more readable documentation.

To maintain consistency, it's best to fully embrace Markdown syntax and avoid mixing it with legacy HTML tags (like `<b>`, `<code>`, `<li>`) or old Javadoc block tags (like `{@code}` or `{@link}`). Mixing these styles can lead to inconsistent rendering across different tools and makes the raw documentation harder to read.

This rule encourages developers to use the modern, more concise Markdown syntax wherever possible.

**Noncompliant Code Example:**

```
///
/// A utility class for <b>String</b> operations.
/// <p>
/// Use this class to perform common manipulations. For more details,
/// see {@link java.lang.String}.
/// You can also use {@code new StringManipulator()}.
///
public class StringManipulator {
  // ...
}
```

This Javadoc mixes bold HTML tags with Javadoc's `{@link}` and `{@code}` tags. The clean, modern approach is to use Markdown for all formatting.

**Compliant Solution:**

```
///
/// A utility class for **String** operations.
///
/// Use this class to perform common manipulations. For more details,
/// see [String].
/// You can also use `new StringManipulator()`.
///
public class StringManipulator {
  // ...
}
```

By adopting a consistent Markdown style, your documentation becomes cleaner, easier to write, and future-proof.

Java 24 introduces the Class-File API ([JEP 457](https://openjdk.org/jeps/457)), a significant enhancement for parsing, generating, and transforming Java class files. This API provides a programmatic way to work with class files at a low level, offering more flexibility and control than existing bytecode manipulation libraries.

It's particularly beneficial for tools that perform static analysis, bytecode instrumentation, or code generation, enabling them to operate directly on the structured representation of class files. By standardizing this access, the Class-File API simplifies development for such tools and ensures greater compatibility across different Java versions.

SonarQube provides a new set of rules to help developers effectively utilize the Class-File API. These rules --- including [S7479](https://rules.sonarsource.com/java/RSPEC-7479), [S7477](https://rules.sonarsource.com/java/RSPEC-7477), and [S7478](https://rules.sonarsource.com/java/RSPEC-7478) --- are designed to ensure that you use the API efficiently and correctly, leading to more concise, readable, and maintainable bytecode generation and transformation code.

Adhering to these guidelines helps developers avoid common pitfalls and leverage the full potential of Java 24's Class-File API.

### **Rule S7479: `withMethodBody` should be used to define methods with a body**

The new Class-File API ([JEP 484](https://openjdk.org/jeps/484)) provides a standardized and flexible way to programmatically generate and modify Java class files. When building a class, the `ClassBuilder` API offers two similar methods for adding a method: `withMethod` and `withMethodBody`.

While both can achieve the same result, `withMethod` is a general-purpose tool that requires an extra step to define the method's code via a nested `methodBuilder`. For the common case of defining a non-abstract method with a body, the `withMethodBody` method is a more direct and efficient choice. It reduces boilerplate code, lowers cognitive complexity by removing a layer of nesting, and ultimately improves the maintainability of your class-generation code.

This rule encourages replacing `withMethod` with its more concise counterpart, `withMethodBody`, whenever you are defining a method that has a concrete implementation.

**Noncompliant Code Example:**

```
ClassBuilder addMethod(ClassBuilder builder) {
    return builder
        .withMethod("foo", MTD_void, ACC_PUBLIC | ACC_STATIC, methodBuilder -> { // Noncompliant
            methodBuilder.withCode(codeBuilder ->
                codeBuilder.getstatic(ClassDesc.of("java.lang.System"), "out", ClassDesc.of("java.io.PrintStream"))
                    .ldc("Hello World")
                    .invokevirtual(ClassDesc.of("java.io.PrintStream"), "println", MTD_void)
                    .return_()
            );
        });
}
```

This code uses `withMethod`, which introduces a `methodBuilder`. This then requires a call to `withCode` and an additional nested lambda (`codeBuilder -> ...`) just to define the method's body, making the code unnecessarily verbose.

**Compliant Solution:**

```
ClassBuilder addMethod(ClassBuilder builder) {
    return builder
        .withMethodBody("foo", MTD_void, ACC_PUBLIC | ACC_STATIC, codeBuilder ->
            codeBuilder.getstatic(ClassDesc.of("java.lang.System"), "out", ClassDesc.of("java.io.PrintStream"))
                .ldc("Hello World")
                .invokevirtual(ClassDesc.of("java.io.PrintStream"), "println", MTD_void)
                .return_()
        );
}
```

The compliant solution uses `withMethodBody`, which directly accepts the code-building lambda. This removes the intermediate `methodBuilder`, resulting in flatter, more readable, and more maintainable code that clearly expresses the intent of defining a method and its body in a single, streamlined operation.

### **Rule S7477: The simpler `transformClass` overload should be used when the class name is unchanged**

The Class-File API, introduced in Java via [JEP 484](https://openjdk.org/jeps/484), provides powerful methods for transforming class files. Among these is the `transformClass` method, which comes in several overloaded versions to handle different use cases.

A common scenario is transforming a class without changing its name. For this specific situation, the API provides a concise two-argument version of `transformClass`. Using the more complex, three-argument overload and manually passing the original class name is unnecessary.

This rule encourages using the simplest possible API to make code shorter, clearer, and less prone to error. By choosing the correct `transformClass` overload, you explicitly signal that the class is not being renamed, which improves the overall readability and maintainability of the code.

**Noncompliant Code Example:**

```
public static void transformClassFile(Path path) throws IOException {
    ClassFile classFile = ClassFile.of();
    ClassModel classModel = classFile.parse(path);
    byte[] newBytes = classFile.transformClass(classModel,
      classModel.thisClass().asSymbol(), // Noncompliant: This argument is redundant
      (classBuilder, classElement) -> {
        if (!(classElement instanceof MethodModel methodModel &&
            methodModel.methodName().stringValue().startsWith("debug"))) {
            classBuilder.with(classElement);
        }
      });
}
```

In this example, the class name is explicitly passed to `transformClass`, even though it remains unchanged. This adds unnecessary code and can make the transformation's intent harder to grasp at a glance.

**Compliant Solution:**

```
public static void transformClassFile(Path path) throws IOException {
    ClassFile classFile = ClassFile.of();
    ClassModel classModel = classFile.parse(path);
    byte[] newBytes = classFile.transformClass(classModel,
      (classBuilder, classElement) -> {
        if (!(classElement instanceof MethodModel methodModel &&
            methodModel.methodName().stringValue().startsWith("debug"))) {
            classBuilder.with(classElement);
        }
      });
}
```

The compliant solution uses the simpler, two-argument overload of `transformClass`. By removing the redundant class name parameter, the code becomes more direct and effectively communicates that the transformation modifies the class in place without renaming it.

### **Rule S7478: `transformClass` should be used to modify existing classes**

The Class-File API ([JEP 484](https://openjdk.org/jeps/484)) provides developers with two primary methods for generating class files: `build` and `transformClass`. While `build` is a general-purpose tool for creating a class from scratch, `transformClass` is specifically designed for the common task of modifying an existing class.

A frequent pattern in bytecode manipulation is to parse a class, iterate through its elements (like methods or fields), and write a new version with some elements removed or altered. Implementing this pattern with `build` requires manually iterating over the original class's elements and adding them one by one to a new `ClassBuilder`. This approach is verbose and full of boilerplate code that obscures the core transformation logic.

This rule encourages using the `transformClass` method for such tasks. It abstracts away the manual iteration, leading to code that is more declarative, easier to read, and clearly expresses the intent of transforming an existing class model.

**Noncompliant Code Example:**

```
public static void transformClassFile(Path path) throws IOException {
  ClassFile classFile = ClassFile.of();
  ClassModel classModel = classFile.parse(path);
  byte[] newBytes = classFile.build( // Noncompliant
    classModel.thisClass().asSymbol(), classBuilder -> {
        // Manual iteration over class elements is boilerplate
        for (ClassElement classElement : classModel) {
          if (!(classElement instanceof MethodModel methodModel &&
              methodModel.methodName().stringValue().startsWith("debug"))) {
            classBuilder.with(classElement);

          }
        }
    });
  Files.write(path, newBytes);
}
```

This code manually rebuilds the class using `build`, requiring an explicit loop to copy over the elements that are being kept. This boilerplate distracts from the actual goal: filtering out debug methods.

**Compliant Solution:**

```
public static void transformClassFile(Path path) throws IOException {
  ClassFile classFile = ClassFile.of();
  ClassModel classModel = classFile.parse(path);
  byte[] newBytes = classFile.transformClass(
    classModel, (classBuilder, classElement) -> {
      // The transform is applied to each element, no manual loop needed
      if (!(classElement instanceof MethodModel methodModel &&
            methodModel.methodName().stringValue().startsWith("debug"))) {
          classBuilder.with(classElement);
        }
      });
  Files.write(path, newBytes);
}
```

The compliant solution uses `transformClass`, which handles the iteration implicitly. The provided lambda is applied to each `ClassElement`, allowing the developer to focus solely on the transformation logic. The resulting code is more concise, readable, and less error-prone.

Java 24 also introduces Stream Gatherers ([JEP 461](https://openjdk.org/jeps/461)), a new feature designed to enhance the Stream API by allowing for custom intermediate stream operations. Unlike existing \\`map\`, \\`filter\`, or \\`reduce\` operations, Gatherers enable more complex, stateful, and flexible transformations of stream elements.

This allows developers to implement operations like grouping, windowing, or de-duplication directly within the stream pipeline, leading to more expressive, efficient, and readable code for advanced data processing scenarios.

SonarQube continues its commitment to code quality by introducing new rules specifically for Java 24's Stream Gatherers. These rules --- including [S7481](https://rules.sonarsource.com/java/RSPEC-7481) and [S7482](https://rules.sonarsource.com/java/RSPEC-7482) --- are designed to guide developers in effectively leveraging this powerful new Stream API feature.

They ensure that your custom intermediate stream operations are implemented efficiently and clearly, promoting best practices and helping to avoid common pitfalls associated with stateful and stateless gatherers, leading to more robust and readable stream pipelines.

### **Rule S7481: Sequential gatherers should use `Gatherer.ofSequential`**

The introduction of Stream Gatherers ([JEP 461](https://openjdk.org/jeps/461)) in Java provides a powerful way to create custom intermediate operations in stream pipelines. When creating a gatherer, the API offers two main factories: `Gatherer.of(...)` for gatherers that can be used in both sequential and parallel streams, and `Gatherer.ofSequential(...)` for those designed exclusively for sequential processing.

A common pattern for a sequential-only gatherer is to provide a combiner function---the third argument in `Gatherer.of(...)`---that simply throws an exception, as it's never expected to be called. This, however, is a signal that the gatherer is not truly parallel-capable.

This rule helps improve code clarity by guiding you to use the more specific `Gatherer.ofSequential(...)` factory in these cases. Doing so makes the intended processing model explicit, removes the need for a dummy or throwing combiner, and makes the code cleaner and easier to understand.

**Noncompliant Code Example:**

```
public static List<Integer> diffWithFirstPositive(List<Integer> list) {
  Gatherer<Integer, AtomicInteger, Integer> gatherer = Gatherer.of(
    () -> new AtomicInteger(-1),
    (state, number, downstream) -> {
      if (state.get() < 0) {
        state.set(number);
        return true;
      }
      return downstream.push(number - state.get());
    },
    (_, _) -> { // The combiner is never meant to be called
      throw new IllegalStateException();
    },
    Gatherer.defaultFinisher());
  return list.stream().gather(gatherer).toList();
}
```

In this code, the presence of a combiner that unconditionally throws an `IllegalStateException` is a clear indicator that the gatherer cannot function in a parallel stream.

**Compliant Solution:**

```
public static List<Integer> diffWithFirstPositive(List<Integer> list) {
  Gatherer<Integer, AtomicInteger, Integer> gatherer = Gatherer.ofSequential(
    () -> new AtomicInteger(-1),
    (state, number, downstream) -> {
      if (state.get() < 0) {
        state.set(number);
        return true;
      }
      return downstream.push(number - state.get());
    },
    Gatherer.defaultFinisher());
  return list.stream().gather(gatherer).toList();
}
```

By switching to `Gatherer.ofSequential`, the code becomes more obvious about its intent. It clearly communicates that the operation is sequential-only and eliminates the unnecessary and misleading throwing combiner, resulting in a cleaner implementation.

### **Rule S7482: Stateless gatherers should be created without a null initializer**

Stream Gatherers can be either stateful---maintaining a state across elements---or stateless, processing each element independently. For stateless gatherers, there is no need to initialize a state object. The `java.util.stream.Gatherer` API reflects this distinction by providing overloaded factory methods, including versions that do not take an `initializer` function.

When creating a stateless gatherer, it is a common mistake to use a factory method that requires an initializer and simply provide a dummy one, such as `() -> null`. This practice, while functional, makes the code less clear and fails to communicate the gatherer's stateless nature effectively.

This rule encourages the use of the correct factory method for stateless gatherers. By choosing the factory that omits the initializer, you make the stateless design explicit and your code more concise and readable.

**Noncompliant Code Example:**

```
private static Gatherer inRange(int start, int end) {
    return Gatherer.<Integer, Void, Integer>ofSequential(
      () -> null, // Noncompliant: unnecessary initializer for a stateless gatherer
      (_, element, downstream) -> {
        if (element >= start && element <= end)
          return downstream.push(element - start);
        return !downstream.isRejecting();
      },
      (_, downstream) -> downstream.push(-1)
    );
}
```

Here, the `() -> null` initializer serves no purpose other than to satisfy the signature of the factory method. This adds unnecessary boilerplate and obscures the fact that the operation does not depend on a state.

**Compliant Solution:**

```
private static Gatherer inRange(int start, int end) {
    return Gatherer.<Integer, Integer>ofSequential(
      (_, element, downstream) -> {
        if (element >= start && element <= end)
          return downstream.push(element - start);
        return !downstream.isRejecting();
      },
      (_, downstream) -> downstream.push(-1)
    );
}
```

The compliant solution uses the appropriate `Gatherer.ofSequential` overload that does not require an initializer. This removes the redundant code and clearly signals to anyone reading it that the gatherer is stateless by design.

By embracing the new features in Java 22, 23, and 24---such as unnamed variables and patterns, Markdown in Javadoc, the Class-File API, and Stream Gatherers---developers can write more efficient, and more maintainable code resulting in a higher-quality code.

However, staying abreast of these evolving language enhancements and consistently applying best practices can be challenging.

This is where tools like [SonarQube](https://www.sonarsource.com/products/sonarqube/), become invaluable. They provide automated checks that help ensure your code not only leverages these modern features correctly but also adheres to high-quality standards, ultimately improving code clarity and overall project quality.
