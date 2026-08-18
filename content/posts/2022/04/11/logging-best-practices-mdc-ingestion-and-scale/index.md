---
title: "Logging Best Practices: MDC, Ingestion, and Scale"
date: "2022-04-11T12:16:37+00:00"
lastmod: "2022-04-11T12:54:33+00:00"
description: "Where should you add logs in a method? Should you log in this method? If so what should you include in the log? How to phrase the message?"
canonical: "https://lightrun.com/best-practices/logging-best-practices-mdc-ingestion-and-scale/"
authors:
  - "shai-almog"
image: "Logs.png"
categories:
  - "Developer Tools"
  - "Tutorials"
related_posts:
  - "debugging-tutorial-java-return-value-intellij-jump-to-line-and-more"
  - "what-is-debugging-in-140-seconds"
  - "the-basics-of-breakpoints-you-might-not-know"
frozen: false
---

I don't care about religious wars over "which logger is the best". They all have their issues. Having said that, the worst logger is probably the one built "in-house". So yes, they suck, but re-inventing the wheel is probably far worse.

Let's discuss making these loggers suck less with proper usage guidelines that range from the obvious to subtle. Hopefully, you can use this post as the basis of your company's standard for logging best practices.

Notice that while this is very much focused around Java/JVM logging, a lot of the subjects discussed here should have universal appeal.

## Why is Logging Difficult?

Normally, it's just printing data to the console/file. It seems trivial. Something we can literally write in 20 minutes with our hands tied behind our back.

For a simple logger, that might be the case. But modern loggers support machine readable formats, MDC and get ingested. We can search logs from multiple machines in the cluster in a single location with many common log processing solutions. Subtle details decided during development time can have a huge impact when you're trying to analyze a problem of scale.

The second problem is performance. Most typical, highly tuned web applications will hit the cache for roughly 95% of requests. That means there will be very little IO and very high performance. However, logging requires IO, especially with ingestion. This means the cost of logging can be the biggest performance burden on the system in some edge cases.

### Seriously, Don't Roll Out Your Own

Some developers still choose to roll out their own logging frameworks or wrappers around the existing logging libraries. It makes sense to have abstractions, but abstracting the abstraction imposes its own set of problems.

Rolling your own is redundant. There are many complexities involved. Whatever you fix or save will probably come back to bite you. As we recently saw with the Log4J security vulnerability all the way to nuanced performance pitfalls and complex custom output expected by ingestion. There's a lot going on.

## Coding Best Practices

In this section, we'll focus on tips related to writing logs in the source code. What we should and shouldn't do both as a coder and a reviewer. Every tip includes an explanation. Please keep in mind that for every "rule" there's always an exception. We do not set these in stone...

### 1. Keep the Log Message Short

Long and verbose messages cost. The cost is in reading time.

Another cost is the ingestion over-time and they don't always inform more than a concise message.

### 2. Logs MUST be 100% Unique

Every log line in the system MUST be 100% unique!

We can easily accomplish this by searching the strings in the system to verify that a log message doesn't repeat.

### 3. Never Use Log Statements in a Loop

This should be obvious, but just in case it isn't. Logging in a loop, even if it's at a level that isn't printed... Is a performance problem. Try to avoid it if possible.

Assuming you want to log something at a level that wouldn't be visible by default and choose to break this rule. Just make sure it's short and simple. It shouldn't break the other rules, especially the one below.

### 4. Log Stuff You Already "Have"

Don't do this:

```
logger.info("Reached X and value of method is {}", method());
```

Even if the method is cheap. You're effectively running the method regardless of the respective logging levels!

If you MUST do that for a case of a different log level, use an if statement to prevent the code from executing every time:

```java
if(LOGGER.getLevel() == Level.DEBUG) {
       ...
}
```

The right things to log are the things you already have as variables.

### 5. Don't Log Lists or Arrays

Even if it's a small list. The concern is that the list might grow and "overcrowd" the log.

Writing the content of the list to the log can balloon it up and slow processing noticeably.

### 6. Don't Concatenate Strings

Never use string concatenation for logging, e.g.:

```java
LOGGER.info("This is the variable: " + var);
```

Instead, use this or the equivalent provided by various popular logging frameworks:

```java
LOGGER.info("This is the variable: {}", var);
```

If the log is swallowed due to log levels, the former must still perform the string concatenation and will produce garbage. The latter will vanish.

Notice, however, that at least in the current JVM, the former is faster, so that might be misleading. This is something that's in the process of resolving in newer versions of Java.

### 7. Logger Field Must Always be Private, Static and Final

When declaring the logger field for a class, it's sometimes exposed or sometimes declared as an instance variable.

These things can lead to mistakes down the road. Especially in cases of refactoring.

### 8. Clearly Define the Logging Levels with Examples

In your guide, define exactly which logging level to assign to which type of code. Personally, I prefer limiting us to four levels, even if the logging framework supports more. I find four levels cover everything we need:

* Errors - these are exceptions, something failed
* Warnings - there was a failure, but it wasn't critical
* Info - something interesting happened, I would want to see this normally in my app
* Debug - this is probably redundant. But if we have a problem, it might be interesting

### 9. Don't Log What the Framework Logs for You

There are great things to log. E.g. the name of the current thread, the time, etc. But those are already written into the log by default almost everywhere. Don't duplicate these efforts.

### 10. Don't log Method Entry/Exit

Log only important events in the system. Entering or exiting a method isn't an important event. E.g. if I have a method that enables feature X the log should be "Feature X enabled" and not "enableFeatureX entered".

### 11. Don't Fill the Method

A complex method might include multiple points of failure, so it makes sense that we'd place logs in multiple points in the method so we can detect the failure along the way. Unfortunately, this leads to duplicate logging and verbosity.

Errors will typically map to error handling code which should be logged in generically. So all error conditions should already be covered.

This creates situations where we sometimes need to change the flow/behavior of the code, so logging will be more elegant. E.g. don't do this:

```java
if(x) {
    LOGGER.info(...);
    return value;
}

if(y) {
    LOGGER.info(...);
    return otherValue;
} else {
    LOGGER.info(...);
    return thirdOption;
}
```

Instead, change the code to lead to one return value:

```java
Object value = computeValue();
LOGGER.info(...);
return value;
```

We can easily achieve this by refactoring the code in the method to another method.

### 12. Method Return Values are Usually Important

If you have a log in the method and don't include the return value of the method, you're missing important information. Make an effort to include that at the expense of slightly less elegant looking code.

### 13. Don't Use AOP Logging

AOP logging lets us inject logs at entry points. E.g. you can inject a log in a Spring application into every method entry/exit in the system. That might sound interesting, as you can have "perfect" tracing for every call and every return value.

Unfortunately, the performance and verbosity make the debugging even harder. This is useful only when running locally and in those situations, we have better tools at our disposal.

Some developers enable this for test running during the CI process. This sounds like a good idea at first, but it makes tracking test failures even harder with all the verbosity.

## Security

There are many important things we need to keep in mind.

### 1. Don't Log Unsanitized User Data

When a user submits information, we can't log it before we do basic sanitation on it. This was at the core of the Log4J issue.

Most sophisticated linters detect this seamlessly and produce a security warning if you log "raw" user input. I would suggest using such a linter as well.

### 2. PII Reduction

Good logging frameworks support seamless removal of personally identifiable information (PII). This is very important for compliance with laws and regulations, e.g. GDPR. It's crucial to understand this functionality and tune it. Otherwise, you might be stuck with information within the logs that can compromise you.

E.g., m ost engineers in an organization have access to the logs. But very few would have access to users' credit card information or social security. If you log a card by mistake, you're effectively disabling that security.

## Aspirations

These are "vague" goals that we need to aspire to. They make sense, but they aren't concrete.

### 1. Don't Test For Logs

Logs are implementation details. They are fragile. Integration tests sometimes rely on a log. This is a recipe for disaster.

### 2. Don't Double Log

It's pretty common to log an error when we're about to throw an error. However, since most error code is generic, it's likely there's a log in the generic error handling code.

### 3. Review the Log File

When looking through the application logs, many things become clear. Small inconsistencies between logs, missing information, and duplicate information. In most corporations, R\&D writes the logging code, but SRE/DevOps, etc reads the output.

This is a problematic situation. Developers MUST dog-food their logging code in production where it looks different from the local execution.

### 4. Log Less, Inject Later

Tools such as [Lightrun](https://lightrun.com/) let you inject a log dynamically into production (among other things...). Instead of ingesting something you probably will never need, do it on-demand.

### 5. Don't Confuse Logging with Operations Log

In an enterprise system, we have two very separate elements referred to as log. There's the standard file/ingested log we're discussing here and another one where we usually discuss log events. One that usually resides in a database table and provides an audit trail for user operations.

This is a remarkably important feature that's usually totally unrelated to this logging other than the naming.

### 6. Think of Application Logs as a Form of Comment

Logs are a form of comment on the code around them and should help code readability as a secondary function.

Thinking of them in this way helps make better, cleaner and more consistent logs.

## MDC Guidelines

Mapped Diagnostic Context (MDC) is essential for modern day logging. Avoiding it is akin to disabling the stack trace on your debugger. The MDC adds a logging context map to every entry. We can see a user ID related to a specific log line or payment transaction ID related to it.

This is immensely useful for putting things in context and also for narrowing down issues. If a specific user reports a problem, the ability to inspect only the logs related to that user is invaluable!

Typically, one would set the values in the MDC when a transaction starts and reset them when it's done.

### 1. Don't Crowd the Mapped Diagnostic Context

There are many things we can track in the MDC. The temptation to add the user's name and not just the user's ID is big. But it's redundant and duplicate. Keep only the bare minimum to give you context for the request. Common valuable MDC values would include:

* Request URI
* User ID
* Requesting Host
* Transaction ID

### 2. Handle Everything in One Class

There are separate areas of the application code where values should go into the mapped diagnostic context. However, there aren't many values altogether. E.g. the User ID context should be added once a user is authenticated. Transaction ID makes only sense when a user is authenticated.

A simple solution is to create an MDC utility class which will perform the actual put operations. The value of this approach is encapsulation of the MDC logic. This helps us keep track of all the pieces in case we need to remove or update something.

### 3. Plan for Failure - e.g. Custom Thread Pool

Most JVM web applications use thread pools and MDC uses the thread context in Java. Notice that there are other options for async APIs both in Java (WebFlux) and Node, etc.

But the default that's most common uses thread context. With a thread pool, it means the context of the current request might implicitly be a part of a future request if we don't invoke clean() when we're done with the request. This is normally pretty easy, except for the case of application errors. In that case, things can be tricky.

A common trick is to use a custom thread pool that cleans up the MDC when a thread returns to the pool.

## Configuration

One of the great things in using a "ready-made" solution is the depth and breadth we can reach by just tuning configuration files.

### 1. Use JSON or other Machine Readable Structured Format

This is remarkably helpful for proper ingestion and you should tune it with your overall backend.

### 2. Consider the Benefits of Asynchronous Logging

Asynchronous logging is more performant but carries with it risks. If performance/throughput is a major consideration, this might be a valid option.

### 3. Set the LogLevel to show Warnings by Default

Including info level logs can result in redundant overhead. This could be high for most production systems, but the benefit is usually pretty big too. Staging should have info as the default.

### 4. Don't Log too Differently in Testing

It's a common practice to increase logging in testing. This is reasonable as costs change and we want to see the cause of failure faster. However, testing also checks that we have the right amount of logs in place to resolve a problem in production.

If we need a higher log level to resolve a test case failure, it might mean our logging is insufficient.

## Summary

I hope this post will help clarify your thoughts on this subject and put these ideas in order. There are so many nuanced logging practices we can improve upon in our day to day coding.

I think we can summarize the core ideas as:

* Be clear and concise
* Be consistent
* Don't screw up performance
* Don't over-log
* Be aware of privacy and security risks

I hope you found this useful and can build upon this.
