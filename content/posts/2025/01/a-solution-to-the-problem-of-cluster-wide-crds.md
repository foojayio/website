---
title: "A solution to the problem of cluster-wide CRDs"
slug: "a-solution-to-the-problem-of-cluster-wide-crds"
date: "2025-01-30T11:55:14+00:00"
lastmod: "2025-01-30T11:55:15+00:00"
description: "The problem of some Kubernetes objects: they are cluster-wide and lock all teams working on the same cluster to use the same version."
canonical: "https://www.loft.sh/blog/solution-clusterwide-crds"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/12/idea.png"
categories:
  - "Cloud"
  - "Use Cases"
tags:
related_posts:
  - "2024-in-retrospective-nicolas-frankel"
  - "blockhound-how-it-works"
  - "chopping-monolith"
enlighterjs: true
frozen: false
---

**I'm an average Reddit user, scrolling much more than reading or interacting. Sometimes, however, a post rings a giant red bell. When I stumbled upon [If you could add one feature to K8s, what would it be?](https://www.reddit.com/r/kubernetes/comments/1ga0deo/comment/lta8itb/?context=3&amp;share_id=ZS15DmQexSXUjhXuqQ81z), I knew the content would be worth it.**

The most voted answer is:
> Namespace scoped CRDs

A short intro to CRDs {#h2-0-a-short-intro-to-crds}
---------------------------------------------------

Kubernetes comes packed with existing objects, such as `Pod`, `Service`, `DaemonSet`, etc., but you can create your own: the latter are called Custom Resource Definitions. Most of the time, s are paired with a custom controller called an *operator*. An operator subscribes to the lifecycle events of CRD(s). When you act upon a CRD by creating, updating, or deleting it, Kubernetes changes its status, and the operator gets notified. What it does depends on the nature of the CRD.

For example, the [Prometheus operator](https://prometheus-operator.dev/docs/getting-started/introduction/) subscribes to the lifecycles of a couple of different CRDs: `Prometheus`, `Alertmanager`, `ServiceMonitor`, etc., to make operating Prometheus easier. In particular, it will create a Prometheus instance when it detects a new `Prometheus` . It will configure the instance according to the CR's manifest.

The issue with cluster-wide CRDs {#h2-1-the-issue-with-cluster-wide-crds}
-------------------------------------------------------------------------

CRDs have a cluster-wide scope; that is, you install a CRD for an entire cluster. Note that while the definition is cluster-wide, the CR's scope is either `Cluster` or `Namespaced` depending on the CRD.

I noticed the problem of cluster-wide CRDs when I worked with Apache APISIX, an API gateway. Routing in Kubernetes has evolved across several steps: `NodePort`, `LoadBalancer`, and `IngressController`, each trying to fix the limitations of its predecessor. The latest step is the Gateway API.

At the time of this writing, the Gateway API is still an add-on and not part of the Kubernetes distro. You need to install it explicitly as a CRD. The Gateway API went through several versions. If team A were a precursor and installed version `v1alpha2`, every other team would need to use the same version because the Gateway API is a CRD. Of course, team B can try to convince team A to upgrade, but if you've been in such a situation, you know how painful it can be.

I mentioned above that the magic happened via an operator. The Gateway API doesn't come with an out-of-the-box operator. Instead, [different vendors provide their own](https://gateway-api.sigs.k8s.io/implementations/). For example, Apache APISIX has one, Traefik has one, etc. Of course, they are more or less advanced. At the time, the APISIX operator only worked with version 0.5.0 of the Gateway API CRD.

So now, it gets worse. Team A installed v0.5.0 to work with APISIX; team B comes later and wants to use Traefik, which fully supports the latest and greatest. Unfortunately, they can't because it would require the latest CRD.

Don't get me wrong; I'm all for a lean architectural landscape that limits the number of different technologies. However, it should be a deliberate choice, not a technical limitation. The above also prevents rolling upgrades. Imagine that we decided on Apache APISIX early on. Yet, it hasn't progressed toward supporting the latest Gateway API versions. We should be able to migrate from APISIX to Traefik (or any other) team by team.

The cluster-wide CRD doesn't allow it, or at least makes it very hard: we should find a Traefik that handles v0.5.0, **if there's one** and it's still maintained, migrate all APISIX CR to Traefik **at once**, and then proceed to upgrade. This approach requires expensive coordination, the cost of which grows exponentially with the number of teams involved.

The separate clusters approach {#h2-2-the-separate-clusters-approach}
---------------------------------------------------------------------

The obvious solution is to have one cluster per team. If you have been operating clusters, you know this approach doesn't scale.

Each cluster requires a primary node and a control plane. These are just "administrative" costs of running a cluster: they don't bring anything to the table.

On top of that, every cluster needs a complete monitoring solution. It includes at least metrics and logging, possibly distributed tracing. Whatever your architecture, it's again an additional burden with no business value. You can generalize the above over every support feature of a cluster, authentication, authorization, etc.

All in all, lots of clusters mean lots of additional operational costs.

vCluster, a sensible alternative {#h2-3-vcluster-a-sensible-alternative}
------------------------------------------------------------------------

The ideal situation, as the initial quote of this post states, would be to have namespace-scoped CRDs. Unfortunately, it's not the path that Kubernetes chose. The next best thing would be to add a virtual cluster on top of the real one to partition it: that's the promise of [vCluster](https://www.vcluster.com/).
> What are virtual clusters?
>
> Virtual clusters are a Kubernetes concept that enables isolated clusters to be run within a single physical Kubernetes cluster. Each cluster has its own API server, which makes them better isolated than namespaces and more affordable than separate Kubernetes clusters.

vCluster isolates each virtual cluster. Hence, with a **single control plane**, you can deploy a v1.0 CRD in one cluster and a v1.2 in another without trouble.

Imagine two teams working with different Gateway API providers, each requiring a different CRD version. Let's create a virtual cluster for each of them with vCluster so each can work independently from the other team. I'll assume you already have the `vcluster` CLI installed; if not, look at the [documentation](https://www.vcluster.com/docs/get-started/#deploy-vcluster), for we provide a couple of different installation options depending on your platform and your tastes.

We can now create our virtual clusters.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">vcluster create teamx</pre>

The output should be similar to the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">08:01:02 info Creating namespace vcluster-teamx
08:01:02 info Detected local kubernetes cluster orbstack. Will deploy vcluster with a NodePort &amp; sync real nodes
08:01:02 info Chart not embedded: "open chart/vcluster-0.21.1.tgz: file does not exist", pulling from helm repository.
08:01:02 info Create vcluster teamx...
08:01:02 info execute command: helm upgrade teamx https://charts.loft.sh/charts/vcluster-0.21.1.tgz --create-namespace --kubeconfig /var/folders/kb/g075x6tx36360yvwjrb1x6yr0000gn/T/83460322 --namespace vcluster-teamx --install --repository-config='' --values /var/folders/kb/g075x6tx36360yvwjrb1x6yr0000gn/T/1777816672
08:01:03 done Successfully created virtual cluster teamx in namespace vcluster-teamx
08:01:07 info Waiting for vcluster to come up...
08:01:32 done vCluster is up and running</pre>

Because we didn't specify any namespace, `vcluster` created one with the same name as the virtual cluster. If you prefer to set a specific namespace, use the `-n` option, .e.g._, `vcluster create mycluster -n mynamespace`.

Note that you can customize each virtual cluster via a `values.yaml` [configuration file](https://www.vcluster.com/docs/vcluster/configure/vcluster-yaml/). In the context of this post, we will keep the default options.

We use the `vcluster connect` command to connect to a virtual cluster. However, we are already connected because we used the `vcluster create` command.

At this point, it's as if we were in a separate Kubernetes cluster. Team X can install the CRDs using the version that they require.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.0.0/standard-install.yaml</pre>

The output is:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">customresourcedefinition.apiextensions.k8s.io/gatewayclasses.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/gateways.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/httproutes.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/referencegrants.gateway.networking.k8s.io created</pre>

Team Y can do the same with their version. Because we are both teams X and Y, we need to disconnect first from the virtual cluster.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">vcluster disconnect</pre>

You should see the result of the operation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">08:05:29 info Successfully disconnected and switched back to the original context: orbstack</pre>

Let's impersonate team Y, create the virtual cluster, and install another version of the CRDs:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">vcluster create teamy
kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.2.0/standard-install.yaml</pre>

The output of the second command is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">customresourcedefinition.apiextensions.k8s.io/gatewayclasses.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/gateways.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/grpcroutes.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/httproutes.gateway.networking.k8s.io created
customresourcedefinition.apiextensions.k8s.io/referencegrants.gateway.networking.k8s.io created</pre>

Version 1.2 has a new GRPC route that is not found in version 1.0. This way, team X can now install their Gateway API provider that works with v1.0 and team Y the one that works with 1.2.

CRDs are cluster-wide resources, but there's no conflict since the virtual clusters behave like isolated clusters. Each team can happily use the version they need without forcing others to use it.

![](/images/posts/2025/01/a-solution-to-the-problem-of-cluster-wide-crds/deployment-vcluster.png)

Conclusion {#h2-4-conclusion}
-----------------------------

In this post, we touched on the problem of some Kubernetes objects: they are cluster-wide and lock all teams working on the same cluster to use the same version. Running a Kubernetes cluster incurs costs; managing lots of them requires mature and organized automation.

vCluster allows an organization to get the best of both worlds: limit the number of clusters while preventing teams from stepping on each others' toes.

**To go further:**

* [Architecting Kubernetes clusters --- how many should you have?](https://learnk8s.io/how-many-clusters)
* [vCluster](https://www.vcluster.com/)

*[CRD]: Custom Resource Definition
