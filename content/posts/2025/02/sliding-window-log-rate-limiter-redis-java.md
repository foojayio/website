---
title: "Sliding Window Log Rate Limiter (Redis & Java)"
slug: "sliding-window-log-rate-limiter-redis-java"
date: "2025-02-04T12:21:56+00:00"
lastmod: "2025-05-02T12:51:23+00:00"
description: "Build a sliding window log rate limiter with Redis and Java. Track requests in a rolling window, expire old entries, and test your setup for smooth..."
canonical: "https://raphaeldelio.com/2025/01/22/sliding-window-log-rate-limiter-redis-java/"
authors:
  - "raphael-delio"
image: "https://foojay.io/wp-content/uploads/2025/01/Redis_Video_RateLimiterImplementations_Part4_YoutubeThumbnail.png"
categories:
  - "Java"
  - "Java Beginner"
  - "nosql"
  - "Redis"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

> [This article is also available on YouTube. Check it out!](https://youtu.be/bCYzRg0oQjY)

**The Sliding Window Log is a *more precise* way to handle rate limiting. Instead of splitting time into fixed intervals like the [Fixed Window Counter](https://foojay.io/wp-admin/post.php?post=115337) , it keeps a log of timestamps for each request. This allows it to track requests over a rolling time period, like the last second or minute, without abrupt resets at the end of an interval.**

By checking how many requests were made within that time frame, it ensures limits are enforced more smoothly. If the log shows too many requests, new ones are denied.

How It Works {#h2-0-how-it-works}
---------------------------------

![](https://cdn-images-1.medium.com/max/2160/1*tmaCfNHgzaAJNop4Aa2afA.gif)

### 1. **Define a Time Window** {#h3-1-1-define-a-time-window}

Choose a rolling time window, such as the last 1 second, 1 minute, or 1 hour.

### 2. **Track Requests** {#h3-2-2-track-requests}

Log each request with a precise timestamp as it comes in.

### 3. **Remove Expired Entries** {#h3-3-3-remove-expired-entries}

Continuously clean up the log by removing entries older than the current time window.

### 4. **Rate Limit Check** {#h3-4-4-rate-limit-check}

Count the remaining entries in the log. If the count exceeds the allowed limit, reject new requests; otherwise, allow them.

How to Implement It with Redis and Java {#h2-5-how-to-implement-it-with-redis-and-java}
---------------------------------------------------------------------------------------

Implementing the **Sliding Window Log** with **Redis** involves logging each request with a timestamp and checking how many requests fall within the defined time window.

With Redis 8 and the introduction of the command HEXPIRE this process became easier and more efficient. The HEXPIRE command allows us to set expiration for specific fields in a hash. Before that, we could only set expiration to the hash as a whole.

Here's how to do it:

### 1. **Log Each Request (If Allowed)** {#h3-6-1-log-each-request-if-allowed}

Use a Redis **hash** (HSET) to track each request. The hash key can represent something unique, like an IP address or client ID. Each field in the hash will represent a successful request.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">HSET bucket_name  ""</pre>

The HSET command creates a hash where the key (bucket_name) identifies the client, and the field () marks the exact time of the request. The value related to this field is left empty since only the timestamp matters.

### 2. **Remove Expired Entries** {#h3-7-2-remove-expired-entries}

To make sure only requests within a specific time window are kept, we can set a time-to-live for each field when adding it to the hash.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">HEXPIRE bucket_name 3600 FIELDS 1</pre>

This example sets the field to expire after 3600 seconds (1 hour). This way, only recent requests stay in the hash, ensuring the bucket reflects activity within the chosen time window.

### 3. **Count Requests in the Time Window** {#h3-8-3-count-requests-in-the-time-window}

Use HLEN bucket_name to count the existing fields in the hash. This gives you the number of requests within the time window.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">HLEN bucket_name</pre>

Compare this count to the allowed limit. **If it's over the limit, reject the new request. Otherwise, add the request to the bucket and allow it.**

Cool! Now that we understand the steps, let's implement this in Java with Jedis!

Implementing it with Jedis {#h2-9-implementing-it-with-jedis}
-------------------------------------------------------------

**Jedis** is a popular library that makes it easy to work with **Redis** from **Java** applications.

Its API is simple and intuitive, which is perfect for implementing our rate limiter. We'll use it to interact with Redis and handle all the commands we need for managing requests and enforcing limits.

### Start by adding the Jedis library to your Maven file: {#h3-10-start-by-adding-the-jedis-library-to-your-maven-file}

Check the latest version [here](https://redis.io/docs/latest/develop/clients/jedis/).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">redis.clients
jedis
5.2.0</pre>

### Create a SlidingWindowLogRateLimiter class: {#h3-11-create-a-slidingwindowlogratelimiter-class}

The class will take:

1. A Jedis instance.
2. A time window size (e.g., 60 seconds).
3. The maximum number of allowed requests. 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package io.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ExpiryOption;

public class SlidingWindowLogRateLimiter {
private final Jedis jedis;
private final int windowSize;
private final int limit;

  public SlidingWindowLogRateLimiter(Jedis jedis, long windowSize, int limit) {
    this.jedis = jedis;
    this.limit = limit;
    this.windowSize = windowSize;
  }

}
</pre>

### Validate the Requests {#h3-12-validate-the-requests}

The main task of this rate limiter is to determine whether a client's request falls within their allowed limit. If it does, the request is permitted, and the log is updated to include it. If it doesn't, the request is denied without updating the log.

**Step 1: Generate a key**   

We'll store each client's request count as a Redis key. To make keys unique for each client, we'll format them like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String key = "rate_limit:" + clientId;
}</pre>

For example, if the client ID is user123, their key would be rate_limit:user123.

**Step 2: Fetch the current count and determine if the request is allowed** !  

To fetch the current count, we'll use the HLEN that will return the number of fields in our hash or 0 when if the hash doesn't exist yet.

To determine if the request is allowed to proceed, we'll simply check if the current count is lower than the limit:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">long requestCount = jedis.hlen(key);
boolean isAllowed = requestCount &lt; limit;</pre>

**Step 3: Update the bucket and return the result**   

If the request is allowed, the next step is to add it to our Redis hash:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">if (isAllowed) {
    String fieldKey = UUID.randomUUID().toString();
    Transaction transaction = jedis.multi();
    transaction.hset(key, fieldKey, "");
    transaction.hexpire(key, windowSize, fieldKey);
    var result = transaction.exec();
}</pre>

We'll do this in a **transaction** to ensure that:

* Both HSET and HEXPIRE happen together, avoiding race conditions.
* Both HSET and HEXPIRE are pipelined (sent in a batch to Redis) to reduce the number of network trips, improving performance.

Besides that, to make sure there are no duplicate entries even if multiple requests arrive at the same millisecond, we'll generate a random UUID to serve as a unique key.

A new Redis transaction is then started to handle the logging operations atomically. First, the HSET command adds the new request to the hash with the random UUID as its field.

Next, the HEXPIRE command sets a time-to-live (TTL) on the field key. This ensures the field is automatically cleaned up once the time window has passed, preventing unnecessary storage of stale data and keeping the bucket updated with all valid requests within the time window.

If the execution of the transaction returns an empty result, it means that something went wrong on Redis and that the operations didn't succeed. For the sake of simplicity, we'll handle such cases by throwing an exception:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">if (result.isEmpty()) {
     throw new IllegalStateException("Empty result from Redis transaction");
}</pre>

Finally, the transaction is executed, and the result of isAllowed is returned.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">return isAllowed;</pre>

### Complete Implementation {#h3-13-complete-implementation}

Here's the full code for the SlidingWindowLogRateLimiter class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String key = "rate_limit:" + clientId;
    long requestCount = jedis.hlen(key);
    boolean isAllowed = requestCount &lt; limit;

    if (isAllowed) {
        String fieldKey = UUID.randomUUID().toString();
        Transaction transaction = jedis.multi();
        transaction.hset(key, fieldKey, "");
        transaction.hexpire(key, windowSize, fieldKey);
        var result = transaction.exec();

        if (result.isEmpty()) {
            throw new IllegalStateException("Empty result from Redis transaction");
        }
    }

    return isAllowed;
}</pre>

And we're ready to start testing its behavior!

Testing our Rate Limiter {#h2-14-testing-our-rate-limiter}
----------------------------------------------------------

To ensure our Sliding Window Log Rate Limiter behaves as expected, we'll write tests for various scenarios. For this, we'll use three tools:

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
test</pre>

Once you've added these dependencies, you're ready to start writing your test class.

### Setting Up the Test Class {#h3-16-setting-up-the-test-class}

The first step is to create a test class named SlidingWindowLogRateLimiterTest. Inside, we'll define three main components:

1. **Redis Test Container**: This launches a Redis instance in a Docker container.
2. **Jedis Instance**: This connects to the Redis container for sending commands.
3. **Rate Limiter**: The actual SlidingWindowLogRateLimiterTest instance we're testing.

Here's how the skeleton of our test class looks:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class SlidingWindowLogRateLimiterTest {
    private static final RedisContainer redisContainer = new RedisContainer("redis:latest")
            .withExposedPorts(6379);
    private Jedis jedis;
    private SlidingWindowLogRateLimiterTest rateLimiter;

    // Start Redis container once before any tests run
    static {
        redisContainer.start();
    }
}</pre>

### Preparing the Environment Before Each Test {#h3-17-preparing-the-environment-before-each-test}

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

### Cleaning Up After Each Test {#h3-18-cleaning-up-after-each-test}

After each test, we need to close the Jedis connection to free up resources. This ensures no lingering connections interfere with subsequent tests.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@AfterEach
public void tearDown() {
    jedis.close();
}</pre>

### Full Setup {#h3-19-full-setup}

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

### Verifying Requests Within the Limit {#h3-20-verifying-requests-within-the-limit}

This test ensures the rate limiter allows requests within the defined limit.

We configure it with a **limit of** **5 requests** and a **10-second window** , then call isAllowed("client-1") **5 times**. Each call should return true, confirming the rate limiter correctly tracks and permits requests under the limit.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldAllowRequestsWithinLimit() {
    rateLimiter = new SlidingWindowLogRateLimiter(jedis, 5, 10);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }
}</pre>

### Verifying Requests Beyond the Limit {#h3-21-verifying-requests-beyond-the-limit}

This test ensures the rate limiter correctly denies requests once the defined limit is exceeded.

Configured with a **limit of** **5 requests** in a **60-second window** , we call isAllowed("client-1") **5 times** and expect all to return true. On the 6th call, it should return false, verifying the rate limiter blocks requests beyond the allowed limit.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldDenyRequestsOnceLimitIsExceeded() {
    rateLimiter = new SlidingWindowLogRateLimiter(jedis, 5, 60);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed("client-1"))
            .withFailMessage("Request beyond limit should be denied")
            .isFalse();
}</pre>

### **Verifying Requests After Sliding Window Resets** {#h3-22-verifying-requests-after-sliding-window-resets}

This test ensures that the rate limiter correctly resets the sliding window and allows requests after the window duration has passed.

Configured with a **limit of 5 requests** and a **1-second window**, we first call isAllowed("client-1") 5 times and expect all to return true. On the 6th call, the rate limiter should return false, indicating that the limit has been reached.

After waiting for the sliding window to reset (1 second + a buffer), the next request is allowed, verifying that the sliding window correctly clears expired entries and permits new requests.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldAllowRequestsAgainAfterSlidingWindowResets() throws InterruptedException {
    int limit = 5;
    String clientId = "client-1";
    long windowSize = 1L;
    rateLimiter = new SlidingWindowLogRateLimiter(jedis, limit, windowSize);

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

### Verifying Independent Handling of Multiple Clients {#h3-23-verifying-independent-handling-of-multiple-clients}

This test ensures the rate limiter handles multiple clients independently.

Configured with a **limit of 5 requests** and a **10-second window** , the first 5 requests from **client-1** are allowed (true), while the 6th is denied (false).

Simultaneously, all 5 requests from **client-2** are allowed (true), confirming the rate limiter maintains separate counters for each client.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void shouldHandleMultipleClientsIndependently() {
    int limit = 5;
    String clientId1 = "client-1";
    String clientId2 = "client-2";
    long windowSize = 10L;
    rateLimiter = new SlidingWindowLogRateLimiter(jedis, limit, windowSize);

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

### **Verifying Gradual Request Allowance in Sliding Window** {#h3-24-verifying-gradual-request-allowance-in-sliding-window}

This test ensures that the sliding window rate limiter gradually allows requests as old requests expire from the window.

Configured with a **limit of 3 requests** and a **4-second window**, we initially call isAllowed("client-1") 3 times, each 1 second apart, and expect all to return true.

On the 4th call, the request is denied, confirming the limit has been reached.

After waiting 2 seconds, enough of the older requests have expired to allow one new request, verifying that the sliding window behaves dynamically and permits requests as the window progresses.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
 public void shouldAllowRequestsAgainGraduallyInSlidingWindow() throws InterruptedException {
     int limit = 3;
     long windowSize = 4L;
     String clientId = "client-1";
     rateLimiter = new SlidingWindowLogRateLimiter(jedis, limit, windowSize);

     for (int i = 1; i &lt;= limit; i++) {
         assertThat(rateLimiter.isAllowed(clientId))
                 .withFailMessage("Request " + i + " should be allowed")
                 .isTrue();
         Thread.sleep(1000);
     }

     assertThat(rateLimiter.isAllowed(clientId))
             .withFailMessage("Request beyond limit should be denied")
             .isFalse();

     Thread.sleep(2000);

     assertThat(rateLimiter.isAllowed(clientId))
             .withFailMessage("Request should be allowed in a sliding window")
             .isTrue();
 }</pre>

### Verifying Denied Requests Are Not Counted {#h3-25-verifying-denied-requests-are-not-counted}

This test ensures that requests denied by the rate limiter are not included in the request count.

Configured with a limit of **3 requests** and a **5-second window**, the first 3 requests (isAllowed("client-1")) are allowed (true), while the 4th is denied (false).

Afterward, the Redis key for the client is checked to confirm the stored count equals the limit (3), ensuring denied requests do not increase the counter.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
public void testRateLimitDeniedRequestsAreNotCounted() {
    int limit = 3;
    long windowSize = 4L;
    String clientId = "client-1";
    rateLimiter = new SlidingWindowLogRateLimiter(jedis, limit, windowSize);

    for (int i = 1; i &lt;= limit; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
                .withFailMessage("Request " + i + " should be allowed")
                .isTrue();
    }

    assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("This request should be denied")
            .isFalse();

    String key = "rate_limit:" + clientId;
    long requestCount = jedis.zcard(key);
    assertThat((long) limit)
            .withFailMessage("The count (" + requestCount + ") should be equal to the limit (" + limit + "), not counting the denied request")
            .isEqualTo(requestCount);
}</pre>

Is there any other behavior we should verify? Let me know in the comments!

The Sliding Window Log Rate Limiter is a smarter way to manage request rates, offering more precision compared to simpler methods. It takes advantage of **Redis's** speed and reliability to keep things efficient.

Using commands like ZADD, ZREMRANGEBYSCORE, and ZCARD, we created a solution that tracks requests dynamically within a rolling time window. This ensures rate limits are applied smoothly without abrupt resets when the window changes.

We implemented the limiter in Java using Jedis, making it straightforward and easy to work with. To make sure it's reliable, we tested it with Redis TestContainers, JUnit 5, and AssertJ in various scenarios.

This setup is a strong and flexible starting point for managing request limits and can be easily adapted to handle more advanced needs as they come up.

### GitHub Repo {#h3-26-github-repo}

You can find this implementation in **Java** and **Kotlin**:

* Java ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-java-example/blob/main/src/main/java/io/redis/SlidingWindowLogRateLimiter.java), [Test](https://github.com/raphaeldelio/redis-rate-limiter-java-example/blob/main/src/test/java/io/redis/SlidingWindowLogHashAlternativeRateLimiterTest.java))
* Kotlin ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/main/kotlin/org/example/SlidingWindowLogRateLimiter.kt), [Test](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/test/kotlin/io/redis/SlidingWindowLogHashAlternativeRateLimiterTest.kt))

### Stay Curious! {#h3-27-stay-curious}

**Related Articles:**

* [Rate limiting with Redis: An essential guide](https://foojay.io/today/rate-limiting-with-redis-an-essential-guide/)
* [Fixed Window Counter Rate Limiter (Redis \& Java)](https://foojay.io/today/fixed-window-counter-rate-limiter-redis-java/)
* [Sliding Window Log Rate Limiter (Redis \& Java)](https://foojay.io/today/sliding-window-log-rate-limiter-redis-java/)
* [Sliding Window Counter Rate Limiter (Redis \& Java)](https://foojay.io/today/sliding-window-counter-rate-limiter-redis-java/)
* [Token Bucket Rate Limiter (Redis \& Java)](https://foojay.io/today/token-bucket-rate-limiter-redis-java/)
