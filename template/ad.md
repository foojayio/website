---
# A sponsored banner in the home page carousel.
#
# Copy this WHOLE FOLDER shape to content/ads/<ad-slug>/index.md and drop the
# creative in next to it:
#
#     content/ads/coderabbit-ai-code-review/index.md
#     content/ads/coderabbit-ai-code-review/coderabbit.png
#
# Name the folder after the advertiser and the campaign -- lowercase letters,
# digits and dashes only. It is a page BUNDLE (index.md, not _index.md) so the
# creative lives beside the copy, which is the same shape a post, an author and
# a sponsor use, and the reason a banner is content rather than a data file:
# there is one folder per campaign instead of a YAML file in one tree and an
# image in another.
#
# One folder per BANNER, not per sponsor. A sponsor runs several a year (10 for
# gold, 6 silver, 3 bronze), each with its own creative, wording and dates, so a
# new campaign is a new folder rather than an edit of this one -- and two people
# adding two banners in the same week never touch the same bytes. The sponsor's
# own page under content/sponsors/ is their profile and outlives any campaign;
# don't put banners there.
#
# Nothing here renders as a page of its own: content/ads/_index.md switches
# rendering off for the whole section, so a banner has no URL, no sitemap entry
# and nothing in site search. Delete the comments and the optional lines you
# don't need.

# --- Required -------------------------------------------------------------

# The headline. One line -- it is set large and shares the slide with the
# description and the button.
title: "Cut Code Review Time & Bugs in Half. Instantly."

# The button's label. Say what happens ("Download Now", "Get it on Leanpub"),
# not "Click here".
cta: "Get Started!"

# Where the button sends a reader. Opens in a new tab.
#
# NOT `url:` -- that is Hugo's own frontmatter key, and it would give the banner
# a page URL of its own while quietly leaving the button with nothing to point
# at. validate/Frontmatter.java fails the PR on it.
link: "https://coderabbit.link/foojay-banner"

# The creative, a bare filename: the file sitting next to this index.md. Local,
# never a hotlink to the advertiser's own host -- a banner has money attached,
# and an image someone else can move or delete leaves an empty slide that no
# build step here can see. Being in the bundle also puts it under the same
# image-weight check every post hero gets.
image: "coderabbit.png"

# The slide's background, #RGB or #RRGGBB: the advertiser's own colour, which is
# the one thing about a banner nothing can derive. The text and icon colour IS
# derived from it (dark ground -> white text, light ground -> black), so there is
# no second colour to pick and no way to ship an unreadable pair.
background: "#EEEEE3"

# --- Optional -------------------------------------------------------------

# The supporting line under the headline. Two sentences at most.
description: "Supercharge your team to ship faster with the most advanced AI code reviews."

# An optional SECOND button, for a banner with somewhere on foojay to send a
# reader as well as off it -- the Sustainability eBook links to Leanpub above and
# to its own page here. Both keys or neither: the template keys off secondaryCta,
# so a link with no label renders no button at all and a label with no link
# renders one pointing nowhere. validate/Frontmatter.java fails the PR on either.
#
# An internal destination is ROOT-RELATIVE ("/some-page/"), which relURL then
# resolves -- a bare "some-page/" resolves against whatever directory the page is
# served from, and an absolute https://foojay.io/... would break the trial deploy
# under /website/. An external https:// URL is used verbatim and is the only one
# that opens in a new tab. The primary button keeps the filled style so two
# buttons never compete.
secondaryCta: "Read more"
secondaryLink: "/sustainability-for-java-developers/"

# The campaign window, YYYY-MM-DD. These are Hugo's OWN scheduling keys, not
# ours: a banner before its publishDate or after its expiryDate is dropped from
# the build entirely, so there is no date logic in the layout and no flag to
# unset. expiryDate is the moment it stops running -- the first day it should
# NOT appear, the same shape as a sponsor author's `till:`.
#
# Leave both out for a banner that runs until someone removes it. With them set
# you never have to come back: the campaign ends on its own at the next build.
publishDate: "2026-09-01"
expiryDate: "2026-10-01"

# false only for foojay's OWN promotions (the eBook, a call for articles), which
# are not paid placements. Defaults to true, so a paid banner is labelled
# "Sponsored Content" without anyone having to remember to say so.
sponsored: true

# Nothing else: the rotation order, the arrows, the label and the text colour are
# the layout's job, and the "Do you want your ad here?" button is site chrome
# shown beside the carousel rather than a property of any one banner.
---
