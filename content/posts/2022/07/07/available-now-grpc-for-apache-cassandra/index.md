---
title: "Available Now - gRPC for Apache Cassandra"
date: "2022-07-07T07:38:47+00:00"
lastmod: "2022-07-07T07:39:32+00:00"
description: "General availability of a gRPC API for Apache Cassandra to leverage a powerful database in combination with a microservices-oriented API."
canonical: "https://www.datastax.com/blog/available-now-grpc-apache-cassandra"
authors:
  - "pieter-humphrey"
image: "2dc360f43686ee3be7bcd2b10d7691ae9fd3b65f-1460x968-1.png"
categories:
  - "Apache Cassandra"
  - "DataStax"
  - "Microservices"
  - "Release Notes"
  - "Tutorials"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "build-a-status-dashboard-using-spring-boot-and-astra-db"
  - "unified-event-driven-architecture-for-the-cloud-native-enterprise"
frozen: false
---

**Build microservices easily with the NoSQL standard database**

If you are like most developers, you are embracing applications built using microservices and a NoSQL database. There are many good reasons: faster time to market, lower total cost of ownership, better performance, less downtime, and easy scalability.

Creating cloud-native applications is hard because microservices are often written in different languages, database drivers have a maintenance burden, or familiar HTTP APIs lack the performance needed.

We are excited to announce the general availability of a gRPC API for Apache Cassandra. Now you can leverage a powerful database in combination with a microservices-oriented API for performant applications.

Native database drivers offer high performance but not all programming languages have commercially supported drivers. Writing and maintaining a native driver is complex and time-consuming because it involves significant configuration management (retries, load balancing, and pooling, for example). Community drivers alleviate development work but often lack enterprise functionality because active development is intermittent.

### **Introducing the gRPC API**

One way to address these challenges is with gRPC, an open-source, high-performance, remote procedure call (RPC) framework developed by Google. DataStax has leveraged this technology to create a gRPC API to query Cassandra that's 100% open source and already includes client libraries for Go, Rust, Node.js, and Java.

Client libraries send CQL queries from the application to the gRPC API which in turn passes those queries to Cassandra. The gRPC API is part of Stargate, an open-source project that serves as a data API gateway to make it easier to work with data in Cassandra. Stargate sits between your applications and Cassandra and includes schemaless JSON, REST, and GraphQL APIs, providing alternatives to traditional database drivers.  
![gRPC for Apache Cassandra\[8X faster\](https://docs.microsoft.com/en-us/dotnet/architecture/cloud-native/grpc "8X faster")](gRPC-blog-image.png)

### **How a gRPC API can solve your challenges**

gRPC is unanimously accepted as the best option for communicating between microservices for two reasons: unmatched performance and multilingual support. Its high-performance results from using a binary data format rather than text-based JSON, resulting in a smaller payload. In some studies this data serialization has made gRPC [8X faster](https://docs.microsoft.com/en-us/dotnet/architecture/cloud-native/grpc "8X faster"). Since gRPC allows for describing a service contract in a binary format, there's a standard way to specify those contracts, regardless of the programming language used, thus ensuring interoperability. If you're creating applications using polyglot microservices, gRPC is the best choice for exchanging data between these services and Cassandra.

Today, DataStax provides a gRPC API with lightweight client libraries for Go, Rust, Node.js, and Java---with the potential to develop more. If you are creating applications using Go or Rust and struggling because there are no enterprise drivers, these client libraries are a viable option andare far easier to maintain than native drivers. Changes that would normally require a driver update can now be made within the Stargate gRPC API; no client library changes are required. There's no application downtime or recompiling drivers.

By offering a gRPC API, we are lowering the bar for creating clients in existing or new programming languages, enabling you and the Cassandra community to deliver support for languages beyond those directly delivered by DataStax. gRPC autogenerates a non-trivial amount of code used to manage operational tasks such as connection pooling, TLS, authentication, load balancing, retry policies, write coalescing, compression, and health checks. DataStax-maintained message-definition files allow for auto-generation of language-specific code that encodes and decodes protobuffers for communicating with Stargate. Abstracting away operational tasks to the underlying infrastructure is consistent with a cloud-native design pattern.

By creating client libraries for a gRPC API, you avoid developmental tasks associated with creating drivers and operational tasks associated with maintenance and deployment, thus saving time and reducing complexity. If you find yourself working with a language or framework that is early in the adoption cycle, you can use our [reference examples](https://docs.datastax.com/en/astra/docs/gRPC.html "reference examples") to create the needed client library.

In summary, gRPC introduces a potent alternative to native Cassandra drivers---one that's modern, performant, and microservice-oriented. We recommend using gRPC if you:

1. Need to interconnect microservices written in different languages
2. Need supported Go or Rust drivers
3. Need to create a client library for framework/language that doesn't have a driver
4. Need native driver-level performance without using a driver

DataStax will continue to introduce more native drivers and innovate with gRPC to create additional ways to interact with Cassandra.

### **How to get started**

Ready to find out if gRPC is a fit for you? If you are using Apache Cassandra (3.x and higher) or DataStax Enterprise 6.8, download and install [Stargate](https://downloads.datastax.com/#stargate "Stargate") to access the gRPC API.

The fastest and easiest way to try out this API is with the free tier on our DBaaS offering, [DataStax Astra DB](http://astra.datastax.com/ "DataStax Astra DB"), which already comes out of the box with API endpoints for gRPC.

Sign up for a free account today!
