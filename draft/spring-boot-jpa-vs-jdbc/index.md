---
title: "Spring Boot JPA vs JDBC: How Much Can a Small App Save?"
# Provisional date; the maintainer sets the publication date.
date: "2026-01-01"
description: "A small Spring Boot app used about 90 MiB less RSS with JDBC than with JPA and Hibernate, and reached its dashboard sooner."
authors:
  - "ted-kupolov"
image: "spring-boot-jpa-vs-jdbc-og.jpg"
categories:
  - "Java"
  - "Spring"
canonical: "https://pvrlabs.xyz/java-performance/experiments/spring-boot-jpa-vs-jdbc.html"
---

A small Spring Boot app that tracks GitHub stars used about 90 MiB less RSS with `JdbcTemplate` than with Spring Data JPA and Hibernate, and reached its dashboard in about half the time.

## Question

For a small Spring Boot application, how much resident memory and startup time do I save by choosing Spring JDBC instead of the conventional Spring Data JPA and Hibernate stack?

## What changed

Both versions are Spring Boot 4.1.1 applications running on JDK 25 with compact object headers. They use H2, Thymeleaf, the same local GitHub fixture, the same dashboard, and the same monitoring request workload. The applications ran as executable fat JARs. Packaging is a separate question, covered by the earlier [comparison of fat JAR and extracted layouts](https://pvrlabs.xyz/articles/spring-boot-extracted-layout.html).

Both variants exposed metrics through Spring Boot Actuator's Prometheus endpoint, suitable for monitoring with tools like [StatLite](https://pvrlabs.xyz/statlite/).

The JPA version uses entities, Spring Data repositories, Hibernate-managed relationships, and Spring transactions. The JDBC version uses `JdbcTemplate` directly and keeps persistence logic in a small service.

The result compares the complete application variants. Changing the persistence approach also changes the application structure and data model. The JPA baseline stores additional repository fields and has more repository and service code.

## Results

Across two runs in reversed order, the JDBC version used about **90 to 91 MiB less RSS**, roughly **38% less than the JPA baseline**. It also reached its first successful dashboard response sooner: about **3.9 to 4.2 seconds** from process launch, compared with **8.1 to 8.8 seconds** for JPA.

| Run order | JPA median RSS | JDBC median RSS | RSS difference | JPA startup | JDBC startup |
| --- | ---: | ---: | ---: | ---: | ---: |
| JPA, then JDBC | 237.0 MiB | 145.8 MiB | 91.2 MiB | 8.134 s | 4.162 s |
| JDBC, then JPA | 236.9 MiB | 146.6 MiB | 90.4 MiB | 8.804 s | 3.939 s |

RSS is the median of twelve samples. Startup means time from process launch until the first successful dashboard response. It includes application initialization and that first request.

## How I measured it

Both variants ran on one macOS machine with Temurin JDK 25.0.4 and the same JVM configuration:

```text
-Xms16m
-Xmx80m
-Xss256k
-XX:+UseSerialGC
-XX:TieredStopAtLevel=1
-XX:ReservedCodeCacheSize=32m
-XX:+UseCompactObjectHeaders
```

The comparison script starts a deterministic local GitHub fixture, launches one application, waits until its dashboard responds, performs one refresh, warms it for 30 seconds, then samples process RSS every five seconds for one minute. During warmup and sampling, it requests the dashboard and the Actuator health, metrics, and Prometheus endpoints. It checks that the dashboard shows all three fixture star counts and that both apps expose JVM metrics.

The fixture served fixed star counts, and background polling was delayed so it stayed out of the measurement window.

The script then repeats the same run for the other variant. I ran it in both orders so the result would show whether running first or second changed the outcome. RSS is whole-process resident memory from `ps -o rss=`. Both processes rose by roughly 1 to 2 MiB during the sample minute, so these are warm runtime measurements.

## What this suggests

For this application, Hibernate is more machinery than the data model needs. Hibernate still solves real problems, particularly as applications and domain models grow and persistence behavior gets more complicated.

This app has a simple data model and straightforward queries. The JDBC version is still small and readable, and its measured runtime footprint is substantially lower.

Saving about 90 MiB can be absorbed on a large server. On a 256 or 512 MiB VPS, the same difference can materially affect capacity. These runs were made on a Mac without a VPS memory limit, so that capacity claim still needs a measurement on the target machine.

The startup difference was also interesting. I expected the JDBC version to use less memory. It also reached the first dashboard response in about half the time in these runs.

## The takeaway

I would read this as a reason to question the default for small applications. If a Spring Boot service has a handful of tables and simple persistence needs, JPA and Hibernate may be more machinery than it requires. Spring JDBC gives up some abstraction, and in this experiment the simpler app used about 90 MiB less RSS and reached its first successful dashboard response about four to five seconds sooner.

For small services on memory-constrained machines, that is enough of a difference that I would measure before automatically reaching for Hibernate.

## Evidence and limits

This is two reversed-order runs on one macOS host, with a 30 second warmup and twelve RSS samples per variant. It compares two complete application variants. An isolated Hibernate measurement would need the same data model, UI, and surrounding code in both builds.

The [public experiment record](https://github.com/PVRLabs/experiments/tree/main/spring-boot/jpa-vs-jdbc-stars) includes the applications, method, and measurements.

*Originally published on [PVR Labs](https://pvrlabs.xyz/java-performance/experiments/spring-boot-jpa-vs-jdbc.html).*
