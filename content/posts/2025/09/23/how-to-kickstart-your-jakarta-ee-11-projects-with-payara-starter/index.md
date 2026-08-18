---
title: "How to Kickstart Your Jakarta EE 11 Projects with Payara Starter"
date: "2025-09-23T10:26:53+00:00"
lastmod: "2025-09-23T10:26:55+00:00"
description: "Payara Starter now supports Jakarta EE 11 with a fast project setup, an ERD-based code generator, and a Jakarta Faces project generator—making enterprise app development easier than ever."
canonical: "https://blog.payara.fish/kickstart-jakarta-ee-11-projects-with-payara-starter"
authors:
  - "dominika-tasarz"
image: "Payara-Starter-04-1.webp"
categories:
  - "Gradle"
  - "Jakarta EE"
  - "Java"
  - "Java Beginner"
  - "Maven"
related_posts:
  - "transitioning-to-java-my-first-book"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "a-simple-service-with-spring-boot"
frozen: false
---

**Jakarta EE 11 is here, bringing powerful new capabilities for enterprise Java developers. But getting started quickly can be a challenge - that's where the latest Payara Starter comes in. With full Jakarta EE 11 support, an updated ERD-based code generator, and a new Jakarta Faces project generator, it gives you everything you need to launch your next application with ease.**

With Jakarta EE 11 now [officially released](https://blog.payara.fish/jakarta-ee-11-is-here-and-its-ready-for-the-cloud "officially released"), you are likely eager to explore its new capabilities but setting up your first application may take longer than you want. That's where the latest version of Payara Starter steps in, giving you a fast, hassle-free way to get a fully functional Jakarta EE 11 project up and running. Read more to learn how!

The latest updates to Payara Starter [entity relationship diagram (ERD)](https://blog.payara.fish/accelerate-app-development-with-ai "entity relationship diagram (ERD) ")and code generator brings two major enhancements, which are designed to make life easier for Java developers:

* Full Jakarta EE 11 support
* A Jakarta Faces project generator

Let's break down what this means for your next enterprise application.

### Why Payara Starter Exists

When starting a new Jakarta EE project, the initial setup to scaffold your application is often the least enjoyable part. The free, online developer tool Payara Starter removes that friction, offering an AI-powered custom project generator for modern Jakarta EE applications that helps you set up a new project in minutes.

It leverages natural language processing (NLP) to automatically create, adjust, scale up or down entity relationship diagrams (ERDs) as well as code. Once you are happy with the result, you can download the project, refine it if needed, and run the resulting Jakarta EE application on Payara Server, Payara Micro and Payara Cloud.

### What's New in Payara Starter

#### Jakarta EE 11 Support

Jakarta EE 11 is the latest evolution of the enterprise Java platform that carries forward the legacy of Java EE with a modernized, cloud-ready approach. This release is packed with updates such as:

* Enhanced CDI Lite support
* Jakarta RESTful Web Services 4.0 improvements
* New Jakarta Data specification
* Modernized Jakarta Security, Jakarta Faces APIs and much more!

With the launch of Jakarta EE 11, our team worked hard to ensure the latest Payara Starter release can help you instantly bootstrap Jakarta EE 11 applications on the Payara Platform, complete with all the right Maven/Gradle coordinates and configuration.

With Payara Starter, you don't need to manually track down compatible dependencies or tweak Maven/Gradle settings. Choose Jakarta EE 11 in the generator, and you'll get:

* The correct Jakarta EE 11 BOM
* Preconfigured dependencies aligned with Payara Server or Payara Micro
* Example code with the new APIs

Your development/testing project is ready to run out of the box. For use in production, make sure you review the AI-generated code.

#### Jakarta Faces Generator

Building a server-side rendered user interfaces (UIs) ? Payara Starter now has a dedicated template to kickstart applications based on Jakarta Faces, previously known as Jakarta Server Faces. As Payara Starter now supports JSF-based applications, you can benefit from additional features to standard HTML.

Instead of wiring everything up manually, you get an immediately runnable UI that is perfect for demos, proof of concepts (PoC), minimum viable products (MVPs) or internal tools.

### How to Create Jakarta EE 11 Apps with Payara Starter

1. Visit [the Payara Starter website](https://start.payara.fish/ "the Payara Starter website")
2. Pick your build tool (Maven or Gradle)
3. Choose your Jakarta EE version (Jakarta EE 11) and profile type (Full, Core or Web)
4. Select the Payara Platform runtime option, including the version you prefer
5. Select the Java SE version you want to use
6. Select optional modules for MicroProfile and Deployment Options.
7. Select a suitable ER diagram and pick any additional feature, including Jakarta Faces under the "Web" section
8. Remember that you can adjust the ERD and code by clicking "Diagram Builder \& Live Preview"
9. Add any necessary security configuration you may want for your application
10. Download your project and refine the code - you'll have a running Jakarta EE 11 app in minutes.

### Why This Matters for Java Developers

In short: The recent updates to Payara Starter make the developer tool even more powerful, helping you to get started learning about the latest Jakarta EE version while fast tracking Jakarta EE 11 application development, without manual boilerplate setup.

Try it today and skip straight to writing modern code .

[Original article by Gaurav Gupta published on Payara Blog.](https://blog.payara.fish/kickstart-jakarta-ee-11-projects-with-payara-starter "Original article by Gaurav Gupta published on Payara Blog.")
