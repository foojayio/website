---
title: "Log4Shell / Leak4J"
slug: "log4shell-leak4j"
date: "2021-12-15T12:49:15+00:00"
lastmod: "2021-12-15T12:52:11+00:00"
description: "Over the last couple of days (and nights) I’ve been studying the new (extremely dangerous) vulnerability in log4j2 called Log4Shell."
canonical: "https://www.royvanrijn.com/blog/2021/12/log4j2-rce-problem/"
authors:
  - "roy-van-rijn"
image: "https://www.royvanrijn.com/images/leak4j1.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Over the last couple of days (and nights) I've been studying the new (extremely dangerous) vulnerability in log4j2 called [Log4Shell](https://en.wikipedia.org/wiki/Log4Shell)).

All versions of log4j-core from 2.0-beta9 to 2.14.1 are affected by this, and it's a **big** one.

This vulnerability allows the attacker to remotely execute code on your system, with the ability to get complete control of the underlying servers.

Log4J has, for a long time, been the most used logging framework in the Java landscape. It's extremely widely used and this attack has the most broad trigger you can imagine: It needs to log something.

The payload can be delivered in a LOT of ways, as long as it gets in a log statement. Either through user-controlled fields, HTTP requests, URLs, **ANYTHING**.

## The attack

After writing some code (a malicious embedded LDAP server) I was able to reproduce the RCE ("Remote Code Execution") attack on even the most basic project.

Here is an example: a simple REST endpoint in a Spring Boot starter project with a single line of logging
![Leak4J](https://www.royvanrijn.com/images/leak4j1.png)

As you can see it downloads and executes a classfile I'm serving from the malicious LDAP server (running seperately) printing a message.

I'll not be sharing the malicious code, it's just too simple to set up and abuse. There are better and easier ways to check if your software is vulnerable. For example using this tool by [Trend Micro](https://log4j-tester.trendmicro.com/).

## Possible risks: 🚨

Risks of this vulnerability are:

* Loss of *ALL* data
* Ransomware
* Backdoors
* Botnet
* Loss of AWS/Kubernetes keys/secrets
* And the list goes on, and on, and on...

## The fix

**Option 1**: If you haven't already: upgrade log4j-core to version \>= 2.16.0

Use version **2.16.0** instead of 2.15.0, this fixes the problem a bit more rigorously.

```
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.16.0</version>
    </dependency>
```

Do this for all transitive dependencies as well (!).

**Option 2**: Another option is to launch the JRE with.

```
    -Dlog4j2.formatMsgNoLookups=true
```

But be **AWARE** this flag was put into log4j2 from 2.10.0 onwards. If you have an older version, this does not work.

**Option 3**: Remove JndiLookup.class

It is also possible to remove `org.apache.logging.log4j.core.lookup.JndiLookup` from your log4j JAR files. I don't recommend doing this, if you want to go this route it's probably easier to just upgrade to \>= *2.16.0*.

**Not an option**:

* A newer Java version
* Property: *com.sun.jndi.ldap.object.trustURLCodebase*

I've seen suggestions online that 'newer' Java versions are not affected, but this is just **not true** . Even with the latest Java versions and with `trustURLCodebase` set to `false`, they would still be able to steal data (for example environment variables) and deserialize objects already known to the classloader. This makes other deserialization RCEs trivial to launch.

It might be *slightly* harder/safer on a newer Java version, but it's definitely **NOT** a fix.

To show this I've taken the latest version of Java 8 (1.8.311) and I'm using the log4j2 deserialization to craft something that opens the Calculator on my MacBook using other classes known to the vulnerable target:
![Leak4J with latest Java](https://www.royvanrijn.com/images/leak4j2.png)

Again: The payload is still being deserialized, on the latest Java version.

## You've been compromised

Great, you've upgraded and fixed the issue. However: Don't stop there, that's just step one.

This leak was known and exploited for a long time, probably weeks before made publicly. And if even I can exploit it in a couple of minutes with my own malicious code: everybody can.

So here is what you need to do:

Step 1: **Upgrade** and fix the leak

Step 2: **Assume** everything was **stolen**: Rotate all your keys

Step 3: Go in and **analyse** the **log** files

Step 4: If you have set up infra-structure as code: **rebuild** your **environment**

Step 4: **Redeploy** all your **applications**

We *HAVE* to take this one seriously. I don't want to hear or read a couple of months from now that some company forgot to patch their software. Not another

## Join our meetup

* "Understanding Log4Shell: vulnerability, attacks and mitigations (livestream)"
* Wednesday, 15 December 2021, 20:00 CET

<https://www.meetup.com/OpenValue/events/282682468/>

## Further reading:

Here are some links for more information:

* <https://www.lunasec.io/docs/blog/log4j-zero-day/>
* <https://snyk.io/blog/find-fix-log4shell-quickly-snyk/>
* <https://en.wikipedia.org/wiki/Log4Shell>
