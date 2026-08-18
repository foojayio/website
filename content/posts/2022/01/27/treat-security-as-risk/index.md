---
title: "Treat Security as a Risk"
date: "2022-01-27T11:26:57+00:00"
lastmod: "2022-01-27T11:26:58+00:00"
description: "Security is the poster child of a Non-Functional Requirement: most people don't care until the proverbial matter hits the rotary propeller."
canonical: "https://blog.frankel.ch/treat-security-as-risk/"
authors:
  - "nicolas-frankel"
image: "nut-ge0bfbb470.jpg"
categories:
  - "Security"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "are-java-security-updates-important"
  - "avoid-java-serialization"
frozen: false
---

Security is the poster child of a Non-Functional Requirement: most people don't care until the proverbial matter hits the rotary propeller.

Consequences can range from losing reputation to legal liability to putting the business out. In my [post on running unsecured code](https://blog.frankel.ch/running-untrusted-code/), I concluded that you should treat security as a risk - and left it at that. I think it warrants a dedicated post.

Risk management is pretty [much documented](https://en.wikipedia.org/wiki/Risk_management). You can find it in many engineering disciplines, if not every one of them. A risk management process consists of the following steps:

**Identify** ⇨ **Analyze** ⇨ **Prioritize** ⇨ **Treat** ⇨ **Monitor**

Let's see how we can apply it to security using the [Attach API](https://blog.frankel.ch/jvm-security/4/) as an example.

## Identify the risk

In short, the Attach API allows one to change the *bytecode* already loaded in a running JVM 1.6+. For that, you need:

* The PID of the running JVM
* The ability to run another JVM on the same system

When the attached JVM starts again, it discards the updated *bytecode* and loads the code from the expected location. Hence, there's no trace that the *bytecode* has been changed via the feature.

With the Attach API, a malicious actor could change the behavior of a running application. For example, the actor could direct a few cents on every transaction to their account in a banking system.

## Analyze the risk

Now that we have correctly identified the risk, we need to quantify its likelihood: how likely can a malicious actor trigger the risk?

For our example, we need to evaluate the likelihood of:

1. Accessing the system
2. Getting permissions to start a JVM
3. Getting the JVM's PID

The system access and the permission depend on unknown factors in the context of this blog post. However, getting hold of the JVM's PID is straightforward if you fulfill the other two.

With JVMs lower than version 9, we need to default to the `Runtime` class and use the `jps` command:

```kotlin
long pid = Runtime.getRuntime()
               .exec("jps")
               .inputStream
               .bufferedReader(Charset.forName("UTF-8"))
               .lines()
               .map { it.split(" ").toTypedArray() }
               .filter { it.size > 1 && it[1].endsWith("BusinessApplicationKt") }
               .map { it[0] }
```

Starting from 9 onwards, the JVM added a dedicated `ProcessHandle` class in the Process API:

```kotlin
long pid = ProcessHandle.current().pid()
```

## Prioritize

To prioritize, we need to evaluate the impact of a successful attack.

In the context of the Attach API, it depends on the target system.  

It's very context-dependent, so here's a sample of some domains:

|           Impact            |       Scale       |                                        Example                                         |
|-----------------------------|-------------------|----------------------------------------------------------------------------------------|
| Threat to human life        | Very high         | * Dangerous industrial process * Surgery device * Hospital                             |
| Economic with large radius  | High to very high | * Banking transaction involving millions * Stock exchange * Food-related goods trading |
| Gaming (except competitive) | Low               | Candy Crush                                                                            |

## Treat

Treating includes two separate things:

* Preventing the risk from happening
* *Mitigations*, if any

Mitigations allow reducing the impact of the attack. There might be several mitigations. With each, you need to describe: how much it reduces the impact and how much it costs.

With the Attach API, the feature is enabled by default. The treatment is to disable it explicitly. I couldn't come up with any mitigation. Once the malicious actor has injected the *bytecode*, the latter will run its course.

## Risk management in the real-world

Security is not a black-and-white concern. There's no such thing as a secure system vs. an insecure one. Some systems are more secure than others against specific threats. Moreover, improving security against a threat has a cost in general. Hence, one needs to find the right balance between the likelihood of the risk, its impact, the cost of the treatment, and possible mitigations.

If you implement all of the risk management steps that I described above, you'll probably fail anyway. The reason is that we work within human organizations: it involves politics, blame games, avoidance of responsibility, and similar company-related niceties.

If you want to have any chance of making risk management work, you need to track the decision taken. The record should include all of the above: risk, description, likelihood, impact, mitigations, etc., without forgetting the decision. It can be *acceptance* , *refusal* or any mitigation action.

A critical piece of info is missing: the person who took the decision. By having a person accountable, they will be *committed* and not only *involved* . At this point, there's still no guarantee. However, the organization will be better equipped to avoid the worst fiascos such as the [Equifax data breach](https://en.wikipedia.org/wiki/2017_Equifax_data_breach).

*Originally published at [A Java Geek](https://blog.frankel.ch/treat-security-as-risk/) on January 23^rd^, 2022*
