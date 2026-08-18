---
title: "An Example of HACS: Adaptive Lighting"
slug: "an-example-of-hacs-adaptive-lighting"
date: "2025-01-15T08:40:46+00:00"
lastmod: "2025-01-15T08:40:48+00:00"
description: "Configure the Adaptive Lighting integration to our Home Assistant and benefit from different intensities based on the time of the day."
canonical: "https://blog.frankel.ch/home-assistant/4/"
authors:
  - "nicolas-frankel"
image: "light-bulbs-3958844.jpg"
categories:
  - "Java"
tags:
related_posts:
  - "building-simple-home-assistant-langchain4j-raspberry-pi"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "getting-started-with-intellij-idea"
frozen: false
---

**In the [previous post](https://blog.frankel.ch/home-assistant/3/) of this focus, we replaced Philips Hue automation with the one from Home Assistant. One significant gap we noticed was that Home Assistant doesn't automatically adjust the brightness according to the time of the day, a feature Philips Hue offers. In this post, we are going to address this gap.**

The first step when wanting to add a feature to Home Assistant is to browse through available integrations. While there was no out-of-the-box integration, I discovered an alternative: HACS.

## The Home Assistant Community Store

Before implementing your integration, you should know about [Home Assistant Community Store](https://hacs.xyz/) or HACS. HACS is a **third-party integration store**. If Home Assistant doesn't offer the necessary integration, there's a considerable chance somebody published it on HACS.

HACS itself is available as an integration.

<img fetchpriority="high" decoding="async" class="aligncenter wp-image-115066 size-medium" src="ha-ui-search-hacs-integration-700x288.jpeg" alt="Search for HACS" width="700" height="288">

Once you install it, it appears in the integration list.

<img decoding="async" class="aligncenter size-medium wp-image-115067" src="ha-ui-integration-hacs-535x510.jpeg" alt="HAC installed" width="535" height="510">

On HACS, we can search for a relevant integration. Indeed, there's [Adaptive Lighting](https://github.com/basnijholt/adaptive-lighting):

### Adaptive Lighting

> Adaptive Lighting is a custom component for Home Assistant that intelligently adjusts the brightness and color of your lights 💡 based on the sun's position, while still allowing for manual control.
>
> -- [GitHub - Adaptive Lighting](https://github.com/basnijholt/adaptive-lighting)

The original app allowed the light to be configured according to time ranges. Adaptive Lighting is head and shoulders above that: it takes into account sunrise and sunset time. Even better, it provides an [app](https://basnijholt.github.io/adaptive-lighting/) for configuration.

<img decoding="async" class="aligncenter size-medium wp-image-115068" src="adaptive-lighting-companion-webapp-700x504.jpeg" alt="Adaptive Lighting companion webapp" width="700" height="504">

To add any repository to HACS, click on the *HACS \> Integrations* item on the main left menu. Then click on the bottom right *Explore and download repositories* button. Add the [Adaptive Lighting](https://github.com/basnijholt/adaptive-lighting) repo.

<img loading="lazy" decoding="async" class="aligncenter size-medium wp-image-115069" src="ha-ui-hacs-adaptive-lighting-700x370.jpeg" alt="Adaptive Lighting added to HACS" width="700" height="370">

Home Assistant features the Adaptive Lighting integration at this stage, ready to be configured and used.

<img loading="lazy" decoding="async" class="aligncenter size-medium wp-image-115070" src="ha-ui-hacs-integration-adaptive-lighting-628x510.jpeg" alt="Adaptive Lighting integration" width="628" height="510">

We can now add a new entry. Click on the *Add Entry* button. Name it accordingly. Once the service is created, click the *Configure* button.

![Configure the Adaptive Lighting service](ha-ui-hacs-configure-service-adaptive-lighting-603x1024.jpeg)

The configuration can be quite intimidating, with its many parameters. You can use the companion app mentioned above to help you with it. I kept all parameters to their default value, but the most important one: the light is to adapt according to the time of the day.

<img loading="lazy" decoding="async" class="aligncenter size-medium wp-image-115072" src="ha-ui-hacs-service-adaptive-lighting-538x510.jpeg" alt="Adaptive Lighting configured service" width="538" height="510">

Depending on your setup, you can configure a single light, a couple of them, or all. I added only a single one, the one managed by the sensor.

## Conclusion

In this article, we configured the Adaptive Lighting integration to our Home Assistant. We can now benefit from different intensities based on the time of the day.

Even better, we learned to search the HACS when we didn't find the relevant out-of-the-box integration.

**To go further:**

* [Home Assistant](https://www.home-assistant.io/)
* [Home Assistant Community Store](https://hacs.xyz/)
* [Adaptive Lighting](https://github.com/basnijholt/adaptive-lighting)
* [Replace Philips Hue automation with Home Assistant's](https://blog.frankel.ch/home-assistant/3/)

*Originally published at [A Java Geek](https://blog.frankel.ch/home-assistant/4/) on December 22^nd^, 2024*
