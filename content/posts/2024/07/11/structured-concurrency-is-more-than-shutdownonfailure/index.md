---
title: "Structured Concurrency is More Than ShutdownOnFailure"
slug: "structured-concurrency-is-more-than-shutdownonfailure"
date: "2024-07-11T16:34:52+00:00"
lastmod: "2024-07-11T16:34:53+00:00"
description: "Let's see other possibilities than the default ones that can be done with the structured concurrency API."
authors:
  - "anthony-goubard"
image: "structured-concurrency-round.png"
categories:
  - "Java"
  - "Java Core"
  - "JDK21"
tags:
related_posts:
  - "lets-replace-the-synchronized-keyword"
  - "java-22-is-here-and-its-ready-to-rock"
  - "what-the-heck-is-project-loom-for-java"
  - "preparing-for-jdk-21-a-comprehensive-overview-of-key-features-and-enhancements"
frozen: false
---

**Since Java 21, structured concurrency has been added as a preview feature. Structured concurrency is a way to manage sub-tasks that are run in parallel within a given scope.**

If you've ever seen a presentation about [structured concurrency](https://docs.oracle.com/en/java/javase/21/core/structured-concurrency.html), you've probably seen the use of the `ShutdownOnSuccess` or `ShutdownOnFailure` classes.  

These classes will stop the scope and the still running sub-tasks within that scope when one of the sub-task succeeds or fails.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
     var student = scope.fork(() -> getStudent(studentID)); 
     var grades = scope.fork(() -> getGrades(studentID));

    scope.join();          
    scope.throwIfFailed();

    return student.get().getName() + " " + grades.get().getAverage();
} catch (Exception ex) {
    return ex.getMessage();
}
```

In project [**Virtually**](https://github.com/japplis/Virtually), I've extended the task scope to offer new possibilities.  

Virtually is an open-source project under the Apache License 2.0 to help projects to migrate to virtual threads friendly code.

## 1️⃣ Throttling

Virtual threads are great, you can create and run million of threads. With task scope, it's easy to make parallel calls to another web service or database but can it handle the load?  

You may not want to DDOS your system and send thousands of calls at the same time.  

In [`EnhancedTaskScope`](https://github.com/japplis/Virtually/blob/main/src/main/java/com/japplis/virtually/scope/EnhancedTaskScope.java), you can use the [`setMaxConcurrentTasks`](https://github.com/japplis/Virtually/blob/351de480d7e5d73f260031a201efa0ac9c02620d/src/main/java/com/japplis/virtually/scope/EnhancedTaskScope.java#L77) to specify the maximum of execution at the same time within this `StructuredTaskScope`. If the maximum is reached, tasks will wait until another sub-task finishes.

## 2️⃣ Circuit breaker

If suddenly many sub-tasks fail within the scope at the same time, it is more likely a problem somewhere and you may want to stop executing sub-tasks within that scope as there are very likely to fail too.  
`EnhancedTaskScope` has a method [setMaxConsecutiveFails](https://github.com/japplis/Virtually/blob/351de480d7e5d73f260031a201efa0ac9c02620d/src/main/java/com/japplis/virtually/scope/EnhancedTaskScope.java#L49). When consecutive failures have reached the maximum the whole task scope will stop.

## 3️⃣ Default value on failure

You may not want a task to fail and provide a default value if the submitted task fails.  
`EnhancedTaskScope` has a [forkWithDefault](https://github.com/japplis/Virtually/blob/351de480d7e5d73f260031a201efa0ac9c02620d/src/main/java/com/japplis/virtually/scope/EnhancedTaskScope.java#L110)`(Callable task, U defaultValue)`. This will never cause the sub-task to fail by providing a default return value if the `Callable` fails.

## 4️⃣ Critical tasks

In the provided `ShutdownOnFailure` scope, all tasks are critical and will fail the scope if one task fails.  

Among the sub-tasks submitted, some may be more critical than others. `EnhancedTaskScope` provides a [`forkCritical`](https://github.com/japplis/Virtually/blob/351de480d7e5d73f260031a201efa0ac9c02620d/src/main/java/com/japplis/virtually/scope/EnhancedTaskScope.java#L90) method that will fail the scope if the submitted task fails.

## 5️⃣ List conversion

Quite often parallel calls within a task scope are done for converting a list. For example, you have a list of student IDs and would like to get the students information.  
[ListTaskScope](https://github.com/japplis/Virtually/blob/main/src/main/java/com/japplis/virtually/scope/ListTaskScope.java) has been added to Virtually where you can provide a mapper to convert a list to another list using structured concurrency.  

As `ListTaskScope` extends `EnhancedTaskScope`, you also benefits of the previous features.

## 6️⃣ More ideas

`EnhancedTaskScope` and `ListTaskScope` can be extended to provide more features.  

For example:

* You may want to do throttling or circuit breaker based on a key, like the web service name. So this would work from multiple task scopes.
* You may want do logging or monitoring inside your task scope.
* If you're converting a list, you may want to do caching in your task scope in case the input list contains same values.
* You may want to execute a lambda to get the default value

## Conclusion

[StructuredTaskScope](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html) is a good class to extend to provide extended features when you want to execute multiple tasks in virtual threads.

```java
// A small demo to finish:
void listTaskScope() {
    List<Product> products = ShopFactory.createManyProducts(15_000);
    CallableFunction<Product, Double> productToPrice = (Product p) -> priceService.retreivePrice(p.id());
    try (ListTaskScope<Product, Double> scope = new ListTaskScope(productToPrice)) {
        scope.setMaxConsecutiveFails(50);
        scope.setMaxConcurrentTasks(1_000);
        for (Product product : products) {
            scope.convert(product);
        }
        Map<Product, Double> productWithPrices = scope.getResultsAsMap();
        List<Double> prices = scope.getResultsAsList();
        System.out.println("Size: " + productWithPrices.size() + " & " + prices.size());
    }
}
```
