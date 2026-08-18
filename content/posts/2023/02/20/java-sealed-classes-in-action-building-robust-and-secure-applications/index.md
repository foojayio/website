---
title: "Java Sealed Classes: Building Robust and Secure Applications"
date: "2023-02-20T11:11:07+00:00"
lastmod: "2023-03-02T23:31:27+00:00"
description: "Sealed classes can help you create more robust and maintainable code by restricting the inheritance hierarchy of your classes and interfaces."
authors:
  - "bazlur-rahman"
image: "Untitled.png"
categories:
  - "Java Core"
  - "JEPs"
  - "Sealed Classes"
related_posts:
  - "jdk-15-sealed-classes"
  - "immutable-collections-in-java-with-sealed-types"
  - "the-state-of-pattern-matching-in-java-17"
frozen: false
---

Java sealed classes were introduced in Java 15 as a way to restrict the inheritance hierarchy of a class or interface.

A sealed class or interface restricts the set of classes that can implement or extend it, which can help prevent bugs and make code more maintainable.

Suppose you're building an e-commerce application that supports different payment methods, such as credit cards, PayPal, and Bitcoin. You could define a sealed class called PaymentMethod that permits various subclasses for each payment method:

```java
public sealed class PaymentMethod permits CreditCard, PayPal, Bitcoin {
    // Class members
}
```

In this example, PaymentMethod is a sealed class that permits CreditCard, PayPal, and Bitcoin to extend it. A sealed class can permit any number of classes to extend it by specifying them in a comma-separated list after the permits keyword.

There are plenty of other use cases where the sealed class can be used to make our life easy.

So let's go for a deep dive!

### Creating a Closed Type Hierarchy

Sealed classes can create a closed-type hierarchy, a limited set of classes that cannot be extended or implemented outside a particular package.

This ensures that only a specified set of classes can be used and prevents unwanted extensions or implementations.

```java
package ca.bazlur

public sealed class Animal permits Cat, Dog {
    // Class definition
}

public final class Cat extends Animal {
    // Class definition
}

public final class Dog extends Animal {
    // Class definition
}
```

In this example, Animal is a sealed class that only permits Cat and Dog to extend it.

Any other attempt to extend Animal will result in a compilation error.

### Creating a Limited Set of Implementations

Sealed classes can also create a limited set of implementations for a specific interface or abstract class. This ensures that the interface or abstract class owners can control and change the set of implementations.

```java
public sealed interface Shape permits Circle, Square {
    double getArea();
}

public final class Circle implements Shape {
    // Class definition
}

public final class Square implements Shape {
    // Class definition
}
```

In this example, Shape is a sealed interface that only permits Circle and Square to implement it.

This ensures that any other implementations of Shape cannot be created.

### Enhancing Pattern Matching in switch Statements

Sealed classes can also be used to enhance pattern matching in switch statements.

By limiting the set of subtypes that can extend a sealed class, developers can use pattern matching with exhaustive checks, ensuring that all possible subtypes are covered.

```java
public sealed abstract class PaymentMethod permits CreditCard, DebitCard, PayPal {
    // Class definition
}

public class PaymentProcessor {
    public void processPayment(PaymentMethod paymentMethod, double amount) {
        switch (paymentMethod) {
            case CreditCard cc -> {
                // Process credit card payment
            }
            case DebitCard dc -> {
                // Process debit card payment
            }
            case PayPal pp -> {
                // Process PayPal payment
            }

        }
    }
}
```

In this example, PaymentMethod is a sealed class that permits CreditCard, DebitCard, and PayPal to extend it.

The processPayment method in the PaymentProcessor class uses a switch statement with pattern matching to process different payment methods.

Using a sealed class ensures that all possible subtypes are covered in the switch statement, making it less error-prone.

### Implementing a State Machine

Sealed classes can be used to implement a state machine, a computational model that defines the behaviour of a system in response to a series of inputs. In a state machine, each state is represented by a sealed class, and the transitions between states are modelled by methods that return a new state.

```java
public sealed class State permits IdleState, ActiveState, ErrorState {
    public State transition(Input input) {
        // Transition logic
    }
}

public final class IdleState extends State {
    // Class definition
}

public final class ActiveState extends State {
    // Class definition
}

public final class ErrorState extends State {
    // Class definition
}
```

In this example, State is a sealed class that permits the extend of IdleState, ActiveState, and ErrorState.

The transition method is responsible for transitioning between states based on the input provided.

The use of sealed classes ensures that the state machine is well-defined and can only be extended with a limited set of classes.

### Creating a Limited Set of Exceptions

Sealed classes can also create a limited set of exceptions that can be thrown by a method. This can help enforce a consistent set of error conditions and prevent the creation of arbitrary exception types.

```java
public sealed class DatabaseException extends Exception permits ConnectionException, QueryException {
    // Class definition
}

public final class ConnectionException extends DatabaseException {
    // Class definition
}

public final class QueryException extends DatabaseException {
    // Class definition
}
```

In this example, DatabaseException is a sealed class that permits ConnectionException and QueryException to extend it.

This ensures that any exception thrown by a method related to a database operation is a well-defined type and can be handled consistently.

### Controlling Access to Constructors

Sealed classes can also control access to constructors, which can help enforce a specific set of invariants for the class.

```java
public sealed class Person {
    private final String name;
    private final int age;

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static final class Child extends Person {
        public Child(String name, int age) {
            super(name, age);
            if (age >= 18) {
                throw new IllegalArgumentException("Children must be under 18 years old.");
            }
        }
    }

    public static final class Adult extends Person {
        public Adult(String name, int age) {
            super(name, age);
            if (age < 18) {
                throw new IllegalArgumentException("Adults must be 18 years old or older.");
            }
        }
    }
}
```

In this example, a Person is a sealed class with two subclasses: Child and Adult.

The constructors for Child and Adult are marked as public, but the constructor for Person is marked as private, ensuring all Person instances are created through its subclasses.

This enables the Person to enforce the invariant that children must be under 18 years old and adults must be 18 years old or older.

### Improving Code Security

Sealed classes can also improve code security by ensuring that only trusted code can extend or implement them. This can help prevent unauthorized access to sensitive parts of the codebase.

```java
public sealed class SecureCode permits TrustedCode {
    // Class definition
}

// Trusted code
public final class TrustedCode extends SecureCode {
    // Class definition
}

// Untrusted code
public final class UntrustedCode extends SecureCode {
    // Class definition
}
```

In this example, SecureCode is a sealed class that only permits TrustedCode to extend it.

This ensures that only trusted code can access the sensitive parts of the codebase.

### Enabling Polymorphism with Exhaustive Pattern Matching

Sealed classes can also be used to enable polymorphism with exhaustive pattern matching.

By using sealed classes, developers can ensure that all possible subtypes are covered in a pattern-matching statement, enabling safer and more efficient code.

```java
public sealed class Shape permits Circle, Square {
    // Class definition
}

public final class Circle extends Shape {
    // Class definition
}

public final class Square extends Shape {
    // Class definition
}

public void drawShape(Shape shape) {
    switch (shape) {
        case Circle c -> c.drawCircle();
        case Square s -> s.drawSquare();
    }
}
```

In this example, Shape is a sealed class that permits Circle and Square to extend it.

The drawShape method uses pattern matching to draw the shape, ensuring that all possible subtypes of Shape are covered in the switch statement.

### Enhancing Code Readability

Sealed classes can also be used to enhance code readability by clearly defining the set of possible subtypes.

By limiting the set of possible subtypes, developers can more easily reason about the code and understand its behaviour.

```java
public sealed class Fruit permits Apple, Banana, Orange {
    // Class definition
}

public final class Apple extends Fruit {
    // Class definition
}

public final class Banana extends Fruit {
    // Class definition
}

public final class Orange extends Fruit {
    // Class definition
}
```

In this example, Fruit is a sealed class that permits Apple, Banana, and Orange to extend it.

This clearly defines the set of possible fruits and enhances code readability by making the code easier to understand.

### Enforcing API Contracts

Sealed classes can also be used to enforce API contracts, which are the set of expectations that consumers of an API have regarding its behavior.

By using sealed classes, API providers can ensure that the set of possible subtypes is well-defined and documented, improving the API's usability and maintainability.

```java
public sealed class Vehicle permits Car, Truck, Motorcycle {
    // Class definition
}

public final class Car extends Vehicle {
    // Class definition
}

public final class Truck extends Vehicle {
    // Class definition
}

public final class Motorcycle extends Vehicle {
    // Class definition
}
```

In this example, Vehicle is a sealed class that permits Car, Truck, and Motorcycle to extend it.

By using a sealed class to define the set of possible vehicle types, API providers can ensure that the API contract is well-defined and enforceable.

### Preventing Unwanted Subtype Extensions

Finally, sealed classes can also be used to prevent unwanted subtype extensions.

By limiting the set of possible subtypes, developers can prevent the creation of arbitrary subclasses that do not conform to the class's intended behavior.

```java
public sealed class PaymentMethod {
    // Class definition
}
```

```java
final class CreditCard extends PaymentMethod {
    // Class definition in the same file
}

final class DebitCard extends PaymentMethod {
    // Class definition in the same file
}

public class StolenCard extends PaymentMethod {
    // Class definition in another file
}
```

The `PaymentMethod` class is defined as `sealed`, which means that it cannot be extended beyond the classes defined within the file.

Two final classes, `CreditCard` and `DebitCard`, are defined as subtypes of `PaymentMethod`, indicating that they are the only allowed extensions of the `PaymentMethod` class.

However, a third class, `StolenCard`, is defined as a subtype `PaymentMethod` outside the file. Since the `PaymentMethod` class is sealed, it cannot be extended by this unauthorized subtype, and a compiler error will be generated.

### Enhancing Type Safety of Collections

Sealed classes can also enhance the type safety of collections, which are a fundamental part of the Java language.

By using sealed classes to define the set of possible elements in a collection, developers can ensure that the collection is type-safe and can enforce certain invariants.

```java
public sealed interface Animal permits Dog, Cat, Bird {
    // Interface definition
}

public final class Dog implements Animal {
    // Class definition
}

public final class Cat implements Animal {
    // Class definition
}

public final class Bird implements Animal {
    // Class definition
}
```

In this example, Animal is a sealed interface that permits Dog, Cat, and Bird to implement it.

By using sealed classes to define the set of possible animals, developers can ensure that a collection of animals is type-safe and can enforce certain invariants.

```java
List animals = List.of(new Dog(), new Cat(), new Bird());
```

In this example, animal is a List of elements that extend the Animal interface.

Because Animal is a sealed interface, the set of possible elements in the list is well-defined and type-safe.

### Facilitating API Evolution

Sealed classes can also facilitate API evolution, which is updating an API to add or remove features.

By using sealed classes to define the set of possible classes that can extend or implement a specific class or interface, developers can ensure that API changes are compatible with existing code.

```java
public sealed class Animal permits Dog, Cat {
    // Class definition
}

public final class Dog extends Animal {
    // Class definition
}

public final class Cat extends Animal {
    // Class definition
}

public final class Bird extends Animal {
    // Class definition
}
```

In this example, Animal is a sealed class that permits Dog and Cat to extend it.

Because Animal is a sealed class, adding a new subtype, Bird would be a breaking change and would require an API version bump.

This ensures that API changes are compatible with existing code and can help maintain the stability of the codebase.

Here are a few more concrete and real-life examples of how sealed classes can be used in Java development:

### Representing Different Types of Messages

In many distributed systems, messages pass data between different components or services.

Sealed classes can represent different types of messages and ensure that each type is well-defined and type-safe.

```java
public sealed interface Message permits RequestMessage, ResponseMessage {
    // Interface definition
}

public final class RequestMessage implements Message {
    // Class definition
}

public final class ResponseMessage implements Message {
    // Class definition
}
```

In this example, Message is a sealed interface that permits RequestMessage and ResponseMessage to implement it.

By using sealed classes, developers can ensure that each message type is well-defined and type-safe, which can help prevent bugs and improve the maintainability of the code.

### Defining a Set of Domain Objects

In domain-driven design, domain objects represent the concepts and entities in a business domain.

Sealed classes can define a set of domain objects and ensure that each object type is well-defined and has a limited set of possible subtypes.

```java
public sealed interface OrderItem permits ProductItem, ServiceItem {
    // Interface definition
}

public final class ProductItem implements OrderItem {
    // Class definition
}

public final class ServiceItem implements OrderItem {
    // Class definition
}
```

In this example, OrderItem is a sealed interface that permits ProductItem and ServiceItem to implement it.

By using sealed classes, developers can ensure that each domain object is well-defined and has a limited set of possible subtypes, which can help prevent the introduction of bugs and make the code more maintainable.

### Representing Different Types of Users

In many systems, users represent individuals who interact with the system somehow. Sealed classes can represent different types of users and ensure that each type is well-defined and type-safe.

```java
public sealed class User permits Customer, Employee, Admin {
    // Class definition
}

public final class Customer extends User {
    // Class definition
}

public final class Employee extends User {
    // Class definition
}

public final class Admin extends User {
    // Class definition
}
```

In this example, User is a sealed class that permits Customer, Employee, and Admin to extend it.

By using sealed classes, developers can ensure that each user type is well-defined and type-safe, which can help prevent bugs and make the code more maintainable.

### Defining a Limited Set of Error Types

In many systems, errors signal that something has gone wrong during the execution of a program.

Sealed classes can define a limited set of error types and ensure that each type is well-defined and has a limited set of possible subtypes.

```java
public sealed class Error permits NetworkError, DatabaseError, SecurityError {
    // Class definition
}

public final class NetworkError extends Error {
    // Class definition
}

public final class DatabaseError extends Error {
    // Class definition
}

public final class SecurityError extends Error {
    // Class definition
}
```

In this example, Error is a sealed class that permits NetworkError, DatabaseError, and SecurityError to extend it.

By using sealed classes to define a limited set of error types, developers can ensure that each error type is well-defined and has a limited set of possible subtypes, which can help make the code more maintainable and easier to reason about.

### Defining a Limited Set of HTTP Methods

In many web applications, HTTP methods interact with web resources such as URLs.

Sealed classes can define a limited set of HTTP methods and ensure that each method is well-defined and has a limited set of possible subtypes.

```java
public sealed class HttpMethod permits GetMethod, PostMethod, PutMethod {
    // Class definition
}

public final class GetMethod extends HttpMethod {
    // Class definition
}

public final class PostMethod extends HttpMethod {
    // Class definition
}

public final class PutMethod extends HttpMethod {
    // Class definition
}
```

In this example, HttpMethod is a sealed class that permits GetMethod, PostMethod, and PutMethod to extend it.

By using sealed classes to define a limited set of HTTP methods, developers can ensure that each method is well-defined and has a limited set of possible subtypes.

This can help make the code more maintainable and easier to reason about.

### Defining a Limited Set of Configuration Parameters

In many systems, configuration parameters are used to control the behaviour of a program.

Sealed classes can define a limited set of configuration parameters and ensure that each parameter is well-defined and has a limited set of possible subtypes.

```java
public sealed class ConfigurationParameter permits DebugMode, LoggingLevel {
    // Class definition
}

public final class DebugMode extends ConfigurationParameter {
    // Class definition
}

public final class LoggingLevel extends ConfigurationParameter {
    // Class definition
}
```

In this example, ConfigurationParameter is a sealed class that permits DebugMode and LoggingLevel to extend it.

By using sealed classes to define a limited set of configuration parameters, developers can ensure that each parameter is well-defined and has a limited set of possible subtypes.

This can help make the code more maintainable and easier to reason about.

### Defining a Limited Set of Database Access Strategies

In many systems, databases are used to store and retrieve data.

Sealed classes can define a limited set of database access strategies and ensure that each strategy is well-defined and has a limited set of possible subtypes.

```java
public sealed class DatabaseAccessStrategy permits JdbcStrategy, JpaStrategy, HibernateStrategy {
    // Class definition
}

public final class JdbcStrategy extends DatabaseAccessStrategy {
    // Class definition
}

public final class JpaStrategy extends DatabaseAccessStrategy {
    // Class definition
}

public final class HibernateStrategy extends DatabaseAccessStrategy {
    // Class definition
}
```

In this example, DatabaseAccessStrategy is a sealed class that permits JdbcStrategy, JpaStrategy, and HibernateStrategy to extend it.

By using sealed classes to define a limited set of database access strategies, developers can ensure that each strategy is well-defined and has a limited set of possible subtypes, which can help make the code more maintainable and easier to reason about.

### Defining a Limited Set of Authentication Methods

In many systems, authentication is used to verify the identity of users.

Sealed classes can define a limited set of authentication methods and ensure that each method is well-defined and has a limited set of possible subtypes.

```java
public sealed class AuthenticationMethod permits PasswordMethod, TokenMethod, BiometricMethod {
    // Class definition
}

public final class PasswordMethod extends AuthenticationMethod {
    // Class definition
}

public final class TokenMethod extends AuthenticationMethod {
    // Class definition
}

public final class BiometricMethod extends AuthenticationMethod {
    // Class definition
}
```

In this example, AuthenticationMethod is a sealed class that permits PasswordMethod, TokenMethod, and BiometricMethod to extend it.

By using sealed classes to define a limited set of authentication methods, developers can ensure that each method is well-defined and has a limited set of possible subtypes.

This can help make the code more maintainable and easier to reason about.

### Conclusion

In conclusion, Java sealed classes are a powerful feature that can help you create more robust and maintainable code by restricting the inheritance hierarchy of your classes and interfaces.

By limiting the set of permitted subclasses or implementers, you can prevent bugs and ensure that your code is more secure and easier to maintain.

By mastering Java-sealed classes, you can take your programming skills to the next level and build better software.
