---
title: "Java’s Structured Concurrency: Finally Finding Its Footing"
slug: "javas-structured-concurrency-finally-finding-its-footing"
date: "2025-06-04T12:21:07+00:00"
lastmod: "2025-06-05T07:15:42+00:00"
description: "Explore the refined Java Structured Concurrency API. Learn about new task scopes, joiners, and improved error handling for cleaner, more robust concurrent code."
canonical: "https://bazlur.ca/2025/05/25/javas-structured-concurrency-finally-finding-its-footing/"
authors:
  - "bazlur-rahman"
image: "shutterstock_1111260050-350x233-1.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-64"
  - "java-21-is-available-today-and-its-quite-the-update"
  - "java-23-has-arrived-and-it-brings-a-truckload-of-changes"
  - "java-24-rolls-out-today-find-out-why-its-aptly-named"
enlighterjs: true
frozen: false
---

The structured concurrency API changed again after two incubations and four rounds of previews.

Ideally, this scenario is unexpected.

However, given its status as a preview API, such changes can occur, as was the case here.

These changes lend considerable maturity to the API, and I am hopeful it will now stabilize without requiring further modifications.

### **What Actually Changed This Time** {#h3-0-what-actually-changed-this-time}

When I first started working with structured concurrency back in its incubation phase, I was excited about the promise of cleaner concurrent code.

The idea was simple: treat concurrent tasks like a structured block, where all spawned tasks complete before the block exits. It sounded perfect in theory, but the API continued to evolve, making it a bit frustrating to keep up with the changes.

The latest iteration in [JEP 505](https://openjdk.org/jeps/505) brings some significant refinements that I believe finally put this feature on solid ground. The most notable change is the introduction of more flexible task handling and better integration with virtual threads. This article will detail the differences and explain the significance of these changes.

### **The Core Concept Remains Strong** {#h3-1-the-core-concept-remains-strong}

Before diving into the changes, let's establish what structured concurrency is trying to solve. In traditional concurrent programming, we often end up with scattered task management:

```
import java.util.Random;
import java.util.concurrent.*;

public class TraditionalConcurrencyExample {
  private static final Random random = new Random();

  private static String fetchUserData(String userId) throws InterruptedException {
    Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
    if (random.nextBoolean()) {
      throw new RuntimeException("User service unavailable");
    }
    return "UserData[" + userId + "]";
  }

  private static String fetchUserPreferences(String userId) throws InterruptedException {
    Thread.sleep(800 + random.nextInt(1500)); // 0.8-2.3 seconds
    if (random.nextBoolean()) {
      throw new RuntimeException("Preferences service down");
    }
    return "Preferences[" + userId + "]";
  }

  private static String combineUserInfo(String userData, String preferences) {
    return userData + " + " + preferences;
  }

  public static String getUserInfoTraditional(String userId) throws Exception {
    try (ExecutorService executor = Executors.newCachedThreadPool()) {
      Future<String> future1 = executor.submit(() -> fetchUserData(userId));
      Future<String> future2 = executor.submit(() -> fetchUserPreferences(userId));

      try {
        String userData = future1.get();
        String preferences = future2.get();
        return combineUserInfo(userData, preferences);
      } catch (Exception e) {
        // Cleanup is messy - what about the other task?
        System.out.println("Error occurred, attempting cleanup...");
        future1.cancel(true);
        future2.cancel(true);
        throw e;
      }
    }
  }

  void main() {
    for (int i = 0; i < 5; i++) {
      try {
        System.out.println("Attempt " + (i + 1) + ": " +
            getUserInfoTraditional("user123"));
      } catch (Exception e) {
        System.out.println("Attempt " + (i + 1) + " failed: " +
            e.getMessage());
      }
      System.out.println();
    }
  }
}
```


When you run this code, several issues typically emerge:

* **Complex error handling:** If one task fails, we must manually cancel the other task. Otherwise, it will continue running despite no longer being required, leading to resource leakage.
* **Thread lifecycle management:** You are responsible for the entire lifecycle of the threads.
* **Exception propagation:** Checked exceptions tend to get wrapped awkwardly.
* **No guarantee of cleanup:** If the main thread exits unexpectedly, tasks might continue running.

Structured concurrency aims to resolve these challenges.

### **The headline change: static factory methods** {#h3-2-the-headline-change-static-factory-methods}

The most obvious tweak in JEP 505 is that you no longer call `new StructuredTaskScope<>()`. You `open()` one instead:

```
try (var scope = StructuredTaskScope.open()) {
  // ...
}
```


The zero-argument open() returns a scope that waits for all subtasks to succeed or any to fail---the default "all-or-fail" policy. If you need something fancier, call the overloaded open(joiner) variant and supply a custom completion policy via a Joiner (more on that in a minute). Why the factory? It packages sensible defaults and, critically, gives the implementation room to evolve without breaking your code. I find this change beneficial: using a single keyword is more concise, and it reduces potential complications.

Now let's rewrite the previous example with the new API:

```
public static String getUserInfoTraditional(String userId) throws Exception {
  try (var scope = StructuredTaskScope.open()) {
    StructuredTaskScope.Subtask<String> task1 = scope.fork(() -> fetchUserData(userId));
    StructuredTaskScope.Subtask<String> task2 = scope.fork(() -> fetchUserPreferences(userId));

    scope.join();

    String userData = task1.get();
    String preferences = task2.get();

    return combineUserInfo(userData, preferences);
  }
}
```


The difference is striking. With structured concurrency, the cleanup is automatic and guaranteed. If any task fails, all other tasks in the scope are cancelled. If the scope exits (normally or exceptionally), all resources are cleaned up. This is comparable to having a try-with-resources mechanism for concurrent tasks.

This approach has several advantages I've come to appreciate:

* Guaranteed cleanup: Tasks cannot outlive their scope.
* Clear ownership: Tasks belong to a specific scope.
* Exception safety: Failures are handled consistently.
* Resource management: No thread pool management needed.
* Composability: Scopes can be nested and combined.

### **Joiners: pick your success policy** {#h3-3-joiners-pick-your-success-policy}

A Joiner intercepts completion events and decides (1) whether to cancel siblings and (2) what join() should return. The JDK ships several factory helpers:

**"First one wins" (aka racing a set of replicas)**

```
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulResultOrThrow())) {
    urls.forEach(url -> scope.fork(() -> fetchFrom(url)));
    return scope.join();             // returns first successful String
}
```


**"All must succeed and I want their results"**

```
try (var scope = StructuredTaskScope.open(Joiner.<Result>allSuccessfulOrThrow())) {
    tasks.forEach(scope::fork);
    return scope.join()              // Stream<Subtask<Result>>
                 .map(Subtask::get)
                 .toList();
}
```


These little helpers make common patterns---"race", "gather", "wait-for-all"---painless.

### **Rolling your own Joiner** {#h3-4-rolling-your-own-joiner}

Sometimes you need a custom policy. Suppose I want to collect every successful subtask but ignore failures:

```
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

void main() {

  List<String> urls = List.of("https://bazlur.ca", "https://foojay.io", "https://github.com");

  try (var scope = StructuredTaskScope.open(new MyCollectingJoiner<String>())) {
    urls.forEach(url -> scope.fork(() -> fetchFrom(url)));
    List<String> fetchedContent = scope.join().toList();

    System.out.println("Total fetched content: " + fetchedContent.size());
  } catch (InterruptedException e) {
    throw new RuntimeException(e);
  }

}

private String fetchFrom(String url) {
  return "fetched from " + url + "";
}

class MyCollectingJoiner<T> implements StructuredTaskScope.Joiner<T, Stream<T>> {
  private final Queue<T> results = new ConcurrentLinkedQueue<>();

  @Override
  public boolean onComplete(StructuredTaskScope.Subtask<? extends T> st) {
    if (st.state() == StructuredTaskScope.Subtask.State.SUCCESS)
      results.add(st.get());
    return false;
  }

  @Override
  public Stream<T> result() {
    return results.stream();
  }
}
```


The interface is tiny---`onFork`, `onComplete`, and `result()`---yet powerful enough for most custom logic. To run this, we need JDK 25, and we can execute it from the CLI using the following command:

```
java --enable-preview CollectingJoiner.java
```


### **Better cancellation and deadlines** {#h3-5-better-cancellation-and-deadlines}

Cancellation rules did not change in spirit, but the API got stricter. If the owner thread is interrupted before or during `join()`, the scope automatically cancels every unfinished subtask. Subtasks should promptly honor InterruptedException; otherwise, `close()` will block, waiting for them to complete. (If you're calling blocking I/O, you're fine; if you're polling, remember to check `Thread.currentThread().isInterrupted()`).

Need a deadline? Pass a configuration lambda:

```
try (var scope = StructuredTaskScope.open(
         Joiner.<String>anySuccessfulResultOrThrow(),
         cfg -> cfg.withTimeout(Duration.ofSeconds(2)))) {
    // ...
}
```


If the timeout fires, the scope cancels, and `join()` throws `TimeoutException`. In practice, I attach a timeout to every external call to keep runaway tasks under control.

You can also swap the default virtual-thread factory for one that sets names or thread-locals:

```
ThreadFactory tagged = Thread.ofVirtual().name("api-%d").factory();

try (var scope = StructuredTaskScope.open(
         Joiner.<Integer>allSuccessfulOrThrow(),
         cfg -> cfg.withThreadFactory(tagged))) {
    // ...
}
```


Thread naming alone makes thread dumps far more readable.

### **Scoped values ride along** {#h3-6-scoped-values-ride-along}

All subtasks inherit bindings for `ScopedValues` established in the parent thread. That means you can pass request context, security credentials, or MDC information without packing it into every lambda. Once you experience this capability, you'll find it hard to revert to using ThreadLocal.

### **Guard-rails against misuse** {#h3-7-guard-rails-against-misuse}

`StructuredTaskScope` strictly enforces structure. If `fork()` is called from any thread other than the owner, a `StructureViolationException` is thrown. Forget the try-with-resources and let the scope escape the method? Same result. This approach is strict, but it effectively prevents accidental resource exhaustion (akin to 'fork-bombs').

### **Observability improvements** {#h3-8-observability-improvements}

Thread dumps now include the scope tree, so tools can show parent--child relationships directly. When I run `jcmd <pid> Thread.dump_to_file -format=json`, every scope appears with its forked threads nested below the owner. Finding the straggler that pins your virtual thread pool becomes a two-second grep instead of a half-hour investigation.

### **Some more examples to try out** {#h3-9-some-more-examples-to-try-out}

#### **Example 1 -- 360° Product View (Gather--Then--Fail)**

A classic e-commerce endpoint where a single HTTP request must aggregate product core data, real-time inventory, and a personalized price. Each sub-service is invoked in parallel inside a `StructuredTaskScope` that enforces an all-or-nothing policy: any failure or exceeding the one-second deadline cancels the whole group and surfaces an error to the caller. The scope's timeout, custom thread names, and `allSuccessfulOrThrow()` joiner encapsulate what is often a complex web of `CompletableFuture` wiring in three declarative lines.

```
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

public class ThreeSixtyProductView {
  record Product(long id, String name) {}
  record Stock(long productId, int quantity) {}
  record Price(long productId, double amount) {}
  record ProductPayload(Product core, Stock stock, Price price) {}

  private static Product coreApi(long id) throws InterruptedException {
    Thread.sleep(100); // simulate latency
    return new Product(id, "Gadget‑" + id);
  }

  private static Stock stockApi(long id) throws InterruptedException {
    Thread.sleep(120);
    return new Stock(id, new Random().nextInt(100));
  }

  private static Price priceApi(long id) throws InterruptedException {
    Thread.sleep(150);
    return new Price(id, 99.99);
  }

  static ProductPayload fetchProduct(long id) throws Exception {
    ThreadFactory named = Thread.ofVirtual().name("prod-%d", 1).factory();

    try (var scope = StructuredTaskScope.open(
        StructuredTaskScope.Joiner.<Object>allSuccessfulOrThrow(),
        cfg -> cfg.withTimeout(Duration.ofSeconds(1))
            .withThreadFactory(named))) {

      StructuredTaskScope.Subtask<Product> core = scope.fork(() -> coreApi(id));
      StructuredTaskScope.Subtask<Stock> stock = scope.fork(() -> stockApi(id));
      StructuredTaskScope.Subtask<Price> price = scope.fork(() -> priceApi(id));

      scope.join(); // throws on first failure / timeout
      return new ProductPayload(core.get(), stock.get(), price.get());
    }
  }

  void main() throws Exception {
    ProductPayload productPayload = fetchProduct(1L);
    System.out.println(productPayload);
  }
}
```


#### **Example 2 -- "Race the Mirrors" File Downloader**

Large binaries are hosted on several CDN mirrors. Latency varies, so we fire requests to every mirror simultaneously and use `Joiner.anySuccessfulResultOrThrow()` to stream the first successful `InputStream`, cancelling the rest. Bandwidth and connection slots are freed instantly, and users perceive the fastest possible download without manual cancellation plumbing.

```
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;

public class MirrorDownloaderDemo {
  void main() throws Exception {
    List<URI> mirrors = List.of(
        URI.create("https://mirror‑a.example.com"),
        URI.create("https://mirror‑b.example.com"),
        URI.create("https://mirror‑c.example.com"));

    Path target = Files.createFile(Path.of("download1.txt"));
    download(target, mirrors);
    System.out.println("Saved to " + target.toAbsolutePath());
  }

  static Path download(Path target, List<URI> mirrors) throws Exception {
    try (var scope = StructuredTaskScope.open(
        StructuredTaskScope.Joiner.<InputStream>anySuccessfulResultOrThrow())) {

      mirrors.forEach(uri -> scope.fork(() -> fetchFromMirror(uri)));
      try (InputStream in = scope.join()) {
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
      }
      return target;
    }
  }

  private static InputStream fetchFromMirror(URI uri) throws InterruptedException {
    Thread.sleep(50 + new Random().nextInt(300));
    String data = "Downloaded from " + uri + "\n";
    return new ByteArrayInputStream(data.getBytes());
  }
}
```


#### **Example 3 -- Batched Thumbnail Generator with Nested Scopes**

A media pipeline step receives a directory of images. An outer scope iterates through the files, while an inner scope, for each image, fans out three resize tasks (small, medium, and large). The inner scope fails fast; if any resize fails, that image is skipped, but the outer batch continues unaffected. Nested scopes separate per-item consistency from batch-level throughput with minimal code.

```
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.StructuredTaskScope;

public class ThumbnailBatchDemo {
  enum Size {SMALL, MEDIUM, LARGE}

  void main() throws Exception {
    Path tmpDir = Files.createTempDirectory("images");
    for (int i = 0; i < 3; i++) Files.createTempFile(tmpDir, "img" + i, ".jpg");
    processBatch(tmpDir);
  }

  static void processBatch(Path dir) throws IOException, InterruptedException {
    try (var batch = StructuredTaskScope.open()) {
      try (var files = Files.list(dir)) {
        files.filter(Files::isRegularFile)
            .forEach(img -> batch.fork(() -> handleOne(img)));
      }
      batch.join();
    }
  }

  private static void handleOne(Path image) {
    try (var scope = StructuredTaskScope.open(
        StructuredTaskScope.Joiner.<Void>allSuccessfulOrThrow())) {
      scope.fork(() -> resizeAndUpload(image, Size.SMALL));
      scope.fork(() -> resizeAndUpload(image, Size.MEDIUM));
      scope.fork(() -> resizeAndUpload(image, Size.LARGE));
      scope.join();
    } catch (Exception ex) {
      System.err.println("Skipping " + image.getFileName() + ": " + ex);
    }
  }

  private static Void resizeAndUpload(Path image, Size size) throws InterruptedException {
    Thread.sleep(80); // simulate resize
    Thread.sleep(40); // simulate upload
    System.out.println("Uploaded " + image.getFileName() + " [" + size + "]");
    return null;
  }
```


#### **Example 4 -- Real-Time Quote Service with Timed Fallback**

A trading UI demands a quote within 30 ms. A custom joiner captures the first successful price from the primary market feed, with a scope-level timeout of 30 ms. If the feed stalls, `scope.join()` returns empty and the service instantly falls back to yesterday's cached closing price. Callers always receive a value on time, and timeout logic lives in one declarative line.

```
import java.time.Duration;
import java.util.*;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class QuoteServiceDemo {
  void main() throws Exception {
    double q = quote("ACME");
    System.out.printf("Quote for ACME: %.2f%n", q);
  }

  static double quote(String symbol) throws InterruptedException {
    var firstSuccess = new StructuredTaskScope.Joiner<Double, Optional<Double>>() {
      private volatile Double value;

      public boolean onComplete(Subtask<? extends Double> st) {
        if (st.state() == Subtask.State.SUCCESS) value = st.get();
        return value != null;           // stop when we have one
      }

      public Optional<Double> result() {
        return Optional.ofNullable(value);
      }
    };

    try (var scope = StructuredTaskScope.open(firstSuccess,
        cfg -> cfg.withTimeout(Duration.ofMillis(30)))) {
      scope.fork(() -> marketFeed(symbol));
      Optional<Double> latest = scope.join();
      return latest.orElseGet(() -> cache(symbol));
    }
  }

  private static double marketFeed(String symbol) throws InterruptedException {
    long delay = new Random().nextBoolean() ? 20 : 60; // 50 % chance timeout
    Thread.sleep(delay);
    return 100 + new Random().nextDouble();
  }

  //for demo purposes only
  private static double cache(String symbol) {
    return 95.00;
  }
}
```


### **Final thoughts** {#h3-10-final-thoughts}

These changes represent a significant maturation of the structured concurrency API.

While I was initially frustrated by the frequent API changes, I now appreciate that the Java team took the time to get this right.

The structured concurrency API we have today is significantly better than what we started with, and I'm confident it will serve as a solid foundation for concurrent programming in Java going forward.
> **Want to dive deeper into the latest advancements in Java concurrency?**
>
> To explore these topics further and master modern techniques, consider checking out the book **"Modern Concurrency in Java"** available on O'Reilly: <https://learning.oreilly.com/library/view/modern-concurrency-in/9781098165406/>
