---
title: "From Cloudflare Zero-trust to Tailscale"
date: "2026-01-12T20:04:31+00:00"
lastmod: "2026-09-21T05:59:26+00:00"
description: "I have spent some time last year implementing Cloudflare Tunnels on my Home Assistant and my Synology NAS. On Mastodon, I had not one but two commenters…"
canonical: "https://blog.frankel.ch/cloudflare-zero-trust-tailscale/"
authors:
  - "nicolas-frankel"
image: "cover_large-1.jpg"
categories:
  - "Cloud"
  - "DevOps"
related_posts:
frozen: false
---

I have spent some time last year implementing [Cloudflare Tunnels](https://developers.cloudflare.com/cloudflare-one/networks/connectors/cloudflare-tunnel/) on my [Home Assistant](https://blog.frankel.ch/home-assistant/6/) and my [Synology NAS](https://blog.frankel.ch/second-cloudflare-tunnel/). On Mastodon, I had not one but two commenters advertising for Tailscale:

<https://mastodon.top/@frankel/115639107167365460>

I decided to give it a try and migrate my servers and devices to Tailscale. In this post, I want to describe how I did. Thanks to Heiko Does and higgins for prompting me to look further!

## What is Tailscale, how and why?

> A Zero Trust identity-based connectivity platform that replaces your legacy VPN, SASE, and PAM and connects remote teams, multi-cloud environments, CI/CD pipelines, Edge \& IoT devices, and AI workloads.
>
> -- [Tailscale](https://tailscale.com/)

In other words, Tailscale allows creating a [mesh VPN](https://tailscale.com/learn/understanding-mesh-vpns) that your devices can connect to. Devices can then communicate with each other inside the network, isolated from the rest of the world. With my current Cloudflare Zero-trust setup, the problem is that my user devices aren't on the network. Hence, I need to provide public endpoints for my services, which come with privacy and security issues.

Tailscale solves them instantly. My user devices on the same isolated network remove the need for public endpoints. At this point, I knew I had to make the move.

## Onboarding on Tailscale

The user experience of onboarding on Tailscale is amazing. You chose among a handful of identity providers, and you're on. Tailscale delegates all authentication to the chosen ++++++. Chose wisely: you can't bind your account to multiple IdPs to have a fallback.

By default, Tailscale onboards you on a 14-day free Enterprise trial plan. You can change to a personal free plan to avoid building on features that aren't necessary. The plan offers three different users and 100 devices. It's more than I need.

## Adding servers and devices

I added my servers and devices to the mesh by installing Taiscale on each of them, then authenticating with the IdP. Here are the supported OS:

* Linux
* Windows
* macOS
* iOS
* Android
* Synology

I did use the web-based IdP authentication because my servers provide such an interface. If yours don't, or if your fleet needs solid DevOps practices, you can generate a ready-made script with a dedicated enrolment key. I think there's even an API for this.

You might have noticed I used two different words: *server* and *device* . Devices are tied to a physical person's identity; servers aren't. Once authenticated, you can move the server to a [tag](https://tailscale.com/kb/1068/tags).
> Tags are essentially service accounts, but with more flexibility⎯you can assign multiple tags to a device to account for multiple purposes.

It makes the semantics clearer. I did, even if I'm not sure about the benefits in my single-user setup.

## Gains and losses

I migrated from Cloudflare Tunnel and public endpoints to Tailscale. It netted me gains and losses. Here is what I found out.

First and foremost, since I'm running my own mesh, I don't need to have a public endpoint. Without an endpoint, I need neither a subdomain nor a TLS certificate that leaks my server's home IP. Tailscale provides a dedicated subdomain of `ts.net`. You can choose between a random string (I assume it's your network ID) or a combination of adjective plus noun. Fun fact: the latter offers 3 choices, but you can "re-roll" until you get something that suits your fancy.

My previous setup with Cloudflare Tunnels worked with HTTP endpoints. Thus, I had no remote SSH access. Now, I can access my servers from my computer remotely, wherever I want. I never needed it before, but it can be very useful during a long trip abroad, when your home infrastructure starts misbehaving.

Likewise, I didn't create dedicated endpoints to synchronize my pictures and my music on the Synology. I only synchronized through the IP on the internal network. As soon as I connect to Tailscale on my devices, I get both. Given that the iPad version of DS Audio doesn't offer caching to listen offline, that's a great benefit.

Tailscale offers a feature called MagicDNS. It allows referencing servers and devices by their name, optionally suffixed by the Tailscale domain name. All in all, you can access them in several ways:

|         Type         |            Example             |
|----------------------|--------------------------------|
| IP v4                | `100.98.98.68`                 |
| IP v6                | `fd7a:115c:a1e0::3701:6261`    |
| Fully qualified name | `nas.pTsDVj8tCL11XNTRL.ts.net` |
| Simple name          | `nas`                          |

And finally, I could remove all the port forwarding rules on my home router.

All the above are net gains, but there are some losses too. Because I let go of subdomains, I need to remember ports when multiple apps are available on the same host. Tailscale offers [services](https://tailscale.com/kb/1552/tailscale-services) to alias a port, but the Tailscale version that comes with the Synology plugin doesn't.

By default, Tailscale doesn't provide TLS over internal servers. It does allow generating certificates, though. I'm too lazy to configure them right now, because the idea of a private mesh should protect from man-in-the-middle attacks. In addition, if Tailscale wants to eavesdrop on the traffic, it could, since Tailscale generates certificates anyway.

The last hurdle is network access from devices that Tailscale doesn't support, _e.g., smart watches. In theory, I would be able to access my Home Assistant from my Garmin watch via the [relevant app](https://apps.garmin.com/en-US/apps/61c91d28-ec5e-438d-9f83-39e9f45b199d). I have installed it, but never used it. With neither a public endpoint nor specialized software, I can't use it anymore. For this specific use case, Tailscale provides [Subnets](https://tailscale.com/kb/1019/subnets).

I'll need to check into the features later.

## Conclusion

Migrating to Tailscale was a leap of faith, but I'm very happy I did it. My setup has improved a lot, both in terms of privacy and security. It is also much simpler regarding my requirements. I encourage you to have a look.

**To go further:**

* [Tailscale quickstart](https://tailscale.com/kb/1017/install)
* [Integrations](https://tailscale.com/kb/1356/integrations)
* [Tailscale Services](https://tailscale.com/kb/1552/tailscale-services)

*Originally published at [A Java Geek](https://blog.frankel.ch/cloudflare-zero-trust-tailscale/) on January 11^th^, 2026*

*[IdP]: Identity Provider
