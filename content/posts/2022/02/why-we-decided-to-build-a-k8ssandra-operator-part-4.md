---
title: "Why we decided to build a K8ssandra Operator - Part 4"
slug: "why-we-decided-to-build-a-k8ssandra-operator-part-4"
date: "2022-02-22T17:04:28+00:00"
lastmod: "2022-02-24T19:41:21+00:00"
description: "In the first, second, and third posts in this series, we’ve shared conversations with K8ssandra core team members on our journey to - by Jeff Carpenter"
canonical: "https://k8ssandra.io/blog/articles/why-we-decided-to-build-a-k8ssandra-operator-part-4/"
authors:
  - "jeff-carpenter"
image: "/images/posts/2022/02/why-we-decided-to-build-a-k8ssandra-operator-part-4/maxresdefault.jpg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Kubernetes"
  - "Microservices"
tags:
related_posts:
frozen: false
---

In the [first](https://k8ssandra.io/blog/other/why_k8ssandra_operator_part_1/), [second](https://k8ssandra.io/blog/articles/why-k8ssandra-operator-part-2/), and [third](https://k8ssandra.io/blog/articles/why-we-decided-to-build-a-k8ssandra-operator-part-3/) posts in this series, we've shared conversations with K8ssandra core team members on our journey to build a Kubernetes operator for K8ssandra. We've discussed the virtues of the Helm package manager versus Kubernetes operators for deploying and managing infrastructure in Kubernetes and some of our implementation choices for the operator.

In this final post of the series, we pick up from the previous post with a discussion of how we decided to structure our projects in GitHub, how we test the K8ssandra operator, and our hopes for how the operator will expand the K8ssandra developer community.

Implications of operators for project structure {#h-implications-of-operators-for-project-structure}
----------------------------------------------------------------------------------------------------

*Jeff Carpenter:* There are external projects that K8ssandra is managing, but don't have operators. If I look in GitHub, I see Reaper under The Last Pickle organization, but Reaper Operator under K8ssandra. Is this another case where Stargate isn't building an operator under its org, but we're building a Stargate operator under K8ssandra?

*John Sanda:* Yes, but note that while we have separate repositories for Reaper Operator, Medusa Operator, Stargate Operator, we do plan to consolidate those into the K8ssandra operator. We'll have multiple CRDs and multiple controllers. Because cass-operator is already used independently, it will continue to be independent and will be a dependency pulled into the K8ssandra operator.

*Jeff Carpenter:* You're saying there will be separate CRDs associated with Stargate, Reaper, and Medusa, but all managed by the K8ssandra operator. This makes me wonder, is there discussion in the Kubernetes operator world about monoliths versus microservices? Is there concern about building a monolithic cooperator?

*John Sanda:* Absolutely. It's not a microservice architecture per se, but it is highly decoupled and highly modular. Let's say we wanted to take the Stargate controller and run that in its own separate pod. We could do that without impacting the code of the Reaper or K8ssandra operator, or the cass-operator controllers, it would just be a matter of repackaging it. They are decoupled and modular in that regard. That's also driven by having distinct CRDs, because you'll typically have a separate controller per CRD, and those controllers, for the most part, act in isolation from one another.

How to test a Kubernetes operator {#h-how-to-test-a-kubernetes-operator}
------------------------------------------------------------------------

*Jeff Carpenter:* Are there any interesting considerations for testing an operator?

*John Sanda:* The multi-cluster testing is going to present some challenges in terms of resource requirements. We've done a lot to make sure we can do all our automation and continuous integration with GitHub Actions using the free tier runners in GitHub, but this is not going to be sufficient in terms of resources for multi-cluster.

*John Sanda:* We're using Kind clusters for running most of our tests. We've put together some automation, in the form of setup scripts that will deploy and configure multiple Kind clusters for testing multi-cluster, but that's just going to be too much for those free tier runners in GitHub. That presents some interesting challenges that we need to work through.

*John Sanda:* For the CassKop operator from Orange, they've used a tool called [Kuttl](https://kuttl.dev/), which does full integration tests with YAML files. There was some discussion of this recently on our Discord server, and I think that will be something for us to look at. Not everyone will be a Go programmer or be familiar with the Kubernetes APIs in order to write tests, but everyone using K8ssandra should know at least a little bit about YAML. That would be a really awesome way for people to contribute and add a lot of value to the project without having to have that deep, intimate knowledge. That's something I'd like to look into.

*Jeff Carpenter:* Is the idea is to describe the desired configuration as a YAML, and that's the spec for a test case?

*John Sanda:* Yes, and the verification would be an additional YAML manifest.

*Jeff Carpenter:* What about making specific Stargate API calls or CQL queries? Could it test those as well?

*John Sanda:* No, it's more along the lines of here's what I want to deploy, like verifying a StatefulSet was created correctly. There's certainly gonna be limitations because obviously, in our tests, we do make calls to Stargate and CQL queries and so forth. That's beyond the scope of what a tool like Kuttl can do, but it would cover certainly cover some use cases.

*Jeff Carpenter:* It sounds like this is more about setting up user-defined configurations, and the test passes once status gets to "Ready".

*John Sanda:* I think that would be a good example. Perhaps it would be a good candidate for user acceptance testing.

Automating operator testing {#h-automating-operator-testing}
------------------------------------------------------------

*Jeff Carpenter:* What amount of testing do you expect to automate? What will the K8ssandra CI/CD pipeline look like with the expected combination of Helm and the K8ssandra operator?

*John Sanda:* Yes, there is automation involved. In terms of local development, the other tool that's considered a counterpart to Helm is Kustomize. This is more of a declarative approach. It's bundled as part of KubeBuilder and Operator SDK. You're going to see Kustomize being used with the K8ssandra operator, and we already use it for testing scenarios. Applying this to the scenario of running unit tests locally, there's a two-step process: first I run the build command to rebuild my operator image, then I'll run another command that will use Kustomize to redeploy things. So while we can automate those steps, it's still not as fast of a turnaround in terms of "wall clock" time, because you're still having to rebuild an image.

*Jeff Carpenter:* Sure, that's a key difference between any case where you have a compiled language versus a scripted language.

Expanding the K8ssandra community {#h-expanding-the-k8ssandra-community}
------------------------------------------------------------------------

*Jeff Carpenter:* What does this push to build a K8ssandra operator mean for contributors outside of the core team?

*John Sanda:* Hopefully, this means that we see an increase in contributions, whether that's in issue activity, on the forums, or Discord. The evolution of the project is a maturation process. People will be looking to use K8ssandra to solve bigger, harder, more challenging problems. That will help to shape K8ssandra to be the solution to those problems.

{{< youtube Ok0VHiH2Px0 >}}

*John Sanda:* Does it mean you have to be fluent in writing Go in order to get involved? Do you have to have experience with writing operators? That's certainly helpful, but no, these things are not required. K8ssandra is still a big collection tying together various projects. There are many avenues for contributors to get involved. If nothing else, this opens the door for more contributions and hopefully bigger and better things for users and contributors.

*Jeff Carpenter:* I agree with you. On the one hand, you could make the argument that having to learn Go is an obstacle to contributing. On the other hand, I'm watching some of the help requests that come from our community, and I can attest it can be semi-inscrutable to figure out what is happening with Helm.

*Jeff Carpenter:* I also remember trying to make a change to see if I could modify the Helm templates to generate multiple Cassandra datacenters, and I thought I had the iterative looping down, but then struggled with the variable scope and pushing down the values that I needed. And that hour I spent was pretty enlightening.

*Jeff Carpenter:* I think that with Go, while you might have to spend some time spinning up on the language, that's probably something you should learn anyway for modern, cloud-native backend development. For people that need to customize the project, it's going to be a lot easier to do their own fork, which hopefully turns into a pull request back to the main project. It's going to be a lot easier for them to do that in Go.

*John Sanda:* I agree, and I think this is something that Jeff DiNoto brought up when we were trying to decide at what point we should commit to building an operator. For engineers and developers, this is going to resonate more. In terms of development and testing, the libraries and the frameworks you'll use for writing unit tests in Go code are the same ones that you can use in Kubernetes. Overall, this will make it easier for folks to get involved, and hopefully, submit PRs.

Summary {#h-summary}
--------------------

That's where our conversation ended, and it's a perfect place to wrap up this series. The K8ssandra team is working hard on implementing the K8ssandra operator for a 2.0 release, but the amount of Go code is still quite manageable to read and learn. This is a great time to get involved in the project, and we'd love to give you a hand with setting up and testing out the in your own environment. Please reach out in the #k8ssandra-dev channel in our [Discord](https://discord.gg/qP5tAt6Uwt) server and we'll help you get started!
Curious to learn more about (or play with) Cassandra itself? We recommend trying it on the [Astra DB](https://astra.dev/3BEiQ7m) for the fastest setup.
