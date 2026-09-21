---
title: "Three Mastodon issues because of Cloudflare Bot protection"
date: "2026-04-27T18:56:21+00:00"
lastmod: "2026-09-21T05:56:20+00:00"
description: "I noticed some time ago that three Mastodon features had stopped working on my blog. Each of them seemed like a separate problem, but they had the same…"
canonical: "https://blog.frankel.ch/mastodon-cloudflare-bot-protection/"
authors:
  - "nicolas-frankel"
image: "cover_large-3.jpg"
categories:
  - "DevOps"
related_posts:
frozen: false
---

I noticed some time ago that three [Mastodon](https://mastodon.top/@frankel) features had stopped working on my blog. Each of them seemed like a separate problem, but they had the same root cause. In this blog post, I want to describe these issues and the simple fix.

## Domain verification

Mastodon allows you to prove that you own a domain. The mechanism requires two steps:

* Add a `<link rel="me">` tag in your pages, pointing to your Mastodon profile
* Add your website URL to your Mastodon profile

When Mastodon crawls your page and finds the backlink, it displays a green checkmark next to your URL in your profile.

I had set this up long ago and made it work, for my blog and GitHub. Then, the blog link stopped working, while GitHub's still worked. It made me sad, but I couldn't understand what changed.

## Post preview

When you share a link on Mastodon, your instance crawls the page to generate a preview card. It reads the page's [OpenGraph](https://ogp.me/) tags: title, description, and image.

{{< img src="mastodon-preview-620x510.png" class="aligncenter size-medium" alt="Mastodon link preview" width="620" height="510" >}}

My blog had all the correct tags. Yet every blog post I shared appeared as a plain link with no card. However, all newsletters shared the same preview image, and they mysteriously worked.

## Attribution

Mastodon introduced author attribution in the 4.3 version. If a page contains the following meta tag, Mastodon displays a "By @author" badge when someone else shares the link — and lets their followers follow you directly from the preview.

```html
<meta name="fediverse:creator" content="@frankel@mastodon.top">
```

This had never worked for my blog since I added it.

## The culprit

All three features share the same mechanism: Mastodon's server needs to crawl your page. I took me about 20 minutes of chatting with Claude Code to solve the three above issues, by solving the card preview one. The key was to understand that since the newsletter card preview worked, my blog wasn't at fault. Claude Code checked the response headers and understood the following.

My blog runs behind [Cloudflare](https://www.cloudflare.com/). I had enabled **Bot Fight Mode**, a Cloudflare feature that blocks automated traffic it deems suspicious. Services can register domains on Cloudflare to be recognized as good actors. In fact, Bluesky and Twitter have done it.

However, Mastodon was betrayed by its decentralized nature. Each Mastodon instance runs a specific domain; mine is <https://mastodon.top>. mastodon.top's crawler runs on [Hetzner](https://www.hetzner.com/) infrastructure. Hetzner IP addresses carry a high threat score in Cloudflare's database — they're a popular choice with bot operators.

When Mastodon's crawler tried to fetch my pages, Cloudflare served it a JavaScript challenge instead of HTML. Mastodon's `http.rb` client can't solve JavaScript challenges. It got no useful response, cached the failure, and moved on.

The fix: turn off Bot Fight Mode in **Security** \> **Bots** in the Cloudflare dashboard.

## Conclusion

This is a well-known issue in both the Mastodon and Cloudflare communities. For a public static blog, Bot Fight Mode offers minimal protection while actively breaking legitimate crawlers. Turning it off fixed all three Mastodon issues at once.

**To go further:**

* [Cloudflare Tunnels and Mastodon](https://tsmith.co/2023/cloudflare-tunnels-and-mastodon/)
* [Bot protection is blocking valid bots](https://community.cloudflare.com/t/bot-protection-is-blocking-valid-bots/287563)
* [Understanding Mastodon Preview Card Display Logic](https://box464.com/posts/mastodon-preview-cards/)

*Originally published at [A Java Geek](https://blog.frankel.ch/mastodon-cloudflare-bot-protection/) on April 26^th^, 2026.*
