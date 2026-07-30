---
title: "Ignore Infrastructure, Focus on Code, with Jakarta EE and Payara"
slug: "ignore-infrastructure-concentrate-on-code-with-jakarta-ee-and-payara-cloud"
date: "2022-08-09T09:59:12+00:00"
lastmod: "2022-08-09T10:07:14+00:00"
description: "What used to take days or weeks of frustration getting the infrastructure set up to run an application in the Cloud is now automated when you use Payara Cloud."
canonical: "https://blog.payara.fish/payara-cloud-and-jakarta-ee"
authors:
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2022/08/Benefits-of-Payara-Cloud-png.png"
categories:
  - "Cloud"
  - "Jakarta EE"
  - "Payara"
tags:
related_posts:
frozen: false
---

Java EE, now Jakarta EE, makes it possible for developers to focus purely on the development of a Java enterprise application, solving the business logic without needing to think about infrastructure and operations when writing code.

Payara Cloud extends this philosophy by also eliminating the need to worry about infrastructure and operations when preparing your application to run in the cloud.

Together, Jakarta EE and Payara Cloud let you focus on writing code and delivering solutions to business challenges from the moment you write your first piece of code to the day people are using your application successfully on the cloud.

**Note:** We are currently offering limited free previews of Payara Cloud to businesses. Follow this link -- [Payara Cloud Preview](https://blog.payara.fish/cs/c/?cta_guid=b2178cf7-a815-4dca-b3e9-58c54aea265f&signature=AAH58kHDuQo6rYX_fbWW4uh9mYZokO0MZg&pageId=56769072996&placement_guid=a2652686-6a2d-4fd5-87f2-eb2d093f233a&click=3361da67-5f24-4357-b1f2-73dd72d5f573&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fpayara-cloud-and-jakarta-ee&utm_referrer=https%3A%2F%2Fblog.payara.fish%2F&portal_id=334594&redirect_url=APefjpFr9eFLySTcF0wYERP83Vp17SFMy8DgAzab5zdYY-_07PCodPa4IPaSbHyWPiOhYHo76I8CKlQvkY5Qjzg-vu4nSxBkfXbxX3mk0vHHRSJu0fnz6QFrxRxvwjjVvShgL0IGSkXM4lTMGZ-fP6k98iGwew9XhiLlejcowQFn57d2w3_rupNLqt43Qe9x2sOofuQmhN3cY2yBEyPRdhtA8q90bhUKoR07BhEFg0YYp5Dz7IS2jds_FRp1Qj2uSiq3FfiFF3CFWgxO4Hsg06aXDbVHa2W4vkuKvByp0zvkY2il0E5Jpqk&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1658840682463.1658844016067.157&__hssc=229474563.12.1658844016067&__hsfp=812266229&contentType=blog-post).

After creating your application, developers (or anyone else, really) can upload it to [Payara Cloud](https://www.payara.fish/products/payara-cloud/?utm_term=&utm_campaign=Weblogic+migration&utm_source=adwords&utm_medium=ppc&hsa_acc=2033025017&hsa_cam=10508961560&hsa_grp=106831447391&hsa_ad=447374669707&hsa_src=g&hsa_tgt=aud-920735757106:dsa-987869216121&hsa_kw=&hsa_mt=&hsa_net=adwords&hsa_ver=3&gclid=CjwKCAjwrNmWBhA4EiwAHbjEQBL6_BolDmZOSZZDjP7a89cFWLEhbbFr5sQdxxW17mSaRuOxmkC1thoC5cQQAvD_BwE) to deliver the app to users, without dealing with the provisioning of the [Jakarta EE](https://jakarta.ee/) runtime, setting up containers, setting up the Pod, setting up TLS and SSL certificates, or making it work with Kubernetes.

What used to take days or weeks of frustration getting the infrastructure set up to run an application in the Cloud is now automated when you use Payara Cloud.

With Jakarta EE and Payara Cloud, developers can finally focus on what they do best: write applications that solve business challenges.

Application-Focused Jakarta EE Model {#h2-0-application-focused-jakarta-ee-model}
---------------------------------------------------------------------------------

Jakarta EE is a [set of specifications](https://blog.payara.fish/jakarta-ee-java-ee-guide) that allow Java developers to work on Java enterprise applications.

It is developed by industry leaders and designed to work well in relation to cloud-native deployment.

By using Java with Jakarta EE specifications, an application server can be installed, configured, and supported by the operations team. They setup the environment and prepare the connection to the database.

The name for this connection is a JNDI name in your environment and it just needs to be referenced in a configuration or descriptor files by your application. This has the added advantage that your application can be deployed unaltered in many environments, like test and production, as the application server configuration can point to the correct database in either the test or production environment.

The Jakarta EE specifications simplify the work of database connection and environment setup. You are ready to start creating your business-specific app.

Another way that Jakarta EE lets you focus on application logic is the integrated specifications, already built-into Jakarta EE.

With the Web Profile, providing Web-based clients like REST endpoints and UI-based interfaces with Jakarta Faces, and in the Full Profile, which also supports other communications like the usage of Messaging protocols, Jakarta EE offers a set of functionalities from different areas that are ready-to-use.

Developers don't need to add more libraries to provide some functionality and then deal with the difficulties of integrating them since everything is already in place - so the developer can simply focus on solving the business challenges.
![](https://blog.payara.fish/hubfs/Benefits%20of%20jakarta%20EE%20model%20(2)-png.png)

Easing the Pressure on the "Plate-Spinning" DevOps Engineer {#h2-1-easing-the-pressure-on-the-plate-spinning-devops-engineer}
-----------------------------------------------------------------------------------------------------------------------------

The popularity of a DevOps method makes it even more useful if infrastructure elements can be dealt with separately to application development.

This is because with [DevOps,](https://www.payara.fish/solutions/devops-and-the-payara-platform/) developers are more likely to be involved in setting up and maintaining the infrastructure. DevOps tends to see a move from different groups of people being responsible for each different part of the application set-up process, to working as a single team to solve the problem. Developing the application process and putting it on a production server is a joint task for many people.

As a result of the DevOps method, developers are confronted more often with infrastructure-related tasks. They need to learn more about the frameworks and infrastructure concepts. They are spinning so many plates and juggling different priorities. Using specifications that reduce the stress of setting up an application's infrastructure is a welcome invitation to spend more time writing the core code.

Avoid Infrastructure Infiltration {#h2-2-avoid-infrastructure-infiltration}
---------------------------------------------------------------------------

Separating the infrastructure from the code using a Jakarta EE application server also avoids the situation where the infrastructure finds its way into the the application itself, making it difficult to change later on.

If you do not just refer to available resources, as you can do with the database connection we mentioned earlier, you make a direct and hard-coded dependency on some infrastructure component. Then you cannot easily change to another system and you need to perform much more maintenance as you probably need to rework the integration when you need to upgrade to a newer version of the integration.

DevOps sparked the creation of Infrastructure as Code (IaC). Since developers are good at writing applications, they approached the new challenge of dealing with the infrastructure in a way they are comfortable with, writing code!

But the environments can already be managed using command-line tools. So, this method requires developing and maintaining the IaC in addition to the CLI tools, which creates additional work and one more thing the developer needs to learn.

The Jakarta EE model avoids: infrastructure swallowing up developers' time, requiring them to learn a host of new skills; the potential problems of intermingled infrastructure and code; the constant extra maintenance of Infrastructure as Code.

And what Jakarta EE does for application set up, Payara Cloud does for getting your web applications to run in the cloud.

Taking the Jakarta EE Deployment Model into the Cloud Era with Payara Cloud {#h2-3-taking-the-jakarta-ee-deployment-model-into-the-cloud-era-with-payara-cloud}
---------------------------------------------------------------------------------------------------------------------------------------------------------------

Using a cloud environment brings a whole new set of things to learn, like [Kubernetes](https://kubernetes.io/), routing, and setting up secure communication - to name just a few.

Payara Cloud is a serverless Platform-as-a-Service (PaaS) that allows you to build and run Jakarta EE Web apps without dealing with infrastructure management.

Payara Cloud scans your application for database usage and configuration parameters defined using the [MicroProfile](https://microprofile.io/) Config specification, and then presents you with a configuration screen to enter these values. That's all you need to do to connect to your database and deploy the application.

The provisioning of the Kubernetes resources, setting up the routing, networking aspects, and providing the SSL certificate for your endpoints are all handled for you. It brings the serverless architecture idea to Jakarta EE, you just need to configure and deploy the application.

Some other Platform As a Service solutions come close to this concept, but with Payara Cloud, the entire infrastructure is shielded away from the user:

![](https://blog.payara.fish/hubfs/Benefits%20of%20Payara%20Cloud-png.png)Jakarta EE + Payara Cloud Lets You Focus on Functionality {#h2-4-jakarta-ee-payara-cloud-lets-you-focus-on-functionality}
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

For an end-user, the provided functionality of an application is the most important aspect. Traditionally, Jakarta EE developers could focus on the most important aspect of development, which is the business logic that supplies the functionality of the finished application, while keeping the operations and deployments separated from the development task.

With the rise of the DevOps movement, better communications emerged between the developers and the operations department. But it also meant that infrastructure elements were introduced into the development process and that diverted the developers' focus away from application development.

Cloud environments and the cloud-native approach also require that people learn new frameworks and processes, further distracting developers from coding - as most of the new frameworks and processes that need to be learned are not really focused on the actual goal of giving the end-users the required functionality in the finished application.

But now with Payara Cloud, we bring the Jakarta EE deployment model to the cloud environment.

As a developer, you can once again focus on creating the web application based on the Jakarta Web Profile without worrying about the infrastructure.

All of the infrastructure parts are taken care of for you based on the configuration value you provide during the upload of your WAR.

All the Kubernetes interactions and Cloud Provider supplied functionality is taken care of for you, too, letting you run your applications in the cloud with ease.

**Sign Up for a Free Preview, Limited Time Only:** [Payara Cloud Preview](https://blog.payara.fish/cs/c/?cta_guid=b2178cf7-a815-4dca-b3e9-58c54aea265f&signature=AAH58kHDuQo6rYX_fbWW4uh9mYZokO0MZg&pageId=56769072996&placement_guid=a2652686-6a2d-4fd5-87f2-eb2d093f233a&click=3361da67-5f24-4357-b1f2-73dd72d5f573&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fpayara-cloud-and-jakarta-ee&utm_referrer=https%3A%2F%2Fblog.payara.fish%2F&portal_id=334594&redirect_url=APefjpFr9eFLySTcF0wYERP83Vp17SFMy8DgAzab5zdYY-_07PCodPa4IPaSbHyWPiOhYHo76I8CKlQvkY5Qjzg-vu4nSxBkfXbxX3mk0vHHRSJu0fnz6QFrxRxvwjjVvShgL0IGSkXM4lTMGZ-fP6k98iGwew9XhiLlejcowQFn57d2w3_rupNLqt43Qe9x2sOofuQmhN3cY2yBEyPRdhtA8q90bhUKoR07BhEFg0YYp5Dz7IS2jds_FRp1Qj2uSiq3FfiFF3CFWgxO4Hsg06aXDbVHa2W4vkuKvByp0zvkY2il0E5Jpqk&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1658840682463.1658844016067.157&__hssc=229474563.12.1658844016067&__hsfp=812266229&contentType=blog-post)
