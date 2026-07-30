---
title: "foojay - Santa Claus Issues YuleLog4J Advisory"
slug: "santa-claus-issues-yulelog4j-advisory"
date: "2021-12-24T16:23:07+00:00"
lastmod: "2021-12-24T16:23:43+00:00"
description: "Santa Claus has issued a security advisory for the popular holiday celebration."
authors:
  - "erikcostlow"
image: "/images/posts/2021/12/santa-claus-issues-yulelog4j-advisory/Favicon-3-2.png"
categories:
  - "Foojay"
tags:
related_posts:
  - "java-where-the-wild-code-isnt"
  - "light-up-your-christmas-tree-with-java-and-raspberry-pi"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
frozen: false
---

Christmas revelers and elves are urged to patch their fireplaces, as a Remote Combustion Effect (RCE) vulnerability has been discovered in the traditional holiday YuleLog4J. YuleLog4J is one of the most popular holiday celebrations, appearing in [approximately 64% of fireplaces](https://www.contrastsecurity.com/security-influencers/log4shell-by-the-numbers) and streamed to millions of homes over [Netflix](https://www.netflix.com/title/70222873) and [Amazon Prime](https://www.amazon.com/Yule-Log-Christmas-Fireplace-Hours/dp/B01MZZWOWH).

The vulnerability occurs in the [Jingle Naming and Directory Interface](https://docs.oracle.com/javase/jndi/tutorial/getStarted/overview/index.html#:~:text=The%20Java%20Naming%20and%20Directory,any%20specific%20directory%20service%20implementation.) (JNDI), a utility that enables lookups of holiday cheer from remote sources. Unpatched versions of YuleLog4J can load potentially un-cheerful items such as coal, traditionally reserved as a stocking stuffer. The advisory was managed through [coordinated disclosure](https://securitylab.github.com/advisories/GHSL-2021-1054_GHSL-2021-1055_log4j2/) between the North Pole and the GiftHub Security Research Team.

Additional vulnerabilities have been detected that may impact holiday celebrations. Previous version of YuleLog4J are also at risk of a [Denial of Santa](https://threatpost.com/third-log4j-bug-dos-apache-patch/177159/) (DoS) vulnerability in recursive lookups based when paired with untrusted kindling.

Mitigating Your Risk {#h2-0-mitigating-your-risk}
-------------------------------------------------

Patches to defend the RCE are available in YuleLog4J 2.17.0.

Additional recommendations for a safe holiday are available in the [Code of the Elves](https://www.youtube.com/watch?v=lxhVHMdWhUs):

1. Treat every day like Christmas.
2. There's room for everyone on the nice list.
3. The best way to spread Christmas cheer is singing loud for all to hear.
