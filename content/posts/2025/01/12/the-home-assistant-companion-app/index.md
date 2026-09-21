---
title: "The Home Assistant companion app"
date: "2025-01-12T15:52:14+00:00"
lastmod: "2026-09-21T06:03:17+00:00"
description: "Besides a regular webapp and a Mac app, which uses the same endpoints as the webapp, Home Assistant also provides mobile apps. In this post, I want to…"
canonical: "https://blog.frankel.ch/https://blog.frankel.ch/home-assistant/5/"
authors:
  - "nicolas-frankel"
image: "cover-companion.jpeg"
categories:
  - "Java"
related_posts:
  - "an-example-of-hacs-adaptive-lighting"
  - "loosely-coupled-configuration-for-home-assistant"
frozen: false
---

Besides a regular webapp and a Mac app, which uses the same endpoints as the webapp, Home Assistant also provides mobile apps. In this post, I want to describe its advantages over the former. I'll use iOS to do this: Samsung's French partner to recycle used mobiles tricked me once, and I moved away from Samsung. I decided to buy Apple for better integration since I've had Mac computers for the last few years.

## Installing and configuring

The companion app is readily available at the [Apple Store](https://apps.apple.com/app/home-assistant/id1099568401). Note that the app is both *free as a beer* and *free as a bird* . It's provided under the Apache v2 License. If interested, the source code is on [GitHub](https://github.com/home-assistant/iOS).

Once installed, you must configure at least one Home Assistant server. At this point, you should get the same UI as the web app. The app's power lies in its widgets, though.

## Widgets

Some iOS applications allow you [adding widgets to the UI](https://support.apple.com/en-us/118610). I'm not a massive user of widgets. I've Google Calendar, the Weather, and a VPN on my phone.

Widgets serve a dual purpose: visualization and possibly action; if a widget doesn't offer an action, pressing it will open the associated app. That's the case with the Google Calendar and Weather apps. My VPN widget displays a button allowing you to connect and pause.

## The companion app widget

The app offers two distinct kinds of widgets, each serving a specific purpose. The first kind allows opening the app on a specific "page", *e.g.*, Overview or Developer tools. IMHO, it's not very useful.

The second kind allows calling commands. However, it requires significantly more effort. First, we must create `action` objects. Here's one to switch the light on:

```yaml
actions:
  - name: OfficeLightsOn                 #1
    label:
      text: "Turn ON Office Lights"      #2
    icon:
      icon: lightbulb-on-outline         #3
      color: "#ffff00"                   #3
    show_in_carplay: false
    show_in_watch: true
```

1. Name, but you should probably think about it as a String ID
2. The label shown in the companion app
3. Graphic details used by the companion app

After creating the action, you **must** restart Home Assistant for devices to detect the new action. Once restarted, you can add a Home Assistant widget that is bound to that action.

image:iphone-add-widget.webp\[Adding a widget to the iPhone, featuring the previously created action(s),590,1278\]

However, pressing the widget does nothing but a slight haptic feedback. We need to bind the action to an `automation` object. I explained automations when we migrated away from the Hue bridge in the [previous post](https://blog.frankel.ch/home-assistant/3/#binding_the_light_to_the_sensor). At the time, we created an automation via the UI; we can also create one in the `/homeassistant/configuration.yaml`. We configure the action as the automation's *trigger*.

```yaml
- alias: Turn On Office Lights On        #1
  trigger:
    - platform: event                    #2
      event_type: ios.action_fired       #2
      event_data:
        actionName: OfficeLightsOn       #3
  action:
    - service: light.turn_on             #4
      entity_id: light.all_office        #5
```

1. Friendly alias for easier maintenance
2. Fixed payload sent to the device
3. Configured payload sent to the device. If the payload matches, it will trigger the action
4. Service to call
5. Call the service on the device ID

Now, pressing the widget turns on the light as expected.

## Summary

Integrating Home Assistant on your device is straightforward. The pressing on the widget is the trigger for an automation - an `action`. You can create the `automation` or reuse an existing one.

**To go further:**

* [Home Assistant Companion Docs](https://companion.home-assistant.io/)

*Originally published at [A Java Geek](https://blog.frankel.ch/https://blog.frankel.ch/home-assistant/5/) on January 12^nd^, 2025*

*[HA]: Home Assistant
