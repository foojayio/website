---
title: "FinOps: Tim O'Brien at Walmart"
date: "2023-05-22T06:01:54+00:00"
lastmod: "2023-05-23T14:14:09+00:00"
description: "\"Java and the Java community are what brought me to this role, but my focus is now on Cloud cost, budget, and ideas for efficiency.\""
authors:
  - "geertjan-wielenga"
image: "1683981616677.jpeg"
categories:
  - "FinOps"
  - "Research"
related_posts:
  - "finops-john-stuart-at-azul"
frozen: false
---

## FinOps and Cloud Cost Management, what's it all about and how does it impact us as developers and others who are close to the code? In this series on Foojay.io, you're introduced to FinOps practitioners around the world, focused on how they have gradually found themselves, their technology, and their organization in the FinOps space.

{{< img src="1683981616677-510x510.jpeg" class="alignleft size-medium is-resized" width="271" height="271" >}}

[Tim O'Brien](https://www.linkedin.com/in/timobrien/) is Senior Director of Engineering, Cloud Cost Management at Walmart, with experience in all aspects of software development from project inception to developing scaleable production architectures for large-scale systems during critical, high-risk events such as Black Friday. He has helped many organizations ranging from small startups to Fortune 100 companies take a more strategic approach to adopting and evaluating technology and managing the risks associated with change.

## **Hi Tim! At Walmart, your focus is Cloud Cost Management. How did you end up in that role, what was your journey?**

*Over a decade ago, I began my journey with Walmart alongside David Blevins at Tomitribe. At that time, Ben Galbraith and Dion Almaer were spearheading the mobile and eCommerce efforts, and we were brought on board to tackle various build-related challenges. Given my prior experience, I was specifically tasked with assisting with Maven and Tomcat configuration.*

*As time passed, my responsibilities at Walmart grew. I was tasked with overseeing production deployments and revamping the deployment architecture. Working there was a fantastic experience because of the diverse group of people I met, and I quickly became addicted to the vibrant energy of the workplace. After a conversation with David, I decided to join Walmart full-time in 2015. Initially, I was responsible for eCommerce DevOps for the front-end before transitioning to Capacity Management and then Cloud Cost. It's been an exciting journey.*

*Java and my experience with the Java community and open source are what brought me to this role, but these days my focus is entirely on Cloud cost, budget, and ideas for efficiency.*

## **What is Cloud Cost Management primarily focused on and why does it interest you?**

*If you are not familiar with the practice you might just think I'm running CPU utilization reports and telling people to scale down. There are teams that do this, but Cloud Cost Management (or FinOps) is really a combination of architecture strategy and encouraging teams to optimize cost.*
> *"Cloud Cost Management (or FinOps) is really a combination of architecture strategy and encouraging teams to optimize cost."*
> --- Tim O'Brien, Walmart

*Cloud cost optimization presents some fascinating challenges, particularly when it comes to technology selection and architecture. This is especially true as systems begin to scale. Questions arise such as:*

* *Are you using the right database? Should you opt for NoSQL or RDBMS?*
* *Is it better to use a PaaS service from a Cloud provider, or run your own software in an IaaS solution with VMs you manage?*
* *How are your logs being managed?*
* *What's your risk posture for multi-region high-availability and disaster recovery?*

*One of the prerequisites here is being able to discuss architecture and identify areas for improvement.*

*There's some overlap with SRE and DevOps, but the biggest Cloud cost wins are often gained by collaborating with architects and helping them discover changes that would reduce cost by an order of magnitude. It's more about architecture and technology selection than it is about dialing systems up and down based on CPU.*

## What are some aspects of the role that have surprised you or areas you hadn't expected to encounter?

*It isn't about Cloud cost reports as much as it is about relationships. When you need to sit down with teams and suggest improvements to an architecture or changes that might not be in their roadmap. You are going to get a lot of pushback. The most valuable tool in controlling Cloud cost is building the right relationships with the teams and departments you support.*
> *"It isn't about Cloud cost reports as much as it is about relationships."*
> --- Tim O'Brien, Walmart

*To succeed in Cloud Cost Management, it's crucial to build strong partnerships with teams that manage databases and other technology SMEs. You need to be able to communicate effectively with hundreds of engineering teams, which is why this role feels more like Developer Relations than anything else. Most of my job now is making sure that the right teams are talking to each other about the right topics - instead of me running at an engineering team criticizing how they use Cassandra, it's "Have you all synced up with the Cassandra team to discuss savings opportunities?"*

*So, everything in this area is relationships, and this is something I feel like my participation in the Java community and open source for the last few decades really gave me an advantage because of the time I spent watching really capable community leaders like Matt Raible, David Blevins, Tim Berglund, Matthew McCullough, Raju Gandhi, and yourself, Geertjan. I haven't been active in developer relations or open source lately, but when I look over at the Java community it's clear that the energy is still there. When I have to stand up in front of 10,000 of my colleagues to get them excited about cloud efficiency I'm trying to channel that same energy.*

## Is there overlap/synergy between what you do and the new FinOps movement? How do you see FinOps playing itself out?

*I'm active with the FinOps Foundation, and I see it as a chance for everyone to come together and define standards across different cloud providers for reporting on both cost and utilization. The energy of the organization reminds me of the energy in Apache back in 2001.*

*One project I'm starting to participate in is the FOCUS project which is the FinOps Open Cost \& Utilization Standard. We're in the specification drafting phase. What we're doing right now is getting together multiple times a week to discuss a standard field set for cloud costs. Once this is formalized I see some potential for the community to start establishing open specifications for measuring utilization and cost.*

## Does Java have a role to play in FinOps and Cloud Cost Management?

*Yes it does. I'm not giving away any secrets here, but I will say that Java is very much a part of some of the largest systems in the enterprise. When you are running something with 10,000 or 20,000 VMs during a high peak event it's so critical both from a resilience and a cost perspective that you have people that understand JVM internals.*

*When you use Cassandra and Kafka at a very large scale, and when you have a large multi-tiered architecture there are going to be areas where Tomcat is critical to performance. But, the one thing that continues to be true even after 20 years is that heap settings always tend to cause the longest email threads.*
> "*What I'd like to propose is that we develop more standards for how we assess workload utilization.*"
> --- Tim O'Brien, Walmart

*Back to my previous answer where I mentioned that the FinOps foundation is starting a common standard for cost and utilization data. As that starts to mature, what I'd also like to propose is that we develop more standards for how we assess workload utilization. When we're running workloads in the JVM there's a constant question of whether a workload is CPU-bound or Memory-bound? If it's a search index or a data-bound system like Cassandra, CPU isn't as important as IO or Memory. If it's a stateless application server running Tomcat, that's when CPU matters.*

## What trends do you see in this domain and hopes you have for the coming years?

*As a community, it would be helpful if different organizations could come together to exchange ideas about how utilization is measured for different applications. Right now I think a lot of organizations are creating their own standards to measure utilization, and I'd like to see more collaboration in this space.*
> *As a community, it would be helpful if different organizations could come together to exchange ideas about how utilization is measured for different applications.*
> --- Tim O'Brien, Walmart

*For big trends, what I predict going forward is that we're going to see more automation, more intelligent ways for systems to autoscale and adapt to changing traffic patterns. There are a few companies that provide automated ways to deploy applications, but I'd like to see more focus on automating scale up and scale down proactively. Right now, almost all the solutions just ask teams to set a threshold for scaling systems. I think there's more complexity that needs to be built in to predict spikes.*

*I also think that there needs to be more focus on unit cost and awareness from engineers. Not every engineer needs to be laser focused on cost, but incorporating an awareness of cloud cost into a daily or weekly process should be something that engineers have some visibility into.*
