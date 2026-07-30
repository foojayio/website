---
title: "Starting a JavaFX Project with Gluon Tools"
slug: "starting-a-javafx-project-with-gluon-tools"
date: "2020-11-17T08:38:06+00:00"
lastmod: "2021-08-23T13:01:07+00:00"
description: "Here on foojay.io you can already find two posts by Carl Dea to get you started with JavaFX. In this post, I want to show you yet another approach that uses the tools provided by Gluon, who are the maintainers, and the driving force behind OpenJFX. The Gluon start website and the plugin allow you to get started with a new JavaFX project in a few clicks. Thanks to the amazing work done by the Gluon team this also gives you a quick-start for the creation of a mobile application which can be built for both Android and iOS. - by Frank Delporte"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2020/11/Screenshot-from-2020-11-16-08-33-50-1024x434.png"
categories:
  - "Gluon"
  - "IntelliJ IDEA"
  - "JavaFX"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Here on foojay.io you can already find two posts by Carl Dea to get you started with JavaFX:

1. [Beginning JavaFX Applications with IntelliJ IDE](https://foojay.io/blog/beginning-javafx-with-intellij/): step-by-step how to start a new JavaFX project without any tools, are by using the Maven and Gradle tools.
2. [A JavaFX App on ZuluFX in 60 Seconds](https://foojay.io/blog/a-javafx-app-on-zulufx-in-60-seconds/): how to use a popular distribution from [Azul](https://www.azul.com/) to build a JavaFX HelloWorld Application in 60 seconds.

In this post, I want to show you yet another approach that uses the tools provided by [Gluon](https://gluonhq.com/), who are the maintainers, and the driving force behind [OpenJFX](https://openjfx.io/).

### Before We Start... {#h3-0-before-we-start}

We will be using IntelliJ IDEA in this post, and to develop JavaFX applications, we need to have the proper JDK and JavaFX installed.

In the past JavaFX was included in the Oracle JDK up till version 11.

But it has always been a separate project (see [openjfx.io](https://openjfx.io/)) and can also be installed separately from the JDK.

This gives us two possible approaches.

#### Use a JDK Including JavaFX

Some JDK providers have a version of the JDK which has JavaFX integrated. This makes it easy to build and run applications without the need to specify the module path and modules required to run your application.

1. [Azul ZuluFX](https://www.azul.com/downloads/zulu-community/?package=jdk-fx)
2. [BellSoft LibericaJDK](https://bell-sw.com/pages/downloads/)

The easiest way to switch between JDK versions is probably [SDKMAN](https://sdkman.io/) which contains a long list of possible options. For example on a Linux PC this is only a few of them of a list of over 80 versions!

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sdk list java
================================================================================
Available Java Versions
================================================================================
 Vendor        | Use | Version      | Dist    | Status     | Identifier
--------------------------------------------------------------------------------
 AdoptOpenJDK  |     | 15.0.1.j9    | adpt    |            | 15.0.1.j9-adpt      
...    
               | &gt;&gt;&gt; | 11.0.7.hs    | adpt    | local only | 11.0.7.hs-adpt      
...       
 Azul Zulu     |     | 15.0.1       | zulu    |            | 15.0.1-zulu         
               |     | 15.0.1.fx    | zulu    |            | 15.0.1.fx-zulu      
...             
               |     | 8.0.272      | zulu    |            | 8.0.272-zulu        
               |     | 8.0.272.fx   | zulu    |            | 8.0.272.fx-zulu     
               |     | 7.0.282      | zulu    |            | 7.0.282-zulu        
               |     | 6.0.119      | zulu    |            | 6.0.119-zulu        
 BellSoft      |     | 15.0.1.fx    | librca  |            | 15.0.1.fx-librca    
               |     | 15.0.1       | librca  |            | 15.0.1-librca       
               |     | 14.0.2.fx    | librca  |            | 14.0.2.fx-librca    
...   
               |     | 8.0.275.fx   | librca  |            | 8.0.275.fx-librca   
               |     | 8.0.275      | librca  |            | 8.0.275-librca     
 GraalVM       |     | 20.2.0.r11   | grl     |            | 20.2.0.r11-grl      
               |     | 20.2.0.r8    | grl     |            | 20.2.0.r8-grl   
...      
================================================================================
Use the Identifier for installation:

    $ sdk install java 11.0.3.hs-adpt
================================================================================
</pre>

For JavaFX-development, select a version with ".fx", e.g. `sdk install java 15.0.1.fx-zulu` and use this version in your IDE.

#### Use JavaFX as a Separate Library

This option allows you more flexibility to use separate versions of Java and JavaFX. In my case - as most of my projects or on Java 11 - I use AdoptOpenJDK 11, but combine it with JavaFX 16-ea to be able to test with the latest version.

In that case you'll need to follow these steps:

1. Download the FX-version of your choice [from the Gluon website](https://gluonhq.com/products/javafx/)
2. Unpack it into a directory of your choice, e.g. `opt/javafx-sdk-16/`
3. In the "Project structure" dialog in IntelliJ IDEA add the location including the "lib" in the "Modules \> Dependencies" screen, e.g. `/opt/javafx-sdk-16/lib`. By doing this, your IDE will be able to provide you the correct syntax highlighting.
4. To run your application, we also need to add the startup arguments to point to the javafx-modules. Open the Configurations dialog and provide the "VM options", including all the modules required for your project, e.g. `--module-path /opt/javafx-sdk-16/lib --add-modules javafx.controls,javafx.fxml`.

![](/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/Screenshot-from-2020-11-16-08-33-50-1024x434.png) Adding openjfx as a dependency ![](/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/Screenshot-from-2020-11-16-08-41-09-1024x194.png) Defining the VM options to run your application

### Create a New Project with start.gluon.io {#h3-1-create-a-new-project-with-start-gluon-io}

<br />

Gluon has created the website [start.gluon.io](https://start.gluon.io/) to start a new JavaFX project, the same way you can also do for Spring with [start.spring.io](https://start.spring.io/), or Quarkus with [code.quarkus.io](https://code.quarkus.io/).

<figure class="wp-block-image size-medium">
 <img decoding="async" width="675" height="510" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/start-gluon-io-minimal-675x510.png" alt="" class="wp-image-36314">
 <figcaption>
  Minimal selections for a JavaFX application on start.gluon.io
 </figcaption>
</figure>

For a minimal JavaFX project, you only need to define the "Application Details" and select the version and modules you are going to need.

As Gluon also offers additional features and you can use their tools to build mobile applications, extra options can be selected in the "Gluon Features" section. Be aware some of these require a license or a popup will be shown when your application starts.

A nice feature of this site is the "Preview Project" button which will show you the structure of your project before you download it.

<figure class="wp-block-image size-medium">
 <img loading="lazy" decoding="async" width="700" height="496" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/start-gluon-io-preview-700x496.png" alt="" class="wp-image-36315">
 <figcaption>
  Preview of the project before downloading
 </figcaption>
</figure>

By clicking the "Generate Project" button, you will get a ZIP with the full project you can open in your IDE and run immediately with `mvn javafx:run`.

<figure class="wp-block-image size-medium">
 <img loading="lazy" decoding="async" width="700" height="372" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/start-gluon-io-running-700x372.png" alt="" class="wp-image-36316">
 <figcaption>
  The created application running in IntelliJ IDEA
 </figcaption>
</figure>

### Create a New Mobile Project with the Gluon Plugin in IntelliJ IDEA {#h3-2-create-a-new-mobile-project-with-the-gluon-plugin-in-intellij-idea}

Gluon also created a [plugin for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/7864-gluon) which makes it very easy to start a JavaFX mobile project.

In your IntelliJ IDEA, double click the SHIFT key to open the universal search box and type "Plugins" and select the first option. Now you can search in the marketplace and install the Gluon plugin.

<figure class="wp-block-gallery columns-2 is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <ul class="blocks-gallery-grid">
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-plugins.png"><img loading="lazy" decoding="async" width="300" height="260" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-plugins-300x260.png" alt="" data-id="36317" data-full-url="https://foojay.io/wp-content/uploads/2020/11/intellij-plugins.png" data-link="https://foojay.io/?attachment_id=36317" class="wp-image-36317"></a>
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-plugins-marketplace.png"><img loading="lazy" decoding="async" width="300" height="260" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-plugins-marketplace-300x260.png" alt="" data-id="36318" data-full-url="https://foojay.io/wp-content/uploads/2020/11/intellij-plugins-marketplace.png" data-link="https://foojay.io/?attachment_id=36318" class="wp-image-36318"></a>
   </figure></li>
 </ul>
</figure>

Starting a new mobile JavaFX project now has become a very easy process. From the main menu select File \> New \> Project. In the left list, you will now find "Gluon" and four different options for a new project.
![](/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project.png)

In the next windows, you can select different options, Maven or Gradle, names of the pre-created first and second view,... Once the project is created and opened in IntelliJ IDEA, you can run it with `mvn javafx:run`.

<figure class="wp-block-gallery columns-3 is-cropped wp-block-gallery-2 is-layout-flex wp-block-gallery-is-layout-flex">
 <ul class="blocks-gallery-grid">
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-1.png"><img loading="lazy" decoding="async" width="983" height="637" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-1.png" alt="" data-id="36320" data-full-url="https://foojay.io/wp-content/uploads/2020/11/intellij-new-project-1.png" data-link="https://foojay.io/?attachment_id=36320" class="wp-image-36320"></a>
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-run.png"><img loading="lazy" decoding="async" width="1024" height="666" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-run-1024x666.png" alt="" data-id="36321" data-full-url="https://foojay.io/wp-content/uploads/2020/11/intellij-new-project-run.png" data-link="https://foojay.io/?attachment_id=36321" class="wp-image-36321"></a>
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-views.png"><img loading="lazy" decoding="async" width="985" height="430" src="/images/posts/2020/11/starting-a-javafx-project-with-gluon-tools/intellij-new-project-views.png" alt="" data-id="36322" data-full-url="https://foojay.io/wp-content/uploads/2020/11/intellij-new-project-views.png" data-link="https://foojay.io/?attachment_id=36322" class="wp-image-36322"></a>
   </figure></li>
 </ul>
</figure>

As you can see in this video, within 30 seconds you have a new JavaFX project opened in IntelliJ IDEA which you can immediately run and test.

<figure class="wp-block-embed is-type-video is-provider-vimeo wp-block-embed-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe loading="lazy" title="Gluon Mobile JavaFX MultiView project" src="https://player.vimeo.com/video/478356481?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

### Conclusion {#h3-3-conclusion}

The Gluon start website and the plugin allow you to get started with a new JavaFX project in a few clicks.

Thanks to the amazing work done by the Gluon team this also gives you a quick-start for the creation of a mobile application which can be built for both Android and iOS.

In a next post, we will see how the build process for these can be done with GitHub Actions.
