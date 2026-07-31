---
# An author is a leaf bundle: content/authors/<first-letter>/<slug>/index.md with
# the avatar image(s) in the same folder. The FOLDER NAME is the slug that posts
# reference in their `authors:` list, and it is the URL: /today/author/<folder>/
title: "{{ replace .Name "-" " " | title }}"

# Square avatar image(s) placed in THIS folder, referenced by bare filename.
# `avatar` (small) is used in cards, grids and the author box; `avatarFull`
# (larger) on the profile page. If only one exists, set `avatar` — the layouts
# fall back to it. e.g. avatar: "avatar.jpg"
avatar: ""
avatarFull: ""

# 1-3 sentences, written in the third person. Shown in full on the profile and
# truncated to ~260 characters in the author box under each article.
bio: ""

# Every link is optional; the profile only renders the ones that are set.
# Full URLs, not handles.
bluesky: ""
mastodon: ""
linkedin: ""
github: ""
youtube: ""
twitter: ""
website: ""

# Legacy WordPress author URLs, if this profile ever lived somewhere else.
aliases: []

# Set true to stop the WordPress conversion scripts from overwriting hand edits.
frozen: false
---

<!-- Optional. Anything here renders below the profile header. Most author
     pages leave this empty and rely on `bio`. -->
