---
title: "Mastering Serverless Debugging"
slug: "mastering-serverless-debugging"
date: "2024-07-29T10:14:26+00:00"
lastmod: "2024-07-29T10:15:22+00:00"
description: "Discover effective strategies for debugging serverless and AWS Lambda. Serverless can be painful, but not a bottomless pit of despair."
canonical: "https://debugagent.com/mastering-serverless-debugging"
authors:
  - "shai-almog"
image: "ff47ff1d20fcebaea29f4f4afaf6bccf.webp-copy.jpg"
categories:
  - "Debugging"
  - "Tutorials"
tags:
related_posts:
  - "why-is-kubernetes-debugging-so-problematic"
  - "debugging-kubernetes-part-1-an-introduction"
  - "software-testing-as-a-debugging-tool"
frozen: false
---

* [Introduction to Serverless Computing](#introduction-to-serverless-computing)
* [Challenges of Serverless Debugging](#challenges-of-serverless-debugging)
  * [Disconnected Environments](#disconnected-environments)
  * [Lack of Standardization](#lack-of-standardization)
  * [Limited Debugging Tools](#limited-debugging-tools)
  * [Concurrency and Scale](#concurrency-and-scale)
* [Effective Strategies for Serverless Debugging](#effective-strategies-for-serverless-debugging)
  * [Local Debugging with IDE Remote Capabilities](#local-debugging-with-ide-remote-capabilities)
  * [Using Feature Flags for Debugging](#using-feature-flags-for-debugging)
  * [Staged Rollouts and Canary Deployments](#staged-rollouts-and-canary-deployments)
  * [Comprehensive Logging](#comprehensive-logging)
  * [Embracing Idempotency](#embracing-idempotency)
* [Debugging a Lambda Application Locally with AWS SAM](#debugging-a-lambda-application-locally-with-aws-sam)
  * [Setting Up the Local Environment](#setting-up-the-local-environment)
  * [Running the Hello World Application Locally](#running-the-hello-world-application-locally)
  * [Configuring Remote Debugging](#configuring-remote-debugging)
  * [Handling Debugger Timeouts](#handling-debugger-timeouts)
* [Final Word](#final-word)

**Serverless computing has emerged as a transformative approach to deploying and managing applications. The theory is that by abstracting away the underlying infrastructure, developers can focus solely on writing code.**

While the benefits are clear---scalability, cost efficiency, and performance---debugging serverless applications presents unique challenges. This post explores effective strategies for debugging serverless applications, particularly focusing on AWS Lambda.

Before I proceed I think it's important to disclose a bias: I am personally not a huge fan of Serverless or PaaS after [I was burned badly by PaaS in the past](https://dev.to/codenameone/production-horrors-handling-disasters-public-debrief-1kf6). However, [some smart people like Adam swear by it](https://www.adam-bien.com/) so I should keep an open mind.

{{< youtube B6uyutAbEDw >}}

<br />

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers **t** his subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/)**.**

Introduction to Serverless Computing {#h2-0-introduction-to-serverless-computing}
---------------------------------------------------------------------------------

Serverless computing, often referred to as Function as a Service (FaaS), allows developers to build and run applications without managing servers. In this model, cloud providers automatically handle the infrastructure, scaling, and management tasks, enabling developers to focus purely on writing and deploying code. Popular serverless platforms include AWS Lambda, Azure Functions, and Google Cloud Functions.

In contrast, Platform as a Service (PaaS) offers a more managed environment where developers can deploy applications but still need to configure and manage some aspects of the infrastructure. PaaS solutions, such as Heroku and Google App Engine, provide a higher level of abstraction than Infrastructure as a Service (IaaS) but still require some server management.

Kubernetes, [which we recently discussed](https://debugagent.com/why-is-kubernetes-debugging-so-problematic?source=more_series_bottom_blogs), is an open-source container orchestration platform that automates the deployment, scaling, and management of containerized applications. While Kubernetes offers powerful capabilities for managing complex, multi-container applications, it requires significant expertise to set up and maintain. Serverless computing simplifies this by removing the need for container orchestration and management altogether.

The "catch" is two fold:

* Serverless programming removes the need to understand the servers but also removes the ability to rely on them resulting in more complex architectures.
* Pricing starts off cheap. Practically free. It can quickly escalate especially in case of an attack or misconfiguration.

Challenges of Serverless Debugging {#h2-1-challenges-of-serverless-debugging}
-----------------------------------------------------------------------------

While serverless architectures offer some benefits, they also introduce unique debugging challenges. The primary issues stem from the inherent complexity and distributed nature of serverless environments. Here are some of the most pressing challenges.

### Disconnected Environments {#h3-2-disconnected-environments}

One of the major hurdles in serverless debugging is the lack of consistency between development, staging, and production environments. While traditional development practices rely on these separate environments to test and validate code changes, serverless architectures often complicate this process. The differences in configuration and scale between these environments can lead to bugs that only appear in production, making them difficult to reproduce and fix.

### Lack of Standardization {#h3-3-lack-of-standardization}

The serverless ecosystem is highly fragmented, with various vendors offering different tools and frameworks. This lack of standardization can make it challenging to adopt a unified debugging approach. Each platform has its own set of practices and tools, requiring developers to learn and adapt to multiple environments.

This is slowly evolving with some platforms gaining traction, but since this is a vendor driven industry there are many edge cases.

### Limited Debugging Tools {#h3-4-limited-debugging-tools}

Traditional debugging tools, such as step-through debugging and breakpoints, are often unavailable in serverless environments. The managed and controlled nature of serverless functions restricts access to these tools, forcing developers to rely on alternative methods, such as logging and remote debugging.

### Concurrency and Scale {#h3-5-concurrency-and-scale}

Serverless functions are designed to handle high concurrency and scale seamlessly. However, this can introduce issues that are hard to reproduce in a local development environment. Bugs that manifest only under specific concurrency conditions or high load are particularly challenging to debug.

Notice that when I discuss concurrency here I'm often referring to race conditions between separate services.

Effective Strategies for Serverless Debugging {#h2-6-effective-strategies-for-serverless-debugging}
---------------------------------------------------------------------------------------------------

Despite these challenges, several strategies can help make serverless debugging more manageable. By leveraging a combination of local debugging, feature flags, staged rollouts, logging, idempotency, and Infrastructure as Code (IaC), developers can effectively diagnose and fix issues in serverless applications.

### Local Debugging with IDE Remote Capabilities {#h3-7-local-debugging-with-ide-remote-capabilities}

While serverless functions run in the cloud, you can simulate their execution locally using tools like AWS SAM (Serverless Application Model). This involves setting up a local server that mimics the cloud environment, allowing you to run tests and perform basic trial-and-error debugging.

To get started, you need to install Docker or Docker Desktop, create an AWS account, and set up the AWS SAM CLI. Deploy your serverless application locally using the SAM CLI, which enables you to run the application and simulate Lambda functions on your local machine. Configure your IDE for remote debugging, launching the application in debug mode, and connecting your debugger to the local host. Set breakpoints to step through the code and identify issues.

### Using Feature Flags for Debugging {#h3-8-using-feature-flags-for-debugging}

Feature flags allow you to enable or disable parts of your application without deploying new code. This can be invaluable for isolating issues in a live environment. By toggling specific features on or off, you can narrow down the problematic areas and observe the application's behavior under different configurations.

Implementing feature flags involves adding conditional checks in your code that control the execution of specific features based on the flag's status. Monitoring the application with different flag settings helps identify the source of bugs and allows you to test fixes without affecting the entire user base.

This is essentially "debugging in production". Working on a new feature?

Wrap it in a feature flag which is effectively akin to wrapping the entire feature (client and server) in if statements. You can then enable it conditionally globally or on a per user basis. This means you can test the feature, enable or disable it based on configuration without redeploying the application.

### Staged Rollouts and Canary Deployments {#h3-9-staged-rollouts-and-canary-deployments}

Deploying changes incrementally can help catch bugs before they affect all users. Staged rollouts involve gradually rolling out updates to a small percentage of users before a full deployment. This allows you to monitor the performance and error logs of the new version in a controlled manner, catching issues early.

Canary deployments take this a step further by deploying new changes to a small subset of instances (canaries) while the rest of the system runs the stable version. If issues are detected in the canaries, you can roll back the changes without impacting the majority of users. This method limits the impact of potential bugs and provides a safer way to introduce updates. This isn't great as in some cases some demographics might be more reluctant to report errors. However, for server side issues this might make sense as you can see the impact based on server logs and metrics.

### Comprehensive Logging {#h3-10-comprehensive-logging}

Logging is one of the most common and essential tools for debugging serverless applications. I wrote and [spoke a lot about logging in the past](https://www.youtube.com/watch?v=53qCLRFcBSs). By logging all relevant data points, including inputs and outputs of your functions, you can trace the flow of execution and identify where things go wrong.

However, excessive logging can increase costs, as serverless billing is often based on execution time and resources used. It's important to strike a balance between sufficient logging and cost efficiency. Implementing log levels and selectively enabling detailed logs only when necessary can help manage costs while providing the information needed for debugging.

I talk about striking the delicate balance between debuggable code, performance and cost with logs in the following video. Notice that this is a general best practice and not specific to serverless.

{{< youtube 53qCLRFcBSs >}}

<br />

### Embracing Idempotency {#h3-11-embracing-idempotency}

Idempotency, a key concept from functional programming, ensures that functions produce the same result given the same inputs, regardless of the number of times they are executed. This simplifies debugging and testing by ensuring consistent and predictable behavior.

Designing your serverless functions to be idempotent involves ensuring that they do not have side effects that could alter the outcome when executed multiple times. For example, including timestamps or unique identifiers in your requests can help maintain consistency. Regularly testing your functions to verify idempotency can make it easier to pinpoint discrepancies and debug issues.

Testing is always important but in serverless and complex deployments it becomes critical. Awareness and embrace of idempotency allows for more testable code and easier to reproduce bugs.

Debugging a Lambda Application Locally with AWS SAM {#h2-12-debugging-a-lambda-application-locally-with-aws-sam}
----------------------------------------------------------------------------------------------------------------

{{< youtube SlFA-JlTYGM >}}

<br />

Debugging serverless applications, particularly AWS Lambda functions, can be challenging due to their distributed nature and the limitations of traditional debugging tools. However, AWS SAM (Serverless Application Model) provides a way to simulate Lambda functions locally, enabling developers to test and debug their applications more effectively. I will use it as a sample to explore the process of setting up a local debugging environment, running a sample application, and configuring remote debugging.

### Setting Up the Local Environment {#h3-13-setting-up-the-local-environment}

Before diving into the debugging process, it's crucial to set up a local environment that can simulate the AWS Lambda environment. This involves a few key steps:

1. **Install Docker** : Docker is required to run the local simulation of the Lambda environment. You can download Docker or Docker Desktop from the official [Docker website](https://docs.docker.com/get-docker/).
2. **Create an AWS Account** : If you don't already have an AWS account, you need to create one. Follow the instructions on the [AWS account creation page](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/).
3. **Set Up AWS SAM CLI** : The AWS SAM CLI is essential for building and running serverless applications locally. You can install it by following the [AWS SAM installation guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html).

### Running the Hello World Application Locally {#h3-14-running-the-hello-world-application-locally}

To illustrate the debugging process, let's use a simple "Hello World" application. The code for this application can be found in the [AWS Hello World tutorial](https://github.com/shai-almog/HelloLambda).

1. **Deploy Locally** : Use the SAM CLI to deploy the Hello World application locally. This can be done with the following command:

   ```bash
   sam local start-api
   ```

   This command starts a local server that simulates the AWS Lambda cloud environment.
2. **Trigger the Endpoint** : Once the local server is running, you can trigger the endpoint using a `curl` command:

   ```bash
   curl http://localhost:3000/hello
   ```

   This command sends a request to the local server, allowing you to test the function's response.

### Configuring Remote Debugging {#h3-15-configuring-remote-debugging}

While running tests locally is a valuable step, it doesn't provide full debugging capabilities. To debug the application, you need to configure remote debugging. This involves several steps.

First we need to start the application in debug mode using the following SAM command:

```bash
sam local invoke -d 5858
```

This command pauses the application and waits for a debugger to connect.

Next we need to configure the IDE for remote debugging. We start by setting up the IDE to connect to the local host for remote debugging. This typically involves creating a new run configuration that matches the remote debugging settings.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0mlv7ij1mypsmg0rzlsk.png)

We can now set breakpoints in the code where we want the execution to pause. This allows us to step through the code and inspect variables and application state just like in any other local application.

We can test this by invoking the endpoint e.g. using curl. With the debugger connected we would stop on the breakpoint like any other tool:

```bash
curl http://localhost:3000/hello
```

The application will pause at the breakpoints you set, allowing you to step through the code.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/yzpgf6da5rpw332ednvm.png)

### Handling Debugger Timeouts {#h3-16-handling-debugger-timeouts}

One significant challenge when debugging Lambda functions is the quick timeout setting. Lambda functions are designed to execute quickly, and if they take too long, the costs can become prohibitive. By default, the timeout is set to a short duration, but you can configure this in the `template.yaml` file e.g.:

```yaml
Resources:
  HelloWorldFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: app.lambdaHandler
      Timeout: 60  # timeout in seconds
```

After updating the timeout value, re-issue the `sam build` command to apply the changes.

In some cases, running the application locally might not be enough. You may need to simulate running on the actual AWS stack to get more accurate debugging information. Solutions like SST (Serverless Stack) or MerLoc can help achieve this, though they are specific to AWS and relatively niche.

Final Word {#h2-17-final-word}
------------------------------

Serverless debugging requires a combination of strategies to effectively identify and resolve issues. While traditional debugging methods may not always apply, leveraging local debugging, feature flags, staged rollouts, comprehensive logging, idempotency, and IaC can significantly improve your ability to debug serverless applications. As the serverless ecosystem continues to evolve, staying adaptable and continuously updating your debugging techniques will be key to success.

Debugging serverless applications, particularly AWS Lambda functions, can be complex due to their distributed nature and the constraints of traditional debugging tools. However, by leveraging tools like AWS SAM, you can simulate the Lambda environment locally and use remote debugging to step through your code. Adjusting timeout settings and considering advanced simulation tools can further enhance your debugging capabilities.
