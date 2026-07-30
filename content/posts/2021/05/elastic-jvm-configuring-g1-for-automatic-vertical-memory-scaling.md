---
title: "Elastic JVM: Configuring G1 GC for Automatic Vertical Memory Scaling"
slug: "elastic-jvm-configuring-g1-for-automatic-vertical-memory-scaling"
date: "2021-05-05T13:45:22+00:00"
lastmod: "2021-09-16T14:48:32+00:00"
description: "Details on OpenJDK patch that improves elasticity & enables automatic vertical memory scaling of Java applications with G1 garbage collector!"
authors:
  - "tetiana-fydorenchyk"
image: "/images/posts/2021/05/elastic-jvm-configuring-g1-for-automatic-vertical-memory-scaling/jvm.png"
categories:
  - "Jelastic"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

<figure class="alignleft">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/12/elastic-jvm-e1545213859238.png" alt="Automatic Vertical Memory Scaling" class="wp-image-31447">
</figure>

Nowadays, JVM-based applications can benefit from elasticity and density of container technology. However, there are still some issues that may prevent from unleashing the full potential of Java and containers "marriage."

Releasing unused but committed Heap memory by the major JVM implementations based on OpenJDK, in most cases, is not performed automatically, or requires specific knowledge to be configured.

To solve this problem and make [Java RAM usage in containers](https://jelastic.com/blog/java-ram-usage-in-containers-top-5-tips-not-to-lose-your-memory/) more efficient, the Jelastic team collaborated with different experts involved in JVM development. We have periodically published articles highlighting this issue and motivating the community to help in finding a solution. As a result, attention to this topic has increased, and new improvements were introduced in different Garbage Collection implementations in order to make automatic vertical memory scaling possible.

Moreover, we initiated and sponsored the development of a patch to OpenJDK which improves elasticity and enables fully automated vertical scaling of Java applications that rely on G1 garbage collector. This work introduces new command line options for heap sizing that allows the JVM to scale its memory resources vertically. In particular, the proposed solution is to [promptly return unused committed memory](http://openjdk.java.net/jeps/8204089) to the operating system.

{{< youtube 9u_FUXFcsek >}}

There is a necessity to rethink the Garbage Collection (GC) policies that control how much and when the heap memory is given back to the operating system. Currently, G1 returns memory from the Java heap only at a Full GC that is rarely performed.

As a result, in most cases, the Java heap will not be released unless forced externally. Such behavior is particularly disadvantageous in scalable container environments when the JVM uses a fraction of assigned memory resources due to inactivity or a small load. This results in customers overpaying for unused resources, and cloud providers not being able to fully utilize their hardware.  

<figure class="alignright">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/12/jdk-9-e1545213845349.png" alt="Automatic Vertical Memory Scaling of Java" class="wp-image-31449">
</figure>

In order to overcome this issue, we have introduced additional logic into the JVM to trigger a heap reduction whenever the amount of unused memory is significant. It can be performed at any time by configurable options, not only during regular GC cycles.

During inactivity of the application, G1 will periodically trigger a concurrent cycle due to the following conditions:

* More than **G1PeriodicGCInterval** milliseconds have passed since any previous garbage collection pause and there is no concurrent cycle in progress at this point. A value of zero indicates that periodic garbage collections to promptly reclaim memory are disabled.
* The average one-minute system load value as returned by the **getloadavg()** call on the JVM host system (e.g. container) is below **G1PeriodicGCSystemLoadThreshold** . This condition is ignored if **G1PeriodicGCSystemLoadThreshold** is zero.

If either of these conditions is not met, the current periodic garbage collection is cancelled and will be reconsidered when **G1PeriodicGCInterval** time passes.

The offered solution is [already implemented in the OpenJDK 12](https://openjdk.java.net/projects/jdk/12/). This improvement will allow Java users to save a significant amount of resources and thus money, as well as help cloud providers to better utilize their infrastructure and introduce more flexible [billing model based on real usage](https://jelastic.com/blog/deceptive-cloud-efficiency-do-you-really-pay-as-you-use/) not on the VM limits.

The stated problem of Java elasticity, details on performed experiments, and specifics of the corresponding OpenJDK patch were presented by Ruslan Synytsky, Jelastic CEO at a number of Java-oriented events. Feel free to review the presentation below.

<figure class="wp-block-embed is-type-rich is-provider-slideshare wp-block-embed-slideshare">
 <div class="wp-block-embed__wrapper">
  <iframe title="State of Java Elasticity. Tuning Java Efficiency - GIDS.JAVA LIVE 2020" src="https://www.slideshare.net/slideshow/embed_code/key/qMag1uxfFDYEeC" width="427" height="356" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" style="border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;" allowfullscreen> </iframe>
  <div style="margin-bottom:5px">
   <strong> <a href="https://www.slideshare.net/slideshow/state-of-java-elasticity-tuning-java-efficiency-gidsjava-live-2020/238379521" title="State of Java Elasticity. Tuning Java Efficiency - GIDS.JAVA LIVE 2020" target="_blank">State of Java Elasticity. Tuning Java Efficiency - GIDS.JAVA LIVE 2020</a> </strong> from <strong><a href="https://www.slideshare.net/jelastic" target="_blank">Jelastic Multi-Cloud PaaS</a></strong>
  </div>
 </div>
</figure>

Have any questions about Java vertical memory scaling? Want to try it in practice? Get in touch with us or just [register for a free trial](https://jelastic.com/public-cloud-registration/).
