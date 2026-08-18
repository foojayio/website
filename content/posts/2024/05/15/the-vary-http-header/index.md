---
title: "The Vary HTTP Header"
date: "2024-05-15T07:46:26+00:00"
lastmod: "2024-05-15T07:46:28+00:00"
description: "Recently, I stumbled upon the list of all registered HTTP Headers. This post is dedicated to the Vary HTTP Header."
canonical: "https://blog.frankel.ch/vary-http-header"
authors:
  - "nicolas-frankel"
image: "web-3876081.jpg"
categories:
  - "Research"
related_posts:
  - "poor-mans-api"
  - "kubernetes-gateway-api"
  - "a-list-of-cache-providers"
  - "comparison-fault-tolerance-libraries"
frozen: false
---

**I try to constantly to deepen my knowledge of HTTP and REST. Recently, I stumbled upon the list of all [registered HTTP Headers](https://www.iana.org/assignments/http-fields/http-fields.xhtml). This post is dedicated to the `Vary` HTTP Header.**

## The problem

Two years ago, I wrote about [web resource caching server-side](https://blog.frankel.ch/web-caching/server/). The idea is to set up a component between the client and the upstream to cache previously computed results to avoid overloading the latter. Depending on your infrastructure and requirements, this component can be a reverse proxy or an API Gateway. HTTP offers the [Cache-Control](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control) header to customize the different aspects of caching, *e.g.* , the time the server holds the resource in cache before it considers it stale. I used plugin configuration in the above post, but you can also delegate to `Cache-Control`.

Now, imagine the following scenario. You request a resource, _e.g., `GET /book/1` and get the result:

```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "Notre-Dame de Paris"
}
```

The request succeeds; the result is cached. Now, I request the same resource, but because my code works around XML, I set the `Accept` header to `application/xml`. Unfortunately, the server returns the cached JSON resource, which differs from what I asked and probably utterly breaks my code.

The problem is that the cache key has a single dimension, the URL, by default.

## The solution

We need a configurable multi-dimension cache key. As you can probably guess by now, that's the role of the `Vary` header: it explicitly lists all dimensions of the cache key. In the example above, the upstream would communicate the additional cache key with the following:

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Language: en
Vary: Accept

{
  "id": 1,
  "title": "Notre-Dame de Paris"
}
```

Instead of a single cache entry per URL, we now have one per MIME type/URL combination. Note that it's up to the caching component to use this information.

Another common request header is `Accept-Encoding`, which usually specifies which compression algorithms the client can accept. Encoding is another possible cache key. The specification allows specifying multiple cache keys:

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Language: en
Vary: Accept, Accept-Encoding

{
  "id": 1,
  "title": "Notre-Dame de Paris"
}
```

## Conclusion

I've described the `Vary`response header in this post. As soon as you configure caching, you must consider possible cache keys and use the `Vary` header accordingly.

**To go further:**

* [RFC 9110: Vary](https://datatracker.ietf.org/doc/html/rfc9110#field.vary)

*Originally published at [A Java Geek](https://blog.frankel.ch/vary-http-header) on May 5^th^, 2024*
