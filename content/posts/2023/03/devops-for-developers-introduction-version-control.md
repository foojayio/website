---
title: "DevOps for Developers - Introduction and Version Control"
slug: "devops-for-developers-introduction-version-control"
date: "2023-03-12T09:13:33+00:00"
lastmod: "2023-03-12T09:16:01+00:00"
description: "Improving our DevOps skills can help us become better developers, team mates, and managers. Learn DevOps principles and advanced git!"
canonical: "https://debugagent.com/devops-for-developers-introduction-and-version-control"
authors:
  - "shai-almog"
image: "/images/posts/2023/03/devops-for-developers-introduction-version-control/devops4devs-01.jpg"
categories:
  - "Developer Tools"
  - "DevOps"
tags:
related_posts:
  - "java-serialization-filtering-prevent-0-day-security-vulnerabilities"
  - "is-it-time-to-go-back-to-the-monolith"
  - "what-are-you-missing-by-debugging-in-vs-code"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

I start some of my talks with a joke: back in my day we didn't have monitoring or observability. We'd go to the server and give it a kick. Hear the HD spin? It's working!

We didn't have DevOps. If we were lucky we had some admins and a technician to solve hardware issues. That's it.

In a small company, we would do all of that ourselves. Today this is no longer practical. The complexity of deployment and scale is so big, it's hard to imagine a growing company without an engineer dedicated to the operations.

In this series, I hope to introduce you to some of the core principles and tools used by DevOps.

This is an important skill that we need to master in a startup where we might not have a DevOps role at all. Or in a big corporation, where we need to communicate with the DevOps team and explain our needs or requirements.

{{< youtube 2HatFLh4xoA >}}

<br />

What's DevOps? {#h2-0-what-s-devops}
------------------------------------

DevOps is a software development methodology that aims to bridge the gap between development and operations teams. It emphasizes collaboration and communication between these two teams to ensure the seamless delivery of high-quality software products.

The core principles behind it are:

1. **Continuous Integration and Continuous Delivery (CI/CD)** - CI/CD is one of the key principles of DevOps. It involves automated processes for building, testing, and deploying software. With CI/CD, developers can identify and fix bugs early in the development cycle, leading to faster and more reliable delivery of software. As a developer, CI/CD can help you by giving you a faster feedback loop, enabling you to make changes to the code and see the results in real-time. This helps you to quickly identify and fix any issues, which saves time and ensures that your code is always in a releasable state.Notice that CD stands for both Continuous Delivery and Deployment. This is a terribly frustrating acronym. The difference between the two is simple though. Deployment relies on the delivery, we can't deploy an application unless it was built and delivered. The deployment aspect means that merging our commits into the main branch will result in a change to production at some point, without any user involvement.
2. **Automation** - Automation involves automating repetitive tasks such as building, testing, and deploying software. This helps to reduce the time and effort required to perform these tasks, freeing up developers to focus on more important tasks.As a developer, automation can help you by freeing up your time and allowing you to focus on writing code, rather than spending time on manual tasks. Additionally, automation helps reduce the risk of human error, ensuring that your code is always deployed correctly.
3. **Collaboration and Communication** - DevOps emphasizes collaboration and communication between development and operations teams. This helps ensure that everyone is on the same page and working towards a common goal. It also helps reduce the time and effort required to resolve any issues that may arise.

Platform Engineering {#h2-1-platform-engineering}
-------------------------------------------------

Recently there's been a rise in the field of platform engineering. This is somewhat confusing as the overlap between the role of DevOps and a Platform Engineer isn't necessarily clear.

However, they are two related but distinct fields within software development. While both are concerned with improving software delivery and operation processes, they have different focuses and approaches.

Platform Engineering is a discipline that focuses on building and maintaining the infrastructure and tools required to support the software development process. This includes the underlying hardware, software, and network infrastructure, as well as the tools and platforms used by development and operations teams.

In other words, DevOps is concerned with improving the way software is developed and delivered, while Platform Engineering is concerned with building and maintaining the platforms and tools that support that process.

While both DevOps and Platform Engineering complement each other, they serve different purposes. DevOps helps teams to work together more effectively and deliver software faster, while Platform Engineering provides the infrastructure and tools needed to support that process.

Where do we Start? {#h2-2-where-do-we-start}
--------------------------------------------

When learning DevOps, it is important to have a solid understanding of the tools and techniques commonly used in the field. Here are some of the most important tools and techniques to learn:

1. **Version control systems**: Understanding how to use version control systems, such as Git, is a key component of DevOps. Version control systems allow teams to track changes to their code, collaborate on projects, and roll back changes if necessary. I assume you know git and will skip it and go directly to the next stage.
2. **Continuous Integration (CI) and Continuous Deployment (CD) tools**: CI/CD tools are at the heart of DevOps and are used to automate the build, test, and deployment of code. Popular CI/CD tools include Jenkins, Travis CI, CircleCI, and GitLab CI/CD. I will focus on GitHub Actions. It isn't a popular tool in the DevOps space since it's relatively limited, but for our needs as developers, it's pretty great.
3. **Infrastructure as Code (IaC) tools**: IaC tools let us manage our infrastructure as if it was source code. This makes it easier to automate the provisioning, configuration, and deployment of infrastructure. Popular IaC tools include Terraform, CloudFormation, and Ansible. I also like Pulumi which lets you use regular programming languages to describe the infrastructure, including Java.
4. **Containerization**: Containerization technologies, such as Docker, allow you to package and deploy applications in a consistent and portable way, making it easier to move applications between development, testing, and production environments.
5. **Orchestration** : Orchestration refers to the automated coordination and management of multiple tasks and processes, often across multiple systems and technologies. In DevOps, orchestration is used to automate the deployment and management of complex, multi-tier applications and infrastructure.  
   Popular orchestration tools include Kubernetes, Docker Swarm, and Apache Mesos. These tools allow teams to manage and deploy containers, automate the scaling of applications, and manage the overall health and availability of their systems.
6. **Monitoring and logging tools**: Monitoring and logging tools allow you to keep track of the performance and behavior of your systems and applications. Popular monitoring tools include Nagios, Zabbix, and New Relic. Prometheus and Grafana are probably the most popular in this field in recent years. Popular logging tools include ELK Stack (Elasticsearch, Logstash, and Kibana), Graylog, and Fluentd.
7. **Configuration management tools**: Configuration management tools, such as Puppet, Chef, and Ansible, allow you to automate the configuration and management of your servers and applications.
8. **Cloud computing platforms**: Cloud computing platforms, such as Amazon Web Services (AWS), Microsoft Azure, and Google Cloud Platform (GCP). They provide the infrastructure and services necessary for DevOps practices.

In addition to these tools, it is also important to understand DevOps practices and methodologies, such as agile.

Remember, the specific tools and techniques you need to learn will depend on the needs of your organization and the projects you are working on.

However, by having a solid understanding of the most commonly used tools and techniques in DevOps, you will be well-prepared to tackle a wide range of projects and challenges.

Most features and capabilities are transferable. If you learn CI principles in one tool, moving to another won't be seamless. But it will be relatively easy.

Version Control {#h2-3-version-control}
---------------------------------------

We all use git, or at least I hope so. Git's dominance in version control has made it much easier to build solutions that integrate deeply. As developers, Git is primarily viewed as a version control system that helps us manage and track changes to our codebase.

We use Git to collaborate with other developers, create and manage branches, merge code changes, track issues, and bugs. Git is an essential tool for developers as it allows them to work efficiently and effectively on code projects.

DevOps have a different vantage point. Git is viewed as a critical component of the CI/CD pipeline. In this context, Git is used as a repository to store code and other artifacts such as configuration files, scripts, and build files.

DevOps professionals use Git to manage the release pipeline, automate builds, and manage deployment configurations. Git is an important part of the DevOps toolchain as it allows for the seamless integration of code changes into the CI/CD pipeline, ensuring the timely delivery of software to production.

### Branch Protection {#h3-4-branch-protection}

By default, GitHub projects allow anyone to commit changes to the main (master) branch. This is problematic in most projects.

We usually want to prevent commits to that branch so we can control the quality of the mainline. This is especially true when working with CI as a break in the master can stop the work of other developers.

We can minimize this risk by forcing everyone to work on branches and submit pull requests to the master. This can be taken further with code review rules that require one or more reviewers. GitHub has highly configurable rules that can be enabled in the project settings. As you can see here.

!\[\](<https://cdn.hashnode.com/res/hashnode/image/upload/v1676992748714/9b4bdfee-5805-49d0-88e7-d3e49c789f9a.png> align="center")

Enabling branch protection on the master branch in GitHub provides several benefits, including:

* Preventing accidental changes to the master branch: By enabling branch protection on the master branch, you can prevent contributors from accidentally pushing changes to the branch. This helps to ensure that the master branch always contains stable and tested code.
* Enforcing code reviews: You can require that all changes to the master branch be reviewed by one or more people before they are merged. This helps to ensure that changes to the master branch are high quality and meet the standards of your team.
* Preventing force pushes: Enabling branch protection on the master branch can prevent contributors from force-pushing changes to the branch, which can overwrite changes made by others. This helps to ensure that changes to the master branch are made intentionally and with careful consideration.
* Enforcing status checks: You can require that certain criteria, such as passing tests or successful builds, are met before changes to the master branch are merged. This helps to ensure that changes to the master branch are of high quality and do not introduce new bugs or issues.

Overall, enabling branch protection on the master branch in GitHub can help to ensure that changes to your codebase are carefully reviewed, tested, and of high quality. This can help to improve the stability and reliability of your software.

### Working with Pull Requests {#h3-5-working-with-pull-requests}

As developers, we find that working with branches and pull requests allow us to collect multiple separate commits and changes to a single feature. This is one of the first areas of overlap between our role as developers and the role of DevOps. Pull requests let us collaborate and review each other's code before merging it into the main branch.

This helps identify issues and ensures that the codebase remains stable and consistent. With pull requests, the team can discuss and review code changes, suggest improvements, and catch bugs before they reach production. This is critical for maintaining code quality, reducing technical debt, and ensuring that the codebase is maintainable. The role of DevOps is to tune the quality vs. churn.

How many reviewers should we have for a pull request? Is a specific reviewer required? Do we require test coverage levels?

A DevOps needs to tune the ratio between developer productivity, stability and churn. By increasing the reviewer count or forcing review by a specific engineer we create bottlenecks and slow development. The flip side is a potential increase in quality.

We decide on these metrics based on rules of thumb and best practices. But a good DevOps engineer will follow through everything with metrics that help an informed decision down the road. E.g. If we force two reviewers we can then look at the time it takes to merge a pull request which will probably increase. But we can compare it to the number of regressions and issues after the policy took place. That way we have a clear and factual indication of the costs and benefits of a policy.

The second benefit of pull requests is their crucial role in the CI/CD process. When a developer creates a pull request, it triggers an automated build and testing process, which verifies that the code changes are compatible with the rest of the codebase and that all tests pass. This helps identify any issues early in the development process and prevents bugs from reaching production. Once the build and test processes are successful, the pull request can be merged into the main branch, triggering the release pipeline to deploy the changes to production. I will cover CI more in-depth in the next installment of this series.

Finally {#h2-6-finally}
-----------------------

I feel that the discussion of DevOps is often very vague.

There are no hard lines between the role of a DevOps engineer and the role of a developer since they are developers and are a part of the R\&D team.

DevOps navigate over that fine line between administration and development, they need to satisfy the sometimes conflicting requirements on both ends.

I think understanding their jobs and tools can help make us better developers, better teammates and better managers.

Next time we'll discuss building a CI pipeline using GitHub actions. Working on your artifacts. Managing secrets and keeping everything in check. Notice we won't discuss the continuous delivery in great detail at this stage because that would drag us into a discussion of deployment.

I fully intend to circle back to it and discuss CD as well once we cover deployment technologies such as IaC, Kubernetes, Docker etc.
