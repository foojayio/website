---
title: "Custom Events in the Blocky World: Using JFR in Minecraft"
date: "2023-11-28T19:12:50+00:00"
lastmod: "2023-11-28T19:12:51+00:00"
description: "I was searching for some JFR-related settings on the internet when I stumbled upon the /jfr command that exists in Minecraft..."
authors:
  - "johannes-bechberger"
image: "https://mostlynerdless.de/wp-content/uploads/2023/11/image-2.png"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "custom-jfr-events-a-short-introduction"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-closer-look-at-jfr-streaming"
  - "firefox-profiler-beyond-the-web"
frozen: false
---

I was searching for some JFR-related settings on the internet when I stumbled upon the [`/jfr` command](https://minecraft.fandom.com/wiki/Commands/jfr) that exists in [Minecraft](https://www.minecraft.net):  

{{< img src="https://mostlynerdless.de/wp-content/uploads/2023/11/image-2.png" class="aligncenter size-full is-resized" style="width:614px;height:auto" >}}

This, of course, intrigued me, especially as Minecraft apparently adds some custom JFR events:
[![](https://mostlynerdless.de/wp-content/uploads/2023/11/image-3.png)](https://minecraft.fandom.com/wiki/Commands/jfr)

So I had to check it out. I downloaded and started the [Java server](https://www.minecraft.net/en-us/download/server), got a demo account, and connected to my local instance. *This works with a demo account when you launch the demo world, enable the cheat mode in the settings, kick yourself via "/kick @p," and then select your own server. I found this via [this bug report](https://bugs.mojang.com/browse/MC-138478).*

You then must ensure that you have OP privileges and add them, if not via the Minecraft server shell. Then, you can type `/jfr start` in the chat (launch it by typing <kbd>T</kbd>) to start the recording and `/jfr stop` to stop it.  

{{< img src="https://mostlynerdless.de/wp-content/uploads/2023/11/Screenshot-from-2023-11-17-15-47-07.png" class="aligncenter size-full is-resized" style="width:614px;height:auto" >}}

*You see that it's my first time "playing" Minecraft, and I'm great at getting attacked. It's probably also my last time.*

Minecraft stores the JFR file in the `debug` folder in the working directory of your server, both as a JFR file and as a JSON file. You can view the JFR file in a JFR viewer of your choice, like JMC or my [IntelliJ JFR plugin](https://plugins.jetbrains.com/plugin/20937-java-jfr-profiler) ([web view of the file](https://share.firefox.dev/3G5dfKr), [JFR file itself](https://mostlynerdless.de/files/blog/server-2023-11-17-155349.jfr)), and explore the custom JFR events:  
[![](https://mostlynerdless.de/wp-content/uploads/2023/11/Screenshot-from-2023-11-20-12-16-59.png)](https://share.firefox.dev/3G5dfKr)

This lets you get insights into the chunk generation and specific traffic patterns of the Minecraft server.

But what does the event specification look like? We could disassemble the Minecraft JAR and potentially get into legal trouble, or we could just use the [jfr](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jfr.html) utility with its `metadata` command and get an approximation of the event definition from the JFR metadata:

```bash
jfr metadata debug/server-2023-11-17-155349.jfr | \
    grep minecraft --after-context=40
```

The ChunkGeneration event looks as follows:

```java
@Name("minecraft.ChunkGeneration")
@Label("Chunk Generation")
@Category({"Minecraft", "World Generation"})
class ChunkGeneration extends jdk.jfr.Event {
  @Label("Start Time")
  @Timestamp("TICKS")
  long startTime;

  @Label("Duration")
  @Timespan("TICKS")
  long duration;

  @Label("Event Thread")
  @Description("Thread in which event was committed in")
  Thread eventThread;

  @Label("Stack Trace")
  @Description("Stack Trace starting from the method the event was committed in")
  StackTrace stackTrace;

  @Label("First Block X World Position")
  int worldPosX;

  @Label("First Block Z World Position")
  int worldPosZ;

  @Label("Chunk X Position")
  int chunkPosX;

  @Label("Chunk Z Position")
  int chunkPosZ;

  @Label("Status")
  String status;

  @Label("Level")
  String level;
}
```

You can find all defined events [here](https://gist.github.com/parttimenerd/a3b0c74eea0c1da89fec533ebd468479). The actual implementation of these events is only slightly larger because some events accumulate data over a period of time.

I'm, of course, not the first OpenJDK developer who stumbled upon these custom events. Erik Gahlin even found them shortly after their addition in 2021 and promptly created an issue to recommend improvements (see [MC-236873](https://bugs.mojang.com/browse/MC-236873)):
[![](https://mostlynerdless.de/wp-content/uploads/2023/11/image-5.png)](https://bugs.mojang.com/browse/MC-236873)

## Conclusion

In [my previous blog post](https://mostlynerdless.de/blog/2023/11/20/custom-jfr-events-a-short-introduction/), I showed you how to create custom JFR events for a small sample application.

Seeing custom events in Minecraft shows you that custom events are used in the wild by applications used by millions of users, helping developers improve the performance of their applications.

**This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. *It first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2023/10/11/jdwp-onthrow-and-a-mysterious-error/).***
