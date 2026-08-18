---
title: "Getting Started Quickly and Easily with Payara Server"
slug: "getting-started-with-payara-server"
date: "2020-11-10T09:39:02+00:00"
lastmod: "2020-11-10T09:49:53+00:00"
description: "The following four short videos will take you step-by-step through installing, writing, and deploying an application to Payara Server."
canonical: "https://www.payara.fish/learn/getting-started-with-payara/"
authors:
  - "jadon-ortlepp"
image: "Introduction-1150x647-1.png"
categories:
  - "Microservices"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
frozen: false
---

The following four short videos will take you step-by-step through installing, writing, and deploying an application to [Payara Server](https://www.payara.fish/ "Payara Server"), even if you've never used the application server before:

{#more-36262}

Introduction to Payara Server
-----------------------------

Learn how to write a simple Hello World application and deploy it to Payara Server.

{{< youtube KEsus1Ihjs4 >}}

<br />

**Install Software**

Four Software Requirements:

1. Java
2. Maven
3. IDE ([NetBeans](https://netbeans.apache.org/ "NetBeans"))
4. Payara Server Community

{{< youtube 50ujuQfho0E >}}

<br />

**Start Payara Server**

Two Methods of Starting Payara Server:

1. Using the CLI
2. Using NetBeans

{{< youtube tl8UbAETM68 >}}

<br />

**Deploy App on Payara Server**

Three Methods to Deploy an App on Payara Server:

1. Through NetBeans
2. Through the CLI
3. Through the Admin Console

{{< youtube XV1_LHUlM3s >}}

<br />

#### As an alternative, you can also follow these instructions...

Payara Server can be downloaded from the Payara website. Download to a directory of your choosing and then unzip. On Linux based systems you can use the following command:

`unzip payara-.zip`

By default, this will unzip the archive into `{CURRENT_DIR}/payara41`.

**Starting Payara Server**   

To start Payara Server, execute the following command:

`$ {PAYARA_INSTALL_DIR}/bin/asadmin start-domain`

This will start domain1 domain, which is the default domain included with Payara Server. If you wanted to start a different domain, the domain name would need to be specified. An example can be seen below:

`$ asadmin start-domain your_domain_name`

**Accessing the Administration Console**   

Once the server is up and running, type this URL <http://localhost:4848> in your browser to access the Administration Console. This is the default URL for accessing the Administration Console.

**Deploying an Application**   

In order for a web application to run, it must be first deployed on an application server such as Payara Server.

**Deploying using Asadmin**   

To deploy a WAR file, you need to use the deploy option, followed by the path to the application to deploy. See below for an example of deploying a WAR file:

`$ {PAYARA_INSTALL_DIR}/bin/asadmin deploy your_application.war`

**Deploying using Administration console**

1. Access the Administration Console by navigating to <http://localhost:4848> (make sure a domain is running beforehand)
2. Click on Applications under the heading Common Tasks pane on the left side of the page.
3. Any deployed applications are listed here. Since there are none right now, click on Deploy.
4. The current display should be the Deploy Applications or Modules page. Select Packaged File to be uploaded to the Server and click browse. Navigate to where your application is located, select the file and click Open. You should be returned to the same page with some settings listed.
5. Change any settings if needed otherwise accept the default settings and click ok to be returned to the applications page. Your application should now be listed.
6. Finally, under the action tab click launch. The default URL for application is <http://localhost:8080/> your_application.

Visit [the Payara Getting Started page](https://www.payara.fish/learn/getting-started-with-payara/ "the Payara Getting Started page") for further resources on getting started, including: Configuring, Adding a data source, Adding functionality, monitoring, security auditing, Creating a Restful Web Service, Logging, Testing Apps, etc.
