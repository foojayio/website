---
title: "Foojay Podcast #102: Why Foojay Walked Away From WordPress for Static Sites"
date: "2026-10-05"
description: "Foojay walked away from WordPress after six years. Andy Damevin and Holly Cummins join to weigh CMS security and cost against Hugo, Jekyll and Roq."
authors:
  - "frankdelporte"
  - "andy-damevin"
  - "holly-cummins"
image: "episode-102-static-websites.jpg"
categories:
  - "Podcast"
  - "Developer Tools"
  - "Security"
related_posts:
  - "announcing-the-new-foojay"
  - "keep-wordpress-if-youre-looking-for-trouble"
  - "asciidoc-support-on-foojay"
  - "diagrams-as-code-mermaid-support-on-foojay"
  - "foojay-podcast-89"
---

Foojay.io quietly moved off WordPress and onto Hugo, a static site generator. No more CMS login, no database, no admin panel — just Markdown and AsciiDoc files, built and deployed straight from GitHub. In this episode we dig into why that move happened, and into the bigger question behind it: is a traditional CMS still the right way to run a content site in 2026, or has static tooling caught up? Along the way we go through the real security numbers behind WordPress's plugin ecosystem — more than 10,000 vulnerabilities found in WordPress plugins since 2025 alone — and put very different static site generators head to head: Hugo and Jekyll, the veterans, versus Roq, the Java-and-Quarkus-based newcomer.

To help unpack it, I'm joined by two guests from the Quarkus team. Andy Damevin built `code.quarkus.io` and created Roq, Quarkus's own static site framework; Holly Cummins led the migration of quarkus.io itself from Jekyll to Roq. Between them they've lived both sides of the "why does my static site generator feel like this" question.

We cover what actually breaks on a WordPress site (it's almost never WordPress core — it's the plugins you need to do anything with it), what "recovery" means when your content lives in Git instead of a database, how to extend Roq as a Java developer, what migrating from WordPress to Roq (or Hugo) actually looks like in practice, and where a CMS still earns its keep despite all of this.

## YouTube

{{< youtube 0rQ2_UF7N1I >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Andy Damevin - Principal Software Engineer at Red Hat, Quarkus core team, creator of `code.quarkus.io`, Quinoa, Web Bundler and Roq
  * [LinkedIn](https://www.linkedin.com/in/andy-damevin/)
  * [Foojay author page](/today/author/andy-damevin/)
* Holly Cummins - Senior Principal Software Engineer on the Red Hat Quarkus team, Java Champion, led the quarkus.io migration from Jekyll to Roq
  * [LinkedIn](https://www.linkedin.com/in/holly-k-cummins/)
  * [Foojay author page](/today/author/holly-cummins/)

## Links

* [Migrate from WordPress to Roq](https://iamroq.dev/posts/migrate-from-wordpress-to-roq/)
* [Pwned: How Roq Saved the Day](https://iamroq.dev/posts/pwned-how-roq-saved-the-day/)
* [Foojay Podcast #89: Quarkus and Agentic Commerce](/today/foojay-podcast-89/)
* Static site generators discussed
  * [Jekyll (Ruby)](https://jekyllrb.com/)
  * [Hugo (Go)](https://gohugo.io/)
  * [Roq (Java/Quarkus)](https://iamroq.dev/)
  * [JBake (Java)](https://jbake.org/)
* Foojay's own move to static, mentioned in the episode
  * [Announcing the New Foojay](/today/announcing-the-new-foojay/)
  * [AsciiDoc Support on Foojay](/today/asciidoc-support-on-foojay/)
  * [Diagrams as Code: Mermaid Support on Foojay](/today/diagrams-as-code-mermaid-support-on-foojay/)

## Content

* 00:00 Introduction of topic and guests
* 03:29 Why WordPress is not the best choice for most sites
* 04:36 Difference between CMS-driven websites and static websites
* 07:02 Jekyll versus Roq and other systems
* 11:04 How to extend Roq
* 12:59 Moving from WordPress to Roq
* 14:43 Pulling data with JBang or Roq from external sources into your publication process
* 16:09 Why should I use Roq instead of Jekyll or Hugo?
* 24:33 Static websites can be created with a small team and are much cheaper (or even free) to host
* 26:52 How many WordPress websites have a backup to recover after getting hacked? And the advantage of having your content sources on Git.
* 33:04 Is there still a use case for WordPress-like systems?
* 37:09 Conclusions
