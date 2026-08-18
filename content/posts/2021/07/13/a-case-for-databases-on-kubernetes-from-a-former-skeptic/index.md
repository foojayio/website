---
title: "A Case for Databases on Kubernetes from a Former Skeptic"
date: "2021-07-13T09:12:18+00:00"
lastmod: "2021-11-10T23:02:46+00:00"
description: "Should you run a database on Kubernetes? Let's retrace the stages in the journey to my current answer: “In a cloud native environment? Yes!”"
canonical: "https://thenewstack.io/a-case-for-databases-on-kubernetes-from-a-former-skeptic/"
authors:
  - "christopher-bradford"
image: "6a7cf1a6-data-2899899_1280-1-1024x682-1.jpg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Kubernetes"
related_posts:
frozen: false
---

Kubernetes is everywhere. Transactional apps, video streaming services and machine learning workloads are finding a home on this ever-growing platform. But what about databases?

If you had asked me this question five years ago, the answer would have been a resounding "**No!** " --- based on my experience in development and operations. In the following years, as more resources emerged for stateful applications, my answer would have changed to "***Maybe**,"* but always with a qualifier: "It's fine for development or test environments..." or "If the rest of your tooling is Kubernetes-based, and you have extensive experience..."

But how about today? Should you run a database on Kubernetes? With complex operations and the requirements of persistent, consistent data, lets retrace the stages in the journey to my current answer: "In a cloud native environment? **Yes!**"

### Stage 1: Running Stateless Workloads on Kubernetes, But Not Databases!

When Kubernetes landed on the DevOps scene, I was keen to explore this new platform. My automation was already dialed in with Puppet configuring hosts and Capistrano shuffling my application bits to virtual servers. I had started exploring Docker containers and loved how I no longer had to install and manage services on my developer workstation. I could just fire up a few containers and continue changing the world with *my* code.

Kubernetes made it trivial to deploy these containers to a fleet of servers. It also handled replacing instances as they went down, and keeping a number of replicas online. No more getting paged at all hours! This was *great* for stateless services, but what about databases? Kubernetes promised agility, but my databases were tied to a giant boat anchor of data. If I ran a database in a container, would my data be there when the container came back? I didn't have time to solve this problem, so I fired up a managed RDBMS and moved on to the next feature ticket. Job done.

### Stage 2: Running Ephemeral Databases on Kubernetes for Testing

This question came up again when I needed to run separate instances of an application for QA testing per GitHub pull request (PR). Each PR needed a running app instance *and a database* . We couldn't just run against a shared database, since some of the PRs contained schema changes. I didn't need a pretty solution, so we ran an instance of the RDBMS in the same *pod* as the app; and pre-loaded the schema and some data. We tossed a reverse proxy in front of it and spun up the instances on-demand as needed. QA was happy as there was no more scheduling of PRs in the test environment, the product team enjoyed feature environments to test drive new functionality, and ops didn't have to write a bunch of automation. This felt like a completely different situation to me, because I never expected these environments to be anything but ephemeral. It certainly wasn't cloud native, so I still wasn't ready to replace my managed database with a Kubernetes-deployed database in production.

### Stage 3: Running Cassandra on Kubernetes StatefulSets

Around this time, I was introduced to Apache Cassandra®. I was amazed by this high-performance database with a phenomenal operations story. A database that could support losing instances? Sign me up! My hopes of running a database on Kubernetes came roaring back. Could Cassandra deal with the ephemeral nature of containers? At the time, it felt like a begrudging "*I guess?* ". It seemed possible, but there were significant gaps in the tooling. To take this to production, I'd need a team of Kubernetes *and* Cassandra veterans, plus a suite of tooling and runbooks to fill in the operational gaps. It certainly seemed like a number of teams were successfully running Cassandra in containers. I fondly recall a webinar by Instaclustr talking about running [Cassandra on CoreOS](https://www.youtube.com/watch?v=rhqSmc9meMw).

In parallel, a number of Kubernetes ecosystem changes started to solidify. StatefulSets handle the creation of pods with persistent storage according to a predictable naming scheme. The persistent volume API and the container storage interface (CSI) allow for loose coupling between compute and storage. In some cases, it's even possible to define storage that follows the application as it is rescheduled around the cluster.

Storage is the core of every database. In a containerized database, data may be stored within the container itself or mounted externally. Using external storage makes it possible to switch the container out to change configuration or upgrade software, while keeping the data intact. Cassandra is already capable of leveraging high performance local storage, but the flexibility of modern CSI implementations means data volumes are moved to new workers as pods are rescheduled. This reduces the time to recovery, as data no longer has to be synced between hosts in the case of a worker failure.

### Stage 4: A Kubernetes Operator for Cassandra

With straightforward deployment of Cassandra nodes to pods, resilient handling of data volumes and a Kubernetes control plane that works to keep everything running, what more could we ask for? At this point I encountered the collision of two separate distributed systems that have been developed independently from each other. The way Kubernetes provisions pods and starts services does **not** align with the operational steps needed to care and feed for a Cassandra cluster --- there's a gap that must be bridged between Kubernetes workflows and Cassandra runbooks.

Kubernetes provides a number of built-in resources --- from a simple building block like a Pod, to higher-level abstractions such as a Deployment. These resources let users define their requirements, and Kubernetes provides control loops to ensure that the running state matches the target state. A control loop takes short incremental actions to nudge the orchestrated components towards a desired end state --- such as restarting a pod, or creating a DNS entry. However, domains like distributed databases require more complex sequences of actions that don't fit nicely within the predefined resources.This is great, but not everything fits nicely within a predefined resource.

Kubernetes Custom Resources were created to allow the Kubernetes API to be extended for domain-specific logic, by defining new resource types and controllers. OSS frameworks like [operator-sdk](https://sdk.operatorframework.io/), [kubebuilder](https://github.com/kubernetes-sigs/kubebuilder) and [juju](https://juju.is/) were created to simplify the creation of custom resources and their controllers. Tools built with these frameworks came to be known as Operators.

As these powerful new tools became available, I joined the effort to codify the Cassandra logical domain and operational runbooks in the cass-operator project. Cass-operator defines the CassandraDatacenter custom resource and provides the glue between projects including the management API, cass-config-builder and others, to provide a cohesive Cassandra experience on Kubernetes.

With cass-operator, we spend less time thinking about pods, stateful sets, persistent volumes, or even the tedious tasks of bootstrapping and scaling clusters, and more time thinking about our applications.

### Stage Now: Running a Full Data Platform with K8ssandra

The next iteration in this cycle, [K8ssandra](https://k8ssandra.io/), elevates us further away from the individual components. Instead of looking at the Cassandra Datacenters, we can consider our data platform holistically: not just the database, but also supporting services including monitoring, backups and APIs. We can ask Kubernetes for a data platform by executing a simple Helm install command; and a suite of operators kick in to provision and manage all of the pieces.

Looking back at the pitfalls of running databases on Kubernetes I encountered several years ago, most of them have been resolved. Starting with a foundational technology like Cassandra takes care of our availability concerns: data is replicated and it's smart enough to deal with shuffling data around as peers come and go. The Kubernetes API has matured to include custom resources and advanced stateful components (like persistent volumes and stateful sets). Cass-operator acts as a Rosetta Stone, providing the wealth of knowledge needed to stitch the terms of Cassandra and Kubernetes together. Finally, K8ssandra takes us to the next level with a complete cohesive experience.

All of these problems are ***hard*** and require technical finesse and careful thinking. Without choosing the right pieces, we'll end up resigning both databases and Kubernetes to niche roles in our infrastructure, as well as the innovative engineers who have invested so much effort in building out all of these pieces and runbooks. Fortunately each of these problems has been met and bested. Should you run your database in Kubernetes? *Definitely.*
