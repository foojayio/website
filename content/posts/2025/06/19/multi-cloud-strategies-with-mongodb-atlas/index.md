---
title: "Multi-cloud Strategies With MongoDB Atlas"
date: "2025-06-19T11:49:29+00:00"
lastmod: "2025-06-19T11:50:11+00:00"
description: "In the technological world, the cloud has become more prevalent. It brings many benefits, including flexibility, scalability, faster innovation, and - by Luce Carter"
authors:
  - "luce-carter"
image: "mongologo.png"
categories:
  - "Databases"
  - "Developer Tools"
  - "Mongo"
related_posts:
  - "testing-mongodb-atlas-search-java-apps-using-testcontainers"
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
  - "java-concurrency-best-practices-for-mongodb"
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
frozen: false
---

**In the technological world, the cloud has become more prevalent. It brings many benefits, including flexibility, scalability, faster innovation, and collaboration. Plus, when it comes to data storage and access with databases, it allows data to be located closer to the user for lower latency and thus, faster performance.**

Most people have heard of the big cloud providers, such as Azure from Microsoft, Google Cloud, and Amazon Web Services (AWS). But when it comes to a business selecting a cloud provider, how do you choose?

This can come down to a few factors, including partnerships with the provider, services available, where the services are available (cloud providers have data centers across the globe called regions), and cost. But one big concern is always vendor lock-in. What if you choose the wrong provider? What if they are usually the provider for you but don't meet a requirement for a specific project?

With MongoDB Atlas' [Multi-Cloud support](https://www.mongodb.com/resources/basics/multicloud/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=multicloud-studio3t&utm_term=luce.carter), that choice is no longer a worry. You can deploy your database cluster to multiple cloud providers at once and it will handle how to manage storage and access while you continue to use it as if it was only deployed on one cloud provider.

In this article, we are going to look at the benefits of doing this and also how you can deploy a multi-cloud cluster on MongoDB Atlas.

## Why use a multi-cloud strategy?

As mentioned earlier, one of the reasons you might want to use multiple cloud providers is to avoid vendor lock-in. If your data is stored with multiple providers and you no longer want to use one of them, you can carry on as normal using just the remaining providers your databases are deployed to. This gives you greater flexibility and less fear of choosing the "wrong" option for you.  

Another consideration is region availability. Regions are data center locations where your services are hosted. Each cloud provider has their own regions in different locations. They are usually available across various continents and countries. But not every country has an equal number of regions. In fact, some countries only have one.

This can be an issue where data sovereignty laws exist—for example, in Germany. Data cannot leave Germany which means you cannot use multiple regions from the same provider. But the cloud provider might not have more than one data center in a country. So what happens if the region goes down? Cloud providers going down and becoming unavailable can happen and there have been many outages in recent years. If there is only one region available and it goes down, your applications will not have access to the data.

You can get around this issue with multi-cloud, by using regions from multiple cloud providers within the same country, avoiding sovereignty issues. If one cloud goes down, it will just fall back to another and your users will be none the wiser!

## Setting up a multi-cloud deployment in MongoDB Atlas

Setting up a new cluster in MongoDB Atlas to be multi-cloud is very simple but let's go through the steps now.

1. Inside MongoDB Atlas, click the button to create a new cluster.
2. In the new cluster wizard, ensure you toggle the switch to enable multi-cloud.

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeNRKpE7oevY6xtrhkRhfIsizzDXqyPkdWw1IhOdQDPSvo1zxr6X9MKrkwobbeTLLfVXiKche9qbww_eozosc2OL_ATjlxeSRdLAhXbs5hh_SgDoBpZSWEMBlMhu1KiMxYXTf8AYQ?key=XVCJ7AwFRtBzbiRuAEWj9A)

3. The next selections in the wizard allow you to add cloud providers and regions for extra write, read, and analytic nodes.

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdVG2u0jyq6NHuZlKPDTKhpvSLBGnx-OlZcQsbI466GexVlO_RLoTZqv2elAYiLeaQq3Km8MOAIyr2DqoDiSlw7eiTdvYma77N818H8M1jakroSP_uCWV372FN20jti7hBbByWKew?key=XVCJ7AwFRtBzbiRuAEWj9A)

4. You can then pick what cluster tier you want to use, give it a name, and then click \`Create Cluster\`, and it will go ahead and deploy a multi-cloud cluster.

## **Summary**

Implementing multi-cloud is a great way to avoid vendor lock-in and data sovereignty rules. It also helps with making your applications highly available, as any region or provider outages will allow it to revert automatically to another provider or region, meaning your customers face no impact!  

MongoDB Atlas makes it really easy to deploy a [multi-cloud database](https://www.mongodb.com/resources/basics/multicloud/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=multicloud-studio3t&utm_term=luce.carter) in just a few steps. Give it a try today!
