---
title: "Make the life of your developer client's easier with smart builders"
date: "2025-01-09T11:49:51+00:00"
lastmod: "2025-01-16T08:35:27+00:00"
description: "We often hear the phrase \"Make the life of your client easier\". But, what about the developers? Make the life of your developer client's easier with smart builders."
authors:
  - "maximillian-arruda"
image: "Favicon-3-2.png"
categories:
  - "Java"
  - "Tutorials"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-ways-to-contribute-to-openjdk"
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**We often hear the phrase: "Make the life of your client easier". But, what about the developers? They are the ones who will be working with the code you write. It is important to make their lives easier too.**

Despite the target customer of your solution, you'll be creating code that will be maintained by other developers and, sometimes, it may include you as well. Choosing good approaches and techniques will make their life, or your life, easier probably.

Being an effective developer is not only about writing code that works, but also about writing code that is easy to read, easy to understand, and easy to maintain.

Talk about good practices and technics is a long conversation and, I'm not here to tell you what you must or not do. I believe that the best approach depends on the context.

In this content, we'll discuss how to make the life of developers easier by using good strategies to build complex objects.

### The scenario: The complex object

A good way to learn things is by examples. So, here is our challenge:

We need to create a `Notification` object that has the following mandatory attributes:

* `title`: the title of the notification;
* `message`: the content of the notification;
* `recipient`: the person who will receive the notification;

and the optional attributes:

* `highPriority`: a flag to indicate if the notification is high priority. Default is `false`;
* `type`: the enum type of the notification. Supported values: `GENERAL`, `INFO`, `WARNING`, `ERROR`. Default is `Type.GENERAL`;
* `attachment`: a string with the path to the attachment file. Default is `null`;

Also, let's define more requirements:

* `title`: it's required and cannot be null;
* `message`: it's required and cannot be null;
* `recipient`: it's required and cannot be null;
* `highPriority`: it's optional and cannot be null;
* `type`: it's optional and cannot be null;

Here's an initial definition for our `Notification` class:

```java
public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean isHighPriority; // optional
    private Type type; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    // omitted getters
}
```

Some developers would argue: "We can use the default constructor and setters to set the optional attributes". Let's try to follow this argument:

```java
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods
}
```

Analyzing the code above, we can see that the developer must call the setters to set the attributes. The code to create a `Notification` object would be like this:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification();
        notification.setTitle("New message");
        notification.setMessage("Hello, world!");
        notification.setRecipient("[email protected]");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

Let's analyze the code above, we can highlight some drawbacks and issues with this approach:

1. **`Notification` objects can be instantiated with invalid state**: it violates the constraints of our challenge.
2. **`Notification` objects are not thread-safe**: the setters can be called by multiple threads at the same time, causing race conditions issues in multithreaded applications.
3. **The developer must call the setters to set the mandatory and optional attributes** : it's verbose and **error-prone** because the developer can forget to set an attribute, keeping the object in an invalid state;
4. **It's not clear**: the developer must read the documentation to know which attributes are mandatory and which are optional;

Also, we can highlight some good things on this approach: **developers are able to call the setters of the optional attributes as they need**.

Let's try to address the issues above.

1. **`Notification` objects can be instantiated with invalid state**: it violates the constraints of our challenge.

Okay, you would say: "*It's not a big deal! We can use the constructor to set the mandatory attributes and the setters to set the optional attributes*". Let's try to follow this argument:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification(String title, String message, String recipient) {
        setTitle(title);
        setMessage(message);
        setRecipient(recipient);
    }

    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods
}
```

Let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification("New message", "Hello, world!", "[email protected]");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

It looks like we solved the first issue, right? Well, let's analyze the code again:

With the changes the `Notification` objects will be instantiated with non-null references for the mandatory attributes. But, we still have some issues: **the `Notification` constructor with all mandatory attributes is error-prone!** Now, developers can set the attributes in the wrong order. For example, the developer can set the `recipient` before the `title`, which invalidate the object state making the fields holds the wrong value. Such issue only can be caught by debugging process or output derived from these invalid `Notification` objects.

Maybe two or three arguments with the same type is not a big deal, but in the perspective of the developers whose will use our class it's not something easier to deal with, and what if we have more attributes? The constructor will become even more complex and error-prone.

Let's continue trying to address the second issues:

1. **`Notification` objects are not thread-safe**: the setters can be called by multiple threads at the same time, causing race conditions issues in multithreaded applications.

The easiest way to make the `Notification` objects thread-safe is to make the getters and setters `synchronized` by using `Locks` from the `java.util.concurrent.locks` package. It would work, but it has some drawbacks to deal with:

* **It's not scalable and error-prone** : if it's needed to add more attributes, developers must have to make sure that any write and read state will be `synchronized`, which can lead to performance issues and undesired threads deadlock;
* **It's verbose**: the developer must write a lot of code to make the object thread-safe.
* **It's not clear**: the developer must read the documentation to know which attributes are mandatory and which are not.
* **It's not efficient**: synchronizing access to read and write data can cause performance issues in multithreaded applications.

All of these concerns are needed just because this class that instantiate mutable objects. If your scenario requires mutable objects then it makes sense to put effort to deal with all of these concerns. Otherwise, if your scenario allows you to use immutable objects, then you can avoid all of these concerns.

That's the reason why we must get clarity about the requirements of your solution before start coding. It will help you to choose the best approach to solve the problem. Let's come back to our `Notification` class and try to make it immutable.

Immutable objects are thread-safe by nature because they cannot be modified after creation. It's a good practice to make your objects immutable whenever possible.

We could use specific constructors to make the `Notification` object immutable. See below:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private final String title; // mandatory
    private final String message; // mandatory
    private final String recipient; // mandatory
    private final boolean highPriority; // optional
    private final Type type; // optional
    private final String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification(String title,
                        String message,
                        String recipient,
                        boolean highPriority,
                        Type type,
                        String attachment) {
        this.title = requireNonNull(title, "title is required");
        this.message = requireNonNull(message, "message is required");
        this.recipient = requireNonNull(recipient, "recipient is required");
        this.highPriority = highPriority;
        this.type = ofNullable(type).orElse(Type.GENERAL);
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods
}
```

Now, let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification(
                "New message",
                "Hello, world!",
                "[email protected]",
                true,
                Notification.Type.INFO,
                "/path/to/attachment.txt");
    }
}
```

Now, the `Notification` objects are immutable and thread-safe. The developer can instantiate the object with all mandatory and optional attributes in a single line of code. The object will be created in a valid state, and the developer cannot change its state after creation.

Since Java 16, we can use the `record` keyword to create immutable objects. If you're using Java 16 or above, I highly recommend you to use Java Records to create immutable objects. Let's see how we can refactor the `Notification` class to become a Java Record:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Less code, more readability, and more maintainability. That's the power of Java Records.

Coming back to our challenge, something is still not right: if `highPriority`, and `type`, and `attachment` are optional attributes, why it's required to provide each value in the constructor?

We can solve this issue by using a traditional approach that it's called **Telescoping constructors**.

### The traditional approach: Telescoping constructors

A common approach to object creation is to provide multiple constructors with different numbers of parameters. Each constructor calls application constructor with the required parameters and sets the optional parameters to default values. It's called **telescoping constructors**. You can use this approach on any java class, including Java Records.

Let's try to follow this approach. Let's see them.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification (String title, String message, String recipient) {
        this(title, message, recipient, false, Type.GENERAL, null);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Now developers will be able to create `Notification` objects with only the mandatory attributes. The optional attributes will be set to default values. The `Notification` objects are immutable and thread-safe.

Let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        var notificationWithDefaultOptionalValues =
                new Notification(
                        "New message",
                        "Hello, world!",
                        "[email protected]");

        var notificationWithCustomOptionalValues =
                new Notification(
                        "Another message",
                        "Oh no! Something wrong happened",
                        "[email protected]",
                        true,
                        Notification.Type.ERROR,
                        "/path/to/attachment.txt");
    }
}
```

Great! Let's review the issues we had and how we solved them:

1. **`Notification` objects only can be instantiated with valid state**: the constructors ensure that the object will be created in a valid state;
2. **`Notification` objects are thread-safe**: the immutability ensure to us this capability.

Well, the third one about the verbosity and error-prone of the constructors is still there yet. The fourth issue item, about the documentation, can help developers to know which constructor should be used with its argument ordering, but we can do more to make it easier.

### Favor Static Factory Methods over Class Constructors

To help developers to know which constructor should be used, we can use static method factories to create objects.

Static factory methods are static methods that return an instance of a given class or its subtype. They can have names that describe the object being returned, making it easier for developers to know which constructor should be used.

Maybe, you have been listened about the "Factory Method" pattern of the book Design Pattern (Gang of Four) before, but the Static Factory Method is not an implementation of this pattern directly. They purpose can be the same, but there is no equivalent pattern in the Gang of Four book to the Static Factory Methods, actually.

Some advantages of using static factory methods are:

* **Static factory methods have names that describe the object being returned**: the developer can know which constructor should be used by reading the method name;
* **Static factory methods don't need to create a new object on even invocation**: it can return the same object if the object is immutable saving memory and CPU resources;
* **Static factory methods can return any object of subtype from the own return type**: it can return a subtype of the class, making it easier to create objects with different configurations;
* **Static factory methods can return different objects depending on the provided input arguments**: different of constructors only return the instance of the class, creating a new instance every time they're called, the static factory methods can apply specifics logics and return objects of the type requested or its subtypes.

Before to put our finger in the code, let's think about how to apply the static factory methods on our challenge. According to the static method factory concept we can create a static method factory for each combination of attributes. It even will make the developer's life easier, but how do we should implement these static factory methods?

In fact, for our challenge, if we concentrate to provide static factory methods for all possible combinations, it would be resulting in a big class with many static factory methods. It's about 16 variations of static factory methods! Maybe it would be not a good idea to have many static factories methods in a class. Maybe it makes the class harder to maintain and understand. Let's change our point of view: instead of cover statically all possible combinations, we can provide static factory methods with the attributes that would be composing possible combinations. Let's see them.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@lombok.Builder
public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public static Notification createNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, false, type, null);
    }

    public static Notification createHighPriorityNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, true, type, null);
    }

    public static Notification createNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, false, type, attachment);
    }

    public static Notification createHighPriorityNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, true, type, attachment);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

}
```

Now, developers can create `Notification` objects using static factory methods. The static factory methods have names that describe the object being returned, making it easier for developers to know which constructor should be used. The `Notification` objects are immutable and thread-safe.

Let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        var generalNotificationWithoutAttachment = Notification
                .createNotification(
                        Notification.Type.GENERAL,
                        "General Notification",
                        "This is a general notification",
                        "[email protected]");

        var highPrioryInfoNotification = Notification
                .createHighPriorityNotification(
                        Notification.Type.INFO,
                        "High Priority Info Notification",
                        "This is a high priority info notification",
                        "[email protected]");
    }
}
```

Great! We're improving our code step by step. Maybe it's even good shape for some cases already, but I'm sure that we can do better!

In our implementation, each static method factory is requiring four arguments. It's not a big deal, but what if we have more attributes? The static factory methods will become even more complex and error-prone. Let's try to address this issue.

### Many parameters? Use the Builder pattern

The Builder pattern is a creational design pattern that allows you to construct complex objects step by step. It's useful when you have many optional attributes in your class and you want to make the object creation more readable and maintainable.

Some libraries like Lombok, or plugins of IDEs like IntelliJ IDEA, can generate the Builder pattern for you. That's amazing, but we need to understand how it works to be able to use it effectively.

Let's see how we can implement the Builder pattern for our `Notification` class using Lombok for example:

```java
import lombok.Builder;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Builder
public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Behind of scenes, Lombok will generate to you all the builder class for the `Notification` class. At the end, we will have a similar result like below:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static NotificationDataBuilder builder() {
        return new NotificationDataBuilder();
    }

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static class NotificationBuilder {
        private String title;
        private String message;
        private String recipient;
        private boolean highPriority;
        private Type type;
        private String attachment;

        NotificationBuilder() {
        }

        public NotificationDataBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationDataBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationDataBuilder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationDataBuilder highPriority(boolean highPriority) {
            this.highPriority = highPriority;
            return this;
        }

        public NotificationDataBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public NotificationDataBuilder attachment(String attachment) {
            this.attachment = attachment;
            return this;
        }

        public Notification build() {
            return new Notification(title, message, recipient, highPriority, type, attachment);
        }
    }
}
```

Lombok provides many annotations to generate boilerplate code for you. The `@Builder` annotation generates a builder class for the annotated class. The generated builder class by Lombok has a fluent interface where developers can call in chaining way the methods to set the attributes of the annotated class and a `build()` method to create an instance of the annotated class.

Builders can be implemented in many ways, like using the traditional setter style, but it's commonly implemented following the **Fluent API** design style.

The **Fluent API** is a design style is enfatize by the method chaining. It allows developers to call methods in a chaining way, making the code more readable and maintainable. It's used in many libraries, frameworks and APIs to improve the developer experience. It can be applied to the Builder pattern for sure, but it's not limited to this use. It can be used in **DSLs (Domain-Specific Languages)** and many other contexts.

Particularly I prefer to have these classes explicitly in my code. It helps me to understand how the Builder pattern works and, the most important in my opinion: it free the developers to have Lombok configured on its IDEs. One less dependency to worry about!

Let's see the flexibility that the Builder pattern provides to developers:

```java
public class NotificationProgram {
    public static void main(String[] args) {
        var generalNotification = Notification.builder()
                .title("Hello")
                .message("Hello World")
                .recipient("[email protected]")
                .build();

        // do something with generalNotification

        var highPriorityInfoNotificationWithAttachment = Notification.builder()
                .title("Hello")
                .message("Hello World")
                .recipient("[email protected]")
                .type(Type.INFO)
                .highPriority(true)
                .attachment("attachment.pdf")
                .build();

        // do something with highPriorityInfoNotificationWithAttachment;
    }
}
```

Now, developers can create `Notification` objects using the Builder pattern. The Builder pattern allows developers to construct complex objects step by step, making the object creation more readable and maintainable.

Well, such builder like that may even help some developers to get their life easier, it means, the developers whose are creating the builder actually, but what about the developers whose will go to use the builder?

What do you mean with that? - you may get to ask. It's a great question!

Before to add the builder solution in the `Notification` class, developers whose are using our class must pass the required arguments to the static factories methods to create `Notification` objects. The Java compiler will enforce the developer to pass the mandatory and required arguments to the static factory methods to create `Notification` objects with valid state. Our builder solution doesn't provide this capability. Look the code below:

```java
public class NotificationProgram {
    public static void main(String[] args) {
        var anotherNotification = Notification.builder()
                .recipient("[email protected]")
                .build();
    }
}
```

You may say: "*It is not a big problem! The class will respect their constraints and no invalid instance will be created! It will throw exceptions to the caller!*". Well, it's true but such exceptions will be thrown in runtime only. It's not a good for anyone!

Runtime exceptions explode in production and affect the image and perception of the final customer of the solution. It'll require a smart way to handle these scenarios and it would force developers to spread error handling logic on each point that it's using that code. It's not a good practice!

In summary, compilation or runtime errors still showing that there are issues in the solution, but compilation errors help developers to discover issues in compile time, which is better! Let's try to use the Builder pattern to enforce the constraints of the `Notification` class in compile time.

### Restricts the order of method calls in the Builder pattern

The Builder pattern allows developers to construct complex objects step by step. The Builder pattern can be used to enforce the constraints of the class in compile time.

Our Builder implementation doesn't restrict the order of method calls. The developer can call the methods in any order, which can lead to invalid objects. It happens because the `NotificationBuilder` expose all the attributes to be set by the developer. We can restrict the order of method calls by using the **Step Builder pattern**.

First, let's break down the `NotificationBuilder` in multiple steps. Each step will be responsible for setting a specific group of attributes. Here is our plan:

* Let's ensure that `title`, `message`, and `recipient` are set in this specific order; Sometimes it's important to follow a predefined order during an object instantiation. That's not our case by the way. However, for learning purposes, let's do it on this way. Once these mandatory attributes are set, let's allow developer be able to build the `Notification` object with the default values for the optional attributes, or...
* Let's allow developers to set `highPriority`, `type` and `attachment` in any order. As these attributes are optional we must allow developers be able to build the `Notification` object any time at this point;

Let's see them:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification(String title, String message, String recipient) {
        this(title, message, recipient, false, null, null);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }
}
```

Now, developers must follow the order of method calls to create a `Notification` object. The `NotificationBuilderWithTitle` class is responsible for setting the `title` attribute. The `NotificationBuilderWithTitleMessage` class is responsible for setting the `message` attribute. The `NotificationBuilderWithTitleMessageRecipient` class is responsible for setting the `recipient` attribute. The `NotificationBuilderWithTitleMessageRecipient` class has a `build()` method to create a `Notification` object. Let's highlight some points:

1. **All the objects created by the building process are thread-safe** , which means, developers can create `Notification` objects in a multithreaded environment without any issues;
2. **The methods provided by the builder objects are named** , which means, developers can know which method should be called next to create a `Notification` object;
3. **The order of method calls is enforced by the builder pattern** , which means, developers must follow the order of method calls to create a `Notification` object and the compiler will enforce this constraint;
4. **This builder provides a fluent API** , which means, developers can create `Notification` objects in a readable and maintainable way.

Great! Let's continue to implement the optional attributes. Let's see them:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification(String title, String message, String recipient) {
        this(title, message, recipient, false, null, null);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore addMore() {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient
            );
        }
    }

    public record NotificationBuilderWithTitleMessageRecipientAndMore(String title,
                                                                      String message,
                                                                      String recipient,
                                                                      Type type,
                                                                      boolean highPriority,
                                                                      String attachment) {

        public NotificationBuilderWithTitleMessageRecipientAndMore(String title, String message, String recipient) {
            this(title, message, recipient, null, false, null);
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore highPriority(boolean highPriority) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    this.type,
                    highPriority,
                    this.attachment
            );
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore attachment(String attachment) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    this.type,
                    this.highPriority,
                    attachment
            );
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore type(Type type) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    type,
                    this.highPriority,
                    this.attachment
            );
        }

        public Notification build() {
            return new Notification(
                    this.title,
                    this.message,
                    this.recipient,
                    this.highPriority,
                    this.type,
                    this.attachment
            );
        }
    }

}
```

Now, developer can create `Notification` objects defining the optional attributes in any order. The `NotificationBuilderWithTitleMessageRecipientAndMore` class is responsible for setting the optional attributes. Also, at this point, developers can set the optional attributes or call the `build()` method to create a `Notification` object arbitrarily. Let's highlight some points:

1. **This builder allows developers to set the optional attributes in any order** , which means, developers can create `Notification` objects with the optional attributes in any order;
2. **The builder allows developers to create `Notification` objects arbitrarily** , which means, developers can set the optional attributes or create the `Notification` object at any point in the building process;

Let's update the `NotificationProgram` that creates a `Notification` object using the Builder pattern:

```java
import notification.Notification;

public class NotificationProgram {

    public static void main(String[] args) {
        var generalNotification = Notification.builder()
                .title("Another title")
                .message("Another message")
                .recipient("[email protected]")
                .build();

        // do something with generalNotification

        var highPriorityWarningNotification = Notification.builder()
                .title("Warning title")
                .message("Attention people!")
                .recipient("[email protected]")
                .addMore()
                .highPriority(true)
                .type(Notification.Type.WARNING)
                .build();

        // do something with highPriorityWarningNotification

        var highPriorityErrorNotificationWithAttachment = Notification.builder()
                .title("Warning title")
                .message("Attention people!")
                .recipient("[email protected]")
                .addMore()
                .type(Notification.Type.ERROR)
                .attachment("error.log")
                .highPriority(true)
                .build();

        // do something with highPriorityErrorNotificationWithAttachment
    }

}
```

This builder implementation go beyond the traditional Builder pattern.  

As we can see, in the previous code, developers can create `Notification` objects with the optional attributes in any order and, at the same time, it enforces the constraints of the `Notification` class in compile time, making the object creation more readable and maintainable.

As Ben Parker used to say - "With great powers come great responsibilities" - be implementing the Builder pattern on that way will make the code complex, making it harder to understand and change, probably. It's a trade-off that you must consider when using the Builder pattern.

Once you have to deal with many attributes to create objects, the Builder pattern can be a good choice to create objects with many optional attributes. As the builder is getting help from the compiler, refactoring the code will be easier and safer.

### Conclusion

In this content, we discussed some approaches to create objects with many optional attributes. We started with the traditional approach, using constructors and setters to create objects. We saw that this approach can lead to invalid objects, thread-safety issues, and verbose code. We then explored some approaches like:

* Telescope constructors;
* Static Method Factory;
* Builder pattern;
* Fluent API design style;
* Step Builder pattern.

All the approaches have their pros and cons. The telescoping constructors approach can solve some scenarios, but it may be error-prone and verbose when dealing with many attributes.

The Static Method Factory can offer a good alternative to build objects when few attributes are required.

The Builder pattern allows developers to construct complex objects step by step using the Fluent API design style providing a fluent interface, making the object creation more readable and maintainable and, the Step Builder pattern can be used to enforce the constraints of the class in compile time. In the end, we were able to see how these approaches can help developers to get their life easier when creating objects with many attributes.

### Key Takeaways

* Making the lives of developers easier may be as important as making the lives of the final customers easier;
* The **Telescoping Constructors** approach can solve some scenarios, but it may be error-prone and verbose when dealing with many attributes;
* The **Static Method Factory** can offer a good alternative to build objects when few attributes are required;
* The **Builder pattern** allows developers to construct complex objects;
* The **Fluent API** design style can help developers to create specialized code focused in method chaining improving the developer experience. It's normally used to express domain-specific languages. In our context, it was used to create a builder easier to use, allowing developers to create objects in a readable and maintainable way;
* The **Step Builder pattern** is a variation of the Builder pattern. As a Builder's variant, it allows developers to create complex objects by setting the attributes following a predefined order.

### Final Thoughts

I hope you enjoyed this content! If you have any questions or feedback, please feel free to reach out. I'd love to hear from you!

Many Java open-source projects brings these approaches to create objects with many optional attributes. Lombok, for example, provides the `@Builder` annotation to generate the Builder pattern for you, but it's important to understand how it works to be able to use it effectively.

The design patterns and code styles can be mixed and matched as needed to solve the problem at hand. **There is no silver bullet in software development**. Each approach has its pros and cons and nobody is better than you to know which one is the best for your scenario.

To see a good example that use some of these technics in action take a look at the Eclipse JNoSQL project, at [org.eclipse.jnosql.mapping.semistructured.AbstractSemiStructuredTemplate](https://github.com/eclipse-jnosql/jnosql/blob/ecf992ba9bb6aaf2f816e9e21802258c2037736c/jnosql-mapping/jnosql-mapping-semistructured/src/main/java/org/eclipse/jnosql/mapping/semistructured/AbstractSemiStructuredTemplate.java#L295) class on the `QueryMapper.MapperFrom select(Class type)` method. It uses a Fluent API design style to help users to perform queries to retrieve data from semi-structured database implementations.

If you want to learn more about the Builder pattern, I recommend the following resources:

* [Fluent-API: Creating Easier, More Intuitive Code With a Fluent API by Otavio Santana](https://dzone.com/articles/java-fluent-api)
* [Effective Java - Item 1: Consider Static Factory Methods Instead Of Constructors](https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997)
* [Effective Java - Item 2: Consider a builder when faced with many constructor parameters](https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997)
* [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612)
* [Builder Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/builder)
* [Builder Pattern - Wikipedia](https://en.wikipedia.org/wiki/Builder_pattern)

Also, I'd like to recommend you put these approaches in practices day-by-day. It will help you to understand when to use each one and how to apply them effectively.

Did you like this content? If so, please share it with your friends and colleagues. Also, don't forget to follow me on social media to stay up to date with the latest content and updates.

See you in the next content!
