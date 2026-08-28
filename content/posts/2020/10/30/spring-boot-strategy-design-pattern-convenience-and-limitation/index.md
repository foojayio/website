---
title: "Spring Boot: Strategy Design Pattern - Convenience and Limitation"
date: "2020-10-30T09:22:41+00:00"
lastmod: "2021-08-23T12:29:40+00:00"
description: "with a few changes in our code we can easily solve the problem of having different strategies which using the same keys in our code."
authors:
  - "karl-heinz-marbaise"
image: "Favicon-3-2.png"
categories:
  - "Spring"
related_posts:
frozen: true
---

You might have already used the [strategy pattern](https://en.wikipedia.org/wiki/Strategy_pattern) in relationship with Spring Boot where it is very convenient to use.

You simply define an interface for example (I use the prefixing `I` only in these examples for clarity):

```java
public interface IOneStrategy {
  void executeTheThing();
}
```

Define some implementations like this:

```java
@Service("FIRST")
public class OneStrategyFirst implements IOneStrategy {

  @Override
  public void executeTheThing() {
    System.out.println("OneStrategyFirst.executeTheThing");
  }
}
```

Now you can simply implement a service which will execute the appropriate strategy based on the given name which looks similar like this:

```java
@Service
public class ExecuteStrategyOne {
  private Map<String, IOneStrategy> strategies;

  public ExecuteStrategyOne(Map<String, IOneStrategy> strategies) {
    this.strategies = strategies;
  }

  public void executeStrategyOne(String name) {
    if (!strategies.containsKey(name)) {
      throw new IllegalArgumentException("The strategy " + name + " does not exist.");
    }
    strategies.get(name).executeTheThing();
  }

}
```

In real world you make several implementations of the strategy interface like `OneStrategyFirst`, `OneStrategySecond` and `OneStrategyThird`. Sometimes the usage is to use the parameter of `executeStrategyOne` which is provided by a REST API or some other domain specific code which needs different implementations.

The convenience here is that Spring Boot (Spring Framework to be more accurate) handles the injection of the different implementation into the `strategies` Map within `ExecuteStrategyOne` via the constructor. This results in a Map where the key is the value which is given by `@Service("FIRST")` and the value of the map contains an instantiates class of every implementation of the interface `IOneStrategy` which can be found.

Really convenient.

In real life it happens that you need to have a different strategy which use the same keys as `FIRST`, `SECOND` and `THIRD` in the examples? Let us define the following:

```java
@Service("FIRST")
public class TwoStrategyFirst implements ITwoStrategy {

  @Override
  public void executeTheThing() {
    System.out.println("TwoStrategyFirst.executeTheThing");
  }
}
```

If you try to start that Spring Boot application you will see an exception like this:

```java
Caused by: org.springframework.context.annotation.ConflictingBeanDefinitionException:
Annotation-specified bean name 'FIRST' for bean class [com.soebes.examples.strategies.functions.two.TwoStrategyFirst]
conflicts with existing, non-compatible bean definition of same name and class
[com.soebes.examples.strategies.functions.one.OneStrategyFirst]
  at org.springframework.context.annotation.ClassPathBeanDefinitionScanner.checkCandidate(ClassPathBeanDefinitionScanner.java:349) ~[spring-context-5.2.9.RELEASE.jar:5.2.9.RELEASE]
  at org.springframework.context.annotation.ClassPathB
```

So what can we do to solve the problem without losing much of the convenience which Spring Boot provides us here?

First we have to define in each of the strategy implementation class the annotations like this:

```java
@Service
@Qualifier("FIRST")
public class TwoStrategyFirst implements ITwoStrategy {

  @Override
  public void executeTheThing() {
    System.out.println("TwoStrategyFirst.executeTheThing");
  }
}
```

By using the key in a different annotation we prevent the duplication of the bean names in contradiction to use `@Service("FIRST")` instead. The usage of `@Qualifier("FIRST")` gives us a criteria to handle that different.

Now we have to change the `ExecuteStrategyOne` class like the following:

```java
@Service
public class ExecuteStrategyOne {

  private Map<String, IOneStrategy> strategies;

  public ExecuteStrategyOne(List<IOneStrategy> strategies) {
    this.strategies = strategies.stream()
        .collect(
            toMap(k -> k.getClass().getDeclaredAnnotation(Qualifier.class).value(),
                  Function.identity()));
  }
  ...
}
```

I would like to emphasis the usage of the constructor parameter `List<IOneStrategy> strategies` instead of the previously used `Map<String, IOneStrategy> strategies` which is a convenience to get a list of all implementations off the given interface into that list by Spring Boot. Now we need to translate that into a map with the key we have defined by using `@Qualifier` annotation. The whole thing can be solved by a stream like this:

```java
this.strategies = strategies
  .stream()
  .collect(
    Collectors.toMap(k -> k.getClass().getDeclaredAnnotation(Qualifier.class).value(),
                     Function.identity()));
```

We go through the implementations and extract the annotation `@Qualifier` and read out the `value()` which is the key we want to have. We collect the result by using the `Collectors.toMap` into a Map and assign the result to the instance variable `private Map<String, IOneStrategy> strategies;`. Depending on your need it is of course possible to define the instance variable as `final` and you can create an unmodifiable map by using the appropriate `Collectors.toUnmodifiableMap` instead of the `toMap(..)` if needed.

So with a few changes in the code we can easily solve the problem of having different strategies which using the same keys in our code.

The given code is available as a [full working example on GitHub](https://github.com/khmarbaise/article-spring-boot-convenience-and-limitation).
