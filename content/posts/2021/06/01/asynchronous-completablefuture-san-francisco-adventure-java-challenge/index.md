---
title: "Asynchronous CompletableFuture Java Challenge"
date: "2021-06-01T07:42:43+00:00"
lastmod: "2021-08-23T12:35:13+00:00"
description: "Solving the limitations of the Future interface, we have the CompletableFuture API with methods to build reliable high-performant software."
authors:
  - "rafael-del-nero"
image: "completable_future-1024x801-1.png"
categories:
  - "Tutorials"
related_posts:
  - "function-calculation-java-challenge"
  - "neo-stream-search-java-challenge"
  - "jedi-lambda-join-java-challenge"
  - "soprano-ofnullable-stream-java-challenge"
frozen: false
---

The Completable Future feature is powerful for better performance when running asynchronous methods.

In Java 5, there is the Future interface, however, the Future interface doesn't have many methods that would help us to create robust code. To solve the limitations of the Future interface, we have the CompletableFuture API with methods that will help us to build reliable high-performant software.

Now that we had the intro about CompletableFuture, let's go to the Java Challenge!

It's time to improve your Java skills with this Completable Future San Francisco Adventure

## Completable Future San Francisco Adventure

In the following code, we are using a `CompletableFuture` invoking the `completeAsync` and `thenAccept` methods to perform actions asynchronously.

Can you guess what will happen when running the following code?

```java
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CompletableFutureChallenge {
  static ExecutorService executor = Executors.newCachedThreadPool();

  public static void main(String... oracleCodeOneAdventure) {
    CompletableFuture<List<String>> adventureStart = new CompletableFuture<>();

    Supplier<List<String>> sanFranSightSupplier = () ->
        List.of("Alcatraz", "Cable Car", "Golden Gate", "Lombard Street");

    adventureStart.completeAsync(sanFranSightSupplier, executor)
        .thenCompose(sights -> CompletableFuture.supplyAsync(() -> sights.stream()
                .map(String::length)
                .collect(Collectors.toList())))
        .thenAccept(ratings -> {
            var rating = ratings.stream()
                    .dropWhile(sightRating -> sightRating <= 12)
                    .findFirst()
                    .orElse(0);
            System.out.print("Rating: " + rating + " ");
        });

    System.out.print("time to go home :( ");
  } 

}
```

A) Rating: 14 time to go home 🙁  

B) time to go home 🙁 Rating: 12  

C) Rating: 0 time to go home 🙁  

D) time to go home 🙁 Rating: 14

**Warning: Try out the Java Challenge before seeing the answer to improve your skills most.**

**Explanation**:

Let's analyze the code: At first, we are populating a Supplier instance with a list:

```java
Supplier sanFranSightSupplier = () ->
List.of("Alcatraz", "Cable Car", "Golden Gate", "Lombard Street");
```

Then the whole process will be made asynchronously. Basically what will happen in this piece of code is that the stream of String from sanFranSightSupplier will be transformed into the length of each String.

```java
adventureStart.completeAsync(sanFranSightSupplier, executor)
.thenCompose(sights -> {
return CompletableFuture.supplyAsync(() -> sights.stream()
.map(String::length)
.collect(Collectors.toList()));
})
```

Then all Strings that are lower or equal to 12 will be dropped. In the end, the length of Lombard Street will remain on the list.

```java
.thenAccept(ratings -> {
var rating = ratings.stream()
.dropWhile(sightRating -> sightRating <= 12)
.findFirst()
.orElse(0);
System.out.print("Rating: " + rating + " ");
});
```

Therefore, the right alternative will be... what do you think? 🙂

If you want to watch the video explanation, check it out, but I recommend trying out the Java Challenge first:

{{< youtube Oy3qWysr4rA >}}

To see the original post, check out the following Java Challengers link:

<https://javachallengers.com/completable-future-san-francisco-adventure-java-challenge/>

That's it challenger, rock on! Keep taking action and relentlessly break your limits!
