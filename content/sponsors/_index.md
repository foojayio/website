---
title: "Our Sponsors"
heroTitle: "Who Keeps Foojay Running"
# Co-located in this branch bundle rather than static/, so it travels with the
# page and resolves through resource-url.html like every other content image.
#
# CROPPED from the 592x309 original at
# foojay.io/wp-content/themes/foojay/img/sponsors-banner.png down to 235x309.
# The original's left 55% is flat #3057ca -- the same blue as the hero card it
# sits on -- so it was invisible filler that only ate layout width. Re-download
# the original and it will need cropping again; the artwork's bounding box is
# x 320..530, y 0..308.
heroImage: "/images/sponsors/sponsors-banner.png"
description: "The organisations funding foojay.io — the platform, podcast, events and content that hundreds of thousands of Java and OpenJDK developers rely on every day."
canonical: "https://foojay.io/our-sponsors/"
# The section listing lives at the WordPress path, not at /sponsors/. Individual
# sponsor pages keep /sponsor/<slug>/ via hugo.toml's [permalinks].
url: "/our-sponsors/"
aliases:
  - "/sponsors/"
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
frozen: true
---

Foojay.io is built by the Java community, for the Java community — and kept free for everyone. Our sponsors make that possible. By supporting Foojay, these organisations help fund the platform, podcast, events and content that hundreds of thousands of Java and OpenJDK developers rely on every day. In return, our sponsors become a visible, trusted part of the ecosystem they're investing in.
