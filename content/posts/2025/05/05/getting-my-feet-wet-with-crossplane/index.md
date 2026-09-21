---
title: "Getting my feet wet with Crossplane"
date: "2025-05-05T09:47:57+00:00"
lastmod: "2026-09-21T06:02:45+00:00"
description: "In the early days of IT, we manually configured servers–each one a precious snowflake, lovingly maintained and documented. But the size of the…"
canonical: "https://blog.frankel.ch/feet-wet-crossplane/"
authors:
  - "nicolas-frankel"
image: "cover.jpg"
categories:
  - "DevOps"
related_posts:
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "fearless-distroless"
  - "introduction-to-kubernetes-extensibility"
  - "running-your-database-on-openshift-and-codeready-containers"
frozen: false
---

In the early days of IT, we manually configured servers–each one a precious snowflake, lovingly maintained and documented. But the size of the infrastructure grew and this approach couldn't scale. Chef and Puppet popularized the idea of Infrastructure-as-Code: engineers would define the state of the machine(s) in text files, stored in Git–hence the name. A global node would read these files to create a registry. Then, a local agent on each machine would check the desired state at regular intervals, and reconcile the current state with the registry.

The first generation of managed the state of existing machines but assumed the machine was already there. The migration to the Cloud created another issue: how do you create the machine in the first place? Another IaC tool appeared in the form of Hashicorp's [Terraform](https://www.hashicorp.com/en/products/terraform). Terraform came with its own fully descriptive language, aptly named the Terraform language. However, it doesn't offer a central registry, and you need to run a command to reconcile the desired state with the current state. Terraform was a huge success. When Hashicorp moved away from a pure Open Source license, the community forked it and christened it OpenTofu. Furthermore, IBM recently acquired Hashicorp.

Terraform isn't without issue, though. Some feel that the descriptive configuration language is limiting. [Pulumi](https://www.pulumi.com/) offers to describe the infrastructure in a couple of existing programming languages, *e.g.*, Python, JavaScript, and Kotlin. Instead of repeating ten configuration lines with only a single parameter changed, you can write functions and loops.

Another issue was the lack of a central registry and automated drift correction. In the current technological landscape, which tool offers such features? Kubernetes! It makes a lot of sense to use Kubernetes to address Terraform's limits; that's the approach of [Crossplane](https://www.crossplane.io/) by Upbound.

I'm working on Kubernetes these days. Recently, I wrote [a series](https://blog.frankel.ch/pr-testing-kubernetes/) on how one could design a full-fledged testing pipeline targeting Google Kubernetes Engine. The second part mentions creating a instance in the context of a GitHub workflow. In this post, I want to assess Crossplane by creating such an instance.

It seems weird that to create a new Kubernetes cluster, one needs a Kubernetes cluster. I admit my use case is a bit weird, but I think that if I can achieve this edge case, I can achieve more nominal ones.

## Crossplane 101

Crossplane is like an engine, using Kubernetes registry and reconciling behavior to manage resources. Resources include virtually anything: cloud resources, GitHub projects, and organizations, Terraform (!), or software stacks, such as Kafka and Keycloak, etc. By default, it doesn't know about these resources, but you can extend its capabilities via *packages*. Packages are of two kinds:

* *Configuration* packages: a configuration package defines a higher-level abstraction over Kubernetes objects. You may have noticed that deploying an application in Kubernetes follows the same pattern: define a `Deployment` (or any other relevant object) and a `Service`. In most cases, you'd rather offer a single `Application` abstraction to developers. Crossplane allows you to compose objects to create abstractions and deliver them in configuration packages.
* *Providers* : a provider integrates with a third-party system, whether a cloud provider or any other system. For example, Crossplane offers a Google Cloud Platform provider. Crossplane offers three provider categories:
  * Official, developed, and supported by Upbound
  * Partner, developed by a third-party
  * Community, developed by the community in general

  Upbound offers providers for the main hyperscalers, while the community has created a couple for smaller ones, *e.g.*, Scaleway or OVH.

## First steps with Crossplane

The first step is to install Crossplane itself. I use a simple Helm Chart with the default configuration. I'm following the advice to install it in its dedicated namespace, `crossplane-system`.

```bash
helm repo add crossplane-stable https://charts.crossplane.io/stable
helm repo update
helm install crossplane --create-namespace --namespace crossplane-system crossplane-stable/crossplane
```

You should see two pods running in the `crossplane-system` namespace:

```bash
NAME                                       READY   STATUS    RESTARTS     AGE
crossplane-6f88554645-b2xng                1/1     Running   1 (1h ago)   1h
crossplane-rbac-manager-75bc66d6b7-8p2fh   1/1     Running   1 (1h ago)   1h
```

We are now ready to start the real work. We target , hence, we need to install the GCP provider. The marketplace offers many [available providers](https://marketplace.upbound.io/providers). The first challenge is to locate the one that contains the abstraction we want to create. Since we want to create a `Cluster`, we need the [provider-gcp-container](https://marketplace.upbound.io/providers/upbound/provider-gcp-container/v1.12.1).
> Cluster is the Schema for the Clusters API. Creates a Google Kubernetes Engine (GKE) cluster.
>
> -- [Cluster managed resource](https://marketplace.upbound.io/providers/upbound/provider-gcp-container/v1.12.1/resources/container.gcp.upbound.io/Cluster/v1beta2)

We create a `Provider` object that points to the provider package:

```yaml
apiVersion: pkg.crossplane.io/v1
kind: Provider
metadata:
  name: provider-gcp                                             #1
spec:
  package: xpkg.upbound.io/upbound/provider-gcp-container:v1     #2
```

1. Providers are cluster-wide
2. Upbound requires a paid subscription for specific versions. Because I don't have one, I can only use a major version, with all the stability issues that come with it.

We can list the installed providers:

```bash
kubectl get providers
```

The result should look like the following:

```
provider-gcp                  True        True      xpkg.upbound.io/upbound/provider-gcp-container:v1     28m
upbound-provider-family-gcp   True        True      xpkg.upbound.io/upbound/provider-family-gcp:v1.12.1   28m
```

t this stage, we can manage GKE instances with Crossplane.

Google offers several ways to authenticate. Here, I'll use the straightforward JSON credentials associated with a Service Account. Get the JSON from the Google Cloud console, then, import it as a secret in Kubernetes:

```bash
kubectl create secret generic gcp-creds -n crossplane-system --from-file=creds=./gcp-credentials.json
```

In the Crossplane model, a `Provider` object is generic and relevant to a single provider. On the other hand, a `ProviderConfig` is relevant to a project, including its credentials.

```yaml
apiVersion: gcp.upbound.io/v1beta1
kind: ProviderConfig
metadata:
  name: gcp-provider
spec:
  projectID: xplane-demo                                         #1
  credentials:
    source: Secret
    secretRef:                                                   #2
      namespace: crossplane-system
      name: gcp-creds
      key: credentials.json
```

1. The project ID we want to manage the resources of
2. Reference to the previously created secret containing the required credentials

The last step consists of creating the cluster. I tried to set the same configuration as in the command line of the original post, but I must admit I couldn't map every single option.

```yaml
apiVersion: container.gcp.upbound.io/v1beta1
kind: Cluster
metadata:
  name: minimal-cluster
spec:
  forProvider:
    initialNodeCount: 1                                          #1
    location: europe-west9                                       #1
    nodeLocations: [ "europe-west9-a" ]                          #1
    network: projects/xplane-demo/global/networks/default        #1
    subnetwork: projects/xplane-demo/regions/europe-west9/subnetworks/default #1
    ipAllocationPolicy:
      - clusterIpv4CidrBlock: "/17"                              #1
    resourceLabels:
      provisioner: crossplane                                    #2
  providerConfigRef:
    name: gcp-provider                                           #3
  writeConnectionSecretToRef:                                    #4
    namespace: default
    name: kubeconfig
```

1. Same parameters as in the command line
2. Set labels to document who the resource manager is. Depending on your context, you can add more, *e.g.* , `environment`
3. Name of the `ProviderConfig` we created above
4. Writes the `kubeconfig` to connect to the created GKE

You can follow the creation of the GKE cluster both on the Google Cloud console and via `kubectl`.

```bash
kubectl get cluster
```

We can see here that the cluster is not ready yet:

```
NAME              SYNCED   READY   EXTERNAL-NAME     AGE
minimal-cluster   True     False   minimal-cluster   2m46s
```

We can use the `kubeconfig` `Secret` that Crossplane created along with the `Cluster` to connect to the latter. First, let's dump the `Secret` value to a file:

```bash
kubectl get secret kubeconfig -o jsonpath="{.data.kubeconfig}" | base64 --decode > kube.config
```

At this point, we can use it to send requests to the newly-created `Cluster`:

```bash
kubectl --kubeconfig ./kube.config get pods
```

To delete the GKE instance, it's enough to delete the local `Cluster`. Kubernetes and Crossplane will take care of sending the `DELETE` request to Google Cloud.

```bash
kubectl delete cluster.container.gcp.upbound.io minimal-cluster
```

The command is synchronous: you won't get the prompt back until the GKE instance is removed.

In this post, I used Crossplane to create a simple GKE cluster. Using Kubernetes for the registry and the reconciling behavior is ingenious. Of course, Crossplane is as good as the number of integrations it offers. At the moment, it can already manage all major hyperscalers, plus a couple of smaller ones.

**To go further:**

* [Crossplane](https://www.crossplane.io/)
* [Crossplane documentation](https://docs.crossplane.io/v1.19/)
* [GCP Quickstart](https://docs.crossplane.io/latest/getting-started/provider-gcp/)
* [Crossplane provider-family-gcp](https://marketplace.upbound.io/providers/upbound/provider-family-gcp/v1.12.1)

*Originally published at [A Java Geek](https://blog.frankel.ch/feet-wet-crossplane/) on May 4th, 2025*

*[GKE]: Google Kubernetes Engine
*[GCP]: Google Cloud Platform
*[IaC]: Infrastructure-as-Code
