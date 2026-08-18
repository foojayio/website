---
title: "How to Secure Your Web Apps With An API Gateway"
slug: "how-to-secure-your-web-apps-with-an-api-gateway"
date: "2022-07-18T18:50:10+00:00"
lastmod: "2022-07-18T19:13:51+00:00"
description: "When header values depend on the web app, we need to reload the configuration without downtime and Continuous Deployment pipeline integration"
canonical: "https://blog.frankel.ch/secure-webapps-api-gateway/"
authors:
  - "nicolas-frankel"
image: "safe-g188ed805c.jpg"
categories:
  - "DevOps"
  - "Security"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "kotlin-delegation"
  - "a-list-of-cache-providers"
  - "system-architecture-move-authentication-to-the-api-gateway"
frozen: false
---

API management solutions, also known as API gateways, are a must in this day and age of APIs.

However, once you've set up such a gateway, you can use it for different purposes unrelated to APIs.

Today, I want to show you how to improve the security of web apps.

## Prevent sniffing

Browsers are fantastic pieces of technology that try to make the life of users as comfortable as possible.

However, the balance between ease of use and security may sometimes tip on the former to the latter's detriment.

For example, if an HTTP response doesn't set the content type, the browser may try to infer it:
> Content sniffing, also known as media type sniffing or MIME sniffing, is the practice of inspecting the content of a byte stream to attempt to deduce the file format of the data within it. Content sniffing is generally used to compensate for a lack of accurate metadata that would otherwise be required to enable the file to be interpreted correctly.
>
> -- [Content sniffing](https://en.wikipedia.org/wiki/Content_sniffing)

It sounds great, but it's a security risk. Imagine a site that allows the upload of images. An attacker could upload an image that also contains malicious JavaScript. If the image is accessed with no content sent, a browser could interpret the stream as a script - served from the domain.

To prevent such catastrophic scenarios, one can set the [X-Content-Type-Options](https://owasp.org/www-project-secure-headers/#x-content-type-options) response header:

```
X-Content-Type-Options: nosniff
```

As an example, I'll use the [Apache APISIX API Gateway](https://apisix.apache.org/). Apache APISIX is built upon a plugin architecture. As its name implies, the [response-rewrite](https://apisix.apache.org/docs/apisix/plugins/response-rewrite/) plugin allows to modify the response, including HTTP response headers.

You can configure Apache APISIX via HTTP calls. Here's the command to prevent sniffing:

```bash
curl -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "plugins": {
    "response-rewrite": {
      "headers": {
        "X-Content-Type-Options": "nosniff"
      }
    }
  }
}'
```

Apache APISIX will set the header on request matching the route. Since the route is a catchall, the browser will sniff response from no response.

## Prevent framing

Some malicious sites may embed your site in an HTML `iframe` to carry on attacks. For example, a malicious actor could devise the website `myshop.trustmebro.com` that embeds your legit webshop `myshop.legit.com`. Users browsing the former would believe they are browsing the latter.

They would be able to browse products, put them in their cart, etc., until the payment page. On this page, `myshop.trustmebro.com` would intercept input to get payment details, such as all information related to a credit card:
> Clickjacking (classified as a user interface redress attack or UI redressing) is a malicious technique of tricking a user into clicking on something different from what the user perceives, thus potentially revealing confidential information or allowing others to take control of their computer while clicking on seemingly innocuous objects, including web pages.
>
> -- [Clickjacking](https://en.wikipedia.org/wiki/Clickjacking)

The [X-Frame-Options](https://owasp.org/www-project-secure-headers/#x-frame-options) helps us to prevent framing. To prevent framing altogether, the value should be `deny`. If you need framing on your domain, use `sameorigin` instead.

Let's use the `response-rewrite` plugin again:

```bash
curl -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "plugins": {
    "response-rewrite": {
      "headers": {
        "X-Frame-Options": "deny"
      }
    }
  }
}'
```

At this point, compliant browsers will prevent bad actors from framing your website.

## HTTPS pinning

HTTPS allows security guarantees compared to plain HTTP:
> Hypertext Transfer Protocol Secure (HTTPS) is an extension of the Hypertext Transfer Protocol (HTTP). It is used for secure communication over a computer network, and is widely used on the Internet. In HTTPS, the communication protocol is encrypted using Transport Layer Security (TLS) or, formerly, Secure Sockets Layer (SSL). The protocol is therefore also referred to as HTTP over TLS, or HTTP over SSL.
>
> The principal motivations for HTTPS are authentication of the accessed website, and protection of the privacy and integrity of the exchanged data while in transit. It protects against man-in-the-middle attacks, and the bidirectional encryption of communications between a client and server protects the communications against eavesdropping and tampering.
>
> -- [HTTPS](https://en.wikipedia.org/wiki/HTTPS)

As a website owner, you can offer HTTPS in two ways:

1. You can configure the site over HTTPS only. Unfortunately, users trying to reach the site over HTTP won't be able to.
2. You can 301 redirect users from HTTP to HTTPS. But since HTTP is unprotected, a attack can potentially occur during the initial request. The malicious actor could return a forged response, not redirecting to the legit HTTPS site.

HTTP Strict Transport Security is the response to the above quandary:
> *HTTP Strict Transport Security (HSTS)* is a policy mechanism that helps to protect websites against man-in-the-middle attacks such as protocol downgrade attacks and cookie hijacking. It allows web servers to declare that web browsers (or other complying user agents) should automatically interact with it using only HTTPS connections, which provide Transport Layer Security (TLS/SSL), unlike the insecure HTTP used alone. HSTS is an IETF standards track protocol and is specified in RFC 6797.
>
> The HSTS Policy is communicated by the server to the user agent via an HTTP response header field named "`Strict-Transport-Security`". HSTS Policy specifies a period of time during which the user agent should only access the server in a secure fashion. Websites using HSTS often do not accept clear text HTTP, either by rejecting connections over HTTP or systematically redirecting users to HTTPS (though this is not required by the specification). The consequence of this is that a user-agent not capable of doing TLS will not be able to connect to the site.
>
> The protection only applies after a user has visited the site at least once, relying on the principle of Trust on first use. The way this protection works is that a user entering or selecting a URL to the site that specifies HTTP, will automatically upgrade to HTTPS, without making an HTTP request, which prevents the HTTP man-in-the-middle attack from occurring.
>
> -- [HTTP Strict Transport Security](https://en.wikipedia.org/wiki/HTTP_Strict_Transport_Security)

The biggest drawback of is that it works only after the user has visited the site at least once. Barring that, it solves the above issue.

At this point, you're used to the `response-rewrite` plugin:

```bash
curl -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "plugins": {
    "response-rewrite": {
      "headers": {
        "Strict-Transport-Security": "dmax-age=86400; includeSubDomains"
      }
    }
  }
}'
```

## Fine-grained content control

Many ways are available for an attacker to load malicious resources from your domain. If they can access the underlying website, they can add a \`\` tag and reference anything they'd want. that render content dynamically, *e.g.*, WordPress, are a favorite target among bad actors.

On the other hand, you may want to load additional resources yourself. Such resources include style sheets, fonts, scripts, etc.; the same domain or any other could host them. Hence, one needs fine-grained control over which resources are allowed and which are not. It's the goal of the `Content Security Policy` HTTP header.
> *Content Security Policy (CSP)* is a computer security standard introduced to prevent cross-site scripting (XSS), clickjacking and other code injection attacks resulting from execution of malicious content in the trusted web page context.
>
> -- [Content Security Policy](https://fr.wikipedia.org/wiki/Content_Security_Policy)

Here's a sample :

```
Content-Security-Policy: default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'self'; form-action 'self';
```

The policy allows images, scripts, form submission, and CSS *from the same origin*. However, it disallows any other resources to load.

Let's pause for a bit. Astute readers probably have realized that the configuration of HTTP headers in the previous sections was generic. Any standard web server such as the Apache HTTP server or reverse proxy, *e.g.*, Nginx, can quickly achieve the same.

CSP, however, is another beast. There's no generic configuration; you must set up the header according to the underlying site. While legacy servers/reverse proxies allow fine-grained configuration, they generally have two drawbacks:

* Downtime: You must restart the server for the new configuration to take effect.
* No native Continuous Deployment integration: You must deploy both the new version of the site and the configuration at the same time. It requires an "admin" API that legacy solutions lack in general.

Apache APISIX offers a hot reload feature and an administrative HTTP endpoint.

Now, to the configuration:

```bash
curl -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "plugins": {
    "response-rewrite": {
      "headers": {
        "Content-Security-Policy": "default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'self'; form-action 'self';"
      }
    }
  }
}'
```

## Conclusion

The correct HTTP response headers can go a long way toward making your webapp more secure.

Most headers are generic: web servers and reverse proxies can easily configure them.

However, when header values depend on the underlying web app, we need to reload the configuration with no downtime and a way to integrate it with our Continuous Deployment pipelines.

API Gateways, such as [Apache APISIX](https://apisix.apache.org/), are a perfect fit for this usage.

**To go further:**

* [OWASP Secure Headers Project](https://owasp.org/www-project-secure-headers/)
* [HTTP Strict Transport Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Strict_Transport_Security_Cheat_Sheet.html)
* [Content Security Policy Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Content_Security_Policy_Cheat_Sheet.html)
* [HTTP Security Headers: The Best Practices](https://kerkour.com/http-security-headers)

*Originally published at [A Java Geek](https://blog.frankel.ch/secure-webapps-api-gateway/) on July 12^th^, 2022*

*[MITM]: Man-In-The-Middle
*[CSP]: Content Security Policy
*[CMS]: Content-Managed Systems
*[HSTS]: HTTP Strict Transport Security
