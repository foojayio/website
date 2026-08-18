---
title: "Log4Shell Shows The Need for \"Trustworthy Java\""
slug: "log4shell-shows-the-need-for-trustworthy-java"
date: "2022-01-10T10:03:24+00:00"
lastmod: "2022-01-11T11:05:09+00:00"
description: "I think the Java community handled this crisis poorly and needs to do much better next time. What do you think?"
canonical: "https://betterprojectsfaster.com/guide/java-full-stack-report-2022-01-editorial"
authors:
  - "karsten-silz"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
  - "java-where-the-wild-code-isnt"
  - "are-java-security-updates-important"
  - "5-tips-to-create-secure-docker-images-for-java-applications"
frozen: false
---

## What Just Happened?

I believe Log4Shell is Java's biggest crisis. I reported on it in the "[New \& Noteworthy](https://betterprojectsfaster.com/guide/java-full-stack-report-2022-01-new-noteworthy)" section of [my newsletter](https://bpfnl.substack.com). A quick recap: The US cybersecurity and infrastructure agency director [called Log4Shell](https://www.zdnet.com/article/log4j-flaw-this-new-threat-is-going-to-affect-cybersecurity-for-a-long-time/) "one of the most serious that I've seen in my entire career, if not the most serious". Exploiting it may be as easy as sending an HTTP request to a Java application, with a JNDI link in the HTTP header. The last wide-spread software vulnerability of this magnitude [scored 7.5](https://nvd.nist.gov/vuln/detail/CVE-2014-0160) - Log4Shell [scored a 10](https://nvd.nist.gov/vuln/detail/CVE-2021-44228#).

It seems that we know how to fix Log4Shell and are busy applying the fixes. So I think it's time for two questions in the Java community:

1. How did we handle Log4Shell?
2. How can we prevent another Log4Shell?

These are my answers. I hope they can contribute to the discussion of Log4Shell in the Java community. I'm not a security expert, and I don't have unique insights into Log4Shell. So if I wrote something wrong, or if you disagree with me, then please let me know!

## How Did We Handle Log4Shell?

In politics, there's a saying: "It's not the crisis that kills you. It's how you handle it." The good thing is we know how to handle a crisis like Log4Shell:

1. Establish a team to handle the crisis, with a single person in charge.
2. Set up centralized channels of communication - going in and going out.
3. Analyze the situation and get the message out about the fixes.
4. Take in new information, adjust your fixes, adjust your message, and get the updated message out.
5. Rinse \& repeat.

So, is that what happened in Log4Shell?

No.

From what I could see, no single team was handling this crisis for Java. Look at some of the official Java sites:

* Oracle's [new Java page](https://dev.java): Nothing. They did [publish news after Log4Shell](https://dev.java/news/), just not about Log4Shell.
* Oracle's [corporate Java page](https://www.oracle.com/java/): Again, nothing.
* The [Java Community Process page](https://www.jcp.org/en/home/index): Nope. I know it's in charge of evolving Java. But hey, it's got "Java Community" in the name - worth a try!

I could also see no centralized, orderly, regularly updated communication. Instead, thousands of articles appeared and told us how to fix Log4Shell. But most of them only solved one part of the problem - how to fix Log4Shell in the software we can build **ourselves**, where we can upgrade Log4j. And as usually happens in a crisis like this, the initial solutions didn't (always) work: Some Java versions were not immune, as we thought initially. And you couldn't (always) set JVM properties to fix Log4Shell. And the initial Log4j fix wasn't enough, either: We got Log4j 2.15, 2.16, 2.17, and 2.17.1 in quick succession.

But what about the Java software we **don't** build ourselves but just use? Are there patches? And what about vulnerable **devices**, like routers? Do they have patches? And how do we protect software or devices that don't have a patch right now or will never get one?

Imagine how much time we, the Java community, could have saved if we had a **single** site that everybody knew. A single source of truth that told us how to fix Log4Shell. A site that covered all the aspects - the software we build ourselves, the software we use, the devices we have. A site that got regularly updated. Wouldn't we love to have had such a site?

Put another way: When you fly, they always tell you how to evacuate in case of an emergency. In Java, who tells us how to evacuate?

**Summary:** I think the Java community handled this crisis poorly and needs to do much better next time.

## How Can We Prevent Another Log4Shell?

In a community as big as the Java one, we **can't** prevent all vulnerabilities. What we **can** do is reduce the probability of a future crisis and lessen its impact. I'll get back to this later.

Let's first look at what we did in the past as a guide for what we should do in the future. As Winston Churchill [supposedly said](https://liberalarts.vt.edu/magazine/2017/history-repeating.html), "Those that fail to learn from history are doomed to repeat it." And I'm not sure that Java can stomach another Log4Shell.

### Remote JNDI Code Execution

From what I know, Log4Shell could only happen because Java can load remote code over [JNDI](https://en.wikipedia.org/wiki/Java_Naming_and_Directory_Interface) and execute it, no questions asked. That's not a bug. That's how this feature was supposed to work - when it was designed in **1997** . Now, this sounds dangerous **today**. So why didn't Oracle do something about it?

Oracle **did** something about it. As Log4Shell showed, these fixes didn't work in the end, unfortunately. Here's a probably incomplete list of Oracle's attempts to fix this:

* In 2015, Oracle fixed [CVE-2015-4902](https://nvd.nist.gov/vuln/detail/CVE-2015-4902) in Java 6, 7, and 8. This [Black Hat cybersecurity presentation from 2016](https://www.blackhat.com/docs/us-16/materials/us-16-Munoz-A-Journey-From-JNDI-LDAP-Manipulation-To-RCE.pdf) showed that this vulnerability was used to attack NATO and the White House. It also discussed how to bypass Oracle's fix.
* This [article from January 2019](https://www.veracode.com/blog/research/exploiting-jndi-injections-java) details how Oracle later fixed remote JNDI code execution twice, in [Java 8u121](https://www.oracle.com/java/technologies/javase/8u121-relnotes.html) and [Java 8u191](https://nvd.nist.gov/vuln/detail/CVE-2018-3149), and why attacks were still possible afterward. I think disabling remote JNDI code execution by default in Java 8u121 is the reason why we initially thought that some Java versions were immune to Log4Shell.

So did Oracle's fixes not work? Or did the attacks simply find other security holes? I don't know.

**Summary:** Oracle fixed remote JNDI code execution multiple times but ultimately failed.

### Deserialization Attacks

Now one of the reasons why Oracle's fixes didn't work was because of exploits for Java serialization. In these so-called "deserialization attacks", our code reads something different than what was initially stored. OWASP [tells us](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html#java) how to defend against these attacks.

So serialization is used in attacks against Java. Why didn't Oracle do something about it?

Oracle **did** something about this, too:

* In 2012, Oracle formally proposed to [remove serialization from Java 9](https://openjdk.java.net/jeps/154). The reason: "The myriad shortcomings of Java's serialization facility." That proposal was rejected.
* In Java 9, Oracle added a way to [validate serialized data](https://openjdk.java.net/jeps/290).
* In Java 17, Oracle [refined this validation](https://openjdk.java.net/jeps/415).

Again, it seems that these fixes didn't prevent Log4Shell, either: Deserialization attacks are [still possible](https://twitter.com/pwntester/status/1470172110479937538) in the latest Java versions. Is it because the validation of serialized data is an optional feature? Or is it because the validation has security holes, too? I don't know.

**Summary:** Oracle tried multiple times to fix Java deserialization attacks but ultimately failed.

### Java Security Manager

Remote JNDI code execution in Java enabled Log4Shell. Another way of fixing this would be to tell Java, "Don't load code from the Internet". So why didn't Oracle do something about that?

Once more, Oracle **did** something about it. Well, Sun did, for that matter. And this time, it worked: Java has had a [Security Manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html) since the very beginning. Many Java standard libraries check with the Security Manager if they're [allowed to proceed](https://www.baeldung.com/java-security-manager), such as opening a URL.

As we all know by now, the Java Security Manager didn't prevent Log4Shell either. So how did Oracle fail this time?

This time, Oracle **didn't** fail. This time, **we failed** , the Java developers: We didn't use the Java Security Manager. We ignored it so entirely that Oracle [deprecated it in Java 17](https://openjdk.java.net/jeps/411#risks-and-assumptions): "It has not been the primary means of securing client-side Java code for many years, and it has rarely been used to secure server-side code."

Ouch.

Maybe the Java Security Manager was too cumbersome to use. But honestly, I think we Java developers just didn't want to go through the hassle of writing more secure code. I mean how many of us even know that the Java Security Manager exists?

**Summary:** Java's built-in Security Manager probably could have prevented Log4Shell, but we Java developers didn't use it.

### Trustworthy Java?

So we have this company that tried to fix security holes in its software multiple times but ultimately failed. And the developers could have written more secure code but didn't. And it all came to a head after a series of successful attacks on their software. The company was "under fire from some of its larger customers--government agencies, financial companies and others--about the security problems". So the company changed its processes and culture and launched the "Trustworthy Computing" initiative.

Wait, what?

Did I trick you? 😃 I didn't write about Oracle and Java - I wrote about **Microsoft and Windows in 2001** ! Bill Gates launched "Trustworthy Computing" in January 2002. [Wikipedia says](https://en.wikipedia.org/wiki/Trustworthy_computing#Microsoft_and_Trustworthy_Computing) that it "was generally accepted as having '...made a positive impact on the industry...'".

Now I told you about "Trustworthy Computing" for a reason: I think Java is in a similar situation as Microsoft Windows was 20 years ago. And I believe **Java needs a "Trustworthy Java" initiative**. Let me explain.

I demonstrated my belief in the first section that the Java community handled the Log4Shell crisis poorly. That's **reason number one** for "Trustworthy Java": The Java community needs to **prepare** to handle the next crisis much better.

When bad guys attack software, they're primarily after data: They steal it, change it, or encrypt it for ransom. Java takes pride in that, apart from COBOL, no other computer language powers as many business-critical enterprise systems as Java does. And those Java systems are as close to that valuable data as you can get - short of getting right into the database.

That's why, if anything, Java needs to be **more** secure than other languages, not less. Thanks to Log4Shell, all the bad guys worldwide now know that Java is a worthy target - and that you can trick into running any code. And because Log4Shell made headlines in mainstream media, our bosses and the business guys know that, too. So at least public perception says that Java is indeed **less** secure than its competition. That's **reason number two** for "Trustworthy Java": Java needs to **regain trust**.

As I explained in the previous sections, Oracle tried to fix important security holes but ultimately failed often enough, so Log4Shell could happen. And we Java developers ignored the built-in Security Manager that probably could have prevented Log4Shell. So what the Java community did for security in Java wasn't enough. That's why I believe that, just like Microsoft did back in 2002, we need to change the processes and culture in Java security.

But that's much harder for Java now than for Microsoft in 2002: Windows is closed software, and Microsoft employs its developers. Java is open-source, and its developers work for different organizations. And there are millions of developers using Java to build their applications. That's why I don't know **how** to change the processes and culture for Java security. But I believe we **have to** anyway - that's my **reason number three** for "Trustworthy Java".

When Microsoft launched "Trustworthy Computing" in 2002, its customers could complain about Windows security (or lack thereof) all day long. But realistically, they had nowhere else to go for their desktop/laptop operating system: Microsoft Windows had a 90% or higher market share back then. Macs and Linux were less competitive than they are today. Still, Microsoft pressed on with "Trustworthy Computing".

Java is in a different position today than Microsoft was. Java has plenty of competition: C# and .NET, JavaScript/TypeScript and Node.js, Python, Go, Rust - the list goes on. And with microservices in wide use, it's easier than ever to try out Java alternatives without going all-in.

To me, it's not a question **if** there will be fewer Java projects in the future because of Log4Shell. It's just a question of **how many** . Imagine you're the Java guy in a meeting that decides whether the next application/microservice should be written in Java or C#, JavaScript/TypeScript, Python, Go, or Rust. The other guys just need to say: "Unlike Java, we don't run any code from the Internet. And unlike Java, we didn't waste years trying to fix this, ultimately failing." How would **you** reply?

So that's **reason number four** for "Trustworthy Java": If Java doesn't get more secure, **developers will leave Java** behind for more secure alternatives.

**Summary:** We need "Trustworthy Java" to prepare for the next crisis, to regain trust, to change the processes and culture in Java security, and to keep developers from leaving Java for more secure alternatives.

### Trustworthy Java!

Let's assume that my wish is granted and Java gets the "Trustworthy Java" initiative. What would actually happen then?

I propose seven actions: Three to reduce the **probability** of a future crisis, three to lessen its **impact** , and one to let the Java community and the world **know** about "Trustworthy Java".

Here are my three proposals to reduce the **probability** of a future crisis.

**Make Java More Secure**   

In the previous section, I wrote that we need to change the processes and culture for Java security. But I don't know **how** to do that specifically. One way of describing how an organization works is to look at "[purpose, people, and process](https://blog.kainexus.com/improvement-disciplines/lean/purpose-people-and-process-the-foundation-of-a-lean-organization)". That view gives us a general way to reason about this.

The **purpose** of Java security is to make Java more secure. But Java also has the purpose of remaining compatible with older versions. That backward compatibility was probably one of the reasons why serialization [didn't get removed in Java 9](https://openjdk.java.net/jeps/154). It's easy to imagine how these two purposes often clash. So maybe the purpose of making Java more secure needs to trump the backward-compatibility purpose more often.

I don't know how many **people** work at Oracle and other organizations to make Java more secure. Maybe we need more. Or perhaps we need different people.

The Java security **process** releases security fixes for Java every two to three months. I think in a crisis like Log4Shell, out-of-band fixes are needed. How would we need to change the process to allow for such fixes? And how would we coordinate that across multiple Java versions and distributions? I don't know.

**Regularly Audit Java Security**   

This point covers multiple actions. With Log4Shell fresh in our mind, the first action is to audit Java **now**. Can we fix remote JNDI code execution and deserialization attacks for good? What other potential vulnerabilities lurk in the shadows of Java?

The second audit is for the **processes** that make Java secure (see my previous point). How do you know everybody follows them, especially once you change processes or establish new ones? You don't - until you **audit**. Companies, especially financial ones, get regularly audited. Auditors check if an organization adequately documents processes, risks, and risk mitigation. And auditors check if the organization does what it said it would. I think that's a blueprint for how to audit Java security.

But no matter how hard we try, there will be security vulnerabilities in Java in the future. So let's get the pros to audit Java: **Bug hunters** . Google paid them [US$6.7 million in 2020](https://www.zdnet.com/article/google-paid-6-7-million-to-bug-bounty-hunters-in-2020/) for finding bugs in Google products. If that doesn't sound like much, it's because it isn't: Google's revenue in 2020 was [US$181.69 billion](https://www.statista.com/statistics/266206/googles-annual-global-revenue/). So bug bounties amounted to 0.004% of Google's revenue. That was money well spent. In contrast, Oracle doesn't seem to pay bug bounties at all: [Go here](https://www.bugcrowd.com/bug-bounty-list/) and type "Oracle" to see. I think Java should have a bug bounty program (if it doesn't already).

**Make Java Application More Secure**   

We also need to make Java applications more secure. That's us, the developers who **didn't** use the Security Manager in Java. I'm afraid I don't know how to do this either. "Purpose, people, and process" isn't much help here because we Java developers work for so many different organizations with different purposes, people, and processes.

I think this includes publishing **security best practices** in Java. And many **tools** find Java vulnerabilities - they could help. But I guess we Java developers simply haven't cared enough until now. Will the business guys finally put enough pressure on us to care? I don't know.

Maybe the Java community needs to create a **certificate program**. Let's call it, say, the "Trustworthy Java Certificate". 😀 To get that certificate, an organization needs to have processes and best practices that make Java applications more secure. And auditors regularly check that the organization follows these processes and best practices. Such a certificate may create some peer pressure.

This also applies to Java **libraries**. Probably 90% or more of the code in our Java application comes from libraries. So how do we know that the Java libraries we use are secure? We don't. I think the "Trustworthy Java Certificate" could help there as well.

These are my three proposals to lessen the **impact** of a future crisis.

**Create Java Crisis Response Team**   

Any organization beyond a certain size has some sort of [Network Operation Center (NOC)](https://en.wikipedia.org/wiki/Network_operations_center) that monitors software, hardware, and the network. And when things go wrong somewhere, the NOC can call somebody to fix them - at any time of the day. Java needs such a NOC for security: The **Java Crisis Response Team**.

This Java Crisis Response Team monitors Java security vulnerabilities around the clock. I mean, this already happens today in security companies. It just doesn't seem to happen centrally for all in the Java community to see. The Java Crisis Response Team members work full-time on this. They have a list of experts to call in case of emergencies. These experts don't work for the Java Crisis Response Team but help in emergencies.

How much would a Java Crisis Response Team cost per year? The team works in shifts around the clock. So let's say you need 100 people for this. At a loaded cost of US$250,000 per person, that's US$25 million per year. I assume multiple companies would carry this cost, such as Oracle, IBM, and Microsoft. Is US$25 million per year too much? To me, the real question is: What's the cost of **not** having a Java Crisis Response Team?

**Create Java Security Website**   

We Java developers currently have no single website to go to in emergencies. I propose that the Java Crisis Response Team creates and maintains the **Java Security Website**. This website is the one-stop, definitive destination for all things security in Java: How Java is made secure, and how we can make Java applications more secure. It lists the latest Java vulnerabilities in Java and popular libraries and tells us how to fix them.

And when there's another crisis like Log4Shell, the Java Security Website gives us up-to-date, reliable guidance on dealing with the crisis.

**Improve Java Application Patching**   

Eventually, fixing Java vulnerability comes down to patching Java applications. But how do we know that our software is affected? We all remember the frantic rush during Log4Shell to find out if our applications used vulnerable Log4j versions. And how can we patch applications more quickly?

Here are two approaches for improving Java application patching that I happen to know. I'm sure there are many more!

* We Java developers probably know most direct dependencies of our Java applications. But we generally don't know the dependencies of our dependencies. A "[Software bill of materials](https://en.wikipedia.org/wiki/Software_bill_of_materials)" (SBOM) solves that issue. It's a list of all components of an application. That's why an SBOM can tell us quickly if our applications are vulnerable. It seems that vendors who want to sell software to the US government [will need such an SBOM](https://www.cybersecuritydive.com/news/software-bill-of-materials-sbom-biden-executive-order-supply-chain/606846/) in the future. The ripple effects of that decision could make SBOM break through into the mainstream and change how we build our Java applications.
* How quickly can we patch our Java applications? Well, who says that we need to do this **manually** ? Java applications increasingly run in virtual machines and containers. Imagine how much time and money we could have saved during Log4Shell if VMware, Docker \& Kubernetes had automatically updated Log4j! Sounds far-fetched? RedHat's OpenShift [apparently already does this](https://www.youtube.com/watch?v=OqxeyfTeAy4&t=3121s) for "blessed Java container images".

And here's how I would let the world **know** about "Trustworthy Java".

**Communicate "Trustworthy Java"**   

I would put two landing pages on the Java Security Website. Both describe how we make Java \& Java applications more secure. But one is for Java **developers** , and one is for **business people** and the world at large. The developer landing page has the necessary links so that Java developers can start making their applications more secure straight away.

I would then ask every Java champion and developer advocate in the Java community to post the link to the developer landing page at every waterhole where Java developers hang out, such as Twitter and LinkedIn, or discussion groups and chats like Slack and Discord. I would also send a Java champion/developer advocate developer to present this topic at every Java User Group. I would send Java champions/developer advocates to every relevant tech publication to discuss this initiative. And I would ask all Java distributions, Java IDEs and Java build tools to post the link on their websites, too.

Finally, select representatives should discuss this with mainstream media. Since Log4Shell was a topic there, "Trustworthy Java" would be a follow-up story.

**Summary:** I propose seven actions for the "Trustworthy Java" initiative. Making Java \& Java applications more secure and regularly auditing Java security will reduce the probability of another crisis like Log4Shell. Creating a Java Crisis Response Team \& a Java Security Website and improving Java application patching will lessen the impact of the next crisis. Communicating "Trustworthy Java" to Java developers and mainstream media will help this initiative to succeed.

*Originally published at [Better Projects Faster](https://betterprojectsfaster.com/guide/java-full-stack-report-2022-01-editorial) on January 5, 2022*
