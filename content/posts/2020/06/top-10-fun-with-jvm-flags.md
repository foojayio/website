---
title: "Fun with Flags: My Top 10 Resources for JVM Flags"
slug: "top-10-fun-with-jvm-flags"
date: "2020-06-08T08:21:02+00:00"
lastmod: "2021-01-29T22:34:03+00:00"
description: "Resources on JVM command line arguments are scattered and hard to find, here is aconsolidated list so that others don't have to scour the internet as I did!"
authors:
  - "betsy"
image: "https://foojay.io/wp-content/uploads/2020/06/Logo.png"
categories:
  - "Performance"
tags:
related_posts:
frozen: false
---

When I first started programming in Java and configuring my local environment, I came across mentions of JVM flags. I wanted to find out more about what options are available, what they do, and how to make use of them. Delving into the internet to discover what other more seasoned developers had to say on the topic, I was surprised at how hard it was to get definitive answers, and ultimately this research left me with more questions than answers. Since resources on this topic are scattered and hard to find, I put together this consolidated list in the hopes that others don't have to scour the internet as I did to find these useful morsels.

Below are the best 10 I have found, listed chronologically, although all are really awesome and worth exploring!

1. [JVM Options Explorer](https://foojay.io/command-line-arguments/openjdk-11/?tab=alloptions), by Chris Newland. An incredible list of Java command line options that is actively maintained and updated. The flags are organized by Java version and vendor showing information about each flag including the file it is defined in. The website offers search and sort features to help navigate the large number of flags. The website also has a separate page that shows which flags were added and removed for each HotSpot version. Java versions in the main list vary by vendor but include options for JDK6 through JDK15. Vendors include HotSpot, Graal, OpenJ9 and Azul. These lists are compiled from vendor sources. **As of June 2020, this tool is actively maintained.**
2. [JaCoLine - Java Command Line Inspector](https://jacoline.dev/inspect), by Chris Newland. A very useful tool for developers and devops to understand and validate their Java command line options. **As of June 2020, this tool is actively maintained.**
3. [10 Important JVM Options for Production Java Application Systems](https://geekflare.com/important-jvm-options/), by Geekflare, **published on October 26, 2019**. Article highlighting a handful of command line options, with explanations on what they do and how to use them.
4. [JVM Options Cheat Sheet](https://www.jrebel.com/blog/jvm-options-cheat-sheet), by Simon Maple, JRebel, **published on December 14, 2016**. Good overview of the types of command line options available, along with a one page cheat sheet detailing some useful options of each type.
5. [A Collection of JVM Options](http://www.reins.altervista.org/java/A_Collection_of_JVM_Options_MP.html), by Mariano Paniga. This list is mainly based on a list compiled by Joseph D. Mocker with a few other resources. It contains Java command line options for versions 1.3.1 through 1.6.0, and **was last updated on January 19, 2015**.
6. [JVM Options - The complete reference](http://jvm-options.tech.xebia.fr/), by Pierre Laporte, published by Xebia. A searchable list of HotSpot options that display option catagory, architecture, and default value. The web application source code is published on GitHub, and **was last updated on August 26, 2013**. Command line options were compiled from HotSpot sources.
7. [Useful JVM Flags](https://blog.codecentric.de/en/2012/07/useful-jvm-flags-part-1-jvm-types-and-compiler-modes/), by Patrick Peschlow. By codecentric, **published from 2012-2014**, a series of 8 blog posts highlighting various JVM flags and how to use them.
8. [The most complete list of -XX options for Java JVM](http://stas-blogspot.blogspot.com/2011/07/most-complete-list-of-xx-options-for.html), by Stanislav Kobylyanskiy. This blog post includes a list of Java command line options revived from a web resource that is no longer available. This blogpost **was published on July 20, 2011**.
9. [Inspecting HotSpot JVM Options](http://q-redux.blogspot.com/2011/01/inspecting-hotspot-jvm-options.html), by Zahid Qureshi **published January 7, 2011**. Guide explaining how to print the comprehensive set of HotSpot JVM options and some examples comparing different outputs.
10. [JVM Memory tool](http://jvmmemory.com/), by Leonid Vygovskiy. A tool that generates JVM command line options based on users inputs for JDK6, JDK7 and JDK8.
