---
title: "Banner statistics"
description: "Impressions and click-throughs for every banner campaign, current and finished."
url: "/ad-stats/"
type: "ad-stats"

# UNLISTED, on purpose. This is the number an advertiser is quoted, so it is
# reachable by anyone who has the URL -- but it is not something to stumble on:
#
#   list: never   keeps it out of site.RegularPages, which is what the /sitemap/
#                 page ranges over AND what sitemap.xml is built from, so one
#                 setting covers both. It is not in any menu because no menu
#                 entry was added -- the nav is explicit in hugo.toml.
#   noindex       baseof.html adds <meta name="robots" content="noindex"> for
#                 `type: "ad-stats"`, so a search engine that finds the URL some
#                 other way (a referrer log, a shared link) still won't list it.
#
# Not a substitute for access control: the numbers behind it come from the
# public /api/views/all and are committed to data/views.json in a public repo,
# so treat this as unadvertised rather than private.
build:
  list: never
---

Every banner foojay has run, with the numbers the counter recorded. Campaigns
that have finished stay on this list: the counts outlive the banner itself.
