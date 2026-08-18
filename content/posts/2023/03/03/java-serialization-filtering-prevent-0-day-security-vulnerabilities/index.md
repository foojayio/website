---
title: "Java Serialization Filtering: Prevent 0-day Security Vulnerabilities"
slug: "java-serialization-filtering-prevent-0-day-security-vulnerabilities"
date: "2023-03-03T16:04:53+00:00"
lastmod: "2023-03-03T16:04:55+00:00"
description: "Simple configuration that requires no code change can save you from hacks such as Log4Shell, & from vulnerabilities we don't know about yet!"
authors:
  - "shai-almog"
image: "Serialization-Filter.png"
categories:
  - "Security"
tags:
related_posts:
  - "foojay-podcast-14"
  - "what-are-you-missing-by-debugging-in-vs-code"
  - "remote-debugging-dangers-and-pitfalls"
enlighterjs: true
frozen: false
---

I've been a Java developer long enough to remember the excitement when Sun introduced the concept of serialization in the JVM.

In the world of C, we could just write a struct into a file but this was always problematic. It wasn't portable and had many issues. But for Java we could just write the class and it "worked". This was pure magic!

Java was still mostly in use in the client side and when we thought about security, we had different vulnerabilities in mind. The sandbox occupied most of our security discussions.

Fast forward a couple of decades and today when most developers discuss serialization the discussion isn't so positive. Serialization as [Brian Vermeer](https://mastodon.social/@brianverm) puts it is: "the gift that keeps giving".

{{< youtube xLXFhRLkxLc >}}

<br />

In fact, just after I created [this video](https://www.youtube.com/watch?v=xLXFhRLkxLc), a new [deserialization vulnerability in SnakeYaml](https://snyk.io/blog/unsafe-deserialization-snakeyaml-java-cve-2022-1471/) was exposed. Serialization is one of the biggest security problems in many programming languages, it isn't just a JVM problem. Hackers can use tools designed to deliver a serialization exploit chain.

You can then generate a gadget used to deliver the exploit without too much knowledge of the system. That is scary stuff...

I'm not a security expert, I care more about the solution. How do we make sure that the next zero-day doesn't affect us?

How do we harden our server code against serialization attacks?

Do we need Serialization?
-------------------------

We rarely need serialization. Ideally, if you can remove serialization entirely from your code and can avoid 3rd party code that uses serialization, you can just block it completely. This will mean that even if a zero-day comes up, the serialization portion will fail. You might have a bug, but it won't be an exploitable vulnerability.

Sometimes we need a bit of serialization. In that case, we can include only the well-known classes needed and block everything else out.

JEP 290: Serialization Filtering
--------------------------------

The solution came in Java 9 in the form of serialization filtering as part of JEP 290. There are critical patch updates for older JDKs such as JDK 8u121. So if you must stay in an older version it's still possible to use this feature.

It is possible to use this feature with no code changes, although we might want to change the code for additional functionality. The fundamental problem is testing that important systems don't break. We might run into difficulty when verifying serialization usage, e.g. in distributed caching layers.

A cache might serialize objects to synchronize them between nodes over the network. We might miss that dependency when running tests locally, but fail in production. In those cases, you can follow the strategies listed below to solve the problems.

Whitelist vs. Blacklist
-----------------------

There are two approaches we can take when filtering specific serializable objects:

* Whitelist - block everything and allow specific classes or packages in.
* Blacklist - block specific problematic classes and packages.

A blacklist lets us block well-known vulnerabilities and that might be enough. But we have no guarantee that we blocked everything. A whitelist is usually the more secure option, yet it might break your code if you missed a class that's required in an edge case.

We can set the filter on the JDK itself by editing the [java.security](http://java.security) properties file. That might make sense if you package a JDK with your application. Personally, I prefer using the command line argument to configure that e.g.:

```
java “-Djdk.serialFilter=!*” -jar MyJar.jar
```


This command will block all serialization. Notice I need to use the quotes to prevent bash from expanding the star sign. The exclamation point means we wish to block and the star means we block everything.

The following code is a blacklist. We're blocking a specific package. We can also narrow it down to a specific class. But as I said before, this isn't ideal:

```
java “-Djdk.serialFilter=!mypackage.*” -jar MyJar.jar
```


Besides the inherent problems with the blacklist, a major problem is knowing what to block. There are obvious targets like classes that have been vulnerable in the past e.g.:

* `java.rmi.server.UnicastRemoteObject`
* `java.util.logging.Handler`
* `java.util.zip.Inflater`
* `org.apache.commons.collections.functors.InvokerTransformer`
* `org.apache.commons.collections4.functors.InvokerTransformer`

Unfortunately, this list is not exhaustive and I couldn't find any list that I can use as a source for a proper blacklist. We can look through the Common Vulnerabilities and Exposures (CVE) database for exploits but that's painstaking work.

Finally, we have a whitelist where we allow the classes under the package `mypackage`. We can serialize them as usual. The JVM seamlessly blocks everything else. This is pretty close to the ideal situation. We can add additional classes and packages as necessary by adding them and separating them with a semicolon:

```
java “-Djdk.serialFilter=mypackage.*;!*” -jar MyJar.jar
```


What about Complexity?
----------------------

How do you know which classes are serialized in the code? How do you get an alert if your code blocked a serialization attempt? This might be something you would want to track since it might be the system breaking or it might be an attempted hack. Both are valid reasons for an alert. You can't do that declaratively but you can write code that can use sophisticated logic to determine whether serialization should succeed.

This is a sample from the Oracle documentation of a simple serialization filter. Notice it can reject the serialization or leave it undecided. This is part of a filter chain where each stage in the validation process can reject the serialization or pass it on to the next stage. We can bind the filter globally as we do here, or do it on a per-stream basis. The API is remarkably flexible and provides a lot of information about the process:

```
ObjectInputFilter.Config.setSerialFilter(info -> info.depth() > 10 ? Status.REJECTED : Status.UNDECIDED);
```


TL;DR
-----

You should always use serialization filtering when running a JVM deployment. This should always be the case.

Serialization filtering was backported to older JVM versions so there is absolutely no excuse.

Serialization filtering requires no code changes and we can enable it via global configuration or command line.

At the very least, you can use it to blacklist known vulnerabilities. Ideally, we should block all serialization and whitelist specific classes or packages as needed.
