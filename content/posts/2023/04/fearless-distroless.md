---
title: "Fearless Distroless"
slug: "fearless-distroless"
date: "2023-04-21T08:59:50+00:00"
lastmod: "2023-04-21T08:59:52+00:00"
description: "Distroless images are a solution to reduce your image size and improve its security, by providing neither a package manager nor a shell."
canonical: "https://blog.frankel.ch/fearless-distroless"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/04/blood-g7e6bb5b41.jpg"
categories:
  - "Developer Tools"
  - "DevOps"
tags:
related_posts:
enlighterjs: true
frozen: false
---

With the rise of Docker came a new focus for engineers: optimizing the build to reach the smallest image size possible.

A couple of options are available.

* **Multi-stage builds.** A `Dockerfile` can consist of multiple steps, each having a different Docker base image. Each step can copy files from any of the previous build steps. Only the last one will receive a tag; the others will be left untagged.This approach separates one or more build steps and a run step. On the JVM, it means that the first step includes compiling and packaging, based on a JDK, and the second step comprises running, based on a JRE.
* **Choosing the smallest base image size.** The smaller the base image, the smaller the resulting image.

In this post, I'm going to focus on the second point.

Minimal base images {#h2-0-minimal-base-images}
-----------------------------------------------

Three approaches are available for base images.  

Here they are:

* `FROM scratch`:  
  > You can use Docker's reserved, minimal image, `scratch`, as a starting point for building containers. Using the `scratch` "image" signals to the build process that you want the next command in the `Dockerfile` to be the first filesystem layer in your image.
  >
  > While `scratch` appears in Docker's repository on the hub, you can't pull it, run it, or tag any image with the name `scratch`. Instead, you can refer to it in your `Dockerfile`. For example, to create a minimal container using scratch:
  >
  > -- [Create a simple parent image using scratch](https://docs.docker.com/build/building/base-images/#creating-a-simple-parent-image-using-scratch)

  ```dockerfile
  FROM scratch
  COPY hello /
  CMD ["/hello"]
  ```

  `scratch` is the smallest possible parent image. It works well if the final image is independent of any system tool.
* Alpine: Alpine Linux is a tiny distribution based on `musl`, `BusyBox`, and `OpenRC`. It's designed to be secure *and* small. For example, the [3.17](https://hub.docker.com/layers/library/alpine/3.17/images/sha256-b6ca290b6b4cdcca5b3db3ffa338ee0285c11744b4a6abaa9627746ee3291d8d?context=explore) Docker image is only 3.22 MB.On the flip side, I already encountered issues because of Alpine's usage of `musl` instead of the more widespread `glibc`. Just last week, I heard about [Alpaquita Linux](https://bell-sw.com/alpaquita-linux/), which is meant to solve this exact issue. The [stream-glibc-230404](https://hub.docker.com/layers/bellsoft/alpaquita-linux-base/stream-glibc-230404/images/sha256-920b91fc763c4bfca017e67168945fc341480519aeb6fc8e71895a1aa0777087?context=explore) tag is 8.4 MB. It's twice bigger as Alpine but is still very respectable compared to regular Linux distros, *e.g.* , [Red Hat's](https://hub.docker.com/layers/redhat/ubi8/8.7-1112/images/sha256-4a6dbfbb845810dce5902ab80cb93ecb24c367460fff9d15438e0b3080e244b3?context=explore) 75.41 MB. \* Distroless:
* Last but far from least comes Distroless.

Since this post focuses on Distroless, I'll dive into it in a dedicated section.

Distroless {#h2-1-distroless}
-----------------------------

I first learned about Distroless because it was the default option in Google's [Jib](https://github.com/GoogleContainerTools/jib/blob/master/README.md). Jib is a Maven plugin to create Docker containers without dependency on Docker. Note that the default has changed now.

Distroless has its own GitHub project:
> "Distroless" images contain only your application and its runtime dependencies. They do not contain package managers, shells or any other programs you would expect to find in a standard Linux distribution.
>
> \[...\]
>
> Restricting what's in your runtime container to precisely what's necessary for your app is a best practice employed by Google and other tech giants that have used containers in production for many years. It improves the signal to noise of scanners (e.g. CVE) and reduces the burden of establishing provenance to just what you need.
>
> -- ["Distroless" Container Images](https://github.com/GoogleContainerTools/distroless)

The statement above hints at what Distroless is and why you should use it. Just like Serverless, Distroless is a misleading term. The most important fact is that Distroless provides neither a package manager nor a *shell*. For this reason, the size of a Distroless image is limited.

Also, Distroless images are considered more secure: the attack surface is reduced compared to other regular images, and they lack package managers and shells - common attack vectors. Note that some articles [dispute](https://www.redhat.com/en/blog/why-distroless-containers-arent-security-solution-you-think-they-are) [this benefit](https://bell-sw.com/blog/distroless-containers-for-security-and-size/).

Distroless images come with four standardized tags:

* `latest`
* `nonroot`: the image doesn't run as `root`, so it's more secure
* `debug`: the image contains a shell for debugging purposes
* `debug-nonroot`

Distroless debugging {#h2-2-distroless-debugging}
-------------------------------------------------

I love the idea of Distroless, but it has a big issue. Something happens during development and sometimes during production, and one needs to log into the container to understand the problem. In general, one uses `docker exec` or `kubect exec` to run a shell: it's then possible to run commands *interactively* from inside the running container. However, Distroless images don't offer a shell **by design**. Hence, one needs to run every command from outside; it could be a better developer experience.

During development, one can switch the base image to a `debug` one. Then, you rebuild and run again, and then solve the problem. Yet, you must remember to roll back to the non-`debug` base image. The more issues you encounter, the more chances you'll finally ship a `debug` image to production.

Worse, you cannot do the same trick in production at all.

Kubernetes to the rescue {#h2-3-kubernetes-to-the-rescue}
---------------------------------------------------------

At the latest JavaLand conference, I attended a talk by my good friend [Matthias Häussler](https://twitter.com/maeddes). In the talk, he made me aware of the `kubectl debug` command, introduced in Kubernetes 1.25:
> Ephemeral containers are useful for interactive troubleshooting when `kubectl exec` is insufficient because a container has crashed or a container image doesn't include debugging utilities, such as with distroless images.
>
> You can use the `kubectl` debug command to add ephemeral containers to a running Pod.
>
> -- [Debugging with an ephemeral debug container](https://kubernetes.io/docs/tasks/debug/debug-application/debug-running-pod/#ephemeral-container)

Let's see how it works by running a Distroless container:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">kubectl run node --image=gcr.io/distroless/nodejs18-debian11:latest --command -- /nodejs/bin/node -e "while(true) { console.log('hello') }"</pre>

The container starts an infinite NodeJS loop. We can check the logs with the expected results:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">kubectl logs node</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">hello
hello
hello
hello</pre>

Imagine that we need to check what is happening inside the container.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">kubectl exec -it node -- sh</pre>

Because the container has no shell, the following error happens:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">OCI runtime exec failed: exec failed: unable to start container process: exec: "sh": executable file not found in $PATH: unknown
command terminated with exit code 126</pre>

We can use use `kubectl debug` magic to achieve it anyway:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">kubectl debug -it \
              --image=bash \      #1
              --target=node \     #2
              node                #3</pre>

1. Image to attach. As we want a shell, we are using `bash`
2. Name of the container to attach to
3. For some reason I don't understand, we must repeat it

The result is precisely what we expect:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Targeting container "node". If you don't see processes from this container it may be because the container runtime doesn't support this feature.
Defaulting debug container name to debugger-tkkdf.
If you don't see a command prompt, try pressing enter.
bash-5.2#</pre>

We can now use the shell to type whatever command we want:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">ps</pre>

The result confirms that we "share" the same container:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">PID   USER     TIME  COMMAND
    1 root     12:18 /nodejs/bin/node -e while(true) { console.log('hello') }
   27 root      0:00 bash
   33 root      0:00 ps</pre>

After we finish the session, we can reattach it to the container by following the instructions:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">bash-5.2# Session ended, the ephemeral container will not be restarted but may be reattached using 'kubectl attach node -c debugger-tkkdf -i -t' if it is still running</pre>

Conclusion {#h2-4-conclusion}
-----------------------------

Distroless images are an exciting solution to reduce your image's size and improve its security. They achieve these advantages by providing neither a package manager nor a shell. The lack of a shell is a huge issue when one needs to debug what happens inside a container.

The new `kubectl debug` command offers a clean way to fix this issue by attaching an external container that shares the same context as the original one. Danke nochmal dafür, Matthias !

**To go further:**

* [Create a simple parent image using scratch](https://docs.docker.com/build/building/base-images/#creating-a-simple-parent-image-using-scratch)
* [Distroless Container Images](https://github.com/GoogleContainerTools/distroless)
* ["What's Inside Of a Distroless Container Image: Taking a Deeper Look"](https://iximiuz.com/en/posts/containers-distroless-images/)
* ["In Pursuit of Better Container Images: Alpine, Distroless, Apko, Chisel, DockerSlim, oh my!"](https://iximiuz.com/en/posts/containers-making-images-better/)
* [How To Debug Distroless And Slim Containers](https://iximiuz.com/en/posts/docker-debug-slim-containers/)
* [Why Distroless containers aren't the security solution you think they are](https://www.redhat.com/en/blog/why-distroless-containers-arent-security-solution-you-think-they-are)
* [Distroless containers for security and size?](https://bell-sw.com/blog/distroless-containers-for-security-and-size/)

*Originally published at [A Java Geek](https://blog.frankel.ch/fearless-distroless) on April 16^th^, 2023*
