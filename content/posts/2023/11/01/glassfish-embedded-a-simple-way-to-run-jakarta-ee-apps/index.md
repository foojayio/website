---
title: "GlassFish Embedded - a simple way to run Jakarta EE apps"
slug: "glassfish-embedded-a-simple-way-to-run-jakarta-ee-apps"
date: "2023-11-01T15:55:14+00:00"
lastmod: "2023-11-01T15:55:16+00:00"
description: "Jan Blavins shares how builds both server component and a client component in his project using the GlassFish Embedded container with little effort."
authors:
  - "ondro-mihalyi"
image: "GlassFish-embedded-e1698251860666.png"
categories:
  - "Jakarta EE"
  - "Use Cases"
tags:
related_posts:
  - "omnifish-announces-enterprise-support-for-eclipse-glassfish"
  - "how-to-upgrade-to-jakarta-ee-10-and-glassfish-7-its-much-easier-than-you-think"
  - "the-future-of-ejb"
  - "ejb-support-in-piranha-via-cdi"
frozen: false
---

**A long-time GlassFish user and active member of the GlassFish community, Jan Blavins, shares how he uses GlassFish Embedded to take advantage of some of its unique features compared to traditional application servers.**

I've been asked by the Eclipse GlassFish project to say a few words about how I use GlassFish Embedded. The GlassFish project is pretty active these days, they have helped me with some complex issues that I have raised. This way I want to thank them, especially to the OmniFish team, who is one of the main contributors to the GlassFish project and helped me with my issues with GlassFish.
> Running GlassFish Embedded is pretty straightforward - you start GlassFish within a client application and deploy the server application to the running embedded GlassFish. The embedded server is created and destroyed by the tool on each tool session.

I started using GlassFish while GlassFish was in Oracle's hands as a reference implementation of Java EE. Some time ago, there were suggestions that GlassFish was not being actively maintained. Since Oracle's donation of GlassFish to the Eclipse Foundation, with support from the Foundation's GlassFish team, and the OmniFish team (that also provides commercial support), the GlassFish project is very active and the community around it is certainly present and responsive. That's one more reason for me to continue using GlassFish in the future.  

<figure class="aligncenter size-full is-resized">
 <img decoding="async" src="https://omnifish.ee/wp-content/uploads/2023/09/GlassFish-embedded.png" alt="" class="wp-image-2518" style="width:264px;height:135px" width="264" height="135">
</figure>

## Overview of the APILoader Project

My project is APILoader. APILoader is, I believe (quite possibly wrongly) the seed for the next generation of software performance testing tools. I won't say a lot about APILoader since it hasn't been released yet and there is IP to protect. But I can say a bit about how it uses GlassFish Embedded.
> For an individual, I intend that APILoader be deployed using GlassFish Embedded. This obviates the server administration, as the server is created and destroyed by the tool on each tool session.

Some software performance engineers work in teams and need to share artefacts. For them, a server-based tool is appropriate. Others work individually. For them, a server-based tool is an overkill, implying, as it does, server administration. So APILoader has a server component and a client component but they are deployed differently depending on the needs of their users. In all deployments, the database remains external.

For teams, I intend that APILoader be deployed as a server installation with multiple clients. The engineers share the server and the database for artefacts, and use the clients for isolation. APILoader supports accounts and projects. Accounts are hermetically sealed sets of projects. Projects are separated sets of artefacts, but with the option to copy selected artefact types between them. So engineers can work completely separately by using different accounts. Or they can work in the same account, sharing account level resources, but with separate sets of project-level artefacts. Or they can work on the same project and share account and project-level resources. A team might use different accounts for testing different products where artefact sharing is unlikely. That team might use a different project for performance testing of each release of one product, initially populating each project selectively from its predecessor.

For an individual, I intend that APILoader be deployed using GlassFish Embedded. This obviates the server administration, as the embedded server is created and destroyed by the tool on each tool session. The individual can still use accounts and projects to separate the artefact sets for different pieces of work.

There is potentially a hybrid approach where each engineer runs an embedded GlassFish instance but they choose to share a single (networked) database. The issue is that the 'database' in APILoader is distributed with some data held in a relational database and some held in files associated with the server. So, in this scenario, those artefacts that are held in the database would be shared but those held by the server are not (since each engineer has their own (embedded) server). This scenario doesn't appear useful as it stands because the relational database is used to access the file-based artefacts held by the server and only a subset of file-based artefacts would be reachable by each engineer. It could be made an installation option that the file-based artefacts be held in one repository, independent of the servers. Then all artefacts would be shared. This would appear that the installation is shared by the team but with a greater degree of isolation for each engineer since the server isn't shared.

## Simple Setup with GlassFish Embedded

Running GlassFish Embedded is pretty straightforward - you start GlassFish within a client application and deploy the server application to the running embedded GlassFish. There is very little to do in the server application to cater for being runnable both as a remote server or embedded. (Or maybe there was more than I remember but it is a once-only thing.)

However, there is one major consideration. GlassFish Embedded runs in the same JVM as the client. In remote server mode it doesn't - it runs in a separate JVM process, often on a remote machine. This has significant implications for static resources. In embedded use, a static resource is shared between the client application and the embedded server. This allows some tempting shortcuts in coding that won't work in non-embedded deployment.
> With EJBs, the serialisation is done automatically. With http-based communication, it would have to be done explicitly via SOAP, XML, or Gson/JSON.

## Benefits of Remote EJBs as a communication method

The APILoader client is a (very) fat GUI. It started life as a web client but I found myself spending inordinate amounts of time on the minutiae of HTML presentation. So now its a GUI. As such, communication with the server presents new options. I have chosen to use remote EJBs. These work just as well against a remote or embedded GlassFish server. Once you overcome the issue of making the remote class definitions available to the client application, server EJBs are pretty straightforward to use. And, with a GUI client, they are simpler to use than http-based messaging. The APILoader server and client communicate complex objects. With EJBs, the serialisation is done automatically. With http-based communication, it would have to be done explicitly via SOAP, XML, or Gson/JSON.

Note that the APILoader client is not an enterprise client. So it isn't deployed to the server to run, and the EJBs aren't injected. Instead, the client gets access to the server's remotely accessible methods by doing context lookup() calls.

## Simplified Distribution and Support

The other benefit of GlassFish Embedded is simplified distribution and support for APILoader to those clients that select it. Packaging and distributing the APILoader only has to cater for one brand of server, and one release of that server. On the other hand, support for the server option is easier in bigger teams because the server environment is usually better understood by infrastructure teams.

Since Oracle's donation of GlassFish to the Eclipse Foundation, the GlassFish project is very active and the community around it is certainly present and responsive.

More information:

* [Introduction to Embedded Eclipse GlassFish](https://glassfish.org/docs/latest/embedded-server-guide.html#introduction-to-embedded-glassfish-server) (GlassFish official documentation)
* [GlassFish 7.0 Delivers Support for JDK 17 and Jakarta EE 10](https://www.infoq.com/news/2023/01/glassfish-delivers-support-jdk17/) (at InfoQ)
* [Embedded GlassFish in less than 10 Minutes](https://www.youtube.com/watch?v=S6eONOG4pfQ) (video by John Yeary)
* [How to upgrade to Jakarta EE 10 and GlassFish 7](https://omnifish.ee/2023/05/06/how-to-upgrade-to-jakarta-ee-10-and-glassfish-7/) (at OmniFish blog)

This article was published in collaboration with [OmniFish](https://omnifish.ee) and the [Eclipse GlassFish](https://glassfish.org) team. For more information about Jakarta EE, Eclipse GlassFish and related topics, subscribe to follow the [OmniFish blog](https://omnifish.ee/blog/) and [Eclipse GlassFish mailing lists](https://projects.eclipse.org/projects/ee4j.glassfish/contact).  

<figure class="alignleft size-full is-resized">
 <img decoding="async" src="omnifish-logo-transparent-400px-margin.png" alt="" class="wp-image-60966" style="width:200px;height:200px" width="200" height="200">
</figure>

## OmniFish - Jakarta EE experts

* Eclipse GlassFish Production Support
* Jakarta EE Consulting
* Custom Development with Jakarta EE

For more information about OmniFish, contact them at their [contact page](https://omnifish.ee/contact-us/), or Twitter at [@OmniFishEE](https://twitter.com/OmniFishEE).
