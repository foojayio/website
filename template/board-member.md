---
# ============================================================================
#  Advisory Board member — starter template.  MAINTAINERS ONLY.
# ----------------------------------------------------------------------------
#  Copy to content/pages/board/<slug>.md and drop the logo in
#  static/images/pages/board/<slug>.<ext>. That is the whole job: /board/
#  builds its grid, its count and the "other board members" list from the
#  files in that folder, so there is no list anywhere to add the member to.
# ============================================================================
title: "Organization Name"
# One or two sentences, usually the opening of the About text below. Shown on
# the card on /board/ and used for SEO and social previews.
description: "What the organization does, in a sentence."
# The URL is explicit for the same reason it is on every other page here:
# legacy paths are load-bearing and Hugo's default section nesting would put
# this under /pages/.
url: "/board/<slug>/"
# Required. This is how the member is found -- without it the page still
# builds, but it never appears on /board/. validate/Frontmatter.java fails the
# PR on a missing or misspelled one.
type: "board"
logo: "/images/pages/board/<slug>.png"
# The tile's backing colour. Logos arrive as a mix of dark-on-transparent and
# light-on-transparent artwork, so each one names the background it needs:
# "#ffffff" for dark artwork, the brand's own dark tone for a white wordmark.
logoBackground: "#ffffff"
website: "https://example.com/"
websiteLabel: "example.com"
twitter: ""
twitterLabel: ""
# The member's statement on why Foojay matters to them, and who said it. Both
# optional -- the section is skipped when there is no quote.
quote: |
  First paragraph of the quote.

  Second paragraph, if there is one.
quoteAuthor: "Name, Job Title at Organization"
frozen: false
---

The About text: a paragraph or two describing the organization, in their own
words.
