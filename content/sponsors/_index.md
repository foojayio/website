---
title: "Our Sponsors and Partners"
# EXPLICIT, and load-bearing: Hugo applies a cascade to the page that declares it
# as well as to its descendants, so without this the cascaded `type: "sponsor"`
# landed on THIS page too and /our-sponsors/ silently rendered with the
# sponsor-profile layout instead of the tier listing. An explicit value always
# beats a cascaded one.
type: "sponsors"
# NO FEED FOR THIS PAGE -- same empty-channel trap as content/authors/_index.md:
# every child is a branch bundle, so the section feed [outputs] hands out had no
# items in it. The per-sponsor feeds are real (layouts/sponsor/section.rss.xml).
outputs:
  - html
heroTitle: "Who Keeps Foojay Running"
# Served from static/images/sponsors/ and resolved through resource-url.html,
# which strips the leading slash so the baseURL subpath applies.
#
# CROPPED from the 592x309 original at
# foojay.io/wp-content/themes/foojay/img/sponsors-banner.png down to 235x309.
# The original's left 55% is flat #3057ca -- the same blue as the hero card it
# sits on -- so it was invisible filler that only ate layout width. Re-download
# the original and it will need cropping again; the artwork's bounding box is
# x 320..530, y 0..308.
heroImage: "/images/sponsors/sponsors-banner.png"
description: "The organisations funding and promoting foojay.io — the platform, podcast, events and content that hundreds of thousands of Java and OpenJDK developers rely on every day."
# The section listing lives at the WordPress path, not at /sponsors/. Individual
# sponsor pages keep /sponsor/<slug>/ via hugo.toml's [permalinks].
url: "/our-sponsors/"
aliases:
  - "/sponsors/"
  - "/community-support/"
# Sponsorship prospectus, linked from every CTA. Held IN THIS REPO rather than
# linked to wp-content, which disappears at cutover. To publish a new edition,
# drop the PDF in static/files/ and point this at it (keeping the same filename
# means links shared elsewhere keep working).
prospectus: "/files/foojay-sponsorship-proposal.pdf"
contactEmail: "hello@foojay.io"
# Tier order + blurbs. The ORDER of this list is the tier ranking used
# everywhere sponsors are listed (see partials/sponsors-ordered.html), so a new
# tier is added here and nowhere else. WordPress puts the blurbs behind a
# tooltip modal; here they're just text, which reads better and needs no JS.
# No `icon:` — the tier marker is a CSS disc tinted per tier, because the 🥈/🥉
# medal emoji render with a literal 2 and 3 in most fonts.
#
# `groupTitle`, `badge` and `noun` are OPTIONAL and exist for a tier that is not
# a sponsorship. They default to "<label> Sponsors", "<label> Sponsor" and
# "sponsor", which is what the three paid tiers want and why those three carry
# none of them. Community Partner sets all three, because a partner who pays
# nothing must not be labelled a sponsor anywhere a reader can see — the tier
# exists precisely to keep that line visible. Resolved by
# partials/sponsor-tier.html, which is also what lets a sponsor PAGE (which
# knows only its own tier string) print the same words as the listing.
tiers:
  - name: "gold"
    label: "Gold"
    blurb: "The ultimate tier for maximum brand presence: 10 homepage banners per year, a premium dedicated sponsor page to showcase eBooks and whitepapers, and 2 featured podcast episodes, plus custom performance analytics and official team swag."
  - name: "silver"
    label: "Silver"
    blurb: "The sweet spot for active community engagement: a dedicated sponsor page with an extended company description, 6 homepage banner campaigns per year and 1 dedicated podcast interview annually, unlimited author accounts and traffic analytics on request."
  - name: "bronze"
    label: "Bronze"
    blurb: "The essential package: prominent logo placement on the sponsor page and 3 homepage banner displays per year, with unlimited author accounts and published articles so your engineering team can share insights freely."
  - name: "community-partner"
    label: "Community Partner"
    groupTitle: "Community Partners"
    badge: "Community Partner"
    noun: "partner"
    blurb: "Not a paid tier: the conferences, JUGs, newsletters, podcasts and open-source projects we promote each other with. Partners get a profile page, a logo across the site and author accounts for their team; in return they link to Foojay, share our content with their audience, or open their event to us."
frozen: true
# Each sponsor bundle is a BRANCH bundle (_index.md), i.e. a Hugo section rather
# than a page -- that is the only page kind .Paginate accepts, and a sponsor with
# 100+ attributed articles needs a pager rather than one endless grid.
#
# The cascade gives every child `type: "sponsor"` (singular) so it resolves to
# layouts/sponsor/section.html. Without it a sponsor and the /our-sponsors/ index
# are both `type: "sponsors"` sections and would fight over one layout.
cascade:
  - target:
      kind: section
    type: "sponsor"
---

Foojay.io is built by the Java community, for the Java community — and kept free for everyone. Our sponsors make that possible. By supporting Foojay, these organisations help fund the platform, podcast, events and content that hundreds of thousands of Java and OpenJDK developers rely on every day. In return, our sponsors become a visible, trusted part of the ecosystem they're investing in.

Our community partners support Foojay differently. They are the conferences, Java User Groups, newsletters, podcasts and open-source projects we promote each other with: no money changes hands in either direction, and the exchange is reach — their audience hears about Foojay, and ours hears about them.
