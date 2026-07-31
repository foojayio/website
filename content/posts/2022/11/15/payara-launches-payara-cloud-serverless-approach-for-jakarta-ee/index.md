---
title: "Payara Launches Payara Cloud – Serverless for Jakarta EE"
slug: "payara-launches-payara-cloud-serverless-approach-for-jakarta-ee"
date: "2022-11-15T16:31:30+00:00"
lastmod: "2022-11-15T16:33:26+00:00"
description: "Payara has launched a new product Payara Cloud, designed to run Jakarta EE apps easily and quickly on the cloud."
canonical: "https://www.payara.fish/teamblog/2022/11/07/payara-launches-payara-cloud-serverless-approach-for-jakarta-ee/"
authors:
  - "jadon-ortlepp"
image: "2.-Payara-Services-Group-Photo-2.jpg"
categories:
  - "Cloud"
  - "Jakarta EE"
  - "Payara"
  - "Release Notes"
  - "Tools"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "do-java-jakarta-ee-standards-matter"
frozen: false
---

The cloud native application runtime automates tasks like Kubernetes deployment, ingress and YAML. {#h2-0-the-cloud-native-application-runtime-automates-tasks-like-kubernetes-deployment-ingress-and-yaml}
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Payara has launched a new product [++Payara Cloud++](https://www.globenewswire.com/Tracker?data=vWpu_qnRrkJcVsDvJOwJah09cL0__ihD70JQn1PC6vbdZhbW7zbNxo2ghVnHWkXX3jO6POtRb8Gjgiaa-vrN66gNjzvUPyiITCnoWqKAaYV4D440OmdOMwAVS0lLCo76), designed to run Jakarta EE apps easily and quickly on the cloud.

Developers simply upload any web application following the Jakarta Web Profile specification and run it in a containerized Payara Micro instance.

Payara Cloud deals with infrastructure, allowing them to build the code and concentrate on business logic.

**"*Developer experience is crucial to us, and with Payara Cloud we are responding to the growing popularity of platform engineering and its integrated products to deal with operational necessities.*
*Payara Cloud caters to this growing demand for tools that automate infrastructural tasks; improving efficiency and shortening development cycles*,"** said Steve Millidge, Payara Founder and CEO.  
![Payara Cloud logo](https://www.payara.fish/payara-site/media/gb/Payara-Cloud-logo-500x216.png)  
![Steve Millidge presenting Payara Cloud](https://www.payara.fish/payara-site/media/gb/Steve-Millidge-presenting-Payara-Cloud-500x310.jpg)

Payara Cloud is "serverless", taking care of server issues on the behalf of users. It utilizes the key [++Jakarta EE++](https://www.globenewswire.com/Tracker?data=baxDHPSzzWH-z0qa1hhkwFMIF7UZndCDv6XXdmt4WkJYeHpJrt9C6eGsU-BkRNeDLQ7vxiv_1fY2-J_Ofqu6_A==) (previously J2EE, and Java EE) concept of a deployable application, isolated from infrastructure. It runs on Microsoft Azure, building a runtime on Azure Kubernetes Service.

The software takes the user's WAR file, packages it into a Docker image with Payara Micro -- Payara's microservices runtime -- and completes all YAML, builds container images, creates a pod, deploys it on Kubernetes, updates the API server to manage ingress on Microsoft Azure and creates an SSL certificate for the application. It successfully manages all the infrastructural tasks related to the cloud. The developer selects their WAR, clicks deploy, and watches the apps run in the cloud.

**"*This is an exciting step for both Payara and Jakarta EE, providing a flexible and powerful route to serverless for the enterprise Java community. Jakarta EE is primed to support the development of modern cloud-native applications. Payara Cloud will facilitate this mission, helping many enterprises leverage existing Jakarta EE skills to simplify their cloud infrastructure management. By playing a key role in Jakarta EE as a Strategic Member of the Working Group, Payara is able to exchange best practices with other leaders in the industry, collaborate and promote new ideas -- we empower members to innovate with potentially game-changing products like Payara Cloud*,"** said [++Mike Milinkovich++](https://www.globenewswire.com/Tracker?data=CGYok-BeWNb3s_GHIFsGuYxW5qUH1ni3I3GYnD16tBta4dkhmFF3P-UJMiHMLkmceSAsxSKdcLhbWiuf8-HpP_fclbrtldjN4HJ_POQ-3TWlv_dOgPA5akSltry8PMZl), Executive Director of the Eclipse Foundation.

Usually, app developers perform everything from writing the code to deploying the application and many face challenges when moving and managing their application on an external infrastructure.

Payara's innovative serverless runtime solution is unique in the market. Unlike Function As a Service, automated cloud provisioning services or raw Kubernetes, Payara Cloud takes care of the installation, configuration, deployment and scaling of Jakarta EE applications.

**"*What I really like about this approach is, for example, we have Helidon and Quarkus, they try to shift the deployment to the build time. Payara Cloud is completely different. It uses what application servers do the best: the separation of the infrastructure from the business logic,*"** emphasized [++Adam Bien++](https://www.globenewswire.com/Tracker?data=A0QxtFZlmK5hxO2ovI6UKvF7r2iL_oTdJ6_BPp_69khtvws3gdTEZSxc6zFH58GsEby9zpDJkSgVFdZ3nNZUmg==), Java Champion and Java/Jakarta EE consultant, podcaster, and blogger, giving his verdict after testing Payara Cloud.

Jakarta EE developers can use the Payara Cloud user interface and/or integrate it in continuous deployment pipelines using a GitHub Action Workflow and Payara Cloud Command Line (PCL). They will also in future be able to integrate Payara Cloud with their external Kubernetes platform -- Kubernetes itself, OpenShift, or Rancher for example.

Quotes on Payara Cloud:

**Ivar Grimstad is a Java Champion \& Developer Advocate at the Eclipse Foundation:**

"*There aren't many comparable products like Payara Cloud out there today. Payara Cloud is simpler. It's easier. And, it's just straightforward to use for developers!*"

**David Heffelfinger, Principal Consultant at Ensode Technology, Java Champion:**

*"I deployed a simple WAR file using MicroProfile APIs and Payara Cloud. It worked as a charm."*

We are running a Payara Cloud webinar to answer all your questions and explain the concept:

[**Payara Cloud -- Realizing the Potential of Cloud Native Serverless Jakarta EE**](https://us02web.zoom.us/webinar/register/WN_AKWjBlnISz69FH7yp5lkzA) {#h2-1-payara-cloud-realizing-the-potential-of-cloud-native-serverless-jakarta-ee}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

### Nov 18, 2022 03:00 PM {#h3-2-nov-18-2022-03-00-pm}
