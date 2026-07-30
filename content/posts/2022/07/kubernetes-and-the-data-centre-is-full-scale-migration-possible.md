---
title: "Kubernetes and the Data Centre: Is Full Scale Migration Possible?"
slug: "kubernetes-and-the-data-centre-is-full-scale-migration-possible"
date: "2022-07-21T13:48:13+00:00"
lastmod: "2022-07-21T13:48:15+00:00"
description: "Using Kubernetes as a standard layer will help everyone to control how and where applications run and data gets created."
canonical: "https://www.capacitymedia.com/articles/3828659/kubernetes-and-the-data-centre-is-a-full-scale-migration-possible-"
authors:
  - "patrick-mcfadin"
image: "https://foojay.io/wp-content/uploads/2022/02/kubernetes-genric-169jpeg_51633.jpg"
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
  - "book-review-effortless-cloud-native-app-development-using-skaffold"
  - "building-reactive-java-applications-with-spring-framework"
frozen: false
---

![](/images/posts/2022/07/kubernetes-and-the-data-centre-is-full-scale-migration-possible/kubernetes-genric-169jpeg_51633.jpg)

Data centres used to be exactly that - the centres of our data worlds, where all information, files and records would be kept and controlled.

Today, that is no longer the case. More companies are moving to cloud services, whether this is dialling down on their traditional on-premises environments or never setting them up in the first place.

This is not to say that data centres will be wholly abandoned for cloud, but the mix will continue to evolve and be very different to today. Gartner [predicts](https://www.gartner.com/smarterwithgartner/the-everywhere-enterprise-a-gartner-qa-with-david-cappuccio/) that by 2025, 85% of infrastructure strategies will take a more hybrid approach, integrating on-premises, colocation, cloud and edge delivery options, compared with 20% in 2020.

This melange of different venues, platforms and approaches is all possible. However, that does not mean that it is currently easy or indeed efficient to work this way. It can end up with IT in silos with the much dreaded technical debt. Coupled with this, many CIOs admit that they fear getting locked into a single provider - [according](https://www.bain.com/insights/cios-say-they-want-to-avoid-vendor-lock-in-snap-chart/) to Bain and Co., around two thirds of CIOs use multiple cloud providers.

***Is Kubernetes the future for the whole data centre?***

Alongside these trends, developers have been using containerisation and microservices to build modern cloud-native applications. The thinking here is understandable - containers are built to be managed at scale, while microservices applications use lots of different components to assemble services that are then delivered to users. Taken together, this technology and application design approach should make IT more agile and better able to deliver what the business wants.

Containers can also run happily agnostic across multiple locations, so this would fit with the idea of running more hybrid and multi-cloud environments over time. Spawn the containers close to the user, and you get the benefit of on-demand compute and low latency. Clouds are left supplying the basic compute, network and storage.

Making it work in practice takes some work but it's getting much easier. Google's Kubernetes project for container orchestration has become the default standard for developers to manage this, but it has been strongest with application workloads alone. It was designed for stateless application images that could be turned on and off as needed, rather than stateful workloads like data that may have to exist for years. Kubernetes does not yet cover areas like databases as standard, so Operators are required to integrate in and provide that management layer.

Putting this all together is for the Kubernetes community as a whole - from cloud providers and data centre operators through to open source projects, vendors and customers. As Kubernetes - and the industry around this project - develops, it can provide that operating system for workloads to exist across multiple locations.

***If you love someone, set them free***

What will this mean in practice for data centre operators? Can Kubernetes provide that fully functioning way to migrate whole environments from one provider to another, scaling up server virtualisation for the cloud age? I think the answer is yes.

Companies of all sizes do care about cloud and lock-in. They always want to know that the exit route is just behind them and that they can use it whenever they want. However, that does not mean that they will automatically start looking at the exit if and when it is possible. Looking at the Bain research, companies tend to spend the majority of their budgets with a primary provider.

What Kubernetes can do is make workloads easier to move and run across multiple locations. What will change is the definition of a workload, from individual application containers to whole data centre environments, especially around commodity applications that rely on standardised compute or storage. Database implementations with Kubernetes Operators can similarly be moved between cloud or on-premises data centres - for some databases, this involves moving from one provider to another, while distributed databases like Apache Cassandra can take this further by operating across multiple cloud services or data centre locations from the start.

For the data centre industry as a whole, embracing more freedom and flexibility around workloads with Kubernetes should provide more opportunities to support customers in their long term strategies around cloud, data and IT.

Trying to be too controlling will, paradoxically, lead to less engagement and commitment from those that are not committed.

With so many moving pieces to think about, using Kubernetes as a standard layer will help everyone to control how and where applications run and data gets created, wherever those locations happen to be.
