---
title: "Idempotent Spring Boot: From 0.1 to 1.1 — Lessons in Reliability, Performance, and Open Source"

date: "2026-11-19"

description: "How an idempotent Spring Boot starter evolved from 0.1 to 1.1: exactly-once JDBC, production fixes, a stability promise, and a first outside contribution."

authors:
  - "youssef"

image: "idempotent-spring-boot-1-1.jpg"

categories:
  - "Java"
  - "Spring"
  - "Library"
  - "Microservices"
  - "Design Patterns"
  - "Databases"
  - "Observability"

related_posts:
  - "idempotent-spring-boot-starter"
  - "idempotent-write-paths-keeping-an-integration-pipeline-correct-when-messages-repeat"
---

## A pod dies mid-request

A pod is killed halfway through a payment request. The client does the right thing and retries, and gets a `409 Conflict`. It retries an hour later and gets another `409`. Until 0.4 of my library, that key stayed locked for a full 24 hours.

The library is [idempotency-spring-boot-starter](https://github.com/benhendayoussef/idempotency-spring-boot-starter). It exists because retries are normal: networks time out, load balancers replay, users double-tap. A second `POST` that arrives after the first one already ran shouldn't charge anyone twice. I introduced it on Foojay in August with [Idempotent Spring Boot: Safe REST with One Annotation](https://foojay.io/today/idempotent-spring-boot-starter/).

Between then and now it went from 0.1 to 1.1. Every release fixed a concrete failure mode, and almost every fix had a cost I had to decide whether to pay. That chain of decisions is more useful than the feature list, so this article follows it release by release.

## 0.1: one annotation, honest gaps

```java
@Idempotent
@PostMapping("/orders")
public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
    return ResponseEntity.status(CREATED).body(orders.charge(request)); // orders: your existing service
}
```

0.1 claims the key atomically: one `SET ... NX` in Redis, or an `INSERT ... ON CONFLICT` in Postgres. It runs the handler once, stores the response, and replays it for every duplicate. A 5xx releases the key, so a legitimate retry can re-execute. A 4xx is deterministic, so it's kept and replayed. The same key with a different body gets a `422`.

It also shipped with a list of what it couldn't do. It was at-least-once, servlet only, Postgres only for JDBC, and Spring Boot 4 only. The next four releases worked through that list.

## 0.2: exactly-once, and exactly what that means

In 0.1 the business data and the idempotency record were committed in **two** transactions. The aspect deliberately runs outside `@Transactional`, so a replay never opens a transaction at all. The cost is a window between the two commits:

```
BEGIN TX ──► business logic ──► COMMIT ──► [ CRASH WINDOW ] ──► write COMPLETED record
```

Crash inside it, and the retry runs again. 0.2 closes the window with one opt-in property:

```yaml
idempotency:
  store: jdbc
  jdbc:
    join-transaction: true   # default false
```

With it on, the aspect opens one transaction around the handler **and** the completion write, and your handler's own `@Transactional(REQUIRED)` joins it. One commit, no window. The claim still commits separately and first. If the `IN_PROGRESS` row only became visible when your handler committed, concurrent duplicates would race straight past it.

**Be precise about what "exactly-once" covers.** It covers the database writes that take part in that same transaction, and nothing else. A call to a payment provider, an email or a message to a broker is outside it. If the process dies after the email has gone out but before the commit, the transaction rolls back. Once the claim expires, the retry runs again and sends the email again.

For external side effects, the usual answer is the **transactional outbox**: write the intent as a row in the same transaction, and let a separate relay deliver it. The library does not implement an outbox. What `join-transaction` gives you is that the outbox row and the idempotency record commit or roll back together.

**What it costs.** It is opt-in because it changes how every `@Idempotent` method runs:

- A handler with no `@Transactional` of its own now runs inside one, and **holds a database connection for its whole duration**.
- Your handler's own `@Transactional(timeout = ...)` stops applying, because a participant can't change the transaction it joined. Use `spring.transaction.default-timeout` instead.
- A handler that asks for `REQUIRES_NEW` opts out of joining, and the library does not override that.

To see what the held connection means, I measured it:

| Mode | Median | Max | Wall clock for all 16 |
|---|---|---|---|
| Default | ~300–345ms | ~310–415ms | **~310–420ms** |
| `join-transaction=true` | ~835ms | ~1110–1215ms | **~1115–1215ms** |

**How it was measured.**
- 16 concurrent requests with distinct keys.
- The handler has no `@Transactional` of its own and spends 250ms outside the database, standing in for an external call.
- A deliberately small 4-connection pool, real Tomcat and real Postgres, measured after warmup.
- You can reproduce it with `./gradlew :idempotency-spring-boot-starter:test --tests "*TxJoin*LoadTest*" -i`.

**What it shows, and what it doesn't.** In joined mode, concurrency is capped at the pool size: 16 ÷ 4 × 250ms ≈ 1s, which is what the table shows. No requests failed; the cost is latency, until the pool's `connection-timeout` is reached. The pool was undersized on purpose to make the effect visible. The gap is a ratio of your pool size to your in-flight concurrency, not a fixed penalty, and a pool at or above peak in-flight concurrency shows none. **Size your pool for concurrent in-flight requests, not for query time.**

## 0.3: fitting the stack you already have

0.3 removed the "I can't adopt this because…" reasons:

- **One artifact for Spring Boot 3.5.x and 4.1.x.** It compiles against the lower bound, and CI runs the full suite on both generations.
- **MySQL and MariaDB.** These needed a different claim *algorithm*, not just different SQL. Connector/J defaults to `useAffectedRows=false`, so a duplicate can look like a fresh insert if you trust the row count. So each dialect now owns its whole claim logic. That decision paid off in 1.1.
- **A Caffeine store** for single instances. The built-in memory store never evicts, which is fine for tests and a slow leak for anything long-lived.
- **WebFlux** for `Mono` handlers, where waiting on a duplicate holds no thread.
- **Filter mode** for byte-exact replay of bodies written straight to the response. It gives up argument fingerprinting.
- **Micrometer metrics**, so you can see what the library is doing (more below).

## 0.4: what happens when a pod dies

That was the bug from the opening. The in-flight claim and the completed record shared one TTL. A process that died mid-request (a deploy, an OOM, a scale-down) left its key `IN_PROGRESS` for the whole retention window. With the 24h default, every retry got a `409` for a day, and the sweeper couldn't help, because it only removes rows already past expiry.

0.4 split it into two lifetimes:

| | Governs | Default |
|---|---|---|
| `claim-ttl` | How long an **in-flight** claim is held before the holder is presumed dead | `5m` |
| `default-ttl` | How long a **completed** response stays replayable | `24h` |

The trade-off is real: **`claim-ttl` must outlive your slowest handler.** If a claim expires while the request is still running, a concurrent duplicate can reclaim the key and both execute. Raise it if your handlers legitimately run longer than five minutes.

0.4 also added two operational tools:
- **An actuator endpoint** to inspect or evict a stuck key. It is not exposed by default, because anyone who can evict a key can defeat idempotency for that request.
- **An `idempotency.outcome` tag** on the request's trace span. Without it, a replay (`201`, no query, two milliseconds) looks like a handler that silently did nothing.

## 1.0: compatibility is the feature

1.0 added no features. It published a promise: nothing in the list below breaks without a 2.0.

- the public API package
- every `idempotency.*` property name and meaning
- the actuator endpoint
- the metric and trace names
- **the storage key format**
- **the stored record format**

The last two aren't Java types, and they matter most.

**The storage key** is `sha256(namespace | method | routePattern | clientKey)`. If a release changed how it's computed, every record already in your store would become unreachable. A client retrying yesterday's request would get a fresh execution instead of a replay, which is exactly the double charge the library exists to prevent.

**The stored record format** matters during a rolling upgrade. For a few minutes, old and new instances serve traffic side by side. A record written by one has to be readable by the other, or in-flight keys get stranded.

1.0 also made the one breaking change that could not wait. `ScopeResolver.namespace()` now receives an `IdempotencyContext` instead of reading the `SecurityContextHolder` ThreadLocal, which is empty on a reactive stack. That is what lets reactive scoping arrive later as an additive change.

A bug-fix release, 1.0.1, followed on October 29. It fixed filter mode on Oracle. It also changed what happens after you switch from aspect mode to filter mode: a key written by aspect mode used to fail with a Jackson error, and now gets a clear `409 Conflict`. Such a key is never re-executed.

## 1.1: the first release I didn't write

1.1.0 adds **Oracle** to the JDBC store, alongside Postgres and MySQL/MariaDB. Set `idempotency.jdbc.dialect=oracle`, or leave it on `auto`. The schema's `IF NOT EXISTS` needs Oracle 23ai, or 19c with patch 19.28 or later.

The story behind it went like this:
- **October 30:** Arnaldo De Maio opened [issue #15](https://github.com/benhendayoussef/idempotency-spring-boot-starter/issues/15). His first attempt to push a branch got a `403`, so I walked him through the fork-and-pull-request flow. I flagged the two Oracle traps up front: it stores an empty string as `NULL`, and it has no `INSERT ... ON CONFLICT`.
- **November 2:** he opened [pull request #16](https://github.com/benhendayoussef/idempotency-spring-boot-starter/pull/16).
- **November 3:** I merged it with a small follow-up of my own, and released 1.1.0.

**Architecture is what made a four-day contribution possible.** Three decisions from earlier releases did the work:

- **The dialect owns the claim.** Since 0.3, a dialect implements the whole claim-or-reclaim operation, not a SQL string the core fills in. Arnaldo's Oracle dialect uses `MERGE`, handles two simultaneous first inserts through `DuplicateKeyException`, and compares expiry against the database's own `SYSTIMESTAMP` rather than the JVM clock. None of that touched the core.
- **One behaviour suite for every store.** His Oracle test extends the same abstract behaviour suite that Redis, Postgres, MySQL and Caffeine run. Correctness wasn't something he had to argue; the suite either passed or it didn't.
- **The compatibility promise as a review checklist.** One change in the PR mapped a missing `status` to `-1` instead of `null` on *every* database. Because the stored record format is covered by 1.0, that was a clear blocker rather than a matter of taste, and it was easy to explain why.

The review also surfaced a real gap. A test had been adjusted to work around Oracle's empty-string behaviour. The right fix belonged in the library, not the test: the Oracle schema leaves `fingerprint` nullable, and the store reads `NULL` back as an empty string. That went into the follow-up, [pull request #17](https://github.com/benhendayoussef/idempotency-spring-boot-starter/pull/17). Thank you, Arnaldo.

## Using it day to day

Add the starter and a store:

```kotlin
dependencies {
    implementation("io.github.benhendayoussef:idempotency-spring-boot-starter:1.1.0")
    implementation("io.github.benhendayoussef:idempotency-store-redis:1.1.0")
}
```

Put `@Idempotent` on the handler (the example under 0.1), then send the same request twice with the same key:

```bash
KEY=$(uuidgen)
curl -i -X POST localhost:8080/orders -H "Idempotency-Key: $KEY" \
     -H "Content-Type: application/json" -d '{"amount":49.99}'
curl -i -X POST localhost:8080/orders -H "Idempotency-Key: $KEY" \
     -H "Content-Type: application/json" -d '{"amount":49.99}'
```

The second response is the first one replayed, with an `Idempotent-Replay: true` header, and the handler doesn't run again. The [README](https://github.com/benhendayoussef/idempotency-spring-boot-starter#readme) covers the JDBC and Caffeine setups.

**Metrics.** If your application has a Micrometer `MeterRegistry` (adding `spring-boot-starter-actuator` is enough), the starter publishes metrics automatically. Every outcome lands on one counter, `idempotency.requests`, tagged by `outcome` (`executed`, `replayed`, `conflict`, `released`, and others). So the replay rate is a single ratio, and an outcome added in a later version shows up in your query without any change:

```promql
sum(rate(idempotency_requests_total{outcome="replayed"}[5m]))
  / sum(rate(idempotency_requests_total[5m]))
```

With Micrometer Tracing configured (actuator plus a bridge such as `micrometer-tracing-bridge-otel` or `-brave`), the trace tag uses the same values, so a dashboard and a trace search select the same requests. Without those dependencies, neither is added.

**Extension points.** Implement `IdempotencyStore` (four methods: `claim`, `complete`, `release`, `find`) for your own backend. Or register a `ScopeResolver` to decide which caller a key belongs to. Your beans win over the built-ins.

## Performance: what I measured, and what I didn't

For the first article, I measured the overhead of an `@Idempotent` endpoint against an identical unannotated one. The setup was 200 requests after 20 warmup iterations, against real Redis and Postgres via Testcontainers on Docker Desktop for Windows. The annotation added **~5–6.4ms at p50 and up to ~40ms at p99**. An in-memory control with no network I/O added **~0.5ms at p50**.

The aspect's own work (fingerprinting, key composition, serialization) is sub-millisecond. The rest is two store round trips plus Docker Desktop's networking layer. A colocated store without Docker should come in much lower. I haven't published numbers for that setup, so treat the figures above as an upper bound from a dev machine, not a production benchmark.

## Where I think idempotency is going

{{< note >}}
This section is my opinion, not a commitment.
{{< /note >}}

**Retries are everywhere, and most aren't yours.** Service meshes retry. Mobile SDKs retry. And now AI agents retry: an agent whose "create order" tool call times out will often just call it again, with no idea the first one committed. The more layers retry for you, the more the server has to be the one that remembers.

**That should change how write APIs are designed.** If callers that can't remember are going to retry, the key should stop being optional. `idempotency.require-key=true` turns a keyless request into a `400` instead of an unprotected execution. Document the header in your API spec too, because an agent is far more likely to send a key when its tool description says one is required.

**The library is following that.** Next on the [roadmap](https://github.com/benhendayoussef/idempotency-spring-boot-starter#roadmap) are reactive stores (R2DBC, reactive Redis) and GraalVM native image hints. After those come Kotlin coroutine support and `@Idempotent` on `@KafkaListener`.
**The header is being standardized, and the work isn't finished.** The IETF httpapi working group's [Idempotency-Key HTTP Header Field](https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/) is an **Internet-Draft**, a work in progress, not an RFC. Its latest revision, -08, was posted on October 22, 2026. It is still a working group document and hasn't been sent to the IESG, so the details can still change. Its current error guidance matches what the library does: `400` when a required key is missing, `409` while the original is in flight, and `422` for a key reused with a different payload.

## What it still doesn't do

Being loud about these is what makes a library trustworthy:

- **At-least-once by default.** Redis is always at-least-once. With JDBC, the annotation path is exactly-once only with `join-transaction=true`, only for writes in that transaction, and at the connection cost shown above. Your own code can also get it without the property, by calling `IdempotencyStore.complete()` inside a transaction it already holds open.
- **`on-conflict=wait` (the default) holds a servlet thread** for each waiting duplicate. In testing, a 20-duplicate burst against an 8-thread pool pushed an *unrelated* endpoint from under 50ms to over a second. Use `fail_fast` where bursts are likely. WebFlux doesn't have this problem.
- **No streaming, SSE or `Flux`.** The library replays one captured response.
- **No multipart bodies in the fingerprint**, and no fingerprint at all in filter mode.
- **WebFlux is `Mono` only, with `scope=global`**, until the reactive pieces land.
- **`on-store-failure=proceed` is only as fast as your store client's timeout.** The library adds none of its own.

## Traction, and a request

As of late October 2026, the starter had passed 3,400 Maven Central downloads across 92 organizations. That's a lot of services relying on formats 1.0 promised not to break, and I intend to keep that promise.

If you run it, tell me what breaks, what's confusing, and what you had to work around. Arnaldo showed how far one well-scoped issue and pull request can go. Issues and PRs are very welcome on [GitHub](https://github.com/benhendayoussef/idempotency-spring-boot-starter).
