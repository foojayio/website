---
title: "Useful Hotspot JVM Options for Today: Heap Sizing"
date: "2020-09-14T16:37:30+00:00"
lastmod: "2020-09-15T14:41:11+00:00"
description: "The HotSpot JVM has a lot of options available. I have summed up here some of the most useful JVM options in the context of heap sizing."
canonical: "https://jpbempel.github.io/2012/11/15/the-useful-jvm-options.html"
authors:
  - "jpbempel"
image: "Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
frozen: false
---

The HotSpot JVM has a lot of options available. Maybe too many. Sometimes we are looking for a specific option or the "magic" one that can give a serious boost in an application. Unfortunately, I think that magic option may not exist! However, some can help you for optimizing your application or for tuning some of its parts.

To find the complete list of options you will find in the [globals.hpp](https://github.com/openjdk/jdk/blob/master/src/hotspot/share/runtime/globals.hpp) file from OpenJDK sources. However, the [VM Options Explorer](https://chriswhocodes.com/vm-options-explorer.html), also [integrated neatly here into foojay](https://foojay.io/command-line-arguments/openjdk-11/?tab=alloptions), can help you to navigate through the list.

I have summed up here, in my humble opinion, some of the most useful JVM options in the context of heap sizing.

### Young Generation

Of course, you know the `-Xms` \& `-Xms` options, which can also be abbreviated to `-ms` `-mx`, though did you know that parts of the Java heap and non-heap can also be sized:

* `-XX:NewSize=n` Defines the initial size of the Young generation, including Eden, \& Survivors.
* `-XX:MaxNewSize=n` Defines the maximum size of the Young generation, including Eden \& Survivors.
* `-XX:SurvivorRatio=n` Ratio between Eden Size and one of the 2 survivors

`n` without unit is expressed in bytes, you can also use `k`, `K`, `m`, `M`, `g` \& `G` to expresse respectively kilobytes, megabytes \& gigabytes.

If `NewSize` \< `MaxNewSize`, young generation size can be adjusted during application life. However, resizing does require a FullGC. To avoid this, set the same value for both options.

### **Metaspace**

* `-XX:MetaspaceSize=n` Defines the initial size of the Metaspace
* `-XX:MaxMetaspaceSize=n` Defines the maximum size of the Metaspace generation

`n` without unit is expressed in bytes, you can also use `k`, `K`, `m`, `M`, `g` \& `G` to express respectively kilobytes, megabytes \& gigabytes.   

If `MetaspaceSize` \< `MaxMetaspaceSize`, Metaspace generation size can be adjusted during application life. However, resizing does require a FullGC. To avoid this, set the same value for both options.

### Code Cache

* `-XX:InitialCodeCacheSize=n` Defines the initial size of the Code Cache.
* `-XX:ReservedCodeCacheSize=n` Defines the maximum size of the Code Cache.

`n` without unit is expressed in bytes, you can also use `k`, `K`, `m`, `M`, `g` \& `G` to express respectively kilobytes, megabytes \& gigabytes.

Code Cache stores the JITed code. This is an off-heap space, so GC does not reclaim it. If you reach the limit of the `ReservedCodeCacheSize`, the JIT compiler stops to compile more methods, since it cannot store them. So, if you have a lot of classes/methods need to be compiled, be aware of these options.

When you reach the limit, a warning is emitted on the standard output:

```
Java HotSpot(TM) Server VM warning: CodeCache is full. Compiler has been disabled"
```

with -XX:+PrintCompilation you will also get:

```
7383 COMPILE SKIPPED: code cache is full
```

**Note:** Used with permission and thanks --- [originally written and published by Jean-Philippe Bempel](https://jpbempel.github.io/2012/11/15/the-useful-jvm-options.html).
