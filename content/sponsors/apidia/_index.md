---
title: "APIdia"
tier: "bronze"
tagline: "Java API documentation made efficient, clean and enjoyable"
description: ""
logo: "logo.svg"
logoBackground: "#ffffff"
website: "https://apidia.net/"
websiteLabel: "apidia.net"
linkedin: ""
youtube: ""
bluesky: "https://bsky.app/profile/apidia.net"
mastodon: "https://mastodon.social/@APIdia"
github: "https://github.com/APIdia-net"
# Hand-maintained: author slugs (content/authors/<slug>/) whose posts
# are this sponsor's articles. transfer/Sponsors.java preserves this block verbatim.
authors:
  - "stefan-richthofer"
# Fallback only -- sponsor/section.html derives the topic list from the tags of
# this sponsor's own articles as soon as there are any, and only falls back to
# this list while there are none.
#
# EVERY ENTRY MUST BE AN EXISTING CATEGORY: the template renders each one as a
# link to /today/category/<urlized>/, so an invented topic ("API",
# "Documentation", "Open Source" -- none of which are categories here) is a dead
# link that validate/BuiltSite.java blocks the deploy on.
topics:
  - "Java"
  - "Library"
  - "Tools"
  - "Maven"
# Never on WordPress -- APIdia joined after the Hugo migration, so the folder
# name IS the only URL and there is no legacy /sponsor/<wpSlug>/ to alias.
wpSlug: "apidia"
frozen: false
---

APIdia is a growing collection of high-quality, structured and interlinked API documentation for open source software. In the past decades, hardly any factor played such a crucial role for advancing the world's technology and economy as the open source movement — and developers should be allowed to dedicate their full energy to software development rather than to documentation plumbing. The goal of APIdia is to lift the API documentation burden from their shoulders: comment your code, and APIdia does the rest. Libraries can link their users straight to their API docs with an [APIdia badge](https://apidia.net/#badge) in their readme, always pointing at the newest version or at a specific one.
