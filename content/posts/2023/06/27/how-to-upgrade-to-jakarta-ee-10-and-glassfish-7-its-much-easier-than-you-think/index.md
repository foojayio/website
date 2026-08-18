---
title: "Upgrade to Jakarta EE 10 and GlassFish 7: Easier Than You Think!"
slug: "how-to-upgrade-to-jakarta-ee-10-and-glassfish-7-its-much-easier-than-you-think"
date: "2023-06-27T07:29:48+00:00"
lastmod: "2023-08-08T10:34:38+00:00"
description: "Everything you need to know before upgrading to Jakarta EE 10 and a guide to upgrade successfully and in almost no time!"
authors:
  - "ondro-mihalyi"
image: "omnifish-jakarta-ee-10-no-release-1024x576-1.jpeg"
categories:
  - "Developer Tools"
  - "Jakarta EE"
  - "OpenRewrite"
  - "Tutorials"
tags:
related_posts:
  - "omnifish-announces-enterprise-support-for-eclipse-glassfish"
  - "ejb-support-in-piranha-via-cdi"
  - "omnifish-jakarta-ee-survey-2022"
  - "reflections-on-2024-a-remarkable-year-for-omnifish-glassfish-piranha-and-jakarta-ee"
frozen: false
---

Upgrading to Jakarta EE 10 from an older version of Jakarta EE or Java EE can be a bit tricky and may require some extra attention to detail. But don't worry, we've got you covered!

In this series of articles, we'll explain everything you need to know before upgrading to Jakarta EE 10 and guide you to complete the upgrade successfully and in almost no time.

## Types of challenges

The challenges with upgrading to Jakarta EE 10 or to Eclipse GlassFish 7 fall into these 3 main categories:

* Changing the `javax` package prefix to `jakarta` requires updating all references to the old packages
* Obsolete annotations may need to be replaced with alternative annotations
* Rewriting code that uses removed APIs, which don't have straightforward alternatives

All of the above applies not only to your codebase but also to all dependencies used by your application.

## Existing tools to automate upgrade steps

Fortunately, many of the challenges can be automated using free and opensource tools like [Openrewrite](https://github.com/openrewrite), and [Eclipse Transformer](https://github.com/eclipse/transformer). These tools can save you time and effort when upgrading to Jakarta EE 10, allowing you to focus on other important aspects of your application's development.

## So, what to do to successfully upgrade to Jakarta EE 10?

All that you need to know is covered or will be covered soon by this series of articles about upgrading to Jakarta EE 10 at the OmniFish blog:

<figure class="wp-block-embed is-type-wp-embed is-provider-omnifish wp-block-embed-omnifish">
 <div class="wp-block-embed__wrapper">
  <blockquote class="wp-embedded-content" data-secret="gqkyrBQGag">
   <a target="_blank" href="https://omnifish.ee/2023/05/06/how-to-upgrade-to-jakarta-ee-10-and-glassfish-7/">How to upgrade to Jakarta EE 10 and GlassFish 7 – it’s much easier than you think!</a>
  </blockquote><iframe class="wp-embedded-content" sandbox="allow-scripts" security="restricted" style="position: absolute; clip: rect(1px, 1px, 1px, 1px);" title="“How to upgrade to Jakarta EE 10 and GlassFish 7 – it’s much easier than you think!” — OmniFish" src="https://omnifish.ee/2023/05/06/how-to-upgrade-to-jakarta-ee-10-and-glassfish-7/embed/#?secret=utJse63fGZ#?secret=gqkyrBQGag" data-secret="gqkyrBQGag" width="500" height="282" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"></iframe>
 </div>
</figure>

So far, the series contains the following posts:

* [How to upgrade to Jakarta EE 10 and GlassFish 7](https://omnifish.ee/2023/05/06/how-to-upgrade-to-jakarta-ee-10-and-glassfish-7/)
* [Transform Applications with Eclipse Transformer](https://omnifish.ee/2023/05/29/upgrading-to-jakarta-ee-10-transforming-applications-with-eclipse-transformer/)
* [Transform Application Source Code](https://omnifish.ee/2023/06/20/upgrade-to-jakarta-ee-10-transform-application-source-code/)

We've prepared detailed instructions that explain how to use available automating tools and example projects that show how to use them. In near future, we'll be adding more articles to cover more details, like how to upgrade dependencies to Jakarta EE 10, how to transform them if needed, and how to refactor your code to remove usage of APIs removed in Jakarta EE 10. We hope that this series of articles will help you upgrade to Jakarta EE 10 easily and in a very short time.

<figure class="alignleft size-full is-resized">
 <img decoding="async" src="omnifish-logo-transparent-400px-margin.png" alt="" class="wp-image-60966" width="200" height="200">
</figure>

## OmniFish - Jakarta EE experts

* Eclipse GlassFish Production Support
* Jakarta EE Consulting
* Custom Development with Jakarta EE

For more information, contact OmniFish at their [contact page](https://omnifish.ee/contact-us/), or Twitter at [@OmniFishEE](https://twitter.com/OmniFishEE).
