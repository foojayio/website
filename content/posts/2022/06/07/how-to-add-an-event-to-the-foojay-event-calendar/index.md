---
title: "How to Add an Event to the Foojay Event Calendar"
date: "2022-06-07T09:34:47+00:00"
lastmod: "2022-06-07T09:39:02+00:00"
description: "If you want to add an event to our calendar or advertize your event on Foojay.io, you can follow the following steps!"
authors:
  - "bazlur-rahman"
  - "frankdelporte"
image: "Screen-Shot-2022-05-30-at-1.01.58-AM.png"
categories:
  - "Events"
  - "Foojay"
related_posts:
  - "how-to-submit-your-next-article-on-foojay-io"
  - "friends-of-openjdk-at-fosdem-2022"
  - "getting-started-with-java-17-and-intellij-idea"
frozen: true
---

Foojay.io, the place for **F**riends **O**f **O**pen**J**DK, is a friendly community of users of the OpenJDK, such as Java developers and Kotlin developers. It is a collaborative community with tips and insights being shared on a daily basis on [Foojay Today](https://foojay.io/today/).

At Foojay, we maintain a list of upcoming Java-related events. It helps the worldwide OpenJDK community find events and participate in them easily.

You will find all the events in the following link:

<https://foojay.io/calendar/>

*Updated: foojay.io no longer runs on WordPress, so events are no longer added
through a login. The steps below are the current ones.*

If you want to add an event to our calendar or advertise your event on
Foojay.io, there are two routes, and which one you need depends on whether your
event already publishes a calendar feed.

## A JUG meetup — nothing to do

If you run a Java User Group, foojay picks your meetups up **automatically** from
the calendar your group already publishes: your own site's iCal feed, a Google
Calendar, or Meetup's own export. Nothing has to be sent to us, and nothing has
to be kept in step.

All that is needed is that your JUG is listed in the community-run
[World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs),
with a `calendar:` or `meetup_slug:` entry. That list is maintained by the JUG
leads themselves, and Foojay reads it once a day.

## A conference, workshop or one-off — one small file

A conference publishes no feed anyone can subscribe to, so those are added by
hand — as a pull request against the site, which is also how articles are
submitted:

1. Copy [`template/event.yaml`](https://github.com/foojayio/website/blob/main/template/event.yaml)
   to `data/events/<event-slug>.yaml`.
2. Name the file after the event **including its year** —
   `devoxx-belgium-2026.yaml`, `jfokus-2027.yaml`. Next year's edition is a new
   file, not an edit of this one.
3. Fill it in and open a pull request.

```yaml
name: "Devoxx Belgium 2026"
type: "Conference"
url: "https://devoxx.be/"
start: "2026-10-05"
end: "2026-10-09"
venue: "Kinepolis Antwerpen"
city: "Antwerp"
country: "Belgium"
```

Only `name`, `url`, `start`, `city` and `country` are required, and `city`/`country`
can be left out for an online event. Everything else the calendar shows is worked
out for you: the colour, the band across the days a multi-day conference runs,
and the counts in the page header. You never have to come back to remove the
file either — an event drops off the calendar by itself the day after it ends.

The full field reference is in
[CONTRIBUTING.md](https://github.com/foojayio/website/blob/main/CONTRIBUTING.md),
and if you would rather not open a pull request yourself, ask in the
[Foojay community on Slack](https://join.slack.com/t/foojay/shared_invite/zt-tgefdcxv-SDwnqUqPH8peWujGNvC1ZQ)
and someone will do it for you.
