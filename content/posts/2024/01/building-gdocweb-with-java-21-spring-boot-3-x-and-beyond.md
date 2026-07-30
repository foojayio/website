---
title: "Building gdocweb with Java 21, Spring Boot 3.x and Beyond"
slug: "building-gdocweb-with-java-21-spring-boot-3-x-and-beyond"
date: "2024-01-30T12:40:00+00:00"
lastmod: "2024-01-30T12:54:57+00:00"
description: "Explore the journey of building gdocweb: a developer's insight into choosing Java 21, Spring Boot 3.x, and navigating tech stack challenges."
canonical: "https://debugagent.com/building-gdocweb-with-java-21-spring-boot-3x-and-beyond#heading-java-21-and-spring-boot-3x-innovation-and-maturity"
authors:
  - "shai-almog"
image: "/images/posts/2024/01/building-gdocweb-with-java-21-spring-boot-3-x-and-beyond/DALL-E-2024-01-15-18.35.19-A-blog-post-header-image-visually-explaining-the-creation-of-gdocweb-a-Google-Docs-to-website-converter-tool.-The-image-should-depict-a-flow-of-ele.jpg"
categories:
  - "Java Core"
  - "Spring"
  - "Use Cases"
tags:
related_posts:
  - "is-it-time-to-go-back-to-the-monolith"
  - "when-should-we-move-to-microservices"
  - "everything-bad-in-java-is-good-for-you"
enlighterjs: true
frozen: false
---

**Table of Contents**

* [Introducing gdocweb](#introducing-gdocweb)
  * [Java 21 and Spring Boot 3.x: Innovation and Maturity](#java-21-and-spring-boot-3x-innovation-and-maturity)
* [GraalVM Native Image for Efficiency](#graalvm-native-image-for-efficiency)
  * [Implementing GraalVM](#implementing-graalvm)
  * [Facing the Production Challenges](#facing-the-production-challenges)
  * [The Decision to Move On](#the-decision-to-move-on)
* [Deployment: VPS and Docker Compose](#deployment-vps-and-docker-compose)
  * [Avoiding Raw VPS Deployment](#avoiding-raw-vps-deployment)
  * [Steering Clear of Managed Containers \& Orchestration](#steering-clear-of-managed-containers-orchestration)
  * [The Choice of Docker Compose](#the-choice-of-docker-compose)
* [Front-End: Thymeleaf Over Modern Alternatives](#front-end-thymeleaf-over-modern-alternatives)
  * [React: Modern but Not a One-Size-Fits-All Solution](#react-modern-but-not-a-one-size-fits-all-solution)
  * [The Appeal of Thymeleaf](#the-appeal-of-thymeleaf)
  * [Considering HTMX for Dynamic Functionality](#considering-htmx-for-dynamic-functionality)
* [Final Word](#final-word)

Starting a new project is always a mix of excitement and tough decisions, especially when you're stitching together familiar tools like Google Docs with powerhouses like GitHub Pages.

This is the story of building [*gdocweb*](https://gdocweb.com/), a tool that I hoped would make life easier for many. I'll be diving into why I chose Java 21 and Spring Boot 3.x, ditched GraalVM after some trial and error, and why a simple VPS with Docker Compose won out over more complex options. I also went with Postgres and JPA, but steered clear of migration tools like Flyway.

It's a no-frills, honest recount of the choices, changes, and the occasional 'aha' moments of an engineer trying to make something useful and efficient.

{#introducing-gdocweb}

Introducing gdocweb {#h2-0-introducing-gdocweb}
-----------------------------------------------

Before we dive into the technical intricacies and the decision-making labyrinth of building [gdocweb](https://gdocweb.com/), let's set the stage by understanding what [gdocweb](https://gdocweb.com/) is and the problem it solves. In simple terms, [gdocweb](https://gdocweb.com/) connects Google Docs to GitHub Pages. It's a simple web builder that generates free sites with all the raw power of GitHub behind it and all the usability of Google Docs.

I decided to build [gdocweb](https://gdocweb.com/) to eliminate the complexities typically associated with website building and documentation. It's for users who seek a hassle-free way to publish and maintain their content, but also for savvy users who enjoy the power of GitHub but don't want to deal with markdown nuances.

Here's a short video explaining [gdocweb](https://gdocweb.com/) for the general public:

{{< youtube aaDBFVx6qC8 >}}

<br />

{#java-21-and-spring-boot-3x-innovation-and-maturity}

### Java 21 and Spring Boot 3.x: Innovation and Maturity {#h3-1-java-21-and-spring-boot-3-x-innovation-and-maturity}

When you're spearheading a project on your own, like I was with [gdocweb](https://gdocweb.com/), you have the liberty to make technology choices that might be more challenging in a team or corporate environment. This freedom led me to choose Java 21 and Spring Boot 3.x for this project.

The decision to go with the current Long-Term Support (LTS) version of Java was a no-brainer. It's always tempting to use the latest and greatest, but with Java 21, it wasn't just about using something new; it was about leveraging a platform that has stood the test of time and has evolved to meet modern development needs. Virtual threads were a major part of the decision to go with Java 21. Cost is a huge factor in such projects, and squeezing the maximum throughput from a server is crucial in these situations.

Java, being a mature technology, offered a sense of reliability even in its latest iteration. Similarly, Spring Boot 3.x, despite being a newer version, comes from a lineage of robust and well-tested frameworks. It's a conservative choice in the sense of its long-standing reputation, but innovative in its features and capabilities.

However, this decision wasn't without its hiccups. During the process of integrating Google API access, I had to go through a security CASA tier 2 review. Here's where the choice of Java 21 threw a curveball. The review tool was tailored for JDK 11, and although it worked with JDK 21, it still added a bit of stress to the process. It was a reminder that when you're working with cutting-edge versions of technologies, there can be unexpected bumps along the road. Even if they are as mature as Java.

The transition to Spring Boot 3.x had its own set of challenges, particularly with the changes in security configurations. These modifications rendered most online samples and guides obsolete, breaking a lot of what I had initially set up. It was a learning curve, adjusting to these changes and figuring out the new way of doing things. However, most other aspects were relatively simple and the best compliment I can give to Spring Boot 3.x is that it's very similar to Spring Boot 2.x.

{#graalvm-native-image-for-efficiency}

GraalVM Native Image for Efficiency {#h2-2-graalvm-native-image-for-efficiency}
-------------------------------------------------------------------------------

My interest in GraalVM native image for [gdocweb](https://gdocweb.com/) was primarily driven by its promise of reduced memory usage and faster startup times.

The idea was that with lower memory requirements, I could run more server instances, leading to better scalability and resilience.

Faster startup times also meant quicker recovery from failures, a crucial aspect for maintaining a reliable service.

{#implementing-graalvm}

### Implementing GraalVM {#h3-3-implementing-graalvm}

Getting GraalVM to work was nontrivial but not too hard. After some trial and error, I managed to set up a Continuous Integration (CI) process that built the GraalVM project and uploaded it to Docker.

This was particularly necessary because I'm using an M2 Mac, while my server runs on Intel architecture. This setup meant I had to deal with an 18-minute wait time for each update -- a significant delay for any development cycle.

{#facing-the-production-challenges}

### Facing the Production Challenges {#h3-4-facing-the-production-challenges}

Things started getting rocky when I started to test the project production and staging environments. It became a 'whack-a-mole' scenario with missing library code from the native image.

Each issue fixed seemed to only lead to another, and the 18-minute cycle for each update added to the frustration.

The final straw was realizing the incompatibility issues with Google API libraries. Solving these issues would require extensive testing on a GraalVM build, which was already burdened by slow build times.

For a small project like mine, this became a bottleneck too cumbersome to justify the benefits.

{#the-decision-to-move-on}

### The Decision to Move On {#h3-5-the-decision-to-move-on}

While GraalVM seemed ideal on paper for saving resources, the reality was different. It consumed my limited GitHub Actions minutes and required extensive testing, which was impractical for a project of this scale. Ultimately, I decided to abandon the GraalVM route.

If you do choose to use GraalVM then this was the GitHub Actions script I used, I hope it can help you with your journey:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">name: Java CI with Maven

on:
  push:
    branches: [ "master" ]
  pull_request:
    branches: [ "master" ]

jobs:
  build:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:latest
        env:
          POSTGRES_PASSWORD: yourpassword
        ports:
          - 5432:5432
        options: &gt;-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v3
    - uses: graalvm/setup-graalvm@v1
      with:
        java-version: '21'
        version: '22.3.2' 
        distribution: 'graalvm'
        cache: 'maven'
        components: 'native-image'
        native-image-job-reports: 'true'
        github-token: ${{ secrets.GITHUB_TOKEN }}
    - name: Wait for PostgreSQL
      run: sleep 10
    - name: Build with Maven
      run: mvn -Pnative native:compile

    - name: Build Docker Image
      run: docker build -t autosite:latest .

    - name: Log in to Docker Hub
      uses: docker/login-action@v1
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Push Docker Image
      run: |
        docker tag autosite:latest mydockeruser/autosite:latest
        docker push mydockeruser/autosite:latest</pre>

```yaml

```

This configuration was a crucial part of my attempt to leverage GraalVM's benefits, but as the project evolved, so did my understanding of the trade-offs between idealism in technology choice and practicality in deployment and maintenance.

{#deployment-vps-and-docker-compose}

Deployment: VPS and Docker Compose {#h2-6-deployment-vps-and-docker-compose}
----------------------------------------------------------------------------

When it came to deploying [gdocweb](https://gdocweb.com/), I had a few paths to consider. Each option came with its pros and cons, but after careful evaluation, I settled on using a Virtual Private Server (VPS) with Docker Compose.

Here's a breakdown of my thought process and why this choice made the most sense for my needs.

{#avoiding-raw-vps-deployment}

### Avoiding Raw VPS Deployment {#h3-7-avoiding-raw-vps-deployment}

I immediately ruled out the straightforward approach of installing the application directly on a VPS. This method fell short in terms of migration ease, testing, and flexibility.

Containers offer a more streamlined and efficient approach. They provide a level of abstraction and consistency across different environments, which is invaluable.

{#steering-clear-of-managed-containers-orchestration}

### Steering Clear of Managed Containers \& Orchestration {#h3-8-steering-clear-of-managed-containers-orchestration}

Managed containers and orchestration (e.g k8s) were another option, and while they offer scalability and ease of management, they introduce complexity in other areas. For instance, when using a managed Kubernetes service it would often mean relying on cloud storage for databases, which can get expensive quickly. My philosophy was to focus on cost before horizontal scale, especially in the early stages of a project.

If we don't optimize and stabilize when we're small, the problems will only get worse as we grow. Scaling should ideally start with vertical scaling before moving to horizontal, vertical scaling means more CPU/RAM while horizontal adds additional machines. Vertical scaling is not only more cost-effective but also crucial from a technical standpoint. It makes it easier to identify performance bottlenecks using simple profiling tools.

In contrast, horizontal scaling can often mask these issues by adding more instances, which could lead to higher costs and hidden performance problems.

{#the-choice-of-docker-compose}

### The Choice of Docker Compose {#h3-9-the-choice-of-docker-compose}

Docker Compose emerged as the clear winner for several reasons. It allowed me to seamlessly integrate the database and the application container.

Their communication is contained within a closed network, adding an extra layer of security with no externally open ports. Moreover, the cost is fixed and predictable, with no surprises based on usage.

This setup offered me the flexibility and ease of containerization without the overhead and complexity of more extensive container orchestration systems. It was the perfect middle ground, providing the necessary features without overcomplicating the deployment process.

By using Docker Compose, I maintained control over the environment and kept the deployment process straightforward and manageable. This decision aligned perfectly with the overall ethos of [gdocweb](https://gdocweb.com/) -- simplicity, efficiency, and practicality.

{#front-end-thymeleaf-over-modern-alternatives}

Front-End: Thymeleaf Over Modern Alternatives {#h2-10-front-end-thymeleaf-over-modern-alternatives}
---------------------------------------------------------------------------------------------------

The front-end development of [gdocweb](https://gdocweb.com/) presented a bit of a challenge for me. In an era where React and similar frameworks are dominating the scene, opting for Thymeleaf might seem like a step back.

However, this decision was based on practical considerations and a clear understanding of the project's requirements and my strengths as a developer.

{#react-modern-but-not-a-one-size-fits-all-solution}

### React: Modern but Not a One-Size-Fits-All Solution {#h3-11-react-modern-but-not-a-one-size-fits-all-solution}

React is undeniably modern and powerful, but it comes with its own set of complexities. My experience with React is akin to many developers dabbling outside their comfort zone - functional but not exactly proficient.

I've seen the kind of perplexed expressions from seasoned React developers when they look at my code, much like the ones I have when I'm reading complex Java code written by others.

React's learning curve, coupled with its slower performance in certain scenarios and the risk of not achieving an aesthetically pleasing result without deep expertise, made me reconsider its suitability for [gdocweb](https://gdocweb.com/).

{#the-appeal-of-thymeleaf}

### The Appeal of Thymeleaf {#h3-12-the-appeal-of-thymeleaf}

Thymeleaf, on the other hand, offered a more straightforward approach, aligning well with the project's ethos of simplicity and efficiency. Its HTML-based interfaces, while perhaps seen as antiquated next to frameworks like React, come with substantial advantages:

1. **Simplicity in Page Flow**: Thymeleaf provides an easy-to-understand and easy-to-debug flow, making it a practical choice for a project like this.
2. **Performance and Speed**: It's known for its fast performance, which is a significant factor in providing a good user experience.
3. **No Need for NPM**: Thymeleaf eliminates the need for additional package management, reducing complexity and potential vulnerabilities.
4. **Lower Risk of Client-Side Vulnerabilities**: The server-side nature of Thymeleaf inherently reduces the risk of client-side issues.

{#considering-htmx-for-dynamic-functionality}

### Considering HTMX for Dynamic Functionality {#h3-13-considering-htmx-for-dynamic-functionality}

The idea of incorporating HTMX for some dynamic behavior in the front-end did cross my mind. HTMX has been on my radar for a while, promising to add dynamic functionalities easily. However, I had to ask myself if it was truly necessary for a tool like [gdocweb](https://gdocweb.com/), which is essentially a straightforward wizard.

My conclusion was that opting for HTMX might be more of Resume Driven Design (RDD) on my part, rather than a technical necessity.

In summary, the choice of Thymeleaf was a blend of practicality, familiarity, and efficiency. It allowed me to build a fast, simple, and effective front-end without the overhead and complexity of more modern frameworks, which, while powerful, weren't necessary for the scope of this project.

{#final-word}

Final Word {#h2-14-final-word}
------------------------------

The key takeaway in this post is the importance of practicality in technology choices.

When we're building our own projects it's much easier to experiment with newer technologies, but this is a slippery slope.

We need to keep our feet grounded in familiar territories while experimenting.

My experience with GraalVM highlights the importance of aligning technology choices with project needs and being flexible in adapting to challenges.

It's a reminder that in technology, sometimes the simpler, tried-and-tested paths can be the most effective.
