---
title: "Token Bucket Rate Limiter (Redis & Java) - Raphael De Lio"
slug: "token-bucket-rate-limiter-redis-java"
date: "2025-01-28T07:07:08+00:00"
lastmod: "2025-05-02T12:52:08+00:00"
description: "Learn how to implement the Token Bucket rate-limiting algorithm using Redis and Java. Discover step-by-step guidance, testing with Redis TestContainers, and..."
canonical: "https://raphaeldelio.com/2025/01/13/token-bucket-rate-limiter-redis-java/"
authors:
  - "raphael-delio"
image: "Redis_Video_RateLimiterImplementations_Part3_YoutubeThumbnail.png"
categories:
  - "Databases"
  - "Java"
  - "Redis"
tags:
related_posts:
  - "rate-limiting-with-redis-an-essential-guide"
  - "fixed-window-counter-rate-limiter-redis-java"
  - "sliding-window-counter-rate-limiter-redis-java"
  - "sliding-window-log-rate-limiter-redis-java"
enlighterjs: true
frozen: false
---

> [This article is also available on YouTube!](https://youtu.be/cfF6nXIpDwE)

The **Token Bucket** algorithm is a flexible and efficient rate-limiting mechanism.

It works by filling a bucket with tokens at a fixed rate (e.g., one token per second).

Each request consumes a token, and if no tokens are available, the request is rejected.

The bucket has a maximum capacity, so it can handle bursts of traffic as long as the burst doesn't exceed the number of tokens in the bucket.
> Looking for a different rate limiter algorithm? [Check the essential guide.](https://foojay.io/?p=115335)

How It Works {#h2-0-how-it-works}
---------------------------------

![](https://cdn-images-1.medium.com/max/2160/1*7cDKq5yh5RD0ygvb3mVwfQ.gif)

### 1. **Define a Token Refill Rate** {#h3-1-1-define-a-token-refill-rate}

Set a rate at which tokens are added to the bucket, such as 1 token per second or 10 tokens per minute.

### 2. **Track Token Consumption** {#h3-2-2-track-token-consumption}

For each incoming request, deduct one token from the bucket.

### 3. **Refill Tokens** {#h3-3-3-refill-tokens}

Continuously refill the bucket at the defined rate, up to its maximum capacity, ensuring unused tokens can accumulate for future bursts.

### 4. **Rate Limit Check** {#h3-4-4-rate-limit-check}

Before processing a request, check if there are enough tokens in the bucket. If the bucket is empty, reject the request until tokens are replenished.

How to Implement It with Redis and Java {#h2-5-how-to-implement-it-with-redis-and-java}
---------------------------------------------------------------------------------------

For the **Token Bucket Rate Limiter**, Redis provides an efficient way to track tokens and implement the algorithm. Here's how to do it:

### 1. Retrieve current token count and last refill time {#h3-6-1-retrieve-current-token-count-and-last-refill-time}

First, retrieve the current token count and the last refill time:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">GET rate_limit:&lt;clientId&gt;:count  
GET rate_limit:&lt;clientId&gt;:lastRefill</pre>

If these keys don't exist, initialize the token count to the bucket's maximum capacity and set the current time as the last refill time using SET.

### 2. Refill tokens if necessary and update the bucket {#h3-7-2-refill-tokens-if-necessary-and-update-the-bucket}

Update the token count and last refill date time after processing each request:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SET rate_limit:&lt;clientId&gt;:count &lt;new_token_count&gt;  
SET rate_limit:&lt;clientId&gt;:lastRefill &lt;current_time&gt;</pre>

### 3. Allow or reject the request {#h3-8-3-allow-or-reject-the-request}

If tokens are available, allow the request and decrement the count by one using:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">DECR rate_limit:&lt;clientId&gt;:count</pre>

Implementing it with Jedis {#h2-9-implementing-it-with-jedis}
-------------------------------------------------------------

**Jedis** is a popular Java library used to interact with **Redis**and we will use it for implementing our rate limiter because it provides a simple and intuitive API for executing Redis commands from JVM applications.

### **Add Jedis to Your Maven File**: {#h3-10-add-jedis-to-your-maven-file}

Check the latest version [here](https://redis.io/docs/latest/develop/clients/jedis/).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
    &lt;groupId&gt;redis.clients&lt;/groupId&gt;
    &lt;artifactId&gt;jedis&lt;/artifactId&gt;
    &lt;version&gt;5.2.0&lt;/version&gt;
&lt;/dependency&gt;</pre>

### Create a **TokenBucketRateLimiter** class: {#h3-11-create-a-tokenbucketratelimiter-class}

The class will take:

1. Accept a Jedis instance.
2. Define the maximum capacity of the token bucket.
3. Specify the token refill rate (tokens per second). 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package io.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TokenBucketRateLimiter {
    private final Jedis jedis;
    private final int bucketCapacity; // Maximum tokens the bucket can hold
    private final double refillRate; // Tokens refilled per second

    public TokenBucketRateLimiter(Jedis jedis, int bucketCapacity, double refillRate) {
        this.jedis = jedis;
        this.bucketCapacity = bucketCapacity;
        this.refillRate = refillRate;
    }
}</pre>

### Validate the Requests {#h3-12-validate-the-requests}

The main task of this rate limiter is to determine whether a client has sufficient tokens to process their request. If yes, the request is allowed, and tokens are deducted. If not, the request is blocked.

**Step 1: Generate the keys**   

We'll store each client's token count and last refill time in Redis using unique keys. The keys will look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String keyCount = "rate_limit:" + clientId + ":count";
    String keyLastRefill = "rate_limit:" + clientId + ":lastRefill";
}</pre>

For example, if the client ID is user123, their keys would be rate_limit:user123:count and rate_limit:user123:lastRefill.

**Step 2: Fetch Current State**   

We use Redis's GET command to retrieve the current token count and the last refill time. If the keys don't exist, we assume the bucket is full, and the last refill time is the current timestamp.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public boolean isAllowed(String clientId) {
    String keyCount = "rate_limit:" + clientId + ":count";
    String keyLastRefill = "rate_limit:" + clientId + ":lastRefill";

    Transaction transaction = jedis.multi();
    transaction.get(keyLastRefill);
    transaction.get(keyCount);
    var results = transaction.exec();

    long currentTime = System.currentTimeMillis();
    long lastRefillTime = results.get(0) != null ? Long.parseLong((String) results.get(0)) : currentTime;
    int tokenCount = results.get(1) != null ? Integer.parseInt((String) results.get(1)) : bucketCapacity;
}</pre>

**Step 3: Refill Tokens**   

Calculate how many tokens should be added based on the time elapsed since the last refill. Ensure the bucket doesn't exceed its maximum capacity.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">long elapsedTimeMs = currentTime - lastRefillTime;
double elapsedTimeSecs = elapsedTimeMs / 1000.0;
int tokensToAdd = (int) (elapsedTimeSecs * refillRate);

tokenCount = Math.min(bucketCapacity, tokenCount + tokensToAdd);</pre>

**Step 4: Check Token Availability**   

Compare the current token count to determine if the request can be allowed. **If tokens are available, deduct one token; otherwise, block the request.**

<pre class="EnlighterJSRAW" data-enlighter-language="generic">boolean isAllowed = tokenCount &gt; 0;

if (isAllowed) {
    tokenCount--;
}</pre>

**Step 5: Update Redis**   

We update the token count and last refill time in Redis. Use a transaction to ensure atomic updates:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Transaction transaction = jedis.multi();
transaction.set(keyLastRefill, String.valueOf(currentTime)); // Update last refill time
transaction.set(keyCount, String.valueOf(tokenCount));       // Update token count
transaction.exec();</pre>

### Complete Implementation {#h3-13-complete-implementation}

Here's the full code for the FixedWindowRateLimiter class:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package io.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TokenBucketRateLimiter {
    private final Jedis jedis;
    private final int bucketCapacity; // Maximum tokens the bucket can hold
    private final double refillRate; // Tokens refilled per second

    public TokenBucketRateLimiter(Jedis jedis, int bucketCapacity, double refillRate) {
        this.jedis = jedis;
        this.bucketCapacity = bucketCapacity;
        this.refillRate = refillRate;
    }

    public boolean isAllowed(String clientId) {
        String keyCount = "rate_limit:" + clientId + ":count";
        String keyLastRefill = "rate_limit:" + clientId + ":lastRefill";

        long currentTime = System.currentTimeMillis();

        // Fetch current state
        Transaction transaction = jedis.multi();
        transaction.get(keyLastRefill);
        transaction.get(keyCount);
        var results = transaction.exec();

        long lastRefillTime = results.get(0) != null ? Long.parseLong((String) results.get(0)) : currentTime;
        int tokenCount = results.get(1) != null ? Integer.parseInt((String) results.get(1)) : bucketCapacity;

        // Refill tokens
        long elapsedTimeMs = currentTime - lastRefillTime;
        double elapsedTimeSecs = elapsedTimeMs / 1000.0;
        int tokensToAdd = (int) (elapsedTimeSecs * refillRate);
        tokenCount = Math.min(bucketCapacity, tokenCount + tokensToAdd);

        // Check if the request is allowed
        boolean isAllowed = tokenCount &gt; 0;

        if (isAllowed) {
            tokenCount--; // Consume one token
        }

        // Update Redis state
        transaction = jedis.multi();
        transaction.set(keyLastRefill, String.valueOf(currentTime));
        transaction.set(keyCount, String.valueOf(tokenCount));
        transaction.exec();

        return isAllowed;
    }
}</pre>

And we're ready to start testing it's behavior!

Testing our Rate Limiter {#h2-14-testing-our-rate-limiter}
----------------------------------------------------------

To ensure our Token Bucket Rate Limiter behaves as expected, we'll write tests for various scenarios. For this, we'll use three tools:

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

The first step is to create a test class named FixedWindowRateLimiterTest. Inside, we'll define three main components:

1. **Redis Test Container**: This launches a Redis instance in a Docker container.
2. **Jedis Instance**: This connects to the Redis container for sending commands.
3. **Rate Limiter**: The actual TokenBucketRateLimiter instance we're testing.

Here's how the skeleton of our test class looks:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class TokenBucketRateLimiterTest {

    private static RedisContainer redisContainer;
    private Jedis jedis;
    private TokenBucketRateLimiter rateLimiter;</pre>

### Preparing the Environment Before Each Test {#h3-17-preparing-the-environment-before-each-test}

Before running any test, we need to ensure a clean Redis environment. Here's what we'll do:

1. **Connect to Redis**: Use a Jedis instance to connect to the Redis container.
2. **Flush Data**: Clear any leftover data in Redis to ensure consistent results for each test.

We'll set this up in a method annotated with @BeforeEach, which runs before every test case.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@BeforeAll
static void startContainer() {
    redisContainer = new RedisContainer("redis:latest");
    redisContainer.withExposedPorts(6379).start();
}

@BeforeEach
void setup() {
    jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
    jedis.flushAll();
}</pre>

> FLUSHALL is an actual Redis command that deletes all the keys of all the existing databases. [Read more about it in the official documentation](https://redis.io/docs/latest/commands/flushall/).

### Cleaning Up After Each Test {#h3-18-cleaning-up-after-each-test}

After each test, we need to close the Jedis connection to free up resources. This ensures no lingering connections interfere with subsequent tests.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@AfterEach
void tearDown() {
    jedis.close();
}</pre>

### Full Setup {#h3-19-full-setup}

Here's how the complete test class looks with everything in place:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class TokenBucketRateLimiterTest {

    private static RedisContainer redisContainer;
    private Jedis jedis;
    private TokenBucketRateLimiter rateLimiter;

    @BeforeAll
    static void startContainer() {
        redisContainer = new RedisContainer("redis:latest");
        redisContainer.withExposedPorts(6379).start();
    }

    @AfterAll
    static void stopContainer() {
        redisContainer.stop();
    }

    @BeforeEach
    void setup() {
        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
        jedis.flushAll();
    }

    @AfterEach
    void tearDown() {
        jedis.close();
    }
}</pre>

### Verifying Requests Within the Bucket Capacity {#h3-20-verifying-requests-within-the-bucket-capacity}

This test ensures the rate limiter allows requests within the defined bucket capacity.

We configure it with a **capacity of** **5 tokens** and a **refill rate of one token per second** , then call isAllowed("client-1") **5 times**.

Each call should return true, confirming the rate limiter correctly tracks and permits requests within the capacity.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void shouldAllowRequestsWithinBucketCapacity() {
    rateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
            .withFailMessage("Request %d should be allowed within bucket capacity", i)
            .isTrue();
    }
}</pre>

### Verifying Requests Are Denied When Bucket is Empty {#h3-21-verifying-requests-are-denied-when-bucket-is-empty}

This test ensures the rate limiter correctly denies requests once the bucket is empty.

Configured with a **capacity of** **5 tokens** and a **refill rate of one token per second** , we isAllowed("client-1") **5 times** and expect all to return true.

On the 6th call, it should return false, verifying the rate limiter blocks requests once the bucket is empty.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void shouldDenyRequestsOnceBucketIsEmpty() {
    rateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);
    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed("client-1"))
            .withFailMessage("Request %d should be allowed within bucket capacity", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed("client-1"))
        .withFailMessage("Request beyond bucket capacity should be denied")
        .isFalse();
}</pre>

### Verifying Bucket is Gradually Refilled {#h3-22-verifying-bucket-is-gradually-refilled}

This test ensures the rate limiter refills the bucket correctly after every second.

Configured with a **capacity of** **5 tokens** and a **refill rate of one token per second**, the first 5 requests (isAllowed("client-1")) return true, while the 6th request is denied (false).

After waiting for two seconds, the next two requests are allowed and the third one is denied. Confirming the refilling behavior works as expected.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void shouldRefillTokensGraduallyAndAllowRequestsOverTime() throws InterruptedException {
    rateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);
    String clientId = "client-1";

    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request %d should be allowed within bucket capacity", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Request beyond bucket capacity should be denied")
        .isFalse();

    TimeUnit.SECONDS.sleep(2);

    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Request after partial refill should be allowed")
        .isTrue();
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Second request after partial refill should be allowed")
        .isTrue();
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Request beyond available tokens should be denied")
        .isFalse();
}</pre>

### Verifying Independent Handling of Multiple Clients {#h3-23-verifying-independent-handling-of-multiple-clients}

This test ensures the rate limiter handles multiple clients independently.

Configured with a **capacity of** **5 tokens** and a **refill rate of one token per second**, the first 5 requests (isAllowed("client-1")) return true, while the 6th request is denied (false).

Simultaneously, all 5 requests from **client-2** are allowed (true), confirming the rate limiter maintains separate counters for each client.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void shouldHandleMultipleClientsIndependently() {
    rateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);

    String clientId1 = "client-1";
    String clientId2 = "client-2";

    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed(clientId1))
            .withFailMessage("Client 1 request %d should be allowed", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed(clientId1))
        .withFailMessage("Client 1 request beyond bucket capacity should be denied")
        .isFalse();

    for (int i = 1; i &lt;= 5; i++) {
        assertThat(rateLimiter.isAllowed(clientId2))
            .withFailMessage("Client 2 request %d should be allowed", i)
            .isTrue();
    }
}</pre>

### Verifying Token Refill Does Not Exceed Bucket Capacity {#h3-24-verifying-token-refill-does-not-exceed-bucket-capacity}

This test verifies that the token bucket rate limiter correctly refills tokens up to the defined capacity without exceeding it.

Configured with a **capacity of 3 tokens** and a **refill rate of 2 tokens per second**, the first 3 requests (isAllowed("client-1")) return true, while the 4th request is denied (false), indicating the bucket is empty.

After waiting 3 seconds (enough to refill 6 tokens), the bucket refills only up to its maximum capacity of 3 tokens. The next 3 requests are allowed (true), but any additional request is denied (false), confirming that the rate limiter maintains the specified capacity limit regardless of refill surplus.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void shouldRefillTokensUpToCapacityWithoutExceedingIt() throws InterruptedException {
    int capacity = 3;
    double refillRate = 2.0;
    String clientId = "client-1";
    rateLimiter = new TokenBucketRateLimiter(jedis, capacity, refillRate);

    for (int i = 1; i &lt;= capacity; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request %d should be allowed within initial bucket capacity", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Request beyond bucket capacity should be denied")
        .isFalse();

    TimeUnit.SECONDS.sleep(3);

    for (int i = 1; i &lt;= capacity; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request %d should be allowed as bucket refills up to capacity", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("Request beyond bucket capacity should be denied")
        .isFalse();
}</pre>

### Verifying Denied Requests Do Not Affect Token Count {#h3-25-verifying-denied-requests-do-not-affect-token-count}

This test ensures that the token bucket rate limiter does not count denied requests when updating the token count.

Configured with a **capacity of 3 tokens** and a **refill rate of 0.5 tokens per second**, the first 3 requests (isAllowed("client-1")) are allowed (true), depleting the bucket. The 4th request is denied (false), confirming the bucket is empty.

The Redis token count (rate_limit:client-1:count) is then verified to ensure it accurately reflects the remaining tokens (0 in this case) and does not include denied requests. This confirms that the rate limiter updates the token count only when requests are successfully processed.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Test
void testRateLimitDeniedRequestsAreNotCounted() {
    int capacity = 3;
    double refillRate = 0.5;
    String clientId = "client-1";
    rateLimiter = new TokenBucketRateLimiter(jedis, capacity, refillRate);

    for (int i = 1; i &lt;= capacity; i++) {
        assertThat(rateLimiter.isAllowed(clientId))
            .withFailMessage("Request %d should be allowed", i)
            .isTrue();
    }
    assertThat(rateLimiter.isAllowed(clientId))
        .withFailMessage("This request should be denied")
        .isFalse();

    String key = "rate_limit:" + clientId + ":count";
    int requestCount = Integer.parseInt(jedis.get(key));
    assertThat(requestCount)
        .withFailMessage("The count should match remaining tokens and not include denied requests")
        .isEqualTo(0);
}</pre>

Is there any other behavior we should verify? Let me know in the comments!

The Token Bucket Rate Limiter is a flexible and efficient way to manage request rates, and **Redis** makes it incredibly fast and reliable.

By leveraging commands like GET, SET, and MULTI/EXEC, we implemented a solution that tracks token counts, refills tokens dynamically based on time elapsed, and ensures the bucket never exceeds its defined capacity.

Using **Jedis** , we built a clear and intuitive **Java** implementation, and with thorough testing using Redis TestContainers, JUnit 5, and AssertJ, we can confidently verify that it works as expected.

This approach offers a robust foundation for managing request limits while allowing for burst handling and gradual refill, making it adaptable for more advanced rate-limiting scenarios when needed.

### GitHub Repo {#h3-26-github-repo}

You can find this implementation in **Java** and **Kotlin**:

* Java ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-java-example/tree/main/src/main/java/io/redis), [Test](https://github.com/raphaeldelio/redis-rate-limiter-java-example/blob/main/src/test/java/io/redis/TokenBucketRateLimiterTest.java))
* Kotlin ([Implementation](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/main/kotlin/org/example/TokenBucketRateLimiter.kt), [Test](https://github.com/raphaeldelio/redis-rate-limiter-kotlin-example/blob/main/src/test/kotlin/org/example/TokenBucketRateLimiterTest.kt))

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
