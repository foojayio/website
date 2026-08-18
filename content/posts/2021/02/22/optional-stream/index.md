---
title: "Learning About Optional.stream() in OpenJDK and Java"
date: "2021-02-22T17:11:33+00:00"
lastmod: "2021-08-23T12:44:08+00:00"
description: "This week, I learned about a \"new\" feature of Optional that I want to share in this post. Available since Java 9, its novelty is relative."
canonical: "https://blog.frankel.ch/optional-stream/"
authors:
  - "nicolas-frankel"
image: "engineer-4690505_1280.jpg"
categories:
  - "Java Core"
tags:
related_posts:
  - "teeing-java-api"
  - "lets-use-optional-to-fix-method-contracts"
frozen: false
---

This week, I learned about a nifty "new" feature of `Optional` that I want to share in this post. It's available since Java 9, so its novelty is relative.

Let's start with the following sequence to compute the total price of an order:

```java
public BigDecimal getOrderPrice(Long orderId) {
    List<OrderLine> lines = orderRepository.findByOrderId(orderId);
    BigDecimal price = BigDecimal.ZERO;       // 1
    for (OrderLine line : lines) {
        price = price.add(line.getPrice());   // 2
    }
    return price;
}
```

1. Provide an accumulator variable for the price
2. Add each line's price to the total price

Nowadays, it's probably more adequate to use streams instead of iterations. The following snippet is the equivalent to the previous one:

```java
public BigDecimal getOrderPrice(Long orderId) {
    List<OrderLine> lines = orderRepository.findByOrderId(orderId);
    return lines.stream()
                .map(OrderLine::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

Let's focus on the `orderId` variable: it may be `null`.

The imperative way to handle `null` values is to check it at the beginning of the method - and eventually throw:

```java
public BigDecimal getOrderPrice(Long orderId) {
    if (orderId == null) {
        throw new IllegalArgumentException("Order ID cannot be null");
    }
    List<OrderLine> lines = orderRepository.findByOrderId(orderId);
    return lines.stream()
                .map(OrderLine::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

The functional way is to wrap the `orderId` in an `Optional`. This is what the code looks like using `Optional`:

```java
public BigDecimal getOrderPrice(Long orderId) {
    return Optional.ofNullable(orderId)                            // 1
            .map(orderRepository::findByOrderId)                   // 2
            .flatMap(lines -> {                                    // 3
                BigDecimal sum = lines.stream()
                        .map(OrderLine::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return Optional.of(sum);                           // 4
            }).orElse(BigDecimal.ZERO);                            // 5
}
```

1. Wrap the `orderId` in an `Optional`
2. Find relevant order lines
3. Use `flatMap()` to get an `Optional`; `map()` would get an `Optional<Optional>`
4. We need to wrap the result into an `Optional` to conform to the method signature
5. If the `Optional` doesn't contain a value, the sum is `0`

`Optional` makes the code less readable! I believe that readability should trump code style every single time.

Fortunately, `Optional` offers a `stream()` method (since Java 9). It allows to simplify the functional pipeline:

```java
public BigDecimal getOrderPrice(Long orderId) {
    return Optional.ofNullable(orderId)
            .stream()
            .map(orderRepository::findByOrderId)
            .flatMap(Collection::stream)
            .map(OrderLine::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

Here's the summary of the type at each line:

|                  Snippet                   |     Type     |
|--------------------------------------------|--------------|
| `Optional.ofNullable(orderId)`             | `Optional`   |
| `stream()`                                 | `Stream`     |
| `map(orderRepository::findByOrderId)`      | `Stream`     |
| `flatMap(Collection::stream)`              | `Stream`     |
| `map(OrderLine::getPrice)`                 | `Stream`     |
| `reduce(BigDecimal.ZERO, BigDecimal::add)` | `BigDecimal` |

Functional code doesn't necessarily mean readable code. With the last changes, I believe it's both.

**To go further:**

* [Option.stream() Javadoc](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Optional.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/optional-stream) on February 19^th^ 2021*
