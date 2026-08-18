---
title: "API Versioning"
date: "2024-01-22T09:00:23+00:00"
lastmod: "2024-01-22T09:00:25+00:00"
description: "In this short article, we detail the three options for versioning HTTP APIs: path-based, query-based, and header-based."
canonical: "https://blog.frankel.ch/api-versioning/"
authors:
  - "nicolas-frankel"
image: "signpost-3691159.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "poor-mans-api"
  - "a-list-of-cache-providers"
  - "kotlin-delegation"
  - "boxlang-ai-deep-dive-part-7-of-7-mcp-the-protocol-that-connects-everything"
frozen: false
---

**In my previous article [Evolving your APIs](https://blog.frankel.ch/evolve-apis/), I mention the main API versioning approaches. During the talk of the same name, I sometimes get some questions on the subject. In this article, I'll detail each of them.**

I assume readers know the reasons behind versioning, semantic versioning, and product lifecycle. If not, I encourage you to read a bit about these themes; in particular, chapter 24 of the excellent [API Design Patterns](https://blog.frankel.ch/api-design-patterns/) book focuses on them.

I'll summarize the subject in a few words in any case.

## Generalities

Software naturally evolves, because of business need or changing regulations. In some cases, the said software has no clients but humans, *e.g.*, a monolith with Server-Side Rendering. In all other cases, at least another software component interacts with your software:

* A JavaScript front-end consumes the REST API
* A webservice consumes the REST API
* etc.

Some changes are backward-compatible - you don't need to update the client; others are not. Removing an endpoint is not backward compatible; it's a breaking change. Removing a parameter, adding a required parameter, or changing a parameter type are also breaking changes.

When introducing a breaking change in regular software, you must increase its major version if you adhere to semantic versioning. It's the same in the realm of APIs. You let your customers keep using the v1 version while releasing breaking changes in the v2 version.

The crux of the problem is now how to use a specific version of the endpoint. Three options are available:

* Path-based versioning
* Query-based versioning
* Header-based versioning

Let's detail them in turn. It all boils down to routing; I'll demo the configuration with [Apache APISIX](https://apisix.apache.org/) to implement each versioning approach.

## Path-based versioning

Path-based versioning is so ubiquitous that it's the approach most people think about when they think about API versioning. The idea is to set the version in the path:

* `/v1/foo`
* `/v2/foo`

Path-based versioning seems easy to implement with Apache APISIX:

```yaml
upstreams:
  - id: 1
    nodes:
      "upstream_1:8080": 1
  - id: 2
    nodes:
      "upstream_2:8080": 1

routes:
  - uri: /v1/*
    upstream_id: 1
  - uri: /v2/*
    upstream_id: 2
```

The above setup doesn't work unfortunately: as it stands, we forward `/v1/*` to the upstream, whereas it probably can handle only `*` - the path behind the version prefix. We need to remove the version prefix before forwarding to the upstream:

```yaml
routes:
  - uri: /v1/*
    upstream_id: 1
    plugins:
      proxy-rewrite:
        regex_uri: [ "/v1(.*)", "$1" ]        #1
  - uri: /v2/*
    upstream_id: 2
    plugins:
      proxy-rewrite:
        regex_uri: [ "/v2(.*)", "$1" ]        #1
```

1. Remove the version path prefix before forwarding

Beware if you use other plugins, which may forward the request before the prefix is removed, *e.g.* , `proxy-mirror`. In this case, we must apply `proxy-rewrite` *before* `proxy-mirror`. Apache APISIX orders plugins by their default priority, so we need to increase the priority of the former:

```yaml
routes:
  - uri: /v1/*
    upstream_id: 1
    plugins:
      proxy-rewrite:                          #1
        regex_uri: [ "/v1(.*)", "$1" ]
      proxy-mirror:                           #2
        host: "http://api.v2:8080"
        _meta:
          priority: 1000                      #3
```

1. `proxy-rewrite` default priority is `1008`
2. `proxy-mirror` default priority is `1010`
3. Set it to `1000` so that it now applies *after* the rewrite takes place

## Query-based versioning

Another way to version is to use query parameters, *e.g.* , `?version=v1`. While I've never seen it in the wild, it deserves a mention nonetheless. We can leverage the following Apache APISIX configuration:

```yaml
routes:
  - uri: /*                                   #1
    upstream_id: 1
    vars: [[ "arg_version", "==", "v1" ]]     #2
    priority: 2                               #1
  - uri: /*                                   #1
    upstream_id: 2
    vars: [[ "arg_version", "==", "v2" ]]     #2
    priority: 3                               #1
  - uri: /*                                   #1-3
    upstream_id: 1
    priority: 1
```

1. Both routes match the same URI, so we must evaluate them in order. That's the role of `priority`: Apache APISIX evaluates the highest priority first
2. Evaluate the query parameter named `version`
3. Default route when no `version` is provided. Here, I route to version 1, but you can also return an HTTP status `4xx` to require a version.

## Header-based versioning

The last alternative for versioning is to use HTTP headers. Here's a custom header:

```visualbasic
GET / HTTP1.1

Version: 1
```

From an HTTP point of view, asking for a version via a header is the definition of *content negotiation* between the client and the server:
> Content negotiation refers to mechanisms defined as a part of HTTP that make it possible to serve different versions of a document (or more generally, representations of a resource) at the same URI, so that user agents can specify which version fits their capabilities the best.
>
> -- [Content Negotiation on Wikipedia](https://en.wikipedia.org/wiki/Content_negotiation)

The agreed-upon content type format follows the pattern `application/vnd.aaa.bbb.vX+format`, where:

* `aaa.bbb` is a reversed domain name, *e.g.* , `ch.frankel`
* `X` is the version number, *e.g.* , `1`
* `format` is the accepted format, *e.g.* , `json`

Hence, here's a possible request:

```visualbasic
GET / HTTP1.1

Accept: application/vnd.ch.frankel.myservice.v1+json
```

Theoretically, the client can leverage the *quality* of `Accept` headers to communicate that it can handle different versions. The following request tells that the client prefers version 2 but can handle version 1 if the need be:

```visualbasic
GET / HTTP1.1

Accept: application/vnd.ch.frankel.myservice.v2+json;q=0.8, application/vnd.ch.frankel.myservice.v1+json;q=0.2
```

In practice, quality requires a high level of maturity, both on the server-side - handling qualities and on the client-side - handling two versions simultaneously.

Here's the APISIX configuration for quality-less content negotiation. It's very similar to the one above, the only difference being the Nginx variable in play, `http_X` instead of `arg_Y`.

```visualbasic
routes:
  - uri: /*
    upstream_id: 1
    vars: [[ "http_accept", "==", "vnd.ch.frankel.myservice.v1+json" ]]
    priority: 2
  - uri: /*
    upstream_id: 2
    vars: [[ "http_accept", "==", "vnd.ch.frankel.myservice.v2+json" ]]
    priority: 3
  - uri: /*
    upstream_id: 1
    priority: 1
```

## Conclusion

In this short article, we detailed the three options for versioning HTTP APIs: path-based, query-based, and header-based.

They don't differ much, each having its little tricky parts.

Whatever path you choose, though, make sure it's consistent across all the organization.

The complete source code for this article can be found on [GitHub](https://github.com/ajavageek/api-versioning).

**To go further:**

* [API deployment strategies](https://navendu.me/posts/api-deployment-strategies/)
* [Content Negotiation in RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html#name-content-negotiation)
* [Routing in Apache APISIX](https://apisix.apache.org/docs/apisix/router-radixtree/)

*Originally published at [A Java Geek](https://blog.frankel.ch/api-versioning/) on November 5^th^, 2023*
