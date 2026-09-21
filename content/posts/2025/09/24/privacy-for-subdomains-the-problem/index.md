---
title: "Privacy for subdomains: the problem"
date: "2025-09-24T18:43:15+00:00"
lastmod: "2026-09-21T06:01:33+00:00"
description: "I recently learned about a new way to leak your privacy, and it's a scary one. Before going further, know that I'm not a network engineer: perhaps if you…"
canonical: "https://blog.frankel.ch/privacy-subdomains/1/"
authors:
  - "nicolas-frankel"
image: "cover_large-1.jpg"
categories:
  - "Java"
related_posts:
  - "privacy-for-subdomains-the-solution"
frozen: false
---

I recently learned about a new way to leak your privacy, and it's a scary one. Before going further, know that I'm not a network engineer: perhaps if you work in this field, you've known it for your whole career, but it's quite new to me. Let me share my findings, and you can judge for yourself.

Since the original post was quite lengthy, I have broken it down into two installments: the problem and the solution.

## The situation

I own my own domain. I've created multiple subdomains out of it. Some of them map to online services, one of them to a [private Cloudflare Tunnel](https://blog.frankel.ch/home-assistant/6/) to my Home Assistant, and one to my hosted at home. The latter obviously forwards requests to my router, which forwards NAS-related requests to it. The router, in turn, has a public IP address given by my Internet provider. Reminder: in the European Union, IPs are considered *private data* in the legal sense of the term. In France, revealing such data is a **penal** offense, punishable by up to 5 years in prison and up to a 300k€ fine. You can read [article 226-2](https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000006417984) of the French penal code if you're interested in the details. In theory, nobody would know about my subdomains, including the one that points to my router IP address.

To secure the connection with my NAS, I'm using Let's Encrypt to get a TLS certificate. Let's Encrypt has been a boon for individuals to get free certificates compared to when one had to pay for them. Aye, there's the rub! Let's Encrypt logs certificate requests in a central registry, namely [Certificate Transparency](https://certificate.transparency.dev/). But Let's Encrypt isn't the only one: every public TLS certificate issuer does the same (Google, Cloudflare, etc.). Actually, there's a publicly available tool to check the logs for a domain: <https://crt.sh/>. We can check the logs of a [French famous newspaper](https://www.lemonde.fr/) to see how they leak personal data. Here's a small excerpt:

| Not Before | Not After  |            Common Name            |                             Matching Identities                             |                                               Issuer Name                                                |
|------------|------------|-----------------------------------|-----------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| 2025-04-02 | 2025-07-01 | `redaction.lemonde.fr`            | `redaction.lemonde.fr`                                                      | `C=US, O=Google Trust Services, CN=WE1`                                                                  |
| 2025-04-03 | 2025-07-02 | `infos.lemonde.fr`                | `infos.lemonde.fr`                                                          | `C=US, O=Google Trust Services, CN=WE1`                                                                  |
| 2025-04-02 | 2025-07-01 | `abonnements.lemonde.fr`          | `abonnements.lemonde.fr`                                                    | `C=US, O=Google Trust Services, CN=WE1`                                                                  |
| 2025-04-02 | 2025-07-01 | `rec-festival.lemonde.fr`         | `rec-festival.lemonde.fr`                                                   | `C=US, O=Let's Encrypt, CN=R11`                                                                          |
| 2025-03-31 | 2025-06-29 | `salon-masters.lemonde.fr`        | `salon-masters.lemonde.fr`                                                  | `C=US, O=Let's Encrypt, CN=R11`                                                                          |
| 2018-04-04 | 2019-02-21 | `s2.shared.global.fastly.net`     | * `*.lemonde.fr` * `lemonde.fr`                                             | `C=BE, O=GlobalSign nv-sa, CN=GlobalSign CloudSSL CA - SHA256 - G3`                                      |
| 2012-03-29 | 2014-03-29 | `application-facebook.lemonde.fr` | * `application-facebook.lemonde.fr` * `www.application-facebook.lemonde.fr` | `C=GB, ST=Greater Manchester, L=Salford, O=COMODO CA Limited, CN=COMODO High-Assurance Secure Server CA` |
| 2012-11-29 | 2015-12-03 | `s8.wac.edgecastcdn.net`          | `application-facebook.lemonde.fr`                                           | `C=US, O=DigiCert Inc, OU=www.digicert.com, CN=DigiCert High Assurance CA-3`                             |

See how effortlessly we get access to subdomains?

The conclusion is simple: every single time you request a TLS certificate from one of the public issuers, it's logged **and** publicly available. From the subdomain, anyone can trace the IP from the DNS records, and if one of the subdomains points to your home, you're easily traceable. If the above looks pretty grim, it's because it is.

Even worse: logs seem to be present forever. If you check with lemonde.fr, you'll see the earliest log is from Digicert in 2013.

## Possible fixes

Let's look at the possible fixes.

* Remove the subdomain: It's an option if you don't need external access. I possibly could, since accessing my NAS remotely is not of utmost importance. However, it would remove the fun of trying to find a solution.
* Remove HTTPS: Do I need to explain how bad an idea it is?
* Obfuscate the subdomain: Instead of `home.yourdomain.com`, you could have something cryptic like `xyz.yourdomain.com`. Unfortunately, as the number of subdomains is pretty limited, one can check them one by one and still locate the IP of your home.
* Use Cloudflare Tunnel: I'm using [Cloudflare Tunnel](https://blog.frankel.ch/home-assistant/6/) for my Home Assistant. I didn't find its certificate requests in the logs. The reason could be that it either uses wildcard certificates or that it doesn't log because its requests are internal.
* Use wildcard certificates: Wildcard certificate requests, *i.e.* , `*.mydomain.com`, are logged like any other. Yet, it doesn't leak any information about any subdomain.

From the above pool of solutions, two seem valid to me: Cloudflare Tunnel and wildcard certificates.

|     |    Cloudflare Tunnel    |                               Wildcard                                |
| Pro |      * Just works       |        * Learning opportunity * Easier fixability if it fails         |
| Con | * Depends on Cloudflare | * Complexity * Security: the certificate can be used on any subdomain |
|-----|-------------------------|-----------------------------------------------------------------------|

With this simple decision matrix, I chose to keep using Let's Encrypt, but with wildcard certificates. You may make another choice in your specific context.

## Summary

Any TLS certificate request to a public provider is logged in a publicly accessible registry. Certificate requests to subdomains that point to your home (or other places you don't want to disclose) can be traced to your IP via DNS records.

A couple of solutions exist. In the follow-up post, I'll use Let's Encrypt on Synology to request a wildcard certificate.

However, remember that once your requests have been logged, they are here forever. The only options to claim back your privacy are either to move physically to another place or, barring that, changing your Internet provider, which will provide you with a new IP.

**To go further:**

* [Certificate Transparency](https://certificate.transparency.dev/)
* [Certificate Search](https://crt.sh/)

*Originally published at [A Java Geek](https://blog.frankel.ch/privacy-subdomains/1/) on September 21^st^, 2025*

*[NAS]: Network Area Storage
