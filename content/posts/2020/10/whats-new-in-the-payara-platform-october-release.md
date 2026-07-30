---
title: "What's New in the Payara Platform October Release?"
slug: "whats-new-in-the-payara-platform-october-release"
date: "2020-10-15T14:19:00+00:00"
lastmod: "2020-10-15T17:58:38+00:00"
description: "We're happy to announce that Payara Platform Community 5.2020.5 and Payara Platform Enterprise 5.22.0 Editions are out!"
authors:
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2020/10/Release-Thumbail-October-09-e1602683979477.jpg"
categories:
  - "Microservices"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

We're happy to announce that Payara Platform Community 5.2020.5 ([direct download here](https://www.payara.fish/downloads/payara-platform-community-edition/)) and Payara Platform Enterprise 5.22.0 ([request here](https://www.payara.fish/page/payara-enterprise-downloads/)) Editions are out!

With this major release of Payara Platform Enterprise, we're introducing **Payara InSight**: a new and improved version of the Community Edition's Monitoring Console. Enterprise Edition also features some tooling enhancements including IntelliJ support and NetBeans Community Tooling.

Meanwhile, the Payara Community Edition introduces support for the Jakarta EE 9 Platform as a *Tech Preview* feature and a major Notifier API Update.

Read more below to find out the details!

### Payara InSight is Coming to Payara Server Enterprise {#h3-0-payara-insight-is-coming-to-payara-server-enterprise}

Accessible from within the Payara Enterprise Admin Console, the new[Payara InSight](https://docs.payara.fish/enterprise/docs/5.22.0/documentation/user-guides/monitoring/monitoring-console.html)(real-time monitoring console) features a new and improved GUI. Easily add, remove, and refresh metrics to monitor, change your layout, and customize alerts and watches of the health status of your applications.

To maintain the security of your Payara InSight settings, you can set three levels of user roles including guest, in which they view the latest server page configuration and do not have the ability to make changes that affect the remote configuration; users, which can be selected for each individual page and has access to manually update local page with server page configuration during the session; or administrators, which can be selected for each individual page and with full access for making changes to the local pages that are automatically shared with other users of Payara InSight.

Payara InSight Demo is coming very soon, so watch this space! Meanwhile, you can give it a go yourself - [request access to Payara Enterprise Edition here.](https://www.payara.fish/page/payara-enterprise-downloads/)
![Screen Shot 2020-10-13 at 10.36.07 AM](https://blog.payara.fish/hs-fs/hubfs/Screen%20Shot%202020-10-13%20at%2010.36.07%20AM.png?width=808&name=Screen%20Shot%202020-10-13%20at%2010.36.07%20AM.png)

### Tooling Enhancements in Payara Platform Enterprise {#h3-1-tooling-enhancements-in-payara-platform-enterprise}

#### **IntelliJ IDEA Support**

As we announced earlier this month, Payara Platform, both Community and Enterprise Editions, provide support for the IntelliJ plugin. Payara IntelliJ IDEA tools provide integration of the Payara Platform Enterprise \& Community Editions into IntelliJ IDEA Ultimate.

Check out [this blog post tutorial](https://blog.payara.fish/payara-platform-support-for-intellij-plugin), which describes how to create, debug and deploy a Maven web application to the Payara Platform.

#### **Eclipse Support**

Payara Eclipse IDE tools v1.1.0 is also out and available on the Eclipse Marketplace and within the Payara Enterprise repository with a few fixes related to Payara Server startup in the Eclipse IDE.

#### **NetBeans Community Tooling**

Major refactor of Apache NetBeans Payara Server modules. Apache NetBeans IDE provides out-of-the-box support for the Payara Platform.

In the upcoming release of Apache NetBeans (planned for release at the end of [November 2020)](https://cwiki.apache.org/confluence/display/NETBEANS/Release+Schedule) we have added the support to download the latest version of Payara Platform from Apache NetBeans without upgrading the IDE.

We've also fixed the Payara Micro integration[issue](https://issues.apache.org/jira/browse/NETBEANS-4559) and removed the derby integration from the Payara Server tools.

### Other Minor Enhancements in Payara Platform Enterprise {#h3-2-other-minor-enhancements-in-payara-platform-enterprise}

#### **Remote EJB Tracing**

Initially [introduced to Payara Platform Community in August](https://blog.payara.fish/remote-ejb-tracing), the Remote EJB Tracing is now coming to Payara Enterprise as a stable and secure feature ready for production use! Filling a gap in the [Payara Request Tracing](https://docs.payara.fish/community/docs/5.201/documentation/payara-server/request-tracing-service/request-tracing-service.html)service, this release adds automatic propagation of active OpenTracing Span Contexts from Remote EJB clients (even standalone Java SE clients) when they invoke their server-side counterparts. Previously, the traces within Payara Server wouldn't have a reference to the invoking client call, and there was also a particular path where a request could complete without actually being traced (now fixed). Since this tracing has support for OpenTracing, the added benefit of allowing you to propagate additional information across the process-boundaries as Baggage Items is also present.

#### **OpenID Connect Session Timeout**

With the OpenID Connect session timeout functionality, the expiry time of the Access Token and/or Identity Token is verified with each request of the user. When the token is expired, a new token is requested using the refresh token if that is available. Otherwise, the user session with Payara is invalidated and a log-out is performed. This way, the expiry time is respected, and a session cannot be used after the expiry time of the token.

This update also includes the possibility to perform a programmatic logout. The new feature is available within Payara Server and Payara Micro Enterprise.

### Notifier API Update in Payara Platform Community {#h3-3-notifier-api-update-in-payara-platform-community}

The notification service received a major overhaul in Payara Server Community this month! It makes implementing new notifiers in a modular fashion easier. The overhaul also involved rewriting the existing notifiers as extensions, porting them over to a new community repository, and then implementing new Discord and Microsoft Teams notifiers.

Since this is quite a large change, we're rolling it out in Payara Server Community Edition first to ensure it is fully stable before moving it into Payara Enterprise.

More info on this update is coming soon in a separate article so watch this space!

### Eclipse Transformer Configuration Option with Jakarta EE 9 Milestone Release {#h3-4-eclipse-transformer-configuration-option-with-jakarta-ee-9-milestone-release}

Payara Services is one of the leading contributor companies of the Jakarta EE and [Eclipse GlassFish](https://projects.eclipse.org/projects/ee4j.glassfish/who) projects. We now provide the support of the Jakarta EE 9 Platform as a *Tech Preview* feature in Payara Server Community 5.2020.5 to give our users an opportunity to try out the new Jakarta EE 9 namespace and start experimenting and migrating their applications. Do not migrate your production apps at this time - this is basically a proof of concept, and a tool for getting us from Jakarta EE 8 to Jakarta EE 9.

### Ongoing MicroProfile Enhancements in Payara Platform Community {#h3-5-ongoing-microprofile-enhancements-in-payara-platform-community}

We've added the\`add-to-microprofile-health\` option to the \`set-healthcheck-service-configuration\` command, which will add the configured Payara Health checks such as CPU Usage, Heap Usage etc. to the MicroProfile health endpoint as readiness checks. This is especially useful for cloud environments, as container orchestration tools such as Kubernetes and Docker Swarm can now stop distributing requests to servers which are highly contested.

In future releases, we intend to make further improvements to our [MicroProfile](https://docs.payara.fish/community/docs/5.2020.5/documentation/microprofile/README.html) support to develop the experience of using these features, and more tightly integrate them with other Payara Server features.

### Release Notes {#h3-6-release-notes}

The Enterprise Release ([request here](https://www.payara.fish/page/payara-enterprise-downloads/)) includes 3 new features, 13 bug fixes, 8 improvements and 1 component upgrade; while the Community Release ([direct download here](https://www.payara.fish/downloads/payara-platform-community-edition/)) includes 4 new features, 26 bug fixes, 18 improvements, 1 security fix and 1 component upgrade.

See more detailed overview of the fixes and improvements in the Release Notes:

* [Payara Platform Community Edition 5.2020.5](https://docs.payara.fish/community/docs/5.2020.5/release-notes/release-notes-2020-5.html)
* [Payara Platform Enterprise Edition 5.22.0](https://docs.payara.fish/enterprise/docs/5.22.0/release-notes/release-notes-22-0.html)

### Download The Latest Payara Platform Release {#h3-7-download-the-latest-payara-platform-release}

Don't forget to update your Payara Platform to the new version! You can [Download the Payara Community Edition here](https://www.payara.fish/downloads/payara-platform-community-edition/), or request the Payara Enterprise Edition [via this form.](https://www.payara.fish/page/payara-enterprise-downloads/)

As usual: if you have any questions, feedback or suggestions, please post them in the comments below or email us at [\[email protected\]](/cdn-cgi/l/email-protection#fa999597978f94938e83ba8a9b839b889bd49c938992)

Original blog can be found here, reused with thanks and permission: <https://blog.payara.fish/whats-new-in-payara-platform-october-2020>

<br />
