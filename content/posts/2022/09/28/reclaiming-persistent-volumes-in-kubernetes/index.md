---
title: "Reclaiming Persistent Volumes in Kubernetes"
slug: "reclaiming-persistent-volumes-in-kubernetes"
date: "2022-09-28T09:55:19+00:00"
lastmod: "2022-09-28T09:55:21+00:00"
description: "Learn how to manually reassign a PersistentVolume from one StatefulSet to another. Also learn about the STS, PV, and PVC API resources."
canonical: "https://medium.com/building-the-open-data-stack/reclaiming-persistent-volumes-in-kubernetes-5e035ba8c770"
authors:
  - "frank-rosner"
image: "1_G13CLQVHRE1sD6-vPcijCA.jpeg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Kubernetes"
  - "Microservices"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "kubernetes-gateway-api"
  - "apisix-api-gateway"
  - "aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2"
frozen: false
---

[Kubernetes](https://kubernetes.io/) is a widely used open-source container management platform for running stateless, containerized applications at scale. In recent years, Kubernetes has been extended to also support stateful workloads, including databases and key-value stores.{#43de}

There are three important API resources when it comes to managing stateful applications in Kubernetes:{#8e8a}

* [StatefulSet](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/) (STS)
* [PersistentVolume](https://kubernetes.io/docs/concepts/storage/persistent-volumes/) (PV)
* [PersistentVolumeClaim](https://kubernetes.io/docs/concepts/storage/storage-classes/) (PVC)

STSs schedule stateful pods, which can claim PVs through PVCs and mount them as volumes. Once a PV is claimed by an STS replica, Kubernetes will make sure that the volume stays with the replica, even if the pod gets rescheduled.{#e855}

This mechanism, however, relies on certain naming conventions involving the STS name, the PVC template name, as well as the STS replica index. If you want to rename your STS for whatever reason, Kubernetes won't be able to reassign your existing PVs to the new pods created by the new STS.{#2c5a}

In this article, we'll show you how to manually reassign a PV from one STS to another. We'll also explain the STS, PV, and PVC API resources in greater detail, and present step-by-step instructions for the reassignment of PVs, including the `kubectl` commands for each step. We'll use [Azure Kubernetes Service](https://azure.microsoft.com/en-gb/services/kubernetes-service/) (AKS) for the given examples, but they're easily transferable to other cloud providers.{#8d2f}

## Persistent Volumes

In Kubernetes, pods can request resources such as CPU, memory, and (persistent) storage. While CPU and memory are provided by nodes, storage can be provided by PVs. Just like nodes, PVs have a life cycle that's independent of the pods that use them. The PV resource captures all the details of the storage implementation. Examples are NFS, Azure File, AWS, and EBS.{#b356}

PVs can either be provisioned statically, by an administrator, or dynamically. Dynamic PV provisioning kicks in if no static PV exists that matches the specifications from a given PVC. To enable dynamic provisioning, the storage request needs to specify a supported storage class.{#e739}

AKS initially creates [four storage classes](https://docs.microsoft.com/en-us/azure/aks/concepts-storage#storage-classes) for clusters using the [in-tree storage plugins](https://github.com/kubernetes/community/blob/master/sig-storage/volume-plugin-faq.md#in-tree-vs-out-of-tree-volume-plugins):{#2d67}

* Standard Managed Disk (default)
* Premium Managed Disk (managed-premium)
* Standard Azure File Share (azurefile)
* Premium Azure File Share (azurefile-premium)

## Persistent Volume Claims

PVCs consume storage resources, just like pods consume CPU and memory resources. A PVC resource specification has different fields, such as access modes, volume size, and storage classes.{#bdbc}

Once a PVC is created, Kubernetes attempts to bind any existing, unassigned, statically created PV that matches the given criteria. If it does not succeed, it attempts to dynamically create a PV based on the specified storage class. To disable dynamic provisioning, `storageClassName` must be set to an empty string.{#d49d}

A PVC can only be bound to a single PV, and a PV can only be bound to a single PVC at the same time. The PV `spec.claimRef` field contains a reference to the PVC, while the PVC resource references the respective PV. PVCs remain unbound as long as no matching PV exists.{#6904}

Pods can use PVCs as volumes, effectively making the PV storage available to the containers inside the pod. In stateful applications, it's important that a given replica keeps the initially assigned PV even if pods get rescheduled. To ensure that, pods must be managed as stateful sets.{#955d}

## Stateful Sets

STSs are similar to `Deployments`, as they manage the deployment and scaling of pods. Although they provide additional guarantees about the ordering and uniqueness of these pods. Pods are created based on an identical specification, but the STS maintains a sticky identity for each of the replicas. The identity stays with the replica even if the pod gets scheduled on a different node.{#f27b}

The identity comprises the following:{#050c}

* **Ordinal index:** An STS with *N* replicas will assign an integer index from *0..N-1* to each replica.
* **Network ID:** Each pod derives its stable hostname using the following pattern: `$STS_NAME-$REPLICA_INDEX`. You can use a headless service to enable access to the pods, but we are not going to go into detail, because networking is beyond the scope of this post.
* **Storage:** Kubernetes creates PVCs for each replica (if they don't exist already) based on the specified VolumeClaimTemplate, which is part of the STS specification. The naming scheme for the PVC is `$PVC_TEMPLATE_NAME-$STS_NAME-$REPLICA_INDEX`.

STSs can be used instead of deployments if pods need to have stable network names, stable persistent storage, or ordered scaling/rollouts. Note that deleting/down-scaling an STS will not delete the PVCs that have been created. Instead, the user is required to clean those up manually. This is done to ensure data safety.{#51b6}

Now that we have all the necessary theory at hand, let's take a look at the required steps to rename an STS while keeping the previously assigned storage.{#1fcd}

For demonstration purposes, we will use an example STS deployed to an AKS cluster that consists of two replicas. Renaming a given STS while reassigning the given PVCs can be accomplished with the following steps:{#637c}

1. Retain PVs.
2. Derive new PVC manifests from existing PVCs.
3. Delete old STS.
4. Delete old PVCs.
5. Allow reclaiming of PVs.
6. Create new PVCs.
7. Create new STS.

The steps are illustrated in this video on [Reassigning a Persistent Volume in Kubernetes](https://www.youtube.com/watch?v=8CSTdrPsOu4). The next sections will go through the process step by step, providing the respective `kubectl` commands needed for each step.{#18b6}

{{< youtube 8CSTdrPsOu4 >}}

Before we start, let's define a few variables for our own convenience. We will need the following:{#0652}

* The name of our old STS.
* The name of the new STS, including the manifest file for the new STS.
* The PVC template name (which could change but we're keeping it the same).
* The old and new PVC names are based on the naming scheme described in the STS concepts section.
* The names of the PVs that are claimed by the existing PVCs.
* Filenames for the new PVC manifest files that will be generated from the existing ones.

Here is a code block you can copy and paste to define variables:{#f278}

```
OLD_STS_NAME="old-app"
NEW_STS_NAME="new-app"
NEW_STS_MANIFEST_FILE="new-app.yaml"
PVC_TEMPLATE_NAME="app-pvc"
OLD_PVC_NAME_0="$PVC_TEMPLATE_NAME-$OLD_STS_NAME-0"
OLD_PVC_NAME_1="$PVC_TEMPLATE_NAME-$OLD_STS_NAME-1"
NEW_PVC_NAME_0="$PVC_TEMPLATE_NAME-$NEW_STS_NAME-0"
NEW_PVC_NAME_1="$PVC_TEMPLATE_NAME-$NEW_STS_NAME-1"
PV_NAME_0=$(kubectl get pvc $OLD_PVC_NAME_0 \
  -o jsonpath="{.items[0].spec.volumeName}")
PV_NAME_1=$(kubectl get pvc $OLD_PVC_NAME_1 \
  -o jsonpath="{.items[0].spec.volumeName}")
NEW_PVC_MANIFEST_FILE_0="$NEW_PVC_NAME_0.yaml"
NEW_PVC_MANIFEST_FILE_1="$NEW_PVC_NAME_1.yaml"
```

Alternatively to specifying the resource names directly, you can also use label selectors and use the index in the JSON path `{.items[i]}` to access individual results.{#f4ea}

To avoid code duplication, we will use a short-hand pseudocode notation to indicate that commands should be repeated for each replica. E.g. `kubectl get pvc $OLD_PVC_NAME_i` needs to be expanded to `kubectl get pvc $OLD_PVC_NAME_0` and `kubectl get pvc $OLD_PVC_NAME_1`.{#cd09}

## Retain PVs

When a PVC bound to a PV gets deleted, the PV reclaim policy dictates what will happen to the PV. The default behavior is that PVs are deleted once their claim is released. We can prevent that by setting the reclaim policy to Retain. Here is the code to retain PVs:{#aaa2}

    kubectl patch pv $PV_NAME_i -p \
      '{"spec":{"persistentVolumeReclaimPolicy":"Retain"}}'

## Create new PVC manifests

Before we can delete the old PVCs, we will export their manifests and modify them to match the naming scheme of the new STS. We are going to use [jq](https://stedolan.github.io/jq/) in combination with `-o json` in this example, but you might also use [yq](https://github.com/mikefarah/yq) and `-o yaml`. Here is the code snippet to create a new PVC manifest.{#3076}

```yaml
kubectl get pvc $OLD_PVC_NAME_i -o json | jq "
  .metadata.name = \"$NEW_PVC_NAME_i\" 
  | with_entries(
      select([.key] | 
        inside([\"metadata\", \"spec\", \"apiVersion\", \"kind\"]))
    ) 
  | del(
      .metadata.annotations, .metadata.creationTimestamp,
      .metadata.finalizers, .metadata.resourceVersion,
      .metadata.selfLink, .metadata.uid
    )
  " > $NEW_PVC_MANIFEST_FILE_i
```

While exporting the JSON manifest, we clean it up a little because it contains internal status information that's not needed to create a new PVC. Specifically, we delete all keys except `metadata`, `spec`, `apiVersion`, and `kind`.{#dc38}

We also remove some additional metadata that was created by Kubernetes automatically. The resulting JSON manifest allows us to create new PVCs that will be bound to the correct PVs.{#aaf9}

```yaml
{
  "apiVersion": "v1",
  "kind": "PersistentVolumeClaim",
  "metadata": {
    "labels": {},
    "name": "$NEW_PVC_NAME_i"
  },
  "spec": {
    "accessModes": [
      "ReadWriteOnce"
    ],
    "resources": {
      "requests": {
        "storage": "100Gi"
      }
    },
    "storageClassName": "azurefile-premium",
    "volumeMode": "Filesystem",
    "volumeName": "pvc-aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
  }
}
```

## Delete old STS

Next, we delete the old STS. Note that this will terminate the pods in no guaranteed order. If you need graceful termination, please scale the STS to 0 before deleting it.{#fac9}

    kubectl delete sts $OLD_STS_NAME

## Delete old PVCs

Since the PVCs do not get deleted automatically, we delete them manually.{#8cb5}

    kubectl delete pvc $OLD_PVC_NAME_i

## Make PVs available again

When a PVC is deleted and the PV is supposed to be reclaimed, it needs to be made available first. This is accomplished by nulling the PV `claimRef` to make PVs available.{#b660}

    kubectl patch pv $PV_NAME_i -p '{"spec":{"claimRef": null}}'

## Create new PVCs

After making the PVs available again, we create the PVCs from the derived JSON manifests to create the PVC.{#8afb}

    kubectl apply -f $NEW_PVC_MANIFEST_FILE_i

## Create new STS

Finally, we can create the new STS.{#21f1}

    kubectl apply -f $NEW_STS_MANIFEST_FILE

Below is an example STS pseudo-YAML manifest. Note that you would have to replace the variables to make it valid YAML.{#f386}

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: $NEW_STS_NAME
spec:
  selector:
    matchLabels:
      app: $NEW_STS_NAME
  serviceName: "new-app"
  replicas: 2
  template:
    metadata:
      labels:
        app: $NEW_STS_NAME
    spec:
      terminationGracePeriodSeconds: 10
      containers:
      - name: nginx
        image: k8s.gcr.io/nginx-slim:0.8
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: $PVC_TEMPLATE_NAME
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:
  - metadata:
      name: $PVC_TEMPLATE_NAME
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 100Gi
      storageClassName: "azurefile-premium"
```

The new STS should now use the newly created PVCs and mount the data from the existing PVs.{#e1e5}

In this post, we saw how PVs, PVCs, and STSs can be used to manage stateful applications on Kubernetes. We also saw that by changing the reclaim policy of a PV it can be reclaimed manually, and creating PVCs that match the naming scheme of a new STS lets us mount existing PVs into the STS replicas.{#20f1}

This technique is not only useful when renaming an existing STS, but can also be used to mount existing cloud storage volumes to an application that is deployed to Kubernetes for the first time.{#6ddb}

Note that when using this method to rename an STS, there will be downtime. Zero-downtime migrations require more sophisticated methods, such as keeping the new and old STS active at the same time, streaming data, and potentially controlling live traffic through a higher level proxy. This requires application-specific knowledge though and is beyond the scope of this post.{#52e4}

*Explore more free tutorials on our* [*DataStax Developers YouTube channel*](https://www.youtube.com/c/DataStaxDevs/videos)*and* [*subscribe to our event alert*](https://docs.google.com/forms/d/e/1FAIpQLSfEtzzVauuFpFJWUiepYndqchBpNsaOwm6raPJDsMt9nTvMbw/viewform)*to get notified about new developer workshops. Follow* [*DataStax on Medium*](https://datastax.medium.com/)*for exclusive posts on all things Kubernetes, Cassandra, streaming, and much more.*{#24a9}

1. [YouTube: Reassigning a Persistent Volume in Kubernetes](https://www.youtube.com/watch?v=8CSTdrPsOu4)
2. [Azure Kubernetes Service](https://azure.microsoft.com/en-gb/services/kubernetes-service/)
3. [Concepts --- Storage in Azure Kubernetes Services (AKS)](https://docs.microsoft.com/en-us/azure/aks/concepts-storage#storage-classes)
4. [GitHub: jq --- a lightweight and flexible command-line JSON processor](https://stedolan.github.io/jq/)
5. [GitHub: yq --- a portable command-line YAML processor](https://github.com/mikefarah/yq)
6. [Using StatefulSets](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/)
7. [Persistent Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/)
8. [Storage Classes](https://kubernetes.io/docs/concepts/storage/storage-classes/)
