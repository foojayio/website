---
title: "Best Practices for Deploying MongoDB in Kubernetes"
date: "2025-09-16T19:01:34+00:00"
lastmod: "2025-09-16T19:01:35+00:00"
description: "Running MongoDB in Kubernetes introduces challenges that don’t exist with stateless applications. By default, Kubernetes doesn’t understand MongoDB’s replica set topology, quorum requirements, or data durability constraints. The MongoDB Kubernetes Operator bridges that gap, enabling you to declaratively deploy and manage replica sets and sharded clusters using familiar Kubernetes patterns."
authors:
  - "tim-kelly"
image: "Kubernetes_logo_without_workmark.svg.png"
categories:
  - "Databases"
  - "Kubernetes"
  - "Mongo"
related_posts:
  - "mongodb-schemas-in-java"
  - "run-an-atlas-cluster-locally-in-minutes"
  - "queryable-encryption-with-spring-data-mongodb-how-to-query-encrypted-fields"
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
frozen: false
---

[Kubernetes](https://kubernetes.io/), also known as K8s, is an open-source system that simplifies the deployment, scaling, and management of containerized applications. You define your application's desired state, such as the number of instances ([pods](https://kubernetes.io/docs/concepts/workloads/pods/)) or how they communicate, and Kubernetes works continuously to ensure that state is met. It excels at running stateless workloads, where pods can be replaced at any time without impacting application state or user data. Think of web servers or REST APIs: If a pod crashes, Kubernetes simply spins up a replacement and everything continues as expected.

But not all applications are stateless. Databases like MongoDB must preserve their internal state (the data they store), even when containers restart, move, or fail. These workloads need persistent storage, predictable identities, and orchestration logic that understands their data dependencies. Kubernetes doesn't provide this out of the box for stateful systems. So how do you safely run something like a MongoDB replica set inside an environment designed for disposable pods?

That's where the [MongoDB Atlas Kubernetes Operator](https://www.mongodb.com/docs/atlas/atlas-operator/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) comes in. It bridges the gap between Kubernetes' general-purpose orchestration and MongoDB's data-oriented architecture. By installing the Operator in your Kubernetes cluster, you can declaratively manage MongoDB Atlas resources, including projects, clusters, and database users, using [Kubernetes Custom Resource Definitions (CRDs)](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/). The Operator continuously reconciles your desired state in Kubernetes with the actual state in Atlas, giving you a single control plane to manage your data infrastructure.

If you're new to the Atlas Kubernetes Operator, check out our [quick start guide](https://www.mongodb.com/docs/atlas/operator/current/ak8so-quick-start/#std-label-ak8so-quick-start-ref?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) to learn how to deploy and configure it.

In this guide, we'll explore production-ready [best practices for deploying MongoDB with Kubernetes](https://www.mongodb.com/docs/kubernetes/current/tutorial/plan-k8s-op-considerations/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly).

## 1. Use the MongoDB Kubernetes Operator

Managing MongoDB manually in Kubernetes can quickly become complex. You'd need to configure [StatefulSets](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/), wire up persistent storage, manage services for each pod, and ensure the cluster maintains high availability through scaling events and upgrades. It's doable, but error-prone, and even small mistakes can lead to data loss or downtime.

The MongoDB Kubernetes Operator simplifies this by automating the entire lifecycle of a MongoDB deployment inside Kubernetes. Instead of writing YAML for every StatefulSet or managing each replica set member by hand, you define a single custom resource, a kind of Kubernetes-native config file, and the Operator takes care of the rest.

Behind the scenes, the operator provisions replica sets or sharded clusters, ensures storage is correctly attached to each pod, manages upgrades with minimal disruption, and continuously reconciles your configuration to match the actual state of the system. It even handles connection strings and secret management for you.

Here's a minimal example of what that might look like:

```
apiVersion: mongodb.com/v1
kind: MongoDB
metadata:
  name: orders-db
  namespace: mongodb

spec:
  members: 3
  version: 8.0.0
  service: orders-db-service
  persistent: true
```

In just a few lines of YAML, this tells the Operator to deploy a three-member MongoDB replica set, version 8.0.0, in the mongodb namespace, with persistence enabled and a dedicated Kubernetes Service for internal communication.

Once applied, the Operator creates the appropriate StatefulSet, provisions and binds persistent volumes, monitors the health of each member, and handles cluster-wide operations like rolling upgrades or scale-outs automatically.

To install the MongoDB Kubernetes Operator, you can use [Helm](https://helm.sh/) for a fast and maintainable setup. Helm charts are versioned, easy to upgrade, and integrate well with GitOps workflows. Alternatively, if you prefer tighter control over every component, you can apply the Operator's manifests directly using kubectl.

Before we go deeper into scaling and availability, we need to understand how MongoDB maintains identity and state inside a Kubernetes environment.

## 2. StatefulSets and persistent volumes: Running MongoDB the right way

MongoDB is a stateful database, and running it safely in Kubernetes means respecting both its need for stable identity and durable storage. Kubernetes offers two key building blocks to make that possible: StatefulSets and persistent volumes. When deploying MongoDB, they always go hand-in-hand.

A StatefulSet is a Kubernetes controller designed for applications that need fixed network identities. Instead of generating random pod names like a deployment does, it assigns predictable names such as mongodb-0, mongodb-1, and so on. Each pod also receives its own persistent volume claim (PVC), a long-lived disk that survives restarts and rescheduling. This is essential for MongoDB replica sets, which require each member to have a stable hostname and consistent access to its data directory.

Behind the scenes, the MongoDB Kubernetes Operator [creates these StatefulSets automatically](https://www.mongodb.com/docs/kubernetes/current/tutorial/plan-k8s-op-considerations/#ensure-proper-persistence-configuration?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly). Each replica set member gets its own pod with a fixed name and its own dedicated storage. That storage is mounted to standard paths like /data, and Kubernetes ensures the volumes remain bound to their respective pods, even if the pods are restarted or moved to a new node.

You can configure this persistence behavior in your CRD under spec.persistent, which should be set to true (this is the default). You can also choose between a single shared volume for all data and logs, or separate volumes to isolate I/O:

### Example: Recommended multiple volume configuration

```
spec:
  persistent: true
  sharedPodSpec:
    persistence:
      multiple:
        data:
          storage: "20Gi"
        logs:
          storage: "4Gi"
          storageClass: standard
```

This configuration allocates 20Gi for MongoDB's database files and 4Gi for logs, using the standard storage class. Having logs on a separate volume helps reduce I/O contention and can make debugging easier during performance issues.

For a full example of persistent volumes configuration, see [replica-set-persistent-volumes.yaml](https://github.com//mongodb/mongodb-kubernetes/blob/master/public/samples/mongodb/persistent-volumes/replica-set-persistent-volumes.yaml) in the [MongoDB Kubernetes persistent volumes samples](https://github.com//mongodb/mongodb-kubernetes/tree/master/public/samples/mongodb/persistent-volumes).

Together, StatefulSets and persistent volumes give MongoDB what it needs to run reliably in Kubernetes: stable identities, durable data, and predictable recovery. Understanding how these pieces work, even if the Operator is managing them for you, is key to operating MongoDB clusters confidently at scale.

## 3. Set CPU and memory resources for MongoDB and the Operator

Kubernetes is excellent at sharing cluster resources efficiently, but it needs clear instructions to do so. This is especially important when deploying MongoDB with the Kubernetes Operator, where both the database pods and the Operator itself must be resource-aware to avoid performance issues, evictions, or startup delays.

Let's break this down into two areas: resource configuration for the [Kubernetes Operator Pod](https://www.mongodb.com/docs/kubernetes/current/tutorial/plan-k8s-op-considerations/#set-cpu-and-memory-utilization-bounds-for-the-k8s-op-short-pod?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly), and for the [MongoDB replica set members](https://www.mongodb.com/docs/kubernetes/current/tutorial/plan-k8s-op-considerations/#set-cpu-and-memory-utilization-bounds-for-mongodb-pods?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) themselves.

### MongoDB Kubernetes Operator: Plan for initial spikes

The Kubernetes Operator performs many actions behind the scenes, including reconciliation, configuration validation, and cluster orchestration. During the initial deployment or when managing several MongoDB clusters, its CPU usage can spike significantly, especially if you're deploying multiple replica sets or sharded clusters in parallel. However, when the replica set deployment process completes, the CPU usage by the Kubernetes Operator reduces considerably.

By default, the number of reconciliation threads is controlled by the [MDB_MAX_CONCURRENT_RECONCILES](https://www.mongodb.com/docs/kubernetes/current/reference/kubectl-operator-settings/#std-label-mdb-max-concurrent-reconciles?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) environment variable. More concurrent threads = more parallel processing = higher CPU demand.

For production environments, especially if you're deploying up to 50 MongoDB clusters, you should explicitly set CPU and memory resource requests and limits for the Operator Pod. These values ensure Kubernetes reserves sufficient resources and prevents overcommitment:

```
resources:
  requests:
    cpu: 500m
    memory: 200Mi
  limits:
    cpu: 1100m
    memory: 1Gi
```

These settings should be applied inside your Operator Deployment manifest. If you're using Helm to deploy the Operator, you can configure the same values inside your values.yaml file.

**Example snippet from the Operator deployment:**

```
containers:
- name: mongodb-kubernetes-operator
  image: quay.io/mongodb/mongodb-kubernetes-operator:1.9.2
  resources:
    requests:
      cpu: 500m
      memory: 200Mi
    limits:
      cpu: 1100m
      memory: 1Gi
```

This ensures the Operator has enough headroom to spin up large numbers of clusters while remaining responsive.

For a full example of CPU and memory utilization resources and limits for the Kubernetes Operator Pod that can satisfy parallel deployment of up to 50 MongoDB replica sets, see the [mongodb-kubernetes.yaml](https://github.com//mongodb/mongodb-kubernetes/blob/master/public/mongodb-kubernetes.yaml#L219-L235) file.

### MongoDB replica set pods: Allocate predictably

The Operator also allows you to define resource requests and limits for each MongoDB database pod it creates. This ensures that pods are scheduled on nodes with enough capacity and are protected from memory overuse or unexpected eviction.

For most production workloads, a good starting point per MongoDB pod is:

```
requests:
  cpu: "0.25"
  memory: 512Mi
limits:
  cpu: "0.25"
  memory: 512Mi
```

These values keep usage predictable and ensure WiredTiger (MongoDB's storage engine) has enough memory to operate efficiently. However, it is important to note that performing some operations, like index builds, large aggregations, or initial syncs, can cause temporary CPU spikes. In those cases, the pod may benefit from a higher CPU limit, such as:

```
limits:
  cpu: "1"
```

Raising the CPU limit allows MongoDB to accommodate short bursts of activity without being throttled, especially during intensive workloads. Requests can remain low to maintain efficient bin-packing, but limits should reflect peak usage patterns based on your deployment profile.

For environments with varying workloads, consider integrating the [Vertical Pod Autoscaler](https://kubernetes.io/docs/concepts/workloads/autoscaling/) (VPA). VPA can adjust pod CPU and memory resource requests over time based on actual usage, reducing the need for manual tuning while still maintaining guardrails around scheduling and stability.

**Example inside your MongoDB CRD:**

```
spec:
  members: 3
  version: 8.0.0
  service: my-service
  persistent: true
  podSpec:
    podTemplate:
      spec:
        containers:
        - name: mongodb-enterprise-database
          resources:
            requests:
              cpu: "0.25"
              memory: 512Mi
            limits:
              cpu: "0.25"
              memory: 512Mi
```

Again, if you use Helm to deploy resources, define these values in the [values.yaml file.](https://www.mongodb.com/docs/kubernetes/current/reference/helm-operator-settings/#std-label-k8s-op-resources-setting?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly)

For more examples, check out the the [replica-set-podspec.yaml](https://github.com//mongodb/mongodb-kubernetes/blob/master/public/samples/mongodb/podspec/replica-set-podspec.yaml#L38-L45) file in the [MongoDB Podspec samples](https://github.com//mongodb/mongodb-kubernetes/tree/master/public/samples/mongodb/podspec) directory. You'll find tailored configurations for replica sets, sharded clusters, and standalone MongoDB deployments.

By clearly defining resource requests and limits for both the Operator and MongoDB pods, you create a Kubernetes environment that is more predictable, resilient, and production-ready. Proper sizing helps avoid performance spikes, prevents out-of-memory errors, and ensures MongoDB clusters scale reliably as workloads grow.

## 4. Spread replica set members across failure domains

High availability isn't just about running multiple MongoDB pods. It's also about ensuring they aren't all vulnerable to the same point of failure. If every replica set member ends up scheduled on the same [node](https://kubernetes.io/docs/concepts/architecture/nodes/), or worse, in the same availability zone, a single failure could bring down your entire cluster.

To prevent this, Kubernetes gives you control over where pods are scheduled using node affinity and pod anti-affinity rules. These features let you guide the scheduler to spread MongoDB pods across nodes and zones, increasing fault tolerance significantly.

### Why this matters for MongoDB

MongoDB replica sets [rely on quorum](https://www.mongodb.com/docs/manual/core/replica-set-elections/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) to function. If you lose too many members due to a node or zone outage, your replica set may become read-only or fully unavailable. Even if data is safe on disk, your application will experience downtime. Kubernetes won't fix that automatically—you need to tell it how to schedule smarter.

### Use node affinity and pod anti-affinity

The goal is simple: Spread MongoDB pods across multiple nodes and, ideally, across multiple availability zones. Here's how you can define that in your MongoDB Custom Resource:

```
spec:
  podSpec:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: topology.kubernetes.io/zone
            operator: In
            values: ["zone-a", "zone-b"]
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchLabels:
            app.kubernetes.io/name: mongodb-enterprise-database
        topologyKey: "kubernetes.io/hostname"
```

* The \[nodeAffinity\](- [Node affinity](https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#node-affinity)) ensures pods only land on nodes within the allowed availability zones (zone-a, zone-b, etc.).
* The podAntiAffinity makes sure that no two MongoDB pods land on the same node, using hostname as the topology key.

This combination helps guarantee that a failure in one node or zone won't impact the majority of your replica set.

### Example with custom zone and node labels

In some environments, you may want to use custom labels (e.g., for internal node groups or simulated zones). Here's a sample configuration with more specific label matching:

```
spec:
  podSpec:
    podAntiAffinityTopologyKey: nodeId
    podAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: security
            operator: In
            values:
            - S1
        topologyKey: failure-domain.beta.kubernetes.io/zone
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: kubernetes.io/e2e-az-name
            operator: In
            values:
              - e2e-az1
              - e2e-az2
```

This instructs Kubernetes to only place MongoDB pods in nodes labeled with those specific zones, and to avoid overlapping placements on the same physical host.

You can find full examples in the replica-set-affinity.yaml file in the [MongoDB Affinity samples](https://github.com//mongodb/mongodb-kubernetes/tree/master/public/samples/mongodb/persistent-volumes) directory. The same directory includes configurations for sharded clusters and standalone instances too.

By default, Kubernetes will schedule pods wherever it finds room, which might be great for stateless web services, but is risky for databases. For MongoDB, you must be explicit about fault tolerance. Spreading your pods across zones and nodes ensures that a localized failure won't turn into a full cluster outage.

## 5. Increase reconciliation throughput with thread count configuration

The MongoDB Kubernetes Operator reconciles resources one at a time by default. For most small-scale deployments, this is sufficient. However, if you plan to deploy more than 10 MongoDB replica sets or sharded clusters in parallel, the Operator can become a bottleneck. In this case, you should consider increasing the number of concurrent reconciliation threads.

The Operator supports this through the [MDB_MAX_CONCURRENT_RECONCILES](https://www.mongodb.com/docs/kubernetes/current/reference/kubectl-operator-settings/#std-label-mdb-max-concurrent-reconciles?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) environment variable, or via the [operator.maxConcurrentReconciles](https://www.mongodb.com/docs/kubernetes/current/reference/helm-operator-settings/#std-label-mdb-max-concurrent-reconciles-helm?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly) field in the values.yaml when using Helm. This controls how many reconciliation processes the Operator can run in parallel.

Increasing the thread count of the Kubernetes Operator allows you to vertically scale your Kubernetes Operator deployment to hundreds of MongoDB resources running within your Kubernetes cluster and optimize CPU utilization.

### Example: Helm values.yaml configuration

```
operator:
  maxConcurrentReconciles: 20
```

Or, if deploying manually, you can set this environment variable in the Operator deployment:

```
env:
- name: MDB_MAX_CONCURRENT_RECONCILES
  value: "20"
```

This setting should be adjusted based on your operational needs and the available compute resources in your Kubernetes cluster. The more concurrent threads the Operator runs, the more CPU and memory it will require—and the more load it will place on the Kubernetes API server.

### Monitor API load and resource usage closely

Increasing the thread count has trade-offs. Higher concurrency can lead to increased throughput, but also increases the number of requests hitting the Kubernetes API server. If the API server or the Operator itself becomes overwhelmed, you may experience degraded performance or downtime during critical operations.

Proceed with caution when setting the thread count above 10. Monitor both the Operator and API server closely. You may need to adjust the Operator's CPU and memory requests and limits to ensure stability.

### Running multiple operators

As an alternative to increasing concurrency on a single Operator, you can deploy multiple instances of the MongoDB Kubernetes Operator. This can allow for greater horizontal scalability, but it requires strict separation of concerns. You must ensure that no two Operator instances watch the same resources.

Multiple Operators should only be used when:

* Resources are partitioned across namespaces or label selectors.
* Each Operator instance is scoped to a distinct subset of MongoDB resources.
* API server usage is closely monitored.

Running multiple Operators without proper isolation will introduce race conditions and conflict during reconciliation. It also increases load on the API server, compounding the risk of overload.

Importantly, scaling the API server is not a valid justification for deploying multiple Operators. If the API server is already under pressure, adding more Operators will worsen the problem, not solve it.

### Summary

* Use MDB_MAX_CONCURRENT_RECONCILES or Helm's operator.maxConcurrentReconciles to increase parallel reconciliation.
* The recommended maximum is 10 unless you've tested higher under production conditions.
* Monitor Operator resource usage and Kubernetes API load closely.
* Only run multiple Operators if they are scoped to different resources and managed carefully.

## Conclusion

Running MongoDB in Kubernetes introduces challenges that don't exist with stateless applications. By default, Kubernetes doesn't understand MongoDB's replica set topology, quorum requirements, or data durability constraints. The MongoDB Kubernetes Operator bridges that gap, enabling you to declaratively deploy and manage replica sets and sharded clusters using familiar Kubernetes patterns.

But the Operator alone isn't enough. A production-grade deployment requires deliberate configuration: StatefulSets for stable identity, persistent volumes for durable storage, resource requests and limits for predictable performance, affinity rules for high availability, and concurrency tuning for scalable control plane operations. Each of these decisions directly impacts the stability, resilience, and operational safety of your MongoDB workloads.

The practices outlined in this guide are designed to make those decisions predictable and repeatable. Whether you're deploying 10 replica sets or 100, the same principles apply: Isolate failures, control resource usage, define clear scheduling behavior, and treat reconciliation capacity as a first-class operational concern.

Kubernetes won't protect your stateful workloads unless you configure it to. The MongoDB Kubernetes Operator gives you the tools, but how you use them determines whether your deployment is resilient or fragile.

If you found this useful, check out [how to deploy Vector Search, Atlas Search, and Search Nodes with the Atlas Kubernetes Operator](https://www.mongodb.com/developer/products/atlas/vector-search-atlas-search-search-nodes-atlas-kubernetes-operator/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=kubernetes+best+practices&utm_term=tim.kelly).
