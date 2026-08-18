---
title: "Book Review: \"Effortless Cloud-Native App Development Using Skaffold\""
date: "2021-10-15T08:19:17+00:00"
lastmod: "2021-10-15T08:19:20+00:00"
description: "Quickly and thoroughly learn how to simplify the development and deployment of Cloud-native SpringBoot applications on Kubernetes."
authors:
  - "geertjan-wielenga"
image: "effortless-cloud-skaffold-835x1024.png"
categories:
  - "Book Review"
  - "Books"
related_posts:
  - "book-review-seriously-good-software"
  - "book-review-java-by-comparison"
  - "book-review-quarkus-for-spring-developers"
frozen: false
---

{{< img src="effortless-cloud-skaffold-835x1024.png" class="size-large is-resized" width="299" height="367" >}}

In "[Effortless Cloud-Native App Development Using Skaffold](https://www.packtpub.com/product/effortless-cloud-native-app-development-using-skaffold/9781801077118)", Ashish Choudhary provides a thorough overview of [Skaffold](https://skaffold.dev/), with the book's subtitle clearly defining its agenda: "Simplify the development and deployment of Cloud-native SpringBoot applications on Kubernetes with Skaffold".

* Section 1, "The Kubernetes Nightmare -- Skaffold to the Rescue", promises to describe "the pain and suffering of developing an application with Kubernetes", with Skaffold as the solution to help in automating the building, pushing, and deployment of applications running on Kubernetes.  
  * In **chapter 1** , "Code, Build, Test, and Repeat -- The Application Development Inner Loop", the author explains what the "application development inner loop" is, as well as the inner versus outer development loops, together with an exploration of the traditional application development inner loop in contrast to the container-native application development inner loop. All this is discussed in the context of JRebel and SpringBoot, with their related tools and technologies.  

  <!-- -->

  * With the previous chapter having focused on traditional development workflows, **chapter 2** , "Developing Cloud-Native Applications with Kubernetes -- A Developer's Nightmare", expounds upon the "hardships that a developer has to go through while developing Cloud-native applications with Kubernetes". The key stumbling blocks turn out to be focused on developers wanting simplified workflows via Kubernetes while not being nor wanting to be Kubernetes experts. "Developers just want to code rather than worry about how and where their applications will be deployed," is a statement that is hard to argue with.  

  <!-- -->

  * What you get in **chapter 3** , "Skaffold — Easy-Peasy Cloud-Native Kubernetes Application Development", is a clear introduction to Skaffold. "Skaffold is a CLI tool that automates the build, push, and deploy steps for Cloud-native applications running on local or remote Kubernetes clusters of your choice. Skaffold is not a replacement for Docker or Kubernetes. It works in conjunction with them and handles the build, push, and deploy boilerplate part for you." Complete instructions for setting up your environment, introducing you to the Skaffold command line, with code samples throughout the book, are provided.   
* Section 2: "Getting Started with Skaffold" goes through everything connected to the basic Skaffold usage, from a run down of its features, an overview of its architecture, bootstrapping projects with Skaffold, including building and deploying container images with Skaffold.   
  * With **chapter 4** , "Understanding Skaffold's Features and Architectures", you learn about Skaffold's specificities, such as "super-fast local development, effortless remote development, built-in tag management, lightweight capabilities, and file sync capability". Really useful in this chapter are several diagrams and images aimed at "demystifying" Skaffold's architecture. By the end of the chapter, you have a really solid understanding of what Skaffold can do for you.  
  * **Chapter 5** , "Installing Skaffold and Demystifying Its Pipeline Stages" gets you into the nitty gritty of installing specificities on different operating systems, as well as the command line, as well as the various stages of its pipeline, such as "init", "build", and "deploy", wrapping up with an introduction to its debugging facilities.  

  <!-- -->

  * In **chapter 6** , "Working with Skaffold Container Image Builders and Deployers", you are introduced to reactive programming, while buidling a SpringBoot CRUD application and being shown Skaffold's supported container image builders, from Docker to kaniko to Jib to Buildpacks. You also learn about the different ways to deploy images to a Kubernetes cluster using tools such as kubectl, Helm, and Kustomize.  
* Section 3: "Building and Deploying Cloud-Native SpringBoot Applications with Skaffold" has as its focus the building and deployment of SpringBoot applications using Skaffold to local (minikube and more) and remote clusters (GKE).  
  * In **chapter 7** , "Building and Deploying a SpringBoot Application with the Cloud Code Plugin", you are introduced to Google's Cloud Code plugin, which is available with IDEs such as IntelliJ. The author shows you step by step how to create a SpringBoot application while using the plugin to deploy it to a local Kubernetes cluster.  
  * **Chapter 8** , "Deploying a SpringBoot Application to the Google Kubernetes Engine Using Skaffold" shows you how to deploy the same SpringBoot application from the previous chapter to the remote Google Kubernetes Engine (GKE), which is a managed Kubernetes service provided by the Google Cloud Platform.  
  * Next, in **chapter 9** , "Creating a Production-Ready CI/CD Pipeline with Skaffold", you are shown how to create a production-ready continuous integration (CI) and continuous deployment (CD) pipeline for the same SpringBoot application as before, using Skaffold and GitHub Actions.  
  * **Chapter 10**, "Exploring Skaffold Alternatives, Best Practices, and Pitfalls" starts by looking at other tools that provide similar functionalities as Skaffold: Telepresence, Tilt.dev, OpenShift Do, Oketo, Garden, and docker-compose. Next, you learn several tips and tricks to follow while developing Cloud-native Kubernetes applications with Skaffold, including how to do faster builds and how to solve typical problems, as well as a range of pitfalls and Skaffold's future roadmap.

Over the years, tooling around Kubernetes has improved significantly in line with the increased adoption of Kubernetes in the industry. Developers need tools that increase productivity while being able to quickly develop applications for the Cloud.

Skaffold dramatically enhances that process and there is no better time than the present for a book such as this, which can surely be seen to be some kind of Skaffold Bible. The author provides a complete and thorough overview of the central issues faced by users of Kubernetes, presents Skaffold as a solution, highlights its features and pitfalls, while placing it within the context of the broader ecosystem of comparable solutions.
