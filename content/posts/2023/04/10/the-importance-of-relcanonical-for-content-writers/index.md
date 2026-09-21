---
title: "The importance of rel=canonical for content writers"
date: "2023-04-10T16:22:20+00:00"
lastmod: "2026-09-21T06:05:00+00:00"
description: "The subject of canonical reference has been touched thousand times. But since some content writers are still making the same mistake over and over, I…"
canonical: "https://blog.frankel.ch/rel-canonical-content-writers/"
authors:
  - "nicolas-frankel"
image: "poppy-capsules-g8d0f5a9fa.jpg"
categories:
  - "Java"
related_posts:
frozen: false
---

The subject of canonical reference has been touched thousand times. But since some content writers are still making the same mistake over and over, I think it's beneficial to add one more. I hope to reach some who aren't aware of it.

## Content writing is not enough

Writing content is not enough: you can have written the best blog post of the century; it's no good if nobody sees it. One has two ways to spread the word:

1. Link to the post on social media
2. Copy the post to another site

The problem happens when copying the content.

## The problem of content duplication

Search engines want to provide users with the most relevant content for their search. They need to order search results to provide more relevant results first. Duplicate content poses the problem of which content to display first. It's a minor issue when it's on the same domain: the search engine will decide, and that's all.

The same happens across domains, but the consequences are worse. If another site decides to copy your content, how will the search engine decide which one to list first? My experience has shown me that the most *important* domain wins. Importance depends on the exact search engine, but generally, sites with more audience are considered more important.

## My previous bad crossposting experience

I started my blog in April 2018. As with every young blood out there, the beginning was very tedious: I was overjoyed the first time I had 100 visits in a single day!

Then, in June 2019, I was approached by DZone to become a "Most Valuable Blogger": DZone would publish all my posts. For me, it meant the world. People recognized the content I wrote as being "worthy" enough. Since there would be a link to the original article, I was also counting on getting a slight boost in audience. At that time, I was hosting my blog on WordPress and trying to balance the costs with AdSense ads: the more audience, the better the balance. For the record, I stopped advertisements as soon as I migrated to Jekyll and GitLab pages.

Then, some years ago, a friend did a Google search and stumbled upon one of my posts on DZone. Interestingly, the DZone copy was the first one found, while the original one was at the top of the second page. I stopped my cooperation with DZone on the spot.

## Setting things right

There's a single thing to do to set things right. All **copies** of a page should set the \`\` attribute:
> A canonical link element is an HTML element that helps webmasters prevent duplicate content issues in search engine optimization by specifying the "canonical" or "preferred" version of a web page. It is described in RFC 6596, which went live in April 2012.
>
> Search engines try to utilize canonical link definitions as an output filter for their search results. If multiple URLs contain the same content in the result set, the canonical link URL definitions will likely be incorporated to determine the original source of the content. "For example, when Google finds identical content instances, it decides to show one of them. Its choice of the resource to display in the search results will depend upon the search query."
>
> According to Google, the `canonical` link element is not considered to be a directive, but rather a hint that the ranking algorithm will "honor strongly."
>
> -- [Canonical link element](https://en.wikipedia.org/wiki/Canonical_link_element)

At the time, I looked at my posts on DZone, and their canonical links pointed to DZone.

## Conclusion

To keep one's original content at the top of search results, a content writer should only crosspost to sites that allow setting the `<link rel="canonical">` attribute. I crosspost to a couple of other sites with this feature: [dev.to](https://dev.to/nfrankel), [Hashnode](https://hashnode.com/@nfrankel), [DZone](https://dzone.com/users/293758/nfrankel.html) (it finally added it), [Medium](https://medium.com/@nfrankel), [Foojay](https://foojay.io/today/author/nicolas-frankel/), and [Hacker Noon](https://hackernoon.com/u/nfrankel).

If you want to avoid your blog being flagged as duplicate by search engines, you must:

1. avoid content aggregators that don't allow setting the `rel=canonical` attribute
2. use it!

*Originally published at [A Java Geek](https://blog.frankel.ch/rel-canonical-content-writers/) on April 9th, 2023*
