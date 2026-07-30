---
title: "Fixed Window Counter Rate Limiter (Redis & Java)"
slug: "fixed-window-counter-rate-limiter-redis-java"
date: "2025-01-20T16:34:18+00:00"
lastmod: "2025-02-22T20:31:41+00:00"
description: "Learn how to implement a Fixed Window Rate Limiter with Redis and Java! This detailed guide covers everything from the basics of rate-limiting to Java implem..."
canonical: "https://raphaeldelio.com/2024/12/30/fixed-window-counter-rate-limiter-redis-java/"
authors:
  - "raphael-delio"
image: "/images/posts/2025/01/fixed-window-counter-rate-limiter-redis-java/Redis_Video_RateLimiterImplementations_Part2_YoutubeThumbnail.png"
categories:
  - "Databases"
  - "Java"
  - "Java Beginner"
tags:
related_posts:
  - "rate-limiting-with-redis-an-essential-guide"
  - "back-to-basics-accessing-kubernetes-pods"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "sliding-window-counter-rate-limiter-redis-java"
enlighterjs: true
frozen: false
---

> [This article is also available on YouTube!](https://youtu.be/Ki3WKSNpdRU)
The **Fixed Window Counter** is the simplest and most straightforward rate-limiting algorithm. It divides time into fixed intervals (e.g., seconds, minutes, or hours) and counts the number of requests within each interval. If the count exceeds a predefined threshold, the requests are rejected until the next interval begins. Looking for a more precise algorithm? Take a look at the [Sliding Window Log implementation](https://foojay.io/today/sliding-window-log-rate-limiter-redis-java/).

**How It Works** {#h2-0-how-it-works}
-------------------------------------

![](https://cdn-images-1.medium.com/max/2160/1*VsdNn5KGd1A0rIfbczGy8Q.gif)

### **1. Define a Window Interval** {#h3-1-1-define-a-window-interval}

Choose a time interval, such as 1 second, 1 minute, or 1 hour.

### 2. **Track Requests** {#h3-2-2-track-requests}

Use a counter to track the number of requests made during the current window.

### 3. **Reset Counter:** {#h3-3-3-reset-counter}

At the end of the time window, reset the counter to zero and start counting again for the new window.

### 4. **Rate Limit Check:** {#h3-4-4-rate-limit-check}

Compare the counter against the allowed limit. If it exceeds the limit, reject further requests until the next window.

How to Implement It with Redis and Java {#h2-5-how-to-implement-it-with-redis-and-java}
---------------------------------------------------------------------------------------

There are two ways to implement the Fixed Rate Limiter with Redis. The simplest way is by:

### 1. Use the INCR command to increment the counter in Redis each time a request is allowed {#h3-6-1-use-the-incr-command-to-increment-the-counter-in-redis-each-time-a-request-is-allowed}

<pre class="EnlighterJSRAW" data-enlighter-language="generic">INCR my_counter</pre>

If there's no counter set yet, the INCR command will create one as zero and then increment it to one. If the counter is already set, the INCR commany will simply increment it by one.

### 2. Set the key to expire in one minute if it's newly created {#h3-7-2-set-the-key-to-expire-in-one-minute-if-it-s-newly-created}

If the counter doesn't exist, we need to set a time-to-live to ensure the time window lasts only for the specified period. **But we should only set an expiration if it doesn't already exist** . Otherwise, Redis would reset the expiration, and older requests could be counted beyond the allowed time. We'll use the EXPIRE command with the NX flag on the key. **The NX flag ensures the expiration is only set if the key doesn't already have one.** This approach is smart because the counter will only track requests during the key's lifespan. **Once the key expires and is removed, the counter resets, ensuring we only account for requests within the intended time window.**

<pre class="EnlighterJSRAW" data-enlighter-language="generic">EXPIRE my_counter 60 NX</pre>

### 3. Check the counter for each new request {#h3-8-3-check-the-counter-for-each-new-request}

When a new request comes in, check the counter to see how many requests have been made. If it's below the threshold, allow the process and increment the counter. If not, block the process from proceeding. If the key doesn't exist, assume the counter starts at 0.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">GET my_counter</pre>

Cool! Now that we understand the basics of our implementation, let's implement it in Java with Jedis.

Implementing it with Jedis {#h2-9-implementing-it-with-jedis}
-------------------------------------------------------------

**Jedis** is a popular Java library used to interact with **Redis** and we will use it for implementing our rate because it provides a simple and intuitive API for executing Redis commands from JVM applications.

### Start by adding the Jedis library to your Maven file: {#h3-10-start-by-adding-the-jedis-library-to-your-maven-file}

Check the latest version [here](https://redis.io/docs/latest/develop/clients/jedis/).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">redis.clients
jedis
5.2.0</pre>

### Create a FixedWindowRateLimiter class: {#h3-11-create-a-fixedwindowratelimiter-class}

The class will take:

1. A Jedis instance.
2. A time window size (e.g., 60 seconds).
3. The maximum number of allowed requests. 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package io.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ExpiryOption;

public class FixedWindowRateLimiter {

    private final Jedis jedis;
    private final int windowSize;
    private final int limit;

    public FixedWindowRateLimiter(Jedis jedis, long windowSize, int limit) {
        this.jedis = jedis;
        this.limit = limit;
        this.windowSize = windowSize;
    }
}</pre>

### Validate the Requests {#h3-12-validate-the-requests}

The main job of this rate limiter is to check if a client is within their allowed request limit. If yes, the request is allowed, and the counter is updated. If not, the request is blocked. **Step 1: Generate a key** We'll store each client's request count as a Redis key. To make keys unique for each client, we'll format them like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String key = "rate_limit:" + clientId;
}</pre>

For example, if the client ID is user123, their key would be rate_limit:user123. **Step 2: Fetch the Current Counter** We'll use Redis's GET command to check how many requests the client has made so far. If the key doesn't exist, we assume the client hasn't made any requests, so the counter is 0.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String key = "rate_limit:" + clientId;
    String currentCountStr = jedis.get(key);
    int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
}</pre>

**Step 3: Check the Request Limit** Next, we compare the current count to the allowed limit. If the counter is less than the limit, the request is allowed. Otherwise, it's blocked.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String key = "rate_limit:" + clientId;
    String currentCountStr = jedis.get(key);
    int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;

    boolean isAllowed = currentCount   The first request marks the start of the time window. Any subsequent requests during this window’s lifespan will increment the counter.
</pre>

> Once the window expires, the key is automatically removed from Redis. The next request after that will define the start of a new window. If we didn't set the NX flag, the expiration would be reset everytime the counter is incremented, increasing the lifespan of the window.

### **Complete Implementation** {#h3-13-complete-implementation}

Here's the full code for the FixedWindowRateLimiter class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package io.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ExpiryOption;

public class FixedWindowRateLimiter {

    private final Jedis jedis;
    private final int windowSize;
    private final int limit;

    public FixedWindowRateLimiter(Jedis jedis, long windowSize, int limit) {
        this.jedis = jedis;
        this.limit = limit;
        this.windowSize = windowSize;
    }

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;
        String currentCountStr = jedis.get(key);
        int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;

        boolean isAllowed = currentCount &lt; limit;

        if (isAllowed) {
            Transaction transaction = jedis.multi();
            transaction.incr(key);
            transaction.expire(key, windowSize, ExpiryOption.NX); // Set expire only if not set
            transaction.exec();
        }

        return isAllowed;
    }
}</pre>

And we're ready to start testing it's behavior!

Testing our Rate Limiter {#h2-14-testing-our-rate-limiter}
----------------------------------------------------------

To ensure our Fixed Window Rate Limiter behaves as expected, we'll write tests for various scenarios. For this, we'll use three tools:

1. **Redis TestContainers**: This library spins up an isolated Redis container for testing. This means we don't need to rely on an external Redis server during our tests. Once the tests are done, the container is stopped, leaving no leftover data.
2. **JUnit 5**: Our main testing framework, which helps us define and structure tests with lifecycle methods like @BeforeEach and @AfterEach.
3. **AssertJ**: A library that makes assertions readable and expressive, like assertThat(result).isTrue().

Let's begin by adding the necessary dependencies to our pom.xml.

### Adding Dependencies {#h3-15-adding-dependencies}

Here's what you'll need in your Maven pom.xml file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">org.junit.jupiter
junit-jupiter-engine
5.10.0
test

com.redis
testcontainers-redis
2.2.2
test

org.assertj
assertj-core
3.11.1
test<code></code></pre>

Once you've added these dependencies, you're ready to start writing your test class.

### Setting Up the Test Class {#h3-16-setting-up-the-test-class}

The first step is to create a test class named FixedWindowRateLimiterTest. Inside, we'll define three main components:

1. **Redis Test Container**: This launches a Redis instance in a Docker container.
2. **Jedis Instance**: This connects to the Redis container for sending commands.
3. **Rate Limiter**: The actual FixedWindowRateLimiter instance we're testing.

Here's how the skeleton of our test class looks:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class FixedWindowRateLimiterTest {

    private static final RedisContainer redisContainer = new RedisContainer("redis:latest")
            .withExposedPorts(6379);

    private Jedis jedis;
    private FixedWindowRateLimiter rateLimiter;

    // Start Redis container once before any tests run
    static {
        redisContainer.start();
    }
}</pre>

### **Preparing the Environment Before Each Test** {#h3-17-preparing-the-environment-before-each-test}

Before running any test, we need to ensure a clean Redis environment. Here's what we'll do:

1. **Connect to Redis**: Use a Jedis instance to connect to the Redis container.
2. **Flush Data**: Clear any leftover data in Redis to ensure consistent results for each test.

We'll set this up in a method annotated with @BeforeEach, which runs before every test case.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@BeforeEach
public void setup() {
    jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
    jedis.flushAll();
}</pre>

> FLUSHALL is an actual Redis command that deletes all the keys of all the existing databases. [Read more about it in the official documentation](https://redis.io/docs/latest/commands/flushall/).

### **Cleaning Up After Each Test** {#h3-18-cleaning-up-after-each-test}

After each test, we need to close the Jedis connection to free up resources. This ensures no lingering connections interfere with subsequent tests.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@AfterEach
public void tearDown() {
    jedis.close();
}</pre>

### **Full Setup** {#h3-19-full-setup}

Here's how the complete test class looks with everything in place:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class FixedWindowRateLimiterTest {
    private static final RedisContainer redisContainer = new RedisContainer("redis:latest")
            .withExposedPorts(6379);

    private Jedis jedis;
    private FixedWindowRateLimiter rateLimiter;

    static {
        redisContainer.start();
    }

    @BeforeEach
    public void setup() {
        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
        jedis.flushAll();
    }

    @AfterEach
    public void tearDown() {
        jedis.close();
    }
}</pre>

### **Verifying Requests Within the Limit** {#h3-20-verifying-requests-within-the-limit}

This test ensures the rate limiter allows requests within the defined limit. We configure it with a **limit of** **5 requests** and a **10-second window** , then call isAllowed("client-1") **5 times** . Each call should return true, confirming the rate limiter correctly tracks and permits requests under the limit.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldAllowRequestsWithinLimit() {
    rateLimiter = new FixedWindowRateLimiter(jedis, 10, 5);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }
}</pre>

### Verifying **Requests Beyond the Limit** {#h3-21-verifying-requests-beyond-the-limit}

This test ensures the rate limiter correctly denies requests once the defined limit is exceeded. Configured with a **limit of** **5 requests** in a **60-second window** , we call isAllowed("client-1") **5 times** and expect all to return true. On the 6th call, it should return false, verifying the rate limiter blocks requests beyond the allowed limit.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldDenyRequestsOnceLimitIsExceeded() {
    rateLimiter = new FixedWindowRateLimiter(jedis, 60, 5);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed("client-1"))
            .withFailMessage("Request beyond limit should be denied")
            .isFalse();
}</pre>

### **Verifying Requests After Window Reset** {#h3-22-verifying-requests-after-window-reset}

This test ensures the rate limiter resets correctly after the fixed window expires. Configured with a **limit of 5 requests** and a **1-second window** , the first 5 requests (isAllowed("client-1")) return true, while the 6th request is denied (false). After waiting for the window to expire, the next request is allowed (true), confirming the reset behavior works as expected.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldAllowRequestsAgainAfterFixedWindowResets() throws InterruptedException {
    int limit = 5;
    String clientId = "client-1";
    int windowSize = 1;
    rateLimiter = new FixedWindowRateLimiter(jedis, windowSize, limit);

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request beyond limit should be denied")
            .isFalse();

    Thread.sleep((windowSize + 1) * 1000);

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request after window reset should be allowed")
            .isTrue();
}</pre>

### **Verifying Independent Handling of Multiple Clients** {#h3-23-verifying-independent-handling-of-multiple-clients}

This test ensures the rate limiter handles multiple clients independently. Configured with a **limit of 5 requests** and a **10-second window** , the first 5 requests from **client-1** are allowed (true), while the 6th is denied (false). Simultaneously, all 5 requests from **client-2** are allowed (true), confirming the rate limiter maintains separate counters for each client.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldHandleMultipleClientsIndependently() {
    int limit = 5;
    String clientId1 = "client-1";
    String clientId2 = "client-2";
    int windowSize = 10;
    rateLimiter = new FixedWindowRateLimiter(jedis, windowSize, limit);

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId1))
                .withFailMessage("Client 1 request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed(clientId1))
            .withFailMessage("Client 1 request beyond limit should be denied")
            .isFalse();

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId2))
                .withFailMessage("Client 2 request " + i + " should be allowed")
                .isTrue();
    }
}</pre>

### **Verifying Requests Are Denied Until Fixed Window Resets** {#h3-24-verifying-requests-are-denied-until-fixed-window-resets}

This test ensures the rate limiter denies additional requests until the fixed window expires. Configured with a **limit of 3 requests** and a **5-second window** , the first 3 requests (isAllowed("client-1")) are allowed (true), while the 4th is denied (false). After waiting for half the window duration (2.5 seconds), requests are still denied (false). Once the window fully resets (after another 2.5 seconds), the next request is allowed (true), confirming proper behavior during and after the fixed window.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldDenyAdditionalRequestsUntilFixedWindowResets() throws InterruptedException {
    int limit = 3;
    int windowSize = 5;
    String clientId = "client-1";
    rateLimiter = new FixedWindowRateLimiter(jedis, windowSize, limit);

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
                .withFailMessage("Request " + i + " should be allowed within limit")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request beyond limit should be denied")
            .isFalse();

    Thread.sleep(2500);

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request should still be denied within the same fixed window")
            .isFalse();

    Thread.sleep(2500);

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request should be allowed after fixed window reset")
            .isTrue();
}</pre>

### **Verifying Denied Requests Are Not Counted** {#h3-25-verifying-denied-requests-are-not-counted}

This test ensures that requests denied by the rate limiter are not included in the request count. Configured with a limit of 3 requests and a 5-second window, the first 3 requests (isAllowed("client-1")) are allowed (true), while the 4th is denied (false). Afterward, the Redis key for the client is checked to confirm the stored count equals the limit (3), ensuring denied requests do not increase the counter.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void testRateLimitDeniedRequestsAreNotCounted() {
    int limit = 3;
    int windowSize = 5;
    String clientId = "client-1";
    rateLimiter = new FixedWindowRateLimiter(jedis, windowSize, limit);

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("This request should be denied")
            .isFalse();

    String key = "rate_limit:" + clientId;
    int requestCount = Integer.parseInt(jedis.get(key));
    assertThat(requestCount)
            .withFailMessage("The count (" + requestCount + ") should be equal to the limit (" + limit + "), not counting the denied request")
            .isEqualTo(limit);
}</pre>

Is there any other behavior we should verify? Let me know in the comments! The Fixed Window Rate Limiter is a simple yet effective way to manage request rates, and **Redis** makes it incredibly fast and reliable. By using commands like INCR and EXPIRE, we created a solution that tracks and limits requests while automatically resetting counters when the time window expires. With **Jedis** , we built an easy-to-understand Java implementation, and thanks to thorough testing with Redis TestContainers, JUnit 5, and AssertJ, we can trust it works as expected. This approach is a great starting point for handling request limits and can easily be adapted for more complex scenarios if needed.

### GitHub Repo {#h3-26-github-repo}

You can find this implementation in **Java** and **Kotlin** :

* Java ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-java-example/blob/main/src/main/java/io/redis/FixedWindowRateLimiter.java), [Test](https://github.com/raphaeldelio/redis-rate-limiter-java-example/blob/main/src/test/java/io/redis/FixedWindowRateLimiterTest.java))
* Kotlin ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/main/kotlin/org/example/FixedWindowRateLimiter.kt), [Test](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/test/kotlin/org/example/FixedWindowRateLimiterTest.kt))

### Stay Curious! {#h3-27-stay-curious}

**Related Articles:**

* [Token Bucket Rate Limiter (Redis \& Java)](https://foojay.io/today/token-bucket-rate-limiter-redis-java/)

<!-- -->

* [Rate limiting with Redis: An essential guide](https://foojay.io/today/rate-limiting-with-redis-an-essential-guide/)

<!-- -->

* [Fixed Window Counter Rate Limiter (Redis \& Java)](https://foojay.io/today/fixed-window-counter-rate-limiter-redis-java/)

<!-- -->

* [Sliding Window Log Rate Limiter (Redis \& Java)](https://foojay.io/today/sliding-window-log-rate-limiter-redis-java/)

<!-- -->

* [Sliding Window Counter Rate Limiter (Redis \& Java)](https://foojay.io/today/sliding-window-counter-rate-limiter-redis-java/)
