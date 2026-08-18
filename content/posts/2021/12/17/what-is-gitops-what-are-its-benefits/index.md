---
title: "What is GitOps? What are its benefits?"
date: "2021-12-17T16:19:17+00:00"
lastmod: "2021-12-17T16:19:20+00:00"
description: "Let's understand what GitOps is and its benefits and learn how ArgoCD can help with the continuous delivery of Kubernetes-based applications."
canonical: "https://ashishtechmill.com/what-is-gitops-and-what-are-its-benefits"
authors:
  - "yrashish"
image: "https://cdn.hashnode.com/res/hashnode/image/upload/v1636304168731/qHmKbqm_y.jpeg"
categories:
  - "Books"
  - "DevOps"
related_posts:
frozen: false
---

> Let's understand what GitOps is and its benefits. You will also learn how ArgoCD can help with the continuous delivery of Kubernetes-based applications.

The following article is an excerpt from my book **Effortless Cloud-Native App Development Using Skaffold** from Packt Publishing.

![B17385_Mockup Cover_High Res.jpg](https://cdn.hashnode.com/res/hashnode/image/upload/v1636304168731/qHmKbqm_y.jpeg)

It is available for order from [Amazon.com](https://www.amazon.com/Effortless-Cloud-Native-Development-using-Skaffold/dp/1801077118) and directly from [Packt](https://www.packtpub.com/product/effortless-cloud-native-apps-development-using-skaffold/9781801077118). This excerpt comes from chapter 9: Creating a Production-Ready CI/CD Pipeline with Skaffold:

### What is GitOps

The word GitOps was coined by a company named Weaveworks. The idea behind GitOps is to consider Git as a single source of truth for your application and declarative infrastructure. Using Git to manage your declarative infrastructure makes it easy for developers because they interact with Git daily. Once you add configuration inside Git, you get the benefits of version control, such as reviewing changes using pull requests, audit, and compliance.

With GitOps, we create automated pipelines to roll out changes to your infrastructure when someone pushes changes to a Git repository. Then we use GitOps tools to compare the actual production state of your application with what you have defined under source control. Then it also tells you when your cluster doesn't match what you have in production and automatically or manually reconciles it with the desired state. This is a true CD.

You can easily roll back your changes from Kubernetes by doing a simple git revert. In disaster scenarios or if someone accidentally nuked your entire Kubernetes cluster, we could quickly reproduce your whole cluster infrastructure from Git.

### What are the benefits of GitOps

* Using GitOps, the team is shipping 30-100 changes per day to production. Of course, you need to use deployment strategies such as blue-green and canary to validate your changes before making them available to all the users. The overall benefit is an increase in developer productivity.
* You get a better developer experience with GitOps as developers are pushing code and not containers. Moreover, they use familiar tools such as Git and don't need to know about the internals of Kubernetes (that is, kubectl commands).
* By putting declarative infrastructure as code in the Git repository, you automatically get benefits such as audit trail for your cluster, such as who did what and when. It further ensures the compliance and stability of your Kubernetes cluster.
* You can also recover your cluster faster, in case of a disaster, from hours to minutes because your entire system is described in Git.
* Your application code is already on Git, and with GitOps, your operation tasks are part of the same end-to-end workflows. You have a consistent Git workflow across your entire organization.

It's only fair that we also cover some details about Argo CD so that it's easier to understand the later part where we implement a GitOps workflow using Skaffold and Argo CD.

### What is Argo CD?

As per the official documentation of [Argo CD](https://argo-cd.readthedocs.io/en/stable/), it is a declarative, GitOps continuous delivery tool for Kubernetes. In the previous section, we used the term GitOps tool that can compare and sync the application state if it deviates from what we have defined in the Git repository, so it is safe to say that Argo CD is the tool that handles this automation.

Kubernetes introduced us to the concept of control loops through which Kubernetes checks whether the number of replicas running matches with the desired number of replicas. Argo CD leverages the same Kubernetes (K8s) capabilities, and its core component is argocd-applicationcontroller, which is basically a Kubernetes controller. It monitors the state of your application and adjusts the cluster accordingly.

### Conclusion

I hope you found this excerpt helpful. Chapter 9 covers the practical knowledge of how you can use GitHub Actions to automate your development workflows. We have also explained how you could build, test, and deploy your Java applications from your GitHub repository.

Then we described how you could create a CI/CD pipeline for your Kubernetes applications using Skaffold and GitHub Actions. And if that's not enough, there are ten chapters filled with Kubernetes and cloud-native goodness. What are you waiting for?

Grab your copy [now](https://www.amazon.com/Effortless-Cloud-Native-Development-using-Skaffold/dp/1801077118)!
