---
title: "Testing Kafka Consumer Recovery Contracts With Testcontainers and JUnit"
date: "2026-09-28"
description: "Use Testcontainers and JUnit to test Kafka consumer recovery contracts for duplicate delivery, malformed records, and offset-boundary failures."
authors:
  - "ishan-shah"
image: "kafka-consumer-recovery-contracts.jpg"
categories:
  - "Java"
  - "Kafka"
  - "Testcontainers"
  - "Testing"
related_posts:
  - "when-not-to-use-event-driven-architecture-eda"
  - "eliminating-flaky-tests-to-end-world-hunger"
  - "testing-emails-with-testcontainers-and-mailpit"
  - "event-driven-architecture-in-java-and-kafka"
---

A Kafka consumer can pass a unit test and still fail when production redelivers a
record.

That happens because the unit test usually checks the handler, not the recovery
contract. It verifies that one message produces one expected side effect. It
rarely verifies what happens when the same event arrives twice, when a malformed
record reaches the topic, or when the consumer applies a side effect and then
crashes before committing the offset.

For Java teams, Testcontainers is a practical middle ground. It lets you run a
test against a real Kafka broker without maintaining a shared integration
environment. But the important part is not the container. The important part is
the contract you test.

## Define The Recovery Contract First

Before writing code, define what the consumer promises. A useful recovery
contract answers these questions:

- When is the business side effect considered complete?
- When is the Kafka offset committed?
- What idempotency key protects duplicate delivery?
- What happens to malformed records?
- What evidence shows a failed record was handled intentionally?

For an order projection consumer, the contract might be:

1. A valid event updates the projection once.
2. A duplicate event does not apply the side effect twice.
3. A malformed event is routed to a dead-letter topic with enough context to
   inspect it.
4. If the consumer crashes after the side effect but before the offset commit,
   redelivery does not create a duplicate business effect.

Those promises are more useful than a generic "Kafka integration test."

## Use Testcontainers As The Harness

The current Testcontainers Kafka module uses classes under the
`org.testcontainers.kafka` package. The old
`org.testcontainers.containers.KafkaContainer` class is deprecated in the
current docs.

A minimal Maven test setup looks like this:

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.14.4</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>testcontainers-junit-jupiter</artifactId>
  <version>2.0.5</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>testcontainers-kafka</artifactId>
  <version>2.0.5</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>3.9.2</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <version>3.26.3</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.awaitility</groupId>
  <artifactId>awaitility</artifactId>
  <version>4.3.0</version>
  <scope>test</scope>
</dependency>
```

Then start Kafka in a JUnit 5 test:

```java
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
class OrderConsumerRecoveryContractTest {

    @Container
    static final KafkaContainer KAFKA =
        new KafkaContainer("apache/kafka-native:3.8.0");
}
```

That only shows the broker starts. The test still needs a controlled side
effect and observable recovery outcomes.

## Test Duplicate Delivery

Kafka consumers should assume duplicate delivery unless the whole processing
path is designed otherwise. That does not mean every system needs exactly-once
processing. It means the business side effect should be protected by an
idempotency key.

For this sample, the event is a simple pipe-delimited string:

```text
evt-1|order-1|1299
```

The first field is the event ID. That is the idempotency key.

```java
@Test
void duplicateEventDoesNotUpdateProjectionTwice() throws Exception {
    publish(ordersTopic, "order-1", "evt-1|order-1|1299");
    publish(ordersTopic, "order-1", "evt-1|order-1|1299");

    try (RecoveringOrderConsumer consumer = consumer("duplicate-contract", false)) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            consumer.pollAndProcessAvailable();
            assertThat(consumer.consumedRecordCount()).isEqualTo(2);
            assertThat(projectionStore.totalFor("order-1")).isEqualTo(1299);
            assertThat(projectionStore.appliedEventCount("evt-1")).isEqualTo(1);
        });
    }
}
```

The consumed-record assertion matters. Without it, the test might pass after the
first record and never prove that the duplicate path was exercised. The consumer
may see two records, but the projection should accept the event ID once for this
contract.

## Test Malformed Records

Invalid records should not disappear. They should move to an owned failure path
with enough context for inspection.

```java
@Test
void malformedRecordGoesToDeadLetterTopic() throws Exception {
    publish(ordersTopic, "order-1", "{broken-json");

    try (RecoveringOrderConsumer consumer = consumer("dlt-contract", false)) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            consumer.pollAndProcessAvailable();
            ConsumerRecord<String, String> dltRecord =
                pollOne(deadLetterTopic, "dlt-reader");

            assertThat(dltRecord.key()).isEqualTo("order-1");
            assertThat(dltRecord.value()).isEqualTo("{broken-json");
            assertThat(headerValue(dltRecord, "failure-reason"))
                .isEqualTo("malformed-order-event");
            assertThat(headerValue(dltRecord, "original-topic"))
                .isEqualTo(ordersTopic);
        });
    }
}
```

In a production test, also assert the original partition, original offset, error
class, consumer group, and schema version if those fields are part of your
failure contract.

## Test The Side-Effect Boundary

The hardest bugs often live between the business side effect and the offset
commit.

If the consumer commits before the side effect, a crash can lose work. If it
commits after the side effect, a crash can redeliver a record whose side effect
already happened. The second pattern is common, but it only works if the side
effect is idempotent.

This test forces the boundary:

```java
@Test
void crashAfterSideEffectIsRecoveredByIdempotencyKey() throws Exception {
    publish(ordersTopic, "order-1", "evt-2|order-1|2500");

    try (RecoveringOrderConsumer firstAttempt = consumer("restart-contract", true)) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            firstAttempt.pollAndProcessAvailable();
            assertThat(firstAttempt.consumedRecordCount()).isEqualTo(1);
            assertThat(firstAttempt.lastConsumedEventId()).isEqualTo("evt-2");
            assertThat(projectionStore.totalFor("order-1")).isEqualTo(2500);
            assertThat(projectionStore.appliedEventCount("evt-2")).isEqualTo(1);
            assertThat(committedOffset("restart-contract")).isNull();
        });
    }

    try (RecoveringOrderConsumer secondAttempt = consumer("restart-contract", false)) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            secondAttempt.pollAndProcessAvailable();
            assertThat(secondAttempt.consumedRecordCount()).isEqualTo(1);
            assertThat(secondAttempt.lastConsumedEventId()).isEqualTo("evt-2");
            assertThat(projectionStore.totalFor("order-1")).isEqualTo(2500);
            assertThat(projectionStore.appliedEventCount("evt-2")).isEqualTo(1);
        });
    }
}
```

The first consumer writes the projection and simulates a crash before committing
the offset, so the committed offset should still be absent for that group and
topic. The second consumer uses the same group ID, receives `evt-2` again, and
relies on the idempotency key to avoid a duplicate business effect.

## Avoid Sleep-Based Tests

Kafka recovery tests often become flaky because they use `Thread.sleep` and hope
the consumer catches up.

Prefer observable conditions:

- projection row reaches the expected value
- idempotency key exists once
- dead-letter topic contains the expected record
- committed offset changes only after successful handling
- retry counter reaches the expected limit

Use a bounded wait utility such as Awaitility, but wait for the business
condition, not for a guessed delay.

## What Not To Claim

Do not claim that Testcontainers proves production behavior perfectly. It does
not reproduce your broker fleet, network, partition count, storage behavior,
security configuration, or consumer-group scale.

That is fine. The goal of this test is narrower and still valuable: test whether
your consumer code honors its recovery contract against a real Kafka broker.

## Checklist

Before calling a Kafka consumer recovery-tested, verify:

- duplicate delivery is safe
- malformed records have an owned failure path
- side effects are idempotent
- offset commit behavior is tested around successful handling and intentional
  failure routing
- restart behavior is tested
- dead-letter records include useful context
- tests assert outcomes, not handler calls

## Conclusion

Kafka does not promise that your application side effect will happen exactly
once. It gives you a log, offsets, consumer groups, and client APIs. Your
application owns the recovery contract around those primitives.

Testcontainers and JUnit are useful because they let you test that contract
against a real broker. But the broker is only the harness. The real work is
defining the consumer's promises and exercising the failure cases that production
will eventually surface.

## Sources

- [Testcontainers Kafka module](https://java.testcontainers.org/modules/kafka/)
- [Apache Kafka design documentation, message delivery semantics](https://kafka.apache.org/design/)
- [Awaitility usage documentation](https://github.com/awaitility/awaitility/wiki/Usage)
