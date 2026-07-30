---
title: "Is your Java application ready for the next generation of server CPUs?"
slug: "is-your-java-application-ready-for-the-next-generation-of-server-cpus"
date: "2024-05-08T08:03:53+00:00"
lastmod: "2024-05-08T08:03:54+00:00"
description: "With every major cloud provider now offering Arm-based instances, it’s time to start looking at what you need to do to migrate your Java applications to Aarch64."
authors:
  - "michael-hall"
image: "/images/posts/2024/05/is-your-java-application-ready-for-the-next-generation-of-server-cpus/Favicon-3-2.png"
categories:
  - "Cloud"
  - "DevOps"
tags:
related_posts:
  - "java-11-javafx-11-on-raspberry-pi-with-armv6-processor"
  - "azul-enhances-readynow-to-solve-javas-warmup-problem-simplify-operations-and-optimize-cloud-costs"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "developer-productivity-masterclass-interview-with-leonid-blouvshtein"
frozen: false
---

**If you only know about Arm CPU architecture from working on smartphones or Raspberry Pi devices, it might come as a surprise to you that there are also high-performance, cloud-scale Arm CPUs already on the Java market.**

The share of cloud and server workloads running on Arm is increasing every year. Back in 2011, Arm released the AArch64 instruction set, the first 64-bit support on Arm CPUs. Since then, Arm's silicon partners have been producing a range of systems-on-chip (SoCs) that adopt Arm's high performance CPUs. These power a broad range of diverse devices and technologies, including [flagship smartphones](https://newsroom.arm.com/news/tcs23-mediatek-vivo "flagship smartphones"), [laptops](https://newsroom.arm.com/blog/windows-on-arm-laptop-momentum "laptops"), [industrial IoT](https://newsroom.arm.com/blog/high-end-aiot-markets), servers, and even [powerful super computers](https://newsroom.arm.com/news/arm-powered-fugaku-supercomputer-claims-1-spot-on-top500-third-time-in-a-row).

Amazon introduced their own Aarch64 chips ([Graviton](https://newsroom.arm.com/blog/aws-graviton4)) to AWS customers in 2018, and now both Microsoft ([Cobalt](https://newsroom.arm.com/news/microsoft-custom-silicon-on-arm)) and Google ([Axion](https://newsroom.arm.com/news/google-cloud-custom-silicon-on-arm)) are producing their own Arm CPUs for Azure and Google Cloud Platform (GCP) services respectively. The independent chip maker [Ampere Computing](https://amperecomputing.com/products/processors) is providing Aarch64 server CPUs to other leading technology companies, including Oracle, Tencent, ByteDance, Scaleway and Equinix.

With every major cloud provider now offering Arm-based instances, often at a better price to users, it's time to start looking at what you need to do to migrate your Java applications to Aarch64.

### It all "just works" on Java {#h3-0-it-all-just-works-on-java}

The good news is that your app is written in Java, so there's nothing you need to change in your code to make this migration. OpenJDK gained [support for Aarch64](https://openjdk.org/jeps/237) architecture on Linux in 2014, and [Windows](https://openjdk.org/jeps/388) and [Mac](https://openjdk.org/jeps/391) in 2020. But simply running isn't enough, you also want your application to run fast. Over the years, significant work has gone into optimizing the Java Virtual Machine (JVM) and Hotspot to use advanced instructions on Intel and AMD chips.

Now that same work is going into using [advanced instructions, such as Neon and SVE, on Arm-based chips](https://openjdk.org/jeps/315), so make sure that your code runs at full speed. Now that Aarch64 is a first-class architecture in OpenJDK, future work like the [new Vector API](https://openjdk.org/jeps/460) will fully support the latest Arm instructions right from the start.

You have your choice of Java distributions too, it's not just OpenJDK that has Aarch64 support. It was added to GraalVM 21, allowing you to build native Aarch64 images of your Java (or other GraalVM language) applications, including support for advanced SIMD (single instruction, multiple data) instructions to improve the performance of the resulting binary.

Amazon has released Coretto JVM with optimizations specific to their Graviton processors, and support for OpenJDK's LTS releases. The Liberica JDK from Bellsoft (who contributed much of the Hotspot optimizations for Neon and SVE) also supports Aarch64 for those LTS releases, and is the default for Paketo's Java Buildpacks on Arm. And both Azul and IBM have Aarch64 support in their Java Development Kits (JDKs) and out-of-process JIT servers (Falcon and JITServer).

### The software and tools you need to consider {#h3-1-the-software-and-tools-you-need-to-consider}

Of course, there's more to deploying a modern Java application than just the hardware and JDK, as there are a whole host of software and tools to consider. Fortunately, the work of migrating those have largely already been done for you. Debian and Ubuntu Linux both gained full Aarch64 support all the way back in 2013 on desktops and servers.

Suse/OpenSuse followed them in 2015, and then Red Hat Enterprise Linux (RHEL) in 2017. Amazon Linux gained support in 2018 with the release of their Graviton processors. And, as of 2023, Microsoft Windows (the full thing, not RT) has been fully ported to run on Arm-based hardware.

Most likely you're not deploying on bare metal anymore though. Instead, you're likely packaging your application in a container and deploying it to the cloud using some orchestration layer. The good news is you're covered there too. Docker already fully supports [building native or multi-arch containers](https://docs.docker.com/reference/cli/docker/buildx/build/#platform) and providing a runtime for Aarch64. Or you can use Buildah and Podman, which support building and running containers in the Open Containers Initiative (OCI) on Arm, as do the CRI-O and Containerd runtimes. If Linux Containers (LXC) are your preference, you'll be happy to know that it fully supports Aarch64 as well.

You can automate this with your favorite CI/CD tools as well. Github supports self-hosted Action runners on your own Aarch64 instances, with fully hosted runners coming to you soon. You can run all of Gitlab on your own Aarch64 currently or, like Github, use self-hosted runners with a hosted instance.

Circle CI's hosted Cloud offering fully supports Arm container images, or you can manage your own Circle CI Server on Arm (only in AWS at the moment). Travis CI currently offers two different options, one for any Armv8 or higher instances, and another specifically optimized for Graviton 2.

Once you have an Aarch64 container image for your app, you'll be ready to deploy it using Kubernetes. Every major cloud's managed Kubernetes service will let you deploy Aarch64 containers onto their Aarch64 instances. Amazon Elastic Kubernetes Service (EKS) will deploy them to Graviton instances, while the Kubernetes engines for Azure, Google and Oracle will deploy to Ampere-powered instances (Azure's Cobalt and Google's Axion CPUs won't be generally available until sometime in the future).

And if you're hosting your own deployment environment, you can use Red Hat's Openshift or SUSE's Rancher on Aarch64 now too.

### Get started now {#h3-2-get-started-now}

Ready to get started? Arm has gathered up all the best options for you under our [Works on Arm](https://www.arm.com/markets/computing-infrastructure/works-on-arm) page, including links to the free tiers and offerings supporting Arm64 from some of the major cloud provides.

We're also building out a database of software and tools that have Aarch64 support already in our [Ecosystem Dashboard](https://www.arm.com/developer-hub/ecosystem-dashboard/), which is open for public contribution so you can add your own software projects or dependencies to the list.

You can also get free access to virtualized hardware, such as a Raspberry Pi 4, Linux PC, or Android device, from the [Arm Virtual Hardware](https://avh.arm.com/) program.

Finally, visit the [Arm Developer Hub](https://www.arm.com/developer-hub) to join our community, find our learning path tutorials, browse our video library, and much more.

Join our [Developer Program](https://www.arm.com/developer-program) to stay up to date on the latest Arm development, invitations to join us at events, and even become an Arm Ambassador.
