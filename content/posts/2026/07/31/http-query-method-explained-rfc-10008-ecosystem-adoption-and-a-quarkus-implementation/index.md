---
title: "HTTP QUERY Method Explained: RFC 10008, Ecosystem Adoption, and a Quarkus Implementation"
date: "2026-07-31T08:00:37+00:00"
description: "When you build a search API, you usually start with HTTP GET, the natural choice for a read operation: it is safe, idempotent, and cacheable. Then the - by Hüseyin Akdoğan"
authors:
  - "huseyin-akdogan"
image: "article-cover-scaled.png"
categories:
  - "Jakarta EE"
  - "Java"
tags:
related_posts:
  - "why-we-moved-our-timefold-java-worker-pods-from-amd-to-arm64"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "build-a-sentiment-analysis-api-in-java-with-quarkus-and-local-llms"
frozen: false
---

When you build a search API, you usually start with HTTP GET, the natural choice for a read operation: it is safe, idempotent, and cacheable. Then the search form grows, filters multiply, and nested criteria appear. Since using GET means placing the query inside the URI, a length limit problem emerges. Worse, placing sensitive query values in the URI increases the chance of exposure through access logs, browser history, proxies, and monitoring systems. Because the HTTP protocol does not forbid it, sending a body with GET may look like a way out, but building your design on behavior the standards leave undefined is not a recommended practice. Elasticsearch's GET-with-body search API is a well-known example, and [Elastic's own documentation](https://www.elastic.co/guide/en/elasticsearch/guide/current/_empty_search.html) openly acknowledges the problem:
> "As a result, some HTTP servers allow it, and some---especially caching proxies---don't. \[...\] However, because GET with a request body is not universally supported, the search API also accepts POST requests."

HTTP POST, on the other hand, carries the query in the request payload rather than the URI, which overcomes both the length limit and the data leakage problems. But POST is neither [safe](https://datatracker.ietf.org/doc/html/rfc9110#section-9.2.1) nor [idempotent](https://datatracker.ietf.org/doc/html/rfc9110#section-9.2.2), since the protocol allows every invocation to change state on the server, and its response is not cached unless it carries explicit freshness information. This nature of POST also imposes a performance cost: results are recomputed and retransferred on every call, and a timed-out request cannot be safely retried.

What is missing is clear: a method that is **safe and idempotent like GET but carries content like POST**. Until June 2026, HTTP did not have such a method in standardized form.

## The QUERY Method

To address this need, the IETF introduced the QUERY method in [RFC 10008](https://www.rfc-editor.org/info/rfc10008/). QUERY is the first new HTTP method since RFC 5789 was standardized in 2010.

The core idea can be summarized as follows: a QUERY request asks the target resource to process the enclosed content in a safe and idempotent manner and to respond with the result. Everything else the RFC introduces either follows from this definition or builds practical machinery around it. Let's look at the key concepts one by one:

### Safe and idempotent

A QUERY is defined as a safe operation: it does not request a state change on the target resource. It can be retried, repeated, or restarted automatically without concern for partial side effects. This is the contract that separates it from POST.

### Meaning comes from Content-Type

RFC 10008 deliberately does not define a query language. The same endpoint may accept a JSON filter document, a form-encoded string, or any other query language defined by a media type; the media type of the request content defines how the server should interpret it. Servers are [required to reject](https://datatracker.ietf.org/doc/html/rfc10008#section-2) requests whose Content-Type is missing or inconsistent with the content. The RFC goes as far as [forbidding content sniffing](https://datatracker.ietf.org/doc/html/rfc10008#section-2.1): a server is not allowed to infer a media type from the request content and use it to repair a missing or erroneous Content-Type.

### Explicitly cacheable

Unlike POST, QUERY introduces cacheability for body-carrying requests, with one crucial twist: the cache key must include the request content in addition to the URI, since two QUERY requests to the same URI with different bodies are different queries.

### Discovery via Accept-Query

A server can advertise QUERY support with the Accept-Query response header, which lists the media types it accepts as query content.

### The equivalent resource

A QUERY response may include a Location header pointing to a URI that represents the same query. A client can later re-fetch the result with a plain GET, no body required. The spec also gives [303 See Other](https://datatracker.ietf.org/doc/html/rfc10008#appendix-A.4.3) a natural role for redirecting a query to a retrievable resource. The RFC's [Security Considerations](https://datatracker.ietf.org/doc/html/rfc10008#section-4) add one caveat here: when the query contains sensitive information that must not be logged, the URI assigned to such a resource should not include any sensitive portions of the original query content, otherwise the exposure problem QUERY avoids would simply reappear one response later.

### Familiar error semantics

The RFC [recommends specific status codes](https://datatracker.ietf.org/doc/html/rfc10008#section-2.1) for the failure cases: 400 when media type information is missing, 415 when the media type is not supported by the resource, and 422 when the content is well formed but the query cannot be processed.

## A decade in the making

The RFC had a long journey. The idea traces back to [WebDAV](https://datatracker.ietf.org/doc/html/rfc4918)'s SEARCH method ([RFC 5323](https://datatracker.ietf.org/doc/rfc5323/), 2008), which demonstrated the demand for body-driven queries but remained confined to the XML-based WebDAV ecosystem. In 2021, the HTTP Working Group adopted the effort as a working group item, moving it from an individual proposal into the IETF standardization process. The method was later renamed from SEARCH to QUERY to avoid confusion with the existing WebDAV SEARCH method and to better reflect its purpose. The document was published as RFC 10008 in June 2026. Eleven years from the first draft to Proposed Standard is a useful reminder that even a seemingly simple addition to HTTP touches an enormous installed base and therefore receives extensive scrutiny.

## Where ecosystem support stands today

As of July 2026, HTTP QUERY has completed the standardization phase with RFC 10008, but ecosystem adoption remains in its early stage. Many HTTP servers and proxies can forward QUERY requests without protocol changes, but native support across frameworks, browser APIs, caches, WAFs, and API tooling is still emerging. The primary barrier is no longer the protocol itself, but the large installed base of software that assumes a fixed set of HTTP methods.

The Java ecosystem offers a useful snapshot of adoption in progress:

### Apache Tomcat

A pull request adding QUERY support was merged on July 1, 2026 ([apache/tomcat#1026](https://github.com/apache/tomcat/pull/1026)). Support is available only in Tomcat 12 because it required Servlet API changes.

### Eclipse Jetty

Eclipse Jetty has an open pull request ([jetty/jetty.project#15316](https://github.com/jetty/jetty.project/pull/15316)) implementing the core RFC 10008 semantics: method registration as safe and idempotent, the Accept-Query header, redirect behavior, and integration with compression and buffering handlers. It was initially aimed at Jetty 12.1 but has been retargeted to Jetty 13, aligning with a possible Jakarta Servlet 6.2 timeline.

### Jakarta Servlet

There is an open issue ([jakartaee/servlet#1068](https://github.com/jakartaee/servlet/issues/1068)) proposing the addition of QUERY to the specification itself, so that `HttpServlet` gains first-class support and QUERY requests receive the same form parameter processing model currently defined for POST. This is arguably the most significant milestone for the broader Jakarta EE ecosystem, because it moves QUERY from container-specific support into the platform specification itself. Once Servlet defines QUERY, application servers such as WildFly, Payara, and Open Liberty can inherit support through their servlet containers as they move to the new specification level. As of this writing, none of them has shipped QUERY support ahead of the specification.

## What about Spring?

Spring deserves its own section because of how request mapping is modeled. Spring MVC and WebFlux expose their annotation-based request mapping model through the `RequestMethod` enum, and that enum currently contains GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, and TRACE. There is no `RequestMethod.QUERY`, which means you cannot declaratively map a QUERY request through Spring's annotation-based programming model today. The available workarounds are awkward and bypass Spring's normal request-mapping model: declare a generic mapping and inspect `request.getMethod()` manually, or implement a custom `RequestMappingHandlerMapping`.

Unlike the Servlet case, this is not primarily a container problem; it is primarily a framework API and abstraction problem. The Spring team is aware. A community pull request adding QUERY support ([spring-projects/spring-framework#34993](https://github.com/spring-projects/spring-framework/pull/34993)) has been open since before RFC 10008 was published. It supersedes a feature request that had remained open for nearly two years, and maintainers have indicated an intention to target Spring Framework 7.1, currently expected in November 2026. There is even a naming collision to solve first: the obvious convenience annotation `@QueryMapping` is already used by Spring for GraphQL.

## Why Quarkus can do it today

This is where an underappreciated property of HTTP pays off: **the request method is simply a token defined by the HTTP grammar** . A server does not need to have built-in knowledge of every method to parse it. Quarkus builds its HTTP layer on Netty and Vert.x, and neither requires the method to be one of a predefined set; the request can reach the routing layer without requiring special handling for QUERY. On top of that, Jakarta REST has had a standard extension point for custom methods since JAX-RS 1.0: the `@HttpMethod` meta-annotation, the same mechanism that has enabled JAX-RS applications to expose WebDAV methods like PROPFIND for years.

Put the two together and RFC 10008-compatible QUERY endpoints in Quarkus require no framework changes; they can be enabled through a single Jakarta REST extension point:

```java
@HttpMethod("QUERY") 
@Documented @Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface QUERY { }
```

The remaining work is implementing RFC 10008 semantics at the application layer, which is precisely what the example project demonstrates.

## The example: a product catalog you can QUERY

The demo repository is available on GitHub: [hakdogan/http-query-method](https://github.com/hakdogan/http-query-method). It is a small Quarkus application exposing a product catalog at `/products`, deliberately compact, with only a handful of classes, but each RFC 10008 concept has a concrete counterpart in the code.

### One query, two media types

The resource accepts the same logical filter in two representations, demonstrating that the query semantics are determined by the Content-Type, not the URI:

```java
@QUERY 
@Consumes(MediaType.APPLICATION_JSON) 
public Response query(ProductFilter filter) { ... } 

@QUERY 
@Consumes(MediaType.APPLICATION_FORM_URLENCODED) 
public Response queryForm(String body) { ... }
```

So both of these work, and mean the same thing:

```bash
curl -i -X QUERY http://localhost:8080/products \ 
    -H 'Content-Type: application/json' \ 
    -d '{"category":"laptop","maxPrice":2000}' 

curl -X QUERY http://localhost:8080/products \ 
    -H 'Content-Type: application/x-www-form-urlencoded' \ 
    -d 'category=laptop&maxPrice=2000'
```

```shell

```

A request with an unsupported media type is rejected with 415, and a filter that is well formed but self-contradictory, such as `minPrice` greater than `maxPrice`, returns 422. The second part is a design choice rather than an RFC requirement: [Section 2.1](https://datatracker.ietf.org/doc/html/rfc10008#section-2.1) says 422 can be used when the content matches its media type but the query cannot be processed due to its actual contents, and returning an empty result with 200 would be an equally valid reading. The demo treats the contradiction as a client error because an empty 200 response would be indistinguishable from a legitimately empty match, silently hiding what is almost certainly a bug in the caller.

### The response tells the whole story

A successful QUERY comes back like this:

```bash
HTTP/1.1 200 OK 
Content-Type: application/json 
Accept-Query: application/json, application/x-www-form-urlencoded 
Location: http://localhost:8080/products?category=laptop&maxPrice=2000 
Cache-Control: no-transform, max-age=60 ETag: "f675e29b" 

[{"category":"laptop","id":2,"name":"ThinkPad X1 Carbon","price":1899.00}, ...]
```

Three headers carry the RFC's ideas:

* **Accept-Query** advertises which media types the resource accepts as query content. In the demo it is added by a small response filter.
* **Location** points to the *equivalent resource* from [Section 2.2](https://datatracker.ietf.org/doc/html/rfc10008#section-2.2) of the RFC: the same query expressed through the request URI. Fetch it with a plain GET and you get the identical result, no body needed. One of the tests does exactly that round trip.
* **Cache-Control and ETag** make the cacheability promise concrete. The ETag is derived from the result, so repeating the query with `If-None-Match` returns `304 Not Modified` without resending the result:

```bash
HTTP/1.1 304 Not Modified 
ETag: "f675e29b"
```

This is the answer to "*why not just POST*": QUERY was designed to provide query semantics without giving up the cache-friendly properties associated with safe methods.

### Discovery without prior knowledge

How does a client discover that a resource supports QUERY? One OPTIONS request:

```bash
curl -i -X OPTIONS http://localhost:8080/products
```

The response answers with two headers, one listing the methods the resource accepts and one listing the media types it accepts as query content:

```bash
HTTP/1.1 200 OK 
Allow: HEAD, QUERY, GET, OPTIONS 
Accept-Query: application/json, application/x-www-form-urlencoded
```

In this case, Quarkus generated the Allow header automatically, including QUERY, simply because a resource method is bound to it.

### Proving idempotency

The demo's test suite covers the filtering logic, the media type handling, the error codes, the equivalent-resource round trip, the conditional request flow, and, fittingly for a method whose defining feature is repeatability, a test that repeats the same QUERY several times and verifies the operation remains safe and produces a consistent response.

The key lesson from this example is not how QUERY was implemented, but why it was possible: the HTTP extension point already existed, and the framework did not need to invent a new abstraction.

## Conclusion

QUERY is not a revolution; it is the standardization of a pattern that many systems have implemented through POST-based query endpoints for years. That is exactly why it matters. The gap between "*works* " and "*works with the guarantees the protocol gives you*" is where caching, idempotent retries, and better tooling become possible.

Adoption is arriving unevenly: first in protocol implementations and servers, then in frameworks, gateways, and CDNs. But as the example shows, on a stack like Quarkus that treats the method as an extensible value rather than a hardcoded list, you do not have to wait to start experimenting.

The protocol was ready for extension; the interesting question was whether the layers above it preserved that flexibility.

The complete example, including all tests, is available on GitHub: [hakdogan/http-query-method](https://github.com/hakdogan/http-query-method).

## References

* RFC 10008, The HTTP QUERY Method: <https://www.rfc-editor.org/info/rfc10008/>
* IETF Datatracker, document history: <https://datatracker.ietf.org/doc/rfc10008/>
* RFC 9110, HTTP Semantics: <https://www.rfc-editor.org/info/rfc9110/>
* RFC 4918, WebDAV: <https://www.rfc-editor.org/info/rfc4918/>
* RFC 5323, WebDAV SEARCH: <https://www.rfc-editor.org/info/rfc5323/>
* RFC 5789, PATCH: <https://www.rfc-editor.org/info/rfc5789/>
