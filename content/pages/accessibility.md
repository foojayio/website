---
title: "Accessibility"
description: "How accessible foojay.io is, which standard we hold it to, what we know is not there yet, and how to tell us when something blocks you."
url: "/accessibility/"
frozen: false
---

We want everything on foojay.io to be readable and usable however you browse it —
with a keyboard, with a screen reader, at 200% zoom, with a colour scheme or a
motion setting of your own choosing.

## The standard we hold the site to

foojay.io aims to meet **[WCAG 2.2 level AA](https://www.w3.org/TR/WCAG22/)**, the
level referenced by the European standard EN 301 549 and by accessibility law in
most of the countries our readers write from.

We say *aims to* deliberately. The site is a static site built from Markdown
contributed by hundreds of authors, and the sections below say plainly where it
falls short today rather than claiming a conformance we have not audited.

## What is in place

- Every page has a **skip link**, one `<h1>`, and landmarks (banner, navigation,
  main, complementary, contentinfo) so a screen reader can jump straight to the
  article.
- The site is **fully keyboard-operable**: the menu, the search field, the image
  viewer, the event calendar, the sortable tables and the sponsor banners all
  work without a mouse, and the focus indicator is drawn at a contrast that is
  visible in both the light and the dark colour scheme.

  Try it, here or on any other page — nothing to install, just the keyboard:

  1. Press <kbd>Tab</kbd> once. A **Skip to content** button drops down at the
     top left; press <kbd>Enter</kbd> and you land past the menu, at the start of
     the article.
  2. Carry on pressing <kbd>Tab</kbd>. Everything you can use is outlined as you
     reach it, in the order you read it — and nothing you cannot see takes a
     turn. On a narrow screen, open the menu and <kbd>Tab</kbd> keeps you inside
     it until <kbd>Esc</kbd> closes it and hands focus back to the button you
     opened it with.
  3. In an article, <kbd>Tab</kbd> to an image and press <kbd>Enter</kbd>. The
     image viewer opens; <kbd>←</kbd> and <kbd>→</kbd> move through the other
     images in that article, and <kbd>Esc</kbd> closes it and puts you back on
     the image you opened it from.
  4. On the [calendar](/calendar/), switch to the month view, <kbd>Tab</kbd> to
     an event and press <kbd>Enter</kbd> for its details, then <kbd>Esc</kbd> to
     close it again.
  5. On the [sitemap](/sitemap/), <kbd>Tab</kbd> to a column heading of the
     articles table and press <kbd>Enter</kbd> to sort by it.

  If any of that does not do what it says here, that is a bug — please
  [tell us](#telling-us-about-a-problem).
- **Colours are measured, not guessed.** Body text, muted text, links and labels
  on coloured fills all meet at least 4.5:1 in both schemes; the values are
  recorded next to each colour in the stylesheet.
- **Nothing moves on its own that you cannot stop.** The sponsor banner rotation
  has a pause button, and it does not start at all if your system asks for
  reduced motion — which the site also honours for every other animation.
- **The dark colour scheme follows your system setting** and can be overridden
  per visit.
- **Podcast episodes have a transcript on the page**, so an episode is readable
  without listening to it — see the caveat below on where those come from.
- **No time limits, no flashing content, no audio that plays by itself.**

## What is not there yet

- **Images in older articles are missing alt text.** foojay.io's archive was
  imported from a previous system that stored most images with an empty
  description, and roughly 3,000 images across the archive are still in that
  state. New articles are checked for this when they are submitted, and the
  archive is being worked through. This is the site's biggest gap.
- **Podcast transcripts are machine-generated.** Every episode page carries a
  transcript you can read, search and skip through, but it comes from automatic
  speech recognition: it is tidied up, not checked against the audio, so names
  and technical terms are sometimes wrong. Each one says so above the text. The
  captions on the videos themselves are YouTube's automatic ones, which for the
  same reason do not meet the standard for captions.
- **Some articles have heading levels that skip** (an h2 followed by an h4),
  inherited from the same import.
- **Embedded content is not ours.** Articles embed videos, code playgrounds and
  slide decks from other sites; we cannot fix the accessibility of what those
  services serve.

## Telling us about a problem

If something on foojay.io blocks you, please tell us — a specific page and what
went wrong is enough, and you do not need to know the technical name for it.

- Email **[hello@foojay.io](mailto:hello@foojay.io)**
- Or open an issue on the site's repository at
  [github.com/foojayio/website](https://github.com/foojayio/website/issues)

We aim to reply asap. If a fix will take longer than that,
we will say so. We'll try to do our best to make the site usable for you, but please remember that the site is built from contributions by volunteers, and we cannot promise a fix for every problem.

## Writing for foojay?

If you contribute articles, the two things that matter most are **a description
for every image** and **link text that says where the link goes**. Both are
covered in the [article template](https://github.com/foojayio/website/blob/main/template/post.md),
the file you copy to start a new post.
