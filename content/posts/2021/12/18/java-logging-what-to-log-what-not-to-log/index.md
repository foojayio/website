---
title: "Java Logging: What To Log & What Not To Log?"
date: "2021-12-18T11:29:33+00:00"
lastmod: "2021-12-18T11:29:34+00:00"
description: "A pragmatic guide to Java logging—what should we log, what shouldn’t we log, and how to implement Java logging properly."
canonical: "https://snyk.io/blog/java-logging-what-should-you-log-and-what-not/"
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
related_posts:
  - "java-where-the-wild-code-isnt"
  - "the-costs-of-hidden-logging"
  - "the-debugger-checklist-part-i"
frozen: false
---

Logs are a handy tool to spot mistakes and debug code. For engineers and, specifically, in a DevOps environment, the logs are a very valuable tool.

In addition to the functional aspect of logging, logs are also critical from a Java security perspective. When a security breach occurs, your log files are the first place to look for clues about what happened. But with a large number of different frameworks available for logging in Java applications, how do you pick the right one? More importantly, how to do Java logging right?

The quality of the logs is critical. Logging both too much or too little information can be catastrophic from a Java security perspective. In fact, Insufficient Logging \& Monitoring (A10:2017) has made the OWASP Top 10 list.

In this article, I am going to guide you through a pragmatic approach to Java logging---what should we log, what shouldn't we log, and how to implement Java logging properly.

### What Should We Log?

Back in the military, whenever we had to report something, we had to use the 5W-H method---Who, What, Where, When, Why, and How. This method is an excellent rule for logging statements as well.

* **Who was doing this?** Identify the user or system that made this request. If possible, with a userID and an IP address.
* **What is going on?** What kind of logging statement was this. Many developers already recognize the different log-levels to annotate the importance of a message (ERROR, WARN, INFO, DEBUG, TRACE). It is wise to distinguish between functional logging, security logging, and maybe even audit logging if that is applicable.
* **Where did it happen?** Identify the location. You usually define a logger per class, so that is already a good start. However, we also need to know the specific method. Also, we want to know the particular node, if you are working with a microservice architecture, for instance.
* **When did it occur?**Time is essential, so make sure you log a timestamp. Be aware of timezone differences and, more importantly, change timezones twice a year. My advice is to always output in UTC, so it is clear to everyone.
* **Why did it happen?**The "why" is generally what you need to find out. You can't always capture this in a single logline. In some cases, it is obvious. If so, make it part of the message. On many occasions, we can only explain the "why" once we see the relation between logs. Therefore make sure that your log messages are absolutely clear.
* **How did it happen?**Like the "why," the "how" typically can't be caught in one statement. Therefore, to correlate between message, especially in a service-based architecture, is extremely important. To achieve this, you should create a unique correlation ID for each incoming request in every service with an outbound interface. Once you have this ID, you should log it in every statement. If you need to transport this ID to another service, you can use the specific HTTP header X-Correlation-ID in order to correlate and be stateless.

### Logging Security Events

Java is mostly used in backend processes and will directly interact with the data. Therefore, we must keep track of security-related events. We have to prioritize these security events accordingly and consider events that need high privileges (admin events) even more critical.

In general, you can think of:

* login success and failures
* logouts
* password changes and resets
* creation and removal of users
* authorization failures
* access level changes

In addition, you should also log indicators that can point to specific attacks.

* input validation errors: recognize script tags that lead to XSS attacks, or parameters like UNION that indicate a SQL-injection
* HTTP 4xx and 5xx responses
* getting insane amounts of requests by a specific IP
* check IP ranges, for instance, if an internal service gets hit by an external IP-address

It is a great tactic to set thresholds for certain security log events. You can actively send alerts when these thresholds are crossed. This way, you can - interfere accordingly and prevent more extensive damage.

### What Should We Not Log?

Logging too much information is just as harmful as logging too little. You do not want your logs to be flooded by information that is not useful in production. Therefore, aim to always set the log level on a production system to a suitable level, like WARN or ERROR.

As part of development, engineers will log sensitive entities that might contain personal data, in order to debug applications. Although we should aim to prevent this, internal debug messages should have the proper log level---for example, DEBUG or TRACE---and should not be visible in the production environment.

As a general rule, an application should not reveal any application-specific or user-specific data---this data can be used by an attacker. Also, by exposing personal information, you are most probably not compliant with privacy regulations. Even if logs are only accessible to insiders, it is a single point of failure. So, we simply don't want this information to fall into the wrong hands.

When developing, think about what you need to log. Do we need to log an internal ID, the unencrypted password, or someone's credit card details? Be careful not to output a complete data object in Java as the output depends on the `toString()` method of that particular object.

By logging personal information for debugging, we circumvent many of our own security policies---it would be sensible not to log these types of information altogether. If we need to log specific data, make sure to set the proper log-level, such as DEBUG. You should also set the log-level of the production system to a higher level, like WARN.

**Bottom line---personal and sensitive information must not appear in your production logging.**

### Security Logging in Java

First and foremost, use a logging framework in Java. This seems like an open door, but in a ton of applications, people still use the old System.out.println() to write directly to the console. This should be prevented at all costs. A good linter (Spotbugs, PMD, SonarLint) could help you with this.

There are many different logging frameworks in Java. This article will focus on Logback and Log4j2, as these are currently the most used frameworks. However, regardless of the framework, it is wise to separate security logging from general application logging. In both Logback and Log4j2, you can easily do this by adding markers to the logger. With a specific marker, like SECURITY or maybe even AUDIT, you can easily filter these out in your logs. You could also add an extra appender to your logger and filter on the specific marker to store or display these logs separately.

#### Logback

To implement Logback, you only need the `logback-classic` dependency, as shown below. This will pull in both `logback-core` and the `slf4j-api` packages.

```xml

```java
<dependency>
   <groupId>ch.qos.logback</groupId>
   <artifactId>logback-classic</artifactId>
   <version>1.2.3</version>
</dependency>
```

```

* Markers. Markers can be set with any given String and added to your logger regardless of the log level, as you see below.

```java

```java
private static final Logger logger = LoggerFactory.getLogger(LogbackExample.class);
private static final Marker security = MarkerFactory.getMarker("SECURITY");

public boolean  login(String userName, String encryptedPassword) {
   logger.warn(security, "login attempt by user: {}", userName);
  ...
}
```

```

* Configuration. In the following example of Logback configuration, I have created two appenders to my `logback.xml` file. The first appender will show all the log lines in the console, including the marker I might have added. The second appender only filters out the security-specific logs based on the maker SECURITY and put these in a separate security log file per day.

```xml

```xml
<configuration>
   <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
       <encoder>
           <pattern>%d{HH:mm:ss.SSS} [%thread] %-8marker %-5level %logger{36} - %msg%n</pattern>
       </encoder>
   </appender>
   <timestamp key="byDay" datePattern="yyyyMMdd"/>
   <appender name="SECURITY-FILE" class="ch.qos.logback.core.FileAppender">
       <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
           <evaluator class="ch.qos.logback.classic.boolex.OnMarkerEvaluator">
               <marker>SECURITY</marker>
           </evaluator>
           <OnMatch>ACCEPT</OnMatch>
           <OnMismatch>DENY</OnMismatch>
       </filter>
       <file> security-${byDay}.log</file>
       <append>true</append>
       <encoder>
           <pattern>%d{HH:mm:ss.SSS} [%thread] %-8marker %-5level %logger{36} - %msg%n</pattern>
       </encoder>
   </appender>
   <root level="warn">
       <appender-ref ref="STDOUT" />
       <appender-ref ref="SECURITY-FILE" />
   </root>
</configuration>
```

```

#### Log4j2

To use, you have to include the `log4j-api` and the `log4j-core` package, as seen below.

```xml

```xml
<dependency>
   <groupId>org.apache.logging.log4j</groupId>
   <artifactId>log4j-api</artifactId>
   <version>2.15.0</version>
</dependency>

<dependency>
   <groupId>org.apache.logging.log4j</groupId>
   <artifactId>log4j-core</artifactId>
   <version>2.15.0</version>
</dependency>
```

```

* Markers. Creating and adding the markers is similar to the Logback example; only this time, we use the specific Log4j manager to create the logger and the marker.

```java

```java
private static final Logger logger = LogManager.getLogger(Log4j2Example.class);
private static final Marker security = MarkerManager.getMarker("SECURITY");

public boolean login(String userName, String encryptedPassword) {
   logger.warn(security, "login attempt by user: {}", userName);
  ...
}
```

```

* Configuration. Similar to the Logback configuration, I created a configuration for Log4j2 with two appenders where the security log will get stored in a separate file based on the markers. This configuration needs to be in the `log4j2.xml` on the classpath.

```xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
   <Appenders>
       <Console name="Console" target="SYSTEM_OUT">
           <PatternLayout pattern="%d{HH:mm:ss.SSS} [%thread] %-8marker %-5level %logger{36} - %msg%n"/>
       </Console>
       <File name="SECURITY-FILE" fileName="securityFile.log">
           <Filters>
               <MarkerFilter marker="SECURITY" onMatch="ACCEPT" onMismatch="DENY"/>
           </Filters>
           <PatternLayout pattern="%d{HH:mm:ss.SSS} [%thread] %-8marker %-5level %logger{36} - %msg%n"/>
       </File>
   </Appenders>
   <Loggers>
       <Root level="WARN">
           <AppenderRef ref="Console"/>
           <AppenderRef ref="SECURITY-FILE"/>
       </Root>
   </Loggers>
</Configuration>
```

```

#### Logging for Distributed Systems

It is wise not to log to a single file per service for distributed systems. Instead, prefer using a central location. Using a central location to store your logs will make it easier to correlate log lines and find a potential problem.

**Pro tip:** When implementing centralized logging, it is often useful to configure your logs lines as JSON. This way, indexing, and searching will be easier and quicker.

In addition to centralized logging, you should also consider active monitoring and alerting on your log lines. Many different products can create real-time graphs based on your logs and actively alert you when things are above a predetermined threshold.

### Conclusion

Logging is not something that you just do. Having a closer look at what you log, when you log it, and utilizing your logs, is essential for doing quick research or intervention. Although prevention is better than a cure, it is always possible that someone breaches your system, and you want to prevent further damage.

If this bitter moment ever comes, make sure your logs can help you!
