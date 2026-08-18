---
title: "Custom JFR Events: A Short Introduction"
date: "2023-11-26T21:08:03+00:00"
lastmod: "2023-11-26T21:09:21+00:00"
description: "Find out how JFR allows you to implement your events to record custom information directly in your profiling file."
authors:
  - "johannes-bechberger"
image: "custom_jfr_event-2000x1203-1.png"
categories:
  - "Java Core"
  - "Performance"
related_posts:
  - "a-closer-look-at-jfr-streaming"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm"
  - "custom-events-in-the-blocky-world-using-jfr-in-minecraft"
frozen: false
---

**JDK Flight Recorder (JFR) is one of the two prominent open-source profilers for the OpenJDK (besides [async-profiler](https://github.com/async-profiler/async-profiler)).**

It offers many features (see [Profiling Talks](https://mostlynerdless.de/profiling-talks/)) and the ability to observe lots of information by recording over one hundred different events.

If you want to know more about the existing events, visit my [JFR Event Collection](https://sapmachine.io/jfrevents) website ([related blog post](https://mostlynerdless.de/blog/2022/12/06/jfr-event-collection/)):  
[![](https://mostlynerdless.de/wp-content/uploads/2023/11/image.png)](https://sapmachine.io/jfrevents)

Besides these built-in events, JFR allows you to implement your events to record custom information directly in your profiling file.

Let's start with a small example to motivate this. Consider for a moment that we want to run the next big thing after Software-as-a-Service: Math-as-a-Service, a service that provides customers with the freshest [Fibonacci](https://en.wikipedia.org/wiki/Fibonacci_sequence) numbers and more.
![](https://mostlynerdless.de/wp-content/uploads/2023/11/Long_Profilers_Basel_One_2023-2000x1125.png)

We develop this service using Javalin:

```java
public static void main(String[] args) throws Exception {
    // create a server with 4 threads in the thread pool                                                                               
    Javalin.create(conf -> {                                                  
            conf.jetty.server(() ->                                           
                new Server(new QueuedThreadPool(4))                           
            );                                                                
            })                                                                
            .get("/fib/{fib}", ctx -> {                                       
                handleRequest(ctx, newSessionId());                           
            })                                                                
            .start(7070);                                                     
    System.in.read();                                                         
}                                                                             

static void handleRequest(Context ctx, int sessionId) {                       
    int n = Integer.parseInt(ctx.pathParam("fib"));
    // log the current session and n                           
    System.out.printf("Handle session %d n = %d\n", sessionId, n);            
    // compute and return the n-th fibonacci number                                                        
    ctx.result("fibonacci: " + fib(n));                                                                                                 
}                                                                             

public static int fib(int n) {                                                
    if (n <= 1) {                                                             
        return n;                                                             
    }                                                                         
    return fib(n - 1) + fib(n - 2);                                           
}
```

This is a pretty standard tiny web endpoint, minus all the user and session handling. It lets the customer query the n-th Fibonacci number by querying /fib/{n}.

Our built-in logging prints n and the session ID on standard out, but what if we want to store it directly in our JFR profile while continuously profiling our application?

This is where custom JFR events come in handy:

```java
public class SessionEvent extends jdk.jfr.Event {
    int sessionId;
    int n;

    public SessionEvent(int sessionId, int n) {
        this.sessionId = sessionId;
        this.n = n;
    }
}
```

The custom event class extends the j[dk.jfr.Event](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/Event.html) class and simply define a few fields for the custom data. These fields can be annotated with `@Label("Human readable label")` and `@Description("Longer description")` to document them.

We can now use this event class to record the relevant data in the `handleRequest` method:

```java
static void handleRequest(Context ctx, int sessionId) {            
    int n = Integer.parseInt(ctx.pathParam("fib"));                
    System.out.printf("Handle session %d n = %d\n", sessionId, n);
    // create event 
    var event = new SessionEvent(sessionId, n);
    // add start and stacktrace                   
    event.begin();                                                 
    ctx.result("fibonacci: " + fib(n));
    // add end and store                          
    event.commit();                                                
}
```

This small addition records the timing and duration of each request, as well as `n` and the session ID in the JFR profile. The sample code, including a request generator, can be found on [GitHub](https://github.com/parttimenerd/custom-jfr-event-sample).

After we ran the server, we can view the recorded events in a JFR viewer, like JDK Mission Control or [my JFR viewer](https://plugins.jetbrains.com/plugin/20937-java-jfr-profiler) ([online view](https://profiler.firefox.com/public/pzwy2v3q9vnefyc6btn9q4fs2yy8et82t5651vr/marker-chart/?globalTrackOrder=0&thread=0wa&v=9)):  
[![](https://mostlynerdless.de/wp-content/uploads/2023/11/custom_jfr_event-2000x1203.png)](https://share.firefox.dev/3sIyFtE)

This was my short introduction to custom JFR events; if you want to learn more, I highly recommend Gunnar Morlings [Monitoring REST APIs with Custom JDK Flight Recorder Events](https://www.morling.dev/blog/rest-api-monitoring-with-custom-jdk-flight-recorder-events/) article.

Come back next week for a real-world example of custom JFR events.

***This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone.* *It first appeared on my personal blog* [*mostlynerdless.*de](https://mostlynerdless.de/blog/2023/11/20/custom-jfr-events-a-short-introduction/).**
