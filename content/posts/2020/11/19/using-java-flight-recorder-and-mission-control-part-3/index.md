---
title: "Using Java Flight Recorder and Mission Control (Part 3)"
date: "2020-11-19T09:13:02+00:00"
lastmod: "2025-02-17T09:30:30+00:00"
description: "Did you know Java Flight Recorder offers a public API for controling JFR programmatically or to read events from a JFR file? Read on!"
canonical: "https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/"
authors:
  - "brice-dutheil"
image: "try.png"
categories:
  - "JDK Flight Recorder"
  - "Tools"
tags:
related_posts:
frozen: false
---

Continuing from [part 1](https://foojay.io/today/using-java-flight-recorder-and-mission-control-part-1/) and [part 2](https://foojay.io/today/using-java-flight-recorder-and-mission-control-part-2/), while from JDK 14 events can be consumed on the fly, previous JDK versions (from JDK 11) offer a public API useful enough to control Flight Recorder programmatically or to read events from a JFR file.

```java
Configuration c = Configuration.getConfiguration("profile"); [1]
Recording r = new Recording(c);
r.setName("monitor jvm");
r.enable("jdk.*"); [2]
r.setMaxAge(java.time.Duration.ofMinutes(4)); [3]
r.start(); [4]

// to be profiled

r.stop(); [5]
r.dump(Files.createFile("/var/log/jfr/app-initiated.jfr")); [6]
```

1. As shown above, choose the JFR configuration.
2. Choose which events the recording should be interested in. Another signature accepts classes, it's unlikely to be helpful for JDK events, but it may get interesting for custom events, your classes.
3. Eventually set recording constraints, like the maximum age of the records.
4. Hit record.
5. When the recording session is over, stop JFR.
6. Then store the results in the location of your choosing.

The above snippet creates a continuous profiling session with a 4 minute window.

Now the API allows reading emitted `.jfr` files. The API represents what's actually in a file, a schema of the events and the events themselves.

```java
try(RecordingFile rf = new RecordingFile(Paths.get("/var/log/jfr/app-initiated.jfr"))( { [1]
    // read the schema
    rf.readEventTypes().forEach((EventType et) -> { [2]
        System.out.println(et.getName());
        et.getFields()
          .stream()
          .map((ValueDescriptor vd) -> vd.getName())
          .forEach(System.out::println);
    });

    // actual events
    for(jdk.jfr.consumer.RecordedEvent e = rf.readEvent(); rf.hasMoreEvents(); e = rf.readEvent()) { [3]
        System.out.println(e.getEventType().getName()); [4]
    }
}
```

1. Open the JFR file, it's a `Closeable` and it reads a file, so be sure to use it in a `try-with-resources` block.
2. `readEventTypes()` gets you the schema of the events, fields name, labels, thresholds, etc.
3. Then there's this weird enumeration style API to read the events `hasMoreEvents()` and `readEvent()`.
4. Access details on the event type.

`RecordingFile` API is a bit awkward to work with, more specifically parsing each event requires looking at the event descriptor (via `getEventType()`, or getFields()), and interrogate the event as fields presence may evolve with each JDK revision. The javadoc advises defensive programming style when reading a JFR file:

```java
if (event.hasField("intValue")) {
   int intValue = event.getValue("intValue");
   System.out.println("Int value: " + intValue);
}
```

This API is now complemented by streaming live events [JEP-349](https://openjdk.java.net/jeps/349) in JDK 14 using an API `RecordingStream` that is mix of the above, that's out of scope for this article. But that's yet another reason to make the effort to upgrade our JDK.

Such API facilities are useful especially when combined with other technologies like Spring Actuators. Yet when there's available integration or when using these integrations is too late, like recording startup, the most actionable way to get recording is from the command line.

______________________________________________________________________________

Used with permission and thanks, [initially written and published by Brice Dutheil in his blog](https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/).
