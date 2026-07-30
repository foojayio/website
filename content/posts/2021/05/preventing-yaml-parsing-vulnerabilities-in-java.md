---
title: "Preventing YAML Parsing Vulnerabilities in Java"
slug: "preventing-yaml-parsing-vulnerabilities-in-java"
date: "2021-05-06T16:09:24+00:00"
lastmod: "2021-07-05T20:09:19+00:00"
description: "With anchors, you can create a YAML bomb! The tremendous amount of (nested) objects will cause a memory overload."
canonical: "https://snyk.io/blog/java-yaml-parser-with-snakeyaml/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Kubernetes"
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

YAML is a human-readable language to serialize data that's commonly used for config files. The word YAML is an acronym for "YAML ain't a markup language" and was first released in 2001. You can compare YAML to JSON or XML as all of them are text-based structured formats.

YAML files are often used to configure applications, application servers, or clusters. It is a very common format in Spring Boot applications and, of course, to configure Kubernetes. However, similarly to JSON and XML, you can use YAML to serialize and deserialize data.

Although YAML looks like an excellent alternative for XML and JSON, many people aren't a big fan of the structure. Since the language is line-based and uses indentation to represent structure and nesting, indentation often causes problems when parsing complex data structures. A single missing (or extra) whitespace in a complex, data-heavy structure will cause failures when parsing YAML. This causes unexpected problems, and finding the problem in a YAML file is difficult.

Most importantly to note, manually importing YAML in your Java application with an outdated version of **[snakeyaml](https://snyk.io/vuln/SNYK-JAVA-ORGYAML-537645)**might get you into trouble.

Billion laughs attack {#h2-0-billion-laughs-attack}
---------------------------------------------------

One feature of YAML is that you can create anchors. You can reuse these anchors in different places so you do not have to repeat yourself. In the simplified example below, I create two variables: `var1` and `var2`. By using anchors, `var2` has the same value as `var1`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var1: &amp;anchor value
var2: *anchor</pre>

Let's take this to the extreme and create the famous billion laughs attack for YAML. By applying this concept in a nested way, I can *actually* make a billion laughs.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">lol1: &amp;lol1 ["lol","lol","lol","lol","lol","lol","lol","lol","lol"]
lol2: &amp;lol2 [*lol1,*lol1,*lol1,*lol1,*lol1,*lol1,*lol1,*lol1,*lol1]
lol3: &amp;lol3 [*lol2,*lol2,*lol2,*lol2,*lol2,*lol2,*lol2,*lol2,*lol2]
lol4: &amp;lol4 [*lol3,*lol3,*lol3,*lol3,*lol3,*lol3,*lol3,*lol3,*lol3]
lol5: &amp;lol5 [*lol4,*lol4,*lol4,*lol4,*lol4,*lol4,*lol4,*lol4,*lol4]
lol6: &amp;lol6 [*lol5,*lol5,*lol5,*lol5,*lol5,*lol5,*lol5,*lol5,*lol5]
lol7: &amp;lol7 [*lol6,*lol6,*lol6,*lol6,*lol6,*lol6,*lol6,*lol6,*lol6]
lol8: &amp;lol8 [*lol7,*lol7,*lol7,*lol7,*lol7,*lol7,*lol7,*lol7,*lol7]
lol9: &amp;lol9 [*lol8,*lol8,*lol8,*lol8,*lol8,*lol8,*lol8,*lol8,*lol8]
lolz: &amp;lolz [*lol9]</pre>

As you can see, `lol1` is a list of 10 strings `"lol"`. The variable `lol2` is a list of 10 times `lol1`. By repeating this principle several times, we end up with `lolz` = 10\^9 times `"lol"`. Better said, a billion laughs.

With anchors, you can create a YAML bomb! The tremendous amount of (nested) objects that such a YAML input creates will cause a memory overload.

Please [**read the full article**](https://snyk.io/blog/java-yaml-parser-with-snakeyaml/) to get a breakdown of how this attack works with actual Java examples and more importantly how to prevent this problem in your Java application.
