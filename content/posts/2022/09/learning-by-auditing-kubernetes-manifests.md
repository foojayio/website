---
title: "Learning by Auditing Kubernetes Manifests | Foojay.io Today"
slug: "learning-by-auditing-kubernetes-manifests"
date: "2022-09-05T08:44:17+00:00"
lastmod: "2022-09-05T08:44:18+00:00"
description: "Find out about Checkov, which scans cloud infrastructure configurations to find misconfigurations before they're deployed."
canonical: "https://blog.frankel.ch/learning-auditing-kubernetes-manifests/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2022/07/20211006091437_large.jpg"
categories:
  - "DevOps"
  - "Research"
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Last year, I spoke at the [National DevOps Conference](https://www.devopsonline.co.uk/national-devops-conference/) that took place at the British Museum. I had already visited the museum before, but speaking there was a fantastic experience. Besides, we had the museum all for ourselves for a couple of hours. If you've ever visited the place, you know what I mean.

Anyway, I also attended a talk about [Checkov](https://www.checkov.io/):
> Checkov scans cloud infrastructure configurations to find misconfigurations before they're deployed.
>
> Checkov uses a common command line interface to manage and analyze infrastructure as code (IaC) scan results across platforms such as Terraform, CloudFormation, Kubernetes, Helm, ARM Templates and Serverless framework.

After the talk, I wanted to give it a try. Though I'm not a huge Cloud user, I'm using Kubernetes in a couple of my demos. I installed the CLI and launched it on this [folder](https://github.com/hazelcast-demos/zerodowntime/tree/master/infrastructure/kube), just for fun. Interestingly, I learned *a lot*.

Let's start with a quick summary:

```bash
checkov -d infrastructure/kube/ --quiet --compact | grep CKV | sort --unique
```

The command outputs all the rules that I broke.

I knew about some of them, even though I don't intend to fix them in my context. For example, requests and limits make sense in production, but I don't care about them for my demo.

Others are entirely new to me. I'm happy to share here my (or not).

Prefer digests to tags {#h2-0-prefer-digests-to-tags}
-----------------------------------------------------

* *Rule* : [CKV_K8S_43 - Ensure images are selected using a digest](https://docs.bridgecrew.io/docs/bc_k8s_39)
* *Severity*: Medium

I've been bitten by this one before. Even though I'll keep it as it is, it deserves an explanation. And the explanation is that **image tags are not immutable**.

Tags are just pointers to specific images. Publishers can point an existing tag to another image. In this case, builds are not idempotent, as the base image may change from one build to another.

For the demo, I'm building images on my local machine so that keeping the tag makes sense. I can update the dependencies' version, rebuild, and get the latest built image.

Explicitly disallow privilege escalation {#h2-1-explicitly-disallow-privilege-escalation}
-----------------------------------------------------------------------------------------

* *Rule* : [CKV_K8S_20 - Ensure containers do not run with AllowPrivilegeEscalation](https://docs.bridgecrew.io/docs/bc_k8s_19)
* *Severity*: Medium

This one came up as a bit of a surprise. To understand the issue, we need to read the relevant documentation:
> **AllowPrivilegeEscalation** - Gates whether or not a user is allowed to set the security context of a container to `allowPrivilegeEscalation=true`. This defaults to allowed so as to not break setuid binaries. Setting it to `false` ensures that no child process of a container can gain more privileges than its parent.
>
> -- [Pod Security Policies - Privilege Escalation](https://kubernetes.io/docs/concepts/policy/pod-security-policy/#privilege-escalation)

To summarize: **by default, a child process of a container can gain more privileges than its parent**.

It's a pretty serious issue. Note that the Pod Security Policy itself has been deprecated in v1.21 and will be removed in v1.25. Until that version, one should always explicitly set the attribute to `false`.

Set the security context {#h2-2-set-the-security-context}
---------------------------------------------------------

* *Rule* : [CKV_K8S_30 - Ensure securityContext is applied to pods and containers](https://docs.bridgecrew.io/docs/bc_k8s_28)
* *Severity*: Low

Though the severity is marked as low, I believe this rule is essential. Each pod or container can have a `securityContext` section in the manifest. The section has many different fields:

* `runAsNonRoot`, `runAsUser` and `runAsGroup`: see *Don't run as root* below
* `seccompProfile`: see *Set the seccomp profile* below
* `fsGroup` and `fsGroupChangePolicy`
* `seLinuxOptions`
* `supplementalGroups`
* `sysctls`
* `windowsOptions`

Some of them are described in more detail in the sections below, but a complete description goes beyond an introductory blog post. If you want to dive deeper, please check the [relevant documentation](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.19/#securitycontext-v1-core).

Alternatively, Snykt wrote a [great explanatory article](https://snyk.io/blog/10-kubernetes-security-context-settings-you-should-understand/) on the most important ones.

Don't run as root {#h2-3-don-t-run-as-root}
-------------------------------------------

* *Rule* : [CKV_K8S_23 - Minimize admission of root containers](https://docs.bridgecrew.io/docs/bc_k8s_22)
* *Severity*: Medium

I think this one is pretty well-known, but it's still worth repeating. A container is a Linux process that leverages kernel features:

* *Control groups* limit, account for, and isolate resource usage, *e.g.* CPU, memory, disk I/O, network, etc.
* *Namespaces* partition kernel resources such that one set of processes sees one set of resources while another set of processes sees a different set of resources

While both provide process isolation, it's not foolproof. If a container running as root is compromised, an attacker could use the extra permissions to attack further. To fix the issue, use the following snippet:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">apiVersion: v1
kind: Pod
metadata:
  name: &lt;name&gt;
spec:
    securityContext:
    runAsNonRoot: true
    runAsUser: &lt;user&gt;</pre>

Set a high UID user {#h2-4-set-a-high-uid-user}
-----------------------------------------------

* *Rule* : [CKV_K8S_40 - Ensure containers run with a high UID to avoid host conflict](https://docs.bridgecrew.io/docs/bc_k8s_37)
* *Severity*: Low

In the previous rule check, we didn't run as `root`, which is UID 1. However, even with other UIDs, we risk impersonating another user on the host system if the container is compromised. To drastically reduce the probability, configured users should only start from UID 10,000.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">apiVersion: v1
kind: Pod
metadata:
  name: &lt;name&gt;
spec:
    securityContext:
    runAsUser: &lt;+10,000&gt;</pre>

Set the seccomp profile {#h2-5-set-the-seccomp-profile}
-------------------------------------------------------

* *Rule* : [CKV_K8S_31 - Ensure seccomp profile is set to Docker/Default or Runtime/Default](https://docs.bridgecrew.io/docs/bc_k8s_30)
* *Severity*: Low

> Secure computing mode (`seccomp`) is a Linux kernel feature. You can use it to restrict the actions available within the container. The `seccomp()` system call operates on the seccomp state of the calling process. You can use this feature to restrict your application's access.
>
> The default `seccomp` profile provides a sane default for running containers with seccomp and disables around 44 system calls out of 300+. It is moderately protective while providing wide application compatibility.
>
> -- [Seccomp security profiles for Docker](https://docs.docker.com/engine/security/seccomp/)

Addressing the issue depends on the version of your Kubernetes cluster:

* For Kubernetes up to 1.18, annotations to the rescue: 

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">apiVersion: v1
kind: Pod
metadata:
  name: 
  annotations:
    seccomp.security.alpha.kubernetes.io/pod: "runtime/default"</pre>

* For Kubernetes 1.19+, the `securityContext` attribute features a `secompProfile`:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">apiVersion: v1
kind: Pod
metadata:
  name: 
spec:
  securityContext:
    seccompProfile:
      type: RuntimeDefault
  containers:
    ...</pre>

Conclusion {#h2-6-conclusion}
-----------------------------

If you want to enforce policies on `seccomp`, please check the documentation relative to [Pod Security Admission](https://kubernetes.io/docs/concepts/security/pod-security-admission/), or [Pod Security Policies](https://kubernetes.io/docs/concepts/policy/pod-security-policy/), depending on your Kubernetes version.

On a more general note, running an analysis/audit tool is a great way to get insight into a subject. I've done it on Kubernetes with Checkov, but the number of such tools is large enough to get an instant knowledge boost if you feel like it. Of course, it won't make you an instant subject matter expert, but it's a good step on the road.

**To go further:**

* [Checkov: Policy-as-code for everyone](https://www.checkov.io/)
* [Checkov: Kubernetes Policy Index](https://docs.bridgecrew.io/docs/kubernetes-policy-index)
* [Hardening Docker and Kubernetes with seccomp](https://martinheinz.dev/blog/41)
* [10 Kubernetes Security Context settings you should understand](https://snyk.io/blog/10-kubernetes-security-context-settings-you-should-understand/)
* [Pod Security Admission](https://kubernetes.io/docs/concepts/security/pod-security-admission/)

*Initially published at [A Java Geek](https://blog.frankel.ch/learning-auditing-kubernetes-manifests/) on July 4^th^, 2022*

*[TIL]: Things I Learned
