---
title: "Warm Up Fast, Run Lean: Vertical Scaling for Java on Kubernetes with Azul Prime and Kedify"
slug: "warm-up-fast-run-lean-vertical-scaling-for-java-on-kubernetes-with-azul-prime-and-kedify"
date: "2026-07-17T08:02:07+00:00"
description: "Autoscaling on Kubernetes has evolved significantly, but many production systems still rely on reactive scaling based on CPU and memory utilization. - by Jiří Holuša"
authors:
  - "jiri-holusa"
  - "zbynek-roubalik"
image: "Favicon-3-2.png"
categories:
  - "Cloud"
  - "Java"
  - "Kubernetes"
tags:
related_posts:
  - "azul-brings-java-from-edge-to-cloud"
  - "azuls-high-performance-java-platform-achieves-historic-first-with-10000-customer-jvms-collaborating-and-sharing-performance-optimizations-cutting-cloud-costs-by-20"
  - "fantastic-jvms-and-where-to-find-them"
  - "java-warmup-and-the-scaling-loop-problem"
enlighterjs: true
frozen: false
---

Autoscaling on Kubernetes has evolved significantly, but many production systems still rely on reactive scaling based on CPU and memory utilization. The issue is that resource metrics often lag behind real demand. By the time the CPU rises, users may already be experiencing unacceptable latency.

For most real services, horizontal scaling should be driven by proactive workload signals such as request rate, concurrency, or queue depth, rather than raw CPU alone. To learn more about this, see [this post](https://kedify.io/resources/blog/autoscaling-delay-resource-based-hpa-vs-proactive-metrics) on autoscaling delay and proactive metrics.

Vertical scaling is not a replacement for horizontal scaling. It is a complement. Horizontal scaling determines how many replicas you need. Vertical scaling determines how much CPU and memory each replica should have at different phases of its lifecycle.

In this post, we focus on a common vertical scaling challenge: **Java warm-up**.
![](prime-kedify-jvm-warm-up.avif)

Why JVM Warmup and Steady State Should Not Share the Same Resource Profile
--------------------------------------------------------------------------

One of the key concepts of a JVM, so called Just-In-Time (JIT) compilation, is the ability to continuously optimize the source code based on the actual data being processed and code paths being executed. The benefit of this approach, compared to static code compilation in programming languages like C/C++, is that it ultimately yields higher peak performance, or from the opposite angle, needs less resources to handle the same load.

Due to the JIT compilation, Java Virtual Machines, and thus Java applications, do not have a single stable resource footprint during the whole lifecycle of the application. In simple terms, the application starts "slow", then is continuously optimized by the JIT compiler until it reaches the "steady state" where the application is running in fully optimized machine code, showing the best possible throughput and latency. The part of getting to "steady state" is referred to as **warm-up** and the fundamental trade-off is that the JIT compiler consumes (one time, typically) extra CPU to perform the optimizations.

###### **Diagram #1: Relationship between application lifecycle, CPU spend by the JIT compiler, and performance**

![](https://azul.imgix.net/wp-content/uploads/Image-2-1-scaled.png?auto=format&crop=faces,entropy&fit=max&q=80&w=739&s=3894c42765e21a1cc44c4d8df8c979a5)

Warm-up happens every time the application is newly started. In the modern Kubernetes world, this is more than common -- horizontal autoscaling, spawning new instances and stopping the unnecessary ones dynamically based on load, has become the new standard of running things cost efficiently.

If you size a Kubernetes [pod](https://kubernetes.io/docs/concepts/workloads/pods/) only for steady state usage, warm-up takes longer. The service may begin receiving traffic while still cold (code is not fully optimized yet) and, even worse, the service is competing for CPU with the JIT compiler. Together, this often leads to unstable latency and poor early performance.

On the other hand, allocating enough CPU permanently to comfortably handle the warm-up phase wastes cluster capacity. It's common that the container utilizes almost all of the available CPU just in the first few minutes (during warm-up), but when steady state is reached, the traffic can be easily handled with, e.g., 50% of the available CPU. In summary, this leads to overprovisioning and in the end, waste of money.

The right model is simple and intuitive: **allocate more CPU during warm-up, then reduce the allocation once the JVM is fully optimized**.

###### **Diagram #2: Warm-up phase vs steady state**

![](https://azul.imgix.net/wp-content/uploads/Image-3-1-scaled.png?auto=format&crop=faces,entropy&fit=max&q=80&w=739&s=d624f67d80430309f25ce340c3f5151f)

Azul Zing, the JVM at the heart of Azul Prime, is the first JVM on the market to integrate directly with Kedify, making this kind of lifecycle-aware right-sizing seamless and trivial to use.
> **What is Azul Prime?**
>
> Azul Prime is a high-performance JVM platform and a drop-in replacement for OpenJDK HotSpot: same APIs, same TCK compliance, no application changes. Three orthogonal performance technologies set it apart:
>
> * LLVM-based Falcon JIT compiler produces more aggressively optimized machine code than HotSpot's C2;
> * ReadyNow addresses the warm-up problem by persisting JIT profile data between runs, so applications start optimized rather than learning from scratch each time.
> * C4 (Continuously Concurrent Compacting Collector) is a truly pauseless garbage collector that scales to multi-terabyte heaps without stop-the-world pauses; and
>
> Combined with the optional Optimizer Hub service (Cloud Native Compiler offloads JIT compilation to a dedicated service; ReadyNow Orchestrator manages profiles across an entire JVM fleet), Azul Prime targets two complementary goals: better, more predictable performance for latency-sensitive Java workloads (financial trading, e-commerce, real-time analytics, Kafka pipelines) and meaningful infrastructure savings on Java workloads more generally, since the same load runs on materially fewer cores.

PodResourceProfiles: Vertical Scaling Without Restarts
------------------------------------------------------

Kubernetes introduced [In Place Pod Resource Resize](https://kubernetes.io/blog/2023/05/12/in-place-pod-resize-alpha/) in v1.27, enabled by default since v1.33. This allows CPU and memory requests and limits to be updated for a running container without restarting the pod.

Kedify [PodResourceProfiles](https://docs.kedify.io/features/pod-resource-profiles/) (PRPs) build on top of this capability.
> **What is Kedify?**
>
> Kedify is an enterprise autoscaling platform built on KEDA, designed to make Kubernetes scaling predictable, efficient, and production-ready.
>
> Key capabilities include:
>
> * **Proactive and predictive autoscaling** based on real workload signals (HTTP/gRPC, queues, custom metrics), not lagging CPU
> * **Multi-cluster autoscaling and scheduling**, managing workloads across clusters from a single control plane
> * **Dynamic right-sizing**, combining real-time vertical autoscaling with lifecycle-aware resource adjustments
> * **Advanced workload support**, including HTTP scale-to-zero and GPU/LLM inference scaling
> * **Enterprise-ready control and visibility**, including multi-tenant KEDA management, FinOps insights, and seamless integration with existing observability stacks
>
> Built by the creators of KEDA, Kedify helps teams improve performance and reliability while reducing infrastructure costs by **30--40%** through smarter scaling.

A PodResourceProfile lets you define a future resource adjustment triggered by workload lifecycle events, such as when a container becomes ready.

This unlocks a lifecycle-aware vertical scaling pattern: allocate extra CPU only during warm-up, then automatically shrink back once the JVM is optimized. This is vertical autoscaling driven by workload lifecycle, not static requests or manual tuning.

Note: While warm-up resizing is a common use case, PRPs can also be triggered by other lifecycle or [demand signals](https://kedify.io/resources/blog/shrink-pods-when-traffic-sleeps), enabling vertical scale-up even when horizontal scaling is con=strained or replica count cannot increase. If we want to achieve fast vertical scaling based on the actual usage and not predefined triggers, we can leverage [PodResourceAutoscalers](https://kedify.io/resources/blog/pod-resource-autoscaler-fast-vertical-scaling) and even combine them with PRPs.

Measuring JVM Warm-up Using Azul Prime JMX Metrics
--------------------------------------------------

To resize resources correctly, we need a real signal that warm-up is complete.

Azul Prime exposes compilation activity through JMX ([docs](https://docs.azul.com/prime/ZingMXBeans_javadoc/com/azul/zing/management/CompilationMXBean.html#getTotalOutstandingCompiles())):

```
Bean: com.azul.zing:type=Compilation 
Attribute: TotalOutstandingCompiles
```


This metric represents the depth of the compilation queue, in other words how much work is left to be optimized. Early in startup it is high. As the JVM finishes optimizing, the compilation queue depth drops and keeps low.

We use this metric as a readiness gate.

#### **Code Snippet: JMX Based Warm-up Probe**

Below is the core logic of the readiness probe:

```
bean = "com.azul.zing:type=Compilation" 
attribute = "TotalOutstandingCompiles" 

value = conn.query([JMXQuery(bean, attribute=attribute)])[0].value 

if value < threshold: 
 conn.invoke_operation(bean, "finishWarm-up", []) 
 sys.exit(0) 
else: 
 sys.exit(2)
```


During warm-up, output looks like this:

```
786 
TotalOutstandingCompiles still above threshold: 786 >= 500
```


Only when the queue depth falls (and stays) below the threshold Kubernetes considers the pod ready. This avoids routing traffic to a JVM that is technically running but not yet optimized.

You can notice an additional small but important detail -- calling a method "finishWarm-up" through JMX. This is another part of the unique tight integration specific to Azul Zing JVM.

When a JVM is started (HotSpot included), it sizes its internal thread pools (e.g. for JIT compiler, garbage collector etc.) according to the available CPU and this sizing is done at startup of the JVM. But that's no longer suitable for this case of vertical (down)scaling. If we reduced the available CPU post warm-up, but kept the original amount of e.g. compiler threads, a sudden spike in JIT activity (commonly caused by e.g. workload type shift) could cause the JVM to consume an inadequate amount of CPU, again affecting the application latency.

By calling Zing's "finishWarm-up" method, Kedify is effectively signaling "let's scale down", making the JVM to properly adjust the internal thread counts.

#### **Demo Workload: Forcing Heavy Warm-up**

To demonstrate the effect clearly, we used the [Renaissance](https://renaissance.dev/) benchmarking suite from MIT, specifically the finagle-http benchmark.

This workload generates significant JIT activity by running many small HTTP requests against a Finagle server. It creates exactly the kind of warm-up pressure seen in real JVM services.

#### **Step 1: Start With Elevated CPU**

We deploy the workload with a high initial CPU allocation:

```
resources: 
 requests: 
 cpu: 10 
 limits: 
 cpu: 10
```


This ensures the JVM has enough compute to get through the compilations quickly.

#### **Step 2: Delay Readiness Until Warm-up Completes**

We add a readiness (or it could be a startup probe) that queries the compilation queue depth through JMX. While the queue is still above the threshold, readiness fails and Kubernetes will not route traffic to the pod.

Once compilation activity settles, readiness succeeds and the pod becomes eligible for traffic.

It is important to note that probes only control traffic and restart behavior. Startup probes can delay restarts, but PodResourceProfiles are what actually resize CPU requests and limits in place once warm-up is finished.

*Note: In the real world, the vast majority of JIT compilations is actually triggered once the load is being handled. So how can you finish warm-up before the readiness probe succeeds? First, the vertical scaling (and calling "finishWarm-up") is not tied to a readiness probe, it's like this for the purpose of the demo. Therefore, you can succeed in a readiness probe, handle traffic for some time with extended resources and scale down later. While this approach already provides value (getting through warm-up faster and having additional resources for the JIT compilation, minimizing the impact on application latency), it can be done even better.*

With Azul Zing JVM's ReadyNow technology ([docs](https://docs.azul.com/prime/Use-ReadyNow)), you can take this a step further. ReadyNow persists the compiler profile from previous runs of the application, so a freshly started JVM doesn't have to discover its hot code paths from scratch, it can pre-compile the methods it knows will be needed, before the first request even arrives. Combined with ReadyNow Orchestrator, which collects and distributes optimization profiles across an entire fleet of JVMs, you can realistically reach near-steady-state performance during the warm-up window itself. By the time the readiness probe succeeds and traffic arrives, the JVM is genuinely ready to handle it at full speed, no early latency tax.

#### **Step 3: Shrink CPU Automatically with PodResourceProfiles**

Once readiness succeeds, Kedify applies a PodResourceProfile:

```
apiVersion: keda.kedify.io/v1alpha1
kind: PodResourceProfile 
metadata: 
 name: heavy-workload 
spec: 
 target: 
 kind: deployment 
 name: heavy-workload 
 containerName: main 
 trigger: 
 after: containerReady 
 delay: 0s 
 newResources: 
 requests: 
 cpu: "4" 
 limits: 
 cpu: "4"
```


This transitions the pod from warm-up sizing to steady state sizing, without restart.

You can observe the change live:

```
kubectl get po -lapp=heavy-workload \ 
 -ojsonpath="{.items[*].spec.containers[?(.name=='main')].resources}" | jq
```


###### **Diagram #3: Resource transition timeline**

![](https://azul.imgix.net/wp-content/uploads/Image-4-2-scaled.png?auto=format&crop=faces,entropy&fit=max&q=80&w=739&s=07e91025e7e8553103fdb5a66db5fd4e)

Metrics: What This Achieves
---------------------------

PodResourceProfiles let you treat JVM warm-up as a short lifecycle phase that deserves temporary compute, and then automatically return to a lean steady state footprint.

With Azul Prime, compilation activity creates an early CPU burst. PRPs make that burst explicit:

* Extra CPU is available only while the JVM is still compiling
* Readiness stays failing until warm-up is truly complete
* Resources shrink immediately once the pod is safe to serve traffic

Warm-up duration is workload dependent, but PRPs ensure you do not pay for warm-up CPU forever. Once optimization finishes, allocation returns to the steady state footprint automatically.

The outcome is straightforward:

* Faster warm-up, so new replicas become useful sooner during rollouts or scale-out
* Better steady state efficiency, because CPU is not reserved permanently
* More reliable traffic gating, because pods only enter rotation once truly optimized

A typical transition looks like:

* Warm-up allocation: 10 CPU cores
* Steady state allocation: 4 CPU cores
* Capacity reclaimed after warm-up: \~60%

The exact numbers depend on workload behavior, but the operational pattern is consistent: fast warm-up, readiness-gated traffic, and lean steady state sizing.

How Vertical and Horizontal Scaling Work Together
-------------------------------------------------

PodResourceProfiles solve the per container sizing problem, while horizontal autoscaling solves the replica count problem. They work best together.

Horizontal scaling adds new replicas early based on proactive demand signals such as request rate or message queue depth. This ensures capacity is created before CPU saturation becomes visible.

Each new Java pod still needs to warm up. PodResourceProfiles apply vertical resizing to every newly created pod, giving it elevated CPU during JVM compilation and optimization. Once the pod is truly ready, resources are automatically reduced to the steady state footprint.

In practice, the flow looks like this:

1. Scale out horizontally based on workload metrics (requests per second, message queue depth).
2. Apply PRP vertical CPU boost during warm-up for each new pod.
3. Shrink resources after warm-up to avoid long lived overprovisioning .
4. Run the real workload efficiently across multiple optimized replicas.

![](https://azul.imgix.net/wp-content/uploads/Image-5-1-scaled.png?auto=format&crop=faces,entropy&fit=max&q=80&w=739&s=5202a8c01a0ddbd87a0da47a64ef6a14)

Try It Yourself
---------------

To experiment with this approach:

1. [Get Azul Prime](https://www.azul.com/prime-trial/).
2. Enable In Place Pod Resource Resize (default in Kubernetes v1.33+).
3. [Install](https://docs.kedify.io/installation) Kedify on your Kubernetes cluster.
4. Deploy a workload with a warm-up aware readiness or startup probe.
5. Apply a PodResourceProfile to shrink resources after warm-up.

This gives you a declarative way to treat warm-up as a first class lifecycle phase rather than a permanent sizing decision.

Closing Thoughts
----------------

Java warm-up is a distinct phase with distinct CPU requirements. Treating pod resources as static forces teams into a tradeoff between overprovisioning and degraded early performance.

With Azul Zing exposing JIT compilation progress, enabling reacting to changed resources and Kedify PodResourceProfiles enabling in place vertical scaling, Kubernetes can allocate extra CPU during warm-up and release it once the pod is truly ready. The result is faster startup, more stable latency, and better cluster efficiency in steady state.

If you run JVM services on Kubernetes, this is a pattern worth trying on a workload with noticeable warm-up behavior. Reach out to the Kedify team if you want help benchmarking or applying PodResourceProfiles in production.

**Warm up fast. Serve traffic only when ready. Run lean afterward.**
