---
title: "The DiscoAPI Helps You To Find Any OpenJDK Distribution"
date: "2023-09-12T03:42:44+00:00"
lastmod: "2023-09-12T03:45:38+00:00"
description: "Foojay is not only a human-readable OpenJDK knowledge base but also provides the Disco API that lets you search all OpenJDK distributions?"
authors:
  - "frankdelporte"
image: "discoapi-logo.png"
categories:
  - "Developer Tools"
  - "DevOps"
  - "Foojay"
  - "Java"
  - "Java Core"
  - "JavaFX"
  - "Tools"
related_posts:
  - "jdkmon-your-friendly-jdk-distribution-updater"
  - "foojay-podcast-4"
  - "installing-java-with-sdkman-on-raspberry-pi"
  - "where-production-policy-belongs-building-eliya-in-public"
frozen: false
---

**Did you know Foojay is not only a human-readable OpenJDK knowledge base but also provides the Disco API that lets you search all OpenJDK distributions? And even if you didn't know, you probably already are using it, as it's integrated into [SDKMAN!](https://sdkman.io/), [JReleaser](https://jreleaser.org/), [JBang!](https://www.jbang.dev/), [Gradle toolchain](https://docs.gradle.org/current/userguide/toolchains.html), [Paketo buildpacks](https://paketo.io/docs/howto/java/),and more.**

The Disco API (i.e., the short name for the Universal OpenJDK Discovery API) is a database and API with all the available OpenJDK versions from all distributors, like Corretto, Liberica, Oracle, Temurin, Zulu, and many others. In total, there are now 89352 different packages at the end of August 2023.

Most of the tools described here are developed by Gerrit Grunwald (aka hansolo on [Twitter/X](https://twitter.com/hansolo_) and [Mastodon](https://foojay.social/@hansolo_@mastodon.social)). As they are all open source, you can review them and propose improvements.

## Learn More About OpenJDK Versions and Distributions

The Devoxx UK presentation ["Welcome to the Jungle" by Gerrit Grunwald](https://www.youtube.com/watch?v=7kURkyISzyM), gives you a lot of insights into the many OpenJDK versions and distributions and how the Disco API provides all the related information.

{{< youtube 7kURkyISzyM >}}

## Disco API

### Sources

The Disco API is a Java project (of course...) you can find on [GitHub](https://github.com/foojayio/discoapi). The sources link to a [slideshow presentation with more details](https://de.slideshare.net/han_solo/disco-api-openjdk-distributions-as-a-service).

### Rest Endpoints

* <https://api.foojay.io/disco/v3.0/major_versions>
* <https://api.foojay.io/disco/v3.0/distributions>
* <https://api.foojay.io/disco/v3.0/packages>
* <https://api.foojay.io/disco/v3.0/packages/jdks>
* <https://api.foojay.io/disco/v3.0/packages/jres>
* <https://api.foojay.io/disco/v3.0/ids>

These endpoints are fully documented in the [README of the GitHub project](https://github.com/foojayio/discoapi) and can be called directly from a [Swagger UI](https://api.foojay.io/swagger-ui).

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="swagger-apis.png" target="_blank" rel="noopener"><img fetchpriority="high" decoding="async" width="802" height="1024" data-id="102105" src="swagger-apis-802x1024.png" alt="Overview of all the APIs in Swagger" class="wp-image-102105"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="swagger-major-versions.png" target="_blank" rel="noopener"><img decoding="async" width="1024" height="678" data-id="102103" src="swagger-major-versions-1024x678.png" alt="Swagger result with all major versions" class="wp-image-102103"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="swagger-packages.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="623" data-id="102104" src="swagger-packages-1024x623.png" alt="Swagger result of package search" class="wp-image-102104"></a>
 </figure>
</figure>

Let's look at a few examples.

#### Get All Maintained Major Versions

Every 6 months, we get a new version of Java, so we can expect 21 later in September. Via the API we can check all the major versions which are currently maintained.

```
curl -X 'GET' 
  'https://api.foojay.io/disco/v3.0/major_versions?maintained=true' 
  -H 'accept: application/json'
```

This will return the following result, in which we can see that version 22 is already available as Early Access (EA).

```
{
    "result":[
    {
        "major_version":22,
        "term_of_support":"STS",
        "maintained":true,
        "early_access_only":true,
        "release_status":"ea",
        "versions": [
            "22-ea+12",
            ...,
            "22-ea+1"
        ]
    },
    {
        "major_version":21,
        "term_of_support":"LTS",
        "maintained":true,
        "early_access_only":true,
        "release_status":"ea",
         "versions": [
            "21-ea+35",
            ...
        ]
    },
    ...
  ],
  "message":""
}
```

#### Get Packages For Filter

The packages API provides a long list of filters. This examples searches for all the packages for:

* Version 17
* JDK only (not JRE)
* With JavaFX bundled
* AARCH64 architecture
* For macOS
* Available as dmg or pkg

```
curl -X 'GET' 
  'https://api.foojay.io/disco/v3.0/packages?version=17&architecture=aarch64&archive_type=dmg&archive_type=pkg&package_type=jdk&operating_system=macos&javafx_bundled=true&latest=available' 
  -H 'accept: application/json'
```

This filter will return the following result:

```
{
    "result":[
        {
            "id":"10140b8f207d245186992f9095c100a0",
            "archive_type":"dmg",
            "distribution":"zulu",
            "major_version":17,
            "java_version":"17.0.8+7",
            "distribution_version":"17.44.15",
            "jdk_version":17,
            "latest_build_available":true,
            "release_status":"ga",
            "term_of_support":"lts",
            "operating_system":"macos",
            "lib_c_type":"libc",
            "architecture":"aarch64",
            "fpu":"unknown",
            "package_type":"jdk",
            "javafx_bundled":true,
            "directly_downloadable":true,
            "filename":"zulu17.44.15-ca-fx-jdk17.0.8-macosx_aarch64.dmg",
            "links":{
                "pkg_info_uri":"https://api.foojay.io/disco/v3.0/ids/10140b8f207d245186992f9095c100a0","pkg_download_redirect":"https://api.foojay.io/disco/v3.0/ids/10140b8f207d245186992f9095c100a0/redirect"
            },
            "free_use_in_production":true,
            "tck_tested":"yes",
            "tck_cert_uri":"https://cdn.azul.com/zulu/pdf/cert.zulu17.44.15-ca-fx-jdk17.0.8-macosx_aarch64.dmg.pdf",
            "aqavit_certified":"unknown",
            "aqavit_cert_uri":"",
            "size":306843401,
            "feature":[]
        },
        ...
        {
            "id":"c66e5d0b8f54852a63db3369eb0ab8a9",
            "archive_type":"pkg",
            "distribution":"liberica",
            "major_version":17,
            ...
        }
    ],
    "message":"4 package(s) found"
}
```

## Tools

As mentioned in the introduction, the Disco API is integrated into many tools you may already be using. But there are even more!

### IDE Plugins

There are several plugins and extensions available that already make use of the Disco API, and that can help you to get the JDK of your choice even faster.

* IntelliJ Idea: [DiscoIdea](https://plugins.jetbrains.com/plugin/16787-discoidea)
* Eclipse: [DiscoEclipse](https://marketplace.eclipse.org/content/discoeclipse)
* Visual Studio Code: [DiscoVSC](https://marketplace.visualstudio.com/items?itemName=GerritGrunwald.discovsc&ssr=false#overview)
* [Apache NetBeans](https://netbeans.apache.org/) (no plugin needed, the API is built into it)

### JDK Mon

JDKMon is another little tool written in JavaFX that tries to detect all JDKs installed on your machine and will inform you about new updates and vulnerabilities of each OpenJDK distribution it has found. In addition, JDKMon is also able to monitor JavaFX SDK versions that are installed on your machine. You can download the latest version from the [GitHub repository](https://github.com/HanSolo/JDKMon/releases).

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-2 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="jdkmon-found.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="984" height="876" data-id="102108" src="jdkmon-found.png" alt="JDKMon showing all the found OpenJDK distributions on the machine" class="wp-image-102108"></a>
 </figure>
 <figure class="wp-block-image size-thumbnail">
  <a href="jdkmon-vulnerabilities.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="300" height="260" data-id="102107" src="jdkmon-vulnerabilities-300x260.png" alt="Alert showing the vulnerabilities in an OpenJDK package" class="wp-image-102107"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="jdkmon-download.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="620" height="824" data-id="102106" src="jdkmon-download.png" alt="Dark/Light view of the download screen" class="wp-image-102106"></a>
 </figure>
</figure>

### GitHub Actions

To be able to use any OpenJDK distribution in a GitHub Action, you can't use the well-known `actions/setup-java@v2` as it only allows you to use the adopt, adopt-openj9, and zulu distributions. Thanks to `foojay2020/setup-java@disco`, that works with the same arguments, you can use all the distributions that are available in the Disco API.

```
    steps:
    - uses: actions/checkout@v1
    - name: Set up JDK
      uses: foojay2020/setup-java@disco
      with:
        java-package: jdk
        java-version: 11.0.10
        distro: zulu
    - name: Build with Maven
      run: ./mvnw package
```

You can check all available combinations on the [Disco Testing Matrix](https://github.com/foojayio/discoTestingMatrix). It verifies JDK tests on various distros and versions using Github Actions.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-3 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="matrix-overview.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="774" height="961" data-id="102109" src="matrix-overview.png" alt="Part of the overview, the actual page is much longer..." class="wp-image-102109"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="matrix-workflows.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="928" data-id="102111" src="matrix-workflows-1024x928.png" alt="A part of the GitHub Actions" class="wp-image-102111"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="matrix-action.png" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="1024" height="638" data-id="102110" src="matrix-action-1024x638.png" alt="One of the GitHub Actions" class="wp-image-102110"></a>
 </figure>
</figure>

## Conclusion

Foojay wants to support the OpenJDK community not only with blogs, podcasts, command line options, and other readable content.

It also wants to make sure everyone has access to all the available distributions and versions.

Thanks to the Disco API this goal has been achieved and the integration of it in many tools is a crucial step that improves the developer experience in many ways.
