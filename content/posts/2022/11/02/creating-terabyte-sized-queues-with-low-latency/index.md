---
title: "Creating Terabyte Sized Queues with Low-Latency"
slug: "creating-terabyte-sized-queues-with-low-latency"
date: "2022-11-02T10:45:07+00:00"
lastmod: "2022-11-30T08:24:33+00:00"
description: "Learn how to create huge persisted queues while retaining predictable and consistent low latency using open-source Chronicle Queue!"
authors:
  - "per-minborg"
image: "Favicon-3-2.png"
categories:
  - "DevOps"
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "did-you-know-you-can-create-mappers-without-creating-underlying-objects-in-java"
  - "high-performance-java-serialisation"
enlighterjs: true
frozen: false
---

Queues are often fundamental components in software design patterns.

But, what if there are millions of messages received every second and multi-process consumers need to be able to read the complete ledger of all messages?

Java can only hold so much information before the heap becomes a limiting factor with high-impacting garbage collections as a result, potentially preventing us from fulfilling targeted SLAs or even halting the JVM for seconds or even minutes.

This article covers how to create huge persisted queues while retaining predictable and consistent low latency using [open-source Chronicle Queue.](https://chronicle.software/queue/ "open-source Chronicle Queue.")

### The Application {#h3-0-the-application}

In this article, the objective is to maintain a queue of objects from market data feeds (e.g. the latest price for securities traded on an exchange). Other business areas such as sensory input from IOT devices or reading crash-recording information within the automotive industry could have been chosen as well. The principle is the same.

To start with, a class holding market data is defined:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class MarketData extends SelfDescribingMarshallable {
    int securityId;
    long time;
    float last;
    float high;
    float low;

    // Getters and setters not shown for brevity
}</pre>

Note: In real-world scenarios, great care must be taken when using float and double for holding monetary values as this could otherwise cause rounding problems \[Bloch18, Item 60\]. However, in this introductory article, I want to keep things simple.

There is also a small utility function MarketDataUtil::create that will create and return a new random MarketData object when invoked:

<pre class="EnlighterJSRAW" data-enlighter-language="java">static MarketData create() {
    MarketData marketData = new MarketData();
    int id = ThreadLocalRandom.current().nextInt(1000);
    marketData.setSecurityId(id);
    float nextFloat = ThreadLocalRandom.current().nextFloat();
    float last = 20 + 100 * nextFloat;

    marketData.setLast(last);
    marketData.setHigh(last * 1.1f);
    marketData.setLow(last * 0.9f);
    marketData.setTime(System.currentTimeMillis());

    return marketData;
}
</pre>

Now, the objective is to create a queue that is durable, concurrent, low-latency, accessible from several processes and that can hold billions of objects.

### The Naïve Approach {#h3-1-the-na-ve-approach}

Armed with these classes, the naïve approach of using a ConcurrentLinkedQueue can be explored:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void main(String[] args) {
    final Queue queue = new ConcurrentLinkedQueue();
    for (long i = 0; i &lt; 1e9; i++) {
        queue.add(MarketDataUtil.create());
    }
}</pre>

This will fail for several reasons:

1. The *ConcurrentLinkedQueue* will create a wrapping Node for each element added to the queue. This will effectively double the number of objects created..
2. Objects are placed on the Java heap, contributing to heap memory pressure and garbage collection problems. On my machine, this led to my entire JVM becoming unresponsive and the only way forward was to kill it forcibly using "*kill -9*".
3. The queue cannot be read from other processes (i.e. other JVMs).
4. Once the JVM terminates, the content of the queue is lost. Hence, the queue is not durable.

Looking at various other standard Java classes, it can be concluded that there is no support for large persisted queues.

### Using Chronicle Queue {#h3-2-using-chronicle-queue}

Chronicle Queue is an open-source library and is designed to meet the requirements set forth above. Here is one way to set it up and use it:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void main(String[] args) {
    final MarketData marketData = new MarketData();
    final ChronicleQueue q = ChronicleQueue
            .single("market-data");
    final ExcerptAppender appender = q.acquireAppender();

    for (long i = 0; i &lt; 1e9; i++) {
        try (final DocumentContext document =
                     appender.acquireWritingDocument(false)) {
             document
                    .wire()
                    .bytes()
                    .writeObject(MarketData.class, 
                            MarketDataUtil.recycle(marketData));
        }
    }
}</pre>

Using a MacBook Pro 2019 with a 2.3 GHz 8-Core Intel Core i9, north of 3,000,000 messages per second could be inserted using only a single thread.

The queue is persisted via a memory mapped file in the given directory "*market-data*". One would expect a MarketData object to occupy 4 (int securityId) + 8 (long time) + 4\*3 (float last, high and low) = 24 bytes at the very least.

In the example above, 1 billion objects were appended causing the mapped file to occupy 30,148,657,152 bytes which translates to about 30 bytes per message. In my opinion, this is very efficient indeed.

As can be seen, a single *MarketData* instance can be reused over and over again because Chronicle Queue will flatten out the content of the current object onto the memory mapped file, allowing object reuse. This reduces memory pressure even more. This is how the recycle method works:

<pre class="EnlighterJSRAW" data-enlighter-language="java">static MarketData recycle(MarketData marketData) {
    final int id = ThreadLocalRandom.current().nextInt(1000);
    marketData.setSecurityId(id);
    final float nextFloat = ThreadLocalRandom.current().nextFloat();
    final float last = 20 + 100 * nextFloat;

    marketData.setLast(last);
    marketData.setHigh(last * 1.1f);
    marketData.setLow(last * 0.9f);
    marketData.setTime(System.currentTimeMillis());

    return marketData;
}</pre>

### Reading from a Chronicle Queue {#h3-3-reading-from-a-chronicle-queue}

Reading from a Chronicle Queue is straightforward. Continuing the example from above, the following shows how the first two MarketData objects can be read from the queue:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void main(String[] args) {
    final ChronicleQueue q = ChronicleQueue
            .single("market-data");
    final ExcerptTailer tailer = q.createTailer();

    for (long i = 0; i &lt; 2; i++) {
        try (final DocumentContext document =
                     tailer.readingDocument()) {
            MarketData marketData = document
                    .wire()
                    .bytes()
                    .readObject(MarketData.class);
            System.out.println(marketData);
        }
    }
}</pre>

This might produce the following output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">!software.chronicle.sandbox.queuedemo.MarketData {
  securityId: 202,
  time: 1634646488837,
  last: 45.8673,
  high: 50.454,
  low: 41.2806
}

!software.chronicle.sandbox.queuedemo.MarketData {
  securityId: 117,
  time: 1634646488842,
  last: 34.7567,
  high: 38.2323,
  low: 31.281
}</pre>

There are provisions to efficiently seek the tailer's position, for example, to the end of the queue or to a certain index.

### What's Next? {#h3-4-what-s-next}

There are many other features that are out of scope for this article.

For example, queue files can be set to roll at certain intervals (such as each day, hour or minute) effectively creating a decomposition of information so that older data may be cleaned over time.

There are also provisions to be able to isolate CPUs and lock Java threads to these isolated CPUs, substantially reducing application jitter.

Finally, there is an enterprise version with replication of queues across server clusters paving the way towards high availability and improved performance in distributed architectures.

The enterprise version also includes a variety of other features such as encryption, time zone rolling and asynchronous appenders.

### Resources {#h3-5-resources}

[Open-source Chronicle Queue](https://chronicle.software/queue/ "Open-source Chronicle Queue")

[Chronicle Queue Enterprise](https://chronicle.software/queue-enterprise/ "Chronicle Queue Enterprise")
