---
title: "Events Calendar"
description: "Upcoming meetups, conferences and workshops from the Java community worldwide."
url: "/calendar/"
type: "events"
# A FEED OF THE NEXT 30 EVENTS, rendered by themes/foojay/layouts/events/single.rss.xml.
# Declared here rather than in hugo.toml because [outputs] there takes CLASSES of
# page (home/section/taxonomy/term) and this is a `page` -- there is no way to
# hand one page a feed from config, and giving every page one would mint 2000
# empty feeds. Hugo's embedded RSS template would render an empty <channel> here
# (it ranges over .Pages, which a page kind has none of), which is why the
# template above exists; see its header for the pubDate reasoning.
outputs:
  - html
  - rss
aliases:
  - "/all-events/"
frozen: true
---

What's on in the Java community: meetups hosted by Java User Groups worldwide,
plus the conferences and workshops on the calendar. Switch between the month
view and the full list, and click an event to see the details or to sign up.
