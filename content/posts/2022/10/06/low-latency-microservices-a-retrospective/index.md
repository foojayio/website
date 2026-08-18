---
title: "Low Latency Microservices, A Retrospective"
date: "2022-10-06T06:25:56+00:00"
lastmod: "2022-10-06T06:28:26+00:00"
description: "Learn what we learned after five years of developing and supporting low latency microservices."
authors:
  - "peter-lawrey"
image: "shutterstock_1111260050-350x233-1.jpg"
categories:
  - "Developer Tools"
  - "DevOps"
  - "Java Core"
related_posts:
  - "book-review-monolith-to-microservices-part-1"
  - "book-review-monolith-to-microservices-part-2"
  - "building-microservices-spring-boot-fat-uber-jar"
frozen: false
---

I wrote an article on [low latency microservices](https://vanilla-java.github.io/2016/03/22/Micro-services-for-performance.html "low latency microservices") almost five years ago now. Chronicle Software has worked with a number of tier one investment banks to implement and support those systems. What has changed in that time and what lessons have we learnt?

Read this article and learn what we learned after five years of developing and supporting low latency microservices.

### Separation of Concerns Gives Better Testability

Microservices repeatedly demonstrated that testing and debugging business components were much easier with simple, stand-alone components with clear contracts between microservices.

Unit tests were still used to start with. However in 2017 we moved almost entirely to behaviour driven development of microservices. Unit tests are still used for lower level libraries and utilities.

As our microservices are all based on [Kappa Architecture](https://milinda.pathirage.org/kappa-architecture.com/ "Kappa Architecture"), all our behaviour driven tests are modelled as a series of events in and out of the service.

An input test might look like this

```
---
oms: OMS1 # the service to receive this message
newOrder: {
  eventId: orderevent1,
  eventTime: 2017-04-27T07:26:40.9836487,
  triggerTime: 2017-05-05T12:56:40.7534873,
  instrument: USDCHF,
  settlementTime: 2017-04-26T09:46:40,
  market: FXALL_MID,
  orderId: orderid1,
  hedgerName: Hedger1,
  user: MidHedger,
  orderType: MARKET,
  side: SELL,
  quantity: 500E3,
  maxShowQuantity: 500E3,
  timeInForceType: GTC,
  timeInForceExpireTime: 2018-01-01T01:00:00
}
---
# more messages
```

The output looks very similar as this is an Order Management Service. The job of OMS and it's job is to normalise, filter and track orders.

```
---
newOrder: {
  eventId: orderevent1,
  eventTime: 2017-04-27T07:26:40.9836487,
  triggerTime: 2017-05-05T12:56:40.7534873,
  instrument: USDCHF,
  settlementTime: 2017-04-26T09:46:40,
  market: FXALL_MID,
  orderId: orderid1,
  hedgerName: Hedger1,
  orderType: MARKET,
  quantity: 500E3,
  user: MidHedger,
  side: SELL,
  maxShowQuantity: 500E3,
  timeInForceType: GTC,
  timeInForceExpireTime: 2018-01-01T01:00:00
}
---
# more results
```

Building variations on tests to explore all the things which could go wrong and check how they are handled is easy.

### What we Needed to Add

Beyond implementing what we envisioned five years ago, there were some features we discovered we needed to add.

#### 1. A Deterministic Clock

To ensure our services produced the same results every time, whether in tests or between production and any redundant system, we made the time an input. This appeared in our test like this

```
periodicUpdate: 2017-04-27T07:26:51
---
```

This ensured that all time outs or events triggered by the clock could be tested, but also ensure each redundant system did the same things at the same point, and produced the same output.

#### 2. Nanosecond Timestamps

We started with millisecond timestamps but quickly found we needed greater resolution switching to microseconds timestamps, and now use nanosecond resolution timestamps.

Nanosecond timestamps were more useful if we could also ensure some level of uniqueness. Nanosecond timestamps can be made unique on a single host in a low latency way. This takes well under 100 nanoseconds. That way a timestamp can be used as a unique id for tracing events through a system (adding a pre-set host id if needed).

#### 3. Ability to store complex data as primitives

For testing purpose, all messages appear as text, however for performance reasons all data is written/read in a binary form. The typical latency of a persisted message between microservices is less than a microsecond so how the objects are stored can made a big difference.

The use of String and LocalDateTime was very expensive for our use case. To reduce the impact of storing this information, we developed a number of strategies for:

* encoding Strings and dates in `long` fields.
* object pooling Strings
* storing text in a mutable/reusable field

Ultimately, this lead to the support of [Trivially Copyable](https://en.cppreference.com/w/cpp/named_req/TriviallyCopyable "Trivially Copyable") objects, a concept adopted from C++, where the majority (or entire) Java object could be copied as a memory copy without any serialization logic. This allowed us to support passing complex market data with around 50 fields between microservices in different processes at well under a microsecond most of the time.

#### 4. Ability to run microservices as a single process or a single thread

We have solved the problem of how to test and debug many microservices but allowing them to run in a single JVM or a single thread. This gave our customers the flexibility of running parts of the system as a compound microservice in the test environment while they could still deploy individual microservices independently in production.

#### 5. Simplifying restarts with idempotency

Idempotency allows an operation to be harmlessly attempted multiple times. Restarts are simplified by replaying any messages you are not sure if they were processed fully.

In low latency systems, transactionality needs to be as simple as possible. This suits most needs, however when you get a complex transaction, idempotency can significantly reduce the complexity of recovery.

#### 6. Multiple replay strategies

Having a complete deterministic persisted record of every message made restarts and fail over simpler, however we found that different use cases needed different strategies as to how that is done. We have learnt a lot about real replayability/restartability requirements from customers' real use cases and the trade offs they make for service start time vs accuracy etc.

One key feature is ensuring upstream messages are replicated before downstream messages to simplify rebuilding the current state.

### What became less important

Some of the features we though would be vital five years ago, turned out to be not always required as clients used our technology in broader contexts.

#### 1. Ultra low garbage collection

Five years ago, we were entirely focused on no minor collections over a day of processing.

However, many clients wanted the productivity gains we offered but didn't have a stringent performance requirements and occasional garbage collections were not a concern.

Creating microservices naturally supports having latency critical services as well as less latency critical services in the same system.

This required a shift in some of our thinking which assumed GCs never occurred normally. We significantly reduced our use of WeakReferences for example.

#### 2. Low throughput data processing

The lowest latencies tend to be at around 1% of the peak capacity. At lower throughputs you tend to get either your hardware trying to power save, increasing latency when an event does occur, and at higher throughputs, you increase the chance of a message coming in while you are still processing previous ones, adding to latency.

Again, we saw a broadening of use cases to very low message rates which showed up behaviour which is only seen when the machine is spending most of it's time waiting. We added [performance test tools to see how our code behaves when the CPU is running cold.](https://github.com/OpenHFT/Chronicle-Core/tree/ea/src/main/java/net/openhft/chronicle/core/cooler "performance test tools to see how our code behaves when the CPU is running cold.")

We also saw the need to support higher throughputs of messages for hours at time (rather than seconds or minutes). For example, if we had a microservice which could process a million messages per second, we would test the latency at 1% of this as this was considered the normal volume.

It also wasn't possible to get high performance, high volume drives which could sustain this rate for hours without filling up. Today, we are testing the latency of systems sustaining long bursts of one million messages per second for many hours at a time.

If you are looking for such a drive you can test in a desktop, I suggest looking at the [Corsair MP600 Pro series](https://www.corsair.com/uk/en/Categories/Products/Storage/M-2-SSDs/MP600-PRO-XT/p/CSSD-F4000GBMP600PXT "Corsair MP600 Pro series")

### Tip: Make your infrastructure as fast as your application needs

Over the last five years, the requirements for core systems have been more stringent, however as we need to integrate with existing system we have see the need to easily support systems which don't have the same requirements (and would rather it be easy and natural to work with)

For the more stringent systems, the latencies clients care about

* have moved from the 99%ile (worst 1 in 100) to the 99.9%ile or 99.99%ile
* The latencies they are looking to achieve, wire to wire (as measured on the network), is more around the 99.99%ile at 20 microsecond or 99.9%ile at 100 microseconds
* have a clearer idea as the worst case latencies they need to see. Many are looking for low milliseconds end to end worst case

At the same time, our client need to integrate with existing systems, were all they need is for that to be as easy as possible.

### Tip: Make the message format a configuration consideration

Five years ago I imagined we would need to support all sorts of formats however a relatively small number turned out to be really useful.

* An existing format. Make using a corporate standard format easy, no need to use ours
* Text format, YAML seems the best one, esp for readability and typed data
* Binary form of YAML. Good trade off between ease of use and performance
* Typed JSON for use over websockets to Javascript based UIs
* Marshalling of fields as binary
* Direct memory copy objects e.g. Trivially Copyable, to maximise speed
* FIX protocol

We regularly use a single DTO in multiple formats depending on what is most appropriate without the need to copy data between DTO specialised for a given format.

### Conclusion

For the use case of our clients, I believe most of the concerns around replacing microservices with a monolith have been solved.

However, having the option to run multiple microservices as if it was a monolith handles those cases where it is easier to work with e.g. testing and debugging multiple services.

#### Links

[OpenHFT open source libraries](https://github.com/OpenHFT "OpenHFT open source libraries")  
[https://github.com/OpenHFT](https://github.com/OpenHFT "https://github.com/OpenHFT")
