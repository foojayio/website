---
title: "Ads"

# The paid home page banners. One page bundle per campaign, so a banner's
# creative sits next to its copy -- see template/ad.md for the fields.
#
# A banner is content with an image, which is why it lives here rather than
# split across data/ads/*.yaml and static/images/ads/. It is NOT a page anyone
# can visit, though, and the two blocks below are what keep it from becoming
# one. Both were verified against a real build, not assumed:
#
#   `render: never`         no HTML is written, so a banner mints no URL, no
#                           sitemap entry, and nothing for Pagefind to index --
#                           site search has no ad pages in it at all, and the
#                           built page count is unchanged (3220 before and
#                           after this section was added).
#   `publishResources`      what still copies the creative out. Without it,
#                           render: never takes the image with it and every
#                           slide silently loses its picture.
#   `list: local`           keeps banners out of site-wide collections
#                           (site.RegularPages, the RSS feeds, /sitemap/) while
#                           leaving them in this section's own .Pages, which is
#                           how a layout gets the list:
#                           `(site.GetPage "/ads").Pages`.
#
# The section's own `build:` is explicit rather than inherited, because a
# cascade applies to the page that declares it too -- and `list: local` would
# then put "Ads" into the home page's local collections.
#
# It is `build:`, not `_build:`. The underscored form was removed in Hugo
# 0.145 and now raises an ERROR, which fails the deploy.
#
# WHEN THE CAROUSEL IS BUILT: put data-pagefind-ignore on it. Rendering off
# keeps ad copy out of the index only for as long as nothing renders it -- the
# moment a layout draws these on the home page, that text is inside
# <main data-pagefind-body> and becomes searchable content credited to the home
# page. partials/sidebar.html carries the attribute for exactly this reason.
build:
  render: never
  list: never

cascade:
  build:
    render: never
    list: local
    publishResources: true
---
