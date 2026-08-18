---
title: "Video series “JavaFX In Action”, Part 5 with Cormac Redmond (KafkIO), Brian Schlining (Annotating the Deep-Sea Wildlife), Gerrit Grunwald (JavaFX Libraries), Dirk Lemmermann (JavaFX Libraries and Applications, JFX Central)"
date: "2025-07-18T06:18:00+00:00"
lastmod: "2025-12-08T07:57:10+00:00"
description: "This is the next part in the series of \"JavaFX in Action\" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's discuss it - by Frank Delporte"
authors:
  - "frankdelporte"
  - "gerrit-grunwald"
image: "jfxinaction-part-5.jpg"
categories:
  - "Interviews"
  - "JavaFX"
tags:
related_posts:
  - "new-video-series-javafx-in-action-part-1"
  - "video-series-javafx-in-action-part-2"
  - "video-series-javafx-in-action-part-3"
  - "video-series-javafx-in-action-part-4"
frozen: false
---

This is the next part in the series of "JavaFX in Action" interviews. Are you working on a fantastic JavaFX application? Let me know, and let's discuss it in the new year!

* [July '24: Pedro Duque Vieira, Daniel Zimmermann, Christopher Schnick, and Robert Ladstätter](https://foojay.io/today/new-video-series-javafx-in-action-part-1/)
* [November '24: Maciej Gorywoda, Ramiro Domínguez Ayub, Christoph Schwentker, Ulas Ergin](https://foojay.io/today/video-series-javafx-in-action-part-2/)
* [December '24: Özkan Pakdil, Clément de Tastes, Almas Baim, Steve Hannah, Jago de Vreede](https://foojay.io/today/video-series-javafx-in-action-part-3/)
* [March '25: Mike Hearn, Sven Reimers, Chris Newland](https://foojay.io/today/video-series-javafx-in-action-part-4/)

## Cormac Redmond: KafkIO, the Kafka UI for Engineers and Admins

[Cormac Redmond](https://www.linkedin.com/in/cormacredmond/) is an "All-things Java / Spring / MicroServices" expert who has been computing and programming from a young age. He has 20 years of professional experience spanning several industries, building everything from complex distributed systems to bespoke intranets and mobile apps. He enjoys fully and deeply understanding any domain or technology and is happiest when working within cultures that value the importance of building clean, elegant, testable, self-documenting systems while adopting forward-thinking practices and techniques.

[KafkIO](https://www.kafkio.com/) is designed for easy, fast, and enjoyable use. It only takes a few clicks before you're exploring your Kafka clusters. No web servers, configuration files, clunky installation, Docker, or funky licensing! You can seamlessly create/edit/delete/dump/clear topics, produce messages, advanced search and streaming with text, offset, and date range criteria, tweak offsets, delete consumers, and browse/create/update schemas. Easily manage access control lists (ACLs). View your data in a variety of ways. Manage Kafka Connect connectors and ksqlDB (with a flexible KSQL editor). Troubleshoot issues with a clear view of the broker, topic, ksqlDB, connector, and consumer configuration, with live statistics such as partition skew, out-of-sync replicas, consumer lag, etc.

The UI is created with JavaFX and uses different libraries, which are all styled in a very consistent and easy-to-understand way. The application is beautifully designed and an excellent showcase of what can be achieved if a Java backend and JavaFX user interface are combined in one single powerful application!

{{< youtube 4qkflNl1ivA >}}

More info in this [blog post](https://webtechie.be/post/2025-02-27-jfxinaction-cormac-redmond-kafkio/).

## Brian Schlining: Annotating the Deep-Sea Wildlife

[Brian Schlining](https://www.linkedin.com/in/brianschlining/) is a Software Engineer at the [Monterey Bay Aquarium Research Institute (MBARI)](https://www.mbari.org/), specializing in designing software systems to support scientific research. This includes data systems design, database development, user interface development (desktop and web applications), GIS, image and video analysis, micro-services, and analysis of large data sets.

Within [MBARI, a full system of micro-services](https://github.com/mbari-media-management) has been developed to store a large number of deep-sea videos and images, a knowledge base about the animals living in the sea, machine learning tools, etc.

[VARS Annotation](https://docs.mbari.org/vars-annotation/) is a JavaFX user interface for creating and editing video annotations. It targets modern video workflows and is part of the MBARI Media Management software stack. It's not a standalone application but depends on several external services that need to be deployed.

{{< youtube W9cs44DHIlA >}}

More info in this [blog post](https://webtechie.be/post/2025-03-20-jfxinaction-brian-schlining-annotating-deep-sea/).

## Gerrit Grunwald: Creator of Many Amazing JavaFX Libraries

[Gerrit Grunwald](https://www.linkedin.com/in/gerritgrunwald/) loves coding for around 40 years already. He is interested in desktop, mobile and IoT projects based on all possible technologies. But above all, he loves all-things-Java. He is the founder and leader of the Java User Group Münster (Germany), JavaOne rockstar and Java Champion. As Developer Advocate at Azul, he speaks a lot at conferences and user groups all around the world.

In the video, we discuss some of the libraries Gerrit has created, but there are a lot more! Check his GitHub repositories and blog...

* [GitHub repositories](https://github.com/HanSolo?tab=repositories)
  * [TilesFX](https://github.com/HanSolo/tilesfx): A JavaFX library containing tiles for Dashboards.
  * [Medusa](https://github.com/HanSolo/medusa): A JavaFX library for Gauges.
  * [Charts](https://github.com/HanSolo/charts): A library for scientific charts in JavaFX.
  * and much more...
* [Personal blog Harmonic Code](https://harmoniccode.blogspot.com/)
* [Blog posts on Foojay.io](https://foojay.io/today/author/gerrit-grunwald/)

{{< youtube 6pgHlHLrX8c >}}

More info in this [blog post](https://webtechie.be/post/2025-04-10-jfxinaction-gerrit-grunwald-amazing-javafx-libraries/).

## Dirk Lemmermann: Creator of JavaFX Libraries and Applications

[Dirk Lemmermann](https://www.linkedin.com/in/dlemmermann/) has over 40 years of programming and 30 years of professional experience. He is a seasoned and passionate software engineer and leader with a master's degree in computer science and multiple honors and awards for his work in UI development and design, tool development, and scheduling applications.

As the CEO of Senapt and DLSC, he oversees the development of CRM systems for energy suppliers in England and Java and JavaFX consulting and software development for various domains and applications. His mission is to deliver high-quality, innovative, and user-friendly solutions that meet the needs and expectations of his clients and partners.

### JavaFX Libraries

Dirk has created, or contributed to, a lot of libraries, and most of them are available as open source libraries!

* [FlexGanttFX](https://www.jfx-central.com/libraries/flexganttfx)
* [ControlsFX](https://www.jfx-central.com/libraries/controlsfx)
* [GemsFX](https://www.jfx-central.com/libraries/gemsfx)
* [WorkbenchFX](https://www.jfx-central.com/libraries/workbenchfx)
* [CalendarFX](https://www.jfx-central.com/libraries/calendarfx)
* [GitHub repositories with more work by Dirk](https://github.com/orgs/dlsc-software-consulting-gmbh/repositories)

### JFX Central

JFX Central, the home to anything JavaFX related, is a website, desktop application, and mobile app, all based on the same code base. Both the tool itself, as the data it uses, are open source projects.

* [JFX Central, website](https://www.jfx-central.com/)
* [JFX Central, sources](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2)
* [JFX Central, data sources](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data)

### [](https://webtechie.be/images/2025/jfx-in-action/dirk/jfx-home.png)[](https://webtechie.be/images/2025/jfx-in-action/dirk/jfx-showcases.png)[](https://webtechie.be/images/2025/jfx-in-action/dirk/jfx-videos.png)Senapt Applications

[Senapt](https://www.senapt.co.uk/) provides "Energy-as-a-Service Platform", by providing an energy transactions platform for energy sellers and energy buyers in the UK. As the energy landscape has continued to change, retail electricity providers have been looking for strategies to meet the ever-growing, ever-changing demands for their customers. Senapt's products have been designed to help energy suppliers to facilitate this changing relationship and allow them to seize the opportunities of the smart grid.

{{< youtube hY7RUN2hHJA >}}

More info in this [blog post](https://webtechie.be/post/2025-05-29-jfxinaction-dirk-lemmermann-javafx-libraries-and-applications/).
