---
title: "A Quick Glance at the Kubernetes Gateway API"
slug: "kubernetes-gateway-api"
date: "2022-09-12T09:55:16+00:00"
lastmod: "2022-09-12T09:55:17+00:00"
description: "The idea behind the Gateway API is to have a clean separation between standard objects and the proprietary implementation."
canonical: "https://blog.frankel.ch/kubernetes-gateway-api/"
authors:
  - "nicolas-frankel"
image: "shinto-shrine-gates-gd37b765bc.jpg"
categories:
  - "DevOps"
  - "Kubernetes"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "apisix-api-gateway"
  - "back-to-basics-accessing-kubernetes-pods"
  - "dry-your-apache-apisix-config"
enlighterjs: true
frozen: false
---

In [one of my recent blog posts](https://blog.frankel.ch/basics-access-kubernetes-pods/), I described several ways to access Kubernetes pods. One can access a pod through its IP, but pods are naturally transient. The nominal way is to configure a `Service`: its IP is stable, and Kubernetes' job is to keep the mapping between a `Service` and its underlying pods up-to-date.

Different kinds of services are available: internal only, `NodePort` to finally allow access from outside the cluster, and `LoadBalancer` that relies on a third-party component - in general, a cloud provider one.

Finally, I mentioned the `Ingress` object, which also allows routing.

I deliberately left out the new kid on the block, the Gateway API. It's the subject of this post.

From Ingress to the Gateway API {#h2-0-from-ingress-to-the-gateway-api}
-----------------------------------------------------------------------

External access to Kubernetes pods went through several evolutionary steps, *e.g.* , `Ingress` is the answer to the problem of the lack of routing in `LoadBalancer`.

The biggest issue of `Ingress` is its dependency on "proprietary" objects.

As a reminder, here's the snippet to create routing using Apache APISIX:

```yaml
apiVersion: apisix.apache.org/v2beta3            #1
kind: ApisixRoute                                #1
metadata:
  name: apisix-route
spec:
  http:
  - name: left
    match:
      paths:
      - "/left"
    backends:
      - serviceName: left
        servicePort: 80
  - name: right
    match:
      paths:
        - "/right"
    backends:
      - serviceName: right
        servicePort: 80
```


1. Proprietary objects

Proprietary objects are a problem when migrating. While migration from one provider to another is probably uncommon, it should be as seamless as possible. When using proprietary objects, you first need to map from the old object to the new ones.

Chances are that it's not a one-to-one mapping. Then, you need to translate the specification into the new model: it's likely to be a full-fledged project.

The idea behind the Gateway API is to have a clean separation between standard objects and the proprietary implementation.

The Gateway API {#h2-1-the-gateway-api}
---------------------------------------

> Gateway API is an open source project managed by the SIG-NETWORK community. It is a collection of resources that model service networking in Kubernetes. These resources - `GatewayClass`, `Gateway`, `HTTPRoute`, `TCPRoute`, `Service`, etc - aim to evolve Kubernetes service networking through expressive, extensible, and role-oriented interfaces that are implemented by many vendors and have broad industry support. -- <https://gateway-api.sigs.k8s.io>

The above definition also mentions an organizational concern: different roles should manage a different set of objects.

![Gateway API Model](https://gateway-api.sigs.k8s.io/images/api-model.png)

*Picture from gateway-api.sigs.k8s.io*

Indeed, the concerns of a cluster operator and a developer are pretty different. It's quite similar to the Java EE application servers of old, which offered a specification organized around roles: developers, deployers, and operators.

IMHO, the most significant difference is that the specification was focused mainly on the developer experience; the rest was up to the implementors. The Gateway API seems to care about all personas.

Configuring pod access via the Gateway API {#h2-2-configuring-pod-access-via-the-gateway-api}
---------------------------------------------------------------------------------------------

Let's replace the `Ingress` we previously configured with the Gateway API. Several steps are necessary.

### Install the new Gateway s {#h3-3-install-the-new-gateway-crds}

```bash
k apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v0.5.0/standard-install.yaml
```


### Install an implementation {#h3-4-install-an-implementation}

I'll be using Apache APISIX. Alternatively, the SIG website maintains [a list of implementations](https://gateway-api.sigs.k8s.io/implementations/).

```bash
helm install apisix apisix/apisix \
  --namespace ingress-apisix \
  --create-namespace \
  --devel \                                                                #1
  --set gateway.type=NodePort \                                            #2
  --set gateway.http.nodePort=30800 \                                      #2
  --set ingress-controller.enabled=true \                                  #2
  --set ingress-controller.config.kubernetes.enableApiGateway=true \       #3
  --set ingressPublishService="ingress-apisix/apisix-gateway"              #4
```


1. Without the `--devel` option, Helm installs the *latest* release, which *doesn't* work with the Gateway API
2. The Gateway needs to be accessible outside the cluster anyway
3. The magic happens here!
4. I'll get back to it later

Let's check that everything works:

```bash
k get all -n ingress-apisix
```


```
pod/apisix-5fc9b45c69-cf42m                      1/1     Running   0          14m         #1
pod/apisix-etcd-0                                1/1     Running   0          14m         #2
pod/apisix-etcd-1                                1/1     Running   0          14m         #2
pod/apisix-etcd-2                                1/1     Running   0          14m         #2
pod/apisix-ingress-controller-6f8bd94d9d-wkzfn   1/1     Running   0          14m         #3

NAME                                TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)
service/apisix-admin                ClusterIP   10.96.69.19     <none>        9180/TCP
service/apisix-etcd                 ClusterIP   10.96.226.79    <none>        2379/TCP,2380/TCP
service/apisix-etcd-headless        ClusterIP   None            <none>        2379/TCP,2380/TCP
service/apisix-gateway              NodePort    10.96.101.224   <none>        80:30800/TCP#4
service/apisix-ingress-controller   ClusterIP   10.96.141.230   <none>        80/TCP
```


1. Apache APISIX itself
2. Apache APISIX stores its configuration in `etcd`. The chart schedules three pods by default, a good practice to handle failures in distributed systems
3. Apache APISIX controller: a Kubernetes controller is a control loop that moves the existing state toward the desired state
4. Apache APISIX Gateway service: it's the `NodePort` `Service` that we installed via the Helm Chart. It's also the name that we referenced during the Helm Chart install - `ingressPublishService`

At this point, the infrastructure is ready.

### Declare the Gateway implementation {#h3-5-declare-the-gateway-implementation}

As I mentioned above, the API makes a clean separation between the specification and the implementation. However, we need to bind it somehow. It's the responsibility of the `GatewayClass` object:

```yaml
apiVersion: gateway.networking.k8s.io/v1alpha2          #1
kind: GatewayClass                                      #2
metadata:
  name: apisix-gateway-class                            #3
spec:
  controllerName: apisix.apache.org/gateway-controller  #4
```


1. We don't use the latest version on purpose, as Apache APISIX uses this version. Be aware that it will evolve in the (near) future
2. `GatewayClass` object
3. Name it however you want; however, we will use it later to reference the gateway class
4. The controller's name depends on the implementation. Here, we are using Apache APISIX's.

Note that the `GatewayClass` has a cluster-wide scope. This model allows us to declare different Gateway API implementations and use them in parallel inside the same cluster.

### Create the Gateway {#h3-6-create-the-gateway}

With Apache APISIX, it's pretty straightforward:

```yaml
apiVersion: gateway.networking.k8s.io/v1alpha2          #1
kind: Gateway                                           #2
metadata:
  name: apisix-gateway
spec:
  gatewayClassName: apisix-gateway-class                #3
  listeners:                                            #4
    - name: http
      protocol: HTTP
      port: 80
```


1. Same namespace as above
2. `Gateway` object
3. Reference the gateway class declared earlier
4. Allow some restrictions at this level so that the cluster operator can avoid unwanted usage

Warning: The Gateway API specifies the option to *dynamically* change the port on the operator side. At the time of this writing, Apache APISIX's port allocation is *static*.

The plan is to make it dynamic in the future. Please subscribe to this [GitHub issue](https://github.com/apache/apisix-ingress-controller/issues/610) to follow the progress.

### Routes, routes, routes everywhere {#h3-7-routes-routes-routes-everywhere}

Until now, everything was infrastructure; we can finally configure routing.

I want the same routing as in the previous post; a `/left` branch and a `right` one. I'll skip the latter for brevity's sake.

```
apiVersion: gateway.networking.k8s.io/v1alpha2          #1
kind: HTTPRoute                                         #2
metadata:
  name: left
spec:
  parentRefs:
    - name: apisix-gateway                              #3
  rules:
    - matches:                                          #4
      - path:                                           #4
          type: PathPrefix                              #4
          value: /left
      backendRefs:                                      #5
        - name: left                                    #5
          port: 80                                      #5
```


1. Same namespace as above
2. `HTTPRoute` object
3. Reference the `Gateway` created above
4. Rule matches. In our case, we match regarding a path prefix, but plenty of rules are available. You can match based on a query parameter, on a header, etc.
5. The "upstream" to forward to. We defined the `left` `Service` in the previous blog post.

### Checking it works {#h3-8-checking-it-works}

Now that we have configured our routes, we can check it works.

```bash
curl localhost:30800/left
```


When we installed the Helm chart, we told Apache APISIX to create a `NodePort` service on port `30800`. Hence, we can use the port to access the service outside the cluster.

```
left
```


Conclusion {#h2-9-conclusion}
-----------------------------

Many alternatives are available to access a pod from outside the cluster. The CNCF added most of them to improve on the previous one.

The Gateway API is the latest proposal in this regard. The specification is a work in progress and products are at different stages of implementation.

For this reason, it's too early to base your production on the API. However, you should probably follow the progress as it's bound to be a considerable improvement over the previous approaches.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/k8s-access).

**To go further:**

* [The Gateway API](https://gateway-api.sigs.k8s.io/)
* [Getting started with the Gateway API](https://gateway-api.sigs.k8s.io/guides/getting-started/)
* [Apache APISIX Ingress Controller](https://apisix.apache.org/docs/ingress-controller/design/)

*Originally published at [A Java Geek](https://blog.frankel.ch/kubernetes-gateway-api/) on September 4^th^, 2022*

*[CRD]: Custom Resource Definition
