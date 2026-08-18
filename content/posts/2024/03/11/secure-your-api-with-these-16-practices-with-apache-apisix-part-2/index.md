---
title: "Secure your API with these 16 Practices with Apache APISIX – part 2"
slug: "secure-your-api-with-these-16-practices-with-apache-apisix-part-2"
date: "2024-03-11T09:39:24+00:00"
lastmod: "2024-03-11T09:43:37+00:00"
description: "In this two-post series, I've addressed most of 16 different practices to secure APIs with Apache APISIX."
canonical: "https://blog.frankel.ch/secure-api-practices-apisix/2/"
authors:
  - "nicolas-frankel"
image: "img-BuLDzx81CexYQAzkaF36h-1.png"
categories:
  - "Security"
tags:
related_posts:
  - "secure-your-api-with-these-16-practices-with-apache-apisix-part-1"
  - "apisix-api-gateway"
  - "authenticate-with-openid-connect-and-apache-apisix"
  - "how-to-secure-your-web-apps-with-an-api-gateway"
enlighterjs: true
frozen: false
---

[We've listed 16 practices to help secure one's APIs](https://foojay.io/today/secure-your-api-with-these-16-practices-with-apache-apisix-part-1/) and described how to implement them with Apache APISIX.
> 1. ~~Authentication 🕵️️ - Verifies the identity of users accessing APIs.~~
> 2. ~~Authorization 🚦 - Determines permissions of authenticated users.~~
> 3. Data Redaction 🖍️ - Obscures sensitive data for protection.
> 4. Encryption 🔒 - Encodes data so only authorized parties can decode it.
> 5. Error Handling ❌ - Manages responses when things go wrong, avoiding revealing sensitive info.
> 6. ~~Input Validation \& Data Sanitization 🧹 - Checks input data and removes harmful parts.~~
> 7. Intrusion Detection Systems 👀 - Monitor networks for suspicious activities.
> 8. ~~IP Whitelisting 📝 - Permits API access only from trusted IP addresses.~~
> 9. ~~Logging and Monitoring 🖥️ - Keeps detailed logs and regularly monitors APIs.~~
> 10. ~~Rate Limiting ⏱️ - Limits user requests to prevent overload.~~
> 11. Secure Dependencies 📦 - Ensures third-party code is free from vulnerabilities.
> 12. Security Headers 📋 - Enhances site security against types of attacks like XSS.
> 13. Token Expiry ⏳ - Regularly expiring and renewing tokens prevents unauthorized access.
> 14. Use of Security Standards and Frameworks 📘 - Guides your API security strategy.
> 15. Web Application Firewall 🔥 - Protects your site from HTTP-specific attacks.
> 16. API Versioning 🔄 - Maintains different versions of your API for seamless updates.

Let's now look at the remaining practices.

## Encryption and Data Redaction

First, we must protect the communication channel between our APIs and clients from unwanted reads and writes. That's the realm of TLS. In this regard, mutual TLS is state-of-the-art. Please read this [previous post](https://blog.frankel.ch/mtls-everywhere/) about mTLS in Apache APISIX.

I can't guess what the author meant by "Obscures sensitive data for protection". If data exchanges are encrypted, it doesn't make sense to obfuscate any payload.

## Error Handling

The list mentions avoiding revealing sensitive info when an error happens. Indeed, some poorly coded upstreams can disclose such data. Here's an example of Tomcat when developers forgot to configure an error page:

It reveals the upstream's technology, version, and the guilty code.

Apache APISIX can intercept such a response and rewrite it:

```yaml
routes:
  - upstream_id: 1
    plugins:
      response-rewrite:
        vars: [[ "status","==",500 ]]                        #1
        body: { "error" : "An unknown exception happened"}   #2
```

1. Triggered only in case of HTTP status code 500 returned by the upstream. You can add additional status codes if necessary
2. The body to return

To make sure the above configuration is applied consistently, we can also make it a global rule:

```yaml
global_rules:
  - id: 1
    plugins:
      response-rewrite:
        vars: [[ "status","==",500 ]]
        body: { "error" : "An unknown exception happened"}
```

## Security Headers

The OWASP lists plenty of [HTTP Headers](https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Headers_Cheat_Sheet.html) you can set to improve the security of your web apps and APIs. Apache APISIX provides two dedicated plugins for specific security risks:

* [CORS](https://apisix.apache.org/docs/apisix/plugins/cors/)
* [CSRF](https://apisix.apache.org/docs/apisix/plugins/csrf/)

For any other header, you can use the more generic [response-rewrite](https://apisix.apache.org/docs/apisix/plugins/response-rewrite/) plugin to add them. Finally, we can remove default HTTP response headers, such as `Server`, to make targeted attacks less likely.

```yaml
global_rules:                               #1
  - id: 1
    plugins:
      response-rewrite:
        headers:
          set:
            X-Content-Type-Options: nosniff #2
          remove:
            - Server                        #3
```

1. Do on every route - security by default! It still can be overridden on a per-route basis, in case of need
2. Tell the browser not to infer the content type if it's not explicitly set
3. Don't advertise the server

## WAF and API versioning

I've addressed these two points in previous posts:

* [Hardening Apache APISIX with the OWASP's Coraza and Core Ruleset](https://blog.frankel.ch/apisix-owasp-coraza-core-ruleset/)
* [API Versioning](https://blog.frankel.ch/api-versioning/)

In short, Apache APSIX allows embedding the [Coraza WAF](https://coraza.io/) as a Rust plugin.

On the versioning side, one can choose three different approaches: path-based, query parameter-based, and header-based. APISIX supports all of them.

## Other items

The remaining items are:

* Intrusion Detection Systems
* Secure Dependencies
* Use of Security Standards and Frameworks

I'm afraid that APISIX cannot help with any of them. You need to address them on the upstream side.

## Conclusion

In this two-post series, I've addressed most of the 16 practices to secure APIs with Apache APISIX.

While I don't claim the list is exhaustive, it's a solid basis to improve the security of one's system.

*Originally published at [A Java Geek](https://blog.frankel.ch/secure-api-practices-apisix/2/) on February 25^th^, 2024*
