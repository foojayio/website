---
title: "Interview on Machine Learning and Disruptive Data Science"
date: "2021-03-26T13:15:29+00:00"
lastmod: "2023-01-08T07:39:57+00:00"
description: "I spoke with Fabiane recently about Tail Target, its objectives, the theory behind it, and the underlying technology. Read on!"
authors:
  - "kevinfarnham"
image: "fabiane-tailtarget.com_8314_small-2-700x467.jpg"
categories:
  - "Interviews"
  - "Machine Learning"
tags:
related_posts:
frozen: false
---

I attended sessions and spoke with Java Champion Fabiane Bizinella Nardon at many JavaOne conferences over the past years. I remember, in our conversations in the hallways, discussing various entrepreneurial ventures she was working on. One of the ideas was [Tail Target](https://www.tail.digital "Tail Target"). Fast forward almost a decade, and Tail Target has truly come to fruition.

![](fabiane-tailtarget.com_8314_small-2-700x467.jpg)

Fabiane Bizinella Nardon has an MSc in Computer Science and a PhD in Electronic Engineering. She is an expert in Data Engineering and Machine Language Engineering. She is the program committee leader for the Machine Learning Engineering track of QCon São Paulo, a frequent speaker on the subject, and author of several articles.

I spoke with Fabiane recently about Tail Target, its objectives, the theory behind it, and the underlying technology.

**Q: What is the primary business objective of Tail Target?**

**Fabiane:** Tail Target uses Data Science to provide insights for companies, especially in marketing and advertisement. Our products aim to store, organize, augment and process first and third party data to provide a solution for understanding customer behavior and help companies to provide better offers.

**Q: Can you briefly describe what DMP and CDP are, and why they are important today?**

**Fabiane:** DMP means Data Management Platform. A DMP is a tool used to collect and organize data from a business audience (website visitors, for example), aiming to process these data to obtain customer segmentations. These customer segments can be used to create marketing campaigns, obtain insights, create personas, analyze customer behavior and more.

CDP means Customer Data Platform. A CDP is a tool that helps companies to store, organize and enrich the data they have about their customers, both data collected online or offline.

These tools are extremely important today to help companies to understand their customers, so they can provide better services, decide which new products to create, measure the ROI of their marketing campaigns and so on.

**Q: Which Java/JVM technologies do you utilize in order to meet these objectives?**

**Fabiane:** A large part of what we do is big data processing. This requires distributed processing technologies, so we can process large volumes of data in clusters. We use Apache Hadoop and Apache Spark for this. The data processing pipelines created either run in Java or Scala.

Although Apache Hadoop and Apache Spark are in the core of our services, we use other Java technologies to provide our services, including AI processing with Apache MLLib, web frameworks, language parsers, database connectors, distributed file systems and others.

**Q: You talk about "disruptive Data Science technologies" - what does this mean?**

**Fabiane:** We started working with Data Science about 8 years ago, when this field was very new and many of the tools we have now, especially in cloud offerings, were not available yet. This made us to learn a lot and build several tools from scratch. We have now a good understanding of what the pain points are when creating Data Science projects, and we created our own suite of tools to address the main problems in the field. There is a long discussion and explanation of how we solved these problems in our blog. See [Tail Refinaria -- Solving Data Engineering problems](https://blog.tail.digital/en/tail-refinaria-solving-data-engineering-problems/ "Tail Refinaria – Solving Data Engineering problems").

**Q: Can you describe the type of Java/JVM programming that's involved in Tail Target's "machine learning engineering"?**

**Fabiane:** Our Machine Learning Engineering tool allows to train, test, catalog and execute machine learning models. It also provides a model lineage tool, to track how models were created and evolved over time. This is very important, since being able to reproduce models and understand how they were created is critical as more and more decisions are made by machine learning algorithms.

When the models are created in our platform, they are created using an Apache MLLib pipeline. Apache MLLib is a Spark library with APIs in Java, Scala, Python and R. We create our Apache MLLib pipelines in Java and execute them in a Apache Spark cluster. Besides that, our solution involves interceptors to capture events and keep track of how the model was created and when it is executed. The whole solution is created in Java. There is also a series of blog posts about it at our website. See [Machine Learning at Tail Refinaria](https://blog.tail.digital/en/machine-learning-at-tail-refinaria/ "Machine Learning at Tail Refinaria").

**Q: How does Tail Target deliver its solution to its customers?**

**Fabiane:** It is an SaaS solution. Once customers subscribe to our services, they receive an account then access our software in the cloud.

**Q: Is there anything else you'd like to tell the foojay audience about in conclusion?**

**Fabiane:** I think Data Engineering and Machine Learning Engineering are fields that could benefit a lot from the experience Java developers have in designing reliable and scalable systems. Good architectures are needed in this field and how you create your solutions can impact performance and cost. Although we advanced a lot in machine learning in the last years, we still have important problems to solve in Data Engineering and Machine Learning Engineering, and this is crucial to make Data Science deliver the results it promised.
