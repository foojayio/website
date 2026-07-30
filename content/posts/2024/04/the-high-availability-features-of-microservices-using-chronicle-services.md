---
title: "The High Availability Features of Microservices using Chronicle Services"
slug: "the-high-availability-features-of-microservices-using-chronicle-services"
date: "2024-04-25T13:36:34+00:00"
lastmod: "2024-04-25T13:37:04+00:00"
description: "Learn how Chronicle Services, a Java-based framework optimised for low-latency microservices, meets critical requirements by integrating HA, performance, and data persistence."
authors:
  - "rob-austin"
image: "https://foojay.io/wp-content/uploads/2024/04/Screenshot-2024-04-16-at-17.51.31-1024x588-1.png"
categories:
  - "Chronicle Software"
  - "Java"
  - "Microservices"
tags:
related_posts:
frozen: false
---

**In low-latency microservices, ensuring system resilience without compromising performance is vital.**

This article explores how [Chronicle Services](https://chronicle.software/services/ "Chronicle Services"), a Java-based framework optimised for low-latency microservices, meets these critical requirements by integrating HA, performance, and data persistence.

Stateful and Stateless Services {#h2-0-stateful-and-stateless-services}
-----------------------------------------------------------------------

A [Chronicle Service](https://chronicle.software/services/ "Chronicle Service") application consists of a number of processing units known as Services, which interact with each other using events posted on [Chronicle Queues](https://chronicle.software/queue-enterprise/ "Chronicle Queues"). The Chronicle Queue is an extremely fast shared memory inter-process communication; it also has an enterprise version that facilitates replication of queues over the network.

Services can be stateless or stateful; however, the framework's real strength lies in facilitating stateful service integration without resorting to database dependencies, which are often unsuitable for low-latency requirements.

Below is an example of a 'stateful' service, which requires internal state in order to be able to handle a request. In this example of a Transaction Service, the 'balance' in the account would be the state, which needs to be maintained in order to process the incoming requests, and then update the balance.

![](/images/posts/2024/04/the-high-availability-features-of-microservices-using-chronicle-services/Screenshot-2024-04-16-at-17.50.34-1024x449.png)  
*Diagram 1: Example of a stateful service*

In the case of a 'stateless' service, state is not required for the service to handle a request. A simple example would be a service that sums up two numbers and outputs the result, as seen below.

![](/images/posts/2024/04/the-high-availability-features-of-microservices-using-chronicle-services/Screenshot-2024-04-16-at-17.50.57.png)  
*Diagram 2: Example of a stateless service*

Persistence in Chronicle Services Applications {#h2-1-persistence-in-chronicle-services-applications}
-----------------------------------------------------------------------------------------------------

Service-to-service interactions in[Chronicle Services](https://chronicle.software/services/ " Chronicle Services") applications is facilitated by [Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue"), a persistent shared memory-based model for inter-process communication. [Chronicle Services](https://chronicle.software/services/ "Chronicle Services ")utilises [Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") to provide a "store everything" model, optionally interleaved with periodic checkpointing, ensuring comprehensive logging of all system activities and state modifications.

[Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") can persist approximately 1 million messages per second, combining efficiency with high throughput.

In the event of an outage, services are able to precisely reconstruct their state by replaying operations from either the input or output persistent queues. Services are thus able to resume precisely from the point of disruption. Data loss is mitigated by adding minimal latency---a few microseconds per message to the queue.

Queue Replication for High Availability {#h2-2-queue-replication-for-high-availability}
---------------------------------------------------------------------------------------

Chronicle's High Availability solution is based on replicating the [Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") instances used to transport messages between services. Suppose a queue instance has been configured to be replicated. In that case, [Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") Replication copies messages as they are added to a queue to one or more replica queue instances, which may be on different hosts, using Chronicle's high speed TCP/IP library.

If there is an issue either with the service or a queue, then it can be restarted, or "failed over", to an instance on a different host where replicas of its required queues exist. As mentioned above, the service will be able to recreate any required state by replaying events from the replica queues, which will contain all of the events that have been duplicated from the primary queue.

[Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") Replication operates in the context of a cluster of hosts. Within the cluster, one host is set to host the Primary queue instance, sometimes referred to as the source or leader queue, and the others host Secondary queues, sometimes referred to as sink or follower queues. A service will read and post messages only from/to the source queue.

![](/images/posts/2024/04/the-high-availability-features-of-microservices-using-chronicle-services/Screenshot-2024-04-16-at-17.51.31-1024x588.png)  
*Diagram 3: Queue Replication*

An optional acknowledgement mechanism ensures events are received and stored by at least one secondary host before the replication is deemed successful, thus preventing data loss in the event of a host failure.

Optimising Latency and Reliability {#h2-3-optimising-latency-and-reliability}
-----------------------------------------------------------------------------

While acknowledgement mechanisms over the network are essential for maintaining data integrity, they naturally incur latency.[Chronicle Services](https://chronicle.software/services/ " Chronicle Services") mitigates this effect by supporting "in-flight" messages---those that have been dispatched but not yet acknowledged.

If enabled, this improves overall latency, balancing performance with looser high availability constraints.

Conclusion {#h2-4-conclusion}
-----------------------------

[Chronicle Services](https://chronicle.software/services/ "Chronicle Services") focuses on resilient, low-latency microservices, efficiently managing stateful and stateless models. It eliminates dependencies on databases for managing service state, using [Chronicle Queue](https://chronicle.software/queue-enterprise/ "Chronicle Queue") for fast data replication and recovery.

<br />

<br />
