---
title: "New User Interface for JFX Central, Home for JavaFX - Part 1"
slug: "new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1"
date: "2023-09-18T05:37:45+00:00"
lastmod: "2023-09-18T06:10:25+00:00"
description: "JFX Central is the place to be for all JavaFX info. The website is a project started by Dirk Lemmermann and has been online since 2021."
canonical: "https://webtechie.be/post/2023-09-07-jfxcentral-new-user-interface/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/homepage-intro-1024x769-1.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

**Just like [Foojay](https://foojay.io/) is the starting place for all info related to Java, [JFX Central](https://www.jfx-central.com/) is the place to be for all JavaFX info. The website is a project started by Dirk Lemmermann and has been online since 2021. The [team has expanded](https://www.jfx-central.com/team) since then, and the content has been extended, partially by the team, but also thanks to many contributors from the JavaFX community. End of August, a new user interface was published to replace the initial version.**

The JFX Central project is unique in a few ways.

First and foremost, it's a complete JavaFX project running in the browser. Thanks to [JPro](https://www.jpro.one/), the JavaFX application is presented to the visitors as a plain HTML website. But the same source code is also used to provide a desktop application packaged with [Hydraulic](https://www.hydraulic.dev/).

Both the [site/app source code](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2), and all the [jfxcentral-data](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data) are available on GitHub, so anyone can contribute and see how the project is built. This same GitHub data is also fetched by the website and app, so no databases or complicated hosting services are needed.

JFX Central Content {#h2-0-jfx-central-content}
-----------------------------------------------

### Homepage {#h3-1-homepage}

The homepage provides links to the [GitHub projects of JFX Central](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2) and its [data](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data), but also to the [latest download of the desktop application](https://downloads.hydraulic.dev/jfxcentral2/download.html) version, and a [page providing more information about OpenJFX Project](https://www.jfx-central.com/openjfx), the open source project behind JavaFX. Furthermore, this page links to randomly selected highlights of the website's various sections and the most recent "Links Of The Week."

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-intro.png" target="_blank" rel="noopener"><img fetchpriority="high" decoding="async" width="1024" height="769" data-id="102243" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-intro-1024x769.png" alt="Homepage of JFX Central" class="wp-image-102243"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-highlights.png" target="_blank" rel="noopener"><img decoding="async" width="903" height="957" data-id="102241" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-highlights.png" alt="Some of the highlights on the homepage" class="wp-image-102241"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-lotw.png" target="_blank" rel="noopener"><img decoding="async" width="891" height="941" data-id="102242" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/homepage-lotw.png" alt="The most recent Links Of The Week on the homepage" class="wp-image-102242"></a>
 </figure>
</figure>

### Resources {#h3-2-resources}

The Resources section of the JFX Central website contains an overview of libraries, tools, videos, books, blogs, tips, tutorials, and icons. All related to JavaFX.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-2 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/books.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="890" data-id="102244" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/books-1024x890.png" alt="Books on JFX Central" class="wp-image-102244"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/tools.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="912" data-id="102246" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/tools-1024x912.png" alt="Tools on JFX Central" class="wp-image-102246"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/videos.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="1003" data-id="102245" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/videos-1024x1003.png" alt="Videos on JFX Central" class="wp-image-102245"></a>
 </figure>
</figure>

The icons are a new part of version 2 of JFX Central, allowing you to search and explore many icon packs.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-3 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-overview.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="780" data-id="102248" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-overview-1024x780.png" alt="Overview of the icons on JFX Central" class="wp-image-102248"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-devicons.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="832" data-id="102249" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-devicons-1024x832.png" alt="All the icons in the Devicons pack" class="wp-image-102249"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-details.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="282" data-id="102247" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/icons-details-1024x282.png" alt="Details of a selected icon" class="wp-image-102247"></a>
 </figure>
</figure>

### Community {#h3-3-community}

The Community section gives you more info about the people and companies in the JavaFX community. This is also the place where you can find all the previous Links Of The Week and the team involved in the JFX Central project.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-4 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/people.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="950" data-id="102252" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/people-1024x950.png" alt="People on JFX Central" class="wp-image-102252"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/companies.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1022" height="1024" data-id="102250" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/companies-1022x1024.png" alt="Companies on JFX Central" class="wp-image-102250"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/linksoftheweek.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="861" data-id="102251" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/linksoftheweek-1024x861.png" alt="History of the Links Of The Week on JFX Central" class="wp-image-102251"></a>
 </figure>
</figure>

### Showcases {#h3-4-showcases}

Head over to the Showcases section to find real-world applications based on JavaFX. This section doesn't aim to be an overview of all available applications but a summary of uses in different markets, industries, use cases, etc.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-5 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcases.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="899" height="1024" data-id="102253" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcases-899x1024.png" alt="Showcases on JFX Central" class="wp-image-102253"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcase-binjr.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="874" data-id="102255" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcase-binjr-1024x874.png" alt="The binjr showcase" class="wp-image-102255"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcase-nerstar.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="941" height="1024" data-id="102254" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/showcase-nerstar-941x1024.png" alt="The NERstar showcase" class="wp-image-102254"></a>
 </figure>
</figure>

### Documentation {#h3-5-documentation}

The Documentation section is also a new addition to version 2 of JFX Central. The goal is to provide links to various sources that explain how to start with JavaFX and related topics.
![Documentation on JFX Central](/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/documentation-1024x623.png)

### Downloads {#h3-6-downloads}

The Downloads section contains a mix of libraries and apps with a direct link to the available downloads.

This section shows the power (and a bit of its weakness) of JFX Central, as it is up to the tool's creators to keep the download links up-to-date.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-6 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/downloads.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="956" height="1024" data-id="102258" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/downloads-956x1024.png" alt="Downloads on JFX Central" class="wp-image-102258"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/download-gluostatusfx-info.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="774" data-id="102259" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/download-gluostatusfx-info-1024x774.png" alt="GlucoStatusFX download page" class="wp-image-102259"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/download-gluostatusfx-links.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="705" height="403" data-id="102257" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/download-gluostatusfx-links.png" alt="GlucoStatusFX links" class="wp-image-102257"></a>
 </figure>
</figure>

### Search {#h3-7-search}

The search box helps you to jump to the correct section of the website for all the different parts of the content.
![Search box with an example search](/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/search.png)

**Conclusion** {#h2-8-conclusion}
---------------------------------

JFX Central is a continuously evolving website and application.

Please let us know via a [GitHub ticket if you notice a problem](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2/issues), or create a pull request in the [data project to add your JavaFX-related content](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data)!

[In part 2 of this post](https://foojay.io/today/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/), you'll meet some of the people behind the JFX Central project...
