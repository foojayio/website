---
title: "Loosely coupled configuration for Home Assistant"
date: "2025-07-14T12:18:55+00:00"
lastmod: "2026-09-21T06:01:45+00:00"
description: "This post will be short, but I hope it prove to be useful. My home is getting more and more connected, and the number of my automations grows each passing…"
canonical: "https://blog.frankel.ch/home-assistant/8/"
authors:
  - "nicolas-frankel"
image: "cover-coupling.jpg"
categories:
  - "Java"
related_posts:
  - "an-example-of-hacs-adaptive-lighting"
  - "the-home-assistant-companion-app"
frozen: false
---

This post will be short, but I hope it prove to be useful.

My home is getting more and more connected, and the number of my automations grows each passing month. Recently, I equipped my roller shutters with connected Somfy engines so they could roll down automatically when it's too hot in summer. Spoiler: given the current heatwave, it's a boon!

I naively created the following automation configuration:

```yaml
- id: '1742926520608'
  alias: Close all shutters
  description: Close all shutters if it's already hot in the morning
  triggers:
  - trigger: time                                                      #1
    at: 07:00:00
  conditions:
  - condition: numeric_state
    entity_id: sensor.saint_julien_en_genevois_temperature
    above: 23                                                          #2
  actions:
  - action: cover.close_cover                                          #3
    metadata: {}
    data: {}
    target:
      entity_id: cover.all_shutters                                    #4
  mode: single
```

1. If at 7 AM local time
2. The temperature where I live is higher than 23°C
3. Close
4. All shutters

Note the entity ID `cover.all_shutters`: it's not an out-of-the-box entity. I explicitly created a group for it:

```yaml
cover:
  - platform: group
    name: "All shutters"
    entities:
      - cover.left_shutter
      - cover.middle_shutter
      - cover.right_shutter
      - cover.bedroom_shutter
      - cover.office_shutter
```

I copied the same approach to switch all the lights off when I leave home. I soon realized there were flaws in this approach.

While the probability of adding more shutters is pretty low, I'll likely add more smart lights in the future. Every time I add one, I'll need to put it in the group. Also, I added lights in waves; I ID'd the lights in French at first, but later switched to English. As I renamed them all in English to be coherent, I had to update my dashboard to adapt to the new IDs.

Home Assistant already has everything to cater for the above, *Areas* . You can–and you probably should-assign every device to an Area. Once done, you can replace the `target` part of the automation with a list of `area_id` instead of `entity_id`.

```yaml
- id: '1742926520608'
  alias: Close all shutters
  description: Close all shutters if it's already hot in the morning
  triggers:
  - trigger: time
    at: 07:00:00
  conditions:
  - condition: numeric_state
    entity_id: sensor.saint_julien_en_genevois_temperature
    above: 23
  actions:
  - action: cover.close_cover
    metadata: {}
    data: {}
    target:
      area_id:                                                         #1
        - office
        - bedroom
        - living_room
  mode: single
```

1. Now, Home Assistant closes all shutters that are in any of these areas

Areas tend to be much more stable than individual devices. How often do you add a new area to your home? Granted, an area doesn't explicitly map to a room, but I think that's a better approach than my original one. Home Assistant will now manage every new device of the same type added to the area, *e.g.*, if you motorize a new shutter or add a new light.

There's one drawback to this approach. UI Tiles accept only a single entity ID. Thus, if you want to use the GUI, you must create a group as I did in my first approach.

In this post, I showed how you could replace the explicit grouping of devices by areas. It's the same idea of loose coupling we have in software, but applied to Home Assistant configuration.

**To go further:**

* [Group](https://www.home-assistant.io/integrations/group/)
* [Targeting areas and devices](https://www.home-assistant.io/docs/scripts/perform-actions/#targeting-areas-and-devices)

*Originally published at [A Java Geek](https://blog.frankel.ch/home-assistant/8/) on July 13th, 2025*
