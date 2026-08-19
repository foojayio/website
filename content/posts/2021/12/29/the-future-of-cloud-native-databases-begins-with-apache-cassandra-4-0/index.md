---
title: "The future of cloud-native databases begins with Apache Cassandra 4.0"
date: "2021-12-29T07:40:00+00:00"
lastmod: "2022-01-25T17:12:32+00:00"
description: "“Reliability at massive scale is one of the biggest challenges we face at Amazon.com, one of the largest e-commerce operations in the world; even the…"
canonical: "https://www.datastax.com/blog/future-cloud-native-databases-begins-apache-cassandra-4"
authors:
  - "patrick-mcfadin"
image: "a6ffe0f0c744734f6c75c45ac4ab9d3bc6f4322b-1460x968-1.png"
categories:
  - "Apache Cassandra"
  - "Cloud"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Kubernetes"
related_posts:
frozen: false
---

> "Reliability at massive scale is one of the biggest challenges we face at Amazon.com, one of the largest e-commerce operations in the world; even the slightest outage has significant financial consequences and impacts customer trust."

This was the first line of the highly impactful [paper](http://www.cs.cornell.edu/courses/cs5414/2017fa/papers/dynamo.pdf) titled "Dynamo: Amazon's Highly Available Key-value Store." Published in 2007, it was written at a time when the status quo of database systems was not working for the massive explosion of internet-based applications. A team of computer engineers and scientists at Amazon completely re-thought the idea of data storage in terms of what would be needed for the future, with a firm footing in the computer science of the past. They were trying to solve an immediate problem but they had unwittingly sparked a huge revolution with distributed databases and the eventual collision with cloud-native applications.

### The original cloud-native database

A year after the Dynamo paper, one of the authors, Avinash Lakshman, joined forces with Prashant Malik at Facebook and built one of the many implementations of Dynamo, called Cassandra. Because they worked at Facebook, they were facing scale problems very few companies were dealing with at the time. Another Facebook tenet in 2008: Move fast and break things. The reliability that was at the top of Amazon's wish list for Dynamo? Facebook was challenging that daily with frenetic non-stop growth. Cassandra was built on the cloud-native principles of scale and self-healing—keeping the world's most important workloads at close to 100% uptime and having been tempered in the hottest scale fires. Now, with the release of Cassandra 4.0, we are seeing the beginning of what's next for a proven database and the cloud-native applications that will be built in the future. The stage is set for a wide range of innovation—all built on the shoulders of the Dynamo giant.

### The prima donna comes to Kubernetes

The previous generation of databases before the NoSQL revolution arguably drove a lot of innovation in the data center. It was typical to spend the most time and money on the "big iron" database server that was required to keep up with demand. We built some amazing palaces of data on bare metal, which made the pressure to virtualize database workloads difficult in the early 2000s. In most cases, database infrastructure sat on dedicated hardware next to the virtualized systems of the application. As cloud adoption grew, similar issues persisted. Ephemeral cloud instances worked great for web and app servers, but "commodity" was a terrible word for the precious database. The transition from virtualization to containerization only increased the cries of "never!" for database teams. Undaunted, Kubernetes moved forward with stateless workloads, and databases remained on the sidelines once again. Those days are now numbered. Technical debt can grow unbounded if left unchecked. Organizations don't want multiple versions of infrastructure to manage—it requires hiring more people and keeping track of more stuff. When deploying virtual datacenters with Kubernetes, the database has to be a part of it.

Some objections are valid when it comes to running a database in a container. The reasons we built specialized hardware for databases are the same reasons we need to pay attention to certain parts of a containerized database. High-performance file systems. Placement of the system away from other containers that could create possible contention and reduce performance. With distributed databases like Apache Cassandra, placement of individual nodes in a way that hardware failure doesn't impact database uptime. Databases that have proven themselves before Kubernetes are trying to find ways to run on Kubernetes. The future of databases and Kubernetes requires we replace the word "on" with "in" and the change has to happen on the database side. The current state of the art for "Runs on Kubernetes" is the use of operators to translate how databases want to work into what Kubernetes wants them to do. Our bright future of "Runs in Kubernetes" means databases use more of what Kubernetes offers with resource management and orchestration for basic operation of the database. Ironically, it means that many databases could remove entire parts of their code base as they hand that function to Kubernetes (reducing the surface area for bugs and potential security flaws).

### Cassandra is ready for what's next

The fact that Apache Cassandra 4.0 was recently released is a huge milestone for the project when it comes to stability and a mature codebase. The project is now looking forward to future Cassandra versions building on this solid foundation. Primarily, how can it support the larger ecosystem around it by becoming a rock-solid foundation for other data infrastructure? During the past decade, Cassandra has built a reputation as a highly performant and resilient database. With the types of modern cloud-native applications we need to write, we'll only need more of that—interoperability will only become more important for Cassandra.

To think of what a cloud-native Cassandra would look like, we should look at how applications are deployed in Kubernetes. The notion of deploying a single monolith should be left rusting in the same pile that my old Sun E450 database server is in now. Cloud-native apps are modular and declarative and adhere to the principles of scale, elastic, and self-healing. They get their control and coordination from the Kubernetes cluster and participate with other parts of the application. The need for capacity is directly linked to the needs of the running application and everything is orchestrated with the total application. The virtual data center acts as a unit but can survive underlying hardware problems and works around them.

### Ecosystem as a first-class

The future of Cassandra in Kubernetes isn't about what it does alone. It's about what new capabilities it brings to the system as a whole. Projects like Stargate create a gateway for developers to build API-based applications without interacting with the underlying data store. Data as a service deployed by you, in your own virtual data center using Kubernetes. Cassandra itself may be using enabling projects such as [OpenEBS](https://docs.openebs.io/docs/next/cassandra.html) to manage database class storage. Or [Prometheus](https://docs.datastax.com/en/cass-operator/doc/cass-operator/cassOperatorMetricReporterDashboards.html) to store metrics. You may even find yourself using Cassandra without it being a part of your application. Projects like [Temporal](https://docs.temporal.io/docs/server/configuration/#persistence) use Cassandra as the underlying storage for their persistence. When you have a data service that deploys easily, scales across multiple regions, it's an obvious choice.

From the spark of innovation that started with the Dynamo paper at Amazon to the recent release of 4.0, Cassandra was destined to be the cloud-native database we all need. The next ten years of data on Kubernetes will see even more innovation as we take the once ivory palace of the database server and make it an equal player as a data service in the application stack. Cassandra is built for that future and ready to go with what is possibly the most stable database release ever in 4.0. If you are interested in joining the data on Kubernetes revolution, you can find an amazing community of like-minded individuals at the [Data on Kubernetes Community](https://dok.community/). If you want to help make Cassandra the default Kubernetes data store, you can join us at the [Cassandra project](https://cassandra.apache.org/community/) or more specifically the Cassandra on Kubernetes project, [K8ssandra](https://k8ssandra.io/). If you are new to Cassandra, [Astra DB](https://astra.dev/3EXzS07) is a great (free) place to learn with none of the infrastructure setup headaches.
