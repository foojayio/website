---
title: "The subtle art of waiting"
date: "2025-04-21T11:59:34+00:00"
lastmod: "2026-09-21T06:02:57+00:00"
description: "Recently, while working on a workshop titled Testing Your Pull Request on Kubernetes with GKE, and GitHub Actions, I faced twice the same issue: service A…"
canonical: "https://blog.frankel.ch/subtle-art-waiting/"
authors:
  - "nicolas-frankel"
image: "cover-1024x683.jpg"
categories:
  - "Kubernetes"
  - "Research"
related_posts:
  - "the-right-feature-at-the-right-place"
  - "choosing-a-cache-1"
frozen: false
---

Recently, while working on a workshop titled [Testing Your Pull Request on Kubernetes with GKE, and GitHub Actions](https://loftlabs-experiments.github.io/workshop-test-pr-k8s/), I faced twice the same issue: service A needs service B, but service A starts faster than service B, and the system fails. In this post, I want to describe the context of these issues and how I solved them both with the same tool.

## Waiting in Kubernetes

It might sound strange to wait in Kubernetes. The self-healing nature of the Kubernetes platform is one of its biggest benefits. Let's consider two pods: a Python application and a PostgreSQL database.

The application starts very fast and eagerly tries to establish a connection to the database. Meanwhile, the database is initializing itself with the provided data; the connection fails. The pod ends up in the `Failed` state.

After a while, Kubernetes requests the application pod's state. Because it's failed, it terminates it and starts a new pod. At this point, two things can happen: the database pod isn't ready yet, and it's back to square one or it's ready, and the application finally connects.

{{< img src="concurrent-pod-initialization-476x510.png" class="aligncenter size-medium" width="476" height="510" >}}

To speed up the process, Kubernetes offers [startup probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-startup-probes):

```yaml
startupProbe:
  httpGet:
    path: /health
    port: 8080
  failureThreshold: 30
  periodSeconds: 10
```

With the above probe, Kubernetes waits for an initial ten seconds before requesting the pod's status. If it fails, it waits for another ten seconds. Rinse and repeat 30 times before it fails definitely.

You may have noticed the HTTP `/healthz` endpoint above. Kubernetes offers two exclusive [Probe](https://kubernetes.io/docs/reference/kubernetes-api/workload-resources/pod-v1/#Probe) configuration settings: `httpGet` or `exec`. The former is suitable for web applications, while the latter is for other applications. It implies we need to know which kind of container the pod contains and how to check its status, provided it can. I'm no PostgreSQL expert, so I searched for a status check command. The [Bitnami Helm Chart](https://github.com/bitnami/charts/blob/main/bitnami/postgresql/templates/primary/statefulset.yaml#L417-L427) looks like the following when applied:

```yaml
startupProbe:
  exec:
    command:
      - /bin/sh
      - -c
      - -e
      - exec pg_isready -U $PG_USER -h $PG_HOST -p $PG_PORT
```

Note that the above is a simplification, as it gladly ignores the database name and an SSL certificate.

The startup probe speeds things up compared to the default situation if you configure it properly. You can set a long initial delay, and then shorter increments. Yet, the more diverse the containers, the harder it gets to configure, as you need to be an expert in each of the underlying containers.

It would be beneficial to look for alternatives.

## Wait4x

Alternatives are tools whose focus is on waiting. A long time ago, I found the [wait-for](https://github.com/eficode/wait-for) script for this. The idea is straightforward:
> `./wait-for` is a script designed to synchronize services like docker containers. It is `sh` and `alpine` compatible.

Here's how to wait for an HTTP API:

```bash
sh -c './wait-for http://my.api/health -- echo "The api is up! Let's use it"'
```

It got the job done, but at the time, you had to copy the script and manually check for updates. I've checked, and the project now provides a regular container.

[wait4x](https://wait4x.dev/) plays the same role, but is available as a versioned container and provides more services to wait for: HTTP, DNS, databases, and message queues. That's my current choice.

Whatever tool you use, you can use it inside an init container:
> A Pod can have multiple containers running apps within it, but it can also have one or more init containers, which are run before the app containers are started.
>
> Init containers are regular containers, except:
>
> * Init containers always run to completion.
> * Each init container must complete successfully before the next one starts.

Imagine the following `Pod` that depends on a PostgreSQL `Deployment`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    type: app
    app: recommandations
spec:
  containers:
    - name: recommandations
      image: recommandations:latest
      envFrom:
        - configMapRef:
            name: postgres-config
```

The application is Python and starts quite fast. It attempts to connect to the PostgreSQL database. Unfortunately, the database hasn't finished initializing, so the connection fails, and Kubernetes restarts the pod.

We can fix it with an `initContainer` and a waiting container:

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    type: app
    app: recommandations
spec:
  initContainers:
    - name: wait-for-postgres
      image: atkrad/wait4x:3.1
      command:
        - wait4x
        - postgresql
        - postgres://$(DATABASE_URL)?sslmode=disable
      envFrom:
        - configMapRef:
            name: postgres-config
  containers:
    - name: recommandations
      image: recommandations:latest
      envFrom:
        - configMapRef:
            name: postgres-config
```

In the above setup, the `initContainer` doesn't stop until the database accepts connections. When it does, it terminates, and the `recommandations` container can start. Kubernetes doesn't need to terminate the `Pod` as in the previous setup! It entails fewer logs and potentially fewer alerts.

## When waiting becomes mandatory

The above is a slight improvement, but you can do without it. In other cases, waiting becomes mandatory. I experienced it recently when preparing for the workshop mentioned above. The scenario is the following:

* The pipeline applies a manifest on the Kubernetes side
* In the next step, it runs the test
* As the test starts before the application is read, it fails.

We must wait until the backend is ready before we test. Let's use `wait4x` to wait for the `Pod` to accept requests before we launch the tests:

```yaml
- name: Wait until the application has started
  uses: addnab/docker-run-action@v3                                       #1
  with:
    image: atkrad/wait4x:latest
    run: wait4x http ${{ env.BASE_URL }}/health --expect-status-code 200  #2
```

1. The GitHub Action allows running a container. I could have downloaded the Go binary instead.
2. Wait until the `/health` endpoint returns a `200` response code.

## Conclusion

Kubernetes startup probes are a great way to avoid unnecessary restarts when you start services that depend on each other. The alternative is an external waiting tool configured in an `initContainer`. `wait4x` is a tool that can be used in other contexts. It's now part of my toolbelt.

**To go further:**

* [wait4x](https://github.com/wait4x/wait4x#wait4x)
* [So you need to wait for some Kubernetes resources?](https://vadosware.io/post/so-you-need-to-wait-for-some-kubernetes-resources/)

*Originally published at [A Java Geek](https://blog.frankel.ch/subtle-art-waiting/) on April 20th, 2025*
