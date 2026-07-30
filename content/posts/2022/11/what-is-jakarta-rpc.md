---
title: "What is Jakarta RPC? | Foojay.io Today"
slug: "what-is-jakarta-rpc"
date: "2022-11-10T15:32:52+00:00"
lastmod: "2022-11-10T17:45:32+00:00"
description: "gRPC is an open source Remote Procedure Call framework; an alternative to REST. Find out more and how Payara is involved!"
canonical: "https://blog.payara.fish/what-is-jakarta-rpc"
authors:
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2020/10/payara_square_logo.jpg"
categories:
  - "Jakarta EE"
  - "Payara"
tags:
related_posts:
frozen: false
---

*Originally written by Alfonso Altamirano*

Jakarta EE is at the forefront of innovation in enterprise Java. This means staying close to the community and working on new specifications to meet developer needs.

Now that [Jakarta EE 10](https://blog.payara.fish/payara-welcomes-jakarta-ee-10) has been released, the first release to bring new features since the move to the `jakarta `namespace, all the upcoming releases of Jakarta EE will bring new features. The improvements will keep arriving!

One of the new specifications being worked on is Jakarta RPC, a specification accepted in January of this year and building on the popularity of [Google Remote Procedure Call,](https://grpc.io/) or gRPC.

At Payara, we are Strategic Members of the [Jakarta EE](https://jakarta.ee/) Working Group and members of our team (including myself) are part of the project group, working on the RPC specification in particular.

In this blog, I'll help you understand this specification in this very early stage - and how you can get involved!

gRPC is an open source Remote Procedure Call framework; an alternative to [REST.](https://blog.payara.fish/getting-started-with-jakarta-ee-9-how-to-create-a-rest-api-with-jakarta-ee-9)

It has become a de-facto standard for inter-service communication, widely adopted by enterprises, startups, and open source projects. It gives to the developer the option to implement services with the following advantages. You can:

* Create services more efficient than REST.
* Use HTTP/2 to provide full duplex communication between the client and the service.
* Use protocol buffers to optimize and simplify communication.
* Enable bidirectional streaming out-of-the-box.

As part of the wide acceptance of the gRPC framework, vendors have been providing their own implementations. Payara created [its own gRPC extension](https://docs.payara.fish/community/docs/documentation/extensions/grpc/README.html#what-is-grpc) in response to a customer request; you can see an example [here](https://blog.payara.fish/grpc-example-in-payara-server).

#### Background

The Jakarta RPC project responds to the popularity of gRPC, and makes it easier and simpler to use it within the Jakarta EE ecosystem, allowing for the creation of services and clients.

* 2018: The initial implementation was made in a coherence project.
* 2019: It was contributed to Helidon.
* 2021: [Proposed](https://projects.eclipse.org/proposals/jakarta-rpc) as a Jakarta EE specification.

At the end of 2021, the specification was delivered, and during January of 2022 it was accepted.[](https://projects.eclipse.org/proposals/jakarta-rpc)

#### Aims

Based on this, the community identified some aspects that can be improved, to be part of the official version:

* Eliminating the need for IDL/proto files and build-time code generation.
* Simplify the way to implement gRPC services.
* Simplify the way to implement gRCP clients.
* Improve integration with Jakarta EE.
* Improve integration with Eclipse [MicroProfile.](https://microprofile.io/)
* Better support for modern Java.
* Better support for payload marshalling.

NOTE: The idea is not to replace the official [java-grpc](https://github.com/grpc/grpc-java) project, but to implement a specification that makes its use simpler with Jakarta EE. We aim to enhance implementations of gRPC and encourage its acceptance within the Jakarta EE Community.

The project's aim is to enable a mechanism based on annotations (like the way the REST services implementation works) to create services on the server side and annotated interfaces on the client. Additionally, we wish to simplify gRPC's integration with other technologies from the Jakarta EE ecosystem like the [CDI](https://www.payara.fish/resource/jakarta-ee-cdi-fact-sheet/) and Config specifications.

Another important aim is to increase flexibility. This is because the current version of gRPC is coupled with Protobuf ([Protocol buffers](https://developers.google.com/protocol-buffers)) to create the stub resources that are needed for service and client implementation, and the intense use of the ["final" keyword](https://en.wikipedia.org/wiki/Final_(Java)) for the generated stubs makes it very difficult to integrate with CDI.

So, by enabling and giving more options for the serialization format and removing the intense use of "final", Jakarta RPC will improve flexibility and enhance the implementation.

The initiative is still new, but we are working on releasing the first version for the Jakarta EE ecosystem.

As members of the Jakarta EE Working Group, we are actively review initiatives and of course Jakarta-RPC is one of them. As members of the project group, we are going to work on the following tasks:

* Making code contributions.
* Revising code.
* Testing code contributions.
* Creating documentation.
* Documentation review.
* Participating in analysis and design meetings.

Follow the the following links to find out more information about the project and how to contribute:

* <https://projects.eclipse.org/projects/ee4j.rpc>
* <https://projects.eclipse.org/proposals/jakarta-rpc>

Jakarta EE is an open source project and we encourage you to get involved! You can help in every part of the process, from creating the specification documentation, to working on the Technology Compatibility Kit (TCK), the test suite that is used to determine whether independent implementations of the API meet its requirements.

Contributing to open source is a great way to learn, boost your CV and help others develop the kind of applications you love.

We hope in this blog we have given you a good overview of one of the exciting specifications being worked on in Jakarta EE.

Jakarta EE 10 has proved that specifications are thriving under the Eclipse Foundation and we can look forward to more innovation like this, keeping Enterprise Java at the cutting edge!

We often like to take a close look at different Jakarta EE Specifications, you may be interested in:

* [Jakarta EE CDI Fact Sheet](https://www.payara.fish/resource/jakarta-ee-cdi-fact-sheet/)
* [Jakarta Concurrency: Present and Future](https://blog.payara.fish/jakarta-concurrency-present-and-future)
* [Jakarta EE 10: What Decision Makers Need to Know](https://blog.payara.fish/jakarta-concurrency-present-and-future)
