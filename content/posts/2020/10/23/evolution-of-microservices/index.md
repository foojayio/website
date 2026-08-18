---
title: "Evolution of the Growth in Interest in Microservices"
slug: "evolution-of-microservices"
date: "2020-10-23T07:35:11+00:00"
lastmod: "2020-10-23T08:09:31+00:00"
description: "For some time now, there has been an undeniable growth in interest for Microservices. The core concept itself, however, is not that new."
canonical: "https://blog.payara.fish/evolution-of-microservices"
authors:
  - "jadon-ortlepp"
image: "microservices.jpg"
categories:
  - "Microservices"
tags:
related_posts:
frozen: false
---

For some time now, there has been an undeniable growth in interest for Microservices. The core concept itself, however, is not that new.
![Microservices Guide Final-05](https://blog.payara.fish/hs-fs/hubfs/Microservices%20Guide%20Final-05.jpg?width=9058&name=Microservices%20Guide%20Final-05.jpg)

## CORBA

As early as 1996, we talked about distributed objects, mainly in the context of an early and now mostly forgotten technology called [CORBA](https://en.wikipedia.org/wiki/Common_Object_Request_Broker_Architecture). CORBA promised to deliver business objects, for which the network and their exact implementation was mostly abstracted away.

In a CORBA world, these business objects representing a service that focused on a single thing could essentially live anywhere. If an application was the aggregation of a number of these distributed objects, each of them living on their own server, one could be updated or restarted without having to take down the entire application.

## EJB

Unfortunately, CORBA as both a spec and an implementation was riddled with issues and soon faded away. After CORBA came[EJB](https://en.wikipedia.org/wiki/Jakarta_Enterprise_Beans#:~:text=EJB%20is%20a%20server%2Dside,processing%2C%20and%20other%20web%20services.), which was originally a remote only technology, EJB re-used many parts of CORBA but also invented many parts of its own.

Specifically, the component model was unique to EJB, and at the time was actually considered as "CORBA done right", after similar attempts to create such model all painfully failed in CORBA itself. Around the time of EJB, the term "distributed objects" changed to "remote objects".

## SOAP

With CORBA and Remote EJB being based on binary protocols, the next step in this evolution came when Microsoft came up with [SOAP (Simple Object Access Protocol)](https://en.wikipedia.org/wiki/SOAP) as an XML based format, using mainly HTTP as its network protocol. Of course, many technologies that have "Simple" in their name aren't actually simple at all, and for SOAP this indeed appeared to be true.

Nevertheless, SOAP had some advantages over remote EJB in that it was language neutral and somewhat possible for humans to read the XML messages being exchanged. Java EE embraced SOAP as a first-class technology, first with JAX-RPC and then with JAX-WS. The nomenclature also changed. From "distributed objects", via "remote objects" and "simple objects", we arrived at "web services". Although there are subtle differences in concepts all these evolutionary variations can essentially do the same thing; provide a single business function to remote clients.

Despite simplifications in working with SOAP that JAX-WS had brought about, SOAP remained a rather complex technology. It had originally been designed to be used via another "Simple" technology called SMTP (Simple Mail Transfer Protocol), or in other words email. Of course, nobody ever used SOAP over email and in practice, only HTTP was used. SOAP however still carried all the baggage of its attempts to abstract from HTTP. This soon led to the creation of REST, which is a simpler and fully HTTP native style. While REST is often said to follow-up SOAP, they are somewhat different things. REST is actually a "style" with SOAP being a "protocol".

Java EE provides first-class support for REST via the JAX-RS API. This time the nomenclature stayed largely the same though; we still talk about a web service, although sometimes the term RESTful web service is used to distinguish it from a SOAP-based web service. With that, we finally arrive at Microservices.

## Microservices

[Microservices](https://en.wikipedia.org/wiki/Microservices) are an architectural type that builds on (mostly) RESTful web services. Where RESTful web services, in general, can be used for just about anything, for instance, to expose an API to external users, Microservices are specifically dedicated to be independent business services that together form a suite of such services that make up an application.

Microservices are typically associated with clouds. Not so much out of sheer necessity perhaps, but because cloud hosting was, and still is, the dominant type of deployment target when Microservices came about. Java EE itself has no explicit support for Microservices or clouds. The cloud topic was briefly touched upon for Java EE 7 but then abandoned. Java EE 8 didn't really address it either, although the EE Security API introduced in EE 8 was created with cloud deployments in mind. A separate effort called [MicroProfile](https://microprofile.io/) was started by [Payara](https://www.payara.fish/), Eclipse Foundation, IBM, Redhat, Oracle et al , and this does provide direct support for the Microservices architectural type. It builds directly on Java EE and is in fact worked on by all active Java EE vendors.

The APIs offered by MicroProfile could have been in Java EE proper, but due to Java EE being transferred and rebranded as [Jakarta EE](https://jakarta.ee/) it currently isn't. This does mean that the level of support and inclusion differs somewhat between the various vendors. Some vendors have separate products for Java EE and MicroProfile or only incorporate a few MicroProfile APIs with their main Java EE product.

Other vendors do allow a free mix and match but require a separate configuration and/or install step for that. There's also a difference between whether the MicroProfile APIs are included in commercial support or not; for some vendors, this is the case, for others, it's not.

\*Originally Written by Arjan Tijms and Ondrej Mihalyi
