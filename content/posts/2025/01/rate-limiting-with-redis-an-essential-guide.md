---
title: "Rate limiting with Redis: An essential guide"
slug: "rate-limiting-with-redis-an-essential-guide"
date: "2025-01-13T18:23:26+00:00"
lastmod: "2025-02-22T20:32:09+00:00"
description: "Discover the essentials of rate limiting with Redis. Learn about popular algorithms like Leaky Bucket, Token Bucket, and Sliding Window, and how to choose..."
canonical: "https://raphaeldelio.com/2024/12/23/rate-limiting-with-redis-an-essential-guide/"
authors:
  - "raphael-delio"
image: "https://foojay.io/wp-content/uploads/2025/01/Redis_Video_RateLimiterImplementations_Part1_YoutubeThumbnail.png"
categories:
  - "Databases"
  - "nosql"
tags:
related_posts:
  - "archunit-testing-your-architecture"
  - "back-to-basics-accessing-kubernetes-pods"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "sliding-window-counter-rate-limiter-redis-java"
frozen: false
---

[This article is also available on YouTube!](https://www.youtube.com/watch?v=YV4ePyW3DO8) Rate limiting --- it's something you've likely encountered, even if you haven't directly implemented one. For example, have you ever been greeted by a "429 Too Many Requests" error? That's a rate limiter in action, protecting a resource from overload. Or maybe you've used a service with explicit request quotas based on your payment tier --- same concept, just more transparent. ![ChatGPT warning user that they have reached the limit of messages they can send in 24 hours.](https://cdn-images-1.medium.com/max/3412/1*YKz05kbmkzQdws-DUsvhdw.png) Rate limiting isn't just about setting limits; it serves a variety of purposes. Take Figma, for instance. Their rate limiter, built with Redis, saved them from a spam attack where bad actors sent massive document invitations to random email addresses. Without it, Figma could have faced skyrocketing email delivery costs and damaged reputation. Or look at Stripe: as their platform grew, they realized they couldn't just throw more infrastructure at the problem. They needed a smarter solution to prevent resource monopolization by misconfigured scripts or bad actors. These stories show just how versatile rate limiting is. It prevents abuse, ensures fair access, manages load, cuts costs, and even protects against downtime. But here's the kicker: the hard part isn't knowing *why* you need a rate limiter. The real challenge is building one that's both efficient and tailored to your needs.

**Why Redis for Rate Limiting?** {#h2-0-why-redis-for-rate-limiting}
--------------------------------------------------------------------

Redis has become a go-to tool for implementing rate limiters, and for good reason. It's fast, reliable, and packed with features like atomic operations, data persistence, and Lua scripting. Just ask GitHub. When they migrated to a Redis-backed solution with client-side sharding, they solved tough challenges like replication, consistency, and scalability while ensuring reliable behavior across their infrastructure. So, why Redis? Its speed, versatility, and built-in capabilities make it perfect for handling distributed traffic patterns. But what's even more important is *how* you use it. Let's break down the most common rate-limiting patterns you can implement with Redis and what each one brings to the table.

**Popular Rate-Limiting Patterns** {#h2-1-popular-rate-limiting-patterns}
-------------------------------------------------------------------------

Choosing the right rate-limiting algorithm can be challenging. Here's a breakdown of the most popular options, when to use them, and their trade-offs, with practical examples to help you decide:

### **Leaky Bucket** {#h3-2-leaky-bucket}

**How It Works** : Imagine a bucket with a small hole at the bottom. Requests (water) are added to the bucket and processed at a steady "drip" rate, preventing sudden floods. ![](https://cdn-images-1.medium.com/max/2160/1*UioRG8-qID51i0rEOPVh-w.gif) **Use Cases:** Ideal for smoothing traffic flow, such as in streaming services or payment processing, where a predictable output is critical. **Example:** A video streaming platform regulates API calls to its content delivery network, ensuring consistent playback quality. **Drawback:** Not suitable for handling sudden bursts, like flash sales or promotional campaigns.

### **Token Bucket** {#h3-3-token-bucket}

**How It Works:** Tokens are generated at a fixed rate and stored in a bucket. Each request consumes a token, allowing for short bursts as long as tokens are available. ![](https://cdn-images-1.medium.com/max/2160/1*7cDKq5yh5RD0ygvb3mVwfQ.gif) **Use Cases:** Perfect for APIs that need to handle occasional traffic spikes while enforcing overall limits, such as login attempts or search queries. **Example:** An e-commerce site allows bursts of up to 20 requests per second during checkout but limits the overall rate to 100 requests per minute. **Drawback Example:** Requires periodic token replenishment, which can introduce minor overhead in distributed systems.

### Fixed Window Counter {#h3-4-fixed-window-counter}

**How It Works:** Tracks the number of requests in fixed intervals (e.g., 1 minute). Once the limit is reached, all subsequent requests in that window are denied. ![](https://cdn-images-1.medium.com/max/2160/1*VsdNn5KGd1A0rIfbczGy8Q.gif) **Use Cases:** Simple APIs with predictable traffic and low precision needs, like throttling a hobbyist developer's free-tier usage. **Example:** A public weather API allows 100 requests per user per minute, with any extra requests returning a "429 Too Many Requests" response. **Drawback:** Users can game the system by stacking requests at the boundary of two time windows (e.g., 100 at 59 seconds and 100 at 1 second of the next window).

### **Sliding Window Log** {#h3-5-sliding-window-log}

**How It Works:** Maintains a log of timestamps for each request and calculates limits based on a rolling time window. ![](https://cdn-images-1.medium.com/max/2160/1*tmaCfNHgzaAJNop4Aa2afA.gif) **Use Cases:** Critical systems requiring high accuracy, such as financial transaction APIs or fraud detection mechanisms. **Example:** A banking API limits withdrawals to 10 per hour, with each new request evaluated against the timestamps of the last 10 requests. **Drawback:** High memory usage and computational cost when scaling to millions of users or frequent requests.

### **Sliding Window Counter** {#h3-6-sliding-window-counter}

**How It Works:** Divides the time window into smaller intervals (e.g., 10-second buckets) and aggregates request counts to approximate a rolling window. ![](https://i0.wp.com/raphaeldelio.com/wp-content/uploads/2024/12/Sliding-Window-Counter.gif?resize=1080%2C608&ssl=1) **Use Cases:** APIs that need a balance between accuracy and efficiency, like chat systems or lightweight rate-limiting for microservices. **Example:** A messaging app limits users to 30 messages per minute but divides the minute into 6 buckets, allowing more flexibility in traffic patterns. **Drawback:** Small inaccuracies can occur, especially during highly bursty traffic patterns.

**Choosing the Right Tool for the Job** {#h2-7-choosing-the-right-tool-for-the-job}
-----------------------------------------------------------------------------------

Selecting a rate-limiting strategy isn't just about matching patterns to scenarios; it's about understanding the trade-offs and the specific needs of your application. Here's how to make a more informed choice:

### **Understand Your Traffic Patterns** {#h3-8-understand-your-traffic-patterns}

* **Predictable Traffic** : If your API serves consistent request rates (e.g., hourly status checks or regular polling), **Leaky Bucket** is excellent for maintaining a steady flow.
* **Burst Traffic** : If you expect short bursts of traffic, such as during product launches or login spikes, **Token Bucket** allows controlled bursts while enforcing limits.
* **Mixed Traffic** : APIs with unpredictable traffic may benefit from **Sliding Window Counter**, which balances accuracy and resource usage.

### **Assess the Level of Precision Needed** {#h3-9-assess-the-level-of-precision-needed}

* **High Precision** : If exact limits are critical (e.g., financial transactions or fraud detection), **Sliding Window Log** provides the most accurate enforcement by logging every request.
* **Approximation is Okay** : For most APIs, **Sliding Window Counter** strikes a balance between precision and efficiency, as it uses aggregated data instead of tracking every request.

### **Consider Resource Constraints** {#h3-10-consider-resource-constraints}

* **Memory and CPU Overhead** : Algorithms like **Sliding Window Log** can become resource-intensive at scale, especially with millions of users. For a lightweight alternative, **Fixed Window Counter** is simple but effective for low-traffic APIs.
* **Scalability** : Redis makes scaling rate limiting easier with atomic operations, Lua scripting, and replication features, but your choice of algorithm still affects performance. For instance, **Token Bucket** is computationally cheaper than **Sliding Window Log** in most distributed systems.

### Account User Experience {#h3-11-account-user-experience}

* **User Tolerance for Errors** : Fixed-window approaches like **Fixed Window Counter** may frustrate users due to rigid resets. Sliding-window methods smooth out these boundaries, leading to a better user experience.
* **Handling Edge Cases** : Algorithms like **Token Bucket** allow some flexibility for bursts, which can help avoid unnecessary rate-limit errors during legitimate usage spikes.

In the end, rate limiting is about more than just enforcing boundaries --- it's about designing systems that are efficient, fair, and user-friendly. By carefully matching the algorithm to your use case, you're not just managing traffic --- you're shaping a better experience for everyone involved. Implementations for each type of rate limiter with Java \& Redis will be released weekly! Stay tuned!

### Stay curious! {#h3-12-stay-curious}

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
