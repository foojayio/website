---
title: "GlassFish is rolling forward. What’s New?"
slug: "glassfish-is-rolling-forward-whats-new"
date: "2024-11-26T15:11:17+00:00"
lastmod: "2024-11-26T16:59:03+00:00"
description: "The Evolution Continues. GlassFish, which used to be a popular application server, free to use and reliable, is evolving again."
canonical: "https://omnifish.ee/2024/09/12/glassfish-is-rolling-forward-whats-new/"
authors:
  - "ondro-mihalyi"
image: "https://foojay.io/wp-content/uploads/2024/11/GlassFish-commits-1024x437-1.png"
categories:
  - "Cloud"
  - "Jakarta EE"
  - "Microservices"
tags:
related_posts:
frozen: false
---

**The Evolution Continues. GlassFish, which used to be a popular application server, free to use and reliable, is evolving again. If you've been holding onto your old GlassFish instances, there's good news---things have gotten a lot more exciting recently.**

Since we created the OmniFish company and started improving GlassFish in 2022 (read about that story in [Oh, What Did You Do to GlassFish?!](https://omnifish.ee/2022/06/28/oh-what-did-you-do-to-glassfish/)), a lot has happened around GlassFish. We joined the Eclipse GlassFish project and [joined the Jakarta EE Working Group](https://blogs.eclipse.org/post/tanja-obradovic/welcome-omnifish-jakarta-ee-working-group) as well.

We announced the [start of our enterprise support services](https://omnifish.ee/2022/09/22/omnifish-announces-support-for-eclipse-glassfish/) for Eclipse GlassFish and have been helping our customers and the community of GlassFish users since then. So you may wonder, what's been happening in the past 2 years and what's up with GlassFish right now?

{{< youtube j2gACdwWyNk >}}

**What's the Buzz?**

First off, **GJULE** ---a completely rewritten logging engine---now makes logging faster and more reliable. If logging felt sluggish before, GJULE breathes new life into it. You can enable even the lowest log levels and catch every detail without bogging down your server and applications. To find out more about the new GlassFish logging, watch this video about Changes in [GlassFish 7 Logging System](https://www.youtube.com/watch?v=j2gACdwWyNk) on YouTube.

For those dabbling in microservices, GlassFish now natively supports **MicroProfile Config, REST Client,** and**JWT Authentication**. It means your applications can be more modular, easier to configure, and more secure without jumping through hoops. Of course, all these MicroProfile APIs are useful in traditional enterprise applications too, which allows you to modernize your existing apps, simplify or even remove some of the boilerplate code.  
[Get started with GlassFish
with this guide](https://omnifish.ee/developer-resources/)

<br />

![](https://omnifish.ee/wp-content/uploads/2024/09/Screenshot-from-2024-09-06-16-08-30-1024x393.png)

**A Developer's Best Friend**

Gone are the days of clunky setups. The **GlassFish Embedded** runtime now simplifies starting up your applications. GlassFish 7.0 brought a lot of fixes to GlassFish Embedded, making it a slick way to run Jakarta EE apps, with faster startup times, whether GlassFish is embedded in the app or started with a Maven plugin.

There's an Arquillian container too, so you can easily run your tests without launching the whole GlassFish server. Since then, [GlassFish Embedded](https://glassfish.org/docs/SNAPSHOT/embedded-server-guide.html) received a few improvements. A simple API to run a plain JAR in the embedded server without a separate WAR file and easier-to-use Maven plugin are among them.

And yes, GlassFish runs on the **latest Java versions**---even those still in pre-release---keeping you on the cutting edge without the usual headaches. Although GlassFish Embedded still requires a few --add-opens JVM arguments with recent Java versions to bypass the Java module system restrictions, the number of them has been reduced, with the aim to avoid any need for --add-opens arguments in the future.

{{< youtube mp0vROxSPmA >}}

**Stay Secure and Auditable**

Security-conscious? GlassFish has you covered. Vulnerabilities are being regularly fixed. GlassFish is kept up to date with recent versions of dependencies, which contain latest security fixes. Besides that, **[Admin Command Logger](https://github.com/eclipse-ee4j/glassfish/pull/24898)** is a new tool that logs all administrative commands, whether from the Admin Console or Asadmin CLI. This feature ensures transparency and security in every admin operation. You can simply enable the logger with a button in the Admin Console or set a system property using the asadmin create-system-properties command.

The logs will then record all the configuration changes, with the time and the author's username. Useful for auditing, or for reverting changes if something goes wrong, right? As a bonus, this feature also gives you a way to find out which asadmin command to use in your setup scripts. Simply launch the server, make changes in the Admin Console UI, check the logs, and, voilà, copy the command to your script.

But that's not all. If you're eyeing the future, work is well underway on GlassFish 8 to support **Jakarta EE 11** . This forward-looking approach means you won't be left behind when modernizing your apps or you'll find some new Jakarta EE 11 features useful and would like to use them as soon as possible. If you want to try out those features, just grab the [latest milestone of GlassFish 8](https://central.sonatype.com/artifact/org.glassfish.main.distributions/glassfish/versions) and get a feeling of what you can expect from the final release of GlassFish 8!

**Need to keep a low profile?**

While GlassFish is getting improvements, new featuresand Jakarta EE 11 support, [Piranha Cloud](https://omnifish.ee/piranha/) has emerged as a fast and lightweight Jakarta EE runtime based on many GlassFish components. It now provides most of the Jakarta EE Web Profile features but differs significantly from traditional application servers. It's extremely modular and allows you to build a custom runtime only with the components you need.

From just a servlet container that can programmatically turn request objects into response objects. To a full runtime JAR with all the components, that can run traditional WAR files. It's up to you to choose the proper balance between simplicity and control. Piranha Cloud can replace GlassFish Server if you just need a lightweight Jakarta EE runtime to run web apps, or it can replace Tomcat or GlassFish Embedded if you want to embed Jakarta EE technologies and have full control over them.

**Community-Driven Power-Ups**

Thanks to OmniFish, GlassFish isn't just an application server alone. Our contributions include **[Docker images](https://omnifish.ee/developers/glassfish-server/docker-image/)** optimized for faster start and lower memory use, an [enhanced **Arquillian container**](https://omnifish.ee/developers/glassfish-server/arquillian-containers/) with simpler configuration and debugging options, and even an **[Eclipse IDE plugin](https://omnifish.ee/developers/glassfish-server/ide-plugins-for-glassfish/eclipse-ide/)** that supports the latest GlassFish versions.

<br />

* [GlassFish Docker Images](https://omnifish.ee/developers/glassfish-server/docker-image/)
* [Arquillian containers for GlassFish](https://omnifish.ee/developers/glassfish-server/arquillian-containers/)
* [IDE plugins for GlassFish](https://omnifish.ee/developers/glassfish-server/ide-plugins-for-glassfish/)

![](https://omnifish.ee/wp-content/uploads/2024/09/GlassFish-commits-1024x437.png) 3 of the 4 top GlassFish contributors from OmniFish

**Commercial support gets you covered**

Eclipse GlassFish is an open source server maintained by project members in the Eclipse Foundation. But it's not supported only by a community of volunteers. Several companies offer commercial services related to GlassFish. OmniFish provides full high-quality [support and consultancy services](https://omnifish.ee/solutions/#support) to cover your back in case you have troubles. Or if you need some new features in GlassFish to make your work more efficient.

OmniFish actively contributes to the development of the GlassFish project, more than all other companies and individual contributors together. OmniFish engineers have years of experience with solving production issues, improving performance and usability of GlassFish, and adding new features in GlassFish and the ecosystem of GlassFish tooling. On top of that, we at OmniFish have a vision of turning GlassFish into a productive development platform and production runtime to write and run Java apps like a god!  
[Need help with GlassFish? Ask OmniFish for help...](https://omnifish.ee/contact-us/)

<br />

**Final Thoughts**

So, what's next for GlassFish? It's not about survival anymore---it's about thriving in a modern development landscape. OmniFish and the whole GlassFish team have more plans for the future. More features to simplify app development and configuration. More ways to run apps in different environments and architectures. And, of course, even more reliable, more secure, and faster production runtime.

**Whether you're maintaining legacy systems or embracing the latest in Jakarta EE, GlassFish has the tools and updates you need to stay ahead.**

<br />

<br />
