---
title: "Playing practically with Stream API"
slug: "playing-practically-with-stream-api"
date: "2022-02-16T00:43:54+00:00"
lastmod: "2022-02-16T00:43:56+00:00"
description: "Let’s learn java stream API with a few practical examples."
authors:
  - "mainuls18"
image: "steve-bittinger-GXasHuHbDmY-unsplash-scaled.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "7-functional-programming-techniques-in-java-a-primer"
  - "book-review-java-by-comparison"
  - "journeys-in-java-level-1-building-an-empire-of-microservices"
  - "debugging-streams-and-collections"
enlighterjs: true
frozen: false
---

We can find many Stream API examples on Google, but most of them don't represent real life scenarios. Hence, it often remains a black box to understand the power of the Stream API for many rising programmers.

In this article, I tried to make scenarios based problems where I have tried to make the solution with Stream API. These were initially for my personal practice purposes, and later, I added some more practice problems to demonstrate in my class at Pondit.

I have added the boilerplate codes of Model classes and initialization [here](https://gist.github.com/mainul35/fc728c7397a73a538cc3df9142ddc353 "here"). Perhaps, "Cart" would be more appropriate rather than naming the class as "Order".

### Problem 1:

#### Find out 3 products with maximum ratings. Sort products by (i) product ratings. If more than 1 products have same number of ratings, (ii) sort by names

```java
var setOfProduct = customer1.getOrders()
       .stream()
       .map(Order::getItems)
       .flatMap(Collection::stream)
       .collect(Collectors.toSet());

   setOfProduct
       .stream()
       .sorted(comparing(Product::getRating)
           .reversed()
           .thenComparing(Product::getName))
       .limit(3)
       .forEach(System.out::println);
```

If we want to combine both in one go -

```
customer1.getOrders()
    .stream()
    .map(Order::getItems)
    .flatMap(Collection::stream)
    .distinct()
    .sorted(comparing(Product::getRating)
        .reversed()
        .thenComparing(Product::getName))
    .limit(3)
    .forEach(System.out::println);
```

**Note**: over here we've used distinct. The reason is, in our first attempt, we have first put the result in Set. The elements of the Set is always unique. In our second attempt, if we don't use distinct, then we will lose the unique property and the result will be different.

### Problem 2:

#### Find out 3 products with minimum ratings. Sort products by (i) product ratings. If 2 products have same amount of ratings, sort by names

Well, this is a practice problem to solve for the new learners.

### Problem 3:

#### Find out total orders of each specific kind of product

To find out the total orders first we need to concat all orders by all customers.

```java
var customer1Orders = customer1.getOrders()
        .stream()
        .flatMap(order -> order.getItems().stream())
        .toList();

    var customer2Orders = customer2.getOrders()
        .stream()
        .flatMap(order -> order.getItems().stream())
        .toList();

    System.out.println(
        "\n============== Find out total orders of each specific kind of product =============");

    Stream.concat(customer1Orders.stream(), customer2Orders.stream())
        .collect(Collectors.groupingBy(Product::getName, Collectors.counting()))
        .entrySet()
        .forEach(System.out::println);
```

### Problem 4:

#### Find out customer 1's favorite product. A favorite product is the maximum quantity a customer ordered of a specific product

```java
// TODO: 4. Find out customer 1's favorite product.
//  A favorite product is the maximum quantity a customer ordered of a specific product
System.out.println("\n============== Customer 1's favorite product =============");
var productsMap = customer1Orders
    .stream()
    .collect(Collectors.groupingBy(Product::getName, Collectors.counting()));

productsMap
    .entrySet()
    .stream()
    .sorted(Map.Entry.comparingByValue(reverseOrder()))
    .findFirst()
    .ifPresent(System.out::println);
```

Or maybe -

```java
productsMap
    .entrySet()
    .stream()
    .max(Map.Entry.comparingByValue())
    .ifPresent(System.out::println);
```

We can try it in one go -

```java
customer1Orders
    .stream()
    .collect(Collectors.groupingBy(Product::getName, Collectors.counting()))
    .entrySet()
    .stream()
    .max(Map.Entry.comparingByValue())
    .ifPresent(System.out::println);
```

### Problem 5:

#### Calculate the total cost of a order for a customer (customer 1, 2nd order)

```java
// TODO: 5. Calculate the total cost of a order for a customer (customer 1, 2nd order)
System.out.println(
    "\n============= Calculate the total cost of a order for a customer =============");
var total = customer1.getOrders().get(0)
    .getItems()
    .stream()
    .map(Product::getPrice)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

System.out.println(total);
```

### Problem 6:

#### Find out all books written by each author

```java
// TODO: 6. Find out all books written by each author
System.out.println("\n============= Find out all books written by each author =============");
products.stream()
    .collect(Collectors.groupingBy(Product::getAuthor, Collectors.toList()))
    .entrySet()
    .stream()
    .sorted(Map.Entry.comparingByKey())
    .forEach(System.out::println);
```

The full code in one file can be found [here](https://github.com/mainul35/pondit-b4/blob/main/src/com/pondit/b4/class30_31/StreamPractice.java "here").

**Acknowlegdement:**

1. Thanks to Shams Bin Sohrab vai for reviewing the code.
2. A big thanks goes to my respected senior brother, A.N.M. Bazlur Rahman, for reviewing and sharing different ideas to solve the problems.
