---
title: "How to Develop and Test an Automated CI/CD Workflow with Cassandra"
slug: "how-to-develop-and-test-an-automated-ci-cd-workflow-with-cassandra"
date: "2022-06-03T08:52:14+00:00"
lastmod: "2022-06-03T08:55:08+00:00"
description: "Learn how to get started today developing a CI/CD workflow using Apache Cassandra with a GitHub Actions runner!"
canonical: "https://medium.com/building-the-open-data-stack/how-to-deploy-apache-cassandra-in-an-automated-ci-cd-workflow-c68a25e67a07"
authors:
  - "jim-dickinson"
  - "patrick-mcfadin"
image: "/images/posts/2022/06/how-to-develop-and-test-an-automated-ci-cd-workflow-with-cassandra/1_-GhKztmv21IPfZZvAS7Ugg.jpeg"
categories:
  - "Apache Cassandra"
  - "DevOps"
  - "Testing"
tags:
related_posts:
  - "adelphi-apache-cassandra-testing-goes-cloud-native"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "reclaiming-persistent-volumes-in-kubernetes"
frozen: false
---

***In this post, we'll show you how to develop a CI/CD workflow using Apache Cassandra* ™*with a GitHub Actions runner. See for yourself how much time and effort you can save by deploying Cassandra cloud-natively while you test and deploy your cloud-native applications!***{#6067}

If you have projects that depend on [Apache Cassandra](https://cassandra.apache.org/_/index.html)™ and you want to develop an automated continuous integration and continuous delivery (CI/CD) flow, you're going to need to create Cassandra clusters dynamically for your tests to make sure that your app works after each code change. [DataStax](https://www.datastax.com/) does this every day --- we run Cassandra in [Kubernetes](https://kubernetes.io/) to power [Astra DB](https://astra.dev/38xbdFI). And, we use continuous testing of our Cassandra deployments to make sure Astra DB works reliably.{#8d10}

In this post, we'd like to show you how you can develop and test your own CI/CD workflows with Cassandra using a GitHub Actions runner.{#7fbd}

### Challenges that vex developers building CI/CD workflows {#56e9}

Say you want to support any number of workflows (within reason, of course) all running at the same time. But if you find your test environment is broken, migrated, scaled down to save costs, or you encounter any one of the many other common situations that developers have to work around, all of your great automation is totally wrecked.{#2e18}

A big "gotcha" when you're implementing continuous integration is that you need a real database for your app to talk to. Historically a DevOps team would provision a static test Cassandra environment on some cloud-based virtual machines (VMs), which was probably time-consuming and required more than a little bit of effort. This process doesn't scale so well if you're running feature branch environments or have multiple teams sharing a Kubernetes cluster. Now, you're probably deploying your apps in a more cloud-native way with containers, and it would be best to get your database deployed cloud-natively, too.{#116f}

You might think all you need is a container running Cassandra. However, it can be more challenging than it looks to get Cassandra going. If you're going to do all this with containers, it's better to take advantage of the best parts of a container orchestration system like Kubernetes. Then you can deploy your app AND database with close-to-production configuration, test it, and tear everything down at the end, reducing costs. No magical, came-from-the-DevOps-team dependencies or expensive test environment databases to maintain!{#c69f}

### Let's try it out without leaving GitHub!! {#72b8}

We've built out a[GitHub repo](https://github.com/DataStax-Examples/cassandra-cicd-example) to show you how you can configure and deploy your app and database and test it in an ephemeral way, leveraging a GitHub Actions runner and [Kubernetes-in-Docker (kind)](https://kind.sigs.k8s.io/docs/user/quick-start/).{#09fd}

GitHub Actions runner VMs come with two cores and 8 GB of RAM. This is plenty of space to install a three-node, Kubernetes-in-Docker (kind) cluster that mimics three distinct physical servers. That allows you to bring up a real RF=3 Cassandra cluster, your frontend pods, and your backend pods. Now, you don't need mock storage code, any special connections, or a VPN.{#62cd}

With this setup, you can bring Cassandra to where your app is running more easily than ever before. You can insert some fake data and then your tests can assert your family of apps behave correctly without the concern you would have with testing it in your live application.{#0de6}

This process also works with [self-hosted GitHub runners](https://docs.github.com/en/actions/hosting-your-own-runners/about-self-hosted-runners#self-hosted-runner-security-with-public-repositories), but you have to be a bit more careful here. If you're on a private repo and you have access to somewhere you can host a private runner (e.g. a private AWS account), that's fine. But, if what you want to provision it with is on private infrastructure, you need to make sure you're not using the public repo because anyone could open a pull request and start running code behind your firewall. You can learn more about the risks associated with mixing self-hosted runners with public repos in the GitHub documentation [here](https://docs.github.com/en/actions/hosting-your-own-runners/about-self-hosted-runners#self-hosted-runner-security-with-public-repositories).{#8bd4}

The public GitHub will provide plenty of RAM to support the learning experience and show you what you can do. And, if you're working on a private project and using GitHub, it isn't difficult to port your workflow to a self-hosted runner after having tested it in the public GitHub. So, let's just stick with that.{#62de}

### The Basic Steps {#f4ce}

Here's a brief rundown of the basic steps you can use for building and testing an automated CI/CD workflow in GitHub:{#b744}

1. Install a three-node [Kubernetes-in-Docker (kind)](https://kind.sigs.k8s.io/docs/user/quick-start/) environment to simulate a more full Kubernetes cluster. We'll use the kind-action by helm in the Actions Marketplace.
2. Deploy [cass-operator](https://docs.datastax.com/en/cass-operator/doc/cass-operator/cassOperatorAbout.html) and three-node Cassandra cluster.
3. Deploy frontend app and backend app.
4. Load up your data and run your tests.
5. Then fold the whole thing up when your test run is over. We get this for free by using helm's kind-action --- It's automatic!

The approach to developing and testing CI/CD workflows that we've described here is one that DataStax uses routinely to test its Astra DB workflows (though not necessarily on GitHub). In production, we run our CI flows with GitHub and [Jenkins.io](https://www.jenkins.io/), and [Harness.io](https://harness.io/).{#931e}

Check it out now on our [GitHub repo](https://github.com/DataStax-Examples/cassandra-cicd-example) to try it out today!{#4ac7}

*Follow the* [*DataStax Tech Blog*](https://datastax.medium.com/)*for more developer stories. Check out our* [*YouTube*](https://www.youtube.com/channel/UCqA6zOSMpQ55vvguq4Y0jAg)*channel for tutorials and here for DataStax Developers on* [*Twitter*](https://twitter.com/DataStaxDevs)*for the latest news about our developer community.*{#2141}

<br />

### Resources {#377c}

1. [Apache Cassandra](https://cassandra.apache.org/_/index.html)
2. [DataStax](https://www.datastax.com/)
3. [Kubernetes](https://kubernetes.io/)
4. [Astra DB](https://astra.dev/38xbdFI)
5. [GitHub Documentation: About Self-Hosted Runners](https://docs.github.com/en/actions/hosting-your-own-runners/about-self-hosted-runners#self-hosted-runner-security-with-public-repositories)
6. [GitHub Documentation: Self-Hosted Runner Security with Public Repositories](https://docs.github.com/en/actions/hosting-your-own-runners/about-self-hosted-runners#self-hosted-runner-security-with-public-repositories)
7. [Quick Start --- kind --- Kubernetes](https://kind.sigs.k8s.io/docs/user/quick-start/)
8. [DataStax Documentation: What is Cass Operator?](https://docs.datastax.com/en/cass-operator/doc/cass-operator/cassOperatorAbout.html)
9. [GitHub repo for the DataStax Cassandra CI/CD Example](https://github.com/DataStax-Examples/cassandra-cicd-example)
10. [Jenkins.io](https://www.jenkins.io/)
11. [Harness.io](https://harness.io/)
