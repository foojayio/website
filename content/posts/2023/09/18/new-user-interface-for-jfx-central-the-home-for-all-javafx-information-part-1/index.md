---
title: "New User Interface for JFX Central, Home for JavaFX - Part 1"
date: "2023-09-18T05:37:45+00:00"
lastmod: "2023-09-18T06:10:25+00:00"
description: "JFX Central is the place to be for all JavaFX info. The website is a project started by Dirk Lemmermann and has been online since 2021."
canonical: "https://webtechie.be/post/2023-09-07-jfxcentral-new-user-interface/"
authors:
  - "frankdelporte"
image: "homepage-intro-1024x769-1.png"
categories:
  - "JavaFX"
related_posts:
  - "javafx-links-of-august-2023"
  - "foojay-podcast-9"
  - "foojay-podcast-25"
frozen: false
---

**Just like [Foojay](https://foojay.io/) is the starting place for all info related to Java, [JFX Central](https://www.jfx-central.com/) is the place to be for all JavaFX info. The website is a project started by Dirk Lemmermann and has been online since 2021. The [team has expanded](https://www.jfx-central.com/team) since then, and the content has been extended, partially by the team, but also thanks to many contributors from the JavaFX community. End of August, a new user interface was published to replace the initial version.**

The JFX Central project is unique in a few ways.

First and foremost, it's a complete JavaFX project running in the browser. Thanks to [JPro](https://www.jpro.one/), the JavaFX application is presented to the visitors as a plain HTML website. But the same source code is also used to provide a desktop application packaged with [Hydraulic](https://www.hydraulic.dev/).

Both the [site/app source code](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2), and all the [jfxcentral-data](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data) are available on GitHub, so anyone can contribute and see how the project is built. This same GitHub data is also fetched by the website and app, so no databases or complicated hosting services are needed.

## JFX Central Content

### Homepage

The homepage provides links to the [GitHub projects of JFX Central](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2) and its [data](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data), but also to the [latest download of the desktop application](https://downloads.hydraulic.dev/jfxcentral2/download.html) version, and a [page providing more information about OpenJFX Project](https://www.jfx-central.com/openjfx), the open source project behind JavaFX. Furthermore, this page links to randomly selected highlights of the website's various sections and the most recent "Links Of The Week."

{{< gallery >}}
homepage-intro-1024x769.png |  | Homepage of JFX Central
homepage-highlights.png |  | Some of the highlights on the homepage
homepage-lotw.png |  | The most recent Links Of The Week on the homepage
{{< /gallery >}}

### Resources

The Resources section of the JFX Central website contains an overview of libraries, tools, videos, books, blogs, tips, tutorials, and icons. All related to JavaFX.

{{< gallery >}}
books-1024x890.png |  | Books on JFX Central
tools-1024x912.png |  | Tools on JFX Central
videos-1024x1003.png |  | Videos on JFX Central
{{< /gallery >}}

The icons are a new part of version 2 of JFX Central, allowing you to search and explore many icon packs.

{{< gallery >}}
icons-overview-1024x780.png |  | Overview of the icons on JFX Central
icons-devicons-1024x832.png |  | All the icons in the Devicons pack
icons-details-1024x282.png |  | Details of a selected icon
{{< /gallery >}}

### Community

The Community section gives you more info about the people and companies in the JavaFX community. This is also the place where you can find all the previous Links Of The Week and the team involved in the JFX Central project.

{{< gallery >}}
people-1024x950.png |  | People on JFX Central
companies-1022x1024.png |  | Companies on JFX Central
linksoftheweek-1024x861.png |  | History of the Links Of The Week on JFX Central
{{< /gallery >}}

### Showcases

Head over to the Showcases section to find real-world applications based on JavaFX. This section doesn't aim to be an overview of all available applications but a summary of uses in different markets, industries, use cases, etc.

{{< gallery >}}
showcases-899x1024.png |  | Showcases on JFX Central
showcase-binjr-1024x874.png |  | The binjr showcase
showcase-nerstar-941x1024.png |  | The NERstar showcase
{{< /gallery >}}

### Documentation

The Documentation section is also a new addition to version 2 of JFX Central. The goal is to provide links to various sources that explain how to start with JavaFX and related topics.
![Documentation on JFX Central](documentation-1024x623.png)

### Downloads

The Downloads section contains a mix of libraries and apps with a direct link to the available downloads.

This section shows the power (and a bit of its weakness) of JFX Central, as it is up to the tool's creators to keep the download links up-to-date.

{{< gallery >}}
downloads-956x1024.png |  | Downloads on JFX Central
download-gluostatusfx-info-1024x774.png |  | GlucoStatusFX download page
download-gluostatusfx-links.png |  | GlucoStatusFX links
{{< /gallery >}}

### Search

The search box helps you to jump to the correct section of the website for all the different parts of the content.
![Search box with an example search](search.png)

## **Conclusion**

JFX Central is a continuously evolving website and application.

Please let us know via a [GitHub ticket if you notice a problem](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2/issues), or create a pull request in the [data project to add your JavaFX-related content](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data)!

[In part 2 of this post](https://foojay.io/today/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/), you'll meet some of the people behind the JFX Central project...
