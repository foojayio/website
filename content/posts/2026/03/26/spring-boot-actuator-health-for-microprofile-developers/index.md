---
title: "Spring Boot Actuator Health for MicroProfile Developers"
slug: "spring-boot-actuator-health-for-microprofile-developers"
date: "2026-03-26T09:30:00+00:00"
description: "This post will help you map your MicroProfile Health knowledge to Spring’s world."
canonical: "https://payara.fish/blog/spring-boot-actuator-health-for-microprofile-developers/"
authors:
  - "luqman-saeed"
image: "Blog-Image_SpringBoot-Actuator-Health-for-Microprofile-Developers-scaled-1.jpg"
categories:
  - "Jakarta EE"
  - "Java"
  - "Microservices"
  - "Payara"
  - "Spring"
tags:
related_posts:
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "did-ai-just-break-software-security-for-ever"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "spring-ai-agents-no-second-runtime"
enlighterjs: true
frozen: false
---

If you worked with MicroProfile Health, you already understand the value of exposing application health information through standardized endpoints. You know about liveness, readiness and startup probes. You implemented the HealthCheck interface and annotated your procedures with @Liveness, @Readiness and @Startup. Spring Boot Actuator follows a similar philosophy but takes a different architectural approach. This post will help you map your MicroProfile Health knowledge to Spring's world.

## The Conceptual BridgeThe Conceptual Bridge

At its core, Spring Boot Actuator serves the same purpose as MicroProfile Health: it provides a mechanism for validating the availability and status of your application in containerized environments. Both specifications recognize that modern cloud platforms like Kubernetes need machine-readable health information to make decisions about pod lifecycle management.

The fundamental concepts translate directly:

* Liveness probes determine whether an application should be restarted
* Readiness probes determine whether an application can accept traffic
* Startup probes handle slow-starting applications before liveness takes over
* Health status is aggregated from multiple contributors using a logical AND policy by default
* HTTP status codes signal overall health (200 for UP, 503 for DOWN)

## Endpoint Mapping

In MicroProfile Health 4.0, you work with four endpoints: /health/ready, /health/live, /health/started, and the combined /health. Spring Boot Actuator uses a similar structure with some naming differences.

The Actuator health endpoint lives at /actuator/health by default, with separate probe endpoints at /actuator/health/liveness and /actuator/health/readiness. These endpoints automatically become available when Spring detects a Kubernetes environment, exposing the application's availability state through dedicated health indicators. Platforms like Payara Qube use these Actuator endpoints to monitor the health of Spring Boot applications, automatically detecting application status and managing container lifecycle based on the probe responses.

For Kubernetes deployments, you configure your probes similarly to what you'd do with MicroProfile:

```
livenessProbe:
  httpGet:
    path: "/actuator/health/liveness"
    port: <actuator-port>
readinessProbe:
  httpGet:
    path: "/actuator/health/readiness"
    port: <actuator-port>
```

## Writing Health ChecksWriting Health Checks

In MicroProfile Health, you implement the HealthCheck functional interface and return a HealthCheckResponse. The annotation determines whether it's a liveness, readiness, or startup check. Spring Boot Actuator follows a similar pattern but uses the HealthIndicator interface instead.

#### MicroProfile Approach

```
@Readiness
@ApplicationScoped
public class DatabaseCheck implements HealthCheck {
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("database")
            .withData("connection", "active")
            .up()
            .build();
    }
}
```

#### Spring Boot Actuator Approach

```
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up()
            .withDetail("connection", "active")
            .build();
    }
}
```

Notice the similarities: both use a builder pattern, both support additional data/details, and both return a status. The naming is derived from the class name in Spring (DatabaseHealthIndicator becomes "database"), while MicroProfile requires explicit naming in the response.

### Auto-Configured Health Indicators

One area where Spring Boot Actuator differs from MicroProfile Health is the extensive set of auto-configured health indicators. While MicroProfile specifies that implementations should support reasonable out-of-the-box procedures, Spring Boot ships with indicators for databases, message brokers, caches, and more.

Available auto-configured indicators include checks for:

* DataSource connections (database health)
* Disk space monitoring
* Elasticsearch, MongoDB, Neo4j, and other databases
* Redis and Hazelcast caches
* RabbitMQ and JMS message brokers
* LDAP servers
* SSL certificate validity

These indicators activate based on classpath detection. If you have a Redis client in your dependencies, the Redis health indicator appears. You can disable specific indicators through configuration properties like management.health.redis.enabled=false.

### Response Format and Status Codes

Both specifications use JSON responses and HTTP status codes to communicate health status. MicroProfile Health 4.0 mandates specific status values (UP, DOWN) and HTTP codes (200, 503, 500). Spring Boot Actuator follows a similar convention but offers more flexibility in customization.

A typical Actuator health response looks like:

```
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": { "database": "PostgreSQL" }
    },
    "diskSpace": { "status": "UP" }
  }
}
```

Compare this to MicroProfile's format using a "checks" array instead of a "components" object. The structure differs slightly, but the information conveyed is equivalent.

### Security Considerations

Both specifications recognize that health endpoints may expose sensitive information. MicroProfile Health states that security is an opt-in feature that must not be enforced by default. Spring Boot Actuator takes a more secure-by-default approach: only the /health endpoint is exposed over HTTP by default, and when Spring Security is present, actuator endpoints are secured automatically.

Spring provides fine-grained control over what health information is displayed:

* **never:** Details are never shown
* **when-authorized:** Details shown only to authenticated users with proper roles
* **always:** Details shown to all users

### Actuator's Extended Capabilities

While MicroProfile Health focuses solely on application health, Spring Boot Actuator provides a broader observability story. The health endpoint is just one of many available endpoints including metrics, environment properties, loggers, thread dumps, heap dumps, and more. This makes Actuator a more comprehensive solution for application monitoring, though it means learning a larger API surface.

Key additional endpoints include:

* /actuator/metrics for application metrics
* /actuator/env for environment properties
* /actuator/loggers for runtime log level management
* /actuator/info for build and git information
* /actuator/prometheus for Prometheus-compatible metrics export

### Practical Migration Tips

If you're moving a MicroProfile application to Spring Boot or working on a team that uses both frameworks, here are key points to remember:

1. **Replace annotations with components**: Instead of @Liveness or @Readiness, create @Component beans implementing HealthIndicator and use health groups for organization.
2. **Update endpoint paths** : Change from /health/*to /actuator/health/* in your Kubernetes configurations.
3. **Review auto-configured indicators**: Check which indicators Spring activates based on your dependencies. You may need to disable some or configure others.
4. **Configure security early**: Spring's secure-by-default approach means you may need to configure access rules for health endpoints to work with your infrastructure.
5. **Consider reactive support**: If you're building reactive applications with WebFlux, use ReactiveHealthIndicator for non-blocking health checks.

### Conclusions

Spring Boot Actuator and MicroProfile Health share the same goal: making applications observable and manageable in cloud-native environments. Your understanding of MicroProfile Health concepts transfers well to Spring. The main differences lie in the API surface (interface vs annotations), configuration approach (properties vs annotations), and scope (Actuator covers more than just health).

Whether you're working with Jakarta EE and MicroProfile or Spring Boot, the underlying patterns for health checking remain consistent. Both ecosystems have learned from Kubernetes and cloud platform requirements, resulting in specifications that, while different in implementation details, serve the same operational needs.

For MicroProfile developers exploring Spring Boot, Actuator health will feel familiar. The learning curve is less about understanding new concepts and more about mapping the patterns you already know to a different syntax and configuration model.
