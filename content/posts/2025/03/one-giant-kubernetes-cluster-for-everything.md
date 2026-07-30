---
title: "One giant Kubernetes cluster for everything"
slug: "one-giant-kubernetes-cluster-for-everything"
date: "2025-03-25T10:33:50+00:00"
lastmod: "2025-03-25T10:33:52+00:00"
description: "The ideal size of your Kubernetes clusters is a day 0 question and demands a definite answer. You find one giant cluster on one end of the spectrum and - by Nicolas Frankel"
canonical: "https://www.loft.sh/blog/one-giant-kubernetes-cluster"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2025/03/giant-kubernetes-1650x927-1.png"
categories:
  - "DevOps"
  - "Kubernetes"
tags:
related_posts:
frozen: false
---

The ideal size of your Kubernetes clusters is a day 0 question and demands a definite answer.

You find one giant cluster on one end of the spectrum and many small-sized ones on the other, with every combination in between. This decision will impact your organization for years to come. Worse, if you decide to change your topology, you're in for a time-wasting and expensive ride.

I want to list each approach's pros and cons in this post. Then, I'll settle the discussion once and for all and argue why selecting the giant cluster option is better.

The one giant cluster approach {#h2-0-the-one-giant-cluster-approach}
---------------------------------------------------------------------

Deciding on a single giant cluster has great benefits.

### Better resource utilization {#h3-1-better-resource-utilization}

Kubernetes was designed to handle large-scale deployments, initially focusing on managing thousands of nodes to support extensive and complex containerized applications. This scalability was a key feature from its inception, enabling it to orchestrate resources across vast, distributed systems efficiently.

Thus, a Kubernetes cluster is a scheduler at its core: it knows how to run workloads on nodes according to constraints. Without constraints, it will happily balance workloads across its available nodes. If you split the cluster into multiple clusters, you lose this benefit. You can have situations where one cluster is idle while another cluster is close to resource starvation and must cancel workloads and kill pods.

### Lower operational overhead {#h3-2-lower-operational-overhead}

Good Kubernetes practices mandate that you back up your etcd data, monitor your cluster metrics, log your cluster events, provide security-related tools, etc. Size aside, it stands to reason that it's more time-effective to operate fewer clusters.

For example, regarding metrics, you'd set up a single Prometheus instance, potentially clustered to handle additional traffic, and be done with it. Automation can mitigate the repetitive aspect of installing and maintaining an instance for each cluster, but you'll still end up with as many instances as you have clusters (or more). Prometheus is just one example because many cluster admins have a long list of tools they run in every cluster.

### Straightforward networking and service communication {#h3-3-straightforward-networking-and-service-communication}

Service-to-service communication inside a single cluster is straightforward. Point to `..svc.cluster.local` and be done with it. Even better, you only need the service name part inside the same namespace.

You'll need a tool to help you with inter-cluster communication, from the simplicity of External DNS with LoadBalancer to the complexity of a full-fledged solution like Istio---both ends of the spectrum mandate time and operation costs.

### Simplified governance {#h3-4-simplified-governance}

Since every object is part of the same cluster inside a single cluster, one can enforce a centralized set of policies with a standardized approach. For example, you can create a namespace per team and environment, restricting access to only that team's members.

Once you start having multiple clusters, even if you take the same approach, you'll duplicate the policy rules across clusters, with potential differences that will drift further with time.

### Cost efficiency {#h3-5-cost-efficiency}

A single cluster means a single control plane, simplifying management and reducing overhead. While a control plane is essential for orchestration, its value comes from enabling efficient workload execution rather than directly running business applications.

Additionally, many of the points above tie into cost optimization. With a single cluster, you only need to configure monitoring (*e.g.*, Prometheus), logging, and security tools once, reducing duplication. In-place automation streamlines operations, helping manage costs without adding unnecessary infrastructure expenses.

Downsides of a one giant cluster approach {#h2-6-downsides-of-a-one-giant-cluster-approach}
-------------------------------------------------------------------------------------------

Unfortunately, the giant cluster option is not only unicorns and rainbows; there are definite downsides.

### Larger blast radius {#h3-7-larger-blast-radius}

The larger the cluster, the more teams will use it. Unfortunately, this means that if something bad happens to the cluster, it will wreak havoc on the work of more teams. It occurs regardless of whether the outage results from a malicious actor, a wrong configuration, or resource starvation.

When an actual malicious actor does indeed breach access, a larger cluster exposes more workloads, and the actor can compromise more of them.

Even without malicious actors, every maintenance operation and upgrade on a cluster can affect its users; the bigger the cluster, the larger the potential impact. When planning an upgrade for a single cluster, you need to conduct an impact analysis that encompasses all users and teams in the organization.

### Complex multi-tenancy management {#h3-8-complex-multi-tenancy-management}

Even within a single organization, multiple teams will use the Kubernetes cluster. Even if every team member behaves professionally, we must set strict policies to avoid issues. If the cluster resembles a building, you'd still put locks on your apartment even if you have friendly neighbors. Likewise, the cluster administrator must enforce strict rules to make sharing a cluster acceptable. At the very least, we need strict namespace isolation to avoid unnecessary access and resource quotas and enforce fairness across teams. The problems caused by using a single cluster by teams across a single organization multiply a hundredfold if the cluster is multi-tenant and shared across several organizations.

### Scalability limits {#h3-9-scalability-limits}

Regardless of how exceptional Kubernetes is, it's still a physically based system with physical limits. For example, some objects have a clear limit, but even if you never reach them, getting close to them will require excellent system administration skills with some [fine-tuning](https://openai.com/index/scaling-kubernetes-to-2500-nodes/).

Even then, the more load you put on a Kubernetes API server, the more sluggish your system will be. If you're lucky, it will degrade linearly, but chances are it will hit some system limit and degrade all at once.

### Cluster-wide objects {#h3-10-cluster-wide-objects}

Kubernetes objects are namespace-scoped, but a couple of them are cluster-scoped. A cluster-scoped object can only have a single instance across the whole cluster. For example, a Custom Resource Definition is cluster-scoped.

It means that if a team wants to use v1 of a CRD, then every team on the same cluster is stuck with v1 if they wish to use this CRD. Worse, if any team wants to upgrade to v2, they must coordinate across all teams using the CRD to synchronize the upgrade.

What's the ideal size, then? {#h2-11-what-s-the-ideal-size-then}
----------------------------------------------------------------

I could describe the pros and cons of very granular clusters, but they mirror the opposite of what we have just seen. For example, very granular clusters allow each team to work on their version of a CRD without stepping on another team's toes. For this reason, I'll avoid repeating myself.

Most, if not all, articles evaluating the pros and cons of each end of the spectrum advise a meet-in-the-middle approach: "a couple" of clusters to mitigate the worst aspects of each extreme approach. It's all well and good, but none of them, at least none I've read, tell precisely how many "a couple" is. Is it a cluster per environment, *i.e.*, production, staging, or development? Is it a cluster per team?

<img fetchpriority="high" decoding="async" class="aligncenter wp-image-115768 size-medium" src="/images/posts/2025/03/one-giant-kubernetes-cluster-for-everything/ideal-cluster-size-700x313.png" alt="What's the ideal cluster topology?" width="700" height="313">

<br />

‎I'll take a risk and advertise for two clusters: one for production and the other for everything else. How would you manage the cons mentioned above? Read on.

vCluster {#h2-12-vcluster}
--------------------------

[vCluster](https://www.vcluster.com/) is an Open Source product that allows creating of so-called virtual clusters. vCluster is part of the CNCF landscape, specifically a [certified Kubernetes distribution](https://www.cncf.io/training/certification/software-conformance/). Being a certified distro means a virtual cluster offers every Kubernetes API you can expect, and you can deploy any application to it just like any other Kubernetes cluster.

vCluster operates by creating the virtual cluster in a dedicated namespace. You can specify the latter, or vCluster will infer it from the virtual cluster's name. By default, it creates a control plane using the vanilla k8s distribution, but you can choose another one, such as k3s. Likewise, by default, it stores its configuration in an SQLite database, which works particularly well for temporary and pre-production clusters, such as those you create for a pull request. Alternatively, you can rely on a regular etcd or even external databases such as MySQL or Postgres as a data store for more permanent usage and better resilience and scalability of the virtual cluster.

<img decoding="async" class="aligncenter wp-image-115769 size-medium" src="/images/posts/2025/03/one-giant-kubernetes-cluster-for-everything/vcluster-700x510.png" alt="Virtual clusters inside a host cluster" width="700" height="510">

<br />

Once you've created a virtual cluster via the CLI or the Helm chart, you can connect to it. The client-side CLI creates a dedicated reusable kubeconfig context. From within a virtual cluster, users see no other virtual clusters.

If you need to access the host cluster resources from the virtual cluster or vice versa, vCluster uses a so-called syncer that syncs objects back and forth according to a configuration file. This way, you can set up an Ingress Controller on the host cluster and define your Ingress objects in the virtual cluster(s).

How vCluster mitigates the downsides of a giant cluster {#h2-13-how-vcluster-mitigates-the-downsides-of-a-giant-cluster}
------------------------------------------------------------------------------------------------------------------------

Let's review each downside of a giant cluster and how vCluster handles it.

* Larger blast radius: When using a virtual cluster, the blast radius is automatically contained inside its boundaries. If you want to be conservative, aim for a small granularity approach, such as a cluster per team and environment.
* Complex multi-tenancy management: Gone are multi-tenancy problems since your tenants don't see each other and are isolated inside their respective virtual clusters.
* Scalability limits: While the limits are still there, the chances of reaching them decrease with the number of virtual clusters. If your giant cluster had 100k services, they are now spread throughout all virtual clusters.
* Upgrades and maintenance risks: Upgrade and maintenance tasks are limited to the scope of a single virtual cluster. You can do them in turn, and they will only affect the virtual clusters you target.
* Cluster-wide objects: Finally, with virtual clusters, every team can install their version of a CRD, and its virtual cluster binds the CRD. It allows each team to be entirely independent of each other regarding the version of a CRD they use.

But I need different clusters! {#h2-14-but-i-need-different-clusters}
---------------------------------------------------------------------

While a single giant cluster provides compelling advantages, there are contexts in which a multi-cluster approach is justified. The most common reason is geographic distribution---specific applications require clusters in multiple regions to meet compliance requirements, reduce latency, or provide disaster recovery. For example, companies operating under GDPR or financial regulations may need strict data residency enforcement, which requires region-specific clusters. Similarly, organizations with stringent security postures may enforce complete isolation between environments or business units, making separate clusters a hard requirement.

However, even in these cases, vCluster remains relevant. It allows for minimizing the number of physical clusters while still enabling workload separation at a virtual level. Instead of creating a sprawling landscape of Kubernetes clusters, teams can deploy regional virtual clusters within a single host cluster, balancing isolation and operation complexity.

Conclusion {#h2-15-conclusion}
------------------------------

Kubernetes cluster topology decisions are critical and long-lasting. While many advocate for a middle-ground approach between a single cluster and many small ones, they rarely specify the exact setup. Instead of guessing how many clusters to create, consolidating everything into a single, well-managed giant cluster makes more sense. The benefits---better resource utilization, lower operational overhead, simplified networking, centralized governance, and cost efficiency---outweigh the downsides.

That said, the traditional downsides of a giant cluster, such as a larger blast radius, multi-tenancy complexities, scalability limits, upgrade challenges, and cluster-wide object constraints, are valid concerns. This is where vCluster changes the game. By using virtual clusters, you retain all the advantages of a single giant cluster while mitigating its worst drawbacks. vCluster isolates workloads, reduces operational risk, scales dynamically, simplifies upgrades, and removes conflicts over cluster-wide objects.

Enhanced with vCluster, one cluster for production and one giant cluster for everything else, is the best approach for long-term scalability, efficiency, and ease of operations.

<br />
