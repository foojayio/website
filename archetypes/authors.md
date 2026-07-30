---
# hugo new content authors/my-name.md --kind authors
# The FILENAME is the slug that posts reference in their `author:` field, and
# it is the URL: /today/author/<filename>/
title: "{{ replace .Name "-" " " | title }}"

# Square. `avatar` is used in cards and grids (96px is plenty), `avatarFull`
# on the profile and in the author box under an article. If only one exists,
# set `avatar` — the layouts fall back to it.
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
