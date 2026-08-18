---
title: "The Serverless Database You Really Want"
date: "2022-03-03T20:43:05+00:00"
lastmod: "2022-05-24T15:16:26+00:00"
description: "This article argues how Apache Cassandra is the Serverless database that is open and free as in freedom in the modern cloud era."
canonical: "https://thenewstack.io/the-serverless-database-you-really-want/"
authors:
  - "patrick-mcfadin"
image: "16701f93-data-2899899_1280-1024x682-1.jpg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Microservices"
related_posts:
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "building-scalable-streaming-applications-with-datastax-astra-streaming"
  - "cassandra-database-migration-to-kubernetes-with-zero-downtime"
  - "modernize-legacy-code-in-production-rebuild-your-airplane-midflight-without-crashing"
frozen: false
---

The dreaded part of every site reliability engineer's (SRE) job eventually: capacity planning. You know, the dance between all the stakeholders when deploying your applications. Did engineering really simulate the right load and do we understand how the application scales? Did product managers accurately estimate the amount of usage? Did we make architectural decisions that will keep us from meeting our SLA goals? And then the question that everyone will have to answer eventually: how much is this going to cost? This forces SREs to assume the roles of engineer, accountant and fortune teller.

The large cloud providers understood this a long time ago and so the term "cloud economics" was coined. Essentially this means: rent everything and only pay for what you need. I would say this message worked because we all love some cloud. It's not a fad either. SREs can eliminate a lot of the downside when the initial infrastructure capacity discussion was *maybe* a little off. Being wrong is no longer devastating. Just add more of what you need and in the best cases, the services scale themselves --- giving everyone a nice night's sleep. All this without provisioning a server, which gave rise to the term "serverless."

As serverless methodologies have burned through the application tiers, databases have turned out to be the last big thing to feel the heat of progress. No surprise though. Stateful workloads --- as in information I really want to keep --- is a much harder problem to solve than stateless workloads. The cloud providers have all released their own version of a serverless database, provided you agree to be locked into their walled garden. Open source has always served as the antidote for the dreaded lock-in, and there are really exciting things happening in the Apache Cassandra community in that regard.

## The Oracle That Foretold the Future

In the early days of distributed databases, a groundbreaking paper changed everything: the [Dynamo paper](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) from Amazon, published in 2007. In it, a team of researchers and engineers described how an ideal system would be built to maximize performance and data consistency while balancing scale and operations. To quote the paper: "A highly available key-value storage system that some of Amazon's core services use to provide an 'always-on' experience." It served as the basis for several database implementations, including what would become Apache Cassandra.

Dynamo assumed the availability of cheap, commodity hardware in the coming cloud era. As our industries have slowly morphed into building cloud native applications, the definition of commodity hardware has changed. Instead of units being bare-metal or virtual machines, we consume individual scale components of network, compute and storage. Building a serverless Cassandra database continues the work of the Dynamo paper inside this new paradigm; and with it, new scaling and deployment options that fit our cloud native world.

## Defining Commodity

In 2007 when the paper was first published, the definition of a commodity was much different than today. Most server-class systems were bulky and incredibly complex to provide the compute power needed and uptime required. "Commodity" entailed very inexpensive, small servers with the most basic CPU, disk and memory. The first time I deployed Cassandra in my infrastructure, I was able to use the commodity servers to scale out and in the process save a lot of money to achieve better results.

Then along came the cloud and even more changes in definitions. Commodity was now an instance type we could provision and pay for by the hour. This fueled a massive expansion of scale applications and the rise of cloud native but CPU, disk and memory all still had to be considered, especially in stateful workloads like a database. So, the dreaded capacity planning discussion was still happening in deployment meetings. Thankfully, the impact of making a wrong decision was much less when using cloud infrastructure, especially with Cassandra. Need more? Just add more instances to your cluster. Goodbye capacity wall, hello scale.

Now we are at a time when Kubernetes is advancing the pointer of what we can do with cloud native applications. With it, we've seen yet another shift in commodity definitions. The classic deployable server or instance type has been decomposed into compute, network and storage. Kubernetes has created a way for us to define and deploy an entire virtual data center, with the parts we need to support the applications we are deploying. Containers allow for precise control over the compute needed (and when).

Software-defined networks do all the complicated virtual wiring in our data centers dynamically. All of which creates an environment that is elastic, scalable, and self-healing. We also get the added benefit of fine-grained cost controls. Goodbye over-provisioning, hello cloud economics.

## Open Source: Now More Important Than Ever

Just like the majority of data infrastructure innovations in the past 10 years, the breadth and depth of the needed changes can only be addressed by an engaged community of users. The revolution in serverless databases will happen in open source. Clouds moved fast on early serverless implementations, but as we in open source know: to go far, we go together. The cloud economics of using a vendor-specific serverless database works great, right up until it doesn't. Free as in freedom means you should be able to use it anywhere. In a cloud, in your own datacenter, or even on your laptop.

One aspect that has driven the popularity of Kubernetes is the undeniable benefit of cloud portability and freedom. Overlay your deployment of a virtual datacenter against any provider of commodity compute, network and storage. Don't like where you are? Take your data center somewhere else. Don't like renting the services in a cloud? Run them yourself in Kubernetes. The near future will be about creating new cloud data services in Kubernetes and the communities we form around this exciting part of modern data applications.

The Dynamo pedigree of Apache Cassandra and years of proven reliability in the biggest workloads put it in a strong position for the next revolution of serverless databases. At [DataStax](https://www.datastax.com/?utm_content=inline-mention), we are the company that just loves open source Cassandra; we have seen this future direction of databases unfolding and we're excited to participate. We have also been building our own deep experience of running large-scale database cloud deployments in Kubernetes, via [DataStax Astra](https://astra.dev/3GggduL). As a result, our engineering teams have created some of the beginning work for a serverless Cassandra. We will be refining and building knowledge about how to take advantage of the new cloud native commodity definitions and passing on the lower costs of cloud economics.

Expect to see our ideas and code in a GitHub repository soon and discussions opening about what we have learned. Already the Cassandra community is talking about what will happen after 4.0 and it's safe to say that a serverless Cassandra is top of the list. Inclusion in the open source project [K8ssandra](https://k8ssandra.io/), combined with the [Stargate](http://stargate.io/) project, will further expand the freedom of deployment options and usage.

Data on Kubernetes depends on true cloud economics and scale, which takes us back to our SREs. In the near future when they are thinking about capacity planning, I would love to give them the option of having one less stressful meeting.

[*Learn more*](https://www.datastax.com/astradb/serverless)*about how [DataStax Astra](https://astra.dev/3GggduL) is enabling faster application development and streamlined operations with serverless databases.*
