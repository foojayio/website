---
title: "Research: Measuring Energy Consumption in Programming Languages for AI Applications"
slug: "research-measuring-energy-consumption-in-programming-languages-for-ai-applications"
date: "2025-09-15T07:03:00+00:00"
lastmod: "2025-09-15T14:24:46+00:00"
description: "I hope my research paper helps the community understand not only the importance of proper language platform selection, but also hardware choice considerations."
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2025/09/20250915_ArticleLogo.png"
categories:
  - "AI"
  - "Arm"
  - "Java"
  - "Java Core"
  - "JDK21"
  - "Pi4J"
  - "Research"
tags:
related_posts:
  - "ai-newsletter-1"
  - "jc-ai-newsletter-2"
  - "jc-ai-newsletter-3"
  - "jc-ai-newsletter-4"
frozen: false
---

**Decades ago, I contributed to a very interesting project called [SunSPOT](https://sunspotdev.org/# "SunSPOT ")at SUN Microsystems. It was a small device with wireless connectivity, built-in sensors, running on Java (compatible with runtime 1.6), and powered by battery. It enabled the design of small applications and the connection of additional hardware, or simply the utilization of available sensors. Later, together with Markus Hirt, we created the [Robo4j framework](https://github.com/Robo4J/robo4j "Robo4j framework"). A small and lightweight Java framework that enables rapid assembly of robots or other IoT systems (JDK 21+). In Robo4j, we primarily used the[Pi4j project](https://www.pi4j.com/blog/ " Pi4j project") for handling I/O operations on the Raspberry Pi platform with drivers created for specific hardware.**

A question that has been concerning me over the decades is: what is the actual energy consumption of such Java-based systems?

The unprecedented growth of artificial intelligence (AI) and applications utilizing agentic AI systems from various vendors has elevated my question about energy consumption to the next level.

Similar to Raspberry Pi development or AI agent implementation, Python serves as the primary language choice due to its simplicity for prototyping applications without requiring strict attention to type definitions. Working with hardware your obvious choice would be C and with utilizing an agentic AI system there is possibility to use JavaScript, which is a very nice functional language, to prototype initial experiments or applications. After my colleague at OpenValue published his hackathon's simple energy consumption experiment, I became even more motivated to answer my question about energy consumption.

I created a fully automated framework in shell script that is capable of executing various runtimes and running parallel processes at the system level to obtain energy consumption measurements. I was eager to investigate how Java 21 and 17 compare against other languages like JavaScript, C, and Python. After gathering data and employing methodologies typically used in clinical studies, I acquired sufficient data to reach statistically significant conclusions.

The findings and conclusions have been added to my research article: ['Measuring Energy Consumption in Programming Languages for AI Applications (LINK)](https://drive.google.com/file/d/1vFR8FhFlA_mCgYOm2lnONziADr0afB3P/view?usp=drive_link "'Measuring Energy Consumption in Programming Languages for AI Applications (LINK)").

I would like to thank all the people who helped me shape this research, as acknowledged in the article's Acknowledgments section.

**I hope my research paper helps the community understand not only the importance of proper language platform selection, but also hardware choice considerations.**
