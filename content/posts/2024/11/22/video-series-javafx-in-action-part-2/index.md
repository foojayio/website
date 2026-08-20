---
title: "Video series “JavaFX In Action”, Part 2"
date: "2024-11-22T14:31:11+00:00"
lastmod: "2024-11-22T14:31:12+00:00"
description: "In this part, we have four new interviews for you! Learn more about JavaFX combined with Scala, Swing, React, trains, scientific research, banking, and more..."
authors:
  - "frankdelporte"
image: "jfxinaction-part-2.jpg"
categories:
  - "Interviews"
  - "JavaFX"
related_posts:
  - "new-video-series-javafx-in-action-part-1"
  - "new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1"
  - "javafx-links-of-september-2024"
  - "video-series-javafx-in-action-part-6"
frozen: false
---

In [July, we published the first part, which included four interviews](https://foojay.io/today/new-video-series-javafx-in-action-part-1/) with Pedro Duque Vieira, Daniel Zimmermann, Christopher Schnick, and Robert Ladstätter.

In this part, we have four new interviews for you! Learn more about JavaFX combined with Scala, Swing, React, trains, scientific research, banking, and more...

## Maciej Gorywoda about FxCalculator

[Maciej Gorywoda](https://www.linkedin.com/in/mgorywoda/) is the product marketing manager for the IntelliJ Scala team at JetBrains. As a Scala Ambassador, he teaches Scala and makes its community more beginner-friendly. He is also self-publishing a fantasy novel.

FxCalculator is a calculator for your Android phone. It's got all the usual buttons with numbers and operators, and parentheses, as well as buttons for storing the partial results of your calculations in the memory. It also has an advanced editor mode, under the "Fx" button.

In this mode, you can write an expression with your virtual keyboard as if you were writing simple programming code. You can use variables and functions, combine them, and write custom ones. They're stored on the phone so they can be actually useful to you more than once.

{{< youtube 93OozqMTqJQ >}}

More info in [this blog post](https://webtechie.be/post/2024-09-17-jfxinaction-maciej-gorywoda/).

## Ramiro Domínguez Ayub about the Televic Generic Update Tool

[Ramiro Domínguez Ayub](https://www.linkedin.com/in/rayub143/) is a Java developer with a lot of experience in Java backend development. His roots are in Cuba, but after living in Spain and visiting Bruges a few times, he permanently moved to Belgium. Since then, he has joined Televic Rail, working on passenger information systems for trains. When he joined Televic, he started using JavaFX to develop the Televic Generic Update Tool (TGUT).

This tool is used internally at Televic Rail by the R\&D, Quality, and production teams to test devices, check installed hardware en software in a train system, perform multiple simultaneous updates, etc. Based on an XML configuration file, the available devices and software packages get visualized for each project so it becomes impossible to apply wrong updates to devices.

The same tool and configuration files are used by the end-customer to update and maintain the Televic devices (speakers, amplifiers, passenger emergency panels, LED displays, TFT screens, servers,...) which are installed in a train.

Thanks to the thread handling in the Java backend of the application, it's possible to perform a lot of those updates at the same time and visualize the progress in the JavaFX UI. This way updating e.g. 30 TFT screens at the same time becomes a quick job that's fully managed by TGUT.

{{< youtube 1gvKCS35ono >}}

More info in [this blog post](https://webtechie.be/post/2024-09-24-jfxinaction-ramiro-dominguez-ayubat/).

## Christoph Schwentker about JabRef

[Christoph Schwentker](https://www.linkedin.com/in/christoph-schwentker-723656247/) is a (Kotlin) software developer at [System Integration Laboratory GmbH (SIL) in Germany](https://www.silab.de/). He is one of the main maintainers of the JabRef project.

JabRef was founded in 2003 and has since been used by many students and researchers. Its mission is to advance knowledge and improve scientific research. JabRef values open access to information and believes modern science can be built on an open institutional structure.

JabRef is developed as free, open-source software and saves your data in a simple text-based file format with no vendor lock-in.

{{< youtube -ddFxwh2U6E >}}

More info in [this blog post](https://webtechie.be/post/2024-10-01-jfxinaction-christoph-schwentker/).

## Ulas Ergin: How JavaFX helps to migrate from Swing to React UIs, all combined in one Java app

Ulas Ergin has over 25 years of experience on large-scale enterprise systems in the banking sector, including core-banking migration, cloud transformation, and digitalization projects. He has led several teams within different domains. Currently, he manages a multicultural department in the Netherlands, Ukraine, and Turkey.

At [Credit Europe Bank](https://www.crediteuropebank.com/) two tools are used, which are created with JavaFX.

The first one is an internal tool used by the developer team to define and setup certain procedures which are used within the applications and for their deployment. This tool is fully created in JavaFX, and was at the same time a research project to gain confidence in the power of Java and JavaFX to build a user interface application.

Based on the knowledge gained from the development of the developer tool, the decision was made to use JavaFX as the foundation to migrate an old application. This resulted in a new internal tool, used by the banking team and in the call centers. The screens of the old Swing application are integrated into this new tool, and are gradually transformed into React views.

JavaFX components bind everything together and provide the main layout, menus, login screens, etc. The React views are integrated into this application with [JxBrowser](https://teamdev.com/jxbrowser/), a Chromium web browser that is fully integrated as a JavaFX component. For the call center team, the call manager was also integrated in this app, so they have all their tools available in one screen.

{{< youtube djbC7zWc-2w >}}

More info in [this blog post](https://webtechie.be/post/2024-10-22-jfxinaction-ulas-ergin/).
