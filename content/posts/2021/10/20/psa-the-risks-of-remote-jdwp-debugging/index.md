---
title: "PSA: The Risks of Remote JDWP Debugging: It's Like a Wide Open Door"
date: "2021-10-20T08:13:29+00:00"
lastmod: "2021-10-20T08:15:28+00:00"
description: "Java Debug Wire Protocol (a.k.a. JDWP) was designed for testing internally. Opening it to production is a HUGE security and stability risk..."
authors:
  - "shai-almog"
image: "jdwp-is-a-security-risk.jpg"
categories:
  - "DevOps"
  - "Security"
related_posts:
  - "the-debugger-checklist-part-i"
  - "the-debugger-checklist-part-ii"
  - "understanding-stack-traces-and-debugging-them-further"
  - "detecting-investigating-and-verifying-fixes-for-security-incidents-and-zero-day-issues-using-lightrun"
frozen: true
---

**It isn't so much a security risk as it is a wide open door with everything labeled for anyone to take freely and matches to burn it all down...**

When I teach or lecture and mention security, I always love the analogy of security as an onion. This is a common cliché yet it's still a powerful analogy for people who are new to the concepts. The equivalent analogy I would pick for remote debugging is like leaving your front door open with all your valuables piled in easy to spot and well labeled boxes. Calling JDWP insecure doesn't make any sense. It wasn't designed to be secure so it isn't...

Java Debug Wire Protocol (a.k.a. JDWP) was designed for testing internally. Over the past few years I've run into quite a few individuals that made the mistake of leaving it on even in production!

That's INSANE!

Even internally it's a HUGE risk with [60% of breaches originating from inside the organization](https://www.idwatchdog.com/insider-threats-and-data-breaches/). Well...

![drew-scream.jpg](https://cdn.hashnode.com/res/hashnode/image/upload/v1634113444886/ChPfwGtMr.jpeg)

This isn't just a security risk, that could essentially give every hacker the "keys" to your server and full access to your server code… It's also a serious stability hazard that could easily crash your production servers.

Even for staging this is a problematic case. Staging servers are sometimes exposed to the internet but might serve as a stepping stone to the internal servers (e.g. same host so they slide under the firewall rule). Hackers can easily "hop" into the "real" servers once they are able to hack (or enter an open door really) the remote debug protocol.

Before I proceed I'd like to clarify my bias: I work for [Lightrun](https://lightrun.com/). We implement a tool that in many respects is a replacement for a debugger designed to work securely in staging and production. Still, you should read this even if you have no intention of using Lightrun since the facts I list below are still applicable to anyone using remote debugging.

Another thing I'd like to point out is that I'm not a security expert. I did read a few excellent articles on the subject from security experts such as [Assaf Morag](https://blog.aquasec.com/jdwp-misconfiguration-container-images) . I think they are great if you're a security researcher or a devops. This article focuses on the perspective of developers.

Notice you don't need to be a security expert to understand these concepts and inherent flaws. In that sense this post is much simpler than the one from Assaf. This post is also heavily biased towards Java since that's my main field of expertise, but the situation isn't much better in other languages/platforms.

## Insecure By Design

Most remote debugging protocols such as JDWP are a product of a different era. An era that gave us telnet and HTTP. Most aren't even encrypted by default. Just leaving JDWP enabled [warrants a CVE](https://nvd.nist.gov/vuln/detail/CVE-2018-5486) .

JDWP effectively allows remote code execution. Lets you access all the bytecode of the server which is effectively almost the same as giving access to your full server source code. It lets attackers do almost anything since it wasn't designed with security in mind.

I won't even get into the complexities of man in the middle attacks etc. That's just insane.

I think the best mitigation is to tunnel the connection over SSH. It doesn't solve all the problems but it's at least not a huge hole like we normally have. Instead of enabling remote debugging as you normally would in the past, just enable it to your servers localhost as such:

```
java -agentlib:jdwp=transport=dt_socket,server=y,address=9000 ApplicationName
```

To connect to this remotely you will need SSH access to the machine and execute the following command:

```
ssh remoteUser@remoteHost -L 9000:127.0.0.1:9000 -N
```

If you need credentials for the command also add them there. This will open a tunnel between your local machine's port 9000 and the remote one. You will be able to debug on localhost but it would work as a standard remote debugger. The only difference is that it wouldn't be as bad in terms of security.

## Crash and Burn

Unfortunately such workarounds don't impact everything else that is broken in the debug protocols. Some operations in the debugger require more than one step in terms of the protocol. As a result you could send a request to the debuggee, lose your connection and the debuggee could be stuck in a problematic state.

This is an inherent limitation of the JDWP protocol and can't be worked around in a standard debugger.

The problem is that even unintentional actions can demolish a server. A simple conditional breakpoint that invokes a method as part of the condition can demolish server performance and crash it.

## Information Sifting

![office-space.jpg](https://cdn.hashnode.com/res/hashnode/image/upload/v1634113633044/LJZGiwg5X.jpeg)

In the cult classic office space the heroes sift pennies from transactions. Big organizations block a lot of this access and rightly so.

Imagine if all your colleagues had debug access to your server… All you need is one conditional breakpoint on the user login code and you have the password of a person you can hack. If you're smart you'll try that password on a different site without anyone knowing so it can't even be traced to you.

Seriously don't do that!

This is a very real risk of placing JDWP on servers and something you need to keep in mind.

## How we Solved these Issues at Lightrun

Pretty much all of those problems don't exist in Lightrun. Before I go into that, Lightrun doesn't just let you start debugging… You need to authenticate. There's an access system with corporate compliance, user roles that provide specific permissions etc.

### Insecure By Design

Lightrun doesn't use JDWP and instead uses a custom agent. This means that there is no need to open an additional port to your service.

The agent connects to a management server so information is always pulled, never pushed. Furthermore, connections use certificate pinning to block man in the middle attacks.

### Crash and Burn

Operations on Lightrun are executed using a fail-safe mechanism and a Sandbox.

Operations sent to the agent are atomic so a connection failure won't impact the agent.

Furthermore, conditions and expressions are sandboxed and checked. If too much CPU is used or a write operation is detected the operation is discarded or rate limited. You will get a printout that rate limiting occurred and your server will remain stable and performant.

### Information Sifting

Lightrun has PII reduction and blocklists. This means that Lightrun can detect problematic patterns in logs such as credit card numbers. It can then block those numbers from entering the logs.

This is fully configurable per install so you can configure this for social security numbers etc.

Blocklists let you define classes, files etc. that are blocked to debugging. Just exclude the authentication and authorization packages once you finish debugging the initial setup and a malicious developer won't be able to place a snapshot there.

Finally everything in Lightrun is audited. That means that even if you forgot to limit access this would all be logged. Any snapshot (breakpoint) or log added by the user is added to the audit log. So a malicious developer would leave a digital trail we can follow.

## TL;DR

Don't use remote debugging unless you REALLY have to and then make sure no one can access your system… Even under those circumstances be vigilant and tunnel your connections via SSH.

Be careful with conditional breakpoints and other similarly elaborate debugger features. They are a recipe for disaster in such situations.

Be aware that you're effectively giving every user who has access to the server the keys to the kingdom. Not just the source and the DB, access to all the encrypted data too such as passwords!

I hope you found this useful and keep safe out there with your deployments. Follow me on [twitter.com/debugagent](https://twitter.com/debugagent) for updates and more.
